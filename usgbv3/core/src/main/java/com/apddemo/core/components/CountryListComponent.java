package com.apddemo.core.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.apddemo.core.models.CountryModel;
import com.apddemo.core.utils.CountryUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CountryListComponent extends WCMUsePojo {
    private static final Logger LOG = LoggerFactory.getLogger(CountryListComponent.class);

    private List<CountryModel> countryModelList;
    
    public List<CountryModel> getCountryModelList() {
        return countryModelList;
    }


    @Override
    public void activate() throws Exception {

        ResourceResolver resourceResolver = getResourceResolver();
        LOG.info("resourceResolver:"+ resourceResolver);
        if(resourceResolver != null){
            this.countryModelList = CountryUtils.getCountryModelList(resourceResolver);
        }
        LOG.info("countryModelList:"+this.countryModelList);
    }
}
