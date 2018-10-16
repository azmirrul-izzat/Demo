package com.apddemo.core.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.rmi.ServerException;

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
import com.apddemo.core.entity.DocumentCollator;
import com.apddemo.core.entity.UserInfo;

@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=SSO My Submittal - Add Doc" ,
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths="+ "/bin/sso/mySubmittal/add"
})
public class SSOMSAddDocServlet
  extends BaseAllMethodsServlet
{
  private static final long serialVersionUID = 1452364151988577055L;
  protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Reference
	private UserInfoDao userInfoDao;
	
	@Reference
	private DocumentCollatorDao documentCollatorDao;
	
  
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
    throws ServerException, IOException
  {
    log.info("SSODCAddDoc Start");
    try {
      response.setCharacterEncoding("utf-8");
      
      String payLoad = request.getReader().readLine();
      JSONObject payloadJson = new JSONObject(payLoad);
      if(payloadJson != null){
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
            
            JSONObject jsonObj = addDoc(user_info, document_list);
            setJsonResponseOk(response, jsonObj.toString());
          }
      }else{
    	  setJsonResponse(response, "{\"error\":\"Missing Parameter \"}", 500);
      }
      
      

    }
    catch (Exception e)
    {
      setJsonResponse(response, "{\"dcAddDoc\":\"exception - " + e.getMessage() + "\"}", 500);
    }
  }
  

  private JSONObject addDoc(JSONObject userInfoParam, JSONArray documentList)
  {
    JSONObject apiResponse = new JSONObject();
    
    UserInfo userInfo = new UserInfo();
	DocumentCollator documentCollator = null;
	String aem_id = "";
	String userId = "";
    
    try
    {
        aem_id = (String) userInfoParam.get("user_id");
    	userInfo.setUser_sso_id(aem_id);
		
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
		
    	userId = userInfoDao.getUserId(userInfo);
    	int success = 0;
		
		for (int i = 0 ; i < documentList.length(); i++) {
            JSONObject obj = documentList.getJSONObject(i);
            documentCollator = new DocumentCollator();
            if(obj.has("document_id")){
            	log.info("addDoc document_id : " + obj.getString("document_id"));
                documentCollator.setDoc_aem_id(obj.getString("document_id"));
            }
        	
            documentCollator.setDoc_name(obj.getString("document_name"));
            documentCollator.setDoc_url(obj.getString("document_url"));
            documentCollator.setDoc_path(obj.getString("document_path"));
            documentCollator.setUser_id(userId);
        	log.info("documentCollator : " + documentCollator.toString());
        	log.info("documentCollatorDao : " + documentCollatorDao);
            success = documentCollatorDao.insertData(documentCollator);
        }
    	
		if(success > 0){
			apiResponse.put("status", "success");
		}else if(success == -999){
			apiResponse.put("status", "error");
			apiResponse.put("error", "Duplication");
		}else{
			apiResponse.put("status", "error");
		}
    	
    } catch (Exception e) {
    	log.info("addDoc ERROR : " + e.getMessage());
      e.printStackTrace();
    }
    
    return apiResponse;
  }
}