/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
define(["dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/aspect",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/on",
    "dojo/query",
    "dojo/topic",
    "dijit/MenuItem",
    "dijit/form/Button",
    "dijit/registry",
    "dojo/NodeList-traverse"
], function (declare, array, lang, aspect, dom, domClass, on, query, topic, MenuItem, Button, registry) {

    return declare(null, {
        title: "Struktur der räumlichen Daten",
        description: "Wenn \"Digitale Repräsentation\" = \"Raster\", dann werden zusätzliche Felder zu Erfassung von \"georektifiziert\" bzw. \"georeferenziert\" angezeigt.",
        defaultActive: true,
        gridRepresentation: "2", // Grid
        run: function () {
            
            this.showFieldsOnGridSelection();

            this.controlPointDescriptionBehaviour();
            
            this.mandatoryIfAnyFieldFilled();
            
            
        },
        
        mandatoryIfAnyFieldFilled: function() {
            var ref1GridAxisTable = registry.byId("ref1GridAxisTable");
            var ref1NumDimensions = registry.byId("ref1NumDimensions");
            var ref1CellGeometry = registry.byId("ref1CellGeometry");
            var ref1GridFormatRefGeoreferencedParam = registry.byId("ref1GridFormatRefGeoreferencedParam");
            var ref1TransfParamAvail = registry.byId("ref1TransfParamAvail");
            var ref1GridFormatRefOrientationParam = registry.byId("ref1GridFormatRefOrientationParam");
            var ref1GridFormatRefControlpoint = registry.byId("ref1GridFormatRefControlpoint");
            var ref1GridFormatRectDescription = registry.byId("ref1GridFormatRectDescription");
            // var ref1GridFormatRectPointInPixel = registry.byId("ref1GridFormatRectPointInPixel");
            var ref1GridFormatRectCornerPoint = registry.byId("ref1GridFormatRectCornerPoint");
            var ref1GridFormatRectCheckpoint = registry.byId("ref1GridFormatRectCheckpoint");
            
            var handleMandatoryFields = function() {
                if( ref1NumDimensions.get("value")
                    || ref1CellGeometry.get("value")
                    || ref1GridFormatRefGeoreferencedParam.get("value")
                    || ref1GridAxisTable.data.length > 0
                    || ref1TransfParamAvail.checked
                    || ref1GridFormatRefOrientationParam.checked
                    || ref1GridFormatRefControlpoint.checked
                    || ref1GridFormatRectDescription.get("value")
                    // || ref1GridFormatRectPointInPixel.get("value")
                    || ref1GridFormatRectCornerPoint.get("value")
                    || ref1GridFormatRectCheckpoint.checked
                    
                ) {
                    UtilUI.setMandatory(dom.byId("uiElement5302"));
                    UtilUI.setMandatory(dom.byId("uiElement5305"));
                } else {
                    UtilUI.setOptional(dom.byId("uiElement5302"));
                    UtilUI.setOptional(dom.byId("uiElement5305"));
                }
            };
            
            // call check to initialize fields on startup
            handleMandatoryFields();

            aspect.after(ref1GridAxisTable, "onDataChanged", handleMandatoryFields);
            on(ref1NumDimensions, "Change", handleMandatoryFields);
            on(ref1CellGeometry, "Change", handleMandatoryFields);
            on(ref1GridFormatRefGeoreferencedParam, "Change", handleMandatoryFields);
            on(ref1TransfParamAvail, "Change", handleMandatoryFields);
            on(ref1GridFormatRefOrientationParam, "Change", handleMandatoryFields);
            on(ref1GridFormatRefControlpoint, "Change", handleMandatoryFields);
            on(ref1GridFormatRectDescription, "Change", handleMandatoryFields);
            // this is a select box and always has a value, so ignore it
            // on(ref1GridFormatRectPointInPixel, "Change", handleMandatoryFields);
            on(ref1GridFormatRectCornerPoint, "Change", handleMandatoryFields);
            on(ref1GridFormatRectCheckpoint, "Change", handleMandatoryFields);
            
        },
        
        controlPointDescriptionBehaviour: function() {
            on(registry.byId("ref1GridFormatRectCheckpoint"), "Change", function () {
                if (registry.byId("ref1GridFormatRectCheckpoint").get("value") === "on") {
                    domClass.add("uiElement5309", "required");
                } else {
                    domClass.remove("uiElement5309", "required");
                }
            });
        },
        
        showFieldsOnGridSelection: function() {
            var self = this;
            on(registry.byId("ref1Representation"), "DataChanged", function () {
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
        }
    })();
});
