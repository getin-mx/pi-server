package mobi.allshoppings.model.embedded;

import java.io.Serializable;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * This class represents a geographical located address
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
@EmbeddedOnly
public class MovieEmbedd implements Serializable {
	
	@Persistent
	/**
	 * Public cinema identifier
	 */
    private String identifier;
    
	@Persistent
	/**
	 * Internal cinema identifier
	 */
	private String internalIdentifier;
	
	@Persistent
    /**
     * cinema name
     */
    private String name;

	/**
	 * 
	 */
	public MovieEmbedd() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param identifier
	 * @param name
	 */
	public MovieEmbedd(String identifier, String name, String internalIdentifier) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.internalIdentifier = internalIdentifier;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
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
		MovieEmbedd other = (MovieEmbedd) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MovieEmbedd [identifier=" + identifier
				+ ", internalIdentifier=" + internalIdentifier + ", name="
				+ name + "]";
	}

    
}
