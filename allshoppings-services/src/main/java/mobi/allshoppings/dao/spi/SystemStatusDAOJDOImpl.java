package mobi.allshoppings.dao.spi;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.SystemStatusDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.SystemStatus;

public class SystemStatusDAOJDOImpl extends GenericDAOJDO<SystemStatus> implements SystemStatusDAO {

	public SystemStatusDAOJDOImpl() {
		super(SystemStatus.class);
	}

	@Override
	public Key createKey(String identifier) throws ASException {
		return keyHelper.obtainKey(SystemStatus.class, identifier);
	}

}
