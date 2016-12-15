package mobi.allshoppings.image;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.click.extras.gae.MemoryFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.inodes.datanucleus.model.Blob;

import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.model.Image;
import mobi.allshoppings.model.SystemConfiguration;


@SuppressWarnings("serial")
public class UploadImageServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(UploadImageServlet.class.getName());

	@Autowired
	SystemConfiguration systemConfiguration;

	@Autowired
	ImageDAO imageDao;

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
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		super.doDelete(req, resp);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		@SuppressWarnings("unused")
		String fileName = req.getParameter("file") != null ? req.getParameter("file") : null;

		super.doGet(req, resp);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		this.doGet(req, resp);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		long maxFileSize = systemConfiguration.getMaxUploadSize();

		FileItemFactory factory = new MemoryFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		JSONObject json = new JSONObject();

		try{ 
			// Parse the request to get file items.
			@SuppressWarnings("unchecked")
			List<FileItem> fileItems = upload.parseRequest(req);

			// Process the uploaded file items
			Iterator<FileItem> i = fileItems.iterator();

			while ( i.hasNext () ) {
				FileItem fi = (FileItem)i.next();
				if ( !fi.isFormField () ) {

					json.put("error", "");
					json.put("type", fi.getContentType());
					json.put("size", fi.getSize());

					if( fi.getSize() > maxFileSize ) {
						json.put("error", "max_file_size");
					} else {
						try {
							Image image = new Image();
							image.setKey(imageDao.createKey(fi.getName()));
							image.setContents(new Blob(fi.get()));
							image.setContentType(fi.getContentType());
							image.setOriginalFileName(fi.getName());
							image.setSessionKey(req.getSession().getId());
							imageDao.create(image);

							json.put("name", image.getIdentifier());
							json.put("url", "/img" + image.getIdentifier());
							json.put("delete_url", "/img/upload?file=" + image.getIdentifier());
							json.put("delete_type", "DELETE");
						} catch( Exception ex ) {
							log.log(Level.SEVERE, ex.getMessage());
							json.put("error", "max_file_size");
						}
					}
				}
			}

			resp.setHeader("Vary", "Accept");
			resp.setHeader("Content-type", "text/plain");
			OutputStream os = resp.getOutputStream();

			os.write(json.toString().getBytes());

		}catch(Exception ex) {
			System.out.println(ex);
			resp.sendError(500);
		}
	}

}
