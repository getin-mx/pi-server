package mobi.allshoppings.tools;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.StringUtils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class GsonDateTypeAdapter extends TypeAdapter<Date> {

	@SuppressWarnings("deprecation")
	@Override
	public Date read(final JsonReader in) throws IOException {
		Date date = null;
		if (in.hasNext()) {
			String s = in.nextString();
			if( StringUtils.hasText(s)) {
				if( NumberUtils.isNumber(s)) {
					date = new Date(Long.parseLong(s));
				} else {
					date = new Date(s);
				}
			}
		}
		return date;
	}

	@Override
	public void write(JsonWriter out, Date value) throws IOException {
	    if( value != null ) {
	    	out.value(value.getTime());
	    } else {
	    	out.value(0L);
	    }
	}

}
