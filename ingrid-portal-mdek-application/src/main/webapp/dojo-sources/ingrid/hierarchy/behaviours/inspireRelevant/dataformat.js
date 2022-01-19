/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
        description: "Wenn aktiviert dann wird die Tabelle Datenformat für Geodatensätze abhängig von INSPIRE relevant + konform zum Pflichtfeld",
        defaultActive: true,
        category: "INSPIRE relevant",
        events: [],
        validationIsOn: false,

        run: function () {
            var self = this;
            // only for geo datasets
            topic.subscribe("/onObjectClassChange", function (msg) {
                // only register if class 1 and if not already registered
                if (msg.objClass === "Class1") {
                    if (self.events.length === 0) {
                        domClass.remove("uiElement1320", "required");
                        domClass.remove("uiElement1320", "show");
                        // delayed register to perform other unregister functions
                        setTimeout(function () { self.register() });
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

            if (inspireRelevantWidget.checked) {
                this.activateValidation();
            } else {
                this.deactivateValidation();
            }

            this.events.push(
                on(inspireRelevantWidget, "Change", function (isChecked) {
                    if (isChecked) {
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
                domClass.add("uiElement1320", "required");
                domClass.remove("uiElement1320", "show");
            }
        },

        deactivateValidation: function () {
            if (this.validationIsOn) {
                this.validationIsOn = false;
                console.log("Deactivate dataformat validation");
                domClass.remove("uiElement1320", "required");
                domClass.remove("uiElement1320", "show");
            }
        },

        unregister: function () {
            utils.removeEvents(this.events);
            this.validationIsOn = false;
            domClass.remove("uiElement1320", "required");
            domClass.remove("uiElement1320", "show");
        }
    })();
});
