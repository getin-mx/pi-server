package mobi.allshoppings.dao;


import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.EmployeeLog;

public interface EmployeeLogDAO extends GenericDAO<EmployeeLog> {

	Key createKey(EmployeeLog obj) throws ASException;

}
