/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
    "dojo/topic",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/message"
], function (registry, array, declare, lang, dom, domClass, construct, topic, dirty, creator, message) {

    return declare(null, {
        title: "Regionalschlüssel",
        description: "Der Regionalschlüssel des Metadatensatzes.",
        defaultActive: true,

        run: function () {
            var sourceIdElement = "uiElementN009"; // Freier Raumbezug
            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];


            id = "regionKey";
            newFieldsToDirtyCheck.push(id);
            construct.place(creator.createDomTextbox({
                id: id,
                name: message.get("ui.obj.spatial.regionKey"),
                help: message.get("ui.obj.spatial.regionKeyHelp"),
                isMandatory: false,
                visible: "optional",
                validator: function(value) {
                    // only digits allowed. max length: 12
                    return /^([\d]{0,12})$/.test(value);
                },
                style: "width:33%"
            }), sourceIdElement, "after");

            var regionKeyWidget = registry.byId(id);
            additionalFields.push(regionKeyWidget);


            // show field only for classes 1 and 3
            topic.subscribe("/onObjectClassChange", function (data) {
                if (data.objClass === "Class1" || data.objClass === "Class3") {
                    domClass.remove(dom.byId("uiElementAddregionKey"), "hide");
                } else {
                    domClass.add(dom.byId("uiElementAddregionKey"), "hide");
                }
            });

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
            return registry.byId(id).promiseInit;
        }
    })();
});
