package mobi.allshoppings.cli;

import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.APDeviceMacMatchDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDeviceMacMatch;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;


public class APDeviceMacMatchDump extends AbstractCLI {

	private static final Logger log = Logger.getLogger(APDeviceMacMatchDump.class.getName());
	public static final long TWENTY_FOUR_HOURS = 86400000;
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "hostname", "APHostname").withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			APDeviceMacMatchDAO apdmmDao = (APDeviceMacMatchDAO)getApplicationContext().getBean("apdevicemacmatch.dao.ref");
			DeviceInfoDAO diDao = (DeviceInfoDAO)getApplicationContext().getBean("deviceinfo.dao.ref");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String hostname = null;
			
			try {
				if( options.has("hostname")) {
					hostname = (String)options.valueOf("hostname");
				}
				else usage(parser);
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Deleting old info...");
			apdmmDao.deleteUsingHostname(hostname);

			log.log(Level.INFO, "Getting info from APHEntry...");
			List<String> macs = CollectionFactory.createList();
			
			PersistenceManager pm;
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
			pm.currentTransaction().begin();

			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();
			BasicDBObject query = new BasicDBObject("hostname", hostname);
			DBCursor c1 = db.getCollection("APHEntry").find(query);
//			c1.sort(new BasicDBObject("creationDateTime", 1));
			c1.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			Iterator<DBObject> i = c1.iterator();

			// Fetches the device list
			while(i.hasNext()) {
				DBObject dbo = i.next();
				if( dbo.containsField("hostname") && dbo.containsField("mac")) {
					String dhostname = (String)dbo.get("hostname");
					String dmac = (String)dbo.get("mac");
					if( dhostname.equals(hostname)) {
						if( !macs.contains(dmac))
							macs.add(dmac);
					}
				}
				
//				if( macs.size() > 10 ) break;
			}
			
			jdoConn.close();
			pm.currentTransaction().commit();
			pm.close();

			log.log(Level.INFO, "Generating Mac List with " + macs.size() + " elements ...");
			List<APDeviceMacMatch> list = CollectionFactory.createList();
			for( String mac : macs ) {
				try {
					List<DeviceInfo> l2 = diDao.getUsingMAC(mac);
					if(!CollectionUtils.isEmpty(l2)) {
						for( DeviceInfo di : l2 ) {
							APDeviceMacMatch obj = new APDeviceMacMatch();
							obj.setDeviceUUID(di.getDeviceUUID());
							obj.setHostname(hostname);
							obj.setMac(mac);
							obj.setKey(apdmmDao.createKey());
							list.add(obj);
						}
					}
				} catch( Exception e ) {
					// nothing to do 
				}
			}
			
			log.log(Level.INFO, "Saving data with " + list.size() + " elements...");
			apdmmDao.createOrUpdate(null, list, true);
			
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
