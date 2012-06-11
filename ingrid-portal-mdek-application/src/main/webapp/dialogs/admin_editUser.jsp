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
        var user = scriptScope.customParams.user;
        var callback = scriptScope.customParams.callback;
        
        if (user) {
            dojo.byId("edit_login").value = user.login;
            dojo.byId("edit_firstName").value = user.firstName;
            dojo.byId("edit_surname").value = user.surname;
            dojo.byId("edit_email").value = user.email;
            dojo.byId("btn_addUser").style.display = "none";
        } else {
            // new user!
            isNewUser = true;
            dojo.byId("btn_updateUser").style.display = "none";
            dojo.byId("passwordInfo").style.display = "none";
            dojo.byId("edit_login").disabled = false;
            
        }
        
        function updateUser() {
            var user = {};
            user.login = dojo.byId("edit_login").value;
            user.password = dojo.byId("edit_password").value;
            user.firstName = dojo.byId("edit_firstName").value;
            user.surname = dojo.byId("edit_surname").value;
            user.email = dojo.byId("edit_email").value;
            
            var errors = isValidUserData(user, dojo.byId("edit_password_again").value);
            
            if (errors.length == 0) { 
                UserRepoManager.updateUser(user.login, user, function(success) {
                    window.location.search="?section=user&rnd="+Math.random();
                    console.debug("success updating user: " + success);
                });
            } else {
                dojo.style("edit_errorAddUser", "display", "");
                dojo.byId("edit_errorAddUser").innerHTML = errors;
            }
        }
        
        function addNewUser() {
            var user = {};
            user.login = dojo.byId("edit_login").value;
            user.password = dojo.byId("edit_password").value;
            user.firstName = dojo.byId("edit_firstName").value;
            user.surname = dojo.byId("edit_surname").value;
            user.email = dojo.byId("edit_email").value;
            
            var errors = isValidUserData(user, dojo.byId("edit_password_again").value);
            errors += isValidNewUserData(user);
            

            // check if username already exists
            if (errors.length == 0) { 
                UserRepoManager.addUser(user, function(success) {
                    window.location.search="?section=user&rnd="+Math.random();
                    //window.location.reload();
                    console.debug("success adding user: " + success);
                });
            } else {
                dojo.style("edit_errorAddUser", "display", "");
                dojo.byId("edit_errorAddUser").innerHTML = errors;
            }
        }
        
        function isValidNewUserData(user) {
            var errors = "";
            if (user.password == "") {
                errors += "<p>Password must not be empty!</p>";
            }
            
            if (usernameExists(user.login))
                errors += "<p>Login already exists! Please choose another one.</p>";
            
            return errors;
        }
        
        function isValidUserData(user, passwordRepeat) {
            var errors = "";
            
            if (user.login == "" || user.firstName == "" || user.surname == "" || user.email == "")
                errors += "<p>All fields have to be filled!</p>";
            
            if (user.password != passwordRepeat)
                errors += "<p>Password does not match! Please type again.</p>";
            
            return errors;
        }
    </script>
</head>

<body class="claro">
        <div id="editUserDiv" class="" style="display: block;">
            <div class="table container">
                <div class="tr">
                    <div class="td">Login:</div><div class="td"><input id="edit_login" type="text" disabled></div>
                </div>
                <div class="tr">
                    <div class="td">Password:</div><div class="td"><input id="edit_password" type="password"></div>
                </div>
                <div class="tr">
                    <div class="td">Password (repeat):</div><div class="td"><input id="edit_password_again" type="password"></div>
                </div>
                <div class="tr">
                    <div class="td"></div><div class="td"><span class="comment" id="passwordInfo">Leave password empty for using old one!</span></div>
                </div>
                <div class="tr">
                    <div class="td">First Name:</div><div class="td"><input id="edit_firstName" type="text"></div>
                </div>
                <div class="tr">
                    <div class="td">Last Name:</div><div class="td"><input id="edit_surname" type="text"></div>
                </div>
                <div class="tr">
                    <div class="td">Email:</div><div class="td"><input id="edit_email" type="text"></div>
                </div>
                <div class="tr">
                    <div class="td">
                        <input type="button" id="btn_updateUser" onclick="updateUser()" value="Update User">
                        <input type="button" id="btn_addUser" onclick="addNewUser()" value="Add User">
                    </div>
                </div>
            </div>
            <span id="edit_errorAddUser" class="error" style="display:none;"></span>
        </div>
</body>
<html>