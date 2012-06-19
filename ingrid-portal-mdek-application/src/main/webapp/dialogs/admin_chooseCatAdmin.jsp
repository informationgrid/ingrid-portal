<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<fmt:setLocale value="<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>" scope="session" />
<fmt:setBundle basename="messages" scope="session"/>

<%
    String currentUser = (String)request.getSession(true).getAttribute("userName");
    if (!"admin".equals(currentUser)) {
        String destination ="login.jsp";
        response.sendRedirect(response.encodeRedirectURL(destination));
    }
%>
<html dir="ltr">

<head>
    <script type="text/javascript">
        var scriptScope = _container_;
        var isNewUser = false;
        var iplug = scriptScope.customParams.iplug;
        var callback = scriptScope.customParams.callback;
        
        var preparedData = prepareData(availableUsers);
        
        var usersChoiceStore = new dojo.data.ItemFileWriteStore(
            {data: {items: preparedData, identifier: 'id',label: 'label'}}
        );
        //usersChoice.setStore(newStore);
        
        function prepareData(users) {
            var data = [];
            dojo.forEach(users, function(user) {
               data.push({id: user, label:formatUser(user)}); 
            });
            return data;
        }
        
        function formatUser(user) {
            var formatString = "user";
            allUsers.some(function(u) {
                if (u.login == user) {
                    formatString = u.surname + ", " + u.firstName + " ("+u.login+")";
                    return true;
                }
            });
            return formatString;
        }
        function addCatalogueAndReload() {
            callback(iplug, usersChoice.value);
            //window.location.reload();
        }
    </script>
</head>

<body class="claro">
        <div>
            <div class="table container">
                <div class="tr">
                    <div class="td">Benutzer:</div><div class="td"><input jsId="usersChoice" dojoType="dijit.form.Select" store="usersChoiceStore"></div>
                </div>
                
                <div class="tr">
                    <div class="td"></div>
                    <div class="td">
                        <input type="button" id="btn_addCatalogue" onclick="addCatalogueAndReload();" value="Katalog verbinden">
                    </div>
                </div>
            </div>
            <span id="edit_errorAddUser" class="error" style="display:none;"></span>
        </div>
</body>
<html>