package com.apddemo.core.components;

import com.google.gson.Gson;
import com.adobe.cq.sightly.WCMUsePojo;
import com.google.common.reflect.TypeToken;
import com.apddemo.core.constants.ApplicationConstants;
import com.apddemo.core.models.CountryLanguageModel;
import com.apddemo.core.models.CountryModel;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;


public class CountryComponent extends WCMUsePojo {

    private static final Logger LOG = LoggerFactory.getLogger(CountryComponent.class);

    public List<CountryModel> getCountryModelList() {
        return countryModelList;
    }
    private List<CountryModel> countryModelList;


    @Override
    public void activate() throws Exception {
        ValueMap componentProperties = getProperties();
        String[] countryLists = componentProperties
                .get(ApplicationConstants.COUNTRY_CONFIGURATION_PROPERTY_LIST, String[].class);
        Gson gson = new Gson();
        LOG.info("countryLists:"+countryLists);
        if(countryLists != null && countryLists.length>0){
            TypeToken<List<CountryModel>> token = new TypeToken<List<CountryModel>>() {
            };
            this.countryModelList = gson.fromJson(Arrays.toString(countryLists), token.getType());
            /*LOG.info("countryModelList:"+countryModelList);
            if(countryModelList != null){
                for(CountryModel countryModel : countryModelList){
                    LOG.info(countryModel.getCountryCode());
                    LOG.info(countryModel.getCountryTitle());
                    List<CountryLanguageModel> countryLanguageModelList = countryModel.getLanguageList();
                    LOG.info("countryLanguageModelList:"+countryLanguageModelList);
                    if(countryLanguageModelList != null){
                        for(CountryLanguageModel languageModel: countryLanguageModelList){
                            LOG.info("langugeMode: "+languageModel.getLanguagePath());
                            LOG.info("languge title: "+languageModel.getLanguageTitle());
                        }
                    }
                }
            }*/
        }
    }
}
