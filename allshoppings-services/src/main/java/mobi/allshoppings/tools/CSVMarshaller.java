package mobi.allshoppings.tools;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.inodes.datanucleus.model.Email;
import com.inodes.datanucleus.model.Text;

import mobi.allshoppings.model.tools.MultiLang;

public class CSVMarshaller {

	private List<String> headers;
	private String delimiter;
	private String separator;
	
	public CSVMarshaller() {
		super();
		delimiter = "\"";
		separator = ",";
		headers = CollectionFactory.createList();
	}

	/**
	 * @return the headers
	 */
	public List<String> getHeaders() {
		return headers;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	/**
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CSVMarshaller [headers=" + headers + ", delimiter=" + delimiter + ", separator=" + separator + "]";
	}

	@SuppressWarnings("deprecation")
	public String marshallHeaders() {
		StringBuffer sb = new StringBuffer();
		
		boolean first = true;
		for( String header : headers ) {

			if( !first ) 
				sb.append(separator);
			first = false;

			sb.append(delimiter).append(StringUtils.escape(header)).append(delimiter);

		}

		sb.append("\n");
		return sb.toString();
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public String marshall(Object input) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		StringBuffer sb = new StringBuffer();
		
		Map<String, Object> properties = PropertyUtils.describe(input);

		boolean first = true;
		for(String header : headers ) {
			if(!header.contains(".")) {
				Object fieldValue = properties.get(header);

				if( !first ) 
					sb.append(separator);
				first = false;
				
				// Process different types for the right output
				if (fieldValue != null) {
					if (fieldValue instanceof Email) {
						fieldValue = ((Email)fieldValue).getEmail();
					} else if (fieldValue instanceof Text) {
						fieldValue = ((Text)fieldValue).getValue();
					} else if (fieldValue instanceof MultiLang) {
						fieldValue = ((MultiLang)fieldValue).get("es");
					}
					sb.append(delimiter).append(StringUtils.escape(fieldValue.toString())).append(delimiter);
				} else {
					sb.append(delimiter).append(delimiter);
				}

			}
		}
		
		sb.append("\n");
		return sb.toString();
	}
}
