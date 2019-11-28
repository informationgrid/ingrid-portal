<%--suppress HtmlUnknownTag --%>
<%--suppress HtmlFormInputWithoutLabel --%>
<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String currLang = (String)session.getAttribute("currLang");%>

<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <meta http-equiv="X-UA-Compatible" content="chrome=1" />

        <!-- <script type="text/javascript" src="js/objectLayout.js"></script>
        <script type="text/javascript" src="js/addressLayout.js"></script> -->

		<script type="text/javascript" src="js/diff.js"></script>

        <script type="text/javascript" src="generated/dynamicJS.js?lang=<%=currLang%>&c=<%=java.lang.Math.random()%>"></script>
        <script type="text/javascript">
            var pageHierachy = _container_;
            require(["dojo/Deferred", "dijit/layout/BorderContainer", "dijit/layout/ContentPane", "dijit/registry", "dojo/_base/lang", "dojo/dom-construct", "dojo/topic",
                    "ingrid/tree/MetadataTree", "ingrid/IgeToolbar", "ingrid/IgeActions", "ingrid/tree/HierarchyTreeActions", "ingrid/utils/Catalog", "dojo/ready"
                ],
                function(Deferred, BorderContainer, ContentPane, registry, lang, construct, topic, MetadataTree, IgeToolbar, igeActions, TreeActions, UtilCatalog, ready) {

                    pageHierachy.dataTreePromise = new Deferred();

                    createLayout();

                    function createLayout() {

                        var contentBorderContainer = new BorderContainer({
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
                        }, "hierarchy"); //.placeAt(contentPane.domNode);
                        //===========================================================
                        // top pane - toolbar
                        var topPane = new ContentPane({
                            id: "toolbarPane",
                            liveSplitters: false,
                            gutters: false,
                            design: "headline",
                            region: "top",
                            style: "height: 29px;"
                        }).placeAt(contentBorderContainer.domNode);

                        // left pane - TreeContainer
                        var leftPane = new ContentPane({
                            id: "treePane",
                            splitter: true,
                            region: "leading",
                            style: "width: 200px; padding-right: 0px;",
                            "class": "contentContainer"
                        }).placeAt(contentBorderContainer.domNode);

                        //===========================================================
                        var objectPane = new BorderContainer({
                            splitter: false,
                            id: "dataFormContainer",
                            region: "center",
                            gutters: false
                            //style: "display:none;"
                        }).placeAt(contentBorderContainer.domNode);


                        new ContentPane({
                            id: "topContent",
                            region: "top",
                            style: "margin-bottom: 2px;"
                        }).placeAt(objectPane.domNode);
                        new ContentPane({
                            id: "centerContent",
                            region: "leading",
                            style: "width:740px;"
                        }).placeAt(objectPane.domNode);

                        construct.place("loadBlockDiv", objectPane.domNode);
                        construct.place("sectionTopObject", "topContent");
                        construct.place("contentFrameBodyObject", "centerContent");

                        construct.place("contentNone", "topContent");

                        construct.place("sectionTopAddress", "topContent");
                        construct.place("contentFrameBodyAddress", "centerContent");

                        // add toolbar to top-pane
                        IgeToolbar.createToolbar(topPane);

                        // add tree to left-pane
                        construct.create("div", {
                            id: "dataTree"
                        }, leftPane.domNode);

                        ready(function() {
                            var tree = new MetadataTree({
                                showRoot: false,
                                sortByClass: UtilCatalog.catalogData.sortByClass === "Y",
                                sortFunction: UtilCatalog.catalogData.treeSortFunction,
                                onClick: TreeActions.clickHandler,
                                // the onMouseDown handler is not always called in IE 11 when expanding a tree and right clicking
                                // a node immediately afterwards. That's why we register on the DNDController of the widget
                                // where the event is still received.
                                //onMouseDown: lang.partial(TreeActions.mouseDownHandler, TreeActions)
                                onLoad: function() {
                                    this.dndController.onMouseDown = lang.hitch(this, lang.partial(TreeActions.mouseDownHandler, TreeActions));
                                }
                            }, "dataTree");
                            tree.onLoadDeferred.then(function() {
                                // send event to be able to hook into this phase
                                topic.publish("/onPageInitialized", "Hiearchy");

                                pageHierachy.dataTreePromise.resolve();
                            });
                            TreeActions.createTreeMenu();
                            igeActions.dataTree = tree;
                            contentBorderContainer.startup();
                            registry.byId("contentContainer").resize();
                        });

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
                                break;
                            case "conformity":
                                localizedTitle = "<fmt:message key='dialog.conformity.title' />";
                                break;
                        }
                        return localizedTitle;
                    }

                    /**
                     * PUBLIC METHODS
                     */
                    pageDashboard.getLocalizedTitle = getLocalizedTitle;

                });
        </script>
    </head>
    <body>
        <div id="hierarchy">
        <div id="loadingZone" style="float:right; margin-top:0; z-index: 100; visibility:hidden; top: 5px; right:60px; position: absolute;">
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
                                    <label for="objectName" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3000)">
                                        <fmt:message key="ui.obj.header.objectName" />*
                                    </label>
                                </td>
                                <td colspan="2">
                                    <input type="text" id="objectName" required="true" name="objectName" style="width: 100%;font-size:11px !important"/>
                                </td>
                            </tr>
                            <tr>
                                <td id="objectClassLabel" class="label col1">
                                    <label for="objectClass" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1020)">
                                        <fmt:message key="ui.obj.header.objectClass" />*
                                    </label>
                                </td>
                                <td class="col2">
                                    <select autoComplete="false" required="true" style="width: 100%; margin:0;" disabled="disabled" id="objectClass"></select>
                                </td>
                                <td class="col3">
                                    <img id="permissionObjLock" src="img/lock.gif" width="9" height="14" alt="gesperrt" />
                                </td>
                            </tr>
                            <tr>
                                <td id="objectOwnerLabel" class="label">
                                    <label for="objectOwner" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1030)">
                                        <fmt:message key="ui.obj.header.responsibleUser" />*
                                    </label>
                                </td>
                                <td>
                                    <div autoComplete="false" required="true" style="width: 100%; margin:0;" id="objectOwner"></div>
                                </td>
                                <td class="bgBlue" style="color:#FFFFFF; ">
                                    <strong><fmt:message key="ui.obj.header.workState" />:</strong><span id="workState" style="margin-left:3px;"></span>
                                </td>
                            </tr>
                            <tr>
                                <td class="note bgBlue" colspan="3">
                                    <strong><fmt:message key="ui.obj.header.creationTime" />:</strong><span id="creationTime">26.06.1998</span>
                                    | <strong><fmt:message key="ui.obj.header.modificationTime" />:</strong><span id="modificationTime">27.09.2000</span>
                                    <span id="publicationTimeStatus" class="hide">| <strong><fmt:message key="ui.obj.header.willBePublishedOn" />:</strong><span id="publicationTime">01.01.1970</span></span>
                                    | <strong><fmt:message key="ui.obj.header.uuid" />:</strong><span id="uuid" class="oneClickMark">XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX</span>
                                    <span id="origIdSpan" title="<fmt:message key="ui.obj.header.orgObjId.tooltip" />">| <strong><fmt:message key="ui.obj.header.orgObjId" />:</strong><span id="orgObjId" class="oneClickMark">XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX</span></span>
                                    <br><strong><fmt:message key="ui.obj.header.modUser" />:</strong><span id="lastEditor">---</span>
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('general');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7011)">
                                <fmt:message key="ui.obj.general.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="generalContent" class="content">
                            <span id="uiElement1001" class="outer optional">
                                <div><span class="label">
                                    <label for="parentIdentifier" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1001)">
                                        <fmt:message key="ui.obj.general.parentIdentifier" />
                                    </label>
                                </span><span class="input">
                                    <div id="parentIdentifier"></div>
                                </span></div>
                            </span>
                            <span id="uiElement5000" class="outer">
                                <div><span class="label">
                                    <label for="generalShortDesc" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5000)">
                                        <fmt:message key="ui.obj.general.shortDescription" />
                                    </label>
                                </span><span class="input">
                                    <div id="generalShortDesc"></div>
                                </span></div>
                            </span>
                            <span id="uiElement5100" class="outer optional">
                                <div><span class="label">
                                    <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5100)">
                                        <fmt:message key="ui.obj.general.previewImage" />
                                    </label>
                                </span><span class="input">
                                    <div id="generalPreviewImage"></div>
                                </span></div>
                            </span>
                            <span id="uiElement5105" class="outer optional">
                                <div><span class="label">
                                    <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5105)">
                                        <fmt:message key="ui.obj.general.previewImageDescription" />
                                    </label>
                                </span><span class="input">
                                    <div id="previewImageDescription">
                                    </div>
                                </span></div>
                            </span>
                            <span id="uiElement1010" class="outer">
                            	<div><span id="generalDescLabel" class="label">
                                    <label for="generalDesc" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1010)">
                                        <fmt:message key="ui.obj.general.description" />
                                    </label>
                                </span>
								<span class="input"><input type="text" id="generalDesc" name="generalDesc" /></span>
								</div>
                            </span>
                            <span id="uiElement1000" class="outer">
                            	<div><span id="generalAddressTableLabel" class="label left">
                                    <label for="generalAddressTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1000)">
                                        <span id="generalAddressTableLabelText"><fmt:message key="ui.obj.general.addressTable.title" /></span>
                                    </label>
                                </span>
                                    <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" />
                                        <a id="generalAddressTableLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('generalAddress'), 'dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 585, true, {grid: 'generalAddress'});" title="<fmt:message key="dialog.popup.addressTable.link" /> [Popup]"><fmt:message key="ui.obj.general.addressTable.link" /></a>
                                    </span>
                                <div id="generalAddressTable" class="input tableContainer clear">
                                    <div id="generalAddress" autoHeight="4" contextMenu="GENERAL_ADDRESS" class="hideTableHeader" interactive="true">
                                    </div>
                                </div></div>
                            </span>
                            <span id="uiElement6000" class="outer checkboxContainer">
                                <div class="input checkboxContainer input-inline">
                                    <input type="checkbox" id="isInspireRelevant"  title=""/><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 6000)"><fmt:message key="ui.obj.general.inspireRelevant" /></label>
                                </div>
                                <span id="uiElement6001" class="hidden">
                                    <div class="input checkboxContainer input-inline">
                                        <input type="radio" id="isInspireConform"/><label for="isInspireConform" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 6001)"><fmt:message key="ui.obj.general.inspireConform" /></label>
                                    </div>
                                    <div class="input checkboxContainer input-inline">
                                        <input type="radio" id="notInspireConform" /><label for="notInspireConform" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 6002)"><fmt:message key="ui.obj.general.notInspireConform" /></label>
                                    </div>
                                </span>
                            </span>
                            <span id="uiElement6005" class="outer hidden">
                                <div class="input checkboxContainer">
                                    <input type="checkbox" id="isAdvCompatible"  title=""/><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 6005)"><fmt:message key="ui.obj.general.advCompatible" /></label>
                                </div>
                            </span>
                            <span id="uiElement6010" class="outer halfWidth">
                                <div class="input checkboxContainer">
                                    <input type="checkbox" id="isOpenData" title="" /><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 6010)"><fmt:message key="ui.obj.general.openData" /></label>
                                </div>
                            </span>
                            <span id="uiElement6020" class="outer halfWidth">
                                <div><span id="categoriesOpenDataLabel" class="label">
                                    <label for="categoriesOpenData" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 6020)">
                                        <fmt:message key="ui.obj.general.categoriesOpenData" />
                                    </label>
                                </span>
                                <div class="input tableContainer">
                                    <div id="categoriesOpenData" autoHeight="4" interactive="true" class="hideTableHeader">
                                    </div>
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('thesaurus');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7005)">
                                <fmt:message key="ui.obj.thesaurus.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="thesaurusContent" class="content">
                            <div class="inputContainer">
                                <span id="uiElement5170" class="outer optional hidden">
                                    <div>
                                        <span id="advProductGroupLabel" class="label">
                                            <label for="advProductGroup" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5170);" >
                                                <fmt:message key="ui.obj.thesaurus.advProductGroup" />
                                            </label>
                                        </span>
                                        <div class="input tableContainer">
                                            <div id="advProductGroup" autoHeight="4" interactive="true" class="hideTableHeader">
                                            </div>
                                        </div>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement5064" class="outer">
                                    <div>
                                        <span id="ref1ThesaurusInspireLabel" class="label">
                                            <label for="thesaurusInspire" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5064);" >
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
                                            <label for="thesaurusTopics" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5060)">
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
                                        <span id="thesaurusTermsLabel" class="label left">
                                        <label for="thesaurusTerms" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1420)">
                                            <fmt:message key="ui.obj.thesaurus.terms" />
                                        </label>
                                        </span>
                                    <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="thesaurusTermsLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('thesaurusTerms'), 'dialogs/mdek_thesaurus_assist_dialog.jsp?c='+userLocale, 735, 430, true);" title="<fmt:message key='dialog.popup.thesaurus.terms.link.assistant' /> [Popup]"><fmt:message key="ui.obj.thesaurus.terms.link.assistant" /></a>
                                        <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="thesaurusTermsNavigatorLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('thesaurusNavigator'), 'dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {dstTable: 'thesaurusTerms'});" title="<fmt:message key="dialog.popup.thesaurus.terms.link.navigator" /> [Popup]"><fmt:message key="ui.obj.thesaurus.terms.link.navigator" /></a></span>
                                    <div class="input tableContainer clear">
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
                                            <label for="thesaurusFreeTerms" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1410)">
                                                <fmt:message key="ui.obj.thesaurus.terms.add.label" />
                                            </label>
                                        </span>
                                        <div class="input">
                                            <input type="text" id="thesaurusFreeTerms" />
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
                                        <label for="thesaurusEnvironment" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10014)">
                                            <fmt:message key="ui.obj.thesaurus.terms.enviromental.title" />
                                        </label>
                                    </span>
                                    <div id="thesaurusEnvironment" class="outlined">
                                        <div class="checkboxContainer">
                                            <span class="input">
                                                <input type="checkbox" name="thesaurusEnvExtRes" id="thesaurusEnvExtRes" title="" /><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7006)">
                                                    <fmt:message key="ui.obj.thesaurus.terms.enviromental.displayCatalogPage" />
                                                </label>
                                            </span>
                                        </div>
                                            <span id="uiElementN015" class="outer headHiddenRows4">
                                                <span id="thesaurusEnvTopicsLabel" class="label">
                                                    <label for="thesaurusEnvTopics" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10015)">
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('refClass1');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7000)">
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
                                        <label for="ref1BasisTabContainer" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3520)">
                                            <fmt:message key="ui.obj.type1.technicalBasisTable.title" />
                                        </label>
                                    </span>
                                    <span id="ref1BasisTab2Header" class="functionalLink onTab marginRight" style="display: none;">
                                        <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddBasisLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref1Basis'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3520, gridId: 'ref1BasisLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type1.technicalBasisTable.link" /></a>
                                    </span>
                                    <div id="ref1BasisTabContainer" selectedChild="ref1BasisTab1">
                                        <div id='ref1BasisTab1' class="input" label="<fmt:message key="ui.obj.type1.technicalBasisTable.tab.text" />">
                                            <textarea id="ref1BasisText" name="ref1BasisText" title=""></textarea>
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
                                    <label for="ref1ObjectIdentifier" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10021)">
                                        <fmt:message key="ui.obj.type1.identifier" />
                                    </label>
                                </span>
								<span class="input">
									<input type="text" id="ref1ObjectIdentifier" />
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
	                                            <label for="ref1DataSet" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5061)">
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
                                    <label for="ref1Representation" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5062)">
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
                                        <label for="ref1VFormatFields" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7014)">
                                            <fmt:message key="ui.obj.type1.vectorFormat.title" />
                                        </label>
                                    </span>
                                    <div id="ref1VFormatFields" class="outlined">
                                        <span id="uiElement5063" class="outer" style="width:33%;">
									     <div>
                                            <span class="label">
                                                <label for="ref1VFormatTopology" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5063)">
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
                                                <label for="ref1VFormatDetails" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10001)">
                                                    weitere Angaben
                                                </label>
                                            </span>
                                            <div class="input tableContainer">
                                                <div id="ref1VFormatDetails" autoHeight="4" interactive="true">
                                                </div>
                                            </div></div>
                                        </span>
									    <div class="clear"></div>
                                    </div>
                                </span>
                            </div>
                            <div id="ref1GridFormat" class="inputContainer optional hide" style="padding:5px;">
                                <span id="uiElementN030">
                                    <span id="ref1GridFormatLabel" class="label">
                                        <label for="ref1GridFormatFields" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5300)">
                                            <fmt:message key="ui.obj.type1.gridFormat.title" />
                                        </label>
                                    </span>
                                    <div id="ref1GridFormatFields" class="outlined">
                                        <span id="uiElement5301" class="outer outer halfWidth">
                                            <div>
                                                <span class="label"></span>
                                                <span class="input checkboxContainer input-inline">
                                                    <input id="ref1TransfParamAvail" />
                                                    <label for="ref1TransfParamAvail" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5301)">
                                                        <fmt:message key="ui.obj.type1.gridFormat.transfParamAvail" />
                                                    </label>
                                                </span>
                                            </div>
                                        </span>
                                        <span id="uiElement5302" class="outer outer halfWidth">
                                            <div>
                                                <span class="label">
                                                    <label for="ref1NumDimensions" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5302)">
                                                        <fmt:message key="ui.obj.type1.gridFormat.numDimensions" />
                                                    </label>
                                                </span>
                                                <span class="input">
                                                    <input id="ref1NumDimensions" />
                                                </span>
                                            </div>
                                        </span>
                                        
                                        <span id="uiElement5303" class="outer" style="width:25%;">
                                            <div>
                                                <span class="label">
                                                    <label for="ref1AxisDimName" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5303)">
                                                        <fmt:message key="ui.obj.type1.gridFormat.axisDimName" />
                                                    </label>
                                                </span>
                                                <span class="input">
                                                    <input id="ref1AxisDimName" />
                                                </span>
                                            </div>
                                        </span>
                                        <span id="uiElement5304" class="outer" style="width:25%;">
                                            <div>
                                                <span class="label">
                                                    <label for="ref1AxisDimSize" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5304)">
                                                        <fmt:message key="ui.obj.type1.gridFormat.axisDimSize" />
                                                    </label>
                                                </span>
                                                <span class="input">
                                                    <input id="ref1AxisDimSize" />
                                                </span>
                                            </div>
                                        </span>
                                        <span id="uiElement5305" class="outer outer halfWidth">
                                            <div>
                                                <span class="label">
                                                    <label for="ref1CellGeometry" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5305)">
                                                        <fmt:message key="ui.obj.type1.gridFormat.cellGeometry" />
                                                    </label>
                                                </span>
                                                <span class="input">
                                                    <input id="ref1CellGeometry" />
                                                </span>
                                            </div>
                                        </span>
                                        
                                        <hr>

                                        <span id="uiElement5306" class="outer">
                                            <div class="input checkboxContainer input-inline">
                                                <input type="radio" id="isGeoRectified"/><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5306)" for="isGeoRectified"><fmt:message key="ui.obj.type1.gridFormat.geoRectified" /></label>
                                            </div>
                                            <div class="input checkboxContainer input-inline">
                                                <input type="radio" id="isGeoReferenced" /><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5307)" for="isGeoReferenced"><fmt:message key="ui.obj.type1.gridFormat.geoReferenced" /></label>
                                            </div>
                                        </span>

                                        <div id="geoRectifiedWrapper">
                                            <span id="uiElement5308" class="outer halfWidth">
                                                <div>
                                                    <span class="label"></span>
                                                    <span class="input checkboxContainer input-inline">
                                                        <input id="ref1GridFormatRectCheckpoint" />
                                                        <label for="ref1GridFormatRectCheckpoint" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5308)">
                                                            <fmt:message key="ui.obj.type1.gridFormat.rectified.checkpoint" />
                                                        </label>
                                                    </span>
                                                </div>
                                            </span>
                                            <span id="uiElement5309" class="outer halfWidth">
                                                <div>
                                                    <span class="label">
                                                        <label for="ref1GridFormatRectDescription" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5309)">
                                                            <fmt:message key="ui.obj.type1.gridFormat.rectified.description" />
                                                        </label>
                                                    </span>
                                                    <span class="input">
                                                        <input id="ref1GridFormatRectDescription" />
                                                    </span>
                                                </div>
                                            </span>
                                            <span id="uiElement5310" class="outer halfWidth">
                                                <div>
                                                    <span class="label">
                                                        <label for="ref1GridFormatRectCornerPoint" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5310)">
                                                            <fmt:message key="ui.obj.type1.gridFormat.rectified.cornerPoint" />
                                                        </label>
                                                    </span>
                                                    <span class="input">
                                                        <input id="ref1GridFormatRectCornerPoint" />
                                                    </span>
                                                </div>
                                            </span>
                                            <span id="uiElement5311" class="outer halfWidth">
                                                <div>
                                                    <span class="label">
                                                        <label for="ref1GridFormatRectPointInPixel" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5311)">
                                                            <fmt:message key="ui.obj.type1.gridFormat.rectified.pointInPixel" />
                                                        </label>
                                                    </span>
                                                    <span class="input">
                                                        <input style="width:100%;" id="ref1GridFormatRectPointInPixel" />
                                                    </span>
                                                </div>
                                            </span>
                                        </div>

                                        <div id="geoReferencedWrapper" class="hide">
                                            <span id="uiElement5312" class="outer halfWidth">
                                                <div>
                                                    <!-- <span class="label"></span> -->
                                                    <span class="input checkboxContainer input-inline">
                                                        <input id="ref1GridFormatRefOrientationParam" />
                                                        <label for="ref1GridFormatRefOrientationParam" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5312)">
                                                            <fmt:message key="ui.obj.type1.gridFormat.referenced.orientationParam" />
                                                        </label>
                                                    </span>
                                                </div>
                                            </span>
                                            <span id="uiElement5313" class="outer halfWidth">
                                                <div>
                                                    <!-- <span class="label"></span> -->
                                                    <span class="input checkboxContainer input-inline">
                                                        <input id="ref1GridFormatRefControlpoint" />
                                                        <label for="ref1GridFormatRefControlpoint" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5313)">
                                                            <fmt:message key="ui.obj.type1.gridFormat.referenced.controlPoint" />
                                                        </label>
                                                    </span>
                                                </div>
                                            </span>
                                            <span id="uiElement5314" class="outer">
                                                <div>
                                                    <span class="label">
                                                        <label for="ref1GridFormatRefGeoreferencedParam" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5314)">
                                                            <fmt:message key="ui.obj.type1.gridFormat.referenced.georeferencedParam" />
                                                        </label>
                                                    </span>
                                                    <span class="input">
                                                        <input id="ref1GridFormatRefGeoreferencedParam" />
                                                    </span>
                                                </div>
                                            </span>
                                        </div>

                                        <div style="clear: both; padding: 5px 0;"></div>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElementN002" class="outer">
                                	<div><span class="label">
                                        <label for="ref1Scale" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3525)">
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
                                        <label for="ref1SymbolsTabContainer" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3555)">
                                            <fmt:message key="ui.obj.type1.symbolCatTable.title" />
                                        </label>
                                    </span><span id="ref1SymbolsTab2Header" class="functionalLink onTab marginRight" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddSymbolsLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref1Symbols'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3555, gridId: 'ref1SymbolsLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type1.symbolCatTable.link" /></a></span>
                                    <div id="ref1SymbolsTabContainer" selectedChild="ref1SymbolsTab1">
                                        <div id='ref1SymbolsTab1' label="<fmt:message key="ui.obj.type1.symbolCatTable.tab.text" />">
                                            <!-- Need to use document.write here since the titel 'label' is set directly in the div
                                            <div id="ref1SymbolsTab1" data-dojo-type="ContentPane" label="Text">
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
                                        <label for="ref1KeysTabContainer" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3535)">
                                            <fmt:message key="ui.obj.type1.keyCatTable.title" />
                                        </label>
                                    </span><span id="ref1KeysTab2Header" class="functionalLink onTab marginRight" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddKeysLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref1Keys'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3535, gridId: 'ref1KeysLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type1.keyCatTable.link" /></a></span>
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
                                        <label for="ref1Data" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5070)">
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
                                        <label for="ref1ServiceLink" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5066)">
                                            <fmt:message key="ui.obj.type1.serviceLink.title" />
                                        </label>
                                    </span>
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
                                        <label for="ref1DataBasisTabContainer" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3570)">
                                            <fmt:message key="ui.obj.type1.dataBasisTable.title" />
                                        </label>
                                    </span><span id="ref1DataBasisTab2Header" class="functionalLink onTab marginRight" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref1AddDataBasisLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref1DataBasis'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3570, gridId: 'ref1DataBasisLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type1.dataBasisTable.link" /></a></span>
                                    <div id="ref1DataBasisTabContainer" selectedChild="ref1DataBasisTab1">
                                        <div id='ref1DataBasisTab1' class="input" label="<fmt:message key="ui.obj.type1.dataBasisTable.tab.text" />">
                                            <input type="text" id="ref1DataBasisText" name="ref1DataBasisText"  title=""/>
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
                                        <label for="ref1ProcessTabContainerSpace" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3515)">
                                            <fmt:message key="ui.obj.type1.processTable.title" />
                                        </label>
                                    </span><span id="ref1ProcessTab2Header" class="functionalLink onTab marginRight" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" />
    									<a id="ref1AddProcessLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref1Process'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3515, gridId: 'ref1ProcessLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type1.processTable.link" /></a>
									</span>
                                    <div id="ref1ProcessTabContainerSpace" selectedChild="ref1ProcessTab1">
                                        <!--<div id='ref1ProcessTab1' label="<fmt:message key="ui.obj.type1.processTable.tab.text" />";-->
                                            <span class="input"><input type="text" id="ref1ProcessText" name="ref1ProcessText" class="" title="" /></span>
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('refClass1DQ');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7500)">
                                <fmt:message key="ui.obj.dq" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                            <div class="inputContainer">
                                <span id="uiElement3565" class="outer halfWidth">
                                    <div><span class="label">
                                        <label for="ref1Coverage" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3565)">
                                            <fmt:message key="ui.obj.type1.coverage" />
                                        </label>
                                    </span>
                                    <span class="input">
                                        <input type="text" id="ref1Coverage" min="0" max="100" name="ref1Coverage" class="" decimal="," /> %</span>
                                    </div>
                                </span>
                                <span id="uiElement5071" class="outer halfWidth optional">
                                    <div><span class="label">
                                        <label for="ref1GridPosAccuracy" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5071)">
                                            <fmt:message key="ui.obj.type1.gridPosAccuracy" />
                                        </label>
                                    </span><span class="input"><input type="text" id="ref1GridPosAccuracy" name="ref1GridPosAccuracy" class="" decimal="," /></span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement5069" class="outer halfWidth">
                                    <div><span class="label">
                                        <label for="ref1AltAccuracy" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5069)">
                                            <fmt:message key="ui.obj.type1.sizeAccuracy" />
                                        </label>
                                    </span><span class="input"><input type="text" id="ref1AltAccuracy" name="ref1AltAccuracy" class="" decimal="," /></span>
                                    </div>
                                </span>
                                <span id="uiElement3530" class="outer halfWidth">
                                    <div><span class="label">
                                        <label for="ref1PosAccuracy" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3530)">
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
                                    <label for="dq109Table" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7509)">
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
                                    <label for="dq112Table" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7512)">
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
                                    <label for="dq113Table" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7513)">
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
                                    <label for="dq114Table" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7514)">
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
                                    <label for="dq115Table" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7515)">
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
                                    <label for="dq120Table" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7520)">
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
                                    <label for="dq125Table" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7525)">
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
                                    <label for="dq126Table" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7526)">
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
                                    <label for="dq127Table" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7527)">
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('refClass2');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7000)">
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
                                            <label for="ref2Author" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3355)">
                                                <fmt:message key="ui.obj.type2.author" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="ref2Author" name="ref2Author" />
                                        </span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3350" class="outer">
                                    <div>
                                        <span class="label">
                                            <label for="ref2Publisher" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3350)">
                                                <fmt:message key="ui.obj.type2.editor" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="ref2Publisher" name="ref2Publisher" />
                                        </span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement3340" class="outer halfWidth">
                                    <div>
                                        <span class="label">
                                            <label for="ref2PublishedIn" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3340)">
                                                <fmt:message key="ui.obj.type2.publishedIn" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="ref2PublishedIn" name="ref2PublishedIn" class="w320" />
                                        </span>
                                    </div>
                                </span>
                                <span id="uiElement3310" class="outer halfWidth">
                                    <div>
                                        <span class="label">
                                            <label for="ref2PublishLocation" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3310)">
                                                <fmt:message key="ui.obj.type2.publishedLocation" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="ref2PublishLocation" name="ref2PublishLocation" class="w320" />
                                        </span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span class="outer halfWidth">
                                    <span id="uiElement3330" class="outer" style="width:33%;">
                                        <div>
                                        <span class="label">
                                            <label for="ref2PublishedInIssue" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3330)">
                                                <fmt:message key="ui.obj.type2.issue" />
                                            </label>
                                        </span>
                                        <span class="input spaceBelow">
                                            <input type="text" id="ref2PublishedInIssue" name="ref2PublishedInIssue" class="" />
                                        </span>
										</div>
                                    </span>
                                    <span id="uiElement3320" class="outer" style="width:33%;">
                                        <div>
                                        <span class="label">
                                            <label for="ref2PublishedInPages" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3320)">
                                                <fmt:message key="ui.obj.type2.pages" />
                                            </label>
                                        </span>
                                        <span class="input spaceBelow">
                                            <input type="text" id="ref2PublishedInPages" name="ref2PublishedInPages" class="" />
                                        </span>
										</div>
                                    </span>
                                    <span id="uiElement3300" class="outer" style="width:34%;">
									    <div>
                                        <span class="label">
                                            <label for="ref2PublishedInYear" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3300)">
                                                <fmt:message key="ui.obj.type2.publishedYear" />
                                            </label>
                                        </span>
                                        <span class="input spaceBelow">
                                            <input type="text" id="ref2PublishedInYear" name="ref2PublishedInYear" class="" />
                                        </span>
										</div>
                                    </span>
                                    <span id="uiElement3365" class="outer" style="clear:both;">
									    <div>
                                        <span class="label">
                                            <label for="ref2PublishedISBN" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3365)">
                                                <fmt:message key="ui.obj.type2.isbn" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="ref2PublishedISBN" name="ref2PublishedISBN" class="w148" />
                                        </span>
										</div>
                                    </span>
                                    <span id="uiElement3370" class="outer">
                                    	<div>
                                        <span class="label">
                                            <label for="ref2PublishedPublisher" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3370)">
                                                <fmt:message key="ui.obj.type2.publisher" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="ref2PublishedPublisher" name="ref2PublishedPublisher" class="w148" />
                                        </span>
										</div>
                                    </span>
                                </span>
                                <span id="uiElement3360" class="outer halfWidth">
                                    <div><span class="label">
                                        <label for="ref2LocationTabContainer" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3360)">
                                            <fmt:message key="ui.obj.type2.locationTable.title" />
                                        </label>
                                    </span>
                                    <span id="ref2LocationTab2Header" class="functionalLink onTab marginRight" style="display: none;">
                                        <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref2AddLocationLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref2Location'), 'dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 580, true, {linkType: 3360, grid: 'ref2LocationLink'});" title="<fmt:message key="dialog.popup.addressTable.link" /> [Popup]"><fmt:message key="ui.obj.type2.locationTable.link" /></a>
                                    </span>
                                    <div id="ref2LocationTabContainer" class="" selectedChild="ref2LocationTab1">
                                        <div id='ref2LocationTab1' label="<fmt:message key="ui.obj.type2.locationTable.tab.text" />">
                                                <input type="text" id="ref2LocationText" name="ref2LocationText" title="" />
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
                                            <label for="ref2DocumentType" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3385)">
                                                <fmt:message key="ui.obj.type2.documentType" />
                                            </label>
                                        </span>
                                        <span class="input spaceBelow">
                                            <div style="width:100%;" id="ref2DocumentType" listId="3385">
                                            </div>
                                        </span>
                                    </div>
                                </span>
                                <span id="uiElement3210" class="outer halfWidth">
	                                <div>
		                                <span class="label">
		                                    <label for="ref2BaseDataTabContainer" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3210)">
		                                        <fmt:message key="ui.obj.type2.generalDataTable.title" />
		                                    </label>
		                                </span>
		                                <span id="ref2BaseDataTab2Header" class="functionalLink onTab marginRight" style="display: none;">
		                                    <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref2AddBaseDataLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref2BaseData'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3345, gridId: 'ref2BaseDataLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type2.generalDataTable.link" /></a>
		                                </span>
		                                <div id="ref2BaseDataTabContainer" selectedChild="ref2BaseDataTab1">
		                                    <div id='ref2BaseDataTab1' label="<fmt:message key="ui.obj.type2.generalDataTable.tab.text" />">
		                                            <input type="text" id="ref2BaseDataText" name="ref2BaseDataText"  title=""/>
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
	                                        <label for="ref2BibData" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3380)">
	                                            <fmt:message key="ui.obj.type2.additionalBibInfo" />
	                                        </label>
	                                    </span>
	                                    <span class="input">
	                                        <input type="text" id="ref2BibData" name="ref2BibData" class="w320 h038" />
	                                    </span>
	                                </div>
	                            </span>
	                            <span id="uiElement3375" class="outer halfWidth">
	                                <div>
	                                    <span class="label">
	                                        <label for="ref2Explanation" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3375)">
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('refClass3');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7000)">
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
                                        <label for="ref3ServiceTypeTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10022)">
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
                                            <label for="ref3ServiceType" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3220)">
                                                <fmt:message key="ui.obj.type3.serviceType" />
                                            </label>
                                        </span>
										<span class="input spaceBelow">
											<div autoComplete="false" listId="5100" id="ref3ServiceType" style="width: 100%;"></div>
                                        </span>
									</div>
                                </span>
                                <span id="uiElement3225" class="optional">
                                    <div class="input checkboxContainer" style="padding-left: 2px;">
                                        <input type="checkbox" id="ref3IsAtomDownload" title=""/><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3225)"><fmt:message key="ui.obj.type3.serviceAtomDownload" /></label>
                                    </div>
                                </span>
                              </div> 
                              </span>
                                <span id="uiElement3230" class="outer halfWidth">
                                   	<div><span class="label">
                                           <label for="ref3ServiceVersion" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3230)">
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
                                    <div><span class="label left">
                                        <label for="ref3Operation" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7015)">
                                            <fmt:message key="ui.obj.type3.operationTable.title" />
                                        </label>
                                    </span><span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="#" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref3Operation'), 'dialogs/mdek_operation_dialog.jsp?c='+userLocale, 735, 745, true, {gridId:'ref3Operation'});" title="<fmt:message key="dialog.popup.operationTable.link" /> [Popup]"><fmt:message key="ui.obj.type3.operationTable.link" /></a></span>
                                    <div class="input tableContainer clear">
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
                                        <label for="ref3Scale" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3525)">
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
                                        <label for="ref3SystemEnv" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3200)">
                                            <fmt:message key="ui.obj.type3.environment" />
                                        </label>
                                    </span><span class="input"><input type="text" id="ref3SystemEnv" name="ref3SystemEnv" /></span>
									</div>
								</span>
                                <span id="uiElement3240" class="outer halfWidth">
                                	<div><span class="label">
                                        <label for="ref3History" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3240)">
                                            <fmt:message key="ui.obj.type3.history" />
                                        </label>
                                    </span><span class="input"><input type="text" id="ref3History" name="ref3History" class="w320 h038" /></span>
									</div>
								</span>
								<span id="uiElement3250" class="outer halfWidth">
                                    <div><span class="label">
                                        <label for="ref3Explanation" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3250)">
                                            <fmt:message key="ui.obj.type3.description" />
                                        </label>
                                        </span><span class="input"><input type="text" id="ref3Explanation" name="ref3Explanation" /></span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement3345" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="ref3BaseDataTabContainer" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3345)">
                                                <fmt:message key="ui.obj.type3.generalDataTable.title" />
                                            </label>
                                        </span>
                                        <!--<span id="ref3MethodTab2Header" class="functionalLink onTab" style=""><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref3AddBaseDataLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(getLocalizedTitle('ref3BaseData'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3210, gridId: 'ref3BaseDataLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type3.generalDataTable.link" /></a></span>
                                        -->
                                        <div id='ref3MethodTab2' label="<fmt:message key="ui.obj.type3.generalDataTable.tab.links" />">
                                            <div class="input tableContainer">
                                                <div id="ref3BaseDataLink" autoHeight="4" query="{relationType:'3600'}" class="hideTableHeader" relation_filter="3600">
                                                </div>
                                            </div>
                                        </div>
                                        <div id="ref3BaseDataTabContainer" class="h088" selectedChild="ref3BaseDataTab1">
                                            <div id='ref3BaseDataTab1' label="<fmt:message key="ui.obj.type3.generalDataTable.tab.text" />">
                                            <input type="text" id="ref3BaseDataText" name="ref3BaseDataText" title="" />
                                        </div>
                                        </div>
										</div>
										<div style="position:relative; float:right;"><button id="btnAddDataLink"><fmt:message key="ui.obj.type1.buttonAddDataLink" /></button></div>
                                    </span>
                                    <span id="uiElement3221" class="outer halfWidth">
                                        <div>
                                            <span id="ref3CouplingTypeLabel" class="label"> 
                                                <label for="ref3CouplingType" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3221)">
                                                    <fmt:message key="ui.obj.type3.couplingType" />
                                                </label> 
                                            </span>
                                            <span class="input">
                                                <div autoComplete="false" id="ref3CouplingType" style="width: 100%;"></div>
                                            </span>
                                        </div> 
                                    </span>
                            </div>
                            <div class="inputContainer">
                            <span id="uiElement3260">
                                <span class="checkboxContainer outer"><div>
                                    <span class="input checkboxContainer">
                                        <input type="checkbox" id="ref3HasAccessConstraint" title="" /><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3260)">
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('refClass4');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7000)">
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
                                            <label for="ref4ParticipantsTabContainer" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3410)">
                                                <fmt:message key="ui.obj.type4.participantsTable.title" />
                                            </label>
                                        </span><span id="ref4ParticipantsTab2Header" class="functionalLink onTab marginRightColumn" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref4AddParticipantsLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref4Participants'), 'dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 580, true, {linkType: 3410, grid: 'ref4ParticipantsLink'});" title="<fmt:message key="dialog.popup.addressTable.link" /> [Popup]"><fmt:message key="ui.obj.type4.participantsTable.link" /></a></span>
                                        <div id="ref4ParticipantsTabContainer" selectedChild="ref4ParticipantsTab1">
                                            <div id='ref4ParticipantsTab1' label="<fmt:message key="ui.obj.type4.participantsTable.tab.text" />">
                                                <input type="text" id="ref4ParticipantsText" name="ref4ParticipantsText" title="" />
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
                                            <label for="ref4PMTabContainer" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3400)">
                                                <fmt:message key="ui.obj.type4.projectManagerTable.title" />
                                            </label>
                                        </span><span id="ref4PMTab2Header" class="functionalLink onTab marginRight" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref4AddPMLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref4PM'), 'dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 580, true, {linkType: 3400, grid: 'ref4PMLink'});" title="<fmt:message key="dialog.popup.addressTable.link" /> [Popup]"><fmt:message key="ui.obj.type4.projectManagerTable.link" /></a></span>
                                        <div id="ref4PMTabContainer" class="h088" selectedChild="ref4PMTab1">
                                            <div id='ref4PMTab1' label="<fmt:message key="ui.obj.type4.projectManagerTable.tab.text" />">
                                                <input type="text" id="ref4PMText" name="ref4PMText" title="" />
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
                                        <label for="ref4Explanation" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3420)">
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('refClass5');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7000)">
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
                                        <label for="ref5KeysTabContainer" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3109)">
                                            <fmt:message key="ui.obj.type5.keyCatTable.title" />
                                        </label>
                                    </span><span id="ref5KeysTab2Header" class="functionalLink onTab marginRight" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref5AddKeysLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref5Keys'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3109, gridId: 'ref5KeysLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type5.keyCatTable.link" /></a></span>
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
                                        <label for="ref5dbContent" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3110)">
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
                                            <label for="ref5MethodTabContainer" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3100)">
                                                <fmt:message key="ui.obj.type5.methodTable.title" />
                                            </label>
                                       </span><span id="ref5MethodTab2Header" class="functionalLink onTab marginRightColumn" style="display: none;"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref5AddMethodLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref5Method'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3100, gridId: 'ref5MethodLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.type5.methodTable.link" /></a></span>
                                        <div id="ref5MethodTabContainer" selectedChild="ref5MethodTab1">
                                            <div id='ref5MethodTab1' label="<fmt:message key="ui.obj.type5.methodTable.tab.text" />">
                                                <span class="input"><input type="text" id="ref5MethodText" name="ref5MethodText" title="" /></span>
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
                                            <label for="ref5Explanation" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3120)">
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('refClass6');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7000)">
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
                                            <label for="ref6ServiceType" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3620)">
                                                <fmt:message key="ui.obj.type6.serviceType" />
                                            </label>
                                        </span>
                                        <span class="input spaceBelow">
                                            <div autoComplete="false" style="width:100%;" listId="5300" id="ref6ServiceType">
                                            </div>
                                        </span>
                                        </div>
                                    </span>
                                    <span id="uiElement3630" class="outer halfWidth">
                                        <div>
                                        <span class="label">
                                            <label for="ref6ServiceVersion" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3630)">
                                                <fmt:message key="ui.obj.type6.serviceVersion" />
                                            </label>
                                        </span>
                                        <div class="input tableContainer">
                                            <div id="ref6ServiceVersion" autoHeight="4" interactive="true" class="hideTableHeader"></div>
                                        </div>
                                        </div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement3600" class="outer halfWidth">
                                        <div>
                                        <span class="label">
                                            <label for="ref6SystemEnv" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3600)">
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
                                            <label for="ref6History" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3640)">
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
                                            <label for="ref6BaseDataTabContainer" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3645)">
                                                <fmt:message key="ui.obj.type6.generalDataTable.title" />
                                            </label>
                                        </span>
                                        <span id="ref6MethodTab2Header" class="functionalLink onTab marginRightColumn" style="display: none;">
                                            <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="ref6AddBaseDataLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('ref6BaseData'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 1010, 680, true, {filter: 3210, gridId: 'ref6BaseDataLink'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.links.linksTo.link" /></a>
                                        </span>
                                        <div id="ref6BaseDataTabContainer" selectedChild="ref6BaseDataTab1">
                                                <input type="text" id="ref6BaseDataText" title="" />
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
                                            <label for="ref6Explanation" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3650)">
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
                                        <label for="ref6UrlList" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3670)">
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('spatialRef');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7001)">
                                <fmt:message key="ui.obj.spatial.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="spatialRefContent" class="content">
                            <div id="spatialRefAdminUnitContainer" class="inputContainer">
                                <span id="uiElementN006" class="outer">
                                	<div><span id="spatialRefAdminUnitLabel" class="label left">
                                        <label for="spatialRefAdminUnit" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10006)">
                                            <fmt:message key="ui.obj.spatial.geoThesTable.title" />
                                        </label>
                                    </span><span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="spatialRefAdminUnitLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('spatialRefAdminUnit'), 'dialogs/mdek_spatial_navigator.jsp?c='+userLocale, 530, 230, true);" title="<fmt:message key="dialog.popup.geoThesTable.link" /> [Popup]"><fmt:message key="ui.obj.spatial.geoThesTable.link" /></a></span>
                                    <div class="input tableContainer clear">
                                        <div id="spatialRefAdminUnit" autoHeight="4">
                                        </div>
                                    </div>
									</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElementN007" class="outer">
                                    <div id="spatialRefCoordsAdminUnit" class="infobox">
                                        <span class="icon" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7012)"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span><span class="title"><a href="javascript:toggleInfo('spatialRefCoordsAdminUnit');" title="<fmt:message key="general.info.open" />"><fmt:message key="ui.obj.spatial.transformedCoordinates" /><img id="spatialRefCoordsAdminUnitToggle" src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a></span>
                                        <div id="spatialRefCoordsAdminUnitContent" style="display:block; height: 44px;">
                                            <span class="outer halfWidth"><div style="margin-left: -6px; margin-top: 3px;">
                                                <div class="input">
                                                    <select id="spatialRefAdminUnitSelect" toggle="plain" style="width:100%; margin:0.01px;" title=""></select>
                                                </div>
                                            </div></span>
                                            <span class="outer halfWidth"><div>
                                                <div class="input">
                                                    <div id="spatialRefAdminUnitCoords" autoHeight="1" allowEmptyRows="true" class="hideTableHeader">
                                                    </div>
                                                </div>
                                            </div></span>
                                        </div>
                                    </div>
                                </span>
                            </div>
                            <div id="spatialRefLocationContainer" class="inputContainer">
                                <span id="uiElementN008" class="outer">
                                	<div><span id="spatialRefLocationLabel" class="label left">
                                        <label for="spatialRefLocation" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10008)">
                                            <fmt:message key="ui.obj.spatial.geoTable.title" />
                                        </label>
                                    </span><span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="spatialRefLocationLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('spatialRefLocation'), 'dialogs/mdek_spatial_assist_dialog.jsp?c='+userLocale, 555, 240, true);" title="<fmt:message key="dialog.popup.geoTable.link" /> [Popup]"><fmt:message key="ui.obj.spatial.geoTable.link" /></a><!-- The following feature is not yet implemented --><!--
                                        <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:void(0);" title="<fmt:message key="dialog.popup.geoSearch.link" /> [Popup]"><fmt:message key="dialog.popup.geoSearch.link" /></a>
                                        --></span>
                                    <span class="functionalLink">
                                        <img src="img/inherit.png" width="10" height="9" alt="inherit" /><a href="#" id="linkGetSpatialRefLocationFromParent" onclick="require('ingrid/IgeEvents').getSpatialRefLocationFromParent();" title="<fmt:message key="ui.obj.inherit.link.tooltip" />"><fmt:message key="ui.obj.inherit.link" /></a>
                                    </span>
                                    <div class="input tableContainer clear">
                                        <div id="spatialRefLocation" interactive="true" autoHeight="4">
                                        </div>
                                    </div>
									</div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElementN009" class="outer">
                                    <div id="spatialRefCoordsLocation" class="infobox">
                                        <span class="icon" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7013)"><img src="img/ic_info.gif" width="16" height="16" alt="Info" /></span><span class="title"><a href="javascript:toggleInfo('spatialRefCoordsLocation');" title="<fmt:message key="general.info.open" />"><fmt:message key="ui.obj.spatial.transformedCoordinates" /><img src="img/ic_info_deflate.gif" width="8" height="8" alt="Pfeil" /></a></span>
                                        <div id="spatialRefCoordsLocationContent" style="display:block; height: 44px;">
                                            <span class="outer halfWidth"><div style="margin-left: -6px; margin-top: 3px;">
                                                <div class="input">
                                                    <select id="spatialRefLocationSelect" toggle="plain" style="width:100%; margin:0.01px;" title=""></select>
                                                </div>
                                                </div>
                                            </span>
                                            <span class="outer halfWidth"><div>
                                                <div class="input">
                                                    <div id="spatialRefLocationCoords" autoHeight="1" allowEmptyRows="true" class="hideTableHeader">
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
                                        <label for="ref1SpatialSystem" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3500)">
                                            <fmt:message key="ui.obj.type1.spatialSystem" />
                                        </label>
                                    </span>
                                    <span class="input">
                                        <div id="ref1SpatialSystem" interactive="true" autoHeight="3" class="hideTableHeader"></div>
                                    </span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElementN010" class="outer halfWidth">
                                    	<div><span id="spatialRefAltHeightLabel" class="label">
                                            <label for="spatialRefAltMin" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4520)">
                                                <fmt:message key="ui.obj.spatial.height" />
                                            </label>
                                        </span>
										<div class="outlined">
                                            	<span id="uiElement1130" class="outer" style="width:33%;">
                                            		<div><span id="spatialRefAltMinLabel" class="label">
                                                        <label for="spatialRefAltMin" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1130)">
                                                            <fmt:message key="ui.obj.spatial.height.min" />
                                                        </label>
                                                    </span><span class="input"><input type="text" id="spatialRefAltMin" name="spatialRefAltMin" /></span>
													</div>
													</span>
													<span id="uiElement5020" class="outer" style="width:33%;">
													<div><span id="spatialRefAltMaxLabel" class="label">
                                                        <label for="spatialRefAltMax" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5020)">
                                                            <fmt:message key="ui.obj.spatial.height.max" />
                                                        </label>
                                                    </span><span class="input"><input type="text" id="spatialRefAltMax" name="spatialRefAltMax" /></span>
													</div></span>
													<span id="uiElement5021" class="outer" style="width:34%;">
													   <div><span id="spatialRefAltMeasureLabel" class="label">
                                                        <label for="spatialRefAltMeasure" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5021)">
                                                            <fmt:message key="ui.obj.spatial.height.unit" />
                                                        </label>
                                                    </span><span class="input"><input listId="102" style="width:100%;" id="spatialRefAltMeasure" /></span>
													</div></span>
													<span id="uiElement5022" class="outer fullWidth">
													   <div><span id="spatialRefAltVDateLabel" class="label">
                                                        <label for="spatialRefAltVDate" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5022)">
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
                                            <label for="spatialRefExplanation" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1140)">
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('timeRef');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7002)">
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
                                            <label for="timeRefTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5030)">
                                                <fmt:message key="ui.obj.time.timeRefTable.title" />
                                            </label>
                                        </span>
                                        <div class="input tableContainer">
                                            <div id="timeRefTable" interactive="true" autoHeight="4" onRowContextMenu="onRowEvent">
                                            </div>
                                        </div>
										</div>
                                    </span>
                                    <span id="uiElement1250" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="timeRefExplanation" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1250)">
                                                <fmt:message key="ui.obj.time.description" />
                                            </label>
                                        </span><span class="input"><input type="text" id="timeRefExplanation" name="timeRefExplanation" class="w320 h105" /></span>
										</div>
									</span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElementN011" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10011)">
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
                                                    <select autoComplete="false" style="width:100%;" id="timeRefType"></select>
                                                </span>
												</div>
											</span>
											<span class="outer" style="width:33%;">
											    <div><span class="label hidden">
                                                    <label for="timeRefDate1">
                                                        Datum 1 [TT.MM.JJJJ]
                                                    </label></span>
                                                <span class="input">
                                                    <div id="timeRefDate1" displayFormat="dd.MM.yyyy"></div>
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
                                                    <div id="timeRefDate2" displayFormat="dd.MM.yyyy"></div>
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
                                            <label for="timeRefStatus" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1220)">
                                                <fmt:message key="ui.obj.time.state" />
                                            </label>
                                        </span><span class="input"><input style="width:100%;" listId="523" id="timeRefStatus" /></span>
										</div>
									</span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement1240" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="timeRefPeriodicity" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1240)">
                                                <fmt:message key="ui.obj.time.periodicity" />
                                            </label>
                                        </span><span class="input"><input style="width:100%;" listId="518" id="timeRefPeriodicity" /></span>
										</div>
									</span>
                                    <span id="uiElement1230" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1230)">
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
	                                            <span class="input" style="display:inline;"><input type="text" id="timeRefIntervalNum" name="timeRefIntervalNum" /></span>
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('extraInfo');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7003)">
                                <fmt:message key="ui.obj.additionalInfo.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="extraInfoContent" class="content">
                            <div class="inputContainer">
                                <span class="outer halfWidth">
                                    <span id="uiElement5041" class="outer fullWidth">
                                    	<div><span id="extraInfoLangMetaDataLabel" class="label">
                                            <label for="extraInfoLangMetaData" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5041)">
                                                <fmt:message key="ui.obj.additionalInfo.language.metadata" />
                                            </label>
                                        </span><span class="input"><input autoComplete="false" style="width:100%;" listId="99999999" id="extraInfoLangMetaData" /></span>
										</div>
									</span>
                                    <span id="uiElement3571" class="outer fullWidth">
                                    	<div><span class="label">
                                            <label id="extraInfoPublishAreaLabel" for="extraInfoPublishArea" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 3571)">
                                                <fmt:message key="ui.obj.additionalInfo.publicationCondition" />
                                            </label>
                                        </span><span class="input"><input autoComplete="false" style="width:100%;" listId="3571" id="extraInfoPublishArea" /></span>
										</div>
									</span>
                               </span>
                                    <span id="uiElement5042" class="outer halfWidth">
                                    	<div><span id="extraInfoLangDataLabel" class="label">
                                            <label for="extraInfoLangData" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5042)">
                                                <fmt:message key="ui.obj.additionalInfo.language.data" />
                                            </label>
                                        </span>
		                                <div class="input tableContainer">
		                                    <div id="extraInfoLangData" autoHeight="3" interactive="true" class="hideTableHeader">
		                                    </div>
		                                </div>
										</div>
									</span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement5043" class="outer halfWidth">
                                        <div>
                                        <span id="extraInfoCharSetDataLabel" class="label">
                                            <label for="extraInfoCharSetData" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5043)">
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
                            <span id="uiElementN024" class="outer">
                                <div><span id="extraInfoConformityTableLabel" class="label left">
                                        <label for="extraInfoConformityTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10024)">
                                            <fmt:message key="ui.obj.additionalInfo.conformityTable.title" />
                                        </label>
                                    </span>
                                    <span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" />
                                        <a id="extraInfoConformityTableLink" href="javascript:void(0);" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('conformity'), 'dialogs/mdek_conformity_dialog.jsp?c='+userLocale, 755, 485, true, {});" title="<fmt:message key="dialog.popup.conformityTable.link" /> [Popup]"><fmt:message key="ui.obj.additionalInfo.conformityTable.link" /></a>
                                    </span>
                                    <div class="input tableContainer clear">
                                        <div id="extraInfoConformityTable" autoHeight="4" interactive="true" contextMenu="EDIT_CONFORMITY">
                                        </div>
                                    </div>
                                </div>
                            </span>
                            <div class="inputContainer">
                                    <span id="uiElementN012" class="outer" style="width:33%;">
									   <div><span class="label">
                                            <label for="extraInfoXMLExportTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1370)">
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
                                            <label for="extraInfoLegalBasicsTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1350)">
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
                                            <label for="extraInfoPurpose" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10013)">
                                                <fmt:message key="ui.obj.additionalInfo.purpose" />
                                            </label>
                                        </span><span class="input"><input type="text" id="extraInfoPurpose" name="extraInfoPurpose" /></span>
										</div>
									</span>
                                    <span id="uiElement5040" class="outer halfWidth">
                                    	<div><span class="label">
                                            <label for="extraInfoUse" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5040)">
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('availability');" title="<fmt:message key="general.open.required.field" />">
                                 <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7004)">
                                <fmt:message key="ui.obj.availability.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="availabilityContent" class="content">
                            <div class="inputContainer">
                                <span id="uiElementN025" class="outer">
                                	<div><span id="availabilityAccessConstraintsLabel" class="label">
                                        <label for="availabilityAccessConstraints" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10025)">
                                            <fmt:message key="ui.obj.availability.accessConstraints" />
                                        </label>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="availabilityAccessConstraints" autoHeight="4" interactive="true" class="hideTableHeader">
                                        </div>
                                    </div>
									</div>
                                </span>
                                <span id="uiElementN027" class="outer required">
                                    <div><span id="availabilityUseAccessConstraintsLabel" class="label">
                                        <label for="availabilityUseAccessConstraints" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10027)">
                                            <fmt:message key="ui.obj.availability.useAccessConstraints" />
                                        </label>
                                    </span>
                                    <div class="input tableContainer">
                                        <div id="availabilityUseAccessConstraints" autoHeight="4" interactive="true">
                                        </div>
                                    </div>
                                    </div>
                                </span>
                                <span id="uiElementN026" class="outer optional">
                                    <div>
                                    <span id="availabilityUseConstraintsLabel" class="label left">
                                        <label for="availabilityUseConstraints" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10026)">
                                            <fmt:message key="ui.obj.availability.useConstraints" />
                                        </label>
                                    </span>
                                    <span class="input clear"><input type="text" id="availabilityUseConstraints" name="availabilityUseConstraints" /></span>
                                    </div>
                                </span>
                            </div>

                            <div class="inputContainer">
                                <span id="uiElement1320" class="outer">
                                	<div><span class="label" id="availabilityDataFormatLabel">
                                        <label for="availabilityDataFormat" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1320)">
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
                                        <label for="availabilityMediaOptions" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1310)">
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
                                            <label for="availabilityOrderInfo" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 5052)">
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('links');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7007)">
                                <fmt:message key="ui.obj.links.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="linksContent" class="content">
                          <div class="inputContainer">
                            <span id="uiElementN017" class="outer">
                                <div>
                                    <span class="label left">
                                        <label for="linksTo" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1500)">
                                            <fmt:message key="ui.obj.links.linksTo.title" />
                                        </label>
                                    </span>
                                    <span class="input right" style="padding-left: 13px;">
                                        <select autoComplete="false" id="linksToRelationTypeFilter" title=""></select>
                                    </span>
                                    <span class="functionalLink">
                                        <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="#" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('linksTo'), 'dialogs/mdek_links_dialog.jsp?c='+userLocale, 900, 680, true, {gridId: 'linksTo'});" title="<fmt:message key="dialog.popup.serviceLink.link" /> [Popup]"><fmt:message key="ui.obj.links.linksTo.link" /></a>
                                    </span>
                                    <span class="functionalLink">
                                        <img src="img/inherit.png" width="10" height="9" alt="Popup" /><a href="#" id="linkGetLinksToFromParent" onclick="require('ingrid/IgeEvents').getLinksToFromParent();" title="<fmt:message key="ui.obj.inherit.link.tooltip" />"><fmt:message key="ui.obj.inherit.link" /></a>
                                    </span>
                                    <div class="input tableContainer clear">
                                        <div id="linksTo" autoHeight="4" class="hideTableHeader" contextMenu="EDIT_LINK">
                                        </div>
                                    </div>
                                </div>
                            </span>
                          </div>
                          <div class="inputContainer">
                            <span id="uiElementN018" class="outer">
                                <div>
                                    <span class="label">
                                        <label for="linksFrom" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1510)">
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
                                    <label for="addressTitle" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7044, 'Adresstitel')">
                                        <fmt:message key="ui.adr.header.addressTitle" />*
                                    </label>
                                </td>
                                <td colspan="2">
                                    <input type="text" id="addressTitle" name="addressTitle" disabled="disabled" />
                                </td>
                            </tr>
                            <tr>
                                <td id="addressTypeLabel" class="label col1">
                                    <label for="addressType" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7045, 'Adresstyp')">
                                        <fmt:message key="ui.adr.header.addressType" />*
                                    </label>
                                </td>
                                <td class="col2">
                                    <input type="text" id="addressType" disabled="disabled" />
                                </td>
                                <td class="col3">
                                    <img id="permissionAdrLock" src="img/lock.gif" width="9" height="14" alt="gesperrt" />
                                </td>
                            </tr>
                            <tr>
                                <td id="addressOwnerLabel" class="label">
                                    <label for="addressOwner" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 1030, 'Verantwortlicher')">
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
                                    | <strong><fmt:message key="ui.adr.header.uuid" />:</strong><span id="addressUuid" class="oneClickMark">XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX</span>
                                    <br><strong><fmt:message key="ui.adr.header.modUser" />:</strong><span id="addressLastEditor">---</span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div id="contentFrameBodyAddress" class="contentContainer" style="display:none; width:720px;">
                    <!-- GREY FIELD //-->
                    <!-- ADDRESS TYPE 0 - Institution //-->
                    <div id="headerAddressType0" class="rubric contentBlock firstBlock">
                        <div id="headerAddressType0Content" class="content">
                            <!-- AddressType0 parent institutions must not be displayed (http://jira.media-style.com/browse/INGRIDII-130) --><!--
                            <div class="inputContainer">
                            <span class="label"><label for="headerAddressType0Institution" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8062, 'Institution/&uuml;bergeordnete Einheit(en)')">Institution/&uuml;bergeordnete Einheit(en)</label></span>
                            <span class="input"><input type="text" data-dojo-type="ingrid:ValidationTextbox" id="headerAddressType0Institution" class="w668 h038" disabled="true" /></span>
                            </div>
                            -->
                            <div class="inputContainer">
                                <span id="uiElement4100" class="outer required">
                                    <div><span id="headerAddressType0UnitLabel" class="label">
                                        <label for="headerAddressType0Unit" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4100)">
                                            <fmt:message key="ui.adr.general.institution" />
                                        </label>
                                    </span><span class="input"><input type="text" required="true" id="headerAddressType0Unit" /></span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement4571_at0" class="outer halfWidth">
                                    <div><span class="label">
                                        <label id="extraInfoPublishAreaAddress0Label" for="extraInfoPublishAreaAddress0" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4571)">
                                            <fmt:message key="ui.obj.additionalInfo.publicationCondition" />
                                        </label>
                                    </span><span class="input"><input autoComplete="false" listId="3571" id="extraInfoPublishAreaAddress0" /></span>
                                    </div>
                                </span>
                                <span class="outer halfWidth"></span>
                                <div class="fill"></div>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- ADDRESS TYPE 1 - Unit //-->
                    <div id="headerAddressType1" class="rubric contentBlock firstBlock">
                        <div id="headerAddressType1Content" class="content">
                            <div class="inputContainer">
                                <span id="uiElement04210" class="outer optional show">
                                    <div><span class="label">
                                        <label for="headerAddressType1Institution" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4210)">
                                            <fmt:message key="ui.adr.general.parentInstitution" />
                                        </label>
                                    </span><span class="input"><input type="text" id="headerAddressType1Institution" disabled="disabled" /></span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement04200" class="outer required">
                                    <div><span id="headerAddressType1UnitLabel" class="label">
                                        <label for="headerAddressType1Unit" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4200)">
                                            <fmt:message key="ui.adr.general.unit" />
                                        </label>
                                    </span><span class="input"><input type="text" required="true" id="headerAddressType1Unit" /></span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement4571_at1" class="outer halfWidth">
                                    <div><span class="label">
                                        <label id="extraInfoPublishAreaAddress1Label" for="extraInfoPublishAreaAddress1" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4571)">
                                            <fmt:message key="ui.obj.additionalInfo.publicationCondition" />
                                        </label>
                                    </span><span class="input"><input autoComplete="false" listId="3571" id="extraInfoPublishAreaAddress1" /></span>
                                    </div>
                                </span>
                                <span class="outer halfWidth"></span>
                                <div class="fill"></div>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- ADDRESS TYPE 2 - Person //-->
                    <div id="headerAddressType2" class="rubric contentBlock firstBlock">
                        <div id="headerAddressType2Content" class="content">
                            <div class="inputContainer">
                                <span id="uiElement14210" class="outer optional show">
                                    <div><span class="label">
                                        <label for="headerAddressType2Institution" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4210)">
                                           <fmt:message key="ui.adr.general.parentInstitution" />
                                        </label>
                                    </span><span class="input"><input type="text" id="headerAddressType2Institution" disabled="disabled" /></span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement4315" class="outer halfWidth required">
                                        <div><span id="headerAddressType2LastnameLabel" class="label">
                                            <label for="headerAddressType2Lastname" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4315)">
                                                <fmt:message key="ui.adr.general.surName" />
                                            </label>
                                        </span><span class="input"><input type="text" id="headerAddressType2Lastname" required="true" name="headerAddressType2Lastname" /></span>
                                        </div>
                                    </span>
                                    <span id="uiElement4310" class="outer halfWidth optional show">
                                        <div><span id="headerAddressType2FirstnameLabel" class="label">
                                            <label for="headerAddressType2Firstname" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4310)">
                                                <fmt:message key="ui.adr.general.foreName" />
                                            </label>
                                        </span><span class="input"><input type="text" id="headerAddressType2Firstname" name="headerAddressType2Firstname" /></span>
                                        </div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement4300" class="outer halfWidth optional show">
                                        <div><span id="headerAddressType2StyleLabel" class="label">
                                            <label for="headerAddressType2Style" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4300)">
                                                <fmt:message key="ui.adr.general.form" />
                                            </label>
                                        </span><span class="input"><input style="width:129px;" listId="4300" id="headerAddressType2Style" /></span>
                                        </div>
                                    </span>
                                    <span id="uiElement4305" class="outer halfWidth optional show">
                                        <div><span id="headerAddressType2TitleLabel" class="label">
                                            <label for="headerAddressType2Title" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4305)">
                                                <fmt:message key="ui.adr.general.title" />
                                            </label>
                                        </span><span class="input"><input style="width:129px;" listId="4305" id="headerAddressType2Title" /></span>
                                        </div>
                                    </span>
                            </div>
                            <div>
                                <span id="uiElement4571_at2" class="outer halfWidth">
                                    <div><span class="label">
                                        <label id="extraInfoPublishAreaAddress2Label" for="extraInfoPublishAreaAddress2" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4571)">
                                            <fmt:message key="ui.obj.additionalInfo.publicationCondition" />
                                        </label>
                                    </span><span class="input"><input autoComplete="false" listId="3571" id="extraInfoPublishAreaAddress2" /></span>
                                    </div>
                                </span>
                                <span id="uiElement4320" class="outer halfWidth optional show">
                                    <div>
                                    <div class="input"><span class="label">&nbsp;</span>
                                        <input type="checkbox" id="headerAddressType2HideAddress" title="" />
                                        <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4320)">
                                            <fmt:message key="ui.adr.general.hideAddress" />
                                        </label>
                                    </div>
                                    </div>
                                </span>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div>
                    <!-- ADDRESS TYPE 3 - Free Address //-->
                    <div id="headerAddressType3" class="rubric contentBlock firstBlock">
                        <div id="headerAddressType3Content" class="content">
                            <div class="inputContainer">
                                    <span id="uiElement4000" class="outer halfWidth required">
                                        <div><span id="headerAddressType3LastnameLabel" class="label">
                                            <label for="headerAddressType3Lastname" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4000)">
                                                <fmt:message key="ui.adr.general.surName" />
                                            </label>
                                        </span><span class="input"><input type="text" id="headerAddressType3Lastname" required="true" name="headerAddressType3Lastname" class="w320" /></span>
                                        </div>
                                    </span>
                                    <span id="uiElement4005" class="outer halfWidth optional show">
                                        <div><span id="headerAddressType3FirstnameLabel" class="label">
                                            <label for="headerAddressType3Firstname" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4005)">
                                                <fmt:message key="ui.adr.general.foreName" />
                                            </label>
                                        </span><span class="input"><input type="text" id="headerAddressType3Firstname" name="headerAddressType3Firstname" class="w320" /></span>
                                        </div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                    <span id="uiElement4015" class="outer halfWidth optional show">
                                        <div><span id="headerAddressType3StyleLabel" class="label">
                                            <label for="headerAddressType3Style" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4015)">
                                                <fmt:message key="ui.adr.general.form" />
                                            </label>
                                        </span><span class="input"><input style="width:129px;" listId="4300" id="headerAddressType3Style" /></span>
                                        </div>
                                    </span>
                                    <span id="uiElement4020" class="outer halfWidth optional show">
                                        <div><span class="label">
                                            <label for="headerAddressType3Title" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4020)">
                                                <fmt:message key="ui.adr.general.title" />
                                            </label>
                                        </span><span class="input"><input style="width:129px;" listId="4305" id="headerAddressType3Title" /></span>
                                        </div>
                                    </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement4010" class="outer optional show">
                                    <div><span class="label">
                                        <label for="headerAddressType3Institution" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4010)">
                                            Institution
                                        </label>
                                    </span><span class="input"><input type="text" id="headerAddressType3Institution" class="w668 h038" /></span>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                                <span id="uiElement4571_at3" class="outer halfWidth">
                                    <div><span class="label">
                                        <label id="extraInfoPublishAreaAddress3Label" for="extraInfoPublishAreaAddress3" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4571)">
                                            <fmt:message key="ui.obj.additionalInfo.publicationCondition" />
                                        </label>
                                    </span><span class="input"><input autoComplete="false" listId="3571" id="extraInfoPublishAreaAddress3" /></span>
                                    </div>
                                </span>
                                <span class="outer halfWidth"></span>
                                <div class="fill"></div>
                            </div>
                        </div>
                        <div class="fill"></div>
                    </div><!-- ADRESSE UND AUFGABEN //-->
                    <!-- ADDRESS //-->
                    <div class="fill"></div>
                    <div id="address" class="rubric contentBlock">
                        <div class="titleBar">
                            <a onclick="require('ingrid/IgeEvents').toggleFields('address');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7008)">
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
                                            <label for="addressCom" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4430)">
                                                <fmt:message key="ui.adr.details.communicationTable.title" />
                                            </label>
                                        </span>
                                        <div class="input">
                                            <div id="addressCom" autoHeight="6" interactive="true">
                                            </div>
                                        </div>
                                    </div>
                                </span>
                                <span class="outer halfWidth optional">
                                    <span id="uiElement4400" class="outer">
                                        <div>
                                            <span id="addressStreetLabel" class="label">
                                                <label for="addressStreet" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4400)">
                                                    <fmt:message key="ui.adr.details.street" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input type="text" id="addressStreet" name="addressStreet" />
                                            </span>
                                        </div>
                                    </span>
                                    <span id="uiElement4410" class="outer" style="width:33%;">
                                        <div>
                                            <span id="addressZipCodeLabel" class="label">
                                                <label for="addressZipCode" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4410)">
                                                    <fmt:message key="ui.adr.details.postCode" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input type="text" id="addressZipCode" name="addressZipCode" />
                                            </span>
                                        </div>
                                    </span>
                                    <span id="uiElement4415" class="outer" style="width:67%;">
                                        <div>
                                            <span id="addressCityLabel" class="label">
                                                <label for="addressCity" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4415)">
                                                    <fmt:message key="ui.adr.details.city" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input type="text" id="addressCity" name="addressCity" />
                                            </span>
                                        </div>
                                    </span>
                                    <span id="uiElement4420" class="outer halfWidth">
                                        <div>
                                            <span id="addressPOBoxLabel" class="label">
                                                <label for="addressPOBox" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4420)">
                                                    <fmt:message key="ui.adr.details.poBox" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input type="text" id="addressPOBox" name="addressPOBox" class="w148" />
                                            </span>
                                        </div>
                                    </span>
                                    <span id="uiElement4425" class="outer halfWidth">
                                        <div>
                                            <span id="addressZipPOBoxLabel" class="label">
                                                <label for="addressZipPOBox" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4425)">
                                                    <fmt:message key="ui.adr.details.poBoxPostCode" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input type="text" id="addressZipPOBox" name="addressZipPOBox" class="w061" />
                                            </span>
                                        </div>
                                    </span>
                                    <span id="uiElement6006" class="outer halfWidth">
                                        <div>
                                            <span id="addressAdministrativeAreaLabel" class="label">
                                                <label for="addressAdministrativeArea" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 6006)">
                                                    <fmt:message key="ui.adr.details.administrativeArea" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input autoComplete="false" listId="6200" maxHeight="200" id="addressAdministrativeArea" />
                                            </span>
                                        </div>
                                    </span>
                                    <span id="uiElement4405" class="outer halfWidth">
                                        <div>
                                            <span id="addressCountryLabel" class="label">
                                                <label for="addressCountry" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4405)">
                                                    <fmt:message key="ui.adr.details.state" />
                                                </label>
                                            </span>
                                            <span class="input">
                                                <input autoComplete="false" listId="6200" maxHeight="200" id="addressCountry" />
                                            </span>
                                        </div>
                                    </span>
                                    <div style="position:relative; float:right;">
                                        <button id="buttonGetAddressFromParent">
                                            <fmt:message key="ui.adr.details.buttonInherit" />
                                        </button>
                                    </div>
                                </span>
                            </div>
	                        <span id="uiElement4440" class="outer optional">
	                            <div>
	                                <span class="label">
	                                    <label for="addressTasks" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4440)">
	                                        <fmt:message key="ui.adr.details.tasks" />
	                                    </label>
	                                </span>
	                                <span class="input">
	                                    <input type="text" id="addressTasks" />
	                                </span>
	                            </div>
	                        </span>
	                        <span id="uiElement4450" class="outer optional">
                                <div>
                                    <span class="label">
                                        <label for="addressHoursOfService" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4450)">
                                            <fmt:message key="ui.adr.details.addressHoursOfService" />
                                        </label>
                                    </span>
                                    <span class="input">
                                        <input type="text" id="addressHoursOfService" />
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('adrThesaurus');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7009)">
                                <fmt:message key="ui.adr.thesaurus.title" />
                            </div>
                            <div class="titleUp">
                                <a href="#sectionBottomContent" title="<fmt:message key="general.up" />"><img src="img/ic_up_blue.gif" width="9" height="6" alt="^" /></a>
                            </div>
                        </div>
                        <div id="adrThesaurusContent" class="content">
                            <div class="inputContainer">
                                <span id="uiElement4510" class="outer optional">
                                    <div><span class="label left">
                                        <label for="thesaurusTermsAddress" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4510)">
                                            <fmt:message key="ui.adr.thesaurus.terms" />
                                        </label>
                                    </span><span class="functionalLink"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="#" onclick="require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('thesaurusNavigator'), 'dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {dstTable: 'thesaurusTermsAddress'});" title="<fmt:message key="dialog.popup.thesaurus.terms.link.navigator" /> [Popup]"><fmt:message key="ui.adr.thesaurus.terms.link.navigator" /></a></span>
                                    <div class="input tableContainer clear">
                                        <div id="thesaurusTermsAddress" autoHeight="3" minRows="3" class="hideTableHeader">
                                        </div>
                                    </div>
                                    </div>
                                </span>
                            </div>
                            <div class="inputContainer">
                            	 <span id="uiElementN019" class="outer optional">
                            	 <div><span class="label">
                                        <label for="thesaurusTermsAddress" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 4500)">
                                            <fmt:message key="ui.adr.thesaurus.terms.custom" />
                                        </label>
                                        </span>
                                        <div class="input">
                                            <input type="text" id="thesaurusFreeTermInputAddress" title="" />
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
                            <a onclick="require('ingrid/IgeEvents').toggleFields('associatedObj');" title="<fmt:message key="general.open.required.field" />">
                                <div class="image18px titleIcon"></div>
                            </a>
                            <div class="titleCaption" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7010)">
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

    </body>
</html>
