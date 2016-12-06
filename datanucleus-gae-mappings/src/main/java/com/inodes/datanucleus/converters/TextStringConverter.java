package com.inodes.datanucleus.converters;

import org.datanucleus.store.types.converters.TypeConverter;

import com.inodes.datanucleus.model.Text;

public class TextStringConverter implements TypeConverter<Text, String> {

	private static final long serialVersionUID = -4072427937202757477L;

	@Override
	public String toDatastoreType(Text memberValue) {
		return memberValue.getValue();
	}

	@Override
	public Text toMemberType(String datastoreValue) {
		return new Text(datastoreValue);
	}

}
