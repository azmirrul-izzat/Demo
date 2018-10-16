package com.apddemo.core.servlets;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.ServerException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.servlet.Servlet;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.apddemo.core.daos.OrderSampleDao;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.apddemo.core.daos.UserInfoDao;
import com.apddemo.core.entity.DocumentCollator;
import com.apddemo.core.entity.OrderInfo;
import com.apddemo.core.entity.ProductInfo;
import com.apddemo.core.entity.UserInfo;
import com.apddemo.core.models.EmailSettings;
import com.apddemo.core.services.EmailService;
import com.apddemo.core.services.RestletService;
import com.apddemo.core.utils.CountryUtils;
import com.apddemo.core.utils.NodeUtils;
import com.apddemo.core.utils.StringUtils;

//localhost:4502/bin/sso/osRetrieveHistory

@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=SSO Order Sample - Retrieve History" ,
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths="+ "/bin/sso/orderSample/retrieve"
})
public class SSOOSRetrieveServlet extends BaseAllMethodsServlet {
	private static final long serialVersionUID = 1452364151988577055L;
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	private SimpleDateFormat formatT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Reference
	private OrderSampleDao orderSampleDao;
	
	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {
				log.info("SSOOSRetrieveServlet Start");
			try {
				response.setCharacterEncoding("utf-8");
				ResourceResolver resolver = request.getResourceResolver();
				String payLoad = request.getReader().readLine();
				JSONObject payloadJson = new JSONObject(payLoad);
				if(!payloadJson.has("user_info")){
					
					this.setJsonResponse(response, "{\"error\":\"Missing Parameter user_info \"}", 500);
					
				}else{
					JSONObject user_info = (JSONObject)payloadJson.get("user_info");
					JSONObject input = null;
					if(payloadJson.has("input")){
						input = (JSONObject)payloadJson.get("input");
						
					}
					JSONObject jsonObj = retrieveValueJson(user_info, input, resolver);
					this.setJsonResponseOk(response,jsonObj.toString());
				}
				
				
			}catch(Exception e){
				this.setJsonResponse(response, "{\"SSOOSRetrieveServlet\":\"exception -" + e.getMessage() + "\"}", 500);
			}
		
	}
	
	private JSONObject retrieveValueJson(JSONObject userInfo,JSONObject input, ResourceResolver resourceResolver){
		
		JSONObject apiResponse = new JSONObject();
		Gson gson = new Gson();
		JSONArray jsonArray = new JSONArray();
		Calendar c = Calendar.getInstance();
//		ObjectMapper mapper = new ObjectMapper();
		List<OrderInfo> oiList = new ArrayList<OrderInfo>();
		UserManager userManager;
		List<String> groupList = new ArrayList<String>();
		List<String> countryList = new ArrayList<String>();
		boolean isAdmin = false;
		boolean isAnonymous = false;
		boolean adminView = false;
		int startMonth = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		List<Map<String, String>> demoCountryList = null;
		String aemId = "";
		Map<String, Object> inputMap = null;
		
		try {

			aemId = (String) userInfo.get("user_id");
			if(userInfo.has("adminView")){
				adminView = (boolean) userInfo.get("adminView");
			}
			
			Session session = resourceResolver.adaptTo(Session.class);
			userManager = AccessControlUtil.getUserManager(session);
			
			if("anonymous".equals(session.getUserID().toLowerCase())){
				isAnonymous = true;
			}if(!adminView){
				// SKIPP
			}else{
				User currentUser = (User) userManager.getAuthorizable(session.getUserID());
				Iterator<Group> currentUserGroups = currentUser.memberOf();
				
				while (currentUserGroups.hasNext()){
					Group currentGroup = currentUserGroups.next();
					String userGroupName = currentGroup.getID();
					if(userGroupName != null){
						log.info(userGroupName);
						if("administrators".equals(userGroupName.toLowerCase())){
							
							isAdmin = true;
							break;
						}else{
							if((userGroupName.toLowerCase()).indexOf("usgb_") > -1 && !(userGroupName.toLowerCase()).equals("usgb_base")){
								groupList.add(userGroupName);
							}
							
						}
						if(currentGroup.memberOf() != null){
							Iterator<Group> groupOfGroups = currentGroup.memberOf();
							while (groupOfGroups.hasNext()){
								Group groupOfGroup = groupOfGroups.next();
								String groupOfGroupName = groupOfGroup.getID();
								log.info("groupOfGroupName : " + groupOfGroupName);
								if((groupOfGroupName.toLowerCase()).indexOf("usgb_") > -1 && !(groupOfGroupName.toLowerCase()).equals("usgb_base")){
									groupList.add(groupOfGroupName);
								}
							}
							
						}
					}
			    }
				
				if(groupList.size() > 0){
					demoCountryList = CountryUtils.retrieveDemoCountry(resourceResolver);
					log.info(" " );
					log.info("groupList : " + groupList.toString());
					log.info(" " );
					log.info("demoCountryList : " + demoCountryList.toString());
					
					for(String groupName : groupList){
						
						if(demoCountryList != null){
							for(Map<String, String> usgbCountry : demoCountryList){
								
								if(usgbCountry.containsKey("damApprover") && (groupName.toLowerCase()).contains(usgbCountry.get("damApprover").toLowerCase())){
									countryList.add(usgbCountry.get("countryCode").toLowerCase());
//									break;
									
								}else if(usgbCountry.containsKey("damAuthor") && (groupName.toLowerCase()).contains(usgbCountry.get("damAuthor").toLowerCase())){
									countryList.add(usgbCountry.get("countryCode").toLowerCase());
//									break;
								}
							}
						}
					}
					
					
				}
				
			}
			
			if(input != null){
				
				if(input.has("start_month")){
					startMonth = (int) input.get("start_month");
				}

				if(input.has("year")){
					year = (int) input.get("year");
				}
			}
			
			Calendar startMonthCal = new GregorianCalendar(year,startMonth-1, 1);
			
			Calendar dummyCal = new GregorianCalendar(year,startMonth-1, 1);
			dummyCal.add(Calendar.MONTH, 2);
			int lastDate = dummyCal.getActualMaximum(Calendar.DAY_OF_MONTH);
			
			Calendar endMonthCal = new GregorianCalendar(dummyCal.get(Calendar.YEAR),dummyCal.get(Calendar.MONTH), lastDate, 23, 59, 59);
			
			inputMap = new HashMap<String, Object>();
			inputMap.put("startDate", startMonthCal.getTime());
			inputMap.put("endDate", endMonthCal.getTime());

			log.info("isAnonymous : " + isAnonymous);
			log.info("isAdmin : " + isAdmin);
			log.info("adminView : " + adminView);
			log.info("startMonth : " + startMonth);
			log.info("year : " + year);
			log.info("startDate : " + startMonthCal.getTime());
			log.info("endDate : " + endMonthCal.getTime());
			if(isAnonymous || !adminView){
				log.info("user is anonymous : " + aemId);
				oiList = orderSampleDao.getOrderListbySsoId(aemId);
			}else if(isAdmin){
				log.info("user is admin");
				oiList = orderSampleDao.getOrderList(inputMap);
			}
			else{
				log.info("user is not admin / anonymous");
				if(countryList.size() > 0){
					log.info(" " );
					log.info("countryList : " + countryList.toString());
					oiList = orderSampleDao.getOrderListbyCountry(countryList, inputMap);
				}else{
					log.info("user is not VALID");
				}
			}
		
			
			for(OrderInfo oi : oiList){
				
				JSONObject oiJson = new JSONObject(gson.toJson(oi));
				oiJson.put("created_date", formatT.format(oi.getCreated_date()));
				oiJson.put("modified_date", formatT.format(oi.getModified_date()));
				jsonArray.put(oiJson);
			}
			
			
			apiResponse.put("orderSamplesList",  jsonArray);
		} catch (Exception e) {
			log.info("ERROR SSOOSRetrieveServlet : " + e.getMessage());
			e.printStackTrace();
		}
		
		return apiResponse;
	}
	
	
}