package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;

/**
 * Daily & hourly indicator to paint all tables in dashboard/apdvisits
 * @author Matias Hapanowicz
 * @author <a href="mailto:ignacio@getin.mx" >Manuel "Nachintoch" Castillo</a>
 * @version 2.0, december 2017
 * @since allshoppings
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
@Cacheable("false")
public class DashboardIndicatorData implements ModelKey, Serializable, Identificable {

	@Deprecated
	public static final String PERIOD_TYPE_DAILY = "D";
	
	@Deprecated
	public static final String PERIOD_TYPE_WEEKLY = "W";
	
	@Deprecated
	public static final String PERIOD_TYPE_MONTHLY = "M";
	
	public static final byte TIME_ZONE_ALL = 0;
	public static final byte TIME_ZONE_MORNING = 1;
	public static final byte TIME_ZONE_NOON = 2;
	public static final byte TIME_ZONE_AFTERNOON = 3;
	public static final byte TIME_ZONE_NIGHT = 4;
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	public String entityId;
	public byte entityKind;
	public String screenName;
	public String elementId;
	public String elementName;
	public String elementSubId;
	public String elementSubName;
	public String shoppingId;
	public String shoppingName;
	
	@Deprecated
	@NotPersistent
	public transient String city;
	
	@Deprecated
	@NotPersistent
	public transient String province;
	
	@Deprecated
	@NotPersistent
	public transient String country;
	
	public String subentityId;
	public String subentityName;
	
	@Deprecated
	@NotPersistent
	public transient String periodType; // D: Daily, W: Weekly
	
	public Date date; 
	public String stringDate; // yyyy-MM-dd
	
	@Deprecated
	@NotPersistent
	public transient String movieId;
	
	@Deprecated
	@NotPersistent
	public transient String movieName;
	
	@Deprecated
	@NotPersistent
	public transient String voucherType;
	
	public byte dayOfWeek;
	public byte timeZone;
	public String stringValue;
	public Double doubleValue;
	public int recordCount;
	
	public Date creationDateTime;
	public Date lastUpdate;
	
	public DashboardIndicatorData() {
		this.creationDateTime = new Date();
		this.doubleValue = 0D;
		this.recordCount = 0;
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
	 * @return the entityId
	 */
	public String getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the entityKind
	 */
	public byte getEntityKind() {
		return entityKind;
	}

	/**
	 * @param entityKind the entityKind to set
	 */
	public void setEntityKind(byte entityKind) {
		this.entityKind = entityKind;
	}

	/**
	 * @return the screenName
	 */
	public String getScreenName() {
		return screenName;
	}

	/**
	 * @param screenName the screenName to set
	 */
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	/**
	 * @return the elementId
	 */
	public String getElementId() {
		return elementId;
	}

	/**
	 * @param elementId the elementId to set
	 */
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	/**
	 * @return the elementName
	 */
	public String getElementName() {
		return elementName;
	}

	/**
	 * @param elementName the elementName to set
	 */
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	/**
	 * @return the elementSubId
	 */
	public String getElementSubId() {
		return elementSubId;
	}

	/**
	 * @param elementSubId the elementSubId to set
	 */
	public void setElementSubId(String elementSubId) {
		this.elementSubId = elementSubId;
	}

	/**
	 * @return the elementSubName
	 */
	public String getElementSubName() {
		return elementSubName;
	}

	/**
	 * @param elementSubName the elementSubName to set
	 */
	public void setElementSubName(String elementSubName) {
		this.elementSubName = elementSubName;
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
	 * @return the subentityId
	 */
	public String getSubentityId() {
		return subentityId;
	}

	/**
	 * @param subentityId the subentityId to set
	 */
	public void setSubentityId(String subentityId) {
		this.subentityId = subentityId;
	}

	/**
	 * @return the subentityName
	 */
	public String getSubentityName() {
		return subentityName;
	}

	/**
	 * @param subentityName the subentityName to set
	 */
	public void setSubentityName(String subentityName) {
		this.subentityName = subentityName;
	}

	/**
	 * @return the periodType
	 */
	public String getPeriodType() {
		return periodType;
	}

	/**
	 * @param periodType the periodType to set
	 */
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the stringDate
	 */
	public String getStringDate() {
		return stringDate;
	}

	/**
	 * @param stringDate the stringDate to set
	 */
	public void setStringDate(String stringDate) {
		this.stringDate = stringDate;
	}

	/**
	 * @return the movieId
	 */
	public String getMovieId() {
		return movieId;
	}

	/**
	 * @param movieId the movieId to set
	 */
	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}

	/**
	 * @return the voucherType
	 */
	public String getVoucherType() {
		return voucherType;
	}

	/**
	 * @param voucherType the voucherType to set
	 */
	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	/**
	 * @return the dayOfWeek
	 */
	public byte getDayOfWeek() {
		return dayOfWeek;
	}

	/**
	 * @param dayOfWeek the dayOfWeek to set
	 */
	public void setDayOfWeek(byte dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	/**
	 * @return the timeZone
	 */
	public byte getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(byte timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the stringValue
	 */
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * @param stringValue the stringValue to set
	 */
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	/**
	 * @return the doubleValue
	 */
	public Double getDoubleValue() {
		return doubleValue;
	}

	/**
	 * @param doubleValue the doubleValue to set
	 */
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
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
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
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
	 * @return the movieName
	 */
	public String getMovieName() {
		return movieName;
	}

	/**
	 * @param movieName the movieName to set
	 */
	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}
	
	/**
	 * @return the recordCount
	 */
	public int getRecordCount() {
		return recordCount;
	}

	/**
	 * @param recordCount the recordCount to set
	 */
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	/* (non-Javadoc) 		------------->		YOU DONT SAY
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 7919;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((elementId == null) ? 0 : elementId.hashCode());
		result = prime * result
				+ ((elementSubId == null) ? 0 : elementSubId.hashCode());
		result = prime * result
				+ ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result + entityKind;
		result = prime * result
				+ ((shoppingId == null) ? 0 : shoppingId.hashCode());
		result = prime * result
				+ ((stringDate == null) ? 0 : stringDate.hashCode());
		result = prime * result
				+ ((subentityId == null) ? 0 : subentityId.hashCode());
		result = prime * result + timeZone;
		result = prime * result
				+ ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((screenName == null) ? 0 : screenName.hashCode());
		result = prime * result
				+ ((elementName == null) ? 0 : elementName.hashCode());
		result = prime * result
				+ ((elementSubName == null) ? 0 : elementSubName.hashCode());
		result = prime * result
				+ ((shoppingName == null) ? 0 : shoppingName.hashCode());
		result = prime * result
				+ ((subentityName == null) ? 0 : subentityName.hashCode());
		result = prime * result + dayOfWeek;
		result = prime * result + timeZone;
		result = prime * result
				+ ((stringValue == null) ? 0 : stringValue.hashCode());
		result = prime * result
				+ ((doubleValue == null) ? 0 : doubleValue.hashCode());
		result = prime * result + recordCount;
		result = prime * result
				+ ((creationDateTime == null) ? 0 : creationDateTime.hashCode());
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
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
		DashboardIndicatorData other = (DashboardIndicatorData) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		if (elementSubId == null) {
			if (other.elementSubId != null)
				return false;
		} else if (!elementSubId.equals(other.elementSubId))
			return false;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		} else if (!entityId.equals(other.entityId))
			return false;
		if (entityKind  != other.entityKind)
			return false;
		if (shoppingId == null) {
			if (other.shoppingId != null)
				return false;
		} else if (!shoppingId.equals(other.shoppingId))
			return false;
		if (stringDate == null) {
			if (other.stringDate != null)
				return false;
		} else if (!stringDate.equals(other.stringDate))
			return false;
		if (subentityId == null) {
			if (other.subentityId != null)
				return false;
		} else if (!subentityId.equals(other.subentityId))
			return false;
		if (timeZone != other.timeZone)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DashboardIndicatorData [key=" + key + ", entityId=" + entityId + ", entityKind=" + entityKind
				+ ", screenName=" + screenName + ", elementId=" + elementId + ", elementName=" + elementName
				+ ", elementSubId=" + elementSubId + ", elementSubName=" + elementSubName + ", shoppingId=" + shoppingId
				+ ", shoppingName=" + shoppingName + ", city=" + city + ", province=" + province + ", country="
				+ country + ", subentityId=" + subentityId + ", subentityName=" + subentityName + ", periodType="
				+ periodType + ", date=" + date + ", stringDate=" + stringDate + ", movieId=" + movieId + ", movieName="
				+ movieName + ", voucherType=" + voucherType + ", dayOfWeek=" + dayOfWeek + ", timeZone=" + timeZone
				+ ", stringValue=" + stringValue + ", doubleValue=" + doubleValue + ", recordCount=" + recordCount
				+ ", creationDateTime=" + creationDateTime + ", lastUpdate=" + lastUpdate + "]";
	}

}
