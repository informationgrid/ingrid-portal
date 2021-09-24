/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
    "dojo/topic"
], function(declare, topic) {

    return declare(null, {
        title: "Codelistenanpassungen",
        description: "Angepasste InGrid-Codelisten und BAW-eigene Codelisten",
        defaultActive: true,
        type: "SYSTEM",
        category: "BAW-MIS",
        run: function() {

            // load custom codelists
            topic.subscribe("/collectAdditionalSyslistsToLoad", function(ids) {
                ids.push(3950000, 3950001, 3950002, 3950003, 3950004, 3950005, 3950010);
            });

            topic.subscribe("/additionalSyslistsLoaded", function() {

                // Add vertical CRSes to the list of horizontal CRSes
                var crsSyslist = sysLists[100];
                if (crsSyslist) {
                    crsSyslist.push([ "EPSG 7837: DHHN2016 Höhe", "7837", "N", null ]);
                    crsSyslist.push([ "EPSG 5783: DHHN92 Höhe", "5783", "N", null ]);
                    crsSyslist.push([ "EPSG 7699: DHHN12 Höhe", "7699", "N", null ]);
                }
            });
        }
    })();
});
