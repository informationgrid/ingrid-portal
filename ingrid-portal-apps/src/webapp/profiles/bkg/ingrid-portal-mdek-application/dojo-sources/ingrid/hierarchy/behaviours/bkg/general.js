/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

    // issue: 556
    return declare(null, {
        title: "Allgemein",
        description: "Lädt benötigte Codelisten beim Start",
        defaultActive: true,
        type: "SYSTEM",
        category: "BKG",
        run: function() {

            // load custom syslists
            topic.subscribe("/collectAdditionalSyslistsToLoad", function(ids) {
                ids.push(10001, 10002, 10003, 10004, 10005, 10006);
            });
        }
    })();
});