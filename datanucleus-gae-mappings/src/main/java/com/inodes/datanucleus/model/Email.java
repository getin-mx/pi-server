package com.inodes.datanucleus.model;

import java.io.Serializable;

public final class Email implements Serializable, Comparable<Object> {

	/**
	 * Serial Version UID
	 */
	static final long serialVersionUID = -4807513785819575482L;
	
	/**
	 * Inner Email
	 */
	private String email;

	/**
	 * Default constructor
	 * 
	 * @param email
	 *            The email address
	 */
	public Email(String email) {
		if (email == null) {
			throw new NullPointerException("email must not be null");
		} else {
			this.email = email;
			return;
		}
	}

	/**
	 * @return Email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Comparator
	 * 
	 * @param e
	 *            The other object to compare
	 * @return
	 */
	public int compareTo(Email e) {
		return email.compareTo(e.email);
	}

	/**
	 * Comparator
	 * 
	 * @param e
	 *            The other object to compare
	 * @return
	 */
	public int compareTo(Object x0) {
		return compareTo((Email) x0);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Email email1 = (Email) o;
		return email.equals(email1.email);
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return email.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "<Email:" + email + ">";
	}

}
