/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
    "dojo/_base/declare",
    "dojo/topic",
    "ingrid/utils/Store",
    "ingrid/utils/Syslist",
    "module"
], function(registry, declare, topic, UtilStore, UtilSyslist, module) {

    return declare(null, {
        title: "Standardeinträge",
        description: "Standardeinträge für einige Metadatenfelder",
        defaultActive: true,
        category: "BAW-MIS",

        run: function() {
            var self = require(module.id);

            topic.subscribe("/onObjectClassChange", function(data) {

                var isNewItem = "newNode" === currentUdk.uuid;
                if (isNewItem && data.objClass !== "Class0") {
                    self._setOpendataKeywords();
                    self._setInspireThemes();
                    self._setIsoTopics();
                    self._setCharsetData();
                    self._setPublishArea();
                    self._setAccessConstraints();
                    self._setUseAccessConstraints();
                }
            });

            return;
        },

        _setOpendataKeywords: function () {
            this._addTableEntriesByEntryNames({
                tableId: "categoriesOpenData",
                listId: 6400,
                entryIds: [ "10" ] // Transport und Verkehr
            });
        },

        _setInspireThemes: function () {
            this._addTableEntriesByEntryIds({
                tableId: "thesaurusInspire",
                entryIds: [ "108" ] // Gewässernetz
            });
        },

        _setIsoTopics: function () {
            this._addTableEntriesByEntryIds({
                tableId: "thesaurusTopics",
                entryIds: [ "18" ] // transportation
            });
        },

        _setCharsetData: function () {
            // If the dataset is new (not saved yet), then initialise the
            // value as "utf8"
            var datasetCharsetUtf8Value = "4";
            var datasetCharsetWidgetId = "extraInfoCharSetData";

            var datasetCharsetWidget = registry.byId(datasetCharsetWidgetId);
            if (datasetCharsetWidget && !datasetCharsetWidget.get("value")) {
                datasetCharsetWidget.set("value", datasetCharsetUtf8Value);
            }
        },

        _setPublishArea: function () {
            registry.byId("extraInfoPublishArea").set("value", "2");
        },

        _setAccessConstraints: function () {
            this._addTableEntriesByEntryNames({
                tableId: "availabilityAccessConstraints",
                listId: 6010,
                entryIds: [ "1" ] // Es gelten keine Zugriffsbeschränkungen
            });
        },

        _setUseAccessConstraints: function () {
            var id = "availabilityUseAccessConstraints";
            var theTable = registry.byId(id);
            var tableData = theTable.data;

            var listId = 6500;
            var entryId = "27"; // CC BY 4.0
            var entryTitle = UtilSyslist.getSyslistEntryName(listId, entryId);
            var source = "Bundesanstalt für Wasserbau";

            var existing = tableData.find(function (row) {
                return row.title === entryTitle && row.source === source;
            });
            if (!existing) {
                tableData.push({
                    title: entryTitle,
                    source: source
                });
            }
            UtilStore.updateWriteStore(id, tableData);
        },

        _addTableEntriesByEntryNames: function (args) {
            var theTable = registry.byId(args.tableId);
            var tableData = theTable.data;

            for (var i=0; i<args.entryIds.length; i++) {
                var entryId = args.entryIds[i];
                var entryTitle = UtilSyslist.getSyslistEntryName(args.listId, entryId);

                var existing = tableData.find(function (value) {
                    return value.title === entryTitle;
                });
                if (!existing) {
                    tableData.push({title: entryTitle});
                    UtilStore.updateWriteStore(args.tableId, tableData);
                }
            }
        },

        _addTableEntriesByEntryIds: function (args) {
            var theTable = registry.byId(args.tableId);
            var tableData = theTable.data;

            for (var i=0; i<args.entryIds.length; i++) {
                var entryId = args.entryIds[i];

                var existing = tableData.find(function (value) {
                    return value.title === entryId;
                });
                if (!existing) {
                    tableData.push({title: entryId});
                    UtilStore.updateWriteStore(args.tableId, tableData);
                }
            }
        }

    })();
});

