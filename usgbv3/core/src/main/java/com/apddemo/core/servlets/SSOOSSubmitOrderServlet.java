package com.apddemo.core.servlets;

import java.io.IOException;
import java.rmi.ServerException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apddemo.core.daos.OrderSampleDao;
import com.apddemo.core.daos.UserInfoDao;
import com.apddemo.core.entity.OrderInfo;
import com.apddemo.core.entity.ProductInfo;
import com.apddemo.core.entity.ShippingDetail;
import com.apddemo.core.entity.UserInfo;
import com.apddemo.core.models.EmailSettings;
import com.apddemo.core.services.EmailService;

@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=SSO Order Sample - Submit Order" ,
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.paths="+ "/bin/sso/orderSample/submit"
})
public class SSOOSSubmitOrderServlet
  extends BaseAllMethodsServlet
{
  private static final long serialVersionUID = 1452364151988577055L;
  protected final Logger log = LoggerFactory.getLogger(getClass());
  
  	public SSOOSSubmitOrderServlet() {}
	
	@Reference
	private UserInfoDao userInfoDao;
	
	@Reference
	private OrderSampleDao orderSampleDao;
	
	@Reference
	private EmailService emailService;
	
	private static String CONTENT_ROOT_PATH = "/content/demo/";
	private static String ORDER_SAMPLE_SETTING_EMAIL =	"/orderSampleEmailSetting";
	final static String masterEmailTemplatePath = "/content/demo/global/master_email_template.html";
  
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
    throws ServerException, IOException
  {
    log.info("SSOOSSubmitOrder Start");
    try {
      response.setCharacterEncoding("utf-8");
      ResourceResolver resolver = request.getResourceResolver();
      String payLoad = request.getReader().readLine();
      JSONObject payloadJson = new JSONObject(payLoad);
      if (!payloadJson.has("user_info"))
      {
        setJsonResponse(response, "{\"error\":\"Missing Parameter user_info \"}", 500);
      }
      else if (!payloadJson.has("order_sample"))
      {
        setJsonResponse(response, "{\"error\":\"Missing Parameter order_sample \"}", 500);
      }
      else if (!payloadJson.has("shipping_detail"))
      {
        setJsonResponse(response, "{\"error\":\"Missing Parameter shipping_detail \"}", 500);
      }
      else {
        JSONObject user_info = (JSONObject)payloadJson.get("user_info");
        JSONArray order_sample = (JSONArray)payloadJson.get("order_sample");
        JSONObject shipping_detail = (JSONObject)payloadJson.get("shipping_detail");
        
        JSONObject jsonObj = submitOrder(resolver,user_info, order_sample, shipping_detail);
        setJsonResponseOk(response, jsonObj.toString());
      }
      

    }
    catch (Exception e)
    {
      setJsonResponse(response, "{\"osSubmitOrder\":\"exception - " + e.getMessage() + "\"}", 500);
    }
  }
  

  private JSONObject submitOrder(ResourceResolver resolver, JSONObject userInfoParam, JSONArray orderSamples, JSONObject shippingDetail) throws JSONException
  {
	  
    JSONObject apiResponse = new JSONObject();
    UserInfo userInfo = new UserInfo();
    List<String> productIdList = new ArrayList<String>();
    List<ProductInfo> productList = new ArrayList<ProductInfo>();
    
    
    try
    {
    	
//    	STORE SSO USER START
    	String aemId = (String)userInfoParam.get("user_id");
		userInfo.setUser_sso_id(aemId);
		
		if(userInfoParam.has("first_name")){
			userInfo.setUser_first_name((String)userInfoParam.get("first_name"));
		}
		
		if(userInfoParam.has("last_name")){
			userInfo.setUser_last_name((String)userInfoParam.get("last_name"));
		}
		
		if(userInfoParam.has("email")){
			userInfo.setEmail((String)userInfoParam.get("email"));
		}
		
		if(userInfoParam.has("display_name")){
			userInfo.setDisplay_name((String)userInfoParam.get("display_name"));
		}
		
		String userId = userInfoDao.getUserId(userInfo);
		log.info("userId : " + userId);
//    	STORE SSO USER END

//    	STORE SHIPPING DETAIL START
		ShippingDetail sd = new ShippingDetail();
		sd.setShip_name((String) shippingDetail.get("name"));
		sd.setShip_phone_no((String) shippingDetail.get("phone_no"));
		sd.setShip_email((String) shippingDetail.get("email"));
		sd.setShip_address1((String) shippingDetail.get("address1"));
		sd.setShip_address2((String) shippingDetail.get("address2"));
		sd.setShip_address3((String) shippingDetail.get("address3"));
		sd.setShip_postcode((String) shippingDetail.get("postcode"));
		sd.setShip_city((String) shippingDetail.get("city"));
		sd.setShip_country((String) shippingDetail.get("country"));
		sd.setRemark((String) shippingDetail.get("remark"));
		
		String shippingId = orderSampleDao.insertDataSD(sd);
		log.info("shippingId : " + shippingId);
//    	STORE SHIPPING DETAIL END		
        
//    	STORE PRODUCT DETAIL START	
    	for (int i = 0 ; i < orderSamples.length(); i++) {
    		JSONObject os = orderSamples.getJSONObject(i);
			ProductInfo product = new ProductInfo();
			
    		if(os.has("product")){
    			JSONObject productJson = os.getJSONObject("product");
    			product.setProduct_name((String) productJson.get("product_name"));
    			product.setProduct_path((String) productJson.get("product_url"));
    			product.setProduct_url((String) productJson.get("product_path"));
    			
    		}
    		if(os.has("quantity")){
    			product.setProduct_quantity((int) os.get("quantity"));
    		}
    		
    		productList.add(product);
    		
    	}
    	
    	productIdList = orderSampleDao.insertDataProduct(productList);
		log.info("productIdList : " + productIdList.toString());
//    	STORE PRODUCT DETAIL END
		//
    	if(!userId.isEmpty() && !shippingId.isEmpty() && productIdList.size() > 0){
    	
    		log.info("OrderInfo : IN ");
    		OrderInfo orderinfo = new OrderInfo();
    		orderinfo.setUser_id(userId);
    		orderinfo.setShip_id(shippingId);
    		orderinfo.setModified_by(userId);
    		orderinfo.setOrder_country((String)userInfoParam.get("country"));
    		orderinfo.setStatus("Active");
    		log.info("OrderInfo : IN 1");
    		OrderInfo orderinfoSuccess = orderSampleDao.insertDataOrder(orderinfo, productIdList);
    		log.info("orderinfoSuccess :  " + orderinfoSuccess.toString());
//    		SENT EMAIL NOTIFICATION START
    		String countryCode = (String) userInfoParam.get("country");
    		String emailSettingPath = StringUtils.EMPTY;
    		// Get email setting from country level.
    		emailSettingPath = CONTENT_ROOT_PATH.concat(countryCode).concat(ORDER_SAMPLE_SETTING_EMAIL);  
    		EmailSettings emailSettings = emailService.getEmailSettingsFromPath(resolver, emailSettingPath);
    		
    		if(null == emailSettings || emailSettings.getSubject().isEmpty()){
    			// get email setting from global if the country does not have specific template.
    			emailSettingPath = CONTENT_ROOT_PATH.concat("global").concat(ORDER_SAMPLE_SETTING_EMAIL);
    			emailSettings = emailService.getEmailSettingsFromPath(resolver, emailSettingPath);
    		}		  		
    		
    		String masterTemplate = emailService.getMasterEmailTemplate(resolver, masterEmailTemplatePath);
    		orderinfoSuccess.setProductList(productList);
    		orderinfoSuccess.setShippingDetail(sd);
    		String emailBody = generateEmailBodyFromMap(orderinfoSuccess);
    		emailSettings.setTemplate(masterTemplate);
    		emailSettings.setTemplateParameters(new String[]{emailBody});
    		emailSettings.setToaddress(new String [] {userInfo.getEmail()});
    		boolean emailSuccess = emailService.sendEmail(emailSettings);
//    		SENT EMAIL NOTIFICATION END
    		apiResponse.put("sendEmailStatus", emailSuccess);
        	apiResponse.put("order_id", orderinfoSuccess.getOrder_id());
        	apiResponse.put("order_date", orderinfoSuccess.getCreated_date());
        	apiResponse.put("status", "success");
        	
    	}else{

        	apiResponse.put("status", "error");
    	}
    	
    	
    } catch (Exception e) {
    	apiResponse.put("status", "error");
    	apiResponse.put("errorMessage", e.getMessage());
    	log.info("ERROR submitOrder : "+ e.getMessage());
        e.printStackTrace();
    }
    
    return apiResponse;
  }
	
	private boolean sendEmail(String emailTo, String emailContent, String sendFrom){
		log.info("In sendEmail");
		EmailSettings emailSettings = new EmailSettings();
		
//		20161202 Izzat - hardCoded email START
		emailSettings.setFromAddress(sendFrom);
		emailSettings.setSubject("USG Boral Order Product Sample");
		emailSettings.setTemplate(emailContent);
		emailSettings.setToaddress(emailTo.split(";"));
		

//		20161202 Izzat - hardCoded email START
		log.info("emailSettings.....:"+emailSettings.getFromAddress());
		
		
		boolean success = emailService.sendEmail(emailSettings);
		return success;
	}
	
	private String generateEmailBodyFromMap(OrderInfo orderInfo){
		
		StringBuilder body = new StringBuilder();
		String datePattern = "EEEEE MMMMM dd, yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);

		body.append("<html>" +
		           "<body><p>" +
		           "<br/><br/>User <b>" + orderInfo.getShipingDetail().getShip_name() + "</b> has placed an order for product sample on <b>" + simpleDateFormat.format(orderInfo.getCreated_date()) +
		           "</b>. Order # <b>" + orderInfo.getOrder_id() +
		           "</b></p><table border='0'>");
		body.append("</table>" +
		           "</body>" +
		           "</html>");
		
		return body.toString();
	}
}