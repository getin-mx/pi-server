package mx.getin.model.interfaces;

import java.io.Serializable;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;

public interface StoreDataEntity extends ModelKey, Serializable, Identificable {

	String getDate();
	double getQty();
	void setQty(double qty);
	void setStoreId(String storeId);
	void setBrandId(String brandId);
	void setDate(String date);
	void setKey(Key key);
	
}
