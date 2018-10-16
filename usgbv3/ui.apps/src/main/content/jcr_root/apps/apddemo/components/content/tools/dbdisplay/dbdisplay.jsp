.<%--

  countrypages component.

  

--%>
<%
%><%@include file="/apps/usgb/global/global.jsp"%><%
%>
<%-- <%@include file="/src/main/content/jcr_root/apps/usgb/global/global.jsp"%> --%>
<%
%><%@page import="com.usgb.core.utils.CountryUtils"%>
<%@page import="com.usgb.core.services.USGBDBListService"%>
<%@page session="false" %><%
%>

<cq:includeClientLib categories="dbdisplay" />

<center><h2> List of Database</h2></center>



<select class="countryDropdown">
<%
    USGBDBListService conf = sling.getService(USGBDBListService.class);
	List<String> strings = Arrays.asList(conf.getDBTable());

    if(strings != null){
        for(String string : strings){

        %>
    	<option value="<%=string%>" ><%=string%></option>
        <%
        }
    }


%>
</select>

<div id="countryTable" class="div-table"></div>
