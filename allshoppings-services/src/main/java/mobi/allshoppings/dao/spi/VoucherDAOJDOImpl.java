package mobi.allshoppings.dao.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.datastore.JDOConnection;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.JDOCursorHelper;
import com.inodes.datanucleus.model.Key;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

import mobi.allshoppings.dao.VoucherDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Voucher;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public class VoucherDAOJDOImpl extends GenericDAOJDO<Voucher> implements VoucherDAO {

	private static final Logger log = Logger.getLogger(VoucherDAOJDOImpl.class.getName());

	public VoucherDAOJDOImpl() {
		super(Voucher.class);
	}

	@Override
	public Key createKey(String identifier) throws ASException {
		return keyHelper.obtainKey(Voucher.class, identifier);
	}

	@Override
	public Voucher getNextAvailable(String type) throws ASException {
		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try {

			pm.currentTransaction().begin();

			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();

			try {
				final BasicDBObject query = new BasicDBObject("$and", Arrays.asList(new BasicDBObject("type", type), new BasicDBObject("status", 1)));
				final BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("status", 2));
				final DBObject modifiedDoc = db.getCollection("Voucher")
						.findAndModify(query, null, null, false, update, true,
								false);
				if( modifiedDoc == null ) throw ASExceptionHelper.defaultException("Voucher is null!!", null);
				String identifier = modifiedDoc.get("code").toString(); 
				jdoConn.close();
				pm.currentTransaction().commit();

				return get(identifier, true);
			} catch( Exception e ) {
				jdoConn.close();
				pm.currentTransaction().rollback();
				throw e;
			}
		} catch(Exception e) {
			log.log(Level.SEVERE, "exception catched", e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally  {
			pm.close();
		}
	}

	@Override
	public Long getNextSequence() throws ASException {

		PersistenceManager pm;
		pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

		try {

			pm.currentTransaction().begin();

			JDOConnection jdoConn = pm.getDataStoreConnection();
			DB db = (DB)jdoConn.getNativeConnection();

			final BasicDBObject query = new BasicDBObject("_id", "vouchertxid");
			final BasicDBObject update = new BasicDBObject("$inc", new BasicDBObject("seq", 1));
			final DBObject modifiedDoc = db.getCollection("counters")
					.findAndModify(query, null, null, false, update, true,
							false);
			long seq = Double.valueOf(modifiedDoc.get("seq").toString()).longValue();
			jdoConn.close();
			pm.currentTransaction().commit();

			return seq;

		} catch(Exception e) {
			log.log(Level.SEVERE, "exception catched", e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		} finally  {
			pm.close();
		}
	}

	@Override
	public List<Voucher> getUsingStatusAndBrandAndType(List<Integer> status, String brandId, List<String> type) throws ASException {
		return getUsingStatusAndBrandAndType(null, status, brandId, type, null, null, true);
	}

	@Override
	public List<Voucher> getUsingStatusAndBrandAndType(PersistenceProvider pp, List<Integer> status, String brandId, List<String> type, Range range, String order, boolean detachable) throws ASException {
		List<Voucher> returnedObjs = new ArrayList<Voucher>();

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

			filters.add(toListFilterCriteria("status", status, false));
			filters.add(toListFilterCriteria("type", type, true));

			// Last update parameter
			if( brandId != null ) {
				declaredParams.add("String brandIdParam");
				filters.add("brandId == brandIdParam");
				parameters.put("brandIdParam", brandId);
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
			List<Voucher> objs = parameters.size() > 0 ? (List<Voucher>)query.executeWithMap(parameters) : (List<Voucher>)query.execute();
			if (objs != null) {
				// force to read
				for (Voucher obj : objs) {
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

	@Override
	public List<Voucher> getUsingDatesAndType(Date fromDate, Date toDate, List<String> type, Range range, String order) throws ASException {
		return getUsingDatesAndType(null, fromDate, toDate, type, range, null, true);
	}

	@Override
	public List<Voucher> getUsingDatesAndType(PersistenceProvider pp, Date fromDate, Date toDate, List<String> type, Range range, String order, boolean detachable) throws ASException {
		List<Voucher> returnedObjs = new ArrayList<Voucher>();

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

			// From Date Parameter
			if( fromDate != null ) {
				declaredParams.add("java.util.Date fromDateParam");
				filters.add("assignationDate >= fromDateParam");
				parameters.put("fromDateParam", fromDate);
			}

			// To Date Parameter
			if( fromDate != null ) {
				declaredParams.add("java.util.Date toDateParam");
				filters.add("assignationDate <= toDateParam");
				parameters.put("toDateParam", toDate);
			}

			filters.add(toListFilterCriteria("type", type, true));

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
			List<Voucher> objs = parameters.size() > 0 ? (List<Voucher>)query.executeWithMap(parameters) : (List<Voucher>)query.execute();
			if (objs != null) {
				// force to read
				for (Voucher obj : objs) {
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
