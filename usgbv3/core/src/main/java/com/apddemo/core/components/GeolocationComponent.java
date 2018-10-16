package com.apddemo.core.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.apddemo.core.models.GlobalPageGeneralTileModel;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class GeolocationComponent extends WCMUsePojo {
    private static final Logger LOG = LoggerFactory.getLogger(GeolocationComponent.class);

    public List<GlobalPageGeneralTileModel> getGeneralTileModelList() {
        return generalTileModelList;
    }

    private List<GlobalPageGeneralTileModel>  generalTileModelList;


    @Override
    public void activate() throws Exception {
        ValueMap properties = getProperties();
        if(properties != null && properties.containsKey("generaltilelist")){
            String[] generaltilelists = properties.get("generaltilelist", String[].class);
            Gson gson = new Gson();
            LOG.info("generaltilelists:"+generaltilelists);
            TypeToken<List<GlobalPageGeneralTileModel>> token = new TypeToken<List<GlobalPageGeneralTileModel>>() {
            };
            this.generalTileModelList = gson.fromJson(Arrays.toString(generaltilelists), token.getType());
            for(GlobalPageGeneralTileModel tile : generalTileModelList){
                LOG.info("tile description"+tile.tiledescription);
                LOG.info("tile title"+tile.tiletitle);
                LOG.info("tile link"+tile.tilelink);
                LOG.info("tile link"+tile.bgtileimage);
            }
        }
    }
}
