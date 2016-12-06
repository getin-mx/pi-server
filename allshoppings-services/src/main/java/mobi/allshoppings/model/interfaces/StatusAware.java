package mobi.allshoppings.model.interfaces;

public interface StatusAware {

	public static final int STATUS_ENABLED = 0;
	public static final int STATUS_DISABLED = 1;
	public static final int STATUS_PENDING = 2;

	public static final int STATUS_NEW = 3;
	public static final int STATUS_VIEWED = 4;
	public static final int STATUS_REMOVED = 5;

	public static final int STATUS_PREPARED = 10;
	public static final int STATUS_RUNNING = 11;
	public static final int STATUS_SUCCEEDED = 12;
	public static final int STATUS_ERROR = 13;
	public static final int STATUS_CANCELLED = 14;

	public Integer getStatus();
	public void setStatus(Integer status);
	
}
