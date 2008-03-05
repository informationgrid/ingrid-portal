/*
 * Special rules for form items
 */


/*
 * Rules that change the required state of a field depending on another field's value
 */

dojo.addOnLoad(function() {
	// RULE 1
	// Applies to: erfassung object -> geoinfo/karte -> fachbezug -> digitale repräsentation Id = ref1Representation
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
	var addressPOBoxField = dojo.widget.byId("addressPOBox");
	var addressZipPOBoxField = dojo.widget.byId("addressZipPOBox");
	if (addressPOBoxField) {
	  dojo.event.connect(addressPOBoxField, "update", function() {applyRule4();});
	}
	if (addressZipPOBoxField) {
	  dojo.event.connect(addressZipPOBoxField, "update", function() {applyRule4();});
	}
	applyRule4();


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
	if (dojo.widget.byId("thesaurusEnvExtRes").checked || dojo.widget.byId("thesaurusEnvTopics").hasData() || dojo.widget.byId("thesaurusEnvCats").hasData())
	  required = true;
	
	var labelNode1 = dojo.byId("thesaurusEnvTopicsLabel");
	var labelNode2 = dojo.byId("thesaurusEnvCatsLabel");
	var containerNode = labelNode1.parentNode.parentNode.parentNode;
	setRequiredState(labelNode1, containerNode, required);
	setRequiredState(labelNode2, containerNode, required);
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

function applyRule4() {
	var poBoxRequired = false;
	if (dojo.byId("addressPOBox").value.length > 0 || dojo.byId("addressZipPOBox").value.length > 0)
		poBoxRequired = true;
	
	// set strasse, plz required state
	setRequiredState(dojo.byId("addressStreetLabel"), null, !poBoxRequired);
	setRequiredState(dojo.byId("addressZipCodeLabel"), null, !poBoxRequired);
	// set postfach, plz (postfach) required state
	setRequiredState(dojo.byId("addressPOBoxLabel"), null, poBoxRequired);
	setRequiredState(dojo.byId("addressZipPOBoxLabel"), null, poBoxRequired);
}
	

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
		if (freeData[i].longitude1 && freeData[i].longitude2 && freeData[i].latitude1 && freeData[i].latitude2)
			freeHasBB = true;
	}

	var labelNode1 = dojo.byId("spatialRefAdminUnitLabel");
	var labelNode2 = dojo.byId("spatialRefLocationLabel");
	var container1 = dojo.byId("spatialRefAdminUnitContainer");
	var container2 = dojo.byId("spatialRefLocationContainer");
// TODO fix toggleContent() before modifying the container 'required' state. Otherwise we could
// end up in a situation where 'unrequired' containers are shown when only required containers shoud be displayed.

	setRequiredState(labelNode1, container1, snsHasBB || !freeHasBB);
	setRequiredState(labelNode2, container2, !snsHasBB);
}
