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
    if ("admin".equals(currentUser)) {
        System.out.println("Session name: " + request.getSession(true).getAttribute("userName"));
        String destination ="admin.jsp";
        response.sendRedirect(response.encodeRedirectURL(destination));
    } else if (currentUser != null) {
        String destination ="start.jsp";
        String redirect = request.getParameter("r") == null ? "" : request.getParameter("r");
        if (redirect.length() > 0) destination = redirect; 
        response.sendRedirect(response.encodeRedirectURL(destination));
    }
    
%>

<html dir="ltr">
    <head>
        <link rel="stylesheet" type="text/css" href="dojo-sources/release/lib/ingrid/css/admin.css"></link>
        <script type='text/javascript' src='dwr/engine.js'></script>
        <script type='text/javascript' src='dwr/util.js'></script>
        <script src='dwr/interface/SecurityService.js'></script>
        
        <script type="text/javascript">
            // only allow to login if portal is not connected
            SecurityService.isPortalConnected(function(response) {
                if (response == true) {
                    window.location.href = "session_expired.jsp";
                }
            });
            function authenticate() {
                var username = document.getElementById("username").value;
                var password = document.getElementById("password").value;

                // check if it's the admin
//                 if (username == "admin") { // && isValidAdminPassword(password)"
//                     SecurityService.checkAdminLogin(username, password, function(result) {
//                         window.location.reload();
//                     });
//                 } else {
                
	                // check if login is registered IGE-user
	                SecurityService.authenticate(username, password, function(result) {
	                    if (result == true) {
	                        //window.location.href = "start.jsp";
	                        window.location.reload();
	                    } else {
	                        showLoginError();
	                    }
	                });
//                 }
            }

            function showLoginError() {
                document.getElementById("error").style.display = "block";
            }
            
            window.onload = function () {
                var pwField = document.getElementById("password");
                if (!pwField.addEventListener) {
                    pwField.attachEvent("keydown", function (event) { 
                        if (event.keyCode == 13) {
                            authenticate();
                        }
                    });
                } else {
                    pwField.addEventListener('keydown', function (event) { 
                        if (event.keyCode == 13) {
                            authenticate();
                        }
                    });
                }
            }
            
        </script>
    </head>
    
    <body>
        <div id="header">
            <img src="img/logo.gif" alt="InGrid Editor">
            <h1>InGrid-Editor Administration</h1>
        </div>
        <div id="menu">
            <div class="block"><p></p></div><h2>Login</h2></div>
        </div>
        <!--<form action='<%= response.encodeURL("j_security_check") %>' method="POST">-->
        <div id="" class="content" style="display: block;">
            <div class="contentBorder">
                <h3>Loginseite</h3>
                <div style="padding: 10px;">
                    <table style="width: 100%;">
                        <tr><td>Login:</td><td><input id="username" name="j_username" style="width: 200px;"></td></tr>
                        <tr><td>Passwort:</td><td><input id="password" name="j_password" type="password" style="width: 200px;"></td></tr>
                        <tr><td></td><td><input id="submit" type="submit" onclick="authenticate()" name="Login" value="Login"></td></tr>
						<!--<tr><td></td><td><input id="submit" type="submit" name="Login" value="Login"></td></tr>-->
                        <span id="error" class="error" style="display:none;">Unbekannter Benutzername oder Passwort!</span>
                    </table>
                </div>
            </div>
        </div>
        <!--</form>-->
    </body>
</html>
