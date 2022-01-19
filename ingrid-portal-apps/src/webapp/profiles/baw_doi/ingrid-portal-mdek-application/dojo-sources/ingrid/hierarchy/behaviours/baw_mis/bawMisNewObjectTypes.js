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
    "dojo/_base/declare",
    "dojo/topic",
    "ingrid/utils/Syslist"
], function(declare, topic, UtilSyslist) {

    return declare(null, {
        title: "Objekttypen",
        description: "Angepasste Liste der erlaubten Objekttypen",
        defaultActive: true,
        type: "SYSTEM",
        category: "BAW-MIS",
        run: function() {

            topic.subscribe("/additionalSyslistsLoaded", function() {
                // out of the existing options, keep only Geodatensatz, Geodatendienst and Literatur for new object type
                arr = sysLists[UtilSyslist.listIdObjectClass].filter(function (item) {
                    return item[1] === "1" || item[1] === "2" || item[1] === "3";
                });

                // Define the order in which the options should appear
                var desired_order = [
                    "4", // Projekt
                    "1", // Geodatensatz
                    "5", // Datensammlung
                    "3", // Geodatendienst
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

