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
    "ingrid/message",
    "ingrid/utils/Syslist"
], function(declare, topic, message, Syslist) {

    return declare(null, {
        title: "Plattform-Objektklasse", // TODO localisation
        description: "Objektklasse für Plattform Metadaten",
        defaultActive: true,
        category: "HZG",
        type: 'SYSTEM',

        run: function() {
            this._addObjectClassForPlatform();
        },

        _addObjectClassForPlatform: function() {
            topic.subscribe("/additionalSyslistsLoaded", function() {
                var objectClasses = sysLists[Syslist.listIdObjectClass];
                if (objectClasses) {
                    objectClasses.push([message.get("ui.obj.type.platform"), "20", "N", ""]);
                } else {
                    alert("Syslist not found: " + Syslist.listIdObjectClass);
                }
            });
        }

    })();
});

