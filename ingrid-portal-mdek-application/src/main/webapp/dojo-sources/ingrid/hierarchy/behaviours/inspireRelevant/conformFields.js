/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
    "dojo/aspect",
    "dojo/string",
    "dojo/on",
    "dojo/topic",
    "dojo/dom-class",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Grid",
    "ingrid/utils/Syslist",
    "ingrid/hierarchy/behaviours/inspireRelevant/utils"
], function(declare, array, aspect, string, on, topic, domClass, registry, message, UtilGrid, UtilSyslist, utils) {
    
    return declare(null, {
        title : "Konform-Auswahl",
        description : "Wenn aktiviert, dann wird für den Typ Geodatensatz beim Aktivieren der Checkbox \"INSPIRE-relevant\", eine zusätzliche Auswahl angeboten, ob dieser Datensatz konform oder nicht ist.",
        defaultActive : true,
        category: "INSPIRE relevant",
        description: "",
        run : function() {
            
            var changeEvent = null,
                clickEvent = null;
            
            // only for geo dataset
            topic.subscribe("/onObjectClassChange", function(msg) {
                if (msg.objClass === "Class1") {
                    register();
                } else {
                    unregister();
                    // don't forget to hide the element
                    domClass.add("uiElement6001", "hidden");
                }
            });
            
            var register = function() {
                var inspireRelevantWidget = registry.byId("isInspireRelevant");
                
                // when registering the event handler then the change might already has happened
                // so that we have to set here manually
                if (inspireRelevantWidget.checked) domClass.remove("uiElement6001", "hidden");

                changeEvent = on(inspireRelevantWidget, "Change", function(isChecked) {
                    if (isChecked) {
                        domClass.remove("uiElement6001", "hidden");
                    } else {
                        domClass.add("uiElement6001", "hidden");
                    }
                });
                clickEvent = on(inspireRelevantWidget, "Click", function(isChecked) {
                    if (isChecked) {
                        registry.byId("isInspireConform").set("checked", true);
                        utils.addConformity();
                    }
                });
            };
            
            var unregister = function() {
                if (changeEvent) {
                    changeEvent.remove();
                    clickEvent.remove();
                }
            }
        }
    })();
});
