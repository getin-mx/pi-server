package mobi.allshoppings.task;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.Range;

public interface QueueTaskHelper {

	// Static parameters and environment names
	public static final String QUEUE_NAME = "X-AppEngine-QueueName";
	public static final String TASK_NAME = "X-AppEngine-TaskName";

	// Queues
	public static String QUEUE_INDEXING = "IndexingQueue";
	public static String QUEUE_GEO = "GeoQueue";
	public static String QUEUE_GENERAL = "GeneralQueue";
	public static String QUEUE_COUNTER_CACHE = "CounterCacheQueue";
	public static String QUEUE_PUSH = "PushQueue";
	public static String QUEUE_REPLICA = "PullReplicaQueue";
	public static String QUEUE_DEVICE_LOCATION = "PullDeviceLocationQueue";

	public abstract void enqueueTransientInReplica(ModelKey obj)
			throws ASException;

	public abstract void enqueueInReplica(ModelKey obj) throws ASException;

	public abstract void enqueueInReplica(String className, JSONObject payload)
			throws JSONException;

	/**
	 * Enqueues a new task in a specific job queue
	 * 
	 * @param queueName
	 *            The job queue name
	 * @param url
	 *            Task URL
	 * @param taskName
	 *            This task name
	 * @param parameters
	 *            A map with this task parameters
	 */
	public abstract void enqueue(String queueName, String url, String taskName,
			Map<String, String> parameters);

	/**
	 * Re enqueues a task (with additional parameters)
	 * 
	 * @param Task
	 *            Request Payload
	 * @param parameters
	 *            Additional Parameters to add
	 */
	public abstract void reenqueue(HttpServletRequest req,
			Map<String, String> parameters);

	/**
	 * Re enqueues a task (with a task range and additional parameters)
	 * 
	 * @param Task
	 *            Request Payload
	 * @param parameters
	 *            Additional Parameters to add
	 * @param range
	 *            Payload entity range
	 */
	public abstract void reenqueueWithRange(HttpServletRequest req,
			Map<String, String> parameters, Range range, List<String> ids);

	/**
	 * Returns this task queue name
	 * 
	 * @param req
	 *            The payload request
	 * @return this task queue name
	 */
	public abstract String getQueueName(HttpServletRequest req);

	/**
	 * Return this task name
	 * 
	 * @param req
	 *            The payload request
	 * @return This task name
	 */
	public abstract String getTaskName(HttpServletRequest req);

	/**
	 * Return this task URL
	 * 
	 * @param req
	 *            The payload request
	 * @return This task URL
	 */
	public abstract String getTaskUrl(HttpServletRequest req);

	// Indexing Tasks ------------------------------------------------------------------------------- //
	public abstract void enqueueUnindex(String clazzName, String id);

	public abstract void enqueueUnindex(String clazzName);

	public abstract void enqueueIndex(String clazzName);

	public abstract void enqueueIndex(String clazzName, String id);

	public abstract void enqueueIndex(String clazzName, Range range, String id);

	public abstract void enqueueIndex(String clazzName, Range range, String id,
			String method);

	public abstract void enqueueUngeo(String clazzName, String id);

	public abstract void enqueueUngeo(String clazzName);

	public abstract void enqueueGeo(String clazzName);

	public abstract void enqueueGeo(String clazzName, String id);

	public abstract void enqueueGeo(String clazzName, Range range, String id);

	public abstract void enqueueGeo(String clazzName, Range range, String id,
			String method);

	public abstract void enqueueNapalm(String clazzName);

	public abstract void enqueueDeleteOrphanDevices();

	public abstract void enqueueDeleteOrphanLocations();

	public abstract void enqueueDeleteEvictedUEC();

	public abstract void enqueueNotifyOffers();

	public abstract void enqueueNotifyOffers(String userId);

	public abstract void enqueueRebuildUEC();

	public abstract void enqueueMoveExpiredOffers();

	public abstract void enqueueNotificationLogReceived(String userId,
			String entityId, int entityKind);

	public abstract void enqueueTrackerInvocation(String userId,
			String ipAddress, String userAgent, String url, String title,
			String query, String goalId);

	public abstract void enqueueCounterCache(String query);

	public abstract void enqueuePointsUpdater(final String userId,
			final Integer action, final Integer targetKind,
			final String targetId, final String data);

	public abstract void enqueueAddPendingFriendsUsingFacebookId(
			final String facebookId, final String userId);

}