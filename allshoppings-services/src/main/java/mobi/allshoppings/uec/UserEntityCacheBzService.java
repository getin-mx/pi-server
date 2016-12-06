package mobi.allshoppings.uec;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.User;
import mobi.allshoppings.model.UserEntityCache;
import mobi.allshoppings.model.tools.ViewLocation;

public interface UserEntityCacheBzService {

	UserEntityCache get(User user, int kind, int returnType ) throws ASException;
	UserEntityCache getCountryList() throws ASException;

	void rebuildUsingViewLocation( ViewLocation vl ) throws ASException;
	
	UserEntityCache rebuildUsingUserAndKind( User user, int kind ) throws ASException;
	UserEntityCache rebuildUsingViewLocationAndKind( ViewLocation vl, int kind ) throws ASException;
	
}
