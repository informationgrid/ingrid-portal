<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String currLang = (String)session.getAttribute("currLang");%>
<% String dojoPath = (String)session.getAttribute("dojoPath");%>

<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <meta http-equiv="X-UA-Compatible" content="chrome=1" />
 
        <!--<script type="text/javascript" src="js/tree.js"></script>-->
        <script type="text/javascript" src="js/objectLayout.js"></script>
        <script type="text/javascript" src="js/addressLayout.js"></script>
        
        <!--<script type="text/javascript" src="dojo-src/release/dojo/custom/layer_forms.js"></script>-->
        <script type="text/javascript" src="<%=dojoPath%>/custom/layer_forms.js"></script>
        
		<script type="text/javascript" src="js/diff.js"></script>
		<script type="text/javascript" src="js/detail_helper.js"></script>
        
        <!--<script type="text/javascript" src="js/rules_checker.js"></script>
        <script type="text/javascript" src="js/rules_validation.js"></script>
        <script type="text/javascript" src="js/rules_required.js"></script>-->
        <script type="text/javascript" src="generated/dynamicJS.js?lang=<%=currLang%>&c=<%=java.lang.Math.random()%>"></script>
        <script type="text/javascript">
            /*
             * FIXME: This javascript is not executed in CHROME when making a xhr-call!
             * FIX: works if you use dojox.layout.ContentPane!!!
             * see: http://old.nabble.com/works-in-firefox-but-not-chrome-td27376202.html
             */
            dojo.require("dojo.data.ItemFileWriteStore");
            dojo.require("dojo.date.locale");
            
            dojo.require("dijit.layout.TabContainer");
            
            dojo.require("ingrid.dijit.tree.LazyTreeStoreModel");
            dojo.require("ingrid.dijit.CustomTree");
            var generalContextMenu;
            
            createLayout();
            
            // do a refresh on the top div to do the layout
            dijit.byId("contentContainer").resize();
            
            function createLayout(){
            
                var contentBorderContainer = new dijit.layout.BorderContainer({
                    id: "hierarchyContent",
                    //title: "Hierarchy",
                    design: "headline",
                    gutters: false,
                    toggleSplitterClosedThreshold: "100px",
                    toggleSplitterOpenSize: "80px",
                    layoutAlign: "client",
                    //doLayout: false, //???
                    persist: true, // remember splitter position in cookie
                    liveSplitters: false,
					style: "height:100%; width:100%"
                }, "hierarchy");//.placeAt(contentPane.domNode);
                //===========================================================
                // top pane - toolbar
                var topPane = new dijit.layout.ContentPane({
                    id: "toolbarPane",
                    splitter: false,
                    region: "top",
                    style: "height: 29px; overflow: hidden;"
                }).placeAt(contentBorderContainer.domNode);
                
                // left pane - TreeContainer 
                var leftPane = new dijit.layout.ContentPane({
                    id: "treePane",
                    splitter: true,
                    region: "leading",
                    style: "width: 200px; padding-right: 0px;",
                    'class': "contentContainer"
                }).placeAt(contentBorderContainer.domNode);
                
                //===========================================================
                var objectPane = new dijit.layout.BorderContainer({
                    splitter: false,
                    id: "dataFormContainer",
                    region: "center",
					gutters: false
					//style: "display:none;"
                }).placeAt(contentBorderContainer.domNode);
                
				
				new dijit.layout.ContentPane({id:"topContent", region:"top", style:"margin-bottom: 2px;"}).placeAt(objectPane.domNode);
				new dijit.layout.ContentPane({id:"centerContent", region:"center", style:"width:740px;"}).placeAt(objectPane.domNode);;
				
                dojo.place("loadBlockDiv", objectPane.domNode);
				dojo.place("sectionTopObject", "topContent");
				dojo.place("contentFrameBodyObject", "centerContent");
				
				dojo.place("contentNone", "topContent");
				
				dojo.place("sectionTopAddress", "topContent");
				dojo.place("contentFrameBodyAddress", "centerContent");
				
                
                // add toolbar to top-pane
                ingridToolbar.createToolbar(topPane);
                
                // add tree to left-pane
                createCustomTree("dataTree", leftPane.domNode, "id", "title", ingridDataTree.loadData);
                ingridDataTree.createTree();
				
				//dojo.create("iframe", {id: 'printFrame', name: 'printFrame', style: 'position:absolute; left:-1000px; height: 0; border:0;'}, dojo.body());
                
                contentBorderContainer.startup();
            }
            
            function getLocalizedTitle(id) {
                var localizedTitle = "";
                switch (id) {
                    case "generalAddress":
                    case "ref2Location":
                    case "ref4Participants":
                    case "ref4PM":
                        localizedTitle = "<fmt:message key='general.addAddress' />";
                        break;
                    case "ref1Basis":
                    case "ref1Symbols":
                    case "ref1Keys":
                    case "ref1Service":
                    case "ref1DataBasis":
                    case "ref1Process":
                    case "ref2BaseData":
                    case "ref3BaseData":
                    case "ref5Keys":
                    case "ref5Method":
                    case "ref6BaseData":
                    case "linksTo":
                        localizedTitle = "<fmt:message key='ui.obj.links.linksTo.link' />";
                        break;
                    case "spatialRefAdminUnit":
                        localizedTitle = "<fmt:message key='dialog.spatialNavigator.title' />";
                        break;
                    case "spatialRefLocation":
                        localizedTitle = "<fmt:message key='dialog.spatialAssist.title' />";
                        break;
                    case "thesaurusTerms":
                        localizedTitle = "<fmt:message key='dialog.thesaurusAssist.title' />";
                        break;
                    case "thesaurusNavigator":
                        localizedTitle = "<fmt:message key='dialog.thesaurusNavigator.title' />";
                        break;
                    case "ref3Operation":
                        localizedTitle = "<fmt:message key='dialog.operations.title' />";
                        break;
                    case "availabilityUseConstraints":
                        localizedTitle = "<fmt:message key='dialog.useConstraints.title' />";
                }
                return localizedTitle;
            }
            
            function setGeneralAddressLabel(/*boolean*/isClass1Or3) {
                if (isClass1Or3)
                    dojo.byId("generalAddressTableLabelText").innerHTML = "<fmt:message key='ui.obj.general.addressTable.title.class1And3' />";
                else
                    dojo.byId("generalAddressTableLabelText").innerHTML = "<fmt:message key='ui.obj.general.addressTable.title' />";
            }
            
        </script>
    </head>
    <body>
        <div id="hierarchy">
        <div id="loadingZone" style="float:right; margin-top:0px; z-index: 100; visibility:hidden; top: 5px; right:60px; position: absolute;">
			<img id="imageZone" src="img/ladekreis.gif" />
		</div>
            <!--<div id="objectContainer" style="">--><!-- START CONTENT OBJECT - HEADER -->
            <div id="contentObject" style="display:none; width:735px;">
                <div id="loadBlockDiv" class="blocker" style="display:none;"><div id="waitInfo"></div></div>
                <div id="sectionTopObject" class="sectionTop" style="display:none; width:720px;">
                    <table cellspacing="5" class="sectionTopTable">
                        <tbody>
                            <tr>
                                <td id="objectNameLabel" class="label">
                                    <label for="objectName" onclick="javascript:dialog.showContextHelp(arguments[0], 3000)">
                                        <fmt:message key="ui.obj.header.objectName" />*
                                    </label>
                                </td>
                                <td colspan="2">
                                    <div type="text" maxLength="255" id="objectName" required="true" name="objectName" style="width: 100%;font-size:11px !important"/>
                                </td>
                            </tr>
                            <tr>
                                <td id="objectClassLabel" class="label col1">
                                    <label for="objectClass" onclick="javascript:dialog.showContextHelp(arguments[0], 1020)">
                                        <fmt:message key="ui.obj.header.objectClass" />*
                                    </label>
                                </td>
                                <td class="col2">
                                    <select autoComplete="false" required="true" style="width: 100%; margin:0px;" id="objectClass">
                                        <!--<option value="Class0"><fmt:message key="dialog.research.ext.obj.class0" /></option><option value="Class1"><fmt:message key="dialog.research.ext.obj.class1" /></option><option value="Class2"><fmt:message key="dialog.research.ext.obj.class2" /></option><option value="Class3"><fmt:message key="dialog.research.ext.obj.class3" /></option><option value="Class4"><fmt:message key="dialog.research.ext.obj.class4" /></option><option value="Class5"><fmt:message key="dialog.research.ext.obj.class5" /></option>-->
                                    </select>
                                </td>
                                <td class="col3">
                                    <img id="permissionObjLock" src="img/lock.gif" width="9" height="14" alt="gesperrt" />
                                </td>
                            </tr>
                            <tr>
                                <td id="objectOwnerLabel" class="label">
                                    <label for="objectOwner" onclick="javascript:dialog.showContextHelp(arguments[0], 1030)">
                                        <fmt:message key="ui.obj.header.responsibleUser" />*
                                    </label>
                                </td>
                                <td>
                                    <div autoComplete="false" required="true" style="width: 100%; margin:0px;" id="objectOwner" />
                                </td>
                                <td class="bgBlue" style="color:#FFFFFF; ">
                                    <strong><fmt:message key="ui.obj.header.workState" />:</strong><span id="workState" style="margin-left:3px;"></span>
                                </td>
                            </tr>
                            <tr>
                                <td class="note bgBlue" colspan="3">
                                    <strong><fmt:message key="ui.obj.header.creationTime" />:</strong><span id="creationTime">26.06.1998</span>
                                    | <strong><fmt:message key="ui.obj.header.modificationTime" />:</strong><span id="modificationTime">27.09.2000</span>
                                    | <strong><fmt:message key="ui.obj.header.modUser" />:</strong><span id="lastEditor">---</span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="fill"></div>
                </div>
                <div id="contentFrameBodyObject" class="contentContainer" style="display:none; width:720px;">
                    <a name="sectionBottomContent"></a>
                    <!--<div>--><!-- this outer div is necessary for a correct scrolling of the child elements! --><!--<div id="dummyContainer">--><!-- ALLGEMEINES //-->
                    <!-- ALLGEMEINES //-->
					<div id="general" class="rubric contentBlock firstBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('general');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7011)">
                                <fmt:message key="ui.obj.general.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="generalContent" class="content">
                            <span id="uiElement5000" class="outer">
                                <div><span class="label">
                                    <label for="generalShortDesc" onclick="javascript:dialog.showContextHelp(arguments[0], 5000)">
                                        <fmt:message key="ui.obj.general.shortDescription" />
                                    </label>
                                </span><span class="input">
                                    <div type="text" maxLength="255" id="generalShortDesc" name="generalShortDesc" class="fullWidth">
                                    </div>
                                </span></div>
                            </span>
                            <span id="uiElement1010" class="outer">
                            	<div><span id="generalDescLabel" class="label">
                                    <label for="generalDesc" onclick="javascript:dialog.showContextHelp(arguments[0], 1010)">
                                        <fmt:message key="ui.obj.general.description" />
                                    </label>
                                </span>
								<span class="input"><input type="text" id="generalDesc" name="generalDesc" /></span>
								</div>
                            </span>									
                            <span id="uiElement1000" class="outer">
                            	<div><span id="generalAddressTableLabel" class="label">
                                    <label for="generalAddressTable" onclick="javascript:dialog.showContextHelp(arguments[0], 1000)">
                                        <span id="generalAddressTableLabelText"><fmt:message key="ui.obj.general.addressTable.title" /></span>
                                    </label>
                                </span>
                                    <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" />
                                        <a id="generalAddressTableLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('generalAddress'), 'dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 585, true, {grid: 'generalAddress'});" title="<fmt:message key="dialog.popup.addressTable.link" /> [Popup]"><fmt:message key="ui.obj.general.addressTable.link" /></a>
                                    </span>
                                <div id="generalAddressTable" class="input tableContainer">
                                    <div id="generalAddress" autoHeight="4" contextMenu="GENERAL_ADDRESS" class="hideTableHeader" interactive="true">
                                    </div>
                                </div></div>
                            </span>
                            <span id="uiElement6000" class="outer">
                                <div>
                                <div class="input">
                                    <input type="checkbox" id="isInspireRelevant" />
                                    <label onclick="javascript:dialog.showContextHelp(arguments[0], 6000)">
                                        <fmt:message key="ui.obj.general.inspireRelevant" />
                                    </label>
                                </div>
                                </div>
                            </span>
                            <div class="fill"></div>
                        </div>
                    </div>
                    <!-- VERSCHLAGWORTUNG //-->
                    <div class="fill"></div>
                    <div id="thesaurus" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('thesaurus');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7005)">
                                <fmt:message key="ui.obj.thesaurus.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="thesaurusContent" class="content">
                            <div class="inputContainer">
                                <span id="uiElement5064" class="outer">
                                    <div>
                                        <span id="ref1ThesaurusInspireLabel" class="label">
                                            <label for="thesaurusInspire" onclick="javascript:dialog.showContextHelp(arguments[0], 5064);" >
                                                <fmt:message key="ui.obj.thesaurus.terms.inspire" />
                                            </label>
                                        </span>
                                        <div class="input tableContainer">
                                            <div id="thesaurusInspire" autoHeight="4" interactive="true" class="hideTableHeader">
                                            </div>
                                        </div>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement5060" class="outer">
                                    <div>
                                        <span id="thesaurusTopicsLabel" class="label">
                                            <label for="thesaurusTopics" onclick="javascript:dialog.showContextHelp(arguments[0], 5060)">
                                                <fmt:message key="ui.obj.thesaurus.terms.category" />
                                            </label>
                                        </span>
                                        <div class="input tableContainer">
                                            <div id="thesaurusTopics" autoHeight="4" interactive="true" class="hideTableHeader">
                                            </div>
                                        </div>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement1409" class="outer">
                                    <div>
                                        <span id="thesaurusTermsLabel" class="label">
                                        <label for="thesaurusTerms" onclick="javascript:dialog.showContextHelp(arguments[0], 1420)">
                                            <fmt:message key="ui.obj.thesaurus.terms" />
                                        </label>
                                        </span>
                                    <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="thesaurusTermsLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('thesaurusTerms'), 'dialogs/mdek_thesaurus_assist_dialog.jsp?c='+userLocale, 735, 430, true);" title="<fmt:message key='dialog.popup.thesaurus.terms.link.assistant' /> [Popup]"><fmt:message key="ui.obj.thesaurus.terms.link.assistant" /></a>
                                        <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="thesaurusTermsNavigatorLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('thesaurusNavigator'), 'dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {dstTable: 'thesaurusTerms'});" title="<fmt:message key="dialog.popup.thesaurus.terms.link.navigator" /> [Popup]"><fmt:message key="ui.obj.thesaurus.terms.link.navigator" /></a></span>
                                    <div class="input tableContainer">
                                        <div id="thesaurusTerms" autoHeight="4" class="hideTableHeader">
                                        </div>
                                    </div>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement1410" class="outer">
                                    <div>
                                        <span id="thesaurusTermsAddLabel" class="label">
                                            <label for="thesaurusFreeTerms" onclick="javascript:dialog.showContextHelp(arguments[0], 1410)">
                                                <fmt:message key="ui.obj.thesaurus.terms.add.label" />
                                            </label>
                                        </span>
                                        <div class="input">
                                            <input type="text" maxLength="255" id="thesaurusFreeTerms" />
                                            <div style="position:relative; top: 2px; float:right;">
                                                <button id="thesaurusFreeTermsAddButton">
                                                    <fmt:message key="ui.obj.thesaurus.terms.custom.buttonAdd" />
                                                </button>
                                            </div>
                                        </div>
                                        <div class="fill">
                                        </div>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElementN014" class="outer">
                                    <div>
                                    <span id="thesaurusEnvironmentLabel" class="label">
                                        <label for="thesaurusEnvironment" onclick="javascript:dialog.showContextHelp(arguments[0], 10014)">
                                            <fmt:message key="ui.obj.thesaurus.terms.enviromental.title" />
                                        </label>
                                    </span>
                                    <div id="thesaurusEnvironment" class="outlined">
                                        <div class="checkboxContainer">
                                            <span class="input"><input type="checkbox" name="thesaurusEnvExtRes" id="thesaurusEnvExtRes" />
                                                <label onclick="javascript:dialog.showContextHelp(arguments[0], 7006)">
                                                    <fmt:message key="ui.obj.thesaurus.terms.enviromental.displayCatalogPage" />
                                                </label>
                                            </span>
                                        </div>
                                            <span id="uiElementN015" class="outer headHiddenRows4">
                                                <span id="thesaurusEnvTopicsLabel" class="label">
                                                    <label for="thesaurusEnvTopics" onclick="javascript:dialog.showContextHelp(arguments[0], 10015)">
                                                        <fmt:message key="ui.obj.thesaurus.terms.enviromental.topics" />
                                                    </label>
                                                </span>
                                                <div class="input tableContainer">
                                                    <div id="thesaurusEnvTopics" autoHeight="4" interactive="true" class="hideTableHeader">
                                                    </div>
                                                </div>
                                            </span>
                                        <div class="fill">
                                        </div>
                                    </div>
                                    </div>
                                </span>
                            </div>
                            <div class="fill">
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- FACHBEZUG CLASS 1 - GEO-INFORMATION/KARTE //-->
                    <div class="fill"></div>
                    <div id="refClass1" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('refClass1');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7000)">
                                <fmt:message key="ui.obj.relevance" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="ref1Content" class="content">
                            <span id="uiElement3520" class="outer">
                                <div>
                                    <span id="ref1BasisTabContainerLabel" class="label">
                                        <label for="ref1BasisTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3520)">
                                            <fmt:message key="ui.obj.type1.technicalBasisTable.title" />
                                        </label>
                                    </span>
                                    <span id="ref1BasisTab2Header" class="functionalLink onTab marginRight" style="display: none;">
                                        <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddBasisLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref1Basis'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3520, gridId: 'ref1BasisLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type1.technicalBasisTable.link" /></a>
                                    </span>
                                    <div id="ref1BasisTabContainer" selectedChild="ref1BasisTab1">
                                        <div id='ref1BasisTab1' class="input" label="<fmt:message key="ui.obj.type1.technicalBasisTable.tab.text" />">
                                            <textarea id="ref1BasisText" name="ref1BasisText"></textarea>
                                        </div>
                                        <div id='ref1BasisTab2' class="input" label="<fmt:message key="ui.obj.type1.technicalBasisTable.tab.links" />">
                                            <div id="ref1BasisLink" autoHeight="4" class="hideTableHeader noValidate" contextMenu="EDIT_LINK" relation_filter="3520" query="{relationType:'3520'}">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </span>
                            <span id="uiElementN021" class="outer">
                            	<div><span id="ref1ObjectIdentifierLabel" class="label">
                                    <label for="ref1ObjectIdentifier" onclick="javascript:dialog.showContextHelp(arguments[0], 10021)">
                                        <fmt:message key="ui.obj.type1.identifier" />
                                    </label>
                                </span>
								<span class="input">
									<input type="text" maxLength="255" id="ref1ObjectIdentifier" style="width:100%;" />
                                    <div style="position:relative; top: 2px; float:right;">
                                        <button id="ref1ObjectIdentifierAddButton">
                                            <fmt:message key="ui.obj.type1.identifier.buttonAdd" />
                                        </button>
                                    </div>
								</span></div>
							</span>
                            <div>
                                <span class="outer halfWidth">
                                	<div>
                                	    <span id="uiElement5061">
                                	        <div><span id="ref1DataSetLabel" class="label">
	                                            <label for="ref1DataSet" onclick="javascript:dialog.showContextHelp(arguments[0], 5061)">
	                                                <fmt:message key="ui.obj.type1.dataset" />
	                                            </label>
	                                        </span>
										    <span class="input spaceBelow"><input listId="525" id="ref1DataSet" style="width:100%;" /></span>
                                            </div>
                                        </span>
                                    </div>
								</span>
                            <span id="uiElement5062" class="outer halfWidth">
                            	<div><span class="label">
                                    <label for="ref1Representation" onclick="javascript:dialog.showContextHelp(arguments[0], 5062)">
                                        <fmt:message key="ui.obj.type1.digitalRepresentation" />
                                    </label>
                                </span>
                                <div class="input tableContainer">
                                    <div id="ref1Representation" autoHeight="4" interactive="true" class="hideTableHeader">
                                    </div>
                                </div>
								</div>
                            </span>
                            <div id="ref1VFormat" class="inputContainer optional" style="padding:5px;">
                                <span id="uiElementN005">
                                    <span id="ref1VFormatLabel" class="label">
                                        <label for="ref1VFormat" onclick="javascript:dialog.showContextHelp(arguments[0], 7014)">
                                            <fmt:message key="ui.obj.type1.vectorFormat.title" />
                                        </label>
                                    </span>
                                    <div id="ref1VFormat" class="outlined">
                                            <span id="uiElement5063" class="outer" style="width:33%;">
											     <div>
                                                <span class="label">
                                                    <label for="ref1VFormatTopology" onclick="javascript:dialog.showContextHelp(arguments[0], 5063)">
                                                        <fmt:message key="ui.obj.type1.vectorFormat.topology" />
                                                    </label>
                                                </span>
                                                <span class="input">
                                                    <input style="width:100%;" listId="528" id="ref1VFormatTopology" />
                                                </span></div>
                                            </span>
                                            <span id="uiElementN001" class="outer" style="width:67%;">
											     <div>
                                                <span class="label hidden">
                                                    <label for="ref1VFormatDetails" onclick="javascript:dialog.showContextHelp(arguments[0], 10001)">
                                                        weitere Angaben
                                                    </label>
                                                </span>
                                                <div class="input tableContainer">
                                                    <div id="ref1VFormatDetails" autoHeight="4" interactive="true">
                                                    </div>
                                                </div></div>
                                            </span>
											<span>&nbsp;</span>
                                    </div>
                                </span>
								
                            </div>
                            <div class="inputContainer">
                                <span id="uiElementN002" class="outer">
                                	<div><span class="label">
                                        <label for="ref1Scale" onclick="javascript:dialog.showContextHelp(arguments[0], 3525)">
                                            <fmt:message key="ui.obj.type1.scaleTable.title" />
                                        </label>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="ref1Scale" autoHeight="4" interactive="true">
                                        </div>
                                    </div>
									</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3555" class="outer">
                                	<div><span class="label">
                                        <label for="ref1SymbolsTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3555)">
                                            <fmt:message key="ui.obj.type1.symbolCatTable.title" />
                                        </label>
                                    </span><span id="ref1SymbolsTab2Header" class="functionalLink onTab marginRight" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddSymbolsLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref1Symbols'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3555, gridId: 'ref1SymbolsLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type1.symbolCatTable.link" /></a></span>
                                    <div id="ref1SymbolsTabContainer" selectedChild="ref1SymbolsTab1">
                                        <div id='ref1SymbolsTab1' label="<fmt:message key="ui.obj.type1.symbolCatTable.tab.text" />">
                                            <!-- Need to use document.write here since the titel 'label' is set directly in the div
                                            <div id="ref1SymbolsTab1" dojoType="ContentPane" label="Text">
                                            -->
                                            <div class="input tableContainer">
                                                <div id="ref1SymbolsText" autoHeight="3" minRows="3" interactive="true" listId="3555" class="">
                                                </div>
                                            </div>
                                        </div>
                                        <div id='ref1SymbolsTab2' class="input" label="<fmt:message key="ui.obj.type1.symbolCatTable.tab.links" />">
                                            <div id="ref1SymbolsLink" class="hideTableHeader noValidate" contextMenu="EDIT_LINK" relation_filter="3555" query="{relationType:'3555'}">
                                            </div>
                                        </div>
                                    </div>
									</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3535" class="outer">
                                	<div><span class="label">
                                        <label for="ref1KeysTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3535)">
                                            <fmt:message key="ui.obj.type1.keyCatTable.title" />
                                        </label>
                                    </span><span id="ref1KeysTab2Header" class="functionalLink onTab marginRight" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddKeysLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref1Keys'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3535, gridId: 'ref1KeysLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type1.keyCatTable.link" /></a></span>
                                    <div id="ref1KeysTabContainer" selectedChild="ref1KeysTab1">
                                        <div id='ref1KeysTab1' label="<fmt:message key="ui.obj.type1.keyCatTable.tab.text" />">
                                            <div class="input tableContainer">
                                                <div id="ref1KeysText" autoHeight="3" minRows="3" interactive="true" class="">
                                                </div>
                                            </div>
                                        </div>
                                        <div id='ref1KeysTab2' class="input" label="<fmt:message key="ui.obj.type1.keyCatTable.tab.links" />">
                                            <div id="ref1KeysLink" class="hideTableHeader noValidate" contextMenu="EDIT_LINK" relation_filter="3535" query="{relationType:'3535'}">
                                            </div>
                                        </div>
                                    </div>
									</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement5070" class="outer">
                                    <div><span class="label">
                                        <label for="ref1Data" onclick="javascript:dialog.showContextHelp(arguments[0], 5070)">
                                            <fmt:message key="ui.obj.type1.attributes" />
                                        </label>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="ref1Data" interactive="true" autoHeight="3" minRows="3" class="hideTableHeader">
                                        </div>
                                    </div>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElementN003" class="outer">
                                	<div><span class="label">
                                        <label for="ref1ServiceLink" onclick="javascript:dialog.showContextHelp(arguments[0], 5066)">
                                            <fmt:message key="ui.obj.type1.serviceLink.title" />
                                        </label>
                                    </span>
<!--                                     <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddServiceLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref1Service'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 5066, gridId: 'ref1ServiceLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type1.serviceLink.link" /></a></span> -->
                                    <div class="input tableContainer">
                                        <div id="ref1ServiceLink" autoHeight="4" class="hideTableHeader">
                                        </div>
                                    </div>
									</div>
									<div style="position:relative; float:right;"><button id="btnAddServiceLink"><fmt:message key="ui.obj.type1.buttonAddServiceLink" /></button></div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3570" class="outer">
                                	<div><span class="label">
                                        <label for="ref1DataBasisTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3570)">
                                            <fmt:message key="ui.obj.type1.dataBasisTable.title" />
                                        </label>
                                    </span><span id="ref1DataBasisTab2Header" class="functionalLink onTab marginRight" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddDataBasisLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref1DataBasis'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3570, gridId: 'ref1DataBasisLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type1.dataBasisTable.link" /></a></span>
                                    <div id="ref1DataBasisTabContainer" selectedChild="ref1DataBasisTab1">
                                        <div id='ref1DataBasisTab1' class="input" label="<fmt:message key="ui.obj.type1.dataBasisTable.tab.text" />">
                                            <input type="text" id="ref1DataBasisText" name="ref1DataBasisText" />
                                        </div>
                                        <div id='ref1DataBasisTab2' label="<fmt:message key="ui.obj.type1.dataBasisTable.tab.links" />">
                                            <div class="input tableContainer">
                                                <div id="ref1DataBasisLink" autoHeight="4" class="hideTableHeader noValidate" contextMenu="EDIT_LINK" relation_filter="3570" query="{relationType:'3570'}">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
									</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3515" class="outer">
                                	<div><span class="label">
                                        <label for="ref1ProcessTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3515)">
                                            <fmt:message key="ui.obj.type1.processTable.title" />
                                        </label>
                                    </span><span id="ref1ProcessTab2Header" class="functionalLink onTab marginRight" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" />
    									<a id="ref1AddProcessLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref1Process'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3515, gridId: 'ref1ProcessLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type1.processTable.link" /></a>
									</span>
                                    <div id="ref1ProcessTabContainerSpace" selectedChild="ref1ProcessTab1">
                                        <!--<div id='ref1ProcessTab1' label="<fmt:message key="ui.obj.type1.processTable.tab.text" />";-->
                                            <span class="input"><input type="text" id="ref1ProcessText" name="ref1ProcessText" class="" /></span>
                                        <!--</div>-->
                                        <div id='ref1ProcessTab2' label="<fmt:message key="ui.obj.type1.processTable.tab.links" />">
                                            <div class="input tableContainer">
                                                <div id="ref1ProcessLink" autoHeight="4" class="hideTableHeader noValidate" contextMenu="EDIT_LINK" relation_filter="3515" query="{relationType:'3515'}">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
									</div>
                                </span>
                            </div>
                        </div>
                        </div>
                         <div class="fill"></div>
					</div>
                    <!-- "FACHBEZUG DATENQUALITAET" CLASS 1 - GEO-INFORMATION/KARTE //-->
                    <div class="fill"></div>
                    <div id="refClass1DQ" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('refClass1DQ');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7500)">
                                <fmt:message key="ui.obj.dq" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                            <span id="uiElement3565" class="outer">
                                <div><span class="label">
                                    <label for="ref1Coverage" onclick="javascript:dialog.showContextHelp(arguments[0], 3565)">
                                        <fmt:message key="ui.obj.type1.coverage" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="ref1Coverage" min="0" max="100" name="ref1Coverage" class="" decimal="," /> %</span>
                                </div>
                            </span>
                            <div class="inputContainer">
                                    <span id="uiElement5069" class="outer halfWidth">
                                        <div><span class="label">
                                            <label for="ref1AltAccuracy" onclick="javascript:dialog.showContextHelp(arguments[0], 5069)">
                                                <fmt:message key="ui.obj.type1.sizeAccuracy" />
                                            </label>
                                        </span><span class="input"><input type="text" id="ref1AltAccuracy" name="ref1AltAccuracy" class="" decimal="," /></span>
                                        </div>
                                    </span>
                                    <span id="uiElement3530" class="outer halfWidth">
                                        <div><span class="label">
                                            <label for="ref1PosAccuracy" onclick="javascript:dialog.showContextHelp(arguments[0], 3530)">
                                                <fmt:message key="ui.obj.type1.posAccuracy" />
                                            </label>
                                        </span><span class="input"><input type="text" id="ref1PosAccuracy" name="ref1PosAccuracy" class="" decimal="," /></span>
                                        </div>
                                    </span>
                            </div>
                        <!-- BELOW HERE ARE THE DQ TABELS with special IDs so validation, content read etc. works automatically (see eventSubscriber.js, rules_validation.js) ! -->
                        <!-- Further the tables are shown DEPENDENT FROM CHOSEN INSPIRE THEME, see rules_requires.js applyRule7() -->
                        <div id="ref1ContentDQTables" class="content">
                            <span id="uiElement7509" class="outer">
                                <div><span class="label">
                                    <label for="dq109Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7509)">
                                        <fmt:message key="ui.obj.dq.table109.title" />
                                    </label>
                                </span>
                                <div class="input tableContainer">
                                    <div id="dq109Table" interactive="true" autoHeight="3">
                                    </div>
                                </div>
                                </div>
                            </span>
                            <span id="uiElement7512" class="outer">
                                <div><span class="label">
                                    <label for="dq112Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7512)">
                                        <fmt:message key="ui.obj.dq.table112.title" />
                                    </label>
                                </span>
                                <div class="input tableContainer">
                                    <div id="dq112Table" interactive="true" autoHeight="3">
                                    </div>
                                </div>
                                </div>
                            </span>
                            <span id="uiElement7513" class="outer">
                                <div><span class="label">
                                    <label for="dq113Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7513)">
                                        <fmt:message key="ui.obj.dq.table113.title" />
                                    </label>
                                </span>
                                <div class="input tableContainer">
                                    <div id="dq113Table" interactive="true" autoHeight="3">
                                    </div>
                                </div>
                                </div>
                            </span>
                            <span id="uiElement7514" class="outer">
                                <div><span class="label">
                                    <label for="dq114Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7514)">
                                        <fmt:message key="ui.obj.dq.table114.title" />
                                    </label>
                                </span>
                                <div class="input tableContainer">
                                    <div id="dq114Table" interactive="true" autoHeight="3">
                                    </div>
                                </div>
                                </div>
                            </span>
                            <span id="uiElement7515" class="outer">
                                <div><span class="label">
                                    <label for="dq115Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7515)">
                                        <fmt:message key="ui.obj.dq.table115.title" />
                                    </label>
                                </span>
                                <div class="input tableContainer">
                                    <div id="dq115Table" interactive="true" autoHeight="3">
                                    </div>
                                </div>
                                </div>
                            </span>
                            <span id="uiElement7520" class="outer">
                                <div><span class="label">
                                    <label for="dq120Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7520)">
                                        <fmt:message key="ui.obj.dq.table120.title" />
                                    </label>
                                </span>
                                <div class="input tableContainer">
                                    <div id="dq120Table" interactive="true" autoHeight="3">
                                    </div>
                                </div>
                                </div>
                            </span>
                            <span id="uiElement7525" class="outer">
                                <div><span class="label">
                                    <label for="dq125Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7525)">
                                        <fmt:message key="ui.obj.dq.table125.title" />
                                    </label>
                                </span>
                                <div class="input tableContainer">
                                    <div id="dq125Table" interactive="true" autoHeight="3">
                                    </div>
                                </div>
                                </div>
                            </span>
                            <span id="uiElement7526" class="outer">
                                <div><span class="label">
                                    <label for="dq126Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7526)">
                                        <fmt:message key="ui.obj.dq.table126.title" />
                                    </label>
                                </span>
                                <div class="input tableContainer">
                                    <div id="dq126Table" interactive="true" autoHeight="3">
                                    </div>
                                </div>
                                </div>
                            </span>
                            <span id="uiElement7527" class="outer">
                                <div><span class="label">
                                    <label for="dq127Table" onclick="javascript:dialog.showContextHelp(arguments[0], 7527)">
                                        <fmt:message key="ui.obj.dq.table127.title" />
                                    </label>
                                </span>
                                <div class="input tableContainer">
                                    <div id="dq127Table" interactive="true" autoHeight="3">
                                    </div>
                                </div>
                                </div>
                            </span>
                        </div>
                         <div class="fill"></div>
                    </div>
                    <!-- FACHBEZUG CLASS 2 - DOKUMENT/BERICHT/LITERATUR //-->
                    <div class="fill"></div>
                    <div id="refClass2" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('refClass2');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7000)">
                                <fmt:message key="ui.obj.relevance" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="ref2Content" class="content">
                            <div class="inputContainer">
                                <span id="uiElement3355" class="outer">
                                    <div>
                                        <span class="label">
                                            <label for="ref2Author" onclick="javascript:dialog.showContextHelp(arguments[0], 3355)">
                                                <fmt:message key="ui.obj.type2.author" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="255" id="ref2Author" name="ref2Author" class="fullWidth" />
                                        </span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3350" class="outer">
                                    <div>
                                        <span class="label">
                                            <label for="ref2Publisher" onclick="javascript:dialog.showContextHelp(arguments[0], 3350)">
                                                <fmt:message key="ui.obj.type2.editor" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="255" id="ref2Publisher" name="ref2Publisher" class="fullWidth" />
                                        </span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3340" class="outer halfWidth">
                                    <div>
                                        <span class="label">
                                            <label for="ref2PublishedIn" onclick="javascript:dialog.showContextHelp(arguments[0], 3340)">
                                                <fmt:message key="ui.obj.type2.publishedIn" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="80" id="ref2PublishedIn" name="ref2PublishedIn" class="w320" />
                                        </span>
                                    </div>
                                </span>
                                <span id="uiElement3310" class="outer halfWidth">
                                    <div>
                                        <span class="label">
                                            <label for="ref2PublishLocation" onclick="javascript:dialog.showContextHelp(arguments[0], 3310)">
                                                <fmt:message key="ui.obj.type2.publishedLocation" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="80" id="ref2PublishLocation" name="ref2PublishLocation" class="w320" />
                                        </span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span class="outer halfWidth">
                                    <span id="uiElement3330" class="outer" style="width:33%;">
                                        <div>
                                        <span class="label">
                                            <label for="ref2PublishedInIssue" onclick="javascript:dialog.showContextHelp(arguments[0], 3330)">
                                                <fmt:message key="ui.obj.type2.issue" />
                                            </label>
                                        </span>
                                        <span class="input spaceBelow">
                                            <input type="text" maxLength="40" id="ref2PublishedInIssue" name="ref2PublishedInIssue" class="" />
                                        </span>
										</div>
                                    </span>
                                    <span id="uiElement3320" class="outer" style="width:33%;">
                                        <div>
                                        <span class="label">
                                            <label for="ref2PublishedInPages" onclick="javascript:dialog.showContextHelp(arguments[0], 3320)">
                                                <fmt:message key="ui.obj.type2.pages" />
                                            </label>
                                        </span>
                                        <span class="input spaceBelow">
                                            <input type="text" maxLength="20" id="ref2PublishedInPages" name="ref2PublishedInPages" class="" />
                                        </span>
										</div>
                                    </span>
                                    <span id="uiElement3300" class="outer" style="width:34%;">
									    <div>
                                        <span class="label">
                                            <label for="ref2PublishedInYear" onclick="javascript:dialog.showContextHelp(arguments[0], 3300)">
                                                <fmt:message key="ui.obj.type2.publishedYear" />
                                            </label>
                                        </span>
                                        <span class="input spaceBelow">
                                            <input type="text" maxLength="20" id="ref2PublishedInYear" name="ref2PublishedInYear" class="" />
                                        </span>
										</div>
                                    </span>
                                    <span id="uiElement3365" class="outer" style="clear:both;">
									    <div>
                                        <span class="label">
                                            <label for="ref2PublishedISBN" onclick="javascript:dialog.showContextHelp(arguments[0], 3365)">
                                                <fmt:message key="ui.obj.type2.isbn" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="40" id="ref2PublishedISBN" name="ref2PublishedISBN" class="w148" />
                                        </span>
										</div>
                                    </span>
                                    <span id="uiElement3370" class="outer">
                                    	<div>
                                        <span class="label">
                                            <label for="ref2PublishedPublisher" onclick="javascript:dialog.showContextHelp(arguments[0], 3370)">
                                                <fmt:message key="ui.obj.type2.publisher" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="80" id="ref2PublishedPublisher" name="ref2PublishedPublisher" class="w148" />
                                        </span>
										</div>
                                    </span>
                                </span>
                                <span id="uiElement3360" class="outer halfWidth">
                                    <div><span class="label">
                                        <label for="ref2LocationTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3360)">
                                            <fmt:message key="ui.obj.type2.locationTable.title" />
                                        </label>
                                    </span>
                                    <span id="ref2LocationTab2Header" class="functionalLink onTab marginRight" style="display: none;">
                                        <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref2AddLocationLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref2Location'), 'dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 580, true, {linkType: 3360, grid: 'ref2LocationLink'});" title="<fmt:message key="dialog.popup.addressTable.link" /> [Popup]"><fmt:message key="ui.obj.type2.locationTable.link" /></a>
                                    </span>
                                    <div id="ref2LocationTabContainer" class="" selectedChild="ref2LocationTab1">
                                        <div id='ref2LocationTab1' label="<fmt:message key="ui.obj.type2.locationTable.tab.text" />">
                                                <input type="text" maxLength="80" id="ref2LocationText" name="ref2LocationText" class="" />
                                        </div>
                                        <div id='ref2LocationTab2' label="<fmt:message key="ui.obj.type2.locationTable.tab.links" />">
                                            <div class="input tableContainer">
                                                <div id="ref2LocationLink" autoHeight="2" class="hideTableHeader noValidate" query="{typeOfRelation:'3360'}">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
    								</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3385" class="outer halfWidth">
                                    <div>
                                        <span class="label">
                                            <label for="ref2DocumentType" onclick="javascript:dialog.showContextHelp(arguments[0], 3385)">
                                                <fmt:message key="ui.obj.type2.documentType" />
                                            </label>
                                        </span>
                                        <span class="input spaceBelow">
                                            <div maxLength="80" style="width:100%;" id="ref2DocumentType" listId="3385">
                                            </div>
                                        </span>
                                    </div>
                                </span>
                                <span id="uiElement3210" class="outer halfWidth">
	                                <div>
		                                <span class="label">
		                                    <label for="ref2BaseDataTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3210)">
		                                        <fmt:message key="ui.obj.type2.generalDataTable.title" />
		                                    </label>
		                                </span>
		                                <span id="ref2BaseDataTab2Header" class="functionalLink onTab marginRight" style="display: none;">
		                                    <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref2AddBaseDataLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref2BaseData'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3345, gridId: 'ref2BaseDataLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type2.generalDataTable.link" /></a>
		                                </span>
		                                <div id="ref2BaseDataTabContainer" selectedChild="ref2BaseDataTab1">
		                                    <div id='ref2BaseDataTab1' label="<fmt:message key="ui.obj.type2.generalDataTable.tab.text" />">
		                                            <input type="text" id="ref2BaseDataText" name="ref2BaseDataText" />
		                                    </div>
		                                    <div id='ref2BaseDataTab2' label="<fmt:message key="ui.obj.type2.generalDataTable.tab.links" />">
		                                        <div class="input tableContainer">
		                                            <div id="ref2BaseDataLink" autoHeight="2" class="hideTableHeader noValidate" contextMenu="EDIT_LINK" relation_filter="3345" query="{relationType:'3345'}">
		                                            </div>
		                                        </div>
		                                    </div>
		                                </div>
		                            </div>
                                </span>
                            </div>
	                        <div class="inputContainer">
	                            <span id="uiElement3380" class="outer halfWidth">
	                                <div>
	                                    <span class="label">
	                                        <label for="ref2BibData" onclick="javascript:dialog.showContextHelp(arguments[0], 3380)">
	                                            <fmt:message key="ui.obj.type2.additionalBibInfo" />
	                                        </label>
	                                    </span>
	                                    <span class="input">
	                                        <input type="text" maxLength="255" id="ref2BibData" name="ref2BibData" class="w320 h038" />
	                                    </span>
	                                </div>
	                            </span>
	                            <span id="uiElement3375" class="outer halfWidth">
	                                <div>
	                                    <span class="label">
	                                        <label for="ref2Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 3375)">
	                                            <fmt:message key="ui.obj.type2.description" />
	                                        </label>
	                                    </span>
	                                    <span class="input">
	                                        <input type="text" id="ref2Explanation" name="ref2Explanation" class="w320 h038" />
	                                    </span>
	                                </div>
	                            </span>
	                        </div>
	                        <div class="fill"></div>
                        </div>
                    </div>
                    <!-- FACHBEZUG CLASS 3 - Geodatendienst //-->
                    <div class="fill"></div>
                    <div id="refClass3" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('refClass3');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7000)">
                                <fmt:message key="ui.obj.relevance" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="ref3Content" class="content">
                            <div class="inputContainer">
                                <span id="uiElementN022" class="outer">
                                	<div><span id="ref3ServiceTypeTableLabel" class="label">
                                        <label for="ref3ServiceTypeTable" onclick="javascript:dialog.showContextHelp(arguments[0], 10022)">
                                            <fmt:message key="ui.obj.type3.ref3ServiceTypeTable.title" />
                                        </label>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="ref3ServiceTypeTable" autoHeight="4" interactive="true" class="hideTableHeader">
                                        </div>
                                    </div>
									</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                              <span class="outer halfWidth">
                              <div>
                                <span id="uiElement3220">
                                    <div>
										<span id="ref3ServiceTypeLabel" class="label">
                                            <label for="ref3ServiceType" onclick="javascript:dialog.showContextHelp(arguments[0], 3220)">
                                                <fmt:message key="ui.obj.type3.serviceType" />
                                            </label>
                                        </span>
										<span class="input spaceBelow">
											<div maxLength="255" autoComplete="false" listId="5100" id="ref3ServiceType" style="width: 100%;"></div> 
                                        </span>
									</div>
                                </span>
                              </div> 
                              </span>
                                <span id="uiElement3230" class="outer halfWidth">
                                   	<div><span class="label">
                                           <label for="ref3ServiceVersion" onclick="javascript:dialog.showContextHelp(arguments[0], 3230)">
                                               <fmt:message key="ui.obj.type3.serviceVersion" />
                                           </label>
                                       </span>
                                       <div class="input tableContainer">
                                           <div id="ref3ServiceVersion" autoHeight="4" interactive="true" class="hideTableHeader">
                                           </div>
                                       </div>
									</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElementN004" class="outer">
                                    <div><span class="label">
                                        <label for="ref3Operation" onclick="javascript:dialog.showContextHelp(arguments[0], 7015)">
                                            <fmt:message key="ui.obj.type3.operationTable.title" />
                                        </label>
                                    </span><span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage(getLocalizedTitle('ref3Operation'), 'dialogs/mdek_operation_dialog.jsp?c='+userLocale, 735, 745, true, {gridId:'ref3Operation'});" title="<fmt:message key="dialog.popup.operationTable.link" /> [Popup]"><fmt:message key="ui.obj.type3.operationTable.link" /></a></span>
                                    <div class="input tableContainer">
                                        <div id="ref3Operation" autoHeight="4" contextMenu="EDIT_OPERATION" class="importantIgnore">
                                        </div>
                                    </div>
                                    <div style="position:relative; top: 2px; float:right;">
                                        <span id="updateGetCapLoadingZone" style="visibility:hidden">
                                            <img src="img/ladekreis.gif" style="vertical-align: middle;"/>
                                        </span>
                                        <button id="updateDatasetWithCapabilities">
                                            <fmt:message key="ui.obj.operations.update.dataset" />
                                        </button>
                                    </div>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElementN023" class="outer">
                                	<div><span class="label">
                                        <label for="ref3Scale" onclick="javascript:dialog.showContextHelp(arguments[0], 3525)">
                                            <fmt:message key="ui.obj.type3.scaleTable.title" />
                                        </label>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="ref3Scale" autoHeight="4" interactive="true">
                                        </div>
                                    </div>
									</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3200" class="outer halfWidth">
                                	<div><span class="label">
                                        <label for="ref3SystemEnv" onclick="javascript:dialog.showContextHelp(arguments[0], 3200)">
                                            <fmt:message key="ui.obj.type3.environment" />
                                        </label>
                                    </span><span class="input"><input type="text" id="ref3SystemEnv" name="ref3SystemEnv" /></span>
									</div>
								</span>
                                <span id="uiElement3240" class="outer halfWidth">
                                	<div><span class="label">
                                        <label for="ref3History" onclick="javascript:dialog.showContextHelp(arguments[0], 3240)">
                                            <fmt:message key="ui.obj.type3.history" />
                                        </label>
                                    </span><span class="input"><input type="text" id="ref3History" name="ref3History" class="w320 h038" /></span>
									</div>
								</span>
								<span id="uiElement3250" class="outer halfWidth">
                                    <div><span class="label">
                                        <label for="ref3Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 3250)">
                                            <fmt:message key="ui.obj.type3.description" />
                                        </label>
                                        </span><span class="input"><input type="text" id="ref3Explanation" name="ref3Explanation" /></span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement3345" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="ref3BaseDataTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3345)">
                                                <fmt:message key="ui.obj.type3.generalDataTable.title" />
                                            </label>
                                        </span>
                                        <!--<span id="ref3MethodTab2Header" class="functionalLink onTab" style=""><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref3AddBaseDataLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref3BaseData'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3210, gridId: 'ref3BaseDataLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type3.generalDataTable.link" /></a></span>
                                        -->
                                        <div id='ref3MethodTab2' label="<fmt:message key="ui.obj.type3.generalDataTable.tab.links" />">
                                            <div class="input tableContainer">
                                                <div id="ref3BaseDataLink" autoHeight="4" query="{relationType:'3600'}" class="hideTableHeader" relation_filter="3600">
                                                </div>
                                            </div>
                                        </div>
                                        <div id="ref3BaseDataTabContainer" class="h088" selectedChild="ref3BaseDataTab1">
                                            <div id='ref3BaseDataTab1' label="<fmt:message key="ui.obj.type3.generalDataTable.tab.text" />">
                                            <input type="text" id="ref3BaseDataText" name="ref3BaseDataText" />
                                        </div>
                                        </div>
										</div>
										<div style="position:relative; float:right;"><button id="btnAddDataLink"><fmt:message key="ui.obj.type1.buttonAddDataLink" /></button></div>
                                    </span>
                                    <span id="uiElement3221" class="outer halfWidth">
                                        <div>
                                            <span id="ref3CouplingTypeLabel" class="label"> 
                                                <label for="ref3CouplingType" onclick="javascript:dialog.showContextHelp(arguments[0], 3221)">
                                                    <fmt:message key="ui.obj.type3.couplingType" /> 
                                                </label> 
                                            </span>
                                            <span class="input">
                                                <div maxLength="255" autoComplete="false" id="ref3CouplingType" style="width: 100%;"></div>
                                            </span>
                                        </div> 
                                    </span>
                            </div>
                            <div class="inputContainer">
                            <span id="uiElement3260">
                                <span class="checkboxContainer outer"><div>
                                    <span class="input">
                                        <input type="checkbox" id="ref3HasAccessConstraint" />
                                        <label onclick="javascript:dialog.showContextHelp(arguments[0], 3260)">
                                            <fmt:message key="ui.obj.type3.ref3HasAccessConstraint" />
                                        </label>
                                    </span>
                                    </div>
                                </span>
                            </span>
                            </div>
                        </div>
                    </div>
                    <!-- FACHBEZUG CLASS 4 - VORHABEN/PROJEKT/PROGRAMM //-->
                    <div class="fill"></div>
                    <div id="refClass4" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('refClass4');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7000)">
                                <fmt:message key="ui.obj.relevance" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="ref4Content" class="content">
                            <div class="inputContainer">
                                    <span id="uiElement3410" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="ref4ParticipantsTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3410)">
                                                <fmt:message key="ui.obj.type4.participantsTable.title" />
                                            </label>
                                        </span><span id="ref4ParticipantsTab2Header" class="functionalLink onTab marginRightColumn" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref4AddParticipantsLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref4Participants'), 'dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 580, true, {linkType: 3410, grid: 'ref4ParticipantsLink'});" title="<fmt:message key="dialog.popup.addressTable.link" /> [Popup]"><fmt:message key="ui.obj.type4.participantsTable.link" /></a></span>
                                        <div id="ref4ParticipantsTabContainer" selectedChild="ref4ParticipantsTab1">
                                            <div id='ref4ParticipantsTab1' label="<fmt:message key="ui.obj.type4.participantsTable.tab.text" />">
                                                <input type="text" maxLength="255" id="ref4ParticipantsText" name="ref4ParticipantsText" />
                                            </div>
                                            <div id='ref4ParticipantsTab2' label="<fmt:message key="ui.obj.type4.participantsTable.tab.links" />">
                                                <div class="input tableContainer">
                                                    <div id="ref4ParticipantsLink" autoHeight="2" class="hideTableHeader noValidate" query="{typeOfRelation:'3410'}">
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
										</div>
                                    </span>
                                    <span id="uiElement3400" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="ref4PMTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3400)">
                                                <fmt:message key="ui.obj.type4.projectManagerTable.title" />
                                            </label>
                                        </span><span id="ref4PMTab2Header" class="functionalLink onTab marginRight" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref4AddPMLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref4PM'), 'dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 580, true, {linkType: 3400, grid: 'ref4PMLink'});" title="<fmt:message key="dialog.popup.addressTable.link" /> [Popup]"><fmt:message key="ui.obj.type4.projectManagerTable.link" /></a></span>
                                        <div id="ref4PMTabContainer" class="h088" selectedChild="ref4PMTab1">
                                            <div id='ref4PMTab1' label="<fmt:message key="ui.obj.type4.projectManagerTable.tab.text" />">
                                                <input type="text" maxLength="80" id="ref4PMText" name="ref4PMText" />
                                            </div>
                                            <div id='ref4PMTab2' label="<fmt:message key="ui.obj.type4.projectManagerTable.tab.links" />">
                                                <div class="input tableContainer">
                                                    <div id="ref4PMLink" autoHeight="2" class="hideTableHeader noValidate" query="{typeOfRelation:'3400'}">
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
										</div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3420" class="outer">
                                	<div><span class="label">
                                        <label for="ref4Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 3420)">
                                            <fmt:message key="ui.obj.type4.description" />
                                        </label>
                                    </span><span class="input"><input type="text" id="ref4Explanation" name="ref4Explanation" class="w668 h055" /></span>
									</div>
								</span>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div><!-- FACHBEZUG CLASS 5 - DATENSAMMLUNG/DATENBANK //-->
                    <!-- FACHBEZUG CLASS 5 -  //-->
                    <div class="fill"></div>
                    <div id="refClass5" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('refClass5');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7000)">
                                <fmt:message key="ui.obj.relevance" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="ref5Content" class="content">
                            <div class="inputContainer">
                                <span id="uiElement3109" class="outer">
                                    <div><span class="label">
                                        <label for="ref5KeysTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3109)">
                                            <fmt:message key="ui.obj.type5.keyCatTable.title" />
                                        </label>
                                    </span><span id="ref5KeysTab2Header" class="functionalLink onTab marginRight" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref5AddKeysLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref5Keys'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3109, gridId: 'ref5KeysLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type5.keyCatTable.link" /></a></span>
                                    <div id="ref5KeysTabContainer" selectedChild="ref5KeysTab1">
                                        <div id='ref5KeysTab1' label="<fmt:message key="ui.obj.type5.keyCatTable.tab.text" />">
                                            <div class="input tableContainer">
                                                <div id="ref5KeysText" autoHeight="3" minRows="3" interactive="true" class="">
                                                </div>
                                            </div>
                                        </div>
                                        <div id='ref5KeysTab2' label="<fmt:message key="ui.obj.type5.keyCatTable.tab.links" />">
                                            <div id="ref5KeysLink" class="hideTableHeader noValidate" contextMenu="EDIT_LINK" relation_filter="3109" query="{relationType:'3109'}">
                                            </div>
                                        </div>
                                    </div>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3110" class="outer">
                                	<div><span class="label">
                                        <label for="ref5dbContent" onclick="javascript:dialog.showContextHelp(arguments[0], 3110)">
                                            <fmt:message key="ui.obj.type5.contentTable.title" />
                                        </label>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="ref5dbContent" autoHeight="4" interactive="true">
                                        </div>
                                    </div>
								    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement3100" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="ref5MethodTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3100)">
                                                <fmt:message key="ui.obj.type5.methodTable.title" />
                                            </label>
                                       </span><span id="ref5MethodTab2Header" class="functionalLink onTab marginRightColumn" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref5AddMethodLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref5Method'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3100, gridId: 'ref5MethodLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type5.methodTable.link" /></a></span>
                                        <div id="ref5MethodTabContainer" selectedChild="ref5MethodTab1">
                                            <div id='ref5MethodTab1' label="<fmt:message key="ui.obj.type5.methodTable.tab.text" />">
                                                <span class="input"><input type="text" maxLength="255" id="ref5MethodText" name="ref5MethodText" /></span>
                                            </div>
                                            <div id='ref5MethodTab2' label="<fmt:message key="ui.obj.type5.methodTable.tab.links" />">
                                                <div class="input tableContainer">
                                                    <div id="ref5MethodLink" autoHeight="2" query="{relationType:'3100'}" class="hideTableHeader noValidate" contextMenu="EDIT_LINK" relation_filter="3100">
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
										</div>
                                    </span>
                                    <span id="uiElement3120" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="ref5Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 3120)">
                                                <fmt:message key="ui.obj.type5.description" />
                                            </label>
                                        </span><span class="input"><input type="text" id="ref5Explanation" name="ref5Explanation" /></span>
										</div>
									</span>
                            </div>
                        </div>
                       	<div class="fill"></div>
                    </div>
                    <!-- FACHBEZUG CLASS 6 - DIENST/ANWENDUNG/INFORMATIONSSYSTEM //-->
                    <div class="fill"></div>
                    <div id="refClass6" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('refClass6');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7000)">
                                <fmt:message key="ui.obj.relevance" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="ref6Content" class="content">
                            <div class="inputContainer">
                                    <span id="uiElement3620" class="outer halfWidth">
                                        <div>
                                        <span id="ref6ServiceTypeLabel" class="label">
                                            <label for="ref6ServiceType" onclick="javascript:dialog.showContextHelp(arguments[0], 3620)">
                                                <fmt:message key="ui.obj.type6.serviceType" />
                                            </label>
                                        </span>
                                        <span class="input spaceBelow">
                                            <div maxLength="255" autoComplete="false" style="width:100%;" listId="5300" id="ref6ServiceType">
                                            </div>
                                        </span>
                                        </div>
                                    </span>
                                    <span id="uiElement3630" class="outer halfWidth">
                                        <div>
                                        <span class="label">
                                            <label for="ref6ServiceVersion" onclick="javascript:dialog.showContextHelp(arguments[0], 3630)">
                                                <fmt:message key="ui.obj.type6.serviceVersion" />
                                            </label>
                                        </span>
                                        <div class="input tableContainer">
                                            <div id="ref6ServiceVersion" autoHeight="4" interactive="true"></div>
                                        </div>
                                        </div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement3600" class="outer halfWidth">
                                        <div>
                                        <span class="label">
                                            <label for="ref6SystemEnv" onclick="javascript:dialog.showContextHelp(arguments[0], 3600)">
                                                <fmt:message key="ui.obj.type6.environment" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="ref6SystemEnv" />
                                        </span>
                                        </div>
                                    </span>
                                    <span id="uiElement3640" class="outer halfWidth">
                                        <div>
                                        <span class="label">
                                            <label for="ref6History" onclick="javascript:dialog.showContextHelp(arguments[0], 3640)">
                                                <fmt:message key="ui.obj.type6.history" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="ref6History" />
                                        </span>
                                        </div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement3645" class="outer halfWidth">
                                        <div>
                                        <span class="label">
                                            <label for="ref6BaseDataTabContainer" onclick="javascript:dialog.showContextHelp(arguments[0], 3645)">
                                                <fmt:message key="ui.obj.type6.generalDataTable.title" />
                                            </label>
                                        </span>
                                        <span id="ref6MethodTab2Header" class="functionalLink onTab marginRightColumn" style="display: none;">
                                            <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref6AddBaseDataLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('ref6BaseData'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3210, gridId: 'ref6BaseDataLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.links.linksTo.link" /></a>
                                        </span>
                                        <div id="ref6BaseDataTabContainer" selectedChild="ref6BaseDataTab1">
                                                <input type="text" id="ref6BaseDataText" />
                                        </div>
                                        <div class="input tableContainer">
                                            <div id="ref6MethodTab2">
                                                <div id="ref6BaseDataLink" autoHeight="2" class="hideTableHeader noValidate" contextMenu="EDIT_LINK" relation_filter="3210" query="{relationType:'3210'}"></div>
                                            </div>
                                        </div>
                                        </div>
                                    </span>
                                    <span id="uiElement3650" class="outer halfWidth">
                                        <div>
                                        <span class="label">
                                            <label for="ref6Explanation" onclick="javascript:dialog.showContextHelp(arguments[0], 3650)">
                                                <fmt:message key="ui.obj.type6.description" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="ref6Explanation" />
                                        </span>
                                        </div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3670" class="outer"><div>
                                    <span class="label">
                                        <label for="ref6UrlList" onclick="javascript:dialog.showContextHelp(arguments[0], 3670)">
                                            <fmt:message key="ui.obj.type6.urlList" />
                                        </label>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="ref6UrlList" autoHeight="4" interactive="true"></div>
                                    </div>
                                    </div>
                                </span>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- RAUMBEZUG //-->
                    <div class="fill"></div>
                    <div id="spatialRef" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('spatialRef');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7001)">
                                <fmt:message key="ui.obj.spatial.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="spatialRefContent" class="content">
                            <div id="spatialRefAdminUnitContainer" class="inputContainer">
                                <span id="uiElementN006" class="outer">
                                	<div><span id="spatialRefAdminUnitLabel" class="label">
                                        <label for="spatialRefAdminUnit" onclick="javascript:dialog.showContextHelp(arguments[0], 10006)">
                                            <fmt:message key="ui.obj.spatial.geoThesTable.title" />
                                        </label>
                                    </span><span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="spatialRefAdminUnitLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('spatialRefAdminUnit'), 'dialogs/mdek_spatial_navigator.jsp?c='+userLocale, 530, 230, true);" title="<fmt:message key="dialog.popup.geoThesTable.link" /> [Popup]"><fmt:message key="ui.obj.spatial.geoThesTable.link" /></a></span>
                                    <div class="input tableContainer">
                                        <div id="spatialRefAdminUnit" autoHeight="4">
                                        </div>
                                    </div>
									</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElementN007" class="outer">
                                    <div id="spatialRefCoordsAdminUnit" class="infobox">
                                        <span class="icon" onclick="javascript:dialog.showContextHelp(arguments[0], 7012)"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span><span class="title"><a href="javascript:toggleInfo('spatialRefCoordsAdminUnit');" title="<fmt:message key="general.info.open" />"><fmt:message key="ui.obj.spatial.transformedCoordinates" /><img id="spatialRefCoordsAdminUnitToggle" src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a></span>
                                        <div id="spatialRefCoordsAdminUnitContent" style="display:block; height: 44px;">
                                            <span class="outer halfWidth"><div style="margin-left: -6px; margin-top: 3px;">
                                                <div class="input">
                                                    <select id="spatialRefAdminUnitSelect" toggle="plain" style="width:100%; margin:0.01px;">
                                                        <!--<option value="GEO_WGS84"><fmt:message key="dialog.research.ext.obj.coordinates.wgs84" /></option> ... -->
                                                    </select>
                                                </div>
                                            </div></span>
                                            <span class="outer halfWidth"><div>
                                                <div class="input">
                                                    <div id="spatialRefAdminUnitCoords" autoHeight="1" class="hideTableHeader">
                                                    </div>
                                                </div>
                                            </div></span>
                                        </div>
                                    </div>
                                </span>
                            </div>
                            <div id="spatialRefLocationContainer" class="inputContainer">
                                <span id="uiElementN008" class="outer">
                                	<div><span id="spatialRefLocationLabel" class="label">
                                        <label for="spatialRefLocation" onclick="javascript:dialog.showContextHelp(arguments[0], 10008)">
                                            <fmt:message key="ui.obj.spatial.geoTable.title" />
                                        </label>
                                    </span><span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="spatialRefLocationLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('spatialRefLocation'), 'dialogs/mdek_spatial_assist_dialog.jsp?c='+userLocale, 555, 240, true);" title="<fmt:message key="dialog.popup.geoTable.link" /> [Popup]"><fmt:message key="ui.obj.spatial.geoTable.link" /></a><!-- The following feature is not yet implemented --><!--
                                        <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:void(0);" title="<fmt:message key="dialog.popup.geoSearch.link" /> [Popup]"><fmt:message key="dialog.popup.geoSearch.link" /></a>
                                        --></span>
                                    <div class="input tableContainer">
                                        <div id="spatialRefLocation" interactive="true" autoHeight="4">
                                        </div>
                                    </div>
									</div>
                                    <div style="position:relative; float:right;">
                                        <span id="updateGetSpatialRefLocationFromParent" style="visibility:hidden">
                                            <img src="img/ladekreis.gif" style="vertical-align: middle;"/>
                                        </span>
                                        <button id="btnGetSpatialRefLocationFromParent"><fmt:message key="ui.obj.spatial.btnGetSpatialRefLocationFromParent" /></button>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElementN009" class="outer">
                                    <div id="spatialRefCoordsLocation" class="infobox">
                                        <span class="icon" onclick="javascript:dialog.showContextHelp(arguments[0], 7013)"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span><span class="title"><a href="javascript:toggleInfo('spatialRefCoordsLocation');" title="<fmt:message key="general.info.open" />"><fmt:message key="ui.obj.spatial.transformedCoordinates" /><img src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a></span>
                                        <div id="spatialRefCoordsLocationContent" style="display:block; height: 44px;">
                                            <span class="outer halfWidth"><div style="margin-left: -6px; margin-top: 3px;">
                                                <div class="input">
                                                    <select id="spatialRefLocationSelect" toggle="plain" style="width:100%; margin:0.01px;">
                                                        <!--<option value="GEO_WGS84"><fmt:message key="dialog.research.ext.obj.coordinates.wgs84" /></option> ... -->
                                                    </select>
                                                </div>
                                                </div>
                                            </span>
                                            <span class="outer halfWidth"><div>
                                                <div class="input">
                                                    <div id="spatialRefLocationCoords" autoHeight="1" class="hideTableHeader">
                                                    </div>
                                                </div>
                                                </div>
                                            </span>
                                        </div>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3500" class="outer">
                                    <div><span class="label">
                                        <label for="ref1SpatialSystem" onclick="javascript:dialog.showContextHelp(arguments[0], 3500)">
                                            <fmt:message key="ui.obj.type1.spatialSystem" />
                                        </label>
                                    </span>
                                    <span class="input">
                                        <!--<input maxLength="120" autoComplete="false" style="width:100%;" maxHeight="150" listId="100" id="ref1SpatialSystem" />-->
                                        <div id="ref1SpatialSystem" interactive="true" autoHeight="3" class="hideTableHeader"></div>
                                    </span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElementN010" class="outer halfWidth">
                                    	<div><span id="spatialRefAltHeightLabel" class="label">
                                            <label for="spatialRefAltMin" onclick="javascript:dialog.showContextHelp(arguments[0], 4520)">
                                                <fmt:message key="ui.obj.spatial.height" />
                                            </label>
                                        </span>
										<div class="outlined">
                                            	<span id="uiElement1130" class="outer" style="width:33%;">
                                            		<div><span id="spatialRefAltMinLabel" class="label">
                                                        <label for="spatialRefAltMin" onclick="javascript:dialog.showContextHelp(arguments[0], 1130)">
                                                            <fmt:message key="ui.obj.spatial.height.min" />
                                                        </label>
                                                    </span><span class="input"><input type="text" id="spatialRefAltMin" name="spatialRefAltMin" /></span>
													</div>
													</span>
													<span id="uiElement5020" class="outer" style="width:33%;">
													<div><span id="spatialRefAltMaxLabel" class="label">
                                                        <label for="spatialRefAltMax" onclick="javascript:dialog.showContextHelp(arguments[0], 5020)">
                                                            <fmt:message key="ui.obj.spatial.height.max" />
                                                        </label>
                                                    </span><span class="input"><input type="text" id="spatialRefAltMax" name="spatialRefAltMax" /></span>
													</div></span>
													<span id="uiElement5021" class="outer" style="width:34%;">
													   <div><span id="spatialRefAltMeasureLabel" class="label">
                                                        <label for="spatialRefAltMeasure" onclick="javascript:dialog.showContextHelp(arguments[0], 5021)">
                                                            <fmt:message key="ui.obj.spatial.height.unit" />
                                                        </label>
                                                    </span><span class="input"><input listId="102" style="width:100%;" id="spatialRefAltMeasure" /></span>
													</div></span>
													<span id="uiElement5022" class="outer fullWidth">
													   <div><span id="spatialRefAltVDateLabel" class="label">
                                                        <label for="spatialRefAltVDate" onclick="javascript:dialog.showContextHelp(arguments[0], 5022)">
                                                            <fmt:message key="ui.obj.spatial.height.geodeticSystem" />
                                                        </label>
                                                    </span><span class="input"><input style="width:100%;" listId="101" id="spatialRefAltVDate" /></span>
													</div>
												</span>
												<div class="fill"></div>
											</div>
										</div>
                                    </span>
                                    <span id="uiElement1140" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="spatialRefExplanation" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                                <fmt:message key="ui.obj.spatial.description" />
                                            </label>
                                        </span><span class="input"><input type="text" id="spatialRefExplanation" name="spatialRefExplanation" /></span>
										</div>
									</span>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- ZEITBEZUG //-->
                    <div class="fill"></div>
                    <div id="timeRef" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('timeRef');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7002)">
                                <fmt:message key="ui.obj.time.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="timeRefContent" class="content">
                            <div class="inputContainer">
                                    <span id="uiElement5030" class="outer halfWidth">
                                    	<div><span id="timeRefTableLabel" class="label">
                                            <label for="timeRefTable" onclick="javascript:dialog.showContextHelp(arguments[0], 5030)">
                                                <fmt:message key="ui.obj.time.timeRefTable.title" />
                                            </label>
                                        </span>
                                        <div class="input tableContainer">
                                            <div id="timeRefTable" interactive="true" autoHeight="4" onRowContextMenu:"onRowEvent">
                                            </div>
                                        </div>
										</div>
                                    </span>
                                    <span id="uiElement1250" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="timeRefExplanation" onclick="javascript:dialog.showContextHelp(arguments[0], 1250)">
                                                <fmt:message key="ui.obj.time.description" />
                                            </label>
                                        </span><span class="input"><input type="text" id="timeRefExplanation" name="timeRefExplanation" class="w320 h105" /></span>
										</div>
									</span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElementN011" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label onclick="javascript:dialog.showContextHelp(arguments[0], 10011)">
                                                <fmt:message key="ui.obj.time.timeRefContent" />
                                            </label>
                                        </span>
                                        <div id="timeRefRef">
                                        	<span class="outer" style="width:33%;">
											    <div>
                                                <span class="label hidden">
                                                    <label for="timeRefType">
                                                        Typ
                                                    </label>
                                                </span><span class="input">
                                                    <select autoComplete="false" style="width:100%;" id="timeRefType">
                                                        <!--<option value="am"><fmt:message key="dialog.research.ext.obj.content.time.at" /></option><option value="seit"><fmt:message key="dialog.research.ext.obj.content.time.since" /></option><option value="bis"><fmt:message key="dialog.research.ext.obj.content.time.until" /></option><option value="von"><fmt:message key="dialog.research.ext.obj.content.time.fromto" /></option>-->
                                                    </select>
                                                </span>
												</div>
											</span>
											<span class="outer" style="width:33%;">
											    <div><span class="label hidden">
                                                    <label for="timeRefDate1">
                                                        Datum 1 [TT.MM.JJJJ]
                                                    </label></span>
                                                <span class="input">
                                                    <div id="timeRefDate1" displayFormat="dd.MM.yyyy" name="timeRefDate1">
                                                    </div>
                                                    <br/>
                                                    TT.MM.JJJJ
                                                </span>
												</div>
											</span>
                                            <span id="timeRefDate2Editor" class="outer" style="width:34%;">
											    <div><span class="label hidden">
                                                    <label for="timeRefDate2">
                                                        Datum 2 [TT.MM.JJJJ]
                                                    </label>
                                                </span><span class="input">
                                                    <div id="timeRefDate2" displayFormat="dd.MM.yyyy" name="timeRefDate2">
                                                    </div>
                                                    <br/>
                                                    TT.MM.JJJJ
                                                </span>
												</div>
                                            </span>
                                        </div>
										</div>
                                    </span>
                                    <span id="uiElement1220" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="timeRefStatus" onclick="javascript:dialog.showContextHelp(arguments[0], 1220)">
                                                <fmt:message key="ui.obj.time.state" />
                                            </label>
                                        </span><span class="input"><input style="width:100%;" listId="523" id="timeRefStatus" /></span>
										</div>
									</span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement1240" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="timeRefPeriodicity" onclick="javascript:dialog.showContextHelp(arguments[0], 1240)">
                                                <fmt:message key="ui.obj.time.periodicity" />
                                            </label>
                                        </span><span class="input"><input style="width:100%;" listId="518" id="timeRefPeriodicity" /></span>
										</div>
									</span>
                                    <span id="uiElement1230" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label onclick="javascript:dialog.showContextHelp(arguments[0], 1230)">
                                                <fmt:message key="ui.obj.time.interval" />
                                            </label>
                                        </span>
                                        <div id="timeRefInterval">
                                            <span  class="outer" style="width:30%;">
                                            	<span><fmt:message key="ui.obj.time.interval.each" /></span>
												<span class="label hidden">
	                                                <label for="timeRefIntervalNum">
	                                                    Intervall Anzahl
	                                                </label>
	                                            </span>
	                                            <span class="input" style="display:inline;"><input type="text" id="timeRefIntervalNum" maxLength="40" name="timeRefIntervalNum" /></span>
											</span>
											<span  class="outer" style="width:70%;">
												<span class="label hidden">
	                                                <label for="timeRefIntervalUnit">
	                                                    Intervall Einheit
	                                                </label>
	                                            </span>
	                                            <span class="input"><input style="width:100%" listId="1230" id="timeRefIntervalUnit" /></span>
											</span>
                                        </div>
										</div>
                                    </span>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- ZUSATZINFORMATION //-->
                    <div class="fill"></div>
                    <div id="extraInfo" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('extraInfo');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7003)">
                                <fmt:message key="ui.obj.additionalInfo.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="extraInfoContent" class="content">
                            <div class="inputContainer">
                                    <span id="uiElement5041" class="outer halfWidth">
                                    	<div><span id="extraInfoLangMetaDataLabel" class="label">
                                            <label for="extraInfoLangMetaData" onclick="javascript:dialog.showContextHelp(arguments[0], 5041)">
                                                <fmt:message key="ui.obj.additionalInfo.language.metadata" />
                                            </label>
                                        </span><span class="input"><input autoComplete="false" style="width:100%;" listId="99999999" id="extraInfoLangMetaData" /></span>
										</div>
									</span>
                                    <span id="uiElement5042" class="outer halfWidth">
                                    	<div><span id="extraInfoLangDataLabel" class="label">
                                            <label for="extraInfoLangData" onclick="javascript:dialog.showContextHelp(arguments[0], 5042)">
                                                <fmt:message key="ui.obj.additionalInfo.language.data" />
                                            </label>
                                        </span><span class="input"><input autoComplete="false" style="width:100%;" listId="99999999" id="extraInfoLangData" /></span>
										</div>
									</span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement3571" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label id="extraInfoPublishAreaLabel" for="extraInfoPublishArea" onclick="javascript:dialog.showContextHelp(arguments[0], 3571)">
                                                <fmt:message key="ui.obj.additionalInfo.publicationCondition" />
                                            </label>
                                        </span><span class="input"><input autoComplete="false" style="width:100%;" listId="3571" id="extraInfoPublishArea" /></span>
										</div>
									</span>
                                    <span id="uiElement5043" class="outer halfWidth">
                                        <div>
                                        <span id="extraInfoCharSetDataLabel" class="label">
                                            <label for="extraInfoCharSetData" onclick="javascript:dialog.showContextHelp(arguments[0], 5043)">
                                                <fmt:message key="ui.obj.additionalInfo.charSet.data" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input autoComplete="false" style="width:100%;" listId="510" id="extraInfoCharSetData" />
                                        </span>
                                        </div>
                                    </span>
                            </div>
                            <!-- displaytype="exclude", the inputContainer is excluded from the standard show/hide mechanism since the container has to be displayed depending on the selected object class -->
                            <span id="uiElementN024" class="outer exclude">
                            	<div><span id="extraInfoConformityTableLabel" class="label">
                                    <label for="extraInfoConformityTable" onclick="javascript:dialog.showContextHelp(arguments[0], 10024)">
                                        <fmt:message key="ui.obj.additionalInfo.conformityTable.title" />
                                    </label>
                                </span>
                                <div class="input tableContainer">
                                    <div id="extraInfoConformityTable" autoHeight="4" interactive="true">
                                    </div>
                                </div>
								</div>
                            </span>
                            <div class="inputContainer">
                                    <span id="uiElementN012" class="outer" style="width:33%;">
									   <div><span class="label">
                                            <label for="extraInfoXMLExportTable" onclick="javascript:dialog.showContextHelp(arguments[0], 1370)">
                                                <fmt:message key="ui.obj.additionalInfo.xmlExportCriteria" />
                                            </label>
                                        </span>
                                        <div class="input tableContainer">
                                            <div id="extraInfoXMLExportTable" autoHeight="4" interactive="true" class="hideTableHeader">
                                            </div>
                                        </div>
										</div>
                                    </span>
                                    <span id="uiElement1350" class="outer" style="width:67%;">
									   <div><span class="label">
                                            <label for="extraInfoLegalBasicsTable" onclick="javascript:dialog.showContextHelp(arguments[0], 1350)">
                                                <fmt:message key="ui.obj.additionalInfo.legalBasis" />
                                            </label>
                                        </span>
                                        <div class="input tableContainer">
                                            <div id="extraInfoLegalBasicsTable" autoHeight="4" interactive="true" class="hideTableHeader">
                                            </div>
                                        </div>
										</div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElementN013" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="extraInfoPurpose" onclick="javascript:dialog.showContextHelp(arguments[0], 10013)">
                                                <fmt:message key="ui.obj.additionalInfo.purpose" />
                                            </label>
                                        </span><span class="input"><input type="text" id="extraInfoPurpose" name="extraInfoPurpose" /></span>
										</div>
									</span>
                                    <span id="uiElement5040" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="extraInfoUse" onclick="javascript:dialog.showContextHelp(arguments[0], 5040)">
                                                <fmt:message key="ui.obj.additionalInfo.suitability" />
                                            </label>
                                        </span><span class="input"><input type="text" id="extraInfoUse" name="extraInfoUse" /></span>
										</div>
									</span>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- VERFÜGBARKEIT //-->
                    <div class="fill"></div>
                    <div id="availability" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('availability');" title="<fmt:message key="general.open.required.field" />">
                                 <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7004)">
                                <fmt:message key="ui.obj.availability.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="availabilityContent" class="content">
                            <div class="inputContainer">
                                <span id="uiElementN025" class="outer halfWidth">
                                	<div><span id="availabilityAccessConstraintsLabel" class="label">
                                        <label for="availabilityAccessConstraints" onclick="javascript:dialog.showContextHelp(arguments[0], 10025)">
                                            <fmt:message key="ui.obj.availability.accessConstraints" />
                                        </label>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="availabilityAccessConstraints" autoHeight="4" interactive="true" class="hideTableHeader">
                                        </div>
                                    </div>
									</div>
                                </span>
                                <span id="uiElementN026" class="outer halfWidth">
                                    <div>
                                    <span id="availabilityUseConstraintsLabel" class="label">
                                        <label for="availabilityUseConstraints" onclick="javascript:dialog.showContextHelp(arguments[0], 10026)">
                                            <fmt:message key="ui.obj.availability.useConstraints" />
                                        </label>
                                    </span>
                                    <span class="functionalLink">
                                        <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" />
                                        <a id="availabilityUseConstraintsLink" href="javascript:void(0);" onclick="javascript:dialog.showPage(getLocalizedTitle('availabilityUseConstraints'), 'dialogs/mdek_use_constraints_dialog.jsp?c='+userLocale, 735, 300, true);" title="<fmt:message key="dialog.popup.useConstraints.link" /> [Popup]"><fmt:message key="ui.obj.availability.useConstraints.link" /></a>
                                    </span>
                                    <span class="input"><input type="text" id="availabilityUseConstraints" name="availabilityUseConstraints" /></span>
                                    </div>
                                </span>
                            </div>
                            <span id="uiElement1315" class="outer">
                                <div>
                                <span id="availabilityDataFormatInspireLabel" class="label">
                                    <label for="availabilityDataFormatInspire" onclick="javascript:dialog.showContextHelp(arguments[0], 1315)">
                                        <fmt:message key="ui.obj.availability.dataFormatInspire" />
                                    </label>
                                </span>
                                <span class="input spaceBelow">
                                    <div maxLength="255" id="availabilityDataFormatInspire" style="width:100%;" listId="6300">
                                    </div>
                                </span>
                                </div>
                            </span>
                            <div class="inputContainer">
                                <span id="uiElement1320" class="outer">
                                	<div><span class="label" id="availabilityDataFormatLabel">
                                        <label for="availabilityDataFormat" onclick="javascript:dialog.showContextHelp(arguments[0], 1320)">
                                            <fmt:message key="ui.obj.availability.dataFormatTable.title" />
                                        </label>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="availabilityDataFormat" autoHeight="4" interactive="true">
                                        </div>
                                    </div>
									</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement1310" class="outer">
                                	<div><span class="label">
                                        <label for="availabilityMediaOptions" onclick="javascript:dialog.showContextHelp(arguments[0], 1310)">
                                            <fmt:message key="ui.obj.availability.mediaOptionTable.title" />
                                        </label>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="availabilityMediaOptions" autoHeight="4" interactive="true">
                                        </div>
                                    </div>
									</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement5052" class="outer">
                                    	<div><span class="label">
                                            <label for="availabilityOrderInfo" onclick="javascript:dialog.showContextHelp(arguments[0], 5052)">
                                                <fmt:message key="ui.obj.availability.orderInfo" />
                                            </label>
                                        </span>
										<span class="input"><input type="text" id="availabilityOrderInfo" name="availabilityOrderInfo" /></span>
									</div></span>
                                <div class="fill">
                                </div>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- VERWEISE //-->
                    <div class="fill"></div>
                    <div id="links" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('links');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7007)">
                                <fmt:message key="ui.obj.links.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="linksContent" class="content">
                            <span id="uiElementN017" class="outer" style="width:50%;">
                                <div>
                                    <span class="label">
                                        <label for="linksTo" onclick="javascript:dialog.showContextHelp(arguments[0], 1500)">
                                            <fmt:message key="ui.obj.links.linksTo.title" />
                                        </label>
                                    </span>
                                    <span class="functionalLink">
                                        <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage(getLocalizedTitle('linksTo'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {gridId: 'linksTo'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.links.linksTo.link" /></a>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="linksTo" autoHeight="4" class="hideTableHeader" contextMenu="EDIT_LINK">
                                        </div>
                                    </div>
                                </div>
                                <div style="position:relative; float:right;">
                                    <span id="updateGetLinksToFromParent" style="visibility:hidden">
                                        <img src="img/ladekreis.gif" style="vertical-align: middle;"/>
                                    </span>
                                    <button id="btnGetLinksToFromParent"><fmt:message key="ui.obj.links.btnGetLinksToFromParent" /></button>
                                </div>
                            </span>
                            <span id="uiElementN018" class="outer" style="width:50%;">
                                <div>
                                    <span class="label">
                                        <label for="linksFrom" onclick="javascript:dialog.showContextHelp(arguments[0], 1510)">
                                            <fmt:message key="ui.obj.links.linksFrom.title" />
                                        </label>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="linksFrom" autoHeight="4" class="hideTableHeader" contextMenu="none">
                                        </div>
                                    </div>
                                </div>
                            </span>
                            <div class="fill">
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                </div>
            </div>
            <div id="contentAddress" style="display:none; width:735px;">
                <div id="sectionTopAddress" class="sectionTop" style="display:none; width:720px;">
                    <table cellspacing="5" class="sectionTopTable">
                        <tbody>
                            <tr>
                                <td class="label">
                                    <label for="addressTitle" onclick="javascript:dialog.showContextHelp(arguments[0], 7044, 'Adresstitel')">
                                        <fmt:message key="ui.adr.header.addressTitle" />*
                                    </label>
                                </td>
                                <td colspan="2">
                                    <input type="text" id="addressTitle" name="addressTitle" style="width:100%;" disabled="disabled" />
                                </td>
                            </tr>
                            <tr>
                                <td id="addressTypeLabel" class="label col1">
                                    <label for="addressType" onclick="javascript:dialog.showContextHelp(arguments[0], 7045, 'Adresstyp')">
                                        <fmt:message key="ui.adr.header.addressType" />*
                                    </label>
                                </td>
                                <td class="col2">
                                    <input type="text" id="addressType" style="width:100%;" disabled="disabled" />
                                </td>
                                <td class="col3">
                                    <img id="permissionAdrLock" src="img/lock.gif" width="9" height="14" alt="gesperrt" />
                                </td>
                            </tr>
                            <tr>
                                <td id="addressOwnerLabel" class="label">
                                    <label for="addressOwner" onclick="javascript:dialog.showContextHelp(arguments[0], 1030, 'Verantwortlicher')">
                                        <fmt:message key="ui.adr.header.responsibleUser" />*
                                    </label>
                                </td>
                                <td>
                                    <input required="true" style="width:100%; margin:0px;" id="addressOwner" />
                                </td>
                                <td class="note bgBlue">
                                    <strong><fmt:message key="ui.adr.header.workState" />:</strong><span id="addressWorkState"></span>
                                </td>
                            </tr>
                            <tr>
                                <td class="note bgBlue" colspan="3">
                                    <strong><fmt:message key="ui.adr.header.creationTime" />:</strong><span id="addressCreationTime">----------</span>
                                    | <strong><fmt:message key="ui.adr.header.modificationTime" />:</strong><span id="addressModificationTime">----------</span>
                                    | <strong><fmt:message key="ui.adr.header.modUser" />:</strong><span id="addressLastEditor">---</span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div id="contentFrameBodyAddress" class="contentContainer" style="display:none; width:720px;">
                    <!-- GREY FIELD //-->
                    <!-- ADDRESS TYPE 0 //-->
                    <div id="headerAddressType0" class="rubric contentBlock firstBlock">
                        <div id="headerAddressType0Content" class="content">
                            <!-- AddressType0 parent institutions must not be displayed (http://jira.media-style.com/browse/INGRIDII-130) --><!--
                            <div class="inputContainer">
                            <span class="label"><label for="headerAddressType0Institution" onclick="javascript:dialog.showContextHelp(arguments[0], 8062, 'Institution/&uuml;bergeordnete Einheit(en)')">Institution/&uuml;bergeordnete Einheit(en)</label></span>
                            <span class="input"><input type="text" dojoType="ingrid:ValidationTextbox" id="headerAddressType0Institution" class="w668 h038" disabled="true" /></span>
                            </div>
                            -->
                            <div class="inputContainer">
                                <span id="uiElement4100" class="outer required">
                                    <div><span id="headerAddressType0UnitLabel" class="label">
                                        <label for="headerAddressType0Unit" onclick="javascript:dialog.showContextHelp(arguments[0], 4100)">
                                            <fmt:message key="ui.adr.general.institution" />
                                        </label>
                                    </span><span class="input"><input type="text" maxLength="255" required="true" id="headerAddressType0Unit" /></span>
                                    </div>
                                </span>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div><!-- ADDRESS TYPE 1 //-->
                    <!-- ADDRESS TYPE 1 //-->                    
                    <div id="headerAddressType1" class="rubric contentBlock firstBlock">
                        <div id="headerAddressType1Content" class="content">
                            <div class="inputContainer">
                                <span id="uiElement04210" class="outer optional show">
                                    <div><span class="label">
                                        <label for="headerAddressType1Institution" onclick="javascript:dialog.showContextHelp(arguments[0], 4210)">
                                            <fmt:message key="ui.adr.general.parentInstitution" />
                                        </label>
                                    </span><span class="input"><input type="text" id="headerAddressType1Institution" disabled="disabled" /></span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement04200" class="outer required">
                                    <div><span id="headerAddressType1UnitLabel" class="label">
                                        <label for="headerAddressType1Unit" onclick="javascript:dialog.showContextHelp(arguments[0], 4200)">
                                            <fmt:message key="ui.adr.general.unit" />
                                        </label>
                                    </span><span class="input"><input type="text" maxLength="255" required="true" id="headerAddressType1Unit" /></span>
                                    </div>
                                </span>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div><!-- ADDRESS TYPE 2 //-->
                    <!-- ADDRESS TYPE 2 //-->                    
                    <div id="headerAddressType2" class="rubric contentBlock firstBlock">
                        <div id="headerAddressType2Content" class="content">
                            <div class="inputContainer">
                                <span id="uiElement14210" class="outer optional show">
                                    <div><span class="label">
                                        <label for="headerAddressType2Institution" onclick="javascript:dialog.showContextHelp(arguments[0], 4210)">
                                           <fmt:message key="ui.adr.general.parentInstitution" />
                                        </label>
                                    </span><span class="input"><input type="text" id="headerAddressType2Institution" disabled="disabled" /></span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement4315" class="outer halfWidth required">
                                        <div><span id="headerAddressType2LastnameLabel" class="label">
                                            <label for="headerAddressType2Lastname" onclick="javascript:dialog.showContextHelp(arguments[0], 4315)">
                                                <fmt:message key="ui.adr.general.surName" />
                                            </label>
                                        </span><span class="input"><input type="text" maxLength="40" id="headerAddressType2Lastname" required="true" name="headerAddressType2Lastname" /></span>
                                        </div>
                                    </span>
                                    <span id="uiElement4310" class="outer halfWidth optional show">
                                        <div><span id="headerAddressType2FirstnameLabel" class="label">
                                            <label for="headerAddressType2Firstname" onclick="javascript:dialog.showContextHelp(arguments[0], 4310)">
                                                <fmt:message key="ui.adr.general.foreName" />
                                            </label>
                                        </span><span class="input"><input type="text" maxLength="40" id="headerAddressType2Firstname" name="headerAddressType2Firstname" /></span>
                                        </div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement4300" class="outer halfWidth optional show">
                                        <div><span id="headerAddressType2StyleLabel" class="label">
                                            <label for="headerAddressType2Style" onclick="javascript:dialog.showContextHelp(arguments[0], 4300)">
                                                <fmt:message key="ui.adr.general.form" />
                                            </label>
                                        </span><span class="input"><input maxLength="40" style="width:129px;" listId="4300" id="headerAddressType2Style" /></span>
                                        </div>
                                    </span>
                                    <span id="uiElement4305" class="outer halfWidth optional show">
                                        <div><span id="headerAddressType2TitleLabel" class="label">
                                            <label for="headerAddressType2Title" onclick="javascript:dialog.showContextHelp(arguments[0], 4305)">
                                                <fmt:message key="ui.adr.general.title" />
                                            </label>
                                        </span><span class="input"><input maxLength="40" style="width:129px;" listId="4305" id="headerAddressType2Title" /></span>
                                        </div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement4320" class="outer optional show">
                                    <div>
                                    <div class="input">
                                        <input type="checkbox" id="headerAddressType2HideAddress" />
                                        <label onclick="javascript:dialog.showContextHelp(arguments[0], 4320)">
                                            <fmt:message key="ui.adr.general.hideAddress" />
                                        </label>
                                    </div>
                                    </div>
                                </span>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- ADDRESS TYPE 3 //-->
                    <div id="headerAddressType3" class="rubric contentBlock firstBlock">
                        <div id="headerAddressType3Content" class="content">
                            <div class="inputContainer">
                                    <span id="uiElement4000" class="outer halfWidth required">
                                        <div><span id="headerAddressType3LastnameLabel" class="label">
                                            <label for="headerAddressType3Lastname" onclick="javascript:dialog.showContextHelp(arguments[0], 4000)">
                                                <fmt:message key="ui.adr.general.surName" />
                                            </label>
                                        </span><span class="input"><input type="text" maxLength="40" id="headerAddressType3Lastname" required="true" name="headerAddressType3Lastname" class="w320" /></span>
                                        </div>
                                    </span>
                                    <span id="uiElement4005" class="outer halfWidth optional show">
                                        <div><span id="headerAddressType3FirstnameLabel" class="label">
                                            <label for="headerAddressType3Firstname" onclick="javascript:dialog.showContextHelp(arguments[0], 4005)">
                                                <fmt:message key="ui.adr.general.foreName" />
                                            </label>
                                        </span><span class="input"><input type="text" maxLength="40" id="headerAddressType3Firstname" name="headerAddressType3Firstname" class="w320" /></span>
                                        </div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement4015" class="outer halfWidth optional show">
                                        <div><span id="headerAddressType3StyleLabel" class="label">
                                            <label for="headerAddressType3Style" onclick="javascript:dialog.showContextHelp(arguments[0], 4015)">
                                                <fmt:message key="ui.adr.general.form" />
                                            </label>
                                        </span><span class="input"><input maxLength="40" style="width:129px;" listId="4300" id="headerAddressType3Style" /></span>
                                        </div>
                                    </span>
                                    <span id="uiElement4020" class="outer halfWidth optional show">
                                        <div><span class="label">
                                            <label for="headerAddressType3Title" onclick="javascript:dialog.showContextHelp(arguments[0], 4020)">
                                                <fmt:message key="ui.adr.general.title" />
                                            </label>
                                        </span><span class="input"><input maxLength="40" style="width:129px;" listId="4305" id="headerAddressType3Title" /></span>
                                        </div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement4010" class="outer optional show">
                                    <div><span class="label">
                                        <label for="headerAddressType3Institution" onclick="javascript:dialog.showContextHelp(arguments[0], 4010)">
                                            Institution
                                        </label>
                                    </span><span class="input"><input type="text" maxLength="255" id="headerAddressType3Institution" class="w668 h038" /></span>
                                    </div>
                                </span>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div><!-- ADRESSE UND AUFGABEN //-->
                    <!-- ADDRESS //-->
                    <div class="fill"></div>
                    <div id="address" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('address');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7008)">
                                <fmt:message key="ui.adr.details.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="addressContent" class="content">
                            <div class="inputContainer">
                                <span id="uiElement4430" class="outer halfWidth required">
                                    <div>
                                        <span id="addressComLabel" class="label">
                                            <label for="addressCom" onclick="javascript:dialog.showContextHelp(arguments[0], 4430)">
                                                <fmt:message key="ui.adr.details.communicationTable.title" />
                                            </label>
                                        </span>
                                        <div class="input">
                                            <div id="addressCom" autoHeight="6" interactive="true" style="width:100%;">
                                            </div>
                                        </div>
                                    </div>
                                </span>
                                <span class="outer halfWidth optional">
                                    <span id="uiElement4400" class="outer" style="width:100%;">
                                        <div>
                                            <span id="addressStreetLabel" class="label">
                                                <label for="addressStreet" onclick="javascript:dialog.showContextHelp(arguments[0], 4400)">
                                                    <fmt:message key="ui.adr.details.street" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input type="text" id="addressStreet" maxLength="80" name="addressStreet" />
                                            </span>
                                        </div>
                                    </span>
                                    <span id="uiElement4410" class="outer" style="width:33%;">
                                        <div>
                                            <span id="addressZipCodeLabel" class="label">
                                                <label for="addressZipCode" onclick="javascript:dialog.showContextHelp(arguments[0], 4410)">
                                                    <fmt:message key="ui.adr.details.postCode" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input type="text" maxLength="10" id="addressZipCode" name="addressZipCode" />
                                            </span>
                                        </div>
                                    </span>
                                    <span id="uiElement4415" class="outer" style="width:67%;">
                                        <div>
                                            <span id="addressCityLabel" class="label">
                                                <label for="addressCity" onclick="javascript:dialog.showContextHelp(arguments[0], 4415)">
                                                    <fmt:message key="ui.adr.details.city" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input type="text" maxLength="80" id="addressCity" name="addressCity" />
                                            </span>
                                        </div>
                                    </span>
                                    <span id="uiElement4420" class="outer halfWidth">
                                        <div>
                                            <span id="addressPOBoxLabel" class="label">
                                                <label for="addressPOBox" onclick="javascript:dialog.showContextHelp(arguments[0], 4420)">
                                                    <fmt:message key="ui.adr.details.poBox" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input type="text" id="addressPOBox" maxLength="10" name="addressPOBox" class="w148" />
                                            </span>
                                        </div>
                                    </span>
                                    <span id="uiElement4425" class="outer halfWidth">
                                        <div>
                                            <span id="addressZipPOBoxLabel" class="label">
                                                <label for="addressZipPOBox" onclick="javascript:dialog.showContextHelp(arguments[0], 4425)">
                                                    <fmt:message key="ui.adr.details.poBoxPostCode" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input type="text" id="addressZipPOBox" maxLength="10" name="addressZipPOBox" class="w061" />
                                            </span>
                                        </div>
                                    </span>
                                    <span id="uiElement4405" class="outer" style="width:100%;">
                                        <div>
                                            <span id="addressCountryLabel" class="label">
                                                <label for="addressCountry" onclick="javascript:dialog.showContextHelp(arguments[0], 4405)">
                                                    <fmt:message key="ui.adr.details.state" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input autoComplete="false" style="width:100%;" listId="6200" maxHeight="200" id="addressCountry" />
                                            </span>
                                        </div>
                                    </span>
                                </span>
                            </div>
                        <span id="uiElement4440" class="outer optional">
                            <div>
                                <span class="label">
                                    <label for="addressTasks" onclick="javascript:dialog.showContextHelp(arguments[0], 4440)">
                                        <fmt:message key="ui.adr.details.tasks" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressTasks" />
                                </span>
                            </div>
                        </span>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- VERSCHLAGWORTUNG //-->
                    <div class="fill"></div>
                    <div id="adrThesaurus" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('adrThesaurus');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7009)">
                                <fmt:message key="ui.adr.thesaurus.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="adrThesaurusContent" class="content">
                            <div class="inputContainer">
                                <span id="uiElement4510" class="outer optional">
                                    <div><span class="label">
                                        <label for="thesaurusTermsAddress" onclick="javascript:dialog.showContextHelp(arguments[0], 4510)">
                                            <fmt:message key="ui.adr.thesaurus.terms" />
                                        </label>
                                    </span><span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage(getLocalizedTitle('thesaurusNavigator'), 'dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {dstTable: 'thesaurusTermsAddress'});" title="<fmt:message key="dialog.popup.thesaurus.terms.link.navigator" /> [Popup]"><fmt:message key="ui.adr.thesaurus.terms.link.navigator" /></a></span>
                                    <div class="input tableContainer">
                                        <div id="thesaurusTermsAddress" autoHeight="3" minRows="3" class="hideTableHeader">
                                        </div>
                                    </div>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                            	 <span id="uiElementN019" class="outer optional">
                            	 <div><span class="label">
                                        <label for="thesaurusTermsAddress" onclick="javascript:dialog.showContextHelp(arguments[0], 4500)">
                                            <fmt:message key="ui.adr.thesaurus.terms.custom" />
                                        </label>
                                        </span>
                                        <div class="input">
                                            <input type="text" maxLength="255" id="thesaurusFreeTermInputAddress" />
                                            <div style="position:relative; float:right;">
                                                <button id="thesaurusFreeTermsAddressAddButton">
                                                    <fmt:message key="ui.adr.thesaurus.terms.custom.buttonAdd" />
                                                </button>
                                            </div>
                                        </div>
                                   </div>
                                </span>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- ZUGEORDNETE OBJEKTE //-->
                    <div class="fill"></div>
                    <div id="associatedObj" class="rubric contentBlock">
                        <div class="titleBar">
                            <a href="javascript:igeEvents.toggleFields('associatedObj');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="javascript:dialog.showContextHelp(arguments[0], 7010)">
                                <fmt:message key="ui.adr.links.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="associatedObjContent" class="content" style="clear:both;">
                            <div class="inputContainer">
                                <span id="uiElementN020" class="outer optional">
                                    <div>
                                    <div class="listInfo full">
                                        <span id="associatedObjNameInfo" style="float:left;" class="searchResultsInfo">&nbsp;</span>
                                        <span id="associatedObjNamePaging" style="float:right;" class="searchResultsPaging">&nbsp;</span>
                                        <div class="fill">
                                        </div>
                                    </div>
                                    <div class="input tableContainer">
                                        <div id="associatedObjName" autoHeight="6" class="hideTableHeader" contextMenu="none">
                                        </div>
                                    </div>
                                    </div>
                                </span>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- END ZUGEORDNETE OBJEKTE //-->
                </div>
            </div>
            <!-- END CONTENT ADDRESS -->
            <div id="contentNone" class="contentContainer">
                <fmt:message key="general.noObjectAddressSelected" />
            </div>
        </div>

		<!-- use this for print div content, see commons.js -> printDivContent() -->
		<!--<iframe id="printFrame" name="printFrame" style="position:absolute; left:-1000px"></iframe>-->
    </body>
</html>