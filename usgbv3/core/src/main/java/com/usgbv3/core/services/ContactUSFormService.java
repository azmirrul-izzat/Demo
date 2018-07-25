package com.usgbv3.core.services;

import com.google.gson.JsonObject;
import com.usgbv3.core.models.ContactUsForm;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public interface ContactUSFormService {
    String submitContactUSBasicForm(JsonObject inputData);

    String submitContactUSAdvancedForm(String data, SlingHttpServletRequest request);

    Boolean sendContactUSBatchEmail(String s, List<ContactUsForm> contactUsFormList, ResourceResolver adminResourceResolver);
}
