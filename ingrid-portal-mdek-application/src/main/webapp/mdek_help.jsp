<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String currLang = (String)session.getAttribute("currLang");%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>


<link rel="StyleSheet" href="css/main.css" type="text/css" />

<style type="text/css">
div.left { float:left; width:15.75em;}
div.right { margin-left:17em; padding:0;}
.float-left {float:left}

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
  	    <div id="logo" class="float-left"><img src="img/logo.gif" width="119" height="24" alt="PortalU" /></div>
  		  <div id="title" class="float-left"><img src="img/title_erfassung_<%=currLang%>.gif" width="158" height="24" alt="Metadatenerfassung" /></div>
  	    <ul>
  	      <li><a href="mdek_help.jsp?hkey=index"><fmt:message key='general.help.index' /></a></li>
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