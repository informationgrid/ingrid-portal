<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

var DEFAULT_LANGUAGE = "de";

_container_.addOnLoad(function() {
	resetInputFields();
	var language = DEFAULT_LANGUAGE;

	CatalogService.getSysAdditionalFields(null, language, {
		callback: function(additionalFieldList) {
			updateAdditionalFields(additionalFieldList);
		},
		errorHandler: function(msg, err) {
			dojo.debug("Error: "+msg);
			dojo.debugShallow(err);
		}
	});

	var addFieldsList = dojo.widget.byId("addFieldsList");
    var contextMenu = addFieldsList.getContextMenu();
	contextMenu.addSeparator();
	var menuEntryCaption = message.get("dialog.admin.catalog.management.additionalFields.editList");
    contextMenu.addItemObject( { caption:menuEntryCaption, method:editListEntries } );
	dojo.event.connect("after", contextMenu, "open", function(arg) {
		var rowData = contextMenu.getRowData();
		if (rowData && rowData.type == "LIST") {
			contextMenu.enableItem(menuEntryCaption);
		} else {
			contextMenu.disableItem(menuEntryCaption);
		} 
	});
});

function updateAdditionalFields(additionalFieldList) {
	additionalFieldList.sort(function(a, b) { return (a.id - b.id); });
	var tableData = UtilList.addTableIndices(additionalFieldList);
	dojo.widget.byId("addFieldsList").store.setData(tableData);	
}

scriptScope.saveChanges = function() {
	// TODO implement
	dojo.debug("Function not implemented yet.");

	var additionalFields = dojo.widget.byId("addFieldsList").store.getData();
	var languageCode = DEFAULT_LANGUAGE;

	var def = storeAdditionalFieldsDef(additionalFields);
	def.addCallback(function(additionalFieldList) {
		dojo.debug("Additional fields stored successfully.");
		updateAdditionalFields(additionalFieldList);
	});
	def.addErrback(function(err) {
		dojo.debug("Error: " + err);
		dojo.debugShallow(err);
	});
}

function storeAdditionalFieldsDef(additionalFields) {
	var def = new dojo.Deferred();

	CatalogService.storeAllSysAdditionalFields(additionalFields, {
		callback: function(res) {
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

scriptScope.addEntry = function() {
	if (validateInputFields()) {
		var fieldName = dojo.string.trim(dojo.widget.byId("addFieldName").getValue());
		var numChars = parseInt(dojo.widget.byId("addFieldNumChars").getValue());
		var fieldType = dojo.widget.byId("addFieldType").getValue();
	
		dojo.debug("name: "+fieldName);
		dojo.debug("numChars: "+numChars);
		dojo.debug("fieldType: "+fieldType);

		var store = dojo.widget.byId("addFieldsList").store;
		var key = UtilStore.getNewKey(store);
		store.addData({
			Id: key,
//			id: "new field",
			name: fieldName,
			size: numChars,
			type: fieldType,
			listLanguage: DEFAULT_LANGUAGE
		});
		resetInputFields();

	} else {
		dialog.show(message.get("general.error"), message.get("dialog.admin.catalog.management.additionalFields.requiredFieldsHint"), dialog.WARNING);
	}
}

function validateInputFields() {
	var fieldName = dojo.string.trim(dojo.widget.byId("addFieldName").getValue());
	var numCharWidget = dojo.widget.byId("addFieldNumChars");
	var selectWidget = dojo.widget.byId("addFieldType");

	return fieldName.length != 0
		&& numCharWidget.isValid() && numCharWidget.isInRange()
		&& selectWidget._isValidOption();
}

function resetInputFields() {
	dojo.widget.byId("addFieldName").setValue("");
	dojo.widget.byId("addFieldNumChars").setValue("");
	dojo.widget.byId("addFieldType").setValue("LIST");
}

function editListEntries(menuItem) {
	var menu = menuItem.parent;
	var rowData = menu.getRowData();
	var listId = rowData.id;
	var listEntries = rowData.listEntries;

	var def = new dojo.Deferred();
	dialog.showPage(
			message.get("dialog.admin.catalog.management.additionalFields.editList"),
			"mdek_admin_catman_additional_fields_list_edit.jsp",
			502, 245, true,
			{ resultHandler: def, listEntries: listEntries });

	def.addCallback(function(resultList) {
		rowData.listEntries = resultList;
	});

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
		<div id="catalogueAddFields" class="content">

			<!-- LEFT HAND SIDE CONTENT START -->
			<div class="spacer"></div>
			<div class="spacer"></div>
			<div id="addFields" class="inputContainer w964">

				<div dojoType="ingrid:TableContextMenu" toggle="plain" contextMenuForWindow="false" widgetId="contextMenu1"></div>
				<div class="tableContainer rows20 w964">
					<div class="cellEditors" id="addFieldsListEditors">
						<div dojoType="ingrid:ValidationTextbox" class="w675" widgetId="nameEditor"></div>
						<div dojoType="ingrid:ValidationTextbox" widgetId="numCharsEditor"></div>
		                <select dojoType="ingrid:Select" autoComplete="false" style="width:100px;" id="typeCombobox">
		                	<option value="TEXT"><fmt:message key="dialog.admin.catalog.management.additionalFields.textField" /></option>
		                	<option value="LIST"><fmt:message key="dialog.admin.catalog.management.additionalFields.list" /></option>
		                </select>
					</div>
					<table id="addFieldsList" dojoType="ingrid:FilteringTable" minRows="20" cellspacing="0" class="filteringTable nosort interactive disablenewentries">
						<thead>
							<tr>
								<th nosort="true" field="id" dataType="String" width="70">Nr.</th>
								<th nosort="true" field="name" dataType="String" width="724" editor="nameEditor"><fmt:message key="dialog.admin.catalog.management.additionalFields.fieldName" /></th>
								<th nosort="true" field="size" dataType="String" width="50" editor="numCharsEditor" createOnly="true"><fmt:message key="dialog.admin.catalog.management.additionalFields.numChars" /></th>
								<th nosort="true" field="type" dataType="String" width="120" editor="typeCombobox"  createOnly="true"><fmt:message key="dialog.admin.catalog.management.additionalFields.type" /></th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>

				<div class="inputContainer">
					<span class="button w924" style="height:20px !important;">
						<span style="float:right;">
							<button dojoType="ingrid:Button" title="Speichern" onClick="javascript:scriptScope.saveChanges();"><fmt:message key="dialog.admin.catalog.management.additionalFields.saveChanges" /></button>
						</span>
					</span>
				</div>

				<div class="inputContainer grey h067 w964">
					<div class="spacer"></div>
					<div class="entry half left w608" style="margin-right:30px;">
						<span class="label"><label for="addFieldName" onclick="javascript:dialog.showContextHelp(arguments[0], 'Feldname')"><fmt:message key="dialog.admin.catalog.management.additionalFields.fieldName" /></label></span>
						<span class="input"><input type="text" id="addFieldName" required="true" class="w608" dojoType="ingrid:ValidationTextBox" /></span>
					</div>
					<div class="entry half left w116" style="margin-right:30px;">
						<span class="label"><label for="addFieldNumChars" onclick="javascript:dialog.showContextHelp(arguments[0], 'Zeichen')"><fmt:message key="dialog.admin.catalog.management.additionalFields.numChars" /></label></span>
						<span class="input"><input id="addFieldNumChars" min="0" max="255" maxlength="3" class="w116" dojoType="IntegerTextbox" /></span>

					</div>
					<div class="entry half w116">
						<span class="label"><label for="addFieldType" onclick="javascript:dialog.showContextHelp(arguments[0], 'Typ')"><fmt:message key="dialog.admin.catalog.management.additionalFields.type" /></label></span>
						<span class="input">
							<select dojoType="ingrid:Select" toggle="plain" required="true" style="width:108px;" widgetId="addFieldType">
								<option value="LIST"><fmt:message key="dialog.admin.catalog.management.additionalFields.list" /></option>
								<option value="TEXT"><fmt:message key="dialog.admin.catalog.management.additionalFields.textField" /></option>
							</select>
						</span>
					</div>
				</div>

				<div class="inputContainer">
					<span class="button w924" style="height:20px !important;">
						<span style="float:right;">
							<button dojoType="ingrid:Button" title="Hinzuf&uuml;gen" onClick="javascript:scriptScope.addEntry();"><fmt:message key="dialog.admin.catalog.management.additionalFields.add" /></button>
						</span>
					</span>
				</div>

			</div> <!-- LEFT HAND SIDE CONTENT END -->
		</div> <!-- CONTENT END -->
	</div>
</div>

</body>
</html>
