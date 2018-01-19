package mobi.allshoppings.bdb.bz.spi;

import java.util.logging.Level;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.apdevice.APDeviceHelper;
import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.StatusAware;
import mx.getin.dao.APDReportDAO;
import mx.getin.model.APDReport;

public class BDBAPDeviceBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<APDReport> implements BDBCrudBzService {

	@Autowired
	private APDReportDAO dao;

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
	public void postChange(APDReport obj) throws ASException {
		try {
			if( obj.getStatus() == StatusAware.STATUS_DISABLED)
				apdeviceHelper.unassignUsingAPDevice(obj.getHostname());
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public void postDelete(APDReport obj) throws ASException {
		try {
			apdeviceHelper.unassignUsingAPDevice(obj.getHostname());
		} catch( Exception e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(APDReport.class);
	}

	@Override
	public void setKey(APDReport obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey(seed.getString("hostname")));
	}

}
