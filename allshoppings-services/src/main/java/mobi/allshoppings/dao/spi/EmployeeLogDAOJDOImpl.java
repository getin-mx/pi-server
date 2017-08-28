package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.EmployeeLogDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.EmployeeLog;

public class EmployeeLogDAOJDOImpl extends GenericDAOJDO<EmployeeLog> implements EmployeeLogDAO {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(EmployeeLogDAOJDOImpl.class.getName());
	
	public EmployeeLogDAOJDOImpl() {
		super(EmployeeLog.class);
	}

	@Override
	public Key createKey(EmployeeLog obj) throws ASException {
		return keyHelper.createStringUniqueKey(EmployeeLog.class);
	}

}
