package mx.getin.xs3.client.model;

import java.io.Serializable;
import java.util.Date;

public class XS3Bucket implements Serializable {

	private static final long serialVersionUID = 7574103021565363458L;

	private String identifier;
	private String name;
	private Date creationDateTime;
	private Date modificationDateTime;
	private Date accessDateTime;
	private Date lastUpdate;
	private long objectCount;
	private long size;

    private ACL acl;
	
    public XS3Bucket() {
		this.creationDateTime = new Date();
		this.modificationDateTime = new Date();
		this.accessDateTime = new Date();
		this.acl = new ACL();
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
	 * @return the modificationDateTime
	 */
	public Date getModificationDateTime() {
		return modificationDateTime;
	}

	/**
	 * @param modificationDateTime the modificationDateTime to set
	 */
	public void setModificationDateTime(Date modificationDateTime) {
		this.modificationDateTime = modificationDateTime;
	}

	/**
	 * @return the accessDateTime
	 */
	public Date getAccessDateTime() {
		return accessDateTime;
	}

	/**
	 * @param accessDateTime the accessDateTime to set
	 */
	public void setAccessDateTime(Date accessDateTime) {
		this.accessDateTime = accessDateTime;
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
	 * @return the objectCount
	 */
	public long getObjectCount() {
		return objectCount;
	}

	/**
	 * @param objectCount the objectCount to set
	 */
	public void setObjectCount(long objectCount) {
		this.objectCount = objectCount;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return the acl
	 */
	public ACL getAcl() {
		return acl;
	}

	/**
	 * @param acl the acl to set
	 */
	public void setAcl(ACL acl) {
		this.acl = acl;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
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
		XS3Bucket other = (XS3Bucket) obj;
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
		return "Bucket [identifier=" + identifier + ", name=" + name + "]";
	}
    
}
