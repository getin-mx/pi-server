package mobi.allshoppings.image;

import java.util.Iterator;
import java.util.Set;

/**
 * Registry class for BitmapEncoders.
 *
 * @author Jeremias Maerki
 * @version $Id: BitmapEncoderRegistry.java,v 1.3 2010/10/05 06:57:44 jmaerki Exp $
 */
public class ASBitmapEncoderRegistry {

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private static Set<Entry> encoders = new java.util.TreeSet();

    static {
        register(ASImageIOBitmapEncoder.class.getName(), 0, false);
    }

    /**
     * Utility class: Constructor prevents instantiating when subclassed.
     */
    protected ASBitmapEncoderRegistry() {
        throw new UnsupportedOperationException();
    }

    public static class Entry implements Comparable<Object> {
        private ASBitmapEncoder encoder;
        private int priority;

        public Entry(ASBitmapEncoder encoder, int priority) {
            this.encoder = encoder;
            this.priority = priority;
        }

        /** {@inheritDoc} */
        public int compareTo(Object o) {
            Entry e = (Entry)o;
            return e.priority - this.priority; //highest priority first
        }

    }

    private static synchronized void register(String classname, int priority, boolean complain) {
        boolean failed = false;
        try {
            Class<?> clazz = Class.forName(classname);
            ASBitmapEncoder encoder = (ASBitmapEncoder)clazz.newInstance();
            encoders.add(new Entry(encoder, priority));
        } catch (Exception e) {
            failed = true;
        } catch (LinkageError le) {
            failed = true; //NoClassDefFoundError for example
        }
        if (failed) {
            if (complain) {
                throw new IllegalArgumentException(
                    "The implementation being registered is unavailable or "
                    + "cannot be instantiated: " + classname);
            } else {
                return;
            }
        }
    }

    /**
     * Register a new BitmapEncoder implementation.
     * @param classname fully qualified classname of the BitmapEncoder
     *      implementation
     * @param priority lets you define a priority for an encoder. If you want
     *      to give an encoder a high priority, assign a value of 100 or higher.
     */
    public static void register(String classname, int priority) {
        register(classname, priority, true);
    }

    /**
     * Indicates whether a specific BitmapEncoder implementation supports a
     * particular MIME type.
     * @param encoder BitmapEncoder to inspect
     * @param mime MIME type to check
     * @return true if the MIME type is supported
     */
    public static boolean supports(ASBitmapEncoder encoder, String mime) {
        String[] mimes = encoder.getSupportedMIMETypes();
        for (int i = 0; i < mimes.length; i++) {
            if (mimes[i].equals(mime)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates whether a particular MIME type is supported by one of the
     * registered BitmapEncoder implementations.
     * @param mime MIME type to check
     * @return true if the MIME type is supported
     */
    public static boolean supports(String mime) {
        Iterator<Entry> i = encoders.iterator();
        while (i.hasNext()) {
            Entry entry = (Entry)i.next();
            ASBitmapEncoder encoder = entry.encoder;
            if (supports(encoder, mime)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a BitmapEncoder instance for a particular MIME type.
     * @param mime desired MIME type
     * @return a BitmapEncoder instance (throws an UnsupportedOperationException
     *      if no suitable BitmapEncoder is available)
     */
    public static ASBitmapEncoder getInstance(String mime) {
        Iterator<Entry> i = encoders.iterator();
        while (i.hasNext()) {
            Entry entry = (Entry)i.next();
            ASBitmapEncoder encoder = entry.encoder;
            if (supports(encoder, mime)) {
                return encoder;
            }
        }
        throw new UnsupportedOperationException(
            "No BitmapEncoder available for " + mime);
    }

    /**
     * Returns a Set of Strings with all the supported MIME types from all
     * registered BitmapEncoders.
     * @return a Set of Strings (MIME types)
     */
    @SuppressWarnings("unchecked")
	public static Set<String> getSupportedMIMETypes() {
        @SuppressWarnings("rawtypes")
		Set<String> mimes = new java.util.HashSet();
        Iterator<Entry> i = encoders.iterator();
        while (i.hasNext()) {
            Entry entry = (Entry)i.next();
            ASBitmapEncoder encoder = entry.encoder;
            String[] s = encoder.getSupportedMIMETypes();
            for (int j = 0; j < s.length; j++) {
                mimes.add(s[j]);
            }
        }
        return mimes;
    }

}
