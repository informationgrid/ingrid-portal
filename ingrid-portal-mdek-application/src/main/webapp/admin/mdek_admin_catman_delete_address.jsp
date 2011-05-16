<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
            dojo.require("ingrid.dijit.CustomTree");
            dojo.require("ingrid.dijit.tree.LazyTreeStoreModel");
            dojo.require("dijit.layout.TabContainer");
            dojo.require("dijit.layout.BorderContainer");
            dojo.require("dijit.form.ValidationTextBox");

            var scriptScopeDeleteAddr = _container_;
            var MAX_NUM_DATASETS = 100;
            
            createDOMElements();
            
            
            function createDOMElements(){
                createCustomTree("treeAddressDelete", null, "id", "title", dojo.partial(expandLoadAddresses, "treeAddressDelete"));
                dojo.connect(dijit.byId("treeAddressDelete"), "onClick", deleteNodeSelected);
                
                createCustomTree("treeAddressNew", null, "id", "title", dojo.partial(expandLoadAddresses, "treeAddressNew"));
                dojo.connect(dijit.byId("treeAddressNew"), "onClick", replaceNodeSelected);
                
                var addressDeleteDataMDQSStructure = [
                    {field: 'title',name: "<fmt:message key='dialog.admin.catalog.management.deleteAddress.name' />",width: 333-scrollBarWidth+'px'}
                ];
                createDataGrid("addressDeleteDataMDQS", null, addressDeleteDataMDQSStructure, null);
                
                var replaceInfoAddressListStructure = [
                    {field: 'icon',name: '&nbsp;',width: '30px'},
                    {field: 'uuid',name: 'ID',width: '300px'}, 
                    {field: 'objectName',name: "<fmt:message key='dialog.admin.catalog.management.deleteAddress.objectName' />",width: '300px'}
                ];
                var replaceInfoAddressListStructure2 = [
                    {field: 'icon',name: '&nbsp;',width: '30px'},
                    {field: 'uuid',name: 'ID',width: '300px'}, 
                    {field: 'title',name: "<fmt:message key='dialog.admin.catalog.management.deleteAddress.objectName' />",width: '300px'}
                ];
                
                createDataGrid("replaceInfoAddressList", null, replaceInfoAddressListStructure, null);
                
                createDataGrid("replaceUserList", null, replaceInfoAddressListStructure, null);
                
                createDataGrid("replaceAddressList", null, replaceInfoAddressListStructure2, null);
                
                createDataGrid("addressNewDataMDQS", null, addressDeleteDataMDQSStructure, null);
                
                
            }
            
            function expandLoadAddresses(treeId, node, callback_function){
                var parentItem = node.item;
                var prefix = treeId + "_";
                var store = dijit.byId(treeId).model.store;
				var pubOnly = treeId == "treeAddressNew";
                
                var def = UtilTree.getSubTree(parentItem, prefix.length);
                
                def.addCallback(function(data){
                    //return _this.loadProcessResponse(node, data);
                    // just use the address node here!
                    if (parentItem.root) {
                        var origId = data[1].id;
                        //data[1].id = prefix + data[1].id;
                        data[1].uuid = data[1].id;
                        data[1].id = prefix + data[1].id;
                        store.newItem(data[1]);
                    }
                    else {
                        dojo.forEach(data, function(entry){
							if (pubOnly && !entry.isPublished && entry.id != "addressFreeRoot")
							    return;
                            var origId = entry.id;
                            entry.uuid = entry.id;
                            entry.id = prefix + entry.id;
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
            

            function deleteNodeSelected(node){
                if (isValidAddressNode(node)) {
                    var addressUuid = node.uuid[0];
                    showLoadingZone();
                    var detailedAddressInfoDef = createDetailedAddressInformationDef(addressUuid);
                    detailedAddressInfoDef.addCallback(function(detailedAddressInfo){
                        updateDeleteAddressInformation(detailedAddressInfo);
                        hideLoadingZone();
                    });
                }
                else {
                    clearDeleteAddressInformation();
                }
            }
            
            function replaceNodeSelected(node){
                if (isValidAddressNode(node)) {
                    var addressUuid = node.uuid[0];
                    showLoadingZone();
                    var detailedAddressInfoDef = createDetailedAddressInformationDef(addressUuid);
                    detailedAddressInfoDef.addCallback(function(detailedAddressInfo){
                        updateNewAddressInformation(detailedAddressInfo);
                        hideLoadingZone();
                    });
                }
                else {
                    clearNewAddressInformation();
                }
            }
            
            // Checks whether a given node is a valid address with an associated uuid (valid) or a container node (invalid)
            function isValidAddressNode(node){
                return (node != null && node.uuid != null && node.uuid != "addressRoot" && node.uuid != "addressFreeRoot");
            }
            
            // Update the fields for 'address to delete'
            // Input parameter is an addressDetails object created by 'createDetailedAddressInformationDef'
            function updateDeleteAddressInformation(detailedAddressInfo){
                // Set general information (title, creation date, Id)
                if (detailedAddressInfo.address) {
                    dijit.byId("addressDeleteDataTitle").setValue(UtilAddress.createAddressTitle(detailedAddressInfo.address));
                    dijit.byId("addressDeleteDataCreationDate").setValue(detailedAddressInfo.address.creationTime);
                    dijit.byId("addressDeleteDataID").setValue(detailedAddressInfo.address.uuid);
                }
                // Set responsible user
                if (detailedAddressInfo.responsibleUser) {
                    dijit.byId("addressDeleteDataUser").setValue(detailedAddressInfo.responsibleUser.title);
                }
                // Set qa user list
                UtilStore.updateWriteStore("addressDeleteDataMDQS", detailedAddressInfo.qaUsers);
                
                // Set List of info addresses
                if (detailedAddressInfo.objAddressList) {
                    UtilList.addIcons(detailedAddressInfo.objAddressList);
                    //replaceInfoStore.setData(detailedAddressInfo.objInfoAddressList);
                    UtilStore.updateWriteStore("replaceInfoAddressList", detailedAddressInfo.objAddressList);
                }
                
                // Set List of objects whose responsible user will be replaced
                if (detailedAddressInfo.objResponsibleAddressList) {
                    UtilList.addIcons(detailedAddressInfo.objResponsibleAddressList);
                    UtilStore.updateWriteStore("replaceUserList", detailedAddressInfo.objResponsibleAddressList);
                }
                
                // Set List of addresses whose responsible user will be replaced
                if (detailedAddressInfo.addrResponsibleUserList) {
                    UtilList.addIcons(detailedAddressInfo.addrResponsibleUserList);
                    UtilList.addAddressTitles(detailedAddressInfo.addrResponsibleUserList);
                    UtilStore.updateWriteStore("replaceAddressList", detailedAddressInfo.addrResponsibleUserList);
                }
                
                if (detailedAddressInfo.objResponsibleAddressList.length >= MAX_NUM_DATASETS ||
                detailedAddressInfo.objAddressList.length >= MAX_NUM_DATASETS ||
                detailedAddressInfo.addrResponsibleUserList.length >= MAX_NUM_DATASETS) {
				dojo.byId('maxNumDatasetsWarning').style.visibility = "visible";
                }
                else {
					dojo.byId('maxNumDatasetsWarning').style.visibility = "hidden";
                }
            }
            
            function clearDeleteAddressInformation(){
                dijit.byId("addressDeleteDataTitle").setValue("");
                dijit.byId("addressDeleteDataCreationDate").setValue("");
                dijit.byId("addressDeleteDataID").setValue("");
                dijit.byId("addressDeleteDataUser").setValue("");
                UtilGrid.setTableData("addressDeleteDataMDQS", []);
                UtilGrid.setTableData("replaceInfoAddressList", []);
                UtilGrid.setTableData("replaceUserList", []);
                UtilGrid.setTableData("replaceAddressList", []);
            }
            
            //Update the fields for 'address to replace'
            //Input parameter is an addressDetails object created by 'createDetailedAddressInformationDef'
            function updateNewAddressInformation(detailedAddressInfo){
                if (detailedAddressInfo.address) {
                    dijit.byId("addressNewDataTitle").setValue(UtilAddress.createAddressTitle(detailedAddressInfo.address));
                    dijit.byId("addressNewDataCreationDate").setValue(detailedAddressInfo.address.creationTime);
                    dijit.byId("addressNewDataID").setValue(detailedAddressInfo.address.uuid);
                }
                if (detailedAddressInfo.responsibleUser) {
                    dijit.byId("addressNewDataUser").setValue(detailedAddressInfo.responsibleUser.title);
                }
                UtilStore.updateWriteStore("addressNewDataMDQS", detailedAddressInfo.qaUsers);
            }
            
            function clearNewAddressInformation(){
                dijit.byId("addressNewDataTitle").setValue("");
                dijit.byId("addressNewDataCreationDate").setValue("");
                dijit.byId("addressNewDataID").setValue("");
                dijit.byId("addressNewDataUser").setValue("");
                UtilGrid.setTableData("addressNewDataMDQS", []);
            }
            
            // Returns a deferred object with the following information on callback:
            // { address: address details for the given uuid,
            //   responsibleUser: {title:address title, uuid:address uuid},
            //   qaUsers: [ {title:address title, uuid:address uuid}, {...}, ...],
            //   objAddressList: [ {obj1}, {obj2}, ... ],
            //   objResponsibleAddressList: [ {obj1}, {obj2}, ... ]
            // }
            function createDetailedAddressInformationDef(addressUuid){
                var def = new dojo.Deferred();
                var addressDetailsDef = getAddressDetailsDef(addressUuid);
                var responsibleUsersDef = getUsersWithWritePermissionDef(addressUuid);
                
                var objAddressDef = getObjectsWithAddressDef(addressUuid);
                var objResponsibleAddressDef = getObjectsWithResponsibleUserDef(addressUuid);
                var addrResponsibleUserDef = getAddressesWithResponsibleUser(addressUuid);
                
                var defList = new dojo.DeferredList([addressDetailsDef, responsibleUsersDef, objAddressDef, objResponsibleAddressDef, addrResponsibleUserDef], false, false, true);
                defList.addCallback(function(resultList){
                    var addressDetails = resultList[0][1];
                    var responsibleUserList = resultList[1][1];
                    var objAddressList = resultList[2][1];
                    var objResponsibleAddressList = resultList[3][1];
                    var addrResponsibleUserList = resultList[4][1];
                    
                    var responsibleUser = null;
                    for (var index = 0; index < responsibleUserList.length; ++index) {
                        if (responsibleUserList[index][1] == addressDetails.addressOwner) {
                            responsibleUser = {
                                title: responsibleUserList[index][0],
                                uuid: responsibleUserList[index][1]
                            };
                        }
                    }
                    
                    var qaUsers = [];
                    for (var index = 0; index < responsibleUserList.length; ++index) {
                        if (responsibleUserList[index][2]) {
                            qaUsers.push({
                                title: responsibleUserList[index][0],
                                uuid: responsibleUserList[index][1]
                            });
                        }
                    }
                    
                    def.callback({
                        address: addressDetails,
                        responsibleUser: responsibleUser,
                        qaUsers: qaUsers,
                        objAddressList:objAddressList,
                        objResponsibleAddressList: objResponsibleAddressList,
                        addrResponsibleUserList: addrResponsibleUserList
                    });
                });
                
                defList.addErrback(function(err){
                    displayErrorMessage(err);
                    console.debug("Error: " + err);
                    def.errback(err);
                });
                
                return def;
            }
            
            function getAddressDetailsDef(addressUuid){
                var def = new dojo.Deferred();
                AddressService.getAddressData(addressUuid, "false", {
                    callback: function(addressDetails){
                        def.callback(addressDetails);
                    },
                    errorHandler: function(message, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + message);
                        def.errback(err);
                    }
                });
                return def;
            }
            
            function getUsersWithWritePermissionDef(addressUuid){
                var def = new dojo.Deferred();
                SecurityService.getUsersWithWritePermissionForAddress(addressUuid, false, true, {
                    callback: function(userList){
                        var list = [];
                        dojo.forEach(userList, function(user){
                            var title = UtilAddress.createAddressTitle(user.address);
                            var uuid = user.address.uuid;
                            list.push([title, uuid, userHasQAPermission(user)]);
                        });
                        
                        def.callback(list);
                    },
                    errorHandler: function(message, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + message);
                        def.errback(err);
                    }
                });
                return def;
            }
            
            //Fetch objects where given adrUuid is set as the info address
            function getObjectsWithAddressDef(adrUuid){
                var def = new dojo.Deferred();
                
                CatalogManagementService.getObjectsOfAddressByType(adrUuid, null, MAX_NUM_DATASETS, {
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
            function getObjectsWithResponsibleUserDef(adrUuid){
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
            
            // opens a download dialog for the search result as csv
            scriptScopeDeleteAddr.openDownloadResultAsCSVDialog = function(){
                var selectedChild = dijit.byId("addressDeleteDataObjectsLists").selectedChildWidget.id;
                var selectedNode = dijit.byId("treeAddressDelete").selectedNode;
                
                if (selectedNode && isValidAddressNode(selectedNode.item)) {
                    var addressUuid = selectedNode.item.uuid[0];
                    
                    if ("replaceInfoAddress" == selectedChild) {
                        var tableToExport = "OBJECTS_OF_AUSKUNFT_ADDRESS";
                    }
                    else 
                        if ("replaceUser" == selectedChild) {
                            var tableToExport = "OBJECTS_OF_RESPONSIBLE_USER";
                        }
                        else 
                            if ("replaceAddress" == selectedChild) {
                                var tableToExport = "ADDRESSES_OF_RESPONSIBLE_USER";
                            }
                    
                    CatalogManagementService.getCsvData(addressUuid, tableToExport, {
                        preHook: showLoadingZone,
                        postHook: hideLoadingZone,
                        callback: function(data){
                            dwr.engine.openInDownload(data);
                        },
                        errorHandler: function(errMsg, err){
                            displayErrorMessage(err);
                            console.debug("Error: " + errMsg);
                        }
                    });
                }
                else {
                    // No node selected
                    console.debug("no node selected.");
                    dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.selectNode' />", dialog.INFO);
                }
            }
            
            function userHasQAPermission(user){
                if (user.permissions) {
                    for (var index = 0; index < user.permissions.length; ++index) {
                        if (user.permissions[index] == "QUALITY_ASSURANCE") {
                            return true;
                        }
                    }
                }
                
                return false;
            }
            
            scriptScopeDeleteAddr.replaceAddress = function(){
                var deleteNode = dijit.byId("treeAddressDelete").selectedNode;
                var newNode = dijit.byId("treeAddressNew").selectedNode;
                
                if (isValidAddressNode(deleteNode.item) && isValidAddressNode(newNode.item) && deleteNode.item.uuid != newNode.item.uuid) {
                    var def = new dojo.Deferred();
                    // ask if user really want to replace address
                    dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.reallyDelete' />", dialog.INFO, [{
                        caption: "<fmt:message key='general.no' />",
                        action: function(){
                            def.errback("CANCEL");
                        }
                    }, {
                        caption: "<fmt:message key='general.ok' />",
                        action: function(){
                            def.callback();
                        }
                    }]);
                    
                    // call this only if user pressed ok
                    def.addCallback(function(){
                        CatalogManagementService.replaceAddress(deleteNode.item.uuid[0], newNode.item.uuid[0], {
                            preHook: showLoadingZone,
                            postHook: hideLoadingZone,
                            callback: function(data){
                                //initTree();
                                var tree = dijit.byId("treeAddressDelete");
                                var newSelectNode = deleteNode.getParent();
                                //tree.selectNode(newSelectNode);

                                UtilTree.selectNode("treeAddressDelete", newSelectNode.item.id[0]);
                                deleteNodeSelected(newSelectNode.item);
                                deleteNode.destroy();
                                
                                console.debug("address has been replaced!");
                                dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.successfulReplaced' />");
                                
                            },
                            errorHandler: function(errMsg, err){
                                if (errMsg.indexOf("ADDRESS_IS_IDCUSER_ADDRESS") != -1) {
                                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.addressIsIdcUser' />", dialog.WARNING);
                                }
                                else 
                                    if (errMsg.indexOf("NODE_HAS_SUBNODES") != -1) {
                                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.nodeHasSubnodes' />", dialog.WARNING);
                                    }
                                    else {
                                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.error' />: " + errMsg, dialog.WARNING);
                                    }
                                console.debug("Error: " + errMsg);
                            }
                        });
                    });
                    def.addErrback(function(err){
                        if (err != "CANCEL") {
                            displayErrorMessage(err);
                            console.debug("Error: " + err);
                        }
                    });
                }
                else {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.catalog.management.deleteAddress.requiredFieldsHint' />", dialog.WARNING);
                }
            }
            /*
             function replaceAddressDef(delAdrUuid, newAdrUuid) {
             // TODO implement
             var def = new dojo.Deferred();
             def.callback();
             return def;
             }
             */
            function showLoadingZone(){
				dojo.byId('deleteAddressWaitForDataLoadingZone').style.visibility = "visible";
            }
            
            function hideLoadingZone(){
				dojo.byId('deleteAddressWaitForDataLoadingZone').style.visibility = "hidden";
            }
        </script>
</head>
<body>
<!-- CONTENT START -->
<div id="contentSection" class="contentBlockWhite top">
    <div id="winNavi" style="top: 0;">
        <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=overall-catalog-management-6#overall-catalog-management-6', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
    </div>
    <div id="catalogueAdresses" class="content">
        <span class="label required">
            <fmt:message key="dialog.admin.catalog.management.deleteAddress.deleteAddress" />
        </span>
        <div dojoType="dijit.layout.BorderContainer" design="sidebar" style="height: 465px;">
            <!-- LEFT HAND SIDE CONTENT BLOCK 1 START -->
            <div id="addressDeleteTree" dojoType="dijit.layout.ContentPane" region="leading" class="inputContainer grey" style="width: 250px;">
                <div class="inputContainer">
                    <div id="treeAddressDelete">
                    </div>
                </div>
            </div>
            <!-- LEFT HAND SIDE CONTENT BLOCK 1 END --><!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
            <div id="addressDeleteData" dojoType="dijit.layout.ContentPane" region="center" class="inputContainer grey">
                <div class="inputContainer field ">
                    <span style="float: right; z-index: 100; visibility: hidden; position: absolute; right: 0px; margin-right: 22px;" id="deleteAddressWaitForDataLoadingZone">
                        <img src="img/ladekreis.gif" />
                    </span>
                    <span class="outer">
                        <div>
                            <span class="label">
                                <label for="addressDeleteDataTitle" onclick="javascript:dialog.showContextHelp(arguments[0], 8044)">
                                    <fmt:message key="dialog.admin.catalog.management.deleteAddress.title" />
                                </label>
                            </span>
                            <span class="input">
                                <input type="text" id="addressDeleteDataTitle" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width: 100%;"/>
                            </span>
                        </div>
                    </span>
                    <div class="inputContainer">
                        <div class="outer halfWidth">
                            <div>
                                <span class="label">
                                    <label for="addressDeleteDataCreationDate" onclick="javascript:dialog.showContextHelp(arguments[0], 8045)">
                                        <fmt:message key="dialog.admin.catalog.management.deleteAddress.creationDate" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressDeleteDataCreationDate" name="addressDeleteDataCreationDate" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width: 100%;"/>
                                </span>
                            </div><div>
                                <span class="label">
                                    <label for="addressDeleteDataUser" onclick="javascript:dialog.showContextHelp(arguments[0], 8046)">
                                        <fmt:message key="dialog.admin.catalog.management.deleteAddress.responsibleUser" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressDeleteDataUser" name="addressDeleteDataUser" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width: 100%;"/>
                                </span>
                            </div><div>
                                <span class="label">
                                    <label for="addressDeleteDataID" onclick="javascript:dialog.showContextHelp(arguments[0], 8047)">
                                        ID 
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressDeleteDataID" name="addressDeleteDataID" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width: 100%;"/>
                                </span>
                            </div>
                        </div>
                        <div class="outer halfWidth">
                            <span class="label">
                                <label for="addressDeleteDataMDQS" onclick="javascript:dialog.showContextHelp(arguments[0], 8048)">
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
            <div id="addressDeleteDataObjects" dojoType="dijit.layout.ContentPane" region="bottom" class="inputContainer innerPadding">
                <span class="label">
                    <label class="inActive" for="objectLists">
                        <fmt:message key="dialog.admin.catalog.management.deleteAddress.objectPreview" />
                    </label>
                </span>
                <span class="functionalLink" style="top: 27px; position: relative; z-index:10;">
                    <img src="img/ic_fl_save_csv.gif" width="11" height="15" style="" alt="Popup" /><a href="javascript:void(0);" onclick="javascript:scriptScopeDeleteAddr.openDownloadResultAsCSVDialog();" title="<fmt:message key="dialog.admin.catalog.management.deleteAddress.saveAsCSV" />"><fmt:message key="dialog.admin.catalog.management.deleteAddress.saveAsCSV" /></a>
                </span>
                <div id="addressDeleteDataObjectsLists" dojoType="dijit.layout.TabContainer" doLayout="false" selectedChild="replaceInfoAddress">
                    <!-- TAB 1 START -->
                    <div id="replaceInfoAddress" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.admin.catalog.management.deleteAddress.replaceAddress" />">
                        <div class="tableContainer">
                            <div id="replaceInfoAddressList" autoHeight="4" contextMenu="none">
                            </div>
                        </div>
                    </div>
                    <!-- TAB 1 END -->
                    <!-- TAB 2 START -->
                    <div id="replaceUser" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.admin.catalog.management.deleteAddress.replaceResponsibleAddress" />">
                        <div class="tableContainer">
                            <div id="replaceUserList" autoHeight="4" contextMenu="none">
                            </div>
                        </div>
                    </div>
                    <!-- TAB 2 END -->
                    <!-- TAB 3 START -->
                    <div id="replaceAddress" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.admin.catalog.management.deleteAddress.replaceResponsibleAddressFromAddress" />">
                        <div class="tableContainer">
                            <div id="replaceAddressList" autoHeight="4" contextMenu="none">
                            </div>
                        </div>
                    </div>
                    <!-- TAB 3 END -->
                </div>
                <span id="maxNumDatasetsWarning" style="visibility: hidden;">
                    <fmt:message key="dialog.admin.catalog.management.deleteAddress.maxNumHits" />
                </span>
            </div><!-- RIGHT HAND SIDE CONTENT BLOCK 1 END -->
        </div><!-- LEFT HAND SIDE CONTENT BLOCK 2 START -->
        <span class="label required" style="padding-top: 15px;">
            <fmt:message key="dialog.admin.catalog.management.deleteAddress.newAddress" />
        </span>
        <div dojoType="dijit.layout.BorderContainer" design="headline" style="height: 410px;">
            <div id="addressNewTree" dojoType="dijit.layout.ContentPane" region="left" class="inputContainer grey" style="width: 250px;">
                <div class="inputContainer noSpaceBelow">
                    <div id="treeAddressNew">
                    </div>
                </div>
            </div>
            <!-- LEFT HAND SIDE CONTENT BLOCK 2 END -->
            <!-- RIGHT HAND SIDE CONTENT BLOCK 2 START -->
            <div id="addressNewData" dojoType="dijit.layout.ContentPane" region="center" class="inputContainer grey">
                <div class="inputContainer field noSpaceBelow">
                    <div class="inputContainer spaceBelow">
                        <span class="outer">
                            <div>
                                <span class="label">
                                    <label for="addressNewDataTitle" onclick="javascript:dialog.showContextHelp(arguments[0], 8049)">
                                        <fmt:message key="dialog.admin.catalog.management.deleteAddress.title" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressNewDataTitle" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width: 100%;"/>
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
                                    <label for="addressNewDataCreationDate" onclick="javascript:dialog.showContextHelp(arguments[0], 8050)">
                                        <fmt:message key="dialog.admin.catalog.management.deleteAddress.creationDate" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressNewDataCreationDate" name="addressNewDataCreationDate" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width: 100%;"/>
                                </span>
                            </div><div>
                                <span class="label">
                                    <label for="addressNewDataUser" onclick="javascript:dialog.showContextHelp(arguments[0], 8051)">
                                        <fmt:message key="dialog.admin.catalog.management.deleteAddress.responsibleUser" />
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressNewDataUser" name="addressNewDataUser" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width: 100%;"/>
                                </span>
                            </div><div>
                                <span class="label">
                                    <label for="addressNewDataID" onclick="javascript:dialog.showContextHelp(arguments[0], 8052)">
                                        ID 
                                    </label>
                                </span>
                                <span class="input">
                                    <input type="text" id="addressNewDataID" name="addressNewDataID" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width: 100%;"/>
                                </span>
                            </div>
                        </div>
                        <div class="outer halfWidth">
                            <span class="label">
                                <label for="addressNewDataMDQS" onclick="javascript:dialog.showContextHelp(arguments[0], 8053)">
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
            <div dojoType="dijit.layout.ContentPane" region="bottom" class="inputContainer">
                <span class="button">
                    <span style="float: right;">
                        <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.management.deleteAddress.replace" />" onClick="javascript:scriptScopeDeleteAddr.replaceAddress();">
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