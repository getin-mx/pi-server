package mx.getin.dao.spi;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.spi.GenericDAOJDO;
import mobi.allshoppings.exception.ASException;
import mx.getin.dao.APDCalibrationDAO;
import mx.getin.model.APDCalibration;

/**
 * APDCalibration implementation for JDO.
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, december 2017
 * @since Mark III, december 2017
 */
public class APDCalibrationDAOJDOImpl extends GenericDAOJDO<APDCalibration> implements APDCalibrationDAO {

	// constructors
	
	/**
	 * Creates an APDCalibrationDAO for JDO.
	 * @since APDCalibration 1.0, december 2017
	 */
	public APDCalibrationDAOJDOImpl() {
		super(APDCalibration.class);
	}//constructor

	// implementation methods
	
	@Override
	public Key createKey(String identifier) throws ASException {
		return keyHelper.obtainKey(APDCalibration.class, identifier);
	}

}
