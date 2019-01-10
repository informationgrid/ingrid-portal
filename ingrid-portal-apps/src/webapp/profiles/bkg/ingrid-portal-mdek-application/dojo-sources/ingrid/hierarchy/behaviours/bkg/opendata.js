/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
    "dojo/on",
    "dojo/dom-class",
    "dijit/registry"
], function(declare, on, domClass, registry) {

    // issue: 339
    return declare(null, {
        title: "Open Data",
        description: "BKG spezifisches Verhalten zu Open Data",
        defaultActive: true,
        category: "BKG",
        run: function() {
            // 1) open data categories shall not be displayed
            // 2) link of type download is optional
            // (3) add "Es gelten keine Zugriffsbeschränkungen" into BKG specific field "Zugriffsbeschränkungen" (#556)
            // (4) use BKG specific codelist for "Nutzungsbedingungen" (#393)
            // (5) add "opendata" keyword to ISO -> mapping script (default)
            // (6) remove keyword information from ISO? How to distinguish when importing?
            // (7) add json to ISO for "Nutzungsbedingungen"


            // delay registration so that this behaviour is called at the end
            on(registry.byId("isOpenData"), "click", function() {
                var bkgUseConstraintsWidget = registry.byId("bkg_useConstraints");
                var isChecked = this.checked;

                if (isChecked) {

                    // automatically replace access constraint with "keine"
                    var access = registry.byId("bkg_accessConstraints");
                    access.selectInput.set("value", "1");

                }

                // reset use constraints since codelist has changed explicitly
                bkgUseConstraintsWidget.selectInput.set("value", "");
            });


            on(registry.byId("isOpenData"), "change", function(isChecked) {
                var bkgUseConstraintsWidget = registry.byId("bkg_useConstraints");
                if (isChecked) {
                    bkgUseConstraintsWidget.setCodelist(10005);
                    bkgUseConstraintsWidget.codelistForText = 10006;
                } else {
                    bkgUseConstraintsWidget.setCodelist(10003);
                    bkgUseConstraintsWidget.codelistForText = 10004;
                }
            });
        }
    })();
});
