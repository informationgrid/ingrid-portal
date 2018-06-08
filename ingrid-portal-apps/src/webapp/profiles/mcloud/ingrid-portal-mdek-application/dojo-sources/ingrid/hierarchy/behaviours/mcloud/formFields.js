/*
 * **************************************************-
 * InGrid Portal MDEK Application
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
    "dijit/form/Button",
    "dijit/form/RadioButton",
    "ingrid/message",
    "ingrid/layoutCreator",
    "ingrid/IgeEvents",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/utils/Grid",
    "ingrid/utils/Syslist"
], function(declare, array, lang, aspect, dom, domClass, domStyle, construct, query, topic, registry, Button, RadioButton, message, creator, IgeEvents, Editors, Formatters, dirty, UtilGrid, UtilSyslist) {
    return declare(null, {
        title: "Formularfelder",
        description: "Hier werden die zusätzlichen Felder im Formular erzeugt sowie überflüssige ausgeblendet.",
        defaultActive: true,
        category: "mcloud",
        prefix: "mcloud_",
        //params: [{id: "categoryCodelist", label: "Codelist (Kategorie)", "default": 9000}],
        run: function() {

            // rename default fields
            query("#general .titleBar .titleCaption")
                .addContent(message.get("mcloud.form.consideration"), "only")
                .style("cursor", "default")
                .attr('onclick', undefined);
            query("#general .titleBar").attr("title", message.get("mcloud.form.consideration.tooltip"));

            // do not override my address title
            IgeEvents.setGeneralAddressLabel = function() { };

            this.hideDefaultFields();

            this.createFields();

            // TODO: additional fields according to #490 and #473

            var self = this;
            topic.subscribe("/onObjectClassChange", function(clazz) {
                self.prepareDocument(clazz);
            });

        },

        prepareDocument: function(classInfo) {
            console.log("Prepare document for class: ", classInfo);

        },

        hideDefaultFields: function() {
            domStyle.set("widget_objectName", "width", "550px");

            domClass.add(dom.byId("objectClassLabel").parentNode, "hide");
            domClass.add(dom.byId("objectOwnerLabel").parentNode, "hide");

            domClass.add(registry.byId("toolbarBtnISO").domNode, "hide");

            // general
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
            domClass.add("uiElementN018", "hide");

            // hide all rubrics not needed
            query(".rubric", "contentFrameBodyObject").forEach(function(item) {
                if (item.id !== "general" && item.id !== "spatialRef" && item.id !== "timeRef" && item.id !== "links") {
                    domClass.add(item, "hide");
                }
            });
        },

        createFields: function() {
            var rubric = "mcloud";
            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            var rubricContainer = creator.createRubric({ id: "mcloud", label: "mcloud Felder", help: "..." });
            construct.place(rubricContainer, "general", "after");

            /*
             * Terms of Use
             */
            var id = "mcloudTermsOfUse";
            construct.place(
                creator.createDomTextarea({ id: id, name: message.get("mcloud.form.termsOfUse"), help: message.get("mcloud.form.termsOfUse.helpMessage"), visible: "required", style: "width:100%" }),
                rubric);
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

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
            construct.place(
                creator.createDomSelectBox({
                    id: id, name: message.get("mcloud.form.category"), help: message.get("mcloud.form.category.helpMessage"),
                    listEntries: [{id: "b", value: "erste kategorie"}],
                    visible: "required", style: "width:100%" }),
                rubric);
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            /*
             * Source Type --> not needed since "Downloads" cover this
             */
            /*id = "mcloudSourceType";
            var structure = [
                {
                    field: 'sourceType',
                    name: 'Quellentyp',
                    type: Editors.SelectboxEditor,
                    editable: true,
                    // listId: codelist,
                    options: ["Download", "Portal", "FTP"],
                    values: ["dl", "portal", "ftp"],
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true
                }
            ];
            newFieldsToDirtyCheck.push(id);
            creator.createDomDataGrid(
                { id: id, name: message.get("mcloud.form.sourceType"), help: message.get("mcloud.form.sourceType.helpMessage"), isMandatory: true, visible: "optional", rows: "4", forceGridHeight: false, style: "width:100%" },
                structure, rubric
            );
            var categoryWidget = registry.byId(id);
            domClass.add(categoryWidget.domNode, "hideTableHeader");
            additionalFields.push(categoryWidget);*/

            /*
             * Downloads
             */
            id = "mcloudDownloads";
            var structure = [
                {
                    field: 'title',
                    name: 'Titel',
                    width: '200px',
                    editable: true
                },
                {
                    field: 'sourceType',
                    name: 'Typ',
                    type: Editors.SelectboxEditor,
                    editable: true,
                    // listId: codelist,
                    options: ["Download", "Portal", "FTP"],
                    values: ["dl", "portal", "ftp"],
                    formatter: Formatters.ListCellFormatter,
                    partialSearch: true,
                    width: '80px'
                },
                {
                    field: 'link',
                    name: 'Link',
                    editable: true
                }
            ];
            newFieldsToDirtyCheck.push(id);
            creator.createDomDataGrid(
                { id: id, name: message.get("mcloud.form.downloads"), help: message.get("mcloud.form.downloads.helpMessage"), isMandatory: true, visible: "optional", rows: "1", forceGridHeight: false, style: "width:100%" },
                structure, rubric
            );
            var categoryWidget = registry.byId(id);
//            domClass.add(categoryWidget.domNode, "hideTableHeader");
            additionalFields.push(categoryWidget);

            /*
             * License --> Codelist 6500 in Selectbox
             */
            id = "mcloudLicense";
            construct.place(
                creator.createDomSelectBox({
                    id: id, name: message.get("mcloud.form.license"), help: message.get("mcloud.form.license.helpMessage"),
                    useSyslist: "6500",
                    visible: "required", style: "width:100%" }),
                rubric);
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));


            /*
             * Source Note
             */
            id = "mcloudSourceNote";
            construct.place(
                creator.createDomTextarea({ id: id, name: message.get("mcloud.form.sourceNote"), help: message.get("mcloud.form.sourceNote.helpMessage"), visible: "required", style: "width:100%" }),
                rubric);
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            /*
             * File Type (optional)
             */
            id = "mcloudFileType";

            /*
             * Availability (optional)
             */
            id = "mcloudAvailability";

            /*
             * File Format (optional)
             */
            id = "mcloudFileFormat";

            /*
             * PeriodOfTime, temporal(!) (DateOfRelevance) --> Zeitraum "Durch die Ressource abged..."
             */
            id = "mcloudDateOfRelevance";

            /*
             * Date Updated --> Zeitbezug der Ressource
             * TODO: allow only certain types
             */
            id = "mcloudDateUpdated";

            /*
             * Realtime Data --> Checkbox / Periodizität(!)->kontinuierlich
             */
            id = "mcloudRealTimeData";

            /*
             * Förderkennzeichen mFund
             */
            id = "mcloudMFundFKZ";
            construct.place(
                creator.createDomTextarea({ id: id, name: message.get("mcloud.form.mFundFKZ"), help: message.get("mcloud.form.mFundFKZ.helpMessage"), visible: "required", style: "width:100%" }),
                rubric);
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
        }

    })();
});
