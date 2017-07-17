package mobi.allshoppings.tools;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory {

	public static Gson getInstance() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new GsonDateTypeAdapter());
		Gson gson = gsonBuilder.create();
		return gson;
	}
}
