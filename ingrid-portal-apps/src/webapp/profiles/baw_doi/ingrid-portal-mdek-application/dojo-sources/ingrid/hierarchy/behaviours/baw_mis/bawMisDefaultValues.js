/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
    "ingrid/utils/Syslist"
], function(registry, declare, topic, UtilStore, UtilSyslist) {

    return declare(null, {
        title: "Standardeinträge",
        description: "Standardeinträge für einige Metadatenfelder",
        defaultActive: true,
        category: "BAW-MIS",

        run: function() {
            topic.subscribe("/onObjectClassChange", function(data) {

                var isNewItem = "newNode" === currentUdk.uuid;
                if (!isNewItem) return;

                // Kategorien (opendata)
                var ogdCategoriesTableId = "categoriesOpenData";
                var ogdCategoriesTable = registry.byId(ogdCategoriesTableId);

                if (isNewItem && data.objClass !== "Class0") {
                    var listId = 6400;
                    var entryId = "10";
                    var entryTitle = UtilSyslist.getSyslistEntryName(listId, entryId);

                    var ogdCategoriesTableData = ogdCategoriesTable.data;
                    var existing = ogdCategoriesTableData.find(function (value) {
                        return value.title === entryTitle;
                    });
                    if (!existing) {
                        ogdCategoriesTableData.push({title: entryTitle });
                        UtilStore.updateWriteStore(ogdCategoriesTableId, ogdCategoriesTableData);
                    }
                }


                // INSPIRE Themen
                // Automatically add entry for "Gewässernetz" to new items
                var inspireThemeTableId = "thesaurusInspire";
                var inspireThemeTable = registry.byId(inspireThemeTableId);
                if (isNewItem && data.objClass !== "Class0") {
                    var inspireThemeTableData = inspireThemeTable.data;
                    // title 108 is for Gewässernetz
                    var existing = inspireThemeTableData.find(function (row) {
                        return row.title === "108";
                    });
                    if (!existing) {
                        inspireThemeTableData.push({title: "108"});
                        UtilStore.updateWriteStore(inspireThemeTableId, inspireThemeTableData);
                    }
                }

                // ISO-Themenkategorie
                // For new items, add "transportation" as category automatically
                var topicsTableId = "thesaurusTopics";
                var topicsTableNodeId = "uiElement5060";
                if (isNewItem && data.objClass !== "Class0") {
                    var topicsTableData = registry.byId(topicsTableId).data;
                    // title 18 is for transportation
                    var existing = topicsTableData.find(function (row) {
                        return row.title === "18";
                    });
                    if (!existing) {
                        topicsTableData.push({title: "18"});
                        UtilStore.updateWriteStore(topicsTableId, topicsTableData);
                    }
                }

                // If the dataset is new (not saved yet), then initialise the
                // value as "utf8"
                var datasetCharsetUtf8Value = "4";
                var datasetCharsetWidgetId = "extraInfoCharSetData";

                var datasetCharsetWidget = registry.byId(datasetCharsetWidgetId);
                if (isNewItem
                    && datasetCharsetWidget
                    && !datasetCharsetWidget.get("value")) {
                    datasetCharsetWidget.set("value", datasetCharsetUtf8Value);
                }

            });

            return;
        }

    })();
});

