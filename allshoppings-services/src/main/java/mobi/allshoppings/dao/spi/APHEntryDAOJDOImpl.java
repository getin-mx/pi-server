package mobi.allshoppings.dao.spi;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.datastore.JDOConnection;

import org.apache.commons.collections.CollectionUtils;

import com.inodes.datanucleus.model.Key;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;

import mobi.allshoppings.dao.APHEntryDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;

public class APHEntryDAOJDOImpl extends GenericDAOJDO<APHEntry> implements APHEntryDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(APHEntryDAOJDOImpl.class.getName());

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public APHEntryDAOJDOImpl() {
		super(APHEntry.class);
	}

	@Override
	public Key createKey(APHEntry obj) throws ASException {
		String hashKey = obj.getMac() + ":" + obj.getHostname() + ":" + obj.getDate();
		return keyHelper.obtainKey(APHEntry.class, hashKey);
	}
	
	@Override
	public List<APHEntry> getUsingHostnameAndDates(List<String> hostname, Date fromDate, Date toDate, Range range, boolean detachable) throws ASException {

		List<APHEntry> ret = CollectionFactory.createList();
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// Hostname Parameter
			if(!CollectionUtils.isEmpty(hostname)) {
				declaredParams.add("java.util.List hostnameParam");
				filters.add("hostnameParam.contains(hostname)");
				parameters.put("hostnameParam", hostname);
			}

			// fromDate Parameter
			if(null != fromDate) {
				declaredParams.add("String fromDateParam");
				filters.add("date >= fromDateParam");
				parameters.put("fromDateParam", sdf.format(fromDate));
			}

			// toDate Parameter
			if(null != fromDate) {
				declaredParams.add("String toDateParam");
				filters.add("date <= toDateParam");
				parameters.put("toDateParam", sdf.format(toDate));
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));
			
			if( null != range )
				query.setRange(range.getFrom(), range.getTo());
			
			@SuppressWarnings("unchecked")
			List<APHEntry> list = (List<APHEntry>)query.executeWithMap(parameters);
			for(APHEntry obj : list ) {
				if( detachable )
					ret.add(pm.detachCopy(obj));
				else
					ret.add(obj);
			}
			
			query.closeAll();
			return ret;
			
		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			pm.close();
		}

	}

	@Override
	public List<String> getMacsUsingHostnameAndDates(List<String> hostname, Date fromDate, Date toDate) throws ASException {
		
		List<String> ret = CollectionFactory.createList();
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		
		try{

			// Obtains DB Connection
			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();

			BasicDBObject query = new BasicDBObject("$and", Arrays.asList(
					new BasicDBObject("hostname", new BasicDBObject("$in", hostname.toArray(new String[hostname.size()]))),
					new BasicDBObject("date", sdf.format(fromDate))
					));

			@SuppressWarnings("unchecked")
			Iterator<String> c = db.getCollection("APHEntry").distinct("mac",query).iterator();
			while(c.hasNext()) {
				ret.add(c.next());
			}
			jdoConn.close();

			return ret;
			
		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			pm.close();
		}
	}

}
