/*
 * **************************************************-
 * InGrid Portal MDEK Application
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
    "dojo/_base/declare",
    "dojo/topic",
    "ingrid/utils/Syslist"
], function(declare, topic, UtilSyslist) {

    return declare(null, {
        title: "Systemanpassungen",
        description: "Anpassungen für die BAW, die beim erstem Laden des IGE laufen.",
        defaultActive: true,
        type: "SYSTEM",
        category: "BAW-MIS",
        run: function() {

            // load custom syslists
            topic.subscribe("/collectAdditionalSyslistsToLoad", function(ids) {
                ids.push(3950000, 3950001, 3950002, 3950003, 3950004, 3950005, 3950010);
            });

            topic.subscribe("/additionalSyslistsLoaded", function() {
                // Keep only Geodatensatz and Geodatendienst for new object type
                sysLists[UtilSyslist.listIdObjectClass] = sysLists[UtilSyslist.listIdObjectClass].filter(function(item) {
                    return item[1] === "1" || item[1] === "3";
                });

            });
        }
    })();
});

