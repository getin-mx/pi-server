package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class CopyFakeAPHotspots extends AbstractCLI {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CopyFakeAPHotspots.class.getName());
	public static final long TWENTY_FOUR_HOURS = 86400000;
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Date From (format yyyy-MM-dd-HH)" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date To (format yyyy-MM-dd-HH)" ).withRequiredArg().ofType( String.class );
		parser.accepts( "offset", "Offset of time added to the Fake APHotspots").withRequiredArg().ofType( Long.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	@SuppressWarnings("unused")
	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sFromDate = null;
			String sToDate = null;
			Date fromDate = null;
			Date toDate = null;
			Long offset = 0L;
			
			try {
				if( options.has("fromDate")) sFromDate = (String)options.valueOf("fromDate");
				if( options.has("toDate")) sToDate = (String)options.valueOf("toDate");
				
				if( StringUtils.hasText(sFromDate)) {
					fromDate = sdf.parse(sFromDate);
				} else {
					fromDate = sdf.parse(sdf.format(new Date(new Date().getTime() - 86400000 /* 24 hours */)));
				}
				
				if( StringUtils.hasText(sToDate)) {
					toDate = sdf.parse(sToDate);
				} else {
					toDate = new Date(fromDate.getTime() + 86400000 /* 24 hours */);
				}

				if( options.has("offset")) offset = (Long)options.valueOf("offset");

			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
