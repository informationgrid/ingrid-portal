<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String currLang = (String)session.getAttribute("currLang");%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<title><fmt:message key="general.help" /></title>
<link rel="StyleSheet" href="css/main.css" type="text/css" />

<style type="text/css">
div.left { float:left; width:15.75em;}
div.right { margin-left:17em; padding:0; float:none;}
.logo { float: left; padding-right: 10px; }

.head-menu {height:30px; background:#156496; padding:0; margin:0; background-image:url(img/head_bg_small.gif);}
.head-menu ul {float:right;list-style:none; line-height:10px; display:block;padding:8px 20px 0 20px; margin:0;}
.head-menu ul li {display:inline}
.head-menu ul li a { font:bold 10px/11px Verdana, Helvetica, Arial, sans-serif; color:#c2e3fc; text-decoration:none; }
.head-menu ul li a:hover { color:#fff; }
.head-menu ul li.separator { color:#fff; }
.helpContainer {padding: 20px 20px 20px 20px;}
.helpContainer h4 {text-decoration: underline;}

h2 {margin:0; padding:20px 0px 15px 0px; font:bold 12px/16px Verdana, Helvetica, Arial, sans-serif;}

</style>


</head>

<body style="margin:0; padding:0;width:100%; height:100%; overflow:auto;font:normal 11px/16px Verdana, Helvetica, Arial, sans-serif;">
	<div class="head-menu">
  	    <div id="logo" class="logo"><img src="img/logo.png" alt="InGrid-Portal" /></div>
  		  <div id="title"><h2 style="color: white; padding: 5px">UVP Editor</h2></div>
  	    <ul>
  	      <li><a href="mdek_help.jsp?lang='+userLocale+'&hkey=index"><fmt:message key='general.help.index' /></a></li>
  	      <li class="separator">|</li>
  	      <li><a href="javascript:window.close()"><fmt:message key='general.help.close' /></a></li>
  	    </ul>
    </div>
    <div style="clear:both"> </div>

	<div class="yScroll helpContainer">
		<!-- include HelpServlet -->
		<jsp:include page="help/help.html" flush="true"/>
	</div>
</body>
</html>
