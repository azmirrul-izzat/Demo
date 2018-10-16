package com.apddemo.core.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.apddemo.core.constants.ApplicationConstants;
import com.apddemo.core.models.CountryModel;
import com.apddemo.core.models.ExportModel;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class OtherSiteComponent extends WCMUsePojo {

    private static final Logger LOG = LoggerFactory.getLogger(OtherSiteComponent.class);

    public List<ExportModel> getExportList() {
        return exportList;
    }

    private List<ExportModel> exportList;

    @Override
    public void activate() throws Exception {
        ValueMap properties = getProperties();
        if(properties != null && properties.containsKey("exportList")){
            String[] exportLists = properties.get("exportList", String[].class);
            Gson gson = new Gson();
            LOG.info("exportLists:"+exportLists);
            TypeToken<List<ExportModel>> token = new TypeToken<List<ExportModel>>() {
            };
            this.exportList = gson.fromJson(Arrays.toString(exportLists), token.getType());
            for(ExportModel exportModel: exportList){
                LOG.info("exportModel:"+exportModel);
                LOG.info("country title:"+exportModel.getCountrytitle());
                LOG.info("country path:"+exportModel.getCountrypath());
            }

        }
    }
}
