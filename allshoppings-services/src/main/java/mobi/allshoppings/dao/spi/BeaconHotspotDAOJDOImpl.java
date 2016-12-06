package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import mobi.allshoppings.dao.BeaconHotspotDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.BeaconHotspot;

import com.inodes.datanucleus.model.Key;

public class BeaconHotspotDAOJDOImpl extends GenericDAOJDO<BeaconHotspot> implements BeaconHotspotDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(BeaconHotspotDAOJDOImpl.class.getName());

	public BeaconHotspotDAOJDOImpl() {
		super(BeaconHotspot.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(BeaconHotspot.class);
	}

}
