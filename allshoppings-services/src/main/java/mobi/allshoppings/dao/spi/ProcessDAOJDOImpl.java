package mobi.allshoppings.dao.spi;

import java.util.logging.Logger;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.ProcessDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Process;

public class ProcessDAOJDOImpl extends GenericDAOJDO<Process> implements ProcessDAO {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ProcessDAOJDOImpl.class.getName());

	public ProcessDAOJDOImpl() {
		super(Process.class);
	}

	/**
	 * Creates a new unique key for the area, using a random number based in
	 * this current unix time as seed.
	 */
	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(Process.class);
	}
}
