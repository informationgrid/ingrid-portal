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
define(["dojo/_base/declare",
    "dojo/_base/array",
    "dojo/topic"
], function(declare, array, topic) {

    return declare(null, {
        title: "Dokumenten Typen",
        description: "Definition der Dokumententypen",
        defaultActive: true,
        type: "SYSTEM",
        category: "Nokis",
        run: function() {

            var self = this;
            topic.subscribe("/afterInitDialog/ChooseWizard", function(data) {
                // remove all assistants
                data.assistants.splice(0, data.assistants.length);

                var nokisTypes = array.filter(data.types, function(t) { return t[1] === "1" || t[1] === "3"; });
                data.types.splice(0, data.types.length);
                array.forEach(nokisTypes, function(type) {
                    data.types.push(type);
                });

            });

            // load custom syslists
            topic.subscribe("/collectAdditionalSyslistsToLoad", function(ids) {

                // ids.push(8002);

            });

        }

    })();
});
