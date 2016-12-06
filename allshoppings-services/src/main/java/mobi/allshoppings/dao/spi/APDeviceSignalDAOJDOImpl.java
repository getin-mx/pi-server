package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.APDeviceSignalDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDeviceSignal;

public class APDeviceSignalDAOJDOImpl extends GenericDAOJDO<APDeviceSignal> implements APDeviceSignalDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(APDeviceSignalDAOJDOImpl.class.getName());

	public APDeviceSignalDAOJDOImpl() {
		super(APDeviceSignal.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(APDeviceSignal.class);
	}

}
