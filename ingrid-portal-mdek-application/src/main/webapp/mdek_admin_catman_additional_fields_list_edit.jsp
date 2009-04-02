<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {
	createTableContextMenu();
	initTableData();
});

function createTableContextMenu() {
	var contextMenu = dojo.widget.createWidget("ingrid:TableContextMenu");
	contextMenu.addItemObject(ingrid.widget.TableContextMenu.prototype.actions.DELETE);
	dojo.widget.byId("fieldsTable").setContextMenu(contextMenu);
}

function initTableData() {
	if (_container_.customParams && _container_.customParams.listEntries) {
		var listEntries = _container_.customParams.listEntries;
		var tableData = UtilList.listToTableData(listEntries);
		UtilList.addTableIndices(tableData);
		dojo.widget.byId("fieldsTable").store.setData(tableData);
	}
}

_container_.addOnUnload(function() {
	dojo.widget.byId('fieldsTable').removeContextMenu();

	if (_container_.customParams && _container_.customParams.resultHandler) {
		if (_container_.customParams.resultHandler.fired == -1) {
			_container_.customParams.resultHandler.errback();
		}
	}
});

scriptScope.saveEntries = function() {
	if (_container_.customParams && _container_.customParams.resultHandler) {
		var resultHandler = _container_.customParams.resultHandler;
		var tableData = dojo.widget.byId("fieldsTable").store.getData();
		var resultList = UtilList.tableDataToList(tableData);
		resultHandler.callback(resultList);
	}
	_container_.closeWindow();
}

</script>
</head>

<body>

<div dojoType="ContentPane">

	<div class="contentBlockWhite top w478">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:dialog.showContextHelp(arguments[0], 8084)" title="Hilfe">[?]</a>
		</div>
		<div id="userContent" class="content h135">

			<div class="spacer"></div>
			<div class="spacer"></div>
			<!-- CONTENT START -->
			<div class="inputContainer noSpaceBelow w478">
				<div class="tableContainer headHiddenRows6 w462">
					<div class="cellEditors" id="fieldsTableEditors">
						<div dojoType="ingrid:ValidationTextbox" class="w405" widgetId="fieldEditor"></div>
					</div>
					<table id="fieldsTable" dojoType="ingrid:FilteringTable" minRows="6" headClass="hidden" cellspacing="0" class="filteringTable interactive w462">
						<thead>
							<tr>
								<th field="title" dataType="String" width="443" editor="fieldEditor"></th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				<div class="spacer"></div>
			</div>

			<span style="float:right;">
				<button dojoType="ingrid:Button" title="Hinzuf&uuml;gen" onClick="javascript:scriptScope.saveEntries();"><fmt:message key="dialog.admin.catalog.management.additionalFields.apply" /></button>
			</span>
			<!-- CONTENT END -->
		</div>
	</div>
</div>

</body>
</html>
