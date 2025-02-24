/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
    "dojo/Deferred",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dojo/on",
    "dojo/query",
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
], function(registry, array, declare, lang, aspect, Deferred, dom, domClass, construct, on, query, topic, MenuItem, MenuSeparator, dialog, gridEditors, dirty, creator, menu, message, UtilStore, UtilSyslist, UtilUI, module) {

    const LFS_LINK_TABLE_ID = "lfsLinkTable";

    return declare(null, {
        title: "Langfristspeicher",
        description: "Verschieben von Datensätzen in den Langfristspeicher",
        defaultActive: true,
        category: "BAW-MIS",
        baseUrl: null,

        run: function() {
            this._setBaseUrl()
                .then(lang.hitch(this, this._createCustomFields))
                .then(lang.hitch(this, this._modifyExistingFields))
                .then(lang.hitch(this._addActivationBehaviour));

            topic.subscribe("/onObjectClassChange", function(data) {
                var isGeodata = data.objClass === "Class1";
                var isProject = data.objClass === "Class4";

                if (isGeodata || isProject) {
                    domClass.remove("uiElementAdd" + LFS_LINK_TABLE_ID, "hide");
                    registry.byId(LFS_LINK_TABLE_ID).reinitLastColumn(true);
                } else {
                    domClass.add("uiElementAdd" + LFS_LINK_TABLE_ID, "hide");
                }
            });

        },

        _setBaseUrl: function() {
            var self = this;
            var def = new Deferred()
            UtilityService.getApplicationConfigEntry( 'bawLfsBaseURL', {
                callback: function(/*string*/res) {
                    if (res.lastIndexOf("/") !== res.length - 1) {
                        res += "/";
                    }
                    self.baseUrl = res;
                    def.resolve();
                }
            });
            return def;
        },

        _createCustomFields: function () {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            // create context menu
            this._createBawLfsLinkTableContextMenu();

            // Create the simulation table parameter
            var lfsGrid = creator.createDomDataGrid({
                id: LFS_LINK_TABLE_ID,
                name: message.get("ui.obj.baw.lfs.link.table.title"),
                help: message.get("ui.obj.baw.lfs.link.table.help"),
                contextMenu: "BAW_LFS_LINK",
                style: "width: 100%"
            }, this.getStructureForLfsLinkTable(), "links");

            var node = dom.byId("uiElementN017").parentElement;
            construct.place(lfsGrid, node, 'before');

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

        _modifyExistingFields: function() {
            query("#uiElementN017 label")
                .addContent(message.get("ui.obj.baw.links.to") + "<span class=\"requiredSign\">*</span>", "only")
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
            var self = this;
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
                        return "<a href='" + self.baseUrl + value + "' target='_blank' title='" + value + "'>" + (dataContext.name || value) + "</a>"
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

