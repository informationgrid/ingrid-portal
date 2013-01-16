<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
dojo.require("ingrid.dijit.tree.LazyTreeStoreModel");
dojo.require("ingrid.dijit.CustomTree");
dojo.require("dijit.layout.TabContainer");
dojo.require("dijit.form.SimpleTextarea");
dojo.require("dijit.form.ValidationTextBox");

var scriptScopeDuplicates = _container_;

var duplicatesListData = {
    identifier: 'identifier',
    items: []
};

	dojo.connect(_container_, "onLoad", function(){
        createDOMElements();
        initDuplicatesTable();
		// Load duplicates info on startup
		scriptScopeDuplicates.startDuplicatesJob();
	});
    dijit.byId("contentPane").resize();

function initDuplicatesTable() {
	var duplicatesTable = UtilGrid.getTable("duplicatesListTable");
	dojo.connect(duplicatesTable, "onSelectedRowsChanged", function(e) {
		scriptScopeDuplicates.fillData(UtilGrid.getSelectedData("duplicatesListTable")[0]);		
	});
}

scriptScopeDuplicates.fillData = function(data) {
    dijit.byId("duplicatesObjectName").setValue(data.title);
    dijit.byId("duplicatesObjectDescription").setValue(data.generalDescription);
    dijit.byId("duplicatesObjectClass").setValue(message.get('ui.obj.type'+ data.objectClass +'.name'));
}

// Switch to the tree view and select the node referenced by the menu action
function selectObjectInTree(menuItem) {
	dijit.byId("duplicatesLists").selectChild("duplicatesList2");
	//console.debug(menuItem);
	var objUuid = clickedSlickGrid.getDataItem(clickedRow).uuid;//rowData.uuid;
	selectObjectInTreeByUuid(objUuid);
}

// Select the node with uuid in the tree
function selectObjectInTreeByUuid(uuid) {
	ObjectService.getPathToObject(uuid, {
		callback: function(path){
		    //console.debug(path);
			//var def = expandPathDef(path);
			path.splice(0, 0, "objectRoot");
			for (i in path) { path[i] = "dubletsAdmin_" + path[i];}
			path.splice(0, 0, "root_duplicatesTree");
			var targetNodeId = path[path.length-1];
			var def = dijit.byId("duplicatesTree")._setPathAttr(path);
			def.addCallback(dojo.partial(selectNode, targetNodeId));
		},
		errorHandler: function(msg, err) {
		    displayErrorMessage(err);
			console.debug("Error: "+msg);
			def.errback();
		}
	});
}


// Get a child from a node in the tree for the given uuid 
scriptScopeDuplicates.getChildFromNode = function(childUuid, node) {
	for (var i = 0; i < node.children.length; ++i) {
		if (node.children[i].uuid == childUuid) {
			return node.children[i];
		}
	}
	return null;
}

function selectNode(nodeId) {
	var node = dijit.byId(nodeId);
	node.setSelected(true);
    setTimeout(function() {dojo.window.scrollIntoView(node.domNode);}, 1000);
}

scriptScopeDuplicates.loadObject = function(data) {
    ObjectService.getNodeData(data.uuid[0], "O", true, {
        preHook: showLoadingZone,
        postHook: hideLoadingZone,
        callback: scriptScopeDuplicates.fillData
    });
}

function createDOMElements() {
	createCustomTree("duplicatesTree", null, "id", "title", expandLoadObjects);
    dojo.connect(dijit.byId("duplicatesTree"), "onClick", scriptScopeDuplicates.loadObject);

	var duplicatesListTableStructure = [
		{field: 'title',name: 'title',width: 258-scrollBarWidth+'px'}
	];
	var def = createDataGrid("duplicatesListTable", null, duplicatesListTableStructure, null);	
}

function expandLoadObjects(node, callback_function){
    var parentItem = node.item;
	var prefix = "dubletsAdmin_";
    var store = dijit.byId("duplicatesTree").model.store;
    var def = UtilTree.getSubTree(parentItem, prefix.length);
    
    def.addCallback(function(data){
        //return _this.loadProcessResponse(node, data);
        // just use the object node here!
        if (parentItem.root) {
			data[0].uuid = data[0].id;
            data[0].id = prefix + data[0].id;
            store.newItem(data[0]);
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

function getDuplicatesDef() {
	var def = new dojo.Deferred();

	CatalogManagementService.getDuplicateObjects({
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(duplicateList) {
			def.callback(duplicateList);
		},
		errorHandler: function(msg, err) {
			hideLoadingZone();
			displayErrorMessage(err);
			console.debug("Error: "+msg);
			def.errback();
		}
	});

	return def;
}

scriptScopeDuplicates.startDuplicatesJob = function() {
	clearInputFields();
	var def = getDuplicatesDef();
	def.addCallback(function(duplicatesList) {
		UtilStore.updateWriteStore("duplicatesListTable", duplicatesList);
		UtilGrid.getTable("duplicatesListTable").resizeCanvas();
	});
}

function clearInputFields() {
	dijit.byId("duplicatesObjectName").setValue("");
	dijit.byId("duplicatesObjectDescription").setValue("");
	dijit.byId("duplicatesObjectClass").setValue("");
}

scriptScopeDuplicates.saveChanges = function() {
	// Get the new name from the name input field
	// Store the new value in the backend
	// If an error occured -> abort
	// If it was successful update the table, refresh the tree and select the updated node in the tree
	var newObjectName = dojo.trim(dijit.byId("duplicatesObjectName").getValue());
	var selectedRow = UtilGrid.getSelectedRowIndexes("duplicatesListTable")[0];
	var selectedObject = UtilGrid.getSelectedData("duplicatesListTable")[0];

	if (newObjectName && selectedObject) {
		var objectUuid = selectedObject.uuid;
		var def = storeNewObjectNameDef(objectUuid, newObjectName);
		def.addCallback(function() {
			UtilGrid.updateTableDataRow("duplicatesListTable", selectedRow, "title", newObjectName);
			var objectRootNode = dijit.byId("duplicatesTree").rootNode;
			var refreshChildrenDef = dijit.byId("duplicatesTree").refreshChildren(objectRootNode);
			//var treeController = dijit.byId("treeDuplicatesController");
			//var refreshChildrenDef = treeController.refreshChildren(objectRootNode);
			refreshChildrenDef.addCallback(function() {
				selectObjectInTreeByUuid(objectUuid);
			});
			dialog.show("<fmt:message key='general.hint' />","<fmt:message key='dialog.admin.management.duplicates.success' />", dialog.INFO);
		});
		def.addErrback(function(err) {
			console.debug("Error: "+err);
			dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.generalError' />", [err+""]), dialog.WARNING);
		});

	} else {
		dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.invalidObjectNameError' />", dialog.WARNING);
	}
}

function storeNewObjectNameDef(objUuid, objName) {
	var def = new dojo.Deferred();

	ObjectService.updateObjectTitle(objUuid, objName, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function() {
			def.callback();
		},
		errorHandler: function(msg, err) {
			hideLoadingZone();
			console.debug("Error: "+msg);
			def.errback();
		}
	});

	return def;
}

function showLoadingZone() {
    //dojo.html.setVisibility(dojo.byId("duplicatesLoadingZone"), "visible");
    dojo.byId("duplicatesLoadingZone").style.visibility = "visible";
}

function hideLoadingZone() {
    //dojo.html.setVisibility(dojo.byId("duplicatesLoadingZone"), "hidden");
    dojo.byId("duplicatesLoadingZone").style.visibility = "hidden";
}

</script>
</head>

<body>

<!-- CONTENT START -->
	<div id="contentSection" dojoType="dijit.layout.ContentPane" class="contentBlockWhite top">
		<div id="duplicatesContent" dojoType="dijit.layout.BorderContainer" class="content" style="height:100%;">

			<!-- INFO START -->
			<!-- LEFT HAND SIDE CONTENT START -->
			<div id="duplicatesListContainer" dojoType="dijit.layout.ContentPane" region="leading" class="inputContainer" style="padding:5px;">
				<div id="duplicatesLists" dojoType="dijit.layout.TabContainer" style="width: 270px; height: 100%;" selectedChild="duplicatesList1">
                    <span class="outer required"><div><span class="label" style="height:12px;"><fmt:message key="dialog.admin.catalog.management.duplicates.result" /></span></div></span>
					<!-- TAB 1 START -->
					<div id="duplicatesList1" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.admin.catalog.management.duplicates.list" />">
						<div class="tableContainer">
							<div id="duplicatesListTable" autoHeight="13" forceGridHeight="false" class="hideTableHeader" contextMenu="DUPLICATE_GRID"></div>
						</div>
					</div> <!-- TAB 1 END -->
	
	        		<!-- TAB 2 START -->
					<div id="duplicatesList2" dojoType="dijit.layout.ContentPane" class="grey" title="<fmt:message key="dialog.admin.catalog.management.duplicates.tree" />">
	
						<div class="inputContainer grey">
							<div id="duplicatesTree"></div>
						</div>
					</div> <!-- TAB 2 END -->
	   
	        	</div>
			</div>
			<!-- LEFT HAND SIDE CONTENT END -->
	
			<!-- RIGHT HAND SIDE CONTENT START -->
			<div id="duplicatesData" dojoType="dijit.layout.ContentPane" region="center" class="inputContainer field">
				<div id="winNavi" style="top:0;">
	                    <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-2#overall-catalog-management-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
	            </div>
				<div class="inputContainer grey">
				    <span class="outer"><div>
				    <span class="label"><label for="duplicatesObjectName" onclick="javascript:dialog.showContextHelp(arguments[0], 8030)"><fmt:message key="dialog.admin.catalog.management.duplicates.objectName" /></label></span>
					<span class="input spaceBelow"><input type="text" id="duplicatesObjectName" dojoType="dijit.form.ValidationTextBox" style="width:100%;"/></span>
					</div></span>
					<span class="outer"><div>
					<span class="label"><label for="duplicatesObjectClass" onclick="javascript:dialog.showContextHelp(arguments[0], 8031)"><fmt:message key="dialog.admin.catalog.management.duplicates.objectClass" /></label></span>
					<span class="input spaceBelow"><input type="text" id="duplicatesObjectClass" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width:100%;" /></span>
					</div></span>
					<span class="outer"><div>
					<span class="label"><label for="duplicatesObjectDescription" onclick="javascript:dialog.showContextHelp(arguments[0], 8032)"><fmt:message key="dialog.admin.catalog.management.duplicates.objectDescription" /></label></span>
   	           		<span class="input"><input type="text" mode="textarea" id="duplicatesObjectDescription" disabled="true" dojoType="dijit.form.SimpleTextarea" style="width:100%;" /></span> 
					</div></span>
					<div class="fill"></div>
				</div>
	
				<div class="inputContainer">
					<span class="button">
						<span style="float:right;">
							<button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.management.duplicates.saveChanges" />" onClick="javascript:scriptScopeDuplicates.saveChanges();"><fmt:message key="dialog.admin.catalog.management.duplicates.saveChanges" /></button>
						</span>
						<span style="float:right;">
							<button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.management.duplicates.refresh" />" onClick="javascript:scriptScopeDuplicates.startDuplicatesJob();"><fmt:message key="dialog.admin.catalog.management.duplicates.refresh" /></button>
						</span>
						<span id="duplicatesLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
							<img src="img/ladekreis.gif" />
						</span>
					</span>
				</div>
			</div> <!-- RIGHT HAND SIDE CONTENT END -->
		</div>
	</div> <!-- CONTENT END -->
</body>
</html>
