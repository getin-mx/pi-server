package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.AreaDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Area;

public class AreaDAOJDOImpl extends GenericDAOJDO<Area> implements AreaDAO {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(AreaDAOJDOImpl.class.getName());

	public AreaDAOJDOImpl() {
		super(Area.class);
	}

	/**
	 * Creates a new unique key for the area, using a random number based in
	 * this current unix time as seed.
	 */
	@Override
	public Key createKey() throws ASException {
		return keyHelper.createNumericUniqueKey(Area.class);
	}
}
