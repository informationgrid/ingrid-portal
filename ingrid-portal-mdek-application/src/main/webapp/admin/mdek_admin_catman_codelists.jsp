<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <script type="text/javascript">
            dojo.require("dijit.layout.TabContainer");
            dojo.require("dijit.form.CheckBox");
            dojo.require("dojo.date.locale");
            var scriptScopeCodeLists = _container_;
            
            var MAINTAINABLE_LIST_IDS = [];//100, 101, 102, 515, 518, 520, 523, 526, 528, 1100, 1320, 1350, 1370, 3385, 3535, 3555, 6005];
            var CAN_SET_DEFAULT_LIST_IDS = [1350, 1370];
            var IDS_WITH_DATA_COLUMNS = ["1100"];

			createDOMElements();
			            
            dojo.connect(_container_, "onLoad", function(){
                initImportExportLink();
                
                initCodelistSelect();
                initCodelistTables();
                initFreeEntrySelect();
                initFreeEntryTables();
                dijit.byId("contentPane").resize();
                refreshReindexProcessInfo();
            });
            
			function createDOMElements() {
				var codeListTable11Structure = [
					{field: 'entryId',name: "<fmt:message key='dialog.admin.catalog.management.codelists.id' />",width: '32px'},
					{field: 'deName',name: "<fmt:message key='dialog.admin.catalog.management.codelists.germanName' />",width: '300px', editable:true},
					{field: 'enName',name: "<fmt:message key='dialog.admin.catalog.management.codelists.englishName' />",width: '300px', editable:true},
					{field: 'data',name: "<fmt:message key='dialog.admin.catalog.management.codelists.data' />",width: '300px', editable:true}
				];
				createDataGrid("codeListTable11", null, codeListTable11Structure, null);
				
				var codeListTable12Structure = [
					{field: 'entryId',name: "<fmt:message key='dialog.admin.catalog.management.codelists.id' />",width: '32px'},
					{field: 'deName',name: "<fmt:message key='dialog.admin.catalog.management.codelists.germanName' />",width: '269px', editable:true},
					{field: 'enName',name: "<fmt:message key='dialog.admin.catalog.management.codelists.englishName' />",width: '269px', editable:true},
					{field: 'isDefault',name: 'isDefault',width: 'auto', editor: YesNoCheckboxCellEditor, formatter:BoolCellFormatter, editable:true},
                    {field: 'data',name: "<fmt:message key='dialog.admin.catalog.management.codelists.data' />",width: '300px', editable:true}
				];
				createDataGrid("codeListTable12", null, codeListTable12Structure, null);
				
				var freeEntryTableStructure = [
					{field: 'title',name: 'title',width: '300px'}
				];
				createDataGrid("freeEntryTable", null, freeEntryTableStructure, null);
                //dijit.byId("freeEntryTable").selectionMode = "single";
				
				var freeEntryCodelistTableStructure = [
					{field: 'deName',name: 'deName',width: '300px'}
				];
				createDataGrid("freeEntryCodelistTable", null, freeEntryCodelistTableStructure, null);
				//dijit.byId("freeEntryCodelistTable").selectionMode = "single";
                
                dojo.connect(dijit.byId("codeListTable12"), "onCellChange", function(msg) {
                    console.debug(msg);
                    console.debug(this);
                    if (msg.cell == 3 && msg.item.isDefault) {
                        // remove true default value from any other row!
                        dojo.forEach(this.getData(), function(item, row) {
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
                var tabContainer = dijit.byId("codeListTab");
                dojo.connect(tabContainer, "onShow", function(){
                    dojo.byId("importExportLink").style.visibility = "visible";
                });
                dojo.connect(tabContainer, "onHide", function(){
                    dojo.byId("importExportLink").style.visibility = "hidden";
                });
            }
            
            function initCodelistSelect(){
                var selectWidget = dijit.byId("selectionList");
                
                var def = getAllSysListInfosDef();
                def.addCallback(function(listInfos){
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
                });
                def.addErrback(function(error){
                    displayErrorMessage(error);
                    console.debug("Error: " + error);
                });
                
                // On value changed load the selected sysList from the backend and update the table
                dojo.connect(selectWidget, "onChange", function(value){
                    if (value) {
                        var germanListDef = getSysListDef(value, "de");
                        var englishListDef = getSysListDef(value, "en");
                        
                        if (dojo.some(MAINTAINABLE_LIST_IDS, function(listId){
                            return listId == parseInt(value);
                        })) {
                            console.debug("enable. (" + parseInt(value) + ")");
                            UtilGrid.updateOption("codeListTable11", "editable", true);
                            UtilGrid.updateOption("codeListTable11", "enableAddRow", true);
                            UtilGrid.updateOption("codeListTable12", "editable", true);
                            UtilGrid.updateOption("codeListTable12", "enableAddRow", true);
                            // enable context menu
                            contextMenu.DEFAULT.bindDomNode(dojo.byId("codeListTable11"));
                            contextMenu.DEFAULT.bindDomNode(dojo.byId("codeListTable12"));
                            hideEditDisabledHint();
                            
                        }
                        else {
                            console.debug("disable. (" + parseInt(value) + ")");
                            UtilGrid.updateOption("codeListTable11", "editable", false);
                            UtilGrid.updateOption("codeListTable11", "enableAddRow", false);
                            UtilGrid.updateOption("codeListTable12", "editable", false);
                            UtilGrid.updateOption("codeListTable12", "enableAddRow", false);
                            // disable context menu
                            contextMenu.DEFAULT.unBindDomNode(dojo.byId("codeListTable11"));
                            contextMenu.DEFAULT.unBindDomNode(dojo.byId("codeListTable12"));
                            showEditDisabledHint();
                        }
                        
                        var defList = new dojo.DeferredList([germanListDef, englishListDef], false, false, true);
                        defList.addCallback(function(resultList){
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
                            dijit.byId("codeListTable11").showColumn("data");
                            dojo.style("infoText", "display", "block");
                        } else {
                            dijit.byId("codeListTable11").hideColumn("data");
                            dojo.style("infoText", "display", "none");
                        }
                    }
                });
            }
            
            function initCodelistTables(){
                // Use the same store for both tables. The First table has to be reinitialised so the new store
                // gets registered properly
                var mainStore = UtilGrid.getTableData("codeListTable12");//.store;
                UtilGrid.setTableData("codeListTable11", mainStore);
                
                // We need to connect 'before' the function is called so the field is updated properly
                // by the filteringTable
                //dojo.connect(mainStore, "onAddData", function(obj){
                /*dojo.connect(UtilGrid.getTable("codeListTable11"), "onAddNewRow", function() {
                    var radioButton = createHtmlForRadio("codeListRadio", "codeListRadio_" + (new Date()).getTime(), false);
                    //obj.src.isDefault = radioButton;
                });
                dojo.connect(UtilGrid.getTable("codeListTable12"), "onAddNewRow", function() {
                    var radioButton = createHtmlForRadio("codeListRadio", "codeListRadio_" + (new Date()).getTime(), false);
                    //obj.src.isDefault = radioButton;
                });*/
                
                
                // Helper function that displays a simple dialog with 'text' as content. There are two buttons 'yes' and 'no'.
                // If the user clicks yes, the invocation is triggered. Otherwise nothing happens.
                var askUserAndInvokeOrCancel = function(text, invocation){
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
                }
                // Register functions 'around' deleteItem and deleteSelectedItems from the table context menu
                /*var contextMenu1 = dijit.byId("codeListTable11").getContextMenu();
                 var contextMenu2 = dijit.byId("codeListTable12").getContextMenu();
                 var displayText = "<fmt:message key='dialog.admin.catalog.management.codelist.deleteSingleHint' />";
                 //!!! connect "around" ???
                 dojo.connect(contextMenu1, "deleteItemClicked", dojo.lang.curry(this, askUserAndInvokeOrCancel, displayText));
                 dojo.connect(contextMenu2, "deleteItemClicked", dojo.lang.curry(this, askUserAndInvokeOrCancel, displayText));
                 displayText = "<fmt:message key='dialog.admin.catalog.management.codelist.deleteMultipleHint' />";
                 dojo.connect(contextMenu1, "deleteSelectedItemsClicked", dojo.lang.curry(this, askUserAndInvokeOrCancel, displayText));
                 dojo.connect(contextMenu2, "deleteSelectedItemsClicked", dojo.lang.curry(this, askUserAndInvokeOrCancel, displayText));
                 */
                
                
            }
            
            
            // Retrieve all sysList ids stored in the backend
            // A list of the following form is returned:
            // [ listId1, listId2, ... ]
            function getAllSysListInfosDef(){
                var def = new dojo.Deferred();
                CatalogService.getAllSysListInfos({
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(listInfos){
                        def.callback(listInfos);
                    },
                    errorHandler: function(msg, err){
                        hideLoadingZone();
                        displayErrorMessage(err);
                        console.debug("Error: " + msg);
                        def.errback(err);
                    }
                });
                return def;
            }
            
            // Retrieve the sysList for the given listId and languageCode from the backend
            // A list of the following form is returned:
            // [ [listEntry, entryId, isDefault], [...] ]
            function getSysListDef(listId, languageCode){
                var def = new dojo.Deferred();
                CatalogService.getSysLists([listId], languageCode, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(listItems){
                        def.callback(listItems[listId]);
                    },
                    errorHandler: function(msg, err){
                        hideLoadingZone();
                        displayErrorMessage(err);
                        console.debug("Error: " + msg);
                        def.errback(err);
                    }
                });
                return def;
            }
            
            // Merge two lists into one. The lists must have the following format:
            // [ { entryId:entryId, name:entryName isDefault:default }, {...} ]
            // The result is of the format:
            // [ { entryId:entryId, deName:entryName, enName:entryName, isDefault:default }, {...} ]
            function mergeTableData(germanList, englishList){
                // Create a hash map referenced by entry ids
                var idEntryMap = {};
                
                // Fill the hash map with all german entries
                for (var index = 0; index < germanList.length; index++) {
                    var currentEntry = germanList[index];
                    idEntryMap[currentEntry.entryId] = {
                        deName: currentEntry.name,
                        entryId: currentEntry.entryId,
                        isDefault: currentEntry.isDefault,
                        data: currentEntry.data
                    };
                }
                
                // add the english name if the corresponding entry id was found in the hash
                // otherwise create a new entry
                for (var index = 0; index < englishList.length; index++) {
                    var currentEntry = englishList[index];
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
                var sysListId = parseInt(dijit.byId("selectionList").getValue());
                
                if (dojo.some(CAN_SET_DEFAULT_LIST_IDS, function(listId){
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
                    data[index].isDefault = data[index].isDefault;//createHtmlForRadio("codeListRadio", "codeListRadio_" + data[index].entryId, data[index].isDefault);
                }
                // Both stores are the same
                //dijit.byId("codeListTable11").store.setData(data);
                UtilGrid.setTableData("codeListTable11", data);
                UtilGrid.setTableData("codeListTable12", data);
            }
            
            function createHtmlForRadio(name, id, checked){
                return "<input type='radio' " +
                "class='radio' " +
                "name='" +
                name +
                "' " +
                "id='" +
                id +
                "' " +
                (checked ? "checked='checked' " : " ") +
                "/>";
            }
            
            // Check if the given list of objects contains an object with property 'isDefault' == true
            function hasDefaultEntry(data){
                return dojo.some(data, function(item){
                    return item.isDefault == true;
                });
            }
            
            function showDefaultRadioButtons(){
                dijit.byId("selectionListDefault").setValue(true);
                switchTableDisplay("codeListTable12", "codeListTable11", true);
            }
            
            function hideDefaultRadioButtons(){
                dijit.byId("selectionListDefault").setValue(false);
                switchTableDisplay("codeListTable12", "codeListTable11", false);
            }
            
            function enableDefaultCheckbox(){
                dojo.byId("codeListDefaultDisabledHint").style.visibility = "hidden";
                dijit.byId("selectionListDefault").setDisabled(false);
            }
            
            function disableDefaultCheckbox(){
                dojo.byId("codeListDefaultDisabledHint").style.visibility = "visible";
                dijit.byId("selectionListDefault").setDisabled(true);
            }
            
            // Get the modified data and send it to the server
            scriptScopeCodeLists.saveChanges = function(){
                var selectedChild = dijit.byId("codeListTabContainer").selectedChildWidget.id;
                
                if ("codeListTab" == selectedChild) {
                    saveChangesCodelist();
                    
                }
                else 
                    if ("freeEntryTab" == selectedChild) {
                        saveChangesFreeEntry();
                    }
            }
            
            
            function saveChangesCodelist(){
                var sysListId = dijit.byId("selectionList").getValue();
                
                var isValid = validateCodelist(sysListId);
                
                if (sysListId && isValid) {
                    var setDefault = dijit.byId("selectionListDefault").checked;
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
                        console.debug("entry id : " + currentEntry.entryId);
                        entryIds.push(currentEntry.entryId);
                        console.debug("name (de): " + currentEntry.deName);
                        entriesGerman.push(currentEntry.deName);
                        console.debug("name (en): " + currentEntry.enName);
                        entriesEnglish.push(currentEntry.enName);
                        console.debug("isDefault: " + isDefault);
                        data.push(currentEntry.data);
                        if (isDefault) {
                            defaultIndex = index;
                        }
                    }
                    
                    // Send data to the db
                    // TODO Implement List of maintainable sysLists
                    var maintainable = dojo.some(MAINTAINABLE_LIST_IDS, function(listId){
                        return listId == parseInt(sysListId);
                    });
                    var def = storeSysListDef(sysListId, maintainable, defaultIndex, entryIds, entriesGerman, entriesEnglish, data);
                    def.addCallback(function(){
                        // Show a 'success' message
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.admin.catalog.management.codelist.storeSuccess' />", dialog.INFO);
                        
                        // Update the frontend after the list has been stored
                        var selectWidget = dijit.byId("selectionList");
                        //selectWidget.setValue(sysListId);
                        dijit.byId("selectionList").onChange()
                    });
                }
                
                if (!isValid) {
                    dojo.style("validationError", "display", "block");
                } else {
                    dojo.style("validationError", "display", "none");
                }
            }
            
            function validateCodelist(sysListId) {
                if (sysListId == "1100") {
                    // check all data columns for valid entries
                    var data = UtilGrid.getTableData("codeListTable11");
                    var isValid = true;
                    dojo.forEach(data, function(row) {
                        var splittedData = row.data.split(" ");
                        if (splittedData.length === 1 && splittedData[0] != "") {
                            isValid = false;
                        } else if (splittedData.length === 4) {
                            var allValid = dojo.every(splittedData, function(item) { return item.match(/\d*\.?\d*/)[0] == item;})
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
            function isMarkedAsDefaultEntry(entry){
                // Extract the id of the corresponding radio button. In most cases it's equal to entryId
                // New entries don't have an entryId so we have to locate it another way
                // Here we get the id from the string used to create the html radio button
                var isDefaultStr = entry.isDefault;
                var entryId = isDefaultStr.match(/codeListRadio_(\d+)'/)[1];
                
                return dojo.byId("codeListRadio_" + entryId).checked;
            }
            
            // Store a modified sysList in the db.
            // listId - Id of the sysList to store
            // maintainable - a boolean flag signaling if the sysList is allowed to be modified
            // defaultIndex - the index of the default entry. Null if no entry should have a default value
            // entryIds - Ids of the entries as Int List. Null if it's a new entry
            // entriesGerman, entriesEnglish - The Entries as String Lists
            function storeSysListDef(listId, maintainable, defaultIndex, entryIds, entriesGerman, entriesEnglish, data){
                var def = new dojo.Deferred();
                CatalogService.storeSysList(listId, maintainable, defaultIndex, entryIds, entriesGerman, entriesEnglish, data, {
                    callback: function(res){
                        console.debug("result:" + res);
                        def.callback(res);
                    },
                    errorHandler: function(msg, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + msg);
                        def.errback();
                    }
                });
                return def;
            }
            
            
            function showLoadingZone(){
                dojo.byId("codelistsLoadingZone").style.visibility = "visible";
                dojo.byId("codelistsFreeLoadingZone").style.visibility = "visible";
            }
            
            function hideLoadingZone(){
                dojo.byId("codelistsLoadingZone").style.visibility = "hidden";
                dojo.byId("codelistsFreeLoadingZone").style.visibility = "hidden";
            }
            
            function showEditDisabledHint(){
                dojo.byId("codeListEditDisabledHint").style.visibility = "visible";
            }
            
            function hideEditDisabledHint(){
                dojo.byId("codeListEditDisabledHint").style.visibility = "hidden";
            }
            
            
            
            // Functions for the second div. Adding free entries to a list / overwriting free entries with sysList entries
            
            function initFreeEntrySelect(){
                var selectWidget = dijit.byId("freeEntrySelectionList");
                
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
                dojo.connect(selectWidget, "onChange", reloadFreeEntryCodelistsDef);
                
                // Initially set the select widget value to the first one in the list
                selectWidget.setValue(listIds[0]);
            }
            
            function reloadFreeEntryCodelistsDef(listId){
                var def = new dojo.Deferred();
                
                if (listId) {
                    var germanListDef = getSysListDef(listId, "de");
                    var englishListDef = getSysListDef(listId, "en");
                    var freeEntryDef = getFreeEntriesDef(listId);
                    
                    var defList = new dojo.DeferredList([germanListDef, englishListDef, freeEntryDef], false, false, true);
                    defList.addCallback(function(resultList){
                        var germanList = resultList[0][1];
                        var englishList = resultList[1][1];
                        var freeList = resultList[2][1];
                        
                        var germanData = UtilSyslist.convertSysListToTableData(germanList);
                        var englishData = UtilSyslist.convertSysListToTableData(englishList);
                        
                        var mergedData = mergeTableData(germanData, englishData);
                        updateFreeEntryCodelistTable(mergedData);
                        
                        updateFreeEntryTable(freeList);
                        
                        def.callback();
                    });
                    defList.addErrback(function(err){
                        def.errback();
                    });
                }
            }
            
            function initFreeEntryTables(){
                //dijit.byId("freeEntryCodelistTable").removeContextMenu();
                //dijit.byId("freeEntryTable").removeContextMenu();
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
                var def = new dojo.Deferred();
                CatalogService.getFreeListEntries(mdekListId, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(entries){
                        def.callback(entries);
                    },
                    errorHandler: function(msg, err){
                        hideLoadingZone();
                        displayErrorMessage(err);
                        console.debug("Error: " + msg);
                        def.errback(err);
                    }
                });
                return def;
            }
            
            
            scriptScopeCodeLists.addFreeEntryToSysList = function(){
                var freeEntry = UtilGrid.getSelectedData("freeEntryTable")[0];
                
                if (freeEntry) {
                    var def = new dojo.Deferred();
                    //var titles = freeEntry.map(function(item) {return item.title[0];});
                    var displayText = dojo.string.substitute("<fmt:message key='dialog.admin.catalog.management.codelist.freeEntryToSysListEntry' />", [freeEntry.title]);
                    dialog.show("<fmt:message key='general.error' />", displayText, dialog.INFO, [{
                        caption: "<fmt:message key='general.no' />",
                        action: function(){
                            def.errback("CANCEL");
                        }
                    }, {
                        caption: "<fmt:message key='general.yes' />",
                        action: function(){
                            def.callback();
                        }
                    }]);
                    
                    def.addCallback(function(){
                        return addFreeEntryToSysListDef(freeEntry);
                    });
                    def.addCallback(function(){
                        // Show a 'success' message
                        dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.codelist.freeEntryToSysListEntrySuccess' />", dialog.INFO);
                        
                        // Update the frontend after the list has been stored
                        var selectWidget = dijit.byId("freeEntrySelectionList");
                        //selectWidget.setValue(selectWidget.getValue());
                        reloadFreeEntryCodelistsDef(selectWidget.getValue());
                    });
                    
                    def.addErrback(function(err){
                        if (err != "CANCEL") {
                            displayErrorMessage(err);
                            console.debug("Error: " + err);
                        }
                    });
                }
            }
            
            scriptScopeCodeLists.addAllFreeEntriesToSysList = function(){
                var freeData = UtilGrid.getTableData("freeEntryTable");
                var sysListId = dijit.byId("freeEntrySelectionList").getValue();
                
                if (freeData && freeData.length > 0) {
                
                    var freeEntryTitles = [];
                    dojo.forEach(freeData, function(freeEntry){
                        freeEntryTitles.push(freeEntry.title);
                    });
                    var def = new dojo.Deferred();
                    var displayText = dojo.string.substitute("<fmt:message key='dialog.admin.catalog.management.codelist.freeEntriesToSysListEntries' />", [freeEntryTitles.join(", ")]);
                    dialog.show("<fmt:message key='general.hint' />", displayText, dialog.INFO, [{
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
                    
                    freeData = freeData.reverse();
                    while (freeData.length > 0) {
                        var freeEntry = freeData.pop();
                        (function(entry){
                            def.addCallback(function(){
                                return addFreeEntryToSysListDef(entry);
                            });
                            //				def.addCallback(function() { return reloadFreeEntryCodelistsDef(sysListId); });
                        })(freeEntry)
                    }
                    
                    def.addCallback(function(){
                        // Show a 'success' message
                        dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.codelist.freeEntriesToSysListEntriesSuccess' />", dialog.INFO);
                        
                        // Update the frontend after the list has been stored
                        var selectWidget = dijit.byId("freeEntrySelectionList");
                        //selectWidget.setValue(selectWidget.getValue());
                        reloadFreeEntryCodelistsDef(selectWidget.getValue());
                    });
                    
                    def.addErrback(function(err){
                        if (err != "CANCEL") {
                            displayErrorMessage(err);
                            console.debug("Error: " + err);
                        }
                    });
                }
            }
            
            function addFreeEntryToSysListDef(freeEntry){
                var addFreeEntryDef = new dojo.Deferred();
                
                var codelistData = UtilGrid.getTableData("freeEntryCodelistTable");
                var sysListId = dijit.byId("freeEntrySelectionList").getValue();
                
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
                    
                    if (sysListEntry != null) {
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
                        
                        def = saveChangesFreeEntryDef();
                        def.addCallback(function(){
                            return getSysListEntryDef(sysListId, freeEntry.title);
                        });
                        def.addCallback(function(newSysListEntry){
                            if (newSysListEntry == null || newSysListEntry == undefined) {
                                console.debug("syslist is null!");
                                return;
                            }
                            newEntry.entryId = newSysListEntry[1];
                            return replaceFreeEntryWithSysListEntryDef(freeEntry.title, newSysListEntry[1], newSysListEntry[0], sysListId);
                        });
                    }
                    
                    def.addCallback(function(){
                        addFreeEntryDef.callback();
                    });
                    def.addErrback(function(err){
                        addFreeEntryDef.errback(err);
                    })
                    
                }
                else {
                    addFreeEntryDef.errback("No free entry selected!");
                }
                
                return addFreeEntryDef;
            }
            
            
            function getSysListEntryDef(listId, title){
                var def = getSysListDef(listId, "de");
                def.addCallback(function(sysList){
                    for (var index = 0; index < sysList.length; ++index) {
                        if (sysList[index][0] == title) {
                            return sysList[index];
                        }
                    }
                    return null;
                });
                
                return def;
            }
            
            
            function replaceFreeEntryWithSysListEntryDef(freeEntry, sysListEntryId, sysListEntryName, sysListId){
                var def = new dojo.Deferred();
                
                CatalogService.replaceFreeEntryWithSysListEntry(freeEntry, sysListId, sysListEntryId, sysListEntryName, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: function(){
                        def.callback();
                    },
                    errorHandler: function(msg, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + msg);
                        def.errback(err);
                    }
                });
                
                return def;
            }
            
            
            scriptScopeCodeLists.replaceFreeEntryWithSysListEntry = function(){
                var sysListId = dijit.byId("freeEntrySelectionList").getValue();
                
                var codelistEntry = UtilGrid.getSelectedData("freeEntryCodelistTable")[0];
                var freeEntry = UtilGrid.getSelectedData("freeEntryTable")[0];
                
                if (codelistEntry && freeEntry) {
                    var def = new dojo.Deferred();
                    var displayText = dojo.string.substitute("<fmt:message key='dialog.admin.catalog.management.codelist.replaceFreeEntryWithSysListEntry' />", [freeEntry.title, codelistEntry.deName]);
                    dialog.show("<fmt:message key='general.hint' />", displayText, dialog.INFO, [{
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
                    
                    def.addCallback(function(){
                        return replaceFreeEntryWithSysListEntryDef(freeEntry.title, codelistEntry.entryId, codelistEntry.deName, sysListId);
                    });
                    
                    def.addCallback(function(){
                        // Show a 'success' message
                        dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.admin.catalog.management.codelist.replaceFreeEntryWithSysListEntrySuccess' />", dialog.INFO);
                        
                        // Update the frontend after the list has been stored
                        var selectWidget = dijit.byId("freeEntrySelectionList");
                        //selectWidget.setValue(selectWidget.getValue());
                        reloadFreeEntryCodelistsDef(selectWidget.getValue());
                    });
                    
                    def.addErrback(function(err){
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
                var def = new dojo.Deferred();
                
                var sysListId = dijit.byId("freeEntrySelectionList").getValue();
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
                    var maintainable = dojo.some(MAINTAINABLE_LIST_IDS, function(listId){
                        return listId == parseInt(sysListId);
                    });
                    var sysListDef = storeSysListDef(sysListId, maintainable, defaultIndex, entryIds, entriesGerman, entriesEnglish, data);
                    sysListDef.addCallback(function(){
                        def.callback();
                    });
                    sysListDef.addErrback(function(err){
                        displayErrorMessage(err);
                        console.debug("Error: " + err);
                        def.errback(err);
                    });
                }
                
                return def;
            }
            
            
            // -- Reindex Tab Functions --
            scriptScopeCodeLists.startReindexJob = function(){
                CatalogManagementService.rebuildSysListData({
                    timeout: 5000,
                    preHook: showReindexLoadingZone,
                    callback: function(res){
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
            
            refreshReindexProcessInfo = function(){
                console.debug("call getRebuildSysListDataJobInfo");
                CatalogManagementService.getRebuildSysListDataJobInfo({
                    callback: function(jobInfo){
                        updateReindexJobInfo(jobInfo);
                        if (!jobFinished(jobInfo)) {
                            setTimeout("refreshReindexProcessInfo()", 1000);
                            
                        }
                        else {
                            hideReindexLoadingZone();
                        }
                    },
                    errorHandler: function(message, err){
                        displayErrorMessage(err);
                        console.debug("Error: " + message);
                        // If there's a timeout try again
                        if (err.message != "USER_LOGIN_ERROR") {
                            setTimeout("refreshReindexProcessInfo()", 1000);
                        }
                    }
                });
            }
            
            function jobFinished(jobInfo){
                //console.debug("start: " + jobInfo.startTime + " . end: " + jobInfo.endTime + " . exception: " + jobInfo.exception);
                return (jobInfo.startTime == null || jobInfo.endTime != null || jobInfo.exception != null);
            }
            
            function updateReindexJobInfo(jobInfo){
                if (jobFinished(jobInfo)) {
                    dojo.byId("reindexJobInfo").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexLastProcessInfo' />" + ":";
                    
                    if (jobInfo.startTime == null) {
                        // Job has never been started...
                        dojo.byId("reindexStart").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexStart' />";
                        dojo.byId("reindexEnd").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexEnd' />";
                        
                    }
                    else {
                        dojo.byId("reindexStart").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexStart' />" + ": " + UtilString.getDateString(jobInfo.startTime, "EEEE, dd. MMMM yyyy HH:mm:ss");
                        dojo.byId("reindexEnd").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexEnd' />" + ": " + UtilString.getDateString(jobInfo.endTime, "EEEE, dd. MMMM yyyy HH:mm:ss");
                    }
					dojo.style("reindexStatus", "display", "none")
					dojo.style("reindexNumEntities", "display", "none")
					dojo.style("reindexEnd", "display", "block")
                
                }
                else {
                    dojo.byId("reindexJobInfo").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexCurrentProcessInfo' />" + ":";
                    dojo.byId("reindexStart").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexStart' />" + ": " + UtilString.getDateString(jobInfo.startTime, "EEEE, dd. MMMM yyyy HH:mm:ss");
                    dojo.byId("reindexStatus").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexState' />" + ": " + jobInfo.description;
                    dojo.byId("reindexNumEntities").innerHTML = "<fmt:message key='dialog.admin.catalog.management.codelist.reindexDatasets' />" + ": " + jobInfo.numProcessedEntities + " / " + jobInfo.numEntities;
					dojo.style("reindexStatus", "display", "block")
					dojo.style("reindexNumEntities", "display", "block")
					dojo.style("reindexEnd", "display", "none")
                }
            }
            
            
            function showReindexLoadingZone(){
                dojo.byId("reindexLoadingZone").style.visibility = "visible";
            }
            
            function hideReindexLoadingZone(){
                dojo.byId("reindexLoadingZone").style.visibility = "hidden";
            }
            
            // -- Import / Export --
            scriptScopeCodeLists.importCodelists = function(){
                var deferred = new dojo.Deferred();
                
                deferred.addCallback(function(inputFile){
                
                    if (inputFile) {
                        var askUserDef = new dojo.Deferred();
                        var displayText = "<fmt:message key='dialog.admin.catalog.management.codelist.importHint' />";
                        dialog.show("<fmt:message key='general.hint' />", displayText, dialog.INFO, [{
                            caption: "<fmt:message key='general.no' />",
                            action: function(){
                                askUserDef.errback("CANCEL");
                            }
                        }, {
                            caption: "<fmt:message key='general.yes' />",
                            action: function(){
                                askUserDef.callback();
                            }
                        }]);
                        
                        askUserDef.addCallback(function(){
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
                    errorHandler: function(errMsg, err){
                        dialog.show("<fmt:message key='general.error' />", dojo.string.substitute("<fmt:message key='dialog.generalError' />", [errMsg]), dialog.WARNING, null, 350, 350);
                        console.debug("Error: " + errMsg);
                    }
                });
            }
            
            scriptScopeCodeLists.exportCodelists = function(){
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
            
            
        </script>
    </head>
    <body>
        <div class="contentBlockWhite content">
        <!-- CONTENT START -->
            <div id="winNavi">
                <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-4#overall-catalog-management-4', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
            <br>
            <br>
                    <div class="content"><span id="importExportLink" class="functionalLink onTab" style="right: 27px;"><img src="img/ic_fl_export.gif" width="11" height="10" alt="<fmt:message key="dialog.admin.catalog.management.codelists.export" />" /><a href="javascript:void(0);" onclick="javascript:scriptScopeCodeLists.exportCodelists();" title="<fmt:message key="dialog.admin.catalog.management.codelists.export" /> [Popup]"><fmt:message key="dialog.admin.catalog.management.codelists.export" /></a><img src="img/ic_fl_import.gif" width="11" height="10" alt="<fmt:message key="dialog.admin.catalog.management.codelists.import" />" /><a href="javascript:void(0);" onclick="javascript:scriptScopeCodeLists.importCodelists();" title="<fmt:message key="dialog.admin.catalog.management.codelists.import" /> [Popup]"><fmt:message key="dialog.admin.catalog.management.codelists.import" /></a></span></div>
                    <div id="codeListTabContainer" dojoType="dijit.layout.TabContainer" style="height:520px;" selectedChild="codeListTab">
                        <!-- TAB 1 START -->
                        <div id="codeListTab" dojoType="dijit.layout.BorderContainer" class="grey" title="<fmt:message key="dialog.admin.catalog.management.codelists.codelistTitle" />">
                                <div dojoType="dijit.layout.ContentPane" region="top" class="grey">
                                <span class="outer"><div>
                                    <span class="label">
                                        <label for="selectionList" onclick="javascript:dialog.showContextHelp(arguments[0], 8036)">
                                            <fmt:message key="dialog.admin.catalog.management.codelists.codelist" />
                                        </label>
                                    </span>
                                    <span class="input">
                                        <input dojoType="dijit.form.Select" autocomplete="false" style="width:100%; margin:0.01px;" maxHeight="350" id="selectionList" />
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
                                            <input type="checkbox" onclick="switchTableDisplay('codeListTable12', 'codeListTable11', dijit.byId('selectionListDefault').checked);" id="selectionListDefault" dojoType="dijit.form.CheckBox" />
                                            <label onclick="javascript:dialog.showContextHelp(arguments[0], 8037)"><fmt:message key="dialog.admin.catalog.management.codelists.setDefault" />
                                            </label>
                                        </span>
                                    </div>
                                    </div>
                                </span>
                                </div>
                                <div dojoType="dijit.layout.ContentPane" region="center" class="innerPadding">
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
                                        <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.management.codelists.save" />" onClick="javascript:scriptScopeCodeLists.saveChanges();">
                                            <fmt:message key="dialog.admin.catalog.management.codelists.save" />
                                        </button>
                                    </span>
                                    <span id="codelistsLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden">
                                        <img src="img/ladekreis.gif" />
                                    </span>
                                    <span id="infoText" style="float:left; display:none;">
                                        <fmt:message key="dialog.admin.catalog.management.codelists.data.info" />
                                    </span>
                                    <span id="validationError" class="error" style="clear: both; display:none;">
                                        <fmt:message key="dialog.admin.catalog.management.codelists.validation.error" />
                                    </span>
                                </span>
                            </div>
                        </div><!-- TAB 1 END -->
                        <!-- TAB 2 START -->
                        <div id="freeEntryTab" dojoType="dijit.layout.BorderContainer" class="grey" title="<fmt:message key="dialog.admin.catalog.management.codelists.freeEntriesTitle" />">
                                    <div dojoType="dijit.layout.ContentPane" region="top">
                                        <span class="outer grey"><div>
                                            <span class="label">
                                                <label for="freeEntrySelectionList"><!-- onclick="javascript:dialog.showContextHelp(arguments[0], 8038, "<fmt:message key='dialog.admin.catalog.management.codelists.codelist' />")">-->
                                                    <fmt:message key="dialog.admin.catalog.management.codelists.codelist" />
                                                </label>
                                            </span>
                                            <span class="input"><input dojoType="dijit.form.Select" autocomplete="false" id="freeEntrySelectionList" style="width:100%; margin:0.01px;"></span>
                                            </div>
                                        </span>
                                    </div>
                                    <div dojoType="dijit.layout.ContentPane" region="left" class="innerPadding" style="width:370px;">
                                        <div dojoType="dijit.layout.BorderContainer">
                                            <div dojoType="dijit.layout.ContentPane" region="top" class="grey">
                                                <span class="label" style="height:37px;"><fmt:message key="dialog.admin.catalog.management.codelists.entriesNotInList" /></span>
                                            </div>
                                            <div dojoType="dijit.layout.ContentPane" region="center" class="grey">
                                                <div class="tableContainer">
                                                    <div id="freeEntryTable" autoHeight="13" forceGridHeight="false" multiSelect="false" class="hideTableHeader"></div>
                                                </div>
                                            </div>
                                         </div>
                                    </div>
                                    
                                    <div dojoType="dijit.layout.ContentPane" region="center" class="grey">
                                        <div class="field"><span class="buttonCol" style="width:120px;margin:80px auto 0px; position:relative;">
                                                <button dojoType="dijit.form.Button" onClick="javascript:scriptScopeCodeLists.addFreeEntryToSysList();">
                                                    &gt; <fmt:message key="dialog.admin.catalog.management.codelists.move" />
                                                </button>
                                                <button dojoType="dijit.form.Button" onClick="javascript:scriptScopeCodeLists.addAllFreeEntriesToSysList();">
                                                    &gt;&gt; <fmt:message key="dialog.admin.catalog.management.codelists.moveAll" />
                                                </button>
                                                <button dojoType="dijit.form.Button" onClick="javascript:scriptScopeCodeLists.replaceFreeEntryWithSysListEntry();">
                                                    &nbsp;&lt; <fmt:message key="dialog.admin.catalog.management.codelists.replace" />&nbsp;
                                                </button>
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <div dojoType="dijit.layout.ContentPane" region="right"  class="innerPadding" style="width:370px;">
                                        <div dojoType="dijit.layout.BorderContainer">
                                            <div dojoType="dijit.layout.ContentPane" region="top" class="grey">
                                                <span class="label" style="height:37px;"><fmt:message key="dialog.admin.catalog.management.codelists.listContent" /></span>
                                            </div>
                                            <div dojoType="dijit.layout.ContentPane" region="center">
                                                <div class="tableContainer">
                                                    <div id="freeEntryCodelistTable" autoHeight="13" forceGridHeight="false" multiSelect="false" class="hideTableHeader"></div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                <div>
                                    <span><span id="codelistsFreeLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span></span>
                                </div>
                        </div><!-- TAB 2 END --><!-- TAB 3 START -->
                        <div id="reindexTab" dojoType="dijit.layout.ContentPane" class="grey" title="<fmt:message key="dialog.admin.catalog.management.codelists.reindex" />">
                            <div class="inputContainer grey field">
                                <div class="innerPadding" style="border:1px solid grey;">
	                                <div id="reindexJobInfo">
	                                </div>
	                                <br/>
	                                <div id="reindexStart">
	                                </div>
	                                <div id="reindexEnd">
	                                </div>
	                                <div id="reindexStatus">
	                                </div>
	                                <div id="reindexNumEntities">
	                                </div>
	                                    <span><span style="float:right;">
	                                            <button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.management.codelists.reindexStart" />" onClick="javascript:scriptScopeCodeLists.startReindexJob();">
	                                                <fmt:message key="dialog.admin.catalog.management.codelists.reindexStart" />
	                                            </button>
	                                        </span><span id="reindexLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span>
	                                    </span>
	                                    <div class="fill"></div>
	                                    </div>
                            </div>
                        </div>
                    </div><!-- TAB 3 END -->
        <!-- CONTENT END -->
        </div>
    </body>
</html>
