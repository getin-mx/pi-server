package mobi.allshoppings.cli;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.APDeviceDAO;
import mobi.allshoppings.dao.APHEntryDAO;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDevice;
import mobi.allshoppings.model.APHEntry;

public class MigrateAPHEntry extends AbstractCLI {

	private static final String FROM_DATE_PARAM = "fromDate";
	private static final String TO_DATE_PARAM = "toDate";
	
	private static final Logger LOG = Logger.getLogger(MigrateAPHEntry.class.getName());
	
	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts(FROM_DATE_PARAM, "Export from date "
				+ "(yyyy-MM-dd)").withRequiredArg().ofType(
						String.class );
		parser.accepts(TO_DATE_PARAM, "Export to date "
				+ "(yyyy-MM-dd)").withRequiredArg().ofType(
						String.class );
		return parser;
	}
	
	public static void main(String[] args) throws ASException {
		OptionSet options = parser.parse(args);
		Date fromDate, toDate;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		TimeZone tz = TimeZone.getTimeZone("GMT");
		sdf.setTimeZone(tz);
		try {
			fromDate = options.has(FROM_DATE_PARAM) ?
					sdf.parse(options.valueOf(FROM_DATE_PARAM)
							.toString()) : new Date(1420070400000l);
			toDate = options.has(TO_DATE_PARAM) ?
					sdf.parse(options.valueOf(TO_DATE_PARAM)
							.toString()) : fromDate;
		} catch(ParseException e) {
			throw ASExceptionHelper.defaultException(
					e.getMessage(), e);
		}
		DumperHelper<APHEntry> dumper = new DumpFactory<APHEntry>()
				.build(null, APHEntry.class);
		APHEntryDAO aphDao = (APHEntryDAO)getApplicationContext()
				.getBean("aphentry.dao.ref");
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(tz);
		Date curDate;
		long total = 0;
		APDeviceDAO apdevDao = (APDeviceDAO)getApplicationContext()
				.getBean("apdevice.dao.ref");
		List<APDevice> apdevs = apdevDao.getAll();
		for(cal.setTime(fromDate);
				(curDate = cal.getTime()).compareTo(toDate) < 0;) {
			cal.add(Calendar.MONTH, 1);
			if(cal.getTime().after(toDate)) cal.setTime(toDate);
			LOG.log(Level.INFO, "Processing dates: " +sdf.format(curDate) +" to date "
					+sdf.format(cal.getTime()));
			for(APDevice dev : apdevs) {
				List<APHEntry> entries = aphDao.getUsingHostnameAndDates(
						Arrays.asList(dev.getHostname()), curDate, cal.getTime(),
						null, true);
				// Counts the total records to export
				total += entries.size();
				curDate = cal.getTime();
				if(entries.size() == 0) continue;
				for(APHEntry aphEntry : entries) dumper.dump(aphEntry);
				dumper.flush();
				dumper.dispose();
				LOG.log(Level.INFO, "Saved " +entries.size() +" APHEntries for "
						+"hostname " +dev.getHostname() +" and date "
						+sdf.format(curDate));
				if(total %10000 == 0) LOG.log(Level.INFO,
						"In total, saved " +total +" APHEntries so far");
			}
		}
		LOG.log(Level.INFO, "Finish dumping APHEntries");
	}
	
}
