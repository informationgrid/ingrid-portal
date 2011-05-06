/*
 * Functions for checking the validity of entered values.
 */

dojo.addOnLoad(function() {});

/* IDs of UI Elements for checking etc. */
var headerUiInputElements = ["objectName", "objectClass"/*, "objectOwner"*/]; // setStore of object owner is delayed! 
var generalUiInputElements = ["generalShortDesc", "generalDesc", "generalAddress"];
var dqUiTableElements = ["dq109Table", "dq110Table", "dq112Table", "dq113Table", "dq114Table", "dq115Table", "dq117Table",
    "dq120Table", "dq125Table", "dq126Table", "dq127Table"]; 
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
//var class3UiInputElements = ["ref3ServiceType", "ref3ServiceTypeTable", "ref3ServiceVersion", "ref3SystemEnv", "ref3History", "ref3BaseDataText",
//                            "ref3BaseDataLink", "ref3Explanation", "ref3Scale", "ref3Operation", "extraInfoConformityTable"];
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
                            ["ref1DataSet", "ref1DataSetLabel"],
                            ["ref1SpatialSystem", "ref1SpatialSystemLabel"],
                            ["availabilityDataFormatInspire", "availabilityDataFormatInspireLabel"],
                            ["extraInfoCharSetDataCode", "extraInfoCharSetDataLabel"]]; 
var notEmptyFieldsClass3 = [["ref3ServiceType", "ref3ServiceTypeLabel"],
                            ["ref1SpatialSystem", "ref1SpatialSystemLabel"]];

var notEmptyFieldsClass6 = [["ref6ServiceType", "ref6ServiceTypeLabel"]];

var notEmptyTables = [["generalAddressTable", "generalAddressTableLabel"],
					  ["timeRefTable", "timeRefTableLabel"]];

// TODO Add class 2, 4, 5 to isObjectPublishable when needed
var notEmptyTablesClass1 = [["availabilityAccessConstraints", "availabilityAccessConstraintsLabel"],
                            ["availabilityUseConstraints", "availabilityUseConstraintsLabel"],
                            ["extraInfoConformityTable", "extraInfoConformityTableLabel"],
                            ["thesaurusInspireTermsList", "thesaurusInspireLabel"],
                            ["thesaurusTopicsList", "thesaurusTopicsLabel"]];
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
    // get all labels within object- and address form and remove class, which makes them red
    dojo.query(".important", "hierarchyContent").removeClass("important");
}

function setErrorLabel(id){
    if (dojo.indexOf(["objectName", "objectClass", "objectOwner"], id) != -1) {
        dojo.addClass(id + "Label", "important");
        return;
    }
    
    var domWidget = dojo.byId(id);
    while (domWidget) {
        if (dojo.hasClass(domWidget, "outer")) {
            dojo.addClass(domWidget, "important");
            return;
        }
        domWidget = domWidget.parentNode;
    }
}

function isObjectPublishable() {
    // get all required input elements and set the required field
    // after validation of the form reset required field
    // reset must be done when saved normally also or object is reloaded!
    // dojo.forEach(dojo.query(".required .input .dijit"), function(e) { console.debug(e.widgetId); dijit.byId(e.getAttribute("widgetid")).set("required", false); })
    var publishable       = true;
    var notPublishableIDs = [];
    
    resetRequiredFields();
    
    // check first general validity
    checkValidityOfInputElements(notPublishableIDs);
    
    var widgets = dojo.query(".rubric:not(.hide) .required .dijitTextBox, .rubric:not(.hide) .required .dijitSelect", "contentFrameBodyObject").map(function(item) {return item.getAttribute("widgetid");});
    widgets = widgets.concat(dojo.query(".dijitTextBox, .dijitSelect", "sectionTopObject").map(function(item) {return item.getAttribute("widgetid");}));
    var grids = dojo.query(".rubric:not(.hide) .required .ui-widget:not(.noValidate)", "contentFrameBodyObject").map(function(item) {return item.id;});
    
    dojo.forEach(widgets, function(w) {
        if (dojo.trim(dijit.byId(w).get("displayedValue")).length == 0) {
            notPublishableIDs.push(w);
        };
    });
    dojo.forEach(grids, function(g) {
        if (UtilGrid.getTableData(g).length == 0) {
            notPublishableIDs.push(g);
        };
    });
    
    dojo.publish("/onBeforeObjectPublish", [notPublishableIDs]);
    
    console.debug("not publishable IDs:");
    console.debug(notPublishableIDs);
    if (notPublishableIDs.length > 0) {
        dojo.forEach(notPublishableIDs, function(id){
            setErrorLabel(id);
        });
        publishable = false;
    }
    
    return publishable;

/*	
    // check if the errorClass was set in one of the input elements
    if (dojo.query(".importantBackground", "contentFrameBodyObject").length > 0) {
        console.debug("One or more inputs are invalid!");
        publishable = false;
    }

	for (var i in notEmptyFields) {
		if (!idcObject[notEmptyFields[i][0]] || idcObject[notEmptyFields[i][0]] == "") {
			dojo.addClass(dojo.byId(notEmptyFields[i][1]), "important");
			console.debug("Field '"+notEmptyFields[i][0]+"' empty but required.");
			publishable = false;
		}
	}

	for (var i in notEmptyTables) {
		if (idcObject[notEmptyTables[i][0]].length == 0) {
			dojo.addClass(dojo.byId(notEmptyTables[i][1]), "important");
			console.debug("Table '"+notEmptyTables[i][0]+"' empty but required.");
			publishable = false;
		}
	}

	// Check if the timeRef table contains valid input (both date and type must contain data)
	var timeRefData = idcObject.timeRefTable;
	if (dojo.some(timeRefData, function(timeRef) {
			return (typeof(timeRef.type) == "undefined" || timeRef.type == null || dojo.trim(timeRef.type+"").length == 0
			     || typeof(timeRef.date) == "undefined" || timeRef.date == null || dojo.trim(timeRef.date+"").length == 0); })) {
		dojo.addClass(dojo.byId("timeRefTableLabel"), "important");		
		console.debug("All entries in the timeRef table must have a valid type and date.");
		publishable = false;
	}

    // Check if the availability access and useConstraints contains valid input (both fields contain data)
    if (""+idcObject.objectClass != '0') {
        var accessData = idcObject.availabilityAccessConstraints;
        if (dojo.some(accessData, function(ad) {
                return (typeof(ad) == "undefined" || ad == null || dojo.trim(ad+"").length == 0); })) {
            dojo.addClass(dojo.byId("availabilityAccessConstraintsLabel"), "important");
            console.debug("All entries in the availabilityAccessConstraints table must contain data.");
            publishable = false;
        }
        var useData = idcObject.availabilityUseConstraints;
        if (dojo.some(useData, function(ud) {
                return (typeof(ud) == "undefined" || ud == null || dojo.trim(ud).length == 0); })) {
            dojo.addClass(dojo.byId("availabilityUseConstraintsLabel"), "important");
            console.debug("All entries in the availabilityUseConstraints table must contain data.");
            publishable = false;
        }
    }

	// Check if all entries in the address table have valid reference types
	var addressData = idcObject.generalAddressTable;
	if (dojo.some(addressData, function(addressRef) { return (typeof(addressRef.uuid) == "undefined" || addressRef.nameOfRelation == null || dojo.trim(addressRef.nameOfRelation).length == 0); })) {
		dojo.addClass(dojo.byId("generalAddressTableLabel"), "important");		
		console.debug("All entries in the address table must have valid references.");
		publishable = false;
	}

	// Get the string (from the syslist) that is used to identify auskunft entries
	var auskunftString = UtilSyslist.getSyslistEntryName(505, 7);//dijit.byId("generalAddressCombobox").getDisplayValueForValue(7);

	// Check if at least one entry exists with the correct relation type
	if (dojo.every(addressData, function(addressRef) { return ( dojo.trim(addressRef.nameOfRelation) != auskunftString); })) {
		dojo.addClass(dojo.byId("generalAddressTableLabel"), "important");
		console.debug("At least one entry has to be of type '"+auskunftString+"'.");
		publishable = false;
	}


	// Check if one of the 'Raumbezug' tables has an entry with a bounding box
	var snsData = idcObject.spatialRefAdminUnitTable;
	var freeData = idcObject.spatialRefLocationTable;
	var hasBB = function(item) {return (item.longitude1 && item.longitude2 && item.latitude1 && item.latitude2);};
	if ( !(dojo.some(snsData, hasBB) || dojo.some(freeData, hasBB)) ) {
		dojo.addClass(dojo.byId("spatialRefAdminUnitLabel"), "important");
		dojo.addClass(dojo.byId("spatialRefLocationLabel"), "important");
		console.debug("At least one 'spatial' table has to contain an entry with a BB.");
		publishable = false;
	}

	// Check if one of the spatial references is expired
	if (dojo.some(snsData, function(item) { return item.locationExpiredAt != null } )) {
		dojo.addClass(dojo.byId("spatialRefAdminUnitLabel"), "important");
		console.debug("The spatial reference table must not contain expired entries.");
		publishable = false;
	}

	// Check if one of the 'height' fields contains data. In this case, all fields are mandatory 
	if (idcObject.spatialRefAltMin || idcObject.spatialRefAltMax || idcObject.spatialRefAltMeasure || idcObject.spatialRefAltVDate) {
		// one of the fields contains data -> All fields must contain data:
		if (!idcObject.spatialRefAltMin) {
			dojo.addClass(dojo.byId("spatialRefAltMinLabel"), "important");
			publishable = false;
		}
		if (!idcObject.spatialRefAltMax) {
			dojo.addClass(dojo.byId("spatialRefAltMaxLabel"), "important");
			publishable = false;
		}
		if (!idcObject.spatialRefAltMeasure) {
			dojo.addClass(dojo.byId("spatialRefAltMeasureLabel"), "important");
			publishable = false;
		}
		if (!idcObject.spatialRefAltVDate) {
			dojo.addClass(dojo.byId("spatialRefAltVDateLabel"), "important");
			publishable = false;
		}
	}

	// If one of the 'Umweltthemen' contains an entry, both of them need to contain at least one entry
	if ((idcObject.thesaurusEnvTopicsList.length > 0 && idcObject.thesaurusEnvCatsList.length == 0)
	  ||(idcObject.thesaurusEnvTopicsList.length == 0 && idcObject.thesaurusEnvCatsList.length > 0) 
	  ||(idcObject.thesaurusEnvExtRes == true && (idcObject.thesaurusEnvTopicsList.length == 0 || idcObject.thesaurusEnvCatsList.length == 0))) {
		dojo.addClass(dojo.byId("thesaurusEnvironmentLabel"), "important");
		dojo.addClass(dojo.byId("thesaurusEnvTopicsLabel"), "important");		
		dojo.addClass(dojo.byId("thesaurusEnvCatsLabel"), "important");		
		console.debug("If one of the 'Umweltthemen' contains an entry, both of them need to contain at least one entry.");
		publishable = false;
	}
    
    
    // Check whether INSPIRE term set and check additional required fields !
    // ONLY IF INSPIRE TERM REQUIRED ! ("Geo-Information/Karte" class 1, "Geodatendienst" class 3)
    var isInspireClass = false; 
    if ((""+idcObject.objectClass == '1') || (""+idcObject.objectClass == '3')) {
       isInspireClass = true;
    }
    if (isInspireClass == true) {

        // Required ONLY if INSPIRE Theme selected
        if (UtilUdk.isInspire(idcObject.thesaurusInspireTermsList)) {
            // Check if "Datenverantwortung" address is set. "Auskunft" address already checked above, is always mandatory !
            // Datenverantwortung = entry id 2 in syslist 505
            // NOTICE: we check via String in dojo widget. addressData bean not updated correctly if directly published (without working save)
    
            // Get the string (from the syslist) that is used to identify Datenverantwortung entries
            var dvString = UtilSyslist.getSyslistEntryName(505, 2);
    
            // Check if at least one entry exists with the correct relation type
    // DOES NOT WORK IF DIRECTLY PUBLISHED AFTER ADDRESS WAS CORRECTLY SET (wrong data in address bean) !
    //        if (dojo.lang.every(addressData, function(addressRef) { return (addressRef.typeOfRelation != 2); })) {
            if (dojo.every(addressData, function(addressRef) { return ( dojo.trim(addressRef.nameOfRelation) != dvString); })) {
                dojo.addClass(dojo.byId("generalAddressTableLabel"), "important");
                console.debug("At least one address relation has to be of type 2 = 'Datenvarantwortung'.");
                publishable = false;
            }
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
					dojo.addClass(dojo.byId(notEmptyFieldsClass1[i][1]), "important");
					publishable = false;
					console.debug("Object class one required field empty.");				
				}
			}
			for (var i in notEmptyTablesClass1) {
				if (idcObject[notEmptyTablesClass1[i][0]].length == 0) {
					dojo.addClass(dojo.byId(notEmptyTablesClass1[i][1]), "important");
					publishable = false;
					console.debug("Object class one required table '"+notEmptyTablesClass1[i][0]+"' empty.");				
				}
			}

			// Check if the conformity table contains valid input (both level and specification must contain data)
			var confData = idcObject.extraInfoConformityTable;
			if (dojo.some(confData, function(conf) {
					return (typeof(conf.level) == "undefined" || conf.level == null || dojo.trim(conf.level+"").length == 0
					     || typeof(conf.specification) == "undefined" || conf.specification == null || dojo.trim(conf.specification).length == 0
					     || typeof(conf.date) == "undefined" || conf.date == null || dojo.trim(conf.date+"").length == 0); })) {
				dojo.addClass(dojo.byId("extraInfoConformityTableLabel"), "important");		
				console.debug("All entries in the conformity table must have a valid level, specification and date.");
				publishable = false;
			}

            // Check dq rows whether complete !
            dojo.forEach(dqUiTableElements, function(dqTableId) {
                var dqRows = idcObject[dqTableId]
                for (var i in dqRows) {
                    var dqRow = dqRows[i];
                    if (dqRow.nameOfMeasure || dqRow.resultValue || dqRow.measureDescription) {
                        if (!dqRow.nameOfMeasure || !dqRow.resultValue) {
                            dojo.addClass(dijit.byId(dqTableId).domNode.parentNode.parentNode, "important");
                            publishable = false;
                            dojo.debug("NameOfMeasure + ResultValue needs to be filled.");
                        }
                    }
                }
            });
			break;
		case '2':
			// No additional required fields for object class 2
			for (var i in notEmptyTablesClass2) {
				if (idcObject[notEmptyTablesClass2[i][0]].length == 0) {
					dojo.addClass(dojo.byId(notEmptyTablesClass2[i][1]), "important");
					publishable = false;
					console.debug("Object class two required table '"+notEmptyTablesClass2[i][0]+"' empty.");				
				}
			}
			break;
		case '3':
			for (var i in notEmptyFieldsClass3) {
				if (!idcObject[notEmptyFieldsClass3[i][0]] || idcObject[notEmptyFieldsClass3[i][0]] == "") {
					dojo.addClass(dojo.byId(notEmptyFieldsClass3[i][1]), "important");
					publishable = false;
					console.debug("Object class three required field empty.");				
				}
			}
			for (var i in notEmptyTablesClass3) {
				if (idcObject[notEmptyTablesClass3[i][0]].length == 0) {
					dojo.addClass(dojo.byId(notEmptyTablesClass3[i][1]), "important");
					publishable = false;
					console.debug("Object class three required table '"+notEmptyTablesClass3[i][0]+"' empty.");				
				}
			}

			// Check if the conformity table contains valid input (both level and specification must contain data)
			var confData = idcObject.extraInfoConformityTable;
			if (dojo.some(confData, function(conf) {
					return (typeof(conf.level) == "undefined" || conf.level == null || dojo.trim(conf.level+"").length == 0
					     || typeof(conf.specification) == "undefined" || conf.specification == null || dojo.trim(conf.specification).length == 0
					     || typeof(conf.date) == "undefined" || conf.date == null || dojo.trim(conf.date+"").length == 0); })) {
				dojo.addClass(dojo.byId("extraInfoConformityTableLabel"), "important");		
				console.debug("All entries in the conformity table must have a valid level and specification.");
				publishable = false;
			}

			break;
		case '4':
			for (var i in notEmptyTablesClass4) {
				if (idcObject[notEmptyTablesClass4[i][0]].length == 0) {
					dojo.addClass(dojo.byId(notEmptyTablesClass4[i][1]), "important");
					publishable = false;
					console.debug("Object class four required table '"+notEmptyTablesClass4[i][0]+"' empty.");				
				}
			}
			break;
		case '5':
			for (var i in notEmptyTablesClass5) {
				if (idcObject[notEmptyTablesClass5[i][0]].length == 0) {
					dojo.addClass(dojo.byId(notEmptyTablesClass5[i][1]), "important");
					publishable = false;
					console.debug("Object class five required table '"+notEmptyTablesClass5[i][0]+"' empty.");				
				}
			}
			break;
        case '6':
            for (var i in notEmptyFieldsClass6) {
                if (!idcObject[notEmptyFieldsClass6[i][0]] || idcObject[notEmptyFieldsClass6[i][0]] == "") {
                    dojo.addClass(dojo.byId(notEmptyFieldsClass6[i][1]), "important");
                    publishable = false;
                    console.debug("Object class six required field empty.");               
                }
            }
            for (var i in notEmptyTablesClass6) {
                if (idcObject[notEmptyTablesClass6[i][0]].length == 0) {
                    dojo.addClass(dojo.byId(notEmptyTablesClass6[i][1]), "important");
                    publishable = false;
                    console.debug("Object class six required table '"+notEmptyTablesClass6[i][0]+"' empty.");              
                }
            }

            break;
		default:
			break;
	}

	return publishable;*/
}


function isAddressPublishable(idcAddress){
    var publishable       = true;
    var notPublishableIDs = [];
    
    resetRequiredFields();
    
    var widgets = dojo.query(".rubric:not(.hide) .required .dijitTextBox, .rubric:not(.hide) .required .dijitSelect", "contentFrameBodyAddress").map(function(item) {return item.getAttribute("widgetid");});
    widgets = widgets.concat(dojo.query(".dijitTextBox, .dijitSelect", "sectionTopAddress").map(function(item) {return item.getAttribute("widgetid");}));
    var grids = dojo.query(".rubric:not(.hide) .required .ui-widget", "contentFrameBodyAddress").map(function(item) {return item.id;});
    
    dojo.forEach(widgets, function(w) {
        if (dojo.trim(dijit.byId(w).get("displayedValue")).length == 0) {
            notPublishableIDs.push(w);
        };
    });
    dojo.forEach(grids, function(g) {
        if (UtilGrid.getTableData(g).length == 0) {
            notPublishableIDs.push(g);
        };
    });
    
    dojo.publish("/onBeforeAddressPublish", [notPublishableIDs]);
    
    console.debug("not publishable IDs:");
    console.debug(notPublishableIDs);
    if (notPublishableIDs.length > 0) {
        dojo.forEach(notPublishableIDs, function(id){
            setErrorLabel(id);
        });
        publishable = false;
    }
    
    return publishable;
    
    
    /*
    // check if the errorClass was set in one of the input elements
    if (dojo.query(".importantBackground", "contentFrameBodyAddress").length > 0) {
        console.debug("One or more inputs are invalid!");
        publishable = false;
    }

	for (var i in notEmptyAddressFields) {
		if (idcAddress[notEmptyAddressFields[i][0]] == null || idcAddress[notEmptyAddressFields[i][0]]+"" == "") {
			dojo.addClass(dojo.byId(notEmptyAddressFields[i][1]), "important");
			console.debug("Address field: "+notEmptyAddressFields[i][0]+" must not be empty.");
			publishable = false;
		}
	}

	for (var i in notEmptyAddressTables) {
		if (idcAddress[notEmptyAddressTables[i][0]].length == 0) {
			dojo.addClass(dojo.byId(notEmptyAddressTables[i][1]), "important");
			console.debug("Address table '"+notEmptyAddressTables[i][0]+"' must not be empty.");
			//dijit.byId("addressCom").applyValidation();
			publishable = false;
		}
	}

	// Check if all entries in the address table are valid
	var addressData = idcAddress.communication;
	if (dojo.some(addressData, function(adr) { return (typeof(adr.medium) == "undefined" || adr.medium == null || dojo.trim(adr.medium).length == 0
														 || typeof(adr.value) == "undefined" || adr.value == null || dojo.trim(adr.value).length == 0); })) {
		dojo.addClass(dojo.byId("addressComLabel"), "important");		
		console.debug("All entries in the addressCom table must contain values.");
		publishable = false;
	}

	// Get the string (from the syslist) that is used to identify email entries
	//var emailString = dijit.byId("addressComType").getDisplayValueForValue(3);
	var emailString = UtilSyslist.getSyslistEntryName(4430, 3);
	// Check if at least one entry exists with type email
	if (dojo.every(addressData, function(adr) { return ( dojo.trim(adr.medium) != emailString); })) {
		dojo.addClass(dojo.byId("addressComLabel"), "important");
		console.debug("At least one entry has to be of type '"+emailString+"'.");
		publishable = false;
	}


	// Check the required fields per address type:
	switch (""+idcAddress.addressClass)
	{
		case '0':
			for (var i in notEmptyAddressFieldsClass0) {
				if (!idcAddress[notEmptyAddressFieldsClass0[i][0]] || idcAddress[notEmptyAddressFieldsClass0[i][0]] == "") {
					dojo.addClass(dojo.byId(notEmptyAddressFieldsClass0[i][1]), "important");
					publishable = false;
					console.debug("Address class one required field empty.");				
				}
			}
			break;
		case '1':
			for (var i in notEmptyAddressFieldsClass1) {
				if (!idcAddress[notEmptyAddressFieldsClass1[i][0]] || idcAddress[notEmptyAddressFieldsClass1[i][0]] == "") {
					dojo.addClass(dojo.byId(notEmptyAddressFieldsClass1[i][1]), "important");
					publishable = false;
					console.debug("Address class one required field empty.");				
				}
			}
			break;
		case '2':
			for (var i in notEmptyAddressFieldsClass2) {
				if (!idcAddress[notEmptyAddressFieldsClass2[i][0]] || idcAddress[notEmptyAddressFieldsClass2[i][0]] == "") {
					dojo.addClass(dojo.byId(notEmptyAddressFieldsClass2[i][1]), "important");
					publishable = false;
					console.debug("Address class one required field empty.");				
				}
			}
			break;
		case '3':
			for (var i in notEmptyAddressFieldsClass3) {
				if (!idcAddress[notEmptyAddressFieldsClass3[i][0]] || idcAddress[notEmptyAddressFieldsClass3[i][0]] == "") {
					dojo.addClass(dojo.byId(notEmptyAddressFieldsClass3[i][1]), "important");
					publishable = false;
					console.debug("Address class one required field empty.");				
				}
			}
			break;
		default:
			console.debug("Error in isAddressPublishable(adr). Invalid address class: "+idcAddress.addressClass);
			break;
	}

	return publishable;
	*/
}

function checkValidityOfInputElements(/*Array*/invalidExtInputs) {
    var widgets = dojo.query("span:not(.hide) .input .dijitTextBox, span:not(.hide) .input .dijitSelect", "contentFrameBodyObject");
    widgets = widgets.concat(dojo.query(".dijitTextBox, .dijitSelect", "sectionTopObject"));
    var grids   = dojo.query("span:not(.hide) .input .ui-widget", "contentFrameBodyObject");
    
    var invalidInputs = invalidExtInputs ? invalidExtInputs : [];
    widgets.forEach(function(w) {var res = dijit.getEnclosingWidget(w).validate(); if (!res) invalidInputs.push(w.id);});
    //var invalidGrids = [];
    grids.forEach(function(g) {
        if (UtilGrid.getTable(g.id).validate) {
            var res = UtilGrid.getTable(g.id).validate();
            if (!res) invalidInputs.push(g.id);
        }
    });
    
    //invalidExtInputs = invalidInputs.concat(invalidGrids);
    if (invalidInputs.length > 0) {
        console.debug("invalid fields:");
        console.debug(invalidInputs);
        dojo.forEach(invalidInputs, function(id){
            setErrorLabel(id);
        });
        return "INVALID_INPUT_INVALID";
    } 
    return "VALID";
    
	/*var isValid = function(widgetId) {
		var widget = dijit.byId(widgetId);
        
        // check SlickGrid
        if (widget == undefined) {
            widget = gridManager[widgetId];
            //return true;
        }

		// check if function isValid() exists, if not then also check if a
		// validator function exists (for textareas!)
		var widgetIsValid   = widget.isValid   ? widget.isValid()   : widget.validator ? widget.validator() : true;
		var widgetIsInRange = widget.isInRange ? widget.isInRange() : true;
		var widgetIsEmpty   = widget.isEmpty   ? widget.isEmpty()   : false;
	
		// fix for NumberTextBoxes who contain NaN
		if (!widgetIsInRange) {
			if (widget.valueNode && widget.valueNode.value == "")
				widgetIsInRange = true;
		}
		
		if (widget.required && widgetIsEmpty) {
			console.debug("Widget "+widgetId+" is required but empty.");
			return "INVALID_REQUIRED_BUT_EMPTY";
		}

		if (!widgetIsEmpty && (!widgetIsValid || !widgetIsInRange)) {
			if (!widgetIsValid) {
				console.debug("Widget "+widgetId+" contains invalid input.");
				return "INVALID_INPUT_INVALID";
			}
			if (!widgetIsInRange) {
				console.debug("Widget "+widgetId+" is out of range.");
				return "INVALID_INPUT_OUT_OF_RANGE";
			}
		}

		// Check if any input element contains invalid html tags
		if (widget instanceof dijit.form.ValidationTextBox) {
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

	var objectClassStr = dijit.byId("objectClass").getValue().toLowerCase(); // Value is a string: "Classx" where x is the class
	var objectClass = objectClassStr.substr(5, 1);
	
	// check if the errorClass was set in one of the input elements
	if (dojo.query(".importantBackground", "contentFrameBodyObject").length > 0)
		return "INVALID_INPUT_INVALID";
	

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
    
    // Only Object class 1 has DQ Tables.
    if (objectClass == "1") {
        valid = checkValidityOfWidgets(dqUiTableElements);
        if (valid != "VALID") { return valid; }
    }

	return "VALID";*/
}

function checkValidityOfAddressInputElements() {
	console.debug("Checking validity of address ui elements...");

    var widgets = dojo.query(".rubric:not(.hide) .input .dijitTextBox, .input .dijitSelect", "contentFrameBodyAddress");
    widgets = widgets.concat(dojo.query(".dijitTextBox, .dijitSelect", "sectionTopAddress"));
    var grids   = dojo.query(".rubric:not(.hide) .input .ui-widget", "contentFrameBodyAddress");
    
    var invalidInputs = [];
    widgets.forEach(function(w) {var res = dijit.getEnclosingWidget(w).validate(); if (!res) invalidInputs.push(w.id);});
    var invalidGrids = [];
    grids.forEach(function(g) {
        if (UtilGrid.getTable(g.id).validate) {
            var res = UtilGrid.getTable(g.id).validate();
            if (!res) invalidGrids.push(g.id);
        }
    });
    
    invalidInputs = invalidInputs.concat(invalidGrids);
    if (invalidInputs.length > 0) {
        console.debug("invalid fields:");
        console.debug(invalidInputs);
        return "INVALID_INPUT_INVALID";
    } 
    return "VALID";



	var isValid = function(widgetId) {
		var widget = dijit.byId(widgetId);
        
        // check SlickGrid
        if (widget == undefined) {
            widget = gridManager[widgetId];
            //return true;
        }

		// check if function isValid() exists, if not then also check if a
		// validator function exists (for textareas!)
		var widgetIsValid   = widget.isValid   ? widget.isValid()   : widget.validator ? widget.validator() : true;
		var widgetIsInRange = widget.isInRange ? widget.isInRange() : true;
		var widgetIsEmpty   = widget.isEmpty   ? widget.isEmpty()   : false;
	
		if (widget.required && widgetIsEmpty) {
			console.debug("Widget "+widgetId+" is required but empty.");
			return "INVALID_REQUIRED_BUT_EMPTY";
		}

		if (!widgetIsEmpty && (!widgetIsValid || !widgetIsInRange)) {
			if (!widgetIsValid) {
				console.debug("Widget "+widgetId+" contains invalid input.");
				return "INVALID_INPUT_INVALID";
			}
			if (!widgetIsInRange) {
				console.debug("Widget "+widgetId+" is out of range.");
				return "INVALID_INPUT_OUT_OF_RANGE";
			}
		}

		// Check if any input element contains invalid html tags
		if (widget instanceof dijit.form.ValidationTextBox) {
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

	// check if the errorClass was set in one of the input elements
	//if (dojo.query(".importantBackground", "contentFrameBodyAddress").length > 0)
	//	return "INVALID_INPUT_INVALID";

	var addressClass = UtilAddress.getAddressClass();

	var valid = checkValidityOfWidgets(adrUiInputElements);
	if (valid != "VALID") { return valid; }
	valid = checkValidityOfWidgets(this["adrClass"+addressClass+"UiInputElements"]);
	if (valid != "VALID") { return valid; }

	return "VALID";
}