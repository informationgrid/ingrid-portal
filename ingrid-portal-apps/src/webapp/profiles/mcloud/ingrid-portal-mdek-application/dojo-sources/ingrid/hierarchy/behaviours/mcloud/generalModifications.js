/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
define(["dojo/_base/declare",
    "dojo/cookie",
    "dojo/Deferred",
    "dojo/dom-construct",
    "dojo/query",
    "dojo/topic",
    "dojox/layout/ContentPane",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/UDK",
    "ingrid/utils/Security"
], function(declare, cookie, Deferred, construct, query, topic, XContentPane, registry, message, udk, UtilSecurity) {

    return declare(null, {
        title: "Generelle Änderungen",
        description: "Hier werden Menüpunkte ausgeblendet und sonstige allgemeine Änderungen am Editor vorgenommen.",
        defaultActive: true,
        category: "mcloud",
        type: "SYSTEM", // execute on IGE page load
        run: function() {
            this.replaceImages();

            this.hideMenuItems();

            this.disableInfoDialogs();

            this.adaptImportPage();

            this.addCssClasses();

            this.interceptHelpMessages();

            query("head title").addContent("mcloud Editor", "only");

            // rename Objekte root node
            topic.subscribe("/onPageInitialized", function(page) {
                if (page === "Hiearchy") {
                    registry.byId("dataTree").rootNode.getChildren()[0].set("label", message.get("mcloud.tree.objectNode"));
                }
            });
        },

        hideMenuItems: function() {
            topic.subscribe("/onMenuBarCreate", function(excludedItems) {
                excludedItems.push("menuPageStatistics", "menuPageQualityEditor", "menuPageQualityAssurance",
                    "menuPageResearchThesaurus", "menuPageAdminAnalysis", "menuPageAdminURL", "menuPageAdminDeleteAddress",
                    "menuPageAdminSearchTerms", "menuPageAdminLocations");
            });
            // TODO: remove stack container or do not let them initialized
            // registry.byId("stackContainer").removeChild(registry.byId("pageStatistics"));
        },

        disableInfoDialogs: function() {
            var MenuActions = require("ingrid/MenuActions");
            // set cookie to hide copy dialog
            cookie(MenuActions.COOKIE_HIDE_COPY_HINT, true, {
                expires: 730
            });
        },

        replaceImages: function() {
            // var titleImageNode = query("#title img")[0];
            // domAttr.set(titleImageNode, "src", "xxx.gif");
            query("#title").addContent(
                "<h1 style='color: white; padding: 5px'>" + message.get("mcloud.title") + "</h1>",
                "only"
            );
        },

        adaptImportPage: function() {
            // only allow InGridCatalog datatypes for import
            topic.subscribe("/afterInitDialog/Import", function( data ) {
                var filteredTypes = data.types.filter(function(t) { return t.value === "igc"; });
                data.types.splice(0, data.types.length);
                data.types.push(filteredTypes[0]);
            });
        },

        addCssClasses: function() {
            query("head")[0].appendChild(
                // construct.toDom('<link rel="stylesheet" type="text/css" href="lightbox_stylesheet.css">')
                construct.toDom(
                    '<style type="text/css">' +
                    '#objSearch .expandContent, #adrSearch .expandContent {display: none;} ' +
                    '#menuItemSeparatorPublicationCondition,#menuItemPublicationCondition1,#menuItemPublicationCondition2,#menuItemPublicationCondition3 { display: none; }' +
                    '</style>')
            );
        },

        interceptHelpMessages: function() {
            var interceptedGuiIds = {
                1000: { helpText: message.get("help.mcloud.form.addresses.text"), name: message.get("help.mcloud.form.addresses.title") },
                1010: { helpText: message.get("help.mcloud.form.description.text"), name: message.get("help.mcloud.form.description.title") },
                3000: { helpText: message.get("help.mcloud.form.title.text"), name: message.get("help.mcloud.form.title.title"), sample: message.get("help.mcloud.form.title.example") }
            }
            udk.loadHelpMessageOrig = udk.loadHelpMessage;
            udk.loadHelpMessage = function(guiId) {
                if (interceptedGuiIds[guiId]) {
                    return (new Deferred()).resolve(interceptedGuiIds[guiId]);
                } else {
                    return this.loadHelpMessageOrig(guiId);
                }
            };
        }

    })();
});
