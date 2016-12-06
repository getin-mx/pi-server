package mobi.allshoppings.ranking.impl;

import java.util.Date;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.ranking.PointsService;

public class PointsServiceNullImpl implements PointsService {

	@Override
	public void enqueue(final String userId, final Integer action, final Integer targetKind, final String targetId, final String data) throws ASException {
		// TODO: Implement me
	}

	@Override
	public long calculatePointsForAction(final String userId, final Integer action, final Integer targetKind, final String targetId, final String data) {
		if( action == null ) return 0L;
		if( action == ACTION_FAVORITE ) return 5L;
		if( action == ACTION_UNFAVORITE ) return -5L;
		if( action == ACTION_INVITE_ALL ) return 1000L;
		if( action == ACTION_SHARE ) return 10L;
		if( action == ACTION_CHECKIN ) return 50L;
		if( action == ACTION_INVITE ) return 30L;
		if( action == ACTION_RATE ) return 10L;
		if( action == ACTION_COUPON_ACCEPTED ) return 5L;
		if( action == ACTION_COUPON_REDEEMED ) return 5L;
		if( action == ACTION_COUPON_REJECTED ) return 0L;
		return 10L;
	}
	
	@Override
	public long assignPointsForAction(final String userId, final Integer action, final Integer targetKind, final String targetId, final String data) throws ASException {
		// TODO: Implement me
		return calculatePointsForAction(userId, action, targetKind, targetId, data);
	}
	
	@Override
	public long recountPointsForUser(String userId, Date fromDate, Date toDate) throws ASException {
		// TODO: Implement me
		return 0L;
	}

	@Override
	public long getTotalPointsForAction(String userId, Integer action) throws ASException {
		// TODO: Implement me
		return 0L;
	}
}
