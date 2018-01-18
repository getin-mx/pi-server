package mobi.allshoppings.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import mobi.allshoppings.dao.SystemStatusDAO;
import mobi.allshoppings.dao.spi.SystemStatusDAOJDOImpl;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.EntityKind;
import mobi.allshoppings.model.SystemStatus;
import mobi.allshoppings.model.interfaces.ModelKey;

public class SystemStatusService {
	
	private static final Logger log = Logger.getLogger(SystemStatusService.class.getName());

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private static final long TIME_LIMIT = 3000; //seconds

	@Autowired
	private SystemStatusDAO dao;

	public SystemStatusService() {
		if( dao == null ) dao = new SystemStatusDAOJDOImpl();
	}
	
	public void updateSystemStatusLastTimestamp(ModelKey obj) {
		SystemStatus stat;
		String identifier = obj.getClass().getName() + "_lastUpdate";
		
		// Adds 20 seconds to avoid problems with UEC
		Date d = new Date();
		d.setTime(d.getTime() + 20000);

		try {
			stat = dao.get(identifier, true);
			Date other = sdf.parse(stat.getData());
			if(( d.getTime() - other.getTime()) > TIME_LIMIT ) {
				stat.setData(sdf.format(d));
				dao.createOrUpdate(stat);
			}
		} catch( ASException e ) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE ) {
				stat = new SystemStatus();
				try {
					stat.setKey(dao.createKey(obj.getClass().getName() + "_lastUpdate"));
					stat.setData(sdf.format(new Date()));
					dao.createOrUpdate(stat);
				} catch( Throwable t ) {
					log.log(Level.SEVERE, t.getMessage(), t);
				}
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		} catch( NumberFormatException e ) {
			// Do nothing here!
		} catch( Throwable t ) {
			log.log(Level.SEVERE, t.getMessage(), t);
		}
	}
	
	public Date getLastUpdate(byte entityKind) {
		return getLastUpdate(EntityKind.getClassByKind(entityKind));
	}
	
	public Date getLastUpdate(Class<?> clazz) {
		try {
			String identifier = clazz.getName() + "_lastUpdate";
			SystemStatus stat = dao.get(identifier, true); 
			if( stat != null && StringUtils.hasText(stat.getData()))
				try {
					return(sdf.parse(stat.getData()));
				} catch( NumberFormatException e ) {
					return new Date(0);
				}
		} catch( ASException e ) {
			if( e.getErrorCode() != ASExceptionHelper.AS_EXCEPTION_NOTFOUND_CODE) {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
		} catch( Throwable t ) {
			log.log(Level.SEVERE, t.getMessage(), t);
		}
		return new Date(0);
	}
}
