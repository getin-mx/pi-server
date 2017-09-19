package mobi.allshoppings.bdb.dashboard.bz.spi;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import joptsimple.OptionParser;
import mobi.allshoppings.exporter.ExcelExportHelper;

public class DBExportServlet extends HttpServlet {

	// class attributes
	
	private String[] storesId;
	private String fromDate, toDate, countryISO, languageISO, destFile;
	
	private static final long serialVersionUID = 6006971372478805453L;
	private static final Logger LOG = Logger.getLogger(DBExportServlet.class.getSimpleName());
	static OptionParser parser;
	
	@Autowired
	private ExcelExportHelper exportHelper;
	
	// implementation methods
	
	@Override
	public void init() {
		WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext())
				.getAutowireCapableBeanFactory().autowireBean(this);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//FIXME: Check Auth Token
		if(req == null || resp == null) {
			
		} else {
			byte[] rawCsv = new byte[0];
			resp.setHeader("Content-Disposition", "inline; filename=\"report.xls\"");
			resp.setHeader("Content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Length", String.valueOf(rawCsv.length));
		}//if parameters are null, read from class attributes
		// TODO call the method that does things... you know.... those... things...
		
		OutputStream os = resp.getOutputStream();
		os.write(rawCsv);
		os.flush();
		
	}
	
	// static methods
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts("fromDate", "Date From").withRequiredArg().ofType( String.class );
		parser.accepts("toDate", "Date To").withRequiredArg().ofType( String.class );
		parser.accepts("brandIds", "List of comma separated brands").withRequiredArg()
				.ofType( String.class );
		parser.accepts("storeIds", "List of comma separated stores (superseeds brandIds)")
				.withRequiredArg().ofType( String.class );
		// TODO visitas?
		//parser.accepts( "shoppingIds", "List of comma separated shoppings (superseeds brandIds and storeIds)").withRequiredArg().ofType( String.class );
		parser.accepts("employees", "set to true to get employees data. Otherwise (false is default) "
				+ "won't fetch employees. This flag can be mixed with visits and stores (at least, "
				+ "one of them is required)").withRequiredArg().ofType( Boolean.class );
		parser.accepts("generatePerDay",
				"True (Default) outputs a database with only daily data. "
				+ "False would generate a hourly one (if possible)")
				.withRequiredArg().ofType( Boolean.class );
		parser.accepts("visits", "set to true to get visits data. Otherwise (false is default) "
				+ "won't fetch visits. This flag can be mixed with employees and stores (at least, "
				+ "one of them is required)").withRequiredArg().ofType(Boolean.class);
		parser.accepts("stores", "set to true to get stores data. Otherwise (false is default) "
				+ "won't fetch stores. This flag can be mixed with employees and visits (at least, "
				+ "one of them is required)").withRequiredArg().ofType(Boolean.class);
		parser.accepts("destFile", "Specifies the route where the result XML should be written into."
				+ " If a directorry is given, the file will be named '<givenIds>.xml'."
				+ " If a file with an extension different from XML is given, '.xml' will be "
				+ "appended to the file name. By default, the file is writen to '<givenIds>.xml' in"
				+ "current working directory").withRequiredArg().ofType(String.class);
		return parser;
	}
	
	public static void main(String[] args) {
		DBExportServlet dbExport = new DBExportServlet();
		// TODO populate dbExport things
		try {
			dbExport.doGet(null, null);
		} catch(IOException | ServletException e) {
			LOG.log(Level.WARN, "Couldn't export DB\n\n" +e.getMessage());
		}
		LOG.log(Level.INFO, "Voy");
	}
	
}
