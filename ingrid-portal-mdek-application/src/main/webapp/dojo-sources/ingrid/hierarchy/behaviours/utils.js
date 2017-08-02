/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
    "ingrid/utils/Grid"
], function(declare, UtilGrid) {

    return declare(null, {

        addConformity: function(name, level) {
            console.log("Add conformity");
            var conformityData = UtilGrid.getTableData("extraInfoConformityTable");

            conformityData = conformityData.filter(function(item) {
                return item.specification !== name;
            });
            conformityData.push({
                specification: name,
                level: level
            });
            UtilGrid.setTableData("extraInfoConformityTable", conformityData);
        },

        removeConformity: function(name) {
            var conformityData = UtilGrid.getTableData("extraInfoConformityTable");

            conformityData = conformityData.filter(function(item) {
                return item.specification !== name;
            });
            UtilGrid.setTableData("extraInfoConformityTable", conformityData);
        }

    })();
});