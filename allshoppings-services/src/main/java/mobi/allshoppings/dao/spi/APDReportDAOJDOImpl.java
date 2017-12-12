package mobi.allshoppings.dao.spi;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.APDReportDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDReport;

/**
 * APDReportDAO implementation for JDO.
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, december 2017
 * @since Mark III, december 2017
 */
public class APDReportDAOJDOImpl extends GenericDAOJDO<APDReport> implements APDReportDAO {

	// constructors
	
	/**
	 * Creates an APDReportDAO for JDO.
	 * @since APDReportDAOJDOImpl 1.0, december 2017
	 */
	public APDReportDAOJDOImpl() {
		super(APDReport.class);
	}//constructor

	// implementation methods
	
	@Override
	public Key createKey(String identifier) throws ASException {
		return keyHelper.obtainKey(APDReport.class, identifier);
	}//createKey

}
