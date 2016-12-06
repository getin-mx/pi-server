package mobi.allshoppings.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import com.inodes.util.FileLoader;

public class LangUtils {

	/**
	 * Builds a message file (jQuery i18n) from a properties file
	 * @param lang
	 * @return
	 * @throws IOException
	 */
	public String buildLangWrapper(String lang) throws IOException {
		// Tries to locate the resource with the class loader
		URL url = FileLoader.getResource(new StringBuffer("lang_").append(lang)
				.append(".properties").toString());
		InputStreamReader is = new InputStreamReader(new FileInputStream(new File(url.getFile())));

		// Opens the properties file and loads its contents
		Properties prop = new Properties();
		prop.load(is);

		// Creates the language javascript file according to the 
		// properties found
		StringBuffer sb = new StringBuffer();
		Iterator<?> i = prop.keySet().iterator();
		sb.append("var my_dictionary = {\n");
		while(i.hasNext()) {
			String key = i.next().toString();
			String value = prop.getProperty(key);
			sb.append("\t'").append(key).append("' : '").append(value).append("',\n");
		}
		sb.append("\t'null':'null'\n");
		sb.append("}\n");
		sb.append("$.i18n.setDictionary(my_dictionary);\n");

		// Lastly, returns the builded message file
		return sb.toString();
	}

}
