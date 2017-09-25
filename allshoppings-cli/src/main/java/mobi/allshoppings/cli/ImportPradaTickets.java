package mobi.allshoppings.cli;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.StoreItemDAO;
import mobi.allshoppings.dao.StoreRevenueDAO;
import mobi.allshoppings.dao.StoreTicketDAO;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.mail.MailHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreItem;
import mobi.allshoppings.model.StoreRevenue;
import mobi.allshoppings.model.StoreTicket;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.CollectionFactory;


public class ImportPradaTickets extends AbstractCLI {

	private static final Logger log = Logger.getLogger(ImportPradaTickets.class.getName());
	
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
	
	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			StoreTicketDAO storeTicketDao = (StoreTicketDAO)
					getApplicationContext().getBean("storeticket.dao.ref");
			StoreItemDAO storeItemDao = (StoreItemDAO)
					getApplicationContext().getBean("storeitem.dao.ref");
			StoreRevenueDAO storeRevenueDao = (StoreRevenueDAO)
					getApplicationContext().getBean("storerevenue.dao.ref");	
			DashboardAPDeviceMapperService mapper = (DashboardAPDeviceMapperService)
					getApplicationContext().getBean("dashboard.apdevice.mapper");
			MailHelper mailHelper = (MailHelper)getApplicationContext().getBean("mail.helper");
			SystemConfiguration systemConfiguration = (SystemConfiguration)
					getApplicationContext().getBean("system.configuration");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String fromDate = null;
			String toDate = null;
			
			try {

				if(options.has("toDate")) {
					toDate = (String)options.valueOf("toDate");
				} else {
					toDate = sdf.format(new Date());
				}
				
				if(options.has("fromDate")) {
					fromDate = (String)options.valueOf("fromDate");
				} else {
					Calendar cal = Calendar.getInstance();
					cal.setTime(sdf.parse(toDate));
					cal.add(Calendar.DATE, -3);
					fromDate = sdf.format(cal.getTime());
				}
				
				if( options.has("help")) usage(parser);				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Starting to import Prada Tickets");

			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
						
			String connectionString = "jdbc:sqlserver://192.168.1.200:1433;databaseName=GETIN";
			String user = "getin";
			String pass = "Getin*2017";
			
			String query1 = "SELECT * FROM Ventas";
			
			try {
				// Prepares SQL Connection
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
				try {
					conn = DriverManager.getConnection(connectionString, user, pass);
					log.log(Level.INFO, "SQL Connection obtained");
				} catch (SQLException ex) {
					// handle any errors
					log.log(Level.SEVERE, "SQLException: " + ex.getMessage());
					log.log(Level.SEVERE, "SQLState: " + ex.getSQLState());
					log.log(Level.SEVERE, "VendorError: " + ex.getErrorCode());
					log.log(Level.SEVERE, "", ex);
					
					String mailText = "Hola!\n\n"
							+ "El proceso de importación de Tickets de Prada no ha podido ejecutarse. Revisa por favor que la VPN con Prada esté activa " 
							+ "Muchas gracias.\n\n"
							+ " Atte. \n" 
							+ "El equipo de Getin";
					String mailTitle = "Proceso de Importación de Tickets de Prada";

					List<String> reportableUsers = CollectionFactory.createList();
					reportableUsers.addAll(systemConfiguration.getApdReportMailList());

					for( String mail : reportableUsers) {
						User fake = new User();
						fake.setEmail(mail);
						try {
							mailHelper.sendMessage(fake, mailTitle, mailText);
						} catch( Exception e ) {
							// If mail server rejected the message, keep going
							log.log(Level.SEVERE, e.getMessage(), e);
						}
					}
					throw ex;
				}

				long start = System.currentTimeMillis();
				log.log(Level.INFO, "Executing query: " + query1);
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query1);
				long end = System.currentTimeMillis();
				log.log(Level.INFO, "Query executed in " + (end - start) + "ms");
				
				Date d1 = sdf.parse(fromDate);
				Date d2 = sdf.parse(toDate);

				int count = 0;
				String identifier, d;
				Integer tickets, items;
				Double revenue;
				Store s;
				StoreTicket st;
				StoreRevenue sr;
				StoreItem si;
				while(rs.next()) {
					Date forDate = rs.getDate(1);
					if(( forDate.after(d1) || forDate.equals(d1)) && ( forDate.before(d2) ||
							forDate.equals(d2))) { 
						identifier = rs.getString(2);
						tickets = rs.getInt(4);
						revenue = rs.getDouble(5);
						items = rs.getInt(6);
						log.log(Level.INFO, forDate + "\t" + identifier + "\t" + tickets +
								"\t" + revenue + "\t" + items);
						try {
							s = storeDao.get(identifier, true);
							d = sdf.format(forDate);
							st = null;
							sr = null;
							si = null;
							try {
								st = storeTicketDao.getUsingStoreIdAndDate(identifier, d, true);
								st.setQty(tickets);
								storeTicketDao.update(st);
							} catch( Exception e ) {
								st = new StoreTicket();
								st.setBrandId(s.getBrandId());
								st.setDate(d);
								st.setStoreId(identifier);
								st.setKey(storeTicketDao.createKey());
								st.setQty(tickets);
								storeTicketDao.create(st);
							} try {
								sr = storeRevenueDao.getUsingStoreIdAndDate(identifier, d, true);
								sr.setQty(revenue);
								storeRevenueDao.update(sr);
							} catch(Exception e) {
								sr = new StoreRevenue();
								sr.setBrandId(s.getBrandId());
								sr.setDate(d);
								sr.setStoreId(identifier);
								sr.setKey(storeRevenueDao.createKey());
								sr.setQty(revenue);
								storeRevenueDao.create(sr);
							} try {
								si = storeItemDao.getUsingStoreIdAndDate(identifier, d, true);
								si.setQty(items);
								storeItemDao.update(si);
							} catch(Exception e) {
								si = new StoreItem();
								si.setBrandId(s.getBrandId());
								si.setDate(d);
								si.setStoreId(identifier);
								si.setKey(storeItemDao.createKey());
								si.setQty(items);
								storeItemDao.create(si);
							}	
							mapper.createStoreTicketDataForDates(d, d, identifier);
							mapper.createStoreRevenueDataForDates(d, d, identifier);
							mapper.createStoreItemDataForDates(d, d, identifier);
							count++;

						} catch( ASException e ) {
							if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
								log.log(Level.WARNING, e.getMessage(), e);
							}
						}
					}
				}
				end = System.currentTimeMillis();
				log.log(Level.INFO, "Process ended in " + (end-start) + "ms for " + count + " records!");
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
