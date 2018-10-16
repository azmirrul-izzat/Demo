package com.apddemo.core.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.apddemo.core.constants.ApplicationConstants;
import com.apddemo.core.models.CountryLanguageModel;
import com.apddemo.core.models.CountryModel;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;

public class CountryUtils {
    private static final Logger LOG = LoggerFactory.getLogger(CountryUtils.class);
    
    private static String countryListPath = "/etc/demoCountryList";

    public static List<CountryModel> getCountryModelList(ResourceResolver resourceResolver){
        List<CountryModel> countryModelList = new ArrayList<>();
        Resource countryConfigurationNodeResource = resourceResolver
                .resolve(ApplicationConstants.COUNTRY_CONFIGURATION_NODE_PATH);
        if(countryConfigurationNodeResource != null){
            ValueMap countryNodeValueMap = countryConfigurationNodeResource.getValueMap();
            if(countryNodeValueMap != null
                    && countryNodeValueMap.containsKey(ApplicationConstants.COUNTRY_CONFIGURATION_PROPERTY_LIST)){
                String[] countryLists = countryNodeValueMap
                        .get(ApplicationConstants.COUNTRY_CONFIGURATION_PROPERTY_LIST, String[].class);
                if(countryLists != null){
                    Gson gson = new Gson();
                    TypeToken<List<CountryModel>> token = new TypeToken<List<CountryModel>>(){};
                    countryModelList = gson.fromJson(Arrays.toString(countryLists), token.getType());
                }
            }
        }
        return countryModelList;
    }

    public static CountryModel getCountry(ResourceResolver resourceResolver, String countryCode) {
        List<CountryModel> countryModelList = getCountryModelList(resourceResolver);
        for(CountryModel countryModel: countryModelList){
            if(countryCode.equals(countryModel.getCountryCode())){
                return countryModel;
            }
        }
        return  null;
    }

    public static String getCountry(ResourceResolver resourceResolver, String country, String lang) {
        CountryModel countryModel = getCountry(resourceResolver, country);
        //LOG.info("countryModel:"+countryModel);
        if(countryModel != null){
            List<CountryLanguageModel> languageList = countryModel.getLanguageList();
            if(languageList != null && languageList.size()>0){
                for(CountryLanguageModel countryLanguageModel: languageList){
                    if(lang.equalsIgnoreCase(countryLanguageModel.getLanguageCode())){
                        return countryLanguageModel.getLanguagePath();
                    }
                   // LOG.info("countryLanguageModel:"+countryLanguageModel.getLanguageCode());
                }
            }

        }
        return null;
    }
    
    public static List<Map<String, String>> retrieveDemoCountry(ResourceResolver resourceResolver){
		Resource countryListResource = null;
		
		
		List<Map<String, String>> usgbCountry = new ArrayList<Map<String,String>>();
		Map<String, String> country = null;
		
		try{
			
			countryListResource = resourceResolver.getResource(countryListPath);
			
			if(countryListResource.hasChildren()){
				for(Resource countryResource : countryListResource.getChildren()){
					country = new HashMap<String, String>();
					Node countryNode = countryResource.adaptTo(Node.class);
					
					PropertyIterator properties = countryNode.getProperties();
					
					while (properties.hasNext()) {
						Property property = properties.nextProperty();
						if(!property.isMultiple()){
							country.put(property.getName(), property.getValue().getString());
						}
					}
					usgbCountry.add(country);
				}
			}
			
		}catch(Exception ex){
			LOG.error("retrieveDemoCountry : " + ex.getMessage());
		}
		
		return usgbCountry;
		
	}
    
    public static Map<String, String> retrieveDemoCountrybyPath(ResourceResolver resourceResolver, String pagePath){
    	
    	Map<String, String> countryInfo = null;
    	
    	List<Map<String, String>> countryList = retrieveDemoCountry(resourceResolver);
    	
    	for(Map<String, String> country : countryList) {
			
			if(pagePath.contains(country.get("pathCode").toString())) {
				countryInfo = country;
				break;
			}
			
		}
    	
    	return countryInfo;
    	
    }
}
