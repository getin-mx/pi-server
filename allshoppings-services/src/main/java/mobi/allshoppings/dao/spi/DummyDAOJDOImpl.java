package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import mobi.allshoppings.dao.DummyDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Dummy;

import com.inodes.datanucleus.model.Key;

public class DummyDAOJDOImpl extends GenericDAOJDO<Dummy> implements DummyDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DummyDAOJDOImpl.class.getName());

	public DummyDAOJDOImpl() {
		super(Dummy.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(Dummy.class);
	}

}
