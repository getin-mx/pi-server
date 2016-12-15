package mobi.allshoppings.location;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.tools.CollectionFactory;


public class LocationUpdateReportService {

	private static final Logger log = Logger.getLogger(LocationUpdateReportService.class.getName());

	public static final String LESS_ONE_MINUTE = "LESS_ONE_MINUTE";
	public static final String LESS_FIVE_MINUTES = "LESS_FIVE_MINUTES";
	public static final String LESS_FIFTEEN_MINUTES = "LESS_FIFTEEN_MINUTES";
	public static final String LESS_THIRTY_MINUTES = "LESS_THIRTY_MINUTES";
	public static final String LESS_ONE_HOUR = "LESS_ONE_HOUR";
	public static final String LESS_TWO_HOURS = "LESS_TWO_HOURS";
	public static final String LESS_FIVE_HOURS = "LESS_FIVE_HOURS";
	public static final String LESS_ONE_DAY = "LESS_ONE_DAY";
	public static final String LESS_TWO_DAYS = "LESS_TWO_DAYS";
	public static final String LESS_THREE_DAYS = "LESS_THREE_DAYS";
	public static final String LESS_ONE_WEEK = "LESS_ONE_WEEK";
	public static final String LESS_TWO_WEEKS = "LESS_TWO_WEEKS";
	public static final String LESS_THREE_WEEKS = "LESS_THREE_WEEKS";
	public static final String LESS_ONE_MONTH = "LESS_ONE_MONTH";
	public static final String LESS_TWO_MONTHS = "LESS_TWO_MONTHS";
	public static final String OTHERS = "OTHERS";

	public static final long ONE_MINUTE = 60000L;
	public static final long FIVE_MINUTES = 300000L;
	public static final long FIFTEEN_MINUTES = 900000L;
	public static final long THIRTY_MINUTES = 1800000L;
	public static final long ONE_HOUR = 3600000L;
	public static final long TWO_HOURS = 7200000L;
	public static final long FIVE_HOURS = 18000000L;
	public static final long ONE_DAY = 86400000L;
	public static final long TWO_DAYS = 172800000L;
	public static final long THREE_DAYS = 259200000L;
	public static final long ONE_WEEK = 604800000L;
	public static final long TWO_WEEKS = 1209600000L;
	public static final long THREE_WEEKS = 1814400000L;
	public static final long ONE_MONTH = 2419200000L;
	public static final long TWO_MONTHS = 4838400000L;

	public Map<String,Long> getLocationUpdateReport(boolean cumulative, boolean showProgress) throws ASException, IOException {

		// Prepares local variables
		long count = 0;
		Map<String, Long> results;
		long batchSize = 100;

		long initTime = new Date().getTime();

		results = CollectionFactory.createMap();
		results.put(LESS_ONE_MINUTE,0L);
		results.put(LESS_FIVE_MINUTES,0L);
		results.put(LESS_FIFTEEN_MINUTES,0L);
		results.put(LESS_THIRTY_MINUTES,0L);
		results.put(LESS_ONE_HOUR,0L);
		results.put(LESS_TWO_HOURS,0L);
		results.put(LESS_FIVE_HOURS,0L); 
		results.put(LESS_ONE_DAY,0L);
		results.put(LESS_TWO_DAYS,0L);
		results.put(LESS_THREE_DAYS,0L);
		results.put(LESS_ONE_WEEK,0L);
		results.put(LESS_TWO_WEEKS,0L);
		results.put(LESS_THREE_WEEKS,0L);
		results.put(LESS_ONE_MONTH,0L);
		results.put(LESS_TWO_MONTHS,0L);
		results.put(OTHERS,0L);

		// Creates JDO Connection
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		pm.currentTransaction().begin();

		// Gets Native connection from JDO
		JDOConnection jdoConn = pm.getDataStoreConnection();
		DB db = (DB)jdoConn.getNativeConnection();

		long myTime = new Date().getTime();

		try {
			// Prepares the cursor
			DBCursor c = db.getCollection(DeviceLocation.class.getSimpleName()).find();
			c.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			Iterator<DBObject> i = c.iterator();

			// Counts the total records to export
			long totalRecords = c.count();
			log.log(Level.INFO, totalRecords + " records found");

			while(i.hasNext()) {
				// Gets the DB Object
				DBObject dbo = i.next();

				Date objDate = (Date)dbo.get("lastUpdate");
				if( objDate != null ) {
					long objTime = myTime - objDate.getTime();

					if( cumulative ) {
						if( objTime < ONE_MINUTE ) add(LESS_ONE_MINUTE, results);
						if( objTime < FIVE_MINUTES ) add(LESS_FIVE_MINUTES, results);
						if( objTime < FIFTEEN_MINUTES ) add(LESS_FIFTEEN_MINUTES, results);
						if( objTime < THIRTY_MINUTES ) add(LESS_THIRTY_MINUTES, results);
						if( objTime < ONE_HOUR ) add(LESS_ONE_HOUR, results);
						if( objTime < TWO_HOURS ) add(LESS_TWO_HOURS, results);
						if( objTime < FIVE_HOURS ) add(LESS_FIVE_HOURS, results);
						if( objTime < ONE_DAY ) add(LESS_ONE_DAY, results);
						if( objTime < TWO_DAYS ) add(LESS_TWO_DAYS, results);
						if( objTime < THREE_DAYS ) add(LESS_THREE_DAYS, results);
						if( objTime < ONE_WEEK ) add(LESS_ONE_WEEK, results);
						if( objTime < TWO_WEEKS ) add(LESS_TWO_WEEKS, results);
						if( objTime < THREE_WEEKS ) add(LESS_THREE_WEEKS, results);
						if( objTime < ONE_MONTH ) add(LESS_ONE_MONTH, results);
						if( objTime < TWO_MONTHS ) add(LESS_TWO_MONTHS, results);
						add(OTHERS, results);

					} else {

						if( objTime < ONE_MINUTE ) add(LESS_ONE_MINUTE, results);
						else if( objTime < FIVE_MINUTES ) add(LESS_FIVE_MINUTES, results);
						else if( objTime < FIFTEEN_MINUTES ) add(LESS_FIFTEEN_MINUTES, results);
						else if( objTime < THIRTY_MINUTES ) add(LESS_THIRTY_MINUTES, results);
						else if( objTime < ONE_HOUR ) add(LESS_ONE_HOUR, results);
						else if( objTime < TWO_HOURS ) add(LESS_TWO_HOURS, results);
						else if( objTime < FIVE_HOURS ) add(LESS_FIVE_HOURS, results);
						else if( objTime < ONE_DAY ) add(LESS_ONE_DAY, results);
						else if( objTime < TWO_DAYS ) add(LESS_TWO_DAYS, results);
						else if( objTime < THREE_DAYS ) add(LESS_THREE_DAYS, results);
						else if( objTime < ONE_WEEK ) add(LESS_ONE_WEEK, results);
						else if( objTime < TWO_WEEKS ) add(LESS_TWO_WEEKS, results);
						else if( objTime < THREE_WEEKS ) add(LESS_THREE_WEEKS, results);
						else if( objTime < ONE_MONTH ) add(LESS_ONE_MONTH, results);
						else if( objTime < TWO_MONTHS ) add(LESS_TWO_MONTHS, results);
						else add(OTHERS, results);
					}
				}

				count++;

				if( count % batchSize == 0 && showProgress)
					log.log(Level.INFO, "Processed " + count + " of " + totalRecords + "...");
			}

			long finalTime = new Date().getTime();
			log.log(Level.INFO, "Finally Processed " + count + " of " + totalRecords + " in " + (finalTime - initTime) + "ms");

			return results;

		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			jdoConn.close();
			pm.currentTransaction().commit();
		}

	}

	private void add(String key, Map<String,Long> results) {
		Long val = results.get(key);
		if( val == null ) val = new Long(0);
		val++;
		results.put(key, val);
	}
}
