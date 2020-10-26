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
], function (declare, array, lang, aspect, dom, domClass, domStyle, construct, query, topic, registry, dialog, message, creator, IgeEvents, Editors, Formatters, dirty, validation, Catalog, UploadWidget, UtilStore) {
    return declare(null, {
        title: "Formularfelder",
        description: "Hier werden die zusätzlichen Felder im Formular erzeugt",
        defaultActive: true,
        category: "HMDK",
        prefix: "hmdk_",
        uploadUrl: "rest/document",

        //params: [{id: "categoryCodelist", label: "Codelist (Kategorie)", "default": 9000}],
        run: function () {

            var defCreateFields = this.createFields();

            var self = this;
            topic.subscribe("/onObjectClassChange", function (clazz) {
                self.prepareDocument(clazz);
            });

            return defCreateFields;

        },

        prepareDocument: function (classInfo) {
            console.log("Prepare document for class: ", classInfo);
        },

        hideDefaultFieldsFromObjects: function () {

        },

        setDefaultFieldVisibility: function () {
            // time TODO checkbox and table
            //domClass.add('uiElementN011', 'show');
        },

        createFields: function () {
            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            /*
             * Veröffentlichung gemäß HmbTG
             */
            var id = "publicationHmbTG";
            construct.place(
                creator.createDomCheckbox({
                    id: id,
                    name: "Veröffentlichung gemäß HmbTG",
                    help: "Veröffentlichung gemäß HmbTG",
                }), "general");
            newFieldsToDirtyCheck.push(id);
            var widgetPublicationHmbTG = registry.byId(id);
            additionalFields.push(widgetPublicationHmbTG);


            /*
             * Informationsgegenstand
             */
            id = "Informationsgegenstand"
            newFieldsToDirtyCheck.push(id);
            creator.createDomDataGrid({
                    id: id,
                    name: "Informationsgegenstand",
                    help: "Informationsgegenstand",
                    visible: "optional",
                    rows: "1"
                },
                [{
                    field: "informationHmbTG",
                    name: "Informationsgegenstand",
                    width: '691px',
                    type: Editors.SelectboxEditor,
                    options: ["Senatsbeschlüsse",
                        "Mitteilungen des Senats",
                        "Öffentliche Beschlüsse",
                        "Verträge der Daseinsvorsorge",
                        "Verwaltungspläne",
                        "Verwaltungsvorschriften",
                        "Statistiken und Tätigkeitsberichte",
                        "Gutachten und Studien",
                        "Geodaten",
                        "Umweltdaten",
                        "Baumkataster",
                        "Öffentliche Pläne",
                        "Baugenehmigungen",
                        "Subventionen und Zuwendungen",
                        "Unternehmensdaten",
                        "Verträge von öffentl. Interesse",
                        "Dienstanweisungen",
                        "vergleichbare Informationen von öffentl. Interesse",
                        "Veröffentlichungspflicht außerhalb HmbTG",
                        "Ohne gesetzliche Verpflichtung"],
                    values:
                        ["hmbtg_01_senatsbeschluss",
                            "hmbtg_02_mitteilung_buergerschaft",
                            "hmbtg_03_beschluesse_oeffentliche_sitzung",
                            "hmbtg_04_vertraege_daseinsvorsorge",
                            "hmbtg_05_verwaltungsplaene",
                            "hmbtg_06_verwaltungsvorschriften",
                            "hmbtg_07_statistiken",
                            "hmbtg_08_gutachten",
                            "hmbtg_09_geodaten",
                            "hmbtg_10_messungen",
                            "hmbtg_11_baumkataster",
                            "hmbtg_12_oeffentliche_plaene",
                            "hmbtg_13_baugenehmigungen",
                            "hmbtg_14_zuwendungen_subventionen",
                            "hmbtg_15_unternehmensdaten",
                            "hmbtg_16_vertraege_oeffentl_interesse",
                            "hmbtg_17_dienstanweisungen",
                            "hmbtg_18_vergleichbar",
                            "hmbtg_19_andere_veroeffentlichungspflicht",
                            "hmbtg_20_ohne_veroeffentlichungspflicht"],
                    editable: true,
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true
                }],
                "general");
            var informationsgegenstandWidget = registry.byId(id);
            informationsgegenstandWidget.reinitLastColumn(true);
            additionalFields.push(informationsgegenstandWidget);


            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
        },
    })();
});
