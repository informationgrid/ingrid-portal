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
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Grid"
], function (array, declare, topic, registry, message, UtilGrid) {
    return declare(null, {

        title: "Eingabe von Capabilities URL bei darstellenden Geodatendiensten verpflichtend",
        description: "Geodatendienste (Typ: Darstellungsdienst) können nur mit Angabe einer getCapabilities URL veröffentlicht werden",
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
                var grid = UtilGrid.getTable("ref3Operation");
                grid.resetInvalidRows();
                var hasCapabilitiesOperation = false;
                var serviceType = registry.byId("ref3ServiceType").get("value");

                array.forEach(UtilGrid.getTableData("ref3Operation"), function(op, row) {
                    if (op.name === "GetCapabilities")
                        hasCapabilitiesOperation = true;
                });

                // at least one GetCapabilities entry has been entered
                // only check for certain serviceTypes (2==Darstellungsdienste)!
                // see email AW: Operationen zum Pflichtfeld (03.04.2013 14:48)
                if ((serviceType === "2") && !hasCapabilitiesOperation)
                    notPublishableIDs.push( ["ref3Operation", message.get( "validation.error.missing.capabilities.entry" )]);

            }
        }
    })();
});
