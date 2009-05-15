<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Raumbezug festlegen</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<style type="text/css">
.floatLeft {float:left;}
.bottomRight { margin-top: -50px !important; margin-left: 373px; }
html>/**/body .bottomRight { margin-top: 58px !important; margin-left: 373px; }
</style>

<script type="text/javascript">
_container_.addOnLoad(function() {
 		init();
});
_container_.addOnUnload(function() {
 		deleteComboboxWidgets();
});
init = function() {
	// Enter key on the ValdiationTextbox has to start a search:
	var inputField = dojo.widget.byId("locationTextBox");
    dojo.event.connect(inputField.domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER) {
                findLocationTopics();
            }
        });
}

function disableUiElements() {
	dojo.widget.byId("addLocationTopicsButton").disable();
	dojo.widget.byId("findLocationTopicsButton").disable();
}
function enableUiElements() {
	dojo.widget.byId("addLocationTopicsButton").enable();
	dojo.widget.byId("findLocationTopicsButton").enable();
}

function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("spatialLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("spatialLoadingZone"), "hidden");
}


var checkboxArray = new Array();


function showNoResults() {
	var resultDiv = dojo.byId("resultList");
	if (resultDiv) {
		resultDiv.innerHTML = message.get("spatial.noResultsHint");
	}	
	dojo.byId("resultLabel").style.visibility = "hidden";
}

function showLoading() {
	var resultDiv = dojo.byId("resultList");
	if (resultDiv) {
//		resultDiv.innerHTML = message.get("spatial.loadingHint");
		resultDiv.innerHTML = "";
	}	
}

function showError() {
	var resultDiv = dojo.byId("resultList");
	if (resultDiv) {
		resultDiv.innerHTML = message.get("spatial.connectionError");
	}	
}

deleteComboboxWidgets = function() {
	while (checkboxArray.length > 0) {
		var checkBox = checkboxArray.pop();
		checkBox.destroy();
	}
}

resetResultDiv = function() {
	var resultDiv = dojo.byId("resultList");
	if (resultDiv) {
		while (resultDiv.lastChild)
			resultDiv.removeChild(resultDiv.lastChild);
	}
	dojo.byId("resultLabel").style.visibility = "hidden";
}

setResultList = function(topicList)
{
	if (topicList != null && topicList.length > 0) {
		resetResultDiv();
		for (var i in topicList) {
			var checkboxDiv = dojo.byId("resultList");
			if (checkboxDiv) {
				var label = topicList[i].name+", "+topicList[i].type;
				var checkBox = dojo.widget.createWidget("checkbox", {
						id: topicList[i].topicId,
						name: label,
						topic: topicList[i]
					});
				checkboxArray.push(checkBox);
				var divElement = document.createElement("div");

				var linkElement = document.createElement("a"); 
				linkElement.setAttribute("href", "javascript:void(0);");
				linkElement.topicId = topicList[i].topicId;
				linkElement.onclick = function() {
					findAssociatedLocations(this.topicId);
				}
				linkElement.setAttribute("href", "javascript:void(0);");
				linkElement.innerHTML = label;

				checkboxDiv.appendChild(divElement);
				divElement.appendChild(checkBox.domNode);
				divElement.appendChild(linkElement);
			}
		}
		dojo.byId("resultLabel").style.visibility = "visible";
	} else {
		showNoResults();
	}
}

// This function queries the SNSService for similar location topics
findAssociatedLocations = function(topicId) {
	resetResultDiv();
	deleteComboboxWidgets();
	showLoading();

	SNSService.getLocationTopicsById(topicId, {
		preHook: function() { showLoadingZone(); disableUiElements(); },
		postHook: function() { hideLoadingZone(); enableUiElements(); },
		callback:setResultList,
		timeout:0,
		errorHandler:showError
	});	
}

// 'Search Button' onClick function
// This function queries the SNSService for location topics
findLocationTopics = function() {
	var queryTerm = dojo.widget.byId("locationTextBox").getValue();
	
	// If input is blank, do nothing
	queryTerm = dojo.string.trim(queryTerm);
	if (queryTerm.length == 0)
		return;

	resetResultDiv();
	deleteComboboxWidgets();
	showLoading();

	SNSService.getLocationTopics(queryTerm, "beginsWith", null, {
		preHook: function() { showLoadingZone(); disableUiElements(); },
		postHook: function() { hideLoadingZone(); enableUiElements(); },
		callback:setResultList,
		timeout:0,
		errorHandler:showError
	});
}

// 'Add Button' onClick function
// This function adds the location topics to the main geothesaurus table
addLocationTopics = function() {
	var store = dojo.widget.byId("spatialRefAdminUnit").store;

	dojo.lang.forEach(checkboxArray, function(item) {
		if (!item.checked) {
			return;
		}

		var topic = item.topic;
		var topicId = item.topic.topicId;

		var storedTopic = null;
		var storeData = store.getData();
		for (var i = 0; i < storeData.length; ++i) {
			if (storeData[i].topicId == topicId) {
				storedTopic = storeData[i];
				continue;
			}
		}

		if (storedTopic) {
			// Element is already in the table. Update the topic.
			// Update elements that are displayed in the table
			store.update(storedTopic, "label", topic.name+", "+topic.type);
			if (topic.boundingBox) {
				store.update(storedTopic, "longitude1", topic.boundingBox[0]);
				store.update(storedTopic, "latitude1", topic.boundingBox[1]);
				store.update(storedTopic, "longitude2", topic.boundingBox[2]);
				store.update(storedTopic, "latitude2", topic.boundingBox[3]);
			}
			// Update elements that aren't displayed in the table
			storedTopic.nativeKey = topic.nativeKey;
			storedTopic.name = topic.name;
			storedTopic.topicType = topic.type;
			storedTopic.topicTypeId = topic.typeId;

		} else {
			// The topic was not found in the result list. Create a new key and
			// add the topic to the list
			var key = UtilStore.getNewKey(store);
			if (topic.boundingBox) {
				store.addData({
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
				store.addData({
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
	});
	_container_.closeWindow();
}

</script>
</head>

<body>

<div dojoType="ContentPane">

	<div id="catalogueSpatialRef" class="contentBlockWhite top">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=maintanance-of-objects-3#maintanance-of-objects-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="Hilfe">[?]</a>
		</div>
		<div id="spatialRefContent" class="content">

			<div class="spacer"></div>
			<div class="spacer"></div>
			<!-- CONTENT START -->
			<div class="inputContainer w478 noSpaceBelow">
        		<span class="label"><label for="locationTextBox" onclick="javascript:dialog.showContextHelp(arguments[0], 7021, 'R&auml;umliche Einheit festlegen')"><fmt:message key="dialog.spatialNavigator.title" /></label></span>
       			<div class="input spaceBelow">
       				<span style="position:relative; float:left; width:292px;">
       					<input type="text" id="locationTextBox" size="20" name="locationTextBox" class="w292" dojoType="ingrid:ValidationTextBox" />
					</span>
					<span style="float:right;">
       					<button dojoType="ingrid:Button" title="In Geo-Thesaurus suchen" id="findLocationTopicsButton" onClick="findLocationTopics"><fmt:message key="dialog.spatialNavigator.search" /></button>
       				</span>
        		</div>

        		<div>&nbsp</div>
        
        		<span id="resultLabel" class="label" style="visibility:hidden;"><fmt:message key="dialog.spatialNavigator.select" /></span>

				<span id="spatialLoadingZone" style="float:right; z-index: 100; visibility:hidden;">
					<img src="img/ladekreis.gif" />
				</span>

        		<span class="floatLeft">
        			<div class="checkboxContainer" id="resultList" style="width: 364px; height: 90px; overflow: auto;"></div>
        		</span>
          		<span class="button w085 transparent bottomRight">
					<button dojoType="ingrid:Button" title="Übernehmen" id="addLocationTopicsButton" onClick="addLocationTopics"><fmt:message key="dialog.spatialNavigator.apply" /></button>          		
          		</span>
			</div>
      		<!-- CONTENT END -->
		</div>
	</div>
</div>

</body>
</html>
