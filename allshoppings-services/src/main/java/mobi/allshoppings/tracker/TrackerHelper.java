package mobi.allshoppings.tracker;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.User;

public interface TrackerHelper {

	void enqueue(User user, String ipAddress, String userAgent, String url, String title, String query, String goalId) throws ASException;
	void enqueue(String userId, String ipAddress, String userAgent, String url, String title, String query, String goalId) throws ASException;
	void trackService(User user, String ipAddress, String userAgent, String url, String title, String query, String goalId) throws ASException;

}
