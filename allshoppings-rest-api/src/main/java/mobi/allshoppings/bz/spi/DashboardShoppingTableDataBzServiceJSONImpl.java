package mobi.allshoppings.bz.spi;


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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.bz.DashboardShoppingTableDataBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.dao.InnerZoneDAO;
import mobi.allshoppings.dao.ShoppingDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.InnerZone;
import mobi.allshoppings.model.Shopping;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.CollectionFactory;


/**
 *
 */
public class DashboardShoppingTableDataBzServiceJSONImpl
extends RestBaseServerResource
implements DashboardShoppingTableDataBzService {

	private static final Logger log = Logger.getLogger(DashboardShoppingTableDataBzServiceJSONImpl.class.getName());

	@Autowired
	private DashboardIndicatorDataDAO dao;
	@Autowired
	private ShoppingDAO shoppingDao;
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
			@SuppressWarnings("unused")
			User user = getUserFromToken();

			String entityId = obtainStringValue("entityId", null);
			@SuppressWarnings("unused")
			Integer entityKind = obtainIntegerValue("entityKind", null);
			@SuppressWarnings("unused")
			String subentityId = obtainStringValue("subentityId", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);
			String country = obtainStringValue("country", null);
			String province = obtainStringValue("province", null);
			String city = obtainStringValue("city", null);

			// Initializes the table using the received information
			DashboardTableRep table = new DashboardTableRep();
			table.setShoppings(shoppingDao.getUsingIdList(Arrays.asList(entityId.split(","))));
			Collections.sort(table.getShoppings(), new Comparator<Shopping>() {
				@Override
				public int compare(Shopping o1, Shopping o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			List<String> entityIds = initializeTableRecords(table, fromStringDate, toStringDate);

			// Starts to Collect the data
			List<DashboardIndicatorData> list;

			// peasents, visits, and tickets
			list = dao.getUsingFilters(entityIds, null, Arrays.asList("apd_visitor"),
					Arrays.asList("visitor_total_peasents", "visitor_total_visits", "visitor_total_tickets"), null,
					null, null, fromStringDate, toStringDate, null, null, null, null, null, country,
					province, city);

			for(DashboardIndicatorData obj : list) {

				DashboardRecordRep rec = table.findRecordWithEntityId(obj.getEntityId(), obj.getEntityKind());
				if( null != rec ) {
					if( obj.getElementSubId().equals("visitor_total_peasents"))
						rec.setPeasants(rec.getPeasants() + obj.getDoubleValue().longValue());
					else if( obj.getElementSubId().equals("visitor_total_visits")) {
						rec.setVisitors(rec.getVisitors() + obj.getDoubleValue().longValue());
						rec.addToDateCache(obj.getDoubleValue().longValue(), obj.getStringDate());
					} else if( obj.getElementSubId().equals("visitor_total_tickets"))
						rec.setTickets(rec.getTickets() + obj.getDoubleValue().longValue());
				}
			}

			// permanence
			list = dao.getUsingFilters(entityIds,
					null, Arrays.asList("apd_permanence"), Arrays.asList("permanence_hourly_visits"), null,
					null, null, fromStringDate, toStringDate,
					null, null, null, null, null, country, province, city);

			{
				Map<String, List<Long>> d = CollectionFactory.createMap();
				for( DashboardIndicatorData obj : list )
					try {
						List<Long> c = d.get(obj.getEntityId());
						if( c == null ) c = CollectionFactory.createList();
						c.add((long)(obj.getDoubleValue() / obj.getRecordCount()));
						d.put(obj.getEntityId(), c);
					} catch( Exception e ){}

				Iterator<String> i = d.keySet().iterator();
				while( i.hasNext() ) {
					String key = i.next();
					DashboardRecordRep rec = table.findRecordWithEntityId(key, null);
					if( null != rec ) {

						List<Long> c = d.get(key);
						Collections.sort(c);
						if( c.size() == 0 ) 
							rec.setPermanenceInMillis(0l);
						else
							if( c.size() % 2 == 0 && c.size() >= 2 ) {
								int med = (int)(c.size() / 2);
								rec.setPermanenceInMillis((c.get(med-1) + c.get(med)) / 2);
							} else
								rec.setPermanenceInMillis(c.get((int)Math.floor(c.size() / 2)));
					}
				}
			}

			// Creates the final JSON Array
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(table.getJSONHeaders());
			table.addJSONRecords(jsonArray);
			jsonArray.put(table.getJSONTotals());

			// Returns the final value
			return jsonArray.toString();

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

		for (Shopping shopping : table.getShoppings()) {
			table.getRecords().add(new DashboardRecordRep(table, 0, shopping.getIdentifier(), EntityKind.KIND_SHOPPING,
					shopping.getName(), fromStringDate, toStringDate));

			List<InnerZone> zonesl1 = innerZoneDao.getUsingEntityIdAndRange(shopping.getIdentifier(),
					EntityKind.KIND_SHOPPING, null, "name", null, true);

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
		private List<Shopping> shoppings;
		private List<DashboardRecordRep> records;

		public DashboardTableRep() {
			shoppings = CollectionFactory.createList();
			records = CollectionFactory.createList();
		}

		public DashboardRecordRep findRecordWithEntityId(String entityId, Integer entityKind) {
			for(DashboardRecordRep rec : records ) {
				if( rec.getEntityId().equals(entityId) && (entityKind == null || rec.getEntityKind().equals(entityKind))) 
					return rec;
			}
			return null;
		}

		/**
		 * @return the shoppings
		 */
		public List<Shopping> getShoppings() {
			return shoppings;
		}
		/**
		 * @param shoppings the shoppings to set
		 */
		public void setShoppings(List<Shopping> shoppings) {
			this.shoppings = shoppings;
		}
		/**
		 * @return the records
		 */
		public List<DashboardRecordRep> getRecords() {
			return records;
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
			titles.put("Paseantes");
			titles.put("Visitantes");
			titles.put("Tickets");
			titles.put("Paseantes/Visitantes");
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
		public JSONArray getJSONTotals() throws ASException {

			DashboardRecordRep totals = new DashboardRecordRep(null, 0, null, null, "Totales", null, null);
			List<Long> c = CollectionFactory.createList();

			for( DashboardRecordRep rec : records ) {
				if( rec.getLevel() == 0 ) {
					totals.setPeasants(totals.getPeasants() + rec.getPeasants());
					totals.setVisitors(totals.getVisitors() + rec.getVisitors());
					totals.setTickets(totals.getTickets() + rec.getTickets());
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
		private Long tickets;
		private Date higherDate;
		private Date lowerDate;
		private Long permanenceInMillis;
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
			permanenceInMillis = 0l;

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
			row.put(h1 + String.valueOf(tickets) + h2);

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
		public Long getPermanenceInMillis() {
			return permanenceInMillis;
		}

		/**
		 * @param permanenceInMillis the permanenceInMillis to set
		 */
		public void setPermanenceInMillis(Long permanenceInMillis) {
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

	}
}
