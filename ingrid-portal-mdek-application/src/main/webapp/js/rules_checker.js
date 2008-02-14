/*
 * Functions for checking the validity of entered values.
 */

dojo.addOnLoad(function() {});

/* IDs of UI Elements for checking etc. */
var headerUiInputElements = ["objectName", "objectClass", "objectOwner"];
var generalUiInputElements = ["generalShortDesc", "generalDesc", "generalAddress"];
var spatialUiInputElements = ["spatialRefAdminUnit", "spatialRefLocation", "spatialRefAltMin", "spatialRefAltMax",
	"spatialRefAltMeasure", "spatialRefAltVDate", "spatialRefExplanation"];
var timeUiInputElements = ["timeRefType", "timeRefDate1", "timeRefDate2", "timeRefStatus", "timeRefPeriodicity", "timeRefIntervalNum",
	"timeRefIntervalUnit", "timeRefTable", "timeRefExplanation"];
var extraUiInputElements = ["extraInfoLangMetaData", "extraInfoLangData", "extraInfoPublishArea",
	"extraInfoXMLExportTable", "extraInfoLegalBasicsTable", "extraInfoPurpose", "extraInfoUse"];
var availUiInputElements = ["availabilityDataFormat", "availabilityMediaOptions", "availabilityOrderInfo",
	"availabilityCosts", "availabilityNoteUse"];
var thesUiInputElements = ["thesaurusTerms", "thesaurusTopics", "thesaurusFreeTermsList", "thesaurusEnvExtRes",
	"thesaurusEnvTopics", "thesaurusEnvCats", "linksTo"];
var class0UiInputElements = [];
var class1UiInputElements = ["ref1DataSet", "ref1Coverage", "ref1Representation", "ref1VFormatTopology", "ref1VFormatDetails",
	"ref1SpatialSystem", "ref1Scale", "ref1AltAccuracy", "ref1PosAccuracy", "ref1SymbolsText", "ref1SymbolsLink",
	"ref1KeysText", "ref1KeysLink", "ref1BasisText", "ref1BasisLink", "ref1DataBasisText", "ref1DataBasisLink", "ref1Data",
	"ref1ProcessText", "ref1ProcessLink"];
var class2UiInputElements = ["ref2Author", "ref2Publisher", "ref2PublishedIn", "ref2PublishLocation", "ref2PublishedInIssue",
	"ref2PublishedInPages", "ref2PublishedInYear", "ref2PublishedISBN", "ref2PublishedPublisher", "ref2LocationText", 
	"ref2LocationLink", "ref2DocumentType", "ref2BaseDataText", "ref2BaseDataLink", "ref2BibData", "ref2Explanation"];
var class3UiInputElements = ["ref3ServiceType", "ref3ServiceVersion", "ref3SystemEnv", "ref3History", "ref3BaseDataText",
	"ref3BaseDataLink", "ref3Explanation", "ref3Operation"];
var class4UiInputElements = ["ref4ParticipantsText", "ref4ParticipantsLink", "ref4PMText", "ref4PMLink", "ref4Explanation"];
var class5UiInputElements = ["ref5dbContent", "ref5MethodText", "ref5MethodLink", "ref5Explanation"];


var labels = ["objectNameLabel", "objectClassLabel", "generalDescLabel", "extraInfoLangDataLabel", "extraInfoLangMetaDataLabel",
			  "ref1DataSetLabel", "ref1VFormatLabel", "ref3ServiceTypeLabel", "generalAddressTableLabel", "timeRefTableLabel",
			  "thesaurusTermsLabel", "thesaurusTopicsLabel", "spatialRefAdminUnitLabel", "spatialRefLocationLabel",
			  "thesaurusEnvTopicsLabel", "thesaurusEnvCatsLabel", "extraInfoPublishAreaLabel"];


var notEmptyFields = [["objectName", "objectNameLabel"],
					  ["objectClass", "objectClassLabel"],
					  ["generalDescription", "generalDescLabel"],
					  ["extraInfoLangData", "extraInfoLangDataLabel"],
					  ["extraInfoLangMetaData", "extraInfoLangMetaDataLabel"],
					  ["extraInfoPublishArea", "extraInfoPublishAreaLabel"]];


var notEmptyFieldsClass1 = [["ref1DataSet", "ref1DataSetLabel"]]; 
var notEmptyFieldsClass3 = [["ref3ServiceType", "ref3ServiceTypeLabel"]];

/* The generalAddress table is made 'not required' for testing purpose */ 
var notEmptyTables = [/*["generalAddressTable", "generalAddressTableLabel"],*/
					  ["timeRefTable", "timeRefTableLabel"],
					  ["thesaurusTopicsList", "thesaurusTopicsLabel"]];


function resetRequiredFields() {
	for (i in labels) {
		dojo.html.removeClass(dojo.byId(labels[i]), "important");	
	}
}


function isObjectPublishable(idcObject) {
	var publishable = true;
	
	resetRequiredFields();

	for (var i in notEmptyFields) {
		if (!idcObject[notEmptyFields[i][0]] || idcObject[notEmptyFields[i][0]] == "") {
			dojo.html.addClass(dojo.byId(notEmptyFields[i][1]), "important");
			publishable = false;
		}
	}

	for (var i in notEmptyTables) {
		if (idcObject[notEmptyTables[i][0]].length == 0) {
			dojo.html.addClass(dojo.byId(notEmptyTables[i][1]), "important");
			publishable = false;
		}
	}


	// Check if one of the 'Raumbezug' tables has an entry with a bounding box
	var snsData = idcObject.spatialRefAdminUnitTable;
	var freeData = idcObject.spatialRefLocationTable;
	var hasBB = function(item) {return (item.longitude1 && item.longitude2 && item.latitude1 && item.latitude2);};
	if ( !(dojo.lang.some(snsData, hasBB) || dojo.lang.some(freeData, hasBB)) ) {
		dojo.html.addClass(dojo.byId("spatialRefAdminUnitLabel"), "important");
		dojo.html.addClass(dojo.byId("spatialRefLocationLabel"), "important");
		dojo.debug("At least one 'spatial' table has to contain an entry with a BB.");
		publishable = false;
	}

	// Check if the thesaurus table has at least three entries
	if (idcObject.thesaurusTermsTable.length < 3) {
		dojo.html.addClass(dojo.byId("thesaurusTermsLabel"), "important");		
		dojo.debug("The thesaurus table needs at least three entries.");
		publishable = false;
	}

	// If one of the 'Umweltthemen' contains an entry, both of them need to contain at least one entry
	dojo.debug("idcObject.thesaurusEnvExtRes: " + idcObject.thesaurusEnvExtRes);
	if ((idcObject.thesaurusEnvTopicsList.length > 0 && idcObject.thesaurusEnvCatsList.length == 0)
	  ||(idcObject.thesaurusEnvTopicsList.length == 0 && idcObject.thesaurusEnvCatsList.length > 0) 
	  ||(idcObject.thesaurusEnvExtRes == true && (idcObject.thesaurusEnvTopicsList.length == 0 || idcObject.thesaurusEnvCatsList.length == 0))) {
		dojo.html.addClass(dojo.byId("thesaurusEnvTopicsLabel"), "important");		
		dojo.html.addClass(dojo.byId("thesaurusEnvCatsLabel"), "important");		
		dojo.debug("If one of the 'Umweltthemen' contains an entry, both of them need to contain at least one entry.");
		publishable = false;
	}

	// Check the required fields per object class:
	switch (idcObject.objectClass)
	{
		case '0':
			// No additional required fields for object class 0
			break;
		case '1':
			for (var i in notEmptyFieldsClass1) {
				if (!idcObject[notEmptyFieldsClass1[i][0]] || idcObject[notEmptyFieldsClass1[i][0]] == "") {
					dojo.html.addClass(dojo.byId(notEmptyFieldsClass1[i][1]), "important");
					publishable = false;
					dojo.debug("Object class one required field empty.");				
				}
			}
			for (var i in idcObject.ref1Representation) {
				if (idcObject.ref1Representation[i] == "1") {
					if (idcObject.ref1VFormatTopology == ""
					  ||idcObject.ref1VFormatDetails.length == 0) {
						dojo.html.addClass(dojo.byId("ref1VFormatLabel"), "important");
						publishable = false;
						dojo.debug("Vector format needs to be filled.");
						continue;
					}
				}
			}

			break;
		case '2':
			// No additional required fields for object class 2
			break;
		case '3':
			for (var i in notEmptyFieldsClass3) {
				if (!idcObject[notEmptyFieldsClass3[i][0]] || idcObject[notEmptyFieldsClass3[i][0]] == "") {
					dojo.html.addClass(dojo.byId(notEmptyFieldsClass3[i][1]), "important");
					publishable = false;
					dojo.debug("Object class three required field empty.");				
				}
			}
			break;
		case '4':
			// No additional required fields for object class 4
			break;
		case '5':
			// No additional required fields for object class 5
			break;
		default:
			break;
	}

	return publishable;
}

function checkValidityOfInputElements() {
	var isValid = function(widgetId) {
//		dojo.debug(widgetId);
		var widget = dojo.widget.byId(widgetId);
		if (widget.isValid) {	// check if the widget has an isValid method
			if (widget.isEmpty) { // check if the widget has an isMissing method
				return (widget.isEmpty() || widget.isValid());
			} else {
				return widget.isValid();
			}
		} else {
//			dojo.debug(widgetId+" has no isValid method.");
			return true;
		}
	}

	var objectClassStr = dojo.widget.byId("objectClass").getValue().toLowerCase(); // Value is a string: "Classx" where x is the class
	var objectClass = objectClassStr[5];

	if (dojo.lang.every(headerUiInputElements, isValid) && dojo.lang.every(generalUiInputElements, isValid)
	 && dojo.lang.every(spatialUiInputElements, isValid) && dojo.lang.every(extraUiInputElements, isValid)
	 && dojo.lang.every(thesUiInputElements, isValid) && dojo.lang.every(timeUiInputElements, isValid)
	 && dojo.lang.every(this[objectClassStr+"UiInputElements"], isValid)) {
		// Object class 0 doesn't have availability information. For all other classes check availability and return
		if (objectClass != "0") {
			return dojo.lang.every(availUiInputElements, isValid);
		}
	} else {
		return false;
	}
	
	return true;
}