package mobi.allshoppings.exporter;

import java.util.List;

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
	
	byte[] exportDB(List<String> sotresId, String brandId, String fromDate, String toDate,
			String countryISO, String languageISO, String outDir, boolean saveTmp) throws ASException;

}