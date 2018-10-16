package com.apddemo.core.services;

import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.Map;

public interface StoreLocatorService {
    String getAllStates(String countryCode, ResourceResolver resourceResolver);

    String getAutoSearch(String pageURL, String text, ResourceResolver resourceResolver, boolean getResource);

    String getStoreSearch(RequestParameter pageURLParameter, RequestParameter textParameter, RequestParameter countryParameter, RequestParameter currentLocationParameter, ResourceResolver resourceResolver, String pageURL);
    Map<String, Object> getStoreFilter(ValueMap storeLocatorFilterProperties);
}
