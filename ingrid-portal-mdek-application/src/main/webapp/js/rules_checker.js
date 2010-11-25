/*
 * Functions for checking the validity of entered values.
 */

dojo.addOnLoad(function() {});

/* IDs of UI Elements for checking etc. */
var headerUiInputElements = ["objectName", "objectClass", "objectOwner"];
var generalUiInputElements = ["generalShortDesc", "generalDesc", "generalAddress"];
var spatialUiInputElements = ["spatialRefAdminUnit", "spatialRefLocation", "ref1SpatialSystem", "spatialRefAltMin", "spatialRefAltMax",
	"spatialRefAltMeasure", "spatialRefAltVDate", "spatialRefExplanation"];
var timeUiInputElements = ["timeRefType", "timeRefDate1", "timeRefDate2", "timeRefStatus", "timeRefPeriodicity", "timeRefIntervalNum",
	"timeRefIntervalUnit", "timeRefTable", "timeRefExplanation"];
var extraUiInputElements = ["extraInfoLangMetaData", "extraInfoLangData", "extraInfoPublishArea", "extraInfoCharSetData",
	"extraInfoXMLExportTable", "extraInfoLegalBasicsTable", "extraInfoPurpose", "extraInfoUse"];
var availUiInputElements = ["availabilityAccessConstraints", "availabilityUseConstraints", "availabilityDataFormat", "availabilityMediaOptions", "availabilityOrderInfo"];
var thesUiInputElements = ["thesaurusTerms", "thesaurusTopics", "thesaurusInspire", "thesaurusEnvExtRes",
	"thesaurusEnvTopics", "thesaurusEnvCats", "linksTo"];
var class0UiInputElements = [];
var class1UiInputElements = ["ref1ObjectIdentifier", "ref1DataSet", "ref1Coverage", "ref1Representation", "ref1VFormatTopology", "ref1VFormatDetails",
	"ref1Scale", "ref1AltAccuracy", "ref1PosAccuracy", "ref1SymbolsText", "ref1SymbolsLink",
	"ref1KeysText", "ref1KeysLink", "ref1BasisText", "ref1BasisLink", "ref1DataBasisText", "ref1DataBasisLink", "ref1Data",
	"ref1ProcessText", "ref1ProcessLink", "extraInfoConformityTable"];
var class2UiInputElements = ["ref2Author", "ref2Publisher", "ref2PublishedIn", "ref2PublishLocation", "ref2PublishedInIssue",
	"ref2PublishedInPages", "ref2PublishedInYear", "ref2PublishedISBN", "ref2PublishedPublisher", "ref2LocationText", 
	"ref2LocationLink", "ref2DocumentType", "ref2BaseDataText", "ref2BaseDataLink", "ref2BibData", "ref2Explanation"];
var class3UiInputElements = ["ref3ServiceType", "ref3ServiceTypeTable", "ref3ServiceVersion", "ref3SystemEnv", "ref3History", "ref3BaseDataText",
	"ref3BaseDataLink", "ref3Explanation", "ref3Scale", "ref3Operation", "extraInfoConformityTable", "ref3HasAccessConstraint"];
var class4UiInputElements = ["ref4ParticipantsText", "ref4ParticipantsLink", "ref4PMText", "ref4PMLink", "ref4Explanation"];
var class5UiInputElements = ["ref5dbContent", "ref5MethodText", "ref5MethodLink", "ref5Explanation"];
var class3UiInputElements = ["ref3ServiceType", "ref3ServiceTypeTable", "ref3ServiceVersion", "ref3SystemEnv", "ref3History", "ref3BaseDataText",
                         	"ref3BaseDataLink", "ref3Explanation", "ref3Scale", "ref3Operation", "extraInfoConformityTable"];
var class6UiInputElements = ["ref6ServiceType", "ref6ServiceVersion", "ref6SystemEnv", "ref6History", "ref6BaseDataText",
                         	"ref6BaseDataLink", "ref6Explanation", "ref6UrlList" ];

// Address Type is not included since the field is filled automatically
var adrUiInputElements = [/*"addressType",*/ "addressOwner", "addressStreet", "addressCountry", "addressZipCode", "addressCity", "addressPOBox",
	"addressZipPOBox", "addressNotes", "addressCom", "addressTasks", "thesaurusTermsAddress"];
var adrClass0UiInputElements = ["headerAddressType0Unit"];
var adrClass1UiInputElements = ["headerAddressType1Unit"];
var adrClass2UiInputElements = ["headerAddressType2Lastname", "headerAddressType2Firstname", "headerAddressType2Style",
	"headerAddressType2Title"];
var adrClass3UiInputElements = ["headerAddressType3Lastname", "headerAddressType3Firstname", "headerAddressType3Style",
	"headerAddressType3Title", "headerAddressType3Institution"];


var labels = ["objectNameLabel", "objectClassLabel", "objectOwnerLabel", "generalDescLabel", "extraInfoLangDataLabel", "extraInfoLangMetaDataLabel", "extraInfoCharSetDataLabel", 
			  "extraInfoConformityTableLabel", "availabilityAccessConstraintsLabel", "availabilityUseConstraintsLabel", "ref1BasisTabContainerLabel", "ref1ObjectIdentifierLabel",
			  "ref1DataSetLabel", "ref1VFormatLabel", "ref3ServiceTypeLabel", "ref3ServiceTypeTableLabel", "ref6ServiceTypeLabel", "generalAddressTableLabel", "timeRefTableLabel",
			  "thesaurusTermsLabel", "thesaurusTopicsLabel", "thesaurusInspireLabel", "spatialRefAdminUnitLabel", "spatialRefLocationLabel", "spatialRefAltHeightLabel",
			  "spatialRefAltMinLabel", "spatialRefAltMaxLabel", "spatialRefAltMeasureLabel", "spatialRefAltVDateLabel",
			  "thesaurusEnvironmentLabel", "thesaurusEnvTopicsLabel", "thesaurusEnvCatsLabel", "extraInfoPublishAreaLabel",
			  "addressTypeLabel", "addressOwnerLabel", "headerAddressType0UnitLabel", "headerAddressType1UnitLabel", "headerAddressType2LastnameLabel",
			  "headerAddressType2StyleLabel", "headerAddressType3LastnameLabel", "headerAddressType3StyleLabel",
			  "addressComLabel", "addressStreetLabel", "addressCountryLabel", "addressZipCodeLabel", "addressCityLabel", "addressPOBoxLabel",
			  "addressZipPOBoxLabel", "ref1SpatialSystemLabel", "availabilityDataFormatLabel",
			  "dq109TableLabel", "dq110TableLabel"];


var notEmptyFields = [["objectName", "objectNameLabel"],
					  ["objectClass", "objectClassLabel"],
					  ["objectOwner", "objectOwnerLabel"],
					  ["generalDescription", "generalDescLabel"],
					  ["extraInfoLangDataCode", "extraInfoLangDataLabel"],
					  ["extraInfoLangMetaDataCode", "extraInfoLangMetaDataLabel"],
					  ["extraInfoPublishArea", "extraInfoPublishAreaLabel"],
                      ["extraInfoCharSetDataCode", "extraInfoCharSetDataLabel"]];

var notEmptyFieldsClass1 = [["ref1BasisText", "ref1BasisTabContainerLabel"],
							["ref1ObjectIdentifier", "ref1ObjectIdentifierLabel"],
                            ["ref1DataSet", "ref1DataSetLabel"]]; 
var notEmptyFieldsClass3 = [["ref3ServiceType", "ref3ServiceTypeLabel"]];

var notEmptyFieldsClass6 = [["ref6ServiceType", "ref6ServiceTypeLabel"]];

var notEmptyTables = [["generalAddressTable", "generalAddressTableLabel"],
					  ["timeRefTable", "timeRefTableLabel"],
					  ["thesaurusTopicsList", "thesaurusTopicsLabel"],
					  ["thesaurusInspireTermsList", "thesaurusInspireLabel"]];

// TODO Add class 2, 4, 5 to isObjectPublishable when needed
var notEmptyTablesClass1 = [["availabilityAccessConstraints", "availabilityAccessConstraintsLabel"],
                            ["availabilityUseConstraints", "availabilityUseConstraintsLabel"],
							["extraInfoConformityTable", "extraInfoConformityTableLabel"]];
var notEmptyTablesClass2 = [["availabilityAccessConstraints", "availabilityAccessConstraintsLabel"],
                            ["availabilityUseConstraints", "availabilityUseConstraintsLabel"]];
var notEmptyTablesClass3 = [["ref3ServiceTypeTable", "ref3ServiceTypeTableLabel"],
							["extraInfoConformityTable", "extraInfoConformityTableLabel"],
                            ["availabilityAccessConstraints", "availabilityAccessConstraintsLabel"],
                            ["availabilityUseConstraints", "availabilityUseConstraintsLabel"]];
var notEmptyTablesClass4 = [["availabilityAccessConstraints", "availabilityAccessConstraintsLabel"],
                            ["availabilityUseConstraints", "availabilityUseConstraintsLabel"]];
var notEmptyTablesClass5 = [["availabilityAccessConstraints", "availabilityAccessConstraintsLabel"],
                            ["availabilityUseConstraints", "availabilityUseConstraintsLabel"]];
var notEmptyTablesClass6 = [["availabilityAccessConstraints", "availabilityAccessConstraintsLabel"],
                            ["availabilityUseConstraints", "availabilityUseConstraintsLabel"]];


// INSPIRE changes. Only one email address is required 
var notEmptyAddressFields = [// ["addressClass", "addressTypeLabel"],
							 // ["addressOwner", "addressOwnerLabel"],
						     // ["countryCode", "addressCountryLabel"],
						     // ["city", "addressCityLabel"]
							 ];

var notEmptyAddressTables = [["communication", "addressComLabel"]];

var notEmptyAddressFieldsClass0 = [["organisation", "headerAddressType0UnitLabel"]];
var notEmptyAddressFieldsClass1 = [["organisation", "headerAddressType1UnitLabel"]];

var notEmptyAddressFieldsClass2 = [["name", "headerAddressType2LastnameLabel"],
								   ["nameForm", "headerAddressType2StyleLabel"]];
var notEmptyAddressFieldsClass3 = [["name", "headerAddressType3LastnameLabel"],
								   ["nameForm", "headerAddressType3StyleLabel"]];


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
			dojo.debug("Field '"+notEmptyFields[i][0]+"' empty but required.");
			publishable = false;
		}
	}

	for (var i in notEmptyTables) {
		if (idcObject[notEmptyTables[i][0]].length == 0) {
			dojo.html.addClass(dojo.byId(notEmptyTables[i][1]), "important");
			dojo.debug("Table '"+notEmptyTables[i][0]+"' empty but required.");
			publishable = false;
		}
	}

	// Check if the timeRef table contains valid input (both date and type must contain data)
	var timeRefData = idcObject.timeRefTable;
	if (dojo.lang.some(timeRefData, function(timeRef) {
			return (typeof(timeRef.type) == "undefined" || timeRef.type == null || dojo.string.trim(timeRef.type).length == 0
			     || typeof(timeRef.date) == "undefined" || timeRef.date == null || dojo.string.trim(timeRef.date).length == 0); })) {
		dojo.html.addClass(dojo.byId("timeRefTableLabel"), "important");		
		dojo.debug("All entries in the timeRef table must have a valid type and date.");
		publishable = false;
	}

	// Check if the availability access and useConstraints contains valid input (both fields contain data)
	if (""+idcObject.objectClass != '0') {
        var accessData = idcObject.availabilityAccessConstraints;
        if (dojo.lang.some(accessData, function(ad) {
                return (typeof(ad) == "undefined" || ad == null || dojo.string.trim(ad).length == 0); })) {
            dojo.html.addClass(dojo.byId("availabilityAccessConstraintsLabel"), "important");
            dojo.debug("All entries in the availabilityAccessConstraints table must contain data.");
            publishable = false;
        }
        var useData = idcObject.availabilityUseConstraints;
        if (dojo.lang.some(useData, function(ud) {
                return (typeof(ud) == "undefined" || ud == null || dojo.string.trim(ud).length == 0); })) {
            dojo.html.addClass(dojo.byId("availabilityUseConstraintsLabel"), "important");
            dojo.debug("All entries in the availabilityUseConstraints table must contain data.");
            publishable = false;
        }
	}

	// Check if all entries in the address table have valid reference types
	var addressData = idcObject.generalAddressTable;
	if (dojo.lang.some(addressData, function(addressRef) { return (typeof(addressRef.uuid) == "undefined" || addressRef.nameOfRelation == null || dojo.string.trim(addressRef.nameOfRelation).length == 0); })) {
		dojo.html.addClass(dojo.byId("generalAddressTableLabel"), "important");		
		dojo.debug("All entries in the address table must have valid references.");
		publishable = false;
	}

	// Get the string (from the syslist) that is used to identify auskunft entries
	var auskunftString = dojo.widget.byId("generalAddressCombobox").getDisplayValueForValue(7);

	// Check if at least one entry exists with the correct relation type
	if (dojo.lang.every(addressData, function(addressRef) { return ( dojo.string.trim(addressRef.nameOfRelation) != auskunftString); })) {
		dojo.html.addClass(dojo.byId("generalAddressTableLabel"), "important");
		dojo.debug("At least one entry has to be of type '"+auskunftString+"'.");
		publishable = false;
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

	// Check if one of the spatial references is expired
	if (dojo.lang.some(snsData, function(item) { return item.locationExpiredAt != null } )) {
		dojo.html.addClass(dojo.byId("spatialRefAdminUnitLabel"), "important");
		dojo.debug("The spatial reference table must not contain expired entries.");
		publishable = false;
	}

	// Check if one of the 'height' fields contains data. In this case, all fields are mandatory 
	if (idcObject.spatialRefAltMin || idcObject.spatialRefAltMax || idcObject.spatialRefAltMeasure || idcObject.spatialRefAltVDate) {
		// one of the fields contains data -> All fields must contain data:
		if (!idcObject.spatialRefAltMin) {
			dojo.html.addClass(dojo.byId("spatialRefAltMinLabel"), "important");
			publishable = false;
		}
		if (!idcObject.spatialRefAltMax) {
			dojo.html.addClass(dojo.byId("spatialRefAltMaxLabel"), "important");
			publishable = false;
		}
		if (!idcObject.spatialRefAltMeasure) {
			dojo.html.addClass(dojo.byId("spatialRefAltMeasureLabel"), "important");
			publishable = false;
		}
		if (!idcObject.spatialRefAltVDate) {
			dojo.html.addClass(dojo.byId("spatialRefAltVDateLabel"), "important");
			publishable = false;
		}
	}

	// If one of the 'Umweltthemen' contains an entry, both of them need to contain at least one entry
	if ((idcObject.thesaurusEnvTopicsList.length > 0 && idcObject.thesaurusEnvCatsList.length == 0)
	  ||(idcObject.thesaurusEnvTopicsList.length == 0 && idcObject.thesaurusEnvCatsList.length > 0) 
	  ||(idcObject.thesaurusEnvExtRes == true && (idcObject.thesaurusEnvTopicsList.length == 0 || idcObject.thesaurusEnvCatsList.length == 0))) {
		dojo.html.addClass(dojo.byId("thesaurusEnvironmentLabel"), "important");
		dojo.html.addClass(dojo.byId("thesaurusEnvTopicsLabel"), "important");		
		dojo.html.addClass(dojo.byId("thesaurusEnvCatsLabel"), "important");		
		dojo.debug("If one of the 'Umweltthemen' contains an entry, both of them need to contain at least one entry.");
		publishable = false;
	}

    // Check whether INSPIRE term set and check additional required fields !
    if (UtilUdk.isInspire(idcObject.thesaurusInspireTermsList)) {

	   // check if spatial reference is set !
	   if (!idcObject.ref1SpatialSystem || idcObject.ref1SpatialSystem == "") {
            dojo.html.addClass(dojo.byId("ref1SpatialSystemLabel"), "important");
            dojo.debug("Field 'ref1SpatialSystem' empty but required due to set INSPIRE theme !.");
            publishable = false;
        }

        // Check if the availabilityDataFormat table is not empty and contains valid input (both name and version must contain data)
        var dataFormatData = idcObject.availabilityDataFormatTable;
		var missingData = false; 
        if (dataFormatData.length == 0) {
            dojo.debug("Table 'availabilityDataFormatTable' empty but required.");
			missingData = true;
        } else {
            if (dojo.lang.some(dataFormatData, function(dataFormat) {
                return (UtilString.noContent(dataFormat.name) || UtilString.noContent(dataFormat.version)); })) {
                dojo.debug("All entries in the 'availabilityDataFormatTable' table must have a valid name and version.");
                missingData = true;
            }
		}
		if (missingData) {
            dojo.html.addClass(dojo.byId("availabilityDataFormatLabel"), "important");
            publishable = false;
		}

        // Check if "Datenverantwortung" address is set. "Auskunft" address already checked above, is always mandatory !
		// Datenverantwortung = entry id 2 in syslist 505
		// NOTICE: we check via String in dojo widget. addressData bean not updated correctly if directly published (without working save)

		// Get the string (from the syslist) that is used to identify Datenverantwortung entries
        var dvString = dojo.widget.byId("generalAddressCombobox").getDisplayValueForValue(2);

	    // Check if at least one entry exists with the correct relation type
// DOES NOT WORK IF DIRECTLY PUBLISHED AFTER ADDRESS WAS CORRECTLY SET (wrong data in address bean) !
//        if (dojo.lang.every(addressData, function(addressRef) { return (addressRef.typeOfRelation != 2); })) {
        if (dojo.lang.every(addressData, function(addressRef) { return ( dojo.string.trim(addressRef.nameOfRelation) != dvString); })) {
            dojo.html.addClass(dojo.byId("generalAddressTableLabel"), "important");
            dojo.debug("At least one address relation has to be of type 2 = 'Datenvarantwortung'.");
            publishable = false;
        }
    }

	// Check the required fields per object class:
	switch (""+idcObject.objectClass)
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
			for (var i in notEmptyTablesClass1) {
				if (idcObject[notEmptyTablesClass1[i][0]].length == 0) {
					dojo.html.addClass(dojo.byId(notEmptyTablesClass1[i][1]), "important");
					publishable = false;
					dojo.debug("Object class one required table '"+notEmptyTablesClass1[i][0]+"' empty.");				
				}
			}

			// Check if the conformity table contains valid input (both level and specification must contain data)
			var confData = idcObject.extraInfoConformityTable;
			if (dojo.lang.some(confData, function(conf) {
					return (typeof(conf.level) == "undefined" || conf.level == null || dojo.string.trim(conf.level).length == 0
					     || typeof(conf.specification) == "undefined" || conf.specification == null || dojo.string.trim(conf.specification).length == 0
					     || typeof(conf.date) == "undefined" || conf.date == null || dojo.string.trim(conf.date+"").length == 0); })) {
				dojo.html.addClass(dojo.byId("extraInfoConformityTableLabel"), "important");		
				dojo.debug("All entries in the conformity table must have a valid level, specification and date.");
				publishable = false;
			}

			// The Vector format area is not required for publishing an object
/*
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
*/
			break;
		case '2':
			// No additional required fields for object class 2
			for (var i in notEmptyTablesClass2) {
				if (idcObject[notEmptyTablesClass2[i][0]].length == 0) {
					dojo.html.addClass(dojo.byId(notEmptyTablesClass2[i][1]), "important");
					publishable = false;
					dojo.debug("Object class two required table '"+notEmptyTablesClass2[i][0]+"' empty.");				
				}
			}
			break;
		case '3':
			for (var i in notEmptyFieldsClass3) {
				if (!idcObject[notEmptyFieldsClass3[i][0]] || idcObject[notEmptyFieldsClass3[i][0]] == "") {
					dojo.html.addClass(dojo.byId(notEmptyFieldsClass3[i][1]), "important");
					publishable = false;
					dojo.debug("Object class three required field empty.");				
				}
			}
			for (var i in notEmptyTablesClass3) {
				if (idcObject[notEmptyTablesClass3[i][0]].length == 0) {
					dojo.html.addClass(dojo.byId(notEmptyTablesClass3[i][1]), "important");
					publishable = false;
					dojo.debug("Object class three required table '"+notEmptyTablesClass3[i][0]+"' empty.");				
				}
			}

			// Check if the conformity table contains valid input (both level and specification must contain data)
			var confData = idcObject.extraInfoConformityTable;
			if (dojo.lang.some(confData, function(conf) {
					return (typeof(conf.level) == "undefined" || conf.level == null || dojo.string.trim(conf.level).length == 0
					     || typeof(conf.specification) == "undefined" || conf.specification == null || dojo.string.trim(conf.specification).length == 0
					     || typeof(conf.date) == "undefined" || conf.date == null || dojo.string.trim(conf.date+"").length == 0); })) {
				dojo.html.addClass(dojo.byId("extraInfoConformityTableLabel"), "important");		
				dojo.debug("All entries in the conformity table must have a valid level and specification.");
				publishable = false;
			}

			break;
		case '4':
			for (var i in notEmptyTablesClass4) {
				if (idcObject[notEmptyTablesClass4[i][0]].length == 0) {
					dojo.html.addClass(dojo.byId(notEmptyTablesClass4[i][1]), "important");
					publishable = false;
					dojo.debug("Object class four required table '"+notEmptyTablesClass4[i][0]+"' empty.");				
				}
			}
			break;
		case '5':
			for (var i in notEmptyTablesClass5) {
				if (idcObject[notEmptyTablesClass5[i][0]].length == 0) {
					dojo.html.addClass(dojo.byId(notEmptyTablesClass5[i][1]), "important");
					publishable = false;
					dojo.debug("Object class five required table '"+notEmptyTablesClass5[i][0]+"' empty.");				
				}
			}
			break;
		case '6':
			for (var i in notEmptyFieldsClass6) {
				if (!idcObject[notEmptyFieldsClass6[i][0]] || idcObject[notEmptyFieldsClass6[i][0]] == "") {
					dojo.html.addClass(dojo.byId(notEmptyFieldsClass6[i][1]), "important");
					publishable = false;
					dojo.debug("Object class six required field empty.");				
				}
			}
			for (var i in notEmptyTablesClass6) {
				if (idcObject[notEmptyTablesClass6[i][0]].length == 0) {
					dojo.html.addClass(dojo.byId(notEmptyTablesClass6[i][1]), "important");
					publishable = false;
					dojo.debug("Object class six required table '"+notEmptyTablesClass6[i][0]+"' empty.");				
				}
			}

			break;
		default:
			break;
	}

	return publishable;
}


function isAddressPublishable(idcAddress) {
	var publishable = true;

	resetRequiredFields();

	for (var i in notEmptyAddressFields) {
		if (idcAddress[notEmptyAddressFields[i][0]] == null || idcAddress[notEmptyAddressFields[i][0]]+"" == "") {
			dojo.html.addClass(dojo.byId(notEmptyAddressFields[i][1]), "important");
			dojo.debug("Address field: "+notEmptyAddressFields[i][0]+" must not be empty.");
			publishable = false;
		}
	}

	for (var i in notEmptyAddressTables) {
		if (idcAddress[notEmptyAddressTables[i][0]].length == 0) {
			dojo.html.addClass(dojo.byId(notEmptyAddressTables[i][1]), "important");
			dojo.debug("Address table '"+notEmptyAddressTables[i][0]+"' must not be empty.");
			dojo.widget.byId("addressCom").applyValidation();
			publishable = false;
		}
	}

	// Check if all entries in the address table are valid
	var addressData = idcAddress.communication;
	if (dojo.lang.some(addressData, function(adr) { return (typeof(adr.medium) == "undefined" || adr.medium == null || dojo.string.trim(adr.medium).length == 0
														 || typeof(adr.value) == "undefined" || adr.value == null || dojo.string.trim(adr.value).length == 0); })) {
		dojo.html.addClass(dojo.byId("addressComLabel"), "important");		
		dojo.debug("All entries in the addressCom table must contain values.");
		publishable = false;
	}

	// Get the string (from the syslist) that is used to identify email entries
	var emailString = dojo.widget.byId("addressComType").getDisplayValueForValue(3);
	// Check if at least one entry exists with type email
	if (dojo.lang.every(addressData, function(adr) { return ( dojo.string.trim(adr.medium) != emailString); })) {
		dojo.html.addClass(dojo.byId("addressComLabel"), "important");
		dojo.debug("At least one entry has to be of type '"+emailString+"'.");
		publishable = false;
	}

	// DEPRECATED:
	// If pobox contains a value, poboxPostalCode has to contain a value as well.
	// Otherwise street and postalCode have to contain values 
	// INSPIRE Change. Removed required fields.
/*
	var pobox = dojo.string.trim(idcAddress.pobox);
	var poboxPostalCode = dojo.string.trim(idcAddress.poboxPostalCode);
	var street = dojo.string.trim(idcAddress.street);
	var postalCode = dojo.string.trim(idcAddress.postalCode);

	if (pobox.length != 0 && poboxPostalCode.length == 0) {
		dojo.html.addClass(dojo.byId("addressZipPOBoxLabel"), "important");
		dojo.debug("If pobox contains a value, poboxPostalCode has to contain a value as well.");
		publishable = false;
	} else if (pobox.length == 0 && poboxPostalCode.length != 0) {
		dojo.html.addClass(dojo.byId("addressPOBoxLabel"), "important");
		dojo.debug("If pobox contains a value, poboxPostalCode has to contain a value as well.");
		publishable = false;	
	} else if (pobox.length == 0 && poboxPostalCode.length == 0) {
		if (postalCode.length == 0) {
			dojo.html.addClass(dojo.byId("addressZipCodeLabel"), "important");
			dojo.debug("Street and postalCode have to contain values.");
			publishable = false;
		}	
		if (street.length == 0) {
			dojo.html.addClass(dojo.byId("addressStreetLabel"), "important");
			dojo.debug("Street and postalCode have to contain values.");
			publishable = false;
		}
	}
*/
	// Check the required fields per address type:
	switch (""+idcAddress.addressClass)
	{
		case '0':
			for (var i in notEmptyAddressFieldsClass0) {
				if (!idcAddress[notEmptyAddressFieldsClass0[i][0]] || idcAddress[notEmptyAddressFieldsClass0[i][0]] == "") {
					dojo.html.addClass(dojo.byId(notEmptyAddressFieldsClass0[i][1]), "important");
					publishable = false;
					dojo.debug("Address class one required field empty.");				
				}
			}
			break;
		case '1':
			for (var i in notEmptyAddressFieldsClass1) {
				if (!idcAddress[notEmptyAddressFieldsClass1[i][0]] || idcAddress[notEmptyAddressFieldsClass1[i][0]] == "") {
					dojo.html.addClass(dojo.byId(notEmptyAddressFieldsClass1[i][1]), "important");
					publishable = false;
					dojo.debug("Address class one required field empty.");				
				}
			}
			break;
		case '2':
			for (var i in notEmptyAddressFieldsClass2) {
				if (!idcAddress[notEmptyAddressFieldsClass2[i][0]] || idcAddress[notEmptyAddressFieldsClass2[i][0]] == "") {
					dojo.html.addClass(dojo.byId(notEmptyAddressFieldsClass2[i][1]), "important");
					publishable = false;
					dojo.debug("Address class one required field empty.");				
				}
			}
			break;
		case '3':
			for (var i in notEmptyAddressFieldsClass3) {
				if (!idcAddress[notEmptyAddressFieldsClass3[i][0]] || idcAddress[notEmptyAddressFieldsClass3[i][0]] == "") {
					dojo.html.addClass(dojo.byId(notEmptyAddressFieldsClass3[i][1]), "important");
					publishable = false;
					dojo.debug("Address class one required field empty.");				
				}
			}
			break;
		default:
			dojo.debug("Error in isAddressPublishable(adr). Invalid address class: "+idcAddress.addressClass);
			break;
	}

	return publishable;
}


/*
function checkValidityOfInputElements() {
	var isValid = function(widgetId) {
		var widget = dojo.widget.byId(widgetId);

		var widgetIsValid   = widget.isValid   ? widget.isValid()   : true;
		var widgetIsInRange = widget.isInRange ? widget.isInRange() : true;
		var widgetIsEmpty   = widget.isEmpty   ? widget.isEmpty()   : false;
	
		if (widget.required && widgetIsEmpty) {
			dojo.debug("Widget "+widgetId+" is required but empty.");
			return false;
		}

		if (!widgetIsEmpty && (!widgetIsValid || !widgetIsInRange)) {
			if (!widgetIsValid)
				dojo.debug("Widget "+widgetId+" contains invalid input.");
			if (!widgetIsInRange)
				dojo.debug("Widget "+widgetId+" is out of range.");
			return false;
		}
		return true;
	}

	var objectClassStr = dojo.widget.byId("objectClass").getValue().toLowerCase(); // Value is a string: "Classx" where x is the class
	var objectClass = objectClassStr.substr(5, 1);

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
*/
function checkValidityOfInputElements() {
	var isValid = function(widgetId) {
		var widget = dojo.widget.byId(widgetId);

		var widgetIsValid   = widget.isValid   ? widget.isValid()   : true;
		var widgetIsInRange = widget.isInRange ? widget.isInRange() : true;
		var widgetIsEmpty   = widget.isEmpty   ? widget.isEmpty()   : false;
	
		if (widget.required && widgetIsEmpty) {
			dojo.debug("Widget "+widgetId+" is required but empty.");
			return "INVALID_REQUIRED_BUT_EMPTY";
		}

		if (!widgetIsEmpty && (!widgetIsValid || !widgetIsInRange)) {
			if (!widgetIsValid) {
				dojo.debug("Widget "+widgetId+" contains invalid input.");
				return "INVALID_INPUT_INVALID";
			}
			if (!widgetIsInRange) {
				dojo.debug("Widget "+widgetId+" is out of range.");
				return "INVALID_INPUT_OUT_OF_RANGE";
			}
		}

		// Check if any input element contains invalid html tags
		if (widget instanceof ingrid.widget.ValidationTextbox) {
			var val = ""+widget.getValue();
			var evilTag = /<(?!b>|\/b>|i>|\/i>|u>|\/u>|p>|\/p>|br>|br\/>|br \/>|strong>|\/strong>|ul>|\/ul>|ol>|\/ol>|li>|\/li>)[^>]*>/i;
			if (val.search(evilTag) != -1) {
				return "INVALID_INPUT_HTML_TAG_INVALID";
			}
		}

		return "VALID";
	}

	var checkValidityOfWidgets = function(widgetIdList) {
		for (var i in widgetIdList) {
			var res = isValid(widgetIdList[i]);
			if (res != "VALID") {
				return res;
			}
		}
		return "VALID";
	}

	var objectClassStr = dojo.widget.byId("objectClass").getValue().toLowerCase(); // Value is a string: "Classx" where x is the class
	var objectClass = objectClassStr.substr(5, 1);

	var valid = checkValidityOfWidgets(headerUiInputElements);
	if (valid != "VALID") { return valid; }
	valid = checkValidityOfWidgets(generalUiInputElements);
	if (valid != "VALID") { return valid; }
	valid = checkValidityOfWidgets(spatialUiInputElements);
	if (valid != "VALID") { return valid; }
	valid = checkValidityOfWidgets(extraUiInputElements);
	if (valid != "VALID") { return valid; }
	valid = checkValidityOfWidgets(thesUiInputElements);
	if (valid != "VALID") { return valid; }
	valid = checkValidityOfWidgets(timeUiInputElements);
	if (valid != "VALID") { return valid; }
	valid = checkValidityOfWidgets(this[objectClassStr+"UiInputElements"]);
	if (valid != "VALID") { return valid; }

	// Object class 0 doesn't have availability information. For all other classes check availability and return
	if (objectClass != "0") {
		valid = checkValidityOfWidgets(availUiInputElements);
		if (valid != "VALID") { return valid; }
	}

	return "VALID";
}

function checkValidityOfAddressInputElements() {
/*
	var isValid = function(widgetId) {
//		dojo.debug(widgetId);
		var widget = dojo.widget.byId(widgetId);
		if (widget.isValid) {	// check if the widget has an isValid method
			if (widget.isEmpty) { // check if the widget has an isEmpty method
				if (widget.required && widget.isEmpty())
					return false;
				else
					return (widget.isEmpty() || widget.isValid());
			} else {
				return widget.isValid();
			}
		} else {
//			dojo.debug(widgetId+" has no isValid method.");
			return true;
		}
	}
*/
	dojo.debug("Checking validity of address ui elements...");

	var isValid = function(widgetId) {
		var widget = dojo.widget.byId(widgetId);

		var widgetIsValid   = widget.isValid   ? widget.isValid()   : true;
		var widgetIsInRange = widget.isInRange ? widget.isInRange() : true;
		var widgetIsEmpty   = widget.isEmpty   ? widget.isEmpty()   : false;
	
		if (widget.required && widgetIsEmpty) {
			dojo.debug("Widget "+widgetId+" is required but empty.");
			return "INVALID_REQUIRED_BUT_EMPTY";
		}

		if (!widgetIsEmpty && (!widgetIsValid || !widgetIsInRange)) {
			if (!widgetIsValid) {
				dojo.debug("Widget "+widgetId+" contains invalid input.");
				return "INVALID_INPUT_INVALID";
			}
			if (!widgetIsInRange) {
				dojo.debug("Widget "+widgetId+" is out of range.");
				return "INVALID_INPUT_OUT_OF_RANGE";
			}
		}

		// Check if any input element contains invalid html tags
		if (widget instanceof ingrid.widget.ValidationTextbox) {
			var val = ""+widget.getValue();
			var evilTag = /<(?!b>|\/b>|i>|\/i>|u>|\/u>|p>|\/p>|br>|br\/>|br \/>|strong>|\/strong>|ul>|\/ul>|ol>|\/ol>|li>|\/li>)[^>]*>/i;
			if (val.search(evilTag) != -1) {
				return "INVALID_INPUT_HTML_TAG_INVALID";
			}
		}

		return "VALID";
	}


	var checkValidityOfWidgets = function(widgetIdList) {
		for (var i in widgetIdList) {
			var res = isValid(widgetIdList[i]);
			if (res != "VALID") {
				return res;
			}
		}
		return "VALID";
	}

	var addressClass = UtilAddress.getAddressClass();

	var valid = checkValidityOfWidgets(adrUiInputElements);
	if (valid != "VALID") { return valid; }
	valid = checkValidityOfWidgets(this["adrClass"+addressClass+"UiInputElements"]);
	if (valid != "VALID") { return valid; }

	return "VALID";
}