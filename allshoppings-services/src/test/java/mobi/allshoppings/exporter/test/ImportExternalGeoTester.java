package mobi.allshoppings.exporter.test;

import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.dao.ExternalGeoDAO;
import mobi.allshoppings.dao.spi.ExternalGeoDAOJDOImpl;
import mobi.allshoppings.exporter.ExternalGeoImporter;

public class ImportExternalGeoTester extends TestCase {

	@Test
	public void testImportFromFile() {
		try {
			
			ExternalGeoDAO dao = new ExternalGeoDAOJDOImpl();
			ExternalGeoImporter importer = new ExternalGeoImporter();
			
			importer.setDao(dao);
			importer.importInfo("Sportium Arboledas", "2016-04", "/tmp/geoloc_sportium_Arboledas.csv");
			importer.importInfo("Sportium Coyoacan", "2016-04", "/tmp/geoloc_sportium_Coyoacan.csv");
			importer.importInfo("Sportium Cuautitlan", "2016-04", "/tmp/geoloc_sportium_Cuautitlan.csv");
			importer.importInfo("Sportium Del VAlle", "2016-04", "/tmp/geoloc_sportium_Del_Valle.csv");
			importer.importInfo("Sportium Desierto", "2016-04", "/tmp/geoloc_sportium_Desierto.csv");
			importer.importInfo("Sportium Lomas Verdes", "2016-04", "/tmp/geoloc_sportium_Lomas_Verdes.csv");
			importer.importInfo("Sportium San Angel", "2016-04", "/tmp/geoloc_sportium_San_Angel.csv");
			importer.importInfo("Sportium Santa Fe", "2016-04", "/tmp/geoloc_sportium_Santa_Fe.csv");
			importer.importInfo("Sportium Satelite", "2016-04", "/tmp/geoloc_sportium_Satelite.csv");

		} catch( Throwable t ) {
			t.printStackTrace();
			fail(t.getMessage());
		}
	}	
}