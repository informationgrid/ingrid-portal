<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- Set the locale to the value of parameter 'lang' and init the message bundle messages.properties -->
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>" scope="session" />
<fmt:setBundle basename="messages" scope="session"/>

<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><fmt:message key="ui.entry.session.expired" /></title>
<link rel="shortcut icon" href="/ingrid-portal/decorations/layout/ingrid-popup/images/favicon.ico" />
  <style type="text/css">
	
	body {
        font-family: Verdana, Helvetica, Arial, sans-serif;
        voice-family: "\"}\"";
        voice-family: inherit;
        padding: 1em;
        background: #fff;
        color: #000;
        white-space: normal;
    	margin: 0;
    	padding: 0;
	}
	
	td,
    th {
		white-space: normal;
    }
    
    th {
		font-size: 12pt;
    }
    
    a:link, a:visited {
        color: blue;
    }
    
    a:hover {
        color: #f30 !important;
    }

  </style>
  <link href="/ingrid-portal/decorations/layout/ingrid-popup/css/styles.css" media="screen, projection" type="text/css" rel="stylesheet">
</head>
<body>
<div class="layout-tigris">
<div id="identity" class="motiv_suche">
  <!-- LOGO -->
  <div id="logo">
    <a href="/ingrid-portal/portal/default-page.psml"><img src="/ingrid-portal/decorations/layout/ingrid/images/logo.gif" width="168" height="60" alt="Logo PortalU" /></a>
  </div>
</div>

<!-- CONTENT BLOCK -->
<div id="container">
	<h1><fmt:message key="ui.entry.session.expired" /></h1>
	<p><fmt:message key="ui.entry.session.expired.text" /></p>
</div>
<div class="clearer"></div>
<div id="footermarginalcontainer">
  <div id="footermarginal">
    <!-- portlets go here -->
  &nbsp;
  </div>
</div>
<div class="clearer"></div>

<!-- footer block -->
<div id="footercontainer">
  <div id="footercontent">
    <!-- portlets go here -->
	<a href="/ingrid-portal/portal/disclaimer.psml" title="<fmt:message key="general.imprint" />"><fmt:message key="general.imprint" /></a>
	<a href="/ingrid-portal/portal/privacy.psml" title="<fmt:message key="general.privacy.policy" />"><fmt:message key="general.privacy.policy" /></a>
	<a href="mailto:webmaster@portalu.de" title="<fmt:message key="general.webmaster" />"><fmt:message key="general.webmaster" /></a>
	<br />
    Copyright &copy; Koordinierungsstelle PortalU im Nds. Ministerium f&uuml;r Umwelt und Klimaschutz<br />

    Alle Rechte vorbehalten<br /><br />
  </div>
</div>

</div>
</body>
</html>