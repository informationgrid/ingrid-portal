/*
 * Special validation rules for form items
 */

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// removed stuff not needed anymore
// see svn log, "CLEAN UP: REMOVED NOT NEEDED JAVASCRIPT FROM rules_*.js"
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


/*
 * Rules that validate a field's value depending on another field's value
 */

function addMinMaxValidation(minWidgetId, maxWidgetId, minCaption, maxCaption) {
	var minWidget = dijit.byId(minWidgetId);
	var maxWidget = dijit.byId(maxWidgetId);

	var minMaxError = dojo.string.substitute(message.get("validation.minmax"), [maxCaption, minCaption]);
	var defaultError = maxWidget.invalidMessage;

	var defValidator = minWidget.validator;
    minWidget.validator = function(value, constraints) {
        var ret = dojo.hitch(this, defValidator)(value, constraints);
        if (ret) {
            this.invalidMessage = minMaxError;
            var minValue = (this.get('value')+"" == "NaN") ? "" : this.get('value');
            var maxValue = (maxWidget.get('value')+"" == "NaN") ? "" : maxWidget.get('value');
            
            if (minValue == "" || maxValue == "") 
                return true;
            else 
                return (parseFloat(minValue) <= parseFloat(maxValue));
        } else {
            this.invalidMessage = defaultError;
        }
	}
	maxWidget.validator = function(value, constraints) {
        var ret = dojo.hitch(this, defValidator)(value, constraints);
        if (ret) {
            this.invalidMessage = minMaxError;
            var minValue = (minWidget.get('value')+"" == "NaN") ? "" : minWidget.get('value');
            var maxValue = (this.get('value')+"" == "NaN") ? "" : this.get('value');
            
            if (minValue == "" || maxValue == "") 
                return true;
            else 
                return (parseFloat(minValue) <= parseFloat(maxValue));
        } else {
            this.invalidMessage = defaultError;
        }
	}

	dojo.connect(minWidget, "onChange", maxWidget, function(){this.validate();});
	dojo.connect(maxWidget, "onChange", minWidget, function(){this.validate();});
}

function spatialRefLocationValidation(){
    var isOk = true;
    var data = UtilGrid.getTableData("spatialRefLocation");

    dojo.forEach(data, function(item, row){
    	var error = false;
        // coordinates not mandatory, see INGRID-2089 
        if (UtilGeneral.hasValue(item.name)
            && !UtilGeneral.hasValue(item.longitude1)
            && !UtilGeneral.hasValue(item.longitude2)
            && !UtilGeneral.hasValue(item.latitude1)
            && !UtilGeneral.hasValue(item.latitude2)) {
            error = false;
        } else {
            if (!UtilGeneral.hasValue(item.longitude1)
                || !UtilGeneral.hasValue(item.longitude2)
                || !UtilGeneral.hasValue(item.latitude1)
                || !UtilGeneral.hasValue(item.latitude2) 
                || item.longitude1 > item.longitude2
                || item.latitude1 > item.latitude2
                || !UtilGeneral.hasValue(item.name)) {
                error = true;
            }        	
        }
        
        if (error) {
        	isOk = false;
            if (!UtilGeneral.hasValue(item.name)) {
                markCells("ERROR", "spatialRefLocation", row, [0]);
            } else {
                if (item.longitude1 > item.longitude2) markCells("ERROR", "spatialRefLocation", row, [1, 3]);
                if (item.latitude1 > item.latitude2) markCells("ERROR", "spatialRefLocation", row, [2, 4]);
                if (!UtilGeneral.hasValue(item.longitude1)) markCells("ERROR", "spatialRefLocation", row, [1]);
                if (!UtilGeneral.hasValue(item.longitude2)) markCells("ERROR", "spatialRefLocation", row, [3]);
                if (!UtilGeneral.hasValue(item.latitude1)) markCells("ERROR", "spatialRefLocation", row, [2]);
                if (!UtilGeneral.hasValue(item.latitude2)) markCells("ERROR", "spatialRefLocation", row, [4]);            	
            }
        } else {
            markCells("VALID", "spatialRefLocation", row, [0, 1, 2, 3, 4]);
        }
    });
    return isOk;
}

function minMaxBoundingBoxValidation(val) {
	var error = false;
	var row = UtilGrid.getTableData("spatialRefLocation")[val.row];
    var value = val.item;
    var gridId = "spatialRefLocation";
    var corrCell = -1;
	
    if (val.cell == 0) return;
    
	if (!val || val == NaN) 
		error = true;
	else {
		// we need to check if columns exist since it might be a new empty row
		// which does not have the properties
		if (val.cell == 1) {
			if (row.longitude2 && value.longitude1 > row.longitude2) {
				error = true;
			}
            corrCell = 3;
		} else if (val.cell == 2) {
    		if (row.latitude2 && value.latitude1 > row.latitude2) {
    			error = true;
    		}
            corrCell = 4;
    	} else if (val.cell == 3) {
			if (row.longitude1 && value.longitude2 < row.longitude1) {
				error = true;
			}
            corrCell = 1;
		} else if (val.cell == 4) {
			if (row.latitude1 && value.latitude2 < row.latitude1) {
				error = true;
			}
            corrCell = 2;
		}
	}
    var cellDom1 = dojo.query("#"+gridId+" .slick-row[row$="+val.row+"] .c"+val.cell)[0];
    var cellDom2 = dojo.query("#"+gridId+" .slick-row[row$="+val.row+"] .c"+corrCell)[0];
	if (error) {
        dojo.addClass(cellDom1, "importantBackground");
        dojo.addClass(cellDom2, "importantBackground");
		// show tooltip
		var toolTip = dojo.string.substitute(message.get("validation.minmax"), [message.get("validation.latLon2"), message.get("validation.latLon1")]);
		showToolTip("spatialRefLocation", toolTip);
            
	} else {
        dojo.removeClass(cellDom1, "importantBackground");
        dojo.removeClass(cellDom2, "importantBackground");
    }
	return val;
}

function emptyOrNullValidation(val, rowIdx, cell) {
	//var row = dijit.byId(gridId).getItem(rowIdx);
	if (!val || val == "") {
		cell.customClasses.push("importantBackground");
	}
	return val;
}

function titleDateValidation(gridId){
    var error = false;
	var data = this.getData();
    
    dojo.forEach(data, function(item, row){
        var rowError = false;
        if (((item.title == undefined || item.title == "") && (item.date != undefined || item.date != "")) ||
             ((item.title != undefined || item.title != "") && (item.date == undefined || item.date == "")))
        rowError = true;
    
        if (((item.title == undefined || item.title == "") && (item.date == undefined || item.date == "")))
            rowError = false;
            
        if (rowError) {
            error = true;
            if (item.date == undefined || item.date == "") {
                markCells("ERROR", gridId, row, [1]);
                markCells("VALID", gridId, row, [0]);
            } else {
                markCells("ERROR", gridId, row, [0]);
                markCells("VALID", gridId, row, [1]);
            }
            var msg = dojo.string.substitute(message.get("validation.titleDate"), [message.get("ui.obj.type1.symbolCatTable.header.title"), message.get("ui.obj.type1.symbolCatTable.header.date")]);
            showToolTip(gridId, msg);
        } else {
            markCells("VALID", gridId, row, [0, 1]);
        }
        
    });
    
    return !error;
    
	//var popup = dijit.createWidget("PopupContainer");
  	//popup.domNode.innerHTML = dojo.string.substituteParams(message.get("validation.titleDate"), message.get("ui.obj.type1.symbolCatTable.header.title"), message.get("ui.obj.type1.symbolCatTable.header.date"));

	/*
	table.isValid = function() { return this.store.getData().length == 0 || this._valid;}
	*/
}

function addressValidation() {
    dojo.connect(UtilGrid.getTable("generalAddress"), "onDataChanged", function(msg) {
        if (msg.type != "deleted")
            return;
            
        // special behaviour only if "Verwalter" is going to be deleted
        var verwalterString = UtilSyslist.getSyslistEntryName(505, 2);
        // if it's the last Verwalter then revert store state since
        // at least one address of this kind must exist
        var anyVerwalter = dojo.some(UtilGrid.getTableData("generalAddress"), function(item){
            return item.nameOfRelation == verwalterString;
        });
        if (!anyVerwalter) {
            dojo.forEach(msg.items, function(itemRow){
                if (itemRow.nameOfRelation == verwalterString) {
                    UtilGrid.addTableDataRow("generalAddress", itemRow);
                    
                    // show tooltip
                    var toolTip = dojo.string.substitute(message.get("validation.addressInfoRequired"), [verwalterString]);
                    showToolTip("generalAddress", toolTip);
                }
            });
        }
	});
	
	// only complete entries are allowed
	UtilGrid.getTable("generalAddress").validate = function(forPublish){
        var result = true;
        var data = this.getData();
		dojo.forEach(data, function(item, i) {
			if (typeof(item.uuid) == "undefined") {
                UtilGrid.getTable(gridId).scrollRowIntoView(i);
                var cell = dojo.query(".slick-row[row$="+i+"] .c2", "generalAddress")[0];
				dojo.addClass(cell, "importantBackground");
				result = false;
			}
			if (forPublish) {
                if (typeof(item.nameOfRelation) == "undefined" || item.nameOfRelation == "") {
                    UtilGrid.getTable(gridId).scrollRowIntoView(i);
                    var cell = dojo.query(".slick-row[row$=" + i + "] .c0", "generalAddress")[0];
                    dojo.addClass(cell, "importantBackground");
                    result = false;
                }
            }
		});
		return result;
	};
	
}

function communicationValidation() {
	//dojo.connect(dijit.byId("addressCom").store, "onNew", function(item) {
    dojo.connect(UtilGrid.getTable("addressCom"), "onDataChanged", function(msg) {
        var email = UtilSyslist.getSyslistEntryName(4430, 3);
        //if (msg.item.nameOfRelation == email) {
            var anyEmail = dojo.some(UtilGrid.getTableData("addressCom"), function(item){
                return (item.medium == email &&
                typeof(item.value) != "undefined" &&
                item.value != null &&
                dojo.trim(item.value).length != 0);
            });
            
            if (!anyEmail) {
                // show tooltip
                var toolTip = dojo.string.substitute(message.get("validation.communicationEmailRequired"), [email]);
                showToolTip("addressCom", toolTip);
            }
        //}
	});
}

function applyRef6UrlListValidation() {
    var error = false;
    var data = UtilGrid.getTableData("ref6UrlList");
    dojo.forEach(data, function(item, row) {
        var rowError = false;
        if (((item.url == undefined || item.url == "") && (item.name != undefined || item.name != "")) ||
             ((item.url != undefined || item.url != "") && (item.name == undefined || item.name == "")))
        rowError = true;
    
        if (((item.url == undefined || item.url == "") && (item.name == undefined || item.name == "")))
            rowError = false;
            
        if (rowError) {
            error = true;
            if (item.url == undefined || item.url == "") {
                markCells("ERROR", "ref6UrlList", row, [1]);
                markCells("VALID", "ref6UrlList", row, [0]);
            } else {
                markCells("ERROR", "ref6UrlList", row, [0]);
                markCells("VALID", "ref6UrlList", row, [1]);
            }
        } else {
            markCells("VALID", "ref6UrlList", row, [0, 1]);
        }
    });
    return !error;
}

function markCells(type, gridId, row, cells) {
    UtilGrid.getTable(gridId).scrollRowIntoView(row);
    dojo.forEach(cells, function(cell) {
        var cellDom = dojo.query("#"+gridId+" .slick-row[row$="+row+"] .c"+cell)[0];
        if (type == "ERROR") 
            dojo.addClass(cellDom, "importantBackground");
        else
            dojo.removeClass(cellDom, "importantBackground");
    });
}   

function showToolTip(gridId, msg) {
    dijit.showTooltip(msg, dojo.byId(gridId), ["below"], false);
    setTimeout(function(){
        var eventWndScroll;
        var eventWndClick = dojo.connect(dojo.byId("contentContainer"), "onclick", function(){
            dijit.hideTooltip(dojo.byId(gridId));
            dojo.disconnect(eventWndClick);
            dojo.disconnect(eventWndScroll);
        });
        eventWndScroll = dojo.connect(dojo.byId("contentContainer"), (!dojo.isMozilla ? "onmousewheel" : "DOMMouseScroll"), function(){
            dijit.hideTooltip(dojo.byId(gridId));
            dojo.disconnect(eventWndScroll);
            dojo.disconnect(eventWndClick);
        });
        setTimeout(function(){
            dijit.hideTooltip(dojo.byId(gridId));
            dojo.disconnect(eventWndScroll);
            dojo.disconnect(eventWndClick);
        }, 5000);
    }, 500);
}

function addServiceUrlValidation(val){
    console.debug("validate addServiceUrlValidation");
    var error = false;
    var gridId = "ref6UrlList";
    var value = val.item;
    var row = val.row != undefined ? val.row : UtilGrid.getTableData("ref6UrlList").length-1;
    var corrCell = -1;
    
    if (!val || val == NaN) 
        error = true;
    else if (((value.url == undefined || value.url == "") && (value.name != undefined || value.name != "")) ||
             ((value.url != undefined || value.url != "") && (value.name == undefined || value.name == "")))
        error = true;
    
    if (((value.url == undefined || value.url == "") && (value.name == undefined || value.name == "")))
        error = false;
        
    UtilGrid.getTable(gridId).scrollRowIntoView(row);
    var cellDom1 = dojo.query("#"+gridId+" .slick-row[row$="+row+"] .c0")[0];
    var cellDom2 = dojo.query("#"+gridId+" .slick-row[row$="+row+"] .c1")[0];
    if (error) {
        if (value.url == undefined || value.url == "") {
            dojo.addClass(cellDom2, "importantBackground");
            dojo.removeClass(cellDom1, "importantBackground");
        } else { 
            dojo.addClass(cellDom1, "importantBackground");
            dojo.removeClass(cellDom2, "importantBackground");
        }
        
        // show tooltip
        var toolTip = dojo.string.substitute(message.get("validation.serviceUrl"), [message.get("ui.obj.type6.urlList.header.name"), message.get("ui.obj.type6.urlList.header.url")]);
        showToolTip(gridId, toolTip);
            
    } else {
        dojo.removeClass(cellDom1, "importantBackground");
        dojo.removeClass(cellDom2, "importantBackground");
    }
    return val;
}

function applyTimeRefIntervalValidation() {
    // If timeRefIntervalNum is not empty, timeRefIntervalUnit must not be empty and vice versa
    var intervalNumWidget = dijit.byId("timeRefIntervalNum");
    var intervalUnitWidget = dijit.byId("timeRefIntervalUnit");
    
    dojo.connect(intervalNumWidget, "onKeyUp", function() {
        var unitVal = dijit.byId("timeRefIntervalUnit").getValue();
        var unitEmpty = (unitVal == null || unitVal.length == 0);

        if (unitEmpty) {
            if (this.get('displayedValue') == "") {
                this.required = false;
                UtilUI.setWidgetStateError(dijit.byId("timeRefIntervalUnit"), false);
            } else {
                this.required = true;
                UtilUI.setWidgetStateError(dijit.byId("timeRefIntervalUnit"), true);
            }
        } else {
            this.required = true;
            dijit.byId("timeRefIntervalUnit").required = true;
        }
        this.validate();
    });
    
    dojo.connect(intervalUnitWidget, "onChange", function() {
        var numWidget = dijit.byId("timeRefIntervalNum");
        if (this.getValue() != "") 
            numWidget.required = true;
        else {
            if (numWidget.get('displayedValue') == "") {
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
}

function thesaurusValidation() {
    var result = true;
    var data = UtilGrid.getTableData("thesaurusTerms");
    dojo.forEach(data, function(row) {
        if (!row.title || !row.sourceString) {
            console.debug("Thesaurus term incomplete!");
            result = false;
        }
    });
    return result;
}

function timeRefTablePublishable(notPublishableIDs) {
    if (dojo.some(UtilGrid.getTableData("timeRefTable"), function(timeRef) {
            return (typeof(timeRef.type) == "undefined" || timeRef.type == null || dojo.trim(timeRef.type+"").length == 0
                || typeof(timeRef.date) == "undefined" || timeRef.date == null || dojo.trim(timeRef.date+"").length == 0); })) {
        notPublishableIDs.push("timeRefTable");
    }
}

function availabilityAccessPublishable(notPublishableIDs) {
    var objClass = dijit.byId("objectClass").getValue().substr(5, 1);
    if (objClass != '0') {
        if (dojo.some(UtilGrid.getTableData("availabilityAccessConstraints"), function(ad){
            return (typeof(ad.title) == "undefined" || ad.title == null || dojo.trim(ad.title + "").length == 0);
        })) {
            notPublishableIDs.push("availabilityAccessConstraints");
            console.debug("All entries in the availabilityAccessConstraints table must contain data.");
        }
    }
}

function generalAddressPublishable(notPublishableIDs) {
    var objClass = dijit.byId("objectClass").getValue().substr(5, 1);
    var addressData = UtilGrid.getTableData("generalAddress");
    // Check if all entries in the address table have valid reference types
    if (dojo.some(addressData, function(addressRef) { return (typeof(addressRef.uuid) == "undefined" || addressRef.nameOfRelation == null || dojo.trim(addressRef.nameOfRelation+"").length == 0); })) {
        console.debug("All entries in the address table must have valid references.");
        dijit.byId("generalAddress").invalidMessage = "All entries in the address table must have valid references.";
        notPublishableIDs.push("generalAddress");
    }

    // Get the string (from the syslist) that is used to identify verwalter entries
    var verwalterString = UtilSyslist.getSyslistEntryName(505, 2);

    // Check if at least one entry exists with the correct relation type
    if (dojo.every(addressData, function(addressRef) { return ( dojo.trim(addressRef.nameOfRelation+"") != verwalterString); })) {
        console.debug("At least one entry has to be of type '"+verwalterString+"'.");
        dijit.byId("generalAddress").invalidMessage = dojo.string.substitute(message.get("validation.error.addressType"), [verwalterString]);
        notPublishableIDs.push("generalAddress");
    }
}

function extraInfoConformityPublishable(notPublishableIDs) {
    var objClass = dijit.byId("objectClass").getValue().substr(5, 1);
    if ((objClass == '1') || (objClass == '3')) {
        // Check if the conformity table contains valid input (both level and specification must contain data)
        if (dojo.some(UtilGrid.getTableData("extraInfoConformityTable"), function(conf) {
                return (typeof(conf.level) == "undefined" || conf.level == null || dojo.trim(conf.level+"").length == 0
                     || typeof(conf.specification) == "undefined" || conf.specification == null || dojo.trim(conf.specification+"").length == 0); })) {
            console.debug("All entries in the conformity table must have a valid level and specification.");
            notPublishableIDs.push("extraInfoConformityTable");
        }
    }
}

function dqTablesPublishable(notPublishableIDs) {
    var objClass = dijit.byId("objectClass").getValue().substr(5, 1);
    if (objClass == '1') {
        var dqUiTableElements = dojo.query("#ref1ContentDQTables span:not(.hide) .ui-widget", "contentFrameBodyObject").map(function(item) {return item.id;});
        // Check dq rows whether complete !
        dojo.forEach(dqUiTableElements, function(dqTableId){
            var dqRows = UtilGrid.getTableData(dqTableId);
            dojo.forEach(dqRows, function(dqRow) {
                if (!UtilGeneral.hasValue(dqRow.nameOfMeasure) || !UtilGeneral.hasValue(dqRow.resultValue)) {
                    console.debug("NameOfMeasure + ResultValue needs to be filled.");
                    notPublishableIDs.push(dqTableId);
                }
            });
        });
    }
}

function spatialRefAdminUnitPublishable(notPublishableIDs) {
    // Check if one of the 'Raumbezug' tables has an entry with a bounding box
    var hasBB = function(item) {return (item.longitude1 && item.longitude2 && item.latitude1 && item.latitude2);};
    if ( !(dojo.some(UtilGrid.getTableData("spatialRefAdminUnit"), hasBB) || dojo.some(UtilGrid.getTableData("spatialRefLocation"), hasBB)) ) {
        console.debug("At least one 'spatial' table has to contain an entry with a BB.");
        notPublishableIDs.push("spatialRefAdminUnit", "spatialRefLocation");
    }

    // Check if one of the spatial references is expired
    if (dojo.some(UtilGrid.getTableData("spatialRefAdminUnit"), function(item) { return item.locationExpiredAt != null } )) {
        console.debug("The spatial reference table must not contain expired entries.");
        notPublishableIDs.push("spatialRefAdminUnit");
    }
}

function ref3OperationPublishable(notPublishableIDs) {
    var objClass = dijit.byId("objectClass").getValue().substr(5, 1);
    if (objClass == '3') {
        // Check if the operation table contains valid input (name has to be set)
    	// NOTICE: Name may be reset to "" if serviceType is changed !!!
        if (dojo.some(UtilGrid.getTableData("ref3Operation"), function(op) {
                return (!UtilGeneral.hasValue(op.name) || !UtilGeneral.hasValue(op.addressList) || !UtilGeneral.hasValue(op.platform)); })) {
            console.debug("All entries in the operation table must have a valid name.");
            notPublishableIDs.push("ref3Operation");
        }
    }
}

function applyBeforeObjectPublishValidation() {
    dojo.subscribe("/onBeforeObjectPublish", function(/*Array*/notPublishableIDs) {
        var objClass = dijit.byId("objectClass").getValue().substr(5, 1);
        var addressData = UtilGrid.getTableData("generalAddress");
        
        // Check if the timeRef table contains valid input (both date and type must contain data)
        timeRefTablePublishable(notPublishableIDs);
        
        // Check if the availability access and useConstraints contains valid input (both fields contain data)
        availabilityAccessPublishable(notPublishableIDs);
        
        availabilityUsePublishable(notPublishableIDs);
        
        // Check if at least one entry exists with the correct relation type
        generalAddressPublishable(notPublishableIDs);
        
        spatialRefAdminUnitPublishable(notPublishableIDs);
        
        extraInfoConformityPublishable(notPublishableIDs);
            
        dqTablesPublishable(notPublishableIDs);
    });
}

function applyBeforeAddressPublishValidation() {
    dojo.subscribe("/onBeforeAddressPublish", function(/*Array*/notPublishableIDs) {
        // Check if all entries in the address table are valid
        var addressData = UtilGrid.getTableData("addressCom");
        if (dojo.some(addressData, function(adr) { return (typeof(adr.medium) == "undefined" || adr.medium == null || dojo.trim(adr.medium+"").length == 0
                                                             || typeof(adr.value) == "undefined" || adr.value == null || dojo.trim(adr.value+"").length == 0); })) {
            console.debug("All entries in the addressCom table must contain values.");
            notPublishableIDs.push("addressCom");
        }
    
        // Get the string (from the syslist) that is used to identify email entries
        var emailString = UtilSyslist.getSyslistEntryName(4430, 3);
        // Check if at least one entry exists with type email
        if (dojo.every(addressData, function(adr) { return ( dojo.trim(adr.medium+"") != emailString); })) {
            console.debug("At least one entry has to be of type '"+emailString+"'.");
            notPublishableIDs.push("addressCom");
        }
    });
}

var Validation = {};

Validation.addEmailCheck = function(id, /*boolean*/onlyBeforePublish) {
    //dijit.byId(id).regExpGen = dojox.validate.regexp.emailAddress;
    Validation.addRegExCheck(id, dojox.validate.regexp.emailAddress)
}

Validation.addUrlCheck = function(id, /*boolean*/onlyBeforePublish) {
    dijit.byId(id).regExpGen = dojox.validate.regexp.url;
}

Validation.addTableCellCheck = function(id, /*Array*/colIds, /*function*/caller, /*boolean*/onlyBeforePublish) {
    var funcValidate = function(){
        var isValid = true;
        // make an array if just one value was given!
        if (!(colIds instanceof Array)) colIds = [colIds];
        dojo.forEach(this.getData(), function(data, row){
            dojo.forEach(colIds, function(colId) {
                var value = data[colId];
                var result = caller(value, colId, data);
                if (result === false) {
                    markCells("ERROR", id, row, [this.columnsById[colId]]);
                    isValid = false;
                } else {
                    markCells("", id, row, [this.columnsById[colId]]);
                }
            }, this);
        }, this);
        return isValid;
    };
    
    if (onlyBeforePublish) {
        dojo.subscribe("/onBeforeObjectPublish", function(/*Array*/notPublishableIDs){
            if (!dojo.hitch(dijit.byId(id), funcValidate)()) {
                notPublishableIDs.push(id);
            }
        });
    } else {
        var widget = dijit.byId(id);
        dojo.connect(widget, "onDataChanged", funcValidate);
        widget.validate = funcValidate;
    }
}

Validation.addValueCheck = function(id, value, /*boolean*/invert, message, /*boolean*/onlyBeforePublish) {
     var func = function(){
         if (invert)
             return this.get("value") != value;
         else
             return this.get("value") == value;
     }
     this.addCheck(id, func, message, onlyBeforePublish);
}

Validation.addNumberCheck = function(id, min, max, /*invalidMessage*/message, /*boolean*/onlyBeforePublish) {
    // TODO: check if widget is a numberbox and use special range function
     
    var func = function(){
        return (min ? this.get("value") >= min : true) && (max ? this.get("value") <= max : true);
    }
    
    this.addCheck(id, func, message, onlyBeforePublish);
}

Validation.addCheck = function(id, /*function*/caller, message, /*boolean*/onlyBeforePublish) {
    if (onlyBeforePublish) {
        dojo.subscribe("/onBeforeObjectPublish", function(/*Array*/notPublishableIDs) {
            if (!dojo.hitch(dijit.byId(id), caller)()) {
                notPublishableIDs.push(id);
            }
        });
    } else {
        var widget = dijit.byId(id);
        widget.validator = caller;
        widget.invalidMessage = message;
    }
}

Validation.addRegExCheck = function(id, /*function*/caller, message, /*boolean*/onlyBeforePublish) {
    if (onlyBeforePublish) {
        dojo.subscribe("/onBeforeObjectPublish", function(/*Array*/notPublishableIDs) {
            if (!dojo.hitch(dijit.byId(id), caller)()) {
                notPublishableIDs.push(id);
            }
        });
    } else {
        var widget = dijit.byId(id);
        widget.validator = caller;
        widget.invalidMessage = message;
    }
}