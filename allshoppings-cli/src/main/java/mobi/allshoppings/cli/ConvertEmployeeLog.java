package mobi.allshoppings.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.datastore.JDOConnection;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.datanucleus.util.Base64;
import org.json.JSONArray;
import org.springframework.context.ApplicationContext;

import com.inodes.datanucleus.model.Blob;
import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.APDMAEmployeeDAO;
import mobi.allshoppings.dao.EmployeeLogDAO;
import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDMAEmployee;
import mobi.allshoppings.model.EmployeeLog;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tools.CollectionFactory;


public class ConvertEmployeeLog extends AbstractCLI {

	private static final Logger log = Logger.getLogger(ConvertEmployeeLog.class.getName());
	public static final long TWENTY_FOUR_HOURS = 86400000;

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
			EmployeeLogDAO employeeLogDao = (EmployeeLogDAO)getApplicationContext().getBean("employeelog.dao.ref");
			APDMAEmployeeDAO apdmaeDao = (APDMAEmployeeDAO)getApplicationContext().getBean("apdmaemployee.dao.ref");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			@SuppressWarnings("unused")
			OptionSet options = parser.parse(args);

			try {

				List<APDMAEmployee> list = apdmaeDao.getAll(true);
				Map<String, APDMAEmployee> cache = CollectionFactory.createMap();
				for( APDMAEmployee obj : list ) {
					cache.put(obj.getMac(), obj);
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

				try {
					// Prepares the cursor
					BasicDBObject query = new BasicDBObject();
					query.put("checkinType", 4 );
					DBCursor c = db.getCollection("APDVisit").find(query);
					c.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
					Iterator<DBObject> i = c.iterator();

					// Counts the total records to export
					long totalRecords = c.count();
					log.log(Level.INFO, totalRecords + " records found");

					while(i.hasNext()) {
						// Gets the DB Object
						DBObject dbo = i.next();

						EmployeeLog obj = new EmployeeLog();
						setPropertiesFromDBObject(dbo, obj);
						obj.setKey(employeeLogDao.createKey(obj));
						APDMAEmployee apdmae = cache.get(obj.getMac());
						if( apdmae != null ) {
							obj.setEmployeeId(apdmae.getIdentifier());
							obj.setEmployeeName(apdmae.getDescription());
							employeeLogDao.create(obj);

							processed ++;
							obj = null;
							count++;
						}

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

			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Converting EmployeeLog Registers");

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
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
	public static void setPropertiesFromDBObject(DBObject dbo, Object obj) {
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

	public static Object safeString(Object from) {
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

}
