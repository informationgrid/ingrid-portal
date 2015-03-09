<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="dialog.popup.object.assign.link" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">

var dialogSelectObject;

require(["dojo/dom",
         "dojo/on",
         "dojo/query",
         "dojo/keys",
         "dojo/dom-class",
         "dojo/topic",
         "dojo/Deferred",
        "dijit/registry",
        "ingrid/tree/MetadataTree",
        "ingrid/utils/Events",
        "dojox/validate"
    ], function(dom, on, query, keys, domClass, topic, Deferred, registry, MetadataTree, UtilEvents) {

        var customParams = _container_.customParams;
        var dlgContainer = _container_;
        
        var btnAnalyze, recordUrl;

        on(_container_, "Load", function() {
            btnAnalyze = registry.byId("btn_analyze");
            recordUrl = registry.byId("recordUrl");
            
            init();

            console.log("Publishing event: '/afterInitDialog/SelectObject'");
            topic.publish("/afterInitDialog/SelectObject");
            
            registry.byId("tabContainerLinkDlg").watch("selectedChildWidget", function(name, oldVal, newVal){
                if (newVal.id === "tabRemote") {
                    domClass.remove(btnAnalyze.domNode, "hide");
                    updateAssignButton();
                    recordUrl.focus();
                } else {
                    domClass.add(btnAnalyze.domNode, "hide");
                    registry.byId("btn_assign").set("disabled", true);
                    registry.byId("treeAssignObj").set("selectedNodes", [])
                }
            });
        });

        function init() {
            createTree();

            // expand tree to selected node
            var deferred = new Deferred();
            deferred.then(function(pathList) {
                console.debug("pathList");
                console.debug(pathList);
                registry.byId("treeAssignObj").set("paths", [["objectRoot"].concat(pathList)]);
            });

            if (customParams.jumpToNode) {
                topic.publish("/getObjectPathRequest", {
                    id: customParams.jumpToNode,
                    resultHandler: deferred,
                    ignoreDirtyFlag: true
                });
            }

            if (customParams.additionalText) {
                dom.byId("additionalText").innerHTML = customParams.additionalText;
            }
            
            on(recordUrl, "keyup", function(key) {
                debugger;
                if (recordUrl.validate()) {
                    btnAnalyze.set("disabled", false);
                    if (key.keyCode == keys.ENTER) analyzeUrl(recordUrl.get("value"));
                } else {
                    btnAnalyze.set("disabled", true);
                }
            });
            
            on(btnAnalyze, "Click", function() { analyzeUrl(recordUrl.get("value")); });
        }

        function createTree() {
            // var tree = createCustomTree("treeAssignObj", null, "id", "title", loadObjectData);
            var tree = new MetadataTree({
                showRoot: false,
                treeType: "Objects"
            }, "treeAssignObj");

            // only allow certain objects to choose from when adding to some tables
            if (customParams.caller && customParams.caller.gridId == "ref1ServiceLink") {
                tree.excludeFunction = function(item) {
                    return item.userWritePermission && item.objectClass != 3;
                };
            }
            if (customParams.caller && customParams.caller.gridId == "ref3BaseDataLink") {
                tree.excludeFunction = function(item) {
                    return item.userWritePermission && item.objectClass != 1;
                };
            }

            on(tree, "Click", function(node, data) {
                if (domClass.contains(data.labelNode, "TreeNodeNotSelectable")) {
                    registry.byId("btn_assign").set("disabled", true);
                } else {
                    registry.byId("btn_assign").set("disabled", false);
                }
            });

        }

        function assignObject() {
            if (!UtilEvents.publishAndContinue("/onBeforeDialogAccept/SelectObject")) return;
            var node = registry.byId("treeAssignObj").selectedNode;
            if (node) {
                var retVal = {};
                retVal.uuid = node.item.id;
                retVal.title = node.item.title;
                retVal.objectClass = node.item.nodeDocType.substr(5, 1);

                customParams.resultHandler.resolve(retVal);
            }

            dlgContainer.hide();
        }
        
        function updateAssignButton() {
            registry.byId("btn_assign").set("disabled", true);
        }
        
        function analyzeUrl(url) {
            console.log("Analyzing URL ...");
            // hide all error messages
            query(".errors .error").addClass("hide");
            
            GetCapabilitiesService.getRecordById(url, function(result) {
                console.log("received record:", result);
                if (!result) {
                    domClass.remove("errorWrongUrl", "hide");
                    return;
                }
                if (!result.identifier) domClass.remove("errorNoId", "hide");
                if (!result.hasDownloadData) domClass.remove("errorNoData", "hide");
                
                dom.byId("recordId").innerHTML = result.identifier;
                dom.byId("recordTitle").innerHTML = result.title;
                dom.byId("recordDownloadData").innerHTML = result.downloadData;
            });
        }

        /**
         * PUBLIC METHODS
         */

        dialogSelectObject = {
            assignObject: assignObject
        };
    }
);

</script>
</head>

<body>

    <div id="catalogueObject" class="">
        <div id="winNavi" style="top: 0;">
            <a href="javascript:void(0);"
                onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-8#maintanance-of-objects-8', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');"
                title="<fmt:message key="general.help" />">[?]</a>
        </div>

        <div id="tabContainerLinkDlg" data-dojo-type="dijit/layout/TabContainer" style="height: 510px; overflow: visible;" selectedChild="tabLocal">
            <div id="tabLocal" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.links.local" />">
                <div id="objectContent" class="content">

                    <!-- CONTENT START -->
                    <div class="inputContainer">
                        <div data-dojo-type="dijit/layout/ContentPane" id="treeContainerAssignObj" style="height: 413px;">
                            <div id="treeAssignObj" style="height: 100%;"></div>
                        </div>
                    </div>
                    <div class="inputContainer">
                        <span id="additionalText"></span>
                        <%-- <span class="button transparent">
                            <span style="float: right;">
                                <button id="btn_assign" data-dojo-type="dijit/form/Button" disabled="disabled" onclick="dialogSelectObject.assignObject()">
                                    <fmt:message key="dialog.links.select.assign" />
                                </button>
                            </span>
                        </span> --%>
                    </div>
                    <div class="fill"></div>
                    <!-- CONTENT END -->
                </div>
            </div>
            <div id="tabRemote" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.links.remote" />">
                <div class="contentBlockWhite">
                    <span class="label">
                        <!-- TODO: add help or remove pointer -->
                        <label for="recordUrl" <!-- onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7017) -->">
                            <fmt:message key="dialog.links.recordUrl" />
                        </label>
                    </span>
                    <span class="input spaceBelow">
                        <input id="recordUrl" class="dijitInputContainer" style="width:100%;" type="url" data-dojo-type="dijit/form/ValidationTextBox" data-dojo-props="regExpXXX:dojox.validate.regexp.url" required />
                    </span>
                    <div class="errors">
                        <div id="errorWrongUrl" class="hide error"><fmt:message key="dialog.links.error.wrongUrl" /></div>
                        <div id="errorNoId" class="hide error"><fmt:message key="dialog.links.error.noIdentifier" /></div>
                        <div id="errorNoData" class="hide error"><fmt:message key="dialog.links.error.noDownloadData" /></div>
                    </div>
                    
                    <div id="recordResults">
                        <div id="recordId"></div>
                        <div id="recordTitle"></div>
                        <div id="recordDownloadData"></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="dijitDialogPaneActionBar">
            <button data-dojo-type="dijit/form/Button" type="button" class="hide" id="btn_analyze" disabled="true">
                <fmt:message key="dialog.links.analyze" />
            </button>
            <button data-dojo-type="dijit/form/Button" type="submit" id="btn_assign">
                <fmt:message key="dialog.links.select.assign" />
            </button>
        </div>
    </div>
</body>
</html>
