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
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dojo/on",
    "dojo/topic",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/message",
    "ingrid/utils/Store",
    "ingrid/utils/Syslist"
], function(registry, array, declare, lang, domClass, construct, on, topic, dirty, creator, message, UtilStore, UtilSyslist) {

    return declare(null, {
        title: "BAW-Allgemein",
        description: "Allgemeine UI Anpassungen für die BAW",
        defaultActive: true,
        category: "BAW-MIS",
        run: function() {
            var promise = this._createCustomFields();

            // For now, don't make hierarchyLevelName mandatory in this profile
            domClass.remove("uiElementAddbawHierarchyLevelName", "required");

            topic.subscribe("/onObjectClassChange", function(data) {

                var isNewItem = "newNode" === currentUdk.uuid;

                // ========================================
                // Modifications to existing fields
                // ========================================

                // ----------------------------------------
                // Allgemeines
                // ----------------------------------------
                // Kurzbezeichnung
                domClass.add("uiElement5000", "hide");
                // Inspire relevant
                domClass.add("uiElement6000", "hidden");
                // AdV kompatibel checkbox
                domClass.add("uiElement6005", "hide");
                // Open Data checkbox
                /*
                 * opendata.js runs after this script and removes the 'hide'
                 * class. So add the 'hidden' class instead!
                 */
                domClass.add("uiElement6010", "hidden");

                // Kategorien (opendata)
                var ogdCategoriesTableId = "categoriesOpenData";
                var ogdCategoriesTable = registry.byId(ogdCategoriesTableId);

                domClass.add("uiElement6020", "required");
                domClass.remove("uiElement6020", "hide");
                domClass.remove("uiElement6020", "halfWidth");
                ogdCategoriesTable.reinitLastColumn(true);

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

                // ----------------------------------------
                // Verschlagwortung
                // ----------------------------------------
                // Inspire priority data set
                domClass.add("uiElement5090", "hidden");
                // Inspire räumlicher Anwendungsbereich
                domClass.add("uiElement5095", "hidden");
                // AdV-Produktgruppe
                domClass.add("uiElement5170", "hide");


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

                // Themen
                domClass.add("uiElementN014", "hide");

                // ----------------------------------------
                // Fachbezug
                // ----------------------------------------
                // Datensatz/Datenserie
                domClass.add("uiElement5061", "hide");
                // Digitale Repräsentation
                domClass.add("uiElement5062", "hide");
                // Vektorformat
                domClass.add("uiElementN005", "hide");
                // Symbolkatalog
                domClass.add("uiElement3555", "hide");
                // Schlüsselkatalog
                domClass.add("uiElement3535", "hide");
                // Sachdaten/Attributinformation
                domClass.add("uiElement5070", "hide");
                // Darstellender Dienst
                domClass.add("uiElementN003", "hide");
                // Herstellungsprozess
                domClass.add("uiElement3515", "hide");

                // ----------------------------------------
                // Datenqualität
                // ----------------------------------------
                // Hide the entire category
                domClass.add("refClass1DQ", "hidden");

                // ----------------------------------------
                // Raumbezugssystem
                // ----------------------------------------
                // Höhe
                domClass.add("uiElementN010", "hide");
                // Erläuterungen
                domClass.add("uiElement1140", "hide");

                // ----------------------------------------
                // Zeitbezug
                // ----------------------------------------
                // Zeitbezug der Ressource
                domClass.remove("uiElement5030", "halfWidth");
                registry.byId("timeRefTable").reinitLastColumn(true);
                // Erläuterungen
                domClass.add("uiElement1250", "hide");
                // Status
                domClass.add("uiElement1220", "hide");
                // Periodizität
                domClass.add("uiElement1240", "hide");
                // Im Intervall
                domClass.add("uiElement1230", "hide");



                // ----------------------------------------
                // Zusatzinformation
                // ----------------------------------------
                // Zeichensatz des Datensatzes
                // Make the node mandatory if it has not been hidden by some
                // other rule
                var datasetCharsetNodeId = "uiElement5043";
                if(!domClass.contains(datasetCharsetNodeId, "hide")) {
                    domClass.add(datasetCharsetNodeId, "required");
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

                // XML-Export-Kriterium
                domClass.add("uiElementN012", "hide");
                // Herstellungszweck
                domClass.add("uiElementN013", "hide");
                // Konformität
                domClass.remove("uiElementN024", "required");
                // Rechtliche Grundlagen
                domClass.add("uiElement1350", "hide");
                // Eignung/Nutzung
                domClass.add("uiElement5040", "hide");



                // ----------------------------------------
                // Verfügbarkeit
                // ----------------------------------------
                // Kodierungsschema der geografischen Daten
                // Not necessarily a required field. See #1273
                // Medienoption
                domClass.add("uiElement1310", "hidden");
                // Bestellinformation
                domClass.add("uiElement5052", "hidden");

            });

            return promise;
        },

        _createCustomFields: function () {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            // TODO mark fields as "required" if not Auftrag

            var id;

            id = "bawHierarchyLevelName";
            construct.place(
                creator.createDomSelectBox({
                    id: id,
                    name: message.get("ui.obj.baw.hierarchy.level.name.title"),
                    help: message.get("ui.obj.baw.hierarchy.level.name.help"),
                    isMandatory: true,
                    useSyslist: 3950002,
                    style: "width: 100%"
                }),
                "uiElement5100", "before"
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));
            on(registry.byId(id), "Change", function (newVal) {
                var key = UtilSyslist.getSyslistEntryKey(3950002, newVal);
                var isSimulationRelated = key === "6" // datei
                    || key === "18"  // simulationslauf
                    || key === "19"  // simulationsmodell
                    || key === "22"  // szenario
                    || key === "24";  // variante
                var isSimulationRunOrFile = key === "6" // datei
                    || key === "18";  // simulationslauf
                topic.publish("onBawHierarchyLevelNameChange", {
                    isSimulationRelated: isSimulationRelated,
                    isSimulationRunOrFile: isSimulationRunOrFile
                })
            });


            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
            return registry.byId(id).promiseInit;
        }

    })();
});

