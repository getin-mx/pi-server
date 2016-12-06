package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import mobi.allshoppings.dao.DashboardIndicatorAliasDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.DashboardIndicatorAlias;

import com.inodes.datanucleus.model.Key;

public class DashboardIndicatorAliasDAOJDOImpl extends GenericDAOJDO<DashboardIndicatorAlias> implements DashboardIndicatorAliasDAO {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DashboardIndicatorAliasDAOJDOImpl.class.getName());

	public DashboardIndicatorAliasDAOJDOImpl() {
		super(DashboardIndicatorAlias.class);
	}

	@Override
	public Key createKey(DashboardIndicatorAlias obj) throws ASException {
		return keyHelper.obtainKey(DashboardIndicatorAlias.class, String.valueOf(obj.hashCode()));
	}

	@Override
	public DashboardIndicatorAlias getUsingFilters(String entityId, Integer entityKind, String elementId, String elementSubId) throws ASException {
		DashboardIndicatorAlias alias = new DashboardIndicatorAlias(entityId, entityKind, elementId, elementSubId);
		alias.setKey(createKey(alias));
		return get(alias.getIdentifier(), true);
	}
}
