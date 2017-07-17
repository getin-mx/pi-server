package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dump.DumperHelper;
import mobi.allshoppings.dump.impl.DumpFactory;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.interfaces.ModelKey;


public class ReadHistory extends AbstractCLI {

	private static final Logger log = Logger.getLogger(ReadHistory.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "fromDate", "Date From" ).withRequiredArg().ofType( String.class );
		parser.accepts( "toDate", "Date To" ).withRequiredArg().ofType( String.class );
		parser.accepts( "entity", "Entity to dump (for example, DeviceLocationHistory)").withRequiredArg().ofType( String.class );
		parser.accepts( "outDir", "Output Directory (for example, /tmp/dump)").withRequiredArg().ofType( String.class );
		parser.accepts( "filter", "A file name filter").withRequiredArg().ofType( String.class );
		return parser;
	}

	@SuppressWarnings("unchecked")
	public static void main(String args[]) throws ASException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sFromDate = null;
			String sToDate = null;
			String filter = null;
			Date fromDate = null;
			Date toDate = null;

			String sEntity = null;
			String sOutDir = null;
			
			Class<ModelKey> entity = null;
			
			try {
				if( options.has("help")) usage(parser);
				if( options.has("fromDate")) sFromDate = (String)options.valueOf("fromDate");
				if( options.has("toDate")) sToDate = (String)options.valueOf("toDate");
				
				if( options.has("entity")) sEntity = (String)options.valueOf("entity");
				else usage(parser);
				entity = (Class<ModelKey>)Class.forName("mobi.allshoppings.model." + sEntity);
				
				if( options.has("outDir")) sOutDir = (String)options.valueOf("outDir");
				else usage(parser);

				if( options.has("filter")) filter = (String)options.valueOf("filter");

				if( StringUtils.hasText(sFromDate)) {
					fromDate = sdf.parse(sFromDate);
				} else {
					fromDate = new Date(0L);
				}
				
				if( StringUtils.hasText(sToDate)) {
					toDate = sdf.parse(sToDate);
				} else {
					toDate = new Date();
				}
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Starting read for entity " + entity.getName() + " from " + fromDate + " to " + toDate);
			DumperHelper<ModelKey> dumper = new DumpFactory<ModelKey>().build(sOutDir, entity);
			if( StringUtils.hasText(filter)) dumper.setFilter(filter);
			
			Iterator<ModelKey> it = dumper.iterator(fromDate, toDate);
			while(it.hasNext()) {
				ModelKey obj = it.next();
				System.out.println(obj);
			}
			
			dumper.dispose();
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
