<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Objekt zuordnen</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
dojo.require("ingrid.dijit.tree.LazyTreeStoreModel");
dojo.require("ingrid.dijit.CustomTree");

var scriptScope = this;

dojo.connect(_container_, "onLoad", function(){
	init();
});

dojo.connect(_container_, "onUnLoad", function(){
	// If the dialog was cancelled via the dialogs close button
	// we need to signal an error (cancel action)
	if (dijit.byId("pageDialog").customParams.resultHandler.fired == -1) {
		dijit.byId("pageDialog").customParams.resultHandler.errback();
	}
});

function init() {
	// Deferred object which is called when the Tree has been initialized.
	// After the tree has been initialized, the tree is expanded to the targetNode
	// specified in the customParameter 'jumpToNode' 
	createCustomTree("selectDatasetTree", null, "id", "title", loadTreeData);

};

function loadTreeData(node, callback_function) {
	var parentItem = node.item;
	var prefix = "export_";
	var store = dijit.byId("selectDatasetTree").model.store;
	var def = UtilTree.getSubTree(parentItem, prefix.length);
	
	def.addCallback(function(data){
	    if (parentItem.root) {
			dojo.forEach(data, function(entry){
				entry.uuid = entry.id;
				entry.id = prefix + entry.id;
		        store.newItem(entry);
			});
	    }
	    else {
	        dojo.forEach(data, function(entry){
				entry.uuid = entry.id;
				entry.id = prefix+entry.id;
	            store.newItem(entry, {
	                parent: parentItem,
	                attribute: "children"
	            });
	        });
	    }
	    callback_function();
	});
	def.addErrback(function(res){
	    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='tree.loadError' />", dialog.WARNING);
	    console.debug(res);
	    return res;
	});
	return def;
}

scriptScope.selectDatasetForExport = function() {
	var node = dijit.byId("selectDatasetTree").selectedNode.item;
	if (node) {
		var retVal = {};
		retVal.uuid = node.uuid[0];
		retVal.title = node.title[0];
		retVal.nodeAppType = node.nodeAppType[0];

		dijit.byId("pageDialog").customParams.resultHandler.callback(retVal);
	}

	dijit.byId("pageDialog").hide(); 
}

</script>
</head>

<body>

    <div id="winNavi" style="top:0px; height: 18px;">
        <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=import-export-1#import-export-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
    </div>
        <!-- CONTENT START -->
        <div class="inputContainer grey">
            <div dojoType="dijit.layout.ContentPane" id="treeContainerAssignObj" style="height:350px;">
                <div id="selectDatasetTree">
                </div>
            </div>
        </div>
        <div class="inputContainer">
            <span>
                <fmt:message key="dialog.admin.export.selectNode.note" />
            </span>
        </div>
        <div class="inputContainer">
            <span class="button transparent">
                <span style="float:right;">
                    <button dojoType="dijit.form.Button" onClick="javascript:scriptScope.selectDatasetForExport()">
                        <fmt:message key="dialog.admin.export.selectNode.select" />
                    </button>
                </span>
            </span>
        </div><!-- CONTENT END -->

</body>
</html>
