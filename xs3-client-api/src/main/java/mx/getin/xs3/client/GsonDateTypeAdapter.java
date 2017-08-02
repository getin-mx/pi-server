package mx.getin.xs3.client;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class GsonDateTypeAdapter extends TypeAdapter<Date> {

	@Override
	public Date read(final JsonReader in) throws IOException {
		Date date = null;
		if (in.hasNext()) {
			String s = in.nextString();
			date = obtainDateValue(s, null);
		}
		return date;
	}

	@Override
	public void write(JsonWriter out, Date value) throws IOException {
	    if( value != null ) {
	    	out.value(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(value));
	    }
	}

	/**
	 * Searches the key parameter on the URL's query. If found returns the value
	 * otherwise the defaultValue. The date must be in
	 * ISO_DATETIME_TIME_ZONE_FORMAT format.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Date obtainDateValue(String value, Date defaultValue) {
		if (value == null) {
			return defaultValue;
		}

		String pattern = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern();
		Date date = null;
		try {
			date = DateUtils.parseDate((String)value, new String[] { pattern });
		} catch (ParseException e) {
		}
		if (date == null) {
			return defaultValue;
		}
		return date;
	}
}
