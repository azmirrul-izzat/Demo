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

    kv.put("None", "");
    kv.put("Home Owner", "homeowner-icon");
    kv.put("Installation Guide", "installation-guide-icon");
    kv.put("Fire Rating", "firerating-icon");
    kv.put("Architect Designer", "architect-designer-icon");
    kv.put("Builder Installers", "builder-installers-icon");
    kv.put("Ceiling", "ceiling-icon");
    kv.put("Cornice", "cornice-icon");
    kv.put("Finishes", "finishes-icon");
    kv.put("Industrial Plaster", "industrial-plaster-icon");
    kv.put("Industrial Plaster 2", "industrial-plaster-two-icon");
    kv.put("Insulation", "insulation-icon");
    kv.put("Interior Lining", "interior-lining-icon");
    kv.put("Plastering DIY", "plasteringdiy-icon");
    kv.put("Steel Framing", "steel-framing-icon");


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
