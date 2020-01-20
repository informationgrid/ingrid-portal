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
        title: "Raumbezugssystem",
        description: "Wenn aktiviert dann wird die Tabelle Raumbezugssystem überprüft, ob mindestens ein zulässiger Raumbezug bei INSPIRE-interoperable Datensätzen vorhanden ist.",
        defaultActive: true,
        category: "INSPIRE relevant",
        events: [],
        publishEvent: null,

        run: function () {
            var self = this;
            // only for geo services
            topic.subscribe("/onObjectClassChange", function (msg) {
                // only register if class 1 and if not already registered
                if (msg.objClass === "Class1") {
                    if (self.events.length === 0) {
                        // delayed register to perform other unregister functions
                        setTimeout(function () { self.register() });
                    }
                } else {
                    self.unregister();
                }
            });
        },

        register: function () {
            var self = this;
            var inspireRelevantWidget = registry.byId("isInspireRelevant");
            this.handleInspireChange(inspireRelevantWidget.checked);

            this.events.push(
                on(inspireRelevantWidget, "Change", function (isChecked) {
                    self.handleInspireChange(isChecked);
                })
            );
        },

        handleInspireChange: function (isChecked, self) {
            if (isChecked) {
                if (this.publishEvent === null) {
                    this.publishEvent = this.handlePublishValidation();
                }
            } else {
                utils.removeEvents([this.publishEvent]);
                this.publishEvent = null;
            }
        },

        handlePublishValidation: function() {
            return topic.subscribe("/onBeforeObjectPublish", function (/*Array*/ notPublishableIDs) {
                var isConform = registry.byId("isInspireConform").checked;
                if (isConform) {
                    // check that an INSPIRE CRS was added
                    var hasInspireCrs = UtilGrid.getTableData("ref1SpatialSystem")
                        .filter(function (item) {
                            return item.title.toLowerCase().indexOf("epsg") !== -1;
                        });

                    if (hasInspireCrs.length === 0) {
                        notPublishableIDs.push(["ref1SpatialSystem", message.get("validation.spatial.system.inspire.missing")]);
                    }
                }

            });
        },

        unregister: function () {
            utils.removeEvents(this.events);
            utils.removeEvents([this.publishEvent]);
            this.publishEvent = null;
        }
    })();
});
