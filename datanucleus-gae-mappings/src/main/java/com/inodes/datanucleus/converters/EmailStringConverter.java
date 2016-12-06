package com.inodes.datanucleus.converters;

import org.datanucleus.store.types.converters.TypeConverter;

import com.inodes.datanucleus.model.Email;

public class EmailStringConverter implements TypeConverter<Email, String> {

	private static final long serialVersionUID = -4072427937202757477L;

	@Override
	public String toDatastoreType(Email memberValue) {
		return memberValue.getEmail();
	}

	@Override
	public Email toMemberType(String datastoreValue) {
		return new Email(datastoreValue);
	}

}
