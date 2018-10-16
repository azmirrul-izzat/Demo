package com.apddemo.core.servlets;

import com.apddemo.core.constants.ApplicationConstants;
import com.apddemo.core.services.StoreLocatorService;
import com.apddemo.core.utils.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Get Auto Complete Store Locator",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths="+ "/bin/usgb/v3/storeAutoComplete"
        })
public class GetAutoSearchStoreSearch extends SlingSafeMethodsServlet {
    private static Logger LOG = LoggerFactory.getLogger(GetAutoSearchStoreSearch.class);

    @Reference
    StoreLocatorService storeLocatorService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        RequestParameter textParameter = request.getRequestParameter(ApplicationConstants.TEXT);
       RequestParameter pageURL = request.getRequestParameter(ApplicationConstants.PAGE_URL);
        String jsonResponse = "";
        if(textParameter != null && request.getHeader("referer") != null){
            try {
                String refererURI = StringUtils.getReferrerURIfromRequest(request);
                LOG.info("refererURI = " + refererURI);
                jsonResponse = storeLocatorService.getAutoSearch(refererURI
                        , textParameter.getString(), request.getResourceResolver(),false);
                
            } catch (URISyntaxException e) {
                LOG.error("URI SyntaxExvception is :"+e);
            }

        }
      /*  jsonResponse = storeLocatorService.getAutoSearch(pageURL.getString()
                , textParameter.getString(), request.getResourceResolver());*/

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }


}
