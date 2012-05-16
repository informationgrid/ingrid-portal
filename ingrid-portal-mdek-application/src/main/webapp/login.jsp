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
        <link rel="stylesheet" type="text/css" href="css/admin.css"></link>
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
	                    console.debug("result is: ");
	                    console.debug(result);
	                    if (result == true) {
	                        console.debug("accept");
	                        //window.location.href = "start.jsp";
	                        window.location.reload();
	                    } else {
	                        console.debug("reject");
	                        showLoginError();
	                    }
	                });
//                 }
            }

            function showLoginError() {
                document.getElementById("error").style.display = "block";
            }
            
            window.onload = function () {
                document.getElementById("password").addEventListener('keydown', function (event) { 
                    if (event.keyCode == 13) {
                        authenticate();
                    }
                });
            }
            
        </script>
    </head>
    
    <body>
        <div id="wrapper">
            <table style="width: 100%;">
                <tr><td>Username:</td><td><input id="username" style="width: 100%;"></td></tr>
                <tr><td>Password:</td><td><input id="password" type="password" style="width: 100%;"></td></tr>
                <tr><td></td><td><input id="submit" onclick="authenticate()" type="button" name="Login" value="Login" style="float:right;"></td></tr>
                <span id="error" class="error" style="display:none;">Invalid Username or password!</span>
            </table>
        </div>
    </body>
</html>
