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
            
            var scriptScope = _container_;
            
            var generalUiInputElements = [["linksFromFieldName", "linksFromFieldNameLabel"]];
            var objUiInputElements = [["linksToObjectName", "linksToObjectNameLabel"], ["linksToObjectClass", "linksToObjectClassLabel"]];
            var urlUiInputElements = [["linksToURLName", "linksToURLNameLabel"], ["linksToURL", "linksToURLLabel"]];
            
            // All the possible values for the select box "Verweisbeziehungen"
            var syslist2000Map = [];

            // data passed by caller of this dialog
            var caller = {};

            dojo.connect(_container_, "onLoad", doDeferredThenInit);
//            dojo.connect(_container_, "onUnload", closeThisDialog);

            function doDeferredThenInit() {
                // first do deferred stuff, takes some time, when finished, call init() !

                // read our "Verweisbeziehungen" from syslist
                var def = UtilSyslist.readSysListData(2000);
                def.then(function(syslistData) {
                    syslist2000Map = UtilSyslist.convertSysListToTableData(syslistData);
                    init();
                });
            }

            function init(){
                // take over passed data
                if (_container_.customParams) {
                    caller = _container_.customParams;
                }

                console.debug("mdek_links_dialog -> data from caller");
                console.debug(caller);

                createDOMElements();
                resetRequiredInputElements();

                // Init static data
                var objectName = dijit.byId("objectName").getValue();
                dijit.byId("linksFromObjectName").setValue(objectName);

                // initialize type widget dependent from passed filter 
                var referenceWidget = dijit.byId("linksFromFieldName");
                if (caller.filter) {
                    // Disable the type input and set it to the filter
                    referenceWidget.setDisabled(true);
                    var filterStr = caller.filter + '';
                    initReferenceWidget(filterStr);
                    var def = UtilStore.getItemByAttribute(referenceWidget.store, "entryId", filterStr)
                    def.then(function(item) {
                        referenceWidget.set("item", item);
                    });
                } else {
                    // set values from syslist dependent from object class
                    initReferenceWidget();
                    referenceWidget.setValue("");
                }

                // adapt UI and data dependent from passed data (create new link or edit link ?)
                if (caller.selectedRow) {

                    // EDIT ROW ! Link type canNOT be changed !
                    dojo.byId("inputLinkTypeRadioButtons").style.display = "none";
                    
                    console.debug("UtilString.hasValue(caller.selectedRow.objectClass)");
                    console.debug(UtilString.hasValue(caller.selectedRow.objectClass));
                    
                    if (UtilString.hasValue(caller.selectedRow.objectClass)) {
                        curSelectedObject = dojo.clone(caller.selectedRow);
                        selectLinkType("obj");
                        setObjectData(curSelectedObject);
                    } else {
                        curSelectedUrl = dojo.clone(caller.selectedRow);
                        selectLinkType("url");
                        setUrlData(curSelectedUrl);
                    }

                    // Update the 'save' button text
                    dijit.byId("saveButton").setLabel("<fmt:message key='dialog.links.apply' />");

                } else {

                    // NEW ROW ! Link type can be chosen !
                    dojo.byId("inputLinkTypeRadioButtons").style.display = "";

                    // initial display is object stuff
                    selectLinkType("obj");

                    // Init the radio buttons onclick functions
                    dojo.byId("linksLinkType1").onclick = function(){
                        selectLinkType("obj");
                    }
                    dojo.byId("linksLinkType2").onclick = function(){
                        selectLinkType("url");
                    }
                    resetInputFields();
                }
                
                disableInputElementsOnWrongPermission();
            };
            
            createDOMElements = function() {
                var storeProps = {data: {identifier: 'entryId', label: 'name'}};
                // initialize with empty store !
                createComboBox("linksFromFieldName", null, storeProps, null);
                dijit.byId("linksFromFieldName").searchAttr = "name";

                // storeProps data attributes overwritten by json data
                createSelectBox("linksToURLType", null, storeProps, null, "js/data/urlReferenceTypes.json");
                createSelectBox("linksToObjectClass", null, storeProps, null, "js/data/objectclasses.json");
            }
            
            setObjectData = function(objectData) {
                var typeWidget = dijit.byId("linksFromFieldName");
                if (objectData.relationType == -1) {
                    typeWidget.setValue(objectData.relationTypeName);
                }
                else {
                    var def = UtilStore.getItemByAttribute(typeWidget.store, "entryId", objectData.relationType);
                    def.then(function(item) {
                        typeWidget.set("item", item);
                    });
                }
                dijit.byId("linksToObjectName").setValue(objectData.title);
                dijit.byId("linksToObjectClass").setValue("Class" + objectData.objectClass);
                dijit.byId("linksToObjectDescription").setValue(objectData.relationDescription);
            }
            
            setUrlData = function(urlData) {
                var typeWidget = dijit.byId("linksFromFieldName");
                if (urlData.relationType == -1) {
                    typeWidget.setValue(urlData.relationTypeName);
                } else {
                    var def = UtilStore.getItemByAttribute(typeWidget.store, "entryId", urlData.relationType);
                    def.then(function(item) {
                        typeWidget.set("item", item);
                    });
                }
                dijit.byId("linksToURL").setValue(urlData.url);
                dijit.byId("linksToURLName").setValue(urlData.name);
                dijit.byId("linksToURLType").setValue(urlData.urlType);
                dijit.byId("linksToUrlDescription").setValue(urlData.description);
            }
            
            disableInputElementsOnWrongPermission = function(){
                if (currentUdk.writePermission == false) {
                    dijit.byId("saveButton").setDisabled(true);
                    dijit.byId("resetButton").setDisabled(true);

                    dijit.byId("linksFromFieldName").setDisabled(true);
                    dojo.byId("linksLinkType1").setDisabled(true);
                    dojo.byId("linksLinkType2").setDisabled(true);

                    // object input fields
                    dijit.byId("linksToObjectName").setDisabled(true);
                    dijit.byId("linksToObjectClass").setDisabled(true);
                    dijit.byId("linksToObjectDescription").setDisabled(true);
                    
                    // URL Input Fields
                    dijit.byId("linksToURLName").setDisabled(true);
                    dijit.byId("linksToURL").setDisabled(true);
                    dijit.byId("linksToURLType").setDisabled(true);
                    dijit.byId("linksToUrlDescription").setDisabled(true);
                }
            }

            // resets marked fields with wrong input
            resetRequiredInputElements = function() {
                dojo.query(".important").forEach(function(item) {dojo.removeClass(item, "important");});
            }

            // marks fields if wrong input
            validateInputElements = function() {
                var valid = true;
                var objSelected = dojo.byId("linksLinkType1").checked;
                var validate = function(widgetLabelList) {
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

            // Initialises the 'linksFromFieldName' select box depending on the current obj class and the filter id
            function initReferenceWidget(filter){
                var initialValues = [];
                if (filter) {
                    dojo.forEach(syslist2000Map, function(item){
                        if (item.entryId == filter) 
                            initialValues.push(item);
                    });
                }
                else {
                    var objectClass = UtilUdk.getCurrentObjectClass();
                    console.debug("objectClass field value: '" + dijit.byId("objectClass").getValue() + "'");
                    console.debug("extracted objectClass: '" + objectClass + "'");
                    
                    var idList = [];
                    switch (objectClass) {
                        case "0":
                            idList = ["9990", "9999"];
                            break;
                        case "1":
                            idList = ["3570", "3520", "3515", "3535", "3555", "5066", "9990", "9999"];
                            break;
                        case "2":
                            idList = ["3345", "9990", "9999"];
                            break;
                        case "3":
                            idList = ["3210", "9990", "9999"];
                            break;
                        case "4":
                            idList = ["9990", "9999"];
                            break;
                        case "5":
                            idList = ["3100", "3109", "9990", "9999"];
                            break;
                        case "6":
                            idList = ["3210", "9990", "9999"];
                            break;
                        default:
                            console.debug("Error: could not determine object class.");
                            break;
                    }
                    initialValues = dojo.filter(syslist2000Map, function(item) {
                        return dojo.some(idList, function(id){
                            return id == item.entryId;
                        });
                    });
/*
                    // add empty item at the beginning. ONLY IF SELECT BOX !
                    if (initialValues.length > 1) {
                        var emptyData = [{entryId:-1, name:""}];
                        initialValues = emptyData.concat(initialValues);
                    }
*/
                }
//                console.debug("initialValues: " + initialValues);
//                console.debug(initialValues);

                // which data attributes for id and label (to show)
                var storeProps = {
                    searchAttr: "name",
                    data: {
                        label: "name",
                        identifier: "entryId",
                        items: initialValues
                    }
                };
                dijit.byId("linksFromFieldName").store = new dojo.data.ItemFileWriteStore(storeProps);
            }
            
            saveLink = function(){
                var objSelected = dojo.byId("linksLinkType1").checked;

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
                    currentLink.relationDescription = dijit.byId("linksToObjectDescription").getValue();
                    UtilList.addObjectLinkLabels([currentLink]);
                } else {
                    // Take the current selected url and add the values that were entered in the ui fields
                    currentLink = curSelectedUrl;
                    currentLink.url = dijit.byId("linksToURL").getValue();
                    currentLink.name = dijit.byId("linksToURLName").getValue();
                    currentLink.urlType = dijit.byId("linksToURLType").getValue();
                    currentLink.description = dijit.byId("linksToUrlDescription").getValue();
                    UtilList.addUrlLinkLabels([currentLink]);
                }
                // then take over stuff for both OBJECT and URL !
                var linkTypeWidget = dijit.byId("linksFromFieldName");
                currentLink.relationType = linkTypeWidget.item == null ? -1 : linkTypeWidget.item.entryId[0];
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
            
            function selectLinkType(e) {
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
                
                if (!caller.filter) {
                    dijit.byId("linksFromFieldName").setDisabled(false);
                    dijit.byId("linksFromFieldName").setValue("");
                }
                
                dijit.byId("linksToObjectName").setValue("");
                dijit.byId("linksToObjectClass").setValue("");
                dijit.byId("linksToObjectDescription").setValue("");
                dijit.byId("linksToURL").setValue("http://");
                dijit.byId("linksToURLName").setValue("");
                dijit.byId("linksToURLType").setValue("");
                dijit.byId("linksToUrlDescription").setValue("");
            }
            
            // Cancel Button onClick function
            resetInput = function() {
                closeThisDialog();
            }
            
            closeThisDialog = function() {
                // we can't clear in a way, that we can immediately reselect the row !? :(
                // so we keep selection, user has to click to other row, before reselecting !
//                UtilGrid.clearSelection(caller.gridId);
//                UtilGrid.getTable(caller.gridId).invalidate();
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
                <div id="linksContent" class="content">
                   <!-- CONTENT START -->
                    <div class="inputContainer" dojoType="dijit.layout.ContentPane" region="center" style="padding:15px;">
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
                                        <input type="text" id="linksFromObjectName" name="linksFromObjectName" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width: 100%;" />
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
                                        <input id="linksFromFieldName" style="width: 100%;" maxLength="255">
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
                          </span><!-- RADIO BUTTONS-->
                            <span class="outer" style="padding-top:5px;">
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
                                            <input type="text" id="linksToObjectName" name="linksToObjectName" disabled="true" dojoType="dijit.form.ValidationTextBox" style="width: 100%;" />
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
                                            <label for="linksToObjectDescription" onclick="javascript:dialog.showContextHelp(arguments[0], 2110)">
                                                <fmt:message key="dialog.links.objDescription" />
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" mode="textarea" id="linksToObjectDescription" name="linksToObjectDescription" class="textAreaFull" dojoType="dijit.form.SimpleTextarea" />
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
                                            <label id="linksToURLNameLabel" for="linksToURLName" onclick="javascript:dialog.showContextHelp(arguments[0], 2210)">
                                                <fmt:message key="dialog.links.urlDescription" />*
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxlength="255" id="linksToURLName" name="linksToURLName" dojoType="dijit.form.ValidationTextBox" style="width:100%;" />
                                        </span>
                                    </div>
                                </span>
                                <span class="outer required" style="width:66%;">
                                    <div>
                                        <span class="label">
                                            <label id="linksToURLLabel" for="linksToURL" onclick="javascript:dialog.showContextHelp(arguments[0], 2200)">
                                                <fmt:message key="dialog.links.url" />*
                                            </label>
                                        </span>
                                        <span class="input">
                                            <input type="text" maxLength="255" id="linksToURL" name="linksToURL" dojoType="dijit.form.ValidationTextBox" style="width:100%;" />
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
                                            <input id="linksToURLType" style="width: 100%;">
                                        </span>
                                    </div>
                                </span>
                                <div class="fill">
                                </div>
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
                            <span id="inputButtons">
                                <div class="inputContainer">
                                    <span class="button">
                                        <span style="float: right;">
                                            <button dojoType="dijit.form.Button" id="resetButton" onClick="resetInput">
                                                <fmt:message key="dialog.links.cancel" />
                                            </button>
                                        </span>
                                        <span style="float: right; padding-right: 5px;">
                                            <button id="saveButton" dojoType="dijit.form.Button" onClick="saveLink" type="button">
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
