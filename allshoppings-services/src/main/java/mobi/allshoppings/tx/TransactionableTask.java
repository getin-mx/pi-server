package mobi.allshoppings.tx;

import mobi.allshoppings.exception.ASException;

public interface TransactionableTask {
	int retriesCount();
	TransactionType transactionType();
	
	void run(PersistenceProvider pp) throws ASException;
}
