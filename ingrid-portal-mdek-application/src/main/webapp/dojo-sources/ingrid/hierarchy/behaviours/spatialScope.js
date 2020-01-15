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
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/on",
    "dojo/topic",
    "dijit/registry",
    "ingrid/utils/UI",
    "ingrid/utils/Syslist",
    "ingrid/hierarchy/behaviours/utils"
], function(array, declare, on, topic, registry, UtilUI, UtilSyslist, utils) {
    return declare(null, {
        
        title : "INSPIRE - Räumlicher Anwendungsbereich",
        description : "Zeige das Feld für Geodatensätze und -dienste an. Pflichtfeld für Geodatensatz und INSPIRE-relevant. Wenn nicht INSPIRE-relevant, dann für Geodatensätze immer sichtbar, aber für Geodatendienste optional. Bei neu angelegten Datensätzen wird der in den Katalogeinstellungen definierte Defaultwert verwendet (z.B. Regional, Global, ...).",
        defaultActive : true,
        category: "Felder",
        params: [{id: "defaultValue", label: "Defaultwert", "default": "Regional"} ],
        events: [],
        run : function() {
            var self = this;
            var defaultValue = this.determineDefaultValue();

            // only for geo dataset
            topic.subscribe("/onObjectClassChange",  function(msg) {
                if (msg.objClass === "Class1" || msg.objClass === "Class3") {
                    self.register(defaultValue);
                } else {
                    self.unregister();
                }
            });
        },

        handleInspireRelevantState: function (isChecked) {
            var clazz = registry.byId("objectClass").get("value");

            if (isChecked) {
                if (clazz === "Class1") {
                    UtilUI.setMandatory("uiElement5095");
                }
            } else {
                if (clazz === "Class1") {
                    UtilUI.setShow("uiElement5095");
                } else {
                    UtilUI.setOptional("uiElement5095");
                }
            }
        },

        handleInspireRelevantClick: function(isChecked, defaultValue) {
            var clazz = registry.byId("objectClass").get("value");
            if (isChecked && clazz === "Class1") {
                registry.byId("spatialScope").set("value", defaultValue);
            }
        },

        register: function(defaultValue) {
            var self = this;
            var inspireRelevantWidget = registry.byId("isInspireRelevant");

            // when registering the event handler then the change might already has happened
            // so that we have to set here manually
            this.handleInspireRelevantState(inspireRelevantWidget.checked);

            this.events.push(
                // show/hide radio boxes when inspire relevant was checked
                on(inspireRelevantWidget, "Change", function(isChecked) {
                    self.handleInspireRelevantState(isChecked);
                }),
                on(inspireRelevantWidget, "Click", function() {
                    self.handleInspireRelevantClick(inspireRelevantWidget.checked, defaultValue);
                })
            )
        },

        unregister: function() {
            utils.removeEvents(this.events);
            UtilUI.setHide("uiElement5095");
        },

        determineDefaultValue: function () {
            var defaultParam = array.filter(this.params, function (p) {
                return p.id === "defaultValue";
            })[0];
            var defaultName = defaultParam.value !== undefined ? defaultParam.value : defaultParam["default"];
            return UtilSyslist.getSyslistEntryKey(6360, defaultName);
        }
        
    })();
});
