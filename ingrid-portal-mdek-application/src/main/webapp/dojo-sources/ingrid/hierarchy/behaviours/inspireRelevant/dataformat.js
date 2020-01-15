/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/dom-class",
    "dojo/on",
    "dojo/string",
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Syslist",
    "ingrid/utils/Grid",
    "ingrid/hierarchy/behaviours/utils"
], function (declare, array, domClass, on, string, topic, registry, message, UtilSyslist, UtilGrid, utils) {

    return declare(null, {
        title: "Datenformat",
        description: "Wenn aktiviert dann wird die Tabelle Datenformat für Geodatensätze abhängig von INSPIRE relevant + konform zum Pflichtfeld und benötigt ein Format mit dem Namen \"GML\"",
        defaultActive: true,
        category: "INSPIRE relevant",
        events: [],
        publishEvent: null,
        validationIsOn: false,

        run: function () {
            var self = this;
            // only for geo datasets
            topic.subscribe("/onObjectClassChange", function (msg) {
                // only register if class 1 and if not already registered
                if (msg.objClass === "Class1") {
                    if (self.events.length === 0) {
                        self.register();
                    }
                } else if (msg.objClass === "Class3") {
                    self.unregister();
                    domClass.remove("uiElement1320", "required");
                    domClass.add("uiElement1320", "show");
                } else {
                    self.unregister();
                }
            });
        },

        register: function () {
            var self = this;
            var inspireRelevantWidget = registry.byId("isInspireRelevant");
            var isConformWidget = registry.byId("isInspireConform");

            inspireRelevantWidget.checked ? domClass.add("uiElement1320", "required") : domClass.add("uiElement1320", "show");

            this.events.push(
                // if conform was changed
                on(isConformWidget, "Change", function(isChecked) {
                    if (inspireRelevantWidget.checked && isChecked) {
                        self.activateValidation();
                    } else {
                        self.deactivateValidation();
                    }
                }),
                on(inspireRelevantWidget, "Change", function (isChecked) {
                    if (isChecked && isConformWidget.checked) {
                        self.activateValidation();
                    } else {
                        self.deactivateValidation();
                    }
                })
            );
        },

        activateValidation: function() {
            if (!this.validationIsOn) {
                console.log("Activate dataformat validation");
                this.validationIsOn = true;
                this.publishEvent = topic.subscribe("/onBeforeObjectPublish", function (/*Array*/ notPublishableIDs) {
                    var requiredSpecification = UtilGrid.getTableData("availabilityDataFormat")
                        .filter(function (item) {
                            return item.name === "GML" && item.version && item.version.trim().length > 0;
                        });

                    if (requiredSpecification.length === 0) {
                        notPublishableIDs.push([
                            "availabilityDataFormat", message.get("validation.dataformat.missing.gml")
                        ]);
                    }

                });
                domClass.add("uiElement1320", "required");
                domClass.remove("uiElement1320", "show");
            }
        },

        deactivateValidation: function () {
            if (this.validationIsOn) {
                this.validationIsOn = false;
                console.log("Deactivate dataformat validation");
                utils.removeEvents([this.publishEvent]);
                this.publishEvent = null;
                domClass.remove("uiElement1320", "required");
                domClass.add("uiElement1320", "show");
            }
        },

        unregister: function () {
            utils.removeEvents(this.events);
            utils.removeEvents([this.publishEvent]);
            this.publishEvent = null;
            domClass.remove("uiElement1320", "required");
            domClass.remove("uiElement1320", "show");
        }
    })();
});
