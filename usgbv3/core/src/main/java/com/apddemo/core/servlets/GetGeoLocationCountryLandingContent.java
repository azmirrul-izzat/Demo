package com.apddemo.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.apddemo.core.models.DynamicGeoLocationTile;
import com.apddemo.core.models.GlobalPageGeneralTileModel;
import com.apddemo.core.utils.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Get GEO country content data",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths="+ "/bin/usgb/v3/getGeoCountryContent"
        })
public class GetGeoLocationCountryLandingContent extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(GetGeoLocationCountryLandingContent.class);

    @Reference
    QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {

        RequestParameter countryParameter = request.getRequestParameter("country");
        String jsonData = "";
        try {
            String refererURI = StringUtils.getReferrerURIfromRequest(request);
            if(refererURI == null && request.getRequestParameter("pageurl")!= null){
                refererURI = request.getRequestParameter("pageurl").getString();
            }
            LOG.info("refererURI:"+refererURI);
            if (countryParameter != null && refererURI != null) {
                // get current page geolocationsection
                Resource resource = getGeoLocationSection(refererURI, request.getResourceResolver());
                LOG.info("resource:"+resource);
                ValueMap geoLocationValueMap = resource.getValueMap();
                String countryString = countryParameter.getString();
                LOG.info("countryString:"+countryString);
                LOG.info("geoLocationValueMap:"+geoLocationValueMap);
                if( geoLocationValueMap != null){
                    if(geoLocationValueMap.containsKey("dynamictilelist")){
                        String[] dynamictilelists = geoLocationValueMap.get("dynamictilelist", String[].class);
                        Gson gson = new Gson();
                        LOG.info("dynamictilelists:"+dynamictilelists);
                        TypeToken<List<DynamicGeoLocationTile>> token = new TypeToken<List<DynamicGeoLocationTile>>() {
                        };
                        List<DynamicGeoLocationTile> dynamicGeoLocationTileList = gson.fromJson(Arrays.toString(dynamictilelists), token.getType());
                        LOG.info("dynamicGeoLocationTileList:"+dynamicGeoLocationTileList);
                        DynamicGeoLocationTile matchingDynamicGeoTile = null;
                        for(DynamicGeoLocationTile dynamicGeoLocationTile : dynamicGeoLocationTileList){
                            if(countryString != null && countryString.equalsIgnoreCase(dynamicGeoLocationTile.getDyncountry())){
                                matchingDynamicGeoTile = dynamicGeoLocationTile;
                                break;
                            }
                        }
                        if(matchingDynamicGeoTile != null){
                            // populate json
                            JsonObject resultJsonObject = new JsonObject();
                            resultJsonObject.addProperty("title", matchingDynamicGeoTile.getGeodynatitle());
                            resultJsonObject.addProperty("description", matchingDynamicGeoTile.getGeodynadescription());
                            resultJsonObject.addProperty("bgimage", matchingDynamicGeoTile.getGeodynabackgroundimage());
                            jsonData = gson.toJson(resultJsonObject);
                        }else{
                            JsonObject resultJsonObject = new JsonObject();
                            if(geoLocationValueMap.containsKey("geodefaulttitle") && geoLocationValueMap.get("geodefaulttitle") != null){
                                resultJsonObject.addProperty("title", (String)geoLocationValueMap.get("geodefaulttitle"));
                            }
                            if(geoLocationValueMap.containsKey("geodefaultdescription") && geoLocationValueMap.get("geodefaultdescription") != null){
                                resultJsonObject.addProperty("description", (String)geoLocationValueMap.get("geodefaultdescription"));
                            }
                            if(geoLocationValueMap.containsKey("geodefaultbackgroundimage") && geoLocationValueMap.get("geodefaultbackgroundimage") != null){
                                resultJsonObject.addProperty("bgimage", (String)geoLocationValueMap.get("geodefaultbackgroundimage"));
                            }
                            jsonData = gson.toJson(resultJsonObject);
                        }
                    }
                }
            }
        } catch (URISyntaxException e) {
            LOG.error("URI Syntax Exception is :"+e);
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonData);
        out.flush();
    }

    private Resource getGeoLocationSection(String pageURL, ResourceResolver resourceResolver) {
        Resource geoLocationResource = null;
        Map<String, String> queryMap = new LinkedHashMap<>();
        queryMap.put("type", "nt:unstructured");
        queryMap.put("path", pageURL);
        queryMap.put("property", "sling:resourceType");
        queryMap.put("property.1_value", "apddemo/components/content/geolocationsection");

        Session session = resourceResolver.adaptTo(Session.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
        SearchResult result = query.getResult();
        List<Hit> hitsList = result.getHits();
        if(hitsList != null && hitsList.size()>0){
            Hit hit = hitsList.get(0);
            try {
                geoLocationResource = hit.getResource();
            } catch (RepositoryException e) {
                LOG.error("RepositoryException in getGeoLocationSection:"+e);
            }

        }
        return geoLocationResource;
    }

}
