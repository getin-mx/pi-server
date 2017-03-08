package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.ExportUnitDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.ExportUnit;

public class ExportUnitDAOJDOImpl extends GenericDAOJDO<ExportUnit> implements ExportUnitDAO {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ExportUnitDAOJDOImpl.class.getName());

	public ExportUnitDAOJDOImpl() {
		super(ExportUnit.class);
	}

	/**
	 * Creates a new unique key for the area, using a random number based in
	 * this current unix time as seed.
	 */
	@Override
	public Key createKey() throws ASException {
		return keyHelper.createNumericUniqueKey(ExportUnit.class);
	}
}
