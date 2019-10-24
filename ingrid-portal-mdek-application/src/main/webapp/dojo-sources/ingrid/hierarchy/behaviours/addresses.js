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
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/aspect",
    "dojo/on",
    "dojo/query",
    "dojo/dom-class",
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Grid",
    "ingrid/utils/UI"
], function (array, declare, lang, aspect, on, query, domClass, topic, registry, message, UtilGrid, UtilUI) {
    return declare(null, {

        title: "Adressen",
        description: "Die Tabelle muss mindestens einen Eintrag enthalten und diese müssen vollständig sein. Beim Veröffentlichen müssen die Referenzen auch gültig sein.",
        category: "Felder",
        defaultActive: true,
        run: function () {
            var self = this;
            this.addressValidation();

            topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                self.generalAddressPublishable(notPublishableIDs);
            });
        },
        addressValidation: function() {
            aspect.after(UtilGrid.getTable("generalAddress"), "onDataChanged", function(result, params) {
                var msg = params[0];
                if (msg.type != "deleted")
                    return;

                // just any address needed (not "Verwalter" anymore), see INGRID32-46
                // if it's the last address then revert store state since
                // at least one address must exist
                var anyAddress = UtilGrid.getTableData("generalAddress").length > 0;
                if (!anyAddress) {
                    array.forEach(msg.items, function(itemRow) {
                        UtilGrid.addTableDataRow("generalAddress", itemRow);
                        // show tooltip
                        var toolTip = message.get("validation.addressInfoRequired");
                        UtilUI.showToolTip("generalAddress", toolTip);
                    });
                }
            });

            // only complete entries are allowed
            UtilGrid.getTable("generalAddress").validate = function(forPublish) {
                var result = true;
                var data = this.getData();
                var cell;
                array.forEach(data, function(item, i) {
                    if (typeof(item.uuid) == "undefined") {
                        this.scrollRowIntoView(i);
                        cell = query(".slick-row[row$=" + i + "] .c2", "generalAddress")[0];
                        domClass.add(cell, "importantBackground");
                        result = false;
                    }
                    if (forPublish) {
                        if (typeof(item.nameOfRelation) == "undefined" || item.nameOfRelation == "") {
                            this.scrollRowIntoView(i);
                            cell = query(".slick-row[row$=" + i + "] .c0", "generalAddress")[0];
                            domClass.add(cell, "importantBackground");
                            result = false;
                        }
                    }
                }, this);
                return result;
            };

        },
        generalAddressPublishable: function(notPublishableIDs) {
            var addressData = UtilGrid.getTableData("generalAddress");
            // Check if all entries in the address table have valid reference types
            if (array.some(addressData, function(addressRef) {
                return (typeof(addressRef.uuid) == "undefined" || addressRef.nameOfRelation === null || lang.trim(addressRef.nameOfRelation + "").length === 0);
            })) {
                console.debug("All entries in the address table must have valid references.");
                registry.byId("generalAddress").invalidMessage = "All entries in the address table must have valid references.";
                notPublishableIDs.push( ["generalAddress", message.get("validation.error.missing.address.user")] );
            }

            // Check if at least one entry exists
            // just any address needed (not "Verwalter" anymore), see INGRID32-46
            var anyAddress = addressData.length > 0;
            if (!anyAddress) {
                console.debug("At least one referenced address has to be set");
                registry.byId("generalAddress").invalidMessage = message.get("validation.error.addressType");
                notPublishableIDs.push( ["generalAddress", message.get("validation.error.missing.address")] );
            }
        }
    })();
});
