package mobi.allshoppings.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;

import org.springframework.context.ApplicationContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;


public class VisitsExport extends AbstractCLI {

	private static final Logger log = Logger.getLogger(VisitsExport.class.getName());
	public static final long TWENTY_FOUR_HOURS = 86400000;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat year = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat month = new SimpleDateFormat("MM");
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Export from date (yyyy-MM-dd)").withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Export to date (yyyy-MM-dd)").withRequiredArg().ofType( String.class );
		parser.accepts( "outDir", "Export Directory").withRequiredArg().ofType( String.class );		
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			KeyHelper keyHelper = (KeyHelper)getApplicationContext().getBean("key.helper");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			List<Store> stores = storeDao.getUsingStatusAndRange(StatusHelper.statusActive(), null);
			Map<String, String> storeNameCache = CollectionFactory.createMap();
			Map<String, Store> storeCache = CollectionFactory.createMap();
			for( Store store : stores ) {
				storeCache.put(store.getIdentifier(), store);
				String name = keyHelper.resolveKey(store.getName());
				storeNameCache.put(store.getIdentifier(), name);
				log.log(Level.INFO, store.getName() + ": " + name);
			}
			
			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
			String outDir = null;
			
			try {
				if( options.has("fromDate")) {
					sFromDate = (String)options.valueOf("fromDate");
					fromDate = sdf.parse(sFromDate);
				}

				if( options.has("toDate")) {
					sToDate = (String)options.valueOf("toDate");
					toDate = sdf.parse(sToDate);
				}
				
				if( options.has("outDir")) {
					outDir = (String)options.valueOf("outDir");
				}
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			
			log.log(Level.INFO, "Getting info from APDVisit...");
			
			PersistenceManager pm;
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
			pm.currentTransaction().begin();

			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();
			
			BasicDBObject fields = new BasicDBObject();
			fields.put("entityId", 1);
			fields.put("mac", 1);
			fields.put("checkinType", 1);
			fields.put("checkinStarted", 1);
			fields.put("checkinFinished", 1);
			DBCursor c1 = db.getCollection("APDVisit").find(new BasicDBObject(),fields);
			c1.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			Iterator<DBObject> i = c1.iterator();

			log.log(Level.INFO, "Processing " + c1.size() + " results...");
			long count = 0;
			// Fetches the visit list
			while(i.hasNext()) {
				DBObject dbo = i.next();
				if( dbo.containsField("entityId") && dbo.containsField("mac") && dbo.containsField("checkinType") && dbo.containsField("checkinStarted") && dbo.containsField("checkinFinished")) {
					String identifier = (String)dbo.get("entityId");
					String dmac = (String)dbo.get("mac");
					Integer checkinType = (Integer)dbo.get("checkinType");
					Date checkinStarted = (Date)dbo.get("checkinStarted");
					Date checkinFinished = (Date)dbo.get("checkinFinished");

					try {
						if( storeCache.containsKey(identifier)) {
							if( fromDate.before(checkinStarted) && toDate.after(checkinFinished)) {
								if(dmac != null ) {
									dmac = dmac.toLowerCase();

									int permanence = (int)((checkinFinished.getTime() - checkinStarted.getTime()) / 60000);
									StringBuffer rec = new StringBuffer();
									rec.append(identifier).append(",");
									rec.append(storeCache.get(identifier).getBrandId()).append(",");
									rec.append(checkinType).append(",");
									rec.append(dmac).append(",");
									rec.append(sdfFull.format(checkinStarted)).append(",");
									rec.append(sdfFull.format(checkinFinished)).append(",");
									rec.append(permanence);

									dump(rec.toString(), outDir, storeNameCache.get(identifier), checkinStarted);

								}
							}
						}
					} catch( Exception e ) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
				}
				
				count++;
				if(count % 5000 == 0 ) {
					log.log(Level.INFO, "Processing APDVisit Record " + count + " of " + c1.size());
				}
				
//				if( count > 10000 ) break;
			}

			jdoConn.close();
			pm.currentTransaction().commit();
			pm.close();
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}

	public static void dump(String jsonRep, String baseDir, String baseName, Date forDate) throws IOException {
		String fileName = resolveDumpFileName(baseDir, baseName, forDate);
		File file = new File(fileName);
		dump( jsonRep, file);
	}

	public static String resolveDumpFileName(String baseDir, String baseName, Date forDate) {
		String myYear = year.format(forDate);
		String myMonth = month.format(forDate);
		
		StringBuffer sb = new StringBuffer();
		sb.append(baseDir);
		if(!baseDir.endsWith(File.separator)) sb.append(File.separator);
		sb.append("visits").append(File.separator);
		sb.append(myYear).append(File.separator);
		sb.append(myMonth).append(File.separator);
		sb.append(baseName).append(".csv");

		return sb.toString();
	}

	public static void dump(String jsonRep, File file) throws IOException {
		synchronized(file.getAbsolutePath()) {

			FileOutputStream fos ;

			File dir = file.getParentFile();
			if( !dir.exists() ) dir.mkdirs();
			if( !file.exists() ) {
				fos = new FileOutputStream(file, false);
			} else {
				fos = new FileOutputStream(file, true);
			}

			try {
				fos.write(jsonRep.getBytes());
				fos.write("\n".getBytes());
				fos.flush();
				fos.close();
			} catch( Throwable t ) {
				throw t;
			} finally {
				fos.flush();
				fos.close();
			}
		}
	}

}
