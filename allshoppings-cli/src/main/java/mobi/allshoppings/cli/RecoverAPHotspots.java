package mobi.allshoppings.cli;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.ibm.icu.util.Calendar;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.APHotspotDAO;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APHotspot;
import mobi.allshoppings.tools.CollectionFactory;


public class RecoverAPHotspots extends AbstractCLI {

	private static  final Calendar fromCal = Calendar.getInstance();
	private static final Calendar toCal = Calendar.getInstance();
	
	private static final Logger log = Logger.getLogger(RecoverAPHotspots.class.getName());
	public static final int HOUR_IN_MILLIS = 1000 *60 *60;
	private static final String FROM_DATE_PARAM = "fromTime";
	private static final String COPY_FROM_PARAM = "copyFromTime";
	private static final String COPY_TO_PARAM = "copyToTime";
	private static final String HOSTNAME_PARAM = "hostname";
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts(FROM_DATE_PARAM, "Frst Time to copy data" ).withRequiredArg()
				.ofType( String.class );
		parser.accepts(COPY_TO_PARAM, "Copy data from this date" ).withRequiredArg()
				.ofType( String.class );
		parser.accepts(COPY_FROM_PARAM, "Copy data to this date").withRequiredArg()
				.ofType(String.class);
		parser.accepts(HOSTNAME_PARAM, "APHostname").withRequiredArg()
				.ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:hh");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			Date fromDate = null;
			Date copyFromD = null;
			Date copyToD = null;
			List<String> hostnames = CollectionFactory.createList();
			
			try {
				String aux = null;
				String hostname = null;
				if( options.has(FROM_DATE_PARAM)) aux = (String)options.valueOf(FROM_DATE_PARAM);
				fromDate = sdf.parse(aux);
				
				if(options.has(COPY_FROM_PARAM)) aux = options.valueOf(COPY_FROM_PARAM).toString();
				copyFromD = sdf.parse(aux);
				
				if( options.has(COPY_TO_PARAM)) aux = (String)options.valueOf(COPY_TO_PARAM);
				copyToD = sdf.parse(aux);
				
				if( options.has(HOSTNAME_PARAM)) {
					hostname = (String)options.valueOf(HOSTNAME_PARAM);
				}
				if(StringUtils.hasText(hostname)) {
					String[] hn = hostname.split(",");
					for(String chn : hn){
						hostnames.add(chn);
					}
				}
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Recovering APHotspots");
			log.log(Level.INFO, "This process PID is: " +ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
			
			Date copyCurDate = new Date(copyFromD.getTime());
			Date curDate = new Date(fromDate.getTime());
			
			APHotspotDAO dao = (APHotspotDAO)getApplicationContext().getBean("aphotspot.dao.ref");
			
			
			// TODO this needs some improvements, like and accumulator dump
			while( copyCurDate.before(copyToD)) {
				DumperHelper<APHotspot> dumpHelper;
				List<String> hosts;
				if( hostnames == null || hostnames.size() == 0 ) {
					dumpHelper = new DumpFactory<APHotspot>().build(null, APHotspot.class, false);
					hosts = dumpHelper.getMultipleNameOptions(copyFromD);
				} else if( hostnames.size() == 1 ) {
					hosts = CollectionFactory.createList();
					hosts.add(hostnames.get(0));
				} else {
					hosts = CollectionFactory.createList();
					hosts.addAll(hostnames);
				}
				for(String host : hosts) {
					log.log(Level.INFO, "Processing " + host + " for date " + copyFromD + " into "
							+curDate);
					dumpHelper = new DumpFactory<APHotspot>().build(null, APHotspot.class, false);
					dumpHelper.setFilter(host);
					Date xdate = new Date(copyCurDate.getTime() +HOUR_IN_MILLIS -1);
					Iterator<APHotspot> i = dumpHelper.iterator(copyCurDate, xdate, false);
					while(i.hasNext()) {
						APHotspot copy = i.next();
						APHotspot _new = new APHotspot();
						_new.setCount(copy.getCount());
						_new.setHostname(copy.getHostname());
						_new.setKey(dao.createKey());
						_new.setMac(copy.getMac());
						_new.setSignalDB(copy.getSignalDB());
						_new.setLastSeen(transformDate(copy.getLastSeen(), curDate));
						_new.setFirstSeen(transformDate(copy.getFirstSeen(), curDate));
						_new.setCreationDateTime(transformDate(copy.getCreationDateTime(), curDate));
						dumpHelper.dump(_new);
					}
					dumpHelper.flush();
					dumpHelper.dispose();
				}
				copyCurDate.setTime(copyCurDate.getTime() + HOUR_IN_MILLIS);
				curDate.setTime(curDate.getTime() +HOUR_IN_MILLIS);
			}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
	private static Date transformDate(Date original, Date curDate) {
		if(original == null) return null;
		fromCal.clear();
		fromCal.setTime(original);
		toCal.clear();
		toCal.setTime(curDate);
		toCal.set(Calendar.MINUTE, fromCal.get(Calendar.MINUTE));
		toCal.set(Calendar.SECOND, fromCal.get(Calendar.SECOND));
		toCal.set(Calendar.MILLISECOND, fromCal.get(Calendar.MILLISECOND));
		return toCal.getTime();
	}
	
}
