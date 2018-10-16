package com.apddemo.core.services.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.apddemo.core.models.DistanceLocatorProductCategory;
import com.apddemo.core.models.StoreLocatorProductCategory;
import com.apddemo.core.models.StoreLocatorStoreCategory;
import com.apddemo.core.services.StoreLocatorService;
import com.apddemo.core.utils.CountryUtils;
import com.apddemo.core.utils.GenericUtils;
import com.apddemo.core.utils.StringUtils;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Component(immediate = true, service = StoreLocatorService.class
        , configurationPid = "com.apddemo.core.services.impl.StoreLocatorServiceImpl")
public class StoreLocatorServiceImpl implements StoreLocatorService{

    private static Logger LOG = LoggerFactory.getLogger(StoreLocatorServiceImpl.class);

    @Reference
    QueryBuilder queryBuilder;

    @Override
    public String getAllStates(String countryCode, ResourceResolver resourceResolver) {
        String lang= null , country=null;
        Set<String> statesNameSet = new HashSet();

        if(StringUtils.isNotBlank(countryCode)){
            if(countryCode.indexOf("_")!= -1){
                String[] countryLanguageArray = countryCode.split("_");
                if(countryLanguageArray != null && countryLanguageArray.length==2){
                    lang=countryLanguageArray[0];
                    country =countryLanguageArray[1];
                }
            }
            //LOG.info("lang :"+lang +" country:"+country);
            if(country != null){
                String countryLanguagePath = CountryUtils.getCountry(resourceResolver, country, lang);
                //LOG.info("countryLanguagePath:"+countryLanguagePath);
                if(countryLanguagePath != null){
                    if(!countryLanguagePath.endsWith("/")){
                        countryLanguagePath = countryLanguagePath+"/";
                    }
                    Resource storeLocationResource = resourceResolver.resolve(countryLanguagePath + "storelocations");
                   // LOG.info("storeLocationResource:"+storeLocationResource);
                    if(storeLocationResource != null){
                        ValueMap storeValueMap = null;
                        Iterable<Resource> storeIterator = storeLocationResource.getChildren();
                        for(Resource store: storeIterator){
                            storeValueMap = store.getValueMap();
                            if(storeValueMap.containsKey("state")){
                                statesNameSet.add(storeValueMap.get("state").toString());
                            }
                        }
                    }
                }
            }

        }
        if(statesNameSet.size()>0){
            List sortedList = new ArrayList(statesNameSet);
            Gson gson = new Gson();
            return gson.toJson(sortedList);
        }
        return null;
    }

    @Override
    public String getAutoSearch(String pageURL, String text, ResourceResolver resourceResolver, boolean getResource) {
        LOG.info("pageURL:"+pageURL);
        JsonArray resultJsonArray = null;
        Gson gson = new Gson();
        Map<String, String> autoSearchConfiguration = new LinkedHashMap<>();
        if(pageURL != null){
            String completePageURL="";
            if(!pageURL.startsWith("/content/demo")){
                completePageURL = "/content/demo"+pageURL;
            }else{
                completePageURL = pageURL;
            }

            Map<String, String>  queryMap = new HashMap<>();
            // forming query filter
            queryMap.put("type", "nt:unstructured");
            queryMap.put("path", completePageURL);
            queryMap.put("property", "sling:resourceType");
            queryMap.put("property.1_value", "apddemo/components/content/where-to-buy-component");

            // logging the query
            GenericUtils.logQuery(LOG, queryMap);

            // querying the data
            Session session = resourceResolver.adaptTo(Session.class);
            Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
            SearchResult result = query.getResult();
            List<Hit> hitsList = result.getHits();
            if(hitsList != null && hitsList.size()>0){
                LOG.info("hitList size:"+hitsList.size());
                // get the properties
                Resource autoSearchResource = null;
                try {
                    autoSearchResource = hitsList.get(0).getResource();
                    if(autoSearchResource != null){
                        ValueMap autoSearchNodeVM = autoSearchResource.getValueMap();
                        if(autoSearchNodeVM != null && autoSearchNodeVM.containsKey("matchField")){
                            autoSearchConfiguration.put("matchField", autoSearchNodeVM.get("matchField").toString());
                        }
                       /* if(autoSearchNodeVM.containsKey("isGlobal")){
                            autoSearchConfiguration.put("isGlobal", autoSearchNodeVM.get("isGlobal").toString());
                        }*/
                        if(autoSearchNodeVM.containsKey("proximityMatchField")){
                            autoSearchConfiguration.put("proximityMatchField", autoSearchNodeVM.get("proximityMatchField").toString());
                        }
                        if(autoSearchNodeVM.containsKey("wordLength")){
                            autoSearchConfiguration.put("wordLength", autoSearchNodeVM.get("wordLength").toString());
                        }
                    }
                } catch (RepositoryException e) {
                    LOG.error("Repository Exception is :"+e);
                }
            }
        }
        LOG.info("properties set in page is :"+autoSearchConfiguration);

        // get the properties and form the query  to get the results and form json
        if(autoSearchConfiguration.size()>0){
            String storeLocationPath = null, proximityLocationPath=null;
            if(pageURL != null && pageURL.indexOf("/")!= -1){
                String country=null;
                if(pageURL.startsWith("/content/demo")){
                    pageURL = pageURL.replace("/content/demo", "");
                }
                String[] pageURLSlashSplitArray = pageURL.split("/");
                for(String pageURLPath : pageURLSlashSplitArray){
                    if(StringUtils.isNotBlank(pageURLPath)){
                        country=pageURLPath;
                        break;
                    }
                }

                if(StringUtils.isNotBlank(country)){
                    storeLocationPath = "/content/demo/"+country+"/storelocations";
                    proximityLocationPath = "/content/demo/"+country+"/proximitylocations";
                }
            }
            LOG.info("storeLocationPath:"+storeLocationPath);
            LOG.info("proximityLocationPath:"+proximityLocationPath);
            String[] matchFieldsArray = null;
            if(StringUtils.isNotBlank(storeLocationPath)){
                if(autoSearchConfiguration.containsKey("matchField")){
                    String matchFieldCSV = autoSearchConfiguration.get("matchField");
                    resultJsonArray = getAutoSearchJsonElements(text, resourceResolver, resultJsonArray
                            , storeLocationPath, matchFieldCSV, "", getResource);
                }
            }

            // if store excel doesnt contain the match criteria and we have proximity search defined in properties
            if((resultJsonArray == null || (resultJsonArray != null && resultJsonArray.size()==0)) && StringUtils.isNotBlank(proximityLocationPath) && autoSearchConfiguration.containsKey("proximityMatchField")){
                LOG.info("resultJsonArray is null so searching in  proximity data");
                String proximityMatchFieldCSV = autoSearchConfiguration.get("proximityMatchField");
                resultJsonArray = getAutoSearchJsonElements(text, resourceResolver, resultJsonArray
                        , proximityLocationPath, proximityMatchFieldCSV, "proximity_", getResource);
            }

        }
        if(resultJsonArray != null){
            JsonObject resultJson = new JsonObject();
            resultJson.add("Items", resultJsonArray);
            return gson.toJson(resultJson);
        }
        return null;
    }

    private JsonArray getAutoSearchJsonElements(String searchText, ResourceResolver resourceResolver
            , JsonArray resultJsonArray, String path, String matchFieldCSV, String prefix, Boolean getResource) {
        String[] matchFieldsArray;
        Map<String, String> queryMap;
        matchFieldsArray = matchFieldCSV.split(",");
        if(matchFieldsArray != null && matchFieldsArray.length>0){
            Session session = resourceResolver.adaptTo(Session.class);
            SearchResult result = null;
            List<Hit> hitsList = null;
            resultJsonArray = new JsonArray();
            JsonObject tempJsonObject = new JsonObject();
            Map<String, String> dataMapNoFlagShip = new LinkedHashMap<>();
            Map<String, String> dataMapYesFlagShip = new LinkedHashMap<>();
            List<JsonObject> flagShipYESStoreJsonObjectList = new ArrayList<>();
            List<JsonObject> flagShipNOStoreJsonObjectList = new ArrayList<>();
            Gson gson = new Gson();
            for(String matchField : matchFieldsArray){
                if(StringUtils.isNotBlank(matchField)){
                    queryMap = new LinkedHashMap<>();
                    queryMap.put("path", path);
                    queryMap.put("type", "nt:unstructured");
                    queryMap.put("fulltext", "*"+searchText+"*");
                    queryMap.put("fulltext.relPath", "@"+matchField);
                    // logging the querymap
                    GenericUtils.logQuery(LOG, queryMap);

                    Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
                    result = query.getResult();
                    hitsList = result.getHits();
                    Resource resultResource = null;
                    for(Hit hit: hitsList){
                        try {
                            resultResource = hit.getResource();
                            ValueMap valueMap = resultResource.getValueMap();
                            LOG.info("for match field :"+matchField+"resultResource:"+resultResource.getPath());
                            if(getResource){
                                if(valueMap.containsKey(matchField)){
                                    if(valueMap.containsKey("flagship") && valueMap.get("flagship") != null
                                            && "yes".equalsIgnoreCase((String)valueMap.get("flagship"))){
                                        flagShipYESStoreJsonObjectList.add(convertResourceToStoreJson(resultResource, gson));
                                    }else{
                                        flagShipNOStoreJsonObjectList.add(convertResourceToStoreJson(resultResource, gson));
                                    }
                                }
                            }else{
                                // to order data as per flagship
                                if(valueMap.containsKey(matchField)){
                                    if(valueMap.containsKey("flagship") && valueMap.get("flagship") != null
                                            && "yes".equalsIgnoreCase((String)valueMap.get("flagship"))){
                                        dataMapYesFlagShip.put(valueMap.get(matchField).toString(), prefix+matchField);
                                    }else{
                                        dataMapNoFlagShip.put(valueMap.get(matchField).toString(), prefix+matchField);
                                    }
                                }
                            }
                        } catch (RepositoryException e) {
                            LOG.error("RepositoryException :"+e);
                        }
                    }
                }
            }
            if(getResource){
                if(flagShipYESStoreJsonObjectList.size()>0){
                    for(JsonObject storeObject : flagShipYESStoreJsonObjectList){
                        resultJsonArray.add(storeObject);
                    }
                }
                if(flagShipNOStoreJsonObjectList.size()>0){
                    for(JsonObject storeObject : flagShipNOStoreJsonObjectList){
                        resultJsonArray.add(storeObject);
                    }
                }
            }else{
                if(dataMapYesFlagShip.size()>0){
                    Iterator it = dataMapYesFlagShip.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        tempJsonObject = new JsonObject();
                        tempJsonObject.addProperty(pair.getValue().toString(), pair.getKey().toString());
                        resultJsonArray.add(tempJsonObject);
                    }
                }
                if(dataMapNoFlagShip.size()>0){
                    Iterator it = dataMapNoFlagShip.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        tempJsonObject = new JsonObject();
                        tempJsonObject.addProperty(pair.getValue().toString(), pair.getKey().toString());
                        resultJsonArray.add(tempJsonObject);
                    }
                }
            }

        }
        return resultJsonArray;
    }

    @Override
    public String getStoreSearch(RequestParameter keyParameter, RequestParameter textParameter
            , RequestParameter countryParameter, RequestParameter currentLocationParameter
            , ResourceResolver resourceResolver, String pageURL) {
        String jsonData = "";
        LOG.info("keyParameter:"+keyParameter);
        LOG.info("textParameter:"+textParameter);
        LOG.info("countryParameter:"+countryParameter);
        LOG.info("currentLocationParameter:"+currentLocationParameter);
        LOG.info("pageURL:"+pageURL);
        Gson gson = new Gson();
        // getting the country path
        String countryParam = countryParameter.getString();
        String country=null, lang= null;
        if(countryParam != null && countryParam.indexOf("_")!= -1){
            String[] countryLangStringArray = countryParam.split("_");
            if(countryLangStringArray != null && countryLangStringArray.length==2){
                lang = countryLangStringArray[0];
                country = countryLangStringArray[1];
            }
        }
        String countryPath = CountryUtils.getCountry(resourceResolver, country, lang);
        LOG.info("countryPath:"+countryPath);
        String storeLocationPath = countryPath+"/storelocations";
        String proximityLocationPath = countryPath+"/proximitylocations";
        JsonObject tempJson=null;
        JsonArray resultJsonArray = new JsonArray();
        JsonArray resultYesFlagshipJsonArray = new JsonArray();
        JsonArray resultNoFlagshipJsonArray = new JsonArray();
        JsonObject resultJsonObject = new JsonObject();
        Set<String> storeTypeSet = new HashSet<>();
        Set<String> productTypeSet = new HashSet<>();
        JsonArray productJsonArray = null;
        if(countryPath != null){
            if(currentLocationParameter != null){
                // we are searching as per the current location and get all the stores in the country parameter sent
                Resource storeLocatorResource = resourceResolver.resolve(storeLocationPath);
                if(storeLocatorResource != null){
                    Iterable<Resource> children = storeLocatorResource.getChildren();
                    for(Resource store : children){
                        tempJson = convertResourceToStoreJson(store, gson);
                        if(tempJson != null && tempJson.has("flagship")
                                && tempJson.get("flagship") != null
                                && "yes".equalsIgnoreCase(tempJson
                                .getAsJsonPrimitive("flagship").getAsString())){
                            resultYesFlagshipJsonArray.add(tempJson);
                        }else{
                            resultNoFlagshipJsonArray.add(tempJson);
                        }

                        // populating unique storetype and product
                        if(tempJson.has("store_type")){
                            storeTypeSet.add(tempJson.get("store_type").getAsString());
                        }
                        if(tempJson.has("product_categories") && tempJson.get("product_categories")!= null){
                            productJsonArray = tempJson.get("product_categories").getAsJsonArray();
                            if(productJsonArray != null && productJsonArray.size()>0){
                                for(JsonElement productJsonElement: productJsonArray){
                                    if(!"".equals(productJsonElement.getAsString())){
                                        productTypeSet.add(productJsonElement.getAsString());
                                    }
                                }
                            }
                        }
                    }
                    LOG.info("storeTypeSet:"+storeTypeSet);
                    LOG.info("productTypeSet:"+productTypeSet);
                    LOG.info("countryPath:"+countryPath);
                    if(resultYesFlagshipJsonArray.size()>0){
                        resultJsonArray.addAll(resultYesFlagshipJsonArray);
                    }
                    if(resultNoFlagshipJsonArray.size()>0){
                        resultJsonArray.addAll(resultNoFlagshipJsonArray);
                    }
                    resultJsonObject.add("storeResults", resultJsonArray);
                    if(storeTypeSet != null && productTypeSet != null){
                        JsonArray filterJsonArray = getFilterJsonData(storeTypeSet, productTypeSet
                                , resourceResolver, pageURL);
                        if(filterJsonArray != null){
                            resultJsonObject.add("filterListing", filterJsonArray);
                        }
                    }
                    // converting to json response
                    jsonData = gson.toJson(resultJsonObject);
                }
            }else if(keyParameter != null && "".equals(keyParameter.getString())){
                LOG.info("hey key parameter is empty means we need to search in all fields");
                String text = textParameter.getString();
                if(text != null ){
                    jsonData = getAutoSearch(pageURL, text, resourceResolver, true);
                    if(jsonData != null){
                        jsonData =  populateStoreResultFormat(jsonData, gson, resourceResolver, pageURL);
                    }
                }
            }else if(keyParameter != null && !"".equals(keyParameter.getString())){
                String key = keyParameter.getString();
                Session session = resourceResolver.adaptTo(Session.class);
                SearchResult result = null;
                List<Hit> hitsList = null;
                if("statename".equals(key)){
                    if(textParameter != null){
                        String text = textParameter.getString();
                        // search in statename and send all store with that statename
                        Map<String, String> queryMap = new LinkedHashMap<>();
                        queryMap.put("path", storeLocationPath);
                        queryMap.put("type", "nt:unstructured");
                        queryMap.put("fulltext", text);
                        queryMap.put("fulltext.relPath", "@state");
                        GenericUtils.logQuery(LOG, queryMap);

                        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
                        result = query.getResult();
                        hitsList = result.getHits();
                        for(Hit hit:hitsList){
                            try {
                                tempJson = convertResourceToStoreJson(hit.getResource(), gson);
                                if(tempJson != null && tempJson.has("flagship")
                                        && tempJson.get("flagship") != null
                                        && "yes".equalsIgnoreCase(tempJson
                                        .getAsJsonPrimitive("flagship").getAsString())){
                                    resultYesFlagshipJsonArray.add(tempJson);
                                }else{
                                    resultNoFlagshipJsonArray.add(tempJson);
                                }

                                // populating unique storetype and product
                                if(tempJson.has("store_type")){
                                    storeTypeSet.add(tempJson.get("store_type").getAsString());
                                }
                                if(tempJson.has("product_categories") && tempJson.get("product_categories")!= null){
                                    productJsonArray = tempJson.get("product_categories").getAsJsonArray();
                                    if(productJsonArray != null && productJsonArray.size()>0){
                                        for(JsonElement productJsonElement: productJsonArray){
                                            if(!"".equals(productJsonElement.getAsString())){
                                                productTypeSet.add(productJsonElement.getAsString());
                                            }
                                        }
                                    }
                                }
                            } catch (RepositoryException e) {
                                LOG.error("Repository Exception:"+e);
                            }
                        }
                        LOG.info("storeTypeSet:"+storeTypeSet);
                        LOG.info("productTypeSet:"+productTypeSet);
                        LOG.info("countryPath:"+countryPath);
                        if(resultYesFlagshipJsonArray.size()>0){
                            resultJsonArray.addAll(resultYesFlagshipJsonArray);
                        }
                        if(resultNoFlagshipJsonArray.size()>0){
                            resultJsonArray.addAll(resultNoFlagshipJsonArray);
                        }
                        resultJsonObject.add("storeResults", resultJsonArray);
                        if(storeTypeSet != null && productTypeSet != null){
                            JsonArray filterJsonArray = getFilterJsonData(storeTypeSet, productTypeSet
                                    , resourceResolver, pageURL);
                            if(filterJsonArray != null){
                                resultJsonObject.add("filterListing", filterJsonArray);
                            }
                        }

                        jsonData = gson.toJson(resultJsonObject);
                    }
                }else if(key.startsWith("proximity")){
                    // get all the store in country and search for the proximity string sent
                    key = key.replaceAll("proximity_", "");

                    String text = textParameter.getString();
                    Map<String, String> queryMap = new LinkedHashMap<>();
                    queryMap.put("path", proximityLocationPath);
                    queryMap.put("type", "nt:unstructured");
                    queryMap.put("fulltext", text);
                    queryMap.put("fulltext.relPath", "@"+key);
                    GenericUtils.logQuery(LOG, queryMap);

                    Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
                    result = query.getResult();
                    hitsList = result.getHits();
                    for(Hit hit:hitsList){
                        try {
                            tempJson = convertResourceToStoreJson(hit.getResource(), gson);
                            resultJsonArray.add(tempJson);

                        } catch (RepositoryException e) {
                            LOG.error("Repository Exception:"+e);
                        }
                    }
                    resultJsonObject.add("proximityResult", resultJsonArray);

                    // store results
                    resultJsonArray = new JsonArray();
                    Resource storeLocatorResource = resourceResolver.resolve(storeLocationPath);
                    if(storeLocatorResource != null){
                        Iterable<Resource> children = storeLocatorResource.getChildren();
                        for(Resource store : children){
                            tempJson = convertResourceToStoreJson(store, gson);
                            if(tempJson != null && tempJson.has("flagship")
                                    && tempJson.get("flagship") != null
                                    && "yes".equalsIgnoreCase(tempJson
                                    .getAsJsonPrimitive("flagship").getAsString())){
                                resultYesFlagshipJsonArray.add(tempJson);
                            }else{
                                resultNoFlagshipJsonArray.add(tempJson);
                            }
                            // populating unique storetype and product
                            if(tempJson.has("store_type")){
                                storeTypeSet.add(tempJson.get("store_type").getAsString());
                            }
                            if(tempJson.has("product_categories") && tempJson.get("product_categories")!= null){
                                productJsonArray = tempJson.get("product_categories").getAsJsonArray();
                                if(productJsonArray != null && productJsonArray.size()>0){
                                    for(JsonElement productJsonElement: productJsonArray){
                                        if(!"".equals(productJsonElement.getAsString())){
                                            productTypeSet.add(productJsonElement.getAsString());
                                        }
                                    }
                                }
                            }
                        }
                        if(resultYesFlagshipJsonArray.size()>0){
                            resultJsonArray.addAll(resultYesFlagshipJsonArray);
                        }
                        if(resultNoFlagshipJsonArray.size()>0){
                            resultJsonArray.addAll(resultNoFlagshipJsonArray);
                        }
                        resultJsonObject.add("storeResults", resultJsonArray);
                    }

                    // filter resutls
                    LOG.info("storeTypeSet:"+storeTypeSet);
                    LOG.info("productTypeSet:"+productTypeSet);
                    LOG.info("countryPath:"+countryPath);
                    if(storeTypeSet != null && productTypeSet != null){
                        JsonArray filterJsonArray = getFilterJsonData(storeTypeSet, productTypeSet
                                , resourceResolver, pageURL);
                        if(filterJsonArray != null){
                            resultJsonObject.add("filterListing", filterJsonArray);
                        }
                    }
                    // converting to json response
                    //jsonData = gson.toJson(resultJsonObject);
                    jsonData = gson.toJson(resultJsonObject);

                }else{
                    // search in country for the key to have the value
                    if(textParameter != null && !"".equals(textParameter.getString())) {
                        String text = textParameter.getString();
                        text = text.replace("-", "");
                        Map<String, String> queryMap = new LinkedHashMap<>();
                        queryMap.put("path", storeLocationPath);
                        queryMap.put("type", "nt:unstructured");
                        queryMap.put("fulltext", text);
                        queryMap.put("fulltext.relPath", "@"+key);
                        GenericUtils.logQuery(LOG, queryMap);

                        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
                        result = query.getResult();
                        hitsList = result.getHits();
                        for(Hit hit:hitsList){
                            try {
                                tempJson = convertResourceToStoreJson(hit.getResource(), gson);
                                if(tempJson != null && tempJson.has("flagship")
                                        && tempJson.get("flagship") != null
                                        && "yes".equalsIgnoreCase(tempJson
                                        .getAsJsonPrimitive("flagship").getAsString())){
                                    resultYesFlagshipJsonArray.add(tempJson);
                                }else{
                                    resultNoFlagshipJsonArray.add(tempJson);
                                }

                                // populating unique storetype and product
                                if(tempJson.has("store_type")){
                                    storeTypeSet.add(tempJson.get("store_type").getAsString());
                                }
                                if(tempJson.has("product_categories") && tempJson.get("product_categories")!= null){
                                    productJsonArray = tempJson.get("product_categories").getAsJsonArray();
                                    if(productJsonArray != null && productJsonArray.size()>0){
                                        for(JsonElement productJsonElement: productJsonArray){
                                            if(!"".equals(productJsonElement.getAsString())){
                                                productTypeSet.add(productJsonElement.getAsString());
                                            }
                                        }
                                    }
                                }
                            } catch (RepositoryException e) {
                                LOG.error("Repository Exception:"+e);
                            }
                        }
                        LOG.info("storeTypeSet:"+storeTypeSet);
                        LOG.info("productTypeSet:"+productTypeSet);
                        LOG.info("countryPath:"+countryPath);
                        if(resultYesFlagshipJsonArray.size()>0){
                            resultJsonArray.addAll(resultYesFlagshipJsonArray);
                        }
                        if(resultNoFlagshipJsonArray.size()>0){
                            resultJsonArray.addAll(resultNoFlagshipJsonArray);
                        }
                        resultJsonObject.add("storeResults", resultJsonArray);
                        if(storeTypeSet != null && productTypeSet != null){
                            JsonArray filterJsonArray = getFilterJsonData(storeTypeSet, productTypeSet
                                    , resourceResolver, pageURL);
                            if(filterJsonArray != null){
                                resultJsonObject.add("filterListing", filterJsonArray);
                            }
                        }
                        // converting to json response
                        jsonData = gson.toJson(resultJsonObject);
                    }
                }
            }
        }
        return jsonData;
    }

    private String populateStoreResultFormat(String jsonData, Gson gson, ResourceResolver resourceResolver, String pageURL) {
        JsonParser parser = new JsonParser();
        JsonElement tradeElement = parser.parse(jsonData);
        JsonObject autosearchResultJsonObject = tradeElement.getAsJsonObject();
        JsonArray storeJsonObjects= null;
        JsonObject tempJson, resultJsonObject = new JsonObject();
        if(autosearchResultJsonObject.has("Items")){
            storeJsonObjects = autosearchResultJsonObject.getAsJsonArray("Items");
        }
        if(storeJsonObjects != null){

            Set<String> storeTypeSet = new HashSet<>();
            Set<String> productTypeSet = new HashSet<>();
            JsonArray productJsonArray = null;
            for(JsonElement storeElement : storeJsonObjects){
                tempJson = storeElement.getAsJsonObject();
                if(tempJson.has("store_type")){
                    storeTypeSet.add(tempJson.get("store_type").getAsString());
                }
                if(tempJson.has("product_categories") && tempJson.get("product_categories")!= null){
                    productJsonArray = tempJson.get("product_categories").getAsJsonArray();
                    if(productJsonArray != null && productJsonArray.size()>0){
                        for(JsonElement productJsonElement: productJsonArray){
                            if(!"".equals(productJsonElement.getAsString())){
                                productTypeSet.add(productJsonElement.getAsString());
                            }
                        }
                    }
                }
            }
            resultJsonObject.add("storeResults", storeJsonObjects);
            if(storeTypeSet != null && productTypeSet != null){
                JsonArray filterJsonArray = getFilterJsonData(storeTypeSet, productTypeSet
                        , resourceResolver, pageURL);
                if(filterJsonArray != null){
                    resultJsonObject.add("filterListing", filterJsonArray);
                }
            }
        }

        return gson.toJson(resultJsonObject);
    }

    private JsonArray getFilterJsonData(Set<String> storeTypeSet, Set<String> productTypeSet
            , ResourceResolver resourceResolver, String pageURL) {
        JsonArray filterJsonArray = new JsonArray();
        Map<String, Object> storeFilter = null;
        // get country specific data
        if(pageURL != null){
            Map<String, String> queryMap = new LinkedHashMap<>();
            queryMap.put("type", "nt:unstructured");
            queryMap.put("path", pageURL);
            queryMap.put("property", "sling:resourceType");
            queryMap.put("property.1_value", "apddemo/components/content/where-to-buy-component");

            GenericUtils.logQuery(LOG, queryMap);
            Session session = resourceResolver.adaptTo(Session.class);
            Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
            SearchResult result = query.getResult();
            List<Hit> hitsList = result.getHits();
            if(hitsList != null &&hitsList.size()>0){
                Hit hit = hitsList.get(0);
                ValueMap storeLocatorFilterNodeVM= null;
                try {
                    storeLocatorFilterNodeVM = hit.getResource().getValueMap();
                } catch (RepositoryException e) {
                    LOG.error("RepositoryException in getFilterJsonData:"+e);
                }
                if(storeLocatorFilterNodeVM != null){
                    storeFilter = getStoreFilter(storeLocatorFilterNodeVM);
                }else{
                    LOG.error("no storeLocatorFilterNodeVM node value");
                }
            }
        }

        // get matching
        if(storeFilter != null && storeFilter.size()>0){
            // populating distance data
            JsonObject tempJsonObject = new JsonObject();
            if(storeFilter.containsKey("distanceCategoryTitle")){
                String name= storeFilter.get("distanceCategoryTitle").toString();
                tempJsonObject.addProperty("title", name);
                name = name.toLowerCase();
                tempJsonObject.addProperty("key", "distance");
            }
            if(storeFilter.containsKey("distanceCategoryList") && storeFilter.get("distanceCategoryList")!= null){
                List<DistanceLocatorProductCategory> distanceCategoryList = (List<DistanceLocatorProductCategory>)
                        storeFilter.get("distanceCategoryList");
                JsonArray categoryArray = new JsonArray();
                JsonObject categoryJson = null;
                for(DistanceLocatorProductCategory category : distanceCategoryList){
                    categoryJson = new JsonObject();
                    categoryJson.addProperty("icon", category.getDistanceCategoryIconClass());
                    String distanceCategoryTitle = category.getDistanceCategoryTitle();
                    if(distanceCategoryTitle != null  ){
                        categoryJson.addProperty("value", distanceCategoryTitle);
                        categoryJson.addProperty("key"
                                , StringUtils.replaceSpecialCharacters(distanceCategoryTitle.toLowerCase()));
                    }
                    categoryArray.add(categoryJson);
                }
                tempJsonObject.add("child",categoryArray );
            }
            filterJsonArray.add(tempJsonObject);

            // populating store type data
            tempJsonObject = new JsonObject();
            if(storeFilter.containsKey("storeCategoryTitle")){
                String name= storeFilter.get("storeCategoryTitle").toString();
                tempJsonObject.addProperty("title", name);
                name = name.toLowerCase();
                tempJsonObject.addProperty("key", "store_type");
            }
            if(storeFilter.containsKey("storeCategoryList") && storeFilter.get("storeCategoryList")!= null){
                List<StoreLocatorStoreCategory> distanceCategoryList = (List<StoreLocatorStoreCategory>)
                        storeFilter.get("storeCategoryList");
                JsonArray categoryArray = new JsonArray();
                JsonObject categoryJson = null;
                for(StoreLocatorStoreCategory storeCategory : distanceCategoryList){
                    categoryJson = new JsonObject();
                    categoryJson.addProperty("icon", storeCategory.getStoreCategoryIconClass());
                    String distanceCategoryTitle = storeCategory.getStoreCategoryTitle();
                    if(distanceCategoryTitle != null && storeTypeSet.contains(distanceCategoryTitle)){
                        categoryJson.addProperty("value", distanceCategoryTitle);
                        categoryJson.addProperty("key"
                                , StringUtils.replaceSpecialCharacters(distanceCategoryTitle.toLowerCase()));
                        categoryArray.add(categoryJson);
                    }

                }
                tempJsonObject.add("child",categoryArray );
            }
            filterJsonArray.add(tempJsonObject);


            // populating product type data
            tempJsonObject = new JsonObject();
            if(storeFilter.containsKey("productCategoryTitle")){
                String name= storeFilter.get("productCategoryTitle").toString();
                tempJsonObject.addProperty("title", name);
                name = name.toLowerCase();
                tempJsonObject.addProperty("key", "product_categories");
            }
            if(storeFilter.containsKey("productCategoryList") && storeFilter.get("productCategoryList")!= null){
                List<StoreLocatorProductCategory> distanceCategoryList = (List<StoreLocatorProductCategory>)
                        storeFilter.get("productCategoryList");
                JsonArray categoryArray = new JsonArray();
                JsonObject categoryJson = null;
                for(StoreLocatorProductCategory productCategory : distanceCategoryList){
                    categoryJson = new JsonObject();
                    categoryJson.addProperty("icon", productCategory.getProductCategoryIconClass());
                    String distanceCategoryTitle = productCategory.getProductCategoryTitle();
                    if(distanceCategoryTitle != null && productTypeSet.contains(distanceCategoryTitle)){
                        categoryJson.addProperty("value", distanceCategoryTitle);
                        categoryJson.addProperty("key"
                                , StringUtils.replaceSpecialCharacters(distanceCategoryTitle.toLowerCase()));
                        categoryArray.add(categoryJson);
                    }

                }
                tempJsonObject.add("child",categoryArray );
            }
            filterJsonArray.add(tempJsonObject);
        }
        return filterJsonArray;
    }

    private JsonObject convertResourceToStoreJson(Resource store, Gson gson) {
        JsonObject jsonObject = new JsonObject();
        if(store != null){
            ValueMap valueMap = store.getValueMap();
            List<String> productList= null;
            for(Map.Entry<String, Object> entry : valueMap.entrySet()) {
                if(entry.getValue() != null){
                    if(entry.getKey().startsWith("product")){
                        if(productList == null){
                            productList = new ArrayList<>();
                        }
                        productList.add(entry.getValue().toString());
                        jsonObject.add("product_categories", gson.toJsonTree(productList));
                    }else if("storeType".equals(entry.getKey())){
                        jsonObject.addProperty("store_type", entry.getValue().toString());
                    }else {
                        jsonObject.addProperty(entry.getKey(), entry.getValue().toString());
                    }

                }
            }
        }
        return jsonObject;
    }

    public  Map<String, Object> getStoreFilter(ValueMap storeLocatorFilterProperties) {
        Map<String, Object> storeData  = new HashMap<>();
        Gson gson = new Gson();

        // store category List
        if(storeLocatorFilterProperties.containsKey("storeCategoryList")){

            if(storeLocatorFilterProperties.containsKey("storeTitle")){
                storeData.put("storeCategoryTitle", storeLocatorFilterProperties.get("storeTitle").toString());
            }
            String[] storeCategoryListsArray = storeLocatorFilterProperties
                    .get("storeCategoryList", String[].class);
            if(storeCategoryListsArray != null && storeCategoryListsArray.length>0) {
                TypeToken<List<StoreLocatorStoreCategory>> token = new TypeToken<List<StoreLocatorStoreCategory>>() {
                };
                storeData.put("storeCategoryList"
                        , gson.fromJson(Arrays.toString(storeCategoryListsArray), token.getType()));
            }
        }
        // product Category List
        if(storeLocatorFilterProperties.containsKey("productCategoryList")){
            if(storeLocatorFilterProperties.containsKey("productTitle")){
                storeData.put("productCategoryTitle",  storeLocatorFilterProperties.get("productTitle").toString());
            }
            String[] productCategoryListsArray = storeLocatorFilterProperties
                    .get("productCategoryList", String[].class);
            if(productCategoryListsArray != null && productCategoryListsArray.length>0) {
                TypeToken<List<StoreLocatorProductCategory>> token = new TypeToken<List<StoreLocatorProductCategory>>() {
                };
                storeData.put("productCategoryList"
                        , gson.fromJson(Arrays.toString(productCategoryListsArray), token.getType()));
            }
        }
        // distance Category List
        if(storeLocatorFilterProperties.containsKey("distanceCategoryList")){
            if(storeLocatorFilterProperties.containsKey("distanceTitle")){
                storeData.put("distanceCategoryTitle",  storeLocatorFilterProperties.get("distanceTitle").toString());
            }
            String[] distanceCategoryListsArray = storeLocatorFilterProperties
                    .get("distanceCategoryList", String[].class);
            if(distanceCategoryListsArray != null && distanceCategoryListsArray.length>0) {
                TypeToken<List<DistanceLocatorProductCategory>> token = new TypeToken<List<DistanceLocatorProductCategory>>() {
                };
                storeData.put("distanceCategoryList"
                        , gson.fromJson(Arrays.toString(distanceCategoryListsArray), token.getType()));
            }
        }
        return storeData;
    }
}
