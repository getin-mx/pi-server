package com.inodes.datanucleus.model;

import java.io.Serializable;

public final class Text implements Serializable {

	/**
	 * Serial Version UID
	 */
	public static final long serialVersionUID = -8389037235415462280L;
	
	/**
	 * Inner Text
	 */
	private String value;

	/**
	 * Default constructor
	 * 
	 * @param value
	 *            The inner text
	 */
	public Text(String value) {
		this.value = value;
	}

	/**
	 * @return The inner text
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		if (value == null)
			return -1;
		else
			return value.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object instanceof Text) {
			Text key = (Text) object;
			if (value == null)
				return key.value == null;
			else
				return value.equals(key.value);
		} else {
			return false;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (value == null)
			return "<Text: null>";
		String text = value;
		if (text.length() > 70)
			text = (new StringBuilder()).append(text.substring(0, 70)).append("...").toString();
		return (new StringBuilder()).append("<Text: ").append(text).append(">").toString();
	}

}
