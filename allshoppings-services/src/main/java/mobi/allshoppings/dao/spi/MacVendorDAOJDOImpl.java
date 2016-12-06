package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.MacVendorDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.MacVendor;

public class MacVendorDAOJDOImpl extends GenericDAOJDO<MacVendor> implements MacVendorDAO {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(MacVendorDAOJDOImpl.class.getName());

	public MacVendorDAOJDOImpl() {
		super(MacVendor.class);
	}

	@Override
	public Key createKey(String seed) throws ASException {
		return (Key)keyHelper.obtainKey(MacVendor.class, seed);
	}

}
