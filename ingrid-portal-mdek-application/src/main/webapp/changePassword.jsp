<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<fmt:setLocale value='<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>' scope="session" />
<html>
<head>
    <title>Passwort ändern</title>
    <link rel="stylesheet" type="text/css" href="dojo-sources/ingrid/css/admin.css"/>
    <script type='text/javascript' src='dwr/engine.js'></script>
    <script type='text/javascript' src='dwr/util.js'></script>
    <script src='dwr/interface/SecurityService.js'></script>

    <script type="text/javascript">
        var passwordChangeId = '<%= request.getParameter("id") %>';

        function updatePassword() {
            var pass1 = document.getElementById("password").value;
            var pass2 = document.getElementById("passwordAgain").value;

            if (pass1 === pass2 && pass1.trim().length > 0) {

                SecurityService.updatePassword(passwordChangeId, pass1, {
                    callback: function (result) {
                        if (result === true) {
                            console.log("Password changed");
                            showSuccess();
                        } else {
                            console.log("passwordChangeId was not found");
                            showError("invalid");
                        }
                    },
                    errorHandler: function(error) {
                        console.error("Could not update password!", error);
                        showError();
                    }
                });

            } else {
                console.error("Passwords empty or do not match");
                showError("match");
            }

        }

        function showError(type) {

            document.getElementById("error").style.display = "none";
            document.getElementById("error-not-matching").style.display = "none";
            document.getElementById("error-invalid").style.display = "none";

            if (type === 'match') {
                document.getElementById("error-not-matching").style.display = "block";
            } else if (type === 'invalid') {
                document.getElementById("error-invalid").style.display = "block";
            } else {
                document.getElementById("error").style.display = "block";
            }
        }

        function showSuccess() {
            document.getElementById("error").style.display = "none";
            document.getElementById("error-not-matching").style.display = "none";
            document.getElementById("error-invalid").style.display = "none";
            document.getElementById("success").style.display = "block";

            setTimeout(function() {
                window.location.href = "login.jsp";
            }, 3000);
        }

    </script>
</head>
<body>
<div id="header">
    <img src="img/logo.png" alt="InGrid Editor">
    <h1><fmt:message key="ui.login.title"/></h1>
</div>

<div class="content-password" style="display: block;">
    <div class="contentBorder">
        <h3>Passwort ändern</h3>
        <div style="padding: 10px;">
            <table style="width: 100%;">
                <tr>
                    <td style="width: 250px;">Neues Password:</td>
                    <td>
                        <input id="password" name="password" type="password" style="width: 200px;">
                    </td>
                </tr>
                <tr>
                    <td>Password Wiederholung:</td>
                    <td>
                        <input id="passwordAgain" name="passwordAgain" type="password" style="width: 200px;">
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <input id="updatePassword" type="submit" onclick="updatePassword()" name="updatePassword" value="Ändern">
                    </td>
                </tr>
                <div id="error-not-matching" class="error" style="display:none;">Passwörter leer oder stimmen nicht überein</div>
                <div id="error-invalid" class="error" style="display:none;">Der Link zum Ändern des Passworts ist abgelaufen (max. 1h) oder nicht gültig</div>
                <div id="error" class="error" style="display:none;">Das Passwort konnte nicht aktualisiert werden</div>
                <div id="success" style="display:none;">Das Passwort wurde erfolgreich geändert. Sie werden zur Login Seite weitergeleitet.</div>
            </table>
        </div>
    </div>
</div>
</body>
</html>
