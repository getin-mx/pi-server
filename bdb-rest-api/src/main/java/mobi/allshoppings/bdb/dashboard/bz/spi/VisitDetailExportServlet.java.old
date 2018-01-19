package mobi.allshoppings.bdb.dashboard.bz.spi;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import mobi.allshoppings.bdb.tools.CSVVisitDetailExport;

@SuppressWarnings("serial")
public class VisitDetailExportServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(VisitDetailExportServlet.class.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private CSVVisitDetailExport exporter;

	/**
	 * Initialization method
	 */
	@Override
	public void init() {
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		AutowireCapableBeanFactory bf = ctx.getAutowireCapableBeanFactory();
		bf.autowireBean(this);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		//FIXME: Check Auth Token

		long start = new Date().getTime();
		log.log(Level.INFO, "Begining VisitDetailExport");
		try {
			//FIXME: URL Hardcoded
			byte[] b = exporter.createCSVRepresentation(req.getParameter("authToken"),
					"http://dashboard.allshoppings.mobi/appv2/", req.getParameter("shoppingId"),
					req.getParameter("brandId"), req.getParameter("storeId"),
					sdf.parse(req.getParameter("fromStringDate")), sdf.parse(req.getParameter("toStringDate")), 
					false);

			long end = new Date().getTime();
			log.log(Level.INFO, "VisitDetailExport finished in " + (end-start) + "ms");

			resp.setHeader("Content-Disposition", "inline; filename=\"details.csv\"");
			resp.setHeader("Content-type", "application/vcsv");
			resp.setHeader("Content-Length", String.valueOf(b.length));
			OutputStream os = resp.getOutputStream();
			os.write(b);

			return;
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}		
	}

}
