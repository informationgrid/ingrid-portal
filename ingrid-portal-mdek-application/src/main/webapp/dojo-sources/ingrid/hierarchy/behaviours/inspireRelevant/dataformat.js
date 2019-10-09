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
        description: "Wenn aktiviert dann wird die Tabelle Datenformat abhängig von INSPIRE relevant zum Pflichtfeld und benötigt ein Format mit dem Namen \"GML\"",
        defaultActive: true,
        category: "INSPIRE relevant",
        events: [],
        publishEvent: null,

        run: function () {
            var self = this;
            // only for geo services
            topic.subscribe("/onObjectClassChange", function (msg) {
                // only register if class 1 or 3 and if not already registered
                if (msg.objClass === "Class1" || msg.objClass === "Class3") {
                    if (self.events.length === 0) {
                        self.register();
                    }
                } else {
                    self.unregister();
                }
            });
        },

        register: function () {
            var self = this;
            var inspireRelevantWidget = registry.byId("isInspireRelevant");

            this.events.push(
                on(inspireRelevantWidget, "Change", function (isChecked) {
                    if (isChecked) {
                        self.publishEvent = topic.subscribe("/onBeforeObjectPublish", function (/*Array*/ notPublishableIDs) {
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
                    } else {
                        utils.removeEvents([self.publishEvent]);
                        self.publishEvent = null;
                        domClass.remove("uiElement1320", "required");
                    }
                })
            );
        },

        unregister: function () {
            utils.removeEvents(this.events);
            utils.removeEvents([this.publishEvent]);
            this.publishEvent = null;
            domClass.remove("uiElement1320", "required");
        }
    })();
});
