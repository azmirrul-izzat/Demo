package com.apddemo.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jackrabbit.api.security.user.User;

import com.apddemo.core.utils.StringUtils;

public class DrmUtils {

	private final static Logger log = LoggerFactory.getLogger(DrmUtils.class);

	public static final String PROJECT_PROP_FILE = "/etc/designs/usgb-drm-lib/project-properties.json";
	public static final String COUNTRY_CONFIG = "countryConfigurations";
	public static final String COUNTRY_CONTENT_PATH = "countryContentPath";
	public static final String COUNTRY_APPROVER_USER = "approverUser";
	public static final String COUNTRY_DAM_FOLDER_PATH = "countryDamFolder";
	public static final String PROJECT_ENABLED = "isDrmEnabled";
	public static final String COUNTRY_NAME = "countryName";
	
	

	public static JSONObject getDamProjectProperties(ResourceResolver resourceResolver){
		Resource dataResource = resourceResolver.getResource(PROJECT_PROP_FILE); 
		JSONObject projectPropJsonObj = null;

		if(dataResource != null){
			BufferedReader br = null;
			try {
				Node jcnode = dataResource.adaptTo(Node.class).getNode("jcr:content");
				InputStream is = jcnode.getProperty("jcr:data").getBinary().getStream();
				StringBuilder sb = new StringBuilder();String line;

				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				projectPropJsonObj = new JSONObject(sb.toString());
			} catch (IOException e) {
				log.error("getDamProjectProperties: encountered IOException", e);
			} catch (PathNotFoundException e) {
				log.error("getDamProjectProperties: encountered PathNotFoundException", e);
			} catch (RepositoryException e) {
				log.error("getDamProjectProperties: encountered RepositoryException", e);
			} catch (JSONException e) {
				log.error("getDamProjectProperties: encountered JSONException", e);
			} finally {
				if(br != null){
					try {
						br.close();
					} catch (IOException e) {
						log.error("getDamProjectProperties: encountered IOException when trying to close BufferedReader", e);
					}
				}
			}
		}

		return projectPropJsonObj;
	}

	public static JSONObject getCountryConfigBasedOnContentPath(ResourceResolver resourceResolver, String path){

		return getCountryConfigBasedOnContentPath(resourceResolver, path, false);
	}
	
	public static JSONObject getCountryConfigBasedOnContentPath(ResourceResolver resourceResolver, String path, boolean compareByContains){

		JSONObject countryConfigJsonObj = null;
		try {
			JSONObject projectPropJsonObj = getDamProjectProperties(resourceResolver);
			if(StringUtils.isNotBlank(path) && projectPropJsonObj != null){
				JSONArray countryConfigJsonArr = projectPropJsonObj.getJSONArray(COUNTRY_CONFIG);

				for(int i=0; i < countryConfigJsonArr.length(); i++){
					JSONObject specificCountryJsonObj = countryConfigJsonArr.getJSONObject(i);
					String configCountryContentPath = specificCountryJsonObj.optString(COUNTRY_CONTENT_PATH);
					String configCountryDamPath = specificCountryJsonObj.optString(COUNTRY_DAM_FOLDER_PATH);
					if(compareByContains){
						if((StringUtils.isNotBlank(configCountryContentPath) && path.contains(configCountryContentPath)) ||
								(StringUtils.isNotBlank(configCountryDamPath) && path.contains(configCountryDamPath))){
							countryConfigJsonObj = specificCountryJsonObj;
							break;
						}
					}else{
						if((StringUtils.isNotBlank(configCountryContentPath) && path.startsWith(configCountryContentPath)) ||
								(StringUtils.isNotBlank(configCountryDamPath) && path.startsWith(configCountryDamPath))){
							countryConfigJsonObj = specificCountryJsonObj;
							break;
						}
					}
				}
			}
		} catch (JSONException e) {
			log.error("getCountryConfigBasedOnContentPath: encountered JSONException", e);
		}

		return countryConfigJsonObj;
	}
	
	public static String getContentApprover(ResourceResolver resourceResolver, String path){
		String approver = "admin";
		
		JSONObject countryConfigJsonObj = getCountryConfigBasedOnContentPath(resourceResolver, path);
		if(countryConfigJsonObj != null){
			String countryApprover = countryConfigJsonObj.optString(COUNTRY_APPROVER_USER);
			if(StringUtils.isNotBlank(countryApprover)){
				approver = countryApprover;
			}
		}
		
		return approver;
	}
	
	public static boolean isDrmEnabled(ResourceResolver resourceResolver){
		JSONObject config = getDamProjectProperties(resourceResolver);
		
		if(config == null){
			log.error("Error isDrmEnabled: null");
		}
		
		return config.optBoolean(PROJECT_ENABLED);
	}
	
	public static boolean isCountryWorkflowEnabled(ResourceResolver resourceResolver, String path, String workflowPropertyName){
		JSONObject config = getCountryConfigBasedOnContentPath( resourceResolver, path, false);

		if(config != null){			
			try {

				if(!config.has(workflowPropertyName) || config.getString(workflowPropertyName).trim().length() == 0){
					return false;
				}
				
			} catch (JSONException e) {
				log.error("Error isCountryWorkflowEnabled: ", e);
			}
		}

		return true;
	}
	
	public static boolean isDrmConfigEnabled(ResourceResolver resourceResolver, String path, String workflowPropertyName){
		return (isDrmEnabled(resourceResolver) && isCountryWorkflowEnabled(resourceResolver,  path,  workflowPropertyName));
	}
	
	public static String getContentApproverGroup(ResourceResolver resourceResolver, String userId){
		try {
			UserManager userManager = (UserManager) resourceResolver.adaptTo(UserManager.class);
		
			if(userManager.getAuthorizable(userId).isGroup()){
				return userId;
			}else{

				User user = (User) userManager.getAuthorizable(userId);
				Iterator<Group> groups = user.declaredMemberOf();
				
				while(groups.hasNext()){
					Group group = groups.next();
					log.info("group.getID()" + group.getID());
					if(group.getID().endsWith("_approvers")){
						
						return group.getID();
					}
				}
			}

		} catch (Exception e) {
			log.error("Error getApproverGroup, userId " + userId, e);
		}
		
		return null;
	}
	
	
	private static JSONObject getCountryConfigBasedOnCountry(ResourceResolver resourceResolver, String country){

		JSONObject countryConfigJsonObj = null;
		try {
			JSONObject projectPropJsonObj = getDamProjectProperties(resourceResolver);
			if(StringUtils.isNotBlank(country) && projectPropJsonObj != null){
				JSONArray countryConfigJsonArr = projectPropJsonObj.getJSONArray(COUNTRY_CONFIG);

				for(int i=0; i < countryConfigJsonArr.length(); i++){
					JSONObject specificCountryJsonObj = countryConfigJsonArr.getJSONObject(i);
					String configCountryCountryName = specificCountryJsonObj.optString(COUNTRY_NAME);

						if(StringUtils.isNotBlank(configCountryCountryName) && country.contains(configCountryCountryName)){
							countryConfigJsonObj = specificCountryJsonObj;
							
							break;
						}
					
				}
			}
		} catch (JSONException e) {
			log.error("getCountryConfigBasedOnContentPath: encountered JSONException", e);
		}

		return countryConfigJsonObj;
	}
	
	
	public static String getDamFolderPathBasedOnCountry(ResourceResolver resourceResolver, String country){		
		JSONObject countryConfigJsonObj =  getCountryConfigBasedOnCountry(resourceResolver,  country);
		
		if(countryConfigJsonObj != null){
			return countryConfigJsonObj.optString(COUNTRY_DAM_FOLDER_PATH);
		}
		
		return null;
	}
	
	
}
