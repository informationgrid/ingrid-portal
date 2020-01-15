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
    "dojo/dom-construct",
    "dojo/query",
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Syslist"
], function(declare, array, construct, query, topic, registry, message, UtilSyslist) {

    return declare(null, {
        title: "Dokumenten Typen und Verhalten",
        description: "Definition der Dokumententypen und Beeinflussing der Erstellung",
        defaultActive: true,
        type: "SYSTEM",
        category: "mcloud",
        // params: [{id: "loadCodelists", label: "Zu ladende Codelisten", "default": "8002"} ],
        run: function() {

            this.addIconClasses();

            var self = this;
            topic.subscribe("/afterInitDialog/ChooseWizard", function(data) {
                var uvpType = null;

                // remove all assistants
                data.assistants.splice(0, data.assistants.length);

                mcloudType = array.filter(data.types, function(t) { return t[1] === "20"; });
                data.types.splice(0, data.types.length);
                data.types.push(mcloudType[0]);

            });

            // load custom syslists
            topic.subscribe("/collectAdditionalSyslistsToLoad", function(ids) {

                ids.push(8002);

            });

            // modify loaded syslists after they have been loaded
            topic.subscribe("/additionalSyslistsLoaded", function() {

                // document types
                sysLists[8002].push(["Dokument", "20", "N", ""]);
                // address types (only "Herausgeber")
                sysLists[505] = sysLists[505].filter(function(item) {
                    return item[1] === "10";
                });
                // time ref types
                sysLists[502] = sysLists[502].filter(function(item) {
                    return item[1] !== "2";
                });
                // remove communication items "Telex" and "X.400"
                sysLists[4430] = sysLists[4430].filter(function(item) {
                    return item[1] !== "5" && item[1] !== "6";
                });

            });

            // get availbale object classes from codelist 8001
            UtilSyslist.listIdObjectClass = 8002;

        },

        addIconClasses: function() {
            query("head")[0].appendChild(
                // construct.toDom('<link rel="stylesheet" type="text/css" href="lightbox_stylesheet.css">')
                construct.toDom(
                    '<style type="text/css">' +
                    '.TreeIconClass20, .TreeIconClass20_V {background-position: -496px;}' +
                    '.TreeIconClass20_B {background-position: -512px;}' +
                    '.TreeIconClass20_BV {background-position: -528px;}' +
                    '</style>')
            );
        }
    })();
});
