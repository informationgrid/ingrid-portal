function SyslistCellFormatter(syslist, row, cell, value, columnDef, dataContext) {
    var result = value == undefined ? "" : value+"";
    var list = sysLists[syslist];
    dojo.some(list, function(item) {
        if (item[1] == result) {
            result = item[0];
            return true;
        }
    });
    return result;
}

function ListCellFormatter(row, cell, value, columnDef, dataContext) {
    if (value == undefined)
        return "";
    var result = value+"";
    for (var i=0; i<columnDef.values.length; i++) {
        if (columnDef.values[i] == value) {
            result = columnDef.options[i];
            break;
        }
    }
    return result;
}

function DateCellFormatter(row, cell, value, columnDef, dataContext){
    // only convert date objects and not string value already formatted by a grid
    if (typeof value == "string")
        return value;
    var result = "";
    if (UtilGeneral.hasValue(value))
        result = UtilString.getDateString(value, "dd.MM.yyyy");
    return result;
}

function LocalizeString(row, cell, value, columnDef, dataContext) {
    return message.get(value);
}

function emptyOrNullValidation(gridId, row, cell, value, columnDef, dataContext){
    if (!UtilGeneral.hasValue(value)) {
        value = "";
        setTimeout(function() {
            var cellDom = dojo.query("#"+gridId+" .slick-row[row$="+row+"] .c"+cell)[0];
            dojo.addClass(cellDom, "importantBackground");
        }, 500);
    } else {
        setTimeout(function() {
            var cellDom = dojo.query("#"+gridId+" .slick-row[row$="+row+"] .c"+cell)[0];
            dojo.removeClass(cellDom, "importantBackground");
        }, 500);
    }
    return value;
}

function LocalizedNumberFormatter(row, cell, value, columnDef, dataContext){
    var retValue = "";
    if (UtilGeneral.hasValue(value)) {
        return dojo.number.format(value);
    }
    return retValue;
}

function BoolCellFormatter(row, cell, value, columnDef, dataContext) {
    return value ? "<img src='img/tick.png'>" : "<img src='img/checkbox.png'>";
}

function FirstEntryFormatter(identifier, row, cell, value, columnDef, dataContext){
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
}
