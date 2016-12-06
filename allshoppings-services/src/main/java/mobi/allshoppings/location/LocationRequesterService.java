package mobi.allshoppings.location;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.DeviceLocationDAO;
import mobi.allshoppings.dao.spi.DeviceInfoDAOJDOImpl;
import mobi.allshoppings.dao.spi.DeviceLocationDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.geocoding.GeoCodingHelper;
import mobi.allshoppings.geocoding.GeoPoint;
import mobi.allshoppings.geocoding.impl.GeoCodingHelperGMapsImpl;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.DeviceLocation;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.CollectionUtils;
import mobi.allshoppings.tools.Range;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class LocationRequesterService {

	private static final Logger log = Logger.getLogger(LocationRequesterService.class.getName());
	
	private DeviceInfoDAO deviceInfoDao = new DeviceInfoDAOJDOImpl();
	private DeviceLocationDAO deviceLocationDao = new DeviceLocationDAOJDOImpl();
	private GeoCodingHelper geocoder = new GeoCodingHelperGMapsImpl();

	public void requestLocation(String serviceUrl, List<GeoPoint> points, int distance, long locationUpdateLimit, int batchSize, String appId) throws ASException {
		
		deviceLocationDao.setGeocoder(geocoder);
		List<String> devicesToUpdate = CollectionFactory.createList();
		
		for(GeoPoint geoPoint : points ) { 
			Date lastLocationUpdate = new Date((long)(new Date().getTime() - (locationUpdateLimit)));
			List<DeviceLocation> candidateDevices = deviceLocationDao.getByProximity(
					geoPoint, new Integer(5), distance, appId, lastLocationUpdate, true);

			for( DeviceLocation dl : candidateDevices ) {
				if( !devicesToUpdate.contains(dl.getDeviceUUID()))
					devicesToUpdate.add(dl.getDeviceUUID());
			}

		}
		
		List<List<String>> batches = CollectionFactory.createList();
		for( int i = 0; i < devicesToUpdate.size(); i+= batchSize) {
			List<String> batch = CollectionFactory.createList();
			for( int j = i; j < (i+batchSize) && j < devicesToUpdate.size(); j++ ) {
					batch.add(devicesToUpdate.get(j));
			}
			batches.add(batch);
		}

		log.log(Level.INFO, devicesToUpdate.size() + " devices found divided in " + batches.size() + " batches...");
		long initTime = new Date().getTime();

		List<RequestLocationWorker> workers = CollectionFactory.createList();
		
		for( List<String> batch : batches ) {
			RequestLocationWorker worker = new RequestLocationWorker(serviceUrl, batch);
			workers.add(worker);
			worker.start();
		}
		
		for( RequestLocationWorker worker : workers ) {
			try {
				worker.join();
			} catch (InterruptedException e) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		long endTime = new Date().getTime();
		log.log(Level.INFO, devicesToUpdate.size() + " locations requested in " + (endTime - initTime) + "ms");

		
	}
	
	public void requestLocation(String serviceUrl, int batchSize, List<String> restrictedDevices) throws ASException {
	
		RestTemplate restTemplate = new RestTemplate();
		HttpMessageConverter<?> formHttpMessageConverter = new MappingJackson2HttpMessageConverter();
		HttpMessageConverter<?> stringHttpMessageConverternew = new StringHttpMessageConverter();
		restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter<?>[]{formHttpMessageConverter, stringHttpMessageConverternew}));

		List<Integer> status = Arrays.asList(new Integer[] {StatusAware.STATUS_ENABLED}); 
		Range range = new Range(0, batchSize);

		if(CollectionUtils.isEmpty(restrictedDevices)) {
			List<DeviceInfo> list = deviceInfoDao.getUsingStatusAndRange(status, range, null, null, true);
			while(!CollectionUtils.isEmpty(list)) {

				List<String> arr = CollectionFactory.createList();
				for( DeviceInfo dl : list ) {
					arr.add(dl.getDeviceUUID());
				}

				Map<String, Object> obj = CollectionFactory.createMap();
				obj.put("deviceList", arr);

				// Log
				log.log(Level.INFO, "Sending Location Request for " + obj.toString());

				// Sends the data to the server
				try {
					Object result = restTemplate.postForObject(serviceUrl, obj, Object.class);
					log.log(Level.INFO, result.toString());
				} catch( Exception e ) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}

				// Changes to a new range
				range.setFrom(range.getTo());
				range.setTo(range.getTo() + batchSize);
				list = deviceInfoDao.getUsingStatusAndRange(status, range, null, null, true);
			}

		} else {

			List<DeviceInfo> list = deviceInfoDao.getUsingIdList(restrictedDevices);
			List<String> arr = CollectionFactory.createList();
			for( DeviceInfo dl : list ) {
				arr.add(dl.getDeviceUUID());
			}

			Map<String, Object> obj = CollectionFactory.createMap();
			obj.put("deviceList", arr);

			// Log
			log.log(Level.INFO, "Sending Location Request for " + obj.toString());

			// Sends the data to the server
			try {
				Object result = restTemplate.postForObject(serviceUrl, obj, Object.class);
				log.log(Level.INFO, result.toString());
			} catch( Exception e ) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	class RequestLocationWorker extends Thread {
		List<String> batch;
		String serviceUrl;
		
		public RequestLocationWorker(String serviceUrl, List<String> batch) {
			this.batch = batch;
			this.serviceUrl = serviceUrl;
		}
		
		public void run() {
			RestTemplate restTemplate = new RestTemplate();
			HttpMessageConverter<?> formHttpMessageConverter = new MappingJackson2HttpMessageConverter();
			HttpMessageConverter<?> stringHttpMessageConverternew = new StringHttpMessageConverter();
			restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter<?>[]{formHttpMessageConverter, stringHttpMessageConverternew}));

			Map<String, Object> obj = CollectionFactory.createMap();
			obj.put("deviceList", batch);

			// Log
			log.log(Level.INFO, "Sending Location Request for " + obj.toString());

			// Sends the data to the server
			int retries = 3;
			while( retries > 0 ) {
				try {
					Object result = restTemplate.postForObject(serviceUrl, obj, Object.class);
					if( result.toString().contains("status=500")) {
						retries--;
					} else {
						log.log(Level.INFO, result.toString());
						retries = 0;
					}
				} catch( Exception e ) {
					retries--;
					try{ Thread.sleep(3000); } catch( Exception e1 ) {}
				}
			}
		}
	}
}

