package mobi.allshoppings.cli;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.ExternalAPHotspotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;
import mx.getin.dao.APDCalibrationDAO;
import mx.getin.dao.APDReportDAO;
import mx.getin.model.APDCalibration;
import mx.getin.model.APDReport;

@Deprecated
public class TouchAPDevices extends AbstractCLI {

	private static final Logger log = Logger.getLogger(TouchAPDevices.class.getName());

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
			APDReportDAO apdreportDao = (APDReportDAO)getApplicationContext().getBean("apdreport.dao.ref");
			APDCalibrationDAO apdcalibration = (APDCalibrationDAO)
					getApplicationContext().getBean("apdcalibration.dao.ref");
			APDeviceDAO apdeviceDao = (APDeviceDAO)getApplicationContext().getBean("apdevice.dao.ref");
			ExternalAPHotspotDAO eaphDao = (ExternalAPHotspotDAO)
					getApplicationContext().getBean("externalaphotspot.dao.ref");
			APDeviceHelper apdeviceHelper = (APDeviceHelper)getApplicationContext().getBean("apdevice.helper");
			SystemConfiguration systemConfiguration = (SystemConfiguration)
					getApplicationContext().getBean("system.configuration");

			log.log(Level.INFO, "Touching apdevices....");
			for( APDReport obj : apdreportDao.getAll(true)) {
				log.log(Level.INFO, "Touching " + obj.getIdentifier() + "...");
				boolean modified = false;
				List<String> mails;
				if( obj.getReportMailList() == null ) {
					mails = CollectionFactory.createList();
					obj.setReportMailList(mails);
					modified = true;
				} else {
					mails = obj.getReportMailList();
					int oSize = mails.size();
					mails.removeAll(systemConfiguration.getApdReportMailList());
					if(oSize != mails.size()) {
						obj.setReportMailList(mails);
						modified = true;
					}//if any mail was removed
				} if(modified) apdreportDao.update(obj);
				APDevice dev = apdeviceDao.get(obj.getIdentifier(), true);
				String description = dev.getDescription();
				description = StringUtils.hasText(description) ? description.replaceAll("_", " ") : null;
				if(!dev.getDescription().equals(description)) {
					dev.setDescription(description);
					apdeviceDao.update(dev);
				}//if the description changes
				apdeviceHelper.updateAssignationsUsingAPDevice(dev, obj);
			}//for every device which reports
			
			
			// External Antennas
			List<String> externalHostnames = eaphDao.getExternalHostnames();
			for( String hostname : externalHostnames ) {
				APDevice obj = null;
				APDReport rep = null;
				APDCalibration cal = null;
				try {
					obj = apdeviceDao.get(hostname);
				} catch( Exception e ) {
					obj = new APDevice();
					obj.setHostname(hostname);
					obj.setExternal(true);
					obj.setKey(apdeviceDao.createKey(hostname));
					apdeviceDao.create(obj);
				} try {
					rep = apdreportDao.get(hostname);
				} catch(Exception e) {
					rep = new APDReport();
					rep.setHostname(hostname);
					rep.setKey(apdreportDao.createKey(hostname));
					apdreportDao.create(rep);
				} try {
					cal = apdcalibration.get(hostname);
				} catch(Exception e) {
					cal = new APDCalibration();
					cal.setHostname(hostname);
					cal.setKey(apdcalibration.createKey(hostname));
					apdcalibration.create(cal);
				}

				log.log(Level.INFO, "Touching " + obj.getIdentifier() + "...");

				boolean modifiedCal = cal.getVisitGapThreshold() != 180;
				cal.setVisitGapThreshold(180);
				boolean modifiedRep = rep.getStatus() == null;
				if(modifiedRep)  rep.setStatus(StatusAware.STATUS_ENABLED);
				modifiedRep |= rep.getReportStatus() == null;
				if( null == rep.getReportStatus() )
					rep.setReportStatus(APDevice.REPORT_STATUS_NOT_REPORTED);
				
				if(modifiedCal) apdcalibration.update(cal);
				if(modifiedRep) apdreportDao.update(rep);
				apdeviceHelper.updateAssignationsUsingAPDevice(obj, rep);
			}
			
			for( APDevice obj : apdeviceDao.getAll()) {
				apdeviceDao.getIndexHelper().indexObject(obj);
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
