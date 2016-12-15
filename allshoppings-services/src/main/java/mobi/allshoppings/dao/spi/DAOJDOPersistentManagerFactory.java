package mobi.allshoppings.dao.spi;

import java.io.File;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import com.inodes.util.FileLoader;

public class DAOJDOPersistentManagerFactory {
	
	private static final PersistenceManagerFactory sharedManagerFactory = JDOHelper.getPersistenceManagerFactory(FileLoader.getResource(
			"datastore.properties", FileLoader.PRECEDENCE_SYSTEMPATH) != null 
			? new File(FileLoader.getResource("datastore.properties",	FileLoader.PRECEDENCE_SYSTEMPATH).getFile())
			: new File("/home/getin-dev/workspace/pi-server/datastore.properties"));
    private static final PersistenceManagerFactory sharedGXManagerFactory = JDOHelper.getPersistenceManagerFactory(FileLoader.getResource(
			"datastore.properties", FileLoader.PRECEDENCE_SYSTEMPATH) != null 
			? new File(FileLoader.getResource("datastore.properties",	FileLoader.PRECEDENCE_SYSTEMPATH).getFile())
			: new File("/home/getin-dev/workspace/pi-server/datastore.properties"));

    private DAOJDOPersistentManagerFactory() {
//    	sharedGXManagerFactory.setDetachAllOnCommit(true);
//    	sharedManagerFactory.setDetachAllOnCommit(true);
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
