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
	void export(String storeId, String fromDate, String toDate, String outDir) throws ASException;

}