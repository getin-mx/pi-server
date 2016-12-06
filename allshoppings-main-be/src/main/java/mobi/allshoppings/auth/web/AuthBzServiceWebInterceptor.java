package mobi.allshoppings.auth.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.allshoppings.bz.web.tools.SpringVelocityHelper;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

public class AuthBzServiceWebInterceptor
extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		Locale loc = RequestContextUtils.getLocale(request);
		request.getSession().setAttribute("lang", loc.toString());

		if (SpringVelocityHelper.isUserLoggedIn(request)) {
			return true;
		}

		// User is not logged in
//		if (handler instanceof LogInWebController
//				|| handler instanceof PasswordRecoveryWebController
//				|| handler instanceof CustomPromotionWebController) {
			// Redirection has already taken place
			return true;
//		}

//		response.sendRedirect("/main-be/login");
//
//		return false;
	}


}
