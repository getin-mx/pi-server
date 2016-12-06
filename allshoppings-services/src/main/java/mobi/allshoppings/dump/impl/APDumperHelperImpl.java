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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.datanucleus.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.inodes.datanucleus.model.Blob;
import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;
import com.mongodb.DBObject;

import mobi.allshoppings.dump.APDumperHelper;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tools.CollectionFactory;

public class APDumperHelperImpl implements APDumperHelper {

	private static final SimpleDateFormat year = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat month = new SimpleDateFormat("MM");
	private static final SimpleDateFormat day = new SimpleDateFormat("dd");
	private static final SimpleDateFormat hour = new SimpleDateFormat("HH");
	private static final Logger log = Logger.getLogger(APDumperHelperImpl.class.getName());
	private static final Gson gson = new Gson();
	private static final int BUFFER = 100;

	private String baseDir;
	
	public APDumperHelperImpl(String baseDir) {
		this.baseDir = baseDir;
	}

	@Override
	public void splitFromGeneralDump(String baseDir, Date fromDate, Date toDate) {
		DumperHelper<APHotspot> dumper = new DumperHelperImpl<APHotspot>(baseDir, APHotspot.class);
		Iterator<APHotspot> it = dumper.iterator(fromDate, toDate);
		long counter = 0;
		while(it.hasNext()) {
			try {
				APHotspot obj = it.next();
				counter ++;
				if( counter % 1000 == 0 ) {
					log.log(Level.INFO, "Dumping record " + counter + " for date " + obj.getCreationDateTime());
				}
				dump(obj);
			} catch(Exception e ) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}
	
	/**
	 * @see mobi.allshoppings.dump.DumperHelper#dump(ModelKey)
	 */
	@Override
	public void dump(APHotspot obj) throws IOException {
		APDumperHelperImpl.dump(gson.toJson(obj), baseDir, obj.getHostname(), obj.getCreationDateTime());
	}

	public static void dump(String jsonRep, String baseDir, String baseName, Date forDate) throws IOException {
		String fileName = resolveDumpFileName(baseDir, baseName, forDate);
		File file = new File(fileName);
		dump( jsonRep, file);
	}

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#resolveDumpFileName(Date)
	 */
	@Override
	public String resolveDumpFileName( String hostname, Date forDate ) {
		return resolveDumpFileName(baseDir, hostname, forDate);
	}
	
	public static String resolveDumpFileName(String baseDir, String baseName, Date forDate) {
		String myYear = year.format(forDate);
		String myMonth = month.format(forDate);
		String myDay = day.format(forDate);
		String myHour = hour.format(forDate);

		StringBuffer sb = new StringBuffer();
		sb.append(baseDir);
		if(!baseDir.endsWith(File.separator)) sb.append(File.separator);
		sb.append("ap").append(File.separator);
		sb.append(myYear).append(File.separator);
		sb.append(myMonth).append(File.separator);
		sb.append(myDay).append(File.separator);
		sb.append(myHour).append(File.separator);
		sb.append(baseName).append(".json");

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

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#iterator(Date, Date)
	 */
	@Override
	public Iterator<APHotspot> iterator(String hostname, Date fromDate, Date toDate) {
		return new DumpEnhancedIterator(hostname, fromDate, toDate);
	}


	/**
	 * @see mobi.allshoppings.dump.DumperHelper#iterator(Date, Date)
	 */
	@Override
	public Iterator<String> stringIterator(String hostname, Date fromDate, Date toDate) {
		return new DumpStringIterator(hostname, fromDate, toDate);
	}

	/**
	 * @see mobi.allshoppings.dump.DumperHelper#iterator(Date, Date)
	 */
	@Override
	public Iterator<JSONObject> jsonIterator(String hostname, Date fromDate, Date toDate) {
		return new DumpJSONIterator(hostname, fromDate, toDate);
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

	public class DumpEnhancedIterator implements Iterator<APHotspot> {
		
		private Iterator<String> iterator;
		
		public DumpEnhancedIterator(String hostname, Date fromDate, Date toDate) {
			iterator = new DumpStringIterator(hostname, fromDate, toDate);
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public APHotspot next() {
			APHotspot element = gson.fromJson(iterator.next(), APHotspot.class);
			return element;
		}

		@Override
		public void remove() {
			// Not Implemented
		}
		
	}

	public class DumpJSONIterator implements Iterator<JSONObject> {
		
		private Iterator<String> iterator;
		
		public DumpJSONIterator(String hostname, Date fromDate, Date toDate) {
			iterator = new DumpStringIterator(hostname, fromDate, toDate);
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
		private String hostname;
		private BufferedReader br;
		
		public DumpStringIterator(String hostname, Date fromDate, Date toDate) {
			elements = CollectionFactory.createList();
			counter = 0;
			this.fromDate = fromDate;
			this.toDate = toDate;
			this.hostname = hostname;
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

				i = 0;
				File f = new File(resolveDumpFileName(baseDir, hostname, curDate));
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
