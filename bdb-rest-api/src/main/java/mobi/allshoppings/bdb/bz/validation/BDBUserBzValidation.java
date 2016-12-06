package mobi.allshoppings.bdb.bz.validation;

import mobi.allshoppings.dao.UserDAO;
import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.User;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class contains others validation to do when the User is created. More
 * validations are in CommonValidator or BaeUserBzService.validateFields
 * */
public class BDBUserBzValidation {

	@Autowired
    private UserDAO userDao;
	
	public boolean validateUniqueMail(User user) throws ASException {
		
		User otherUser = userDao.getByEmail(user.getContactInfo().getMail());
		if	(null == otherUser) {
			return true;
		}
		if	(user.getIdentifier().equals(otherUser.getIdentifier())) {
			return true;
		}
		
		return false;
	}
}
