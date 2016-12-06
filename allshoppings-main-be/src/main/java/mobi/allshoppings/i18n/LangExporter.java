package mobi.allshoppings.i18n;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class LangExporter extends HttpServlet {

	private static HashMap<String,String> langCache = new HashMap<String,String>();
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String lang = req.getParameter("lang");
		String wrapper = langCache.get(lang);
		
		if( wrapper == null ) {
			wrapper = buildLangWrapper(lang);
			langCache.put(lang, wrapper);
		}
		
		resp.setContentType("text/plain");
		resp.getWriter().println(wrapper);
	}

	/**
	 * Builds a message file (jQuery i18n) from a properties file
	 * @param lang
	 * @return
	 * @throws IOException
	 */
	private String buildLangWrapper(String lang) throws IOException {
		// Tries to locate the resource with the class loader
		InputStreamReader is = new InputStreamReader(LangExporter.class.getClassLoader().getResourceAsStream(
				new StringBuffer("lang_").append(lang)
				.append(".properties").toString()));

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
