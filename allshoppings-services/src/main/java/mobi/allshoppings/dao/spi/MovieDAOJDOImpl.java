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

import mobi.allshoppings.dao.MovieDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Movie;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

public class MovieDAOJDOImpl extends GenericDAOJDO<Movie> implements MovieDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(MovieDAOJDOImpl.class.getName());

	public MovieDAOJDOImpl() {
		super(Movie.class);
	}

	@Override
	public Key createKey(String identifier) throws ASException {
		return keyHelper.obtainKey(Movie.class, identifier);
	}

	@Override
	public void disableOldMovies(Date referenceDate) throws ASException {
		List<Movie> list = getUsingLastUpdateStatusAndRange(null, referenceDate, false, null, null, null, null, true);
		for( Movie obj : list ) {
			obj.setStatus(StatusAware.STATUS_DISABLED);
			update(obj);
		}
	}

	@Override
	public List<Movie> getUsingBrandAndStatusAndRange(String brand, List<Integer> status, Range range, String order) throws ASException {
		return getUsingBrandAndStatusAndRange(null, brand, status, range, order, true);
	}
	
	@Override
	public List<Movie> getUsingBrandAndStatusAndRange(PersistenceProvider pp, String brand, List<Integer> status, Range range, String order, boolean detachable) throws ASException {
		List<Movie> returnedObjs = new ArrayList<Movie>();

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

			if( isThisClassSafeForStatusFiltering() && status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

			// Last update parameter
			if( StringUtils.hasText(brand) ) {
				declaredParams.add("String brandIdParam");
				filters.add("brandId == brandIdParam");
				parameters.put("brandIdParam", brand);
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
			List<Movie> objs = parameters.size() > 0 ? (List<Movie>)query.executeWithMap(parameters) : (List<Movie>)query.execute();
			if (objs != null) {
				// force to read
				for (Movie obj : objs) {
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
