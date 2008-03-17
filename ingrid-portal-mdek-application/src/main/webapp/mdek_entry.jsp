<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Metadaten-Erfassungskomponente</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script src='/ingrid-portal-mdek-application/dwr/interface/EntryService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/SNSService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/CTService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/VersionInformation.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/interface/DownloadTest.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>


<script type="text/javascript">
	var djConfig = {isDebug: true, /* use with care, may lead to unexpected errors! */debugAtAllCosts: false, debugContainerId: "dojoDebugOutput"};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>
<script type="text/javascript" src="js/recherche.js"></script>
<script type="text/javascript" src="js/treeEventHandler.js"></script>
<script type="text/javascript" src="js/menuEventHandler.js"></script>
<script type="text/javascript" src="js/udkDataProxy.js"></script>
<script type="text/javascript" src="js/rules_checker.js"></script>
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

<script type="text/javascript">

// click handler for main menus
var menus = [{menu:"page1", submenus:[]}, 
			 {menu:"page2", submenus:["page2", "page2Sub2", "page2Sub3"]},
			 {menu:"page3", submenus:[]},
			 {menu:"page4", submenus:["page4", "page4Sub2"]}
			];
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
</style>
<!--[if IE]>
<style type="text/css">
	@import url(css/scrolling_table.ie.css);
</style>
<![endif]-->

</head>

<body>

<!-- use this for print div content, see commons.js -> printDivContent() -->
<iframe id="printFrame" name="printFrame" style="position:absolute; left:-1000px"></iframe>

<div id="splash" style="position: absolute; top: 0px; width: 100%;z-index: 100; height:2000px;background-color:#FFFFFF">
<div style="position: relative; width: 100%;z-index: 100;top:200px">
   <div align="center" style="line-height:16px">
        <div style="width:550px; height:20px; background-color:#156496">&nbsp;</div>
        <div style="width:550px; background-color:#e6f0f5; font-family:Verdana,Helvetica,Arial,sans-serif; font-size:12px; padding: 20px 0px 20px 0px; margin:0px">
          <p style="font-size:24px; font-weight:bold; line-height:16px; margin:16px"> Metadaten-Erfassungskomponente</p>
<!--           <p style="font-size:16px; font-weight:bold; line-height:16px; margin:16px">Version 1.0.0</p>  -->
          <p style="font-size:12px; font-weight:normal; line-height:16px; margin:16px">Die Applikation wird geladen...</p>
        </div>
   </div>
</div>
</div>

<!-- 
<div id="loadingZone" style="position: absolute; top: 0px; left: 0px; width: 5px;z-index: 100; height:5px;background-color:#FF0000;visibility:hidden"></div> 
 -->


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
  	      <li>Fred Kruse · Rollenbezeichnung · Katalog Niedersachsen</li>
  	      <li class="seperator">|</li>
  	      <li><a href="javascript:void(0);" title="Hilfe">Hilfe</a></li>
  	      <li class="seperator">|</li>
  	      <li><a href="javascript:void(0);" onclick="javascript:window.open('http://www.portalu.de:80/ingrid-portal/portal/disclaimer.psml', 'impressum', 'width=966,height=994,resizable=yes,scrollbars=yes,locationbar=no');" title="Impressum">Impressum</a></li>
  	      <li class="seperator">|</li>
  	      <li><a href="javascript:void(0);" title="English">English</a></li>
  	      <li class="seperator">|</li>
  	      <li><a href="javascript:dialog.showPage('Info', 'mdek_info_dialog.html', 365, 160, false);" title="Info">Info</a></li>
  	      <li class="seperator">|</li>
  	      <li><a href="javascript:void(0);" title="schlie&szlig;en"><strong>SCHLIESSEN</strong></a></li>
  	    </ul>
  	  </div>
  	  <div id="navi">
  	    <ul>
  	      <li><a id="page1Menu" onClick="clickMenu('page1')" href="javascript:void(0);" class="current" title="Hierarchie & Erfassung">Hierarchie & Erfassung</a></li>
  	      <li><a id="page2Menu" onClick="clickMenu('page2')" href="javascript:void(0);" title="Recherche">Recherche</a></li>
  	      <li><a id="page3Menu" onClick="clickMenu('page3')" href="javascript:void(0);" title="Statistik">Statistik</a></li>
  	      <li><a id="page4Menu" onClick="clickMenu('page4')" href="javascript:void(0);" title="Qualitätssicherung">Qualitätssicherung</a></li>
  	    </ul>
  	  </div>
  	  
	  <div id="page1Subnavi" class="subnavi" style="display:none"></div>
	  <div id="page2Subnavi" class="subnavi" style="display:none">
  	    <ul>
  	      <li><a id="page2Subnavi1" onClick="clickMenu('page2', 'page2')" href="javascript:void(0);" class="current" title="Suche">Suche</a></li>
  	      <li><a id="page2Subnavi2" onClick="clickMenu('page2', 'page2Sub2')" href="javascript:void(0);" title="Thesaurus-Navigator">Thesaurus-Navigator</a></li>
  	      <li><a id="page2Subnavi3" onClick="clickMenu('page2', 'page2Sub3')" href="javascript:void(0);" title="Datenbank-Suche">Datenbank-Suche</a></li>
  	    </ul>
  	  </div>
	  <div id="page3Subnavi" class="subnavi" style="display:none"></div>
	  <div id="page4Subnavi" class="subnavi" style="display:none">
  	    <ul>
   	      <li><a id="page4Subnavi1" onClick="clickMenu('page4', 'page4')" href="javascript:void(0);" class="current" title="Bearbeitung/Verantwortlich">Bearbeitung/Verantwortlich</a></li>
  	      <li><a id="page4Subnavi2" onClick="clickMenu('page4', 'page4Sub2')" href="javascript:void(0);" title="Qualitätssicherung">Qualitätssicherung</a></li>
  	    </ul>
  	  </div>
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
  <div dojoType="SplitContainer" id="contentSection" class="contentSection" orientation="horizontal" sizerWidth="15" layoutAlign="client" templateCssPath="js/dojo/widget/templates/SplitContainer.css">
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
	      <form dojoType="ingrid:Form" method="get" id="headerFormObject">
	        <table cellspacing="0">
	          <tbody>
	            <tr>
	              <td id="objectNameLabel" class="label required"><label for="objectName">Objektname*</label></td>
	              <td colspan="2"><input type="text" id="objectName" required="true" name="objectName" class="w550" dojoType="ingrid:ValidationTextBox" /></td></tr>
	            <tr>
	              <td id="objectClassLabel" class="label required col1"><label for="objectClass">Objektklasse*</label></td>
	              <td class="col2">
	                <!-- autoComplete=false because of 'weird' SelectBox behaviour (Click on Box Arrow adds wrong text to the selection) -->
	                <select dojoType="ingrid:Select" autoComplete="false" style="width:386px;" id="objectClass" name="objectClass">
	                	<option value="Class0">Organisationseinheit/Fachaufgabe</option>
	                	<option value="Class1">Geo-Information/Karte</option>
	                	<option value="Class2">Dokument/Bericht/Literatur</option>
	                	<option value="Class3">Dienst/Anwendung/Informationssystem</option>
	                	<option value="Class4">Vorhaben/Projekt/Programm</option>
	                	<option value="Class5">Datensammlung/Datenbank</option>
	                </select>
 	              </td>
	              <td class="col3"><img src="img/lock.gif" width="9" height="14" alt="gesperrt" /></td>
	            </tr>
	            <tr>
	              <td class="label"><label for="objectOwner">Verantwortlicher</label></td>
	              <td><input dojoType="ingrid:Select" autoComplete="false" disabled="true" style="width:386px;" id="objectOwner" /></td>
	              <td class="note"><strong>Status:</strong> <span id="workState"></span></td>
	            </tr>
	            <tr>
	              <td class="note" colspan="3"><strong>Erstellt am:</strong> <span id="creationTime">26.06.1998</span> | <strong>Ge&auml;ndert am:</strong> <span id="modificationTime">27.09.2000</span> | <strong>Von:</strong> <span id="last_editor">---</span></td>
	            </tr>
	          </tbody>
	        </table>
	      </form>
	    </div>

	      <!-- FORM -->
	      <div dojoType="ContentPane" widgetId="contentFrameObject" id="contentFrameObject" class="sectionBottom" style="overflow:auto;">
	        <a name="sectionBottomContent"></a>

	        <form dojoType="ingrid:FormErfassungObjektContent" widgetId="contentFormObject" selectedClass="Class0" method="get" id="contentFormObject" action="">

	        <div dojoType="ContentPane" id="contentFrameBodyObject" class="sectionBottomContent">

	          <!-- ALLGEMEINES //-->
	          <div id="general" class="contentBlock firstBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('general');" title="Nur Pflichtfelder aufklappen"><img id="generalRequiredToggle" src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Allgemeines')">Allgemeines</div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="generalContent" class="content">

	              <div class="inputContainer notRequired">
	                <span class="label"><label for="generalShortDesc" onclick="javascript:dialog.showContextHelp(arguments[0], 'Kurzbezeichnung')">Kurzbezeichnung</label></span>
	                <span class="input"><input type="text" id="generalShortDesc" name="generalShortDesc" class="w668" dojoType="ingrid:ValidationTextBox" /></span>
	          	  </div>

	              <div class="inputContainer">
	                <span id="generalDescLabel" class="label required"><label for="generalDesc" onclick="javascript:dialog.showContextHelp(arguments[0], 'Beschreibung')">Beschreibung*</label></span>
               		<span class="input"><input type="text" mode="textarea" id="generalDesc" name="generalDesc" class="w668 h055" dojoType="ingrid:ValidationTextbox" /></span> 
	          	  </div>

	              <div class="inputContainer noSpaceBelow">
					
					<!-- The Address table is made 'not required' for testing purpose -->
	                <span id="generalAddressTableLabel" class="label required"><label for="generalAddressTable" onclick="javascript:dialog.showContextHelp(arguments[0], 'Adressen')">Adressen*</label></span>

	                <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Adresse hinzuf&uuml;gen', 'mdek_address_dialog.html', 755, 580, true);" title="Adresse hinzuf&uuml;gen [Popup]">Adresse hinzuf&uuml;gen</a></span>
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
	          	  </div>

	            </div>
	          </div>

	          <!-- FACHBEZUG CLASS 1 - GEO-INFORMATION/KARTE //-->
	          <div id="refClass1" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('refClass1');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Fachbezug')">Fachbezug</div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="ref1Content" class="content">

	              <div class="inputContainer required">
	                <div class="half left">
	                  <span id="ref1DataSetLabel" class="label required"><label for="ref1DataSet" onclick="javascript:dialog.showContextHelp(arguments[0], 'Datensatz/Datenserie')">Datensatz/Datenserie*</label></span>
	                  <span class="input spaceBelow"><input dojoType="ingrid:Select" style="width:302px;" listId="525" id="ref1DataSet" /></span>
	                  <span class="label"><label for="ref1Coverage" onclick="javascript:dialog.showContextHelp(arguments[0], 'Erfassungsgrad')">Erfassungsgrad</label></span>
	                  <span class="input"><input type="text" id="ref1Coverage" name="ref1Coverage" class="w038" dojoType="RealNumberTextbox" /> %</span>
	            	  </div>

	                <div class="half">
	                  <span class="label"><label for="ref1Representation" onclick="javascript:dialog.showContextHelp(arguments[0], 'Digitale Repr&auml;sentation')">Digitale Repr&auml;sentation</label></span>
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
	            	  </div>
	            	  <div class="fill"></div>
	              </div>

	              <div id="ref1VFormat" class="inputContainer notRequired h154">
	                <span id="ref1VFormatLabel" class="label"><label class="inActive">Vektorformat</label></span>
	                <div id="ref1VFormat" class="outlined h110">
	                  <div class="thirdInside left">
	                    <span class="label"><label for="ref1VFormatTopology" onclick="javascript:dialog.showContextHelp(arguments[0], 'Topologieinformation')">Topologieinformation</label></span>
	                    <span class="input"><input dojoType="ingrid:Select" style="width:129px;" listId="528" id="ref1VFormatTopology" /></span>
	                  </div>

	                  <div class="thirdInside2">
	                    <span class="label hidden"><label for="ref1VFormatDetails" onclick="javascript:dialog.showContextHelp(arguments[0], 'weitere Angaben')">weitere Angaben</label></span>
	                    <div class="tableContainer rows4 thirdInside2">
	                      <div class="cellEditors" id="ref1VFormatDetailsEditors">
	                        <div dojoType="ingrid:Select" toggle="plain" style="width:100px;" listId="515" id="geometryTypeEditor"></div>
	                        <div dojoType="IntegerTextbox" min="0" max="2147483647" widgetId="elementNumberEditor"></div>
	                      </div>
	                	    <table id="ref1VFormatDetails" dojoType="ingrid:FilteringTable" minRows="4" headClass="fixedHeader" cellspacing="0" class="filteringTable nosort interactive">
	                	      <thead>
	                		      <tr>
	                      			<th nosort="true" field="geometryType" dataType="String" width="120" editor="geometryTypeEditor">Geometrietyp</th>
	                      			<th nosort="true" field="numElements" dataType="String" width="200" editor="elementNumberEditor">Elementanzahl</th>
	                		      </tr>
	                	      </thead>
	                	      <tbody>
	                	      </tbody>
	                	    </table>
	                    </div>
	                  </div>
	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer notRequired">
	                <span class="label"><label for="ref1SpatialSystem" onclick="javascript:dialog.showContextHelp(arguments[0], 'Raumbezugssystem')">Raumbezugssystem</label></span>
	                <span class="input"><input dojoType="ingrid:ComboBox" autoComplete="false" style="width:649px;" listId="100" id="ref1SpatialSystem" /></span>
	          	  </div>

	              <div class="inputContainer notRequired h130">
	                <span class="label">Erstellungsma&szlig;stab</span>
	                <div class="tableContainer rows4 full">
	                  <div class="cellEditors" id="ref1ScaleEditors">
	                    <div dojoType="IntegerTextbox" min="0" max="2147483647" widgetId="ref1ScaleScale"></div>
	                    <div dojoType="RealNumberTextbox" widgetId="ref1ScaleGroundResolution"></div>
	                    <div dojoType="RealNumberTextbox" widgetId="ref1ScaleScanResolution"></div>
	                  </div>
	            	    <table id="ref1Scale" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort interactive">
	            	      <thead>
	            		      <tr>
	                  			<th nosort="true" field="scale" dataType="String" width="105" editor="ref1ScaleScale">Ma&szlig;stab 1:x</th>
	                  			<th nosort="true" field="groundResolution" dataType="String" width="285" editor="ref1ScaleGroundResolution">Bodenaufl&ouml;sung (m)</th>
	                  			<th nosort="true" field="scanResolution" dataType="String" width="285" editor="ref1ScaleScanResolution">Scanaufl&ouml;sung (DPI)</th>
	            		      </tr>
	            	      </thead>
	            	      <tbody>
	            	      </tbody>
	            	    </table>
	                </div>
	          	  </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
	                  <span class="label"><label for="ref1AltAccuracy" onclick="javascript:dialog.showContextHelp(arguments[0], 'H&ouml;hengenauigkeit (m)')">H&ouml;hengenauigkeit (m)</label></span>
	                  <span class="input"><input type="text" id="ref1AltAccuracy" name="ref1AltAccuracy" class="w320" dojoType="RealNumberTextbox" /></span>
	                </div>
	          
	                <div class="half">
	                  <span class="label"><label for="ref1PosAccuracy" onclick="javascript:dialog.showContextHelp(arguments[0], 'Lagegenauigkeit (m)')">Lagegenauigkeit (m)</label></span>
	                  <span class="input"><input type="text" id="ref1PosAccuracy" name="ref1PosAccuracy" class="w320" dojoType="RealNumberTextbox" /></span>
	                </div>
	                <div class="fill"></div>
	          	  </div>

	              <div class="inputContainer notRequired h126">
	                <span class="label"><label for="ref1SymbolsTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 'Symbolkatalog')">Symbolkatalog</label></span>
	                <span id="ref1SymbolsTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Verweis anlegen/bearbeiten', 'mdek_links_dialog.html', 1010, 680, true, {filter: 3555});" title="Verweis anlegen/bearbeiten [Popup]">Verweis anlegen/bearbeiten</a></span>
	              	<div id="ref1SymbolsTabContainer" dojoType="ingrid:TabContainer" class="h108" selectedChild="ref1SymbolsTab1">
	               		<div id="ref1SymbolsTab1" dojoType="ContentPane" label="Text">
	                    <div class="tableContainer rows3 full">
	                      <div class="cellEditors" id="ref1SymbolsTextEditors">
	                        <div dojoType="ingrid:ComboBox" toggle="plain" style="width:400px;" listId="3555" id="ref1SymbolsTitleCombobox"></div>
	                        <div dojoType="ingrid:DropdownDatePicker" displayFormat="dd.MM.yyyy" toggle="plain" widgetId="ref1SymbolsDateDatePicker"></div>
	                        <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="ref1SymbolsVersion"></div>
	                      </div>
	                	    <table id="ref1SymbolsText" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y" minRows="3" cellspacing="0" class="filteringTable interactive nosort">
	                	      <thead>
	                		      <tr>
	                      			<th nosort="true" field="title" dataType="String" width="435" editor="ref1SymbolsTitleCombobox">Titel</th>
	                      			<th nosort="true" field="date" dataType="Date" width="120" editor="ref1SymbolsDateDatePicker">Datum</th>
	                      			<th nosort="true" field="version" dataType="String" width="120" editor="ref1SymbolsVersion">Version</th>
	                		      </tr>
	                	      </thead>
	                	      <tbody>
	                	      </tbody>
	                	    </table>
	                	  </div>
	                	</div>
	              		<div id="ref1SymbolsTab2" dojoType="ContentPane" label="Verweise">
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
	          	  </div>

	              <div class="inputContainer notRequired h126">
	                <span class="label"><label for="ref1KeysTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 'Schl&uuml;sselkatalog')">Schl&uuml;sselkatalog</label></span>
	                <span id="ref1KeysTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Verweis anlegen/bearbeiten', 'mdek_links_dialog.html', 1010, 680, true, {filter: 3535});" title="Verweis anlegen/bearbeiten [Popup]">Verweis anlegen/bearbeiten</a></span>
	              	<div id="ref1KeysTabContainer" dojoType="ingrid:TabContainer" class="h108" selectedChild="ref1KeysTab1">
	              		<div id="ref1KeysTab1" dojoType="ContentPane" label="Text">
	                    <div class="tableContainer rows3 full">
	                      <div class="cellEditors" id="ref1KeysTextEditors">
	                        <div dojoType="ingrid:ComboBox" toggle="plain" style="width:400px;" listId="3535" id="ref1KeysTitleCombobox"></div>
	                        <div dojoType="ingrid:DropdownDatePicker" displayFormat="dd.MM.yyyy" toggle="plain" widgetId="ref1KeysDateDatePicker"></div>
	                        <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="ref1KeysVersion"></div>
	                      </div>
	                	    <table id="ref1KeysText" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y" minRows="3" cellspacing="0" class="filteringTable interactive nosort">
	                	      <thead>
	                		      <tr>
	                      			<th nosort="true" field="title" dataType="String" width="435" editor="ref1KeysTitleCombobox">Titel</th>
	                      			<th nosort="true" field="date" dataType="Date" width="120" editor="ref1KeysDateDatePicker">Datum</th>
	                      			<th nosort="true" field="version" dataType="String" width="120" editor="ref1KeysVersion">Version</th>
	                		      </tr>
	                	      </thead>
	                	      <tbody>
	                	      </tbody>
	                	    </table>
	                	  </div>
	              		</div>
	              		<div id="ref1KeysTab2" dojoType="ContentPane" label="Verweise">
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
	          	  </div>

	              <div class="inputContainer notRequired h108">
	                <span class="label">Verweis zu Dienst</span>
	                <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Verweis anlegen/bearbeiten', 'mdek_links_dialog.html', 1010, 680, true, {filter: 5066});" title="Verweis anlegen/bearbeiten [Popup]">Verweis anlegen/bearbeiten</a></span>
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
	          	  </div>

	              <div class="inputContainer notRequired">
	                <span class="label"><label for="ref1BasisTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 'Fachliche Grundlage')">Fachliche Grundlage</label></span>
	                <span id="ref1BasisTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Verweis anlegen/bearbeiten', 'mdek_links_dialog.html', 1010, 680, true, {filter: 3520});" title="Verweis anlegen/bearbeiten [Popup]">Verweis anlegen/bearbeiten</a></span>
	              	<div id="ref1BasisTabContainer" dojoType="ingrid:TabContainer" class="h108" selectedChild="ref1BasisTab1">
	              		<div id="ref1BasisTab1" dojoType="ContentPane" label="Text">
	                    <span class="input">
	               			<input type="text" mode="textarea" id="ref1BasisText" name="ref1BasisText" class="w668 h083" dojoType="ingrid:ValidationTextbox" />
	               		</span> 
	              		</div>
	              		<div id="ref1BasisTab2" dojoType="ContentPane" label="Verweise">
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
	          	  </div>

	              <div class="inputContainer notRequired">
	                <span class="label"><label for="ref1DataBasisTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 'Datengrundlage')">Datengrundlage</label></span>
	                <span id="ref1DataBasisTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Verweis anlegen/bearbeiten', 'mdek_links_dialog.html', 1010, 680, true, {filter: 3570});" title="Verweis anlegen/bearbeiten [Popup]">Verweis anlegen/bearbeiten</a></span>
	              	<div id="ref1DataBasisTabContainer" dojoType="ingrid:TabContainer" class="h108" selectedChild="ref1DataBasisTab1">
	              		<div id="ref1DataBasisTab1" dojoType="ContentPane" label="Text">
	                    <span class="input">
	               			<input type="text" mode="textarea" id="ref1DataBasisText" name="ref1DataBasisText" class="w668 h083" dojoType="ingrid:ValidationTextbox" /> 
	                    </span>
	              		</div>
	              		<div id="ref1DataBasisTab2" dojoType="ContentPane" label="Verweise">
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
	          	  </div>

	              <div class="inputContainer notRequired h088">
	                <span class="label"><label for="ref1Data" onclick="javascript:dialog.showContextHelp(arguments[0], 'Sachdaten/Attributinformation')">Sachdaten/Attributinformation</label></span>
	                <div class="tableContainer headHiddenRows3 full">
	                  <div class="cellEditors" id="ref1DataEditors">
	                    <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" class="w659" widgetId="ref1DataEditor"></div>
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
	          	  </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <span class="label"><label for="ref1ProcessTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 'Herstellungsprozess')">Herstellungsprozess</label></span>
	                <span id="ref1ProcessTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Verweis anlegen/bearbeiten', 'mdek_links_dialog.html', 1010, 680, true, {filter: 3515});" title="Verweis anlegen/bearbeiten [Popup]">Verweis anlegen/bearbeiten</a></span>
	              	<div id="ref1ProcessTabContainer" dojoType="ingrid:TabContainer" class="h108" selectedChild="ref1ProcessTab1">
	              		<div id="ref1ProcessTab1" dojoType="ContentPane" label="Text">
	                    <span class="input">
	               			<input type="text" mode="textarea" id="ref1ProcessText" name="ref1ProcessText" class="w668 h083" dojoType="ingrid:ValidationTextbox" /> 
	                    </span>
	              		</div>
	              		<div id="ref1ProcessTab2" dojoType="ContentPane" label="Verweise">
	                    <div class="tableContainer rows4 full">
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
	          	  </div>

	            </div>
	          </div>

	          <!-- FACHBEZUG CLASS 2 - DOKUMENT/BERICHT/LITERATUR //-->
	          <div id="refClass2" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('refClass2');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Fachbezug')">Fachbezug</div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="ref2Content" class="content">

	              <div class="inputContainer notRequired">
	                <span class="label"><label for="ref2Author" onclick="javascript:dialog.showContextHelp(arguments[0], 'Autor/Verfasser')">Autor/Verfasser</label></span>
               		<span class="input"><input type="text" mode="textarea" id="ref2Author" name="ref2Author" class="w668 h038" dojoType="ingrid:ValidationTextbox" /></span> 
	              </div>

	              <div class="inputContainer notRequired">
	                <span class="label"><label for="ref2Publisher" onclick="javascript:dialog.showContextHelp(arguments[0], 'Herausgeber')">Herausgeber</label></span>
	                <span class="input"><input type="text" id="ref2Publisher" name="ref2Publisher" class="w668" dojoType="ingrid:ValidationTextBox" /></span>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
	                  <span class="label"><label for="ref2PublishedIn" onclick="javascript:dialog.showContextHelp(arguments[0], 'Erschienen in')">Erschienen in</label></span>
	                  <span class="input"><input type="text" id="ref2PublishedIn" name="ref2PublishedIn" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
	                </div>
	          
	                <div class="half">
	                  <span class="label"><label for="ref2PublishLocation" onclick="javascript:dialog.showContextHelp(arguments[0], 'Erscheinungsort')">Erscheinungsort</label></span>
	                  <span class="input"><input type="text" id="ref2PublishLocation" name="ref2PublishLocation" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
	                  <div id="ref2PublishedInDetails1">
	                    <span class="entry first">
	                      <span class="label"><label for="ref2PublishedInIssue" onclick="javascript:dialog.showContextHelp(arguments[0], 'Band/Heft')">Band/Heft</label></span>
	                      <span class="input spaceBelow"><input type="text" id="ref2PublishedInIssue" name="ref2PublishedInIssue" class="w085" dojoType="ingrid:ValidationTextBox" /></span>
	                    </span>
	                    <span class="entry">
	                      <span class="label"><label for="ref2PublishedInPages" onclick="javascript:dialog.showContextHelp(arguments[0], 'Seiten')">Seiten</label></span>
	                      <span class="input spaceBelow"><input type="text" id="ref2PublishedInPages" name="ref2PublishedInPages" class="w085" dojoType="ingrid:ValidationTextBox" /></span>
	                    </span>
	                    <span class="entry rightAlign">
	                      <span class="label"><label for="ref2PublishedInYear" onclick="javascript:dialog.showContextHelp(arguments[0], 'Erscheinungsjahr')">Erscheinungsjahr</label></span>
	                      <span class="input spaceBelow"><input type="text" id="ref2PublishedInYear" name="ref2PublishedInYear" class="w085" dojoType="ingrid:ValidationTextBox" /></span>
	                    </span>
	                  </div>

	                  <div id="ref2PublishedInDetails2">
	                    <span class="entry first">
	                      <span class="label"><label for="ref2PublishedISBN" onclick="javascript:dialog.showContextHelp(arguments[0], 'ISBN-Nr.')">ISBN-Nr.</label></span>
	                      <span class="input"><input type="text" id="ref2PublishedISBN" name="ref2PublishedISBN" class="w148" dojoType="ingrid:ValidationTextBox" /></span>
	                    </span>
	                    <span class="entry">
	                      <span class="label"><label for="ref2PublishedPublisher" onclick="javascript:dialog.showContextHelp(arguments[0], 'Verlag')">Verlag</label></span>
	                      <span class="input"><input type="text" id="ref2PublishedPublisher" name="ref2PublishedPublisher" class="w148" dojoType="ingrid:ValidationTextBox" /></span>
	                    </span>
	                  </div>
	                </div>
	          
	                <div class="half">
	                  <span class="label"><label for="ref2LocationTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 'Standort')">Standort</label></span>
	                  <span id="ref2LocationTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Adresse hinzuf&uuml;gen', 'mdek_address_dialog.html', 755, 580, true, {linkType: 3360});" title="Adresse hinzuf&uuml;gen [Popup]">Adresse hinzuf&uuml;gen</a></span>

	                	<div id="ref2LocationTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref2LocationTab1">
	                		<div id="ref2LocationTab1" dojoType="ContentPane" label="Text">
								<span class="input">
									<input type="text" mode="textarea" id="ref2LocationText" name="ref2LocationText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
								</span>
	                		</div>
	                		<div id="ref2LocationTab2" dojoType="ContentPane" label="Verweise">
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
	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
	                  <span class="label"><label for="ref2DocumentType" onclick="javascript:dialog.showContextHelp(arguments[0], 'Dokumententyp')">Dokumententyp</label></span>
	                  <span class="input spaceBelow">
	                  	<select dojoType="ingrid:ComboBox" style="width:302px;" id="ref2DocumentType" name="ref2DocumentType">
	                  	  <option value="1">Aufsatz/Artikel/Tagungsbeitrag</option>
	                  	  <option value="2">Brosch&uuml;re/Bericht</option>
	                  	  <option value="3">Zeitschrift</option>
	                  	  <option value="4">Buch/Monographie/Reihe</option>
	                  	  <option value="5">Tagungsband/Sammelwerk</option>
	                  	  <option value="6">Fachgutachten</option>
	                  	</select>
	                  </span>
	                </div>
	          
	                <div class="half">
	                  <span class="label"><label for="ref2BaseDataTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 'Basisdaten')">Basisdaten</label></span>
	                  <span id="ref2BaseDataTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Verweis anlegen/bearbeiten', 'mdek_links_dialog.html', 1010, 680, true, {filter: 3345});" title="Verweis anlegen/bearbeiten [Popup]">Verweis anlegen/bearbeiten</a></span>
	                	<div id="ref2BaseDataTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref2BaseDataTab1">
	                		<div id="ref2BaseDataTab1" dojoType="ContentPane" label="Text">
	                      <span class="input">
	                      	<input type="text" mode="textarea" id="ref2BaseDataText" name="ref2BaseDataText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
	                      </span>
	                		</div>
	                		<div id="ref2BaseDataTab2" dojoType="ContentPane" label="Verweise">
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
	                </div>
	              </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <div class="half left">
	                  <span class="label"><label for="ref2BibData" onclick="javascript:dialog.showContextHelp(arguments[0], 'Weitere bibliographische Angaben')">Weitere bibliographische Angaben</label></span>
               		<span class="input"><input type="text" mode="textarea" id="ref2BibData" name="ref2BibData" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span> 
 	                </div>

	                <div class="half">
	                  <span class="label"><label for="ref2Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 'Erl&auml;uterungen')">Erl&auml;uterungen</label></span>
               		<span class="input"><input type="text" mode="textarea" id="ref2Explanation" name="ref2Explanation" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span> 
	                </div>
	                <div class="fill"></div>
	              </div>

	            </div>
	          </div>

	          <!-- FACHBEZUG CLASS 3 - DIENST/ANWENDUNG/INFORMATIONSSYSTEM //-->
	          <div id="refClass3" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('refClass3');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Fachbezug')">Fachbezug</div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="ref3Content" class="content">

	              <div class="inputContainer required">
	                <div class="half left">
	                  <span id="ref3ServiceTypeLabel" class="label required"><label for="ref3ServiceType" onclick="javascript:dialog.showContextHelp(arguments[0], 'Servicetyp')">Servicetyp*</label></span>
	                  <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Assistent', 'erfassung_assistent_capabilities.html', 755, 195, true);" title="Assistent [Popup]">Assistent</a></span>
	                  <span class="input spaceBelow">
<!-- 
						<input type="text" mode="textarea" id="ref3ServiceType" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
 -->
	                    <div dojoType="ingrid:ComboBox" autoComplete="false" class="w308" listId="5100" id="ref3ServiceType"></div>

					  </span>
	            	</div>

	                <div class="half">
	                  <span class="label"><label for="ref3ServiceVersion" onclick="javascript:dialog.showContextHelp(arguments[0], 'Version des Services')">Version des Services</label></span>
	                  <div class="tableContainer headHiddenRows4 half">
	                    <div class="cellEditors" id="ref3ServiceVersionEditors">
	                      <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="ref3ServiceVersionServiceType"></div>
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
	            	  </div>
	            	  <div class="fill"></div>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
	                  <span class="label"><label for="ref3SystemEnv" onclick="javascript:dialog.showContextHelp(arguments[0], 'Systemumgebung')">Systemumgebung</label></span>
               		<span class="input"><input type="text" mode="textarea" id="ref3SystemEnv" name="ref3SystemEnv" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span> 
<!-- 
	                  <span class="input"><textarea id="ref3SystemEnv" name="ref3SystemEnv" class="w320 h038" /></textarea></span>
 -->
 	                </div>
	          
	                <div class="half">
	                  <span class="label"><label for="ref3History" onclick="javascript:dialog.showContextHelp(arguments[0], 'Historie')">Historie</label></span>
               		<span class="input"><input type="text" mode="textarea" id="ref3History" name="ref3History" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span> 
<!--
	                  <span class="input"><textarea id="ref3History" name="ref3History" class="w320 h038" /></textarea></span>
-->
  	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
	                  <span class="label"><label for="ref3BaseDataTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 'Basisdaten')">Basisdaten</label></span>
	                  <span id="ref3MethodTab2Header" class="functionalLink onTab marginRightColumn"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Verweis anlegen/bearbeiten', 'mdek_links_dialog.html', 1010, 680, true, {filter: 3210});" title="Verweis anlegen/bearbeiten [Popup]">Verweis anlegen/bearbeiten</a></span>
	                	<div id="ref3BaseDataTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref3BaseDataTab1">
	                		<div id="ref3BaseDataTab1" dojoType="ContentPane" label="Text">
	                      	<span class="input">
	                      		<input type="text" mode="textarea" id="ref3BaseDataText" name="ref3BaseDataText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
	                      	</span>
	                		</div>
	                		<div id="ref3MethodTab2" dojoType="ContentPane" label="Verweise">
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
	                </div>

	                <div class="half">
	                  <span class="label"><label for="ref3Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 'Erl&auml;uterungen')">Erl&auml;uterungen</label></span>
               		<span class="input"><input type="text" mode="textarea" id="ref3Explanation" name="ref3Explanation" class="w320 h055" dojoType="ingrid:ValidationTextbox" /></span> 
	                </div>
	                <div class="fill"></div>
	          	  </div>

	              <div class="inputContainer noSpaceBelow notRequired h130">
	                <span class="label">Operationen</span>
	                <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Operation hinzuf&uuml;gen/bearbeiten', 'mdek_operation_dialog.html', 735, 745, true);" title="Operation hinzuf&uuml;gen/bearbeiten [Popup]">Operation hinzuf&uuml;gen/bearbeiten</a></span>
	                <div class="tableContainer rows4 full">
	            	    <table id="ref3Operation" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort">
	            	      <thead>
	            		      <tr>
	                  			<th nosort="true" field="name" dataType="String" width="165">Name</th>
	                  			<th nosort="true" field="description" dataType="String" width="510">Beschreibung</th>
	            		      </tr>
	            	      </thead>
	            	      <tbody>
	            	      </tbody>
	            	    </table>
	                </div>
	          	  </div>

	            </div>
	          </div>

	          <!-- FACHBEZUG CLASS 4 - VORHABEN/PROJEKT/PROGRAMM //-->
	          <div id="refClass4" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('refClass4');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Fachbezug')">Fachbezug</div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="ref4Content" class="content">

	              <div class="inputContainer notRequired">
	                <div class="half left">
	                  <span class="label"><label for="ref4ParticipantsTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 'Beteiligte')">Beteiligte</label></span>
	                  <span id="ref4ParticipantsTab2Header" class="functionalLink onTab marginRightColumn"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Adresse hinzuf&uuml;gen', 'mdek_address_dialog.html', 755, 580, true, {linkType: 3410});" title="Adresse hinzuf&uuml;gen [Popup]">Adresse hinzuf&uuml;gen</a></span>
	                	<div id="ref4ParticipantsTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref4ParticipantsTab1">
	                		<div id="ref4ParticipantsTab1" dojoType="ContentPane" label="Text">
	                      	<span class="input">
	                      		<input type="text" mode="textarea" id="ref4ParticipantsText" name="ref4ParticipantsText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
	                      	</span>
	                		</div>
	                		<div id="ref4ParticipantsTab2" dojoType="ContentPane" label="Verweise">
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
	                </div>

	                <div class="half">
	                  <span class="label"><label for="ref4PMTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 'Projektleiter')">Projektleiter</label></span>
	                  <span id="ref4PMTab2Header" class="functionalLink onTab marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Adresse hinzuf&uuml;gen', 'mdek_address_dialog.html', 755, 580, true, {linkType: 3400});" title="Adresse hinzuf&uuml;gen [Popup]">Adresse hinzuf&uuml;gen</a></span>
	                	<div id="ref4PMTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref4PMTab1">
	                		<div id="ref4PMTab1" dojoType="ContentPane" label="Text">
	                      	<span class="input">
	                      		<input type="text" mode="textarea" id="ref4PMText" name="ref4PMText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
	                      	</span>
	                		</div>
	                		<div id="ref4PMTab2" dojoType="ContentPane" label="Verweise">
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
	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <span class="label"><label for="ref4Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 'Erl&auml;uterungen')">Erl&auml;uterungen</label></span>
               		<span class="input"><input type="text" mode="textarea" id="ref4Explanation" name="ref4Explanation" class="w668 h055" dojoType="ingrid:ValidationTextbox" /></span> 
	          	  </div>

	            </div>
	          </div>

	          <!-- FACHBEZUG CLASS 5 - DATENSAMMLUNG/DATENBANK //-->
	          <div id="refClass5" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('refClass5');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Fachbezug')">Fachbezug</div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="ref5Content" class="content">

	              <div class="inputContainer notRequired h130">
	                <span class="label"><label for="ref5dbContent" onclick="javascript:dialog.showContextHelp(arguments[0], 'Inhalte der Datensammlung/Datenbank')">Inhalte der Datensammlung/Datenbank</label></span>
	                <div class="tableContainer rows4 full">
	                  <div class="cellEditors" id="ref5dbContentEditors">
	                    <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="ref5dbContentParameter"></div>
	                    <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="ref5dbContentAdditionalData"></div>
	                  </div>
	            	    <table id="ref5dbContent" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort interactive">
	            	      <thead>
	            		      <tr>
	                  			<th nosort="true" field="parameter" dataType="String" width="335" editor="ref5dbContentParameter">Parameter</th>
	                  			<th nosort="true" field="additionalData" dataType="String" width="340" editor="ref5dbContentAdditionalData">Erg&auml;nzende Angaben</th>
	            		      </tr>
	            	      </thead>
	            	      <tbody>
	            	      </tbody>
	            	    </table>
	                </div>
	          	  </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <div class="half left">
	                  <span class="label"><label for="ref5MethodTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 'Methode/Datengrundlage')">Methode/Datengrundlage</label></span>
	                  <span id="ref5MethodTab2Header" class="functionalLink onTab marginRightColumn"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Verweis anlegen/bearbeiten', 'mdek_links_dialog.html', 1010, 680, true, {filter: 3100});" title="Verweis anlegen/bearbeiten [Popup]">Verweis anlegen/bearbeiten</a></span>
	                	<div id="ref5MethodTabContainer" dojoType="ingrid:TabContainer" class="h088" selectedChild="ref5MethodTab1">
	                		<div id="ref5MethodTab1" dojoType="ContentPane" label="Text">
	                      	<span class="input">
	                      		<input type="text" mode="textarea" id="ref5MethodText" name="ref5MethodText" class="w320 h038" dojoType="ingrid:ValidationTextbox" />
	                      	</span>
	                		</div>
	                		<div id="ref5MethodTab2" dojoType="ContentPane" label="Verweise">
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
	                </div>

	                <div class="half">
	                  <span class="label"><label for="ref5Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 'Erl&auml;uterungen')">Erl&auml;uterungen</label></span>
               		<span class="input"><input type="text" mode="textarea" id="ref5Explanation" name="ref5Explanation" class="w320 h055" dojoType="ingrid:ValidationTextbox" /></span> 
  	                </div>
	                <div class="fill"></div>
	          	  </div>
	          	  
	            </div>
	          </div>
	          
	          <!-- RAUMBEZUG //-->
	          <div id="spatialRef" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('spatialRef');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Raumbezug')">Raumbezug</div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="spatialRefContent" class="content">

	              <div id="spatialRefAdminUnitContainer" class="inputContainer noSpaceBelow h130">
	                <span id="spatialRefAdminUnitLabel" class="label required"><label for="spatialRefAdminUnit" onclick="javascript:dialog.showContextHelp(arguments[0], 'Geothesaurus-Raumbezug')">Geothesaurus-Raumbezug*</label></span>
	                <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Geothesaurus-Navigator', 'mdek_spatial_navigator.html', 530, 230, true);" title="Geothesaurus-Navigator [Popup]">Geothesaurus-Navigator</a></span>
	                <div class="tableContainer rows4 full">
	            	    <table id="spatialRefAdminUnit" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort interactive">
	            	      <thead>
	            		      <tr>
	                  			<th nosort="true" field="name" dataType="String" width="315">Geothesaurus-Raumbezug</th>
	                  			<th nosort="true" field="longitude1" dataType="String" width="90">L&auml;nge 1</th>
	                  			<th nosort="true" field="latitude1" dataType="String" width="90">Breite 1</th>
	                  			<th nosort="true" field="longitude2" dataType="String" width="90">L&auml;nge 2</th>
	                  			<th nosort="true" field="latitude2" dataType="String" width="90">Breite 2</th>
	            		      </tr>
	            	      </thead>
	            	      <tbody>
	            	      </tbody>
	            	    </table>
	                </div>
	          	  </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <div id="spatialRefCoordsAdminUnit" class="infobox">
	                  <span class="icon"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span>
	                  <span class="title"><a href="javascript:toggleInfo('spatialRefCoordsAdminUnit');" title="Info aufklappen">Umgerechnete Koordinaten:
	                    <img id="spatialRefCoordsAdminUnitToggle" src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a>
	                  </span>
	                  <div id="spatialRefCoordsAdminUnitContent" style="display:block;">
	                    <div class="left" style="float:left; margin-top:10px;">
	                    	<select id="spatialRefAdminUnitSelect" dojoType="ingrid:Select" toggle="plain" style="width:250px;">
	                    		<option value="GEO84">Bezugssystem WGS84</option>
	                    		<option value="GEO_BESSEL_POTSDAM">Bezugssystem Bessel/Potsdam</option>
	                    		<option value="GK2">GK-Abbildung im 2. Meridianstreifen</option>
	                    		<option value="GK3">GK-Abbildung im 3. Meridianstreifen</option>
	                    		<option value="GK4">GK-Abbildung im 4. Meridianstreifen</option>
	                    		<option value="GK5">GK-Abbildung im 5. Meridianstreifen</option>
	                    		<option value="UTM32W">UTM-32 mit f&uuml;hrender Zonenangabe</option>
	                    		<option value="UTM33W">UTM-33 mit f&uuml;hrender Zonenangabe</option>
	                    		<option value="UTM32S">UTM-32 ohne f&uuml;hrender Zonenangabe</option>
	                    		<option value="UTM33S">UTM-33 ohne f&uuml;hrender Zonenangabe</option>
	                    		<option value="LAMGW">Lambert-Abbildung</option>
	                    	</select>
	                    </div>

						<div class="headHiddenRows1 thirdInside3">
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
	              </div>

	              <div id="spatialRefLocationContainer" class="inputContainer noSpaceBelow notRequired h130">
	                <span id="spatialRefLocationLabel" class="label required"><label for="spatialRefLocation" onclick="javascript:dialog.showContextHelp(arguments[0], 'Freier Raumbezug')">Freier Raumbezug*</label></span>
	                <span class="functionalLink">
	                	<img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Raumbezug hinzuf&uuml;gen', 'mdek_spatial_assist_dialog.html', 505, 220, true);" title="Raumbezug Assistent [Popup]">Raumbezug hinzuf&uuml;gen</a>
	                	<img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:void(0);" title="Koordinate mit geografischer Suche aussuchen [Popup]">Koordinate mit geografischer Suche aussuchen</a>
	                </span>
	                <div class="tableContainer rows4 full">
	                  <div class="cellEditors" id="spatialRefLocationEditors">
	                    <div dojoType="ingrid:ComboBox" toggle="plain" style="width:300px;" listId="1100" id="freeReferencesEditor"></div>
	                    <div dojoType="RealNumberTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="latitude1Editor"></div>
	                    <div dojoType="RealNumberTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="longitude1Editor"></div>
	                    <div dojoType="RealNumberTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="latitude2Editor"></div>
	                    <div dojoType="RealNumberTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="longitude2Editor"></div>
	                  </div>
	            	    <table id="spatialRefLocation" dojoType="ingrid:FilteringTable" minRows="4" headClass="fixedHeader" cellspacing="0" class="filteringTable nosort interactive">
	            	      <thead>
	            		      <tr>
	                  			<th nosort="true" field="name" dataType="String" width="315" editor="freeReferencesEditor">Freier Raumbezug</th>
	                  			<th nosort="true" field="longitude1" dataType="String" width="90" editor="longitude1Editor">L&auml;nge 1</th>
	                  			<th nosort="true" field="latitude1" dataType="String" width="90" editor="latitude1Editor">Breite 1</th>
	                  			<th nosort="true" field="longitude2" dataType="String" width="90" editor="longitude2Editor">L&auml;nge 2</th>
	                  			<th nosort="true" field="latitude2" dataType="String" width="90" editor="latitude2Editor">Breite 2</th>
	            		      </tr>
	            	      </thead>
	            	      <tbody>
	            	      </tbody>
	            	    </table>
	                </div>
	          	  </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <div id="spatialRefCoordsLocation" class="infobox">
	                  <span class="icon"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span>
	                  <span class="title"><a href="javascript:toggleInfo('spatialRefCoordsLocation');" title="Info aufklappen">Umgerechnete Koordinaten:
	                    <img src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a></span>
	                  <div id="spatialRefCoordsLocationContent" style="display:block;">
	                    <div class="left" style="float:left; margin-top:10px;">
	                    	<select id="spatialRefLocationSelect" dojoType="ingrid:Select" toggle="plain" style="width:250px;">
	                    		<option value="GEO84">Bezugssystem WGS84</option>
	                    		<option value="GEO_BESSEL_POTSDAM">Bezugssystem Bessel/Potsdam</option>
	                    		<option value="GK2">GK-Abbildung im 2. Meridianstreifen</option>
	                    		<option value="GK3">GK-Abbildung im 3. Meridianstreifen</option>
	                    		<option value="GK4">GK-Abbildung im 4. Meridianstreifen</option>
	                    		<option value="GK5">GK-Abbildung im 5. Meridianstreifen</option>
	                    		<option value="UTM32W">UTM-32 mit f&uuml;hrender Zonenangabe</option>
	                    		<option value="UTM33W">UTM-33 mit f&uuml;hrender Zonenangabe</option>
	                    		<option value="UTM32S">UTM-32 ohne f&uuml;hrender Zonenangabe</option>
	                    		<option value="UTM33S">UTM-33 ohne f&uuml;hrender Zonenangabe</option>
	                    		<option value="LAMGW">Lambert-Abbildung</option>
	                    	</select>
	                    </div>

						<div class="headHiddenRows1 thirdInside3">
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
	              </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <div class="half left">
	                  <span class="label">H&ouml;he</span>
	                  <div id="spatialRefAltitude">
	                    <span class="entry">
	                      <span class="label"><label for="spatialRefAltMin" onclick="javascript:dialog.showContextHelp(arguments[0], 'Minimum')">Minimum</label></span>
	                      <span class="input"><input type="text" id="spatialRefAltMin" name="spatialRefAltMin" class="w080" dojoType="RealNumberTextBox" /></span>
	                    </span>
	                    <span class="entry">
	                      <span class="label"><label for="spatialRefAltMax" onclick="javascript:dialog.showContextHelp(arguments[0], 'Maximum')">Maximum</label></span>
	                      <span class="input"><input type="text" id="spatialRefAltMax" name="spatialRefAltMax" class="w080" dojoType="RealNumberTextBox" /></span>
	                    </span>
	                    <span class="entry">
	                      <span class="label"><label for="spatialRefAltMeasure" onclick="javascript:dialog.showContextHelp(arguments[0], 'Ma&szlig;einheit')">Ma&szlig;einheit</label></span>
	                      <span class="input"><input dojoType="ingrid:Select" style="width:58px;" listId="102" id="spatialRefAltMeasure" /></span>
	                    </span>
	                    <span class="entry">
	                      <span class="label"><label for="spatialRefAltVDate" onclick="javascript:dialog.showContextHelp(arguments[0], 'Vertikaldatum')">Vertikaldatum</label></span>
	                      <span class="input"><input dojoType="ingrid:Select" style="width:266px;" listId="101" id="spatialRefAltVDate" /></span>
	                    </span>
	                  </div>
	                </div>
	          
	                <div class="half">
	                  <span class="label"><label for="spatialRefExplanation" onclick="javascript:dialog.showContextHelp(arguments[0], 'Erl&auml;uterungen')">Erl&auml;uterungen</label></span>
               		<span class="input"><input type="text" mode="textarea" id="spatialRefExplanation" name="spatialRefExplanation" class="w320 h118" dojoType="ingrid:ValidationTextbox" /></span> 
<!--
               		  <span class="input"><input type="text" id="spatialRefExplanation" name="spatialRefExplanation" class="w320 h118" dojoType="ingrid:ValidationTextBox" /></span> 
-->
 	                </div>
	                <div class="fill"></div>
	              </div>

	            </div>
	          </div>

	          <!-- ZEITBEZUG //-->
	          <div id="timeRef" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('timeRef');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Zeitbezug')">Zeitbezug</div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="timeRefContent" class="content">


	              <div class="inputContainer noSpaceBelow">
	                <div class="half left">
	                  <span id="timeRefTableLabel" class="label required"><label for="timeRefTable" onclick="javascript:dialog.showContextHelp(arguments[0], 'Zeitbezug des Datensatzes')">Zeitbezug des Datensatzes*</label></span>
	                  <div class="tableContainer rows4 half">
	                    <div class="cellEditors" id="timeRefTableEditors">
	                      <div dojoType="ingrid:DropdownDatePicker" displayFormat="dd.MM.yyyy" toggle="plain" widgetId="timeRefDateDatePicker"></div>
	                      <div dojoType="ingrid:Select" toggle="plain" style="width:155px;" listId="502" id="timeRefTypeCombobox"></div>
	                    </div>
	              	    <table id="timeRefTable" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y" minRows="4" cellspacing="0" class="filteringTable interactive nosort">
	              	      <thead>
	              		      <tr>
	                    			<th nosort="true" field="date" dataType="Date" width="120" editor="timeRefDateDatePicker">Datum</th>
	                    			<th nosort="true" field="type" dataType="String" width="200" editor="timeRefTypeCombobox">Typ</th>
	              		      </tr>
	              	      </thead>
	              	      <tbody>
	              	      </tbody>
	              	    </table>
	                  </div>
	            	  </div>
	          
	                <div class="half">
	                  <span class="label"><label for="timeRefExplanation" onclick="javascript:dialog.showContextHelp(arguments[0], 'Erl&auml;uterungen')">Erl&auml;uterungen</label></span>
               		  <span class="input"><input type="text" mode="textarea" id="timeRefExplanation" name="timeRefExplanation" class="w320 h105" dojoType="ingrid:ValidationTextbox" /></span> 
	                </div>

	                <div class="fill"></div>
	              </div>
	        	  
	              <div class="inputContainer notRequired">
	                <div class="half left">
	                  <span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 'Zeitbezug des Dateninhaltes')">Zeitbezug des Dateninhaltes</label></span>
	                  <div id="timeRefRef">
	                    <span class="entry first">
	                      <span class="label hidden"><label for="timeRefType">Typ</label></span>
                		  <span class="input">
                		  	<select dojoType="ingrid:Select" autoComplete="false" style="width:61px;" id="timeRefType">
                		  	<option value="1">Erstellung</option>
                		  	<option value="2">Publikation</option>
                		  	<option value="3">letzte &Auml;nderung</option>
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
	                </div>
	          
	                <div class="half">
	                  <span class="label"><label for="timeRefStatus" onclick="javascript:dialog.showContextHelp(arguments[0], 'Status')">Status</label></span>
	                  <span class="input"><input dojoType="ingrid:Select" style="width:301px;" listId="523" id="timeRefStatus" /></span>
	                </div>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
	                  <span class="label"><label for="timeRefPeriodicity" onclick="javascript:dialog.showContextHelp(arguments[0], 'Periodizit&auml;t')">Periodizit&auml;t</label></span>
	                  <span class="input"><input dojoType="ingrid:Select" style="width:302px;" listId="518" id="timeRefPeriodicity" /></span>
	                </div>
	          
	                <div class="half">
	                  <span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 'Im Intervall')">Im Intervall</label></span>
	                  <div id="timeRefInterval">
	                    <span>Alle</span>
	                    <span class="label hidden"><label for="timeRefIntervalNum">Intervall Anzahl</label></span>
	                    <span class="input"><input type="text" id="timeRefIntervalNum" name="timeRefIntervalNum" class="w038" dojoType="ingrid:ValidationTextBox" /></span>
	                    <span class="label hidden"><label for="timeRefIntervalUnit">Intervall Einheit</label></span>
	                    <span class="input"><input dojoType="ingrid:Select" style="width:223px;" listId="1230" id="timeRefIntervalUnit" /></span>
	                  </div>
	                </div>
	                <div class="fill"></div>
	              </div>

	            </div>
	          </div>

	          <!-- ZUSATZINFORMATION //-->
	          <div id="extraInfo" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('extraInfo');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Zusatzinformation')">Zusatzinformation</div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="extraInfoContent" class="content">
	        	  
	              <div class="inputContainer">
	                <div class="half left">
	                  <span id="extraInfoLangMetaDataLabel" class="label required"><label for="extraInfoLangMetaData" onclick="javascript:dialog.showContextHelp(arguments[0], 'Sprache des Metadatensatzes')">Sprache des Metadatensatzes*</label></span>
	                  <span class="input"><input dojoType="ingrid:Select" autoComplete="false" style="width:302px;" listId="99999999" id="extraInfoLangMetaData" /></span>
	                </div>
	          
	                <div class="half">
	                  <span id="extraInfoLangDataLabel" class="label required"><label for="extraInfoLangData" onclick="javascript:dialog.showContextHelp(arguments[0], 'Sprache des Datensatzes')">Sprache des Datensatzes*</label></span>
	                  <span class="input"><input dojoType="ingrid:Select" autoComplete="false" style="width:302px;" listId="99999999" id="extraInfoLangData" /></span>
	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer">
	                <div class="half left">
	                  <span class="label required"><label id="extraInfoPublishAreaLabel" for="extraInfoPublishArea" onclick="javascript:dialog.showContextHelp(arguments[0], 'Ver&ouml;ffentlichung')">Ver&ouml;ffentlichung*</label></span>
	                  <span class="input"><input dojoType="ingrid:Select" autoComplete="false" style="width:302px;" listId="3571" id="extraInfoPublishArea" /></span>
 	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer notRequired h110">
	                <div class="third1 left">
	                  <span class="label"><label for="extraInfoXMLExportTable" onclick="javascript:dialog.showContextHelp(arguments[0], 'XML-Export-Kriterium')">XML-Export-Kriterium</label></span>
	                  <div class="tableContainer headHiddenRows4 third1">
	                    <div class="cellEditors" id="extraInfoXMLExportTableEditors">
	                      <div dojoType="ingrid:ComboBox" toggle="plain" style="width:161px;" listId="1370" id="extraInfoXMLExportTableEditor"></div>
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
	            	  </div>
	          
	                <div class="third2">
	                  <span class="label"><label for="extraInfoLegalBasicsTable" onclick="javascript:dialog.showContextHelp(arguments[0], 'Rechtliche Grundlagen')">Rechtliche Grundlagen</label></span>
	                  <div class="tableContainer headHiddenRows4 third2">
	                    <div class="cellEditors" id="extraInfoLegalBasicsTableEditors">
	                      <div dojoType="ingrid:ComboBox" toggle="plain" style="width:397px;" listId="1350" id="extraInfoLegalBasicsTableEditor"></div>
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
	            	  </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <div class="half left">
	                  <span class="label"><label for="extraInfoPurpose" onclick="javascript:dialog.showContextHelp(arguments[0], 'Herstellungszweck')">Herstellungszweck</label></span>
               		<span class="input"><input type="text" mode="textarea" id="extraInfoPurpose" name="extraInfoPurpose" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span> 
	                </div>
	          
	                <div class="half">
	                  <span class="label"><label for="extraInfoUse" onclick="javascript:dialog.showContextHelp(arguments[0], 'Eignung/Nutzung')">Eignung/Nutzung</label></span>
               		<span class="input"><input type="text" mode="textarea" id="extraInfoUse" name="extraInfoUse" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span> 
	                </div>
	                <div class="fill"></div>
	              </div>

	            </div>
	          </div>

	          <!-- VERFÜGBARKEIT //-->
	          <div id="availability" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('availability');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Verf&uuml;gbarkeit')">Verf&uuml;gbarkeit</div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="availabilityContent" class="content">
	        	  
	              <div class="inputContainer notRequired h130">
	                <span class="label"><label for="availabilityDataFormat" onclick="javascript:dialog.showContextHelp(arguments[0], 'Datenformat')">Datenformat</label></span>
	                <div class="tableContainer rows4 full">
	                  <div class="cellEditors" id="availabilityDataFormatEditors">
<!-- 
	                    <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="availabilityDataFormatName"></div>
 -->
                        <div dojoType="ingrid:ComboBox" toggle="plain" style="width:150px;" listId="1320" widgetId="availabilityDataFormatName"></div>
	                    <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="availabilityDataFormatVersion"></div>
	                    <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="availabilityDataFormatCompression"></div>
	                    <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="availabilityDataFormatPixelDepth"></div>
	                  </div>
	            	    <table id="availabilityDataFormat" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable interactive nosort">
	            	      <thead>
	            		      <tr>
	                  			<th nosort="true" field="name" dataType="String" width="170" editor="availabilityDataFormatName">Name</th>
	                  			<th nosort="true" field="version" dataType="String" width="125" editor="availabilityDataFormatVersion">Version</th>
	                  			<th nosort="true" field="compression" dataType="String" width="205" editor="availabilityDataFormatCompression">Kompressionstechnik</th>
	                  			<th nosort="true" field="pixelDepth" dataType="String" width="175" editor="availabilityDataFormatPixelDepth">Bildpunkttiefe</th>
	            		      </tr>
	            	      </thead>
	            	      <tbody>
	            	      </tbody>
	            	    </table>
	                </div>
	              </div>

	              <div class="inputContainer notRequired h130">
	                <span class="label"><label for="availabilityMediaOptions" onclick="javascript:dialog.showContextHelp(arguments[0], 'Medienoption')">Medienoption</label></span>
	                <div class="tableContainer rows4 full">
	                  <div class="cellEditors" id="availabilityMediaOptionsEditors">
	                    <div dojoType="ingrid:Select" toggle="plain" style="width:117px;" listId="520" id="availabilityMediaOptionsMediumCombobox"></div>
	                    <div dojoType="RealNumberTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="availabilityMediaOptionsSize"></div>
	                    <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="availabilityMediaOptionsLocation"></div>
	                  </div>
	            	    <table id="availabilityMediaOptions" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort interactive">
	            	      <thead>
	            		      <tr>
	                  			<th nosort="true" field="name" dataType="String" width="150" editor="availabilityMediaOptionsMediumCombobox">Medium</th>
	                  			<th nosort="true" field="transferSize" dataType="String" width="250" editor="availabilityMediaOptionsSize">Datenvolumen (MB)</th>
	                  			<th nosort="true" field="location" dataType="String" width="275" editor="availabilityMediaOptionsLocation">Speicherort</th>
	            		      </tr>
	            	      </thead>
	            	      <tbody>
	            	      </tbody>
	            	    </table>
	                </div>
	              </div>

	              <div class="inputContainer notRequired">
	                <div class="half left">
	                  <span class="label"><label for="availabilityOrderInfo" onclick="javascript:dialog.showContextHelp(arguments[0], 'Bestellinformation')">Bestellinformation</label></span>
               		<span class="input"><input type="text" mode="textarea" id="availabilityOrderInfo" name="availabilityOrderInfo" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span> 

	                </div>
	          
	                <div class="half">
	                  <span class="label"><label for="availabilityCosts" onclick="javascript:dialog.showContextHelp(arguments[0], 'Kosten')">Kosten</label></span>
               		<span class="input"><input type="text" mode="textarea" id="availabilityCosts" name="availabilityCosts" class="w320 h038" dojoType="ingrid:ValidationTextbox" /></span> 

 	                </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer noSpaceBelow notRequired">
	                <div class="full">
	                  <span class="label"><label for="availabilityNoteUse" onclick="javascript:dialog.showContextHelp(arguments[0], 'Nutzungsanmerkung')">Nutzungsanmerkung</label></span>
               		<span class="input"><input type="text" mode="textarea" id="availabilityNoteUse" name="availabilityNoteUse" class="w668 h038" dojoType="ingrid:ValidationTextbox" /></span> 
	                </div>
	                <div class="fill"></div>
	              </div>

	            </div>
	          </div>

	          <!-- VERSCHLAGWORTUNG //-->
	          <div id="thesaurus" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('thesaurus');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Verschlagwortung')">Verschlagwortung</div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="thesaurusContent" class="content">
	        	  
	              <div class="inputContainer h110">
	                <span id="thesaurusTermsLabel" class="label required"><label for="thesaurusTerms" onclick="javascript:dialog.showContextHelp(arguments[0], 'Thesaurus-Suchbegriffe')">Thesaurus-Suchbegriffe (mindestens 3)*</label></span>
	                <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Verschlagwortungsassistent', 'mdek_thesaurus_assist_dialog.html', 735, 430, true);" title="Verschlagwortungsassistent [Popup]">Verschlagwortungsassistent</a>
	                  <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Thesaurus-Navigator', 'mdek_thesaurus_dialog.html', 1010, 430, true, {dstTable: 'thesaurusTerms'});" title="Thesaurus-Navigator [Popup]">Thesaurus-Navigator</a></span>
	                <div class="tableContainer headHiddenRows4 full">
	            	    <table id="thesaurusTerms" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
	            	      <thead>
	            		      <tr>
	                  			<th nosort="true" field="title" dataType="String">&nbsp;</th>
	            		      </tr>
	            	      </thead>
	            	    </table>
	                </div>
	              </div>

	              <div class="inputContainer h116">
	                <div class="half left">
	                  <span id="thesaurusTopicsLabel" class="label required"><label for="thesaurusTopics" onclick="javascript:dialog.showContextHelp(arguments[0], 'Themenkategorie')">Themenkategorie*</label></span>
	                  <div class="tableContainer headHiddenRows4 half">
	                    <div class="cellEditors" id="thesaurusTopicsEditors">
	                      <div dojoType="ingrid:Select" toggle="plain" style="width:260px;" listId="527" id="thesaurusTopicsCombobox"></div>
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
	            	  </div>

	                <div class="half">
	                  <span class="label"><label for="thesaurusFreeTerms" onclick="javascript:dialog.showContextHelp(arguments[0], 'Freie Suchbegriffe')">Freie Suchbegriffe</label></span>
	                  <div class="tableContainer spaceBelow headHiddenRows3 half">
	              	    <table id="thesaurusFreeTermsList" dojoType="ingrid:FilteringTable" minRows="3" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
	              	      <thead>
	              		      <tr>
	                    			<th nosort="true" field="title" dataType="String">Freie Suchbegriffe</th>
	              		      </tr>
	              	      </thead>
	              	      <tbody>
	              	      </tbody>
	              	    </table>
	              	  </div>

	                  <span class="input" style="position:relative; top:-8px; float:left; width:238px;">
                  		<input type="text" id="thesaurusFreeTerms" name="thesaurusFreeTerms" class="w238 nextToButton aboveTable" dojoType="ingrid:ValidationTextBox" />
					  </span>
	                  <span style="position:relative; top:-8px; float:right;">
						<button id="thesaurusFreeTermsAddButton" dojoType="ingrid:Button">Hinzuf&uuml;gen</button>
					  </span>
	            	  </div>
	                <div class="fill"></div>
	              </div>

	              <div class="inputContainer noSpaceBelow notRequired h185">
	                <span class="label">Umweltthemen</span>
	                <div id="thesaurusEnvironment" class="outlined h140">
	                  <div class="checkboxContainer">
	                    <span class="input"><input type="checkbox" name="thesaurusEnvExtRes" id="thesaurusEnvExtRes" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 'Als Katalogseite anzeigen')">Als Katalogseite anzeigen</label></span>
	                  </div>
	                  
	                  <div class="halfInside left">
	                    <span id="thesaurusEnvTopicsLabel" class="label"><label for="thesaurusEnvTopics" onclick="javascript:dialog.showContextHelp(arguments[0], 'Themen')">Themen</label></span>
	                    <div class="tableContainer headHiddenRows4 halfInside">
	                      <div class="cellEditors" id="thesaurusEnvTopicsEditors">
	                        <div dojoType="ingrid:Select" toggle="plain" style="width:240px;" listId="1400" id="thesaurusEnvTopicsCombobox"></div>
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
	                  </div>

	                  <div class="halfInside">
	                    <span id="thesaurusEnvCatsLabel" class="label"><label for="thesaurusEnvCats" onclick="javascript:dialog.showContextHelp(arguments[0], 'Kategorien')">Kategorien</label></span>
	                    <div class="tableContainer headHiddenRows4 halfInside">
	                      <div class="cellEditors" id="thesaurusEnvCatsEditors">
	                        <div dojoType="ingrid:Select" toggle="plain" style="width:240px;" listId="1410" id="thesaurusEnvCatsCombobox"></div>
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
	                  </div>
	                  <div class="fill"></div>
	                </div>
	              </div>

	            </div>
	          </div>

	          <!-- VERWEISE //-->
	          <div id="links" class="contentBlock">
	          	<div class="titleBar">
	          	  <div class="titleIcon"><a href="javascript:toggleFields('links');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
	        	    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Verweise')">Verweise</div>
	        	    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
	          	</div>
	        	  <div id="linksContent" class="content">
	        	  
	              <div class="inputContainer noSpaceBelow notRequired h164">
	                <div class="half left">
	                  <span class="label">Verweise zu</span>
	                  <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Verweis anlegen/bearbeiten', 'mdek_links_dialog.html', 1010, 680, true);" title="Verweis anlegen/bearbeiten [Popup]">Verweis anlegen/bearbeiten</a></span>
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
	            	  </div>

	                <div class="half">
	                  <span class="label">Verweise von</span>
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
	            	  </div>
	              <div class="fill"></div>
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
		  <form dojoType="ingrid:Form" method="get" id="headerFormAddress" action="">
			<table cellspacing="0">
		  	<tbody>
		  		<tr>
		  		  <td class="label required"><label for="addressTitle">Adresstitel*</label></td>
		  		  <td colspan="2"><input type="text" id="addressTitle" name="addressTitle" class="w550" disabled="true" dojoType="ingrid:ValidationTextBox" /></td></tr>
		  		<tr>
		  		  <td id="addressTypeLabel" class="label required col1"><label for="addressType">Adresstyp*</label></td>
		  		  <td class="col2">
		          <select dojoType="ingrid:Select" autoComplete="false" style="width:386px;" id="addressType" name="addressType">
		            <!-- TODO: fill in jsp -->
		            <option value="AddressType0">Institution</option>
		            <option value="AddressType1">Einheit</option>
		            <option value="AddressType2">Person</option>
		            <option value="AddressType3">Freie Adresse</option>
		          </select>
		        </td>
		  		  <td class="col3"><img src="img/lock.gif" width="9" height="14" alt="gesperrt" /></td></tr>
		  		<tr>
		  		  <td class="label"><label for="addressOwner">Verantwortlicher</label></td>
		  		  <td><input dojoType="ingrid:Select" style="width:386px;" id="addressOwner" disabled="true" /></td>
	              <td class="note"><strong>Status:</strong> <span id="addressWorkState"></span></td>

		  		<tr>
	              <td class="note" colspan="3"><strong>Erstellt am:</strong> <span id="addressCreationTime">----------</span> | <strong>Ge&auml;ndert am:</strong> <span id="addressModificationTime">----------</span> | <strong>Von:</strong> <span id="addressLastEditor">---</span></td>
				</tr>
		  	</tbody>
			</table>
			</form>
		</div>
		<div dojoType="ContentPane" widgetId="contentFrameAddress" id="contentFrameAddress" class="sectionBottom" style="overflow:auto;">
			
      <form dojoType="ingrid:FormErfassungAdresseContent" widgetId="contentFormAddress" selectedClass="AddressType0" method="get" id="contentFormAddress" action="">

			<div dojoType="ContentPane" id="contentFrameBodyAddress" class="sectionBottomContent">

			  <!-- GREY FIELD //-->
			  <!-- ADDRESS TYPE 0 //-->
			  <div id="headerAddressType0" class="contentBlock firstBlock grey">
				<div id="headerAddressType0Content" class="content">

			      <div class="inputContainer">
			        <span class="label"><label for="headerAddressType0Institution" onclick="javascript:dialog.showContextHelp(arguments[0], 'Institution/&uuml;bergeordnete Einheit(en)')">Institution/&uuml;bergeordnete Einheit(en)</label></span>
			        <span class="input"><input type="text" mode="textarea" dojoType="ingrid:ValidationTextbox" id="headerAddressType0Institution" class="w668 h038" disabled="true" /></span>
			  	  </div>

			      <div class="inputContainer noSpaceBelow">
			        <span id="headerAddressType0UnitLabel" class="label required"><label for="headerAddressType0Unit" onclick="javascript:dialog.showContextHelp(arguments[0], 'Institution')">Institution*</label></span>
			        <span class="input"><input type="text" mode="textarea" dojoType="ingrid:ValidationTextbox" required="true" id="headerAddressType0Unit" class="w668 h038" /></span>
			  	  </div>

			    </div>
			  </div>

			  <!-- ADDRESS TYPE 1 //-->
			  <div id="headerAddressType1" class="contentBlock firstBlock grey">
				  <div id="headerAddressType1Content" class="content">

			      <div class="inputContainer">
			        <span class="label"><label for="headerAddressType1Institution" onclick="javascript:dialog.showContextHelp(arguments[0], 'Institution/&uuml;bergeordnete Einheit(en)')">Institution/&uuml;bergeordnete Einheit(en)</label></span>
			        <span class="input"><input type="text" mode="textarea" dojoType="ingrid:ValidationTextbox" id="headerAddressType1Institution" class="w668 h038" disabled=true" /></span>
			  	  </div>
			  	  
			      <div class="inputContainer noSpaceBelow">
			        <span id="headerAddressType1UnitLabel" class="label required"><label for="headerAddressType1Unit" onclick="javascript:dialog.showContextHelp(arguments[0], 'Einheit')">Einheit*</label></span>
			        <span class="input"><input type="text" mode="textarea" dojoType="ingrid:ValidationTextbox" required="true" id="headerAddressType1Unit" class="w668 h038" /></span>
			  	  </div>

			    </div>
			  </div>

			  <!-- ADDRESS TYPE 2 //-->
			  <div id="headerAddressType2" class="contentBlock firstBlock grey">
				  <div id="headerAddressType2Content" class="content">

			      <div class="inputContainer">
			        <span class="label"><label for="headerAddressType2Institution" onclick="javascript:dialog.showContextHelp(arguments[0], 'Institution/&uuml;bergeordnete Einheit(en)')">Institution/&uuml;bergeordnete Einheit(en)</label></span>
			        <span class="input"><input type="text" mode="textarea" dojoType="ingrid:ValidationTextbox" id="headerAddressType2Institution" class="w668 h038" disabled="true" /></span>
			  	  </div>
			  	  
			      <div class="inputContainer">
			        <div class="half left">
			          <span id="headerAddressType2LastnameLabel" class="label required"><label for="headerAddressType2Lastname" onclick="javascript:dialog.showContextHelp(arguments[0], 'Name')">Name*</label></span>
			          <span class="input"><input type="text" id="headerAddressType2Lastname" required="true" name="headerAddressType2Lastname" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
			        </div>
			  
			        <div class="half">
			          <span id="headerAddressType2FirstnameLabel" class="label"><label for="headerAddressType2Firstname" onclick="javascript:dialog.showContextHelp(arguments[0], 'Vorname')">Vorname</label></span>
			          <span class="input"><input type="text" id="headerAddressType2Firstname" name="headerAddressType2Firstname" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
			        </div>
			        <div class="fill"></div>
			  	  </div>

			      <div class="inputContainer noSpaceBelow">
			        <div class="half left">
			          <span id="headerAddressType2StyleLabel" class="label required"><label for="headerAddressType2Style" onclick="javascript:dialog.showContextHelp(arguments[0], 'Anrede')">Anrede*</label></span>
			          <span class="input"><input dojoType="ingrid:ComboBox" style="width:129px;" listId="4300" id="headerAddressType2Style" /></span>
			        </div>
			  
			        <div class="half">
			          <span id="headerAddressType2TitleLabel" class="label"><label for="headerAddressType2Title" onclick="javascript:dialog.showContextHelp(arguments[0], 'Titel')">Titel</label></span>
			          <span class="input"><input dojoType="ingrid:ComboBox" style="width:129px;" listId="4305" id="headerAddressType2Title" /></span>
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
			          <span id="headerAddressType3LastnameLabel" class="label required"><label for="headerAddressType3Lastname" onclick="javascript:dialog.showContextHelp(arguments[0], 'Name')">Name*</label></span>
			            <span class="input"><input type="text" id="headerAddressType3Lastname" required="true" name="headerAddressType3Lastname" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
			        </div>
			  
			        <div class="half">
			          <span id="headerAddressType3FirstnameLabel" class="label"><label for="headerAddressType3Firstname" onclick="javascript:dialog.showContextHelp(arguments[0], 'Vorname')">Vorname</label></span>
			            <span class="input"><input type="text" id="headerAddressType3Firstname" name="headerAddressType3Firstname" class="w320" dojoType="ingrid:ValidationTextBox" /></span>
			        </div>
			        <div class="fill"></div>
			  	  </div>

			      <div class="inputContainer">
			        <div class="half left">
			          <span id="headerAddressType3StyleLabel" class="label required"><label for="headerAddressType3Style" onclick="javascript:dialog.showContextHelp(arguments[0], 'Anrede')">Anrede*</label></span>
			          <span class="input"><input dojoType="ingrid:ComboBox" style="width:129px;" listId="4300" id="headerAddressType3Style" /></span>
			        </div>
			  
			        <div class="half">
			          <span class="label"><label for="headerAddressType3Title" onclick="javascript:dialog.showContextHelp(arguments[0], 'Titel')">Titel</label></span>
			          <span class="input"><input dojoType="ingrid:ComboBox" style="width:129px;" listId="4305" id="headerAddressType3Title" /></span>
			        </div>
			        <div class="fill"></div>
			  	  </div>

			      <div class="inputContainer noSpaceBelow">
			        <span class="label"><label for="headerAddressType3Institution" onclick="javascript:dialog.showContextHelp(arguments[0], 'Institution')">Institution</label></span>
			        <span class="input"><input type="text" mode="textarea" dojoType="ingrid:ValidationTextbox" id="headerAddressType3Institution" class="w668 h038" /></span>
			  	  </div>

			    </div>
			  </div>

			  <!-- ADRESSE UND AUFGABEN //-->
			  <div id="address" class="contentBlock">
			  	<div class="titleBar">
			  	  <div class="titleIcon"><a href="javascript:toggleFields('address');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
				    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Adresse und Aufgaben')">Adresse und Aufgaben</div>
				    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
			  	</div>
				  <div id="addressContent" class="content">

			      <div class="inputContainer">
			        <div class="half left">
			          <span id="addressStreetLabel" class="label required"><label for="addressStreet" onclick="javascript:dialog.showContextHelp(arguments[0], 'Stra&szlig;e/Hausnummer')">Stra&szlig;e/Hausnummer*</label></span>
			          <span class="input spaceBelow"><input type="text" id="addressStreet" name="addressStreet" class="w320" dojoType="ingrid:ValidationTextBox" /></span>

			          <div id="addressDetails1">
			            <span class="entry first">
			              <span id="addressCountryLabel" class="label required"><label for="addressCountry" onclick="javascript:dialog.showContextHelp(arguments[0], 'Staat')">Staat*</label></span>
			              <span class="input spaceBelow">
			              	<select dojoType="ingrid:Select" style="width:43px;" id="addressCountry" name="addressCountry">
			              		<option value="de">DE</option>
			              	</select>
			              </span>
			            </span>
			            <span class="entry">
			              <span id="addressZipCodeLabel" class="label required"><label for="addressZipCode" onclick="javascript:dialog.showContextHelp(arguments[0], 'PLZ')">PLZ*</label></span>
			              <span class="input spaceBelow"><input type="text" id="addressZipCode" name="addressZipCode" class="w061" dojoType="ingrid:ValidationTextBox" /></span>
			            </span>
			            <span class="entry">
			              <span id="addressCityLabel" class="label required"><label for="addressCity" onclick="javascript:dialog.showContextHelp(arguments[0], 'Ort')">Ort*</label></span>
			              <span class="input spaceBelow"><input type="text" id="addressCity" name="addressCity" class="w148" dojoType="ingrid:ValidationTextBox" /></span>
			            </span>
			          </div>

			          <div id="addressDetails2">
			            <span class="entry first">
			              <span id="addressPOBoxLabel" class="label"><label for="addressPOBox" onclick="javascript:dialog.showContextHelp(arguments[0], 'Postfach')">Postfach</label></span>
			              <span class="input"><input type="text" id="addressPOBox" name="addressPOBox" class="w148" dojoType="ingrid:ValidationTextBox" /></span>
			            </span>
			            <span class="entry">
			              <span id="addressZipPOBoxLabel" class="label"><label for="addressZipPOBox" onclick="javascript:dialog.showContextHelp(arguments[0], 'PLZ (Postfach)')">PLZ (Postfach)</label></span>
			              <span class="input"><input type="text" id="addressZipPOBox" name="addressZipPOBox" class="w061" dojoType="ingrid:ValidationTextBox" /></span>
			            </span>
			          </div>
			        </div>
			  
			        <div class="half">
			          <span class="label"><label for="addressNotes" onclick="javascript:dialog.showContextHelp(arguments[0], 'Notizen')">Notizen</label></span>
			          <span class="input"><input type="text" mode="textarea" dojoType="ingrid:ValidationTextbox" id="addressNotes" class="w320 h120" /></span>
			        </div>
			        <div class="fill"></div>
			      </div>

			      <div class="inputContainer noSpaceBelow notRequired">
			        <div class="half left">
			          <span class="label"><label for="addressCom" onclick="javascript:dialog.showContextHelp(arguments[0], 'Kommunikation')">Kommunikation</label></span>
			          <div class="tableContainer rows4 half">
			            <div class="cellEditors" id="addressComEditors">
			              <div dojoType="ingrid:ComboBox" toggle="plain" style="width:37px;" listId="4430" id="addressComType"></div>
			              <div dojoType="ingrid:ValidationTextbox" templateCssPath="js/dojo/widget/templates/FilteringTable.css" widgetId="addressComConnection"></div>
			            </div>
			      	    <table id="addressCom" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort interactive">
			      	      <thead>
			      		      <tr>
			            			<th nosort="true" field="communicationMedium" dataType="String" width="65" editor="addressComType">Art</th>
			            			<th nosort="true" field="communicationValue" dataType="String" width="255" editor="addressComConnection">Verbindung</th>
			      		      </tr>
			      	      </thead>
			      	      <tbody>
			      	      </tbody>
			      	    </table>
			      	  </div>
			        </div>

			        <div class="half">
			          <span class="label"><label for="addressTasks" onclick="javascript:dialog.showContextHelp(arguments[0], 'Aufgaben')">Aufgaben</label></span>
			          <span class="input"><input type="text" mode="textarea" dojoType="ingrid:ValidationTextbox" id="addressTasks" class="w320 h105" /></span>
			        </div>
			        <div class="fill"></div>
			      </div>

			    </div>
			  </div>

			  	<!-- VERSCHLAGWORTUNG //-->
				<div id="adrThesaurus" class="contentBlock">
					<div class="titleBar">
						<div class="titleIcon"><a href="javascript:toggleFields('adrThesaurus');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
						<div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Verschlagwortung')">Verschlagwortung</div>
						<div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
					</div>
					<div id="adrThesaurusContent" class="content">
				  
						<div class="inputContainer notRequired h088">
							<span class="label"><label for="thesaurusTermsAddress" onclick="javascript:dialog.showContextHelp(arguments[0], 'Thesaurus-Suchbegriffe')">Thesaurus-Suchbegriffe</label></span>
							<span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage('Thesaurus-Navigator', 'mdek_thesaurus_dialog.html', 1010, 430, true, {dstTable: 'thesaurusTermsAddress'});" title="Thesaurus-Navigator [Popup]">Thesaurus-Navigator</a></span>

							<div class="tableContainer headHiddenRows3 full">
								<table id="thesaurusTermsAddress" dojoType="ingrid:FilteringTable" minRows="3" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
									<thead>
										<tr>
											<th nosort="true" field="title" dataType="String">Term</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
			      		</div>

						<div class="inputContainer notRequired h088">
							<div class="full">
								<span class="label"><label for="thesaurusFreeTermInputAddress" onclick="javascript:dialog.showContextHelp(arguments[0], 'Freie Suchbegriffe')">Freie Suchbegriffe</label></span>

								<div class="fill"></div>
								<div class="tableContainer headHiddenRows3 full">
			      	    			<table id="thesaurusFreeTermsListAddress" dojoType="ingrid:FilteringTable" minRows="3" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
			      	      				<thead>
			      		      				<tr>
			            						<th nosort="true" field="title" dataType="String">Freie Suchbegriffe</th>
			      		      				</tr>
			      	      				</thead>
			      	      				<tbody>
			      	      				</tbody>
			      	    			</table>
			      	  			</div>
						        <div class="fill"></div>
							</div>
			      		</div>

						<div class="inputContainer notRequired">
							<div class="full">
			          			<div class="input">
									<input type="text" id="thesaurusFreeTermInputAddress" class="w585" dojoType="ingrid:ValidationTextBox" />
		                  			<div style="position:relative; height:0px; top:-22px; float:right;">
										<button id="thesaurusFreeTermsAddressAddButton" dojoType="ingrid:Button">Hinzuf&uuml;gen</button>
						  			</div>
			          			</div>
				        		<div class="fill"></div>
			        		</div>
			      		</div>

					</div>
				</div>
			  
			  <!-- ZUGEORDNETE OBJEKTE //-->
			  <div id="associatedObj" class="contentBlock">
			  	<div class="titleBar">
			  	  <div class="titleIcon"><a href="javascript:toggleFields('associatedObj');" title="Nur Pflichtfelder aufklappen"><img src="img/ic_expand_required_blue.gif" width="18" height="18" alt="Nur Pflichtfelder aufklappen" /></a></div>
				    <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 'Zugeordnete Objekte')">Zugeordnete Objekte</div>
				    <div class="titleUp"><a href="#sectionBottomContent" title="nach oben"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a></div>
			  	</div>
				  <div id="associatedObjContent" class="content" style="clear:both;">
			  
			      <div class="inputContainer noSpaceBelow notRequired h098">
			        <div class="spacer"></div>
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
			      </div>

			    </div>
			  </div>
			</div>

			</form>
		</div>
	  </div>
	  <!-- END CONTENT ADDRESS -->
      <div id="contentNone">Kein Objekt oder Adresse ausgewählt</div>
    </div>

  </div>
  <!-- SPLIT CONTAINER END -->
  </div>

<!-- 
  <div widgetId="page2" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_research_search.html" preload="true" executeScripts="true"></div>
  <div widgetId="page2Sub2" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_research_thesaurus.html" preload="true" refreshOnShow="true" executeScripts="true"></div>
  <div widgetId="page2Sub3" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_research_database.html" preload="true" executeScripts="true"></div>
 -->
  <div widgetId="page2" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_research_search.html" preload="true" refreshOnShow="true" executeScripts="true"></div>
  <div widgetId="page2Sub2" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_research_thesaurus.html" preload="true" executeScripts="true"></div>
  <div widgetId="page2Sub3" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_research_database.html" preload="true" refreshOnShow="true" executeScripts="true"></div>
  <div widgetId="page3" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_statistics.html" preload="true"></div>
  <div widgetId="page4" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_qa_editor.html" preload="true"></div>
  <div widgetId="page4Sub2" dojoType="ContentPane" layoutAlign="client" style="display:none" href="mdek_qa_assurance.html" preload="true"></div>
  </div>

</body>
</html>
