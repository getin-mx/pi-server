package com.inodes.datanucleus.mapping;

import org.datanucleus.store.rdbms.mapping.java.SerialisedMapping;

import com.inodes.datanucleus.model.Blob;

public class BlobMapping extends SerialisedMapping {

	/**
	 * Method to return the Java type being represented
	 * 
	 * @return The Java type we represent
	 */
	@Override
	public Class<Blob> getJavaType() {
		return Blob.class;
	}

}
