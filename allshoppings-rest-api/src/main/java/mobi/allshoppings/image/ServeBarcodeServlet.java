package mobi.allshoppings.image;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.allshoppings.model.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@SuppressWarnings("serial")
public class ServeBarcodeServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ServeBarcodeServlet.class.getName());

	@Autowired
	BarcodeService barcodeService;

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

		String uriParts[] = req.getRequestURI().split("/");
		String name = uriParts[uriParts.length - 1];

		try {
			Image image = barcodeService.barCode128(name);
			resp.setHeader("Content-type", image.getContentType());
			OutputStream os = resp.getOutputStream();
			os.write(image.getContents().getBytes());

		} catch (Exception e1) {
			log.log(Level.WARNING, e1.getMessage(), e1);
		}

	}

}
