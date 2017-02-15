/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
    "dojo/dom-class",
    "ingrid/utils/General",
    "ingrid/utils/String",
    "ingrid/utils/Grid",
    "ingrid/message"
], function(declare, array, domClass, UtilGeneral, UtilString, UtilGrid, message) {

        return declare(null, {

            SyslistCellFormatter: function(syslist, row, cell, value, columnDef, dataContext) {
                var result = value == undefined ? "" : value + "";
                var list = sysLists[syslist];
                array.some(list, function(item) {
                    if (item[1] == result) {
                        result = item[0];
                        return true;
                    }
                });
                return result;
            },

            ListCellFormatter: function(row, cell, value, columnDef, dataContext) {
                if (value == undefined)
                    return "";
                var result = value + "";
                for (var i = 0; i < columnDef.values.length; i++) {
                    if (columnDef.values[i] == value) {
                        result = columnDef.options[i];
                        break;
                    }
                }
                return result;
            },

            DateCellFormatter: function(row, cell, value, columnDef, dataContext) {
                // only convert date objects and not string value already formatted by a grid
                if (typeof value == "string")
                    return value;
                var result = "";
                if (UtilGeneral.hasValue(value))
                    result = UtilString.getDateString(value, "dd.MM.yyyy");
                return result;
            },

            LocalizeString: function(row, cell, value, columnDef, dataContext) {
                return message.get(value);
            },

            EvalString: function(row, cell, value, columnDef, dataContext) {
                return eval(value);
            },

            emptyOrNullValidation: function(gridId, row, cell, value, columnDef, dataContext) {
                var grid = UtilGrid.getTable( gridId );
                if (!UtilGeneral.hasValue(value)) {
                    value = "";
                    grid.addInvalidCell( {row: row, column: cell} );
                    setTimeout(function() {
                        var cellDom = query("#" + gridId + " .slick-row[row$=" + row + "] .c" + cell)[0];
                        if (cellDom) domClass.add(cellDom, "importantBackground");
                    }, 500);
                } else {
                    grid.removeInvalidCell( {row: row, column: cell} );
                    setTimeout(function() {
                        var cellDom = query("#" + gridId + " .slick-row[row$=" + row + "] .c" + cell)[0];
                        if (cellDom) domClass.remove(cellDom, "importantBackground");
                    }, 500);
                }
                return value;
            },

            LocalizedNumberFormatter: function(row, cell, value, columnDef, dataContext) {
                var retValue = "";
                if (UtilGeneral.hasValue(value)) {
                    return dojo.number.format(value);
                }
                return retValue;
            },

            BoolCellFormatter: function(row, cell, value, columnDef, dataContext) {
                return value ? "<img src='img/tick.png'>" : "<img src='img/checkbox.png'>";
            },

            FirstEntryFormatter: function(identifier, row, cell, value, columnDef, dataContext) {
                var retValue = "";
                if (UtilGeneral.hasValue(value) && dojo.isArray(value) && value.length > 0) {
                    retValue = value[0];
                    if (dojo.isObject(retValue)) {
                        if (identifier) {
                            retValue = retValue[identifier];
                        } else {
                            retValue = retValue["title"];
                        }
                    }
                }
                return retValue;
            },

            renderIconClass: function(row, cell, value, columnDef, dataContext) {
                return "<div class=\"TreeIcon TreeIcon" + value + "\"></div>";
            },

            LinkCellFormatter: function(baseUrl, row, cell, value, columnDef, dataContext) {
                if (!value) {
                    return value;
                }
                if (baseUrl && baseUrl.length > 0 && baseUrl.charAt(baseUrl.length-1) !== "/") {
                    baseUrl += "/";
                }

                var link = null;
                if (value.indexOf("http") === 0) {
                    link = value;
                } else {
                    link = baseUrl + value;
                }
                return "<span class=\"text-truncate left\" style=\"width: 100%\">" + value +
                    " <a href=\"" + link + "\" title=\"" + link + "\" target=\"_blank\"><img src=\"img/ic_fl_popup.gif\" width=\"10\" height=\"9\" alt=\"Popup\">Link</a></span>";
            },

            BytesCellFormatter: function(row, cell, value, columnDef, dataContext) {
                if (parseInt(value) != value) {
                    return value;
                }
                if (value == 0) {
                    return '0B';
                }
                var k = 1000,
                    dm = 1,
                    sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
                    i = Math.floor(Math.log(value) / Math.log(k));
                return parseFloat((value / Math.pow(k, i)).toFixed(dm)) + sizes[i];
             }
        })();
    });
