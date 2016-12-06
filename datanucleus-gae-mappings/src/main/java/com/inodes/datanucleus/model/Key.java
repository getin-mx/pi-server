package com.inodes.datanucleus.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

public final class Key implements Serializable, Comparable<Object> {

	static final long serialVersionUID = -448150158203091507L;

	/**
	 * Default Application ID
	 */
	private static final String DEFAULT_APPID = "defaultAppId";
	
	/**
	 * Default Namespace
	 */
	private static final String DEFAULT_NAMESPACE = ""; 

	/**
	 * Holder for not assigned numeric IDs
	 */
	static final long NOT_ASSIGNED = 0L;
	
	
	private Key parentKey;
	private String kind;
	private String appId;
	private long id;
	private String name;
	private transient AppIdNamespace appIdNamespace;

	/**
	 * Constructor Groups
	 */
	public Key() {
		this.parentKey = null;
		this.kind = null;
		this.appId = DEFAULT_APPID;
		this.id = NOT_ASSIGNED;
		this.name = null;
		this.appIdNamespace = new AppIdNamespace(DEFAULT_APPID, DEFAULT_NAMESPACE);
	}
	
	public Key(String kind) {
		this(kind, null, NOT_ASSIGNED);
	}

	public Key(String kind, String name) {
		this(kind, null, name);
	}

	public Key(String kind, Key parentKey) {
		this(kind, parentKey, NOT_ASSIGNED);
	}

	public Key(String kind, Key parentKey, long id) {
		this(kind, parentKey, id, null);
	}

	public Key(String kind, Key parentKey, long id, AppIdNamespace appIdNamespace) {
		this(kind, parentKey, id, null, appIdNamespace);
	}

	public Key(String kind, Key parentKey, String name) {
		this(kind, parentKey, name, null);
	}

	public Key(String kind, Key parentKey, String name, AppIdNamespace appIdNamespace) {
		this(kind, parentKey, NOT_ASSIGNED, name, appIdNamespace);
	}

	/**
	 * Full Constructor
	 * @param kind The entity kind
	 * @param parentKey The key parent
	 * @param id A numeric ID
	 * @param name A String ID
	 * @param appIdNamespace Application ID and Namespace
	 */
	public Key(String kind, Key parentKey, long id, String name, AppIdNamespace appIdNamespace) {

		if (kind == null || kind.length() == 0) {
			throw new IllegalArgumentException("No kind specified.");
		}

		if (appIdNamespace == null) {
			if (parentKey == null) {
				appIdNamespace = new AppIdNamespace(DEFAULT_APPID, DEFAULT_NAMESPACE);
			} else {
				appIdNamespace = parentKey.getAppIdNamespace();
			}
		}
		
		validateAppIdNamespace(parentKey, appIdNamespace);
		
		if (name != null) {
			if (name.length() == 0) {
				throw new IllegalArgumentException("Name may not be empty.");
			}
			
			if (id != NOT_ASSIGNED) {
				throw new IllegalArgumentException("Id and name may not both be specified at once.");
			}
		}
		
		this.id = id;
		this.parentKey = parentKey;
		this.name = name == null ? null : new String(name);
		this.kind = kind == null ? null : new String(kind);
		this.appIdNamespace = appIdNamespace;

		if (isValidKey(kind)) parse(kind);

	}

	/**
	 * Builds a key from its string representation
	 * 
	 * @param keyrep
	 *            The key string representation
	 * @return A fully functional Key
	 */
	public static Key fromString(String keyrep) {
		Key ret = new Key();
		ret.parse(keyrep);
		return ret;
	}
	
	/**
	 * Builds a key from its string representation
	 * 
	 * @param keyrep
	 *            The key string representation
	 * @return A fully functional Key
	 */
	private void parse(String keyrep) {
		String[] parts = keyrep.split("\\(");
		parts[1] = parts[1].replaceAll("\\)", " ");
		parts[1] = parts[1].replace("\"", " ");
		parts[1] = parts[1].trim();
		this.kind = parts[0];
		this.name = parts[1];
	}

	public static boolean isValidKey(String key) {
		if( key.matches("^[a-zA-Z0-9_\\-\\@\\.]*\\(\"[a-zA-Z0-9_\\-\\@\\.]*\"\\)$")) return true;
		else return false;
	}
	
	/**
	 * Validates a correct parent key and application namespace
	 * @param parentKey The parent Key to validate
	 * @param appIdNamespace the AppIdNamespace to validate
	 */
	private static void validateAppIdNamespace(Key parentKey, AppIdNamespace appIdNamespace) {
		if (parentKey != null && appIdNamespace != null
				&& parentKey.getAppIdNamespace() != null
				&& !parentKey.getAppIdNamespace().equals(appIdNamespace)) {
			throw new IllegalArgumentException("Parent key must have same app id and namespace as child.");
		} else {
			return;
		}
	}

	/**
	 * Writes the key object to an output stream
	 * 
	 * @param out
	 *            The output stream to write
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		if (appIdNamespace != null)
			appId = appIdNamespace.toEncodedString();
		out.defaultWriteObject();
	}

	/**
	 * Reads the key object from an input stream
	 * 
	 * @param in
	 *            The input stream to read
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if (appId != null) {
			appIdNamespace = AppIdNamespace.parseEncodedAppIdNamespace(appId);
			appId = null;
		} else {
			appIdNamespace = new AppIdNamespace(DEFAULT_APPID, DEFAULT_NAMESPACE);
		}
		validateAppIdNamespace(parentKey, appIdNamespace);
	}

	/**
	 * @return The Kind
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * @return the Key Parent
	 */
	public Key getParent() {
		return parentKey;
	}

	/**
	 * @return the first (root) key
	 */
	Key getRootKey() {
		Key curKey;
		for (curKey = this; curKey.getParent() != null; curKey = curKey.getParent());
		return curKey;
	}

	/**
	 * @return The AppIDNamespace
	 */
	AppIdNamespace getAppIdNamespace() {
		return appIdNamespace;
	}

	/**
	 * @return The Application ID
	 */
	public String getAppId() {
		return appIdNamespace.getAppId();
	}

	/**
	 * @return The GAE Namespace
	 */
	public String getNamespace() {
		return appIdNamespace.getNamespace();
	}

	/**
	 * @return The numeric ID
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return The String ID
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result	+ (appIdNamespace != null ? appIdNamespace.hashCode() : 0);
		result = prime * result + (int) (id ^ id >>> 32);
		result = prime * result + (kind != null ? kind.hashCode() : 0);
		result = prime * result + (name != null ? name.hashCode() : 0);
		result = prime * result + (parentKey != null ? parentKey.hashCode() : 0);
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) {
		return equals(object, true);
	}

	/**
	 * A modified version of the {@link Object#equals(Object)}
	 * 
	 * @param object
	 *            The object to compare
	 * @param considerNotAssigned
	 *            Boolean value that represents if we have to consider a not
	 *            assigned key to the comparison
	 * @return @see {@link Object#equals(Object)}
	 */
	boolean equals(Object object, boolean considerNotAssigned) {
		if (object instanceof Key) {
			Key key = (Key) object;
			if (this == key)
				return true;
			if (!appIdNamespace.equals(key.appIdNamespace))
				return false;
			if (considerNotAssigned && name == null && id == NOT_ASSIGNED && key.id == NOT_ASSIGNED)
				return false;
			if (id != key.id || !kind.equals(key.kind)
					|| !Objects.equals(name, key.name))
				return false;
			return parentKey == key.parentKey || parentKey != null
					&& parentKey.equals(key.parentKey, considerNotAssigned);
		} else {
			return false;
		}
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		appendToString(buffer);
		return buffer.toString();
	}

	/**
	 * Builds the key string representation into a StringBuffer
	 * 
	 * @param buffer
	 *            The StringBuffer object to be completed
	 */
	private void appendToString(StringBuffer buffer) {
		if (parentKey != null) {
			parentKey.appendToString(buffer);
			buffer.append("/");
		} else if (appIdNamespace != null) {
			String namespace = appIdNamespace.getNamespace();
			if (namespace.length() > 0) {
				buffer.append("!");
				buffer.append(namespace);
				buffer.append(":");
			}
		}
		buffer.append(kind);
		buffer.append("(");
		if (name != null) {
			buffer.append((new StringBuilder()).append("\"").append(name)
					.append("\"").toString());
		} else if (id == NOT_ASSIGNED) {
			buffer.append("no-id-yet");
		} else {
			buffer.append(String.valueOf(id));
		}
		buffer.append(")");
	}

	/**
	 * Creates a new child key
	 * 
	 * @param kind
	 *            Entity kind
	 * @param id
	 *            Entity ID
	 * @return A fully functional sibling key that
	 */
	public Key getChild(String kind, long id) {
		if (!isComplete())
			throw new IllegalStateException(
					"Cannot get a child of an incomplete key.");
		else
			return new Key(kind, this, id);
	}

	/**
	 * Creates a new child key
	 * 
	 * @param kind
	 *            Entity kind
	 * @param name
	 *            Entity ID
	 * @return A fully functional sibling key that
	 */
	public Key getChild(String kind, String name) {
		if (!isComplete())
			throw new IllegalStateException(
					"Cannot get a child of an incomplete key.");
		else
			return new Key(kind, this, name);
	}

	/**
	 * Checks if this key is completed (it means, that has an ID or a Name)
	 * 
	 * @return True if complete, False if not
	 */
	public boolean isComplete() {
		return id != NOT_ASSIGNED || name != null;
	}

	/**
	 * 
	 * @param id
	 */
	void setId(long id) {
		if (name != null) {
			throw new IllegalArgumentException("Cannot set id; key already has a name.");
		} else {
			this.id = id;
			return;
		}
	}

	void simulatePutForTesting(long testId) {
		id = testId;
	}

	private static Iterator<Key> getPathIterator(Key key) {
		LinkedList<Key> stack = new LinkedList<Key>();
		do {
			stack.addFirst(key);
			key = key.getParent();
		} while (key != null);
		return stack.iterator();
	}

	/**
	 * Sort Comparator
	 * 
	 * @param other
	 *            The other key to be compared
	 * @return
	 */
	public int compareTo(Key other) {
		if (this == other)
			return 0;
		Iterator<Key> thisPath = getPathIterator(this);
		Iterator<Key> otherPath = getPathIterator(other);
		while (thisPath.hasNext()) {
			Key thisKey = (Key) thisPath.next();
			if (otherPath.hasNext()) {
				Key otherKey = (Key) otherPath.next();
				int result = compareToInternal(thisKey, otherKey);
				if (result != 0)
					return result;
			} else {
				return 1;
			}
		}
		return otherPath.hasNext() ? -1 : 0;
	}

	/**
	 * Sort Comparator
	 * 
	 * @param x0
	 *            The other key to be compared
	 * @return
	 */
	public int compareTo(Object x0) {
		return compareTo((Key) x0);
	}

	/**
	 * Internal sort comparator
	 * 
	 * @param thisKey
	 *            The first key to be compared
	 * @param otherKey
	 *            The second key to be compared
	 * @return
	 */
	private static int compareToInternal(Key thisKey, Key otherKey) {
		if (thisKey == otherKey)
			return 0;
		int result = thisKey.getAppIdNamespace().compareTo(
				otherKey.getAppIdNamespace());
		if (result != 0)
			return result;
		result = thisKey.getKind().compareTo(otherKey.getKind());
		if (result != 0)
			return result;
		if (!thisKey.isComplete() && !otherKey.isComplete())
			return compareToWithIdentityHash(thisKey, otherKey);
		if (thisKey.getId() != NOT_ASSIGNED)
			if (otherKey.getId() == NOT_ASSIGNED)
				return -1;
			else
				return Long.compare(thisKey.getId(), otherKey.getId());
		if (otherKey.getId() != NOT_ASSIGNED)
			return 1;
		else
			return thisKey.getName().compareTo(otherKey.getName());
	}

	/**
	 * Compares two keys using Identity Hash Code
	 * 
	 * @param k1
	 *            The first key to be compared
	 * @param k2
	 *            The second key to be compared
	 * @return
	 */
	static int compareToWithIdentityHash(Key k1, Key k2) {
		return Integer.compare(System.identityHashCode(k1),System.identityHashCode(k2));
	}

}
