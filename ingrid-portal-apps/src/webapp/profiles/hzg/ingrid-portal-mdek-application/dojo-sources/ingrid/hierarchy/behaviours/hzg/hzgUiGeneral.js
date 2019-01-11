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
    "dijit/registry",
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/aspect",
    "dojo/dom-class",
    "ingrid/utils/UI"
], function(registry, array, declare, aspect, domClass, UtilUI) {

    return declare(null, {
        title: "UI-Allgemein",
        description: "Allgemeine Anpassungen der Editor-Oberfläche",
        defaultActive: true,
        category: "HZG",
        run: function() {

            // Allgemeines / Vorschaugrafik
            UtilUI.setShow("uiElement5100");

            // Fachbezug / Digitale Repräsentation
            UtilUI.setShow("uiElement5062");
            aspect.after(registry.byId("ref1Representation"), "onDataChanged", function() {
                // row.title 1 is Vector and row.title 2 is Raster
                // Check if the list contains either of these entries
                var hasVectorType = array.some(this.getData(), function(row) {
                    return row.title === 1 || row.title === "1";
                });

                var hasGridType = array.some(this.getData(), function(row) {
                    // 2 === Raster, Gitter
                    return row.title === 2 || row.title === "2";
                });

                // show / hide relevant fields
                if (hasVectorType) {
                    UtilUI.setShow("ref1VFormat");
                    UtilUI.setShow("uiElement5063");
                    UtilUI.setShow("uiElementN001");
                } else {
                    UtilUI.setHide("ref1VFormat");
                }
                if (hasGridType) {
                    UtilUI.setShow("ref1GridFormat");
                } else {
                    UtilUI.setHide("ref1GridFormat");
                }
            });

            // Fachbezug / Sachdaten/Attributinformation
            UtilUI.setShow("uiElement5070");

            // Zeitbezug / Durch die Ressource Abgedeckte Zeitspanne
            UtilUI.setShow("uiElementN011");

            // Zeitbezug / Status
            UtilUI.setShow("uiElement1220");

            // Zeitbezug / Periodizität
            UtilUI.setShow("uiElement1240");
        }
    })();
});
