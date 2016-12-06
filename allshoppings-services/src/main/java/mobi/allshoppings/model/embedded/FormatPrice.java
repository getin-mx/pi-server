package mobi.allshoppings.model.embedded;

import java.io.Serializable;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * This class represents a geographical located address
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
@EmbeddedOnly
public class FormatPrice implements Serializable {
	
	@Persistent
	/**
	 * Format Name
	 */
    private String format;
    
	@Persistent
	/**
	 * Format Price
	 */
	private double price;
	
	/**
	 * 
	 */
	public FormatPrice() {
		super();
	}

	/**
	 * @param format
	 * @param price
	 */
	public FormatPrice(String format, double price) {
		super();
		this.format = format;
		this.price = price;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormatPrice other = (FormatPrice) obj;
		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.equals(other.format))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FormatPrice [format=" + format + ", price=" + price + "]";
	}

}
