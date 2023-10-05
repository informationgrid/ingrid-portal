/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
define([
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/aspect",
    "dojo/on",
    "dojo/dom-construct",
    "dojo/query",
    "dojo/dom-class",
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/layoutCreator",
    "ingrid/hierarchy/dirty",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/utils/Grid",
    "ingrid/utils/UI"
], function (array, declare, lang, aspect, on, construct, query, domClass, topic, registry, message, creator, dirty, Editors, Formatters, UtilGrid, UtilUI) {
    return declare(null, {

        title: "InVeKoS-Daten",
        description: "Unterstützung von Feldern für das 'Integrierte Verwaltungs- und Kontrollsystem'",
        category: "Felder",
        defaultActive: false,
        run: function () {
            var self = this;

            this.register();

            topic.subscribe("/onObjectClassChange", function (msg) {
                if (msg.objClass === "Class1") {
                    domClass.remove("invekosType", "hide");
                    domClass.remove("invekosType", "hide");
                } else {
                    domClass.add("invekosType", "hide");
                    domClass.add("invekosType", "hide");
                }
            });
        },

        register: function () {

            this.addFields();
            this.addChangeDetection();
            this.addValidations();

        },
        addFields: function () {
            var newFieldsToDirtyCheck = [];
            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;

            creator.addToSection("generalContent",
                creator.createDomSelectBox({
                    id: "invekosType",
                    name: message.get("invekos.type"),
                    help: message.get("invekos.type.helpMessage"),
                    listEntries: [
                        {id: "none", value: "Kein InVeKoS Datensatz"},
                        {id: "gsaa", value: "InVeKoS/IACS (GSAA)"},
                        {id: "lpis", value: "InVeKoS/IACS (LPIS)"}
                    ],
                    isExtendable: false,
                    visible: "show",
                    style: "width:100%"
                })
            );

            newFieldsToDirtyCheck.push("invekosType");
            additionalFields.push(registry.byId("invekosType"));

            var structure = [
                {
                    field: "keyword",
                    name: message.get("ui.obj.invekos.keywords.title") + "*",
                    type: Editors.SelectboxEditor,
                    options: ["GSAA", "Im Umweltinteresse genutzte Fläche", "InVeKoS", "Landwirtschaftliche Fläche", "LPIS", "Referenzparzelle"],
                    values: ["http://inspire.ec.europa.eu/metadata-codelist/IACSData/gsaa", "http://inspire.ec.europa.eu/metadata-codelist/IACSData/ecologicalFocusArea", "http://inspire.ec.europa.eu/metadata-codelist/IACSData/iacs", "http://inspire.ec.europa.eu/metadata-codelist/IACSData/agriculturalArea", "http://inspire.ec.europa.eu/metadata-codelist/IACSData/lpis", "http://inspire.ec.europa.eu/metadata-codelist/IACSData/referenceParcel"],
                    isMandatory: true,
                    editable: true,
                    formatter: Formatters.ListCellFormatter
                }
            ];

            construct.place(
                creator.createDomDataGrid({
                    id: "invekosKeywords",
                    name: message.get("ui.obj.invekos.keywords.title"),
                    help: message.get("ui.obj.invekos.keywords.help"),
                    style: "width: 100%"
                }, structure, "thesaurus")
            , "uiElement5170", "after");

            domClass.add("invekosKeywords", "hideTableHeader");

            newFieldsToDirtyCheck.push("invekosKeywords");
            additionalFields.push(registry.byId("invekosKeywords"));

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
        },
        
        addValidations: function () {
            var self = this;
            topic.subscribe("/onBeforeObjectPublish", function(notPublishableIDs) {
                self.checkRequiredKeywords(notPublishableIDs);
            });
        },

        checkRequiredKeywords: function (notPublishableIDs) {
            var item = registry.byId("invekosType").item;
            var invekosType = item ? item.id + "" : "";
            if (invekosType === "lpis") {
                if (!this.hasInvekosKeywords(["iacs", "lpis"])) notPublishableIDs.push( ["invekosKeywords", message.get("validation.error.invekos.missing.lpis")] );
                if (!this.hasInspireKeywords(["202"])) notPublishableIDs.push( ["thesaurusInspire", message.get("validation.error.invekos.missing.inspireLandCover")] );
            } else if (invekosType === "gsaa") {
                if (!this.hasInvekosKeywords(["iacs", "gsaa"])) notPublishableIDs.push( ["invekosKeywords", message.get("validation.error.invekos.missing.gsaa")] );
                if (!this.hasInspireKeywords(["304"])) notPublishableIDs.push( ["thesaurusInspire", message.get("validation.error.invekos.missing.inspireLandUse")] );
            }
            if (invekosType === "lpis" || invekosType === "gsaa") {
                if (!this.hasThesaurusTerms(["Gemeinsame Agrarpolitik"])) notPublishableIDs.push( ["thesaurusTerms", message.get("validation.error.invekos.missing.commonAgriculturalPolicy")] );
                // topic category "farming"
                if (!this.hasThesaurusTopics(["1"])) notPublishableIDs.push( ["thesaurusTopics", message.get("validation.error.invekos.missing.farming")] );
                // date of type "revision"
                if (!this.hasTimeReferenceType(["3"])) notPublishableIDs.push( ["timeRefTable", message.get("validation.error.invekos.missing.revision")] );
            }
        },
        
        hasInvekosKeywords: function(/*string[]*/items) {
            var keywords = registry.byId("invekosKeywords").data;
            var mappedKeywords = array.map(keywords, function (kw) {
                return kw.keyword;
            });
            return array.every(items, function(item) {
                return mappedKeywords.indexOf("http://inspire.ec.europa.eu/metadata-codelist/IACSData/" + item) !== -1;
            });
        },

        hasInspireKeywords: function(/*string[]*/items) {
            var keywords = registry.byId("thesaurusInspire").data;
            var mappedKeywords = array.map(keywords, function (kw) {
                return kw.title + "";
            });
            return array.every(items, function(item) {
                return mappedKeywords.indexOf(item) !== -1;
            });
        },

        hasThesaurusTerms: function(/*string[]*/items) {
            var keywords = registry.byId("thesaurusTerms").data;
            var mappedKeywords = array.map(keywords, function (kw) {
                return kw.title + "";
            });
            return array.every(items, function(item) {
                return mappedKeywords.indexOf(item) !== -1;
            });
        },

        hasThesaurusTopics: function(/*string[]*/items) {
            var keywords = registry.byId("thesaurusTopics").data;
            var mappedKeywords = array.map(keywords, function (kw) {
                return kw.title + "";
            });
            return array.every(items, function(item) {
                return mappedKeywords.indexOf(item) !== -1;
            });
        },

        hasTimeReferenceType: function(/*string[]*/items) {
            var keywords = registry.byId("timeRefTable").data;
            var mappedKeywords = array.map(keywords, function (kw) {
                return kw.type + "";
            });
            return array.every(items, function(item) {
                return mappedKeywords.indexOf(item) !== -1;
            });
        },
        
        addChangeDetection: function () {
            domClass.add("uiElementAddinvekosType", "hide");
            domClass.add("uiElementAddinvekosKeywords", "hide");
            
            on(registry.byId("isInspireRelevant"), "Change", function (isChecked) {
                if (isChecked) {
                    domClass.remove("uiElementAddinvekosType", "hide");
                    domClass.remove("uiElementAddinvekosKeywords", "hide");
                } else {
                    domClass.add("uiElementAddinvekosType", "hide");
                    domClass.add("uiElementAddinvekosKeywords", "hide");
                }
            });
        }
    })();
});
