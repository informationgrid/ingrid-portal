/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
    "dojo/_base/lang",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-style",
    "dojo/query",
    "dijit/registry",
    "ingrid/IgeEvents",
    "ingrid/layoutCreator",
    "ingrid/hierarchy/behaviours",
    "ingrid/widgets/UvpPhases"
], function (lang, dom, domClass, domStyle, query, registry, IgeEvents, creator, behaviours, UvpPhases) {

    return lang.mixin(behaviours, {

        uvpPhaseField: {
            title: "UVP Phasen Feld",
            description: "Hinzufügen von dynamischen Feldern",
            defaultActive: true,
            prefix: "uvp_",
            run: function () {

                // rename default fields
                query("#generalDescLabel label").addContent("Bekanntmachungstext", "only");
                query("#objectNameLabel label").addContent("Vorhabenbezeichnung", "only");
                query("#general .titleBar .titleCaption").addContent("Bekanntmachung", "only");
                query("#general .titleBar").attr("title", "Für die allgemeine Vorhabenbeschreibung sollte der Einfachheit halber der Text der Bekanntmachung verwendet werden.");
                dom.byId("generalAddressTableLabelText").innerHTML = "Federführende Behörde";
                
                // rename Objekte root node
                registry.byId("dataTree").rootNode.getChildren()[0].set("label", "Vorhaben");
                
                // do not override my address title
                IgeEvents.setGeneralAddressLabel = function() {};
                
                this.hideDefaultFields();
                
                this.createFields();
                
                // TODO: additional fields according to #490 and #473
            },
            
            hideDefaultFields: function() {
                domStyle.set("widget_objectName", "width", "550px");
                
                domClass.add(dom.byId("objectClassLabel").parentNode, "hide");
                domClass.add(dom.byId("objectOwnerLabel").parentNode, "hide");
                
                domClass.add(registry.byId("toolbarBtnISO").domNode, "hide");
                
                // hide all rubrics
                query(".rubric", "contentFrameBodyObject").forEach(function (item) {
                    if (item.id !== "general") {
                        domClass.add(item, "hide");
                    }
                });
            },
            
            createFields: function() {
                var rubric = "general";
                
                new UvpPhases().placeAt("generalContent");
                
                // creator.addToSection(rubric, creator.createDomTextarea({id: this.prefix + "description", name: "Bekanntmachungstext", help: "...", isMandatory: true, visible: "optional", rows: 10, style: "width:100%"}));

            }
        }

    });
});
