package mobi.allshoppings.dao.spi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import mobi.allshoppings.dao.ImageDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.Image;

import com.inodes.datanucleus.model.Key;

public class ImageDAOJDOImpl extends GenericDAOJDO<Image> implements ImageDAO {

	private static final Logger log = Logger.getLogger(ImageDAOJDOImpl.class.getName());

	public ImageDAOJDOImpl() {
		super(Image.class);
	}

	@Override
	public synchronized Image associateByOriginalNameAndSession(String originalName, String session, Key owner, String deleteId) throws ASException {
		if( originalName == null || session == null || owner == null ) {
			throw ASExceptionHelper.notAcceptedException();
		}
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {
			
			Map<String, Object> parms = new HashMap<String, Object>();
			Query query = pm.newQuery(Image.class);
			query.setFilter("sessionKey == parmSessionKey && originalFileName == parmOriginalName && owner == null");
			query.setOrdering("creationDate desc");
			
			parms.put("String parmSessionKey", session);
			parms.put("String parmOriginalName", originalName);
			@SuppressWarnings("unchecked")
			List<Image> images = (List<Image>) query.executeWithMap(parms);

			Image obj = null;
			
			if( images.size() > 0 ) {
				pm.currentTransaction().begin();
				obj = images.get(0);
				obj.setOwner(owner);
				pm.makePersistent(obj);
				pm.currentTransaction().commit();
			} else {
				throw ASExceptionHelper.notFoundException();
			}

			if( deleteId != null ) {
				try {
					Image toDelete = get(deleteId);
					pm.currentTransaction().begin();
					pm.makePersistent(toDelete);
					pm.deletePersistent(toDelete);
					pm.currentTransaction().commit();
				} catch( ASException e ) {
					// Do nothing for now
				}
			}

			return obj;
			
		}catch(ASException ASException){
			if(pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASException;
		}catch(Exception e){
			if(pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}finally{
			pm.close();
		}
	}

	@Override
	public synchronized Image associateByIdentifier(String identifier, Key owner, String deleteId) throws ASException {
		if( identifier == null ) {
			throw ASExceptionHelper.notAcceptedException();
		}
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {
			Image obj = get(identifier, true);
			pm.currentTransaction().begin();
			obj.setOwner(owner);
			pm.makePersistent(obj);
			pm.currentTransaction().commit();

			if( deleteId != null ) {
				try {
					Image toDelete = get(deleteId);
					pm.currentTransaction().begin();
					pm.makePersistent(toDelete);
					pm.deletePersistent(toDelete);
					pm.currentTransaction().commit();
				} catch( ASException e ) {
					// Do nothing for now
				}
			}

			return obj;

		}catch(ASException ASException){
			if(pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASException;
		}catch(Exception e){
			if(pm.currentTransaction().isActive()){
				pm.currentTransaction().rollback();
			}
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}finally{
			pm.close();
		}
	}

	@Override
	public Key createKey(String fileName) throws ASException {
		return keyHelper.createStringUniqueKey(Image.class, UUID.randomUUID().toString() 
				+ "." + fileName.split("\\.")[fileName.split("\\.").length - 1]);
	}
	
	@Override
	public Key forceKey(String fileName) throws ASException {
		return keyHelper.obtainKey(Image.class, fileName);
	}
	
	@Override
	public Image getByOriginalName(String name, Boolean detachable) throws ASException {

		if (name == null) {
			log.info("not accepted:id null");
			throw ASExceptionHelper.notAcceptedException();
		}
		
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(clazz);
			query.setFilter("originalFileName == \"" + name + "\"");
			@SuppressWarnings("unchecked")
			List<Image> objs = (List<Image>)query.execute();
			// force to read
			for (Image obj : objs) {
				if( detachable == true ) {
					return pm.detachCopy(obj);
				} else {
					return obj;
				}
			}
			throw new JDOObjectNotFoundException();
		} catch(Exception e) {
			if (!(e instanceof JDOObjectNotFoundException)) {
				log.log(Level.SEVERE, "exception catched", e);
			}
			throw ASExceptionHelper.notFoundException();
	    } finally  {
			pm.close();
	    }
	}

	@Override
	public Image getBySource(String source) throws ASException {
		if (source == null) {
			log.info("not accepted:id null");
			throw ASExceptionHelper.notAcceptedException();
		}
		
		PersistenceManager pm = DAOJDOPersistentManagerFactory.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(clazz);
			query.setFilter("source == \"" + source + "\"");
			@SuppressWarnings("unchecked")
			List<Image> objs = (List<Image>)query.execute();
			// force to read
			for (Image obj : objs) {
				return pm.detachCopy(obj);
			}
			throw new JDOObjectNotFoundException();
		} catch(Exception e) {
			if (!(e instanceof JDOObjectNotFoundException)) {
				log.log(Level.SEVERE, "exception catched", e);
			}
			throw ASExceptionHelper.notFoundException();
	    } finally  {
			pm.close();
	    }
	}


}
