<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V3</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {
	hideResults();

	dojo.widget.byId("assistantURL").setValue("http://");
	dojo.widget.byId("assistantNumWords").setValue(1000);
	dojo.widget.byId("assistantHtmlContentNumWords").setValue(100);

    // Pressing 'enter' on the input field is equal to a button click
    dojo.event.connect(dojo.widget.byId("assistantURL").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER) {
                scriptScope.startSearch();
            }
	});
    // Pressing 'enter' on the input field is equal to a button click
    dojo.event.connect(dojo.widget.byId("assistantNumWords").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER) {
                scriptScope.startSearch();
            }
	});
    // Pressing 'enter' on the input field is equal to a button click
    dojo.event.connect(dojo.widget.byId("assistantHtmlContentNumWords").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER) {
                scriptScope.startSearch();
            }
	});


	var descriptorCheckbox = dojo.widget.byId("assistantDescriptorTableCheckbox");
	dojo.event.connect(descriptorCheckbox, "onClick", function() {
		// get checkbox value
		var value = dojo.widget.byId("assistantDescriptorTableCheckbox").checked;
		// get data drom the topic store
		var data = dojo.widget.byId("assistantDescriptorTable").store.getData();
		for (var i in data) {
			// check / uncheck all select boxes
			dojo.byId("objectWiz_"+data[i].topicId).checked = value;
		}
	});
	
	var spatialCheckbox = dojo.widget.byId("assistantSpatialRefTableCheckbox");
	dojo.event.connect(spatialCheckbox, "onClick", function() {
		// get checkbox value
		var value = dojo.widget.byId("assistantSpatialRefTableCheckbox").checked;
		// get data drom the spatial store
		var data = dojo.widget.byId("assistantSpatialRefTable").store.getData();
		for (var i in data) {
			// check / uncheck all select boxes
			dojo.byId("objectWiz_"+data[i].topicId).checked = value;
		}
	});
});

function resetInputFields() {
	dojo.widget.byId("assistantHtmlTitleCheckbox").setValue(false);
	dojo.widget.byId("assistantHtmlTitle").setValue("");
	dojo.widget.byId("assistantDescriptionCheckbox").setValue(false);
	dojo.widget.byId("assistantDescription").setValue("");
	dojo.widget.byId("assistantDescriptorTable").clear();
	dojo.widget.byId("assistantDescriptorTableCheckbox").setValue(false);
	dojo.widget.byId("assistantSpatialRefTable").clear();
	dojo.widget.byId("assistantSpatialRefTableCheckbox").setValue(false);
	dojo.widget.byId("assistantTimeRefTable").clear();
}

function hideResults() {
	dojo.html.hide(dojo.byId("resultContainer"));
	dojo.html.hide(dojo.byId("resultButtonContainer"));
}

function showResults(showDescription, showHtmlContent) {
	dojo.html.show(dojo.byId("resultContainer"));
	dojo.html.show(dojo.byId("resultButtonContainer"));
	
	if (showDescription) {
		dojo.html.show("assistantDescriptionContainer");
	} else {
		dojo.html.hide("assistantDescriptionContainer");	
	}

	if (showHtmlContent) {
		dojo.html.show("assistantHtmlContentContainer");
	} else {
		dojo.html.hide("assistantHtmlContentContainer");	
	}
}

// Updates the input fields with values from the given topic map
function updateInputFields(topicMap) {
	var indexedDocument = topicMap.indexedDocument;
	var thesaTopicList = topicMap.thesaTopics;
	var locationTopicList = topicMap.locationTopics;
	var eventTopicList = topicMap.eventTopics;

	dojo.widget.byId("assistantDescriptionCheckbox").setValue(false);
	dojo.widget.byId("assistantHtmlContentCheckbox").setValue(false);

	// Description
	if (indexedDocument.description != null)
		dojo.widget.byId("assistantDescription").setValue(dojo.string.trim(""+indexedDocument.description));

	// Thesaurus Topics
	if (thesaTopicList != null) {
		UtilList.addTableIndices(thesaTopicList);
		for (var i in thesaTopicList) {
			// Add checkbox to entry
			thesaTopicList[i].selection = "<input type='checkbox' id='objectWiz_"+thesaTopicList[i].topicId+"'>";
		}
		dojo.widget.byId("assistantDescriptorTable").store.setData(thesaTopicList);
	}

	// Location Topics
	if (locationTopicList != null) {
		UtilList.addTableIndices(locationTopicList);
		UtilList.addSNSLocationLabels(locationTopicList);
		for (var i in locationTopicList) {
			// Add checkbox to entry
			locationTopicList[i].selection = "<input type='checkbox' id='objectWiz_"+locationTopicList[i].topicId+"'>";
			// Prepare bb for display in the table
			if (locationTopicList[i].boundingBox) {
				locationTopicList[i].longitude1 = locationTopicList[i].boundingBox[0];
				locationTopicList[i].latitude1  = locationTopicList[i].boundingBox[1];
				locationTopicList[i].longitude2 = locationTopicList[i].boundingBox[2];
				locationTopicList[i].latitude2  = locationTopicList[i].boundingBox[3];
			}
		}
		dojo.widget.byId("assistantSpatialRefTable").store.setData(locationTopicList);
	}

	// Event Topics
	if (eventTopicList != null) {
		UtilList.addTableIndices(eventTopicList);
		for (var i in eventTopicList) {
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
		dojo.widget.byId("assistantTimeRefTable").store.setData(eventTopicList);
	}
}


scriptScope.addValuesToObject = function() {
	// If selected, add description
	var desc = "";
	if (dojo.widget.byId("assistantDescriptionCheckbox").checked) {
		desc += dojo.widget.byId("assistantDescription").getValue();
		desc += "\n\n";
	}
	if (dojo.widget.byId("assistantHtmlContentCheckbox").checked) {
		desc += dojo.widget.byId("assistantHtmlContent").getValue();
	}
	dojo.widget.byId("generalDesc").setValue(dojo.string.trim(desc));

	// Title
	if (dojo.widget.byId("assistantHtmlTitleCheckbox").checked) {
		dojo.widget.byId("objectName").setValue(dojo.string.trim(dojo.widget.byId("assistantHtmlTitle").getValue()));
	}

	// Add Thesaurus Topics 
    var descStore = dojo.widget.byId("thesaurusTerms").store;
	var thesaList = dojo.widget.byId("assistantDescriptorTable").store.getData();
	for (var i in thesaList) {
		// If checkbox is selected
		if (dojo.byId("objectWiz_"+thesaList[i].topicId).checked) {
			// and if the descriptor isn't already in the target list
			if (dojo.lang.every(descStore.getData(), function(item){ return (thesaList[i].topicId != item.topicId); })) {
				// add descriptor to store
				descStore.addData( {Id: UtilStore.getNewKey(descStore), topicId: thesaList[i].topicId, title: thesaList[i].title} );
			}
		}
	}

	// Add Location Topics
	var spatialStore = dojo.widget.byId("spatialRefAdminUnit").store;
	var locationList = dojo.widget.byId("assistantSpatialRefTable").store.getData();
	for (var i in locationList) {
		// If checkbox is selected
		if (dojo.byId("objectWiz_"+locationList[i].topicId).checked) {
			var topic = locationList[i];
			var topicId = locationList[i].topicId;
			var storeData = spatialStore.getData();
			var storedTopic = null;
			// Check if the topic is already in the spatial ref list
			for (var j in storeData) {
				if (storeData[j].topicId == topicId) {
					storedTopic = storeData[j];
					continue;
				}
			}

			// If the topic was found, update the bounding box and continue
			if (storedTopic) {
				spatialStore.update(storedTopic, "label", topic.name+", "+topic.type);
				if (topic.boundingBox) {
					spatialStore.update(storedTopic, "longitude1", topic.boundingBox[0]);
					spatialStore.update(storedTopic, "latitude1", topic.boundingBox[1]);
					spatialStore.update(storedTopic, "longitude2", topic.boundingBox[2]);
					spatialStore.update(storedTopic, "latitude2", topic.boundingBox[3]);
				}
				// Update elements that aren't displayed in the table
				storedTopic.nativeKey = topic.nativeKey;
				storedTopic.name = topic.name;
				storedTopic.topicType = topic.type;
				storedTopic.topicTypeId = topic.typeId;
			} else {
				// The topic was not found in the result list. Create a new key and
				// add the topic to the list
				var key = UtilStore.getNewKey(spatialStore);
				if (topic.boundingBox) {
					spatialStore.addData({
							Id: key,
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
					spatialStore.addData({
							Id: key,
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
	var eventList = dojo.widget.byId("assistantTimeRefTable").store.getData();

	for (var i in eventList) {
		// If radio button is selected
		if (dojo.byId("objectWiz_"+eventList[i].topicId).checked) {
			if (eventList[i].type == "von - bis")
				eventList[i].type = "von";

			dojo.widget.byId("timeRefType").setValue(eventList[i].type);

			if (eventList[i].type == "bis") {
				dojo.widget.byId("timeRefDate1").setValue(eventList[i].to);

			} else if (eventList[i].type == "am") {
				dojo.widget.byId("timeRefDate1").setValue(eventList[i].at);
				dojo.widget.byId("timeRefDate2").setValue(eventList[i].at);

			} else {
				dojo.widget.byId("timeRefDate1").setValue(eventList[i].from);
				dojo.widget.byId("timeRefDate2").setValue(eventList[i].to);
			}
		}
	}

	// Add the url to the link table
	var newUrl = createNewUrl();

	var urlData = [newUrl];
	UtilList.addIcons(urlData);
	UtilList.addUrlLinkLabels(urlData);
	dojo.widget.byId("linksTo").store.setData(urlData);

	_container_.closeWindow();
}

function createNewUrl() {
	var newUrl = {};

	newUrl.Id = 0;
	newUrl.relationType = -1;
	newUrl.relationTypeName = "Internet-Verweis";
	var urlName = dojo.string.trim(dojo.widget.byId("assistantHtmlTitle").getValue());
	newUrl.name = urlName.length != 0 ? urlName : "Internet-Verweis";
	newUrl.url = dojo.string.trim(dojo.widget.byId("assistantURL").getValue());
	newUrl.datatype = "HTML";
	newUrl.volume = "";
	newUrl.urlType = "";
	newUrl.iconUrl = "";
	newUrl.iconText = "";
	newUrl.description = "";

	return newUrl;
}


function showLoadingZone() {
	var loadingZone = dojo.byId("createObjectWizardLoadingZone");
	loadingZone.style.visibility = "visible";
}

function hideLoadingZone() {
	var loadingZone = dojo.byId("createObjectWizardLoadingZone");
	loadingZone.style.visibility = "hidden";
}


// SNS Start search button function
// Reads the url from the input field and executes a SNS autoClassify request
scriptScope.startSearch = function() {
	hideResults();
	resetInputFields();
	var url = dojo.string.trim(dojo.widget.byId("assistantURL").getValue());
	var numWords = dojo.widget.byId("assistantNumWords").getValue();
	var htmlContentNumWords = dojo.widget.byId("assistantHtmlContentNumWords").getValue();

	var showDescription = dojo.widget.byId("assistantIncludeMetaTagCheckbox").checked;
	var showHtmlContent = dojo.widget.byId("assistantIncludeHtmlContentCheckbox").checked;

	var autoClassifyDef = autoClassifyUrl(url, numWords);
	var getHtmlContentDef = getHtmlContent(url, htmlContentNumWords);
	var getHtmlTitleDef = getHtmlTitle(url);

	showLoadingZone();

	var l1 = new dojo.DeferredList([autoClassifyDef, getHtmlContentDef, getHtmlTitleDef], false, false, true);
	l1.addCallback(function (resultList) {
		if (resultList[0][0] == false || resultList[1][0] == false || resultList[2][0] == false) {
			handleError(resultList[0], resultList[1]);

		} else {
			var topicMap = resultList[0][1];
			var htmlContent = resultList[1][1];
			var htmlTitle = resultList[2][1];
	
			if ((topicMap.indexedDocument == null || topicMap.indexedDocument.description == null) && topicMap.thesaTopics == null
			    && topicMap.locationTopics == null && topicMap.eventTopics == null) {
					dialog.show(message.get("general.hint"), message.get("dialog.wizard.create.snsNoResultError"), dialog.INFO);

			} else {
				updateInputFields(topicMap);
				dojo.widget.byId("assistantHtmlContent").setValue(dojo.string.trim(htmlContent));
				dojo.widget.byId("assistantHtmlTitle").setValue(dojo.string.trim(htmlTitle));
				showResults(showDescription, showHtmlContent);
			}
		}

		hideLoadingZone();
	});

	l1.addErrback(function(err) {
		dojo.debug("l1 Error: "+err);
		dojo.debugShallow(err);
		hideLoadingZone();
	});
}


function handleError(autoClassifyResult, getHtmlContentResult) {
	// If an sns error exists, display an error message
	if (autoClassifyResult[0] == false) {
		var err = autoClassifyResult[1];
//		dojo.debug("Error: "+err);
//		dojo.debugShallow(err);

		if (err.message.indexOf("SNS_TIMEOUT") != -1) {
			dialog.show(message.get("general.error"), message.get("sns.timeoutError"), dialog.WARNING);

		} else if (err.message.indexOf("SNS_INVALID_URL") != -1) {
			dialog.show(message.get("general.error"), message.get("dialog.wizard.create.snsInvalidPageError"), dialog.WARNING);

		} else {
			displayErrorMessage(err);
		}

	} else if (getHtmlContentResult[0] == false) {
		// Handle the exception which was thrown while parsing 
		var err = getHtmlContentResult[1];
//		dojo.debug("Error: "+err);
//		dojo.debugShallow(err);

		displayErrorMessage(err);
	}
}

function autoClassifyUrl(url, numWords) {
	var def = new dojo.Deferred();

	SNSService.autoClassifyURL(url, numWords, null, false, null, {
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

scriptScope.closeDialog = function() {
	_container_.closeWindow();
}

</script>
</head>

<body>

<div dojoType="ContentPane">

	<div id="assistant" class="contentBlockWhite top fullBlock">
		<div id="winNavi">
			<a href="#" title="Hilfe">[?]</a>
		</div>
		<div id="assistantContent" class="content">

			<!-- LEFT HAND SIDE CONTENT START -->
			<div class="spacer"></div>
			<div class="spacer"></div>
			<div class="inputContainer field grey noSpaceBelow fullField">
				<span class="label"><label for="assistantURL" onclick="javascript:dialog.showContextHelp(arguments[0], 'URL der Internetseite')"><fmt:message key="dialog.wizard.create.url" /></label></span>
				<span class="input spaceBelow"><input type="text" id="assistantURL" name="assistantURL" class="w640" dojoType="ingrid:ValidationTextBox" /></span>
				<span><label for="assistantNumWords" onclick="javascript:dialog.showContextHelp(arguments[0], 'Anzahl der zu analysierenden W&ouml;rter')"><fmt:message key="dialog.wizard.create.numWords" /></label></span>
				<span><input dojoType="IntegerTextbox" min="0" max="1000" maxlength="4" id="assistantNumWords" class="w038" /></span>

				<div class="checkboxContainer">
					<span class="input"><input type="checkbox" name="assistantIncludeMetaTagCheckbox" id="assistantIncludeMetaTagCheckbox" dojoType="Checkbox" checked /><label onclick="javascript:dialog.showContextHelp(arguments[0], 'Beschreibung')">Zeige die Beschreibung der &uuml;bergebenen Webseite an</label></span>
					<span class="input"><input type="checkbox" name="assistantIncludeHtmlContentCheckbox" id="assistantIncludeHtmlContentCheckbox" dojoType="Checkbox" checked /><label style="cursor:default;"><fmt:message key="dialog.wizard.create.showNumWords.1" /> <input dojoType="IntegerTextbox" min="0" max="10000" maxlength="5" id="assistantHtmlContentNumWords" class="w038" /> <fmt:message key="dialog.wizard.create.showNumWords.2" /></label></span>
				</div>

				<div class="spacerField"></div>
			</div>

			<div class="inputContainer">
				<span class="button w644" style="height:20px !important;">
					<span style="float:right;">
						<button id="createObjWizardStartButton" dojoType="ingrid:Button" onClick="javascript:scriptScope.startSearch();" title="Start"><fmt:message key="dialog.wizard.create.start" /></button>
					</span>
					<span id="createObjectWizardLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
						<img src="img/ladekreis.gif" />
					</span>
				</span>
			</div>

			<div id="resultContainer" class="inputContainer noSpaceBelow">
				<span class="label"><fmt:message key="dialog.wizard.create.result" /></span>

					<div class="checkboxContainer">
						<span class="input"><input type="checkbox" name="assistantHtmlTitleCheckbox" id="assistantHtmlTitleCheckbox" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 'Titel')"><fmt:message key="dialog.wizard.create.addTitle" /></label></span>
					</div>
					<div class="inputContainer">
		           		<span class="input"><input type="text" id="assistantHtmlTitle" class="w676" dojoType="ingrid:ValidationTextbox" /></span> 
					</div>


				<span id="assistantDescriptionContainer">
					<div class="checkboxContainer">
						<span class="input"><input type="checkbox" name="assistantDescriptionCheckbox" id="assistantDescriptionCheckbox" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 'Beschreibung')"><fmt:message key="dialog.wizard.create.addDescription" /></label></span>
					</div>
					<div class="inputContainer">
		           		<span class="input"><input type="text" mode="textarea" id="assistantDescription" class="w676 h062" dojoType="ingrid:ValidationTextbox" /></span> 
					</div>
				</span>

				<span id="assistantHtmlContentContainer">
					<div class="checkboxContainer">
						<span class="input"><input type="checkbox" name="assistantHtmlContentCheckbox" id="assistantHtmlContentCheckbox" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 'Inhalt')"><fmt:message key="dialog.wizard.create.addContent" /></label></span>
					</div>
					<div class="inputContainer">
		           		<span class="input"><input type="text" mode="textarea" id="assistantHtmlContent" class="w676 h098" dojoType="ingrid:ValidationTextbox" /></span> 
					</div>
				</span>

				<span class="label"><label for="assistantDescriptorTable" onclick="javascript:dialog.showContextHelp(arguments[0], 'Deskriptoren')"><fmt:message key="dialog.wizard.create.descriptors" /></label></span>
				<div class="inputContainer">
	                <div class="tableContainer headHiddenRows4 full">
	            	    <table id="assistantDescriptorTable" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort">
	            	      <thead>
	            		      <tr>
	                  			<th nosort="true" field="selection" dataType="String">&nbsp;</th>
	                  			<th nosort="true" field="title" dataType="String">&nbsp;</th>
	            		      </tr>
	            	      </thead>
						  <colgroup>
						    <col width="23">
						    <col width="500">
						  </colgroup>
	            	    </table>
	                </div>
				</div>

				<div class="checkboxContainer">
					<span class="input"><input type="checkbox" name="assistantDescriptorTableCheckbox" id="assistantDescriptorTableCheckbox" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 'Alle Deskriptoren ausw&auml;hlen')"><fmt:message key="dialog.wizard.create.selectAllDescriptors" /></label></span>
				</div>

	            <div class="inputContainer">
	                <span class="label"><label for="assistantSpatialRefTable" onclick="javascript:dialog.showContextHelp(arguments[0], 'Geothesaurus-Raumbezug')"><fmt:message key="dialog.wizard.create.spatial" /></label></span>
	                <div class="tableContainer rows4 full">
	            	    <table id="assistantSpatialRefTable" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort">
	            	      <thead>
	            		      <tr>
	                  			<th nosort="true" field="selection" dataType="String" width="30"><fmt:message key="dialog.wizard.create.spatial.select" /></th>
	                  			<th nosort="true" field="label" dataType="String" width="285"><fmt:message key="dialog.wizard.create.spatial.name" /></th>
	                  			<th nosort="true" field="longitude1" dataType="String" width="90"><fmt:message key="dialog.wizard.create.spatial.longitude1" /></th>
	                  			<th nosort="true" field="latitude1" dataType="String" width="90"><fmt:message key="dialog.wizard.create.spatial.latitude1" /></th>
	                  			<th nosort="true" field="longitude2" dataType="String" width="90"><fmt:message key="dialog.wizard.create.spatial.longitude2" /></th>
	                  			<th nosort="true" field="latitude2" dataType="String" width="90"><fmt:message key="dialog.wizard.create.spatial.latitude2" /></th>
	            		      </tr>
	            	      </thead>
	            	      <tbody>
	            	      </tbody>
	            	    </table>
	                </div>
				</div>

				<div class="checkboxContainer">
					<span class="input"><input type="checkbox" name="assistantSpatialRefTableCheckbox" id="assistantSpatialRefTableCheckbox" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 'Alle Raumbez&uuml;ge ausw&auml;hlen')"><fmt:message key="dialog.wizard.create.selectAllSpatialRefs" /></label></span>
				</div>

				<div class="inputContainer">
					<span class="label"><label for="assistantTimeRefTable" onclick="javascript:dialog.showContextHelp(arguments[0], 'Zeitbezug')"><fmt:message key="dialog.wizard.create.time" /></label></span>
	                <div class="tableContainer rows4 full">
						<table id="assistantTimeRefTable" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y" minRows="4" cellspacing="0" class="filteringTable">
			    	      <thead>
			    		      <tr>
			          			<th nosort="true" field="selection" dataType="String" width="60"><fmt:message key="dialog.wizard.create.time.select" /></th>
			          			<th nosort="true" field="name" dataType="String" width="346"><fmt:message key="dialog.wizard.create.time.description" /></th>
			          			<th nosort="true" field="date1" dataType="Date" width="100"><fmt:message key="dialog.wizard.create.time.firstDate" /></th>
			          			<th nosort="true" field="date2" dataType="Date" width="100"><fmt:message key="dialog.wizard.create.time.secondDate" /></th>
			          			<th nosort="true" field="type" dataType="String" width="100"><fmt:message key="dialog.wizard.create.time.type" /></th>
			    		      </tr>
			    	      </thead>
			    	      <tbody>
			    	      </tbody>
						</table>
					</div>
				</div>
			</div> <!-- RESULT CONTAINER END -->

			<div id="resultButtonContainer" class="inputContainer">
				<span class="button w644" style="height:20px !important;">
					<span style="float:right;">
						<button id="createObjWizardAcceptButton" dojoType="ingrid:Button" title="&Uuml;bernehmen" onClick="javascript:scriptScope.addValuesToObject();"><fmt:message key="dialog.wizard.create.apply" /></button>
					</span>
					<span style="float:right;">
						<button id="createObjWizardCancelButton" dojoType="ingrid:Button" title="Abbrechen" onClick="javascript:scriptScope.closeDialog();"><fmt:message key="dialog.wizard.create.cancel" /></button>
					</span>
				</span>
			</div>

			<!-- LEFT HAND SIDE CONTENT END -->
		</div>
	</div>
</div>

</body>
</html>
