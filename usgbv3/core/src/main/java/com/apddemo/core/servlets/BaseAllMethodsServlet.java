package com.apddemo.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseAllMethodsServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5965662347987258302L;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Automatically forwards the request based on the request parameter named "fPath".
	 * Catches and logs Exceptions that occur
	 * 
	 * @param request
	 * @param response
	 */
	protected void paramBasedReqestForward(SlingHttpServletRequest request,
			SlingHttpServletResponse response){
		
		try {
			String forwardPath = (String) request.getParameter("fPath");
			if(log.isDebugEnabled()){
				log.debug(String.format("paramBasedReqestForward: forwardPath[%s]", forwardPath));
			}
			request.getRequestDispatcher(forwardPath).forward(request, response);
		} catch (ServletException e) {
			log.error(String.format("paramBasedReqestForward: encountered ServletException", e));
		} catch (IOException e) {
			log.error(String.format("paramBasedReqestForward: encountered IOException", e));
		}
	}
	
	/**
	 * Sets the response format to application/json and the JSON string response output
	 * with the status code 200 (OK)
	 * 
	 * @param response
	 * @param json
	 */
	protected void setJsonResponseOk(SlingHttpServletResponse response, String json){
		setJsonResponse(response, json, 200); //TODO should come from an Enum or a constant
	}
	
	/**
	 * Sets the response format to application/json and the JSON string response output
	 * with the status code from given httpStatusCode parameter
	 * 
	 * @param response
	 * @param json
	 * @param httpStatusCode
	 */
	protected void setJsonResponse(SlingHttpServletResponse response, String json, int httpStatusCode){
		try {
			if(log.isDebugEnabled()){
				log.debug(String.format("setJsonResponseOk: json[%s]", json));
			}
			response.setStatus(httpStatusCode);
			response.setContentType("application/json"); //TODO get from enum or constant
			PrintWriter out;
			out = response.getWriter();
			out.print(json);
		} catch (IOException e) {
			log.error(String.format("setJsonResponseOk: encountered IOException", e));
		}
	}
}
