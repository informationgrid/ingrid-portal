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
define(["dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/dom-class",
    "dojo/on",
    "dojo/query",
    "dojo/topic",
    "dijit/MenuItem",
    "dijit/form/Button",
    "dijit/registry",
    "dojo/NodeList-traverse"
], function (declare, array, lang, domClass, on, query, topic, MenuItem, Button, registry) {

    return declare(null, {
        title: "Struktur der räumlichen Daten",
        description: "Wenn \"Digitale Repräsentation\" = \"Raster\", dann werden zusätzliche Felder zu Erfassung von \"georektifiziert\" bzw. \"georeferenziert\" angezeigt.",
        defaultActive: true,
        gridRepresentation: "2", // Grid
        run: function () {
            var self = this;
            on(registry.byId("ref1Representation"), "DataChanged", function (msg) {
                var hasGrid = array.some(this.data, function (element) {
                    return self.gridRepresentation === (element.title+"");
                });

                if (hasGrid) {
                    domClass.remove("ref1GridFormat", "hide");
                    // also remove hide class from required fields (which was needed for correct publication)
                    domClass.remove("uiElement5302", "hide"); // ref1NumDimensions
                    domClass.remove("uiElement5305", "hide"); // ref1CellGeometry
                    registry.byId("ref1GridAxisTable").reinitLastColumn();
                } else {
                    domClass.add("ref1GridFormat", "hide");
                    // also add hide class to required fields (which is needed for correct publication)
                    domClass.add("uiElement5302", "hide"); // ref1NumDimensions
                    domClass.add("uiElement5305", "hide"); // ref1CellGeometry
                }
            });

            on(registry.byId("ref1GridFormatRectCheckpoint"), "Change", function () {
                if (registry.byId("ref1GridFormatRectCheckpoint").get("value") === "on") {
                    domClass.add("uiElement5309", "required");
                } else {
                    domClass.remove("uiElement5309", "required");
                }
            })
        }
    })();
});
