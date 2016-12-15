package mobi.allshoppings.cli;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.StoreTicketDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.StoreTicket;


public class StoreTicketDump extends AbstractCLI {

	private static final Logger log = Logger.getLogger(StoreTicketDump.class.getName());

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
			StoreTicketDAO stDao = (StoreTicketDAO)getApplicationContext().getBean("storeticket.dao.ref");
			DashboardAPDeviceMapperService mapper = (DashboardAPDeviceMapperService)getApplicationContext().getBean("dashboard.apdevice.mapper");

			log.log(Level.INFO, "Dumping Store Tickets....");

			List<StoreTicket> list = stDao.getAll(false);
			for( StoreTicket st : list ) {
				mapper.createStoreTicketDataForDates(st.getDate(), st.getDate(), st.getStoreId());
			}
			
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
}
