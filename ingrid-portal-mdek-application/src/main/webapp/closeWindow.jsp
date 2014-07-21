<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>InGrid-Editor</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type='text/javascript' src='dwr/engine.js'></script>
<script type='text/javascript' src='dwr/util.js'></script>
<script src='dwr/interface/SecurityService.js'></script>
</head>

<%
    request.getSession(true).setAttribute("userName", null);
    request.getSession().invalidate();
%>

<script>
    SecurityService.isPortalConnected(function(response) {
        if (response == true) {
            window.open('', '_self', '');
            window.close();
        } else {
            // redirect
            document.location.href = document.location.href.replace(/closeWindow.jsp/, "login.jsp");
        }
    });
</script>

<body>
</body>
</html>