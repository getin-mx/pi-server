package mobi.allshoppings.cinepolis.vista.voucher;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;

public class VistaVoucherConfiguration {

	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("mx.com.cineticket.inetvis.vista.voucher");
		return marshaller;
	}

	public VistaVoucherService vistaDataService(Jaxb2Marshaller marshaller) {
		VistaVoucherService client = new VistaVoucherService();
//		client.setDefaultUri("http://cinepolis-vpn.allshoppings.mobi:8020/WSVistaVoucher/WSVistaVoucher.asmx");		// Dev
		client.setDefaultUri("http://cinepolis-vpn.allshoppings.mobi:8020/WSVistaVoucher/WSVistaVoucher.asmx");		// Prod
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		client.setMessageFactory(new AxiomSoapMessageFactory());
		return client;
	}
	
}
