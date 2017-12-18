package mobi.allshoppings.dao;


import java.util.Date;
import java.util.List;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.APDAssignation;

public interface APDAssignationDAO extends GenericDAO<APDAssignation> {

	Key createKey(APDAssignation obj) throws ASException;
	List<APDAssignation> getUsingEntityIdAndEntityKind(String entityId, byte entityKind) throws ASException;
	List<APDAssignation> getUsingEntityIdAndEntityKindAndDate(String entityId, byte entityKind, Date date) throws ASException;
	List<APDAssignation> getUsingHostnameAndDate(String hostname, Date date) throws ASException;
	APDAssignation getOneUsingHostnameAndDate(String hostname, Date date) throws ASException;
	List<String> getEntityIds(byte entityKind, Date forDate) throws ASException;
	
}
