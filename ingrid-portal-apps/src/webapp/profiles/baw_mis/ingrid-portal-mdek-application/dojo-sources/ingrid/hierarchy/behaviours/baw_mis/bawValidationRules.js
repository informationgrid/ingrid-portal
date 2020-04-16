/*
 * **************************************************-
 * InGrid Portal MDEK Application
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
    "dijit/registry",
    "dojo/_base/declare",
    "dojo/topic",
    "ingrid/message",
    "ingrid/utils/Syslist"
], function(registry, declare, topic, message, UtilSyslist) {

    return declare(null, {
        title: "BAW Validierungsregeln",
        description: "Zusätzliche Validierungsregeln für die BAW",
        defaultActive: true,
        category: "BAW-MIS",
        run: function(){

            topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                var roleCodeSyslistId = 505;
                var ownerSyslistKey = 7;
                var ownerSyslistName = UtilSyslist.getSyslistEntryName(roleCodeSyslistId, ownerSyslistKey);

                var addressTableIsValid = true;
                var addressTableId = "generalAddress";
                var addressTable = registry.byId(addressTableId);
                var ownerRows = addressTable.data.filter(function(row) {
                    return row.nameOfRelation === ownerSyslistName;
                });

                if (ownerRows.length === 0) {
                    addressTableIsValid = false;
                }

                var bawOrganisation = "Bundesanstalt für Wasserbau";
                var bawRow = ownerRows.find(function(row) {
                    return row.organisation === bawOrganisation;
                });

                if (!bawRow) addressTableIsValid = false;

                if (!addressTableIsValid) {
                    notPublishableIDs.push([addressTableId, message.get("validation.baw.address.role.pointOfContact")]);
                }
            });
        }
    })();
});

