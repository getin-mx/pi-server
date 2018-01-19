package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.Replicable;
import mobi.allshoppings.model.interfaces.StatusAware;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class FloorMap implements ModelKey, Serializable, Identificable, StatusAware, Replicable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String shoppingId;
	private String floor;
	private String imageId;
	private byte status;
	
	private Date creationDateTime;
	private Date lastUpdate;

	private int marginTop;
	private int screenHeight;
	private int screenWidth;
	private int mapHeight;
	private int mapWidth;

	private boolean corrected;
	
	@NotPersistent
	private boolean doIndexNow = true;
	
    public FloorMap() {
		this.creationDateTime = new Date();
		this.marginTop = 0;
		this.screenHeight = 0;
		this.screenWidth = 0;
		this.mapHeight = 0;
		this.mapWidth = 0;
		this.corrected = false;
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
	 * @return the floor
	 */
	public String getFloor() {
		return floor;
	}

	/**
	 * @param floor the floor to set
	 */
	public void setFloor(String floor) {
		this.floor = floor;
	}

	/**
	 * @return the imageId
	 */
	public String getImageId() {
		return imageId;
	}

	/**
	 * @param imageId the imageId to set
	 */
	public void setImageId(String imageId) {
		this.imageId = imageId;
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
	 * @return the marginTop
	 */
	public Integer getMarginTop() {
		return marginTop;
	}

	/**
	 * @param marginTop the marginTop to set
	 */
	public void setMarginTop(Integer marginTop) {
		this.marginTop = marginTop;
	}

	/**
	 * @return the screenHeight
	 */
	public Integer getScreenHeight() {
		return screenHeight;
	}

	/**
	 * @param screenHeight the screenHeight to set
	 */
	public void setScreenHeight(Integer screenHeight) {
		this.screenHeight = screenHeight;
	}

	/**
	 * @return the screenWidth
	 */
	public Integer getScreenWidth() {
		return screenWidth;
	}

	/**
	 * @param screenWidth the screenWidth to set
	 */
	public void setScreenWidth(Integer screenWidth) {
		this.screenWidth = screenWidth;
	}

	/**
	 * @return the mapHeight
	 */
	public Integer getMapHeight() {
		return mapHeight;
	}

	/**
	 * @param mapHeight the mapHeight to set
	 */
	public void setMapHeight(Integer mapHeight) {
		this.mapHeight = mapHeight;
	}

	/**
	 * @return the mapWidth
	 */
	public Integer getMapWidth() {
		return mapWidth;
	}

	/**
	 * @param mapWidth the mapWidth to set
	 */
	public void setMapWidth(Integer mapWidth) {
		this.mapWidth = mapWidth;
	}

	/**
	 * @return the doIndexNow
	 */
	public boolean isDoIndexNow() {
		return doIndexNow;
	}

	/**
	 * @param doIndexNow the doIndexNow to set
	 */
	public void setDoIndexNow(boolean doIndexNow) {
		this.doIndexNow = doIndexNow;
	}

	@Override
	public byte getStatus() {
		return status;
	}

	@Override
	public void setStatus(byte status) {
		this.status = status;
	}

	/**
	 * @return the corrected
	 */
	public Boolean getCorrected() {
		return corrected;
	}

	/**
	 * @param corrected the corrected to set
	 */
	public void setCorrected(Boolean corrected) {
		this.corrected = corrected;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FloorMap [key=" + key + ", shoppingId=" + shoppingId
				+ ", floor=" + floor + ", imageId=" + imageId + ", status="
				+ status + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((floor == null) ? 0 : floor.hashCode());
		result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
		result = prime * result
				+ ((shoppingId == null) ? 0 : shoppingId.hashCode());
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
		FloorMap other = (FloorMap) obj;
		if (floor == null) {
			if (other.floor != null)
				return false;
		} else if (!floor.equals(other.floor))
			return false;
		if (imageId == null) {
			if (other.imageId != null)
				return false;
		} else if (!imageId.equals(other.imageId))
			return false;
		if (shoppingId == null) {
			if (other.shoppingId != null)
				return false;
		} else if (!shoppingId.equals(other.shoppingId))
			return false;
		return true;
	}

}
