/**
 * 
 */
package mx.getin.bdb.dashboard.bz.spi;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.BrandDAO;
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
import mobi.allshoppings.model.UserSecurity;
import mobi.allshoppings.model.tools.StatusHelper;
import mobi.allshoppings.tools.CollectionFactory;
import mx.getin.Constants;

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
	private BrandDAO brandDao;
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
	public String retrieve() {
		long start = markStart();
		try {
			// obtain the id and validates the auth token
			User user = getUserFromToken();

			String entityId = obtainStringValue("entityId", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);
			String format = obtainStringValue("format", "table");
			String region = obtainStringValue("region", null);
			String storeFormat = obtainStringValue("storeFormat", null);
			String district = obtainStringValue("district", null);

			// Initializes the table using the received information
			DashboardTableRep table = new DashboardTableRep();
			List<Store> tmpStores2 = CollectionFactory.createList();
			String userBrand;
			
			for( Store store : storeDao.getUsingRegionAndFormatAndDistrict(entityId, null, null,
					StatusHelper.statusActive(), region, storeFormat, district, "name") ) {
			 	if( isValidForUser(user, store) ) tmpStores2.add(store);
			} switch(user.getSecuritySettings().getRole()) {
			case UserSecurity.Role.ADMIN :
			case UserSecurity.Role.COUNTRY_ADMIN :
				userBrand = "";
				break;
			default :
				UserSecurity sec = user.getSecuritySettings();
				if(sec.getBrands() != null && !sec.getBrands().isEmpty()) {
					userBrand = sec.getBrands().get(0);
				} else {
					try { 
						userBrand = brandDao.get(user.getIdentifier()).getName();
					} catch(ASException e) {
						userBrand = "";
					}
				}
			}
			table.setStores(tmpStores2);
			Collections.sort(table.getStores(), new Comparator<Store>() {
				@Override
				public int compare(Store o1, Store o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			LinkedList<String> entityIds = new LinkedList<>();
			entityIds.add(entityId);
			entityIds.addAll(initializeTableRecords(table, fromStringDate, toStringDate, userBrand));
			
			List<DashboardIndicatorData> list = dao.getUsingFilters(entityIds, null, Arrays.asList("apd_visitor"),
					Arrays.asList("visitor_total_peasents", "visitor_total_visits", "visitor_total_tickets",
							"visitor_total_items", "visitor_total_revenue", "visitor_total_viewer"), null, null, null,
					fromStringDate, toStringDate, null, null, null, null, null, null, null, null);

			for(DashboardIndicatorData obj : list) {

				DashboardRecordRep rec = table.findRecordWithEntityId(obj.getSubentityId());

				// solo se toma en cuenta la zona de gabinete como zona
				if( null != rec ) {
					if( obj.getElementSubId().equals("visitor_total_peasents"))
						rec.setPeasants(rec.getPeasants() + obj.getDoubleValue().longValue());
					else if( obj.getElementSubId().equals("visitor_total_visits")) {
						if(obj.getEntityKind().equals(EntityKind.KIND_INNER_ZONE)) {
							if(obj.getSubentityName().toLowerCase().contains(" gabinete"))
								rec.setCabinets(rec.getCabinetes() + obj.getDoubleValue().longValue());
						} else {
							rec.setVisitors(rec.getVisitors() + obj.getDoubleValue().longValue());
							rec.addToDateCache(obj.getDoubleValue().longValue(), obj.getStringDate());
							table.getTotals().addToDateCache(obj.getDoubleValue().longValue(), obj.getStringDate());
						}
					} else if( obj.getElementSubId().equals("visitor_total_tickets")) {
						rec.setTickets(rec.getTickets() + obj.getDoubleValue().longValue());
					} else if( obj.getElementSubId().equals("visitor_total_items")) {
						rec.setItems(rec.getItems() + (int) obj.getDoubleValue().longValue());
					} else if( obj.getElementSubId().equals("visitor_total_revenue"))
						rec.setRevenue(rec.getRevenue() + obj.getDoubleValue());
					else if( obj.getElementSubId().equals("visitor_total_viewer")) {
						rec.setViewers(rec.getViewers() + obj.getDoubleValue().longValue());
					}	
				}
			}

			// permanence
			list = dao.getUsingFilters(entityIds, null, Arrays.asList("apd_permanence"),
					Arrays.asList("permanence_hourly_visits"), null, null, null, fromStringDate, toStringDate, null,
	 				null, null, null, null, null, null, null);
			for(DashboardIndicatorData obj : list) {
				DashboardRecordRep rec = table.findRecordWithEntityId(obj.getSubentityId());
				if( rec != null ) {
					if( obj.getDoubleValue() != null ) {
						if(obj.getEntityKind().equals(EntityKind.KIND_INNER_ZONE)) {
							if(obj.getSubentityName().toLowerCase().contains(" gabinete"))
								rec.addPermanenceCab(obj.getDoubleValue().longValue(), obj.getRecordCount());
							if(obj.getSubentityName().toLowerCase().contains(" exhibicion"))
								rec.addPermanenceExh(obj.getDoubleValue().longValue(), obj.getRecordCount());
						}
					} else log.log(Level.WARNING, "Inconsistent DashboardIndicator: " + obj.toString());
				}
			}

			// Creates the final JSON Array
			if( format.equals("json")) {

				JSONObject resp = new JSONObject();
				JSONArray data = new JSONArray();
				for(DashboardRecordRep r : table.records) {
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
		StringBuilder sb = new StringBuilder();
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
	 *          
	 */
	public List<String> initializeTableRecords(DashboardTableRep table, String fromStringDate, String toStringDate,
			String userBrand) throws ASException {

		HashSet<String> ret = new HashSet<>();
		
		for (Store store : table.getStores()) {
			
			List<String> ids = new ArrayList<>();
			
			List<InnerZone> zonesl1 = innerZoneDao.getUsingEntityIdAndRange(store.getIdentifier(),
					EntityKind.KIND_STORE, null, "name", null, true);

			for (InnerZone zonel1 : zonesl1) {
				ids.add(zonel1.getIdentifier());
		
				List<InnerZone> zonesl2 = innerZoneDao.getUsingEntityIdAndRange(zonel1.getIdentifier(),
						EntityKind.KIND_INNER_ZONE, null, "name", null, true);
				for (InnerZone zonel2 : zonesl2) {
					ids.add(zonel2.getIdentifier());
				}
			}
			
			ids.add(store.getIdentifier());
			
			table.getRecords().add(new DashboardRecordRep(table, 0, ids, store.getName().replaceFirst(userBrand, ""),
					fromStringDate, toStringDate));
			
			ret.addAll(ids);
		}
		
		return new ArrayList<>(ret);
	}

	public class DashboardTableRep {
		private List<Store> stores;
		private LinkedList<DashboardRecordRep> records;
		private DashboardRecordRep totals;

		public DashboardTableRep() {
			stores = CollectionFactory.createList();
			records = new LinkedList<>();
			totals = new DashboardRecordRep(null, 0, null, "Totales", null, null);
		}

		public DashboardRecordRep findRecordWithEntityId(String entityId) {
			for(DashboardRecordRep rec : records) {
				if(rec.entityIds.contains(entityId)) return rec;
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
		public LinkedList<DashboardRecordRep> getRecords() {
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
		 * Gets all the headers in a JSON Array
		 * @return
		 */
		public JSONArray getJSONHeaders() {

			// Titles row
			JSONArray titles = new JSONArray();

			titles.put("Tienda");
			titles.put("Paseantes");
			titles.put("Miradores");
			titles.put("Visitas");
			titles.put("Gabinetes");
			titles.put("Tickets");
			titles.put("Items");
			titles.put("Revenue");
			titles.put("Exhibici&ocuate;n/Mirador");
			titles.put("Ovs/Exhibici&oacute;n");
			titles.put("Gabinete/Exhibici&oacute;n");
			titles.put("D&iacute;a m&aacute;s Alto");
			titles.put("D&iacute;a m&aacute;s Bajo");
			titles.put("Permanencia Exhibici&oacute;n");
			titles.put("Permanencia Gabinete");
			titles.put("Permanencia &Oacute;ptica");
			
			return titles;
		}

		/**
		 * Gets the totals JSON Array
		 * @return
		 * @throws ASException
		 */
		public JSONArray getJSONTotals() throws ASException {

			DashboardRecordRep totals = new DashboardRecordRep(null, 0, null, "Totales", null, null);

			for( DashboardRecordRep rec : records) {
				if( rec.getLevel() == 0 ) {
					totals.setPeasants(totals.getPeasants() + rec.getPeasants());
					totals.setVisitors(totals.getVisitors() + rec.getVisitors());
					totals.setCabinets(totals.getCabinetes() + rec.getCabinetes());
					totals.setTickets(totals.getTickets() + rec.getTickets());
					totals.setItems(totals.getItems() + rec.getItems());
					totals.setRevenue(totals.getRevenue() + rec.getRevenue());
					totals.addPermanenceCab(totals.getPermanenceCab());
					totals.addPermanenceExh(totals.getPermanenceExh());
					totals.setViewers(totals.getViewers() +rec.getViewers());

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

			return totals.toJSONArray();

		}

		/**
		 * Gets the totals JSON Array
		 * @return
		 * @throws ASException
		 */
		public JSONObject toJSONTotals() throws ASException {

			totals.setParent(null);
			totals.setPeasants(0);
			totals.setVisitors(0);
			totals.setCabinets(0);
			totals.setRevenue(0);
			totals.setItems(0);
			totals.setViewers(0);

			for( DashboardRecordRep rec : records) {
				if( rec.getLevel() == 0 ) {
					totals.setPeasants(totals.getPeasants() + rec.getPeasants());
					totals.setVisitors(totals.getVisitors() + rec.getVisitors());
					totals.setCabinets(totals.getCabinetes() + rec .getCabinetes());
					totals.setTickets(totals.getTickets() + rec.getTickets());
					totals.setItems(totals.getItems() + rec.getItems());
					totals.setRevenue(totals.getRevenue() + rec.getRevenue());
					totals.addPermanenceCab(totals.getPermanenceCab());
					totals.addPermanenceExh(totals.getPermanenceExh());
					totals.setViewers(totals.getViewers() +rec.getViewers());
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

			for( DashboardRecordRep rec : records) array.put(rec.toJSONArray());

			return array;
		}
	}

	public class DashboardRecordRep {
		private boolean header;
		private int level;
		private String title;
		private long peasants;
		private long visitors;
		private long cabinet;
		private int items;
		private long tickets;
		private long viewers;
		private double revenue;
		private Date higherDate;
		private Date lowerDate;
		private LinkedList<Long> permanenceCab;
		private LinkedList<Long> permanenceExh;
		private ArrayList<String> entityIds;
		private Map<String, Long> datesCache;
		private DashboardTableRep parent;

		public DashboardRecordRep(DashboardTableRep parent, int level, List<String> entityIds,
				String title, String fromStringDate, String toStringDate) {
			this.parent = parent;

			this.level = level;
			this.title = title;

			peasants = 0;
			visitors = 0;
			tickets = 0;
			items = 0;
			revenue = 0;
			cabinet = 0;
			permanenceCab = new LinkedList<>();
			permanenceExh = new LinkedList<>();
			this.entityIds = new ArrayList<>();
			if(entityIds != null) this.entityIds.addAll(entityIds);
			try {
				datesCache = CollectionFactory.createMap();
				Date d1 = sdf.parse(fromStringDate);
				Date d2 = sdf.parse(toStringDate);
				while( d1.before(d2) || d1.equals(d2)) {
					datesCache.put(sdf.format(d1), 0l);
					d1.setTime(d1.getTime() +Constants.DAY_IN_MILLIS);
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
			row.put(h1 + String.valueOf(viewers) +h2);
			row.put(h1 + String.valueOf(visitors) + h2);
			row.put(h1 + String.valueOf(cabinet)+ h2);
			row.put(h1 + String.valueOf(tickets) + h2);
			row.put(h1 + String.valueOf(items)    + h2);
			row.put(h1 + String.valueOf(revenue) + h2);

			// peasents_conversion
			row.put(h1 + (peasants != 0 ? df.format(visitors * 100f / peasants) : 0) + "%" + h2);
			row.put(h1 + (cabinet != 0 ? df.format(cabinet* 100f / visitors) : 0) + "%" + h2);
			row.put(h1 + (viewers != 0 ? df.format(visitors *100f /viewers) : 0) + "%" +h2);
			// tickets_conversion
			row.put(h1 + (visitors != 0 ? df.format(tickets * 100f / visitors) : 0) + "%" + h2);
			row.put(h1 + calculateHigherDay() + h2);
			row.put(h1 + calculateLowerDay() + h2);

			row.put(h1 +(getExhMedian() / 60000) +"mins" +h2);
			row.put(h1 +(getCabMedian() / 60000) + " mins"  + h2);
			row.put(h1 +(getTotalMedian() / 60000) +" mins" +h2);
			
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
			row.put("viewers", viewers);
			row.put("visitors", visitors);
			row.put("cabinet", cabinet);
			row.put("tickets", tickets);
			row.put("items", items);
			row.put("revenue", revenue);

			// peasents_conversion
			row.put("visitsConversion", peasants == 0 ? 0 : (visitors * 100f) / peasants);
			row.put("viewerConversion", viewers == 0 ? 0 : (visitors *100f) /viewers);
			row.put("cabinetConversion", visitors == 0 ? 0 : (cabinet * 100f)/visitors);
			row.put("ticketsConversion", visitors == 0 ? 0 : (tickets * 100f) / visitors);

			row.put("higherDay", calculateHigherDay());
			row.put("lowerDay", calculateLowerDay());

			row.put("averagePermanenceEntrance", getExhMedian() / 60000);
			row.put("averagePermanenceCabinet", getCabMedian() / 60000);
			row.put("averagePermanenceAll", getTotalMedian() / 60000);
			
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
		public void setRevenue(double revenue) {
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
		public void setPeasants(long peasants) {
			this.peasants = peasants;
		}

		/**
		 * @return the visitors
		 */
		public long getVisitors() {
			return visitors;
		}

		/**
		 * @param visitors the visitors to set
		 */
		public void setVisitors(long visitors) {
			this.visitors = visitors;
		}
		
		/**
		 * @param visitors cabinet the visitors to set
		 */
		public void setCabinets(long cabinet) {
			this.cabinet = cabinet;
		}
		
		/**
		 * @param return cabinet
		 */
		public long getCabinetes() {
			return cabinet;
		}

		/**
		 * @return the tickets
		 */
		public long getTickets() {
			return tickets;
		}

		/**
		 * @param tickets the tickets to set
		 */
		public void setTickets(long tickets) {
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
		public List<Long> getPermanenceCab() {
			return permanenceCab;
		}
		
		/**
		 * @return the permanenceInMillisGab
		 */
		public List<Long> getPermanenceExh() {
			return permanenceExh;
		}

		/**
		 * @param permanenceInMillis the permanenceInMillis to set
		 */
		public void addPermanenceCab(long permanenceInMillis, long repetitions) {
			permanenceCab.add(permanenceInMillis /repetitions);
		}
		
		private void addPermanenceCab(List<Long> permanecnes) {
			permanenceCab.addAll(permanecnes);
		}
		
		/**
		 * @param permanenceInMillis the permanenceInMillis to set in gabinete
		 */
		public void addPermanenceExh(long permanenceInMillis, long repetitions) {
			permanenceExh.add(permanenceInMillis /repetitions);
		}
		
		private void addPermanenceExh(List<Long> permanences) {
			permanenceExh.addAll(permanences);
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
		
		public long getViewers() {
			return viewers;
		}

		public void setViewers(long viewers) {
			this.viewers = viewers;
		}
		
		private long getCabMedian() {
			switch(permanenceCab.size()) {
			case 0 :
				return 0;
			case 1 :
				return permanenceCab.get(0);
			case 2 :
				return (permanenceCab.get(0) +permanenceCab.get(1)) /2;
			default :
				Long[] permanences = permanenceCab.toArray(new Long[0]);
				Arrays.sort(permanences);
				return permanences.length % 2 == 0 ? (permanences[permanences.length /2]
						+permanences[(permanences.length /2) +1]) /2 : permanences[permanences.length /2];
			}
		}
		
		private long getExhMedian() {
			switch(permanenceExh.size()) {
			case 0 :
				return 0;
			case 1 :
				return permanenceExh.get(0);
			case 2 :
				return (permanenceExh.get(0) +permanenceExh.get(1)) /2;
			default :
				Long[] permancens = permanenceExh.toArray(new Long[0]);
				Arrays.sort(permancens);
				return permancens.length % 2 == 0 ? (permancens[permancens.length /2]
						+permancens[(permancens.length /2) +1]) /2 : permancens[permancens.length /2];
			}
		}
		
		private long getTotalMedian() {
			LinkedList<Long> permanences = new LinkedList<>();
			permanences.addAll(permanenceCab);
			permanences.addAll(permanenceExh);
			switch(permanences.size()) {
			case 0 :
				return 0;
			case 1 :
				return permanences.get(0);
			case 2 :
				return (permanences.get(0) +permanences.get(1)) /2;
			default :
				Long[] perms = permanences.toArray(new Long[0]);
				Arrays.sort(perms);
				return perms.length %2 == 0 ? (perms[perms.length /2] +perms[(perms.length /2) +1]) /2 :
					perms[perms.length /2];
			}
		}

	}
	
}
