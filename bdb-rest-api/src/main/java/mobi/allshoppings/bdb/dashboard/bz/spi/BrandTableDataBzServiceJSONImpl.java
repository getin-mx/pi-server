package mobi.allshoppings.bdb.dashboard.bz.spi;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
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
 * Creates the totals table for the given stores. This is used in dashboard/apdvisits
 * @author Matias Hapanowicz
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 2.0, december 2017
 * @since Allshoppings
 */
public class BrandTableDataBzServiceJSONImpl extends BDBRestBaseServerResource implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(BrandTableDataBzServiceJSONImpl.class.getName());

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

			String entityId = obtainStringValue("storeIds", null);
			List<String> brandId = Arrays.asList(obtainStringValue("brandId", null));
			
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);
			String format = obtainStringValue("format", "table");

			if(!StringUtils.hasText(entityId)) throw ASExceptionHelper.invalidArgumentsException("entityId");
			
			// Initializes the table using the received information
			DashboardTableRep table = new DashboardTableRep();
			List<Store> tmpStores2 = CollectionFactory.createList();
			List<Store> stores = StringUtils.hasText(entityId) ?
					storeDao.getUsingIdList(CollectionFactory.createList(entityId.split(","))) :
						storeDao.getUsingBrandAndStatus(brandId.get(0), StatusHelper.statusActive(), null);
			for( Store store : stores) {
			 	if( isValidForUser(user, store) ) tmpStores2.add(store);
			}// gets all stores to display
			Collections.sort(tmpStores2, new Comparator<Store>() {
				@Override
				public int compare(Store o1, Store o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			table.setStores(tmpStores2);
			List<String> entityIds = initializeTableRecords(table, fromStringDate, toStringDate);

			// Starts to Collect the data
			
			// peasents, visits, and tickets
			for(DashboardIndicatorData obj : dao.getUsingFilters(brandId, null, Arrays.asList("apd_visitor"),
					Arrays.asList("visitor_total_peasents", "visitor_total_visits", "visitor_total_tickets",
							"visitor_total_items", "visitor_total_revenue"), null, entityIds, null,
					fromStringDate, toStringDate, null, null, null, null, null, null, null, null)) {

				DashboardRecordRep rec = obj.getEntityKind() == EntityKind.KIND_INNER_ZONE ?
						table.findRecordWithEntityId(obj.getEntityId(), EntityKind.KIND_INNER_ZONE, obj.getSubentityName())
						: table.findRecordWithEntityId(obj.getSubentityId(), EntityKind.KIND_STORE, obj.getSubentityName());

				DashboardRecordRep totals = table.getTotals();

				if(rec == null) continue;
				switch(obj.getElementSubId()) {
				case "visitor_total_peasents" :
					rec.setPeasants(rec.getPeasants() + obj.getDoubleValue().longValue());
					break;
				case "visitor_total_visits" :
					rec.setVisitors(rec.getVisitors() + obj.getDoubleValue().longValue());
					rec.addToDateCache(obj.getDoubleValue().longValue(), obj.getStringDate());
					totals.addToDateCache(obj.getDoubleValue().longValue(), obj.getStringDate());
					break;
				case "visitor_total_tickets" :
					rec.setTickets(rec.getTickets() + obj.getDoubleValue().longValue());
					break;
				case "visitor_total_items" :
					rec.setItems(rec.getItems() + (int) obj.getDoubleValue().longValue());
					break;
				case "visitor_total_revenue" :
					rec.setRevenue(rec.getRevenue() + obj.getDoubleValue());
				}
					
			}

			// permanence
			for(DashboardIndicatorData obj : dao.getUsingFilters(brandId, null, Arrays.asList("apd_permanence"),
					Arrays.asList("permanence_hourly_visits"), null, entityIds, null, fromStringDate, toStringDate,
					null, null, null, null, null, null, null, null)) {
				DashboardRecordRep rec = table.findRecordWithEntityId(obj.getSubentityId(), null, obj.getSubentityName());
				if( rec == null ) continue;
				if( obj.getDoubleValue() != null )
					rec.setPermanenceInMillis(rec.getPermanenceInMillis() + obj.getDoubleValue().longValue());
				else log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());
				if( obj.getRecordCount() != null ) rec.setPermancenceQty(rec.getPermancenceQty() + obj.getRecordCount());
				else log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());
			}

			// Creates the final JSON Array
			Collection<DashboardRecordRep> values = table.records.values();
			if( format.equals("json")) {

				JSONObject resp = new JSONObject();
				JSONArray data = new JSONArray();
				for( DashboardRecordRep r : values ) data.put(r.toJSONObject());
				resp.put("data", data);
				resp.put("totals", table.toJSONTotals(values));
				return resp.toString();

			} else {
				JSONArray jsonArray = new JSONArray();
				jsonArray.put(table.getJSONHeaders());
				table.addJSONRecords(jsonArray, values);
				jsonArray.put(table.getJSONTotals(values));

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

	public List<String> initializeTableRecords(DashboardTableRep table, String fromStringDate, String toStringDate)
			throws ASException {

		List<String> ret = CollectionFactory.createList();

		for (Store store : table.getStores()) {
			table.getRecords().put(new DashboardRecordRepKey(EntityKind.KIND_STORE, store.getIdentifier(), store.getName()),
					new DashboardRecordRep(table, 0, store.getIdentifier(), EntityKind.KIND_STORE, store.getName(),
							fromStringDate, toStringDate));
			
			if(StringUtils.hasText(store.getIdentifier())) ret.add(store.getIdentifier());

			for (InnerZone zonel1 : innerZoneDao.getUsingEntityIdAndRange(store.getIdentifier(),
					EntityKind.KIND_STORE, null, "name", null, true)) {
				DashboardRecordRep drr = new DashboardRecordRep(null, 1, zonel1.getIdentifier(), EntityKind.KIND_INNER_ZONE,
						zonel1.getName(), fromStringDate, toStringDate);
				table.getRecords().put(new DashboardRecordRepKey(EntityKind.KIND_INNER_ZONE, zonel1.getIdentifier(),
						zonel1.getName()), drr);
				
				if(StringUtils.hasText(zonel1.getIdentifier())) ret.add(zonel1.getIdentifier());

				List<InnerZone> zonesl2 = innerZoneDao.getUsingEntityIdAndRange(zonel1.getIdentifier(),
						EntityKind.KIND_INNER_ZONE, null, "name", null, true);
				if( zonesl2.size() > 0 ) drr.setHeader(true);

				for (InnerZone zonel2 : zonesl2) {
					table.getRecords().put(new DashboardRecordRepKey(EntityKind.KIND_INNER_ZONE, zonel2.getIdentifier(),
							zonel2.getName()), new DashboardRecordRep(null, 2, zonel2.getIdentifier(),
									EntityKind.KIND_INNER_ZONE, zonel2.getName(), fromStringDate, toStringDate));
					
					if(StringUtils.hasText(zonel2.getIdentifier())) ret.add(zonel2.getIdentifier());
				}
			}
		}
		
		return ret;

	}

	public class DashboardTableRep {
		private List<Store> stores;
		private Map<DashboardRecordRepKey, DashboardRecordRep> records;
		private DashboardRecordRep totals;

		public DashboardTableRep() {
			stores = CollectionFactory.createList();
			records = CollectionFactory.createMap(Hashtable.class);
			totals = new DashboardRecordRep(null, 0, null, 0, "Totales", null, null);
		}

		public DashboardRecordRep findRecordWithEntityId(String entityId, Integer entityKind, String entityName) {
			return records.get(new DashboardRecordRepKey(entityKind, entityId, entityName));
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
		public Map<DashboardRecordRepKey, DashboardRecordRep> getRecords() {
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
		public void setRecords(Map<DashboardRecordRepKey, DashboardRecordRep> records) {
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
			titles.put("Paseantes");
			titles.put("Visitantes");
			titles.put("Tickets");
			titles.put("Items");
			titles.put("Revenue");
			titles.put("Visitantes/Paseantes");
			titles.put("Visitantes/Tickets");
			titles.put("Día más Alto");
			titles.put("Día más Bajo");
			titles.put("Permanencia Promedio");

			return titles;
		}

		/**
		 * Gets the totals JSON Array
		 * @return
		 * @throws ASException
		 */
		public JSONArray getJSONTotals(Collection<DashboardRecordRep> records) throws ASException {

			DashboardRecordRep totals = new DashboardRecordRep(null, 0, null, 0, "Totales", null, null);
			List<Long> c = CollectionFactory.createList();

			for( DashboardRecordRep rec : records ) {
				if( rec.getLevel() == 0 ) {
					totals.setPeasants(totals.getPeasants() + rec.getPeasants());
					totals.setVisitors(totals.getVisitors() + rec.getVisitors());
					totals.setTickets(totals.getTickets() + rec.getTickets());
					totals.setItems(totals.getItems() + rec.getItems());
					totals.setRevenue(totals.getRevenue() + rec.getRevenue());
					c.add(rec.getPermanenceInMillis());

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
				}
			}

			Collections.sort(c);
			if( c.size() == 0 )
				totals.setPermanenceInMillis(0l);
			else
				if( c.size() % 2 == 0 && c.size() >= 2 ) {
					int med = (int)(c.size() / 2);
					totals.setPermanenceInMillis((c.get(med-1) + c.get(med)) / 2);
				} else
					totals.setPermanenceInMillis(c.get((int)Math.floor(c.size() / 2)));

			return totals.toJSONArray();

		}

		/**
		 * Gets the totals JSON Array
		 * @return
		 * @throws ASException
		 */
		public JSONObject toJSONTotals(Collection<DashboardRecordRep> records) throws ASException {

			totals.setParent(null);
			totals.setPeasants(0L);
			totals.setVisitors(0L);
			totals.setRevenue(0.0);
			totals.setItems(0);
			totals.setPermanenceInMillis(0L);
			totals.setPermancenceQty(0);

			for( DashboardRecordRep rec : records ) {
				if( rec.getLevel() != 0 ) continue;
				totals.setPeasants(totals.getPeasants() + rec.getPeasants());
				totals.setVisitors(totals.getVisitors() + rec.getVisitors());
				totals.setTickets(totals.getTickets() + rec.getTickets());
				totals.setItems(totals.getItems() + rec.getItems());
				totals.setRevenue(totals.getRevenue() + rec.getRevenue());
				totals.setPermanenceInMillis(totals.getPermanenceInMillis() + rec.getPermanenceInMillis());
				totals.setPermancenceQty(totals.getPermancenceQty() + rec.getPermancenceQty());
			}

			return totals.toJSONObject();

		}

		/**
		 * Adds all records in a JSON Array
		 * @param array
		 * @return
		 * @throws ASException
		 */
		public JSONArray addJSONRecords(JSONArray array, Collection<DashboardRecordRep> records) throws ASException {

			if( array == null) array = new JSONArray();

			for( DashboardRecordRep rec : records ) array.put(rec.toJSONArray());

			return array;
		}
		
	}

	public class DashboardRecordRep {
		private boolean header;
		private int level;
		private String entityId;
		private int entityKind;
		private String title;
		private long peasants;
		private long visitors;
		private int items;
		private long tickets;
		private double revenue;
		private Date higherDate;
		private Date lowerDate;
		private long permanenceInMillis;
		private int permancenceQty;
		private Map<String, Long> datesCache;
		private DashboardTableRep parent;

		public DashboardRecordRep(DashboardTableRep parent, int level, String entityId, int entityKind, String title,
				String fromStringDate, String toStringDate) {
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
			permanenceInMillis = 0;

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
			for( int x = 0; x < level; x++ ) tit = "&nbsp;&nbsp;" + tit;

			row.put(h1 + tit + h2);
			row.put(h1 + String.valueOf(peasants) + h2);
			row.put(h1 + String.valueOf(visitors) + h2);
			row.put(h1 + String.valueOf(tickets) + h2);
			row.put(h1 + String.valueOf(items)    + h2);
			row.put(h1 + String.valueOf(revenue) + h2);

			// peasents_conversion
			if( peasants != 0)
				row.put(h1 + df.format(visitors * 100 / peasants) + "%" + h2);
			else
				row.put(h1 + df.format(0) + "%" + h2);

			// tickets_conversion
			if( visitors != 0)
				row.put(h1 + df.format(tickets * 100 / visitors) + "%" + h2);
			else
				row.put(h1 + df.format(0) + "%" + h2);

			row.put(h1 + calculateHigherDay() + h2);
			row.put(h1 + calculateLowerDay() + h2);

			row.put(h1 + String.valueOf(Math.round(permanenceInMillis / 60000)) + " mins"  + h2);

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
			row.put("tickets", tickets);
			row.put("items", items);
			row.put("revenue", revenue);

			// peasents_conversion
			if( peasants != 0)
				row.put("visitsConversion", (float)((float)(visitors * 100) / peasants));
			else
				row.put("visitsConversion", 0);

			// tickets_conversion
			if( visitors != 0)
				row.put("ticketsConversion", (float)((float)(tickets * 100) / visitors));
			else
				row.put("ticketsConversion", 0);

			row.put("higherDay", calculateHigherDay());
			row.put("lowerDay", calculateLowerDay());

			if( permanenceInMillis > 0 && permancenceQty > 0 ) {
				row.put("averagePermanence", Math.round(permanenceInMillis / permancenceQty / 60000D ));
			} else {
				row.put("averagePermanence", 0);
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

		public void setPermancenceQty(int permancenceQty) {
			this.permancenceQty = permancenceQty;
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
		public long getPermanenceInMillis() {
			return permanenceInMillis;
		}

		/**
		 * @param permanenceInMillis the permanenceInMillis to set
		 */
		public void setPermanenceInMillis(long permanenceInMillis) {
			this.permanenceInMillis = permanenceInMillis;
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
		
		@Override
		public int hashCode() {
			int prime = 7919;
			int result = prime *this.entityKind;
			result += this.entityId == null ? 0 : this.entityId.hashCode();
			result += prime *this.level;
			result += this.title == null ? 0 : this.title.hashCode();
			result += prime *this.peasants;
			result += prime *this.visitors;
			result += prime *this.items;
			result += prime *this.tickets;
			result += (int) (prime *this.revenue);
			result += this.higherDate == null ? 0 : this.higherDate.hashCode();
			result += this.lowerDate == null ? 0 : this.lowerDate.hashCode();
			result += prime *this.permanenceInMillis;
			result += prime *this.permancenceQty;
			return result;
		}
		
		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(o instanceof DashboardRecordRep && hashCode() == o.hashCode()) {
				DashboardRecordRep drr = (DashboardRecordRep) o;
				return drr.header == this.header && drr.level == this.level && drr.entityId.equals(this.entityId) &&
						drr.entityKind == this.entityKind && drr.title.equals(this.title) && drr.peasants == this.peasants
						&& drr.visitors == this.visitors && drr.items == this.items && drr.tickets == this.tickets &&
						drr.revenue == this.revenue && drr.higherDate.equals(this.higherDate) &&
						drr.lowerDate.equals(this.lowerDate) && drr.permanenceInMillis == this.permanenceInMillis &&
						drr.permancenceQty == this.permancenceQty;
			} return false;
		}

	}
	
	private final class DashboardRecordRepKey {
		
		private Integer entityKind;
		private String entityId;
		private String entityName;
		
		private DashboardRecordRepKey(Integer eKind, String eId, String entityName) {
			entityKind = eKind;
			entityId = eId;
			this.entityName = entityName;
		}
		
		@Override
		public int hashCode() {
			int prime = 7919;
			int result = prime *(this.entityId == null ? 0 : this.entityId.hashCode());
			result += prime *(this.entityName == null ? 0 : this.entityName.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(o instanceof DashboardRecordRepKey && hashCode() == o.hashCode()) {
				DashboardRecordRepKey drr = (DashboardRecordRepKey) o;
				return ((drr.entityName == null && this.entityName == null) || (drr.entityName.equals(this.entityName))) &&
						((drr.entityId == null && this.entityId == null) || (drr.entityId.equals(this.entityId))) &&
						(drr.entityKind == null || drr.entityKind.equals(this.entityKind));
			} return false;
		}
		
	}
}
