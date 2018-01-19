package mobi.allshoppings.image;

import java.io.IOException;
import java.io.OutputStream;

import com.google.code.appengine.awt.image.BufferedImage;

/**
 * This interface is used to encode bitmaps into their target formats.
 * 
 * @author Jeremias Maerki
 * @version $Id: BitmapEncoder.java,v 1.2 2004/09/04 20:25:54 jmaerki Exp $
 */
public interface ASBitmapEncoder {

    /**
     * Returns an array of MIME types supported.
     * @return the array of MIME types
     */
    String[] getSupportedMIMETypes();
    
    /**
     * Encodes a BufferedImage to a target format and writes it to the 
     * OutputStream.
     * @param image the image to encode
     * @param out the OutputStream to write the image to
     * @param mime the MIME type in which to encode the image
     * @param resolution the resolution in dpi of the image
     * @throws IOException in case of an I/O problem
     */
    void encode(BufferedImage image, OutputStream out, 
            String mime, int resolution) throws IOException;

}
