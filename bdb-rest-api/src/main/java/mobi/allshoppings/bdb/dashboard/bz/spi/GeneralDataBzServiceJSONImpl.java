package mobi.allshoppings.bdb.dashboard.bz.spi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;

import mobi.allshoppings.bdb.bz.BDBDashboardBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.dao.DashboardIndicatorDataDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.DashboardIndicatorData;
import mobi.allshoppings.model.User;
import mobi.allshoppings.tools.CollectionFactory;

/**
 *
 */
public class GeneralDataBzServiceJSONImpl
extends BDBRestBaseServerResource
implements BDBDashboardBzService {

	private static final Logger log = Logger.getLogger(GeneralDataBzServiceJSONImpl.class.getName());

	@Autowired
	private DashboardIndicatorDataDAO dao;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM");

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
			/*@SuppressWarnings("unused")
			Integer entityKind = obtainIntegerValue("entityKind", null);*/
			String subentityId = obtainStringValue("subentityId", null);
			String fromStringDate = obtainStringValue("fromStringDate", null);
			String toStringDate = obtainStringValue("toStringDate", null);

			// Initializes the table using the received information
			GeneralDataRep info = new GeneralDataRep();

			List<String> entityIds = CollectionFactory.createList();
			entityIds.add(entityId);

			List<String> subentityIds = CollectionFactory.createList();
			if( StringUtils.hasText(subentityId)) subentityIds.add(subentityId);

			// Starts to Collect the data
			List<DashboardIndicatorData> list;

			// peasents, visits, and tickets
			list = dao.getUsingFilters(entityIds, (byte) -1, Arrays.asList("apd_visitor"),
					Arrays.asList("visitor_total_peasents", "visitor_total_visits", "visitor_total_tickets", "visitor_total_revenue"), null,
					subentityIds, null, fromStringDate, toStringDate, null, null, (byte) -1, (byte) -1, null, null,
					null, null);

			for(DashboardIndicatorData obj : list) {
				if( obj.getElementSubId().equals("visitor_total_peasents")) {
					info.setPeasants(info.getPeasants() + obj.getDoubleValue().longValue());
				} else if( obj.getElementSubId().equals("visitor_total_visits")) {
					info.setVisits(info.getVisits() + obj.getDoubleValue().longValue());
					Long val = info.getVisitsByDate().get(obj.getStringDate());
					if( val == null ) val = 0L;
					val += obj.getDoubleValue().longValue();
					info.getVisitsByDate().put(obj.getStringDate(), val);
				} else if( obj.getElementSubId().equals("visitor_total_tickets")) {
					info.setTickets(info.getTickets() + obj.getDoubleValue().longValue());
					Long val = info.getTicketsByDate().get(obj.getStringDate());
					if( val == null ) val = 0L;
					val += obj.getDoubleValue().longValue();
					info.getTicketsByDate().put(obj.getStringDate(), val);
				} else if( obj.getElementSubId().equals("visitor_total_revenue")) {
					info.setRevenue(info.getRevenue() + obj.getDoubleValue().longValue());
					Long val = info.getRevenueByDate().get(obj.getStringDate());
					if( val == null ) val = 0L;
					val += obj.getDoubleValue().longValue();
					info.getRevenueByDate().put(obj.getStringDate(), val);
				}
			}

			// permanence
			list = dao.getUsingFilters(entityIds,
					(byte) -1, Arrays.asList("apd_permanence"), Arrays.asList("permanence_hourly_visits"), null,
					subentityIds, null, fromStringDate, toStringDate,
					null, null, (byte) -1, (byte) -1, null, null, null, null);

			for( DashboardIndicatorData obj : list ) {
				try {
					info.getPermanenceMedianList().add((long)(obj.getDoubleValue() / obj.getRecordCount()));
				} catch( Exception e ){}
			}

			JSONObject resp = new JSONObject(new Gson().toJson(info.toSummary()));
			return resp.toString();

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

	public String getDateName(String date) throws ParseException {
		return getDateName(sdf.parse(date));
	}
	
	public String getDateName(Date date) {
		StringBuffer sb = new StringBuffer();

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dof = cal.get(Calendar.DAY_OF_WEEK);

		switch(dof) {
		case Calendar.SUNDAY:
			sb.append("Domingo");
			break;
		case Calendar.MONDAY:
			sb.append("Lunes");
			break;
		case Calendar.TUESDAY:
			sb.append("Martes");
			break;
		case Calendar.WEDNESDAY:
			sb.append("Miercoles");
			break;
		case Calendar.THURSDAY:
			sb.append("Jueves");
			break;
		case Calendar.FRIDAY:
			sb.append("Viernes");
			break;
		case Calendar.SATURDAY:
			sb.append("Sabado");
			break;
		}

		return sb.toString();
	}

	public String getSmallDateName(String date) {
		try {
			return getSmallDateName(sdf.parse(date));
		} catch( Throwable T ) {
			return "-";
		}
	}
	
	public String getSmallDateName(Date date) {
		StringBuffer sb = new StringBuffer();

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int dof = cal.get(Calendar.DAY_OF_WEEK);

		switch(dof) {
		case Calendar.SUNDAY:
			sb.append("Dom");
			break;
		case Calendar.MONDAY:
			sb.append("Lun");
			break;
		case Calendar.TUESDAY:
			sb.append("Mar");
			break;
		case Calendar.WEDNESDAY:
			sb.append("Mie");
			break;
		case Calendar.THURSDAY:
			sb.append("Jue");
			break;
		case Calendar.FRIDAY:
			sb.append("Vie");
			break;
		case Calendar.SATURDAY:
			sb.append("Sab");
			break;
		}

		return sb.toString();
	}

	public class GeneralDataSummary {
		private double revenue;
		private long peasants;
		private long visits;
		private long tickets;
		private double avgTickets;
		private long permanenceMedian;
		private long avgVisits;
		private String higherDate;
		private String lowerDate;
		private List<PerformanceSummary> performance;
		
		public GeneralDataSummary() {
			super();
			performance = CollectionFactory.createList();
		}

		/**
		 * @return the revenue
		 */
		public double getRevenue() {
			return revenue;
		}

		/**
		 * @return the performance
		 */
		public List<PerformanceSummary> getPerformance() {
			return performance;
		}

		/**
		 * @param performance the performance to set
		 */
		public void setPerformance(List<PerformanceSummary> performance) {
			this.performance = performance;
		}

		/**
		 * @param revenue the revenue to set
		 */
		public void setRevenue(double revenue) {
			this.revenue = revenue;
		}

		/**
		 * @return the peasants
		 */
		public long getPeasants() {
			return peasants;
		}

		/**
		 * @param peasants the peasants to set
		 */
		public void setPeasants(long peasants) {
			this.peasants = peasants;
		}

		/**
		 * @return the visits
		 */
		public long getVisits() {
			return visits;
		}

		/**
		 * @param visits the visits to set
		 */
		public void setVisits(long visits) {
			this.visits = visits;
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
		 * @return the avgTickets
		 */
		public double getAvgTickets() {
			return avgTickets;
		}

		/**
		 * @param avgTickets the avgTickets to set
		 */
		public void setAvgTickets(double avgTickets) {
			this.avgTickets = avgTickets;
		}

		/**
		 * @return the permanenceMedian
		 */
		public long getPermanenceMedian() {
			return permanenceMedian;
		}

		/**
		 * @param permanenceMedian the permanenceMedian to set
		 */
		public void setPermanenceMedian(long permanenceMedian) {
			this.permanenceMedian = permanenceMedian;
		}

		/**
		 * @return the avgVisits
		 */
		public long getAvgVisits() {
			return avgVisits;
		}

		/**
		 * @param avgVisits the avgVisits to set
		 */
		public void setAvgVisits(long avgVisits) {
			this.avgVisits = avgVisits;
		}

		/**
		 * @return the higherDate
		 */
		public String getHigherDate() {
			return higherDate;
		}

		/**
		 * @param higherDate the higherDate to set
		 */
		public void setHigherDate(String higherDate) {
			this.higherDate = higherDate;
		}

		/**
		 * @return the lowerDate
		 */
		public String getLowerDate() {
			return lowerDate;
		}

		/**
		 * @param lowerDate the lowerDate to set
		 */
		public void setLowerDate(String lowerDate) {
			this.lowerDate = lowerDate;
		}

	}

	public class GeneralDataRep {
		private double revenue;
		private long peasants;
		private long visits;
		private long tickets;
		private List<Long> permanenceMedianList;

		private Map<String, Long> ticketsByDate;
		private Map<String, Long> visitsByDate;
		private Map<String, Long> revenueByDate;

		public GeneralDataRep() {
			super();

			permanenceMedianList = CollectionFactory.createList();
			ticketsByDate = CollectionFactory.createMap();
			visitsByDate = CollectionFactory.createMap();
			revenueByDate = CollectionFactory.createMap();
		}

		/**
		 * @return the ticketsByDate
		 */
		public Map<String, Long> getTicketsByDate() {
			return ticketsByDate;
		}

		/**
		 * @param ticketsByDate the ticketsByDate to set
		 */
		public void setTicketsByDate(Map<String, Long> ticketsByDate) {
			this.ticketsByDate = ticketsByDate;
		}

		/**
		 * @return the permanenceMedianList
		 */
		public List<Long> getPermanenceMedianList() {
			return permanenceMedianList;
		}

		/**
		 * @param permanenceMedianList the permanenceMedianList to set
		 */
		public void setPermanenceMedianList(List<Long> permanenceMedianList) {
			this.permanenceMedianList = permanenceMedianList;
		}

		/**
		 * @return the revenueByDate
		 */
		public Map<String, Long> getRevenueByDate() {
			return revenueByDate;
		}

		/**
		 * @param revenueByDate the revenueByDate to set
		 */
		public void setRevenueByDate(Map<String, Long> revenueByDate) {
			this.revenueByDate = revenueByDate;
		}

		/**
		 * @return the visitsByDate
		 */
		public Map<String, Long> getVisitsByDate() {
			return visitsByDate;
		}

		/**
		 * @param visitsByDate the visitsByDate to set
		 */
		public void setVisitsByDate(Map<String, Long> visitsByDate) {
			this.visitsByDate = visitsByDate;
		}

		/**
		 * @return the revenue
		 */
		public double getRevenue() {
			return revenue;
		}

		/**
		 * @param revenue the revenue to set
		 */
		public void setRevenue(double revenue) {
			this.revenue = revenue;
		}

		/**
		 * @return the peasants
		 */
		public long getPeasants() {
			return peasants;
		}

		/**
		 * @param peasants the peasants to set
		 */
		public void setPeasants(long peasants) {
			this.peasants = peasants;
		}

		/**
		 * @return the visits
		 */
		public long getVisits() {
			return visits;
		}

		/**
		 * @param visits the visits to set
		 */
		public void setVisits(long visits) {
			this.visits = visits;
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

		public GeneralDataSummary toSummary() {
			GeneralDataSummary sum = new GeneralDataSummary();

			sum.setRevenue(revenue);
			sum.setPeasants(peasants);
			sum.setVisits(visits);
			sum.setTickets(tickets);
			sum.setAvgTickets(revenue/tickets);
			
			{
				List<PerformanceEntry> performance = CollectionFactory.createList();
				for( int i = 0; i < 6; i++ ) {
					performance.add(new PerformanceEntry(null, 0L, 0L, 0L));
				}

				String maxDate = null;
				long maxTotal = 0;
				String minDate = null;
				long minTotal = 0;
				
				long total = 0;
				long count = 0;
				Iterator<String> it = visitsByDate.keySet().iterator();
				while( it.hasNext() ) {
					String key = it.next();
					Long val = visitsByDate.get(key);
					if( val != null ) {
						total += val;
						count++;
						
						if( val > maxTotal || maxDate == null ) {
							maxTotal = val;
							maxDate = key;
						}
						
						if( val < minTotal || minDate == null ) {
							minTotal = val;
							minDate = key;
						}
						
						PerformanceEntry pe = new PerformanceEntry(key, val, ticketsByDate.get(key), revenueByDate.get(key));
						if( pe.getVisits() > performance.get(0).getVisits()) {
							performance.set(2, performance.get(1));
							performance.set(1, performance.get(0));
							performance.set(0, pe);
						} else if( pe.getVisits() > performance.get(1).getVisits()) {
							performance.set(2, performance.get(1));
							performance.set(1, pe);
						} else if( pe.getVisits() > performance.get(2).getVisits()) {
							performance.set(2, pe);
						}
						
						if( pe.getVisits() < performance.get(5).getVisits() || performance.get(5).getVisits() == 0) {
							performance.set(3, performance.get(4));
							performance.set(4, performance.get(5));
							performance.set(5, pe);
						} else if( pe.getVisits() < performance.get(4).getVisits() || performance.get(4).getVisits() == 0) {
							performance.set(3, performance.get(4));
							performance.set(4, pe);
						} else if( pe.getVisits() < performance.get(3).getVisits() || performance.get(3).getVisits() == 0) {
							performance.set(3, pe);
						}
					}
				}
				
				if( count > 0 )
					sum.setAvgVisits(total/count);
				else
					sum.setAvgVisits(0);

				try {
					if( maxDate != null )
						sum.setHigherDate(getDateName(maxDate));
				} catch (ParseException e) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}

				try {
					if( minDate != null )
						sum.setLowerDate(getDateName(minDate));
				} catch (ParseException e) {
					log.log(Level.SEVERE, e.getMessage(), e);
				}
				
				for( PerformanceEntry pe : performance ) {
					sum.getPerformance().add(pe.toSummary());
				}
			}

			
			Collections.sort(permanenceMedianList);
			long permanenceInMillis = 0;
			if( permanenceMedianList.size() == 0 ) 
				sum.setPermanenceMedian(0);
			else {
				if( permanenceMedianList.size() % 2 == 0 && permanenceMedianList.size() >= 2 ) {
					int med = (int)(permanenceMedianList.size() / 2);
					permanenceInMillis = ((permanenceMedianList.get(med-1) + permanenceMedianList.get(med)) / 2);
				} else
					permanenceInMillis = (permanenceMedianList.get((int)Math.floor(permanenceMedianList.size() / 2)));
				
				sum.setPermanenceMedian(permanenceInMillis / 1000 / 60 );
			}
			
			return sum;
		}
	}

	public class PerformanceSummary {
		private String date;
		private String day;
		private long visits;
		private float conversion;
		private long avgTicket;
		
		public PerformanceSummary(String date, String day, long visits, float conversion, long avgTicket) {
			super();
			this.date = date;
			this.day = day;
			this.visits = visits;
			this.conversion = conversion;
			this.avgTicket = avgTicket;
		}

		/**
		 * @return the date
		 */
		public String getDate() {
			return date;
		}

		/**
		 * @param date the date to set
		 */
		public void setDate(String date) {
			this.date = date;
		}

		/**
		 * @return the day
		 */
		public String getDay() {
			return day;
		}

		/**
		 * @param day the day to set
		 */
		public void setDay(String day) {
			this.day = day;
		}

		/**
		 * @return the visits
		 */
		public long getVisits() {
			return visits;
		}

		/**
		 * @param visits the visits to set
		 */
		public void setVisits(long visits) {
			this.visits = visits;
		}

		/**
		 * @return the conversion
		 */
		public float getConversion() {
			return conversion;
		}

		/**
		 * @param conversion the conversion to set
		 */
		public void setConversion(float conversion) {
			this.conversion = conversion;
		}

		/**
		 * @return the avgTicket
		 */
		public long getAvgTicket() {
			return avgTicket;
		}

		/**
		 * @param avgTicket the avgTicket to set
		 */
		public void setAvgTicket(long avgTicket) {
			this.avgTicket = avgTicket;
		}
		
	}
	
	public class PerformanceEntry {
		private String stringDate;
		private Long visits;
		private Long tickets;
		private Long revenue;
		
		public PerformanceEntry(String stringDate, Long visits, Long tickets, Long revenue) {
			this.stringDate = stringDate;
			this.visits = visits;
			this.tickets = tickets;
			this.revenue = revenue;
		}

		/**
		 * @return the stringDate
		 */
		public String getStringDate() {
			return stringDate;
		}

		/**
		 * @param stringDate the stringDate to set
		 */
		public void setStringDate(String stringDate) {
			this.stringDate = stringDate;
		}

		/**
		 * @return the visits
		 */
		public Long getVisits() {
			return visits;
		}

		/**
		 * @param visits the visits to set
		 */
		public void setVisits(Long visits) {
			this.visits = visits;
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
		 * @return the revenue
		 */
		public Long getRevenue() {
			return revenue;
		}

		/**
		 * @param revenue the revenue to set
		 */
		public void setRevenue(Long revenue) {
			this.revenue = revenue;
		}

		public PerformanceSummary toSummary() {
			
			float conversion = 0F;
			if( tickets != null && tickets > 0 ) {
				conversion = (float)visits / (float)tickets;
			}
			
			long avgTicket = 0;
			if( tickets != null && tickets > 0 && revenue != null && revenue > 0 ) {
				avgTicket = revenue / tickets;
			}
			
			String date = "-";
			try {
				date = sdf2.format(sdf.parse(stringDate));
			} catch( Exception e ) {}
			
			PerformanceSummary sum = new PerformanceSummary(date, getSmallDateName(stringDate), visits, conversion, avgTicket);
			return sum;
		}
	}
}
