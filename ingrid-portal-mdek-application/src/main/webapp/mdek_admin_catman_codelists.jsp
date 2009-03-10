<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {
	initCodelistSelect();
	initTables();
});

function initCodelistSelect() {
	var selectWidget = dojo.widget.byId("selectionList");

	var def = getAllSysListIdsDef();
	def.addCallback(function(listIds) {
		var selectWidgetData = [];
		for (var index = 0; index < listIds.length; ++index) {
			// TODO Localize entries
			selectWidgetData.push([listIds[index]+"", listIds[index]+""]);
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

function initTables() {
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

	hasDefaultEntry(data)? showDefaultRadioButtons() : hideDefaultRadioButtons();

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
		"checked='"+checked+"' "+
		"/>";
}

// Check if the given list of objects contains an object with property 'isDefault' == true
function hasDefaultEntry(data) {
	return dojo.lang.some(data, function(item) { return item.isDefault == true; });
}

function showDefaultRadioButtons() {
	if (!dojo.widget.byId("selectionListDefault").checked) {
		dojo.widget.byId("selectionListDefault").setValue(true);
		switchTableDisplay("codeListTable12Container", "codeListTable11Container", true);
	}
}

function hideDefaultRadioButtons() {
	if (dojo.widget.byId("selectionListDefault").checked) {
		dojo.widget.byId("selectionListDefault").setValue(false);
		switchTableDisplay("codeListTable12Container", "codeListTable11Container", false);
	}
}

// Get the modified data and send it to the server
scriptScope.saveChanges = function() {
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
		var maintainable = false;
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
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("codelistsLoadingZone"), "hidden");
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
				<span class="functionalLink onTab"><img src="img/ic_fl_export.gif" width="11" height="10" alt="Export" /><a href="#" title="Exportieren [Popup]">Exportieren</a><img src="img/ic_fl_import.gif" width="11" height="10" alt="Import" /><a href="#" title="Importieren [Popup]">Importieren</a></span>
				<div id="lists" dojoType="ingrid:TabContainer" class="w668 h452" selectedChild="selection">

					<!-- TAB 1 START -->
					<div id="selection" dojoType="ContentPane" class="blueTopBorder grey" label="Auswahllistenpflege">
						<div class="inputContainer grey field w668 noSpaceBelow">
							<span class="label"><label for="selectionList" onclick="javascript:dialog.showContextHelp(arguments[0], 'Auswahlliste')">Auswahlliste</label></span>
							<span class="input spaceBelow"><input dojoType="ingrid:Select" autocomplete="false" style="width:606px;" id="selectionList" /></span>
							<div class="checkboxContainer">
								<span class="input spaceBelow"><input type="checkbox" onclick="switchTableDisplay('codeListTable12Container', 'codeListTable11Container', dojo.widget.byId('selectionListDefault').checked);" id="selectionListDefault" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 'Defaultwert einstellbar')">Defaultwert einstellbar</label></span>
							</div>
							<div class="tableContainer rows8 w632" id="codeListTable11Container">
								<div class="cellEditors" id="codeListTable11Editors">
									<div dojoType="ingrid:ValidationTextbox" maxlength="255" class="w277" widgetId="codeListTable11DeNameEditor"></div>
									<div dojoType="ingrid:ValidationTextbox" maxlength="255" class="w277" widgetId="codeListTable11EnNameEditor"></div>
								</div>
								<table id="codeListTable11" dojoType="ingrid:FilteringTable" minRows="8" cellspacing="0" class="filteringTable nosort interactive">
									<thead>
										<tr>
											<th nosort="true" field="entryId" dataType="String" width="32">ID</th>
											<th nosort="true" field="deName" dataType="String" width="300" editor="codeListTable11DeNameEditor">Name in Deutsch</th>
											<th nosort="true" field="enName" dataType="String" width="300" editor="codeListTable11EnNameEditor">Name in Englisch</th>
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
											<th nosort="true" field="entryId" dataType="String" width="32">ID</th>
											<th nosort="true" field="deName" dataType="String" width="269" editor="codeListTable12DeNameEditor">Name in Deutsch</th>
											<th nosort="true" field="enName" dataType="String" width="269" editor="codeListTable12EnNameEditor">Name in Englisch</th>
											<th nosort="true" field="isDefault" dataType="String" width="62" noSort="true">Default</th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
							<div class="fill"></div>
						</div>
					</div> <!-- TAB 1 END -->

					<!-- TAB 2 START -->
					<div dojoType="ContentPane" class="blueTopBorder grey" label="Liste: gesetzliche Grundlagen">
						<div class="inputContainer grey field w668 noSpaceBelow">
							<span class="label"><label for="isoList" onclick="javascript:dialog.showContextHelp(arguments[0], 'Auswahlliste')">Auswahlliste</label></span>
							<span class="input spaceBelow"><div dojoType="ingrid:ComboBox" toggle="plain" style="width:606px;" widgetId="isoList"></div></span>

							<div class="inputContainer w644 noSpaceBelow">
								<span class="entry first">
									<span class="label" style="height:37px;">Eint&auml;ge, die nicht in der Schl&uuml;sseltabelle<br />vorhanden sind</span>

									<table id="legalBasicsLeft" dojoType="ingrid:FilteringTable" minRows="6" headClass="fixedHeader hidden" tbodyClass="scrollContent rows6" cellspacing="0" class="filteringTable interactive w264 relativePos">
										<thead>
											<tr>
												<th field="name" dataType="String">Name</th>
											</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
								</span>

								<span class="entry">
									<span class="buttonCol" style="margin:80px 0px 0px;">
										<button dojoType="ingrid:Button" onClick="javascript:scriptScope.addFreeEntryToSysList();">&gt; &Uuml;bertragen</button>
										<button dojoType="ingrid:Button" onClick="addAll">&lt; Ersetzen</button>
										<button dojoType="ingrid:Button" onClick="removeAll">&gt;&gt; Alle</button>
									</span>
								</span>

								<span class="entry">
									<span class="label" style="height:37px;">Inhalte der Schl&uuml;sseltabelle</span>
									<table id="legalBasicsRight" dojoType="ingrid:FilteringTable" minRows="6" headClass="fixedHeader hidden" tbodyClass="scrollContent rows6" cellspacing="0" class="filteringTable interactive w264 relativePos">
										<thead>
											<tr>
												<th field="name" dataType="String">Name*</th>
											</tr>
										</thead>
										<tbody>
										</tbody>
									</table>
								</span>

								<div class="fill spacer"></div>
							</div>

						</div>
					</div> <!-- TAB 2 END -->
				</div>
			</div>

			<div class="inputContainer">
				<span class="button w628" style="height:20px !important;">
					<span style="float:right;">
						<button dojoType="ingrid:Button" title="Speichern" onClick="javascript:scriptScope.saveChanges();">Speichern</button>
					</span>
					<span id="codelistsLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
						<img src="img/ladekreis.gif" />
					</span>
				</span>
			</div>
		</div>
	</div>
</div> <!-- CONTENT END -->

</body>
</html>
