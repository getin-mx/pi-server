package mx.getin;

/**
 * Class with common system constants, to reduce memory usage and make manteinance cheaper & easier.
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @since Mark III, decebmer 2917
 * @version 0.5
 */
public final class Constants {

	public static final int FIVE_MINUTES_IN_MILLIS = 5 * 60 * 1000;
	public static final int TEN_MINUTES_IN_MILLIS = 10 * 60 * 1000;
	public static final int THIRTY_MINUTES_IN_MILLIS = 30 * 60 * 1000;
	public static final int DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
	
	public static final byte APDEVICE_REPORT_INTERVAL_MINUTES = 30;
	
	public static final int SLOT_NUMBER_IN_DAY = DAY_IN_MILLIS /1000 /20;
	
	private Constants() {}

}
