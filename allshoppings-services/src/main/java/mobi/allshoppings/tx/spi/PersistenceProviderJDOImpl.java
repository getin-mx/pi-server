package mobi.allshoppings.tx.spi;

import javax.jdo.PersistenceManager;

import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.tx.PersistenceProvider;
import mobi.allshoppings.tx.TransactionType;


public class PersistenceProviderJDOImpl implements PersistenceProvider {
	private	 PersistenceManager pm;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get() {
		return (T)this.pm;
	}

	public PersistenceProviderJDOImpl(TransactionType type) {
		if (type == TransactionType.CROSS) { 
			this.pm = DAOJDOPersistentManagerFactory.getGX().getPersistenceManager();
		} else {
			this.pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		}
	}
}
