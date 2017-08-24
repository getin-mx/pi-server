package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.adapter.IAdaptable;
import mobi.allshoppings.model.interfaces.ACLAware;
import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.Indexable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.Replicable;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.model.interfaces.ViewLocationAware;
import mobi.allshoppings.model.tools.ACL;
import mobi.allshoppings.model.tools.ViewLocation;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class Store implements ModelKey, Serializable, IAdaptable, Identificable, Indexable, ViewLocationAware, ACLAware, StatusAware, Replicable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String shoppingId;
	private String shoppingName;

	private String brandId;
	private String brandName;
	
	private String storeNumber;
	private String floorNumber;
	
	private String name;
	
	private String avatarId;
	@Persistent(defaultFetchGroup = "true")
	private List<String> photoId;
	@Persistent(defaultFetchGroup = "true")
	private List<String> videoId;
	private Date creationDateTime;
	private Date statusModificationDateTime;
	private Date lastUpdate;
	
	private Integer fenceSize;
	private Integer checkinAreaSize;
	private String timezone;
	private String customCheckinMessage;
	private String externalId;

	@Persistent(defaultFetchGroup = "true")
	@Embedded
	private ContactInfo contactInfo;

	@Persistent(defaultFetchGroup = "true")
	@Embedded
	private Address address;

	@Persistent(defaultFetchGroup = "true")
	@Embedded	
	private InvoicingInfo invoicingInfo;

	@Persistent(defaultFetchGroup = "true")
	@Embedded	
	private ACL acl;
	
	private Integer status;

	// Search fields ... this is too ugly... Fuck you Google!!!!
	@SuppressWarnings("unused")
	private String uIdentifier;
	@SuppressWarnings("unused")
	private String uBrandName;
	@SuppressWarnings("unused")
	private String uStoreNumber;
	@SuppressWarnings("unused")
	private String uFloorNumber;
	@SuppressWarnings("unused")
	private String uShoppingName;
	@SuppressWarnings("unused")
	private String uName;

	@NotPersistent
	private boolean doIndexNow = true;

    public Store() {
    	photoId = new ArrayList<String>();
    	videoId = new ArrayList<String>();
		this.address = new Address();
		this.creationDateTime = new Date();
		this.contactInfo = new ContactInfo();
		this.acl = new ACL();
		this.status = StatusAware.STATUS_ENABLED;
		this.timezone = TimeZone.getDefault().toString();
		this.checkinAreaSize = 50;
		this.fenceSize = 0;
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
		uIdentifier = getIdentifier() == null ? "" : getIdentifier().toUpperCase();
		uStoreNumber = getStoreNumber() == null ? "" : getStoreNumber().toUpperCase();
		uFloorNumber = getFloorNumber() == null ? "" : getFloorNumber().toUpperCase();
		uShoppingName = getShoppingName() == null ? "" : getShoppingName().toUpperCase();
		uBrandName = getBrandName() == null ? "" : getBrandName().toUpperCase();
		uName = getName() == null ? "" : getName().toUpperCase();
		this.lastUpdate = new Date();
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
	 * @param shopping the shopping to set
	 */
	public void setShopping(Shopping shopping) {
		if( shopping == null || shopping.getIdentifier() == null || shopping.getIdentifier().equals("")) {
			this.shoppingId = null;
			this.shoppingName = null;
			if( null == this.address ) this.address = new Address();
			this.address.setCountry(null);
		} else {
			this.shoppingId = shopping.getIdentifier();
			this.shoppingName = shopping.getName();
			if( null == this.address ) this.address = new Address();
			this.address.setCountry(shopping.getAddress().getCountry());
		}
	}

	/**
	 * @param shopping the shopping to set
	 */
	public void setBrand(Brand brand) {
		if( brand == null || brand.getIdentifier() == null || brand.getIdentifier().equals("")) {
			this.brandId = null;
			this.brandName = null;
		} else {
			this.brandId = brand.getIdentifier();
			this.brandName = brand.getName();
		}
	}

	/**
	 * @return the shoppingName
	 */
	public String getShoppingName() {
		return shoppingName;
	}

	/**
	 * @param shoppingName the shoppingName to set
	 */
	public void setShoppingName(String shoppingName) {
		this.shoppingName = shoppingName;
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
	 * @return the brandName
	 */
	public String getBrandName() {
		return brandName;
	}

	/**
	 * @param brandName the brandName to set
	 */
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	/**
	 * @return the storeNumber
	 */
	public String getStoreNumber() {
		return storeNumber;
	}

	/**
	 * @param storeNumber the storeNumber to set
	 */
	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}

	/**
	 * @return the floorNumber
	 */
	public String getFloorNumber() {
		return floorNumber;
	}

	/**
	 * @param floorNumber the floorNumber to set
	 */
	public void setFloorNumber(String floorNumber) {
		this.floorNumber = floorNumber;
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
	 * @return the statusModificationDateTime
	 */
	public Date getStatusModificationDateTime() {
		return statusModificationDateTime;
	}

	/**
	 * @param statusModificationDateTime the statusModificationDateTime to set
	 */
	public void setStatusModificationDateTime(Date statusModificationDateTime) {
		this.statusModificationDateTime = statusModificationDateTime;
	}

	/**
	 * @return the invoicingInfo
	 */
	public InvoicingInfo getInvoicingInfo() {
		return invoicingInfo;
	}

	/**
	 * @param invoicingInfo the invoicingInfo to set
	 */
	public void setInvoicingInfo(InvoicingInfo invoicingInfo) {
		this.invoicingInfo = invoicingInfo;
	}

	/**
	 * @return the avatarId
	 */
	public String getAvatarId() {
		return avatarId;
	}

	/**
	 * @param avatarId the avatarId to set
	 */
	public void setAvatarId(String avatarId) {
		this.avatarId = avatarId;
	}

	/**
	 * @return the photoId
	 */
	public List<String> getPhotoId() {
		return photoId;
	}

	/**
	 * @param photoId the photoId to set
	 */
	public void setPhotoId(List<String> photoId) {
		this.photoId = photoId;
	}

	/**
	 * @return the videoId
	 */
	public List<String> getVideoId() {
		return videoId;
	}

	/**
	 * @param videoId the videoId to set
	 */
	public void setVideoId(List<String> videoId) {
		this.videoId = videoId;
	}

	/**
	 * Adds an Image to the current photo Id array
	 * @param photoId the Image Id to add
	 */
	public void addPhotoId(String photoId) {
		if( !this.photoId.contains(photoId)) {
			this.photoId.add(photoId);
		}
	}

	/**
	 * Removes an Image from the current photo id array
	 * @param photoId the Image Id to remove
	 */
	public void removePhotoId(String photoId) {
		if( this.photoId.contains(photoId)) {
			this.photoId.remove(photoId);
		}
	}

	/**
	 * @return the contactInfo
	 */
	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	/**
	 * @param contactInfo the contactInfo to set
	 */
	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
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
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * @return the fenceSize
	 */
	public Integer getFenceSize() {
		return fenceSize;
	}

	/**
	 * @param fenceSize the fenceSize to set
	 */
	public void setFenceSize(Integer fenceSize) {
		this.fenceSize = fenceSize;
	}

	/**
	 * @return the checkinAreaSize
	 */
	public Integer getCheckinAreaSize() {
		return checkinAreaSize;
	}

	/**
	 * @param checkinAreaSize the checkinAreaSize to set
	 */
	public void setCheckinAreaSize(Integer checkinAreaSize) {
		this.checkinAreaSize = checkinAreaSize;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the customCheckinMessage
	 */
	public String getCustomCheckinMessage() {
		return customCheckinMessage;
	}

	/**
	 * @param customCheckinMessage the customCheckinMessage to set
	 */
	public void setCustomCheckinMessage(String customCheckinMessage) {
		this.customCheckinMessage = customCheckinMessage;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @param externalId the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((brandId == null) ? 0 : brandId.hashCode());
		result = prime * result
				+ ((brandName == null) ? 0 : brandName.hashCode());
		result = prime * result
				+ ((floorNumber == null) ? 0 : floorNumber.hashCode());
		result = prime * result
				+ ((invoicingInfo == null) ? 0 : invoicingInfo.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((shoppingId == null) ? 0 : shoppingId.hashCode());
		result = prime * result
				+ ((shoppingName == null) ? 0 : shoppingName.hashCode());
		return result;
	}

	/**
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
		Store other = (Store) obj;
		if (brandId == null) {
			if (other.brandId != null)
				return false;
		} else if (!brandId.equals(other.brandId))
			return false;
		if (brandName == null) {
			if (other.brandName != null)
				return false;
		} else if (!brandName.equals(other.brandName))
			return false;
		if (floorNumber == null) {
			if (other.floorNumber != null)
				return false;
		} else if (!floorNumber.equals(other.floorNumber))
			return false;
		if (invoicingInfo == null) {
			if (other.invoicingInfo != null)
				return false;
		} else if (!invoicingInfo.equals(other.invoicingInfo))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (shoppingId == null) {
			if (other.shoppingId != null)
				return false;
		} else if (!shoppingId.equals(other.shoppingId))
			return false;
		if (shoppingName == null) {
			if (other.shoppingName != null)
				return false;
		} else if (!shoppingName.equals(other.shoppingName))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Store [key=" + key + ", shoppingId=" + shoppingId
				+ ", brandId=" + brandId + ", storeNumber=" + storeNumber
				+ ", floorNumber=" + floorNumber + "]";
	}

	@Override
	public boolean isAvailableFor(ViewLocation vl) {
		if( null == vl || !StringUtils.hasText(vl.getCountry()) 
				|| null == address || !StringUtils.hasText(address.getCountry())) return true;
		if( vl.getCountry().equalsIgnoreCase(address.getCountry())) return true;
		return false;
	}
	
	@Override
	public ACL getAcl() {
		return acl;
	}

	@Override
	public boolean doIndex() {
		return doIndexNow;
	}

	@Override
	public void disableIndexing(boolean val) {
		this.doIndexNow = !val;
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
	 * Returns current country
	 */
	public String getCountry() {
		return this.address != null ? this.address.getCountry() : null;
	}

}
