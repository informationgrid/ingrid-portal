<%--
  **************************************************-
  Ingrid Portal MDEK Application
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
    var dialogAdminChooseCatAdmin = null,
        usersChoice;

    require([
        "dojo/_base/array",
        "dojo/store/Memory",
        "dojo/on"
    ], function(array, Memory, on) {

        var iplug = _container_.customParams.iplug;
        var callback = _container_.customParams.callback;
        var availableUsers = _container_.customParams.availableUsers;
        var allUsers = _container_.customParams.allUsers;
        
        var preparedData = prepareData(availableUsers);
        
        var usersChoiceStore = new Memory(
            {data: {items: preparedData}}
        );

        on(_container_, "Load", function() {
            usersChoice.set("store", usersChoiceStore);
        });
        
        function prepareData(users) {
            var data = [];
            array.forEach(users, function(user) {
                data.push({"id": user, "label":formatUser(user)});
            });
            return data;
        }
        
        function formatUser(user) {
            var formatString = "user";
            array.some(allUsers, function(u) {
                if (u.login == user) {
                    formatString = u.surname + ", " + u.firstName + " ("+u.login+")";
                    return true;
                }
            });
            return formatString;
        }

        function addCatalogueAndReload() {
            callback(iplug, usersChoice.value);
        }

        dialogAdminChooseCatAdmin = {
            addCatalogueAndReload: addCatalogueAndReload
        };
    });
    </script>
</head>

<body class="claro">
        <div>
            <div class="table container">
                <div class="tr">
                    <div class="td">Benutzer:</div><div class="td"><input data-dojo-id="usersChoice" data-dojo-type="dijit/form/Select" data-dojo-props="labelAttr: 'label'"></div>
                </div>
                
                <div class="tr">
                    <div class="td"></div>
                    <div class="td">
                        <input type="button" id="btn_addCatalogue" onclick="dialogAdminChooseCatAdmin.addCatalogueAndReload()" value="Katalog verbinden">
                    </div>
                </div>
            </div>
            <span id="edit_errorAddUser" class="error" style="display:none;"></span>
        </div>
</body>
<html>