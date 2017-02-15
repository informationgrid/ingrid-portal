/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
    "dojo/_base/array",
    "dojo/dom-construct",
    "dojo/query",
    "dojo/topic",
    "dijit/registry",
    "ingrid/message",
    "ingrid/utils/Syslist"
], function(declare, array, construct, query, topic, registry, message, UtilSyslist) {

    return declare(null, {
        title: "UVP: Dokumenten Typen und Verhalten",
        description: "Definition der Dokumententypen und Beeinflussing der Erstellung",
        defaultActive: true,
        type: "SYSTEM",
        specialNodes: [message.get("uvp.form.categories.uvp"), message.get("uvp.form.categories.uvpNegative"), message.get("uvp.form.categories.uvpForeign"), message.get("uvp.form.categories.uvpInFront")],
        run: function() {

            this.addIconClasses();

            var self = this;
            topic.subscribe("/afterInitDialog/ChooseWizard", function(data) {
                var uvpType = null;

                // remove all assistants
                data.assistants.splice(0, data.assistants.length);

                var parentTitlePos = self.checkForSpecialParent();
                // var parentTitle = registry.byId("dataTree").selectedNode.getParent().item.title;
                console.debug("parent title is: ", self.specialNodes[parentTitlePos]);

                if (parentTitlePos === 0) {
                    uvpType = array.filter(data.types, function(t) { return t[1] === "10"; });
                    data.types.splice(0, data.types.length);
                    data.types.push(uvpType[0]);

                } else if (parentTitlePos === 1) {
                    uvpType = array.filter(data.types, function(t) { return t[1] === "12"; });
                    data.types.splice(0, data.types.length);
                    data.types.push(uvpType[0]);

                } else if (parentTitlePos === 2) {
                    uvpType = array.filter(data.types, function(t) { return t[1] === "11"; });
                    data.types.splice(0, data.types.length);
                    data.types.push(uvpType[0]);

                } else if (parentTitlePos === 3) {
                    uvpType = array.filter(data.types, function(t) { return t[1] === "13" || t[1] === "14"; });
                    data.types.splice(0, data.types.length);
                    array.forEach(uvpType, function(t) { data.types.push(t); });

                }
            });

            // load custom syslists
            topic.subscribe("/collectAdditionalSyslistsToLoad", function(ids) {
                ids.push(8001, 9000);
            });

            // get availbale object classes from codelist 8001
            UtilSyslist.listIdObjectClass = 8001;

        },

        /**
         * Get the array position of the special parent node, defined in "specialNodes"-array.
         */
        checkForSpecialParent: function() {
            var tree = registry.byId("dataTree");
            var newNode = tree.getNodesByItem("newNode")[0];

            // if new node is not yet created (because parent is still expanding)
            // use the selected node as the parent
            var parent = newNode ? newNode.getParent() : tree.selectedNode;

            // search for the special parent up to the root node
            while (parent && parent.item) {
                var position = this.specialNodes.indexOf(parent.item.title);
                if (position !== -1) {
                    return position;
                }
                parent = parent.getParent();
            }
            return null;
        },

        addIconClasses: function() {
            query("head")[0].appendChild(
                // construct.toDom('<link rel="stylesheet" type="text/css" href="lightbox_stylesheet.css">')
                construct.toDom(
                    '<style type="text/css">' +
                    '.TreeIconClass10, .TreeIconClass10_V {background-image:url("img/uvp/uvp_icons.gif");}' +
                    '.TreeIconClass10_B {background-image:url("img/uvp/uvp_icons.gif"); background-position: -16px;}' +
                    '.TreeIconClass10_BV {background-image:url("img/uvp/uvp_icons.gif"); background-position: -32px;}' +
                    '.TreeIconClass12, .TreeIconClass12_V {background-image:url("img/uvp/uvp_icons.gif"); background-position: -48px;}' +
                    '.TreeIconClass12_B {background-image:url("img/uvp/uvp_icons.gif"); background-position: -64px;}' +
                    '.TreeIconClass12_BV {background-image:url("img/uvp/uvp_icons.gif"); background-position: -80px;}' +
                    '.TreeIconClass11, .TreeIconClass11_V {background-image:url("img/uvp/uvp_icons.gif"); background-position: -96px;}' +
                    '.TreeIconClass11_B {background-image:url("img/uvp/uvp_icons.gif"); background-position: -112px;}' +
                    '.TreeIconClass11_BV {background-image:url("img/uvp/uvp_icons.gif"); background-position: -128px;}' +
                    '.TreeIconClass13, .TreeIconClass11_V {background-image:url("img/uvp/uvp_icons.gif"); background-position: -144px;}' +
                    '.TreeIconClass13_B {background-image:url("img/uvp/uvp_icons.gif"); background-position: -160px;}' +
                    '.TreeIconClass13_BV {background-image:url("img/uvp/uvp_icons.gif"); background-position: -176px;}' +
                    '.TreeIconClass14, .TreeIconClass11_V {background-image:url("img/uvp/uvp_icons.gif"); background-position: -192px;}' +
                    '.TreeIconClass14_B {background-image:url("img/uvp/uvp_icons.gif"); background-position: -208px;}' +
                    '.TreeIconClass14_BV {background-image:url("img/uvp/uvp_icons.gif"); background-position: -224px;}' +
                    '</style>')
            );
        }
    })();
});