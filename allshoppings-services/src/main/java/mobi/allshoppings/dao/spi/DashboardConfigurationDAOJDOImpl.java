package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.DashboardConfigurationDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DashboardConfiguration;

public class DashboardConfigurationDAOJDOImpl extends GenericDAOJDO<DashboardConfiguration> implements DashboardConfigurationDAO {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DashboardConfigurationDAOJDOImpl.class.getName());

	public DashboardConfigurationDAOJDOImpl() {
		super(DashboardConfiguration.class);
	}

	@Override
	public Key createKey(DashboardConfiguration seed) throws ASException {
		return (Key) keyHelper.obtainKey(DashboardConfiguration.class,
				getIdentifierUsingEntityIdAndEntityKind(seed.getEntityId(), seed.getEntityKind()));
	}

	@Override
	public String getIdentifierUsingEntityIdAndEntityKind(String entityId, Integer entityKind) throws ASException {
		return entityId + ":" + entityKind;
	}

	@Override
	public DashboardConfiguration getUsingEntityIdAndEntityKind(String entityId, Integer entityKind, boolean detachable) throws ASException {
		String identifier = getIdentifierUsingEntityIdAndEntityKind(entityId, entityKind);
		return get(identifier, detachable);
	}
}
