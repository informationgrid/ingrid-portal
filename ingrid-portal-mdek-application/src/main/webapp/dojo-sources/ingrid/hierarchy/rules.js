/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
                if (!dateValue) {
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

        applyRuleOpenData: function() {
            // hide open-data checkbox for classes 0 and 4
            topic.subscribe("/onObjectClassChange", function(data) {
                if (data.objClass === "Class0" || data.objClass === "Class4") {
                    domClass.add(dom.byId("uiElement6010"), "hide");
                    // also uncheck openData checkbox, so that categories table must not be
                    // displayed according to the state
                    registry.byId("isOpenData").set("checked", false);
                } else {
                    domClass.remove(dom.byId("uiElement6010"), "hide");
                }
            });

            // this event will also execute when object is loaded
            // here we have to make sure the categories table is shown/removed correctly
            on(registry.byId("isOpenData"), "Change", function(isChecked) {
                // get link for use constraints which will be changed depending on the
                // state of the open data checkbox
                var link = dom.byId("availabilityUseConstraintsLink");
                var onclickValue = link.attributes.onclick.value;

                if (isChecked) {

                    // show categories
                    domClass.remove("uiElement6020", "hide");

                    // make field mandatory
                    domClass.add("uiElement6020", "required");

                    // change codelist for 'availabilityUseConstraints'
                    link.attributes.onclick.value = onclickValue.replace(/(listId:).*'}/, "$1 '6500'}");

                    // add check for url reference of type download when publishing
                    // we check name and not id cause is combo box ! id not adapted yet if not saved !
                    this.openDataLinkCheck = topic.subscribe("/onBeforeObjectPublish", function( /*Array*/ notPublishableIDs) {
                        // get name of codelist entry for entry-id "9990" = "Download of data"/"Datendownload"
                        var entryNameDownload = UtilSyslist.getSyslistEntryName(2000, 9990);
                        var data = UtilGrid.getTableData("linksTo");
                        var containsDownloadLink = array.some(data, function(item) {
                            if (item.relationTypeName == entryNameDownload) return true;
                        });
                        if (!containsDownloadLink)
                            notPublishableIDs.push( ["linksTo", message.get("validation.error.missing.download.link")] );
                    });
                } else {
                    // change codelist for 'availabilityUseConstraints'
                    link.attributes.onclick.value = onclickValue.replace(/(listId:).*'}/, "$1 '6020'}");

                    // hide categories
                    domClass.add("uiElement6020", "hide");

                    // revert field mandatory
                    domClass.remove("uiElement6020", "required");

                    // unregister from check for download link
                    if (this.openDataLinkCheck)
                        this.openDataLinkCheck.remove();
                }
            });

            // behavior when the checkbox is actively clicked by a user
            // only actions that shall modify data once and has nothing to do with the view!
            var rules = this;
            on(registry.byId("isOpenData"), "Click", function(/*evnt*/) {
                var isChecked = this.checked;
                if (isChecked) {
                    var def = new Deferred();
                    var self = this;
                    if (cookie(rules.COOKIE_HIDE_OPEN_DATA_HINT) !== "true") {
                        dialog.show(message.get("dialog.general.info"), message.get("hint.selectOpenData"), dialog.INFO,
                            [
                                { caption: message.get("general.ok.hide.next.time"), type: "checkbox",
                                    action: function(newValue) {
                                        cookie(rules.COOKIE_HIDE_OPEN_DATA_HINT, newValue, {expires: 730});
                                    }
                                },
                                { caption: message.get("general.cancel"),
                                    action: function() {
                                        // reset checkbox state
                                        self.set("checked", false);
                                    }
                                },
                                { caption: message.get("general.ok"),
                                    action: function() {
                                        def.resolve();
                                    }
                                }
                            ]);
                    } else {
                        def.resolve();
                    }

                    def.then(function() {
                        // automatically replace access constraint with "keine"
                        var data = [{title: UtilSyslist.getSyslistEntryName(6010, 1) }];
                        UtilGrid.setTableData('availabilityAccessConstraints', data);
                    });
                    
                } else {
                    // remove all categories
                    UtilGrid.setTableData("categoriesOpenData", []);
                }
            });
        },

        applyRuleThesaurusInspire: function() {

            var applySpecification = function(inspireId, deleteEntry) {
                if (inspireId == 103) {
                    UtilUI.updateEntryToConformityTable(4, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 18);
                } else if (inspireId == 104) {
                    UtilUI.updateEntryToConformityTable(2, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 16);
                } else if (inspireId == 105) {
                    UtilUI.updateEntryToConformityTable(1, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 15);
                } else if (inspireId == 106) {
                    UtilUI.updateEntryToConformityTable(3, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 17);
                } else if (inspireId == 107) {
                    UtilUI.updateEntryToConformityTable(7, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 7);
                } else if (inspireId == 108) {
                    UtilUI.updateEntryToConformityTable(5, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 2);
                } else if (inspireId == 109) {
                    UtilUI.updateEntryToConformityTable(6, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 14);
                } else if (inspireId == 201) {
                    UtilUI.updateEntryToConformityTable(14, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 19);
                } else if (inspireId == 202) {
                    UtilUI.updateEntryToConformityTable(15, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 20);
                } else if (inspireId == 203) {
                    UtilUI.updateEntryToConformityTable(16, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 21);
                } else if (inspireId == 204) {
                    UtilUI.updateEntryToConformityTable(17, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 22);
                } else if (inspireId == 301) {
                    UtilUI.updateEntryToConformityTable(18, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 23);
                } else if (inspireId == 302) {
                    UtilUI.updateEntryToConformityTable(19, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 24);
                } else if (inspireId == 303) {
                    UtilUI.updateEntryToConformityTable(20, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 25);
                } else if (inspireId == 304) {
                    UtilUI.updateEntryToConformityTable(21, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 26);
                } else if (inspireId == 305) {
                    UtilUI.updateEntryToConformityTable(22, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 27);
                } else if (inspireId == 306) {
                    UtilUI.updateEntryToConformityTable(23, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 28);
                } else if (inspireId == 307) {
                    UtilUI.updateEntryToConformityTable(24, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 29);
                } else if (inspireId == 308) {
                    UtilUI.updateEntryToConformityTable(25, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 30);
                } else if (inspireId == 309) {
                    UtilUI.updateEntryToConformityTable(26, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 31);
                } else if (inspireId == 310) {
                    UtilUI.updateEntryToConformityTable(27, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 32);
                } else if (inspireId == 311) {
                    UtilUI.updateEntryToConformityTable(28, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 33);
                } else if (inspireId == 312) {
                    UtilUI.updateEntryToConformityTable(29, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 34);
                } else if (inspireId == 313 || inspireId == 314) {
                    UtilUI.updateEntryToConformityTable(30, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 35);
                } else if (inspireId == 315) {
                    UtilUI.updateEntryToConformityTable(31, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 36);
                } else if (inspireId == 316) {
                    UtilUI.updateEntryToConformityTable(32, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 37);
                } else if (inspireId == 317) {
                    UtilUI.updateEntryToConformityTable(33, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 38);
                } else if (inspireId == 318) {
                    UtilUI.updateEntryToConformityTable(34, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 39);
                } else if (inspireId == 319) {
                    UtilUI.updateEntryToConformityTable(35, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 40);
                } else if (inspireId == 320) {
                    UtilUI.updateEntryToConformityTable(36, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 41);
                } else if (inspireId == 321) {
                    UtilUI.updateEntryToConformityTable(37, deleteEntry);
                    UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 42);
                }
            };

            // react when inspire topics has been added
            aspect.after(UtilGrid.getTable("thesaurusInspire"), "onCellChange", function(result, args) {
            	var msg = args[0];
                var objClass = registry.byId("objectClass").get("value");
                // only react if class == 1
                if (objClass == "Class1") {
                    // remove old dependent values
                    if (msg.oldItem) {
                        applySpecification(msg.oldItem.title, true);
                    }
                    // add new dependent value
                    applySpecification(msg.item.title, false);
                }
            });

            // remove specific entry from conformity table when inspire topic was deleted
            aspect.after(UtilGrid.getTable("thesaurusInspire"), "onDeleteItems", function(result, args) {
            	var msg = args[0];
                var objClass = registry.byId("objectClass").get("value");
                // only react if class == 1
                if (objClass == "Class1") {
                    array.forEach(msg.items, function(item) {
                        applySpecification(item.title, true);
                    });
                }
            });
        },

        applyRuleServiceType: function() {
            var typesWithBehavior = [1, 2, 3, 5];
            var applySpecification = function(type, deleteEntry) {
                if (type == 1) UtilUI.updateEntryToConformityTable(38, deleteEntry);
                else if (type == 2) UtilUI.updateEntryToConformityTable(39, deleteEntry);
                else if (type == 3) UtilUI.updateEntryToConformityTable(40, deleteEntry);
                else if (type == 5) UtilUI.updateEntryToConformityTable(43, deleteEntry);
            };

            // react when inspire topics has been added
            on(registry.byId("ref3ServiceType"), "Change", function(value) {
                var objClass = registry.byId("objectClass").get("value");
                if (objClass == "Class3") {
                    // remove all dependent types
                    array.forEach(typesWithBehavior, function(type) {
                        applySpecification(type, true);
                    });
                    // add possibly new type
                    applySpecification(value, false);
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
