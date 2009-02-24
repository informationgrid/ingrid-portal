<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Verweise anlegen/bearbeiten</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">
var curSelectedObject = {};
var curSelectedUrl = {};
var dirtyFlag = null;

_container_.addOnLoad(function() {
	dirtyFlag = udkDataProxy.dirtyFlag;
	init();

	disableInputElementsOnWrongPermission();
});
_container_.addOnUnload(function() {
	acceptLinkList();
	dirtyFlag ? udkDataProxy.setDirtyFlag() : udkDataProxy.resetDirtyFlag();
});


var generalUiInputElements = [["linksFromFieldName", "linksFromFieldNameLabel"]];
var objUiInputElements = [["linksToObjectName", "linksToObjectNameLabel"],
						  ["linksToObjectClass", "linksToObjectClassLabel"]];

var urlUiInputElements = [["linksToName", "linksToNameLabel"],
						  ["linksToURL", "linksToURLLabel"],
						  ["linksToDataType", "linksToDataTypeLabel"]];

// All the possible values for the select box
var referenceMap = [["Basisdaten", "3210"],
					["Basisdaten", "3345"],
					["Datengrundlage", "3570"],
					["Fachliche Grundlage", "3520"],
					["Herstellungsprozess", "3515"],
					["Methode / Datengrundlage", "3100"],
					["Schl\u00fcsselkatalog", "3535"],
					["Symbolkatalog", "3555"],
					["Verweis zu Dienst", "5066"]];


setDirtyFlag = function() {
	dirtyFlag = true;
}

connectStoreWithDirtyFlag = function(store)
{
//	dojo.event.connect(store, "onSetData", this, "setDirtyFlag");
//	dojo.event.connect(store, "onClearData", this, "setDirtyFlag");
//	dojo.event.connect(store, "onAddDataRange", this, "setDirtyFlag");
	dojo.event.connect(store, "onAddData", this, "setDirtyFlag");
	dojo.event.connect(store, "onRemoveData", this, "setDirtyFlag");
	dojo.event.connect(store, "onUpdateField", this, "setDirtyFlag");
}

disableInputElementsOnWrongPermission = function() {
	if (currentUdk.writePermission == false) {
		dojo.widget.byId("newLinkButton").disable();
		dojo.widget.byId("saveButton").disable();
		dojo.widget.byId("resetButton").disable();
		dojo.byId("linksLinkType1").disabled = true;
		dojo.byId("linksLinkType2").disabled = true;
		dojo.widget.byId("linksFromFieldName").disable();
		dojo.widget.byId("linksToDescription").disable();

		// URL Input Fields
		dojo.widget.byId("linksToURL").disable();
		dojo.widget.byId("linksToDataType").disable();
		dojo.widget.byId("linksToDataVolume").disable();
		dojo.widget.byId("linksToURLType").disable();
		dojo.widget.byId("linksToIconURL").disable();
		dojo.widget.byId("linksToIconText").disable();
		dojo.widget.byId("linksToUrlDescription").disable();
		dojo.widget.byId("linksToName").disable();
	}
}

resetRequiredInputElements = function() {
	var resetRequiredState = function(widgetLabelList) {
		for (var i in widgetLabelList) {
			dojo.html.removeClass(dojo.byId(widgetLabelList[i][1]), "important");
		}
	}

	resetRequiredState(generalUiInputElements);
	resetRequiredState(objUiInputElements);
	resetRequiredState(urlUiInputElements);
}

validateInputElements = function() {
	var valid = true;
	var objSelected = dojo.byId("linksLinkType1").checked;
	var validate = function(widgetLabelList) {
		for (var i in widgetLabelList) {
			var val = dojo.widget.byId(widgetLabelList[i][0]).getValue();
	
			if (!val || val == "") {
				dojo.html.addClass(dojo.byId(widgetLabelList[i][1]), "important");
				valid = false;
			}
		}		
	}

	validate(generalUiInputElements);

	if (objSelected) {
		validate(objUiInputElements);
	} else {
		validate(urlUiInputElements);
	}
	return valid;
}


hideInputElements = function() {
	dojo.byId("inputFields").style.display = "none";
	dojo.byId("inputButtons").style.display = "none";
	dojo.widget.byId("linksFromFieldName").disable();
}

showInputElements = function() {
	dojo.byId("inputFields").style.display = "";
	dojo.byId("inputButtons").style.display = "";

	if (!_container_.customParams || !_container_.customParams.filter) {
		if (currentUdk.writePermission)
			dojo.widget.byId("linksFromFieldName").enable();
	}
}

function getRelationTypeForDisplayValue(dispVal) {
	var relationType = dojo.widget.byId("linksFromFieldName").getValueForDisplayValue(dispVal);
	if (relationType != null) {
		return relationType;
	} else {
		return -1;
	}
}

function getDisplayValueForRelationType(relType) {
	for (var i = 0; i < referenceMap.length; ++i) {
		if (referenceMap[i][1] == relType)
			return referenceMap[i][0];
	}
	return -1;
}


// Initialises the 'linksFromFieldName' select box depending on the current obj class and the filter id
function initReferenceWidget(filter) {
	var initialValues = [];
	if (filter) {
		dojo.lang.forEach(referenceMap, function(item) {
			if (item[1] == filter)
				initialValues.push(item);
		});
	} else {
		var objectClass = dojo.widget.byId("objectClass").getValue().substr(5, 1);

		var idList = [];
		switch (objectClass) {
			case "0": break; // Empty select box
			case "1": idList = ["3570", "3520", "3515", "3535", "3555", "5066"]; break;
			case "2": idList = ["3345"]; break;
			case "3": idList = ["3210"]; break;
			case "4": break; // Empty select box
			case "5": idList = ["3100"]; break;
			default: dojo.debug("Error: could not determine object class."); break;
		}
		initialValues = dojo.lang.filter(referenceMap, function(item) {
			return dojo.lang.some( idList, function(id) { return id == item[1]; } );
		});
	}
	dojo.widget.byId("linksFromFieldName").dataProvider.setData(initialValues);
}

init = function() {
	// reset the dialog
//	newLink();
	hideInputElements();
	resetRequiredInputElements();

	// Init static data
	var objectName = dojo.widget.byId("objectName").getValue();
	dojo.widget.byId("linksFromObjectName").setValue(objectName);

	// Data provider for resolving the object class values
	_container_.objectClassDP = new dojo.widget.basicComboBoxDataProvider({
		dataUrl: "js/data/objectclasses.js"
	});
	// Attach the neccessary functions to get Display Values for a given value
	_container_.objectClassDP.getDisplayValueForValue = function(value) {
		for (var i=0; i<this._data.length; i++) {
			if (this._data[i][1] == value) {
				return this._data[i][0];
			}
		}
	}
	_container_.objectClassDP.getValueForDisplayValue = function(dispValue) {
		for (var i=0; i<this._data.length; i++) {
			if (this._data[i][0] == dispValue) {
				return this._data[i][1];
			}
		}
	}


	// Connect both tables so only one value is selected at once. If the user selects an object the
	// current url is deselected, if an url is selected the object is deselected 
	var objectList = dojo.widget.byId("linkListObject");
	var urlList = dojo.widget.byId("linkListURL");

	// If a selected object was removed from the lists, reset the input fields
	dojo.event.connectOnce("after", objectList.store, "onRemoveData", function(obj) {	
		if (dojo.lang.some(objectList.getSelectedData(), function(item){ return (item == obj.src); }))
			resetInput();
	});
	dojo.event.connectOnce("after", urlList.store, "onRemoveData", function(url) {	
		if (dojo.lang.some(urlList.getSelectedData(), function(item){ return (item == url.src); }))
			resetInput();
	});

	// Init the radio buttons onclick functions
	dojo.byId("linksLinkType1").onclick = function() {
	    selectLinkType("obj");
		newLink();
	}
	dojo.byId("linksLinkType2").onclick = function() {
	    selectLinkType("url");
		newLink();
	}
	// Init the form
	dojo.byId("linkToObject").style.display = "block";
	dojo.byId("linkToURL").style.display = "none";


	// The selected value has to be displayed on select
	dojo.event.connectOnce("after", objectList, "onSelect", function() {
		// Update the displayed data
		var selectedObjects = objectList.getSelectedData();
		if (selectedObjects.length == 0) {
			curSelectedObject = {};
			return;
		} else if (selectedObjects.length == 1) {
			selectLinkType("obj");
			curSelectedUrl = {};
			curSelectedObject = selectedObjects[0];

			// Reset the URL List
			urlList.resetSelections();
			urlList.renderSelections();
//			dojo.debug("Relation type is "+curSelectedObject.relationType);
			if (curSelectedObject.relationType == -1) {
				dojo.widget.byId("linksFromFieldName").setValue(curSelectedObject.relationTypeName);
//				dojo.debug("setting value to "+curSelectedObject.relationTypeName);
			} else {
				dojo.widget.byId("linksFromFieldName").setValue(getDisplayValueForRelationType(curSelectedObject.relationType));
//				dojo.debug("setting value to "+getDisplayValueForRelationType(curSelectedObject.relationType));
			}
//			dojo.debug("value is: "+dojo.widget.byId("linksFromFieldName").getValue());

			dojo.widget.byId("linksToObjectName").setValue(curSelectedObject.title);
			dojo.widget.byId("linksToObjectClass").setValue(_container_.objectClassDP.getDisplayValueForValue("Class"+curSelectedObject.objectClass));
			dojo.widget.byId("linksToDescription").setValue(curSelectedObject.relationDescription);
		
			// Update the 'save' button text
			dojo.widget.byId("saveButton").setCaption(message.get("general.save"));
			showInputElements();
		} else {
			curSelectedObject = {};
			curSelectedUrl = {};
			hideInputElements(); 
			resetInputFields();
		}
	});

	// The selected value has to be displayed on select
	dojo.event.connectOnce("after", urlList, "onSelect", function() {
		// Update the displayed data
		var selectedUrls = urlList.getSelectedData();
		if (selectedUrls.length == 0) {
			curSelectedUrl = {};
			return;
		} else if (selectedUrls.length == 1) {
			selectLinkType("url");
			curSelectedObject = {};
			curSelectedUrl = selectedUrls[0];
			// Reset the obj List
			objectList.resetSelections();
			objectList.renderSelections();
			dojo.widget.byId("linksFromFieldName").setValue(curSelectedUrl.relationTypeName);
			if (curSelectedUrl.relationType == -1) {
				dojo.widget.byId("linksFromFieldName").setValue(curSelectedUrl.relationTypeName);
			} else {
				dojo.widget.byId("linksFromFieldName").setValue(getDisplayValueForRelationType(curSelectedUrl.relationType));
			}

			dojo.widget.byId("linksToURL").setValue(curSelectedUrl.url);
			dojo.widget.byId("linksToName").setValue(curSelectedUrl.name);
			dojo.widget.byId("linksToDataType").setValue(curSelectedUrl.datatype);
			dojo.widget.byId("linksToDataVolume").setValue(curSelectedUrl.volume);
			dojo.widget.byId("linksToURLType").setValue(curSelectedUrl.urlType);
			dojo.widget.byId("linksToIconURL").setValue(curSelectedUrl.iconUrl);
			dojo.widget.byId("linksToIconText").setValue(curSelectedUrl.iconText);
			dojo.widget.byId("linksToUrlDescription").setValue(curSelectedUrl.description);

			// Update the 'save' button text
			dojo.widget.byId("saveButton").setCaption(message.get("general.save"));
			showInputElements();
		} else {
			curSelectedObject = {};
			curSelectedUrl = {};
			hideInputElements(); 
			resetInputFields();
		}
	});

	// Populate the object and url links list
	var srcStore = dojo.widget.byId("linksTo").store;
	var objStore = objectList.store;
	var urlStore = urlList.store;
	var linkData = srcStore.getData();
	var objLinks = [];
	var urlLinks = [];
	dojo.lang.forEach(linkData, function(link){
		if (link.url) {
			urlLinks.push(link);
		} else {
			objLinks.push(link);
		}
	});
	objStore.setData(objLinks);
	urlStore.setData(urlLinks);

	// Check if a filter was passes as an argument
	if (_container_.customParams && _container_.customParams.filter) {	
		// Filter the list according to the filter arg
		var filterFunc = function(relationType) { return (relationType == _container_.customParams.filter); };
		objectList.setFilter("relationType", filterFunc);
		urlList.setFilter("relationType", filterFunc);
	
		objectList.applyFilters();	
		objectList.render();
		urlList.applyFilters();	
		urlList.render();

		// Disable the type input and set it to the filter
		var referenceWidget = dojo.widget.byId("linksFromFieldName");
		referenceWidget.disable();

		var filterStr = _container_.customParams.filter+'';
		initReferenceWidget(filterStr);
		referenceWidget.setValue(referenceWidget.getDisplayValueForValue(filterStr));
	} else {
		// Load the initial values from the backend
		initReferenceWidget();
		dojo.widget.byId("linksFromFieldName").setValue("");
	}

	// Connect the object and url store with the internal dirty flag
	connectStoreWithDirtyFlag(objStore);
	connectStoreWithDirtyFlag(urlStore);
};

saveLink = function() {
	var objStore = dojo.widget.byId("linkListObject").store;
	var urlStore = dojo.widget.byId("linkListURL").store;
	var objData = dojo.widget.byId("linkListObject").getSelectedData()[0];
	var urlData = dojo.widget.byId("linkListURL").getSelectedData()[0];
	var objSelected = dojo.byId("linksLinkType1").checked;

	resetRequiredInputElements();

	if (!validateInputElements()) {
  		dialog.show(message.get("general.hint"), message.get("links.fillRequiredFieldsHint"), dialog.WARNING);
		return;
	}

	// Disallow links to self
	if (objSelected && curSelectedObject.uuid == currentUdk.uuid) {
  		dialog.show(message.get("general.hint"), message.get("links.noLinkToSelfHint"), dialog.WARNING);
		return;		
	}

	if (objData) {
		// If a node was selected we have to update the information
		// The dojo FilteringTable does not like updates on values that are not displayed
		// It simply throws a cryptic error on update. The connected 'onUpdateField' function in
		// FilteringTable.init tries to get a row idx which is 'undefined'
//		store.update(data, "uuid", curSelectedObject.uuid);
//		store.update(data, "relationDescription", dojo.widget.byId("linksToDescription").getValue());
//		store.update(data, "objectClass", curSelectedObject.objectClass);
		objData.uuid = curSelectedObject.uuid;
		objData.relationDescription = dojo.widget.byId("linksToDescription").getValue();
		objData.objectClass = curSelectedObject.objectClass;
		var typeName = dojo.widget.byId("linksFromFieldName").getValue();
		objData.relationType = getRelationTypeForDisplayValue(typeName);
		if (objData.relationType == -1) {
			objData.relationTypeName = typeName;
		}
		UtilList.addIcons([objData]);
		objStore.update(objData, "icon", objData.icon);
		objStore.update(objData, "title", curSelectedObject.title);
	} else if (urlData) {
		var newUrl = _getUrl();
		urlData.url = newUrl.url;
		urlData.datatype = newUrl.datatype;
		urlData.volume = newUrl.volume;
		urlData.urlType = newUrl.urlType;
		urlData.iconUrl = newUrl.iconUrl;
		urlData.iconText = newUrl.iconText;
		urlData.description = newUrl.description;
		urlData.relationType = newUrl.relationType;
		if (newUrl.relationType == -1) {
			urlData.relationTypeName = newUrl.relationTypeName;
		}
		UtilList.addIcons([urlData]);
		urlStore.update(urlData, "name", newUrl.name);
	} else if (objSelected && curSelectedObject) {
		// Otherwise a new link has to be created
		// Take the current selected object and add the values that were entered in the ui fields
		curSelectedObject.relationDescription = dojo.widget.byId("linksToDescription").getValue();

		var typeName = dojo.widget.byId("linksFromFieldName").getValue();
		curSelectedObject.relationType = getRelationTypeForDisplayValue(typeName);
		if (curSelectedObject.relationType == -1) {
			curSelectedObject.relationTypeName = typeName;
		}

		// No checks if the store already contains the current element.
		curSelectedObject.Id = _getNewKey();
		UtilList.addIcons([curSelectedObject]);
		objStore.addData(curSelectedObject);
		// Select the object in the list
		dojo.widget.byId("linkListObject").resetSelections();
		dojo.widget.byId("linkListObject").select(curSelectedObject);
		dojo.widget.byId("linkListObject").renderSelections();
		dojo.widget.byId("saveButton").setCaption(message.get("general.save"));
	} else if (!objSelected) {
		var newUrl = _getUrl();
		newUrl.Id = _getNewKey();
		UtilList.addIcons([newUrl]);
		urlStore.addData(newUrl);
		dojo.widget.byId("linkListURL").resetSelections();
		dojo.widget.byId("linkListURL").select(newUrl);
		dojo.widget.byId("linkListURL").renderSelections();
		dojo.widget.byId("saveButton").setCaption(message.get("general.save"));
	} else {
  		dialog.show(message.get("general.hint"), message.get("links.fillRequiredFieldsHint"), dialog.WARNING);
		return;
	}
}

_getUrl = function() {
	var newUrl = {};
	newUrl.url = dojo.widget.byId("linksToURL").getValue();
	newUrl.datatype = dojo.widget.byId("linksToDataType").getValue();
	newUrl.volume = dojo.widget.byId("linksToDataVolume").getValue();
	newUrl.urlType = dojo.widget.byId("linksToURLType").getValue();
	newUrl.iconUrl = dojo.widget.byId("linksToIconURL").getValue();
	newUrl.iconText = dojo.widget.byId("linksToIconText").getValue();
	newUrl.description = dojo.widget.byId("linksToUrlDescription").getValue();

	var typeName = dojo.widget.byId("linksFromFieldName").getValue();
	newUrl.relationType = getRelationTypeForDisplayValue(typeName);
	if (newUrl.relationType == -1) {
		newUrl.relationTypeName = typeName;
	}

	newUrl.name = dojo.widget.byId("linksToName").getValue();
	return newUrl;
}

// iterates over all entries in the stores and returns a key that is not in use yet
_getNewKey = function() {
	var objStore = dojo.widget.byId("linkListObject").store;
	var urlStore = dojo.widget.byId("linkListURL").store;

    var objKey = UtilStore.getNewKey(objStore);
    var urlKey = UtilStore.getNewKey(urlStore);

	return (objKey > urlKey ? objKey : urlKey);
}


// 'New Link' Button onClick function.
//
// Resets the list selection and currently selected objects/urls
newLink = function() {
	showInputElements();
	// Reset object and url selections
	var objectList = dojo.widget.byId("linkListObject");
	var urlList = dojo.widget.byId("linkListURL");
	objectList.resetSelections();
	objectList.renderSelections();
	urlList.resetSelections();
	urlList.renderSelections();

	resetInputFields();
}

showAssignObjectDialog = function() {
	if (currentUdk.writePermission == false) {
		return;
	}

	var deferred = new dojo.Deferred();
	var setSelectedObject = function(obj) {
		if (obj.uuid != "objectRoot") {
			curSelectedObject = obj;
			dojo.widget.byId("linksToObjectName").setValue(curSelectedObject.title);
			dojo.widget.byId("linksToObjectClass").setValue(_container_.objectClassDP.getDisplayValueForValue("Class"+curSelectedObject.objectClass));
		}
	}

	deferred.addCallback(setSelectedObject);

	if (curSelectedObject) {
		dialog.showPage(message.get("dialog.links.selectObject.title"), 'mdek_links_select_object_dialog.jsp', 522, 520, true,{
			// custom parameters
			resultHandler: deferred,
			jumpToNode: curSelectedObject.uuid
		});
	} else {
		dialog.showPage(message.get("dialog.links.selectObject.title"), 'mdek_links_select_object_dialog.jsp', 522, 520, true,{
			// custom parameters
			resultHandler: deferred
		});
	}
}

function selectLinkType(e)
{
  if (this.id == "linksLinkType2" || e == "url")
  {
    document.getElementById("linkToObject").style.display = "none";
    document.getElementById("linkToURL").style.display = "block";
    document.getElementById("linksLinkType1").checked = false;
    document.getElementById("linksLinkType2").checked = true;
  }
  else
  {
    document.getElementById("linkToObject").style.display = "block";
    document.getElementById("linkToURL").style.display = "none";
    document.getElementById("linksLinkType1").checked = true;
    document.getElementById("linksLinkType2").checked = false;
  }
}


resetInputFields = function() {
	// Reset field values
	curSelectedObject = {};
	curSelectedUrl = {};
	
	if (!_container_.customParams || !_container_.customParams.filter) {
		if (currentUdk.writePermission) {
			dojo.widget.byId("linksFromFieldName").enable();
		}
//		dojo.widget.byId("linksFromFieldName").setValue("");
	}

	dojo.widget.byId("linksToObjectName").setValue("");
	dojo.widget.byId("linksToObjectClass").setValue("");
	dojo.widget.byId("linksToDescription").setValue("");	
	dojo.widget.byId("linksToURL").setValue("http://");
	dojo.widget.byId("linksToName").setValue("");
	dojo.widget.byId("linksToDataType").setValue("");
	dojo.widget.byId("linksToDataVolume").setValue("");
	dojo.widget.byId("linksToURLType").setValue("");
	dojo.widget.byId("linksToIconURL").setValue("");
	dojo.widget.byId("linksToIconText").setValue("");
	dojo.widget.byId("linksToUrlDescription").setValue("");	

	// Change 'save' Button text
	dojo.widget.byId("saveButton").setCaption(message.get("general.add"));
}

// Cancel Button onClick function
//
resetInput = function() {
	newLink();
	hideInputElements();
	resetRequiredInputElements();

	if (!_container_.customParams || !_container_.customParams.filter) {
		dojo.widget.byId("linksFromFieldName").setValue("");
	}
}

// Accept Button onClick function.
//
// This function copies the object links list to the main mdek linksTo list
acceptLinkList = function() {
	var objData = dojo.widget.byId("linkListObject").store.getData();
	var urlData = dojo.widget.byId("linkListURL").store.getData();
	var destStore = dojo.widget.byId("linksTo").store;

	UtilList.addObjectLinkLabels(objData);
	UtilList.addUrlLinkLabels(urlData);

	destStore.setData(objData.concat(urlData));

//	_container_.closeWindow();
}

closeDialog = function() {
	_container_.closeWindow();
}

</script>
</head>

<body>

<div dojoType="ContentPane">

<div id="links" class="contentBlockWhite top wideBlock">
<div id="winNavi"><a href="javascript:closeDialog();"
	title="schlie&szlig;en"><img src="img/ic_close.gif" /><fmt:message
	key="dialog.links.close" /></a> <a href="javascript:void(0);"
	onclick="javascript:dialog.showContextHelp(arguments[0], 7033)"
	title="Hilfe">[?]</a></div>
<div id="linksContent" class="content"><!-- LEFT HAND SIDE CONTENT START -->
<div class="spacer"></div>
<div class="inputContainer field grey noSpaceBelow fullField"><span
	class="label"><label
	onclick="javascript:dialog.showContextHelp(arguments[0], 7034, 'Verweis von')"><fmt:message
	key="dialog.links.source" /></label></span>
<div class="outlined w616"><span class="label"><label
	for="linksFromObjectName"
	onclick="javascript:dialog.showContextHelp(arguments[0], 7035, 'Objektname')"><fmt:message
	key="dialog.links.objTitle" /></label></span> <span class="input spaceBelow"><input
	type="text" id="linksFromObjectName" name="linksFromObjectName"
	class="w608" disabled="true" dojoType="ingrid:ValidationTextBox" /></span> <span
	class="label required"><label id="linksFromFieldNameLabel"
	for="linksFromFieldName"
	onclick="javascript:dialog.showContextHelp(arguments[0], 2000, 'Feldname/Bezeichnung der Verweisbeziehung')"><fmt:message
	key="dialog.links.link" />*</label></span> <span class="input">
<div dojoType="ingrid:ComboBox" maxlength="80" autoComplete="false"
	toggle="plain" style="width: 590px;" id="linksFromFieldName"></div>
</span></div>
<span class="button transparent w644" style="height: 20px !important;">
<span style="float: right; padding-right: 16px;">
<button dojoType="ingrid:Button" id="newLinkButton" onClick="newLink"><fmt:message
	key="dialog.links.new" /></button>
</span> </span> <span id="inputFields" style="display: none;"> <span
	class="label required"><label for="linksLinkType"
	onclick="javascript:dialog.showContextHelp(arguments[0], 7037, 'Verweistyp')"><fmt:message
	key="dialog.links.linkType" />*</label></span>
<div class="checkboxContainer"><span><input type="radio"
	id="linksLinkType1" class="radio" checked /><label class="inActive"
	for="linksLinkType1"><fmt:message key="dialog.links.object" /></label></span>
<span><input type="radio" id="linksLinkType2" class="radio" /><label
	class="inActive" for="linksLinkType2"><fmt:message
	key="dialog.links.urlTitle" /></label></span></div>

<div class="spacer"></div>
<span class="label"><label
	onclick="javascript:dialog.showContextHelp(arguments[0], 7038, 'Verweis auf')"><fmt:message
	key="dialog.links.target" /></label></span> <!-- VERWEIS AUF OBJEKT -->
<div id="linkToObject" class="outlined w616"><span
	class="label required"><label id="linksToObjectNameLabel"
	for="linksToObjectName"
	onclick="javascript:dialog.showContextHelp(arguments[0], 2100, 'Objektname')"><fmt:message
	key="dialog.links.objTitle" />*</label></span> <span class="functionalLink"><img
	src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a
	id="assignObjectDialogLink" href="javascript:void(0);"
	onClick="javascript:showAssignObjectDialog();"
	title="Objekt ausw&auml;hlen [Popup]"><fmt:message
	key="dialog.links.selectObject" /></a></span> <span class="input spaceBelow"><input
	type="text" id="linksToObjectName" name="linksToObjectName"
	class="w608" disabled="true" dojoType="ingrid:ValidationTextBox" /></span> <span
	class="label required"><label id="linksToObjectClassLabel"
	for="linksToObjectClass"
	onclick="javascript:dialog.showContextHelp(arguments[0], 7040, 'Objektklasse')"><fmt:message
	key="dialog.links.objClass" />*</label></span> <span class="input spaceBelow"><input
	type="text" id="linksToObjectClass" name="linksToObjectClass"
	class="w608" disabled="true" dojoType="ingrid:ValidationTextBox" /></span> <span
	class="label"><label for="linksToDescription"
	onclick="javascript:dialog.showContextHelp(arguments[0], 2110, 'Erl&auml;uterungen')"><fmt:message
	key="dialog.links.objDescription" /></label></span> <span class="input"><input
	type="text" mode="textarea" id="linksToDescription"
	name="linksToDescription" class="w608 h038"
	dojoType="ingrid:ValidationTextbox" /></span></div>
<!-- VERWEIS AUF URL -->
<div id="linkToURL" class="outlined w616"><span
	class="label required"><label id="linksToNameLabel"
	for="linksToName"
	onclick="javascript:dialog.showContextHelp(arguments[0], 2210, 'Bezeichnung des Verweises')"><fmt:message
	key="dialog.links.urlDescription" />*</label></span> <span class="input spaceBelow"><input
	type="text" maxlength="255" id="linksToName" name="linksToName"
	class="w608" dojoType="ingrid:ValidationTextBox" /></span> <span
	class="label required"><label id="linksToURLLabel"
	for="linksToURL"
	onclick="javascript:dialog.showContextHelp(arguments[0], 2200, 'Internet-Adresse (URL)')"><fmt:message
	key="dialog.links.url" />*</label></span> <span class="input spaceBelow"><input
	type="text" maxlength="255" id="linksToURL" name="linksToURL"
	class="w608" dojoType="ingrid:ValidationTextBox" /></span>

<div class="inputContainer"><span class="entry first"> <span
	class="label required"><label id="linksToDataTypeLabel"
	for="linksToDataType"
	onclick="javascript:dialog.showContextHelp(arguments[0], 2240, 'Datentyp')"><fmt:message
	key="dialog.links.dataType" />*</label></span> <span class="input">
<div dojoType="ingrid:ComboBox" maxlength="40" toggle="plain"
	dataUrl="js/data/datatypes.js" style="width: 274px;"
	widgetId="linksToDataType"></div>
</span> </span> <span class="entry"> <span class="label"><label
	for="linksToDataVolume"
	onclick="javascript:dialog.showContextHelp(arguments[0], 2220, 'Datenvolumen')"><fmt:message
	key="dialog.links.dataSize" /></label></span> <span class="input"><input
	type="text" id="linksToDataVolume" maxlength="20"
	name="linksToDataVolume" class="w134"
	dojoType="ingrid:ValidationTextBox" /></span> </span> <span class="entry"> <span
	class="label"><label for="linksToURLType"
	onclick="javascript:dialog.showContextHelp(arguments[0], 2251, 'URL-Typ')"><fmt:message
	key="dialog.links.urlType" /></label></span> <span class="input">
<div dojoType="ingrid:Select" autoComplete="false" toggle="plain"
	dataUrl="js/data/urlReferenceTypes.js" style="width: 116px;"
	widgetId="linksToURLType"></div>
</span> </span>
<div class="fill"></div>
</div>
<div class="inputContainer spaceBelow"><span class="entry first">
<span class="label"><label for="linksToIconURL"
	onclick="javascript:dialog.showContextHelp(arguments[0], 2250, 'Icon-URL')"><fmt:message
	key="dialog.links.urlIcon" /></label></span> <span class="input"><input
	type="text" id="linksToIconURL" maxlength="255" name="linksToIconURL"
	class="w292" dojoType="ingrid:ValidationTextBox" /></span> </span> <span
	class="entry"> <span class="label"><label
	for="linksToIconText"
	onclick="javascript:dialog.showContextHelp(arguments[0], 2230, 'Icon-Text')"><fmt:message
	key="dialog.links.urlIconText" /></label></span> <span class="input"><input
	type="text" id="linksToIconText" maxlength="80" name="linksToIconText"
	class="w292" dojoType="ingrid:ValidationTextBox" /></span> </span>
<div class="fill"></div>
</div>

<span class="label"><label for="linksToUrlDescription"
	onclick="javascript:dialog.showContextHelp(arguments[0], 2260, 'Erl&auml;uterungen')"><fmt:message
	key="dialog.links.objDescription" /></label></span> <span class="input"><input
	type="text" mode="textarea" id="linksToUrlDescription"
	name="linksToUrlDescription" class="w608 h038"
	dojoType="ingrid:ValidationTextbox" /></span></div>
<div class="spacerField"></div></div>
</span> <span id="inputButtons" style="display: none;">
<div class="inputContainer full noSpaceBelow"><span
	class="button w644" style="height: 20px !important;"> <span
	style="float: right;">
<button dojoType="ingrid:Button" id="resetButton" onClick="resetInput"><fmt:message
	key="dialog.links.cancel" /></button>
</span> <!-- 
        	<span style="float:right; padding-right:5px;"><button dojoType="ingrid:Button" onClick="acceptLinkList">&Auml;nderungen &uuml;bernehmen</button></span>
 --> <span style="float: right; padding-right: 5px;">
<button id="saveButton" dojoType="ingrid:Button" onClick="saveLink"><fmt:message
	key="dialog.links.add" /></button>
</span> </span></div>
</span> <!-- LEFT HAND SIDE CONTENT END --> <!-- RIGHT HAND SIDE CONTENT START -->
<div id="listLinks" class="inputContainer"><span class="label"><label
	class="inActive" for="linkList"><fmt:message
	key="dialog.links.list" /></label></span>

<div dojoType="ContentPane" class="scrollable"><span class="label"><label
	class="inActive" for="linkListObject"><fmt:message
	key="dialog.links.objects" /></label></span>


<div class="tableContainer headHiddenRows9 third">
<table id="linkListObject" dojoType="ingrid:FilteringTable" minRows="9"
	headClass="hidden" cellspacing="0"
	class="filteringTable nosort interactive relativePos">
	<thead>
		<tr>
			<th field="icon" width="30">&nbsp;</th>
			<th field="title" width="290">&nbsp;</th>
		</tr>
	</thead>
	<colgroup>
		<col width="30">
		<col width="290">
	</colgroup>
	<tbody>
	</tbody>
</table>

</div>

<div class="spacer"></div>

<div dojoType="ContentPane" class="scrollable"><span class="label"><label
	for="linkListURL" class="inActive">URL</label></span>

<div class="tableContainer headHiddenRows9 third">

<table id="linkListURL" dojoType="ingrid:FilteringTable" minRows="9"
	headClass="hidden" cellspacing="0"
	class="filteringTable nosort interactive relativePos">
	<thead>
		<tr>
			<th nosort="true" field="icon" dataType="String" width="30">&nbsp;</th>
			<th nosort="true" field="name" dataType="String" width="290">&nbsp;</th>
		</tr>
	</thead>
	<colgroup>
		<col width="30">
		<col width="290">
	</colgroup>
	<tbody>
	</tbody>
</table>
</div>
</div>

</div>
<!-- RIGHT HAND SIDE CONTENT END --></div>
</div>
</div>
</body>
</html>
