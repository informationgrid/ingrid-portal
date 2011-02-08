<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- Set the locale to the value of parameter 'lang' and init the message bundle messages.properties -->
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>" scope="session" />
<fmt:setBundle basename="messages" scope="session"/>

<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="ui.entry.title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
    // browser check (only IE7/8, FF2/3)
	var browser = navigator.appName;
	var version = navigator.appVersion;
	if (!(version.indexOf("MSIE 7") != -1 || version.indexOf("MSIE 8") != -1 || (browser=="Netscape" && version.indexOf("5.0") != -1 && version.indexOf("Chrome") == -1))) {
		document.location.href = "not_supported_browser.jsp"
	}
</script>

<script src='/ingrid-portal-mdek-application/js/error_handler.js'></script>

<script src='/ingrid-portal-mdek-application/dwr/interface/AddressService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/BackendService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/CatalogService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/HelpService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/HttpService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/ObjectService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/QueryService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/SecurityService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/TreeService.js'></script>

<script src='/ingrid-portal-mdek-application/dwr/interface/CTService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/GetCapabilitiesService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/SNSService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/UtilityService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/VersionInformation.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>


<script type="text/javascript">
	var userLocale = '<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>';
	var initJumpToNodeId = '<%= request.getParameter("nodeId") == null ? "" : request.getParameter("nodeId") %>';
	var initJumpToNodeType = '<%= request.getParameter("nodeType") == null ? "" : request.getParameter("nodeType")%>';
	var isDebug = <%= "true".equals(request.getParameter("debug")) %>;
	var selenium = '<%= request.getParameter("selenium") == null ? false : true %>';
		
	var djConfig = {
		locale: userLocale,
		isDebug: isDebug, // use with care, may lead to unexpected errors!
		debugAtAllCosts: false,
		debugContainerId: "dojoDebugOutput"
	};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>

<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>
<script type="text/javascript" src="js/treeEventHandler.js"></script>
<script type="text/javascript" src="js/menuEventHandler.js"></script>
<script type="text/javascript" src="js/udkDataProxy.js"></script>
<script type="text/javascript" src="js/diff.js"></script>
<script type="text/javascript" src="js/utilities.js"></script>

<script type="text/javascript">
dojo.require("ingrid.widget.FormErfassungObjektContent");
dojo.require("ingrid.widget.FormErfassungAdresseContent");
dojo.require("ingrid.widget.Form");
</script>

<script type="text/javascript" src="js/dialog.js"></script>
<script type="text/javascript" src="js/erfassung.js"></script>
<script type="text/javascript" src="js/common.js"></script>
<script type="text/javascript" src="js/init.js"></script>
<script type="text/javascript" src="js/rules_checker.js"></script>
<script type="text/javascript" src="js/rules_required.js"></script>
<script type="text/javascript" src="js/rules_validation.js"></script>

<script type="text/javascript">

// click handler for main menus
var menus = [{menu:"page1", submenus:[]}, 
			 {menu:"page2", submenus:["page2", "page2Sub2", "page2Sub3"]},
			 {menu:"page3", submenus:["page3", "page3Sub2"]},
			 {menu:"page4", submenus:[]}];
var currentMenu = null;
var currentSubMenu = new Array();

function clickMenu(menuName, submenuName) {
	// Activate the menu by removing the following lines
	// --- DEACTIVATE BEGIN ---
//	dojo.debug("Main menu is deactivated. Activate it by uncommenting the corresponding lines in mdek_entry.jsp - function clickMenu()");
//	return;
	// --- DEACTIVATE END ---

	for(var i=0; i<menus.length; i++) {
		var menu = menus[i].menu;
		var submenus = menus[i].submenus;
		if (menu != menuName) {
			dojo.widget.byId(menu).hide();
			dojo.html.removeClass(dojo.byId(menu+"Menu"), "current");
			dojo.byId(menu+"Subnavi").style.display="none";
			for (var j=0; j<submenus.length; j++) {
				dojo.widget.byId(submenus[j]).hide();
			}
		} else {
			dojo.widget.byId(menu).show();
			dojo.html.addClass(dojo.byId(menu+"Menu"), "current");
			dojo.byId(menu+"Subnavi").style.display="block";
			
			if (!submenuName)  {
				submenuName = currentSubMenu[menu];
			}
			for (var j=0; j<submenus.length; j++) {
				if (!submenuName)  {
					submenuName = submenus[j];
				}
				if (submenus[j] != submenuName) {
					dojo.widget.byId(submenus[j]).hide();
					dojo.html.removeClass(dojo.byId(menu+"Subnavi"+(j+1)), "current");
				} else {
					dojo.widget.byId(submenus[j]).show();
					dojo.html.addClass(dojo.byId(menu+"Subnavi"+(j+1)), "current");
					currentSubMenu[menu] = submenus[j];
				}
			}
		}
	}
}
  


  
function hideSplash(){
        dojo.byId("splash").style.display="none";
        dojo.byId("layout").style.top="0px";
}


</script>


<link rel="StyleSheet" href="css/main.css" type="text/css" />
<link rel="StyleSheet" href="css/recherche.css" type="text/css" />
<link rel="StyleSheet" href="css/erfassung.css" type="text/css" />

<style type="text/css">
	@import url(css/scrolling_table.css);
	
	/** correct IE bug where button not floating correctly **/
	#uiElement1410 span {
	  float: left !important;
	}
</style>

</head>

<body>

<!-- use this for print div content, see commons.js -> printDivContent() -->
<iframe id="printFrame" name="printFrame" style="position:absolute; left:-1000px"></iframe>

<div id="splash" style="position: absolute; top: 0px; width: 100%;z-index: 100; height:2000px;background-color:#FFFFFF">
<div style="position: relative; width: 100%;z-index: 100;top:200px">
   <div align="center" style="line-height:16px">
        <div style="width:550px; height:20px; background-color:#156496">&nbsp;</div>
        <div style="width:550px; background-color:#e6f0f5; font-family:Verdana,Helvetica,Arial,sans-serif; font-size:12px; padding: 20px 0px 20px 0px; margin:0px">
          <p style="font-size:24px; font-weight:bold; line-height:16px; margin:16px">InGrid-Editor</p>
<!--           <p style="font-size:16px; font-weight:bold; line-height:16px; margin:16px">Version 1.0.0</p>  -->
          <p style="font-size:12px; font-weight:normal; line-height:16px; margin:16px"><script>document.write(message.get("ui.entry.loadingMessage"))</script></p>
        </div>
   </div>
</div>
</div>


<div id="blockInputDiv" style="position: absolute; top: 0px; left: 0px; width: 100%; height:100%; z-index: 99; visibility:hidden"></div> 


<div dojoType="LayoutContainer" id="layout" class="layout" layoutChildPriority="top-bottom">

  <div dojoType="FloatingPane" title="Dojo Debug Console" constrainToContainer="true" resizable="true" id="dojoDebugConsole"
    displayMinimizeAction="true" displayMaximizeAction="true" displayCloseAction="true"	style="width:300px; height:500px; left:1000px; top:0; visibility:hidden;">
    <div dojoType="ContentPane" id="dojoDebugOutput"></div>
  </div>

  <!-- NAVIGATION START -->
	<div dojoType="ContentPane" id="headSection" layoutAlign="top">
  	<div dojoType="ContentPane" style="height:70px; background-image:url(img/head_bg.gif);">
  	  <div id="logo"><img src="img/logo.gif" width="119" height="24" alt="PortalU" /></div>
  	  <div id="title"><img src="img/title_erfassung.gif" width="158" height="24" alt="Metadatenerfassung" /></div>
  	  <div id="metaNavi">
  	    <ul>
  	      <li><span id="currentUserName">Benutzername</span> · <span id="currentUserRole">Rollenbezeichnung</span> · <span id="currentCatalogName">Katalogname</span></li>
  	      <li class="seperator">|</li>
  	      <li><a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />"><script>document.write(message.get("menu.general.help"))</script></a></li>
  	      <li class="seperator">|</li>
  	      <li><a href="javascript:void(0);" onclick="javascript:window.open('http://www.portalu.de:80/ingrid-portal/portal/disclaimer.psml', 'impressum', 'width=966,height=994,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.imprint" />"><script>document.write(message.get("menu.general.disclaimer"))</script></a></li>
  	      <li class="seperator">|</li>
  	      <li><a href="javascript:void(0);" onclick="javascript:menuEventHandler.switchLanguage();"><script>document.write(UtilLanguage.getNextLanguageName())</script></a></li>
  	      <li class="seperator">|</li>
  	      <li><a href="javascript:void(0);" onclick="javascript:dialog.showPage('Info', 'mdek_info_dialog.html', 365, 210, false); return false;" title="Info"><script>document.write(message.get("menu.general.info"))</script></a></li>
  	      <li class="seperator">|</li>
  	      <li><a href="#dummyAnchor" onClick="window.location.href='closeWindow.jsp';" title="<fmt:message key="dialog.links.close" />"><strong><script>document.write(message.get("menu.general.closeApplication"))</script></strong></a></li>
  	    </ul>
  	  </div>
  	  <div id="navi">
  	    <ul>
  	      <li><a id="page1Menu" onClick="clickMenu('page1')" href="javascript:void(0);" class="current" title="<fmt:message key="dialog.menu.hierachy" />"><script>document.write(message.get("menu.main.hierarchyAcquisition"))</script></a></li>
  	      <li><a id="page2Menu" onClick="clickMenu('page2')" href="javascript:void(0);" title="<fmt:message key="dialog.menu.research" />"><script>document.write(message.get("menu.main.research"))</script></a></li>
  	      <li><a id="page3Menu" onClick="clickMenu('page3')" href="javascript:void(0);" title="<fmt:message key="dialog.menu.qualityassurance" />"><script>document.write(message.get("menu.main.qualityAssurance"))</script></a></li>
  	      <li><a id="page4Menu" onClick="clickMenu('page4')" href="javascript:void(0);" title="<fmt:message key="dialog.menu.statistics" />"><script>document.write(message.get("menu.main.statistics"))</script></a></li>
  	    </ul>
  	  </div>
  	  
	  <div id="page1Subnavi" class="subnavi" style="display:none"></div>
	  <div id="page2Subnavi" class="subnavi" style="display:none">
  	    <ul>
  	      <li><a id="page2Subnavi1" onClick="clickMenu('page2', 'page2')" href="javascript:void(0);" class="current" title="<fmt:message key="dialog.menu.search" />"><script>document.write(message.get("menu.main.research.search"))</script></a></li>
  	      <li><a id="page2Subnavi2" onClick="clickMenu('page2', 'page2Sub2')" href="javascript:void(0);" title="<fmt:message key="dialog.menu.thesaurus" />"><script>document.write(message.get("menu.main.research.thesaurusNavigator"))</script></a></li>
  	      <li><a id="page2Subnavi3" onClick="clickMenu('page2', 'page2Sub3')" href="javascript:void(0);" title="<fmt:message key="dialog.menu.databasesearch" />"><script>document.write(message.get("menu.main.research.databaseSearch"))</script></a></li>
  	    </ul>
  	  </div>
	  <div id="page3Subnavi" class="subnavi" style="display:none">
  	    <ul>
   	      <li><a id="page3Subnavi1" onClick="clickMenu('page3', 'page3')" href="javascript:void(0);" class="current" title="<fmt:message key="dialog.menu.editor" />"><script>document.write(message.get("menu.main.qualityAssurance.editor"))</script></a></li>
  	      <li><a id="page3Subnavi2" onClick="clickMenu('page3', 'page3Sub2')" href="javascript:void(0);" title="<fmt:message key="dialog.menu.qualityassurance" />"><script>document.write(message.get("menu.main.qualityAssurance.qa"))</script></a></li>
  	    </ul>
  	  </div>
	  <div id="page4Subnavi" class="subnavi" style="display:none"></div>
    </div>
  
  </div>
  <!-- NAVIGATION END -->
  
  <div widgetId="page1" dojoType="LayoutContainer" layoutAlign="client">
    <div dojoType="ContentPane" style="height:28px; background-image:url(img/toolbar_bg.gif);" layoutAlign="top">
      <div id="toolbarLeft">
      	<div dojoType="ToolbarContainer" templateCssPath="js/dojo/widget/templates/Toolbar.css">
          <div dojoType="ingrid:Toolbar" widgetId="leftToolbar" id="leftToolbar"><!-- add items in addOnLoad //--></div>
        </div>
      </div>
      
      <div id="toolbarRight">
      	<div dojoType="ToolbarContainer" templateCssPath="js/dojo/widget/templates/Toolbar.css">
          <div dojoType="ingrid:Toolbar" widgetId="rightToolbar" id="rightToolbar"><!-- add items in addOnLoad //--></div>
        </div>
      </div>

	 <!-- The loading zone height and width can be set to 100% to block all inputs while loading. We may want to do this in the release version. -->
	<div id="loadingZone" style="float:right; margin-top:3px; z-index: 100; visibility:hidden">
		<img id="imageZone" src="img/ladekreis.gif" style="background-color:#EEEEEE;" />
	</div> 

    </div>
  <!-- SPLIT CONTAINER START -->
  <div dojoType="ingrid:SplitContainer" id="contentSection" class="contentSection" orientation="horizontal" sizerWidth="15" layoutAlign="client" templateCssPath="js/dojo/widget/templates/SplitContainer.css">
    <!-- LEFT CONTENT PANE START -->
  	<div dojoType="ContentPane" id="treeContainer" class="treeContainer">
      <!-- tree components -->
      <div dojoType="ingrid:TreeController" widgetId="treeController" RpcUrl="server/treelistener.php"></div>
      <div dojoType="ingrid:TreeListener" widgetId="treeListener"></div>	
      <div dojoType="ingrid:TreeDocIcons" widgetId="treeDocIcons"></div>	
      <div dojoType="ingrid:TreeDecorator" listener="treeListener"></div>
      
      <!-- context menus -->
      <div dojoType="ingrid:TreeContextMenu" toggle="plain" contextMenuForWindow="false" widgetId="contextMenu1"></div>
      <div dojoType="ingrid:TreeContextMenu" toggle="plain" contextMenuForWindow="false" widgetId="contextMenu2"></div>

      <!-- tree -->
      <div dojoType="ingrid:Tree" listeners="treeController;treeListener;contextMenu1;contextMenu2;treeDocIcons" widgetId="tree">
      </div>
  	</div>
    <!-- LEFT CONTENT PANE END -->

    <!-- RIGHT CONTENT PANE -->
    <div dojoType="ContentPane" id="contentContainer" class="contentContainer" sizeMin="734" widgetId="content" style="overflow:hidden;">
      <!-- START CONTENT OBJECT -->
	  <div id="contentObject" style="display:block">
	    <div dojoType="ContentPane" widgetId="headerFrame" id="sectionTopObject" class="sectionTop">
	        <table cellspacing="0">
	          <tbody>
	            <tr>
	              <td id="objectNameLabel" class="label required"><label for="objectName" onclick="javascript:dialog.showContextHelp(arguments[0], 3000)"><script>document.write(message.get("ui.obj.header.objectName"))</script>*</label></td>
	              <td colspan="2"><input type="text" maxlength="255" id="objectName" required="true" name="objectName" class="w550" dojoType="ingrid:ValidationTextBox" /></td></tr>
	            <tr>
	              <td id="objectClassLabel" class="label required col1"><label for="objectClass" onclick="javascript:dialog.showContextHelp(arguments[0], 1020)"><script>document.write(message.get("ui.obj.header.objectClass"))</script>*</label></td>
	              <td class="col2">
	                <!-- autoComplete=false because of 'weird' SelectBox behaviour (Click on Box Arrow adds wrong text to the selection) -->
	                <select dojoType="ingrid:Select" autoComplete="false" required="true" style="width:386px;" id="objectClass">
	                	<option value="Class0"><fmt:message key="dialog.research.ext.obj.class0" /></option>
	                	<option value="Class1"><fmt:message key="dialog.research.ext.obj.class1" /></option>
	                	<option value="Class2"><fmt:message key="dialog.research.ext.obj.class2" /></option>
	                	<option value="Class3"><fmt:message key="dialog.research.ext.obj.class3" /></option>
	                	<option value="Class4"><fmt:message key="dialog.research.ext.obj.class4" /></option>
	                	<option value="Class5"><fmt:message key="dialog.research.ext.obj.class5" /></option>
	                	<option value="Class6"><fmt:message key="dialog.research.ext.obj.class6" /></option>
	                </select>
 	              </td>
	              <td class="col3"><img id="permissionObjLock" src="img/lock.gif" width="9" height="14" alt="gesperrt" /></td>
	            </tr>
	            <tr>
	              <td id="objectOwnerLabel" class="label required"><label for="objectOwner" onclick="javascript:dialog.showContextHelp(arguments[0], 1030)"><script>document.write(message.get("ui.obj.header.responsibleUser"))</script>*</label></td>
	              <td><input dojoType="ingrid:Select" autoComplete="false" required="true" style="width:386px;" id="objectOwner" /></td>
	              <td class="note"><strong><script>document.write(message.get("ui.obj.header.workState"))</script>:</strong> <span id="workState"></span></td>
	            </tr>
	            <tr>
	              <td class="note" colspan="3"><strong><script>document.write(message.get("ui.obj.header.creationTime"))</script>:</strong> <span id="creationTime">26.06.1998</span> | <strong><script>document.write(message.get("ui.obj.header.modificationTime"))</script>:</strong> <span id="modificationTime">27.09.2000</span> | <strong><script>document.write(message.get("ui.obj.header.modUser"))</script>:</strong> <span id="lastEditor">---</span></td>
	            </tr>
	          </tbody>
	        </table>
	    </div>

	      <!-- FORM -->
	      <div dojoType="ContentPane" widgetId="contentFrameObject" id="contentFrameObject" class="sectionBottom" style="overflow:auto;">
	        <a name="sectionBottomContent"></a>

	        <form dojoType="ingrid:FormErfassungObjektContent" widgetId="contentFormObject" selectedClass="Class0" method="get" id="contentFormObject" action="">

	        <div dojoType="ContentPane" id="contentFrameBodyObject" class="sectionBottomContent">

	          <!-- ALLGEMEINES //-->
	          <div id="general" class="contentBlock firstBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('general');" title="<fmt:message key="general.open.required.field" />"><img id="generalRequiredToggle" src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7011)"><script>document.write(message.get("ui.obj.general.title"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="generalContent" class="content">

	              <div class="inputContainer notRequired">
	                <span id="uiElement5000" type="optional">
	                	<span class="label"><label for="generalShortDesc" onclick="javascript:dialog.showContextHelp(arguments[0], 5000)"><script>document.write(message.get("ui.obj.general.shortDescription"))</script></label></span>
	                	<span class="input"><input type="text" maxlength="255" id="generalShortDesc" name="generalShortDesc" class="w668" dojoType="ingrid:ValidationTextBox" /></span>
	                </span>
	          	  </div>

	              <div class="inputContainer">
	                <span id="uiElement1010" type="required">
		                <span id="generalDescLabel" class="label required"><label for="generalDesc" onclick="javascript:dialog.showContextHelp(arguments[0], 1010)"><script>document.write(message.get("ui.obj.general.description"))</script>*</label></span>
    	           		<span class="input"><input type="text" mode="textarea" id="generalDesc" name="generalDesc" class="w668 h154" dojoType="ingrid:ValidationTextbox" /></span> 
	                </span>
	          	  </div>

	              <div class="inputContainer">
					<span id="uiElement1000" type="required">
		                <span id="generalAddressTableLabel" class="label required"><label for="generalAddressTable" onclick="javascript:dialog.showContextHelp(arguments[0], 1000)"><script>document.write(message.get("ui.obj.general.addressTable.title"))</script>*</label></span>
		                <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="generalAddressTableLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('general.addAddress'), 'mdek_address_dialog.jsp', 755, 580, true);" title="<fmt:message key="dialog.popup.addressTable.link" /> [Popup]"><script>document.write(message.get("ui.obj.general.addressTable.link"))</script></a></span>
		                <div id="generalAddressTable" class="tableContainer headHiddenRows4 full">
		                    <div class="cellEditors" id="generalAddressEditors">
		                      <div dojoType="ingrid:Combobox" toggle="plain" style="width:120px;" listId="505" id="generalAddressCombobox"></div>
		                    </div>
		            	    <table id="generalAddress" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="nameOfRelation" dataType="String" editor="generalAddressCombobox"></th>
		                  			<th nosort="true" field="icon" dataType="String"></th>
		                  			<th nosort="true" field="linkLabel" dataType="String">Namen</th>
		            		      </tr>
		            	      </thead>
							  <colgroup>
							    <col width="120">
							    <col width="23">
							    <col width="520">
							  </colgroup>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
					</span>
	          	  </div>

                    <div id="thesaurusInspireContainer" class="inputContainer noSpaceBelow">
                      <span id="uiElement5064" type="required">
                          <span id="thesaurusInspireLabel" class="label required"><label for="thesaurusInspire" onclick="javascript:dialog.showContextHelp(arguments[0], 5064)"><script>document.write(message.get("ui.obj.thesaurus.terms.inspire"))</script>*</label></span>
                          <div class="tableContainer headHiddenRows4 full">
                            <div class="cellEditors" id="thesaurusInspireEditors">
                              <div dojoType="ingrid:Select" toggle="plain" autoComplete="false" style="width:600px;" listId="6100" id="thesaurusInspireCombobox"></div>
                            </div>
                            <table id="thesaurusInspire" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                        <th nosort="true" field="title" dataType="String" editor="thesaurusInspireCombobox">Inspire Themen</th>
                                  </tr>
                              </thead>
                              <tbody>
                              </tbody>
                            </table>
                          </div>
                        </span>
                      </div>

	            </div>
	          </div>

	          <!-- FACHBEZUG CLASS 1 - GEO-INFORMATION/KARTE //-->
	          <div id="refClass1" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('refClass1');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7000)"><script>document.write(message.get("ui.obj.relevance"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="ref1Content" class="content">

	              <div class="inputContainer required">
                	<span id="uiElement3520" type="required">
		                <span id="ref1BasisTabContainerLabel" class="label required"><label for="ref1BasisTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3520)"><script>document.write(message.get("ui.obj.type1.technicalBasisTable.title"))</script>*</label></span>
		                <span id="ref1BasisTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddBasisLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('ui.obj.links.linksTo.link'), 'mdek_links_dialog.jsp', 1010, 680, true, {filter: 3520});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><script>document.write(message.get("ui.obj.type1.technicalBasisTable.link"))</script></a></span>
		              	<div id="ref1BasisTabContainer" dojoType="ingrid:TabContainer" class="h108" selectedChild="ref1BasisTab1">
							<script>document.write("<div id='ref1BasisTab1' dojoType='ContentPane' label='"+message.get("ui.obj.type1.technicalBasisTable.tab.text")+"'>")</script>
		                    <span class="input">
		               			<input type="text" mode="textarea" id="ref1BasisText" name="ref1BasisText" class="w668 h083" dojoType="ingrid:ValidationTextbox" />
		               		</span> 
		              		</div>
							<script>document.write("<div id='ref1BasisTab2' dojoType='ContentPane' label='"+message.get("ui.obj.type1.technicalBasisTable.tab.links")+"'>")</script>
		                    <div class="tableContainer headHiddenRows4 full">
		                	    <table id="ref1BasisLink" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort">
		                	      <thead>
		              		      <tr>
		                    			<th nosort="true" field="icon" dataType="String"></th>
		                    			<th nosort="true" field="linkLabel" dataType="String">Objekte</th>
		              		      </tr>
		                	      </thead>
								  <colgroup>
								    <col width="23">
								    <col width="297">
								  </colgroup>
		                	      <tbody>
		                	      </tbody>
		                	    </table>
		                    </div>
		              		</div>
		              	</div>
					</span>
	          	  </div>

	              <div class="inputContainer required">
					<span id="uiElementN021" type="required">
						<span id="ref1ObjectIdentifierLabel" class="label required"><label for="ref1ObjectIdentifier" onclick="javascript:dialog.showContextHelp(arguments[0], 10021)"><script>document.write(message.get("ui.obj.type1.identifier"))</script>*</label></span>
	                	<span class="input"><input type="text" maxlength="255" id="ref1ObjectIdentifier" class="w667" dojoType="ingrid:ValidationTextBox" /></span>
					</span>
				  </div>

	              <div class="inputContainer required">
	                <div class="half left">
	                  <span id="uiElement5061" type="required">
		                  <span id="ref1DataSetLabel" class="label required"><label for="ref1DataSet" onclick="javascript:dialog.showContextHelp(arguments[0], 5061)"><script>document.write(message.get("ui.obj.type1.dataset"))</script>*</label></span>
		                  <span class="input spaceBelow"><input dojoType="ingrid:Select" style="width:302px;" listId="525" id="ref1DataSet" /></span>
	            	  </span>
	                  <span id="uiElement3565" type="optional">
		                  <span class="label"><label for="ref1Coverage" onclick="javascript:dialog.showContextHelp(arguments[0], 3565)"><script>document.write(message.get("ui.obj.type1.coverage"))</script></label></span>
		                  <span class="input"><input type="text" id="ref1Coverage" min="0" max="100" name="ref1Coverage" class="w038" decimal="," dojoType="ingrid:RealNumberTextbox" /> %</span>
	            	  </span>
	            	</div>

					<div class="half">
						<span id="uiElement5062" type="optional">
							<span class="label"><label for="ref1Representation" onclick="javascript:dialog.showContextHelp(arguments[0], 5062)"><script>document.write(message.get("ui.obj.type1.digitalRepresentation"))</script></label></span>
							<div class="tableContainer headHiddenRows4 half">
								<div class="cellEditors" id="ref1RepresentationEditors">
									<div dojoType="ingrid:Select" toggle="plain" style="width:260px;" listId="526" id="ref1RepresentationCombobox"></div>
								</div>
								<table id="ref1Representation" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
									<thead>
										<tr>
											<th nosort="true" field="title" dataType="String" editor="ref1RepresentationCombobox">Digitale Repräsentation</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</span>
					</div>
				<div class="fill"></div>
	            </div>

				<div id="ref1VFormat" class="inputContainer notRequired h154">
					<span id="uiElementN005" type="optional">
						<span id="ref1VFormatLabel" class="label"><label for="ref1VFormat" onclick="javascript:dialog.showContextHelp(arguments[0], 7014)"><script>document.write(message.get("ui.obj.type1.vectorFormat.title"))</script></label></span>
						<div id="ref1VFormat" class="outlined h110">
							<div class="thirdInside left">
	                			<span id="uiElement5063" type="optional">
			                    	<span class="label"><label for="ref1VFormatTopology" onclick="javascript:dialog.showContextHelp(arguments[0], 5063)"><script>document.write(message.get("ui.obj.type1.vectorFormat.topology"))</script></label></span>
			                    	<span class="input"><input dojoType="ingrid:Select" style="width:129px;" listId="528" id="ref1VFormatTopology" /></span>
			                	</span>
		                  	</div>
	
							<div class="thirdInside2">
	                			<span id="uiElementN001" type="optional">
			                    	<span class="label hidden"><label for="ref1VFormatDetails" onclick="javascript:dialog.showContextHelp(arguments[0], 10001)">weitere Angaben</label></span>
			                    	<div class="tableContainer rows4 thirdInside2">
			                      	<div class="cellEditors" id="ref1VFormatDetailsEditors">
				                        <div dojoType="ingrid:Select" toggle="plain" style="width:100px;" listId="515" id="geometryTypeEditor"></div>
				                        <div dojoType="IntegerTextbox" min="0" max="2147483647" maxlength="10" widgetId="elementNumberEditor"></div>
				                      </div>
			    	            	    <table id="ref1VFormatDetails" dojoType="ingrid:FilteringTable" minRows="4" headClass="fixedHeader" cellspacing="0" class="filteringTable nosort interactive">
			        	        	      <thead>
			            	    		      <tr>
			                	      			<th nosort="true" field="geometryType" dataType="String" width="120" editor="geometryTypeEditor"><script>document.write(message.get("ui.obj.type1.vectorFormat.detailsTable.header.geoType"))</script></th>
			                    	  			<th nosort="true" field="numElements" dataType="String" width="200" editor="elementNumberEditor"><script>document.write(message.get("ui.obj.type1.vectorFormat.detailsTable.header.elementCount"))</script></th>
			                			      </tr>
				                	      </thead>
				                	      <tbody>
				                	      </tbody>
			    	            	    </table>
			        	            </div>
			            	  	</span>
		                  	</div>
						</div>
		                <div class="fill"></div>
					</span>
				</div>

	              <div class="inputContainer notRequired h130">
                	<span id="uiElementN002" type="optional">
		                <span class="label"><label for="ref1Scale" onclick="javascript:dialog.showContextHelp(arguments[0], 3525)"><script>document.write(message.get("ui.obj.type1.scaleTable.title"))</script></label></span>
		                <div class="tableContainer rows4 full">
		                  <div class="cellEditors" id="ref1ScaleEditors">
		                    <div dojoType="IntegerTextbox" min="0" max="2147483647" maxlength="10" widgetId="ref1ScaleScale"></div>
		                    <div dojoType="ingrid:RealNumberTextbox" decimal="," widgetId="ref1ScaleGroundResolution"></div>
		                    <div dojoType="ingrid:RealNumberTextbox" decimal="," widgetId="ref1ScaleScanResolution"></div>
		                  </div>
		            	    <table id="ref1Scale" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort interactive">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="scale" dataType="String" width="105" editor="ref1ScaleScale"><script>document.write(message.get("ui.obj.type1.scaleTable.header.scale"))</script></th>
		                  			<th nosort="true" field="groundResolution" dataType="String" width="285" editor="ref1ScaleGroundResolution"><script>document.write(message.get("ui.obj.type1.scaleTable.header.groundResolution"))</script></th>
		                  			<th nosort="true" field="scanResolution" dataType="String" width="285" editor="ref1ScaleScanResolution"><script>document.write(message.get("ui.obj.type1.scaleTable.header.scanResolution"))</script></th>
		            		      </tr>
		            	      </thead>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
					</span>
	          	  </div>

	              <div class="inputContainer notRequired">
                	<div class="half left">
						<span id="uiElement5069" type="optional">
		                  <span class="label"><label for="ref1AltAccuracy" onclick="javascript:dialog.showContextHelp(arguments[0], 5069)"><script>document.write(message.get("ui.obj.type1.sizeAccuracy"))</script></label></span>
		                  <span class="input"><input type="text" id="ref1AltAccuracy" name="ref1AltAccuracy" class="w320" decimal="," dojoType="ingrid:RealNumberTextbox" /></span>
	                  	</span>
	               	</div>

	                <div class="half">
                	  <span id="uiElement3530" type="optional">
		                  <span class="label"><label for="ref1PosAccuracy" onclick="javascript:dialog.showContextHelp(arguments[0], 3530)"><script>document.write(message.get("ui.obj.type1.posAccuracy"))</script></label></span>
		                  <span class="input"><input type="text" id="ref1PosAccuracy" name="ref1PosAccuracy" class="w320" decimal="," dojoType="ingrid:RealNumberTextbox" /></span>
		              </span>
	                </div>
	                <div class="fill"></div>
	          	  </div>

	              <div class="inputContainer notRequired h126">
                	  <span id="uiElement3555" type="optional">
		                <span class="label"><label for="ref1SymbolsTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3555)"><script>document.write(message.get("ui.obj.type1.symbolCatTable.title"))</script></label></span>
		                <span id="ref1SymbolsTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddSymbolsLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('ui.obj.links.linksTo.link'), 'mdek_links_dialog.jsp', 1010, 680, true, {filter: 3555});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><script>document.write(message.get("ui.obj.type1.symbolCatTable.link"))</script></a></span>
		              	<div id="ref1SymbolsTabContainer" dojoType="ingrid:TabContainer" class="h108" selectedChild="ref1SymbolsTab1">
							<script>document.write("<div id='ref1SymbolsTab1' dojoType='ContentPane' label='"+message.get("ui.obj.type1.symbolCatTable.tab.text")+"'>")</script>
		               		<!-- Need to use document.write here since the titel 'label' is set directly in the div
		               			<div id="ref1SymbolsTab1" dojoType="ContentPane" label="Text">
	 						-->
		                    <div class="tableContainer rows3 full">
		                      <div class="cellEditors" id="ref1SymbolsTextEditors">
		                        <div dojoType="ingrid:ComboBox" toggle="plain" maxlength="80" style="width:400px;" listId="3555" id="ref1SymbolsTitleCombobox"></div>
		                        <div dojoType="ingrid:DropdownDatePicker" displayFormat="dd.MM.yyyy" toggle="plain" widgetId="ref1SymbolsDateDatePicker"></div>
		                        <div dojoType="ingrid:ValidationTextbox" maxlength="80" widgetId="ref1SymbolsVersion"></div>
		                      </div>
		                	    <table id="ref1SymbolsText" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y" minRows="3" cellspacing="0" class="filteringTable interactive nosort">
		                	      <thead>
		                		      <tr>
		                      			<th nosort="true" field="title" dataType="String" width="435" editor="ref1SymbolsTitleCombobox"><script>document.write(message.get("ui.obj.type1.symbolCatTable.header.title"))</script></th>
		                      			<th nosort="true" field="date" dataType="Date" width="120" editor="ref1SymbolsDateDatePicker"><script>document.write(message.get("ui.obj.type1.symbolCatTable.header.date"))</script></th>
		                      			<th nosort="true" field="version" dataType="String" width="120" editor="ref1SymbolsVersion"><script>document.write(message.get("ui.obj.type1.symbolCatTable.header.version"))</script></th>
		                		      </tr>
		                	      </thead>
		                	      <tbody>
		                	      </tbody>
		                	    </table>
		                	  </div>
		                	</div>
							<script>document.write("<div id='ref1SymbolsTab2' dojoType='ContentPane' label='"+message.get("ui.obj.type1.symbolCatTable.tab.links")+"'>")</script>
		                    <table id="ref1SymbolsLink" width="676" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort">
		              	      <thead>
		              		      <tr>
		                    			<th nosort="true" field="icon" dataType="String"></th>
		                    			<th nosort="true" field="linkLabel" dataType="String">Objekte</th>
		              		      </tr>
		              	      </thead>
							  <colgroup>
							    <col width="23">
							    <col width="297">
							  </colgroup>
		              	      <tbody>
		              	      </tbody>
		              	    </table>
		              		</div>
		                </div>
					</span>
	          	  </div>

	              <div class="inputContainer notRequired h126">
					<span id="uiElement3535" type="optional">
		                <span class="label"><label for="ref1KeysTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3535)"><script>document.write(message.get("ui.obj.type1.keyCatTable.title"))</script></label></span>
		                <span id="ref1KeysTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddKeysLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('ui.obj.links.linksTo.link'), 'mdek_links_dialog.jsp', 1010, 680, true, {filter: 3535});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><script>document.write(message.get("ui.obj.type1.keyCatTable.link"))</script></a></span>
		              	<div id="ref1KeysTabContainer" dojoType="ingrid:TabContainer" class="h108" selectedChild="ref1KeysTab1">
							<script>document.write("<div id='ref1KeysTab1' dojoType='ContentPane' label='"+message.get("ui.obj.type1.keyCatTable.tab.text")+"'>")</script>
		                    <div class="tableContainer rows3 full">
		                      <div class="cellEditors" id="ref1KeysTextEditors">
		                        <div dojoType="ingrid:ComboBox" toggle="plain" maxlength="80" style="width:400px;" listId="3535" id="ref1KeysTitleCombobox"></div>
		                        <div dojoType="ingrid:DropdownDatePicker" displayFormat="dd.MM.yyyy" toggle="plain" widgetId="ref1KeysDateDatePicker"></div>
		                        <div dojoType="ingrid:ValidationTextbox" maxlength="80" widgetId="ref1KeysVersion"></div>
		                      </div>
		                	    <table id="ref1KeysText" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y" minRows="3" cellspacing="0" class="filteringTable interactive nosort">
		                	      <thead>
		                		      <tr>
		                      			<th nosort="true" field="title" dataType="String" width="435" editor="ref1KeysTitleCombobox"><script>document.write(message.get("ui.obj.type1.keyCatTable.header.title"))</script></th>
		                      			<th nosort="true" field="date" dataType="Date" width="120" editor="ref1KeysDateDatePicker"><script>document.write(message.get("ui.obj.type1.keyCatTable.header.date"))</script></th>
		                      			<th nosort="true" field="version" dataType="String" width="120" editor="ref1KeysVersion"><script>document.write(message.get("ui.obj.type1.keyCatTable.header.version"))</script></th>
		                		      </tr>
		                	      </thead>
		                	      <tbody>
		                	      </tbody>
		                	    </table>
		                	  </div>
		              		</div>
							<script>document.write("<div id='ref1KeysTab2' dojoType='ContentPane' label='"+message.get("ui.obj.type1.keyCatTable.tab.links")+"'>")</script>
		              	    <table id="ref1KeysLink" width="676" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort">
		              	      <thead>
		              		      <tr>
		                    			<th nosort="true" field="icon" dataType="String"></th>
		                    			<th nosort="true" field="linkLabel" dataType="String">Objekte</th>
		              		      </tr>
		              	      </thead>
							  <colgroup>
							    <col width="23">
							    <col width="297">
							  </colgroup>
		              	      <tbody>
		              	      </tbody>
		              	    </table>
		              		</div>
		              	</div>
		             </span>
	          	  </div>

	              <div class="inputContainer notRequired h108">
                	<span id="uiElementN003" type="optional">
	                	<span class="label"><label for="ref1ServiceLink" onclick="javascript:dialog.showContextHelp(arguments[0], 5066)"><script>document.write(message.get("ui.obj.type1.serviceLink.title"))</script></label></span>
		                <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddServiceLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('ui.obj.links.linksTo.link'), 'mdek_links_dialog.jsp', 1010, 680, true, {filter: 5066});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><script>document.write(message.get("ui.obj.type1.serviceLink.link"))</script></a></span>
	                		<div class="tableContainer headHiddenRows4 full">
		            	    	<table id="ref1ServiceLink" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort">
									<thead>
										<tr>
	                    					<th nosort="true" field="icon" dataType="String"></th>
	                    					<th nosort="true" field="linkLabel" dataType="String">Objekte</th>
	              		      			</tr>
	            	      			</thead>
						  			<colgroup>
						    			<col width="23">
						    			<col width="297">
						  			</colgroup>
	            	      			<tbody>
	            	      			</tbody>
	            	    		</table>
	                		</div>
						</span>
					</div>

	              <div class="inputContainer notRequired">
                	<span id="uiElement3570" type="optional">
		                <span class="label"><label for="ref1DataBasisTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3570)"><script>document.write(message.get("ui.obj.type1.dataBasisTable.title"))</script></label></span>
		                <span id="ref1DataBasisTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddDataBasisLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('ui.obj.links.linksTo.link'), 'mdek_links_dialog.jsp', 1010, 680, true, {filter: 3570});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><script>document.write(message.get("ui.obj.type1.dataBasisTable.link"))</script></a></span>
		              	<div id="ref1DataBasisTabContainer" dojoType="ingrid:TabContainer" class="h108" selectedChild="ref1DataBasisTab1">
							<script>document.write("<div id='ref1DataBasisTab1' dojoType='ContentPane' label='"+message.get("ui.obj.type1.dataBasisTable.tab.text")+"'>")</script>
		                    <span class="input">
		               			<input type="text" mode="textarea" id="ref1DataBasisText" name="ref1DataBasisText" class="w668 h083" dojoType="ingrid:ValidationTextbox" /> 
		                    </span>
		              		</div>
							<script>document.write("<div id='ref1DataBasisTab2' dojoType='ContentPane' label='"+message.get("ui.obj.type1.dataBasisTable.tab.links")+"'>")</script>
		                    <div class="tableContainer headHiddenRows4 full">
		                	    <table id="ref1DataBasisLink" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort">
		                	      <thead>
			              		      <tr>
			                    			<th nosort="true" field="icon" dataType="String"></th>
			                    			<th nosort="true" field="linkLabel" dataType="String">Objekte</th>
			              		      </tr>
		                	      </thead>
								  <colgroup>
								    <col width="23">
								    <col width="297">
								  </colgroup>
		                	      <tbody>
		                	      </tbody>
		                	    </table>
		                    </div>
		              		</div>
		              	</div>
		          	</span>
	          	  </div>

	              <div class="inputContainer notRequired h088">
                	<span id="uiElement5070" type="optional">
		                <span class="label"><label for="ref1Data" onclick="javascript:dialog.showContextHelp(arguments[0], 5070)"><script>document.write(message.get("ui.obj.type1.attributes"))</script></label></span>
		                <div class="tableContainer headHiddenRows3 full">
		                  <div class="cellEditors" id="ref1DataEditors">
		                    <div dojoType="ingrid:ValidationTextbox" maxlength="255" class="w659" widgetId="ref1DataEditor"></div>
		                  </div>
		             	    <table id="ref1Data" dojoType="ingrid:FilteringTable" minRows="3" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="title" dataType="String" editor="ref1DataEditor">Sachdaten</th>
		            		      </tr>
		            	      </thead>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
		          	</span>
	          	  </div>

	              <div class="inputContainer noSpaceBelow notRequired">
					<span id="uiElement3515" type="optional">
		                <span class="label"><label for="ref1ProcessTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3515)"><script>document.write(message.get("ui.obj.type1.processTable.title"))</script></label></span>
		                <span id="ref1ProcessTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddProcessLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('ui.obj.links.linksTo.link'), 'mdek_links_dialog.jsp', 1010, 680, true, {filter: 3515});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><script>document.write(message.get("ui.obj.type1.processTable.link"))</script></a></span>
						<div id="ref1ProcessTabContainer" dojoType="ingrid:TabContainer" class="h108" selectedChild="ref1ProcessTab1">
							<script>document.write("<div id='ref1ProcessTab1' dojoType='ContentPane' label='"+message.get("ui.obj.type1.processTable.tab.text")+"'>")</script>
								<span class="input">
		               				<input type="text" mode="textarea" id="ref1ProcessText" name="ref1ProcessText" class="w668 h083" dojoType="ingrid:ValidationTextbox" /> 
		                    	</span>
		              		</div>
							<script>document.write("<div id='ref1ProcessTab2' dojoType='ContentPane' label='"+message.get("ui.obj.type1.processTable.tab.links")+"'>")</script>
		                    	<div class="tableContainer headHiddenRows4 full">
		                	    	<table id="ref1ProcessLink" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort">
			                	      	<thead>
				              		    	<tr>
				                    			<th nosort="true" field="icon" dataType="String"></th>
				                    			<th nosort="true" field="linkLabel" dataType="String">Objekte</th>
											</tr>
			                	      	</thead>
									  	<colgroup>
									    	<col width="23">
									    	<col width="297">
									  	</colgroup>
			                	      	<tbody>
			                	      	</tbody>
		                	    	</table>
								</div>
							</div>
						</div>
					</span>
	          	  </div>

	            </div>
	          </div>

              <!-- "FACHBEZUG DATENQUALITAET" CLASS 1 - GEO-INFORMATION/KARTE //-->
              <div id="refClass1DQ" class="contentBlock">
                <div class="titleBar">
                  <div class="titleIcon"><a href="javascript:toggleFields('refClass1DQ');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
                    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7500)"><script>document.write(message.get("ui.obj.dq"))</script></div>
                    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
                </div>
                <div id="ref1ContentDQ" class="content">

                  <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected INSPIRE theme -->
                  <div id="dq109TableContainer" class="inputContainer notRequired" displaytype="exclude">
                    <span id="uiElement7509" type="optional">
                        <span id="dq109TableLabel" class="label"><label for="dq109Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7509)"><script>document.write(message.get("ui.obj.dq.table109.title"))</script></label></span>
                        <div class="tableContainer full h075">
                          <div class="cellEditors" id="dq109TableEditors">
                            <div dojoType="ingrid:ComboBox" toggle="plain" class="w268" listId="7109" id="dq109NameOfMeasureEditor"></div>
                            <div dojoType="ingrid:RealNumberTextbox" decimal="," maxlength="255" class="w085" widgetId="dq109ValueEditor"></div>
                            <div dojoType="ingrid:ValidationTextbox" maxlength="4000" class="w238" widgetId="dq109DescriptionEditor"></div>
                          </div>
                            <table id="dq109Table" dojoType="ingrid:FilteringTable" minRows="2" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                    <th nosort="true" field="nameOfMeasure" dataType="String" editor="dq109NameOfMeasureEditor"><script>document.write(message.get("ui.obj.dq.table.header1"))</script></th>
                                    <th nosort="true" field="resultValue" dataType="String" editor="dq109ValueEditor"><script>document.write(message.get("ui.obj.dq.table.header2"))</script></th>
                                    <th nosort="true" field="measureDescription" dataType="String" editor="dq109DescriptionEditor"><script>document.write(message.get("ui.obj.dq.table.header3"))</script></th>
                                  </tr>
                              </thead>
                              <colgroup>
                                <col width="300">
                                <col width="105">
                                <col width="250">
                              </colgroup>
                              <tbody>
                              </tbody>
                            </table>
                        </div>
                    </span>
                  </div>

                  <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected INSPIRE theme -->
                  <div id="dq110TableContainer" class="inputContainer notRequired" displaytype="exclude">
                    <span id="uiElement7510" type="optional">
                        <span id="dq110TableLabel" class="label"><label for="dq110Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7510)"><script>document.write(message.get("ui.obj.dq.table110.title"))</script></label></span>
                        <div class="tableContainer full h075">
                          <div class="cellEditors" id="dq110TableEditors">
                            <div dojoType="ingrid:ComboBox" toggle="plain" class="w268" listId="7110" id="dq110NameOfMeasureEditor"></div>
                            <div dojoType="ingrid:RealNumberTextbox" decimal="," maxlength="255" class="w085" widgetId="dq110ValueEditor"></div>
                            <div dojoType="ingrid:ValidationTextbox" maxlength="4000" class="w238" widgetId="dq110DescriptionEditor"></div>
                          </div>
                            <table id="dq110Table" dojoType="ingrid:FilteringTable" minRows="2" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                    <th nosort="true" field="nameOfMeasure" dataType="String" editor="dq110NameOfMeasureEditor"><script>document.write(message.get("ui.obj.dq.table.header1"))</script></th>
                                    <th nosort="true" field="resultValue" dataType="String" editor="dq110ValueEditor"><script>document.write(message.get("ui.obj.dq.table.header2"))</script></th>
                                    <th nosort="true" field="measureDescription" dataType="String" editor="dq110DescriptionEditor"><script>document.write(message.get("ui.obj.dq.table.header3"))</script></th>
                                  </tr>
                              </thead>
                              <colgroup>
                                <col width="300">
                                <col width="105">
                                <col width="250">
                              </colgroup>
                              <tbody>
                              </tbody>
                            </table>
                        </div>
                    </span>
                  </div>

                  <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected INSPIRE theme -->
                  <div id="dq112TableContainer" class="inputContainer notRequired" displaytype="exclude">
                    <span id="uiElement7512" type="optional">
                        <span id="dq112TableLabel" class="label"><label for="dq112Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7512)"><script>document.write(message.get("ui.obj.dq.table112.title"))</script></label></span>
                        <div class="tableContainer full h075">
                          <div class="cellEditors" id="dq112TableEditors">
                            <div dojoType="ingrid:ComboBox" toggle="plain" class="w268" listId="7112" id="dq112NameOfMeasureEditor"></div>
                            <div dojoType="ingrid:RealNumberTextbox" decimal="," maxlength="255" class="w085" widgetId="dq112ValueEditor"></div>
                            <div dojoType="ingrid:ValidationTextbox" maxlength="4000" class="w238" widgetId="dq112DescriptionEditor"></div>
                          </div>
                            <table id="dq112Table" dojoType="ingrid:FilteringTable" minRows="2" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                    <th nosort="true" field="nameOfMeasure" dataType="String" editor="dq112NameOfMeasureEditor"><script>document.write(message.get("ui.obj.dq.table.header1"))</script></th>
                                    <th nosort="true" field="resultValue" dataType="String" editor="dq112ValueEditor"><script>document.write(message.get("ui.obj.dq.table.header2"))</script></th>
                                    <th nosort="true" field="measureDescription" dataType="String" editor="dq112DescriptionEditor"><script>document.write(message.get("ui.obj.dq.table.header3"))</script></th>
                                  </tr>
                              </thead>
                              <colgroup>
                                <col width="300">
                                <col width="105">
                                <col width="250">
                              </colgroup>
                              <tbody>
                              </tbody>
                            </table>
                        </div>
                    </span>
                  </div>

                  <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected INSPIRE theme -->
                  <div id="dq113TableContainer" class="inputContainer notRequired" displaytype="exclude">
                    <span id="uiElement7513" type="optional">
                        <span id="dq113TableLabel" class="label"><label for="dq113Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7513)"><script>document.write(message.get("ui.obj.dq.table113.title"))</script></label></span>
                        <div class="tableContainer full h075">
                          <div class="cellEditors" id="dq113TableEditors">
                            <div dojoType="ingrid:ComboBox" toggle="plain" class="w268" listId="7113" id="dq113NameOfMeasureEditor"></div>
                            <div dojoType="ingrid:RealNumberTextbox" decimal="," maxlength="255" class="w085" widgetId="dq113ValueEditor"></div>
                            <div dojoType="ingrid:ValidationTextbox" maxlength="4000" class="w238" widgetId="dq113DescriptionEditor"></div>
                          </div>
                            <table id="dq113Table" dojoType="ingrid:FilteringTable" minRows="2" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                    <th nosort="true" field="nameOfMeasure" dataType="String" editor="dq113NameOfMeasureEditor"><script>document.write(message.get("ui.obj.dq.table.header1"))</script></th>
                                    <th nosort="true" field="resultValue" dataType="String" editor="dq113ValueEditor"><script>document.write(message.get("ui.obj.dq.table.header2"))</script></th>
                                    <th nosort="true" field="measureDescription" dataType="String" editor="dq113DescriptionEditor"><script>document.write(message.get("ui.obj.dq.table.header3"))</script></th>
                                  </tr>
                              </thead>
                              <colgroup>
                                <col width="300">
                                <col width="105">
                                <col width="250">
                              </colgroup>
                              <tbody>
                              </tbody>
                            </table>
                        </div>
                    </span>
                  </div>

                  <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected INSPIRE theme -->
                  <div id="dq114TableContainer" class="inputContainer notRequired" displaytype="exclude">
                    <span id="uiElement7514" type="optional">
                        <span id="dq114TableLabel" class="label"><label for="dq114Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7514)"><script>document.write(message.get("ui.obj.dq.table114.title"))</script></label></span>
                        <div class="tableContainer full h075">
                          <div class="cellEditors" id="dq114TableEditors">
                            <div dojoType="ingrid:ComboBox" toggle="plain" class="w268" listId="7114" id="dq114NameOfMeasureEditor"></div>
                            <div dojoType="ingrid:RealNumberTextbox" decimal="," maxlength="255" class="w085" widgetId="dq114ValueEditor"></div>
                            <div dojoType="ingrid:ValidationTextbox" maxlength="4000" class="w238" widgetId="dq114DescriptionEditor"></div>
                          </div>
                            <table id="dq114Table" dojoType="ingrid:FilteringTable" minRows="2" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                    <th nosort="true" field="nameOfMeasure" dataType="String" editor="dq114NameOfMeasureEditor"><script>document.write(message.get("ui.obj.dq.table.header1"))</script></th>
                                    <th nosort="true" field="resultValue" dataType="String" editor="dq114ValueEditor"><script>document.write(message.get("ui.obj.dq.table.header2"))</script></th>
                                    <th nosort="true" field="measureDescription" dataType="String" editor="dq114DescriptionEditor"><script>document.write(message.get("ui.obj.dq.table.header3"))</script></th>
                                  </tr>
                              </thead>
                              <colgroup>
                                <col width="300">
                                <col width="105">
                                <col width="250">
                              </colgroup>
                              <tbody>
                              </tbody>
                            </table>
                        </div>
                    </span>
                  </div>

                  <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected INSPIRE theme -->
                  <div id="dq115TableContainer" class="inputContainer notRequired" displaytype="exclude">
                    <span id="uiElement7515" type="optional">
                        <span id="dq115TableLabel" class="label"><label for="dq115Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7515)"><script>document.write(message.get("ui.obj.dq.table115.title"))</script></label></span>
                        <div class="tableContainer full h075">
                          <div class="cellEditors" id="dq115TableEditors">
                            <div dojoType="ingrid:ComboBox" toggle="plain" class="w268" listId="7115" id="dq115NameOfMeasureEditor"></div>
                            <div dojoType="ingrid:RealNumberTextbox" decimal="," maxlength="255" class="w085" widgetId="dq115ValueEditor"></div>
                            <div dojoType="ingrid:ValidationTextbox" maxlength="4000" class="w238" widgetId="dq115DescriptionEditor"></div>
                          </div>
                            <table id="dq115Table" dojoType="ingrid:FilteringTable" minRows="2" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                    <th nosort="true" field="nameOfMeasure" dataType="String" editor="dq115NameOfMeasureEditor"><script>document.write(message.get("ui.obj.dq.table.header1"))</script></th>
                                    <th nosort="true" field="resultValue" dataType="String" editor="dq115ValueEditor"><script>document.write(message.get("ui.obj.dq.table.header2"))</script></th>
                                    <th nosort="true" field="measureDescription" dataType="String" editor="dq115DescriptionEditor"><script>document.write(message.get("ui.obj.dq.table.header3"))</script></th>
                                  </tr>
                              </thead>
                              <colgroup>
                                <col width="300">
                                <col width="105">
                                <col width="250">
                              </colgroup>
                              <tbody>
                              </tbody>
                            </table>
                        </div>
                    </span>
                  </div>

                  <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected INSPIRE theme -->
                  <div id="dq117TableContainer" class="inputContainer notRequired" displaytype="exclude">
                    <span id="uiElement7517" type="optional">
                        <span id="dq117TableLabel" class="label"><label for="dq117Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7517)"><script>document.write(message.get("ui.obj.dq.table117.title"))</script></label></span>
                        <div class="tableContainer full h075">
                          <div class="cellEditors" id="dq117TableEditors">
                            <div dojoType="ingrid:ComboBox" toggle="plain" class="w268" listId="7117" id="dq117NameOfMeasureEditor"></div>
                            <div dojoType="ingrid:RealNumberTextbox" decimal="," maxlength="255" class="w085" widgetId="dq117ValueEditor"></div>
                            <div dojoType="ingrid:ValidationTextbox" maxlength="4000" class="w238" widgetId="dq117DescriptionEditor"></div>
                          </div>
                            <table id="dq117Table" dojoType="ingrid:FilteringTable" minRows="2" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                    <th nosort="true" field="nameOfMeasure" dataType="String" editor="dq117NameOfMeasureEditor"><script>document.write(message.get("ui.obj.dq.table.header1"))</script></th>
                                    <th nosort="true" field="resultValue" dataType="String" editor="dq117ValueEditor"><script>document.write(message.get("ui.obj.dq.table.header2"))</script></th>
                                    <th nosort="true" field="measureDescription" dataType="String" editor="dq117DescriptionEditor"><script>document.write(message.get("ui.obj.dq.table.header3"))</script></th>
                                  </tr>
                              </thead>
                              <colgroup>
                                <col width="300">
                                <col width="105">
                                <col width="250">
                              </colgroup>
                              <tbody>
                              </tbody>
                            </table>
                        </div>
                    </span>
                  </div>

                  <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected INSPIRE theme -->
                  <div id="dq120TableContainer" class="inputContainer notRequired" displaytype="exclude">
                    <span id="uiElement7520" type="optional">
                        <span id="dq120TableLabel" class="label"><label for="dq120Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7520)"><script>document.write(message.get("ui.obj.dq.table120.title"))</script></label></span>
                        <div class="tableContainer full h075">
                          <div class="cellEditors" id="dq120TableEditors">
                            <div dojoType="ingrid:ComboBox" toggle="plain" class="w268" listId="7120" id="dq120NameOfMeasureEditor"></div>
                            <div dojoType="ingrid:RealNumberTextbox" decimal="," maxlength="255" class="w085" widgetId="dq120ValueEditor"></div>
                            <div dojoType="ingrid:ValidationTextbox" maxlength="4000" class="w238" widgetId="dq120DescriptionEditor"></div>
                          </div>
                            <table id="dq120Table" dojoType="ingrid:FilteringTable" minRows="2" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                    <th nosort="true" field="nameOfMeasure" dataType="String" editor="dq120NameOfMeasureEditor"><script>document.write(message.get("ui.obj.dq.table.header1"))</script></th>
                                    <th nosort="true" field="resultValue" dataType="String" editor="dq120ValueEditor"><script>document.write(message.get("ui.obj.dq.table.header2"))</script></th>
                                    <th nosort="true" field="measureDescription" dataType="String" editor="dq120DescriptionEditor"><script>document.write(message.get("ui.obj.dq.table.header3"))</script></th>
                                  </tr>
                              </thead>
                              <colgroup>
                                <col width="300">
                                <col width="105">
                                <col width="250">
                              </colgroup>
                              <tbody>
                              </tbody>
                            </table>
                        </div>
                    </span>
                  </div>

                  <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected INSPIRE theme -->
                  <div id="dq125TableContainer" class="inputContainer notRequired" displaytype="exclude">
                    <span id="uiElement7525" type="optional">
                        <span id="dq125TableLabel" class="label"><label for="dq125Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7525)"><script>document.write(message.get("ui.obj.dq.table125.title"))</script></label></span>
                        <div class="tableContainer full h075">
                          <div class="cellEditors" id="dq125TableEditors">
                            <div dojoType="ingrid:ComboBox" toggle="plain" class="w268" listId="7125" id="dq125NameOfMeasureEditor"></div>
                            <div dojoType="ingrid:RealNumberTextbox" decimal="," maxlength="255" class="w085" widgetId="dq125ValueEditor"></div>
                            <div dojoType="ingrid:ValidationTextbox" maxlength="4000" class="w238" widgetId="dq125DescriptionEditor"></div>
                          </div>
                            <table id="dq125Table" dojoType="ingrid:FilteringTable" minRows="2" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                    <th nosort="true" field="nameOfMeasure" dataType="String" editor="dq125NameOfMeasureEditor"><script>document.write(message.get("ui.obj.dq.table.header1"))</script></th>
                                    <th nosort="true" field="resultValue" dataType="String" editor="dq125ValueEditor"><script>document.write(message.get("ui.obj.dq.table.header2"))</script></th>
                                    <th nosort="true" field="measureDescription" dataType="String" editor="dq125DescriptionEditor"><script>document.write(message.get("ui.obj.dq.table.header3"))</script></th>
                                  </tr>
                              </thead>
                              <colgroup>
                                <col width="300">
                                <col width="105">
                                <col width="250">
                              </colgroup>
                              <tbody>
                              </tbody>
                            </table>
                        </div>
                    </span>
                  </div>

                  <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected INSPIRE theme -->
                  <div id="dq126TableContainer" class="inputContainer notRequired" displaytype="exclude">
                    <span id="uiElement7526" type="optional">
                        <span id="dq126TableLabel" class="label"><label for="dq126Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7526)"><script>document.write(message.get("ui.obj.dq.table126.title"))</script></label></span>
                        <div class="tableContainer full h075">
                          <div class="cellEditors" id="dq126TableEditors">
                            <div dojoType="ingrid:ComboBox" toggle="plain" class="w268" listId="7126" id="dq126NameOfMeasureEditor"></div>
                            <div dojoType="ingrid:RealNumberTextbox" decimal="," maxlength="255" class="w085" widgetId="dq126ValueEditor"></div>
                            <div dojoType="ingrid:ValidationTextbox" maxlength="4000" class="w238" widgetId="dq126DescriptionEditor"></div>
                          </div>
                            <table id="dq126Table" dojoType="ingrid:FilteringTable" minRows="2" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                    <th nosort="true" field="nameOfMeasure" dataType="String" editor="dq126NameOfMeasureEditor"><script>document.write(message.get("ui.obj.dq.table.header1"))</script></th>
                                    <th nosort="true" field="resultValue" dataType="String" editor="dq126ValueEditor"><script>document.write(message.get("ui.obj.dq.table.header2"))</script></th>
                                    <th nosort="true" field="measureDescription" dataType="String" editor="dq126DescriptionEditor"><script>document.write(message.get("ui.obj.dq.table.header3"))</script></th>
                                  </tr>
                              </thead>
                              <colgroup>
                                <col width="300">
                                <col width="105">
                                <col width="250">
                              </colgroup>
                              <tbody>
                              </tbody>
                            </table>
                        </div>
                    </span>
                  </div>

                  <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected INSPIRE theme -->
                  <div id="dq127TableContainer" class="inputContainer notRequired" displaytype="exclude">
                    <span id="uiElement7527" type="optional">
                        <span id="dq127TableLabel" class="label"><label for="dq127Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7527)"><script>document.write(message.get("ui.obj.dq.table127.title"))</script></label></span>
                        <div class="tableContainer full h075">
                          <div class="cellEditors" id="dq127TableEditors">
                            <div dojoType="ingrid:ComboBox" toggle="plain" class="w268" listId="7127" id="dq127NameOfMeasureEditor"></div>
                            <div dojoType="ingrid:RealNumberTextbox" decimal="," maxlength="255" class="w085" widgetId="dq127ValueEditor"></div>
                            <div dojoType="ingrid:ValidationTextbox" maxlength="4000" class="w238" widgetId="dq127DescriptionEditor"></div>
                          </div>
                            <table id="dq127Table" dojoType="ingrid:FilteringTable" minRows="2" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                    <th nosort="true" field="nameOfMeasure" dataType="String" editor="dq127NameOfMeasureEditor"><script>document.write(message.get("ui.obj.dq.table.header1"))</script></th>
                                    <th nosort="true" field="resultValue" dataType="String" editor="dq127ValueEditor"><script>document.write(message.get("ui.obj.dq.table.header2"))</script></th>
                                    <th nosort="true" field="measureDescription" dataType="String" editor="dq127DescriptionEditor"><script>document.write(message.get("ui.obj.dq.table.header3"))</script></th>
                                  </tr>
                              </thead>
                              <colgroup>
                                <col width="300">
                                <col width="105">
                                <col width="250">
                              </colgroup>
                              <tbody>
                              </tbody>
                            </table>
                        </div>
                    </span>
                  </div>

                </div>
              </div>

	          <!-- FACHBEZUG CLASS 2 - DOKUMENT/BERICHT/LITERATUR //-->
	          <div id="refClass2" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('refClass2');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7000)"><script>document.write(message.get("ui.obj.relevance"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="ref2Content" class="content">

	              <div class="inputContainer notRequired">
                	<span id="uiElement3355" type="optional">
		                <span class="label"><label for="ref2Author" onclick="javascript:dialog.showContextHelp(arguments[0], 3355)"><script>document.write(message.get("ui.obj.type2.author"))</script></label></span>
    	           		<span class="input"><input type="text" maxlength="255" mode="textarea" id="ref2Author" name="ref2Author" class="w668 h038" dojoType="ingrid:ValidationTextbox" /></span> 
	    			</span>
	              </div>

	              <div class="inputContainer notRequired">
                	<span id="uiElement3350" type="optional">
		                <span class="label"><label for="ref2Publisher" onclick="javascript:dialog.showContextHelp(arguments[0], 3350)"><script>document.write(message.get("ui.obj.type2.editor"))</script></label></span>
		                <span class="input"><input type="text" maxlength="255" id="ref2Publisher" name="ref2Publisher" class="w668" dojoType="ingrid:ValidationTextBox" /></span>
		            </span>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
                	  <span id="uiElement3340" type="optional">
		                  <span class="label"><label for="ref2PublishedIn" onclick="javascript:dialog.showContextHelp(arguments[0], 3340)"><script>document.write(message.get("ui.obj.type2.publishedIn"))</script></label></span>
		                  <span class="input"><input type="text" maxlength="80" id="ref2PublishedIn" name="ref2PublishedIn" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
		              </span>
	                </div>
	          
	                <div class="half">
                	  <span id="uiElement3310" type="optional">
		                  <span class="label"><label for="ref2PublishLocation" onclick="javascript:dialog.showContextHelp(arguments[0], 3310)"><script>document.write(message.get("ui.obj.type2.publishedLocation"))</script></label></span>
		                  <span class="input"><input type="text" maxlength="80" id="ref2PublishLocation" name="ref2PublishLocation" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
		              </span>
	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
	                  <div id="ref2PublishedInDetails1">
	                    <span class="entry first">
                	  	  <span id="uiElement3330" type="optional">
		                      <span class="label"><label for="ref2PublishedInIssue" onclick="javascript:dialog.showContextHelp(arguments[0], 3330)"><script>document.write(message.get("ui.obj.type2.issue"))</script></label></span>
		                      <span class="input spaceBelow"><input type="text" maxlength="40" id="ref2PublishedInIssue" name="ref2PublishedInIssue" class="w085" dojoType="ingrid:ValidationTextBox" /></span>
		                  </span>
	                    </span>
	                    <span class="entry">
                	  	  <span id="uiElement3320" type="optional">
		                      <span class="label"><label for="ref2PublishedInPages" onclick="javascript:dialog.showContextHelp(arguments[0], 3320)"><script>document.write(message.get("ui.obj.type2.pages"))</script></label></span>
		                      <span class="input spaceBelow"><input type="text" maxlength="20" id="ref2PublishedInPages" name="ref2PublishedInPages" class="w085" dojoType="ingrid:ValidationTextBox" /></span>
		                  </span>
	                    </span>
	                    <span class="entry rightAlign">
                	  	  <span id="uiElement3300" type="optional">
		                      <span class="label"><label for="ref2PublishedInYear" onclick="javascript:dialog.showContextHelp(arguments[0], 3300)"><script>document.write(message.get("ui.obj.type2.publishedYear"))</script></label></span>
		                      <span class="input spaceBelow"><input type="text" maxlength="20" id="ref2PublishedInYear" name="ref2PublishedInYear" class="w085" dojoType="ingrid:ValidationTextBox" /></span>
		                  </span>
	                    </span>
	                  </div>

	                  <div id="ref2PublishedInDetails2">
	                    <span class="entry first">
                	  	  <span id="uiElement3365" type="optional">
		                      <span class="label"><label for="ref2PublishedISBN" onclick="javascript:dialog.showContextHelp(arguments[0], 3365)"><script>document.write(message.get("ui.obj.type2.isbn"))</script></label></span>
		                      <span class="input"><input type="text" maxlength="40" id="ref2PublishedISBN" name="ref2PublishedISBN" class="w148" dojoType="ingrid:ValidationTextBox" /></span>
		                  </span>
	                    </span>
	                    <span class="entry">
                	  	  <span id="uiElement3370" type="optional">
		                      <span class="label"><label for="ref2PublishedPublisher" onclick="javascript:dialog.showContextHelp(arguments[0], 3370)"><script>document.write(message.get("ui.obj.type2.publisher"))</script></label></span>
		                      <span class="input"><input type="text" maxlength="80" id="ref2PublishedPublisher" name="ref2PublishedPublisher" class="w148" dojoType="ingrid:ValidationTextBox" /></span>
		                  </span>
	                    </span>
	                  </div>
	                </div>
	          
	                <div class="half">
						<span id="uiElement3360" type="optional">
							<span class="label"><label for="ref2LocationTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3360)"><script>document.write(message.get("ui.obj.type2.locationTable.title"))</script></label></span>
	                  		<span id="ref2LocationTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref2AddLocationLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('general.addAddress'), 'mdek_address_dialog.jsp', 755, 580, true, {linkType: 3360});" title="<fmt:message key="dialog.popup.addressTable.link" /> [Popup]"><script>document.write(message.get("ui.obj.type2.locationTable.link"))</script></a></span>

	                		<div id="ref2LocationTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref2LocationTab1">
								<script>document.write("<div id='ref2LocationTab1' dojoType='ContentPane' label='"+message.get("ui.obj.type2.locationTable.tab.text")+"'>")</script>
									<span class="input">
										<input type="text" maxlength="80" mode="textarea" id="ref2LocationText" name="ref2LocationText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
									</span>
	                			</div>
								<script>document.write("<div id='ref2LocationTab2' dojoType='ContentPane' label='"+message.get("ui.obj.type2.locationTable.tab.links")+"'>")</script>
	                      			<div class="tableContainer headHiddenRows2 half">
	                  	    			<table id="ref2LocationLink" dojoType="ingrid:FilteringTable" minRows="2" headClass="hidden" cellspacing="0" class="filteringTable interactive nosort">
	                  	      				<thead>
		              		      				<tr>
													<th nosort="true" field="icon" dataType="String"></th>
													<th nosort="true" field="linkLabel" dataType="String">Objekte</th>
		              		      				</tr>
	                  	      				</thead>
							  				<colgroup>
							    				<col width="23">
							    				<col width="297">
							  				</colgroup>
	                  	      				<tbody>
	                  	      				</tbody>
	                  	    			</table>
	                      			</div>
	                			</div>
	                		</div>
	                	</span>
	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
						<span id="uiElement3385" type="optional">
	                  		<span class="label"><label for="ref2DocumentType" onclick="javascript:dialog.showContextHelp(arguments[0], 3385)"><script>document.write(message.get("ui.obj.type2.documentType"))</script></label></span>
	                  		<span class="input spaceBelow">
	                  			<div dojoType="ingrid:ComboBox" maxlength="80" style="width:302px;" id="ref2DocumentType" listId="3385"></div>
			                </span>
						</span>
					</div>
	          
	                <div class="half">
						<span id="uiElement3210" type="optional">
							<span class="label"><label for="ref2BaseDataTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3210)"><script>document.write(message.get("ui.obj.type2.generalDataTable.title"))</script></label></span>
							<span id="ref2BaseDataTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref2AddBaseDataLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('ui.obj.links.linksTo.link'), 'mdek_links_dialog.jsp', 1010, 680, true, {filter: 3345});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><script>document.write(message.get("ui.obj.type2.generalDataTable.link"))</script></a></span>
							<div id="ref2BaseDataTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref2BaseDataTab1">
								<script>document.write("<div id='ref2BaseDataTab1' dojoType='ContentPane' label='"+message.get("ui.obj.type2.generalDataTable.tab.text")+"'>")</script>
		                     		<span class="input">
		                      			<input type="text" mode="textarea" id="ref2BaseDataText" name="ref2BaseDataText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
		                      		</span>
	                			</div>
								<script>document.write("<div id='ref2BaseDataTab2' dojoType='ContentPane' label='"+message.get("ui.obj.type2.generalDataTable.tab.links")+"'>")</script>
	                      			<div class="tableContainer headHiddenRows2 half">
	                  	    			<table id="ref2BaseDataLink" dojoType="ingrid:FilteringTable" minRows="2" headClass="hidden" cellspacing="0" class="filteringTable nosort">
	                  	      				<thead>
		              		      				<tr>
		                    						<th nosort="true" field="icon" dataType="String"></th>
		                    						<th nosort="true" field="linkLabel" dataType="String">Objekte</th>
		              		      				</tr>
	                  	      				</thead>
							  				<colgroup>
							    				<col width="23">
							    				<col width="297">
							  				</colgroup>
	                  	      				<tbody>
	                  	      				</tbody>
	                  	    			</table>
	                      			</div>
								</div>
							</div>
						</span>
					</div>
	              </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <div class="half left">
	                	<span id="uiElement3380" type="optional">
	                		<span class="label"><label for="ref2BibData" onclick="javascript:dialog.showContextHelp(arguments[0], 3380)"><script>document.write(message.get("ui.obj.type2.additionalBibInfo"))</script></label></span>
               				<span class="input"><input type="text" maxlength="255" mode="textarea" id="ref2BibData" name="ref2BibData" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span>
               			</span> 
 	                </div>

	                <div class="half">
	                	<span id="uiElement3375" type="optional">
		                	<span class="label"><label for="ref2Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 3375)"><script>document.write(message.get("ui.obj.type2.description"))</script></label></span>
    	           			<span class="input"><input type="text" mode="textarea" id="ref2Explanation" name="ref2Explanation" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span>
    	           		</span> 
	                </div>
	                <div class="fill"></div>
	              </div>

	            </div>
	          </div>

	          <!-- FACHBEZUG CLASS 3 - Geodatendienst //-->
	          <div id="refClass3" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('refClass3');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7000)"><script>document.write(message.get("ui.obj.relevance"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="ref3Content" class="content">

	              <div class="inputContainer required">
					<span id="uiElementN022" type="required">
	                  <span id="ref3ServiceTypeTableLabel" class="label required"><label for="ref3ServiceTypeTable" onclick="javascript:dialog.showContextHelp(arguments[0], 10022)"><script>document.write(message.get("ui.obj.type3.ref3ServiceTypeTable.title"))</script>*</label></span>
	                  <div class="tableContainer headHiddenRows4 full">
	                    <div class="cellEditors" id="ref3ServiceTypeTableEditors">
	                      <div dojoType="ingrid:Select" toggle="plain" class="w608" listId="5200" id="ref3ServiceTypeEditor"></div>
	                    </div>
	               	    <table id="ref3ServiceTypeTable" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
	              	      <thead>
	              		      <tr>
	                    			<th nosort="true" field="title" dataType="String" editor="ref3ServiceTypeEditor">Art des Dienstes</th>
	              		      </tr>
	              	      </thead>
	              	      <tbody>
	              	      </tbody>
	              	    </table>
	                  </div>
					</span>
	              </div>


	              <div class="inputContainer required">
	                <div class="half left">
	                	<span id="uiElement3220" type="required">
							<span id="ref3ServiceTypeLabel" class="label required"><label for="ref3ServiceType" onclick="javascript:dialog.showContextHelp(arguments[0], 3220)"><script>document.write(message.get("ui.obj.type3.serviceType"))</script>*</label></span>
<!-- 
							<span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Assistent', 'erfassung_assistent_capabilities.html', 755, 195, true);" title="<fmt:message key="general.assistant" /> [Popup]"><fmt:message key="dialog.popup.serviceLink.link" /></a></span>
 -->
							<span class="input spaceBelow">
								<div dojoType="ingrid:Select" maxlength="255" autoComplete="false" class="w308" listId="5100" id="ref3ServiceType"></div>
				  			</span>
						</span>
            		</div>

	                <div class="half">
                	  <span id="uiElement3230" type="optional">
		                  <span class="label"><label for="ref3ServiceVersion" onclick="javascript:dialog.showContextHelp(arguments[0], 3230)"><script>document.write(message.get("ui.obj.type3.serviceVersion"))</script></label></span>
		                  <div class="tableContainer headHiddenRows4 half">
		                    <div class="cellEditors" id="ref3ServiceVersionEditors">
		                      <div dojoType="ingrid:ValidationTextbox" maxlength="80" widgetId="ref3ServiceVersionServiceType"></div>
		                    </div>
		               	    <table id="ref3ServiceVersion" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
		              	      <thead>
		              		      <tr>
		                    			<th nosort="true" field="title" dataType="String" editor="ref3ServiceVersionServiceType">Version des Services</th>
		              		      </tr>
		              	      </thead>
		              	      <tbody>
		              	      </tbody>
		              	    </table>
		                  </div>
		                </span>
	            	  </div>
	            	  <div class="fill"></div>
	              </div>

	              <div class="inputContainer notRequired h130">
					<span id="uiElementN023" type="optional">
		                <span class="label"><label for="ref3Scale" onclick="javascript:dialog.showContextHelp(arguments[0], 3525)"><script>document.write(message.get("ui.obj.type3.scaleTable.title"))</script></label></span>
		                <div class="tableContainer rows4 full">
		                  <div class="cellEditors" id="ref3ScaleEditors">
		                    <div dojoType="IntegerTextbox" min="0" max="2147483647" maxlength="10" widgetId="ref3ScaleScale"></div>
		                    <div dojoType="ingrid:RealNumberTextbox" decimal="," widgetId="ref3ScaleGroundResolution"></div>
		                    <div dojoType="ingrid:RealNumberTextbox" decimal="," widgetId="ref3ScaleScanResolution"></div>
		                  </div>
		            	    <table id="ref3Scale" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort interactive">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="scale" dataType="String" width="105" editor="ref3ScaleScale"><script>document.write(message.get("ui.obj.type3.scaleTable.header.scale"))</script></th>
		                  			<th nosort="true" field="groundResolution" dataType="String" width="285" editor="ref3ScaleGroundResolution"><script>document.write(message.get("ui.obj.type3.scaleTable.header.groundResolution"))</script></th>
		                  			<th nosort="true" field="scanResolution" dataType="String" width="285" editor="ref3ScaleScanResolution"><script>document.write(message.get("ui.obj.type3.scaleTable.header.scanResolution"))</script></th>
		            		      </tr>
		            	      </thead>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
					</span>
	          	  </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
                	  <span id="uiElement3200" type="optional">
		                  <span class="label"><label for="ref3SystemEnv" onclick="javascript:dialog.showContextHelp(arguments[0], 3200)"><script>document.write(message.get("ui.obj.type3.environment"))</script></label></span>
	               		  <span class="input"><input type="text" mode="textarea" id="ref3SystemEnv" name="ref3SystemEnv" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span>
	                  </span> 
 	                </div>
	          
	                <div class="half">
                	  <span id="uiElement3240" type="optional">
		                  <span class="label"><label for="ref3History" onclick="javascript:dialog.showContextHelp(arguments[0], 3240)"><script>document.write(message.get("ui.obj.type3.history"))</script></label></span>
    		        	  <span class="input"><input type="text" mode="textarea" id="ref3History" name="ref3History" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span> 
  	              	  </span>
  	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
						<span id="uiElement3345" type="optional">
							<span class="label"><label for="ref3BaseDataTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3345)"><script>document.write(message.get("ui.obj.type3.generalDataTable.title"))</script></label></span>
		                  	<span id="ref3MethodTab2Header" class="functionalLink onTab marginRightColumn"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref3AddBaseDataLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('ui.obj.links.linksTo.link'), 'mdek_links_dialog.jsp', 1010, 680, true, {filter: 3210});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><script>document.write(message.get("ui.obj.type3.generalDataTable.link"))</script></a></span>
		                	<div id="ref3BaseDataTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref3BaseDataTab1">
								<script>document.write("<div id='ref3BaseDataTab1' dojoType='ContentPane' label='"+message.get("ui.obj.type3.generalDataTable.tab.text")+"'>")</script>
		                      		<span class="input">
		                      			<input type="text" mode="textarea" id="ref3BaseDataText" name="ref3BaseDataText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
		                      		</span>
	                			</div>
								<script>document.write("<div id='ref3MethodTab2' dojoType='ContentPane' label='"+message.get("ui.obj.type3.generalDataTable.tab.links")+"'>")</script>
		                      		<div class="tableContainer headHiddenRows2 half">
			                  	    	<table id="ref3BaseDataLink" width="320" dojoType="ingrid:FilteringTable" minRows="2" headClass="hidden" cellspacing="0" class="filteringTable nosort">
			                  	      		<thead>
				              		      		<tr>
					                    			<th nosort="true" field="icon" dataType="String" width="30"></th>
					                    			<th nosort="true" field="linkLabel" dataType="String" width="290">Objekte</th>
												</tr>
			    	              	      	</thead>
									  		<colgroup>
									    		<col width="23">
									    		<col width="297">
									  		</colgroup>
			                  	      		<tbody>
			                  	      		</tbody>
			                  	    	</table>
			                      	</div>
	                			</div>
	                		</div>
	                	</span>
	                </div>
	                
	                <div class="half">
						<span id="uiElement3250" type="optional">
	                  		<span class="label"><label for="ref3Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 3250)"><script>document.write(message.get("ui.obj.type3.description"))</script></label></span>
               				<span class="input"><input type="text" mode="textarea" id="ref3Explanation" name="ref3Explanation" class="w320 h055" dojoType="ingrid:ValidationTextbox" /></span>
               			</span> 
	                </div>
	                <div class="fill"></div>
	          	  </div>

	              <div class="inputContainer notRequired h130">
					<span id="uiElementN004" type="optional">
		                <span class="label"><label for="ref3Operation" onclick="javascript:dialog.showContextHelp(arguments[0], 7015)"><script>document.write(message.get("ui.obj.type3.operationTable.title"))</script></label></span>
		                <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage(message.get('dialog.operations.title'), 'mdek_operation_dialog.jsp', 735, 745, true);" title="<fmt:message key="dialog.popup.operationTable.link" /> [Popup]"><script>document.write(message.get("ui.obj.type3.operationTable.link"))</script></a></span>
		                <div class="tableContainer rows4 full">
		            	    <table id="ref3Operation" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="name" dataType="String" width="165"><script>document.write(message.get("ui.obj.type3.operationTable.header.name"))</script></th>
		                  			<th nosort="true" field="description" dataType="String" width="510"><script>document.write(message.get("ui.obj.type3.operationTable.header.description"))</script></th>
		            		      </tr>
		            	      </thead>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
		          	</span>
	          	  </div>

                  <div class="inputContainer noSpaceBelow notRequired">
					<span id="uiElement3260" type="optional">
                    <div class="checkboxContainer">
                      <span class="input"><input type="checkbox" name="ref3HasAccessConstraint" id="ref3HasAccessConstraint" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 3260)"><script>document.write(message.get("ui.obj.type3.ref3HasAccessConstraint"))</script></label></span>
                    </div>
                    </span>
                  </div>
	          	  
	            </div>
	          </div>

	          <!-- FACHBEZUG CLASS 4 - VORHABEN/PROJEKT/PROGRAMM //-->
	          <div id="refClass4" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('refClass4');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7000)"><script>document.write(message.get("ui.obj.relevance"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="ref4Content" class="content">

	              <div class="inputContainer notRequired">
	                <div class="half left">
	                	<span id="uiElement3410" type="optional">
		                  <span class="label"><label for="ref4ParticipantsTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3410)"><script>document.write(message.get("ui.obj.type4.participantsTable.title"))</script></label></span>
		                  <span id="ref4ParticipantsTab2Header" class="functionalLink onTab marginRightColumn"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref4AddParticipantsLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('general.addAddress'), 'mdek_address_dialog.jsp', 755, 580, true, {linkType: 3410});" title="<fmt:message key="dialog.popup.addressTable.link" /> [Popup]"><script>document.write(message.get("ui.obj.type4.participantsTable.link"))</script></a></span>
		                	<div id="ref4ParticipantsTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref4ParticipantsTab1">
								<script>document.write("<div id='ref4ParticipantsTab1' dojoType='ContentPane' label='"+message.get("ui.obj.type4.participantsTable.tab.text")+"'>")</script>
		                      	<span class="input">
		                      		<input type="text" maxlength="255" mode="textarea" id="ref4ParticipantsText" name="ref4ParticipantsText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
		                      	</span>
		                		</div>
								<script>document.write("<div id='ref4ParticipantsTab2' dojoType='ContentPane' label='"+message.get("ui.obj.type4.participantsTable.tab.links")+"'>")</script>
		                      <div class="tableContainer headHiddenRows2 half">
		                  	    <table id="ref4ParticipantsLink" dojoType="ingrid:FilteringTable" minRows="2" headClass="hidden" cellspacing="0" class="filteringTable interactive nosort">
		                  	      <thead>
			              		      <tr>
			                    			<th nosort="true" field="icon" dataType="String"></th>
			                    			<th nosort="true" field="linkLabel" dataType="String">Objekte</th>
			              		      </tr>
		                  	      </thead>
								  <colgroup>
								    <col width="23">
								    <col width="297">
								  </colgroup>
		                  	      <tbody>
		                  	      </tbody>
		                  	    </table>
		                      </div>
		                		</div>
		                	</div>
		               </span>
	                </div>

	                <div class="half">
	                	<span id="uiElement3400" type="optional">
							<span class="label"><label for="ref4PMTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3400)"><script>document.write(message.get("ui.obj.type4.projectManagerTable.title"))</script></label></span>
		                  	<span id="ref4PMTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref4AddPMLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('general.addAddress'), 'mdek_address_dialog.jsp', 755, 580, true, {linkType: 3400});" title="<fmt:message key="dialog.popup.addressTable.link" /> [Popup]"><script>document.write(message.get("ui.obj.type4.projectManagerTable.link"))</script></a></span>
	                		<div id="ref4PMTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref4PMTab1">
								<script>document.write("<div id='ref4PMTab1' dojoType='ContentPane' label='"+message.get("ui.obj.type4.projectManagerTable.tab.text")+"'>")</script>
	                      			<span class="input">
	                      				<input type="text" maxlength="80" mode="textarea" id="ref4PMText" name="ref4PMText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
	                      			</span>
	                			</div>
								<script>document.write("<div id='ref4PMTab2' dojoType='ContentPane' label='"+message.get("ui.obj.type4.projectManagerTable.tab.links")+"'>")</script>
	                      			<div class="tableContainer rows2 half">
	                  	    			<table id="ref4PMLink" dojoType="ingrid:FilteringTable" minRows="2" headClass="hidden" cellspacing="0" class="filteringTable interactive nosort">
	                  	      				<thead>
	              		      					<tr>
	                    							<th nosort="true" field="icon" dataType="String"></th>
	                    							<th nosort="true" field="linkLabel" dataType="String">Objekte</th>
	              		      					</tr>
	                  	      				</thead>
							  				<colgroup>
							    				<col width="23">
							    				<col width="297">
							  				</colgroup>
	                  	      				<tbody>
	                  	      				</tbody>
	                  	    			</table>
	                      			</div>
                				</div>
                			</div>
                		</span>
	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer noSpaceBelow notRequired">
                	<span id="uiElement3420" type="optional">
		                <span class="label"><label for="ref4Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 3420)"><script>document.write(message.get("ui.obj.type4.description"))</script></label></span>
    	           		<span class="input"><input type="text" mode="textarea" id="ref4Explanation" name="ref4Explanation" class="w668 h055" dojoType="ingrid:ValidationTextbox" /></span>
    	           	</span> 
	          	  </div>

	            </div>
	          </div>

	          <!-- FACHBEZUG CLASS 5 - DATENSAMMLUNG/DATENBANK //-->
	          <div id="refClass5" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('refClass5');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7000)"><script>document.write(message.get("ui.obj.relevance"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="ref5Content" class="content">

	              <div class="inputContainer notRequired h130">
                	<span id="uiElement3110" type="optional">
		                <span class="label"><label for="ref5dbContent" onclick="javascript:dialog.showContextHelp(arguments[0], 3110)"><script>document.write(message.get("ui.obj.type5.contentTable.title"))</script></label></span>
		                <div class="tableContainer rows4 full">
		                  <div class="cellEditors" id="ref5dbContentEditors">
		                    <div dojoType="ingrid:ValidationTextbox" maxlength="80" widgetId="ref5dbContentParameter"></div>
		                    <div dojoType="ingrid:ValidationTextbox" maxlength="120" widgetId="ref5dbContentAdditionalData"></div>
		                  </div>
		            	    <table id="ref5dbContent" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort interactive">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="parameter" dataType="String" width="335" editor="ref5dbContentParameter"><script>document.write(message.get("ui.obj.type5.contentTable.header.parameter"))</script></th>
		                  			<th nosort="true" field="additionalData" dataType="String" width="340" editor="ref5dbContentAdditionalData"><script>document.write(message.get("ui.obj.type5.contentTable.header.additionalData"))</script></th>
		            		      </tr>
		            	      </thead>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
					</span>
	          	  </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <div class="half left">
						<span id="uiElement3100" type="optional">
							<span class="label"><label for="ref5MethodTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3100)"><script>document.write(message.get("ui.obj.type5.methodTable.title"))</script></label></span>
		                  	<span id="ref5MethodTab2Header" class="functionalLink onTab marginRightColumn"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref5AddMethodLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('ui.obj.links.linksTo.link'), 'mdek_links_dialog.jsp', 1010, 680, true, {filter: 3100});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><script>document.write(message.get("ui.obj.type5.methodTable.link"))</script></a></span>
	                		<div id="ref5MethodTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref5MethodTab1">
								<script>document.write("<div id='ref5MethodTab1' dojoType='ContentPane' label='"+message.get("ui.obj.type5.methodTable.tab.text")+"'>")</script>
	                      			<span class="input">
	                      				<input type="text" mode="textarea" maxlength="255" id="ref5MethodText" name="ref5MethodText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
	                      			</span>
	                			</div>
								<script>document.write("<div id='ref5MethodTab2' dojoType='ContentPane' label='"+message.get("ui.obj.type5.methodTable.tab.links")+"'>")</script>
	                      			<div class="tableContainer headHiddenRows2 half">
	                  	    			<table id="ref5MethodLink" dojoType="ingrid:FilteringTable" minRows="2" headClass="hidden" cellspacing="0" class="filteringTable nosort">
	                  	      				<thead>
	                  		      				<tr>
	                        						<th nosort="true" field="icon" dataType="String"></th>
	                        						<th nosort="true" field="linkLabel" dataType="String">Namen</th>
	                  		      				</tr>
	                  	      				</thead>
							  				<colgroup>
							    				<col width="23">
							    				<col width="297">
							  				</colgroup>
	                  	      				<tbody>
	                  	      				</tbody>
	                  	    			</table>
	                      			</div>
	                			</div>
                			</div>
                		</span>
	                </div>

	                <div class="half">
						<span id="uiElement3120" type="optional">
	                  		<span class="label"><label for="ref5Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 3120)"><script>document.write(message.get("ui.obj.type5.description"))</script></label></span>
               				<span class="input"><input type="text" mode="textarea" id="ref5Explanation" name="ref5Explanation" class="w320 h055" dojoType="ingrid:ValidationTextbox" /></span>
               			</span> 
  	                </div>
	                <div class="fill"></div>
	          	  </div>
	          	  
	            </div>
	          </div>

	          <!-- FACHBEZUG CLASS 6 - DIENST/ANWENDUNG/INFORMATIONSSYSTEM //-->
	          <div id="refClass6" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('refClass6');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7000)"><script>document.write(message.get("ui.obj.relevance"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="ref6Content" class="content">

	              <div class="inputContainer required">
	                <div class="half left">
	                	<span id="uiElement3620" type="required">
							<span id="ref6ServiceTypeLabel" class="label required"><label for="ref6ServiceType" onclick="javascript:dialog.showContextHelp(arguments[0], 3620)"><script>document.write(message.get("ui.obj.type6.serviceType"))</script>*</label></span>
							<span class="input spaceBelow">
								<div dojoType="ingrid:Select" maxlength="255" autoComplete="false" class="w308" listId="5300" id="ref6ServiceType"></div>
				  			</span>
						</span>
            		</div>

	                <div class="half">
                	  <span id="uiElement3630" type="optional">
		                  <span class="label"><label for="ref6ServiceVersion" onclick="javascript:dialog.showContextHelp(arguments[0], 3630)"><script>document.write(message.get("ui.obj.type6.serviceVersion"))</script></label></span>
		                  <div class="tableContainer headHiddenRows4 half">
		                    <div class="cellEditors" id="ref6ServiceVersionEditors">
		                      <div dojoType="ingrid:ValidationTextbox" maxlength="80" widgetId="ref6ServiceVersionServiceType"></div>
		                    </div>
		               	    <table id="ref6ServiceVersion" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
		              	      <thead>
		              		      <tr>
		                    			<th nosort="true" field="title" dataType="String" editor="ref6ServiceVersionServiceType">Version</th>
		              		      </tr>
		              	      </thead>
		              	      <tbody>
		              	      </tbody>
		              	    </table>
		                  </div>
		                </span>
	            	  </div>
	            	  <div class="fill"></div>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
                	  <span id="uiElement3600" type="optional">
		                  <span class="label"><label for="ref6SystemEnv" onclick="javascript:dialog.showContextHelp(arguments[0], 3600)"><script>document.write(message.get("ui.obj.type6.environment"))</script></label></span>
	               		  <span class="input"><input type="text" mode="textarea" id="ref6SystemEnv" name="ref6SystemEnv" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span>
	                  </span> 
 	                </div>
	          
	                <div class="half">
                	  <span id="uiElement3640" type="optional">
		                  <span class="label"><label for="ref6History" onclick="javascript:dialog.showContextHelp(arguments[0], 3640)"><script>document.write(message.get("ui.obj.type6.history"))</script></label></span>
    		        	  <span class="input"><input type="text" mode="textarea" id="ref6History" name="ref6History" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span> 
  	              	  </span>
  	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
						<span id="uiElement3645" type="optional">
							<span class="label"><label for="ref6BaseDataTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3645)"><script>document.write(message.get("ui.obj.type6.generalDataTable.title"))</script></label></span>
		                  	<span id="ref6MethodTab2Header" class="functionalLink onTab marginRightColumn"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref6AddBaseDataLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('ui.obj.links.linksTo.link'), 'mdek_links_dialog.jsp', 1010, 680, true, {filter: 3210});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><script>document.write(message.get("ui.obj.type6.generalDataTable.link"))</script></a></span>
		                	<div id="ref6BaseDataTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref6BaseDataTab1">
								<script>document.write("<div id='ref6BaseDataTab1' dojoType='ContentPane' label='"+message.get("ui.obj.type6.generalDataTable.tab.text")+"'>")</script>
		                      		<span class="input">
		                      			<input type="text" mode="textarea" id="ref6BaseDataText" name="ref6BaseDataText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
		                      		</span>
	                			</div>
								<script>document.write("<div id='ref6MethodTab2' dojoType='ContentPane' label='"+message.get("ui.obj.type6.generalDataTable.tab.links")+"'>")</script>
		                      		<div class="tableContainer headHiddenRows2 half">
			                  	    	<table id="ref6BaseDataLink" width="320" dojoType="ingrid:FilteringTable" minRows="2" headClass="hidden" cellspacing="0" class="filteringTable nosort">
			                  	      		<thead>
				              		      		<tr>
					                    			<th nosort="true" field="icon" dataType="String" width="30"></th>
					                    			<th nosort="true" field="linkLabel" dataType="String" width="290">Objekte</th>
												</tr>
			    	              	      	</thead>
									  		<colgroup>
									    		<col width="23">
									    		<col width="297">
									  		</colgroup>
			                  	      		<tbody>
			                  	      		</tbody>
			                  	    	</table>
			                      	</div>
	                			</div>
	                		</div>
	                	</span>
	                </div>

	                <div class="half">
						<span id="uiElement3650" type="optional">
	                  		<span class="label"><label for="ref6Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 3650)"><script>document.write(message.get("ui.obj.type6.description"))</script></label></span>
               				<span class="input"><input type="text" mode="textarea" id="ref6Explanation" name="ref6Explanation" class="w320 h055" dojoType="ingrid:ValidationTextbox" /></span>
               			</span> 
	                </div>
	                <div class="fill"></div>
	          	  </div>

	              <div class="inputContainer notRequired h130">
					<span id="uiElement3670" type="optional">
		                <span class="label"><label for="ref6UrlList" onclick="javascript:dialog.showContextHelp(arguments[0], 3670)"><script>document.write(message.get("ui.obj.type6.urlList"))</script></label></span>
		                <div class="tableContainer rows4 full">
		                  <div class="cellEditors" id="ref6UrlListEditors">
		                    <div dojoType="ingrid:ValidationTextbox" maxlength="1024" widgetId="ref6Name"></div>
		                    <div dojoType="ingrid:ValidationTextbox" maxlength="1024" widgetId="ref6Url"></div>
		                    <div dojoType="ingrid:ValidationTextbox" maxlength="2048" widgetId="ref6UrlDescription"></div>
		                  </div>
		            	    <table id="ref6UrlList" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort interactive">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="name" dataType="String" width="225" editor="ref6Name"><script>document.write(message.get("ui.obj.type6.urlList.header.name"))</script></th>
		                  			<th nosort="true" field="url" dataType="String" width="225" editor="ref6Url"><script>document.write(message.get("ui.obj.type6.urlList.header.url"))</script></th>
		                  			<th nosort="true" field="urlDescription" dataType="String" width="225" editor="ref6UrlDescription"><script>document.write(message.get("ui.obj.type6.urlList.header.urlDescription"))</script></th>
		            		      </tr>
		            	      </thead>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
					</span>
	          	  </div>
	          	  
	            </div>
	          </div>
	          
	          <!-- RAUMBEZUG //-->
	          <div id="spatialRef" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('spatialRef');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7001)"><script>document.write(message.get("ui.obj.spatial.title"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	<div id="spatialRefContent" class="content">

	            <div id="spatialRefAdminUnitContainer" class="inputContainer noSpaceBelow h130">
					<span id="uiElementN006" type="required">
		                <span id="spatialRefAdminUnitLabel" class="label required"><label for="spatialRefAdminUnit" onclick="javascript:dialog.showContextHelp(arguments[0], 10006)"><script>document.write(message.get("ui.obj.spatial.geoThesTable.title"))</script>*</label></span>
		                <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="spatialRefAdminUnitLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('dialog.spatialNavigator.title'), 'mdek_spatial_navigator.jsp', 530, 230, true);" title="<fmt:message key="dialog.popup.geoThesTable.link" /> [Popup]"><script>document.write(message.get("ui.obj.spatial.geoThesTable.link"))</script></a></span>
		                <div class="tableContainer rows4 full">
		            	    <table id="spatialRefAdminUnit" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort interactive">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="label" dataType="String" width="315"><script>document.write(message.get("ui.obj.spatial.geoThesTable.header.name"))</script></th>
		                  			<th nosort="true" field="longitude1" dataType="String" width="90"><script>document.write(message.get("ui.obj.spatial.geoThesTable.header.longitude1"))</script></th>
		                  			<th nosort="true" field="latitude1" dataType="String" width="90"><script>document.write(message.get("ui.obj.spatial.geoThesTable.header.latitude1"))</script></th>
		                  			<th nosort="true" field="longitude2" dataType="String" width="90"><script>document.write(message.get("ui.obj.spatial.geoThesTable.header.longitude2"))</script></th>
		                  			<th nosort="true" field="latitude2" dataType="String" width="90"><script>document.write(message.get("ui.obj.spatial.geoThesTable.header.latitude2"))</script></th>
		            		      </tr>
		            	      </thead>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
					</span>
	          	  </div>

				<div class="inputContainer noSpaceBelow notRequired">
					<span id="uiElementN007" type="optional">
						<div id="spatialRefCoordsAdminUnit" class="infobox">
							<span class="icon" onclick="javascript:dialog.showContextHelp(arguments[0], 7012)"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span>
							<span class="title"><a href="javascript:toggleInfo('spatialRefCoordsAdminUnit');" title="<fmt:message key="general.info.open" />"><script>document.write(message.get("ui.obj.spatial.transformedCoordinates"))</script>
								<img id="spatialRefCoordsAdminUnitToggle" src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a>
							</span>
							<div id="spatialRefCoordsAdminUnitContent" style="display:block; height: 44px;">
								<div class="left" style="float:left; margin-top:10px;">
									<select id="spatialRefAdminUnitSelect" dojoType="ingrid:Select" toggle="plain" style="width:250px;">
			                    		<option value="GEO84"><fmt:message key="dialog.research.ext.obj.coordinates.wgs84" /></option>
			                    		<option value="GEO_BESSEL_POTSDAM"><fmt:message key="dialog.research.ext.obj.coordinates.geobp" /></option>
			                    		<option value="GK2"><fmt:message key="dialog.research.ext.obj.coordinates.gk2" /></option>
			                    		<option value="GK3"><fmt:message key="dialog.research.ext.obj.coordinates.gk3" /></option>
			                    		<option value="GK4"><fmt:message key="dialog.research.ext.obj.coordinates.gk4" /></option>
			                    		<option value="GK5"><fmt:message key="dialog.research.ext.obj.coordinates.gk5" /></option>
			                    		<option value="UTM32W"><fmt:message key="dialog.research.ext.obj.coordinates.utm32w" /></option>
			                    		<option value="UTM33W"><fmt:message key="dialog.research.ext.obj.coordinates.utm33w" /></option>
			                    		<option value="UTM32S"><fmt:message key="dialog.research.ext.obj.coordinates.utm32s" /></option>
			                    		<option value="UTM33S"><fmt:message key="dialog.research.ext.obj.coordinates.utm33s" /></option>
			                    		<option value="LAMGW"><fmt:message key="dialog.research.ext.obj.coordinates.lamgw" /></option>
			                    	</select>
			                    </div>
	
								<div class="headHiddenRows1" style="float: right;">
				              	    <table id="spatialRefAdminUnitCoords" dojoType="ingrid:FilteringTable" headClass="hidden" cellspacing="0" class="filteringTable nosort relativePos">
					              	    <thead>
					              	      <tr>
											<th nosort="true" field="longitude1" dataType="String">L&auml;nge 1</th>
											<th nosort="true" field="latitude1" dataType="String">Breite 1</th>
											<th nosort="true" field="longitude2" dataType="String">L&auml;nge 2</th>
											<th nosort="true" field="latitude2" dataType="String">Breite 2</th>
				              			  </tr>
				              	    	</thead>
									  	<colgroup>
										    <col width="120">
										    <col width="120">
										    <col width="120">
										    <col width="120">
										</colgroup>
					              	    <tbody>
											<tr value="0">
					              		        <td>&nbsp;</td>
					              		        <td>&nbsp;</td>
					              		        <td>&nbsp;</td>
					              		        <td>&nbsp;</td></tr>
										</tbody>
				              	    </table>
								</div>
							</div>
						</div>
					</span>
				</div>

	            <div id="spatialRefLocationContainer" class="inputContainer noSpaceBelow notRequired h130">
					<span id="uiElementN008" type="required">
		                <span id="spatialRefLocationLabel" class="label required"><label for="spatialRefLocation" onclick="javascript:dialog.showContextHelp(arguments[0], 10008)"><script>document.write(message.get("ui.obj.spatial.geoTable.title"))</script>*</label></span>
		                <span class="functionalLink">
		                	<img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="spatialRefLocationLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('dialog.spatialAssist.title'), 'mdek_spatial_assist_dialog.jsp', 555, 240, true);" title="<fmt:message key="dialog.popup.geoTable.link" /> [Popup]"><script>document.write(message.get("ui.obj.spatial.geoTable.link"))</script></a>
	<!-- The following feature is not yet implemented -->
	<!-- 
		                	<img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:void(0);" title="<fmt:message key="dialog.popup.geoSearch.link" /> [Popup]"><fmt:message key="dialog.popup.geoSearch.link" /></a>
	 -->
		                </span>
		                <div class="tableContainer rows4 full">
		                  <div class="cellEditors" id="spatialRefLocationEditors">
		                    <div dojoType="ingrid:ComboBox" toggle="plain" maxlength="60" style="width:300px;" listId="1100" id="freeReferencesEditor"></div>
		                    <div decimal="," dojoType="ingrid:RealNumberTextbox" widgetId="latitude1Editor"></div>
		                    <div decimal="," dojoType="ingrid:RealNumberTextbox" widgetId="longitude1Editor"></div>
		                    <div decimal="," dojoType="ingrid:RealNumberTextbox" widgetId="latitude2Editor"></div>
		                    <div decimal="," dojoType="ingrid:RealNumberTextbox" widgetId="longitude2Editor"></div>
		                  </div>
		            	    <table id="spatialRefLocation" dojoType="ingrid:FilteringTable" minRows="4" headClass="fixedHeader" cellspacing="0" class="filteringTable nosort interactive">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="name" dataType="String" width="315" editor="freeReferencesEditor"><script>document.write(message.get("ui.obj.spatial.geoTable.header.name"))</script></th>
		                  			<th nosort="true" field="longitude1" dataType="String" width="90" editor="longitude1Editor"><script>document.write(message.get("ui.obj.spatial.geoTable.header.longitude1"))</script></th>
		                  			<th nosort="true" field="latitude1" dataType="String" width="90" editor="latitude1Editor"><script>document.write(message.get("ui.obj.spatial.geoTable.header.latitude1"))</script></th>
		                  			<th nosort="true" field="longitude2" dataType="String" width="90" editor="longitude2Editor"><script>document.write(message.get("ui.obj.spatial.geoTable.header.longitude2"))</script></th>
		                  			<th nosort="true" field="latitude2" dataType="String" width="90" editor="latitude2Editor"><script>document.write(message.get("ui.obj.spatial.geoTable.header.latitude2"))</script></th>
		            		      </tr>
		            	      </thead>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
					</span>
	          	  </div>

				<div class="inputContainer noSpaceBelow notRequired">
					<span id="uiElementN009" type="optional">
						<div id="spatialRefCoordsLocation" class="infobox">
							<span class="icon" onclick="javascript:dialog.showContextHelp(arguments[0], 7013)"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span>
							<span class="title"><a href="javascript:toggleInfo('spatialRefCoordsLocation');" title="<fmt:message key="general.info.open" />"><script>document.write(message.get("ui.obj.spatial.transformedCoordinates"))</script>
								<img src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a>
							</span>
							<div id="spatialRefCoordsLocationContent" style="display:block; height: 44px;">
		                    	<div class="left" style="float:left; margin-top:10px;">
		                    		<select id="spatialRefLocationSelect" dojoType="ingrid:Select" toggle="plain" style="width:250px;">
			                    		<option value="GEO84"><fmt:message key="dialog.research.ext.obj.coordinates.wgs84" /></option>
			                    		<option value="GEO_BESSEL_POTSDAM"><fmt:message key="dialog.research.ext.obj.coordinates.geobp" /></option>
			                    		<option value="GK2"><fmt:message key="dialog.research.ext.obj.coordinates.gk2" /></option>
			                    		<option value="GK3"><fmt:message key="dialog.research.ext.obj.coordinates.gk3" /></option>
			                    		<option value="GK4"><fmt:message key="dialog.research.ext.obj.coordinates.gk4" /></option>
			                    		<option value="GK5"><fmt:message key="dialog.research.ext.obj.coordinates.gk5" /></option>
			                    		<option value="UTM32W"><fmt:message key="dialog.research.ext.obj.coordinates.utm32w" /></option>
			                    		<option value="UTM33W"><fmt:message key="dialog.research.ext.obj.coordinates.utm33w" /></option>
			                    		<option value="UTM32S"><fmt:message key="dialog.research.ext.obj.coordinates.utm32s" /></option>
			                    		<option value="UTM33S"><fmt:message key="dialog.research.ext.obj.coordinates.utm33s" /></option>
			                    		<option value="LAMGW"><fmt:message key="dialog.research.ext.obj.coordinates.lamgw" /></option>
			                    	</select>
			                    </div>
	
								<div class="headHiddenRows1" style="float: right;">
				              	    <table id="spatialRefLocationCoords" dojoType="ingrid:FilteringTable" headClass="hidden" cellspacing="0" class="filteringTable nosort relativePos">
				              	      <thead>
				              		      <tr>
				                    			<th nosort="true" field="longitude1" dataType="String" width="90">L&auml;nge 1</th>
				                    			<th nosort="true" field="latitude1" dataType="String" width="90">Breite 1</th>
				                    			<th nosort="true" field="longitude2" dataType="String" width="90">L&auml;nge 2</th>
				                    			<th nosort="true" field="latitude2" dataType="String" width="90">Breite 2</th>
				              		      </tr>
				              	      </thead>
									  <colgroup>
									    <col width="120">
									    <col width="120">
									    <col width="120">
									    <col width="120">
									  </colgroup>
				              	      <tbody>
				              		      <tr value="0">
				              		        <td>&nbsp;</td>
				              		        <td>&nbsp;</td>
				              		        <td>&nbsp;</td>
				              		        <td>&nbsp;</td></tr>
				              	      </tbody>
				              	    </table>
		              	    	</div>
							</div>
		                </div>
					</span>
				</div>

                  <div id="ref1SpatialSystemContainer" class="inputContainer notRequired">
                    <span id="uiElement3500" type="optional">
                        <span id="ref1SpatialSystemLabel" class="label"><label for="ref1SpatialSystem" onclick="javascript:dialog.showContextHelp(arguments[0], 3500)"><script>document.write(message.get("ui.obj.type1.spatialSystem"))</script></label></span>
                        <span class="input"><input dojoType="ingrid:ComboBox" maxlength="120" autoComplete="false" style="width:649px;" listId="100" id="ref1SpatialSystem" /></span>
                    </span>
                  </div>

				<div class="inputContainer noSpaceBelow notRequired">
	                <div class="half left">
						<span id="uiElementN010" type="optional">
		                  <span id="spatialRefAltHeightLabel" class="label"><label for="spatialRefAltMin" onclick="javascript:dialog.showContextHelp(arguments[0], 4520)"><script>document.write(message.get("ui.obj.spatial.height"))</script></label></span>
		                  <div id="spatialRefAltitude">
		                    <span class="entry">
								<span id="uiElement1130" type="optional">
			                      <span id="spatialRefAltMinLabel" class="label"><label for="spatialRefAltMin" onclick="javascript:dialog.showContextHelp(arguments[0], 1130)"><script>document.write(message.get("ui.obj.spatial.height.min"))</script></label></span>
			                      <span class="input"><input type="text" id="spatialRefAltMin" name="spatialRefAltMin" class="w080" dojoType="RealNumberTextBox" /></span>
								</span>
		                    </span>
		                    <span class="entry">
								<span id="uiElement5020" type="optional">
			                      <span id="spatialRefAltMaxLabel" class="label"><label for="spatialRefAltMax" onclick="javascript:dialog.showContextHelp(arguments[0], 5020)"><script>document.write(message.get("ui.obj.spatial.height.max"))</script></label></span>
			                      <span class="input"><input type="text" id="spatialRefAltMax" name="spatialRefAltMax" class="w080" dojoType="RealNumberTextBox" /></span>
								</span>
		                    </span>
		                    <span class="entry">
								<span id="uiElement5021" type="optional">
			                      <span id="spatialRefAltMeasureLabel" class="label"><label for="spatialRefAltMeasure" onclick="javascript:dialog.showContextHelp(arguments[0], 5021)"><script>document.write(message.get("ui.obj.spatial.height.unit"))</script></label></span>
			                      <span class="input"><input dojoType="ingrid:Select" style="width:58px;" listId="102" id="spatialRefAltMeasure" /></span>
								</span>
		                    </span>
		                    <span class="entry">
								<span id="uiElement5022" type="optional">
			                      <span id="spatialRefAltVDateLabel" class="label"><label for="spatialRefAltVDate" onclick="javascript:dialog.showContextHelp(arguments[0], 5022)"><script>document.write(message.get("ui.obj.spatial.height.geodeticSystem"))</script></label></span>
			                      <span class="input"><input dojoType="ingrid:Select" style="width:266px;" listId="101" id="spatialRefAltVDate" /></span>
								</span>
		                    </span>
						  </div>
						</span>
					</div>

					<div class="half">
						<span id="uiElement1140" type="optional">
							<span class="label"><label for="spatialRefExplanation" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)"><script>document.write(message.get("ui.obj.spatial.description"))</script></label></span>
							<span class="input"><input type="text" mode="textarea" id="spatialRefExplanation" name="spatialRefExplanation" class="w320 h118" dojoType="ingrid:ValidationTextbox" /></span>
						</span> 
					</div>
					<div class="fill"></div>
				</div>
	          
			</div>

		</div>

	          <!-- ZEITBEZUG //-->
	          <div id="timeRef" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('timeRef');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7002)"><script>document.write(message.get("ui.obj.time.title"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="timeRefContent" class="content">


	              <div class="inputContainer noSpaceBelow">
	                <div class="half left">
						<span id="uiElement5030" type="required">
		                  <span id="timeRefTableLabel" class="label required"><label for="timeRefTable" onclick="javascript:dialog.showContextHelp(arguments[0], 5030)"><script>document.write(message.get("ui.obj.time.timeRefTable.title"))</script>*</label></span>
		                  <div class="tableContainer rows4 half">
		                    <div class="cellEditors" id="timeRefTableEditors">
		                      <div dojoType="ingrid:DropdownDatePicker" displayFormat="dd.MM.yyyy" toggle="plain" widgetId="timeRefDateDatePicker"></div>
		                      <div dojoType="ingrid:Select" toggle="plain" style="width:155px;" listId="502" id="timeRefTypeCombobox"></div>
		                    </div>
		              	    <table id="timeRefTable" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y" minRows="4" cellspacing="0" class="filteringTable interactive nosort">
		              	      <thead>
		              		      <tr>
		                    			<th nosort="true" field="date" dataType="Date" width="120" editor="timeRefDateDatePicker"><script>document.write(message.get("ui.obj.time.timeRefTable.header.date"))</script></th>
		                    			<th nosort="true" field="type" dataType="String" width="200" editor="timeRefTypeCombobox"><script>document.write(message.get("ui.obj.time.timeRefTable.header.type"))</script></th>
		              		      </tr>
		              	      </thead>
		              	      <tbody>
		              	      </tbody>
		              	    </table>
		                  </div>
		                 </span>
	            	  </div>
	          
	                <div class="half">
						<span id="uiElement1250" type="optional">
		                  <span class="label"><label for="timeRefExplanation" onclick="javascript:dialog.showContextHelp(arguments[0], 1250)"><script>document.write(message.get("ui.obj.time.description"))</script></label></span>
	               		  <span class="input"><input type="text" mode="textarea" id="timeRefExplanation" name="timeRefExplanation" class="w320 h105" dojoType="ingrid:ValidationTextbox" /></span>
	               		</span> 
	                </div>

	                <div class="fill"></div>
	              </div>
	        	  
	              <div class="inputContainer notRequired">
	                <div class="half left">
						<span id="uiElementN011" type="optional">
		                  <span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 10011)"><script>document.write(message.get("ui.obj.time.timeRefContent"))</script></label></span>
		                  <div id="timeRefRef">
		                    <span class="entry first">
		                      <span class="label hidden"><label for="timeRefType">Typ</label></span>
	                		  <span class="input">
	                		  	<select dojoType="ingrid:Select" autoComplete="false" style="width:61px;" id="timeRefType">
	                		  	<option value="am"><fmt:message key="dialog.research.ext.obj.content.time.at" /></option>
	                		  	<option value="seit"><fmt:message key="dialog.research.ext.obj.content.time.since" /></option>
	                		  	<option value="bis"><fmt:message key="dialog.research.ext.obj.content.time.until" /></option>
	                		  	<option value="von"><fmt:message key="dialog.research.ext.obj.content.time.fromto" /></option>
	                		  	</select>
	                		  </span>
		                    </span>
		                    <span class="entry">
		                      <span class="label hidden"><label for="timeRefDate1">Datum 1 [TT.MM.JJJJ]</label></span>
		                      <span class="input"><div dojoType="ingrid:DropdownDatePicker" id="timeRefDate1"  displayFormat="dd.MM.yyyy" name="timeRefDate1"></div><br />TT.MM.JJJJ</span>
		                    </span>
		                    <span class="entry last" id="timeRefDate2Editor">
		                      <span class="label hidden"><label for="timeRefDate2">Datum 2 [TT.MM.JJJJ]</label></span>
		                      <span class="input"><div dojoType="ingrid:DropdownDatePicker" id="timeRefDate2" displayFormat="dd.MM.yyyy" name="timeRefDate2"></div><br />TT.MM.JJJJ</span>
		                    </span>
		                  </div>
	                	</span>
	                </div>

	                <div class="half">
					  <span id="uiElement1220" type="optional">
		                  <span class="label"><label for="timeRefStatus" onclick="javascript:dialog.showContextHelp(arguments[0], 1220)"><script>document.write(message.get("ui.obj.time.state"))</script></label></span>
		                  <span class="input"><input dojoType="ingrid:Select" style="width:301px;" listId="523" id="timeRefStatus" /></span>
	                  </span>
	                </div>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
					  <span id="uiElement1240" type="optional">
	                  	<span class="label"><label for="timeRefPeriodicity" onclick="javascript:dialog.showContextHelp(arguments[0], 1240)"><script>document.write(message.get("ui.obj.time.periodicity"))</script></label></span>
	                  	<span class="input"><input dojoType="ingrid:Select" style="width:302px;" listId="518" id="timeRefPeriodicity" /></span>
	                  </span>
	                </div>
	          
	                <div class="half">
					  <span id="uiElement1230" type="optional">
		                  <span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 1230)"><script>document.write(message.get("ui.obj.time.interval"))</script></label></span>
		                  <div id="timeRefInterval">
		                    <span><script>document.write(message.get("ui.obj.time.interval.each"))</script></span>
		                    <span class="label hidden"><label for="timeRefIntervalNum">Intervall Anzahl</label></span>
		                    <span class="input"><input type="text" id="timeRefIntervalNum" maxlength="40" name="timeRefIntervalNum" class="w038" dojoType="ingrid:ValidationTextBox" /></span>
		                    <span class="label hidden"><label for="timeRefIntervalUnit">Intervall Einheit</label></span>
		                    <span class="input"><input dojoType="ingrid:Select" style="width:223px;" listId="1230" id="timeRefIntervalUnit" /></span>
		                  </div>
						</span>
	                </div>
	                <div class="fill"></div>
	              </div>

	            </div>
	          </div>

	          <!-- ZUSATZINFORMATION //-->
	          <div id="extraInfo" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('extraInfo');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7003)"><script>document.write(message.get("ui.obj.additionalInfo.title"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="extraInfoContent" class="content">
	        	  
	              <div class="inputContainer">
	                <div class="half left">
					  <span id="uiElement5041" type="required">
		                  <span id="extraInfoLangMetaDataLabel" class="label required"><label for="extraInfoLangMetaData" onclick="javascript:dialog.showContextHelp(arguments[0], 5041)"><script>document.write(message.get("ui.obj.additionalInfo.language.metadata"))</script>*</label></span>
		                  <span class="input"><input dojoType="ingrid:Select" autoComplete="false" style="width:302px;" listId="99999999" id="extraInfoLangMetaData" /></span>
		              </span>
	                </div>
	          
	                <div class="half">
					  <span id="uiElement5042" type="required">
		                  <span id="extraInfoLangDataLabel" class="label required"><label for="extraInfoLangData" onclick="javascript:dialog.showContextHelp(arguments[0], 5042)"><script>document.write(message.get("ui.obj.additionalInfo.language.data"))</script>*</label></span>
		                  <span class="input"><input dojoType="ingrid:Select" autoComplete="false" style="width:302px;" listId="99999999" id="extraInfoLangData" /></span>
		              </span>
	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer">
	                <div class="half left">
					  <span id="uiElement3571" type="required">
		                  <span class="label required"><label id="extraInfoPublishAreaLabel" for="extraInfoPublishArea" onclick="javascript:dialog.showContextHelp(arguments[0], 3571)"><script>document.write(message.get("ui.obj.additionalInfo.publicationCondition"))</script>*</label></span>
		                  <span class="input"><input dojoType="ingrid:Select" autoComplete="false" style="width:302px;" listId="3571" id="extraInfoPublishArea" /></span>
					  </span>
 	                </div>

                    <div id="extraInfoCharSetDataContainer" class="half">
                      <span id="uiElement5043" type="required">
                          <span id="extraInfoCharSetDataLabel" class="label required"><label for="extraInfoCharSetData" onclick="javascript:dialog.showContextHelp(arguments[0], 5043)"><script>document.write(message.get("ui.obj.additionalInfo.charSet.data"))</script>*</label></span>
                          <span class="input"><input dojoType="ingrid:Select" autoComplete="false" style="width:302px;" listId="510" id="extraInfoCharSetData" /></span>
                      </span>
                    </div>
	                <div class="fill"></div>
	              </div>

	              <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected object class -->
	              <div id="extraInfoConformityTableContainer" class="inputContainer" displaytype="exclude">
				  	<span id="uiElementN024" type="required">
		                <span id="extraInfoConformityTableLabel" class="label required"><label for="extraInfoConformityTable" onclick="javascript:dialog.showContextHelp(arguments[0], 10024)"><script>document.write(message.get("ui.obj.additionalInfo.conformityTable.title"))</script>*</label></span>

		                <div class="tableContainer rows4 full">
		                    <div class="cellEditors" id="extraInfoConformityTableEditors">
		                      <div dojoType="ingrid:Select" toggle="plain" style="width:130px;" listId="6000" id="extraInfoConformityLevelEditor"></div>
		                      <div dojoType="ingrid:ValidationTextbox" maxlength="255" class="w405" widgetId="extraInfoConformitySpecificationEditor"></div>
		                      <div dojoType="ingrid:DropdownDatePicker" displayFormat="dd.MM.yyyy" toggle="plain" widgetId="extraInfoConformityDatePicker"></div>
		                    </div>
		            	    <table id="extraInfoConformityTable" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y" minRows="4" cellspacing="0" class="filteringTable nosort interactive">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="level" dataType="String" editor="extraInfoConformityLevelEditor"><script>document.write(message.get("ui.obj.additionalInfo.conformityTable.header.level"))</script></th>
		                  			<th nosort="true" field="specification" dataType="String" editor="extraInfoConformitySpecificationEditor"><script>document.write(message.get("ui.obj.additionalInfo.conformityTable.header.specification"))</script></th>
	                    			<th nosort="true" field="date" dataType="Date" width="120" editor="extraInfoConformityDatePicker"><script>document.write(message.get("ui.obj.additionalInfo.conformityTable.header.date"))</script></th>
		            		      </tr>
		            	      </thead>
							  <colgroup>
							    <col width="150">
							    <col width="414">
							    <col width="100">
							  </colgroup>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
					</span>
	          	  </div>

	              <div class="inputContainer notRequired h110">
	                <div class="third1 left">
					  <span id="uiElementN012" type="optional">
		                  <span class="label"><label for="extraInfoXMLExportTable" onclick="javascript:dialog.showContextHelp(arguments[0], 1370)"><script>document.write(message.get("ui.obj.additionalInfo.xmlExportCriteria"))</script></label></span>
		                  <div class="tableContainer headHiddenRows4 third1">
		                    <div class="cellEditors" id="extraInfoXMLExportTableEditors">
		                      <div dojoType="ingrid:ComboBox" maxlength="80" toggle="plain" style="width:161px;" listId="1370" id="extraInfoXMLExportTableEditor"></div>
		                    </div>
		              	    <table id="extraInfoXMLExportTable" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
		              	      <thead>
		              		      <tr>
		                    			<th nosort="true" field="title" dataType="String" editor="extraInfoXMLExportTableEditor">XML-Export-Kriterium</th>
		              		      </tr>
		              	      </thead>
		              	      <tbody>
		              	      </tbody>
		              	    </table>
		                  </div>
						</span>
					</div>
	          
	                <div class="third2">
					  <span id="uiElement1350" type="optional">
		                  <span class="label"><label for="extraInfoLegalBasicsTable" onclick="javascript:dialog.showContextHelp(arguments[0], 1350)"><script>document.write(message.get("ui.obj.additionalInfo.legalBasis"))</script></label></span>
		                  <div class="tableContainer headHiddenRows4 third2">
		                    <div class="cellEditors" id="extraInfoLegalBasicsTableEditors">
		                      <div dojoType="ingrid:ComboBox" maxlength="120" toggle="plain" style="width:397px;" listId="1350" id="extraInfoLegalBasicsTableEditor"></div>
		                    </div>
		              	    <table id="extraInfoLegalBasicsTable" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
		              	      <thead>
		              		      <tr>
		                    			<th nosort="true" field="title" dataType="String" editor="extraInfoLegalBasicsTableEditor">Rechtliche Grundlagen</th>
		              		      </tr>
		              	      </thead>
		              	      <tbody>
		              	      </tbody>
		              	    </table>
		                  </div>
		                </span>
	            	  </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <div class="half left">
						<span id="uiElementN013" type="optional">
							<span class="label"><label for="extraInfoPurpose" onclick="javascript:dialog.showContextHelp(arguments[0], 10013)"><script>document.write(message.get("ui.obj.additionalInfo.purpose"))</script></label></span>
							<span class="input"><input type="text" mode="textarea" id="extraInfoPurpose" name="extraInfoPurpose" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span> 
	                	</span>
	                </div>
	          
	                <div class="half">
						<span id="uiElement5040" type="optional">
							<span class="label"><label for="extraInfoUse" onclick="javascript:dialog.showContextHelp(arguments[0], 5040)"><script>document.write(message.get("ui.obj.additionalInfo.suitability"))</script></label></span>
							<span class="input"><input type="text" mode="textarea" id="extraInfoUse" name="extraInfoUse" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span>
						</span> 
	                </div>
	                <div class="fill"></div>
	              </div>

	            </div>
	          </div>

	          <!-- VERFÜGBARKEIT //-->
	          <div id="availability" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('availability');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7004)"><script>document.write(message.get("ui.obj.availability.title"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="availabilityContent" class="content">

                  <div class="inputContainer h116">
                    <div class="half left">
                      <span id="uiElementN025" type="required">
                          <span id="availabilityAccessConstraintsLabel" class="label required"><label for="availabilityAccessConstraints" onclick="javascript:dialog.showContextHelp(arguments[0], 10025)"><script>document.write(message.get("ui.obj.availability.accessConstraints"))</script>*</label></span>
                          <div class="tableContainer headHiddenRows4 half">
                            <div class="cellEditors" id="availabilityAccessConstraintsEditors">
                              <div dojoType="ingrid:Select" toggle="plain" style="width:260px;" listId="6010" id="availabilityAccessConstraintsEditor"></div>
                            </div>
                            <table id="availabilityAccessConstraints" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                        <th nosort="true" field="title" dataType="String" editor="availabilityAccessConstraintsEditor">Zugangsbeschr&auml;nkungen</th>
                                  </tr>
                              </thead>
                              <tbody>
                              </tbody>
                            </table>
                          </div>
                        </span>
                      </div>

                    <div class="half">
                      <span id="uiElementN026" type="required">
                          <span id="availabilityUseConstraintsLabel" class="label required"><label for="availabilityUseConstraints" onclick="javascript:dialog.showContextHelp(arguments[0], 10026)"><script>document.write(message.get("ui.obj.availability.useConstraints"))</script>*</label></span>
                          <div class="tableContainer headHiddenRows4 half">
                            <div class="cellEditors" id="availabilityUseConstraintsEditors">
                              <div dojoType="ingrid:ValidationTextbox" class="w296" widgetId="availabilityUseConstraintsEditor"></div>
                            </div>
                            <table id="availabilityUseConstraints" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
                              <thead>
                                  <tr>
                                        <th nosort="true" field="title" dataType="String" editor="availabilityUseConstraintsEditor">Nutzungsbedingungen</th>
                                  </tr>
                              </thead>
                              <tbody>
                              </tbody>
                            </table>
                          </div>
                        </span>
                      </div>
                  </div>

                  <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected INSPIRE theme -->
                  <div id="availabilityDataFormatInspireContainer" class="inputContainer notRequired" displaytype="exclude">
                        <span id="uiElement1315" type="optional">
                            <span id="availabilityDataFormatInspireLabel" class="label"><label for="availabilityDataFormatInspire" onclick="javascript:dialog.showContextHelp(arguments[0], 1315)"><script>document.write(message.get("ui.obj.availability.dataFormatInspire"))</script></label></span>
                            <span class="input spaceBelow">
                                <div dojoType="ingrid:ComboBox" maxlength="255" style="width:649px;" id="availabilityDataFormatInspire" listId="6300"></div>
                            </span>
                        </span>
                  </div>

	              <div class="inputContainer notRequired h130">
					<span id="uiElement1320" type="optional">
		                <span id="availabilityDataFormatLabel" class="label"><label for="availabilityDataFormat" onclick="javascript:dialog.showContextHelp(arguments[0], 1320)"><script>document.write(message.get("ui.obj.availability.dataFormatTable.title"))</script></label></span>
		                <div class="tableContainer rows4 full">
		                  <div class="cellEditors" id="availabilityDataFormatEditors">
	                        <div dojoType="ingrid:ComboBox" maxlength="80" toggle="plain" style="width:150px;" listId="1320" widgetId="availabilityDataFormatName"></div>
		                    <div dojoType="ingrid:ValidationTextbox" maxlength="40" widgetId="availabilityDataFormatVersion"></div>
		                    <div dojoType="ingrid:ValidationTextbox" maxlength="80" widgetId="availabilityDataFormatCompression"></div>
		                    <div dojoType="ingrid:ValidationTextbox" maxlength="80" widgetId="availabilityDataFormatPixelDepth"></div>
		                  </div>
		            	    <table id="availabilityDataFormat" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable interactive nosort">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="name" dataType="String" width="170" editor="availabilityDataFormatName"><script>document.write(message.get("ui.obj.availability.dataFormatTable.header.name"))</script></th>
		                  			<th nosort="true" field="version" dataType="String" width="125" editor="availabilityDataFormatVersion"><script>document.write(message.get("ui.obj.availability.dataFormatTable.header.version"))</script></th>
		                  			<th nosort="true" field="compression" dataType="String" width="205" editor="availabilityDataFormatCompression"><script>document.write(message.get("ui.obj.availability.dataFormatTable.header.compression"))</script></th>
		                  			<th nosort="true" field="pixelDepth" dataType="String" width="175" editor="availabilityDataFormatPixelDepth"><script>document.write(message.get("ui.obj.availability.dataFormatTable.header.depth"))</script></th>
		            		      </tr>
		            	      </thead>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
					</span>
	              </div>

	              <div class="inputContainer notRequired h130">
					<span id="uiElement1310" type="optional">
		                <span class="label"><label for="availabilityMediaOptions" onclick="javascript:dialog.showContextHelp(arguments[0], 1310)"><script>document.write(message.get("ui.obj.availability.mediaOptionTable.title"))</script></label></span>
		                <div class="tableContainer rows4 full">
		                  <div class="cellEditors" id="availabilityMediaOptionsEditors">
		                    <div dojoType="ingrid:Select" toggle="plain" style="width:117px;" listId="520" id="availabilityMediaOptionsMediumCombobox"></div>
		                    <div decimal="," dojoType="ingrid:RealNumberTextbox" widgetId="availabilityMediaOptionsSize"></div>
		                    <div dojoType="ingrid:ValidationTextbox" maxlength="255" widgetId="availabilityMediaOptionsLocation"></div>
		                  </div>
		            	    <table id="availabilityMediaOptions" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort interactive">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="name" dataType="String" width="150" editor="availabilityMediaOptionsMediumCombobox"><script>document.write(message.get("ui.obj.availability.mediaOptionTable.header.type"))</script></th>
		                  			<th nosort="true" field="transferSize" dataType="String" width="250" editor="availabilityMediaOptionsSize"><script>document.write(message.get("ui.obj.availability.mediaOptionTable.header.amount"))</script></th>
		                  			<th nosort="true" field="location" dataType="String" width="275" editor="availabilityMediaOptionsLocation"><script>document.write(message.get("ui.obj.availability.mediaOptionTable.header.location"))</script></th>
		            		      </tr>
		            	      </thead>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
					</span>
	              </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <div class="full">
						<span id="uiElement5052" type="optional">
							<span class="label"><label for="availabilityOrderInfo" onclick="javascript:dialog.showContextHelp(arguments[0], 5052)"><script>document.write(message.get("ui.obj.availability.orderInfo"))</script></label></span>
							<span class="input"><input type="text" mode="textarea" id="availabilityOrderInfo" name="availabilityOrderInfo" class="w668 h038" dojoType="ingrid:ValidationTextbox" /></span> 
						</span>
	                </div>
	                <div class="fill"></div>
	              </div>

	            </div>
	          </div>

	          <!-- VERSCHLAGWORTUNG //-->
	          <div id="thesaurus" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('thesaurus');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7005)"><script>document.write(message.get("ui.obj.thesaurus.title"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="thesaurusContent" class="content">
	        	  
	              <div id="thesaurusTopicsContainer" class="inputContainer h116">
					  <span id="uiElement5060" type="required">
		                  <span id="thesaurusTopicsLabel" class="label required"><label for="thesaurusTopics" onclick="javascript:dialog.showContextHelp(arguments[0], 5060)"><script>document.write(message.get("ui.obj.thesaurus.terms.category"))</script>*</label></span>
		                  <div class="tableContainer headHiddenRows4 full">
		                    <div class="cellEditors" id="thesaurusTopicsEditors">
		                      <div dojoType="ingrid:Select" toggle="plain" autoComplete="false" style="width:600px;" listId="527" id="thesaurusTopicsCombobox"></div>
		                    </div>
		              	    <table id="thesaurusTopics" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
		              	      <thead>
		              		      <tr>
		                    			<th nosort="true" field="title" dataType="String" editor="thesaurusTopicsCombobox">Themenkategorie</th>
		              		      </tr>
		              	      </thead>
		              	      <tbody>
		              	      </tbody>
		              	    </table>
		                  </div>
		                </span>
	              </div>

	              <div class="inputContainer h110">
					<span id="uiElement1409" type="optional">
		                <span id="thesaurusTermsLabel" class="label"><label for="thesaurusTerms" onclick="javascript:dialog.showContextHelp(arguments[0], 4510)"><script>document.write(message.get("ui.obj.thesaurus.terms"))</script></label></span>
		                <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="thesaurusTermsLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('dialog.thesaurusAssist.title'), 'mdek_thesaurus_assist_dialog.jsp', 735, 430, true);" title="<fmt:message key="dialog.popup.thesaurus.terms.link.assistant" /> [Popup]"><script>document.write(message.get("ui.obj.thesaurus.terms.link.assistant"))</script></a>
		                  <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="thesaurusTermsNavigatorLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(message.get('dialog.thesaurusNavigator.title'), 'mdek_thesaurus_dialog.jsp', 1010, 430, true, {dstTable: 'thesaurusTerms'});" title="<fmt:message key="dialog.popup.thesaurus.terms.link.navigator" /> [Popup]"><script>document.write(message.get("ui.obj.thesaurus.terms.link.navigator"))</script></a></span>
		                <div class="tableContainer headHiddenRows4 full">
		            	    <table id="thesaurusTerms" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
		            	      <thead>
		            		      <tr>
		                  			<th nosort="true" field="label" dataType="String">&nbsp;</th>
		                  			<th nosort="true" field="sourceString" dataType="String">&nbsp;</th>
		            		      </tr>
		            	      </thead>
							  <colgroup>
							    <col width="90%">
							    <col width="10%">
							  </colgroup>
		            	      <tbody>
		            	      </tbody>
		            	    </table>
		                </div>
					</span>
	              </div>

					<div class="inputContainer">
					    <span id="thesaurusTermsAddLabel" class="label"><label for="thesaurusFreeTerms" onclick="javascript:dialog.showContextHelp(arguments[0], 1410)"><script>document.write(message.get("ui.obj.thesaurus.terms.add.label"))</script></label></span>
						<div class="full">
		          			<div class="input">
								<span id="uiElement1410" type="optional">
									<input type="text" mode="textarea" maxlength="255" id="thesaurusFreeTerms" class="w585 h055" style="float: left !important;" dojoType="ingrid:ValidationTextBox" />
	                  				<div style="position:relative; height:0px; top: 38px; width:78px; float:right;">
										<button id="thesaurusFreeTermsAddButton" dojoType="ingrid:Button"><script>document.write(message.get("ui.obj.thesaurus.terms.custom.buttonAdd"))</script></button>
					  				</div>
		          				</span>
		          			</div>
			        		<div class="fill"></div>
		        		</div>
		      		</div>

	              <div class="inputContainer noSpaceBelow notRequired h185">
					<span id="uiElementN014" type="optional">
	                	<span id="thesaurusEnvironmentLabel" class="label"><label for="thesaurusEnvironment" onclick="javascript:dialog.showContextHelp(arguments[0], 10014)"><script>document.write(message.get("ui.obj.thesaurus.terms.enviromental.title"))</script></label></span>
		                <div id="thesaurusEnvironment" class="outlined h140">
	                  <div class="checkboxContainer">
	                    <span class="input"><input type="checkbox" name="thesaurusEnvExtRes" id="thesaurusEnvExtRes" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 7006)"><script>document.write(message.get("ui.obj.thesaurus.terms.enviromental.displayCatalogPage"))</script></label></span>
	                  </div>
	                  
	                  <div class="halfInside left">
						<span id="uiElementN015" type="optional">
		                    <span id="thesaurusEnvTopicsLabel" class="label"><label for="thesaurusEnvTopics" onclick="javascript:dialog.showContextHelp(arguments[0], 10015)"><script>document.write(message.get("ui.obj.thesaurus.terms.enviromental.topics"))</script></label></span>
		                    <div class="tableContainer headHiddenRows4 halfInside">
		                      <div class="cellEditors" id="thesaurusEnvTopicsEditors">
		                        <div dojoType="ingrid:Select" toggle="plain" style="width:240px;" listId="1410" id="thesaurusEnvTopicsCombobox"></div>
		                      </div>
		                	    <table id="thesaurusEnvTopics" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
		                	      <thead>
		                		      <tr>
		                      			<th nosort="true" field="title" dataType="String" editor="thesaurusEnvTopicsCombobox">Umweltthemen</th>
		                		      </tr>
		                	      </thead>
		                	      <tbody>
		                	      </tbody>
		                	    </table>
		                    </div>
		              	</span>
	                  </div>

	                  <div class="halfInside">
						<span id="uiElementN016" type="optional">
		                    <span id="thesaurusEnvCatsLabel" class="label"><label for="thesaurusEnvCats" onclick="javascript:dialog.showContextHelp(arguments[0], 10016)"><script>document.write(message.get("ui.obj.thesaurus.terms.enviromental.categories"))</script></label></span>
		                    <div class="tableContainer headHiddenRows4 halfInside">
		                      <div class="cellEditors" id="thesaurusEnvCatsEditors">
		                        <div dojoType="ingrid:Select" toggle="plain" style="width:240px;" listId="1400" id="thesaurusEnvCatsCombobox"></div>
		                      </div>
		                	    <table id="thesaurusEnvCats" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
		                	      <thead>
		                		      <tr>
		                      			<th nosort="true" field="title" dataType="String" editor="thesaurusEnvCatsCombobox">Umweltkategorien</th>
		                		      </tr>
		                	      </thead>
		                	      <tbody>
		                	      </tbody>
		                	    </table>
		                    </div>
		                 </span>
	                  </div>
	                  <div class="fill"></div>
	                </div>
					</span>
	              </div>

	            </div>
	          </div>

	          <!-- VERWEISE //-->
	          <div id="links" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('links');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7007)"><script>document.write(message.get("ui.obj.links.title"))</script></div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="linksContent" class="content">

	              <div class="inputContainer noSpaceBelow notRequired h164">
	                <div class="half left">
						<span id="uiElementN017" type="optional">
	                  		<span class="label"><label for="linksTo" onclick="javascript:dialog.showContextHelp(arguments[0], 1500)"><script>document.write(message.get("ui.obj.links.linksTo.title"))</script></label></span>
	                  		<span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage(message.get('ui.obj.links.linksTo.link'), 'mdek_links_dialog.jsp', 1010, 680, true);" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><script>document.write(message.get("ui.obj.links.linksTo.link"))</script></a></span>
	                  		<div class="tableContainer headHiddenRows6 half">
	              	    		<table id="linksTo" dojoType="ingrid:FilteringTable" minRows="6" headClass="hidden" cellspacing="0" class="filteringTable nosort">
	              	      			<thead>
	              		      			<tr>
	                    					<th nosort="true" field="icon" dataType="String"></th>
	                    					<th nosort="true" field="linkLabel" dataType="String">Objekte</th>
	              		      			</tr>
	              	      			</thead>
						  			<colgroup>
						    			<col width="23">
						    			<col width="297">
						  			</colgroup>
	              	      			<tbody>
	              	      			</tbody>
	              	    		</table>
	                  		</div>
	                  	</span>
	            	  </div>

	                <div class="half">
						<span id="uiElementN018" type="optional">
		                  <span class="label"><label for="thesaurusEnvCats" onclick="javascript:dialog.showContextHelp(arguments[0], 1510)"><script>document.write(message.get("ui.obj.links.linksFrom.title"))</script></label></span>
		                  <div class="tableContainer headHiddenRows6 half">
		              	    <table id="linksFrom" dojoType="ingrid:FilteringTable" minRows="6" headClass="hidden" cellspacing="0" class="filteringTable nosort">
		              	      <thead>
		              		      <tr>
		                    			<th nosort="true" field="icon" dataType="String"></th>
		                    			<th nosort="true" field="linkLabel" dataType="String">Objekte</th>
		              		      </tr>
		              	      </thead>
							  <colgroup>
							    <col width="23">
							    <col width="297">
							  </colgroup>
		              	      <tbody>
		              	      </tbody>
		              	    </table>
		                  </div>
						 </span>
	            	  </div>
	              <div class="fill"></div>
	              </div>

	            </div>
	          </div>


				<!-- Zusaetzliche Felder -->
				<div id="additionalFields" class="contentBlock">
					<div class="titleBar">
						<div class="titleIcon"><a href="javascript:toggleFields('additionalFields');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
						<div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7075)"><script>document.write(message.get("ui.obj.additionalFields.title"))</script></div>
						<div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
					</div>
					<div id="additionalFieldsContent" class="content">

						<div id="additionalFieldsContainer" class="inputContainer notRequired">
							<!-- Additional fields are attached on init via 'addAdditionalFieldsToDocument' -->
						</div>

					</div>
				</div>
			</div>

	        </form>
	      </div>   
	  </div>
	  <!-- END CONTENT OBJECT -->
	  
	  <!-- START CONTENT ADDRESS -->
	  <div id="contentAddress" style="display:block">
		<div dojoType="ContentPane" widgetId="headerFrameAddress" id="sectionTopAddress" class="sectionTop">
			<table cellspacing="0">
		  	<tbody>
		  		<tr>
		  		  <td class="label"><label for="addressTitle" onclick="javascript:dialog.showContextHelp(arguments[0], 7044, 'Adresstitel')"><script>document.write(message.get("ui.adr.header.addressTitle"))</script></label></td>
		  		  <td colspan="2"><input type="text" id="addressTitle" name="addressTitle" class="w550" disabled="true" dojoType="ingrid:ValidationTextBox" /></td></tr>
		  		<tr>
		  		  <td id="addressTypeLabel" class="label col1"><label for="addressType" onclick="javascript:dialog.showContextHelp(arguments[0], 7045, 'Adresstyp')"><script>document.write(message.get("ui.adr.header.addressType"))</script></label></td>
		  		  <td class="col2">

				  <input type="text" id="addressType" class="w405" disabled="true" dojoType="ingrid:ValidationTextBox" />
<!-- 
		          <select dojoType="ingrid:Select" autoComplete="false" style="width:386px;" disabled="true" id="addressType" name="addressType">
		            <option value="AddressType0">Institution</option>
		            <option value="AddressType1">Einheit</option>
		            <option value="AddressType2">Person</option>
		            <option value="AddressType3">Freie Adresse</option>
		          </select>
 -->
		        </td>
		  		  <td class="col3"><img id="permissionAdrLock" src="img/lock.gif" width="9" height="14" alt="gesperrt" /></td></tr>
		  		<tr>
		  		  <td id="addressOwnerLabel" class="label required"><label for="addressOwner" onclick="javascript:dialog.showContextHelp(arguments[0], 1030, 'Verantwortlicher')"><script>document.write(message.get("ui.adr.header.responsibleUser"))</script>*</label></td>
		  		  <td><input dojoType="ingrid:Select" required="true" style="width:386px;" id="addressOwner" /></td>
	              <td class="note"><strong><script>document.write(message.get("ui.adr.header.workState"))</script>:</strong> <span id="addressWorkState"></span></td>

		  		<tr>
	              <td class="note" colspan="3"><strong><script>document.write(message.get("ui.adr.header.creationTime"))</script>:</strong> <span id="addressCreationTime">----------</span> | <strong><script>document.write(message.get("ui.adr.header.modificationTime"))</script>:</strong> <span id="addressModificationTime">----------</span> | <strong><script>document.write(message.get("ui.adr.header.modUser"))</script>:</strong> <span id="addressLastEditor">---</span></td>
				</tr>
		  	</tbody>
			</table>
		</div>
		<div dojoType="ContentPane" widgetId="contentFrameAddress" id="contentFrameAddress" class="sectionBottom" style="overflow:auto;">
			
      <form dojoType="ingrid:FormErfassungAdresseContent" widgetId="contentFormAddress" selectedClass="AddressType0" method="get" id="contentFormAddress" action="">

			<div dojoType="ContentPane" id="contentFrameBodyAddress" class="sectionBottomContent">

			  <!-- GREY FIELD //-->
			  <!-- ADDRESS TYPE 0 //-->
			  <div id="headerAddressType0" class="contentBlock firstBlock grey">
				<div id="headerAddressType0Content" class="content">

<!-- AddressType0 parent institutions must not be displayed (http://jira.media-style.com/browse/INGRIDII-130) -->  
<!--
			      <div class="inputContainer">
			        <span class="label"><label for="headerAddressType0Institution" onclick="javascript:dialog.showContextHelp(arguments[0], 8062, 'Institution/&uuml;bergeordnete Einheit(en)')">Institution/&uuml;bergeordnete Einheit(en)</label></span>
			        <span class="input"><input type="text" mode="textarea" dojoType="ingrid:ValidationTextbox" id="headerAddressType0Institution" class="w668 h038" disabled="true" /></span>
			  	  </div>
-->
			      <div class="inputContainer noSpaceBelow">
					<span id="uiElement4100" type="required">
			        	<span id="headerAddressType0UnitLabel" class="label required"><label for="headerAddressType0Unit" onclick="javascript:dialog.showContextHelp(arguments[0], 4100)"><script>document.write(message.get("ui.adr.general.institution"))</script>*</label></span>
			        	<span class="input"><input type="text" mode="textarea" maxlength="255" dojoType="ingrid:ValidationTextbox" required="true" id="headerAddressType0Unit" class="w668 h038" /></span>
			  	  	</span>
			  	  </div>

			    </div>
			  </div>

			  <!-- ADDRESS TYPE 1 //-->
			  <div id="headerAddressType1" class="contentBlock firstBlock grey">
				  <div id="headerAddressType1Content" class="content">

			      <div class="inputContainer">
					<span id="uiElement04210" type="required">
				        <span class="label"><label for="headerAddressType1Institution" onclick="javascript:dialog.showContextHelp(arguments[0], 4210)"><script>document.write(message.get("ui.adr.general.parentInstitution"))</script></label></span>
				        <span class="input"><input type="text" mode="textarea" dojoType="ingrid:ValidationTextbox" id="headerAddressType1Institution" class="w668 h038" disabled=true" /></span>
					</span>
			  	  </div>

			      <div class="inputContainer noSpaceBelow">
					<span id="uiElement04200" type="required">
				        <span id="headerAddressType1UnitLabel" class="label required"><label for="headerAddressType1Unit" onclick="javascript:dialog.showContextHelp(arguments[0], 4200)"><script>document.write(message.get("ui.adr.general.unit"))</script>*</label></span>
				        <span class="input"><input type="text" mode="textarea" maxlength="255" dojoType="ingrid:ValidationTextbox" required="true" id="headerAddressType1Unit" class="w668 h038" /></span>
				  	</span>
			  	  </div>

			    </div>
			  </div>

			  <!-- ADDRESS TYPE 2 //-->
			  <div id="headerAddressType2" class="contentBlock firstBlock grey">
				  <div id="headerAddressType2Content" class="content">

			      <div class="inputContainer">
					<span id="uiElement14210" type="required">
				        <span class="label"><label for="headerAddressType2Institution" onclick="javascript:dialog.showContextHelp(arguments[0], 4210)">Institution/&uuml;bergeordnete Einheit(en)</label></span>
				        <span class="input"><input type="text" mode="textarea" dojoType="ingrid:ValidationTextbox" id="headerAddressType2Institution" class="w668 h038" disabled="true" /></span>
					</span>
			  	  </div>
			  	  
			      <div class="inputContainer">
			        <div class="half left">
						<span id="uiElement4315" type="required">
			          		<span id="headerAddressType2LastnameLabel" class="label required"><label for="headerAddressType2Lastname" onclick="javascript:dialog.showContextHelp(arguments[0], 4315)"><script>document.write(message.get("ui.adr.general.surName"))</script>*</label></span>
			          		<span class="input"><input type="text" maxlength="40" id="headerAddressType2Lastname" required="true" name="headerAddressType2Lastname" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
			        	</span>
			        </div>
			  
			        <div class="half">
						<span id="uiElement4310" type="required">
			          		<span id="headerAddressType2FirstnameLabel" class="label"><label for="headerAddressType2Firstname" onclick="javascript:dialog.showContextHelp(arguments[0], 4310)"><script>document.write(message.get("ui.adr.general.foreName"))</script></label></span>
			          		<span class="input"><input type="text" maxlength="40" id="headerAddressType2Firstname" name="headerAddressType2Firstname" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
			          	</span>
			        </div>
			        <div class="fill"></div>
			  	  </div>

			      <div class="inputContainer noSpaceBelow">
			        <div class="half left">
						<span id="uiElement4300" type="required">
			          		<span id="headerAddressType2StyleLabel" class="label required"><label for="headerAddressType2Style" onclick="javascript:dialog.showContextHelp(arguments[0], 4300)"><script>document.write(message.get("ui.adr.general.form"))</script>*</label></span>
			          		<span class="input"><input dojoType="ingrid:ComboBox" maxlength="40" style="width:129px;" listId="4300" id="headerAddressType2Style" /></span>
			          	</span>
			        </div>
			  
			        <div class="half">
						<span id="uiElement4305" type="required">
				        	<span id="headerAddressType2TitleLabel" class="label"><label for="headerAddressType2Title" onclick="javascript:dialog.showContextHelp(arguments[0], 4305)"><script>document.write(message.get("ui.adr.general.title"))</script></label></span>
				        	<span class="input"><input dojoType="ingrid:ComboBox" maxlength="40" style="width:129px;" listId="4305" id="headerAddressType2Title" /></span>
				       	</span>
			        </div>
			        <div class="fill"></div>
			  	  </div>

			    </div>
			  </div>

			  <!-- ADDRESS TYPE 3 //-->
			  <div id="headerAddressType3" class="contentBlock firstBlock grey">
				  <div id="headerAddressType3Content" class="content">

			      <div class="inputContainer">
			        <div class="half left">
						<span id="uiElement4000" type="required">
			          		<span id="headerAddressType3LastnameLabel" class="label required"><label for="headerAddressType3Lastname" onclick="javascript:dialog.showContextHelp(arguments[0], 4000)"><script>document.write(message.get("ui.adr.general.surName"))</script>*</label></span>
			            	<span class="input"><input type="text" maxlength="40" id="headerAddressType3Lastname" required="true" name="headerAddressType3Lastname" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
			            </span>
			        </div>
			  
			        <div class="half">
						<span id="uiElement4005" type="required">
			          		<span id="headerAddressType3FirstnameLabel" class="label"><label for="headerAddressType3Firstname" onclick="javascript:dialog.showContextHelp(arguments[0], 4005)"><script>document.write(message.get("ui.adr.general.foreName"))</script></label></span>
			            	<span class="input"><input type="text" maxlength="40" id="headerAddressType3Firstname" name="headerAddressType3Firstname" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
			        	</span>
			        </div>
			        <div class="fill"></div>
			  	  </div>

			      <div class="inputContainer">
			        <div class="half left">
						<span id="uiElement4015" type="required">
			          		<span id="headerAddressType3StyleLabel" class="label required"><label for="headerAddressType3Style" onclick="javascript:dialog.showContextHelp(arguments[0], 4015)"><script>document.write(message.get("ui.adr.general.form"))</script>*</label></span>
			          		<span class="input"><input dojoType="ingrid:ComboBox" maxlength="40" style="width:129px;" listId="4300" id="headerAddressType3Style" /></span>
			          	</span>
			        </div>
			  
			        <div class="half">
						<span id="uiElement4020" type="required">
			          		<span class="label"><label for="headerAddressType3Title" onclick="javascript:dialog.showContextHelp(arguments[0], 4020)"><script>document.write(message.get("ui.adr.general.title"))</script></label></span>
			          		<span class="input"><input dojoType="ingrid:ComboBox" maxlength="40" style="width:129px;" listId="4305" id="headerAddressType3Title" /></span>
			          	</span>
			        </div>
			        <div class="fill"></div>
			  	  </div>

					<div class="inputContainer noSpaceBelow">
						<span id="uiElement4010" type="required">
			        		<span class="label"><label for="headerAddressType3Institution" onclick="javascript:dialog.showContextHelp(arguments[0], 4010)">Institution</label></span>
			        		<span class="input"><input type="text" mode="textarea" maxlength="255" dojoType="ingrid:ValidationTextbox" id="headerAddressType3Institution" class="w668 h038" /></span>
						</span>
					</div>

			    </div>
			  </div>

			  <!-- ADRESSE UND AUFGABEN //-->
			  <div id="address" class="contentBlock">
			  	<div class="titleBar">
			  	  <div class="titleIcon"><a href="javascript:toggleFields('address');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
				    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7008)"><script>document.write(message.get("ui.adr.details.title"))</script></div>
				    <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
			  	</div>
				  <div id="addressContent" class="content">

			      <div class="inputContainer">
			        <div class="half left">
						<span id="uiElement4430" type="required">
				          <span id="addressComLabel" class="label required"><label for="addressCom" onclick="javascript:dialog.showContextHelp(arguments[0], 4430)"><script>document.write(message.get("ui.adr.details.communicationTable.title"))</script>*</label></span>
				          <div class="tableContainer rows7 half" style="top: 4px;">
				            <div class="cellEditors" id="addressComEditors">
				              <div dojoType="ingrid:ComboBox" maxlength="20" toggle="plain" style="width:37px;" listId="4430" id="addressComType"></div>
				              <div dojoType="ingrid:ValidationTextbox" maxlength="255" class="w238" widgetId="addressComConnection"></div>
				            </div>
				      	    <table id="addressCom" dojoType="ingrid:FilteringTable" minRows="7" cellspacing="0" class="filteringTable nosort interactive">
				      	      <thead>
				      		      <tr>
				            			<th nosort="true" field="medium" dataType="String" width="65" editor="addressComType"><script>document.write(message.get("ui.adr.details.communicationTable.header.medium"))</script></th>
				            			<th nosort="true" field="value" dataType="String" width="255" editor="addressComConnection"><script>document.write(message.get("ui.adr.details.communicationTable.header.value"))</script></th>
				      		      </tr>
				      	      </thead>
				      	      <tbody>
				      	      </tbody>
				      	    </table>
				      	  </div>
						</span>
			        </div>

			        <div class="half">
						<span id="uiElement4400" type="optional">
			          		<span id="addressStreetLabel" class="label"><label for="addressStreet" onclick="javascript:dialog.showContextHelp(arguments[0], 4400)"><script>document.write(message.get("ui.adr.details.street"))</script></label></span>
							<span class="input spaceBelow"><input type="text" id="addressStreet" maxlength="80" name="addressStreet" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
						</span>

			          <div id="addressDetails1">
			            <span class="entry first">
							<span id="uiElement4410" type="optional">
			              		<span id="addressZipCodeLabel" class="label"><label for="addressZipCode" onclick="javascript:dialog.showContextHelp(arguments[0], 4410)"><script>document.write(message.get("ui.adr.details.postCode"))</script></label></span>
			              		<span class="input spaceBelow"><input type="text" maxlength="10" id="addressZipCode" name="addressZipCode" class="w061" dojoType="ingrid:ValidationTextBox" /></span>
			              	</span>
			            </span>
			            <span class="entry">
							<span id="uiElement4415" type="optional">
			              		<span id="addressCityLabel" class="label"><label for="addressCity" onclick="javascript:dialog.showContextHelp(arguments[0], 4415)"><script>document.write(message.get("ui.adr.details.city"))</script></label></span>
			              		<span class="input spaceBelow"><input type="text" maxlength="80" id="addressCity" name="addressCity" class="w148" dojoType="ingrid:ValidationTextBox" /></span>
			              	</span>
			            </span>
			          </div>

			          <div id="addressDetails2">
			            <span class="entry first">
							<span id="uiElement4420" type="optional">
			              		<span id="addressPOBoxLabel" class="label"><label for="addressPOBox" onclick="javascript:dialog.showContextHelp(arguments[0], 4420)"><script>document.write(message.get("ui.adr.details.poBox"))</script></label></span>
			              		<span class="input spaceBelow"><input type="text" id="addressPOBox" maxlength="10" name="addressPOBox" class="w148" dojoType="ingrid:ValidationTextBox" /></span>
			            	</span>
			            </span>
			            <span class="entry">
							<span id="uiElement4425" type="optional">
			              		<span id="addressZipPOBoxLabel" class="label"><label for="addressZipPOBox" onclick="javascript:dialog.showContextHelp(arguments[0], 4425)"><script>document.write(message.get("ui.adr.details.poBoxPostCode"))</script></label></span>
			              		<span class="input spaceBelow"><input type="text" id="addressZipPOBox" maxlength="10" name="addressZipPOBox" class="w061" dojoType="ingrid:ValidationTextBox" /></span>
			            	</span>
			            </span>
			          </div>
			          <div id="addressDetails3">
			          	<span class="entry first">
							<span id="uiElement4405" type="optional">
			              		<span id="addressCountryLabel" class="label"><label for="addressCountry" onclick="javascript:dialog.showContextHelp(arguments[0], 4405)"><script>document.write(message.get("ui.adr.details.state"))</script></label></span>
		                  		<span class="input"><input dojoType="ingrid:Select" autoComplete="false" style="width:302px;" listId="6200" id="addressCountry" /></span>
			            	</span>
			            </span>
			          </div>
			        </div>

			        <div class="fill"></div>
			      </div>

			      <div class="inputContainer noSpaceBelow notRequired">

			        <div class="half left">
						<span id="uiElement4435" type="optional">
							<span class="label"><label for="addressNotes" onclick="javascript:dialog.showContextHelp(arguments[0], 4435)"><script>document.write(message.get("ui.adr.details.notes"))</script></label></span>
							<span class="input"><input type="text" mode="textarea" maxlength="255" dojoType="ingrid:ValidationTextbox" id="addressNotes" class="w320 h120" /></span>
						</span>
			        </div>

			        <div class="half">
						<span id="uiElement4440" type="optional">
			          		<span class="label"><label for="addressTasks" onclick="javascript:dialog.showContextHelp(arguments[0], 4440)"><script>document.write(message.get("ui.adr.details.tasks"))</script></label></span>
			          		<span class="input"><input type="text" mode="textarea" dojoType="ingrid:ValidationTextbox" id="addressTasks" class="w320 h120" /></span>
			          	</span>
			        </div>
			        <div class="fill"></div>
			      </div>

			    </div>
			  </div>

			  	<!-- VERSCHLAGWORTUNG //-->
				<div id="adrThesaurus" class="contentBlock">
					<div class="titleBar">
						<div class="titleIcon"><a href="javascript:toggleFields('adrThesaurus');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
						<div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7009)"><script>document.write(message.get("ui.adr.thesaurus.title"))</script></div>
						<div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
					</div>
					<div id="adrThesaurusContent" class="content">
				  
						<div class="inputContainer notRequired h088">
							<span id="uiElement4510" type="optional">
								<span class="label"><label for="thesaurusTermsAddress" onclick="javascript:dialog.showContextHelp(arguments[0], 4510)"><script>document.write(message.get("ui.adr.thesaurus.terms"))</script></label></span>
								<span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage(message.get('dialog.thesaurusNavigator.title'), 'mdek_thesaurus_dialog.jsp', 1010, 430, true, {dstTable: 'thesaurusTermsAddress'});" title="<fmt:message key="dialog.popup.thesaurus.terms.link.navigator" /> [Popup]"><script>document.write(message.get("ui.adr.thesaurus.terms.link.navigator"))</script></a></span>

								<div class="tableContainer headHiddenRows3 full">
									<table id="thesaurusTermsAddress" dojoType="ingrid:FilteringTable" minRows="3" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
										<thead>
											<tr>
												<th nosort="true" field="label" dataType="String">Term</th>
												<th nosort="true" field="sourceString" dataType="String">Source</th>
											</tr>
										</thead>
									  <colgroup>
									    <col width="90%">
									    <col width="10%">
									  </colgroup>
				            	      <tbody>
				            	      </tbody>
									</table>
								</div>
							</span>
			      		</div>

						<div class="inputContainer notRequired">
							<div class="full">
			          			<div class="input">
									<span id="uiElementN019" type="optional">
										<input type="text" mode="textarea" maxlength="255" id="thesaurusFreeTermInputAddress" class="w585 h055" dojoType="ingrid:ValidationTextBox" />
		                  				<div style="position:relative; height:0px; top:-22px; float:right;">
											<button id="thesaurusFreeTermsAddressAddButton" dojoType="ingrid:Button"><script>document.write(message.get("ui.adr.thesaurus.terms.custom.buttonAdd"))</script></button>
						  				</div>
			          				</span>
			          			</div>
				        		<div class="fill"></div>
			        		</div>
			      		</div>

					</div>
				</div>
			  
			  <!-- ZUGEORDNETE OBJEKTE //-->
			  <div id="associatedObj" class="contentBlock">
			    <div class="titleBar">
			  	  <div class="titleIcon"><a href="javascript:toggleFields('associatedObj');" title="<fmt:message key="general.open.required.field" />"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
				  <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7010)"><script>document.write(message.get("ui.adr.links.title"))</script></div>
				  <div class="titleUp"><a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
			  	</div>
				<div id="associatedObjContent" class="content" style="clear:both;">

			      <div class="inputContainer noSpaceBelow notRequired">
					<span id="uiElementN020" type="optional">
						<div class="spacer"></div>
	
			            <div class="listInfo full">
							<span id="associatedObjNameInfo" style="float:left;" class="searchResultsInfo">&nbsp;</span>
			              	<span id="associatedObjNamePaging" style="float:right;" class="searchResultsPaging">&nbsp;</span>
			        	  	<div class="fill"></div>
			            </div>
	
				        <div class="tableContainer headHiddenRows6 full">
							<table id="associatedObjName" dojoType="ingrid:FilteringTable" minRows="6" headClass="hidden" cellspacing="0" class="filteringTable nosort">
				    	    	<thead>
				    		  		<tr>
				                		<th nosort="true" field="icon" dataType="String"></th>
				          	    		<th nosort="true" field="linkLabel" dataType="String">Name</th>
				    		  		</tr>
				    	    	</thead>
					        	<colgroup>
							  		<col width="23">
							  		<col width="634">
						    	</colgroup>
				    	    	<tbody>
				    	    	</tbody>
				    	  	</table>
				        </div>
					</span>
			      </div>

				</div>
			  </div>
		  	  <!-- END ZUGEORDNETE OBJEKTE //-->

	        </div>
		  </form>
		</div>
	  </div>
	  <!-- END CONTENT ADDRESS -->
      <div id="contentNone"><script>document.write(message.get("general.noObjectAddressSelected"))</script></div>
    </div>

  </div>
  <!-- SPLIT CONTAINER END -->
  </div>

  <div widgetId="page2" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_research_search.jsp" preload="false" executeScripts="true"></div>
  <div widgetId="page2Sub2" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_research_thesaurus.jsp" preload="false" executeScripts="true"></div>
  <div widgetId="page2Sub3" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_research_database.jsp" preload="false" executeScripts="true"></div>

  <div widgetId="page3" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_qa_editor.jsp" preload="false" executeScripts="true" refreshOnShow="false"></div>
  <div widgetId="page3Sub2" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_qa_assurance.jsp" preload="false" executeScripts="true" refreshOnShow="false"></div>

  <div widgetId="page4" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_statistics.jsp" preload="false" executeScripts="true" refreshOnShow="false"></div>
  </div>

</body>
</html>
