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
import org.apache.commons.io.IOUtils;
import org.datanucleus.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.ibm.icu.util.Calendar;
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
import mobi.allshoppings.dao.APUptimeDAO;
import mobi.allshoppings.dao.InnerZoneDAO;
import mobi.allshoppings.dao.MacVendorDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.mail.MailHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.APUptime;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.InnerZone;
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
	public static final long ONE_DAY = 24 * 60 * 60 * 1000;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

	private static final Logger log = Logger.getLogger(APDeviceHelperImpl.class.getName());

	@Autowired
	private APDeviceDAO dao;
	@Autowired
	private MailHelper mailHelper;
	@Autowired
	private APUptimeDAO apuDao;
	@Autowired
	private InnerZoneDAO zoneDao;
	@Autowired
	private MacVendorDAO mvDao;
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
	private APHHelper aphHelper;
	
	
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

		if( systemConfiguration.getAppleMacs().contains(mac.substring(0, 8)))
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

	/**
	 * Sanitizes the mail list according to the antenna mail list plus the
	 * system configuration general mail list
	 * 
	 * @param device
	 *            The device to sanitize the mail list for
	 * @return A fully formal mail address list adapted to the device
	 */
	private List<String> sanitizeMailList(APDevice device) {

		List<String> reportableUsers = CollectionFactory.createList();
		for( String mail : device.getReportMailList()) {
			String sMail = mail.trim().toLowerCase();
			if(!reportableUsers.contains(sMail))
				reportableUsers.add(sMail);
		}
		
		for( String mail : systemConfiguration.getApdReportMailList()) {
			String sMail = mail.trim().toLowerCase();
			if(!reportableUsers.contains(sMail)) 
				reportableUsers.add(sMail);
		}

		return reportableUsers;
	}

	@Override
	public void reportDownDevices() throws ASException {

		Date limitDate = new Date(new Date().getTime() - THIRTY_MINUTES);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		
		List<APDevice> list = dao.getAll(true);
		for( APDevice device : list ) {

			// First, check is the device needs report
			if( device.getLastRecordDate() != null && device.getLastRecordDate().before(limitDate)) {
				if( device.getStatus() == null ) device.setStatus(StatusAware.STATUS_ENABLED);
				if( device.getReportStatus() == null ) device.setReportStatus(APDevice.REPORT_STATUS_REPORTED);
				if(device.getReportable() != null && device.getReportable()) {
					if( device.getStatus().equals(StatusAware.STATUS_ENABLED) && device.getReportStatus().equals(APDevice.REPORT_STATUS_NOT_REPORTED)) {

						if( onReportTime(device)) {
						
							String mailText = "Hola!\n\n"
									+ "La antena de " + device.getDescription() + " se apagó desde las " 
									+ timeFormat.format(device.getLastRecordDate()) + " hrs. Les pedimos su apoyo para volver a conectarla.\n\n" 
									+ "Muchas gracias.\n\n"
									+ " Atte. \n" 
									+ "El equipo de Getin";
							String mailTitle = "La antena de " + device.getDescription() + " se encuentra apagada";

							List<String> reportableUsers = sanitizeMailList(device);
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

							device.setReportStatus(APDevice.REPORT_STATUS_REPORTED);
							dao.update(device);
							indexHelper.indexObject(device);
						}
					}
				}
			} 

			// Now, checks if needs to report a back to life message
			else {
				if(device.getReportable() != null && device.getReportable()) {
					if( device.getStatus() == null ) device.setStatus(StatusAware.STATUS_ENABLED);
					if( device.getReportStatus() == null ) device.setReportStatus(APDevice.REPORT_STATUS_REPORTED);
					if( device.getStatus().equals(StatusAware.STATUS_ENABLED) && device.getReportStatus().equals(APDevice.REPORT_STATUS_REPORTED)) {

						if( onReportTime(device)) {
							String mailText = "Hola!\n\n"
									+ "La antena de " + device.getDescription() + " volvió a conectarse a las " 
									+ timeFormat.format(device.getLastRecordDate()) + " hrs. \n"
									+ "Gracias por su apoyo en mantener la antena encendida.\n\n" 
									+ " Atte. \n" 
									+ "El equipo de Getin";
							String mailTitle = "La antena de " + device.getDescription() + " volvió a conectarse!!!";

							List<String> reportableUsers = sanitizeMailList(device);
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

							device.setReportStatus(APDevice.REPORT_STATUS_NOT_REPORTED);
							dao.update(device);
							indexHelper.indexObject(device);
						}
					}
				}
			}


		}
	}

	private boolean onReportTime(APDevice device) {
		boolean doReport = false;

		try {
			Date from;
			Date to;
			Date now = sdfTime.parse(sdfTime.format(new Date()));
			Calendar cal = Calendar.getInstance();
			
			cal.setTime(new Date());
			int dof = cal.get(Calendar.DAY_OF_WEEK);
			switch( dof ) {
			case Calendar.SUNDAY:
				if(device.getVisitsOnSun()) {
					from = sdfTime.parse(device.getVisitStartSun());
					from = new Date(from.getTime() + 1800000);
					to = sdfTime.parse(device.getVisitEndSun());
					to = new Date(to.getTime() - 1800000);
					if(to.before(from)) to = new Date(to.getTime() + 86400000);
					if( from.before(now) && to.after(now))
						doReport = true;
				}
				break;
			case Calendar.MONDAY:
				if(device.getVisitsOnMon()) {
					from = sdfTime.parse(device.getVisitStartMon());
					from = new Date(from.getTime() + 1800000);
					to = sdfTime.parse(device.getVisitEndMon());
					to = new Date(to.getTime() - 1800000);
					if(to.before(from)) to = new Date(to.getTime() + 86400000);
					if( from.before(now) && to.after(now))
						doReport = true;
				}
				break;
			case Calendar.TUESDAY:
				if(device.getVisitsOnTue()) {
					from = sdfTime.parse(device.getVisitStartTue());
					from = new Date(from.getTime() + 1800000);
					to = sdfTime.parse(device.getVisitEndTue());
					to = new Date(to.getTime() - 1800000);
					if(to.before(from)) to = new Date(to.getTime() + 86400000);
					if( from.before(now) && to.after(now))
						doReport = true;
				}
				break;
			case Calendar.WEDNESDAY:
				if(device.getVisitsOnWed()) {
					from = sdfTime.parse(device.getVisitStartWed());
					from = new Date(from.getTime() + 1800000);
					to = sdfTime.parse(device.getVisitEndWed());
					to = new Date(to.getTime() - 1800000);
					if(to.before(from)) to = new Date(to.getTime() + 86400000);
					if( from.before(now) && to.after(now))
						doReport = true;
				}
				break;
			case Calendar.THURSDAY:
				if(device.getVisitsOnThu()) {
					from = sdfTime.parse(device.getVisitStartThu());
					from = new Date(from.getTime() + 1800000);
					to = sdfTime.parse(device.getVisitEndThu());
					to = new Date(to.getTime() - 1800000);
					if(to.before(from)) to = new Date(to.getTime() + 86400000);
					if( from.before(now) && to.after(now))
						doReport = true;
				}
				break;
			case Calendar.FRIDAY:
				if(device.getVisitsOnFri()) {
					from = sdfTime.parse(device.getVisitStartFri());
					from = new Date(from.getTime() + 1800000);
					to = sdfTime.parse(device.getVisitEndFri());
					to = new Date(to.getTime() - 1800000);
					if(to.before(from)) to = new Date(to.getTime() + 86400000);
					if( from.before(now) && to.after(now))
						doReport = true;
				}
				break;
			case Calendar.SATURDAY:
				if(device.getVisitsOnSat()) {
					from = sdfTime.parse(device.getVisitStartSat());
					from = new Date(from.getTime() + 1800000);
					to = sdfTime.parse(device.getVisitEndSat());
					to = new Date(to.getTime() - 1800000);
					if(to.before(from)) to = new Date(to.getTime() + 86400000);
					if( from.before(now) && to.after(now))
						doReport = true;
				}
				break;
			}
		} catch( Exception e ) {}
		
		return doReport;
	}
	
	@Override
	public void calculateUptime(Date fromDate, Date toDate, List<String> apdevices) throws ASException {

		Map<String, APUptime> cache = CollectionFactory.createMap();
		DumperHelper<APHEntry> dumpHelper;
		boolean predefinedAPDevicesList = true;
		
		// Populates the apdevices list if empty
		if(CollectionUtils.isEmpty(apdevices)) {
			predefinedAPDevicesList = false;
			apdevices = CollectionFactory.createList();
			List<APDevice> list = dao.getAll(true);
			for( APDevice obj : list ) {
				apdevices.add(obj.getHostname());
			}
		}

		// Prepares the basic records
		log.log(Level.INFO, "Preparing APUptime cache");
		Date date = new Date(fromDate.getTime());
		Date xtoDate = new Date(fromDate.getTime());
		while( date.before(toDate)) {
			for( String hostname : apdevices ) {
				APUptime apu = null;
				apu = new APUptime(hostname, date);
				apu.setKey(apuDao.createKey(hostname, date));
				apuDao.createOrUpdate(apu);

				cache.put(apu.getKey().getName(), apu);
			}
			date = new Date(date.getTime() + 86400000);
		}

		// Gets the input data
		date = new Date(fromDate.getTime());
		while( date.before(toDate)) {
			xtoDate = new Date(date.getTime() + 86400000);
			dumpHelper = new DumpFactory<APHEntry>().build(null, APHEntry.class);
			List<String> hostnames;
			if(!predefinedAPDevicesList) {
				hostnames = dumpHelper.getMultipleNameOptions(date);
				dumpHelper.startPrefetch();
				dumpHelper.iterator(date, xtoDate);
			} else {
				hostnames = apdevices;
			}
			
			for( String hostname : hostnames ) {
				log.log(Level.INFO, "Processing for hostname " + hostname + " for date " + date);
				dumpHelper.setFilter(hostname);
				Iterator<APHEntry> i = dumpHelper.iterator(date, xtoDate);
				while( i.hasNext() ) {
					APHEntry aphe = i.next();
					if( aphe != null ) {
						if( apdevices.contains(aphe.getHostname())) {
							Key apuKey = apuDao.createKey(aphe.getHostname(), date);
							APUptime apu = cache.get(apuKey.getName());
							Iterator<String> slots = aphe.getRssi().keySet().iterator();
							while(slots.hasNext()) {
								try {
									String slot = slots.next();
									Date vDate = aphHelper.slotToDate(aphe.getDate(), Integer.valueOf(slot));
									if(( vDate.after(date) || vDate.equals(date)) && ( vDate.before(xtoDate) || vDate.equals(xtoDate))) {
										String key = APUptime.getRecordKey(vDate);
										if( apu != null && apu.getRecord() != null ) {
											Integer val = apu.getRecord().get(key);
											if( val.equals(0)) {
												apu.getRecord().put(key, 1);
											}
										}
									}
								} catch( Exception e ) {}
							}
						}
					}
				}

			}
			
			date = new Date(date.getTime() + 86400000);
			
			log.log(Level.INFO, "Disposing APHEntry Cache...");
			dumpHelper.dispose();
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

		int index = 0;
		boolean done = false;
		
		while(!done) {

			if( list.size() > index ) {

				// APDevice is assigned
				APDAssignation apda = list.get(index);
				try {
					if( apda.getEntityKind().equals(EntityKind.KIND_SHOPPING)) {
						Shopping shopping = shoppingDao.get(apda.getEntityId());
						apd.setDescription(shopping.getName());
					} else if( apda.getEntityKind().equals(EntityKind.KIND_STORE)) {
						Store store = storeDao.get(apda.getEntityId());
						apd.setDescription(store.getName());
					} else if( apda.getEntityKind().equals(EntityKind.KIND_INNER_ZONE)) {
						InnerZone zone = zoneDao.get(apda.getEntityId());
						apd.setDescription(zone.getName());
					}
					apd.setStatus(StatusAware.STATUS_ENABLED);
					apd.setReportable(true);
					if( apd.getReportMailList() == null )
						apd.setReportMailList(new ArrayList<String>());
					done = true;
				} catch( ASException e ) {
					if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
						apdaDao.delete(apda);
					}
					index++;
					done = false;
				}
			} else {

				// APDevice is no longer assigned
				apd.setDescription(null);
				apd.setReportable(false);
				if( apd.getReportMailList() == null ) {
					apd.setReportMailList(new ArrayList<String>());
				} else {
					apd.getReportMailList().clear();
				}
				done = true;
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
}
