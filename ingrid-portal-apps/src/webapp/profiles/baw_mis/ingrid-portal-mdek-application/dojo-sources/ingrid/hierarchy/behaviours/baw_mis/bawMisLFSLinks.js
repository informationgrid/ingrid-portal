/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
define([
    "dijit/registry",
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/aspect",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dojo/on",
    "dojo/topic",
    "dijit/MenuItem",
    "dijit/MenuSeparator",
    "ingrid/dialog",
    "ingrid/grid/CustomGridEditors",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/menu",
    "ingrid/message",
    "ingrid/utils/Store",
    "ingrid/utils/Syslist",
    "ingrid/utils/UI",
    "module"
], function(registry, array, declare, lang, aspect, dom, domClass, construct, on, topic, MenuItem, MenuSeparator, dialog, gridEditors, dirty, creator, menu, message, UtilStore, UtilSyslist, UtilUI, module) {

    const LFS_LINK_TABLE_ID = "lfsLinkTable";

    return declare(null, {
        title: "Langfristspeicher",
        description: "Verschieben von Datensätzen in den Langfristspeicher",
        defaultActive: true,
        category: "BAW-MIS",

        run: function() {
            this._createCustomFields();
            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass === "Class1") {
                    domClass.remove("uiElementAdd" + LFS_LINK_TABLE_ID, "hide");
                    registry.byId(LFS_LINK_TABLE_ID).reinitLastColumn(true);
                } else {
                    domClass.add("uiElementAdd" + LFS_LINK_TABLE_ID, "hide");
                }
            });

            this._addActivationBehaviour();
        },

        _createCustomFields: function () {
            var self = require(module.id);
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            // create context menu
            this._createBawLfsLinkTableContextMenu();

            // Create the simulation table parameter
            creator.createDomDataGrid({
                id: LFS_LINK_TABLE_ID,
                name: message.get("ui.obj.baw.lfs.link.table.title"),
                help: message.get("ui.obj.baw.lfs.link.table.help"),
                contextMenu: "BAW_LFS_LINK",
                visible: "optional",
                style: "width: 100%"
            }, this.getStructureForLfsLinkTable(), "refClass1");

            newFieldsToDirtyCheck.push(LFS_LINK_TABLE_ID);
            additionalFields.push(registry.byId(LFS_LINK_TABLE_ID));

            // Add link for creating a new entry to the simulation parameter table
            creator.createTableLink(
                "lfsLinkTable",
                message.get("ui.obj.baw.lfs.link.table.new.row"),
                'dialogs/mdek_baw_lfs_link_dialog.jsp',
                message.get('ui.obj.baw.lfs.link.table.new.row'),
                message.get('ui.obj.baw.lfs.link.table.new.row.tooltip')
            );

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
        },

        /**
         * PSP and Streckenabschnitte need to be filled
         * @private
         */
        _addActivationBehaviour: function () {

            var checkCondition = function() {
                var psp = registry.byId("bawAuftragsnummer");
                var bwastr = registry.byId("bwastrTable");
                if (psp.value.trim().length > 0 && bwastr.data.length > 0) {
                    UtilUI.enableHtmlLink("lfsLinkTableLink");
                } else {
                    UtilUI.disableHtmlLink("lfsLinkTableLink");
                }
            }

            on(registry.byId("bawAuftragsnummer"), "Change", checkCondition);
            aspect.after(registry.byId("bwastrTable"), "onDataChanged", checkCondition);
        },

        getStructureForLfsLinkTable: function() {
            return [
                {
                    field: "name",
                    hidden: true
                }, {
                    field: "link",
                    name: message.get("ui.obj.baw.lfs.link.table.column.link"),
                    editable: false,
                    isMandatory: true,
                    width: "500px",
                    formatter: function(row, cell, value, columnDef, dataContext) {
                        return "<a href='" + value + "' target='_blank' title='" + value + "'>" + (dataContext.name || value) + "</a>"
                    },
                }, {
                    field: "explanation",
                    hidden: true
                }, {
                    field: "fileFormat",
                    name: message.get("ui.obj.baw.lfs.link.table.column.fileFormat"),
                    editable: true,
                    type: gridEditors.ComboboxEditor,
                    options: [], // will be filled later, when syslists are loaded
                    listId: 1320
                }, {
                    field: "urlType",
                    hidden: true
                }
            ];
        },

        _createBawLfsLinkTableContextMenu: function () {
            var type = "BAW_LFS_LINK";
            var contextMenu = menu.initContextMenu({contextMenu: type});
            contextMenu.addChild(new MenuSeparator());
            contextMenu.addChild(new MenuItem({
                id: "menuEditClicked_" + type,
                label: message.get('contextmenu.table.editClicked'),
                onClick: function () {
                    var rowData = clickedSlickGrid.getData()[clickedRow];
                    var dialogData = {
                        gridId: clickedSlickGridProperties.id,
                        selectedRow: rowData
                    };
                    dialog.showPage(message.get("ui.obj.baw.lfs.link.table.edit.row"), 'dialogs/mdek_baw_lfs_link_dialog.jsp?c=' + userLocale, 600, 300, true, dialogData);
                }
            }));
        },
    })();
});

