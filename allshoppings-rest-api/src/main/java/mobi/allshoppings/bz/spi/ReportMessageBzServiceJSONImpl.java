package mobi.allshoppings.bz.spi;

import java.util.logging.Level;
import java.util.logging.Logger;

import mobi.allshoppings.bz.ReportMessageBzService;
import mobi.allshoppings.bz.RestBaseServerResource;
import mobi.allshoppings.exception.ASExceptionHelper;
import mobi.allshoppings.model.User;
import mobi.allshoppings.push.PushMessageHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class ReportMessageBzServiceJSONImpl extends RestBaseServerResource implements ReportMessageBzService {

	private static final Logger log = Logger.getLogger(ReportMessageBzServiceJSONImpl.class.getName());

	@Autowired
	private PushMessageHelper pushMessageHelper;

	private final static String IDENTIFIER = "identifier";
	private final static String STATUS = "status";

	@Override
	public String post(final JsonRepresentation entity) {
		long start = markStart();
		try {
			// obtain the object
			final JSONObject obj = entity.getJsonObject();

			String identifier = this.getParameters().get(IDENTIFIER);
			String status = obj.getString(STATUS);
			
			log.log(Level.INFO, "Ack for message " + identifier + " and status " + status);
			if( status.equalsIgnoreCase("received")) {
				pushMessageHelper.markAsReceived(identifier, null);
			} else if ( status.equalsIgnoreCase("opened")) {
				pushMessageHelper.markAsOpened(identifier, null);
			} else {
				throw ASExceptionHelper.invalidArgumentsException("status");
			}
			
			// track action
			trackerHelper.enqueue( (User)null, getRequestIP(),
					getRequestAgent(), getFullRequestURI(),
					getI18NMessage("es_AR", "service.PushMessageLog." + status ), 
					null, null);

			return generateJSONOkResponse().toString();

		} catch (JSONException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(ASExceptionHelper.defaultException(e.getMessage(), e)).toString();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			return getJSONRepresentationFromException(e).toString();
		} finally {
			markEnd(start);
		}
	}

}
