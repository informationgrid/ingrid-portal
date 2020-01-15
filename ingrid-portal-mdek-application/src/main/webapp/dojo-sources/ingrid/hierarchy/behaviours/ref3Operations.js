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
    "dojo/_base/lang",
    "dojo/aspect",
    "dojo/on",
    "dojo/dom-class",
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/General"
], function (array, declare, lang, aspect, on, domClass, topic, registry, message, UtilGeneral) {
    return declare(null, {

        title: "Operationen",
        description: "Mindestens ein GetCapabilities Eintrag muss für Darstellungsdienste vorhanden sein und der Name der Operation muss gültig sein.",
        category: "Felder",
        defaultActive: true,
        run: function () {
            var self = this;

            topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                self.ref3OperationPublishable(notPublishableIDs);
            });
        },
        ref3OperationPublishable: function(notPublishableIDs) {
            var objClass = registry.byId("objectClass").get("value").substr(5, 1);
            if (objClass === "3") {
                // Check if the operation table contains valid input (name has to be set)
                // NOTICE: Name may be reset to "" if serviceType is changed !!!
                var grid = UtilGrid.getTable("ref3Operation");
                grid.resetInvalidRows();
                var newInvalidRows = [];
                var hasCapabilitiesOperation = false;
                var serviceType = registry.byId("ref3ServiceType").get("value");

                array.forEach(UtilGrid.getTableData("ref3Operation"), function(op, row) {
                    if (!UtilGeneral.hasValue(op.name) || !UtilGeneral.hasValue(op.addressList) || !UtilGeneral.hasValue(op.platform)) {
                        newInvalidRows.push(row);
                    }
                    if (op.name === "GetCapabilities")
                        hasCapabilitiesOperation = true;
                });
                if (newInvalidRows.length > 0) {
                    grid.setInvalidRows(newInvalidRows);
                    console.debug("All entries in the operation table must have a valid name.");
                    notPublishableIDs.push( ["ref3Operation", message.get( "validation.error.invalid.operation.name" )] );
                }

                // at least one GetCapabilities entry has been entered
                // only check for certain serviceTypes (2==Darstellungsdienste)!
                // see email AW: Operationen zum Pflichtfeld (03.04.2013 14:48)
                if ((serviceType === "2") && !hasCapabilitiesOperation)
                    notPublishableIDs.push( ["ref3Operation", message.get( "validation.error.missing.capabilities.entry" )]);

            }
        }
    })();
});
