package mobi.allshoppings.model.tools;

import java.util.Arrays;
import java.util.List;

public class CountryHelper {

	private static final String[] countryNames = { "Argentina", "Bolivia",
			"Brasil", "Chile", "Ecuador", "Guatemala", "Mexico", "Panama",
			"Paraguay", "Peru", "Uruguay", "USA" };

	private static final String[] countryCodes = { "AR", "BO", "BR", "CL",
			"EC", "GT", "MX", "PA", "PY", "PE", "UY", "US" };

	public static String[] getCountryNames() {
		return countryNames;
	}
	
	public static String[] getCountryCodes() {
		return countryCodes;
	}
	
	public static List<String> getCountryNamesAsList() {
		return Arrays.asList(countryNames);
	}
	
	public static List<String> getCountryCodesAsList() {
		return Arrays.asList(countryCodes);
	}
	
	public static String getCountryCode(String country) {
		if( country == null ) return null;
		for(int i = 0; i < countryNames.length; i++) {
			if(countryNames[i].toLowerCase().equals(country.toLowerCase()))
				return countryCodes[i];
		}
		return null;
	}
	
	public static String getCountryCodeLowerCase(String country) {
		String c = getCountryCode(country);
		return c == null ? null : c.toLowerCase();
	}
}
