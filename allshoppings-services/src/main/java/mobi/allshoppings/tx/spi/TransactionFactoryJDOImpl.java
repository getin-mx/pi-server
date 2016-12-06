package mobi.allshoppings.tx.spi;

import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exception.ExceptionHelper;
import mobi.allshoppings.tx.PersistenceProvider;
import mobi.allshoppings.tx.TransactionFactory;
import mobi.allshoppings.tx.TransactionType;
import mobi.allshoppings.tx.TransactionableTask;

public class TransactionFactoryJDOImpl implements TransactionFactory, Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(TransactionFactoryJDOImpl.class.getName());
	
	public TransactionFactoryJDOImpl() {
	}
	
	@Override
	public void createWithTransactionableTask(TransactionableTask task) throws ASException {
		int localRetries = task.retriesCount();
		TransactionType type = task.transactionType();
		PersistenceManager pm = null;
		PersistenceProvider pp = null;
		
		while (true) {
			try {
				pp = new PersistenceProviderJDOImpl(type);
				pm = pp.get();
				pm.currentTransaction().begin();

				task.run(pp);

				pm.currentTransaction().commit();
				break;
			
			} catch (Exception e) {
				if (ExceptionHelper.containCausalChain(e, ConcurrentModificationException.class) == true || e instanceof ASException) {
			        log.log(Level.SEVERE, "Retrying after error...", e);
			        // Allow retry to occur
			        --localRetries;
				} else {
					// rethrow error
					throw ASExceptionHelper.defaultException(e.getMessage(), e);
				}
		        if (localRetries < 0) {
					throw ASExceptionHelper.concurrentModificationException();
		        } else {
		        	try { Thread.sleep(500); } catch(InterruptedException ie ) {}
		        }
			} finally {
				if (pm.currentTransaction().isActive()) {
					pm.currentTransaction().rollback();
				}
				pm.close();
			}
		}
	}
}
