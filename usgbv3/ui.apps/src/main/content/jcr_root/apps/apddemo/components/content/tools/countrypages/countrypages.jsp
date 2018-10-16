<%--

  countrypages component.

  

--%>
<%
%><%@include file="/apps/usgb/global/global.jsp"%><%
%>
<%-- <%@include file="/src/main/content/jcr_root/apps/usgb/global/global.jsp"%> --%>
<%
%><%@page import="com.usgb.core.utils.CountryUtils"%>
<%@page session="false" %><%
%>

<cq:includeClientLib categories="countrypages" />

<center><h2> List of Pages by Country</h2></center>

<p>

<select class="countryDropdown">
<%
    List<Map<String, String>> demoCountryList = CountryUtils.retrieveDemoCountry(resourceResolver);

    if(demoCountryList != null){
        for(Map<String, String> usgbCountry : demoCountryList){

            String countryName = usgbCountry.get("countryName");
            String pathCode = usgbCountry.get("pathCode");
            String countryCode = usgbCountry.get("countryCode");
        %>
    	<option value="/content/demo/<%=pathCode%>" ><%=countryName%> (<%=countryCode%>)</option>
        <%
        }
    }

%>
</select>

<div id="countryTable" class="div-table"></div>
