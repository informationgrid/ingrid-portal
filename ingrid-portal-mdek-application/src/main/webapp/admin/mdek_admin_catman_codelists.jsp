<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
    <script type="text/javascript">
        var pageCodelists = null;
        
        require([
            "dojo/on",
            "dojo/aspect",
            "dojo/dom",
            "dojo/dom-style",
            "dijit/registry",
            "dojo/_base/lang",
            "dojo/_base/array",
            "dojo/Deferred",
            "dojo/DeferredList",
            "dojo/string",
            "ingrid/utils/Grid",
            "ingrid/layoutCreator",
            "ingrid/menu",
            "ingrid/utils/String",
            "ingrid/utils/List",
            "ingrid/utils/Catalog",
            "ingrid/utils/LoadingZone",
            "ingrid/utils/Syslist",
            "ingrid/utils/Store",
            "ingrid/utils/UI",
            "ingrid/dialog",
            "ingrid/message",
            "ingrid/grid/CustomGridEditors",
            "ingrid/grid/CustomGridEditors",
            "ingrid/grid/CustomGridFormatters"
        ], function(on, aspect, dom, style, registry, lang, array, Deferred, DeferredList, string, UtilGrid, layoutCreator, menu, UtilString, UtilList, UtilCatalog, LoadingZone, UtilSyslist, UtilStore, UtilUI, dialog, message, GridEditors, gridFormatters) {

            
            var MAINTAINABLE_LIST_IDS = [];
            var CAN_SET_DEFAULT_LIST_IDS = [1350, 1370];
            var IDS_WITH_DATA_COLUMNS = ["1100"];

            createDOMElements();
                        
            on( _container_, "Load", function() {
                initImportExportLink();
                updateCodelistTimeStamp();
                initCodelistSelect();
                initCodelistTables();
                initFreeEntrySelect();
                initFreeEntryTables();
                registry.byId("contentPane").resize();
                refreshReindexProcessInfo();

                var tab = registry.byId("codeListTabContainer");
                var handle = tab.watch("selectedChildWidget", function(event, tabWidget, selectedTab) {
                    if (selectedTab.id == "freeEntryTab") {
                        registry.byId("freeEntryTable").reinitLastColumn(true);
                        registry.byId("freeEntryCodelistTable").reinitLastColumn(true);
                        // handle.unwatch();
                    }
                });
            });
            
            function createDOMElements() {
                var codeListTable11Structure = [
                    {field: 'entryId',name: "<fmt:message key='dialog.admin.catalog.management.codelists.id' />",width: '32px'},
                    {field: 'deName',name: "<fmt:message key='dialog.admin.catalog.management.codelists.germanName' />",width: '300px', editable:true},
                    {field: 'enName',name: "<fmt:message key='dialog.admin.catalog.management.codelists.englishName' />",width: '300px', editable:true},
                    {field: 'data',name: "<fmt:message key='dialog.admin.catalog.management.codelists.data' />",width: '300px', editable:true}
                ];
                layoutCreator.createDataGrid("codeListTable11", null, codeListTable11Structure, null);
                
                var codeListTable12Structure = [
                    {field: 'entryId',name: "<fmt:message key='dialog.admin.catalog.management.codelists.id' />",width: '32px'},
                    {field: 'deName',name: "<fmt:message key='dialog.admin.catalog.management.codelists.germanName' />",width: '269px', editable:true},
                    {field: 'enName',name: "<fmt:message key='dialog.admin.catalog.management.codelists.englishName' />",width: '269px', editable:true},
                    {field: 'isDefault',name: 'isDefault',width: 'auto', editor: GridEditors.YesNoCheckboxCellEditor, formatter:gridFormatters.BoolCellFormatter, editable:true},
                    {field: 'data',name: "<fmt:message key='dialog.admin.catalog.management.codelists.data' />",width: '300px', editable:true}
                ];
                layoutCreator.createDataGrid("codeListTable12", null, codeListTable12Structure, null);
                
                var freeEntryTableStructure = [
                    {field: 'title',name: 'title',width: '300px'}
                ];
                layoutCreator.createDataGrid("freeEntryTable", null, freeEntryTableStructure, null);
                
                var freeEntryCodelistTableStructure = [
                    {field: 'deName',name: 'deName',width: '300px'}
                ];
                layoutCreator.createDataGrid("freeEntryCodelistTable", null, freeEntryCodelistTableStructure, null);
                
                aspect.after(registry.byId("codeListTable12"), "onCellChange", function(result, args) {
                    var msg = args[0];
                    if (msg.cell == 3 && msg.item.isDefault) {
                        // remove true default value from any other row!
                        array.forEach(this.getData(), function(item, row) {
                            if (msg.row != row) {
                                item.isDefault = false;
                            }
                        });
                        this.invalidate();
                    }
                });
                
            }
            
            function initImportExportLink(){
                // Only show the import/export links if the first tab (codeListTab) is selected
                var tabContainer = registry.byId("codeListTab");
                on(tabContainer, "onShow", function(){
                    dom.byId("importExportLink").style.visibility = "visible";
                });
                on(tabContainer, "onHide", function(){
                    dom.byId("importExportLink").style.visibility = "hidden";
                });
            }
            
            function initCodelistSelect(){
                var selectWidget = registry.byId("selectionList");
                
                getAllSysListInfosDef()
                .then(function(listInfos){
                    var selectWidgetData = [];
                    for (var index = 0; index < listInfos.length; ++index) {
                        var name = UtilCatalog.getNameForSysList(listInfos[index].id);
                        var editable = listInfos[index].maintainable;
                        var displayedText = name + " (" + listInfos[index].id + ")";
                        if (!editable) {
                            displayedText += " [" + "<fmt:message key='dialog.admin.catalog.management.codelist.notMaintainable' />" + "]";
                        } else {
                            MAINTAINABLE_LIST_IDS.push(listInfos[index].id);
                        }
                        selectWidgetData.push([displayedText, listInfos[index].id + ""]);
                    }
                    
                    // Sort list by the display values
                    selectWidgetData.sort(function(a, b){
                        return UtilString.compareIgnoreCase(a[0], b[0]);
                    });
                    
                    //selectWidget.dataProvider.setData(selectWidgetData);
                    UtilStore.updateWriteStore("selectionList", selectWidgetData, {identifier:'1', label:'0'});
                }, function(error){
                    displayErrorMessage(error);
                    console.debug("Error: " + error);
                });
                
                // On value changed load the selected sysList from the backend and update the table
                on(selectWidget, "Change", function(value){
                    if (value) {
                        var germanListDef = getSysListDef(value, "de");
                        var englishListDef = getSysListDef(value, "en");
                        
                        if (array.some(MAINTAINABLE_LIST_IDS, function(listId){
                            return listId == parseInt(value);
                        })) {
                            console.debug("enable. (" + parseInt(value) + ")");
                            UtilGrid.updateOption("codeListTable11", "editable", true);
                            UtilGrid.updateOption("codeListTable11", "enableAddRow", true);
                            UtilGrid.updateOption("codeListTable12", "editable", true);
                            UtilGrid.updateOption("codeListTable12", "enableAddRow", true);
                            // enable context menu
                            menu.contextMenu.DEFAULT.bindDomNode(dom.byId("codeListTable11"));
                            menu.contextMenu.DEFAULT.bindDomNode(dom.byId("codeListTable12"));
                            registry.byId("button_codelistSave").set("disabled", false);
                            hideEditDisabledHint();
                            
                        }
                        else {
                            console.debug("disable. (" + parseInt(value) + ")");
                            UtilGrid.updateOption("codeListTable11", "editable", false);
                            UtilGrid.updateOption("codeListTable11", "enableAddRow", false);
                            UtilGrid.updateOption("codeListTable12", "editable", false);
                            UtilGrid.updateOption("codeListTable12", "enableAddRow", false);
                            // disable context menu
                            menu.contextMenu.DEFAULT.unBindDomNode(dom.byId("codeListTable11"));
                            menu.contextMenu.DEFAULT.unBindDomNode(dom.byId("codeListTable12"));
                            registry.byId("button_codelistSave").set("disabled", true);
                            showEditDisabledHint();
                        }
                        
                        new DeferredList([germanListDef, englishListDef])
                        .then(function(resultList){
                            var germanList = resultList[0][1];
                            var englishList = resultList[1][1];
                            var germanData = UtilSyslist.convertSysListToTableData(germanList);
                            var englishData = UtilSyslist.convertSysListToTableData(englishList);
                            var mergedData = mergeTableData(germanData, englishData);
                            updateCodelistTable(mergedData);
                        });
                        
                        // hide/show data column
                        var showColumn = IDS_WITH_DATA_COLUMNS.indexOf(value) !== -1;
                        if (showColumn) {
                            registry.byId("codeListTable11").showColumn("data");
                            style.set("infoText", "display", "block");
                        } else {
                            registry.byId("codeListTable11").hideColumn("data");
                            style.set("infoText", "display", "none");
                        }

                        registry.byId("codeListTable11").reinitLastColumn(true);
                    }
                });
            }
            
            function initCodelistTables(){
                // Use the same store for both tables. The First table has to be reinitialised so the new store
                // gets registered properly
                var mainStore = UtilGrid.getTableData("codeListTable12");//.store;
                UtilGrid.setTableData("codeListTable11", mainStore);
                
                // Helper function that displays a simple dialog with 'text' as content. There are two buttons 'yes' and 'no'.
                // If the user clicks yes, the invocation is triggered. Otherwise nothing happens.
                /*var askUserAndInvokeOrCancel = function(text, invocation) {
                    dialog.show("<fmt:message key='general.hint' />", text, dialog.INFO, [{
                        caption: "<fmt:message key='general.no' />",
                        action: function(){
                            ;
                        }
                    }, {
                        caption: "<fmt:message key='general.yes' />",
                        action: function(){
                            invocation.proceed();
                        }
                    }]);
                }*/
            }
            
            
            // Retrieve all sysList ids stored in the backend
            // A list of the following form is returned:
            // [ listId1, listId2, ... ]
            function getAllSysListInfosDef(){
                var def = new Deferred();
                CatalogService.getAllSysListInfos({
                    preHook: LoadingZone.show,
                    postHook: LoadingZone.hide,
                    callback: function(listInfos){
                        def.resolve(listInfos);
                    },
                    errorHandler: function(msg, err){
                        LoadingZone.hide();
                        displayErrorMessage(err);
                        console.debug("Error: " + msg);
                        def.reject(err);
                    }
                });
                return def.promise;
            }
            
            // Retrieve the sysList for the given listId and languageCode from the backend
            // A list of the following form is returned:
            // [ [listEntry, entryId, isDefault], [...] ]
            function getSysListDef(listId, languageCode){
                var def = new Deferred();
                CatalogService.getSysLists([listId], languageCode, {
                    preHook: LoadingZone.show,
                    postHook: LoadingZone.hide,
                    callback: function(listItems){
                        def.resolve(listItems[listId]);
                    },
                    errorHandler: function(msg, err){
                        LoadingZone.hide();
                        displayErrorMessage(err);
                        console.debug("Error: " + msg);
                        def.reject(err);
                    }
                });
                return def.promise;
            }
            
            // Merge two lists into one. The lists must have the following format:
            // [ { entryId:entryId, name:entryName isDefault:default }, {...} ]
            // The result is of the format:
            // [ { entryId:entryId, deName:entryName, enName:entryName, isDefault:default }, {...} ]
            function mergeTableData(germanList, englishList){
                // Create a hash map referenced by entry ids
                var idEntryMap = {};
                
                // Fill the hash map with all german entries
                var index,
                    currentEntry;
                for (index = 0; index < germanList.length; index++) {
                    currentEntry = germanList[index];
                    idEntryMap[currentEntry.entryId] = {
                        deName: currentEntry.name,
                        entryId: currentEntry.entryId,
                        isDefault: currentEntry.isDefault,
                        data: currentEntry.data
                    };
                }
                
                // add the english name if the corresponding entry id was found in the hash
                // otherwise create a new entry
                for (index = 0; index < englishList.length; index++) {
                    currentEntry = englishList[index];
                    if (idEntryMap[currentEntry.entryId]) {
                        // Entry found in hash, only update enName
                        idEntryMap[currentEntry.entryId].enName = currentEntry.name;
                        
                    }
                    else {
                        // New entry
                        idEntryMap[currentEntry.entryId] = {
                            enName: currentEntry.name,
                            entryId: currentEntry.entryId,
                            isDefault: currentEntry.isDefault,
                            data: currentEntry.data
                        };
                    }
                }
                
                // Convert hash map to table
                var mergedData = [];
                for (var entry in idEntryMap) {
                    mergedData.push(idEntryMap[entry]);
                }
                
                mergedData = mergedData.sort(function(a, b){
                    return parseInt(a.entryId) - parseInt(b.entryId);
                });
                return mergedData;
            }
            
            // Prepare the lists for display (add indices and checkboxes) and overwrite the codeListTables list entries
            // Data must be an array containing entries (objects) of the following form:
            // [ { entryId:entryId, deName:entryName, enName:entryName, isDefault:default }, {...} ]
            function updateCodelistTable(data){
                //UtilList.addTableIndices(data);
                var sysListId = parseInt(registry.byId("selectionList").getValue());
                
                if (array.some(CAN_SET_DEFAULT_LIST_IDS, function(listId){
                    return listId == sysListId;
                })) {
                    enableDefaultCheckbox();
                    hasDefaultEntry(data) ? showDefaultRadioButtons() : hideDefaultRadioButtons();
                    
                }
                else {
                    hideDefaultRadioButtons();
                    disableDefaultCheckbox();
                }
                // Add radio buttons
                for (var index = 0; index < data.length; index++) {
                    data[index].isDefault = data[index].isDefault;
                }
                // Both stores are the same
                //registry.byId("codeListTable11").store.setData(data);
                UtilGrid.setTableData("codeListTable11", data);
                UtilGrid.setTableData("codeListTable12", data);
            }
            
            // Check if the given list of objects contains an object with property 'isDefault' == true
            function hasDefaultEntry(data){
                return array.some(data, function(item){
                    return item.isDefault === true;
                });
            }
            
            function showDefaultRadioButtons(){
                registry.byId("selectionListDefault").setValue(true);
                UtilUI.switchTableDisplay("codeListTable12", "codeListTable11", true);
            }
            
            function hideDefaultRadioButtons(){
                registry.byId("selectionListDefault").setValue(false);
                UtilUI.switchTableDisplay("codeListTable12", "codeListTable11", false);
            }
            
            function enableDefaultCheckbox(){
                dom.byId("codeListDefaultDisabledHint").style.visibility = "hidden";
                registry.byId("selectionListDefault").setDisabled(false);
            }
            
            function disableDefaultCheckbox(){
                dom.byId("codeListDefaultDisabledHint").style.visibility = "visible";
                registry.byId("selectionListDefault").setDisabled(true);
            }
            
            // Get the modified data and send it to the server
            function saveChanges() {
                var selectedChild = registry.byId("codeListTabContainer").selectedChildWidget.id;
                
                if ("codeListTab" == selectedChild) {
                    saveChangesCodelist();
                    
                } else if ("freeEntryTab" == selectedChild) {
                    saveChangesFreeEntryDef();
                }
            }
            
            
            function saveChangesCodelist(){
                var sysListId = registry.byId("selectionList").getValue();
                
                var isValid = validateCodelist(sysListId);
                if (!isValid) {
                    dialog.show(message.get("general.error"), message.get("dialog.admin.catalog.management.codelist.boundingbox.wrong.format"), dialog.WARNING);
                    return;
                }
                
                if (sysListId) {
                    var setDefault = registry.byId("selectionListDefault").checked;
                    var tableData = UtilGrid.getTableData("codeListTable11");
                    
                    console.debug("sysList id: " + sysListId);
                    
                    // Build the required parameters
                    var defaultIndex = null;
                    var entryIds = [];
                    var entriesGerman = [];
                    var entriesEnglish = [];
                    var data = [];
                    for (var index = 0; index < tableData.length; index++) {
                        var currentEntry = tableData[index];
                        var isDefault = setDefault && currentEntry.isDefault;// && isMarkedAsDefaultEntry(currentEntry);
                        entryIds.push(currentEntry.entryId);
                        entriesGerman.push(currentEntry.deName);
                        entriesEnglish.push(currentEntry.enName);
                        data.push(currentEntry.data);
                        if (isDefault) {
                            defaultIndex = index;
                        }
                    }
                    
                    // Send data to the db
                    // TODO Implement List of maintainable sysLists
                    var maintainable = array.some(MAINTAINABLE_LIST_IDS, function(listId){
                        return listId == parseInt(sysListId);
                    });
                    storeSysListDef(sysListId, maintainable, defaultIndex, entryIds, entriesGerman, entriesEnglish, data)
                    .then(function(){
                        // Show a 'success' message
                        dialog.show("<fmt:message key='general.info' />", "<fmt:message key='dialog.admin.catalog.management.codelist.storeSuccess' />", dialog.INFO);
                        
                        // Update the frontend after the list has been stored
                        registry.byId("selectionList").onChange();
                    });
                }
            }
            
            function validateCodelist(sysListId) {
                if (sysListId == "1100") {
                    // check all data columns for valid entries
                    var data = UtilGrid.getTableData("codeListTable11");
                    var isValid = true;
                    array.forEach(data, function(row) {
                        // continue with next entry if data field is null!
                        if (!row.data) return;
                        
                        var splittedData = row.data.split(" ");
                        if (splittedData.length === 1 && splittedData[0] != "") {
                            isValid = false;
                        } else if (splittedData.length === 4) {
                            var allValid = dojo.every(splittedData, function(item) { return item.match(/\d*\,?\d*/)[0] == item;});
                            if (!allValid) isValid = false;
                        } else if (splittedData.length > 1) {
                            isValid = false;
                        }
                    });
                    return isValid;
                } else {
                    return true;
                }
            }
            
            // Returns whether an entry in the table is marked as default
            /*function isMarkedAsDefaultEntry(entry){
                // Extract the id of the corresponding radio button. In most cases it's equal to entryId
                // New entries don't have an entryId so we have to locate it another way
                // Here we get the id from the string used to create the html radio button
                var isDefaultStr = entry.isDefault;
                var entryId = isDefaultStr.match(/codeListRadio_(\d+)'/)[1];
                
                return dom.byId("codeListRadio_" + entryId).checked;
            }*/
            
            // Store a modified sysList in the db.
            // listId - Id of the sysList to store
            // maintainable - a boolean flag signaling if the sysList is allowed to be modified
            // defaultIndex - the index of the default entry. Null if no entry should have a default value
            // entryIds - Ids of the entries as Int List. Null if it's a new entry
            // entriesGerman, entriesEnglish - The Entries as String Lists
            function storeSysListDef(listId, maintainable, defaultIndex, entryIds, entriesGerman, entriesEnglish, data){
                var def = new Deferred();
                CatalogService.storeSysList(listId, maintainable, defaultIndex, entryIds, entriesGerman, entriesEnglish, data, {
                    callback: function(res){
                        console.debug("result:" + res);
                        def.resolve(res);
                    },
                    errorHandler: function(msg, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + msg);
                        def.reject();
                    }
                });
                return def.promise;
            }
            
            
            /* function LoadingZone.show(){
                dom.byId("codelistsLoadingZone").style.visibility = "visible";
                dom.byId("codelistsFreeLoadingZone").style.visibility = "visible";
            }
            
            function LoadingZone.hide(){
                dom.byId("codelistsLoadingZone").style.visibility = "hidden";
                dom.byId("codelistsFreeLoadingZone").style.visibility = "hidden";
            } */
            
            function showEditDisabledHint(){
                dom.byId("codeListEditDisabledHint").style.visibility = "visible";
            }
            
            function hideEditDisabledHint(){
                dom.byId("codeListEditDisabledHint").style.visibility = "hidden";
            }
            
            
            
            // Functions for the second div. Adding free entries to a list / overwriting free entries with sysList entries
            
            function initFreeEntrySelect(){
                var selectWidget = registry.byId("freeEntrySelectionList");
                
                // Currently the form is restricted to the list with id 1350 - 'rechtliche Grundlagen'
                // 6005 - Zusatzinfo -> Spezifikation der Konformität
                // 6020 - Verfügbarkeit -> Nutzungsbedingungen
                var listIds = [1350, 6005, 6020];
                var selectWidgetData = [];
                for (var index = 0; index < listIds.length; ++index) {
                    var name = UtilCatalog.getNameForSysList(listIds[index]);
                    selectWidgetData.push([name + " (" + listIds[index] + ")", listIds[index] + ""]);
                }
                //selectWidget.dataProvider.setData( selectWidgetData );
                UtilStore.updateWriteStore("freeEntrySelectionList", selectWidgetData, {identifier:'1', label:'0'});
                
                // On value changed load the selected sysList from the backend and update the table
                on(selectWidget, "Change", reloadFreeEntryCodelistsDef);
                
                // Initially set the select widget value to the first one in the list
                selectWidget.setValue(listIds[0]);
            }
            
            function reloadFreeEntryCodelistsDef(listId){
                var def = new Deferred();
                
                if (listId) {
                    var germanListDef = getSysListDef(listId, "de");
                    var englishListDef = getSysListDef(listId, "en");
                    var freeEntryDef = getFreeEntriesDef(listId);
                    
                    new DeferredList([germanListDef, englishListDef, freeEntryDef])
                    .then(function(resultList){
                        var germanList = resultList[0][1];
                        var englishList = resultList[1][1];
                        var freeList = resultList[2][1];
                        
                        var germanData = UtilSyslist.convertSysListToTableData(germanList);
                        var englishData = UtilSyslist.convertSysListToTableData(englishList);
                        
                        var mergedData = mergeTableData(germanData, englishData);
                        updateFreeEntryCodelistTable(mergedData);
                        
                        updateFreeEntryTable(freeList);
                        
                        def.resolve();

                    }, function(){
                        def.reject();
                    });
                }
                return def.promise;
            }
            
            function initFreeEntryTables(){
                //registry.byId("freeEntryCodelistTable").removeContextMenu();
                //registry.byId("freeEntryTable").removeContextMenu();
            }
            
            function updateFreeEntryCodelistTable(data){
                UtilStore.updateWriteStore("freeEntryCodelistTable", data);
                UtilGrid.getTable("freeEntryCodelistTable").resizeCanvas();
            }
            
            function updateFreeEntryTable(entries){
                var data = UtilList.listToTableData(entries);
                UtilStore.updateWriteStore("freeEntryTable", data);
                UtilGrid.getTable("freeEntryTable").resizeCanvas();
            }
            
            function getFreeEntriesDef(mdekListId){
                var def = new Deferred();
                CatalogService.getFreeListEntries(mdekListId, {
                    preHook: LoadingZone.show,
                    postHook: LoadingZone.hide,
                    callback: function(entries){
                        def.resolve(entries);
                    },
                    errorHandler: function(msg, err){
                        LoadingZone.hide();
                        displayErrorMessage(err);
                        console.debug("Error: " + msg);
                        def.reject(err);
                    }
                });
                return def.promise;
            }
            
            
            function addFreeEntryToSysList(){
                var freeEntry = UtilGrid.getSelectedData("freeEntryTable")[0];
                
                if (freeEntry) {
                    var def = new Deferred();
                    //var titles = freeEntry.map(function(item) {return item.title[0];});
                    var displayText = string.substitute("<fmt:message key='dialog.admin.catalog.management.codelist.freeEntryToSysListEntry' />", [freeEntry.title]);
                    dialog.show("<fmt:message key='general.error' />", displayText, dialog.INFO, [{
                        caption: "<fmt:message key='general.no' />",
                        action: function(){
                            def.reject("CANCEL");
                        }
                    }, {
                        caption: "<fmt:message key='general.yes' />",
                        action: function(){
                            def.resolve();
                        }
                    }]);
                    
                    def.then(function(){
                        return addFreeEntryToSysListDef(freeEntry);
                    })
                    .then(function(){
                        // Show a 'success' message
                        dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.codelist.freeEntryToSysListEntrySuccess' />", dialog.INFO);
                        
                        // Update the frontend after the list has been stored
                        var selectWidget = registry.byId("freeEntrySelectionList");
                        //selectWidget.setValue(selectWidget.getValue());
                        reloadFreeEntryCodelistsDef(selectWidget.getValue());
                    }, function(err){
                        if (err != "CANCEL") {
                            displayErrorMessage(err);
                            console.debug("Error: " + err);
                        }
                    });
                }
            }
            
            function addAllFreeEntriesToSysList() {
                var freeData = UtilGrid.getTableData("freeEntryTable");
                //var sysListId = registry.byId("freeEntrySelectionList").getValue();
                
                if (freeData && freeData.length > 0) {
                
                    var freeEntryTitles = [];
                    array.forEach(freeData, function(freeEntry){
                        freeEntryTitles.push(freeEntry.title);
                    });
                    var def = new Deferred();
                    var displayText = string.substitute("<fmt:message key='dialog.admin.catalog.management.codelist.freeEntriesToSysListEntries' />", [freeEntryTitles.join(", ")]);
                    dialog.show("<fmt:message key='general.hint' />", displayText, dialog.INFO, [{
                        caption: "<fmt:message key='general.no' />",
                        action: function(){
                            def.reject("CANCEL");
                        }
                    }, {
                        caption: "<fmt:message key='general.ok' />",
                        action: function(){
                            def.resolve();
                        }
                    }]);
                    
                    freeData = freeData.reverse();
                    while (freeData.length > 0) {
                        var freeEntry = freeData.pop();
                        (function(entry){
                            def.then(function(){
                                return addFreeEntryToSysListDef(entry);
                            });
                            //              def.then(function() { return reloadFreeEntryCodelistsDef(sysListId); });
                        })(freeEntry);
                    }
                    
                    def.then(function(){
                        // Show a 'success' message
                        dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.codelist.freeEntriesToSysListEntriesSuccess' />", dialog.INFO);
                        
                        // Update the frontend after the list has been stored
                        var selectWidget = registry.byId("freeEntrySelectionList");
                        //selectWidget.setValue(selectWidget.getValue());
                        reloadFreeEntryCodelistsDef(selectWidget.getValue());
                    }, function(err){
                        if (err != "CANCEL") {
                            displayErrorMessage(err);
                            console.debug("Error: " + err);
                        }
                    });
                }
            }
            
            function addFreeEntryToSysListDef(freeEntry){
                var addFreeEntryDef = new Deferred();
                
                var codelistData = UtilGrid.getTableData("freeEntryCodelistTable");
                var sysListId = registry.byId("freeEntrySelectionList").getValue();
                
                console.debug("free entry: " + freeEntry);
                
                if (freeEntry) {
                    // First add the free entry to the sysList and store the sysList in the db
                    // After that, replace the free entry which was just added to the sysList with the stored sysList entry 
                    var sysListEntry = null;
                    for (var index = 0; index < codelistData.length; ++index) {
                        if (codelistData[index].deName == freeEntry.title) {
                            sysListEntry = codelistData[index];
                            break;
                        }
                    }
                    
                    var def = null;
                    
                    if (sysListEntry !== null) {
                        // If the entry was already found, just replace the free entry with the sysList entry
                        console.debug("Entry already exists. Replacing free entry with sysList entry.");
                        def = replaceFreeEntryWithSysListEntryDef(freeEntry.title, sysListEntry.entryId, sysListEntry.deName, sysListId);
                        
                    }
                    else {
                        // Otherwise add the free entry to the list, store it, and replace afterwards
                        var newEntry = {
                            deName: freeEntry.title,
                            enName: "",
                            entryId: null,
                            isDefault: false
                        };
                        
                        UtilGrid.addTableDataRow("freeEntryCodelistTable", newEntry);
                        
                        def = saveChangesFreeEntryDef()
                        .then(function(){
                            return getSysListEntryDef(sysListId, freeEntry.title);
                        })
                        .then(function(newSysListEntry){
                            if (newSysListEntry === null || newSysListEntry === undefined) {
                                console.debug("syslist is null!");
                                return;
                            }
                            newEntry.entryId = newSysListEntry[1];
                            return replaceFreeEntryWithSysListEntryDef(freeEntry.title, newSysListEntry[1], newSysListEntry[0], sysListId);
                        });
                    }
                    
                    def.then(function(){
                        addFreeEntryDef.resolve();
                    }, function(err){
                        addFreeEntryDef.reject(err);
                    });
                    
                }
                else {
                    addFreeEntryDef.reject("No free entry selected!");
                }
                
                return addFreeEntryDef;
            }
            
            
            function getSysListEntryDef(listId, title){
                return getSysListDef(listId, "de")
                .then(function(sysList){
                    for (var index = 0; index < sysList.length; ++index) {
                        if (sysList[index][0] == title) {
                            return sysList[index];
                        }
                    }
                    return null;
                });
            }
            
            
            function replaceFreeEntryWithSysListEntryDef(freeEntry, sysListEntryId, sysListEntryName, sysListId){
                var def = new Deferred();
                
                CatalogService.replaceFreeEntryWithSysListEntry(freeEntry, sysListId, sysListEntryId, sysListEntryName, {
                    preHook: LoadingZone.show,
                    postHook: LoadingZone.hide,
                    callback: function(){
                        def.resolve();
                    },
                    errorHandler: function(msg, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + msg);
                        def.reject(err);
                    }
                });
                
                return def.promise;
            }
            
            
            function replaceFreeEntryWithSysListEntry(){
                var sysListId = registry.byId("freeEntrySelectionList").getValue();
                
                var codelistEntry = UtilGrid.getSelectedData("freeEntryCodelistTable")[0];
                var freeEntry = UtilGrid.getSelectedData("freeEntryTable")[0];
                
                if (codelistEntry && freeEntry) {
                    var def = new Deferred();
                    var displayText = string.substitute("<fmt:message key='dialog.admin.catalog.management.codelist.replaceFreeEntryWithSysListEntry' />", [freeEntry.title, codelistEntry.deName]);
                    dialog.show("<fmt:message key='general.hint' />", displayText, dialog.INFO, [{
                        caption: "<fmt:message key='general.no' />",
                        action: function(){
                            def.reject("CANCEL");
                        }
                    }, {
                        caption: "<fmt:message key='general.ok' />",
                        action: function(){
                            def.resolve();
                        }
                    }]);
                    
                    def.then(function(){
                        return replaceFreeEntryWithSysListEntryDef(freeEntry.title, codelistEntry.entryId, codelistEntry.deName, sysListId);
                    })
                    .then(function(){
                        // Show a 'success' message
                        dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.codelist.replaceFreeEntryWithSysListEntrySuccess' />", dialog.INFO);
                        
                        // Update the frontend after the list has been stored
                        var selectWidget = registry.byId("freeEntrySelectionList");
                        //selectWidget.setValue(selectWidget.getValue());
                        reloadFreeEntryCodelistsDef(selectWidget.getValue());
                    }, function(err){
                        if (err != "CANCEL") {
                            displayErrorMessage(err);
                            console.debug("Error: " + err);
                        }
                    });
                    
                }
                else {
                    console.debug("Must select items in both tables!");
                    dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.codelists.selectFromBothTables' />", dialog.INFO);
                }
            }
            
            
            
            function saveChangesFreeEntryDef(){
                var def = new Deferred();
                
                var sysListId = registry.byId("freeEntrySelectionList").getValue();
                if (sysListId) {
                    var tableData = UtilGrid.getTableData("freeEntryCodelistTable");
                    
                    console.debug("sysList id: " + sysListId);
                    
                    // Build the required parameters
                    var defaultIndex = null;
                    var entryIds = [];
                    var entriesGerman = [];
                    var entriesEnglish = [];
                    var data = [];
                    for (var index = 0; index < tableData.length; index++) {
                        var currentEntry = tableData[index];
                        var isDefault = currentEntry.isDefault;
                        entryIds.push(currentEntry.entryId);
                        entriesGerman.push(currentEntry.deName ? currentEntry.deName : null);
                        entriesEnglish.push(currentEntry.enName ? currentEntry.enName : null);
                        data.push(currentEntry.data ? currentEntry.data : "");
                        if (isDefault) {
                            defaultIndex = index;
                        }
                    }
                    
                    // Send data to the db
                    var maintainable = array.some(MAINTAINABLE_LIST_IDS, function(listId){
                        return listId == parseInt(sysListId);
                    });
                    var sysListDef = storeSysListDef(sysListId, maintainable, defaultIndex, entryIds, entriesGerman, entriesEnglish, data);
                    sysListDef.then(function(){
                        def.resolve();
                    }, function(err){
                        displayErrorMessage(err);
                        console.debug("Error: " + err);
                        def.reject(err);
                    });
                }
                
                return def;
            }
            
            
            // -- Reindex Tab Functions --
            function startReindexJob(){
                CatalogManagementService.rebuildSysListData({
                    timeout: 1000,
                    preHook: LoadingZone.show,
                    postHook: LoadingZone.hide,
                    callback: function(){
                        refreshReindexProcessInfo();
                    },
                    errorHandler: function(err){
                        if (err == "Timeout") {
                            refreshReindexProcessInfo();
                            
                        }
                        else {
                            displayErrorMessage(err);
                            console.debug("Error: " + err);
                        }
                    }
                });
            }
            
            function refreshReindexProcessInfo(){
                console.debug("call getRebuildSysListDataJobInfo");
                CatalogManagementService.getRebuildSysListDataJobInfo({
                    callback: function(jobInfo){
                        updateReindexJobInfo(jobInfo);
                        if (!jobFinished(jobInfo)) {
                            setTimeout(refreshReindexProcessInfo, 1000);
                            
                        }
                        else {
                            LoadingZone.hide();//("reindexLoadingZone");
                        }
                    },
                    errorHandler: function(message, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + message);
                        // If there's a timeout try again
                        if (err.message != "USER_LOGIN_ERROR") {
                            setTimeout(refreshReindexProcessInfo, 1000);
                        }
                    }
                });
            }
            
            function jobFinished(jobInfo){
                //console.debug("start: " + jobInfo.startTime + " . end: " + jobInfo.endTime + " . exception: " + jobInfo.exception);
                return (jobInfo.startTime === null || jobInfo.endTime !== null || jobInfo.exception !== null);
            }
            
            function updateReindexJobInfo(jobInfo){
                if (jobFinished(jobInfo)) {
                    dom.byId("reindexJobInfo").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexLastProcessInfo' />" + ":";
                    
                    if (jobInfo.startTime === null) {
                        // Job has never been started...
                        dom.byId("reindexStart").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexStart' />";
                        dom.byId("reindexEnd").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexEnd' />";
                        
                    }
                    else {
                        dom.byId("reindexStart").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexStart' />" + ": " + UtilString.getDateString(jobInfo.startTime, "EEEE, dd. MMMM yyyy HH:mm:ss");
                        dom.byId("reindexEnd").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexEnd' />" + ": " + UtilString.getDateString(jobInfo.endTime, "EEEE, dd. MMMM yyyy HH:mm:ss");
                    }
                    style.set("reindexStatus", "display", "none");
                    style.set("reindexNumEntities", "display", "none");
                    style.set("reindexEnd", "display", "block");
                
                }
                else {
                    dom.byId("reindexJobInfo").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexCurrentProcessInfo' />" + ":";
                    dom.byId("reindexStart").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexStart' />" + ": " + UtilString.getDateString(jobInfo.startTime, "EEEE, dd. MMMM yyyy HH:mm:ss");
                    dom.byId("reindexStatus").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexState' />" + ": " + jobInfo.description;
                    dom.byId("reindexNumEntities").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexDatasets' />" + ": " + jobInfo.numProcessedEntities + " / " + jobInfo.numEntities;
                    style.set("reindexStatus", "display", "block");
                    style.set("reindexNumEntities", "display", "block");
                    style.set("reindexEnd", "display", "none");
                }
            }
            
            
            /* LoadingZone.show = function(id) {
                dom.byId(id).style.visibility = "visible";
            }
            
            LoadingZone.hide = function(id) {
                dom.byId(id).style.visibility = "hidden";
            } */
            
            // -- Import / Export --
            function importCodelists(){
                var deferred = new Deferred();
                
                deferred.then(function(inputFile){
                
                    if (inputFile) {
                        var askUserDef = new Deferred();
                        var displayText = "<fmt:message key='dialog.admin.catalog.management.codelist.importHint' />";
                        dialog.show("<fmt:message key='general.hint' />", displayText, dialog.INFO, [{
                            caption: "<fmt:message key='general.no' />",
                            action: function(){}
                        }, {
                            caption: "<fmt:message key='general.yes' />",
                            action: function(){
                                askUserDef.resolve();
                            }
                        }]);
                        
                        askUserDef.then(function(){
                            importSysLists(inputFile);
                        });
                    }
                    
                });
                
                dialog.showPage("<fmt:message key='dialog.admin.catalog.management.codelist.selectImportFile' />", "admin/dialogs/mdek_select_file_dialog.jsp", 620, 180, true, {
                    // custom parameters
                    resultHandler: deferred
                });
                
            }
            
            function importSysLists(inputFile){
                CatalogService.importSysLists(inputFile, {
                    callback: function(){
                        dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.codelist.importSuccess' />", dialog.INFO);
                    },
                    errorHandler: function(errMsg){
                        dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.generalError' />", [errMsg]), dialog.WARNING, null, 350, 350);
                        console.debug("Error: " + errMsg);
                    }
                });
            }
            
            function exportCodelists() {
                CatalogService.exportSysLists(null, {
                    callback: function(exportFile){
                        dwr.engine.openInDownload(exportFile);
                    },
                    errorHandler: function(errMsg, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + errMsg);
                    }
                });
            }
            
            function updateCodelistTimeStamp() {
                this.codelistTimestamp = null;
                var self = this;
                CatalogManagementService.getLastModifiedCodelistTimestamp( {
                    preHook: LoadingZone.show,// "updateCodelistsLoadingZone"),
                    postHook: LoadingZone.hide,// "updateCodelistsLoadingZone"),
                    callback: function(timestamp){
                        var content = "";
                        if (timestamp > 0) {
                            self.codelistTimestamp = timestamp;
                            content = new Date(timestamp);
                        } else {
                            content = timestamp;
                        }
                        dom.byId("updateCodelistsStatus").innerHTML = content;
                    },
                    errorHandler: function(errMsg, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + errMsg);
                    }
                });
                
            }
            
            /**
             * Force to update all codelists from initial codelist or from connected repository.
             */
            function updateCodelists() {
                CatalogManagementService.forceUpdateCodelists( {
                    preHook: LoadingZone.show,//, "updateCodelistsLoadingZone"),
                    postHook: LoadingZone.hide,//, "updateCodelistsLoadingZone"),
                    callback: function( result ) {
                        dom.byId("updateCodelistsResult").innerHTML = result ? '<fmt:message key="dialog.admin.catalog.management.codelists.result.success" />' : '<fmt:message key="dialog.admin.catalog.management.codelists.result.error" />';
                        updateCodelistTimeStamp();
                    },
                    errorHandler: function(errMsg, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + errMsg);
                    }
                } );
            }

            pageCodelists = {
                updateCodelists: updateCodelists,
                saveChanges: saveChanges,
                addFreeEntryToSysList: addFreeEntryToSysList,
                addAllFreeEntriesToSysList: addAllFreeEntriesToSysList,
                replaceFreeEntryWithSysListEntry: replaceFreeEntryWithSysListEntry,
                exportCodelists: exportCodelists,
                importCodelists: importCodelists,
                startReindexJob: startReindexJob
            };

        });
        </script>
    </head>
    <body>
        <div class="contentBlockWhite content">
        <!-- CONTENT START -->
            <div id="winNavi" class="right">
                <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-4#overall-catalog-management-4', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
            </div>
            <span class="outer">
                <div>
                    <span id="importExportLink" class="functionalLink onTab"><img src="img/ic_fl_export.gif" width="11" height="10" alt="<fmt:message key="dialog.admin.catalog.management.codelists.export" />" /><a href="javascript:void(0);" onclick="pageCodelists.exportCodelists()" title="<fmt:message key="dialog.admin.catalog.management.codelists.export" /> [Popup]"><fmt:message key="dialog.admin.catalog.management.codelists.export" /></a><img src="img/ic_fl_import.gif" width="11" height="10" alt="<fmt:message key="dialog.admin.catalog.management.codelists.import" />" /><a href="javascript:void(0);" onclick="pageCodelists.importCodelists()" title="<fmt:message key="dialog.admin.catalog.management.codelists.import" /> [Popup]"><fmt:message key="dialog.admin.catalog.management.codelists.import" /></a></span>
                    <div id="codeListTabContainer" data-dojo-type="dijit/layout/TabContainer" style="height:520px; width: 100%;" selectedChild="codeListTab">
                        <!-- TAB 1 START -->
                        <div id="codeListTab" data-dojo-type="dijit.layout.BorderContainer" class="grey" title="<fmt:message key="dialog.admin.catalog.management.codelists.codelistTitle" />">
                                <div data-dojo-type="dijit/layout/ContentPane" region="top" class="grey">
                                <span class="outer"><div>
                                    <span class="label">
                                        <label for="selectionList" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8036)">
                                            <fmt:message key="dialog.admin.catalog.management.codelists.codelist" />
                                        </label>
                                    </span>
                                    <span class="input">
                                        <input data-dojo-type="dijit/form/Select" autocomplete="false" style="width:100%; margin:0.01px;" maxHeight="350" id="selectionList" />
                                    </span>
                                    </div>
                                </span>
                                <span class="outer"><div>
                                    <span id="codeListEditDisabledHint" style="visibility:hidden;" class="label">
                                        <label class="inActive">
                                            <fmt:message key="dialog.admin.catalog.management.codelists.note.unchangable.entry" />
                                        </label>
                                    </span>
                                    <span id="codeListDefaultDisabledHint" style="visibility:hidden;" class="label">
                                        <label class="inActive">
                                            <fmt:message key="dialog.admin.catalog.management.codelists.note.unset.default" />
                                        </label>
                                    </span>
                                    <div class="checkboxContainer">
                                        <span class="input spaceBelow">
                                            <input type="checkbox" onclick="require('ingrid/utils/UI').switchTableDisplay('codeListTable12', 'codeListTable11', require('dijit/registry').byId('selectionListDefault').checked);" id="selectionListDefault" data-dojo-type="dijit/form/CheckBox" />
                                            <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8037)"><fmt:message key="dialog.admin.catalog.management.codelists.setDefault" />
                                            </label>
                                        </span>
                                    </div>
                                    </div>
                                </span>
                                </div>
                                <div data-dojo-type="dijit/layout/ContentPane" region="center" class="innerPadding">
                                <div class="tableContainer" id="codeListTable11Container">
                                    <div id="codeListTable11" autoHeight="8" multiSelect="false">
                                    </div>
                                </div>
                                <div class="tableContainer" id="codeListTable12Container">
                                    <div id="codeListTable12" autoHeight="8" multiSelect="false" style="display:none">
                                    </div>
                                </div>
                                <span>
                                    <span style="float:right;">
                                        <button id="button_codelistSave" data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.management.codelists.save" />" onclick="pageCodelists.saveChanges()">
                                            <fmt:message key="dialog.admin.catalog.management.codelists.save" />
                                        </button>
                                    </span>
                                    <span id="codelistsLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden">
                                        <img src="img/ladekreis.gif" />
                                    </span>
                                    <span id="infoText" style="float:left; display:none;">
                                        <fmt:message key="dialog.admin.catalog.management.codelists.data.info" />
                                    </span>
                                </span>
                            </div>
                        </div><!-- TAB 1 END -->
                        <!-- TAB 2 START -->
                        <div id="freeEntryTab" data-dojo-type="dijit.layout.BorderContainer" class="grey" title="<fmt:message key="dialog.admin.catalog.management.codelists.freeEntriesTitle" />">
                                    <div data-dojo-type="dijit/layout/ContentPane" region="top">
                                        <span class="outer grey"><div>
                                            <span class="label">
                                                <label for="freeEntrySelectionList"><!-- onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8038, "<fmt:message key='dialog.admin.catalog.management.codelists.codelist' />")">-->
                                                    <fmt:message key="dialog.admin.catalog.management.codelists.codelist" />
                                                </label>
                                            </span>
                                            <span class="input"><input data-dojo-type="dijit/form/Select" autocomplete="false" id="freeEntrySelectionList" style="width:100%; margin:0.01px;"></span>
                                            </div>
                                        </span>
                                    </div>
                                    <div data-dojo-type="dijit/layout/ContentPane" region="left" class="innerPadding" style="width:370px;">
                                        <!-- <div data-dojo-type="dijit.layout.BorderContainer" style="height: 100%; border: 0;"> -->
                                            <!-- <div data-dojo-type="dijit/layout/ContentPane" region="top" class="grey"> -->
                                                <span class="label" style="height:37px;"><fmt:message key="dialog.admin.catalog.management.codelists.entriesNotInList" /></span>
                                            <!-- </div> -->
                                            <!-- <div data-dojo-type="dijit/layout/ContentPane" region="center" class="grey"> -->
                                                <div class="tableContainer">
                                                    <div id="freeEntryTable" autoHeight="16" contextMenu="none" forceGridHeight="true" multiSelect="false" class="hideTableHeader"></div>
                                                </div>
                                            <!-- </div> -->
                                         <!-- </div> -->
                                    </div>
                                    
                                    <div data-dojo-type="dijit/layout/ContentPane" region="center" class="grey">
                                        <div class="field"><span class="buttonCol" style="width:120px;margin:80px auto 0px; position:relative;">
                                                <button id="btnAddFreeEntry" data-dojo-type="dijit/form/Button" onclick="pageCodelists.addFreeEntryToSysList()">
                                                    &gt; <fmt:message key="dialog.admin.catalog.management.codelists.move" />
                                                </button>
                                                <button id="btnAddAllFreeEntry" data-dojo-type="dijit/form/Button" onclick="pageCodelists.addAllFreeEntriesToSysList()">
                                                    &gt;&gt; <fmt:message key="dialog.admin.catalog.management.codelists.moveAll" />
                                                </button>
                                                <button id="btnReplaceFreeEntry" data-dojo-type="dijit/form/Button" onclick="pageCodelists.replaceFreeEntryWithSysListEntry()">
                                                    &nbsp;&lt; <fmt:message key="dialog.admin.catalog.management.codelists.replace" />&nbsp;
                                                </button>
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <div data-dojo-type="dijit/layout/ContentPane" region="right"  class="innerPadding" style="width:370px;">
                                        <span class="label" style="height:37px;"><fmt:message key="dialog.admin.catalog.management.codelists.listContent" /></span>
                                        <div class="tableContainer">
                                            <div id="freeEntryCodelistTable" autoHeight="16" contextMenu="none" forceGridHeight="true" multiSelect="false" class="hideTableHeader"></div>
                                        </div>
                                    </div>
                                <div>
                                    <span><span id="codelistsFreeLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span></span>
                                </div>
                        </div><!-- TAB 2 END --><!-- TAB 3 START -->
                        <div id="reindexTab" data-dojo-type="dijit/layout/ContentPane" class="grey" title="<fmt:message key="dialog.admin.catalog.management.codelists.reindex" />">
                            <div class="inputContainer grey field">
                                <div class="innerPadding" style="border:1px solid grey;">
                                    <div><fmt:message key="dialog.admin.catalog.management.codelists.header" />:</div>
                                    <br/>
                                    <div><fmt:message key="dialog.admin.catalog.management.codelists.timestamp" />: <span id="updateCodelistsStatus"></span></div>
                                    <div><fmt:message key="dialog.admin.catalog.management.codelists.result" />: <span id="updateCodelistsResult">---</span></div>
                                    <span>
                                        <span style="float:right;">
                                            <button data-dojo-type="dijit/form/Button" onclick="pageCodelists.updateCodelists()">
                                                <fmt:message key="dialog.admin.catalog.management.codelists.updateCodelistsStart" />
                                            </button>
                                        </span>
                                        <span id="updateCodelistsLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span>
                                    </span>
                                    <div class="fill"></div>
                                </div>
                            </div>
                            <div class="inputContainer grey field">
                                <div class="innerPadding" style="border: 1px solid grey;">
                                    <div id="reindexJobInfo"></div>
                                    <br />
                                    <div id="reindexStart"></div>
                                    <div id="reindexEnd"></div>
                                    <div id="reindexStatus"></div>
                                    <div id="reindexNumEntities"></div>
                                    <span>
                                        <span style="float: right;">
                                            <button data-dojo-type="dijit/form/Button"
                                                onclick="pageCodelists.startReindexJob()">
                                                <fmt:message key="dialog.admin.catalog.management.codelists.reindexStart" />
                                            </button>
                                        </span>
                                        <span id="reindexLoadingZone" style="float: right; margin-top: 1px; z-index: 100; visibility: hidden">
                                            <img src="img/ladekreis.gif" />
                                        </span>
                                    </span>
                                    <div class="fill"></div>
                                </div>
                          </div>
                    </div>
                        </div>
                    </div><!-- TAB 3 END -->
                </div>
            </span>
        <!-- CONTENT END -->
        </div>
    </body>
</html>
