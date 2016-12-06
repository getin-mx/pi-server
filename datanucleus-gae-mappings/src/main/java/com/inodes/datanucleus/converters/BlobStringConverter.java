package com.inodes.datanucleus.converters;

import org.datanucleus.store.types.converters.TypeConverter;
import org.datanucleus.util.Base64;

import com.inodes.datanucleus.model.Blob;

public class BlobStringConverter implements TypeConverter<Blob, String> {

	private static final long serialVersionUID = -4072427937202757477L;

	@Override
	public String toDatastoreType(Blob memberValue) {
		return new String(Base64.encode(memberValue.getBytes()));
	}

	@Override
	public Blob toMemberType(String datastoreValue) {
		return new Blob(Base64.decode(datastoreValue));
	}

}
