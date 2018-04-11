/*-
 * **************************************************-
 * InGrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/aspect",
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
    "ingrid/hierarchy/dirty",
    "ingrid/utils/Store",
    "ingrid/utils/Catalog",
    "./upload/UploadWidget",
    "dojo/NodeList-traverse"
], function(declare, array, lang, aspect, construct, domClass, query, topic, _WidgetBase, registry, Button, DateTextBox, _FormValueWidget,
    creator, dialog, message, IgeEvents, CustomGrid, Editors, Formatters, dirty, UtilStore, Catalog, UploadWidget) {

        return declare("UVPPhases", [_WidgetBase], {

            // phases consists of 0 or n containers, which itself contain additional fields
            // e.g.: [ { key: "öffentl. Auslegung", fields: [addField11, addField12]}, {key: "zulassungs.", fields: [addField21, addField22]}, ... ]
            // so each item of phases is a container of type
            //   - 1) "Öffentliche Auslegung"
            //   - 2) "Erörterungstermin"
            //   - 3) "Zulassungsentscheidung"
            phases: [],

            // define which phases can be created through the dialog
            availablePhases: [1, 2, 3],

            // define if the add button for a phase is shown or hidden
            _buttonAddHide: false,

            // toggled state of phases (needed when saving document)
            expandedPhases: [],

            addButton: null,

            // handle data as complex value
            valueAsTableData: true,

            counter: 1,

            // TODO put URLs into configuration?
            uploadUrl: "rest/document",

            buildRendering: function() {
                var self = this;
                this.domNode = construct.create("div");

                this.prepareContextMenu();

                var addWidgets = require("ingrid/IgeActions");
                addWidgets.additionalFieldWidgets.push(this);

                // create button which creates the different phases
                console.log("Create phase button");
                this.addButton = this._createPhaseButton();

                var clearFixDiv = construct.toDom("<div class='clear' style='text-align: center'></div>");
                construct.place(this.addButton.domNode, clearFixDiv);

                // show phase button depending on the selected node
                topic.subscribe("/selectNode", function(message) {
                    var data = message.node;
                    if (data.nodeAppType === "O" && !self._buttonAddHide && (
                        (data.id !== "objectRoot" && data.objectClass !== 1000) ||
                        data.id === "newNode")) {
                        domClass.remove(self.addButton.domNode, "hide");
                    } else {
                        domClass.add(self.addButton.domNode, "hide");
                    }

                    // set disabled state according to permission
                    if (data.userWritePermission || data.id === "newNode") {
                        // button for phases
                        self.addButton.set("disabled", false);

                        // all upload links
                        query(".functionalLink", "contentFrameBodyObject").removeClass("hide");
                    } else {
                        // button for phases
                        self.addButton.set("disabled", true);

                        // all upload links
                        query(".functionalLink", "contentFrameBodyObject").addClass("hide");
                    }
                });

                var handler = topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                    // iterate over all phases
                    array.forEach(self.phases, function(phase) {
                        array.forEach(phase.fields, function(phaseField) {
                            // only check doc tables
                            if (phaseField.isDocTable) {
                                // check all rows if they have title, link and size (mandatory)
                                var data = phaseField.field.data;
                                var hasInvalidRows = data.some(function(item) {
                                    // check label, link and size if they have any value
                                    return !item.label || !item.link ||
                                        item.label.trim().length === 0 ||
                                        item.link.trim().length === 0;
                                });
                                if (hasInvalidRows) {
                                    notPublishableIDs.push( [phaseField.field.id, message.get("validation.error.document.table.invalid")] );
                                }
                            }
                        });
                    });
                });

                this.handleToggledState();

                construct.place(clearFixDiv, "contentFrameBodyObject", "after");
            },

            /**
             * Add a new context menu type for the document grid. This adds a new entry to set the expire date for the selected rows.
             */
            prepareContextMenu: function() {
                var menu = require("ingrid/menu");

                var contextMenu = menu.initContextMenu( { contextMenu: "EXPIRED_GRID", moveRows: true } );
                contextMenu.addChild(new require("dijit/MenuSeparator")());
                var expireMenu = new require("dijit/MenuItem")({
                    id: "menuExpireDateClicked_EXPIRED_GRID",
                    label: message.get('contextmenu.table.expireClicked'),
                    onClick: function() {
                        dialog.showPage('Datum wählen', 'dialogs/mdek_select_expiry_date_dialog.jsp', 500, 240, false, {
                            selectedRows: clickedSlickGrid.getSelectedRows(),
                            gridId: clickedSlickGrid.id
                        });
                    }
                });
                contextMenu.addChild(expireMenu);

                aspect.after(contextMenu, "_openMyself", function(result, args) {
                    var e = args[0];
                    var findGrid = function(element) {
                        while (element) {
                            if (domClass.contains(element, "ui-widget")) {
                                return element.id;
                            }
                            element = element.parentNode;
                        }
                    };

                    // all items have to be disabled if user has no permission if hierarchy page is used!
                    var isHierarchyPage = registry.byId("stackContainer").selectedChildWidget.id == "pageHierarchy";

                    if (!isHierarchyPage || currentUdk.writePermission) {
                        var gridId = findGrid(e.target);
                        var nothingIsSelected = UtilGrid.getSelectedRowIndexes(gridId).length === 0;

                         if (nothingIsSelected) {
                            expireMenu.set("disabled", true);
                         }
                    }
                });
            },

            attr: function(type, value) {
                console.log("set value of UVP-Phase to: ", value);
                if (type === "value") {
                    this.removeAllFields();
                    this.phases = [];
                    this.counter = 1;
                    this.createFieldsFromValues(value);
                }
            },

            createFieldsFromValues: function(values) {

                array.forEach(values, function(phase, index) {
                    var phaseValues = phase[0].tableRows;
                    var rubricId = null;
                    switch (phase[0].identifier) {
                        case "phase1": rubricId = this.addPhase1(phaseValues); break;
                        case "phase2": rubricId = this.addPhase2(phaseValues); break;
                        case "phase3": rubricId = this.addPhase3(phaseValues); break;
                        default: console.error("dynamic rubric not supported: " + phase[0].identifier);
                    }

                    // recover toggled state
                    if (this.expandedPhases.indexOf(index) !== -1) {
                        IgeEvents.toggleFields(rubricId, "showAll");
                    }

                }, this);
            },

            // attr can be: "displayedValue"
            // is used to receive the value of the whole Widget
            // we have to construct a complex value here
            // Array of additional fields
            get: function(attr) {
                if (attr !== "displayedValue") return;
                var addWidgets = require("ingrid/IgeActions");

                var result = [];
                // iterate over all dynamically added blocks
                array.forEach(this.phases, function(phase) {
                    var entries = [];
                    var items = phase.fields;

                    // iterate over all fields of this block
                    array.forEach(items, function(item) {

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

            removePhaseInfo: function() {
                // remove text information if no phase was added yet
                var phaseInfo = query(".no-phase");
                if (phaseInfo.length > 0) construct.destroy(phaseInfo[0]);
            },

            removeAllFields: function() {
                this.removePhaseInfo();

                while (this.phases.length > 0) {
                    var phase = this.phases[0];
                    var rubric = phase.key + "_" + phase.pos;
                    this.removePhase(rubric, phase);
                }
            },

            addPhase1: function(values) {
                this.removePhaseInfo();
                var phaseFields = [];
                var newFieldsToDirtyCheck = [];
                var counter = this.counter;

                var rubric = "phase1_" + counter;
                var rubricDiv = creator.createRubric({
                    id: rubric,
                    help: message.get("uvp.form.phase1.rubric.helpMessage"),
                    label: message.get("uvp.form.phase1.rubric")
                });
                domClass.add(rubricDiv, "phase");
                creator.addElementToObjectForm(rubricDiv);

                /**
                 * Datum
                 */
                var idDateFrom = "publicDateFrom_" + counter;
                var idDateTo = "publicDateTo_" + counter;
                newFieldsToDirtyCheck.push(idDateFrom, idDateTo);
                creator.addToSection(rubric, creator.createDomDatebox({ id: idDateFrom, name: message.get("uvp.form.phase1.dateFrom"), help: message.get("uvp.form.phase1.dateFrom.helpMessage"), visible: "required showOnlyExpanded", style: "width:33%" }));
                creator.addToSection(rubric, creator.createDomDatebox({ id: idDateTo, name: message.get("uvp.form.phase1.dateTo"), help: message.get("uvp.form.phase1.dateTo.helpMessage"), visible: "required showOnlyExpanded", style: "width:33%" }));
                phaseFields.push({ key: "publicDateFrom", field: registry.byId(idDateFrom) });
                phaseFields.push({ key: "publicDateTo", field: registry.byId(idDateTo) });

                // TODO: add behaviour for mandatory date field?

                // layout fix!
                construct.place(construct.toDom("<div class='clear'></div>"), rubric);

                /**
                 * Auslegungsinformationen
                 */

                var id = "technicalDocs_" + counter;
                newFieldsToDirtyCheck.push(id);
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.technicalDocs"), help: message.get("uvp.form.phase1.technicalDocs.helpMessage"), visible: "required showOnlyExpanded", rows: "1", forceGridHeight: false, style: "width:100%", contextMenu: "EXPIRED_GRID", moveRows: true },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
                phaseFields.push({ key: "technicalDocs", field: registry.byId(id), isDocTable: true });

                // TODO: at least one document validation

                /**
                 * Antragsunterlagen
                 */
                id = "applicationDocs_" + counter;
                newFieldsToDirtyCheck.push(id);
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.applicationDocs"), help: message.get("uvp.form.phase1.applicationDocs.helpMessage"), visible: "required showOnlyExpanded", rows: "1", forceGridHeight: false, style: "width:100%", contextMenu: "EXPIRED_GRID", moveRows: true },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
                phaseFields.push({ key: "applicationDocs", field: registry.byId(id), isDocTable: true });

                // TODO: at least one document validation

                /**
                 * Berichte und Empfehlungen
                 */
                id = "reportsRecommendationsDocs_" + counter;
                newFieldsToDirtyCheck.push(id);
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.reportsRecommendationsDocs"), help: message.get("uvp.form.phase1.reportsRecommendationsDocs.helpMessage"), isMandatory: false, visible: "optional", rows: "1", forceGridHeight: false, style: "width:100%", contextMenu: "EXPIRED_GRID", moveRows: true },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
                phaseFields.push({ key: "reportsRecommendationsDocs", field: registry.byId(id), isDocTable: true });

                /**
                 * Weitere Unterlagen
                 */
                id = "moreDocs_" + counter;
                newFieldsToDirtyCheck.push(id);
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.moreDocs"), help: message.get("uvp.form.phase1.moreDocs.helpMessage"), isMandatory: false, visible: "optional", rows: "1", forceGridHeight: false, style: "width:100%", contextMenu: "EXPIRED_GRID", moveRows: true },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
                phaseFields.push({ key: "moreDocs", field: registry.byId(id), isDocTable: true });

                /**
                 * Bekanntmachung
                 */
                // id = "publicationDocs_" + counter;
                // newFieldsToDirtyCheck.push(id);
                // creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.publicationDocs"), help: "...", visible: "optional", rows: "1", forceGridHeight: false, style: "width:100%" },
                //     this.getDocTableStructure(), rubric);
                // this.addUploadLink(id);
                // phaseFields.push({ key: "publicationDocs", field: registry.byId(id), isDocTable: true });

                this.phases.push({
                    key: "phase1",
                    pos: counter,
                    fields: phaseFields
                });

                this.addDeletePhaseButton(rubric, counter);

                // add values to the created phase
                this.addValuesToPhase(phaseFields, values);

                this.counter++;

                // add dirty check on new fields
                array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

                return rubric;
            },

            addPhase2: function(values) {
                this.removePhaseInfo();
                var phaseFields = [];
                var newFieldsToDirtyCheck = [];
                var counter = this.counter;

                var rubric = "phase2_" + counter;
                var rubricDiv = creator.createRubric({
                    id: rubric,
                    help: message.get("uvp.form.phase2.rubric.helpMessage"),
                    label: message.get("uvp.form.phase2.rubric")
                });
                domClass.add(rubricDiv, "phase");
                creator.addElementToObjectForm(rubricDiv);

                /**
                 * Datum
                 */
                var idDateFrom = "considerDateFrom_" + counter;
                var idDateTo = "considerDateTo_" + counter;
                newFieldsToDirtyCheck.push(idDateFrom, idDateTo);
                creator.addToSection(rubric, creator.createDomDatebox({ id: idDateFrom, name: message.get("uvp.form.phase2.dateFrom"), help: message.get("uvp.form.phase2.dateFrom.helpMessage"), visible: "required showOnlyExpanded", style: "width:33%" }));
                creator.addToSection(rubric, creator.createDomDatebox({ id: idDateTo, name: message.get("uvp.form.phase2.dateTo"), help: message.get("uvp.form.phase2.dateTo.helpMessage"), visible: "showOnlyExpanded", style: "width:33%" }));
                phaseFields.push({ key: "considerDateFrom", field: registry.byId(idDateFrom) });
                phaseFields.push({ key: "considerDateTo", field: registry.byId(idDateTo) });

                /**
                 * Bekanntmachung
                 */
                var id = "considerationDocs_" + counter;
                newFieldsToDirtyCheck.push(id);
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase2.considerationDocs"), help: message.get("uvp.form.phase2.considerationDocs.helpMessage"), visible: "required showOnlyExpanded", rows: "1", forceGridHeight: false, style: "width:100%", contextMenu: "EXPIRED_GRID", moveRows: true },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
                phaseFields.push({ key: "considerationDocs", field: registry.byId(id), isDocTable: true });

                this.phases.push({
                    key: "phase2",
                    pos: counter,
                    fields: phaseFields
                });

                this.addDeletePhaseButton(rubric, counter);

                // add values to the created phase
                this.addValuesToPhase(phaseFields, values);

                this.counter++;

                // add dirty check on new fields
                array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

                return rubric;
            },

            addPhase3: function(values) {
                this.removePhaseInfo();
                var phaseFields = [];
                var newFieldsToDirtyCheck = [];
                var counter = this.counter;

                var rubric = "phase3_" + counter;
                var rubricDiv = creator.createRubric({
                    id: rubric,
                    help: message.get("uvp.form.phase3.rubric.helpMessage"),
                    label: message.get("uvp.form.phase3.rubric")
                });
                domClass.add(rubricDiv, "phase");
                creator.addElementToObjectForm(rubricDiv);

                /**
                 * Datum der Entscheidung über die Zulassung
                 */
                var id = "approvalDate_" + counter;
                newFieldsToDirtyCheck.push(id);
                creator.addToSection(rubric, creator.createDomDatebox({ id: id, name: message.get("uvp.form.phase3.approvalDate"), help: message.get("uvp.form.phase3.approvalDate.helpMessage"), visible: "required showOnlyExpanded", style: "width:50%" }));
                phaseFields.push({ key: "approvalDate", field: registry.byId(id) });

                construct.place(construct.toDom("<div class='clear'></div>"), rubric);

                /**
                 * Zulassungsdokument
                 */
                id = "approvalDocs_" + counter;
                newFieldsToDirtyCheck.push(id);
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase3.approvalDocs"), help: message.get("uvp.form.phase3.approvalDocs.helpMessage"), visible: "required showOnlyExpanded", rows: "1", forceGridHeight: false, style: "width:100%", contextMenu: "EXPIRED_GRID", moveRows: true },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
                phaseFields.push({ key: "approvalDocs", field: registry.byId(id), isDocTable: true });

                /**
                 * Planungsunterlagen
                 */
                id = "designDocs_" + counter;
                newFieldsToDirtyCheck.push(id);
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase3.designDocs"), help: message.get("uvp.form.phase3.designDocs.helpMessage"), visible: "required showOnlyExpanded", rows: "1", forceGridHeight: false, style: "width:100%", contextMenu: "EXPIRED_GRID", moveRows: true },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
                phaseFields.push({ key: "designDocs", field: registry.byId(id), isDocTable: true });

                this.phases.push({
                    key: "phase3",
                    pos: counter,
                    fields: phaseFields
                });

                this.addDeletePhaseButton(rubric, counter);

                // add values to the created phase
                this.addValuesToPhase(phaseFields, values);

                this.counter++;

                // add dirty check on new fields
                array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

                return rubric;
            },

            getDocTableStructure: function() {
                return [
                    { field: 'label', name: message.get("uvp.form.table.docs.title") + "*", width: '310px', editable: true },
                    { field: 'link', name: message.get("uvp.form.table.docs.link") + "*", width: '260px', editable: false, formatter: Formatters.LinkCellFormatter },
                    // { field: 'type', name: message.get("uvp.form.table.docs.type"), width: '50px', editable: true }, // do not display type (#1081)
                    // { field: 'size', name: message.get("uvp.form.table.docs.size") + "*", width: '60px', editable: true, formatter: Formatters.MegaBytesCellFormatter },
                    { field: 'expires', name: message.get("uvp.form.table.docs.expires"), width: '78px', type: Editors.DateCellEditorToString, minDate: new Date(), editable: true, formatter: Formatters.DateCellFormatter }
                ];
            },

            openPhase: function(rubric) {
                setTimeout(function() { IgeEvents.toggleFields(rubric, "showAll"); }, 500);
            },

            addDeletePhaseButton: function(rubric, counter) {
                var self = this;
                var delButton = new Button({
                    label: message.get("uvp.form.deletePhase"),
                    "class": "right optional",
                    onClick: function() {
                        dialog.show(message.get("uvp.form.deletePhase"), message.get("uvp.form.deletePhase.confirmText"), dialog.WARN, [
                            {
                                caption: "Ja",
                                action: function() {
                                    // delete all widgets of this phase
                                    var phase = array.filter(self.phases, function(phase) { return phase.pos === counter; })[0];
                                    self.removePhase(rubric, phase);
                                }
                            }, {
                                caption: "Nein",
                                action: function() { }
                            }
                        ]);

                    }
                });
                construct.place(delButton.domNode, rubric);

                // layout fix!
                construct.place(construct.toDom("<div class='clear'></div>"), rubric);
            },

            removePhase: function(rubric, phase) {
                array.forEach(phase.fields, function(fieldObj) {
                    fieldObj.field.destroyRecursive();
                });

                // delete dom of phase
                construct.destroy(rubric);

                // remove phase from array
                var pos = array.indexOf(this.phases, phase);
                this.phases.splice(pos, 1);

                // TODO: remove dirty checks on removed fields
            },

            addValuesToPhase: function(phaseFields, values) {
                if (!values) return;

                var addWidgets = require("ingrid/IgeActions");

                array.forEach(values, function(valueObj) {
                    var key = valueObj[0].identifier;
                    var fieldWidgetObj = array.filter(phaseFields, function(item) { return item.key === key; })[0];
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

            addValidatorForTextarea: function(widget) {
                widget.validate = function() {
                    return this.validator();
                };
                widget.validator = function() {
                    if (this.required && this.get("value") === "") {
                        domClass.add(this.domNode, "importantBackground");
                        return false;
                    } else {
                        domClass.remove(this.domNode, "importantBackground");
                        return true;
                    }
                };
            },

            _createPhaseButton: function() {
                var self = this;
                return new Button({
                    label: message.get("uvp.form.addPhase"),
                    onClick: function() {
                        var buttons = [];
                        if (self.availablePhases.indexOf(1) !== -1) {
                            buttons.push({
                                caption: message.get("uvp.form.dialog.addPhase.phase1"),
                                action: function() {
                                    var rubric = lang.hitch(self, self.addPhase1)();
                                    self.openPhase(rubric);
                                }
                            });
                        }
                        if (self.availablePhases.indexOf(2) !== -1) {
                            buttons.push({
                                caption: message.get("uvp.form.dialog.addPhase.phase2"),
                                action: function() {
                                    var rubric = lang.hitch(self, self.addPhase2)();
                                    self.openPhase(rubric);
                                }
                            });
                        }
                        if (self.availablePhases.indexOf(3) !== -1) {
                            buttons.push({
                                caption: message.get("uvp.form.dialog.addPhase.phase3"),
                                action: function() {
                                    var rubric = lang.hitch(self, self.addPhase3)();
                                    self.openPhase(rubric);
                                }
                            });
                        }
                        dialog.show(message.get("uvp.form.dialog.addPhase.title"), message.get("uvp.form.dialog.addPhase.text"), dialog.INFO, buttons, 500);
                    }
                });
            },

            handleToggledState: function() {
                var self = this;

                // remember toggled state of rubrics before save
                topic.subscribe("/onBeforeObjectSave", function() {
                    self.expandedPhases = [];
                    query(".rubric.phase").forEach(function(rubric, index) {
                        if (domClass.contains(rubric, "expanded")) {
                            self.expandedPhases.push(index);
                        }
                    });
                });

                // remove toggled state if we load a new document
                topic.subscribe("/loadRequest", function() {
                    self.expandedPhases = [];
                });
            },

            addUploadLink: function(tableId) {
                var table = registry.byId(tableId);
                if (table) {
                    // upload base path, regexp filter must correspond to FileSystemStorage.ILLEGAL_PATH_CHARS in FileSystemStorage.java
                    var basePath = Catalog.catalogData.plugId.replace(/[<>?\":|\\*]/, "_")+"/"+currentUdk.uuid;

                    // create uploader instance
                    var uploader = new UploadWidget({
                        uploadUrl: this.uploadUrl
                    });

                    // upload handler
                    var handleUploads = lang.hitch(this, function(uploads, basePath) {
                        // get existing table data
                        var rows = table.data;

                        // create map from uploads array
                        var uploadMap = {};
                        array.forEach(uploads, function(upload) {
                            uploadMap[upload.uri] = upload;
                        });
                        // update existing uploads
                        array.forEach(rows, function(row) {
                            var uri = row.link;
                            if (uri && uploadMap[uri]) {
                                var upload = uploadMap[uri];
                                row = getRowData(row, upload, basePath);
                                delete uploadMap[uri];
                            }
                        });
                        // map back to list
                        uploads = [];
                        for (var uri in uploadMap) {
                            uploads.push(uploadMap[uri]);
                        }

                        // fill existing rows without link
                        array.forEach(rows, function(row) {
                            var uri = row.link;
                            if (!uri) {
                                var upload = uploads.shift();
                                if (upload) {
                                    row = getRowData(row, upload, basePath);
                                }
                            }
                        });

                        // add remaining uploads
                        array.forEach(uploads, function(upload) {
                            rows.push(getRowData({}, upload, basePath));
                        });

                        // store changes
                        UtilStore.updateWriteStore(tableId, rows);
                    });

                    var getFiles = function(phases, flat) {
                        var files = flat ? [] : {};
                        for (var phase in phases) {
                            if (!flat) {
                                files[phase] = {};
                            }
                            var fields = phases[phase].fields;
                            for (var field in fields) {
                                var key = fields[field].key;
                                var data = fields[field].field.data;
                                if (data) {
                                    if (!flat) {
                                        files[phase][key] = [];
                                    }
                                    for (var row in data) {
                                        var file = decodeURI(data[row].link);
                                        if (flat) {
                                            if (array.indexOf(files, file) === -1) {
                                                files.push(file)
                                            }
                                        }
                                        else {
                                            files[phase][key].push(file);
                                        }
                                    }
                                }
                            }
                        }
                        return files;
                    };

                    var getRowData = function(row, data, basePath) {
                        if (!row.label || row.label.length === 0) {
                            var file = data.uri;
                            
                            // generate basepath ready for comparison with 
                            var basePathWithoutLeadingSlash = basePath;
                            // strip leading slash because the file name comes in without trainling slash
                            if (basePath.indexOf("/") == 0) {
                            	basePathWithoutLeadingSlash = basePath.substring(1, basePath.length);
                            }
                            var fileRel;
                            if (file.indexOf(basePathWithoutLeadingSlash) == 0) {
                                // uploaded file, cut base path of uploaded file, keep hierarchy structure from extracted ZIPs
                            	// strip slash following base path 
                            	fileRel = file.substring(basePathWithoutLeadingSlash.length + 1, file.length);
                            } else {
                                // link, get the last path of the link
                            	fileRel = file.substring(file.lastIndexOf('/')+1, file.length);
                            }
                            var lastDotPos = fileRel.lastIndexOf(".");
                            var name = fileRel.substring(0,
                                    lastDotPos === -1 ? file.length : lastDotPos);
                            row.label = decodeURI(name);
                        }
                        row.link = data.uri;
                        row.type = data.type;
                        row.size = data.size;
                        return row;
                    };

                    // create interface
                    var inactiveHint = construct.create("span", {
                        id: tableId + "_uploadHint",
                        'class': "right",
                        innerHTML: "Dokument-Upload inaktiv",
                        style: {
                            cursor: "help"
                        },
                        onclick: function(e) {
                            dialog.showContextHelp(e, "Die Upload Funktionalität steht nach dem ersten Speichern zur Verfügung.");
                        }
                    }, table.domNode.parentNode, "before");

                    var linkContainer = construct.create("span", {
                        "class": "functionalLink",
                        innerHTML: "<img src='img/ic_fl_popup.gif' width='10' height='9' alt='Popup' />"
                    }, table.domNode.parentNode, "before");

                    construct.create("a", {
                        id: tableId + "_uploadLink",
                        title: "Dokument-Upload [Popup]",
                        innerHTML: "Dokument-Upload",
                        style: {
                            cursor: "pointer"
                        },
                        onclick: lang.hitch(this, function() {
                            var files = getFiles(this.phases, true);
                            uploader.open(basePath, files).then(lang.hitch(this, function(uploads) {
                                handleUploads(uploads, basePath);
                            }));
                        })
                    }, linkContainer);

                    // link state handler
                    var setLinkState = function() {
                        var isActive = currentUdk.uuid !== "newNode";
                        if (isActive) {
                            domClass.remove(linkContainer, "hide");
                            domClass.add(inactiveHint, "hide");
                        }
                        else {
                            domClass.remove(inactiveHint, "hide");
                            domClass.add(linkContainer, "hide");
                        }
                    };

                    // adapt upload interface to currentUdk state
                    setLinkState();
                    this.own(
                        topic.subscribe("/onBeforeObjectPublish", function() {
                            setLinkState();
                        })
                    );
                }
            },

            hideAddButton: function() {
                this._buttonAddHide = true;
                domClass.add(this.addButton.domNode, "hide")
            },

            showAddButton: function() {
                this._buttonAddHide = false;
                domClass.remove(this.addButton.domNode, "hide")
            }
        });
    });
