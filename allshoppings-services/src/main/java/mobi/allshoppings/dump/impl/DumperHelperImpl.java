package mobi.allshoppings.dump.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.datanucleus.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.inodes.datanucleus.model.Blob;
import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.dump.DumperFileNameResolver;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.DumperPlugin;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tools.CollectionFactory;

public class DumperHelperImpl<T extends ModelKey> implements DumperHelper<T> {

	private static final SimpleDateFormat year = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat month = new SimpleDateFormat("MM");
	private static final SimpleDateFormat day = new SimpleDateFormat("dd");
	private static final SimpleDateFormat hour = new SimpleDateFormat("HH");
	private static final Logger log = Logger.getLogger(DumperHelperImpl.class.getName());
	private static final Gson gson = new Gson();
	private static final int BUFFER = 100;

	private Class<T> clazz;
	private String baseDir;
	private List<DumperPlugin<ModelKey>> registeredPlugins;
	private DumperFileNameResolver<ModelKey> fileNameResolver;
	
	public DumperHelperImpl(String baseDir, Class<T> clazz) {
		this.baseDir = baseDir;
		this.clazz = clazz;
		registeredPlugins = CollectionFactory.createList();
	}

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#registerPlugin(DumperPlugin)
	 */
	@Override
	public void registerPlugin(DumperPlugin<ModelKey> plugin) {
		if(!registeredPlugins.contains(plugin))
			registeredPlugins.add(plugin);
	}

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#unregisterPlugin(DumperPlugin)
	 */
	@Override
	public void unregisterPlugin(DumperPlugin<ModelKey> plugin) {
		registeredPlugins.remove(plugin);
	}

	@Override
	public void registerFileNameResolver(DumperFileNameResolver<ModelKey> fileNameResolver) {
		this.fileNameResolver = fileNameResolver;
	}
	
	@Override
	public void unregisterFileNameResolver() {
		fileNameResolver = null;
	}
	
	/**
	 * @see mobi.allshoppings.dump.DumperHelper#dumpModelKey(String, Date, Date, boolean)
	 */
	@Override
	public void dumpModelKey(String collection, Date fromDate, Date toDate, boolean deleteAfterDump, boolean moveCollectionBeforeDump) throws ASException {

		// Prepares local variables
		long count = 0;
		long processed = 0;
		long batchSize = 100;

		long initTime = new Date().getTime();

		// Creates JDO Connection
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		pm.currentTransaction().begin();

		// Gets Native connection from JDO
		JDOConnection jdoConn = pm.getDataStoreConnection();
		DB db = (DB)jdoConn.getNativeConnection();

		// Moves the collection if requested
		if( moveCollectionBeforeDump) {
			SimpleDateFormat backupSDF = new SimpleDateFormat("yyyyMMddHHmm");
			String newCollection = "BK" + collection + backupSDF.format(new Date());
			db.getCollection(collection).rename(newCollection);
			collection = newCollection;
		}
				
		try {
			// Prepares the cursor
			DBCursor c = db.getCollection(collection).find();
			c.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			Iterator<DBObject> i = c.iterator();

			// Counts the total records to export
			long totalRecords = c.count();
			log.log(Level.INFO, totalRecords + " records found");

			while(i.hasNext()) {
				// Gets the DB Object
				DBObject dbo = i.next();

				T obj = clazz.newInstance();
				setPropertiesFromDBObject(dbo, obj);
				if ((fromDate == null || fromDate.before(obj.getCreationDateTime()))
						&& (toDate == null || toDate.after(obj.getCreationDateTime()))) {

					// Apply pre dump plugins
					applyPreDumpPlugins(obj);
					
					// Dumps the object
					dump(obj);
					
					// Apply post dump plugins
					applyPostDumpPlugins(obj);
					
					processed ++;

					if( deleteAfterDump ) {
						db.getCollection(collection).remove(dbo);
					}
				}
				obj = null;
				count++;

				if( count % batchSize == 0 )
					log.log(Level.INFO, "Processed " + count + " of " + totalRecords + " with " + processed + " results...");
			}

			long finalTime = new Date().getTime();
			log.log(Level.INFO, "Finally Processed " + count + " of " + totalRecords + " with " + processed + " results in " + (finalTime - initTime) + "ms");
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			jdoConn.close();
			pm.currentTransaction().commit();
		}
	}

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#applyPreDumpPlugins(ModelKey)
	 */
	public void applyPreDumpPlugins(T obj) throws ASException {
		for(DumperPlugin<ModelKey> plugin : registeredPlugins) {
			if(plugin.isAvailableFor(obj)) plugin.preDump(obj);
		}
	}
	
	/**
	 * @see mobi.allshoppings.dump.DumperHelper#applyPostDumpPlugins(ModelKey)
	 */
	public void applyPostDumpPlugins(T obj) throws ASException {
		for(DumperPlugin<ModelKey> plugin : registeredPlugins) {
			if(plugin.isAvailableFor(obj)) plugin.postDump(obj);
		}
	}

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#dump(ModelKey)
	 */
	@Override
	public void dump(T obj) throws IOException {
		this.dump(gson.toJson(obj), baseDir, clazz.getSimpleName(), obj.getCreationDateTime(), obj);
	}

	public void dump(String jsonRep, String baseDir, String baseName, Date forDate, T element) throws IOException {
		String fileName = resolveDumpFileName(baseDir, baseName, forDate, element);
		File file = new File(fileName);
		dump( jsonRep, file);
	}

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#resolveDumpFileName(Date)
	 */
	@Override
	public String resolveDumpFileName( Date forDate, T element ) {
		return resolveDumpFileName(baseDir, clazz.getSimpleName(), forDate, element);
	}
	
	public String resolveDumpFileName(String baseDir, String baseName, Date forDate, T element) {

		String fileName = null;
		if( fileNameResolver != null && element != null ) {
			fileName = fileNameResolver.resolveDumpFileName(baseDir, baseName, forDate, element);
		}

		if( fileName == null ) {

			String myYear = year.format(forDate);
			String myMonth = month.format(forDate);
			String myDay = day.format(forDate);
			String myHour = hour.format(forDate);

			StringBuffer sb = new StringBuffer();
			sb.append(baseDir);
			if(!baseDir.endsWith(File.separator)) sb.append(File.separator);
			sb.append(myYear).append(File.separator);
			sb.append(myMonth).append(File.separator);
			sb.append(myDay).append(File.separator);
			sb.append(myHour).append(File.separator);
			sb.append(baseName).append(".json");

			return sb.toString();
		} else {
			return fileName;
		}
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

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#iterator(Date, Date)
	 */
	@Override
	public Iterator<T> iterator(Date fromDate, Date toDate) {
		return new DumpEnhancedIterator(fromDate, toDate);
	}


	/**
	 * @see mobi.allshoppings.dump.DumperHelper#iterator(Date, Date)
	 */
	@Override
	public Iterator<String> stringIterator(Date fromDate, Date toDate) {
		return new DumpStringIterator(fromDate, toDate);
	}

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#iterator(Date, Date)
	 */
	@Override
	public Iterator<JSONObject> jsonIterator(Date fromDate, Date toDate) {
		return new DumpJSONIterator(fromDate, toDate);
	}

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#retrieveModelKeyList(Date, Date)
	 */
	@Override
	public List<T> retrieveModelKeyList(Date fromDate, Date toDate) throws IOException {
		List<T> ret = CollectionFactory.createList();
		
		Date curDate = new Date(fromDate.getTime());
		while(curDate.before(toDate) || curDate.equals(toDate)) {
			
			File f = new File(resolveDumpFileName(baseDir, clazz.getSimpleName(), curDate, null));
			if( f.exists() && f.canRead()) {
				try(BufferedReader br = new BufferedReader(new FileReader(f))) {
				    for(String line; (line = br.readLine()) != null; ) {
				        try {
				        	T element = gson.fromJson(line, clazz);
				        	ret.add(element);
				        } catch( Exception e ) {
				        	log.log(Level.SEVERE, e.getMessage(), e);
				        }
				    }
					br.close();
				}
			}
			
			curDate = new Date(curDate.getTime() + 3600000);
		}
		
		return ret;
	}
	
	@Override
	public void fakeModelKey(Date fromDate, Date toDate) throws ASException {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(toDate);
		
		Calendar cal2 = Calendar.getInstance();
		
		Date fromWorkDate = DateUtils.truncate(fromDate, Calendar.DATE);
		Date toWorkDate = DateUtils.addMinutes(DateUtils.addDays(fromWorkDate, 1), -1); 
		
		Iterator<T> i = iterator(fromWorkDate, toWorkDate);
		while(i.hasNext()) {
			T obj = i.next();
			if(obj instanceof DeviceLocationHistory) {
				DeviceLocationHistory ele = (DeviceLocationHistory)obj;
				if( ele != null && ele.getLastUpdate() != null ) {
					cal2.setTime(ele.getCreationDateTime());
					cal2.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DATE));
					ele.setCreationDateTime(cal2.getTime());
				}
			}
			if(obj instanceof DeviceWifiLocationHistory) {
				DeviceWifiLocationHistory ele = (DeviceWifiLocationHistory)obj;
				if( ele != null && ele.getLastUpdate() != null ) {
					cal2.setTime(ele.getCreationDateTime());
					cal2.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DATE));
					ele.setCreationDateTime(cal2.getTime());
				}
			}

			if( obj != null && obj.getLastUpdate() != null ) {
				cal2.setTime(obj.getLastUpdate());
				cal2.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DATE));
				obj.setLastUpdate(cal2.getTime());
			}

			try {
				dump(obj);
			} catch( Exception e ) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Sets properties of an entity object based in the attributes received in
	 * JSON representation
	 * 
	 * @param jsonObj
	 *            The JSON representation which contains the input data
	 * @param obj
	 *            The object to be modified
	 * @param excludeFields
	 *            a list of fields which cannot be modified
	 */
	public void setPropertiesFromDBObject(DBObject dbo, Object obj) {
		Key objKey = new Key(dbo.get("_id").toString());
		((ModelKey)obj).setKey(objKey);

		for (Iterator<String> it = dbo.keySet().iterator(); it.hasNext(); ) {
			try {
				String key = it.next();
				if(!key.equals("_id")) {
					Object fieldValue = dbo.get(key);
					if( fieldValue instanceof DBObject ) {
						Object data = PropertyUtils.getProperty(obj, key);
						setPropertiesFromDBObject((DBObject)fieldValue, data);
					} else {
						if (PropertyUtils.getPropertyType(obj, key) == Text.class) {
							Text text = new Text(fieldValue.toString());
							PropertyUtils.setProperty(obj, key, text);
						} else if (PropertyUtils.getPropertyType(obj, key) == Email.class){
							Email mail = new Email(((String)safeString(fieldValue)).toLowerCase());
							PropertyUtils.setProperty(obj, key, mail);
						} else if (PropertyUtils.getPropertyType(obj, key) == Date.class) {
							PropertyUtils.setProperty(obj, key, fieldValue);
						} else if (PropertyUtils.getPropertyType(obj, key) == Key.class) {
							String[] parts = ((String)fieldValue).split("\"");
							Class<?> c = Class.forName("mobi.allshoppings.model." + parts[0].split("\\(")[0]);
							Key data = new KeyHelperGaeImpl().obtainKey(c, parts[1]);
							PropertyUtils.setProperty(obj, key, data);
						} else if (PropertyUtils.getPropertyType(obj, key) == Blob.class) {
							Blob data = new Blob(Base64.decode((String)fieldValue));
							PropertyUtils.setProperty(obj, key, data);
						} else if (fieldValue instanceof JSONArray) {
							JSONArray array = (JSONArray)fieldValue;
							Collection<String> col = new ArrayList<String>();
							for (int idx = 0; idx < array.length(); idx++) {
								String value = array.getString(idx);
								col.add(value);
							}
							BeanUtils.setProperty(obj, key, col);
						} else {
							BeanUtils.setProperty(obj, key, safeString(fieldValue));
						}
					}
				}
			} catch (Exception e) {
				// ignore property
				log.log(Level.INFO, "Error setting properties from DBO", e);
			}
		}
	}

	public Object safeString(Object from) {
		try {
			if( from instanceof String ) {
				return new String(((String)from).getBytes());
			} else {
				return from;
			}
		} catch( Exception e ) {
			log.log(Level.INFO, e.getMessage(), e);
			return from;
		}
	}

	public class DumpEnhancedIterator implements Iterator<T> {
		
		private Iterator<String> iterator;
		
		public DumpEnhancedIterator(Date fromDate, Date toDate) {
			iterator = new DumpStringIterator(fromDate, toDate);
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public T next() {
			while(iterator.hasNext()) {
				String el = iterator.next();
				try {
					T element = gson.fromJson(el, clazz);
					return element;
				} catch( Exception e ) {
					log.log(Level.INFO, el, e);
				}
			}
			return null;
		}

		@Override
		public void remove() {
			// Not Implemented
		}
		
	}

	public class DumpJSONIterator implements Iterator<JSONObject> {
		
		private Iterator<String> iterator;
		
		public DumpJSONIterator(Date fromDate, Date toDate) {
			iterator = new DumpStringIterator(fromDate, toDate);
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public JSONObject next() {
			JSONObject element = new JSONObject(iterator.next());
			return element;
		}

		@Override
		public void remove() {
			// Not Implemented
		}
		
	}

	public class DumpStringIterator implements Iterator<String> {

		private List<String> elements;
		private int counter;
		private Date fromDate;
		private Date toDate;
		private Date curDate;
		private BufferedReader br;
		
		public DumpStringIterator(Date fromDate, Date toDate) {
			elements = CollectionFactory.createList();
			counter = 0;
			this.fromDate = fromDate;
			this.toDate = toDate;
		}
		
		@Override
		public boolean hasNext() {
			
			// Checks if it has more elements in this iteration;
			if( counter < elements.size()) 
				return true;
			
			// Iterates over candidate elements
			elements.clear();
			counter = 0;
			
			// Has an open buffered Reader
			int i = 0;
			if( br != null ) {
				
				// Reads until EOF or Buffer Filled
				try {
					for(String line; i < BUFFER && (line = br.readLine()) != null; ) {
						try {
							elements.add(line);
							i++;
						} catch( Exception e ) {
							log.log(Level.SEVERE, e.getMessage(), e);
						}
					}
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
				
				// If there was no read, then close the file and try with a new one
				if( i == 0 ) {
					try {
						br.close();
					} catch (IOException e) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
					br = null;
				}
			}
			
			while( curDate == null || curDate.before(toDate) && elements.size() == 0 ) {

				// Establishes the process Date
				if( curDate == null )
					curDate = new Date(fromDate.getTime());
				else
					curDate = new Date(curDate.getTime() + 3600000);

				if(curDate.equals(toDate) || curDate.after(toDate))
					break;
				
				i = 0;
				File f = new File(resolveDumpFileName(baseDir, clazz.getSimpleName(), curDate, null));
				if( f.exists() && f.canRead()) {
					try {
						br = new BufferedReader(new FileReader(f));
						for(String line; i < BUFFER && (line = br.readLine()) != null; ) {
							try {
								elements.add(line);
								i++;
							} catch( Exception e ) {
								log.log(Level.SEVERE, e.getMessage(), e);
							}
						}
						
						if( elements.size() > 0 ) break;
						
					} catch( Exception e ) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
				}

			}

			if( elements.size() > 0 ) 
				return true;

			return false;
		}

		@Override
		public String next() {

			String ret = elements.get(counter);
			counter++;
			
			return ret;
		}

		@Override
		public void remove() {
			// Not Implemented
		}
		
	}
}
