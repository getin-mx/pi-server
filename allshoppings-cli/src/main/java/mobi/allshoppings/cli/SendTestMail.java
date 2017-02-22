package mobi.allshoppings.cli;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.mail.MailHelper;
import mobi.allshoppings.model.User;


public class SendTestMail extends AbstractCLI {

	private static final Logger log = Logger.getLogger(SendTestMail.class.getName());

	public static void setApplicationContext(ApplicationContext ctx) {
		context = ctx;
	}

	public static OptionParser buildOptionParser(OptionParser base) {
		if( base == null ) parser = new OptionParser();
		else parser = base;
		parser.accepts( "mail", "Mail Address" ).withRequiredArg().ofType( String.class );
		return parser;
	}

	public static void main(String args[]) throws ASException {
		try {
			// Option parser help is in http://pholser.github.io/jopt-simple/examples.html
			OptionSet options = parser.parse(args);

			String mail = null;
			
			try {
				if( options.has("mail")) mail = (String)options.valueOf("mail");
				else usage(parser);

			} catch( Exception e ) {
				e.printStackTrace();
				usage(parser);
			}

			MailHelper mailHelper = (MailHelper)getApplicationContext().getBean("mail.helper");
			
			log.log(Level.INFO, "Sending test message to " + mail);
    		User user = new User();
    		user.setEmail(mail);
    		mailHelper.sendMessage(user, "Getin Service", "Prueba de Antenas");

			
		} catch( Exception e ) {
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
		System.exit(0);
	}
	
}
