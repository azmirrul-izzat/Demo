package com.apddemo.core.servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.ServerException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
//import org.languagetool.JLanguageTool;
//import org.languagetool.language.BritishEnglish;
//import org.languagetool.rules.Rule;
//import org.languagetool.rules.RuleMatch;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.Gson;
import com.apddemo.core.constants.ApplicationConstants;
import com.apddemo.core.daos.UserInfoDao;
import com.apddemo.core.models.SearchContentModel;
import com.apddemo.core.models.Stack;
import com.apddemo.core.services.RestletService;
import com.apddemo.core.services.SSOConfigurationService;
import com.apddemo.core.services.SearchService;
import com.apddemo.core.spelling.Configuration;
import com.apddemo.core.spelling.Language;
import com.apddemo.core.spelling.SpellChecker;
import com.apddemo.core.spelling.SpellCorrection;
import com.apddemo.core.spelling.SpellRequest;
import com.apddemo.core.spelling.SpellResponse;
import com.apddemo.core.utils.CountryUtils;
import com.apddemo.core.utils.Dictionary;
import com.apddemo.core.utils.Search;
import com.apddemo.core.utils.StringUtils;


//localhost:4502/bin/usgb/v3/search
/*
 * This servlet is called from /etc/designs/usgb/clientlib-site/js/sso.app.js 
 * We would be getting the access_token and domain from the request params which would be used for operation here
 * This class would get the user info.
 */
@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=Search Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths="+ "/bin/usgb/v3/search"
})
public class SearchServlet extends BaseAllMethodsServlet {
	private static final long serialVersionUID = 1452364151988577055L; 
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	
	//for auto suggest START
	private static String wordsFileName = "/config-node/auto-correct.txt"; // Word list text file
	private static String[] words; // Word list array
	private static Stack<String> suggestions = new Stack<String>(50);
	
	private Dictionary dict;
    final static String filePath = "d:/desktop/words.txt";
    final static char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	//for auto suggest END
	
	@Reference
    private QueryBuilder builder;
	
	private static String defaultSearchImage = "/content/dam/demo/Global/Website/Images/v3/logo/default_search.jpg";
	private static String defaultPdfIcon = "/content/dam/demo/Global/Website/Images/v3/logo/pdf-icon.png";
	
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServerException,
			IOException {
				LOG.info("SearchServlet Start");
			try {
				response.setCharacterEncoding("utf-8");
				
				String pageURL = null;
		        if(request.getHeader("referer") != null){
		            try {
		                String refererURI = new URI(request.getHeader("referer")).getPath();
		                if(refererURI != null && refererURI.endsWith(".html")){
		                    pageURL = refererURI.replace(".html", "");
		                }
		            } catch (URISyntaxException e) {
		                LOG.error("URI SyntaxExvception is :"+e);
		            }
		        }else{
		            RequestParameter pageURLParameter = request.getRequestParameter(ApplicationConstants.PAGE_URL);
		            if(pageURLParameter != null){
		                pageURL = pageURLParameter.getString();
		            }
		        }
//		        
//		        Map<String, String> countryInfo = CountryUtils.retrieveDemoCountrybyPath(request.getResourceResolver(), pageURL);
//		        String countryHomePagePath = countryInfo.get("homepageUrl").toString();
		        
//		        String countryHomePagePath = "/content/demo/en_au/search";
		        String countryHomePagePath = pageURL;
		        LOG.info("SearchServlet countryHomePagePath : " + countryHomePagePath);
		        Map<String, String> countryInfo = CountryUtils.retrieveDemoCountrybyPath(request.getResourceResolver(), countryHomePagePath);
		        LOG.info("SearchServlet countryInfo : " + countryInfo.size());
//				String rawkeyword = request.getRequestParameter("text").toString();
//				String keyword = StringUtils.unescape(rawkeyword);
		        String keyword = request.getRequestParameter("text").toString();
				String category = request.getRequestParameter("category").toString();
				
				keyword = keyword.replace("-", " ");
				JSONObject jsonObj = new JSONObject();

				jsonObj.put("path", countryHomePagePath);
				jsonObj.put("searched_text", keyword);
				
				if("content".equalsIgnoreCase(category)) {
					String path = countryInfo.get("sitePath").toString();
					jsonObj = generateContentListbyKeyword(request.getResourceResolver(), path, keyword);
					
				}else if("doc_finder".equalsIgnoreCase(category)) {
					LOG.info("In doc_finder");
					String path = countryInfo.get("sitePath").toString();
//					LOG.info("doc_finder = " + path);
					jsonObj = generateDocFinderListbyKeyword(request.getResourceResolver(), path, keyword);
					
				}else {
					this.setJsonResponse(response, "{\"ERROR\": Unknown Category\"}", 500);
				}
				
				try {
					String autocompletepath = countryInfo.get("sitePath").toString() + wordsFileName;
					
					dict = new Dictionary();
					dict.build(request.getResourceResolver(), autocompletepath);
					
					if (dict.contains(keyword)) {
						jsonObj.put("suggestionsStatus", false);
		                System.out.println("\n" + keyword + " is spelled correctly");
		            } else {
		                jsonObj.put("suggestionsStatus", true);
		                System.out.print("is not spelled correctly, ");
//		                System.out.println(printSuggestions(keyword));
		                ArrayList<String> suggestionList = makeSuggestions(keyword);
		                
		                JSONArray suggestions = new JSONArray();
		                for(String sugg : suggestionList) {
		                	suggestions.put(sugg);
		                }
		                jsonObj.put("suggestionsList", suggestions);
		                
		                
		            }
					
					Configuration config = new Configuration(); 
					config.setProxy( "my_proxy_host", 8080, "http" );
					SpellChecker checker = new SpellChecker(config);
			        checker.setOverHttps( true ); // Now default is true
			        checker.setLanguage( Language.ENGLISH ); // Default is English

			        SpellRequest requestChecker = new SpellRequest();
			        requestChecker.setText( "helloo helloo worlrd" );
			        requestChecker.setIgnoreDuplicates( true );

			        SpellResponse spellResponse = checker.check( requestChecker );

			        for( SpellCorrection sc : spellResponse.getCorrections() ) {
				            System.out.println( sc.getValue() );
				    }
					
//					JLanguageTool langTool = new JLanguageTool(new BritishEnglish());
//	                for (Rule rule : langTool.getAllRules()) {
//	                  if (!rule.isDictionaryBasedSpellingRule()) {
//	                    langTool.disableRule(rule.getId());
//	                  }
//	                }
//	                List<RuleMatch> matches = langTool.check("A speling error");
//	                JSONArray toPosList = new JSONArray();
//	                JSONArray suggestedReplacementList = new JSONArray();
//	                
//	                for (RuleMatch match : matches) {
//	                	
//	                  toPosList.put(match.getToPos());
//	                  System.out.println("Potential typo at characters " +
//	                      match.getFromPos() + "-" + match.getToPos() + ": " +
//	                      match.getMessage());
//	                  
//	                  suggestedReplacementList.put(match.getSuggestedReplacements());
//	                  System.out.println("Suggested correction(s): " +
//	                      match.getSuggestedReplacements());
//	                }
//	                jsonObj.put("toPosList", toPosList);
//	                jsonObj.put("suggestedReplacementList", suggestedReplacementList);
					
					
					
//					loadWords(request.getResourceResolver(),autocompletepath);
//					
//					int result = Search.binarySearch(words, keyword);
//
//			        // First, check for an exact match
//			        if (result != -1) {
//			        	jsonObj.put("autoCorrectStatus", "Correct. Congratulations, you can spell!");
//			            System.out.println("Correct. Congratulations, you can spell!"
//			                + "\n");
//			        }
//			        // Else, check if the user's word is an anagram of a dictionary word
//			        else {
//			            for (String word : words) {
//			                char wordStart; // First char of word
//			                char userWordStart; // First char of userWord
//			                if (!word.isEmpty()) { // If word is NOT empty
//			                    wordStart = word.charAt(0);
//			                    userWordStart = keyword.charAt(0);
//
//			                    // if (userWordStart == wordStart) // Same starting char
//			                        if (keyword.length() == word.length()) // Same length
//			                            if (containsAllChars(keyword, word)) // Same chars
//			                                suggestions.push(word);
//			                }
//			            }
//
//			            if (suggestions.isEmpty()) {
//			            	jsonObj.put("autoCorrectStatus", "No suggestions.");
//			                System.out.println("No suggestions.\n");
//			            }
//			            else {
//			                System.out.print("Suggestions: ");
//			                JSONArray suggestionList = new JSONArray();
//			                while (!suggestions.isEmpty()) {
////			                    System.out.print(suggestions.pop() + " ");
//			                    suggestionList.put(suggestions.pop());
//			                }
//			            	jsonObj.put("autoCorrect", suggestionList);
////			                System.out.println("\n");
//			            }
//			        }
					
				}catch (Exception e) {
					jsonObj.put("autoCorrectERROR", e.getMessage());
					LOG.error("AUTO SUGGEST : " + e.getMessage());
				}
				
				
				this.setJsonResponseOk(response,jsonObj.toString());
				
			}catch(Exception e){
				this.setJsonResponse(response, "{\"ERROR\":\"exception"+ e.getMessage() + "\"}", 500);
			}
		
	}
	
	private JSONObject generateContentListbyKeyword(ResourceResolver resourceResolver, String path, String keyword) throws JSONException{
		
		JSONObject apiResponse = new JSONObject();
		Gson gson = new Gson();
		try {


			int totalMatchforContent = 0;
			
			SearchResult resultPages = queryContent(resourceResolver, path, keyword, "-1");

			
			int totalMatchforPage = (int) resultPages.getTotalMatches();
			LOG.info("queryContent result = " + totalMatchforPage);


			JSONArray jsonArrayProduct = new JSONArray();
			JSONArray jsonArrayFilter = new JSONArray();
			
			if(totalMatchforPage > 0){
				
				
				JSONObject jsonFilterType = new JSONObject();
				jsonFilterType.put("key", "type");
				jsonFilterType.put("title", "Type");
				
				JSONArray typeChild = new JSONArray();
				JSONObject pageType = new JSONObject();
				pageType.put("key", "page");
				pageType.put("name", "Page");
				typeChild.put(pageType);
				jsonFilterType.put("child", typeChild);
				
				jsonArrayFilter.put(jsonFilterType);
				
				int hitNo = 0;
				for(Hit hit : resultPages.getHits()){
					ValueMap hitProperties = hit.getProperties();
					
					//Skip if excludeSearch at pageproperties is ticked
					if(hitProperties.containsKey("excludeSearch")) {
						continue;
					}
					totalMatchforContent++;
					
					JSONObject searchContentJson = new JSONObject();
//					SearchContentModel searchContent = new SearchContentModel();
					
					searchContentJson.put("key", "content_" + hitNo);
					searchContentJson.put("title", hitProperties.get("jcr:title", String.class));
					
					if(hitProperties.containsKey("pageDescription")) {
						searchContentJson.put("description", hitProperties.get("pageDescription", String.class));
					}else {
						searchContentJson.put("description", hitProperties.get("jcr:description", String.class));
					}
					
					if(hitProperties.containsKey("pageImage")) {
						searchContentJson.put("image", hitProperties.get("pageImage", String.class));
					}else {
						searchContentJson.put("image", defaultSearchImage);
					}
					
					searchContentJson.put("link", hit.getPath() + ".html");
					searchContentJson.put("type", "page");
					
					Calendar calendar = hitProperties.get("jcr:created", Calendar.class);
					Date date = (Date) calendar.getTime();
//					searchContent.setCreated_date(formatter.format(date));
					searchContentJson.put("created_date", formatter.format(date));
					
					
						
					JSONObject pageCategory = getPageCategory(resourceResolver, path, hit.getPath());
//					LOG.info("Content pageCategory = " + pageCategory);
					
					searchContentJson.put("category_header", pageCategory.get("name").toString());
					searchContentJson.put("category", pageCategory.get("key").toString());
					
					boolean addCategory = false;
					
					for (int i = 0 ; i < jsonArrayFilter.length(); i++) {
						
						if("category".equalsIgnoreCase(jsonArrayFilter.getJSONObject(i).get("key").toString())) {
							
							if(jsonArrayFilter.getJSONObject(i).has("child")){
								
								JSONArray categoryList = (JSONArray) jsonArrayFilter.getJSONObject(i).get("child");
								categoryList.put(pageCategory);
								
								jsonArrayFilter.getJSONObject(i).remove("child");
								jsonArrayFilter.getJSONObject(i).put("child", categoryList);
								addCategory = true;
								break;
							}else {
								
								JSONArray categoryList = new JSONArray();
								categoryList.put(pageCategory);
								jsonArrayFilter.getJSONObject(i).put("child", categoryList);
								addCategory = true;
								break;
							}
							
						}
						
					}
					
					if(!addCategory) {
						
						JSONObject jsonFilterCategory = new JSONObject();
						jsonFilterCategory.put("key", "category");
						jsonFilterCategory.put("title", "Category");
						
						JSONArray categoryList = new JSONArray();
						categoryList.put(pageCategory);
						jsonFilterCategory.put("child", categoryList);
						jsonArrayFilter.put(jsonFilterCategory);
						
					}
						
					
					jsonArrayProduct.put(searchContentJson);
					hitNo++;
				}
				
				
				
				
				
			}
			
			// get data from hero-banner-component and video-component
			SearchResult resultVideo = queryVideo(resourceResolver, path, keyword);
			
			LOG.info("videooo = " + resultVideo);
			
			int totalMatchforVideo = (int) resultVideo.getTotalMatches();


			LOG.info("videooo count = " + totalMatchforVideo);
			if(totalMatchforVideo > 0){
				
				
				int hitVideoNo = 0;
				for(Hit hit : resultVideo.getHits()){
					
					ValueMap hitProperties = hit.getProperties();
					JSONObject searchContentJson = new JSONObject();
//					SearchContentModel searchContent = new SearchContentModel();
					
					if(hitProperties.containsKey("videoDam")) {
						
						
						
						Resource res = resourceResolver.getResource(hitProperties.get("videoDam", String.class));
						Asset asset = res.adaptTo(Asset.class);
						
						
						
						String videoTitle = "";
						if(hitProperties.containsKey("title")) {
//							searchContentJson.put("title", hitProperties.get("title", String.class));
							videoTitle = hitProperties.get("title", String.class);
							
						}else {
//							searchContentJson.put("title", asset.getMetadataValue("dc:title"));
							if(asset.getMetadataValue("dc:title") != null) {
								videoTitle = asset.getMetadataValue("dc:title");
							}else {
								videoTitle = asset.getName();
							}
						}
						LOG.info("VIDEO COMPARE = " + videoTitle.toLowerCase() + " | " + keyword);
						if(videoTitle.toLowerCase().contains(keyword)) {
							
							// Setup the filter for video
							boolean addVideo = false;
							for (int i = 0 ; i < jsonArrayFilter.length(); i++) {
								
								
								if("type".equalsIgnoreCase(jsonArrayFilter.getJSONObject(i).get("key").toString())) {
									
									
									JSONArray videoList = (JSONArray) jsonArrayFilter.getJSONObject(i).get("child");
									
									JSONObject videoFilter = new JSONObject();
									videoFilter.put("key", "video");
									videoFilter.put("name", "Video");
									videoList.put(videoFilter);
									
									jsonArrayFilter.getJSONObject(i).remove("child");
									jsonArrayFilter.getJSONObject(i).put("child", videoList);
									addVideo = true;
									break;
									
								}
							}
							
							if(!addVideo) {
								
								JSONObject jsonFilterType = new JSONObject();
								jsonFilterType.put("key", "type");
								jsonFilterType.put("title", "Type");
								
								JSONArray typeChild = new JSONArray();
								JSONObject pageType = new JSONObject();
								pageType.put("key", "video");
								pageType.put("name", "Video");
								typeChild.put(pageType);
								jsonFilterType.put("child", typeChild);
							}
							
							
							totalMatchforContent++;
							searchContentJson.put("key", "video_" + hitVideoNo);
							searchContentJson.put("title", videoTitle);
							if(hitProperties.containsKey("pageDescription")) {
								searchContentJson.put("description", hitProperties.get("pageDescription", String.class));
							}else {
								
								if(asset.getMetadataValue("jcr:description") != null) {
									searchContentJson.put("description", asset.getMetadataValue("jcr:description"));
								}else {
									searchContentJson.put("description", asset.getMetadataValue("dc:description"));
								}
								
							}
							
							if(hitProperties.containsKey("pageImage")) {
								searchContentJson.put("image", hitProperties.get("pageImage", String.class));
							}else {
								if(asset.getRendition("cq5dam.thumbnail.319.319.png") != null) {
									
									searchContentJson.put("image", asset.getRendition("cq5dam.thumbnail.319.319.png").getPath());
								}else {
									searchContentJson.put("image", defaultSearchImage);
								}
								
							}
							
							if(hitProperties.containsKey("videoDam")) {
								searchContentJson.put("link", hitProperties.get("videoDam", String.class));
							}
							
							searchContentJson.put("type", "video");
							hitVideoNo++;
							
							
							jsonArrayProduct.put(searchContentJson);
							
						}
						
						
					}
					
					
				}
			}
			
			// get data from main-banner-component and mix-media-player-component
			SearchResult resultMultifieldVideo = queryMultifieldVideo(resourceResolver, path, keyword);
			
			LOG.info("videooo = " + resultVideo);
			
			int totalMatchforMultifieldVideo = (int) resultVideo.getTotalMatches();


			LOG.info("Multifield videooo count = " + totalMatchforMultifieldVideo);
			if(totalMatchforMultifieldVideo > 0){
				
				
				int hitMultifieldVideoNo = 0;
				for(Hit hit : resultMultifieldVideo.getHits()){

					JSONObject searchContentJson = new JSONObject();
					Node hitNode = hit.getNode();
					NodeIterator ni =  hitNode.getNodes() ; 
					

				    while (ni.hasNext()) {
				        Node child = (Node)ni.nextNode(); 
				         
				        
				        NodeIterator ni2 =  child.getNodes() ; 
				        while (ni2.hasNext()) {
					        Node child2 = (Node)ni2.nextNode(); 
					        
					        if(child2.hasProperty("enableVideo") && 
									("yes".equalsIgnoreCase(child2.getProperty("enableVideo").getString()) || "true".equalsIgnoreCase(child2.getProperty("enableVideo").getString()))) {
								
								
					        	if(child2.hasProperty("videoDam") && !(child2.getProperty("videoDam").getString().isEmpty())){
					        		
					        		String videoDam = child2.getProperty("videoDam").getString();
									searchContentJson.put("link", videoDam);
					        		
					        		Resource res = resourceResolver.getResource(videoDam);
									Asset asset = res.adaptTo(Asset.class);
									
									
									
									String videoTitle = "";
									if(child2.hasProperty("title") && !(child2.getProperty("title").getString().isEmpty())){
										videoTitle = child2.getProperty("title").getString();
										
									}
									else {
										if(asset.getMetadataValue("dc:title") != null) {
											videoTitle = asset.getMetadataValue("dc:title");
										}else {
											videoTitle = asset.getName();
										}
									}
									LOG.info("VIDEO COMPARE = " + videoTitle.toLowerCase() + " | " + keyword);
									if(videoTitle.toLowerCase().contains(keyword)) {
										
										// Setup the filter for video
										boolean addVideo = false;
										for (int i = 0 ; i < jsonArrayFilter.length(); i++) {
											
											
											if("type".equalsIgnoreCase(jsonArrayFilter.getJSONObject(i).get("key").toString())) {
												
												
												JSONArray videoList = (JSONArray) jsonArrayFilter.getJSONObject(i).get("child");
												
												JSONObject videoFilter = new JSONObject();
												videoFilter.put("key", "video");
												videoFilter.put("name", "Video");
												videoList.put(videoFilter);
												
												jsonArrayFilter.getJSONObject(i).remove("child");
												jsonArrayFilter.getJSONObject(i).put("child", videoList);
												addVideo = true;
												break;
												
											}
										}
										
										if(!addVideo) {
											
											JSONObject jsonFilterType = new JSONObject();
											jsonFilterType.put("key", "type");
											jsonFilterType.put("title", "Type");
											
											JSONArray typeChild = new JSONArray();
											JSONObject pageType = new JSONObject();
											pageType.put("key", "video");
											pageType.put("name", "Video");
											typeChild.put(pageType);
											jsonFilterType.put("child", typeChild);
										}
										
										
										totalMatchforContent++;
										searchContentJson.put("key", "video_" + hitMultifieldVideoNo);
										searchContentJson.put("title", videoTitle);
										if(child2.getProperty("description") != null && !(child2.getProperty("description").getString().isEmpty())){
											searchContentJson.put("description", child2.getProperty("description").getString());
										}else {
											
											if(asset.getMetadataValue("jcr:description") != null) {
												searchContentJson.put("description", asset.getMetadataValue("jcr:description"));
											}else {
												searchContentJson.put("description", asset.getMetadataValue("dc:description"));
											}
											
										}
										
										if(asset.getRendition("cq5dam.thumbnail.319.319.png") != null) {
											
											searchContentJson.put("image", asset.getRendition("cq5dam.thumbnail.319.319.png").getPath());
										}else {
											searchContentJson.put("image", defaultSearchImage);
										}
																					
										searchContentJson.put("type", "video");
										hitMultifieldVideoNo++;
										
										
										jsonArrayProduct.put(searchContentJson);
										
									}
					        	}
								
							}
				        }
				    }					
					
				}
			}
			
			
			//get relevent in Doc Finder
			
			JSONArray jsonArrayReleventDF = new JSONArray();
			SearchResult resultDocFinder = queryDocFinder(resourceResolver, path, keyword, "-1");
			
			int totalMatchforDocFinder = (int) resultDocFinder.getTotalMatches();
			if(totalMatchforDocFinder > 0){
				
				int hitNo = 0;
				for(Hit hit : resultDocFinder.getHits()){
					
					 Node hitNode =  hit.getNode();
					 NodeIterator ni =  hitNode.getNodes(); 
				 
					 while (ni.hasNext()) {
				        Node child = (Node)ni.nextNode(); 
				         
				        NodeIterator ni2 =  child.getNodes() ; 
				        while (ni2.hasNext()) {
					        Node child2 = (Node)ni2.nextNode(); 
					        
					        if(child2.hasProperty("docPath") && hitNo <= 2) {
					        	
					        	JSONObject searchContentJson = new JSONObject();
					        	
	//				        	jsonArrayFilter.put("getPath  = " + child2.getPath());
	//				        	jsonArrayFilter.put("docPath  = " + child2.getProperty("docPath").getString());
	
					        	String docPath = child2.getProperty("docPath").getString();
					        	
					        	Resource res = resourceResolver.getResource(docPath);
								Asset asset = res.adaptTo(Asset.class);
								Node assetNode = resourceResolver.getResource(docPath).adaptTo(Node.class);
								
								searchContentJson.put("key", "docFinder_" + hitNo);
								searchContentJson.put("link", docPath);
	
								if(child2.hasProperty("thumbnail")) {
									searchContentJson.put("image", child2.getProperty("thumbnail").getString());
								}else {
									searchContentJson.put("image", defaultPdfIcon);
								}
								
								if(!(asset.getMetadataValue("dc:title").isEmpty())) {
									searchContentJson.put("title", asset.getMetadataValue("dc:title"));
									
								}else {
									searchContentJson.put("title", asset.getName());
								}
						        
								if(!(searchContentJson.get("title").toString().toLowerCase()).contains(keyword.toLowerCase())){
					        		continue;
					        	}
								
								searchContentJson.put("description", asset.getMetadataValue("dc:description"));
								
														
								JSONObject pageCategory = getPageCategory(resourceResolver, path, hit.getPath());
//								LOG.info("type" + pageCategory);
								searchContentJson.put("type", pageCategory.get("key").toString());
								
								
								
								Calendar calendar = assetNode.getProperty("jcr:created").getDate();
								Date date = (Date) calendar.getTime();
	//							searchContent.setCreated_date(formatter.format(date));
								searchContentJson.put("created_date", formatter.format(date));
														
									
								
								jsonArrayReleventDF.put(searchContentJson);
								hitNo++;
								
					        }
					        
				        }
				    }
				}
			}
			//remove duplicate Json
			for (int i = 0 ; i < jsonArrayFilter.length(); i++) {
				
				if(jsonArrayFilter.getJSONObject(i).has("child")){
					
					JSONArray categoryList = (JSONArray) jsonArrayFilter.getJSONObject(i).get("child");
					categoryList = removeDuplicateFilter(categoryList);
					
					jsonArrayFilter.getJSONObject(i).remove("child");
					jsonArrayFilter.getJSONObject(i).put("child", categoryList);
				}
			}
			
			

			if(totalMatchforContent > 0) {
				apiResponse.put("result", true);
			}else {
				apiResponse.put("result", false);
			}
			
			apiResponse.put("path", path);
			apiResponse.put("single_video", totalMatchforVideo);
			apiResponse.put("multifield_video", totalMatchforMultifieldVideo);
			apiResponse.put("keyword", keyword);
			apiResponse.put("num_result", totalMatchforContent);
			apiResponse.put("filter_listing", jsonArrayFilter);
			apiResponse.put("content", jsonArrayProduct);
			apiResponse.put("doc_finder", jsonArrayReleventDF);
			
		} catch (Exception e) {
			LOG.info("result = " + e.getMessage());
			e.printStackTrace();
		}
		
		
		
		
		return apiResponse;
	}
	
	public SearchResult queryContent(ResourceResolver resourceResolver, String path, String keyword, String limit) {

		Session session = resourceResolver.adaptTo(Session.class);
		Map<String, String> queryMap = new HashMap<String, String>();
		String keywordWildCard = "*" + keyword + "*";
		LOG.info("queryContent keywordWildCard = " + keywordWildCard);

		queryMap.put("_charset_", "UTF-8");
		queryMap.put("path", path);
		queryMap.put("type", "cq:Page");
//		queryMap.put("property", "jcr:content/@excludeSearch");
//		queryMap.put("property.operation", "unequals");
//		queryMap.put("property.value", "true");
		queryMap.put("fulltext", keywordWildCard);
		queryMap.put("p.limit", limit);
		queryMap.put("p.offset", "0");
		queryMap.put("orderby", "@jcr:score");
		queryMap.put("orderby.sort", "desc");
//		excludeSearch
		LOG.info("queryContent builder = " + builder);
		
		Query query = builder.createQuery(PredicateGroup.create(queryMap), session);
		SearchResult result = query.getResult();
		
		return result;
	}
	
	public SearchResult queryVideo(ResourceResolver resourceResolver, String path, String keyword) {

		Session session = resourceResolver.adaptTo(Session.class);
		Map<String, String> queryMap = new HashMap<String, String>();
		String keywordWildCard = "*" + keyword + "*";

		queryMap.put("path", path);
		queryMap.put("1_property", "sling:resourceType");
		queryMap.put("1_property.1_value", "apddemo/components/content/hero-banner-component");
		queryMap.put("1_property.2_value", "apddemo/components/content/video-component");
		queryMap.put("2_property", "assetType");
		queryMap.put("2_property.value", "assetVideo");
//		queryMap.put("fulltext", keywordWildCard);
		queryMap.put("orderby", "@jcr:score");
		queryMap.put("orderby.sort", "desc");
		queryMap.put("p.limit", "-1");

		LOG.info("queryVideo builder = " + builder);
		
		Query query = builder.createQuery(PredicateGroup.create(queryMap), session);
		SearchResult result = query.getResult();
		
		return result;
	}
	
	public SearchResult queryMultifieldVideo(ResourceResolver resourceResolver, String path, String keyword) {

		Session session = resourceResolver.adaptTo(Session.class);
		Map<String, String> queryMap = new HashMap<String, String>();
		String keywordWildCard = "*" + keyword + "*";

		queryMap.put("path", path);
		queryMap.put("1_property", "sling:resourceType");
		queryMap.put("1_property.1_value", "apddemo/components/content/main-banner-component");
		queryMap.put("1_property.2_value", "apddemo/components/content/mix-media-player-component");
		queryMap.put("2_property", "enableVideo");
		queryMap.put("2_property.value", "true");
		queryMap.put("orderby", "@jcr:score");
		queryMap.put("orderby.sort", "desc");
		queryMap.put("p.limit", "-1");

		LOG.info("queryVideo builder = " + builder);
		
		Query query = builder.createQuery(PredicateGroup.create(queryMap), session);
		SearchResult result = query.getResult();
		
		return result;
	}
	
	private JSONObject generateDocFinderListbyKeyword(ResourceResolver resourceResolver, String path, String keyword) throws JSONException{
		
		JSONObject apiResponse = new JSONObject();
		int totalMatchforContent = 0;
		try {
			
			SearchResult resultPages = queryDocFinder(resourceResolver, path, keyword, "-1");
			int totalMatchforPage = (int) resultPages.getTotalMatches();
			LOG.info("generateDocFinderListbyKeyword : " + totalMatchforPage);
			
			JSONArray jsonArrayProduct = new JSONArray();
			JSONArray jsonArrayFilter = new JSONArray();
			
			if(totalMatchforPage > 0){
			
				int hitNo = 0;
				for(Hit hit : resultPages.getHits()){
					
			
//					LOG.info("DOC Path ( " + hitNo + " ) : " + hit.getPath());
	//				jsonArrayFilter.put(hit.getPath());
					 Node hitNode =  hit.getNode();
					 NodeIterator ni =  hitNode.getNodes(); 
				 
					 while (ni.hasNext()) {
				        Node child = (Node)ni.nextNode(); 
				         
				        
				        NodeIterator ni2 =  child.getNodes() ; 
				        while (ni2.hasNext()) {
					        Node child2 = (Node)ni2.nextNode(); 
					        
					        if(child2.hasProperty("docPath")) {
					        	
					        	JSONObject searchContentJson = new JSONObject();
					        	String docPath = child2.getProperty("docPath").getString();
					        	
					        	Resource res = resourceResolver.getResource(docPath);
								Asset asset = res.adaptTo(Asset.class);
								
					        	if(!(asset.getMetadataValue("dc:title").isEmpty())) {
									searchContentJson.put("title", asset.getMetadataValue("dc:title"));
									
								}else {
									searchContentJson.put("title", asset.getName());
								}
					        	
					        	if(!(searchContentJson.get("title").toString().toLowerCase()).contains(keyword.toLowerCase())){
					        		continue;
					        	}
					        	
	//				        	jsonArrayFilter.put("getPath  = " + child2.getPath());
	//				        	jsonArrayFilter.put("docPath  = " + child2.getProperty("docPath").getString());
	
					        	
								Node assetNode = resourceResolver.getResource(docPath).adaptTo(Node.class);
								
								searchContentJson.put("key", "docFinder_" + hitNo);
								searchContentJson.put("link", docPath);
	
								if(child2.hasProperty("thumbnail")) {
									searchContentJson.put("image", child2.getProperty("thumbnail").getString());
								}else {
									searchContentJson.put("image", defaultPdfIcon);
								}
								
								if(!(asset.getMetadataValue("dc:title").isEmpty())) {
									searchContentJson.put("title", asset.getMetadataValue("dc:title"));
									
								}else {
									searchContentJson.put("title", asset.getName());
								}
						        
								searchContentJson.put("description", asset.getMetadataValue("dc:description"));
								
														
								JSONObject pageCategory = getPageCategory(resourceResolver, path, hit.getPath());
//								LOG.info("type" + pageCategory);
								searchContentJson.put("type", pageCategory.get("key").toString());
								
								boolean addType = false;
								
								for (int i = 0 ; i < jsonArrayFilter.length(); i++) {
									
									if("type".equalsIgnoreCase(jsonArrayFilter.getJSONObject(i).get("key").toString())) {
										
										if(jsonArrayFilter.getJSONObject(i).has("child")){
											
											JSONArray categoryList = (JSONArray) jsonArrayFilter.getJSONObject(i).get("child");
											categoryList.put(pageCategory);
											
											jsonArrayFilter.getJSONObject(i).remove("child");
											jsonArrayFilter.getJSONObject(i).put("child", categoryList);
											addType = true;
											break;
										}else {
											
											JSONArray categoryList = new JSONArray();
											categoryList.put(pageCategory);
											jsonArrayFilter.getJSONObject(i).put("child", categoryList);
											addType = true;
											break;
										}
										
									}
									
								}
								
								if(!addType) {
									
									JSONObject jsonFilterCategory = new JSONObject();
									jsonFilterCategory.put("key", "type");
									jsonFilterCategory.put("title", "Type");
									
									JSONArray categoryList = new JSONArray();
									categoryList.put(pageCategory);
									jsonFilterCategory.put("child", categoryList);
									jsonArrayFilter.put(jsonFilterCategory);
									
								}
								
								
								
								
								
								
								JSONObject pageSubCategory = getPageCategory(resourceResolver, path + "/" + pageCategory.get("key").toString(), hit.getPath());
								//searchContentJson.put("type", pageCategory.get("key").toString());
								String pageCategoryFilterKey = pageCategory.get("key").toString() + "_category";
								String pageCategoryFilterTitle = pageCategory.get("name").toString() + " Category";

								searchContentJson.put("category_header", pageSubCategory.get("name").toString());
								searchContentJson.put(pageCategoryFilterKey, pageSubCategory.get("key").toString());
								
								boolean addCategory = false;
								
								for (int i = 0 ; i < jsonArrayFilter.length(); i++) {
									
									if(pageCategoryFilterKey.equalsIgnoreCase(jsonArrayFilter.getJSONObject(i).get("key").toString())) {
										
										if(jsonArrayFilter.getJSONObject(i).has("child")){
											
											JSONArray categoryList = (JSONArray) jsonArrayFilter.getJSONObject(i).get("child");
											categoryList.put(pageSubCategory);
											
											jsonArrayFilter.getJSONObject(i).remove("child");
											jsonArrayFilter.getJSONObject(i).put("child", categoryList);
											addCategory = true;
											break;
										}else {
											
											JSONArray categoryList = new JSONArray();
											categoryList.put(pageSubCategory);
											jsonArrayFilter.getJSONObject(i).put("child", categoryList);
											addCategory = true;
											break;
										}
										
									}
									
								}
								
								if(!addCategory) {
									
									JSONObject jsonFilterCategory = new JSONObject();
									jsonFilterCategory.put("key", pageCategoryFilterKey);
									jsonFilterCategory.put("title", pageCategoryFilterTitle);
									
									JSONArray categoryList = new JSONArray();
									categoryList.put(pageSubCategory);
									jsonFilterCategory.put("child", categoryList);
									jsonArrayFilter.put(jsonFilterCategory);
									
								}
								
								
								TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
								JSONArray tagValues = new JSONArray();
								
								if(!(asset.getMetadataValue("cq:tags").isEmpty())) {
									
									Object[] tags = (Object[]) asset.getMetadata("cq:tags");
									
									for (Object obj : tags) {
									     Tag tag = tagManager.resolve(obj.toString());
									     
									     //check if tags is under asset category or not
									     if("category".equals(tag.getParent().getName())) {
									    	 tagValues.put(tag.getName());
									    	 
									    	 JSONObject jsonTag = new JSONObject();
									    	 jsonTag.put("key", tag.getName());
									    	 jsonTag.put("name", tag.getTitle());
									    	 
									    	 boolean addTag = false;
												
												for (int i = 0 ; i < jsonArrayFilter.length(); i++) {
													
													if("document_type".equalsIgnoreCase(jsonArrayFilter.getJSONObject(i).get("key").toString())) {
														
														if(jsonArrayFilter.getJSONObject(i).has("child")){
															
															JSONArray categoryList = (JSONArray) jsonArrayFilter.getJSONObject(i).get("child");
															categoryList.put(jsonTag);
															
															jsonArrayFilter.getJSONObject(i).remove("child");
															jsonArrayFilter.getJSONObject(i).put("child", categoryList);
															addTag = true;
															break;
														}else {
															
															JSONArray categoryList = new JSONArray();
															categoryList.put(jsonTag);
															jsonArrayFilter.getJSONObject(i).put("child", categoryList);
															addTag = true;
															break;
														}
														
													}
													
												}
												
												if(!addTag) {
													
													JSONObject jsonFilterCategory = new JSONObject();
													jsonFilterCategory.put("key", "document_type");
													jsonFilterCategory.put("title", "Document Type");
													
													JSONArray categoryList = new JSONArray();
													categoryList.put(jsonTag);
													jsonFilterCategory.put("child", categoryList);
													jsonArrayFilter.put(jsonFilterCategory);
													
												}
												
									     }   
									}
									
									searchContentJson.put("document_type", tagValues);
								}
								
								
								
								
								Calendar calendar = assetNode.getProperty("jcr:created").getDate();
								Date date = (Date) calendar.getTime();
	//							searchContent.setCreated_date(formatter.format(date));
								searchContentJson.put("created_date", formatter.format(date));
														
														
															
								
								
								
								
						        totalMatchforContent++;
						        jsonArrayProduct.put(searchContentJson);
								hitNo++;
					        }
					        
				        }
				    }
					
					hitNo++;
				}
			}
			JSONArray releventContent = new JSONArray();
			releventContent = getRelaventContent(resourceResolver, path, keyword);
			
			
			//remove duplicate Json
			for (int i = 0 ; i < jsonArrayFilter.length(); i++) {
				
				if(jsonArrayFilter.getJSONObject(i).has("child")){
					
					JSONArray categoryList = (JSONArray) jsonArrayFilter.getJSONObject(i).get("child");
					categoryList = removeDuplicateFilter(categoryList);
					
					jsonArrayFilter.getJSONObject(i).remove("child");
					jsonArrayFilter.getJSONObject(i).put("child", categoryList);
				}
			}
			
			if(totalMatchforContent > 0) {
				apiResponse.put("result", true);
			}else {
				apiResponse.put("result", false);
			}
			
			apiResponse.put("path", path);
			apiResponse.put("keyword", keyword);
			apiResponse.put("num_result", totalMatchforContent);
			apiResponse.put("filter_listing", jsonArrayFilter);
			apiResponse.put("doc_finder", jsonArrayProduct);
			apiResponse.put("content", releventContent);
			
		}catch (Exception e) {
//			LOG.info("result = " + e.getMessage());
			e.getStackTrace();
		}
		
		
		
		
		return apiResponse;
	}
	
	public SearchResult queryDocFinder(ResourceResolver resourceResolver, String path, String keyword, String limit) {

		LOG.info("IN queryDocFinder");
		SearchResult result = null;
		
		try {
			
			Session session = resourceResolver.adaptTo(Session.class);
			Map<String, String> queryMap = new HashMap<String, String>();
			String keywordWildCard = "*" + keyword + "*";

			
			LOG.info("session = " + session + "keywordWildCard = " + keywordWildCard);
			
			
			queryMap.put("path", path);
			queryMap.put("1_property", "sling:resourceType");
			queryMap.put("1_property.1_value", "apddemo/components/content/resources-component");
			queryMap.put("1_property.2_value", "apddemo/components/content/resources-component");
//			queryMap.put("fulltext", keywordWildCard);
			queryMap.put("orderby", "@jcr:score");
			queryMap.put("orderby.sort", "desc");
			queryMap.put("p.limit", "-1");

			
			Query query = builder.createQuery(PredicateGroup.create(queryMap), session);
			result = query.getResult();
			
		}catch(Exception ex) {
			LOG.info("Exception queryDocFinder = " + ex.getMessage());
		}
		
		
		return result;
	}
	
	public JSONArray removeDuplicateFilter(JSONArray rawJsonArray) {
		
		Set<String> keyCodes=new HashSet<String>();
		JSONArray tempArray=new JSONArray();
		
		try {
			
			for(int i = 0; i < rawJsonArray.length(); i++){
				   String  keyCode=rawJsonArray.getJSONObject(i).getString("key");
				   if(keyCodes.contains(keyCode)){
				     continue;
				   }
				   else{
					   keyCodes.add(keyCode);
					   tempArray.put(rawJsonArray.getJSONObject(i));
				   }

				}
			
		}catch (Exception e) {
			LOG.info("removeDuplicateFilter Exception " + e.getMessage());
		}
		


		return tempArray;
	}
	
	public JSONObject getPageCategory(ResourceResolver resourceResolver, String homePage, String pagePath) {
		JSONObject category = new JSONObject();

//		LOG.info("getPageCategory Start = " + homePage + "  |  " + pagePath);
		
		if(pagePath.contains("jcr:content")) {
			pagePath = pagePath.substring(0,pagePath.indexOf("jcr:content"));
		}
		Page contentPage = resourceResolver.resolve(pagePath).adaptTo(Page.class); 
		
		
		try {
			
			
//			LOG.info("getPageCategory pagePath = " + pagePath);
			
			if(pagePath.indexOf(homePage) > -1) {
//				LOG.info("getPageCategory indexOf > -1 = " + pagePath);
				
				if(homePage.equals(pagePath)) {

//					LOG.info("getPageCategory equals = " + pagePath);
					Page parentPage = contentPage.getParent(); 
					
					category.put("key", "homepage");
					category.put("name", "Home Page");
					
				}else {
					
					if(!(homePage.equals(contentPage.getParent().getPath()))) {

//						LOG.info("getPageCategory if = " + pagePath);
						category = getPageCategory(resourceResolver, homePage, contentPage.getParent().getPath());
						
					}else {

//						LOG.info("getPageCategory else = " + pagePath);
						
						category.put("key", contentPage.getName());
						category.put("name", contentPage.getTitle());
					}
				}
				
			}else {

//				LOG.info("getPageCategory indexOf not = " + pagePath);
				category.put("key", "others");
				category.put("name", "Others");
			}
			
				
			
			
		}catch (Exception e) {
			LOG.info("getPageCategory ERROR = " + e.getMessage());
		}
		
		
		
		
		return category;
	}
	
	public JSONArray getRelaventContent(ResourceResolver resourceResolver, String path, String keyword) {

		JSONArray jsonArrayProduct = new JSONArray();
		
		try {
			
			SearchResult resultPages = queryContent(resourceResolver, path, keyword, "3");

			LOG.info("result = " + resultPages);
			
			int totalMatchforPage = (int) resultPages.getTotalMatches();


			
			if(totalMatchforPage > 0){
				
				
				int hitNo = 0;
				for(Hit hit : resultPages.getHits()){
					ValueMap hitProperties = hit.getProperties();
					
					//Skip if excludeSearch at pageproperties is ticked
					if(hitProperties.containsKey("excludeSearch")) {
						continue;
					}
					
					JSONObject searchContentJson = new JSONObject();
//					SearchContentModel searchContent = new SearchContentModel();
					
					searchContentJson.put("key", "content_" + hitNo);
					searchContentJson.put("title", hitProperties.get("jcr:title", String.class));
					
					if(hitProperties.containsKey("pageDescription")) {
						searchContentJson.put("description", hitProperties.get("pageDescription", String.class));
					}else {
						searchContentJson.put("description", hitProperties.get("jcr:description", String.class));
					}
					
					if(hitProperties.containsKey("pageImage")) {
						searchContentJson.put("image", hitProperties.get("pageImage", String.class));
					}else {
						searchContentJson.put("image", defaultSearchImage);
					}
					
					searchContentJson.put("link", hit.getPath() + ".html");
					searchContentJson.put("type", "page");
					
					Calendar calendar = hitProperties.get("jcr:created", Calendar.class);
					Date date = (Date) calendar.getTime();
//					searchContent.setCreated_date(formatter.format(date));
					searchContentJson.put("created_date", formatter.format(date));
					
					
						
					JSONObject pageCategory = getPageCategory(resourceResolver, path, hit.getPath());
					LOG.info("getRelaventContent pageCategory = " + pageCategory);
					
					searchContentJson.put("category_header", pageCategory.get("name").toString());
					searchContentJson.put("category", pageCategory.get("key").toString());
					
					jsonArrayProduct.put(searchContentJson);
				}
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
			return jsonArrayProduct;
	}
	
	/**
     * Loads a word list text file into memory for fast access.
     *
     */
    public static void loadWords(ResourceResolver resourceResolver, String txtPath) {
        String line = null; 	// Temp variable for storing one line at a time
        ArrayList<String> temp = new ArrayList<String>();

        try {
        	Resource resource = resourceResolver.getResource(txtPath);
        	Node asset = resource.adaptTo(Node.class);
        	BufferedReader buffReader = new BufferedReader(new InputStreamReader(asset.getNode("jcr:content").getProperty("jcr:data").getStream()));
//            FileReader fileReader = new FileReader(txtPath);
//            BufferedReader buffReader = new BufferedReader(fileReader);

            while ((line = buffReader.readLine()) != null) {
                temp.add(line.trim());
            }

            buffReader.close();
            words = new String[temp.size()];
            temp.toArray(words);
            System.out.println("test");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Searches for userWord in word list.<br>
     * Prints "Correct." if word is spelled correctly.<br>
     * Prints a suggestion if word is close to a correct word.<br>
     * Prints "No suggestions." if word is clearly obscure.
     *
     */
    public static void autoCorrect(String keyword) {
        int result = Search.binarySearch(words, keyword);

        // First, check for an exact match
        if (result != -1) {
            System.out.println("Correct. Congratulations, you can spell!"
                + "\n");
        }
        // Else, check if the user's word is an anagram of a dictionary word
        else {
            for (String word : words) {
                char wordStart; // First char of word
                char userWordStart; // First char of userWord
                if (!word.isEmpty()) { // If word is NOT empty
                    wordStart = word.charAt(0);
                    userWordStart = keyword.charAt(0);

                    // if (userWordStart == wordStart) // Same starting char
                        if (keyword.length() == word.length()) // Same length
                            if (containsAllChars(keyword, word)) // Same chars
                                suggestions.push(word);
                }
            }

            if (suggestions.isEmpty()) {
                System.out.println("No suggestions.\n");
            }
            else {
                System.out.print("Suggestions: ");
                while (!suggestions.isEmpty()) {
                    System.out.print(suggestions.pop() + " ");
                }
                System.out.println("\n");
            }
        }
    }
    
    /**
     * Checks if strTwo contains exclusively all characters from strOne.
     *
     * @param strOne String to pull chars from.
     * @param strTwo String to check for containment.
     * @return boolean True if strTwo contains all chars of strOne.
     */
    public static boolean containsAllChars(String strOne, String strTwo) {
        Character[] one = strToCharArray(strOne);
        Character[] two = strToCharArray(strTwo);

        sort(one);
        sort(two);

        for (int i = 0; i < one.length; i++) {
            if (Search.binarySearch(two, one[i]) == -1)
                return false;
            two[i] = '0';
        }

        two = strToCharArray(strTwo);
        sort(two);

        for (int i = 0; i < two.length; i++) {
            if (Search.binarySearch(one, two[i]) == -1)
                return false;
            one[i] = '0';
        }

        return true;
    }

    /**
     * Converts a String into a Character array.
     *
     * @param str String to convert.
     * @return Character[] Returns a Character[].
     */
    public static Character[] strToCharArray(String str) {
        Character[] charArray = new Character[str.length()];
        for (int i = 0; i < str.length(); i++) {
            charArray[i] = new Character(str.charAt(i));
        }

        return charArray;
    }



    /**
     * Sorts any E[] using insertion sort.
     *
     * @param <E> Type of Object to use.
     * @param array The E[] to be sorted in place.
     */
    public static <E extends Comparable<E>> void sort(E[] array) {
        int n = array.length; // Get length of array

        // Insertion sort
        for (int i = 1; i < n; i++) {
            E temp = array[i]; // Save the element at index i
            int j = i - 1; // Let j be the element one index before i

            // Iterate through array
            while (j > -1 && (array[j].compareTo(temp) > 0)) {
                // Insert element at array[j] in proper place
                array[j + 1] = array[j];
                j--;
            }

            // Complete swap
            array[j + 1] = temp;
        }
    }
    
    String printSuggestions(String input) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> print = makeSuggestions(input);
        if (print.size() == 0) {
            return "and I have no idea what word you could mean.\n";
        }
        sb.append("perhaps you meant:\n");
        for (String s : print) {
            sb.append("\n  -" + s);
        }
        return sb.toString();
    }
    
    private ArrayList<String> makeSuggestions(String input) {
        ArrayList<String> toReturn = new ArrayList<>();
        toReturn.addAll(charAppended(input));
        toReturn.addAll(charMissing(input));
        toReturn.addAll(charsSwapped(input));
        return toReturn;
    }
    
    private ArrayList<String> charAppended(String input) { 
        ArrayList<String> toReturn = new ArrayList<>();
        for (char c : alphabet) {
            String atFront = c + input;
            String atBack = input + c;
            if (dict.contains(atFront)) {
                toReturn.add(atFront);
            }
            if (dict.contains(atBack)) {
                toReturn.add(atBack);
            }
        }
        return toReturn;
    }
    
    private ArrayList<String> charMissing(String input) {   
        ArrayList<String> toReturn = new ArrayList<>();

        int len = input.length() - 1;
        //try removing char from the front
        if (dict.contains(input.substring(1))) {
            toReturn.add(input.substring(1));
        }
        for (int i = 1; i < len; i++) {
            //try removing each char between (not including) the first and last
            String working = input.substring(0, i);
            working = working.concat(input.substring((i + 1), input.length()));
            if (dict.contains(working)) {
                toReturn.add(working);
            }
        }
        if (dict.contains(input.substring(0, len))) {
            toReturn.add(input.substring(0, len));
        }
        return toReturn;
    }

    private ArrayList<String> charsSwapped(String input) {   
        ArrayList<String> toReturn = new ArrayList<>();

        for (int i = 0; i < input.length() - 1; i++) {
            String working = input.substring(0, i);// System.out.println("    0:" + working);
            working = working + input.charAt(i + 1);  //System.out.println("    1:" + working);
            working = working + input.charAt(i); //System.out.println("    2:" + working);
            working = working.concat(input.substring((i + 2)));//System.out.println("    FIN:" + working); 
            if (dict.contains(working)) {
                toReturn.add(working);
            }
        }
        return toReturn;
    }
	
	
}