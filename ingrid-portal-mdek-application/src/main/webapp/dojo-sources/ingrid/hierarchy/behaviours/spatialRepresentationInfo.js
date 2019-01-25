/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
    "ingrid/message",
    "ingrid/utils/Syslist",
    "ingrid/utils/Tree",
    "dojo/NodeList-traverse"
], function (declare, array, lang, domClass, on, query, topic, MenuItem, Button, registry, message, Syslist, TreeUtils) {

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
                } else {
                    domClass.add("ref1GridFormat", "hide");
                }
                // UtilUI.showToolTip("ref1Representation", message.get("validation.digitalRepresentation.conform"));
            });

            var fxnGridSpatialRepresentationValidation = function(msg) {
                var numDimensions = registry.byId("ref1NumDimensions").get("value");
                var cellGeometry = registry.byId("ref1CellGeometry").get("value");
                var axisDimName = registry.byId("ref1AxisDimName").get("value");
                var axisDimSize = registry.byId("ref1AxisDimSize").get("value");

                // If any of the items has been defined, then all of them should
                // be marked as required
                if (numDimensions
                    || (cellGeometry && cellGeometry.trim())
                    || (axisDimName && axisDimName.trim())
                    || (axisDimSize && axisDimSize.trim())) {

                    domClass.add("uiElement5302", "required");
                    domClass.add("uiElement5305", "required");
                    domClass.add("uiElement5303", "required");
                    domClass.add("uiElement5304", "required");
                } else {
                    domClass.remove("uiElement5302", "required");
                    domClass.remove("uiElement5305", "required");
                    domClass.remove("uiElement5303", "required");
                    domClass.remove("uiElement5304", "required");
                }
            };


            on(registry.byId("ref1NumDimensions"), "Change", fxnGridSpatialRepresentationValidation);
            on(registry.byId("ref1CellGeometry"), "Change", fxnGridSpatialRepresentationValidation);
            on(registry.byId("ref1AxisDimName"), "Change", fxnGridSpatialRepresentationValidation);
            on(registry.byId("ref1AxisDimSize"), "Change", fxnGridSpatialRepresentationValidation);
        }
    })();
});
