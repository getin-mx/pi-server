package mobi.allshoppings.dao;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.OfferType;

public interface OfferTypeDAO extends GenericDAO<OfferType> {

	Key createKey() throws ASException;

}
