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

import com.apddemo.core.daos.UserInfoDao;
import com.apddemo.core.services.RestletService;
import com.apddemo.core.services.SSOConfigurationService;


//localhost:4502/bin/sso/userInfo
/*
 * This servlet is called from /etc/designs/usgb/clientlib-site/js/sso.app.js 
 * We would be getting the access_token and domain from the request params which would be used for operation here
 * This class would get the user info.
 */
@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=SSO User Info",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths="+ "/bin/sso/info/user"
})
public class SSOUserInfoServlet extends BaseAllMethodsServlet {
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
				LOG.info("SSOUserInfo Servlet Start");
			try {
				response.setCharacterEncoding("utf-8");
				// getting the header 
				String payLoad = request.getReader().readLine();
				//converting the payload to json for easy readable format
				JSONObject payloadJson = new JSONObject(payLoad);
				//reading the access_token
				String access_token = (String) payloadJson.get("access_token");
				// reading the domain
				String domain = (String) payloadJson.get("domain");
				LOG.debug("String : " + domain);
				
//				UserInfo userInfo = new UserInfo();

				HashMap<String, Object> result = new HashMap<String, Object>();
				
				JSONObject jsonObj = new JSONObject(result);
				// making internal call to validate
				jsonObj = retrieveValueJson(access_token, domain);

//				String sso_id = jsonObj.getString("id");
//				HARDCODE START
//				String sso_id = "XQu+fqxRNthzrF48kwE9LM";
//				String sso_id = "XQu+fqxRNthzrF48kwE9LM123";
//				String sso_id = "DUH/pKfTOq7BxWN86MzsQw";
//				HARDCODE END
//				LOG.info("sso_id : " + sso_id);
//				userInfo.setUser_sso_id(sso_id);
//				String userId = userInfoDao.getUserId(userInfo);
//				
//				LOG.info("userId : " + userId);
				// setting the json response for the request.
				this.setJsonResponseOk(response,jsonObj.toString());
				
			}catch(Exception e){
				this.setJsonResponse(response, "{\"SSOSessionToken\":\"exception"+ e.getMessage() + "\"}", 500);
			}
		
	}
	
	private JSONObject retrieveValueJson(String access_token, String domain) throws JSONException{
		
		JSONObject apiResponse = null;
		// getting the SSO config based on the domain
		Map<String, String> config = ssoConfig.getConfig(domain);
		LOG.info("domain : " + domain);
		
		try {
			
			Map<String, String> parameter = new HashMap<String, String>();
			if(config != null ){
				// setting the parameters required for the making the session validating call 
				parameter.put("client_id", config.get("clientId"));
				parameter.put("client_secret", config.get("clientSecret"));
				parameter.put("access_token", access_token);
				String domainCall = config.get("clientDomain") + "/services/api/GetUserInfo";
				// making the call to restService for validating the details.
				apiResponse = new JSONObject(restService.retrieveRestAPI(domainCall, parameter));
			}else{
				LOG.debug("the config for the domain passed:"+domain+" is null");
			}
//			HardCoded Start
//			apiResponse = getHardcoded();			
//			HardCoded End
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		return apiResponse;
	}
	
	public JSONObject getHardcoded(){
		JSONObject hardcode = new JSONObject();
		JSONObject profile = new JSONObject();
		try {

			profile.put("salutation", "null");
			profile.put("first_name", "Azmirrul Izzat");
			profile.put("last_name", "Abdul Aziz");
			profile.put("address1", "");
			profile.put("address2", "");
			profile.put("address3", "");
			profile.put("postal_code", "");
			profile.put("city", "");
			profile.put("state", "");
			profile.put("country", "");
			profile.put("phone", "");
			profile.put("language", "en");
			profile.put("picture", "https://s3-ap-southeast-1.amazonaws.com/demosso-stg/pictures/photo.jpg");
			profile.put("job_title", "");
			profile.put("company_name", "APD Group SDN BHD");
			profile.put("company_size", "");
			profile.put("industry", "");
			profile.put("segment", "");
			profile.put("updated_time", "20170412062036");
			
			hardcode.put("id", "v7ZfO3nzd6yncm2vO2MoVw");
			hardcode.put("profile", profile);
			hardcode.put("email", "null");
			hardcode.put("error", "null");
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		
		return hardcode;
	}
}