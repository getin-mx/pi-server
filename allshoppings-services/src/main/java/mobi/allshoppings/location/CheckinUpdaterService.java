package mobi.allshoppings.location;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.google.gson.Gson;

import mobi.allshoppings.dao.CheckinDAO;
import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.spi.CheckinDAOJDOImpl;
import mobi.allshoppings.dao.spi.ShoppingDAOJDOImpl;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.model.Checkin;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.GsonFactory;


public class CheckinUpdaterService {

	private static final Logger log = Logger.getLogger(CheckinUpdaterService.class.getName());
	private static final long FIFTEEN_MINUTES = 900000L;
	private static final long CHECKINCLOSELIMITMILLIS = 900000;
	private static final int GEOHASH_PRESITION = 6;

	private ShoppingDAO shoppingDao = new ShoppingDAOJDOImpl();
	private GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();
	private CheckinDAO checkinDao = new CheckinDAOJDOImpl();
	protected Gson gson = GsonFactory.getInstance();
	
	public void updateCheckins(Date fromDate, Date toDate) throws ASException, IOException {

		DumperHelper<DeviceLocationHistory> dumper = new DumpFactory<DeviceLocationHistory>().build(
				null, DeviceLocationHistory.class, false);
		Map<String, List<InterestingPoint>> interestingPointsMap = getInterestingPointMap(shoppingDao, geocoder, null); 

		long totals = 0;
		long initTime = System.currentTimeMillis();

		DeviceLocationHistory obj = null;
		InterestingPoint nearest = null;

		Set<String> openedCheckins = CollectionFactory.createSet();
		
		Iterator<DeviceLocationHistory> i = dumper.iterator(fromDate, toDate, false);
		while(i.hasNext()) {

			obj = i.next();
			if( obj != null && obj.getLat() != null && obj.getLon() != null ) {
				totals++;
				
				if( totals % 1000 == 0 ) 
					log.log(Level.INFO, "Processing for date " + obj.getCreationDateTime());
				
				if( obj.getGeohash() == null ) obj.setGeohash(geocoder.encodeGeohash(obj.getLat(), obj.getLon()));
				try {
					nearest = getNearestPoint(geocoder, interestingPointsMap, obj.getLat(), obj.getLon(), obj.getGeohash());
				} catch( IllegalArgumentException e ) {
					nearest = null;
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
					nearest = null;
				}

				if( nearest != null && nearest.getDistance() <= nearest.getCheckinDistance()) {

					try {
						Checkin checkin = checkinDao.getUnfinishedCheckinByEntityAndKindAndType(obj.getDeviceUUID(),
								null, nearest.getEntityKind(), Checkin.CHECKIN_AUTO,
								CHECKINCLOSELIMITMILLIS, obj.getCreationDateTime());

						// Checks that we are talking about the same checkin point
						if(!checkin.getEntityId().equals(nearest.getEntityId())) {
							checkin.setCheckinFinished(obj.getCreationDateTime());

							// If checkin is too short... it means this is possibly a fake one...
							// And in this case, fake means a car passing through the door of a 
							// too large geofence limit
							if(( checkin.getCheckinFinished().getTime() - checkin.getCheckinStarted().getTime()) < FIFTEEN_MINUTES) {
								checkin.setPossibleFake(true);
							}
							checkinDao.update(checkin);

							// Registers the checkin
							checkin = buildCheckin(obj.getUserId(), obj.getDeviceUUID(), nearest.getIdentifier(), nearest.getEntityKind(), Checkin.CHECKIN_AUTO, obj.getCreationDateTime());
							checkinDao.create(checkin);

						} else {
							// If Checkin is Finished, and it meets the closeLimitMillis criteria,
							// It means that I have to open it
							if( checkin.getCheckinFinished() != null ) {
								reopenClosedCheckin(checkin);
							}
						}

						openedCheckins.add(obj.getDeviceUUID());

					} catch( ASException e ) {
						if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
							throw e;
						} else {
							try {
								checkinDao.getCheckinByEntityAndKindAndType(obj.getDeviceUUID(), nearest.getIdentifier(), nearest.getEntityKind(), Checkin.CHECKIN_AUTO, obj.getCreationDateTime());
							} catch( ASException e1 ) {
								// Registers the checkin
								final Checkin checkin = buildCheckin(obj.getUserId(), obj.getDeviceUUID(), nearest.getIdentifier(), nearest.getEntityKind(), Checkin.CHECKIN_AUTO, obj.getCreationDateTime());
								checkinDao.create(checkin);
							}
						}
					}

					// If not... will try to close any pre-existent checkin
				} else {
					try {
						if( openedCheckins.contains(obj.getDeviceUUID())) {
							final Checkin checkin = checkinDao.getUnfinishedCheckinByEntityAndKindAndType(obj.getDeviceUUID(), null, null, null, null, obj.getCreationDateTime());
							checkin.setCheckinFinished(obj.getCreationDateTime());

							// If checkin is too short... it means this is possibly a fake one...
							// And in this case, fake means a car passing through the door of a 
							// too large geofence limit
							if(( checkin.getCheckinFinished().getTime() - checkin.getCheckinStarted().getTime()) < FIFTEEN_MINUTES) {
								checkin.setPossibleFake(true);
							}
							checkinDao.update(checkin);
							openedCheckins.remove(obj.getDeviceUUID());
						}
					} catch( ASException e ) {
						if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
							throw e;
						}
					}
				}

			}
		}

		dumper.dispose();
		
		long endTime = System.currentTimeMillis();
		log.log(Level.INFO, totals + " elements calculated in " + (endTime - initTime) + "ms for this process");

	}

	/**
	 * Reopens a closed checkin. This method is used to reopen a recently closed
	 * checkin due to a reingress (probably, it could be a car pass in front of
	 * the hotspot or checkin area)
	 * 
	 * @param checkin
	 *            The checkin to reopen
	 * @return The reopened Checkin
	 * @throws ASException
	 */
	public Checkin reopenClosedCheckin(Checkin checkin) throws ASException {
		checkin.setCheckinFinished(null);
		checkin.setPossibleFake(false);
		checkinDao.update(checkin);
		return checkin;
	}

	/**
	 * Creates a new checkin entity based on data received from service
	 * 
	 * @param userId
	 *            The user Identifier, obtained from the device ID
	 * @param deviceUUID
	 *            The real device ID
	 * @param entityId
	 *            Nearest entity ID
	 * @param entityKind
	 *            Nearest entity Kind
	 * @param checkinType
	 *            Check in type (typically, CHECKIN_AUTO)
	 * @param forDate
	 *            Date for wich the checkin will be made
	 * @return A fully formed Checkin instance
	 * @throws ASException
	 */
	public Checkin buildCheckin(String userId, String deviceUUID, String entityId, Integer entityKind, Integer checkinType, Date forDate) throws ASException {

		Checkin checkin = new Checkin();
		checkin.setCheckinStarted(forDate);
		checkin.setCheckinFinished(null);
		checkin.setCheckinType(checkinType);
		checkin.setDeviceUUID(deviceUUID);
		checkin.setEntityId(entityId);
		checkin.setEntityKind(entityKind);
		checkin.setUserId(userId);

		checkin.setKey(checkinDao.createKey());

		return checkin;
	}


	/**
	 * Gets the nearest point
	 * 
	 * @param geocoder
	 *            The GeoCodingHelper implementation to do the math
	 * @param interestingPointMap
	 *            A map containing the cached info
	 * @param lat
	 *            Point latitude
	 * @param lon
	 *            Point longitude
	 * @return
	 */
	public InterestingPoint getNearestPoint(GeoCodingHelper geocoder, Map<String, List<InterestingPoint>> interestingPointMap, double lat, double lon, String geohash) {

		String[] boxes = geocoder.getAdjacentPoints(new GeoPoint(lat, lon, geohash), GEOHASH_PRESITION);
		List<InterestingPoint> foundPoints = CollectionFactory.createList();
		for( String box : boxes ) {
			if( interestingPointMap.containsKey(box))
				foundPoints.addAll(interestingPointMap.get(box));
		}

		InterestingPoint nearest = null;
		int nearestDistance = 0;
		for ( InterestingPoint gp : foundPoints ) {
			int distance = geocoder.calculateDistance(lat, lon, gp.getLat(), gp.getLon());
			if( distance < 1000 ) {
				if( nearest == null || distance < nearestDistance ) {
					nearest = gp;
					nearestDistance = distance;
				}
			}
		}

		if( nearest != null ) {
			nearest.setDistance(nearestDistance);
			return nearest;
		} else {
			return null;
		}
	}

	/**
	 * Builds a map of interesting points and hashes it
	 * 
	 * @param dao
	 *            The Dao from which the data will be readed
	 * @param geocoder
	 *            The GeoCodingHelper implementation to do the math
	 * @param incremental
	 *            If it needs to be incremental
	 * @return
	 * @throws ASException
	 */
	public Map<String, List<InterestingPoint>> getInterestingPointMap(GenericDAO<?> dao, GeoCodingHelper geocoder, Map<String, List<InterestingPoint>> incremental) throws ASException {
		Map<String, List<InterestingPoint>> ret = CollectionFactory.createMap();
		if( incremental != null ) ret.putAll(incremental);
		@SuppressWarnings("unchecked")
		List<ModelKey> list = (List<ModelKey>)dao.getAll();
		for( ModelKey object : list ) {
			JSONObject obj = new JSONObject(gson.toJson(object));
			JSONObject address = obj.getJSONObject("address");

			InterestingPoint point = new InterestingPoint(address.getDouble("latitude"), 
					address.getDouble("longitude"), obj.has("checkinAreaSize") ? obj.getInt("checkinAreaSize") : 60,
					object.getIdentifier(), 
					(object instanceof Shopping) ? EntityKind.KIND_SHOPPING : EntityKind.KIND_STORE);
			
			point.setGeohash(geocoder.encodeGeohash(point.getLat(), point.getLon()));
			
			List<InterestingPoint> l = ret.get(point.getGeohash().substring(0,GEOHASH_PRESITION));
			if( l == null ) l = CollectionFactory.createList();
			l.add(point);
			
			ret.put(point.getGeohash().substring(0,GEOHASH_PRESITION), l);
			
		}
		return ret;
	}


	public class InterestingPoint extends GeoPoint {

		private int checkinDistance;
		private String entityId;
		private int entityKind;
		private int distance;

		/**
		 * @param checkinDistance
		 * @param entityId
		 * @param entityKind
		 */
		public InterestingPoint(double lat, double lon, int checkinDistance, String entityId, int entityKind) {
			super(lat, lon, null);
			this.checkinDistance = checkinDistance;
			this.entityId = entityId;
			this.entityKind = entityKind;
		}
		/**
		 * @return the checkinDistance
		 */
		public int getCheckinDistance() {
			return checkinDistance;
		}
		/**
		 * @param checkinDistance the checkinDistance to set
		 */
		public void setCheckinDistance(int checkinDistance) {
			this.checkinDistance = checkinDistance;
		}
		/**
		 * @return the entityId
		 */
		public String getEntityId() {
			return entityId;
		}
		public String getIdentifier() {
			return entityId;
		}
		/**
		 * @param entityId the entityId to set
		 */
		public void setEntityId(String entityId) {
			this.entityId = entityId;
		}
		/**
		 * @return the entityKind
		 */
		public int getEntityKind() {
			return entityKind;
		}
		/**
		 * @param entityKind the entityKind to set
		 */
		public void setEntityKind(int entityKind) {
			this.entityKind = entityKind;
		}
		/**
		 * @return the distance
		 */
		public int getDistance() {
			return distance;
		}
		/**
		 * @param distance the distance to set
		 */
		public void setDistance(int distance) {
			this.distance = distance;
		}
	}
}
