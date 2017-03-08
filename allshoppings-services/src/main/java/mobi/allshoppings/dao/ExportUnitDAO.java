package mobi.allshoppings.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.ExportUnit;

public interface ExportUnitDAO extends GenericDAO<ExportUnit> {

	Key createKey() throws ASException;

}
