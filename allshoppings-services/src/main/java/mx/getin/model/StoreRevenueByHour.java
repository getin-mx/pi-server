package mx.getin.model;

import javax.jdo.annotations.PersistenceCapable;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.StoreRevenue;
import mx.getin.model.interfaces.StoreDataByHourEntity;

/**
 * Models a "Revenue by hour", which is total sales within the same local hour for a store.
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, january 2017 
 * @since Mark III
 */
@PersistenceCapable(detachable="true")
public class StoreRevenueByHour extends StoreRevenue implements StoreDataByHourEntity {
	
	private static final long serialVersionUID = 3175715115738373101L;

	/*@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED) */
	private Key key;

	private String hour;

	public StoreRevenueByHour() {
		super();
	}
	
	/*
	 * Gets the Store revenue By Hour ID
	 * @return String - this entity key
	 *
	public String getIdentifier() {
		return this.getKey() != null ? this.getKey().getName() : "";
	}//getIdentifier

	/**
	 * Gets the Store revenue By Hour Key
	 * @return Key - the key of the entity
	 *
	public Key getKey() {
		return key;
	}//getKey

	/**
	 * Sets the Store revenue By Hour BD key
	 * @param key - the key to set
	 *
	public void setKey(Key key) {
		this.key = key;
	}//setKey	*/

	/**
	 * Gets the Store revenue By Hour's hour
	 * @return String - the hour
	 */
	public String getHour() {
		return hour;
	}//getHour

	/**
	 * Sets the Store revenue By Hour's hour
	 * @param hour - the hour to set
	 */
	public void setHour(String hour) {
		this.hour = hour;
	}//setHour

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}//hashCode

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StoreRevenueByHour other = (StoreRevenueByHour) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}//equals

	@Override
	public String toString() {
		return "StoreRevenueByHour [key=" + key + ", brandId=" + brandId + ", storeId=" + storeId + ", date=" + date
				+ ", hour=" + hour + ", qty=" + qty + ", creationDateTime=" + creationDateTime + ", lastUpdate="
				+ lastUpdate + "]";
	}//toString
}
