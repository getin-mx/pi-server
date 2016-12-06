package mobi.allshoppings.model;

import java.io.Serializable;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * This class represents contact info for an entity
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
@EmbeddedOnly
public final class StructureInfo implements Serializable {

    @Persistent
    /**
     * Shopping Shop count
     */
    private Integer storeCount;

    @Persistent
    /**
     * Shopping Floor count
     */
    private Integer floorCount;
    
    @Persistent
    /**
     * Shopping Brands count;
     */
    private Integer brandCount;
    
    /**
     * Default constructor
     */
    public StructureInfo() {
    	super();
    }

	/**
	 * @return the storeCount
	 */
	public Integer getStoreCount() {
		return storeCount;
	}

	/**
	 * @param storeCount the storeCount to set
	 */
	public void setStoreCount(Integer storeCount) {
		this.storeCount = storeCount;
	}

	/**
	 * @return the floorCount
	 */
	public Integer getFloorCount() {
		return floorCount;
	}

	/**
	 * @param floorCount the floorCount to set
	 */
	public void setFloorCount(Integer floorCount) {
		this.floorCount = floorCount;
	}

	/**
	 * @return the brandCount
	 */
	public Integer getBrandCount() {
		return brandCount;
	}

	/**
	 * @param brandCount the brandCount to set
	 */
	public void setBrandCount(Integer brandCount) {
		this.brandCount = brandCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((brandCount == null) ? 0 : brandCount.hashCode());
		result = prime * result
				+ ((floorCount == null) ? 0 : floorCount.hashCode());
		result = prime * result
				+ ((storeCount == null) ? 0 : storeCount.hashCode());
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
		StructureInfo other = (StructureInfo) obj;
		if (brandCount == null) {
			if (other.brandCount != null)
				return false;
		} else if (!brandCount.equals(other.brandCount))
			return false;
		if (floorCount == null) {
			if (other.floorCount != null)
				return false;
		} else if (!floorCount.equals(other.floorCount))
			return false;
		if (storeCount == null) {
			if (other.storeCount != null)
				return false;
		} else if (!storeCount.equals(other.storeCount))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StructureInfo [storeCount=" + storeCount + ", floorCount="
				+ floorCount + ", brandCount=" + brandCount + "]";
	}

	
}
