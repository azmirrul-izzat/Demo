package com.apddemo.core.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;

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

import com.apddemo.core.daos.DocumentCollatorDao;
import com.apddemo.core.daos.UserInfoDao;
import com.apddemo.core.entity.UserInfo;

@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=SSO My Submittal - Remove Doc" ,
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths="+ "/bin/sso/mySubmittal/remove"
})
public class SSOMYRemoveDocServlet
  extends BaseAllMethodsServlet
{
  private static final long serialVersionUID = 1452364151988577055L;
  protected final Logger log = LoggerFactory.getLogger(getClass());
  
	
	@Reference
	private DocumentCollatorDao documentCollatorDao;
	
	@Reference
	private UserInfoDao userInfoDao;
  
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
    throws ServerException, IOException
  {
    log.info("SSODCRetrieveServlet Start");
    try {
      response.setCharacterEncoding("utf-8");
      
      String payLoad = request.getReader().readLine();
      JSONObject payloadJson = new JSONObject(payLoad);
      if (!payloadJson.has("user_info"))
      {
        setJsonResponse(response, "{\"error\":\"Missing Parameter user_info \"}", 500);
      }
      else if (!payloadJson.has("document_list"))
      {
        setJsonResponse(response, "{\"error\":\"Missing Parameter document_list \"}", 500);
      }
      else {
        JSONObject user_info = (JSONObject)payloadJson.get("user_info");
        
        JSONArray document_list = (JSONArray)payloadJson.get("document_list");
        
        JSONObject jsonObj = removeDoc(user_info, document_list);
        setJsonResponseOk(response, jsonObj.toString());
      }
      

    }
    catch (Exception e)
    {
      setJsonResponse(response, "{\"SSODCRemoveDoc\":\"exception - " + e.getMessage() + "\"}", 500);
    }
  }
  

  private JSONObject removeDoc(JSONObject userInfoParam, JSONArray documentList)
  {
    JSONObject apiResponse = new JSONObject();
    List<String> removeList = new ArrayList<String>();
    UserInfo userInfo = new UserInfo();
    
    try
    {

        String aemId = (String)userInfoParam.get("user_id");
		userInfo.setUser_sso_id(aemId);
		
		if(userInfoParam.has("first_name")){
			userInfo.setUser_first_name((String)userInfoParam.get("first_name"));
		}
		
		if(userInfoParam.has("last_name")){
			userInfo.setUser_last_name((String)userInfoParam.get("last_name"));
		}
		
		if(userInfoParam.has("email")){
			userInfo.setEmail((String)userInfoParam.get("email"));
		}
		
		if(userInfoParam.has("display_name")){
			userInfo.setDisplay_name((String)userInfoParam.get("display_name"));
		}
		
		String userId = userInfoDao.getUserId(userInfo);;
        
    	for (int i = 0 ; i < documentList.length(); i++) {
    		JSONObject obj = documentList.getJSONObject(i);
    		if(obj.has("document_id")){
    			removeList.add(obj.getString("document_id"));
    		}
    	}
    	if(removeList.size() > 0){
    		boolean result = documentCollatorDao.removeData(userId, removeList);
    		
    		if(result){
    			apiResponse.put("status", "success");
    		}else{
    			apiResponse.put("status", "error");
    		}
    		
    	}else{
    		apiResponse.put("status", "error");
    	}
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return apiResponse;
  }
}

