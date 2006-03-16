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
<%@ page language="java" session="true" %>
<%@ page import="org.apache.jetspeed.portlets.security.SecurityResources" %>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.apache.jetspeed.portlets.security.resources.SecurityResources" />

<c:set var="errorMessagesKey"><%=SecurityResources.ERROR_MESSAGES%></c:set>
<c:set var="errorMessages" value="${requestScope[errorMessagesKey]}"/>

<portlet:defineObjects/>
<script language="JavaScript">function t(i,f){if(i.checked) f.value='true';else f.value='false';}</script>
<h3 class="portlet-section-header">User Detail Preferences</h3>
<c:if test="${errorMessages != null}">
  <jsp:include page="/WEB-INF/templates/administration/errors-include.jsp"/>
</c:if>
<form action="<portlet:actionURL/>" method="post">
<c:set var="prefs" value="${renderRequest.preferences.map}"/>
<table border="0" cellspacing="2" cellpadding="3">
  <c:forEach var="prefName" items="showUserTab,showAttributesTab,showPasswordTab,showPasswordExpiration,showRoleTab,showGroupTab,showProfileTab,showPasswordOnUserTab,showChangePasswordRequiredForAddUser,showRoleForAddUser,showProfileForAddUser,defaultChangePasswordRequired">
  <tr>
    <c:set var="prefValue" value='${prefs[prefName][0]}'/>
    <td class="portlet-section-alternate">
      <font class="portlet-form-field-label"><fmt:message key="user.details.preference.${prefName}"/></font>
    </td>
    <td class="portlet-section-body">
      <input type="hidden" name="<c:out value="${prefName}"/>" value="<c:out value="${prefValue}"/>"/>
      <input type="checkbox" <c:if test="${prefValue}">checked</c:if> 
             onclick="t(this,<c:out value="${prefName}"/>)" class="portlet-form-field-label" />
    </td>
  </tr>
 </c:forEach>
  <tr>
    <c:set var="prefName" value="defaultRole"/>
    <c:set var="prefValue" value='${prefs[prefName][0]}'/>
    <td class="portlet-section-alternate">
      <font class="portlet-form-field-label"><fmt:message key="user.details.preference.${prefName}"/></font>
    </td>
    <td class="portlet-section-body">
      <select name="<c:out value="${prefName}"/>" class="portlet-form-field-label">     
              <option value=""/>                
              <c:forEach var="item" items="${jetspeedRoles}">
                <option value="<c:out value='${item}'/>" <c:if test="${item == prefValue}">selected="true"</c:if>>
                    <c:out value="${item}"/>
                </option>
              </c:forEach>
          </select>      
    </td>
  </tr>
  <tr>
    <c:set var="prefName" value="defaultProfile"/>
    <c:set var="prefValue" value='${prefs[prefName][0]}'/>
    <td class="portlet-section-alternate">
      <font class="portlet-form-field-label"><fmt:message key="user.details.preference.${prefName}"/></font>
    </td>
    <td class="portlet-section-body">
      <select name="<c:out value="${prefName}"/>" class="portlet-form-field-label">     
              <option value=""/>                
              <c:forEach var="item" items="${jetspeedRules}">
                <option value="<c:out value='${item}'/>" <c:if test="${item == prefValue}">selected="true"</c:if>>
                    <c:out value="${item}"/>
                </option>
              </c:forEach>
          </select>      
    </td>
  </tr>
</table>
<input type="submit" value="<fmt:message key="user.details.save.preferences"/>" class="portlet-form-button" />
</form>
