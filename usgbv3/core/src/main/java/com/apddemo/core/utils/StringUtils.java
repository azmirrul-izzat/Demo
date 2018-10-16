package com.apddemo.core.utils;

import org.apache.sling.api.SlingHttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class StringUtils {

	/**
	 * Checks if string is NOT null or NOT empty after trimming start and trailing spaces
	 * str = null, returns false;
	 * str = "", returns false;
	 * str = " ", returns false;
	 * str = "   ", returns false;
	 * str = " abc", returns true;
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str){
		return str != null && !str.trim().isEmpty();
	}

	/**
	 * Converts InputStream to String
	 * @param is
	 * @return
	 */
	public static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();



	}

	public static String replaceSpecialCharacters(String inputString){
		inputString = inputString.replace(" ", "_");
		//inputString = inputString.replace("(", "_");
		//inputString = inputString.replace(")", "_");
		return inputString;
	}

	/**
	 * Format string by a specifically ordered parameters
	 * eg. format "hello %02, %01 good %03"
	 * parameters firstname, lastname, afternoon
	 * would result to hello lastname, firstname good afternoon
	 *
	 * @param format
	 * @param parameters
	 * @return
	 */
	public static String formatString(String format, String... parameters){

		String formattedString = new String(format);

		if(parameters != null){
			for(int i=0; i < parameters.length; i++){
				String replaceValue = i < 9 ? "%0"+(i+1) : "%"+(i+1);
				if(formattedString != null){
					String value = parameters[i] != null ? parameters[i] : "";
					//value = convertToUTF8(value);
					formattedString = formattedString.replace(replaceValue, value);
				}
			}
		}

		return formattedString;
	}

	/**
	 * Format string by a specifically ordered parameters
	 * eg. format "hello %02, %01 good %03"
	 * parameters firstname, lastname, afternoon
	 * would result to hello lastname, firstname good afternoon
	 *
	 * @param format
	 * @param parameters
	 * @return
	 */
	public static String formatStringUTF8(String format, String... parameters){

		String formattedString = new String(format);

		if(parameters != null){
			for(int i=0; i < parameters.length; i++){
				String replaceValue = i < 9 ? "%0"+(i+1) : "%"+(i+1);
				if(formattedString != null){
					String value = parameters[i] != null ? parameters[i] : "";
					value = convertToUTF8(value);
					formattedString = formattedString.replace(replaceValue, value);
				}
			}
		}

		return formattedString;
	}

	// convert from internal Java String format -> UTF-8
	public static String convertToUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return out;
	}

	/**
	 * Checks if string is null or empty after trimming start and trailing spaces
	 * str = null, returns true;
	 * str = "", returns true;
	 * str = " ", returns true;
	 * str = "   ", returns true;
	 * str = " abc", returns false;
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str){
		return !isNotBlank(str);
	}

	public static String getRootSitePath(String currentPagePath) {
		if(currentPagePath != null && currentPagePath.indexOf("/content/demo/")!= -1){
			currentPagePath = currentPagePath.replace("/content/demo/", "");
		}
		String countryPath = null;
		if(currentPagePath.indexOf("/")!= -1){
			String[] pagePathValues = currentPagePath.split("/");
			if(pagePathValues != null && pagePathValues.length>0){
				countryPath = "/content/demo/"+pagePathValues[0];
			}
		}
		return countryPath;
	}

	public static String getReferrerURIfromRequest(SlingHttpServletRequest request) throws URISyntaxException {
		if(request.getHeader("referer") != null){
			String refererURI = new URI(request.getHeader("referer")).getPath();
			if(refererURI != null && refererURI.endsWith(".html")){
				refererURI = refererURI.replace(".html", "");
			}
			return refererURI;
		}
		return null;
	}
	
	/* Decode by reversing the initial order of replacements */
	public static String unescape(String value) {
		
		return value = value.replaceAll("\\x3E", ">")
		        .replaceAll("\\x3C", "<")
		        .replaceAll("\\x22", "\"")
		        .replaceAll("\\x27", "\'")
		        .replaceAll("\\pp", "%")
		        .replaceAll("\\x26", "&") /* These 5 replacements protect from HTML/XML. */
		        .replaceAll("\\u00A0", "\u00A0") /* Useful but not absolutely necessary. */
		        .replaceAll("\\n", "\n")
		        .replaceAll("\\t", "\t") /* These 2 replacements protect whitespaces. */
		        .replaceAll("\\", "");
	}

}