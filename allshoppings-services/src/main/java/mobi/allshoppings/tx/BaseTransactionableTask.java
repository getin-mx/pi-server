package mobi.allshoppings.tx;


public abstract class BaseTransactionableTask implements TransactionableTask {
	private TransactionType transactionType;
	private int retriesCount;
	
	public BaseTransactionableTask() {
		this(5, TransactionType.SIMPLE);
	}
	
	public BaseTransactionableTask(int retriescount) {
		this(retriescount, TransactionType.SIMPLE);
	}
	
	public BaseTransactionableTask(int retriescount, TransactionType transactionType) {
		super();
		this.retriesCount = retriescount;
		this.transactionType = transactionType;
	}
	
	@Override
	public int retriesCount() {
		return this.retriesCount;
	}

	@Override
	public TransactionType transactionType() {
		return this.transactionType;
	}

}
