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
        title: "Eingeschränkte-Schreibrechte-für-Felder",
        description: "Nur LiteraturAdmins das Bearbeiten von einigen Feldern erlauben.",
        defaultActive: true,
        category: "BAW-MIS",

        run: function () {
            topic.subscribe("/onObjectClassChange", function(data) {

                var isGeodata = data.objClass === "Class1";
                var isLiterature = data.objClass === "Class2";

                var doiIdField = registry.byId('doiId');
                var doiTypeField = registry.byId('doiType');
                var publishArea = registry.byId("extraInfoPublishArea");

                var currentGroups = UtilSecurity.currentGroups;

                // Katadmin and users explicitly added to group 'LiteraturAdmin'
                var isLiteratureAdmin = UtilSecurity.currentUser.role === 1 || array.some(currentGroups, function (item) {
                    return item.name === "LiteraturAdmin";
                });

                if (isGeodata && !isLiteratureAdmin) {
                    if (doiIdField) {
                        doiIdField.set('disabled', true);
                        doiTypeField.set('disabled', true);
                    }
                } else {
                    if (doiTypeField) {
                        doiIdField.set('disabled', false);
                        doiTypeField.set('disabled', false);
                    }
                }

                if (isLiterature && !isLiteratureAdmin) {
                    if (publishArea) {
                        publishArea.set('disabled', true);
                    }
                } else {
                    if (publishArea) {
                        publishArea.set('disabled', false);
                    }
                }
            });
        }
    })();
});

