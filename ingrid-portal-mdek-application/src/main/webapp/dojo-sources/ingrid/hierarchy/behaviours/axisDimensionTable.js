/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/topic",
    "ingrid/message",
    "ingrid/utils/Grid"
], function (declare, array, lang, topic, message, UtilGrid) {
    return declare(null, {

        title: "Tabelle für Achsen",
        description: "Die Spalten 'Achsenbezeichnung' und 'Elementanzahl' sind verpflichtend",
        category: "Felder",
        defaultActive: true,
        run: function () {
            topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                if (array.some(UtilGrid.getTableData("ref1GridAxisTable"), function(axis) {
                    return (typeof(axis.name) == "undefined" ||
                        axis.name === null ||
                        lang.trim(axis.name + "").length === 0 ||
                        typeof(axis.count) == "undefined" ||
                        axis.count === null ||
                        lang.trim(axis.count + "").length === 0);
                })) {
                    notPublishableIDs.push( ["ref1GridAxisTable", message.get("validation.error.axisdim.required.columns")] );
                }
            });
        }
    })();
});
