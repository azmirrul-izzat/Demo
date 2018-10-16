package com.apddemo.core.servlets;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.i18n.I18n;
import com.apddemo.core.models.EmailSettings;
import com.apddemo.core.services.EmailService;

//localhost:4502/bin/sso/dcSendEmail

@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=SSO My Submittal - Send Email" ,
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths="+ "/bin/sso/mySubmittal/email"
})
public class SSOMSSendEmailServlet extends BaseAllMethodsServlet {
	private static final long serialVersionUID = 1452364151988577055L;
	protected final Logger log = LoggerFactory.getLogger(this.getClass());


	@Reference
	private EmailService emailService;
	
	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {
				log.info("SSODCSendEmail Servlet Start");
			try {
				response.setCharacterEncoding("utf-8");
				String domain = request.getScheme() + "://" +request.getServerName().replaceAll(".*\\.(?=.*\\.)", "") + ":" + request.getServerPort();
				String payLoad = request.getReader().readLine();
				JSONObject payloadJson = new JSONObject(payLoad);

				
				String bodyTitle = "";
				String sendTo = "";
				String sendFrom = "";
				if(payloadJson != null){
					
					if(!payloadJson.has("email_detail")){
						
						this.setJsonResponse(response, "{\"error\":\"Missing Parameter email_detail \"}", 500);
						
					}else if(!payloadJson.has("document_list")){
						
						this.setJsonResponse(response, "{\"error\":\"Missing Parameter document_list \"}", 500);
						
					}else{
						JSONObject email_detail = (JSONObject) payloadJson.get("email_detail");
						//String user_id = (String) email_detail.get("user_id");
						JSONArray document_list = (JSONArray) payloadJson.get("document_list");
						JSONObject user_info = (JSONObject) payloadJson.get("user_info");
						
						Map<String, String> mapObject = new HashMap<String, String>();
						
//						set sender Name START
						String senderName = "Someone";
						
						if(user_info.has("display_name")){
							senderName = user_info.getString("display_name");
						}else if(user_info.has("first_name")){
							senderName = user_info.getString("first_name");
						}
//						set sender Name END
						
//						set i18n body START
						String countryCode = "en_au";
						
						if(user_info.has("country")){
							countryCode = user_info.getString("country");
						}
						
						Locale currentLocale = new Locale(countryCode);
						ResourceBundle resourceBundle = request.getResourceBundle(currentLocale);
						I18n i18n = new I18n(resourceBundle);
						bodyTitle = senderName + " " + i18n.get("has shared document(s) for you to read") + ";";
//						set i18n body END
						
	//					hardcode value START
						mapObject.put("DOC 1", "google.com");
						mapObject.put("DOC 2", "facebook.com");
						mapObject.put("DOC 3", "w3school.com");
						
						sendTo = "aaziz@apdgroup.com";
						sendFrom = "noreply@demo.com";
						
						
//						bodyTitle = senderName + " has shared document(s) for you to read:";
						
//						bodyTitle = senderName + " " + i18n.get("has shared document(s) for you to read") + ";";
	//					hardcode value END
						
						sendTo = email_detail.getString("sent_to");
						
						mapObject = new HashMap<String, String>();
						for(int i = 0; i < document_list.length(); i++){
	
							JSONObject object = document_list.getJSONObject(i);
							String url = object.getString("document_url");
	
							mapObject.put(object.getString("document_name"), url.replaceAll(" ","%20"));
						}
						String emailBody = generateEmailBodyFromMap(bodyTitle, mapObject);
						
						boolean success = sendEmail(sendTo, emailBody, sendFrom);
						
						if(success){
							this.setJsonResponseOk(response, "{\"status\":\"success\"}");
						}else{
							this.setJsonResponse(response, "{\"status\":\"Not Success\"}", 500);
						}
						
					}
					
				}else{

					this.setJsonResponse(response, "{\"error\":\"Missing Parameter\"}", 500);
				}
				
				
			}catch(Exception e){
				this.setJsonResponse(response, "{\"dcSendEmail\":\"exception - " + e.getMessage() + "\"}", 500);
			}
		
	}
	
	private boolean sendEmail(String emailTo, String emailContent, String sendFrom){
		log.info("In sendEmail");
		EmailSettings emailSettings = new EmailSettings();
		
//		20161202 Izzat - hardCoded email START
		emailSettings.setFromAddress(sendFrom);
		emailSettings.setSubject("USG Boral My Submittals");
		emailSettings.setTemplate(emailContent);
		emailSettings.setToaddress(emailTo.split(";"));
		

//		20161202 Izzat - hardCoded email START
		log.info("emailSettings.....:"+emailSettings.getFromAddress());
		
		
		boolean success = emailService.sendEmail(emailSettings);
		return success;
	}
	
	private String generateEmailBodyFromMap(String bodyTitle, Map<String, String> fileList){
		
		StringBuilder body = new StringBuilder();
		
		body.append("<html>" +
		           "<body>" +
		           "<p> " + bodyTitle + " </p>" +
		           "<table border='0'>");
		int count = 1;
		if(fileList != null){
			for(String key: fileList.keySet()){
				body.append("<tr>");
				body.append("<td>" + count + ". <td>");
				body.append("<td> <a href='" + fileList.get(key) + "'> " + key + "</a><td>");
				body.append("</tr>");
	            count++;
	        }
		}
		body.append("</table>" +
		           "</body>" +
		           "</html>");
		
		return body.toString();
	}
}
