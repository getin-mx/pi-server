package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDevice;

import com.inodes.datanucleus.model.Key;

public class APDeviceDAOJDOImpl extends GenericDAOJDO<APDevice> implements APDeviceDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(APDeviceDAOJDOImpl.class.getName());

	public APDeviceDAOJDOImpl() {
		super(APDevice.class);
	}

	@Override
	public Key createKey(String identifier) throws ASException {
		return keyHelper.obtainKey(APDevice.class, identifier);
	}

}
