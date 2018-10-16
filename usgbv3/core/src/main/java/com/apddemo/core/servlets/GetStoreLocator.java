package com.apddemo.core.servlets;


import com.apddemo.core.constants.ApplicationConstants;
import com.apddemo.core.services.StoreLocatorService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Get store search Store Locator",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths="+ "/bin/usgb/v3/storeSearch"
        })
public class GetStoreLocator extends SlingSafeMethodsServlet {
    private static Logger LOG = LoggerFactory.getLogger(GetStoreLocator.class);

    @Reference
    StoreLocatorService storeLocatorService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {
        RequestParameter keyParameter = request.getRequestParameter(ApplicationConstants.KEY);
        RequestParameter textParameter = request.getRequestParameter(ApplicationConstants.TEXT);
        RequestParameter countryParameter = request.getRequestParameter(ApplicationConstants.COUNTRY);
        RequestParameter currentLocationParameter = request.getRequestParameter(ApplicationConstants.CURRENT_LOCATION);

        String pageURL = null;
        if(request.getHeader("referer") != null){
            try {
                String refererURI = new URI(request.getHeader("referer")).getPath();
                if(refererURI != null && refererURI.endsWith(".html")){
                    pageURL = refererURI.replace(".html", "");
                }
            } catch (URISyntaxException e) {
                LOG.error("URI SyntaxExvception is :"+e);
            }
        }else{
            RequestParameter pageURLParameter = request.getRequestParameter(ApplicationConstants.PAGE_URL);
            if(pageURLParameter != null){
                pageURL = pageURLParameter.getString();
            }
        }
        String jsonResponse = "";
        if(countryParameter != null
                && (keyParameter != null || textParameter != null || currentLocationParameter != null)
                && pageURL != null){
            jsonResponse = storeLocatorService.getStoreSearch(keyParameter, textParameter, countryParameter
                    , currentLocationParameter, request.getResourceResolver(), pageURL);
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}
