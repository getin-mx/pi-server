package mobi.allshoppings.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import mobi.allshoppings.model.adapter.IAdaptable;
import mobi.allshoppings.model.interfaces.ACLAware;
import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.Indexable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.Replicable;
import mobi.allshoppings.model.interfaces.ViewLocationAware;
import mobi.allshoppings.model.tools.ACL;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.CollectionFactory;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class Offer implements ModelKey, IAdaptable, Serializable, Identificable, Indexable, ViewLocationAware, ACLAware, Replicable {

	public static final String FILTER_ACTIVE_ONLY = "offersOnlyActive";
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	@Persistent(defaultFetchGroup = "true")
	private List<String> shoppings;
	@Persistent(defaultFetchGroup = "true")
	private List<String> brands;
	@Persistent(defaultFetchGroup = "true")
	private List<String> stores;

	private String offerTypeId;
	private String offerTypeName;
	private String offerTypeRibbonText;
	
	@Persistent(defaultFetchGroup = "true")
	private List<String> areaId;
	
	private String name;
	private String description;
	private String avatarId;
	@Persistent(defaultFetchGroup = "true")
	private List<String> photoId;
	@Persistent(defaultFetchGroup = "true")
	private List<String> videoId;
	private Date creationDateTime;
	private Date statusModificationDateTime;
	private Text policies;
	private Date validFrom;
	private Date validTo;
	private Date lastUpdate;
	@Persistent(defaultFetchGroup = "true")
	private List<String> availableFinancialEntities;
	private String notifyFromHour;
	private String notifyToHour;
	@Persistent(defaultFetchGroup = "true")
	private List<String> notifyDays;
	private String country;
	private String source;
	private String origin;

	private Boolean exclusive = false;
	
	@Persistent(defaultFetchGroup = "true")
	@Embedded	
	private ACL acl;
	
	private boolean expired = false;
	
	// Search fields ... this is too ugly... Fuck you Google!!!!
	@SuppressWarnings("unused")
	private String uIdentifier;
	@SuppressWarnings("unused")
	private String uOfferTypeName;
	@SuppressWarnings("unused")
	private String uCountry;

	@NotPersistent
	private boolean doIndexNow = true;
	@NotPersistent
	private final static SimpleDateFormat dateOnlySDF = new SimpleDateFormat("yyyyMMdd"); 

    public Offer() {
		this.creationDateTime = new Date();
		this.areaId = new ArrayList<String>();
		this.photoId = new ArrayList<String>();
		this.videoId = new ArrayList<String>();
		this.shoppings = new ArrayList<String>();
		this.brands = new ArrayList<String>();
		this.stores = new ArrayList<String>();
		this.availableFinancialEntities = new ArrayList<String>();
		this.acl = new ACL();
		this.policies = new Text("");
		this.notifyDays = new ArrayList<String>();
		this.notifyDays.add("0");
		this.notifyDays.add("1");
		this.notifyDays.add("2");
		this.notifyDays.add("3");
		this.notifyDays.add("4");
		this.notifyDays.add("5");
		this.notifyDays.add("6");
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
		uOfferTypeName = getOfferTypeName() == null ? "" : getOfferTypeName().toUpperCase();
		uCountry = getCountry() == null ? "" : getCountry().toUpperCase();
		this.lastUpdate = new Date();
		try {
			if(validTo != null && validTo.before(dateOnlySDF.parse(dateOnlySDF.format(new Date())))) {
				setExpired(true);
			} else {
				setExpired(false);
			}
		} catch( ParseException | NumberFormatException e ) {
			// Nothing to do here
		}
	}

	/**
	 * @return the areaId
	 */
	public List<String> getAreaId() {
		return areaId;
	}

	/**
	 * @return the areaId
	 */
	public List<String> getAreas() {
		return areaId;
	}
	
	/**
	 * @param areaId the areaId to set
	 */
	public void setAreaId(List<String> areaId) {
		this.areaId = areaId;
	}

	/**
	 * @param areas the area list to add
	 */
	public void addAreas(Collection<String> areas) {
		for(String s : areas) {
			this.areaId.add(s);
		}
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the offerTypeId
	 */
	public String getOfferTypeId() {
		return offerTypeId;
	}

	/**
	 * @param offerTypeId the offerTypeId to set
	 */
	public void setOfferTypeId(String offerTypeId) {
		this.offerTypeId = offerTypeId;
	}

	/**
	 * Sets a new offer type
	 * @param offerType the offer type to set
	 */
	public void setOfferType(OfferType offerType) {
		if( offerType != null ) {
			this.offerTypeId = offerType.getIdentifier();
			this.offerTypeName = offerType.getName().get("es");
			this.offerTypeRibbonText = offerType.getRibbonText();
		} else {
			this.offerTypeId = null;
			this.offerTypeName = null;
			this.offerTypeRibbonText = null;
		}
	}
	
	/**
	 * Clears the Shopping List
	 */
	public void clearShoppings() {
		this.shoppings.clear();
	}
	
	/**
	 * Sets a new shopping for this offer
	 * @param shopping the shopping to set
	 */
	public void addShopping(Shopping shopping) {
		if( shopping != null ) {
			this.shoppings.add(shopping.getIdentifier());
		}
	}

	/**
	 * Sets a new shopping for this offer
	 * @param id Shopping id
	 */
	public void addShopping(String id) {
		if( StringUtils.hasText(id))
			this.shoppings.add(id);
	}
	
	/**
	 * Removes a shopping from this offer
	 * @param shopping The shopping to remove
	 */
	public void removeShopping(Shopping shopping) {
		this.shoppings.remove(shopping.getIdentifier());
	}
	
	/**
	 * Removes a shopping from this offer
	 * @param shopping The shopping to remove
	 */
	public void removeShopping(String shopping) {
		this.shoppings.remove(shopping);
	}

	/**
	 * Clears brand list
	 */
	public void clearBrands() {
		this.brands.clear();
	}
	
	/**
	 * Sets a new brand for this offer
	 * @param brand the brand to set
	 */
	public void addBrand(Brand brand) {
		if( brand != null ) {
			this.brands.add(brand.getIdentifier());
		}
	}

	/**
	 * Sets a new brand for this offer
	 * @param id Brand id
	 */
	public void addBrand(String id) {
		if( StringUtils.hasText(id))
			this.brands.add(id);
	}
	
	/**
	 * Removes a brand from this offer
	 * @param brand The brand to remove
	 */
	public void removeBrand(Brand brand) {
		this.brands.remove(brand.getIdentifier());
	}

	/**
	 * Removes a brand from this offer
	 * @param brand The brand to remove
	 */
	public void removeBrand(String brand) {
		this.brands.remove(brand);
	}

	/**
	 * Clears stores
	 */
	public void clearStores() {
		this.stores.clear();
	}
	
	/**
	 * Sets a new store for this offer
	 * @param store the store to set
	 */
	public void addStore(Store store) {
		if( store != null ) {
			this.stores.add(store.getIdentifier());
		}
	}

	/**
	 * Sets a new store for this offer
	 * @param store the store to set
	 */
	public void addStore(String store) {
		if( store != null ) {
			this.stores.add(store);
		}
	}
	
	/**
	 * Removes a store from this offer
	 * @param store the store to remove
	 */
	public void removeStore(Store store) {
		this.stores.remove(store.getIdentifier());
	}

	/**
	 * Removes a store from this offer
	 * @param store the store to remove
	 */
	public void removeStore(String store) {
		this.stores.remove(store);
	}
	
	/**
	 * @return the offerTypeName
	 */
	public String getOfferTypeName() {
		return offerTypeName;
	}

	/**
	 * @param offerTypeName the offerTypeName to set
	 */
	public void setOfferTypeName(String offerTypeName) {
		this.offerTypeName = offerTypeName;
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
	 * @return the policies
	 */
	public String getPolicies() {
		return policies.getValue();
	}

	/**
	 * @param policies the policies to set
	 */
	public void setPolicies(String policies) {
		this.policies = new Text(policies);
	}

	/**
	 * @return the validFrom
	 */
	public Date getValidFrom() {
		return validFrom;
	}

	/**
	 * @param validFrom the validFrom to set
	 */
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	/**
	 * @return the validTo
	 */
	public Date getValidTo() {
		return validTo;
	}

	/**
	 * @param validTo the validTo to set
	 */
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		Offer other = (Offer) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Offer [key=" + key + ", shoppingId=" + shoppings
				+ ", storeId=" + stores + ", brandId=" + brands + "]";
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
	 * Adds a new area to the areas id array
	 * @param areaId The area to add
	 */
	public void addAreaId(String areaId) {
		if(!this.areaId.contains(areaId)) {
			this.areaId.add(areaId);
		}
	}
	
	/**
	 * Clears the areas id array
	 */
	public void clearAreas() {
		this.areaId.clear();
	}
	
	/**
	 * Removes an area from the areas id array
	 * @param areaId The area to remove
	 */
	public void removeAreaId(String areaId) {
		if(this.areaId.contains(areaId)) {
			this.areaId.remove(areaId);
		}
	}

	/**
	 * @return the shoppings
	 */
	public List<String> getShoppings() {
		return shoppings;
	}

	/**
	 * @param shoppings the shoppings to set
	 */
	public void setShoppings(List<String> shoppings) {
		this.shoppings = shoppings;
	}

	/**
	 * @param shoppings the shopping list to add
	 */
	public void addShoppings(Collection<String> shoppings) {
		for(String s : shoppings) {
			this.shoppings.add(s);
		}
	}
	
	/**
	 * @return the brands
	 */
	public List<String> getBrands() {
		return brands;
	}

	/**
	 * @param brands the brands to set
	 */
	public void setBrands(List<String> brands) {
		this.brands = brands;
	}

	/**
	 * @return the stores
	 */
	public List<String> getStores() {
		return stores;
	}

	/**
	 * @param stores the stores to set
	 */
	public void setStores(List<String> stores) {
		this.stores = stores;
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
	 * @return the availableFinancialEntities
	 */
	public List<String> getAvailableFinancialEntities() {
		return availableFinancialEntities;
	}

	/**
	 * @param availableFinancialEntities the availableFinancialEntities to set
	 */
	public void setAvailableFinancialEntities(List<String> availableFinancialEntities) {
		this.availableFinancialEntities = availableFinancialEntities;
	}
	
	/**
	 * Adds a new financial asset to the available financial assets list
	 * @param financialEntity The financial asset to add
	 */
	public void addAvailableFinancialEntity(String financialEntity) {
		if(!availableFinancialEntities.contains(financialEntity))
			availableFinancialEntities.add(financialEntity);
	}

	/**
	 * Removes a country from the available financial asset list
	 * @param financialEntity the financial asset to remove
	 */
	public void removeAvailableFinancialEntity(String financialEntity) {
		if( StringUtils.hasText(financialEntity))
			if(availableFinancialEntities.contains(financialEntity))
				availableFinancialEntities.remove(financialEntity);
	}
	
	/**
	 * Clears the available financial assets list
	 */
	public void clearAvailableFinancialEntities() {
		this.availableFinancialEntities = CollectionFactory.createList();
	}
	
	/**
	 * Clears the notify days list
	 */
	public void clearNotifyDays() {
		this.notifyDays = CollectionFactory.createList();
	}
	
	/**
	 * Adds a new day to the notify days list
	 * @param day
	 */
	public void addNotifyDay(String day) {
		if(!notifyDays.contains(day))
			notifyDays.add(day);
	}
	
	@Override
	public boolean isAvailableFor(ViewLocation vl) {
		if( null == vl || !StringUtils.hasText(vl.getCountry())) return true;
		if( vl.getCountry().equals(country)) return true;
		return false;
	}


	public String getNotifyFromHour() {
		return notifyFromHour;
	}

	public void setNotifyFromHour(String notifyFromHour) {
		this.notifyFromHour = notifyFromHour;
	}

	public String getNotifyToHour() {
		return notifyToHour;
	}

	public void setNotifyToHour(String notifyToHour) {
		this.notifyToHour = notifyToHour;
	}

	public List<String> getNotifyDays() {
		return notifyDays;
	}

	public void setNotifyDays(List<String> notifyDays) {
		this.notifyDays = notifyDays;
	}

	/**
	 * @return the exclusive
	 */
	public Boolean getExclusive() {
		return exclusive;
	}

	/**
	 * @param exclusive the exclusive to set
	 */
	public void setExclusive(Boolean exclusive) {
		this.exclusive = exclusive;
	}

	public boolean appliesForToday() {
		return appliesForDate(new Date());
	}
	
	/**
	 * @return the offerTypeRibbonText
	 */
	public String getOfferTypeRibbonText() {
		return offerTypeRibbonText;
	}

	/**
	 * @param offerTypeRibbonText the offerTypeRibbonText to set
	 */
	public void setOfferTypeRibbonText(String offerTypeRibbonText) {
		this.offerTypeRibbonText = offerTypeRibbonText;
	}

	/**
	 * Checks if the selected offer applies with the notify days and times
	 * criteria
	 * 
	 * @param offer
	 *            The offer to check
	 * @param date
	 *            The date to validate
	 * @return true if the offer applies for the selected date, false if not
	 */
	public boolean appliesForDate(Date date) {

		try {
			// Gets day of week
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			String dayCode = null;
			switch(dayOfWeek) {
			case Calendar.MONDAY:
				dayCode = "0";
				break;
			case Calendar.TUESDAY:
				dayCode = "1";
				break;
			case Calendar.WEDNESDAY:
				dayCode = "2";
				break;
			case Calendar.THURSDAY:
				dayCode = "3";
				break;
			case Calendar.FRIDAY:
				dayCode = "4";
				break;
			case Calendar.SATURDAY:
				dayCode = "5";
				break;
			case Calendar.SUNDAY:
				dayCode = "6";
				break;
			}

			// First we do a day check
			if( dayCode == null ) return false;
			if(!this.getNotifyDays().contains(dayCode)) return false;

			return true;

		} catch( Throwable t ) {
			return false;
		}
	}

	/**
	 * @return the expired
	 */
	public boolean isExpired() {
		return expired;
	}

	/**
	 * @param expired the expired to set
	 */
	public void setExpired(boolean expired) {
		this.expired = expired;
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
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

}
