package com.apddemo.core.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.apddemo.core.models.CountryLanguageModel;
import com.apddemo.core.models.CountryModel;
import com.apddemo.core.utils.CountryUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CountryPopupComponent extends WCMUsePojo {
    private static final Logger LOG = LoggerFactory.getLogger(CountryPopupComponent.class);

    private List<CountryModel> countryModelList;
    private String error;
    
    public List<CountryModel> getCountryModelList() {
        return countryModelList;
    }

    public String getError() {
		return error;
	}
    
	public void setError(String error) {
		this.error = error;
	}


	@Override
    public void activate() throws Exception {

        ResourceResolver resourceResolver = getResourceResolver();
        countryModelList = new ArrayList<CountryModel>();
        setError("");
        LOG.info("resourceResolver:"+ resourceResolver);
        if(resourceResolver != null){
            
        	List<Map<String, String>> countryList = CountryUtils.retrieveDemoCountry(getResourceResolver());
        	Set<String> countryAssetPathList = new HashSet<>();
        	for(Map<String, String> country : countryList) {
        		
        		if("en_ip".equals(country.get("countryCode"))) {
        			continue;
        		}
        		countryAssetPathList.add(country.get("damPath").toLowerCase());
        		
        	}

    		for(String country : countryAssetPathList) {
    			
    			CountryModel countryModel = new CountryModel();
    			List<CountryLanguageModel> countryLanguageList = new ArrayList<CountryLanguageModel>();
    			
    			for(Map<String, String> countryInfo : countryList) {
    				
    				if(country.equalsIgnoreCase(countryInfo.get("damPath").toLowerCase())) {
    					
    					CountryLanguageModel countryLanguage = new CountryLanguageModel();
    					
    					if(countryInfo.containsKey("countryId") && (countryModel.getCountryCode() == null || countryModel.getCountryCode().isEmpty())) {
    						
    						countryModel.setCountryCode(countryInfo.get("countryId"));
    					}

    					if(countryInfo.containsKey("countryName") && (countryModel.getCountryTitle() == null || countryModel.getCountryTitle().isEmpty())) {
    						
    						countryModel.setCountryTitle(countryInfo.get("countryName"));
    					}

    					if(countryInfo.containsKey("logo") && (countryModel.getDamPath() == null || countryModel.getDamPath().isEmpty())) {
    						
    						countryModel.setDamPath(countryInfo.get("logo"));
    					}
    					
    					countryLanguage.setLanguageCode(countryInfo.get("countryCode"));
    					countryLanguage.setLanguageTitle(countryInfo.get("languageFull"));
    					countryLanguage.setLanguagePath(countryInfo.get("sitePath"));
    					countryLanguageList.add(countryLanguage);
    					
    				}
    			}
    			
    			countryModel.setLanguageList(countryLanguageList);
    			countryModelList.add(countryModel);
    		}
    		
            
            
        }
        LOG.info("countryModelList:"+this.countryModelList);
    }
}
