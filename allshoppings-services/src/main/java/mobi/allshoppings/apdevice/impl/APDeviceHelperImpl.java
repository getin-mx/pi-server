package mobi.allshoppings.apdevice.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.datanucleus.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.google.common.io.Files;
import com.inodes.datanucleus.model.Blob;
import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;
import com.jcraft.jsch.JSchException;
import com.mongodb.DBObject;

import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.APHEntryDAO;
import mobi.allshoppings.dao.APHotspotDAO;
import mobi.allshoppings.dao.APUptimeDAO;
import mobi.allshoppings.dao.MacVendorDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumperHelperImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.mail.MailHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.model.APUptime;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.MacVendor;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.IndexHelper;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;

public class APDeviceHelperImpl implements APDeviceHelper {

	public static final long FIVE_MINUTES = 5 * 60 * 1000;
	public static final long TEN_MINUTES = 10 * 60 * 1000;
	public static final long THIRTY_MINUTES = 30 * 60 * 1000;
	public static final long SIXTY_MINUTES = 60 * 60 * 1000;
	public static final long ONE_DAY = 24 * 60 * 60 * 1000;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private static final Logger log = Logger.getLogger(APDeviceHelperImpl.class.getName());

	@Autowired
	private APDeviceDAO dao;
	@Autowired
	private MailHelper mailHelper;
	@Autowired
	private APUptimeDAO apuDao;
	@Autowired
	private MacVendorDAO mvDao;
	@Autowired
	private MacVendorDAO macVendorDao;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private APDAssignationDAO apdaDao;
	@Autowired
	private ShoppingDAO shoppingDao;
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private IndexHelper indexHelper;
	@Autowired
	private APHotspotDAO aphDao;
	@Autowired
	private APHHelper aphHelper;
	@Autowired
	private APHEntryDAO apheDao;
	
	private DumperHelper<APHotspot> dumpHelper;

	@Override
	public void updateMacVendors(String filename) throws ASException {

		// Deletes previous data
		log.log(Level.INFO, "Deleting previous MAC Vendors");
		mvDao.deleteAll();

		// Obtains the refreshed list
		log.log(Level.INFO, "Parsing file " + filename);
		List<MacVendor> list = macVendorFileParser(filename, null);

		// Writes the new list
		log.log(Level.INFO, "Writing " + list.size() + " new elements");
		for( MacVendor obj : list ) {
			obj.setKey(mvDao.createKey(obj.getMac()));
			mvDao.create(obj);
		}

	}

	@Override
	public String getDevicePlatform(String mac, Map<String, MacVendor> cache) {

		if (!StringUtils.hasText(mac))
			return "generic";
		if (mac.length() < 8)
			return "generic";

		MacVendor mv = null;
		if (null != cache) {
			mv = cache.get(mac);
			if (null == mv)
				mv = cache.get(mac.substring(0, 8));
		} else {
			try {
				mv = macVendorDao.get(mac, true);
			} catch (Exception e) {
				try {
					mv = macVendorDao.get(mac.substring(0, 8), true);
				} catch (Exception e1) {
				}
			}
		}

		if (mv == null)
			return "generic";

		if (mv.getCode().equals("Apple"))
			return "iOS";

		return "Android";
	}
	
	@Override
	public List<MacVendor> macVendorFileParser(String filename, String outfile) throws ASException {

		List<MacVendor> ret = CollectionFactory.createList();
		
		try {
			BufferedReader br = null;
			
			// Opens the file according to the source
			// The file is based on the one found at https://code.wireshark.org/review/gitweb?p=wireshark.git;a=blob_plain;f=manuf
			
			if( filename.toLowerCase().startsWith("http://") || filename.toLowerCase().startsWith("https://")) {
				URL url = new URL(filename);
			    byte[] bContents = null;
			    int count = 5;
			    while(( bContents == null || bContents.length == 0 ) && count > 0 ) {
			    	if( count < 5 ) try {Thread.sleep(500);}catch(Exception e1){}
			    	bContents = IOUtils.toByteArray(url.openStream());
			    	count--;
			    }
			    br = new BufferedReader(new StringReader(new String(bContents)));
			    
			} else {
				File f = new File(filename);
				if( f.exists() && f.canRead()) {
					br = new BufferedReader(new FileReader(f));
				}
			}

			// Prepares the output
			File out = null;
			FileOutputStream fos = null;
			if( StringUtils.hasText(outfile)) {
				out = new File(outfile);
				fos = new FileOutputStream(out);
			}
			
			// Scans the file
			for(String line; (line = br.readLine()) != null; ) {
				try {
					MacVendor element = parseMacVendorElement(line);
					if( element != null ) {
						
						ret.add(element);
						
						if( fos != null )
							fos.write(
									("\"" + element.getMac() + "\"," 
									+ "\"" + element.getCode() + "\"," 
									+ "\"" + element.getComments() + "\"\n").getBytes());
						else 
							System.out.println(element.toString());
					}
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
			
			// Closes open resources
			br.close();
			if( fos != null ) fos.close();

			// Returns the result
			return ret;
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}

	private MacVendor parseMacVendorElement(String buffer) {
		
		// Initial validations
		if( buffer == null ) return null;
		String myBuffer = buffer.trim();
		if( !StringUtils.hasText(myBuffer)) return null;
		if( myBuffer.startsWith("#")) return null;
		if( myBuffer.startsWith(";")) return null;
		if( myBuffer.startsWith("\n")) return null;
		if( myBuffer.startsWith("\r")) return null;

		// Defines the required fields
		String mac = "";
		String code = "";
		String comments = "";
		
		// Starts to Separate the contents in the format:
		// MAC \t Code \b # Comments
		
		// Starts trying to get comments
		String[] parts = myBuffer.split("#");
		if( parts.length > 1 ) comments = parts[1].trim().replaceAll("\t", " ");
		
		// Now divides mac from code
		parts = parts[0].split("\t");
		if( parts.length < 2) return null;
		
		mac = parts[0].trim();
		code = parts[1].trim();
		
		// Final validation
		if( mac.contains("/")) return null;
		if( !StringUtils.hasText(code)) return null;

		mac = mac.toLowerCase();
		mac = mac.replace("-", ":");
		
		// Prints the result
		MacVendor res = new MacVendor(mac, code, comments);
		
		return res;
	}
		
	@Override
	public void updateDeviceData(String identifier, String description, boolean enableAlerts, List<String> alertMails) throws ASException {

		APDevice device = null;
		boolean forUpdate = true;

		try {
			device = dao.get(identifier, true);
		} catch( ASException e ) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
				device = new APDevice();
				device.setHostname(identifier);
				device.setKey(dao.createKey(identifier));
				forUpdate = false;
			}
		}
		device.setDescription(description);
		device.setReportable(enableAlerts);
		device.setStatus(StatusAware.STATUS_ENABLED);
		if( enableAlerts ) {
			if( device.getReportMailList() == null )
				device.setReportMailList(new ArrayList<String>());
			device.getReportMailList().clear();
			device.getReportMailList().addAll(alertMails);
			device.setReportStatus(APDevice.REPORT_STATUS_NOT_REPORTED);
		}

		if( forUpdate )
			dao.update(device);
		else
			dao.create(device);

	}


	@Override
	public void reportDownDevices() throws ASException {

		Date limitDate = new Date(new Date().getTime() - SIXTY_MINUTES);

		List<APDevice> list = dao.getAll(true);
		for( APDevice device : list ) {

			// First, check is the device needs report
			if( device.getLastRecordDate() != null && device.getLastRecordDate().before(limitDate)) {
				if( device.getStatus() == null ) device.setStatus(StatusAware.STATUS_ENABLED);
				if( device.getReportStatus() == null ) device.setReportStatus(APDevice.REPORT_STATUS_REPORTED);
				if(device.getReportable() != null && device.getReportable() && device.getReportMailList() != null && device.getReportMailList().size() > 0 ) {
					if( device.getStatus().equals(StatusAware.STATUS_ENABLED) && device.getReportStatus().equals(APDevice.REPORT_STATUS_NOT_REPORTED)) {

						String mailText = "The device " + device.getHostname()
						+ " referent of " + device.getDescription()
						+ " is not sending mac addresses since "
						+ device.getLastRecordDate()
						+ "<br/><br/>Detailed record is:<br/><br/>"
						+ device.toString();
						String mailTitle = "Device " + device.getDescription() + " is Down!!!";

						for( String mail : device.getReportMailList() ) {
							User fake = new User();
							fake.setEmail(mail);
							try {
								mailHelper.sendMessage(fake, mailTitle, mailText);
							} catch( Exception e ) {
								// If mail server rejected the message, keep going
								log.log(Level.SEVERE, e.getMessage(), e);
							}
						}

						device.setReportStatus(APDevice.REPORT_STATUS_REPORTED);
						dao.update(device);
						indexHelper.indexObject(device);
					}
				}
			} 

			// Now, checks if needs to report a back to life message
			else {
				if(device.getReportable() != null && device.getReportable() && device.getReportMailList() != null && device.getReportMailList().size() > 0 ) {
					if( device.getStatus() == null ) device.setStatus(StatusAware.STATUS_ENABLED);
					if( device.getReportStatus() == null ) device.setReportStatus(APDevice.REPORT_STATUS_REPORTED);
					if( device.getStatus().equals(StatusAware.STATUS_ENABLED) && device.getReportStatus().equals(APDevice.REPORT_STATUS_REPORTED)) {

						String mailText = "The device " + device.getHostname() + " referent of " + device.getDescription() + " is back alive!";
						String mailTitle = "Device " + device.getDescription() + " is Back to Normal!!!";

						for( String mail : device.getReportMailList() ) {
							User fake = new User();
							fake.setEmail(mail);
							try {
								mailHelper.sendMessage(fake, mailTitle, mailText);
							} catch( Exception e ) {
								// If mail server rejected the message, keep going
								log.log(Level.SEVERE, e.getMessage(), e);
							}
						}

						device.setReportStatus(APDevice.REPORT_STATUS_NOT_REPORTED);
						dao.update(device);
						indexHelper.indexObject(device);
					}
				}
			}


		}
	}

	public void calculateUptimeFromDump(String baseDir, Date fromDate, Date toDate, List<String> apdevices) throws ASException {

		Map<String, APUptime> cache = CollectionFactory.createMap();

		// Populates the apdevices list if empty
		if(CollectionUtils.isEmpty(apdevices)) {
			apdevices = CollectionFactory.createList();
			List<APDevice> list = dao.getAll(true);
			for( APDevice obj : list ) {
				apdevices.add(obj.getHostname());
			}
		}

		// Prepares the basic records
		Date date = new Date(fromDate.getTime());
		while( date.before(toDate)) {
			for( String hostname : apdevices ) {
				APUptime apu = null;
				try {
					apu = apuDao.getUsingHostnameAndDate(hostname, date);
				} catch( ASException e ) {
					if( ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE == e.getErrorCode()) {
						apu = new APUptime(hostname, date);
						apu.setKey(apuDao.createKey(hostname, date));
						apuDao.create(apu);
					}
				}

				cache.put(apu.getKey().getName(), apu);
			}
			date = new Date(date.getTime() + 86400000);
		}

		// Gets the input data
		long totals = 0;
		dumpHelper = new DumperHelperImpl<APHotspot>(baseDir, APHotspot.class);
		Iterator<APHotspot> i = dumpHelper.iterator(fromDate, toDate);
		while( i.hasNext() ) {
			APHotspot aph = i.next();
			if( totals % 1000 == 0 ) 
				log.log(Level.INFO, "Processing for date " + aph.getCreationDateTime());

			if( apdevices.contains(aph.getHostname())) {
				Key apuKey = apuDao.createKey(aph.getHostname(), aph.getCreationDateTime());
				APUptime apu = cache.get(apuKey.getName());
				String key = APUptime.getRecordKey(aph.getCreationDateTime());
				if( apu != null && apu.getRecord() != null ) {
					Integer val = apu.getRecord().get(key);
					if( val.equals(0)) {
						apu.getRecord().put(key, 1);
					}
				}
			}

			totals++;
		}

		// Write to the database
		Iterator<String> x = cache.keySet().iterator();
		while(x.hasNext()) {
			String key = x.next();
			APUptime apu = cache.get(key);
			apuDao.update(apu);
		}
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
		if( obj instanceof ModelKey) ((ModelKey)obj).setKey(objKey);

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

	public class GetinMac {
		String hostname;
		String mac;
		/**
		 * @return the hostname
		 */
		public String getHostname() {
			return hostname;
		}
		/**
		 * @param hostname the hostname to set
		 */
		public void setHostname(String hostname) {
			this.hostname = hostname;
		}
		/**
		 * @return the mac
		 */
		public String getMac() {
			return mac;
		}
		/**
		 * @param mac the mac to set
		 */
		public void setMac(String mac) {
			this.mac = mac;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "GetinMac [hostname=" + hostname + ", mac=" + mac + "]";
		}

	}

	public void tryRestartAPDevices() throws ASException {
		List<APDevice> list = dao.getAll(true);
		Date tenMinutes = new Date(new Date().getTime() - TEN_MINUTES);
		Date oneDay = new Date(new Date().getTime() - ONE_DAY);
		
		for( APDevice apdevice : list ) {
			if (null != apdevice.getStatus() && StatusAware.STATUS_ENABLED == apdevice.getStatus() 
					&& apdevice.getReportable() != null && apdevice.getReportable() == true
					&& apdevice.getLastRecordDate() != null && apdevice.getLastRecordDate().before(tenMinutes)
					&& apdevice.getLastRecordDate().after(oneDay)) {

				try {
					restartAPDevice(apdevice);
				} catch( Exception e ) {
					log.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}
	
	public void restartAPDevice(String identifier) throws ASException {
		APDevice apdevice = dao.get(identifier, true);
		restartAPDevice(apdevice);
	}
	
	public void restartAPDevice(APDevice apdevice) throws ASException {
		StringBuffer stdout = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		@SuppressWarnings("unused")
		int exitStatus = 0;

		String[] command = new String[] {
				"reboot"
		};

		APDeviceSSHSession session = new APDeviceSSHSession(apdevice, systemConfiguration);
		try {
			session.connect();
			for(String cmd : command ) {
				try {
					exitStatus = session.executeCommandOnSSHSession(session.getApdSession(), cmd, stdout, stderr);
				} catch( Exception e ) {}
			}
			
		} finally {
			session.disconnect();
		}
	}
	
	public void updateAPDeviceStatus(String identifier) throws ASException {
		APDevice apdevice = dao.get(identifier, true);
		StringBuffer stdout = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		int exitStatus = 0;

		String[] command = new String[] {
				"wget http://aragorn.getin.mx/sysinfo.sh -O /tmp/sysinfo.sh",
				"chmod 775 /tmp/sysinfo.sh",
				"sh /tmp/sysinfo.sh",
				"rm -f /tmp/sysinfo.sh"
		};

		APDeviceSSHSession session = new APDeviceSSHSession(apdevice, systemConfiguration);
		try {
			session.connect();
			for(String cmd : command ) {
				try {
					exitStatus = session.executeCommandOnSSHSession(session.getApdSession(), cmd, stdout, stderr);
				} catch( Exception e ) {}
			}
		} finally {
			session.disconnect();
		}

		log.log(Level.INFO, "Command " + command + " executed on " + identifier + " with exit status " + exitStatus);
		log.log(Level.INFO, "STDOUT");
		log.log(Level.INFO, stdout.toString());
		log.log(Level.INFO, "STDERR");
		log.log(Level.INFO, stderr.toString());
	}

	public void updateAPDeviceInfo(String identifier) throws ASException {
		APDevice apdevice = dao.get(identifier, true);
		StringBuffer stdout = new StringBuffer();
		StringBuffer stderr = new StringBuffer();
		@SuppressWarnings("unused")
		int exitStatus = 0;

		String[] command = new String[] {
				"wget http://aragorn.getin.mx/antennainfo.sh -O /tmp/antennainfo.sh",
				"chmod 775 /tmp/antennainfo.sh",
				"sh /tmp/antennainfo.sh",
				"rm -f /tmp/antennainfo.sh"
		};

		APDeviceSSHSession session = new APDeviceSSHSession(apdevice, systemConfiguration);
		try {
			session.connect();
			for(String cmd : command ) {
				try {
					exitStatus = session.executeCommandOnSSHSession(session.getApdSession(), cmd, stdout, stderr);
				} catch( Exception e ) {}
			}
			
			JSONObject json = new JSONObject(stdout.toString());
			apdevice.setMode(json.getString("mode"));
			apdevice.setModel(json.getString("model"));
			apdevice.setVersion(json.getString("version"));
			apdevice.setTunnelIp(json.getString("tunnelIp"));
			apdevice.setLanIp(json.getString("lanIp"));
			apdevice.setWanIp(json.getString("wanIp"));
			apdevice.setPublicIp(json.getString("publicIp"));
			apdevice.setLastInfoUpdate(new Date());
			
			try {
				apdevice = geoIp(apdevice);
			} catch( Exception e ) {
				log.log(Level.WARNING, e.getMessage(), e);
			}
			
			dao.update(apdevice);

		} finally {
			session.disconnect();
		}
	}

	/**
	 * Try to find out location using the public IP Address
	 * 
	 * @param apd
	 *            The APDevice to use as reference
	 * @return The completed APDevice
	 */
	@Override
	public APDevice geoIp(APDevice apd) throws ASException {
		try {

			URL url = new URL("http://freegeoip.net/json/" + apd.getPublicIp());
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);

			JSONObject resp = new JSONObject(body);
			apd.setCountry(resp.getString("country_name"));
			apd.setProvince(resp.getString("region_name"));
			apd.setCity(resp.getString("city"));
			apd.setLat(resp.getDouble("latitude"));
			apd.setLon(resp.getDouble("longitude"));
			
			in.close();

			return apd;

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
	/**
	 * Imports apdevice parameters from legacy getin interface <b>NOTE</b>: This method
	 * is intended for temporary use only
	 * 
	 * @param hostnames
	 *            List of hostnames to import. If it is null, it will import all
	 *            hostnames information
	 * 
	 */
	@Override
	public void importParametersFromLegacy(List<String> hostnames) throws ASException {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		List<String> nomodify = Arrays.asList(new String[] { "ashs-0024", "ashs-0032", "ashs-0033", "ashs-0034",
				"ashs-0037", "ashs-0038", "gihs-0132", "ashs-0027", "ashs-0067", "ashs-0036" });

		SimpleDateFormat tdf = new SimpleDateFormat("HH:mm");
		
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
			rs = stmt.executeQuery("SELECT * from wifi_sensor");
			
			if( rs.first()) {
				while(!rs.isAfterLast()) {
					String hostname = rs.getString("hostname");
					if(( hostnames == null || hostnames.contains(hostname))
							&& !"-".equals(hostname)) {

						if(!nomodify.contains(hostname)) {
							boolean update = true;
							APDevice apd;
							try {
								apd = dao.get(hostname, true);
							} catch( Exception e ) {
								apd = new APDevice();
								apd.setHostname(hostname);
								apd.setKey(dao.createKey(hostname));
								update = false;
							}

							int visitTimeThreshold = rs.getInt("visit_time_thrs");
							int visitGapThreshold = rs.getInt("visit_gap_thrs");
							int visitPowerThreshold = rs.getInt("visit_pwr_thrs");
							int visitMaxThreshold = rs.getInt("visit_max_thrs");
							int daysToNotRepit = rs.getInt("bash_repit_thrs");

							String timezone = rs.getString("timezone");
							String ovisitStart = rs.getString("visits_begin");
							String ovisitEnd = rs.getString("visits_end");
							String omonitorStart = rs.getString("monitor_begin");
							String omonitorEnd = rs.getString("monitor_end");
							String opassStart = rs.getString("pass_begin");
							String opassEnd = rs.getString("pass_end");
							int negative = rs.getInt("negative");

							String visitStart = tdf.format(new Date(tdf.parse(ovisitStart).getTime() - 21600000));
							String visitEnd = tdf.format(new Date(tdf.parse(ovisitEnd).getTime() - 21600000));
							String monitorStart = tdf.format(new Date(tdf.parse(omonitorStart).getTime() - 21600000));
							String monitorEnd = tdf.format(new Date(tdf.parse(omonitorEnd).getTime() - 21600000));
							String passStart = tdf.format(new Date(tdf.parse(opassStart).getTime() - 21600000));
							String passEnd = tdf.format(new Date(tdf.parse(opassEnd).getTime() - 21600000));

							apd.setVisitTimeThreshold(Long.valueOf(visitTimeThreshold));
							apd.setVisitGapThreshold(Long.valueOf(visitGapThreshold));
							apd.setVisitPowerThreshold(Long.valueOf(visitPowerThreshold));
							apd.setVisitMaxThreshold(Long.valueOf(visitMaxThreshold));
							apd.setDaysToNotRepit(daysToNotRepit);
							apd.setTimezone(timezone);
							apd.setNegative(negative == 0 ? false : true);
							apd.setMonitorStart(monitorStart);
							apd.setMonitorEnd(monitorEnd);
							apd.setPassStart(passStart);
							apd.setPassEnd(passEnd);
							apd.setVisitCountThreshold(0L);
							apd.setVisitsOnMon(true);
							apd.setVisitsOnTue(true);
							apd.setVisitsOnWed(true);
							apd.setVisitsOnThu(true);
							apd.setVisitsOnFri(true);
							apd.setVisitsOnSat(true);
							apd.setVisitsOnSun(true);
							apd.setVisitStartMon(visitStart);
							apd.setVisitEndMon(visitEnd);
							apd.setVisitStartTue(visitStart);
							apd.setVisitEndTue(visitEnd);
							apd.setVisitStartWed(visitStart);
							apd.setVisitEndWed(visitEnd);
							apd.setVisitStartThu(visitStart);
							apd.setVisitEndThu(visitEnd);
							apd.setVisitStartFri(visitStart);
							apd.setVisitEndFri(visitEnd);
							apd.setVisitStartSat(visitStart);
							apd.setVisitEndSat(visitEnd);
							apd.setVisitStartSun(visitStart);
							apd.setVisitEndSun(visitEnd);

							if( update ) {
								dao.update(apd);
							} else {
								dao.create(apd);
							}
						}
					}
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

	}
	
	/**
	 * Gets a file content from an APDevice
	 * 
	 * @param identifier
	 *            The APDevice Identifier
	 * @param fileName
	 *            The file to get the contents from
	 * @return The file Contents
	 * @throws ASException
	 */
	public byte[] getFileFromAPDevice(String identifier, String fileName) throws ASException {
		APDevice apdevice = dao.get(identifier, true);
		return getFileFromAPDevice(apdevice, fileName);
	}

	/**
	 * Gets a file content from an APDevice
	 * 
	 * @param identifier
	 *            The APDevice Identifier
	 * @param fileName
	 *            The file to get the contents from
	 * @return The file Contents
	 * @throws ASException
	 */
	public byte[] getFileFromAPDevice(APDevice apdevice, String fileName) throws ASException {
		APDeviceSSHSession session = new APDeviceSSHSession(apdevice, systemConfiguration);

		try {
			session.connect();
			StringBuffer stdout = new StringBuffer();
			StringBuffer stderr = new StringBuffer();
			int exitStatus = session.executeCommandOnSSHSession(session.getApdSession(), "cat " + fileName , stdout, stderr);
			if( exitStatus != 0 ) throw new FileNotFoundException();
			return stdout.toString().getBytes();
		} catch( ASException e ) {
			throw e;
		} catch( JSchException e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} catch (IOException e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			session.disconnect();
		}
	}

	/**
	 * Executes a command in an APDevice
	 * @param identifier The APDevice Identifier
	 * @param command The command to execute
	 * @param stdout The command standard output results
	 * @return
	 * @throws ASException
	 */
	public int executeCommandOnAPDevice(String identifier, String command, StringBuffer stdout, StringBuffer stderr) throws ASException {
		APDevice apdevice = dao.get(identifier);
		return executeCommandOnAPDevice(apdevice, command, stdout, stderr);
	}

	/**
	 * Executes a command in an APDevice
	 * @param identifier The APDevice Identifier
	 * @param command The command to execute
	 * @param stdout The command standard output results
	 * @return
	 * @throws ASException
	 */
	public int executeCommandOnAPDevice(APDevice apdevice, String command, StringBuffer stdout, StringBuffer stderr) throws ASException {
		APDeviceSSHSession session = new APDeviceSSHSession(apdevice, systemConfiguration);

		try {
			session.connect();
			session.executeCommandOnSSHSession(session.getApdSession(), command, stdout, stderr);
			return 0;
		} catch( ASException e ) {
			throw e;
		} catch( JSchException e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} catch (IOException e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally {
			session.disconnect();
		}
	}

	/**
	 * Updates an APDevice according to its assignations
	 * 
	 * @param hostname
	 *            APDevice hostname to update
	 * @throws ASException
	 */
	@Override
	public void updateAssignationsUsingAPDevice(String hostname) throws ASException {
		APDevice apd = dao.get(hostname, true);
		List<APDAssignation> list = apdaDao.getUsingHostnameAndDate(hostname, new Date());
		
		if( null == apd.getStatus() ) 
			apd.setStatus(StatusAware.STATUS_ENABLED);
		
		if( null == apd.getReportStatus() )
			apd.setReportStatus(APDevice.REPORT_STATUS_NOT_REPORTED);

		if( list.size() == 0 ) {
			// APDevice is no longer assigned
			apd.setDescription(null);
			apd.setReportable(false);
			if( apd.getReportMailList() == null ) {
				apd.setReportMailList(new ArrayList<String>());
			} else {
				apd.getReportMailList().clear();
			}
		} else {
			// APDevice is assigned
			APDAssignation apda = list.get(0);
			if( apda.getEntityKind().equals(EntityKind.KIND_SHOPPING)) {
				Shopping shopping = shoppingDao.get(apda.getEntityId());
				apd.setDescription(shopping.getName());
			} else if( apda.getEntityKind().equals(EntityKind.KIND_STORE)) {
				Store store = storeDao.get(apda.getEntityId());
				apd.setDescription(store.getName());
			}
			apd.setStatus(StatusAware.STATUS_ENABLED);
			apd.setReportable(true);
			if( apd.getReportMailList() == null )
				apd.setReportMailList(new ArrayList<String>());
			if( apd.getReportMailList().isEmpty()) {
				apd.getReportMailList().clear();
				apd.getReportMailList().addAll(systemConfiguration.getApdReportMailList());
			}
		}
		
		dao.update(apd);
		indexHelper.indexObject(apd);
	}

	@Override
	public void unassignUsingAPDevice(String hostname) throws ASException {
		List<APDAssignation> list = apdaDao.getUsingHostnameAndDate(hostname, new Date());

		for( APDAssignation apda : list ) {
			try {
				apda.setToDate(sdf.parse(sdf.format(new Date())));
				apdaDao.update(apda);
			} catch( Exception e ) {}
		}

		try {
			updateAssignationsUsingAPDevice(hostname);
		} catch( Exception e ) {}
	}

	@Override
	public void importRecordsFromFileSystem(String dir, String backupDir, Date fakeDeviationStartDate) throws ASException {
	
		File inputDir = new File(dir);
		File outputDir = new File(backupDir);
		
		long firstUnixTime = 0;
		long deviationOffset = 0;
		
		// Validates input directory
		if(!inputDir.exists() || !inputDir.isDirectory())
			throw ASExceptionHelper.invalidArgumentsException("input dir is not a directory");
		
		// Validates output directory
		if(outputDir.exists()) {
			if(!outputDir.isDirectory())
				throw ASExceptionHelper.invalidArgumentsException("output dir is not a directory");
		} else {
			boolean res = outputDir.mkdirs();
			if(!res)
				throw ASExceptionHelper.defaultException("Cannot create output directory", null);
		}
		
		// Gets the first unixtime needed
		if( null != fakeDeviationStartDate )
			firstUnixTime = (long)(fakeDeviationStartDate.getTime() / 1000);		
		
		// Get directory contents and start iteration
		List<File> contents = new ArrayList<File>(Arrays.asList(inputDir.listFiles()));
		Collections.sort(contents);
		for( File file : contents ) {
			// Just use .dat files
			if( file.getName().endsWith(".dat")) {
				
				long fileUnixTime = Long.parseLong(file.getName().split(".dat")[0]);
				// Charge file offset
				if( firstUnixTime > 0 && deviationOffset == 0 ) 
					deviationOffset = firstUnixTime - fileUnixTime;
				
				try {
					String jsonText = getFileContents(file);
					JSONObject json = new JSONObject(jsonText);

					// Do main process

					String hostname = json.getString("hostname");

					JSONArray data = json.getJSONArray("data");

					log.log(Level.INFO, "Reporting " + data.length() + " AP Members from " + hostname);
					log.log(Level.FINEST, json.toString());

					for( int i = 0; i < data.length(); i++ ) {

						try {
							JSONObject ele = (JSONObject)data.get(i);

							APHotspot aphotspot = new APHotspot();
							aphotspot.setHostname(hostname);
							aphotspot.setFirstSeen(new Date((ele.getLong("firstSeen") + deviationOffset) * 1000));
							aphotspot.setLastSeen(new Date((ele.getLong("lastSeen") + deviationOffset) * 1000));
							aphotspot.setMac(ele.getString("mac").toLowerCase());
							aphotspot.setSignalDB(ele.getInt("signalDB"));
							aphotspot.setCount(ele.getInt("count"));
							aphotspot.setKey(aphDao.createKey());
							
							// Just for offset setting... modify the creation date time
							aphotspot.setCreationDateTime(new Date((fileUnixTime + deviationOffset) * 1000));

							if(!aphotspot.getMac().startsWith("broadcast")) {
								aphDao.create(aphotspot);

								// Updates APHEntries
								try {
									aphHelper.setUseCache(false);
									APHEntry aphe = aphHelper.setFramedRSSI(aphotspot);
									aphHelper.artificiateRSSI(aphe);
									if( StringUtils.hasText(aphe.getIdentifier())) {
										apheDao.update(aphe);
									} else {
										aphe.setKey(apheDao.createKey(aphe));
										apheDao.create(aphe);
									}
								} catch( Exception e ) {
									log.log(Level.SEVERE, "Error updating APHEntries", e);
								}
							}
							
						} catch( Exception e ) {
							log.log(Level.SEVERE, e.getMessage(), e);
						}
					}
					
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
				
				// Backups the file
				try {
					Files.move(file, new File(outputDir.getAbsoluteFile() + File.separator + file.getName()));
				} catch( Exception e ) {
					throw ASExceptionHelper.defaultException(e.getMessage(), e);
				}
			}
		}
	}
	
	
	private String getFileContents(File inputFile) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    return(sb.toString());
		} finally {
		    br.close();
		}

	}
}
