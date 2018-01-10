package mobi.allshoppings.bdb.bz.spi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONObject;

import com.inodes.util.CollectionFactory;

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.Range;

public class BDBAPDVisitBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<APDVisit> implements BDBCrudBzService {

	private static final long ONE_DAY = 86400000;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	static {
	    //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	
	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"mac",
				"devicePlatform",
				"entityId",
				"entityKind",
				"checkinStarted",
				"checkinFinished",
				"checkinType",
				"apheSource"
		};
	}

	@Override
	public void config() {
		setMyClazz(APDVisit.class);
	}

	@Override
	public void setKey(APDVisit obj, JSONObject seed) throws ASException {
	}
	
	public String list() {
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<APDVisit> list = new ArrayList<APDVisit>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get range, if not defined use default value
			Range range = this.obtainRange();
			
			// Aditional Parameters
			String entityId = this.obtainStringValue(ENTITY_ID, null);
			Integer entityKind = this.obtainIntegerValue(ENTITY_KIND, null);
			String date = this.obtainStringValue(DATE, sdf.format(new Date()));
			Integer checkinType = this.obtainIntegerValue(CHECKIN_TYPE, null);
			String order = this.obtainStringValue(ORDER, null);

			if( "".equals(entityId)) entityId = null;
			if( "".equals(date)) date = sdf.format(new Date());

			// Get the output fields
			String[] fields = this.obtainOutputFields(myClazz, getListFields());
			
			Map<String,String> attributes = CollectionFactory.createMap();
			
			// retrieve all elements
			long millisPre = new Date().getTime();
			Date fromDate = sdf.parse(date);
			Date toDate = new Date(fromDate.getTime() + ONE_DAY);
			
			//list = dao.getUsingEntityIdAndEntityKindAndDate(entityId, entityKind, fromDate, toDate, checkinType, range, order, attributes, false);
			
			
			DumperHelper<APDVisit> dumper = new DumpFactory<APDVisit>().build(null, APDVisit.class, false);
			dumper.setFilter(entityId);			
			Iterator<APDVisit> visits = dumper.iterator(fromDate, toDate, false);
			
			
			DateTime first = new DateTime(fromDate);
			LocalDate firstDate = first.toLocalDate();
			
			while (visits.hasNext()) {

				
				APDVisit visit = visits.next();
				DateTime comparable = new DateTime(visit.getCheckinStarted());
				LocalDate comparableLocal = comparable.toLocalDate();

				if(visit.getCheckinType() == 2 && firstDate.equals(comparableLocal)) {
					list.add(visit);	
				}
					
			}
	
					
			long diff = System.currentTimeMillis() - millisPre;
			
			// Logs the result
			log.info("Number of elements found [" + list.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(list, fields);
			
			if( attributes.containsKey("recordCount"))
				returnValue.put("recordCount", Long.valueOf(attributes.get("recordCount")));
			else 
				returnValue.put("recordCount", list.size());
				
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service." + myClazz.getSimpleName() + "BzService"),
					null, null);

    	} catch (ASException e) {
    		if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
    				e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
    			log.log(Level.INFO, e.getMessage());
    		} else {
    			log.log(Level.SEVERE, e.getMessage(), e);
    		}
    		returnValue = getJSONRepresentationFromException(e);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = this.getJSONRepresentationFromException(e);
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
	}
}
