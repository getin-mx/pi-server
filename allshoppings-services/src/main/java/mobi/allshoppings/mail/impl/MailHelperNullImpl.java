package mobi.allshoppings.mail.impl;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.mail.MailHelper;
import mobi.allshoppings.model.User;

public class MailHelperNullImpl implements MailHelper {

	@Override
	public void sendMessage(User user, String subject, String message)
			throws ASException {
	}

	/*@Override
	public void sendMessage(User user, String subject, String template,
			Map<String, Object> replaceValues) throws ASException {
	}*/

	@Override
	public void sendMessageWithAttachMents(User user, String subject, String message, String attachmentPath,
			String attachmentName) throws ASException {
	}

}
