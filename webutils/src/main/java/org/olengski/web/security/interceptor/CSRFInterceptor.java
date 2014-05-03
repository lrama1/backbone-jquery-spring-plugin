package org.olengski.web.security.interceptor;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.olengski.web.security.annotation.CSRFAssignToken;
import org.olengski.web.security.annotation.CSRFValidateToken;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CSRFInterceptor implements HandlerInterceptor {

	private Logger logger = Logger.getLogger(CSRFInterceptor.class);
	
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {		
		if(handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod)handler;
			CSRFValidateToken csrfValidateToken = handlerMethod.getMethod().getAnnotation(CSRFValidateToken.class);
			if(csrfValidateToken != null){
				logger.info("Checking for token.------------------");
				if(request.getSession().getAttribute("VALID_TOKENS") != null){
					List<String> validTokens = (List<String>) request.getSession().getAttribute("VALID_TOKENS");
					boolean isTokenValid =  validTokens.contains(request.getHeader("CSRFToken"));
					logger.info("Token is: " + isTokenValid);
					return isTokenValid;
				}else{
					return false;
				}
			}
			
			CSRFAssignToken csrfAssignToken = handlerMethod.getMethod().getAnnotation(CSRFAssignToken.class);
			if(csrfAssignToken != null){
				response.addHeader("CSRFToken", getNewToken(request.getSession()));
			}
		}
		return true;
	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {		

	}

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}
	
	private String getNewToken(HttpSession httpSession){
		SecureRandom random = new SecureRandom();
		String randomToken =  new BigInteger(130, random).toString(32);
		List<String> tokens = null;
		if(httpSession.getAttribute("VALID_TOKENS") == null){
			tokens = new ArrayList<String>(); 
		}else{
			tokens = (List<String>)httpSession.getAttribute("VALID_TOKENS");
		}
		logger.info("Issuing token: " + randomToken);
		tokens.add(randomToken);
		httpSession.setAttribute("VALID_TOKENS", tokens);
		return randomToken;
	}

}
