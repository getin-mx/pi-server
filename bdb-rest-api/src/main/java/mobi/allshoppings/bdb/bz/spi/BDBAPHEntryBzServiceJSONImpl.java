package mobi.allshoppings.bdb.bz.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.util.CollectionFactory;

import mobi.allshoppings.bdb.bz.BDBCrudBzService;
import mobi.allshoppings.dao.APDVisitDAO;
import mobi.allshoppings.dao.APHEntryDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserSecurity.Role;
import mobi.allshoppings.tools.Range;

public class BDBAPHEntryBzServiceJSONImpl extends BDBCrudBzServiceJSONImpl<APHEntry> implements BDBCrudBzService {

	@Autowired
	private APHEntryDAO dao;

	@Autowired
	private APDVisitDAO apdvDao;

	@Override
	public String[] getListFields() {
		return new String[] {
				"identifier",
				"hostname",
				"mac",
				"date",
				"devicePlatform",
				"dataCount",
				"minRssi",
				"maxRssi",
				"visitsGenerated",
				"peasantsGenerated",
				"employeesGenerated",
				"lastUpdate",
				"creationDateTime"
		};
	}

	@Override
	public String[] getRetrieveFields() {
		return new String[] {
				"identifier",
				"hostname",
				"mac",
				"date",
				"devicePlatform",
				"dataCount",
				"minRssi",
				"maxRssi",
				"visitsGenerated",
				"peasantsGenerated",
				"employeesGenerated",
				"lastUpdate",
				"creationDateTime"
		};
	}

	@Override
	public void config() {
		setMyDao(dao);
		setMyClazz(APHEntry.class);
	}

	@Override
	public void setKey(APHEntry obj, JSONObject seed) throws ASException {
		obj.setKey(dao.createKey(obj));
	}
	
	public String list() {
    	long start = markStart();
		JSONObject returnValue = null;
		try {
			List<APHEntry> list = new ArrayList<APHEntry>();
			
			// validate authToken
			User user = this.getUserFromToken();

			// get range, if not defined use default value
			Range range = this.obtainRange();
			
			// Get Search query
			String q = this.obtainStringValue(Q, null);

			// Additional Search Fields
			Map<String, String> additionalFields = CollectionFactory.createMap();

			// Get Status query
			String status = this.obtainStringValue(STATUS, null);
	
			// Get order
			String order = this.obtainStringValue(ORDER, null);

			// Get the output fields
			String[] fields = this.obtainOutputFields(APHEntryAdapter.class, getListFields());

			// Get the language;
			String lang = this.obtainLang();
			
			Map<String,String> attributes = CollectionFactory.createMap();
			
			// Staus filter
			List<Integer> statusList = null;
			if( StringUtils.hasText(status))
				statusList = Arrays.asList(new Integer[] {Integer.parseInt(status)});

			// retrieve all elements
			long millisPre = new Date().getTime();
			if( q != null && !q.trim().equals("")) {
				
				if( StringUtils.hasText(status) )
					additionalFields.put("status", status);

				list = myDao.getUsingIndex(
								myClazz.getName(), q, user.getSecuritySettings().getRole().equals(Role.ADMIN) 
								? null : user.getViewLocation(),
								StringUtils.hasText(status) 
								? Arrays.asList(new Integer[] {Integer.valueOf(status)}) : null, range, 
								additionalFields, order, lang);
			} else {
				list = myDao.getUsingStatusAndRange(statusList, range, order, attributes, true);
			}
			
			long diff = new Date().getTime() - millisPre;

			// Logs the result
			log.info("Number of elements found [" + list.size() + "] in " + diff + " millis");
			List<APHEntryAdapter> adapted = CollectionFactory.createList();
			for( APHEntry obj : list ) {
				adapted.add(new APHEntryAdapter(obj));
			}
			
			returnValue = this.getJSONRepresentationFromArrayOfObjects(adapted, fields);
			
			if( attributes.containsKey("recordCount"))
				returnValue.put("recordCount", Long.valueOf(attributes.get("recordCount")));
			else 
				returnValue.put("recordCount", list.size());
				
			
			// track action
			trackerHelper.enqueue(user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service." + myClazz.getSimpleName() + "BzService"),
					q, null);

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

	public String retrieve()
	{
		long start = markStart();
		JSONObject returnValue;
		try {
			// obtain the id and validates the auth token
			User user = getUserFromToken();
			String identifier = obtainIdentifier(GENERAL_IDENTIFIER_KEY);
			final APHEntry obj = myDao.get(identifier);
			
			// Get the output fields
			String[] fields = this.obtainOutputFields(APHEntryAdapter.class, getRetrieveFields());

			// Obtains the user JSON representation
			returnValue = getJSONRepresentationFromObject(new APHEntryAdapter(obj), fields);

			// track action
			trackerHelper.enqueue( user, getRequestIP(),
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
			returnValue = getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e));
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
	}

	public class APHEntryAdapter extends APHEntry {
		private static final long serialVersionUID = 2085616713214351396L;

		private int visitsGenerated = 0;
		private int peasantsGenerated = 0;
		private int employeesGenerated = 0;

		public APHEntryAdapter() {
			super();
		}
		
		public APHEntryAdapter(APHEntry src) {

			this.setKey(src.getKey());
			this.setHostname(src.getHostname());
			this.setMac(src.getMac());
			this.setDate(src.getDate());
			this.setDevicePlatform(src.getDevicePlatform());
			this.setRssi(src.getRssi());
			this.setArtificialRssi(src.getArtificialRssi());
			this.setDataCount(src.getDataCount());
			this.setMinRssi(src.getMinRssi());
			this.setMaxRssi(src.getMaxRssi());
			this.setLastUpdate(src.getLastUpdate());
			this.setCreationDateTime(src.getCreationDateTime());

			try {
				Map<Integer, Integer> count = apdvDao.countUsingAPHE(this.getIdentifier());
				if( count.containsKey(APDVisit.CHECKIN_VISIT))
					visitsGenerated = count.get(APDVisit.CHECKIN_VISIT);
				if( count.containsKey(APDVisit.CHECKIN_PEASANT))
					peasantsGenerated = count.get(APDVisit.CHECKIN_PEASANT);
				if( count.containsKey(APDVisit.CHECKIN_EMPLOYEE))
					employeesGenerated = count.get(APDVisit.CHECKIN_EMPLOYEE);
			} catch( Exception e ) {
				log.log(Level.WARNING, e.getMessage(), e);
			}

		}

		/**
		 * @return the visitsGenerated
		 */
		public int getVisitsGenerated() {
			return visitsGenerated;
		}

		/**
		 * @param visitsGenerated the visitsGenerated to set
		 */
		public void setVisitsGenerated(int visitsGenerated) {
			this.visitsGenerated = visitsGenerated;
		}

		/**
		 * @return the peasantsGenerated
		 */
		public int getPeasantsGenerated() {
			return peasantsGenerated;
		}

		/**
		 * @param peasantsGenerated the peasantsGenerated to set
		 */
		public void setPeasantsGenerated(int peasantsGenerated) {
			this.peasantsGenerated = peasantsGenerated;
		}

		/**
		 * @return the employeesGenerated
		 */
		public int getEmployeesGenerated() {
			return employeesGenerated;
		}

		/**
		 * @param employeesGenerated the employeesGenerated to set
		 */
		public void setEmployeesGenerated(int employeesGenerated) {
			this.employeesGenerated = employeesGenerated;
		}
		
	}
}
