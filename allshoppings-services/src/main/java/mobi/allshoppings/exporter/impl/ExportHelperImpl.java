package mobi.allshoppings.exporter.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.dao.ExportUnitDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExportHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.ExportUnit;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;

public class ExportHelperImpl implements ExportHelper {

	private static final Logger log = Logger.getLogger(ExportHelperImpl.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private ExportUnitDAO dao;
	@Autowired
	private StoreDAO storeDao;

	/* (non-Javadoc)
	 * @see mobi.allshoppings.exporter.impl.ExportHelper#export(java.util.Date, java.util.Date)
	 */
	@Override
	public void export(Date fromDate, Date toDate, String outfile) throws ASException {
		
		List<ExportUnit> list = dao.getUsingStatusAndRange(StatusHelper.statusActive(),
				null, null);
		for( ExportUnit unit : list ) {
			if(unit.getTargetUser().equals("test_mx")) continue;
			try {
				export(unit, fromDate, toDate, outfile);
			} catch( ASException e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see mobi.allshoppings.exporter.impl.ExportHelper#export(mobi.allshoppings.model.ExportUnit, java.util.Date, java.util.Date)
	 */
	@Override
	public void export(ExportUnit unit, Date fromDate, Date toDate, String outfile) throws ASException {
		switch (unit.getTargetType()) {
		case ExportUnit.TARGET_MYSQL:
			exportUsingMySQL(unit, fromDate, toDate, outfile);
			break;
		default:
			break;
		}
	}

	/**
	 * Exports an ExportUnit using a MySQL Target
	 * 
	 * @param unit
	 *            The ExportUnit to use
	 * @throws ASException
	 */
	private void exportUsingMySQL(ExportUnit unit, Date fromDate, Date toDate, String outfile) throws ASException {
		switch (unit.getSourceType()) {
		case ExportUnit.SOURCE_VISITS:
			exportVisitsUsingMySQL(unit, fromDate, toDate, outfile);
			break;
		default:
			break;
		}
	}

	/**
	 * Exports APDVisits using a MySQL Target
	 * 
	 * @param unit
	 *            The ExportUnit to use
	 * @throws ASException
	 */
	private void exportVisitsUsingMySQL(ExportUnit unit, Date fromDate, Date toDate, String outfile) throws ASException {
		
		List<String> entityIds = resolveEntityIds(unit.getEntityIds(), unit.getEntityKind());
		Connection conn = getMySQLConnection(unit);
		Statement stmt = null;
		ResultSet rs = null;

		try {
			File f = null;
			FileOutputStream fos = null;
			if( StringUtils.hasText(outfile)) {
				f = new File(outfile);
				fos = new FileOutputStream(f);
			}
			
			MessageDigest md = MessageDigest.getInstance("MD5");

			// Gets the last update made to the unit
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT MAX(last_update) from apd_visit");
			Date lastUpdate = null;
			if( rs.first() && !rs.isAfterLast())
				try {
					lastUpdate = rs.getTimestamp(1);
				} catch( Exception e ) {}

			DumperHelper<APDVisit> dumper = null;
			for(String entityId : entityIds) {
				
				try {
			/*// Now gets the cursor to the mongoDB database
			PersistenceManager pm;
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
			pm.currentTransaction().begin();

			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();
			BasicDBObject fields = new BasicDBObject();
			fields.put("_id", 1);
			fields.put("entityId", 1);
			fields.put("entityKind", 1);
			fields.put("mac", 1);
			fields.put("devicePlatform", 1);
			fields.put("checkinType", 1);
			fields.put("checkinStarted", 1);
			fields.put("checkinFinished", 1);
			fields.put("creationDateTime", 1);
			fields.put("lastUpdate", 1);

			BasicDBObject query = new BasicDBObject();
			query.put("entityId", new BasicDBObject("$in", entityIds.toArray(new String[entityIds.size()])));
			if( lastUpdate != null )
				query.put("lastUpdate", new BasicDBObject("$gt", lastUpdate));

			BasicDBObject sort = new BasicDBObject();
			sort.put("lastUpdate", 1);
			
			DBCursor c1 = db.getCollection("APDVisit").find(query,fields).sort(sort);
			c1.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			Iterator<DBObject> i = c1.iterator();*/
					
					Calendar cal = Calendar.getInstance();
					if(fromDate == null) {
						cal.set(2017, 0, 1, 0, 0);
						fromDate = cal.getTime();
					}
					if(toDate == null) {
						cal.add(Calendar.DATE, 1);
						toDate = cal.getTime();
					}
					dumper = new DumpFactory<APDVisit>().build(null,
							APDVisit.class, false);
					dumper.setFilter(entityId);
					Iterator<APDVisit> i = dumper.iterator(lastUpdate != null ? lastUpdate :
								fromDate, toDate, false);

					log.log(Level.INFO, "Processing results...");
					long cnt = 0;
					
					boolean transOpened = false;
					
					// Now iterates over all the found objects
					while(i.hasNext()) {
						APDVisit dbo = i.next();
						if(StringUtils.hasText(dbo.getEntityId()) &&
								StringUtils.hasText(dbo.getMac()) &&
								dbo.getCheckinType() != null &&
								dbo.getCheckinStarted() != null &&
								dbo.getCheckinFinished() != null) {

							cnt++;
							if( cnt % 1000 == 0 ) {
								log.log(Level.INFO, "Processing record " + cnt + "...");
							}
							
							String realMac = dbo.getMac();
							//String identifier = Key.fromString((String)dbo.get("_id")).getName();
							String identifier = dbo.getKey().toString();
							String mac = new String(realMac);

							if(unit.getHideMac()) {
								byte[] bytes = md.digest(realMac.getBytes());
								StringBuilder sb = new StringBuilder();
							    for (byte b : bytes) {
							        sb.append(String.format("%02X", b));
							    }
								mac = sb.toString().toLowerCase();
								identifier = mac + identifier.substring(17);
							}
							
							Integer entityKind = dbo.getEntityKind();
							String devicePlatform = dbo.getDevicePlatform();
							Integer checkinType = dbo.getCheckinType();
							Date checkinStarted = dbo.getCheckinStarted();
							Date checkinFinished = dbo.getCheckinFinished();
							Date creationDateTime = dbo.getCreationDateTime();
							Date objLastUpdate = dbo.getLastUpdate();
							
							// Creates the statement and executes the insert
							StringBuffer sb = new StringBuffer();
							sb.append(
									"REPLACE INTO `apd_visit` ("
									+ "`visit_id`, `entity_id`, `entity_kind`, "
									+ "`mac`, `device_platform`, `checkin_type`, "
									+ "`checkin_started`, `checkin_finished`, "
									+ "`creation_datetime`, `last_update`"
									+ ") VALUES (");

							sb.append("'").append(identifier).append("', ");
							sb.append("'").append(entityId).append("', ");
							sb.append("'").append(entityKind).append("', ");
							sb.append("'").append(mac).append("', ");
							sb.append("'").append(devicePlatform).append("', ");
							sb.append("'").append(checkinType).append("', ");
							sb.append("'").append(sdf.format(checkinStarted)).append("', ");
							sb.append("'").append(sdf.format(checkinFinished)).append("', ");
							sb.append("'").append(sdf.format(creationDateTime)).append("', ");
							sb.append("'").append(sdf.format(objLastUpdate)).append("')");

							if( fos != null ) {
								sb.append(";\n");
								fos.write(sb.toString().getBytes());
							} else {
								try {
									if( !transOpened ) {
										conn.setAutoCommit(false);
										stmt = conn.createStatement();
										transOpened = true;
									}
									stmt.addBatch(sb.toString());
									if( cnt % 100 == 0 ) {
										stmt.executeBatch();
										conn.commit();
										stmt.close();
										transOpened = false;
									}
								} catch( Exception e ) {
									log.log(Level.SEVERE, e.getMessage(), e);
									log.log(Level.INFO, sb.toString());
								}
							}

						}
					}

					if( fos != null ) {
						fos.close();
					}
					
				} catch( Exception e ) {
					throw ASExceptionHelper.defaultException(e.getMessage(), e);

				} finally {
					// Close and clean up the connection
					if(dumper != null) dumper.dispose();
				}
			}	
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			try {
				conn.close();
			} catch( Exception e1 ) {}
		}

		
		
	}

	/**
	 * Gets a MySQL Connection for an export unit
	 * 
	 * @param unit
	 *            The export Unit to get the connection for
	 * @return The actual MySQL Connection
	 * @throws ASException
	 */
	private Connection getMySQLConnection(ExportUnit unit) throws ASException {
		try {
			// Prepares SQL Connection
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			try {
				String url = unit.getTargetURL();
				boolean first = url.contains("?") ? false : true;
				boolean containsUser = url.contains("user=") ? true : false;
				boolean containsPasword = url.contains("password=") ? true : false;

				if( !containsUser ) {
					if( first )
						url = url + "?";
					else
						url = url + "&";
					first = false;
					url = url + "user=" + unit.getTargetUser();
				}
				
				if( !containsPasword ) {
					if( first )
						url = url + "?";
					else
						url = url + "&";
					first = false;
					url = url + "password=" + unit.getTargetPassword();
				}
				
				Connection conn = DriverManager.getConnection(url);
				log.log(Level.INFO, "MySQL Connection obtained for export unit " + unit.getName());
				return conn;
			} catch (SQLException ex) {
				// handle any errors
				log.log(Level.SEVERE, "SQLException: " + ex.getMessage());
				log.log(Level.SEVERE, "SQLState: " + ex.getSQLState());
				log.log(Level.SEVERE, "VendorError: " + ex.getErrorCode());
				log.log(Level.SEVERE, "", ex);
				throw ex;
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
	/**
	 * Resolve an EntityRepresentation List in a Store ID list
	 * 
	 * @param src
	 *            An EntityRepresentation List
	 * @return The fully formed store ID list
	 * @throws ASException
	 */
	private List<String> resolveEntityIds(List<String> src, Integer entityKind) throws ASException {
		List<String> ret = CollectionFactory.createList();
		
		for( String rep : src ) {
			if( EntityKind.KIND_STORE == entityKind) {
				if( !ret.contains(rep))
					ret.add(rep);
			} else if( EntityKind.KIND_BRAND == entityKind) {
				List<Store> stores = storeDao.getUsingBrandAndStatus(rep, StatusHelper.statusActive(), null);
				for( Store store : stores ) {
					if( !ret.contains(store.getIdentifier()))
						ret.add(store.getIdentifier());
				}
			}
		}
		
		return ret;
	}
	
}
