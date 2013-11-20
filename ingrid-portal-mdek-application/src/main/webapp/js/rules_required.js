/*
 * Special rules for form items
 */

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// removed stuff not needed anymore
// see svn log, "CLEAN UP: REMOVED NOT NEEDED JAVASCRIPT FROM rules_*.js"
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

// NOTICE: Most of these functions are "called from" Profile XML !

// handler for subscriber to check for required download link when open data is selected
var openDataLinkCheck = null;

function applyRule1() {
	console.debug("apply rule 1");
	var tableData = UtilGrid.getTableData("ref1Representation");
	var labelNode = dojo.byId("ref1VFormatLabel");
	var containerNode = labelNode.parentNode;

	if (dojo.some(tableData, function(item){ return (item.title == "1"); })) {
//		UtilUI.setRequiredState(labelNode, containerNode, true);
		dijit.byId("ref1VFormatTopology").setDisabled(false);
		//dijit.byId("ref1VFormatDetails").set('_canEdit', true);
        UtilGrid.updateOption("ref1VFormatDetails", "editable", true);
	} else {
//	    UtilUI.setRequiredState(labelNode, containerNode, false);
		dijit.byId("ref1VFormatTopology").setValue(null);
		dijit.byId("ref1VFormatTopology").setDisabled(true);
		//dijit.byId("ref1VFormatDetails").clear();
		//dijit.byId("ref1VFormatDetails").set('_canEdit', false);
        UtilGrid.setTableData("ref1VFormatDetails", []);
        UtilGrid.updateOption("ref1VFormatDetails", "editable", false);
	}
}

function applyRule2() {
	console.debug("apply rule 2");
	if (!dojo.hasClass("uiElementN014", "hide")) {
    	if (dijit.byId("thesaurusEnvExtRes").checked || UtilGrid.getTableData("thesaurusEnvTopics").length != 0) {
			UtilUI.setMandatory(dojo.byId("uiElementN014"));
    		UtilUI.setMandatory(dojo.byId("uiElementN015"));
    	} else {
    		UtilUI.setOptional(dojo.byId("uiElementN014"));
    		UtilUI.setOptional(dojo.byId("uiElementN015"));		
    	}
	}
}
	
function applyRule3(value) {
	console.debug("apply rule 3");
    if (value.indexOf("von") == 0) {
		dojo.style("timeRefDate2Editor", "display", "block");
		var dateValue = dijit.byId("timeRefDate1").get("value");
		var date2Widget = dijit.byId("timeRefDate2");
		//date2Widget.attr("value", dateValue);
		if (!dateValue) {
            dijit.byId("timeRefDate1").constraints.max = dateValue;
            date2Widget.constraints.min = dateValue;
        }
		date2Widget.validate();
	}
	else {
		dojo.style("timeRefDate2Editor", "display", "none");
		// reset constraints for min/max date
		delete(dijit.byId("timeRefDate1").constraints.max);// = null;
		delete(dijit.byId("timeRefDate2").constraints.min);// = null;
		dijit.byId("timeRefDate1").validate();
	}
  // date must not be null when value != ""
}

function applyRule5() {
    console.debug("apply rule 5");
    if (!dojo.hasClass("uiElementN008", "hide")) {
    	var snsHasBB = false;
    	var freeHasBB = false;
    	var snsData = UtilGrid.getTableData("spatialRefAdminUnit");
    	var freeData = UtilGrid.getTableData("spatialRefLocation");
    
    	for (var i = 0; i < snsData.length; ++i) {
    		if (snsData[i].longitude1 && snsData[i].longitude2 && snsData[i].latitude1 && snsData[i].latitude2)
    			snsHasBB = true;
    	}
    	for (var i = 0; i < freeData.length; ++i) {
    		// The values stored in freeData[] can be strings or numbers (when first loaded)
    		// -> convert them to strings and check the size
    		var lon1Length = dojo.trim(freeData[i].longitude1+"").length;
    		var lon2Length = dojo.trim(freeData[i].longitude2+"").length;
    		var lat1Length = dojo.trim(freeData[i].latitude1+"").length;
    		var lat2Length = dojo.trim(freeData[i].latitude2+"").length;
    		if (lon1Length != 0 && lon2Length != 0 && lat1Length != 0 && lat2Length != 0)
    			freeHasBB = true;
    	}
    
    	if (snsHasBB || !freeHasBB) {
    		UtilUI.setMandatory(dojo.byId("uiElementN006"));
    	} else {
    		UtilUI.setOptional(dojo.byId("uiElementN006"));		
    	}
    	if (!snsHasBB) {
    		UtilUI.setMandatory(dojo.byId("uiElementN008"));
    	} else {
    		UtilUI.setOptional(dojo.byId("uiElementN008"));
    	}
    }
}

// If one of the fields contains data, all fields are mandatory
function applyRule6() {
	console.debug("apply rule 6");
	
	if (!dojo.hasClass("uiElementN010", "hide")) {
    	var spatialRefAltMin = dijit.byId("spatialRefAltMin");
    	var spatialRefAltMax = dijit.byId("spatialRefAltMax");
    	var spatialRefAltMeasure = dijit.byId("spatialRefAltMeasure");
    	var spatialRefAltVDate = dijit.byId("spatialRefAltVDate");
    
    
    	if (spatialRefAltMin.getValue() || spatialRefAltMax.getValue() ||
    			spatialRefAltMeasure.getValue() || spatialRefAltVDate.getValue()) {
    		UtilUI.setMandatory(dojo.byId("uiElementN010"));
    		UtilUI.setMandatory(dojo.byId("uiElement1130"));
    		UtilUI.setMandatory(dojo.byId("uiElement5020"));
    		UtilUI.setMandatory(dojo.byId("uiElement5021"));
    		UtilUI.setMandatory(dojo.byId("uiElement5022"));
    
    	} else {
    		UtilUI.setOptional(dojo.byId("uiElementN010"));
    		UtilUI.setOptional(dojo.byId("uiElement1130"));
    		UtilUI.setOptional(dojo.byId("uiElement5020"));
    		UtilUI.setOptional(dojo.byId("uiElement5021"));
    		UtilUI.setOptional(dojo.byId("uiElement5022"));
    	}
	}
}


// If INSPIRE theme make additional fields mandatory
function applyRule7() {
	console.debug("apply rule 7");
    // checks dependent from object class !
    var objectClassStr = dijit.byId("objectClass").getValue().toLowerCase(); // Value is a string: "Classx" where x is the class
    var objectClass = objectClassStr.substr(5, 1);

    // INSPIRE mandatory only in classes "Geo-Information/Karte" class 1
    if (objectClass != "1") {
       return;
    }

    var termsList = UtilList.tableDataToList(UtilGrid.getTableData("thesaurusInspire"));

    // show/remove DQ tables in class 1 dependent from themes
    if (objectClass == "1") {
        // hide all spans underneath ref1ContentDQTables div -> DQ tables
        dojo.query("#ref1ContentDQTables > span").forEach(hideDiv);

        // then show table dependent from theme

        // Coordinate reference systems (101)
        if (dojo.some(termsList, function(iTermKey) {return (iTermKey == 101); })) {
            // ???
        }
        // Geographical grid systems (102)
        if (dojo.some(termsList, function(iTermKey) {return (iTermKey == 102); })) {
            // ???
        }
        // Geographical names (103)
        if (dojo.some(termsList, function(iTermKey) {return (iTermKey == 103); })) {
            // ???
        }
        // Administrative units (104)
        if (dojo.some(termsList, function(iTermKey) {return (iTermKey == 104); })) {
            displayDiv(dojo.byId("uiElement7509"));
            displayDiv(dojo.byId("uiElement7512"));
            displayDiv(dojo.byId("uiElement7515"));
        }
        // Addresses (105)
        if (dojo.some(termsList, function(iTermKey) {return (iTermKey == 105); })) {
            displayDiv(dojo.byId("uiElement7509"));
            displayDiv(dojo.byId("uiElement7512"));
            displayDiv(dojo.byId("uiElement7513"));
            displayDiv(dojo.byId("uiElement7520"));
            displayDiv(dojo.byId("uiElement7526"));
        }
        // Cadastral parcels (106)
        if (dojo.some(termsList, function(iTermKey) {return (iTermKey == 106); })) {
            // ???
        }
        // Transport networks (107)
        if (dojo.some(termsList, function(iTermKey) {return (iTermKey == 107); })) {
            displayDiv(dojo.byId("uiElement7509"));
            displayDiv(dojo.byId("uiElement7512"));
            displayDiv(dojo.byId("uiElement7513"));
            displayDiv(dojo.byId("uiElement7514"));
            displayDiv(dojo.byId("uiElement7515"));
            displayDiv(dojo.byId("uiElement7525"));
            displayDiv(dojo.byId("uiElement7526"));
        }
        // Hydrography (108)
        if (dojo.some(termsList, function(iTermKey) {return (iTermKey == 108); })) {
            displayDiv(dojo.byId("uiElement7509"));
            displayDiv(dojo.byId("uiElement7512"));
            displayDiv(dojo.byId("uiElement7513"));
            displayDiv(dojo.byId("uiElement7515"));
            displayDiv(dojo.byId("uiElement7526"));
            displayDiv(dojo.byId("uiElement7527"));
        }
        // Protected sites (109)
        if (dojo.some(termsList, function(iTermKey) {return (iTermKey == 109); })) {
            displayDiv(dojo.byId("uiElement7509"));
        }
    }
}

function applyRuleOpenData() {
    // hide open-data checkbox for classes 0 and 4
    dojo.subscribe("/onObjectClassChange", function(data) { 
        if (data.objClass === "Class0" || data.objClass === "Class4") {
            dojo.addClass(dojo.byId("uiElement6010"), "hide");
        } else {
            dojo.removeClass(dojo.byId("uiElement6010"), "hide");
        }
    });
    
    dojo.connect(dijit.byId("isOpenData"), "onChange", function(isChecked) {
        // get link for use constraints which will be changed depending on the
        // state of the open data checkbox
        var link = dojo.byId("availabilityUseConstraintsLink");
        var onclickValue = link.attributes.onclick.nodeValue;
        
        if (isChecked) {
            // automatically add access constraint 
            var data = UtilGrid.getTableData('availabilityAccessConstraints');
            var entryExists = dojo.some(data, function(item) {
                if (item.title == "keine") return true;
            });
            if (!entryExists) {
                data.push({title: "keine"});
                UtilGrid.setTableData('availabilityAccessConstraints', data);
            }
            
            // change codelist for 'availabilityUseConstraints'
            link.attributes.onclick.nodeValue = onclickValue.replace(/(listId:).*'}/, "$1 '6500'}");
            
            // show categories
            dojo.removeClass("uiElement6020", "hide");
            
            // make field mandatory
            dojo.addClass("uiElement6020", "required");
            
            // add check for url reference of type download when publishing
            // we check name and not id cause is combo box ! id not adapted yet if not saved !
            openDataLinkCheck = dojo.subscribe("/onBeforeObjectPublish", function(/*Array*/notPublishableIDs) {
                // get name of codelist entry for entry-id "9990" = "Download of data"/"Datendownload"
                var entryNameDownload = UtilSyslist.getSyslistEntryName(2000, 9990);
                var data = UtilGrid.getTableData("linksTo");
                var containsDownloadLink = dojo.some(data, function(item) { if (item.relationTypeName == entryNameDownload) return true; });
                if (!containsDownloadLink)
                    notPublishableIDs.push("linksTo");
            });
        } else {
            // change codelist for 'availabilityUseConstraints'
            link.attributes.onclick.nodeValue = onclickValue.replace(/(listId:).*'}/, "$1 '6020'}");
            
            // hide categories
            dojo.addClass("uiElement6020", "hide");
            
            // remove all categories
            UtilGrid.setTableData("categoriesOpenData", []);
            
            // revert field mandatory
            dojo.removeClass("uiElement6020", "required");
            
            // unregister from check for download link
            if (openDataLinkCheck)
                dojo.unsubscribe(openDataLinkCheck);
        }
    });
}

function applyRuleThesaurusInspire() {
    
    var applySpecification = function(inspireId, deleteEntry) {
        if (inspireId == 103) { UtilUI.updateEntryToConformityTable(4, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 18); }
        else if (inspireId == 104) { UtilUI.updateEntryToConformityTable(2, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 16); }
        else if (inspireId == 105) { UtilUI.updateEntryToConformityTable(1, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 15); }
        else if (inspireId == 106) { UtilUI.updateEntryToConformityTable(3, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 17); }
        else if (inspireId == 107) { UtilUI.updateEntryToConformityTable(7, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 7); }
        else if (inspireId == 108) { UtilUI.updateEntryToConformityTable(5, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 2); }
        else if (inspireId == 109) { UtilUI.updateEntryToConformityTable(6, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 14); }
        else if (inspireId == 201) { UtilUI.updateEntryToConformityTable(14, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 19); }
        else if (inspireId == 202) { UtilUI.updateEntryToConformityTable(15, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 20); }
        else if (inspireId == 203) { UtilUI.updateEntryToConformityTable(16, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 21); }
        else if (inspireId == 204) { UtilUI.updateEntryToConformityTable(17, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 22); }
        else if (inspireId == 301) { UtilUI.updateEntryToConformityTable(18, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 23); }
        else if (inspireId == 302) { UtilUI.updateEntryToConformityTable(19, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 24); }
        else if (inspireId == 303) { UtilUI.updateEntryToConformityTable(20, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 25); }
        else if (inspireId == 304) { UtilUI.updateEntryToConformityTable(21, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 26); }
        else if (inspireId == 305) { UtilUI.updateEntryToConformityTable(22, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 27); }
        else if (inspireId == 306) { UtilUI.updateEntryToConformityTable(23, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 28); }
        else if (inspireId == 307) { UtilUI.updateEntryToConformityTable(24, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 29); }
        else if (inspireId == 308) { UtilUI.updateEntryToConformityTable(25, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 30); }
        else if (inspireId == 309) { UtilUI.updateEntryToConformityTable(26, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 31); }
        else if (inspireId == 310) { UtilUI.updateEntryToConformityTable(27, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 32); }
        else if (inspireId == 311) { UtilUI.updateEntryToConformityTable(28, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 33); }
        else if (inspireId == 312) { UtilUI.updateEntryToConformityTable(29, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 34); }
        else if (inspireId == 313 || inspireId == 314) { UtilUI.updateEntryToConformityTable(30, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 35); } 
        else if (inspireId == 315) { UtilUI.updateEntryToConformityTable(31, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 36); }
        else if (inspireId == 316) { UtilUI.updateEntryToConformityTable(32, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 37); }
        else if (inspireId == 317) { UtilUI.updateEntryToConformityTable(33, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 38); }
        else if (inspireId == 318) { UtilUI.updateEntryToConformityTable(34, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 39); }
        else if (inspireId == 319) { UtilUI.updateEntryToConformityTable(35, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 40); }
        else if (inspireId == 320) { UtilUI.updateEntryToConformityTable(36, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 41); }
        else if (inspireId == 321) { UtilUI.updateEntryToConformityTable(37, deleteEntry); UtilUI.setComboBySyslistValue("availabilityDataFormatInspire", 42); }
    };
    
    // react when inspire topics has been added
    dojo.connect(UtilGrid.getTable("thesaurusInspire"), "onCellChange", function(msg) {
        var objClass = dijit.byId("objectClass").getValue();
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
    dojo.connect(UtilGrid.getTable("thesaurusInspire"), "onDeleteItems", function(msg) {
        var objClass = dijit.byId("objectClass").getValue();
        // only react if class == 1
        if (objClass == "Class1") {
            dojo.forEach(msg.items, function(item) {
                applySpecification(item.title, true);
            });
        }
    });
}

function applyRuleServiceType() {
    var typesWithBehavior = [1,2,3,5];
    var applySpecification = function(type, deleteEntry) {
        if (type == 1) UtilUI.updateEntryToConformityTable(38, deleteEntry);
        else if (type == 2) UtilUI.updateEntryToConformityTable(39, deleteEntry);
        else if (type == 3) UtilUI.updateEntryToConformityTable(40, deleteEntry);
        else if (type == 5) UtilUI.updateEntryToConformityTable(43, deleteEntry);
    };
    
    // react when inspire topics has been added
    dojo.connect(dijit.byId("ref3ServiceType"), "onChange", function(value) {
        var objClass = dijit.byId("objectClass").getValue();
        if (objClass == "Class3") {
            // remove all dependent types
            dojo.forEach(typesWithBehavior, function(type) { applySpecification(type, true); })
            // add possibly new type
            applySpecification(value, false);
        }
    });
    
}

function _updateAtomFieldVisibility(value) {
    if (value == "3") { // Downloadservice
        dojo.removeClass("uiElement3225", "hide");
    } else {
        dijit.byId("ref3IsAtomDownload").set("checked", false);
        dojo.addClass("uiElement3225", "hide");
    }
}

function applyRuleDownloadService() {
    dojo.connect(dijit.byId("ref3ServiceType"), "onChange", function(value) {
        _updateAtomFieldVisibility(value);
    });
}

function displayDiv(divElement) {
    if (divElement)
        dojo.removeClass(divElement, "hide");
}

function hideDiv(divElement) {
    if (divElement) {
        dojo.addClass(divElement, "hide");
        //UtilGrid.setTableData(divElement.id, []);
    }
}