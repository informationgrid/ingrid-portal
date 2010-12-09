<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;


var currentSelectedAddressId = null;
var currentSelectedUser = null;
var catAdminUserData = null;
// store cat admins adminstrators group Id for later user, see setTreeRoot() method
var administratorGroupId = null;
var administratorId = null;

var requiredElements = [["userDataAddressLink", "userDataAddressLinkLabel"]];


_container_.addOnLoad(function() {
	initializeUserTree();
	initializeGroupLists(true);

	dojo.widget.byId("userDataAddressLink").isValid = function() { return (!this.isEmpty() && this.getValue() != "Neuer Benutzer"); };
});

function setTreeRoot(catAdmin) {
/*
	dojo.debugShallow(catAdmin);
	dojo.debugShallow(catAdmin.userData);
	dojo.debugShallow(catAdmin.address);
*/
	// Add the data to the tree
	var title = UtilAddress.createAddressTitle(catAdmin.address);
	var role = catAdmin.role;
	var roleName = catAdmin.roleName;
//	var groupId = catAdmin.groupIds[0];
	var groupIds = catAdmin.groupIds;
	// store cat admins adminstrators group Id for later user
	administratorGroupId = groupIds[0];
	// store cat admins Id for later user
	administratorId = catAdmin.id;
	dojo.debug("administrators group id: " + administratorGroupId);
	var portalLogin = catAdmin.userData.portalLogin;
	var hasChildren = catAdmin.hasChildren;

	var rootNode = {
		userId: catAdmin.id,
		addressUuid: catAdmin.addressUuid,
		title: title,
		role: role,
		roleName: roleName,
//		groupId: groupId,
		groupIds: groupIds,
		portalLogin: portalLogin,
		nodeDocType: getDocTypeForRole(role),
		isFolder: hasChildren
	}

	dojo.widget.byId("treeUser").setChildren([rootNode]);
}


// Resets the tree view and loads the catalog administrator from the backend
function initializeUserTree() {

	var def = getCatalogAdmin();
	def.addCallback(function(catAdmin) {
		setTreeRoot(catAdmin);
	});

	var treeController = dojo.widget.byId("treeControllerUser");
	treeController.loadRemote = function(node, sync) {

		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();

		SecurityService.getSubUsers(node.userId, {
			preHook: showLoadingZone,
			postHook: hideLoadingZone,
  			callback:function(res) { deferred.callback(res); },
//			timeout:10000,
			errorHandler:function(message) {
				hideLoadingZone();
				deferred.errback(new dojo.RpcError(message, this));
			}
  		});
		
		deferred.addCallback(function(res) {
			return _this.loadProcessResponse(node, convertUserListToTreeNodes(res));
		});

		deferred.addErrback(function(res) { dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING); dojo.debug(res); return res;});
		return deferred;
	};

	var treeListener = dojo.widget.byId('treeListenerUser');
	dojo.event.topic.subscribe(treeListener.eventNames.select, function(data) { treeNodeSelected(data.node); });
}

function initializeGroupLists(includeCatAdminGroup) {
	var def = getAllGroups(includeCatAdminGroup);

	def.addCallback(function(groupList) {
		var listAvailableGroups = [];
		var listUserGroups = [];
		var groupIdList = currentSelectedUser.groupIds;
		dojo.lang.forEach(groupList, function(item) {
			if (dojo.lang.inArray(groupIdList, item.id)) {
				listUserGroups.push( {"name":item.name, "Id":item.id} );
			} else if (currentSelectedUser.userId != administratorId){
				listAvailableGroups.push( {"name":item.name, "Id":item.id} );
			}
		});
		dojo.widget.byId("availableGroupsList").store.setData(listAvailableGroups);
		dojo.widget.byId("groupsList").store.setData(listUserGroups);
	});

/*
 * Disable forced group for users

	dojo.widget.byId("groupsList").isValid = function() {
		return (this.store.getData().length != 0);
	}
*/	
	return def;
}


// 'Add selected groups' Button onClick function.
//
// This function moves the selected groups from the selection list (left) to the result list (right)
addSelected = function() {
    var availableGroupsTable = dojo.widget.byId("availableGroupsList"); 
    var groupsTable = dojo.widget.byId("groupsList");

    var selectedGroups = availableGroupsTable.getSelectedData();
    if (selectedGroups) {
        dojo.lang.forEach(selectedGroups, function(item) {
            availableGroupsTable.store.removeData(item);
            groupsTable.store.addData(item);
        });
    }
}


// 'Add all groups' Button onClick function.
//
// This function moves all groups from the selection list (left) to the result list (right)
addAll = function() {
    var availableGroupsTable = dojo.widget.byId("availableGroupsList"); 
    var groupsTable = dojo.widget.byId("groupsList");

    var groups = availableGroupsTable.store.getData();
    if (groups) {
        dojo.lang.forEach(groups, function(item) {
            availableGroupsTable.store.removeData(item);
            groupsTable.store.addData(item);
        });
    }
}


// 'Remove selected groups' Button onClick function.
//
// This function moves the selected groups from the result list (right) to the selection list (left)
removeSelected = function() {
    var availableGroupsTable = dojo.widget.byId("availableGroupsList"); 
    var groupsTable = dojo.widget.byId("groupsList");

    var selectedGroups = groupsTable.getSelectedData();
    if (selectedGroups) {
        dojo.lang.forEach(selectedGroups, function(item) {
            if (item.name != "administrators") {
	        	groupsTable.store.removeData(item);
	            availableGroupsTable.store.addData(item);
            } else {
            	dojo.debug("Cannot remove group 'administrators' from user.")
            }
        });
    }
}


// 'Remove all groups' Button onClick function.
//
// This function moves all groups from the result list (right) to the selection list (left)
removeAll = function() {
    var availableGroupsTable = dojo.widget.byId("availableGroupsList"); 
    var groupsTable = dojo.widget.byId("groupsList");

    var groups = groupsTable.store.getData();
    if (groups) {
        dojo.lang.forEach(groups, function(item) {
            if (item.name != "administrators") {
	            groupsTable.store.removeData(item);
	            availableGroupsTable.store.addData(item);
            } else {
            	dojo.debug("Cannot remove group 'administrators' from user.")
            }
        });
    }
}

// 'Delete user' button function
// Get the selected user from the tree and delete it
scriptScope.deleteUser = function() {
	var user = dojo.widget.byId("treeUser").selectedNode;

	// Can't delete the cat admin. This is also checked in the backend.
	if (user == null || user.role == 1 || user.isFolder) {
		dialog.show(message.get("general.error"), message.get("dialog.admin.users.canNotDeleteError"), dialog.WARNING);
		dojo.debug("Can't delete.");
		return;
	}

	var deferred = new dojo.Deferred();

	var displayText = dojo.string.substituteParams(message.get("dialog.admin.users.confirmDelete"), user.title);
	dialog.show(message.get("dialog.admin.users.deleteUser"), displayText, dialog.INFO, [
		{ caption: message.get("general.no"),  action: function() { deferred.errback(); } },	
    	{ caption: message.get("general.ok"), action: function() { deferred.callback(); } }
	]);

	deferred.addCallback(function() {
		SecurityService.deleteUser(user.userId, user.addressUuid, {
			preHook: showLoadingZone,
			postHook: hideLoadingZone,
			callback: function() {
				user.destroy();
				resetInputFields();
				dojo.widget.byId("treeUser").selectedNode = null;
			},
			errorHandler: function(errMsg, err) {
				hideLoadingZone();
				if (errMsg.indexOf("USER_HAS_WRONG_ROLE") != -1 || errMsg.indexOf("USER_HIERARCHY_WRONG") != -1) {
					dialog.show(message.get("general.error"), message.get("dialog.admin.users.noDeletePermissionError"), dialog.WARNING);				
				} else {
				    displayErrorMessage(err);
				}

				dojo.debug(errMsg);
				dojo.debugShallow(err);
			}
		});	
	});
}

// 'Import portal user' button function
scriptScope.importPortalUser = function() {
	var selectedUser = dojo.widget.byId("treeUser").selectedNode;

	if (currentUser.role == 1 && selectedUser == null) {
		dialog.show(message.get("general.error"), message.get("dialog.admin.users.noParentSelectedError"), dialog.WARNING);
		dojo.debug("No node selected.");
		return;
	}

	// In case a mdAdmin wants to create a new user, we have to check if the mdAdmin is selected in the tree
	if (currentUser.role == 2) {		
		if (selectedUser == null || (selectedUser.userId != currentUser.id)) {
			dialog.show(message.get("general.error"), message.get("dialog.admin.users.adminSelectedInvalidUserError"), dialog.WARNING);
			dojo.debug("Can't create users here. Select the correct mdAdmin.");
			return;
		}
	}

	// In case a user with role mdAutor is selected, do nothing and return
	if (selectedUser.role < 1 || selectedUser.role > 2) {
		dialog.show(message.get("general.error"), message.get("dialog.admin.users.authorSelectedAsParentError"), dialog.WARNING);
		dojo.debug("Select a user with role catAdmin or mdAdmin.");
		return;
	}

	// In case a newUserNode already exists, return
	if (selectedUser.id == "newUserNode") {
		dialog.show(message.get("general.error"), message.get("dialog.admin.users.newNodeSelectedAsParentError"), dialog.WARNING);
		dojo.debug("Please save the current user before creating a new one.");
		return;
	}


	var deferred = new dojo.Deferred();
	dialog.showPage(message.get("dialog.admin.users.importUser"), "mdek_admin_import_user_dialog.jsp", 360, 240, true, { resultHandler:deferred });

	deferred.addCallback(function(portalUser){
		var tree = dojo.widget.byId("treeUser");
		var treeController = dojo.widget.byId("treeControllerUser");
		var treeListener = dojo.widget.byId("treeListenerUser");
		var def = treeController.expand(selectedUser);

		def.addCallback(function(deferred){
			dojo.debug("parent group ids: " + selectedUser.groupIds);
			treeController.createChild(selectedUser, "last", createNewUserNode(selectedUser, portalUser));
		});

		def.addBoth(function(data){
			resetInputFields();
			var newNode = dojo.widget.byId("newUserNode");
			tree.selectNode(newNode);
			tree.selectedNode = newNode;
			dojo.event.topic.publish(treeListener.eventNames.select, {node: newNode});
		});
	});
}

// 'Save user' button function
scriptScope.saveUser = function() {
	var selectedUser = dojo.widget.byId("treeUser").selectedNode;
	var login = dojo.widget.byId("userDataLogin").getValue();
	var oldUser = selectedUser.portalLogin; 
	selectedUser.portalLogin = login;

	if (dojo.string.trim(login).length == 0) {
		dialog.show(message.get("general.error"), message.get("dialog.admin.users.noUserSelectedForSaveError"), dialog.WARNING);
		dojo.debug("No user selected for save/import.");
		return;
	}

	if (!isValidUser()) {
		dialog.show(message.get("general.error"), message.get("dialog.admin.users.requiredFieldsError"), dialog.WARNING);
		dojo.debug("Invalid information entered.");
		return;
	}

	var user = {};
	var userData = {};

	if (selectedUser.id == "newUserNode") {
		// If the current selected node is a new node, create it in the db
		user.addressUuid = currentSelectedAddressId;
	    var groupsTable = dojo.widget.byId("groupsList");
	    var groups = groupsTable.store.getData();
		
		var groudIds = UtilList.map(groups, function(group) {
			return group.Id;
			
		});
		dojo.debug(groudIds);


		user.groupIds = groudIds;
		user.roleName = selectedUser.roleName;
		user.role = selectedUser.role;
		user.parentUserId = selectedUser.parentUserId;

		dojo.debugShallow(user);

		SecurityService.createUser(user, login, true, {
			preHook: showLoadingZone,
			postHook: hideLoadingZone,
			callback: function(newUser) {
				var tree = dojo.widget.byId("treeUser");
				var treeListener = dojo.widget.byId("treeListenerUser");
				updateTreeNode(selectedUser, newUser);

				var newNode = dojo.widget.byId("TreeNode_"+newUser.id);
				tree.selectNode(newNode);
				tree.selectedNode = newNode;
				dojo.event.topic.publish(treeListener.eventNames.select, {node: newNode});
				dialog.show(message.get("general.hint"), message.get("dialog.admin.users.createSuccess"), dialog.INFO);
			
			},
			errorHandler: function(errMsg, err) {
				hideLoadingZone();
				if (errMsg.indexOf("USER_HAS_WRONG_ROLE") != -1) {
					dialog.show(message.get("general.error"), message.get("dialog.admin.users.createPermissionError"), dialog.WARNING);				

				} else if (errMsg.indexOf("ENTITY_ALREADY_EXISTS") != -1) {
					dialog.show(message.get("general.error"), message.get("dialog.admin.users.addressCollisionError"), dialog.WARNING);				

				} else {
					dialog.show(message.get("general.error"), message.get("dialog.admin.users.createError"), dialog.WARNING);
				}
				dojo.debug(errMsg);
				dojo.debugShallow(err);
			}
		});

	} else {
		user.id = currentSelectedUser.userId;
		user.addressUuid = currentSelectedAddressId;
	    var groupsTable = dojo.widget.byId("groupsList");
	    var groups = groupsTable.store.getData();
		
		var groudIds = UtilList.map(groups, function(group) {
			return group.Id;
			
		});
		dojo.debug(groudIds);


		user.groupIds = groudIds;
		user.role = currentSelectedUser.role;
		user.parentUserId = currentSelectedUser.parentUserId;

		SecurityService.storeUser(oldUser, user, login, true, {
			preHook: showLoadingZone,
			postHook: hideLoadingZone,
			callback: function(newUser) {
			    // if cat-admin was changed then user must be logged out
			    if (user.role == 1 && (oldUser != login)) {
			    	dialog.show(message.get("general.hint"), message.get("dialog.admin.users.updateCatAdmin"), dialog.WARNING);
			    } else {
			        updateTreeNode(selectedUser, newUser);
			        dialog.show(message.get("general.hint"), message.get("dialog.admin.users.updateSuccess"), dialog.INFO);
			    }				

			},
			errorHandler: function(errMsg, err) {
				hideLoadingZone();
				if (errMsg.indexOf("USER_HAS_WRONG_ROLE") != -1 || errMsg.indexOf("USER_HIERARCHY_WRONG") != -1) {
					dialog.show(message.get("general.error"), message.get("dialog.admin.users.updatePermissionError"), dialog.WARNING);				

				} else if (errMsg.indexOf("ENTITY_ALREADY_EXISTS") != -1) {
					dialog.show(message.get("general.error"), message.get("dialog.admin.users.addressCollisionError"), dialog.WARNING);
				} else {
					dialog.show(message.get("general.error"), message.get("dialog.admin.users.updateError"), dialog.WARNING);
				}
				dojo.debug(errMsg);
				dojo.debugShallow(err);
				selectedUser.portalLogin = oldUser;
				treeNodeSelected(currentSelectedUser);
			}
		});		
	}
}

function updateTreeNode(user, newUser) {
	if (user.id == "newUserNode") {
		var parent = user.parent;

		// A new node is a leaf node. It's safe to destroy it
		user.destroy();
		var treeController = dojo.widget.byId("treeControllerUser");
		treeController.createChild(parent, "last", convertUserToTreeNode(newUser));
	
	} else {
		user.setTitle(UtilAddress.createAddressTitle(newUser.address));
		user.addressUuid = newUser.addressUuid;
		user.groupIds = newUser.groupIds;
		if (newUser.userPermissions != null) {
			dojo.lang.forEach(newUser.userPermissions, function(p) {
				if (p == ("CREATE_ROOT")) {
					user.createRoot = true;
				}
			});
		}
		
		if (user.role == 1) {
			// The catAdmin address was changed. This can only be done by the catAdmin himself
			// -> Update the current user data
			var def = initCurrentUser();
			def.addCallback(initPageHeader);
		}
	}

	updateInputElements(user);
}

// Check all input fields for their validity
function isValidUser() {
	resetRequiredElements();

	var valid = true;

	dojo.lang.forEach(requiredElements, function(element) {
		if (!dojo.widget.byId(element[0]).isValid()) {
			dojo.html.addClass(dojo.byId(element[1]), "important");		
			valid = false;
		}
	});

	return valid;
}

// Resolves the current role id from the input string
function getRoleId(roleName) {
	if (roleName == message.get("security.role.catalogAdmin")) {
		return 1;

	} else if (roleName == message.get("security.role.metadataAdmin")) {
		return 2;

	} else if (roleName == message.get("security.role.metadataAuthor")) {
		return 3;
	}
}

// Resolves the role name from the input id
function getRoleName(roleId) {
	switch(roleId) {
		case 1:
			return message.get("security.role.catalogAdmin");
		case 2:
			return message.get("security.role.metadataAdmin");
		case 3:
			return message.get("security.role.metadataAuthor");
		default:
			return null;
	}
}

// Resets all labels that are tagged as 'important' to their initial state
function resetRequiredElements() {
	dojo.lang.forEach(requiredElements, function(element) {
		dojo.html.removeClass(dojo.byId(element[1]), "important");		
	});
}

// Function that is called when a treeNode is clicked
function treeNodeSelected(treeNode) {
	// Check if a 'newUserNode' exists. If it does and the user clicked another node, delete the newUserNode.
	var newUserNode = dojo.widget.byId("newUserNode");
	if (newUserNode != null && treeNode.id != "newUserNode") {
		newUserNode.destroy();
	}

	updateInputElements(treeNode);

	// Delete the displayed address link since it's build from the node title ('Neuer Benutzer')
	if (newUserNode != null && treeNode.id == "newUserNode") {
		dojo.widget.byId("userDataAddressLink").setValue("");
	}
}

// Updates the input elements with the data from 'treeNode'
function updateInputElements(treeNode) {

	currentSelectedUser = treeNode;
	
	if (treeNode.role == 1) {
		// Standards for catAdmin
		var def = initializeGroupLists(true);
		dojo.widget.byId("availableGroupsList").disable();
		dojo.widget.byId("groupsList").disable();

	} else {
		var def = initializeGroupLists(false);
		dojo.widget.byId("availableGroupsList").enable();
		dojo.widget.byId("groupsList").enable();
	}
	
	

	def.addCallback(function() {
		dojo.widget.byId("userDataLogin").setValue(treeNode.portalLogin);
		dojo.widget.byId("userDataAddressLink").setValue(treeNode.title);
		dojo.widget.byId("userDataRole").setValue(treeNode.roleName);
//		dojo.widget.byId("userDataGroup").setValue(treeNode.groupId);
		currentSelectedAddressId = treeNode.addressUuid;
//		currentSelectedUser = treeNode;
	});
}


function resetInputFields() {
	dojo.widget.byId("userDataLogin").setValue("");
	dojo.widget.byId("userDataAddressLink").setValue("");
	dojo.widget.byId("userDataRole").setValue("");
	dojo.widget.byId("groupsList").store.clearData();
	dojo.widget.byId("availableGroupsList").store.clearData();
	currentSelectedAddressId = null;
	currentSelectedUser = null;
}

scriptScope.searchForAddress = function() {
	var def = new dojo.Deferred();
	dialog.showPage(message.get('general.searchAddress'), 'mdek_address_dialog.jsp', 755, 580, true, { resultHandler: def });

	def.addCallback(getAddressData);
	def.addCallback(function(addressData) {
		currentSelectedAddressId = addressData.uuid;
		var title = UtilAddress.createAddressTitle(addressData);
		dojo.widget.byId("userDataAddressLink").setValue(title);
	});
}

scriptScope.searchForPortalUser = function() {
	var selectedUser = dojo.widget.byId("treeUser").selectedNode;

	if (currentUser.role == 1 && selectedUser == null) {
		dialog.show(message.get("general.error"), message.get("dialog.admin.users.noParentSelectedError"), dialog.WARNING);
		dojo.debug("No node selected.");
		return;
	}
	
	var deferred = new dojo.Deferred();
	dialog.showPage(message.get("dialog.admin.users.importUser"), "mdek_admin_import_user_dialog.jsp", 360, 240, true, { resultHandler:deferred });
	
	deferred.addCallback(function(portalUser) {
		dojo.debug("user chosen: " + portalUser);
		dojo.widget.byId("userDataLogin").setValue(portalUser);
	});
}

function getAllGroups(includeCatAdminGroup) {
	var deferred = new dojo.Deferred();

	SecurityService.getGroups(includeCatAdminGroup, {
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

function getUserDataForAddress(adrUuid) {
	var deferred = new dojo.Deferred();

	SecurityService.getUserDataForAddress( adrUuid, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(userData) {
			deferred.callback(userData);
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

function getAddressData(adrUuid) {
	var deferred = new dojo.Deferred();

	AddressService.getAddressData( adrUuid, null, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(adr) {
			deferred.callback(adr);
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

function getCatalogAdmin() {
	var deferred = new dojo.Deferred();

	SecurityService.getCatalogAdmin( {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(user) {
			deferred.callback(user);
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

function convertUserListToTreeNodes(userList) {
	var treeNodes = [];

	dojo.lang.forEach(userList, function(user) {
		treeNodes.push(convertUserToTreeNode(user));
	});

	// Return a sorted list according to the users titles
	return treeNodes.sort(function(a, b) { return UtilString.compareIgnoreCase(a.title, b.title); });
}

function convertUserToTreeNode(user) {
	var portalLogin = "?";
	var notExists = true;
	if (user.userData != null) {
		portalLogin = user.userData.portalLogin;
		notExists = false;
	}
	
	return {
		userId: user.id,
		parentUserId: user.parentUserId,
		addressUuid: user.addressUuid,
		title: UtilAddress.createAddressTitle(user.address),
		role: user.role,
		roleName: user.roleName,
//		groupId: user.groupIds[0],
		groupIds: user.groupIds,
		portalLogin: portalLogin,
		nodeDocType: getDocTypeForRole(user.role),
		isFolder: user.hasChildren,
		id: "TreeNode_"+user.id,
		noPortalLogin: notExists
	}
}

function createNewUserNode(parentNode, login) {
	return {
		id: "newUserNode",
		parentUserId: parentNode.userId,
//		addressUuid: user.addressUuid,
		title: "Neuer Benutzer",
		role: parentNode.role + 1,
		roleName: getRoleName(parentNode.role + 1),
//		groupId: parentNode.groupId,
		groupIds: UtilList.map(parentNode.groupIds, function(groupId) {if (groupId != administratorGroupId) return groupId}),
		createRoot: false,
		portalLogin: login,
		nodeDocType: getDocTypeForRole(parentNode.role + 1),
		isFolder: false
	}
}

function getDocTypeForRole(roleId) {
	switch (roleId) {
		case 1:
			return "KatalogAdmin";
		case 2:
			return "MDAdmin";
		case 3:
			return "MDAutor";
		default:
			return null;
	}
}

function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("adminUserLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("adminUserLoadingZone"), "hidden");
}

</script>
</head>

<body>

<!-- CONTENT START -->
<div dojoType="ContentPane" layoutAlign="client">

	<div id="contentSection" class="contentBlockWhite top">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=user-administration-1#user-administration-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
		</div>
		<div id="userAdmin" class="content">

			<!-- LEFT HAND SIDE CONTENT BLOCK 1 START -->
			<div class="spacer"></div>
			<div class="spacer"></div>
		
		    <div class="inputContainer grey noSpaceBelow w264 h349 scrollable">
				<div dojoType="ContentPane" id="treeContainerUser">
					<!-- tree components -->
			        <div dojoType="ingrid:TreeController" widgetId="treeControllerUser"></div>
			        <div dojoType="ingrid:TreeListener" widgetId="treeListenerUser"></div>	
			        <div dojoType="ingrid:TreeDocIcons" widgetId="treeDocIconsUser"></div>	
			        <div dojoType="ingrid:TreeDecorator" listener="treeListenerUser"></div>
		
			        <!-- tree -->
			        <div dojoType="ingrid:Tree" listeners="treeControllerUser;treeListenerUser;treeDocIconsUser" widgetId="treeUser">
					</div>
		    	</div>
				<div class="spacer"></div>
			</div>
			<div class="inputContainer">
				<span class="button w224" style="height:20px !important;">
					<span style="float:left;">
						<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.users.delete" />" onClick="javascript:scriptScope.deleteUser();"><fmt:message key="dialog.admin.users.delete" /></button>
					</span>
					<span style="float:right;">
						<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.users.create" />" onClick="javascript:scriptScope.importPortalUser();"><fmt:message key="dialog.admin.users.create" /></button>
					</span>
				</span>
			</div>

		    <!-- LEFT HAND SIDE CONTENT BLOCK 1 END -->
	
		    <!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
			<div id="userData" class="inputContainer" style="float:right;">
				<span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 8012)"><fmt:message key="dialog.admin.users.userData" /></label></span>
				<div class="inputContainer field grey noSpaceBelow">
					<span class="label"><label for="userDataLogin" onclick="javascript:dialog.showContextHelp(arguments[0], 8013, 'Login')"><fmt:message key="dialog.admin.users.login" /></label></span>
					<span class="functionalLink marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:void(0);" onclick="javascript:scriptScope.searchForPortalUser();" title="<fmt:message key="dialog.admin.users.searchPortalUser" /> [Popup]"><fmt:message key="dialog.admin.users.searchPortalUser" /></a></span>
					<span class="input spaceBelow"><input type="text" id="userDataLogin" name="userDataLogin" class="w640" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
					<span class="label"><label for="userDataRole" onclick="javascript:dialog.showContextHelp(arguments[0], 8014, 'Rolle')"><fmt:message key="dialog.admin.users.role" /></label></span>
					<span class="input spaceBelow"><input type="text" id="userDataRole" name="userDataRole" class="w640" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>

					<span class="label required"><label id="userDataAddressLinkLabel" for="userDataAddressLink" onclick="javascript:dialog.showContextHelp(arguments[0], 8015, 'Adressverweis')"><fmt:message key="dialog.admin.users.address" />*</label></span>
					<span class="functionalLink marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:void(0);" onclick="javascript:scriptScope.searchForAddress();" title="<fmt:message key="dialog.admin.users.searchAddress" /> [Popup]"><fmt:message key="dialog.admin.users.searchAddress" /></a></span>
					<span class="input spaceBelow"><input type="text" id="userDataAddressLink" name="userDataAddressLink" class="w640" disabled="true" dojoType="ingrid:ValidationTextBox" /></span>
					
                    <span class="entry first">
                      <span class="label"><label id="availableGroupsListLabel" for="availableGroupsList" onclick="javascript:dialog.showContextHelp(arguments[0], 8016, 'Gruppen')"><fmt:message key="dialog.admin.users.available.groups"/></label></span>
                        <div class="tableContainer headHiddenRows10 third303">
                        <table id="availableGroupsList" dojoType="ingrid:FilteringTable" minRows="10" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive relativePos">
                          <thead>
                              <tr>
                                <th nosort="true" field="name" dataType="String">Name</th>
                              </tr>
                          </thead>
                          <colgroup>
                            <col width="100%">
                          </colgroup>
                          <tbody>
                          </tbody>
                        </table>
                        </div>
                    </span>
                
                    <span class="entry">
                      <span class="buttonCol" style="margin:80px -4px 0px;">
                        <button dojoType="ingrid:Button" id="addSelectedButton" onClick="addSelected">&nbsp;>&nbsp;</button>
                        <button dojoType="ingrid:Button" id="addAllButton" onClick="addAll">>></button>
                        <button dojoType="ingrid:Button" id="removeAllButton" onClick="removeAll"><<</button>
                        <button dojoType="ingrid:Button" id="removeSelectedButton" onClick="removeSelected">&nbsp;<&nbsp;</button>
                      </span>
                    </span>
                
                    <span class="entry">
                      <span class="label"><label id="groupsListLabel" for="groupsList" onclick="javascript:dialog.showContextHelp(arguments[0], 8016, 'Gruppen')"><fmt:message key="dialog.admin.users.groups" /></label></span>
                        <div class="tableContainer headHiddenRows10 third303">
                        <table id="groupsList" dojoType="ingrid:FilteringTable" minRows="10" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive relativePos">
                          <thead>
                              <tr>
                                <th nosort="true" field="name" dataType="String">Name</th>
                              </tr>
                          </thead>
                          <colgroup>
                            <col width="100%">
                          </colgroup>
                          <tbody>
                          </tbody>
                        </table>
                        </div>
                    </span>

			        <div class="fill"></div>
				</div>

				<div class="inputContainer">
					<span class="button w644" style="height:20px !important;">
						<span style="float:right;">
							<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.users.save" />" onClick="javascript:scriptScope.saveUser();"><fmt:message key="dialog.admin.users.save" /></button>
						</span>
						<span id="adminUserLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
							<img src="img/ladekreis.gif" />
						</span>
					</span>
				</div>
			</div>
		    <!-- RIGHT HAND SIDE CONTENT BLOCK 1 END -->
	
		</div>
	</div>
</div>
<!-- CONTENT END -->

</body>
</html>
