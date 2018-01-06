package mx.getin.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;

/**
 * Models a "Item by hour", which is the number of items sold within the same local hour for a store.
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, january 2017 
 * @since Mark III
 */
@PersistenceCapable(detachable="true")
public class StoreItemByHour implements ModelKey, Serializable, Identificable {

	private static final long serialVersionUID = -5883423685204800884L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
	private Key key;

	private String brandId;
	private String storeId;
	private String date;
	private String hour;
	private int qty;
	private Date creationDateTime;
	private Date lastUpdate;

	public StoreItemByHour() {
		this.creationDateTime = new Date();
		this.qty = 0;
	}
	
	/**
	 * Gets the Store Item By Hour ID
	 * @return String - this entity key
	 */
	public String getIdentifier() {
		return this.getKey() != null ? this.getKey().getName() : "";
	}//getIdentifier

	/**
	 * Gets the Store Item By Hour Key
	 * @return Key - the key of the entity
	 */
	public Key getKey() {
		return key;
	}//getKey

	/**
	 * Sets the Store Item By Hour BD key
	 * @param key - the key to set
	 */
	public void setKey(Key key) {
		this.key = key;
	}//setKey

	@Override
	public void preStore() {
		this.lastUpdate = new Date();
	}//preStore

	/**
	 * Gets the Store Item By Hour's brand.
	 * @return String - the brandId
	 */
	public String getBrandId() {
		return brandId;
	}//getBrandId

	/**
	 * Sets the Store Item By Hour brand
	 * @param brandId - the brandId to set
	 */
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}//setBrandId

	/**
	 * Gets the Store ITem By Hour's hour
	 * @return String - the hour
	 */
	public String getHour() {
		return hour;
	}//getHour

	/**
	 * Sets the Store Item By Hour's hour
	 * @param hour - the hour to set
	 */
	public void setHour(String hour) {
		this.hour = hour;
	}//setHour

	/**
	 * Gets the Store Item By Hour's Store ID
	 * @return String - the storeId
	 */
	public String getStoreId() {
		return storeId;
	}//getStoreId

	/**
	 * Sets the Store Item By Hour's Store ID.
	 * @param storeId - the storeId to set
	 */
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}//setStoreId

	/**
	 * Gets the Store Item By Hour's value; the number of tickets sold in that hour.
	 * @return double - the tickets by hour quantity.
	 */
	public int getQty() {
		return qty;
	}//getQty

	/**
	 * Sets the Store Item By Hour's value.
	 * @param qty - the number of tickets sold in the represented hour to set
	 */
	public void setQty(int qty) {
		this.qty = qty;
	}//setQty

	/**
	 * Gets the time when the Store Item By Hour was created for the system. This does not necessarily matches
	 * the actual time of any sale in this items.
	 * @return Date - the creationDateTime of this entity for the system.
	 */
	public Date getCreationDateTime() {
		return creationDateTime;
	}//get CreationDateTime 

	/**
	 * Sets the creation date time for this entity. The creation date time should only be setted when reading
	 * data from the DB.
	 * @param creationDateTime - the creationDateTime to set
	 */
	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}//setCreationDateTime

	/**
	 * Gets the last date when this data was modified.
	 * @return Date - the lastUpdate time.
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}//getLastUpdate

	/**
	 * Sets the last modification time for this data.
	 * @param lastUpdate - the lastUpdate date to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}//setLastUpdate

	/**
	 * Gets the date that corresponds to the items sale represented by this model.
	 * @return String - the date of the sales
	 */
	public String getDate() {
		return date;
	}//getDate

	/**
	 * Sets the date when the items were sold. 
	 * @param date - the date to set.
	 */
	public void setDate(String date) {
		this.date = date;
	}//setDate

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
		StoreItemByHour other = (StoreItemByHour) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}//equals

	@Override
	public String toString() {
		return "StoreItemByHour [key=" + key + ", brandId=" + brandId + ", storeId=" + storeId + ", date=" + date
				+ ", hour=" + hour + ", qty=" + qty + ", creationDateTime=" + creationDateTime + ", lastUpdate="
				+ lastUpdate + "]";
	}//toString
	
}//Store Item by Hour model
