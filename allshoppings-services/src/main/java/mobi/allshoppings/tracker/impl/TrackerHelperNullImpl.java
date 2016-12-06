package mobi.allshoppings.tracker.impl;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tracker.TrackerHelper;

public class TrackerHelperNullImpl implements TrackerHelper {

	@Override
	public void enqueue(User user, String ipAddress, String userAgent,
			String url, String title, String query, String goalId)
			throws ASException {
	}

	@Override
	public void enqueue(String userId, String ipAddress, String userAgent,
			String url, String title, String query, String goalId)
			throws ASException {
	}

	@Override
	public void trackService(User user, String ipAddress, String userAgent,
			String url, String title, String query, String goalId)
			throws ASException {
	}

}
