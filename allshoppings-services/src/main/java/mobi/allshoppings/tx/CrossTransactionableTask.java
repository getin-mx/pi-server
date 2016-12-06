package mobi.allshoppings.tx;


public abstract class CrossTransactionableTask implements TransactionableTask {
	private TransactionType transactionType;
	private int retriesCount;
	
	public CrossTransactionableTask() {
		this(5, TransactionType.CROSS);
	}
	
	public CrossTransactionableTask(int retriescount) {
		this(retriescount, TransactionType.CROSS);
	}
	
	public CrossTransactionableTask(int retriescount, TransactionType transactionType) {
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
