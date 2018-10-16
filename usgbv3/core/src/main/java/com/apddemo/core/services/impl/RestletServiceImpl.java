package com.apddemo.core.services.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.apache.http.client.utils.URIBuilder;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apddemo.core.services.RestletService;

@Component(immediate = true, service = RestletService.class, configurationPid = "com.apddemo.core.services.impl.RestletServiceImpl")
public class RestletServiceImpl implements RestletService {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public String retrieveRestAPI(String domain, Map<String, String> param) {
		
        String output = "";
		
		try{
			URIBuilder uriBuilder = new URIBuilder(domain);
			
			if(param != null){
				for(String key: param.keySet()){
					uriBuilder.addParameter(key, param.get(key));
		        }
			}
			
			output = sendGet(uriBuilder);
			
		}catch( Exception ex){
			log.info("ERROR retrieveRestAPI :: " + ex.getMessage());
		}
		
        return output;
		
	}
	
	// HTTP GET request
		private String sendGet(URIBuilder urlBuilder) throws Exception {


			URL obj = urlBuilder.build().toURL();
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			//con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
//			System.out.println("\nSending 'GET' request to URL : " + urlBuilder.toString());
//			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
//			System.out.println(response.toString());
			return response.toString();

		}


}
