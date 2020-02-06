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
], function(declare, array, domClass, on, string, topic, registry, message, UtilSyslist, UtilGrid, utils) {

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
                    // delayed register to perform other unregister functions
                    setTimeout(function () { self.register() });
                } else {
                    self.unregister();
                }
            });
        },

        register: function() {
            var self = this;
            var inspireRelevantWidget = registry.byId("isInspireRelevant");

            this.handleInspireChange(inspireRelevantWidget.checked);

            this.events.push(
                on(inspireRelevantWidget, "Click", function () {
                    var checkboxContext = this;

                    if (checkboxContext.checked) {
                        utils.showConfirmDialog(utils.inspireConformityHint, utils.COOKIE_HIDE_INSPIRE_CONFORMITY_HINT).then(function () {
                            utils.addConformity(true, self.specificationName, "1");
                        }, function () {
                            // toggle checkbox state
                            checkboxContext.set("checked", !checkboxContext.checked);
                        });
                    } else {
                        utils.showConfirmDialog(utils.inspireConformityHintDeleted, utils.COOKIE_HIDE_INSPIRE_CONFORMITY_HINT).then(function () {
                            utils.removeConformity(self.specificationName);
                        }, function () {
                            // toggle checkbox state
                            checkboxContext.set("checked", !checkboxContext.checked);
                        });
                    }
                }),

                on(inspireRelevantWidget, "Change", function(isChecked) {
                    self.handleInspireChange(isChecked);
                })
            );
        },

        handleInspireChange: function (isChecked) {
            var self = this;
            if (isChecked) {
                var missingMessage = string.substitute(message.get("validation.specification.missing.service"), [this.specificationName]);
                this.publishEvent = topic.subscribe("/onBeforeObjectPublish", function (/*Array*/ notPublishableIDs) {
                    var requiredSpecification = UtilGrid.getTableData("extraInfoConformityTable")
                        .filter(function (item) {
                            return item.specification === self.specificationName;
                        });

                    if (requiredSpecification.length === 0) {
                        notPublishableIDs.push(["extraInfoConformityTable", missingMessage]);
                    }

                });
                domClass.add("uiElementN024", "required");
            } else {
                utils.removeEvents([this.publishEvent]);
                domClass.remove("uiElementN024", "required");
            }
        },

        updateToNotConform: function() {
            utils.addConformity(true, this.specificationName, "2");
        },

        unregister: function() {
           utils.removeEvents(this.events);
           utils.removeEvents([this.publishEvent]);
           this.publishEvent = null;
           domClass.remove("uiElementN024", "required");
        }
    })();
});
