<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<fmt:setLocale value='<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>' scope="session" />
<html>
<head>
    <title>Passwort vergessen</title>
    <link rel="stylesheet" type="text/css" href="dojo-sources/ingrid/css/admin.css"/>
    <script type='text/javascript' src='dwr/engine.js'></script>
    <script type='text/javascript' src='dwr/util.js'></script>
    <script src='dwr/interface/SecurityService.js'></script>

    <script type="text/javascript">

        function forgotPassword() {
            var email = document.getElementById("email").value;

            SecurityService.sendPasswordEmail(email, {
                callback: function (result) {
                    if (result === true) {
                        console.log("Password sent");
                        showSuccess();
                    } else {
                        console.log("Error sending password");
                        showError();
                    }
                },
                errorHandler: function (error) {
                    console.error("Could not send password!", error);
                    showError('send');
                }
            });
        }

        function showError(type) {

            document.getElementById("error").style.display = "none";
            document.getElementById("error-send").style.display = "none";

            if (type === 'send') {
                document.getElementById("error-send").style.display = "block";
            } else {
                document.getElementById("error").style.display = "block";
            }
        }

        function showSuccess() {
            document.getElementById("error").style.display = "none";
            document.getElementById("error-send").style.display = "none";
            document.getElementById("success").style.display = "block";

            setTimeout(function() {
                gotoLoginPage();
            }, 3000);
        }

        function gotoLoginPage() {
            window.location.href = "login.jsp";
        }
    </script>
</head>
<body>
<div id="header">
    <img src="img/logo.png" alt="InGrid Editor">
    <h1><fmt:message key="ui.login.title"/></h1>
</div>

<input onclick="gotoLoginPage()" type="submit" value="Zurück" style="float: left;">

<div class="content-password" style="display: block; padding-left: 140px;">
    <div class="contentBorder">
        <h3>Passwort vergessen</h3>
        <div style="padding: 10px;">
            <table style="width: 100%;">
                <tr>
                    <td>Email-Adresse:</td>
                    <td>
                        <input id="email" name="email" style="width: 200px;">
                        <input id="forgotPassword" type="submit" onclick="forgotPassword()" name="forgotPassword" value="Neues Passwort zuschicken">
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>

                    </td>
                </tr>
                <div id="error" class="error" style="display:none;">Email-Adresse ist nicht registriert oder mehrfach vorhanden! Bitte setzen Sie sich unter Angabe Ihres Logins und Ihrer E-Mail mit dem mCLOUD-Team unter mcloud@itzbund.de in Verbindung</div>
                <div id="error-send" class="error" style="display:none;">Email konnte nicht verschickt werden</div>
                <div id="success" style="display:none;">Email zum Ändern des Passworts wurde gesendet</div>
            </table>
        </div>
    </div>
</div>
</body>
</html>
