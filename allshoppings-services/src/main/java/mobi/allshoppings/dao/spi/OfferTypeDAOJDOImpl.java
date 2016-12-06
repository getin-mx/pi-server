package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import mobi.allshoppings.dao.OfferTypeDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.OfferType;

import com.inodes.datanucleus.model.Key;

public class OfferTypeDAOJDOImpl extends GenericDAOJDO<OfferType> implements OfferTypeDAO {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(OfferTypeDAOJDOImpl.class.getName());

	public OfferTypeDAOJDOImpl() {
		super(OfferType.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createNumericUniqueKey(OfferType.class);
	}

}
