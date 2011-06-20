<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

	createDOMElements();
	createTableContextMenu();
	initTableData();
	
	dojo.connect(_container_, "onLoad", function(){
		if (this.customParams && this.customParams.resultHandler) {
			if (this.customParams.resultHandler.fired == -1) {
				this.customParams.resultHandler.errback();
			}
		}
	});

function createDOMElements() {
	var fieldsTableStructure = [
		{field: 'title',name: 'title',width: 'auto', editable: true}		
	];
    
    createDataGrid("fieldsTable", null, fieldsTableStructure, null);
}

function createTableContextMenu() {
	var menu = new dijit.Menu({
		targetNodeIds: ["fieldsTable"]
	});
	
	menu.addChild(new dijit.MenuItem({
		label: "<fmt:message key='table.rowDelete' />",
		onClick: menuEventHandler.removeClickedRow
	}));
	
	dijit.byId("fieldsTable").onRowContextMenu = onRowEvent;
}

function initTableData() {
	if (dijit.byId("pageDialog").customParams && dijit.byId("pageDialog").customParams.listEntries) {
		var listEntries = dijit.byId("pageDialog").customParams.listEntries;
		var tableData = UtilList.listToTableData(listEntries);
		//UtilList.addTableIndices(tableData);
		//dijit("fieldsTable").store.setData(tableData);
		UtilStore.updateWriteStore("fieldsTable", tableData);
	}
}

scriptScope.saveEntries = function() {
	if (dijit.byId("pageDialog").customParams && dijit.byId("pageDialog").customParams.resultHandler) {
		var resultHandler = dijit.byId("pageDialog").customParams.resultHandler;
		var tableData = UtilStore.convertItemsToJS(dijit.byId("fieldsTable").store, dijit.byId("fieldsTable").store._arrayOfTopLevelItems);
		var resultList = UtilList.tableDataToList(tableData);
		resultHandler.callback(resultList);
	}
	dijit.byId("pageDialog").hide();
}

</script>
</head>

<body>
	<div class="contentBlockWhite top w478">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=overall-catalog-management-5#overall-catalog-management-5', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
		</div>
		<div id="userContent" class="content h135">
			<!-- CONTENT START -->
			<div class="tableContainer headHiddenRows6 w462" style="width:462px;">
				<div id="fieldsTable" minRows="6" class="hideTableHeader"></div>
			</div>
			<span style="float:right;">
				<button dojoType="dijit.form.Button" title="<fmt:message key="dialog.admin.catalog.management.additionalFields.apply" />" onClick="javascript:scriptScope.saveEntries();"><fmt:message key="dialog.admin.catalog.management.additionalFields.apply" /></button>
			</span>
			<!-- CONTENT END -->
		</div>
	</div>

</body>
</html>
