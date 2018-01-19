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

import mobi.allshoppings.model.embedded.CinemaEmbedd;
import mobi.allshoppings.model.embedded.MovieEmbedd;
import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.StatusAware;
import mobi.allshoppings.tools.CollectionFactory;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class Showtime implements ModelKey, Serializable, Identificable, StatusAware {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String internalIdentifier;

	private String formatName;

	@Persistent(defaultFetchGroup = "true")
	@Embedded
	private CinemaEmbedd cinema;

	@Persistent(defaultFetchGroup = "true")
	@Embedded
	private MovieEmbedd movie;
	
	private String showDate;
	private String showTime;
		
	private String shoppingId;
	private String brandId;
	private String storeId;

	private Integer status;
	
	private Date creationDateTime;
	private Date lastUpdate;
	
	private Integer availableSeats;
	private String screen;
	private Integer maxCouponsToSend;
	private Integer candidatesFound;
	@Persistent(defaultFetchGroup = "true")
	private List<String> userIds;
	@Persistent(defaultFetchGroup = "true")
	private List<String> campaignActivityIds;

	public Showtime() {
		super();
		this.creationDateTime = new Date();
		this.userIds = CollectionFactory.createList();
		this.campaignActivityIds = CollectionFactory.createList();
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
	 * @return the formatName
	 */
	public String getFormatName() {
		return formatName;
	}

	/**
	 * @param formatName the formatName to set
	 */
	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	/**
	 * @return the cinema
	 */
	public CinemaEmbedd getCinema() {
		return cinema;
	}

	/**
	 * @param cinema the cinema to set
	 */
	public void setCinema(CinemaEmbedd cinema) {
		this.cinema = cinema;
	}

	/**
	 * @return the movie
	 */
	public MovieEmbedd getMovie() {
		return movie;
	}

	/**
	 * @param movie the movie to set
	 */
	public void setMovie(MovieEmbedd movie) {
		this.movie = movie;
	}

	/**
	 * @return the showDate
	 */
	public String getShowDate() {
		return showDate;
	}

	/**
	 * @param showDate the showDate to set
	 */
	public void setShowDate(String showDate) {
		this.showDate = showDate;
	}

	/**
	 * @return the showTime
	 */
	public String getShowTime() {
		return showTime;
	}

	/**
	 * @param showTime the showTime to set
	 */
	public void setShowTime(String showTime) {
		this.showTime = showTime;
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
	 * @return the availableSeats
	 */
	public Integer getAvailableSeats() {
		return availableSeats;
	}

	/**
	 * @param availableSeats the availableSeats to set
	 */
	public void setAvailableSeats(Integer availableSeats) {
		this.availableSeats = availableSeats;
	}

	/**
	 * @return the screen
	 */
	public String getScreen() {
		return screen;
	}

	/**
	 * @param screen the screen to set
	 */
	public void setScreen(String screen) {
		this.screen = screen;
	}

	/**
	 * @return the maxCouponsToSend
	 */
	public Integer getMaxCouponsToSend() {
		return maxCouponsToSend;
	}

	/**
	 * @param maxCouponsToSend the maxCouponsToSend to set
	 */
	public void setMaxCouponsToSend(Integer maxCouponsToSend) {
		this.maxCouponsToSend = maxCouponsToSend;
	}

	/**
	 * @return the candidatesFound
	 */
	public Integer getCandidatesFound() {
		return candidatesFound;
	}

	/**
	 * @param candidatesFound the candidatesFound to set
	 */
	public void setCandidatesFound(Integer candidatesFound) {
		this.candidatesFound = candidatesFound;
	}

	/**
	 * @return the userIds
	 */
	public List<String> getUserIds() {
		return userIds;
	}

	/**
	 * @param userIds the userIds to set
	 */
	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	/**
	 * @return the campaignActivityIds
	 */
	public List<String> getCampaignActivityIds() {
		return campaignActivityIds;
	}

	/**
	 * @param campaignActivityIds the campaignActivityIds to set
	 */
	public void setCampaignActivityIds(List<String> campaignActivityIds) {
		this.campaignActivityIds = campaignActivityIds;
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
		Showtime other = (Showtime) obj;
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
		return "ShowTime [key=" + key + ", internalIdentifier="
				+ internalIdentifier 
				+ ", formatName=" + formatName + ", cinema=" + cinema
				+ ", movie=" + movie + ", showDate=" + showDate + ", showTime="
				+ showTime + ", shoppingId=" + shoppingId + ", brandId="
				+ brandId + ", storeId=" + storeId + ", status=" + status
				+ ", creationDateTime=" + creationDateTime + ", lastUpdate="
				+ lastUpdate + "]";
	}

}
