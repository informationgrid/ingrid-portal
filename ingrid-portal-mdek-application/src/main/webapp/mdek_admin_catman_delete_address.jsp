<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {
	initTree();
	initTreeEventHandler();
});

function initTree() {
	// Load initial first level of the tree from the server
	TreeService.getSubTree(null, null, 1, 
		function (rootNodeList) {
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

		TreeService.getSubTree(node.uuid, node.nodeAppType, 1, {
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
		var detailedAddressInfoDef = createDetailedAddressInformationDef(addressUuid);
		detailedAddressInfoDef.addCallback(function(detailedAddressInfo) {
			updateDeleteAddressInformation(detailedAddressInfo);
		});
	} else {
		clearDeleteAddressInformation();
	}
}

function replaceNodeSelected(node) {
	if (isValidAddressNode(node)) {
		var addressUuid = node.uuid;
		var detailedAddressInfoDef = createDetailedAddressInformationDef(addressUuid);
		detailedAddressInfoDef.addCallback(function(detailedAddressInfo) {
			updateNewAddressInformation(detailedAddressInfo);
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
	if (detailedAddressInfo.address) {
		dojo.widget.byId("addressDeleteDataTitle").setValue(UtilAddress.createAddressTitle(detailedAddressInfo.address));
		dojo.widget.byId("addressDeleteDataCreationDate").setValue(detailedAddressInfo.address.creationTime);
		dojo.widget.byId("addressDeleteDataID").setValue(detailedAddressInfo.address.uuid);
	}
	if (detailedAddressInfo.responsibleUser) {
		dojo.widget.byId("addressDeleteDataUser").setValue(detailedAddressInfo.responsibleUser.title);
	}
	dojo.widget.byId("addressDeleteDataMDQS").store.setData(detailedAddressInfo.qaUsers);

	if (detailedAddressInfo.objInfoAddressList) {
		UtilList.addIcons(detailedAddressInfo.objInfoAddressList);
		var replaceInfoStore = dojo.widget.byId("replaceInfoAddressList").store;
		replaceInfoStore.setData(detailedAddressInfo.objInfoAddressList);
	}

	if (detailedAddressInfo.objResponsibleAddressList) {
		UtilList.addIcons(detailedAddressInfo.objResponsibleAddressList);
		var replaceInfoStore = dojo.widget.byId("replaceUserList").store;
		replaceInfoStore.setData(detailedAddressInfo.objResponsibleAddressList);
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
//   qaUsers: [ {title:address title, uuid:address uuid}, {...}, ...]
// }
function createDetailedAddressInformationDef(addressUuid) {
	var def = new dojo.Deferred();
	var addressDetailsDef = getAddressDetailsDef(addressUuid);
	var responsibleUsersDef = getUsersWithWritePermissionDef(addressUuid);
	var objInfoAddressDef = getObjectsWithInfoAddressDef(addressUuid);
	var objResponsibleAddressDef = getObjectsWithResponsibleUserDef(addressUuid);

	var defList = new dojo.DeferredList([addressDetailsDef, responsibleUsersDef, objInfoAddressDef, objResponsibleAddressDef], false, false, true);
	defList.addCallback(function (resultList) {
		var addressDetails = resultList[0][1]; 
		var responsibleUserList = resultList[1][1];
		var objInfoAddressList = resultList[2][1];
		var objResponsibleAddressList = resultList[3][1];

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
			objResponsibleAddressList:objResponsibleAddressList
		});
	});

	defList.addErrback(function(err) {
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
			dojo.debug("Error: "+message);
			dojo.debugShallow(err);
			def.errback(err);
		}
	});
	return def;
}

//Fetch objects where given adrUuid is set as the info address
function getObjectsWithInfoAddressDef(adrUuid) {
	var queryStr = buildInfoAddressQuery(adrUuid);
	return getObjectsForHQLQueryDef(queryStr);
}

// Fetch objects where given adrUuid is set as the responsible user
function getObjectsWithResponsibleUserDef(adrUuid) {
	var queryStr = buildResponsibleAddressQuery(adrUuid);
	return getObjectsForHQLQueryDef(queryStr);
}

// Fetch objects for the given hql query. The list of objects is returned through the deferred object.
// If no objects are found, an empty list is returned through the callback
function getObjectsForHQLQueryDef(query) {
	var def = new dojo.Deferred();

	QueryService.queryHQL(query, 0, 100, {
		callback: function(searchResult) {
			if (searchResult.objectSearchResult) {
				def.callback(searchResult.objectSearchResult.resultList);

			} else {
				// No objects found
				def.callback([]);
			}
		},
		errorHandler: function(message, err) {
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

		var query;
		if ("replaceInfoAddress" == selectedChild) {
			query = buildInfoAddressQuery(addressUuid);

		} else if ("replaceUser" == selectedChild) {
			query = buildResponsibleAddressQuery(addressUuid);
		}

		QueryService.queryHQLToCSV(query, {
			preHook: showLoadingZone,
			postHook: hideLoadingZone,
			callback: function(data) {
				dwr.engine.openInDownload(data);
			},
			errorHandler: function(errMsg, err) {
				dojo.debug("Error: "+message);
				dojo.debugShallow(err);
			}		
		});

	} else {
		// No node selected
		dojo.debug("no node selected.");
	}
}

// HQL Query to fetch all objects where the given address uuid is set as the info address (Auskunft)
function buildInfoAddressQuery(addressUuid) {
	return "from ObjectNode oNode "+
		"join oNode.t01ObjectPublished obj "+
		"join obj.t012ObjAdrs objAdr "+
	"where "+
		"oNode.objIdPublished = objAdr.objId "+
		"and objAdr.adrUuid = '"+addressUuid+"' "+
		"and objAdr.type = 7 "+
		"and objAdr.specialRef = 505";
}

//HQL Query to fetch all objects where the given address uuid is set as the responsible user
function buildResponsibleAddressQuery(addressUuid) {
	return "from ObjectNode oNode "+
		"join oNode.t01ObjectPublished obj "+
	"where "+
		"obj.responsibleUuid = '"+addressUuid+"'";
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
	var newNode = dojo.widget.byId("treeAddressDelete").selectedNode;
	var deleteNode = dojo.widget.byId("treeAddressNew").selectedNode;

	if (isValidAddressNode(deleteNode) && isValidAddressNode(newNode) && deleteNode.uuid != newNode.uuid) {
		var def = replaceAddressDef(deleteNode.uuid, newNode.uuid);
		def.addCallback(function() {
			dialog.show(message.get("general.hint"), "Funktion noch nicht implementiert.");
		});

	} else {
		dialog.show(message.get("general.error"), message.get("dialog.admin.catalog.management.deleteAddress.requiredFieldsHint"), dialog.WARNING);
	}
}

function replaceAddressDef(delAdrUuid, newAdrUuid) {
	// TODO implement
	var def = new dojo.Deferred();
	def.callback();
	return def;
}

function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("deleteAddressLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("deleteAddressLoadingZone"), "hidden");
}


</script>
</head>

<body>

<!-- CONTENT START -->
<div dojoType="ContentPane" layoutAlign="client">

	<div id="contentSection" class="contentBlockWhite top">
		<div id="winNavi">
			<a href="#" title="Hilfe">[?]</a>
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
				<div class="inputContainer field grey noSpaceBelow h236">
					<span class="label"><label for="addressDeleteDataTitle" onclick="javascript:dialog.showContextHelp(arguments[0], 'Titel')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.title" /></label></span>
					<span class="input spaceBelow">
						<input type="text" id="addressDeleteDataTitle" class="w640" disabled="true" dojoType="ingrid:ValidationTextbox" />
					</span>
					<div class="inputContainer">
						<div class="half left">
							<span class="label"><label for="addressDeleteDataCreationDate" onclick="javascript:dialog.showContextHelp(arguments[0], 'Erstellt am')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.creationDate" /></label></span>
							<span class="input spaceBelow"><input type="text" id="addressDeleteDataCreationDate" name="addressDeleteDataCreationDate" class="w308" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
							<span class="label"><label for="addressDeleteDataUser" onclick="javascript:dialog.showContextHelp(arguments[0], 'Verantwortlicher')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.responsibleUser" /></label></span>
							<span class="input spaceBelow"><input type="text" id="addressDeleteDataUser" name="addressDeleteDataUser" class="w308" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
							<span class="label"><label for="addressDeleteDataID" onclick="javascript:dialog.showContextHelp(arguments[0], 'ID')">ID</label></span>
							<span class="input"><input type="text" id="addressDeleteDataID" name="addressDeleteDataID" class="w308" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
						</div>
						<div class="half">
							<span class="label"><label for="addressDeleteDataMDQS" onclick="javascript:dialog.showContextHelp(arguments[0], 'Qualit&auml;tssichernder')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.qa" /></label></span>
							<div class="tableContainer headHiddenRows5 w315">
								<table id="addressDeleteDataMDQS" dojoType="ingrid:FilteringTable" minRows="5" headClass="hidden" cellspacing="0" class="filteringTable">
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
			</div>

			<div id="addressDeleteDataObjects" class="inputContainer">
				<span class="label"><label class="inActive" for="objectLists"><fmt:message key="dialog.admin.catalog.management.deleteAddress.objectPreview" /></label></span>
				<span class="functionalLink"><img src="img/ic_fl_save_csv.gif" width="11" height="15" alt="Popup" /><a href="javascript:void(0);" onclick="javascript:scriptScope.openDownloadResultAsCSVDialog();" title="Als CSV-Datei speichern"><fmt:message key="dialog.admin.catalog.management.deleteAddress.saveAsCSV" /></a></span>
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

				</div>
			</div> <!-- RIGHT HAND SIDE CONTENT BLOCK 1 END -->

			<!-- LEFT HAND SIDE CONTENT BLOCK 2 START -->
			<div id="addressNewTree" class="inputContainer">
				<span class="label">Neue Auskunftsadresse (Es können ausschlie&szlig;lich ver&ouml;ffentliche Adressen ausgew&auml;hlt werden)</span>
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
						<span class="label"><label for="addressNewDataTitle" onclick="javascript:dialog.showContextHelp(arguments[0], 'Titel')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.title" /></label></span>
						<span class="input"><input type="text" id="addressNewDataTitle" class="w640" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
						<div class="fill"></div>
					</div>
					<div class="inputContainer spaceBelow">
						<div class="half left">
							<span class="label"><label for="addressNewDataCreationDate" onclick="javascript:dialog.showContextHelp(arguments[0], 'Erstellt am')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.creationDate" /></label></span>
							<span class="input spaceBelow"><input type="text" id="addressNewDataCreationDate" name="addressNewDataCreationDate" class="w308" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
							<span class="label"><label for="addressNewDataUser" onclick="javascript:dialog.showContextHelp(arguments[0], 'Verantwortlicher')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.responsibleUser" /></label></span>
							<span class="input spaceBelow"><input type="text" id="addressNewDataUser" name="addressNewDataUser" class="w308" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
							<span class="label"><label for="addressNewDataID" onclick="javascript:dialog.showContextHelp(arguments[0], 'ID')">ID</label></span>
							<span class="input"><input type="text" id="addressNewDataID" name="addressNewDataID" class="w308" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
						</div>
						<div class="half">
							<span class="label"><label for="addressNewDataMDQS" onclick="javascript:dialog.showContextHelp(arguments[0], 'Qualit&auml;tssichernder')"><fmt:message key="dialog.admin.catalog.management.deleteAddress.qa" /></label></span>
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
						<button dojoType="ingrid:Button" title="Ersetzen" onClick="javascript:scriptScope.replaceAddress();"><fmt:message key="dialog.admin.catalog.management.deleteAddress.replace" /></button>
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
