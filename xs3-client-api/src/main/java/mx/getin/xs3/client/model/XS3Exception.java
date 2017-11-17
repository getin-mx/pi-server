package mx.getin.xs3.client.model;

public class XS3Exception extends Exception {

	private static final long serialVersionUID = 1L;
	private String errorMessage;
	private int errorCode;
	
	public XS3Exception() {
		super();
	}

	public XS3Exception(String errorMessage, int errorCode) {
		super(errorMessage);
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
	}

	public XS3Exception(String errorMessage, int errorCode, Throwable cause) {
		super(errorMessage, cause);
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return this.errorCode;
	}
}
