package com.inodes.datanucleus.model;

import java.io.Serializable;

class AppIdNamespace implements Serializable, Comparable<Object> {
	/**
	 * Public Version UID
	 */
	private static final long serialVersionUID = -5935482659516133193L;

	/**
	 * GAE Application ID
	 */
	private final String appId;
	
	/**
	 * GAE Namespace
	 */
	private final String namespace;

	/**
	 * Default Constructor
	 * 
	 * @param appId
	 *            The GAE Application ID
	 * @param namespace
	 *            The GAE Namespace
	 */
	public AppIdNamespace(String appId, String namespace) {
		
		if (appId == null || namespace == null) {
			throw new IllegalArgumentException("appId or namespace may not be null");
		}
		
		if (appId.indexOf('!') != -1 || namespace.indexOf('!') != -1) {
			throw new IllegalArgumentException("appId or namespace cannot contain '!'");
		} else {
			this.appId = appId;
			this.namespace = namespace;
			return;
		}
	}

	/**
	 * Parses a new AppIdNamespace object from an encoded string
	 * 
	 * @param encodedAppIdNamespace
	 *            The encoded string to parse
	 * @return A new created AppIdNamespace Object
	 */
	public static AppIdNamespace parseEncodedAppIdNamespace(String encodedAppIdNamespace) {
		
		if (encodedAppIdNamespace == null) {
			throw new IllegalArgumentException("appIdNamespaceString may not be null");
		}
		
		int index = encodedAppIdNamespace.indexOf('!');
		if (index == -1) return new AppIdNamespace(encodedAppIdNamespace, "");
		String appId = encodedAppIdNamespace.substring(0, index);
		String namespace = encodedAppIdNamespace.substring(index + 1);
		if (namespace.length() == 0) {
			throw new IllegalArgumentException("encodedAppIdNamespace with empty namespace may not contain a '!'");
		} else {
			return new AppIdNamespace(appId, namespace);
		}
	}

	/**
	 * Comparator
	 * 
	 * @param The
	 *            other object to compare
	 * @return
	 */
	public int compareTo(AppIdNamespace other) {
		int appidCompare = appId.compareTo(other.appId);
		if (appidCompare == 0)
			return namespace.compareTo(other.namespace);
		else
			return appidCompare;
	}

	/**
	 * Comparator
	 * 
	 * @param The
	 *            other object to compare
	 * @return
	 */
	public int compareTo(Object x0) {
		return compareTo((AppIdNamespace) x0);
	}

	/**
	 * @return The Application ID
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @return The Namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Encodes this object as a string representation
	 * 
	 * @return A String representing this AppIdNamespace object
	 */
	public String toEncodedString() {
		if (namespace.isEmpty())
			return appId;
		else
			return (new StringBuilder()).append(appId).append('!').append(namespace).toString();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + (appId != null ? appId.hashCode() : 0);
		result = prime * result + (namespace != null ? namespace.hashCode() : 0);
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AppIdNamespace other = (AppIdNamespace) obj;
		if (appId == null) {
			if (other.appId != null)
				return false;
		} else if (!appId.equals(other.appId))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		return true;
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return toEncodedString();
	}

}
