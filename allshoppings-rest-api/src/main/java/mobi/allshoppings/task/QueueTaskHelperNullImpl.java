package mobi.allshoppings.task;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.Range;

public class QueueTaskHelperNullImpl implements QueueTaskHelper {

	@Override
	public void enqueueTransientInReplica(ModelKey obj) throws ASException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueInReplica(ModelKey obj) throws ASException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueInReplica(String className, JSONObject payload)
			throws JSONException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueue(String queueName, String url, String taskName,
			Map<String, String> parameters) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reenqueue(HttpServletRequest req, Map<String, String> parameters) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reenqueueWithRange(HttpServletRequest req,
			Map<String, String> parameters, Range range, List<String> ids) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getQueueName(HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTaskName(HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTaskUrl(HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enqueueUnindex(String clazzName, String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueUnindex(String clazzName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueIndex(String clazzName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueIndex(String clazzName, String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueIndex(String clazzName, Range range, String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueIndex(String clazzName, Range range, String id,
			String method) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueUngeo(String clazzName, String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueUngeo(String clazzName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueGeo(String clazzName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueGeo(String clazzName, String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueGeo(String clazzName, Range range, String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueGeo(String clazzName, Range range, String id,
			String method) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueNapalm(String clazzName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueDeleteOrphanDevices() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueDeleteOrphanLocations() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueDeleteEvictedUEC() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueNotifyOffers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueNotifyOffers(String userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueRebuildUEC() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueMoveExpiredOffers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueNotificationLogReceived(String userId, String entityId,
			int entityKind) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueTrackerInvocation(String userId, String ipAddress,
			String userAgent, String url, String title, String query,
			String goalId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueCounterCache(String query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueuePointsUpdater(String userId, Integer action,
			Integer targetKind, String targetId, String data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enqueueAddPendingFriendsUsingFacebookId(String facebookId,
			String userId) {
		// TODO Auto-generated method stub
		
	}

}
