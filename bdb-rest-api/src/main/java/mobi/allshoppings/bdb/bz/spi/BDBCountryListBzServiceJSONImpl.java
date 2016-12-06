package mobi.allshoppings.bdb.bz.spi;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.inodes.util.CollectionFactory;

import mobi.allshoppings.bdb.bz.BDBListBzService;
import mobi.allshoppings.bdb.bz.BDBRestBaseServerResource;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.adapter.NameAndIdAdapter;

public class BDBCountryListBzServiceJSONImpl extends BDBRestBaseServerResource implements BDBListBzService {

	private final static Logger log = Logger.getLogger(BDBCountryListBzServiceJSONImpl.class.getName()); 
	
	/**
	 * Obtains information about a user
	 * 
	 * @return A JSON representation of the selected fields for a user
	 */
	@Override
	public String list()
	{
		long start = markStart();
		JSONObject returnValue;
		try {
			// obtain the id and validates the auth token
			User user = getUserFromToken();

			List<NameAndIdAdapter> countries = CollectionFactory.createList();
			countries.add(new NameAndIdAdapter("argentina","Argentina",null));
			countries.add(new NameAndIdAdapter("mexico","México",null));
			countries.add(new NameAndIdAdapter("panama","Panamá",null));
			
			// Get the output fields
			String[] fields = this.obtainOutputFields(NameAndIdAdapter.class, null);

			// Obtains the user JSON representation
			returnValue = getJSONRepresentationFromArrayOfObjects(countries, fields);

			// track action
			trackerHelper.enqueue( user, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.CountryListBzService"), 
					null, null);

		} catch (ASException e) {
			if( e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENEXPIRED_CODE || 
					e.getErrorCode() == ASExceptionHelper.AS_EXCEPTION_AUTHTOKENMISSING_CODE) {
				log.log(Level.INFO, e.getMessage());
			} else {
				log.log(Level.SEVERE, e.getMessage(), e);
			}
			returnValue = getJSONRepresentationFromException(e);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			returnValue = getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e));
		} finally {
			markEnd(start);
		}
		return returnValue.toString();
	}

}
