/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
    "dojo/dom-construct",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/message",
], function(registry, array, declare, lang, construct, dirty, creator, message) {

    return declare(null, {
        title: "environmentDescription",
        description: "Beschreibung der Bearbeitungsumgebung, in welcher der Datenbestand erstellt wird.",
        defaultActive: true,
        category: "LUBW",
        run: function() {
            var creationProcessElement = "uiElement3515"; // Herstellungsprozess
            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            /*
             * environmentDescription
             */
            var id = "environmentDescription";
            construct.place(
                creator.createDomTextbox({
                    id: id,
                    name: message.get("ui.obj.lubw.environmentDescription.title"),
                    help: message.get("ui.obj.lubw.environmentDescription.help"),
                    isMandatory: true,
                    visible: "show",
                    style: "width:100%" }),
                creationProcessElement, "after"
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
            return registry.byId(id).promiseInit;
        }
    })();
});
