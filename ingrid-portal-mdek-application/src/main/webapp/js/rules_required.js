/*
 * Special rules for form items
 */


/*
 * Rules that change the required state of a field depending on another field's value
 */

/*function applyRules() {
	// RULE 1
	// Applies to: erfassung object -> geoinfo/karte -> fachbezug -> digitale repr�sentation Id = ref1Representation
	// Rule: If value = 'Vector' then Vektorformat (below) is required
	var representationTable = UtilGrid.getTable("ref1Representation");
	if (representationTable) {
      dojo.connect(representationTable, "onDataChanged", applyRule1);
	}

	// RULE 2
	// Applies to: erfassung object -> verschlagwortung -> umweltthemen Ids = thesaurusEnvTopics
	// Rule: If value != leer then Umweltthemen is required
	var thesaurusEnvTopicsTable = UtilGrid.getTable("thesaurusEnvTopics");
	var thesaurusEnvExtResCheckBox = dijit.byId("thesaurusEnvExtRes");
	if (thesaurusEnvTopicsTable) {
		dojo.connect(thesaurusEnvTopicsTable, "onDataChanged", applyRule2);
	}
	if (thesaurusEnvExtResCheckBox) {
	  dojo.connect(thesaurusEnvExtResCheckBox, "onChange", function(val) {applyRule2();});
	  dojo.connect(thesaurusEnvExtResCheckBox, "onClick", function(obj, field) {applyRule2();});
	}
	
	// RULE 3
	// Applies to: erfassung object -> zeitbezug -> Zeitbezug des Dateninhaltes Id = timeRefType
	// Rule: If value == 'von' then timeRefDate2 is visible
	var refTypeList = dijit.byId("timeRefType");
	if (refTypeList) {
	  dojo.connect(refTypeList, "onChange", function(value) {applyRule3(value);});
	}
	// initialize
	applyRule3("");
*/	
	
	// RULE 4
	
	// Applies to: erfassung adresse -> adresse und aufgaben -> postfach/plz (postfach) Ids = addressPOBox, addressZipPOBox
	// Rule: If value != leer then other fields is required, strasse, plz are not required
	// INSPIRE CHANGE - Remove required state.
/*
	var addressPOBoxField = dijit.byId("addressPOBox");
	var addressZipPOBoxField = dijit.byId("addressZipPOBox");
	if (addressPOBoxField) {
	  dojo.connect(addressPOBoxField, "update", function() {applyRule4();});
	}
	if (addressZipPOBoxField) {
	  dojo.connect(addressZipPOBoxField, "update", function() {applyRule4();});
	}
	applyRule4();
*/

	// Applies to: erfassung object -> Raumbezug -> Geothesaurus-Raumbezug & freier Raumbezug Ids = spatialRefAdminUnit, spatialRefLocation
	// Rule: At least one entry with a bounding box is required
/*    var spatialRefAdminUnit = UtilGrid.getTable("spatialRefAdminUnit");
    var spatialRefLocation = UtilGrid.getTable("spatialRefLocation");
	if (spatialRefAdminUnit) {
		dojo.connect(spatialRefAdminUnit, "onDataChanged", applyRule5);
	}
	if (spatialRefLocation) {
		dojo.connect(spatialRefLocation, "onDataChanged", applyRule5);
	}

	// initialize
	applyRule5();

	var spatialRefAltMin = dijit.byId("spatialRefAltMin");
	var spatialRefAltMax = dijit.byId("spatialRefAltMax");
	var spatialRefAltMeasure = dijit.byId("spatialRefAltMeasure");
	var spatialRefAltVDate = dijit.byId("spatialRefAltVDate");
	if (spatialRefAltMin) {
		dojo.connect(spatialRefAltMin, "onChange", function(val) {applyRule6();});
	}
	if (spatialRefAltMax) {
		dojo.connect(spatialRefAltMax, "onChange", function(val) {applyRule6();});
	}
	if (spatialRefAltMeasure) {
		dojo.connect(spatialRefAltMeasure, "onChange", function(val) {applyRule6();});
	}
	if (spatialRefAltVDate) {
		dojo.connect(spatialRefAltVDate, "onChange", function(val) {applyRule6();});
	}
	applyRule6();
    
    // RULE 7
    // Set fields required if INSPIRE theme selected

    var thesaurusInspire = UtilGrid.getTable("thesaurusInspire");
    //if (thesaurusInspire) {
      // handles new and changed values on table
    dojo.connect(thesaurusInspire, "onDataChanged", applyRule7);
    //}
    applyRule7();
}*/

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
	if (dijit.byId("thesaurusEnvExtRes").checked || UtilGrid.getTableData("thesaurusEnvTopics").length != 0) {
		UtilUI.setMandatory(dojo.byId("uiElementN014"));
		UtilUI.setMandatory(dojo.byId("uiElementN015"));
	} else {
		UtilUI.setOptional(dojo.byId("uiElementN014"));
		UtilUI.setOptional(dojo.byId("uiElementN015"));		
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

/*
function applyRule4() {
	var poBoxRequired = false;
	if (dojo.byId("addressPOBox").value.length > 0 || dojo.byId("addressZipPOBox").value.length > 0)
		poBoxRequired = true;
	
	// set strasse, plz required state
	UtilUI.setRequiredState(dojo.byId("addressStreetLabel"), dojo.byId("uiElement4400"), !poBoxRequired);
	UtilUI.setRequiredState(dojo.byId("addressZipCodeLabel"), dojo.byId("uiElement4410"), !poBoxRequired);
	// set postfach, plz (postfach) required state
	UtilUI.setRequiredState(dojo.byId("addressPOBoxLabel"), dojo.byId("uiElement4420"), poBoxRequired);
	UtilUI.setRequiredState(dojo.byId("addressZipPOBoxLabel"), dojo.byId("uiElement4425"), poBoxRequired);
}
*/

function applyRule5() {
	console.debug("apply rule 5");
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

// If one of the fields contains data, all fields are mandatory
function applyRule6() {
	console.debug("apply rule 6");
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

/*
// NOW handled in FormErfassungObjektContent.setSelectedClass
    if (UtilUdk.isInspire(termsList)) {
        setRequiredState(dojo.byId("ref1SpatialSystemLabel"), dojo.byId("uiElement3500"), true);

    } else {
        setRequiredState(dojo.byId("ref1SpatialSystemLabel"), dojo.byId("uiElement3500"), false);
    }
    
    // update sections, so new required fields are shown !
    // we do this manually, does not work inside of setRequiredState ???
    var sectionNames = new Array("spatialRef");
    for (var i=0; i<sectionNames.length; i++) {
        var mode = UtilUI.getCurrentExpandModeOfSectionElement(dojo.byId(sectionNames[i]));
        toggleFields(sectionNames[i], mode);
    }
*/

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