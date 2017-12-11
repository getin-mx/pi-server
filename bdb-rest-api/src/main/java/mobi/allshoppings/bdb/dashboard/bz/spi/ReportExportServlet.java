package mobi.allshoppings.bdb.dashboard.bz.spi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExcelExportHelper;
import mobi.allshoppings.model.User;

@SuppressWarnings("serial")
public class ReportExportServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ReportExportServlet.class.getName());

	@Autowired
	private ExcelExportHelper exportHelper;
	@Autowired
	private UserDAO userDao;

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

		String userToken = req.getParameter("authToken");
		User user = null;
		
		if(StringUtils.hasText(userToken)) {
			try {
				user = userDao.getByAuthToken(userToken);
			} catch(ASException e) {
				if(e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE
						|| e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE)
					log.log(Level.INFO, "Mario (token) is missing", e);
				else
					log.log(Level.SEVERE, "", e);
			}
		}
		
		long start = System.currentTimeMillis();
		log.log(Level.INFO, "Begining reportExport");
		try {
			//FIXME: URL Hardcoded
			byte[] b = exportHelper.export(req.getParameter("storeId"), "2017-01-02",
					req.getParameter("toStringDate"), 4, "/tmp", user);

			long end = System.currentTimeMillis();
			log.log(Level.INFO, "BrandExport finished in " + (end-start) + "ms");

			resp.setHeader("Content-Disposition", "inline; filename=\"report.xls\"");
			resp.setHeader("Content-type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Length", String.valueOf(b.length));
			OutputStream os = resp.getOutputStream();
			os.write(b);

			return;
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}		
	}

}
