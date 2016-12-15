package mobi.allshoppings.cinepolis.vista.loyalty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
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
import mx.com.cineticket.inetvis.vista.loyalty.InvokeLoyalty;

public class VistaLoyaltyService extends WebServiceGatewaySupport {

	private static final Logger log = Logger.getLogger(VistaLoyaltyService.class.getName());
	
	private static VistaLoyaltyService instance;
	private static final SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	@Autowired
	private static VoucherDAO dao = new VoucherDAOJDOImpl();

	public static final String ACTION_ACTIVATE = "ACTIVATE";
	public static final String ACTION_DELETE = "DELETEMEMBER";
	public static final String ACTION_STATUS = "RETURNMBR";
	
	public static synchronized VistaLoyaltyService getInstance() {
		if( instance == null ) {
			VistaLoyaltyConfiguration config = new VistaLoyaltyConfiguration();
			Jaxb2Marshaller marshaller = config.marshaller();
			instance = config.vistaDataService(marshaller);
		}
		
		return instance;
	}
	
	public List<Voucher> get(int count, String type, String deviceUUID, String campaignSpecialId) throws ASException {

		try {
			List<Voucher> list = CollectionFactory.createList();
			for( int i = 0; i < count; i++ ) {
				Voucher obj = dao.getNextAvailable(type);
				obj.setAssignationDate(new Date());
				obj.setDeviceUUID(deviceUUID);
				obj.setAssignationMember(campaignSpecialId);
				dao.update(obj);
				Map<String, String> result = activate(obj.getCode(), obj.getSubcode1(), obj.getSubcode2());
				if(!result.get("ResponseCode").equals("0")) {
					ASExceptionHelper.invalidArgumentsException("Loyalty Service returned error " + result.get("ResponseDesc"));
				}
				log.log(Level.INFO, result.toString());
				list.add(obj);
			}

			return list;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	
	public Map<String, String> activate(String cardNumber, String membershipId, String clubId) throws Exception {
		return callService(cardNumber, membershipId, clubId, ACTION_ACTIVATE);
	}

	public Map<String, String> delete(String cardNumber, String membershipId, String clubId) throws Exception {
		return callService(cardNumber, membershipId, clubId, ACTION_DELETE);
	}
	
	public Map<String, String> getStatus(String cardNumber, String membershipId, String clubId) throws Exception {
		return callService(cardNumber, membershipId, clubId, ACTION_STATUS);
	}
	
	public Map<String, String> callService(String cardNumber, String membershipId, String clubId, String requestAction) throws Exception {

		Map<String, String> responseMap = new HashMap<String, String>();
		
		try {
			InvokeLoyalty request = new InvokeLoyalty();
			request.setXMLMsgIn(getXMLSMsgIn(cardNumber, membershipId, clubId, requestAction));

			Document doc = sendAndReceive(request, "http://tempuri.org/InvokeLoyalty");
			
			// Get the result
			NodeList nList = doc.getElementsByTagName("Param");
			for(int i = 0; i < nList.getLength(); i++ ) {
				Node nNode = nList.item(i);
				if( nNode.getNodeName().equals("Param")) {
					String key = nNode.getAttributes().getNamedItem("Name").getTextContent();
					String value = nNode.getTextContent();
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
			// System.out.println(xml);

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
			
			String xmlOut = baosOut.toString()./* replaceAll("&amp;","&").replaceAll("&quot;", "\""). */ replaceAll("&lt;", "<").replaceAll("&gt;", ">").replace("<?xml version=\"1.0\" encoding=\"utf-16\"?>", "");
			// System.out.println(xmlOut);

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

	private String getXMLSMsgIn(String cardNumber, String membershipId, String clubId, String requestAction) {
		
		String cmddata = MSG_ACTIVATE;
		if(requestAction.equals(ACTION_DELETE)) cmddata = MSG_DELETE;
		if(requestAction.equals(ACTION_STATUS)) cmddata = MSG_STATUS;
		
		Date now = new Date();

		cmddata = cmddata.replace("{localdatetime}", localDateFormat.format(now));
		cmddata = cmddata.replace("{clubId}", clubId);
		cmddata = cmddata.replace("{cardNumber}", cardNumber);
		cmddata = cmddata.replace("{membershipId}", membershipId);

		return cmddata;
	}
	
	
	private static final String MSG_ACTIVATE =
			"<![CDATA[<VistaLoyaltyMsg>"
					+ "<MsgID></MsgID>"
					+ "<MsgType>ACTIVATE</MsgType>"
					+ "<MsgTime>{localdatetime}</MsgTime>"
					+ "<MsgResponseReq>Y</MsgResponseReq>"
					+ "<DataItem DataItemType=\"MsgParamList\">"
					+ "<Param Name=\"CinemaID\">447</Param>"
					+ "<Param Name=\"LoyaltyServer\">http://10.2.90.22/vistaloyaltyadmin/VistaLoyalty.asmx?WSDL</Param>"
					+ "<Param Name=\"LoggingLevel\">2</Param>"
					+ "<Param Name=\"CardNumber\">{cardNumber}</Param>"
					+ "<Param Name=\"MembershipID\">{membershipId}</Param>"
					+ "<Param Name=\"CardStatus\">6</Param>"
					+ "<Param Name=\"ClubID\">{clubId}</Param>"
					+ "</DataItem>"
					+ "</VistaLoyaltyMsg>]]>";

	private static final String MSG_DELETE =
			"<![CDATA[<VistaLoyaltyMsg>"
					+ "<MsgID></MsgID>"
					+ "<MsgType>DELETEMEMBER</MsgType>"
					+ "<MsgTime>{localdatetime}</MsgTime>"
					+ "<MsgResponseReq>Y</MsgResponseReq>"
					+ "<DataItem DataItemType=\"MsgParamList\">"
					+ "<Param Name=\"MembershipID\">{membershipId}</Param>"
					+ "<Param Name=\"UserID\">WWW</Param>"
					+ "</DataItem>"
					+ "</VistaLoyaltyMsg>]]>";

	private static final String MSG_STATUS =
			"<![CDATA[<VistaLoyaltyMsg>"
					+ "<MsgID></MsgID>"
					+ "<MsgType>RETURNMBR</MsgType>"
					+ "<MsgTime>{localdatetime}</MsgTime>"
					+ "<MsgResponseReq>Y</MsgResponseReq>"
					+ "<DataItem DataItemType=\"MsgParamList\">"
					+ "<Param Name=\"CinemaID\">447</Param>"
					+ "<Param Name=\"LoyaltyServer\">http://10.2.90.22/vistaloyaltyadmin/VistaLoyalty.asmx?WSDL</Param>"
					+ "<Param Name=\"LoggingLevel\">2</Param>"
					+ "<Param Name=\"CardNumber\">{cardNumber}</Param>"
					+ "</DataItem>"
					+ "</VistaLoyaltyMsg>]]>";
}
