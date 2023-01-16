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
    "dojo/aspect",
    "dojo/on",
    "dojo/dom-class",
    "dojo/string",
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/UI"
], function (array, declare, aspect, on, domClass, string, topic, registry, message, UtilUI) {
    return declare(null, {

        title: "Symbolkatalog",
        description: "Titel und Datum müssen gesetzt sein.",
        category: "Felder",
        defaultActive: true,
        run: function () {
            var grid = registry.byId("ref1SymbolsText");
            grid.validate = this.titleDateValidation;
            on(grid, "onCellChange", this.titleDateValidation);
            on(grid, "onAddNewRow", this.titleDateValidation);

        },
        titleDateValidation: function() {
            var error = false;
            var data = this.getData();

            array.forEach(data, function(item, row) {
                var rowError = false;
                var hasTitle = item.title && item.title.trim().length > 0;
                var hasDate = item.date;

                if (!hasTitle || !hasDate) {
                    rowError = true;
                }

                if (rowError) {
                    error = true;
                    if (!hasDate) {
                        UtilUI.markCells("ERROR", "ref1SymbolsText", row, [1]);
                        UtilUI.markCells("VALID", "ref1SymbolsText", row, [0]);
                    } else {
                        UtilUI.markCells("ERROR", "ref1SymbolsText", row, [0]);
                        UtilUI.markCells("VALID", "ref1SymbolsText", row, [1]);
                    }
                    var msg = string.substitute(message.get("validation.titleDate"), [message.get("ui.obj.type1.symbolCatTable.header.title"), message.get("ui.obj.type1.symbolCatTable.header.date")]);
                    UtilUI.showToolTip("ref1SymbolsText", msg);
                } else {
                    UtilUI.markCells("VALID", "ref1SymbolsText", row, [0, 1]);
                }

            });

            return !error;
        }
    })();
});
