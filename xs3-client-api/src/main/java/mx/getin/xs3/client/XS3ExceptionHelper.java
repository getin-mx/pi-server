package mx.getin.xs3.client;

import java.util.List;

import mx.getin.xs3.client.model.XS3Exception;

public class XS3ExceptionHelper {
	public static int AS_EXCEPTION_FORBIDDEN_CODE = 403;
	public static int AS_EXCEPTION_NOTFOUND_CODE = 404;
	public static int AS_EXCEPTION_ALREADYEXISTS_CODE = 405;
	public static int AS_EXCEPTION_NOTACCEPTED_CODE = 406;
	public static int AS_EXCEPTION_INVALIDARGUMENTS_CODE = 407;
	public static int AS_EXCEPTION_AUTHTOKENEXPIRED_CODE = 408;
	public static int AS_EXCEPTION_AUTHTOKENMISSING_CODE = 409;
	public static int AS_EXCEPTION_CONCURRENT_MODIFICATION_CODE = 410;
	public static int AS_EXCEPTION_MAILALREADYEXISTS_CODE = 411;
	public static int AS_EXCEPTION_NOT_UNIQUE_CODE = 412;
	public static int AS_EXCEPTION_PUSH_MESSAGE = 413;
	public static int AS_EXCEPTION_INTERNALERROR_CODE = 500;
	
	public static XS3Exception forbiddenException() {
		return new XS3Exception("Forbidden", AS_EXCEPTION_FORBIDDEN_CODE);
	}
	public static XS3Exception notFoundException() {
		return new XS3Exception("Not found", AS_EXCEPTION_NOTFOUND_CODE);
	}
	public static XS3Exception notFoundException(String data) {
		return new XS3Exception("Entity Id " + data + " Not found", AS_EXCEPTION_NOTFOUND_CODE);
	}
	public static XS3Exception alreadyExistsException() {
		return new XS3Exception("Entity already exists", AS_EXCEPTION_ALREADYEXISTS_CODE);
	}
	public static XS3Exception notAcceptedException() {
		return new XS3Exception("Not accepted", AS_EXCEPTION_NOTACCEPTED_CODE);
	}
	public static XS3Exception invalidArgumentsException(List<String> invalidFields) {
		return XS3ExceptionHelper.invalidArgumentsException(invalidFields.toString());
	}
	public static XS3Exception invalidArgumentsException(String invalidField) {
		StringBuffer sb = new StringBuffer("Invalid Arguments on fields:");
		sb.append(invalidField.toString());	
		return new XS3Exception(sb.toString(), AS_EXCEPTION_INVALIDARGUMENTS_CODE);
	}
	public static XS3Exception invalidArgumentsException() {
		return new XS3Exception("Invalid Arguments", AS_EXCEPTION_INVALIDARGUMENTS_CODE);
	}
	public static XS3Exception tokenExpiredException() {
		return new XS3Exception("Auth token expired", AS_EXCEPTION_AUTHTOKENEXPIRED_CODE);
	}
	public static XS3Exception authTokenMissingException() {
		return new XS3Exception("Auth token missing", AS_EXCEPTION_AUTHTOKENMISSING_CODE);
	}
	public static XS3Exception defaultException(String message, Throwable cause) {
		return new XS3Exception(message, AS_EXCEPTION_INTERNALERROR_CODE, cause);
	}
	public static XS3Exception concurrentModificationException() {
		return new XS3Exception("Concurrent error", AS_EXCEPTION_CONCURRENT_MODIFICATION_CODE);
	}
	public static XS3Exception mailAlreadyExistsException() {
		return new XS3Exception("Mail already exists", AS_EXCEPTION_MAILALREADYEXISTS_CODE);
	}
	public static XS3Exception notUniqueException() {
		return new XS3Exception("Not Unique value found", AS_EXCEPTION_NOT_UNIQUE_CODE);
	}
	public static XS3Exception pushMessageException(String userId, String title) {
		return new XS3Exception("Push Message not delivered to user " + userId  
			+ " and title " + title, AS_EXCEPTION_PUSH_MESSAGE);
	}
}
