package mobi.allshoppings.bz.custom.web;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import mobi.allshoppings.bz.web.BaseWebController;
import mobi.allshoppings.dao.CampaignActivityDAO;
import mobi.allshoppings.dao.CampaignSpecialDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.CampaignSpecial;
import mobi.allshoppings.model.User;

import org.apache.commons.lang.time.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomPromotionWebController extends BaseWebController {

	private static final Logger log = Logger.getLogger(CustomPromotionWebController.class.getName());

	@Autowired
	private CampaignActivityDAO dao;
	@Autowired
	private CampaignSpecialDAO csDao;
	@Autowired
	private UserDAO userDao;
	
	private static final DecimalFormat df = new DecimalFormat("#0.00");

	@RequestMapping("/custom/promotions/{customName}/{customModule}/{identifier}")
	public String user(@PathVariable String customName, @PathVariable String customModule, @PathVariable String identifier, HttpServletRequest request) {
		try {

			CampaignActivity ca = identifier.equals("last") ? dao.getLast() : dao.get(identifier, true);
			CampaignSpecial cs = csDao.get(ca.getCampaignSpecialId());
			User app = userDao.get(customName);
			
			JSONObject extras;
			try {
				extras = new JSONObject(ca.getExtras().getValue());
			} catch( Exception e ) {
				extras = new JSONObject();
			}

			if( ca.getViewDateTime() == null ) {
				ca.setViewDateTime(new Date());
				dao.update(ca);
			}
			
			request.setAttribute("ca", ca);
			request.setAttribute("app", app.getSecuritySettings().getAuthToken());
			request.setAttribute("usr", ca.getUserId());
			
			request.setAttribute("name", cs.getName());
			request.setAttribute("description", cs.getDescription());
			request.setAttribute("avatarId", cs.getAvatarId());
			
			if( extras.has("name")) request.setAttribute("name", extras.getString("name"));
			if( extras.has("format")) request.setAttribute("format", extras.getString("format"));
			if( extras.has("screen")) request.setAttribute("screen", extras.getString("screen"));
			if( extras.has("rate")) request.setAttribute("rate", extras.getString("rate"));
			if( extras.has("length")) request.setAttribute("length", extras.getString("length"));
			if( extras.has("movieGender")) request.setAttribute("movieGender", extras.getString("movieGender"));
			if( extras.has("cinema")) request.setAttribute("cinema", extras.getString("cinema"));
			if( extras.has("price")) request.setAttribute("price", "$" + df.format(Double.valueOf(extras.get("price").toString())));
			if( extras.has("description")) request.setAttribute("description", extras.getString("description"));
			if( extras.has("avatarId")) request.setAttribute("avatarId", extras.getString("avatarId"));
			if( extras.has("availableSeats")) request.setAttribute("availableSeats", extras.get("availableSeats"));
			if( extras.has("showDateTime")) request.setAttribute("showDateTime", getFormattedDate(new Date(extras.getLong("showDateTime")), "es"));
			if( extras.has("validIn")) request.setAttribute("validIn", extras.get("validIn"));
			if( extras.has("validFrom")) request.setAttribute("validFrom", getFormattedDate2(new Date(extras.getLong("validFrom")), "es"));

			request.setAttribute("couponCount", extras.has("couponCount") ? extras.getInt("couponCount") : 0);
			request.setAttribute("couponCode1", ca.getCouponCode());
			request.setAttribute("couponCode2", extras.has("extraCoupon1") ? extras.getString("extraCoupon1") : null);
			request.setAttribute("couponCode3", extras.has("extraCoupon2") ? extras.getString("extraCoupon2") : null);
			
			// returns the profile
			return "/custom/promotions/" + customName + "/" + customModule;

		} catch( ASException e ) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
				return R404;
			} else {
				log.log(Level.SEVERE, "Exception catched: " + e, e);
				throw new RuntimeException(e);
			}
		} catch( JSONException e ) {
			log.log(Level.SEVERE, "Exception catched: " + e, e);
			throw new RuntimeException(e);
		}
	}
	
	private String getFormattedDate(Date date, String locale) {
		
		StringBuffer sb = new StringBuffer();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
    	Date today = DateUtils.truncate(new Date(), Calendar.DATE);
    	if( today.equals(DateUtils.truncate(date, Calendar.DATE))) {
    		sb.append("Hoy, ");
    	}
    	
    	sb.append(getWeekDay(calendar.get(Calendar.DAY_OF_WEEK), locale));
    	sb.append(" ");
    	sb.append(calendar.get(Calendar.DAY_OF_MONTH));
    	sb.append(" de ");
    	sb.append(getMonth(calendar.get(Calendar.MONTH), locale).toLowerCase());
    	sb.append(", ");
    	sb.append(new DecimalFormat("00").format(calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR)));
    	sb.append(":");
    	sb.append(new DecimalFormat("00").format(calendar.get(Calendar.MINUTE)));
		if( calendar.get(Calendar.HOUR_OF_DAY) >= 12 ) {
			sb.append(" p.m.");
		} else {
			sb.append(" a.m.");
		}
		
		return sb.toString();
	}

	private String getFormattedDate2(Date date, String locale) {
		
		StringBuffer sb = new StringBuffer();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
    	Date today = DateUtils.truncate(new Date(), Calendar.DATE);
    	if( today.equals(DateUtils.truncate(date, Calendar.DATE))) {
    		sb.append("Canjeable hoy, ");
    	}

    	sb.append(getWeekDay(calendar.get(Calendar.DAY_OF_WEEK), locale));
    	sb.append(" ");
    	sb.append(calendar.get(Calendar.DAY_OF_MONTH));
    	sb.append(" de ");
    	sb.append(getMonth(calendar.get(Calendar.MONTH), locale).toLowerCase());
    	sb.append(" de ");
    	sb.append(calendar.get(Calendar.YEAR));
    	sb.append(", de 02:00 p.m. a 04:00 p.m.");
		
		return sb.toString();
	}

	private String getWeekDay(int day, String locale) {
		switch (day) {
		case Calendar.SUNDAY:
			return "domingo";
		case Calendar.MONDAY: 
			return "lunes";
		case Calendar.TUESDAY:
			return "martes";
		case Calendar.WEDNESDAY:
			return "mi&eacute;rcoles";
		case Calendar.THURSDAY:
			return "jueves";
		case Calendar.FRIDAY:
			return "viernes";
		case Calendar.SATURDAY:
			return "s&aacute;bado";
		default:
			return null;
		}
	}

	private String getMonth(int month, String locale) {
		switch (month) {
		case 0:
			return "Enero";
		case 1: 
			return "Febrero";
		case 2:
			return "Marzo";
		case 3:
			return "Abril";
		case 4:
			return "Mayo";
		case 5:
			return "Junio";
		case 6:
			return "Julio";
		case 7:
			return "Agosto";
		case 8: 
			return "Septiembre";
		case 9: 
			return "Octubre";
		case 10:
			return "Noviembre";
		case 11: 
			return "Diciembre";
		default:
			return null;
		}
	}
}
