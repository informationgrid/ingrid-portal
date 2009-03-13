<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">

var scriptScope = this;

var MAINTAINABLE_LIST_IDS = [101, 102, 515, 518, 520, 523, 526, 528, 1100, 1320, 1350, 1370, 3385, 3535, 3555];
var CAN_SET_DEFAULT_LIST_IDS = [100, 518, 523, 525, 526, 527, 1350, 1370, 3385, 3571, 4300, 4305, 5100, 5200, 99999999];

_container_.addOnLoad(function() {
	initCodelistSelect();
	initCodelistTables();

	initFreeEntrySelect();
	initFreeEntryTables();
});

function initCodelistSelect() {
	var selectWidget = dojo.widget.byId("selectionList");

	var def = getAllSysListIdsDef();
	def.addCallback(function(listIds) {
		var selectWidgetData = [];
		for (var index = 0; index < listIds.length; ++index) {
			var name = UtilCatalog.getNameForSysList(listIds[index]);
			selectWidgetData.push([name + " (" + listIds[index] + ")", listIds[index]+""]);
		}
		selectWidget.dataProvider.setData(selectWidgetData);
	});
	def.addErrback(function(error) {
		dojo.debug("Error: " + error);
		dojo.debugShallow(error);
	});

	// On value changed load the selected sysList from the backend and update the table
	dojo.event.connect(selectWidget, "onValueChanged", function(value) {
		if (value) {
			var germanListDef = getSysListDef(value, "de");
			var englishListDef = getSysListDef(value, "en");

			if (dojo.lang.some(MAINTAINABLE_LIST_IDS, function(listId) { return listId == parseInt(value); } )) {
				dojo.debug("enable. ("+parseInt(value)+")");
				dojo.widget.byId("codeListTable11").enable();
				dojo.widget.byId("codeListTable12").enable();
				hideEditDisabledHint();

			} else {
				dojo.debug("disable. ("+parseInt(value)+")");
				dojo.widget.byId("codeListTable11").disable();
				dojo.widget.byId("codeListTable12").disable();
				showEditDisabledHint();
			}

			var defList = new dojo.DeferredList([germanListDef, englishListDef], false, false, true);
			defList.addCallback(function (resultList) {
				var germanList = resultList[0][1]; 
				var englishList = resultList[1][1];

				var germanData = convertSysListToTableData(germanList);
				var englishData = convertSysListToTableData(englishList);

				var mergedData = mergeTableData(germanData, englishData);
				updateCodelistTable(mergedData);
			});
		}
	});
}

function initCodelistTables() {
	// Use the same store for both tables. The First table has to be reinitialised so the new store
	// gets registered properly
	var mainStore = dojo.widget.byId("codeListTable12").store;
	
	dojo.widget.byId("codeListTable11").store = mainStore;
	dojo.widget.byId("codeListTable11").initialize();

	// We need to connect 'before' the function is called so the field is updated properly
	// by the filteringTable
	dojo.event.connect("before", mainStore, "onAddData", function(obj) {
		dojo.debugShallow(obj.src);
		var radioButton = createHtmlForRadio("codeListRadio", "codeListRadio_" + (new Date()).getTime(), false);
		obj.src.isDefault = radioButton;
	});
}


// Retrieve all sysList ids stored in the backend
// A list of the following form is returned:
// [ listId1, listId2, ... ]
function getAllSysListIdsDef() {
	var def = new dojo.Deferred();
	CatalogService.getAllSysListIds({
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(listIds) {
			def.callback(listIds);
		},
		errorHandler: function(msg, err) {
			hideLoadingZone();
			dojo.debug("Error: "+msg);
			dojo.debugShallow(err);
			def.errback(err);
		}
	});
	return def;
}

// Retrieve the sysList for the given listId and languageCode from the backend
// A list of the following form is returned:
// [ [listEntry, entryId, isDefault], [...] ]
function getSysListDef(listId, languageCode) {
	var def = new dojo.Deferred();
	CatalogService.getSysLists([listId], languageCode, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(listItems) {
			def.callback(listItems[listId]);
		},
		errorHandler: function(msg, err) {
			hideLoadingZone();
			dojo.debug("Error: "+msg);
			dojo.debugShallow(err);
			def.errback(err);
		}
	});
	return def;
}

// Input is a list of string arrays of the following form:
// [ [listEntry, entryId, isDefault], [...] ]
// e.g [ ["Inch", "4", "N"], ["Meter", "9001", "N"], ["Fuss", "9002", "N"], ["Kilometer", "9036", "N"] ]
// The codelist table is being overwritten
function convertSysListToTableData(listItems) {
	// Prepare the list items for display
	var listData = [];
	for (var index = 0; index < listItems.length; ++index) {
		listData.push( {
			name: listItems[index][0],
			entryId: listItems[index][1],
			isDefault: listItems[index][2] == "Y"
		});
	}

	return listData;
}

// Merge two lists into one. The lists must have the following format:
// [ { entryId:entryId, name:entryName isDefault:default }, {...} ]
// The result is of the format:
// [ { entryId:entryId, deName:entryName, enName:entryName, isDefault:default }, {...} ]
function mergeTableData(germanList, englishList) {
	// Create a hash map referenced by entry ids
	var idEntryMap = {};

	// Fill the hash map with all german entries
	for (var index = 0; index < germanList.length; index++) {
		var currentEntry = germanList[index];
		idEntryMap[currentEntry.entryId] = { deName: currentEntry.name, entryId: currentEntry.entryId, isDefault: currentEntry.isDefault };
	}

	// add the english name if the corresponding entry id was found in the hash
	// otherwise create a new entry
	for (var index = 0; index < englishList.length; index++) {
		var currentEntry = englishList[index];
		if (idEntryMap[currentEntry.entryId]) {
			// Entry found in hash, only update enName
			idEntryMap[currentEntry.entryId].enName = currentEntry.name;

		} else {
			// New entry
			idEntryMap[currentEntry.entryId] = { enName: currentEntry.name, entryId: currentEntry.entryId, isDefault: currentEntry.isDefault };
		}
	}

	// Convert hash map to table
	var mergedData = [];
	for (var entry in idEntryMap) {
		mergedData.push(idEntryMap[entry]);
	}

	mergedData = mergedData.sort(function(a, b) { return parseInt(a.entryId) - parseInt(b.entryId); });
	return mergedData;
}

// Prepare the lists for display (add indices and checkboxes) and overwrite the codeListTables list entries
// Data must be an array containing entries (objects) of the following form:
// [ { entryId:entryId, deName:entryName, enName:entryName, isDefault:default }, {...} ]
function updateCodelistTable(data) {
	UtilList.addTableIndices(data);
	var sysListId = parseInt(dojo.widget.byId("selectionList").getValue());

	if (dojo.lang.some(CAN_SET_DEFAULT_LIST_IDS, function(listId) { return listId == sysListId; })) {
		hasDefaultEntry(data)? showDefaultRadioButtons() : hideDefaultRadioButtons();
		enableDefaultCheckbox();

	} else {
		hideDefaultRadioButtons();
		disableDefaultCheckbox();
	}

	// Add radio buttons
	for (var index = 0; index < data.length; index++) {
		data[index].isDefault =
			createHtmlForRadio(
					"codeListRadio",
					"codeListRadio_"+data[index].entryId,
					data[index].isDefault);
	}

	// Both stores are the same
	dojo.widget.byId("codeListTable11").store.setData(data);
//	dojo.widget.byId("codeListTable12").store.setData(data);
}

function createHtmlForRadio(name, id, checked) {
	return "<input type='radio' "+
		"class='radio' "+
		"name='"+name+"' "+
		"id='"+id+"' "+
		(checked ? "checked='checked' " : " ") +
		"/>";
}

// Check if the given list of objects contains an object with property 'isDefault' == true
function hasDefaultEntry(data) {
	return dojo.lang.some(data, function(item) { return item.isDefault == true; });
}

function showDefaultRadioButtons() {
	dojo.widget.byId("selectionListDefault").setValue(true);
	switchTableDisplay("codeListTable12Container", "codeListTable11Container", true);
}

function hideDefaultRadioButtons() {
	dojo.widget.byId("selectionListDefault").setValue(false);
	switchTableDisplay("codeListTable12Container", "codeListTable11Container", false);
}

function enableDefaultCheckbox() {
	dojo.html.setVisibility("codeListDefaultDisabledHint", false);
	dojo.widget.byId("selectionListDefault").enable();
}

function disableDefaultCheckbox() {
	dojo.html.setVisibility("codeListDefaultDisabledHint", true);
	dojo.widget.byId("selectionListDefault").disable();	
}

// Get the modified data and send it to the server
scriptScope.saveChanges = function() {
	var selectedChild = dojo.widget.byId("codeListTabContainer").selectedChild;

	if ("codeListTab" == selectedChild) {
		saveChangesCodelist();

	} else if ("freeEntryTab" == selectedChild) {
		saveChangesFreeEntry();
	}
}


function saveChangesCodelist() {
	var sysListId = dojo.widget.byId("selectionList").getValue();
	if (sysListId) {
		var setDefault = dojo.widget.byId("selectionListDefault").checked;
		var tableData = dojo.widget.byId("codeListTable11").store.getData();

		dojo.debug("sysList id: "+sysListId);

		// Build the required parameters
		var defaultIndex = null;
		var entryIds = [];
		var entriesGerman = [];
		var entriesEnglish = [];
		for (var index = 0; index < tableData.length; index++) {
			var currentEntry = tableData[index];
			var isDefault = setDefault && isMarkedAsDefaultEntry(currentEntry);
			dojo.debug("entry id : "+currentEntry.entryId);
			entryIds.push(currentEntry.entryId);
			dojo.debug("name (de): "+currentEntry.deName);
			entriesGerman.push(currentEntry.deName);
			dojo.debug("name (en): "+currentEntry.enName);
			entriesEnglish.push(currentEntry.enName);
			dojo.debug("isDefault: "+isDefault);
			if (isDefault) {
				defaultIndex = index;
			}
		}

		// Send data to the db
		// TODO Implement List of maintainable sysLists
		var maintainable = dojo.lang.some(MAINTAINABLE_LIST_IDS, function(listId) { return listId == parseInt(sysListId); } );
		var def = storeSysListDef(sysListId, maintainable, defaultIndex, entryIds, entriesGerman, entriesEnglish);
		def.addCallback(function() {
			// Show a 'success' message
			dialog.show(message.get("general.hint"), message.get("dialog.admin.catalog.management.codelist.storeSuccess"), dialog.INFO);

			// Update the frontend after the list has been stored
			var selectWidget = dojo.widget.byId("selectionList");
			selectWidget.setValue(sysListId);
		});
	}
}


// Returns whether an entry in the table is marked as default
function isMarkedAsDefaultEntry(entry) {
	// Extract the id of the corresponding radio button. In most cases it's equal to entryId
	// New entries don't have an entryId so we have to locate it another way
	// Here we get the id from the string used to create the html radio button
	var isDefaultStr = entry.isDefault;
	var entryId = isDefaultStr.match(/codeListRadio_(\d+)'/)[1];

	return dojo.byId("codeListRadio_"+entryId).checked;
}

// Store a modified sysList in the db.
// listId - Id of the sysList to store
// maintainable - a boolean flag signaling if the sysList is allowed to be modified
// defaultIndex - the index of the default entry. Null if no entry should have a default value
// entryIds - Ids of the entries as Int List. Null if it's a new entry
// entriesGerman, entriesEnglish - The Entries as String Lists
function storeSysListDef(listId, maintainable, defaultIndex, entryIds, entriesGerman, entriesEnglish) {
	var def = new dojo.Deferred();
	CatalogService.storeSysList(listId, maintainable, defaultIndex, entryIds, entriesGerman, entriesEnglish, {
		callback: function(res) {
			dojo.debug("result:" + res);
			def.callback(res);
		},
		errorHandler: function(msg, err) {
			dojo.debug("Error: "+msg);
			dojo.debugShallow(err);
			def.errback();
		}
	});
	return def;
}


function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("codelistsLoadingZone"), "visible");
    dojo.html.setVisibility(dojo.byId("codelistsFreeLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("codelistsLoadingZone"), "hidden");
    dojo.html.setVisibility(dojo.byId("codelistsFreeLoadingZone"), "hidden");
}

function showEditDisabledHint() {
	dojo.html.setVisibility("codeListEditDisabledHint", true);	
}

function hideEditDisabledHint() {
	dojo.html.setVisibility("codeListEditDisabledHint", false);	
}



// Functions for the second div. Adding free entries to a list / overwriting free entries with sysList entries

function initFreeEntrySelect() {
	var selectWidget = dojo.widget.byId("freeEntrySelectionList");

	// Currently the form is restricted to the list with id 1350 - 'rechtliche Grundlagen'
	var listIds = [ 1350 ];
	var selectWidgetData = [];
	for (var index = 0; index < listIds.length; ++index) {
		var name = UtilCatalog.getNameForSysList(listIds[index]);
		selectWidgetData.push([name + " (" + listIds[index] + ")", listIds[index]+""]);
	}
	selectWidget.dataProvider.setData( selectWidgetData );


	// On value changed load the selected sysList from the backend and update the table
	dojo.event.connect(selectWidget, "onValueChanged", reloadFreeEntryCodelistsDef);

	// Initially set the select widget value to the first one in the list
	selectWidget.setValue(listIds[0]);
}

function reloadFreeEntryCodelistsDef(listId) {
	var def = new dojo.Deferred();

	if (listId) {
		var germanListDef = getSysListDef(listId, "de");
		var englishListDef = getSysListDef(listId, "en");
		var freeEntryDef = getFreeEntriesDef("LEGIST");

		var defList = new dojo.DeferredList([germanListDef, englishListDef, freeEntryDef], false, false, true);
		defList.addCallback(function (resultList) {
			var germanList = resultList[0][1]; 
			var englishList = resultList[1][1];
			var freeList = resultList[2][1];

			var germanData = convertSysListToTableData(germanList);
			var englishData = convertSysListToTableData(englishList);
	
			var mergedData = mergeTableData(germanData, englishData);
			updateFreeEntryCodelistTable(mergedData);

			updateFreeEntryTable(freeList);

			def.callback();
		});
		defList.addErrback(function(err) {
			def.errback();
		});
	}
}

function initFreeEntryTables() {
	dojo.widget.byId("freeEntryCodelistTable").removeContextMenu();
	dojo.widget.byId("freeEntryTable").removeContextMenu();
}

function updateFreeEntryCodelistTable(data) {
	UtilList.addTableIndices(data);
	dojo.widget.byId("freeEntryCodelistTable").store.setData(data);
}

function updateFreeEntryTable(entries) {
	var data = UtilList.listToTableData(entries);
	UtilList.addTableIndices(data);
	dojo.widget.byId("freeEntryTable").store.setData(data);
}

function getFreeEntriesDef(mdekListName) {
	var def = new dojo.Deferred();
	CatalogService.getFreeListEntries(mdekListName, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(entries) {
			def.callback(entries);
		},
		errorHandler: function(msg, err) {
			hideLoadingZone();
			dojo.debug("Error: "+msg);
			dojo.debugShallow(err);
			def.errback(err);
		}
	});
	return def;
}


scriptScope.addFreeEntryToSysList = function() {
	var freeTable = dojo.widget.byId("freeEntryTable");
	var freeEntry = freeTable.getSelectedData();

	if (freeEntry) {
		var def = new dojo.Deferred();
		var displayText = dojo.string.substituteParams(message.get("dialog.admin.catalog.management.codelist.freeEntryToSysListEntry"), freeEntry.title);
		dialog.show(message.get("general.hint"), displayText, dialog.INFO, [
	        { caption: message.get("general.no"),  action: function() { def.errback("CANCEL"); } },
	    	{ caption: message.get("general.ok"), action: function() { def.callback(); } }
		]);

		def.addCallback(function() {
			return addFreeEntryToSysListDef(freeEntry);
		});
		def.addCallback(function() {
			// Show a 'success' message
			dialog.show(message.get("general.hint"), message.get("dialog.admin.catalog.management.codelist.freeEntryToSysListEntrySuccess"), dialog.INFO);

			// Update the frontend after the list has been stored
			var selectWidget = dojo.widget.byId("freeEntrySelectionList");
			selectWidget.setValue(selectWidget.getValue());
		});
	
		def.addErrback(function(err) {
			if (err.message != "CANCEL") {
				dojo.debug("Error: " + err);
				dojo.debugShallow(err);
			}
		});
	}
}

scriptScope.addAllFreeEntriesToSysList = function() {
	var freeData = dojo.widget.byId("freeEntryTable").store.getData();
	var sysListId = dojo.widget.byId("freeEntrySelectionList").getValue();

	if (freeData && freeData.length > 0) {

		var freeEntryTitles = [];
		dojo.lang.forEach(freeData, function(freeEntry) { freeEntryTitles.push(freeEntry.title); });
		var def = new dojo.Deferred();
		var displayText = dojo.string.substituteParams(message.get("dialog.admin.catalog.management.codelist.freeEntriesToSysListEntries"), freeEntryTitles.join(", "));
		dialog.show(message.get("general.hint"), displayText, dialog.INFO, [
	        { caption: message.get("general.no"),  action: function() { def.errback("CANCEL"); } },
	    	{ caption: message.get("general.ok"), action: function() { def.callback(); } }
		]);

		freeData = freeData.reverse();
		while (freeData.length > 0) {
			var freeEntry = freeData.pop();
			(function(entry) {
				def.addCallback(function() { return addFreeEntryToSysListDef(entry); });
//				def.addCallback(function() { return reloadFreeEntryCodelistsDef(sysListId); });
			})(freeEntry)
		}

		def.addCallback(function() {
			// Show a 'success' message
			dialog.show(message.get("general.hint"), message.get("dialog.admin.catalog.management.codelist.freeEntriesToSysListEntriesSuccess"), dialog.INFO);

			// Update the frontend after the list has been stored
			var selectWidget = dojo.widget.byId("freeEntrySelectionList");
			selectWidget.setValue(selectWidget.getValue());
		});

		def.addErrback(function(err) {
			if (err.message != "CANCEL") {
				dojo.debug("Error: " + err);
				dojo.debugShallow(err);
			}
		});
	}
}

function addFreeEntryToSysListDef(freeEntry) {
	var addFreeEntryDef = new dojo.Deferred();

	var codelistTable = dojo.widget.byId("freeEntryCodelistTable");
	var codelistData = codelistTable.store.getData();

	var sysListId = dojo.widget.byId("freeEntrySelectionList").getValue();

	dojo.debug("free entry: " + freeEntry);

	if (freeEntry) {
		// First add the free entry to the sysList and store the sysList in the db
		// After that, replace the free entry which was just added to the sysList with the stored sysList entry 
		var sysListEntry = null;
		for (var index = 0; index < codelistData.length; ++index) {
			if (codelistData[index].deName == freeEntry.title) {
				sysListEntry = codelistData[index];
				break;
			}
		}

		var def = null;

		if (sysListEntry != null) {
			// If the entry was already found, just replace the free entry with the sysList entry
			dojo.debug("Entry already exists. Replacing free entry with sysList entry.");
			def = replaceFreeEntryWithSysListEntryDef(freeEntry.title, sysListEntry.entryId, sysListEntry.deName, "LEGIST");

		} else {
			// Otherwise add the free entry to the list, store it, and replace afterwards
			var newEntry = {
				Id: UtilStore.getNewKey(codelistTable.store),
				deName: freeEntry.title,
				enName: "",
				entryId: null,
				isDefault: false
			};

			codelistTable.store.addData(newEntry);

			def = saveChangesFreeEntryDef();
			def.addCallback(function() {
				return getSysListEntryDef(sysListId, freeEntry.title);
			});
			def.addCallback(function(newSysListEntry) {
				newEntry.entryId = newSysListEntry[1];
				return replaceFreeEntryWithSysListEntryDef(freeEntry.title, newSysListEntry[1], newSysListEntry[0], "LEGIST");
			});
		}

		def.addCallback(function() {
			addFreeEntryDef.callback();
		});
		def.addErrback(function(err) {
			addFreeEntryDef.errback(err);
		})

	} else {
		addFreeEntryDef.errback("No free entry selected!");
	}

	return addFreeEntryDef;
}


function getSysListEntryDef(listId, title) {
	var def = getSysListDef(listId, "de");
	def.addCallback(function(sysList) {
		for (var index = 0; index < sysList.length; ++index) {
			if (sysList[index][0] == title) {
				return sysList[index];
			}
		}
		return null;
	});

	return def;
}


function replaceFreeEntryWithSysListEntryDef(freeEntry, sysListEntryId, sysListEntryName, mdekListName) {
	var def = new dojo.Deferred();

	CatalogService.replaceFreeEntryWithSysListEntry(freeEntry, mdekListName, sysListEntryId, sysListEntryName, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function() {
			def.callback();
		},
		errorHandler: function(msg, err) {
			dojo.debug("Error: "+msg);
			dojo.debugShallow(err);
			def.errback(err);
		}
	});

	return def;
}


scriptScope.replaceFreeEntryWithSysListEntry = function() {
	var sysListId = dojo.widget.byId("freeEntrySelectionList").getValue();

	var codelistEntry = dojo.widget.byId("freeEntryCodelistTable").getSelectedData();
	var freeEntry = dojo.widget.byId("freeEntryTable").getSelectedData();

	if (codelistEntry && freeEntry) {
		var def = new dojo.Deferred();
		var displayText = dojo.string.substituteParams(message.get("dialog.admin.catalog.management.codelist.replaceFreeEntryWithSysListEntry"), freeEntry.title, codelistEntry.deName);
		dialog.show(message.get("general.hint"), displayText, dialog.INFO, [
	        { caption: message.get("general.no"),  action: function() { def.errback("CANCEL"); } },
	    	{ caption: message.get("general.ok"), action: function() { def.callback(); } }
		]);

		def.addCallback(function() {
			return replaceFreeEntryWithSysListEntryDef(freeEntry.title, codelistEntry.entryId, codelistEntry.deName, "LEGIST");
		});

		def.addCallback(function() {
			// Show a 'success' message
			dialog.show(message.get("general.hint"), message.get("dialog.admin.catalog.management.codelist.replaceFreeEntryWithSysListEntrySuccess"), dialog.INFO);

			// Update the frontend after the list has been stored
			var selectWidget = dojo.widget.byId("freeEntrySelectionList");
			selectWidget.setValue(selectWidget.getValue());
		});

		def.addErrback(function(err) {
			if (err.message != "CANCEL") {
				dojo.debug("Error: " + err);
				dojo.debugShallow(err);
			}
		});

	} else {
		dojo.debug("Must select items in both tables!");
	}
}



function saveChangesFreeEntryDef() {
	var def = new dojo.Deferred();

	var sysListId = dojo.widget.byId("freeEntrySelectionList").getValue();
	if (sysListId) {
		var tableData = dojo.widget.byId("freeEntryCodelistTable").store.getData();

		dojo.debug("sysList id: "+sysListId);

		// Build the required parameters
		var defaultIndex = null;
		var entryIds = [];
		var entriesGerman = [];
		var entriesEnglish = [];
		for (var index = 0; index < tableData.length; index++) {
			var currentEntry = tableData[index];
			var isDefault = currentEntry.isDefault;
			entryIds.push(currentEntry.entryId);
			entriesGerman.push(currentEntry.deName);
			entriesEnglish.push(currentEntry.enName);
			if (isDefault) {
				defaultIndex = index;
			}
		}

		// Send data to the db
		var maintainable = dojo.lang.some(MAINTAINABLE_LIST_IDS, function(listId) { return listId == parseInt(sysListId); } );
		var sysListDef = storeSysListDef(sysListId, maintainable, defaultIndex, entryIds, entriesGerman, entriesEnglish);
		sysListDef.addCallback(function() {
			def.callback();
		});
		sysListDef.addErrback(function(err) {
			dojo.debug("Error: " + err);
			dojo.debugShallow(err);
			def.errback(err);
		});
	}

	return def;
}


</script>
</head>

<body>

<!-- CONTENT START -->
<div dojoType="ContentPane" layoutAlign="client">

	<div id="contentSection" class="contentBlockWhite top">
		<div id="winNavi">
			<a href="#" title="Hilfe">[?]</a>
		</div>
		<div id="codeListsContent" class="content">

			<!-- LEFT HAND SIDE CONTENT START -->
			<div class="spacer"></div>
			<div class="spacer"></div>
			<div id="codeLists" class="inputContainer noSpaceBelow">
				<span class="functionalLink onTab"><img src="img/ic_fl_export.gif" width="11" height="10" alt="<fmt:message key="dialog.admin.catalog.management.codelists.export" />" /><a href="#" title="Exportieren [Popup]"><fmt:message key="dialog.admin.catalog.management.codelists.export" /></a><img src="img/ic_fl_import.gif" width="11" height="10" alt="<fmt:message key="dialog.admin.catalog.management.codelists.import" />" /><a href="#" title="Importieren [Popup]"><fmt:message key="dialog.admin.catalog.management.codelists.import" /></a></span>
				<div id="codeListTabContainer" dojoType="ingrid:TabContainer" class="w668 h452" selectedChild="codeListTab">

					<!-- TAB 1 START -->
					<div id="codeListTab" dojoType="ContentPane" class="blueTopBorder grey" label="<fmt:message key="dialog.admin.catalog.management.codelists.codelistTitle" />">
						<div class="inputContainer grey field w668 noSpaceBelow">
							<span class="label"><label for="selectionList" onclick="javascript:dialog.showContextHelp(arguments[0], '<fmt:message key="dialog.admin.catalog.management.codelists.codelist" />')"><fmt:message key="dialog.admin.catalog.management.codelists.codelist" /></label></span>
							<span class="input spaceBelow"><input dojoType="ingrid:Select" autocomplete="false" style="width:606px;" id="selectionList" /></span>
							<span id="codeListEditDisabledHint" style="visibility:hidden;" class="label"><label class="inActive">Hinweis: Die Eintr&auml;ge dieser Auswahlliste k&ouml;nnen nicht ge&auml;ndert werden.</label></span>
							<span id="codeListDefaultDisabledHint" style="visibility:hidden;" class="label"><label class="inActive">Hinweis: F&uuml;r diese Liste kann kein Defaultwert eingestellt werden.</label></span>
							<div class="checkboxContainer">
								<span class="input spaceBelow"><input type="checkbox" onclick="switchTableDisplay('codeListTable12Container', 'codeListTable11Container', dojo.widget.byId('selectionListDefault').checked);" id="selectionListDefault" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], '<fmt:message key="dialog.admin.catalog.management.codelists.setDefault" />')"><fmt:message key="dialog.admin.catalog.management.codelists.setDefault" /></label></span>
							</div>
							<div class="tableContainer rows8 w632" id="codeListTable11Container">
								<div class="cellEditors" id="codeListTable11Editors">
									<div dojoType="ingrid:ValidationTextbox" maxlength="255" class="w277" widgetId="codeListTable11DeNameEditor"></div>
									<div dojoType="ingrid:ValidationTextbox" maxlength="255" class="w277" widgetId="codeListTable11EnNameEditor"></div>
								</div>
								<table id="codeListTable11" dojoType="ingrid:FilteringTable" minRows="8" cellspacing="0" class="filteringTable nosort interactive">
									<thead>
										<tr>
											<th nosort="true" field="entryId" dataType="String" width="32"><fmt:message key="dialog.admin.catalog.management.codelists.id" /></th>
											<th nosort="true" field="deName" dataType="String" width="300" editor="codeListTable11DeNameEditor"><fmt:message key="dialog.admin.catalog.management.codelists.germanName" /></th>
											<th nosort="true" field="enName" dataType="String" width="300" editor="codeListTable11EnNameEditor"><fmt:message key="dialog.admin.catalog.management.codelists.englishName" /></th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
							<div class="tableContainer rows8 w632" id="codeListTable12Container" style="display:none">
								<div class="cellEditors" id="codeListTable12Editors">
									<div dojoType="ingrid:ValidationTextbox" maxlength="255" class="w248" widgetId="codeListTable12DeNameEditor"></div>
									<div dojoType="ingrid:ValidationTextbox" maxlength="255" class="w248" widgetId="codeListTable12EnNameEditor"></div>
								</div>
								<table id="codeListTable12" dojoType="ingrid:FilteringTable" minRows="8" cellspacing="0" class="filteringTable nosort interactive">
									<thead>
										<tr>
											<th nosort="true" field="entryId" dataType="String" width="32"><fmt:message key="dialog.admin.catalog.management.codelists.id" /></th>
											<th nosort="true" field="deName" dataType="String" width="269" editor="codeListTable12DeNameEditor"><fmt:message key="dialog.admin.catalog.management.codelists.germanName" /></th>
											<th nosort="true" field="enName" dataType="String" width="269" editor="codeListTable12EnNameEditor"><fmt:message key="dialog.admin.catalog.management.codelists.englishName" /></th>
											<th nosort="true" field="isDefault" dataType="String" width="62" noSort="true">Default</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
							<div class="fill"></div>
						</div>
						<div style="margin-top:40px; margin-right:14px;" >
							<span style="height:20px !important;">
								<span style="float:right;">
									<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.catalog.management.codelists.save" />" onClick="javascript:scriptScope.saveChanges();"><fmt:message key="dialog.admin.catalog.management.codelists.save" /></button>
								</span>
								<span id="codelistsLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden">
									<img src="img/ladekreis.gif" />
								</span>
							</span>
						</div>
					</div> <!-- TAB 1 END -->

					<!-- TAB 2 START -->
					<div id="freeEntryTab" dojoType="ContentPane" class="blueTopBorder grey" label="<fmt:message key="dialog.admin.catalog.management.codelists.legalBaseTitle" />">
						<div class="inputContainer grey field w668 noSpaceBelow">
							<span class="label"><label for="freeEntrySelectionList" onclick="javascript:dialog.showContextHelp(arguments[0], '<fmt:message key="dialog.admin.catalog.management.codelists.codelist" />')"><fmt:message key="dialog.admin.catalog.management.codelists.codelist" /></label></span>
							<span class="input spaceBelow"><input dojoType="ingrid:Select" autocomplete="false" style="width:606px;" id="freeEntrySelectionList"></div></span>

							<span class="entry first field">
								<span class="label" style="height:37px;"><fmt:message key="dialog.admin.catalog.management.codelists.entriesNotInList" /></span>

								<div class="tableContainer headHiddenRows6 third">
									<table id="freeEntryTable" dojoType="ingrid:FilteringTable" minRows="6" headClass="hidden" cellspacing="0" multiple="false" class="filteringTable nosort interactive">
										<thead>
											<tr>
												<th field="title" nosort="true" dataType="String">Name</th>
											</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
								</div>
							</span>

							<span class="entry">
								<span class="buttonCol" style="margin:80px 0px 0px;">
									<button dojoType="ingrid:Button" onClick="javascript:scriptScope.addFreeEntryToSysList();">&gt; <fmt:message key="dialog.admin.catalog.management.codelists.move" /></button>
									<button dojoType="ingrid:Button" onClick="javascript:scriptScope.addAllFreeEntriesToSysList();">&nbsp;&nbsp;&nbsp;&nbsp;&gt;&gt; <fmt:message key="dialog.admin.catalog.management.codelists.moveAll" />&nbsp;&nbsp;&nbsp;&nbsp;</button>
									<button dojoType="ingrid:Button" onClick="javascript:scriptScope.replaceFreeEntryWithSysListEntry();">&nbsp;&lt; <fmt:message key="dialog.admin.catalog.management.codelists.replace" /> &nbsp;</button>
								</span>
							</span>

							<span class="entry field" style="padding-left:0px;">
								<span class="label" style="height:37px;"><fmt:message key="dialog.admin.catalog.management.codelists.listContent" /></span>

								<div class="tableContainer headHiddenRows6" style="width:284px;">
									<table id="freeEntryCodelistTable" dojoType="ingrid:FilteringTable" minRows="6" headClass="hidden" cellspacing="0" multiple="false" class="filteringTable nosort interactive">
										<thead>
											<tr>
												<th field="deName" nosort="true" dataType="String">Name</th>
											</tr>
										</thead>
										<colgroup>
											<col width="50">
										</colgroup>
										<tbody>
										</tbody>
									</table>
								</div>
							</span>

							<div class="fill spacer"></div>

							<div style="margin-top:40px; margin-right:40px;" >
								<span style="height:20px !important;">
									<span id="codelistsFreeLoadingZone" style="float:right; margin-top:1px; z-index: 100; visibility:hidden">
										<img src="img/ladekreis.gif" />
									</span>
								</span>
							</div>

						</div>
					</div> <!-- TAB 2 END -->
				</div>
			</div>
		</div>
	</div>
</div> <!-- CONTENT END -->

</body>
</html>
