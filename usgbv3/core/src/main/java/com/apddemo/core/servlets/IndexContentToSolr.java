package com.apddemo.core.servlets;
 
import java.io.IOException;
 
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.osgi.framework.Constants;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apddemo.core.configuration.SolrServerConfiguration;
import com.apddemo.core.services.SolrSearchService;
 
 
/**
 * 
 * This servlet acts as a bulk update to index content pages and assets to the
 * configured Solr server
 *
 */
 
@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Solr Index Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths="+ "/bin/solr/push/pages"
           })        
public class IndexContentToSolr extends SlingAllMethodsServlet {
 
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(IndexContentToSolr.class);
 
    @Reference
    SolrServerConfiguration solrConfigurationService;
 
    @Reference
    SolrSearchService solrSearchService;
 
    @Override
    protected void doGet(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws ServletException,
            IOException {
        doPost(request, response);
 
    }
 
    @Override
    protected void doPost(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws ServletException,
            IOException {
        response.setContentType("text/html");
        String indexType = request.getParameter("indexType");
        final String protocol = solrConfigurationService.getSolrProtocol();
        final String serverName = solrConfigurationService.getSolrServerName();
        final String serverPort = solrConfigurationService.getSolrServerPort();
        final String coreName = solrConfigurationService.getSolrCoreName();
        final String pagesResourcePath = solrConfigurationService
                .getContentPagePath();
        String URL = protocol + "://" + serverName + ":" + serverPort
                + "/solr/" + coreName;
        HttpSolrClient server = new HttpSolrClient(URL);
         
        if (indexType.equalsIgnoreCase("indexpages")) {
            try {
                JSONArray indexPageData = solrSearchService.crawlContent(pagesResourcePath, "cq:PageContent");
                 
                boolean resultindexingPages = solrSearchService.indexPagesToSolr(indexPageData, server);
                if (resultindexingPages == true) {
                    response.getWriter()
                            .write("<h3>Successfully indexed content pages to Solr server </h3>");
                } else {
                    response.getWriter().write("<h3>Something went wrong</h3>");
                }
            } catch (Exception e) {
                LOG.error("Exception due to", e);
                response.getWriter()
                        .write("<h3>Something went wrong. Please make sure Solr server is configured properly in Felix</h3>");
            }
 
        } else {
            response.getWriter().write("<h3>Something went wrong</h3>");
        }
 
    }
 
}