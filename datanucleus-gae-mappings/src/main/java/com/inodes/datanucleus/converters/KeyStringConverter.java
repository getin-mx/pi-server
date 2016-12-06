package com.inodes.datanucleus.converters;

import org.datanucleus.store.types.converters.TypeConverter;

import com.inodes.datanucleus.model.Key;

public class KeyStringConverter implements TypeConverter<Key, String> {

	private static final long serialVersionUID = -4072427937202757477L;

	@Override
	public String toDatastoreType(Key memberValue) {
		return memberValue.toString();
	}

	@Override
	public Key toMemberType(String datastoreValue) {
		return Key.fromString(datastoreValue);
	}

}
