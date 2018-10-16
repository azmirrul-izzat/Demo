package com.apddemo.core.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.apddemo.core.daos.ShareDataDao;
import com.apddemo.core.models.ShareData;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.commons.json.JSONException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;


@Component(service=Servlet.class,
		property={
				Constants.SERVICE_DESCRIPTION + "=Get store search Store Locator",
				"sling.servlet.methods=" + HttpConstants.METHOD_GET,
				"sling.servlet.paths="+ "/bin/usgb/v3/shareService"
		})
public class ShareServlet extends BaseAllMethodsServlet {

	public static final String REQUEST_TYPE_WRITE = "write";
	public static final String REQUEST_TYPE_GET = "get";
	public static final String DELIMITER = ";";

	@Reference
	private ShareDataDao dao;
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = 2208455315170853896L;

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		// check the request type
		try {
			if (ShareServlet.REQUEST_TYPE_GET.equalsIgnoreCase(request
					.getParameter("rType"))) {
				String key = request.getParameter("key");
				ShareData shareData = this.getData(key);
				JsonArray jsonShareData = new JsonArray();
				Iterator <String> pathIterator = shareData.getPaths().iterator();
				while (pathIterator.hasNext()){
					String path = pathIterator.next();
					jsonShareData.add(new JsonPrimitive(path));
				}
				JsonObject JSONproduct = new JsonObject();
				JSONproduct.add("path", jsonShareData);
				this.setJsonResponseOk(response, JSONproduct.toString());
			} else if (ShareServlet.REQUEST_TYPE_WRITE
					.equalsIgnoreCase(request.getParameter("rType"))) {
				JsonParser parser = new JsonParser();
				JsonObject paths = parser.parse(request.getParameter("paths")).getAsJsonObject();
				String key = this.createDataForShare(this.parseJSONPaths(paths));
				JsonObject JSONkey = new JsonObject();
				JSONkey.addProperty("key", key);
				this.setJsonResponseOk(response, JSONkey.toString());

			} else {
				this.log.debug("Invalid request for share data Service");
				this.setJsonResponse(response, "500", 500);
			}
		} catch (JSONException e) {
			this.log.debug("JSON excpetion occured in share data Service",e);
			this.setJsonResponse(response, "500", 500);
		}
	}
	private String createDataForShare(List<String> products) {
		ShareData data = null;
		if (products == null || products.size() == 0){
			return null;
		}
		data = this.prepareData(products);
		//boolean result = true;
		boolean result = dao.insertShareData(data);
		if (result != true){
			log.debug("share data Service is unable to create a data record");
			return null;
		}
		return data.getUuid();
		//return "123123";
	}
	private ShareData prepareData (List <String> paths){
		ShareData result = null;
		
		String uuid = this.generateUUID();
		result = new ShareData();
		result.setUuid(uuid);
		result.setPaths(paths);
		String concatenatedPaths = this.concatenatePaths(result.getPaths());
		result.setConcatenatedPaths(concatenatedPaths);
		return result;
	}
	private String generateUUID(){
		String result= null;
		
		long time = System.currentTimeMillis();
		Long longTime = new Long(time);
		
		result = UUID.nameUUIDFromBytes(longTime.toString().getBytes()).toString();
		return result;
	}
	private String concatenatePaths (List <String> pathList){
		String result = null;
		Iterator <String> pathListIterator = pathList.iterator();
		while (pathListIterator.hasNext()){
			String path = pathListIterator.next();
			if (result == null){
				result = path;
			}else{
				result = result + ShareServlet.DELIMITER + path;
			}
		}
		return result;
	}
	
	private ShareData getData(String key) {
		ShareData result = null;
		result = dao.getShareData(key);
		
		if ( result == null){
			log.debug("Unable to access share data for the give key = "+key);
			return null;
		}

		// prepare the parsed paths
		result.setPaths(this.parseConcatenatedPaths(result.getConcatenatedPaths()));
		return result;
	}
	private List <String> parseConcatenatedPaths(String concatenatedPaths){
		List <String> result = null;
		if (concatenatedPaths == null){
			return null;
		}
		String[] paths = concatenatedPaths.split(ShareServlet.DELIMITER);
		result = Arrays.asList(paths);
		return result;
	}
	
	private List <String> parseJSONPaths(JsonObject paths) throws JSONException {
		List <String > result = new ArrayList<String> ();
		JsonArray jsonPaths = paths.getAsJsonArray("paths");
		for (int i=0; i<jsonPaths.size(); i++){
			String path = jsonPaths.get(i).getAsString();
			result.add(path);
		}
		return result;
	}

}
