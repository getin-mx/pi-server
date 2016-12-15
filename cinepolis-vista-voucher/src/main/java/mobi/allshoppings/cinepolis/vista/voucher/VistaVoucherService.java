package mobi.allshoppings.cinepolis.vista.voucher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.dao.spi.VoucherDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Voucher;
import mobi.allshoppings.tools.CollectionFactory;
import mx.com.cineticket.inetvis.vista.voucher.Executecmd;

public class VistaVoucherService extends WebServiceGatewaySupport {

	private static final Logger log = Logger.getLogger(VistaVoucherService.class.getName());
	
	private static VistaVoucherService instance;
	private static final SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final SimpleDateFormat transDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat transTimeFormat = new SimpleDateFormat("HH:mm:ss");
	private static final String TIMEZONE = "-05:00";
	private static final DecimalFormat decimaFormat = new DecimalFormat("000000000000");
	private static final long ONE_YEAR = 31536000000L;
	
	public static final String ACTION_GET_STATUS = "16";
	public static final String ACTION_ACTIVATE = "14";
	public static final String ACTION_SELL_COMMIT = "2";
	public static final String ACTION_REFUND_COMMIT = "10";

	@Autowired
	private static VoucherDAO dao = new VoucherDAOJDOImpl();
	
	public static synchronized VistaVoucherService getInstance() {
		if( instance == null ) {
			VistaVoucherConfiguration config = new VistaVoucherConfiguration();
			Jaxb2Marshaller marshaller = config.marshaller();
			instance = config.vistaDataService(marshaller);
		}
		
		return instance;
	}
	
	public List<Voucher> get(int count, String format, String deviceUUID, String showtime) throws ASException {

		try {
			List<Voucher> list = CollectionFactory.createList();
			for( int i = 0; i < count; i++ ) {
				Voucher obj = dao.getNextAvailable(format);
				obj.setAssignationDate(new Date());
				obj.setDeviceUUID(deviceUUID);
				obj.setAssignationMember(showtime);
				dao.update(obj);
				Map<String, String> result = sellAndCommit(obj.getCode(), dao.getNextSequence());
				if(!result.get("RESULT").equals("0")) {
					ASExceptionHelper.invalidArgumentsException("Voucher Service returned error " + result.get("RESPONSEDESC"));
				}
				log.log(Level.INFO, result.toString());
				list.add(obj);
			}

			return list;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		
	}
	
	public Map<String, String> activate(String voucherId, long txId) throws Exception {
		return callService(voucherId, txId, ACTION_ACTIVATE);
	}

	public Map<String, String> sellAndCommit(String voucherId, long txId) throws Exception {
		return callService(voucherId, txId, ACTION_SELL_COMMIT);
	}
	
	public Map<String, String> refundAndCommit(String voucherId, long txId) throws Exception {
		return callService(voucherId, txId, ACTION_REFUND_COMMIT);
	}

	public Map<String, String> getStatus(String voucherId) throws Exception {
		return callService(voucherId, 0, ACTION_GET_STATUS);
	}
	
	public Map<String, String> callService(String voucherId, long txId, String requestAction) throws Exception {

		Map<String, String> responseMap = new HashMap<String, String>();
		
		try {
			Executecmd request = new Executecmd();
			request.setSyssettings(getSyssettings(requestAction));
			request.setCmddata(getCmddata(voucherId, txId, requestAction));
			request.setReturndata(getReturndata());

			Document doc = sendAndReceive(request, "http://vista.co.nz/webservices/WSVistaVoucher/executecmd");
			
			// Get the result
			NodeList nList = doc.getElementsByTagName("executecmdResult");
			Node nExecutecmdResult = nList.item(0);
			int result = Integer.parseInt(nExecutecmdResult.getChildNodes().item(0).getTextContent());
			
			responseMap.put("RESULT", String.valueOf(result));
			
			// Get responses
			nList = doc.getElementsByTagName("returndata").item(0).getChildNodes().item(1).getChildNodes();
			for(int i = 0; i < nList.getLength(); i++ ) {
				Node nNode = nList.item(i);
				if( nNode.getNodeName().equals("prop")) {
					String key = nNode.getAttributes().getNamedItem("name").getTextContent();
					String value = nNode.getAttributes().getNamedItem("value").getTextContent();
					responseMap.put(key, value);
				}
			}
			
			return responseMap;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("restriction")
	private Document sendAndReceive(Object request, String actionCallback) throws ASException {
		try {
			JAXBContext jaxbContextIn = JAXBContext.newInstance(request.getClass());
			Marshaller marshallerIn = jaxbContextIn.createMarshaller();
			marshallerIn.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ByteArrayOutputStream baosIn = new ByteArrayOutputStream();
			com.sun.org.apache.xml.internal.serialize.XMLSerializer serializer = createXMLSerializer(new String[]{}, baosIn);

			marshallerIn.marshal(request, serializer.asContentHandler());

			String xml = baosIn.toString().replaceAll("&lt;", "<").replaceAll("&gt;", ">");
//			System.out.println(xml);

			ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
			new StreamResult(baosOut);

			int retries = 3;
			while(retries > 0 ) {
				try {
					getWebServiceTemplate()
					.sendSourceAndReceiveToResult(
							new StreamSource(new StringReader(xml)),
							new SoapActionCallback(
									actionCallback),
									new StreamResult(baosOut));
					retries = 0;
				} catch( Exception e ) {
					retries--;
					if( retries == 0 )
						throw e;
					try{ Thread.sleep(1000); } catch( Exception e1 ) {}
				}
			}
			
			String xmlOut = baosOut.toString().replaceAll("&lt;", "<").replaceAll("&gt;", ">").replace("<?xml version=\"1.0\" encoding=\"utf-16\"?>", "");
//			System.out.println(xmlOut);


			// Constructs a XML Document with the response
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringBuilder xmlStringBuilder = new StringBuilder();
			xmlStringBuilder.append(xmlOut);
			ByteArrayInputStream input =  new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));

			Document doc = builder.parse(input);

			return doc;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("restriction")
	private com.sun.org.apache.xml.internal.serialize.XMLSerializer createXMLSerializer(String[] cDataElements, OutputStream cOut) {
		// This code is from a sample online: http://jaxb.java.net/faq/JaxbCDATASample.java
		// configure an OutputFormat to handle CDATA
		com.sun.org.apache.xml.internal.serialize.OutputFormat of = new com.sun.org.apache.xml.internal.serialize.OutputFormat();

		// specify which of your elements you want to be handled as CDATA.
		// The use of the '^' between the namespaceURI and the localname
		// seems to be an implementation detail of the xerces code.
		// When processing xml that doesn't use namespaces, simply omit the
		// namespace prefix as shown in the third CDataElement below.
		of.setCDataElements(cDataElements); //

		// set any other options you'd like
		of.setPreserveSpace(true);
		of.setIndenting(true);
		of.setPreserveSpace(false);

		// create the serializer
		com.sun.org.apache.xml.internal.serialize.XMLSerializer serializer = new com.sun.org.apache.xml.internal.serialize.XMLSerializer(of);
		serializer.setOutputByteStream(cOut);

		return serializer;
	}

	private String getReturndata() {
		return RETURNDATA;
	}
	
	private String getSyssettings(String requestAction) {
		if( requestAction.equals(ACTION_ACTIVATE)) {
			return ACTIVATE_SYSSETTINGS;
		} else {
			return SYSSETTINGS;
		}
	}
	
	private String getCmddata(String voucherId, long txId, String requestAction) {
		
		String cmddata = STATUS_CMDDATA;
		if(requestAction.equals(ACTION_ACTIVATE)) cmddata = ACTIVATE_CMDDATA;
		if(requestAction.equals(ACTION_SELL_COMMIT)) cmddata = SELL_COMMIT_CMDDATA;
		if(requestAction.equals(ACTION_REFUND_COMMIT)) cmddata = REFUND_COMMIT_CMDDATA;
		
		Date now = new Date();
		String voucherCode = voucherId.substring(0, 5);
		String serialNo = voucherId.substring(5);  
		String serialEnd = voucherId.substring(5);
		String transNo = String.valueOf(txId);

		cmddata = cmddata.replace("{id}", decimaFormat.format(now.getTime()));
		cmddata = cmddata.replace("{voucherId}", voucherId);
		cmddata = cmddata.replace("{requestAction}", requestAction);
		cmddata = cmddata.replace("{localdatetime}", localDateFormat.format(now));
		cmddata = cmddata.replace("{voucherCode}", voucherCode);
		cmddata = cmddata.replace("{serialNo}", serialNo);
		cmddata = cmddata.replace("{serialEnd}", serialEnd);
		cmddata = cmddata.replace("{transNo}", transNo);
		cmddata = cmddata.replace("{transdatetime}", transDateFormat.format(now) + "T" + transTimeFormat.format(now) + TIMEZONE);
		cmddata = cmddata.replace("{expirityDate}", localDateFormat.format(new Date(now.getTime() + ONE_YEAR)));

		return cmddata;
	}
	
	private static final String SYSSETTINGS = 
			"<![CDATA[<proplist>"

					+ "<prop name=\"ID\" value=\"\"/>"
					+ "<prop name=\"SERVICEURL\" value=\"http://10.2.90.62/WSVistaVoucher/WSVistaVoucher.asmx\\\"/>"
					+ "<prop name=\"SERVICETIMEOUTSECS\" value=\"30\"/>"
					+ "<prop name=\"CHECKSUMCODE\" value=\"SKIP\"/>"
					+ "<prop name=\"TAXMODE\" value=\"I\"/>"
					+ "<prop name=\"CASEINSENSITIVEVOUCHERCODES\" value=\"N\"/>"
					+ "<prop name=\"VOIDSALEONREFUND\" value=\"Y\"/>"
					+ "<prop name=\"LOGGINGLEVEL\" value=\"2\"/>"
					+ "<prop name=\"CINEMAID\" value=\"786\"/>"
					+ "<prop name=\"HOSERVER\" value=\"\"/>"
					+ "<prop name=\"MODE\" value=\"ONLINE\"/>"

					+ "</proplist>]]>";

	private static final String ACTIVATE_SYSSETTINGS = 
			"<![CDATA[<proplist>"
					+ "<prop name=\"ID\" value=\"\"/>"
					+ "<prop name=\"SERVICEURL\" value=\"http://10.2.90.62/WSVistaVoucher/WSVistaVoucher.asmx\\\"/>"
					+ "<prop name=\"SERVICETIMEOUTSECS\" value=\"30\"/>"
					+ "<prop name=\"CHECKSUMCODE\" value=\"SKIP\"/>"
					+ "<prop name=\"TAXMODE\" value=\"I\"/>"
					+ "<prop name=\"CASEINSENSITIVEVOUCHERCODES\" value=\"N\"/>"
					+ "<prop name=\"VOIDSALEONREFUND\" value=\"N\"/>"
					+ "<prop name=\"LOGGINGLEVEL\" value=\"2\"/>"
					+ "<prop name=\"CINEMAID\" value=\"786\"/>"
					+ "<prop name=\"HOSERVER\" value=\"\"/>"
					+ "<prop name=\"MODE\" value=\"ONLINE\"/>"

					+ "</proplist>]]>";

	private static final String STATUS_CMDDATA = 
			"<![CDATA[<proplist>"

					+ "<prop name=\"ID\" value=\"{voucherId}\"/>"
					+ "<prop name=\"USEPRICE\" value=\"-1\"/>"
					+ "<prop name=\"REQUESTACTION\" value=\"{requestAction}\"/>"
					+ "<prop name=\"DUPLICATENO\" value=\"0\"/>"
					+ "<prop name=\"VOUCHERCODE\" value=\"{voucherCode}\"/>"
					+ "<prop name=\"SERIALNO\" value=\"{serialNo}\"/>"
					+ "<prop name=\"SCANORMANUAL\" value=\"M\"/>"
					+ "<prop name=\"TOTALACTIONS\" value=\"1\"/>"
					+ "<prop name=\"TOTALVALUE\" value=\"-1\"/>"
					+ "<prop name=\"ISCONNECTED\" value=\"Y\"/>"
					+ "<prop name=\"LOCALDATETIME\" value=\"{localdatetime}\"/>"
					+ "<prop name=\"LEGACY\" value=\"N\"/>"
					+ "<prop name=\"SALESCHANNEL\" value=\"TRANSFER\"/>"
					+ "<prop name=\"ORPIN\" value=\"\"/>"
					+ "<prop name=\"CINLOCSHORTNAME\" value=\"\""
					+ "/>"
					
					+ "</proplist>]]>";

	private static final String SELL_COMMIT_CMDDATA = 
			"<![CDATA[<proplist>"
					
					+ "<prop name=\"ID\" value=\"\"/>"
					+ "<prop name=\"REQUESTACTION\" value=\"3\"/>"
					+ "<prop name=\"ISCONNECTED\" value=\"Y\"/>"
					+ "<prop name=\"LOCALDATETIME\" value=\"{localdatetime}\"/>"
					+ "<prop name=\"USERID\" value=\"9999\"/>"
					+ "<prop name=\"SALESCHANNEL\" value=\"WWW\"/>"
					+ "<prop name=\"WORKSTATIONID\" value=\"ALLSHOPPINGS\"/>"
					+ "<prop name=\"TRANSNO\" value=\"{transNo}\"/>"
					+ "<prop name=\"TRANSDATETIME\" value=\"{transdatetime}\"/>"
					+ "<proplist>"
					+ "<prop name=\"ID\" value=\"{voucherId}\"/>"
					+ "<prop name=\"USEPRICE\" value=\"-1\"/>"
					+ "<prop name=\"REQUESTACTION\" value=\"2\"/>"
					+ "<prop name=\"DUPLICATENO\" value=\"0\"/>"
					+ "<prop name=\"VOUCHERCODE\" value=\"{voucherCode}\"/>"
					+ "<prop name=\"SERIALNO\" value=\"{serialNo}\"/>"
					+ "<prop name=\"SCANORMANUAL\" value=\"M\"/>"
					+ "<prop name=\"TOTALACTIONS\" value=\"1\"/>"
					+ "<prop name=\"TOTALVALUE\" value=\"-1\"/>"
					+ "<prop name=\"ISCONNECTED\" value=\"Y\"/>"
					+ "<prop name=\"LOCALDATETIME\" value=\"{localdatetime}\"/>"
					+ "<prop name=\"LEGACY\" value=\"N\"/>"
					+ "<prop name=\"SALESCHANNEL\" value=\"WWW\"/>"
					+ "<prop name=\"ORPIN\" value=\"\"/>"
					+ "<prop name=\"CINLOCSHORTNAME\" value=\"\"/>"
					+ "<proplist>"
					+ "<prop name=\"USEPRICE\" value=\"0\" />"
					+ "<prop name=\"VALUE\" value=\"0\" />"
					+ "<prop name=\"VOUCHERCODE\" value=\"{voucherCode}\" />"
					+ "<prop name=\"LISTTYPE\" value=\"VCH\" />"
					+ "<prop name=\"VOUCHERID\" value=\"769\" />"
					+ "<prop name=\"VALUEALT\" value=\"0\" />"
					+ "<prop name=\"CHILDQTY\" value=\"1\" />"
					+ "<prop name=\"ISGIFTCARD\" value=\"False\" />"
					+ "<prop name=\"ID\" value=\"{voucherId}\" />"
					+ "<prop name=\"SERIALEND\" value=\"{serialEnd}\" />"
					+ "<prop name=\"SERIALSTART\" value=\"{serialEnd}\" />"

					+ "</proplist>"
					+ "</proplist>"
					+ "</proplist>]]>";
	
	private static final String REFUND_COMMIT_CMDDATA = 
			"<![CDATA[<proplist>"
					
					+ "<prop name=\"ID\" value=\"\"/>"
					+ "<prop name=\"REQUESTACTION\" value=\"3\"/>"
					+ "<prop name=\"ISCONNECTED\" value=\"Y\"/>"
					+ "<prop name=\"LOCALDATETIME\" value=\"{localdatetime}\"/>"
					+ "<prop name=\"USERID\" value=\"9999\"/>"
					+ "<prop name=\"SALESCHANNEL\" value=\"WWW\"/>"
					+ "<prop name=\"WORKSTATIONID\" value=\"ALLSHOPPINGS\"/>"
					+ "<prop name=\"TRANSNO\" value=\"{transNo}\"/>"
					+ "<prop name=\"TRANSDATETIME\" value=\"{transdatetime}\"/>"
					+ "<proplist>"
					+ "<prop name=\"ID\" value=\"{voucherId}\"/>"
					+ "<prop name=\"USEPRICE\" value=\"-1\"/>"
					+ "<prop name=\"REQUESTACTION\" value=\"10\"/>"
					+ "<prop name=\"DUPLICATENO\" value=\"0\"/>"
					+ "<prop name=\"VOUCHERCODE\" value=\"{voucherCode}\"/>"
					+ "<prop name=\"SERIALNO\" value=\"{serialNo}\"/>"
					+ "<prop name=\"SCANORMANUAL\" value=\"M\"/>"
					+ "<prop name=\"TOTALACTIONS\" value=\"1\"/>"
					+ "<prop name=\"TOTALVALUE\" value=\"-1\"/>"
					+ "<prop name=\"ISCONNECTED\" value=\"Y\"/>"
					+ "<prop name=\"LOCALDATETIME\" value=\"{localdatetime}\"/>"
					+ "<prop name=\"LEGACY\" value=\"N\"/>"
					+ "<prop name=\"SALESCHANNEL\" value=\"WWW\"/>"
					+ "<prop name=\"ORPIN\" value=\"\"/>"
					+ "<prop name=\"CINLOCSHORTNAME\" value=\"\"/>"
					+ "<proplist>"
					+ "<prop name=\"VOUCHERCODE\" value=\"{voucherCode}\" />"
					+ "<prop name=\"LISTTYPE\" value=\"VCH\" />"
					+ "<prop name=\"VOUCHERID\" value=\"769\" />"
					+ "<prop name=\"CHILDQTY\" value=\"1\" />"
					+ "<prop name=\"ISGIFTCARD\" value=\"False\" />"
					+ "<prop name=\"ID\" value=\"{voucherId}\" />"
					+ "<prop name=\"SERIALEND\" value=\"{serialEnd}\" />"
					+ "<prop name=\"SERIALSTART\" value=\"{serialEnd}\" />"

					+ "</proplist>"
					+ "</proplist>"
					+ "</proplist>]]>";

	private static final String ACTIVATE_CMDDATA = 
			"<![CDATA[<proplist>"

					+ "<prop name=\"ID\" value=\"{voucherId}\"/>"
					+ "<prop name=\"REQUESTACTION\" value=\"14\"/>"
					+ "<prop name=\"VOUCHERCODE\" value=\"{voucherCode}\"/>"
					+ "<prop name=\"SERIALNO\" value=\"{serialNo}\"/>"
					+ "<prop name=\"ISCONNECTED\" value=\"Y\"/>"
					+ "<prop name=\"LOCALDATETIME\" value=\"{localdatetime}\"/>"
					+ "<prop name=\"SALESCHANNEL\" value=\"WWW\"/>"
					+ "<prop name=\"EXPIRYDATE\" value=\"{expirityDate}\"/>"

					+ "</proplist>]]>";

	private static final String RETURNDATA =
			"<![CDATA[<proplist>"

					+ "<prop name=\"ID\" value=\"\"/>"
					+ "<prop name=\"USEPRICE\" value=\"-1\"/>"
					+ "<prop name=\"ORREQ\" value=\"N\"/>"
					+ "<prop name=\"REDEEMDATE\" value=\"\"/>"
					+ "<prop name=\"LEGACY\" value=\"N\"/>"
					+ "<prop name=\"RESPONSEDESC\" value=\"\"/>"
					+ "<prop name=\"RESPONSECODE\" value=\"-1\"/>"
					+ "<prop name=\"EXPIRYDATE\" value=\"\"/>"
					+ "<prop name=\"ORSECLEVEL\" value=\"9\"/>"
					+ "<prop name=\"RESPONSELCODE\" value=\"\"/>"

					+ "</proplist>]]>";
	
}
