package mobi.allshoppings.cinepolis.services;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.BindingProvider;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.tools.CollectionFactory;
import nz.co.vista.services.wsvistawebclient_datatypes._1.DataResponse;
import nz.co.vista.services.wsvistawebclient_datatypes._1.GetSessionInfoRequest;
import nz.co.vista.services.wsvistawebclient_servicecontracts._1.DataService;
import nz.co.vista.services.wsvistawebclient_servicecontracts._1.DataService_Service;
import oracle.webservices.ClientConstants;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class VistaDataService extends WebServiceGatewaySupport {

	private static VistaDataService instance;
	private DataService_Service dataService_Service;
	private DataService port;
		
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(VistaDataService.class.getName());
	
	public static synchronized VistaDataService getInstance() {
		if( instance == null ) {
			instance = new VistaDataService();

			instance.dataService_Service = new DataService_Service();
	        SecurityPoliciesFeature securityFeatures =
	            new SecurityPoliciesFeature(new String[] { "oracle/wss11_message_protection_client_policy" });
	        instance.port = instance.dataService_Service.getDataService(securityFeatures);
	        
	        BindingProvider wsbp = (BindingProvider) instance.port;
	                    
	        Map<String, Object> reqContext = wsbp.getRequestContext();        
	        reqContext.put(ClientConstants.WSSEC_KEYSTORE_TYPE, "JKS");  
	        reqContext.put(ClientConstants.WSSEC_KEYSTORE_LOCATION, "cinepolis.jks");  
	        reqContext.put(ClientConstants.WSSEC_KEYSTORE_PASSWORD, "cinepolis1");  
	  
	        reqContext.put(ClientConstants.WSSEC_ENC_KEY_ALIAS, "1");  
	        reqContext.put(ClientConstants.WSSEC_ENC_KEY_PASSWORD, "cinepolis1");  
	        reqContext.put(ClientConstants.WSSEC_RECIPIENT_KEY_ALIAS, "1");  
		}
		
		return instance;
	}
	
	public Map<String, Object> getShowtimeAttributes(String cinemaId, String sessionId) throws ASException {
		Map<String, Object> res = CollectionFactory.createMap();

		try {
			GetSessionInfoRequest request = new GetSessionInfoRequest();
			request.setCinemaId(cinemaId);
			request.setSessionId(sessionId);

			DataResponse response = port.getSessionInfo(request);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			StringBuilder xmlStringBuilder = new StringBuilder();
			xmlStringBuilder.append(response.getDatasetXML());
			ByteArrayInputStream input =  new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));

			Document doc = builder.parse(input);

			if( doc.getDocumentElement().getChildNodes().item(3) != null ) {
				NodeList nodeList = doc.getDocumentElement().getChildNodes().item(3).getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if( node.getNodeName().equals("Screen_bytNum")) {
						res.put("screen", "Sala " + node.getTextContent());
					}
					if( node.getNodeName().equals("Session_decSeats_Available")) {
						res.put("availableSeats", Long.parseLong(node.getTextContent()));
					}
				}
			}

			return res;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
}
