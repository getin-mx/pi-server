package mobi.allshoppings.exporter;

import java.util.Date;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.ExportUnit;

public interface ExportHelper {

	/**
	 * Execute all export units in a date range
	 * 
	 * @param fromDate
	 *            Initial date to export
	 * @param toDate
	 *            Final date to export
	 * @throws ASException
	 */
	void export(Date fromDate, Date toDate) throws ASException;

	/**
	 * Exports an ExportUnit
	 * 
	 * @param unit
	 *            The ExportUnit to use
	 * @throws ASException
	 */
	void export(ExportUnit unit, Date fromDate, Date toDate) throws ASException;

}