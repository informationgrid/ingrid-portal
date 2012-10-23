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

var scriptScopeDetailViewAddr = _container_;

dojo.connect(_container_, "onLoad", function() {
    dojo.connect(dijit.byId("showSubTree"), "onClick", dojo.hitch(scriptScopeDetailViewAddr, "refreshView"));
    dojo.connect(dijit.byId("showDetailedView"), "onClick", dojo.hitch(scriptScopeDetailViewAddr, "refreshView"));
    
    this.mappedNodes = {};
    this.subNodesCount = 1;
    this.useDirtyData = false;
    this.detailDivContent = "";
    
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
        // or udkDataProxy._getData()!!!
        
        // start from selected (parent) node to create the complete subtree, which will
        // be used to render the whole detailed information or just the titles
        // this is only needed once and saves time especially when printing big trees
        console.debug("determine subtree");
        this.setLoadingZone(true);
        var def = this.determineCompleteSubTree([this.nodeTreeItem], 0);
        def.addCallback( dojo.hitch(this, function() {
            if (this.customParams.useDirtyData != true) {
                console.debug("load data");
                // make sure the object forms are already loaded since they are needed
                // for syslist values for example
                ingridAddressLayout.create();
            }
            this.refreshView();
        }));
    }
});

scriptScopeDetailViewAddr.refreshView = function() {
    console.debug("refresh view");
    
    this.setLoadingZone(true);
    
    var showSubTree = dijit.byId("showSubTree").checked;
    this.useDirtyData = this.customParams.useDirtyData;
    
    // empty preview div
    dojo.byId("detailViewContent").innerHTML = "";
    scriptScopeDetailViewAddr.detailDivContent    = "";

    // reset processing info
    scriptScopeDetailViewAddr.currentlyProcessedNode = 0;
    scriptScopeDetailViewAddr.stopOperation = false;
    scriptScopeDetailViewAddr.detailData = [];
    
    // handle search results
    if (this.resultView) {
        this.renderSearchResults();
        
    } else { // handle tree
        if (showSubTree) {
            var def = this.renderSubTreeNodeData();
            def.addCallback(function() { dojo.byId("detailViewContent").innerHTML = scriptScopeDetailViewAddr.detailDivContent});
            def.addCallback(this.hideProcessingDialog);
        } else {
            var def = this.loadAndRenderTreeNode(this.selectedNode.id[0]);
            def.addCallback(function() { dojo.byId("detailViewContent").innerHTML = scriptScopeDetailViewAddr.detailDivContent});
            def.addCallback(dojo.partial(this.setLoadingZone, false));
        }
    }
}

scriptScopeDetailViewAddr.renderSearchResults = function() {
    var result = this.customParams.searchResult;
    this.subNodesCount = result.numHits;
    var showDetails = dijit.byId("showDetailedView").checked;
    
    // show a warning dialog if it would take a long time to prepare the print preview
    var def = scriptScopeDetailViewAddr.showTooManyNodesDialogIfNecessary();
    def.addCallback(scriptScopeDetailViewAddr.showProcessingDialog);
    dojo.forEach(result.resultList, function(entry) {
        if (showDetails) {
            def.addCallback(dojo.hitch(this, dojo.partial(scriptScopeDetailViewAddr.loadAndRenderTreeNode, entry.uuid, entry.nodeAppType)));
        } else {
            // give some time to render!
            setTimeout(function() { def.addCallback(dojo.hitch(this, function() {
                entry.title = UtilAddress.createAddressTitle(entry);
                scriptScopeDetailViewAddr.renderSimpleTreeNode(entry);
                scriptScopeDetailViewAddr.processNode();
            })); }, 10);
            //def.addCallback(function() { scriptScopeDetailViewAddr.detailDiv.innerHTML = scriptScopeDetailViewAddr.detailData.join("")});
        }
    }, this);
    setTimeout(function() { def.addCallback(function() { dojo.byId("detailViewContent").innerHTML = scriptScopeDetailViewAddr.detailDivContent;})}, 10);
    setTimeout(function() { def.addCallback(scriptScopeDetailViewAddr.hideProcessingDialog); }, 10);
}

scriptScopeDetailViewAddr.updateCheckboxesFunctionality = function() {
    if (scriptScopeDetailViewAddr.resultView) {
        UtilUI.disableElement("showSubTree");
    } else {
        UtilUI.enableElement("showSubTree");
    }
    
    if (dijit.byId("showSubTree").checked) {
        UtilUI.enableElement("showDetailedView");
    } else {
        UtilUI.disableElement("showDetailedView");
    }
}

/**
 * Determine the subtree structure with all elements hierarchically ordered. This improves
 * the speed when determining all subordinate objects since no backend requests are sent anymore.
 * Also determine statistical data.
 */
scriptScopeDetailViewAddr.determineCompleteSubTree = function(nodeDataList, hierarchy) {
    if (!(nodeDataList instanceof Array)) nodeDataList = [nodeDataList];
    var allDeferredsToWaitFor = [];
    
    dojo.forEach(nodeDataList, function(nodeData) {
        this.mappedNodes[nodeData.id] = nodeData;
        nodeData.hierarchy = hierarchy;
        if (nodeData.isFolder) {
            childrenToLoad = true;
            var waitForMe = new dojo.Deferred();
            var def = this.getImmediateChildrenOfNode(nodeData);
            def.addCallback(dojo.hitch(this, function(children) {
                // remember number of children for later statistics
                this.subNodesCount += children.length;
                // add parent information
                dojo.forEach(children, function(child) { child.parent = nodeData.id;});
                nodeData.children = children;
                var def2 = this.determineCompleteSubTree(children, hierarchy+1);
                def2.addCallback(function() { waitForMe.callback();});
            }));
            allDeferredsToWaitFor.push(waitForMe);
        }
    }, this);
    
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

// UtilTree.getSubTree
scriptScopeDetailViewAddr.getImmediateChildrenOfNode = function(nodeData) {
    var deferred = new dojo.Deferred();
    // share the deferred to keep in correct order!
    // otherwise the children of a node wouldn't be rendered before its siblings
    // -> other approach!!!
    TreeService.getSubTree(nodeData.id, nodeData.nodeAppType, userLocale, {
        callback:function(res) {
            deferred.callback(res);
        },
        errorHandler:function(message) {
            displayErrorMessage(new Error(message));
            deferred.errback();
        }
    });
    return deferred;
}

scriptScopeDetailViewAddr.renderSubTreeNodeData = function() {
    console.debug("renderTreeNodeData");
    var showSubTree = dijit.byId("showSubTree").checked;
    var renderDetailView = dijit.byId("showDetailedView").checked;
    
    // show a warning dialog if it would take a long time to prepare the print preview
    var def = scriptScopeDetailViewAddr.showTooManyNodesDialogIfNecessary();
    
    if (renderDetailView && showSubTree)
        def.addCallback(scriptScopeDetailViewAddr.showProcessingDialog);
    
    deferredRender = new dojo.Deferred();
    deferredRender.callback();
    
    def.addCallback(dojo.partial(dojo.hitch(scriptScopeDetailViewAddr, "renderSubTree"), scriptScopeDetailViewAddr.nodeTreeItem, deferredRender));
    // set innerHTML of detail div at the end which is much faster than appending!!! 
    //def.addCallback(function() { if (!renderDetailView && showSubTree) scriptScopeDetailView.detailDiv.innerHTML = scriptScopeDetailView.detailData.join("")});
    return def;
}

scriptScopeDetailViewAddr.showTooManyNodesDialogIfNecessary = function() {
    var def = new dojo.Deferred();
    var renderDetailView = dijit.byId("showDetailedView").checked;
    
    // count all subnodes first!
    if (renderDetailView && scriptScopeDetailViewAddr.subNodesCount > 100) {
        var displayText = dojo.string.substitute(message.get("dialog.object.detailView.dialog.warning"), [scriptScopeDetailViewAddr.subNodesCount]);
        var infoDlg = dialog.show("<fmt:message key='general.info' />", displayText, dialog.INFO, [{
            caption: "<fmt:message key='general.cancel' />",
            action: scriptScopeDetailViewAddr.hideProcessingDialog
        }, {
            caption: "<fmt:message key='general.ok' />",
            action: function() { setTimeout(def.callback, 0); } // delay execution since error occurs when second dialog is called
        }]);
    } else {
        def.callback();
    }
    return def;
}

scriptScopeDetailViewAddr.showProcessingDialog = function() {
    var displayText = dojo.string.substitute(message.get("dialog.object.detailView.dialog.processing"), [scriptScopeDetailViewAddr.subNodesCount]);
    scriptScopeDetailViewAddr.processDialog = dialog.show("<fmt:message key='general.info' />", displayText, dialog.INFO, [{
        caption: "<fmt:message key='general.cancel' />",
        action: function(){
            scriptScopeDetailViewAddr.stopOperation = true;
        }
    }]);
    scriptScopeDetailViewAddr.setLoadingZone(true);
}

scriptScopeDetailViewAddr.hideProcessingDialog = function() {
    if (scriptScopeDetailViewAddr.processDialog)
        scriptScopeDetailViewAddr.processDialog.hide();
    scriptScopeDetailViewAddr.setLoadingZone(false);
}

scriptScopeDetailViewAddr.processNode = function() {
    scriptScopeDetailViewAddr.currentlyProcessedNode += 1;
    //console.debug("Processed: " + scriptScopeDetailView.currentlyProcessedNode);
    if (dojo.byId("processInfoCurrent"))
        dojo.byId("processInfoCurrent").innerHTML = scriptScopeDetailViewAddr.currentlyProcessedNode; 
};

scriptScopeDetailViewAddr.setLoadingZone = function(activate) {
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
        scriptScopeDetailViewAddr.updateCheckboxesFunctionality();
    }
}

scriptScopeDetailViewAddr.renderSubTree = function(nodeDataList, deferred) {
    if (!(nodeDataList instanceof Array)) nodeDataList = [nodeDataList];
    dojo.forEach(nodeDataList, function(nodeData) {
        if (dijit.byId("showDetailedView").checked) {
            deferred.addCallback(dojo.partial(dojo.hitch(this, "loadAndRenderTreeNode"), nodeData.id));
        } else {
            this.renderSimpleTreeNode(nodeData);
        }
        
        // if node has children then recursively render these
        if (nodeData.isFolder) {
            this.renderSubTree(nodeData.children, deferred);            
        }
        
    }, this);
    return deferred;
}

scriptScopeDetailViewAddr.renderSimpleTreeNode = function(nodeData) {
    //scriptScopeDetailView.processNode();
    if (!this.stopOperation) {
        this.detailDivContent += "<div class='v_line'><div class='h_line' style='padding-left: " + (nodeData.hierarchy * 20) + "px'>&nbsp;</div><span>" + nodeData.title + "</span></div>";
    }
}

scriptScopeDetailViewAddr.loadAndRenderTreeNode = function(nodeId) {
    // skip if the user canceled
    // since several asynchronous deferreds exists it's better to ignore execution
    if (!this.stopOperation) {
        // update process info
        scriptScopeDetailViewAddr.processNode();
        var def = this.getNodeData(nodeId);
        def.addCallback(dojo.hitch(this, "renderNodeData"));
        // show a horizontal line between objects if a detail subtree is listed
        def.addCallback(function() { if (dijit.byId("showSubTree").checked) scriptScopeDetailViewAddr.detailDivContent += "<hr />"; });
        return def;
    }
}

scriptScopeDetailViewAddr.getNodeData = function(id) {
    var def = new dojo.Deferred();
    
    if (this.useDirtyData) {
        def.callback(udkDataProxy._getData());
    } else {
        AddressService.getAddressData(id, "false",
        {
            callback: def.callback,
            errorHandler:function(message) {
                displayErrorMessage(new Error(message));
                //dojo.debug("Error in mdek_detail_view_adress_dialog.html: Error while waiting for nodeData: " + message);
            }
        });
    }
    
    return def;
}

scriptScopeDetailViewAddr.renderNodeData = function(nodeData) {
    this.renderSectionTitel("<fmt:message key='dialog.compare.address.address' />");
    var allOrganisations = this.getOrganisations(nodeData);
	nodeData.organisation = allOrganisations ? allOrganisations : nodeData.organisation;
	this.renderText(detailHelper.renderAddressEntry(nodeData).replace(/\n/g, '<br />'));
	
	// administrative data
	this.renderSectionTitel("<fmt:message key='dialog.compare.address.administrative' />");
	this.renderTextWithTitle(nodeData.uuid, "<fmt:message key='dialog.compare.address.id' />");
	this.renderTextWithTitle(catalogData.catalogName, "<fmt:message key='dialog.compare.address.catalog' />");

}

scriptScopeDetailViewAddr.getOrganisations = function(nodeData) {
	//console.debug("addressClass: " + nodeData.addressClass);
	if (this.useDirtyData) {
	    // can only be used once! for top node of tree
	    this.useDirtyData = false;
        if (nodeData.addressClass == 0) {
            id = "headerAddressType0Unit";
        } else if (nodeData.addressClass == 1) {
            return dijit.byId("headerAddressType1Institution").getValue() + "\n" + dijit.byId("headerAddressType1Unit").getValue();
        } else if (nodeData.addressClass == 2) {
            id = "headerAddressType2Institution";
        } else if (nodeData.addressClass == 3) {
            id = "headerAddressType3Institution";
        }
        return dijit.byId(id).getValue();
	} else {
	    //var organisation = nodeData.organisation ? nodeData.organisation : "";
	    var res = UtilAddress.determineInstitution(nodeData);
	    return res;
	}
}

scriptScopeDetailViewAddr.renderSectionTitel = function(val) {
    this.detailDivContent += "<br/><h2><u>" + val + "</u></h2><br/>";
}

scriptScopeDetailViewAddr.renderTextWithTitle = function(val, title) {
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

scriptScopeDetailViewAddr.renderText = function(val) {
	if (val && val.length>0) {
		// Replace newlines with <br/>
		val = val.replace(/\n/g, "<br/>");
		this.detailDivContent += "<p>" + val + "</p><br/>";
	}
}


</script>
</head>

<body>
    <div dojoType="dijit.layout.ContentPane">
        <div id="contentPane" layoutAlign="client" class="contentBlockWhite" style="width: 700px;">
            <div id="dialogContent" class="content">
                <div id="printDialogSettings">
                    <input type="checkbox" id="showSubTree" dojoType="dijit.form.CheckBox" /> <label for="showSubTree"
                        class="inActive"> <fmt:message key="dialog.detail.print.showSubTree" />
                    </label> 
                    <input type="checkbox" id="showDetailedView" dojoType="dijit.form.CheckBox" checked=false /> 
                    <label
                        for="showDetailedView" class="inActive"> <fmt:message
                            key="dialog.detail.print.showDetailedView" />
                    </label> 
                    <span id="detailLoadingZone" style="visibility: hidden;" class="processInfo right">
                        <img src="img/ladekreis.gif" width="20" height="20" alt="Loading" />
                    </span>
                </div>
                <span class="functionalLink onTab">
                    <a id="printDetailObject" href="javascript:printDivContent('detailViewContent')"
                        title="<fmt:message key="dialog.detail.print" />">[<fmt:message key="dialog.detail.print" />]
                    </a>
                </span>
                <!-- MAIN TAB CONTAINER START -->
                <div id="detailViewContainer" dojoType="dijit.layout.TabContainer" style="height:528px; width:100%;" selectedChild="detailView">
                    <!-- MAIN TAB 1 START -->
                    <div id="detailView" dojoType="dijit.layout.ContentPane" class="blueTopBorder" style="height: 500px;" 
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
