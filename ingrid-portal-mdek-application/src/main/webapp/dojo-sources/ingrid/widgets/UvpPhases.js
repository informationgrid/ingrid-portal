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
    "ingrid/IgeEvents",
    "ingrid/grid/CustomGrid",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "dojo/NodeList-traverse"
], function (declare, array, lang, construct, domClass, query, topic, _WidgetBase, registry, Button, DateTextBox, _FormValueWidget,
    creator, dialog, IgeEvents, CustomGrid, Editors, Formatters) {

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
                    label: "Phase hinzufügen",
                    onClick: function () {
                        // alert("Will show a dialog here ...");
                        dialog.show("Phase erstellen", "Wählen Sie die zu erstellende Phase aus:", dialog.INFO, [
                            {
                                caption: "Öffentliche Auslegung",
                                action: function () {
                                    var rubric = lang.hitch(self, self.addPhase1)();
                                    self.openPhase(rubric);
                                }
                            }, {
                                caption: "Erörterungstermin",
                                action: function () {
                                    var rubric = lang.hitch(self, self.addPhase2)();
                                    self.openPhase(rubric);
                                }
                            }, {
                                caption: "Zulassungsentscheidung",
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
                
                topic.subscribe("/loadRequest", function(data) {
                    console.log("recognized loaded data:", data);
                    if (data.appType === "O") {
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
                        label: counter + ". Beteiligung der Öffentlichkeit"
                    })
                );

                /**
                 * Datum
                 */
                var idDateFrom = "publicDateFrom_" + counter;
                var idDateTo = "publicDateTo_" + counter;
                creator.addToSection(rubric, creator.createDomDatebox({ id: idDateFrom, name: "Auslegungszeitraum von", help: "...", isMandatory: true, visible: "optional", style: "width:25%" }));
                creator.addToSection(rubric, creator.createDomDatebox({ id: idDateTo, name: "bis", help: "...", isMandatory: false, visible: "optional", style: "width:25%" }));
                phaseFields.push({ key: "dateFrom", field: registry.byId(idDateFrom) });
                phaseFields.push({ key: "dateTo", field: registry.byId(idDateTo) });
                
                // TODO: add behaviour for mandatory date field?

                // layout fix!
                construct.place(construct.toDom("<div class='clear'></div>"), rubric);

                /**
                 * Auslegungsinformationen
                 */
                var structure = [
                    { field: 'auslegungLabel', name: 'Titel', width: '300px', editable: true },
                    { field: 'auslegungLink', name: 'Link', width: '200px', editable: true },
                    { field: 'auslegungType', name: 'Typ', width: '50px', editable: true },
                    { field: 'auslegungSize', name: 'Größe', width: '50px', editable: true },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                var id = "auslegungsTable_" + counter;
                creator.createDomDataGrid({ id: id, name: "Antrag auf Entscheidung über die Zulässigkeit des Vorhabens", help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "auslegungsTable", field: registry.byId(id) });

                // TODO: at least one document validation
                
                /**
                 * Antragsunterlagen
                 */
                structure = [
                    { field: 'antraegeLabel', name: 'Titel', width: '300px', editable: true },
                    { field: 'antraegeLink', name: 'Link', width: '200px', editable: true },
                    { field: 'antraegeType', name: 'Typ', width: '50px', editable: true },
                    { field: 'antraegeSize', name: 'Größe', width: '50px', editable: true },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                id = "antraegeTable_" + counter;
                creator.createDomDataGrid({ id: id, name: "UVP-Bericht nach § 6 UVPG", help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "antraegeTable", field: registry.byId(id) });

                // TODO: at least one document validation
                
                /**
                 * Berichte und Empfehlungen
                 */
//                structure = [
//                    { field: 'berichteLabel', name: 'Titel', width: '300px', editable: true },
//                    { field: 'berichteLink', name: 'Link', width: '200px', editable: true },
//                    { field: 'berichteType', name: 'Typ', width: '50px', editable: true },
//                    { field: 'berichteSize', name: 'Größe', width: '50px', editable: true },
//                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
//                ];
//                id = "berichteTable_" + counter;
//                creator.createDomDataGrid({ id: id, name: "Berichte und Empfehlungen", help: "...", isMandatory: false, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
//                    structure, rubric);
//                phaseFields.push({ key: "berichteTable", field: registry.byId(id) });

                /**
                 * Weitere Unterlagen
                 */
//                structure = [
//                    { field: 'weitereLabel', name: 'Titel', width: '300px', editable: true },
//                    { field: 'weitereLink', name: 'Link', width: '200px', editable: true },
//                    { field: 'weitereType', name: 'Typ', width: '50px', editable: true },
//                    { field: 'weitereSize', name: 'Größe', width: '50px', editable: true },
//                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
//                ];
//                id = "weitereTable_" + counter;
//                creator.createDomDataGrid({ id: id, name: "Weitere Unterlagen", help: "...", isMandatory: false, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
//                    structure, rubric);
//                phaseFields.push({ key: "weitereTable", field: registry.byId(id) });

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
                        label: counter + ". Erörterungstermin"
                    })
                );

//                var id = "date_" + counter;
//                creator.addToSection(rubric, creator.createDomDatebox({ id: id, name: "Datum", help: "...", isMandatory: false, visible: "optional", style: "width:25%" }));
//                phaseFields.push({ key: "date", field: registry.byId(id) });
//
//                construct.place(construct.toDom("<div class='clear'></div>"), rubric);
                
                /**
                 * 
                 */
                var structure = [
                    { field: 'dateText', name: 'Beschreibung', width: '300px', editable: true },
                    { field: 'dateValue', name: 'Datum', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                var id = "eroerterungDateTable_" + counter;
                creator.createDomDataGrid({ id: id, name: "Erörterungstermin", help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "eroerterungDateTable", field: registry.byId(id) });
                
                /**
                 * 
                 */
                id = "eroerterungDateDescriptionTable_" + counter;
                creator.addToSection(rubric, creator.createDomTextarea({id: id, name: "Bekanntmachungstext", help: "...", isMandatory: false, visible: "optional", rows: 10, style: "width:100%"}));
                phaseFields.push({ key: "eroerterungDateDescriptionTable", field: registry.byId(id) });

                /**
                 * 
                 */
                structure = [
                    { field: 'eroerterungLabel', name: 'Titel', width: '300px', editable: true },
                    { field: 'eroerterungLink', name: 'Link', width: '200px', editable: true },
                    { field: 'eroerterungType', name: 'Typ', width: '50px', editable: true },
                    { field: 'eroerterungSize', name: 'Größe', width: '50px', editable: true },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                id = "eroerterungTable_" + counter;
                creator.createDomDataGrid({ id: id, name: "Bekanntmachung", help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "eroerterungTable", field: registry.byId(id) });

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
                        label: counter + ". Entscheidung über die Zulassung"
                    })
                );

                /**
                 * 
                 */
                var id = "date_" + counter;
                creator.addToSection(rubric, creator.createDomDatebox({ id: id, name: "Datum der Entscheidung über die Zulassung", help: "...", isMandatory: true, visible: "optional", style: "width:50%" }));
                phaseFields.push({ key: "date", field: registry.byId(id) });

                construct.place(construct.toDom("<div class='clear'></div>"), rubric);

                /**
                 * 
                 */
                id = "zulassung_" + counter;
                creator.addToSection(rubric, creator.createDomTextarea({ id: id, name: "Bekanntmachungstext der Zulassungsentscheidung", help: "...", isMandatory: true, visible: "optional", rows: 3, style: "width:100%" }));
                var textarea = registry.byId(id);
                this.addValidatorForTextarea(textarea);
                phaseFields.push({ key: "zulassung", field: textarea });

                /**
                 * 
                 */
                var structure = [
                    { field: 'auslegungLabel', name: 'Titel', width: '300px', editable: true },
                    { field: 'auslegungLink', name: 'Link', width: '200px', editable: true },
                    { field: 'auslegungType', name: 'Typ', width: '50px', editable: true },
                    { field: 'auslegungSize', name: 'Größe', width: '50px', editable: true },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                id = "auslegungTable_" + counter;
                creator.createDomDataGrid({ id: id, name: "Zulassungsdokument", help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "auslegungTable", field: registry.byId(id) });

                /**
                 * 
                 */
                structure = [
                    { field: 'beschlussLabel', name: 'Titel', width: '300px', editable: true },
                    { field: 'beschlussLink', name: 'Link', width: '200px', editable: true },
                    { field: 'beschlussType', name: 'Typ', width: '50px', editable: true },
                    { field: 'beschlussSize', name: 'Größe', width: '50px', editable: true },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
                id = "beschlussTable_" + counter;
                creator.createDomDataGrid({ id: id, name: "Planungsunterlagen", help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "beschlussTable", field: registry.byId(id) });

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
                    label: "Phase löschen",
                    "class": "right optional",
                    onClick: function () {
                        dialog.show("Phase löschen", "Möchten Sie wirklich diese Phase entfernen?", dialog.WARN, [
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