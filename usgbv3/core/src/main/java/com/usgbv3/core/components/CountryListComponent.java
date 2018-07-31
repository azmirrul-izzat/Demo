package com.usgbv3.core.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.usgbv3.core.models.CountryModel;
import com.usgbv3.core.utils.CountryUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CountryListComponent extends WCMUsePojo {
    private static final Logger LOG = LoggerFactory.getLogger(CountryListComponent.class);

    public List<CountryModel> getCountryModelList() {
        return countryModelList;
    }

    private List<CountryModel> countryModelList;

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