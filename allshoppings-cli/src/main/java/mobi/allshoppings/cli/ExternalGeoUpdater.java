package mobi.allshoppings.cli;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.ExternalGeoDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.ExternalGeo;


public class ExternalGeoUpdater extends AbstractCLI {

	private static final Logger log = Logger.getLogger(ExternalGeoUpdater.class.getName());

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {
		try {
			ExternalGeoDAO dao = (ExternalGeoDAO)getApplicationContext().getBean("externalgeo.dao.ref");

			log.log(Level.INFO, "Updating ExternalGeo ....");

			List<ExternalGeo> list = dao.getAll(true);
			for( ExternalGeo obj : list ) {
				obj.setEntityKind(EntityKind.KIND_STORE);
				if( obj.getVenue().equals("Sportium Cuautitlan")) {
					obj.setEntityId("d0f83d5d-e451-45ee-ad41-e2b4da086f2f");
				} else if( obj.getVenue().equals("Sportium Santa Fe")) {
					obj.setEntityId("1471039822614");
				} else if( obj.getVenue().equals("Sportium San Angel")) {
					obj.setEntityId("109ec028-6749-4332-9427-a39ccbfe7244");
				} else if( obj.getVenue().equals("Sportium Del VAlle")) {
					obj.setEntityId("970b5795-ad0a-49ac-a7eb-110d826c7b8f");
				} else if( obj.getVenue().equals("Sportium Arboledas")) {
					obj.setEntityId("8cd52856-7e34-4f19-8c45-e25e325d4ff9");
				} else if( obj.getVenue().equals("Sportium Lomas Verdes")) {
					obj.setEntityId("f2a79040-fefe-48f2-bd5d-e2db54ef5f23");
				} else if( obj.getVenue().equals("Sportium Satelite")) {
					obj.setEntityId("1471039822461");
				} else if( obj.getVenue().equals("Sportium Desierto")) {
					obj.setEntityId("c726776f-0a96-43d1-ae97-4169e595e5c6");
				} else if( obj.getVenue().equals("Sportium Coyoacan")) {
					obj.setEntityId("67af6e6e-9f11-4948-9887-65679bfd3d69");
				}
				
				if(obj.getPeriod().equals("2016-04")) {
					obj.setType(ExternalGeo.TYPE_WIFI);
				} else if(obj.getPeriod().equals("2016-05")) {
					obj.setType(ExternalGeo.TYPE_GPS);
				} else if(obj.getPeriod().equals("2016-08")) {
					obj.setType(ExternalGeo.TYPE_GPS_HOME);
				}
				
				log.log(Level.INFO, "Updating object " + obj.toString());
				dao.update(obj);
			}
						
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
