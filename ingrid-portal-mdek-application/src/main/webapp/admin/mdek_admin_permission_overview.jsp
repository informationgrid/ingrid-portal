<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
    var scriptScopePermission = _container_;
    require([
        "dojo/on",
        "dijit/registry",
        "dojo/_base/lang",
        "ingrid/layoutCreator",
        "ingrid/tree/MetadataTree",
        "ingrid/utils/Grid",
        "ingrid/utils/Address"
    ], function(on, registry, lang, layoutCreator, MetadataTree, UtilGrid, UtilAddress) {

            createDOMElements();

            // This method is executed when an object in the tree is selected 
            // The argument is an event with an attached treeNode (event.node)
            function displayPermissionsForObject(item, node) {
                var uuid = item.id;
                //var objPermission = registry.byId("rightsObjectsUserList");
                if (uuid == null || uuid == "objectRoot") {
                    UtilGrid.setTableData("rightsObjectsUserList", []);
                    return;
                }

                SecurityService.getUsersWithPermissionForObject(uuid, false, true, {
                    callback: function(userList) {
                        UtilGrid.setTableData("rightsObjectsUserList", []);
                        for (var i in userList) {
                            addPermissionsToUser(userList[i]);
                            userList[i].title = UtilAddress.createAddressTitle(userList[i].address);

                            UtilGrid.addTableDataRow("rightsObjectsUserList", userList[i]);
                        }
                    },
                    errorHandler: function(errMsg, err) {
                        displayErrorMessage(err);
                        console.debug(errMsg);
                    }
                });
            }

            // This method is executed when an address in the tree is selected 
            // The argument is an event with an attached treeNode (event.node)
            function displayPermissionsForAddress(item, node) {
                var uuid = item.id;
                //var adrPermission = registry.byId("rightsAddressesUserList");

                if (uuid == null || uuid == "addressRoot" || uuid == "addressFreeRoot") {
                    UtilGrid.setTableData("rightsAddressesUserList", []);
                    return;
                }

                SecurityService.getUsersWithPermissionForAddress(uuid, false, true, {
                    callback: function(userList) {
                        UtilGrid.setTableData("rightsAddressesUserList", []);
                        for (var i in userList) {
                            addPermissionsToUser(userList[i]);
                            userList[i].title = UtilAddress.createAddressTitle(userList[i].address);

                            UtilGrid.addTableDataRow("rightsAddressesUserList", userList[i]);
                        }
                    },
                    errorHandler: function(errMsg, err) {
                        displayErrorMessage(err);
                        console.debug(errMsg);
                    }
                });
            }

            function createDOMElements() {
                new MetadataTree({
                    treeType: "Objects",
                    showRoot: false,
                    onClick: displayPermissionsForObject
                }, "permissionOverviewTreeObj");
                new MetadataTree({
                    treeType: "Addresses",
                    showRoot: false,
                    onClick: displayPermissionsForAddress
                }, "permissionOverviewTreeAdr");

                var rightsObjectsUserListStructure = [{
                    field: 'title',
                    name: "<fmt:message key='dialog.admin.permissions.userName' />",
                    sortable: true,
                    width: '280px'
                }, {
                    field: 'roleName',
                    name: "<fmt:message key='dialog.admin.permissions.role' />",
                    width: '150px'
                }, {
                    field: 'writeSingle',
                    name: "<fmt:message key='dialog.admin.permissions.single' />",
                    width: '100px'
                }, {
                    field: 'writeTree',
                    name: "<fmt:message key='dialog.admin.permissions.tree' />",
                    width: '100px'
                }, {
                    field: 'writeSubNode',
                    name: "<fmt:message key='dialog.admin.permissions.subnodes' />",
                    width: '100px'
                }, {
                    field: 'qa',
                    name: "<fmt:message key='dialog.admin.permissions.qa' />",
                    width: '100px'
                }];

                on(scriptScopePermission, "Load", function() {
                    layoutCreator.createDataGrid("rightsObjectsUserList", null, rightsObjectsUserListStructure, null);
                    layoutCreator.createDataGrid("rightsAddressesUserList", null, lang
                        .clone(rightsObjectsUserListStructure), null);
                });

            }

            // Helper function that adds html tags to a user object, for display in the result table.
            function addPermissionsToUser(user) {
                for (var i in user.permissions) {
                    if (user.permissions[i] == "WRITE_SINGLE") {
                        user.writeSingle = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";

                    } else if (user.permissions[i] == "WRITE_TREE") {
                        user.writeTree = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";
                    } else if (user.permissions[i] == "WRITE_SUBNODE") {
                        user.writeSubNode = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";
                    } else if (user.permissions[i] == "QUALITY_ASSURANCE") {
                        user.qa = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";
                    }
                }
            }

        });
</script>


</head>
<body>
    <div class="contentBlockWhite top">
        <div id="winNavi" style="top: 0;">
            <a href="javascript:void(0);"
                onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&amp;hkey=user-administration-3#user-administration-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');"
                title='<fmt:message key="general.help" />'>[?]</a>
        </div>
        <div id="rightsAdmin" class="content">

            <!-- CONTENT BLOCK 1 START -->
            <!-- SPLIT CONTAINER START -->
            <div>
                <span class="label required"><label
                    onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8025)"><fmt:message
                            key="dialog.admin.permissions.objectPermissions" /></label></span>
                <div data-dojo-type="dijit.layout.BorderContainer" design="sidebar" gutters="true" liveSplitters="false"
                    style="height: 250px;" id="rightsObjects">
                    <!-- LEFT HAND SIDE CONTENT BLOCK 1 START -->
                    <div data-dojo-type="dijit/layout/ContentPane" region="leading" splitter="true"
                        class="inputContainer grey" style="width: 200px;">
                        <div class="inputContainer">
                            <div id="permissionOverviewTreeObj"></div>
                        </div>
                    </div>
                    <!-- LEFT HAND SIDE CONTENT BLOCK 1 END -->

                    <!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
                    <div data-dojo-type="dijit/layout/ContentPane" region="center" class="inputContainer"
                        style="padding: 5px;">

                        <div id="rightsObjectsUser" class="inputContainer grey" style="width: 100%;">
                            <div class="tableContainer">
                                <div id="rightsObjectsUserList" autoHeight="9" contextMenu="none"></div>
                            </div>
                        </div>
                    </div>
                    <!-- RIGHT HAND SIDE CONTENT BLOCK 1 END -->
                </div>
            </div>
            <!-- CONTENT BLOCK 1 END -->

            <div class="spacer"></div>

            <!-- CONTENT BLOCK 2 START -->
            <!-- SPLIT CONTAINER START -->
            <div style="padding-top: 15px;">
                <span class="label required"><label
                    onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8026)"><fmt:message
                            key="dialog.admin.permissions.addressPermissions" /></label></span>
                <div data-dojo-type="dijit.layout.BorderContainer" id="rightsAddresses" orientation="horizontal"
                    style="height: 250px;" layoutAlign="client">
                    <!-- LEFT HAND SIDE CONTENT BLOCK 2 START -->
                    <div data-dojo-type="dijit/layout/ContentPane" region="leading" splitter="true"
                        class="inputContainer grey" style="width: 200px;">
                        <div class="inputContainer">
                            <div id="permissionOverviewTreeAdr"></div>
                        </div>
                    </div>
                    <!-- LEFT HAND SIDE CONTENT BLOCK 2 END -->

                    <!-- RIGHT HAND SIDE CONTENT BLOCK 2 START -->
                    <div data-dojo-type="dijit/layout/ContentPane" region="center" class="inputContainer"
                        style="padding: 5px;">

                        <div id="rightsAddressesUser" class="inputContainer grey" style="">
                            <div class="tableContainer">
                                <div id="rightsAddressesUserList" autoHeight="9" contextMenu="none"></div>
                            </div>
                        </div>
                    </div>
                    <!-- RIGHT HAND SIDE CONTENT BLOCK 2 END -->
                </div>
            </div>
            <!-- CONTENT BLOCK 2 END -->
        </div>
    </div>
    <!-- CONTENT END -->

</body>
</html>
