package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import mobi.allshoppings.dao.PushMessageLogDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.PushMessageLog;

import com.inodes.datanucleus.model.Key;

public class PushMessageLogDAOJDOImpl extends GenericDAOJDO<PushMessageLog> implements PushMessageLogDAO {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(PushMessageLogDAOJDOImpl.class.getName());

	public PushMessageLogDAOJDOImpl() {
		super(PushMessageLog.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(PushMessageLog.class);
	}

}
