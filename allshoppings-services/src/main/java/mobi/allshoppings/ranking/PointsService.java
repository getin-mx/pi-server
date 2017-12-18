package mobi.allshoppings.ranking;

import java.util.Date;

import mobi.allshoppings.exception.ASException;

public interface PointsService {

	public static final int ACTION_COMMENT = 0;
	public static final int ACTION_SHARE = 1;
	public static final int ACTION_FAVORITE = 2;
	public static final int ACTION_UNFAVORITE = 3;
	public static final int ACTION_INVITE_ALL = 4;
	public static final int ACTION_CHECKIN = 5;
	public static final int ACTION_INVITE = 6;
	public static final int ACTION_RATE = 7;
	public static final int ACTION_COUPON_ACCEPTED = 8;
	public static final int ACTION_COUPON_REDEEMED = 9;
	public static final int ACTION_COUPON_REJECTED = 10;

	void enqueue(final String userId, final Integer action, final byte targetKind,
			final String targetId, final String data) throws ASException;
	long calculatePointsForAction(final String userId, final Integer action,
			final byte targetKind, final String targetId, String data);
	long assignPointsForAction(final String userId, final Integer action, final byte targetKind,
			final String targetId, String data) throws ASException;
	long getTotalPointsForAction(final String userId, final Integer action) throws ASException;
	long recountPointsForUser(final String userId, final Date fromDate, final Date toDate) throws ASException; 
	
}
