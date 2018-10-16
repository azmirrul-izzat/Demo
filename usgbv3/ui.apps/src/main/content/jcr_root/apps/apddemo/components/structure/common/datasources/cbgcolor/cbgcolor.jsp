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

    kv.put("color-primary-green", "#009b3a");
    kv.put("color-primary-yellow", "#fed100");
    kv.put("color-secondary-red", "#c20051");
    kv.put("color-secondary-blue", "#00a5dd");
    kv.put("color-secondary-brown", "#845023");
    kv.put("color-secondary-purple", "#292182");
    kv.put("color-secondary-orange", "#e85314");
    kv.put("color-secondary-gold", "#70772a");
    kv.put("color-secondary-grey", "#4c4d4f");
    kv.put("color-secondary-teal", "#006870");
    kv.put("color-white", "#FFFFFF");
    kv.put("color-light-grey", "#f6f6f6");
    kv.put("color-grey", "#CCCCCC");
    kv.put("color-dark-grey", "#5f6062");
    kv.put("color-deep-dark-grey", "#353535");
    kv.put("color-black", "#000000");
    kv.put("color-transparent", "#ffffff00");


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
