package mobi.allshoppings.dao.spi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import mobi.allshoppings.dao.ShowtimeDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Showtime;
import mobi.allshoppings.tools.CollectionFactory;
import mobi.allshoppings.tools.Range;
import mobi.allshoppings.tx.PersistenceProvider;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Cursor;
import com.inodes.datanucleus.model.JDOCursorHelper;
import com.inodes.datanucleus.model.Key;

public class ShowtimeDAOJDOImpl extends GenericDAOJDO<Showtime> implements ShowtimeDAO {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ShowtimeDAOJDOImpl.class.getName());

	private static final SimpleDateFormat internalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public ShowtimeDAOJDOImpl() {
		super(Showtime.class);
	}

	@Override
	public Key createKey(String identifier) throws ASException {
		return keyHelper.obtainKey(Showtime.class, identifier);
	}

	@Override
	public List<Showtime> getUsingCinemaAndDateAndStatusAndRange(String cinemaId, Date date, List<Integer> status, Range range, String order) throws ASException {
		return getUsingCinemaAndDateAndStatusAndRange(null, cinemaId, date, status, range, order, true);
	}
	
	@Override
	public List<Showtime> getUsingCinemaAndDateAndStatusAndRange(PersistenceProvider pp, String cinemaId, Date date, List<Integer> status, Range range, String order, boolean detachable) throws ASException {
		List<Showtime> returnedObjs = new ArrayList<Showtime>();

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

			// Status parameter
			if( isThisClassSafeForStatusFiltering() && status != null && status.size() > 0 ) {
				filters.add(toListFilterCriteria("status", status, false));
			}

			// Cinema parameter
			if( StringUtils.hasText(cinemaId) ) {
				declaredParams.add("String cinemaIdParam");
				filters.add("cinema.identifier == cinemaIdParam");
				parameters.put("cinemaIdParam", cinemaId);
			}
			
			// Date parameter
			if( date != null ) {
				declaredParams.add("String showDateParam");
				filters.add("showDate == showDateParam");
				parameters.put("showDateParam", internalDateFormat.format(date));
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
			List<Showtime> objs = parameters.size() > 0 ? (List<Showtime>)query.executeWithMap(parameters) : (List<Showtime>)query.execute();
			if (objs != null) {
				// force to read
				for (Showtime obj : objs) {
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
	
	@Override
	public List<Showtime> getUsingCinemaAndMovieAndDate(String cinemaId, String movieId, String showDate, String showTime) throws ASException {
		return getUsingCinemaAndMovieAndDate(null, cinemaId, movieId, showDate, showTime, true);
	}
	
	@Override
	public List<Showtime> getUsingCinemaAndMovieAndDate(PersistenceProvider pp, String cinemaId, String movieId, String showDate, String showTime, boolean detachable) throws ASException {
		List<Showtime> returnedObjs = new ArrayList<Showtime>();

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

			// Cinema parameter
			if( StringUtils.hasText(cinemaId) ) {
				declaredParams.add("String cinemaIdParam");
				filters.add("cinema.identifier == cinemaIdParam");
				parameters.put("cinemaIdParam", cinemaId);
			}
			
			// Movie parameter
			if( StringUtils.hasText(cinemaId) ) {
				declaredParams.add("String movieIdParam");
				filters.add("movie.identifier == movieIdParam");
				parameters.put("movieIdParam", movieId);
			}
			
			// Date parameter
			if( StringUtils.hasText(showDate) ) {
				declaredParams.add("String showDateParam");
				filters.add("showDate == showDateParam");
				parameters.put("showDateParam", showDate);
			}
			
			// Time parameter
			if( StringUtils.hasText(showTime) ) {
				declaredParams.add("String showTimeParam");
				filters.add("showTime == showTimeParam");
				parameters.put("showTimeParam", showTime);
			}
			
			query.declareParameters(toParameterList(declaredParams));
			query.setFilter(toWellParametrizedFilter(filters));

			@SuppressWarnings("unchecked")
			List<Showtime> objs = parameters.size() > 0 ? (List<Showtime>)query.executeWithMap(parameters) : (List<Showtime>)query.execute();
			if (objs != null) {
				// force to read
				for (Showtime obj : objs) {
					if( detachable )
						returnedObjs.add(pm.detachCopy(obj));
					else
						returnedObjs.add(obj);
				}
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
