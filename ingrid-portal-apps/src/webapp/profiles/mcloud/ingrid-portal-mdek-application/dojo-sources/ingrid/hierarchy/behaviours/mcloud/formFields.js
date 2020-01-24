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
define(["dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/aspect",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-style",
    "dojo/dom-construct",
    "dojo/query",
    "dojo/topic",
    "dijit/registry",
    "ingrid/dialog",
    "ingrid/message",
    "ingrid/layoutCreator",
    "ingrid/IgeEvents",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/hierarchy/validation",
    "ingrid/utils/Catalog",
    "ingrid/widgets/upload/UploadWidget",
    "ingrid/utils/Store"
], function(declare, array, lang, aspect, dom, domClass, domStyle, construct, query, topic, registry, dialog, message, creator, IgeEvents, Editors, Formatters, dirty, validation, Catalog, UploadWidget, UtilStore) {
    return declare(null, {
        title: "Formularfelder",
        description: "Hier werden die zusätzlichen Felder im Formular erzeugt sowie überflüssige ausgeblendet.",
        defaultActive: true,
        category: "mcloud",
        prefix: "mcloud_",
        uploadUrl: "rest/document",

        //params: [{id: "categoryCodelist", label: "Codelist (Kategorie)", "default": 9000}],
        run: function() {

            // rename default fields
            query("#general .titleBar .titleCaption")
                .addContent(message.get("mcloud.form.general"), "only")
                .style("cursor", "default")
                .attr('onclick', undefined);
            query("#general .titleBar").attr("title", message.get("mcloud.form.general.tooltip"));

            // make Geothesaurus optional and remove validation after initialization
            require("ingrid/hierarchy/objectLayout").deferredCreation.then(function() {
                domClass.replace("uiElementN006", "outer optional");
                domClass.add('uiElement5030', 'show');
                domClass.remove('uiElement5030', ['required', 'optional']);
                validation.spatialRefAdminUnitPublishable = function() {};
                spatialRefAdminUnitPublishable = function() {};
            });

            // do not override my address title
            IgeEvents.setGeneralAddressLabel = function() { };

            // disable editing the address table and automatically set point of contact type
            UtilGrid.getTable("generalAddress").options.editable = false;

            var handleAddressAdd = function() {
                var publisher = UtilSyslist.getSyslistEntryName(505, 10);
                UtilGrid.addTableDataRow("generalAddress", {nameOfRelation: publisher});
            };
            topic.subscribe("/onBeforeDialogAccept/AddressesFromTree", handleAddressAdd);
            topic.subscribe("/onBeforeDialogAccept/Addresses", handleAddressAdd);

            // hide preview toolbar button
            domClass.add("toolbarBtnPrintDoc", "hide");

            this.hideDefaultFieldsFromObjects();
            this.setDefaultFieldVisibility();

            var defCreateFields = this.createFields();

            // TODO: additional fields according to #490 and #473

            var self = this;
            topic.subscribe("/onObjectClassChange", function(clazz) {
                self.prepareDocument(clazz);
            });

            return defCreateFields;

        },

        prepareDocument: function(classInfo) {
            console.log("Prepare document for class: ", classInfo);

        },

        hideDefaultFieldsFromObjects: function() {
            domStyle.set("widget_objectName", "width", "550px");

            domClass.add(dom.byId("objectClassLabel").parentNode, "hide");
            domClass.add(dom.byId("objectOwnerLabel").parentNode, "hide");

            domClass.add(registry.byId("toolbarBtnISO").domNode, "hide");

            // general
            domClass.add("uiElement1001", "hide");
            domClass.add("uiElement5000", "hide");
            domClass.add("uiElement5100", "hide");
            domClass.add("uiElement5105", "hide");
            domClass.add("uiElement6005", "hide");
            domClass.add("uiElement6010", "hide");

            // spatial
            domClass.add("uiElement3500", "hide");
            domClass.add("uiElementN010", "hide");
            domClass.add("uiElement1140", "hide");

            // time
            // domClass.add("uiElement5030", "hide");
            domClass.add("uiElement1250", "hide");
            domClass.add("uiElement1220", "hide");
            // domClass.add("uiElement1240", "hide");
            domClass.add("uiElement1230", "hide");

            // links
            // domClass.add("uiElementN018", "hide");

            // hide all rubrics not needed
            query(".rubric", "contentFrameBodyObject").forEach(function (item) {
                if (item.id !== "general" && item.id !== "spatialRef" && item.id !== "timeRef") {
                    domClass.add(item, "hide");
                }
            });
        },

        setDefaultFieldVisibility: function() {
            // time
            domClass.add('uiElementN011', 'show');
            domClass.add('uiElementN011', 'show');
            domClass.add('uiElement1240', 'show');
        },

        createFields: function() {
            var rubric = "mcloud";
            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            var rubricContainer = creator.createRubric({
                id: "mcloud",
                label: message.get("mcloud.form.mcloudFields"),
                help: message.get("mcloud.form.mcloudFields.toolTip") });
            construct.place(rubricContainer, "general", "after");

            /*
             * Terms of Use
             */
            var id = "mcloudTermsOfUse";
            construct.place(
                creator.createDomTextarea({
                    id: id,
                    name: message.get("mcloud.form.termsOfUse"),
                    help: message.get("mcloud.form.termsOfUse.helpMessage"),
                    visible: "show",
                    style: "width:100%" }),
                rubric);
            newFieldsToDirtyCheck.push(id);
            var widgetTermsOfUse = registry.byId(id);
            additionalFields.push(widgetTermsOfUse);

            /*
             * Data Holding Place --> in Addresses
             */
            /*id = "mclouddataHoldingPlace";
            construct.place(
                creator.createDomSelectBox({
                    id: id, name: message.get("mcloud.form.dataHoldingPlace"), help: message.get("mcloud.form.dataHoldingPlace.helpMessage"),
                    listEntries: [{id: "a", value: "erster eintrag"}],
                    visible: "required", style: "width:100%" }),
                rubric);
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));*/

            /*
             * Category
             */
            id = "mcloudCategory";
            newFieldsToDirtyCheck.push(id);
            creator.createDomDataGrid({
                    id: id,
                    name: message.get("mcloud.form.category"),
                    help: message.get("mcloud.form.category.helpMessage"),
                    isMandatory: true,
                    visible: "optional",
                    rows: "2",
                    forceGridHeight: false,
                    style: "width:50%"
                },
                [{
                    field: "category",
                    name: message.get("mcloud.form.category"),
                    width: '708px',
                    type: Editors.SelectboxEditor,
                    options: ["Bahn", "Wasserstraßen und Gewässer", "Infrastruktur", "Klima und Wetter", "Luft- und Raumfahrt", "Straßen"],
                    values: ["railway", "waters", "infrastructure", "climate", "aviation", "roads"],
                    editable: true,
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true
                }],
                rubric
            );
            var categoryWidget = registry.byId(id);
            categoryWidget.reinitLastColumn(true);
            domClass.add(categoryWidget.domNode, "hideTableHeader");
            additionalFields.push(categoryWidget);

            /*
             * DCAT Category
             */
            id = "mcloudDcatCategory";
            newFieldsToDirtyCheck.push(id);
            creator.createDomDataGrid({
                    id: id,
                    name: message.get("mcloud.form.dcatcategory"),
                    help: message.get("mcloud.form.dcatcategory.helpMessage"),
                    isMandatory: true,
                    visible: "show",
                    rows: "2",
                    forceGridHeight: false,
                    style: "width:50%"
                },
                [{
                    field: 'dcatCategory',
                    name: message.get("mcloud.form.dcatcategory"),
                    width: '708px',
                    type: Editors.SelectboxEditor,
                    options: [
                        "Bevölkerung und Gesellschaft",
                        "Bildung, Kultur und Sport",
                        "Energie",
                        "Gesundheit",
                        "Internationale Themen",
                        "Justiz, Rechtssystem und öffentliche Sicherheit",
                        "Landwirtschaft, Fischerei, Forstwirtschaft und Nahrungsmittel",
                        "Regierung und öffentlicher Sektor",
                        "Regionen und Städte",
                        "Umwelt",
                        "Verkehr",
                        "Wirtschaft und Finanzen",
                        "Wissenschaft und Technologie"
                    ],
                    values: ["SOCI", "EDUC", "ENER", "HEAL", "INTR", "JUST", "AGRI", "GOVE", "REGI", "ENVI", "TRAN", "ECON", "TECH"],
                    editable: true,
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true
                }],
                rubric
            );
            var dcatCategoryWidget = registry.byId(id);
            dcatCategoryWidget.reinitLastColumn(true);
            domClass.add(dcatCategoryWidget.domNode, "hideTableHeader");
            additionalFields.push(dcatCategoryWidget);

            /*
             * Downloads
             */
            id = "mcloudDownloads";
            var structure = [
                {
                    field: 'title',
                    name: 'Titel',
                    width: '150px',
                    editable: true
                },
                {
                    field: 'link',
                    name: 'Link*',
                    editable: false,
                    width: '200px',
                    formatter: Formatters.LinkCellFormatter
                },
                {
                    field: 'sourceType',
                    name: 'Typ*',
                    type: Editors.SelectboxEditor,
                    editable: true,
                    // listId: codelist,
                    options: ["API", "AtomFeed", "Dateidownload", "FTP", "Portal", "SOS", "WCS", "WFS", "WMS", "WMTS"],
                    values:  ["api", "atomFeed", "download", "ftp", "portal", "sos", "wcs", "wfs", "wms", "wmts"],
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true,
                    width: '70px'
                },
                /*{
                    field: 'dataType',
                    name: 'Datentyp',
                    type: Editors.SelectboxEditor,
                    editable: true,
                    // listId: codelist,
                    options: ["Rasterdaten", "Sensordaten", "Tabelle", "Text", "Vektordaten"],
                    values:  ["Rasterdaten", "Sensordaten", "Tabelle", "Text", "Vektordaten"],
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true,
                    width: '80px'
                },
                {
                    field: 'availability',
                    name: 'Verfügbarkeit',
                    type: Editors.SelectboxEditor,
                    editable: true,
                    // listId: codelist,
                    options: ["Darstellungsdienst", "Dateidownload", "Datendienst"],
                    values:  ["Darstellungsdienst", "DateiDownload", "Datendienst"],
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true,
                    width: '110px'
                },*/
                {
                    field: 'dateFormat',
                    name: 'Datenformat',
                    type: Editors.ComboboxEditor,
                    editable: true,
                    // listId: codelist,
                    options: ["ASCII","Binär","BUFR","CAP","CSV","DATEX II","ECDIS","Excel","GeoDB","GeoJSON","GeoRSS","GeoTIFF","GML","GPX","GRIB","GRIB2","GTFS","HAFAS","HTML","JPEG2000","JSON","KL","KML","KMZ","netCDF","NetCDF","OpenDrive","OVL","PNG","Shapefile","SHDL90","TCX","TIFF","TRIAS","TSV","TXT","XML","XML/JSON","ZIP","ZRXP"],
                    values:  ["ASCII","Binär","BUFR","CAP","CSV","DATEX II","ECDIS","Excel","GeoDB","GeoJSON","GeoRSS","GeoTIFF","GML","GPX","GRIB","GRIB2","GTFS","HAFAS","HTML","JPEG2000","JSON","KL","KML","KMZ","netCDF","NetCDF","OpenDrive","OVL","PNG","Shapefile","SHDL90","TCX","TIFF","TRIAS","TSV","TXT","XML","XML/JSON","ZIP","ZRXP"],
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true
                }
            ];
            newFieldsToDirtyCheck.push(id);
            creator.createDomDataGrid({
                id: id,
                name: message.get("mcloud.form.downloads"),
                help: message.get("mcloud.form.downloads.helpMessage"),
                isMandatory: true,
                visible: "optional",
                rows: "1",
                forceGridHeight: false,
                style: "width:100%" },
                structure, rubric
            );
            var downloadsTable = registry.byId(id);
            this.addUploadLink(id);
            additionalFields.push(downloadsTable);
            downloadsTable.reinitLastColumn(true);

            /*
             * License --> Codelist 6500 in Selectbox
             */
            id = "mcloudLicense";
            construct.place(
                creator.createDomSelectBox({
                    id: id,
                    name: message.get("mcloud.form.license"),
                    help: message.get("mcloud.form.license.helpMessage"),
                    useSyslist: "6500",
                    isExtendable: true,
                    visible: "required",
                    style: "width:100%" }),
                rubric);
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));


            /*
             * Source Note
             */
            id = "mcloudSourceNote";
            construct.place(
                creator.createDomTextarea({
                    id: id,
                    name: message.get("mcloud.form.sourceNote"),
                    help: message.get("mcloud.form.sourceNote.helpMessage"),
                    visible: "show",
                    style: "width:100%" }),
                rubric);
            newFieldsToDirtyCheck.push(id);
            var widgetSourceNote = registry.byId(id);
            additionalFields.push(widgetSourceNote);

            /*
             * Data Type (optional)
             */
            // id = "mcloudDataType";

            /*
             * Availability (optional)
             */
            // id = "mcloudAvailability";

            /*
             * File Format (optional)
             */
            // id = "mcloudFileFormat";

            /*
             * PeriodOfTime, temporal(!) (DateOfRelevance) --> Zeitraum "Durch die Ressource abged..."
             */
            // id = "mcloudDateOfRelevance";

            /*
             * Date Updated --> Zeitbezug der Ressource
             * TODO: allow only certain types
             */
            // id = "mcloudDateUpdated";

            /*
             * Realtime Data --> Checkbox / Periodizität(!)->kontinuierlich
             */
            // id = "mcloudRealTimeData";

            /*
             * mFUND Projekt
             */
            id = "mcloudMFundProject";
            construct.place(
                creator.createDomTextbox({
                    id: id,
                    style: "width: 50%",
                    name: message.get("mcloud.form.mFundProject"),
                    help: message.get("mcloud.form.mFundProject.helpMessage"),
                    visible: "show"}),
                rubric);
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            /*
             * Förderkennzeichen mFund
             */
            id = "mcloudMFundFKZ";
            construct.place(
                creator.createDomTextbox({
                    id: id,
                    style: "width: 50%",
                    name: message.get("mcloud.form.mFundFKZ"),
                    help: message.get("mcloud.form.mFundFKZ.helpMessage"),
                    visible: "show"}),
                rubric);
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

            return registry.byId("mcloudLicense").promiseInit;
        },

        addUploadLink: function(tableId) {
            var table = registry.byId(tableId);

            if (table) {

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
                            getRowData(row, upload, basePath);
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
                                getRowData(row, upload, basePath);
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
                    if (!row.title || row.title.length === 0) {
                        var file = data.uri;

                        // generate basepath ready for comparison with
                        var basePathWithoutLeadingSlash = basePath;
                        // strip leading slash because the file name comes in without trainling slash
                        if (basePath.indexOf("/") === 0) {
                            basePathWithoutLeadingSlash = basePath.substring(1, basePath.length);
                        }
                        var fileRel;
                        if (file.indexOf(basePathWithoutLeadingSlash) === 0) {
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
                        row.title = decodeURI(name);
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
                        // upload base path, regexp filter must correspond to FileSystemStorage.ILLEGAL_PATH_CHARS in FileSystemStorage.java
                        var basePath = Catalog.catalogData.plugId.replace(/[<>?\":|\\*]/, "_") + "/" + currentUdk.uuid;

                        var files = getFiles(this.phases, true);
                        uploader.open(basePath, files).then(lang.hitch(this, function(uploads) {
                            handleUploads(uploads, basePath);
                        }));
                    })
                }, linkContainer);


                // adapt upload interface to currentUdk state
                require(["ingrid/IgeActions"], function(igeAction) {

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

                    aspect.after(igeAction, "onAfterLoad", function () {
                        setLinkState();
                    });
                    aspect.after(igeAction, "onAfterSave", function () {
                        setLinkState();
                    });
                    topic.subscribe("/onAfterObjectCreate", function() {
                        setLinkState();
                    });

                });
            }
        },


    })();
});
