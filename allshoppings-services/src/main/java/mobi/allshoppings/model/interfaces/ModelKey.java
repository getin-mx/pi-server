package mobi.allshoppings.model.interfaces;

import java.util.Date;

import com.inodes.datanucleus.model.Key;

public interface ModelKey {
	Key getKey();
	void setKey(Key key);
	void setLastUpdate(Date lastUpdate);
	Date getLastUpdate();
	Date getCreationDateTime();
	void preStore();
	String getIdentifier();
}
