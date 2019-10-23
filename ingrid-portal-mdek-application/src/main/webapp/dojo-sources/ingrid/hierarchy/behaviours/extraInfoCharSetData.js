/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
    "ingrid/utils/UI"
], function (declare, topic, UtilUI) {
    return declare(null, {

        title: "Zeichensatz des Datensatzes",
        description: "Ist optional in Geodatensätzen, ansonsten versteckt.",
        category: "Felder",
        defaultActive: true,
        run: function () {
            topic.subscribe("/onObjectClassChange", function (msg) {
                // only in 'Geo-Information/Karte', then optional
                if (msg.objClass === "Class1") {
                    UtilUI.setOptional("uiElement5043");
                } else {
                    UtilUI.setHide("uiElement5043");
                }
            });
        }
    })();
});
