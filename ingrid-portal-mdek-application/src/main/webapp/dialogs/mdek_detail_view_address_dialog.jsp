<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 wemove digital solutions GmbH
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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<style type="text/css">
    #printDialogSettings {
        border: 1px #ccc solid;
        padding: 10px;
        margin-bottom: 20px;
    }
</style>
<script type="text/javascript">

var pageDetailViewAddress = _container_;

require([
    "dojo/on",
    "dojo/Deferred",
    "dojo/DeferredList",
    "dojo/dom-style",
    "dijit/registry",
    "dojo/dom",
    "dojo/topic",
    "dojo/string",
    "dojo/_base/lang",
    "dojo/_base/array",
    "ingrid/IgeActions",
    "ingrid/utils/UI",
    "ingrid/hierarchy/addressLayout",
    "ingrid/message",
    "ingrid/dialog",
    "ingrid/hierarchy/detail_helper",
    "ingrid/utils/Address",
    "ingrid/utils/Catalog"
], function(on, Deferred, DeferredList, style, registry, dom, topic, string, lang, array,
        IgeActions, UtilUI, ingridAddressLayout, message, dialog, detailHelper, UtilAddress, UtilCatalog) {

        on(_container_, "Load", function() {
            on(registry.byId("showSubTree"), "click", refreshView);
            on(registry.byId("showDetailedView"), "click", refreshView);

            pageDetailViewAddress.mappedNodes = {};
            pageDetailViewAddress.subNodesCount = 1;
            pageDetailViewAddress.useDirtyData = false;
            pageDetailViewAddress.detailDivContent = "";

            // preview/print search results
            if (pageDetailViewAddress.customParams.searchResult) {
                pageDetailViewAddress.resultView = true;
                registry.byId("showSubTree").set("checked", true);
                registry.byId("showDetailedView").set("checked", false);
                refreshView();

            } else { // print hierarchy tree

                // determine selected node by dirty data flag
                pageDetailViewAddress.selectedNode = pageDetailViewAddress.customParams.useDirtyData ? registry.byId("dataTree").selectedNode : pageDetailViewAddress.customParams.selectedNode;

                // convert tree item to a json construct
                pageDetailViewAddress.nodeTreeItem = pageDetailViewAddress.selectedNode.item;
                // or udkDataProxy._getData()!!!

                // start from selected (parent) node to create the complete subtree, which will
                // be used to render the whole detailed information or just the titles
                // this is only needed once and saves time especially when printing big trees
                console.debug("determine subtree");
                setLoadingZone(true);
                determineCompleteSubTree([pageDetailViewAddress.nodeTreeItem], 0)
                .then(lang.hitch(this, function() {
                    if (pageDetailViewAddress.customParams.useDirtyData !== true) {
                        console.debug("load data");
                        // make sure the object forms are already loaded since they are needed
                        // for syslist values for example
                        ingridAddressLayout.create();
                    }
                    refreshView();
                }));
            }

            console.log("Publishing event: '/afterInitDialog/AddressDetail'");
            topic.publish("/afterInitDialog/AddressDetail");
        });

        function refreshView() {
            console.debug("refresh view");

            setLoadingZone(true);

            var showSubTree = registry.byId("showSubTree").checked;
            var showDetails = registry.byId("showDetailedView").checked;
            pageDetailViewAddress.useDirtyData = pageDetailViewAddress.customParams.useDirtyData;

            // empty preview div
            dom.byId("detailViewContent").innerHTML = "";
            pageDetailViewAddress.detailDivContent = "";

            // reset processing info
            pageDetailViewAddress.currentlyProcessedNode = 0;
            pageDetailViewAddress.stopOperation = false;
            pageDetailViewAddress.detailData = [];

            // handle search results
            if (pageDetailViewAddress.resultView) {
                renderSearchResults();

            } else { // handle tree
                if (showSubTree) {
                    renderSubTreeNodeData()
                    .then(function() {
                        dom.byId("detailViewContent").innerHTML = pageDetailViewAddress.detailDivContent;
                    })
                    .then(hideProcessingDialog);
                } else {
                    var def = null;
                    if (showDetails) {
                        def = loadAndRenderTreeNode(pageDetailViewAddress.selectedNode.item.id);
                    } else {
                        renderSimpleTreeNode(pageDetailViewAddress.nodeTreeItem);
                        def = new Deferred();
                        def.resolve();
                    }
                    def.then(function() {
                        dom.byId("detailViewContent").innerHTML = pageDetailViewAddress.detailDivContent;
                    })
                    .then(dojo.partial(setLoadingZone, false));
                }
            }
        }

        function renderSearchResults() {
            var result = pageDetailViewAddress.customParams.searchResult;
            pageDetailViewAddress.subNodesCount = result.numHits;
            var showDetails = registry.byId("showDetailedView").checked;

            // show a warning dialog if it would take a long time to prepare the print preview
            var def = showTooManyNodesDialogIfNecessary()
            .then(showProcessingDialog);
            array.forEach(result.resultList, function(entry) {
                if (showDetails) {
                    def = def.then(lang.hitch(this, dojo.partial(loadAndRenderTreeNode, entry.uuid, entry.nodeAppType)));
                } else {
                    // give some time to render!
                    setTimeout(function() {
                        def = def.then(lang.hitch(this, function() {
                            entry.title = UtilAddress.createAddressTitle(entry);
                            renderSimpleTreeNode(entry);
                            processNode();
                        }));
                    }, 10);
                }
            }, this);
            setTimeout(function() {
                def = def.then(function() {
                    dom.byId("detailViewContent").innerHTML = pageDetailViewAddress.detailDivContent;
                    hideProcessingDialog();
                });
            }, 10);
        }

        function updateCheckboxesFunctionality() {
            if (pageDetailViewAddress.resultView) {
                UtilUI.disableElement("showSubTree");
            } else {
                UtilUI.enableElement("showSubTree");
            }
        }

        /**
         * Determine the subtree structure with all elements hierarchically ordered. This improves
         * the speed when determining all subordinate objects since no backend requests are sent anymore.
         * Also determine statistical data.
         */
        function determineCompleteSubTree(nodeDataList, hierarchy) {
            if (!(nodeDataList instanceof Array)) nodeDataList = [nodeDataList];
            var allDeferredsToWaitFor = [];

            array.forEach(nodeDataList, function(nodeData) {
                pageDetailViewAddress.mappedNodes[nodeData.id] = nodeData;
                nodeData.hierarchy = hierarchy;
                if (nodeData.isFolder) {
                    var waitForMe = new Deferred();
                    getImmediateChildrenOfNode(nodeData)
                    .then(lang.hitch(this, function(children) {
                        // remember number of children for later statistics
                        pageDetailViewAddress.subNodesCount += children.length;
                        // add parent information
                        array.forEach(children, function(child) {
                            child.parent = nodeData.id;
                        });
                        nodeData.children = children;
                        determineCompleteSubTree(children, hierarchy + 1)
                        .then(function() {
                            waitForMe.resolve();
                        });
                    }));
                    allDeferredsToWaitFor.push(waitForMe);
                }
            }, this);

            // only resolve the deferred if all children have no other children!
            // this means that the next sibling in parent can be rendered
            if (allDeferredsToWaitFor.length > 0) {
                return new DeferredList(allDeferredsToWaitFor);
            } else {
                var def = new Deferred();
                def.resolve();
                return def;
            }
        }

        // UtilTree.getSubTree
        function getImmediateChildrenOfNode(nodeData) {
            var deferred = new Deferred();
            // share the deferred to keep in correct order!
            // otherwise the children of a node wouldn't be rendered before its siblings
            // -> other approach!!!
            TreeService.getSubTree(nodeData.id, nodeData.nodeAppType, userLocale, {
                callback: function(res) {
                    deferred.resolve(res);
                },
                errorHandler: function(message) {
                    displayErrorMessage(new Error(message));
                    deferred.reject();
                }
            });
            return deferred;
        }

        function renderSubTreeNodeData() {
            console.debug("renderTreeNodeData");
            var showSubTree = registry.byId("showSubTree").checked;
            var renderDetailView = registry.byId("showDetailedView").checked;

            // show a warning dialog if it would take a long time to prepare the print preview
            var def = showTooManyNodesDialogIfNecessary();

            if (renderDetailView && showSubTree)
                def = def.then(showProcessingDialog);

            var deferredRender = new Deferred();

            def.then(dojo.partial(lang.hitch(pageDetailViewAddress, renderSubTree), pageDetailViewAddress.nodeTreeItem))
            .then(deferredRender.resolve);

            return deferredRender;
        }

        function showTooManyNodesDialogIfNecessary() {
            var def = new Deferred();
            var renderDetailView = registry.byId("showDetailedView").checked;

            // count all subnodes first!
            if (renderDetailView && pageDetailViewAddress.subNodesCount > 100) {
                var displayText = string.substitute(message.get("dialog.object.detailView.dialog.warning"), [pageDetailViewAddress.subNodesCount]);
                dialog.show("<fmt:message key='general.info' />", displayText, dialog.INFO, [{
                    caption: "<fmt:message key='general.cancel' />",
                    action: hideProcessingDialog
                }, {
                    caption: "<fmt:message key='general.ok' />",
                    action: function() {
                        setTimeout(def.resolve, 0);
                    } // delay execution since error occurs when second dialog is called
                }]);
            } else {
                def.resolve();
            }
            return def;
        }

        function showProcessingDialog() {
            var displayText = string.substitute(message.get("dialog.object.detailView.dialog.processing"), [pageDetailViewAddress.subNodesCount]);
            pageDetailViewAddress.processDialog = dialog.show("<fmt:message key='general.info' />", displayText, dialog.INFO, [{
                caption: "<fmt:message key='general.cancel' />",
                action: function() {
                    pageDetailViewAddress.stopOperation = true;
                }
            }]);
            setLoadingZone(true);
        }

        function hideProcessingDialog() {
            // in case the dialog could not be rendered since the process was too quick
            // we need to wait for the promise which signals the end of animation
            var dialog = pageDetailViewAddress.processDialog;
            if (dialog) {
                dialog.promise.then(function() {
                    // anonymous function is needed here, otherwise dialog is not hidden!!!
                    dialog.hide();
                });
            }
            setLoadingZone(false);
        }

        function processNode() {
            pageDetailViewAddress.currentlyProcessedNode += 1;
            //console.debug("Processed: " + scriptScopeDetailView.currentlyProcessedNode);
            if (dom.byId("processInfoCurrent"))
                dom.byId("processInfoCurrent").innerHTML = pageDetailViewAddress.currentlyProcessedNode;
        }

        function setLoadingZone(activate) {
            if (activate) {
                style.set("detailLoadingZone", "visibility", "visible");
                UtilUI.disableElement("showSubTree");
                UtilUI.disableElement("showDetailedView");
                UtilUI.disableElement("printDetailObject");
            } else {
                style.set("detailLoadingZone", "visibility", "hidden");
                UtilUI.enableElement("showSubTree");
                UtilUI.enableElement("showDetailedView");
                UtilUI.enableElement("printDetailObject");
                updateCheckboxesFunctionality();
            }
        }

        function renderSubTree(nodeDataList) {
            // the main deferred of this function
            var def = new Deferred();
            
            // convert to array if necessary
            if (!(nodeDataList instanceof Array)) nodeDataList = [nodeDataList];

            // the deferred used for the sequencial execution of the sub nodes
            var defSubs = new Deferred();

            // start the execution of the sub-nodes from the beginning!
            defSubs.resolve();

            array.forEach(nodeDataList, function(nodeData) {
                if (registry.byId("showDetailedView").checked) {
                    defSubs = defSubs.then(dojo.partial(lang.hitch(this, loadAndRenderTreeNode), nodeData.id));
                } else {
                    renderSimpleTreeNode(nodeData);
                }

                // if node has children then recursively render these
                if (nodeData.isFolder) {
                    defSubs = defSubs.then(lang.partial(renderSubTree, nodeData.children));
                }

            }, this);

            // return the main deferred of this function after all sub-nodes were rendered
            defSubs.then(function() {
                def.resolve();
            });

            return def.promise;
        }

        function renderSimpleTreeNode(nodeData) {
            //scriptScopeDetailView.processNode();
            if (!pageDetailViewAddress.stopOperation) {
                pageDetailViewAddress.detailDivContent += "<div class='v_line'><div class='h_line' style='padding-left: " + (nodeData.hierarchy * 20) + "px'>&nbsp;</div><span>" + nodeData.title + "</span></div>";
            }
        }

        function loadAndRenderTreeNode(nodeId) {
            // skip if the user canceled
            // since several asynchronous deferreds exists it's better to ignore execution
            if (!pageDetailViewAddress.stopOperation) {
                // update process info
                processNode();
                var promise = getNodeData(nodeId)
                .then(lang.hitch(this, renderNodeData))
                // show a horizontal line between objects if a detail subtree is listed
                .then(function() {
                    if (registry.byId("showSubTree").checked) pageDetailViewAddress.detailDivContent += "<hr />";
                });
                return promise;
            }
        }

        function getNodeData(id) {
            var def = new Deferred();

            if (pageDetailViewAddress.useDirtyData) {
                def.resolve(IgeActions._getData());

            } else {
                AddressService.getAddressData(id, "false", {
                    callback: def.resolve,
                    errorHandler: function(message) {
                        displayErrorMessage(new Error(message));
                        def.reject();
                    }
                });
            }

            return def.promise;
        }

        function renderNodeData(nodeData) {
            renderSectionTitel("<fmt:message key='dialog.compare.address.address' />");
            var allOrganisations = getOrganisations(nodeData);
            nodeData.organisation = allOrganisations ? allOrganisations : nodeData.organisation;
            renderText(detailHelper.renderAddressEntry(nodeData).replace(/\n/g, '<br />'));

            // administrative data
            renderSectionTitel("<fmt:message key='dialog.compare.address.administrative' />");
            renderTextWithTitle(nodeData.uuid, "<fmt:message key='dialog.compare.address.id' />");
            renderTextWithTitle(UtilCatalog.catalogData.catalogName, "<fmt:message key='dialog.compare.address.catalog' />");

        }

        function getOrganisations(nodeData) {
            //console.debug("addressClass: " + nodeData.addressClass);
            if (pageDetailViewAddress.useDirtyData) {
                // can only be used once! for top node of tree
                pageDetailViewAddress.useDirtyData = false;
                var id = null;
                if (nodeData.addressClass === 0) {
                    id = "headerAddressType0Unit";
                } else if (nodeData.addressClass === 1) {
                    return registry.byId("headerAddressType1Institution").getValue() + "\n" + registry.byId("headerAddressType1Unit").getValue();
                } else if (nodeData.addressClass === 2) {
                    id = "headerAddressType2Institution";
                } else if (nodeData.addressClass === 3) {
                    id = "headerAddressType3Institution";
                }
                return registry.byId(id).getValue();
            } else {
                //var organisation = nodeData.organisation ? nodeData.organisation : "";
                var res = UtilAddress.determineInstitution(nodeData);
                return res;
            }
        }

        function renderSectionTitel(val) {
            pageDetailViewAddress.detailDivContent += "<br/><h2><u>" + val + "</u></h2><br/>";
        }

        function renderTextWithTitle(val, title) {
            if (detailHelper.isValid(val)) {
                // Replace newlines with <br/>
                val = val.replace(/\n/g, "<br/>");
                if (detailHelper.isValid(title)) {
                    pageDetailViewAddress.detailDivContent += "<p><strong>" + title + "</strong><br/>" + val + "</p><br/>";
                } else {
                    pageDetailViewAddress.detailDivContent += "<p>" + val + "</p><br/>";
                }
            }
        }

        function renderText(val) {
            if (val && val.length > 0) {
                // Replace newlines with <br/>
                val = val.replace(/\n/g, "<br/>");
                pageDetailViewAddress.detailDivContent += "<p>" + val + "</p><br/>";
            }
        }
    });

</script>
</head>

<body>
    <div data-dojo-type="dijit/layout/ContentPane">
        <div id="contentPane" class="contentBlockWhite">
            <div id="dialogContent" class="content">
                <div id="printDialogSettings" class="grey">
                    <input type="checkbox" id="showDetailedView" data-dojo-type="dijit/form/CheckBox" checked=true /> 
                    <label
                        for="showDetailedView" style="margin-right: 15px;"> <fmt:message
                            key="dialog.detail.print.showDetailedView" />
                    </label>
                    <input type="checkbox" id="showSubTree" data-dojo-type="dijit/form/CheckBox" /> <label for="showSubTree"
                        class="inActive"> <fmt:message key="dialog.detail.print.showSubTreeAddress" />
                    </label>
                    <button id="printDetailObject" data-dojo-type="dijit/form/Button" onclick="require('ingrid/utils/General').printDivContent('detailViewContent')" class="right" style="margin: -3px 0 0 10px;"><fmt:message key="dialog.detail.print" />
                    </button> 
                    <span id="detailLoadingZone" style="visibility: hidden;" class="processInfo right">
                        <img src="img/ladekreis.gif" width="20" height="20" alt="Loading" />
                    </span>
                </div>
                <!-- MAIN TAB CONTAINER START -->
                <div style="height:528px; width:100%; clear: both;">
                    <!-- MAIN TAB 1 START -->
                    <div id="detailView" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder" style="height: 500px;" 
                            title="<fmt:message key="dialog.detail.title" />">
                        <div id="detailViewContent" class="detailViewContentContainer" style="padding: 5px;"></div>
                    </div>
                    <!-- MAIN TAB 1 END -->
                </div>
                <!-- MAIN TAB CONTAINER END -->
            </div>
        </div>
    </div>
    <!-- CONTENT END -->
</body>
</html>
