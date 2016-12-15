package mobi.allshoppings.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.inodes.datanucleus.model.Blob;
import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;

import mobi.allshoppings.model.interfaces.Identificable;
import mobi.allshoppings.model.interfaces.ModelKey;

@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
public class Dummy implements ModelKey, Serializable, Identificable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.UNSPECIFIED)
    private Key key;

	private String name;
	private Text text;
	private Email email;
	private Blob blob;
	private Date creationDateTime;
	private Date lastUpdate;

	@Persistent(defaultFetchGroup = "true")
	private List<String> someArray;
	
	public Dummy() {
		this.creationDateTime = new Date();
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
	 * @return the text
	 */
	public Text getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(Text text) {
		this.text = text;
	}

	/**
	 * @return the email
	 */
	public Email getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(Email email) {
		this.email = email;
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
	 * @return the blob
	 */
	public Blob getBlob() {
		return blob;
	}

	/**
	 * @return the someArray
	 */
	public List<String> getSomeArray() {
		return someArray;
	}

	/**
	 * @param someArray the someArray to set
	 */
	public void setSomeArray(List<String> someArray) {
		this.someArray = someArray;
	}

	/**
	 * @param blob the blob to set
	 */
	public void setBlob(Blob blob) {
		this.blob = blob;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		Dummy other = (Dummy) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Dummy [key=" + key + ", name=" + name + ", text=" + text
				+ ", email=" + email + ", blob=" + blob + ", creationDateTime="
				+ creationDateTime + ", lastUpdate=" + lastUpdate
				+ ", someArray=" + someArray + "]";
	}

}
