/*
 * Special rules for form items
 */


/*
 * Rules that change the required state of a field depending on another field's value
 */

dojo.addOnLoad(function() {
	// RULE 1
	// Applies to: erfassung object -> geoinfo/karte -> fachbezug -> digitale repraesentation Id = ref1Representation
	// Rule: If value = 'Vector' then Vektorformat (below) is required
	var representationTable = dojo.widget.byId("ref1Representation");
	if (representationTable) {
	  dojo.event.connect(representationTable, "onValueChanged", function(obj, field) {applyRule1();});
	  dojo.event.connect(representationTable, "onValueAdded", function(obj) {applyRule1();});
	  dojo.event.connect(representationTable, "onValueDeleted", function(obj) {applyRule1();});
	  dojo.event.connect(representationTable.store, "onSetData", function() {applyRule1();});
	}

	// RULE 2
	// Applies to: erfassung object -> verschlagwortung -> umweltthemen Ids = thesaurusEnvTopics, thesaurusEnvCats
	// Rule: If value != leer then Umweltthemen, Umweltkategorien is required
	var thesaurusEnvTopicsTable = dojo.widget.byId("thesaurusEnvTopics");
	var thesaurusEnvCatsTable = dojo.widget.byId("thesaurusEnvCats");
	var thesaurusEnvExtResCheckBox = dojo.widget.byId("thesaurusEnvExtRes");
	if (thesaurusEnvTopicsTable) {
	  dojo.event.connect(thesaurusEnvTopicsTable, "onValueChanged", function(obj, field) {applyRule2();});
	  dojo.event.connect(thesaurusEnvTopicsTable, "onValueAdded", function(obj) {applyRule2();});
	  dojo.event.connect(thesaurusEnvTopicsTable, "onValueDeleted", function(obj) {applyRule2();});
	  dojo.event.connect(thesaurusEnvTopicsTable.store, "onSetData", function() {applyRule2();});
	}
	if (thesaurusEnvCatsTable) {
	  dojo.event.connect(thesaurusEnvCatsTable, "onValueChanged", function(obj, field) {applyRule2();});
	  dojo.event.connect(thesaurusEnvCatsTable, "onValueAdded", function(obj) {applyRule2();});
	  dojo.event.connect(thesaurusEnvCatsTable, "onValueDeleted", function(obj) {applyRule2();});
	  dojo.event.connect(thesaurusEnvCatsTable.store, "onSetData", function() {applyRule2();});
	}
	if (thesaurusEnvExtResCheckBox) {
	  dojo.event.connect(thesaurusEnvExtResCheckBox, "setValue", function(val) {applyRule2();});
	  dojo.event.connect(thesaurusEnvExtResCheckBox, "onClick", function(obj, field) {applyRule2();});
	}
	
	// RULE 3
	// Applies to: erfassung object -> zeitbezug -> Zeitbezug des Dateninhaltes Id = timeRefType
	// Rule: If value == 'von' then timeRefDate2 is visible
	var refTypeList = dojo.widget.byId("timeRefType");
	if (refTypeList) {
	  dojo.event.connect(refTypeList, "onValueChanged", function(value) {applyRule3(value);});
	}
	// initialize
	applyRule3("");
	
	
	// RULE 4
	
	// Applies to: erfassung adresse -> adresse und aufgaben -> postfach/plz (postfach) Ids = addressPOBox, addressZipPOBox
	// Rule: If value != leer then other fields is required, strasse, plz are not required
	// INSPIRE CHANGE - Remove required state.
/*
	var addressPOBoxField = dojo.widget.byId("addressPOBox");
	var addressZipPOBoxField = dojo.widget.byId("addressZipPOBox");
	if (addressPOBoxField) {
	  dojo.event.connect(addressPOBoxField, "update", function() {applyRule4();});
	}
	if (addressZipPOBoxField) {
	  dojo.event.connect(addressZipPOBoxField, "update", function() {applyRule4();});
	}
	applyRule4();
*/

	// Applies to: erfassung object -> Raumbezug -> Geothesaurus-Raumbezug & freier Raumbezug Ids = spatialRefAdminUnit, spatialRefLocation
	// Rule: At least one entry with a bounding box is required
	var spatialRefAdminUnit = dojo.widget.byId("spatialRefAdminUnit");
	var spatialRefLocation = dojo.widget.byId("spatialRefLocation");
	if (spatialRefAdminUnit) {
	  dojo.event.connect(spatialRefAdminUnit, "onValueChanged", function(obj, field) {applyRule5();});
	  dojo.event.connect(spatialRefAdminUnit, "onValueAdded", function(obj) {applyRule5();});
	  dojo.event.connect(spatialRefAdminUnit, "onValueDeleted", function(obj) {applyRule5();});
	  dojo.event.connect(spatialRefAdminUnit.store, "onSetData", function() {applyRule5();});
	  dojo.event.connect(spatialRefAdminUnit.store, "onAddData", function(obj) {applyRule5();});
	}
	if (spatialRefLocation) {
	  dojo.event.connect(spatialRefLocation, "onValueChanged", function(obj, field) {applyRule5();});
	  dojo.event.connect(spatialRefLocation, "onValueAdded", function(obj) {applyRule5();});
	  dojo.event.connect(spatialRefLocation, "onValueDeleted", function(obj) {applyRule5();});
	  dojo.event.connect(spatialRefLocation.store, "onSetData", function() {applyRule5();});
	}

	// initialize
	applyRule5();

	var spatialRefAltMin = dojo.widget.byId("spatialRefAltMin");
	var spatialRefAltMax = dojo.widget.byId("spatialRefAltMax");
	var spatialRefAltMeasure = dojo.widget.byId("spatialRefAltMeasure");
	var spatialRefAltVDate = dojo.widget.byId("spatialRefAltVDate");
	if (spatialRefAltMin) {
		dojo.event.connect(spatialRefAltMin, "update", function(val) {applyRule6();});
	}
	if (spatialRefAltMax) {
		dojo.event.connect(spatialRefAltMax, "update", function(val) {applyRule6();});
	}
	if (spatialRefAltMeasure) {
		dojo.event.connect(spatialRefAltMeasure, "onValueChanged", function(val) {applyRule6();});
	}
	if (spatialRefAltVDate) {
		dojo.event.connect(spatialRefAltVDate, "onValueChanged", function(val) {applyRule6();});
	}
	applyRule6();


	// RULE 7
	// Set fields required if INSPIRE theme selected

    var thesaurusInspire = dojo.widget.byId("thesaurusInspire");
    if (thesaurusInspire) {
	  dojo.event.connect(thesaurusInspire, "onValueChanged", function(obj, field) {applyRule7();});
      dojo.event.connect(thesaurusInspire, "onValueAdded", function(obj) {applyRule7();});
      dojo.event.connect(thesaurusInspire, "onValueDeleted", function(obj) {applyRule7();});
      dojo.event.connect(thesaurusInspire.store, "onSetData", function() {applyRule7();});
      dojo.event.connect(thesaurusInspire.store, "onAddData", function(obj) {applyRule7();});
    }
    applyRule7();
});

function applyRule1() {
	var tableData = dojo.widget.byId("ref1Representation").store.getData();
	var labelNode = dojo.byId("ref1VFormatLabel");
	var containerNode = labelNode.parentNode;

	if (dojo.lang.some(tableData, function(item){ return (item.title == "1"); })) {
//		setRequiredState(labelNode, containerNode, true);
		dojo.widget.byId("ref1VFormatTopology").enable();
		dojo.widget.byId("ref1VFormatDetails").enable();
	} else {
//	    setRequiredState(labelNode, containerNode, false);
		dojo.widget.byId("ref1VFormatTopology").setValue(null);
		dojo.widget.byId("ref1VFormatTopology").disable();
		dojo.widget.byId("ref1VFormatDetails").clear();
		dojo.widget.byId("ref1VFormatDetails").disable();
	}
}

function applyRule2() {
	var required = false;
	if (dojo.widget.byId("thesaurusEnvExtRes").checked || dojo.widget.byId("thesaurusEnvTopics").store.getData().length != 0 || dojo.widget.byId("thesaurusEnvCats").store.getData().length != 0)
	  required = true;

	var labelNode1 = dojo.byId("thesaurusEnvironmentLabel");
	var labelNode2 = dojo.byId("thesaurusEnvTopicsLabel");
	var labelNode3 = dojo.byId("thesaurusEnvCatsLabel");
	var containerNode1 = dojo.byId("uiElementN014");
	var containerNode2 = dojo.byId("uiElementN015");
	var containerNode3 = dojo.byId("uiElementN016");

	setRequiredState(labelNode1, containerNode1, required);
	setRequiredState(labelNode2, containerNode2, required);
	setRequiredState(labelNode3, containerNode3, required);
}
	
function applyRule3(value) {
  var datePickerNode = dojo.byId("timeRefDate2Editor");
  if (datePickerNode) {
    if (value.indexOf("von") == 0)
      datePickerNode.style.display = "block";
    else
      datePickerNode.style.display = "none";
  }
}

/*
function applyRule4() {
	var poBoxRequired = false;
	if (dojo.byId("addressPOBox").value.length > 0 || dojo.byId("addressZipPOBox").value.length > 0)
		poBoxRequired = true;
	
	// set strasse, plz required state
	setRequiredState(dojo.byId("addressStreetLabel"), dojo.byId("uiElement4400"), !poBoxRequired);
	setRequiredState(dojo.byId("addressZipCodeLabel"), dojo.byId("uiElement4410"), !poBoxRequired);
	// set postfach, plz (postfach) required state
	setRequiredState(dojo.byId("addressPOBoxLabel"), dojo.byId("uiElement4420"), poBoxRequired);
	setRequiredState(dojo.byId("addressZipPOBoxLabel"), dojo.byId("uiElement4425"), poBoxRequired);
}
*/

function applyRule5() {
	var snsHasBB = false;
	var freeHasBB = false;
	var snsData = dojo.widget.byId("spatialRefAdminUnit").store.getData();
	var freeData = dojo.widget.byId("spatialRefLocation").store.getData();

	for (var i = 0; i < snsData.length; ++i) {
		if (snsData[i].longitude1 && snsData[i].longitude2 && snsData[i].latitude1 && snsData[i].latitude2)
			snsHasBB = true;
	}
	for (var i = 0; i < freeData.length; ++i) {
		// The values stored in freeData[] can be strings or numbers (when first loaded)
		// -> convert them to strings and check the size
		var lon1Length = dojo.string.trim(freeData[i].longitude1+"").length;
		var lon2Length = dojo.string.trim(freeData[i].longitude2+"").length;
		var lat1Length = dojo.string.trim(freeData[i].latitude1+"").length;
		var lat2Length = dojo.string.trim(freeData[i].latitude2+"").length;
		if (lon1Length != 0 && lon2Length != 0 && lat1Length != 0 && lat2Length != 0)
			freeHasBB = true;
	}

	var labelNode1 = dojo.byId("spatialRefAdminUnitLabel");
	var labelNode2 = dojo.byId("spatialRefLocationLabel");
	var container1 = dojo.byId("uiElementN006");
	var container2 = dojo.byId("uiElementN008");

	setRequiredState(labelNode1, container1, snsHasBB || !freeHasBB);
	setRequiredState(labelNode2, container2, !snsHasBB);
}

// If one of the fields contains data, all fields are mandatory
function applyRule6() {
	var spatialRefAltMin = dojo.widget.byId("spatialRefAltMin");
	var spatialRefAltMax = dojo.widget.byId("spatialRefAltMax");
	var spatialRefAltMeasure = dojo.widget.byId("spatialRefAltMeasure");
	var spatialRefAltVDate = dojo.widget.byId("spatialRefAltVDate");


	if (spatialRefAltMin.getValue() || spatialRefAltMax.getValue() ||
			spatialRefAltMeasure.getValue() || spatialRefAltVDate.getValue()) {
		setRequiredState(dojo.byId("spatialRefAltHeightLabel"), dojo.byId("uiElementN010"), true);
		setRequiredState(dojo.byId("spatialRefAltMinLabel"), dojo.byId("uiElement1130"), true);
		setRequiredState(dojo.byId("spatialRefAltMaxLabel"), dojo.byId("uiElement5020"), true);
		setRequiredState(dojo.byId("spatialRefAltMeasureLabel"), dojo.byId("uiElement5021"), true);
		setRequiredState(dojo.byId("spatialRefAltVDateLabel"), dojo.byId("uiElement5022"), true);

	} else {
		setRequiredState(dojo.byId("spatialRefAltHeightLabel"), dojo.byId("uiElementN010"), false);
		setRequiredState(dojo.byId("spatialRefAltMinLabel"), dojo.byId("uiElement1130"), false);
		setRequiredState(dojo.byId("spatialRefAltMaxLabel"), dojo.byId("uiElement5020"), false);
		setRequiredState(dojo.byId("spatialRefAltMeasureLabel"), dojo.byId("uiElement5021"), false);
		setRequiredState(dojo.byId("spatialRefAltVDateLabel"), dojo.byId("uiElement5022"), false);
	}
}

// If INSPIRE theme make additional fields mandatory
function applyRule7() {
	var termsList = UtilList.tableDataToList(dojo.widget.byId("thesaurusInspire").store.getData());

    if (UtilUdk.isInspire(termsList)) {
        setRequiredState(dojo.byId("ref1SpatialSystemLabel"), dojo.byId("uiElement3500"), true);
        setRequiredState(dojo.byId("availabilityDataFormatLabel"), dojo.byId("uiElement1320"), true);
    } else {
        setRequiredState(dojo.byId("ref1SpatialSystemLabel"), dojo.byId("uiElement3500"), false);
        setRequiredState(dojo.byId("availabilityDataFormatLabel"), dojo.byId("uiElement1320"), false);
    }
	
	// update sections, so new required fields are shown !
	// we do this manually, does not work inside of setRequiredState ???
	var sectionNames = new Array("spatialRef", "availability");
	for (var i=0; i<sectionNames.length; i++) {
        var mode = UtilUI.getCurrentExpandModeOfSectionElement(dojo.byId(sectionNames[i]));
        toggleFields(sectionNames[i], mode);
	}

	// show/remove DQ tables in class 1 dependent from themes
    var objectClassStr = dojo.widget.byId("objectClass").getValue().toLowerCase(); // Value is a string: "Classx" where x is the class
    var objectClass = objectClassStr.substr(5, 1);

	if (objectClass == "1") {
        // hide all DQ tables
        dojo.lang.forEach(dqUiTableElements, function(dqTableId) {
            hideDiv(dojo.byId(dqTableId + "Container"));
        });

		// then show table dependent from theme

        // Coordinate reference systems (101)
        if (dojo.lang.some(termsList, function(iTermKey) {return (iTermKey == 101); })) {
            // ???
		}
        // Geographical grid systems (102)
        if (dojo.lang.some(termsList, function(iTermKey) {return (iTermKey == 102); })) {
            // ???
        }
        // Geographical names (103)
        if (dojo.lang.some(termsList, function(iTermKey) {return (iTermKey == 103); })) {
            displayDiv(dojo.byId("dq110TableContainer"));
            displayDiv(dojo.byId("dq117TableContainer"));
        }
        // Administrative units (104)
        if (dojo.lang.some(termsList, function(iTermKey) {return (iTermKey == 104); })) {
            displayDiv(dojo.byId("dq109TableContainer"));
            displayDiv(dojo.byId("dq110TableContainer"));
            displayDiv(dojo.byId("dq112TableContainer"));
            displayDiv(dojo.byId("dq115TableContainer"));
            displayDiv(dojo.byId("dq117TableContainer"));
        }
        // Addresses (105)
        if (dojo.lang.some(termsList, function(iTermKey) {return (iTermKey == 105); })) {
            displayDiv(dojo.byId("dq109TableContainer"));
            displayDiv(dojo.byId("dq110TableContainer"));
            displayDiv(dojo.byId("dq112TableContainer"));
            displayDiv(dojo.byId("dq113TableContainer"));
            displayDiv(dojo.byId("dq117TableContainer"));
            displayDiv(dojo.byId("dq120TableContainer"));
            displayDiv(dojo.byId("dq126TableContainer"));
        }
        // Cadastral parcels (106)
        if (dojo.lang.some(termsList, function(iTermKey) {return (iTermKey == 106); })) {
            displayDiv(dojo.byId("dq110TableContainer"));
            displayDiv(dojo.byId("dq117TableContainer"));
        }
        // Transport networks (107)
        if (dojo.lang.some(termsList, function(iTermKey) {return (iTermKey == 107); })) {
            displayDiv(dojo.byId("dq109TableContainer"));
            displayDiv(dojo.byId("dq110TableContainer"));
            displayDiv(dojo.byId("dq112TableContainer"));
            displayDiv(dojo.byId("dq113TableContainer"));
            displayDiv(dojo.byId("dq114TableContainer"));
            displayDiv(dojo.byId("dq115TableContainer"));
            displayDiv(dojo.byId("dq117TableContainer"));
            displayDiv(dojo.byId("dq125TableContainer"));
            displayDiv(dojo.byId("dq126TableContainer"));
        }
        // Hydrography (108)
        if (dojo.lang.some(termsList, function(iTermKey) {return (iTermKey == 108); })) {
            displayDiv(dojo.byId("dq109TableContainer"));
            displayDiv(dojo.byId("dq110TableContainer"));
            displayDiv(dojo.byId("dq112TableContainer"));
            displayDiv(dojo.byId("dq113TableContainer"));
            displayDiv(dojo.byId("dq115TableContainer"));
            displayDiv(dojo.byId("dq117TableContainer"));
            displayDiv(dojo.byId("dq126TableContainer"));
            displayDiv(dojo.byId("dq127TableContainer"));
        }
        // Protected sites (109)
        if (dojo.lang.some(termsList, function(iTermKey) {return (iTermKey == 109); })) {
            displayDiv(dojo.byId("dq109TableContainer"));
            displayDiv(dojo.byId("dq110TableContainer"));
            displayDiv(dojo.byId("dq117TableContainer"));
        }

        // update DQ section, so new fields are shown !
        // NECESSARY ???
        var mode = UtilUI.getCurrentExpandModeOfSectionElement(dojo.byId("refClass1DQ"));
        toggleFields("refClass1DQ", mode);
    }
}

function displayDiv(divElement) {
    if (divElement)
        divElement.style.display = 'block';
}

function hideDiv(divElement) {
    if (divElement)
        divElement.style.display = 'none';
}
