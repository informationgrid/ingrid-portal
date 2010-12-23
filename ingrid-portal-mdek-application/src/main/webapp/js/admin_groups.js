var adminGroupScope = {};


function deleteGroup(group) {
	var def = new dojo.Deferred();
	var deferred = new dojo.Deferred();
	
	var displayText = dojo.string.substituteParams(message.get("dialog.admin.groups.confirmDelete"), group.name);
	dialog.show(message.get("dialog.admin.users.deleteGroup"), displayText, dialog.INFO, [
		{ caption: message.get("general.no"),  action: function() { deferred.errback(); } },	
    	{ caption: message.get("general.ok"), action: function() { deferred.callback(); } }
	]);

	deferred.addCallback(function() {
		SecurityService.deleteGroup(group.id, {
			preHook: showLoadingZone,
			postHook: hideLoadingZone,
			callback: function() {
				def.callback();
			},
			errorHandler: function(errMsg, err) {
				hideLoadingZone();
				displayErrorMessage(err);
				dojo.debug(errMsg);
				displayDeleteGroupError(err);
				def.errback();
			}
		});
	});

	return def;
}

function reloadCurrentGroup() {
	var groupList = dojo.widget.byId("groups");
	var selectedGroups = groupList.getSelectedData();
	if (selectedGroups.length == 1) {
		updateAllInputData(selectedGroups[0]);
	}
}

function addObjectToPermissionTable(obj) {
	var objStore = dojo.widget.byId("groupDataRightsObjectsList").store;

	if (dojo.lang.every(objStore.getData(), function(item){ return (item.uuid != obj.id); })) {
		var permission = {}
		permission.Id = UtilStore.getNewKey(objStore);
		permission.title = obj.title;
		permission.uuid = obj.id;
		permission.single =  "<input type='radio' name='"+obj.id+"' id='"+obj.id+"_single' class='radio' />";
		permission.tree = "<input type='radio' name='"+obj.id+"' id='"+obj.id+"_tree' class='radio' checked='true' />";
        permission.subnode = "<input type='radio' name='"+obj.id+"' id='"+obj.id+"_subnode' class='radio' />";

		objStore.addData(permission);
	}
}


function addAddressToPermissionTable(adr) {
	var adrStore = dojo.widget.byId("groupDataRightsAddressesList").store;

	if (dojo.lang.every(adrStore.getData(), function(item){ return (item.uuid != adr.id); })) {
		var permission = {}
		permission.Id = UtilStore.getNewKey(adrStore);
		permission.title = adr.title;
		permission.uuid = adr.id;
		permission.single =  "<input type='radio' name='"+adr.id+"' id='"+adr.id+"_single' class='radio' />";
		permission.tree = "<input type='radio' name='"+adr.id+"' id='"+adr.id+"_tree' class='radio' checked='true' />";
        permission.subnode = "<input type='radio' name='"+adr.id+"' id='"+adr.id+"_subnode' class='radio' />";

		adrStore.addData(permission);
	}
}

function addFreeRootToPermissionTable(node) {
	var deferred = new dojo.Deferred();

	dojo.debug("Getting all free addresses");
    TreeService.getSubTree(node.id, node.nodeAppType, {
        callback:function(res) { deferred.callback(res); },
        errorHandler:function(message) {
            deferred.errback(new dojo.RpcError(message, this));
        }
    });

    deferred.addCallback(
    	function(res) {
    		dojo.debug("Adding free addresses to permission table"); 
    		dojo.lang.forEach(res, function(adr){
    		    addAddressToPermissionTable(adr);
    		});        	 
        }
    );
    deferred.addErrback(function(res) { dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING); dojo.debug(res); return res;});
    return deferred;
}

// 'Create new group' button function.
// If a group is selected, reset the input fields and deselect any selected groups
// Otherwise create a new group
adminGroupScope.newGroup = function() {
	var groupList = dojo.widget.byId("groups");
	var groupStore = groupList.store;
	var groupName = dojo.string.trim(dojo.widget.byId("groupDataName").getValue());

	if (groupList.getSelectedData().length != 1) {
		createNewGroup(groupName);

	} else {
		groupList.resetSelections();
		groupList.renderSelections();
	
		resetAllInputData();
		hidePermissionLists();
	}
}


// 'Save group' button function.
// If a group is selected update it with the new name.
// If zero or multiple groups are selected, create a new group.
adminGroupScope.saveGroup = function() {
	var groupList = dojo.widget.byId("groups");
	var groupStore = groupList.store;
	var groupName = dojo.string.trim(dojo.widget.byId("groupDataName").getValue());

	var selectedGroups = groupList.getSelectedData();
	if (selectedGroups.length == 1) {
		// exactly one group selected
//		var group = selectedGroups[0];
		var group = curSelectedGroupDetails;
		
		// update group name
		group.name = groupName;

		// update object permissions
		var objs = dojo.widget.byId("groupDataRightsObjectsList").store.getData();
		var objPermissionList = [];
	
		dojo.lang.forEach(objs, function(obj){
			var id = obj.uuid;
			var permissionType;
			if (dojo.byId(obj.uuid+"_tree").checked) {
			  permissionType = "WRITE_TREE";
			} else if (dojo.byId(obj.uuid+"_subnode").checked) {
			  permissionType = "WRITE_SUBNODE";
			} else {
              permissionType = "WRITE_SINGLE";
			} 
			objPermissionList.push( { uuid:id, permission:permissionType } );
		});
		group.objectPermissions = objPermissionList;

		// update address permissions
		var adrs = dojo.widget.byId("groupDataRightsAddressesList").store.getData();
		var adrPermissionList = [];

		dojo.lang.forEach(adrs, function(adr){
			var id = adr.uuid;
            var permissionType;
            if (dojo.byId(adr.uuid+"_tree").checked) {
              permissionType = "WRITE_TREE";
            } else if (dojo.byId(adr.uuid+"_subnode").checked) {
              permissionType = "WRITE_SUBNODE";
            } else {
              permissionType = "WRITE_SINGLE";
            } 
			adrPermissionList.push( { uuid:id, permission:permissionType } );
		});
		group.addressPermissions = adrPermissionList;

		// update group permissions
		group.groupPermissions = [];
		if (dojo.widget.byId("userDataCreate").checked) {
			group.groupPermissions.push("CREATE_ROOT");
		}
		if (dojo.widget.byId("userDataQS").checked) {
			group.groupPermissions.push("QUALITY_ASSURANCE");
		}


		var def = storeGroup(group, true);
		def.addCallback(function(storedGroup) {
			groupStore.update(selectedGroups[0], "name", storedGroup.name);
			curSelectedGroupDetails = storedGroup;

			dialog.show(message.get("dialog.storeGroupTitle"), message.get("dialog.storeGroupSuccess"), dialog.INFO, [
	        	{ caption: message.get("general.ok"),  action: function() {} }
			]);
		});

		def.addErrback(function(err) {
			dojo.debug("Error: "+err);
			if (err && err.message) {
				if (err.message.indexOf("NO_RIGHT_TO_REMOVE_USER_PERMISSION") != -1
				 || err.message.indexOf("NO_RIGHT_TO_ADD_USER_PERMISSION") != -1
				 || err.message.indexOf("NO_RIGHT_TO_REMOVE_OBJECT_PERMISSION") != -1
				 || err.message.indexOf("NO_RIGHT_TO_REMOVE_ADDRESS_PERMISSION") != -1) {
					reloadCurrentGroup();
				}
			}
		});

	} else {
		// zero or multiple groups selected
		createNewGroup(groupName);
	}
}


// 'Add Object' button function.
adminGroupScope.addObject = function() {
	var obj = dojo.widget.byId("treeObjects").selectedNode;

	if (obj == null || obj.id == "objectRoot") {
		return;
	
	} else {
		addObjectToPermissionTable(obj);
	}
}

// 'Add Address' button function.
adminGroupScope.addAddress = function() {
	var adr = dojo.widget.byId("treeAddresses").selectedNode;

	if (adr == null || adr.id == "addressRoot") {
		return;
	} else if (adr.id == "addressFreeRoot") {
		addFreeRootToPermissionTable(adr);
	} else {
		addAddressToPermissionTable(adr);
	}
}

// Creates a new group and returns a deferred obj which is called with the newly created group
function createNewGroup(groupName) {
	var deferred = new dojo.Deferred();

	if (groupName.length == 0) {
		return;
	}

	SecurityService.createGroup( { name: groupName }, true, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(data) {
			var groupList = dojo.widget.byId("groups");
			var groupStore = groupList.store;
			data.Id = UtilStore.getNewKey(groupStore);
			groupStore.addData(data);
			deferred.callback(data);

			groupList.resetSelections();
			groupList.select(data);
			groupList.renderSelections();

		},
		errorHandler: function(errMsg, err) {
			hideLoadingZone();
			displayCreateGroupErrorMessage(err);
/*
			dojo.debug(errMsg);
			dojo.debugShallow(err);
*/
			deferred.errback(err);
		}
	});	

	return deferred;
}

function initObjectTree() {
	var tree = dojo.widget.byId("treeObjects");
	var treeController = dojo.widget.byId("treeControllerObjects");

	// initially load data (first hierarchy level) from server if the user is catAdmin
	if (currentUser.role == 1) {
		// catAdmin. Display all nodes starting from the rootNode
		TreeService.getSubTree(null, null, function (str) {
			tree.setChildren([str[0]]);
		});
	
	} else {
		// mdAdmin. Only display nodes from user's group
		var treeNodes = [];
		dojo.lang.forEach(currentUserGroupDetails.objectPermissions, function(p){
			treeNodes.push(convertObjectPermissionToTreeNode(p));
		});

		tree.setChildren(treeNodes);
	}
	
	
	treeController.loadRemote = function(node, sync) {
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();

		TreeService.getSubTree(node.id, node.nodeAppType, {
//			preHook: UtilDWR.enterLoadingState,
//			postHook: UtilDWR.exitLoadingState,
  			callback:function(res) { deferred.callback(res); },
//			timeout:10000,
			errorHandler:function(message) {
//				UtilDWR.exitLoadingState();
				deferred.errback(new dojo.RpcError(message, this));
			}
  		});
		
		deferred.addCallback(function(res) { return _this.loadProcessResponse(node,res); });
		deferred.addErrback(function(res) { dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING); dojo.debug(res); return res;});
		return deferred;
	};
}

function initAddressTree() {
	var tree = dojo.widget.byId("treeAddresses");
	var treeController = dojo.widget.byId("treeControllerAddresses");

	// initially load data (first hierarchy level) from server if the user is catAdmin
	if (currentUser.role == 1) {
		// catAdmin. Display all nodes starting from the rootNode
		TreeService.getSubTree(null, null, function (str) {
			tree.setChildren([str[1]]);
		});
	
	} else {
		// mdAdmin. Only display nodes from user's group
		var treeNodes = [];
		dojo.lang.forEach(currentUserGroupDetails.addressPermissions, function(p){
			treeNodes.push(convertAddressPermissionToTreeNode(p));
		});

		tree.setChildren(treeNodes);
	}




	treeController.loadRemote = function(node, sync) {
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();

		TreeService.getSubTree(node.id, node.nodeAppType, {
//			preHook: UtilDWR.enterLoadingState,
//			postHook: UtilDWR.exitLoadingState,
  			callback:function(res) { deferred.callback(res); },
//			timeout:10000,
			errorHandler:function(message) {
//				UtilDWR.exitLoadingState();
				deferred.errback(new dojo.RpcError(message, this));
			}
  		});
		
		deferred.addCallback(function(res) { return _this.loadProcessResponse(node,res); });
		deferred.addErrback(function(res) { dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING); dojo.debug(res); return res;});
		return deferred;
	};
}


// Initialize the group table
function initGroupList() {
	var def = getAllGroups();

	def.addCallback(function(groupList) {
		UtilList.addTableIndices(groupList);
		dojo.widget.byId("groups").store.setData(groupList);
	});

	var groupList = dojo.widget.byId("groups");
	var groupStore = groupList.store;

	dojo.event.connectOnce("around", groupList, "deleteRow", function(invocation) {
//		dojo.debug("delete row called on obj:");
//		dojo.debugShallow(invocation.args[0]);
		var def = deleteGroup(invocation.args[0]);
		def.addCallback(function() {
			var result = invocation.proceed();
			return result;		
		});
	});

/*
	dojo.event.connectOnce(groupStore, "onRemoveData", function(item) {
		deleteGroup(item.src);
	});
*/
	// If the user deletes a group that is currently selected, the input fields have to be reset
	dojo.event.connectOnce("after", groupStore, "onRemoveData", function(data) {
		if (dojo.lang.some(groupList.getSelectedData(), function(item){ return (item == data.src); })) {
			resetAllInputData();
			hidePermissionLists();
		}
	});

	// Selection of data via list.select() doesn't fire an 'onDataSelect' event.
	// Therefore we can't rely on the 'onSelect' event and have to update the input manually  
	dojo.event.connectOnce("after", groupList, "onDataSelect", function(item) {
		// Update the displayed data
		updateAllInputData(item);
		showPermissionLists();
	});

	dojo.event.connectOnce("after", groupList, "onSelect", function() {
		// Update the displayed data
		var selectedGroups = groupList.getSelectedData();
		if (selectedGroups.length == 1) {
			// one group selected, load and display it's group data
			updateAllInputData(selectedGroups[0]);
			showPermissionLists();

		} else {
			// zero or multiple groups selected
			resetAllInputData();
			hidePermissionLists();
		}
	});
}

// Fetch all groups from the backend. Returns a dojo.Deferred which is called with the groupList.
function getAllGroups() {
	var deferred = new dojo.Deferred();

	// Don't display the administrator group
	SecurityService.getGroups(false, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(groupList) {
			deferred.callback(groupList);
		},
		errorHandler: function(errMsg, err) {
			hideLoadingZone();
			displayErrorMessage(err);
			dojo.debug(errMsg);
			dojo.debugShallow(err);
			deferred.errback(err);			
		}
	});

	return deferred;
}

// Store the group 'group'
function storeGroup(group, refetch) {
	var deferred = new dojo.Deferred();

	SecurityService.storeGroup(group, refetch, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(group) {
			deferred.callback(group);
		},
		errorHandler: function(errMsg, err) {
			hideLoadingZone();
			displayStoreGroupErrorMessage(err);
/*
			dojo.debug(errMsg);
			dojo.debugShallow(err);
*/
			deferred.errback(errMsg);			
		}
	});

	return deferred;
}


function updateAllInputData(group) {
//	dojo.debugShallow(group);
	dojo.widget.byId("groupDataName").setValue(group.name);

	// Load the group details from the server
	var def = getGroupDetails(group.name);
	def.addCallback(function(groupDetails) {
//		dojo.debug("groupDetails: ");
//		dojo.debugShallow(groupDetails);
		curSelectedGroupDetails = groupDetails;

		setAddressPermissions(groupDetails.addressPermissions);
		setObjectPermissions(groupDetails.objectPermissions);
		setGroupPermissions(groupDetails.groupPermissions);
	});
}


function setAddressPermissions(permissionList) {
	var adrStore = dojo.widget.byId("groupDataRightsAddressesList").store;
	adrStore.clearData();

	dojo.lang.forEach(permissionList, function(p) {
		var adr = {}

		adr.Id = UtilStore.getNewKey(adrStore);
		adr.title = UtilAddress.createAddressTitle(p.address);
		adr.uuid = p.uuid;

		if (p.permission == "WRITE_SINGLE") {
			adr.single =  "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_single' class='radio' checked='true' />";
			adr.tree = "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_tree' class='radio' />";
            adr.subnode = "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_subnode' class='radio' />";
		} else if (p.permission == "WRITE_TREE") {
			adr.single =  "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_single' class='radio' />";
			adr.tree = "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_tree' class='radio' checked='true' />";
            adr.subnode = "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_subnode' class='radio' />";
        } else {
            adr.single =  "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_single' class='radio' />";
            adr.tree = "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_tree' class='radio' />";
            adr.subnode = "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_subnode' class='radio' checked='true' />";
		}

		adrStore.addData(adr);
	});
}


function setObjectPermissions(permissionList) {
	var objStore = dojo.widget.byId("groupDataRightsObjectsList").store;
	objStore.clearData();

	dojo.lang.forEach(permissionList, function(p) {
		var obj = {}

		obj.Id = UtilStore.getNewKey(objStore);
		obj.title = p.object.title;
		obj.uuid = p.uuid;

		if (p.permission == "WRITE_SINGLE") {
			obj.single =  "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_single' class='radio' checked='true' />";
			obj.tree = "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_tree' class='radio' />";
            obj.subnode = "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_subnode' class='radio' />";
		} else if (p.permission == "WRITE_TREE") {
			obj.single =  "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_single' class='radio' />";
			obj.tree = "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_tree' class='radio' checked='true' />";
            obj.subnode = "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_subnode' class='radio' />";
        } else {
            obj.single =  "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_single' class='radio' />";
            obj.tree = "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_tree' class='radio' />";
            obj.subnode = "<input type='radio' name='"+p.uuid+"' id='"+p.uuid+"_subnode' class='radio' checked='true' />";
		}

		objStore.addData(obj);
	});
}

function setGroupPermissions(permissionList) {
	var canCreateRoot = false;
	var isQA = false;

	dojo.lang.forEach(permissionList, function(idcPermission){
		if (idcPermission == "CREATE_ROOT") {
			canCreateRoot = true;
		} else if (idcPermission == "QUALITY_ASSURANCE") {
			isQA = true;
		}
	});

	dojo.widget.byId("userDataCreate").setValue(canCreateRoot);
	dojo.widget.byId("userDataQS").setValue(isQA);
}


function resetAllInputData() {
	dojo.widget.byId("groupDataName").setValue("");

	dojo.widget.byId("groupDataRightsObjectsList").store.clearData();
	dojo.widget.byId("groupDataRightsAddressesList").store.clearData();

	dojo.widget.byId("userDataCreate").setValue(false);
	dojo.widget.byId("userDataQS").setValue(false);
}

function hidePermissionLists() {
	dojo.html.hide(dojo.byId("permissionCheckboxContainer"));
	dojo.html.hide(dojo.byId("permissionListObjects"));
	dojo.html.hide(dojo.byId("permissionListAddresses"));
}

function showPermissionLists() {
	dojo.html.show(dojo.byId("permissionCheckboxContainer"));
	dojo.html.show(dojo.byId("permissionListObjects"));
	dojo.html.show(dojo.byId("permissionListAddresses"));

	dojo.widget.byId("groupDataObjects").onResized();
	dojo.widget.byId("groupDataAddresses").onResized();
}

function getGroupDetails(groupName) {

	var deferred = new dojo.Deferred();

	SecurityService.getGroupDetails(groupName, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(groupDetails) {
			deferred.callback(groupDetails);
		},
		errorHandler: function(errMsg, err) {
			hideLoadingZone();
			displayErrorMessage(err);
			dojo.debug(errMsg);
			dojo.debugShallow(err);
			deferred.errback(err);
		}
	});

	return deferred;
}

function getGroupDetailsById(groupIds) {

	var def = getAllGroups();

	def.addCallback(function(groupList) {
	    if (groupIds == null) {
	       return null;
	    }
        for (var i = 0; i < groupList.length; ++i) {
            for (var j = 0; j < groupIds.length; ++j) {
                if (groupList[i].id == groupIds[j]) {
				    return groupList[i].name;
				}
			}
		}
		return null;
	});

	return def.addCallback(getGroupDetails);
}

function convertObjectPermissionToTreeNode(p) {
	return {
		id: p.object.uuid,
		title: p.object.title,
		isFolder: p.object.hasChildren && (p.permission == "WRITE_TREE" || p.permission == "WRITE_SUBNODE"),
		nodeDocType: p.object.nodeDocType,
		dojoType: "ingrid:TreeNode",
		nodeAppType: "O"
	}
}

function convertAddressPermissionToTreeNode(p) {
	return {
		id: p.address.uuid,
		title: UtilAddress.createAddressTitle(p.address),
		isFolder: p.address.hasChildren && (p.permission == "WRITE_TREE" || p.permission == "WRITE_SUBNODE"),
		nodeDocType: p.address.nodeDocType,
		dojoType: "ingrid:TreeNode",
		nodeAppType: "A"
	}
}


// -- Error handling --
function displayCreateGroupErrorMessage(err) {
	if (err && err.message) {
		if (err.message.indexOf("ENTITY_ALREADY_EXISTS") != -1) {
//			dialog.show(message.get("general.error"), message.get("dialog.noPermissionError"), dialog.WARNING);
			dialog.show(message.get("general.error"), message.get("dialog.admin.groups.groupAlreadyExistsError"), dialog.WARNING);

		} else {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.generalError"), err.message), dialog.WARNING, null, 350, 350);				
		}

	} else {
		// Show general error message if we can't determine what went wrong
		dialog.show(message.get("general.error"), message.get("dialog.undefinedError"), dialog.WARNING);
	}
}

function displayStoreGroupErrorMessage(err) {
	if (err && err.message) {
		if (err.message.indexOf("SINGLE_BELOW_TREE_OBJECT_PERMISSION") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.object.singleBelowTreeError"), err.invalidObject.title, err.rootObject.title), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("TREE_BELOW_TREE_OBJECT_PERMISSION") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.object.treeBelowTreeError"), err.invalidObject.title, err.rootObject.title), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("SINGLE_BELOW_TREE_ADDRESS_PERMISSION") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.address.singleBelowTreeError"), UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("TREE_BELOW_TREE_ADDRESS_PERMISSION") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.address.treeBelowTreeError"), UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("USER_EDITING_OBJECT_PERMISSION_MISSING") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.object.userEditingError"), err.invalidObject.title, UtilAddress.createAddressTitle(err.rootAddress)), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("USER_RESPONSIBLE_FOR_OBJECT_PERMISSION_MISSING") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.object.userResponsibleError"), err.invalidObject.title, UtilAddress.createAddressTitle(err.rootAddress)), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("USER_EDITING_ADDRESS_PERMISSION_MISSING") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.address.userEditingError"), UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("USER_RESPONSIBLE_FOR_ADDRESS_PERMISSION_MISSING") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.address.userResponsibleError"), UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("NO_RIGHT_TO_REMOVE_OBJECT_PERMISSION") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.object.permissionError"), err.rootObject.title), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("NO_RIGHT_TO_REMOVE_ADDRESS_PERMISSION") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.address.permissionError"), UtilAddress.createAddressTitle(err.rootAddress)), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("NO_RIGHT_TO_REMOVE_USER_PERMISSION") != -1) {
			dialog.show(message.get("general.error"), message.get("dialog.admin.groups.removePermissionError"), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("NO_RIGHT_TO_ADD_USER_PERMISSION") != -1) {
			dialog.show(message.get("general.error"), message.get("dialog.admin.groups.addPermissionError"), dialog.WARNING, null, 350, 200);

		} else {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.generalError"), err.message), dialog.WARNING, null, 350, 350);				
		}

	} else {
		// Show general error message if we can't determine what went wrong
		dialog.show(message.get("general.error"), message.get("dialog.undefinedError"), dialog.WARNING);
	}
}

function displayDeleteGroupError(err) {
	if (err && err.message) {
		if (err.message.indexOf("GROUP_HAS_USERS") != -1) {
			var userList = "";
			dojo.lang.forEach(err.addresses, function(adr) {
				userList += UtilAddress.createAddressTitle(adr)+"\n";
			});
			userList = dojo.string.trim(userList);
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.groupHasUsersError"), userList), dialog.WARNING, null, 350, 200);
			
		} else if (err.message.indexOf("USER_EDITING_OBJECT_PERMISSION_MISSING") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.object.userEditingError"), err.invalidObject.title, UtilAddress.createAddressTitle(err.rootAddress)), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("USER_RESPONSIBLE_FOR_OBJECT_PERMISSION_MISSING") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.object.userResponsibleError"), err.invalidObject.title, UtilAddress.createAddressTitle(err.rootAddress)), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("USER_EDITING_ADDRESS_PERMISSION_MISSING") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.address.userEditingError"), UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)), dialog.WARNING, null, 350, 200);

		} else if (err.message.indexOf("USER_RESPONSIBLE_FOR_ADDRESS_PERMISSION_MISSING") != -1) {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.admin.groups.address.userResponsibleError"), UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)), dialog.WARNING, null, 350, 200);

		} else {
			dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.generalError"), err.message), dialog.WARNING, null, 350, 350);				
		}

	} else {
		// Show general error message if we can't determine what went wrong
		dialog.show(message.get("general.error"), message.get("dialog.undefinedError"), dialog.WARNING);
	}
}

function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("adminGroupLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("adminGroupLoadingZone"), "hidden");
}
