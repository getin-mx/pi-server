package mobi.allshoppings.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Image;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.springframework.beans.factory.annotation.Autowired;

import com.inodes.datanucleus.model.Blob;

public class BarcodeService {

	public static final String PNG = "image/png";
	public static final String CODE128 = "code128";

	@Autowired
	private ImageDAO dao;

	public Image barCode128(String inputText) throws ASException {

		Image image = null;

		try {
			String identifier = "barcode-" + URLEncoder.encode(inputText, "UTF8") + ".png";
			try {
				image = dao.getByOriginalName(identifier, false);
			} catch( ASException e ) {
				if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
					
					BarcodeUtil util = BarcodeUtil.getInstance();
					BarcodeGenerator gen = util.createBarcodeGenerator(buildCfg(CODE128));

					OutputStream fout = new ByteArrayOutputStream();
					int resolution = 300;
					
					ASBitmapCanvasProvider canvas = new ASBitmapCanvasProvider(fout, 
							PNG, resolution, BufferedImage.TYPE_BYTE_BINARY,
							false, 0);

					gen.generateBarcode(canvas, inputText);
					canvas.finish();
					byte[] data = ((ByteArrayOutputStream)fout).toByteArray();

					image = new Image();
					image.setContents(new Blob(data));
					image.setContentType(PNG);
					image.setOriginalFileName(identifier);
					image.setSource("barcode4j");
					image.setKey(dao.createKey(identifier));
					dao.create(image);
					
				} else {
					throw e;
				}
			}
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}

		return image;
	}

	private static Configuration buildCfg(String type) {
		DefaultConfiguration cfg = new DefaultConfiguration("barcode");

		// Bar code type
		DefaultConfiguration child = new DefaultConfiguration(type);
		cfg.addChild(child);

		// Human readable text position
		DefaultConfiguration attr = new DefaultConfiguration("human-readable");
		DefaultConfiguration subAttr = new DefaultConfiguration("placement");
		subAttr.setValue("none");
		attr.addChild(subAttr);

		child.addChild(attr);
		return cfg;
	}


}
