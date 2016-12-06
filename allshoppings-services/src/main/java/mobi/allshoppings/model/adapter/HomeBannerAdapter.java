package mobi.allshoppings.model.adapter;

import java.util.List;

import mobi.allshoppings.tools.CollectionFactory;

public class HomeBannerAdapter implements IGenericAdapter {

	private String homeBanner;
	private List<String> homeCarouselImages;

	public HomeBannerAdapter() {
		homeCarouselImages = CollectionFactory.createList();
	}
	
	/**
	 * @return the homeCarouselImages
	 */
	public List<String> getHomeCarouselImages() {
		return homeCarouselImages;
	}
	
	/**
	 * @param homeCarouselImages the homeCarouselImages to set
	 */
	public void setHomeCarouselImages(List<String> homeCarouselImages) {
		this.homeCarouselImages = homeCarouselImages;
	}

	/**
	 * @return the homeBanner
	 */
	public String getHomeBanner() {
		return homeBanner;
	}

	/**
	 * @param homeBanner the homeBanner to set
	 */
	public void setHomeBanner(String homeBanner) {
		this.homeBanner = homeBanner;
	}

}
