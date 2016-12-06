package mobi.allshoppings.image;

import java.io.IOException;
import java.io.OutputStream;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.AbstractCanvasProvider;

import com.google.code.appengine.awt.image.BufferedImage;

/**
 * CanvasProvider implementation for generating bitmaps. This class wraps
 * Java2DCanvasProvider to do the actual rendering.
 * 
 * @author Jeremias Maerki
 * @version $Id: BitmapCanvasProvider.java,v 1.4 2008/05/13 13:00:46 jmaerki Exp $
 */
public class ASBitmapCanvasProvider extends AbstractCanvasProvider {

    private OutputStream out;
    private String mime;
    private int resolution;
    private int imageType;
    private boolean antiAlias;
    private BufferedImage image;
    private ASJava2DCanvasProvider delegate;

    /**
     * Creates a new BitmapCanvasProvider. 
     * @param out OutputStream to write to
     * @param mime MIME type of the desired output format (ex. "image/png")
     * @param resolution the desired image resolution (dots per inch)
     * @param imageType the desired image type (Values: BufferedImage.TYPE_*)
     * @param antiAlias true if anti-aliasing should be enabled
     */
    public ASBitmapCanvasProvider(OutputStream out, String mime, 
                    int resolution, int imageType, boolean antiAlias, int orientation) {
        super(orientation);
        this.out = out;
        this.mime = mime;
        this.resolution = resolution;
        this.imageType = imageType;
        this.antiAlias = antiAlias;
    }

    /**
     * Creates a new BitmapCanvasProvider. 
     * @param resolution the desired image resolution (dots per inch)
     * @param imageType the desired image type (Values: BufferedImage.TYPE_*)
     * @param antiAlias true if anti-aliasing should be enabled
     */
    public ASBitmapCanvasProvider(int resolution, int imageType, boolean antiAlias, 
                    int orientation) {
        this(null, null, resolution, imageType, antiAlias, orientation);
    }

    /**
     * Call this method to finish any pending operations after the 
     * BarcodeGenerator has finished its work.
     * @throws IOException in case of an I/O problem
     */
    public void finish() throws IOException {
        this.image.flush();
        if (this.out != null) {
            final ASBitmapEncoder encoder = ASBitmapEncoderRegistry.getInstance(mime);
            encoder.encode(this.image, out, mime, resolution);
        }
    }
    
    /**
     * Returns the buffered image that is used to paint the barcode on.
     * @return the image.
     */
    public BufferedImage getBufferedImage() {
        return this.image;
    }

    /** {@inheritDoc} */
    public void establishDimensions(BarcodeDimension dim) {
        super.establishDimensions(dim);
        this.image = ASBitmapBuilder.prepareImage(dim, getOrientation(),
                this.resolution, this.imageType);
        this.delegate = new ASJava2DCanvasProvider(
            ASBitmapBuilder.prepareGraphics2D(this.image, dim, getOrientation(),
                    this.antiAlias), getOrientation());
        this.delegate.establishDimensions(dim);
    }

    /** {@inheritDoc} */
    public void deviceFillRect(double x, double y, double w, double h) {
        this.delegate.deviceFillRect(x, y, w, h);
    }

    /** {@inheritDoc} */
    public void deviceText(String text,
            double x1, double x2, double y1,
            String fontName, double fontSize, TextAlignment textAlign) {
        this.delegate.deviceText(text, x1, x2, y1, fontName, fontSize, textAlign);
    }

}
