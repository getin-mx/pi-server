package mobi.allshoppings.cli;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.StoreTicketDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreTicket;
import mobi.allshoppings.model.SystemConfiguration;


public class TicketDump extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TicketDump.class.getName());

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {
		try {
			
			StoreTicketDAO dao = (StoreTicketDAO)getApplicationContext().getBean("storeticket.dao.ref");
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			SystemConfiguration systemConfiguration = (SystemConfiguration)getApplicationContext().getBean("system.configuration");
			
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			try {
				// Prepares SQL Connection
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				try {
					conn = DriverManager.getConnection(systemConfiguration.getGetinSqlUrl());
					log.log(Level.INFO, "SQL Connection obtained");
				} catch (SQLException ex) {
					// handle any errors
					log.log(Level.SEVERE, "SQLException: " + ex.getMessage());
					log.log(Level.SEVERE, "SQLState: " + ex.getSQLState());
					log.log(Level.SEVERE, "VendorError: " + ex.getErrorCode());
					log.log(Level.SEVERE, "", ex);
					throw ex;
				}

				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * from ticket");
				int cnt = 0;
				
				if( rs.first()) {
					while(!rs.isAfterLast()) {
						Integer storeId = rs.getInt("id_store");
						Date readAt = rs.getDate("read_at");
						Integer tickets = rs.getInt("tickets");
						
						try {
							Store store = storeDao.getUsingExternalId(String.valueOf(storeId));
							StoreTicket obj = new StoreTicket();
							obj.setBrandId(store.getBrandId());
							obj.setStoreId(store.getIdentifier());
							obj.setDate(sdf.format(readAt));
							obj.setQty(tickets);
							obj.setKey(dao.createKey());
							
							dao.create(obj);
						} catch( Exception e ) {
							log.log(Level.SEVERE, e.getMessage(), e);
						}

						
						cnt++;
						log.log(Level.INFO, cnt + " tickets so far...");
						rs.next();

					}
				}
				
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
