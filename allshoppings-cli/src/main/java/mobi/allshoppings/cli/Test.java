package mobi.allshoppings.cli;

import java.util.List;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.StatusHelper;


public class Test extends AbstractCLI {

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
			
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");

			List<Store> stores = storeDao.getUsingBrandAndStatus("modatelas_mx", StatusHelper.statusActive(), null);
			for(Store store : stores ) {
				List<APDAssignation> apdas = apdaDao.getUsingEntityIdAndEntityKind(store.getIdentifier(), EntityKind.KIND_STORE);
				for( APDAssignation apda : apdas ) {
					System.out.println(apda.getHostname());
				}
			}
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
