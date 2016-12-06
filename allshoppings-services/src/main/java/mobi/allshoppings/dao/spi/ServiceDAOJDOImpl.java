package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import mobi.allshoppings.dao.ServiceDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Service;

import com.inodes.datanucleus.model.Key;

public class ServiceDAOJDOImpl extends GenericDAOJDO<Service> implements ServiceDAO {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ServiceDAOJDOImpl.class.getName());

	public ServiceDAOJDOImpl() {
		super(Service.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createNumericUniqueKey(Service.class);
	}

}
