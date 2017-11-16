package mobi.allshoppings.bdb.dashboard.bz.spi;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.bdb.bz.BDBTimelineHourBzService;
import mobi.allshoppings.dao.APDVisitDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class RepetitionsBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBTimelineHourBzService {

	private static final Logger log = Logger.getLogger(RepetitionsBzServiceJSONImpl.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private APDVisitDAO apdvDao;
	@Autowired
	private StoreDAO storeDao;

	/**
	 * Obtains information about a user
	 *
	 * @return A JSON representation of the selected fields for a user
	 */
	@Override
	public String retrieve()
	{
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			getUserFromToken();

			String entityId = obtainStringValue("entityId", null);
			Integer entityKind = obtainIntegerValue("entityKind", null);
			String subentityId = obtainStringValue("subentityId", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);

			List<String> ids = CollectionFactory.createList();
			if( !StringUtils.hasText(subentityId)) {
				List<Store> stores = storeDao.getUsingBrandAndStatus(entityId, StatusHelper.statusActive(), null);
				for( Store store : stores ) {
					ids.add(store.getIdentifier());
				}
			} else {
				ids.add(subentityId);
			}

			Map<Integer, Integer> repetitions = null;
			Map<Integer, Integer> repetitionsV = null;

			toStringDate = sdf.format(new Date(sdf.parse(toStringDate).getTime() + 86400000));

			if( EntityKind.KIND_SHOPPING == entityKind ) {
				repetitions = apdvDao.getRepetitions(Arrays.asList(entityId),
						EntityKind.KIND_SHOPPING, APDVisit.CHECKIN_PEASANT, sdf.parse(fromStringDate), sdf.parse(toStringDate));

				repetitionsV = apdvDao.getRepetitions(Arrays.asList(entityId),
						EntityKind.KIND_SHOPPING, APDVisit.CHECKIN_VISIT, sdf.parse(fromStringDate), sdf.parse(toStringDate));
			} else {
				repetitions = apdvDao.getRepetitions(ids,
						EntityKind.KIND_STORE, APDVisit.CHECKIN_PEASANT, sdf.parse(fromStringDate), sdf.parse(toStringDate));

				repetitionsV = apdvDao.getRepetitions(ids,
						EntityKind.KIND_STORE, APDVisit.CHECKIN_VISIT, sdf.parse(fromStringDate), sdf.parse(toStringDate));
			}

			JSONArray series = new JSONArray();
			JSONObject serie = new JSONObject();
			serie.put("name", "Paseantes");
			serie.put("type", "column");
			JSONArray data = new JSONArray();
			Iterator<Integer> i = repetitions.keySet().iterator();
			while(i.hasNext()) {
				Integer key = i.next();
				data.put(repetitions.get(key));
			}
			serie.put("data", data);
			series.put(serie);

			serie = new JSONObject();
			serie.put("name", "Visitantes");
			serie.put("type", "spline");
			data = new JSONArray();
			i = repetitionsV.keySet().iterator();
			while(i.hasNext()) {
				Integer key = i.next();
				data.put(repetitionsV.get(key));
			}
			serie.put("data", data);
			series.put(serie);

			JSONArray categories = new JSONArray();
			int max = repetitions.size();
			int count = 0;
			i = repetitions.keySet().iterator();
			while(i.hasNext()) {
				Integer key = i.next();
				count++;
				if( count < max) {
					categories.put(key + " Veces");
				} else {
					categories.put("Mas Veces");
				}
			}

			JSONObject ret = new JSONObject();
			ret.put("series", series);
			ret.put("categories", categories);

			return ret.toString();

		} catch (ASException e) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE ||
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
				log.log(Level.INFO, e.getMessage());
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			return getJSONRepresentationFromException(e).toString();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} finally {
			markEnd(start);
		}
	}
}
