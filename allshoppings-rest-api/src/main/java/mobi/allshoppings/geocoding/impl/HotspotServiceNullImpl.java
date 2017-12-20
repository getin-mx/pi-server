package mobi.allshoppings.geocoding.impl;

import java.util.Date;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.dao.CheckinDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.HotspotService;
import mobi.allshoppings.model.Checkin;
import mobi.allshoppings.model.DeviceWifiLocationHistory;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.adapter.DeviceLocationAdapter;
import mobi.allshoppings.model.adapter.HotSpotAdapter;
import mobi.allshoppings.tx.BaseTransactionableTask;
import mobi.allshoppings.tx.PersistenceProvider;
import mobi.allshoppings.tx.TransactionFactory;


public class HotspotServiceNullImpl implements HotspotService {

	@SuppressWarnings("unused")
	private final static Logger log = Logger.getLogger(HotspotServiceNullImpl.class.getName());
	private static final long FIVE_MINUTES = 300000L;
	private static final long ONE_HOUR = 3600000L;
	private static final long FIVE_HOURS = ONE_HOUR * 5;

	@Autowired
	private TransactionFactory txFactory;
	@Autowired
	private CheckinDAO checkinDao;
	@Autowired
	private SystemConfiguration systemConfiguration;


	@Override
	public void calculateWifiSpot(String deviceWifiLocationId)
			throws ASException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calculateWifiSpot(DeviceWifiLocationHistory deviceWifiLocation)
			throws ASException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(String deviceUUID, Double lat, Double lon)
			throws ASException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addWithKnownLocation(DeviceLocationAdapter adapter,
			HotSpotAdapter nearest) throws ASException {
		if( nearest == null ) {
			for( HotSpotAdapter spot : adapter.getNearSpots()) {
				if( nearest == null || spot.getPointDistance() < nearest.getPointDistance() ) nearest = spot;
			}
		}

		if( nearest != null ) {
			if( nearest.getPointDistance() <= nearest.getCheckinDistance() ) {
				try {
					Checkin checkin = checkinDao.getUnfinishedCheckinByEntityAndKindAndType(
							adapter.getDeviceUUID(), nearest.getIdentifier(), nearest.getEntityKind(),
							Checkin.CHECKIN_AUTO, systemConfiguration.getCheckinCloseLimitMillis());

					// If Checkin is Finished, and it meets the closeLimitMillis criteria,
					// It means that I have to open it
					if( checkin.getCheckinFinished() != null ) {
						reopenClosedCheckin(checkin);
					}

					if( (System.currentTimeMillis() - checkin.getCheckinStarted().getTime()) > FIVE_HOURS ) {
						throw ASExceptionHelper.forbiddenException();
					}

				} catch( ASException e ) {
					if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
						throw e;
					} else {
						// Registers the checkin
						final Checkin checkin = buildCheckin(adapter.getUserId(), adapter.getDeviceUUID(),
								nearest.getIdentifier(), nearest.getEntityKind(), Checkin.CHECKIN_AUTO);
						txFactory.createWithTransactionableTask(new BaseTransactionableTask() {
							@Override
							public void run(PersistenceProvider pp) throws ASException {
								checkinDao.create(checkin);
							}
						});
					}
				}

				// If not... will try to close any pre-existent checkin
			} else {
				try {
					final Checkin checkin = checkinDao.getUnfinishedCheckinByEntityAndKindAndType(
							adapter.getDeviceUUID(), null, (byte) -1, (byte) -1);
					checkin.setCheckinFinished(new Date());

					// If checkin is too short... it means this is possibly a fake one...
					// And in this case, fake means a car passing through the door of a 
					// too large geofence limit
					if(( checkin.getCheckinFinished().getTime() - checkin.getCheckinStarted().getTime()) < FIVE_MINUTES) {
						checkin.setPossibleFake(true);
					}

					txFactory.createWithTransactionableTask(new BaseTransactionableTask() {
						@Override
						public void run(PersistenceProvider pp) throws ASException {
							checkinDao.update(checkin);
						}
					});
				} catch( ASException e ) {
					if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
						throw e;
					}
				}
			}
		}
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
	 * @return A fully formed Checkin instance
	 * @throws ASException
	 */
	public Checkin buildCheckin(String userId, String deviceUUID, String entityId, byte entityKind,
			byte checkinType) throws ASException {

		Checkin checkin = new Checkin();
		checkin.setCheckinStarted(new Date());
		checkin.setCheckinFinished(null);
		checkin.setCheckinType(checkinType);
		checkin.setDeviceUUID(deviceUUID);
		checkin.setEntityId(entityId);
		checkin.setEntityKind(entityKind);
		checkin.setUserId(userId);

		checkin.setKey(checkinDao.createKey());

		return checkin;
	}

}
