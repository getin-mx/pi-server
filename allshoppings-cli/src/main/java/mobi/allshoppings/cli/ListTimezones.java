package mobi.allshoppings.cli;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;


public class ListTimezones extends AbstractCLI {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ListTimezones.class.getName());

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		return parser;
	}

	public static void main(String args[]) throws ASException {
		try {
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			@SuppressWarnings("unused")
			OptionSet options = parser.parse(args);

			String[] ids = TimeZone.getAvailableIDs();
			for (String id : ids) {
				System.out.println(displayTimeZone(TimeZone.getTimeZone(id)));
			}

			System.out.println("\nTotal TimeZone ID " + ids.length);

		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}

	private static String displayTimeZone(TimeZone tz) {

		long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
		long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
				- TimeUnit.HOURS.toMinutes(hours);
		// avoid -4:-30 issue
		minutes = Math.abs(minutes);

		String result = "";
		if (hours > 0) {
			result = String.format("(GMT+%d:%02d) Daylight:%b - %s - %s", hours, minutes, tz.useDaylightTime(), tz.getID(), tz.getDisplayName());
		} else {
			result = String.format("(GMT%d:%02d) Daylight:%b - %s - %s", hours, minutes, tz.useDaylightTime(), tz.getID(), tz.getDisplayName());
		}

		return result;

	}
}
