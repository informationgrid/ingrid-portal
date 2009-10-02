<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script type="text/javascript">
var scriptScope = this;
_container_.addOnLoad(function() {
/*
	addLineToFieldsTable({ identifier: "5000", name: "Kurzbezeichnung", displayType: "alwaysHide" });
	addLineToFieldsTable({ identifier: "1010", name: "Beschreibung", displayType: "standard" });
	addLineToFieldsTable({ identifier: "1020", name: "Objektklasse", displayType: "alwaysShow" });
*/
	initFieldsTable();
});

function initFieldsTable() {
	showLoadingZone();

	var guiIdList = getGuiIdList();

	guiIdList.addCallback(function(guiIdList) {
		var data = [];
		dojo.lang.forEach(guiIdList, function(entry) {

			var dispType = "standard";
			if (entry.mode == "-1") {
				dispType = "standard";
			} else if (entry.mode == "0") {
				dispType = "alwaysHide";
			} else if (entry.mode == "1") {
				dispType = "alwaysShow";
			}

			data.push(createTableEntryForField({ identifier: entry.id, name: UtilUI.getDescriptionForGuiId(entry.id), displayType: dispType }));
		});
		var compareByName = function(a, b) {
			return (a.name == b.name) ? 0 : (a.name < b.name) ? -1 : 1;
		}
		data.sort(compareByName);
		UtilList.addTableIndices(data);

		var fieldStore = dojo.widget.byId("fieldTable").store;
		fieldStore.setData(data);
		hideLoadingZone();
	});
	guiIdList.addErrback(hideLoadingZone);
}

// Button function for the 'save' button
// The current settings are sent to the backend
scriptScope.saveGuiIdList = function() {
	var store = dojo.widget.byId("fieldTable").store;
	var data = store.getData();
	var guiIdList = [];

	for (var i in data) {
		var guiId = data[i].identifier;

		var mode = "-1";
		if (dojo.byId(guiId+"_noModifier").checked) {
			mode = "-1";
		} else if (dojo.byId(guiId+"_alwaysHide").checked) {
			mode = "0";
		} else if (dojo.byId(guiId+"_alwaysShow").checked) {
			mode = "1";
		}

		guiIdList.push({id: guiId, mode: mode});
	}

	var def = storeGuiIdList(guiIdList, false);

	def.addCallback(function(res) {
		dialog.show(message.get("general.hint"), message.get("dialog.admin.fieldSettingsUpdated"), dialog.INFO);
	});
}


function getGuiIdList() {
	var deferred = new dojo.Deferred();

	// Get all gui ids
	CatalogService.getSysGuis(null, {
		callback: function(sysGuiList) {
			deferred.callback(sysGuiList);
		},
		errorHandler: function(errMsg, err) {
		    displayErrorMessage(err);
			dojo.debug(errMsg);
			dojo.debugShallow(err);
			deferred.errback(err);			
		}
	});

	return deferred;
}

function storeGuiIdList(guiIdList, refetchAfterStore) {
	var deferred = new dojo.Deferred();

	// Store gui id list
	CatalogService.storeSysGuis(guiIdList, refetchAfterStore, {
		preHook: showLoadingZone,
		postHook: hideLoadingZone,
		callback: function(sysGuiList) {
			deferred.callback(sysGuiList);
		},
		errorHandler: function(errMsg, err) {
			hideLoadingZone();
			displayErrorMessage(err);
			dojo.debug(errMsg);
			dojo.debugShallow(err);
			deferred.errback(err);			
		}
	});

	return deferred;	
}


function createTableEntryForField(field) {
	var f = {}
	f.identifier = field.identifier;
	f.name = field.name;
	if (field.displayType == "standard")
		f.noModifier = "<input type='radio' name='"+field.identifier+"' id='"+field.identifier+"_noModifier' class='radio' checked='true' />";
	else
		f.noModifier = "<input type='radio' name='"+field.identifier+"' id='"+field.identifier+"_noModifier' class='radio' />";

	if (field.displayType == "alwaysHide")
		f.alwaysHide = "<input type='radio' name='"+field.identifier+"' id='"+field.identifier+"_alwaysHide' class='radio' checked='true' />";
	else
		f.alwaysHide = "<input type='radio' name='"+field.identifier+"' id='"+field.identifier+"_alwaysHide' class='radio' />";

	if (field.displayType == "alwaysShow")
		f.alwaysShow = "<input type='radio' name='"+field.identifier+"' id='"+field.identifier+"_alwaysShow' class='radio' checked='true' />";
	else
		f.alwaysShow = "<input type='radio' name='"+field.identifier+"' id='"+field.identifier+"_alwaysShow' class='radio' />";

	return f;
}

function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("catalogueFieldSettingsLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("catalogueFieldSettingsLoadingZone"), "hidden");
}

</script>

</head>

<body>

<div dojoType="ContentPane" layoutAlign="client">

	<div id="contentSection" class="contentBlockWhite top">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=catalog-administration-2#catalog-administration-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="Hilfe">[?]</a>
		</div>
		<div id="catalogueAdminFields" class="content">
	
			<!-- LEFT HAND SIDE CONTENT START -->
			<div class="spacer"></div>
			<div class="spacer"></div>
			<div class="inputContainer field grey">
				<span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 8010)"><fmt:message key="dialog.admin.catalog.fields.settings" /></label></span>
				<div class="tableContainer rows10 w652">
					<table id="fieldTable" dojoType="ingrid:FilteringTable" minRows="10" headClass="fixedHeader" cellspacing="0" class="filteringTable nosort relativePos">
						<thead>
							<tr>
								<th nosort="true" field="identifier" dataType="String" width="45"><fmt:message key="dialog.admin.catalog.fields.fieldId" /></th>
								<th nosort="true" field="name" dataType="String" width="270"><fmt:message key="dialog.admin.catalog.fields.fieldName" /></th>
								<th nosort="true" field="noModifier" dataType="String" width="105"><fmt:message key="dialog.admin.catalog.fields.standard" /></th>
								<th nosort="true" field="alwaysHide" dataType="String" width="115"><fmt:message key="dialog.admin.catalog.fields.hide" /></th>
								<th nosort="true" field="alwaysShow" dataType="String" width="110"><fmt:message key="dialog.admin.catalog.fields.show" /></th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
					<div class="spacerField"></div>
				</div>
			</div>
			<div class="inputContainer">
				<span class="button w644" style="height:20px !important;">
					<span style="float:right;">
						<button dojoType="ingrid:Button" title="Speichern" onClick="javascript:scriptScope.saveGuiIdList();"><fmt:message key="dialog.admin.catalog.fields.save" /></button>
					</span>
					<span id="catalogueFieldSettingsLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
						<img src="img/ladekreis.gif" />
					</span>
				</span>
			</div>
			<!-- LEFT HAND SIDE CONTENT END -->        
		</div>

	</div>
</div>
<!-- CONTENT END -->
</body>
</html>
