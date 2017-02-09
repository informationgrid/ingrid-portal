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
    "ingrid/hierarchy/dirty",
    "ingrid/utils/Store",
    "./upload/UploadWidget",
    "dojo/NodeList-traverse",
], function (declare, array, lang, construct, domClass, query, topic, _WidgetBase, registry, Button, DateTextBox, _FormValueWidget,
    creator, dialog, message, IgeEvents, CustomGrid, Editors, Formatters, dirty, UtilStore, UploadWidget) {

        return declare("UVPPhases", [_WidgetBase], {

            // phases consists of 0 or n containers, which itself contain additional fields
            // e.g.: [ { key: "öffentl. Auslegung", fields: [addField11, addField12]}, {key: "zulassungs.", fields: [addField21, addField22]}, ... ]
            // so each item of phases is a container of type 
            //   - 1) "Öffentliche Auslegung"
            //   - 2) "Erörterungstermin"
            //   - 3) "Zulassungsentscheidung"
            phases: [],

            // define which phases can be created through the dialog
            availablePhases: [1,2,3],

            addButton: null,

            // handle data as complex value
            valueAsTableData: true,

            counter: 1,

            // TODO put URLs into configuration?
            uploadUrl: "rest/document",
            downloadBaseUrl: "http://localhost:8080/ingrid-portal-mdek-application/rest/document/",

            buildRendering: function () {
                this.domNode = construct.create("div");

                var addWidgets = require("ingrid/IgeActions");
                addWidgets.additionalFieldWidgets.push(this);

                var self = this;
                this.addButton = new Button({
                    label: message.get("uvp.form.addPhase"),
                    onClick: function () {
                        var buttons = [];
                        if (self.availablePhases.indexOf(1) !== -1) {
                            buttons.push({
                                caption: message.get("uvp.form.dialog.addPhase.phase1"),
                                action: function () {
                                    var rubric = lang.hitch(self, self.addPhase1)();
                                    self.openPhase(rubric);
                                }
                            });
                        }
                        if (self.availablePhases.indexOf(2) !== -1) {
                            buttons.push({
                                caption: message.get("uvp.form.dialog.addPhase.phase2"),
                                action: function () {
                                    var rubric = lang.hitch(self, self.addPhase2)();
                                    self.openPhase(rubric);
                                }
                            });
                        }
                        if (self.availablePhases.indexOf(3) !== -1) {
                            buttons.push({
                                caption: message.get("uvp.form.dialog.addPhase.phase3"),
                                action: function () {
                                    var rubric = lang.hitch(self, self.addPhase3)();
                                    self.openPhase(rubric);
                                }
                            });
                        }
                        dialog.show(message.get("uvp.form.dialog.addPhase.title"), message.get("uvp.form.dialog.addPhase.text"), dialog.INFO, buttons, 500);
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
                var newFieldsToDirtyCheck = [];
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
                newFieldsToDirtyCheck.push( idDateFrom, idDateTo );
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
                
                var id = "legitimacyDocs_" + counter;
                newFieldsToDirtyCheck.push( id );
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.legitimacyDocs"), help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
                phaseFields.push({ key: "legitimacyDocs", field: registry.byId(id) });

                // TODO: at least one document validation
                
                /**
                 * UVP-Bericht nach § 6 UVPG
                 */
                id = "reportArticle6Docs_" + counter;
                newFieldsToDirtyCheck.push( id );
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.reportArticle6Docs"), help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
                phaseFields.push({ key: "reportArticle6Docs", field: registry.byId(id) });

                // TODO: at least one document validation
                
                /**
                 * Berichte und Empfehlungen
                 */
                id = "reportsRecommendationsDocs_" + counter;
                newFieldsToDirtyCheck.push( id );
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.reportsRecommendationsDocs"), help: "...", isMandatory: false, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
                phaseFields.push({ key: "reportsRecommendationsDocs", field: registry.byId(id) });

                /**
                 * Weitere Unterlagen
                 */
                id = "moreDocs_" + counter;
                newFieldsToDirtyCheck.push( id );
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase1.moreDocs"), help: "...", isMandatory: false, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
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

                // add dirty check on new fields
                array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

                return rubric;
            },

            addPhase2: function (values) {
                this.removePhaseInfo();
                var phaseFields = [];
                var newFieldsToDirtyCheck = [];
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
                newFieldsToDirtyCheck.push( id );
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase2.considerationDate"), help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    structure, rubric);
                phaseFields.push({ key: "considerationDate", field: registry.byId(id) });
                
                /**
                 * Bekanntmachungstext
                 */
                id = "considerationDateDescription_" + counter;
                newFieldsToDirtyCheck.push( id );
                creator.addToSection(rubric, creator.createDomTextarea({id: id, name: message.get("uvp.form.phase2.considerationDateDescription"), help: "...", isMandatory: false, visible: "optional", rows: 10, style: "width:100%"}));
                var textarea = registry.byId(id);
                this.addValidatorForTextarea(textarea);
                phaseFields.push({ key: "considerationDateDescription", field: textarea });

                /**
                 * Bekanntmachung
                 */
                id = "considerationDocs_" + counter;
                newFieldsToDirtyCheck.push( id );
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase2.considerationDocs"), help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
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

                // add dirty check on new fields
                array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

                return rubric;
            },

            addPhase3: function (values) {
                this.removePhaseInfo();
                var phaseFields = [];
                var newFieldsToDirtyCheck = [];
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
                newFieldsToDirtyCheck.push( id );
                creator.addToSection(rubric, creator.createDomDatebox({ id: id, name: message.get("uvp.form.phase3.approvalDate"), help: "...", isMandatory: true, visible: "optional", style: "width:50%" }));
                phaseFields.push({ key: "approvalDate", field: registry.byId(id) });

                construct.place(construct.toDom("<div class='clear'></div>"), rubric);

                /**
                 * Bekanntmachungstext der Zulassungsentscheidung
                 */
                id = "approvalDescription_" + counter;
                newFieldsToDirtyCheck.push( id );
                creator.addToSection(rubric, creator.createDomTextarea({ id: id, name: message.get("uvp.form.phase3.approvalDescription"), help: "...", isMandatory: true, visible: "optional", rows: 10, style: "width:100%" }));
                var textarea = registry.byId(id);
                this.addValidatorForTextarea(textarea);
                phaseFields.push({ key: "approvalDescription", field: textarea });

                /**
                 * Zulassungsdokument
                 */
                id = "approvalDocs_" + counter;
                newFieldsToDirtyCheck.push( id );
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase3.approvalDocs"), help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
                phaseFields.push({ key: "approvalDocs", field: registry.byId(id) });

                /**
                 * Planungsunterlagen
                 */
                id = "designDocs_" + counter;
                newFieldsToDirtyCheck.push( id );
                creator.createDomDataGrid({ id: id, name: message.get("uvp.form.phase3.designDocs"), help: "...", isMandatory: true, visible: "optional", rows: "3", forceGridHeight: false, style: "width:100%" },
                    this.getDocTableStructure(), rubric);
                this.addUploadLink(id);
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

                // add dirty check on new fields
                array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

                return rubric;
            },

            getDocTableStructure: function() {
                return [
                    { field: 'label', name: 'Titel', width: '290px', editable: true },
                    { field: 'link', name: 'Link', width: '200px', editable: false, formatter: lang.partial(Formatters.LinkCellFormatter, this.downloadBaseUrl) },
                    { field: 'type', name: 'Typ', width: '50px', editable: true },
                    { field: 'size', name: 'Größe', width: '60px', editable: false, formatter: Formatters.BytesCellFormatter },
                    { field: 'expires', name: 'Gültig bis', width: '78px', type: Editors.DateCellEditorToString, editable: true, formatter: Formatters.DateCellFormatter }
                ];
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

                // TODO: remove dirty checks on removed fields
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
            },

            addUploadLink: function (tableId) {
                var table = registry.byId(tableId);
                if (table) {
                    // create uploader instance
                    var uploader = new UploadWidget({
                        uploadUrl: this.uploadUrl
                    });

                    // upload handler
                    var handleUploads = lang.hitch(this, function (uploads) {
                        // make upload URIs relative for storage
                        // NOTE: the server response contains absolute download URIs
                        array.forEach(uploads, function (upload) {
                            upload.uri = upload.uri.replace(this.downloadBaseUrl, '');
                        }, this);

                        // get existing table data
                        var rows = table.data;

                        // create map from uploads array
                        var uploadMap = {};
                        array.forEach(uploads, function (upload) {
                            uploadMap[upload.uri] = upload;
                        });
                        // update existing uploads
                        array.forEach(rows, function (row) {
                            var uri = row.link;
                            if (uri && uploadMap[uri]) {
                                var upload = uploadMap[uri];
                                row.type = upload.type;
                                row.size = upload.size;
                                delete uploadMap[uri];
                            }
                        });
                        // map back to list
                        uploads = [];
                        for (var uri in uploadMap) {
                            uploads.push(uploadMap[uri]);
                        }

                        // fill existing rows without link
                        array.forEach(rows, function (row) {
                            var uri = row.link;
                            if (!uri) {
                                var upload = uploads.shift();
                                if (upload) {
                                    row.link = upload.uri;
                                    row.type = upload.type;
                                    row.size = upload.size;
                                }
                            }
                        });

                        // add remaining uploads
                        array.forEach(uploads, function (upload) {
                            rows.push({
                                link: upload.uri,
                                type: upload.type,
                                size: upload.size
                            });
                        });

                        // store changes
                        UtilStore.updateWriteStore(tableId, rows);
                    });

                    // create interface
                    var inactiveHint = construct.create("span", {
                        id: tableId+"_uploadHint",
                        'class': "right",
                        innerHTML: "Dokument-Upload inaktiv",
                        style: {
                            cursor: "help"
                        },
                        onclick: function (e) {
                            dialog.showContextHelp(e, "Die Upload Funktionalität steht nach dem ersten Speichern zur Verfügung.");
                        }
                    }, table.domNode.parentNode, "before");
                    var linkContainer = construct.create("span", {
                        "class": "functionalLink",
                        innerHTML: "<img src='img/ic_fl_popup.gif' width='10' height='9' alt='Popup' />"
                    }, table.domNode.parentNode, "before");
                    construct.create("a", {
                        id: tableId+"_uploadLink",
                        title: "Dokument-Upload [Popup]",
                        innerHTML: "Dokument-Upload",
                        style: {
                            cursor: "pointer"
                        },
                        onclick: lang.hitch(this, function () {
                            var path = currentUdk.uuid;
                            uploader.open(path).then(lang.hitch(this, function (uploads) {
                                handleUploads(uploads);
                            }));
                        })
                    }, linkContainer);

                    // link state handler
                    var setLinkState = function () {
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
                        topic.subscribe("/onBeforeObjectPublish", function () {
                            setLinkState();
                        })
                    );
                }
            }
        });
    });