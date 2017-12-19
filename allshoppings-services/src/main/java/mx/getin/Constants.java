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
	public static final int TWELVE_HOURS_IN_MILLIS = 12 *60 *60 *1000;
	public static final int DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
	
	public static final byte APDEVICE_REPORT_INTERVAL_MINUTES = 30;
	
	public static final String FROM_DATE_PARAM = "fromDate";
	public static final String TO_DATE_PARAM = "toDate";
	public static final String OUT_DIR_PARAM = "outDir";
	
	//be aware: orthography is important!
	public static final String DELETE_PREVIOUS_RECORDS_PARAM = "deletePreviousRecords";
	
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	
	public static final String GMT_TIMEZONE_ID = "GMT";
	
	private Constants() {}

}
