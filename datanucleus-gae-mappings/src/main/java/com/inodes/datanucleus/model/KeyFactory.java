package com.inodes.datanucleus.model;

public class KeyFactory {

	public static final class Builder {

		private Key current;

		public Builder addChild(String kind, String name) {
			current = KeyFactory.createKey(current, kind, name);
			return this;
		}

		public Builder addChild(String kind, long id) {
			current = KeyFactory.createKey(current, kind, id);
			return this;
		}

		public Key getKey() {
			return current;
		}

		public String getString() {
			return KeyFactory.keyToString(current);
		}

		public Builder(String kind, String name) {
			current = KeyFactory.createKey(null, kind, name);
		}

		public Builder(String kind, long id) {
			current = KeyFactory.createKey(null, kind, id);
		}

		public Builder(Key key) {
			current = key;
		}
	}

	public static Key createKey(String kind, long id) {
		return createKey(null, kind, id);
	}

	public static Key createKey(Key parent, String kind, long id) {
		return createKey(parent, kind, id, null);
	}

	static Key createKey(Key parent, String kind, long id,
			AppIdNamespace appIdNamespace) {
		if (id == 0L)
			throw new IllegalArgumentException("id cannot be zero");
		else
			return new Key(kind, parent, id, appIdNamespace);
	}

	public static Key createKey(String kind, String name) {
		return createKey(null, kind, name);
	}

	public static Key createKey(Key parent, String kind, String name) {
		return createKey(parent, kind, name, null);
	}

	static Key createKey(Key parent, String kind, String name,
			AppIdNamespace appIdNamespace) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("name cannot be null or empty");
		else
			return new Key(kind, parent, name, appIdNamespace);
	}

	public static String createKeyString(String kind, long id) {
		return keyToString(createKey(kind, id));
	}

	public static String createKeyString(Key parent, String kind, long id) {
		return keyToString(createKey(parent, kind, id));
	}

	public static String createKeyString(String kind, String name) {
		return keyToString(createKey(kind, name));
	}

	public static String createKeyString(Key parent, String kind, String name) {
		return keyToString(createKey(parent, kind, name));
	}

	public static String keyToString(Key key) {
		if (!key.isComplete()) {
			throw new IllegalArgumentException("Key is incomplete.");
		} else {
			return key.toString();
		}
	}

	public static Key stringToKey(String encoded) {
		return Key.fromString(encoded);
	}

}
