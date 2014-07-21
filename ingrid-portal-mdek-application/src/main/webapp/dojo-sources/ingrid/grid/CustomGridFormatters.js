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
            }

        })();
    });