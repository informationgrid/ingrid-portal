/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Special validation rules for form items
 */

/*
 * Rules that validate a field's value depending on another field's value
 */

var globalValidation = {};

define(["dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/topic",
    "dojo/on",
    "dojo/aspect",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/query",
    "dojo/string",
    "dijit/registry",
    "ingrid/utils/Grid",
    "ingrid/utils/UI",
    "ingrid/utils/Syslist",
    "ingrid/utils/General",
    "ingrid/message"
], function(declare, array, lang, topic, on, aspect, dom, domClass, query, string, registry, UtilGrid, UtilUI, UtilSyslist, UtilGeneral, message) {

    var Validators = declare(null, {

        constructor: function() {
            globalValidation.unmarkGridCells = UtilUI.unmarkGridCells;
        },

        addMinMaxValidation: function(minWidgetId, maxWidgetId, minCaption, maxCaption) {
            var minWidget = registry.byId(minWidgetId);
            var maxWidget = registry.byId(maxWidgetId);

            var minMaxError = string.substitute(message.get("validation.minmax"), [maxCaption, minCaption]);
            var defaultError = maxWidget.invalidMessage;

            var defValidator = minWidget.validator;
            minWidget.validator = function(value, constraints) {
                var ret = lang.hitch(this, defValidator)(value, constraints);
                if (ret) {
                    this.invalidMessage = minMaxError;
                    var minValue = (this.get("value") + "" == "NaN") ? "" : this.get("value");
                    var maxValue = (maxWidget.get("value") + "" == "NaN") ? "" : maxWidget.get("value");

                    if (minValue == "" || maxValue == "")
                        return true;
                    else
                        return (parseFloat(minValue) <= parseFloat(maxValue));
                } else {
                    this.invalidMessage = defaultError;
                }
            };
            maxWidget.validator = function(value, constraints) {
                var ret = lang.hitch(this, defValidator)(value, constraints);
                if (ret) {
                    this.invalidMessage = minMaxError;
                    var minValue = (minWidget.get("value") + "" == "NaN") ? "" : minWidget.get("value");
                    var maxValue = (this.get("value") + "" == "NaN") ? "" : this.get("value");

                    if (minValue == "" || maxValue == "")
                        return true;
                    else
                        return (parseFloat(minValue) <= parseFloat(maxValue));
                } else {
                    this.invalidMessage = defaultError;
                }
            };

            on(minWidget, "change", function() {
                maxWidget.validate();
            });
            on(maxWidget, "change", function() {
                minWidget.validate();
            });
        },

        spatialRefLocationValidation: function() {
            var isOk = true;
            var data = UtilGrid.getTableData("spatialRefLocation");

            array.forEach(data, function(item, row) {
                var error = false;
                // coordinates not mandatory, see INGRID-2089
                if (UtilGeneral.hasValue(item.name) && !UtilGeneral.hasValue(item.longitude1) && !UtilGeneral.hasValue(item.longitude2) && !UtilGeneral.hasValue(item.latitude1) && !UtilGeneral.hasValue(item.latitude2)) {
                    error = false;
                } else {
                    if (!UtilGeneral.hasValue(item.longitude1) || !UtilGeneral.hasValue(item.longitude2) || !UtilGeneral.hasValue(item.latitude1) || !UtilGeneral.hasValue(item.latitude2) || item.longitude1 > item.longitude2 || item.latitude1 > item.latitude2 || !UtilGeneral.hasValue(item.name)) {
                        error = true;
                    }
                }

                if (error) {
                    isOk = false;
                    if (!UtilGeneral.hasValue(item.name)) {
                        UtilUI.markCells("ERROR", "spatialRefLocation", row, [0]);
                        UtilGrid.getTable("spatialRefLocation").message = message.get("validation.error.missing.spatial.name");
                    } else {
                        if (item.longitude1 > item.longitude2) UtilUI.markCells("ERROR", "spatialRefLocation", row, [1, 3]);
                        if (item.latitude1 > item.latitude2) UtilUI.markCells("ERROR", "spatialRefLocation", row, [2, 4]);
                        if (!UtilGeneral.hasValue(item.longitude1)) UtilUI.markCells("ERROR", "spatialRefLocation", row, [1]);
                        if (!UtilGeneral.hasValue(item.longitude2)) UtilUI.markCells("ERROR", "spatialRefLocation", row, [3]);
                        if (!UtilGeneral.hasValue(item.latitude1)) UtilUI.markCells("ERROR", "spatialRefLocation", row, [2]);
                        if (!UtilGeneral.hasValue(item.latitude2)) UtilUI.markCells("ERROR", "spatialRefLocation", row, [4]);
                        UtilGrid.getTable("spatialRefLocation").message = message.get("validation.error.spatial.bounding.box");
                    }
                } else {
                    UtilUI.markCells("VALID", "spatialRefLocation", row, [0, 1, 2, 3, 4]);
                }
            });
            return isOk;
        },

        minMaxBoundingBoxValidation: function(res, args, args2) {
            if (!args) args = args2[0];
            var val = args instanceof Array ? args[0] : args;
            var error = false;
            var grid = UtilGrid.getTable("spatialRefLocation");
            var row = val.row;
            var rowAll = grid.getData()[row];
            var value = val.item;
            var column = val.cell;
            var gridId = "spatialRefLocation";
            var corrCell = -1;

            if (column === 0) return;

            if (!value)
                error = true;
            else {
                // we need to check if columns exist since it might be a new empty row
                // which does not have the properties
                if (column == 1) {
                    if (rowAll.longitude2 && value.longitude1 > rowAll.longitude2) {
                        error = true;
                    }
                    corrCell = 3;
                } else if (column == 2) {
                    if (rowAll.latitude2 && value.latitude1 > rowAll.latitude2) {
                        error = true;
                    }
                    corrCell = 4;
                } else if (column == 3) {
                    if (rowAll.longitude1 && value.longitude2 < rowAll.longitude1) {
                        error = true;
                    }
                    corrCell = 1;
                } else if (column == 4) {
                    if (rowAll.latitude1 && value.latitude2 < rowAll.latitude1) {
                        error = true;
                    }
                    corrCell = 2;
                }
            }
            var cellDom1 = query("#"+gridId+" .slick-row[row$="+row+"] .c"+column)[0];
            var cellDom2 = query("#"+gridId+" .slick-row[row$="+row+"] .c"+corrCell)[0];
            if (error) {
                domClass.add(cellDom1, "importantBackground");
                domClass.add(cellDom2, "importantBackground");
                grid.addInvalidCell({row: row, column: column});
                grid.addInvalidCell({row: row, column: corrCell});
                // show tooltip
                var toolTip = string.substitute(message.get("validation.minmax"), [message.get("validation.latLon2"), message.get("validation.latLon1")]);
                UtilUI.showToolTip("spatialRefLocation", toolTip);

            } else {
                domClass.remove(cellDom1, "importantBackground");
                domClass.remove(cellDom2, "importantBackground");
                grid.removeInvalidCell({row: row, column: column});
                grid.removeInvalidCell({row: row, column: corrCell});
            }

            return val;
        },

        emptyOrNullValidation: function(val, rowIdx, cell) {
            //var row = registry.byId(gridId).getItem(rowIdx);
            if (!val || val == "") {
                cell.customClasses.push("importantBackground");
            }
            return val;
        },

        emptyRowValidation: function(gridId) {
            var grid = UtilGrid.getTable( gridId );
            return grid.validateNonEmptyRows();
        },

        communicationValidation: function() {
            //on(registry.byId("addressCom").store, "onNew", function(item) {
            aspect.after(UtilGrid.getTable("addressCom"), "onDataChanged", function(msg) {
                // ignore address folders
                if (currentUdk.addressClass === 1000) return;

                var email = UtilSyslist.getSyslistEntryName(4430, 3);
                //if (msg.item.nameOfRelation == email) {
                var anyEmail = array.some(UtilGrid.getTableData("addressCom"), function(item) {
                    return (item.medium == email &&
                        typeof(item.value) != "undefined" &&
                        item.value !== null &&
                        lang.trim(item.value).length !== 0);
                });

                if (!anyEmail) {
                    // show tooltip
                    var toolTip = string.substitute(message.get("validation.communicationEmailRequired"), [email]);
                    UtilUI.showToolTip("addressCom", toolTip);
                }
                //}
            });
        },

        applyRef6UrlListValidation: function() {
            var error = false;
            var data = UtilGrid.getTableData("ref6UrlList");
            array.forEach(data, function(item, row) {
                var rowError = false;
                if (((item.url === undefined || item.url == "") && (item.name !== undefined || item.name != "")) ||
                    ((item.url !== undefined || item.url != "") && (item.name === undefined || item.name == "")))
                    rowError = true;

                if (((item.url === undefined || item.url == "") && (item.name === undefined || item.name == "")))
                    rowError = false;

                if (rowError) {
                    error = true;
                    if (item.url === undefined || item.url == "") {
                        UtilUI.markCells("ERROR", "ref6UrlList", row, [1]);
                        UtilUI.markCells("VALID", "ref6UrlList", row, [0]);
                    } else {
                        UtilUI.markCells("ERROR", "ref6UrlList", row, [0]);
                        UtilUI.markCells("VALID", "ref6UrlList", row, [1]);
                    }
                } else {
                    UtilUI.markCells("VALID", "ref6UrlList", row, [0, 1]);
                }
            });
            return !error;
        },

        addServiceUrlValidation: function(other, data) {
            var val = data[0];
            console.debug("validate addServiceUrlValidation");
            var error = false;
            var gridId = "ref6UrlList";
            var value = val.item;
            var row = val.row !== undefined ? val.row : UtilGrid.getTableData("ref6UrlList").length - 1;
            var corrCell = -1;

            if (!val || val === NaN)
                error = true;
            else if (((value.url === undefined || value.url == "") && (value.name !== undefined || value.name != "")) ||
                ((value.url !== undefined || value.url != "") && (value.name === undefined || value.name == "")))
                error = true;

            if (((value.url === undefined || value.url == "") && (value.name === undefined || value.name == "")))
                error = false;

            UtilGrid.getTable(gridId).scrollRowIntoView(row);
            var cellDom1 = query("#" + gridId + " .slick-row[row$=" + row + "] .c0")[0];
            var cellDom2 = query("#" + gridId + " .slick-row[row$=" + row + "] .c1")[0];
            if (error) {
                if (value.url === undefined || value.url == "") {
                    domClass.add(cellDom2, "importantBackground");
                    domClass.remove(cellDom1, "importantBackground");
                } else {
                    domClass.add(cellDom1, "importantBackground");
                    domClass.remove(cellDom2, "importantBackground");
                }

                // show tooltip
                var toolTip = string.substitute(message.get("validation.serviceUrl"), [message.get("ui.obj.type6.urlList.header.name"), message.get("ui.obj.type6.urlList.header.url")]);
                UtilUI.showToolTip(gridId, toolTip);

            } else {
                domClass.remove(cellDom1, "importantBackground");
                domClass.remove(cellDom2, "importantBackground");
            }
            return val;
        },

        applyTimeRefIntervalValidation: function() {
            // If timeRefIntervalNum is not empty, timeRefIntervalUnit must not be empty and vice versa
            var intervalNumWidget = registry.byId("timeRefIntervalNum");
            var intervalUnitWidget = registry.byId("timeRefIntervalUnit");

            on(intervalNumWidget, "KeyUp", function() {
                var unitVal = registry.byId("timeRefIntervalUnit").get("value");
                var unitEmpty = (unitVal === null || unitVal.length === 0);

                if (unitEmpty) {
                    if (this.get("displayedValue") == "") {
                        this.required = false;
                        UtilUI.setWidgetStateError(registry.byId("timeRefIntervalUnit"), false);
                    } else {
                        this.required = true;
                        UtilUI.setWidgetStateError(registry.byId("timeRefIntervalUnit"), true);
                    }
                } else {
                    this.required = true;
                    registry.byId("timeRefIntervalUnit").required = true;
                }
                this.validate();
            });

            on(intervalUnitWidget, "Change", function() {
                var numWidget = registry.byId("timeRefIntervalNum");
                if (this.get("value") != "")
                    numWidget.required = true;
                else {
                    if (numWidget.get("displayedValue") == "") {
                        numWidget.required = false;
                        this.required = false;
                        this.validate();
                    } else {
                        numWidget.required = true;
                        this.required = true;
                    }
                }
                intervalNumWidget.validate();
            });
        },

        thesaurusValidation: function() {
            var result = true;
            var data = UtilGrid.getTableData("thesaurusTerms");
            array.forEach(data, function(row) {
                if (!row.title || !row.sourceString) {
                    console.debug("Thesaurus term incomplete!");
                    result = false;
                }
            });
            return result;
        },

        timeRefTablePublishable: function(notPublishableIDs) {
            if (array.some(UtilGrid.getTableData("timeRefTable"), function(timeRef) {
                return (typeof(timeRef.type) == "undefined" ||
                    timeRef.type === null ||
                    lang.trim(timeRef.type + "").length === 0 ||
                    typeof(timeRef.date) == "undefined" ||
                    timeRef.date === null ||
                    lang.trim(timeRef.date + "").length === 0);
            })) {
                notPublishableIDs.push( ["timeRefTable", message.get("validation.error.time.reference")] );
            }
        },


        timeRefDatePublishable: function (notPublishableIDs) {
            var validationFailed = false;
            var date1 = registry.byId("timeRefDate1").get("value");
            var date2 = registry.byId("timeRefDate2").get("value");
            var type = registry.byId("timeRefType").get("value");
            var subType = registry.byId("timeRefSubType").get("value");

            if (type != null && type !== "") {
                // if type is not null or empty, date1 must be set
                if (date1 == null) validationFailed = true;
                if (type === "fromType") {
                    // if type is fromType a subType must be set
                    if (subType == null && subType !== "") validationFailed = true;
                    // if subType is 'von', date2 must be set
                    if (subType === "von" && date2 == null) validationFailed = true;
                }
            }
            if (validationFailed)
                notPublishableIDs.push(["timeRefRef", message.get("validation.error.time.reference")]);
        },

        extraInfoConformityPublishable: function(notPublishableIDs) {
            var objClass = registry.byId("objectClass").get("value").substr(5, 1);
            if ((objClass == "1") || (objClass == "3")) {
                // Check if the conformity table contains valid input (both level and specification must contain data)
                if (array.some(UtilGrid.getTableData("extraInfoConformityTable"), function(conf) {
                    return (typeof(conf.level) == "undefined" || conf.level === null || lang.trim(conf.level + "").length === 0 || typeof(conf.specification) == "undefined" || conf.specification === null || lang.trim(conf.specification + "").length === 0);
                })) {
                    console.debug("All entries in the conformity table must have a valid level and specification.");
                    notPublishableIDs.push( ["extraInfoConformityTable", message.get("validation.error.conformity.table")] );
                }
            }
        },

        spatialRefAdminUnitPublishable: function(notPublishableIDs) {
            // Check if one of the 'Raumbezug' tables has an entry with a bounding box
            var hasBB = function(item) {
                return (item.longitude1 && item.longitude2 && item.latitude1 && item.latitude2);
            };
            if (!(array.some(UtilGrid.getTableData("spatialRefAdminUnit"), hasBB) || array.some(UtilGrid.getTableData("spatialRefLocation"), hasBB))) {
                console.debug("At least one 'spatial' table has to contain an entry with a BB.");
                notPublishableIDs.push( ["spatialRefAdminUnit", message.get("validation.error.spatial.no.entry")], "spatialRefLocation");
            }

            // Check if one of the spatial references is expired
            if (array.some(UtilGrid.getTableData("spatialRefAdminUnit"), function(item) {
                return item.locationExpiredAt !== null && item.locationExpiredAt !== undefined;
            })) {
                console.debug("The spatial reference table must not contain expired entries.");
                notPublishableIDs.push( ["spatialRefAdminUnit", message.get("validation.error.spatial.no.expired")] );
            }
        },

        applyBeforeAddressPublishValidation: function() {
            topic.subscribe("/onBeforeAddressPublish", function( /*Array*/ notPublishableIDs) {
                // Check if all entries in the address table are valid
                var addressData = UtilGrid.getTableData("addressCom");
                if (array.some(addressData, function(adr) {
                    return (typeof(adr.medium) == "undefined" || adr.medium === null || lang.trim(adr.medium + "").length === 0 || typeof(adr.value) == "undefined" || adr.value === null || lang.trim(adr.value + "").length === 0);
                })) {
                    console.debug("All entries in the addressCom table must contain values.");
                    notPublishableIDs.push( ["addressCom", message.get("validation.error.incomplete.tablerow")] );
                }

                // Get the string (from the syslist) that is used to identify email entries
                var emailString = UtilSyslist.getSyslistEntryName(4430, 3);
                // Check if at least one entry exists with type email
                if (dojo.every(addressData, function(adr) {
                    return (lang.trim(adr.medium + "") != emailString);
                })) {
                    console.debug("At least one entry has to be of type '" + emailString + "'.");
                    var errorTxt = string.substitute( message.get("validation.error.email.table"), [emailString] );
                    registry.byId("addressCom").invalidMessage = errorTxt;
                    notPublishableIDs.push( ["addressCom", errorTxt] );
                }
            });
        },


        addEmailCheck: function(id, /*boolean*/ onlyBeforePublish) {
            registry.byId(id).regExpGen = dojox.validate.regexp.emailAddress;
            // addRegExCheck DOES NOT WORK !!!
            //Validation.addRegExCheck(id, dojox.validate.regexp.emailAddress)
        },

        addUrlCheck: function(id, /*boolean*/ onlyBeforePublish) {
            registry.byId(id).regExpGen = dojox.validate.regexp.url;
        },

        addTableCellCheck: function(id, /*Array*/ colIds, /*function*/ caller, /*boolean*/ onlyBeforePublish) {
            var funcValidate = function() {
                var isValid = true;
                // make an array if just one value was given!
                if (!(colIds instanceof Array)) colIds = [colIds];
                array.forEach(this.getData(), function(data, row) {
                    array.forEach(colIds, function(colId) {
                        var value = data[colId];
                        var result = caller(value, colId, data);
                        if (result === false) {
                            UtilUI.markCells("ERROR", id, row, [this.columnsById[colId]]);
                            isValid = false;
                        } else {
                            UtilUI.markCells("", id, row, [this.columnsById[colId]]);
                        }
                    }, this);
                }, this);
                return isValid;
            };

            if (onlyBeforePublish) {
                topic.subscribe("/onBeforeObjectPublish", function( /*Array*/ notPublishableIDs) {
                    if (!lang.hitch(registry.byId(id), funcValidate)()) {
                        notPublishableIDs.push(id);
                    }
                });
            } else {
                var widget = registry.byId(id);
                aspect.after(widget, "onDataChanged", funcValidate);
                widget.validate = funcValidate;
            }
        },

        addValueCheck: function(id, value, /*boolean*/ invert, message, /*boolean*/ onlyBeforePublish) {
            var func = function() {
                if (invert)
                    return this.get("value") != value;
                else
                    return this.get("value") == value;
            };
            this.addCheck(id, func, message, onlyBeforePublish);
        },

        addNumberCheck: function(id, min, max, /*invalidMessage*/ message, /*boolean*/ onlyBeforePublish) {
            // TODO: check if widget is a numberbox and use special range function

            var func = function() {
                return (min ? this.get("value") >= min : true) && (max ? this.get("value") <= max : true);
            };

            this.addCheck(id, func, message, onlyBeforePublish);
        },

        addCheck: function(id, /*function*/ caller, message, /*boolean*/ onlyBeforePublish) {
            if (onlyBeforePublish) {
                topic.subscribe("/onBeforeObjectPublish", function( /*Array*/ notPublishableIDs) {
                    if (!lang.hitch(registry.byId(id), caller)()) {
                        notPublishableIDs.push(id);
                    }
                });
            } else {
                var widget = registry.byId(id);
                widget.validator = caller;
                widget.invalidMessage = message;
            }
        },

        addRegExCheck: function(id, /*function*/ caller, message, /*boolean*/ onlyBeforePublish) {
            if (onlyBeforePublish) {
                topic.subscribe("/onBeforeObjectPublish", function( /*Array*/ notPublishableIDs) {
                    if (!lang.hitch(registry.byId(id), caller)()) {
                        notPublishableIDs.push(id);
                    }
                });
            } else {
                var widget = registry.byId(id);
                widget.validator = caller;
                widget.invalidMessage = message;
            }
        }

    })();

    // add global function objects for backward compatibility
    emptyRowValidation = Validators.emptyRowValidation;
    applyRef6UrlListValidation = Validators.applyRef6UrlListValidation;
    addServiceUrlValidation = Validators.addServiceUrlValidation;
    minMaxBoundingBoxValidation = lang.partial(Validators.minMaxBoundingBoxValidation, null);
    addMinMaxValidation = Validators.addMinMaxValidation;
    spatialRefLocationValidation = Validators.spatialRefLocationValidation;
    applyTimeRefIntervalValidation = Validators.applyTimeRefIntervalValidation;

    timeRefTablePublishable = Validators.timeRefTablePublishable;
    timeRefDatePublishable = Validators.timeRefDatePublishable;
    dqTablesPublishable = Validators.dqTablesPublishable;
    spatialRefAdminUnitPublishable = Validators.spatialRefAdminUnitPublishable;
    extraInfoConformityPublishable = Validators.extraInfoConformityPublishable;

    return Validators;
});
