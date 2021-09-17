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
                // out of the existing options, keep only Geodatensatz and Geodatendienst for new object type
                // Items are added one by one to ensure the correct order
                arr = [];
                arr.push(sysLists[UtilSyslist.listIdObjectClass].find(function (item) {
                    return item[1] === "1";
                }));
                arr.push(sysLists[UtilSyslist.listIdObjectClass].find(function (item) {
                    return item[1] === "3";
                }));
                sysLists[UtilSyslist.listIdObjectClass] = arr;
            });
        }
    })();
});

