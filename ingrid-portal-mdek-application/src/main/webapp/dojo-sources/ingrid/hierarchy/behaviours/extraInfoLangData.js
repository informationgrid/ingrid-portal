/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
    "ingrid/utils/UI"
], function (declare, topic, UtilUI) {
    return declare(null, {

        title: "Sprache der Ressource",
        description: "In Geodatendiensten versteckt, für Fachbezug, Projekt und Informationssystem optional, ansonten Pflichtfeld.",
        category: "Felder",
        defaultActive: true,
        run: function () {
            topic.subscribe("/onObjectClassChange", function (msg) {
                if (msg.objClass === "Class3") {
                    // hide in 'Geodatendienst'
                    UtilUI.setHide("uiElement5042");
                } else if (msg.objClass === "Class0" || msg.objClass === "Class4" || msg.objClass === "Class6") {
                    // optional in classes 'Organisationenseinheit' + 'Vorhaben' + 'Informationssystem'
                    UtilUI.setOptional("uiElement5042");
                } else {
                    UtilUI.setMandatory("uiElement5042");
                }
            });
        }
    })();
});
