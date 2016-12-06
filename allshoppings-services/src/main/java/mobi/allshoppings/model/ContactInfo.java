package mobi.allshoppings.model;

import java.io.Serializable;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.inodes.datanucleus.model.Email;

/**
 * This class represents contact info for an entity
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
@EmbeddedOnly
public final class ContactInfo implements Serializable {

    @Persistent
    /**
     * Contact's first name
     */
    private String firstname;

    @Persistent
    /**
     * Contact's last name
     */
    private String lastname;
    
    @Persistent
    /**
     * Contact phone number
     */
    private String phone;
    
    @Persistent
    /**
     * Contact mobile phone number
     */
    private String mobile;
    
    @Persistent
    /**
     * Contact email
     */
    private Email mail;
    
    @Persistent
    /**
     * Facebook Id
     */
    private String facebookId;

    @Persistent
    /**
     * Twitter Id
     */
    private String twitterId;
    
    @Persistent
    /**
     * Google Id
     */
    private String googleId;
    
    @Persistent
    /**
     * Foursquare Id
     */
    private String foursquareId;
    
    @Persistent
    /**
     * Tiendeo Id
     */
    private String tiendeoId;
    
    @Persistent
    /**
     * Contact's web page
     */
    private String webURL;

    @Persistent
    /**
     * Contact's youtube channel
     */
    private String youtubeChannel;

    /**
     * Default constructor
     */
    public ContactInfo() {
    	super();
    }

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the mail
	 */
	public Email getMail() {
		return mail;
	}

	/**
	 * @param mail the mail to set
	 */
	public void setMail(Email mail) {
		this.mail = mail;
	}

	/**
	 * @return the facebookId
	 */
	public String getFacebookId() {
		return facebookId;
	}

	/**
	 * @param facebookId the facebookId to set
	 */
	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	/**
	 * @return the twitterId
	 */
	public String getTwitterId() {
		return twitterId;
	}

	/**
	 * @param twitterId the twitterId to set
	 */
	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}

	/**
	 * @return the googleId
	 */
	public String getGoogleId() {
		return googleId;
	}

	/**
	 * @param googleId the googleId to set
	 */
	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	/**
	 * @return the webURL
	 */
	public String getWebURL() {
		return webURL;
	}

	/**
	 * @param webURL the webURL to set
	 */
	public void setWebURL(String webURL) {
		this.webURL = webURL;
	}

	/**
	 * @return the youtubeChannel
	 */
	public String getYoutubeChannel() {
		return youtubeChannel;
	}

	/**
	 * @param youtubeChannel the youtubeChannel to set
	 */
	public void setYoutubeChannel(String youtubeChannel) {
		this.youtubeChannel = youtubeChannel;
	}

	/**
	 * @return the foursquareId
	 */
	public String getFoursquareId() {
		return foursquareId;
	}

	/**
	 * @param foursquareId the foursquareId to set
	 */
	public void setFoursquareId(String foursquareId) {
		this.foursquareId = foursquareId;
	}

	/**
	 * @return the tiendeoId
	 */
	public String getTiendeoId() {
		return tiendeoId;
	}

	/**
	 * @param tiendeoId the tiendeoId to set
	 */
	public void setTiendeoId(String tiendeoId) {
		this.tiendeoId = tiendeoId;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((facebookId == null) ? 0 : facebookId.hashCode());
		result = prime * result
				+ ((firstname == null) ? 0 : firstname.hashCode());
		result = prime * result
				+ ((googleId == null) ? 0 : googleId.hashCode());
		result = prime * result
				+ ((lastname == null) ? 0 : lastname.hashCode());
		result = prime * result + ((mail == null) ? 0 : mail.hashCode());
		result = prime * result + ((mobile == null) ? 0 : mobile.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result
				+ ((twitterId == null) ? 0 : twitterId.hashCode());
		result = prime * result + ((webURL == null) ? 0 : webURL.hashCode());
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
		ContactInfo other = (ContactInfo) obj;
		if (facebookId == null) {
			if (other.facebookId != null)
				return false;
		} else if (!facebookId.equals(other.facebookId))
			return false;
		if (firstname == null) {
			if (other.firstname != null)
				return false;
		} else if (!firstname.equals(other.firstname))
			return false;
		if (googleId == null) {
			if (other.googleId != null)
				return false;
		} else if (!googleId.equals(other.googleId))
			return false;
		if (lastname == null) {
			if (other.lastname != null)
				return false;
		} else if (!lastname.equals(other.lastname))
			return false;
		if (mail == null) {
			if (other.mail != null)
				return false;
		} else if (!mail.equals(other.mail))
			return false;
		if (mobile == null) {
			if (other.mobile != null)
				return false;
		} else if (!mobile.equals(other.mobile))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (twitterId == null) {
			if (other.twitterId != null)
				return false;
		} else if (!twitterId.equals(other.twitterId))
			return false;
		if (webURL == null) {
			if (other.webURL != null)
				return false;
		} else if (!webURL.equals(other.webURL))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ContactInfo [firstname=" + firstname + ", lastname=" + lastname
				+ ", mail=" + mail + "]";
	}

    
}
