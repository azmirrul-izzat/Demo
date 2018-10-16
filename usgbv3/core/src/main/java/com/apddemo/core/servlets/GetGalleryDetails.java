package com.apddemo.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import java.util.*;

@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Get Gallery Detils",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths="+ "/bin/usgb/v3/galleryDetails"
        })
public class GetGalleryDetails  extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(GetGalleryDetails.class);

    @Reference
    QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {

        RequestParameter pageURLParameter = request.getRequestParameter("pageurl");
        String jsonData = "";
        if(pageURLParameter != null && queryBuilder != null){
            Resource galleryResource = getGalleryResourceFromPage(pageURLParameter, request.getResourceResolver());
            if(galleryResource != null){
                ValueMap galleryResourceValueMap = galleryResource.getValueMap();
                if(galleryResourceValueMap.containsKey("galleryPath")){
                    String galleryParenthPath = galleryResource.getValueMap().get("galleryPath", String.class);
                    LOG.info("galleryParenthPath:"+galleryParenthPath);
                    if(galleryParenthPath != null){
                        jsonData = getJsonDataOfGallery(galleryParenthPath, request.getResourceResolver(), galleryResourceValueMap);
                    }
                }

            }
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonData);
        out.flush();
    }


    /**
     * function to get gallery resource
     * @param pageURLParameter
     * @param resourceResolver
     * @return
     */
    private Resource getGalleryResourceFromPage(RequestParameter pageURLParameter, ResourceResolver resourceResolver) {
        Resource galleryResource = null;
        String pageURL  = pageURLParameter.getString();
        Map<String, String> queryMap = new LinkedHashMap<>();
        queryMap.put("type", "nt:unstructured");
        queryMap.put("path", pageURL);
        queryMap.put("property", "sling:resourceType");
        queryMap.put("property.1_value", "apddemo/components/content/gallery");

        Session session = resourceResolver.adaptTo(Session.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
        SearchResult result = query.getResult();
        List<Hit> hitsList = result.getHits();
        if(hitsList != null && hitsList.size()>0){
            Hit hit = hitsList.get(0);
            String galleryPath = null;
            try {
                galleryResource = hit.getResource();
            } catch (RepositoryException e) {
                LOG.error("RepositoryException in getGalleryParetPathFromPage:"+e);
            }

        }
        return  galleryResource;
    }

    /**
     * function to get json data from one parent galley path
     * @param galleryParenthPath
     * @param resourceResolver
     * @param galleryResourceValueMap
     * @return
     */
    private String getJsonDataOfGallery(String galleryParenthPath, ResourceResolver resourceResolver, ValueMap galleryResourceValueMap) {
        JsonObject finalJsonData= new JsonObject();
        JsonArray categoryDropdownJsonArray = new JsonArray();
        Map<String, JsonObject> specificsMap = new HashMap<>();
        Map<String, JsonObject> styleMap = new HashMap<>();
        JsonArray galleryList = new JsonArray();
        if(galleryParenthPath != null){
            Resource galleryParentPageResource = resourceResolver.resolve(galleryParenthPath);
            if(galleryParentPageResource != null){
                Page galleryParentPage = galleryParentPageResource.adaptTo(Page.class);
                Iterator<Page> pageIterator = galleryParentPage.listChildren(null, true);
                JsonObject tempJsonObject ;
                while(pageIterator.hasNext()){
                    Page childPage = pageIterator.next();
                   // LOG.info("childPage path :"+childPage.getPath());
                    Tag[] tags = childPage.getTags();
                    if(tags != null && tags.length>0){
                        for(int i=0; i< tags.length; i++){
                            Tag tag = tags[i];
                            //LOG.info("tag id:"+tag.getTagID());
                            if(tag.getTagID().indexOf(":specifics/") != -1){
                                // it is a specifics page.
                                if(!specificsMap.containsKey(tag.getName())){
                                    tempJsonObject = new JsonObject();
                                    tempJsonObject.addProperty("key", tag.getName());
                                    tempJsonObject.addProperty("value", tag.getTitle());
                                    specificsMap.put(tag.getName(), tempJsonObject);
                                }

                                break;
                            }else if(tag.getTagID().indexOf(":style/") != -1){
                                //it is a style page.
                                if(!styleMap.containsKey(tag.getTagID())){
                                    tempJsonObject = new JsonObject();
                                    tempJsonObject.addProperty("key", tag.getName());
                                    tempJsonObject.addProperty("value", tag.getTitle());
                                    tempJsonObject.addProperty("desc", tag.getDescription());
                                    styleMap.put(tag.getTagID(), tempJsonObject);
                                }
                                // add to the data
                                tempJsonObject = getPageProperiesForGallery(childPage, tag.getName(), resourceResolver);
                                galleryList.add(tempJsonObject);
                                // dont look for other tags
                                break;
                            }
                        }
                    }
                }
            }
        }
        LOG.info("specificsMap:"+specificsMap);
        LOG.info("styleMap:"+styleMap);

        // populating specifics json data
        if(specificsMap.size()>0){
            JsonObject specificsJson = new JsonObject();
            specificsJson.addProperty("category_key", "view");
            if(galleryResourceValueMap.containsKey("ddlabel1")){
                specificsJson.addProperty("category_title", galleryResourceValueMap.get("ddlabel1", String.class));
            }else{
                specificsJson.addProperty("category_title", "View By");
            }
            JsonArray optionValue = new JsonArray();
            // adding the all field
            JsonObject allJsonObject = new JsonObject();
            allJsonObject.addProperty("key", "all");
            if(galleryResourceValueMap.containsKey("ddtitle1")){
                allJsonObject.addProperty("value", galleryResourceValueMap.get("ddtitle1", String.class));
            }else{
                allJsonObject.addProperty("value", "All");
            }

            optionValue.add(allJsonObject);
            // adding the specifics populated from pages
            SortedSet<String> keys = new TreeSet<>(specificsMap.keySet());
            for (String key : keys) {
                optionValue.add(specificsMap.get(key));
            }
            specificsJson.add("child", optionValue);
            categoryDropdownJsonArray.add(specificsJson);
        }
        // populating specifics json data
        if(styleMap.size()>0){
            JsonObject styleJson = new JsonObject();
            styleJson.addProperty("category_key", "type");
            if(galleryResourceValueMap.containsKey("ddlabel2")){
                styleJson.addProperty("category_title", galleryResourceValueMap.get("ddlabel2", String.class));
            }else {
                styleJson.addProperty("category_title", "Type");
            }

            JsonArray optionValue = new JsonArray();
            // adding the all field
            JsonObject allJsonObject = new JsonObject();
            allJsonObject.addProperty("key", "all");
            if(galleryResourceValueMap.containsKey("ddtitle2")){
                allJsonObject.addProperty("value", galleryResourceValueMap.get("ddtitle2", String.class));
            }else{
                allJsonObject.addProperty("value", "All");
            }

            optionValue.add(allJsonObject);
            // adding the style populated from pages
            SortedSet<String> keys = new TreeSet<>(styleMap.keySet());
            for (String key : keys) {
                optionValue.add(styleMap.get(key));
            }
            styleJson.add("child", optionValue);
            categoryDropdownJsonArray.add(styleJson);
        }
        // adding the dropdown data
        finalJsonData.add("gallery_filter", categoryDropdownJsonArray);

        // adding the gallery data
        if(galleryList != null){
            finalJsonData.add("result", galleryList);
        }

        Gson gson = new Gson();
        return gson.toJson(finalJsonData);
    }

    /**
     * get page properties from gallery
     * @param childPage
     * @param tagName
     * @param resourceResolver
     * @return
     */
    private JsonObject getPageProperiesForGallery(Page childPage, String tagName, ResourceResolver resourceResolver) {
        ValueMap pageProperties = childPage.getContentResource().getValueMap();
        JsonObject tempJsonObject = new JsonObject();


        int mixedMediaCountInPage = getMixedMediaCountInPage(childPage.getPath(), resourceResolver);
        if(mixedMediaCountInPage>-1){
            tempJsonObject.addProperty("num", mixedMediaCountInPage);
        }

        if(pageProperties.containsKey("pageImage")){
            tempJsonObject.addProperty("img", pageProperties.get("pageImage").toString());
        }
        if(pageProperties.containsKey("jcr:title")) {
            tempJsonObject.addProperty("title", pageProperties.get("jcr:title").toString());
        }
        if(pageProperties.containsKey("jcr:description")) {
            tempJsonObject.addProperty("desc", pageProperties.get("jcr:description").toString());
        }

        Page parentPage = childPage.getParent();
        if(parentPage != null){
            Tag[] parentTags = parentPage.getTags();
            if(parentTags != null && parentTags.length>0){
                for(int i=0; i<parentTags.length; i++){
                    Tag parentTag = parentTags[i];
                    if(parentTag.getTagID().indexOf(":specifics/") != -1){
                        tempJsonObject.addProperty("view", parentTag.getName());
                        break;
                    }
                }
            }
        }

        tempJsonObject.addProperty("type", tagName);
        tempJsonObject.addProperty("link", childPage.getPath() + ".html");

        return tempJsonObject;
    }

    /**
     * this method would be used to get the count of images in mixed media component
     * @param path
     * @param resourceResolver
     * @return
     */
    private int getMixedMediaCountInPage(String path, ResourceResolver resourceResolver) {

        Map<String, String> queryMap = new LinkedHashMap<>();
        queryMap.put("type", "nt:unstructured");
        queryMap.put("path", path);
        queryMap.put("property", "sling:resourceType");
        queryMap.put("property.1_value", "apddemo/components/content/mix-media-player-component");

        Session session = resourceResolver.adaptTo(Session.class);
        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
        SearchResult result = query.getResult();
        List<Hit> hitsList = result.getHits();
        if(hitsList != null && hitsList.size()>0){
            Hit hit = hitsList.get(0);
            String galleryPath = null;
            try {
                Resource mixMediaResource = hit.getResource();
                if(mixMediaResource != null){
                    ValueMap mixMediaValueMap = mixMediaResource.getValueMap();
                    String[] mediaStringArray= null;
                    if(mixMediaValueMap.containsKey("imgPath")){
                        mediaStringArray = mixMediaValueMap.get("imgPath", String[].class);
                    }else if(mixMediaValueMap.containsKey("videoDam")){
                        mediaStringArray = mixMediaValueMap.get("videoDam", String[].class);
                    }else if(mixMediaValueMap.containsKey("thumbnail")){
                        mediaStringArray = mixMediaValueMap.get("thumbnail", String[].class);
                    }
                    if(mediaStringArray != null){
                        return mediaStringArray.length;
                    }
                }
            } catch (RepositoryException e) {
                LOG.error("RepositoryException in getGalleryParetPathFromPage:"+e);
            }
        }
        return -1;
    }

}
