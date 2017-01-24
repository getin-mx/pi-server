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
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


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
			APDeviceDAO apdeviceDao = (APDeviceDAO)getApplicationContext().getBean("apdevice.dao.ref");
			ExternalAPHotspotDAO eaphDao = (ExternalAPHotspotDAO)getApplicationContext().getBean("externalaphotspot.dao.ref");
			APDeviceHelper apdeviceHelper = (APDeviceHelper)getApplicationContext().getBean("apdevice.helper");

			log.log(Level.INFO, "Touching apdevices....");
			List<APDevice> list = apdeviceDao.getAll(true);
			for( APDevice obj : list ) {
				log.log(Level.INFO, "Touching " + obj.getIdentifier() + "...");
				if(StringUtils.hasText(obj.getDescription()))
					obj.setDescription(obj.getDescription().replaceAll("_", " "));
				else
					obj.setDescription(null);
				
				if( null == obj.getStatus() ) 
					obj.setStatus(StatusAware.STATUS_ENABLED);
				
				if( null == obj.getReportStatus() )
					obj.setReportStatus(APDevice.REPORT_STATUS_NOT_REPORTED);
				
				apdeviceDao.update(obj);
				apdeviceHelper.updateAssignationsUsingAPDevice(obj.getHostname());
			}
			
			
			// External Antennas
			List<String> externalHostnames = eaphDao.getExternalHostnames();
			for( String hostname : externalHostnames ) {
				APDevice obj = null;
				try {
					obj = apdeviceDao.get(hostname);
				} catch( Exception e ) {
					obj = new APDevice();
					obj.setHostname(hostname);
					obj.setExternal(true);
					obj.setKey(apdeviceDao.createKey(hostname));
					apdeviceDao.create(obj);
				}

				log.log(Level.INFO, "Touching " + obj.getIdentifier() + "...");

				if( null == obj.getStatus() ) 
					obj.setStatus(StatusAware.STATUS_ENABLED);

				if( null == obj.getReportStatus() )
					obj.setReportStatus(APDevice.REPORT_STATUS_NOT_REPORTED);
				
				apdeviceDao.update(obj);
				apdeviceHelper.updateAssignationsUsingAPDevice(obj.getHostname());

			}
			
			list = apdeviceDao.getAll();
			List<ModelKey> index = CollectionFactory.createList();
			for( APDevice obj : list ) {
				index.add(obj);
			}
			apdeviceDao.getIndexHelper().indexObject(index);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
