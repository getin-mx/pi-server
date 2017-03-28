package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import com.google.gson.Gson;
import com.ibm.icu.util.Calendar;
import com.inodes.util.CollectionFactory;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class TestRetailCalendar extends AbstractCLI {

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			
//			APDVisitDAO dao = (APDVisitDAO)getApplicationContext().getBean("apdvisit.dao.ref");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			@SuppressWarnings("unused")
			OptionSet options = parser.parse(args);

			List<RetailCalendarEntry> list = CollectionFactory.createList();
			TestRetailCalendar instance = new TestRetailCalendar();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date initial = sdf.parse("2016-01-01");
			Calendar cal = Calendar.getInstance();
			cal.setTime(initial);
						
			while( cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) 
				cal.add(Calendar.DATE, 1);
			
			int index = 0;
			int key = 0;
			int lastYear = cal.get(Calendar.YEAR);
			Date fromDate;
			Date toDate;

			while( cal.get(Calendar.YEAR) < 2018) {
				if( cal.get(Calendar.YEAR) != lastYear ) {
					index = 0;
					lastYear = cal.get(Calendar.YEAR); 
				}
				
				fromDate = new Date(cal.getTimeInMillis());
				while( cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) 
					cal.add(Calendar.DATE, 1);
				toDate = new Date(cal.getTimeInMillis());

				list.add(instance.new RetailCalendarEntry(key++, ++index, fromDate, toDate));
				cal.add(Calendar.DATE, 1);
			}

			Gson gson = new Gson();
			JSONArray array = new JSONArray();
			for( RetailCalendarEntry entry : list ) {
				JSONObject obj = new JSONObject(gson.toJson(entry));
				array.put(obj);
			}
			
			System.out.println(array);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
	public class RetailCalendarEntry {
	
		int id;
		int index;
		int year;
		String name;
		String fromDate;
		String toDate;
		
		public RetailCalendarEntry(int id, int index, Date fromDate, Date toDate) {
			super();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			this.id = id;
			Calendar cal = Calendar.getInstance();
			this.index = index;
			this.fromDate = sdf.format(fromDate);
			this.toDate = sdf.format(toDate);
			cal.setTime(fromDate);
			this.year = cal.get(Calendar.YEAR);
			this.name = this.year + " Semana " + this.index + ": " + this.fromDate + " - " + this.toDate;
		}
		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}
		/**
		 * @param id the id to set
		 */
		public void setId(int id) {
			this.id = id;
		}
		/**
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}
		/**
		 * @param index the index to set
		 */
		public void setIndex(int index) {
			this.index = index;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the fromDate
		 */
		public String getFromDate() {
			return fromDate;
		}
		/**
		 * @param fromDate the fromDate to set
		 */
		public void setFromDate(String fromDate) {
			this.fromDate = fromDate;
		}
		/**
		 * @return the toDate
		 */
		public String getToDate() {
			return toDate;
		}
		/**
		 * @param toDate the toDate to set
		 */
		public void setToDate(String toDate) {
			this.toDate = toDate;
		}
		/**
		 * @return the year
		 */
		public int getYear() {
			return year;
		}
		/**
		 * @param year the year to set
		 */
		public void setYear(int year) {
			this.year = year;
		}

		public String toString() {
			return getName();
		}
	}
}
