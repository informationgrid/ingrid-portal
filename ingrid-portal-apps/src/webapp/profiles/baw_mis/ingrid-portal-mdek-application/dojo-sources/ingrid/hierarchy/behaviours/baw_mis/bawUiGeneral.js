/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
    "dojo/_base/declare",
    "dojo/dom-class",
    "dojo/topic",
    "ingrid/utils/Security"
], function(registry, declare, domClass, topic, UtilSecurity) {

    return declare(null, {
        title: "BAW-Allgemein",
        description: "Allgemeine UI Anpassungen für die BAW",
        defaultActive: true,
        category: "BAW-MIS",
        run: function() {

            topic.subscribe("/onObjectClassChange", function(data) {
                // Reset everything first

                // Allgemeines > Kurzbezeichnung
                domClass.add("uiElement5000", "hide");
                domClass.add("uiElement5000", "optional");
                domClass.remove("uiElement5000", "additional");
                domClass.remove("uiElement5000", "content");
                // INSPIRE
                domClass.remove("uiElement5064", "hide");
                // Rubrik: Raumbezugssystem
                domClass.remove("spatialRef", "hide");
                // Raumbezugssystem
                domClass.remove("uiElement3500", "hide");
                // Rubrik: Verfügbarkeit
                domClass.add("availability", "hide");
                
                domClass.remove("timeRef", "hide");
                domClass.remove("uiElement5042", "hide");
                domClass.remove("uiElementN025", "hide");
                domClass.remove("uiElementN026", "hide");
                domClass.remove("uiElement1320", "hide");
                
                // for class: Software
                domClass.add("uiElementAddpurpose", "hide");
                domClass.add("uiElementAdduserGroup", "hide");
                domClass.add("uiElementAddproductiveUse", "hide");


                var isGeodataset = data.objClass === "Class1";
                var isLiterature = data.objClass === "Class2";
                var isProject = data.objClass === "Class4";
                var isSoftware = data.objClass === "Class6";

                // Object-class specific rules
                if (isLiterature) {
                    // INSPIRE
                    domClass.add("uiElement5064", "hide");
                    // Rubrik: Raumbezugssystem
                    domClass.add("spatialRef", "hide");
                } else if (isProject) {
                    // Allgemeines > Kurzbezeichnung
                    domClass.remove("uiElement5000", "hide");
                    domClass.remove("uiElement5000", "optional");
                    domClass.add("uiElement5000", "additional");
                    domClass.add("uiElement5000", "content");

                    // Rubrik: Zeitbezug
                    domClass.add("timeRef", "hide");
                } else if (isSoftware) {
                    // INSPIRE
                    domClass.add("uiElement5064", "hide");

                    // Rubrik: Raumbezugssystem
                    domClass.add("spatialRef", "hide");

                    // Rubrik: Zeitbezug
                    domClass.add("timeRef", "hide");

                    // Sprache der Ressource
                    // set delayed since another behaviour is changing value
                    setTimeout(function() { domClass.add("uiElement5042", "hide"); });
                    
                    // Zugriffsbeschränkungen
                    domClass.add("uiElementN025", "hide");
                    
                    // Anwendungseinschränkungen
                    domClass.add("uiElementN026", "hide");
                    
                    // Datenformat
                    domClass.add("uiElement1320", "hide");

                    // Allgemein
                    domClass.remove("uiElementAddpurpose", "hide");
                    domClass.remove("uiElementAdduserGroup", "hide");
                    domClass.remove("uiElementAddproductiveUse", "hide");

                    // Rubrik: Verfügbarkeit
                    domClass.remove("availability", "hide");

                    // Version (Fachbezug)
                    domClass.remove("uiElement3630", "optional");
                }
                
                if (isGeodataset) {
                    // INSPIRE-relevant
                    domClass.remove("uiElement6000", "hide");
                    domClass.add("uiElement6000", "optional");

                    // Open Data checkbox
                    /*
                     * opendata.js runs after this script and removes the 'hide'
                     * class. So add the 'hidden' class instead!
                     */
                    domClass.remove("uiElement6010", "hidden");
                    domClass.remove("uiElement6010", "show");
                    domClass.add("uiElement6010", "optional");
                    
                } else {
                    // Inspire relevant
                    domClass.add("uiElement6000", "hide");
                    // OpenData
                    domClass.add("uiElement6010", "hidden");
                }
            });

            // Autor / Verfasser
            domClass.add("uiElement3355", "hide");
            // Herausgeber
            domClass.add("uiElement3350", "hide");
            // Erschienen in
            domClass.add("uiElement3340", "hide");
            // Erscheinungsort
            domClass.add("uiElement3310", "hide");
            // Band / Heft
            domClass.add("uiElement3330", "hide");
            // Seiten
            domClass.add("uiElement3320", "hide");
            // Erscheinungsjahr
            domClass.add("uiElement3300", "hide");
            // Verlag
            domClass.add("uiElement3370", "hide");
            // Standort
            domClass.add("uiElement3360", "hide");
            // Dokumententyp
            domClass.add("uiElement3385", "hide");
            // Basisdaten
            domClass.add("uiElement3210", "hide");
            // Weitere bibliografische Angaben
            domClass.add("uiElement3380", "hide");
            // Erläuterungen
            domClass.add("uiElement3375", "hide");



            // ========================================
            // Modifications to existing fields
            // ========================================

            // ----------------------------------------
            // Allgemeines
            // ----------------------------------------
            // AdV kompatibel checkbox
            domClass.add("uiElement6005", "hide");

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
            // Fachbezug: Geodatensatz
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
            domClass.add("uiElement3535", "hidden");
            // Sachdaten/Attributinformation
            domClass.add("uiElement5070", "hide");
            // Darstellender Dienst
            domClass.add("uiElementN003", "hide");
            // Herstellungsprozess
            domClass.add("uiElement3515", "hide");

            // ----------------------------------------
            // Fachbezug: Projekt
            // ----------------------------------------
            domClass.add("refClass4", "hidden");

            // SOFTWARE
            // Art des Dienstes
            domClass.add("uiElement3620", "hide");
            // Systemumgebung
            domClass.add("uiElement3600", "hide");
            // Historie
            domClass.add("uiElement3640", "hide");
            // Basisdaten
            domClass.add("uiElement3645", "hide");
            // Erläuterungen
            domClass.add("uiElement3650", "hide");
            // Service-Urls
            domClass.add("uiElement3670", "hide");
            
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

            var isKatAdmin = UtilSecurity.currentUser.role === 1;
            if (isKatAdmin) {
                domClass.remove('uiElement3571', 'hide');
            } else {
                domClass.add('uiElement3571', 'hide');
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

        }

    })();
});

