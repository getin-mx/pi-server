package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.Replicable;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class WifiSpot implements ModelKey, Serializable, Identificable, Replicable {

	public static final int AVERAGE = 0;
	public static final int BEST_VALUE = 1;
	public static final int LAST_VALUE = 2;
	
	public static final int REST_TOP = 0;
	public static final int TRIM_TOP = 1;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String shoppingId;
	private String floorMapId;
	private String zoneName;
	private Text data;
	private Integer recordStrategy;
	private Integer calculusStrategy;
	private Integer measures;
	private Integer x;
	private Integer y;
	private String apDevice;
	private String wordAlias;
	
	private Date creationDateTime;
	private Date lastUpdate;

	@NotPersistent
	private HashMap<String, Integer> signals;
	
    public WifiSpot() {
		this.creationDateTime = new Date();
		this.recordStrategy = 0;
		this.calculusStrategy = 0;
		this.measures = 0;
    }

	/**
	 * @return the key
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * @return this entity key
	 */
	public String getIdentifier() {
		return this.getKey() != null ? this.getKey().getName() : "";
	}

	/**
	 * @return this entity key
	 */
	public String getUid() {
		return this.getIdentifier();
	}
	
	/**
	 * Pre store information to assign index values
	 */
	@Override
	public void preStore() {
		this.lastUpdate = new Date();
	}

	/**
	 * @return the shoppingId
	 */
	public String getShoppingId() {
		return shoppingId;
	}

	/**
	 * @param shoppingId the shoppingId to set
	 */
	public void setShoppingId(String shoppingId) {
		this.shoppingId = shoppingId;
	}

	/**
	 * @return the mapFloorId
	 */
	public String getFloorMapId() {
		return floorMapId;
	}

	/**
	 * @param mapFloorId the mapFloorId to set
	 */
	public void setFloorMapId(String floorMapId) {
		this.floorMapId = floorMapId;
	}

	/**
	 * @return the data
	 */
	public Text getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Text data) {
		this.data = data;
	}

	public void setData(String data) {
		if( data == null ) this.data = null;
		else this.data = new Text(data);
	}
	
	/**
	 * @return the recordStrategy
	 */
	public Integer getRecordStrategy() {
		return recordStrategy;
	}

	/**
	 * @param recordStrategy the recordStrategy to set
	 */
	public void setRecordStrategy(Integer recordStrategy) {
		this.recordStrategy = recordStrategy;
	}

	/**
	 * @return the calculusStrategy
	 */
	public Integer getCalculusStrategy() {
		return calculusStrategy;
	}

	/**
	 * @param calculusStrategy the calculusStrategy to set
	 */
	public void setCalculusStrategy(Integer calculusStrategy) {
		this.calculusStrategy = calculusStrategy;
	}

	/**
	 * @return the measures
	 */
	public Integer getMeasures() {
		return measures;
	}

	/**
	 * @param measures the measures to set
	 */
	public void setMeasures(Integer measures) {
		this.measures = measures;
	}

	/**
	 * @return the x
	 */
	public Integer getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(Integer x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public Integer getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(Integer y) {
		this.y = y;
	}

	/**
	 * @return the creationDateTime
	 */
	public Date getCreationDateTime() {
		return creationDateTime;
	}

	/**
	 * @param creationDateTime the creationDateTime to set
	 */
	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	
	/**
	 * @return the apDevice
	 */
	public String getApDevice() {
		return apDevice;
	}

	/**
	 * @param apDevice the apDevice to set
	 */
	public void setApDevice(String apDevice) {
		this.apDevice = apDevice;
	}

	/**
	 * @return the wordAlias
	 */
	public String getWordAlias() {
		return wordAlias;
	}

	/**
	 * @param wordAlias the wordAlias to set
	 */
	public void setWordAlias(String wordAlias) {
		this.wordAlias = wordAlias;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((calculusStrategy == null) ? 0 : calculusStrategy.hashCode());
		result = prime
				* result
				+ ((creationDateTime == null) ? 0 : creationDateTime.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result
				+ ((floorMapId == null) ? 0 : floorMapId.hashCode());
		result = prime * result
				+ ((measures == null) ? 0 : measures.hashCode());
		result = prime * result
				+ ((recordStrategy == null) ? 0 : recordStrategy.hashCode());
		result = prime * result
				+ ((shoppingId == null) ? 0 : shoppingId.hashCode());
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WifiSpot other = (WifiSpot) obj;
		if (calculusStrategy == null) {
			if (other.calculusStrategy != null)
				return false;
		} else if (!calculusStrategy.equals(other.calculusStrategy))
			return false;
		if (creationDateTime == null) {
			if (other.creationDateTime != null)
				return false;
		} else if (!creationDateTime.equals(other.creationDateTime))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (floorMapId == null) {
			if (other.floorMapId != null)
				return false;
		} else if (!floorMapId.equals(other.floorMapId))
			return false;
		if (measures == null) {
			if (other.measures != null)
				return false;
		} else if (!measures.equals(other.measures))
			return false;
		if (recordStrategy == null) {
			if (other.recordStrategy != null)
				return false;
		} else if (!recordStrategy.equals(other.recordStrategy))
			return false;
		if (shoppingId == null) {
			if (other.shoppingId != null)
				return false;
		} else if (!shoppingId.equals(other.shoppingId))
			return false;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}

	/**
	 * @return the signals
	 */
	public HashMap<String, Integer> getSignals() {
		if( signals == null ) {
			signals = new HashMap<String, Integer>();
			if( this.data != null ) {
				String[] parts = this.data.getValue().split(";");
				for(String part : parts) {
					String[] components = part.split(":");
					if( components.length > 1 ) {
						signals.put(components[0], Integer.parseInt(components[1]));
					}
				}
			}
		}
		return signals;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WifiSpot [key=" + key + ", shoppingId=" + shoppingId + ", floorMapId=" + floorMapId + ", zoneName="
				+ zoneName + ", data=" + data + ", recordStrategy=" + recordStrategy + ", calculusStrategy="
				+ calculusStrategy + ", measures=" + measures + ", x=" + x + ", y=" + y + ", apDevice=" + apDevice
				+ ", wordAlias=" + wordAlias + ", creationDateTime=" + creationDateTime + ", lastUpdate=" + lastUpdate
				+ ", signals=" + signals + "]";
	}

}
