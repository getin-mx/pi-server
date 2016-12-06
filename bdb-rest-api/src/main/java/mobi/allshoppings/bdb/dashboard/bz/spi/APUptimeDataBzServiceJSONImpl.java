package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.APUptimeDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APUptime;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class APUptimeDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(APUptimeDataBzServiceJSONImpl.class.getName());

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final DecimalFormat df = new DecimalFormat("00");
	private static final long ONE_DAY = 86400000;
	
	@Autowired
	private APUptimeDAO dao;

	/**
	 * Obtains heatmap report for antennas uptime
	 * 
	 * @return A JSON representation of the selected graph
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			obtainUserIdentifier(true);

			// Obtains request parameters
			String hostname = obtainStringValue("identifier", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);

			// Converts date parameters to their date form
			Date fromDate = DateUtils.truncate(sdf.parse(fromStringDate), Calendar.DATE);
			Date toDate = DateUtils.truncate(sdf.parse(toStringDate), Calendar.DATE);

			// Prepares the data segment
			List<String> xCat = CollectionFactory.createList();
			List<String> yCat = CollectionFactory.createList();
			boolean first = true;
			Map<String, Map<String, Integer>> data = CollectionFactory.createMap();
			Date thisDate = new Date(fromDate.getTime());
			while(( thisDate.before(toDate) || thisDate.equals(toDate)) && xCat.size() <= 35) {
				Map<String, Integer> day = CollectionFactory.createMap();
				for( int i = 0; i < 24; i++) {
					day.put(df.format(i) + ":00", 0);
					if( first ) {
						yCat.add(df.format(i) + ":00");
					}
				}
				first = false;
				data.put(getDateName(thisDate), day);
				xCat.add(getDateName(thisDate));
				thisDate = new Date(thisDate.getTime() + ONE_DAY);
			}

			if( thisDate.before(toDate)) toDate = new Date(thisDate.getTime());
			
			// Obtains the filtered records
			List<APUptime> list = dao.getUsingHostnameAndDates(hostname, fromDate, toDate);
			log.log(Level.INFO, list.size() + " dashboard elements found");
			

			// Writes the data segment
			for( APUptime obj : list ) {
				Map<String, Integer> day = data.get(getDateName(sdf.parse(obj.getDate())));
				Iterator<String> i = obj.getRecord().keySet().iterator();
				while(i.hasNext()) {
					String key = i.next();
					String evalKey = key.substring(0,3) + "00";
					Integer val = obj.getRecord().get(key);
					if(day == null ) {
						log.log(Level.SEVERE, getDateName(sdf.parse(obj.getDate())));
					} else {
						Integer evalVal = day.get(evalKey);
						if( evalVal == null ) log.log(Level.SEVERE, evalKey);
						evalVal += val;
						day.put(evalKey, evalVal);
					}
				}
			}
			
			// Writes the return object
			JSONObject ret = new JSONObject();
			JSONArray jsonArray = new JSONArray();

			int x = 0;
			for( String xKey : xCat ) {
				Map<String, Integer> day = data.get(xKey);
				int y = 0;
				for( String yKey : yCat ) {
					Integer val = day.get(yKey);
					JSONArray ele = new JSONArray();
					ele.put(x);
					ele.put(y);
					ele.put(roundPercentage((val * 100 / 12),2));
					jsonArray.put(ele);
					y++;
				}
				x++;
			}
			
			// Returns the final value
			ret.put("yCategories", yCat);
			ret.put("xCategories", xCat);
			ret.put("data", jsonArray);
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

	public String getDateName(Date date) {
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dof = cal.get(Calendar.DAY_OF_WEEK);
		
		switch(dof) {
		case Calendar.SUNDAY:
			sb.append("Dom ");
			break;
		case Calendar.MONDAY:
			sb.append("Lun ");
			break;
		case Calendar.TUESDAY:
			sb.append("Mar ");
			break;
		case Calendar.WEDNESDAY:
			sb.append("Mie ");
			break;
		case Calendar.THURSDAY:
			sb.append("Jue ");
			break;
		case Calendar.FRIDAY:
			sb.append("Vie ");
			break;
		case Calendar.SATURDAY:
			sb.append("Sab ");
			break;
		}

		sb.append(sdf.format(date));
		
		return sb.toString();
	}

    public static float roundPercentage(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
