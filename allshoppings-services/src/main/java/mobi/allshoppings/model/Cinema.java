package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.embedded.AlternateLocation;
import mobi.allshoppings.model.embedded.FormatPrice;
import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class Cinema implements ModelKey, Serializable, Identificable, StatusAware {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String internalIdentifier;
	private String alternateIdentifier;
	private String name;
	private String customUrl;
	
	private String shoppingId;
	private String brandId;
	private String storeId;

	private Double radius;
	
	private Integer status;
	
	@Persistent(defaultFetchGroup = "true")
	@Embedded
	private Address address;
	
	private Date creationDateTime;
	private Date lastUpdate;

	@Persistent(defaultFetchGroup = "true")
	private List<AlternateLocation> alternateLocations;
	
	@Persistent(defaultFetchGroup = "true")
	private List<FormatPrice> prices;

	public Cinema() {
		super();
		this.address = new Address();
		this.creationDateTime = new Date();
		this.alternateLocations = CollectionFactory.createList();
		this.prices = CollectionFactory.createList();
		this.radius = 200D;
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
	 * Sets last update parameter
	 */
	@Override
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * Obtains last update parameter
	 */
	@Override
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @return the internalIdentifier
	 */
	public String getInternalIdentifier() {
		return internalIdentifier;
	}

	/**
	 * @param internalIdentifier the internalIdentifier to set
	 */
	public void setInternalIdentifier(String internalIdentifier) {
		this.internalIdentifier = internalIdentifier;
	}

	/**
	 * @return the alternateIdentifier
	 */
	public String getAlternateIdentifier() {
		return alternateIdentifier;
	}

	/**
	 * @param alternateIdentifier the alternateIdentifier to set
	 */
	public void setAlternateIdentifier(String alternateIdentifier) {
		this.alternateIdentifier = alternateIdentifier;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the brandId
	 */
	public String getBrandId() {
		return brandId;
	}

	/**
	 * @param brandId the brandId to set
	 */
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	/**
	 * @return the storeId
	 */
	public String getStoreId() {
		return storeId;
	}

	/**
	 * @param storeId the storeId to set
	 */
	public void setStoreId(String storeId) {
		this.storeId = storeId;
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
	 * @return the customUrl
	 */
	public String getCustomUrl() {
		return customUrl;
	}

	/**
	 * @param customUrl the customUrl to set
	 */
	public void setCustomUrl(String customUrl) {
		this.customUrl = customUrl;
	}

	/**
	 * @return the address
	 */
	public Address getAddress() {
		if( address == null ) address = new Address();
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the alternateLocations
	 */
	public List<AlternateLocation> getAlternateLocations() {
		if( alternateLocations == null ) alternateLocations = CollectionFactory.createList();
		return alternateLocations;
	}

	/**
	 * @param alternateLocations the alternateLocations to set
	 */
	public void setAlternateLocations(List<AlternateLocation> alternateLocations) {
		this.alternateLocations = alternateLocations;
	}

	/**
	 * @return the prices
	 */
	public List<FormatPrice> getPrices() {
		return prices;
	}

	/**
	 * @param prices the prices to set
	 */
	public void setPrices(List<FormatPrice> prices) {
		this.prices = prices;
	}

	public Double getPriceForFormat(String format) {
		if( this.prices == null ) return null;
		for( FormatPrice fp : prices ) {
			if(fp.getFormat().equalsIgnoreCase(format)) 
				return fp.getPrice();
		}
		return null;
	}
	
	/**
	 * @return the radius
	 */
	public Double getRadius() {
		return radius;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(Double radius) {
		this.radius = radius;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		Cinema other = (Cinema) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Cinema [key=" + key + ", internalIdentifier="
				+ internalIdentifier + ", alternateIdentifier="
				+ alternateIdentifier + ", name=" + name + ", customUrl="
				+ customUrl + ", shoppingId=" + shoppingId + ", brandId="
				+ brandId + ", storeId=" + storeId + ", radius=" + radius
				+ ", status=" + status + ", address=" + address
				+ ", creationDateTime=" + creationDateTime + ", lastUpdate="
				+ lastUpdate + ", alternateLocations=" + alternateLocations
				+ ", prices=" + prices + "]";
	}

}
