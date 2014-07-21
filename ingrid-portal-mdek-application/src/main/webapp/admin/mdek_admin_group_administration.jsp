<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
    <script type="text/javascript">
        var pageGroupAdmin = _container_;

        require([
            "dojo/Deferred",
            "dojo/on",
            "dojo/aspect",
            "dojo/dom",
            "dojo/dom-construct",
            "dojo/_base/array",
            "dojo/_base/lang",
            "dojo/string",
            "dijit/registry",
            "ingrid/layoutCreator",
            "ingrid/dialog",
            "ingrid/utils/LoadingZone",
            "ingrid/utils/Security",
            "ingrid/utils/Tree",
            "ingrid/utils/Grid",
            "ingrid/utils/Address",
            "ingrid/tree/MetadataTree",
            "ingrid/grid/CustomGridEditors",
            "ingrid/grid/CustomGridFormatters"
        ], function(Deferred, on, aspect, dom, construct, array, lang, string, registry, layoutCreator, dialog, LoadingZone, UtilSecurity, UtilTree, UtilGrid, UtilAddress, MetadataTree, gridEditors, gridFormatters) {

            pageGroupAdmin.currentUserGroupDetails = null;
            pageGroupAdmin.curSelectedGroupDetails = null;

            pageGroupAdmin.prefix = "groupAdmin_";

            LoadingZone.show();

            on(_container_, "Load", function() {

                var def = pageGroupAdmin.createLayout();
                if (UtilSecurity.currentUser.role == 1) {
                    //def.resolve(null);
                } else {
                    def = def.then(function() {
                        return pageGroupAdmin.getGroupDetailsById(currentUser.groupIds);
                    });
                }

                def.then(function(groupDetails) {
                    pageGroupAdmin.currentUserGroupDetails = groupDetails;
                    pageGroupAdmin.hidePermissionLists();
                    LoadingZone.hide();
                });

                registry.byId("groupAdministrationTab").watch("selectedChildWidget", function() {
                    registry.byId("groupDataRightsAddressesList").reinitLastColumn();
                    registry.byId("usersOfGroupTable").reinitLastColumn();
                });
            });

            pageGroupAdmin.createLayout = function() {
                var groupsStructure = [{
                    field: 'name',
                    name: "<fmt:message key='dialog.admin.groups.header' />",
                    width: '258px'
                }];
                var def = layoutCreator.createDataGrid("groups", null, groupsStructure, dojo.partial(UtilSecurity.getAllGroups, false))
                .then(pageGroupAdmin.initGroupList);

                var objRightsStructure = [{
                    field: 'title',
                    name: "<fmt:message key='dialog.admin.groups.objectName' />",
                    width: '350px'
                }, {
                    field: 'single',
                    name: "<fmt:message key='dialog.admin.groups.objectSingle' />",
                    editor: gridEditors.YesNoCheckboxCellEditor,
                    formatter: gridFormatters.BoolCellFormatter,
                    editable: true,
                    width: '100px'
                }, {
                    field: 'tree',
                    name: "<fmt:message key='dialog.admin.groups.objectTree' />",
                    editor: gridEditors.YesNoCheckboxCellEditor,
                    formatter: gridFormatters.BoolCellFormatter,
                    editable: true,
                    width: '100px'
                }, {
                    field: 'subnode',
                    name: "<fmt:message key='dialog.admin.groups.objectSubNode' />",
                    editor: gridEditors.YesNoCheckboxCellEditor,
                    formatter: gridFormatters.BoolCellFormatter,
                    editable: true,
                    width: '100px'
                }];

                var addrRightsStructure = [{
                    field: 'title',
                    name: "<fmt:message key='dialog.admin.groups.addressName' />",
                    width: '350px'
                }, {
                    field: 'single',
                    name: "<fmt:message key='dialog.admin.groups.addressSingle' />",
                    editor: gridEditors.YesNoCheckboxCellEditor,
                    formatter: gridFormatters.BoolCellFormatter,
                    editable: true,
                    width: '100px'
                }, {
                    field: 'tree',
                    name: "<fmt:message key='dialog.admin.groups.addressTree' />",
                    editor: gridEditors.YesNoCheckboxCellEditor,
                    formatter: gridFormatters.BoolCellFormatter,
                    editable: true,
                    width: '100px'
                }, {
                    field: 'subnode',
                    name: "<fmt:message key='dialog.admin.groups.addressSubNode' />",
                    editor: gridEditors.YesNoCheckboxCellEditor,
                    formatter: gridFormatters.BoolCellFormatter,
                    editable: true,
                    width: '100px'
                }];

                var usersStructure = [{
                    field: 'nodeDocType',
                    name: "&nbsp;",
                    width: '23px',
                    formatter: gridFormatters.renderIconClass
                }, {
                    field: 'title',
                    name: "<fmt:message key='dialog.admin.groups.userName' />",
                    width: '350px'
                }];

                layoutCreator.createDataGrid("groupDataRightsObjectsList", null, objRightsStructure, null);
                layoutCreator.createDataGrid("groupDataRightsAddressesList", null, addrRightsStructure, null);
                layoutCreator.createDataGrid("usersOfGroupTable", null, usersStructure, null);

                new MetadataTree({
                    showRoot: false,
                    treeType: "Objects"
                }, "treeObjects");
                new MetadataTree({
                    showRoot: false,
                    treeType: "Addresses"
                }, "treeAddresses");

                var radioBehavior = function(result, params) {
                    var msg = params[0];
                    if (msg.cell > 0) {
                        // remove true default value from any other column!
                        if (msg.cell != 1) msg.item.single = false;
                        if (msg.cell != 2) msg.item.tree = false;
                        if (msg.cell != 3) msg.item.subnode = false;
                        this.invalidate();
                    }
                };

                aspect.after(registry.byId("groupDataRightsObjectsList"), "onCellChange", radioBehavior);
                aspect.after(registry.byId("groupDataRightsAddressesList"), "onCellChange", radioBehavior);

                return def;
            };

            pageGroupAdmin.deleteGroup = function(group) {
                var def = new Deferred();
                var deferred = new Deferred();

                var displayText = string.substitute("<fmt:message key='dialog.admin.groups.confirmDelete' />", [group.name]);
                dialog.show("<fmt:message key='dialog.admin.users.deleteGroup' />", displayText, dialog.INFO, [{
                    caption: "<fmt:message key='general.no' />",
                    action: function() {
                        console.debug("error call");
                        deferred.reject();
                    }
                }, {
                    caption: "<fmt:message key='general.ok' />",
                    action: function() {
                        deferred.resolve();
                    }
                }]);

                deferred.then(function() {
                    SecurityService.deleteGroup(group.id, {
                        preHook: LoadingZone.show,
                        postHook: LoadingZone.hide,
                        callback: function() {
                            def.resolve();
                        },
                        errorHandler: function(errMsg, err) {
                            pageGroupAdmin.displayErrorMessage(err);
                            console.debug(errMsg);
                            pageGroupAdmin.displayDeleteGroupError(err);
                            def.reject();
                        }
                    });
                }, function() {
                    def.reject();
                });

                return def;
            };

            pageGroupAdmin.reloadCurrentGroup = function() {
                var selectedGroups = UtilGrid.getSelectedData("groups");
                if (selectedGroups.length == 1) {
                    pageGroupAdmin.updateAllInputData(selectedGroups[0]);
                }
            };

            pageGroupAdmin.addObjectToPermissionTable = function(obj) {
                var data = UtilGrid.getTableData("groupDataRightsObjectsList");
                if (dojo.every(data, function(item) {
                    return (item.uuid != obj.id);
                })) {
                    var permission = {};
                    permission.title = obj.title;
                    permission.uuid = obj.id;
                    permission.single = false;
                    permission.tree = true;
                    permission.subnode = false;

                    UtilGrid.addTableDataRow("groupDataRightsObjectsList", permission);
                }
            };

            pageGroupAdmin.addAddressToPermissionTable = function(adr) {
                var data = UtilGrid.getTableData("groupDataRightsAddressesList");
                if (dojo.every(data, function(item) {
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
            };

            pageGroupAdmin.addFreeRootToPermissionTable = function(node) {
                var deferred = new Deferred();
                var uuid = node.item.id;

                console.debug("Getting all free addresses: " + uuid);
                TreeService.getSubTree(uuid, node.nodeAppType, userLocale, {
                    callback: function(res) {
                        deferred.resolve(res);
                    },
                    errorHandler: function(message) {
                        deferred.reject(message);
                    }
                });

                deferred.then(function(res) {
                    console.debug("Adding free addresses to permission table");
                    array.forEach(res, function(adr) {
                        pageGroupAdmin.addAddressToPermissionTable({
                            id: adr.id,
                            title: adr.title
                        });
                    });
                });
                deferred.addErrback(function(res) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='tree.loadError' />", dialog.WARNING);
                    console.debug(res);
                    return res;
                });
                return deferred;
            };

            // 'Create new group' button function.
            // If a group is selected, reset the input fields and deselect any selected groups
            // Otherwise create a new group
            pageGroupAdmin.newGroup = function() {
                var groupName = lang.trim(registry.byId("groupDataName").getValue());

                // if nothing is selected
                if (UtilGrid.getSelectedRowIndexes("groups").length != 1) {
                    pageGroupAdmin.createNewGroup(groupName);
                } else {
                    UtilGrid.clearSelection("groups");

                    pageGroupAdmin.resetAllInputData();
                    pageGroupAdmin.hidePermissionLists();
                }
            };


            pageGroupAdmin.mapPermissions = function(data) {
                var objPermissionList = [];

                array.forEach(data, function(obj) {
                    var id = obj.uuid;
                    var permissionType;
                    if (obj.tree) {
                        permissionType = "WRITE_TREE";
                    } else if (obj.subnode) {
                        permissionType = "WRITE_SUBNODE";
                    } else {
                        permissionType = "WRITE_SINGLE";
                    }
                    objPermissionList.push({
                        uuid: id,
                        permission: permissionType
                    });
                });
                return objPermissionList;
            };

            // 'Save group' button function.
            // If a group is selected update it with the new name.
            // If zero or multiple groups are selected, create a new group.
            pageGroupAdmin.saveGroup = function() {
                var groupName = lang.trim(registry.byId("groupDataName").getValue());

                var selectedGroups = UtilGrid.getSelectedRowIndexes("groups").length;
                if (selectedGroups == 1) {
                    // exactly one group selected
                    //      var group = selectedGroups[0];
                    var group = pageGroupAdmin.curSelectedGroupDetails;

                    // update group name
                    group.name = groupName;

                    // update object permissions
                    var grid = UtilGrid.getTable("groupDataRightsObjectsList");
                    var objs = UtilGrid.getTableData("groupDataRightsObjectsList");
                    var objPermissionList = [];

                    console.debug("map permissions object");
                    group.objectPermissions = pageGroupAdmin.mapPermissions(objs, grid); //objPermissionList;

                    // update address permissions
                    var gridAdr = UtilGrid.getTable("groupDataRightsAddressesList");
                    var adrs = UtilGrid.getTableData("groupDataRightsAddressesList");
                    var adrPermissionList = [];

                    console.debug("map permissions address");
                    group.addressPermissions = pageGroupAdmin.mapPermissions(adrs, gridAdr); //adrPermissionList;

                    // update group permissions
                    group.groupPermissions = [];
                    if (registry.byId("userDataCreate").checked) {
                        group.groupPermissions.push("CREATE_ROOT");
                    }
                    if (registry.byId("userDataQS").checked) {
                        group.groupPermissions.push("QUALITY_ASSURANCE");
                    }


                    console.debug("store group:");
                    var self = this;
                    var def = pageGroupAdmin.storeGroup(group, true);
                    def.then(function(storedGroup) {
                        console.debug("update group name");
                        // updated name of group in case it was changed
                        //groupStore.update(selectedGroups[0], "name", storedGroup.name);
                        UtilGrid.updateTableDataRowAttr("groups", UtilGrid.getSelectedRowIndexes("groups")[0], "name", storedGroup.name);
                        pageGroupAdmin.curSelectedGroupDetails = storedGroup;

                        dialog.show("<fmt:message key='dialog.storeGroupTitle' />", "<fmt:message key='dialog.storeGroupSuccess' />", dialog.INFO, [{
                            caption: "<fmt:message key='general.ok' />",
                            action: function() {}
                        }]);
                    }, function(err) {
                        console.debug("Error: " + err);
                        if (err && err.message) {
                            if (err.message.indexOf("NO_RIGHT_TO_REMOVE_USER_PERMISSION") != -1 ||
                                err.message.indexOf("NO_RIGHT_TO_ADD_USER_PERMISSION") != -1 ||
                                err.message.indexOf("NO_RIGHT_TO_REMOVE_OBJECT_PERMISSION") != -1 ||
                                err.message.indexOf("NO_RIGHT_TO_REMOVE_ADDRESS_PERMISSION") != -1) {
                                self.reloadCurrentGroup();
                            }
                        }
                    });

                } else {
                    // zero or multiple groups selected
                    pageGroupAdmin.createNewGroup(groupName);
                }
            };


            // 'Add Object' button function.
            pageGroupAdmin.addObject = function() {
                var obj = registry.byId("treeObjects").selectedNode.item;

                if (obj === null || obj.id == "objectRoot") {
                    return;

                } else {
                    pageGroupAdmin.addObjectToPermissionTable({
                        id: obj.id,
                        title: obj.title
                    });
                }
            };

            // 'Add Address' button function.
            pageGroupAdmin.addAddress = function() {
                var adr = registry.byId("treeAddresses").selectedNode.item;

                // remove prefix from node id
                //adr.id[0] = adr.id[0].substr(11);

                if (adr === null || adr.id == "addressRoot") {
                    return;
                } else
                if (adr.id == "addressFreeRoot") {
                    pageGroupAdmin.addFreeRootToPermissionTable(adr);
                } else {
                    pageGroupAdmin.addAddressToPermissionTable({
                        id: adr.id,
                        title: adr.title
                    });
                }
            };

            // Creates a new group and returns a deferred obj which is called with the newly created group
            pageGroupAdmin.createNewGroup = function(groupName) {
                var deferred = new Deferred();

                if (groupName.length === 0) {
                    return;
                }

                SecurityService.createGroup({
                    name: groupName
                }, true, {
                    preHook: LoadingZone.show,
                    postHook: LoadingZone.hide,
                    callback: function(data) {
                        UtilGrid.addTableDataRow("groups", data);
                        deferred.resolve(data);
                        UtilGrid.setSelection("groups", [UtilGrid.getTableData("groups").length - 1]);
                    },
                    errorHandler: function(errMsg, err) {
                        pageGroupAdmin.displayCreateGroupErrorMessage(err);
                        deferred.reject(err);
                    }
                });

                return deferred;
            };

            // Initialize the group table
            pageGroupAdmin.initGroupList = function() {
                var groupList = UtilGrid.getTable("groups");

                var self = this;
                //on(groupList, "deleteRow", function(invocation){
                on(groupList, "onDeleteItems", function(msg) {
                    console.debug("delete group:");
                    console.debug(msg.item);
                    var groupToDelete = msg.items[0];
                    var def = self.deleteGroup(groupToDelete, function() {
                        UtilGrid.addTableDataRow("groups", groupToDelete);
                    });
                    return def;
                });

                aspect.after(groupList, "onSelectedRowsChanged", function() {
                    console.debug("group selected");
                    // Update the displayed data
                    var selectedGroups = UtilGrid.getSelectedData("groups");
                    if (selectedGroups.length == 1 && selectedGroups[0] != undefined) {
                        // one group selected, load and display it's group data
                        pageGroupAdmin.updateAllInputData(selectedGroups[0]);
                        pageGroupAdmin.showPermissionLists();

                    } else {
                        // zero or multiple groups selected
                        pageGroupAdmin.resetAllInputData();
                        pageGroupAdmin.hidePermissionLists();
                    }
                });
            };

            // Store the group 'group'
            pageGroupAdmin.storeGroup = function(group, refetch) {
                var deferred = new Deferred();
                SecurityService.storeGroup(group, refetch, {
                    preHook: LoadingZone.show,
                    postHook: LoadingZone.hide,
                    callback: function(group) {
                        deferred.resolve(group);
                    },
                    errorHandler: function(errMsg, err) {
                        pageGroupAdmin.displayStoreGroupErrorMessage(err);
                        deferred.reject(errMsg);
                    }
                });

                return deferred.promise;
            };


            pageGroupAdmin.updateAllInputData = function(group) {
                console.debug("update all input data");
                registry.byId("groupDataName").setValue(group.name);

                // Load the group details from the server
                var def = pageGroupAdmin.getGroupDetails(group.name);
                def.then(function(groupDetails) {
                    pageGroupAdmin.curSelectedGroupDetails = groupDetails;

                    pageGroupAdmin.setAddressPermissions(groupDetails.addressPermissions);
                    pageGroupAdmin.setObjectPermissions(groupDetails.objectPermissions);
                    pageGroupAdmin.setGroupPermissions(groupDetails.groupPermissions);
                });

                var def2 = UtilSecurity.getUsersFromGroup(group.name);
                def2.then(function(users) {
                    array.forEach(users, function(adr) {
                        adr.title = UtilAddress.createAddressTitle(adr.address);
                    });
                    UtilGrid.setTableData("usersOfGroupTable", users);
                });
            };


            pageGroupAdmin.setAddressPermissions = function(permissionList) {
                UtilGrid.setTableData("groupDataRightsAddressesList", []);

                array.forEach(permissionList, function(p) {
                    var adr = {};

                    adr.title = UtilAddress.createAddressTitle(p.address);
                    adr.uuid = p.uuid;

                    if (p.permission == "WRITE_SINGLE") {
                        adr.single = true;
                        adr.tree = false;
                        adr.subnode = false;
                    } else if (p.permission == "WRITE_TREE") {
                        adr.single = false;
                        adr.tree = true;
                        adr.subnode = false;
                    } else {
                        adr.single = false;
                        adr.tree = false;
                        adr.subnode = true;
                    }

                    UtilGrid.addTableDataRow("groupDataRightsAddressesList", adr);
                });
            };


            pageGroupAdmin.setObjectPermissions = function(permissionList) {
                UtilGrid.setTableData("groupDataRightsObjectsList", []);

                array.forEach(permissionList, function(p) {
                    var obj = {};

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
            };

            pageGroupAdmin.setGroupPermissions = function(permissionList) {
                var canCreateRoot = false;
                var isQA = false;

                array.forEach(permissionList, function(idcPermission) {
                    if (idcPermission == "CREATE_ROOT") {
                        canCreateRoot = true;
                    } else
                    if (idcPermission == "QUALITY_ASSURANCE") {
                        isQA = true;
                    }
                });

                registry.byId("userDataCreate").setValue(canCreateRoot);
                registry.byId("userDataQS").setValue(isQA);
            };


            pageGroupAdmin.resetAllInputData = function() {
                registry.byId("groupDataName").setValue("");

                UtilGrid.setTableData("groupDataRightsObjectsList", []);
                UtilGrid.setTableData("groupDataRightsAddressesList", []);

                registry.byId("userDataCreate").setValue(false);
                registry.byId("userDataQS").setValue(false);
            };

            pageGroupAdmin.hidePermissionLists = function() {
                dom.byId('permissionCheckboxContainer').style.display = "none";
                dom.byId('permissionListObjects').style.display = "none";
                dom.byId('permissionListAddresses').style.display = "none";
            };

            pageGroupAdmin.showPermissionLists = function() {
                dom.byId('permissionCheckboxContainer').style.display = "block";
                dom.byId('permissionListObjects').style.display = "block";
                dom.byId('permissionListAddresses').style.display = "block";

                registry.byId("groupDataObjects").resize();
                registry.byId("groupDataAddresses").resize();
            };

            pageGroupAdmin.getGroupDetails = function(groupName) {

                var deferred = new Deferred();

                SecurityService.getGroupDetails(groupName, {
                    preHook: LoadingZone.show,
                    postHook: LoadingZone.hide,
                    callback: function(groupDetails) {
                        deferred.resolve(groupDetails);
                    },
                    errorHandler: function(errMsg, err) {
                        pageGroupAdmin.displayErrorMessage(err);
                        console.debug(errMsg);
                        deferred.reject(err);
                    }
                });

                return deferred;
            };

            pageGroupAdmin.getGroupDetailsById = function(groupIds) {

                var def = UtilSecurity.getAllGroups(false);

                def.then(function(groupList) {
                    if (groupIds === null) {
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

                return def.then(pageGroupAdmin.getGroupDetails);
            };

            pageGroupAdmin.convertObjectPermissionToTreeNode = function(p) {
                return {
                    id: p.object.uuid,
                    title: p.object.title,
                    isFolder: p.object.hasChildren && (p.permission == "WRITE_TREE" || p.permission == "WRITE_SUBNODE"),
                    nodeDocType: p.object.nodeDocType,
                    //data-dojo-type: "ingrid:TreeNode",
                    nodeAppType: "O"
                };
            };

            pageGroupAdmin.convertAddressPermissionToTreeNode = function(p) {
                return {
                    id: p.address.uuid,
                    title: UtilAddress.createAddressTitle(p.address),
                    isFolder: p.address.hasChildren && (p.permission == "WRITE_TREE" || p.permission == "WRITE_SUBNODE"),
                    nodeDocType: p.address.nodeDocType,
                    //data-dojo-type: "ingrid:TreeNode",
                    nodeAppType: "A"
                };
            };


            // -- Error handling --
            pageGroupAdmin.displayCreateGroupErrorMessage = function(err) {
                if (err && err.message) {
                    if (err.message.indexOf("ENTITY_ALREADY_EXISTS") != -1) {
                        //          dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.noPermissionError' />", dialog.WARNING);
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.groups.groupAlreadyExistsError' />", dialog.WARNING);

                    } else {
                        dialog.show("<fmt:message key='general.error' />", string.substituteParams("<fmt:message key='dialog.generalError' />", err.message), dialog.WARNING, null, 350, 350);
                    }

                } else {
                    // Show general error message if we can't determine what went wrong
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.undefinedError' />", dialog.WARNING);
                }
            };

            pageGroupAdmin.displayStoreGroupErrorMessage = function(err) {
                if (err && err.message) {
                    if (err.message.indexOf("SINGLE_BELOW_TREE_OBJECT_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.object.singleBelowTreeError' />", [err.invalidObject.title, err.rootObject.title]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("TREE_BELOW_TREE_OBJECT_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.object.treeBelowTreeError' />", [err.invalidObject.title, err.rootObject.title]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("SINGLE_BELOW_TREE_ADDRESS_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.address.singleBelowTreeError' />", [UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("TREE_BELOW_TREE_ADDRESS_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.address.treeBelowTreeError' />", [UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("USER_EDITING_OBJECT_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.object.userEditingError' />", [err.invalidObject.title, UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("USER_RESPONSIBLE_FOR_OBJECT_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.object.userResponsibleError' />", [err.invalidObject.title, UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("USER_EDITING_ADDRESS_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.address.userEditingError' />", [UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("USER_RESPONSIBLE_FOR_ADDRESS_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.address.userResponsibleError' />", [UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("NO_RIGHT_TO_REMOVE_OBJECT_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.object.permissionError' />", [err.rootObject.title]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("NO_RIGHT_TO_REMOVE_ADDRESS_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.address.permissionError' />", [UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("NO_RIGHT_TO_REMOVE_USER_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.groups.removePermissionError' />", dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("NO_RIGHT_TO_ADD_USER_PERMISSION") != -1) {
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.groups.addPermissionError' />", dialog.WARNING, null, 350, 200);

                    } else {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.generalError' />", [err.message]), dialog.WARNING, null, 350, 350);
                    }

                } else {
                    // Show general error message if we can't determine what went wrong
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.undefinedError' />", dialog.WARNING);
                }
            };

            pageGroupAdmin.displayDeleteGroupError = function(err) {
                if (err && err.message) {
                    if (err.message.indexOf("GROUP_HAS_USERS") != -1) {
                        var userList = "";
                        array.forEach(err.addresses, function(adr) {
                            userList += UtilAddress.createAddressTitle(adr) + "\n";
                        });
                        userList = dojo.string.trim(userList);
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.groupHasUsersError' />", [userList]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("USER_EDITING_OBJECT_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.object.userEditingError' />", [err.invalidObject.title, UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("USER_RESPONSIBLE_FOR_OBJECT_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.object.userResponsibleError' />", [err.invalidObject.title, UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("USER_EDITING_ADDRESS_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.address.userEditingError' />", [UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);

                    } else if (err.message.indexOf("USER_RESPONSIBLE_FOR_ADDRESS_PERMISSION_MISSING") != -1) {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.admin.groups.address.userResponsibleError' />", [UtilAddress.createAddressTitle(err.invalidAddress), UtilAddress.createAddressTitle(err.rootAddress)]), dialog.WARNING, null, 350, 200);

                    } else {
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.generalError' />", [err.message]), dialog.WARNING, null, 350, 350);
                    }

                } else {
                    // Show general error message if we can't determine what went wrong
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.undefinedError' />", dialog.WARNING);
                }
            };

        });
    </script>
</head>
<body>
    <!-- CONTENT START -->
    <div class="contentBlockWhite top">
        <div id="winNavi" style="width: 100%; padding-bottom: 35px;">
            <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=user-administration-2#user-administration-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
        </div>
        <div id="groupAdmin" class="content">
            <div data-dojo-type="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="false" class="spaceBelow" style="height:250px; clear:both;" layoutAlign="client" design="headline">
                <!-- LEFT HAND SIDE CONTENT BLOCK 1 START -->
                <div data-dojo-type="dijit/layout/ContentPane" region="leading" class="tableContainer" style="width: 270px; padding:5px;">
                    <div id="groups" autoHeight="9" forceGridHeight="false">
                    </div>
                </div>
                <!-- LEFT HAND SIDE CONTENT BLOCK 1 END --><!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
                <div data-dojo-type="dijit/layout/ContentPane" region="center" id="groupData_" class="inputContainer innerPadding">
                    <span class="label">
                        <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8018)">
                            <fmt:message key="dialog.admin.groups.data" />
                        </label>
                    </span>
                    <div class="inputContainer field grey">
                        <span class="label">
                            <label for="groupDataName" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8019)">
                                <fmt:message key="dialog.admin.groups.name" />
                            </label>
                        </span><span class="input"><input type="text" maxlength="50" id="groupDataName" style="width:100%;" data-dojo-type="dijit/form/ValidationTextBox" /></span>
                        <div class="spacerField">
                        </div>
                    </div>
                    <div class="inputContainer">
                        <span class="button"><span style="float: right;">
                                <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.groups.save" />" onclick="javascript:pageGroupAdmin.saveGroup();">
                                    <fmt:message key="dialog.admin.groups.save" />
                                </button>
                            </span><span style="float: right;">
                                <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.groups.createGroup" />" onclick="javascript:pageGroupAdmin.newGroup();">
                                    <fmt:message key="dialog.admin.groups.createGroup" />
                                </button>
                            </span><span id="adminGroupLoadingZone" style="float: left; margin-top: 1px; z-index: 100; visibility: hidden"><img src="img/ladekreis.gif" /></span></span>
                    </div>
                    <div id="permissionCheckboxContainer" class="inputContainer grey field checkboxContainer" style="display: none;">
                        <span class="input"><input type="checkbox" id="userDataCreate" data-dojo-type="dijit/form/CheckBox" />
                            <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8020)">
                                <fmt:message key="dialog.admin.groups.createRoot" />
                            </label>
                        </span><span class="input"><input type="checkbox" id="userDataQS" data-dojo-type="dijit/form/CheckBox" />
                            <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8021)">
                                <fmt:message key="dialog.admin.groups.qa" />
                            </label>
                        </span>
                    </div>
                </div><!-- RIGHT HAND SIDE CONTENT BLOCK 1 END --><!-- CONTENT BLOCK 2 START --><!-- SPLIT CONTAINER START -->
            </div>
            <div id="groupAdministrationTab" data-dojo-type="dijit/layout/TabContainer" doLayout="false" style="width:100%;margin-top:40px;" selectedChild="permissionListObjects">
                <!-- MAIN TAB 1 START -->
                <div id="permissionListObjects" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.admin.groups.objectPermissions" />" class="tab" style="overflow:hidden;">
                    <div data-dojo-type="dijit.layout.BorderContainer" gutters="true" liveSplitters="false" id="groupDataObjects" layoutAlign="client" design="headline" style="height:250px;">
                        <!-- LEFT HAND SIDE CONTENT BLOCK 2 START -->
                        <div data-dojo-type="dijit/layout/ContentPane" region="leading" class="" splitter="true" style="width: 300px;">
                            <!-- tree components -->
                            <div id="treeObjects">
                            </div>
                        </div>
                        <!-- LEFT HAND SIDE CONTENT BLOCK 2 END --><!-- RIGHT HAND SIDE CONTENT BLOCK 2 START -->
                        <div data-dojo-type="dijit/layout/ContentPane" region="center" splitter="false" class="field">
                            <div class="selectEntryBtn">
                                <button data-dojo-type="dijit/form/Button" id="addObjectButton" onclick="pageGroupAdmin.addObject();">
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
                <div id="permissionListAddresses" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.admin.groups.addressPermissions" />" class="tab" style="overflow:hidden;">
                    <div data-dojo-type="dijit.layout.BorderContainer" design="headline" gutters="true" liveSplitters="false" id="groupDataAddresses" layoutAlign="client" style="height:250px;">
                        <!-- LEFT HAND SIDE CONTENT BLOCK 3 START -->
                        <div data-dojo-type="dijit/layout/ContentPane" region="leading" splitter="true" style="width: 300px;">
                            <!-- tree components -->
                            <div id="treeAddresses">
                            </div>
                        </div>
                        <!-- LEFT HAND SIDE CONTENT BLOCK 3 END -->
                        <!-- RIGHT HAND SIDE CONTENT BLOCK 3 START -->
                        <div data-dojo-type="dijit/layout/ContentPane" region="center" splitter="false" class="inputContainer field">
                            <div class="selectEntryBtn">
                                <button data-dojo-type="dijit/form/Button" id="addAddressButton" onclick="pageGroupAdmin.addAddress();">
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
                <div id="belongingUsers" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.admin.groups.belongingUsers" />" class="tab grey" style="overflow:hidden;">
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
