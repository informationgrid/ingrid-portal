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
<%@ page import="javax.portlet.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.portals.bridges.beans.TabBean" %>
<%@ page import="org.apache.jetspeed.portlets.security.users.UserDetailsPortlet" %>
<%@ page import="org.apache.jetspeed.portlets.security.SecurityResources" %>
<%@ page import="org.apache.jetspeed.portlets.security.users.JetspeedUserBean" %>
<%@ page import="org.apache.jetspeed.om.common.preference.*" %>
<%@ page import="org.apache.jetspeed.om.common.*" %>

<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.apache.jetspeed.portlets.security.resources.SecurityResources" />

<c:set var="errorMessagesKey"><%=SecurityResources.ERROR_MESSAGES%></c:set>
<c:set var="errorMessages" value="${requestScope[errorMessagesKey]}"/>

<portlet:defineObjects/>

<c:set var="user" value="${requestScope.user}" />
<c:set var="prefs" value="${renderRequest.preferences.map}"/>

<%--Beginning of User check --%>
<c:if test="${user != null}">

<c:set var="tabs" value="${requestScope.tabs}"/>
<c:set var="selectedTab" value="${requestScope.selected_tab}"/>

<br/>
<div class='portlet-section-header'>
<fmt:message key="user.principal.name"/> :
<span style='font-size:11pt; text-transform:uppercase'>
<c:out value="${user.principal}"/>
</span>
</div>
<br/>

<div id="tabs">
	<c:set var="tab_items" value="${tabs}"/>
	<c:set var="currentTab" value="${selectedTab}"/>
	<c:set var="url_param_name" value="selected_tab"/>
	<%@ include file="/WEB-INF/templates/administration/tabs.jsp"%>
</div>

<%--Beginning of User tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${currentTab.id == 'user'}">
  <div id="user">	
  <portlet:actionURL var="edit_user_link" />
  
  <c:if test="${errorMessages != null}">
    <jsp:include page="/WEB-INF/view/errors-include.jsp"/>
  </c:if>

	<form name="Edit_UserAttr_Form" action="<c:out value="${edit_user_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.edit_user"/>

    <table>
		
		<c:if test="${not empty requestScope.paUserAttributes}">
      <c:set var="canUpdate" value="true"/>
			<tr>
				<th class="portlet-section-header" colspan="2"><fmt:message key="user.attributes.header"/></th>
				<td colspan="2"></td>
			</tr>
	  </c:if>
    <c:forEach var="attr" items="${requestScope.paUserAttributes}">
			<tr>
				<td class="portlet-section-alternate">
          <c:out value="${attr.description}"/>
				</td>
				<td class="portlet-section-body">
				  <c:set var="attrName" value="${attr.name}"/>
					<input type="text" name="<c:out value="attr_${attr.name}"/>" value="<c:out value="${user.attributes[attrName]}"/>" class="portlet-form-field-label"/>
				</td>
				<td colspan="2"></td>
			</tr>
    </c:forEach>

    <c:if test='${prefs["showPasswordOnUserTab"][0]}'>
      <c:if test="${canUpdate}">
        <tr><td colspan="4">&nbsp;</td></tr>
      </c:if>
      <c:set var="canUpdate" value="true"/>
			<tr>
				<th class="portlet-section-header" colspan="4"><fmt:message key="user.password.header"/></th>
			</tr>
      <tr>
        <td class="portlet-section-alternate" >
          <fmt:message key="security.credential.value"/>
        </td>
        <td class="portlet-section-body" >
          <input type="password" name="user_cred_value" value="" class="portlet-form-field-label"/>
        </td>
        <td>
          &nbsp;
        </td>
        <td class="portlet-section-body" >
          <input type="hidden" name="user_cred_updreq" value="<c:out value="${credential.updateRequired}"/>"/>
          <input type="checkbox" 
                 <c:if test="${credential.updateRequired}">checked</c:if>
                 onclick="if(this.checked) user_cred_updreq.value='true';else user_cred_updreq.value='false';"
           class="portlet-form-field-label" />
          <fmt:message key="security.credential.update.required"/>
        </td>
      </tr>
      <tr>
        <td class="portlet-section-alternate" >
          <fmt:message key="security.credential.last.logon"/>
        </td>
        <td class="portlet-section-body" >
          <fmt:formatDate value="${credential.lastAuthenticationDate}" type="both" dateStyle="long" timeStyle="long"/>
        </td>
        <td>
          &nbsp;
        </td>
        <td class="portlet-section-body" >
          <input type="hidden" name="user_cred_enabled" value="<c:out value="${credential.enabled}"/>"/>
          <input type="checkbox" 
                 <c:if test="${credential.enabled}">checked</c:if>
                 onclick="if(this.checked) user_cred_enabled.value='true';else user_cred_enabled.value='false';"
           class="portlet-form-field-label" />
          <fmt:message key="security.enabled"/>
        </td>
      </tr>
      <c:if test='${prefs["showPasswordExpiration"][0]}'>
      <tr>
        <td class="portlet-section-alternate" >
          <fmt:message key="security.credential.expires"/>
        </td>
        <td class="portlet-section-body" nowrap>
          <fmt:formatDate value="${credential.expirationDate}" type="both" dateStyle="long" timeStyle="long"/>
        </td>
        <td>
          &nbsp;
        </td>
        <td class="portlet-section-body">
          <table cellpadding="0" cellspacing="0">
            <tr>
              <td>
              <c:if test="${!credential.expired}">
                <input type="radio" name="user_expired_flag" value="active" checked>
                <fmt:message key="security.active"/>
                &nbsp;
              </c:if>
              </td>
              <td>
                <input type="radio" name="user_expired_flag" value="expired" <c:if test="${credential.expired}">checked</c:if>>
                <fmt:message key="security.expired"/>
                &nbsp;
              </td>
              <td>
                <input type="radio" name="user_expired_flag" value="extend">
                <fmt:message key="security.Extend"/>
                &nbsp;
              </td>
              <td>
                <input type="radio" name="user_expired_flag" value="unlimited">
                <fmt:message key="security.Extend.Unlimited"/>
              </td>
            </tr>
          </table>
        </td>        
      </tr>
      </c:if>
    </c:if>		
    </table>
    <c:if test="${canUpdate}">
      <br/>
      <input type="submit" value="<fmt:message key="security.update"/>" class="portlet-form-button" />
    </c:if>
  </form>
    
  </div>  
</c:if>
<%--End of User tab data--%>

<%--Beginning of User Attributes tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${currentTab.id == 'user_attributes'}">
  <div id="attributes">	
  <portlet:actionURL var="edit_user_attr_link" />
  
  <c:if test="${errorMessages != null}">
    <jsp:include page="/WEB-INF/view/errors-include.jsp"/>
  </c:if>

	<form name="Edit_UserAttr_Form" action="<c:out value="${edit_user_attr_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.edit_user_attribute"/>
		
		<table>
			<tr>
				<th class="portlet-section-header" >&nbsp;</th>
				<th class="portlet-section-header" ><fmt:message key="security.name"/></th>
				<th class="portlet-section-header" ><fmt:message key="security.value"/></th>
			</tr>
		<c:forEach var="entry" items="${user.attributes}">
			<tr>
			<%--<input type="hidden" name="user_attr_name" value="<c:out value="${userAttr.name}"/>"/>--%>
			
				<td class="portlet-section-body" >
					<input type="checkbox" name="user_attr_id" value="<c:out value="${entry.key}"/>"/>
				</td>
				<td class="portlet-section-body" >
					<c:out value="${entry.key}"/>
				</td>
				<td class="portlet-section-body" >
					<input type="text" name="<c:out value="${entry.key}"/>:value" value="<c:out value="${entry.value}"/>"/>
				</td>
			</tr>
		</c:forEach>
		</table>
		<input type="submit" value="<fmt:message key="security.update"/>" onClick="this.form.portlet_action.value = 'security_user.update_user_attribute'" class="portlet-form-button" />
		<input type="submit" value="<fmt:message key="security.remove"/>" onClick="this.form.portlet_action.value = 'security_user.remove_user_attribute'" class="portlet-form-button" />
	</form>
	<form name="Add_UserAttr_Form" action="<c:out value="${edit_user_attr_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.add_user_attribute"/>
		
		<table>
			<tr>
				<td class="portlet-section-alternate" >
					<fmt:message key="security.name"/>
				</td>
				<td class="portlet-section-body" >
					<input type="text" name="user_attr_name" value=""/>
				</td>
			</tr>
			<tr>
				<td class="portlet-section-alternate" >
					<fmt:message key="security.value"/>
				</td>
				<td class="portlet-section-body" >
					<input type="text" name="user_attr_value" value=""/>
				</td>
			</tr>
		</table>
		<input type="submit" value="<fmt:message key="security.add"/>" class="portlet-form-button" />
	</form>
  </div>	
</c:if>
<%--End of User Attributes tab data--%>

<%--Beginning Security Credential tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'user_credential'}">
  <div id="Credential">
  <portlet:actionURL var="edit_credential_link" />
  
  <c:if test="${errorMessages != null}">
    <jsp:include page="/WEB-INF/view/errors-include.jsp"/>
  </c:if>

  <form name="Edit_Credential_Form" action="<c:out value="${edit_credential_link}"/>" method="post">
    <input type="hidden" name="portlet_action" value="security_user.update_user_credential"/>    
    <table>
      <tr>
        <td class="portlet-section-alternate" >
          <fmt:message key="security.credential.value"/>
        </td>
        <td class="portlet-section-body" >
          <input type="password" name="user_cred_value" value=""/>
        </td>
        <td>
          &nbsp;
        </td>
        <td class="portlet-section-body" >
          <input type="hidden" name="user_cred_updreq" value="<c:out value="${credential.updateRequired}"/>"/>
          <input type="checkbox" 
                 <c:if test="${credential.updateRequired}">checked</c:if>
                 onclick="if(this.checked) user_cred_updreq.value='true';else user_cred_updreq.value='false';"
           class="portlet-form-field-label" />
          <fmt:message key="security.credential.update.required"/>
        </td>
      </tr>
      <tr>
        <td class="portlet-section-alternate" >
          <fmt:message key="security.credential.last.logon"/>
        </td>
        <td class="portlet-section-body" >
          <fmt:formatDate value="${credential.lastAuthenticationDate}" type="both" dateStyle="long" timeStyle="long"/>
        </td>
        <td>
          &nbsp;
        </td>
        <td class="portlet-section-body" >
          <input type="hidden" name="user_cred_enabled" value="<c:out value="${credential.enabled}"/>"/>
          <input type="checkbox" 
                 <c:if test="${credential.enabled}">checked</c:if>
                 onclick="if(this.checked) user_cred_enabled.value='true';else user_cred_enabled.value='false';"
           class="portlet-form-field-label" />
          <fmt:message key="security.enabled"/>
        </td>
      </tr>
      <c:if test='${prefs["showPasswordExpiration"][0]}'>      
      <tr>
        <td class="portlet-section-alternate" >
          <fmt:message key="security.credential.expires"/>
        </td>
        <td class="portlet-section-body" >
          <fmt:formatDate value="${credential.expirationDate}" type="both" dateStyle="long" timeStyle="long"/>
        </td>
        <td>
          &nbsp;
        </td>
        <td class="portlet-section-body" >
          <table cellpadding="0" cellspacing="0">
            <tr>
              <td>
              <c:if test="${!credential.expired}">
                <input type="radio" name="user_expired_flag" value="active" checked>
                <fmt:message key="security.active"/>
                &nbsp;
              </c:if>
              </td>
              <td>
                <input type="radio" name="user_expired_flag" value="expired" <c:if test="${credential.expired}">checked</c:if>>
                <fmt:message key="security.expired"/>
                &nbsp;
              </td>
              <td>
                <input type="radio" name="user_expired_flag" value="extend">
                <fmt:message key="security.Extend"/>
                &nbsp;
              </td>
              <td>
                <input type="radio" name="user_expired_flag" value="unlimited">
                <fmt:message key="security.Extend.Unlimited"/>
              </td>
            </tr>
          </table>
        </td>
      </tr>
      </c:if>
    </table>
    <input type="submit" value="<fmt:message key="security.update"/>" class="portlet-form-button" />
  </form>
    
  </div>  
</c:if>
<%--End of Security Credential tab data--%>

<%--Beginning Security Role tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'user_role'}">
  <div id="Role">
  <portlet:actionURL var="edit_role_link" />
  
  <c:if test="${errorMessages != null}">
    <jsp:include page="/WEB-INF/view/errors-include.jsp"/>
  </c:if>

	<form name="Edit_Role_Form" action="<c:out value="${edit_role_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.edit_role"/>		
		<table>
			<tr>
				<th class="portlet-section-header" >&nbsp;</th>
				<th class="portlet-section-header" ><fmt:message key="security.rolename"/></th>
			</tr>
		<c:forEach var="role" items="${roles}">
			<tr>			
				<td class="portlet-section-body" >
					<input type="checkbox" name="user_role_id" value="<c:out value="${role.principal.name}"/>"/>
				</td>
				<td class="portlet-section-body" >
					<c:out value="${role.principal.name}"/>
				</td>
			</tr>
		</c:forEach>
		</table>
		<input type="submit" value="<fmt:message key="security.remove"/>" onClick="this.form.portlet_action.value = 'security_user.remove_user_role'" class="portlet-form-button" />
    </form>
    <c:if test="${not empty jetspeedRoles}">
  	  <form name="Add_Role_Form" action="<c:out value="${edit_role_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.add_user_role"/>
		
		<table>
			<tr>
				<td class="portlet-section-alternate" >
					<fmt:message key="security.rolename"/>
				</td>
			    <td class="portlet-section-body" align="left">
			 		<select name="role_name" class="portlet-form-field-label">		
						<c:forEach var="roleName" items="${jetspeedRoles}">			    
						    <option value="<c:out value='${roleName}'/>">
							  <c:out value="${roleName}"/>						    
						    </option>
						</c:forEach>
					</select>      
			    </td>				
			</tr>
		</table>
		<input type="submit" value="<fmt:message key="security.add"/>" class="portlet-form-button" />
	  </form>
    </c:if>
  </div>  
</c:if>
<%--End of Security Role tab data--%>

<%--Beginning Security Group tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'user_group'}">
  <div id="Group">
  <portlet:actionURL var="edit_group_link" />
  
  <c:if test="${errorMessages != null}">
    <jsp:include page="/WEB-INF/view/errors-include.jsp"/>
  </c:if>

	<form name="Edit_Group_Form" action="<c:out value="${edit_group_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.edit_group"/>		
		<table>
			<tr>
				<th class="portlet-section-header" >&nbsp;</th>
				<th class="portlet-section-header" ><fmt:message key="security.groupname"/></th>
			</tr>
		<c:forEach var="group" items="${groups}">
			<tr>			
				<td class="portlet-section-body" >
					<input type="checkbox" name="user_group_id" value="<c:out value="${group.principal.name}"/>"/>
				</td>
				<td class="portlet-section-body" >
					<c:out value="${group.principal.name}"/>
				</td>				
			</tr>
		</c:forEach>
		</table>
		<input type="submit" value="<fmt:message key="security.remove"/>" onClick="this.form.portlet_action.value = 'security_user.remove_user_group'" class="portlet-form-button" />
    </form>
    <c:if test="${not empty jetspeedGroups}">
	  <form name="Add_Group_Form" action="<c:out value="${edit_group_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.add_user_group"/>
		
		<table>
			<tr>
				<td class="portlet-section-alternate" >
					<fmt:message key="security.groupname"/>
				</td>
			    <td class="portlet-section-body" align="left">
			 		<select name="group_name" class="portlet-form-field-label">		
						<c:forEach var="groupName" items="${jetspeedGroups}">			    
						    <option value="<c:out value='${groupName}'/>">
							  <c:out value="${groupName}"/>						    
						    </option>
						</c:forEach>
					</select>      
			    </td>								
			</tr>
		</table>
		<input type="submit" value="<fmt:message key="security.add"/>" class="portlet-form-button" />
	  </form>
    </c:if>
  </div>  
</c:if>
<%--End of Security Group tab data--%>


<%--Beginning Profile tab data--%>
<%--TODO:  switch to c:choose --%>
<c:if test="${selectedTab.id == 'user_profile'}">
  <div id="Profile">	
  <portlet:actionURL var="edit_profile_link" />
  
  <c:if test="${errorMessages != null}">
    <jsp:include page="/WEB-INF/view/errors-include.jsp"/>
  </c:if>

	<form name="Edit_Profile_Form" action="<c:out value="${edit_profile_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.remove_user_rule"/>		
		<table>
			<tr>
				<th class="portlet-section-header" >&nbsp;</th>
				<th class="portlet-section-header" ><fmt:message key="security.name"/></th>
				<th class="portlet-section-header" ><fmt:message key="security.rule"/></th>
			</tr>
		<c:forEach var="rule" items="${rules}">
			<tr>			
				<td class="portlet-section-body" >
					<input type="checkbox" name="user_profile_id" value="<c:out value="${rule.locatorName}"/>"/>
				</td>
				<td class="portlet-section-body" >
					<c:out value="${rule.locatorName}"/>
				</td>
				<td class="portlet-section-body" >
					<c:out value="${rule.profilingRule}"/>
				</td>
			</tr>
		</c:forEach>
		</table>
		<input type="submit" value="<fmt:message key="security.remove"/>" onClick="this.form.portlet_action.value = 'security_user.remove_user_rule'" class="portlet-form-button" />
	</form>
	<form name="Add_Profile_Form" action="<c:out value="${edit_profile_link}"/>" method="post">
		<input type="hidden" name="portlet_action" value="security_user.add_rule"/>
		
		<table>
			<tr>
				<td class="portlet-section-alternate" >
					<fmt:message key="security.name"/>
				</td>
				<td class="portlet-section-body" >
					<input type="text" name="locator_name" value=""/>
					<fmt:message key="security.common.locator.names"/>
				</td>
			</tr>
			<tr>
				<td class="portlet-section-alternate" >
					<fmt:message key="security.rule"/>
				</td>
				<td class="portlet-section-body" >
					<select name="select_rule" class="portlet-form-field-label">								
						<c:forEach var="prule" items="${prules}">						    						    
						    <option value="<c:out value="${prule.id}"/>">
							  <c:out value="${prule.id}"/>
						    </option>
						</c:forEach>
					</select>
				</td>
			</tr>
		</table>
		<input type="submit" value="<fmt:message key="security.add"/>" class="portlet-form-button" />
	</form>
  </div>	
</c:if>
<%--End of Profile tab data--%>

<table>
<tr>
<td>
<portlet:actionURL var="addUser" />
<form action="<c:out value="${addUser}"/>" method="post">
<input type="hidden" name="portlet_action" value="add.new.user"/>
<input type="submit" value="<fmt:message key="security.add.new.user"/>" class="portlet-form-button"/>
</form>
</td>
<td>
<portlet:actionURL var="removeUser" />
<form action="<c:out value="${removeUser}"/>" method="post">
<input type="hidden" name="portlet_action" value="remove.user"/>
<input type="submit" value="<fmt:message key="security.remove.user"/>" class="portlet-form-button"/>
</form>
</td>
</tr>
</table>
<%--End of User check --%>
</c:if>

<%-- Add New User --%>
<c:if test="${user == null}">
<script language="JavaScript">function t(i,f){if(i.checked) f.value='true';else f.value='false';}</script>

<h3 class="portlet-section-header"><fmt:message key="security.add.user"/></h3>

  <c:if test="${errorMessages != null}">
    <jsp:include page="/WEB-INF/view/errors-include.jsp"/>
  </c:if>

<div class="portlet-section-text">
<portlet:actionURL var="addUser" />
<form action="<c:out value="${addUser}"/>" method="post">
<input type='hidden' name='portlet_action' value='add.user'/>
<table>
  <tr colspan="2" align="right">
    <td nowrap class="portlet-section-alternate" align="right"><fmt:message key="security.new.user.name"/>&nbsp;</td>
    <td class="portlet-section-body" align="left">
      <input type="text" name="jetspeed.user" size="30" value="" class="portlet-form-field-label">
    </td>
  </tr>
  <tr colspan="2" align="right">
    <td nowrap class="portlet-section-alternate" align="right"><fmt:message key="security.password"/>&nbsp;</td>
    <td class="portlet-section-body" align="left">
      <input type="password" name="jetspeed.password" size="30" value="" class="portlet-form-field-label">
    </td>
  </tr>
  
  <c:set var="defaultRole" value='${prefs["defaultRole"][0]}'/>
  <c:set var="defaultProfile" value='${prefs["defaultProfile"][0]}'/>
  
  <c:set var="prefName" value="showChangePasswordRequiredForAddUser"/>
  <c:set var="prefValue" value='${prefs[prefName][0]}'/>
  <c:choose>
    <c:when test='${prefs[prefName][0]}'>
      <tr colspan="2" align="right">
        <td nowrap class="portlet-section-alternate" align="right"><fmt:message key="security.change.password.on.first.login"/>&nbsp;</td>
        <td class="portlet-section-body" align="left">
          <c:set var="prefName" value="defaultChangePasswordRequired"/>
          <c:set var="prefValue" value='${prefs[prefName][0]}'/>
          <input type="hidden" name="user_cred_updreq" value="<c:out value="${prefValue}"/>"/>
          <input type="checkbox" <c:if test="${prefValue}">checked</c:if> 
             onclick="t(this,user_cred_updreq)" class="portlet-form-field-label" />
        </td>
      </tr>
    </c:when>
    <c:otherwise>
      <input type="hidden" name="jetspeedRoles" value="<c:out value="${defaultRole}"/>">
    </c:otherwise>
  </c:choose>

  <c:choose>
    <c:when test='${prefs["showRoleForAddUser"][0]}'>
      <!-- Select Roles -->
      <tr colspan="2" align="right">
        <td nowrap class="portlet-section-alternate" align="right"><fmt:message key="security.default.role"/>&nbsp;</td>
        <td class="portlet-section-body" align="left">
     		<select name="jetspeedRoles" class="portlet-form-field-label">		
    			<option value=""/> 		 		
    			<c:forEach var="roleName" items="${jetspeedRoles}">			    
    			    <option value="<c:out value='${roleName}'/>"
      			    <c:if test="${roleName == defaultRole}">selected="true"</c:if>>			    
    				  <c:out value="${roleName}"/>
    			    </option>
    			</c:forEach>
    		</select>      
        </td>
      </tr>
    </c:when>
    <c:otherwise>
      <input type="hidden" name="jetspeedRoles" value="<c:out value="${defaultRole}"/>">
    </c:otherwise>
  </c:choose>

  <c:choose>
    <c:when test='${prefs["showProfileForAddUser"][0]}'>
      <!-- Select Profiling Rules -->
      <tr colspan="2" align="right">
        <td nowrap class="portlet-section-alternate" align="right"><fmt:message key="security.profiling.rule"/>&nbsp;</td>
        <td class="portlet-section-body" align="left">
     		<select name="jetspeedRules" class="portlet-form-field-label">		
    			<option value=""/> 		
    			<c:forEach var="ruleName" items="${jetspeedRules}">
    			    <option value="<c:out value='${ruleName}'/>"
      			    <c:if test="${ruleName == defaultProfile}">selected="true"</c:if>>
    				  <c:out value="${ruleName}"/>
    			    </option>
    			</c:forEach>
    		</select>      
        </td>
      </tr>
    </c:when>
    <c:otherwise>
      <input type="hidden" name="jetspeedRules" value="<c:out value="${defaultProfile}"/>">
    </c:otherwise>
  </c:choose>
  
</table>
<br/>
<input type="submit" value="<fmt:message key="security.add.user.submit"/>" class="portlet-form-button"/>
</form>
<c:if test="${errorMessage != null}">
  <li style="color:red"><c:out value="${errorMessage}"/></li>
</c:if>

</c:if>
