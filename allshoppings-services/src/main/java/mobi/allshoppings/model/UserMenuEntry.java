package mobi.allshoppings.model;

import java.io.Serializable;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * This class represents contact info for an entity
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
@EmbeddedOnly
public final class UserMenuEntry implements Serializable {

    @Persistent
    /**
     * Angular SRef
     */
    private String sref;

    @Persistent
    /**
     * Menu icon
     */
    private String icon;
    
    @Persistent
    /**
     * Menu name
     */
    private String name;

    /**
     * Default Constructor
     */
	public UserMenuEntry() {
		super();
	}

	/**
	 * Full Field Constructor
	 * @param sref
	 * @param icon
	 * @param name
	 */
	public UserMenuEntry(String sref, String icon, String name) {
		super();
		this.sref = sref;
		this.icon = icon;
		this.name = name;
	}

	/**
	 * @return the sref
	 */
	public String getSref() {
		return sref;
	}

	/**
	 * @param sref the sref to set
	 */
	public void setSref(String sref) {
		this.sref = sref;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((icon == null) ? 0 : icon.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((sref == null) ? 0 : sref.hashCode());
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
		UserMenuEntry other = (UserMenuEntry) obj;
		if (icon == null) {
			if (other.icon != null)
				return false;
		} else if (!icon.equals(other.icon))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sref == null) {
			if (other.sref != null)
				return false;
		} else if (!sref.equals(other.sref))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserMenuEntry [sref=" + sref + ", icon=" + icon + ", name=" + name + "]";
	}
    
}
