package mobi.allshoppings.bz.spi;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.bz.TableListBzService;
import mobi.allshoppings.bz.spi.fields.BzFields;
import mobi.allshoppings.dao.AreaDAO;
import mobi.allshoppings.dao.OfferTypeDAO;
import mobi.allshoppings.dao.ServiceDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Area;
import mobi.allshoppings.model.OfferType;
import mobi.allshoppings.model.Service;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.tools.CollectionUtils;
import mobi.allshoppings.uec.UserEntityCacheBzService;

/**
 *
 */
public class LocalTablesListBzServiceJSONImpl extends RestBaseServerResource implements TableListBzService {

    private static final Logger log = Logger.getLogger(LocalTablesListBzServiceJSONImpl.class.getName());

    @Autowired
    private ServiceDAO serviceDao;
    @Autowired
    private AreaDAO areaDao;
    @Autowired
    private OfferTypeDAO offerTypeDao;
    @Autowired
    private UserEntityCacheBzService uecService;
    @Autowired
    private SystemConfiguration systemConfiguration;
    
    private BzFields serviceBzFields = BzFields.getBzFields(ServiceListBzServiceJSONImpl.class);
    private BzFields areaBzFields = BzFields.getBzFields(AreaListBzServiceJSONImpl.class);
    private BzFields offerTypeBzFields = BzFields.getBzFields(OfferTypeListBzServiceJSONImpl.class);

    @Override
    public String retrieve() {
    	
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			// validate authToken
			User user = null;
			try {
				this.getUserFromToken();
			} catch(Exception e ) {}
			
			String lang = this.obtainLang();

			// get level, if not defined use default value
			String level = this.obtainStringValue(LEVEL, BzFields.LEVEL_LIST);

			// Retrieve the defined entities
			List<Service> serviceList = serviceDao.getAll(true);
			List<Area> areaList = areaDao.getAll(true);
			List<OfferType> offerTypeList = offerTypeDao.getAll(true);
			List<String> countriesList = new ArrayList<String>();
			UserEntityCache uec = uecService.getCountryList();
			countriesList.addAll((uec == null || CollectionUtils.isEmpty(uec.getEntities()))
					? Arrays.asList(new String[] {systemConfiguration.getDefaultCountry()}) 
							: uec.getEntities());

			log.info("Number of services found [" + serviceList.size() + "]");
			log.info("Number of areas found [" + areaList.size() + "]");
			log.info("Number of offerTypes found [" + offerTypeList.size() + "]");
			log.info("Number of countries found [" + countriesList.size() + "]");

			// retrieve all offerTypes
			returnValue = new JSONObject();
			returnValue.put("services", this.getJSONRepresentationFromArrayOfObjects(serviceList, this.obtainOutputFields(serviceBzFields, level), lang));
			returnValue.put("areas", this.getJSONRepresentationFromArrayOfObjects(areaList, this.obtainOutputFields(areaBzFields, level), lang));
			returnValue.put("offerTypes", this.getJSONRepresentationFromArrayOfObjects(offerTypeList, this.obtainOutputFields(offerTypeBzFields, level), lang));
			returnValue.put("countries", countriesList);
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.LocalTablesListBzService"),
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
