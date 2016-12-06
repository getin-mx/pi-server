package mobi.allshoppings.jetty;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.remote.JMXServiceURL;

import org.eclipse.jetty.jmx.ConnectorServer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.webapp.WebAppContext;

import com.inodes.util.FileLoader;

public class JettyServer {

	private final static Integer DEFAULT_PORT = 8080; 

	public static void startup(Integer connectorPort, Integer jmxPort, List<String> contexts) throws Exception {

		// Defines server and port
		Server server;
		if( connectorPort != null && connectorPort > 0 ) {
			server = new Server(connectorPort);
		} else {
			server = new Server(DEFAULT_PORT);
		}

		if( jmxPort != null && jmxPort > 0 ) {
			// Setup JMX
			MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
			server.addEventListener(mbContainer);
			server.addBean(mbContainer);
			server.addBean(Log.getLog());
			ConnectorServer jmxConnector = new ConnectorServer(new JMXServiceURL("rmi", null, jmxPort, "/jndi/rmi://localhost:" + jmxPort + "/jmxrmi"), "org.eclipse.jetty.jmx:name=rmiconnectorserver");
			jmxConnector.start();
		}

		if( contexts == null || contexts.size() == 0 || contexts.contains("appv2")) {

			WebAppContext webapp = new WebAppContext();
			webapp.setContextPath("/appv2");
			File warFile = new File(FileLoader.getResource("allshoppings-rest-api.war").getFile());
			webapp.setWar(warFile.getAbsolutePath());

			WebAppContext webapp2 = new WebAppContext();
			webapp2.setContextPath("/main-be");
			File warFile2 = new File(FileLoader.getResource("allshoppings-main-be.war").getFile());
			webapp2.setWar(warFile2.getAbsolutePath());

			WebAppContext webapp3 = new WebAppContext();
			webapp3.setContextPath("/bdb");
			File warFile3 = new File(FileLoader.getResource("bdb-rest-api.war").getFile());
			webapp3.setWar(warFile3.getAbsolutePath());

			ContextHandlerCollection multi = new ContextHandlerCollection();
			multi.setHandlers(new Handler[] { webapp, webapp2, webapp3 });

			server.setHandler(multi);
		}
		// Starts the server up
		server.start();
		server.join();
	}
}
