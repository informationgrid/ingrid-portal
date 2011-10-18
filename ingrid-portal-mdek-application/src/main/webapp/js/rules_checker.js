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
	"thesaurusEnvTopics", "linksTo"];
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
    //dojo.style(dijit.byId("bShowNextError").domNode, "display", "none");
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
        showErrorButton(notPublishableIDs);
    }
    
    return publishable;

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
        //dojo.style(dijit.byId("bShowNextError").domNode, "display", "block");
    }
    
    return publishable;
    
}

function checkValidityOfInputElements(/*Array*/invalidExtInputs) {
    resetRequiredFields();
    
    var widgets = dojo.query(".rubric:not(.hide) span:not(.hide) .dijitTextBox, .rubric:not(.hide) span:not(.hide) .dijitSelect", "contentFrameBodyObject");
    widgets = widgets.concat(dojo.query(".dijitTextBox, .dijitSelect", "sectionTopObject"));
    var grids   = dojo.query("span:not(.hide) .input .ui-widget", "contentFrameBodyObject");
    
    var invalidInputs = invalidExtInputs ? invalidExtInputs : [];
    widgets.forEach(function(w) {var res = dijit.getEnclosingWidget(w).validate(); if (!res) invalidInputs.push(w.id);});
    grids.forEach(function(g) {
        var grid = UtilGrid.getTable(g.id);
        if (grid.validate) {
            var res = grid.validate();
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
        showErrorButton(invalidInputs);
        return "INVALID_INPUT_INVALID";
    } 
    return "VALID";
    
}

function showErrorButton(ids) {
	/*
    var errorButton = dijit.byId("bShowNextError");
    var unique = {};
    //get rid of duplicates
    var filteredIDs = dojo.filter(ids, function(value) {
        if (!unique[value]) {
            unique[value] = true;
            return true;
        }
        return false;
    });
    errorButton.invalidIds = filteredIDs;
    errorButton.pos = 0;
    dojo.style(errorButton.domNode, "display", "inline-block");
    */
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