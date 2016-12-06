package mobi.allshoppings.push.test;

import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.push.PushMessageHelper;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"../../test/testApplicationContext.xml"})
public class PushHelperTester extends AbstractJUnit4SpringContextTests {

	@Autowired
	PushMessageHelper pushHelper;
	@Autowired
	DeviceInfoDAO deviceInfoDao;
	
	
	@Test
	public void test0001() throws Exception {
		try {

			DeviceInfo device = deviceInfoDao.get("d9ae567caf438b98@cinepolis_mx");
			
			pushHelper.sendMessage("Hola Mundo", "Este es un mensaje", "http://www.google.com", device);
			
		} catch( Throwable t ) {
			t.printStackTrace();
			throw t;
		}
	}

	@Test
	public void test0002() throws Exception {
		try {

			DeviceInfo device = deviceInfoDao.get("d9ae567caf438b98@cinepolis_mx");
			
			pushHelper.requestLocation(device);
			
		} catch( Throwable t ) {
			t.printStackTrace();
			throw t;
		}
	}

	@Test
	public void test0003() throws Exception {
		try {

			DeviceInfo device = deviceInfoDao.get("E54A6221-BD51-4CFD-8A38-DD61CC3BEE90");
			
			pushHelper.sendMessage("Hola Mundo", "Este es un mensaje", "http://www.google.com", device);
			
		} catch( Throwable t ) {
			t.printStackTrace();
			throw t;
		}
	}

	@Test
	public void test0004() throws Exception {
		try {

			DeviceInfo device = deviceInfoDao.get("E54A6221-BD51-4CFD-8A38-DD61CC3BEE90");
			
			pushHelper.requestLocation(device);
			
		} catch( Throwable t ) {
			t.printStackTrace();
			throw t;
		}
	}

}
