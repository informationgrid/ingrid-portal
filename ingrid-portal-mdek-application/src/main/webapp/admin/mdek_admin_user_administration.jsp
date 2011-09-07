<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <script type="text/javascript">
            dojo.require("dijit.form.Select");
            dojo.require("dijit.form.ValidationTextBox");
            dojo.require("ingrid.dijit.tree.LazyTreeStoreModel");
            dojo.require("ingrid.dijit.CustomTree");
            var userDataGroupData;
            
            var scriptScopeUser = _container_;
            
            var currentSelectedAddressId = null;
            var currentSelectedUser = null;
            // store cat admins adminstrators group Id for later user, see setTreeRoot() method
            var administratorGroupId = null;
            var administratorId = null;
            var catAdminUserData = null;
            
            var requiredElements = [["userDataAddressLink", "userDataAddressLinkLabel"]];
            
            dojo.connect(_container_, "onLoad", function(){
                createGrids();
                
                createCustomTree("treeUser", null, "userId", "title", loadUserData);
                
                createGroupLists();
                
                initializeUserTree();
                
                // select first user?
                //tree.attr("path", [tree.rootNode.item, tree.rootNode.getChildren()[0].item]);
                dijit.byId("userDataAddressLink").validator = function() { return (this.getValue() != "" && this.getValue() != "Neuer Benutzer"); };
            });
            
            
            function createGrids() {
                var tableStructure = [
                    {field: 'name',name: "<fmt:message key='dialog.admin.users.name' />",width: 350-scrollBarWidth+'px'}
                ];
                createDataGrid("availableGroupsList", null, tableStructure, null);
                createDataGrid("groupsList", null, tableStructure, null);
            }
            
            function loadUserData(node, callback_function){
                var parentItem = node.item;
                var store = this.tree.model.store;
                
                if (parentItem.root) {
                    callback_function();
                    return;
                }
                console.debug("parentItem");
                console.debug(parentItem);
                SecurityService.getSubUsers(parentItem.userId[0], {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(res){
                        var userNodes = convertUserListToTreeNodes(res);
                        dojo.forEach(userNodes, function(userNode){
                            if (parentItem.root && parentItem.root) 
                                store.newItem(userNode);
                            else 
                                store.newItem(userNode, {
                                    parent: parentItem,
                                    attribute: "children"
                                });
                        });
                        callback_function();
                        //deferred.callback(res);
                    },
                    //          timeout:10000,
                    errorHandler: function(message){
                        hideLoadingZone();
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='tree.loadError' />", dialog.WARNING);
                        console.debug(res);
                        return res;
                    }
                });
            }
            
            
            function setTreeRoot(catAdmin){
                console.debug(catAdmin);
                // Add the data to the tree
                var title = UtilAddress.createAddressTitle(catAdmin.address);
                var role = catAdmin.role;
                var roleName = catAdmin.roleName;
                //  var groupId = catAdmin.groupIds[0];
                var groupIds = catAdmin.groupIds;
                // store cat admins adminstrators group Id for later user
                administratorGroupId = groupIds[0];
                // store cat admins Id for later user
                administratorId = catAdmin.id;
                console.debug("administrators group id: " + administratorGroupId);

                var portalLogin = catAdmin.userData.portalLogin;
                var hasChildren = catAdmin.hasChildren;
                
                var rootNode = {
                    userId: catAdmin.id,
                    addressUuid: catAdmin.addressUuid,
                    title: title,
                    role: role,
                    roleName: roleName,
                    groupIds: groupIds,
                    portalLogin: portalLogin,
                    nodeDocType: getDocTypeForRole(role),
                    isFolder: hasChildren
                }
                
                //console.debug("isFolder:" + rootNode.isFolder);
                dijit.byId("treeUser").model.store.newItem(rootNode);
            }
            
            
            // Resets the tree view and loads the catalog administrator from the backend
            function initializeUserTree(){
            
                var def = getCatalogAdmin();
                def.addCallback(function(catAdmin){
                    setTreeRoot(catAdmin);
                });
                
                dojo.connect(dijit.byId("treeUser"), "onClick", treeNodeSelected);
            }
            
            function createGroupLists() {
                var storeProps = { identifier: 'id', label: "<fmt:message key='dialog.admin.users.name' />" };
                createSelectBox("userDataGroup",
                    null, 
                    storeProps,
                    null//function(){return UtilSecurity.getAllGroups(includeCatAdminGroup);}
                );
                initializeGroupLists(true);
            }
            
            function initializeGroupLists(includeCatAdminGroup){
                var def = UtilSecurity.getAllGroups(includeCatAdminGroup);
                
                def.then(function(groupList){
                    var listAvailableGroups = [];
                    var listUserGroups = [];
                    var groupIdList = currentSelectedUser ? currentSelectedUser.groupIds : [];
                    dojo.forEach(groupList, function(item){
                        // Due to a dojo bug, you cannot use numbers as an id
                        // it won't work when selecting an item! (06.09.2010)
                        //list.push({name:item.name, id:item.id+""});
                        if (dojo.indexOf(groupIdList, item.id) != -1) {
                            listUserGroups.push( {"name":item.name, "Id":item.id} );
                        } else if (currentSelectedUser && currentSelectedUser.userId != administratorId){
                            listAvailableGroups.push( {"name":item.name, "Id":item.id} );
                        }
                    });
                    
                    UtilGrid.setTableData("availableGroupsList", listAvailableGroups);
                    UtilGrid.setTableData("groupsList", listUserGroups);
                });
                
                console.debug("set valid function");
                /*dijit.byId("userDataGroup").isValid = function(){
                    return (this.getValue() != "");
                };*/
                console.debug("return from group set");
                return def;
            }
            
                        
            // 'Add selected groups' Button onClick function.
            //
            // This function moves the selected groups from the selection list (left) to the result list (right)
            addSelected = function() {
                var selectedGroups = UtilGrid.getSelectedData("availableGroupsList");
                if (selectedGroups) {
                    dojo.forEach(selectedGroups, function(item) {
                        UtilGrid.removeTableDataRow("availableGroupsList", UtilGrid.getSelectedRowIndexes("availableGroupsList"));
                        UtilGrid.addTableDataRow("groupsList", item);
                    });
                }
            }
            
            
            // 'Add all groups' Button onClick function.
            //
            // This function moves all groups from the selection list (left) to the result list (right)
            addAll = function() {
                var groups = UtilGrid.getTableData("availableGroupsList");
                if (groups) {
                    for (i in groups) {
                        UtilGrid.removeTableDataRow("availableGroupsList", i);
                        UtilGrid.addTableDataRow("groupsList", groups[i]);
                    }
                }
            }
            
            
            // 'Remove selected groups' Button onClick function.
            //
            // This function moves the selected groups from the result list (right) to the selection list (left)
            removeSelected = function() {
                var selectedGroups = UtilGrid.getSelectedData("groupsList");
                if (selectedGroups) {
                    dojo.forEach(selectedGroups, function(item) {
                        if (item.name != "administrators") {
                            UtilGrid.removeTableDataRow("groupsList", UtilGrid.getSelectedRowIndexes("groupsList"));
                            UtilGrid.addTableDataRow("availableGroupsList", item);
                        } else {
                            console.debug("Cannot remove group 'administrators' from user.")
                        }
                    });
                }
            }
            
            
            // 'Remove all groups' Button onClick function.
            //
            // This function moves all groups from the result list (right) to the selection list (left)
            removeAll = function() {
                var groups = UtilGrid.getTableData("groupsList");
                if (groups) {
                    for (i in groups) {
                        if (groups[i].name != "administrators") {
                            UtilGrid.removeTableDataRow("groupsList", i);
                            UtilGrid.addTableDataRow("availableGroupsList", groups[i]);
                        } else {
                            console.debug("Cannot remove group 'administrators' from user.")
                        }
                    }
                }
            }
            
            // 'Delete user' button function
            // Get the selected user from the tree and delete it
            scriptScopeUser.deleteUser = function(){
                var user = dijit.byId("treeUser").selectedNode.item;
                
                // Can't delete the cat admin. This is also checked in the backend.
                if (user == null || user.role[0] == 1 || user.isFolder[0]) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.canNotDeleteError' />", dialog.WARNING);
                    console.debug("Can't delete.");
                    return;
                }
                
                var deferred = new dojo.Deferred();
                
                var displayText = dojo.string.substitute("<fmt:message key='dialog.admin.users.confirmDelete' />", [user.title[0]]);
                dialog.show("<fmt:message key='dialog.admin.users.deleteUser' />", displayText, dialog.INFO, [{
                    caption: "<fmt:message key='general.no' />",
                    action: function(){
                        deferred.errback();
                    }
                }, {
                    caption: "<fmt:message key='general.ok' />",
                    action: function(){
                        deferred.callback();
                    }
                }]);
                
                deferred.addCallback(function(){
                    SecurityService.deleteUser(user.userId+"", user.addressUuid+"", {
                        preHook: showLoadingZone,
                        postHook: hideLoadingZone,
                        callback: function(){
                            var tree = dijit.byId("treeUser");
                            tree.model.store.deleteItem(user);
                            tree.model.store.save();  
                            resetInputFields();
                            //dijit.byId("treeUser").selectedNode = null;
                        },
                        errorHandler: function(errMsg, err){
                            hideLoadingZone();
                            if (errMsg.indexOf("USER_HAS_WRONG_ROLE") != -1 || errMsg.indexOf("USER_HIERARCHY_WRONG") != -1) {
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.noDeletePermissionError' />", dialog.WARNING);
                            }
                            else {
                                displayErrorMessage(err);
                            }
                            
                            console.debug(errMsg);
                        }
                    });
                });
            }
            
            // 'Import portal user' button function
            scriptScopeUser.importPortalUser = function(){
                var selectedUser = dijit.byId("treeUser").selectedNode;
                
                if (currentUser.role == 1 && selectedUser == null) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.noParentSelectedError' />", dialog.WARNING);
                    console.debug("No node selected.");
                    return;
                }
                
                // In case a mdAdmin wants to create a new user, we have to check if the mdAdmin is selected in the tree
                if (currentUser.role == 2) {
                    if (selectedUser == null || (selectedUser.userId != currentUser.id)) {
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.adminSelectedInvalidUserError' />", dialog.WARNING);
                        console.debug("Can't create users here. Select the correct mdAdmin.");
                        return;
                    }
                }
                
                // In case a user with role mdAutor is selected, do nothing and return
                if (selectedUser.role < 1 || selectedUser.role > 2) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.authorSelectedAsParentError' />", dialog.WARNING);
                    console.debug("Select a user with role catAdmin or mdAdmin.");
                    return;
                }
                
                // In case a newUserNode already exists, return
                if (selectedUser.id == "newUserNode") {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.newNodeSelectedAsParentError' />", dialog.WARNING);
                    console.debug("Please save the current user before creating a new one.");
                    return;
                }
                
                
                var deferred = new dojo.Deferred();
                dialog.showPage("<fmt:message key='dialog.admin.users.importUser' />", "admin/dialogs/mdek_admin_import_user_dialog.jsp", 370, 240, true, {
                    resultHandler: deferred
                });
                
                deferred.addCallback(function(portalUser){
                    var tree = dijit.byId("treeUser");
                    var def = tree._expandNode(selectedUser);
                    
                    def.addCallback(function(deferred){
                        //treeController.createChild(selectedUser, "last", createNewUserNode(selectedUser, portalUser));
                        console.debug("create new user: ");
                        console.debug(selectedUser);
                        console.debug(createNewUserNode(selectedUser.item, portalUser));
                        tree.model.store.newItem(createNewUserNode(selectedUser.item, portalUser), {parent: selectedUser.item, attribute:"children"} );
                    });
                    
                    def.addCallback(function(data){
                        console.debug("after new item");
                        resetInputFields();
                        UtilTree.selectNode("treeUser", "newUserNode");
                        treeNodeSelected(dijit.byId("newUserNode").item);
                        //setTimeout(function() {UtilTree.selectNode("treeUser", "newUserNode");}, 500);
                        /*dojo.event.topic.publish(treeListener.eventNames.select, {
                            node: newNode
                        });*/
                    });
                });
            }
            
            // 'Save user' button function
            scriptScopeUser.saveUser = function(){
                var selectedUser = dijit.byId("treeUser").selectedNode;
                var login = dijit.byId("userDataLogin").getValue();
                var oldUser = selectedUser.item.portalLogin+"";
                selectedUser.item.portalLogin = login;
                
                if (dojo.trim(login).length == 0) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.noUserSelectedForSaveError' />", dialog.WARNING);
                    console.debug("No user selected for save/import.");
                    return;
                }
                
                if (!isValidUser()) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.requiredFieldsError' />", dialog.WARNING);
                    console.debug("Invalid information entered.");
                    return;
                }
                
                var user = {};
                var userData = {};
                
                if (selectedUser.item.id == "newUserNode") {
                    // If the current selected node is a new node, create it in the db
                    user.addressUuid = currentSelectedAddressId+"";
                    var groups = UtilGrid.getTableData("groupsList");
                    
                    var groudIds = UtilList.map(groups, function(group) {
                        return group.Id;
                    });
            
                    user.groupIds = groudIds;
                    user.roleName = selectedUser.item.roleName[0];
                    user.role = selectedUser.item.role[0];
                    user.parentUserId = selectedUser.item.parentUserId+"";
                    console.debug(user);
                    
                    SecurityService.createUser(user, login, true, {
                        preHook: showLoadingZone,
                        postHook: hideLoadingZone,
                        callback: function(newUser){
                            var tree = dijit.byId("treeUser");
                            updateTreeNode(selectedUser, newUser);
                            UtilTree.selectNode("treeUser", "TreeNode_" + newUser.id);
                            updateInputElements(tree.selectedNode.item);

                            dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.createSuccess' />", dialog.INFO);
                        },
                        errorHandler: function(errMsg, err){
                            hideLoadingZone();
                            if (errMsg.indexOf("USER_HAS_WRONG_ROLE") != -1) {
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.createPermissionError' />", dialog.WARNING);
                                
                            }
                            else 
                                if (errMsg.indexOf("ENTITY_ALREADY_EXISTS") != -1) {
                                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.addressCollisionError' />", dialog.WARNING);
                                    
                                }
                                else {
                                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.createError' />", dialog.WARNING);
                                }
                            console.debug(errMsg);
                        }
                    });
                    
                }
                else {
                    user.id = currentSelectedUser.userId+"";
                    user.addressUuid = currentSelectedAddressId+"";
                    var groups = UtilGrid.getTableData("groupsList");
                    var groudIds = UtilList.map(groups, function(group) {
                        return group.Id;
                    });
            
                    user.groupIds = groudIds;
                    user.role = currentSelectedUser.role[0];
                    user.parentUserId = currentSelectedUser.parentUserId == undefined ? undefined : currentSelectedUser.parentUserId+"";
                    
                    console.debug("newUser:");
                    console.debug(user);
                    
                    SecurityService.storeUser(oldUser, user, login, true, {
                        preHook: showLoadingZone,
                        postHook: hideLoadingZone,
                        callback: function(newUser){
                            // if cat-admin was changed then user must be logged out
                            if (user.role == 1 && (oldUser != login)) {
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.updateCatAdmin' />", dialog.WARNING);
                            }
                            else {
                                updateTreeNode(selectedUser, newUser);
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.updateSuccess' />", dialog.INFO);
                            }
                            
                        },
                        errorHandler: function(errMsg, err){
                            hideLoadingZone();
                            if (errMsg.indexOf("USER_HAS_WRONG_ROLE") != -1 || errMsg.indexOf("USER_HIERARCHY_WRONG") != -1) {
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.updatePermissionError' />", dialog.WARNING);
                                
                            }
                            else 
                                if (errMsg.indexOf("ENTITY_ALREADY_EXISTS") != -1) {
                                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.addressCollisionError' />", dialog.WARNING);
                                }
                                else {
                                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.updateError' />", dialog.WARNING);
                                }
                            console.debug(errMsg);
                            selectedUser.item.portalLogin = oldUser;
                            treeNodeSelected(currentSelectedUser);
                        }
                    });
                }
            }
            
            function updateTreeNode(userNode, newUser){
                var user = userNode.item;
                if (user.id == "newUserNode") {
                    var parent = userNode.getParent().item;
                    var tree = dijit.byId("treeUser");
                    
                    // A new node is a leaf node. It's safe to destroy it
                    tree.model.store.deleteItem(user);
                    tree.model.store.save(); 
                    tree.model.store.newItem(convertUserToTreeNode(newUser), {parent: parent, attribute:"children"} );
                    
                }
                else {
                    var userName = UtilAddress.createAddressTitle(newUser.address);
                    userNode.set('label', userName);
                    user.addressUuid = newUser.addressUuid+"";
                    user.groupIds = newUser.groupIds;
                    user.title = userName;
                    if (newUser.userPermissions != null) {
                        dojo.forEach(newUser.userPermissions, function(p){
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
            function isValidUser(){
                resetRequiredElements();
                
                var valid = true;
                
                dojo.forEach(requiredElements, function(element){
                    if (!dijit.byId(element[0]).isValid()) {
                        dojo.addClass(dojo.byId(element[1]), "important");
                        valid = false;
                    }
                });
                
                return valid;
            }
            
            // Resolves the current role id from the input string
            function getRoleId(roleName){
                if (roleName == "<fmt:message key='security.role.catalogAdmin' />") {
                    return 1;
                    
                }
                else 
                    if (roleName == "<fmt:message key='security.role.metadataAdmin' />") {
                        return 2;
                        
                    }
                    else 
                        if (roleName == "<fmt:message key='security.role.metadataAuthor' />") {
                            return 3;
                        }
            }
            
            // Resolves the role name from the input id
            function getRoleName(roleId){
                switch (roleId) {
                    case 1:
                        return "<fmt:message key='security.role.catalogAdmin' />";
                    case 2:
                        return "<fmt:message key='security.role.metadataAdmin' />";
                    case 3:
                        return "<fmt:message key='security.role.metadataAuthor' />";
                    default:
                        return null;
                }
            }
            
            // Resets all labels that are tagged as 'important' to their initial state
            function resetRequiredElements(){
                dojo.forEach(requiredElements, function(element){
                    dojo.removeClass(dojo.byId(element[1]), "important");
                });
            }
            
            // Function that is called when a treeNode is clicked
            function treeNodeSelected(item, treeNode){
                // Check if a 'newUserNode' exists. If it does and the user clicked another node, delete the newUserNode.
                var newUserNode = dijit.byId("newUserNode");
                if (newUserNode != null && item.id != "newUserNode") {
                    var tree = dijit.byId("treeUser");
                    tree.model.store.deleteItem(newUserNode.item);
                    tree.model.store.save();                    
                }
                
                updateInputElements(item);
                
                // Delete the displayed address link since it's build from the node title ('Neuer Benutzer')
                if (newUserNode != null && item.id == "newUserNode") {
                    dijit.byId("userDataAddressLink").set("value","");
                }
            }
            
            // Updates the input elements with the data from 'treeNode'
            function updateInputElements(treeNode){
            
                currentSelectedUser = treeNode;
                dijit.byId("bImportUser").set("disabled", false);
                if (treeNode.role == 1) {
                    // Standards for catAdmin
                    var def = initializeGroupLists(true);
                    UtilGrid.updateOption("availableGroupsList", "editable", false);//.disable();
                    UtilGrid.updateOption("groupsList", "editable", false);//).disable();
                }
                else {
                    if (treeNode.role == 3) {
                        dijit.byId("bImportUser").set("disabled", true);
                    }
                    var def = initializeGroupLists(false);
                    UtilGrid.updateOption("availableGroupsList", "editable", true);//.disable();
                    UtilGrid.updateOption("groupsList", "editable", true);//).disable();
                    
                }
                
                def.addCallback(function(){
                    dijit.byId("userDataLogin").setValue(treeNode.portalLogin);
                    dijit.byId("userDataAddressLink").setValue(treeNode.title);
                    dijit.byId("userDataRole").setValue(treeNode.roleName);
                    console.debug("set to groupid: " + treeNode.groupId);
                    //dijit.byId("userDataGroup").set("value",treeNode.groupId+"");
                    currentSelectedAddressId = treeNode.addressUuid;
                    //currentSelectedUser = treeNode;
                });
            }
            
            
            function resetInputFields(){
                dijit.byId("userDataLogin").set("value","");
                dijit.byId("userDataAddressLink").set("value","");
                dijit.byId("userDataRole").set("value","");
                UtilGrid.setTableData("groupsList", []);
                UtilGrid.setTableData("availableGroupsList", []);
                currentSelectedAddressId = null;
                currentSelectedUser = null;
            }
            
            scriptScopeUser.searchForAddress = function(){
                console.debug("searchForAddress");
                var def = new dojo.Deferred();
                dialog.showPage("<fmt:message key='general.searchAddress' />", 'dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 580, true, {
                    resultHandler: def
                });
                
                def.addCallback(getAddressData);
                
                def.addCallback(function(addressData){
                    // the addressClass of an item in a tree is stored under objectClass
                    // this has to be remapped for further usage
                    /*if (addressData.addressClass == null) {
                        console.debug("modify addressClass");
                        addressData.addressClass = addressData.objectClass;
                        addressData.organisation = addressData.title;
                    }*/
                    console.debug("add address: " + addressData);
                    currentSelectedAddressId = [addressData.uuid];
                    var title = UtilAddress.createAddressTitle(addressData);
                    dijit.byId("userDataAddressLink").set("value", title);
                });
            }
            
            scriptScopeUser.searchForPortalUser = function(){
                var selectedUser = dijit.byId("treeUser").selectedNode;
                
                if (currentUser.role == 1 && selectedUser == null) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.noParentSelectedError' />", dialog.WARNING);
                    console.debug("No node selected.");
                    return;
                }
                
                var deferred = new dojo.Deferred();
                dialog.showPage("<fmt:message key='dialog.admin.users.importUser' />", "admin/dialogs/mdek_admin_import_user_dialog.jsp", 370, 240, true, {
                    resultHandler: deferred
                });
                
                deferred.addCallback(function(portalUser){
                    console.debug("user chosen: " + portalUser);
                    dijit.byId("userDataLogin").setValue(portalUser);
                });
            }
            
            function getUserDataForAddress(adrUuid){
                var deferred = new dojo.Deferred();
                
                SecurityService.getUserDataForAddress(adrUuid, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(userData){
                        deferred.callback(userData);
                    },
                    errorHandler: function(errMsg, err){
                        hideLoadingZone();
                        displayErrorMessage(err);
                        console.debug(errMsg);
                        deferred.errback(err);
                    }
                });
                
                return deferred;
            }
            
            function getAddressData(adrUuid){
                var deferred = new dojo.Deferred();
                
                AddressService.getAddressData(adrUuid, null, {
                    //preHook: showLoadingZone,
                    //postHook: hideLoadingZone,
                    callback: function(adr){
                        console.debug("got addressData: " + adr);
                        deferred.callback(adr);
                    },
                    errorHandler: function(errMsg, err){
                        hideLoadingZone();
                        displayErrorMessage(err);
                        console.debug(errMsg);
                        deferred.errback(err);
                    }
                });
                
                return deferred;
            }
            
            function getCatalogAdmin(){
                var deferred = new dojo.Deferred();
                
                SecurityService.getCatalogAdmin({
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(user){
                        deferred.callback(user);
                    },
                    errorHandler: function(errMsg, err){
                        hideLoadingZone();
                        displayErrorMessage(err);
                        console.debug(errMsg);
                        deferred.errback(err);
                    }
                });
                
                return deferred;
            }
            
            function convertUserListToTreeNodes(userList){
                var treeNodes = [];
                
                dojo.forEach(userList, function(user){
                    treeNodes.push(convertUserToTreeNode(user));
                });
                
                // Return a sorted list according to the users titles
                return treeNodes.sort(function(a, b){
                    return UtilString.compareIgnoreCase(a.title, b.title);
                });
            }
            
            function convertUserToTreeNode(user){
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
                    groupIds: user.groupIds,
                    portalLogin: portalLogin,
                    nodeDocType: getDocTypeForRole(user.role),
                    isFolder: user.hasChildren,
                    id: "TreeNode_" + user.id,
                    noPortalLogin: notExists
                }
            }
            
            function createNewUserNode(parentNode, login){
                return {
                    id: "newUserNode",
                    userId: "newUserNode",
                    parentUserId: parentNode.userId[0],
                    //      addressUuid: user.addressUuid,
                    title: "Neuer Benutzer",
                    role: parentNode.role[0] + 1,
                    roleName: getRoleName(parentNode.role[0] + 1),
                    //      groupId: parentNode.groupId,
                    groupIds: UtilList.map(parentNode.groupIds, function(groupId) {if (groupId != administratorGroupId) return groupId}),
                    createRoot: false,
                    portalLogin: login,
                    nodeDocType: getDocTypeForRole(parentNode.role[0] + 1),
                    isFolder: false
                }
            }
            
            function getDocTypeForRole(roleId){
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
            
            function showLoadingZone(){
                dojo.byId("adminUserLoadingZone").style.visibility = "visible";
            }
            
            function hideLoadingZone(){
                dojo.byId("adminUserLoadingZone").style.visibility = "hidden";
            }
        </script>
    </head>
    <body>
        <!-- CONTENT START -->
        <!--<div dojoType="dijit.layout.ContentPane" layoutAlign="client">-->
            <div id="contentSection" class="content contentBlockWhite top" style="">
                <div dojoType="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="false" id="borderContainerMdekAdminUserAdmin" style="height:550px;">
                    <div dojoType="dijit.layout.BorderContainer" splitter="false" region="leading" style="width:264px;">
                        <div dojoType="dijit.layout.ContentPane" splitter="false" region="center" class="grey">
                            <!--<div id="userAdmin" class="content">--><!-- LEFT HAND SIDE CONTENT BLOCK 1 START --><!--<div class="spacer"></div>-->
                            <div class="inputContainer">
                                <div id="treeUser"></div>
                            </div>
                        </div>
                        <div dojoType="dijit.layout.ContentPane" splitter="false" region="bottom">
                            <span class="button"><span style="float:left;">
                                    <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.users.delete" />" onClick="javascript:scriptScopeUser.deleteUser();">
                                        <fmt:message key="dialog.admin.users.delete" />
                                    </button>
                                </span><span style="float:right;">
                                    <button dojoType="dijit.form.Button" id ="bImportUser" title="<fmt:message key="dialog.admin.users.create" />" onClick="javascript:scriptScopeUser.importPortalUser();">
                                        <fmt:message key="dialog.admin.users.create" />
                                    </button>
                                </span>
                            </span>
                        </div><!-- LEFT HAND SIDE CONTENT BLOCK 1 END -->
                    </div>
                    <div dojoType="dijit.layout.BorderContainer" splitter="false" region="center" style="">
                        <div dojoType="dijit.layout.ContentPane" splitter="false" region="center">
                            <!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
                            <div id="winNavi" style="top:0px;">
                                <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=user-administration-1#user-administration-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
                            </div>
                            <div id="userData" class="inputContainer" style="">
                                <span class="label">
                                    <label onclick="javascript:dialog.showContextHelp(arguments[0], 8012)">
                                        <fmt:message key="dialog.admin.users.userData" />
                                    </label>
                                </span>
                                <div class="inputContainer field grey" style="">
                                    <span class="outer"><div>
                                        <span class="label">
                                            <label for="userDataLogin" onclick="javascript:dialog.showContextHelp(arguments[0], 8013)">
                                                <fmt:message key="dialog.admin.users.login" />
                                            </label>
                                        </span>
                                        <span class="functionalLink marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:void(0);" onClick="javascript:scriptScopeUser.searchForPortalUser();" title="<fmt:message key="dialog.admin.users.searchPortalUser" /> [Popup]"><fmt:message key="dialog.admin.users.searchPortalUser" /></a></span>
                                        <span class="input"><input type="text" id="userDataLogin" name="userDataLogin" style="width:100%;" disabled="true" dojoType="dijit.form.ValidationTextBox" /></span>
                                    </div></span>
                                    <span class="outer"><div>
                                        <span class="label">
                                            <label for="userDataRole" onclick="javascript:dialog.showContextHelp(arguments[0], 8014)">
                                                <fmt:message key="dialog.admin.users.role" />
                                            </label>
                                        </span>
                                        <span class="input"><input type="text" id="userDataRole" name="userDataRole" style="width:100%;" disabled="true" dojoType="dijit.form.ValidationTextBox" /></span>
                                    </div></span>
                                    
                                    <span class="outer required"><div>
                                        <span class="label">
                                            <label id="userDataAddressLinkLabel" for="userDataAddressLink" onclick="javascript:dialog.showContextHelp(arguments[0], 8015)">
                                                <fmt:message key="dialog.admin.users.address" />*
                                            </label>
                                        </span>
                                        <span class="functionalLink marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:void(0);" onClick="javascript:scriptScopeUser.searchForAddress();" title="<fmt:message key="dialog.admin.users.searchAddress" /> [Popup]"><fmt:message key="dialog.admin.users.searchAddress" /></a></span>
                                        <span class="input"><input type="text" id="userDataAddressLink" name="userDataAddressLink" style="width:100%;" disabled="true" dojoType="dijit.form.ValidationTextBox" /></span>
                                    </div></span>
                                    
                                    <span class="outer" style="width:45%;"><div>
                                        <span class="label"><label id="availableGroupsListLabel" for="availableGroupsList" onclick="javascript:dialog.showContextHelp(arguments[0], 8016)"><fmt:message key="dialog.admin.users.available.groups"/></label></span>
                                        <div class="tableContainer">
                                            <div id="availableGroupsList" autoHeight="10" contextMenu="none" ></div>
                                        </div>
                                    </div></span>
                                    
                                    <span class="outer" style="width:60px;"><div>
                                        <span class="entry">
                                            <span class="buttonCol" style="margin:80px -4px 0px;">
                                                <button dojoType="dijit.form.Button" id="addSelectedButton" onClick="addSelected">&nbsp;>&nbsp;</button>
                                                <button dojoType="dijit.form.Button" id="addAllButton" onClick="addAll">>></button>
                                                <button dojoType="dijit.form.Button" id="removeAllButton" onClick="removeAll"><<</button>
                                                <button dojoType="dijit.form.Button" id="removeSelectedButton" onClick="removeSelected">&nbsp;<&nbsp;</button>
                                            </span>
                                        </span>
                                    </div></span>
                                    
                                    <span class="outer" style="width:45%;"><div>
                                        <span class="label"><label id="groupsListLabel" for="groupsList" onclick="javascript:dialog.showContextHelp(arguments[0], 8016)"><fmt:message key="dialog.admin.users.groups" /></label></span>
                                        <div class="tableContainer">
                                            <div id="groupsList" autoHeight="10" contextMenu="none" ></div>
                                        </div>
                                    </div></span>
                                    <div class="fill"></div>
                                </div>
                            </div>
                        </div>
                        <div dojoType="dijit.layout.ContentPane" splitter="false" region="bottom">
                            <div class="inputContainer">
                                <span class="button"><span style="float:right;">
                                    <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.users.save" />" onClick="javascript:scriptScopeUser.saveUser();">
                                        <fmt:message key="dialog.admin.users.save" />
                                    </button>
                                </span><span id="adminUserLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span></span>
                            </div>
                        </div>
                        <!-- RIGHT HAND SIDE CONTENT BLOCK 1 END -->
                    </div>
                </div>
            </div>
        <!--</div>--><!-- CONTENT END -->
    </body>
</html>