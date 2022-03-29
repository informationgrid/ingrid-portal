/*
 * **************************************************-
 * InGrid Portal MDEK Application
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
    "dijit/registry",
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/dom",
    "dojo/dom-construct",
    "dojo/topic",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/message",
    "ingrid/utils/General"
], function(registry, array, declare, lang, dom, construct, topic, Editors, Formatters, dirty, creator, message, UtilGeneral) {

    return declare(null, {
        title: "Literatur und Querverweise",
        description: "Literatur und Querverweise",
        defaultActive: true,
        category: "BAW-MIS",
        run: function() {
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            var authorsGridId = this._initObjClass2AuthorsTable();
            newFieldsToDirtyCheck.push(authorsGridId);
            additionalFields.push(registry.byId(authorsGridId));

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));


            return registry.byId(authorsGridId).promiseInit;
        },

        _initObjClass2AuthorsTable: function() {
            var id = "bawLiteratureAuthorsTable";
            var structure = [
                {
                    field: "authorGivenName",
                    name: message.get("ui.obj.literature.author.table.column.given.name"),
                    type: Editors.TextCellEditor,
                    editable: true,
                    isMandatory: false,
                    width: "230px"
                },
                {
                    field: "authorFamilyName",
                    name: message.get("ui.obj.literature.author.table.column.family.name"),
                    type: Editors.TextCellEditor,
                    editable: true,
                    isMandatory: false,
                    width: "230px"
                },
                {
                    field: "authorOrganisation",
                    name: message.get("ui.obj.literature.author.table.column.organisation"),
                    type: Editors.TextCellEditor,
                    editable: true,
                    isMandatory: false,
                    width: "auto"
                }
            ];
            var authorsGrid = creator.createDomDataGrid({
                id: id,
                name: message.get("ui.obj.literature.author.table.title"),
                help: message.get("ui.obj.literature.author.table.help"),
                style: "width: 100%"
            }, structure, "refClass2");

            var node = dom.byId("uiElement3350").parentElement;
            construct.place(authorsGrid, node, 'before');

            // Validation rules
            topic.subscribe("/onBeforeObjectPublish", function (notPublishableIDs) {
                var authorsData = registry.byId(id).data;

                var hasInvalidRows = array.some(authorsData, function (row) {
                    var hasGivenName = UtilGeneral.hasValue(row.authorGivenName);
                    var hasFamilyName = UtilGeneral.hasValue(row.authorFamilyName);
                    var hasOrg = UtilGeneral.hasValue(row.authorOrganisation);

                    return (hasGivenName && !hasFamilyName)
                        || (!hasGivenName && hasFamilyName);
                })

                if (hasInvalidRows) {
                    notPublishableIDs.push([id, message.get("validation.baw.literature.authors")]);
                }
            });

            return id;
        }

    })();
});

