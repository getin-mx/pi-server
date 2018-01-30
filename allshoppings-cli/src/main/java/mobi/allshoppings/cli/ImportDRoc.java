package mobi.allshoppings.cli;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;

import com.inodes.datanucleus.model.Key;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.ExternalAPHotspot;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mx.getin.Constants;


public class ImportDRoc extends AbstractCLI {

	private static final Logger log = Logger.getLogger(ImportDRoc.class.getName());
	private static final byte EVENT_JOIN = 0;
	private static final byte EVENT_LEAVE = 1;
	private static final byte EVENT_INVALID = 2;
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Date from" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date to" ).withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String[] args) throws ASException {
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//sdf.setTimeZone(TimeZone.getTimeZone(Constants.ME));
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String fromDate = null;
			String toDate = null;
			Date fd = null;
			Date td = null;
			
			try {
				if( options.has("help")) usage(parser);
				if(options.has("fromDate")) {
					fromDate = options.valueOf("fromDate").toString();
					fd = sdf.parse(fromDate);
				} else {
					fd = new Date(System.currentTimeMillis() -Constants.DAY_IN_MILLIS);
					fromDate = sdf.format(fd);
				} if(options.has("toDate")) {
					toDate = options.valueOf("toDate").toString();
					td = sdf.parse(toDate);
				} else {
					td = new Date(fd.getTime() +Constants.DAY_IN_MILLIS);
					toDate = sdf.format(td);
				}
			} catch( Exception e ) {
				log.log(Level.FINE, e.getMessage(), e);
				usage(parser);
			}

			log.log(Level.INFO, "Starting to import droc information");

			ResultSet rs = null;
			Statement stmt = null;
			SystemConfiguration config = (SystemConfiguration)getApplicationContext().getBean("system.configuration");
			String connectionString = "jdbc:mysql://" +config.getDrocExternalSource() +"/" +config.getDrocDatabaseName()
					+"?user=" +config.getDrocUser() +"&password=" +config.getDrocPassword();
			Map<String, Date> lastUpdates = new HashMap<>();
			Map<String,ExternalAPHotspot> connections = new HashMap<>();
			List<ExternalAPHotspot> list = new LinkedList<>();
			KeyHelper keyHelper = new KeyHelperGaeImpl();
			long start, end;
			Date curDate = new Date(fd.getTime());
			try(Connection conn = DriverManager.getConnection(connectionString)) {
				// Prepares SQL Connection
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				log.log(Level.INFO, "SQL Connection obtained");
				while(curDate.compareTo(td) < 0) {
					log.log(Level.INFO, "Processing for date: {0}", sdf.format(curDate));
					start = System.currentTimeMillis();
					StringBuilder sb  = new StringBuilder();
					sb.append("SELECT SysLogTag, ReceivedAt, Message FROM SystemEvents WHERE ReceivedAt > '");
					sb.append(sdf.format(curDate)).append("' AND ReceivedAt < '");
					sb.append(sdf.format(curDate.getTime() +Constants.DAY_IN_MILLIS));
					sb.append("' AND SysLogTag LIKE '(\"%,%,%\")' AND (Message LIKE '%EVENT_STA_LEAVE%' ");
					sb.append("OR Message LIKE '%EVENT_STA_JOIN%')");
					String query = sb.toString();
					log.log(Level.INFO, "Executing query: {0}", query);
					stmt = conn.createStatement();
					rs = stmt.executeQuery(query);
					end = System.currentTimeMillis();
					log.log(Level.INFO, "Query executed in {0}ms", end - start);
					int cnt = 0;
					if( rs.first()) {
						while(!rs.isAfterLast()) {
							String hostname = rs.getString(1);
							Date receivedAt = rs.getTimestamp(2);
							String message = rs.getString(3);
							int event = 0;
							String ifc = null;
							String mac = null;
							int channel = 0;
							
							String[] hostParts = hostname.split(",");
							hostname = "droc-" + hostParts[1];
							
							if(message.contains("EVENT_STA_JOIN")) {
								event = EVENT_JOIN;
								String[] parts = message.split("EVENT_STA_JOIN");
								if( parts.length > 1 ) {
									String[] parts1 = parts[1].trim().split(" ");
									if( parts1.length > 3) {
										ifc = parts1[0].trim().substring(0, parts1[0].length() -1);
										mac = parts1[1].trim();
										channel = Integer.valueOf(parts1[3].trim());
									
										String key = hostname + ":" + ifc + ":" + channel;
	
										ExternalAPHotspot obj = new ExternalAPHotspot();
										obj.setCount(1);
										obj.setFirstSeen(receivedAt);
										obj.setCreationDateTime(receivedAt);
										obj.setHostname(hostname);
										obj.setMac(mac);
										obj.setSignalDB(-20);
										obj.setKey((Key)keyHelper.createStringUniqueKey(ExternalAPHotspot.class));
										
										list.add(obj);
										
										// controls last update
										Date last = lastUpdates.get(hostname);
										if( last == null || last.before(receivedAt)) {
											last = receivedAt;
										}
										lastUpdates.put(hostname, last);
										
										connections.put(key, obj);
	
									} else {
										event = EVENT_INVALID;
									}
								} else {
									event = EVENT_INVALID;
								}
								
							} else {
								event = EVENT_LEAVE;
								String[] parts = message.split("EVENT_STA_LEAVE");
								if( parts.length > 1 ) {
									String parts1[] = parts[1].trim().split(" ");
									if( parts1.length > 1 ) {
										ifc = parts1[0].trim().substring(0, parts1[0].length() -1);
										channel = Integer.valueOf(parts1[1].trim());
	
										mac = null;
										String key = hostname + ":" + ifc + ":" + channel;
										ExternalAPHotspot obj = connections.get(key);
										if( obj != null ) {
											obj.setLastSeen(receivedAt);
											mac = obj.getMac();
										}
										connections.remove(key);
										
									} else {
										event = EVENT_INVALID;
									}
								} else {
									event = EVENT_INVALID;
								}							
							}
	
							log.log(Level.FINE, hostname + "\t" + receivedAt + "\t" + event + "\t" + ifc + "\t" + channel + "\t" + mac);
							if( cnt % 1000 == 0 ) {
								log.log(Level.INFO, "Processing record " + cnt);
							}
							
							rs.next();
							cnt++;
						}
					}
				
					log.log(Level.INFO, list.size() + " elements to write");
					DumperHelper<ExternalAPHotspot> dumper = new DumpFactory<ExternalAPHotspot>().build(null,
							ExternalAPHotspot.class, false);
					start = System.currentTimeMillis();
					for( ExternalAPHotspot ele : list ) {
						dumper.dump(ele);
					}
					dumper.dispose();
		
					end = System.currentTimeMillis();
					log.log(Level.INFO, list.size() + " elements written in " + (end - start) + "ms");
					curDate.setTime(curDate.getTime() +Constants.DAY_IN_MILLIS);
					stmt.close();
					rs.close();
				}
			} catch (SQLException ex) {
				// handle any errors
				log.log(Level.SEVERE, "SQLException: " + ex.getMessage());
				log.log(Level.SEVERE, "SQLState: " + ex.getSQLState());
				log.log(Level.SEVERE, "VendorError: " + ex.getErrorCode());
				log.log(Level.SEVERE, "", ex);
				throw ex;
			} catch( Exception e ) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} finally {
				stmt.close();
				rs.close();
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
