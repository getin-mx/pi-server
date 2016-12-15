package mobi.allshoppings.dao.spi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.JDOCursorHelper;
import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.ExternalActivityLogDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.ExternalActivityLog;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public class ExternalActivityLogDAOJDOImpl extends GenericDAOJDO<ExternalActivityLog> implements ExternalActivityLogDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ExternalActivityLogDAOJDOImpl.class.getName());

	public ExternalActivityLogDAOJDOImpl() {
		super(ExternalActivityLog.class);
	}

	@Override
	public Key createKey() throws ASException {
		return keyHelper.createStringUniqueKey(ExternalActivityLog.class);
	}

	@Override
	public List<ExternalActivityLog> getUsingDatesAndCampaignSpecial(Date fromDate, Date toDate, String campaignSpecialId, Range range, String order) throws ASException {
		return getUsingDatesAndCampaignSpecial(null, fromDate, toDate, campaignSpecialId, range, null, true);
	}
	
	@Override
	public List<ExternalActivityLog> getUsingDatesAndCampaignSpecial(PersistenceProvider pp, Date fromDate, Date toDate, String campaignSpecialId, Range range, String order, boolean detachable) throws ASException {
		List<ExternalActivityLog> returnedObjs = new ArrayList<ExternalActivityLog>();

		PersistenceManager pm;
		if( pp == null ) {
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		} else {
			pm = pp.get();
		}

		try{
			Map<String, Object> parameters = CollectionFactory.createMap();
			List<String> declaredParams = CollectionFactory.createList();
			List<String> filters = CollectionFactory.createList();

			Query query = pm.newQuery(clazz);

			// Campaign Special Parameter
			if( campaignSpecialId != null ) {
				declaredParams.add("String campaignSpecialIdParam");
				filters.add("campaignSpecialId == campaignSpecialIdParam");
				parameters.put("campaignSpecialIdParam", campaignSpecialId);
			}

			// From Date Parameter
			if( fromDate != null ) {
				declaredParams.add("java.util.Date fromDateParam");
				filters.add("creationDateTime >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			// To Date Parameter
			if( fromDate != null ) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("creationDateTime <= toDateParam");
				parameters.put("toDateParam", toDate);
			}

			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			// Adds a cursor to the ranged query
			if( range != null ) {
				if( StringUtils.hasText(range.getCursor())) {
					// Query q = the same query that produced the cursor
					// String cursorString = the string from storage
					Cursor cursor = Cursor.fromWebSafeString(range.getCursor());
					Map<String, Object> extensionMap = new HashMap<String, Object>();
					extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
					query.setExtensions(extensionMap);
					query.setRange(0, (range.getTo() - range.getFrom()));
				} else {
					query.setRange(range.getFrom(), range.getTo());
				}
			}

			// Sets order as required
			if( StringUtils.hasText(order)) query.setOrdering(order);

			@SuppressWarnings("unchecked")
			List<ExternalActivityLog> objs = parameters.size() > 0 ? (List<ExternalActivityLog>)query.executeWithMap(parameters) : (List<ExternalActivityLog>)query.execute();
			if (objs != null) {
				// force to read
				for (ExternalActivityLog obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}

			// Store the cursorString
			if( range != null ) {
				Cursor cursor = JDOCursorHelper.getCursor(objs);
				if( cursor != null )
					range.setCursor(cursor.toWebSafeString());
			}

		} catch(Exception e) {
			if(!( e instanceof ASException )) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			} else {
				throw e;
			}
		} finally  {
			if( null == pp ) pm.close();
		}

		return returnedObjs;

	}
}
