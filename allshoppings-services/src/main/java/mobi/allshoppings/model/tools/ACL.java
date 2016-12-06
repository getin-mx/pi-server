package mobi.allshoppings.model.tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable(detachable="true")
@EmbeddedOnly

public class ACL implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int PERMISSION_READ = 1;
	public static final int PERMISSION_WRITE = 2;
	
	private String owner;
	private List<String> userIds;
	private Map<String, ACLEntry> entries;
	
	public ACL() {
		super();
		this.userIds = new ArrayList<String>();
		this.entries = new HashMap<String, ACLEntry>();
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
	 * @return the entries
	 */
	public Map<String, ACLEntry> getEntries() {
		return entries;
	}

	/**
	 * @param entries the entries to set
	 */
	public void setEntries(Map<String, ACLEntry> entries) {
		this.entries = entries;
	}

	/**
	 * Sets a permission for a user
	 * 
	 * @param userId
	 *            User id to set the permission
	 * @param read
	 *            Read permission type
	 * @param write
	 *            Write permission type
	 */
	public void setPermission(String userId, boolean read, boolean write) {
		ACLEntry acle = entries.get(userId);
		if( acle == null ) acle = new ACLEntry();
		acle.setUserId(userId);
		acle.setRead(read);
		acle.setWrite(write);
		entries.put(userId, acle);
		if( !userIds.contains(userId) ) userIds.add(userId);
	}

	/**
	 * Removes permission for a user
	 * 
	 * @param userId
	 *            User id to remove permissions from
	 */
	public void removePermission(String userId) {
		entries.remove(userId);
		userIds.remove(userId);
	}
	
	/**
	 * Removes all permissions
	 */
	public void clearPermissions() {
		entries.clear();
		userIds.clear();
	}
	
	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * Checks a given permission for a user
	 * 
	 * @param userId
	 *            The user to check
	 * @param permission
	 *            The permission to Check. It must be PERMISSION_READ or
	 *            PERMISSION_WRITE
	 * 
	 * @return True if the user has correct access to this object and permission
	 *         type. False if not
	 */
	public boolean checkPermission(String userId, int permission) {
		ACLEntry acle = entries.get(userId);
		if( acle == null ) return false;
		if( permission == PERMISSION_READ ) return acle.isRead();
		if( permission == PERMISSION_WRITE ) return acle.isWrite();
		return false;
	}
	
	/**
	 * Class that defines the particular permissions
	 * @author mhapanowicz
	 *
	 */
	@SuppressWarnings("serial")
	public class ACLEntry implements Serializable {
		private String userId;
		private boolean read = false;
		private boolean write = false;
		
		public ACLEntry() {
			super();
		}
		
		public ACLEntry(String userId, boolean read, boolean write) {
			super();
			this.userId = userId;
			this.read = read;
			this.write = write;
		}

		/**
		 * @return the userId
		 */
		public String getUserId() {
			return userId;
		}

		/**
		 * @param userId the userId to set
		 */
		public void setUserId(String userId) {
			this.userId = userId;
		}

		/**
		 * @return the read
		 */
		public boolean isRead() {
			return read;
		}

		/**
		 * @param read the read to set
		 */
		public void setRead(boolean read) {
			this.read = read;
		}

		/**
		 * @return the write
		 */
		public boolean isWrite() {
			return write;
		}

		/**
		 * @param write the write to set
		 */
		public void setWrite(boolean write) {
			this.write = write;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (read ? 1231 : 1237);
			result = prime * result
					+ ((userId == null) ? 0 : userId.hashCode());
			result = prime * result + (write ? 1231 : 1237);
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
			ACLEntry other = (ACLEntry) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (read != other.read)
				return false;
			if (userId == null) {
				if (other.userId != null)
					return false;
			} else if (!userId.equals(other.userId))
				return false;
			if (write != other.write)
				return false;
			return true;
		}

		private ACL getOuterType() {
			return ACL.this;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ACLEntry [userId=" + userId + ", read=" + read + ", write="
					+ write + "]";
		}
		
	}
}
