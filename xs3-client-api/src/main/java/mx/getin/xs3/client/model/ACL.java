package mx.getin.xs3.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class ACL implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int PERMISSION_READ = 1;
	public static final int PERMISSION_WRITE = 2;
	
	private String owner;

	private Map<String, Integer> entries;
	
	public ACL() {
		super();
		this.entries = new HashMap<String, Integer>();
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
	 * @return the entries
	 */
	public Map<String, Integer> getEntries() {
		return entries;
	}

	/**
	 * @param entries the entries to set
	 */
	public void setEntries(Map<String, Integer> entries) {
		this.entries = entries;
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
		if( null != owner && owner.equals(userId)) return true;
		Integer val = entries.get(userId);
		if( val == null ) return false;
		if( val >= permission) return true;
			return false;
	}
}
