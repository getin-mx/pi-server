package mobi.allshoppings.model.adapter;

import java.util.List;

import mobi.allshoppings.model.Offer;

public class OfferAdapter extends Offer implements IGenericAdapter, IFavorite {

	private static final long serialVersionUID = 5657858175564391408L;

	private String requester;
	
	private Boolean favorite;
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
	
	public OfferAdapter() {
		super();
	}
	
	@Override
	public Boolean getFavorite() {
		return this.favorite;
	}

	@Override
	public void setFavorite(Boolean favorite) {
		this.favorite = favorite;
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


	@Override
	public String getRequester() {
		return requester;
	}

	@Override
	public void setRequester(String requester) {
		this.requester = requester;
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

}
