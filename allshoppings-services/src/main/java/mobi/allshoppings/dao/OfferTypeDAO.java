package mobi.allshoppings.dao;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.OfferType;

import com.inodes.datanucleus.model.Key;

public interface OfferTypeDAO extends GenericDAO<OfferType> {

	Key createKey() throws ASException;

}
