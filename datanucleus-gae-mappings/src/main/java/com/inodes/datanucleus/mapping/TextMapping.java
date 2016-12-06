package com.inodes.datanucleus.mapping;

import org.datanucleus.store.rdbms.mapping.java.ObjectAsStringMapping;

import com.inodes.datanucleus.model.Key;
import com.inodes.datanucleus.model.Text;

public class TextMapping extends ObjectAsStringMapping {

	/**
	 * Method to return the Java type being represented
	 * 
	 * @return The Java type we represent
	 */
	@Override
	public Class<Text> getJavaType() {
		return Text.class;
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
			key = ((Text) arg0).getValue();
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
		return new Text(arg0);
	}

}
