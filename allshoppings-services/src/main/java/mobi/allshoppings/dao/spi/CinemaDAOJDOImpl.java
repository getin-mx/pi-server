package mobi.allshoppings.dao.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import mobi.allshoppings.dao.CinemaDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Cinema;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.JDOCursorHelper;
import com.inodes.datanucleus.model.Key;

public class CinemaDAOJDOImpl extends GenericDAOJDO<Cinema> implements CinemaDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CinemaDAOJDOImpl.class.getName());

	public CinemaDAOJDOImpl() {
		super(Cinema.class);
	}

	@Override
	public Key createKey(String identifier) throws ASException {
		return keyHelper.obtainKey(Cinema.class, identifier);
	}
	
	/**
	 * Get a list of instances of this entity according to a selected brand Identifier.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param brandId
	 *            A Brand identifier to search for
	 * @param range
	 *            The range object that bounds the query limits
	 * @param order
	 *            Property to use as order. If null, sets default order
	 */
	@Override
	public List<Cinema> getUsingBrandAndRange(String brandId, Range range, String order) throws ASException {
		return getUsingBrandAndRange(null, brandId, range, order, true);
	}

	/**
	 * Get a list of instances of this entity according to a selected brand Identifier.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param brandId
	 *            A Brand identifier to search for
	 * @param range
	 *            The range object that bounds the query limits
	 * @param order
	 *            Property to use as order. If null, sets default order
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<Cinema> getUsingBrandAndRange(PersistenceProvider pp, String brandId, Range range, String order, boolean detachable) throws ASException {
		return getUsingBrandAndStatusAndRange(pp, brandId, null, range, order, detachable);
	}
	
	/**
	 * Get a list of instances of this entity according to a selected brand Identifier.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param brandId
	 *            A Brand identifier to search for
	 * @param statuses
	 *            A List of valid statuses
	 * @param range
	 *            The range object that bounds the query limits
	 * @param order
	 *            Property to use as order. If null, sets default order
	 */
	@Override
	public List<Cinema> getUsingBrandAndStatusAndRange(String brandId, List<Integer> statuses, Range range, String order) throws ASException {
		return getUsingBrandAndStatusAndRange(null, brandId, statuses, range, order, true);
	}
	
	/**
	 * Get a list of instances of this entity according to a selected brand Identifier.<br>
	 * This is mostly preferred against {@link #getAll()}
	 * 
	 * @param pp
	 *            A JDO Persitence Provider to be used in the transaction. If
	 *            this value is null, then a new Persistence Manager will be
	 *            created
	 * @param brandId
	 *            A Brand identifier to search for
	 * @param statuses
	 *            A List of valid statuses
	 * @param range
	 *            The range object that bounds the query limits
	 * @param order
	 *            Property to use as order. If null, sets default order
	 * @param detachable
	 *            Flag to determine if the entity is returned detached (true) or
	 *            attached (false) to a JDO Session
	 */
	@Override
	public List<Cinema> getUsingBrandAndStatusAndRange(PersistenceProvider pp, String brandId, List<Integer> statuses, Range range, String order, boolean detachable) throws ASException {

		List<Cinema> returnedObjs = new ArrayList<Cinema>();

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
			
			Query query = pm.newQuery(Cinema.class);

			// Status filter
			filters.add(toListFilterCriteria("status", statuses, false));

			// Shopping parameters
			if( StringUtils.hasText(brandId)) {
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
			List<Cinema> objs = (List<Cinema>)query.executeWithMap(parameters);
			if (objs != null) {
				// force to read
				for (Cinema obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
			}

			// Store the cursorString
			if( range != null ) {
				Cursor cursor = JDOCursorHelper.getCursor(objs);
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
