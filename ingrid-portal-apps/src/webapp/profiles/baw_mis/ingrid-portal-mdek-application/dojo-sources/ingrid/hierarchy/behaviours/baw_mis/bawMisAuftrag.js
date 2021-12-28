/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
    "dijit/registry",
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-construct",
    "dojo/on",
    "dojo/topic",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/message",
    "ingrid/utils/Store",
    "ingrid/utils/Syslist",
    "module"
], function(registry, array, declare, lang, dom, domClass, construct, on, topic, dirty, creator, message, UtilStore, UtilSyslist, module) {

    const AUFTRAGSNUMMER_ID = "bawAuftragsnummer";
    const AUFTRAGSTITEL_ID = "bawAuftragstitel";

    return declare(null, {
        title: "Auftragsinformationen",
        description: "Informationen zu den WSV-Aufträge",
        defaultActive: true,
        category: "BAW-MIS",

        run: function() {
            var promise = this._createCustomFields();
            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass === "Class1") {
                    domClass.remove("uiElementAdd" + AUFTRAGSNUMMER_ID, "hide");
                    domClass.remove("uiElementAdd" + AUFTRAGSTITEL_ID, "hide");
                } else {
                    domClass.add("uiElementAdd" + AUFTRAGSNUMMER_ID, "hide");
                    domClass.add("uiElementAdd" + AUFTRAGSTITEL_ID, "hide");
                }

            });

            return promise;
        },

        _createCustomFields: function () {
            var self = require(module.id);
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            // Create elements in the reverse order instead of deriving ids
            // of the wrapped DOM elements.
            construct.place(
                creator.createDomTextbox({
                    id: AUFTRAGSNUMMER_ID,
                    name: message.get("ui.obj.baw.auftragsnummer.title"),
                    help: message.get("ui.obj.baw.auftragsnummer.help"),
                    isMandatory: true,
                    visible: "optional",
                    style: "width: 100%"
                }),
                "uiElement1000", "after");
            newFieldsToDirtyCheck.push(AUFTRAGSNUMMER_ID);
            additionalFields.push(registry.byId(AUFTRAGSNUMMER_ID));

            construct.place(
                creator.createDomTextbox({
                    id: AUFTRAGSTITEL_ID,
                    name: message.get("ui.obj.baw.auftragstitel.title"),
                    help: message.get("ui.obj.baw.auftragstitel.help"),
                    visible: "optional",
                    isMandatory: true,
                    style: "width: 100%"
                }),
                "uiElement1000", "after");
            newFieldsToDirtyCheck.push(AUFTRAGSTITEL_ID);
            additionalFields.push(registry.byId(AUFTRAGSTITEL_ID));


            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
            return registry.byId(AUFTRAGSTITEL_ID).promiseInit;
        }

    })();
});

