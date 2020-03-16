/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
    "dojo/topic",
    "dojo/dom",
    "dojo/dom-class"
], function(declare, topic, dom, domClass) {

    // issue: 556
    return declare(null, {
        title: "Pflichtfeld-Anpassungen",
        description: "Anpassung der Pflichtfelder",
        defaultActive: false,
        category: "Kommunaler Metadatenkatalog Sachsen-Anhalt",
        run: function() {
            // Optionale Schlagworte
            domClass.add( "uiElement1409", "required" );
            // Anwendungsanschränkung
            domClass.add( "uiElementN026", "required" );
            // Operationen
            domClass.add( "uiElementN004", "required" );
            require("ingrid/hierarchy/objectLayout").deferredCreation.then(function() {
                // Klassifikation des Dienstes
                domClass.remove( "uiElementN022", ['required', 'optional'] );
                // ISO-Themenkategorie
                domClass.remove( "uiElement5060", ['hide'] );
                domClass.add( "uiElement5060", ['optional'] );
                // Fachliche Grundlage Geodatensatz
                domClass.remove( "uiElement3520", ['required'] );
                // Datensatz/Datenserie
                domClass.remove( "uiElement5061", ['required'] );
                // Schlüsselkatalog
                domClass.remove( "uiElement3535", ['required'] );
            });
        }
    })();
});
