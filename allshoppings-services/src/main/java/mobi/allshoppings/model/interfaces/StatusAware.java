package mobi.allshoppings.model.interfaces;

public interface StatusAware {

	public static final byte STATUS_ENABLED = 0;
	public static final byte STATUS_DISABLED = 1;
	public static final byte STATUS_PENDING = 2;

	public static final byte STATUS_NEW = 3;
	public static final byte STATUS_VIEWED = 4;
	public static final byte STATUS_REMOVED = 5;

	public static final byte STATUS_PREPARED = 10;
	public static final byte STATUS_RUNNING = 11;
	public static final byte STATUS_SUCCEEDED = 12;
	public static final byte STATUS_ERROR = 13;
	public static final byte STATUS_CANCELLED = 14;

	public byte getStatus();
	public void setStatus(byte status);
	
}
