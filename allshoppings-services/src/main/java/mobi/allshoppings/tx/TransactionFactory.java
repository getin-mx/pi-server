package mobi.allshoppings.tx;

import mobi.allshoppings.exception.ASException;

public interface TransactionFactory {

	// An ACID tx, with retries, commit/rollback, and persistor stuff
	void createWithTransactionableTask(TransactionableTask task) throws ASException;

}
