package com.apddemo.core.servlets;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

import com.google.gson.Gson;
import com.apddemo.core.daos.DocumentCollatorDao;
import com.apddemo.core.daos.UserInfoDao;
import com.apddemo.core.entity.DocumentCollator;
import com.apddemo.core.entity.UserInfo;

//localhost:4502/bin/sso/dcRetrieveHistory

@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=SSO My Submittal - Retrieve History" ,
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths="+ "/bin/sso/mySubmittal/retrieve"
})
public class SSOMSRetrieveServlet extends BaseAllMethodsServlet {
	private static final long serialVersionUID = 1452364151988577055L;
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	
	@Reference
	private UserInfoDao userInfoDao;
	
	@Reference
	private DocumentCollatorDao documentCollatorDao;
	
	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {
				log.info("SSODCRetrieveServlet Start");
			try {
				response.setCharacterEncoding("utf-8");
				
				String payLoad = request.getReader().readLine();
				JSONObject payloadJson = new JSONObject(payLoad);
				
				 if(payloadJson != null){
			    	  if (!payloadJson.has("user_info"))
			          {
			            setJsonResponse(response, "{\"error\":\"Missing Parameter user_info \"}", 500);
			          }
			          else {
			            JSONObject user_info = (JSONObject)payloadJson.get("user_info");
			            String aemId = (String)user_info.get("user_id");
			            
			            JSONObject jsonObj = retrieveValueJson(aemId);
			            setJsonResponseOk(response, jsonObj.toString());
			          }
			      }else{
			    	  setJsonResponse(response, "{\"error\":\"Missing Parameter \"}", 500);
			      }
				
				
			}catch(Exception e){
				this.setJsonResponse(response, "{\"SSODCRetrieveServlet\":\"exception\"}", 500);
			}
		
	}
	
	private JSONObject retrieveValueJson(String aem_id){
		
		JSONObject apiResponse = new JSONObject();
		Gson gson = new Gson();
		JSONArray jsonArray = new JSONArray();
//		ObjectMapper mapper = new ObjectMapper();
		List<DocumentCollator> dcList = new ArrayList<DocumentCollator>();
		UserInfo userInfo = new UserInfo();
		String userId = "";
		try {
//	    	userInfo.setUser_sso_id(aem_id);
//	    	userId = userInfoDao.getUserId(userInfo);
	    	log.info("aem_id : " + aem_id);
	    	
	    	dcList = documentCollatorDao.getDocumentListbySsoId(aem_id);
	    	
	    	log.info("dcList : " + dcList);
	    	
	    	for(DocumentCollator documentCollator : dcList){
	    		
	    		JSONObject dc1Json = new JSONObject(gson.toJson(documentCollator));
				jsonArray.put(dc1Json);
	    	}
	    	
	    	
//			DocumentCollator dc1 = new DocumentCollator();
//			dc1.setDoc_aem_id("");
//			dc1.setDoc_id("DC000001");
//			dc1.setDoc_name("Echostop Installation and Layout Guide");
//			dc1.setDoc_path("/content/dam/demo/Australia/Website/Documents/English/installation-guide/13654_USG_Echostop_Install%20Guide_LR.pdf");
//			dc1.setDoc_url("http://localhost:4502/content/dam/demo/Australia/Website/Documents/English/installation-guide/13654_USG_Echostop_Install%20Guide_LR.pdf");
//			dc1.setCreated_date(new Date());
//			dc1.setUser_id("admin");
////			dcList.add(dc1);
//			JSONObject dc1Json = new JSONObject(gson.toJson(dc1));
//			jsonArray.put(dc1Json);
//			
//			DocumentCollator dc2 = new DocumentCollator();
//			dc2.setDoc_aem_id("");
//			dc2.setDoc_id("DC000002");
//			dc2.setDoc_name("Product Data Sheet - Nova White Tile");
//			dc2.setDoc_path("/content/dam/demo/Australia/Website/Documents/English/technical-data/NovaWhite_Tiles_Product_Data_Sheet_Nov14.pdf");
//			dc2.setDoc_url("http://localhost:4502/content/dam/demo/Australia/Website/Documents/English/technical-data/NovaWhite_Tiles_Product_Data_Sheet_Nov14.pdf");
//			dc2.setCreated_date(new Date());
//			dc2.setUser_id("admin");
//			dcList.add(dc2);
//			JSONObject dc2Json = new JSONObject(gson.toJson(dc2));
//			jsonArray.put(dc2Json);

			apiResponse.put("documentCollatorList",  jsonArray);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return apiResponse;
	}
	
	
}
