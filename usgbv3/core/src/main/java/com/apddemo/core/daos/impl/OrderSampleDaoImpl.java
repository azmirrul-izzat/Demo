package com.apddemo.core.daos.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apddemo.core.daos.OrderSampleDao;
import com.apddemo.core.entity.DocumentCollator;
import com.apddemo.core.entity.OrderInfo;
import com.apddemo.core.entity.ProductInfo;
import com.apddemo.core.entity.ShippingDetail;
import com.apddemo.core.entity.UserInfo;
import com.apddemo.core.models.OrderSampleForm;
import com.apddemo.core.daos.BaseDao;
import com.apddemo.core.daos.DocumentCollatorDao;

@Component(immediate = true, service = OrderSampleDao.class
, configurationPid = "com.apddemo.core.daos.impl.OrderSampleDaoImpl")
public class OrderSampleDaoImpl implements OrderSampleDao {
	
	@Reference
	BaseDao baseDao;
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	private SimpleDateFormat submitTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String baseQuery = "SELECT oi.order_id AS 'oi.order_id', " +
			"oi.user_id AS 'oi.user_id', " +
			"oi.order_country AS 'oi.order_country', " +
			"oi.ship_id AS 'oi.ship_id', " +
			"oi.order_status AS 'oi.order_status', " +
			"oi.created_date AS 'oi.created_date', " +
			"oi.modified_date AS 'oi.modified_date', " +
			"oi.modified_by AS 'oi.modified_by', " +
			"sso.user_id AS 'sso.user_id', " + 
			"sso.user_sso_id AS 'sso.user_sso_id', " +
			"sso.display_name AS 'sso.display_name', " +
			"sso.user_first_name AS 'sso.user_first_name', " +
			"sso.user_last_name AS 'sso.user_last_name', " +
			"sso.created_date AS 'sso.created_date', " +
			"sso.last_login_date AS 'sso.last_login_date', " +
			"sd.ship_id AS 'sd.ship_id', " +
			"sd.ship_name AS 'sd.ship_name', " +
			"sd.ship_phone_no AS 'sd.ship_phone_no', " +
			"sd.ship_email AS 'sd.ship_email', " +
			"sd.ship_address1 AS 'sd.ship_address1', " +
			"sd.ship_address2 AS 'sd.ship_address2', " +
			"sd.ship_address3 AS 'sd.ship_address3', " +
			"sd.ship_postcode AS 'sd.ship_postcode', " +
			"sd.ship_city AS 'sd.ship_city', " +
			"sd.ship_country AS 'sd.ship_country', " +
			"sd.created_date AS 'sd.created_date', " +
			"sd.remark AS 'sd.remark' " +
		"FROM os_orderinfo oi, SSO_User sso, OS_ShippingDetail sd " +
		"WHERE oi.user_id = sso.user_id " +
		"AND oi.ship_id = sd.ship_id ";
	@Override
	public boolean insertData(OrderSampleForm data) {
		String query = "INSERT INTO OrderSamplesForm (name,email,address,suburb,state,postcode,phone,segment,product,comments,informNews,timeAdded) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
		boolean success = baseDao.insertData(query,
				data.getName(),
				data.getEmail(),
				data.getAddress(),
				data.getSuburb(),
				data.getState(),
				data.getPostcode(),
				data.getPhone(),
				data.getSegment(),
				data.getProduct(),
				data.getComments(),
				data.getInformNews(),
				new Date());
		
		return success;
	}

	@Override
	public String insertDataSD(ShippingDetail data) {
		String shippingId = "";
		String query = "INSERT INTO OS_ShippingDetail (ship_name, ship_phone_no, ship_email, ship_address1, ship_address2, ship_address3, ship_postcode, ship_city, ship_country, created_date, remark) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		int insertId = baseDao.insertDataReturnId(query,
				data.getShip_name(),
				data.getShip_phone_no(),
				data.getShip_email(),
				data.getShip_address1(),
				data.getShip_address2(),
				data.getShip_address3(),
				data.getShip_postcode(),
				data.getShip_city(),
				data.getShip_country(),
				submitTime.format(new Date()),
				data.getRemark());
		
		shippingId = "OSSD" + String.format("%08d", insertId);
		
		return shippingId;
	}

	@Override
	public List<String> insertDataProduct(List<ProductInfo> data) {
		List<String> shippingIdList = new ArrayList<String>();
		
		String query = "INSERT INTO OS_ProductInfo (product_name, product_path, product_url, product_quantity) "
				+ "VALUES (?,?,?,?)";
		
		for(ProductInfo product : data){
			
			int insertId = baseDao.insertDataReturnId(query,
					product.getProduct_name(),
					product.getProduct_path(),
					product.getProduct_url(),
					product.getProduct_quantity());
			
			if(insertId > 0){
				String shippingId = "PROD" + String.format("%08d", insertId);
				shippingIdList.add(shippingId);
				
			}
			
		}
		
		
		return shippingIdList;
	}

	@Override
	public OrderInfo insertDataOrder(OrderInfo orderInfo, List<String> productIdList) {
//		List<String> shippingIdList = new ArrayList<String>();
		log.info("START insertDataOrder IN " + orderInfo.toString());
		OrderInfo submittedOrder = new OrderInfo();
		
		String orderQuery = "INSERT INTO OS_OrderInfo (user_id, order_country, order_status, ship_id, modified_by, created_date, modified_date) "
				+ "VALUES (?,?,?,?,?,?,?)";
		
		String orderRetrieveQuery = "SELECT * FROM OS_OrderInfo WHERE ID = ? ";

		String orderRelationQuery = "INSERT INTO OS_OrderProductMapping (order_id, product_id) VALUES (?,?) ";
		
		log.info("START insertDataOrder ");
		int insertId = baseDao.insertDataReturnId(orderQuery,
				orderInfo.getUser_id(),
				orderInfo.getOrder_country(),
				orderInfo.getStatus(),
				orderInfo.getShip_id(),
				orderInfo.getModified_by(),
				submitTime.format(new Date()),
				submitTime.format(new Date()));
		

		log.info("insertDataOrder insertId : " + insertId);
		if(insertId > 0){
			List<Map<String, Object>> retrieveOrder = baseDao.retrieveData(orderRetrieveQuery, insertId);
			
			for(Map<String, Object> queryMap : retrieveOrder){
				submittedOrder.setOrder_id((String) queryMap.get("order_id"));
				submittedOrder.setUser_id((String) queryMap.get("user_id"));
				submittedOrder.setOrder_country((String) queryMap.get("order_country"));
				submittedOrder.setShip_id((String) queryMap.get("ship_id"));
				submittedOrder.setStatus((String) queryMap.get("order_status"));
				submittedOrder.setCreated_date((Date) queryMap.get("created_date"));
				submittedOrder.setModified_date((Date) queryMap.get("modified_date"));
				submittedOrder.setModified_by((String) queryMap.get("modified_by"));
			}
			
			for(String product : productIdList){
				baseDao.insertData(orderRelationQuery, 
						submittedOrder.getOrder_id(),
						product);
			}
			
		}
		
		
		return submittedOrder;
	}

	@Override
	public List<OrderInfo> getOrderListbySsoId(String sso_id) {
		
//		log.info("getDocumentListbySsoId : " + sso_id);
		String query = baseQuery +
			"AND sso.user_sso_id = ? " +
			"order by oi.created_date desc";
		List<OrderInfo> oiList = new ArrayList<OrderInfo>();
		Date today = new Date();
//		ResultSetMapper resultSetMapper = new ResultSetMapper();
//		ResultSet resultSet = null;
//		QueryDataResult queryDataResult = null;
		
		try{
			List<Map<String, Object>> newQuery = baseDao.retrieveData(query, sso_id);
			
			for(Map<String, Object> queryMap : newQuery){

				OrderInfo orderInfo = dbMapToOrderInfo(queryMap);
				List<ProductInfo> productList = getProductListByOrderId(orderInfo.getOrder_id());
				if(productList != null && productList.size() > 0){
					orderInfo.setProductList(productList);
				}
				oiList.add(orderInfo);
				
			}
		}catch(Exception ex){
			log.error("getOrderListbySsoId : " + ex.getMessage());
		}
		
		
		return oiList;
	}

	@Override
	public boolean UpdateData(String userId, String removeId, String status) {
		
		log.info("UpdateData getUser : " + userId);
		String query = "UPDATE OS_OrderInfo SET order_status = ?, modified_date = ?, modified_by = ? WHERE order_id = ?";
		boolean success = false;
			
		success = baseDao.removeUpdateData(query, 
				status,
				submitTime.format(new Date()),
				userId,
				removeId);
		
		return success;
	}
	
	@Override
	public List<OrderInfo> getOrderList(Map<String, Object> inputMap){
		log.info("getOrderList : ");
		String query = baseQuery +
			"AND (oi.created_date >= ? AND oi.created_date <= ? ) " +
			"order by oi.created_date desc";
		List<OrderInfo> oiList = new ArrayList<OrderInfo>();
				
		try{
			
			Date startDate = (Date) inputMap.get("startDate");
			Date endDate = (Date) inputMap.get("endDate");
			List<Map<String, Object>> newQuery = baseDao.retrieveData(query, submitTime.format(startDate), submitTime.format(endDate));
			
			for(Map<String, Object> queryMap : newQuery){

				OrderInfo orderInfo = dbMapToOrderInfo(queryMap);
				List<ProductInfo> productList = getProductListByOrderId(orderInfo.getOrder_id());
				if(productList != null && productList.size() > 0){
					orderInfo.setProductList(productList);
				}
				oiList.add(orderInfo);
				
			}
			
		}catch(Exception ex){
			log.error("getOrderListbySsoId : " + ex.getMessage());
		}
		return oiList;
	}

	@Override
	public List<OrderInfo> getOrderListbyCountry(List<String> countryList, Map<String, Object> inputMap) {
		
		log.info("getOrderListbyCountry : " + countryList.toString());
		List<OrderInfo> oiList = new ArrayList<OrderInfo>();
		List<Map<String, Object>> newQuery = null;
		String query = "";
		
		try{
			Date startDate = (Date) inputMap.get("startDate");
			Date endDate = (Date) inputMap.get("endDate");
			
			log.info("countryList.size() : " + countryList.size());
			if(countryList.size() == 1){
				query = baseQuery +
						"AND oi.order_country IN (?) " +
						"AND (oi.created_date >= ? AND oi.created_date <= ? ) " +
						"order by oi.created_date desc";
				newQuery = baseDao.retrieveData(query, countryList.get(0), submitTime.format(startDate), submitTime.format(endDate));
				
			}else if(countryList.size() == 2){
				query = baseQuery +
						"AND oi.order_country IN (?,?) " +
						"AND (oi.created_date >= ? AND oi.created_date <= ? ) " +
						"order by oi.created_date desc";
				newQuery = baseDao.retrieveData(query, countryList.get(0), countryList.get(1), submitTime.format(startDate), submitTime.format(endDate));
				
			}else if(countryList.size() == 3){
				query = baseQuery +
						"AND oi.order_country IN (?,?,?) " +
						"AND (oi.created_date >= ? AND oi.created_date <= ? ) " +
						"order by oi.created_date desc";
				newQuery = baseDao.retrieveData(query, countryList.get(0), countryList.get(1), countryList.get(2), submitTime.format(startDate), submitTime.format(endDate));
				
			}else if(countryList.size() == 4){
				query = baseQuery +
						"AND oi.order_country IN (?,?,?,?) " +
						"AND (oi.created_date >= ? AND oi.created_date <= ? ) " +
						"order by oi.created_date desc";
				newQuery = baseDao.retrieveData(query, countryList.get(0), countryList.get(1), countryList.get(2), countryList.get(3), submitTime.format(startDate), submitTime.format(endDate));
				
			}else if(countryList.size() == 5){
				query = baseQuery +
						"AND oi.order_country IN (?,?,?,?,?) " +
						"AND (oi.created_date >= ? AND oi.created_date <= ? ) " +
						"order by oi.created_date desc";
				newQuery = baseDao.retrieveData(query, countryList.get(0), countryList.get(1), countryList.get(2), countryList.get(3), countryList.get(4), submitTime.format(startDate), submitTime.format(endDate));
			}

			log.info("baseQuery : " + baseQuery);
			log.info("countryList : " + countryList.toString());
			
			for(Map<String, Object> queryMap : newQuery){

				OrderInfo orderInfo = dbMapToOrderInfo(queryMap);
				List<ProductInfo> productList = getProductListByOrderId(orderInfo.getOrder_id());
				if(productList != null && productList.size() > 0){
					orderInfo.setProductList(productList);
				}
				oiList.add(orderInfo);
				
			}
			
		}catch(Exception ex){
			log.error("getOrderListbySsoId : " + ex.getMessage());
		}
		return oiList;
	}
	
	public String convertListStringToINClause(List<String> stringList){
		
		Iterator<String> itr = stringList.iterator();
		String result = "";
	    while(itr.hasNext()) {

	    	result += itr.next();

	        if(itr.hasNext()) {
	        	result += "','";
	        }
	    }
	    
	    return result;
	}
	
	public OrderInfo dbMapToOrderInfo(Map<String, Object> queryMap){
		OrderInfo orderInfo = new OrderInfo();
		ShippingDetail shippingDetail = new ShippingDetail();

		orderInfo.setOrder_id((String) queryMap.get("oi.order_id"));
		orderInfo.setUser_id((String) queryMap.get("oi.user_id"));
		orderInfo.setOrder_country((String) queryMap.get("oi.order_country"));
		orderInfo.setShip_id((String) queryMap.get("oi.ship_id"));
		orderInfo.setStatus((String) queryMap.get("oi.order_status"));
		orderInfo.setCreated_date((Date) queryMap.get("oi.created_date"));
		orderInfo.setModified_date((Date) queryMap.get("oi.modified_date"));
		orderInfo.setModified_by((String) queryMap.get("oi.modified_by"));
		orderInfo.setAgeOfLastModified(daysBetween(orderInfo.getModified_date(),new Date()));
		orderInfo.setAgeOfSamplesOrder(daysBetween(orderInfo.getCreated_date(),new Date()));
		
		shippingDetail.setShip_id((String) queryMap.get("sd.ship_id"));
		shippingDetail.setShip_name((String) queryMap.get("sd.ship_name"));
		shippingDetail.setShip_phone_no((String) queryMap.get("sd.ship_phone_no"));
		shippingDetail.setShip_email((String) queryMap.get("sd.ship_email"));
		shippingDetail.setShip_address1((String) queryMap.get("sd.ship_address1"));
		shippingDetail.setShip_address2((String) queryMap.get("sd.ship_address2"));
		shippingDetail.setShip_address3((String) queryMap.get("sd.ship_address3"));
		shippingDetail.setShip_postcode((String) queryMap.get("sd.ship_postcode"));
		shippingDetail.setShip_city((String) queryMap.get("sd.ship_city"));
		shippingDetail.setShip_country((String) queryMap.get("sd.ship_country"));
		shippingDetail.setCreated_date((Date) queryMap.get("sd.modified_date"));
		shippingDetail.setRemark((String) queryMap.get("sd.remark"));
		orderInfo.setShippingDetail(shippingDetail);
		
		UserInfo userinfo = new UserInfo();
		userinfo.setUser_id((String) queryMap.get("sso.user_id"));
		userinfo.setDisplay_name((String) queryMap.get("sso.display_name"));
		userinfo.setUser_first_name((String) queryMap.get("sso.user_first_name"));
		userinfo.setUser_last_name((String) queryMap.get("sso.user_last_name"));
		userinfo.setCreated_date((Date) queryMap.get("sso.created_date"));
		userinfo.setLast_login_date((Date) queryMap.get("sso.last_login_date"));
		orderInfo.setUserInfo(userinfo);
		
		return orderInfo;
	}
	
	
	public List<ProductInfo> getProductListByOrderId(String orderId){
	
		List<ProductInfo> productList = new ArrayList<ProductInfo>();
		
		String query = "SELECT pi.* " +
				"FROM OS_ProductInfo pi, OS_OrderProductMapping pm  " +
				"WHERE pi.product_id = pm.product_id " +
				"AND pm.order_id = ? " +
				"ORDER BY pi.product_id asc";
		
		try{
			List<Map<String, Object>> newQuery = baseDao.retrieveData(query, orderId);
			
			for(Map<String, Object> queryMap : newQuery){

				ProductInfo product = new ProductInfo();
				product.setProduct_id((String) queryMap.get("product_id"));
				product.setProduct_name((String) queryMap.get("product_name"));
				product.setProduct_path((String) queryMap.get("product_path"));
				product.setProduct_url((String) queryMap.get("product_url"));
				product.setProduct_quantity( (int) queryMap.get("product_quantity"));
				productList.add(product);
			}
			
		}catch(Exception ex){
			log.error("getOrderListbySsoId : " + ex.getMessage());
		}
		
		return productList;
	}
	
	public int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}
	

}
