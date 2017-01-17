package mobi.allshoppings.cli;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.dao.APDVisitDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class TestRepetitions extends AbstractCLI {

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
			
			APDVisitDAO dao = (APDVisitDAO)getApplicationContext().getBean("apdvisit.dao.ref");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			@SuppressWarnings("unused")
			OptionSet options = parser.parse(args);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			long start = System.currentTimeMillis();
			System.out.println(dao.getRepetitions(Arrays.asList(new String[] {"dcddac8b-fea4-4cfe-af7d-0aced11d0900"}), 3, 2, sdf.parse("2016-12-01"), sdf.parse("2016-12-16")));
			long end = System.currentTimeMillis();
			System.out.println("Process ended in " + (end - start) + "ms");
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
