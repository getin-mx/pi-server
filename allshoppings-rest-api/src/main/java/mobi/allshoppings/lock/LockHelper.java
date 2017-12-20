package mobi.allshoppings.lock;

import java.util.Date;

import mobi.allshoppings.exception.ASException;

public interface LockHelper {

	void deviceMessageLock(String deviceId, byte scope, String campaignActivityId, Date fromDate,
			long duration, String subEntityId, int subEntityKind) throws ASException;
	void clearLocks(String deviceId, byte scope, String campaignActivityId) throws ASException;
	
}
