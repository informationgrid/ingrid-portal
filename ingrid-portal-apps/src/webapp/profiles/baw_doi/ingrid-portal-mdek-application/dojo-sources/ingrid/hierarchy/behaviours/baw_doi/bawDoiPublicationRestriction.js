/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
    "dijit/registry",
    "dojo/_base/declare",
    "dojo/topic",
    "ingrid/utils/Security"
    ], function (registry, declare, topic, UtilSecurity) {

    return declare(null, {
        title: "Eingeschränkte-Veröffentlichungsfunktion",
        description: "Veröffentlichungen der Objekte nur dem Katalogadministrator erlauben",
        defaultActive: true,
        category: "BAW-Datenrepository",

        run: function () {
            topic.subscribe("/onObjectClassChange", function() {
                if (UtilSecurity.currentUser.role !== 1) {
                    var publishBtn = registry.byId('toolbarBtnFinalSave');
                    if (publishBtn) {
                        publishBtn.set('disabled', true);
                    }
                }
            });
        }
    })();
});

