package com.apddemo.core.services;

import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;

public interface PagePropertyService {

	public final static String KEY_URI = "uri";
	public final static String KEY_PAGE_TITLE = "pageTitle";
	public final static String KEY_RESOURCE_LIST = "resourceList";
	public final static String KEY_COMPARABLE_PROP_LIST = "comparablePropList";
	public final static String KEY_CATEGORY_PARENT_LEVEL = "categoryParentLevel";
	public final static String KEY_CATEGORY_NAME = "categoryName";
	public final static String KEY_CQ_TAGS = "cq:tags";
	public final static String KEY_CATEGORY_PAGE_URI = "categoryPageUri";
//	public final static String KEY_ORDER_SAMPLE_EMAIL = "orderSampleEmail";
	public final static String KEY_ORDER_SAMPLE_CATEGORY = "orderSampleCategory";
	
	public List<Map<String, Object>> getMultiplePageProperties(ResourceResolver resourceResolver, List<String> pathList);
	public Map<String, Object> getPageProperties(ResourceResolver resourceResolver, String path);
}
