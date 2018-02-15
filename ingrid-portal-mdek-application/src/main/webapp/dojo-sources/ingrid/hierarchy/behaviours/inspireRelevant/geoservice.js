/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
define([
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/on",
    "dojo/string",
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Syslist",
    "ingrid/utils/Grid",
    "ingrid/hierarchy/behaviours/utils"
], function(declare, array, on, string, topic, registry, message, UtilSyslist, UtilGrid, utils) {

    return declare(null, {
        title: "Geodatenservice",
        description: "Wenn aktiviert und der Datensatz ist ein Geodatenservice (INSPIRE-relevant), dann wird die Spezfikation auf \"VO 976/2009\" und \"nicht evaluiert\" gesetzt.",
        defaultActive: true,
        category: "INSPIRE relevant",
        events: [],
        publishEvent: null,
        specificationName: null,
        run: function() {
            var self = this;

            this.specificationName = UtilSyslist.getSyslistEntryName(6005, 10);

            // only for geo services
            topic.subscribe("/onObjectClassChange", function(msg) {
                if (msg.objClass === "Class3") {
                    self.register();
                } else {
                    self.unregister();
                }
            });
        },

        register: function() {
            var self = this;
            var inspireRelevantWidget = registry.byId("isInspireRelevant");

            this.events.push(
                on(inspireRelevantWidget, "Click", function(isChecked) {
                    if (this.checked) {
                        utils.addConformity(self.specificationName, "3");
                    }
                }),

                on(inspireRelevantWidget, "Change", function(isChecked) {
                    if (isChecked) {
                        var missingMessage = string.substitute(message.get("validation.specification.missing.service"), [self.specificationName]);
                        self.publishEvent = topic.subscribe("/onBeforeObjectPublish", function(/*Array*/ notPublishableIDs) {
                            var requiredSpecification = UtilGrid.getTableData("extraInfoConformityTable")
                                .filter(function(item) { return item.specification === self.specificationName; });

                            if (requiredSpecification.length === 0) {
                                notPublishableIDs.push( ["extraInfoConformityTable", missingMessage] );
                            }

                        });
                    } else {
                        utils.removeEvents([self.publishEvent]);
                    }
                })
            );
        },

        unregister: function() {
           utils.removeEvents(this.events);
           utils.removeEvents([this.publishEvent]);
        }
    })();
});
