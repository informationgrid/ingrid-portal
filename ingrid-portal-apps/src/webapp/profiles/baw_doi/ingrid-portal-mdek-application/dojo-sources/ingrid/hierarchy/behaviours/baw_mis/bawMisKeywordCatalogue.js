/*
 * **************************************************-
 * InGrid Portal MDEK Application
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
    "dijit/registry",
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/message"
], function(registry, array, declare, lang, Editors, Formatters, dirty, creator, message) {

    return declare(null, {
        title: "Schlagwortkatalog-2012",
        description: "BAW-Schlagwortkatalog (2012)",
        defaultActive: true,
        category: "BAW-MIS",
        run: function() {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            var id = "bawKeywordCatalogueTable";
            var structure = [
                {
                    field: "bawKeywordCatalogueEntry",
                    name: message.get("ui.obj.baw.keyword.catalogue.row.title"),
                    editable: true,
                    type: Editors.SelectboxEditor,
                    options: [],
                    values: [],
                    listId: 3950005,
                    isMandatory: true,
                    formatter: lang.partial(Formatters.SyslistCellFormatter, 3950005),
                    partialSearch: false,
                    style: "width: auto"
                }
            ];
            creator.createDomDataGrid({
                id: id,
                name: message.get("ui.obj.baw.keyword.catalogue.table.title"),
                help: message.get("ui.obj.baw.keyword.catalogue.table.help"),
                style: "width: 100%"
            }, structure, "thesaurus");
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
            return registry.byId(id).promiseInit;
        }

    })();
});

