package mobi.allshoppings.mail.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import mobi.allshoppings.mail.MailHelper;
import mobi.allshoppings.model.User;

@ContextConfiguration(locations = {"../../test/testApplicationContext.xml"})
public class MailHelperTester extends AbstractJUnit4SpringContextTests {

    @Autowired
    private MailHelper mailHelper;

    @Test
    public void testMail01() {
    	try {
    		User user = new User();
    		user.setEmail("mhapanowicz@gmail.com");
    		mailHelper.sendMessage(user, "AllShoppings Service", "Prueba de Antenas");
    	} catch(Throwable e ) {
    		e.printStackTrace();
    	}
    }
}