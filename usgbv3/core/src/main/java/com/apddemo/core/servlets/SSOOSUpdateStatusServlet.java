package com.apddemo.core.servlets;

import java.io.IOException;
import java.rmi.ServerException;

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



@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=SSO Order Sample - Update Status" ,
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths="+ "/bin/sso/orderSample/update"
})
public class SSOOSUpdateStatusServlet
  extends BaseAllMethodsServlet
{
  private static final long serialVersionUID = 1452364151988577055L;
  protected final Logger log = LoggerFactory.getLogger(getClass());
  
	
	@Reference
	private OrderSampleDao orderSampleDao;
  
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
        
        JSONObject jsonObj = updateOrder(user_info, order_info);
        setJsonResponseOk(response, jsonObj.toString());
      }
      

    }
    catch (Exception e)
    {
      setJsonResponse(response, "{\"osUpdateStatus\":\"exception - " + e.getMessage() + "\"}", 500);
    }
  }
  

  private JSONObject updateOrder(JSONObject user_info, JSONObject order_info)
  {
	  
	  
	  JSONObject apiResponse = new JSONObject();
    
    try
    {
    	String userId = (String)user_info.get("user_id");
    	String orderId = (String) order_info.get("order_id");
    	String status = (String) order_info.get("status");
    	
    	
    	boolean successUpdate = orderSampleDao.UpdateData(userId, orderId, status);
    	
    	if(successUpdate){
    		apiResponse.put("status", "success");
    	}else{
    		apiResponse.put("status", "error");
    	}
    	
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return apiResponse;
  }
}