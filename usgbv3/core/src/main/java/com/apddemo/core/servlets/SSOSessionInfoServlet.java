package com.apddemo.core.servlets;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apddemo.core.services.RestletService;
import com.apddemo.core.services.SSOConfigurationService;


/*
 * This servlet is called from /etc/designs/usgb/clientlib-site/js/sso.app.js 
 * We would be getting the session_token and domain from the request params which would be used for operation here
 * This class would validate the session info.
 */
@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=SSO Session Info",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths="+ "/bin/sso/info/session"
})
public class SSOSessionInfoServlet extends BaseAllMethodsServlet {
	private static final long serialVersionUID = 1452364151988577055L;
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
	

	@Reference
	private SSOConfigurationService ssoConfig;
	
	@Reference
	private RestletService restService;
	
	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {
				LOG.info("SSOSessionInfo Servlet Start");
			try {
				response.setCharacterEncoding("utf-8");
				// getting the header 
				String payLoad = request.getReader().readLine();
				//converting the payload to json for easy readable format
				JSONObject payloadJson = new JSONObject(payLoad);

				LOG.info("payloadJson : " + payloadJson.toString());
				// reading the session_token
				String session_token = (String) payloadJson.get("session_token");
				LOG.info("session_token : " + session_token);
				// reading the domain
				String domain =  (String) payloadJson.get("domain");
				LOG.info("domain : " + domain);
								
				// making internal call to validate
				JSONObject jsonObj = retrieveValueJson(session_token, domain);
				// setting the json response for the request.
				this.setJsonResponseOk(response,jsonObj.toString());
				
			}catch(Exception e){
				this.setJsonResponse(response, "{\"SSOSessionInfo\":\"exception "+ e.getMessage() + "\"}", 500);
			}
		
	}
	
	/*
	 * this function would call the restservice which would make a call for validating the session
	 */
	private JSONObject retrieveValueJson(String session_token, String domain){
		
		JSONObject apiResponse = null;
		LOG.info("retrieveValueJson START");
		// getting the SSO config based on the domain
		Map<String, String> config = ssoConfig.getConfig(domain);
		LOG.info("domain : " + domain);
		LOG.info("clientId : " + config.get("clientId"));
		LOG.info("clientSecret : " + config.get("clientSecret"));
		try {
			Map<String, String> parameter = new HashMap<String, String>();
	
			if(config != null ){
				// setting the parameters required for the making the session validating call 
				parameter.put("client_id", config.get("clientId"));
				parameter.put("client_secret", config.get("clientSecret"));
				parameter.put("session_token", session_token);
				String domainCall = config.get("clientDomain") + "/services/api/GetSessionInfo";
				// making the call to restService for validating the details.
				apiResponse = new JSONObject(restService.retrieveRestAPI(domainCall, parameter));
			}else{
				LOG.info("the config for the domain passed:"+domain+" is null");
			}
			
			
//			HardCoded Start
//			apiResponse = getHardcoded();
//			HardCoded End
		} catch (Exception e) {
			LOG.error("Exception in retrieveValueJson : "+ e);
		}
		return apiResponse;
	}
	
	/*
	 * This function is purely for test purspose. When needed for a hard coded response.
	 */
	public JSONObject getHardcoded(){
		JSONObject hardcode = new JSONObject();
		try {
			hardcode.put("status", "logged_in");
			hardcode.put("first_name", "Azmirrul Izzat");
			hardcode.put("last_name", "Abdul Aziz");
			hardcode.put("error", "null");
		} catch (JSONException e) {
			LOG.error("JSONException : in get HardCoded:"+e);
		}
		return hardcode;
	}
}