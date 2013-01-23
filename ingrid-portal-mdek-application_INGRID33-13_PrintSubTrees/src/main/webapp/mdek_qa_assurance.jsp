<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
    dojo.require("dijit.layout.TabContainer");
    dojo.require("dojo.date.locale");
    
var scriptScopeQaA = _container_;

// display 10 datasets per page
var resultsPerPage = 10;
var tableIdList = ["qaAssignedObjTable", "qaAssignedAdrTable", "qaModifiedObjTable", "qaModifiedAdrTable",
	"qaExpiredObjTable", "qaExpiredAdrTable", "qaSpatialObjTable"];

dojo.addOnLoad(function() {
    createDomElements();
	initTables();
	initSortFunctions();
	reloadTables();
});

function createDomElements(){
    var qaAssignedObjTableStructure = [
       {field: 'icon',name: '&nbsp;',width: '32px'},
       {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '550px'},
       {field: 'assignerUserTitle',name: "<fmt:message key='dialog.qa.assignedBy' />",width: '130px'},
       {field: 'type',name: "<fmt:message key='dialog.qa.type' />",width: '160px'},
       {field: 'assignTime',name: "<fmt:message key='dialog.qa.assignedAt' />",width: 'auto', formatter: DateCellFormatter}
    ];
    createDataGrid("qaAssignedObjTable", null, qaAssignedObjTableStructure, null);
    
    var qaAssignedAdrTableStructure = [
       {field: 'icon',name: '&nbsp;',width: '32px'},
       {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '550px'},
       {field: 'assignerUserTitle',name: "<fmt:message key='dialog.qa.assignedBy' />",width: '130px'},
       {field: 'type',name: "<fmt:message key='dialog.qa.type' />",width: '160px'},
       {field: 'assignTime',name: "<fmt:message key='dialog.qa.assignedAt' />",width: 'auto', formatter: DateCellFormatter}
    ];
    createDataGrid("qaAssignedAdrTable", null, qaAssignedAdrTableStructure, null);
    
    var qaModifiedObjTableStructure = [
       {field: 'icon',name: '&nbsp;',width: '32px'},
       {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '550px'},
       {field: 'modUserTitle',name: "<fmt:message key='dialog.qa.modUser' />",width: '130px'},
       {field: 'type',name: "<fmt:message key='dialog.qa.type' />",width: '160px'},
       {field: 'date',name: "<fmt:message key='dialog.qa.date' />",width: 'auto', formatter: DateCellFormatter}
    ];
    createDataGrid("qaModifiedObjTable", null, qaModifiedObjTableStructure, null);
    
    var qaModifiedAdrTableStructure = [
       {field: 'icon',name: '&nbsp;',width: '32px'},
       {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '550px'},
       {field: 'modUserTitle',name: "<fmt:message key='dialog.qa.modUser' />",width: '130px'},
       {field: 'type',name: "<fmt:message key='dialog.qa.type' />",width: '160px'},
       {field: 'date',name: "<fmt:message key='dialog.qa.date' />",width: 'auto', formatter: DateCellFormatter}
    ];
    createDataGrid("qaModifiedAdrTable", null, qaModifiedAdrTableStructure, null);
    
    var qaExpiredObjTableStructure = [
       {field: 'icon',name: '&nbsp;',width: '32px'},
       {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '700px'},
       {field: 'modUserTitle',name: "<fmt:message key='dialog.qa.modUser' />",width: '130px'},
       {field: 'expiryDate',name: "<fmt:message key='dialog.qa.expiredAt' />",width: 'auto', formatter: DateCellFormatter}
    ];
    createDataGrid("qaExpiredObjTable", null, qaExpiredObjTableStructure, null);
    
    var qaExpiredAdrTableStructure = [
       {field: 'icon',name: '&nbsp;',width: '32px'},
       {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '700px'},
       {field: 'modUserTitle',name: "<fmt:message key='dialog.qa.modUser' />",width: '130px'},
       {field: 'expiryDate',name: "<fmt:message key='dialog.qa.expiredAt' />",width: 'auto', formatter: DateCellFormatter}
    ];
    createDataGrid("qaExpiredAdrTable", null, qaExpiredAdrTableStructure, null);
    
    var qaSpatialObjTableStructure = [
       {field: 'icon',name: '&nbsp;',width: '32px'},
       {field: 'linkLabel',name: "<fmt:message key='dialog.qa.name' />",width: '700px'},
       {field: 'expiryDate',name: "<fmt:message key='dialog.qa.expiredAt' />",width: 'auto', formatter: DateCellFormatter}
    ];
    createDataGrid("qaSpatialObjTable", null, qaSpatialObjTableStructure, null);
}   
   

// Initialise the tables with PageNavs, sortParams, etc.
function initTables() {
	// Add PageNavs to tables
	dojo.forEach(tableIdList, function(tableId) {
		UtilGrid.getTable(tableId).pageNav = new PageNavigation({
				resultsPerPage: resultsPerPage,
				infoSpan:dojo.byId(tableId+"Info"),
				pagingSpan:dojo.byId(tableId+"Paging") });
	});

	// Add query parameters to tables.
	UtilGrid.getTable("qaAssignedObjTable").queryParameters = { workState: "QS_UEBERWIESEN", selectionType: null };
	UtilGrid.getTable("qaAssignedAdrTable").queryParameters = { workState: "QS_UEBERWIESEN", selectionType: null };
	UtilGrid.getTable("qaModifiedObjTable").queryParameters = { workState: "IN_BEARBEITUNG", selectionType: null };
	UtilGrid.getTable("qaModifiedAdrTable").queryParameters = { workState: "IN_BEARBEITUNG", selectionType: null };
	UtilGrid.getTable("qaExpiredObjTable").queryParameters = { workState: null, selectionType: "EXPIRED" };
	UtilGrid.getTable("qaExpiredAdrTable").queryParameters = { workState: null, selectionType: "EXPIRED" };
	UtilGrid.getTable("qaSpatialObjTable").queryParameters = { workState: null, selectionType: "SPATIAL_REF_EXPIRED" };

	// Add initial sortParams to tables
	dojo.forEach(tableIdList, function(tableId) {
		UtilGrid.getTable(tableId).sortParams = { sortBy: "NAME", sortAsc: true }
	});

	// Connect PageNavs with navigate functions
	dojo.forEach(tableIdList, function(tableId) {
		dojo.connect(UtilGrid.getTable(tableId).pageNav, "onPageSelected", function() { navigateTable(tableId); });
	});
}

// Init the sort functions
function initSortFunctions() {
	dojo.forEach(tableIdList, function(tableId) {
		var table = UtilGrid.getTable(tableId);
		//!!!table.onSort = dojo.curry(table, sortTable, tableId);
		// remove the standard sorter since we sort by starting a new query
		table.createSorter = function(){return null;}
	});
}


// If a table header is clicked, the sortTable function is invoked with the corresponding table
function sortTable(tableId, e) {
	var table = UtilGrid.getTable(tableId);
	var field = getFieldFromTableHeaderClickEvent(table, e);
	var sortBy = getSortIdentifierFor(field);

	if (sortBy == "") {
		return;

	} else if (sortBy == table.sortParams.sortBy) {
		table.sortParams.sortAsc = !table.sortParams.sortAsc;

	} else {
		table.sortParams.sortBy = sortBy;
		table.sortParams.sortAsc = true;
	}

	navigateTable(tableId);
}


// Convert table field identifiers to sort identifiers (de.ingrid.mdek.MdekUtils.IdcEntityOrderBy)
function getSortIdentifierFor(field) {
//	console.debug("getSortIdentifierFor("+field+") called");

	if (field == "icon") {
		return "CLASS";

	} else if (field == "linkLabel") {
		return "NAME";

	} else if (field == "expiryDate" || field == "date" || field == "assignTime") {
		return "DATE";

	} else if (field == "modUserTitle" || field == "assignerUserTitle") {
		return "USER";

	} else if (field == "state") {
		return "STATE";

	} else {
		return "";
	}
}

// Get the column identifier from the ClickEvent.
function getFieldFromTableHeaderClickEvent(table, e) {
	var source = e.target;
	var row = dojo.html.getParentByType(source,"tr");
	var cellTag = "td";
	if(row.getElementsByTagName(cellTag).length == 0){
		cellTag = "th";
	}

	var headers = row.getElementsByTagName(cellTag);
	var header = dojo.html.getParentByType(source,cellTag);

	for(var i=0; i<headers.length; i++){
		if(headers[i] == header){
			var meta = table.columns[i];
			return meta.getField();
		}
	}
	// Field was not found (should not happen), return an empty string
	return "";
}

// Reset PageNavs
function resetAllPageNavs() {
	dojo.forEach(tableIdList, function(tableId) {
		UtilGrid.getTable(tableId).pageNav.reset();
	});
}

// Reset the tables sorting parameters
function resetAllSortParams() {
	dojo.forEach(tableIdList, function(tableId) {
		var tableWidget = UtilGrid.getTable(tableId);
		tableWidget.sortParams.sortBy = "NAME";
		tableWidget.sortParams.sortAsc = true;
	});
}

// Reload all the tables on the page
function reloadTables() {
	resetAllPageNavs();
	resetAllSortParams();

	var defList = [];
	dojo.forEach(tableIdList, function(tableId) {
		defList.push(navigateTable(tableId));
	});

	enterLoadingState();

	var l1 = new dojo.DeferredList(defList, false, false, true);
	l1.addCallback(function (resultList) {
/*
		var expObjResult = resultList[0][1];
		var expAdrResult = resultList[1][1];
		var modObjResult = resultList[2][1];
		var modAdrResult = resultList[3][1];
		var qaObjResult = resultList[4][1];
		var qaAdrResult = resultList[5][1];
*/
		exitLoadingState();
	});

	l1.addErrback(function(err) {
		console.debug("Error: "+err);
		exitLoadingState();
	});
}

// NavigateTable should be called whenever the page or sortParameters have changed
// A new request will be started with the new starHit (from PageNav) and sortParameters (from the Table)
function navigateTable(tableId) {
	var table = UtilGrid.getTable(tableId);
	var startHit = table.pageNav.getStartHit();

	var def;
	if (tableId.indexOf("Obj") != -1) {
		def = getQAObjects(table.queryParameters.workState, table.queryParameters.selectionType, table.sortParams.sortBy,
			table.sortParams.sortAsc, startHit, resultsPerPage);

	} else {
		def = getQAAddresses(table.queryParameters.workState, table.queryParameters.selectionType, table.sortParams.sortBy,
			table.sortParams.sortAsc, startHit, resultsPerPage);
	}

	// After the data is loaded from the backend, update the table and return the result for the
	// following callback functions
	def.addCallback(function(res) {updateTable(tableId, res); return res; });

	return def;
}


// Update a table with new data from SearchResult (see de.ingrid.mdek.beans.query.(Object|Address)SearchResultBean)
// The connected PageNav is updated automatically
function updateTable(tableId, searchResult) {
	var table = UtilGrid.getTable(tableId);
	var resList = searchResult.resultList;
	var numHits = searchResult.numHits;
	var totalNumHits = searchResult.totalNumHits;

	if (tableId.indexOf("Obj") != -1) {
		addObjectTableIdentifiers(resList);
	} else {
		addAddressTableIdentifiers(resList);
	}
	addCommonTableIdentifiers(resList);

	//table.store.setData(resList);
    UtilStore.updateWriteStore(tableId, resList);
	table.pageNav.setTotalNumHits(totalNumHits);
	table.pageNav.updateDomNodes();
}

// Get QA Objects from the backend. The result is returned in a deferred object.
// workState - de.ingrid.mdek.MdekUtils.WorkState
// selectionType - de.ingrid.mdek.MdekUtils.IdcQAEntitiesSelectionType
// sortBy - de.ingrid.mdek.MdekUtils.IdcEntityOrderBy
// sortAsc - boolean
// startHit - int, where to start
// numHits - int, limit number of results
function getQAObjects(workState, selectionType, sortBy, sortAsc, startHit, numHits) {
	var def = new dojo.Deferred();

	ObjectService.getQAObjects(workState, selectionType, sortBy, sortAsc, startHit, numHits, {
		callback: function(result) {
			def.callback(result);
		},
		errorHandler: function(errMsg, err) {
			console.debug("Error: "+errMsg);
			def.errback(err);
		}
	});
	return def;
}

// Get QA Addresses from the backend. The result is returned in a deferred object.
// workState - de.ingrid.mdek.MdekUtils.WorkState
// selectionType - de.ingrid.mdek.MdekUtils.IdcQAEntitiesSelectionType
// sortBy - de.ingrid.mdek.MdekUtils.IdcEntityOrderBy
// sortAsc - boolean
// startHit - int, where to start
// numHits - int, limit number of results
function getQAAddresses(workState, selectionType, sortBy, sortAsc, startHit, numHits) {
	var def = new dojo.Deferred();

	AddressService.getQAAddresses(workState, selectionType, sortBy, sortAsc, startHit, numHits, {
		callback: function(result) {
			def.callback(result);
		},
		errorHandler: function(errMsg, err) {
			console.debug("Error: "+errMsg);
			def.errback(err);
		}
	});
	return def;
}

// Add labels to each dataset in dsList, so they are properly displayed in the table.
// Properties that will be set:
// ds.lastEditor, ds.assignerUser, ds.date (last modification), ds.expiryData, ds.userOperation, ds.workState
function addCommonTableIdentifiers(dsList) {
	if (dsList) {
		dojo.forEach(dsList, function(ds) {
			// modUser
			if (ds.lastEditor) {
				var modUserAddressTitle = UtilAddress.createAddressTitle(ds.lastEditor);
				ds.modUserTitle = UtilAddress.createAddressLinkLabel(ds.lastEditor.uuid, modUserAddressTitle);
			}
			// assignerUser
			if (ds.assignerUser) {
				var assignerUserAddressTitle = UtilAddress.createAddressTitle(ds.assignerUser);
				ds.assignerUserTitle = UtilAddress.createAddressLinkLabel(ds.assignerUser.uuid, assignerUserAddressTitle);
			}
			// date & expiryDate
			ds.date = dojo.date.locale.parse(ds.modificationTime, {datePattern: "dd.MM.yyyy", selector: "date"});
			ds.expiryDate = dojo.date.locale.parse(ds.modificationTime, {datePattern: "dd.MM.yyyy", selector: "date"});
 			ds.expiryDate.setDate(ds.expiryDate.getDate()+catalogData.expiryDuration);
			// state
			if (ds.userOperation) {
				ds.type = getUserOperationText(ds.userOperation);
			}
			if (ds.workState) {
				ds.state = getWorkStateText(ds.workState);
			}
		});
	}
}

// Add titles to all objects in objList (escape obj.objectName and write it to obj.title)
function addObjectTableIdentifiers(objList) {
	if (objList) {
		dojo.forEach(objList, function(obj) {
			//!!!obj.title = dojo.string.escape("html", obj.objectName);
		});
		UtilList.addObjectLinkLabels(objList);
		UtilList.addIcons(objList);
	}
}

// Add titles to all objects in objList (escape adr title and write it to adr.title)
function addAddressTableIdentifiers(adrList) {
	if (adrList) {
		dojo.forEach(adrList, function(adr) {
			adr.title = UtilAddress.createAddressTitle(adr);
		});
		UtilList.addAddressLinkLabels(adrList);
		UtilList.addIcons(adrList);
	}

}

// Get The localized text for a userOperation (NEW, EDITED, DELETED)
function getUserOperationText(userOperation) {
	return message.get('general.userOperation.'+ userOperation);
}

// Get The localized text for a workState (Q, R)
function getWorkStateText(workState) {
	return message.get('general.workState.'+ workState);
}


function enterLoadingState() {
	dojo.byId("qaLoadingZone").style.display = "block";
	dojo.byId("qaContent").style.visibility = "hidden";
}

function exitLoadingState() {
	dojo.byId("qaLoadingZone").style.display = "none";
	dojo.byId("qaContent").style.visibility = "visible";
}

scriptScopeQaA.reloadPage = function() {
	reloadTables();
}

</script>
</head>

<body>
	<div class="contentBlockWhite">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=quality-assurance-0#quality-assurance-0', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
		</div>

		<span class="label" id="qaLoadingZone">
			<div z-index="100">
		        <img src="img/ladekreis.gif" style="background-color:#FFFFFF;" />
		        <label><fmt:message key="general.loading.data.long" /> ...</label>
		    </div>
		</span>

		<div class="content" id="qaContent" style="visibility:hidden;">
        
			<div class="spacer"></div>
			<button dojoType="dijit.form.Button" title="<fmt:message key="dialog.qa.refresh" />" onClick="javascript:reloadTables();"><fmt:message key="dialog.qa.refresh" /></button>
			<div class="spacer"></div>

			<span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 7070)"><fmt:message key="dialog.qa.assignedObjects" /></label></span>

        	<div id="qaAssignedDSTabContainer" dojoType="dijit.layout.TabContainer" doLayout="false" selectedChild="qaAssignedObjContentPane">

        		<div id="qaAssignedObjContentPane" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.qa.objects" />" style="overflow:hidden;">
					<div class="inputContainer">
						<div class="listInfo wide">
							<span id="qaAssignedObjTableInfo" class="searchResultsInfo">&nbsp;</span>
							<span id="qaAssignedObjTablePaging" class="searchResultsPaging">&nbsp;</span>
							<div class="fill"></div>
						</div>
		
				        <div class="tableContainer">
				            <div id="qaAssignedObjTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
						</div> <!-- tableContainer end -->
					</div> <!-- inputContainer end -->
				</div> <!-- ContentPane -->

        		<div id="qaAssignedAdrContentPane" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.qa.addresses" />" style="overflow:hidden;">
					<div class="inputContainer">
						<div class="listInfo wide">
							<span id="qaAssignedAdrTableInfo" class="searchResultsInfo">&nbsp;</span>
							<span id="qaAssignedAdrTablePaging" class="searchResultsPaging">&nbsp;</span>
							<div class="fill"></div>
						</div>
		
				        <div class="tableContainer">
				            <div id="qaAssignedAdrTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
						</div> <!-- tableContainer end -->
					</div> <!-- inputContainer end -->
				</div> <!-- ContentPane -->
			</div> <!-- TabContainer -->
			<div class="spacer"></div>
			<span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 7071)"><fmt:message key="dialog.qa.modifiedObjects" /></label></span>

        	<div id="qaModifiedDSTabContainer" dojoType="dijit.layout.TabContainer" doLayout="false" selectedChild="qaModifiedObjContentPane">

        		<div id="qaModifiedObjContentPane" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.qa.objects" />" style="overflow:hidden;">
					<div class="inputContainer">
						<div class="listInfo wide">
							<span id="qaModifiedObjTableInfo" class="searchResultsInfo">&nbsp;</span>
							<span id="qaModifiedObjTablePaging" class="searchResultsPaging">&nbsp;</span>
							<div class="fill"></div>
						</div>
		
				        <div class="tableContainer">
				            <div id="qaModifiedObjTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
						</div> <!-- tableContainer end -->
					</div> <!-- inputContainer end -->
				</div> <!-- ContentPane -->

        		<div id="qaModifiedAdrContentPane" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.qa.addresses" />" style="overflow:hidden;">
					<div class="inputContainer">
						<div class="listInfo wide">
							<span id="qaModifiedAdrTableInfo" class="searchResultsInfo">&nbsp;</span>
							<span id="qaModifiedAdrTablePaging" class="searchResultsPaging">&nbsp;</span>
							<div class="fill"></div>
						</div>
		
				        <div class="tableContainer">
				            <div id="qaModifiedAdrTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
						</div> <!-- tableContainer end -->
					</div> <!-- inputContainer end -->
				</div> <!-- ContentPane -->
			</div> <!-- TabContainer -->
			<div class="spacer"></div>
			<span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 7072)"><fmt:message key="dialog.qa.expiredObjects" /></label></span>

        	<div id="qaExpiredDSTabContainer" dojoType="dijit.layout.TabContainer" doLayout="false" selectedChild="qaExpiredObjContentPane">

				<div id="qaExpiredObjContentPane" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.qa.objects" />" style="overflow:hidden;">
					<div class="inputContainer">
						<div class="listInfo wide">
							<span id="qaExpiredObjTableInfo" class="searchResultsInfo">&nbsp;</span>
							<span id="qaExpiredObjTablePaging" class="searchResultsPaging">&nbsp;</span>
							<div class="fill"></div>
						</div>
		
				        <div class="tableContainer">
				            <div id="qaExpiredObjTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
						</div> <!-- tableContainer end -->
					</div> <!-- inputContainer end -->
				</div> <!-- ContentPane -->

				<div id="qaExpiredAdrContentPane" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.qa.addresses" />" style="overflow:hidden;">
					<div class="inputContainer">
						<div class="listInfo wide">
							<span id="qaExpiredAdrTableInfo" class="searchResultsInfo">&nbsp;</span>
							<span id="qaExpiredAdrTablePaging" class="searchResultsPaging">&nbsp;</span>
							<div class="fill"></div>
						</div>
		
				        <div class="tableContainer">
				            <div id="qaExpiredAdrTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
						</div> <!-- tableContainer end -->
					</div> <!-- inputContainer end -->
				</div> <!-- ContentPane -->
			</div> <!-- TabContainer -->
			<div class="spacer"></div>

			<span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 7073)"><fmt:message key="dialog.qa.spatialObjects" /></label></span>

			<div id="qaSpatialObjContentPane" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.qa.objects" />" style="overflow:hidden;">
				<div class="inputContainer">
					<div class="listInfo wide">
						<span id="qaSpatialObjTableInfo" class="searchResultsInfo">&nbsp;</span>
						<span id="qaSpatialObjTablePaging" class="searchResultsPaging">&nbsp;</span>
						<div class="fill"></div>
					</div>
	
			        <div class="tableContainer">
			            <div id="qaSpatialObjTable" minRows="10" contextMenu="none" defaultHideScrollbar="true"></div>
					</div> <!-- tableContainer end -->
				</div> <!-- inputContainer end -->
			</div> <!-- ContentPane -->

		</div> <!-- content end -->
	</div> <!-- contentBlock end -->
</body>
</html>
