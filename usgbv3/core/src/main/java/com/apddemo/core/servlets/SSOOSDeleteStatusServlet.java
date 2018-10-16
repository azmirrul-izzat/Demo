package com.apddemo.core.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.rmi.ServerException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apddemo.core.daos.OrderSampleDao;
import com.apddemo.core.daos.UserInfoDao;
import com.apddemo.core.entity.OrderInfo;
import com.apddemo.core.entity.UserInfo;
import com.apddemo.core.models.EmailSettings;
import com.apddemo.core.services.EmailService;


@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=SSO Order Sample - Cancel Order" ,
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths="+ "/bin/sso/orderSample/cancel"
})
public class SSOOSDeleteStatusServlet
  extends BaseAllMethodsServlet
{
  private static final long serialVersionUID = 1452364151988577055L;
  protected final Logger log = LoggerFactory.getLogger(getClass());
  

	@Reference
	private OrderSampleDao orderSampleDao;
	
	@Reference
	private EmailService emailService;
	
	@Reference
	private UserInfoDao userInfoDao;
  
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
    throws ServerException, IOException
  {
    log.info("SSOOSUpdateStatus Start");
    try {
      response.setCharacterEncoding("utf-8");
      
      String payLoad = request.getReader().readLine();
      JSONObject payloadJson = new JSONObject(payLoad);
      if (!payloadJson.has("user_info"))
      {
        setJsonResponse(response, "{\"error\":\"Missing Parameter user_info \"}", 500);
      }
      else if (!payloadJson.has("order_info"))
      {
        setJsonResponse(response, "{\"error\":\"Missing Parameter order_info \"}", 500);
      }
      else {
        JSONObject user_info = (JSONObject)payloadJson.get("user_info");
        JSONObject order_info = (JSONObject)payloadJson.get("order_info");
        
        JSONObject jsonObj = deleteOrder(user_info, order_info);
        setJsonResponseOk(response, jsonObj.toString());
      }
      

    }
    catch (Exception e)
    {
      setJsonResponse(response, "{\"osUpdateStatus\":\"exception - " + e.getMessage() + "\"}", 500);
    }
  }
  

  private JSONObject deleteOrder(JSONObject user_info, JSONObject order_info)
  {
    JSONObject apiResponse = new JSONObject();
    UserInfo userInfo = new UserInfo();
    String userId = "";
    
    try
    {
    	String aemId = (String)user_info.get("user_id");
    	userInfo.setUser_sso_id(aemId);
    	String orderId = (String) order_info.get("order_id");
    	String status = "Cancelled";
    	
    	userId = userInfoDao.getUserId(userInfo);
    	
    	boolean successUpdate = orderSampleDao.UpdateData(userId, orderId, status);
    	
    	if(successUpdate){
    		
//    		String emailBody = generateEmailBodyFromMap(orderId);
//    		String sendFrom = "noreply@demo.com";
    		apiResponse.put("status", "success");
    	}else{
    		apiResponse.put("status", "error");
    	}
    	
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return apiResponse;
  }
	
	private boolean sendEmail(String emailTo, String emailContent, String sendFrom){
		log.info("In sendEmail");
		EmailSettings emailSettings = new EmailSettings();
		
//		20161202 Izzat - hardCoded email START
		emailSettings.setFromAddress(sendFrom);
		emailSettings.setSubject("Test Email Template");
		emailSettings.setTemplate(emailContent);
		emailSettings.setToaddress(emailTo.split(";"));
		

//		20161202 Izzat - hardCoded email START
		log.info("emailSettings.....:"+emailSettings.getFromAddress());
		
		
		boolean success = emailService.sendEmail(emailSettings);
		return success;
	}
	
	private String generateEmailBodyFromMap(String orderId){
		
		StringBuilder body = new StringBuilder();
		String datePattern = "EEEEE MMMMM dd, yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);

		body.append("<html>" +
		           "<body><p>" +
		           "<br/><br/>Your Order # <b>" + orderId + "</b> has been Cancelled on <b>" + simpleDateFormat.format(new Date()) + "</b>" +
		           "</p><table border='0'>");
		
		body.append("</table>" +
		           "</body>" +
		           "</html>");
		
		return body.toString();
	}
}