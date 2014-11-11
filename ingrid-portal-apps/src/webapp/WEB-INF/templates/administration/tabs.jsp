<%--
  **************************************************-
  Ingrid Portal Apps
  ==================================================
  Copyright (C) 2014 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<%--
Copyright 2004 The Apache Software Foundation
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
  	<c:forEach var="tab" items="${tab_items}">
  		<td <c:if test="${tab == currentTab}"> class="LTabLeft" </c:if>
  		    <c:if test="${tab != currentTab}"> class="LTabLeftLow" </c:if>
  		     style="font-size:1pt;" nowrap="true">
  		     &nbsp;
  		</td>
  	    <td <c:if test="${tab == currentTab}"> class="LTab" </c:if>
  	        <c:if test="${tab != currentTab}"> class="LTabLow" </c:if>
  	         align="center" style="vertical-align:middle" >
		                     
  	         <% 
  				String tabid = ((TabBean)pageContext.getAttribute("tab")).getId(); 
  				String paramName = (String)pageContext.getAttribute("url_param_name");
  	         %>
  	         
  	         <c:choose>
  	          <c:when test="${tab != currentTab}">
  	           <portlet:actionURL var="select_portlet_tab_link" >
  	               <portlet:param name="<%= paramName %>" value="<%= tabid %>" />
  	           </portlet:actionURL>
              		                     
  	           <a href="<c:out value="${select_portlet_tab_link}"/>">
  				 <fmt:message>pam.details.tabs.<c:out value="${tab.id}"/></fmt:message>
  	           </a>
  	          </c:when>
  	          <c:otherwise>
  	            <fmt:message>pam.details.tabs.<c:out value="${tab.id}"/></fmt:message>
  	          </c:otherwise>  
  	        </c:choose>
  	    </td>
  	    <td <c:if test="${tab == currentTab}"> class="LTabRight" </c:if>
  	        <c:if test="${tab != currentTab}"> class="LTabRightLow" </c:if> 
  	        style="font-size:1pt;" nowrap="true">
  	        &nbsp;
  	    </td>
  	</c:forEach>
  </tr>
</table>