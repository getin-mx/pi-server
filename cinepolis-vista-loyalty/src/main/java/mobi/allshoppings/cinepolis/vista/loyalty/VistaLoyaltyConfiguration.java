package mobi.allshoppings.cinepolis.vista.loyalty;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;

public class VistaLoyaltyConfiguration {

	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("mx.com.cineticket.inetvis.vista.loyalty");
		return marshaller;
	}

	public VistaLoyaltyService vistaDataService(Jaxb2Marshaller marshaller) {
		VistaLoyaltyService client = new VistaLoyaltyService();
//		client.setDefaultUri("http://cinepolis-vpn.allshoppings.mobi:8030/visloyalty/vistaLoyalty.asmx");			// Dev
		client.setDefaultUri("http://cinepolis-vpn.allshoppings.mobi:8040/vistaloyaltyadmin/vistaLoyalty.asmx");	// Prod
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		client.setMessageFactory(new AxiomSoapMessageFactory());
		return client;
	}
	
}
