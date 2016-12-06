package mobi.allshoppings.mail;

import java.util.Map;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.model.User;

public interface MailHelper {

	void sendMessage(User user, String subject, String message ) throws ASException;
	void sendMessage(User user, String subject, String template, Map<String, Object> replaceValues) throws ASException;
}
