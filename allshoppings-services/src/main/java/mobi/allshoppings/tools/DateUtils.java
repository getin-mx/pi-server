package mobi.allshoppings.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

	public static TimeZone GMT = TimeZone.getTimeZone("GMT");
	public static SimpleDateFormat localSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat gmtSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static {
		gmtSDF.setTimeZone(GMT);
	}
	
	/**
	 * Converts any date to GMT
	 * 
	 * @param input
	 *            The input date
	 * @return a GMT date object
	 */
	public static Date toGMT(Date input ) {
		try {
			return gmtSDF.parse(localSDF.format(input));
		} catch( Exception e ) {
			return input;
		}
		
	}
	
}
