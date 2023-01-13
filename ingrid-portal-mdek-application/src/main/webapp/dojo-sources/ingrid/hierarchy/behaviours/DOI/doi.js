/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
define(["dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/dom-construct",
    "dijit/form/Button",
    "dijit/registry",
    "ingrid/dialog",
    "ingrid/message",
    "ingrid/layoutCreator",
    "ingrid/hierarchy/dirty"
], function (declare, array, lang, construct, Button, registry, dialog, message, creator, dirty) {

    return declare(null, {
        title: "Unterstützung der Erfassung von DOIs/Export im DataCite Format",
        description: "Ermöglicht die Erfassung eines DOI für einen Datensatz. Der Datensatz kann in das DataCite Format exportiert werden.",
        defaultActive: false,
        run: function () {

            this.createFields();

        },

        createFields: function () {
            var self = this;
            var newFieldsToDirtyCheck = [];
            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;

            /*
             * Export Button
             */
            var exportButton = new Button({
                label: message.get("doi.button.exportDataCite"),
                "class": "right show",
                onClick: function () {
                    // *****************
                    // for quick development
                    // delete require.modules["ingrid/hierarchy/behaviours/DOI/doiExport"];
                    // *****************
                    if (self.hasUnsavedChanges()) {
                        dialog.show(message.get("dialog.general.info"), message.get("doi.hint.save"), dialog.INFO);
                    } else {
                        dialog.showPage(message.get("doi.button.exportDataCite"), "dialogs/mdek_show_doi_export.jsp", 755, 600, true, {});
                    }
                }
            });
            
            // create div element to insert new field at correct place
            // var container = this.createOutlinedWrapper(rubric);
            creator.addToSection("links",
                creator.addOutlinedSection("doiDataCite", message.get("doi.title"), null, [
                    creator.createDomTextbox({
                        id: "doiId",
                        style: "width: 50%",
                        name: message.get("doi.id"),
                        help: message.get("doi.id.helpMessage"),
                        visible: "show"
                    }),
                    creator.createDomSelectBox({
                        id: "doiType",
                        name: message.get("doi.type"),
                        help: message.get("doi.type.helpMessage"),
                        listEntries: [
                            {id: "1", value: "Audiovisual"},
                            {id: "2", value: "Collection"},
                            {id: "3", value: "DataPaper"},
                            {id: "4", value: "Dataset"},
                            {id: "5", value: "Event"},
                            {id: "6", value: "Image"},
                            {id: "7", value: "InteractiveResource"},
                            {id: "8", value: "Model"},
                            {id: "9", value: "PhysicalObject"},
                            {id: "10", value: "Service"},
                            {id: "11", value: "Software"},
                            {id: "12", value: "Sound"},
                            {id: "13", value: "Text"},
                            {id: "14", value: "Workflow"}
                        ],
                        isExtendable: true,
                        visible: "show",
                        style: "width:50%"
                    }),
                    exportButton.domNode
                ], {disableHelp: true})
            );

            /*
             * DOI - Field
             */
            var id = "doiId";
            newFieldsToDirtyCheck.push(id);
            var doiField = registry.byId(id);
            additionalFields.push(doiField);

            this.addValidationToDOI(doiField);

            /*
             * Type
             */
            id = "doiType";
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
        },

        hasUnsavedChanges: function() {
            return dirty.dirtyFlag;
        },

        addValidationToDOI: function (field) {
            field.invalidMessage = message.get("doi.id.validation");
            var doiRegExp = new RegExp('(?:^(10[.][0-9]{4,}(?:[.][0-9]+)*/(?:(?![%"#? ])\\S)+)$)');

            // see: https://www.crossref.org/blog/dois-and-matching-regular-expressions/
            // and: https://github.com/regexhq/doi-regex
            field.validator = function () {
                var value = this.get("value");
                return value.trim().length === 0 || doiRegExp.test(value);
            };
        }

    })();
});
