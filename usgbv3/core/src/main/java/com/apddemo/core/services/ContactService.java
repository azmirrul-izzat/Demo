package com.apddemo.core.services;

import com.apddemo.core.models.ContactDetails;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public interface ContactService {

	public static final String KEYWORD_MULTIPLE_PRODUCTS = "Multiple Products";
	public static final String KEYWORD_SPECIFIC = "specific";
	
	public List<ContactDetails> getContactDetails(ResourceResolver resourceResolver, String nodePath, String enquiryType
			, String product);

}
