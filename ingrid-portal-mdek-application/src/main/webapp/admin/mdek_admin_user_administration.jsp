<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
    var pageUserAdmin = _container_;
    require([
        "dojo/on",
        "ingrid/layoutCreator",
        "ingrid/utils/Grid",
        "ingrid/utils/Address",
        "ingrid/utils/Security",
        "ingrid/utils/LoadingZone",
        "ingrid/utils/List",
        "ingrid/utils/Tree",
        "ingrid/utils/String",
        "ingrid/tree/MetadataTree",
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/query",
        "dojo/dom-class",
        "dojo/Deferred",
        "dojo/promise/all",
        "dojo/string",
        "dijit/registry",
        "ingrid/dialog",
        "ingrid/init",
        "ingrid/hierarchy/requiredChecks"
    ], function(on, layoutCreator, UtilGrid, UtilAddress, UtilSecurity, LoadingZone, UtilList, UtilTree, UtilString, MetadataTree, array, lang, query, domClass, Deferred, all, string, registry, dialog, init, checks) {

            var currentSelectedAddressId = null;
            var currentSelectedUser = null;
            // store cat admins adminstrators group Id for later user, see setTreeRoot() method
            var administratorGroupId = null;
            var administratorId = null;

            var MAX_NUM_DATASETS = 100;

            on(_container_, "Load", function() {
                try {
                    registry.byId("borderContainerMdekAdminUserAdmin").resize();
                    createGrids();

                    new MetadataTree({
                        treeType: "Users",
                        showRoot: false,
                        onClick: treeNodeSelected
                    }, "treeUser");

                    createGroupLists();

                    setUserAddressDataFieldsDisabled(true);

                    var handle = registry.byId("userManagementTab").watch("selectedChildWidget", function() {
                        registry.byId("responsibleUserInObjects").reinitLastColumn();
                        registry.byId("responsibleUserInAddresses").reinitLastColumn();
                        handle.unwatch();
                    });

                    // validation rule has been put in html since then the input is validated
                    // more intelligently after text has been entered
                    //Validation.addEmailCheck("userDataAddressEmailUser", false);
                    //Validation.addEmailCheck("userDataAddressEmailPointOfContact", false);

                    addRequiredBehaviour();
                } catch (error) {
                    console.error(error);
                }

            });


            function createGrids() {
                var tableStructure = [{
                    field: 'name',
                    name: "<fmt:message key='dialog.admin.users.groupName' />",
                    width: 350 + 'px'
                }];
                layoutCreator.createDataGrid("availableGroupsList", null, tableStructure, null);
                layoutCreator.createDataGrid("groupsList", null, tableStructure, null);
                // responsible tables
                var responsibleUserInObjectsStructure = [{
                    field: 'icon',
                    name: '&nbsp;',
                    width: '30px'
                }, {
                    field: 'uuid',
                    name: 'ID',
                    width: '300px'
                }, {
                    field: 'linkLabel',
                    name: "<fmt:message key='dialog.admin.catalog.management.deleteAddress.objectName' />",
                    width: '300px'
                }];

                layoutCreator.createDataGrid("responsibleUserInObjects", null, responsibleUserInObjectsStructure, null);
                layoutCreator.createDataGrid("responsibleUserInAddresses", null, responsibleUserInObjectsStructure, null);
            }


            function createGroupLists() {
                var storeProps = {
                    data: {
                        identifier: 'id',
                        label: "<fmt:message key='dialog.admin.groupName' />"
                    }
                };
                layoutCreator.createSelectBox("userDataGroup",
                    null,
                    storeProps,
                    null //function(){return UtilSecurity.getAllGroups(includeCatAdminGroup);}
                );
                initializeGroupLists(true);
            }

            function initializeGroupLists(includeCatAdminGroup) {
                var def = UtilSecurity.getAllGroups(includeCatAdminGroup)
                    .then(function(groupList) {
                        var listAvailableGroups = [];
                        var listUserGroups = [];
                        var groupIdList = currentSelectedUser ? currentSelectedUser.groupIds : [];
                        array.forEach(groupList, function(item) {
                            // Due to a dojo bug, you cannot use numbers as an id
                            // it won't work when selecting an item! (06.09.2010)
                            //list.push({name:item.name, id:item.id+""});
                            if (array.indexOf(groupIdList, item.id) != -1) {
                                listUserGroups.push({
                                    "name": item.name,
                                    "Id": item.id
                                });
                            } else if (currentSelectedUser && currentSelectedUser.userId != administratorId) {
                                listAvailableGroups.push({
                                    "name": item.name,
                                    "Id": item.id
                                });
                            }
                        });

                        UtilGrid.setTableData("availableGroupsList", listAvailableGroups);
                        UtilGrid.setTableData("groupsList", listUserGroups);
                    });

                return def;
            }


            // 'Add selected groups' Button onClick function.
            //
            // This function moves the selected groups from the selection list (left) to the result list (right)
            function addSelected() {
                var selectedGroups = UtilGrid.getSelectedData("availableGroupsList");
                if (selectedGroups) {
                    array.forEach(selectedGroups, function(item) {
                        UtilGrid.removeTableDataRow("availableGroupsList", UtilGrid.getSelectedRowIndexes("availableGroupsList"));
                        UtilGrid.addTableDataRow("groupsList", item);
                    });
                }
            }


            // 'Add all groups' Button onClick function.
            //
            // This function moves all groups from the selection list (left) to the result list (right)
            function addAll() {
                var groups = UtilGrid.getTableData("availableGroupsList");
                if (groups) {
                    for (var i in groups) {
                        UtilGrid.addTableDataRow("groupsList", groups[i]);
                    }
                    UtilGrid.setTableData("availableGroupsList", []);
                }
            }


            // 'Remove selected groups' Button onClick function.
            //
            // This function moves the selected groups from the result list (right) to the selection list (left)
            function removeSelected() {
                var selectedGroups = UtilGrid.getSelectedData("groupsList");
                if (selectedGroups) {
                    array.forEach(selectedGroups, function(item) {
                        if (item.name != "administrators") {
                            UtilGrid.removeTableDataRow("groupsList", UtilGrid.getSelectedRowIndexes("groupsList"));
                            UtilGrid.addTableDataRow("availableGroupsList", item);
                        } else {
                            console.debug("Cannot remove group 'administrators' from user.");
                        }
                    });
                }
            }


            // 'Remove all groups' Button onClick function.
            //
            // This function moves all groups from the result list (right) to the selection list (left)
            function removeAll() {
                var groups = UtilGrid.getTableData("groupsList");
                if (groups) {
                    var emptyGroups = [];
                    for (var i in groups) {
                        if (groups[i].name != "administrators") {
                            UtilGrid.addTableDataRow("availableGroupsList", groups[i]);
                        } else {
                            console.debug("Cannot remove group 'administrators' from user.");
                            // add administrator to group, so that it will not be removed in the following swipe!
                            emptyGroups.push(groups[i]);
                        }
                    }
                    UtilGrid.setTableData("groupsList", emptyGroups);
                }
            }

            // 'Delete user' button function
            // Get the selected user from the tree and delete it
            function deleteUser() {
                var tree = registry.byId("treeUser");
                var user = tree.selectedNode.item;

                // if it's a new node
                if (user !== null && user.id == "newUserNode") {
                    tree.model.store.remove(user.id);
                    resetInputFields();
                    setUserAddressDataFieldsDisabled(true);
                    registry.byId("bImportUser").set("disabled", true);
                    registry.byId("bDeleteUser").set("disabled", true);
                    return;
                }

                // Can't delete the cat admin. This is also checked in the backend.
                if (user === null || user.role == 1 || user.isFolder) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.canNotDeleteError' />", dialog.WARNING);
                    console.debug("Can't delete.");
                    return;
                }

                var deferred = new Deferred();

                var displayText = string.substitute("<fmt:message key='dialog.admin.users.confirmDelete' />", [user.title]);
                dialog.show("<fmt:message key='dialog.admin.users.deleteUser' />", displayText, dialog.INFO, [{
                    caption: "<fmt:message key='general.no' />",
                    action: function() {
                        deferred.reject();
                    }
                }, {
                    caption: "<fmt:message key='general.ok' />",
                    action: function() {
                        deferred.resolve();
                    }
                }]);

                deferred.then(function() {
                    SecurityService.deleteUser(user.userId, user.addressUuid, {
                        preHook: LoadingZone.show,
                        postHook: LoadingZone.hide,
                        callback: function() {
                            tree.model.store.remove(user.id);
                            resetInputFields();
                            setUserAddressDataFieldsDisabled(true);
                        },
                        errorHandler: function(errMsg, err) {
                            LoadingZone.hide();
                            if (errMsg.indexOf("USER_HAS_WRONG_ROLE") != -1 || errMsg.indexOf("USER_HIERARCHY_WRONG") != -1) {
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.noDeletePermissionError' />", dialog.WARNING);
                            } else {
                                displayErrorMessage(err);
                            }

                            console.error(errMsg);
                        }
                    });
                }, function() {
                    console.debug("User delete Canceled!");
                });
            }

            // 'Import portal user' button function
            function importPortalUser() {
                var selectedUser = registry.byId("treeUser").selectedNode;
                console.debug("selectedUser");
                console.debug(selectedUser);
                console.debug("currentUser");
                console.debug(UtilSecurity.currentUser);

                if (selectedUser === null) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.noParentSelectedError' />", dialog.WARNING);
                    console.debug("No node selected.");
                    return;
                }

                // In case a mdAdmin wants to create a new user, we have to check if the mdAdmin is selected in the tree
                if (UtilSecurity.currentUser.role == 2) {
                    if (selectedUser.item.userId != UtilSecurity.currentUser.id) {
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.adminSelectedInvalidUserError' />", dialog.WARNING);
                        console.debug("Can't create users here. Select the correct mdAdmin.");
                        return;
                    }
                }

                // In case a user with role mdAutor is selected, do nothing and return
                if (selectedUser.item.role < 1 || selectedUser.item.role > 2) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.authorSelectedAsParentError' />", dialog.WARNING);
                    console.debug("Select a user with role catAdmin or mdAdmin.");
                    return;
                }

                // In case a newUserNode already exists, return
                if (selectedUser.item.id == "newUserNode") {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.newNodeSelectedAsParentError' />", dialog.WARNING);
                    console.debug("Please save the current user before creating a new one.");
                    return;
                }


                var deferred = new Deferred();
                dialog.showPage("<fmt:message key='dialog.admin.users.importUser' />", "admin/dialogs/mdek_admin_import_user_dialog.jsp", 370, 240, true, {
                    resultHandler: deferred
                });

                deferred.then(function(portalUser) {
                    selectedUser.expand()
                    .then(function() {
                        console.debug("create new user: ");
                        var newNode = createNewUserNode(selectedUser.item, portalUser);
                        UtilTree.addNode( "treeUser", selectedUser, newNode )
                        .then(function() {
                            console.debug("after new item");
                            resetInputFields();
                            setUserAddressDataFieldsDisabled(false);
                            UtilTree.selectNode("treeUser", "newUserNode");
                            treeNodeSelected(UtilTree.getNodeById("treeUser", "newUserNode").item);
                        });

                    });
                });
            }

            // 'Save user' button function
            function saveUser() {
                var selectedUser = registry.byId("treeUser").selectedNode;
                var login = registry.byId("userDataLogin").getValue();
                var oldUserLogin = selectedUser.item.portalLogin + "";
                selectedUser.item.portalLogin = login;

                if (lang.trim(login).length === 0) {
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
                var groups, groudIds = null;
                user.address = getAddressDataFromFields();

                if (selectedUser.item.id == "newUserNode") {
                    // DO NOT SET addressUuid, will be created in backend !
                    //user.addressUuid = currentSelectedAddressId+""; // currentSelectedAddressId is undefined

                    groups = UtilGrid.getTableData("groupsList");

                    groudIds = UtilList.map(groups, function(group) {
                        return group.Id;
                    });

                    user.groupIds = groudIds;
                    user.roleName = selectedUser.item.roleName;
                    user.role = selectedUser.item.role;
                    user.parentUserId = selectedUser.item.parentUserId + "";
                    console.debug(user);

                    SecurityService.createUser(user, login, true, {
                        preHook: LoadingZone.show,
                        postHook: LoadingZone.hide,
                        callback: function(newUser) {
                            var tree = registry.byId("treeUser");
                            updateTreeNode(selectedUser, newUser);
                            UtilTree.selectNode("treeUser", "TreeNode_" + newUser.id);
                            updateInputElements(tree.selectedNode.item);

                            dialog.show("<fmt:message key='general.info' />", "<fmt:message key='dialog.admin.users.createSuccess' />", dialog.INFO);
                        },
                        errorHandler: function(errMsg, err) {
                            LoadingZone.hide();
                            if (errMsg.indexOf("USER_HAS_WRONG_ROLE") != -1) {
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.createPermissionError' />", dialog.WARNING);
                            } else
                            if (errMsg.indexOf("ENTITY_ALREADY_EXISTS") != -1) {
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.addressCollisionError' />", dialog.WARNING);

                            } else {
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.createError' />", dialog.WARNING);
                            }
                            console.error(errMsg);
                        }
                    });

                } else {
                    user.id = currentSelectedUser.userId + "";
                    user.addressUuid = currentSelectedAddressId + "";
                    groups = UtilGrid.getTableData("groupsList");
                    groudIds = UtilList.map(groups, function(group) {
                        return group.Id;
                    });

                    user.groupIds = groudIds;
                    user.role = currentSelectedUser.role;
                    user.parentUserId = currentSelectedUser.parentUserId == undefined ? undefined : currentSelectedUser.parentUserId + "";

                    console.log("storeUser:", user);
                    SecurityService.storeUser(oldUserLogin, user, login, true, {
                        preHook: LoadingZone.show,
                        postHook: LoadingZone.hide,
                        callback: function(newUser) {
                            // if cat-admin was changed then user must be logged out
                            if (user.role == 1 && (oldUserLogin != login)) {
                                dialog.show("<fmt:message key='general.info' />", "<fmt:message key='dialog.admin.users.updateCatAdmin' />", dialog.WARNING);
                            } else {
                                updateTreeNode(selectedUser, newUser);
                                dialog.show("<fmt:message key='general.info' />", "<fmt:message key='dialog.admin.users.updateSuccess' />", dialog.INFO);
                            }

                        },
                        errorHandler: function(errMsg, err) {
                            if (errMsg.indexOf("USER_HAS_WRONG_ROLE") != -1 || errMsg.indexOf("USER_HIERARCHY_WRONG") != -1) {
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.updatePermissionError' />", dialog.WARNING);

                            } else
                            if (errMsg.indexOf("ENTITY_ALREADY_EXISTS") != -1) {
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.addressCollisionError' />", dialog.WARNING);
                            } else {
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.updateError' />", dialog.WARNING);
                            }
                            console.error(errMsg);
                            selectedUser.item.portalLogin = oldUserLogin;
                            treeNodeSelected(currentSelectedUser);
                        }
                    });
                }
            }

            function updateTreeNode(userNode, newUser) {
                var user = userNode.item;
                if (user.id == "newUserNode") {
                    var parent = userNode.getParent();
                    var tree = registry.byId("treeUser");

                    // A new node is a leaf node. It's safe to destroy it
                    tree.model.store.remove("newUserNode");
                    UtilTree.addNode("treeUser", parent, convertUserToTreeNode(newUser));

                } else {
                    var userName = UtilAddress.createAddressTitle(newUser.address);
                    userNode.set('label', userName);
                    user.addressUuid = newUser.addressUuid + "";
                    user.groupIds = newUser.groupIds;
                    user.title = userName;
                    if (newUser.userPermissions !== null) {
                        array.forEach(newUser.userPermissions, function(p) {
                            if (p == ("CREATE_ROOT")) {
                                user.createRoot = true;
                            }
                        });
                    }

                    if (user.role == 1) {
                        // The catAdmin address was changed. This can only be done by the catAdmin himself
                        // -> Update the current user data
                        init.initCurrentUser();//.then(init.initPageHeader);
                    }
                }

                updateInputElements(user);
            }

            // Check all input fields for their validity
            function isValidUser() {
                resetRequiredElements();

                var valid = true;

                var elements = query(".dijitTextBox", "userManagementTab");
                array.forEach(elements, function(element) {
                    if (!dijit.getEnclosingWidget(element).isValid()) {
                        checks.setErrorLabel(element.id);
                        valid = false;
                    }
                });

                return valid;
            }

            // Resolves the current role id from the input string
            /*function getRoleId(roleName) {
                if (roleName == "<fmt:message key='security.role.catalogAdmin' />") {
                    return 1;

                } else if (roleName == "<fmt:message key='security.role.metadataAdmin' />") {
                    return 2;

                } else if (roleName == "<fmt:message key='security.role.metadataAuthor' />") {
                    return 3;
                }
            }*/

            // Resolves the role name from the input id
            function getRoleName(roleId) {
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
            function resetRequiredElements() {
                query(".important", "userManagementTab").removeClass("important");
            }

            function addRequiredBehaviour() {
                on(registry.byId("userDataAddressSurname"), "onChange", checkFields);
                on(registry.byId("userDataAddressForename"), "onChange", checkFields);
            }

            // 1) if a forename or surname has text and the institution is empty -> the other field also is required
            // 2) if only institution has text -> only this field is required 
            // 3) all field are required otherwise
            function checkFields() {
                var surnameEmpty = lang.trim(registry.byId("userDataAddressSurname").value) === "";
                var forenameEmpty = lang.trim(registry.byId("userDataAddressForename").value) === "";

                if (!surnameEmpty || !forenameEmpty) {
                    domClass.add("surnameInput", "required");
                    registry.byId("userDataAddressSurname").required = true;
                    domClass.add("forenameInput", "required");
                    registry.byId("userDataAddressForename").required = true;
                } else {
                    domClass.remove("surnameInput", "required");
                    registry.byId("userDataAddressSurname").required = false;
                    domClass.remove("forenameInput", "required");
                    registry.byId("userDataAddressForename").required = false;
                }
                // make changes immediately
                revalidate();
            }

            function revalidate() {
                registry.byId("userDataAddressSurname").validate();
                registry.byId("userDataAddressForename").validate();
                registry.byId("userDataAddressInstitution").validate();
            }

            // Function that is called when a treeNode is clicked
            function treeNodeSelected(item) {
                // Check if a 'newUserNode' exists. If it does and the user clicked another node, delete the newUserNode.
                var newUserNode = UtilTree.getNodeById("treeUser", "newUserNode");
                if (newUserNode !== null && item.id != "newUserNode") {
                    registry.byId("treeUser").model.store.remove("newUserNode");
                }

                updateInputElements(item);

                // activate save button
                registry.byId("btnSaveUser").setDisabled(false);
            }

            // Updates the input elements with the data from 'treeNode'
            function updateInputElements(treeNode) {
                LoadingZone.show();
                resetRequiredElements();
                setUserAddressDataFieldsDisabled(false);

                currentSelectedUser = treeNode;
                registry.byId("bImportUser").set("disabled", false);
                registry.byId("bDeleteUser").set("disabled", false);
                var def;
                if (treeNode.role == 1) {
                    // Standards for catAdmin
                    def = initializeGroupLists(true);
                    UtilGrid.updateOption("availableGroupsList", "editable", false);
                    UtilGrid.updateOption("groupsList", "editable", false);
                } else {
                    if (treeNode.role == 3) {
                        registry.byId("bImportUser").set("disabled", true);
                    }
                    def = initializeGroupLists(false);
                    UtilGrid.updateOption("availableGroupsList", "editable", true);
                    UtilGrid.updateOption("groupsList", "editable", true);

                }

                def.then(function() {
                    if (treeNode.addressUuid) {
                        getAddressData(treeNode.addressUuid)
                            .then(function(addressData) {
                                setUserAddressDataFields(addressData);
                            });
                    } else {
                        setUserAddressDataFields(null);
                    }

                    registry.byId("userDataLogin").setValue(treeNode.portalLogin);
                    registry.byId("userDataRole").setValue(treeNode.roleName);
                    console.debug("set to groupid: " + treeNode.groupId);
                    //registry.byId("userDataGroup").set("value",treeNode.groupId+"");
                    currentSelectedAddressId = treeNode.addressUuid;

                    // new users do not have an ID yet!
                    if (currentSelectedAddressId) {
                        LoadingZone.show();
                        // update responsible User data
                        var defRespObj = getObjectsWithResponsibleUser(currentSelectedAddressId);
                        defRespObj.then(function(objects) {
                            UtilList.addIcons(objects);
                            UtilList.addObjectLinkLabels(objects);
                            UtilGrid.setTableData("responsibleUserInObjects", objects);
                        });
                        var defRespAddr = getAddressesWithResponsibleUser(currentSelectedAddressId);
                        defRespAddr.then(function(addresses) {
                            UtilList.addIcons(addresses);
                            UtilAddress.addAddressTitles(addresses);
                            UtilList.addAddressLinkLabels(addresses);
                            UtilGrid.setTableData("responsibleUserInAddresses", addresses);
                        });

                        all([defRespObj, defRespAddr]).then(function() { LoadingZone.hide(); });
                    }
                });

            }

            function resetInputFields() {
                registry.byId("userDataLogin").set("value", "");
                registry.byId("userDataRole").set("value", "");

                setUserAddressDataFields(null);

                UtilGrid.setTableData("groupsList", []);
                UtilGrid.setTableData("availableGroupsList", []);
                currentSelectedAddressId = null;
                currentSelectedUser = null;
            }

            function setUserAddressDataFields(addressData) {
                if (addressData) {
                    registry.byId("userDataAddressSurname").setValue(addressData.name);
                    registry.byId("userDataAddressForename").setValue(addressData.givenName);
                    var commValue = UtilAddress.getAddressCommunicationValue(addressData, ["E-Mail", "e-mail"]);
                    registry.byId("userDataAddressEmailUser").setValue(commValue);
                    commValue = UtilAddress.getAddressCommunicationValue(addressData, ["emailPointOfContact"]);
                    registry.byId("userDataAddressEmailPointOfContact").setValue(commValue);
                    registry.byId("userDataAddressInstitution").setValue(addressData.organisation);
                    registry.byId("userDataAddressStreet").setValue(addressData.street);
                    registry.byId("userDataAddressPostCode").setValue(addressData.postalCode);
                    registry.byId("userDataAddressCity").setValue(addressData.city);
                    commValue = UtilAddress.getAddressCommunicationValue(addressData, ["Telefon", "telephone"]);
                    registry.byId("userDataAddressPhone").setValue(commValue);
                } else {
                    // reset fields
                    registry.byId("userDataAddressSurname").set("value", "");
                    registry.byId("userDataAddressForename").set("value", "");
                    registry.byId("userDataAddressEmailUser").set("value", "");
                    registry.byId("userDataAddressEmailPointOfContact").set("value", "");
                    registry.byId("userDataAddressInstitution").set("value", "");
                    registry.byId("userDataAddressStreet").set("value", "");
                    registry.byId("userDataAddressPostCode").set("value", "");
                    registry.byId("userDataAddressCity").set("value", "");
                    registry.byId("userDataAddressPhone").set("value", "");
                }
            }

            function setUserAddressDataFieldsDisabled(isDisabled) {
                if (isDisabled) {
                    resetRequiredElements();
                }

                registry.byId("userDataAddressSurname").set("disabled", isDisabled);
                registry.byId("userDataAddressForename").set("disabled", isDisabled);
                registry.byId("userDataAddressEmailUser").set("disabled", isDisabled);
                registry.byId("userDataAddressEmailPointOfContact").set("disabled", isDisabled);
                registry.byId("userDataAddressInstitution").set("disabled", isDisabled);
                registry.byId("userDataAddressStreet").set("disabled", isDisabled);
                registry.byId("userDataAddressPostCode").set("disabled", isDisabled);
                registry.byId("userDataAddressCity").set("disabled", isDisabled);
                registry.byId("userDataAddressPhone").set("disabled", isDisabled);
            }

            function getAddressDataFromFields() {
                var addr = {};
                addr.name = registry.byId("userDataAddressSurname").getValue();
                addr.givenName = registry.byId("userDataAddressForename").getValue();
                addr.organisation = registry.byId("userDataAddressInstitution").getValue();
                addr.street = registry.byId("userDataAddressStreet").getValue();
                addr.postalCode = registry.byId("userDataAddressPostCode").getValue();
                addr.city = registry.byId("userDataAddressCity").getValue();

                var commList = [],
                    comm;
                if (registry.byId("userDataAddressEmailUser").getValue()) {
                    comm = {};
                    comm.value = registry.byId("userDataAddressEmailUser").getValue();
                    comm.medium = "E-Mail";
                    comm.description = null;
                    commList.push(comm);
                }
                if (registry.byId("userDataAddressEmailPointOfContact").getValue()) {
                    comm = {};
                    comm.value = registry.byId("userDataAddressEmailPointOfContact").getValue();
                    comm.medium = "emailPointOfContact";
                    comm.description = null;
                    commList.push(comm);
                }
                if (registry.byId("userDataAddressPhone").getValue()) {
                    comm = {};
                    comm.value = registry.byId("userDataAddressPhone").getValue();
                    comm.medium = "Telefon";
                    comm.description = null;
                    commList.push(comm);
                }

                if (commList.length > 0) {
                    addr.communication = commList;
                }

                return addr;
            }

            function searchForPortalUser() {
                var selectedUser = registry.byId("treeUser").selectedNode;

                if (UtilSecurity.currentUser.role == 1 && selectedUser == null) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.users.noParentSelectedError' />", dialog.WARNING);
                    console.debug("No node selected.");
                    return;
                }

                var deferred = new Deferred();
                dialog.showPage("<fmt:message key='dialog.admin.users.importUser' />", "admin/dialogs/mdek_admin_import_user_dialog.jsp", 370, 240, true, {
                    resultHandler: deferred
                });

                deferred.then(function(portalUser) {
                    console.debug("user chosen: " + portalUser);
                    registry.byId("userDataLogin").setValue(portalUser);
                });
            }

            function getAddressData(addrUuid) {
                var uuidToFetch = addrUuid;
                if (addrUuid instanceof Array) {
                    uuidToFetch = addrUuid;
                }

                var deferred = new Deferred();

                AddressService.getAddressData(uuidToFetch, null, {
                    callback: function(adr) {
                        console.log("got addressData: ", adr);
                        deferred.resolve(adr);
                    },
                    errorHandler: function(errMsg, err) {
                        LoadingZone.hide();
                        displayErrorMessage(err);
                        console.error(errMsg);
                        deferred.reject(err);
                    }
                });

                return deferred;
            }

            /*function convertUserListToTreeNodes(userList) {
                var treeNodes = [];

                array.forEach(userList, function(user) {
                    treeNodes.push(convertUserToTreeNode(user));
                });

                // Return a sorted list according to the users titles
                return treeNodes.sort(function(a, b) {
                    return UtilString.compareIgnoreCase(a.title, b.title);
                });
            }*/

            function convertUserToTreeNode(user) {
                var portalLogin = "?";
                var notExists = true;
                if (user.userData !== null) {
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
                    nodeDocType: UtilSecurity.getDocTypeForRole(user.role),
                    isFolder: user.hasChildren,
                    id: "TreeNode_" + user.id,
                    noPortalLogin: notExists
                };
            }

            function createNewUserNode(parentNode, login) {
                return {
                    id: "newUserNode",
                    userId: "newUserNode",
                    parentUserId: parentNode.userId,
                    //      addressUuid: user.addressUuid,
                    title: "Neuer Benutzer",
                    role: parentNode.role + 1,
                    roleName: getRoleName(parentNode.role + 1),
                    //      groupId: parentNode.groupId,
                    groupIds: UtilList.map(parentNode.groupIds, function(groupId) {
                        if (groupId != administratorGroupId) return groupId;
                    }),
                    createRoot: false,
                    portalLogin: login,
                    nodeDocType: UtilSecurity.getDocTypeForRole(parentNode.role + 1),
                    isFolder: false
                };
            }

            // Fetch objects where given adrUuid is set as the responsible user
            function getObjectsWithResponsibleUser(adrUuid) {
                var def = new Deferred();

                CatalogManagementService.getObjectsOfResponsibleUser(adrUuid, MAX_NUM_DATASETS, {
                    callback: function(objects) {
                        def.resolve(objects);
                    },
                    errorHandler: function(message, err) {
                        displayErrorMessage(err);
                        console.debug("Error: " + message);
                        def.reject(err);
                    }
                });

                return def;
            }

            // Fetch objects where given adrUuid is set as the responsible user
            function getAddressesWithResponsibleUser(adrUuid) {
                var def = new Deferred();

                CatalogManagementService.getAddressesOfResponsibleUser(adrUuid, MAX_NUM_DATASETS, {
                    callback: function(objects) {
                        def.resolve(objects);
                    },
                    errorHandler: function(message, err) {
                        displayErrorMessage(err);
                        console.debug("Error: " + message);
                        def.reject(err);
                    }
                });

                return def;
            }

            pageUserAdmin = {
                searchForPortalUser: searchForPortalUser,
                deleteUser: deleteUser,
                importPortalUser: importPortalUser,
                addSelected: addSelected,
                addAll: addAll,
                removeSelected: removeSelected,
                removeAll: removeAll,
                saveUser: saveUser
            };

        });
    </script>
</head>
<body>
    <!-- CONTENT START -->
    <!--<div data-dojo-type="dijit/layout/ContentPane" layoutAlign="client">-->
    <div id="contentSection" class="content contentBlockWhite top">
        <div data-dojo-type="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="true"
            id="borderContainerMdekAdminUserAdmin" style="height: 680px;">
            <div data-dojo-type="dijit.layout.BorderContainer" splitter="true" region="leading" style="width: 264px;">
                <div data-dojo-type="dijit/layout/ContentPane" splitter="false" region="center" class="grey">
                    <!--<div id="userAdmin" class="content">-->
                    <!-- LEFT HAND SIDE CONTENT BLOCK 1 START -->
                    <!--<div class="spacer"></div>-->
                    <div class="inputContainer">
                        <div id="treeUser"></div>
                    </div>
                </div>
                <div data-dojo-type="dijit/layout/ContentPane" splitter="false" region="bottom">
                    <span class="button">
                        <span style="float: left;">
                            <button data-dojo-type="dijit/form/Button" id="bDeleteUser"
                                title='<fmt:message key="dialog.admin.users.delete" />'
                                onclick="pageUserAdmin.deleteUser()">
                                <fmt:message key="dialog.admin.users.delete" />
                            </button>
                        </span>
                        <span style="float: right;">
                            <button data-dojo-type="dijit/form/Button" id="bImportUser"
                                title='<fmt:message key="dialog.admin.users.create" />'
                                onclick="pageUserAdmin.importPortalUser()">
                                <fmt:message key="dialog.admin.users.create" />
                            </button>
                        </span>
                    </span>
                </div>
                <!-- LEFT HAND SIDE CONTENT BLOCK 1 END -->
            </div>
            <div data-dojo-type="dijit.layout.BorderContainer" splitter="false" region="center">
                <!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
                <span class="functionalLink onTab" style="top: 0px;">
                    <a href="#"
                        onclick="window.open('mdek_help.jsp?lang='+userLocale+'&amp;hkey=user-administration-1#user-administration-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');"
                        title='<fmt:message key="general.help" />'>[?]</a>
                </span>
                <div id="userManagementTab" data-dojo-type="dijit/layout/TabContainer" splitter="false" region="center"
                    doLayout="true" style="width: 100%;" selectedChild="userData">
                    <!-- MAIN TAB 1 START -->
                    <div id="userData" data-dojo-type="dijit/layout/ContentPane"
                        title='<fmt:message key="dialog.admin.users.userData" />' class="tab grey"
                        style="overflow: hidden;">
                        <div class="inputContainer field grey">
                            <span class="outer">
                                <div>
                                    <span class="label left">
                                        <label for="userDataLogin"
                                            onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8013)"> <fmt:message
                                                key="dialog.admin.users.login" />
                                        </label>
                                    </span>
                                    <span class="functionalLink marginRight">
                                        <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a
                                            href="javascript:void(0);" onclick="pageUserAdmin.searchForPortalUser()"
                                            title='<fmt:message key="dialog.admin.users.searchLoginUser" /> [Popup]'><fmt:message
                                                key="dialog.admin.users.searchLoginUser" /></a>
                                    </span>
                                    <span class="input">
                                        <input type="text" id="userDataLogin" name="userDataLogin" style="width: 100%;"
                                            disabled="true" data-dojo-type="dijit/form/ValidationTextBox" />
                                    </span>
                                </div>
                            </span>
                            <span class="outer">
                                <div>
                                    <span class="label">
                                        <label for="userDataRole"
                                            onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8014)"> <fmt:message
                                                key="dialog.admin.users.role" />
                                        </label>
                                    </span>
                                    <span class="input">
                                        <input type="text" id="userDataRole" name="userDataRole" style="width: 100%;"
                                            disabled="true" data-dojo-type="dijit/form/ValidationTextBox" />
                                    </span>
                                </div>
                            </span>

                            <!-- USER DATA -->
                            <div class="outer" style="margin-top: 15px; margin-bottom: 15px;">
                                <span id="surnameInput" class="outer required" style="width: 33%;">
                                    <div>
                                        <span class="label">
                                            <label id="userDataAddressSurnameLabel" for="userDataAddressSurname"
                                                onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8015)">
                                                <fmt:message key="dialog.admin.users.surName" />
                                                <span class="requiredSign">*</span>
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="255" id="userDataAddressSurname"
                                                name="userDataAddressSurname" required="false" style="width: 100%;"
                                                data-dojo-type="dijit/form/ValidationTextBox" />
                                        </span>
                                    </div>
                                </span>
                                <span id="forenameInput" class="outer required" style="width: 33%;">
                                    <div>
                                        <span class="label">
                                            <label id="userDataAddressForenameLabel" for="userDataAddressForename"
                                                onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8200)">
                                                <fmt:message key="dialog.admin.users.foreName" />
                                                <span class="requiredSign">*</span>
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="255" id="userDataAddressForename"
                                                name="userDataAddressForename" required="false" style="width: 100%;"
                                                data-dojo-type="dijit/form/ValidationTextBox" />
                                        </span>
                                    </div>
                                </span>
                                <span class="outer required" style="width: 34%;">
                                    <div>
                                        <span class="label">
                                            <label id="userDataAddressEmailUserLabel" for="userDataAddressEmailUser"
                                                onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8201)">
                                                <fmt:message key="dialog.admin.users.emailUser" />
                                                <span class="requiredSign">*</span>
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="255" id="userDataAddressEmailUser"
                                                name="userDataAddressEmailUser"
                                                regExp="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$"
                                                required="true" style="width: 100%;"
                                                data-dojo-type="dijit/form/ValidationTextBox" />
                                        </span>
                                    </div>
                                </span>
                                <span id="institutionInput" class="outer required" style="width: 33%; clear: both;">
                                    <div>
                                        <span class="label">
                                            <label id="userDataAddressInstitutionLabel" for="userDataAddressInstitution"
                                                onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8203)">
                                                <fmt:message key="dialog.admin.users.institution" />
                                                <span class="requiredSign">*</span>
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="userDataAddressInstitution"
                                                name="userDataAddressInstitution" required="true" style="width: 100%;"
                                                data-dojo-type="dijit/form/ValidationTextBox" />
                                        </span>
                                    </div>
                                </span>
                                <span class="outer" style="width: 33%;">
                                    <div>
                                        <span class="label">
                                            <label id="userDataAddressPhoneLabel" for="userDataAddressPhone"
                                                onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8207)">
                                                <fmt:message key="dialog.admin.users.phone" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="255" id="userDataAddressPhone"
                                                name="userDataAddressPhone" style="width: 100%;"
                                                data-dojo-type="dijit/form/ValidationTextBox" />
                                        </span>
                                    </div>
                                </span>
                                <span class="outer" style="width: 34%;">
                                    <div>
                                        <span class="label">
                                            <label id="userDataAddressEmailPointOfContactLabel"
                                                for="userDataAddressEmailPointOfContact"
                                                onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8202)">
                                                <fmt:message key="dialog.admin.users.emailPointOfContact" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="255" id="userDataAddressEmailPointOfContact"
                                                name="userDataAddressEmailPointOfContact"
                                                regExp="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$"
                                                style="width: 100%;" data-dojo-type="dijit/form/ValidationTextBox" />
                                        </span>
                                    </div>
                                </span>
                                <span class="outer" style="width: 33%; clear: both;">
                                    <div>
                                        <span class="label">
                                            <label id="userDataAddressStreetLabel" for="userDataAddressStreet"
                                                onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8204)">
                                                <fmt:message key="dialog.admin.users.street" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="255" id="userDataAddressStreet"
                                                name="userDataAddressStreet" style="width: 100%;"
                                                data-dojo-type="dijit/form/ValidationTextBox" />
                                        </span>
                                    </div>
                                </span>
                                <span class="outer" style="width: 33%;">
                                    <div>
                                        <span class="label">
                                            <label id="userDataAddressPostCodeLabel" for="userDataAddressPostCode"
                                                onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8205)">
                                                <fmt:message key="dialog.admin.users.postCode" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="10" id="userDataAddressPostCode"
                                                name="userDataAddressPostCode" style="width: 100%;"
                                                data-dojo-type="dijit/form/ValidationTextBox" />
                                        </span>
                                    </div>
                                </span>
                                <span class="outer" style="width: 34%;">
                                    <div>
                                        <span class="label">
                                            <label id="userDataAddressCityLabel" for="userDataAddressCity"
                                                onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8206)">
                                                <fmt:message key="dialog.admin.users.city" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="255" id="userDataAddressCity"
                                                name="userDataAddressCity" style="width: 100%;"
                                                data-dojo-type="dijit/form/ValidationTextBox" />
                                        </span>
                                    </div>
                                </span>
                            </div>

                            <span class="outer" style="width: 45%; clear: both;">
                                <div>
                                    <span class="label">
                                        <label id="availableGroupsListLabel" for="availableGroupsList"
                                            onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8016)"><fmt:message
                                                key="dialog.admin.users.groupsAvailable" /></label>
                                    </span>
                                    <div class="tableContainer">
                                        <div id="availableGroupsList" autoHeight="10" contextMenu="none"></div>
                                    </div>
                                </div>
                            </span>
                            <span class="outer" style="width: 60px;">
                                <div>
                                    <span class="entry">
                                        <span class="buttonCol" style="margin: 80px -4px 0px;">
                                            <button data-dojo-type="dijit/form/Button" id="addSelectedButton"
                                                onclick="pageUserAdmin.addSelected()">&nbsp;&gt;&nbsp;</button>
                                            <button data-dojo-type="dijit/form/Button" id="addAllButton"
                                                onclick="pageUserAdmin.addAll()">&gt;&gt;</button>
                                            <button data-dojo-type="dijit/form/Button" id="removeAllButton"
                                                onclick="pageUserAdmin.removeAll()">&lt;&lt;</button>
                                            <button data-dojo-type="dijit/form/Button" id="removeSelectedButton"
                                                onclick="pageUserAdmin.removeSelected()">&nbsp;&lt;&nbsp;</button>
                                        </span>
                                    </span>
                                </div>
                            </span>
                            <span class="outer" style="width: 45%;">
                                <div>
                                    <span class="label">
                                        <label id="groupsListLabel" for="groupsList"
                                            onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8016)"><fmt:message
                                                key="dialog.admin.users.groupsAssigned" /></label>
                                    </span>
                                    <div class="tableContainer">
                                        <div id="groupsList" autoHeight="10" contextMenu="none"></div>
                                    </div>
                                </div>
                            </span>
                            <div class="fill"></div>
                        </div>
                    </div>
                    <!-- END OF TAB 1 -->
                    <!-- BEGIN OF TAB 2 -->
                    <div id="responsibleData" data-dojo-type="dijit/layout/ContentPane"
                        title='<fmt:message key="dialog.admin.users.responsible" />' class="tab grey"
                        style="width: 100%; overflow: hidden;">
                        <div class="inputContainer field grey">
                            <span class="label">
                                <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8210)"><fmt:message
                                        key="dialog.admin.users.inObjects" /></label>
                            </span>
                            <div class="tableContainer spaceBelow">
                                <div id="responsibleUserInObjects" autoHeight="10" contextMenu="none"></div>
                            </div>
                            <span class="label">
                                <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8211)"><fmt:message
                                        key="dialog.admin.users.inAddresses" /></label>
                            </span>
                            <div class="tableContainer">
                                <div id="responsibleUserInAddresses" autoHeight="10" contextMenu="none"></div>
                            </div>
                        </div>
                    </div>
                    <!-- END OF TAB 2 -->
                </div>
                <div data-dojo-type="dijit/layout/ContentPane" splitter="false" region="bottom">
                    <div class="inputContainer">
                        <span class="button">
                            <span style="float: right;">
                                <button id="btnSaveUser" data-dojo-type="dijit/form/Button" disabled
                                    title='<fmt:message key="dialog.admin.users.save" />'
                                    onclick="pageUserAdmin.saveUser()">
                                    <fmt:message key="dialog.admin.users.save" />
                                </button>
                            </span>
                            <span id="adminUserLoadingZone"
                                style="float: left; margin: 4px 0 0 4px; z-index: 100; visibility: hidden">
                                <img src="img/ladekreis.gif" alt="" />
                            </span>
                        </span>
                    </div>
                </div>
                <!-- RIGHT HAND SIDE CONTENT BLOCK 1 END -->
            </div>
        </div>
    </div>
    <!--</div>-->
    <!-- CONTENT END -->
</body>
</html>