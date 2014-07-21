<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
    var pageDeleteAddress = _container_;
    require([
        "dojo/_base/array",
        "dojo/dom-style",
        "dojo/Deferred",
        "dojo/DeferredList",
        "dojo/on",
        "dijit/registry",
        "ingrid/utils/Grid",
        "ingrid/utils/List",
        "ingrid/utils/Store",
        "ingrid/utils/Address",
        "ingrid/utils/Tree",
        "ingrid/utils/LoadingZone",
        "ingrid/dialog",
        "ingrid/tree/MetadataTree",
        "ingrid/layoutCreator"
    ], function(array, style, Deferred, DeferredList, on, registry, UtilGrid, UtilList, UtilStore, UtilAddress, UtilTree, LoadingZone, dialog, MetadataTree, layoutCreator) {

            var MAX_NUM_DATASETS = 100;

            on(_container_, "Load", function() {
                createDOMElements();
                registry.byId("catalogueAdressesContainer").resize();
            });


            function createDOMElements() {
                new MetadataTree({
                    treeType: "Addresses",
                    showRoot: false,
                    onClick: loadDeleteNodeSelected
                }, "treeAddressDelete");

                new MetadataTree({
                    treeType: "Addresses",
                    showRoot: false,
                    onClick: replaceNodeSelected
                }, "treeAddressNew");

                var addressDeleteDataMDQSStructure = [{
                    field: 'title',
                    name: "<fmt:message key='dialog.admin.catalog.management.deleteAddress.name' />",
                    width: '333px'
                }];
                layoutCreator.createDataGrid("addressDeleteDataMDQS", null, addressDeleteDataMDQSStructure, null);

                var replaceInfoAddressListStructure = [{
                    field: 'icon',
                    name: '&nbsp;',
                    width: '30px'
                }, {
                    field: 'uuid',
                    name: 'ID',
                    width: '300px'
                }, {
                    field: 'objectName',
                    name: "<fmt:message key='dialog.admin.catalog.management.deleteAddress.objectName' />",
                    width: '300px'
                }];

                layoutCreator.createDataGrid("replaceInfoAddressList", null, replaceInfoAddressListStructure, null);

                layoutCreator.createDataGrid("addressNewDataMDQS", null, addressDeleteDataMDQSStructure, null);


            }

            function loadDeleteNodeSelected(node) {
                if (isValidAddressNode(node)) {
                    var addressUuid = node.id;
                    LoadingZone.show();
                    createDetailedAddressInformationDef(addressUuid)
                        .then(function(detailedAddressInfo) {
                            updateDeleteAddressInformation(detailedAddressInfo);
                            LoadingZone.hide();
                        });
                } else {
                    clearDeleteAddressInformation();
                }
            }

            function replaceNodeSelected(node) {
                if (isValidAddressNode(node)) {
                    var addressUuid = node.id;
                    LoadingZone.show();
                    createDetailedAddressInformationDef(addressUuid)
                        .then(function(detailedAddressInfo) {
                            updateNewAddressInformation(detailedAddressInfo);
                            LoadingZone.hide();
                        });
                } else {
                    clearNewAddressInformation();
                }
            }

            // Checks whether a given node is a valid address with an associated uuid (valid) or a container node (invalid)
            function isValidAddressNode(node) {
                return (node !== null && node.id !== null && node.id != "addressRoot" && node.id != "addressFreeRoot");
            }

            // Update the fields for 'address to delete'
            // Input parameter is an addressDetails object created by 'createDetailedAddressInformationDef'
            function updateDeleteAddressInformation(detailedAddressInfo) {
                // Set general information (title, creation date, Id)
                if (detailedAddressInfo.address) {
                    registry.byId("addressDeleteDataTitle").setValue(UtilAddress.createAddressTitle(detailedAddressInfo.address));
                    registry.byId("addressDeleteDataCreationDate").setValue(detailedAddressInfo.address.creationTime);
                    registry.byId("addressDeleteDataID").setValue(detailedAddressInfo.address.uuid);
                }
                // Set responsible user
                if (detailedAddressInfo.responsibleUser) {
                    registry.byId("addressDeleteDataUser").setValue(detailedAddressInfo.responsibleUser.title);
                }
                // Set qa user list
                UtilStore.updateWriteStore("addressDeleteDataMDQS", detailedAddressInfo.qaUsers);

                // Set List of info addresses
                if (detailedAddressInfo.objAddressList) {
                    UtilList.addIcons(detailedAddressInfo.objAddressList);
                    //replaceInfoStore.setData(detailedAddressInfo.objInfoAddressList);
                    UtilStore.updateWriteStore("replaceInfoAddressList", detailedAddressInfo.objAddressList);
                }

                if (detailedAddressInfo.objAddressList.length >= MAX_NUM_DATASETS) {
                    style.set("maxNumDatasetsWarning", "visibility", "visible");
                } else {
                    style.set("maxNumDatasetsWarning", "visibility", "hidden");
                }
            }

            function clearDeleteAddressInformation() {
                registry.byId("addressDeleteDataTitle").setValue("");
                registry.byId("addressDeleteDataCreationDate").setValue("");
                registry.byId("addressDeleteDataID").setValue("");
                registry.byId("addressDeleteDataUser").setValue("");
                UtilGrid.setTableData("addressDeleteDataMDQS", []);
                UtilGrid.setTableData("replaceInfoAddressList", []);
            }

            //Update the fields for 'address to replace'
            //Input parameter is an addressDetails object created by 'createDetailedAddressInformationDef'
            function updateNewAddressInformation(detailedAddressInfo) {
                if (detailedAddressInfo.address) {
                    registry.byId("addressNewDataTitle").setValue(UtilAddress.createAddressTitle(detailedAddressInfo.address));
                    registry.byId("addressNewDataCreationDate").setValue(detailedAddressInfo.address.creationTime);
                    registry.byId("addressNewDataID").setValue(detailedAddressInfo.address.uuid);
                }
                if (detailedAddressInfo.responsibleUser) {
                    registry.byId("addressNewDataUser").setValue(detailedAddressInfo.responsibleUser.title);
                }
                UtilStore.updateWriteStore("addressNewDataMDQS", detailedAddressInfo.qaUsers);
            }

            function clearNewAddressInformation() {
                registry.byId("addressNewDataTitle").setValue("");
                registry.byId("addressNewDataCreationDate").setValue("");
                registry.byId("addressNewDataID").setValue("");
                registry.byId("addressNewDataUser").setValue("");
                UtilGrid.setTableData("addressNewDataMDQS", []);
            }

            // Returns a deferred object with the following information on callback:
            // { address: address details for the given uuid,
            //   responsibleUser: {title:address title, uuid:address uuid},
            //   qaUsers: [ {title:address title, uuid:address uuid}, {...}, ...],
            //   objAddressList: [ {obj1}, {obj2}, ... ],
            // }
            function createDetailedAddressInformationDef(addressUuid) {
                var def = new Deferred();
                var addressDetailsDef = getAddressDetailsDef(addressUuid);
                var responsibleUsersDef = getUsersWithWritePermissionDef(addressUuid);

                var objAddressDef = getObjectsWithAddressDef(addressUuid);

                var defList = new DeferredList([addressDetailsDef, responsibleUsersDef, objAddressDef], false, false, true);
                defList.then(function(resultList) {
                    var addressDetails = resultList[0][1];
                    var responsibleUserList = resultList[1][1];
                    var objAddressList = resultList[2][1];

                    var responsibleUser = null;
                    var index;
                    for (index = 0; index < responsibleUserList.length; ++index) {
                        if (responsibleUserList[index][1] == addressDetails.addressOwner) {
                            responsibleUser = {
                                title: responsibleUserList[index][0],
                                uuid: responsibleUserList[index][1]
                            };
                        }
                    }

                    var qaUsers = [];
                    for (index = 0; index < responsibleUserList.length; ++index) {
                        if (responsibleUserList[index][2]) {
                            qaUsers.push({
                                title: responsibleUserList[index][0],
                                uuid: responsibleUserList[index][1]
                            });
                        }
                    }

                    def.resolve({
                        address: addressDetails,
                        responsibleUser: responsibleUser,
                        qaUsers: qaUsers,
                        objAddressList: objAddressList
                    });
                }, function(err) {
                    displayErrorMessage(err);
                    console.debug("Error: " + err);
                    def.reject(err);
                });

                return def;
            }

            function getAddressDetailsDef(addressUuid) {
                var def = new Deferred();
                AddressService.getAddressData(addressUuid, "false", {
                    callback: function(addressDetails) {
                        def.resolve(addressDetails);
                    },
                    errorHandler: function(message, err) {
                        displayErrorMessage(err);
                        console.debug("Error: " + message);
                        def.reject(err);
                    }
                });
                return def;
            }

            function getUsersWithWritePermissionDef(addressUuid) {
                var def = new Deferred();
                SecurityService.getUsersWithWritePermissionForAddress(addressUuid, false, true, {
                    callback: function(userList) {
                        var list = [];
                        array.forEach(userList, function(user) {
                            var title = UtilAddress.createAddressTitle(user.address);
                            var uuid = user.address.uuid;
                            list.push([title, uuid, userHasQAPermission(user)]);
                        });

                        def.resolve(list);
                    },
                    errorHandler: function(message, err) {
                        displayErrorMessage(err);
                        console.debug("Error: " + message);
                        def.reject(err);
                    }
                });
                return def;
            }

            //Fetch objects where given adrUuid is set as the info address
            function getObjectsWithAddressDef(adrUuid) {
                var def = new Deferred();

                CatalogManagementService.getObjectsOfAddressByType(adrUuid, null, MAX_NUM_DATASETS, {
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

            // opens a download dialog for the search result as csv
            pageDeleteAddress.openDownloadResultAsCSVDialog = function() {
                var selectedNode = registry.byId("treeAddressDelete").selectedNode;

                if (selectedNode && isValidAddressNode(selectedNode.item)) {
                    var addressUuid = selectedNode.item.id;

                    var tableToExport = "OBJECTS_OF_ADDRESS";

                    CatalogManagementService.getCsvData(addressUuid, tableToExport, {
                        preHook: LoadingZone.show,
                        postHook: LoadingZone.hide,
                        callback: function(data) {
                            dwr.engine.openInDownload(data);
                        },
                        errorHandler: function(errMsg, err) {
                            displayErrorMessage(err);
                            console.debug("Error: " + errMsg);
                        }
                    });
                } else {
                    // No node selected
                    console.debug("no node selected.");
                    dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.selectNode' />", dialog.INFO);
                }
            };

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

            pageDeleteAddress.replaceAddress = function() {
                var deleteNode = registry.byId("treeAddressDelete").selectedNode;
                var newNode = registry.byId("treeAddressNew").selectedNode;

                if (isValidAddressNode(deleteNode.item) && isValidAddressNode(newNode.item) && deleteNode.item.id != newNode.item.id) {
                    var def = new Deferred();
                    // ask if user really want to replace address
                    dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.reallyDelete' />", dialog.INFO, [{
                        caption: "<fmt:message key='general.no' />",
                        action: function() {
                            def.reject("CANCEL");
                        }
                    }, {
                        caption: "<fmt:message key='general.ok' />",
                        action: function() {
                            def.resolve();
                        }
                    }]);

                    // call this only if user pressed ok
                    def.then(function() {
                        CatalogManagementService.replaceAddress(deleteNode.item.id, newNode.item.id, {
                            preHook: LoadingZone.show,
                            postHook: LoadingZone.hide,
                            callback: function(data) {
                                // var tree = registry.byId("treeAddressDelete");
                                var newSelectNode = deleteNode.getParent();

                                UtilTree.selectNode("treeAddressDelete", newSelectNode.item.id);
                                newSelectNode.removeChild(deleteNode);
                                loadDeleteNodeSelected(newSelectNode.item);

                                console.debug("address has been replaced!");
                                dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.successfulReplaced' />");

                            },
                            errorHandler: function(errMsg, err) {
                                if (errMsg.indexOf("ADDRESS_IS_IDCUSER_ADDRESS") != -1) {
                                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.addressIsIdcUser' />", dialog.WARNING);
                                } else
                                if (errMsg.indexOf("NODE_HAS_SUBNODES") != -1) {
                                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.nodeHasSubnodes' />", dialog.WARNING);
                                } else {
                                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.error' />: " + errMsg, dialog.WARNING);
                                }
                                console.debug("Error: " + errMsg);
                            }
                        });
                    }, function(err) {
                        if (err != "CANCEL") {
                            displayErrorMessage(err);
                            console.debug("Error: " + err);
                        }
                    });
                } else {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.requiredFieldsHint' />", dialog.WARNING);
                }
            };
        });
        </script>
</head>
<body>
<!-- CONTENT START -->
<div id="contentSection" class="contentBlockWhite top">
    <div id="winNavi" style="top: 0;">
        <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-6#overall-catalog-management-6', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
    </div>
    <div id="catalogueAdresses" class="content">
        <span class="label required">
            <fmt:message key="dialog.admin.catalog.management.deleteAddress.deleteAddress" />
        </span>
        <div id="catalogueAdressesContainer" data-dojo-type="dijit.layout.BorderContainer" design="sidebar" style="height: 465px;">
            <!-- LEFT HAND SIDE CONTENT BLOCK 1 START -->
            <div id="addressDeleteTree" data-dojo-type="dijit/layout/ContentPane" region="leading" class="inputContainer grey" style="width: 250px;">
                <div class="inputContainer">
                    <div id="treeAddressDelete">
                    </div>
                </div>
            </div>
            <!-- LEFT HAND SIDE CONTENT BLOCK 1 END --><!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
            <div id="addressDeleteData" data-dojo-type="dijit/layout/ContentPane" region="center" class="inputContainer grey">
                <div class="inputContainer field ">
                    <span style="float: right; z-index: 100; visibility: hidden; position: absolute; right: 0px; margin-right: 22px;" id="deleteAddressWaitForDataLoadingZone">
                        <img src="img/ladekreis.gif" />
                    </span>
                    <span class="outer">
                        <div>
                            <span class="label">
                                <label for="addressDeleteDataTitle" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8044)">
                                    <fmt:message key="dialog.admin.catalog.management.deleteAddress.title" />
                                </label>
                            </span>
                            <span class="input">
                                <input type="text" id="addressDeleteDataTitle" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width: 100%;"/>
                            </span>
                        </div>
                    </span>
                    <div class="inputContainer">
                        <div class="outer halfWidth">
                            <div>
                                <span class="label">
                                    <label for="addressDeleteDataCreationDate" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8045)">
                                        <fmt:message key="dialog.admin.catalog.management.deleteAddress.creationDate" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressDeleteDataCreationDate" name="addressDeleteDataCreationDate" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width: 100%;"/>
                                </span>
                            </div><div>
                                <span class="label">
                                    <label for="addressDeleteDataUser" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8046)">
                                        <fmt:message key="dialog.admin.catalog.management.deleteAddress.responsibleUser" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressDeleteDataUser" name="addressDeleteDataUser" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width: 100%;"/>
                                </span>
                            </div><div>
                                <span class="label">
                                    <label for="addressDeleteDataID" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8047)">
                                        ID 
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressDeleteDataID" name="addressDeleteDataID" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width: 100%;"/>
                                </span>
                            </div>
                        </div>
                        <div class="outer halfWidth">
                            <span class="label">
                                <label for="addressDeleteDataMDQS" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8048)">
                                    <fmt:message key="dialog.admin.catalog.management.deleteAddress.qa" />
                                </label>
                            </span>
                            <div class="tableContainer">
                                <div id="addressDeleteDataMDQS" autoHeight="5" minRows="5" contextMenu="none" class="hideTableHeader">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div id="addressDeleteDataObjects" data-dojo-type="dijit/layout/ContentPane" region="bottom" class="inputContainer innerPadding">
                <span class="label">
                    <label class="inActive" for="objectLists">
                        <fmt:message key="dialog.admin.catalog.management.deleteAddress.objectPreview" />
                    </label>
                </span>
                <span class="functionalLink" style="position: relative; z-index:10;">
                    <img src="img/ic_fl_save_csv.gif" width="11" height="15" style="" alt="Popup" /><a href="javascript:void(0);" onclick="pageDeleteAddress.openDownloadResultAsCSVDialog()" title="<fmt:message key="dialog.admin.catalog.management.deleteAddress.saveAsCSV" />"><fmt:message key="dialog.admin.catalog.management.deleteAddress.saveAsCSV" /></a>
                </span>
                <!-- <div id="addressDeleteDataObjectsLists" data-dojo-type="dijit/layout/TabContainer" doLayout="false" selectedChild="replaceInfoAddress"> -->
                    <!-- TAB 1 START -->
                    <!-- <div id="replaceInfoAddress" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.admin.catalog.management.deleteAddress.replaceAddress" />"> -->
                        <div class="tableContainer clear">
                            <div id="replaceInfoAddressList" autoHeight="4" contextMenu="none">
                            </div>
                        </div>
                    <!-- </div> -->
                    <!-- TAB 1 END -->
                <!-- </div> -->
                <span id="maxNumDatasetsWarning" style="visibility: hidden;">
                    <fmt:message key="dialog.admin.catalog.management.deleteAddress.maxNumHits" />
                </span>
            </div><!-- RIGHT HAND SIDE CONTENT BLOCK 1 END -->
        </div><!-- LEFT HAND SIDE CONTENT BLOCK 2 START -->
        <span class="label required" style="padding-top: 15px;">
            <fmt:message key="dialog.admin.catalog.management.deleteAddress.newAddress" />
        </span>
        <div data-dojo-type="dijit.layout.BorderContainer" design="headline" style="height: 410px;">
            <div id="addressNewTree" data-dojo-type="dijit/layout/ContentPane" region="left" class="inputContainer grey" style="width: 250px;">
                <div class="inputContainer noSpaceBelow">
                    <div id="treeAddressNew">
                    </div>
                </div>
            </div>
            <!-- LEFT HAND SIDE CONTENT BLOCK 2 END -->
            <!-- RIGHT HAND SIDE CONTENT BLOCK 2 START -->
            <div id="addressNewData" data-dojo-type="dijit/layout/ContentPane" region="center" class="inputContainer grey">
                <div class="inputContainer field noSpaceBelow">
                    <div class="inputContainer spaceBelow">
                        <span class="outer">
                            <div>
                                <span class="label">
                                    <label for="addressNewDataTitle" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8049)">
                                        <fmt:message key="dialog.admin.catalog.management.deleteAddress.title" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressNewDataTitle" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width: 100%;"/>
                                </span>
                                <div class="fill">
                                </div>
                            </div>
                        </span>
                    </div>
                    <div class="inputContainer spaceBelow">
                        <div class="outer halfWidth">
                            <div>
                                <span class="label">
                                    <label for="addressNewDataCreationDate" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8050)">
                                        <fmt:message key="dialog.admin.catalog.management.deleteAddress.creationDate" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressNewDataCreationDate" name="addressNewDataCreationDate" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width: 100%;"/>
                                </span>
                            </div><div>
                                <span class="label">
                                    <label for="addressNewDataUser" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8051)">
                                        <fmt:message key="dialog.admin.catalog.management.deleteAddress.responsibleUser" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressNewDataUser" name="addressNewDataUser" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width: 100%;"/>
                                </span>
                            </div><div>
                                <span class="label">
                                    <label for="addressNewDataID" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8052)">
                                        ID 
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressNewDataID" name="addressNewDataID" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width: 100%;"/>
                                </span>
                            </div>
                        </div>
                        <div class="outer halfWidth">
                            <span class="label">
                                <label for="addressNewDataMDQS" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8053)">
                                    <fmt:message key="dialog.admin.catalog.management.deleteAddress.qa" />
                                </label>
                            </span>
                            <div class="tableContainer ">
                                <div id="addressNewDataMDQS" autoHeight="5" contextMenu="none" class="hideTableHeader">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- RIGHT HAND SIDE CONTENT BLOCK 2 END -->
            <div data-dojo-type="dijit/layout/ContentPane" region="bottom" class="inputContainer">
                <span class="button">
                    <span style="float: right;">
                        <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.management.deleteAddress.replace" />" onclick="pageDeleteAddress.replaceAddress();">
                            <fmt:message key="dialog.admin.catalog.management.deleteAddress.replace" />
                        </button>
                    </span>
                    <span id="deleteAddressLoadingZone" style="float: left; margin-top: 1px; z-index: 100; visibility: hidden">
                        <img src="img/ladekreis.gif" />
                    </span>
                </span>
            </div>
        </div>
    </div>
</div>
<!-- CONTENT END -->
</body>
</html>