package mx.getin;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Class with common system constants, to reduce memory usage and make manteinance cheaper & easier.
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @since Mark III, decebmer 2917
 * @version 0.5
 */
public final class Constants {

	public static final int FIVE_MINUTES_IN_MILLIS = 5 * 60 * 1000;
	public static final int TEN_MINUTES_IN_MILLIS = 2 *FIVE_MINUTES_IN_MILLIS;
	public static final int THIRTY_MINUTES_IN_MILLIS = 3 *TEN_MINUTES_IN_MILLIS;
	public static final int HOUR_IN_MILLIS = 2 *THIRTY_MINUTES_IN_MILLIS;
	public static final int SIX_HOURS_IN_MILLIS = 6 *HOUR_IN_MILLIS;
	public static final int TWELVE_HOURS_IN_MILLIS = 2 *SIX_HOURS_IN_MILLIS;
	public static final int DAY_IN_MILLIS = 2 *TWELVE_HOURS_IN_MILLIS;
	
	public static final byte APDEVICE_REPORT_INTERVAL_MINUTES = 30;
	
	public static final short SLOT_NUMBER_IN_DAY = DAY_IN_MILLIS /1000 /20;
	
	public static final byte MINUTE_TO_TWENTY_SECONDS_SLOT = 3;
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	
	static {
		sdf.setTimeZone(GMT);
	}
	
	private Constants() {}

}
