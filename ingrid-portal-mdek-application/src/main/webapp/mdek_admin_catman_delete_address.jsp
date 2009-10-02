<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;
var MAX_NUM_DATASETS = 100;

_container_.addOnLoad(function() {
	initTree();
	initTreeEventHandler();
});

function initTree() {
	// Load initial first level of the tree from the server
	TreeService.getSubTree(null, null, { 
		callback: function (rootNodeList) {
			dojo.lang.forEach(rootNodeList, function(rootNode){
				rootNode.title = dojo.string.escape("html", rootNode.title);
				rootNode.uuid = rootNode.id;
				rootNode.id = null;
			});

			// Only use 'addresses' and drop 'objects' (rootNodeList[1])
			var addressDeleteTree = dojo.widget.byId("treeAddressDelete");
			addressDeleteTree.setChildren([rootNodeList[1]]);
			var addressReplaceTree = dojo.widget.byId("treeAddressNew");
			addressReplaceTree.setChildren([rootNodeList[1]]);
	      },
	    errorHandler: function(errMsg, err) {
            displayErrorMessage(err);
            dojo.debug(errMsg);
            dojo.debugShallow(err);
        }
	});

	// Function to load children of the node from server
	var loadRemote = function(pubOnly, node, sync){
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();

		deferred.addCallback(function(res) {
			if (pubOnly) {
				// Remove all objects from the list which are not published 
				res = dojo.lang.filter(res, function(adr){
					return (adr.isPublished || adr.id == "addressFreeRoot");
				});
			}

			dojo.lang.forEach(res, function(obj){
				obj.title = dojo.string.escape("html", obj.title);
				obj.uuid = obj.id;
				obj.id = null;
			});
			return _this.loadProcessResponse(node,res);
		});
		deferred.addErrback(function(res) { dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING); dojo.debug(res); return res;});

		TreeService.getSubTree(node.uuid, node.nodeAppType, {
			preHook: showLoadingZone,
			postHook: hideLoadingZone,
  			callback:function(res) { deferred.callback(res); },
			errorHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); },
			exceptionHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); }
  		});

		return deferred;
	};

	// Attach load remote function to the tree controllers
	var addressDeleteTreeController = dojo.widget.byId("treeControllerAddressDelete");
//	addressDeleteTreeController.loadRemote = loadRemote;
	addressDeleteTreeController.loadRemote = dojo.lang.curry(addressDeleteTreeController, loadRemote, false);
	var addressReplaceTreeController = dojo.widget.byId("treeControllerAddressNew");
//	addressReplaceTreeController.loadRemote = loadRemote;
	addressReplaceTreeController.loadRemote = dojo.lang.curry(addressReplaceTreeController, loadRemote, true);
}

function initTreeEventHandler() {
	var addressDeleteTreeListener = dojo.widget.byId("treeListenerAddressDelete");
	dojo.event.topic.subscribe(addressDeleteTreeListener.eventNames.select, function(data) { deleteNodeSelected(data.node); });
	var addressNewTreeListener = dojo.widget.byId("treeListenerAddressNew");
	dojo.event.topic.subscribe(addressNewTreeListener.eventNames.select, function(data) { replaceNodeSelected(data.node); });
}

function deleteNodeSelected(node) {
	if (isValidAddressNode(node)) {
		var addressUuid = node.uuid;
		showLoadingZone();
		var detailedAddressInfoDef = createDetailedAddressInformationDef(addressUuid);
		detailedAddressInfoDef.addCallback(function(detailedAddressInfo) {
			updateDeleteAddressInformation(detailedAddressInfo);
			hideLoadingZone();
		});
	} else {
		clearDeleteAddressInformation();
	}
}

function replaceNodeSelected(node) {
	if (isValidAddressNode(node)) {
		var addressUuid = node.uuid;
		showLoadingZone();
		var detailedAddressInfoDef = createDetailedAddressInformationDef(addressUuid);
		detailedAddressInfoDef.addCallback(function(detailedAddressInfo) {
			updateNewAddressInformation(detailedAddressInfo);
			hideLoadingZone();
		});
	} else {
		clearNewAddressInformation();
	}
}

// Checks whether a given node is a valid address with an associated uuid (valid) or a container node (invalid)
function isValidAddressNode(node) {
	return (node != null && node.uuid != null && node.uuid != "addressRoot" && node.uuid != "addressFreeRoot");
}

// Update the fields for 'address to delete'
// Input parameter is an addressDetails object created by 'createDetailedAddressInformationDef'
function updateDeleteAddressInformation(detailedAddressInfo) {
	// Set general information (title, creation date, Id)
	if (detailedAddressInfo.address) {
		dojo.widget.byId("addressDeleteDataTitle").setValue(UtilAddress.createAddressTitle(detailedAddressInfo.address));
		dojo.widget.byId("addressDeleteDataCreationDate").setValue(detailedAddressInfo.address.creationTime);
		dojo.widget.byId("addressDeleteDataID").setValue(detailedAddressInfo.address.uuid);
	}
	// Set responsible user
	if (detailedAddressInfo.responsibleUser) {
		dojo.widget.byId("addressDeleteDataUser").setValue(detailedAddressInfo.responsibleUser.title);
	}
	// Set qa user list
	dojo.widget.byId("addressDeleteDataMDQS").store.setData(detailedAddressInfo.qaUsers);

	// Set List of info addresses
	if (detailedAddressInfo.objInfoAddressList) {
		UtilList.addIcons(detailedAddressInfo.objInfoAddressList);
		var replaceInfoStore = dojo.widget.byId("replaceInfoAddressList").store;
		replaceInfoStore.setData(detailedAddressInfo.objInfoAddressList);
	}

	// Set List of objects whose responsible user will be replaced
	if (detailedAddressInfo.objResponsibleAddressList) {
		UtilList.addIcons(detailedAddressInfo.objResponsibleAddressList);
		var replaceInfoStore = dojo.widget.byId("replaceUserList").store;
		replaceInfoStore.setData(detailedAddressInfo.objResponsibleAddressList);
	}
	
	// Set List of addresses whose responsible user will be replaced
	if (detailedAddressInfo.addrResponsibleUserList) {
		UtilList.addIcons(detailedAddressInfo.addrResponsibleUserList);
		UtilList.addAddressTitles(detailedAddressInfo.addrResponsibleUserList);
		var replaceInfoStore = dojo.widget.byId("replaceAddressList").store;
		replaceInfoStore.setData(detailedAddressInfo.addrResponsibleUserList);
	}

	if (detailedAddressInfo.objResponsibleAddressList.length >= MAX_NUM_DATASETS
		|| detailedAddressInfo.objInfoAddressList.length >= MAX_NUM_DATASETS
		|| detailedAddressInfo.addrResponsibleUserList.length >= MAX_NUM_DATASETS) {
		dojo.html.setVisibility("maxNumDatasetsWarning", true);
	} else {
		dojo.html.setVisibility("maxNumDatasetsWarning", false);
	}
}

function clearDeleteAddressInformation() {
	dojo.widget.byId("addressDeleteDataTitle").setValue("");
	dojo.widget.byId("addressDeleteDataCreationDate").setValue("");
	dojo.widget.byId("addressDeleteDataID").setValue("");
	dojo.widget.byId("addressDeleteDataUser").setValue("");
	dojo.widget.byId("addressDeleteDataMDQS").store.clearData();
	dojo.widget.byId("replaceInfoAddressList").store.clearData();
	dojo.widget.byId("replaceUserList").store.clearData();
	dojo.widget.byId("replaceAddressList").store.clearData();
}

//Update the fields for 'address to replace'
//Input parameter is an addressDetails object created by 'createDetailedAddressInformationDef'
function updateNewAddressInformation(detailedAddressInfo) {
	if (detailedAddressInfo.address) {
		dojo.widget.byId("addressNewDataTitle").setValue(UtilAddress.createAddressTitle(detailedAddressInfo.address));
		dojo.widget.byId("addressNewDataCreationDate").setValue(detailedAddressInfo.address.creationTime);
		dojo.widget.byId("addressNewDataID").setValue(detailedAddressInfo.address.uuid);
	}
	if (detailedAddressInfo.responsibleUser) {
		dojo.widget.byId("addressNewDataUser").setValue(detailedAddressInfo.responsibleUser.title);
	}
	dojo.widget.byId("addressNewDataMDQS").store.setData(detailedAddressInfo.qaUsers);
}

function clearNewAddressInformation() {
	dojo.widget.byId("addressNewDataTitle").setValue("");
	dojo.widget.byId("addressNewDataCreationDate").setValue("");
	dojo.widget.byId("addressNewDataID").setValue("");
	dojo.widget.byId("addressNewDataUser").setValue("");
	dojo.widget.byId("addressNewDataMDQS").store.clearData();
}

// Returns a deferred object with the following information on callback:
// { address: address details for the given uuid,
//   responsibleUser: {title:address title, uuid:address uuid},
//   qaUsers: [ {title:address title, uuid:address uuid}, {...}, ...],
//   objInfoAddressList: [ {obj1}, {obj2}, ... ],
//   objResponsibleAddressList: [ {obj1}, {obj2}, ... ]
// }
function createDetailedAddressInformationDef(addressUuid) {
	var def = new dojo.Deferred();
	var addressDetailsDef = getAddressDetailsDef(addressUuid);
	var responsibleUsersDef = getUsersWithWritePermissionDef(addressUuid);

	var objInfoAddressDef = getObjectsWithInfoAddressDef(addressUuid);
	var objResponsibleAddressDef = getObjectsWithResponsibleUserDef(addressUuid);
	var addrResponsibleUserDef = getAddressesWithResponsibleUser(addressUuid);

	var defList = new dojo.DeferredList([addressDetailsDef, responsibleUsersDef, objInfoAddressDef, objResponsibleAddressDef, addrResponsibleUserDef], false, false, true);
	defList.addCallback(function (resultList) {
		var addressDetails = resultList[0][1]; 
		var responsibleUserList = resultList[1][1];
		var objInfoAddressList = resultList[2][1];
		var objResponsibleAddressList = resultList[3][1];
		var addrResponsibleUserList = resultList[4][1];

		var responsibleUser = null;
		for (var index = 0; index < responsibleUserList.length; ++index) {
			if (responsibleUserList[index][1] == addressDetails.addressOwner) {
				responsibleUser = { title:responsibleUserList[index][0], uuid:responsibleUserList[index][1] };
			}
		}

		var qaUsers = [];
		for (var index = 0; index < responsibleUserList.length; ++index) {
			if (responsibleUserList[index][2]) {
				qaUsers.push( { title:responsibleUserList[index][0], uuid:responsibleUserList[index][1] } );
			}
		}

		def.callback({
			address:addressDetails,
			responsibleUser:responsibleUser,
			qaUsers:qaUsers,
			objInfoAddressList:objInfoAddressList,
			objResponsibleAddressList:objResponsibleAddressList,
			addrResponsibleUserList:addrResponsibleUserList
		});
	});

	defList.addErrback(function(err) {
	    displayErrorMessage(err);
		dojo.debug("Error: "+err);
		dojo.debugShallow(err);
		def.errback(err);
	});

	return def;
}

function getAddressDetailsDef(addressUuid) {
	var def = new dojo.Deferred();
	AddressService.getAddressData(addressUuid, "false", {
		callback: function(addressDetails){
			def.callback(addressDetails);
		},
		errorHandler: function(message, err) {
		    displayErrorMessage(err);
			dojo.debug("Error: "+message);
			dojo.debugShallow(err);
			def.errback(err);
		}
	});
	return def;
}

function getUsersWithWritePermissionDef(addressUuid) {
	var def = new dojo.Deferred();
	SecurityService.getUsersWithWritePermissionForAddress(addressUuid, false, true, {
		callback: function(userList) {
			var list = [];
			dojo.lang.forEach(userList, function(user){
				var title = UtilAddress.createAddressTitle(user.address);
				var uuid = user.address.uuid;
				list.push([title, uuid, userHasQAPermission(user)]);
			});

			def.callback(list);
		},
		errorHandler: function(message, err) {
		    displayErrorMessage(err);
			dojo.debug("Error: "+message);
			dojo.debugShallow(err);
			def.errback(err);
		}
	});
	return def;
}

//Fetch objects where given adrUuid is set as the info address
function getObjectsWithInfoAddressDef(adrUuid) {
	var def = new dojo.Deferred();
	
	CatalogManagementService.getObjectsOfAuskunftAddress(adrUuid, MAX_NUM_DATASETS, {
		callback: function(objects) {
			def.callback(objects);
		},
		errorHandler: function(message, err) {
		    displayErrorMessage(err);
			dojo.debug("Error: "+message);
			dojo.debugShallow(err);
			def.errback(err);
		}
	});
	
	return def;
}

// Fetch objects where given adrUuid is set as the responsible user
function getObjectsWithResponsibleUserDef(adrUuid) {
	var def = new dojo.Deferred();

	CatalogManagementService.getObjectsOfResponsibleUser(adrUuid, MAX_NUM_DATASETS, {
		callback: function(objects) {
			def.callback(objects);
		},
		errorHandler: function(message, err) {
		    displayErrorMessage(err);
			dojo.debug("Error: "+message);
			dojo.debugShallow(err);
			def.errback(err);
		}
	});
	
	return def;
}

// Fetch objects where given adrUuid is set as the responsible user
function getAddressesWithResponsibleUser(adrUuid) {
	var def = new dojo.Deferred();

	CatalogManagementService.getAddressesOfResponsibleUser(adrUuid, MAX_NUM_DATASETS, {
		callback: function(objects) {
			def.callback(objects);
		},
		errorHandler: function(message, err) {
		    displayErrorMessage(err);
			dojo.debug("Error: "+message);
			dojo.debugShallow(err);
			def.errback(err);
		}
	});
	
	return def;
}

// opens a download dialog for the search result as csv
scriptScope.openDownloadResultAsCSVDialog = function() {
	var selectedChild = dojo.widget.byId("addressDeleteDataObjectsLists").selectedChild;
	var selectedNode = dojo.widget.byId("treeAddressDelete").selectedNode;

	if (isValidAddressNode(selectedNode)) {
		var addressUuid = selectedNode.uuid;

		if ("replaceInfoAddress" == selectedChild) {
			var tableToExport = "OBJECTS_OF_AUSKUNFT_ADDRESS";
		} else if ("replaceUser" == selectedChild) {
			var tableToExport = "OBJECTS_OF_RESPONSIBLE_USER";
		} else if ("replaceAddress" == selectedChild) {
			var tableToExport = "ADDRESSES_OF_RESPONSIBLE_USER";
		}

		CatalogManagementService.getCsvData(addressUuid, tableToExport, {
			preHook: showLoadingZone,
			postHook: hideLoadingZone,
			callback: function(data) {
				dwr.engine.openInDownload(data);
			},
			errorHandler: function(errMsg, err) {
			    displayErrorMessage(err);
				dojo.debug("Error: "+errMsg);
				dojo.debugShallow(err);
			}
		});
	} else {
		// No node selected
		dojo.debug("no node selected.");
	}
}

function userHasQAPermission(user) {
	if (user.permissions) {
		for (var index = 0; index < user.permissions.length; ++index) {
			if (user.permissions[index] == "QUALITY_ASSURANCE") {
				return true;
			}
		}
	}

	return false;
}

scriptScope.replaceAddress = function() {
	var deleteNode = dojo.widget.byId("treeAddressDelete").selectedNode;
	var newNode    = dojo.widget.byId("treeAddressNew").selectedNode;

	if (isValidAddressNode(deleteNode) && isValidAddressNode(newNode) && deleteNode.uuid != newNode.uuid) {
		var def = new dojo.Deferred();
		// ask if user really want to replace address
		dialog.show(message.get("general.hint"), message.get("dialog.admin.catalog.management.deleteAddress.reallyDelete"), dialog.INFO, [
	    	    { caption: message.get("general.no"),  action: function() { def.errback("CANCEL"); } },
	    		{ caption: message.get("general.ok"), action: function() { def.callback(); } }
			]);
			
		// call this only if user pressed ok
		def.addCallback(function() {
			CatalogManagementService.replaceAddress(deleteNode.uuid, newNode.uuid, {
				preHook: showLoadingZone,
				postHook: hideLoadingZone,
				callback: function(data) {
					//initTree();
					var tree = dojo.widget.byId("treeAddressDelete");
					var newSelectNode = deleteNode.parent;
					tree.selectNode(newSelectNode);
					deleteNodeSelected(newSelectNode);
					deleteNode.destroy();
					
					dojo.debug("address has been replaced!");
					dialog.show(message.get("general.hint"), "<fmt:message key="dialog.admin.catalog.management.deleteAddress.successfulReplaced" />");
					
				},
				errorHandler: function(errMsg, err) {
					if (errMsg.indexOf("ADDRESS_IS_IDCUSER_ADDRESS") != -1) {
						dialog.show(message.get("general.error"), "<fmt:message key="dialog.admin.catalog.management.deleteAddress.addressIsIdcUser" />", dialog.WARNING);
					} else if (errMsg.indexOf("NODE_HAS_SUBNODES") != -1) {
						dialog.show(message.get("general.error"), "<fmt:message key="dialog.admin.catalog.management.deleteAddress.nodeHasSubnodes" />", dialog.WARNING);
					} else {
						dialog.show(message.get("general.error"), "<fmt:message key="dialog.admin.catalog.management.deleteAddress.error" />: " + errMsg, dialog.WARNING);
					}
					dojo.debug("Error: "+errMsg);
					dojo.debugShallow(err);
				}
			});
		});
		def.addErrback(function(err) {
			if (err.message != "CANCEL") {
			    displayErrorMessage(err);
				dojo.debug("Error: " + err);
				dojo.debugShallow(err);
			}
		});
	} else {
		dialog.show(message.get("general.error"), message.get("dialog.admin.catalog.management.deleteAddress.requiredFieldsHint"), dialog.WARNING);
	}
}
/*
function replaceAddressDef(delAdrUuid, newAdrUuid) {
	// TODO implement
	var def = new dojo.Deferred();
	def.callback();
	return def;
}
*/
function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("deleteAddressWaitForDataLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("deleteAddressWaitForDataLoadingZone"), "hidden");
}


</script>
</head>

<body>

<!-- CONTENT START -->
<div dojoType="ContentPane" layoutAlign="client">

	<div id="contentSection" class="contentBlockWhite top">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=overall-catalog-management-6#overall-catalog-management-6', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="Hilfe">[?]</a>
		</div>
		<div id="catalogueAdresses" class="content">

			<!-- LEFT HAND SIDE CONTENT BLOCK 1 START -->
			<div class="spacer"></div>
			<div id="addressDeleteTree" class="inputContainer">
				<span class="label"><fmt:message key="dialog.admin.catalog.management.deleteAddress.deleteAddress" /></span>
				<div class="inputContainer grey noSpaceBelow w264 h413 scrollable">
					<div dojoType="ContentPane" id="treeContainerAddressDelete">
						<!-- tree components -->
						<div dojoType="ingrid:TreeController" widgetId="treeControllerAddressDelete" RpcUrl="server/treelistener.php"></div>
						<div dojoType="ingrid:TreeListener" widgetId="treeListenerAddressDelete"></div>	
						<div dojoType="ingrid:TreeDocIcons" widgetId="treeDocIconsAddressDelete"></div>	
						<div dojoType="ingrid:TreeDecorator" listener="treeListenerAddressDelete"></div>

						<!-- tree -->
						<div dojoType="ingrid:Tree" listeners="treeControllerAddressDelete;treeListenerAddressDelete;treeDocIconsAddressDelete" widgetId="treeAddressDelete">
						</div>
					</div>
					<div class="spacer"></div>
				</div>
			</div> <!-- LEFT HAND SIDE CONTENT BLOCK 1 END -->

			<!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
			<div id="addressDeleteData" class="inputContainer">
				<div class="inputContainer field grey noSpaceBelow h220">
					<span style="float: right; z-index: 100; visibility: hidden; margin-top: -7px; margin-right: 22px;" id="deleteAddressWaitForDataLoadingZone">
						<img src="img/ladekreis.gif"/>
					</span>
					<span class="label"><label for="addressDeleteDataTitle" onclick="javascript:dialog.showContextHelp(arguments[0], 8044, 'Titel')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.title" /></label></span>
					<span class="input spaceBelow">
						<input type="text" id="addressDeleteDataTitle" class="w640" disabled="true" dojoType="ingrid:ValidationTextbox" />
					</span>
					<div class="inputContainer">
						<div class="half left">
							<span class="label"><label for="addressDeleteDataCreationDate" onclick="javascript:dialog.showContextHelp(arguments[0], 8045, 'Erstellt am')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.creationDate" /></label></span>
							<span class="input spaceBelow"><input type="text" id="addressDeleteDataCreationDate" name="addressDeleteDataCreationDate" class="w308" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
							<span class="label"><label for="addressDeleteDataUser" onclick="javascript:dialog.showContextHelp(arguments[0], 8046, 'Verantwortlicher')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.responsibleUser" /></label></span>
							<span class="input spaceBelow"><input type="text" id="addressDeleteDataUser" name="addressDeleteDataUser" class="w308" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
							<span class="label"><label for="addressDeleteDataID" onclick="javascript:dialog.showContextHelp(arguments[0], 8047, 'ID')">ID</label></span>
							<span class="input"><input type="text" id="addressDeleteDataID" name="addressDeleteDataID" class="w308" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
						</div>
						<div class="half">
							<span class="label"><label for="addressDeleteDataMDQS" onclick="javascript:dialog.showContextHelp(arguments[0], 8048, 'Qualit&auml;tssichernder')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.qa" /></label></span>
							<div class="tableContainer headHiddenRows5 w315">
								<table id="addressDeleteDataMDQS" dojoType="ingrid:FilteringTable" minRows="5" headClass="hidden" cellspacing="0" class="filteringTable">
									<thead>
										<tr>
											<th field="title" dataType="String" width="315">fmt:message key="dialog.admin.catalog.management.deleteAddress.name /></th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div id="addressDeleteDataObjects" class="inputContainer">
				<span class="label"><label class="inActive" for="objectLists"><fmt:message key="dialog.admin.catalog.management.deleteAddress.objectPreview" /></label></span>
				<span class="functionalLink"><img src="img/ic_fl_save_csv.gif" width="11" height="15" alt="Popup" /><a href="javascript:void(0);" onclick="javascript:scriptScope.openDownloadResultAsCSVDialog();" title="<fmt:message key="dialog.admin.catalog.management.deleteAddress.saveAsCSV" />"><fmt:message key="dialog.admin.catalog.management.deleteAddress.saveAsCSV" /></a></span>
				<div id="addressDeleteDataObjectsLists" dojoType="ingrid:TabContainer" doLayout="false" class="w684" selectedChild="replaceInfoAddress">

					<!-- TAB 1 START -->
					<div id="replaceInfoAddress" dojoType="ContentPane" label="<fmt:message key="dialog.admin.catalog.management.deleteAddress.replaceInfoAddress" />">
						<div dojoType="ContentPane">
							<div class="tableContainer rows4 w684">
								<table id="replaceInfoAddressList" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort">
									<thead>
										<tr>
											<th nosort="true" field="icon" dataType="String" width="30">&nbsp;</th>
											<th nosort="true" field="uuid" dataType="String" width="254">ID</th>
											<th nosort="true" field="objectName" dataType="String" width="400"><fmt:message key="dialog.admin.catalog.management.deleteAddress.objectName" /></th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
					</div> <!-- TAB 1 END -->
  
					<!-- TAB 2 START -->
					<div id="replaceUser" dojoType="ContentPane" label="<fmt:message key="dialog.admin.catalog.management.deleteAddress.replaceResponsibleAddress" />">
						<div dojoType="ContentPane">
							<div class="tableContainer rows4 w684">
								<table id="replaceUserList" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort">
									<thead>
										<tr>
											<th nosort="true" field="icon" dataType="String" width="30">&nbsp;</th>
											<th nosort="true" field="uuid" dataType="String" width="254">ID</th>
											<th nosort="true" field="objectName" dataType="String" width="400"><fmt:message key="dialog.admin.catalog.management.deleteAddress.objectName" /></th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
					</div> <!-- TAB 2 END -->
					
					<!-- TAB 3 START -->
					<div id="replaceAddress" dojoType="ContentPane" label="<fmt:message key="dialog.admin.catalog.management.deleteAddress.replaceResponsibleAddressFromAddress" />">
						<div dojoType="ContentPane">
							<div class="tableContainer rows4 w684">
								<table id="replaceAddressList" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort">
									<thead>
										<tr>
											<th nosort="true" field="icon" dataType="String" width="30">&nbsp;</th>
											<th nosort="true" field="uuid" dataType="String" width="254">ID</th>
											<th nosort="true" field="title" dataType="String" width="400"><fmt:message key="dialog.admin.catalog.management.deleteAddress.objectName" /></th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
					</div> <!-- TAB 3 END -->

				</div>

				<span id="maxNumDatasetsWarning" style="visibility:hidden;"><fmt:message key="dialog.admin.catalog.management.deleteAddress.maxNumHits" /></span>

			</div> <!-- RIGHT HAND SIDE CONTENT BLOCK 1 END -->

			<!-- LEFT HAND SIDE CONTENT BLOCK 2 START -->
			<div id="addressNewTree" class="inputContainer">
				<span class="label"><fmt:message key="dialog.admin.catalog.management.deleteAddress.newInfoAddress" /></span>
				<div class="inputContainer grey noSpaceBelow w264 h236 scrollable">
					<div dojoType="ContentPane" id="treeContainerAddressNew">
						<!-- tree components -->
						<div dojoType="ingrid:TreeController" widgetId="treeControllerAddressNew" RpcUrl="server/treelistener.php"></div>
						<div dojoType="ingrid:TreeListener" widgetId="treeListenerAddressNew"></div>	
						<div dojoType="ingrid:TreeDocIcons" widgetId="treeDocIconsAddressNew"></div>	
						<div dojoType="ingrid:TreeDecorator" listener="treeListenerAddressNew"></div>

						<!-- tree -->
						<div dojoType="ingrid:Tree" listeners="treeControllerAddressNew;treeListenerAddressNew;treeDocIconsAddressNew" widgetId="treeAddressNew">
						</div>
					</div>
					<div class="spacer"></div>
				</div>
			</div> <!-- LEFT HAND SIDE CONTENT BLOCK 2 END -->
 
			<!-- RIGHT HAND SIDE CONTENT BLOCK 2 START -->
			<div id="addressNewData" class="inputContainer">
				<div class="inputContainer field grey noSpaceBelow h220">
					<div class="inputContainer spaceBelow">
						<span class="label"><label for="addressNewDataTitle" onclick="javascript:dialog.showContextHelp(arguments[0], 8049, 'Titel')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.title" /></label></span>
						<span class="input"><input type="text" id="addressNewDataTitle" class="w640" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
						<div class="fill"></div>
					</div>
					<div class="inputContainer spaceBelow">
						<div class="half left">
							<span class="label"><label for="addressNewDataCreationDate" onclick="javascript:dialog.showContextHelp(arguments[0], 8050, 'Erstellt am')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.creationDate" /></label></span>
							<span class="input spaceBelow"><input type="text" id="addressNewDataCreationDate" name="addressNewDataCreationDate" class="w308" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
							<span class="label"><label for="addressNewDataUser" onclick="javascript:dialog.showContextHelp(arguments[0], 8051, 'Verantwortlicher')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.responsibleUser" /></label></span>
							<span class="input spaceBelow"><input type="text" id="addressNewDataUser" name="addressNewDataUser" class="w308" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
							<span class="label"><label for="addressNewDataID" onclick="javascript:dialog.showContextHelp(arguments[0], 8052, 'ID')">ID</label></span>
							<span class="input"><input type="text" id="addressNewDataID" name="addressNewDataID" class="w308" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
						</div>
						<div class="half">
							<span class="label"><label for="addressNewDataMDQS" onclick="javascript:dialog.showContextHelp(arguments[0], 8053, 'Qualit&auml;tssichernder')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.qa" /></label></span>
							<div class="tableContainer headHiddenRows5 w315">
								<table id="addressNewDataMDQS" dojoType="ingrid:FilteringTable" minRows="5" headClass="hidden" cellspacing="0" class="filteringTable">
									<thead>
										<tr>
											<th field="title" dataType="String" width="315">Name</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div> <!-- RIGHT HAND SIDE CONTENT BLOCK 2 END -->

			<div class="inputContainer">
				<span class="button w924" style="height:20px !important;">
					<span style="float:right;">
						<button dojoType="ingrid:Button" title="fmt:message key="dialog.admin.catalog.management.deleteAddress.replace" onClick="javascript:scriptScope.replaceAddress();"><fmt:message key="dialog.admin.catalog.management.deleteAddress.replace" /></button>
					</span>
					<span id="deleteAddressLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
						<img src="img/ladekreis.gif" />
					</span>
				</span>
			</div>
		</div>
	</div>
</div> <!-- CONTENT END -->

</body>
</html>
