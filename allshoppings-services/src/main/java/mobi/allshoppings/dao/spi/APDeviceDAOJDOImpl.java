package mobi.allshoppings.dao.spi;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDevice;

public class APDeviceDAOJDOImpl extends GenericDAOJDO<APDevice> implements APDeviceDAO {
	
	public APDeviceDAOJDOImpl() {
		super(APDevice.class);
	}

	@Override
	public Key createKey(String identifier) throws ASException {
		return keyHelper.obtainKey(APDevice.class, identifier);
	}

}
