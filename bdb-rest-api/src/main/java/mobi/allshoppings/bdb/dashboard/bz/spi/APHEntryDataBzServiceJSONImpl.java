package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;

import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.APDVisitDAO;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.APHEntryDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class APHEntryDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(APHEntryDataBzServiceJSONImpl.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
	@Autowired
	private APHHelper aphHelper;

	@Autowired
	private APHEntryDAO dao;
	
	@Autowired
	private APDVisitDAO apdvDao;
	
	@Autowired
	private APDeviceDAO apdDao;

	/**
	 * Obtains a Dashboard report prepared to form a APHEntry graph
	 * 
	 * @return A JSON representation of the selected graph
	 */
	@Override
	public String retrieve()
	{
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		sdf2.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			obtainUserIdentifier(true);

			String identifier = obtainStringValue("identifier", null);
			String hostnameList = obtainStringValue("hostnames", null);
			String mac = obtainStringValue("mac", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			Boolean original = obtainBooleanValue("original", false);

			APDevice dev = null;
			
			int fromHour = 0;
			int toHour = 4320;
			try { 
				String fromStringHour = obtainStringValue("fromStringHour", null);
				fromHour = aphHelper.stringToOffsetTime(fromStringHour); 
			} catch( Exception e ) {}
			try { 
				String toStringHour = obtainStringValue("toStringHour", null);
				toHour = aphHelper.stringToOffsetTime(toStringHour); 
			} catch( Exception e ) {}
			
			List<APHEntry> entries = CollectionFactory.createList();
			if (StringUtils.hasText(identifier)) {
				APHEntry obj = dao.get(identifier);
				entries.add(aphHelper.getFromCache(obj.getHostname(), obj.getMac(), obj.getDate()));
			} else {
				String[] hostnames = hostnameList.split(",");
				for (String hostname : hostnames) {
					entries.add(aphHelper.getFromCache(hostname, mac, fromStringDate));
				}
			}

			List<APDVisit> visits = CollectionFactory.createList();
			Map<Long, Integer> values = CollectionFactory.createMap();
			
			JSONArray series = new JSONArray();

			// Natural RSSI
			if( !original ) {
				for( APHEntry entry : entries ) {
					JSONObject serie = new JSONObject();
					JSONArray data = new JSONArray();
					Map<String, Integer> candidate = entry.getRssi();
					boolean hasFirst = false;
					int max = getLastPosition(entry);
					for(int i = fromHour; i < toHour; i++) {
						if( candidate.containsKey(String.valueOf(i))) {
							JSONArray ele = new JSONArray();
							long key = sdf.parse(entry.getDate()).getTime() + (i*20000); 
							ele.put(key);
							ele.put(candidate.get(String.valueOf(i)));
							data.put(ele);
							
							Integer val = values.get(key);
							if( val == null ) val = new Integer(-200);
							if( candidate.get(String.valueOf(i)) > val )
								val = candidate.get(String.valueOf(i));
							values.put(key, val);
							
							hasFirst = true;
							
						} else {
							if(hasFirst && i <= max) {
								JSONArray ele = new JSONArray();
								ele.put(sdf.parse(entry.getDate()).getTime() + (i*20000));
								ele.put((Integer)null);
								data.put(ele);
							}
						}
					}
					serie.put("data", data);
					serie.put("name", entry.getHostname());
					serie.put("type", "spline");
					serie.put("yAxis", 0);
					series.put(serie);
				}
			}
			
			// Main Selected RSSI (Natural or Artificial... depending on service parameters )
			for( APHEntry entry : entries ) {
				
				if( dev == null )
					dev = apdDao.get(entry.getHostname());
				
				JSONObject serie = new JSONObject();
				JSONArray data = new JSONArray();
				Map<String, Integer> candidate = (entry.getArtificialRssi().size() > 0 && !original) ? entry.getArtificialRssi() : entry.getRssi();
				boolean hasFirst = false;
				int max = getLastPosition(entry);
				for(int i = fromHour; i < toHour; i++) {
					if( candidate.containsKey(String.valueOf(i))) {
						JSONArray ele = new JSONArray();
						long key = sdf.parse(entry.getDate()).getTime() + (i*20000); 
						ele.put(key);
						ele.put(candidate.get(String.valueOf(i)));
						data.put(ele);
						
						Integer val = values.get(key);
						if( val == null ) val = new Integer(-200);
						if( candidate.get(String.valueOf(i)) > val )
							val = candidate.get(String.valueOf(i));
						values.put(key, val);

						hasFirst = true;
						
					} else {
						if( hasFirst && i <= max) {
							JSONArray ele = new JSONArray();
							ele.put(sdf.parse(entry.getDate()).getTime() + (i*20000));
							ele.put((Integer)null);
							data.put(ele);
						}
					}
				}
				serie.put("data", data);
				serie.put("name", entry.getHostname());
				serie.put("type", "spline");
				serie.put("yAxis", 1);
				series.put(serie);
				
				if(!original)
					visits.addAll(apdvDao.getUsingAPHE(entry.getIdentifier(), false));
				
			}
			
			// Visits 
			
			if(!original) {
				for(APDVisit visit : visits ) {
					
					long vstart = sdf2.parse(sdf3.format(visit.getCheckinStarted())).getTime();
					long vend = sdf2.parse(sdf3.format(visit.getCheckinFinished())).getTime();

					JSONObject serie = new JSONObject();
					JSONArray data = new JSONArray();

					while( vstart <= vend ) {

						JSONArray ele = new JSONArray();
						long key = vstart; 
						ele.put(key);
						ele.put(values.get(key));
						data.put(ele);

						vstart += 20000;
					}

					String name = "";
					int yAxis = 0;
					if( visit.getCheckinType().equals(APDVisit.CHECKIN_VISIT)) {
						name = "Visita";
						yAxis = 3;
					}
					else if( visit.getCheckinType().equals(APDVisit.CHECKIN_PEASANT)) {
						name = "Paseante";
						yAxis = 2;
					}
					else if( visit.getCheckinType().equals(APDVisit.CHECKIN_EMPLOYEE)) {
						name = "Empleado";
						yAxis = 3;
					}
					
					serie.put("data", data);
					serie.put("name", name);
					serie.put("type", "spline");
					serie.put("checkinType", visit.getCheckinType());
					serie.put("yAxis", yAxis);
					series.put(serie);

				}
			}
			
			// Returns the final value
			JSONObject ret = new JSONObject();
			ret.put("series", series);
			ret.put("apdevice", new JSONObject(new Gson().toJson(dev)));
			return ret.toString();
			
		} catch (ASException e) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
				log.log(Level.INFO, e.getMessage());
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			return getJSONRepresentationFromException(e).toString();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} finally {
			markEnd(start);
		}
	}

	int getLastPosition(APHEntry obj) {
		int ret = 0;
		Iterator<String> i = obj.getRssi().keySet().iterator();
		while(i.hasNext()) {
			int val = Integer.valueOf(i.next());
			if( val > ret )
				ret = val;
		}
		return ret;
	}
	
}
