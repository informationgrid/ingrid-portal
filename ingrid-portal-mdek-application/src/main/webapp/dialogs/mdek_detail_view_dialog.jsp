<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<style type="text/css">
    #printDialogSettings {
        border: 1px #ccc solid;
        padding: 10px;
    }
</style>
<script type="text/javascript" src="js/detail_helper.js"></script>
<script type="text/javascript">

var scriptScopeDetailView = _container_;

dojo.connect(_container_, "onLoad", function() {
    this.detailDiv = dojo.byId("detailViewContent");
    this.detailDivContent = "";
    this.subNodesCount = 1;
    this.currentlyProcessedNode = 0;
    this.stopOperation = false;
    this.mappedNodes = {};
    
    // preview/print search results
    if (this.customParams.searchResult) {
        this.resultView = true;
        dijit.byId("showSubTree").set("checked", true);
        dijit.byId("showDetailedView").set("checked", false);
        this.refreshView();
        
    } else { // print hierarchy tree
        // determine selected node by dirty data flag
        this.selectedNode = this.customParams.useDirtyData ? dijit.byId("dataTree").selectedNode : dijit.byId(this.customParams.selectedNode);
        
        // convert tree item to a json construct
        this.nodeTreeItem = itemToJS(dijit.byId("dataTree").model.store, this.selectedNode.item);
        
        // start from selected (parent) node to create the complete subtree, which will
        // be used to render the whole detailed information or just the titles
        // this is only needed once and saves time especially when printing big trees
        console.debug("determine subtree");
        scriptScopeDetailView.setLoadingZone(true);
        var def = this.determineCompleteSubTree([this.nodeTreeItem], 0);
        
        def.addCallback( function() {
            if (scriptScopeDetailView.customParams.useDirtyData != true) {
                console.debug("load data");
                // make sure the object forms are already loaded since they are needed
                // for syslist values for example
                ingridObjectLayout.create();
            }
            scriptScopeDetailView.refreshView();
        });
    }
    
    dojo.connect(dijit.byId("showSubTree"),             "onClick", dojo.hitch(this, "refreshView"));
    dojo.connect(dijit.byId("showDetailedView"),        "onClick", dojo.hitch(this, "refreshView"));
    dojo.connect(dijit.byId("showSubordinateObjects"),  "onClick", dojo.hitch(this, "refreshView"));
});

scriptScopeDetailView.refreshView = function() {
    console.debug("refresh view");
    
    scriptScopeDetailView.updateCheckboxesFunctionality();
    scriptScopeDetailView.setLoadingZone(true);
    
    var showSubTree = dijit.byId("showSubTree").checked;
    
    // empty preview div
    scriptScopeDetailView.detailDiv.innerHTML = "";
    scriptScopeDetailView.detailDivContent    = "";
    
    // reset processing info
    scriptScopeDetailView.currentlyProcessedNode = 0;
    scriptScopeDetailView.stopOperation = false;
    
    // handle search results
    if (this.resultView) {
        this.renderSearchResults();
        
    } else { // handle tree
        if (showSubTree) {
            var def = scriptScopeDetailView.renderSubTreeNodeData();
            def.addCallback(function() { scriptScopeDetailView.detailDiv.innerHTML = scriptScopeDetailView.detailDivContent});
            def.addCallback(scriptScopeDetailView.hideProcessingDialog);
        } else {
            var def = scriptScopeDetailView.loadAndRenderTreeNode(scriptScopeDetailView.selectedNode.id[0]);
            def.addCallback(function() { scriptScopeDetailView.detailDiv.innerHTML = scriptScopeDetailView.detailDivContent});
            def.addCallback(dojo.partial(scriptScopeDetailView.setLoadingZone, false));
        }
    }
}

scriptScopeDetailView.updateCheckboxesFunctionality = function() {
    if (scriptScopeDetailView.resultView) {
        UtilUI.disableElement("showSubTree");
    } else {
        UtilUI.enableElement("showSubTree");
    }
    
    if (dijit.byId("showSubTree").checked) {
        UtilUI.enableElement("showDetailedView");
    } else {
        UtilUI.disableElement("showDetailedView");
    }
    
    if (dijit.byId("showDetailedView").checked) {
        UtilUI.enableElement("showSubordinateObjects");
    } else {
        UtilUI.disableElement("showSubordinateObjects");
    }
}

scriptScopeDetailView.renderSearchResults = function() {
    var result = this.customParams.searchResult;
    this.subNodesCount = result.numHits;
    var showDetails = dijit.byId("showDetailedView").checked;
    
    // show a warning dialog if it would take a long time to prepare the print preview
    var def = scriptScopeDetailView.showTooManyNodesDialogIfNecessary();
    def.addCallback(scriptScopeDetailView.showProcessingDialog);
    dojo.forEach(result.resultList, function(entry) {
        if (showDetails) {
            def.addCallback(dojo.hitch(this, dojo.partial(scriptScopeDetailView.loadAndRenderTreeNode, entry.uuid, entry.nodeAppType)));
        } else {
            def.addCallback(dojo.hitch(this, function() {
                scriptScopeDetailView.renderSimpleTreeNode(entry);
            }));
        }
    }, this);
    def.addCallback(function() { scriptScopeDetailView.detailDiv.innerHTML = scriptScopeDetailView.detailDivContent});
    def.addCallback(scriptScopeDetailView.hideProcessingDialog);
}

/**
 * Determine the subtree structure with all elements hierarchically ordered. This improves
 * the speed when determining all subordinate objects since no backend requests are sent anymore.
 * Also determine statistical data.
 */
scriptScopeDetailView.determineCompleteSubTree = function(nodeDataList, hierarchy) {
    if (!(nodeDataList instanceof Array)) nodeDataList = [nodeDataList];
    var allDeferredsToWaitFor = [];
    
    dojo.forEach(nodeDataList, function(nodeData) {
        scriptScopeDetailView.mappedNodes[nodeData.id] = nodeData;
        nodeData.hierarchy = hierarchy;
        if (nodeData.isFolder) {
            childrenToLoad = true;
            var waitForMe = new dojo.Deferred();
            var def = scriptScopeDetailView.getImmediateChildrenOfNode(nodeData);
            def.addCallback(function(children) {
                // remember number of children for later statistics
                scriptScopeDetailView.subNodesCount += children.length;
                // add parent information
                dojo.forEach(children, function(child) { child.parent = nodeData.id;});
                nodeData.children = children;
                var def2 = scriptScopeDetailView.determineCompleteSubTree(children, hierarchy+1);
                def2.addCallback(function() { waitForMe.callback();});
            });
            allDeferredsToWaitFor.push(waitForMe);
        }
    });
    
    // only resolve the deferred if all children have no other children!
    // this means that the next sibling in parent can be rendered
    if (allDeferredsToWaitFor.length > 0) {
        return new dojo.DeferredList(allDeferredsToWaitFor);
    } else {
        var def = new dojo.Deferred();
        def.callback();
        return def;
    }
}

scriptScopeDetailView.setLoadingZone = function(activate) {
    if (activate) {
        dojo.style("detailLoadingZone", "visibility", "visible");
        UtilUI.disableElement("showSubTree");
        UtilUI.disableElement("showDetailedView");
        UtilUI.disableHtmlLink("printDetailObject");
    } else {
        dojo.style("detailLoadingZone", "visibility", "hidden");
        UtilUI.enableElement("showSubTree");
        UtilUI.enableElement("showDetailedView");
        UtilUI.enableHtmlLink("printDetailObject");
        scriptScopeDetailView.updateCheckboxesFunctionality();
    }
}

scriptScopeDetailView.renderSubTreeNodeData = function() {
    console.debug("renderTreeNodeData");
    var showSubTree = dijit.byId("showSubTree").checked;
    var renderDetailView = dijit.byId("showDetailedView").checked;
    
    // show a warning dialog if it would take a long time to prepare the print preview
    var def = scriptScopeDetailView.showTooManyNodesDialogIfNecessary();
    
    if (renderDetailView && showSubTree)
        def.addCallback(scriptScopeDetailView.showProcessingDialog);
    
    deferredRender = new dojo.Deferred();
    deferredRender.callback();
    
    def.addCallback(dojo.partial(dojo.hitch(scriptScopeDetailView, "renderSubTree"), scriptScopeDetailView.nodeTreeItem, deferredRender));
    return def;
}

scriptScopeDetailView.showTooManyNodesDialogIfNecessary = function() {
    var def = new dojo.Deferred();
    var renderDetailView = dijit.byId("showDetailedView").checked;
    
    // count all subnodes first!
    if (renderDetailView && scriptScopeDetailView.subNodesCount > 100) {
        var displayText = dojo.string.substitute(message.get("dialog.object.detailView.dialog.warning"), [scriptScopeDetailView.subNodesCount]);
        dialog.show("<fmt:message key='general.info' />", displayText, dialog.INFO, [{
            caption: "<fmt:message key='general.cancel' />",
            action: scriptScopeDetailView.hideProcessingDialog
        }, {
            caption: "<fmt:message key='general.ok' />",
            action: function() { setTimeout(def.callback, 0); } // delay execution since error occurs when second dialog is called
        }]);
    } else {
        def.callback();
    }
    return def;
}

scriptScopeDetailView.showProcessingDialog = function() {
    var displayText = dojo.string.substitute(message.get("dialog.object.detailView.dialog.processing"), [scriptScopeDetailView.subNodesCount]);
    scriptScopeDetailView.processDialog = dialog.show("<fmt:message key='general.info' />", displayText, dialog.INFO, [{
        caption: "<fmt:message key='general.cancel' />",
        action: function(){
            scriptScopeDetailView.stopOperation = true;
        }
    }]);
}

scriptScopeDetailView.hideProcessingDialog = function() {
    if (scriptScopeDetailView.processDialog)
        scriptScopeDetailView.processDialog.hide();
    scriptScopeDetailView.setLoadingZone(false);
}

scriptScopeDetailView.processNode = function() {
    scriptScopeDetailView.currentlyProcessedNode += 1;
    //console.debug("Processed: " + scriptScopeDetailView.currentlyProcessedNode);
    if (dojo.byId("processInfoCurrent"))
        dojo.byId("processInfoCurrent").innerHTML = scriptScopeDetailView.currentlyProcessedNode; 
};

scriptScopeDetailView.renderSubTree = function(nodeDataList, deferred) {
    if (!(nodeDataList instanceof Array)) nodeDataList = [nodeDataList];
    dojo.forEach(nodeDataList, function(nodeData) {
        if (dijit.byId("showDetailedView").checked) {
            deferred.addCallback(dojo.partial(dojo.hitch(scriptScopeDetailView, "loadAndRenderTreeNode"), nodeData.id));
        } else {
            scriptScopeDetailView.renderSimpleTreeNode(nodeData);
        }
        
        // if node has children then recursively render these
        if (nodeData.isFolder) {
            scriptScopeDetailView.renderSubTree(nodeData.children, deferred);            
        }
        
    });
    return deferred;
}

scriptScopeDetailView.renderSimpleTreeNode = function(nodeData) {
    scriptScopeDetailView.processNode();
    if (!scriptScopeDetailView.stopOperation) {
        this.detailDivContent += "<div class='v_line'><div class='h_line' style='padding-left: " + (nodeData.hierarchy * 20) + "px'>&nbsp;</div><span>" + nodeData.title + "</span></div>";
    }
}

scriptScopeDetailView.loadAndRenderTreeNode = function(nodeId, nodeType) {
    // skip if the user canceled
    // since several asynchronous deferreds exists it's better to ignore execution
    if (!scriptScopeDetailView.stopOperation) {
        // update process info
        scriptScopeDetailView.processNode();
        var def = scriptScopeDetailView.getNodeData(nodeId, nodeType);
        def.addCallback(dojo.hitch(scriptScopeDetailView, "enrichNodeDataWithInstitutions"));
        def.addCallback(dojo.hitch(scriptScopeDetailView, "renderNodeData"));
        // show a horizontal line between objects if a detail subtree is listed
        def.addCallback(function() { if (dijit.byId("showSubTree").checked) scriptScopeDetailView.detailDivContent += "<hr />"; });
        return def;
    }
}

scriptScopeDetailView.getImmediateChildrenOfNode = function(nodeData) {
    var deferred = new dojo.Deferred();
    // share the deferred to keep in correct order!
    // otherwise the children of a node wouldn't be rendered before its siblings
    // -> other approach!!!
    TreeService.getSubTree(nodeData.id, nodeData.nodeAppType, userLocale,
        {
            callback:function(res) {
                deferred.callback(res);
            },
            errorHandler:function(message) {
                displayErrorMessage(new Error(message));
                deferred.errback();
            }
        }
    );
    return deferred;
}

scriptScopeDetailView.getAllChildrenRecursively = function(nodeData) {
    var deferred = new dojo.Deferred();
    TreeService.getAllSubTreeChildren(nodeData.uuid, nodeData.nodeAppType, userLocale,
        {
            callback:function(children) {
                deferred.callback(children);
            },
            errorHandler:function(message) {
                displayErrorMessage(new Error(message));
                deferred.errback();
            }
        }
    );
    return deferred;
}

scriptScopeDetailView.getNodeData = function(id, type) {
    var nodeId   = id ? id : this.selectedNode.item.id[0];
    var nodeType = type ? type : this.selectedNode.item.nodeAppType[0];
    var isSelectedNode = false;
    if (this.selectedNode && nodeId == this.selectedNode.item.id[0]) {
        isSelectedNode = true;
    }

    console.debug("getNodeData");
    var def = new dojo.Deferred();
    
    // return currently modified data or a freshly loaded node
    if (isSelectedNode && scriptScopeDetailView.customParams.useDirtyData == true) {

        console.debug("use dirty data");
        // get dirty data from proxy
        def.callback(udkDataProxy._getData());
    } else {

        ObjectService.getNodeData(nodeId, nodeType, "false",
            {
                callback:function(res) {
                    def.callback(res);
                },
                errorHandler:function(message) {
                    displayErrorMessage(new Error(message));
                    def.errback();
                }
            }
        );
    }
    return def;
}

scriptScopeDetailView.getAddressUuids = function(addressTable){
    var uuids = new Array();
    for (var i = 0; i < addressTable.length; i++) {
        uuids[i] = addressTable[i].uuid;
    }
    return uuids;
}

scriptScopeDetailView.enrichNodeDataWithInstitutions = function(nodeData) {
    var def = new dojo.Deferred();
    var uuids = this.getAddressUuids(nodeData.generalAddressTable);
    
    AddressService.getAddressInstitutions(uuids, {
        callback:function(res){ 
            console.debug("enrich node");
            for (var i = 0; i < res.length; i++) {
                nodeData.generalAddressTable[i].organisation = res[i];
            }
            //renderNodeData(nodeData);
            def.callback(nodeData);
        },
        errorHandler:function(message) {
            displayErrorMessage(new Error(message));
        }
    });
    return def;
}

scriptScopeDetailView.renderNodeData = function(nodeData) {
    // a deferred to keep in sync with other nodes to print
    console.debug("renderNodeData");
    var defMain = new dojo.Deferred();
    
    var additionalFields = nodeData.additionalFields;
    
	this.renderObjectTitel(nodeData.objectName);

	this.renderText(nodeData.generalShortDescription);
	this.renderTextWithTitle(message.get("dialog.research.ext.obj.class"+nodeData.objectClass), "<fmt:message key='ui.obj.header.objectClass' />");
	this.renderTextWithTitle(this.removeEvilTags(nodeData.generalDescription), "<fmt:message key='ui.obj.general.description' />");
	// addresses
	this.renderAddressList(nodeData.generalAddressTable);
    
    this.renderAdditionalFieldsForRubric("general", additionalFields);

    var def = new dojo.Deferred();
    
	// add superior elements if checkbox is selected
    var renderSupSubObjects = dijit.byId("showSubordinateObjects").checked;
    if (renderSupSubObjects) {
        if (this.resultView) {
            var def = this.renderSuperiorElementsRemoteCall(nodeData.uuid);
        } else {
        	var superiorElements = this.renderSuperiorElements(nodeData.uuid, true);
        	def.callback(superiorElements);
        	//this.renderList(superiorElements.split("@@"), "<fmt:message key='ui.obj.general.superior.objects' />");
        }
        def.addCallback(function(superiorElements) {scriptScopeDetailView.renderList(superiorElements.split("@@"), "<fmt:message key='ui.obj.general.superior.objects' />");});
    	
        def.addCallback(dojo.hitch(this, function() {
            var defSubordinate = new dojo.Deferred(); 
        	// add subordinated elements
            if (this.resultView) {
                var defSubordinate = this.renderSubordinatedElementsRemoteCall(nodeData);
                //defRemote.addCallback(function() {defSubordinate.callback();});
            } else {
                var res = this.renderSubordinatedElements(nodeData.uuid);
                defSubordinate.callback(res);
            }
            return defSubordinate;
        }));
        
        def.addCallback(dojo.hitch(this, function(subordinateElements) { 
            var sortedList = subordinateElements.split("@@").sort(function(a,b) {return UtilString.compareIgnoreCase(a,b);});
            this.renderList(sortedList, "<fmt:message key='ui.obj.general.subordinated.objects' />");
        }));
    } else {
        def.callback();
    }
		
    def.addCallback(dojo.hitch(this, function() {
		// define date conversion renderer function
		function formatDate(val) {
            if (typeof val == "undefined" || val == null || val == "") {
                return "";
            }
			return dojo.date.locale.format(val, {selector:'date', datePattern:'dd.MM.yyyy'});
		}
	
		// technical domains
		if (nodeData.objectClass == 1) {
			this.renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
			// Objekt, Karte
			this.renderTextWithTitle(nodeData.ref1BasisText, "<fmt:message key='ui.obj.type1.technicalBasisTable.title' />");
			this.renderTextWithTitle(nodeData.ref1ObjectIdentifier, "<fmt:message key='ui.obj.type1.identifier' />");
			this.renderTextWithTitle(UtilSyslist.getSyslistEntryName(525, nodeData.ref1DataSet), "<fmt:message key='ui.obj.type1.dataset' />");
			this.renderList(nodeData.ref1Representation, "<fmt:message key='ui.obj.type1.digitalRepresentation' />", null, function(val) { return UtilSyslist.getSyslistEntryName(526, val); });
			this.renderTextWithTitle(UtilSyslist.getSyslistEntryName(528, nodeData.ref1VFormatTopology), "<fmt:message key='ui.obj.type1.vectorFormat.topology' />");
			this.renderTable(nodeData.ref1VFormatDetails, ["geometryType", "numElements"], ["<fmt:message key='ui.obj.type1.vectorFormat.detailsTable.header.geoType' />", "<fmt:message key='ui.obj.type1.vectorFormat.detailsTable.header.elementCount' />"], "<fmt:message key='ui.obj.type1.vectorFormat.title' />", [function(val) {return UtilSyslist.getSyslistEntryName(515, val);}, null]);
			// NOTICE: moved to general section "Raumbezug"
            //this.renderTextWithTitle(nodeData.ref1SpatialSystem, "<fmt:message key='ui.obj.type1.spatialSystem' />");
			this.renderTable(nodeData.ref1Scale, ["scale", "groundResolution", "scanResolution"], ["<fmt:message key='ui.obj.type1.scaleTable.header.scale' />", "<fmt:message key='ui.obj.type1.scaleTable.header.groundResolution' />", "<fmt:message key='ui.obj.type1.scaleTable.header.scanResolution' />"], "<fmt:message key='ui.obj.type1.scaleTable.title' />");
			this.renderTable(nodeData.ref1SymbolsText, ["title", "date", "version"], ["<fmt:message key='ui.obj.type1.symbolCatTable.header.title' />", "<fmt:message key='ui.obj.type1.symbolCatTable.header.date' />", "<fmt:message key='ui.obj.type1.symbolCatTable.header.version' />"], "<fmt:message key='ui.obj.type1.symbolCatTable.title' />", [null, formatDate, null]);
			this.renderTable(nodeData.ref1KeysText, ["title", "date", "version"], ["<fmt:message key='ui.obj.type1.keyCatTable.header.title' />", "<fmt:message key='ui.obj.type1.keyCatTable.header.date' />", "<fmt:message key='ui.obj.type1.keyCatTable.header.version' />"], "<fmt:message key='ui.obj.type1.keyCatTable.title' />", [null, formatDate, null]);
            this.renderList(nodeData.ref1Data, "<fmt:message key='ui.obj.type1.attributes' />");
			this.renderTextWithTitle(nodeData.ref1DataBasisText, "<fmt:message key='ui.obj.type1.dataBasisTable.title' />");
			this.renderTextWithTitle(nodeData.ref1ProcessText, "<fmt:message key='ui.obj.type1.processTable.title' />");
            
            this.renderAdditionalFieldsForRubric("refClass1", additionalFields);
            
            // DQ
            this.renderSectionTitel("<fmt:message key='ui.obj.dq' />");
            this.renderTextWithTitle(nodeData.ref1Coverage, "<fmt:message key='ui.obj.type1.coverage' />" + " [%]");
            this.renderTextWithTitle(nodeData.ref1AltAccuracy, "<fmt:message key='ui.obj.type1.sizeAccuracy' />");
            this.renderTextWithTitle(nodeData.ref1PosAccuracy, "<fmt:message key='ui.obj.type1.posAccuracy' />");
            this.renderTable(nodeData.dq109Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table109.title' />");
            this.renderTable(nodeData.dq112Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table112.title' />");
            this.renderTable(nodeData.dq113Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table113.title' />");
            this.renderTable(nodeData.dq114Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table114.title' />");
            this.renderTable(nodeData.dq115Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table115.title' />");
            this.renderTable(nodeData.dq120Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table120.title' />");
            this.renderTable(nodeData.dq125Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table125.title' />");
            this.renderTable(nodeData.dq126Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table126.title' />");
            this.renderTable(nodeData.dq127Table, ["nameOfMeasure", "resultValue", "measureDescription"], ["<fmt:message key='ui.obj.dq.table.header1' />", "<fmt:message key='ui.obj.dq.table.header2' />", "<fmt:message key='ui.obj.dq.table.header3' />"], "<fmt:message key='ui.obj.dq.table127.title' />");
            
            this.renderAdditionalFieldsForRubric("refClass1DQ", additionalFields);
		} else if (nodeData.objectClass == 2) {
			this.renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
			// Literature
			this.renderTextWithTitle(nodeData.ref2Author, "<fmt:message key='ui.obj.type2.author' />");
			this.renderTextWithTitle(nodeData.ref2Publisher, "<fmt:message key='ui.obj.type2.editor' />");
			this.renderTextWithTitle(nodeData.ref2PublishedIn, "<fmt:message key='ui.obj.type2.publishedIn' />");
			this.renderTextWithTitle(nodeData.ref2PublishLocation, "<fmt:message key='ui.obj.type2.publishedLocation' />");
			this.renderTextWithTitle(nodeData.ref2PublishedInIssue, "<fmt:message key='ui.obj.type2.issue' />");
			this.renderTextWithTitle(nodeData.ref2PublishedInPages, "<fmt:message key='ui.obj.type2.pages' />");
			this.renderTextWithTitle(nodeData.ref2PublishedInYear, "<fmt:message key='ui.obj.type2.publishedYear' />");
			this.renderTextWithTitle(nodeData.ref2PublishedISBN, "<fmt:message key='ui.obj.type2.isbn' />");
			this.renderTextWithTitle(nodeData.ref2PublishedPublisher, "<fmt:message key='ui.obj.type2.publisher' />");
			this.renderTextWithTitle(nodeData.ref2LocationText, "<fmt:message key='ui.obj.type2.locationTable.title' />");
			this.renderTextWithTitle(nodeData.ref2DocumentType, "<fmt:message key='ui.obj.type2.documentType' />");
			this.renderTextWithTitle(nodeData.ref2BaseDataText, "<fmt:message key='ui.obj.type2.generalDataTable.title' />");
			this.renderTextWithTitle(nodeData.ref2BibData, "<fmt:message key='ui.obj.type2.additionalBibInfo' />");
			this.renderTextWithTitle(nodeData.ref2Explanation, "<fmt:message key='ui.obj.type2.description' />");
            
            this.renderAdditionalFieldsForRubric("refClass2", additionalFields);
		} else if (nodeData.objectClass == 3) {
			this.renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
			// Geodatendienst
			this.renderList(nodeData.ref3ServiceTypeTable, "<fmt:message key='ui.obj.type3.ref3ServiceTypeTable.title' />", null, function(val) { return UtilSyslist.getSyslistEntryName(5200, val); });
			this.renderTextWithTitle(UtilSyslist.getSyslistEntryName(5100, nodeData.ref3ServiceType), "<fmt:message key='ui.obj.type3.serviceType' />");
			this.renderTextWithTitle(nodeData.ref3CouplingType, "<fmt:message key='ui.obj.type3.couplingType' />");
			this.renderList(nodeData.ref3ServiceVersion, "<fmt:message key='ui.obj.type3.serviceVersion' />");
			this.renderTextWithTitle(nodeData.ref3SystemEnv, "<fmt:message key='ui.obj.type3.environment' />");
			this.renderTextWithTitle(nodeData.ref3History, "<fmt:message key='ui.obj.type3.history' />");
			this.renderTextWithTitle(nodeData.ref3BaseDataText, "<fmt:message key='ui.obj.type3.generalDataTable.title' />" + " (" + "<fmt:message key='ui.obj.type3.generalDataTable.tab.text' />" + ")");
			this.renderTextWithTitle(nodeData.ref3Explanation, "<fmt:message key='ui.obj.type3.description' />");
            this.renderOperations(nodeData.ref3Operation);
			this.renderTable(nodeData.ref3Scale, ["scale", "groundResolution", "scanResolution"], ["<fmt:message key='ui.obj.type1.scaleTable.header.scale' />", "<fmt:message key='ui.obj.type1.scaleTable.header.groundResolution' />", "<fmt:message key='ui.obj.type1.scaleTable.header.scanResolution' />"], "<fmt:message key='ui.obj.type3.scaleTable.title' />");
            this.renderTextWithTitle(nodeData.ref3HasAccessConstraint ? "<fmt:message key='general.yes' />": "<fmt:message key='general.no' />", "<fmt:message key='ui.obj.type3.ref3HasAccessConstraint' />");
            
            this.renderAdditionalFieldsForRubric("refClass3", additionalFields);
		} else if (nodeData.objectClass == 4) {
			this.renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
			// Vorhaben
			this.renderTextWithTitle(nodeData.ref4ParticipantsText, "<fmt:message key='ui.obj.type4.participantsTable.title' />");
			this.renderTextWithTitle(nodeData.ref4PMText, "<fmt:message key='ui.obj.type4.projectManagerTable.title' />");
			this.renderTextWithTitle(nodeData.ref4Explanation, "<fmt:message key='ui.obj.type4.description' />");
            
            this.renderAdditionalFieldsForRubric("refClass4", additionalFields);
		} else if (nodeData.objectClass == 5) {
			this.renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
			// Datensammlung/Datenbank
            this.renderTable(nodeData.ref5KeysText, ["title", "date", "version"], ["<fmt:message key='ui.obj.type5.keyCatTable.header.title' />", "<fmt:message key='ui.obj.type5.keyCatTable.header.date' />", "<fmt:message key='ui.obj.type5.keyCatTable.header.version' />"], "<fmt:message key='ui.obj.type5.keyCatTable.title' />", [null, formatDate, null]);
			this.renderTable(nodeData.ref5dbContent, ["parameter", "additionalData"], ["<fmt:message key='ui.obj.type5.contentTable.header.parameter' />", "<fmt:message key='ui.obj.type5.contentTable.header.additionalData' />"], "<fmt:message key='ui.obj.type5.contentTable.header.additionalData' />");
			this.renderTextWithTitle(nodeData.ref5MethodText, "<fmt:message key='ui.obj.type5.methodTable.title' />");
			this.renderTextWithTitle(nodeData.ref5Explanation, "<fmt:message key='ui.obj.type5.description' />");
            
            this.renderAdditionalFieldsForRubric("refClass5", additionalFields);
		} else if (nodeData.objectClass == 6) {
            this.renderSectionTitel("<fmt:message key='ui.obj.relevance' />");
            // Dienst/Anwendung/Informationssystem
            this.renderTextWithTitle(UtilSyslist.getSyslistEntryName(5300, nodeData.ref6ServiceType), "<fmt:message key='ui.obj.type6.serviceType' />");
            this.renderList(nodeData.ref6ServiceVersion, "<fmt:message key='ui.obj.type6.serviceVersion' />");
            this.renderTextWithTitle(nodeData.ref6SystemEnv, "<fmt:message key='ui.obj.type6.environment' />");
            this.renderTextWithTitle(nodeData.ref6History, "<fmt:message key='ui.obj.type6.history' />");
            this.renderTextWithTitle(nodeData.ref6BaseDataText, "<fmt:message key='ui.obj.type6.generalDataTable.title' />" + " (" + "<fmt:message key='ui.obj.type6.generalDataTable.tab.text' />" + ")");
            this.renderTextWithTitle(nodeData.ref6Explanation, "<fmt:message key='ui.obj.type6.description' />");
            this.renderTable(nodeData.ref6UrlList, ["name", "url", "urlDescription"], ["<fmt:message key='ui.obj.type6.urlList.header.name' />", "<fmt:message key='ui.obj.type6.urlList.header.url' />", "<fmt:message key='ui.obj.type6.urlList.header.urlDescription' />"], "<fmt:message key='ui.obj.type6.urlList' />");
            
            this.renderAdditionalFieldsForRubric("refClass6", additionalFields);
        }
		
		// spatial reference
		this.renderSectionTitel("<fmt:message key='ui.obj.spatial.title' />");
		UtilList.addSNSLocationLabels(nodeData.spatialRefAdminUnitTable);
		this.renderTable(nodeData.spatialRefAdminUnitTable, ["label", "nativeKey", "longitude1", "latitude1", "longitude2", "latitude2"], ["<fmt:message key='ui.obj.spatial.geoThesTable.header.name' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.nativeKey' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.longitude1' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.latitude1' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.longitude2' />", "<fmt:message key='ui.obj.spatial.geoThesTable.header.latitude2' />"], "<fmt:message key='dialog.compare.object.spatialTable.title' />");
		this.renderTable(nodeData.spatialRefLocationTable, ["name", "longitude1", "latitude1", "longitude2", "latitude2"], ["<fmt:message key='ui.obj.spatial.geoTable.header.name' />", "<fmt:message key='ui.obj.spatial.geoTable.header.longitude1' />", "<fmt:message key='ui.obj.spatial.geoTable.header.latitude1' />", "<fmt:message key='ui.obj.spatial.geoTable.header.longitude2' />", "<fmt:message key='ui.obj.spatial.geoTable.header.latitude2' />"], "<fmt:message key='ui.obj.spatial.geoTable.title' />");
		
        // NOTICE: moved from class 1 to general "Raumbezug"
        //this.renderTextWithTitle(nodeData.ref1SpatialSystem, "<fmt:message key='ui.obj.type1.spatialSystem' />");
        this.renderList(nodeData.ref1SpatialSystemTable, "<fmt:message key='ui.obj.type1.spatialSystem' />");
        
        // create cell render functions
		function lookupSpatialRefAltMeasure(val) {
			return UtilSyslist.getSyslistEntryName(102, val);
		}
		function lookupSpatialRefAltVDate(val) {
			return UtilSyslist.getSyslistEntryName(101, val);
		}
		
		var altitudeData = []; // empty list means no rendering!
		if (nodeData.spatialRefAltMin || nodeData.spatialRefAltMax || lookupSpatialRefAltMeasure(nodeData.spatialRefAltMeasure) || lookupSpatialRefAltVDate(nodeData.spatialRefAltVDate) ) {
			altitudeData = [nodeData]; // add nodeData to the list so that it's rendered with values from it
		}
		this.renderTable(altitudeData, ["spatialRefAltMin", "spatialRefAltMax", "spatialRefAltMeasure", "spatialRefAltVDate"], ["<fmt:message key='ui.obj.spatial.height.min' />", "<fmt:message key='ui.obj.spatial.height.max' />", "<fmt:message key='ui.obj.spatial.height.unit' />", "<fmt:message key='ui.obj.spatial.height.geodeticSystem' />"], "<fmt:message key='ui.obj.spatial.height' />", [null, null, lookupSpatialRefAltMeasure, lookupSpatialRefAltVDate]);
		this.renderTextWithTitle(nodeData.spatialRefExplanation, "<fmt:message key='ui.obj.spatial.description' />");
	
        this.renderAdditionalFieldsForRubric("spatialRef", additionalFields);
        
		// temporal reference
		this.renderSectionTitel("<fmt:message key='ui.obj.time.title' />");
		var timeRefTxt;
		if (nodeData.timeRefDate1) {
			if (nodeData.timeRefType && nodeData.timeRefType == "von") {
				timeRefTxt = "von "+formatDate(nodeData.timeRefDate1)+" bis "+formatDate(nodeData.timeRefDate2);
			} else if (nodeData.timeRefType) {
				timeRefTxt = nodeData.timeRefType+" "+formatDate(nodeData.timeRefDate1);
			}
		}
		
		this.renderTextWithTitle(timeRefTxt, "<fmt:message key='ui.obj.time.timeRefContent' />");
		this.renderTextWithTitle(UtilSyslist.getSyslistEntryName(523, nodeData.timeRefStatus), "<fmt:message key='ui.obj.time.state' />");
		this.renderTextWithTitle(UtilSyslist.getSyslistEntryName(518, nodeData.timeRefPeriodicity), "<fmt:message key='ui.obj.time.periodicity' />");
		if (nodeData.timeRefIntervalNum && nodeData.timeRefIntervalUnit) {
			// Do NOT use selectedResult[...] !!! selectedResult IS NULL !!! only not null if once selected by user interaction !!!  
            // this.renderTextWithTitle("<fmt:message key='ui.obj.time.interval.each' />" + " "+nodeData.timeRefIntervalNum+" "+dojo.widget.byId("timeRefIntervalUnit").selectedResult[0], "<fmt:message key='ui.obj.time.interval' />"); //_getDisplayValueForValue(nodeData.timeRefIntervalUnit)
            this.renderTextWithTitle("<fmt:message key='ui.obj.time.interval.each' />" + " "+nodeData.timeRefIntervalNum+" "+nodeData.timeRefIntervalUnit, "<fmt:message key='ui.obj.time.interval' />");
		}
		// create cell render functions
		function lookupTimeRefType(val) {
			return UtilSyslist.getSyslistEntryName(502, val);
		}
		this.renderTable(nodeData.timeRefTable, ["date", "type"], ["<fmt:message key='ui.obj.time.timeRefTable.header.date' />", "<fmt:message key='ui.obj.time.timeRefTable.header.type' />"], "<fmt:message key='ui.obj.time.timeRefTable.title' />", [formatDate, lookupTimeRefType]);
		this.renderTextWithTitle(nodeData.timeRefExplanation, "<fmt:message key='ui.obj.time.description' />");
        
        this.renderAdditionalFieldsForRubric("timeRef", additionalFields);
		
		// additional information
		this.renderSectionTitel("<fmt:message key='ui.obj.additionalInfo.title' />");
		this.renderTextWithTitle(UtilSyslist.getSyslistEntryName(99999999, nodeData.extraInfoLangMetaDataCode), "<fmt:message key='ui.obj.additionalInfo.language.metadata' />");
		this.renderTextWithTitle(UtilSyslist.getSyslistEntryName(99999999, nodeData.extraInfoLangDataCode), "<fmt:message key='ui.obj.additionalInfo.language.data' />");
		this.renderTextWithTitle(UtilSyslist.getSyslistEntryName(3571, nodeData.extraInfoPublishArea), "<fmt:message key='ui.obj.additionalInfo.publicationCondition' />");
		//!!!this.renderTextWithTitle(dijit.byId("extraInfoCharSetData").get("displayedValue"), "<fmt:message key='ui.obj.additionalInfo.charSet.data' />");
        // Table is only displayed for object classes 1 and 3
		if (nodeData.objectClass == 1 || nodeData.objectClass == 3) {
			this.renderTable(nodeData.extraInfoConformityTable, ["specification", "level"],
						["<fmt:message key='ui.obj.additionalInfo.conformityTable.header.specification' />", "<fmt:message key='ui.obj.additionalInfo.conformityTable.header.level' />"],
						"<fmt:message key='ui.obj.additionalInfo.conformityTable.title' />",
						[function(val) { return UtilSyslist.getSyslistEntryName(6005, val); }, function(val) { return UtilSyslist.getSyslistEntryName(6000, val); }]);
		}
		this.renderList(nodeData.extraInfoXMLExportTable, "<fmt:message key='ui.obj.additionalInfo.xmlExportCriteria' />");
		this.renderList(nodeData.extraInfoLegalBasicsTable, "<fmt:message key='ui.obj.additionalInfo.legalBasis' />");
		this.renderTextWithTitle(nodeData.extraInfoPurpose, "<fmt:message key='ui.obj.additionalInfo.purpose' />");
		this.renderTextWithTitle(nodeData.extraInfoUse, "<fmt:message key='ui.obj.additionalInfo.suitability' />");
        
        this.renderAdditionalFieldsForRubric("extraInfo", additionalFields);
		
		// availability
		this.renderSectionTitel("<fmt:message key='ui.obj.availability.title' />");
        this.renderList(nodeData.availabilityAccessConstraints, "<fmt:message key='ui.obj.availability.accessConstraints' />", null, function(val){
            return UtilSyslist.getSyslistEntryName(6010, val);
        });
        this.renderTextWithTitle(nodeData.availabilityUseConstraints, "<fmt:message key='ui.obj.availability.useConstraints' />");
        this.renderTextWithTitle(UtilSyslist.getSyslistEntryName(6300, nodeData.availabilityDataFormatInspire), "<fmt:message key='ui.obj.availability.dataFormatInspire' />");
		this.renderTable(nodeData.availabilityDataFormatTable, ["name", "version", "compression", "pixelDepth"], ["<fmt:message key='ui.obj.availability.dataFormatTable.header.name' />", "<fmt:message key='ui.obj.availability.dataFormatTable.header.version' />", "<fmt:message key='ui.obj.availability.dataFormatTable.header.compression' />", "<fmt:message key='ui.obj.availability.dataFormatTable.header.depth' />"], "<fmt:message key='ui.obj.availability.dataFormatTable.title' />", [function(val) { return UtilSyslist.getSyslistEntryName(1320, val); }, null, null, null]);
		this.renderTable(nodeData.availabilityMediaOptionsTable, ["name", "transferSize", "location"], ["<fmt:message key='ui.obj.availability.mediaOptionTable.header.type' />", "<fmt:message key='ui.obj.availability.mediaOptionTable.header.amount' />", "<fmt:message key='ui.obj.availability.mediaOptionTable.header.location' />"], "<fmt:message key='ui.obj.availability.mediaOptionTable.title' />", [function(val) { return UtilSyslist.getSyslistEntryName(520, val); }, null, null]);
		this.renderTextWithTitle(nodeData.availabilityOrderInfo, "<fmt:message key='ui.obj.availability.orderInfo' />");
        
        this.renderAdditionalFieldsForRubric("availability", additionalFields);
	
		// indexing
		this.renderSectionTitel("<fmt:message key='ui.obj.thesaurus.title' />");
		var sortedList = nodeData.thesaurusTermsTable.sort(function(a,b) {return UtilString.compareIgnoreCase(a.title,b.title);});
		this.renderList(sortedList, "<fmt:message key='ui.adr.thesaurus.terms' />", "title");
		sortedList = nodeData.thesaurusTopicsList.sort(function(a,b) {return UtilString.compareIgnoreCase(UtilSyslist.getSyslistEntryName(527, a),UtilSyslist.getSyslistEntryName(527, b));}); 
		this.renderList(sortedList, "<fmt:message key='ui.obj.thesaurus.terms.category' />", null, function (val) { return UtilSyslist.getSyslistEntryName(527, val);});
		sortedList = nodeData.thesaurusInspireTermsList.sort(function(a,b) {return UtilString.compareIgnoreCase(UtilSyslist.getSyslistEntryName(6100, a),UtilSyslist.getSyslistEntryName(6100, b));});
		this.renderList(sortedList, "<fmt:message key='ui.obj.thesaurus.terms.inspire' />", null, function (val) { return UtilSyslist.getSyslistEntryName(6100, val);});
		this.renderTextWithTitle(nodeData.thesaurusEnvExtRes ? "<fmt:message key='general.yes' />": "<fmt:message key='general.no' />", "<fmt:message key='ui.obj.thesaurus.terms.enviromental.displayCatalogPage' />");
		this.renderList(nodeData.thesaurusEnvTopicsList, "<fmt:message key='ui.obj.thesaurus.terms.enviromental.title' />"+ " - " + "<fmt:message key='ui.obj.thesaurus.terms.enviromental.topics' />", null, function (val) { return UtilSyslist.getSyslistEntryName(1410, val);});
        
        this.renderAdditionalFieldsForRubric("thesaurus", additionalFields);
	
		// references
		if (nodeData.linksFromObjectTable.length > 0 || nodeData.linksToObjectTable.length > 0 || nodeData.linksToUrlTable.length > 0) {
	    	this.renderSectionTitel("<fmt:message key='ui.obj.links.title' />");
	    	sortedList = nodeData.linksFromObjectTable.sort(function(a,b) {return (b.title<a.title)-(a.title<b.title);});
	    	this.renderList(sortedList, "<fmt:message key='dialog.compare.object.linksFromTable.title' />", "title")
	    	sortedList = nodeData.linksToObjectTable.sort(function(a,b) {return (b.title<a.title)-(a.title<b.title);});
	    	this.renderList(sortedList, "<fmt:message key='dialog.compare.object.linksToTable.title' />", "title")
	    	this.renderUrlLinkList(nodeData.linksToUrlTable);
	    }
        
        this.renderAdditionalFieldsForRubric("links", additionalFields);
        
        this.renderAdditionalRubrics(additionalFields);
        console.debug(nodeData);
		
		// administrative data
		this.renderSectionTitel("<fmt:message key='dialog.compare.object.administrative' />");
        var objId = nodeData.orgObjId ? nodeData.orgObjId : nodeData.uuid;
		this.renderTextWithTitle(objId, "<fmt:message key='dialog.compare.object.id' />");
		this.renderTextWithTitle(catalogData.catalogName, "<fmt:message key='dialog.compare.object.catalog' />");
	
		// additional fields
		/*var addFields = nodeData.additionalFields;
		if (addFields.length > 0) {
	    	this.renderSectionTitel("<fmt:message key='ui.obj.additionalFields.title' />");
	    	for(var i=0; i<addFields.length; i++) {
	    		this.renderTextWithTitle(addFields[i].value, addFields[i].name);
	    	}
		}*/

		// modification time
		this.renderText("<fmt:message key='ui.obj.header.modificationTime' />" + ": " + nodeData.modificationTime);
		
		defMain.callback();
    }));
    
    return defMain; 
}

scriptScopeDetailView.renderAdditionalFieldsForRubric = function(rubric, nodeData) {
    //console.debug("add additional fields info of rubric: " + rubric);
    var addDomWidgets = dojo.query(".additionalField", rubric);
    dojo.forEach(addDomWidgets, function(domWidget) {
        var widgetId = domWidget.getAttribute("widgetId");
        if (!widgetId) widgetId = domWidget.id;
        var label = this.searchLabelFrom(domWidget);
        var data = this.getValueFromAdditional(widgetId, nodeData);
        if (data) {
            // if it is a table
            if (typeof(data) == "object") {
                if (data.length > 0) {
                    var columnFields = this.getColumnFields(widgetId);
                    var columnNames = this.getColumnNames(widgetId);
                    var formatters = this.getColumnFormatters(widgetId);
                    this.renderTable(data, columnFields, columnNames, label, formatters, true, UtilGrid.getTable(widgetId).getColumns());
                }
            } else {
                this.renderTextWithTitle(data, label);
            }
        }
    }, this);
}

scriptScopeDetailView.getValueFromAdditional = function(id, additionalFields) {
    var result = null;
    dojo.some(additionalFields, function(field) {
        //console.debug("check: " + id + " with "+field.identifier);
        if (field.identifier === id) {
            // special handling of tables
            if (field.tableRows != null) {
                result = this.prepareAdditionalTable(field.tableRows);
                return true;
            }
            else if (field.value != null) {
                result = field.value + this.getUnitFromField(id);
                return true;
            }
        }
    }, this);
    return result;
}

scriptScopeDetailView.getUnitFromField = function(id) {
    var widget = dijit.byId(id);
    var unit = widget ? widget.domNode.getAttribute("unit") : null;
    if (unit)
        return " " + unit;
    else
        return "";
}

scriptScopeDetailView.getColumnFields = function(id) {
    return dojo.map(UtilGrid.getTable(id).getColumns(), function(col) {return col.field;});
}

scriptScopeDetailView.getColumnNames = function(id) {
    return dojo.map(UtilGrid.getTable(id).getColumns(), function(col) {return col.name;});
}
scriptScopeDetailView.getColumnFormatters = function(id) {
    return dojo.map(UtilGrid.getTable(id).getColumns(), function(col) {return col.formatter;});
}

scriptScopeDetailView.prepareAdditionalTable = function(rows) {
    var tableList = [];
    dojo.forEach(rows, function(row) {
        if (row.length == 0) return; // empty table
        var item = {};
        dojo.forEach(row, function(rowItem) {
            item[rowItem.identifier] = rowItem.value ? rowItem.value : ""; // TODO: listIds? mapping?
        });
        tableList.push(item)
    });
    return tableList;
    
}

scriptScopeDetailView.searchLabelFrom = function(element) {
    while (!dojo.hasClass(element, "input")) {
        element = element.parentNode;
    }
    element = element.previousSibling;
    while (!dojo.hasClass(element, "label")) {
        element = element.previousSibling;
    }
    
    if (element != null) {
        var text = dojo.isFF ? element.textContent : element.innerText;
        if (text[text.length-1] === "*")
            return text.slice(0,-1);
        return text;
    } else 
        return "???";    
}

scriptScopeDetailView.renderAdditionalRubrics = function(additionalFields) {
    var addDomRubrics = dojo.query(".rubric.additional", "contentFrameBodyObject");
    dojo.forEach(addDomRubrics, function(domRubric) {
        var rubricText = dojo.isFF ? domRubric.children[0].textContent : domRubric.children[0].innerText;
        this.renderSectionTitel(rubricText);
        this.renderAdditionalFieldsForRubric(domRubric.id, additionalFields);
    }, this);
}

scriptScopeDetailView.renderYesNo = function(val) {
	if (val == 1) {
		return "<fmt:message key='general.yes' />";
	} else {
		return "<fmt:message key='general.no' />";
	}
}

scriptScopeDetailView.renderSuperiorElements = function(nodeUuid, ignoreFirstNode) {
    var node = scriptScopeDetailView.mappedNodes[nodeUuid];
	if (nodeUuid == "objectRoot") {
		return "";
	} else if (!node || !node.parent) {
	    // leave selected subtree and find parents in the rendered tree
	    var treeNode = dijit.byId(nodeUuid);
	    if (ignoreFirstNode)
	        return this.renderSuperiorElements(treeNode.getParent().id[0]);
	    else
	        return this.renderSuperiorElements(treeNode.getParent().id[0]) + "@@" + treeNode.label;
	} else {
	    if (ignoreFirstNode)
	        return this.renderSuperiorElements(node.parent);
	    else
	        return this.renderSuperiorElements(node.parent) + "@@" + node.title;
	}
}

scriptScopeDetailView.renderSuperiorElementsRemoteCall = function(nodeUuid) {
    var def = new dojo.Deferred();
    ObjectService.getPathToObject(nodeUuid, function(path) {
        // remove last element which is the node itself!
        path.pop();
        var titlePath = "";
        var defNodeTitle = new dojo.Deferred(); 
        dojo.forEach(path, function(uuid) {
            //var node = scriptScopeDetailView.mappedNodes[uuid];
            // load node if not yet already done
            //if (!node) {
                defNodeTitle.addCallback(dojo.partial(scriptScopeDetailView.getNodeData, uuid, "O"));
                defNodeTitle.addCallback(function(nodeDataOther) {
                    titlePath += "@@" + nodeDataOther.title;
                });
            //}
        });
        defNodeTitle.addCallback(function() { def.callback(titlePath); });
        defNodeTitle.callback();
    });
    return def;
}


scriptScopeDetailView.renderSubordinatedElements = function(nodeUuid) {
    var node = scriptScopeDetailView.mappedNodes[nodeUuid];

    var subList = "";
    
    if (node.children) {
        dojo.forEach(node.children, function(child) {
            //console.debug("Got Children: " + children[i].title);
            subList = child.title + "@@" + subList;
            //console.debug("return list: " + subList);
            
            if (child.children) {
                subList = scriptScopeDetailView.renderSubordinatedElements(child.id) + "@@" + subList;
            }
            
            return subList;
        });
    }
    
    return subList;
}

scriptScopeDetailView.renderSubordinatedElementsRemoteCall = function(nodeData) {
    var defMain = new dojo.Deferred();
    
    var def = this.getAllChildrenRecursively(nodeData);
    
    var subList = "";
    def.addCallback(function(children) {
        dojo.forEach(children, function(child) {
            subList = child.title + "@@" + subList;
        });
        defMain.callback(subList);
    });    
    
    return defMain;
}

scriptScopeDetailView.renderOperations = function(list) {
	this.renderTable(list, ["name", "addressList"], ["<fmt:message key='ui.obj.type3.operationTable.header.name' />", "<fmt:message key='ui.obj.type3.operationTable.header.address' />"], "<fmt:message key='ui.obj.type3.operationTable.title' />", [null, this.renderFirstElement]);
	for(var i=0; i<list.length; i++) {
		var op = list[i];
        this.renderTextWithTitle(op.name, "<fmt:message key='dialog.operation.opName' />");
        this.renderTextWithTitle(op.description, "<fmt:message key='dialog.operation.description' />");
        this.renderList(op.addressList, "<fmt:message key='dialog.operation.address' />");
        sortedList = op.platform.sort(function(a,b) {return UtilString.compareIgnoreCase(UtilSyslist.getSyslistEntryName(5180, a),UtilSyslist.getSyslistEntryName(5180, b));}); 
        this.renderList(sortedList, "<fmt:message key='dialog.operation.platforms' />", null, function (val) { return UtilSyslist.getSyslistEntryName(5180, val);});
		this.renderTextWithTitle(op.methodCall, "<fmt:message key='dialog.operation.call' />");
		this.renderTable(op.paramList, ["name", "direction", "description", "optional", "multiple"], ["<fmt:message key='dialog.operation.name' />", "<fmt:message key='dialog.operation.direction' />", "<fmt:message key='dialog.operation.description' />", "<fmt:message key='dialog.operation.optional' />", "<fmt:message key='dialog.operation.multiplicity' />"], "<fmt:message key='dialog.operation.parameter' />", [null, null, null, this.renderYesNo, this.renderYesNo]);
		this.renderList(op.dependencies, "<fmt:message key='dialog.operation.dependencies' />");
	}
}

scriptScopeDetailView.renderUrlLinkList = function(list) {
	if (list && list.length > 0) {
		var t = "<p><strong>URL Verweise</strong><br/>";
		for (var i = 0; i < list.length; i++) {
			t += "<a href=\"" + list[i].url + "\" target=\"new\">" + list[i].name + "</a><br/>";
		}
		this.detailDivContent += t + "<br/><br/>";
	}
}

scriptScopeDetailView.renderObjectTitel = function(val) {
	this.detailDivContent += "<h1>" + val + "</h1><br/><br/>";
}

scriptScopeDetailView.renderSectionTitel = function(val) {
	this.detailDivContent += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
}

scriptScopeDetailView.renderTextWithTitle = function(val, title) {
	// compare with null so 0 will be different ! !oldVal handles 0 the same as null so replaces it with "" !
    if (val == null) val = "";
    val += "";
	if (detailHelper.isValid(val)) {
		// Replace newlines with <br/>
		val = val.replace(/\n/g, "<br/>");
		if (detailHelper.isValid(title)) {
			this.detailDivContent += "<p><strong>" + title + "</strong><br/>" + val + "</p><br/>";
		} else {
			this.detailDivContent += "<p>" + val + "</p><br/>";
		}
	}
}

scriptScopeDetailView.removeEvilTags = function(val) {
	if (val) {
		return val.replace(/<(?!b>|\/b>|i>|\/i>|u>|\/u>|p>|\/p>|br>|br\/>|br \/>|strong>|\/strong>|ul>|\/ul>|ol>|\/ol>|li>|\/li>)[^>]*>/gi, '');
	} else {
		return "";
	}
}

scriptScopeDetailView.renderAddressList = function(list) {
	if (list.length > 0) {
		var t = "";
		for (var i = 0; i < list.length; i++) {
			t += "<strong>" + list[i].nameOfRelation+ "</strong><br/><br/>";
			t += "<p>";
			t += detailHelper.renderAddressEntry(list[i]).replace(/\n/g, '<br />');
			t += "</p><br/>";
		}
		this.detailDivContent += t;
	}
}

scriptScopeDetailView.renderText = function(val) {
	// compare with null so 0 will be different ! !oldVal handles 0 the same as null so replaces it with "" !
    if (val == null) val = "";
    val += "";
	if (val && val.length>0) {
		// Replace newlines with <br/>
		val = val.replace(/\n/g, "<br/>");
		this.detailDivContent += "<p>" + val + "</p><br/>";
	}
}

scriptScopeDetailView.renderList = function(list, title, rowProperty, renderFunction) {
	if (list && list.length > 0) {
		var t = "<p>";
		if (detailHelper.isValid(title)) {
			t += "<strong>" + title + "</strong><br/>";
		}
		var valList = "";
		for (var i=0; i<list.length; i++) {
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
			this.detailDivContent += t + valList + "</p><br/>";
		}
	}	
}

scriptScopeDetailView.renderTable = function(list, rowProperties, listHeader, title, cellRenderFunction, useGridFormatter, columns) {
	if (list && list.length > 0) {
		var t = "";
		if (detailHelper.isValid(title)) {
			t += "<strong>" + title + "</strong><br/><br/>";
		}
		t += "<p><table class=\"filteringTable\" cellspacing=\"0\">";
		if (listHeader && listHeader.length > 0) {
			t += "<thead class=\"fixedHeader\"><tr>";
			for (i=0; i<listHeader.length; i++) {
				t += "<th style=\"padding-right:4px\">"+listHeader[i]+"</th>";
			}
			t += "</tr></thead>";
		}
		t += "<tbody>";
		for (var i=0; i<list.length; i++) {
			if (i % 2) {
				t += "<tr class=\"alt\">";
			} else {
				t += "<tr>";
			}
			for (var j=0; j<rowProperties.length; j++) {
                var value = list[i][rowProperties[j]] ? list[i][rowProperties[j]] : "";
				if (cellRenderFunction && cellRenderFunction[j]) {
                    if (useGridFormatter)
                        t += "<td style=\"padding-right:4px\">"+cellRenderFunction[j].call(this, i, j, value, columns[j])+"</td>";
                    else
					    t += "<td style=\"padding-right:4px\">"+cellRenderFunction[j].call(this, value)+"</td>";
				} else {
					t += "<td style=\"padding-right:4px\">"+value+"</td>";
				}
			}
			t += "</tr>";
			
		}
		t += "</tbody></table></p>";
		this.detailDivContent += t + "<br/><br/>";
	}
}

scriptScopeDetailView.renderFirstElement = function(val) {
    var retVal = "";
    if (val && dojo.isArray(val) && val.length > 0) {
        retVal = val[0];
    }
    return retVal;
}

scriptScopeDetailView.findNodeInSubTree = function(uuid) {
    scriptScopeDetailView.mappedNodes;
}

</script>
</head>

<body>
    <div id="contentPane" layoutAlign="client" class="contentBlockWhite" style="width:700px;">
        <div id="dialogContent" class="content">
            <div id="printDialogSettings">
                <input type="checkbox" id="showSubTree" dojoType="dijit.form.CheckBox" />
                <label for="showSubTree" class="inActive">
                    <fmt:message key="dialog.detail.print.showSubTree" />
                </label>
                <input type="checkbox" id="showDetailedView" dojoType="dijit.form.CheckBox" checked=false />
                <label for="showDetailedView" class="inActive">
                    <fmt:message key="dialog.detail.print.showDetailedView" />
                </label>
                <input type="checkbox" id="showSubordinateObjects" dojoType="dijit.form.CheckBox" style="display:none;" checked=true />
                <label for="showSubordinateObjects" style="display:none;" class="inActive" title="<fmt:message key="dialog.detail.print.showSubordinate.tooltip" />">
                    <fmt:message key="dialog.detail.print.showSubordinate" />
                </label>
                <span id="detailLoadingZone" style="visibility:hidden;" class="processInfo right"><img src="img/ladekreis.gif" width="20" height="20" alt="Loading" /></span>
            </div>
            <span class="functionalLink onTab">
                <a id="printDetailObject" href="javascript:printDivContent('detailViewContent')" title="<fmt:message key="dialog.detail.print" />">[<fmt:message key="dialog.detail.print" />]</a>
            </span>
            <!-- MAIN TAB CONTAINER START -->
            <div id="detailViewContainer" dojoType="dijit.layout.TabContainer" style="height:528px; width:100%;" selectedChild="detailView">
                <!-- MAIN TAB 1 START -->
                <div id="detailView" dojoType="dijit.layout.ContentPane" class="blueTopBorder" style="width:500px;" title="<fmt:message key="dialog.detail.title" />">
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
