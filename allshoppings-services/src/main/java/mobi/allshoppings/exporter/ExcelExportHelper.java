package mobi.allshoppings.exporter;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.mail.MailHelper;
import mobi.allshoppings.model.User;

public interface ExcelExportHelper {

	/**
	 * Execute all export units in a date range
	 * 
	 * @param fromDate
	 *            Initial date to export
	 * @param toDate
	 *            Final date to export
	 * @param toNotify - The user to mail when the report is
	 * done. If the process was started by console, this
	 * argument can be null.
	 * @throws ASException
	 */
	byte[] export(String storeId, String fromDate, String toDate, int weeks,
			String outDir, User toNotify) throws ASException;
	
	/**
	 * Exports most dashboard indicators to excel.
	 * @param sotresId - The stores to export
	 * @param brandId - A brand to export all of its stores.
	 * @param fromDate - Starting dump date.
	 * @param toDate - Final dump date.
	 * @param outDir - Optional output dir for reports
	 * @param saveTmp - Keep a copy of the result in RAM
	 * (in /tmp dir)
	 * @param toNotify - The user to mail about results.
	 * It can be null if no notification is desired.
	 * @return byte[] - A stream of bytes which may be parsed
	 * into a Microsof Excel &lt;= 2013 report. 
	 * @throws ASException - If something goes wrong.
	 */
	byte[] exportDB(List<String> sotresId, String brandId, String fromDate, String toDate,
			String outDir, boolean saveTmp, User toNotify) throws ASException;
	
}