package mobi.allshoppings.model.adapter;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.CampaignActivity;

public class CouponAdapter extends CampaignActivity implements IGenericAdapter, ICompletableAdapter {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CouponAdapter.class.getName());
	
	public static final String OPTIONS_CSDAO = "CampaignSpecialDAO";
	public static final String OPTIONS_CHELPER = "CampaignHelper";
	public static final String OPTIONS_SDAO = "ShoppingDAO";
	public static final String OPTIONS_BDAO = "BrandDAO";
	public static final String OPTIONS_FEDAO = "FinancialEntityDAO";
	public static final String OPTIONS_UECS = "uecShopping";
	public static final String OPTIONS_UECB = "uecBrand";
	public static final String OPTIONS_UECFE = "uecFinancialEntity";
	public static final String OPTIONS_REQUESTER = "requester";
	public static final String OPTIONS_INCLUDE_EXPIRED_STATUS = "includeExpiredStatus";
	
	private static final long serialVersionUID = 5657858175564391408L;
	
	private String shoppingId;
	private String shoppingName;
	private String storeId;
	private String storeName;
	private String brandId;
	private String brandName;
	private Long points;
	
	private List<NameAndIdAndFavoriteAdapter> shoppingList;
	private List<NameAndIdAndFavoriteAdapter> brandList;
	private List<NameAndIdAndFavoriteAdapter> financialEntityList;
	private List<NameAndIdAdapter> storeList;
	
	private String offerTypeId;
	private String offerTypeName;
	private String offerTypeRibbonText;
	
	private List<String> areaId;
	
	private String name;
	private String campaignId;
	private String description;
	private String instructions;
	private String avatarId;
	private List<String> photoId;
	private List<String> videoId;
	private String policies;
	private Date validFrom;
	private Date validTo;
	private Date lastUpdate;
	private List<String> availableFinancialEntities;
	private String notifyFromHour;
	private String notifyToHour;
	private List<String> notifyDays;
	private List<String> genders;
	private Integer ageFrom;
	private Integer ageTo;	
	private String country;
	private Float timezone;
	private Integer trigger;
	private Long span;
	private Long quantity;
	private Long dailyQuantity;

	private String userName;
	private String email;
	
	public CouponAdapter() {
		super();
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
	 * @return the storeName
	 */
	public String getStoreName() {
		return storeName;
	}

	/**
	 * @param storeName the storeName to set
	 */
	public void setStoreName(String storeName) {
		this.storeName = storeName;
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
	 * @return the shoppingList
	 */
	public List<NameAndIdAndFavoriteAdapter> getShoppingList() {
		return shoppingList;
	}

	/**
	 * @param shoppingList the shoppingList to set
	 */
	public void setShoppingList(List<NameAndIdAndFavoriteAdapter> shoppingList) {
		this.shoppingList = shoppingList;
	}

	/**
	 * @return the brandList
	 */
	public List<NameAndIdAndFavoriteAdapter> getBrandList() {
		return brandList;
	}

	/**
	 * @param brandList the brandList to set
	 */
	public void setBrandList(List<NameAndIdAndFavoriteAdapter> brandList) {
		this.brandList = brandList;
	}

	/**
	 * @return the storeList
	 */
	public List<NameAndIdAdapter> getStoreList() {
		return storeList;
	}

	/**
	 * @param storeList the storeList to set
	 */
	public void setStoreList(List<NameAndIdAdapter> storeList) {
		this.storeList = storeList;
	}

	/**
	 * @return the financialEntityList
	 */
	public List<NameAndIdAndFavoriteAdapter> getFinancialEntityList() {
		return financialEntityList;
	}

	/**
	 * @param financialEntityList the financialEntityList to set
	 */
	public void setFinancialEntityList(
			List<NameAndIdAndFavoriteAdapter> financialEntityList) {
		this.financialEntityList = financialEntityList;
	}

	/**
	 * @return the points
	 */
	public Long getPoints() {
		return points;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(Long points) {
		this.points = points;
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
	 * @return the policies
	 */
	public String getPolicies() {
		return policies;
	}

	/**
	 * @param policies the policies to set
	 */
	public void setPolicies(String policies) {
		this.policies = policies;
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
	public void setAvailableFinancialEntities(
			List<String> availableFinancialEntities) {
		this.availableFinancialEntities = availableFinancialEntities;
	}

	/**
	 * @return the notifyFromHourgetRequester()
	 */
	public String getNotifyFromHour() {
		return notifyFromHour;
	}

	/**
	 * @param notifyFromHour the notifyFromHour to set
	 */
	public void setNotifyFromHour(String notifyFromHour) {
		this.notifyFromHour = notifyFromHour;
	}

	/**
	 * @return the notifyToHour
	 */
	public String getNotifyToHour() {
		return notifyToHour;
	}

	/**
	 * @param notifyToHour the notifyToHour to set
	 */
	public void setNotifyToHour(String notifyToHour) {
		this.notifyToHour = notifyToHour;
	}

	/**
	 * @return the notifyDays
	 */
	public List<String> getNotifyDays() {
		return notifyDays;
	}

	/**
	 * @param notifyDays the notifyDays to set
	 */
	public void setNotifyDays(List<String> notifyDays) {
		this.notifyDays = notifyDays;
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
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public void completeAdaptation(Map<String, Object> options) throws ASException {
		// FIXME: Add this stuff here
	}

}
