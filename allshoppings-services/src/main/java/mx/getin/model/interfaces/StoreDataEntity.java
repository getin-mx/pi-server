package mx.getin.model.interfaces;

import java.io.Serializable;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;

public interface StoreDataEntity extends ModelKey, Serializable, Identificable {

	String getDate();
	double getQty();
	void setQty(double qty);
}
