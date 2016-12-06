package mobi.allshoppings.model.tools;

import java.io.Serializable;
import java.util.HashMap;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Generic Multi Language Wrapper
 * 
 * @author mhapanowicz
 */
@SuppressWarnings("serial")
@PersistenceCapable(detachable="true")
@EmbeddedOnly
public class MultiLang implements Serializable {

	/**
	 * Multi language wrapper 
	 */
	@Persistent(defaultFetchGroup = "true", embedded = "true")
	private HashMap<String, String> values;

	/**
	 * Default Constructor
	 */
	public MultiLang() {
		values = new HashMap<String, String>();
	}
	
	/**
	 * Gets a message using its language key
	 * 
	 * @param lang
	 *            The Specified language
	 * @return The requested message
	 */
	public String get(String lang) {
		if( null == values ) values = new HashMap<String, String>();
		if( values.containsKey(lang)) {
			return values.get(lang);
		} else if (lang.length() > 2 ) {
			return values.get(lang.substring(0, 2));
		} else {
			return null;
		}
	}

	/**
	 * Sets a message using its language key
	 * 
	 * @param lang
	 *            The specified language
	 * @param value
	 *            The proper message
	 */
	public void set(String lang, String value) {
		if( null == values ) values = new HashMap<String, String>();
		values.put(lang, value);
	}

	/**
	 * @return the values
	 */
	public HashMap<String, String> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(HashMap<String, String> values) {
		this.values = values;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	/**
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
		MultiLang other = (MultiLang) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MultiLang [values=" + values + "]";
	}
	
}
