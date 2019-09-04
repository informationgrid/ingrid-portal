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
        type: "SYSTEM",
        // execute on IGE page load
        run: function() {
            this.replaceImages();

            this.hideMenuItems();

            this.disableInfoDialogs();

            this.adaptImportPage();

            this.addCssClasses();

            this.interceptHelpMessages();

            this.disableRules();

            this.addPrivacyPolicyLink();

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
                1240: { helpText: message.get("help.mcloud.form.periodicity.text"), name: message.get("help.mcloud.form.periodicity.title"), sample: message.get("help.mcloud.form.periodicity.example") },
                3000: { helpText: message.get("help.mcloud.form.title.text"), name: message.get("help.mcloud.form.title.title"), sample: message.get("help.mcloud.form.title.example") },
                3500: { helpText: message.get("help.mcloud.form.crs.text"), name: message.get("help.mcloud.form.crs.title"), sample: message.get("help.mcloud.form.crs.example") },
                4100: { helpText: message.get("help.mcloud.form.address.institution.text"), name: message.get("help.mcloud.form.address.institution.title") },
                4400: { helpText: message.get("help.mcloud.form.address.street.text"), name: message.get("help.mcloud.form.address.street.title") },
                4415: { helpText: message.get("help.mcloud.form.address.city.text"), name: message.get("help.mcloud.form.address.city.title") },
                4430: { helpText: message.get("help.mcloud.form.address.communication.text"), name: message.get("help.mcloud.form.address.communication.title") },
                5030: { helpText: message.get("help.mcloud.form.resourceDate.text"), name: message.get("help.mcloud.form.resourceDate.title"), sample: message.get("help.mcloud.form.resourceDate.example") },
                7001: { helpText: message.get("help.mcloud.form.crsSection.text"), name: message.get("help.mcloud.form.crsSection.title") },
                7002: { helpText: message.get("help.mcloud.form.timeSection.text"), name: message.get("help.mcloud.form.timeSection.title") },
                7013: { helpText: message.get("help.mcloud.form.freeCoordsTransformed.text"), name: message.get("help.mcloud.form.freeCoordsTransformed.title") },
                10006: { helpText: message.get("help.mcloud.form.geothesaurus.text"), name: message.get("help.mcloud.form.geothesaurus.title") },
                10008: { helpText: message.get("help.mcloud.form.freeBBox.text"), name: message.get("help.mcloud.form.freeBBox.title") },
                10011: { helpText: message.get("help.mcloud.form.timeCoverage.text"), name: message.get("help.mcloud.form.timeCoverage.title") }
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

        disableRules: function() {

            var Rules = require("ingrid/hierarchy/rules");

            Rules.openDataLinkCheck = null;
            Rules.applyRule1 = function() {};
            Rules.applyRule2 = function() {};
            // Rule 3 shows/hides "to" date field based on whether "from - to"
            // option is selected or not. Keep it.
            Rules.applyRule5 = function() {};
            Rules.applyRule6 = function() {};
            Rules.applyRule7 = function() {};
            Rules.applyRuleThesaurusInspire = function() {};
            Rules.applyRuleServiceType = function() {};
            Rules._updateAtomFieldVisibility = function(value) {};
            Rules.applyRuleDownloadService = function() {};

            applyRuleThesaurusInspire = function() {};
            applyRule1 = function() {};
            applyRule2 = function() {};
            applyRule5 = function() {};
            applyRule6 = function() {};
            applyRule7 = function() {};
        },
        addPrivacyPolicyLink: function () {
            construct.create(
                "span", {
                    id: "privacyPolicy",
                    style: "float: right",
                    innerHTML: "<a href='/datenschutz' target='_blank'>Datenschutz</a>"
                },
                "logout",
                "after"
            );
            construct.create(
                "span", {
                    style: "float:right; padding:0px 5px 0px 5px;",
                    innerHTML: "|"
                },
                "privacyPolicy",
                "before"
            );
        }

    })();
});
