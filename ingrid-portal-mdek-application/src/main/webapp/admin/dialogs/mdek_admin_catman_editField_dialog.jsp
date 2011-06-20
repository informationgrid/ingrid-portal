<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <title>Edit Field</title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <meta name="author" content="wemove digital solutions" />
        <meta name="copyright" content="wemove digital solutions GmbH" />
        <script type="text/javascript">
            dojo.require("dijit.layout.TabContainer");
            dojo.require("dijit.layout.BorderContainer");
            dojo.require("dojo.data.ItemFileWriteStore");
            dojo.require("dijit.form.Form");
            
            syncRowsInTableProgress = false;
            
            var scriptScope = _container_;
            scriptScope.info = {};
            
            // onload function below all functions since IE does not understand this event
            // for a container
            dojo.connect(_container_, "onLoad", init);
            
            function init(){
                scriptScope.info.edit = _container_.customParams.edit;
                scriptScope.createDynamicElements(_container_.customParams.item);
                scriptScope.fillData(_container_.customParams.item, _container_.customParams.type);
            }
            
            scriptScope.createDynamicElements = function(data){
                dojo.forEach(scopeAdminFormFields.profileData.languages, function(lang){
                    // create localized labels
                    //console.debug("create: " + "formTitle_" + lang);
                    var titleWidget = new dijit.form.TextBox({
                        id: "formTitle_" + lang,
                        title: lang,
                        'class': "fullWidth"
                    });
                    dijit.byId("formTitle").addChild(titleWidget);
                    
                    // create help messages labels
                    var helpWidget = new dijit.form.TextBox({
                        id: "formHelp_" + lang,
                        title: lang,
                        'class': "fullWidth"
                    });
                    dijit.byId("formHelp").addChild(helpWidget);
                    
                    // create number units
                    var unitWidget = new dijit.form.TextBox({
                        id: "formUnits_" + lang,
                        title: lang,
                        'class': "fullWidth"
                    });
                    dijit.byId("formUnits").addChild(unitWidget);
                });
                
                dojo.connect(dijit.byId("formMandatory"), "onChange", scriptScope.mandatoryChanged);
            }
            
            scriptScope.mandatoryChanged = function(checked) {
                var visForm = dijit.byId("formVisibility");
                // to see the disabled/enabled state of radio box we need
                // to visit it first!
                if (checked) {
                    visForm.set("value", {visible: "optional"});
                    dijit.byId("visibleOptional").disabled = true;
                    visForm.set("value", {visible: "hide"});
                    dijit.byId("visibleHide").disabled = true;
                    visForm.set("value", {visible: "show"});
                } else {
                    dijit.byId("visibleOptional").disabled = false;
                    dijit.byId("visibleHide").disabled = false;
                    var oldValue = visForm.get("value");
                    visForm.set("value", {visible: "optional"});
                    visForm.set("value", {visible: "hidden"});
                    visForm.set("value", oldValue);
                }
            }
            
            scriptScope.fillData = function(profileObjectToEdit, type){
                // remember id in case it is going to be changed
                scriptScope.info.oldId = profileObjectToEdit.id;
                
                //dijit.byId("formType").set('value', profileObjectToEdit.type);
                this.setAndShowField("span_formId", "formId", profileObjectToEdit.id);
                if (profileObjectToEdit.isLegacy) {
                    // remove CSW Script Field for Legacy elements
                    dijit.byId("formScript").removeChild(dijit.byId("formIsoScript"));
                    dijit.byId("formIsoScript").destroy();
                }
                
                // fill form elements with available data
                // according to the available properties the type of this control is defined (layout)
                //console.debug("type: " + type);
                switch (type) {
                    case "tableControl":
                        var countColumn = 0;
                        var lastColumn = null;
                        dojo.forEach(profileObjectToEdit.columns, function(column){
                        	countColumn++;
                            scriptScope.writeColumn(column, countColumn);
                        });
                        this.setAndShowField("span_formNumTableRows", "formNumTableRows", profileObjectToEdit.numTableRows);
                        dojo.removeClass("span_formColumns", "hide");
                        dojo.connect(dijit.byId("formWidth"), "onChange", scriptScope.updateAvailableTableWidth);
                        setTimeout(scriptScope.updateAvailableTableWidth, 1000);
                        // fall through!
                    case "selectControl":
                        if (type == "selectControl") {
                            scriptScope.prepareSelectOptions(profileObjectToEdit);
                            dojo.removeClass("span_formListOptions", "hide");
                        }
                        // fall through!
                    case "numberControl":
                        if (type == "numberControl") 
                            this.setAndShowField("span_formUnits", "formUnits", profileObjectToEdit.unit);
                        // fall through!
                    case "dateControl":
                    case "textControl":
                        this.setAndShowField("span_formTitle", "formTitle", profileObjectToEdit.label);
                        this.setAndShowField("span_formHelp", "formHelp", profileObjectToEdit.helpMessage);
                        dijit.byId("formIsoScript").set("value", profileObjectToEdit.scriptedCswMapping);
                        this.setAndShowField("span_formIndex", "formIndex", profileObjectToEdit.indexName);
                        this.setAndShowField("span_formWidth", "formWidth", profileObjectToEdit.width);
                        this.setAndShowField("span_formIndex", "formIndex", profileObjectToEdit.indexName);
                        if (type == "textControl") 
                            this.setAndShowField("span_formNumTextRows", "formNumTextRows", profileObjectToEdit.numLines);
                        // fall through!
                    case "legacyControl":
                        dijit.byId("formMandatory").set("checked", profileObjectToEdit.isMandatory);
                        dojo.removeClass("span_formMandatory", "hide");
                        dijit.byId("formVisibility").set("value", {visible: profileObjectToEdit.isVisible});
                        dojo.removeClass("span_formVisibility", "hide");
                        this.setAndShowField("span_formScript", "formJsScript", profileObjectToEdit.scriptedProperties);
                        break;
                    case "rubric":
                        this.setAndShowField("span_formTitle", "formTitle", profileObjectToEdit.label);
                        this.setAndShowField("span_formHelp", "formHelp", profileObjectToEdit.helpMessage);
                        break;
                }
                
                // do not allow to change the id, once it was set during creation
                //console.debug(scriptScope.info.edit);
                if (scriptScope.info.edit) {
                    dijit.byId("formId").set("disabled", true);
                    dijit.byId("saveAddField").set("label", "<fmt:message key='dialog.admin.additionalfields.btnUpdate' />");
                }
                
                
            }
            
            scriptScope.prepareSelectOptions = function(profileObjectToEdit) {
                if (profileObjectToEdit.allowFreeEntries) {
                    dijit.byId("formAsCombobox").set("checked", profileObjectToEdit.allowFreeEntries);
                }
                // create table with data
                var formListOptionsStructure = [
                    {field: 'id',name: 'id',width: '30px',editable: false},
                    {field: 'value',name: 'option',width: '200px',editable: true}
                    ];
                //for (lang in profileObjectToEdit.options) {
                dojo.forEach(scopeAdminFormFields.profileData.languages, function(lang){
                    dijit.byId("formListOptions").addChild(new dijit.layout.ContentPane({id:"formListOptions_" + lang + "Container", title: lang, 'class': "innerPadding"}));
                    var divElement = document.createElement("div");
                    divElement.setAttribute("id", "formListOptions_" + lang);
                    dojo.byId("formListOptions_" + lang + "Container").appendChild(divElement);
                    var props = {interactive: "true", moveRows: "true"};
                    createDataGrid("formListOptions_" + lang, null, formListOptionsStructure, null, props);
                    
                    if (profileObjectToEdit.options[lang])
                        UtilGrid.setTableData("formListOptions_" + lang, profileObjectToEdit.options[lang]);
                    
                    // add special action to events on store for synchronization
                    dojo.connect(UtilGrid.getTable("formListOptions_" + lang), "onAddNewRow", scriptScope.syncOptionTableRowsNew);
                    dojo.connect(UtilGrid.getTable("formListOptions_" + lang), "onDataChanged", scriptScope.syncOptionTableRowsDelete);
                    
                });
                scriptScope.checkConsistency();
            }
                        
            scriptScope.syncOptionTableRowsNew = function(msg) {
                if (syncRowsInTableProgress) 
                    return;
                    
                syncRowsInTableProgress = true;
                //console.debug("sync rows");
                // get active tab for language of new item
                var activelang = dijit.byId("formListOptions").selectedChildWidget.title;
                var newId = getNewId(UtilGrid.getTableData("formListOptions_"+activelang));
                msg.item.id = newId;
                msg.item.lang = activelang;
                
                dojo.forEach(scopeAdminFormFields.profileData.languages, function(lang){
                    if (activelang != lang) {
                        var otherLangItem = {id: newId,lang: lang,value: "?"};
                        UtilGrid.addTableDataRow("formListOptions_"+lang, otherLangItem);
                    }
                });
                
                UtilGrid.getTable("formListOptions_"+activelang).invalidate();
                
                scopeAdminFormFields.showSaveHint();
                syncRowsInTableProgress = false;
            }
            
            scriptScope.syncOptionTableRowsDelete = function(msg){
                // only sync once!
                // since this function also calls the onNew-event we need to ignore those
                if (syncRowsInTableProgress || msg.type != "deleted") 
                    return;
                
                var activelang = dijit.byId("formListOptions").selectedChildWidget.title;
                
                syncRowsInTableProgress = true;
                //console.debug("sync rows delete");
                
                dojo.forEach(scopeAdminFormFields.profileData.languages, function(lang){
                    if (activelang != lang) {
                        var toDelete = [];
                        //console.debug("check: " + item.id + " with " + deletedItem.id + " => " + (item.id[0] == deletedItem.id[0]));
                        dojo.forEach(msg.items, function(deleteMe) {
                            var pos = 0;
                            var data = UtilGrid.getTableData("formListOptions_" + lang);
                            dojo.some(data, function(item) {
                                //console.debug("check: " + item.id + " with " + deleteMe.id);
                                if (item.id == deleteMe.id) {
                                    toDelete.push(pos);
                                    return true;
                                }
                                pos++;
                            });
                        });
                        // delete from lang-table now
                        UtilGrid.removeTableDataRow("formListOptions_" + lang, toDelete);
                    }
                });
                
                scopeAdminFormFields.showSaveHint();
                syncRowsInTableProgress = false;
            }
            
            scriptScope.checkConsistency = function() {
                var allIds = [];
                var consistent = true;
                
                dojo.forEach(scopeAdminFormFields.profileData.languages, function(lang){
                    var data = UtilGrid.getTableData("formListOptions_" + lang);
                    // collect all unique ids from all languages
                    dojo.forEach(data, function(item) { if (dojo.indexOf(allIds, item.id) == -1) allIds.push(item.id);});
                });
                // check if every language supports all ids
                dojo.forEach(scopeAdminFormFields.profileData.languages, function(lang){
                    var data = UtilGrid.getTableData("formListOptions_" + lang);
                    // collect all unique ids from all languages
                    var ids = dojo.map(data, function(item) { return item.id; });
                    dojo.forEach(allIds, function(id) { 
                        if (dojo.indexOf(ids, id) == -1) {
                            data.push({id: id, lang: lang, value: "?"});
                            consistent = false;
                        }
                    });
                    UtilGrid.getTable("formListOptions_" + lang).invalidate();
                });
                if (!consistent) {
                    scopeAdminFormFields.showSaveHint();
                    dialog.show("<fmt:message key='dialog.admin.catalog.management.additionalFields.hint.inconsistency' />", "<fmt:message key='dialog.admin.catalog.management.additionalFields.hint.inconsistency.text' />", dialog.INFO);
                }
            }
            
            function getNewId(items) {
                var maxId = 0;
                dojo.forEach(items, function(item) {
                    if (item.id > maxId) {
                        maxId = parseInt(item.id);
                    }
                });
                return parseInt(maxId)+1;
            }
            
            scriptScope.getListData = function(options){
                var prepOptions = [];
                dojo.forEach(options, function(option){
                    prepOptions.push({
                        option: option
                    });
                });
                return prepOptions;
            }
            
            scriptScope.writeColumn = function(column, countColumn){
                // use same control creation of parent page
                scopeAdminFormFields.writeControl(column, "formColumnsContent", dojo.partial(scriptScope.editColumn, column), dojo.partial(scriptScope.deleteColumn, column.id), countColumn);
            }
            
            scriptScope.setAndShowField = function(formIdToShow, fieldId, data){
                if (typeof data == "object" && data != null) {
                    // write localized values
                    dojo.forEach(scopeAdminFormFields.profileData.languages, function(lang){
                        if (data[lang]) 
                            dijit.byId(fieldId + "_" + lang).set('value', data[lang]);
                    });
                } else 
                    dijit.byId(fieldId).set('value', data);
                    
                dojo.removeClass(formIdToShow, "hide");
            }
            
            scriptScope.save = function(){
                // instead of creating a new object use the old one and modify 
                // some properties
                // -> so we don't lose a property when data is updated
                //var data = {};
                var exitDialog = true;
                var message;
                var data = scriptScope.customParams.item;
                data.label = {};
                data.helpMessage = {};
                data.options = {};
                data.unit = {};
                
                //data.type = dijit.byId("formType").get('value');
                data.id = dijit.byId("formId").get('value');
                data.isMandatory = dijit.byId("formMandatory").get("checked");
                data.isVisible = dijit.byId("formVisibility").get("value").visible;
                // get localized values
                dojo.forEach(scopeAdminFormFields.profileData.languages, function(lang){
                    if (data.type == "selectControl") {
                        data.allowFreeEntries = dijit.byId("formAsCombobox").get("checked");
                        data.options[lang] = UtilGrid.getTableData("formListOptions_" + lang);
                    } else if(data.type == "numberControl") {
                        data.unit[lang] = dijit.byId("formUnits_" + lang).get("value");
                    }
                    
                    data.label[lang] = dijit.byId("formTitle_" + lang).get('value');
                    data.helpMessage[lang] = dijit.byId("formHelp_" + lang).get('value');
                });
                
                // title and help must be available in default/first language
                var defaultLang = scopeAdminFormFields.profileData.languages[0];
                if (!data.isLegacy && (data.label[defaultLang] == "" || data.helpMessage[defaultLang] == "")) {
                    message = "<fmt:message key='dialog.admin.catalog.management.additionalFields.error.labelOrHelp' />";
                    exitDialog = false;
                }
                
                data.scriptedProperties = dijit.byId("formJsScript").get('value');
                if (dijit.byId("formIsoScript"))
                    data.scriptedCswMapping = dijit.byId("formIsoScript").get('value');
                data.indexName = dijit.byId("formIndex").get('value');
                data.width = dijit.byId("formWidth").get('value');
                data.widthUnit = "%";
                data.numTableRows = dijit.byId("formNumTableRows").get('value');
                data.numLines = dijit.byId("formNumTextRows").get("value");
                
                // check if id is unique
                // for this only check all created div containers in admin area
                // who have the id
                // -> skip this if we edit a control and didn't change its id
                
                // check for tables that at least one column is present
                if (data.type == "tableControl" && data.columns.length == 0) {
                    message = "<fmt:message key='dialog.admin.catalog.management.additionalFields.error.columns' />";
                    exitDialog = false;
                }
                
                if (!scriptScope.info.edit || (scriptScope.info.edit && scriptScope.info.oldId != data.id)) {
                    if (dojo.query("#catalogueFormFields #admin_" + data.id).length > 0) {
                        message = "<fmt:message key='dialog.admin.catalog.management.additionalFields.error.id' />";
                        exitDialog = false;
                    }
                }
                
                if (!dijit.byId("formWidth").validate()) {
                    message = "<fmt:message key='dialog.admin.catalog.management.additionalFields.error.validate' />";
                    exitDialog = false;
                }
                
                if (exitDialog) {
                    dijit.byId("pageDialog").customParams.resultHandler.callback(data);
                    dijit.byId("pageDialog").hide();
                } else {
                    dialog.show("<fmt:message key='dialog.admin.catalog.management.additionalFields.error.save' />", message, dialog.WARNING);
                }
            }
            
            scriptScope.editColumn = function(column){
                var def = new dojo.Deferred();
                dialog.showPage("<fmt:message key='dialog.admin.additionalfields.title.editColumn' />", "admin/dialogs/mdek_admin_catman_editField_editColumn_dialog.jsp", 450, 400, true, {
                    resultHandler: def,
                    column: column
                });
                
                def.addCallback(function(data){
                    console.debug("change column: " + column.id);
                    var columns = dijit.byId("pageDialog").customParams.item.columns;
                    var pos = 0;
                    dojo.forEach(columns, function(oldColumn){
                        // replace column
                        if (oldColumn.id == column.id) {
                            dijit.byId("pageDialog").customParams.item.columns[pos] = data;
                            oldColumn = data;
                        }
                        dijit.byId("admin_" + oldColumn.id).destroy();
                        pos++;
                        scriptScope.writeColumn(oldColumn, pos);
                    });
                    scriptScope.updateAvailableTableWidth();
                });
            }
            
            scriptScope.deleteColumn = function(id){
                console.debug("delete column: " + id);
                
                var deleteAColumn = function(){
                    var columns = dijit.byId("pageDialog").customParams.item.columns;
                    var pos = 0;
                    dojo.some(columns, function(column){
                        if (column.id == id) {
                            columns.splice(pos, 1);
                            return true;
                        }
                        pos++;
                    });
                    dijit.byId("admin_" + id).destroy();
                    
                    scopeAdminFormFields.showSaveHint();
                    scriptScope.updateAvailableTableWidth();
                };
                                
                UtilGeneral.askUserAndInvokeOrCancel("<fmt:message key='dialog.admin.catalog.management.additionalFields.info.delete' />", deleteAColumn);
            }
            
            scriptScope.addColumn = function(){
                var def = new dojo.Deferred();
                dialog.showPage("<fmt:message key='dialog.admin.additionalfields.title.addColumn' />", "admin/dialogs/mdek_admin_catman_editField_editColumn_dialog.jsp", 450, 400, true, {
                    resultHandler: def
                });
                
                def.addCallback(function(data){
                    console.debug("create new column");
                    dijit.byId("pageDialog").customParams.item.columns.push(data);
                    scriptScope.writeColumn(data);
                    scriptScope.updateAvailableTableWidth();
                });
            }

            scriptScope.checkSyntax = function() {
                var tabId = dijit.byId("formScript").selectedChildWidget.id;
                var input = dijit.byId(tabId).get("value");
                var globalVariables = (tabId == "formJsScript") ? globalJSVariables : globalIDFVariables;
                var result = JSLINT.jslint(input, globalVariables);
                var errors = JSLINT.jslint.report(true);
                if (errors == "") {
                    dojo.byId("jsLintOutput").innerHTML = "OK";
                } else {
                    dojo.byId("jsLintOutput").innerHTML = errors;
                }
                dojo.style("jsLintOutput", "display", "block");
                dojo.style("jsLintHideLink", "display", "block"); 
            }
            
            scriptScope.hideErrorContainer = function() {
                dojo.style("jsLintOutput", "display", "none");
                dojo.style("jsLintHideLink", "display", "none");
            }
            
            scriptScope.updateAvailableTableWidth = function() {
                var infoMessage = '<fmt:message key="dialog.admin.additionalfields.availWidth" />';
                var scrollWidth =  scrollbarDimensions ? scrollbarDimensions.width : 17;
                var fieldWidth = dijit.byId("formWidth").get("value");
                var fieldWidthFactor = fieldWidth/100;
                
                var allColumns = 0;
                dojo.forEach(dijit.byId("pageDialog").customParams.item.columns, function(col) {
                    allColumns += eval(col.width);
                });
                
                dojo.byId("availWidth").innerHTML = dojo.string.substitute(infoMessage, [dojo.number.round(708*fieldWidthFactor - scrollWidth -allColumns)]);
            }
            
            scriptScope.closeThisDialog = function(){
                dijit.byId("pageDialog").hide();
            }
            
        </script>
    </head>
    <body>
            <div dojoType="dijit.layout.BorderContainer" gutters="false" style="height:550px;">
                <div dojoType="dijit.layout.ContentPane" region="top" class="content">
                    <div id="winNavi" style="top:0;">
                        <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-5#overall-catalog-management-5', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
                    </div>
                </div>
                <div dojoType="dijit.layout.ContentPane" region="center" class="content grey">
                    <!-- CONTENT START -->
                    <span id="span_formType" class="outer required hide">
                        <div>
                            <span class="label">
                                <label for="formType" onclick="javascript:dialog.showContextHelp(arguments[0], 10100)">
                                    <fmt:message key="dialog.admin.additionalfields.type" />
                                </label>
                            </span>
                            <span class="input">
                                <div jsId="typeStore" dojoType="dojo.data.ItemFileReadStore" url="js/data/additionalFormTypes.json">
                                </div>
                                <input dojoType="dijit.form.Select" store="typeStore" id="formType" disabled="disabled" style="width:100%;float:left;"/>
                            </span>
                        </div>
                    </span>
                    <span id="span_formId" class="outer required hide">
                        <div>
                            <span class="label">
                                <label for="formId" onclick="javascript:dialog.showContextHelp(arguments[0], 10101)">
                                    <fmt:message key="dialog.admin.additionalfields.id" />*
                                </label>
                            </span>
                            <span class="input">
                                <input dojoType="dijit.form.TextBox" id="formId" class="fullWidth" required="true"/>
                            </span>
                        </div>
                    </span>
                    <span id="span_formMandatory" class="outer halfWidth hide">
                        <div>
                            <span class="label">
                                <label for="" onclick="javascript:dialog.showContextHelp(arguments[0], 10102)">
                                    <fmt:message key="dialog.admin.additionalfields.mandatory" />
                                </label>
                            </span>
                            <span class="input">
                                <input id="formMandatory" dojoType="dijit.form.CheckBox" value="required">
                                <label for="formMandatory">
                                    <fmt:message key="dialog.admin.additionalfields.mandatory.check" />
                                </label>
                            </span>
                        </div>
                    </span>
                    <span id="span_formVisibility" class="outer halfWidth hide">
                        <div>
                            <span class="label">
                                <label for="" onclick="javascript:dialog.showContextHelp(arguments[0], 10103)">
                                    <fmt:message key="dialog.admin.additionalfields.visibility" />
                                </label>
                            </span>
                            <span class="input">
                                <form dojoType="dijit.form.Form" id="formVisibility">
                                    <input dojoType="dijit.form.RadioButton" type="radio" name="visible" id="visibleOptional" checked value="optional" />
                                    <label for="visibleOptional">
                                        <fmt:message key="dialog.admin.additionalfields.visibility.optional" />
                                    </label>
                                    <input dojoType="dijit.form.RadioButton" type="radio" name="visible" id="visibleShow" value="show" />
                                    <label for="visibleShow">
                                        <fmt:message key="dialog.admin.additionalfields.visibility.show" />
                                    </label>
                                    <input dojoType="dijit.form.RadioButton" type="radio" name="visible" id="visibleHide" value="hide" />
                                    <label for="visibleHide">
                                        <fmt:message key="dialog.admin.additionalfields.visibility.hide" />
                                    </label>
                                </form>
                            </span>
                        </div>
                    </span>
                    <span id="span_formTitle" class="outer required hide">
                        <div>
                            <span class="label">
                                <label for="formTitle" onclick="javascript:dialog.showContextHelp(arguments[0], 10104)">
                                    <fmt:message key="dialog.admin.additionalfields.title" />*
                                </label>
                            </span>
                            <span class="input">
                                <div dojoType="dijit.layout.TabContainer" doLayout="false" controllerWidget="dijit.layout.TabController" id="formTitle" style="width:100%;" selected="en">
                                </div>
                            </span>
                        </div>
                    </span>
                    <span id="span_formHelp" class="outer required hide">
                        <div>
                            <span class="label">
                                <label for="formHelp" onclick="javascript:dialog.showContextHelp(arguments[0], 10105)">
                                    <fmt:message key="dialog.admin.additionalfields.help" />*
                                </label>
                            </span>
                            <span class="input">
                                <div dojoType="dijit.layout.TabContainer" doLayout="false" controllerWidget="dijit.layout.TabController" id="formHelp" style="width:100%;" selected="en">
                                </div>
                            </span>
                        </div>
                    </span>
                    <span id="span_formScript" class="outer hide">
                        <div>
                            <span class="label">
                                <label for="formJsScript" onclick="javascript:dialog.showContextHelp(arguments[0], 10106)">
                                    <fmt:message key="dialog.admin.additionalfields.script" />
                                </label>
                            </span>
                            <span class="input">
                                <div dojoType="dijit.layout.TabContainer" doLayout="false" controllerWidget="dijit.layout.TabController" id="formScript" style="width:100%;" selected="formJsScript">
                                    <input dojoType="dijit.form.SimpleTextarea" id="formJsScript" class="innerPadding" style="width: 100%;" rows="6" title="<fmt:message key="dialog.admin.additionalfields.JS" />"/>
                                    <input dojoType="dijit.form.SimpleTextarea" id="formIsoScript" class="innerPadding fullWidth" rows="6" title="<fmt:message key="dialog.admin.additionalfields.CSW" />"/>
                                </div>
                                <button dojoType="dijit.form.Button" style="float:right;" type="button" label="<fmt:message key="dialog.admin.additionalfields.btnCheckSyntax" />" >
                                    <script type="dojo/method" event="onClick" args="evt">
                                        scriptScope.checkSyntax();
                                    </script>
                                </button>
                                <p id="jsLintHideLink" style="float: left; display: none; padding-top: 4px;"><a href="#" onClick="javascript:scriptScope.hideErrorContainer();"><fmt:message key="dialog.admin.additionalfields.hideErrorContainer" /></a></p>
                                <div id="jsLintOutput" style="clear:both; max-height: 200px; padding: 5px; overflow: auto; border: 1px dashed gray; display: none;"></div>
                            </span>
                        </div>
                    </span>
                    <span id="span_formListOptions" class="outer halfWidth hide">
                        <div>
                            <span class="label">
                                <label for="formListOptions" onclick="javascript:dialog.showContextHelp(arguments[0], 10107)">
                                    <fmt:message key="dialog.admin.additionalfields.options" />
                                </label>
                            </span>
                            <div class="tableContainer">
                                <div id="formListOptions" dojoType="dijit.layout.TabContainer" controllerWidget="dijit.layout.TabController" doLayout="false" style="">
                                </div>
                            </div>
                            <span class="input">
                                <input id="formAsCombobox" dojoType="dijit.form.CheckBox" value="required">
                                <label onclick="javascript:dialog.showContextHelp(arguments[0], 10114)">
                                    <fmt:message key="dialog.admin.additionalfields.allowFreeEntries" />
                                </label>
                            </span>
                        </div>
                    </span>
                    <span id="span_formUnits" class="outer halfWidth hide">
                        <div>
                            <span class="label">
                                <label for="formUnits" onclick="javascript:dialog.showContextHelp(arguments[0], 10108)">
                                    <fmt:message key="dialog.admin.additionalfields.unit" />
                                </label>
                            </span>
                            <div class="tableContainer headHiddenRows4">
                                <div id="formUnits" dojoType="dijit.layout.TabContainer" controllerWidget="dijit.layout.TabController" doLayout="false" style="">
                                </div>
                            </div>
                        </div>
                    </span>
                    <span id="span_formIndex" class="outer halfWidth hide">
                        <div style="padding-bottom: 10px;">
                            <span class="label">
                                <label for="formIndex" onclick="javascript:dialog.showContextHelp(arguments[0], 10109)">
                                    <fmt:message key="dialog.admin.additionalfields.index" />
                                </label>
                            </span>
                            <span class="input">
                                <input dojoType="dijit.form.TextBox" id="formIndex" style="width: 100%;"/>
                            </span>
                        </div>
                    </span>
                    <span id="span_formWidth" class="outer halfWidth hide">
                        <div>
                            <span class="label">
                                <label for="formWidth" onclick="javascript:dialog.showContextHelp(arguments[0], 10110)">
                                    <fmt:message key="dialog.admin.additionalfields.width" />
                                </label>
                            </span>
                            <span class="input">
                                <table style="width:100%; margin: -2px 0 0 0;"><tr><td>
                                    <input dojoType="dijit.form.NumberTextBox" constraints="{min:0,max:100}" id="formWidth" style="width: 100%;"/>
                                </td><td style='width:1px; padding-left:5px;'>%</td></tr>
                                </table>
                            </span>
                        </div>
                    </span>
                    <span id="span_formNumTextRows" class="outer halfWidth hide">
                        <div>
                            <span class="label">
                                <label for="formNumTextRows" onclick="javascript:dialog.showContextHelp(arguments[0], 10111)">
                                    <fmt:message key="dialog.admin.additionalfields.numTextRows" />
                                </label>
                            </span>
                            <span class="input">
                                <input dojoType="dijit.form.NumberTextBox" id="formNumTextRows" contraints="{min:1}" style="width: 100%;"/>
                            </span>
                        </div>
                    </span>
                    <span id="span_formNumTableRows" class="outer halfWidth hide">
                        <div>
                            <span class="label">
                                <label for="formNumTableRows" onclick="javascript:dialog.showContextHelp(arguments[0], 10112)">
                                    <fmt:message key="dialog.admin.additionalfields.numTableRows" />
                                </label>
                            </span>
                            <span class="input">
                                <input dojoType="dijit.form.NumberTextBox" id="formNumTableRows" constraints="{min:1}" style="width: 100%;"/>
                            </span>
                        </div>
                    </span>
                    <span id="span_formColumns" class="outer required hide">
                        <div>
                            <span class="label">
                                <label for="formColumns" onclick="javascript:dialog.showContextHelp(arguments[0], 10113)">
                                    <fmt:message key="dialog.admin.additionalfields.columns" />*
                                </label>
                                <span id="availWidth" style="font-weight:normal; float:right;"></span>
                            </span>
                            <span id="formColumnsContent" class="input innerPadding" style="border: 1px solid gray;">
                            </span>
                            <button dojoType="dijit.form.Button" style="float:right;">
                                <fmt:message key="dialog.admin.additionalfields.btnAddColumn" />
                                <script type="dojo/method" event="onClick" args="evt">
                                    scriptScope.addColumn();
                                </script>
                            </button>
                        </div>
                    </span>
                    <div class="fill">
                    </div>
                    <!-- CONTENT END -->
                </div>
                <div dojoType="dijit.layout.ContentPane" region="bottom" class="content" style="height:49px; background-color:#EDEDED;">
                    <hr/>
                    <button dojoType="dijit.form.Button" style="float:left;">
                        <fmt:message key="dialog.admin.additionalfields.btnClose" />
                        <script type="dojo/method" event="onClick" args="evt">
                            scriptScope.closeThisDialog();
                        </script>
                    </button>
                    <button id="saveAddField" dojoType="dijit.form.Button" style="float:right;">
                        <fmt:message key="dialog.admin.additionalfields.btnAdd" />
                        <script type="dojo/method" event="onClick" args="evt">
                            scriptScope.save();
                        </script>
                    </button>
                </div>
            </div>
    </body>
</html>
