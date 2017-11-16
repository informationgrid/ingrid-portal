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
    "dojo/_base/lang",
    "dojo/aspect",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-style",
    "dojo/dom-construct",
    "dojo/query",
    "dojo/topic",
    "dijit/registry",
    "dijit/form/Button",
    "ingrid/message",
    "ingrid/layoutCreator",
    "ingrid/IgeEvents",
    "ingrid/grid/CustomGridEditors",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/hierarchy/dirty",
    "ingrid/utils/Grid",
    "ingrid/utils/Syslist",
    "ingrid/widgets/UvpPhases",
    "ingrid/widgets/NominatimSearch"
], function(declare, array, lang, aspect, dom, domClass, domStyle, construct, query, topic, registry, Button, message, creator, IgeEvents, Editors, Formatters, dirty, UtilGrid, UtilSyslist, UvpPhases, NominatimSearch) {
    return declare(null, {
        title: "UVP: Formularfelder",
        description: "Hier werden die zusätzlichen Felder im Formular erzeugt sowie überflüssige ausgeblendet.",
        defaultActive: true,
        category: "UVP",
        prefix: "uvp_",
        uvpPhaseField: null,
        params: [{id: "categoryCodelist", label: "Codelist (Kategorie)", "default": 9000}],
        run: function() {

            // rename default fields
            query("#general .titleBar .titleCaption")
                .addContent(message.get("uvp.form.consideration"), "only")
                .style("cursor", "default")
                .attr('onclick', undefined);
            query("#general .titleBar").attr("title", message.get("uvp.form.consideration.tooltip"));

            // do not override my address title
            IgeEvents.setGeneralAddressLabel = function() { };

            this.hideDefaultFields();

            this.createFields();

            // TODO: additional fields according to #490 and #473

            var self = this;
            topic.subscribe("/onObjectClassChange", function(clazz) {
                self.prepareDocument(clazz);
            });

            // disable editing the address table and automatically set point of contact type
            UtilGrid.getTable("generalAddress").options.editable = false;

            var handleAddressAdd = function() {
                var pointOfContactName = UtilSyslist.getSyslistEntryName(505, 7);
                UtilGrid.addTableDataRow("generalAddress", {nameOfRelation: pointOfContactName});
            };
            topic.subscribe("/onBeforeDialogAccept/AddressesFromTree", handleAddressAdd);
            topic.subscribe("/onBeforeDialogAccept/Addresses", handleAddressAdd);
        },

        prepareDocument: function(classInfo) {
            console.log("Prepare document for class: ", classInfo);
            var objClass = classInfo.objClass;
            if (objClass === "Class10") { // UVP Vorhaben
                domClass.remove("uiElementAdduvpgCategory", "hide");
                domClass.remove("uiElementAdduvpNeedsExamination", "hide");
                query("#generalDescLabel label").addContent(message.get("uvp.form.generalDescription"), "only");
                query("#generalAddressTableLabel label").addContent(message.get("uvp.form.address"), "only");
                this.uvpPhaseField.availablePhases = [1, 2, 3];

            } else if (objClass === "Class11") { // ausländische
                domClass.add("uiElementAdduvpgCategory", "hide");
                domClass.add("uiElementAdduvpNeedsExamination", "hide");
                query("#generalDescLabel label").addContent(message.get("uvp.form.foreign.generalDescription"), "only");
                query("#generalAddressTableLabel label").addContent(message.get("uvp.form.foreign.address"), "only");
                this.uvpPhaseField.availablePhases = [1, 3];

            } else if (objClass === "Class12") { // negative (not implemented yet)

            } else if (objClass === "Class13" || objClass === "Class14") { // Raumordnungsverfahren or Linienbestimmungen
                domClass.remove("uiElementAdduvpgCategory", "hide");
                domClass.add("uiElementAdduvpNeedsExamination", "hide");
                query("#generalDescLabel label").addContent(message.get("uvp.form.spatial.generalDescription"), "only");
                query("#generalAddressTableLabel label").addContent(message.get("uvp.form.spatial.address"), "only");
                this.uvpPhaseField.availablePhases = [1, 2, 3];
            }
        },

        hideDefaultFields: function() {
            domStyle.set("widget_objectName", "width", "550px");

            domClass.add(dom.byId("objectClassLabel").parentNode, "hide");
            domClass.add(dom.byId("objectOwnerLabel").parentNode, "hide");

            domClass.add(registry.byId("toolbarBtnISO").domNode, "hide");

            domClass.add("uiElement5000", "hide");
            domClass.add("uiElement5100", "hide");
            domClass.add("uiElement5105", "hide");
            domClass.add("uiElement6005", "hide");
            domClass.add("uiElement6010", "hide");

            // hide all rubrics
            query(".rubric", "contentFrameBodyObject").forEach(function(item) {
                if (item.id !== "general") {
                    domClass.add(item, "hide");
                }
            });
        },

        _getCategoryCodelist: function() {
            var codeListParam = array.filter(this.params, function(p) { return p.id === "categoryCodelist"; })[0];
            return codeListParam.value ? +codeListParam.value : +codeListParam["default"];
        },

        _sortUvpNumbers: function(data) {

            // get the string from a text without the number part (e.g. (6,'6a') => 'a')
            var getStringFromNumText = function(number, text) {
                var str = text;
                if (number && number !== -1) {
                    var len = (number+"").length;
                    if (len !== text.length) {
                        str = text.substr(len);
                    }
                }
                return str;
            };

            // the amazing sort function
            data.sort( function(a, b) {
                var partA = a[0].split("-");
                var partB = b[0].split("-");

                // check first alphanumeric part
                if (partA[0] === partB[0]) {
                    var partANumber = partA[1].split(".");
                    var partBNumber = partB[1].split(".");

                    // check second numeric part
                    for (var i=0; i<partANumber.length; i++) {
                        if (partANumber[i] !== partBNumber[i]) {
                            var intANumber = -1;
                            var intBNumber = -1;
                            // check if we can parse the string to a number (e.g. '6a', '4', but not 'a')
                            if (isNaN(partANumber[i])) {
                                intANumber = parseInt(partANumber[i]);
                            } else {
                                intANumber = +partANumber[i];
                            }
                            if (isNaN(partBNumber[i])) {
                                intBNumber = parseInt(partBNumber[i]);
                            } else {
                                intBNumber = +partBNumber[i];
                            }
                            
                            // if it's still is not a number then compare as string (e.g. 'aa')
                            if (isNaN(intANumber) && isNaN(intBNumber)) {
                                return partANumber[i] < partBNumber[i] ? -1 : 1;
                            } else if (isNaN(intANumber)) {
                                return 1;
                            } else if (isNaN(intBNumber)) {
                                return -1;
                            }

                            // if a number is same then we expect a string inside at least one number
                            // otherwise the condition above would have taken care already
                            if (intANumber === intBNumber && intANumber !== -1) {
                                var s1 = getStringFromNumText(intANumber, partANumber[i]);
                                var s2 = getStringFromNumText(intBNumber, partBNumber[i]);

                                // sort strings
                                if (s1 === s2) return 0;
                                else {
                                    return s1 < s2 ? -1 : 1;
                                }
                                
                            } else {
                                return intANumber < intBNumber ? -1 : 1;
                            }
                        } else if (i === partANumber.length-1) {
                            if (partANumber.length < partBNumber.length) {
                                return -1;
                            }
                        }

                    }
                } else {
                    return partA[0] < partB[0] ? -1 : 1;
                }
            });
        },

        createFields: function() {
            var rubric = "general";
            var additionalFields = require("ingrid/IgeActions").additionalFieldWidgets;
            var newFieldsToDirtyCheck = [];

            this.uvpPhaseField = new UvpPhases({ id: "UVPPhases" });
            this.uvpPhaseField.placeAt("generalContent");

            this.createSpatial(rubric);
            newFieldsToDirtyCheck.push(this.prefix + "spatialValue");

            /**
             * Vorhabensnummer
             */
            var codelist = this._getCategoryCodelist();
            var structure = [
                {
                    field: 'categoryId',
                    name: 'Kategorie',
                    type: Editors.SelectboxEditor,
                    editable: true,
                    listId: codelist,
                    sortFunc: this._sortUvpNumbers,
                    formatter: lang.partial(Formatters.SyslistCellFormatter, codelist),
                    partialSearch: true
                }
            ];

            var id = "uvpgCategory";
            newFieldsToDirtyCheck.push(id);
            creator.createDomDataGrid(
                { id: id, name: message.get("uvp.form.categoryIds"), help: message.get("uvp.form.categoryIds.helpMessage"), isMandatory: true, visible: "optional", rows: "4", forceGridHeight: false, style: "width:100%" },
                structure, rubric
            );
            var categoryWidget = registry.byId(id);
            domClass.add(categoryWidget.domNode, "hideTableHeader");
            additionalFields.push(categoryWidget);

            /**
             * Checkbox für Vorprüfung
             */
            id = "uvpNeedsExamination";
            var checkbox = creator.createDomCheckbox({
				id : id,
				name : message.get("uvp.form.checkExamination"),
				help : message.get("uvp.form.checkExamination.helpMessage"),
				isMandatory : true,
				visible : "optional",
				rows : "4",
				forceGridHeight : false,
				style : "width:100%"
			});
            newFieldsToDirtyCheck.push(id);
            construct.place(checkbox, rubric);
            additionalFields.push(registry.byId(id));

            array.forEach(newFieldsToDirtyCheck, lang.hitch(dirty, dirty._connectWidgetWithDirtyFlag));
        },

        createSpatial: function(rubric) {
            var self = this;
            var zoomToBoundingBox = function() {
                var val = spatialInput.get("value");
                if (val !== "") {
                    var fixedValue = val.indexOf(": ") === -1 ? val : val.substr(val.indexOf(": ") + 2);
                    var arrayValue = fixedValue.split(',');

                    self.nominatimSearch._zoomToBoundingBox([arrayValue[1], arrayValue[3], arrayValue[0], arrayValue[2]], true);
                } else {
                    // if no bounding box was set
                    self.nominatimSearch._removeBoundingBox();
                }
            };

            /*new Button({
                id: this.prefix + "btnSpatialValueShow",
                label: message.get("uvp.form.showSpatial"),
                "class": "optional show right",
                onClick: function() {
                    zoomToBoundingBox();
                    domClass.remove("uvpNominatimSearch", "hide");
                    self.nominatimSearch.map.invalidateSize();
                    dom.byId("uvp_spatial").focus();
                }
            }).placeAt(rubric);
            // layout fix!
            construct.place(construct.toDom("<div class='clear'></div>"), rubric);*/

            /**
             * Map
             */
            this.nominatimSearch = new NominatimSearch({
                id: "uvpNominatimSearch",
                collapseOnEmptyInput: true,
                scrollWheelZoom: false
                // hideOnStartup: true
            }).placeAt(rubric);
            aspect.after(this.nominatimSearch, "onData", function(meta, args) {
                var bbox = args[0];
                var title = args[1];
                console.log("Received bbox:", bbox);
                if (title) bbox = title + ": " + bbox;
                spatialInput.set("value", bbox);
                // domClass.add("uvpNominatimSearch", "hide");
            });
            /*aspect.after(this.nominatimSearch, "onClose", function() {
                domClass.add("uvpNominatimSearch", "hide");
            });*/

            // spatial reference
            creator.addToSection(rubric, creator.createDomTextbox({
                id: this.prefix + "spatialValue", 
                name: message.get("widget.spatial"), 
                help: message.get("widget.spatial.helpMessage"), 
                isMandatory: true, 
                visible: "optional", 
                style: "width:100%"
            }));
            var spatialInput = registry.byId(this.prefix + "spatialValue");
            spatialInput.set("disabled", true);
            spatialInput.set("placeHolder", message.get("widget.spatial.placeholder"));

            var igeActions = require("ingrid/IgeActions");
            igeActions.additionalFieldWidgets.push(spatialInput);

            // reposition map on object load
            aspect.after(igeActions, "onAfterLoad", function() {
                zoomToBoundingBox();
            });
        }
    })();
});