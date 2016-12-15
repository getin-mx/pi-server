package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.AddressComponentsCacheDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.AddressComponentsCache;

public class AddressComponentsCacheDAOJDOImpl extends GenericDAOJDO<AddressComponentsCache> implements AddressComponentsCacheDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(AddressComponentsCacheDAOJDOImpl.class.getName());
	
	public AddressComponentsCacheDAOJDOImpl() {
		super(AddressComponentsCache.class);
	}

	@Override
	public Key createKey(String geohash) throws ASException {
		return keyHelper.obtainKey(AddressComponentsCache.class, geohash);
	}

}
