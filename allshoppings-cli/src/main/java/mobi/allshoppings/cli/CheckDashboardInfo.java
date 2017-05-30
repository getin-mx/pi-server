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
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.dao.BrandDAO;
import mobi.allshoppings.dao.GenericDAO;
import mobi.allshoppings.dao.ProcessDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.dao.spi.DAOJDOPersistentManagerFactory;
import mobi.allshoppings.dashboards.DashboardAPDeviceMapperService;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APDVisit;
import mobi.allshoppings.model.Brand;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.Process;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.StoreTicket;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.process.ProcessHelper;
import mobi.allshoppings.tools.CollectionFactory;


public class CheckDashboardInfo extends AbstractCLI {

	private static final Logger log = Logger.getLogger(CheckDashboardInfo.class.getName());
	public static final long TWENTY_FOUR_HOURS = 86400000;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Export from date (yyyy-MM-dd)").withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Export to date (yyyy-MM-dd)").withRequiredArg().ofType( String.class );
		parser.accepts( "entityIds", "List of entityIds, separated by comma").withRequiredArg().ofType( String.class );		
		parser.accepts( "entityKind", "Entity kind").withRequiredArg().ofType( Integer.class );		
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			StoreDAO storeDao = (StoreDAO)getApplicationContext().getBean("store.dao.ref");
			BrandDAO brandDao = (BrandDAO)getApplicationContext().getBean("brand.dao.ref");
			ProcessDAO processDao = (ProcessDAO)getApplicationContext().getBean("process.dao.ref");
			APDAssignationDAO apdaDao = (APDAssignationDAO)getApplicationContext().getBean("apdassignation.dao.ref");
			ProcessHelper processHelper = (ProcessHelper)getApplicationContext().getBean("process.helper");
			DashboardAPDeviceMapperService mapper = (DashboardAPDeviceMapperService)getApplicationContext().getBean("dashboard.apdevice.mapper");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
			String sEntityIds[] = null;
			Integer entityKind = EntityKind.KIND_STORE; // Store by default
			
			try {
				if( options.has("fromDate")) {
					sFromDate = (String)options.valueOf("fromDate");
					fromDate = sdf.parse(sFromDate);
				}

				if( options.has("toDate")) {
					sToDate = (String)options.valueOf("toDate");
					toDate = sdf.parse(sToDate);
				}

				if( options.has("entityKind")) {
					entityKind = (Integer)options.valueOf("entityKind");
				}

				if( options.has("entityIds")) {
					sEntityIds = ((String)options.valueOf("entityIds")).split(",");
				}
					
				if( null == fromDate )
					fromDate = new Date(System.currentTimeMillis() - 86400000);

				if( null == toDate )
					toDate = new Date(fromDate.getTime() + 86400000);

			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}
			
			List<Store> stores = CollectionFactory.createList();
			if( null != sEntityIds ) {
				for( String eid : sEntityIds ) {
					if( entityKind.equals(EntityKind.KIND_STORE)) {
						stores.add(storeDao.get(eid, true));
					} else if( entityKind.equals(EntityKind.KIND_BRAND)) {
						stores.addAll(storeDao.getUsingBrandAndStatus(eid, StatusHelper.statusActive(), "name"));
					}
				}
			} else {
				List<Brand> brands = brandDao.getUsingStatusAndRange(StatusHelper.statusActive(), null, "name");
				for( Brand brand : brands ) {
					stores.addAll(storeDao.getUsingBrandAndStatus(brand.getIdentifier(), StatusHelper.statusActive(), "name"));
				}
			}
			
			PersistenceManager pm;
			pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();

			Date curDate = new Date(fromDate.getTime());
			mapper.buildCaches(true);
			
			while( curDate.before(toDate)) {
				for( Store store : stores ) {

					Long visitCountAPDV = countAPDVisits(pm, curDate, store, storeDao);
					Long visitCountDI = countDIVisits(pm, curDate, store, storeDao);
					
					if(visitCountAPDV.equals(visitCountDI)) {
						log.log(Level.INFO, "Checking Visits count for " + store.getName() + " in " + sdf.format(curDate) + " (" + visitCountAPDV + " vs " + visitCountDI + ")");
					} else {
						log.log(Level.WARNING, "--------- Error in Visits count for " + store.getName() + " in " + sdf.format(curDate) + " (" + visitCountAPDV + " vs " + visitCountDI + ")...");
						Process p = buildProcess(store, processDao, curDate);
						processHelper.startProcess(p.getIdentifier(), true);
					}
					
					if( visitCountAPDV.equals(0)) {
						List<APDAssignation> assigs = apdaDao.getUsingEntityIdAndEntityKindAndDate(store.getIdentifier(), EntityKind.KIND_STORE, curDate);
						if(!assigs.isEmpty()) {
							log.log(Level.WARNING, "--------- Rebuilding Visits count for " + store.getName() + " in " + sdf.format(curDate) + "...");
							Process p = buildProcess(store, processDao, curDate);
							processHelper.startProcess(p.getIdentifier(), true);
						}
					}
					
					
					Long ticketsCount = countTickets(pm, curDate, store, storeDao);
					Long ticketsCountDI = countDITickets(pm, curDate, store, storeDao);
					
					if(ticketsCount.equals(ticketsCountDI)) {
						log.log(Level.INFO, "Checking Tickets for " + store.getName() + " in " + sdf.format(curDate) + " (" + ticketsCount + " vs " + ticketsCountDI + ")");
					} else {
						log.log(Level.WARNING, "--------- Error in Tickets for " + store.getName() + " in " + sdf.format(curDate) + " (" + ticketsCount + " vs " + ticketsCountDI + ")...");
						mapper.createStoreTicketDataForDates(sdf.format(curDate), sdf.format(new Date(curDate.getTime() + 86400000)), store.getIdentifier());
					}
					
				}
				
				curDate = new Date(curDate.getTime() + 86400000);
			}
			
			pm.close();
			
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
		p.setUserId("adminb");
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
