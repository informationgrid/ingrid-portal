/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/topic",
    "ingrid/utils/Security"
    ], function (registry, array, declare, topic, UtilSecurity) {

    return declare(null, {
        title: "Eingeschränkte-Rechte-für-DOI-Felder",
        description: "Nur DOI-Admins das Bearbeiten von DOI-Feldern erlauben.",
        defaultActive: true,
        category: "BAW-MIS",

        run: function () {
            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass !== "Class1") {
                    // do nothing if the object class isn't geodataset
                    return;
                }

                var currentGroups = UtilSecurity.currentGroups;

                // Katadmin and users explicitly added to group 'DOI-Admin'
                var isDoiAdmin = UtilSecurity.currentUser.role === 1 || array.some(currentGroups, function (item) {
                    return item.name === "DOI-Admin";
                });

                if (!isDoiAdmin) {
                    var doiIdField = registry.byId('doiId');
                    var doiTypeField = registry.byId('doiType');
                    if (doiIdField) {
                        doiIdField.set('disabled', true);
                    }
                    if (doiTypeField) {
                        doiTypeField.set('disabled', true);
                    }
                }
            });
        }
    })();
});

