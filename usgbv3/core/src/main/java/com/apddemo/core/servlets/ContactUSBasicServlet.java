package com.apddemo.core.servlets;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.apddemo.core.services.CaptchaService;
import com.apddemo.core.services.ContactUSFormService;
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
import java.util.Enumeration;

@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Submit Basic contact us",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths="+ "/bin/usgb/v3/contactusbasic"
        })
public class ContactUSBasicServlet  extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ContactUSBasicServlet.class);

    @Reference
    ContactUSFormService contactUSFormService;

    @Reference
    CaptchaService captchaService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {

     /*   Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            LOG.info("Header Name - " + headerName + ", Value - " + request.getHeader(headerName));
        }

        Enumeration<String> params = request.getParameterNames();
        while(params.hasMoreElements()){
            String paramName = params.nextElement();
            LOG.info("Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
        }
*/
        String jsonData = "";
        if(request.getParameter("data") != null){
            String data = request.getParameter("data");
            JsonParser parser = new JsonParser();
            JsonObject inputData = parser.parse(data).getAsJsonObject();
            Boolean isValidCaptcha= false;
            //get captcha
            if(inputData != null && inputData.has("g-recaptcha-response")){
                JsonElement jsonElement = inputData.get("g-recaptcha-response");
                if(jsonElement != null){
                    String recaptchaResponse  = jsonElement.getAsString();
                    // call captcha service and verify
                    if(captchaService != null){
                        isValidCaptcha = captchaService.validateCaptcha(recaptchaResponse);
                    }
                    LOG.info("isValidCaptcha:"+isValidCaptcha);
                }
            }
            if(isValidCaptcha){
                // push the data to DB
                jsonData = contactUSFormService.submitContactUSBasicForm(inputData);
            }
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonData);
        out.flush();

    }
}
