package mobi.allshoppings.lock;

import java.util.Date;

import mobi.allshoppings.exception.ASException;

public interface LockHelper {

	void deviceMessageLock(String deviceId, Integer scope, String campaignActivityId, Date fromDate, long duration, String subEntityId, Integer subEntityKind) throws ASException;
	void clearLocks(String deviceId, Integer scope, String campaignActivityId) throws ASException;
	
}
