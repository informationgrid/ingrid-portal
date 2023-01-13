/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
    "ingrid/utils/Syslist"
], function(registry, array, declare, lang, domClass, construct, on, topic, dirty, creator, message, UtilSyslist) {

    const HIERARCHY_LEVEL_NAME_ID = "bawHierarchyLevelName";

    return declare(null, {
        title: "Bezeichnung-der-Hierarchieebene",
        description: "Bezeichnung der Hierarchieebene",
        defaultActive: true,
        category: "BAW-MIS",
        run: function() {
            var promise = this._createCustomFields();

            // For now, don't make hierarchyLevelName mandatory in this profile
            domClass.remove("uiElementAddbawHierarchyLevelName", "required");

            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass === "Class1") {
                    domClass.remove("uiElementAdd" + HIERARCHY_LEVEL_NAME_ID, "hide");
                } else {
                    domClass.add("uiElementAdd" + HIERARCHY_LEVEL_NAME_ID, "hide");
                }
            });

            return promise;
        },

        _createCustomFields: function () {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            construct.place(
                creator.createDomSelectBox({
                    id: HIERARCHY_LEVEL_NAME_ID,
                    name: message.get("ui.obj.baw.hierarchy.level.name.title"),
                    help: message.get("ui.obj.baw.hierarchy.level.name.help"),
                    isMandatory: true,
                    useSyslist: 3950002,
                    style: "width: 100%"
                }),
                "uiElement5100", "before"
            );
            newFieldsToDirtyCheck.push(HIERARCHY_LEVEL_NAME_ID);
            additionalFields.push(registry.byId(HIERARCHY_LEVEL_NAME_ID));
            on(registry.byId(HIERARCHY_LEVEL_NAME_ID), "Change", function (newVal) {
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
            return registry.byId(HIERARCHY_LEVEL_NAME_ID).promiseInit;
        }

    })();
});

