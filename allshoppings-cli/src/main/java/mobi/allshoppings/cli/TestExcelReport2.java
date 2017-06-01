package mobi.allshoppings.cli;

import java.util.List;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExcelExportHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.StatusHelper;


public class TestExcelReport2 extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			
			ExcelExportHelper helper = (ExcelExportHelper)getApplicationContext().getBean("excel.export.helper");
			BrandDAO brandDao = (BrandDAO)getApplicationContext().getBean("brand.dao.ref");
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			@SuppressWarnings("unused")
			OptionSet options = parser.parse(args);

			final String FROM_DATE = "2017-01-02"; // Retail Calendar initial 2017 day
			final String TO_DATE = "2017-04-30"; // Retail Calendar final march day
			final String outDir = "/usr/local/allshoppings/dump";
			
			List<Brand> brands = brandDao.getUsingStatusAndRange(StatusHelper.statusActive(), null, "name");
			for( Brand brand : brands ) {
				List<Store> stores = storeDao.getUsingBrandAndStatus(brand.getIdentifier(), StatusHelper.statusActive(), "name");
				for( Store store : stores ) {
					System.out.println("Listing " + store.getName());
					try {
						helper.export(store.getIdentifier(), FROM_DATE, TO_DATE, 4, outDir);
					} catch( Exception e ) {
						e.printStackTrace();
					}
				}
			}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
