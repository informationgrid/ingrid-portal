<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <title><fmt:message key="dialog.popup.serviceLink.link" /></title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <meta name="author" content="wemove digital solutions" />
        <meta name="copyright" content="wemove digital solutions GmbH" />
        <script type="text/javascript">
            var curSelectedObject = {};
            var curSelectedUrl = {};
            var dirtyFlag = null;
            
            var scriptScope = _container_;
            
            var fieldData = {
                items: []
            };
            
            var generalUiInputElements = [["linksFromFieldName", "linksFromFieldNameLabel"]];
            var objUiInputElements = [["linksToObjectName", "linksToObjectNameLabel"], ["linksToObjectClass", "linksToObjectClassLabel"]];
            
            var urlUiInputElements = [["linksToName", "linksToNameLabel"], ["linksToURL", "linksToURLLabel"], ["linksToDataType", "linksToDataTypeLabel"]];
            
            // All the possible values for the select box
            //var referenceMap = [["Basisdaten", "3210"], ["Basisdaten", "3345"], ["Datengrundlage", "3570"], ["Fachliche Grundlage", "3520"], ["Herstellungsprozess", "3515"], ["Methode / Datengrundlage", "3100"], ["Schl\u00fcsselkatalog", "3535"], ["Symbolkatalog", "3555"], ["Verweis zu Dienst", "5066"]];
            var referenceMap = [{
                label: "Basisdaten",
                id: "3210"
            }, {
                label: "Basisdaten",
                id: "3345"
            }, {
                label: "Datengrundlage",
                id: "3570"
            }, {
                label: "Fachliche Grundlage",
                id: "3520"
            }, {
                label: "Herstellungsprozess",
                id: "3515"
            }, {
                label: "Methode / Datengrundlage",
                id: "3100"
            }, {
                label: "Schl\u00fcsselkatalog",
                id: "3535"
            }, {
                label: "Symbolkatalog",
                id: "3555"
            }, {
                label: "Verweis zu Dienst",
                id: "5066"
            }];
            
            //dijit.byId('pageDialog').onLoadDeferred.addCallback(init);
            dojo.connect(_container_, "onLoad", init);
            
            dojo.connect(_container_, "onUnload", acceptLinkList);
            
            function init(){
                dirtyFlag = udkDataProxy.dirtyFlag;
                
                disableInputElementsOnWrongPermission();
                
                createDOMElements();
                
                // reset the dialog
                hideInputElements();
                console.debug("a2");
                resetRequiredInputElements();
                console.debug("b");
                // Init static data
                var objectName = dijit.byId("objectName").getValue();
                dijit.byId("linksFromObjectName").setValue(objectName);
                
                // Connect both tables so only one value is selected at once. If the user selects an object the
                // current url is deselected, if an url is selected the object is deselected 
                var objectList = UtilGrid.getTable("linkListObject");
                var urlList = UtilGrid.getTable("linkListURL");
                
                // If a selected object was removed from the lists, reset the input fields
                //dojo.connect(objectList.store, "onRemoveData", function(obj){
                dojo.connect(objectList, "onDataChanged", function(msg) {
                    //if (dojo.some(objectList.selection.getSelected(), function(item){
                    //    return (item == obj.src);
                    //})) 
                    if (msg.type == "deleted")
                        resetInput();
                });
                dojo.connect(urlList, "onDataChanged", function(msg) {
                    //if (dojo.some(urlList.selection.getSelected(), function(item){
                    //    return (item == url.src);
                    //}))
                    if (msg.type == "deleted") 
                        resetInput();
                });
                
                // Init the radio buttons onclick functions
                dojo.byId("linksLinkType1").onclick = function(){
                    selectLinkType("obj");
                    newLink();
                }
                dojo.byId("linksLinkType2").onclick = function(){
                    selectLinkType("url");
                    newLink();
                }
                // Init the form
                dojo.byId("linkToObject").style.display = "block";
                dojo.byId("linkToURL").style.display = "none";
                
                
                // The selected value has to be displayed on select
                //dojo.connect(objectList, "onSelected", function(){
                dojo.connect(objectList, "onSelectedRowsChanged", function(row) {
                    // Update the displayed data
                    var selectedObjects = UtilGrid.getSelectedRowIndexes("linkListObject");//objectList.selection.getSelected();
                    if (selectedObjects.length == 0) {
                        curSelectedObject = {};
                        return;
                    }
                    else {
                        if (selectedObjects[0] == null) 
                            return;
                        if (selectedObjects.length == 1 && selectedObjects[0] != undefined && selectedObjects[0] < UtilGrid.getTableData("linkListObject").length) {
                            selectLinkType("obj");
                            curSelectedUrl = {};
                            curSelectedObject = UtilGrid.getSelectedData("linkListObject")[0];
                            
                            if (curSelectedObject.relationType == -1) {
                                dijit.byId("linksFromFieldName").setValue(curSelectedObject.relationTypeName);
                                //				console.debug("setting value to "+curSelectedObject.relationTypeName);
                            }
                            else {
                                //dijit.byId("linksFromFieldName").setValue(getDisplayValueForRelationType(curSelectedObject.relationType));
                                var typeWidget = dijit.byId("linksFromFieldName");
                                var def = UtilStore.getItemByAttribute(typeWidget.store, "id", curSelectedObject.relationType);
                                def.addCallback(function(item) {typeWidget.set("item", item);});
                                //dijit.byId("linksFromFieldName").set("item", dijit.byId("linksFromFieldName").store._arrayOfTopLevelItems[0]) // set the first item from store here!
                            }
                            
                            dijit.byId("linksToObjectName").setValue(curSelectedObject.title);
                            dijit.byId("linksToObjectClass").setValue("Class" + curSelectedObject.objectClass);
                            dijit.byId("linksToDescription").setValue(curSelectedObject.relationDescription);
                            
                            // Update the 'save' button text
                            dijit.byId("saveButton").setLabel("<fmt:message key='general.save' />");
                            showInputElements();
                        }
                        else {
                            curSelectedObject = {};
                            curSelectedUrl = {};
                            hideInputElements();
                            resetInputFields();
                        }
                        UtilGrid.clearSelection("linkListURL");
                    }
                });
                
                // The selected value has to be displayed on select
                //dojo.connect(urlList, "onSelected", function(){
                dojo.connect(urlList, "onSelectedRowsChanged", function(row) {
                    // Update the displayed data
                    var selectedUrls = UtilGrid.getSelectedRowIndexes("linkListURL");//urlList.selection.getSelected();
                    if (selectedUrls.length == 0) {
                        curSelectedUrl = {};
                        return;
                    }
                    else 
                        if (selectedUrls.length == 1 && selectedUrls[0] != undefined && selectedUrls[0] < UtilGrid.getTableData("linkListURL").length) {
                            selectLinkType("url");
                            curSelectedObject = {};
                            curSelectedUrl = UtilGrid.getSelectedData("linkListURL")[0];
                            // Reset the obj List
                            //objectList.selection.deselectAll();
                            
                            //objectList.renderSelections();
                            var typeWidget = dijit.byId("linksFromFieldName");
                            typeWidget.setValue(curSelectedUrl.relationTypeName);
                            if (curSelectedUrl.relationType == -1) {
                                typeWidget.setValue(curSelectedUrl.relationTypeName);
                            } else {
                                var def = UtilStore.getItemByAttribute(typeWidget.store, "id", curSelectedUrl.relationType);
                                def.addCallback(function(item) {typeWidget.set("item", item);});
                            }
                            
                            dijit.byId("linksToURL").setValue(curSelectedUrl.url);
                            dijit.byId("linksToName").setValue(curSelectedUrl.name);
                            dijit.byId("linksToDataType").setValue(curSelectedUrl.datatype);
                            dijit.byId("linksToDataVolume").setValue(curSelectedUrl.volume);
                            dijit.byId("linksToURLType").setValue(curSelectedUrl.urlType);
                            dijit.byId("linksToIconURL").setValue(curSelectedUrl.iconUrl);
                            dijit.byId("linksToIconText").setValue(curSelectedUrl.iconText);
                            dijit.byId("linksToUrlDescription").setValue(curSelectedUrl.description);
                            
                            // Update the 'save' button text
                            dijit.byId("saveButton").setLabel("<fmt:message key='general.save' />");
                            showInputElements();
                        }
                        else {
                            curSelectedObject = {};
                            curSelectedUrl = {};
                            hideInputElements();
                            resetInputFields();
                        }
                        UtilGrid.clearSelection("linkListObject");
                });
                
                // Populate the object and url links list
                var srcStore = UtilGrid.getTableData("linksTo");
                
                var container = dijit.byId("pageDialog");
                // Check if a filter was passes as an argument
                if (container.customParams && container.customParams.filter) {
                    // Filter the list according to the filter arg
                    
                    var filter = container.customParams.filter;
                    var grid = container.customParams.grid;
                    /*var filterFuncObject = function(item){
                        return item.relationType == filter && item.objectClass != undefined;
                    };
                    var filterFuncUrl = function(item){
                        return item.relationType == filter && item.url != undefined;
                    };

                    objectList.getData().setFilter(filterFuncObject);
                    urlList.getData().setFilter(filterFuncUrl);*/
                    UtilGrid.setTableData("linkListObject", dojo.filter(UtilGrid.getTableData(grid), function(item) {return item.objectClass != undefined;}));
                    UtilGrid.setTableData("linkListURL", dojo.filter(UtilGrid.getTableData(grid), function(item) {return item.url != undefined;}));
                    
                    // Disable the type input and set it to the filter
                    var referenceWidget = dijit.byId("linksFromFieldName");
                    referenceWidget.setDisabled(true);
                    
                    var filterStr = container.customParams.filter + '';
                    initReferenceWidget(filterStr);
                    var def = UtilStore.getItemByAttribute(referenceWidget.store, "id", filterStr)
                    def.addCallback(function(item) {referenceWidget.set("item", item);});
                }
                else {
                    // Load the initial values from the backend
                    initReferenceWidget();
                    dijit.byId("linksFromFieldName").setValue("");
                    /*var showAllObj = function(item) { return item.objectClass != undefined; };
                    var showAllURL = function(item) { return item.url != undefined; };
                    objectList.getData().setFilter(showAllObj);
                    urlList.getData().setFilter(showAllURL);*/
                    //objectList.setData(srcStore);
                    //urlList.setData(srcStore);
                    UtilGrid.setTableData("linkListObject", dojo.filter(srcStore, function(item) {return item.objectClass != undefined;}));
                    UtilGrid.setTableData("linkListURL", dojo.filter(srcStore, function(item) {return item.url != undefined;}));
                    
                }

                objectList.invalidate();
                urlList.invalidate();
                
                // Connect the object and url store with the internal dirty flag
                //connectStoreWithDirtyFlag(objStore);
                //connectStoreWithDirtyFlag(urlStore);
            };
            
            
            createDOMElements = function(){
            
                var storeProps = {data: {identifier: 'id',label: 'id'}};
                //var storeProps2 = {searchAttr:"label", data: {identifier: 'abbreviation',label: 'label'}};
                
                createComboBox("linksFromFieldName", null, storeProps, null);
                dijit.byId("linksFromFieldName").searchAttr = "label";
                
                createComboBox("linksToDataType", null, storeProps, null, "js/data/datatypes.json");
                dijit.byId("linksToDataType").searchAttr = "label";
                createSelectBox("linksToURLType", null, storeProps, null, "js/data/urlReferenceTypes.json");
                
                var linkListObjectStructure = [
                    {field: 'icon', name: 'icon', width: '23px'}, 
                    {field: 'title', name: 'title', width: 175-scrollBarWidth+'px'}
                ];
                createDataGrid("linkListObject", null, linkListObjectStructure, null);
                
                var linkListObjectStructure = [{
                    field: 'icon',
                    name: 'icon',
                    width: '23px'
                }, {
                    field: 'name',
                    name: 'name',
                    width: 175-scrollBarWidth+'px'
                }];
                createDataGrid("linkListURL", null, linkListObjectStructure, null);
                
                //var storeProps2 = {data: {identifier: 'id',label: 'label'}};
                createSelectBox("linksToObjectClass", null, storeProps, null, "js/data/objectclasses.json");
            }
            
            setDirtyFlag = function(){
                dirtyFlag = true;
            }
            
            connectStoreWithDirtyFlag = function(store){
                dojo.connect(store, "onAddData", this, "setDirtyFlag");
                dojo.connect(store, "onRemoveData", this, "setDirtyFlag");
                dojo.connect(store, "onUpdateField", this, "setDirtyFlag");
            }
            
            disableInputElementsOnWrongPermission = function(){
                if (currentUdk.writePermission == false) {
                    dijit.byId("newLinkButton").setDisabled(true);
                    dijit.byId("saveButton").setDisabled(true);
                    dijit.byId("resetButton").setDisabled(true);
                    dojo.byId("linksLinkType1").setDisabled(true);
                    dojo.byId("linksLinkType2").setDisabled(true);
                    dijit.byId("linksFromFieldName").setDisabled(true);
                    dijit.byId("linksToDescription").setDisabled(true);
                    
                    // URL Input Fields
                    dijit.byId("linksToURL").setDisabled(true);
                    dijit.byId("linksToDataType").setDisabled(true);
                    dijit.byId("linksToDataVolume").setDisabled(true);
                    dijit.byId("linksToURLType").setDisabled(true);
                    dijit.byId("linksToIconURL").setDisabled(true);
                    dijit.byId("linksToIconText").setDisabled(true);
                    dijit.byId("linksToUrlDescription").setDisabled(true);
                    dijit.byId("linksToName").setDisabled(true);
                }
            }
            
            resetRequiredInputElements = function(){
                dojo.query(".important").forEach(function(item) {dojo.removeClass(item, "important");});
                /*var resetRequiredState = function(widgetLabelList){
                    for (var i in widgetLabelList) {
                        console.debug("remove class from: " + widgetLabelList[i][1]);
                        dojo.removeClass(dojo.byId(widgetLabelList[i][1]), "important");
                    }
                }
                
                resetRequiredState(generalUiInputElements);
                console.debug("1");
                resetRequiredState(objUiInputElements);
                console.debug(dijit.byId("linksToDataType"));
                resetRequiredState(urlUiInputElements);*/
            }
            
            validateInputElements = function(){
                var valid = true;
                var objSelected = dojo.byId("linksLinkType1").checked;
                var validate = function(widgetLabelList){
                    for (var i in widgetLabelList) {
                        var val = dijit.byId(widgetLabelList[i][0]).getValue();
                        
                        if (!val || val == "") {
                            dojo.addClass(dojo.byId(widgetLabelList[i][1]), "important");
                            valid = false;
                        }
                    }
                }
                
                validate(generalUiInputElements);
                
                if (objSelected) {
                    validate(objUiInputElements);
                }
                else {
                    validate(urlUiInputElements);
                }
                return valid;
            }
            
            
            hideInputElements = function(){
                dojo.byId("inputFields").style.display = "none";
                dojo.byId("inputButtons").style.display = "none";
                dijit.byId("linksFromFieldName").setDisabled(true);
            }
            
            showInputElements = function(){
                dojo.byId("inputFields").style.display = "";
                dojo.byId("inputButtons").style.display = "";
                
                if (!dijit.byId("pageDialog").customParams || !dijit.byId("pageDialog").customParams.filter) {
                    if (currentUdk.writePermission) 
                        dijit.byId("linksFromFieldName").setDisabled(false);
                }
            }
            
            /*
             function getRelationTypeForDisplayValue(dispVal){
             var relationType = dijit.byId("linksFromFieldName").getValueForDisplayValue(dispVal);
             if (relationType != null) {
             return relationType;
             }
             else {
             return -1;
             }
             }*/
            
            function getDisplayValueForRelationType(relType){
                var result = -1;
                dojo.some(referenceMap, function(ref){
                    //console.debug("check: " + typeof(relType) + "-" + typeof(ref.id));
                    if (ref.id == relType) {
                        result = ref.label;
                        return true;
                    }
                });
                return result;
            }
            // Initialises the 'linksFromFieldName' select box depending on the current obj class and the filter id
            function initReferenceWidget(filter){
                var initialValues = [];
                if (filter) {
                    dojo.forEach(referenceMap, function(item){
                        if (item.id == filter) 
                            initialValues.push(item);
                    });
                }
                else {
                    var objectClass = dijit.byId("objectClass").getValue().substr(5, 1);
                    
                    var idList = [];
                    switch (objectClass) {
                        case "0":
                            break; // Empty select box
                        case "1":
                            idList = ["3570", "3520", "3515", "3535", "3555", "5066"];
                            break;
                        case "2":
                            idList = ["3345"];
                            break;
                        case "3":
                            idList = ["3210"];
                            break;
                        case "4":
                            break; // Empty select box
                        case "5":
                            idList = ["3100"];
                            break;
                        default:
                            console.debug("Error: could not determine object class.");
                            break;
                    }
                    initialValues = dojo.filter(referenceMap, function(item){
                        return dojo.some(idList, function(id){
                            return id == item.id;
                        });
                    });
                }
                // add empty item at the beginning
                //console.debug("initialValues: " + initialValues);
                var storeProps = {
                    searchAttr: "label",
                    data: {
                        label: "id",
                        identifier: "id",
                        items: initialValues
                    }
                };
                //UtilStore.updateWriteStore("linksFromFieldName", initialValues, storeProps);
                dijit.byId("linksFromFieldName").store = new dojo.data.ItemFileWriteStore(storeProps);
                
            }
            
            
            
            saveLink = function(){
                var objData = UtilGrid.getSelectedData("linkListObject")[0];
                var urlData = UtilGrid.getSelectedData("linkListURL")[0];
                var objSelected = dojo.byId("linksLinkType1").checked;
                
                resetRequiredInputElements();
                
                if (!validateInputElements()) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='links.fillRequiredFieldsHint' />", dialog.WARNING);
                    return;
                }
                
                // Disallow links to self
                if (objSelected && curSelectedObject.uuid == currentUdk.uuid) {
                    dialog.show("<fmt:message key='general.error' />", "<fmt:message key='links.noLinkToSelfHint' />", dialog.WARNING);
                    return;
                }
                
                if (objData) {
                    // If a node was selected we have to update the information
                    // The dojo FilteringTable does not like updates on values that are not displayed
                    // It simply throws a cryptic error on update. The connected 'onUpdateField' function in
                    // FilteringTable.init tries to get a row idx which is 'undefined'
                    //		store.update(data, "uuid", curSelectedObject.uuid);
                    //		store.update(data, "relationDescription", dijit.byId("linksToDescription").getValue());
                    //		store.update(data, "objectClass", curSelectedObject.objectClass);
                    objData.uuid = curSelectedObject.uuid;
                    objData.relationDescription = dijit.byId("linksToDescription").getValue();
                    objData.objectClass = curSelectedObject.objectClass;
                    //var typeName = dijit.byId("linksFromFieldName").getValue();
                    var selectedLinksFrom = dijit.byId("linksFromFieldName");
                    objData.relationType = selectedLinksFrom.item == null ? -1 : selectedLinksFrom.item.id[0];
                    if (objData.relationType == -1) {
                        objData.relationTypeName = selectedLinksFrom.get("value");
                    }
                    UtilList.addIcons([objData]);
                    UtilList.addObjectLinkLabels([objData]);
                    UtilGrid.updateTableDataRowAttr("linkListObject", UtilGrid.getSelectedRowIndexes("linkListObject")[0], "icon", objData.icon);
                    UtilGrid.updateTableDataRowAttr("linkListObject", UtilGrid.getSelectedRowIndexes("linkListObject")[0], "title", curSelectedObject.title);
                }
                else 
                    if (urlData) {
                        var newUrl = _getUrl();
                        urlData.url = newUrl.url;
                        urlData.datatype = newUrl.datatype;
                        urlData.volume = newUrl.volume;
                        urlData.urlType = newUrl.urlType;
                        urlData.iconUrl = newUrl.iconUrl;
                        urlData.iconText = newUrl.iconText;
                        urlData.description = newUrl.description;
                        urlData.relationType = newUrl.relationType;
                        if (newUrl.relationType == -1) {
                            urlData.relationTypeName = newUrl.relationTypeName;
                        }
                        UtilList.addIcons([urlData]);
                        UtilList.addUrlLinkLabels([urlData]);
                        //urlStore.setValue(urlData, "name", newUrl.name);
                        UtilGrid.updateTableDataRowAttr("linkListURL", UtilGrid.getSelectedRowIndexes("linkListURL")[0], "name", newUrl.name);
                    }
                    else 
                        if (objSelected && curSelectedObject) {
                            // Otherwise a new link has to be created
                            // Take the current selected object and add the values that were entered in the ui fields
                            curSelectedObject.relationDescription = dijit.byId("linksToDescription").getValue();
                            
                            //var typeName = dijit.byId("linksFromFieldName").getValue();
                            var selectedLinksFrom = dijit.byId("linksFromFieldName");//._getSelectedOptionsAttr();
                            curSelectedObject.relationType = selectedLinksFrom.item == null ? -1 : selectedLinksFrom.item.id[0];
                            if (curSelectedObject.relationType == -1) {
                                curSelectedObject.relationTypeName = selectedLinksFrom.get("value");
                            }
                            
                            // No checks if the store already contains the current element.
                            //curSelectedObject.Id = _getNewKey();
                            UtilList.addIcons([curSelectedObject]);
                            UtilList.addObjectLinkLabels([curSelectedObject]);
                            UtilGrid.addTableDataRow("linkListObject", curSelectedObject);
                            // Select the object in the list
                            //console.debug("data.length: " + UtilGrid.getTableData("linkListObject").getLength());
                            // row seems to be added to store a bit later!?
                            UtilGrid.setSelection("linkListObject", [UtilGrid.getTableData("linkListObject").length-1]);
                            
                            dijit.byId("saveButton").setLabel("<fmt:message key='general.save' />");
                        }
                        else 
                            if (!objSelected) {
                                var newUrl = _getUrl();
                                //newUrl.Id = _getNewKey();
                                UtilList.addIcons([newUrl]);
                                UtilList.addUrlLinkLabels([newUrl]);
                                //urlStore.newItem(newUrl);
                                UtilGrid.addTableDataRow("linkListURL", newUrl);
                                //dijit.byId("linkListURL").selection.deselectAll();
                                //dijit.byId("linkListURL").selection.select(newUrl);
                                UtilGrid.setSelection("linkListURL", [UtilGrid.getTableData("linkListURL").length-1]);
                                
                                dijit.byId("saveButton").setLabel("<fmt:message key='general.save' />");
                            }
                            else {
                                dialog.show("<fmt:message key='general.error' />", "<fmt:message key='links.fillRequiredFieldsHint' />", dialog.WARNING);
                                return;
                            }
                UtilGrid.getTable("linkListObject").invalidate();
                UtilGrid.getTable("linkListURL").invalidate();
            }
            
            _getUrl = function(){
                var newUrl = {};
                newUrl.url = dijit.byId("linksToURL").getValue();
                newUrl.datatype = dijit.byId("linksToDataType").getValue();
                newUrl.volume = dijit.byId("linksToDataVolume").getValue();
                newUrl.urlType = dijit.byId("linksToURLType").getValue();
                newUrl.iconUrl = dijit.byId("linksToIconURL").getValue();
                newUrl.iconText = dijit.byId("linksToIconText").getValue();
                newUrl.description = dijit.byId("linksToUrlDescription").getValue();
                
                //var typeName = dijit.byId("linksFromFieldName").getValue();
                var selectedLinksFrom = dijit.byId("linksFromFieldName");//._getSelectedOptionsAttr();
                newUrl.relationType = selectedLinksFrom.item == null ? -1 : selectedLinksFrom.item.id[0];
                if (newUrl.relationType == -1) {
                    newUrl.relationTypeName = selectedLinksFrom.get("value");
                }
                
                newUrl.name = dijit.byId("linksToName").getValue();
                return newUrl;
            }
            
            // iterates over all entries in the stores and returns a key that is not in use yet
            /*_getNewKey = function(){
             var objStore = dijit.byId("linkListObject").store;
             var urlStore = dijit.byId("linkListURL").store;
             
             var objKey = UtilStore.getNewKey(objStore);
             var urlKey = UtilStore.getNewKey(urlStore);
             
             return (objKey > urlKey ? objKey : urlKey);
             }*/
            // 'New Link' Button onClick function.
            //
            // Resets the list selection and currently selected objects/urls
            newLink = function(){
                showInputElements();
                // Reset object and url selections
                //var objectList = dijit.byId("linkListObject");
                //var urlList = dijit.byId("linkListURL");
                //objectList.selection.deselectAll();
                //objectList.renderSelections();
                //urlList.selection.deselectAll();
                //urlList.renderSelections();
                UtilGrid.clearSelection("linkListObject");
                UtilGrid.clearSelection("linkListURL");
                
                resetInputFields();
            }
            
            showAssignObjectDialog = function(){
                if (currentUdk.writePermission == false) {
                    return;
                }
                
                var deferred = new dojo.Deferred();
                var setSelectedObject = function(obj){
                    if (obj.uuid != "objectRoot") {
                        curSelectedObject = obj;
                        dijit.byId("linksToObjectName").setValue(curSelectedObject.title);
                        dijit.byId("linksToObjectClass").setValue("Class" + curSelectedObject.objectClass);
                    }
                }
                
                deferred.addCallback(setSelectedObject);
                
                if (curSelectedObject) {
                    dialog.showPage("<fmt:message key='dialog.links.selectObject.title' />", 'dialogs/mdek_links_select_object_dialog.jsp?c='+userLocale, 522, 520, true, {
                        // custom parameters
                        resultHandler: deferred,
                        jumpToNode: curSelectedObject.uuid
                    });
                }
                else {
                    dialog.showPage("<fmt:message key='dialog.links.selectObject.title' />", 'dialogs/mdek_links_select_object_dialog.jsp?c='+userLocale, 522, 520, true, {
                        // custom parameters
                        resultHandler: deferred
                    });
                }
            }
            
            function selectLinkType(e){
                if (this.id == "linksLinkType2" || e == "url") {
                    document.getElementById("linkToObject").style.display = "none";
                    document.getElementById("linkToURL").style.display = "block";
                    document.getElementById("linksLinkType1").checked = false;
                    document.getElementById("linksLinkType2").checked = true;
                }
                else {
                    document.getElementById("linkToObject").style.display = "block";
                    document.getElementById("linkToURL").style.display = "none";
                    document.getElementById("linksLinkType1").checked = true;
                    document.getElementById("linksLinkType2").checked = false;
                }
            }
            
            
            resetInputFields = function(){
                // Reset field values
                curSelectedObject = {};
                curSelectedUrl = {};
                
                var thisDialog = dijit.byId("pageDialog");
                if (!thisDialog.customParams || !thisDialog.customParams.filter) {
                    //if (currentUdk.writePermission) {
                    dijit.byId("linksFromFieldName").setDisabled(false);
                    //}
                    dijit.byId("linksFromFieldName").setValue("");
                }
                
                dijit.byId("linksToObjectName").setValue("");
                dijit.byId("linksToObjectClass").setValue("");
                dijit.byId("linksToDescription").setValue("");
                dijit.byId("linksToURL").setValue("http://");
                dijit.byId("linksToName").setValue("");
                dijit.byId("linksToDataType").setValue("");
                dijit.byId("linksToDataVolume").setValue("");
                dijit.byId("linksToURLType").setValue("");
                dijit.byId("linksToIconURL").setValue("");
                dijit.byId("linksToIconText").setValue("");
                dijit.byId("linksToUrlDescription").setValue("");
                
                // Change 'save' Button text
                dijit.byId("saveButton").setLabel("<fmt:message key='general.add' />");
            }
            
            // Cancel Button onClick function
            //
            resetInput = function(){
                newLink();
                hideInputElements();
                resetRequiredInputElements();
                
                var thisDialog = dijit.byId("pageDialog");
                if (!thisDialog.customParams || !thisDialog.customParams.filter) {
                    dijit.byId("linksFromFieldName").setValue("");
                }
            }
            
            // Accept Button onClick function.
            //
            // This function copies the object links list to the main mdek linksTo list
            function acceptLinkList(){
            	//delete gridManager.linkListObject;
                //delete gridManager.linkListURL;
                var dlg = dijit.byId("pageDialog");
             
                var relationType = dlg.customParams ? dlg.customParams.filter : -2;
                var objectData = UtilGrid.getTableData("linkListObject");
                var relTypeData = objectData.concat(UtilGrid.getTableData("linkListURL"));

                console.debug("relTypeData: " + relationType);
                console.debug(relTypeData);
                var allDataWithoutThisRelationType = [];
                if (dlg.customParams && dlg.customParams.grid) {
                	UtilGrid.setTableData(dlg.customParams.grid, relTypeData);
                	// remove all entires with certain relationtype and insert all from here
                	allDataWithoutThisRelationType = dojo.filter(UtilGrid.getTableData("linksTo"), function(item) { return item.relationType != relationType});
                    console.debug("allDataWithoutThisRelationType:");
                    console.debug(allDataWithoutThisRelationType);
                }
                
                UtilGrid.setTableData("linksTo", allDataWithoutThisRelationType.concat(relTypeData));
            }
            
            closeThisDialog = function(){
                dijit.byId("pageDialog").hide();
            }
        </script>
    </head>
    <body>
        <div id="mainContentTest" dojoType="dijit.layout.ContentPane">
            <div id="links">
             <div id="winNavi" style="right:10px;">
                	    <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-7#maintanance-of-objects-7', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
                	</div>
                <br>
                <br>   
                <div id="linksContent" class="content" dojoType="dijit.layout.BorderContainer" design="sidebar" style="height:600px;">
                   <!-- LEFT HAND SIDE CONTENT START -->
                    <div class="inputContainer grey" dojoType="dijit.layout.ContentPane" region="center" style="padding:15px;">
                        <span class="label">
                            <label onclick="javascript:dialog.showContextHelp(arguments[0], 7034)">
                                <fmt:message key="dialog.links.source" />
                            </label>
                        </span>
                        <div class="outlined">
                            <span class="outer">
                                <div>
                                    <span class="label">
                                        <label for="linksFromObjectName" onclick="javascript:dialog.showContextHelp(arguments[0], 7035)">
                                            <fmt:message key="dialog.links.objTitle" />
                                        </label>
                                    </span>
                                    <span class="input">
                                        <input type="text" id="linksFromObjectName" name="linksFromObjectName" disabled="true" dojoType="dijit.form.ValidationTextBox" />
                                    </span>
                                </div>
                            </span>
                            <span class="outer required">
                                <div>
                                    <span id="linksFromFieldNameLabel" class="label">
                                        <label for="linksFromFieldName" onclick="javascript:dialog.showContextHelp(arguments[0], 2000)">
                                            <fmt:message key="dialog.links.link" />*
                                        </label>
                                    </span>
                                    <span class="input">
                                        <input id="linksFromFieldName" style="width: 100%;"><!--<span dojoType="dojo.data.ItemFileWriteStore" id="linksFromStore" jsId="linksFromStore" data="fieldData"></span>
                                        <div dojoType="dijit.form.ComboBox" store="linksFromStore" maxlength="80" autoComplete="false" toggle="plain" style="width: 590px;" id="linksFromFieldName">
                                        </div>-->
                                    </span>
                                </div>
                            </span>
                            <div class="fill">
                            </div>
                        </div>
                        <span class="button transparent">
                            <span style="float: right;">
                                <button dojoType="dijit.form.Button" id="newLinkButton" onClick="newLink" type="button">
                                    <fmt:message key="dialog.links.new" />
                                </button>
                            </span>
                        </span>
                        <div class="fill"></div>
                        <span id="inputFields" style="display: none;">
                            <span class="outer required">
                                <div>
                                    <span class="label">
                                        <label for="linksLinkType" onclick="javascript:dialog.showContextHelp(arguments[0], 7037)">
                                            <fmt:message key="dialog.links.linkType" />*
                                        </label>
                                    </span>
                                    <div class="checkboxContainer input">
                                        <span>
                                            <input type="radio" id="linksLinkType1" class="radio" checked/>
                                            <label class="inActive" for="linksLinkType1">
                                                <fmt:message key="dialog.links.object" />
                                            </label>
                                        </span>
                                        <span>
                                            <input type="radio" id="linksLinkType2" class="radio" />
                                            <label class="inActive" for="linksLinkType2">
                                                <fmt:message key="dialog.links.urlTitle" />
                                            </label>
                                        </span>
                                    </div>
                                </div>
                            </span>
                            <span class="outer">
                            <span class="label">
                                <label onclick="javascript:dialog.showContextHelp(arguments[0], 7038)">
                                    <fmt:message key="dialog.links.target" />
                                </label>
                            </span><!-- VERWEIS AUF OBJEKT -->
                            <div id="linkToObject" class="outlined">
                                <span class="outer required">
                                    <div>
                                        <span class="label">
                                            <label id="linksToObjectNameLabel" for="linksToObjectName" onclick="javascript:dialog.showContextHelp(arguments[0], 2100)">
                                                <fmt:message key="dialog.links.objTitle" />*
                                            </label>
                                        </span>
                                        <span class="functionalLink">
                                            <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="assignObjectDialogLink" href="javascript:void(0);" onClick="javascript:showAssignObjectDialog();" title="<fmt:message	key="dialog.links.selectObject" /> [Popup]"><fmt:message key="dialog.links.selectObject" /></a>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="linksToObjectName" name="linksToObjectName" disabled="true" dojoType="dijit.form.ValidationTextBox" />
                                        </span>
                                    </div>
                                </span>
                                <span class="outer required">
                                    <div>
                                        <span class="label">
                                            <label id="linksToObjectClassLabel" for="linksToObjectClass" onclick="javascript:dialog.showContextHelp(arguments[0], 7040)">
                                                <fmt:message key="dialog.links.objClass" />*
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="linksToObjectClass" name="linksToObjectClass" style="width:100%;" disabled="true" />
                                        </span>
                                    </div>
                                </span>
                                <span class="outer">
                                    <div>
                                        <span class="label">
                                            <label for="linksToDescription" onclick="javascript:dialog.showContextHelp(arguments[0], 2110)">
                                                <fmt:message key="dialog.links.objDescription" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" mode="textarea" id="linksToDescription" name="linksToDescription" class="textAreaFull" dojoType="dijit.form.SimpleTextarea" />
                                        </span>
                                    </div>
                                </span>
                                <div class="fill"></div>
                            </div>
                            <!-- VERWEIS AUF URL -->
                            <div id="linkToURL" class="outlined">
                                <span class="outer required">
                                    <div>
                                        <span class="label">
                                            <label id="linksToNameLabel" for="linksToName" onclick="javascript:dialog.showContextHelp(arguments[0], 2210)">
                                                <fmt:message key="dialog.links.urlDescription" />*
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxlength="255" id="linksToName" name="linksToName" dojoType="dijit.form.ValidationTextBox" />
                                        </span>
                                    </div>
                                </span>
                                <span class="outer required">
                                    <div>
                                        <span class="label">
                                            <label id="linksToURLLabel" for="linksToURL" onclick="javascript:dialog.showContextHelp(arguments[0], 2200)">
                                                <fmt:message key="dialog.links.url" />*
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="255" id="linksToURL" name="linksToURL" dojoType="dijit.form.ValidationTextBox" />
                                        </span>
                                    </div>
                                </span>
                                <span class="outer required" style="width:33%;">
                                    <div>
                                        <span class="label">
                                            <label id="linksToDataTypeLabel" onclick="javascript:dialog.showContextHelp(arguments[0], 2240)">
                                                <fmt:message key="dialog.links.dataType" />*
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input id="linksToDataType" searchAttr="label" style="width: 100%;">
                                        </span>
                                    </div>
                                </span>
                                <span class="outer" style="width:33%;">
                                    <div>
                                        <span class="label">
                                            <label for="linksToDataVolume" onclick="javascript:dialog.showContextHelp(arguments[0], 2220)">
                                                <fmt:message key="dialog.links.dataSize" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="linksToDataVolume" maxlength="20" name="linksToDataVolume" dojoType="dijit.form.ValidationTextBox" style="width:100%;"/>
                                        </span>
                                    </div>
                                </span>
                                <span class="outer" style="width:34%;">
                                    <div>
                                        <span class="label">
                                            <label for="linksToURLType" onclick="javascript:dialog.showContextHelp(arguments[0], 2251)">
                                                <fmt:message key="dialog.links.urlType" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input id="linksToURLType" style="width: 100%;"><!--<div dojoType="dijit.form.Select" autoComplete="false" toggle="plain" dataUrl="js/data/urlReferenceTypes.js" style="width: 116px;" widgetId="linksToURLType">
                                            </div>-->
                                        </span>
                                    </div>
                                </span>
                                <div class="fill">
                                </div>
                                <span class="outer required halfWidth">
                                    <div>
                                        <span class="label">
                                            <label for="linksToIconURL" onclick="javascript:dialog.showContextHelp(arguments[0], 2250)">
                                                <fmt:message key="dialog.links.urlIcon" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="linksToIconURL" maxlength="255" name="linksToIconURL" dojoType="dijit.form.ValidationTextBox" style="width:100%;"/>
                                        </span>
                                    </div>
                                </span>
                                <span class="outer required halfWidth">
                                    <div>
                                        <span class="label">
                                            <label for="linksToIconText" onclick="javascript:dialog.showContextHelp(arguments[0], 2230)">
                                                <fmt:message key="dialog.links.urlIconText" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="linksToIconText" maxlength="80" name="linksToIconText" dojoType="dijit.form.ValidationTextBox" style="width:100%;"/>
                                        </span>
                                    </div>
                                </span>
                                <span class="outer">
                                    <div>
                                        <span class="label">
                                            <label for="linksToUrlDescription" onclick="javascript:dialog.showContextHelp(arguments[0], 2260)">
                                                <fmt:message key="dialog.links.objDescription" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="linksToUrlDescription" name="linksToUrlDescription" class="textAreaFull" dojoType="dijit.form.SimpleTextarea" />
                                        </span>
                                    </div>
                                </span>
                                <div class="fill">
                                </div>
                            </div>
                            </span>
                            <span id="inputButtons" style="display: none;">
                                <div class="inputContainer">
                                    <span class="button">
                                        <span style="float: right;">
                                            <button dojoType="dijit.form.Button" id="resetButton" onClick="resetInput">
                                                <fmt:message key="dialog.links.cancel" />
                                            </button>
                                        </span><!--
                                        <span style="float:right; padding-right:5px;"><button dojoType="ingrid:Button" onClick="acceptLinkList">&Auml;nderungen &uuml;bernehmen</button></span>
                                        -->
                                        <span style="float: right; padding-right: 5px;">
                                            <button id="saveButton" dojoType="dijit.form.Button" onClick="saveLink" type="button">
                                                <fmt:message key="dialog.links.add" />
                                            </button>
                                        </span>
                                    </span>
                                </div>
                            </span>
                        </span>
                    </div><!-- LEFT HAND SIDE CONTENT END -->
                    <!-- RIGHT HAND SIDE CONTENT START -->
                    <div id="listLinks" class="inputContainer" dojoType="dijit.layout.ContentPane" region="right" style="width: 200px; padding:5px;">
                        <span class="label">
                            <label class="inActive" for="linkList">
                                <fmt:message key="dialog.links.list" />
                            </label>
                        </span>
                        <div dojoType="dijit.layout.ContentPane">
                            <span class="label">
                                <label class="inActive" for="linkListObject">
                                    <fmt:message key="dialog.links.objects" />
                                </label>
                            </span>
                            <div class="tableContainer">
                                <div id="linkListObject" autoHeight="9" class="hideTableHeader" query="{relationType:'-9'}">
                                </div>
                            </div>
                            <div class="spacer">
                            </div>
                            <div dojoType="dijit.layout.ContentPane">
                                <span class="label">
                                    <label for="linkListURL" class="inActive">
                                        URL
                                    </label>
                                </span>
                                <div class="tableContainer">
                                    <div id="linkListURL" autoHeight="9" class="hideTableHeader" query="{relationType:'-9'}">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!-- RIGHT HAND SIDE CONTENT END -->
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
