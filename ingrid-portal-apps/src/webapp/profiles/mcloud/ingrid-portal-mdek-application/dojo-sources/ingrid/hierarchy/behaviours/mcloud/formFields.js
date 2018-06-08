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
        prefix: "uvp_",
        params: [{id: "categoryCodelist", label: "Codelist (Kategorie)", "default": 9000}],
        run: function() {

            // rename default fields
            query("#general .titleBar .titleCaption")
                .addContent(message.get("uvp.form.consideration"), "only")
                .style("cursor", "default")
                .attr('onclick', undefined);
            query("#general .titleBar").attr("title", message.get("uvp.form.consideration.tooltip"));

            // do not override my address title
            IgeEvents.setGeneralAddressLabel = function() { };

            this.hideDefaultFields();

            this.createFields();

            // TODO: additional fields according to #490 and #473

            var self = this;
            topic.subscribe("/onObjectClassChange", function(clazz) {
                self.prepareDocument(clazz);
            });

            // disable editing the address table and automatically set point of contact type
            UtilGrid.getTable("generalAddress").options.editable = false;

            var handleAddressAdd = function() {
                var pointOfContactName = UtilSyslist.getSyslistEntryName(505, 7);
                UtilGrid.addTableDataRow("generalAddress", {nameOfRelation: pointOfContactName});
            };
            topic.subscribe("/onBeforeDialogAccept/AddressesFromTree", handleAddressAdd);
            topic.subscribe("/onBeforeDialogAccept/Addresses", handleAddressAdd);
            /*topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {

            });*/
        },

        prepareDocument: function(classInfo) {
            console.log("Prepare document for class: ", classInfo);

        },

        hideDefaultFields: function() {
            domStyle.set("widget_objectName", "width", "550px");

            domClass.add(dom.byId("objectClassLabel").parentNode, "hide");
            domClass.add(dom.byId("objectOwnerLabel").parentNode, "hide");

            domClass.add(registry.byId("toolbarBtnISO").domNode, "hide");

            domClass.add("uiElement5000", "hide");
            domClass.add("uiElement5100", "hide");
            domClass.add("uiElement5105", "hide");
            domClass.add("uiElement6005", "hide");
            domClass.add("uiElement6010", "hide");

            // hide all rubrics
            query(".rubric", "contentFrameBodyObject").forEach(function(item) {
                if (item.id !== "general") {
                    domClass.add(item, "hide");
                }
            });
        },

        createFields: function() {
            var rubric = "general";
            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];


            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
        }

    })();
});
