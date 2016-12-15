package mobi.allshoppings.bz.spi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.DeviceInfoBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.DeviceInfoDAO;
import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DeviceInfo;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.KeyHelper;
import mobi.allshoppings.model.tools.ViewLocation;


/**
 *
 */
public class DeviceInfoBzServicePIJSONImpl
        extends RestBaseServerResource
        implements DeviceInfoBzService {

    private static final Logger log = Logger.getLogger(DeviceInfoBzServicePIJSONImpl.class.getName());
    
    @Autowired
    private DeviceInfoDAO dao;
    @Autowired
    private KeyHelper keyHelper;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private SystemConfiguration systemConfiguration;

    private BzFields bzFields = BzFields.getBzFields(getClass());

    @Override
    public String post(final JsonRepresentation entity) {

    	long start = markStart();
    	try {

    		String userId = obtainUserIdentifier(false);
    		if( userId == null ) {
    			try {
    				userId = getUserFromToken().getIdentifier();
    			} catch( Exception e ) {}
    		}
            final JSONObject obj = entity.getJsonObject();
        	JSONObject returnValue;

            //check mandatory fields
            log.info("check mandatory fields");
            if (!hasDefaultParameters(obj, bzFields.DEFAULT_FIELDS.toArray(EMPTY_STRING_ARRAY))) {
                throw ASExceptionHelper.invalidArgumentsException(INVALID_DEFAULT_FIELDS);
            }

            boolean newDevice = false;
            DeviceInfo deviceInfo = null;
            try {
            	deviceInfo = dao.get(obj.get("deviceUUID").toString(), true);
            } catch( ASException e ) {
            	if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
            		newDevice = true;
            		deviceInfo = new DeviceInfo();
    				keyHelper.setKeyWithIdentifier(deviceInfo, obj.get("deviceUUID").toString());
            	} else {
            		throw e;
            	}
            }
            
            setPropertiesFromJSONObject(obj, deviceInfo, bzFields.READONLY_FIELDS);
            if( deviceInfo.getApiVersion() == null ) deviceInfo.setApiVersion("10000");
            if( deviceInfo.getAppId() == null ) deviceInfo.setAppId(systemConfiguration.getDefaultAppId());
            
            if( userId != null ) {
            	try {
            		User user = userDao.get(userId, true);
            		if( user != null && user.getSecuritySettings().getRole().equals(Role.APPLICATION)) {
            			deviceInfo.setAppId(user.getIdentifier());
            			String appInternalUserId = null;
            			if( obj.has("userEmail")) {
            				appInternalUserId = obj.getString("userEmail");
            			} else {
							appInternalUserId = deviceInfo.getDeviceUUID()
									.contains("@" + user.getIdentifier()) 
									? deviceInfo.getDeviceUUID() 
									: deviceInfo.getDeviceUUID() + "@" + user.getIdentifier();
            			}
            			
            			deviceInfo.setUserId(appInternalUserId);
            			
            			
            			// Manages the app user
            			User appInternalUser = null;
            			try {
            				appInternalUser = userDao.get(appInternalUserId, true);
            			} catch(ASException e ) {
            				try {
            					appInternalUser = new User();
            					keyHelper.setKeyWithIdentifier(appInternalUser, appInternalUserId);
            					appInternalUser.setEmail(appInternalUserId);
            					ViewLocation vl = new ViewLocation();
            					vl.setCountry(user.getViewLocation().getCountry());
            					appInternalUser.setViewLocation(vl);
            				} catch(Exception e1 ) {
            					log.log(Level.SEVERE, e1.getMessage(), e1);
            				}
            			}
    					
            			appInternalUser.setFirstname(obj.has("userFirstname") ? obj.getString("userFirstname") : "");
    					appInternalUser.setLastname(obj.has("userLastname") ? obj.getString("userLastname") : "");
    					appInternalUser.setGender(obj.has("userGender") ? obj.getString("userGender") : "");
    					try {
    						appInternalUser.setBirthDate(obj.has("userBirthdate") ? new SimpleDateFormat("yyyy-MM-dd").parse(obj.getString("userBirthdate")) : null);
    					} catch( Exception e1 ) {}
            			appInternalUser.setLastLogin(new Date());

            			userDao.createOrUpdate(appInternalUser);
            			
            		} else {
            			deviceInfo.setUserId(userId);
            		}
            		if(user != null) deviceInfo.setUserName(user.getFullname());
            	} catch( ASException e ) {
            		deviceInfo.setUserId(userId);
            	}

            } else if(!StringUtils.hasText(deviceInfo.getUserId())) {

    			String appInternalUserId = null;
    			if( obj.has("userEmail")) {
    				appInternalUserId = obj.getString("userEmail");
    			} else {
					appInternalUserId = deviceInfo.getDeviceUUID()
							.contains("@" + deviceInfo.getAppId()) 
							? deviceInfo.getDeviceUUID() 
							: deviceInfo.getDeviceUUID() + "@" + deviceInfo.getAppId();
    			}
    			
    			deviceInfo.setUserId(appInternalUserId);
    			
    			// Manages the app user
    			User appInternalUser = null;
    			try {
    				appInternalUser = userDao.get(appInternalUserId, true);
    			} catch(ASException e ) {
    				try {
    					appInternalUser = new User();
    					keyHelper.setKeyWithIdentifier(appInternalUser, appInternalUserId);
    					appInternalUser.setEmail(appInternalUserId);
    					ViewLocation vl = new ViewLocation();
    					vl.setCountry(systemConfiguration.getDefaultCountry());
    					appInternalUser.setViewLocation(vl);
    				} catch(Exception e1 ) {
    					log.log(Level.SEVERE, e1.getMessage(), e1);
    				}
    			}
				
    			appInternalUser.setFirstname(obj.has("userFirstname") ? obj.getString("userFirstname") : "");
				appInternalUser.setLastname(obj.has("userLastname") ? obj.getString("userLastname") : "");
				appInternalUser.setGender(obj.has("userGender") ? obj.getString("userGender") : "");
				try {
					appInternalUser.setBirthDate(obj.has("userBirthdate") ? new SimpleDateFormat("yyyy-MM-dd").parse(obj.getString("userBirthdate")) : null);
				} catch( Exception e1 ) {}
    			appInternalUser.setLastLogin(new Date());
   				userDao.createOrUpdate(appInternalUser);
            }
            
            // Check token for sandbox in APNS
            if( StringUtils.hasText(deviceInfo.getMessagingToken()) && deviceInfo.getMessagingToken().split(";;").length > 1 ) {
            	String[] parts = deviceInfo.getMessagingToken().split(";;");
            	deviceInfo.setMessagingToken(parts[0]);
            	if("SANDBOX".equals(parts[1])) {
            		deviceInfo.setMessagingSandbox(true);
            	} else {
            		deviceInfo.setMessagingSandbox(false);
            	}
            }
            
            deviceInfo.setStatus(StatusAware.STATUS_ENABLED);

            if( newDevice ) {
            	dao.create(deviceInfo);
            } else {
            	dao.update(deviceInfo);
            }

    		// Obtains the user JSON representation
    		final String[] fields = obtainOutputFields(bzFields, "all_public");
    		returnValue = getJSONRepresentationFromObject(deviceInfo, fields);

            log.info("update device info end");
        	return returnValue.toString();

        } catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
            return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
        } catch (ASException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
            return getJSONRepresentationFromException(e).toString();
        } finally {
        	markEnd(start);
        }

    }

}
