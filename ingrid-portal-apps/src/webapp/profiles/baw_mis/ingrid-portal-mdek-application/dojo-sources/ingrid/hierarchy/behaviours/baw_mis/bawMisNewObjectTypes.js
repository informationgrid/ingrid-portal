/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
    "ingrid/utils/Catalog",
    "ingrid/utils/Syslist"
], function(declare, topic, UtilCatalog, UtilSyslist) {

    return declare(null, {
        title: "Objekttypen",
        description: "Angepasste Liste der erlaubten Objekttypen",
        defaultActive: true,
        type: "SYSTEM",
        category: "BAW-MIS",
        run: function() {

            topic.subscribe("/additionalSyslistsLoaded", function() {
                // out of the existing options, keep only Geodatensatz, Geodatendienst, Literatur and Informationssystem for new object type
                var arr = sysLists[UtilSyslist.listIdObjectClass].filter(function (item) {
                    // rename Informationssystem to Software
                    if (item[1] === "6") item[0] = "Software";
                    // Rename project as "Projekt / Auftrag"
                    if (item[1] === "4") item[0] = "Projekt / Auftrag";
                    
                    return item[1] === "1" || item[1] === "2" || item[1] === "3" || item[1] === "4" || item[1] === "6";
                });

                // Define the order in which the options should appear
                var desired_order = [
                    "1", // Geodatensatz
                    "5", // Datensammlung
                    "3", // Geodatendienst
                    "4", // Projekt
                    "2", // Literatur
                    "6", // Informationssystem
                    "0"  // Fachaufgabe
                ];

                arr.sort(function (a, b) {
                    var idxa = desired_order.indexOf(a[1]);
                    var idxb = desired_order.indexOf(b[1]);

                    if (idxa > idxb) return 1;
                    else if (idxa < idxb) return -1;
                    else return 0;
                });

                sysLists[UtilSyslist.listIdObjectClass] = arr;
            });
        }
    })();
});

