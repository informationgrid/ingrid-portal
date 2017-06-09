/*-
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
    "dojo/dom-construct",
    "dojo/dom-class",
    "dojo/query",
    "dojo/on",
    "dojo/request",
    "dojo/json",
    "dijit/registry",
    "dijit/_WidgetBase",
    "dijit/_Templated",
    "dijit/form/FilteringSelect",
    "dijit/form/SimpleTextarea",
    "ingrid/layoutCreator",
    "ingrid/message",
    "ingrid/utils/Syslist",
    "ingrid/utils/Store"
], function(declare, array, lang, construct, domClass, query, on, request, json, registry, _WidgetBase, _Templated, FilteringSelect, SimpleTextarea, creator, message, UtilSyslist, UtilStore) {

    return declare("MultiInputInfoField", [_WidgetBase, dijit._Templated], {

        prefix: "bkg_",

        label: "",

        infoText: "---",

        codelist: null,

        codelistForText: null,

        selectRequired: false,

        freeTextRequired: false,

        templateString:
        "<span>" +
        "  <span class='outer'>" +
        "    <div>" +
        "      <span class='label'>" +
        "        <label>${label}</label>" +
        "      </span>" +
        "      <div class='outlined'>" +
        "        <!--<span class='functionalLink'>" +
        "          <img src='img/ic_fl_popup.gif' width='10' height='9' alt='Popup' />" +
        "          <a id='spatialRefAdminUnitLink' href='javascript:void(0);'>Freitext Vorlagen</a>" +
        "        </span>-->" +
        "        <div class='clear'>" +
        "          <span class='outer halfWidth'>" +
        "            <div>" +
        "              <select data-dojo-type='dijit/form/FilteringSelect' data-dojo-attach-point='selectInput' style='width:100%;'></select>" +
        "              <div class='comment' style='width:100%; height:30px; overflow:hidden;'>Info-Text ...</div>" +
        "            </div>" +
        "          </span>" +
        "          <span class='outer halfWidth'>" +
        "            <div>" +
        "              <textarea data-dojo-type='dijit/form/SimpleTextarea' data-dojo-attach-point='freeTextInput' rows=3 style='width:100%;'></textarea>" +
        "            </div>" +
        "          </span>" +
        "          <div class='clear'></div>" +
        "        </div>" +
        "      </  div>" +
        "    </div>" +
        "  </span>" +
        "</span>",

        widgetsInTemplate: true,

        // handle data as complex value
        valueAsTableData: true,

        postCreate: function() {
            console.log("MultiInputInfoField: postCreate");
        },

        startup: function() {
            var self = this;
            console.log("MultiInputInfoField: startup");

            this.infoText = query(".comment", this.domNode);

            this.addTextareaValidator();

            // set correct search attribute
            this.selectInput.set("searchAttr", "0");
            this.selectInput.set("required", this.selectRequired);

            on(this.selectInput, "change", function(value) {
                var text = UtilSyslist.getSyslistEntryName(self.codelistForText, this.get("value"));
                if (text !== this.get("value")) {
                    self.infoText.addContent(text, "only");
                    // also set tooltip on mouse over to show full content
                    self.infoText.attr("title", text);
                } else {
                    self.infoText.addContent("", "only");
                    self.infoText.attr("title", "");
                }
            });

            // set codelist of select box
            this.setCodelist(this.codelist);

        },

        attr: function(type, values) {
            console.log("set value of MultiInputInfoField to: ", values);

            this.clearInputs();

            var self = this;
            if (type === "value" && values && values.length > 0) {
                array.forEach(values[0], function(entry, index) {
                    if (entry.identifier === self.id + "_select") {
                        self.selectInput.set("value", entry.listId);
                    } else if (entry.identifier === self.id + "_freeText") {
                        self.freeTextInput.set("value", entry.value);
                    }
                });
            }
        },

        get: function(attr) {
            if (attr !== "displayedValue") return;

            console.log("returning value of MultiInputInfoField");

            entrySelect = {
                identifier: this.id + "_select",
                value: null, // this.selectInput.get("value"),
                listId: this.selectInput.get("value"),
                tableRows: null
            };

            entryFreeText = {
                identifier: this.id + "_freeText",
                value: this.freeTextInput.get("value"),
                listId: null,
                tableRows: null
            };

            return [[entrySelect, entryFreeText]];
        },

        addTextareaValidator: function() {
            this.freeTextInput.validator = function() {
                if (this.required && this.get("value") === "") {
                    domClass.add(this.domNode, "importantBackground");
                    return false;
                } else {
                    domClass.remove(this.domNode, "importantBackground");
                    return true;
                }
            };
            this.freeTextInput.validate = function() {
                return this.validator();
            };
        },

        setCodelist: function(codelist) {
            var self = this;
            
            UtilSyslist.getSyslistEntry(codelist).then(function(entries) {
                UtilStore.updateWriteStore(self.selectInput.id, entries, {
                    identifier: "1",
                    label: "0",
                    items: entries
                });
            });
        },

        clearInputs: function() {
            this.selectInput.set("value", null);
            this.freeTextInput.set("value", "");
        }

    });
});
