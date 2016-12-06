package com.inodes.datanucleus.model;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;

public final class Blob implements Serializable {

	/**
	 * Serial Version UUID
	 */
	private static final long serialVersionUID = 6210713401925622518L;

	/**
	 * Inner bytes
	 */
	private byte bytes[];

	/**
	 * Default constructor
	 * @param bytes Inner Bytes
	 */
	public Blob(byte bytes[]) {
		this.bytes = bytes;
	}

	/**
	 * @return The byte array
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return Arrays.hashCode(bytes);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object instanceof Blob) {
			Blob key = (Blob) object;
			return Arrays.equals(bytes, key.bytes);
		} else {
			return false;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return (new StringBuilder()).append("<Blob: ").append(bytes.length)
				.append(" bytes>").toString();
	}

	/**
	 * Required for the serialized interface
	 * 
	 * @param out The received object output stream
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.write(bytes);
		out.flush();
	}

	/**
	 * Required for the serialized interface
	 * 
	 * @param in The received object input stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		bytes = new byte[in.available()];
		in.read(bytes);
	}

	@SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException {
		this.bytes = null;
	}

}
