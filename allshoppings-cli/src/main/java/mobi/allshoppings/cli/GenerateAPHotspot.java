package mobi.allshoppings.cli;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;

import org.springframework.context.ApplicationContext;

import com.inodes.datanucleus.model.KeyFactory;
import com.inodes.util.CollectionFactory;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APHotspot;

public class GenerateAPHotspot extends AbstractCLI {

	private final Calendar CALENDAR = Calendar.getInstance();
	
	private static final String DELETE_AFTER_DUMP_PARAM = "deleteAfterDump";
	private static final String RENAME_COLLECTION  = "renameCollection";
	private static final String COLLECTIONS_PARAM = "collections";
	
	
	private static final Logger LOG = Logger.getLogger(GenerateAPHotspot.class.getName());
	
	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts(DELETE_AFTER_DUMP_PARAM, "Do I have to "
				+ "delete the entity from the DB after dump?")
				.withRequiredArg().ofType(Boolean.class);
		parser.accepts(RENAME_COLLECTION, "Do I have to rename "
				+ "the collection before run?").withOptionalArg()
				.ofType(Boolean.class);
		parser.accepts(COLLECTIONS_PARAM, "Mongo table names "
				+ "(sepparated by comma) to search for "
				+ "APHotspots").withRequiredArg()
				.ofType(String.class);
		return parser;
	}
	
	public static void main(String[] args) throws ASException {
		OptionSet options = parser.parse(args);
		
		DumperHelper<APHotspot> dumper = new DumpFactory<APHotspot>().build(null, APHotspot.class);
		
		// Creates JDO Connection
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		pm.currentTransaction().begin();
		
		// Gets Native connection from JDO
		JDOConnection jdoConn = pm.getDataStoreConnection();
		DB db = (DB) jdoConn.getNativeConnection();
		if(!options.has(COLLECTIONS_PARAM))
			throw ASExceptionHelper.defaultException("A collecion"
					+ " is required", null);
		Boolean deleteAfterDump = options.has(
				DELETE_AFTER_DUMP_PARAM) &&
				(Boolean)options.valueOf(DELETE_AFTER_DUMP_PARAM);
		String backupFormat = "yyyyMMddHHmm";
		SimpleDateFormat backupSDF =
				new SimpleDateFormat(backupFormat);
		List<String> collections = CollectionFactory.createList();
		collections.addAll(Arrays.asList(options.valueOf(COLLECTIONS_PARAM).toString()
				.split(",")));
		if(options.has(RENAME_COLLECTION) &&
				(Boolean)options.valueOf(RENAME_COLLECTION)) {
			String collection = "APHotspot";
			String newCollection = "BK" + collection
					+ backupSDF.format(new Date());
			db.getCollection(collection)
					.rename(newCollection);
			collection = newCollection;
			if(!collections.contains(collection))
				collections.add(collection);
		} else if(!collections.contains("APHotspot"))
			collections.add("APHotspot");
		Map<Integer, List<APHotspot>> hotspots = CollectionFactory.createMap();
		GenerateAPHotspot generator = new GenerateAPHotspot();
		Calendar cal = Calendar.getInstance();
		for(String collection : collections) {
			LOG.log(Level.INFO, "Saving collection " +collection);
			hotspots.clear();
			DBCursor c = db.getCollection(collection).find();
			c.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			Iterator<DBObject> i = c.iterator();
			Date expected;
			try {
				expected = backupSDF.parse(collection.substring(collection.length()
						-backupFormat.length()));
			} catch(ParseException e) {
				LOG.log(Level.WARNING, "Invalid date in collection name: " +collection);
				continue;
			} while(i.hasNext())  {
				DBObject obj = i.next();
				APHotspot hotspot = new APHotspot();
				hotspot.setHostname(obj.get("hostname").toString());
				hotspot.setFirstSeen(generator.restoreDate(
						((Date)obj.get("firstSeen")).getTime(), expected));
				hotspot.setLastSeen((Date)obj.get("lastSeen"));
				hotspot.setMac(obj.get("mac").toString().toLowerCase());
				hotspot.setSignalDB((Integer)obj.get("signalDB"));
				hotspot.setCount((Integer)obj.get("count"));
				hotspot.setLastUpdate(generator.restoreDate(
						((Date)obj.get("lastUpdate")).getTime(), expected));
				cal.clear();
				cal.setTimeInMillis(((Date)obj.get("creationDateTime")).getTime());
				hotspot.setCreationDateTime(cal.getTime());
				hotspot.setKey(KeyFactory.stringToKey(obj.get("_id").toString()));
				if(!hotspots.containsKey(cal.get(Calendar.HOUR_OF_DAY))) {
					List<APHotspot> list = CollectionFactory.createList();
					list.add(hotspot);
					hotspots.put(cal.get(Calendar.HOUR_OF_DAY), list);
				} else hotspots.get(cal.get(Calendar.HOUR_OF_DAY)).add(hotspot);
				if(hotspots.size() %10000 == 0) LOG.log(Level.INFO, "Found "
						+hotspots.size() +" APHotspost so far");
				
			}
			c.close();
			for(Iterator<Integer> hours = hotspots.keySet().iterator();
					hours.hasNext();) {
				List<APHotspot> hotspotList = hotspots.get(hours.next());
				for(APHotspot hot : hotspotList) dumper.dump(hot);
				dumper.flush();
				dumper.dispose();
				LOG.log(Level.INFO, "Saved " +hotspots.size() +" APHotspots so far");
			}
		} if(deleteAfterDump) {
			Set<String> all_collections = db.getCollectionNames();
			//Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.setTimeInMillis(System.currentTimeMillis());
			cal.add(Calendar.WEEK_OF_YEAR, -1);
			for(String coll : all_collections) {
				try {
					if(coll.startsWith("BK") &&
							backupSDF.parse(coll.substring(
									coll.length()
									-backupFormat.length()))
							.compareTo(cal.getTime()) <= 0)
						db.getCollection(coll).drop();
				} catch(ParseException e) {
					LOG.log(Level.INFO, "Could not remove "
							+ "collection " +coll, e);
				}
			}//drops all backups
		}
		LOG.log(Level.INFO, "Finish dumping APHotspots");
	}
	
	private Date restoreDate(long time, Date expected) {
		CALENDAR.clear();
		CALENDAR.setTimeInMillis(time);
		Calendar expectedTime = Calendar.getInstance();
		expectedTime.setTime(expected);
		int yearsDiff = Math.abs(CALENDAR.get(Calendar.YEAR)
				-expectedTime.get(Calendar.YEAR));
		int monthsDiff = Math.abs(CALENDAR.get(Calendar.MONTH)
				-expectedTime.get(Calendar.MONTH));
		int daysDiff = Math.abs(CALENDAR.get(Calendar.DATE)
				-expectedTime.get(Calendar.DATE));
		if(yearsDiff > 1 || (yearsDiff != 0 && (monthsDiff > 1 || daysDiff > 1)))
			CALENDAR.set(Calendar.YEAR, expectedTime.get(Calendar.YEAR));
		if(monthsDiff > 1 || (monthsDiff != 0 && daysDiff > 1))
			CALENDAR.set(Calendar.MONTH, expectedTime.get(Calendar.MONTH));
		return CALENDAR.getTime();
	}
	
}
