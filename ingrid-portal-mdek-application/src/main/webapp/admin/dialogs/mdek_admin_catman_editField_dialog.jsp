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
        var pageAdditionalFieldsDlg = _container_;
        require(["dojo/on",
            "dojo/aspect",
            "dojo/dom",
            "dojo/query",
            "dojo/string",
            "dojo/_base/array",
            "dojo/_base/lang",
            "dojo/dom-class",
            "dojo/dom-style",
            "dojo/Deferred",
            "dijit/registry",
            "dijit/form/TextBox",
            "dijit/layout/ContentPane",
            "ingrid/utils/Grid",
            "ingrid/utils/General",
            "ingrid/layoutCreator",
            "ingrid/dialog"
        ], function(on, aspect, dom, query, string, array, lang, domClass, style, Deferred, registry, TextBox, ContentPane, UtilGrid, UtilGeneral, layoutCreator, dialog) {

            var syncRowsInTableProgress = false;

            var info = {};

            var customParams = pageAdditionalFieldsDlg.customParams;

            var self = pageAdditionalFieldsDlg;

            var lastSelectedTab;

            // onload function below all functions since IE does not understand this event
            // for a container
            on(_container_, "Load", init);

            function init() {
                info.edit = customParams.edit;
                createDynamicElements(customParams.item);
                fillData(customParams.item, customParams.type);
                self.resize();
            }

            function createDynamicElements(data) {
                array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                    // create localized labels
                    //console.debug("create: " + "formTitle_" + lang);
                    var titleWidget = new TextBox({
                        id: "formTitle_" + lang,
                        title: lang,
                        "class": "fullWidth"
                    });
                    registry.byId("formTitle").addChild(titleWidget);

                    // create help messages labels
                    var helpWidget = new TextBox({
                        id: "formHelp_" + lang,
                        title: lang,
                        "class": "fullWidth"
                    });
                    registry.byId("formHelp").addChild(helpWidget);

                    // create number units
                    var unitWidget = new TextBox({
                        id: "formUnits_" + lang,
                        title: lang,
                        "class": "fullWidth"
                    });
                    registry.byId("formUnits").addChild(unitWidget);

                    // create link label
                    var linkLabel = new TextBox({
                        id: "formThesaurusLinkLabel_" + lang,
                        title: lang,
                        "class": "fullWidth"
                    });
                    registry.byId("formThesaurusLinkLabel").addChild(linkLabel);
                });

                on(registry.byId("formMandatory"), "Change", mandatoryChanged);
            }

            function mandatoryChanged(checked) {
                var visForm = registry.byId("formVisibility");
                // to see the disabled/enabled state of radio box we need
                // to visit it first!
                if (checked) {
                    visForm.set("value", {
                        visible: "optional"
                    });
                    registry.byId("visibleOptional").disabled = true;
                    visForm.set("value", {
                        visible: "hide"
                    });
                    registry.byId("visibleHide").disabled = true;
                    visForm.set("value", {
                        visible: "show"
                    });
                } else {
                    registry.byId("visibleOptional").disabled = false;
                    registry.byId("visibleHide").disabled = false;
                    var oldValue = visForm.get("value");
                    visForm.set("value", {
                        visible: "optional"
                    });
                    visForm.set("value", {
                        visible: "hidden"
                    });
                    visForm.set("value", oldValue);
                }
            }

            function fillData(profileObjectToEdit, type) {
                // remember id in case it is going to be changed
                info.oldId = profileObjectToEdit.id;
                //registry.byId("formType").set("value", profileObjectToEdit.type);
                setAndShowField("span_formId", "formId", profileObjectToEdit.id);
                if (profileObjectToEdit.isLegacy) {
                    // remove CSW Script Field for Legacy elements
                    registry.byId("formScript").removeChild(registry.byId("formIsoScript"));
                    registry.byId("formIsoScript").destroy();
                }

                // fill form elements with available data
                // according to the available properties the type of this control is defined (layout)
                //console.debug("type: " + type);
                switch (type) {
                    case "tableControl":
                        var countColumn = 0;
                        // var lastColumn = null;
                        array.forEach(profileObjectToEdit.columns, function(column) {
                            countColumn++;
                            writeColumn(column, countColumn);
                        });
                        domClass.remove("span_formColumns", "hide");
                        on(registry.byId("formWidth"), "onChange", updateAvailableTableWidth);
                        setTimeout(updateAvailableTableWidth, 1000);
                        // fall through!
                    case "thesaurusControl":
                        setAndShowField("span_formNumTableRows", "formNumTableRows", profileObjectToEdit.numTableRows);
                        if (type == "thesaurusControl") {
                            //domClass.remove("span_formThesaurusUrl", "hide");
                            setAndShowField("span_formThesaurusUrl", "formThesaurusUrl", profileObjectToEdit.thesaurusUrl);
                            setAndShowField("span_formThesaurusLinkLabel", "formThesaurusLinkLabel", profileObjectToEdit.linkLabel);
                        }
                    case "selectControl":
                        if (type == "selectControl") {
                            prepareSelectOptions(profileObjectToEdit);
                            domClass.remove("span_formListOptions", "hide");
                            // remember last selected tab delayed, needed if text was entered 
                            // without hitting enter but switching tab
                            lastSelectedTab = pageAdditionalFields.profileData.languages[0];
                            on(registry.byId("formListOptions"), "MouseDown", lang.hitch(this, function() {
                                registry.byId("formListOptions_" + lastSelectedTab).editorLock.commitCurrentEdit();
                            }));
                            registry.byId("formListOptions").watch("selectedChildWidget", lang.hitch(this, function() {
                                setTimeout(function() {
                                    lastSelectedTab = registry.byId("formListOptions").selectedChildWidget.title;
                                }, 100);
                                registry.byId("formListOptions_de").reinitLastColumn();
                            }));
                            setTimeout(function() {
                                registry.byId("formListOptions_en").reinitLastColumn();
                            }, 100);
                        }
                        // fall through!
                    case "numberControl":
                        if (type == "numberControl")
                            setAndShowField("span_formUnits", "formUnits", profileObjectToEdit.unit);
                        // fall through!
                    case "checkboxControl":
                    case "dateControl":
                    case "textControl":
                        setAndShowField("span_formTitle", "formTitle", profileObjectToEdit.label);
                        setAndShowField("span_formHelp", "formHelp", profileObjectToEdit.helpMessage);
                        registry.byId("formIsoScript").set("value", profileObjectToEdit.scriptedCswMapping);
                        setAndShowField("span_formIndex", "formIndex", profileObjectToEdit.indexName);
                        setAndShowField("span_formWidth", "formWidth", profileObjectToEdit.width);
                        setAndShowField("span_formIndex", "formIndex", profileObjectToEdit.indexName);
                        if (type == "textControl")
                            setAndShowField("span_formNumTextRows", "formNumTextRows", profileObjectToEdit.numLines);
                        // fall through!
                    case "legacyControl":
                        registry.byId("formMandatory").set("checked", profileObjectToEdit.isMandatory);
                        domClass.remove("span_formMandatory", "hide");
                        registry.byId("formVisibility").set("value", {
                            visible: profileObjectToEdit.isVisible
                        });
                        domClass.remove("span_formVisibility", "hide");
                        setAndShowField("span_formScript", "formJsScript", profileObjectToEdit.scriptedProperties);
                        break;
                    case "rubric":
                        setAndShowField("span_formTitle", "formTitle", profileObjectToEdit.label);
                        setAndShowField("span_formHelp", "formHelp", profileObjectToEdit.helpMessage);
                        break;
                }

                // do not allow to change the id, once it was set during creation
                //console.debug(info.edit);
                if (info.edit) {
                    registry.byId("formId").set("disabled", true);
                    registry.byId("saveAddField").set("label", "<fmt:message key='dialog.admin.additionalfields.btnUpdate' />");
                }


            }

            function prepareSelectOptions(profileObjectToEdit) {
                if (profileObjectToEdit.allowFreeEntries) {
                    registry.byId("formAsCombobox").set("checked", profileObjectToEdit.allowFreeEntries);
                }
                // create table with data
                var formListOptionsStructure = [{
                    field: "id",
                    name: "id",
                    width: "30px",
                    editable: true
                }, {
                    field: "value",
                    name: "option",
                    width: "200px",
                    editable: true
                }];
                //for (lang in profileObjectToEdit.options) {
                array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                    registry.byId("formListOptions").addChild(new ContentPane({
                        id: "formListOptions_" + lang + "Container",
                        title: lang,
                        "class": "innerPadding"
                    }));
                    var divElement = document.createElement("div");
                    divElement.setAttribute("id", "formListOptions_" + lang);
                    dom.byId("formListOptions_" + lang + "Container").appendChild(divElement);
                    var props = {
                        interactive: "true",
                        moveRows: "true"
                    };
                    layoutCreator.createDataGrid("formListOptions_" + lang, null, formListOptionsStructure, null, props);

                    if (profileObjectToEdit.options[lang])
                        UtilGrid.setTableData("formListOptions_" + lang, profileObjectToEdit.options[lang]);

                    // add special action to events on store for synchronization
                    aspect.after(UtilGrid.getTable("formListOptions_" + lang), "onAddNewRow", syncOptionTableRowsNew);
                    aspect.after(UtilGrid.getTable("formListOptions_" + lang), "onDataChanged", syncOptionTableRowsDelete);
                    aspect.after(UtilGrid.getTable("formListOptions_" + lang), "onCellChange", syncOptionTableRowsCellChange);

                });
                checkConsistency();
            }

            // sync id among all tables if it has changed
            function syncOptionTableRowsCellChange(result, params) {
                var msg = params[0];
                // only update if id-column was changed!
                if (msg.cell === 1) {
                    //console.log("Cell changed: ", msg);
                    var newId = msg.item.id;
                    var oldId = msg.oldItem.id;
                    var activelang = msg.item.lang;

                    array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                        if (activelang != lang) {
                            var data = UtilGrid.getTableData("formListOptions_" + lang);
                            array.some(data, function(row) {
                                if (row.id === oldId) {
                                    row.id = newId;
                                    return true;
                                }
                            });
                            UtilGrid.getTable("formListOptions_" + lang).invalidate();
                        }
                    });

                }
            }

            function syncOptionTableRowsNew(result, params) {
                var msg = params[0];
                if (syncRowsInTableProgress)
                    return;

                syncRowsInTableProgress = true;
                //console.debug("sync rows");
                // get active tab for language of new item
                var activelang = lastSelectedTab; //registry.byId("formListOptions").selectedChildWidget.title;
                var newId = getNewId(UtilGrid.getTableData("formListOptions_" + activelang));
                msg.item.id = newId;
                msg.item.lang = activelang;

                array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                    if (activelang != lang) {
                        var otherLangItem = {
                            id: newId,
                            lang: lang,
                            value: "?"
                        };
                        UtilGrid.addTableDataRow("formListOptions_" + lang, otherLangItem);
                    }
                });

                UtilGrid.getTable("formListOptions_" + activelang).invalidate();

                pageAdditionalFields.showSaveHint();
                syncRowsInTableProgress = false;
            }

            function syncOptionTableRowsDelete(result, params) {
                var msg = params[0];
                // only sync once!
                // since this function also calls the onNew-event we need to ignore those
                if (syncRowsInTableProgress || msg.type != "deleted")
                    return;

                var activelang = registry.byId("formListOptions").selectedChildWidget.title;

                syncRowsInTableProgress = true;
                //console.debug("sync rows delete");

                array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                    if (activelang != lang) {
                        var toDelete = [];
                        //console.debug("check: " + item.id + " with " + deletedItem.id + " => " + (item.id[0] == deletedItem.id[0]));
                        array.forEach(msg.items, function(deleteMe) {
                            var pos = 0;
                            var data = UtilGrid.getTableData("formListOptions_" + lang);
                            array.some(data, function(item) {
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

                pageAdditionalFields.showSaveHint();
                syncRowsInTableProgress = false;
            }

            function checkConsistency() {
                var allIds = [];
                var consistent = true;

                array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                    var data = UtilGrid.getTableData("formListOptions_" + lang);
                    // collect all unique ids from all languages
                    array.forEach(data, function(item) {
                        if (array.indexOf(allIds, item.id) == -1) allIds.push(item.id);
                    });
                });
                // check if every language supports all ids
                array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                    var data = UtilGrid.getTableData("formListOptions_" + lang);
                    // collect all unique ids from all languages
                    var ids = dojo.map(data, function(item) {
                        return item.id;
                    });
                    array.forEach(allIds, function(id) {
                        if (array.indexOf(ids, id) == -1) {
                            data.push({
                                id: id,
                                lang: lang,
                                value: "?"
                            });
                            consistent = false;
                        }
                    });
                    UtilGrid.getTable("formListOptions_" + lang).invalidate();
                });
                if (!consistent) {
                    pageAdditionalFields.showSaveHint();
                    dialog.show("<fmt:message key='dialog.admin.catalog.management.additionalFields.hint.inconsistency' />", "<fmt:message key='dialog.admin.catalog.management.additionalFields.hint.inconsistency.text' />", dialog.INFO);
                }
            }

            function getNewId(items) {
                var maxId = 0;
                array.forEach(items, function(item) {
                    if (item.id > maxId) {
                        maxId = parseInt(item.id);
                    }
                });
                return parseInt(maxId) + 1;
            }

            function getListData(options) {
                var prepOptions = [];
                array.forEach(options, function(option) {
                    prepOptions.push({
                        option: option
                    });
                });
                return prepOptions;
            }

            function writeColumn(column, countColumn) {
                // use same control creation of parent page
                pageAdditionalFields.writeControl(column, "formColumnsContent", dojo.partial(editColumn, column), dojo.partial(deleteColumn, column.id), countColumn);
            }

            function setAndShowField(formIdToShow, fieldId, data) {
                if (typeof data == "object" && data != null) {
                    // write localized values
                    array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                        if (data[lang])
                            registry.byId(fieldId + "_" + lang).set("value", data[lang]);
                    });
                } else
                    registry.byId(fieldId).set("value", data);

                domClass.remove(formIdToShow, "hide");
            }

            function save() {
                // instead of creating a new object use the old one and modify 
                // some properties
                // -> so we don"t lose a property when data is updated
                //var data = {};
                var exitDialog = true;
                var message;
                var data = customParams.item;
                data.label = {};
                data.helpMessage = {};
                data.linkLabel = {};
                data.options = {};
                data.unit = {};

                //data.type = registry.byId("formType").get("value");
                data.id = lang.trim(registry.byId("formId").get("value"));
                data.isMandatory = registry.byId("formMandatory").get("checked");
                data.isVisible = registry.byId("formVisibility").get("value").visible;
                // get localized values
                array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                    if (data.type == "selectControl") {
                        data.allowFreeEntries = registry.byId("formAsCombobox").get("checked");
                        data.options[lang] = UtilGrid.getTableData("formListOptions_" + lang);
                    } else if (data.type == "numberControl") {
                        data.unit[lang] = registry.byId("formUnits_" + lang).get("value");
                    }

                    data.label[lang] = registry.byId("formTitle_" + lang).get("value");
                    data.helpMessage[lang] = registry.byId("formHelp_" + lang).get("value");
                    data.linkLabel[lang] = registry.byId("formThesaurusLinkLabel_" + lang).get("value");
                });

                // title and help must be available in default/first language
                var defaultLang = pageAdditionalFields.profileData.languages[0];
                if (!data.isLegacy && (data.label[defaultLang] == "" || data.helpMessage[defaultLang] == "")) {
                    message = "<fmt:message key='dialog.admin.catalog.management.additionalFields.error.labelOrHelp' />";
                    exitDialog = false;
                }

                data.scriptedProperties = registry.byId("formJsScript").get("value");
                if (registry.byId("formIsoScript"))
                    data.scriptedCswMapping = registry.byId("formIsoScript").get("value");
                data.indexName = registry.byId("formIndex").get("value");
                data.width = registry.byId("formWidth").get("value");
                data.widthUnit = "%";
                data.numTableRows = registry.byId("formNumTableRows").get("value");
                data.numLines = registry.byId("formNumTextRows").get("value");
                data.thesaurusUrl = registry.byId("formThesaurusUrl").get("value");

                // check if id is unique
                // for this only check all created div containers in admin area
                // who have the id
                // -> skip this if we edit a control and didn't change its id

                // check for tables that at least one column is present
                if (data.type == "tableControl" && data.columns.length == 0) {
                    message = "<fmt:message key='dialog.admin.catalog.management.additionalFields.error.columns' />";
                    exitDialog = false;
                }

                if (!info.edit || (info.edit && info.oldId != data.id)) {
                    if (query("#catalogueFormFields #admin_" + data.id).length > 0) {
                        message = "<fmt:message key='dialog.admin.catalog.management.additionalFields.error.id' />";
                        exitDialog = false;
                    }
                }

                if (!registry.byId("formWidth").validate()) {
                    message = "<fmt:message key='dialog.admin.catalog.management.additionalFields.error.validate' />";
                    exitDialog = false;
                }

                if (exitDialog) {
                    customParams.resultHandler.resolve(data);
                    self.hide();
                } else {
                    dialog.show("<fmt:message key='dialog.admin.catalog.management.additionalFields.error.save' />", message, dialog.WARNING);
                }
            }

            function editColumn(column) {
                var def = new Deferred();
                dialog.showPage("<fmt:message key='dialog.admin.additionalfields.title.editColumn' />", "admin/dialogs/mdek_admin_catman_editField_editColumn_dialog.jsp", 450, 400, true, {
                    resultHandler: def,
                    column: column
                });

                def.then(function(data) {
                    console.debug("change column: " + column.id);
                    var columns = customParams.item.columns;
                    var pos = 0;
                    array.forEach(columns, function(oldColumn) {
                        // replace column
                        if (oldColumn.id == column.id) {
                            customParams.item.columns[pos] = data;
                            oldColumn = data;
                        }
                        registry.byId("admin_" + oldColumn.id).destroy();
                        pos++;
                        writeColumn(oldColumn, pos);
                    });
                    updateAvailableTableWidth();
                });
            }

            function deleteColumn(id) {
                console.debug("delete column: " + id);

                var deleteAColumn = function() {
                    var columns = customParams.item.columns;
                    var pos = 0;
                    array.some(columns, function(column) {
                        if (column.id == id) {
                            columns.splice(pos, 1);
                            return true;
                        }
                        pos++;
                    });
                    registry.byId("admin_" + id).destroy();

                    pageAdditionalFields.showSaveHint();
                    updateAvailableTableWidth();
                };

                UtilGeneral.askUserAndInvokeOrCancel("<fmt:message key='dialog.admin.catalog.management.additionalFields.info.delete' />", deleteAColumn);
            }

            function addColumn() {
                var def = new Deferred();
                dialog.showPage("<fmt:message key='dialog.admin.additionalfields.title.addColumn' />", "admin/dialogs/mdek_admin_catman_editField_editColumn_dialog.jsp", 450, 400, true, {
                    resultHandler: def
                });

                def.then(function(data) {
                    console.debug("create new column");
                    customParams.item.columns.push(data);
                    writeColumn(data);
                    updateAvailableTableWidth();
                });
            }

            function checkSyntax() {
                var tabId = registry.byId("formScript").selectedChildWidget.id;
                var input = registry.byId(tabId).get("value");
                var globalVariables = (tabId == "formJsScript") ? globalJSVariables : globalIDFVariables;
                var result = JSLINT.jslint(input, globalVariables);
                var errors = JSLINT.jslint.report(true);
                if (errors == "") {
                    dom.byId("jsLintOutput").innerHTML = "OK";
                } else {
                    dom.byId("jsLintOutput").innerHTML = errors;
                }
                style.set("jsLintOutput", "display", "block");
                style.set("jsLintHideLink", "display", "block");
            }

            function hideErrorContainer() {
                style.set("jsLintOutput", "display", "none");
                style.set("jsLintHideLink", "display", "none");
            }

            function updateAvailableTableWidth() {
                var infoMessage = "<fmt:message key='dialog.admin.additionalfields.availWidth' />";
                var scrollWidth = typeof scrollbarDimensions != "undefined" ? scrollbarDimensions.width : 17;
                var fieldWidth = registry.byId("formWidth").get("value");
                var fieldWidthFactor = fieldWidth / 100;

                var allColumns = 0;
                array.forEach(customParams.item.columns, function(col) {
                    allColumns += eval(col.width);
                });

                dom.byId("availWidth").innerHTML = string.substitute(infoMessage, [dojo.number.round(708 * fieldWidthFactor - scrollWidth - allColumns)]);
            }

            function closeThisDialog() {
                self.hide();
            }

            /**
             * PUBLIC METHODS
             */
            pageAdditionalFieldsDlg.checkSyntax = checkSyntax;
            pageAdditionalFieldsDlg.save = save;
            pageAdditionalFieldsDlg.closeThisDialog = closeThisDialog;
            pageAdditionalFieldsDlg.addColumn = addColumn;
            pageAdditionalFieldsDlg.getNewId = getNewId;
        });
    </script>
</head>
<body>
        <div data-dojo-type="dijit/layout/BorderContainer" gutters="false" style="height:550px;">
            <div data-dojo-type="dijit/layout/ContentPane" region="top" class="content">
                <div id="winNavi" style="top:0;">
                    <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-5#overall-catalog-management-5', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
                </div>
            </div>
            <div data-dojo-type="dijit/layout/ContentPane" region="center" class="content grey">
                <!-- CONTENT START -->
                <span id="span_formType" class="outer required hide">
                    <div>
                        <span class="label">
                            <label for="formType" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10100)">
                                <fmt:message key="dialog.admin.additionalfields.type" />
                            </label>
                        </span>
                        <span class="input">
                            <div jsId="typeStore" data-dojo-type="dojo.data.ItemFileReadStore" url="js/data/additionalFormTypes.json">
                            </div>
                            <input data-dojo-type="dijit/form/Select" store="typeStore" id="formType" disabled="disabled" style="width:100%;float:left;"/>
                        </span>
                    </div>
                </span>
                <span id="span_formId" class="outer required hide">
                    <div>
                        <span class="label">
                            <label for="formId" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10101)">
                                <fmt:message key="dialog.admin.additionalfields.id" />*
                            </label>
                        </span>
                        <span class="input">
                            <input data-dojo-type="dijit/form/TextBox" id="formId" class="fullWidth" required="true"/>
                        </span>
                    </div>
                </span>
                <span id="span_formMandatory" class="outer halfWidth hide">
                    <div>
                        <span class="label">
                            <label for="" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10102)">
                                <fmt:message key="dialog.admin.additionalfields.mandatory" />
                            </label>
                        </span>
                        <span class="input">
                            <input id="formMandatory" data-dojo-type="dijit/form/CheckBox" value="required">
                            <label for="formMandatory">
                                <fmt:message key="dialog.admin.additionalfields.mandatory.check" />
                            </label>
                        </span>
                    </div>
                </span>
                <span id="span_formVisibility" class="outer halfWidth hide">
                    <div>
                        <span class="label">
                            <label for="" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10103)">
                                <fmt:message key="dialog.admin.additionalfields.visibility" />
                            </label>
                        </span>
                        <span class="input">
                            <form data-dojo-type="dijit/form/Form" id="formVisibility">
                                <input data-dojo-type="dijit/form/RadioButton" type="radio" name="visible" id="visibleOptional" checked value="optional" />
                                <label for="visibleOptional">
                                    <fmt:message key="dialog.admin.additionalfields.visibility.optional" />
                                </label>
                                <input data-dojo-type="dijit/form/RadioButton" type="radio" name="visible" id="visibleShow" value="show" />
                                <label for="visibleShow">
                                    <fmt:message key="dialog.admin.additionalfields.visibility.show" />
                                </label>
                                <input data-dojo-type="dijit/form/RadioButton" type="radio" name="visible" id="visibleHide" value="alwaysHidden" />
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
                            <label for="formTitle" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10104)">
                                <fmt:message key="dialog.admin.additionalfields.title" />*
                            </label>
                        </span>
                        <span class="input">
                            <div data-dojo-type="dijit/layout/TabContainer" doLayout="false" id="formTitle" style="width:100%;" selected="en">
                            </div>
                        </span>
                    </div>
                </span>
                <span id="span_formHelp" class="outer required hide">
                    <div>
                        <span class="label">
                            <label for="formHelp" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10105)">
                                <fmt:message key="dialog.admin.additionalfields.help" />*
                            </label>
                        </span>
                        <span class="input">
                            <div data-dojo-type="dijit/layout/TabContainer" doLayout="false" id="formHelp" style="width:100%;" selected="en">
                            </div>
                        </span>
                    </div>
                </span>
                <span id="span_formScript" class="outer hide">
                    <div>
                        <span class="label">
                            <label for="formJsScript" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10106)">
                                <fmt:message key="dialog.admin.additionalfields.script" />
                            </label>
                        </span>
                        <span class="input">
                            <div data-dojo-type="dijit/layout/TabContainer" doLayout="false" id="formScript" style="width:100%;" selected="formJsScript">
                                <input data-dojo-type="dijit/form/SimpleTextarea" id="formJsScript" class="innerPadding" style="width: 100%;" rows="6" title="<fmt:message key="dialog.admin.additionalfields.JS" />"/>
                                <input data-dojo-type="dijit/form/SimpleTextarea" id="formIsoScript" class="innerPadding fullWidth" rows="6" title="<fmt:message key="dialog.admin.additionalfields.CSW" />"/>
                            </div>
                            <button data-dojo-type="dijit/form/Button" style="float:right;" type="button" onclick="pageAdditionalFieldsDlg.checkSyntax()" label="<fmt:message key="dialog.admin.additionalfields.btnCheckSyntax" />" >
                            </button>
                            <p id="jsLintHideLink" style="float: left; display: none; padding-top: 4px;"><a href="#" onclick="javascript:hideErrorContainer();"><fmt:message key="dialog.admin.additionalfields.hideErrorContainer" /></a></p>
                            <div id="jsLintOutput" style="clear:both; max-height: 200px; padding: 5px; overflow: auto; border: 1px dashed gray; display: none;"></div>
                        </span>
                    </div>
                </span>
                <span id="span_formListOptions" class="outer halfWidth hide">
                    <div>
                        <span class="label">
                            <label for="formListOptions" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10107)">
                                <fmt:message key="dialog.admin.additionalfields.options" />
                            </label>
                        </span>
                        <div class="tableContainer">
                            <div id="formListOptions" data-dojo-type="dijit/layout/TabContainer" doLayout="false" style="">
                            </div>
                        </div>
                        <span class="input">
                            <input id="formAsCombobox" data-dojo-type="dijit/form/CheckBox" value="required">
                            <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10114)">
                                <fmt:message key="dialog.admin.additionalfields.allowFreeEntries" />
                            </label>
                        </span>
                    </div>
                </span>
                <span id="span_formUnits" class="outer halfWidth hide">
                    <div>
                        <span class="label">
                            <label for="formUnits" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10108)">
                                <fmt:message key="dialog.admin.additionalfields.unit" />
                            </label>
                        </span>
                        <div class="tableContainer headHiddenRows4">
                            <div id="formUnits" data-dojo-type="dijit/layout/TabContainer" doLayout="false" style="">
                            </div>
                        </div>
                    </div>
                </span>
                <span id="span_formIndex" class="outer halfWidth hide">
                    <div style="padding-bottom: 10px;">
                        <span class="label">
                            <label for="formIndex" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10109)">
                                <fmt:message key="dialog.admin.additionalfields.index" />
                            </label>
                        </span>
                        <span class="input">
                            <input data-dojo-type="dijit/form/TextBox" id="formIndex" style="width: 100%;"/>
                        </span>
                    </div>
                </span>
                <span id="span_formWidth" class="outer halfWidth hide">
                    <div>
                        <span class="label">
                            <label for="formWidth" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10110)">
                                <fmt:message key="dialog.admin.additionalfields.width" />
                            </label>
                        </span>
                        <span class="input">
                            <table style="width:100%; margin: -2px 0 0 0;"><tr><td>
                                <input data-dojo-type="dijit/form/NumberTextBox" constraints="{min:0,max:100}" id="formWidth" style="width: 100%;"/>
                            </td><td style='width:1px; padding-left:5px;'>%</td></tr>
                            </table>
                        </span>
                    </div>
                </span>
                <span id="span_formNumTextRows" class="outer halfWidth hide">
                    <div>
                        <span class="label">
                            <label for="formNumTextRows" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10111)">
                                <fmt:message key="dialog.admin.additionalfields.numTextRows" />
                            </label>
                        </span>
                        <span class="input">
                            <input data-dojo-type="dijit/form/NumberTextBox" id="formNumTextRows" contraints="{min:1}" style="width: 100%;"/>
                        </span>
                    </div>
                </span>
                <span id="span_formNumTableRows" class="outer halfWidth hide">
                    <div>
                        <span class="label">
                            <label for="formNumTableRows" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10112)">
                                <fmt:message key="dialog.admin.additionalfields.numTableRows" />
                            </label>
                        </span>
                        <span class="input">
                            <input data-dojo-type="dijit/form/NumberTextBox" id="formNumTableRows" constraints="{min:1}" style="width: 100%;"/>
                        </span>
                    </div>
                </span>
                <span id="span_formThesaurusUrl" class="outer hide">
                    <div>
                        <span class="label">
                            <label for="formThesaurusUrl" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 9999)">
                                <fmt:message key="dialog.admin.additionalfields.thesaurusUrl" />
                            </label>
                        </span>
                        <span class="input">
                            <input data-dojo-type="dijit/form/TextBox" id="formThesaurusUrl" style="width: 100%;"/>
                        </span>
                    </div>
                </span>
                <span id="span_formThesaurusLinkLabel" class="outer hide">
                    <div>
                        <span class="label">
                            <label for="formThesaurusLinkLabel" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10104)">
                                <fmt:message key="dialog.admin.additionalfields.thesaurusLinkLabel" />
                            </label>
                        </span>
                        <span class="input">
                            <div data-dojo-type="dijit/layout/TabContainer" doLayout="false" id="formThesaurusLinkLabel" style="width:100%;" selected="en">
                            </div>
                        </span>
                    </div>
                </span>
                <span id="span_formColumns" class="outer required hide">
                    <div>
                        <span class="label">
                            <label for="formColumns" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10113)">
                                <fmt:message key="dialog.admin.additionalfields.columns" />*
                            </label>
                            <span id="availWidth" style="font-weight:normal; float:right;"></span>
                        </span>
                        <span id="formColumnsContent" class="input innerPadding" style="border: 1px solid gray;">
                        </span>
                        <button data-dojo-type="dijit/form/Button" onclick="pageAdditionalFieldsDlg.addColumn()" style="float:right;">
                            <fmt:message key="dialog.admin.additionalfields.btnAddColumn" />
                        </button>
                    </div>
                </span>
                <div class="fill">
                </div>
                <!-- CONTENT END -->
            </div>
            <div data-dojo-type="dijit/layout/ContentPane" region="bottom" class="content" style="height:49px; background-color:#EDEDED;">
                <hr/>
                <button data-dojo-type="dijit/form/Button" onclick="pageAdditionalFieldsDlg.closeThisDialog()" style="float:left;">
                    <fmt:message key="dialog.admin.additionalfields.btnClose" />
                </button>
                <button id="saveAddField" data-dojo-type="dijit/form/Button" onclick="pageAdditionalFieldsDlg.save()" style="float:right;">
                    <fmt:message key="dialog.admin.additionalfields.btnAdd" />
                </button>
            </div>
        </div>
</body>
</html>
