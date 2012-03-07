<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="dialog.popup.object.wizard.link" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
var scriptScope = this;

var thisDialog = dijit.byId("subPageDialog");
if (thisDialog == undefined)
    thisDialog = dijit.byId("pageDialog");

hideResults();

//dojo.addOnLoad(function() {
    /*if (dojo.isIE) {
        console.debug("IE!");
        init();
    } else {*/
        dojo.connect(thisDialog, "onLoad", function(){
            init();
        });
    //}
//});

createDOMElements = function() {
	var assistantDescriptorTableStructure = [
		{field: 'selection',name: 'selection',width: '23px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
		{field: 'label',name: 'label',width: '580px'},
		{field: 'source',name: 'source',width: 92-scrollBarWidth+'px'}
	];
    createDataGrid("assistantDescriptorTable", null, assistantDescriptorTableStructure, null);
	
	var assistantSpatialRefTableStructure = [
		{field: 'selection',name: '&nbsp;',width: '30px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
		{field: 'label',name: "<fmt:message key='dialog.wizard.create.spatial.name' />",width: '300px'},
		{field: 'longitude1',name: "<fmt:message key='dialog.wizard.create.spatial.longitude1' />",width: '90px'},
		{field: 'latitude1',name: "<fmt:message key='dialog.wizard.create.spatial.latitude1' />",width: '90px'},
		{field: 'longitude2',name: "<fmt:message key='dialog.wizard.create.spatial.longitude2' />",width: '90px'},
		{field: 'latitude2',name: "<fmt:message key='dialog.wizard.create.spatial.latitude2' />",width: 95-scrollBarWidth+'px'}
	];
    createDataGrid("assistantSpatialRefTable", null, assistantSpatialRefTableStructure, null);
	
	var assistantTimeRefTableStructure = [
		{field: 'selection',name: "<fmt:message key='dialog.wizard.create.time.select' />",width: '60px'},
		{field: 'name',name: "<fmt:message key='dialog.wizard.create.time.description' />",width: '346px'},
		{field: 'date1',name: "<fmt:message key='dialog.wizard.create.time.firstDate' />",width: '100px', formatter: DateCellFormatter},
		{field: 'date2',name: "<fmt:message key='dialog.wizard.create.time.secondDate' />",width: '100px', formatter: DateCellFormatter},
		{field: 'type',name: "<fmt:message key='dialog.wizard.create.time.type' />",width: 89-scrollBarWidth+'px'}
	];
    createDataGrid("assistantTimeRefTable", null, assistantTimeRefTableStructure, null);
}

init = (function() {
    console.debug("init");
    createDOMElements();
    
    console.debug("created");
	dijit.byId("assistantURL").setValue("http://");
	dijit.byId("assistantNumWords").setValue(1000);
	dijit.byId("assistantHtmlContentNumWords").setValue(100);

    // Pressing 'enter' on the input field is equal to a button click
    dojo.connect(dijit.byId("assistantURL").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == dojo.keys.ENTER) {
                scriptScope.startSearch();
            }
	});
    // Pressing 'enter' on the input field is equal to a button click
    dojo.connect(dijit.byId("assistantNumWords").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == dojo.keys.ENTER) {
                scriptScope.startSearch();
            }
	});
    // Pressing 'enter' on the input field is equal to a button click
    dojo.connect(dijit.byId("assistantHtmlContentNumWords").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == dojo.keys.ENTER) {
                scriptScope.startSearch();
            }
	});


	var descriptorCheckbox = dijit.byId("assistantDescriptorTableCheckbox");
	dojo.connect(descriptorCheckbox, "onClick", function() {
		// get checkbox value
		var value = dijit.byId("assistantDescriptorTableCheckbox").checked;
		// get data from the topic store
		var data = UtilGrid.getTableData("assistantDescriptorTable");
        dojo.forEach(data, function(d) {
           d.selection = value ? 1 : 0; 
        });
        UtilGrid.getTable("assistantDescriptorTable").invalidate();
		//for (var i in data) {
			// check / uncheck all select boxes
		//		dojo.byId("objectWiz_"+getThesaurusId(data[i])).checked = value;
		//}
	});
	
	var spatialCheckbox = dijit.byId("assistantSpatialRefTableCheckbox");
	dojo.connect(spatialCheckbox, "onClick", function() {
		// get checkbox value
		var value = dijit.byId("assistantSpatialRefTableCheckbox").checked;
		// get data drom the spatial store
		var data = UtilGrid.getTableData("assistantSpatialRefTable");
        dojo.forEach(data, function(d) {
           d.selection = value ? 1 : 0; 
        });
        UtilGrid.getTable("assistantSpatialRefTable").invalidate();
		//for (var i in data) {
			// check / uncheck all select boxes
		//	dojo.byId("objectWiz_"+data[i].topicId).checked = value;
		//}
	});
});

function resetInputFields() {
	dijit.byId("assistantHtmlTitleCheckbox").setValue(false);
	dijit.byId("assistantHtmlTitle").setValue("");
	dijit.byId("assistantDescriptionCheckbox").setValue(false);
	dijit.byId("assistantDescription").setValue("");
	UtilGrid.setTableData("assistantDescriptorTable", []);
	dijit.byId("assistantDescriptorTableCheckbox").setValue(false);
	UtilGrid.setTableData("assistantSpatialRefTable", []);
	dijit.byId("assistantSpatialRefTableCheckbox").setValue(false);
	UtilGrid.setTableData("assistantTimeRefTable", []);
}

function hideResults() {
	dojo.style("resultContainer", "display", "none");
	dojo.style("resultButtonContainer", "display", "none");
}

function showResults(showDescription, showHtmlContent) {
    console.debug("showResults");
	dojo.style("resultContainer", "display", "block");
	dojo.style("resultButtonContainer", "display", "block");
	
	if (showDescription) {
		dojo.style("assistantDescriptionContainer", "display", "block");
	} else {
		dojo.style("assistantDescriptionContainer", "display", "none");
	}

	if (showHtmlContent) {
		dojo.style("assistantHtmlContentContainer", "display", "block");
	} else {
		dojo.style("assistantHtmlContentContainer", "display", "none");
	}
}

// Updates the input fields with values from the given topic map
function updateInputFields(topicMap) {
	var indexedDocument = topicMap.indexedDocument;
	var thesaTopicList = topicMap.thesaTopics;
	var locationTopicList = topicMap.locationTopics;
	var eventTopicList = topicMap.eventTopics;
	UtilList.addSNSTopicLabels(thesaTopicList);

	dijit.byId("assistantDescriptionCheckbox").setValue(false);
	dijit.byId("assistantHtmlContentCheckbox").setValue(false);

	// Description
	if (indexedDocument.description != null)
		dijit.byId("assistantDescription").setValue(dojo.trim(""+indexedDocument.description));

	//-- test value
	  //thesaTopicList[0].inspireList[0] = "Adressen";
	  //thesaTopicList[0].inspireList[1] = "Medien der Umwelt";
	//--
	
	// // Inspire Topics
	var inspireTopics = getInspireTopics(thesaTopicList);
	console.debug(inspireTopics.length + " inspire topics found");
	thesaTopicList = thesaTopicList.concat(inspireTopics);
    console.debug("go on");
	// Thesaurus Topics
	if (thesaTopicList != null) {
		//UtilList.addTableIndices(thesaTopicList);
		for (var i in thesaTopicList) {
			// Add checkbox to entry
			thesaTopicList[i].selection = 0;//"<input type='checkbox' id='objectWiz_"+getThesaurusId(thesaTopicList[i])+"'>";
		}
		UtilStore.updateWriteStore("assistantDescriptorTable", thesaTopicList);
	}

	// Location Topics
	if (locationTopicList != null) {
		//UtilList.addTableIndices(locationTopicList);
		UtilList.addSNSLocationLabels(locationTopicList);
		for (var i in locationTopicList) {
			// Add checkbox to entry
			locationTopicList[i].selection = 0;//"<input type='checkbox' id='objectWiz_"+locationTopicList[i].topicId+"'>";
			// Prepare bb for display in the table
			if (locationTopicList[i].boundingBox) {
				locationTopicList[i].longitude1 = locationTopicList[i].boundingBox[0];
				locationTopicList[i].latitude1  = locationTopicList[i].boundingBox[1];
				locationTopicList[i].longitude2 = locationTopicList[i].boundingBox[2];
				locationTopicList[i].latitude2  = locationTopicList[i].boundingBox[3];
			}
		}
		UtilStore.updateWriteStore("assistantSpatialRefTable", locationTopicList);
	}
    console.debug("event topics");
	// Event Topics
	if (eventTopicList != null) {
		//UtilList.addTableIndices(eventTopicList);
		for (var i in eventTopicList) {
            console.debug("prepare: " + i);
			eventTopicList[i].selection = "<input type='radio' name='objectWiz_eventTopic' id='objectWiz_"+eventTopicList[i].topicId+"'>";
	
			if (null != eventTopicList[i].at) {
				eventTopicList[i].date1 = eventTopicList[i].at;
				eventTopicList[i].type = "am";
	
			} else if (null != eventTopicList[i].from && null != eventTopicList[i].to) {
				eventTopicList[i].date1 = eventTopicList[i].from;
				eventTopicList[i].date2 = eventTopicList[i].to;
				eventTopicList[i].type = "von - bis";
	
			} else if (null != eventTopicList[i].from && null == eventTopicList[i].to) {
				eventTopicList[i].date1 = eventTopicList[i].from;
				eventTopicList[i].type = "seit";
	
			} else if (null == eventTopicList[i].from && null != eventTopicList[i].to) {
				eventTopicList[i].date1 = eventTopicList[i].to;
				eventTopicList[i].type = "bis";
			}
		}
        console.debug("update store");
        console.debug(eventTopicList);
		UtilStore.updateWriteStore("assistantTimeRefTable", eventTopicList);
        console.debug("update store finished");
	}
    console.debug("finished update");
}


scriptScope.addValuesToObject = function() {
	// If selected, add description
	var desc = "";
	if (dijit.byId("assistantDescriptionCheckbox").checked) {
		desc += dijit.byId("assistantDescription").getValue();
		desc += "\n\n";
	}
	if (dijit.byId("assistantHtmlContentCheckbox").checked) {
		desc += dijit.byId("assistantHtmlContent").getValue();
	}
	dijit.byId("generalDesc").setValue(dojo.trim(desc));

	// Title
	if (dijit.byId("assistantHtmlTitleCheckbox").checked) {
		dijit.byId("objectName").setValue(dojo.trim(dijit.byId("assistantHtmlTitle").getValue()));
	}

	// Add Thesaurus Topics 
    //var descStore = UtilGrid.getTable("thesaurusTerms");
	var thesaList = UtilGrid.getTableData("assistantDescriptorTable");
	for (var i in thesaList) {
		// If checkbox is selected
		if (thesaList[i].selection == 1) {//dojo.byId("objectWiz_"+getThesaurusId(thesaList[i])).checked) {
			if (thesaList[i].source == "INSPIRE") {
				// if it's an Inspire topic that's not already in the list
				UtilThesaurus.addInspireTopics([thesaList[i].title]);
			} else {
				// and if the descriptor isn't already in the target list
				if (dojo.every(UtilGrid.getTableData("thesaurusTerms"), function(item){ return (thesaList[i].topicId != item.topicId); })) {
					// add descriptor to store
                    UtilGrid.addTableDataRow("thesaurusTerms", {
                        topicId: thesaList[i].topicId, 
                        label:thesaList[i].title, 
                        title: thesaList[i].title, 
                        source: thesaList[i].source,
                        sourceString: thesaList[i].source} );
				}
			}
		}
	}

	// Add Location Topics
	//var spatialStore = dijit.byId("spatialRefAdminUnit").store;
	var locationList = UtilGrid.getTableData("assistantSpatialRefTable");
	for (var i in locationList) {
		// If checkbox is selected
		if (locationList[i].selection == 1) {//if (dojo.byId("objectWiz_"+locationList[i].topicId).checked) {
			var topic = locationList[i];
			var topicId = locationList[i].topicId;
			var storeData = UtilGrid.getTableData("spatialRefAdminUnit");
			var storedTopicIdx = null;
            var storedTopic = null;
			// Check if the topic is already in the spatial ref list
			for (var j in storeData) {
				if (storeData[j].topicId == topicId) {
					storedTopic = storeData[j];
                    storedTopicIdx = j;
					continue;
				}
			}

			// If the topic was found, update the bounding box and continue
			if (storedTopic) {
				UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopicIdx, "label", topic.name+", "+topic.type);
				if (topic.boundingBox) {
					UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopicIdx, "longitude1", topic.boundingBox[0]);
					UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopicIdx, "latitude1", topic.boundingBox[1]);
					UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopicIdx, "longitude2", topic.boundingBox[2]);
					UtilGrid.updateTableDataRowAttr("spatialRefAdminUnit", storedTopicIdx, "latitude2", topic.boundingBox[3]);
				}
				// Update elements that aren't displayed in the table
				storedTopic.nativeKey = topic.nativeKey;
				storedTopic.name = topic.name;
				storedTopic.topicType = topic.type;
				storedTopic.topicTypeId = topic.typeId;
			} else {
				// The topic was not found in the result list. Create a new key and
				// add the topic to the list
				//var key = UtilStore.getNewKey(spatialStore);
				if (topic.boundingBox) {
					UtilGrid.addTableDataRow("spatialRefAdminUnit", {
							topicId: topic.topicId,
							label: topic.name+", "+topic.type,
							name: topic.name,
							longitude1: topic.boundingBox[0],
							latitude1: topic.boundingBox[1],
							longitude2: topic.boundingBox[2],
							latitude2: topic.boundingBox[3],
							nativeKey: topic.nativeKey,
							topicType: topic.type,
							topicTypeId: topic.typeId
					});
				} else {
					UtilGrid.addTableDataRow("spatialRefAdminUnit", {
							topicId: topic.topicId,
							label: topic.name+", "+topic.type,
							name: topic.name,
							nativeKey: topic.nativeKey,
							topicType: topic.type,
							topicTypeId: topic.typeId
					});
				}
			}
		}
	}

	// Add Event Topic
	//var timeRefStore = dijit.byId("assistantTimeRefTable").store;
	var eventList = UtilGrid.getTableData("assistantTimeRefTable");

	for (var i in eventList) {
		var eventJS = eventList[i];//itemToJS(timeRefStore, eventList[i]);
		// If radio button is selected
		if (dojo.byId("objectWiz_"+eventJS.topicId).checked) {
			if (eventJS.type == "von - bis")
				eventJS.type = "von";

			dijit.byId("timeRefType").setValue(eventJS.type);

			if (eventJS.type == "bis") {
				dijit.byId("timeRefDate1").setValue(eventJS.to);

			} else if (eventJS.type == "am") {
				dijit.byId("timeRefDate1").setValue(eventJS.at);
				dijit.byId("timeRefDate2").setValue(eventJS.at);

			} else {
				dijit.byId("timeRefDate1").setValue(eventJS.from);
				dijit.byId("timeRefDate2").setValue(eventJS.to);
			}
		}
	}

	// Add the url to the link table
	var newUrl = createNewUrl();

	var urlData = [newUrl];
	UtilList.addIcons(urlData);
	UtilList.addUrlLinkLabels(urlData);
	UtilStore.updateWriteStore("linksTo", urlData);

	thisDialog.hide();
}

// needed for checkbox-ids
// distinguish between inspire topics and other ones
// since it can happen that they have the same name it's important to
// classify them so that the checkboxes can be identified
function getThesaurusId(topic) {
	if (topic.source == "INSPIRE") {
		return "inspire_"+topic.title;
	} else {
		return topic.topicId;
	}
}

function createNewUrl() {
	var newUrl = {};

	//newUrl.Id = 0;
	newUrl.relationType = 9999;
	newUrl.relationTypeName = "unspezifischer Verweis";
	var urlName = dojo.trim(dijit.byId("assistantHtmlTitle").getValue());
	newUrl.name = urlName.length != 0 ? urlName : "Internet-Verweis";
	newUrl.url = dojo.trim(dijit.byId("assistantURL").getValue());
	newUrl.urlType = "";
	newUrl.description = "";

	return newUrl;
}


function showLoadingZone() {
	dojo.style("createObjectWizardLoadingZone", "visibility", "visible");
}

function hideLoadingZone() {
	dojo.style("createObjectWizardLoadingZone", "visibility", "hidden");
}


// SNS Start search button function
// Reads the url from the input field and executes a SNS autoClassify request
scriptScope.startSearch = function() {
	hideResults();
	resetInputFields();
	var url = dojo.trim(dijit.byId("assistantURL").getValue());
	var numWords = dijit.byId("assistantNumWords").getValue();
	var htmlContentNumWords = dijit.byId("assistantHtmlContentNumWords").getValue();

	var showDescription = dijit.byId("assistantIncludeMetaTagCheckbox").checked;
	var showHtmlContent = dijit.byId("assistantIncludeHtmlContentCheckbox").checked;

	var autoClassifyDef = autoClassifyUrl(url, numWords);
	var getHtmlContentDef = getHtmlContent(url, htmlContentNumWords);
	var getHtmlTitleDef = getHtmlTitle(url);

	showLoadingZone();
	var l1 = new dojo.DeferredList([autoClassifyDef, getHtmlContentDef, getHtmlTitleDef]);
	l1.addCallback(function (resultList) {
		if (resultList[0][0] == false || resultList[1][0] == false || resultList[2][0] == false) {
			handleError(resultList[0], resultList[1]);

		} else {
			var topicMap = resultList[0][1];
			var htmlContent = resultList[1][1];
			var htmlTitle = resultList[2][1];
	
			if ((topicMap.indexedDocument == null || topicMap.indexedDocument.description == null) && topicMap.thesaTopics == null
			    && topicMap.locationTopics == null && topicMap.eventTopics == null) {
					dialog.show("<fmt:message key='general.hint' />", "<fmt:message key='dialog.wizard.create.snsNoResultError' />", dialog.INFO);

			} else {
                console.debug("callback else else");
                console.debug(topicMap);
                console.debug(htmlContent);
                console.debug(htmlTitle);
				updateInputFields(topicMap);
                console.debug("update content");
				dijit.byId("assistantHtmlContent").setValue(htmlContent);
				console.debug("update title");
                dijit.byId("assistantHtmlTitle").setValue(htmlTitle);
				showResults(showDescription, showHtmlContent);
			}
		}
		thisDialog.resize();
		hideLoadingZone();
	});

	l1.addErrback(function(err) {
		console.debug("l1 Error: "+err);
		hideLoadingZone();
	});
}


function handleError(autoClassifyResult, getHtmlContentResult) {
	// If an sns error exists, display an error message
	if (autoClassifyResult[0] == false) {
		var err = autoClassifyResult[1];
//		console.debug("Error: "+err);

		if (err.message.indexOf("SNS_TIMEOUT") != -1) {
			dialog.show("<fmt:message key='general.error' />", "<fmt:message key='sns.timeoutError' />", dialog.WARNING);

		} else if (err.message.indexOf("SNS_INVALID_URL") != -1) {
			dialog.show("<fmt:message key='general.error' />", "<fmt:message key='dialog.wizard.create.snsInvalidPageError' />", dialog.WARNING);

		} else {
			displayErrorMessage(err);
		}

	} else if (getHtmlContentResult[0] == false) {
		// Handle the exception which was thrown while parsing 
		var err = getHtmlContentResult[1];
//		console.debug("Error: "+err);

		displayErrorMessage(err);
	}
}

function autoClassifyUrl(url, numWords) {
	var def = new dojo.Deferred();

	SNSService.autoClassifyURL(url, numWords, null, false, null, 100, {
		callback: function(topicMap) {
			def.callback(topicMap);
		},

		errorHandler: function(msg, err) {
			def.errback(new Error(msg));
		}
	});

	return def;
}

function getHtmlContent(url, maxWords) {
	var def = new dojo.Deferred();

	HttpService.parseHtml(url, maxWords, {
		callback: function(htmlContent) {
			def.callback(htmlContent);
		},

		errorHandler: function(msg, err) {
			def.errback(new Error(msg));
		}
	});

	return def;
}

function getHtmlTitle(url) {
	var def = new dojo.Deferred();

	HttpService.getHtmlTitle(url, {
		callback: function(title) {
			def.callback(title);
		},

		errorHandler: function(msg, err) {
			def.errback(new Error(msg));
		}
	});

	return def;
}

closeThisDialog = function() {
	thisDialog.hide();
}

</script>
</head>

<body>
	<div dojotype="dijit.layout.ContentPane" style="height:550px">
	<div id="assistant" class="">
		<div id="assistantContent" class="content">
			<div id="winNavi" style="top:0px; right: 3px;">
                    <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=creation-of-objects-2#creation-of-objects-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
			<!-- LEFT HAND SIDE CONTENT START -->
			<div class="inputContainer field grey" style="padding:10px !important;">
			    <span class="outer"><div>
				    <span class="label"><label for="assistantURL" onclick="javascript:dialog.showContextHelp(arguments[0], 8063)"><fmt:message key="dialog.wizard.create.url" /></label></span>
				    <span class="input"><input type="text" id="assistantURL" name="assistantURL" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
				</div></span>
                <span class="outer"><div>
                <span>
			     <label for="assistantNumWords" onclick="javascript:dialog.showContextHelp(arguments[0], 8064)"><fmt:message key="dialog.wizard.create.numWords" /></label></span>
				<span><input dojoType="dijit.form.NumberTextBox" min="0" max="1000" maxLength="4" id="assistantNumWords" class="w038" /></span>
				<div class="checkboxContainer">
					<span class="input"><input type="checkbox" name="assistantIncludeMetaTagCheckbox" id="assistantIncludeMetaTagCheckbox" dojoType="dijit.form.CheckBox" checked /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8065, 'Beschreibung')"><fmt:message key="dialog.wizard.create.showDescription" /></label></span>
					<span class="input"><input type="checkbox" name="assistantIncludeHtmlContentCheckbox" id="assistantIncludeHtmlContentCheckbox" dojoType="dijit.form.CheckBox" checked /><label style="cursor:default;"><fmt:message key="dialog.wizard.create.showNumWords.1" /> <input dojoType="dijit.form.NumberTextBox" min="0" max="10000" maxlength="5" id="assistantHtmlContentNumWords" /> <fmt:message key="dialog.wizard.create.showNumWords.2" /></label></span>
				</div>
                </div></span>
                <div class="fill"></div>
			</div>

			<div class="inputContainerFooter">
				<span class="button">
					<span>
						<button id="createObjWizardStartButton" type="button" style="float:right" dojoType="dijit.form.Button" onClick="javascript:scriptScope.startSearch();" title="<fmt:message key="dialog.wizard.create.start" />"><fmt:message key="dialog.wizard.create.start" /></button>
					</span>
					<span id="createObjectWizardLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
						<img src="img/ladekreis.gif" />
					</span>
				</span>
			</div>

			<div id="resultContainer" class="inputContainer">
			    <span class="outer"><div>
				<span class="label"><h2><fmt:message key="dialog.wizard.create.result" /></h2></span>
					<div class="checkboxContainer">
						<span class="input" style="margin-bottom: 5px;"><input type="checkbox" name="assistantHtmlTitleCheckbox" id="assistantHtmlTitleCheckbox" dojoType="dijit.form.CheckBox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8066)"><fmt:message key="dialog.wizard.create.addTitle" /></label></span>
					</div>
					<div class="inputContainer">
		           		<span class="input" style="margin-bottom: 5px;"><input type="text" id="assistantHtmlTitle" dojoType="dijit.form.ValidationTextBox" style="width:100%;" /></span> 
					</div>


				<span id="assistantDescriptionContainer">
					<div class="checkboxContainer">
						<span class="input"><input type="checkbox" name="assistantDescriptionCheckbox" id="assistantDescriptionCheckbox" dojoType="dijit.form.CheckBox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8067)"><fmt:message key="dialog.wizard.create.addDescription" /></label></span>
					</div>
					<div class="inputContainer">
		           		<span class="input" style="margin-bottom: 5px;"><input type="text" mode="textarea" id="assistantDescription" dojoType="dijit.form.SimpleTextarea" rows="5" style="width:100%;" /></span> 
					</div>
				</span>

				<span id="assistantHtmlContentContainer">
					<div class="checkboxContainer">
						<span class="input"><input type="checkbox" name="assistantHtmlContentCheckbox" id="assistantHtmlContentCheckbox" dojoType="dijit.form.CheckBox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8068)"><fmt:message key="dialog.wizard.create.addContent" /></label></span>
					</div>
					<div class="inputContainer">
		           		<span class="input"><input type="text" mode="textarea" id="assistantHtmlContent" dojoType="dijit.form.SimpleTextarea" rows="5" style="width:100%;" /></span> 
					</div>
				</span>
                </div></span>
                
                <span class="outer"><div>
				<span class="label"><label for="assistantDescriptorTable" onclick="javascript:dialog.showContextHelp(arguments[0], 8069)"><fmt:message key="dialog.wizard.create.descriptors" /></label></span>
	                <div class="tableContainer" style="margin-bottom: 5px;">
	                	<div id="assistantDescriptorTable" interactive="true" class="hideTableHeader"></div>
	                </div>

    				<div class="checkboxContainer">
    					<span class="input"><input type="checkbox" name="assistantDescriptorTableCheckbox" id="assistantDescriptorTableCheckbox" dojoType="dijit.form.CheckBox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8070)"><fmt:message key="dialog.wizard.create.selectAllDescriptors" /></label></span>
    				</div>
                </div></span>

                <span class="outer"><div>
	                <span class="label"><label for="assistantSpatialRefTable" onclick="javascript:dialog.showContextHelp(arguments[0], 8071)"><fmt:message key="dialog.wizard.create.spatial" /></label></span>
					<div class="tableContainer" style="margin-bottom: 5px;">
						<div id="assistantSpatialRefTable" interactive="true" ></div>
	                </div>
    				<div class="checkboxContainer">
    					<span class="input"><input type="checkbox" name="assistantSpatialRefTableCheckbox" id="assistantSpatialRefTableCheckbox" dojoType="dijit.form.CheckBox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8072)"><fmt:message key="dialog.wizard.create.selectAllSpatialRefs" /></label></span>
    				</div>
                </div></span>
                
                <span class="outer"><div>
					<span class="label"><label for="assistantTimeRefTable" onclick="javascript:dialog.showContextHelp(arguments[0], 8073)"><fmt:message key="dialog.wizard.create.time" /></label></span>
	                <div class="tableContainer">
	                	<div id="assistantTimeRefTable"></div>
					</div>
                </div></span>
			</div> <!-- RESULT CONTAINER END -->

			<div id="resultButtonContainer" class="inputContainer">
				<span class="button" style="height:20px !important;">
					<span style="float:right;">
						<button id="createObjWizardAcceptButton" dojoType="dijit.form.Button" title="<fmt:message key="dialog.wizard.create.apply" />" onClick="javascript:scriptScope.addValuesToObject();"><fmt:message key="dialog.wizard.create.apply" /></button>
					</span>
					<span style="float:right;">
						<button id="createObjWizardCancelButton" dojoType="dijit.form.Button" title="<fmt:message key="dialog.wizard.create.cancel" />" onClick="javascript:closeThisDialog();"><fmt:message key="dialog.wizard.create.cancel" /></button>
					</span>
				</span>
			</div>

			<!-- LEFT HAND SIDE CONTENT END -->
		</div>
	</div>
	</div>
</body>
</html>
