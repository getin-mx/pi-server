package mobi.allshoppings.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;

import mobi.allshoppings.model.adapter.IAdaptable;
import mobi.allshoppings.model.interfaces.ACLAware;
import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.Indexable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.ViewLocationAware;
import mobi.allshoppings.model.tools.ACL;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.CollectionFactory;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class CampaignSpecial implements ModelKey, IAdaptable, Serializable, Identificable, Indexable, ViewLocationAware, ACLAware {

	public static final String FILTER_ACTIVE_ONLY = "offersOnlyActive";
	public static final String DEFAULT_PROMOTION_TYPE = "Checkin";
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	@Persistent(defaultFetchGroup = "true")
	private Set<String> shoppings;
	@Persistent(defaultFetchGroup = "true")
	private Set<String> brands;
	@Persistent(defaultFetchGroup = "true")
	private Set<String> stores;
	@Persistent(defaultFetchGroup = "true")
	private Set<String> appIds;

	private String offerTypeId;
	private String offerTypeName;
	private String offerTypeRibbonText;
	
	@Persistent(defaultFetchGroup = "true")
	private List<String> areaId;
	
	private String name;
	private String campaignId;
	private String description;
	private String instructions;
	private String avatarId;
	@Persistent(defaultFetchGroup = "true")
	private List<String> photoId;
	@Persistent(defaultFetchGroup = "true")
	private List<String> videoId;
	private Date creationDateTime;
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
	@Persistent(defaultFetchGroup = "true")
	private List<String> genders;
	private Integer ageFrom;
	private Integer ageTo;	
	private String country;
	private Float timezone;
	private Integer trigger;
	private Long span;
	private Long quantity;
	private Long dailyQuantity;
	private String customUrl;
	private String promotionType;

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

    public CampaignSpecial() {
    	this.creationDateTime = new Date();
		this.areaId = new ArrayList<String>();
		this.photoId = new ArrayList<String>();
		this.videoId = new ArrayList<String>();
		this.shoppings = new HashSet<String>();
		this.brands = new HashSet<String>();
		this.stores = new HashSet<String>();
		this.acl = new ACL();
		this.policies = new Text("");
		this.notifyDays = new ArrayList<String>();
		
    	this.timezone = -5.0F;
    	this.span = 7200000L;
    	this.notifyFromHour = "09:00";
    	this.notifyToHour = "21:00";
    	this.quantity = 0L;
    	this.dailyQuantity = 0L;
    	this.ageFrom = 0;
    	this.ageTo = 99;
    	this.promotionType = DEFAULT_PROMOTION_TYPE;
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
		uIdentifier = getName() == null ? "" : getName().toUpperCase();
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
	 * @param areaId the areaId to set
	 */
	public void setAreaId(List<String> areaId) {
		this.areaId = areaId;
	}

	/**
	 * @param areas the area list to add
	 */
	public void addAreas(Collection<String> areas) {
		if( areas == null ) areas = new HashSet<String>();
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
		if( shoppings == null ) shoppings = new HashSet<String>();
		this.shoppings.clear();
	}
	
	/**
	 * Sets a new shopping for this offer
	 * @param shopping the shopping to set
	 */
	public void addShopping(Shopping shopping) {
		if( shoppings == null ) shoppings = new HashSet<String>();
		if( shopping != null ) {
			this.shoppings.add(shopping.getIdentifier());
		}
	}

	/**
	 * Sets a new shopping for this offer
	 * @param id Shopping id
	 */
	public void addShopping(String id) {
		if( shoppings == null ) shoppings = new HashSet<String>();
		if( StringUtils.hasText(id))
			this.shoppings.add(id);
	}
	
	/**
	 * Removes a shopping from this offer
	 * @param shopping The shopping to remove
	 */
	public void removeShopping(Shopping shopping) {
		if( shoppings == null ) shoppings = new HashSet<String>();
		this.shoppings.remove(shopping.getIdentifier());
	}
	
	/**
	 * Removes a shopping from this offer
	 * @param shopping The shopping to remove
	 */
	public void removeShopping(String shopping) {
		if( shoppings == null ) shoppings = new HashSet<String>();
		this.shoppings.remove(shopping);
	}

	/**
	 * Clears brand list
	 */
	public void clearBrands() {
		if( brands == null ) brands = new HashSet<String>();
		this.brands.clear();
	}
	
	/**
	 * Sets a new brand for this offer
	 * @param brand the brand to set
	 */
	public void addBrand(Brand brand) {
		if( brands == null ) brands = new HashSet<String>();
		if( brand != null ) {
			this.brands.add(brand.getIdentifier());
		}
	}

	/**
	 * Sets a new brand for this offer
	 * @param id Brand id
	 */
	public void addBrand(String id) {
		if( brands == null ) brands = new HashSet<String>();
		if( StringUtils.hasText(id))
			this.brands.add(id);
	}
	
	/**
	 * Removes a brand from this offer
	 * @param brand The brand to remove
	 */
	public void removeBrand(Brand brand) {
		if( brands == null ) brands = new HashSet<String>();
		this.brands.remove(brand.getIdentifier());
	}

	/**
	 * Removes a brand from this offer
	 * @param brand The brand to remove
	 */
	public void removeBrand(String brand) {
		if( brands == null ) brands = new HashSet<String>();
		this.brands.remove(brand);
	}

	/**
	 * Clears stores
	 */
	public void clearStores() {
		if( stores == null ) stores = new HashSet<String>();
		this.stores.clear();
	}
	
	/**
	 * Sets a new store for this offer
	 * @param store the store to set
	 */
	public void addStore(Store store) {
		if( stores == null ) stores = new HashSet<String>();
		if( store != null ) {
			this.stores.add(store.getIdentifier());
		}
	}

	/**
	 * Sets a new store for this offer
	 * @param store the store to set
	 */
	public void addStore(String store) {
		if( stores == null ) stores = new HashSet<String>();
		if( store != null ) {
			this.stores.add(store);
		}
	}
	
	/**
	 * Removes a store from this offer
	 * @param store the store to remove
	 */
	public void removeStore(Store store) {
		if( stores == null ) stores = new HashSet<String>();
		this.stores.remove(store.getIdentifier());
	}

	/**
	 * Removes a store from this offer
	 * @param store the store to remove
	 */
	public void removeStore(String store) {
		if( stores == null ) stores = new HashSet<String>();
		this.stores.remove(store);
	}

	/**
	 * Clears appIds
	 */
	public void clearAppIds() {
		if( appIds == null ) appIds = new HashSet<String>();
		this.appIds.clear();
	}
	
	/**
	 * Sets a new appId for this offer
	 * @param store the store to set
	 */
	public void addAppId(String appId) {
		if( appIds == null ) appIds = new HashSet<String>();
		if( StringUtils.hasText(appId)) {
			this.appIds.add(appId);
		}
	}

	/**
	 * Removes an appId from this offer
	 * @param store the store to remove
	 */
	public void removeAppId(String appId) {
		if( appIds == null ) appIds = new HashSet<String>();
		this.stores.remove(appId);
	}
	
	/**
	 * @return the appIds
	 */
	public Set<String> getAppIds() {
		if( appIds == null ) appIds = new HashSet<String>();
		return appIds;
	}

	/**
	 * @param appIds the appIds to set
	 */
	public void setAppIds(Set<String> appIds) {
		if( appIds == null ) appIds = new HashSet<String>();
		this.appIds = appIds;
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
	public Set<String> getShoppings() {
		return shoppings;
	}

	/**
	 * @param shoppings the shoppings to set
	 */
	public void setShoppings(Set<String> shoppings) {
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
	public Set<String> getBrands() {
		return brands;
	}

	/**
	 * @param brands the brands to set
	 */
	public void setBrands(Set<String> brands) {
		this.brands = brands;
	}

	/**
	 * @return the stores
	 */
	public Set<String> getStores() {
		return stores;
	}

	/**
	 * @param stores the stores to set
	 */
	public void setStores(Set<String> stores) {
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
	 * Clears the genders list
	 */
	public void clearGenders() {
		this.genders = CollectionFactory.createList();
	}

	/**
	 * Adds a new day to the notify days list
	 * @param day
	 */
	public void addNotifyDay(String day) {
		if(!notifyDays.contains(day))
			notifyDays.add(day);
	}
	
	/**
	 * Adds a new gender to the genders list
	 * @param gender
	 */
	public void addGender(String gender) {
		if(!genders.contains(gender))
			genders.add(gender);
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
	 * @return the campaignId
	 */
	public String getCampaignId() {
		return campaignId;
	}

	/**
	 * @param campaignId the campaignId to set
	 */
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	/**
	 * @return the span
	 */
	public Long getSpan() {
		return span;
	}

	/**
	 * @param span the span to set
	 */
	public void setSpan(Long span) {
		this.span = span;
	}

	/**
	 * @return the trigger
	 */
	public Integer getTrigger() {
		return trigger;
	}

	/**
	 * @param trigger the trigger to set
	 */
	public void setTrigger(Integer trigger) {
		this.trigger = trigger;
	}

	/**
	 * @return the timezone
	 */
	public Float getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(Float timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the quantity
	 */
	public Long getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the dailyQuantity
	 */
	public Long getDailyQuantity() {
		return dailyQuantity;
	}

	/**
	 * @param dailyQuantity the dailyQuantity to set
	 */
	public void setDailyQuantity(Long dailyQuantity) {
		this.dailyQuantity = dailyQuantity;
	}

	/**
	 * @return the instructions
	 */
	public String getInstructions() {
		return instructions;
	}

	/**
	 * @param instructions the instructions to set
	 */
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	/**
	 * @return the genders
	 */
	public List<String> getGenders() {
		return genders;
	}

	/**
	 * @param genders the genders to set
	 */
	public void setGenders(List<String> genders) {
		this.genders = genders;
	}

	/**
	 * @return the ageFrom
	 */
	public Integer getAgeFrom() {
		return ageFrom;
	}

	/**
	 * @param ageFrom the ageFrom to set
	 */
	public void setAgeFrom(Integer ageFrom) {
		this.ageFrom = ageFrom;
	}

	/**
	 * @return the ageTo
	 */
	public Integer getAgeTo() {
		return ageTo;
	}

	/**
	 * @param ageTo the ageTo to set
	 */
	public void setAgeTo(Integer ageTo) {
		this.ageTo = ageTo;
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
	 * @return the promotionType
	 */
	public String getPromotionType() {
		return promotionType;
	}

	/**
	 * @param promotionType the promotionType to set
	 */
	public void setPromotionType(String promotionType) {
		this.promotionType = promotionType;
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
		CampaignSpecial other = (CampaignSpecial) obj;
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
		return "CampaignSpecial [key=" + key + ", shoppings=" + shoppings + ", brands=" + brands + ", stores=" + stores
				+ ", appIds=" + appIds + ", offerTypeId=" + offerTypeId + ", offerTypeName=" + offerTypeName
				+ ", offerTypeRibbonText=" + offerTypeRibbonText + ", areaId=" + areaId + ", name=" + name
				+ ", campaignId=" + campaignId + ", description=" + description + ", instructions=" + instructions
				+ ", avatarId=" + avatarId + ", photoId=" + photoId + ", videoId=" + videoId + ", creationDateTime="
				+ creationDateTime + ", policies=" + policies + ", validFrom=" + validFrom + ", validTo=" + validTo
				+ ", lastUpdate=" + lastUpdate + ", availableFinancialEntities=" + availableFinancialEntities
				+ ", notifyFromHour=" + notifyFromHour + ", notifyToHour=" + notifyToHour + ", notifyDays=" + notifyDays
				+ ", genders=" + genders + ", ageFrom=" + ageFrom + ", ageTo=" + ageTo + ", country=" + country
				+ ", timezone=" + timezone + ", trigger=" + trigger + ", span=" + span + ", quantity=" + quantity
				+ ", dailyQuantity=" + dailyQuantity + ", customUrl=" + customUrl + ", promotionType=" + promotionType
				+ ", acl=" + acl + ", expired=" + expired + "]";
	}
	
}
