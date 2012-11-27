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
            dojo.require("dojox.validate.regexp");
            var userDataGroupData;
            
            var scriptScopeUser = _container_;
            
            var currentSelectedAddressId = null;
            var currentSelectedUser = null;
            // store cat admins adminstrators group Id for later user, see setTreeRoot() method
            var administratorGroupId = null;
            var administratorId = null;
            var catAdminUserData = null;
            
            var MAX_NUM_DATASETS = 100;
            
            var requiredElements = [["userDataAddressSurname", "userDataAddressSurnameLabel"],
                ["userDataAddressForename", "userDataAddressForenameLabel"],
                ["userDataAddressEmailUser", "userDataAddressEmailUserLabel",
                ["userDataAddressInstitution", "userDataAddressInstitutionLabel"]]
            ];
            
            dojo.connect(_container_, "onLoad", function(){
                dijit.byId("borderContainerMdekAdminUserAdmin").resize();
                
                this.createGrids();
                
                createCustomTree("treeUser", null, "userId", "title", this.loadUserData);
                
                this.createGroupLists();
                
                this.initializeUserTree();
                
                // select first user?
                //tree.attr("path", [tree.rootNode.item, tree.rootNode.getChildren()[0].item]);
                this.setUserAddressDataFieldsDisabled(true);

                Validation.addEmailCheck("userDataAddressEmailUser", false);
                Validation.addEmailCheck("userDataAddressEmailPointOfContact", false);
                
                
                this.addRequiredBehaviour();
                
            });
            
            
            scriptScopeUser.createGrids = function() {
                var tableStructure = [
                    {field: 'name',name: "<fmt:message key='dialog.admin.users.groupName' />",width: 350-scrollBarWidth+'px'}
                ];
                createDataGrid("availableGroupsList", null, tableStructure, null);
                createDataGrid("groupsList", null, tableStructure, null);
                
                // responsible tables
                var responsibleUserInObjectsStructure = [
                   {field: 'icon',name: '&nbsp;',width: '30px'},
                   {field: 'uuid',name: 'ID',width: '300px'}, 
                   {field: 'linkLabel',name: "<fmt:message key='dialog.admin.catalog.management.deleteAddress.objectName' />",width: '300px'}
                ];
                var responsibleUserInAddressesStructure = [
                   {field: 'icon',name: '&nbsp;',width: '30px'},
                   {field: 'uuid',name: 'ID',width: '300px'}, 
                   {field: 'title',name: "<fmt:message key='dialog.admin.catalog.management.deleteAddress.objectName' />",width: '300px'}
                ];
               
                createDataGrid("responsibleUserInObjects", null, responsibleUserInObjectsStructure, null);
                createDataGrid("responsibleUserInAddresses", null, responsibleUserInObjectsStructure, null);
            }
            
            scriptScopeUser.loadUserData = function(node, callback_function){
                var parentItem = node.item;
                var store = this.tree.model.store;
                
                if (parentItem.root) {
                    callback_function();
                    return;
                }
                console.debug("parentItem");
                console.debug(parentItem);
                SecurityService.getSubUsers(parentItem.userId[0], {
                    preHook: scriptScopeUser.showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(res){
                        var userNodes = convertUserListToTreeNodes(res);
                        dojo.forEach(userNodes, function(userNode){
                            if (parentItem.root) 
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
            
            
            scriptScopeUser.setTreeRoot = function(catAdmin){
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
            scriptScopeUser.initializeUserTree = function(){
            
                var def = UtilSecurity.getCatAdmin();
                def.addCallback(function(catAdmin){
                    scriptScopeUser.setTreeRoot(catAdmin);
                });
                
                dojo.connect(dijit.byId("treeUser"), "onClick", treeNodeSelected);
            }
            
            scriptScopeUser.createGroupLists = function() {
                var storeProps = { identifier: 'id', label: "<fmt:message key='dialog.admin.groupName' />" };
                createSelectBox("userDataGroup",
                    null, 
                    storeProps,
                    null//function(){return UtilSecurity.getAllGroups(includeCatAdminGroup);}
                );
                this.initializeGroupLists(true);
            }
            
            scriptScopeUser.initializeGroupLists = function(includeCatAdminGroup){
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
                            scriptScopeUser.setUserAddressDataFieldsDisabled(true);
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
                console.debug("selectedUser");
                console.debug(selectedUser);
                console.debug("currentUser");
                console.debug(currentUser);
                
                if (selectedUser == null) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.noParentSelectedError' />", dialog.WARNING);
                    console.debug("No node selected.");
                    return;
                }
                
                // In case a mdAdmin wants to create a new user, we have to check if the mdAdmin is selected in the tree
                if (currentUser.role == 2) {
                    if (selectedUser.item.userId[0] != currentUser.id) {
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.adminSelectedInvalidUserError' />", dialog.WARNING);
                        console.debug("Can't create users here. Select the correct mdAdmin.");
                        return;
                    }
                }
                
                // In case a user with role mdAutor is selected, do nothing and return
                if (selectedUser.item.role[0] < 1 || selectedUser.item.role[0] > 2) {
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
                        //console.debug(selectedUser);
                        //console.debug(createNewUserNode(selectedUser.item, portalUser));
                        tree.model.store.newItem(createNewUserNode(selectedUser.item, portalUser), {parent: selectedUser.item, attribute:"children"} );
                    });
                    
                    def.addCallback(function(data){
                        console.debug("after new item");
                        resetInputFields();
                        scriptScopeUser.setUserAddressDataFieldsDisabled(false);
                        UtilTree.selectNode("treeUser", "newUserNode");
                        treeNodeSelected(dijit.byId("newUserNode").item);
                    });
                });
            }
            
            // 'Save user' button function
            scriptScopeUser.saveUser = function(){
                var selectedUser = dijit.byId("treeUser").selectedNode;
                var login = dijit.byId("userDataLogin").getValue();
                var oldUserLogin = selectedUser.item.portalLogin+"";
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
                user.address = scriptScopeUser.getAddressDataFromFields();  
                
                if (selectedUser.item.id == "newUserNode") {
                    // DO NOT SET addressUuid, will be created in backend !
                    //user.addressUuid = currentSelectedAddressId+""; // currentSelectedAddressId is undefined

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
                            scriptScopeUser.updateInputElements(tree.selectedNode.item);

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
                    
                    SecurityService.storeUser(oldUserLogin, user, login, true, {
                        preHook: showLoadingZone,
                        postHook: hideLoadingZone,
                        callback: function(newUser){
                            // if cat-admin was changed then user must be logged out
                            if (user.role == 1 && (oldUserLogin != login)) {
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
                            selectedUser.item.portalLogin = oldUserLogin;
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
                
                scriptScopeUser.updateInputElements(user);
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
            
            scriptScopeUser.addRequiredBehaviour = function() {
                dojo.connect(dijit.byId("userDataAddressInstitution"), "onChange", scriptScopeUser.checkFields);                
                dojo.connect(dijit.byId("userDataAddressSurname"), "onChange", scriptScopeUser.checkFields);                
                dojo.connect(dijit.byId("userDataAddressForename"), "onChange", scriptScopeUser.checkFields);
            }
            
            // 1) if a forename or surname has text and the institution is empty -> the other field also is required
            // 2) if only institution has text -> only this field is required 
            // 3) all field are required otherwise
            scriptScopeUser.checkFields = function() {
                var surnameEmpty     = dijit.byId("userDataAddressSurname").value.trim() === "";
                var forenameEmpty    = dijit.byId("userDataAddressForename").value.trim() === "";
                var institutionEmpty = dijit.byId("userDataAddressInstitution").value.trim() === "";

                if (institutionEmpty && (!surnameEmpty || !forenameEmpty)) {
                    dojo.addClass("surnameInput", "required"); dijit.byId("userDataAddressSurname").required = true;
                    dojo.addClass("forenameInput", "required"); dijit.byId("userDataAddressForename").required = true;
                    dojo.removeClass("institutionInput", "required"); dijit.byId("userDataAddressInstitution").required = false;
                } else if (!institutionEmpty && surnameEmpty && forenameEmpty) {
                    dojo.removeClass("surnameInput", "required"); dijit.byId("userDataAddressSurname").required = false;
                    dojo.removeClass("forenameInput", "required"); dijit.byId("userDataAddressForename").required = false;
                    dojo.addClass("institutionInput", "required"); dijit.byId("userDataAddressInstitution").required = true;
                } else {
                    dojo.addClass("surnameInput", "required"); dijit.byId("userDataAddressSurname").required = true;
                    dojo.addClass("forenameInput", "required"); dijit.byId("userDataAddressForename").required = true;
                    dojo.addClass("institutionInput", "required"); dijit.byId("userDataAddressInstitution").required = true;
                }
                // make changes immediately
                scriptScopeUser.revalidate();
            }
            
            scriptScopeUser.revalidate = function() {
                dijit.byId("userDataAddressSurname").validate();
                dijit.byId("userDataAddressForename").validate();
                dijit.byId("userDataAddressInstitution").validate();
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
                
                scriptScopeUser.updateInputElements(item);
            }
            
            // Updates the input elements with the data from 'treeNode'
            scriptScopeUser.updateInputElements = function(treeNode){
                showLoadingZone();
                resetRequiredElements();
                scriptScopeUser.setUserAddressDataFieldsDisabled(false);

                currentSelectedUser = treeNode;
                dijit.byId("bImportUser").set("disabled", false);
                if (treeNode.role == 1) {
                    // Standards for catAdmin
                    var def = scriptScopeUser.initializeGroupLists(true);
                    UtilGrid.updateOption("availableGroupsList", "editable", false);
                    UtilGrid.updateOption("groupsList", "editable", false);
                }
                else {
                    if (treeNode.role == 3) {
                        dijit.byId("bImportUser").set("disabled", true);
                    }
                    var def = scriptScopeUser.initializeGroupLists(false);
                    UtilGrid.updateOption("availableGroupsList", "editable", true);
                    UtilGrid.updateOption("groupsList", "editable", true);
                    
                }
                
                def.addCallback(function(){
                    if (treeNode.addressUuid) {
                        var def2 = scriptScopeUser.getAddressData(treeNode.addressUuid);
                        def2.addCallback(function(addressData) {
                            setUserAddressDataFields(addressData);
                        });
                    } else {
                        setUserAddressDataFields(null);
                    }

                    dijit.byId("userDataLogin").setValue(treeNode.portalLogin);
                    dijit.byId("userDataRole").setValue(treeNode.roleName);
                    console.debug("set to groupid: " + treeNode.groupId);
                    //dijit.byId("userDataGroup").set("value",treeNode.groupId+"");
                    currentSelectedAddressId = treeNode.addressUuid;
                    
                    // new users do not have an ID yet!
                    if (currentSelectedAddressId) {
                        showLoadingZone();
                        // update responsible User data
                        var defRespObj = getObjectsWithResponsibleUser(currentSelectedAddressId[0]);
                        defRespObj.addCallback(function(objects) {
                            UtilList.addIcons(objects);
                            UtilList.addObjectLinkLabels(objects);
                            UtilGrid.setTableData("responsibleUserInObjects", objects);
                        });
                        var defRespAddr = getAddressesWithResponsibleUser(currentSelectedAddressId[0]);
                        defRespAddr.addCallback(function(addresses) {
                            UtilList.addIcons(addresses);
                            UtilList.addAddressTitles(addresses);
                            UtilList.addAddressLinkLabels(addresses);
                            UtilGrid.setTableData("responsibleUserInAddresses", addresses);
                        });
                        
                        var defList = new dojo.DeferredList([defRespObj, defRespAddr]);
                        defList.addCallback(function() {hideLoadingZone();});
                    }
                });
                
            }
            
            function resetInputFields(){
                dijit.byId("userDataLogin").set("value","");
                dijit.byId("userDataRole").set("value","");

                setUserAddressDataFields(null);

                UtilGrid.setTableData("groupsList", []);
                UtilGrid.setTableData("availableGroupsList", []);
                currentSelectedAddressId = null;
                currentSelectedUser = null;
            }
            
            function setUserAddressDataFields(addressData) {
                if (addressData) {
                    dijit.byId("userDataAddressSurname").setValue(addressData.name);
                    dijit.byId("userDataAddressForename").setValue(addressData.givenName);
                    var commValue = UtilAddress.getAddressCommunicationValue(addressData, ["E-Mail", "e-mail"])
                    dijit.byId("userDataAddressEmailUser").setValue(commValue);
                    var commValue = UtilAddress.getAddressCommunicationValue(addressData, ["emailPointOfContact"])
                    dijit.byId("userDataAddressEmailPointOfContact").setValue(commValue);
                    dijit.byId("userDataAddressInstitution").setValue(addressData.organisation);
                    dijit.byId("userDataAddressStreet").setValue(addressData.street);
                    dijit.byId("userDataAddressPostCode").setValue(addressData.postalCode);
                    dijit.byId("userDataAddressCity").setValue(addressData.city);
                    var commValue = UtilAddress.getAddressCommunicationValue(addressData, ["Telefon", "telephone"])
                    dijit.byId("userDataAddressPhone").setValue(commValue);
                } else {
                    // reset fields
                    dijit.byId("userDataAddressSurname").set("value","");
                    dijit.byId("userDataAddressForename").set("value","");
                    dijit.byId("userDataAddressEmailUser").set("value","");
                    dijit.byId("userDataAddressEmailPointOfContact").set("value","");
                    dijit.byId("userDataAddressInstitution").set("value","");
                    dijit.byId("userDataAddressStreet").set("value","");
                    dijit.byId("userDataAddressPostCode").set("value","");
                    dijit.byId("userDataAddressCity").set("value","");
                    dijit.byId("userDataAddressPhone").set("value","");
                }
            }

            scriptScopeUser.setUserAddressDataFieldsDisabled = function(isDisabled) {
                if (isDisabled) {
                    resetRequiredElements();
                }

                dijit.byId("userDataAddressSurname").set("disabled", isDisabled);
                dijit.byId("userDataAddressForename").set("disabled", isDisabled);
                dijit.byId("userDataAddressEmailUser").set("disabled", isDisabled);
                dijit.byId("userDataAddressEmailPointOfContact").set("disabled", isDisabled);
                dijit.byId("userDataAddressInstitution").set("disabled", isDisabled);
                dijit.byId("userDataAddressStreet").set("disabled", isDisabled);
                dijit.byId("userDataAddressPostCode").set("disabled", isDisabled);
                dijit.byId("userDataAddressCity").set("disabled", isDisabled);
                dijit.byId("userDataAddressPhone").set("disabled", isDisabled);
            }

            scriptScopeUser.getAddressDataFromFields = function(){
                var addr = {};
                addr.name = dijit.byId("userDataAddressSurname").getValue();
                addr.givenName = dijit.byId("userDataAddressForename").getValue();
                addr.organisation = dijit.byId("userDataAddressInstitution").getValue();
                addr.street = dijit.byId("userDataAddressStreet").getValue();
                addr.postalCode = dijit.byId("userDataAddressPostCode").getValue();
                addr.city = dijit.byId("userDataAddressCity").getValue();

                var commList = [];
                if (dijit.byId("userDataAddressEmailUser").getValue()) {
                    var comm = {};
                    comm.value = dijit.byId("userDataAddressEmailUser").getValue();
                    comm.medium = "E-Mail";
                    comm.description = null;
                    commList.push(comm);
                }
                if (dijit.byId("userDataAddressEmailPointOfContact").getValue()) {
                    var comm = {};
                    comm.value = dijit.byId("userDataAddressEmailPointOfContact").getValue();
                    comm.medium = "emailPointOfContact";
                    comm.description = null;
                    commList.push(comm);
                }
                if (dijit.byId("userDataAddressPhone").getValue()) {
                    var comm = {};
                    comm.value = dijit.byId("userDataAddressPhone").getValue();
                    comm.medium = "Telefon";
                    comm.description = null;
                    commList.push(comm);
                }

                if (commList.length > 0) {
                    addr.communication = commList;
                }

                return addr;
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
            
            scriptScopeUser.getAddressData = function(addrUuid){
                var uuidToFetch = addrUuid;
                if (addrUuid instanceof Array) {
                    uuidToFetch = addrUuid[0];
                }

                var deferred = new dojo.Deferred();
                
                AddressService.getAddressData(uuidToFetch, null, {
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
            
            // Fetch objects where given adrUuid is set as the responsible user
            function getObjectsWithResponsibleUser(adrUuid){
                var def = new dojo.Deferred();
                
                CatalogManagementService.getObjectsOfResponsibleUser(adrUuid, MAX_NUM_DATASETS, {
                    callback: function(objects){
                        def.callback(objects);
                    },
                    errorHandler: function(message, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + message);
                        def.errback(err);
                    }
                });
                
                return def;
            }
            
            // Fetch objects where given adrUuid is set as the responsible user
            function getAddressesWithResponsibleUser(adrUuid){
                var def = new dojo.Deferred();
                
                CatalogManagementService.getAddressesOfResponsibleUser(adrUuid, MAX_NUM_DATASETS, {
                    callback: function(objects){
                        def.callback(objects);
                    },
                    errorHandler: function(message, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + message);
                        def.errback(err);
                    }
                });
                
                return def;
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
            <div id="contentSection" class="content contentBlockWhite top">
                <div dojoType="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="false" id="borderContainerMdekAdminUserAdmin" style="height:680px;">
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
                    <div dojoType="dijit.layout.BorderContainer" splitter="false" region="center">
                        <!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
                        <span class="functionalLink onTab" style="top: 0px;">
                            <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=user-administration-1#user-administration-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
                        </span>
                        <div id="userManagementTab" dojoType="dijit.layout.TabContainer" splitter="false" region="center" doLayout="true" style="width:100%;" selectedChild="userData">
                            <!-- MAIN TAB 1 START -->
                            <div id="userData" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.admin.users.userData" />" class="tab grey" style="overflow:hidden;">
                                <div class="inputContainer field grey">
                                    <span class="outer"><div>
                                        <span class="label left">
                                            <label for="userDataLogin" onclick="javascript:dialog.showContextHelp(arguments[0], 8013)">
                                                <fmt:message key="dialog.admin.users.login" />
                                            </label>
                                        </span>
                                        <span class="functionalLink marginRight"><img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:void(0);" onClick="javascript:scriptScopeUser.searchForPortalUser();" title="<fmt:message key="dialog.admin.users.searchLoginUser" /> [Popup]"><fmt:message key="dialog.admin.users.searchLoginUser" /></a></span>
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
                                    
                                <!-- USER DATA -->
                                <div class="outer" style="margin-top:15px; margin-bottom:15px;">
                                    <span id="surnameInput" class="outer required" style="width:33%;"><div>
                                        <span class="label">
                                            <label id="userDataAddressSurnameLabel" for="userDataAddressSurname" onclick="javascript:dialog.showContextHelp(arguments[0], 8015)">
                                                <fmt:message key="dialog.admin.users.surName" /><span class="requiredSign">*</span>
                                            </label>
                                        </span>
                                        <span class="input"><input type="text" maxLength="255" id="userDataAddressSurname" name="userDataAddressSurname" required="true" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                                    </div></span>
                                    <span id="forenameInput" class="outer required" style="width:33%;"><div>
                                        <span class="label">
                                            <label id="userDataAddressForenameLabel" for="userDataAddressForename" onclick="javascript:dialog.showContextHelp(arguments[0], 8200)">
                                                <fmt:message key="dialog.admin.users.foreName" /><span class="requiredSign">*</span>
                                            </label>
                                        </span>
                                        <span class="input"><input type="text" maxLength="255" id="userDataAddressForename" name="userDataAddressForename" required="true" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                                    </div></span>
                                    <span class="outer required" style="width:34%;"><div>
                                        <span class="label">
                                            <label id="userDataAddressEmailUserLabel" for="userDataAddressEmailUser" onclick="javascript:dialog.showContextHelp(arguments[0], 8201)">
                                                <fmt:message key="dialog.admin.users.emailUser" /><span class="requiredSign">*</span>
                                            </label>
                                        </span>
                                        <span class="input"><input type="text" maxLength="255" id="userDataAddressEmailUser" name="userDataAddressEmailUser" required="true" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                                    </div></span>
                                    <span id="institutionInput" class="outer required" style="width:33%; clear:both;"><div>
                                        <span class="label">
                                            <label id="userDataAddressInstitutionLabel" for="userDataAddressInstitution" onclick="javascript:dialog.showContextHelp(arguments[0], 8203)">
                                                <fmt:message key="dialog.admin.users.institution" /><span class="requiredSign">*</span>
                                            </label>
                                        </span>
                                        <span class="input"><input type="text" id="userDataAddressInstitution" name="userDataAddressEmailPointOfContact" required="true" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                                    </div></span>
                                    <span class="outer" style="width:33%;"><div>
                                        <span class="label">
                                            <label id="userDataAddressPhoneLabel" for="userDataAddressPhone" onclick="javascript:dialog.showContextHelp(arguments[0], 8207)">
                                                <fmt:message key="dialog.admin.users.phone" />
                                            </label>
                                        </span>
                                        <span class="input"><input type="text" maxLength="255" id="userDataAddressPhone" name="userDataAddressPhone" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                                    </div></span>
                                    <span class="outer" style="width:34%;"><div>
                                        <span class="label">
                                            <label id="userDataAddressEmailPointOfContactLabel" for="userDataAddressEmailPointOfContact" onclick="javascript:dialog.showContextHelp(arguments[0], 8202)">
                                                <fmt:message key="dialog.admin.users.emailPointOfContact" />
                                            </label>
                                        </span>
                                        <span class="input"><input type="text" maxLength="255" id="userDataAddressEmailPointOfContact" name="userDataAddressEmailPointOfContact" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                                    </div></span>
                                    <span class="outer" style="width:33%; clear:both;"><div>
                                        <span class="label">
                                            <label id="userDataAddressStreetLabel" for="userDataAddressStreet" onclick="javascript:dialog.showContextHelp(arguments[0], 8204)">
                                                <fmt:message key="dialog.admin.users.street" />
                                            </label>
                                        </span>
                                        <span class="input"><input type="text" maxLength="255" id="userDataAddressStreet" name="userDataAddressStreet" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                                    </div></span>
                                    <span class="outer" style="width:33%;"><div>
                                        <span class="label">
                                            <label id="userDataAddressPostCodeLabel" for="userDataAddressPostCode" onclick="javascript:dialog.showContextHelp(arguments[0], 8205)">
                                                <fmt:message key="dialog.admin.users.postCode" />
                                            </label>
                                        </span>
                                        <span class="input"><input type="text" maxLength="10" id="userDataAddressPostCode" name="userDataAddressPostCode" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                                    </div></span>
                                    <span class="outer" style="width:34%;"><div>
                                        <span class="label">
                                            <label id="userDataAddressCityLabel" for="userDataAddressCity" onclick="javascript:dialog.showContextHelp(arguments[0], 8206)">
                                                <fmt:message key="dialog.admin.users.city" />
                                            </label>
                                        </span>
                                        <span class="input"><input type="text" maxLength="255" id="userDataAddressCity" name="userDataAddressCity" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                                    </div></span>
                                </div>

                                    <span class="outer" style="width:45%; clear:both;"><div>
                                        <span class="label"><label id="availableGroupsListLabel" for="availableGroupsList" onclick="javascript:dialog.showContextHelp(arguments[0], 8016)"><fmt:message key="dialog.admin.users.groupsAvailable"/></label></span>
                                        <div class="tableContainer">
                                            <div id="availableGroupsList" autoHeight="10" contextMenu="none" ></div>
                                        </div>
                                    </div></span>
                                    
                                    <span class="outer" style="width:60px;"><div>
                                        <span class="entry">
                                            <span class="buttonCol" style="margin:80px -4px 0px;">
                                                <button dojoType="dijit.form.Button" id="addSelectedButton" onClick="addSelected">&nbsp;&gt;&nbsp;</button>
                                                <button dojoType="dijit.form.Button" id="addAllButton" onClick="addAll">&gt;&gt;</button>
                                                <button dojoType="dijit.form.Button" id="removeAllButton" onClick="removeAll">&lt;&lt;</button>
                                                <button dojoType="dijit.form.Button" id="removeSelectedButton" onClick="removeSelected">&nbsp;&lt;&nbsp;</button>
                                            </span>
                                        </span>
                                    </div></span>
                                    
                                    <span class="outer" style="width:45%;"><div>
                                        <span class="label"><label id="groupsListLabel" for="groupsList" onclick="javascript:dialog.showContextHelp(arguments[0], 8016)"><fmt:message key="dialog.admin.users.groupsAssigned" /></label></span>
                                        <div class="tableContainer">
                                            <div id="groupsList" autoHeight="10" contextMenu="none" ></div>
                                        </div>
                                    </div></span>
                                    <div class="fill"></div>
                                </div>
                            </div> <!-- END OF TAB 1 -->
                            <!-- BEGIN OF TAB 2 -->
                            <div id="responsibleData" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.admin.users.responsible" />" class="tab grey" style="width: 100%; overflow:hidden;">
                                <div class="inputContainer field grey">
                                    <span class="label"><label><fmt:message key="dialog.admin.users.inObjects" /></label></span>
                                    <div class="tableContainer spaceBelow">
                                        <div id="responsibleUserInObjects" autoHeight="10" contextMenu="none">
                                        </div>
                                    </div>
                                    <span class="label"><label><fmt:message key="dialog.admin.users.inAddresses" /></label></span>
                                    <div class="tableContainer">
                                        <div id="responsibleUserInAddresses" autoHeight="10" contextMenu="none">
                                        </div>
                                    </div>
                                </div>
                            </div> <!-- END OF TAB 2 -->
                        </div>
                        <!-- </div> -->
                        <div dojoType="dijit.layout.ContentPane" splitter="false" region="bottom">
                            <div class="inputContainer">
                                <span class="button"><span style="float:right;">
                                    <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.users.save" />" onClick="javascript:scriptScopeUser.saveUser();">
                                        <fmt:message key="dialog.admin.users.save" />
                                    </button>
                                </span><span id="adminUserLoadingZone" style="float:left; margin:4px 0 0 4px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span></span>
                            </div>
                        </div>
                        <!-- RIGHT HAND SIDE CONTENT BLOCK 1 END -->
                    </div>
                </div>
            </div>
        <!--</div>--><!-- CONTENT END -->
    </body>
</html>