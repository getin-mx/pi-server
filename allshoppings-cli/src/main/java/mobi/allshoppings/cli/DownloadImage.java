package mobi.allshoppings.cli;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.image.ImageDownloader;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;


public class DownloadImage extends AbstractCLI {

	private static final Logger log = Logger.getLogger(DownloadImage.class.getName());
	
	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "url", "Image URL" ).withRequiredArg().ofType( String.class );
		parser.accepts( "name", "Image Name" ).withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}
	
	public static void main(String args[]) throws ASException {
		try {
			
			ImageDownloader downloader = (ImageDownloader)getApplicationContext().getBean("image.downloader");
			
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String url = null;
			String name = null;
			
			try {
				if( options.has("help")) usage(parser);
				if( options.has("url")) url = (String)options.valueOf("url");
				if( options.has("name")) name = (String)options.valueOf("name");

				if(!StringUtils.hasText(url)) usage(parser);
				if( name == null ) {
					name = FilenameUtils.getBaseName(url);
					String extension = FilenameUtils.getExtension(url);
					if( StringUtils.hasText(extension)) {
						name = name + "." + extension;
					}
				}
				
			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			log.log(Level.INFO, "Starting to import image from " + url + " and saving with name " + name);
			downloader.downloadImage(name, url, null, 0, 0, 0, 0);			
			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
