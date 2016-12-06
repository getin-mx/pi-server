package mobi.allshoppings.bdb.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.util.CollectionFactory;

import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.interfaces.StatusAware;

public class CSVVisitDetailExport {

	private static final Logger log = Logger.getLogger(CSVVisitDetailExport.class.getName());
	
	@Autowired
	private BrandDAO brandDao;
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private SystemConfiguration systemConfiguration;
	
	private Connection conn;
	private Statement stmt = null;
	private ResultSet rs = null;	

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
	
	public byte[] createCSVRepresentation(String authToken, String baseUrl, String brandId, String storeId, Date dateFrom, Date dateTo) throws ASException {
		
		// Get the Brand
		Brand brand = brandDao.get(brandId, true);
		
		// Get the Stores
		List<String> storeIds = CollectionFactory.createList();
		Store store = !StringUtils.hasText(storeId) ? null : storeDao.get(storeId, true);
		if( store != null ) {
			storeIds.add(store.getExternalId());
		} else {
			List<Store> stores = storeDao.getUsingBrandAndStatus(brand.getIdentifier(), Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED}), null);
			for(Store obj : stores ) {
				if( StringUtils.hasText(obj.getExternalId()))
					storeIds.add(obj.getExternalId());
			}
		}

		if( storeIds.isEmpty() ) 
			throw ASExceptionHelper.notAcceptedException();
		
		// Data Format ------------------------------------------------------------------------------------------------------

		try {

			buildCaches();

			StringBuffer sb = new StringBuffer();
			StringBuffer sbIn = new StringBuffer();
			for( int i = 0; i < storeIds.size(); i++ ) {
				if( i > 0 ) sbIn.append(",");
				sbIn.append(storeIds.get(i));
			}
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT  "
					+ "id_store, " 
					+ "mac, " 
					+ "Cast(Convert_tz(enter_at, 'UTC', 'America/Mexico_City') AS datetime) as enter_at, " 
					+ "(time_to_sec(timediff(quit_at, enter_at)) / 60) as perm "
					+ "from visit " 
					+ "where id_store in (" + sbIn.toString() + ") " 
					+ "and Cast(Convert_tz(enter_at, 'UTC', 'America/Mexico_City') AS date) >= Date('" + sdf.format(dateFrom) + "') "
					+ "and Cast(Convert_tz(enter_at, 'UTC', 'America/Mexico_City') AS date) <= Date('" + sdf.format(dateTo) + "') "
					+ "and mac in (select mac from visit where id_store in (" + sbIn + ") group by mac having count(mac) > 2)"
					);
			
			if( rs.first()) {
				while(!rs.isAfterLast()) {
					int extId = rs.getInt("id_store");
					String mac = rs.getString("mac");
					Date enterAt = rs.getTimestamp("enter_at");
					Double permanence = rs.getDouble("perm");

					sb.append("\"").append(extId)
					.append("\",\"").append(mac)
					.append("\",\"").append(sdf.format(enterAt))
					.append("\",\"").append(sdfTime.format(enterAt))
					.append("\",\"").append(permanence)
					.append("\"\n");
					
					rs.next();
				}
			}
			
			disposeCaches();
			
			return sb.toString().getBytes();
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
	private void buildCaches() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, ASException {

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
		log.log(Level.INFO, "General Cache Built");

	}

	private void disposeCaches() throws SQLException {
		conn.close();
		log.log(Level.INFO, "SQL Connection disposed");
	}
}
