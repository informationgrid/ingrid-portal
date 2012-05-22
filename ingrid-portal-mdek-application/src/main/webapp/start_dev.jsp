<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% request.getSession(true).setAttribute("userName", request.getParameter("user")); %>
<% request.getSession(true).setAttribute("currLang", request.getParameter("lang") == null ? "de" : request.getParameter("lang")); %>


<fmt:setLocale value="<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>" scope="session" />
<fmt:setBundle basename="messages" scope="session"/>

<html dir="ltr">
    
    <head>
        <title>InGrid Editor</title>
        <link rel="shortcut icon" href="img/iconLogo.gif" type="image/x-icon">
        <link rel="stylesheet" href="css/slick.grid.css" type="text/css" media="screen" charset="utf-8" />
        <script type="text/javascript">
            var userLocale = '<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>';
            var initJumpToNodeId = '<%= request.getParameter("nodeId") == null ? "" : request.getParameter("nodeId") %>';
            var initJumpToNodeType = '<%= request.getParameter("nodeType") == null ? "" : request.getParameter("nodeType")%>';
            var selenium = '<%= request.getParameter("selenium") == null ? false : true %>';
        </script>
        
        <jsp:include page="dojoScripts_dev.jsp" />
        <!-- this css contains mainly references to images which must be held
             separately to avoid problems during optimization                 -->
        <link rel="stylesheet" href="css/imageReferences.css" />
        
        <!--<script type="text/javascript" src="dojo-release-1.6.0/dojo/dojo.js" djConfig="parseOnLoad:false, locale:userLocale"></script>-->
        
        <script type='text/javascript' src='dwr/engine.js'></script>
        <script type='text/javascript' src='dwr/util.js'></script>
        
        <script type="text/javascript">
            // browser check (no IE9 yet)
            if (!(dojo.isChrome || dojo.isIE == 7 || dojo.isIE == 8 || dojo.isFF >= 3.6)) {
                document.location.href = "not_supported_browser.jsp"
            }
        </script>
        
		<script src='dwr/interface/CatalogService.js'></script>
		<script src='dwr/interface/TreeService.js'></script>
		<script src='dwr/interface/ObjectService.js'></script>
		<script src='dwr/interface/AddressService.js'></script>
		<script src='dwr/interface/SecurityService.js'></script>
		<script src='dwr/interface/SNSService.js'></script>
		<script src='dwr/interface/QueryService.js'></script>
		<script src='dwr/interface/HelpService.js'></script>
		<script src='dwr/interface/CTService.js'></script>
		<script src='dwr/interface/HttpService.js'></script>
		<script src='dwr/interface/GetCapabilitiesService.js'></script>
		<script src='dwr/interface/ImportService.js'></script>
		<script src='dwr/interface/ExportService.js'></script>
		<script src='dwr/interface/CatalogManagementService.js'></script>
        <script src='dwr/interface/UtilityService.js'></script>
<!--         <script src='dwr/interface/UserRepoManager.js'></script> -->
	
		<script type="text/javascript" src="js/config.js"></script>
		<script type="text/javascript" src="js/message.js"></script>
		<script type="text/javascript" src="js/utilities.js"></script>
		<script type="text/javascript" src="js/dialog.js"></script>
		<script type="text/javascript" src="js/error_handler.js"></script>
		<script type="text/javascript" src="js/menuEventHandler.js"></script>
		<script type="text/javascript" src="js/eventSubscriber.js"></script>
		
        
        <!--<script type="text/javascript" src="dojo-src/release/dojo/custom/layer.js"></script>-->
        
		<!--<script type="text/javascript" src="js/dojo/dijit/tree/LazyTreeStoreModel.js"></script>-->
		<script type="text/javascript" src="js/layoutCreator.js"></script>
		<script type="text/javascript" src="js/debugFunctions.js"></script>
		<script type="text/javascript" src="js/menu.js"></script>
		<script type="text/javascript" src="js/toolbar.js"></script>

		<!--<script type="text/javascript" src="dojo-src/dojox/grid/DataGrid.js"></script>-->
		<!--<script type="text/javascript" src="js/tree.js"></script>-->
		<script type="text/javascript" src="js/init.js"></script>
		<!--<script type="text/javascript" src="dojo-src/dojox/grid/cells/dijit.js"></script>-->
		<script type="text/javascript" src="js/dojo/dijit/CustomGrid.js"></script>
        <script type="text/javascript" src="js/dojo/dijit/CustomTree.js"></script>
        
        <script type="text/javascript" src="js/rules_required.js"></script>
        <script type="text/javascript" src="js/rules_validation.js"></script>
        <script type="text/javascript" src="js/rules_checker.js"></script>
        
        <!--<script type="text/javascript" src="dojo-src/dijit/Tree.js"></script>
        <script type="text/javascript" src="js/dojo/dijit/CustomDialog.js"></script>-->
        <script type="text/javascript" src="js/tree.js"></script>
        
        <script type="text/javascript" src="js/dojo/dijit/CustomGridRowMover.js"></script>
        <script type="text/javascript" src="js/dojo/dijit/CustomGridRowSelector.js"></script>
        <script type="text/javascript" src="js/dojo/dijit/CustomGridEditors.js"></script>
        <script type="text/javascript" src="js/dojo/dijit/CustomGridFormatters.js"></script>
        
    </head>
    <body class="claro">
		<!-- SPLIT/BORDER CONTAINER START --> 
		<div id="contentContainer" class="contentSection">
			<div id="headerContainer">
                <div id="logoContainer">
                    <div id="logo"><img width="119" height="24" alt="PortalU" src="img/logo.gif"></div>
                    <div id="title"><img width="158" height="24" alt="Metadatenerfassung" src="img/title_erfassung_<%=(String)session.getAttribute("currLang")%>.gif"></div>
                    <div id="languageBox" value="<%=(String)session.getAttribute("currLang")%>"></div>
                    <div id="headMenu">
                    	<span id="logout" style="float:right;"><a href="#dummyAnchor" onClick="window.location.href='closeWindow.jsp';" style="color: #FFF; font-weight: bold;"><fmt:message key='general.close' /></a></span>
                        <span style="float:right; padding:0px 5px 0px 5px;">|</span>
                        <span id="info" style="float:right;"><a href="javascript:void(0);" onclick="javascript:dialog.showPage('Info', 'dialogs/mdek_info_dialog.html', 365, 210, false); return false;" title="Info"><fmt:message key='general.info' /></a></span>
                        <span style="float:right; padding:0px 5px 0px 5px;">|</span>
                        <span id="impressum" style="float:right;"><a href="javascript:void(0);" onclick="javascript:window.open('http://www.portalu.de:80/portal/disclaimer.psml', 'impressum', 'width=966,height=994,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key='general.imprint' />"><fmt:message key='general.imprint' /></a></span>
                        <span style="float:right; padding:0px 5px 0px 5px;">|</span>
                        <span id="help" style="float:right;"><a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?c='+userLocale, 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key='general.help' />"><fmt:message key='general.help' /></a></span>
	                	<span style="float:right; padding:0px 5px 0px 5px;">|</span>
	                	<span id="currentCatalogName" style="float:right;"></span>
                        <span id="currentUserRole" style="float:right; padding: 0 5px;"></span>
	                 	<span id="currentUserName" style="float:right;"></span>
	                </div>
	            </div>
                <div id="menuContainer"></div>
                <div id="menuInfoContainer">
                    <div style="padding-top: 3px; padding-left: 10px; color:#000000;">
	                        <div id="currentPageName">Dashboard</div>
	                </div>
                </div>
            </div>
		</div>
		<div id="loadingZone" style="float:right; margin-top:3px; z-index: 100; visibility:hidden; top: 28px; right: 5px; position: absolute;">
			<img id="imageZone" src="img/ladekreis.gif" style="background-color:#EEEEEE;" />
		</div>
		<div id="blockInputDiv" style="position: absolute; top: 0px; left: 0px; width: 100%; height:100%; z-index: 99; visibility:hidden"></div>
    </body>
</html>
	