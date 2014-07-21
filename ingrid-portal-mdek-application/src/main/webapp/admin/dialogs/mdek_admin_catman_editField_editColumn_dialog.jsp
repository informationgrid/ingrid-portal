<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <title>Add/Edit Column</title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <meta name="author" content="wemove digital solutions" />
        <meta name="copyright" content="wemove digital solutions GmbH" />
        <script type="text/javascript">

            var pageAdditionalFieldsColumnDlg = _container_;

            require(["dojo/on", "dojo/aspect", "dojo/dom", "dojo/query", "dojo/_base/array", "dojo/_base/lang", "dojo/dom-style",
                    "dijit/registry", "dijit/form/TextBox", "dijit/layout/ContentPane", "dojo/data/ItemFileReadStore", "dijit/form/Select",
                    "ingrid/utils/Grid", "ingrid/dialog", "ingrid/layoutCreator"
                ],
                function(on, aspect, dom, query, array, lang, style, registry, TextBox, ContentPane, ItemFileReadStore, Select, UtilGrid, dialog, layoutCreator) {

                    on(_container_, "Load", init);

                    var syncRowsInTableProgress = false;

                    

                    function init() {
                        var url = userLocale == "de" ? "js/data/additionalFormTypes_de.json" : "js/data/additionalFormTypes.json";
                        var formTypeStore = new ItemFileReadStore({url: url});
                        var select = new Select({
                            id: "formColumnType",
                            store: formTypeStore,
                            query: {
                                asColumn: "true"
                            },
                            searchAttr: "name",
                            style: "width:100%;float:left;"
                        }, "formColumnType");

                        fillFields(pageAdditionalFieldsColumnDlg.customParams.column);
                        layoutForms();
                        on(select, "Change", layoutForms);
                    }

                    function fillFields(column) {
                        on(registry.byId("formColumnOptions"), "MouseDown", function() {
                            registry.byId("formColumnOptions_" + this.selectedChildWidget.title).editorLock.commitCurrentEdit();
                        });
                        // create table with data
                        var formColumnOptionsStructure = [{
                            field: 'id',
                            name: 'id',
                            width: '30px',
                            editable: true
                        }, {
                            field: 'value',
                            name: 'option',
                            width: '300px',
                            editable: true
                        }];
                        array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                            registry.byId("formColumnOptions").addChild(new ContentPane({
                                id: "formColumnOptions_" + lang + "Container",
                                title: lang,
                                'class': "innerPadding"
                            }));
                            var divElement = document.createElement("div");
                            divElement.setAttribute("id", "formColumnOptions_" + lang);
                            dom.byId("formColumnOptions_" + lang + "Container").appendChild(divElement);
                            var props = {
                                interactive: "true",
                                moveRows: "true"
                            };
                            layoutCreator.createDataGrid("formColumnOptions_" + lang, null, formColumnOptionsStructure, null, props);

                            // add special action to events on store for synchronization
                            aspect.after(UtilGrid.getTable("formColumnOptions_" + lang), "onAddNewRow", syncOptionTableRowsNew);
                            aspect.after(UtilGrid.getTable("formColumnOptions_" + lang), "onDataChanged", syncOptionTableRowsDelete);
                            aspect.after(UtilGrid.getTable("formColumnOptions_" + lang), "onCellChange", syncOptionTableRowsCellChange);

                            // set localized label
                            var titleWidget = new TextBox({
                                id: "formColumnLabel_" + lang,
                                title: lang,
                                'class': "fullWidth"
                            });
                            registry.byId("formColumnLabel").addChild(titleWidget);
                            if (column !== undefined) {
                                if (column.options)
                                    UtilGrid.setTableData("formColumnOptions_" + lang, column.options[lang]);

                                registry.byId("formColumnLabel_" + lang).set('value', column.label[lang]);
                            }
                        });

                        // if it's not a new column
                        if (column !== undefined) {
                            registry.byId("formColumnType").set('value', column.type);
                            registry.byId("formColumnField").set('value', column.id);
                            registry.byId("formColumnField").set('disabled', true);
                            this.editMode = true;
                            registry.byId("formColumnWidth").set('value', column.width);
                            registry.byId("formColumnIndex").set('value', column.indexName);
                            //if (column.allowFreeEntries) {
                            registry.byId("columnAsCombobox").set("checked", column.allowFreeEntries);
                            //}
                            registry.byId("saveAddColumn").set("label", "<fmt:message key='dialog.admin.additionalfields.btnUpdate' />");

                            // we need to resize the table delayed since the dialog opens
                            // animated and hasn't the full size yet so that the table can 
                            // set its layout
                            if (column.type == "selectControl") {
                                //    setTimeout(function(){
                                registry.byId("formColumnOptions").resize();
                                //    }, 700);
                            }

                        } else {
                            registry.byId("formColumnWidth").set('value', 100);
                        }
                        
                        registry.byId("formColumnOptions").watch("selectedChildWidget", lang.hitch(this, function() {
                            registry.byId("formColumnOptions_de").reinitLastColumn();
                        }));
                        setTimeout(function() {
                            registry.byId("formColumnOptions_en").reinitLastColumn();
                        });
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
                                    var data = UtilGrid.getTableData("formColumnOptions_" + lang);
                                    array.some(data, function(row) {
                                        if (row.id === oldId) {
                                            row.id = newId;
                                            return true;
                                        }
                                    });
                                    UtilGrid.getTable("formColumnOptions_" + lang).invalidate();
                                }
                            });

                        }
                    }

                    function syncOptionTableRowsNew(result, params) {
                        var msg = params[0];
                        if (syncRowsInTableProgress)
                            return;

                        syncRowsInTableProgress = true;
                        console.debug("sync rows");
                        // get active tab for language of new item
                        var activelang = registry.byId("formColumnOptions").selectedChildWidget.title;
                        var newId = pageAdditionalFieldsDlg.getNewId(UtilGrid.getTableData("formColumnOptions_" + activelang));
                        console.debug(msg);
                        msg.item.id = newId;
                        msg.item.lang = activelang;

                        array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                            if (activelang != lang) {
                                var otherLangItem = {
                                    id: newId,
                                    lang: lang,
                                    value: "?"
                                };
                                UtilGrid.addTableDataRow("formColumnOptions_" + lang, otherLangItem);
                            }
                        });

                        UtilGrid.getTable("formColumnOptions_" + activelang).invalidate();

                        syncRowsInTableProgress = false;
                    }

                    function syncOptionTableRowsDelete(result, params) {
                        var msg = params[0];
                        // only sync once!
                        // since this function also calls the onNew-event we need to ignore those
                        if (syncRowsInTableProgress || msg.type != "deleted")
                            return;

                        var activelang = registry.byId("formColumnOptions").selectedChildWidget.title;

                        syncRowsInTableProgress = true;
                        console.debug("sync rows delete");

                        array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                            if (activelang != lang) {
                                var toDelete = [];
                                //console.debug("check: " + item.id + " with " + deletedItem.id + " => " + (item.id[0] == deletedItem.id[0]));
                                array.forEach(msg.items, function(deleteMe) {
                                    var pos = 0;
                                    var data = UtilGrid.getTableData("formColumnOptions_" + lang);
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
                                UtilGrid.removeTableDataRow("formColumnOptions_" + lang, toDelete);
                            }
                        });

                        syncRowsInTableProgress = false;
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

                    function layoutForms() {
                        console.debug("update form");
                        var type = registry.byId("formColumnType").get('value');
                        if (type == "selectControl") {
                            style.set(dom.byId("span_formColumnOptions"), "display", "block");
                            registry.byId("formColumnOptions").resize();
                        } else {
                            style.set(dom.byId("span_formColumnOptions"), "display", "none");
                        }
                    }

                    function save() {
                        var exitDialog = true;
                        var message = "";
                        var column = {};
                        column.type = registry.byId("formColumnType").get('value');
                        column.id = registry.byId("formColumnField").get('value');
                        column.label = {};
                        column.width = registry.byId("formColumnWidth").get('value');
                        column.indexName = registry.byId("formColumnIndex").get('value');
                        column.widthUnit = "px";

                        if (column.type == "selectControl") {
                            column.options = {};
                            column.allowFreeEntries = registry.byId("columnAsCombobox").get("checked");
                        }

                        array.forEach(pageAdditionalFields.profileData.languages, function(lang) {
                            column.label[lang] = registry.byId("formColumnLabel_" + lang).get('value');
                            if (column.type == "selectControl") {
                                column.options[lang] = UtilGrid.getTableData("formColumnOptions_" + lang);
                            }
                        });
                        console.debug(column.options);

                        if (!this.editMode && (lang.trim(column.id).length == 0 || query("#formColumnsContent #admin_" + column.id).length > 0)) {
                            exitDialog = false;
                            message = "Bitte vergeben Sie eine eindeutige ID!";
                        }

                        if (!exitDialog) {
                            dialog.show("Speichern nicht möglich", message, dialog.WARNING);
                        } else {
                            pageAdditionalFieldsColumnDlg.customParams.resultHandler.resolve(column);
                            pageAdditionalFieldsColumnDlg.hide();
                        }
                    }

                    pageAdditionalFieldsColumnDlg.save = save;
                });
		</script>
    </head>
    <body>
        <div id="catalogueObject" class="content" style="height:100%;">
            <div id="winNavi" style="top:0;">
                <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-additionalfields-2#overall-catalog-management-5', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
                    <!-- CONTENT START -->
                    <span class="outer required">
                        <div>
                            <span class="label">
                                <label for="formColumnType" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10150)">
                                    <fmt:message key="dialog.admin.additionalfields.type" />*
                                </label>
                            </span>
                            <span class="input">
                            	<!-- <div data-dojo-id="columnTypeStore" data-dojo-type="dojo/data/ItemFileReadStore" data-dojo-props="data:{items:[]}, clearOnClose:true" ></div>
                                <input data-dojo-type="dijit/form/Select" id="formColumnType" style="width:100%;float:left;" data-dojo-props="store:columnTypeStore, searchAttr: 'name'," > -->
                                <input id="formColumnType">
                            </span>
                        </div>
                    </span>
                    <span class="outer required">
                        <div>
                            <span class="label">
                                <label for="formColumnField" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10151)">
                                    <fmt:message key="dialog.admin.additionalfields.id" />*
                                </label>
                            </span>
                            <span class="input">
                                 <input data-dojo-type="dijit.form.TextBox" class="fullWidth" id="formColumnField"/>
                            </span>
                        </div>
                    </span>
                    <span class="outer">
                        <div>
                            <span class="label">
                                <label for="formColumnLabel" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10152)">
                                    <fmt:message key="dialog.admin.additionalfields.title" />
                                </label>
                            </span>
                            <span class="input">
                                <div data-dojo-type="dijit/layout/TabContainer" doLayout="false" id="formColumnLabel" style="width:100%;" selected="de">
                                    <!--<input data-dojo-type="dijit.form.TextBox" class="fullWidth" id="formColumnLabel_de" title="de"/>
                                    <input data-dojo-type="dijit.form.TextBox" class="fullWidth" id="formColumnLabel_en" title="en"/>-->
                                </div>
                            </span>
                        </div>
                    </span>
					<span id="span_formColumnOptions" class="outer">
                        <div>
                            <span class="label">
                                <label for="formColumnOptions" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10153)">
                                    <fmt:message key="dialog.admin.additionalfields.options" />
                                </label>
                            </span>
                            <div class="tableContainer">
                                <!--<div id="formColumnOptions" interactive="true" class="hideTableHeader"></div>-->
								<div id="formColumnOptions" data-dojo-type="dijit/layout/TabContainer" doLayout="false"></div>
                            </div>
                            <span class="input">
                                <input id="columnAsCombobox" data-dojo-type="dijit/form/CheckBox" value="required">
                                <label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10156)">
                                    <fmt:message key="dialog.admin.additionalfields.allowFreeEntries" />
                                </label>
                            </span>
                        </div>
                    </span>
                    <span class="outer halfWidth">
                        <div>
                            <span class="label">
                                <label for="formColumnWidth" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10154)">
                                    <fmt:message key="dialog.admin.additionalfields.width" />
                                </label>
                            </span>
                            <span class="input">
                                <table style="width:100%; margin: -2px 0 0 0;"><tr><td>
                                    <input data-dojo-type="dijit/form/NumberTextBox" constraints="{min:0}" id="formColumnWidth" class="fullWidth" />
                                </td><td style='width:1px; padding-left:5px;'>px</td></tr>
                                </table>
                            </span>
                        </div>
                    </span>
					<span class="outer halfWidth">
                        <div>
                            <span class="label">
                                <label for="formColumnIndex" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10155)">
                                    <fmt:message key="dialog.admin.additionalfields.index" />
                                </label>
                            </span>
                            <span class="input">
                                 <input data-dojo-type="dijit/form/TextBox" id="formColumnIndex" class="fullWidth" />
                            </span>
                        </div>
                    </span>
                    
                    <!-- CONTENT END -->
                    <button data-dojo-type="dijit/form/Button" onclick="pageAdditionalFieldsColumnDlg.hide()" style="float:left;">
                        <fmt:message key="dialog.admin.additionalfields.btnClose" />
                    </button>
                    <button id="saveAddColumn" data-dojo-type="dijit/form/Button" onclick="pageAdditionalFieldsColumnDlg.save()" style="float:right;" >
                        <fmt:message key="dialog.admin.additionalfields.btnAdd" />
                    </button>
                    <div class="fill"></div>
        </div>
        
    </body>
</html>