package mobi.allshoppings.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.Image;

public interface ImageDAO extends GenericDAO<Image> {
	
	Key createKey(String fileName) throws ASException;
	Key forceKey(String fileName) throws ASException;
	Image associateByOriginalNameAndSession(String originalName, String session, Key owner, String deleteId) throws ASException;
	Image associateByIdentifier(String identifier, Key owner, String deleteId) throws ASException;
	Image getByOriginalName(String originalName, Boolean detached) throws ASException;
	Image getBySource(String source) throws ASException;
}
