<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <script type="text/javascript">
            dojo.require("dojo.data.ItemFileWriteStore");
            dojo.require("dijit.form.CheckBox");
            dojo.require("dijit.form.ValidationTextBox");
            
            dojo.require("ingrid.dijit.CustomTree");
            dojo.require("ingrid.dijit.tree.LazyTreeStoreModel");
            
            var currentUserGroupDetails = null;
            var curSelectedGroupDetails = null;
            var adminGroupScope = _container_;
            
            showLoadingZone();
            
            var def = createLayout();
            if (currentUser.role == 1) {
                var def = new dojo.Deferred();
                def.callback(null);
            }
            else {
				def.then(function() {return getGroupDetailsById(currentUser.groupIds);});
            }
            
            def.then(function(groupDetails){
                currentUserGroupDetails = groupDetails;
                hidePermissionLists();
                hideLoadingZone();
            });
            
            function createLayout(){
                var groupsStructure = [
                    {field: 'name',name: "<fmt:message key='dialog.admin.groups.header' />",width: 258-scrollBarWidth+'px'}
                ];
                var def = createDataGrid("groups", null, groupsStructure, dojo.partial(UtilSecurity.getAllGroups, false));
                def.addCallback(initGroupList);
                
                var objRightsStructure = [
                    {field: 'title',name: "<fmt:message key='dialog.admin.groups.objectName' />",width: '350px'},
                    {field: 'single',name: "<fmt:message key='dialog.admin.groups.objectSingle' />", editor: YesNoCheckboxCellEditor, formatter:BoolCellFormatter, editable:true, width: '100px'}, 
                    {field: 'tree',name: "<fmt:message key='dialog.admin.groups.objectTree' />", editor: YesNoCheckboxCellEditor, formatter:BoolCellFormatter, editable:true, width: '100px'},
                    {field: 'subnode',name: "<fmt:message key='dialog.admin.groups.objectSubNode' />", editor: YesNoCheckboxCellEditor, formatter:BoolCellFormatter, editable:true, width: '100px'}
                ];
                
                var addrRightsStructure = [
                    {field: 'title',name: "<fmt:message key='dialog.admin.groups.addressName' />",width: '350px'},
                    {field: 'single',name: "<fmt:message key='dialog.admin.groups.addressSingle' />", editor: YesNoCheckboxCellEditor, formatter:BoolCellFormatter, editable:true, width: '100px'}, 
                    {field: 'tree',name: "<fmt:message key='dialog.admin.groups.addressTree' />", editor: YesNoCheckboxCellEditor, formatter:BoolCellFormatter, editable:true, width: '100px'},
                    {field: 'subnode',name: "<fmt:message key='dialog.admin.groups.addressSubNode' />", editor: YesNoCheckboxCellEditor, formatter:BoolCellFormatter, editable:true, width: '100px'}
                ];
                
                var usersStructure = [
                   {field: 'nodeDocType', name: "&nbsp;", width: '23px',formatter:renderIconClass },
                   {field: 'title',name: "<fmt:message key='dialog.admin.groups.userName' />",width: '350px'}
               ];
                
                createDataGrid("groupDataRightsObjectsList", null, objRightsStructure, null);
                createDataGrid("groupDataRightsAddressesList", null, addrRightsStructure, null);
                createDataGrid("usersOfGroupTable", null, usersStructure, null);
                
                createCustomTree("treeObjects", null, "id", "title", expandLoadObjects);
                createCustomTree("treeAddresses", null, "id", "title", expandLoadAddresses);

                var radioBehavior = function(msg) {
                    if (msg.cell > 0) {
                        // remove true default value from any other column!
                        if (msg.cell != 1) msg.item.single = false;
                        if (msg.cell != 2) msg.item.tree = false;
                        if (msg.cell != 3) msg.item.subnode = false;
                        this.invalidate();
                    }
                };
                
                dojo.connect(dijit.byId("groupDataRightsObjectsList"), "onCellChange", radioBehavior);
                dojo.connect(dijit.byId("groupDataRightsAddressesList"), "onCellChange", radioBehavior);
                
				return def;
            }
            
            
            
            function expandLoadObjects(node, callback_function){
                var parentItem = node.item;
				var prefix = "groupAdmin_";
                var store = dijit.byId("treeObjects").model.store;
                var def = UtilTree.getSubTree(parentItem, prefix.length);
                
                def.addCallback(function(data){
                    // just use the object node here!
                    if (parentItem.root) {
                        data[0].id = prefix + data[0].id;
                        store.newItem(data[0]);
                    }
                    else {
                        //parentItem.id = "groupAdmin_"+parentItem.id;
                        dojo.forEach(data, function(entry){
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
            
            function expandLoadAddresses(node, callback_function){
                var parentItem = node.item;
				var prefix = "groupAdmin_";
                var store = dijit.byId("treeAddresses").model.store;
                
                var def = UtilTree.getSubTree(parentItem, prefix.length);
                
                def.addCallback(function(data){
                    // just use the object node here!
                    if (parentItem.root) {
                        var origId = data[1].id;
						data[1].id = prefix + data[1].id;
                        store.newItem(data[1]);
                    }
                    else {
                        dojo.forEach(data, function(entry){
                            var origId = entry.id;
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
            
            function deleteGroup(group) {
                var def = new dojo.Deferred();
                var deferred = new dojo.Deferred();
                
                var displayText = dojo.string.substitute("<fmt:message key='dialog.admin.groups.confirmDelete' />", [group.name]);
                dialog.show("<fmt:message key='dialog.admin.users.deleteGroup' />", displayText, dialog.INFO, [{
                    caption: "<fmt:message key='general.no' />",
                    action: function(){console.debug("error call");deferred.errback();}
                }, {
                    caption: "<fmt:message key='general.ok' />",
                    action: function(){deferred.callback();}
                }]);
                
                deferred.addCallback(function(){
                    SecurityService.deleteGroup(group.id, {
                        preHook: showLoadingZone,
                        postHook: hideLoadingZone,
                        callback: function(){
                            def.callback();
                        },
                        errorHandler: function(errMsg, err){
                            hideLoadingZone();
                            displayErrorMessage(err);
                            console.debug(errMsg);
                            displayDeleteGroupError(err);
                            def.errback();
                        }
                    });
                });
                deferred.addErrback(function(){
                    def.errback();
                });
                
                return def;
            }
            
            function reloadCurrentGroup() {
                var selectedGroups = UtilGrid.getSelectedData("groups");
                if (selectedGroups.length == 1) {
                    updateAllInputData(selectedGroups[0]);
                }
            }
            
            function addObjectToPermissionTable(obj) {
                var data = UtilGrid.getTableData("groupDataRightsObjectsList");
                if (dojo.every(data, function(item){
                    return (item.uuid != obj.id);
                })) {
                    var permission = {};
                    permission.title = obj.title;
                    permission.uuid = obj.id;
                    permission.single = false;
                    permission.tree = true
                    permission.subnode = false;
                    
                    UtilGrid.addTableDataRow("groupDataRightsObjectsList", permission);
                }
            }
            
            function addAddressToPermissionTable(adr) {
                var data = UtilGrid.getTableData("groupDataRightsAddressesList");
                if (dojo.every(data, function(item){
                    return (item.uuid != adr.id);
                })) {
                    var permission = {};
                    permission.title = adr.title;
                    permission.uuid = adr.id;
                    permission.single = false;
                    permission.tree = true;
                    permission.subnode = false;
                    
                    UtilGrid.addTableDataRow("groupDataRightsAddressesList", permission);
                }
            }
            
            function addFreeRootToPermissionTable(node) {
                var deferred = new dojo.Deferred();
                var uuid = node.id[0].substr(11);
                
                console.debug("Getting all free addresses: " + uuid);
                TreeService.getSubTree(uuid, node.nodeAppType[0], userLocale, {
                    callback: function(res){
                        deferred.callback(res);
                    },
                    errorHandler: function(message){
                        deferred.errback(message);
                    }
                });
                
                deferred.addCallback(function(res){
                    console.debug("Adding free addresses to permission table");
                    dojo.forEach(res, function(adr){
                        addAddressToPermissionTable({id:adr.id, title:adr.title});
                    });
                });
                deferred.addErrback(function(res){
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='tree.loadError' />", dialog.WARNING);
                    console.debug(res);
                    return res;
                });
                return deferred;
            }
            
            // 'Create new group' button function.
            // If a group is selected, reset the input fields and deselect any selected groups
            // Otherwise create a new group
            adminGroupScope.newGroup = function() {
                var groupName = dojo.trim(dijit.byId("groupDataName").getValue());
                            
                // if nothing is selected
                if (UtilGrid.getSelectedRowIndexes("groups").length != 1) {
                    createNewGroup(groupName);
                } else {
                    UtilGrid.clearSelection("groups");
                    
                    resetAllInputData();
                    hidePermissionLists();
                }
            }
            
            
            adminGroupScope.mapPermissions = function(data) {
                var objPermissionList = [];
                    
                dojo.forEach(data, function(obj){
                    var id = obj.uuid;
                    var permissionType;
                    if (obj.tree) {
                        permissionType = "WRITE_TREE";
                    } else if (obj.subnode) {
                        permissionType = "WRITE_SUBNODE";
                    } else {
                        permissionType = "WRITE_SINGLE";
                    } 
                    objPermissionList.push( { uuid:id, permission:permissionType } );
                });
                return objPermissionList;
            }
            
            // 'Save group' button function.
            // If a group is selected update it with the new name.
            // If zero or multiple groups are selected, create a new group.
            adminGroupScope.saveGroup = function() {
                var groupName = dojo.trim(dijit.byId("groupDataName").getValue());
                            
                var selectedGroups = UtilGrid.getSelectedRowIndexes("groups").length;
                if (selectedGroups == 1) {
                    // exactly one group selected
                    //      var group = selectedGroups[0];
                    var group = curSelectedGroupDetails;
                    
                    // update group name
                    group.name = groupName;
                    
                    // update object permissions
                    var grid = UtilGrid.getTable("groupDataRightsObjectsList");
                    var objs = UtilGrid.getTableData("groupDataRightsObjectsList");
                    var objPermissionList = [];
                    
                    console.debug("map permissions object");
                    group.objectPermissions = adminGroupScope.mapPermissions(objs, grid);//objPermissionList;
                    
                    // update address permissions
                    var gridAdr = UtilGrid.getTable("groupDataRightsAddressesList");
                    var adrs = UtilGrid.getTableData("groupDataRightsAddressesList");
                    var adrPermissionList = [];
                    
                    console.debug("map permissions address");
                    group.addressPermissions = adminGroupScope.mapPermissions(adrs, gridAdr);//adrPermissionList;
                    
                    // update group permissions
                    group.groupPermissions = [];
                    if (dijit.byId("userDataCreate").checked) {
                        group.groupPermissions.push("CREATE_ROOT");
                    }
                    if (dijit.byId("userDataQS").checked) {
                        group.groupPermissions.push("QUALITY_ASSURANCE");
                    }
                    
                    
                    console.debug("store group:");
                    var def = storeGroup(group, true);
                    def.addCallback(function(storedGroup){
                        console.debug("update group name");
                        // updated name of group in case it was changed
                        //groupStore.update(selectedGroups[0], "name", storedGroup.name);
                        UtilGrid.updateTableDataRowAttr("groups", UtilGrid.getSelectedRowIndexes("groups")[0], "name", storedGroup.name);
                        curSelectedGroupDetails = storedGroup;
                        
                        dialog.show("<fmt:message key='dialog.storeGroupTitle' />", "<fmt:message key='dialog.storeGroupSuccess' />", dialog.INFO, [{
                            caption: "<fmt:message key='general.ok' />",
                            action: function(){
                            }
                        }]);
                    });
                    
                    def.addErrback(function(err){
                        console.debug("Error: " + err);
                        if (err && err.message) {
                            if (err.message.indexOf("NO_RIGHT_TO_REMOVE_USER_PERMISSION") != -1 ||
                            err.message.indexOf("NO_RIGHT_TO_ADD_USER_PERMISSION") != -1 ||
                            err.message.indexOf("NO_RIGHT_TO_REMOVE_OBJECT_PERMISSION") != -1 ||
                            err.message.indexOf("NO_RIGHT_TO_REMOVE_ADDRESS_PERMISSION") != -1) {
                                reloadCurrentGroup();
                            }
                        }
                    });
                    
                }
                else {
                    // zero or multiple groups selected
                    createNewGroup(groupName);
                }
            }
            
            
            // 'Add Object' button function.
            adminGroupScope.addObject = function() {
                var obj = dijit.byId("treeObjects").selectedNode.item;
                            
                // remove prefix from node id
                //obj.id[0] = obj.id[0].substr(11);
                
                if (obj == null || obj.id[0] == "groupAdmin_objectRoot") {
                    return;
                    
                }
                else {
                    addObjectToPermissionTable({id:obj.id[0].substr(11), title: obj.title[0]});
                }
            }
            
            // 'Add Address' button function.
            adminGroupScope.addAddress = function() {
                var adr = dijit.byId("treeAddresses").selectedNode.item;
                            
                // remove prefix from node id
                //adr.id[0] = adr.id[0].substr(11);
                
                if (adr == null || adr.id[0] == "groupAdmin_addressRoot") {
                    return;
                }
                else 
                    if (adr.id == "groupAdmin_addressFreeRoot") {
                        addFreeRootToPermissionTable(adr);
                    }
                    else {
                        addAddressToPermissionTable({id: adr.id[0].substr(11),title: adr.title[0]});
                    }
            }
            
            // Creates a new group and returns a deferred obj which is called with the newly created group
            function createNewGroup(groupName) {
                var deferred = new dojo.Deferred();
                            
                if (groupName.length == 0) {
                    return;
                }
                
                SecurityService.createGroup({
                    name: groupName
                }, true, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(data){
                        var item = UtilGrid.addTableDataRow("groups", data);
                        deferred.callback(data);
                        UtilGrid.setSelection("groups", [UtilGrid.getTableData("groups").length-1]);
                    },
                    errorHandler: function(errMsg, err){
                        hideLoadingZone();
                        displayCreateGroupErrorMessage(err);
                        deferred.errback(err);
                    }
                });
                
                return deferred;
            }
            
            // Initialize the group table
            function initGroupList() {
                var groupList = UtilGrid.getTable("groups");
            
                //dojo.connect(groupList, "deleteRow", function(invocation){
                dojo.connect(groupList, "onDeleteItems", function(msg) {
                    console.debug("delete group:");
                    console.debug(msg.item);
                    var groupToDelete = msg.items[0];
                    var def = deleteGroup(groupToDelete);
                    def.addErrback(function(error) {
                        UtilGrid.addTableDataRow("groups", groupToDelete);
                    });
                    return def;
                });
                
                dojo.connect(groupList, "onSelectedRowsChanged", function(e) {
                    console.debug("group selected");
                    // Update the displayed data
                    var selectedGroups = UtilGrid.getSelectedData("groups");
                    if (selectedGroups.length == 1 && selectedGroups[0] != undefined) {
                        // one group selected, load and display it's group data
                        updateAllInputData(selectedGroups[0]);
                        showPermissionLists();
                        
                    }
                    else {
                        // zero or multiple groups selected
                        resetAllInputData();
                        hidePermissionLists();
                    }
                });
            }
            
            // Store the group 'group'
            function storeGroup(group, refetch) {
                var deferred = new dojo.Deferred();
                            
                SecurityService.storeGroup(group, refetch, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(group){
                        deferred.callback(group);
                    },
                    errorHandler: function(errMsg, err){
                        hideLoadingZone();
                        displayStoreGroupErrorMessage(err);
                        deferred.errback(errMsg);
                    }
                });
                
                return deferred;
            }
            
            
            function updateAllInputData(group) {
                console.debug("update all input data");
                dijit.byId("groupDataName").setValue(group.name);
                
                // Load the group details from the server
                var def = getGroupDetails(group.name);
                def.addCallback(function(groupDetails){
                    curSelectedGroupDetails = groupDetails;
                    
                    setAddressPermissions(groupDetails.addressPermissions);
                    setObjectPermissions(groupDetails.objectPermissions);
                    setGroupPermissions(groupDetails.groupPermissions);
                });
                
                var def2 = UtilSecurity.getUsersFromGroup(group.name);
                def2.addCallback(function(users){
                    dojo.forEach(users, function(adr) {
                        adr.title = UtilAddress.createAddressTitle(adr.address);
                    });
                    UtilGrid.setTableData("usersOfGroupTable", users);
                });
            }
            
            
            function setAddressPermissions(permissionList) {
                UtilGrid.setTableData("groupDataRightsAddressesList", []);
                            
                dojo.forEach(permissionList, function(p){
                    var adr = {}
                    
                    adr.title = UtilAddress.createAddressTitle(p.address);
                    adr.uuid = p.uuid;
                    
                    if (p.permission == "WRITE_SINGLE") {
                        adr.single = true;
                        adr.tree = false;
                        adr.subnode = false;
                    } else if (p.permission == "WRITE_TREE") {
                        adr.single =  false;
                        adr.tree = true;
                        adr.subnode = false;
                    } else {
                        adr.single = false;
                        adr.tree = false;
                        adr.subnode = true;
                    }
                    
                    UtilGrid.addTableDataRow("groupDataRightsAddressesList", adr);
                });
            }
            
            
            function setObjectPermissions(permissionList) {
                UtilGrid.setTableData("groupDataRightsObjectsList", []);
                            
                dojo.forEach(permissionList, function(p){
                    var obj = {}
                    
                    obj.title = p.object.title;
                    obj.uuid = p.uuid;
                    
                    if (p.permission == "WRITE_SINGLE") {
                        obj.single = true;
                        obj.tree = false;
                        obj.subnode = false;
                    } else if (p.permission == "WRITE_TREE") {
                        obj.single = false;
                        obj.tree = true;
                        obj.subnode = false;
                    } else {
                        obj.single = false;
                        obj.tree = false;
                        obj.subnode = true;
                    }
                    
                    UtilGrid.addTableDataRow("groupDataRightsObjectsList", obj);
                });
            }
            
            function setGroupPermissions(permissionList) {
                var canCreateRoot = false;
                var isQA = false;
                
                dojo.forEach(permissionList, function(idcPermission){
                    if (idcPermission == "CREATE_ROOT") {
                        canCreateRoot = true;
                    } else 
                        if (idcPermission == "QUALITY_ASSURANCE") {
                            isQA = true;
                        }
                });
                
                dijit.byId("userDataCreate").setValue(canCreateRoot);
                dijit.byId("userDataQS").setValue(isQA);
            }
            
            
            function resetAllInputData() {
                dijit.byId("groupDataName").setValue("");
                            
                UtilGrid.setTableData("groupDataRightsObjectsList", []);
                UtilGrid.setTableData("groupDataRightsAddressesList", []);
                
                dijit.byId("userDataCreate").setValue(false);
                dijit.byId("userDataQS").setValue(false);
            }
            
            function hidePermissionLists() {
                dojo.byId('permissionCheckboxContainer').style.display = "none";
                dojo.byId('permissionListObjects').style.display = "none";
                dojo.byId('permissionListAddresses').style.display = "none";
            }
            
            function showPermissionLists() {
                dojo.byId('permissionCheckboxContainer').style.display = "block";
                dojo.byId('permissionListObjects').style.display = "block";
                dojo.byId('permissionListAddresses').style.display = "block";
                
                dijit.byId("groupDataObjects").resize();
                dijit.byId("groupDataAddresses").resize();
            }
            
            function getGroupDetails(groupName) {
            
                var deferred = new dojo.Deferred();
                            
                SecurityService.getGroupDetails(groupName, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(groupDetails){
                        deferred.callback(groupDetails);
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
            
            function getGroupDetailsById(groupIds) {
            
                var def = UtilSecurity.getAllGroups(false);
                            
                def.addCallback(function(groupList){
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
            //          dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.noPermissionError' />", dialog.WARNING);
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.groups.groupAlreadyExistsError' />", dialog.WARNING);
            
                    } else {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substituteParams("<fmt:message key='dialog.generalError' />", err.message), dialog.WARNING, null, 350, 350);               
                    }
            
                } else {
                    // Show general error message if we can't determine what went wrong
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.undefinedError' />", dialog.WARNING);
                }
            }
            
            function displayStoreGroupErrorMessage(err) {
                if (err && err.message) {
                    if (err.message.indexOf("SINGLE_BELOW_TREE_OBJECT_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.object.singleBelowTreeError' />", [err.invalidObject.title, err.rootObject.title]), dialog.WARNING, null, 350, 200);
                        
                    }
                    else if (err.message.indexOf("TREE_BELOW_TREE_OBJECT_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.object.treeBelowTreeError' />", [err.invalidObject.title, err.rootObject.title]), dialog.WARNING, null, 350, 200);
                            
                    } else if (err.message.indexOf("SINGLE_BELOW_TREE_ADDRESS_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.address.singleBelowTreeError' />", [UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);
                                
                    } else if (err.message.indexOf("TREE_BELOW_TREE_ADDRESS_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.address.treeBelowTreeError' />", [UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);
                                    
                    } else if (err.message.indexOf("USER_EDITING_OBJECT_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.object.userEditingError' />", [err.invalidObject.title, UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);
                                        
                    } else if (err.message.indexOf("USER_RESPONSIBLE_FOR_OBJECT_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.object.userResponsibleError' />", [err.invalidObject.title, UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);
                                            
                    } else if (err.message.indexOf("USER_EDITING_ADDRESS_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.address.userEditingError' />", [UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);
                                                
                    } else if (err.message.indexOf("USER_RESPONSIBLE_FOR_ADDRESS_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.address.userResponsibleError' />", [UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);
                                                    
                    } else if (err.message.indexOf("NO_RIGHT_TO_REMOVE_OBJECT_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.object.permissionError' />", [err.rootObject.title]), dialog.WARNING, null, 350, 200);
                                                        
                    } else if (err.message.indexOf("NO_RIGHT_TO_REMOVE_ADDRESS_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.address.permissionError' />", [UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);
                            
                    } else if (err.message.indexOf("NO_RIGHT_TO_REMOVE_USER_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.groups.removePermissionError' />", dialog.WARNING, null, 350, 200);
                                
                    } else if (err.message.indexOf("NO_RIGHT_TO_ADD_USER_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.groups.addPermissionError' />", dialog.WARNING, null, 350, 200);
                    
                    } else {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.generalError' />", [err.message]), dialog.WARNING, null, 350, 350);
                    }
                    
                }
                else {
                    // Show general error message if we can't determine what went wrong
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.undefinedError' />", dialog.WARNING);
                }
            }
            
            function displayDeleteGroupError(err) {
                if (err && err.message) {
                    if (err.message.indexOf("GROUP_HAS_USERS") != -1) {
                        var userList = "";
                        dojo.forEach(err.addresses, function(adr){
                            userList += UtilAddress.createAddressTitle(adr) + "\n";
                        });
                        userList = dojo.string.trim(userList);
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.groupHasUsersError' />", [userList]), dialog.WARNING, null, 350, 200);
                        
                    } else if (err.message.indexOf("USER_EDITING_OBJECT_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.object.userEditingError' />", [err.invalidObject.title, UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);
                            
                    } else if (err.message.indexOf("USER_RESPONSIBLE_FOR_OBJECT_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.object.userResponsibleError' />", [err.invalidObject.title, UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);
                                
                    } else if (err.message.indexOf("USER_EDITING_ADDRESS_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.address.userEditingError' />", [UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);
                                    
                    } else if (err.message.indexOf("USER_RESPONSIBLE_FOR_ADDRESS_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.admin.groups.address.userResponsibleError' />", [UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);
                                        
                    } else {
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.generalError' />", [err.message]), dialog.WARNING, null, 350, 350);
                    }
                    
                } else {
                    // Show general error message if we can't determine what went wrong
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.undefinedError' />", dialog.WARNING);
                }
            }
            
            function showLoadingZone() {
                dojo.byId("adminGroupLoadingZone").style.visibility = "visible";
            }
            
            function hideLoadingZone() {
                dojo.byId("adminGroupLoadingZone").style.visibility = "hidden";
            }
            
            
        </script>
    </head>
    <body>
        <!-- CONTENT START -->
        <div class="contentBlockWhite top">
            <div id="winNavi" style="width: 100%; padding-bottom: 35px;">
                <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=user-administration-2#user-administration-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
            <div id="groupAdmin" class="content">
                <div dojoType="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="false" class="spaceBelow" style="height:250px; clear:both;" layoutAlign="client" design="headline">
                    <!-- LEFT HAND SIDE CONTENT BLOCK 1 START -->
                    <div dojoType="dijit.layout.ContentPane" region="leading" class="tableContainer" style="width: 270px; padding:5px;">
                        <div id="groups" autoHeight="9" forceGridHeight="false">
                        </div>
                    </div>
                    <!-- LEFT HAND SIDE CONTENT BLOCK 1 END --><!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
                    <div dojoType="dijit.layout.ContentPane" region="center" id="groupData_" class="inputContainer innerPadding">
                        <span class="label">
                            <label onclick="javascript:dialog.showContextHelp(arguments[0], 8018)">
                                <fmt:message key="dialog.admin.groups.data" />
                            </label>
                        </span>
                        <div class="inputContainer field grey">
                            <span class="label">
                                <label for="groupDataName" onclick="javascript:dialog.showContextHelp(arguments[0], 8019)">
                                    <fmt:message key="dialog.admin.groups.name" />
                                </label>
                            </span><span class="input"><input type="text" maxlength="50" id="groupDataName" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                            <div class="spacerField">
                            </div>
                        </div>
                        <div class="inputContainer">
                            <span class="button"><span style="float: right;">
                                    <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.groups.save" />" onClick="javascript:adminGroupScope.saveGroup();">
                                        <fmt:message key="dialog.admin.groups.save" />
                                    </button>
                                </span><span style="float: right;">
                                    <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.groups.createGroup" />" onClick="javascript:adminGroupScope.newGroup();">
                                        <fmt:message key="dialog.admin.groups.createGroup" />
                                    </button>
                                </span><span id="adminGroupLoadingZone" style="float: left; margin-top: 1px; z-index: 100; visibility: hidden"><img src="img/ladekreis.gif" /></span></span>
                        </div>
                        <div id="permissionCheckboxContainer" class="inputContainer grey field checkboxContainer" style="display: none;">
                            <span class="input"><input type="checkbox" id="userDataCreate" dojoType="dijit.form.CheckBox" />
                                <label onclick="javascript:dialog.showContextHelp(arguments[0], 8020)">
                                    <fmt:message key="dialog.admin.groups.createRoot" />
                                </label>
                            </span><span class="input"><input type="checkbox" id="userDataQS" dojoType="dijit.form.CheckBox" />
                                <label onclick="javascript:dialog.showContextHelp(arguments[0], 8021)">
                                    <fmt:message key="dialog.admin.groups.qa" />
                                </label>
                            </span>
                        </div>
                    </div><!-- RIGHT HAND SIDE CONTENT BLOCK 1 END --><!-- CONTENT BLOCK 2 START --><!-- SPLIT CONTAINER START -->
                </div>
                <div id="groupAdministrationTab" dojoType="dijit.layout.TabContainer" doLayout="false" style="width:100%;margin-top:40px;" selectedChild="permissionListObjects">
                    <!-- MAIN TAB 1 START -->
                    <div id="permissionListObjects" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.admin.groups.objectPermissions" />" class="tab" style="overflow:hidden;">
                        <div dojoType="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="false" id="groupDataObjects" layoutAlign="client" design="headline" style="height:250px;">
                            <!-- LEFT HAND SIDE CONTENT BLOCK 2 START -->
                            <div dojoType="dijit.layout.ContentPane" region="leading" class="" splitter="true" style="width: 300px;">
                                <!-- tree components -->
                                <div id="treeObjects">
                                </div>
                            </div>
                            <!-- LEFT HAND SIDE CONTENT BLOCK 2 END --><!-- RIGHT HAND SIDE CONTENT BLOCK 2 START -->
                            <div dojoType="dijit.layout.ContentPane" region="center" splitter="false" class="field">
                                <div class="selectEntryBtn">
                                    <button dojoType="dijit.form.Button" id="addObjectButton" onClick="javascript:adminGroupScope.addObject();">
                                        &nbsp;>&nbsp;
                                    </button>
                                </div>
                                <div id="groupDataObjectsData">
                                    <div class="tableContainer">
                                        <div id="groupDataRightsObjectsList" interactive="true" autoedit="true" autoHeight="9">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <!-- RIGHT HAND SIDE CONTENT BLOCK 2 END -->
                        </div>
                    </div>
                <!-- CONTENT BLOCK 2 END --><!-- CONTENT BLOCK 3 START --><!-- SPLIT CONTAINER START -->
                    <div id="permissionListAddresses" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.admin.groups.addressPermissions" />" class="tab" style="overflow:hidden;">
                        <div dojoType="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="false" id="groupDataAddresses" style="height:250px;">
                            <!-- LEFT HAND SIDE CONTENT BLOCK 3 START -->
                            <div dojoType="dijit.layout.ContentPane" region="leading" splitter="true" style="width: 300px;">
                                <!-- tree components -->
                                <div id="treeAddresses">
                                </div>
                            </div>
                            <!-- LEFT HAND SIDE CONTENT BLOCK 3 END -->
                            <!-- RIGHT HAND SIDE CONTENT BLOCK 3 START -->
                            <div dojoType="dijit.layout.ContentPane" region="center" splitter="false" class="inputContainer field">
                                <div class="selectEntryBtn">
                                    <button dojoType="dijit.form.Button" id="addAddressButton" onClick="javascript:adminGroupScope.addAddress();">
                                        &nbsp;>&nbsp;
                                    </button>
                                </div>
                                <div id="groupDataAddressesData">
                                    <div class="tableContainer">
                                        <div id="groupDataRightsAddressesList" interactive="true" autoedit="true" autoHeight="9">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <!-- RIGHT HAND SIDE CONTENT BLOCK 2 END -->
                        </div>
                        <!-- CONTENT BLOCK 2 END -->
                    </div>
                    <!-- CONTENT BLOCK 3 START -->
                    <div id="belongingUsers" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.admin.groups.belongingUsers" />" class="tab grey" style="overflow:hidden;">
                        <div class="field">
                            <div id="usersOfGroupTable" autoHeight="10" contextMenu="none">
                            </div>
                        </div>
                    </div>
                    <!-- CONTENT BLOCK 3 END -->
                </div>
            </div>
        </div>
        <!-- CONTENT END -->
    </body>
</html>
