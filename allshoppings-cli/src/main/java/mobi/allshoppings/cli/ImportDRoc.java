package mobi.allshoppings.cli;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.ExternalAPHotspotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.ExternalAPHotspot;
import mobi.allshoppings.tools.CollectionFactory;


public class ImportDRoc extends AbstractCLI {

	private static final Logger log = Logger.getLogger(ImportDRoc.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Date from" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date to" ).withRequiredArg().ofType( String.class );
		parser.accepts( "fromLast", "Update from last request" ).withRequiredArg().ofType( Boolean.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ExternalAPHotspotDAO eaphDao = (ExternalAPHotspotDAO)getApplicationContext().getBean("externalaphotspot.dao.ref");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String fromDate = null;
			String toDate = null;
			Boolean fromLast = false;
			
			try {
				
				if(options.has("fromDate"))
					fromDate = (String)options.valueOf("fromDate");
				
				if(options.has("toDate"))
					toDate = (String)options.valueOf("toDate");
				
				if(options.has("fromLast"))
					fromLast = (Boolean)options.valueOf("fromLast");
				
				if( options.has("help")) usage(parser);				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Starting to import droc information");

			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			Date lastDate = null;
			
			int EVENT_JOIN = 0;
			int EVENT_LEAVE = 1;
			int EVENT_INVALID = 2;
			
			String connectionString = "jdbc:mysql://173.224.112.51:3306/Syslog?user=getin&password=getindroc";
			String query1 = "select distinct SysLogTag from SystemEvents where SysLogTag like '(\"U7LT,%' or SysLogTag like '(\"U2HSR,%'";
			String queryca = "select Count(*) from SystemEvents where ";
			String queryca1 = " SysLogTag in (";
			String querycb = ") and( Message like '%EVENT_STA_LEAVE%' or Message like '%EVENT_STA_JOIN%')";
			String queryc = "";
			String query2a = "select * from SystemEvents where ";
			String query2a1 = " SysLogTag in (";
			String query2b = ") and( Message like '%EVENT_STA_LEAVE%' or Message like '%EVENT_STA_JOIN%') limit 0,2000000";
			String query2 = "";
			List<String> hostnames = CollectionFactory.createList();
			Map<String,ExternalAPHotspot> connections = CollectionFactory.createMap();
			List<ExternalAPHotspot> list = CollectionFactory.createList(); 
			
			try {
				// Prepares SQL Connection
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				try {
					conn = DriverManager.getConnection(connectionString);
					log.log(Level.INFO, "SQL Connection obtained");
				} catch (SQLException ex) {
					// handle any errors
					log.log(Level.SEVERE, "SQLException: " + ex.getMessage());
					log.log(Level.SEVERE, "SQLState: " + ex.getSQLState());
					log.log(Level.SEVERE, "VendorError: " + ex.getErrorCode());
					log.log(Level.SEVERE, "", ex);
					throw ex;
				}

				long start = System.currentTimeMillis();
				log.log(Level.INFO, "Executing query: " + query1);
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query1);
				long end = System.currentTimeMillis();
				log.log(Level.INFO, "Query executed in " + (end - start) + "ms");
				
				if( rs.first()) {
					while(!rs.isAfterLast()) {
						String hostname = rs.getString(1);
						hostnames.add(hostname);
						rs.next();
					}
				}

				if( fromLast ) {
					List<String> data = CollectionFactory.createList();
					for(String hostname : hostnames ) {
						String hostParts[] = hostname.split(",");
						hostname = "droc-" + hostParts[1];
						data.add(hostname);
					}
					lastDate = eaphDao.getLastEntryDate(data);
				}
				
				{
					// Build Query Count
					StringBuffer sb  = new StringBuffer();
					sb.append(queryca);

					if( fromLast ) {
						sb.append("ReceivedAt > timestamp('");
						sb.append(sdf.format(lastDate));
						sb.append("') and ");
					} else {
						if( fromDate != null ) {
							sb.append("ReceivedAt > timestamp('");
							sb.append(fromDate);
							sb.append(" 00:00:00') and ");
						}
						
						if( toDate != null ) {
							sb.append("ReceivedAt < timestamp('");
							sb.append(toDate);
							sb.append(" 00:00:00') and ");
						}
					}
					
					sb.append(queryca1);
					boolean first = true;
					for( String hostname : hostnames ) {
						if(!first) sb.append(",");
						first = false;
						sb.append("'").append(hostname).append("'");
					}
					sb.append(querycb);
					queryc = sb.toString();
				}

				start = System.currentTimeMillis();
				log.log(Level.INFO, "Executing query: " + queryc);
				stmt = conn.createStatement();
				rs = stmt.executeQuery(queryc);
				end = System.currentTimeMillis();
				log.log(Level.INFO, "Query executed in " + (end - start) + "ms");
				
				long recordsCount = 0;
				if( rs.first()) {
					recordsCount = rs.getLong(1);
					log.log(Level.INFO, recordsCount + " total records");
					while(!rs.isAfterLast()) {
						String hostname = rs.getString(1);
						hostnames.add(hostname);
						rs.next();
					}
				}

				{
					// Build Query 2
					StringBuffer sb  = new StringBuffer();
					sb.append(query2a);

					if( fromLast ) {
						sb.append("ReceivedAt > timestamp('");
						sb.append(sdf.format(lastDate));
						sb.append("') and ");
					} else {
						if( fromDate != null ) {
							sb.append("ReceivedAt > timestamp('");
							sb.append(fromDate);
							sb.append(" 00:00:00') and ");
						}
						
						if( toDate != null ) {
							sb.append("ReceivedAt < timestamp('");
							sb.append(toDate);
							sb.append(" 00:00:00') and ");
						}
					}
					
					sb.append(query2a1);
					boolean first = true;
					for( String hostname : hostnames ) {
						if(!first) sb.append(",");
						first = false;
						sb.append("'").append(hostname).append("'");
					}
					sb.append(query2b);
					query2 = sb.toString();
				}
				
				log.log(Level.INFO, hostnames.size() + " hostnames found");
				log.log(Level.INFO, hostnames.toString());
				
				start = System.currentTimeMillis();
				log.log(Level.INFO, "Executing query: " + query2);
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query2);
				end = System.currentTimeMillis();
				log.log(Level.INFO, "Query executed in " + (end - start) + "ms");
				
				int cnt = 0;
				
				if( rs.first()) {
					while(!rs.isAfterLast()) {
						String hostname = rs.getString("SysLogTag");
						Date receivedAt = rs.getTimestamp("ReceivedAt");
						String message = rs.getString("Message");
						int event = 0;
						String ifc = null;
						String mac = null;
						int channel = 0;
						
						String hostParts[] = hostname.split(",");
						hostname = "droc-" + hostParts[1];
						
						if(message.contains("EVENT_STA_JOIN")) {
							event = EVENT_JOIN;
							String[] parts = message.split("EVENT_STA_JOIN");
							if( parts.length > 1 ) {
								String parts1[] = parts[1].trim().split(" ");
								if( parts1.length > 3) {
									ifc = parts1[0].trim().substring(0, parts1[0].length() -1);
									mac = parts1[1].trim();
									channel = Integer.valueOf(parts1[3].trim());
								
									String key = hostname + ":" + ifc + ":" + channel;

									ExternalAPHotspot obj = new ExternalAPHotspot();
									obj.setCount(1);
									obj.setFirstSeen(receivedAt);
									obj.setHostname(hostname);
									obj.setMac(mac);
									obj.setSignalDB(-20);
									obj.setKey(eaphDao.createKey());
									
									list.add(obj);
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
						if( cnt % 10 == 0 ) {
							log.log(Level.INFO, "Processing record " + cnt + " of " + recordsCount);
						}
						
						rs.next();
						cnt++;
					}
				}
				
				
				log.log(Level.INFO, list.size() + " elements to write");
				start = System.currentTimeMillis();
				eaphDao.createOrUpdate(null, list, true);
				end = System.currentTimeMillis();
				log.log(Level.INFO, list.size() + " elements written in " + (end - start) + "ms");
				
			} catch( Exception e ) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} finally {
				if( conn != null ) {
					try {
						conn.close();
					} catch( Exception e1 ) {}
				}
			}
			
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
