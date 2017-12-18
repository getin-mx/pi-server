package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.dao.ProcessDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Process;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreTicket;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;


public class ConvertName extends AbstractCLI {

	private static final Logger log = Logger.getLogger(ConvertName.class.getName());
	public static final long TWENTY_FOUR_HOURS = 86400000;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "entityId", "Entity ID").withRequiredArg().ofType( String.class );
		parser.accepts( "entityKind", "Entity Kind").withRequiredArg().ofType( Byte.class );
		parser.accepts( "newName", "New Name").withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			BrandDAO brandDao = (BrandDAO)getApplicationContext().getBean("brand.dao.ref");
			ShoppingDAO shoppingDao = (ShoppingDAO)getApplicationContext().getBean("shopping.dao.ref");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String entityId = null;
			byte entityKind = -1;
			String newName = null;
			
			try {
				if( options.has("entityId")) entityId = (String)options.valueOf("entityId");
				else usage(parser);
				
				if( options.has("entityKind")) entityKind = (Byte)options.valueOf("entityKind");
				else usage(parser);

				if( options.has("newName")) newName = (String)options.valueOf("newName");
				else usage(parser);

			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Changing name for entityId " + entityId + " and kind " + entityKind + " to '" + newName + "'");
			
			switch( entityKind ) {
				case EntityKind.KIND_SHOPPING:
					Shopping s = shoppingDao.get(entityId, true);
					s.setName(newName);
					shoppingDao.update(s);
					break;
				case EntityKind.KIND_BRAND:
					Brand b = brandDao.get(entityId, true);
					b.setName(newName);
					brandDao.update(b);
					break;
				case EntityKind.KIND_STORE:
					Store s1 = storeDao.get(entityId, true);
					s1.setName(newName);
					storeDao.update(s1);
					break;
				default:
					usage(parser);
					break;
			}
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}

	public static Process buildProcess(Store store, ProcessDAO dao, Date forDate) throws ASException {
		Process p = new Process();

		JSONObject obj = new JSONObject();
		obj.put("processType", 0);
		obj.put("entityKind", EntityKind.KIND_STORE);
		obj.put("entityId", store.getIdentifier());
		obj.put("fromDate", sdf.format(forDate));
		obj.put("toDate", sdf.format(new Date(forDate.getTime() + 86400000 )));
		
		p.setEntityId(store.getIdentifier());
		p.setEntityKind(EntityKind.KIND_STORE);
		p.setData(obj.toString());
		p.setUserId("admin");
		p.setStatus(StatusAware.STATUS_PREPARED);
		p.setProcessType(Process.PROCESS_TYPE_GENERATE_VISITS);
		
		p.setName("Reproceso de " + store.getName() + " de: " + forDate +" hasta: " + new Date(forDate.getTime() + 86400000));
		p.setKey(dao.createKey());		
		
		dao.create(p);

		return p;
	}
	
	public static Long countAPDVisits(PersistenceManager pm, Date curDate, Store store, GenericDAO<?> dao) {

		Map<String, Object> parameters = CollectionFactory.createMap();
		List<String> declaredParams = CollectionFactory.createList();
		List<String> filters = CollectionFactory.createList();

		Query query = pm.newQuery(APDVisit.class);

		declaredParams.add("java.util.Date fromParam");
		declaredParams.add("java.util.Date toParam");
		declaredParams.add("String entityIdParam");
		declaredParams.add("Integer checkinTypeParam");

		filters.add("checkinStarted >= fromParam");
		filters.add("checkinStarted < toParam");
		filters.add("entityId == entityIdParam");
		filters.add("checkinType == checkinTypeParam");

		parameters.put("fromParam", curDate);
		parameters.put("toParam", new Date(curDate.getTime() + 86400000));
		parameters.put("entityIdParam", store.getIdentifier());
		parameters.put("checkinTypeParam", APDVisit.CHECKIN_VISIT);
		
		query.declareParameters(dao.toParameterList(declaredParams));
		query.setFilter(dao.toWellParametrizedFilter(filters));

		query.setResult("count(this)");
		Long count = Long.parseLong(query.executeWithMap(parameters).toString());

		return count;
	}

	public static Long countTickets(PersistenceManager pm, Date curDate, Store store, GenericDAO<?> dao) {

		Map<String, Object> parameters = CollectionFactory.createMap();
		List<String> declaredParams = CollectionFactory.createList();
		List<String> filters = CollectionFactory.createList();

		Query query = pm.newQuery(StoreTicket.class);

		declaredParams.add("String dateParam");
		declaredParams.add("String storeIdParam");

		filters.add("date == dateParam");
		filters.add("storeId == storeIdParam");

		parameters.put("dateParam", sdf.format(curDate));
		parameters.put("storeIdParam", store.getIdentifier());
		
		query.declareParameters(dao.toParameterList(declaredParams));
		query.setFilter(dao.toWellParametrizedFilter(filters));

		query.setResult("sum(qty)");
		try {
			Long count = Long.parseLong(query.executeWithMap(parameters).toString());
			return count.longValue();
		} catch( NullPointerException e ) {
			return 0L;
		}

	}

	public static Long countDITickets(PersistenceManager pm, Date curDate, Store store, GenericDAO<?> dao) {

		Map<String, Object> parameters = CollectionFactory.createMap();
		List<String> declaredParams = CollectionFactory.createList();
		List<String> filters = CollectionFactory.createList();

		Query query = pm.newQuery(DashboardIndicatorData.class);

		declaredParams.add("String stringDateParam");
		declaredParams.add("String subentityIdParam");
		declaredParams.add("String elementIdParam");
		declaredParams.add("String elementSubIdParam");
		declaredParams.add("String periodTypeParam");

		filters.add("stringDate == stringDateParam");
		filters.add("subentityId == subentityIdParam");
		filters.add("elementId == elementIdParam");
		filters.add("elementSubId == elementSubIdParam");
		filters.add("periodType == periodTypeParam");

		parameters.put("stringDateParam", sdf.format(curDate));
		parameters.put("subentityIdParam", store.getIdentifier());
		parameters.put("elementIdParam", "apd_visitor");
		parameters.put("elementSubIdParam", "visitor_total_tickets");
		parameters.put("periodTypeParam", "D");
		
		query.declareParameters(dao.toParameterList(declaredParams));
		query.setFilter(dao.toWellParametrizedFilter(filters));

		query.setResult("sum(doubleValue)");
		try {
			Double count = Double.parseDouble(query.executeWithMap(parameters).toString());
			return count.longValue();
		} catch( NullPointerException e ) {
			return 0L;
		}
	}

	public static Long countDIVisits(PersistenceManager pm, Date curDate, Store store, GenericDAO<?> dao) {

		Map<String, Object> parameters = CollectionFactory.createMap();
		List<String> declaredParams = CollectionFactory.createList();
		List<String> filters = CollectionFactory.createList();

		Query query = pm.newQuery(DashboardIndicatorData.class);

		declaredParams.add("String stringDateParam");
		declaredParams.add("String subentityIdParam");
		declaredParams.add("String elementIdParam");
		declaredParams.add("String elementSubIdParam");
		declaredParams.add("String periodTypeParam");

		filters.add("stringDate == stringDateParam");
		filters.add("subentityId == subentityIdParam");
		filters.add("elementId == elementIdParam");
		filters.add("elementSubId == elementSubIdParam");
		filters.add("periodType == periodTypeParam");

		parameters.put("stringDateParam", sdf.format(curDate));
		parameters.put("subentityIdParam", store.getIdentifier());
		parameters.put("elementIdParam", "apd_visitor");
		parameters.put("elementSubIdParam", "visitor_total_visits");
		parameters.put("periodTypeParam", "D");
		
		query.declareParameters(dao.toParameterList(declaredParams));
		query.setFilter(dao.toWellParametrizedFilter(filters));

		query.setResult("sum(doubleValue)");
		try {
			Double count = Double.parseDouble(query.executeWithMap(parameters).toString());
			return count.longValue();
		} catch( NullPointerException e ) {
			return 0L;
		}
	}

}
