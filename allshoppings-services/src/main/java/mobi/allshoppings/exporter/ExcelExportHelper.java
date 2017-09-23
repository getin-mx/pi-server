package mobi.allshoppings.exporter;

import mobi.allshoppings.exception.ASException;

public interface ExcelExportHelper {

	/**
	 * Execute all export units in a date range
	 * 
	 * @param fromDate
	 *            Initial date to export
	 * @param toDate
	 *            Final date to export
	 * @throws ASException
	 */
	byte[] export(String storeId, String fromDate, String toDate, int weeks, String outDir) throws ASException;
	
	byte[] exportDB(String[] sotresId, String fromDate, String toDate, String countryISO, String languageISO, String outDir) throws ASException;

}