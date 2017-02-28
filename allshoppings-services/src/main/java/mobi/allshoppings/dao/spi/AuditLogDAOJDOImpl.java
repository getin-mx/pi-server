package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.AuditLogDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.AuditLog;

public class AuditLogDAOJDOImpl extends GenericDAOJDO<AuditLog> implements AuditLogDAO {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(AuditLogDAOJDOImpl.class.getName());

	public AuditLogDAOJDOImpl() {
		super(AuditLog.class);
	}

	/**
	 * Creates a new unique key for the area, using a random number based in
	 * this current unix time as seed.
	 */
	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(AuditLog.class);
	}
}
