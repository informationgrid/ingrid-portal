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
    "dojo/aspect",
    "dijit/registry",
    "ingrid/utils/Grid",
    "ingrid/utils/UI"
], function (declare, aspect, registry, UtilGrid, UtilUI) {
    return declare(null, {

        title: "Objektartenkatalog",
        description: "Wenn in der Tabelle 'Inhalte der Datensammlung/Datenbank' ein Eintrag existiert, wird dieses Feld zum Pflichtfeld.",
        category: "Felder",
        defaultActive: true,
        run: function () {

            // make 'Objektartenkatalog' mandatory on input 'Inhalte der Datensammlung/Datenbank'
            aspect.after(registry.byId("ref5dbContent"), "onDataChanged", function() {
                if (UtilGrid.getTableData("ref5dbContent").length !== 0) {
                    UtilUI.setMandatory("uiElement3109");
                } else {
                    UtilUI.setOptional("uiElement3109");
                }
            });


        }
    })();
});
