/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
    "dojo/on",
    "dojo/topic",
    "dijit/registry",
    "ingrid/utils/UI",
    "ingrid/hierarchy/behaviours/utils"
], function(declare, on, topic, registry, UtilUI, utils) {
    return declare(null, {
        
        title : "INSPIRE - priority data set",
        description : "Zeige das Feld für Geodatensätze und -dienste an.",
        defaultActive : true,
        category: "Felder",
        events: [],
        run : function() {
            var self = this;
            // only for geo dataset
            topic.subscribe("/onObjectClassChange",  function(msg) {
                if (msg.objClass === "Class1" || msg.objClass === "Class3") {
                    self.register();
                } else {
                    self.unregister();
                }
            });
        },

        register: function() {
            var inspireRelevantWidget = registry.byId("isInspireRelevant");

            // when registering the event handler then the change might already has happened
            // so that we have to set here manually
            if (inspireRelevantWidget.checked) {
                UtilUI.setShow("uiElement5090");
                registry.byId("priorityDataset").reinitLastColumn();
            }

            this.events.push(
                // show/hide radio boxes when inspire relevant was checked
                on(inspireRelevantWidget, "Change", function(isChecked) {
                    if (isChecked) {
                        UtilUI.setShow("uiElement5090");
                    } else {
                        UtilUI.setOptional("uiElement5090");
                    }
                })
            )
        },

        unregister: function() {
            utils.removeEvents(this.events);
            UtilUI.setHide("uiElement5090");
        }
        
    })();
});
