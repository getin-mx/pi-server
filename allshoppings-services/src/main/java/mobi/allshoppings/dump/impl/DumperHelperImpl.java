package mobi.allshoppings.dump.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.datanucleus.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.inodes.datanucleus.model.Blob;
import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.dump.CloudFileManager;
import mobi.allshoppings.dump.DumperFileNameResolver;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.DumperPlugin;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.GsonFactory;

public class DumperHelperImpl<T extends ModelKey> implements DumperHelper<T> {

	public static final long TIMEFRAME_ONE_HOUR = 3600000;
	public static final long TIMEFRAME_ONE_DAY = 86400000;
	public static final long TWENTY_THREE_HOURS = 82800000;
	
	private static final SimpleDateFormat year = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat month = new SimpleDateFormat("MM");
	private static final SimpleDateFormat day = new SimpleDateFormat("dd");
	private static final SimpleDateFormat hour = new SimpleDateFormat("HH");
	private static final Logger log = Logger.getLogger(DumperHelperImpl.class.getName());
	private static final int BUFFER = 100;
	
	private static Gson gson = GsonFactory.getInstance();

	private Class<T> clazz;
	private String baseDir;
	private boolean tmpDir = false;
	private List<DumperPlugin<ModelKey>> registeredPlugins;
	private DumperFileNameResolver<ModelKey> fileNameResolver;
	private List<String> currentCachedFileNames;
	private String filter;
	private CloudFileManager cfm;
	private String lastFileUsed;
	private T instance;
	private long timeFrame;
	
	public DumperHelperImpl(String baseDir, Class<T> clazz) {
		this.baseDir = baseDir;
		this.clazz = clazz;
		registeredPlugins = CollectionFactory.createList();
		
		TimeZone tz = TimeZone.getTimeZone("GMT");
		year.setTimeZone(tz);
		month.setTimeZone(tz);
		day.setTimeZone(tz);
		hour.setTimeZone(tz);

		this.timeFrame = TIMEFRAME_ONE_HOUR;
		
		try {
			instance = clazz.newInstance();
		} catch( Exception e ) {
			instance = null;
		}
	}

	/**
	 * @return the tmpDir
	 */
	public boolean isTmpDir() {
		return tmpDir;
	}

	/**
	 * @param tmpDir the tmpDir to set
	 */
	public void setTmpDir(boolean tmpDir) {
		this.tmpDir = tmpDir;
	}

	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @return the timeFrame
	 */
	public long getTimeFrame() {
		return timeFrame;
	}

	/**
	 * @param timeFrame the timeFrame to set
	 */
	@Override
	public void setTimeFrame(long timeFrame) {
		this.timeFrame = timeFrame;
	}

	/**
	 * @return the baseDir
	 */
	public String getBaseDir() {
		return baseDir;
	}

	/**
	 * @param baseDir the baseDir to set
	 */
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
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

		// Special dumper for model key
		if(clazz.equals(APDVisit.class)) {
			dumpAPDVisit(collection, fromDate, toDate, deleteAfterDump, moveCollectionBeforeDump);
			return;
		}
		
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

			if( cfm != null ) {
				cfm.flush();
				cfm.forceCleanup();
			}
			
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			jdoConn.close();
			pm.currentTransaction().commit();
		}
	}

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#dumpModelKey(String, Date, Date, boolean)
	 */
	@Override
	public void dumpAPDVisit(String collection, Date fromDate, Date toDate, boolean deleteAfterDump, boolean moveCollectionBeforeDump) throws ASException {

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
			DBCursor c;
			if( fromDate != null && toDate != null ) {
				BasicDBObject query = new BasicDBObject("checkinStarted", 
	                      new BasicDBObject("$gte", fromDate).append("$lt", toDate));
				c = db.getCollection(collection).find(query);
			} else {
				c = db.getCollection(collection).find();
			}
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
				
				obj = null;
				count++;

				if( count % batchSize == 0 )
					log.log(Level.INFO, "Processed " + count + " of " + totalRecords + " with " + processed + " results...");
			}

			long finalTime = new Date().getTime();
			log.log(Level.INFO, "Finally Processed " + count + " of " + totalRecords + " with " + processed + " results in " + (finalTime - initTime) + "ms");

			if( cfm != null ) {
				cfm.flush();
				cfm.forceCleanup();
			}
			
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
	 * @see mobi.allshoppings.dump.DumperHelper#applyJsonPlugins(ModelKey)
	 */
	public String applyJsonPlugins(T obj) throws ASException {
		String json = gson.toJson(obj);
		for(DumperPlugin<ModelKey> plugin : registeredPlugins) {
			if(plugin.isAvailableFor(obj)) json = plugin.toJson(obj, json);
		}
		return json;
	}

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#applyJsonPlugins(ModelKey)
	 */
	public String applyJsonPlugins(JSONObject obj) throws ASException {
		String json = obj.toString();
		for(DumperPlugin<ModelKey> plugin : registeredPlugins) {
			if(plugin.isAvailableFor(instance)) json = plugin.toJson(obj, json);
		}
		return json;
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
	public void dump(T obj) throws ASException {
		this.dump(obj, baseDir, clazz.getSimpleName(), obj.getCreationDateTime(), obj);
	}

	public void dump(T obj, String baseDir, String baseName, Date forDate, T element) throws ASException {
		try {
			String fileName = resolveDumpFileName(baseDir, baseName, forDate, element, null);
			File file = new File(fileName);
			String jsonRep = applyJsonPlugins(obj);
			dump( jsonRep, file);
			if( cfm != null ) 
				cfm.registerFileForUpdate(fileName);
		} catch( ASException e ) {
			throw e;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	public void dump(JSONObject obj, String baseDir, String baseName, Date forDate, String filter) throws ASException {
		try {
			String fileName = resolveDumpFileName(baseDir, baseName, forDate, null, filter);
			File file = new File(fileName);
			String jsonRep = applyJsonPlugins(obj);
			dump( jsonRep, file);
			if( cfm != null ) 
				cfm.registerFileForUpdate(fileName);
		} catch( ASException e ) {
			throw e;
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#resolveDumpFileName(Date)
	 */
	@Override
	public String resolveDumpFileName( Date forDate, T element ) {
		return resolveDumpFileName(baseDir, clazz.getSimpleName(), forDate, element, null);
	}
	
	public String resolveDumpFileName(String baseDir, String baseName, Date forDate, T element, String filter) {

		String fileName = null;
		if( fileNameResolver != null ) {
			fileName = fileNameResolver.resolveDumpFileName(baseDir, baseName, forDate, element, filter);
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
	 * @see mobi.allshoppings.dump.DumperHelper#getMultipleNameOptions(Date)
	 */
	@Override
	public List<String> getMultipleNameOptions(Date date) {
		List<String> ret = CollectionFactory.createList();
		
		Date toDate = new Date(date.getTime() + TWENTY_THREE_HOURS);
		Date myDate = new Date(date.getTime());
		while( myDate.before(toDate) || myDate.equals(toDate)) {
			try {
				if( fileNameResolver != null && fileNameResolver.mayHaveMultiple() && !StringUtils.hasText(filter)) {
					List<String> l = fileNameResolver.getMultipleFileOptions(baseDir,
							clazz.getSimpleName(), myDate, cfm);
					for( String e : l ) {
						File f = new File(e);
						String[] parts = f.getName().split("\\.");
						StringBuffer sn = new StringBuffer();
						String n = null;
						for( int i = 0; i < parts.length -1; i++) {
							if( i != 0 ) sn.append(".");
							sn.append(parts[i]);
						}
						n = sn.toString();
						if( !ret.contains(n))
							ret.add(n);
					}
				}
			} catch (ASException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			myDate = new Date(myDate.getTime() + timeFrame);
		}

		return ret;
	}
	
	/**
	 * @see mobi.allshoppings.dump.DumperHelper#getMultipleFileOptions(Date)
	 */
	@Override
	public List<String> getMultipleFileOptions(Date date) {

		List<String> ret = CollectionFactory.createList();
		
		Date toDate = new Date(date.getTime() + TWENTY_THREE_HOURS);
		Date myDate = new Date(date.getTime());
		while( myDate.before(toDate) || myDate.equals(toDate)) {
			try {
				if( fileNameResolver != null && fileNameResolver.mayHaveMultiple() && !StringUtils.hasText(filter)) {
					List<String> l = fileNameResolver.getMultipleFileOptions(baseDir,
							clazz.getSimpleName(), myDate, cfm);
					for( String e : l ) {
						File f = new File(e);
						String n = f.getName();
						if( !ret.contains(n))
							ret.add(n);
					}
				}
			} catch (ASException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			myDate = new Date(myDate.getTime() + timeFrame);
		}

		return ret;
	
	}
	
	
	/**
	 * @see mobi.allshoppings.dump.DumperHelper#iterator(Date, Date)
	 */
	@Override
	public Iterator<JSONObject> jsonIterator(Date fromDate, Date toDate) {
		return new DumpJSONIterator(fromDate, toDate);
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
			try {
				JSONObject element = new JSONObject(iterator.next());
				return element;
			} catch( Exception e ) {
				return new JSONObject();
			}
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

			this.registerPrefetchFiles();
			if( cfm != null ) {
				try {
					cfm.startPrefetch();
				} catch( ASException e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
		
		private void registerPrefetchFiles() {
			if( cfm != null ) {
				Date myDate = new Date(fromDate.getTime());
				while( myDate.before(toDate) || myDate.equals(toDate)) {
					try {
						if( fileNameResolver != null && fileNameResolver.mayHaveMultiple() && !StringUtils.hasText(filter)) {
							List<String> l = fileNameResolver.getMultipleFileOptions(baseDir,
									clazz.getSimpleName(), myDate, cfm);
							for( String e : l ) {
								cfm.registerFileForPrefetch(e);
							}
						} else {
							try {
								cfm.registerFileForPrefetch(resolveDumpFileName(baseDir, clazz.getSimpleName(), myDate, null, filter));
							} catch (ASException e) {
								e.printStackTrace();
							}
						}
					} catch (ASException e) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
					myDate = new Date(myDate.getTime() + timeFrame);
				}
			}
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
					curDate = new Date(fromDate.getTime() - timeFrame);

				if(curDate.equals(toDate) || curDate.after(toDate))
					break;
				
				i = 0;

				boolean ableToGo = true;
				String currentFileName = null;
				if( fileNameResolver != null && fileNameResolver.mayHaveMultiple() && !StringUtils.hasText(filter)) {
					if( currentCachedFileNames == null || currentCachedFileNames.size() == 0 ) {
						curDate = new Date(curDate.getTime() + timeFrame);
						try {
							currentCachedFileNames = fileNameResolver.getMultipleFileOptions(baseDir,
									clazz.getSimpleName(), curDate, cfm);
						} catch( ASException e ) {
							log.log(Level.SEVERE, e.getMessage(), e);
							ableToGo = false;
						}
					}

					if( currentCachedFileNames.size() > 0 ) {
						currentFileName = currentCachedFileNames.get(0);
						currentCachedFileNames.remove(0);
					} else {
						ableToGo = false;
					}
				} else {
					curDate = new Date(curDate.getTime() + timeFrame);
					if( curDate.before(toDate) || curDate.equals(toDate) ) {
						currentFileName = resolveDumpFileName(baseDir, clazz.getSimpleName(), curDate, null, filter);
					} else {
						ableToGo = false;
					}
				}
				
				try {
					if( ableToGo ) {
						if( !currentFileName.equals(lastFileUsed)) {
							if( StringUtils.hasText(lastFileUsed) && cfm != null )
								cfm.registerFileAsDisposable(lastFileUsed);
							lastFileUsed = currentFileName;
						}

						if( cfm == null || cfm.checkLocalCopyIntegrity(currentFileName, true)) {
							File f = new File(currentFileName);
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
					}
				} catch( ASException e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
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

	@Override
	public void registerCloudFileManager(CloudFileManager cloudFileManager) {
		this.cfm = cloudFileManager;
	}

	@Override
	public void unregisterCloudFileManager() {
		this.cfm = null;
	}

	@Override
	public void flush() throws ASException {
		if( cfm != null )
			cfm.flush();
	}

	@Override
	public void dispose() {
		if( cfm != null )
			cfm.dispose();

		if(tmpDir) {
			try {
				FileUtils.deleteDirectory(new File(baseDir));
			} catch( Exception e ) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
		}
		
	}

	@Override
	public void startPrefetch() throws ASException {
		if( cfm != null )
			cfm.startPrefetch();
	}

}
