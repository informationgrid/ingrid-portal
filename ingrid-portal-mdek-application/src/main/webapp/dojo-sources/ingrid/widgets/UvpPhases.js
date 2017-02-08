define([
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/dom-construct",
    "dojo/dom-class",
    "dojo/query",
    "dojo/topic",
    "dijit/_WidgetBase",
    "dijit/registry",
    "dijit/form/Button",
    "dijit/form/DateTextBox",
    "dijit/form/_FormValueWidget",
    "ingrid/layoutCreator",
    "ingrid/dialog",
    "ingrid/message",
    "ingrid/IgeEvents",
    "ingrid/grid/CustomGrid",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "dojo/NodeList-traverse"
], function (declare, array, lang, construct, domClass, query, topic, _WidgetBase, registry, Button, DateTextBox, _FormValueWidget,
    creator, dialog, message, IgeEvents, CustomGrid, Editors, Formatters) {

        return declare("UVPPhases", [_WidgetBase], {

            // phases consists of 0 or n containers, which itself contain additional fields
            // e.g.: [ { key: "öffentl. Auslegung", fields: [addField11, addField12]}, {key: "zulassungs.", fields: [addField21, addField22]}, ... ]
            // so each item of phases is a container of type 
            //   - "Öffentliche Auslegung"
            //   - "Erörterungstermin"
            //   - "Zulassungsentscheidung"
            phases: [],

            addButton: null,

            // handle data as complex value
            valueAsTableData: true,

            counter: 1,

            buildRendering: function () {
                this.domNode = construct.create("div");

                var addWidgets = require("ingrid/IgeActions");
                addWidgets.additionalFieldWidgets.push(this);

                var self = this;
                this.addButton = new Button({
                    label: message.get("uvp.form.addPhase"),
                    onClick: function () {
                        // alert("Will show a dialog here ...");
                        dialog.show(message.get("uvp.form.dialog.addPhase.title"), message.get("uvp.form.dialog.addPhase.text"), dialog.INFO, [
                            {
                                caption: message.get("uvp.form.dialog.addPhase.phase1"),
                                action: function () {
                                    var rubric = lang.hitch(self, self.addPhase1)();
                                    self.openPhase(rubric);
                                }
                            }, {
                                caption: message.get("uvp.form.dialog.addPhase.phase2"),
                                action: function () {
                                    var rubric = lang.hitch(self, self.addPhase2)();
                                    self.openPhase(rubric);
                                }
                            }, {
                                caption: message.get("uvp.form.dialog.addPhase.phase3"),
                                action: function () {
                                    var rubric = lang.hitch(self, self.addPhase3)();
                                    self.openPhase(rubric);
                                }
                            }
                        ], 500);

                    }
                });
                
                var clearFixDiv = construct.toDom("<div class='clear' style='text-align: center'></div>");
                construct.place(this.addButton.domNode, clearFixDiv);
                
                topic.subscribe("/selectNode", function(message) {
                    var data = message.node;
                    if (data.nodeAppType === "O" && data.id !== "objectRoot" && data.objectClass !== 1000) {
                        domClass.remove(self.addButton.domNode, "hide");
                    } else {
                        domClass.add(self.addButton.domNode, "hide");
                    }
                });
                
                construct.place(clearFixDiv, "contentFrameBodyObject", "after");
            },

            attr: function (type, value) {
                console.log("set value of UVP-Phase to: ", value);
                if (type === "value") {
                    this.removeAllFields();
                    this.phases = [];
                    this.counter = 1;
                    this.createFieldsFromValues(value);
                }
            },

            createFieldsFromValues: function (values) {

                array.forEach(values, function (phase) {
                    var phaseValues = phase[0].tableRows;
                    switch (phase[0].identifier) {
                        case "phase1": this.addPhase1(phaseValues); break;
                        case "phase2": this.addPhase2(phaseValues); break;
                        case "phase3": this.addPhase3(phaseValues); break;
                        default: console.error("dynamic rubric not supported: " + phase[0].identifier);
                    }
                }, this);
            },

            // attr can be: "displayedValue"
            // is used to receive the value of the whole Widget
            // we have to construct a complex value here
            // Array of additional fields
            get: function (attr) {
                if (attr !== "displayedValue") return;
                var addWidgets = require("ingrid/IgeActions");

                var result = [];
                // iterate over all dynamically added blocks
                array.forEach(this.phases, function (phase) {
                    var entries = [];
                    var items = phase.fields;

                    // iterate over all fields of this block
                    array.forEach(items, function (item) {

                        var field = item.field;
                        var entry;
                        var id = field.id.split("_")[0];

                        if (field instanceof _FormValueWidget) {

                            entry = {
                                identifier: id,
                                value: field.get("value"),
                                listId: null,
                                tableRows: null
                            };
                        } else if (field instanceof CustomGrid) {
                            var tableData = addWidgets.prepareGridDataForBackend(field);
                            entry = {
                                identifier: id,
                                value: null,
                                listId: null,
                                tableRows: tableData
                            };
                        }
                        entries.push([entry]);
                    });
                    result.push([{
                        identifier: phase.key,
                        tableRows: entries
                    }]);
                });
                return result;
            },

            removePhaseInfo: function () {
                // remove text information if no phase was added yet
                var phaseInfo = query(".no-phase");
                if (phaseInfo.length > 0) construct.destroy(phaseInfo[0]);
            },

            removeAllFields: function () {
                this.removePhaseInfo();

                while (this.phases.length > 0) {
                    var phase = this.phases[0];
                    var rubric = phase.key + "_" + phase.pos;
                    this.removePhase(rubric, phase);
                }
            },

            addPhase1: function (values) {
                this.removePhaseInfo();
                var phaseFields = [];
                var counter = this.counter;

                var rubric = "phase1_" + counter;
                creator.addElementToObjectForm(
                    creator.createRubric({
                        id: rubric,
                        help: "Hilfetext ...",
                        label: counter + ". " + message.get("uvp.form.phase1.rubric")
                    })
                );

                /**
                 * Datum
                 */
                var idDateFrom = "publicDateFrom_" + counter;
                var idDateTo = "publicDateTo_" + counter;
                creator.addToSection(rubric, creator.createDomDatebox({ id: idDateFrom, name: message.get("uvp.form.phase1.dateFrom"), help: "...", isMandatory: true, visible: "optional", style: "width:25%" }));
                creator.addToSection(rubric, creator.createDomDatebox({ id: idDateTo, name: message.get("uvp.form.phase1.dateTo"), help: "...", isMandatory: true, visible: "optional", style: "width:25%" }));
                phaseFields.push({ key: "publicDateFrom", field: registry.byId(idDateFrom) });
                phaseFields.push({ key: "publicDateTo", field: registry.byId(idDateTo) });
                
                // TODO: add behaviour for mandatory date field?

                // layout fix!
                construct.place(construct.toDom("<div class='clear'></div>"), rubric);

                /**
                 * Antrag auf Entscheidung über die Zulässigkeit des Vorhabens
                 */
                var structure = [
                    { field: 'label', name: 'Titel', width: '300px', editable: true },
                    { field: 'link', name: 'Link', width: '200px', editable: true },
                    { field: 'type', name: 'Typ', width: '50px', editable: true },
                    { field: 'size', name: 'Größe', width: '50px', editable: true },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                var id = "legitimacyDocs_" + counter;
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.legitimacyDocs"), help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "legitimacyDocs", field: registry.byId(id) });

                // TODO: at least one document validation
                
                /**
                 * UVP-Bericht nach § 6 UVPG
                 */
                structure = [
                    { field: 'label', name: 'Titel', width: '300px', editable: true },
                    { field: 'link', name: 'Link', width: '200px', editable: true },
                    { field: 'type', name: 'Typ', width: '50px', editable: true },
                    { field: 'size', name: 'Größe', width: '50px', editable: true },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                id = "reportArticle6Docs_" + counter;
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.reportArticle6Docs"), help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "reportArticle6Docs", field: registry.byId(id) });

                // TODO: at least one document validation
                
                /**
                 * Berichte und Empfehlungen
                 */
                structure = [
                    { field: 'label', name: 'Titel', width: '300px', editable: true },
                    { field: 'link', name: 'Link', width: '200px', editable: true },
                    { field: 'type', name: 'Typ', width: '50px', editable: true },
                    { field: 'size', name: 'Größe', width: '50px', editable: true },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                id = "reportsRecommendationsDocs_" + counter;
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.reportsRecommendationsDocs"), help: "...", isMandatory: false, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "reportsRecommendationsDocs", field: registry.byId(id) });

                /**
                 * Weitere Unterlagen
                 */
                structure = [
                    { field: 'label', name: 'Titel', width: '300px', editable: true },
                    { field: 'link', name: 'Link', width: '200px', editable: true },
                    { field: 'type', name: 'Typ', width: '50px', editable: true },
                    { field: 'size', name: 'Größe', width: '50px', editable: true },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                id = "moreDocs_" + counter;
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.moreDocs"), help: "...", isMandatory: false, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "moreDocs", field: registry.byId(id) });

                this.phases.push({
                    key: "phase1",
                    pos: counter,
                    fields: phaseFields
                });

                this.addDeletePhaseButton(rubric, counter);

                // add values to the created phase
                this.addValuesToPhase(phaseFields, values);

                this.counter++;

                return rubric;
            },

            addPhase2: function (values) {
                this.removePhaseInfo();
                var phaseFields = [];
                var counter = this.counter;

                var rubric = "phase2_" + counter;
                creator.addElementToObjectForm(
                    creator.createRubric({
                        id: rubric,
                        help: "Hilfetext ...",
                        label: counter + ". " + message.get("uvp.form.phase2.rubric")
                    })
                );
                
                /**
                 * Erörterungstermin
                 */
                var structure = [
                    { field: 'dateText', name: 'Beschreibung', width: '300px', editable: true },
                    { field: 'dateValue', name: 'Datum', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                var id = "considerationDate_" + counter;
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase2.considerationDate"), help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "considerationDate", field: registry.byId(id) });
                
                /**
                 * Bekanntmachungstext
                 */
                id = "considerationDateDescription_" + counter;
                creator.addToSection(rubric, creator.createDomTextarea({id: id, name: message.get("uvp.form.phase2.considerationDateDescription"), help: "...", isMandatory: false, visible: "optional", rows: 10, style: "width:100%"}));
                var textarea = registry.byId(id);
                this.addValidatorForTextarea(textarea);
                phaseFields.push({ key: "considerationDateDescription", field: textarea });

                /**
                 * Bekanntmachung
                 */
                structure = [
                    { field: 'label', name: 'Titel', width: '300px', editable: true },
                    { field: 'link', name: 'Link', width: '200px', editable: true },
                    { field: 'type', name: 'Typ', width: '50px', editable: true },
                    { field: 'size', name: 'Größe', width: '50px', editable: true },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                id = "considerationDocs_" + counter;
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase2.considerationDocs"), help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "considerationDocs", field: registry.byId(id) });

                this.phases.push({
                    key: "phase2",
                    pos: counter,
                    fields: phaseFields
                });

                this.addDeletePhaseButton(rubric, counter);
                
                // add values to the created phase
                this.addValuesToPhase(phaseFields, values);

                this.counter++;

                return rubric;
            },

            addPhase3: function (values) {
                this.removePhaseInfo();
                var phaseFields = [];
                var counter = this.counter;

                var rubric = "phase3_" + counter;
                creator.addElementToObjectForm(
                    creator.createRubric({
                        id: rubric,
                        help: "Hilfetext ...",
                        label: counter + ". " + message.get("uvp.form.phase3.rubric")
                    })
                );

                /**
                 * Datum der Entscheidung über die Zulassung
                 */
                var id = "approvalDate_" + counter;
                creator.addToSection(rubric, creator.createDomDatebox({ id: id, name: message.get("uvp.form.phase3.approvalDate"), help: "...", isMandatory: true, visible: "optional", style: "width:50%" }));
                phaseFields.push({ key: "approvalDate", field: registry.byId(id) });

                construct.place(construct.toDom("<div class='clear'></div>"), rubric);

                /**
                 * Bekanntmachungstext der Zulassungsentscheidung
                 */
                id = "approvalDescription_" + counter;
                creator.addToSection(rubric, creator.createDomTextarea({ id: id, name: message.get("uvp.form.phase3.approvalDescription"), help: "...", isMandatory: true, visible: "optional", rows: 3, style: "width:100%" }));
                var textarea = registry.byId(id);
                this.addValidatorForTextarea(textarea);
                phaseFields.push({ key: "approvalDescription", field: textarea });

                /**
                 * Zulassungsdokument
                 */
                var structure = [
                   { field: 'label', name: 'Titel', width: '300px', editable: true },
                    { field: 'link', name: 'Link', width: '200px', editable: true },
                    { field: 'type', name: 'Typ', width: '50px', editable: true },
                    { field: 'size', name: 'Größe', width: '50px', editable: true },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                id = "approvalDocs_" + counter;
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase3.approvalDocs"), help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "approvalDocs", field: registry.byId(id) });

                /**
                 * Planungsunterlagen
                 */
                structure = [
                    { field: 'label', name: 'Titel', width: '300px', editable: true },
                    { field: 'link', name: 'Link', width: '200px', editable: true },
                    { field: 'type', name: 'Typ', width: '50px', editable: true },
                    { field: 'size', name: 'Größe', width: '50px', editable: true },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                id = "designDocs_" + counter;
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase3.designDocs"), help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "designDocs", field: registry.byId(id) });

                this.phases.push({
                    key: "phase3",
                    pos: counter,
                    fields: phaseFields
                });
                
                this.addDeletePhaseButton(rubric, counter);
                
                // add values to the created phase
                this.addValuesToPhase(phaseFields, values);

                this.counter++;

                return rubric;
            },

            openPhase: function (rubric) {
                setTimeout(function () { IgeEvents.toggleFields(rubric, "showAll"); }, 500);
            },
            
            addDeletePhaseButton: function(rubric, counter) {
                var self = this;
                var delButton = new Button({
                    label: message.get("uvp.form.deletePhase"),
                    "class": "right optional",
                    onClick: function () {
                        dialog.show(message.get("uvp.form.deletePhase"), message.get("uvp.form.deletePhase.confirmText"), dialog.WARN, [
                            {
                                caption: "Ja",
                                action: function () {
                                    // delete all widgets of this phase
                                    var phase = array.filter(self.phases, function (phase) { return phase.pos === counter; })[0];
                                    self.removePhase(rubric, phase);
                                }
                            }, {
                                caption: "Nein",
                                action: function () { }
                            }
                        ]);

                    }
                });
                construct.place(delButton.domNode, rubric);
                
                // layout fix!
                construct.place(construct.toDom("<div class='clear'></div>"), rubric);
            },

            removePhase: function (rubric, phase) {
                array.forEach(phase.fields, function (fieldObj) {
                    fieldObj.field.destroyRecursive();
                });

                // delete dom of phase
                construct.destroy(rubric);

                // remove phase from array
                var pos = array.indexOf(this.phases, phase);
                this.phases.splice(pos, 1);
            },

            addValuesToPhase: function (phaseFields, values) {
                if (!values) return;

                var addWidgets = require("ingrid/IgeActions");

                array.forEach(values, function (valueObj) {
                    var key = valueObj[0].identifier;
                    var fieldWidgetObj = array.filter(phaseFields, function (item) { return item.key === key; })[0];
                    if (fieldWidgetObj) {
                        var fieldWidget = fieldWidgetObj.field;
                        if (fieldWidget instanceof DateTextBox) {
                            if (valueObj[0].value !== null) {
                                fieldWidget.set("value", new Date(+valueObj[0].value));
                            }

                        } else if (fieldWidget instanceof CustomGrid) {
                            var rowData = addWidgets.prepareBackendDataForGrid(valueObj[0]);
                            fieldWidget.setData(rowData);
                            fieldWidget.render();
                        } else {
                            fieldWidget.set("value", valueObj[0].value);
                        }
                    }
                });
            },

            addValidatorForTextarea: function (widget) {
                widget.validate = function () {
                    return this.validator();
                };
                widget.validator = function () {
                    if (this.required && this.get("value") === "") {
                        domClass.add(this.domNode, "importantBackground");
                        return false;
                    } else {
                        domClass.remove(this.domNode, "importantBackground");
                        return true;
                    }
                };
            }
        });

    });