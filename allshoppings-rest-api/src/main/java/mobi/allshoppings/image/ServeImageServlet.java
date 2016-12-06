package mobi.allshoppings.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
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

import com.inodes.util.FileLoader;

import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Image;

@SuppressWarnings("serial")
public class ServeImageServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ServeImageServlet.class.getName());

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
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String uriParts[] = req.getRequestURI().split("/");
		String name = uriParts[uriParts.length - 1];
		String scanName = name;

		int selectedWidth = 0;
		try { 
			selectedWidth = Integer.parseInt(req.getParameter("width"));
			scanName = name + "?width=" + selectedWidth;
		} catch( Exception e ) {
			Enumeration<String> i = req.getParameterNames();
			while(i.hasMoreElements()) {
				String s = i.nextElement();
				if( s.startsWith("width=")) {
					String[] parts = s.split("\\?")[0].split("=");
					if( parts.length > 0 ) {
						scanName = name + "?width=" + parts[1];
					}
				} else if( s.equals("width")) {
					String[] parts = req.getParameter(s).split("\\?");
					if( parts.length > 0 ) {
						scanName = name + "?width=" + parts[0];
					}
				}
			}
			
		}
		
		// First, try to get the resolution changed image
		try {
			Image image = imageDao.get(scanName, true);
			
			resp.setHeader("Content-type", image.getContentType());
			resp.setHeader("Content-Length", String.valueOf(image.getContents().getBytes().length));
			OutputStream os = resp.getOutputStream();
			os.write(image.getContents().getBytes());

			return;
		} catch (ASException e) {
			if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
				log.log(Level.WARNING, e.getMessage(), e);
			} else {
				// If the resolution changed name was not found, 
				// then try to find the original one and change the res
				if( !name.equals(scanName)) {
					try {
						
						Image image = imageDao.get(name, true);
						
						// TODO: Transform image
						
//						ImagesService imagesService = ImagesServiceFactory.getImagesService();
//						com.google.appengine.api.images.Image oldImage = ImagesServiceFactory.makeImage(image.getContents().getBytes());
//						if(oldImage.getWidth() > selectedWidth ) {
//							float factor = selectedWidth * 100 / oldImage.getWidth();
//							int selectedHeight = Math.round(factor * oldImage.getHeight() / 100);
//							Transform resize = ImagesServiceFactory.makeResize(selectedWidth,selectedHeight);
//							com.google.appengine.api.images.Image newImage = imagesService.applyTransform(resize, oldImage);
//							image.setContents(new Blob(newImage.getImageData()));
//						}
//
//						Image imageToSave = new Image();
//						imageToSave.setContents(image.getContents());
//						imageToSave.setContentType(image.getContentType());
//						imageToSave.setOriginalFileName(image.getOriginalFileName());
//						imageToSave.setOwner(image.getOwner());
//						imageToSave.setSource(image.getSource());
//						imageToSave.setKey(imageDao.createKey(scanName));
//						imageDao.create(imageToSave);
						
						resp.setHeader("Content-type", image.getContentType());
						resp.setHeader("Content-Length", String.valueOf(image.getContents().getBytes().length));
						OutputStream os = resp.getOutputStream();
						os.write(image.getContents().getBytes());
						
						return;
						
					} catch( ASException e2 ) {
						if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
							log.log(Level.WARNING, e.getMessage(), e);
						} else {
							log.log(Level.WARNING, "image not found: " + req.getRequestURI() );
						}
					} catch (Exception e2) {
						log.log(Level.WARNING, e2.getMessage(), e2);
					}
				} else {
					log.log(Level.WARNING, "image not found: " + req.getRequestURI() );
				}
			}
		} catch (Exception e1) {
			log.log(Level.WARNING, e1.getMessage(), e1);
		}
		
		try {
			// Sends the default image
			int off = 0;
			int len = 1024;
			byte[] b = new byte[len];
			int r;

			File f = new File(FileLoader.getResource("defaultavatar.png", FileLoader.PRECEDENCE_SYSTEMPATH).getFile().toString());
			InputStream def = new FileInputStream(f);
			resp.setHeader("Content-type", "image/png");
			resp.setHeader("Content-Length", String.valueOf(f.length()));
			OutputStream os = resp.getOutputStream();
			r = def.read(b, off, len);
			while( r >= 0 ) {
				os.write(b, 0, r);
				r = def.read(b, off, len);
			}
			def.close();

		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			resp.sendError(404);
		}
		
	}

}
