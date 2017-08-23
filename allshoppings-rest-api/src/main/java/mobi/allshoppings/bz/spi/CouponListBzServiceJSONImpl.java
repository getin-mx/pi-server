package mobi.allshoppings.bz.spi;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.CouponListBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.CampaignActivityDAO;
import mobi.allshoppings.dao.CampaignActionDAO;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.CampaignActivity;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.adapter.CouponAdapter;
import mobi.allshoppings.model.adapter.GenericAdapterImpl;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

/**
 *
 */
public class CouponListBzServiceJSONImpl extends RestBaseServerResource implements CouponListBzService {

    private static final Logger log = Logger.getLogger(CouponListBzServiceJSONImpl.class.getName());

    @Autowired
    private CampaignActivityDAO caDao;
    @Autowired
    private CampaignActionDAO csDao;
    @Autowired
    private DeviceInfoDAO diDao;
    @Autowired
    private UserDAO userDao;
    
    private BzFields bzFields = BzFields.getBzFields(getClass());
    
    @Override
    public String retrieve() {
    	
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<CouponAdapter> coupons = new ArrayList<CouponAdapter>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);
			Range range = this.obtainRange();
			
			// set adapter options
			Map<String,Object> options = CollectionFactory.createMap();
			options.put(CouponAdapter.OPTIONS_CSDAO, csDao);

			// Get Search query
			String q = this.obtainStringValue(Q, null);
			
			// retrieve all offers
			long millisPre = new Date().getTime();

			List<Integer> redeemStatuses = new ArrayList<Integer>();
			redeemStatuses.add(CampaignActivity.REDEEM_STATUS_ACCEPTED);
			redeemStatuses.add(CampaignActivity.REDEEM_STATUS_DELIVERED);
			redeemStatuses.add(CampaignActivity.REDEEM_STATUS_REDEEMED);

			// get the real user
			User realUser = user;
			if( user.getSecuritySettings().getRole().equals(Role.APPLICATION) && getParameters().containsKey("deviceUUID")) {
				String realDeviceId = (String)getParameters().get("deviceUUID");
				if( StringUtils.hasText(realDeviceId)) {
					DeviceInfo di = diDao.get(realDeviceId);
					if( StringUtils.hasText(di.getUserId()) ) {
						realUser = userDao.get(di.getUserId());
					} else {
						if( realDeviceId.endsWith(di.getAppId())) {
							realUser = userDao.get(realDeviceId);
						} else {
							realUser = userDao.get(realDeviceId + "@" + di.getAppId());
						}
					}
				}
			}

			boolean onlyDisplayableItems = false;
			if(getParameters().containsKey("activeOnly")) {
				onlyDisplayableItems = true;
			}
				
			// and the coupon list
			coupons = new GenericAdapterImpl<CouponAdapter>().adaptList(
					caDao.getUsingUserAndRedeemStatusAndRange(null, realUser.getIdentifier(), 
							redeemStatuses, onlyDisplayableItems, range, "creationDateTime desc", true), 
					realUser.getIdentifier(), CouponAdapter.class, null, options);
			long diff = new Date().getTime() - millisPre;

			// check for returning only active coupons
			if(getParameters().containsKey("activeOnly")) {
				List<CouponAdapter> other = new ArrayList<CouponAdapter>();
				other.addAll(coupons);
				coupons.clear();
				Date now = new Date();
				for( CouponAdapter o : other ) {
					if( o.getLimitDateTime() == null || o.getLimitDateTime().after(now))
						coupons.add(o);
				}
			}
			
			// Logs the result
			log.info("Number of coupons found [" + coupons.size() + "] in " + diff + " millis");
			returnValue = this.getJSONRepresentationFromArrayOfObjects(	coupons, this.obtainOutputFields(bzFields, level));
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.CouponListBzService"),
					q, null);

		} catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = this.getJSONRepresentationFromException(e);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = this.getJSONRepresentationFromException(e);
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
    }

}
