package mobi.allshoppings.dao.spi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.APUptimeDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APUptime;
import mobi.allshoppings.tools.CollectionFactory;

public class APUptimeDAOJDOImpl extends GenericDAOJDO<APUptime> implements APUptimeDAO {
	
	private static final Logger log = Logger.getLogger(APUptimeDAOJDOImpl.class.getName());

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public APUptimeDAOJDOImpl() {
		super(APUptime.class);
	}

	@Override
	public Key createKey(String hostname, Date date) throws ASException {
		return keyHelper.obtainKey(APUptime.class, hostname + "-" + sdf.format(date));
	}
	
	/**
	 * Gets an APUptime instance using hostname and date
	 * 
	 * @param hostname
	 *            The referenced APDevice hostname
	 * @param date
	 *            The referenced date
	 */
	@Override
	public APUptime getUsingHostnameAndDate(String hostname, Date date) throws ASException {

		if (!StringUtils.hasText(hostname) || hostname.equals("null")) {
			log.info("not accepted:hostname null");
			throw ASExceptionHelper.notAcceptedException();
		}
		
		if( date == null ) {
			log.info("not accepted:date null");
			throw ASExceptionHelper.notAcceptedException();
		}

		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {
			Key key = keyHelper.obtainKey(APUptime.class, hostname + "-" + sdf.format(date));
			APUptime bs = pm.getObjectById(clazz, key);

			return pm.detachCopy(bs);

		} catch(Exception e) {
			if (!(e instanceof JDOObjectNotFoundException)) {
				log.log(Level.SEVERE, "exception catched", e);
				throw e;
			}
			throw ASExceptionHelper.notFoundException();
			
		} finally  {
			pm.close();
		}
	}

	/**
	 * Gets an APUptime instance using hostname and date range
	 * 
	 * @param hostname
	 *            The referenced APDevice hostname
	 * @param dateFrom
	 *            The referenced from date
	 * @param dateTo
	 *            The referenced to date
	 */
	@Override
	public List<APUptime> getUsingHostnameAndDates(String hostname, Date fromDate, Date toDate) throws ASException {

		List<APUptime> ret = CollectionFactory.createList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		if (!StringUtils.hasText(hostname) || hostname.equals("null")) {
			log.info("not accepted:hostname null");
			throw ASExceptionHelper.notAcceptedException();
		}
		
		if( fromDate == null ) {
			log.info("not accepted:date null");
			throw ASExceptionHelper.notAcceptedException();
		}

		if( toDate == null ) {
			log.info("not accepted:date null");
			throw ASExceptionHelper.notAcceptedException();
		}


		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {
			
			Query query = pm.newQuery(APUptime.class);

			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			declaredParams.add("String hostnameParam");
			filters.add("hostname == hostnameParam");
			parameters.put("hostnameParam", hostname);

			declaredParams.add("String dateFromParam");
			filters.add("date >= dateFromParam");
			parameters.put("dateFromParam", sdf.format(fromDate));

			declaredParams.add("String dateToParam");
			filters.add("date <= dateToParam");
			parameters.put("dateToParam", sdf.format(toDate));

			// Setting query parameters
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			query.setOrdering("date");

			@SuppressWarnings("unchecked")
			List<APUptime> objs = (List<APUptime>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (APUptime obj : objs) {
					ret.add(pm.detachCopy(obj));
				}
			}
			
		} catch(Exception e) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
	    } finally  {
	    	pm.close();
	    }
		
		return ret;
	}

}
