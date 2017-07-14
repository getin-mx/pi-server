package mobi.allshoppings.cinepolis.services;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.dao.CheckinDAO;
import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.dao.spi.CheckinDAOJDOImpl;
import mobi.allshoppings.dao.spi.CinemaDAOJDOImpl;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.dump.impl.DumperHelperImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.location.CheckinUpdaterService;
import mobi.allshoppings.model.Checkin;
import mobi.allshoppings.model.DeviceLocationHistory;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.tools.CollectionFactory;

public class CinepolisCheckinUpdaterService extends CheckinUpdaterService {
	
	private final static Logger log = Logger.getLogger(CinepolisCheckinUpdaterService.class.getName());

	private static final long HUNDRED_MINUTES = 6000000;
	private static final long CHECKINCLOSELIMITMILLIS = 900000;

	private CinemaDAO cinemaDao = new CinemaDAOJDOImpl();
	private GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();
	private CheckinDAO checkinDao = new CheckinDAOJDOImpl();

	@Override
	public void updateCheckins(String baseDir, Date fromDate, Date toDate) throws ASException, IOException {
		DumperHelper<DeviceLocationHistory> dumper = new DumpFactory<DeviceLocationHistory>().build(baseDir, DeviceLocationHistory.class);
		Map<String, List<InterestingPoint>> interestingPointsMap = getInterestingPointMap(cinemaDao, geocoder, null); 

		long totals = 0;
		long initTime = new Date().getTime();

		DeviceLocationHistory obj = null;
		InterestingPoint nearest = null;

		Set<String> openedCheckins = CollectionFactory.createSet();
		
		Iterator<DeviceLocationHistory> i = dumper.iterator(fromDate, toDate);
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
							if(( checkin.getCheckinFinished().getTime() - checkin.getCheckinStarted().getTime()) < HUNDRED_MINUTES) {
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
							if(( checkin.getCheckinFinished().getTime() - checkin.getCheckinStarted().getTime()) < HUNDRED_MINUTES) {
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

		long endTime = new Date().getTime();
		log.log(Level.INFO, totals + " elements calculated in " + (endTime - initTime) + "ms for this process");

	}

}
