package com.apddemo.core.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.apddemo.core.services.ContactService;
import com.apddemo.core.utils.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ContactUsComponent extends WCMUsePojo {

    private static final Logger LOG = LoggerFactory.getLogger(ContactUsComponent.class);

    private Map<String, String> occupation;
    private Map<String, String> workingWith;
    private Map<String, String> enquiryType;
    private Map<String, String> productInterest;

    public Map<String, String> getOccupation() {
        return occupation;
    }

    public Map<String, String> getWorkingWith() {
        return workingWith;
    }

    public Map<String, String> getEnquiryType() {
        return enquiryType;
    }

    public Map<String, String> getProductInterest() {
        return productInterest;
    }



    @Override
    public void activate() throws Exception {

        // populating country path
        Page currentPage = getCurrentPage();
        String currentPagePath = currentPage.getPath();
        String countryPath = StringUtils.getRootSitePath(currentPagePath);
        // getting the details
        if(countryPath != null){
            String  contactsURL = countryPath+"/contacts";
            String  occupationURL = countryPath+"/occupations";
            String  workingWithURL = countryPath+"/workingwiths";

            ResourceResolver resourceResolver = getResourceResolver();

            // populating occupation details
            Resource occupationsResource = resourceResolver.resolve(occupationURL);
            occupation = getMapDetails(occupationsResource);

            // populating working with details
            Resource workingWithResource = resourceResolver.resolve(workingWithURL);
            workingWith = getMapDetails(workingWithResource);

            // populating enquiry type and product interest
            Resource contactParentResource = resourceResolver.resolve(contactsURL);
            if(contactParentResource != null){
                enquiryType = new LinkedHashMap<>();
                productInterest = new LinkedHashMap<>();
                Iterator<Resource> contactResourceIterator = contactParentResource.listChildren();
                while(contactResourceIterator.hasNext()){
                    Resource contact = contactResourceIterator.next();
                    ValueMap contactValueMap = contact.getValueMap();
                    if(contactValueMap.containsKey("inquiryTopic")){
                        enquiryType.put(StringUtils.replaceSpecialCharacters(contactValueMap.get("inquiryTopic").toString().toLowerCase())
                                ,  contactValueMap.get("inquiryTopic").toString());
                    }
                    if(contactValueMap.containsKey("productInterest")){
                        String productInterest = contactValueMap.get("productInterest").toString();
                        if(!(ContactService.KEYWORD_MULTIPLE_PRODUCTS.equals(productInterest) || "global".equals(productInterest))){
                            this.productInterest.put(StringUtils.replaceSpecialCharacters(contactValueMap.get("productInterest").toString().toLowerCase())
                                    ,  contactValueMap.get("productInterest").toString());
                        }
                    }
                }
            }
            LOG.info("occupation:"+occupation);
            LOG.info("workingWith:"+workingWith);
            LOG.info("enquiryType:"+enquiryType);
            LOG.info("productInterest:"+productInterest);
        }
    }



    private Map<String, String> getMapDetails(Resource occupationsResource) {
        Map<String, String> map= null;
        if (occupationsResource != null) {
            map = new LinkedHashMap<>();
            Iterator<Resource> childOccupationIterator = occupationsResource.listChildren();
            Resource occupationResource = null;
            ValueMap valueMap = null;
            while (childOccupationIterator.hasNext()) {
                occupationResource = childOccupationIterator.next();
                valueMap = occupationResource.getValueMap();
                if (valueMap.containsKey("name") && valueMap.get("name") != null) {
                    map.put(StringUtils.replaceSpecialCharacters(valueMap.get("name").toString().toLowerCase())
                            , valueMap.get("name").toString());
                }
            }
        }
        return map;
    }
}
