package com.apddemo.core.models;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apddemo.core.utils.DrmUtils;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageEditorModel {	
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@Self private SlingHttpServletRequest slingRequest;
	private static String UNAUTHORIZED_ACCESS_ROOT_PATH = "/unauthorized-access";
	private static String GLOBAL_USER_ROOT_PATH = "/content/dam";

	public String getDamFolderPath(){
		String damFolderPath;		
		
		try {
			Session session = slingRequest.getResourceResolver().adaptTo(Session.class);
            String userCountry = getUserCountry(session);
			if(userCountry != null){
				damFolderPath = DrmUtils.getDamFolderPathBasedOnCountry(slingRequest.getResourceResolver(), userCountry);
				
				if(damFolderPath != null){
					return damFolderPath;					
				}	
				
			}else{
				// global user account don't have associated country
				return GLOBAL_USER_ROOT_PATH;
			}

		} catch (RepositoryException ex) {
			LOG.error("Error getRootPath", ex);
		}
		
		return UNAUTHORIZED_ACCESS_ROOT_PATH;
	}
	
	private String getUserCountry(Session session) throws RepositoryException{		
		UserManager userManager = slingRequest.getResourceResolver().adaptTo(UserManager.class);		
		User user = (User) userManager.getAuthorizable(session.getUserID());
		
		if(user.getProperty("./profile/country") != null){
			return user.getProperty("./profile/country")[0].toString();
		}
		
		return null;
	}
}
