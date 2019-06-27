<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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


<fmt:setLocale value='<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>' scope="session" />

<html dir="ltr">

<head>
    <script type="text/javascript">
    var dialogAdminEditUser = _container_;

    require([
        "dojo/dom",
        "dojo/dom-style",
        "dojo/domReady!"
    ], function(dom, style) {

        var isNewUser = false;
        var user = _container_.customParams.user;
        var callback = _container_.customParams.callback;
        var dlgContainer = _container_;

        if (user) {
            UserRepoManager.getUser(user, function(data) {
                dom.byId("edit_login").value = data.login;
                dom.byId("edit_firstName").value = data.firstName;
                dom.byId("edit_surname").value = data.surname;
                dom.byId("edit_email").value = data.email;
            });
            style.set("btn_addUser", "display", "none");

        } else {
            // new user!
            isNewUser = true;
            style.set("btn_updateUser", "display", "none");
            style.set("passwordInfo", "display", "none");
            dom.byId("edit_login").disabled = false;
            
        }
        
        function updateUser() {
            var execCallback = callback;
            var user = {};
            user.login = dom.byId("edit_login").value;
            user.password = dom.byId("edit_password").value;
            user.firstName = dom.byId("edit_firstName").value;
            user.surname = dom.byId("edit_surname").value;
            user.email = dom.byId("edit_email").value;
            
            var errors = isValidUserData(user, dom.byId("edit_password_again").value);
            if (errors.length === 0) {
                UserRepoManager.updateUser(user.login, user, function(success) {
                    console.debug("success updating user: " + success);
                    if (execCallback) execCallback(user);
                });
            } else {
                style.set("edit_errorAddUser", "display", "");
                dom.byId("edit_errorAddUser").innerHTML = errors;
            }

            dlgContainer.hide();
        }
        
        function addNewUser() {
            var execCallback = callback;
            var user = {};
            user.login = dom.byId("edit_login").value;
            user.password = dom.byId("edit_password").value;
            user.firstName = dom.byId("edit_firstName").value;
            user.surname = dom.byId("edit_surname").value;
            user.email = dom.byId("edit_email").value;
            
            var errors = isValidUserData(user, dom.byId("edit_password_again").value);
            errors += isValidNewUserData(user);
            

            // check if username already exists
            if (errors.length === 0) {
                UserRepoManager.addUser(user, function(success) {
                    console.debug("success adding user: " + success);
                    if (execCallback) execCallback(user);
                });
            } else {
                style.set("edit_errorAddUser", "display", "");
                dom.byId("edit_errorAddUser").innerHTML = errors;
            }

            dlgContainer.hide();
        }
        
        function isValidNewUserData(user) {
            var errors = "";
            if (user.password == "") {
                errors += "<p>Passwort darf nicht leer sein!</p>";
            }
            
            if (pageAdminOnly.usernameExists(user.login)) {
                errors += "<p>Login schon vorhanden! Bitte ein anderes auswählen.</p>";
            }
            
            return errors;
        }
        
        function isValidUserData(user, passwordRepeat) {
            var errors = "";
            
            if (user.login == "" || user.firstName == "" || user.surname == "" || user.email == "")
                errors += "<p>Alle Felder müssen ausgefüllt sein!</p>";
            
            if (user.password != passwordRepeat)
                errors += "<p>Passwort stimmt nicht überein! Bitte erneut eingeben.</p>";
            
            return errors;
        }

        function validateEmail() {
            console.log(".");
            var login = dom.byId("edit_login").value;
            var email = dom.byId("edit_email").value;

            let emailExists = pageAdminOnly.emailExists(email);
            if (emailExists && emailExists.login !== login) {
                style.set("multipleEmailsError", "display", "block");
            } else {
                style.set("multipleEmailsError", "display", "none");
            }
        }

        dialogAdminEditUser = {
            addNewUser: addNewUser,
            updateUser: updateUser,
            validateEmail: validateEmail
        };

    });
    </script>
</head>

<body class="claro">
        <div id="editUserDiv" style="display: block; width: 400px;">
            <div class="table container">
                <div class="tr">
                    <div class="td">Login:</div><div class="td"><input id="edit_login" type="text" disabled></div>
                </div>
                <div class="tr">
                    <div class="td">Passwort:</div><div class="td"><input id="edit_password" type="password" autocomplete="new-password"></div>
                </div>
                <div class="tr">
                    <div class="td">Passwort (Wiederholung):</div><div class="td"><input id="edit_password_again" type="password"></div>
                </div>
                <div class="tr">
                    <div class="td"></div><div class="td"><span class="comment" id="passwordInfo">Leer lassen, wenn Passwort<br />unverändert bleiben soll!</span></div>
                </div>
                <div class="tr">
                    <div class="td">Vorname:</div><div class="td"><input id="edit_firstName" type="text"></div>
                </div>
                <div class="tr">
                    <div class="td">Nachname:</div><div class="td"><input id="edit_surname" type="text"></div>
                </div>
                <div class="tr">
                    <div class="td">E-Mail:</div><div class="td"><input data-dojo-type="dijit/form/ValidationTextBox" onkeyup="dialogAdminEditUser.validateEmail()" id="edit_email" type="text"></div>
                </div>
                <div class="tr">
                    <div class="td">
                        <button data-dojo-type="dijit/form/Button" id="btn_updateUser" onclick="dialogAdminEditUser.updateUser()">Benutzer aktualisieren</button>
                        <button data-dojo-type="dijit/form/Button" id="btn_addUser" onclick="dialogAdminEditUser.addNewUser()">Benutzer hinzufügen</button>
                    </div>
                </div>
            </div>
            <div id="multipleEmailsError" class="tr error" style="display: none;">
                Diese Email wird bereits verwendet. Die Funktion "Passwort vergessen" geht für diese Benutzer mit der Email nicht.
            </div>
            <span id="edit_errorAddUser" class="error" style="display:none;"></span>
        </div>
</body>
<html>
