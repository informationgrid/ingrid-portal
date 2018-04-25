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
    "ingrid/utils/UDK"
], function(declare, cookie, Deferred, construct, query, topic, XContentPane, registry, message, udk) {

    return declare(null, {
        title: "UVP: Generelle Änderungen",
        description: "Hier werden Menüpunkte ausgeblendet und sonstige allgemeine Änderungen am Editor vorgenommen.",
        defaultActive: true,
        category: "UVP",
        type: "SYSTEM", // execute on IGE page load
        run: function() {
            this.replaceImages();

            this.hideMenuItems();

            this.disableInfoDialogs();

            this.adaptImportPage();

            this.addCssClasses();

            this.interceptHelpMessages();

            this.addMenuPages();

            query("head title").addContent("UVP Editor", "only");

            // rename Objekte root node
            topic.subscribe("/onPageInitialized", function(page) {
                if (page === "Hiearchy") {
                    registry.byId("dataTree").rootNode.getChildren()[0].set("label", message.get("uvp.tree.objectNode"));
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
                "<h1 style='color: white; padding: 5px'>" + message.get("uvp.title") + "</h1>",
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
                1000: { helpText: message.get("help.uvp.form.addresses.text"), name: message.get("help.uvp.form.addresses.title") },
                1010: { helpText: message.get("help.uvp.form.description.text"), name: message.get("help.uvp.form.description.title") },
                3000: { helpText: message.get("help.uvp.form.title.text"), name: message.get("help.uvp.form.title.title"), sample: message.get("help.uvp.form.title.example") }
            }
            udk.loadHelpMessageOrig = udk.loadHelpMessage;
            udk.loadHelpMessage = function(guiId) {
                if (interceptedGuiIds[guiId]) {
                    return (new Deferred()).resolve(interceptedGuiIds[guiId]);
                } else {
                    return this.loadHelpMessageOrig(guiId);
                }
            };
        },

        addMenuPages: function() {
            var menu = require("ingrid/menu");
            menu.additionalMenuItems.push({
                id: "menuPageUvpStatistic",
                label: "UVP Statistik",
                clickHandler: function() {
                    // self.selectChild("pageDashboard");
                    console.log("UVP Statistik");
                    menu.selectChild("pageUvpStatistic");
                }
            });

            registry.byId("stackContainer").addChild(
                new XContentPane({
                    id: "pageUvpStatistic",
                    title: "uvpStatistic",
                    layoutAlign: "client",
                    href: "mdek_uvp_statistic.jsp?c=" + userLocale,
                    preload: false,
                    scriptHasHooks: true,
                    executeScripts: true
                })
            );
        }

    })();
});
