/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
/*
 * Special rules for form items
 */

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// removed stuff not needed anymore
// see svn log, "CLEAN UP: REMOVED NOT NEEDED JAVASCRIPT FROM rules_*.js"
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

// NOTICE: Most of these functions are "called from" Profile XML !

define(["dojo/_base/declare", "dojo/_base/array", "dojo/Deferred", "dojo/_base/lang", "dojo/dom-style", "dojo/topic", "dojo/query", "dojo/on", "dojo/aspect", "dojo/dom", "dojo/dom-class",
    "dijit/registry", "dojo/cookie",
    "ingrid/message", "ingrid/dialog",
    "ingrid/utils/Grid", "ingrid/utils/UI", "ingrid/utils/List", "ingrid/utils/Syslist"
], function(declare, array, Deferred, lang, style, topic, query, on, aspect, dom, domClass, registry, cookie, message, dialog, UtilGrid, UtilUI, UtilList, UtilSyslist) {

    var Rules = declare(null, {

        openDataLinkCheck: null,

        COOKIE_HIDE_OPEN_DATA_HINT: "ingrid.open.data.hint",

        applyRule1: function() {
            console.debug("apply rule 1");
            var tableData = UtilGrid.getTableData("ref1Representation");
            //var labelNode = dom.byId("ref1VFormatLabel");
            //var containerNode = labelNode.parentNode;

            if (array.some(tableData, function(item) {
                return (item.title == "1");
            })) {
                //		UtilUI.setRequiredState(labelNode, containerNode, true);
                registry.byId("ref1VFormatTopology").set("disabled", false);
                //registry.byId("ref1VFormatDetails").set('_canEdit', true);
                UtilGrid.updateOption("ref1VFormatDetails", "editable", true);
            } else {
                //	    UtilUI.setRequiredState(labelNode, containerNode, false);
                registry.byId("ref1VFormatTopology").set("value", null);
                registry.byId("ref1VFormatTopology").set("disabled", true);
                //registry.byId("ref1VFormatDetails").clear();
                //registry.byId("ref1VFormatDetails").set('_canEdit', false);
                UtilGrid.setTableData("ref1VFormatDetails", []);
                UtilGrid.updateOption("ref1VFormatDetails", "editable", false);
            }
        },

        applyRule2: function() {
            console.debug("apply rule 2");
            if (!domClass.contains("uiElementN014", "hide")) {
                if (registry.byId("thesaurusEnvExtRes").checked || UtilGrid.getTableData("thesaurusEnvTopics").length !== 0) {
                    UtilUI.setMandatory(dom.byId("uiElementN014"));
                    UtilUI.setMandatory(dom.byId("uiElementN015"));
                } else {
                    UtilUI.setOptional(dom.byId("uiElementN014"));
                    UtilUI.setOptional(dom.byId("uiElementN015"));
                }
            }
        },

        applyRule3: function(value) {
            console.debug("apply rule 3");
            if (value.indexOf("von") === 0) {
                domClass.remove("timeRefDate2Editor", "hide");
                var dateValue = registry.byId("timeRefDate1").get("value");
                var date2Widget = registry.byId("timeRefDate2");
                //date2Widget.attr("value", dateValue);
                if (dateValue) {
                    registry.byId("timeRefDate1").constraints.max = dateValue;
                    date2Widget.constraints.min = dateValue;
                }
                date2Widget.validate();
            } else {
                domClass.add("timeRefDate2Editor", "hide");
                // reset constraints for min/max date
                delete(registry.byId("timeRefDate1").constraints.max); // = null;
                delete(registry.byId("timeRefDate2").constraints.min); // = null;
                registry.byId("timeRefDate1").validate();
            }
            // date must not be null when value != ""
        },

        applyRule5: function() {
            console.debug("apply rule 5");
            if (!domClass.contains("uiElementN008", "hide")) {
                var snsHasBB = false;
                var freeHasBB = false;
                var snsData = UtilGrid.getTableData("spatialRefAdminUnit");
                var freeData = UtilGrid.getTableData("spatialRefLocation");

                var i;
                for (i = 0; i < snsData.length; ++i) {
                    if (snsData[i].longitude1 && snsData[i].longitude2 && snsData[i].latitude1 && snsData[i].latitude2)
                        snsHasBB = true;
                }
                for (i = 0; i < freeData.length; ++i) {
                    // The values stored in freeData[] can be strings or numbers (when first loaded)
                    // -> convert them to strings and check the size
                    var lon1Length = lang.trim(freeData[i].longitude1 + "").length;
                    var lon2Length = lang.trim(freeData[i].longitude2 + "").length;
                    var lat1Length = lang.trim(freeData[i].latitude1 + "").length;
                    var lat2Length = lang.trim(freeData[i].latitude2 + "").length;
                    if (lon1Length !== 0 && lon2Length !== 0 && lat1Length !== 0 && lat2Length !== 0)
                        freeHasBB = true;
                }

                if (snsHasBB || !freeHasBB) {
                    UtilUI.setMandatory(dom.byId("uiElementN006"));
                } else {
                    UtilUI.setOptional(dom.byId("uiElementN006"));
                }
                if (!snsHasBB) {
                    UtilUI.setMandatory(dom.byId("uiElementN008"));
                } else {
                    UtilUI.setOptional(dom.byId("uiElementN008"));
                }
            }
        },

        // If one of the fields contains data, all fields are mandatory
        applyRule6: function() {
            console.debug("apply rule 6");

            if (!domClass.contains("uiElementN010", "hide")) {
                var spatialRefAltMin = registry.byId("spatialRefAltMin");
                var spatialRefAltMax = registry.byId("spatialRefAltMax");
                var spatialRefAltMeasure = registry.byId("spatialRefAltMeasure");
                var spatialRefAltVDate = registry.byId("spatialRefAltVDate");


                if (spatialRefAltMin.get("value") || spatialRefAltMax.get("value") ||
                    spatialRefAltMeasure.get("value") || spatialRefAltVDate.get("value")) {
                    UtilUI.setMandatory(dom.byId("uiElementN010"));
                    UtilUI.setMandatory(dom.byId("uiElement1130"));
                    UtilUI.setMandatory(dom.byId("uiElement5020"));
                    UtilUI.setMandatory(dom.byId("uiElement5021"));
                    UtilUI.setMandatory(dom.byId("uiElement5022"));

                } else {
                    UtilUI.setOptional(dom.byId("uiElementN010"));
                    UtilUI.setOptional(dom.byId("uiElement1130"));
                    UtilUI.setOptional(dom.byId("uiElement5020"));
                    UtilUI.setOptional(dom.byId("uiElement5021"));
                    UtilUI.setOptional(dom.byId("uiElement5022"));
                }
            }
        },


        // If INSPIRE theme make additional fields mandatory
        applyRule7: function() {
            console.debug("apply rule 7");
            // checks dependent from object class !
            var objectClassStr = registry.byId("objectClass").get("value").toLowerCase(); // Value is a string: "Classx" where x is the class
            var objectClass = objectClassStr.substr(5, 1);

            // INSPIRE mandatory only in classes "Geo-Information/Karte" class 1
            if (objectClass != "1") {
                return;
            }

            var termsList = UtilList.tableDataToList(UtilGrid.getTableData("thesaurusInspire"));

            // show/remove DQ tables in class 1 dependent from themes
            if (objectClass == "1") {
                // hide all spans underneath ref1ContentDQTables div -> DQ tables
                query("#ref1ContentDQTables > span").forEach(this.hideDiv);

                // then show table dependent from theme

                // Coordinate reference systems (101)
                if (array.some(termsList, function(iTermKey) {
                    return (iTermKey == 101);
                })) {
                    // ???
                }
                // Geographical grid systems (102)
                if (array.some(termsList, function(iTermKey) {
                    return (iTermKey == 102);
                })) {
                    // ???
                }
                // Geographical names (103)
                if (array.some(termsList, function(iTermKey) {
                    return (iTermKey == 103);
                })) {
                    // ???
                }
                // Administrative units (104)
                if (array.some(termsList, function(iTermKey) {
                    return (iTermKey == 104);
                })) {
                    this.displayDiv(dom.byId("uiElement7509"));
                    this.displayDiv(dom.byId("uiElement7512"));
                    this.displayDiv(dom.byId("uiElement7515"));
                }
                // Addresses (105)
                if (array.some(termsList, function(iTermKey) {
                    return (iTermKey == 105);
                })) {
                    this.displayDiv(dom.byId("uiElement7509"));
                    this.displayDiv(dom.byId("uiElement7512"));
                    this.displayDiv(dom.byId("uiElement7513"));
                    this.displayDiv(dom.byId("uiElement7520"));
                    this.displayDiv(dom.byId("uiElement7526"));
                }
                // Cadastral parcels (106)
                if (array.some(termsList, function(iTermKey) {
                    return (iTermKey == 106);
                })) {
                    // ???
                }
                // Transport networks (107)
                if (array.some(termsList, function(iTermKey) {
                    return (iTermKey == 107);
                })) {
                    this.displayDiv(dom.byId("uiElement7509"));
                    this.displayDiv(dom.byId("uiElement7512"));
                    this.displayDiv(dom.byId("uiElement7513"));
                    this.displayDiv(dom.byId("uiElement7514"));
                    this.displayDiv(dom.byId("uiElement7515"));
                    this.displayDiv(dom.byId("uiElement7525"));
                    this.displayDiv(dom.byId("uiElement7526"));
                }
                // Hydrography (108)
                if (array.some(termsList, function(iTermKey) {
                    return (iTermKey == 108);
                })) {
                    this.displayDiv(dom.byId("uiElement7509"));
                    this.displayDiv(dom.byId("uiElement7512"));
                    this.displayDiv(dom.byId("uiElement7513"));
                    this.displayDiv(dom.byId("uiElement7515"));
                    this.displayDiv(dom.byId("uiElement7526"));
                    this.displayDiv(dom.byId("uiElement7527"));
                }
                // Protected sites (109)
                if (array.some(termsList, function(iTermKey) {
                    return (iTermKey == 109);
                })) {
                    this.displayDiv(dom.byId("uiElement7509"));
                }
            }
        },

        applyRuleThesaurusInspire: function() {},

        applyRuleServiceType: function() {
            var typesWithBehavior = [1, 2, 3, 5];
            var applySpecification = function(type, deleteEntry) {
                if (type == 1) UtilUI.updateEntryToConformityTable(38, deleteEntry);
                else if (type == 2) UtilUI.updateEntryToConformityTable(39, deleteEntry);
                else if (type == 3) UtilUI.updateEntryToConformityTable(40, deleteEntry);
                else if (type == 5) UtilUI.updateEntryToConformityTable(43, deleteEntry);
            };

            var updateSyslistServiceVersion = function(type) {
                if (type == 1) registry.byId("ref3ServiceVersion").columns[0].listId = 5151;
                else if (type == 2) registry.byId("ref3ServiceVersion").columns[0].listId = 5152;
                else if (type == 3) registry.byId("ref3ServiceVersion").columns[0].listId = 5153;
                else if (type == 4) registry.byId("ref3ServiceVersion").columns[0].listId = 5154;
                else registry.byId("ref3ServiceVersion").columns[0].listId = null;
            };

            // switch codelists when service type changed
            on(registry.byId("ref3ServiceType"), "Change", function(value) {
                var objClass = registry.byId("objectClass").get("value");
                if (objClass == "Class3") {
                    // change syslist of supported versions
                    updateSyslistServiceVersion(value);
                }
            });

            var handleConformityValue = function(value) {
                // remove all dependent types
                array.forEach(typesWithBehavior, function(type) {
                    applySpecification(type, true);
                });
                // add possibly new type
                applySpecification(value, false);
            };

            // on manual service type change automatically add/remove conformity entries to the table
            aspect.after(registry.byId("ref3ServiceType"), "closeDropDown", function() {
                var objClass = registry.byId("objectClass").get("value");
                if (objClass == "Class3") {
                    handleConformityValue(this.get("value"));
                }
            });

            // execute behaviour when a new document is created
            topic.subscribe("/afterInitDialog/CloseWizard", function() {
                var objClass = registry.byId("objectClass").get("value");
                if (objClass == "Class3") {
                    var value = registry.byId("ref3ServiceType").get("value");
                    handleConformityValue(value);
                }
            });

        },

        _updateAtomFieldVisibility: function(value) {
            if (value == "3") { // Downloadservice
                domClass.remove("uiElement3225", "hide");
            } else {
                registry.byId("ref3IsAtomDownload").set("checked", false);
                domClass.add("uiElement3225", "hide");
            }
        },

        applyRuleDownloadService: function() {
            var self = this;
            on(registry.byId("ref3ServiceType"), "Change", function(value) {
                self._updateAtomFieldVisibility(value);
            });
        },

        displayDiv: function(divElement) {
            if (divElement)
                domClass.remove(divElement, "hide");
        },

        hideDiv: function(divElement) {
            if (divElement) {
                domClass.add(divElement, "hide");
                //UtilGrid.setTableData(divElement.id, []);
            }
        }

    })();

    // add global function objects for backward compatibility
    applyRuleThesaurusInspire = Rules.applyRuleThesaurusInspire;
    applyRule1 = Rules.applyRule1;
    applyRule2 = Rules.applyRule2;
    applyRule3 = Rules.applyRule3;
    applyRule5 = Rules.applyRule5;
    applyRule6 = Rules.applyRule6;
    applyRule7 = lang.hitch(Rules, Rules.applyRule7); // method has this reference!

    return Rules;
});
