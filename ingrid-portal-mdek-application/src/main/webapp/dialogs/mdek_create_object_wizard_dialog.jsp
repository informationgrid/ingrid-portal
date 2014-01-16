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
var scopeCreateObjectWiz = _container_;

var thisDialog = dijit.byId("subPageDialog");
if (thisDialog == undefined)
    thisDialog = dijit.byId("pageDialog");


new dojox.layout.ContentPane({
    title: "results",
    layoutAlign: "client",
    href: "dialogs/mdek_wizard_result.jsp?c="+userLocale,
    preload: "true",
    scriptHasHooks: true,
    executeScripts: true
}, "resultsContainer");

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


init = (function() {
    console.debug("init");
	dijit.byId("assistantURL").setValue("http://");
	dijit.byId("assistantNumWords").setValue(1000);
	dijit.byId("assistantHtmlContentNumWords").setValue(100);

    // Pressing 'enter' on the input field is equal to a button click
    dojo.connect(dijit.byId("assistantURL").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == dojo.keys.ENTER) {
                scopeCreateObjectWiz.startSearch();
            }
	});
    // Pressing 'enter' on the input field is equal to a button click
    dojo.connect(dijit.byId("assistantNumWords").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == dojo.keys.ENTER) {
                scopeCreateObjectWiz.startSearch();
            }
	});
    // Pressing 'enter' on the input field is equal to a button click
    dojo.connect(dijit.byId("assistantHtmlContentNumWords").domNode, "onkeypress",
        function(event) {
            if (event.keyCode == dojo.keys.ENTER) {
                scopeCreateObjectWiz.startSearch();
            }
	});

});

scopeCreateObjectWiz.addValuesToObject = function() {
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
scopeCreateObjectWiz.startSearch = function() {
    scopeWizardResults.hideResults();
    scopeWizardResults.resetInputFields();
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
                scopeWizardResults.updateInputFields(topicMap, {title: htmlTitle, content: htmlContent}, true);
                scopeWizardResults.showResults(showDescription, showHtmlContent);
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


</script>
</head>

<body>
	<div dojotype="dijit.layout.ContentPane" style="height:650px">
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
                        <button id="createObjWizardStartButton" type="button" style="float:right" dojoType="dijit.form.Button" onClick="javascript:scopeCreateObjectWiz.startSearch();" title="<fmt:message key="dialog.wizard.create.start" />"><fmt:message key="dialog.wizard.create.start" /></button>
                    </span>
                    <span id="createObjectWizardLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
                        <img src="img/ladekreis.gif" />
                    </span>
                </span>
            </div>
            <div class="fill"></div>
            <div id="resultsContainer"></div>
        </div>
    </div>
	
	</div>
</body>
</html>
