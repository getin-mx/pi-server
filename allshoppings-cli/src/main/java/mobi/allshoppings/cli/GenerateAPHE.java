package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.apdevice.APHHelper;
import mobi.allshoppings.dao.APDAssignationDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.APDAssignation;
import mobi.allshoppings.model.APHEntry;
import mobi.allshoppings.tools.CollectionFactory;


public class GenerateAPHE extends AbstractCLI {

	private static final Logger log = Logger.getLogger(GenerateAPHE.class.getName());
	public static final int TWENTY_FOUR_HOURS = 86400000;
	public static final int TWELVE_HOURS = TWENTY_FOUR_HOURS /2;
	private static final String FROM_DATE_PARAM = "fromDate";
	private static final String TO_DATE_PARAM = "toDate";
	private static final String HOSTNAME_PARAM = "hostname";
	private static final String FORCE_DATE_PARAM = "forceDate";
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts(FROM_DATE_PARAM, "Date From" ).withRequiredArg()
				.ofType( String.class );
		parser.accepts(TO_DATE_PARAM, "Date To" ).withRequiredArg()
				.ofType( String.class );
		parser.accepts(HOSTNAME_PARAM, "APHostname").withRequiredArg()
				.ofType( String.class );
		parser.accepts(FORCE_DATE_PARAM, "Set to true to ignore APHotspot date and use "
				+ "the source directories. Default is false");
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			
			APHHelper helper = (APHHelper)getApplicationContext().getBean("aphentry.helper");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
			String hostname = null;
			Boolean forceDate = false;
			
			try {
				if( options.has(FROM_DATE_PARAM)) sFromDate =
						(String)options.valueOf(FROM_DATE_PARAM);
				if( options.has(TO_DATE_PARAM)) sToDate =
						(String)options.valueOf(TO_DATE_PARAM);
				
				fromDate = StringUtils.hasText(sFromDate) ? sdf.parse(sFromDate) :
					sdf.parse(sdf.format(new Date(System.currentTimeMillis()
							-TWENTY_FOUR_HOURS)));
				if(StringUtils.hasText(sToDate)) {
					toDate = sdf.parse(sToDate);// FIXME if legacy date
					TimeZone lTz = TimeZone.getTimeZone("Mexico/General");
					toDate.setTime(toDate.getTime() -lTz.getOffset(toDate.getTime()));
				} else toDate =  new Date(fromDate.getTime() +TWENTY_FOUR_HOURS);
				
				if( options.has(HOSTNAME_PARAM)) {
					hostname = (String)options.valueOf(HOSTNAME_PARAM);
				}
				forceDate = options.has(FORCE_DATE_PARAM) &&
						(Boolean)options.valueOf(FORCE_DATE_PARAM);
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Generating APHEntries");
			List<String> hostnames = CollectionFactory.createList();
			if(StringUtils.hasText(hostname)) {
				String[] hn = hostname.split(",");
				for(String chn : hn){
					hostnames.add(chn);
				}
			} if(hostnames.isEmpty()) {
				APDAssignationDAO apdaDao = (APDAssignationDAO) getApplicationContext()
						.getBean("apdassignation.dao.ref");
				for(APDAssignation apda : apdaDao.getAll())
					hostnames.add(apda.getHostname());
			}
			
			helper.setScanInDevices(false);
			helper.setUseCache(true);

			Date ffromDate = new Date(fromDate.getTime()/* -TWELVE_HOURS*/);
			Date ftoDate = new Date(fromDate.getTime());
			
			Map<String, List<APHEntry>> dayMem = CollectionFactory.createMap();
			
			while( ftoDate.before(toDate)) {

				ffromDate = new Date(ftoDate.getTime());
				ftoDate = new Date(ftoDate.getTime() + TWENTY_FOUR_HOURS);
				
				helper.generateAPHEntriesFromDump(ffromDate, ftoDate, hostnames,
						false, dayMem, !ftoDate.before(toDate), forceDate);

			}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
