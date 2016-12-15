package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.Indexable;
import mobi.allshoppings.model.interfaces.ModelKey;
import mobi.allshoppings.model.interfaces.Replicable;
import mobi.allshoppings.model.tools.ViewLocation;
import mobi.allshoppings.tools.Hex;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class User implements ModelKey, Serializable, Identificable, Indexable, Replicable {

	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_INACTIVE = 2;
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String gender;
	private Date birthDate;
	private String avatarId;
	private Date creationDateTime;
	private Date statusModificationDateTime;
	private Date lastUpdate;
	private Boolean receivePushMessages = true;
	private Boolean geoFenceEnabled = true;
	private Date lastLogin;
	private String trackingCode;
	private Long points;

	@Persistent(defaultFetchGroup = "true")
	@Embedded
	private ViewLocation viewLocation;
	
	@Persistent(defaultFetchGroup = "true")
	@Embedded
	private Address address;

	@Persistent(defaultFetchGroup = "true")
	@Embedded
	private ContactInfo contactInfo;

	@Persistent(defaultFetchGroup = "true")
    @Embedded
    private UserSecurity securitySettings;

	private Integer activityStatus;
	
	// Search fields ... this is too ugly... Fuck you Google!!!!
	@SuppressWarnings("unused")
	private String uIdentifier;
	@SuppressWarnings("unused")
	private String uFirstname;
	@SuppressWarnings("unused")
	private String uLastname;
	@SuppressWarnings("unused")
	private String uEmail;
	
	@NotPersistent
	private boolean doIndexNow = true;

    public User() {
		this.creationDateTime = new Date();
		this.securitySettings = new UserSecurity();
		this.contactInfo = new ContactInfo();
		this.address = new Address();
		this.viewLocation = new ViewLocation();
		this.activityStatus = STATUS_ACTIVE;
		this.points = 0L;
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
		
		// Creates a 16 bytes (almost) unique tracking code for this user
		byte[] bytes = getIdentifier().getBytes();
		Checksum checksum = new CRC32();
		checksum.update(bytes,0,bytes.length);
		setTrackingCode(Hex.fromLong(checksum.getValue()));
	}

	/**
	 * @return the viewLocation
	 */
	public ViewLocation getViewLocation() {
		return viewLocation;
	}

	/**
	 * @param viewLocation the viewLocation to set
	 */
	public void setViewLocation(ViewLocation viewLocation) {
		this.viewLocation = viewLocation;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstname() {
		return this.getContactInfo().getFirstname();
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstname(String firstname) {
		this.getContactInfo().setFirstname(firstname);
	}

	/**
	 * @return the lastName
	 */
	public String getLastname() {
		return this.getContactInfo().getLastname();
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastname(String lastname) {
		this.getContactInfo().setLastname(lastname);
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		if( gender == null || gender.equals("") || gender.equals("male") || gender.equals("female")) {
			this.gender = gender;
		}
	}

	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * @return the receivePushMessages
	 */
	public Boolean getReceivePushMessages() {
		return receivePushMessages;
	}

	/**
	 * @param receivePushMessages the receivePushMessages to set
	 */
	public void setReceivePushMessages(Boolean receivePushMessages) {
		this.receivePushMessages = receivePushMessages;
	}

	/**
	 * @return the geoFenceEnabled
	 */
	public Boolean getGeoFenceEnabled() {
		return geoFenceEnabled;
	}

	/**
	 * @param geoFenceEnabled the geoFenceEnabled to set
	 */
	public void setGeoFenceEnabled(Boolean geoFenceEnabled) {
		this.geoFenceEnabled = geoFenceEnabled;
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
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
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		try {
			this.contactInfo.setMail(new Email(email.toLowerCase()));
		} catch( Exception e ){}
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
	 * @return the securitySettings
	 */
	public UserSecurity getSecuritySettings() {
		return securitySettings;
	}

	/**
	 * @param securitySettings the securitySettings to set
	 */
	public void setSecuritySettings(UserSecurity securitySettings) {
		this.securitySettings = securitySettings;
	}

	/**
	 * @return this entity key
	 */
	public String getIdentifier() {
		return this.getKey() != null ? this.getKey().getName() : "";
	}

	/**
	 * @return The concatenation of users first and last names
	 */
	public String getFullname() {
		return new StringBuffer().append(getFirstname()).append(" ").append(getLastname()).toString().trim();
	}

	/**
	 * @return The concatenation of users last and first names
	 */
	public String getFullnameReverse() {
		return new StringBuffer().append(getLastname()).append(" ").append(getFirstname()).toString().trim();
	}

	/**
	 * @return The plain email address as a String, or a null value if not email address is set
	 */
	public String getEmail() {
		if( this.getContactInfo() == null ) return null;
		if( this.getContactInfo().getMail() == null ) return null;
		return this.getContactInfo().getMail().getEmail();
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
	 * @return the lastLogin
	 */
	public Date getLastLogin() {
		return lastLogin;
	}

	/**
	 * @param lastLogin the lastLogin to set
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	/**
	 * @return the trackingCode
	 */
	public String getTrackingCode() {
		return trackingCode;
	}

	/**
	 * @param trackingCode the trackingCode to set
	 */
	public void setTrackingCode(String trackingCode) {
		this.trackingCode = trackingCode;
	}

	/**
	 * Pre store information to assign index values
	 */
	@Override
	public void preStore() {
		uIdentifier = getIdentifier().toUpperCase();
		uFirstname = getFullname().toUpperCase();
		uLastname = getFullnameReverse().toUpperCase();
		uEmail = getContactInfo().getMail() != null ? getContactInfo().getMail().getEmail().toUpperCase() : null;
		this.lastUpdate = new Date();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [identifier=" + getIdentifier() + "]";
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
		User other = (User) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
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
	 * @return the activityStatus
	 */
	public Integer getActivityStatus() {
		return activityStatus;
	}

	/**
	 * @param activityStatus the activityStatus to set
	 */
	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
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
