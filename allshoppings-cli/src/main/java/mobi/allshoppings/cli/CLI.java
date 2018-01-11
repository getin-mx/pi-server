package mobi.allshoppings.cli;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.inodes.util.FileLoader;

import joptsimple.OptionParser;
import joptsimple.OptionSet;


public class CLI {

	private static final Logger logger = Logger.getLogger(CLI.class);

	static {
		try {
			try {
				URL url = FileLoader.getResource("log4j.properties", FileLoader.PRECEDENCE_SYSTEMPATH);
				PropertyConfigurator.configure(url);
			} catch( Exception e ) {}
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
		System.setProperty("log4j.configurationFile", FileLoader.getResource("log4j2.xml", FileLoader.PRECEDENCE_SYSTEMPATH).getFile());

		if (System.getProperty("dynamic.loader") == null ) {
			System.setProperty("dynamic.loader", "lib");
		}

		String className;
		Class<?> clazz;
		if(!args[0].contains("\\.")) {
			className = "mobi.allshoppings.cli." + args[0];
		} else {
			className = args[0];
		} try {
			clazz = (Class<?>)Class.forName(className);
		} catch(ClassNotFoundException e) {
			className = "mx.getin.cli." +args[0];
			clazz = (Class<?>)Class.forName(className);
		}
		List<String> newArgList = new ArrayList<String>();
		for( int i = 1; i < args.length; i++ ) {
			newArgList.add(args[i]);
		}

		OptionParser parser = new OptionParser();
		parser.accepts( "datastore", "Datastore configuration properties file. Defauts to $ALLSHOPPINGS/etc/datastore.properties" ).withRequiredArg().ofType( String.class );
		parser.accepts( "appcontext", "Application Context File. Defauts to $ALLSHOPPINGS/etc/baseApplicationContext.xml" ).withRequiredArg().ofType( String.class );
		parser.accepts( "nocontext", "Don't use an application context. This is used for StartServer" );
		parser.accepts( "help", "Shows this help message" );

		Method buildOptionParser = clazz.getDeclaredMethod("buildOptionParser", OptionParser.class);
		buildOptionParser.invoke(null, new Object[] {parser});

		OptionSet options = parser.parse(args);

		if( options.has("datastore")) {
			System.setProperty("datastore.configuration", options.valueOf("datastore").toString());
			logger.log(Level.INFO, "Setting datastore configuration to " + System.getProperty("datastore.configuration"));
		}
		
		String appcontext = null;
		if( options.has("appcontext")) {
			appcontext = options.valueOf("appcontext").toString();
		}

		final ConfigurableApplicationContext context = options.has("nocontext") ? null :
			new FileSystemXmlApplicationContext( appcontext == null 
			? "/" + FileLoader.getResource("baseApplicationContext.xml",
					FileLoader.PRECEDENCE_SYSTEMPATH).getFile()
					: "/" + appcontext );


		try {
			Method setApplicationContext = clazz.getDeclaredMethod("setApplicationContext", ApplicationContext.class);
			setApplicationContext.invoke(null, new Object[] {context});
		} catch( Throwable t ) {}

		if( options.has("help")) {
			try {
				AbstractCLI.usage(parser);
			} catch( Exception e ) {}
		} else {
			try {
				Method m = clazz.getDeclaredMethod("main", String[].class);
				m.invoke(null, new Object[] {newArgList.toArray(new String[newArgList.size()])});
			} catch( Throwable t ) {
				t.printStackTrace();
			}
		}

		System.exit(0);
	}
}
