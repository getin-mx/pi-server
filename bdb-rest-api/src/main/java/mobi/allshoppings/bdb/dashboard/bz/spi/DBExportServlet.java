package mobi.allshoppings.bdb.dashboard.bz.spi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.exporter.ExcelExportHelper;
import mobi.allshoppings.tools.CollectionFactory;

public class DBExportServlet extends HttpServlet {

	// class attributes
	
	private List<String> storesId;
	private String fromDate, toDate, destFile, brandId;
	
	private static final long serialVersionUID = 6006971372478805453L;
	public static final String FROM_DATE_PARAM = "fromDate";
	public static final String TO_DATE_PARAM = "toDate";
	public static final String BRAND_PARAM = "brandId";
	public static final String STORES_PARAM = "storeIds";
	public static final String DESTINATION_FILE_PARAM = "destFile";
	private static final Logger LOG = Logger.getLogger(DBExportServlet.class.getSimpleName());
	
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
		try {
			byte[] rawCsv;
		
			//FIXME: Check Auth Token
			fromDate = req.getParameter(FROM_DATE_PARAM);
			toDate = req.getParameter(TO_DATE_PARAM);
			brandId = req.getParameter(BRAND_PARAM);
			StringBuilder storesIds = new StringBuilder();
			String aux;
			storesId = CollectionFactory.createList();
			for(String store : req.getParameter(STORES_PARAM).split(",")) {
				aux = store.trim();
				if(!storesId.contains(aux)) storesId.add(aux);
				storesIds.append(aux).append('-');
			}
			storesIds.replace(storesIds.length() -1, storesIds.length(), "");
			if(!StringUtils.hasText(brandId) && (storesId == null || storesId.size() == 0))
				throw ASExceptionHelper.defaultException("At least, one brand or store ID must be given",
						null);
			destFile = StringUtils.hasText(brandId) ? brandId : "";
			destFile += storesIds.toString();
			long benchmark = System.currentTimeMillis();
			rawCsv = exportHelper.exportDB(storesId, brandId, fromDate, toDate, destFile, false);
			String fileName = req.getParameter(DESTINATION_FILE_PARAM);
			int index;
			if(fileName == null) fileName = "report";
			else if((index = fileName.indexOf('.')) >= 0) 
				fileName = index == 0 ? "report" : fileName.substring(0, index);
			resp.setHeader("Content-Disposition", "inline; filename=\"" +fileName +".xlsx\"");
			resp.setHeader("Content-type",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			resp.setHeader("Content-Length", String.valueOf(rawCsv.length));
			OutputStream os = resp.getOutputStream();
			os.write(rawCsv);
			os.flush();
			LOG.log(Level.FINE, "DB Exported to MS Excel in "
						+(System.currentTimeMillis() -benchmark) +"ms");
		} catch(ASException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
		
	}
	
}
