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
define(["dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/dom-class",
    "dijit/registry",
    "ingrid/layoutCreator",
    "ingrid/hierarchy/dirty",
    "ingrid/message",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters"
], function (declare, array, lang, domClass, registry, layoutCreator, dirty, message, Editors, Formatters) {

    return declare(null, {
        title: "Wortliste",
        description: "Auswahl von Wörtern, die im NOKIS System Verwendung finden.",
        defaultActive: true,
        category: "Nokis",
        run: function () {

            this.createFields();

        },

        createFields: function () {

            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            var id = "nokisThesaurus";
            var thesaurusStructure = [
                {
                    field: 'name',
                    editable: true,
                    type: Editors.SelectboxEditor,
                    options: [],
                    values: [],
                    listId: 7200,
                    formatter: lang.partial(Formatters.SyslistCellFormatter, 7200),
                    partialSearch: true
                }
            ];

            var table = layoutCreator.createDomDataGrid({
                id: id,
                name: message.get("nokis.form.thesaurus"),
                help: message.get("nokis.form.thesaurus.help"),
                isMandatory: false,
                disableHelp: true,
                visible: "show",
                style: "width: 100%"
            }, thesaurusStructure, "thesaurus");
            domClass.add(table, "hideTableHeader");
            // construct.place(table, "ref1VFormat", "after");

            newFieldsToDirtyCheck.push(id);
            var tableWidget = registry.byId(id);
            tableWidget.reinitLastColumn(true);
            additionalFields.push(tableWidget);

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

        }

    })();
})
