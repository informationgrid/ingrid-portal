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

var pageDetailViewObjects = _container_;

require([
    "dojo/on",
    "dojo/has",
    "dojo/Deferred",
    "dojo/DeferredList",
    "dojo/dom-style",
    "dojo/query",
    "dijit/registry",
    "dojo/dom",
    "dojo/string",
    "dojo/topic",
    "dojo/dom-class",
    "dojo/_base/lang",
    "dojo/_base/array",
    "ingrid/IgeActions",
    "ingrid/IgeEvents",
    "ingrid/utils/UI",
    "ingrid/hierarchy/objectLayout",
    "ingrid/message",
    "ingrid/dialog",
    "ingrid/hierarchy/detail_helper",
    "ingrid/utils/String",
    "ingrid/utils/Syslist",
    "ingrid/utils/List",
    "ingrid/utils/Grid",
    "ingrid/utils/Tree",
    "ingrid/utils/Catalog"
], function(on, has, Deferred, DeferredList, style, query, registry, dom, string, topic, domClass, lang, array, IgeActions, IgeEvents, UtilUI, ingridObjectLayout, message, dialog, detailHelper, UtilString, UtilSyslist, UtilList, UtilGrid, UtilTree, UtilCatalog) {

        on(_container_, "Load", function() {
            pageDetailViewObjects.detailDiv = dom.byId("detailViewContent");
            pageDetailViewObjects.detailDivContent = "";
            pageDetailViewObjects.subNodesCount = 1;
            pageDetailViewObjects.currentlyProcessedNode = 0;
            pageDetailViewObjects.stopOperation = false;
            pageDetailViewObjects.mappedNodes = {};


            // preview/print search results
            if (pageDetailViewObjects.customParams.searchResult) {
                pageDetailViewObjects.resultView = true;
                registry.byId("showSubTree").set("checked", true);
                registry.byId("showDetailedView").set("checked", false);
                refreshView();

            } else { // print hierarchy tree
                // determine selected node by dirty data flag
                pageDetailViewObjects.selectedNode = pageDetailViewObjects.customParams.useDirtyData ? registry.byId("dataTree").selectedNode : pageDetailViewObjects.customParams.selectedNode;

                // convert tree item to a json construct
                pageDetailViewObjects.nodeTreeItem = pageDetailViewObjects.selectedNode.item;//itemToJS(registry.byId("dataTree").model.store, pageDetailViewObjects.selectedNode.item);

                // start from selected (parent) node to create the complete subtree, which will
                // be used to render the whole detailed information or just the titles
                // this is only needed once and saves time especially when printing big trees
                console.debug("determine subtree");
                setLoadingZone(true);
                determineCompleteSubTree([pageDetailViewObjects.nodeTreeItem], 0)
                .then(function() {
                    if (pageDetailViewObjects.customParams.useDirtyData !== true) {
                        console.debug("load data");
                        // make sure the object forms are already loaded since they are needed
                        // for syslist values for example
                        ingridObjectLayout.create();
                    }
                    refreshView();
                });
            }

            on(registry.byId("showSubTree"), "click", refreshView);
            on(registry.byId("showDetailedView"), "click", refreshView);
            on(registry.byId("showSubordinateObjects"), "click", refreshView);

            console.log("Publishing event: '/afterInitDialog/ObjectDetail'");
            topic.publish("/afterInitDialog/ObjectDetail");
        });

        function refreshView() {
            console.debug("refresh view");

            updateCheckboxesFunctionality();
            setLoadingZone(true);

            var showSubTree = registry.byId("showSubTree").checked;
            var showDetails = registry.byId("showDetailedView").checked;

            // empty preview div
            pageDetailViewObjects.detailDiv.innerHTML = "";
            pageDetailViewObjects.detailDivContent = "";

            // reset processing info
            pageDetailViewObjects.currentlyProcessedNode = 0;
            pageDetailViewObjects.stopOperation = false;

            // handle search results
            if (pageDetailViewObjects.resultView) {
                renderSearchResults();

            } else { // handle tree
                var def = null;
                if (showSubTree) {
                    renderSubTreeNodeData()
                    .then(function() {
                        pageDetailViewObjects.detailDiv.innerHTML = pageDetailViewObjects.detailDivContent;
                    })
                    .then(hideProcessingDialog);
                } else {
                    if (showDetails) {
                        def = loadAndRenderTreeNode(pageDetailViewObjects.selectedNode.item.id);
                    } else {
                        renderSimpleTreeNode(pageDetailViewObjects.nodeTreeItem);
                        def = new Deferred();
                        def.resolve();
                    }
                    def.then(function() {
                        pageDetailViewObjects.detailDiv.innerHTML = pageDetailViewObjects.detailDivContent;
                    })
                    .then(lang.partial(setLoadingZone, false));
                }
            }
        }

        function updateCheckboxesFunctionality() {
            if (pageDetailViewObjects.resultView) {
                UtilUI.disableElement("showSubTree");
            } else {
                UtilUI.enableElement("showSubTree");
            }

            /*if (registry.byId("showSubTree").checked) {
                UtilUI.enableElement("showDetailedView");
            } else {
                UtilUI.disableElement("showDetailedView");
            }    
            
            if (registry.byId("showDetailedView").checked) {
                UtilUI.enableElement("showSubordinateObjects");
            } else {
                UtilUI.disableElement("showSubordinateObjects");
            }*/
        }

        function renderSearchResults() {
            var result = pageDetailViewObjects.customParams.searchResult;
            pageDetailViewObjects.subNodesCount = result.numHits;
            var showDetails = registry.byId("showDetailedView").checked;

            // show a warning dialog if it would take a long time to prepare the print preview
            var def = showTooManyNodesDialogIfNecessary()
            .then(showProcessingDialog);
            
            array.forEach(result.resultList, function(entry) {
                if (showDetails) {
                    def = def.then(lang.hitch(pageDetailViewObjects, lang.partial(loadAndRenderTreeNode, entry.uuid, entry.nodeAppType)));
                } else {
                    def = def.then(lang.hitch(pageDetailViewObjects, function() {
                        renderSimpleTreeNode(entry);
                    }));
                }
            }, this);
            def.then(function() {
                pageDetailViewObjects.detailDiv.innerHTML = pageDetailViewObjects.detailDivContent;
                hideProcessingDialog();
            });
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
                pageDetailViewObjects.mappedNodes[nodeData.id] = nodeData;
                nodeData.hierarchy = hierarchy;
                if (nodeData.isFolder) {
                    // childrenToLoad = true;
                    var waitForMe = new Deferred();
                    getImmediateChildrenOfNode(nodeData)
                    .then(function(children) {
                        // remember number of children for later statistics
                        pageDetailViewObjects.subNodesCount += children.length;
                        // add parent information
                        array.forEach(children, function(child) {
                            child.parent = nodeData.id;
                        });
                        nodeData.children = children;
                        determineCompleteSubTree(children, hierarchy + 1)
                        .then(function() {
                            waitForMe.resolve();
                        });
                    });
                    allDeferredsToWaitFor.push(waitForMe);
                }
            });

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

        function renderSubTreeNodeData() {
            console.debug("renderTreeNodeData");
            var showSubTree = registry.byId("showSubTree").checked;
            var renderDetailView = registry.byId("showDetailedView").checked;
            // show a warning dialog if it would take a long time to prepare the print preview
            var def = showTooManyNodesDialogIfNecessary();

            if (renderDetailView && showSubTree)
                def = def.then(showProcessingDialog);

            var deferredRender = new Deferred();
            
            def.then(lang.hitch(pageDetailViewObjects, lang.partial(renderSubTree, pageDetailViewObjects.nodeTreeItem)))
            .then(deferredRender.resolve);

            return deferredRender;
        }

        function showTooManyNodesDialogIfNecessary() {
            var def = new Deferred();
            var renderDetailView = registry.byId("showDetailedView").checked;

            // count all subnodes first!
            if (renderDetailView && pageDetailViewObjects.subNodesCount > 100) {
                var displayText = string.substitute(message.get("dialog.object.detailView.dialog.warning"), [pageDetailViewObjects.subNodesCount]);
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
            var displayText = string.substitute(message.get("dialog.object.detailView.dialog.processing"), [pageDetailViewObjects.subNodesCount]);
            pageDetailViewObjects.processDialog = dialog.show("<fmt:message key='general.info' />", displayText, dialog.INFO, [{
                caption: "<fmt:message key='general.cancel' />",
                action: function() {
                    pageDetailViewObjects.stopOperation = true;
                }
            }]);
        }

        function hideProcessingDialog() {
            // in case the dialog could not be rendered since the process was too quick
            // we need to wait for the promise which signals the end of animation
            var dialog = pageDetailViewObjects.processDialog;
            if (dialog) {
                dialog.promise.then(function() {
                    // anonymous function is needed here, otherwise dialog is not hidden!!!
                    dialog.hide();
                });
            }
            setLoadingZone(false);
        }

        function processNode() {
            pageDetailViewObjects.currentlyProcessedNode += 1;
            //console.debug("Processed: " + pageDetailViewObjects.currentlyProcessedNode);
            if (dom.byId("processInfoCurrent"))
                dom.byId("processInfoCurrent").innerHTML = pageDetailViewObjects.currentlyProcessedNode;
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

            // each node in the tree will be loaded and rendered
            array.forEach(nodeDataList, function(nodeData) {
                if (registry.byId("showDetailedView").checked) {
                    // get the returned promise to chain it to the next call
                    defSubs = defSubs.then(lang.hitch(this, lang.partial(loadAndRenderTreeNode, nodeData.id)));
                } else {
                    renderSimpleTreeNode(nodeData);
                }

                // if node has children then recursively render these AFTER the current node has
                // been rendered
                if (nodeData.isFolder) {
                    defSubs = defSubs.then(lang.partial(renderSubTree, nodeData.children));
                }
            }, this);

            // return the main deferred of this function after all sub-nodes were rendered
            defSubs.then(def.resolve);

            return def.promise;
        }

        function renderSimpleTreeNode(nodeData) {
            processNode();
            if (!pageDetailViewObjects.stopOperation) {
                pageDetailViewObjects.detailDivContent += "<div class='v_line'><div class='h_line' style='padding-left: " + (nodeData.hierarchy * 20) + "px'>&nbsp;</div><span>" + nodeData.title + "</span></div>";
            }
        }

        function loadAndRenderTreeNode(nodeId, nodeType) {
            /*    if (nodeId == "objectRoot") {
                pageDetailViewObjects.detailDivContent += "<p>Objekte</p>";
                var def = new Deferred();
                def.resolve();
                return def;
            }
        */
            // skip if the user canceled
            // since several asynchronous deferreds exists it's better to ignore execution
            if (!pageDetailViewObjects.stopOperation) {
                // update process info
                processNode();
                var def = getNodeData(nodeId, nodeType)
                .then(lang.hitch(pageDetailViewObjects, enrichNodeDataWithInstitutions))
                .then(lang.hitch(pageDetailViewObjects, renderNodeData))
                // show a horizontal line between objects if a detail subtree is listed
                .then(function() {
                    if (registry.byId("showSubTree").checked) pageDetailViewObjects.detailDivContent += "<hr />";
                });
                return def;
            }
        }

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

        function getAllChildrenRecursively(nodeData) {
            var deferred = new Deferred();
            TreeService.getAllSubTreeChildren(nodeData.uuid, nodeData.nodeAppType, userLocale, {
                callback: function(children) {
                    deferred.resolve(children);
                },
                errorHandler: function(message) {
                    displayErrorMessage(new Error(message));
                    deferred.reject();
                }
            });
            return deferred;
        }

        function getNodeData(id, type) {
            var nodeId = id ? id : pageDetailViewObjects.selectedNode.item.id;
            var nodeType = type ? type : pageDetailViewObjects.selectedNode.item.nodeAppType;
            var isSelectedNode = false;
            if (pageDetailViewObjects.selectedNode && nodeId == pageDetailViewObjects.selectedNode.item.id) {
                isSelectedNode = true;
            }

            console.debug("getNodeData");
            var def = new Deferred();

            // return currently modified data or a freshly loaded node
            if (isSelectedNode && pageDetailViewObjects.customParams.useDirtyData === true) {

                console.debug("use dirty data");
                // get dirty data from proxy
                def.resolve(IgeActions._getData());
            } else {

                ObjectService.getNodeData(nodeId, nodeType, "false", {
                    callback: function(res) {
                        def.resolve(res);
                    },
                    errorHandler: function(message) {
                        displayErrorMessage(new Error(message));
                        def.reject();
                    }
                });
            }
            return def;
        }

        function getAddressUuids(addressTable) {
            var uuids = [];
            for (var i = 0; i < addressTable.length; i++) {
                uuids[i] = addressTable[i].uuid;
            }
            return uuids;
        }

        function enrichNodeDataWithInstitutions(nodeData) {
            var def = new Deferred();
            var uuids = getAddressUuids(nodeData.generalAddressTable);

            AddressService.getAddressInstitutions(uuids, {
                callback: function(res) {
                    console.debug("enrich node");
                    for (var i = 0; i < res.length; i++) {
                        nodeData.generalAddressTable[i].organisation = res[i];
                    }
                    //renderNodeData(nodeData);
                    def.resolve(nodeData);
                },
                errorHandler: function(message) {
                    displayErrorMessage(new Error(message));
                }
            });
            return def;
        }

        function renderNodeData(nodeData) {
            // a deferred to keep in sync with other nodes to print
            console.debug("renderNodeData");
            var defMain = new Deferred();
            var additionalFields = nodeData.additionalFields;

            renderObjectTitel(nodeData.objectName);

            renderText(nodeData.generalShortDescription);
            renderTextWithTitle(UtilSyslist.getSyslistEntryName(8000, nodeData.objectClass), "<fmt:message key='ui.obj.header.objectClass' />");
            renderTextWithTitle(removeEvilTags(nodeData.generalDescription), "<fmt:message key='ui.obj.general.description' />");
            // addresses
            renderAddressList(nodeData.generalAddressTable);
            // preview image
            var previewImageUrl = IgeEvents._filterPreviewImage(nodeData.linksToUrlTable);
            renderTextWithTitle(previewImageUrl, "<fmt:message key='ui.obj.general.previewImage' />");

            renderTextWithTitle(nodeData.inspireRelevant ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", "<fmt:message key='ui.obj.general.inspireRelevant' />");
            renderTextWithTitle(nodeData.openData ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", "<fmt:message key='ui.obj.general.openData' />");
            renderList(nodeData.openDataCategories, "<fmt:message key='ui.obj.general.categoriesOpenData' />");

            renderAdditionalFieldsForRubric("general", additionalFields);

            var def = new Deferred();

            // add superior elements if checkbox is selected
            var renderSupSubObjects = registry.byId("showSubordinateObjects").checked;
            if (renderSupSubObjects) {
                if (pageDetailViewObjects.resultView) {
                    def = renderSuperiorElementsRemoteCall(nodeData.uuid);
                } else {
                    var superiorElements = renderSuperiorElements(nodeData.uuid, true);
                    def.resolve(superiorElements);
                    //renderList(superiorElements.split("@@"), "<fmt:message key='ui.obj.general.superior.objects' />");
                }
                def = def.then(function(superiorElements) {
                    renderList(superiorElements.split("@@"), "<fmt:message key='ui.obj.general.superior.objects' />");
                })
                .then(lang.hitch(pageDetailViewObjects, function() {
                    var defSubordinate = new Deferred();
                    // add subordinated elements
                    if (pageDetailViewObjects.resultView) {
                        defSubordinate = renderSubordinatedElementsRemoteCall(nodeData);
                        //defRemote.then(function() {defSubordinate.resolve();});
                    } else {
                        var res = renderSubordinatedElements(nodeData.uuid);
                        defSubordinate.resolve(res);
                    }
                    return defSubordinate;
                }))
                .then(lang.hitch(pageDetailViewObjects, function(subordinateElements) {
                    var sortedList = subordinateElements.split("@@").sort(function(a, b) {
                        return UtilString.compareIgnoreCase(a, b);
                    });
                    renderList(sortedList, "<fmt:message key='ui.obj.general.subordinated.objects' />");
                }));
            } else {
                def.resolve();
            }

            def.then(lang.hitch(pageDetailViewObjects, function() {
                // define date conversion renderer function
                function formatDate(val) {
                    if (typeof val == "undefined" || val === null || val == "") {
                        return "";
                    }
                    return dojo.date.locale.format(val, {
                        selector: "date",
                        datePattern: "dd.MM.yyyy"
                    });
                }

                // technical domains
                if (nodeData.objectClass == 1) {
                    renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
                    // Objekt, Karte
                    renderTextWithTitle(nodeData.ref1BasisText, "<fmt:message key='ui.obj.type1.technicalBasisTable.title' />");
                    renderTextWithTitle(nodeData.ref1ObjectIdentifier, "<fmt:message key='ui.obj.type1.identifier' />");
                    renderTextWithTitle(UtilSyslist.getSyslistEntryName(525, nodeData.ref1DataSet), "<fmt:message key='ui.obj.type1.dataset' />");
                    renderList(nodeData.ref1Representation, "<fmt:message key='ui.obj.type1.digitalRepresentation' />", null, function(val) {
                        return UtilSyslist.getSyslistEntryName(526, val);
                    });
                    renderTextWithTitle(UtilSyslist.getSyslistEntryName(528, nodeData.ref1VFormatTopology), "<fmt:message key='ui.obj.type1.vectorFormat.topology' />");
                    renderTable(nodeData.ref1VFormatDetails, ["geometryType", "numElements"], ["<fmt:message key='ui.obj.type1.vectorFormat.detailsTable.header.geoType' />", "<fmt:message key='ui.obj.type1.vectorFormat.detailsTable.header.elementCount' />"], "<fmt:message key='ui.obj.type1.vectorFormat.title' />", [
                        function(val) {
                            return UtilSyslist.getSyslistEntryName(515, val);
                        },
                        null
                    ]);
                    // NOTICE: moved to general section "Raumbezug"
                    //renderTextWithTitle(nodeData.ref1SpatialSystem, "<fmt:message key='ui.obj.type1.spatialSystem' />");
                    renderTable(nodeData.ref1Scale, ["scale", "groundResolution", "scanResolution"], ["<fmt:message key='ui.obj.type1.scaleTable.header.scale' />", "<fmt:message key='ui.obj.type1.scaleTable.header.groundResolution' />", "<fmt:message key='ui.obj.type1.scaleTable.header.scanResolution' />"], "<fmt:message key='ui.obj.type1.scaleTable.title' />");
                    renderTable(nodeData.ref1SymbolsText, ["title", "date", "version"], ["<fmt:message key='ui.obj.type1.symbolCatTable.header.title' />", "<fmt:message key='ui.obj.type1.symbolCatTable.header.date' />", "<fmt:message key='ui.obj.type1.symbolCatTable.header.version' />"], "<fmt:message key='ui.obj.type1.symbolCatTable.title' />", [null, formatDate, null]);
                    renderTable(nodeData.ref1KeysText, ["title", "date", "version"], ["<fmt:message key='ui.obj.type1.keyCatTable.header.title' />", "<fmt:message key='ui.obj.type1.keyCatTable.header.date' />", "<fmt:message key='ui.obj.type1.keyCatTable.header.version' />"], "<fmt:message key='ui.obj.type1.keyCatTable.title' />", [null, formatDate, null]);
                    renderList(nodeData.ref1Data, "<fmt:message key='ui.obj.type1.attributes' />");
                    renderTextWithTitle(nodeData.ref1DataBasisText, "<fmt:message key='ui.obj.type1.dataBasisTable.title' />");
                    renderTextWithTitle(nodeData.ref1ProcessText, "<fmt:message key='ui.obj.type1.processTable.title' />");

                    renderAdditionalFieldsForRubric("refClass1", additionalFields);

                    // DQ
                    renderSectionTitel("<fmt:message key='ui.obj.dq' />");
                    renderTextWithTitle(nodeData.ref1Coverage, "<fmt:message key='ui.obj.type1.coverage' />" + " [%]");
                    renderTextWithTitle(nodeData.ref1AltAccuracy, "<fmt:message key='ui.obj.type1.sizeAccuracy' />");
                    renderTextWithTitle(nodeData.ref1PosAccuracy, "<fmt:message key='ui.obj.type1.posAccuracy' />");
                    renderTable(nodeData.dq109Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table109.title' />");
                    renderTable(nodeData.dq112Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table112.title' />");
                    renderTable(nodeData.dq113Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table113.title' />");
                    renderTable(nodeData.dq114Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table114.title' />");
                    renderTable(nodeData.dq115Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table115.title' />");
                    renderTable(nodeData.dq120Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table120.title' />");
                    renderTable(nodeData.dq125Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table125.title' />");
                    renderTable(nodeData.dq126Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table126.title' />");
                    renderTable(nodeData.dq127Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table127.title' />");

                    renderAdditionalFieldsForRubric("refClass1DQ", additionalFields);
                } else if (nodeData.objectClass == 2) {
                    renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
                    // Literature
                    renderTextWithTitle(nodeData.ref2Author, "<fmt:message key='ui.obj.type2.author' />");
                    renderTextWithTitle(nodeData.ref2Publisher, "<fmt:message key='ui.obj.type2.editor' />");
                    renderTextWithTitle(nodeData.ref2PublishedIn, "<fmt:message key='ui.obj.type2.publishedIn' />");
                    renderTextWithTitle(nodeData.ref2PublishLocation, "<fmt:message key='ui.obj.type2.publishedLocation' />");
                    renderTextWithTitle(nodeData.ref2PublishedInIssue, "<fmt:message key='ui.obj.type2.issue' />");
                    renderTextWithTitle(nodeData.ref2PublishedInPages, "<fmt:message key='ui.obj.type2.pages' />");
                    renderTextWithTitle(nodeData.ref2PublishedInYear, "<fmt:message key='ui.obj.type2.publishedYear' />");
                    renderTextWithTitle(nodeData.ref2PublishedISBN, "<fmt:message key='ui.obj.type2.isbn' />");
                    renderTextWithTitle(nodeData.ref2PublishedPublisher, "<fmt:message key='ui.obj.type2.publisher' />");
                    renderTextWithTitle(nodeData.ref2LocationText, "<fmt:message key='ui.obj.type2.locationTable.title' />");
                    renderTextWithTitle(nodeData.ref2DocumentType, "<fmt:message key='ui.obj.type2.documentType' />");
                    renderTextWithTitle(nodeData.ref2BaseDataText, "<fmt:message key='ui.obj.type2.generalDataTable.title' />");
                    renderTextWithTitle(nodeData.ref2BibData, "<fmt:message key='ui.obj.type2.additionalBibInfo' />");
                    renderTextWithTitle(nodeData.ref2Explanation, "<fmt:message key='ui.obj.type2.description' />");

                    renderAdditionalFieldsForRubric("refClass2", additionalFields);
                } else if (nodeData.objectClass == 3) {
                    renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
                    // Geodatendienst
                    renderList(nodeData.ref3ServiceTypeTable, "<fmt:message key='ui.obj.type3.ref3ServiceTypeTable.title' />", null, function(val) {
                        return UtilSyslist.getSyslistEntryName(5200, val);
                    });
                    renderTextWithTitle(UtilSyslist.getSyslistEntryName(5100, nodeData.ref3ServiceType), "<fmt:message key='ui.obj.type3.serviceType' />");
                    renderTextWithTitle(nodeData.ref3CouplingType, "<fmt:message key='ui.obj.type3.couplingType' />");
                    renderList(nodeData.ref3ServiceVersion, "<fmt:message key='ui.obj.type3.serviceVersion' />");
                    renderTextWithTitle(nodeData.ref3SystemEnv, "<fmt:message key='ui.obj.type3.environment' />");
                    renderTextWithTitle(nodeData.ref3History, "<fmt:message key='ui.obj.type3.history' />");
                    renderTextWithTitle(nodeData.ref3BaseDataText, "<fmt:message key='ui.obj.type3.generalDataTable.title' />" + " (" + "<fmt:message key='ui.obj.type3.generalDataTable.tab.text' />" + ")");
                    renderTextWithTitle(nodeData.ref3Explanation, "<fmt:message key='ui.obj.type3.description' />");
                    renderOperations(nodeData.ref3Operation);
                    renderTable(nodeData.ref3Scale, ["scale", "groundResolution", "scanResolution"], ["<fmt:message key='ui.obj.type1.scaleTable.header.scale' />", "<fmt:message key='ui.obj.type1.scaleTable.header.groundResolution' />", "<fmt:message key='ui.obj.type1.scaleTable.header.scanResolution' />"], "<fmt:message key='ui.obj.type3.scaleTable.title' />");
                    renderTextWithTitle(nodeData.ref3HasAccessConstraint ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", "<fmt:message key='ui.obj.type3.ref3HasAccessConstraint' />");

                    renderAdditionalFieldsForRubric("refClass3", additionalFields);
                } else if (nodeData.objectClass == 4) {
                    renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
                    // Vorhaben
                    renderTextWithTitle(nodeData.ref4ParticipantsText, "<fmt:message key='ui.obj.type4.participantsTable.title' />");
                    renderTextWithTitle(nodeData.ref4PMText, "<fmt:message key='ui.obj.type4.projectManagerTable.title' />");
                    renderTextWithTitle(nodeData.ref4Explanation, "<fmt:message key='ui.obj.type4.description' />");

                    renderAdditionalFieldsForRubric("refClass4", additionalFields);
                } else if (nodeData.objectClass == 5) {
                    renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
                    // Datensammlung/Datenbank
                    renderTable(nodeData.ref5KeysText, ["title", "date", "version"], ["<fmt:message key='ui.obj.type5.keyCatTable.header.title' />", "<fmt:message key='ui.obj.type5.keyCatTable.header.date' />", "<fmt:message key='ui.obj.type5.keyCatTable.header.version' />"], "<fmt:message key='ui.obj.type5.keyCatTable.title' />", [null, formatDate, null]);
                    renderTable(nodeData.ref5dbContent, ["parameter", "additionalData"], ["<fmt:message key='ui.obj.type5.contentTable.header.parameter' />", "<fmt:message key='ui.obj.type5.contentTable.header.additionalData' />"], "<fmt:message key='ui.obj.type5.contentTable.header.additionalData' />");
                    renderTextWithTitle(nodeData.ref5MethodText, "<fmt:message key='ui.obj.type5.methodTable.title' />");
                    renderTextWithTitle(nodeData.ref5Explanation, "<fmt:message key='ui.obj.type5.description' />");

                    renderAdditionalFieldsForRubric("refClass5", additionalFields);
                } else if (nodeData.objectClass == 6) {
                    renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
                    // Dienst/Anwendung/Informationssystem
                    renderTextWithTitle(UtilSyslist.getSyslistEntryName(5300, nodeData.ref6ServiceType), "<fmt:message key='ui.obj.type6.serviceType' />");
                    renderList(nodeData.ref6ServiceVersion, "<fmt:message key='ui.obj.type6.serviceVersion' />");
                    renderTextWithTitle(nodeData.ref6SystemEnv, "<fmt:message key='ui.obj.type6.environment' />");
                    renderTextWithTitle(nodeData.ref6History, "<fmt:message key='ui.obj.type6.history' />");
                    renderTextWithTitle(nodeData.ref6BaseDataText, "<fmt:message key='ui.obj.type6.generalDataTable.title' />" + " (" + "<fmt:message key='ui.obj.type6.generalDataTable.tab.text' />" + ")");
                    renderTextWithTitle(nodeData.ref6Explanation, "<fmt:message key='ui.obj.type6.description' />");
                    renderTable(nodeData.ref6UrlList, ["name", "url", "urlDescription"], ["<fmt:message key='ui.obj.type6.urlList.header.name' />", "<fmt:message key='ui.obj.type6.urlList.header.url' />", "<fmt:message key='ui.obj.type6.urlList.header.urlDescription' />"], "<fmt:message key='ui.obj.type6.urlList' />");

                    renderAdditionalFieldsForRubric("refClass6", additionalFields);
                }

                // spatial reference
                renderSectionTitel("<fmt:message key='ui.obj.spatial.title' />");
                UtilList.addSNSLocationLabels(nodeData.spatialRefAdminUnitTable);
                renderTable(nodeData.spatialRefAdminUnitTable, ["label", "nativeKey", "longitude1", "latitude1", "longitude2", "latitude2"], ["<fmt:message key='ui.obj.spatial.geoThesTable.header.name' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.nativeKey' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.longitude1' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.latitude1' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.longitude2' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.latitude2' />"], "<fmt:message key='dialog.compare.object.spatialTable.title' />");
                renderTable(nodeData.spatialRefLocationTable, ["name", "longitude1", "latitude1", "longitude2", "latitude2"], ["<fmt:message key='ui.obj.spatial.geoTable.header.name' />", "<fmt:message key='ui.obj.spatial.geoTable.header.longitude1' />", "<fmt:message key='ui.obj.spatial.geoTable.header.latitude1' />", "<fmt:message key='ui.obj.spatial.geoTable.header.longitude2' />", "<fmt:message key='ui.obj.spatial.geoTable.header.latitude2' />"], "<fmt:message key='ui.obj.spatial.geoTable.title' />");

                // NOTICE: moved from class 1 to general "Raumbezug"
                //renderTextWithTitle(nodeData.ref1SpatialSystem, "<fmt:message key='ui.obj.type1.spatialSystem' />");
                renderList(nodeData.ref1SpatialSystemTable, "<fmt:message key='ui.obj.type1.spatialSystem' />");

                // create cell render functions
                function lookupSpatialRefAltMeasure(val) {
                    return UtilSyslist.getSyslistEntryName(102, val);
                }

                function lookupSpatialRefAltVDate(val) {
                    return UtilSyslist.getSyslistEntryName(101, val);
                }

                var altitudeData = []; // empty list means no rendering!
                if (nodeData.spatialRefAltMin || nodeData.spatialRefAltMax || lookupSpatialRefAltMeasure(nodeData.spatialRefAltMeasure) || lookupSpatialRefAltVDate(nodeData.spatialRefAltVDate)) {
                    altitudeData = [nodeData]; // add nodeData to the list so that it's rendered with values from it
                }
                renderTable(altitudeData, ["spatialRefAltMin", "spatialRefAltMax", "spatialRefAltMeasure", "spatialRefAltVDate"], ["<fmt:message key='ui.obj.spatial.height.min' />", "<fmt:message key='ui.obj.spatial.height.max' />", "<fmt:message key='ui.obj.spatial.height.unit' />", "<fmt:message key='ui.obj.spatial.height.geodeticSystem' />"], "<fmt:message key='ui.obj.spatial.height' />", [null, null, lookupSpatialRefAltMeasure, lookupSpatialRefAltVDate]);
                renderTextWithTitle(nodeData.spatialRefExplanation, "<fmt:message key='ui.obj.spatial.description' />");

                renderAdditionalFieldsForRubric("spatialRef", additionalFields);

                // temporal reference
                renderSectionTitel("<fmt:message key='ui.obj.time.title' />");
                var timeRefTxt;
                if (nodeData.timeRefDate1) {
                    if (nodeData.timeRefType && nodeData.timeRefType == "von") {
                        timeRefTxt = "von " + formatDate(nodeData.timeRefDate1) + " bis " + formatDate(nodeData.timeRefDate2);
                    } else if (nodeData.timeRefType) {
                        timeRefTxt = nodeData.timeRefType + " " + formatDate(nodeData.timeRefDate1);
                    }
                }

                renderTextWithTitle(timeRefTxt, "<fmt:message key='ui.obj.time.timeRefContent' />");
                renderTextWithTitle(UtilSyslist.getSyslistEntryName(523, nodeData.timeRefStatus), "<fmt:message key='ui.obj.time.state' />");
                renderTextWithTitle(UtilSyslist.getSyslistEntryName(518, nodeData.timeRefPeriodicity), "<fmt:message key='ui.obj.time.periodicity' />");
                if (nodeData.timeRefIntervalNum && nodeData.timeRefIntervalUnit) {
                    // Do NOT use selectedResult[...] !!! selectedResult IS NULL !!! only not null if once selected by user interaction !!!  
                    // renderTextWithTitle("<fmt:message key='ui.obj.time.interval.each' />" + " "+nodeData.timeRefIntervalNum+" "+dojo.widget.byId("timeRefIntervalUnit").selectedResult[0], "<fmt:message key='ui.obj.time.interval' />"); //_getDisplayValueForValue(nodeData.timeRefIntervalUnit)
                    renderTextWithTitle("<fmt:message key='ui.obj.time.interval.each' />" + " " + nodeData.timeRefIntervalNum + " " + nodeData.timeRefIntervalUnit, "<fmt:message key='ui.obj.time.interval' />");
                }
                // create cell render functions
                function lookupTimeRefType(val) {
                    return UtilSyslist.getSyslistEntryName(502, val);
                }
                renderTable(nodeData.timeRefTable, ["date", "type"], ["<fmt:message key='ui.obj.time.timeRefTable.header.date' />", "<fmt:message key='ui.obj.time.timeRefTable.header.type' />"], "<fmt:message key='ui.obj.time.timeRefTable.title' />", [formatDate, lookupTimeRefType]);
                renderTextWithTitle(nodeData.timeRefExplanation, "<fmt:message key='ui.obj.time.description' />");

                renderAdditionalFieldsForRubric("timeRef", additionalFields);

                // additional information
                renderSectionTitel("<fmt:message key='ui.obj.additionalInfo.title' />");
                renderTextWithTitle(UtilSyslist.getSyslistEntryName(99999999, nodeData.extraInfoLangMetaDataCode), "<fmt:message key='ui.obj.additionalInfo.language.metadata' />");
                renderTextWithTitle(UtilSyslist.getSyslistEntryName(99999999, nodeData.extraInfoLangDataCode), "<fmt:message key='ui.obj.additionalInfo.language.data' />");
                renderTextWithTitle(UtilSyslist.getSyslistEntryName(3571, nodeData.extraInfoPublishArea), "<fmt:message key='ui.obj.additionalInfo.publicationCondition' />");
                //!!!renderTextWithTitle(registry.byId("extraInfoCharSetData").get("displayedValue"), "<fmt:message key='ui.obj.additionalInfo.charSet.data' />");
                // Table is only displayed for object classes 1 and 3
                if (nodeData.objectClass == 1 || nodeData.objectClass == 3) {
                    renderTable(nodeData.extraInfoConformityTable, ["specification", "level"], ["<fmt:message key='ui.obj.additionalInfo.conformityTable.header.specification' />", "<fmt:message key='ui.obj.additionalInfo.conformityTable.header.level' />"],
                        "<fmt:message key='ui.obj.additionalInfo.conformityTable.title' />", [
                            function(val) {
                                return UtilSyslist.getSyslistEntryName(6005, val);
                            },
                            function(val) {
                                return UtilSyslist.getSyslistEntryName(6000, val);
                            }
                        ]);
                }
                renderList(nodeData.extraInfoXMLExportTable, "<fmt:message key='ui.obj.additionalInfo.xmlExportCriteria' />");
                renderList(nodeData.extraInfoLegalBasicsTable, "<fmt:message key='ui.obj.additionalInfo.legalBasis' />");
                renderTextWithTitle(nodeData.extraInfoPurpose, "<fmt:message key='ui.obj.additionalInfo.purpose' />");
                renderTextWithTitle(nodeData.extraInfoUse, "<fmt:message key='ui.obj.additionalInfo.suitability' />");

                renderAdditionalFieldsForRubric("extraInfo", additionalFields);

                // availability
                renderSectionTitel("<fmt:message key='ui.obj.availability.title' />");
                renderList(nodeData.availabilityAccessConstraints, "<fmt:message key='ui.obj.availability.accessConstraints' />", null, function(val) {
                    return UtilSyslist.getSyslistEntryName(6010, val);
                });
                renderTextWithTitle(nodeData.availabilityUseConstraints, "<fmt:message key='ui.obj.availability.useConstraints' />");
                renderTextWithTitle(UtilSyslist.getSyslistEntryName(6300, nodeData.availabilityDataFormatInspire), "<fmt:message key='ui.obj.availability.dataFormatInspire' />");
                renderTable(nodeData.availabilityDataFormatTable, ["name", "version", "compression", "pixelDepth"], ["<fmt:message key='ui.obj.availability.dataFormatTable.header.name' />", "<fmt:message key='ui.obj.availability.dataFormatTable.header.version' />", "<fmt:message key='ui.obj.availability.dataFormatTable.header.compression' />", "<fmt:message key='ui.obj.availability.dataFormatTable.header.depth' />"], "<fmt:message key='ui.obj.availability.dataFormatTable.title' />", [
                    function(val) {
                        return UtilSyslist.getSyslistEntryName(1320, val);
                    },
                    null, null, null
                ]);
                renderTable(nodeData.availabilityMediaOptionsTable, ["name", "transferSize", "location"], ["<fmt:message key='ui.obj.availability.mediaOptionTable.header.type' />", "<fmt:message key='ui.obj.availability.mediaOptionTable.header.amount' />", "<fmt:message key='ui.obj.availability.mediaOptionTable.header.location' />"], "<fmt:message key='ui.obj.availability.mediaOptionTable.title' />", [
                    function(val) {
                        return UtilSyslist.getSyslistEntryName(520, val);
                    },
                    null, null
                ]);
                renderTextWithTitle(nodeData.availabilityOrderInfo, "<fmt:message key='ui.obj.availability.orderInfo' />");

                renderAdditionalFieldsForRubric("availability", additionalFields);

                // indexing
                renderSectionTitel("<fmt:message key='ui.obj.thesaurus.title' />");
                var sortedList = nodeData.thesaurusTermsTable.sort(function(a, b) {
                    return UtilString.compareIgnoreCase(a.title, b.title);
                });
                renderList(sortedList, "<fmt:message key='ui.adr.thesaurus.terms' />", "title");
                sortedList = nodeData.thesaurusTopicsList.sort(function(a, b) {
                    return UtilString.compareIgnoreCase(UtilSyslist.getSyslistEntryName(527, a), UtilSyslist.getSyslistEntryName(527, b));
                });
                renderList(sortedList, "<fmt:message key='ui.obj.thesaurus.terms.category' />", null, function(val) {
                    return UtilSyslist.getSyslistEntryName(527, val);
                });
                sortedList = nodeData.thesaurusInspireTermsList.sort(function(a, b) {
                    return UtilString.compareIgnoreCase(UtilSyslist.getSyslistEntryName(6100, a), UtilSyslist.getSyslistEntryName(6100, b));
                });
                renderList(sortedList, "<fmt:message key='ui.obj.thesaurus.terms.inspire' />", null, function(val) {
                    return UtilSyslist.getSyslistEntryName(6100, val);
                });
                renderTextWithTitle(nodeData.thesaurusEnvExtRes ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", "<fmt:message key='ui.obj.thesaurus.terms.enviromental.displayCatalogPage' />");
                renderList(nodeData.thesaurusEnvTopicsList, "<fmt:message key='ui.obj.thesaurus.terms.enviromental.title' />" + " - " + "<fmt:message key='ui.obj.thesaurus.terms.enviromental.topics' />", null, function(val) {
                    return UtilSyslist.getSyslistEntryName(1410, val);
                });

                renderAdditionalFieldsForRubric("thesaurus", additionalFields);

                // references
                if (nodeData.linksFromObjectTable.length > 0 || nodeData.linksToObjectTable.length > 0 || nodeData.linksToUrlTable.length > 0) {
                    renderSectionTitel("<fmt:message key='ui.obj.links.title' />");
                    sortedList = nodeData.linksFromObjectTable.sort(function(a, b) {
                        return (b.title < a.title) - (a.title < b.title);
                    });
                    renderList(sortedList, "<fmt:message key='dialog.compare.object.linksFromTable.title' />", "title");
                    sortedList = nodeData.linksToObjectTable.sort(function(a, b) {
                        return (b.title < a.title) - (a.title < b.title);
                    });
                    renderLinkList(sortedList, "<fmt:message key='dialog.compare.object.linksToTable.title' />", false);
                    renderLinkList(nodeData.linksToUrlTable, "<fmt:message key='dialog.compare.object.linksToUrlTable.title' />", true);
                }

                renderAdditionalFieldsForRubric("links", additionalFields);

                renderAdditionalRubrics(additionalFields);
                console.debug(nodeData);

                // administrative data
                renderSectionTitel("<fmt:message key='dialog.compare.object.administrative' />");
                var objId = nodeData.orgObjId ? nodeData.orgObjId : nodeData.uuid;
                renderTextWithTitle(objId, "<fmt:message key='dialog.compare.object.id' />");
                renderTextWithTitle(UtilCatalog.catalogData.catalogName, "<fmt:message key='dialog.compare.object.catalog' />");

                // additional fields
                /*var addFields = nodeData.additionalFields;
                if (addFields.length > 0) {
                    renderSectionTitel("<fmt:message key='ui.obj.additionalFields.title' />");
                    for(var i=0; i<addFields.length; i++) {
                        renderTextWithTitle(addFields[i].value, addFields[i].name);
                    }
                }*/

                // modification time
                renderText("<fmt:message key='ui.obj.header.modificationTime' />" + ": " + nodeData.modificationTime);

                defMain.resolve();
            }));

            return defMain;
        }

        function renderAdditionalFieldsForRubric(rubric, nodeData) {
            //console.debug("add additional fields info of rubric: " + rubric);
            var addDomWidgets = query(".additionalField", rubric);
            array.forEach(addDomWidgets, function(domWidget) {
                var widgetId = domWidget.getAttribute("widgetId");
                if (!widgetId) widgetId = domWidget.id;
                var label = searchLabelFrom(domWidget);
                var data = getValueFromAdditional(widgetId, nodeData);
                if (data) {
                    // if it is a table
                    if (typeof(data) == "object") {
                        if (data.length > 0) {
                            var columnFields = getColumnFields(widgetId);
                            var columnNames = getColumnNames(widgetId);
                            var formatters = getColumnFormatters(widgetId);
                            renderTable(data, columnFields, columnNames, label, formatters, true, UtilGrid.getTable(widgetId).getColumns());
                        }
                    } else {
                        renderTextWithTitle(data, label);
                    }
                }
            }, this);
        }

        function getValueFromAdditional(id, additionalFields) {
            var result = null;
            array.some(additionalFields, function(field) {
                //console.debug("check: " + id + " with "+field.identifier);
                if (field.identifier === id) {
                    // special handling of tables
                    if (field.tableRows !== null) {
                        result = prepareAdditionalTable(field.tableRows);
                        return true;
                    } else if (field.value !== null) {
                        result = field.value + getUnitFromField(id);
                        return true;
                    }
                }
            }, this);
            if (result == "true") result = "<fmt:message key='general.yes' />";
            if (result == "false") result = "<fmt:message key='general.no' />";
            return result;
        }

        function getUnitFromField(id) {
            var widget = registry.byId(id);
            var unit = widget ? widget.domNode.getAttribute("unit") : null;
            if (unit)
                return " " + unit;
            else
                return "";
        }

        function getColumnFields(id) {
            return array.map(UtilGrid.getTable(id).getColumns(), function(col) {
                return col.field;
            });
        }

        function getColumnNames(id) {
            return dojo.map(UtilGrid.getTable(id).getColumns(), function(col) {
                return col.name;
            });
        }
        function getColumnFormatters(id) {
            return dojo.map(UtilGrid.getTable(id).getColumns(), function(col) {
                return col.formatter;
            });
        }

        function prepareAdditionalTable(rows) {
            var tableList = [];
            array.forEach(rows, function(row) {
                if (row.length === 0) return; // empty table
                var item = {};
                array.forEach(row, function(rowItem) {
                    item[rowItem.identifier] = rowItem.value ? rowItem.value : ""; // TODO: listIds? mapping?
                });
                tableList.push(item);
            });
            return tableList;

        }

        function searchLabelFrom(element) {
            while (!domClass.contains(element, "input") && !domClass.contains(element, "dijitCheckBox")) {
                element = element.parentNode;
            }
            if (domClass.contains(element, "dijitCheckBox")) {
                element = element.nextSibling;
            } else {
                element = element.previousSibling;

                while (!domClass.contains(element, "label")) {
                    element = element.previousSibling;
                }
            }

            if (element !== null) {
                var text = dojo.isFF ? element.textContent : element.innerText;
                if (text[text.length - 1] === "*")
                    return text.slice(0, -1);
                return text;
            } else
                return "???";
        }

        function renderAdditionalRubrics(additionalFields) {
            var addDomRubrics = query(".rubric.additional", "contentFrameBodyObject");
            array.forEach(addDomRubrics, function(domRubric) {
                var rubricText = has("firefox") ? domRubric.children[0].textContent : domRubric.children[0].innerText;
                renderSectionTitel(rubricText);
                renderAdditionalFieldsForRubric(domRubric.id, additionalFields);
            }, this);
        }

        function renderYesNo(val) {
            if (val == 1) {
                return "<fmt:message key='general.yes' />";
            } else {
                return "<fmt:message key='general.no' />";
            }
        }

        function renderSuperiorElements(nodeUuid, ignoreFirstNode) {
            var node = pageDetailViewObjects.mappedNodes[nodeUuid];
            if (nodeUuid == "objectRoot") {
                return "";
            } else if (!node || !node.parent) {
                // leave selected subtree and find parents in the rendered tree
                var treeNode = UtilTree.getNodeById("dataTree", nodeUuid);
                if (ignoreFirstNode)
                    return renderSuperiorElements(treeNode.getParent().item.id);
                else
                    return renderSuperiorElements(treeNode.getParent().item.id) + "@@" + treeNode.label;
            } else {
                if (ignoreFirstNode)
                    return renderSuperiorElements(node.parent);
                else
                    return renderSuperiorElements(node.parent) + "@@" + node.title;
            }
        }

        function renderSuperiorElementsRemoteCall(nodeUuid) {
            var def = new Deferred();
            ObjectService.getPathToObject(nodeUuid, function(path) {
                // remove last element which is the node itself!
                path.pop();
                var titlePath = "";
                var defNodeTitle = new Deferred();
                defNodeTitle.resolve();
                array.forEach(path, function(uuid) {
                    //var node = pageDetailViewObjects.mappedNodes[uuid];
                    // load node if not yet already done
                    //if (!node) {
                    defNodeTitle = defNodeTitle.then(lang.partial(getNodeData, uuid, "O"))
                    .then(function(nodeDataOther) {
                        titlePath += "@@" + nodeDataOther.title;
                    });
                    //}
                });
                defNodeTitle.then(function() {
                    def.resolve(titlePath);
                });
            });
            return def;
        }


        function renderSubordinatedElements(nodeUuid) {
            var node = pageDetailViewObjects.mappedNodes[nodeUuid];

            var subList = "";

            if (node.children) {
                array.forEach(node.children, function(child) {
                    //console.debug("Got Children: " + children[i].title);
                    subList = child.title + "@@" + subList;
                    //console.debug("return list: " + subList);

                    if (child.children) {
                        subList = renderSubordinatedElements(child.id) + "@@" + subList;
                    }

                    return subList;
                });
            }

            return subList;
        }

        function renderSubordinatedElementsRemoteCall(nodeData) {
            var defMain = new Deferred();

            var subList = "";
            getAllChildrenRecursively(nodeData)
            .then(function(children) {
                array.forEach(children, function(child) {
                    subList = child.title + "@@" + subList;
                });
                defMain.resolve(subList);
            });

            return defMain;
        }

        function renderOperations(list) {
            renderTable(list, ["name", "addressList"], ["<fmt:message key='ui.obj.type3.operationTable.header.name' />", "<fmt:message key='ui.obj.type3.operationTable.header.address' />"], "<fmt:message key='ui.obj.type3.operationTable.title' />", [null, renderFirstElement]);
            for (var i = 0; i < list.length; i++) {
                var op = list[i];
                renderTextWithTitle(op.name, "<fmt:message key='dialog.operation.opName' />");
                renderTextWithTitle(op.description, "<fmt:message key='dialog.operation.description' />");
                renderList(op.addressList, "<fmt:message key='dialog.operation.address' />");
                var sortedList = op.platform.sort(function(a, b) {
                    return UtilString.compareIgnoreCase(UtilSyslist.getSyslistEntryName(5180, a), UtilSyslist.getSyslistEntryName(5180, b));
                });
                renderList(sortedList, "<fmt:message key='dialog.operation.platforms' />", null, function(val) {
                    return UtilSyslist.getSyslistEntryName(5180, val);
                });
                renderTextWithTitle(op.methodCall, "<fmt:message key='dialog.operation.call' />");
                renderTable(op.paramList, ["name", "direction", "description", "optional", "multiple"], ["<fmt:message key='dialog.operation.name' />", "<fmt:message key='dialog.operation.direction' />", "<fmt:message key='dialog.operation.description' />", "<fmt:message key='dialog.operation.optional' />", "<fmt:message key='dialog.operation.multiplicity' />"], "<fmt:message key='dialog.operation.parameter' />", [null, null, null, renderYesNo, renderYesNo]);
                renderList(op.dependencies, "<fmt:message key='dialog.operation.dependencies' />");
            }
        }


        function renderLinkList(list, title, isUrl) {
            if (list && list.length > 0) {
                var valList = "";
                var val = null;
                for (var i = 0; i < list.length; i++) {
                    if (isUrl) {
                        val = "<a href=\"" + list[i].url + "\" target=\"new\">" + list[i].name + "</a>";
                    } else {
                        val = "" + list[i].title;
                    }
                    if (UtilString.hasValue(list[i].relationTypeName)) {
                        val += " (" + list[i].relationTypeName + ")";
                    }
                    valList += val + "<br/>";
                }
                if (valList != "") {
                    var t = "<strong>" + title + "</strong><br/>";
                    pageDetailViewObjects.detailDivContent += "<p>" + t + valList + "</p><br/>";
                }
            }
        }

        function renderObjectTitel(val) {
            pageDetailViewObjects.detailDivContent += "<h1>" + val + "</h1><br/><br/>";
        }

        function renderSectionTitel(val) {
            pageDetailViewObjects.detailDivContent += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
        }

        function renderTextWithTitle(val, title) {
            // compare with null so 0 will be different ! !oldVal handles 0 the same as null so replaces it with "" !
            if (val === null) val = "";
            val += "";
            if (detailHelper.isValid(val)) {
                // Replace newlines with <br/>
                val = val.replace(/\n/g, "<br/>");
                if (detailHelper.isValid(title)) {
                    pageDetailViewObjects.detailDivContent += "<p><strong>" + title + "</strong><br/>" + val + "</p><br/>";
                } else {
                    pageDetailViewObjects.detailDivContent += "<p>" + val + "</p><br/>";
                }
            }
        }

        function removeEvilTags(val) {
            if (val) {
                return val.replace(/<(?!b>|\/b>|i>|\/i>|u>|\/u>|p>|\/p>|br>|br\/>|br \/>|strong>|\/strong>|ul>|\/ul>|ol>|\/ol>|li>|\/li>)[^>]*>/gi, "");
            } else {
                return "";
            }
        }

        function renderAddressList(list) {
            if (list.length > 0) {
                var t = "";
                for (var i = 0; i < list.length; i++) {
                    t += "<strong>" + list[i].nameOfRelation + "</strong><br/><br/>";
                    t += "<p>";
                    t += detailHelper.renderAddressEntry(list[i]).replace(/\n/g, "<br />");
                    t += "</p><br/>";
                }
                pageDetailViewObjects.detailDivContent += t;
            }
        }

        function renderText(val) {
            // compare with null so 0 will be different ! !oldVal handles 0 the same as null so replaces it with "" !
            if (val === null) val = "";
            val += "";
            if (val && val.length > 0) {
                // Replace newlines with <br/>
                val = val.replace(/\n/g, "<br/>");
                pageDetailViewObjects.detailDivContent += "<p>" + val + "</p><br/>";
            }
        }

        function renderList(list, title, rowProperty, renderFunction) {
            if (list && list.length > 0) {
                var t = "<p>";
                if (detailHelper.isValid(title)) {
                    t += "<strong>" + title + "</strong><br/>";
                }
                var valList = "";
                for (var i = 0; i < list.length; i++) {
                    var val = "";
                    if (rowProperty) {
                        val = list[i][rowProperty];
                    } else {
                        val = list[i];
                    }
                    if (renderFunction) {
                        val = renderFunction.call(this, val);
                    }
                    if (val && val != "") {
                        valList += val + "<br/>";
                    }
                }
                if (valList != "") {
                    pageDetailViewObjects.detailDivContent += t + valList + "</p><br/>";
                }
            }
        }

        function renderTable(list, rowProperties, listHeader, title, cellRenderFunction, useGridFormatter, columns) {
            if (list && list.length > 0) {
                var t = "";
                if (detailHelper.isValid(title)) {
                    t += "<strong>" + title + "</strong><br/><br/>";
                }
                t += "<p><table class=\"filteringTable\" cellspacing=\"0\">";
                if (listHeader && listHeader.length > 0) {
                    t += "<thead class=\"fixedHeader\"><tr>";
                    for (i = 0; i < listHeader.length; i++) {
                        t += "<th style=\"padding-right:4px\">" + listHeader[i] + "</th>";
                    }
                    t += "</tr></thead>";
                }
                t += "<tbody>";
                for (var i = 0; i < list.length; i++) {
                    if (i % 2) {
                        t += "<tr class=\"alt\">";
                    } else {
                        t += "<tr>";
                    }
                    for (var j = 0; j < rowProperties.length; j++) {
                        var value = list[i][rowProperties[j]] ? list[i][rowProperties[j]] : "";
                        if (cellRenderFunction && cellRenderFunction[j]) {
                            if (useGridFormatter)
                                t += "<td style=\"padding-right:4px\">" + cellRenderFunction[j].call(this, i, j, value, columns[j]) + "</td>";
                            else
                                t += "<td style=\"padding-right:4px\">" + cellRenderFunction[j].call(this, value) + "</td>";
                        } else {
                            t += "<td style=\"padding-right:4px\">" + value + "</td>";
                        }
                    }
                    t += "</tr>";

                }
                t += "</tbody></table></p>";
                pageDetailViewObjects.detailDivContent += t + "<br/><br/>";
            }
        }

        function renderFirstElement(val) {
            var retVal = "";
            if (val && val instanceof Array && val.length > 0) {
                retVal = val[0];
            }
            return retVal;
        }

        // function findNodeInSubTree(uuid) {
        //     pageDetailViewObjects.mappedNodes;
        // }

    }
);
</script>
</head>

<body>
    <div id="contentPane" class="contentBlockWhite">
        <div id="dialogContent" class="content">
            <div id="printDialogSettings" class="grey">
                <input type="checkbox" id="showDetailedView" data-dojo-type="dijit/form/CheckBox" checked=true />
                <label for="showDetailedView" class="inActive" style="margin-right: 15px;">
                    <fmt:message key="dialog.detail.print.showDetailedView" />
                </label>
                <input type="checkbox" id="showSubTree" data-dojo-type="dijit/form/CheckBox" />
                <label for="showSubTree" class="inActive">
                    <fmt:message key="dialog.detail.print.showSubTree" />
                </label>
                <input type="checkbox" id="showSubordinateObjects" data-dojo-type="dijit/form/CheckBox" style="display:none;" checked=true />
                <label for="showSubordinateObjects" style="display:none;" class="inActive" title="<fmt:message key="dialog.detail.print.showSubordinate.tooltip" />">
                    <fmt:message key="dialog.detail.print.showSubordinate" />
                </label>
                <button id="printDetailObject" data-dojo-type="dijit/form/Button" onclick="require('ingrid/utils/General').printDivContent('detailViewContent')" class="right" style="margin: -3px 0 0 10px;"><fmt:message key="dialog.detail.print" />
                </button>
                <span id="detailLoadingZone" style="visibility:hidden;" class="processInfo right"><img src="img/ladekreis.gif" width="20" height="20" alt="Loading" /></span>
            </div>
            <!-- MAIN TAB CONTAINER START -->
            <!-- <div id="detailViewContainer" data-dojo-type="dijit/layout/TabContainer" style="height:528px; width:100%;" selectedChild="detailView"> -->
            <div style="width:100%; clear: both;">
                <!-- MAIN TAB 1 START -->
                <div id="detailView" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder" style="height: 550px;" title="<fmt:message key="dialog.detail.title" />">
                    <div id="detailViewContent" class="detailViewContentContainer" style="padding: 5px;"></div>
                </div>
                <!-- MAIN TAB 1 END -->
            </div>
            <!-- MAIN TAB CONTAINER END -->
        </div>
    </div>
  <!-- CONTENT END -->
</body>
</html>
