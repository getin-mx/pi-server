package mobi.allshoppings.mail.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.context.ContextLoader;

import mobi.allshoppings.exception.ASException;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.mail.MailHelper;
import mobi.allshoppings.model.SystemConfiguration;
import mobi.allshoppings.model.User;

public class MailHelperImpl implements MailHelper {

	private static final Logger log = Logger.getLogger(MailHelperImpl.class.getName());

	@Autowired
	private SystemConfiguration systemConfiguration;

	@Override
	public void sendMessage(User user, String subject, String messageContents) throws ASException {

		try {

			Properties props = System.getProperties();
			if( systemConfiguration.getSmtpEncription().equals("tls"))
				props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.ssl.trust", systemConfiguration.getSmtpServer());

			props.setProperty("mail.smtp.port", String.valueOf(systemConfiguration.getSmtpPort()));
			props.setProperty("mail.smtp.host", systemConfiguration.getSmtpServer());
			props.setProperty("mail.smtp.user", systemConfiguration.getSmtpUser());

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
					return new javax.mail.PasswordAuthentication(systemConfiguration.getSmtpUser(), systemConfiguration.getSmtpPassword());
				}
			});

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(systemConfiguration.getMailFrom(), systemConfiguration.getMailFromName()));
			msg.setReplyTo(new Address[] {new InternetAddress(systemConfiguration.getMailFrom(), systemConfiguration.getMailFromName())});
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(user.getEmail(), user.getEmail()));
			msg.setSubject(subject);

			Multipart mp = new MimeMultipart();

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(messageContents, "text/html");
			mp.addBodyPart(htmlPart);
			
			msg.setContent(mp);
			Transport.send(msg);

		} catch (MessagingException | UnsupportedEncodingException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}

	}

	@Override
	public void sendMessage(User user, String subject, String template,
			Map<String, Object> replaceValues) throws ASException {

		if ( replaceValues == null ) replaceValues = new HashMap<String, Object>();
		replaceValues.put("staticContentURL", systemConfiguration.getStaticContentURL());
		replaceValues.put("user", user);

		ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();

		@SuppressWarnings("deprecation")
		String text = VelocityEngineUtils.mergeTemplateIntoString(
				(VelocityEngine)ctx.getBean("velocityEngine"), template, replaceValues);
		sendMessage(user, subject, text);

	}
	
	@Override
	public void sendMessageWithAttachMents(User user, String subject, String message,
			String attachmentPath, String attachmentName) throws ASException {
		try {
			Properties props = System.getProperties();
			if( systemConfiguration.getSmtpEncription().equals("tls"))
				props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.ssl.trust", systemConfiguration.getSmtpServer());

			props.setProperty("mail.smtp.port",
					String.valueOf(systemConfiguration.getSmtpPort()));
			props.setProperty("mail.smtp.host", systemConfiguration.getSmtpServer());
			props.setProperty("mail.smtp.user", systemConfiguration.getSmtpUser());

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
					return new javax.mail.PasswordAuthentication(
							systemConfiguration.getSmtpUser(),
							systemConfiguration.getSmtpPassword());
				}
			});

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(systemConfiguration.getMailFrom(),
					systemConfiguration.getMailFromName()));
			msg.setReplyTo(new Address[] {new InternetAddress(
					systemConfiguration.getReplyTo(),
					systemConfiguration.getMailFromName())});
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(user.getEmail(), user.getEmail()));
			msg.setSubject(subject);

			Multipart mp = new MimeMultipart();

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(message, "text/html");
			mp.addBodyPart(htmlPart);
			
			MimeBodyPart attachment = new MimeBodyPart();
			attachment.setDataHandler(new DataHandler(
					new FileDataSource(attachmentPath)));
			attachment.setFileName(attachmentName);
			mp.addBodyPart(attachment);
			
			msg.setContent(mp);
			Transport.send(msg);

		} catch (MessagingException | UnsupportedEncodingException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw ASExceptionHelper.defaultException(e.getMessage(), e);
		}
	}
	
}
