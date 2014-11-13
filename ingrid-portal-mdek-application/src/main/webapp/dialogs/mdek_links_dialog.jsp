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
<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <title><fmt:message key="dialog.popup.serviceLink.link" /></title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <meta name="author" content="wemove digital solutions" />
        <meta name="copyright" content="wemove digital solutions GmbH" />
        <script type="text/javascript">

        var pageLinksDialog = _container_;
        require(["dojo/_base/lang",
            "dojo/_base/array",
            "dojo/on",
            "dojo/Deferred",
            "dojo/promise/all",
            "dojo/dom",
            "dojo/topic",
            "dojo/dom-class",
            "dojo/dom-style",
            "dojo/query",
            "dijit/registry",
            "dojo/data/ItemFileWriteStore",
            "ingrid/layoutCreator",
            "ingrid/dialog",
            "ingrid/utils/Syslist",
            "ingrid/utils/String",
            "ingrid/utils/Store",
            "ingrid/utils/UDK",
            "ingrid/utils/List",
            "ingrid/utils/Grid",
            "ingrid/utils/UI",
            "ingrid/hierarchy/requiredChecks"
        ], function(lang, array, on, Deferred, all, dom, topic, domClass, style, query, registry, ItemFileWriteStore, layoutCreator, dialog, UtilSyslist, UtilString, UtilStore, UtilUdk, UtilList, UtilGrid, UtilUI, checks) {

                var curSelectedObject = {};
                var curSelectedUrl = {};

                // All the possible values for the select box "Verweisbeziehungen"
                var syslist2000Map = [];

                // data passed by caller of this dialog
                var caller = {};

                on(_container_, "Load", doDeferredThenInit);
                //            on(_container_, "onUnload", closeThisDialog);

                function doDeferredThenInit() {
                    // first do deferred stuff, takes some time, when finished, call init() !

                    // read our "Verweisbeziehungen" from syslist
                    UtilSyslist.readSysListData(2000)
                    .then(function(syslistData) {
                        syslist2000Map = UtilSyslist.convertSysListToTableData(syslistData);
                        init();
                        console.log("Publishing event: '/afterInitDialog/LinksDialog'");
                        topic.publish("/afterInitDialog/LinksDialog");
                    });
                }

                function init() {
                    // take over passed data
                    if (_container_.customParams) {
                        caller = _container_.customParams;
                    }

                    console.debug("mdek_links_dialog -> data from caller");
                    console.debug(caller);

                    var def = createDOMElements();
                    resetRequiredInputElements();

                    // Init static data
                    var objectName = registry.byId("objectName").get("value");
                    registry.byId("linksFromObjectName").set("value", objectName);

                    // initialize type widget dependent from passed filter 
                    var referenceWidget = registry.byId("linksFromFieldName");
                    if (caller.filter) {
                        // Disable the type input and set it to the filter
                        referenceWidget.setDisabled(true);
                        var filterStr = caller.filter + '';
                        // initReferenceWidget(filterStr);
                        UtilStore.getItemByAttribute(referenceWidget.store, "entryId", filterStr)
                        .then(function(item) {
                            referenceWidget.set("item", item);
                        });
                    } else {
                        // set values from syslist dependent from object class
                        // initReferenceWidget();
                        referenceWidget.set("value", "");
                    }

                    // adapt UI and data dependent from passed data (create new link or edit link ?)
                    if (caller.selectedRow) {

                        // EDIT ROW ! Link type canNOT be changed !
                        dom.byId("inputLinkTypeRadioButtons").style.display = "none";

                        console.debug("UtilString.hasValue(caller.selectedRow.objectClass)");
                        console.debug(UtilString.hasValue(caller.selectedRow.objectClass));

                        if (UtilString.hasValue(caller.selectedRow.objectClass)) {
                            curSelectedObject = lang.clone(caller.selectedRow);
                            selectLinkType("obj");
                            def.then(lang.partial(setObjectData, curSelectedObject));
                        } else {
                            curSelectedUrl = lang.clone(caller.selectedRow);
                            selectLinkType("url");
                            def.then(lang.partial(setUrlData, curSelectedUrl));
                        }

                        // Update the 'save' button text
                        registry.byId("saveButton").setLabel("<fmt:message key='dialog.links.apply' />");

                    } else {

                        // NEW ROW ! Link type can be chosen !
                        dom.byId("inputLinkTypeRadioButtons").style.display = "";

                        // initial display is object stuff
                        selectLinkType("obj");

                        // Init the radio buttons onclick functions
                        dom.byId("linksLinkType1").onclick = function() {
                            selectLinkType("obj");
                        };
                        dom.byId("linksLinkType2").onclick = function() {
                            selectLinkType("url");
                        };
                        def.then(resetInputFields);
                    }

                    disableInputElementsOnWrongPermission();
                }

                function createDOMElements() {
                    var defs = [];
                    // "Verweistyp"
                    // initialize with empty store !
                    var storeProps = {
                        data: {
                            identifier: 'entryId',
                            label: 'name'
                        }
                    };
                    defs.push(layoutCreator.createComboBox("linksFromFieldName", null, storeProps, lang.partial(getLinkTypes, caller.filter)));
                    registry.byId("linksFromFieldName").searchAttr = "name";

                    // React on change of content of "Verweistyp"
                    on(registry.byId("linksFromFieldName"), "Change", handleRelationTypeChange);

                    // Datenformat
                    storeProps = {
                        data: {
                            identifier: '1',
                            label: '0'
                        }
                    };
                    defs.push(layoutCreator.createComboBox("linksToDataType", null, storeProps, function() {
                        return UtilSyslist.getSyslistEntry(1320);
                    }));

                    // storeProps data attributes overwritten by json data
                    storeProps = {
                        data: {
                            identifier: 'abbreviation',
                            label: 'label'
                        }
                    };
                    defs.push(layoutCreator.createFilteringSelect("linksToURLType", null, storeProps, null, "js/data/urlReferenceTypes.json"));
                    storeProps = {
                        data: {
                            identifier: 'id',
                            label: 'name'
                        }
                    };
                    defs.push(layoutCreator.createSelectBox("linksToObjectClass", null, storeProps, null, "js/data/objectclasses.json"));

                    return all(defs);
                }

                function setObjectData(objectData) {
                    var typeWidget = registry.byId("linksFromFieldName");
                    if (objectData.relationType == -1) {
                        typeWidget.set("value", objectData.relationTypeName);
                    } else {
                        var def = UtilStore.getItemByAttribute(typeWidget.store, "entryId", objectData.relationType);
                        def.then(function(item) {
                            typeWidget.set("item", item);
                        });
                    }
                    registry.byId("linksToObjectName").set("value", objectData.title);
                    registry.byId("linksToObjectClass").set("value", "Class" + objectData.objectClass);
                    registry.byId("linksToObjectDescription").set("value", objectData.relationDescription);
                }

                function setUrlData(urlData, res, res2) {
                    var typeWidget = registry.byId("linksFromFieldName");
                    if (urlData.relationType == -1) {
                        typeWidget.set("value", urlData.relationTypeName);
                    } else {
                        var def = UtilStore.getItemByAttribute(typeWidget.store, "entryId", urlData.relationType);
                        def.then(function(item) {
                            typeWidget.set("item", item);
                        });
                    }
                    registry.byId("linksToURL").set("value" , urlData.url);
                    registry.byId("linksToURLName").set("value" , urlData.name);
                    registry.byId("linksToDataType").set("value" , urlData.datatype);
                    registry.byId("linksToURLType").set("value" , urlData.urlType);
                    registry.byId("linksToUrlDescription").set("value" , urlData.description);
                }

                function disableInputElementsOnWrongPermission() {
                    if (currentUdk.writePermission === false) {
                        registry.byId("saveButton").setDisabled(true);
                        registry.byId("resetButton").setDisabled(true);

                        registry.byId("linksFromFieldName").setDisabled(true);
                        dom.byId("linksLinkType1").setDisabled(true);
                        dom.byId("linksLinkType2").setDisabled(true);

                        // object input fields
                        registry.byId("linksToObjectName").setDisabled(true);
                        registry.byId("linksToObjectClass").setDisabled(true);
                        registry.byId("linksToObjectDescription").setDisabled(true);

                        // URL Input Fields
                        registry.byId("linksToURLName").setDisabled(true);
                        registry.byId("linksToURL").setDisabled(true);
                        registry.byId("linksToDataType").setDisabled(true);
                        registry.byId("linksToURLType").setDisabled(true);
                        registry.byId("linksToUrlDescription").setDisabled(true);
                    }
                }

                // resets marked fields with wrong input
                function resetRequiredInputElements() {
                    query(".important").forEach(function(item) {
                        domClass.remove(item, "important");
                    });
                }

                // marks fields if wrong input
                function validateInputElements() {
                    var valid = true;
                
                    var visibleRequiredElements = query(".header .required .dijitTextBox, .header .required .dijitSelect, #linkToObject:not(.hide) .required .dijitTextBox, #linkToObject:not(.hide) .required .dijitSelect, #linkToURL:not(.hide) .required .dijitTextBox, #linkToURL:not(.hide) .required .dijitSelect", "pageDialog").map(function(item) {return item.getAttribute("widgetid");});

                    dojo.forEach(visibleRequiredElements, function(id){
                        var value = dijit.byId(id).get("value");
                        if (!value || value === "") {
                            valid = false;
                            checks.setErrorLabel(id);
                        }
                    });

                    return valid;
                }

                // Initialises the 'linksFromFieldName' select box depending on the current obj class and the filter id
                function getLinkTypes(filter) {
                    var def = new Deferred();

                    var initialValues = [];
                    if (filter) {
                        array.forEach(syslist2000Map, function(item) {
                            if (item.entryId == filter)
                                initialValues.push(item);
                        });
                    } else {
                        var objectClass = UtilUdk.getCurrentObjectClass();
                        //console.debug("objectClass field value: '" + registry.byId("objectClass").getValue() + "'");
                        console.debug("extracted objectClass: '" + objectClass + "'");

                        var idList = [];
                        array.forEach(syslist2000Map, function(entry) {
                            if (entry.data) {
                                var containsClass = array.indexOf(entry.data.split(','), objectClass) !== -1;
                                if (containsClass) idList.push(entry.entryId);
                            }
                        });

                        initialValues = array.filter(syslist2000Map, function(item) {
                            return array.some(idList, function(id) {
                                return id == item.entryId;
                            });
                        });
                    }

                    // which data attributes for id and label (to show)
                    // var storeProps = {
                    //     searchAttr: "name",
                    //     data: {
                    //         label: "name",
                    //         identifier: "entryId",
                    //         items: initialValues
                    //     }
                    // };
                    // registry.byId("linksFromFieldName").store = new ItemFileWriteStore(storeProps);
                    def.resolve(initialValues);
                    return def.promise;
                }

                function handleRelationTypeChange(typeName) {
                    console.debug("handleRelationTypeChange: " + typeName);

                    var typeKey = UtilSyslist.getSyslistEntryKey(2000, typeName);

                    // If not "Datendownload" then hide "Dateiformat"
                    if (typeKey == 9990) {
                        // reduce left input field and show field
                        style.set("uiElement2200", "width", "60%");
                        UtilUI.setShow(dom.byId("uiElement2240"));
                    } else {
                        // widen left input field and hide field and clean data
                        style.set("uiElement2200", "width", "80%");
                        registry.byId("linksToDataType").set("value", "");
                        UtilUI.setHide(dom.byId("uiElement2240"));
                    }
                }

                function saveLink() {
                    var params = { abort: false };
                    // do some externally defined actions which can abort the closing of the dialog
                    console.log("Publishing event: '/onBeforeDialogAccept/LinksDialog' with parameter 'abort'");
                    dojo.publish("/onBeforeDialogAccept/LinksDialog", [params]);
                    if (params.abort) return;

                    var objSelected = dom.byId("linksLinkType1").checked;

                    // validate input !
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

                    // take over data ! NOTICE: this is a copy of the selected row of calling grid !
                    // first special stuff for OBJECT / URL !
                    var currentLink;

                    if (objSelected) {
                        // Take the current selected object and add the values that were entered in the ui fields
                        // NOTICE: The curSelectedObject was selected by object tree dialog and already has all object data !
                        currentLink = curSelectedObject;
                        // this is the only field that can be changed after selection !
                        currentLink.relationDescription = registry.byId("linksToObjectDescription").get("value");
                        UtilList.addObjectLinkLabels([currentLink]);
                    } else {
                        // Take the current selected url and add the values that were entered in the ui fields
                        currentLink = curSelectedUrl;
                        currentLink.url = registry.byId("linksToURL").get("value");
                        currentLink.name = registry.byId("linksToURLName").get("value");
                        currentLink.datatype = registry.byId("linksToDataType").get("value");
                        currentLink.urlType = registry.byId("linksToURLType").get("value");
                        currentLink.description = registry.byId("linksToUrlDescription").get("value");
                        UtilList.addUrlLinkLabels([currentLink]);
                    }
                    // then take over stuff for both OBJECT and URL !
                    var linkTypeWidget = registry.byId("linksFromFieldName");
                    currentLink.relationType = linkTypeWidget.item === null ? -1 : linkTypeWidget.item.entryId;
                    currentLink.relationTypeName = linkTypeWidget.get("value");
                    //                console.debug("currentLink to save !");
                    //                console.debug(currentLink);
                    UtilList.addIcons([currentLink]);


                    // update calling grid ! Here we use the selected row in calling grid !
                    if (caller.selectedRow) {
                        // EDIT existing link
                        // we only change the stuff which is stored or is visible

                        if (objSelected) {
                            caller.selectedRow.uuid = currentLink.uuid;
                            caller.selectedRow.relationDescription = currentLink.relationDescription;
                            caller.selectedRow.objectClass = currentLink.objectClass;
                            caller.selectedRow.title = currentLink.title;
                        } else {
                            caller.selectedRow.url = currentLink.url;
                            caller.selectedRow.name = currentLink.name;
                            caller.selectedRow.datatype = currentLink.datatype;
                            caller.selectedRow.urlType = currentLink.urlType;
                            caller.selectedRow.description = currentLink.description;
                        }

                        caller.selectedRow.relationType = currentLink.relationType;
                        caller.selectedRow.relationTypeName = currentLink.relationTypeName;
                        caller.selectedRow.icon = currentLink.icon;
                        caller.selectedRow.linkLabel = currentLink.linkLabel;

                    } else {
                        // add NEW link
                        // No checks if the store already contains the current element.
                        UtilGrid.addTableDataRow(caller.gridId, currentLink);

                        // also add to "global" link table in "Verweise"
                        if (caller.gridId != "linksTo") {
                            UtilGrid.addTableDataRow("linksTo", currentLink);
                        }
                    }

                    var callerGrid = UtilGrid.getTable(caller.gridId);
                    callerGrid.invalidate();
                    callerGrid.notifyChangedData({});

                    // "global" link table in "Verweise"
                    //                UtilGrid.getTable("linksTo").invalidate();

                    // save also closes dialog !
                    closeThisDialog();
                }

                function showAssignObjectDialog() {
                    if (currentUdk.writePermission === false) {
                        return;
                    }

                    var deferred = new Deferred();
                    var setSelectedObject = function(obj) {
                        if (obj.uuid != "objectRoot") {
                            curSelectedObject = obj;
                            registry.byId("linksToObjectName").set("value", curSelectedObject.title);
                            registry.byId("linksToObjectClass").set("value", "Class" + curSelectedObject.objectClass);
                        }
                    };

                    deferred.then(setSelectedObject);

                    if (curSelectedObject) {
                        dialog.showPage("<fmt:message key='dialog.links.selectObject.title' />", 'dialogs/mdek_links_select_object_dialog.jsp?c=' + userLocale, 522, 520, true, {
                            // custom parameters
                            resultHandler: deferred,
                            jumpToNode: curSelectedObject.uuid
                        });
                    } else {
                        dialog.showPage("<fmt:message key='dialog.links.selectObject.title' />", 'dialogs/mdek_links_select_object_dialog.jsp?c=' + userLocale, 522, 520, true, {
                            // custom parameters
                            resultHandler: deferred
                        });
                    }
                }

                function selectLinkType(e) {
                    if (this.id == "linksLinkType2" || e == "url") {
                        domClass.add("linkToObject", "hide");
                        domClass.remove("linkToURL", "hide");
                        dom.byId("linksLinkType1").checked = false;
                        dom.byId("linksLinkType2").checked = true;
                    } else {
                        domClass.remove("linkToObject", "hide");
                        domClass.add("linkToURL", "hide");
                        dom.byId("linksLinkType1").checked = true;
                        dom.byId("linksLinkType2").checked = false;
                    }
                }

                function resetInputFields() {
                    // Reset field values
                    curSelectedObject = {};
                    curSelectedUrl = {};

                    if (!caller.filter) {
                        registry.byId("linksFromFieldName").setDisabled(false);
                        registry.byId("linksFromFieldName").set("value", "");
                    }

                    registry.byId("linksToObjectName").set("value", "");
                    registry.byId("linksToObjectClass").set("value", "");
                    registry.byId("linksToObjectDescription").set("value", "");
                    registry.byId("linksToURL").set("value", "http://");
                    registry.byId("linksToURLName").set("value", "");
                    registry.byId("linksToDataType").set("value", "");
                    registry.byId("linksToURLType").set("value", "");
                    registry.byId("linksToUrlDescription").set("value", "");
                }

                // Cancel Button onClick function
                function resetInput() {
                    closeThisDialog();
                }

                function closeThisDialog() {
                    // we can't clear in a way, that we can immediately reselect the row !? :(
                    // so we keep selection, user has to click to other row, before reselecting !
                    //                UtilGrid.clearSelection(caller.gridId);
                    //                UtilGrid.getTable(caller.gridId).invalidate();
                    registry.byId("pageDialog").hide();
                }


                /**
                 * PUBLIC METHODS
                 */
                pageLinksDialog.saveLink = saveLink;
                pageLinksDialog.resetInput = resetInput;
                pageLinksDialog.showAssignObjectDialog = showAssignObjectDialog;
            });
        </script>
    </head>
    <body>
        <div id="mainContentTest" data-dojo-type="dijit/layout/ContentPane">
            <div id="links">
             <div id="winNavi" style="right:10px;">
                        <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-7#maintanance-of-objects-7', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
                    </div>
                <br>
                <br>   
                <div id="linksContent" class="content">
                   <!-- CONTENT START -->
                    <div class="inputContainer" data-dojo-type="dijit/layout/ContentPane" region="center" style="padding:15px;">
                        <span class="label">
                            <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7034)">
                                <fmt:message key="dialog.links.source" />
                            </label>
                        </span>
                        <div class="outlined header">
                            <span class="outer">
                                <div>
                                    <span class="label">
                                        <label for="linksFromObjectName" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7035)">
                                            <fmt:message key="dialog.links.objTitle" />
                                        </label>
                                    </span>
                                    <span class="input">
                                        <input type="text" id="linksFromObjectName" name="linksFromObjectName" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width: 100%;" />
                                    </span>
                                </div>
                            </span>
                            <span class="outer required">
                                <div>
                                    <span id="linksFromFieldNameLabel" class="label">
                                        <label for="linksFromFieldName" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2000)">
                                            <fmt:message key="dialog.links.link" />*
                                        </label>
                                    </span>
                                    <span class="input">
                                        <input id="linksFromFieldName" style="width: 100%;" maxLength="255" required="true">
                                    </span>
                                </div>
                            </span>
                            <div class="fill">
                            </div>
                        </div>
                        <div class="fill"></div>
                        <span id="inputFields">
                          <!-- RADIO BUTTONS-->
                          <span id="inputLinkTypeRadioButtons" style="display: none;">
                            <span class="outer required">
                                <div>
                                    <span class="label">
                                        <label for="linksLinkType" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7037)">
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
                          </span><!-- RADIO BUTTONS-->
                            <span class="outer" style="padding-top:5px;">
                            <span class="label">
                                <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7038)">
                                    <fmt:message key="dialog.links.target" />
                                </label>
                            </span><!-- VERWEIS AUF OBJEKT -->
                            <div id="linkToObject" class="outlined">
                                <span class="outer required">
                                    <div>
                                        <span class="label">
                                            <label id="linksToObjectNameLabel" for="linksToObjectName" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2100)">
                                                <fmt:message key="dialog.links.objTitle" />*
                                            </label>
                                        </span>
                                        <span class="functionalLink">
                                            <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a id="assignObjectDialogLink" href="javascript:void(0);" onclick="pageLinksDialog.showAssignObjectDialog()" title="<fmt:message key="dialog.links.selectObject" /> [Popup]"><fmt:message key="dialog.links.selectObject" /></a>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="linksToObjectName" name="linksToObjectName" disabled="true" data-dojo-type="dijit/form/ValidationTextBox" style="width: 100%;" required="true" />
                                        </span>
                                    </div>
                                </span>
                                <span class="outer required">
                                    <div>
                                        <span class="label">
                                            <label id="linksToObjectClassLabel" for="linksToObjectClass" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7040)">
                                                <fmt:message key="dialog.links.objClass" />*
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="linksToObjectClass" name="linksToObjectClass" style="width:100%;" disabled="true" required="true"/>
                                        </span>
                                    </div>
                                </span>
                                <span class="outer">
                                    <div>
                                        <span class="label">
                                            <label for="linksToObjectDescription" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2110)">
                                                <fmt:message key="dialog.links.objDescription" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" mode="textarea" id="linksToObjectDescription" name="linksToObjectDescription" class="textAreaFull" data-dojo-type="dijit/form/SimpleTextarea" />
                                        </span>
                                    </div>
                                </span>
                                <div class="fill"></div>
                            </div>
                            <!-- VERWEIS AUF URL -->
                            <div id="linkToURL" class="outlined">
                                <span class="outer required" style="width:100%;">
                                    <div>
                                        <span class="label">
                                            <label id="linksToURLNameLabel" for="linksToURLName" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2210)">
                                                <fmt:message key="dialog.links.urlDescription" />*
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxlength="255" id="linksToURLName" name="linksToURLName" data-dojo-type="dijit/form/ValidationTextBox" style="width:100%;" required="true" />
                                        </span>
                                    </div>
                                </span>
                                <span id="uiElement2200" class="outer required" style="width:60%;">
                                    <div>
                                        <span class="label">
                                            <label id="linksToURLLabel" for="linksToURL" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2200)">
                                                <fmt:message key="dialog.links.url" />*
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="linksToURL" name="linksToURL" data-dojo-type="dijit/form/ValidationTextBox" style="width:100%;" required="true" />
                                        </span>
                                    </div>
                                </span>
                                <span id="uiElement2240" class="outer" style="width:20%;">
                                    <div>
                                        <span class="label">
                                            <label id="linksToDataTypeLabel" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2240)">
                                                <fmt:message key="dialog.links.dataType" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input id="linksToDataType" style="width: 100%;">
                                        </span>
                                    </div>
                                </span>
                                <span class="outer" style="width:20%;">
                                    <div>
                                        <span class="label">
                                            <label for="linksToURLType" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2251)">
                                                <fmt:message key="dialog.links.urlType" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input id="linksToURLType" style="width: 100%;">
                                        </span>
                                    </div>
                                </span>
                                <div class="fill">
                                </div>
                                <span class="outer">
                                    <div>
                                        <span class="label">
                                            <label for="linksToUrlDescription" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 2260)">
                                                <fmt:message key="dialog.links.objDescription" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" id="linksToUrlDescription" name="linksToUrlDescription" class="textAreaFull" data-dojo-type="dijit/form/SimpleTextarea" />
                                        </span>
                                    </div>
                                </span>
                                <div class="fill">
                                </div>
                            </div>
                            </span>
                            <span id="inputButtons">
                                <div class="inputContainer">
                                    <span class="button">
                                        <span style="float: right;">
                                            <button data-dojo-type="dijit/form/Button" id="resetButton" onclick="pageLinksDialog.resetInput()">
                                                <fmt:message key="dialog.links.cancel" />
                                            </button>
                                        </span>
                                        <span style="float: right; padding-right: 5px;">
                                            <button id="saveButton" data-dojo-type="dijit/form/Button" onclick="pageLinksDialog.saveLink()" type="button">
                                                <fmt:message key="dialog.links.add" />
                                            </button>
                                        </span>
                                    </span>
                                </div>
                            </span>
                        </span>
                    </div><!-- CONTENT END -->
                </div>
            </div>
        </div>
    </body>
</html>
