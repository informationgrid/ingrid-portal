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
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/aspect",
    "dojo/on",
    "dojo/query",
    "dojo/dom-class",
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/General",
    "ingrid/utils/UI",
    "ingrid/utils/Grid"
], function (array, declare, aspect, on, query, domClass, topic, registry, message, UtilGeneral, UtilUI, UtilGrid) {
    return declare(null, {

        title: "Datenqualität Sektion",
        description: "Diese wird nur für Geodatensätze angezeigt. Die Tabellen müssen 'Art der Messung' und 'Wert' gesetzt haben.",
        defaultActive: true,
        run: function () {
            var self = this;

            topic.subscribe("/onObjectClassChange", function (msg) {
                if (msg.objClass === "Class1") {
                    UtilUI.setShow("refClass1DQ");
                } else {
                    UtilUI.setHide("refClass1DQ");
                }
            });

            topic.subscribe("/onBeforeObjectPublish", function(/*Array*/ notPublishableIDs) {
               self.dqTablesPublishable(notPublishableIDs);
            });
        },
        dqTablesPublishable: function(notPublishableIDs) {
            var objClass = registry.byId("objectClass").get("value").substr(5, 1);
            if (objClass === "1") {
                var dqUiTableElements = query("#ref1ContentDQTables span:not(.hide) .ui-widget", "contentFrameBodyObject").map(function(item) {
                    return item.id;
                });
                // Check dq rows whether complete !
                array.forEach(dqUiTableElements, function(dqTableId) {
                    var dqRows = UtilGrid.getTableData(dqTableId);
                    array.forEach(dqRows, function(dqRow) {
                        if (!UtilGeneral.hasValue(dqRow.nameOfMeasure) || !UtilGeneral.hasValue(dqRow.resultValue)) {
                            console.debug("NameOfMeasure + ResultValue needs to be filled.");
                            notPublishableIDs.push( [dqTableId, message.get("validation.error.dq.table")] );
                        }
                    });
                });
            }
        }
    })();
});
