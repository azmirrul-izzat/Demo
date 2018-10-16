package com.apddemo.core.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.apddemo.core.models.FeaturedProduct;
import com.apddemo.core.utils.CountryUtils;


public class CountryInfoComponent extends WCMUsePojo {
	private static final Logger LOG = LoggerFactory.getLogger(CountryInfoComponent.class);
	
	private Map<String, String> countryInfo;
	private String error;

	
	public Map<String, String> getCountryInfo() {
		return countryInfo;
	}

	public void setCountryInfo(Map<String, String> countryInfo) {
		this.countryInfo = countryInfo;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public void activate() throws Exception {		
		
		
		countryInfo = CountryUtils.retrieveDemoCountrybyPath(getResourceResolver(), getCurrentPage().getPath());
		

	}
	
	

}
