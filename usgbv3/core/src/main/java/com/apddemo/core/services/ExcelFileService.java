package com.apddemo.core.services;

import org.apache.sling.api.resource.ResourceResolver;

public interface ExcelFileService {

	public static final String PROPERTY_GENERATED_KEY = "generated";
	public static final String PROPERTY_GENERATED_VALUE = "exceltonode";
	
	public boolean parseExcelToNodes(String excelPath, String targetPath,
			String parentName, String childName, ResourceResolver resourceResolver);
	public boolean parseExcelToNodes(String excelPath, String targetPath,
			String parentName, String childName, ResourceResolver resourceResolver, boolean dontLogoutSession);
}
