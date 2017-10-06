package mobi.allshoppings.cli;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.inodes.util.CollectionFactory;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExternalGeoImporter;


public class GenerateExternalGeo extends AbstractCLI {

	private static final Logger log = Logger.getLogger(GenerateExternalGeo.class.getName());
	private static final String ENTITY_IDS_PARAM = "entityIds";
	private static final String ENTITY_KIND_PARAM = "entityKind";
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts(ENTITY_IDS_PARAM, "Comma separated list of Entity Id" ).withRequiredArg().ofType( String.class );
		parser.accepts(ENTITY_KIND_PARAM, "Entity Kind" ).withRequiredArg().ofType( Integer.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			ExternalGeoImporter importer = (ExternalGeoImporter)getApplicationContext().getBean("external.geo.importer");

			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String sEntityIds = null;
			Integer entityKind = null;
			List<String> entityIds = CollectionFactory.createList();
			
			try {
				if( options.has("help")) usage(parser);
				if(!options.has(ENTITY_IDS_PARAM))
					throw ASExceptionHelper.defaultException("No data source given: Entity IDs "
							+ " are required (at least one)", null);

				sEntityIds = (String)options.valueOf("entityIds");
				if(sEntityIds != null) {
					String parts[] = sEntityIds.split(",");
					for(int i = 0; i < parts.length; i++) {
						entityIds.add(parts[i].trim());
					}
				}
				entityKind = (Integer)options.valueOf("entityKind");
				
			} catch( Exception e ) {
				usage(parser);
				System.exit(-1);
			}
			
			log.log(Level.INFO, "Updating External Geo with GPS References...");
			importer.importFromGpsRecords(entityIds, entityKind);
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
