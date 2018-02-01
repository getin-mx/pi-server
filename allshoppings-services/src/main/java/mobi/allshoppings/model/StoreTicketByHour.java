package mobi.allshoppings.model;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import com.inodes.datanucleus.model.Key;

import mx.getin.model.interfaces.StoreDataByHourEntity;

/**
 * Models a "Ticket by hour", which is the number of tickets sold within the same local hour for a store.
 * @author Matias Hapanowics
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 1.0, 
 * @since Allshoppings
 */
@PersistenceCapable(detachable="true")
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
public class StoreTicketByHour extends StoreTicket implements StoreDataByHourEntity {

	private static final long serialVersionUID = 1L;
	
	/*@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED) */
	private Key key;

	private String hour;
	
	public StoreTicketByHour() {
		super();
	}
	
	/*
	 * @return this entity key
	 *
	public String getIdentifier() {
		return this.getKey() != null ? this.getKey().getName() : "";
	}

	/**
	 * @return the key
	 *
	public Key getKey() {
		return key;
	}

	/*
	 * @param key the key to set
	 *
	public void setKey(Key key) {
		this.key = key;
	} */

	/**
	 * @return the hour
	 */
	public String getHour() {
		return hour;
	}

	/**
	 * @param hour the hour to set
	 */
	public void setHour(String hour) {
		this.hour = hour;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StoreTicketByHour other = (StoreTicketByHour) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StoreTicketByHour [key=" + key + ", brandId=" + brandId + ", storeId=" + storeId + ", date=" + date
				+ ", hour=" + hour + ", qty=" + qty + ", creationDateTime=" + creationDateTime + ", lastUpdate="
				+ lastUpdate + "]";
	}

}
