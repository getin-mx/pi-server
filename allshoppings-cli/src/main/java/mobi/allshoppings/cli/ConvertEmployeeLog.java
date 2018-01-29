package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.datanucleus.util.Base64;
import org.json.JSONArray;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Blob;
import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;
import com.mongodb.DBObject;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.APDMAEmployeeDAO;
import mobi.allshoppings.dao.EmployeeLogDAO;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDMAEmployee;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.EmployeeLog;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tools.CollectionFactory;
import mx.getin.Constants;

public class ConvertEmployeeLog extends AbstractCLI {

	private static final Logger log = Logger.getLogger(ConvertEmployeeLog.class.getName());
	private static final String FROM_DATE_PARAM = "fromDate";
	private static final String TO_DATE_PARAM = "toDate";
	private static final String ENTITY_IDS_PARAM = "entityIds";
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

	public static OptionParser buildOptionParser(OptionParser base) {
		parser = base == null ? new OptionParser() : base;
		parser.accepts(FROM_DATE_PARAM, "The starting date to process").withRequiredArg().ofType(String.class);
		parser.accepts(TO_DATE_PARAM, "The limit date to process").withRequiredArg().ofType(String.class);
		parser.accepts(ENTITY_IDS_PARAM, "The entities whose employees will be " + "processed").withRequiredArg()
				.ofType(String.class);
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static void main(String[] args) throws ASException {
		try {
			EmployeeLogDAO employeeLogDao = (EmployeeLogDAO)getApplicationContext().getBean("employeelog.dao.ref");
			APDMAEmployeeDAO apdmaeDao = (APDMAEmployeeDAO)getApplicationContext().getBean("apdmaemployee.dao.ref");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			try {

				String sFromDate = null;
				String sToDate = null;
				Date fromDate = null;
				Date toDate = null;
				
				if( options.has(FROM_DATE_PARAM)) sFromDate =
						(String)options.valueOf(FROM_DATE_PARAM);
				if( options.has(TO_DATE_PARAM)) sToDate =
						(String)options.valueOf(TO_DATE_PARAM);
				
				fromDate = StringUtils.hasText(sFromDate) ? SDF.parse(sFromDate) :
					SDF.parse(SDF.format(
							new Date(System.currentTimeMillis() -Constants.DAY_IN_MILLIS)));
				
				toDate = StringUtils.hasText(sToDate) ? SDF.parse(sToDate) :
					new Date(fromDate.getTime() +Constants.DAY_IN_MILLIS);
				
				List<String> entities = null;
				
				boolean requiresEntities = !options.has(ENTITY_IDS_PARAM); 
				
				if(!requiresEntities) {
					String tmp[] = options.valueOf(ENTITY_IDS_PARAM)
							.toString().split(",");
					entities = CollectionFactory.createList();
					for( String s : tmp ) {
						if(!entities.contains(s.trim())) entities.add(s.trim());
					}
				}
				
				List<APDMAEmployee> list = apdmaeDao.getAll(true);
				Map<String, APDMAEmployee> cache = CollectionFactory.createMap();
				for( APDMAEmployee obj : list ) cache.put(obj.getMac(), obj);
				
				// Prepares local variables
				long count = 0;
				long processed = 0;
				long batchSize = 1000;
				Date curDate = new Date(fromDate.getTime());
				
				log.log(Level.INFO, "Converting Employees for dates: " +fromDate +" - "
						+toDate);

				long initTime = System.currentTimeMillis();
				
				try {
					long total = 0;
					while(curDate.compareTo(toDate) < 0) {
						if(requiresEntities) {
							DumperHelper<APDVisit> visitDumper =
									new DumpFactory<APDVisit>().build(null, APDVisit.class, false);
							entities = visitDumper.getMultipleNameOptions(curDate);
						}
						Date nextDate = new Date(curDate.getTime() +Constants.DAY_IN_MILLIS -1);
						for(String entityId : entities) {
							DumperHelper<APDVisit> visitDumper =
									new DumpFactory<APDVisit>().build(null, APDVisit.class, false);
							visitDumper.setFilter(entityId);
							Iterator<APDVisit> visits = visitDumper.iterator(curDate,
									nextDate, false);
							while(visits.hasNext()) {
								APDVisit apdv = visits.next();
								total++;
								if(apdv.getCheckinType() != APDVisit.CHECKIN_EMPLOYEE)
									continue;
								count++;
								EmployeeLog obj = new EmployeeLog();
								obj.setApheSource(apdv.getApheSource());
								obj.setCheckinFinished(apdv.getCheckinFinished());
								obj.setCheckinStarted(apdv.getCheckinStarted());
								obj.setDeviceUUID(apdv.getDeviceUUID());
								obj.setEntityId(entityId);
								obj.setEntityKind(apdv.getEntityKind());
								obj.setHidePermanence(apdv.getHidePermanence());
								obj.setMac(apdv.getMac());
								obj.setUserId(apdv.getUserId());
								obj.setKey(employeeLogDao.createKey(obj));
								APDMAEmployee apdmae = cache.get(obj.getMac());
								if( apdmae != null ) {
									obj.setEmployeeId(apdmae.getIdentifier());
									obj.setEmployeeName(apdmae.getDescription());
									employeeLogDao.create(obj);

									processed ++;
									obj = null;
								}

								if( count % batchSize == 0 )
									log.log(Level.INFO, "Processed " + count + " of "
								+ total + " with " + processed + " results...");

							}//while there are more visits
							visitDumper.dispose();
						}//for every given entity
						curDate.setTime(nextDate.getTime() +1);
					}//for every given date
					
					long finalTime = System.currentTimeMillis();
					log.log(Level.INFO, "Finally Processed " + count + " of " + total + " with " + processed + " results in " + (finalTime - initTime) + "ms");
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
					throw ASExceptionHelper.defaultException(e.getMessage(), e);
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
	 * Sets properties of an entity object based in the attributes received in JSON
	 * representation
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
		((ModelKey) obj).setKey(objKey);

		for (Iterator<String> it = dbo.keySet().iterator(); it.hasNext();) {
			try {
				String key = it.next();
				if (!key.equals("_id")) {
					Object fieldValue = dbo.get(key);
					if (fieldValue instanceof DBObject) {
						Object data = PropertyUtils.getProperty(obj, key);
						setPropertiesFromDBObject((DBObject) fieldValue, data);
					} else {
						if (PropertyUtils.getPropertyType(obj, key) == Text.class) {
							Text text = new Text(fieldValue.toString());
							PropertyUtils.setProperty(obj, key, text);
						} else if (PropertyUtils.getPropertyType(obj, key) == Email.class) {
							Email mail = new Email(((String) safeString(fieldValue)).toLowerCase());
							PropertyUtils.setProperty(obj, key, mail);
						} else if (PropertyUtils.getPropertyType(obj, key) == Date.class) {
							PropertyUtils.setProperty(obj, key, fieldValue);
						} else if (PropertyUtils.getPropertyType(obj, key) == Key.class) {
							String[] parts = ((String) fieldValue).split("\"");
							Class<?> c = Class.forName("mobi.allshoppings.model." + parts[0].split("\\(")[0]);
							Key data = new KeyHelperGaeImpl().obtainKey(c, parts[1]);
							PropertyUtils.setProperty(obj, key, data);
						} else if (PropertyUtils.getPropertyType(obj, key) == Blob.class) {
							Blob data = new Blob(Base64.decode((String) fieldValue));
							PropertyUtils.setProperty(obj, key, data);
						} else if (fieldValue instanceof JSONArray) {
							JSONArray array = (JSONArray) fieldValue;
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
			if (from instanceof String) {
				return new String(((String) from).getBytes());
			} else {
				return from;
			}
		} catch (Exception e) {
			log.log(Level.INFO, e.getMessage(), e);
			return from;
		}
	}

}
