/**
 * 
 */
package mx.getin.bdb.dashboard.bz.spi;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.bdb.dashboard.bz.spi.BrandTableDataBzServiceJSONImpl.DashboardRecordRep;
import mobi.allshoppings.bdb.dashboard.bz.spi.BrandTableDataBzServiceJSONImpl.DashboardTableRep;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.InnerZoneDAO;
import mobi.allshoppings.dao.StoreDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.InnerZone;
import mobi.allshoppings.model.Store;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;

/**
 * @author ArturoArmengod
 *
 */
public class DevlynTableDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(DevlynTableDataBzServiceJSONImpl.class.getName());

	@Autowired
	private DashboardIndicatorDataDAO dao;
	@Autowired
	private StoreDAO storeDao;
	@Autowired
	private InnerZoneDAO innerZoneDao;

	private DecimalFormat df = new DecimalFormat("##.00");
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

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
			User user = getUserFromToken();

			String entityId = obtainStringValue("entityId", null);
			@SuppressWarnings("unused")
			Integer entityKind = obtainIntegerValue("entityKind", null);
			@SuppressWarnings("unused")
			String subentityId = obtainStringValue("subentityId", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);
			String format = obtainStringValue("format", "table");

			// Initializes the table using the received information
			DashboardTableRep table = new DashboardTableRep();
			List<Store> tmpStores = storeDao.getUsingBrandAndStatus(entityId, StatusHelper.statusActive(), "name");
			List<Store> tmpStores2 = CollectionFactory.createList();
			for( Store store : tmpStores ) {
			 	if( isValidForUser(user, store) )
					tmpStores2.add(store);
			}
			table.setStores(tmpStores2);
			Collections.sort(table.getStores(), new Comparator<Store>() {
				@Override
				public int compare(Store o1, Store o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			List<String> entityIds = initializeTableRecords(table, fromStringDate, toStringDate);
			entityIds.add(entityId);

			// Starts to Collect the data
			List<DashboardIndicatorData> list;

			// peasents, visits, and tickets
			list = dao.getUsingFilters(entityIds, null, Arrays.asList("apd_visitor"),
					Arrays.asList("visitor_total_peasents", "visitor_total_visits", "visitor_total_tickets", "visitor_total_items", "visitor_total_revenue"), null,
					null, null, fromStringDate, toStringDate, null, null, null, null, null, null,
					null, null);

			for(DashboardIndicatorData obj : list) {

				DashboardRecordRep rec = obj.getEntityKind().equals(EntityKind.KIND_INNER_ZONE)
						? table.findRecordWithEntityId(obj.getEntityId(), obj.getEntityKind())
						: table.findRecordWithEntityId(obj.getSubentityId(), EntityKind.KIND_STORE);

				DashboardRecordRep totals = table.getTotals();

				if( null != rec ) {
					if( obj.getElementSubId().equals("visitor_total_peasents"))
						rec.setPeasants(rec.getPeasants() + obj.getDoubleValue().longValue());
					else if( obj.getElementSubId().equals("visitor_total_visits") && obj.getEntityKind().equals(EntityKind.KIND_STORE)) {
						rec.setVisitors(rec.getVisitors() + obj.getDoubleValue().longValue());
						rec.addToDateCache(obj.getDoubleValue().longValue(), obj.getStringDate());
						totals.addToDateCache(obj.getDoubleValue().longValue(), obj.getStringDate());
					} else if( obj.getElementSubId().equals("visitor_total_tickets")) {
						rec.setTickets(rec.getTickets() + obj.getDoubleValue().longValue());
					} else if( obj.getElementSubId().equals("visitor_total_items")) {
						rec.setItems(rec.getItems() + (int) obj.getDoubleValue().longValue());
					}
					else if( obj.getElementSubId().equals("visitor_total_revenue"))
						rec.setRevenue(rec.getRevenue() + obj.getDoubleValue());
					else if( obj.getElementSubId().equals("visitor_total_visits") && obj.getEntityKind().equals(EntityKind.KIND_INNER_ZONE) && obj.getSubentityName().toLowerCase().contains("gabinete"))
					{
						rec.setgabinetes(rec.getgabinetes() + obj.getDoubleValue().longValue());
						rec.addToDateCache(obj.getDoubleValue().longValue(), obj.getStringDate());
						totals.addToDateCache(obj.getDoubleValue().longValue(), obj.getStringDate());
					}
						
				}
			}

			// permanence
			list = dao.getUsingFilters(entityIds,
					null, Arrays.asList("apd_permanence"), Arrays.asList("permanence_hourly_visits"), null,
					null, null, fromStringDate, toStringDate,
					null, null, null, null, null, null, null, null);

			{
				for(DashboardIndicatorData obj : list) {
					DashboardRecordRep rec = table.findRecordWithEntityId(obj.getSubentityId(), null);
					if( rec != null ) {
						if( obj.getDoubleValue() != null ) rec.setPermanenceInMillis(rec.getPermanenceInMillis() + obj.getDoubleValue().longValue());
						else log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());
						if( obj.getRecordCount() != null ) rec.setPermancenceQty(rec.getPermancenceQty() + obj.getRecordCount());
						else log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());
						if(obj.getDoubleValue() != null && obj.getEntityKind().equals(EntityKind.KIND_INNER_ZONE) && obj.getSubentityName().toLowerCase().contains("gabinete"))
							rec.setPermanenceInMillisGab(rec.getPermanenceInMillisGab() + obj.getDoubleValue().longValue());
						if(obj.getRecordCount() != null && obj.getEntityKind().equals(EntityKind.KIND_INNER_ZONE) && obj.getSubentityName().toLowerCase().contains("gabinete"))
							rec.setPermancenceQtyGab(rec.getPermancenceQtyGab() + obj.getRecordCount());
					}
				}
			}

			// Creates the final JSON Array
			if( format.equals("json")) {

				JSONObject resp = new JSONObject();
				JSONArray data = new JSONArray();
				for( DashboardRecordRep r : table.records ) {
					data.put(r.toJSONObject());
				}
				resp.put("data", data);
				resp.put("totals", table.toJSONTotals());
				return resp.toString();

			} else {
				JSONArray jsonArray = new JSONArray();
				jsonArray.put(table.getJSONHeaders());
				table.addJSONRecords(jsonArray);
				jsonArray.put(table.getJSONTotals());

				// Returns the final value
				return jsonArray.toString();

			}

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

	public String getDateName(Date date) {
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dof = cal.get(Calendar.DAY_OF_WEEK);

		switch(dof) {
		case Calendar.SUNDAY:
			sb.append("Dom ");
			break;
		case Calendar.MONDAY:
			sb.append("Lun ");
			break;
		case Calendar.TUESDAY:
			sb.append("Mar ");
			break;
		case Calendar.WEDNESDAY:
			sb.append("Mie ");
			break;
		case Calendar.THURSDAY:
			sb.append("Jue ");
			break;
		case Calendar.FRIDAY:
			sb.append("Vie ");
			break;
		case Calendar.SATURDAY:
			sb.append("Sab ");
			break;
		}

		sb.append(sdf.format(date));

		return sb.toString();
	}
	/**
	 *initializeTableRecords from each store in the collection
	 * 
	 * @param table
	 *            - The DashboardTableRep to get all the information.
	 * @param fromStringDate
	 *            - initial date for the records.
	 * @param toStringDate
	 *            - final date to for the records.           
	 * @return String list.
	 *          
	 */
	public List<String> initializeTableRecords(DashboardTableRep table, String fromStringDate, String toStringDate)
			throws ASException {

		List<String> ret = CollectionFactory.createList();

		for (Store store : table.getStores()) {
			table.getRecords().add(new DashboardRecordRep(table, 0, store.getIdentifier(), EntityKind.KIND_STORE,
					store.getName(), fromStringDate, toStringDate));

			List<InnerZone> zonesl1 = innerZoneDao.getUsingEntityIdAndRange(store.getIdentifier(),
					EntityKind.KIND_STORE, null, "name", null, true);

			for (InnerZone zonel1 : zonesl1) {
				table.getRecords().add(new DashboardRecordRep(null, 1, zonel1.getIdentifier(), EntityKind.KIND_INNER_ZONE,
						zonel1.getName(), fromStringDate, toStringDate));

				List<InnerZone> zonesl2 = innerZoneDao.getUsingEntityIdAndRange(zonel1.getIdentifier(),
						EntityKind.KIND_INNER_ZONE, null, "name", null, true);
				if( zonesl2.size() > 0 ) table.getRecords().get(table.getRecords().size()-1).setHeader(true);

				for (InnerZone zonel2 : zonesl2) {
					table.getRecords().add(new DashboardRecordRep(null, 2, zonel2.getIdentifier(), EntityKind.KIND_INNER_ZONE,
							zonel2.getName(), fromStringDate, toStringDate));

				}
			}
		}

		for( DashboardRecordRep rec : table.getRecords()) {
			if( StringUtils.hasText(rec.getEntityId()))
				ret.add(rec.getEntityId());
		}

		return ret;

	}

	public class DashboardTableRep {
		private List<Store> stores;
		private List<DashboardRecordRep> records;
		private DashboardRecordRep totals;

		public DashboardTableRep() {
			stores = CollectionFactory.createList();
			records = CollectionFactory.createList();
			totals = new DashboardRecordRep(null, 0, null, null, "Totales", null, null);
		}

		public DashboardRecordRep findRecordWithEntityId(String entityId, Integer entityKind) {
			for(DashboardRecordRep rec : records ) {
				if( rec.getEntityId().equals(entityId) && (entityKind == null || rec.getEntityKind().equals(entityKind)))
					return rec;
			}
			return null;
		}

		/**
		 * @return the totals
		 */
		public DashboardRecordRep getTotals() {
			return totals;
		}

		/**
		 * @param totals the totals to set
		 */
		public void setTotals(DashboardRecordRep totals) {
			this.totals = totals;
		}

		/**
		 * @return the records
		 */
		public List<DashboardRecordRep> getRecords() {
			return records;
		}
		/**
		 * @return the stores
		 */
		public List<Store> getStores() {
			return stores;
		}
		/**
		 * @param stores the stores to set
		 */
		public void setStores(List<Store> stores) {
			this.stores = stores;
		}

		/**
		 * @param records the records to set
		 */
		public void setRecords(List<DashboardRecordRep> records) {
			this.records = records;
		}
		/**
		 * Gets all the headers in a JSON Array
		 * @return
		 */
		public JSONArray getJSONHeaders() {

			// Titles row
			JSONArray titles = new JSONArray();

			titles.put("Tienda");
			titles.put("Mirador");
			titles.put("Visitas");
			titles.put("Gabinetes");
			titles.put("Tickets");
			titles.put("Items");
			titles.put("Revenue");
			titles.put("Visitantes/Mirador");
			titles.put("Gabinete/visitas");
			titles.put("Ovs/visitas");
			titles.put("Día más Alto");
			titles.put("Día más Bajo");
			titles.put("Permanencia Exhibición");
			titles.put("Permanencia Promedio");
			titles.put("Permanencia Óptica");
			

			return titles;
		}

		/**
		 * Gets the totals JSON Array
		 * @return
		 * @throws ASException
		 */
		public JSONArray getJSONTotals() throws ASException {

			DashboardRecordRep totals = new DashboardRecordRep(null, 0, null, null, "Totales", null, null);
			//List<Long> c = CollectionFactory.createList();

			for( DashboardRecordRep rec : records ) {
				if( rec.getLevel() == 0 ) {
					totals.setPeasants(totals.getPeasants() + rec.getPeasants());
					totals.setVisitors(totals.getVisitors() + rec.getVisitors());
					totals.setgabinetes(totals.getgabinetes() + rec.getgabinetes());
					totals.setTickets(totals.getTickets() + rec.getTickets());
					totals.setItems(totals.getItems() + rec.getItems());
					totals.setRevenue(totals.getRevenue() + rec.getRevenue());
					//c.add(rec.getPermanenceInMillis());
					totals.setPermancenceQty(totals.getPermancenceQty() +rec.getPermancenceQty());
					totals.setPermanenceInMillis(totals.getPermanenceInMillis() +rec.getPermanenceInMillis());
					totals.setPermanenceInMillisGab(totals.getPermanenceInMillisGab() + rec.getPermanenceInMillisGab());
					totals.setPermancenceQtyGab(totals.getPermancenceQtyGab() + rec.getPermancenceQtyGab());

					Map<String, Long> datesCache = totals.getDatesCache();
					Map<String, Long> recDatesCache = rec.getDatesCache();

					Iterator<String> i = recDatesCache.keySet().iterator();
					while(i.hasNext()) {
						String key = i.next();
						Long val = recDatesCache.get(key);
						Long tot = datesCache.get(key);
						if( val == null ) val = 0l;
						if( tot == null ) tot = 0l;
						tot += val;
						datesCache.put(key, tot);
					}
					totals.setDatesCache(datesCache);
					if(totals.getEntityId() == "gabinete")
					{
						//totals.setVisitors(totals.getVisitors() + rec.getVisitors());
						//rec.title="gabinete";
					}
				}
			}

			// TODO saca la mediana
			/*Collections.sort(c);
			if( c.size() == 0 )
				totals.setPermanenceInMillis(0l);
			else
				if( c.size() % 2 == 0 && c.size() >= 2 ) {
					int med = (int)(c.size() / 2);
					totals.setPermanenceInMillis((c.get(med-1) + c.get(med)) / 2);
				} else
					totals.setPermanenceInMillis(c.get((int)Math.floor(c.size() / 2)));*/
			return totals.toJSONArray();

		}

		/**
		 * Gets the totals JSON Array
		 * @return
		 * @throws ASException
		 */
		public JSONObject toJSONTotals() throws ASException {

			totals.setParent(null);
			totals.setPeasants(0L);
			totals.setVisitors(0L);
			totals.setgabinetes(0l);
			totals.setRevenue(0.0);
			totals.setItems(0);
			totals.setPermanenceInMillis(0L);
			totals.setPermancenceQty(0);

			for( DashboardRecordRep rec : records ) {
				if( rec.getLevel() == 0 ) {
					totals.setPeasants(totals.getPeasants() + rec.getPeasants());
					totals.setVisitors(totals.getVisitors() + rec.getVisitors());
					totals.setgabinetes(totals.getgabinetes() + rec .getgabinetes());
					totals.setTickets(totals.getTickets() + rec.getTickets());
					totals.setItems(totals.getItems() + rec.getItems());
					totals.setRevenue(totals.getRevenue() + rec.getRevenue());
					totals.setPermanenceInMillis(totals.getPermanenceInMillis() + rec.getPermanenceInMillis());
					totals.setPermanenceInMillisGab(totals.getPermanenceInMillisGab() + rec.getPermanenceInMillisGab());
					totals.setPermancenceQty(totals.getPermancenceQty() + rec.getPermancenceQty());
					totals.setPermancenceQtyGab(totals.getPermancenceQtyGab() + rec.getPermancenceQtyGab());
				}
			}

			return totals.toJSONObject();

		}

		/**
		 * Adds all records in a JSON Array
		 * @param array
		 * @return
		 * @throws ASException
		 */
		public JSONArray addJSONRecords(JSONArray array) throws ASException {

			if( array == null)
				array = new JSONArray();

			for( DashboardRecordRep rec : records )
				array.put(rec.toJSONArray());

			return array;
		}
	}

	public class DashboardRecordRep {
		private boolean header;
		private int level;
		private String entityId;
		private Integer entityKind;
		private String title;
		private Long peasants;
		private Long visitors;
		private long cabinet;
		private int items;
		private Long tickets;
		private Double revenue;
		private Date higherDate;
		private Date lowerDate;
		private Long permanenceInMillis;
		private long permanenceInMillisGab;
		private int permancenceQty;
		private int permancenceQtyGabs;
		private Map<String, Long> datesCache;
		private DashboardTableRep parent;

		public DashboardRecordRep(DashboardTableRep parent, int level, String entityId, Integer entityKind, String title, String fromStringDate, String toStringDate) {
			super();

			this.parent = parent;

			this.entityId = entityId;
			this.entityKind = entityKind;
			this.level = level;
			this.title = title;

			peasants = 0l;
			visitors = 0l;
			tickets = 0l;
			items = 0;
			revenue = 0.0;
			permanenceInMillis = 0l;
			permanenceInMillisGab=0l;
			cabinet = 0l;
			permancenceQtyGabs=0;

			try {
				datesCache = CollectionFactory.createMap();
				Date d1 = sdf.parse(fromStringDate);
				Date d2 = sdf.parse(toStringDate);
				while( d1.before(d2) || d1.equals(d2)) {
					datesCache.put(sdf.format(d1), 0l);
					d1 = new Date(d1.getTime() + 86400000);
				}
			} catch( Exception e ) {}

		}

		/**
		 * Adds a new date to the date cache
		 * @param value
		 * @param date
		 */
		public void addToDateCache(long value, String date) {
			Long dValue = datesCache.get(date);
			if( dValue == null ) dValue = 0L;
			dValue += value;
			datesCache.put(date, dValue);
		}

		/**
		 * Transforms this record in a JSONArray
		 * @return
		 * @throws ASException
		 */
		public JSONArray toJSONArray() throws ASException {
			JSONArray row = new JSONArray();

			String h1 = "";
			String h2 = "";
			if( isHeader() ) {
				h1 = "<b>";
				h2 = "</b>";
			}

			String tit = title;
			for( int x = 0; x < level; x++ )
				tit = "&nbsp;&nbsp;" + tit;

			row.put(h1 + tit + h2);
			row.put(h1 + String.valueOf(peasants) + h2);
			row.put(h1 + String.valueOf(visitors) + h2);
			row.put(h1 + String.valueOf(cabinet)+ h2);
			row.put(h1 + String.valueOf(tickets) + h2);
			row.put(h1 + String.valueOf(items)    + h2);
			row.put(h1 + String.valueOf(revenue) + h2);


			// peasents_conversion
			if( peasants != 0)
				row.put(h1 + df.format(visitors * 100 / peasants) + "%" + h2);
			else
				row.put(h1 + df.format(0) + "%" + h2);
			if(cabinet != 0)
				row.put(h1 + df.format(cabinet* 100 / visitors) + "%" + h2);
			else
				row.put(h1 + df.format(0) + "%" + h2);
				

			// tickets_conversion
			if( visitors != 0)
				row.put(h1 + df.format(tickets * 100 / visitors) + "%" + h2);
			else
				row.put(h1 + df.format(0) + "%" + h2);

			row.put(h1 + calculateHigherDay() + h2);
			row.put(h1 + calculateLowerDay() + h2);

			if(permancenceQty != 0)
				row.put(h1 + String.valueOf(Math.round(permanenceInMillis /permancenceQty / 60000)) + " mins"  + h2);
			else
				row.put(h1 +df.format(0) +" mins" +h2);
			if(permancenceQtyGabs != 0)
				row.put(h1 + String.valueOf(Math.round(permanenceInMillisGab /permancenceQtyGabs / 60000)) + " mins"  + h2);
			else
				row.put(h1 +df.format(0) +" mins" +h2);
			if(permancenceQtyGabs != 0 && permancenceQty != 0)
				row.put(h1 + String.valueOf(Math.round((permanenceInMillisGab /permancenceQtyGabs / 60000) + Math.round(permanenceInMillis /permancenceQty / 60000)/2)) +  " mins"  + h2);
			else
				row.put(h1 +df.format(0) +" mins" +h2);

			return row;
		}

		/**
		 * Transforms this record in a JSONArray
		 * @return
		 * @throws ASException
		 */
		public JSONObject toJSONObject() throws ASException {
			JSONObject row = new JSONObject();

			String tit = title;
			for( int x = 0; x < level; x++ )
				tit = "&nbsp;&nbsp;" + tit;

			row.put("header", header);
			row.put("title", tit);
			row.put("peasants", peasants);
			row.put("visitors", visitors);
			row.put("cabinet", cabinet);
			row.put("tickets", tickets);
			row.put("items", items);
			row.put("revenue", revenue);

			// peasents_conversion
			if( peasants != 0)
				row.put("visitsConversion", (float)((float)(visitors * 100) / peasants));
			else
				row.put("visitsConversion", 0);
			if(cabinet != 0)
				row.put("cabinetConversion", (float)((float)(cabinet * 100)/visitors));
			else 
				row.put("cabinetConversion", 0);

			// tickets_conversion
			if( visitors != 0)
				row.put("ticketsConversion", (float)((float)(tickets * 100) / visitors));
			else
				row.put("ticketsConversion", 0);

			row.put("higherDay", calculateHigherDay());
			row.put("lowerDay", calculateLowerDay());

			if( permanenceInMillis > 0 && permancenceQty > 0 ) {
				row.put("averagePermanenceEntrance", Math.round(permanenceInMillis / permancenceQty / 60000D ));
			} else {
				row.put("averagePermanenceEntrance", 0);
			}
			if( permanenceInMillisGab > 0 && permancenceQtyGabs > 0 ) {
				row.put("averagePermanencecabinet", Math.round(permanenceInMillisGab / permancenceQtyGabs / 60000D ));
			} else {
				row.put("averagePermanencecabinet", 0);
			}
			if( permanenceInMillisGab > 0 && permancenceQtyGabs > 0 ) {
				row.put("averagePermanenceAll", ((Math.round(permanenceInMillisGab / permancenceQtyGabs / 60000D) + Math.round(permanenceInMillis / permancenceQty / 60000D )/2)));
			} else {
				row.put("averagePermanenceAll", 0);
			}
			

			return row;
		}

		/**
		 * Calculates the higher visits day in the selected period
		 * @return
		 * @throws ASException
		 */
		private String calculateHigherDay() throws ASException {
			try {
				String higher = null;
				long higherVal = 0L;
				Iterator<String> i = datesCache.keySet().iterator();
				while(i.hasNext()) {
					String key = i.next();
					long value = datesCache.get(key);
					if( value > higherVal || higher == null ) {
						higherVal = value;
						higher = key;
					}
				}

				if( higher == null )
					return "-";

				return getDateName(sdf.parse(higher));
			} catch( Exception e ) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			}
		}

		/**
		 * Calculates the lower visits day in the selected period
		 * @return
		 * @throws ASException
		 */
		private String calculateLowerDay() throws ASException {
			try {
				String lower = null;
				long lowerVal = 9999999999999999L;
				Iterator<String> i = datesCache.keySet().iterator();
				while(i.hasNext()) {
					String key = i.next();
					long value = datesCache.get(key);
					if( value < lowerVal || lower == null ) {
						lowerVal = value;
						lower = key;
					}
				}

				if( lower == null )
					return "-";

				return getDateName(sdf.parse(lower));
			} catch( Exception e ) {
				throw ASExceptionHelper.defaultException(e.getMessage(), e);
			}
		}

		/**
		 * @return the header
		 */
		public boolean isHeader() {
			return header;
		}

		public int getPermancenceQty() {
			return permancenceQty;
		}
		
		public int getPermancenceQtyGab() {
			return permancenceQtyGabs;
		}

		public void setPermancenceQty(int permancenceQty) {
			this.permancenceQty = permancenceQty;
		}
		
		public void setPermancenceQtyGab(int permancenceQtyGab) {
			this.permancenceQtyGabs = permancenceQtyGab;
		}

		/**
		 * @param header the header to set
		 */
		public void setHeader(boolean header) {
			this.header = header;
		}

		/**
		 * @return the revenue
		 */
		public Double getRevenue() {
			return revenue;
		}

		/**
		 * @param revenue the revenue to set
		 */
		public void setRevenue(Double revenue) {
			this.revenue = revenue;
		}

		/**
		 * @return the level
		 */
		public int getLevel() {
			return level;
		}

		/**
		 * @param level the level to set
		 */
		public void setLevel(int level) {
			this.level = level;
		}

		/**
		 * @return the entityId
		 */
		public String getEntityId() {
			return entityId;
		}

		/**
		 * @param entityId the entityId to set
		 */
		public void setEntityId(String entityId) {
			this.entityId = entityId;
		}

		/**
		 * @return the entityKind
		 */
		public Integer getEntityKind() {
			return entityKind;
		}

		/**
		 * @param entityKind the entityKind to set
		 */
		public void setEntityKind(Integer entityKind) {
			this.entityKind = entityKind;
		}

		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * @param title the title to set
		 */
		public void setTitle(String title) {
			this.title = title;
		}

		/**
		 * @return the peasants
		 */
		public Long getPeasants() {
			return peasants;
		}

		/**
		 * @param peasants the peasants to set
		 */
		public void setPeasants(Long peasants) {
			this.peasants = peasants;
		}

		/**
		 * @return the visitors
		 */
		public Long getVisitors() {
			return visitors;
		}

		/**
		 * @param visitors the visitors to set
		 */
		public void setVisitors(Long visitors) {
			this.visitors = visitors;
		}
		
		/**
		 * @param visitors cabinet the visitors to set
		 */
		public void setgabinetes(long cabinet) {
			this.cabinet = cabinet;
		}
		
		/**
		 * @param return cabinet
		 */
		public long getgabinetes() {
			return cabinet;
		}

		/**
		 * @return the tickets
		 */
		public Long getTickets() {
			return tickets;
		}

		/**
		 * @param tickets the tickets to set
		 */
		public void setTickets(Long tickets) {
			this.tickets = tickets;
		}

		/**
		 * @return the higherDate
		 */
		public Date getHigherDate() {
			return higherDate;
		}

		/**
		 * @param higherDate the higherDate to set
		 */
		public void setHigherDate(Date higherDate) {
			this.higherDate = higherDate;
		}

		/**
		 * @return the lowerDate
		 */
		public Date getLowerDate() {
			return lowerDate;
		}

		/**
		 * @param lowerDate the lowerDate to set
		 */
		public void setLowerDate(Date lowerDate) {
			this.lowerDate = lowerDate;
		}

		/**
		 * @return the permanenceInMillis
		 */
		public Long getPermanenceInMillis() {
			return permanenceInMillis;
		}
		
		/**
		 * @return the permanenceInMillisGab
		 */
		public Long getPermanenceInMillisGab() {
			return permanenceInMillisGab;
		}

		/**
		 * @param permanenceInMillis the permanenceInMillis to set
		 */
		public void setPermanenceInMillis(Long permanenceInMillis) {
			this.permanenceInMillis = permanenceInMillis;
		}
		
		/**
		 * @param permanenceInMillis the permanenceInMillis to set in gabinete
		 */
		public void setPermanenceInMillisGab(Long permanenceInMillisGab) {
			this.permanenceInMillisGab = permanenceInMillisGab;
		}

		/**
		 * @return the datesCache
		 */
		public Map<String, Long> getDatesCache() {
			return datesCache;
		}

		/**
		 * @param datesCache the datesCache to set
		 */
		public void setDatesCache(Map<String, Long> datesCache) {
			this.datesCache = datesCache;
		}

		/**
		 * @return the parent
		 */
		public DashboardTableRep getParent() {
			return parent;
		}

		/**
		 * @param parent the parent to set
		 */
		public void setParent(DashboardTableRep parent) {
			this.parent = parent;
		}

		public int getItems() {
			return items;
		}

		public void setItems(int items) {
			this.items = items;
		}

	}
}
