package mobi.allshoppings.bdb.bz.spi;

import java.util.logging.Level;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.interfaces.StatusAware;

public class BDBAPDeviceBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<APDevice> implements BDBCrudBzService {

	@Autowired
	private APDeviceDAO dao;

	@Autowired
	private APDeviceHelper apdeviceHelper;

	@Override
	public String[] getMandatoryUpdateFields() {
		return new String[] {
				"identifier",
				"hostname"
		};
	}

	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"hostname",
				"description",
				"lastRecordDate",
				"reportable",
				"reportStatus",
				"model",
				"mode",
				"version",
				"status",
				"reportable",
				"reportStatus"
		};
	}
	
	@Override
	public void postChange(APDevice obj) throws ASException {
		try {
			if( obj.getStatus().equals(StatusAware.STATUS_DISABLED))
				apdeviceHelper.unassignUsingAPDevice(obj.getHostname());
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public void postDelete(APDevice obj) throws ASException {
		try {
			apdeviceHelper.unassignUsingAPDevice(obj.getHostname());
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(APDevice.class);
	}

	@Override
	public void setKey(APDevice obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey(seed.getString("hostname")));
	}

}
