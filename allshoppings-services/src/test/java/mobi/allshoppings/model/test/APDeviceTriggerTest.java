package mobi.allshoppings.model.test;

import java.util.List;

import org.json.JSONObject;
import org.junit.Test;

import junit.framework.TestCase;
import mobi.allshoppings.dao.APDeviceTriggerEntryDAO;
import mobi.allshoppings.dao.spi.APDeviceTriggerEntryDAOJDOImpl;
import mobi.allshoppings.model.APDeviceTriggerEntry;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.impl.KeyHelperGaeImpl;

public class APDeviceTriggerTest extends TestCase {

	APDeviceTriggerEntryDAO triggerDao = new APDeviceTriggerEntryDAOJDOImpl();
	KeyHelper keyHelper = new KeyHelperGaeImpl();

	@Test
	public void test0001() {

		try {
			List<APDeviceTriggerEntry> list = triggerDao.getUsingCoincidence("ashs-9005", "28:27:bf:d2:d9:c8");
			for( APDeviceTriggerEntry entry : list ) {
				System.out.println(entry);
				triggerDao.delete(entry.getIdentifier());
			}
			APDeviceTriggerEntry obj = new APDeviceTriggerEntry();
			obj.setDeviceUUID(null);
			obj.setHostname("ashs-9005");
			obj.setMac("28:27:bf:d2:d9:c8");
			obj.setRssi(-70);
			obj.setTriggerClassName("mobi.allshoppings.apdevice.impl.APDeviceTriggerStartVisitImpl");
			obj.setTriggerName("Start Visit Trigger");
			JSONObject metadata = new JSONObject();
			metadata.put("message", "Bienvenido a Casa Mat!!");
			metadata.put("timeLimit", 600000);
			obj.setTriggerMetadata(metadata.toString());
			obj.setKey(triggerDao.createKey());
			triggerDao.create(obj);
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void test0002() {

		try {
			List<APDeviceTriggerEntry> list = triggerDao.getUsingCoincidence("ashs-9003", "28:27:bf:d2:d9:c8");
			for( APDeviceTriggerEntry entry : list ) {
				System.out.println(entry);
				triggerDao.delete(entry.getIdentifier());
			}
			APDeviceTriggerEntry obj = new APDeviceTriggerEntry();
			obj.setDeviceUUID(null);
			obj.setHostname("ashs-9003");
			obj.setMac("28:27:bf:d2:d9:c8");
			obj.setRssi(-70);
			obj.setTriggerClassName("mobi.allshoppings.apdevice.impl.APDeviceTriggerStartVisitImpl");
			obj.setTriggerName("Start Visit Trigger");
			JSONObject metadata = new JSONObject();
			metadata.put("message", "Bienvenido a GetIn Mat!!");
			metadata.put("timeLimit", 600000);
			obj.setTriggerMetadata(metadata.toString());
			obj.setKey(triggerDao.createKey());
			triggerDao.create(obj);
		} catch( Throwable e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
