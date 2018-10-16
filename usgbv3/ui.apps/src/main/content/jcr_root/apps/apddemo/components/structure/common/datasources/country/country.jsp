<%@page session="false"
                import="java.util.ArrayList,
                  java.util.HashMap,
                  java.util.LinkedHashMap,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.adobe.granite.ui.components.ds.EmptyDataSource,
                  com.adobe.granite.ui.components.ds.SimpleDataSource,
                  com.adobe.granite.ui.components.ds.ValueMapResource,
				  org.apache.sling.api.resource.ResourceMetadata,
				  com.adobe.cq.commerce.api.ProductRelationshipsProvider,
				  java.util.Map" %><%
%><%@include file="/libs/foundation/global.jsp"%><%

    ArrayList<Resource> resourceList = new ArrayList<Resource>();
    
    Map<String, String> kv = new LinkedHashMap<String, String>();

	kv.put("Singapore", "Singapore");
	kv.put("Malaysia", "Malaysia");
	kv.put("Australia", "Australia");
	kv.put("China", "China");
	kv.put("India", "India");
	kv.put("Indonesia", "Indonesia");
	kv.put("South Korea", "South Korea");
	kv.put("New Zealand", "New Zealand");
	kv.put("Philippines", "Philippines");
	kv.put("Thailand", "Thailand");
	kv.put("Vietnam", "Vietnam");
	kv.put("Middle East", "Middle East");
	kv.put("Kingdom of Saudi Arabia", "Kingdom of Saudi Arabia");
	kv.put("Oman", "Oman");
/*
    kv.put("Form Modal", "modal");
	kv.put("Form Modal (Full Width)", "modal-full");
    kv.put("Video Modal", "video-modal");
    */
    for (String key : kv.keySet()) {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("text", key);
		map.put("value", kv.get(key));
		
		ValueMapResource syntheticResource = new ValueMapResource(resourceResolver, new ResourceMetadata(), "", new ValueMapDecorator(map));
		resourceList.add(syntheticResource);
    }

    DataSource ds;

    // if no matching nodes where found
    if (resourceList.size() == 0){
        // return empty datasource
        ds = EmptyDataSource.instance();
    } else {
        // create a new datasource object
        ds = new SimpleDataSource(resourceList.iterator());
    }

    // place it in request for consumption by datasource mechanism
    request.setAttribute(DataSource.class.getName(), ds);
%>
