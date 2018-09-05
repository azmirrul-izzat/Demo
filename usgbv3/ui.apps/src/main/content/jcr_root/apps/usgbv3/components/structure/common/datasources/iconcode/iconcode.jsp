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
    kv.put("Statistic", "icon-stc");
    kv.put("Home Owner", "icon-homeowner");
    kv.put("Installation Guide", "icon-installation-guide");
    kv.put("Fire Rating", "icon-firerating");
    kv.put("Architect Designer", "icon-architect-designer");
    kv.put("Builder Installers", "icon-builder-installers");
    kv.put("Ceiling", "icon-ceiling");
    kv.put("Cornice", "icon-cornice");
    kv.put("Finishes", "icon-finishes");
    kv.put("Industrial Plaster", "icon-industrial-plaster");
    kv.put("Industrial Plaster 2", "icon-industrial-plaster-two");
    kv.put("Insulation", "icon-insulation");
    kv.put("Interior Lining", "icon-interior-lining");
    kv.put("Plastering DIY", "icon-plasteringdiy");
    kv.put("Steel Framing", "icon-steel-framing");

    kv.put("System Plasterboard", "icon-systemplasterboard");
    kv.put("Bimwizard", "icon-bimwizard");
    kv.put("E-Selector", "icon-eselector");
    kv.put("Speciality", "icon-speciality");


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
