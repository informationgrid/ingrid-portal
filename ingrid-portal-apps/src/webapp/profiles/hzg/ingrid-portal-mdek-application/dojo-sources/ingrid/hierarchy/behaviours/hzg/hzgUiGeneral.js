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
    "dijit/MenuItem",
    "dijit/MenuSeparator",
    "dijit/registry",
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/aspect",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-construct",
    "ingrid/dialog",
    "ingrid/hierarchy/dirty",
    "ingrid/layoutCreator",
    "ingrid/menu",
    "ingrid/message",
    "ingrid/utils/UI"
], function(MenuItem, MenuSeparator, registry, array, declare, lang, aspect, dom, domClass, construct, dialog, dirty, creator, menu, message, UtilUI) {

    return declare(null, {
        title: "HZG-UI",
        description: "Anpassungen der Editor-Oberfläche für das HZG",
        defaultActive: true,
        category: "HZG",
        run: function() {
            this._generalModifications();
            var promise = this._createObservedPropertiesFields();

            return promise;
        },

        _generalModifications: function() {

            // Allgemeines / Vorschaugrafik
            UtilUI.setShow("uiElement5100");
            registry.byId("generalPreviewImageTable").reinitLastColumn(true);

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
                    registry.byId("ref1VFormatDetails").reinitLastColumn(true);
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

            // Datenqualität / Datendefizit
            domClass.remove("uiElement3565", "show");
            domClass.add("uiElement3565", "optional");

            // Zeitbezug / Durch die Ressource Abgedeckte Zeitspanne
            UtilUI.setShow("uiElementN011");

            // Zeitbezug / Status
            UtilUI.setShow("uiElement1220");

            // Zeitbezug / Periodizität
            UtilUI.setShow("uiElement1240");

            // Zusatzinformation
            var isInspireRelevant = registry.byId("isInspireRelevant").get("checked");
            if (isInspireRelevant) {
                domClass.add("uiElementN024", "required");
            } else {
                domClass.remove("uiElementN024", "required");
            }
        },

        _createObservedPropertiesFields: function() {
            // Observed properties
            var category = "Observed Properties";
            var additionalFields = require('ingrid/IgeActions').additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            var categoryContainer = creator.createRubric({
                id: "observedProperties",
                label: "Observed Properties",
                help: message.get("ui.obj.observedProperty.help")
            });
            construct.place(categoryContainer, 'links', 'after');

            // Observed Property table
            // First initialise the context menu
            this._createObsPropGridContextMenu();

            // Next define the table structure
            var structure = [
                {
                    field: "observedPropertyName",
                    name: message.get("table.observedProperty.name"),
                    editable: false,
                    width: "300px"
                },
                {
                    field: "observedPropertyXmlDescription",
                    name: message.get("table.observedProperty.xmlDescription"),
                    editable: false,
                    width: "auto"
                }
            ];

            // Finally create the table
            var id = "observedPropertiesDataGrid";
            creator.createDomDataGrid(
                {id: id, name: "Observed Properties", help:message.get("table.observedProperty.help"), style: "width: 100%", contextMenu: "OBSERVED_PROPERTIES"},
                structure, categoryContainer
            );
            newFieldsToDirtyCheck.push(id);
            additionalFields.push(registry.byId(id));

            var linkId = "observedPropertiesTableLink";
            var linkText = message.get("dialog.observedProperty.title");
            var linkOnClick = "require('ingrid/dialog').showPage(pageDashboard.getLocalizedTitle('observedProperty'), 'dialogs/mdek_observed_property_dialog.jsp?c='+userLocale, 600, 350, true, {});";
            var span = this._createLinkToDialog(linkId, linkText, linkOnClick);
            var node = dom.byId(id).parentElement;
            construct.place(span, node, 'before');

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));

            return registry.byId(id).promiseInit;
        },

        _createLinkToDialog: function(linkId, linkText, linkOnClick) {
            var span = document.createElement("span");
            span.setAttribute("class", "functionalLink");

            var img = document.createElement("img");
            img.setAttribute("src", "img/ic_fl_popup.gif");
            img.setAttribute("width", "10");
            img.setAttribute("height", "9");
            img.setAttribute("alt", "Popup");

            var link = document.createElement("a");
            link.setAttribute("id", linkId);
            link.setAttribute("href", "javascript:void(0);");
            link.setAttribute("onclick", linkOnClick);
            link.setAttribute("title", linkText + " [Popup]");
            link.textContent = linkText;

            span.appendChild(img);
            span.appendChild(link);

            return span;
        },

        _createObsPropGridContextMenu: function() {
            var type = "OBSERVED_PROPERTIES";
            var contextMenu = menu.initContextMenu({contextMenu: type});
            contextMenu.addChild(new MenuSeparator());
            contextMenu.addChild(new MenuItem({
                id: "menuEditClicked_" + type,
                label: message.get('contextmenu.table.editClicked'),
                onClick: function() {
                    var rowData = clickedSlickGrid.getData()[clickedRow];
                    var dialogData = {
                        gridId: clickedSlickGridProperties.id,
                        selectedRow: rowData
                    };
                    dialog.showPage(message.get("dialog.operations.title"), 'dialogs/mdek_observed_property_dialog.jsp?c=' + userLocale, 600, 300, true, dialogData);
                }
            }));
        }
    })();
});
