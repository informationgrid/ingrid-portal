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
    "dojo/_base/array",
    "dojo/_base/declare",
    "ingrid/utils/Grid",
    "ingrid/utils/Syslist"
], function(array, declare, UtilGrid, UtilSyslist) {

    return declare(null, {

        addConformity: function(isFromInspireList, name, level) {
            console.log("Add conformity");
            var listId = 6005;
            var conformityData = UtilGrid.getTableData("extraInfoConformityTable");
            var publicationDate = UtilSyslist.getSyslistEntryData(listId, name);

            conformityData = conformityData.filter(function(item) {
                return item.specification !== name;
            });
            conformityData.push({
                isInspire: true,
                specification: name,
                level: level,
                publicationDate: new Date(publicationDate)
            });
            UtilGrid.setTableData("extraInfoConformityTable", conformityData);
        },

        removeConformity: function(name) {
            var conformityData = UtilGrid.getTableData("extraInfoConformityTable");

            conformityData = conformityData.filter(function(item) {
                return item.specification !== name;
            });
            UtilGrid.setTableData("extraInfoConformityTable", conformityData);
        },

        removeEvents: function(events) {
            array.forEach(events, function(event) {
                if (event !== null) {
                    event.remove();
                }
            });
            // empty array
            events.splice(null);
        }

    })();
});
