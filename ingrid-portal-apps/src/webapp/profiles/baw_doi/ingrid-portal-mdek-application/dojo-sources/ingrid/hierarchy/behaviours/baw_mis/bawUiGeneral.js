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
    "dojo/_base/declare",
    "dojo/dom-class",
    "dojo/topic"
], function(registry, declare, domClass, topic) {

    return declare(null, {
        title: "BAW-Allgemein",
        description: "Allgemeine UI Anpassungen für die BAW",
        defaultActive: true,
        category: "BAW-MIS",
        run: function() {
            topic.subscribe("/onObjectClassChange", function(data) {

                // ========================================
                // Modifications to existing fields
                // ========================================

                // ----------------------------------------
                // Allgemeines
                // ----------------------------------------
                // Kurzbezeichnung
                domClass.add("uiElement5000", "hide");
                // Inspire relevant
                domClass.add("uiElement6000", "hidden");
                // AdV kompatibel checkbox
                domClass.add("uiElement6005", "hide");
                // Open Data checkbox
                /*
                 * opendata.js runs after this script and removes the 'hide'
                 * class. So add the 'hidden' class instead!
                 */
                domClass.add("uiElement6010", "hidden");

                // Kategorien (opendata)
                domClass.add("uiElement6020", "required");
                domClass.remove("uiElement6020", "hide");
                domClass.remove("uiElement6020", "halfWidth");
                registry.byId("categoriesOpenData").reinitLastColumn(true);

                // ----------------------------------------
                // Verschlagwortung
                // ----------------------------------------
                // Inspire priority data set
                domClass.add("uiElement5090", "hidden");
                // Inspire räumlicher Anwendungsbereich
                domClass.add("uiElement5095", "hidden");
                // AdV-Produktgruppe
                domClass.add("uiElement5170", "hide");


                // Themen
                domClass.add("uiElementN014", "hide");

                // ----------------------------------------
                // Fachbezug
                // ----------------------------------------
                // Datensatz/Datenserie
                domClass.add("uiElement5061", "hide");
                // Digitale Repräsentation
                domClass.add("uiElement5062", "hide");
                // Vektorformat
                domClass.add("uiElementN005", "hide");
                // Symbolkatalog
                domClass.add("uiElement3555", "hide");
                // Schlüsselkatalog
                domClass.add("uiElement3535", "hide");
                // Sachdaten/Attributinformation
                domClass.add("uiElement5070", "hide");
                // Darstellender Dienst
                domClass.add("uiElementN003", "hide");
                // Herstellungsprozess
                domClass.add("uiElement3515", "hide");

                // ----------------------------------------
                // Datenqualität
                // ----------------------------------------
                // Hide the entire category
                domClass.add("refClass1DQ", "hidden");

                // ----------------------------------------
                // Raumbezugssystem
                // ----------------------------------------
                // Höhe
                domClass.add("uiElementN010", "hide");
                // Erläuterungen
                domClass.add("uiElement1140", "hide");

                // ----------------------------------------
                // Zeitbezug
                // ----------------------------------------
                // Zeitbezug der Ressource
                domClass.remove("uiElement5030", "halfWidth");
                registry.byId("timeRefTable").reinitLastColumn(true);
                // Erläuterungen
                domClass.add("uiElement1250", "hide");
                // Status
                domClass.add("uiElement1220", "hide");
                // Periodizität
                domClass.add("uiElement1240", "hide");
                // Im Intervall
                domClass.add("uiElement1230", "hide");



                // ----------------------------------------
                // Zusatzinformation
                // ----------------------------------------
                // Zeichensatz des Datensatzes
                // Make the node mandatory if it has not been hidden by some
                // other rule
                var datasetCharsetNodeId = "uiElement5043";
                if(!domClass.contains(datasetCharsetNodeId, "hide")) {
                    domClass.add(datasetCharsetNodeId, "required");
                }

                // XML-Export-Kriterium
                domClass.add("uiElementN012", "hide");
                // Herstellungszweck
                domClass.add("uiElementN013", "hide");
                // Konformität
                domClass.remove("uiElementN024", "required");
                // Rechtliche Grundlagen
                domClass.add("uiElement1350", "hide");
                // Eignung/Nutzung
                domClass.add("uiElement5040", "hide");



                // ----------------------------------------
                // Verfügbarkeit
                // ----------------------------------------
                // Kodierungsschema der geografischen Daten
                // Not necessarily a required field. See #1273
                // Medienoption
                domClass.add("uiElement1310", "hidden");
                // Bestellinformation
                domClass.add("uiElement5052", "hidden");


                // ----------------------------------------
                // Verweise
                // ----------------------------------------
                // Verweise zu
                domClass.add("uiElementN017", "show");
            });

            return;
        }

    })();
});

