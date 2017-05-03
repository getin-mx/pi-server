package mobi.allshoppings.dao.spi;

import java.io.File;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.inodes.util.FileLoader;

public class DAOJDOPersistentManagerFactory {
	
	private static final PersistenceManagerFactory sharedManagerFactory = JDOHelper.getPersistenceManagerFactory(
			System.getProperty("datastore.configuration") == null
			? new File(FileLoader.getResource("datastore.properties",	FileLoader.PRECEDENCE_SYSTEMPATH).getFile())
			: new File(System.getProperty("datastore.configuration")));
    private static final PersistenceManagerFactory sharedGXManagerFactory = JDOHelper.getPersistenceManagerFactory(
			System.getProperty("datastore.configuration") == null
			? new File(FileLoader.getResource("datastore.properties",	FileLoader.PRECEDENCE_SYSTEMPATH).getFile())
			: new File(System.getProperty("datastore.configuration")));

    private DAOJDOPersistentManagerFactory() {
    }

    public static PersistenceManagerFactory get() {
        return getOptional();
    }

    public static PersistenceManagerFactory getOptional() {
    	return sharedManagerFactory;
    }

    public static PersistenceManagerFactory getGX() {
        return sharedGXManagerFactory;
    }
}
