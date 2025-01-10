/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/topic",
    "ingrid/message"
], function (array, declare, lang, topic, message) {
    return declare(null, {

        title: "Vektorformat",
        description: "Validierung, dass beim Ausfüllen der Spalte Elementenanzahl auch die Spalte Geometrietyp ausgefüllt ist",
        category: "Felder",
        defaultActive: true,
        run: function () {

            var self = this;
            this.publishEvent = topic.subscribe("/onBeforeObjectPublish", function (/*Array*/ notPublishableIDs) {

                if (array.some(UtilGrid.getTableData("ref1VFormatDetails"), function (vectorItem) {
                    return !self.hasNoValue(vectorItem.numElements) && self.hasNoValue(vectorItem.geometryType);
                })) {
                    notPublishableIDs.push(["ref1VFormatDetails", message.get("validation.error.geometry.type")]);
                }

            });

        },

        hasNoValue: function(value) {
            return typeof (value) == "undefined" ||
                value === null ||
                lang.trim(value + "").length === 0
        }
    })();
});
