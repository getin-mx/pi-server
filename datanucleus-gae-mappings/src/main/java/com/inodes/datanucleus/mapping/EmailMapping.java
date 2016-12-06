package com.inodes.datanucleus.mapping;

import org.datanucleus.store.rdbms.mapping.java.ObjectAsStringMapping;

import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Key;

public class EmailMapping extends ObjectAsStringMapping {

	/**
	 * Method to return the Java type being represented
	 * 
	 * @return The Java type we represent
	 */
	@Override
	public Class<Email> getJavaType() {
		return Email.class;
	}

	/**
	 * Method to return the default length of this type in the data store. 
	 * 
	 * @return The default length
	 */
	@Override
	public int getDefaultLength(int index) {
		return 100;
	}

	/**
	 * Method to set the data store string value based on the object value.
	 * 
	 * @param object
	 *            The object
	 * @return The string value to pass to the data store
	 */
	@Override
	protected String objectToString(Object arg0) {
		String key;
		if (arg0 instanceof Key) {
			key = ((Email) arg0).getEmail();
		} else {
			key = (String) arg0;
		}
		return key;
	}

	/**
	 * Method to extract the objects value from the datastore string value.
	 * 
	 * @param datastoreValue
	 *            Value obtained from the datastore
	 * @return The value of this object (derived from the datastore string
	 *         value)
	 */
	@Override
	protected Object stringToObject(String arg0) {
		return new Email(arg0);
	}

}
