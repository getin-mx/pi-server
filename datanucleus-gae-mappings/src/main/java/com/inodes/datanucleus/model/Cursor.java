package com.inodes.datanucleus.model;

/**
 * Dummy object to emulate GAE Cursors
 * @author mhapanowicz
 *
 */
public class Cursor {

	public static Cursor fromWebSafeString(String webString) {
		return new Cursor();
	}
	
	public String toWebSafeString() {
		return null;
	}
}
