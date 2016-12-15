package mobi.allshoppings.image;

import java.net.URL;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.inodes.datanucleus.model.Blob;
import com.inodes.datanucleus.model.Key;

import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Image;

public class ImageDownloader {

	@Autowired
	private ImageDAO dao;

	public Image downloadImage(String imageUrl, Key owner) throws ASException {
		return downloadImage(imageUrl, owner, 0, 0, 0, 0);
	}
	
	public Image downloadImage(String imageUrl, Key owner, int width, int height, double cropx, double cropy) throws ASException {
		return downloadImage(null, imageUrl, owner, width, height, cropx, cropy);
	}
	
	public Image downloadImage(String name, String imageUrl, Key owner, int width, int height, double cropx, double cropy) throws ASException {
		Image ret = null;
		try {
			ret = dao.getBySource(imageUrl);
		} catch( ASException e ) {
			if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
				throw e;
			}

			String parts[] = imageUrl.split("\\?")[0].split("/");
			String extParts[] = parts[parts.length - 1].split("\\.");

			String extension = (extParts[extParts.length -1].equals(parts[parts.length -1])) ? "jpg" : extParts[extParts.length -1];
			String originalFileName = parts[parts.length -1];
			boolean nameRequested = true;
			if(!StringUtils.hasText(name)) {
				name = UUID.randomUUID().toString() + "." + extension;
				nameRequested = false;
			}

			try {
				URL url = new URL(imageUrl);
			    byte[] bContents = null;
			    int count = 5;
			    while(( bContents == null || bContents.length == 0 ) && count > 0 ) {
			    	if( count < 5 ) try {Thread.sleep(500);}catch(Exception e1){}
			    	bContents = IOUtils.toByteArray(url.openStream());
			    	count--;
			    }
			    Blob contents = null;

			    // TODO: Transformation Services
//				if( width > 0 ) {
//					ImagesService imagesService = ImagesServiceFactory.getImagesService();
//					com.google.appengine.api.images.Image oldImage = ImagesServiceFactory.makeImage(bContents);
//					Transform resize = ImagesServiceFactory.makeResize(100, 100, 0, 0);
//					com.google.appengine.api.images.Image newImage = imagesService.applyTransform(resize, oldImage);
//					byte[] bContents2 = newImage.getImageData();
//					contents = new Blob(bContents2);
//				} else {
					contents = new Blob(bContents);
//				}

				ret = new Image();
				ret.setKey(nameRequested ? dao.forceKey(name) : dao.createKey(name));
				ret.setSessionKey(null);
				ret.setOwner(owner);
				ret.setContents(contents);
				ret.setOriginalFileName(originalFileName);
				ret.setContentType("image/jpeg");
				ret.setSource(imageUrl);
				dao.create(ret);
			} catch( Exception e1 ) {
				throw ASExceptionHelper.defaultException(e1.getMessage(), e1);
			}
		}

		return ret;
	}

}
