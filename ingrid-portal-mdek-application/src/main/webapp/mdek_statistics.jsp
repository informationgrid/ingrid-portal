<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
    dojo.require("dijit.layout.TabContainer");
    dojo.require("ingrid.dijit.CustomTree");
    dojo.require("ingrid.dijit.tree.LazyTreeStoreModel");
    
var scriptScopeStatistic = _container_;

var resultsPerPage = 10;
var statsFreeTermsObjPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("statsFreeTermsObjTableInfo"), pagingSpan:dojo.byId("statsFreeTermsObjTablePaging") });
var statsFreeTermsAdrPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("statsFreeTermsAdrTableInfo"), pagingSpan:dojo.byId("statsFreeTermsAdrTablePaging") });
var statsThesTermsObjPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("statsThesTermsObjTableInfo"), pagingSpan:dojo.byId("statsThesTermsObjTablePaging") });
var statsThesTermsAdrPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("statsThesTermsAdrTableInfo"), pagingSpan:dojo.byId("statsThesTermsAdrTablePaging") });

dojo.addOnLoad(function() {
    
    var def = createDomElements();
    
    def.addCallback(function() {
        console.debug("init2");
    	dojo.connect(statsFreeTermsObjPageNav, "onPageSelected", function() {
    		showLoadingZone();
    		var def = navigateFreeTermsObjTable();
    		def.addBoth(hideLoadingZone);
    	});
    	dojo.connect(statsFreeTermsAdrPageNav, "onPageSelected", function() {
    		showLoadingZone();
    		var def = navigateFreeTermsAdrTable();
    		def.addBoth(hideLoadingZone);
    	});
    	dojo.connect(statsThesTermsObjPageNav, "onPageSelected", function() {
    		showLoadingZone();
    		var def = navigateThesTermsObjTable();
    		def.addBoth(hideLoadingZone);	
    	});
    	dojo.connect(statsThesTermsAdrPageNav, "onPageSelected", function() {
    		showLoadingZone();
    		var def = navigateThesTermsAdrTable();
    		def.addBoth(hideLoadingZone);	
    	});
    
    	initStatsTree();
    
    	resetStatsObjectsTable();
    	resetStatsAdrTable();
    	resetThesaurusTable("statsThesTermsObjTable");
    	resetThesaurusTable("statsFreeTermsObjTable");
    	resetThesaurusTable("statsThesTermsAdrTable");
    	resetThesaurusTable("statsFreeTermsAdrTable");
        
        dijit.byId("contentPane").resize();
    });
});

function createDomElements() {
    createCustomTree("statisticsTree", null, "id", "title", loadTreeData);
    dijit.byId("statisticsTree").set('showRoot', false);
    dijit.byId("statisticsTree").set('showTitle', true);
    //dijit.byId("statisticsTree").set('title', "All");
    dijit.byId("statisticsTree").set('openOnClick', false);
    
    var statsObjectsTableStructure = [
       {field: 'type',name: '&nbsp;',width: '40px'},
       {field: 'objectClass',name: "<fmt:message key='dialog.statistics.objClass' />",width: '364px', formatter:LocalizeString}, 
       {field: 'amount',name: "<fmt:message key='dialog.statistics.num' />",width: '80px'}, 
       {field: 'percentage',name: "<fmt:message key='dialog.statistics.percentage' />",width: '100px'}, 
       {field: 'published',name: "<fmt:message key='dialog.statistics.header.published' />", toolTip:"<fmt:message key='dialog.statistics.tooltip.published' />", width: '50px'}, 
       {field: 'modified',name: "<fmt:message key='dialog.statistics.header.modified' />", toolTip:"<fmt:message key='dialog.statistics.tooltip.modified' />",width: '50px'}, 
       {field: 'qa',name: "<fmt:message key='dialog.statistics.header.qa' />", toolTip:"<fmt:message key='dialog.statistics.tooltip.qa' />",width: '50px'},  
       {field: 'returned',name: "<fmt:message key='dialog.statistics.header.returned' />", toolTip:"<fmt:message key='dialog.statistics.tooltip.returned' />",width: 'auto'}
    ];
    var def1 = createDataGrid("statsObjectsTable", null, statsObjectsTableStructure, null, undefined, "js/data/statisticTableObject.json");
    
    var statsAdrTableStructure = [
       {field: 'type',name: '&nbsp;',width: '40px'},
       {field: 'addressType',name: "<fmt:message key='dialog.statistics.adrClass' />",width: '364px', formatter:LocalizeString}, 
       {field: 'amount',name: "<fmt:message key='dialog.statistics.num' />",width: '80px'}, 
       {field: 'percentage',name: "<fmt:message key='dialog.statistics.percentage' />",width: '100px'},
       {field: 'published',name: "<fmt:message key='dialog.statistics.header.published' />", toolTip:"<fmt:message key='dialog.statistics.tooltip.published' />", width: '50px'}, 
       {field: 'modified',name: "<fmt:message key='dialog.statistics.header.modified' />", toolTip:"<fmt:message key='dialog.statistics.tooltip.modified' />",width: '50px'}, 
       {field: 'qa',name: "<fmt:message key='dialog.statistics.header.qa' />", toolTip:"<fmt:message key='dialog.statistics.tooltip.qa' />",width: '50px'},  
       {field: 'returned',name: "<fmt:message key='dialog.statistics.header.returned' />", toolTip:"<fmt:message key='dialog.statistics.tooltip.returned' />",width: 'auto'}
    ];
    var def2 = createDataGrid("statsAdrTable", null, statsAdrTableStructure, null, undefined, "js/data/statisticTableAddress.json");
    
    var statsFreeTermsObjTableStructure = [
       {field: 'term',name: "<fmt:message key='dialog.statistics.objTerms' />",width: '524px'},
       {field: 'numOccurences',name: "<fmt:message key='dialog.statistics.num' />",width: '80px'},  
       {field: 'percentage',name: "<fmt:message key='dialog.statistics.percentage' />",width: 'auto'}
    ];
    createDataGrid("statsFreeTermsObjTable", null, statsFreeTermsObjTableStructure, null);
    
    var statsFreeTermsAdrTableStructure = [
       {field: 'term',name: "<fmt:message key='dialog.statistics.adrTerms' />",width: '524px'},
       {field: 'numOccurences',name: "<fmt:message key='dialog.statistics.num' />",width: '80px'},  
       {field: 'percentage',name: "<fmt:message key='dialog.statistics.percentage' />",width: 'auto'}
    ];
    createDataGrid("statsFreeTermsAdrTable", null, statsFreeTermsAdrTableStructure, null);
    
    var statsThesTermsObjTableStructure = [
       {field: 'term',name: "<fmt:message key='dialog.statistics.objTerms' />",width: '524px'},
       {field: 'numOccurences',name: "<fmt:message key='dialog.statistics.num' />",width: '80px'},  
       {field: 'percentage',name: "<fmt:message key='dialog.statistics.percentage' />",width: 'auto'}
    ];
    createDataGrid("statsThesTermsObjTable", null, statsThesTermsObjTableStructure, null);
    
    var statsThesTermsAdrTableStructure = [
       {field: 'term',name: "<fmt:message key='dialog.statistics.adrTerms' />",width: '524px'},
       {field: 'numOccurences',name: "<fmt:message key='dialog.statistics.num' />",width: '80px'},  
       {field: 'percentage',name: "<fmt:message key='dialog.statistics.percentage' />",width: 'auto'}
    ];
    createDataGrid("statsThesTermsAdrTable", null, statsThesTermsAdrTableStructure, null);
    
    return new dojo.DeferredList([def1, def2]);
}

function loadTreeData(node, callback_function){
    var parentItem = node.item;
    var prefix = "statistic_";
    var store = dijit.byId("statisticsTree").model.store;
    var def = UtilTree.getSubTree(parentItem, prefix.length);
    
    def.addCallback(function(data){
        if (parentItem.root) {
            dojo.forEach(data, function(entry){
                entry.uuid = entry.id;
                entry.id = prefix + entry.id;
                store.newItem(entry);
            });
        }
        else {
            //parentItem.id = "groupAdmin_"+parentItem.id;
            dojo.forEach(data, function(entry){
                //entry.id = prefix+entry.id;
                //entry.title = dojo.string.escape("html", entry.title);
                entry.uuid = entry.id;
                entry.id = prefix+entry.id;
                store.newItem(entry, {
                    parent: parentItem,
                    attribute: "children"
                });
            });
        }
        callback_function();
    });
    def.addErrback(function(res){
        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='tree.loadError' />", dialog.WARNING);
        console.debug(res);
        return res;
    });
    return def;
}

// Set the data for the objects statistics table
// stats is an object with maps containing information about the number of objects in the different states.
// See ObjectStatisticsResultBean
// Example:
// stats = { 0:{numTotal:23, classMap:{v:14, b:7, q:2, r:0}}, 1:{numTotal:18, classMap:{v:11, b:3, q:1, r:3}}, ... };
function setStatsObjectsTableContent(stats) {
	var amounts = [];
	var percentages = [];
	var numTotal = 0;
	var numObjs = {v:0,b:0,q:0,r:0};

	for (var i = 0; i < 7; ++i) {
		var classMap = stats[i].classMap;
		amounts.push(classMap.V + classMap.B + classMap.Q + classMap.R);
		numObjs.v += classMap.V;
		numObjs.b += classMap.B;
		numObjs.q += classMap.Q;
		numObjs.r += classMap.R;
		numTotal += stats[i].numTotal;
	}

	if (numTotal <= 0) {
		percentages = [0, 0, 0, 0, 0, 0];

	} else {
		for (var i in amounts) {
			percentages.push(amounts[i]*100 / numTotal);
		}
	}
    
	// Update table data
	var storeData = UtilGrid.getTableData("statsObjectsTable");
	UtilGrid.updateTableDataRowAttr("statsObjectsTable", 0, "amount", numTotal);
	UtilGrid.updateTableDataRowAttr("statsObjectsTable", 0, "percentage", 100);
	UtilGrid.updateTableDataRowAttr("statsObjectsTable", 0, "published", numObjs.v);
	UtilGrid.updateTableDataRowAttr("statsObjectsTable", 0, "modified", numObjs.b);
	UtilGrid.updateTableDataRowAttr("statsObjectsTable", 0, "qa", numObjs.q);
	UtilGrid.updateTableDataRowAttr("statsObjectsTable", 0, "returned", numObjs.r);

	for (var i = 1; i < storeData.length; ++i) {
		UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, "amount", amounts[i-1]);
		UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, "percentage", Math.round(percentages[i-1]));
		UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, "published", stats[i-1].classMap.V);
		UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, "modified", stats[i-1].classMap.B);
		UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, "qa", stats[i-1].classMap.Q);
		UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, "returned", stats[i-1].classMap.R);
	}
    UtilGrid.getTable("statsObjectsTable").invalidate();
}

// Set the data for the address statistics table
// stats is an object with maps containing information about the number of addresses in the different states.
// See AddressStatisticsResultBean
// Example:
// stats = { 0:{numTotal:23, classMap:{v:14, b:7, q:2, r:0}}, 1:{numTotal:18, classMap:{v:11, b:3, q:1, r:3}}, ... };
function setStatsAdrTableContent(stats) {
	var amounts = [0, 0, 0, 0];
	var percentages = [0, 0, 0, 0];
	var numTotal = 0;
	var numAdrs = {v:0,b:0,q:0,r:0};

	for (var i = 0; i < 4; ++i) {
		var classMap = stats[i+""].classMap;

		amounts[i] = classMap.V + classMap.B + classMap.Q + classMap.R;
		numAdrs.v += classMap.V;
		numAdrs.b += classMap.B;
		numAdrs.q += classMap.Q;
		numAdrs.r += classMap.R;
		numTotal += stats[i+""].numTotal;
	}

	if (numTotal > 0) {
		for (var i = 0; i < amounts.length; ++i) {
			percentages[i] = amounts[i]*100 / numTotal;
		}
	}

	// Update table data
    var storeData = UtilGrid.getTableData("statsAdrTable");
	UtilGrid.updateTableDataRowAttr("statsAdrTable", 0, "amount", numTotal);
	UtilGrid.updateTableDataRowAttr("statsAdrTable", 0, "percentage", 100);
	UtilGrid.updateTableDataRowAttr("statsAdrTable", 0, "published", numAdrs.v);
	UtilGrid.updateTableDataRowAttr("statsAdrTable", 0, "modified", numAdrs.b);
	UtilGrid.updateTableDataRowAttr("statsAdrTable", 0, "qa", numAdrs.q);
	UtilGrid.updateTableDataRowAttr("statsAdrTable", 0, "returned", numAdrs.r);

	for (var i = 1; i < storeData.length; ++i) {
		UtilGrid.updateTableDataRowAttr("statsAdrTable", i, "amount", amounts[i-1]);
		UtilGrid.updateTableDataRowAttr("statsAdrTable", i, "percentage", Math.round(percentages[i-1]));
		UtilGrid.updateTableDataRowAttr("statsAdrTable", i, "published", stats[(i-1)+""].classMap.V);
		UtilGrid.updateTableDataRowAttr("statsAdrTable", i, "modified", stats[(i-1)+""].classMap.B);
		UtilGrid.updateTableDataRowAttr("statsAdrTable", i, "qa", stats[(i-1)+""].classMap.Q);
		UtilGrid.updateTableDataRowAttr("statsAdrTable", i, "returned", stats[(i-1)+""].classMap.R);
	}
    UtilGrid.getTable("statsAdrTable").invalidate();
}

function setStatsThesaurusTermsTable(tableId, thesaurusResult) {
	var numTotal = thesaurusResult.numTermsTotal;
	var termList = thesaurusResult.searchTermList;

	for (var i in termList) {
		termList[i].percentage = Math.round(termList[i].numOccurences*100 / numTotal);
	}

	// Update table data
	termList.unshift({term:"<fmt:message key='dialog.statistics.numTotal' />", numOccurences:numTotal, percentage:100});
	//store.setData(termList);
    UtilStore.updateWriteStore(tableId, termList);

	var pageNav;
	
	if (tableId == "statsThesTermsObjTable") {
		pageNav = statsThesTermsObjPageNav;
	} else if (tableId == "statsThesTermsAdrTable") {
		pageNav = statsThesTermsAdrPageNav;	
	} else if (tableId == "statsFreeTermsObjTable") {
		pageNav = statsFreeTermsObjPageNav;	
	} else if (tableId == "statsFreeTermsAdrTable") {
		pageNav = statsFreeTermsAdrPageNav;	
	}

	pageNav.setTotalNumHits(thesaurusResult.numHitsTotal);
	pageNav.updateDomNodes();
}


function navigateThesTermsObjTable(objUuid) {
    //console.debug("selected: " + dijit.byId("statisticsTree").selected);
	//var objUuid = dijit.byId("statisticsTree").selectedItem.uuid;
	if (objUuid == "objectRoot") {
		objUuid = null;
	}
	var startHit = statsThesTermsObjPageNav.getStartHit();

	var def = getObjectThesaurusStatistics(objUuid, true, startHit, resultsPerPage);

	def.addCallback(function(thesResult) {
		setStatsThesaurusTermsTable("statsThesTermsObjTable", thesResult);
	});

	return def;
}

function navigateFreeTermsObjTable(objUuid) {
	//var objUuid = dijit.byId("statisticsTree").selectedItem.uuid;
	if (objUuid == "objectRoot") {
		objUuid = null;
	}
	var startHit = statsFreeTermsObjPageNav.getStartHit();

	var def = getObjectThesaurusStatistics(objUuid, false, startHit, resultsPerPage);

	def.addCallback(function(thesResult) {
		setStatsThesaurusTermsTable("statsFreeTermsObjTable", thesResult);
	});

	return def;
}

function navigateThesTermsAdrTable(adrUuid) {
	//var adrUuid = dijit.byId("statisticsTree").selectedNode.uuid;

	var freeAddressesOnly = (adrUuid == "addressFreeRoot");
	if (freeAddressesOnly || adrUuid == "addressRoot") {
		adrUuid = null;
	}
	var startHit = statsThesTermsAdrPageNav.getStartHit();

	var def = getAddressThesaurusStatistics(adrUuid, freeAddressesOnly, true, startHit, resultsPerPage);

	def.addCallback(function(thesResult) {
		setStatsThesaurusTermsTable("statsThesTermsAdrTable", thesResult);
	});

	return def;
}

function navigateFreeTermsAdrTable(adrUuid) {
	//var adrUuid = dijit.byId("statisticsTree").selectedNode.uuid;

	var freeAddressesOnly = (adrUuid == "addressFreeRoot");
	if (freeAddressesOnly || adrUuid == "addressRoot") {
		adrUuid = null;
	}
	var startHit = statsFreeTermsAdrPageNav.getStartHit();

	var def = getAddressThesaurusStatistics(adrUuid, freeAddressesOnly, false, startHit, resultsPerPage);

	def.addCallback(function(thesResult) {
		setStatsThesaurusTermsTable("statsFreeTermsAdrTable", thesResult);
	});

	return def;
}

// The user clicked the catalog root node in the tree. Show statistics for the complete catalog.
function showStatisticsForCatalog() {
	showLoadingZone();

	resetThesaurusTablePageNav("statsThesTermsObjTable");
	resetThesaurusTablePageNav("statsFreeTermsObjTable");
	resetThesaurusTablePageNav("statsThesTermsAdrTable");
	resetThesaurusTablePageNav("statsFreeTermsAdrTable");

	var d1 = getObjectStatistics(null);
	var d2 = getAddressStatistics(null, false);
	var d3 = navigateThesTermsObjTable();
	var d4 = navigateFreeTermsObjTable();
	var d5 = navigateThesTermsAdrTable();
	var d6 = navigateFreeTermsAdrTable();

	var l1 = new dojo.DeferredList([d1, d2, d3, d4, d5, d6], false, false, true);

	l1.addCallback(function(resultList) {
		setStatsObjectsTableContent(resultList[0][1]);
		setStatsAdrTableContent(resultList[1][1]);
		hideLoadingZone();
	});

	l1.addErrback(function(err) {
		console.debug("Error: "+err);
		hideLoadingZone();
	});
}

// The user clicked the object root node in the tree. Show statistics for all objects.
function showStatisticsForAllObjects() {
	showLoadingZone();

	resetStatsAdrTable();
	resetThesaurusTablePageNav("statsThesTermsObjTable");
	resetThesaurusTablePageNav("statsFreeTermsObjTable");
	resetThesaurusTable("statsThesTermsAdrTable");
	resetThesaurusTable("statsFreeTermsAdrTable");

	var d1 = getObjectStatistics(null);
	var d2 = navigateThesTermsObjTable();
	var d3 = navigateFreeTermsObjTable();

	var l1 = new dojo.DeferredList([d1, d2, d3], false, false, true);
console.debug("show all");
	l1.addCallback(function(resultList) {
		setStatsObjectsTableContent(resultList[0][1]);
		hideLoadingZone();
	});

	l1.addErrback(function(err) {
		console.debug("Error: "+err);
		hideLoadingZone();
	});
}

// The user clicked the address root node in the tree. Show statistics for all addresses.
function showStatisticsForAllAddresses() {
	showLoadingZone();

	resetStatsObjectsTable();
	resetThesaurusTable("statsThesTermsObjTable");
	resetThesaurusTable("statsFreeTermsObjTable");
	resetThesaurusTablePageNav("statsThesTermsAdrTable");
	resetThesaurusTablePageNav("statsFreeTermsAdrTable");

	var d1 = getAddressStatistics(null, false);
	var d2 = navigateThesTermsAdrTable();
	var d3 = navigateFreeTermsAdrTable();

	var l1 = new dojo.DeferredList([d1, d2, d3], false, false, true);

	l1.addCallback(function(resultList) {
		setStatsAdrTableContent(resultList[0][1]);
		hideLoadingZone();
	});

	l1.addErrback(function(err) {
		console.debug("Error: "+err);
		hideLoadingZone();
	});
}

// The user clicked the free address root node in the tree. Show statistics for all free addresses.
function showStatisticsForAllFreeAddresses() {
	showLoadingZone();

	resetStatsObjectsTable();
	resetThesaurusTable("statsThesTermsObjTable");
	resetThesaurusTable("statsFreeTermsObjTable");
	resetThesaurusTablePageNav("statsThesTermsAdrTable");
	resetThesaurusTablePageNav("statsFreeTermsAdrTable");

	var d1 = getAddressStatistics(null, true);
	var d2 = navigateThesTermsAdrTable();
	var d3 = navigateFreeTermsAdrTable();

	var l1 = new dojo.DeferredList([d1, d2, d3], false, false, true);

	l1.addCallback(function(resultList) {
		setStatsAdrTableContent(resultList[0][1]);
		hideLoadingZone();
	});

	l1.addErrback(function(err) {
		console.debug("Error: "+err);
		hideLoadingZone();
	});
}

// The user clicked a specific address node in the tree. Show statistics for the address.
function showStatisticsForAddress(adrUuid) {
	showLoadingZone();

	resetStatsObjectsTable();
	resetThesaurusTable("statsThesTermsObjTable");
	resetThesaurusTable("statsFreeTermsObjTable");
	resetThesaurusTablePageNav("statsThesTermsAdrTable");
	resetThesaurusTablePageNav("statsFreeTermsAdrTable");

	var d1 = getAddressStatistics(adrUuid, false);
	var d2 = navigateThesTermsAdrTable(adrUuid);
	var d3 = navigateFreeTermsAdrTable(adrUuid);

	var l1 = new dojo.DeferredList([d1, d2, d3], false, false, true);

	l1.addCallback(function(resultList) {
		setStatsAdrTableContent(resultList[0][1]);
		hideLoadingZone();
	});

	l1.addErrback(function(err) {
		console.debug("Error: "+err);
		hideLoadingZone();
	});
}

// The user clicked a specific object node in the tree. Show statistics for the object.
function showStatisticsForObject(uuid) {
	showLoadingZone();

	resetStatsAdrTable();
	resetThesaurusTablePageNav("statsThesTermsObjTable");
	resetThesaurusTablePageNav("statsFreeTermsObjTable");
	resetThesaurusTable("statsThesTermsAdrTable");
	resetThesaurusTable("statsFreeTermsAdrTable");

	var d1 = getObjectStatistics(uuid);
	var d2 = navigateThesTermsObjTable(uuid);
	var d3 = navigateFreeTermsObjTable(uuid);

	var l1 = new dojo.DeferredList([d1, d2, d3], false, false, true);

	l1.addCallback(function(resultList) {
        console.debug("set table");
		setStatsObjectsTableContent(resultList[0][1]);
        console.debug("finished set table");
		hideLoadingZone();
	});

	l1.addErrback(function(err) {
		console.debug("Error: "+err);
		hideLoadingZone();
	});
}


// This function is called when the user clicks a node in the tree
function onStatisticsNodeSelected(item, node) {
    console.debug("node: " + item.uuid);
	//var node = message.node;
	var nodeId = null;
	if (typeof(item.id) != "undefined") {
		// Remove the prefix from the nodeId
		nodeId = item.uuid[0];
	}

	if (nodeId == null) {
		showStatisticsForCatalog();

	} else if (nodeId == "objectRoot") {
		showStatisticsForAllObjects();

	} else if (nodeId == "addressRoot") {
		showStatisticsForAllAddresses();

	} else if (nodeId == "addressFreeRoot") {
		showStatisticsForAllFreeAddresses();

	} else if (item.nodeAppType == "A") {
		showStatisticsForAddress(nodeId);

	} else if (item.nodeAppType == "O") {
		showStatisticsForObject(nodeId);

	} else {
		// TODO Show error message?
		console.debug("clicked unknown node with id '"+nodeId+"'");
	}
}


function initStatsTree() {
	// Listen for nodeSelections
	//var treeListener = dijit.byId('statisticsTreeListener');
	//dojo.subscribe("", onStatisticsNodeSelected);
    dojo.connect(dijit.byId("statisticsTree"), "onClick", onStatisticsNodeSelected);

	// init the context menu
	//var contextMenu = dijit.byId('statsTreeContextMenu');
	//contextMenu.treeController = dijit.byId('statisticsTreeController');
	//contextMenu.addItem("<fmt:message key='tree.reload' />", 'reload', reloadStatsTree);
}

function getObjectStatistics(objUuid) {
	var def = new dojo.Deferred();

	ObjectService.getObjectStatistics(objUuid, {
		callback: function(objStats) {
			def.callback(objStats.resultMap);
		},
		errorHandler: function(errMsg, err) {
			console.debug("Error: "+errMsg);
			def.errback(err);
		}
	});

	return def;
}

function getObjectThesaurusStatistics(objUuid, thesaurusTerms, startHit, numHits) {
	var def = new dojo.Deferred();

	ObjectService.getObjectThesaurusStatistics(objUuid, thesaurusTerms, startHit, numHits, {
		callback: function(objThesStats) {
			def.callback(objThesStats);
		},
		errorHandler: function(errMsg, err) {
			console.debug("Error: "+errMsg);
			def.errback(err);
		}
	});

	return def;
}

function getAddressStatistics(adrUuid, freeAddressesOnly) {
	var def = new dojo.Deferred();

	AddressService.getAddressStatistics(adrUuid, freeAddressesOnly, {
		callback: function(adrStats) {
			def.callback(adrStats.resultMap);
		},
		errorHandler: function(errMsg, err) {
			console.debug("Error: "+errMsg);
			def.errback(err);
		}
	});

	return def;
}

function getAddressThesaurusStatistics(adrUuid, freeAddressesOnly, thesaurusTerms, startHit, numHits) {
	var def = new dojo.Deferred();

	AddressService.getAddressThesaurusStatistics(adrUuid, freeAddressesOnly, thesaurusTerms, startHit, numHits, {
		callback: function(adrThesStats) {
			def.callback(adrThesStats);
		},
		errorHandler: function(errMsg, err) {
			console.debug("Error: "+errMsg);
			def.errback(err);
		}
	});

	return def;
}


function resetStatsObjectsTable() {
	// Reset table data
	var fieldIdList = ["amount", "percentage", "published", "modified", "qa", "returned"];
	var data = UtilGrid.getTableData("statsObjectsTable");
	for (var i in data) {
		dojo.forEach(fieldIdList, function(item) {
			UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, item, 0);
		});
	}
}

function resetStatsAdrTable() {
	// Reset table data
	var fieldIdList = ["amount", "percentage", "published", "modified", "qa", "returned"];
	var data = UtilGrid.getTableData("statsAdrTable");
	for (var i in data) {
		dojo.forEach(fieldIdList, function(item) {
			UtilGrid.updateTableDataRowAttr("statsAdrTable", i, item, 0);
		});
	}
}

function resetThesaurusTablePageNav(tableId) {
	var pageNav;
	
	if (tableId == "statsThesTermsObjTable") {
		pageNav = statsThesTermsObjPageNav;
	} else if (tableId == "statsThesTermsAdrTable") {
		pageNav = statsThesTermsAdrPageNav;	
	} else if (tableId == "statsFreeTermsObjTable") {
		pageNav = statsFreeTermsObjPageNav;	
	} else if (tableId == "statsFreeTermsAdrTable") {
		pageNav = statsFreeTermsAdrPageNav;	
	}

	pageNav.reset();
//	pageNav.updateDomNodes();
//	dijit.byId(tableId).store.clearData();
}

function resetThesaurusTable(tableId) {
	var pageNav;
	
	if (tableId == "statsThesTermsObjTable") {
		pageNav = statsThesTermsObjPageNav;
	} else if (tableId == "statsThesTermsAdrTable") {
		pageNav = statsThesTermsAdrPageNav;	
	} else if (tableId == "statsFreeTermsObjTable") {
		pageNav = statsFreeTermsObjPageNav;	
	} else if (tableId == "statsFreeTermsAdrTable") {
		pageNav = statsFreeTermsAdrPageNav;	
	}

	pageNav.reset();
	pageNav.updateDomNodes();
	//dijit.byId(tableId).store.clearData();
}


function reloadStatsTree() {
	var treeController = dijit.byId("statisticsTreeController");
	var rootNode = dijit.byId("statsTreeRoot");
	treeController.refreshChildren(rootNode);

	// reset tables as well since no node is selected
	resetStatsObjectsTable();
	resetStatsAdrTable();
	resetThesaurusTable("statsThesTermsObjTable");
	resetThesaurusTable("statsFreeTermsObjTable");
	resetThesaurusTable("statsThesTermsAdrTable");
	resetThesaurusTable("statsFreeTermsAdrTable");
}

scriptScopeStatistic.print = function() {
	var selectedTab = dijit.byId("statsTable").selectedChildWidget.id;
    var div = document.createElement("div");
    div.setAttribute("id", "printPreparation");
    if (selectedTab == "statsObjectsAdr") {
        this.renderTable(div, UtilGrid.getTableData("statsObjectsTable"), dojo.map(UtilGrid.getTable("statsObjectsTable").getColumns(), function(col){
            return col.field;
        }), dojo.map(UtilGrid.getTable("statsObjectsTable").getColumns(), function(col){
            return col.name;
        }), "", [null, LocalizeString, null, null, null, null, null, null]);
        this.renderTable(div, UtilGrid.getTableData("statsAdrTable"), dojo.map(UtilGrid.getTable("statsAdrTable").getColumns(), function(col){
            return col.field;
        }), dojo.map(UtilGrid.getTable("statsAdrTable").getColumns(), function(col){
            return col.name;
        }), "", [null, LocalizeString, null, null, null, null, null, null]);
    } else if (selectedTab == "statsFreeTerms") {
        this.renderTable(div, UtilGrid.getTableData("statsFreeTermsObjTable"), dojo.map(UtilGrid.getTable("statsFreeTermsObjTable").getColumns(), function(col){
            return col.field;
        }), dojo.map(UtilGrid.getTable("statsFreeTermsObjTable").getColumns(), function(col){
            return col.name;
        }), "", [null, LocalizeString, null, null, null, null, null, null]);
        this.renderTable(div, UtilGrid.getTableData("statsFreeTermsAdrTable"), dojo.map(UtilGrid.getTable("statsFreeTermsAdrTable").getColumns(), function(col){
            return col.field;
        }), dojo.map(UtilGrid.getTable("statsFreeTermsAdrTable").getColumns(), function(col){
            return col.name;
        }), "", [null, LocalizeString, null, null, null, null, null, null]);
    } else if (selectedTab == "statsThesaurusTerms") {
        this.renderTable(div, UtilGrid.getTableData("statsThesTermsObjTable"), dojo.map(UtilGrid.getTable("statsThesTermsObjTable").getColumns(), function(col){
            return col.field;
        }), dojo.map(UtilGrid.getTable("statsThesTermsObjTable").getColumns(), function(col){
            return col.name;
        }), "", [null, LocalizeString, null, null, null, null, null, null]);
        this.renderTable(div, UtilGrid.getTableData("statsThesTermsAdrTable"), dojo.map(UtilGrid.getTable("statsThesTermsAdrTable").getColumns(), function(col){
            return col.field;
        }), dojo.map(UtilGrid.getTable("statsThesTermsAdrTable").getColumns(), function(col){
            return col.name;
        }), "", [null, LocalizeString, null, null, null, null, null, null]);
    }
	printDivContent(null, null, div);
}

scriptScopeStatistic.renderTable = function(div, list, rowProperties, listHeader, title, cellRenderFunction) {
    if (list && list.length > 0) {
        var t = "";
        t += "<strong>" + title + "</strong><br/><br/>";
        t += "<p><table class=\"filteringTable\" cellspacing=\"0\">";
        if (listHeader && listHeader.length > 0) {
            t += "<thead class=\"fixedHeader\"><tr>";
            for (i=0; i<listHeader.length; i++) {
                t += "<th style=\"padding-right:4px\">"+listHeader[i]+"</th>";
            }
            t += "</tr></thead>";
        }
        t += "<tbody>";
        for (var i=0; i<list.length; i++) {
            if (i % 2) {
                t += "<tr class=\"alt\">";
            } else {
                t += "<tr>";
            }
            for (var j=0; j<rowProperties.length; j++) {
                if (cellRenderFunction && cellRenderFunction[j]) {
                    t += "<td style=\"padding-right:4px\">"+cellRenderFunction[j].call(this, -1, -1, list[i][rowProperties[j]])+"</td>";
                } else {
                    t += "<td style=\"padding-right:4px\">"+list[i][rowProperties[j]]+"</td>";
                }
            }
            t += "</tr>";
            
        }
        t += "</tbody></table></p>";
        div.innerHTML += t + "<br/><br/>";
    }
}

function showLoadingZone() {
    dojo.byId('statisticsLoadingZone').style.visibility = "visible";
}

function hideLoadingZone() {
    dojo.byId('statisticsLoadingZone').style.visibility = "hidden";
}


</script>
</head>

<body>
	<!-- SPLIT CONTAINER START -->
    <div dojoType="dijit.layout.BorderContainer" id="statisticsContentSection" design="headline" sizerWidth="15" layoutAlign="client">
        <!-- LEFT CONTENT PANE START -->
        <div dojoType="dijit.layout.ContentPane" class="contentContainer" id="statisticsTreeContainer" region="leading" splitter="true" style="width:300px;">
            <div id="statisticsTree">
            </div>
        </div>
        <!-- LEFT CONTENT PANE END --><!-- RIGHT CONTENT PANE START -->
        <div dojoType="dijit.layout.ContentPane" id="statisticsContentContainer" class="contentContainer" region="center">
            <div id="statisticsContentSection" class="contentBlockWhite">
                <div id="stats" class="content">
                    <span class="functionalLink onTab" style="margin-bottom: 5px;">
                        <span id="statisticsLoadingZone" style="visibility:hidden;">
                            <img src="img/ladekreis.gif" style="background-color:#EEEEEE;"/>
                        </span><a href="javascript:scriptScopeStatistic.print()" title="<fmt:message key="dialog.statistics.print" />"><img src="img/ic_fl_print.gif" width="11" height="11" alt="drucken" /><fmt:message key="dialog.statistics.print" /></a><a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=statistic-1#statistic-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
                    </span>
                    <!-- MAIN TAB CONTAINER START -->
                    <div id="statsTable" dojoType="dijit.layout.TabContainer" doLayout="false" style="width:100%;" selectedChild="statsObjectsAdr">
                        <!-- MAIN TAB 1 START -->
                        <div id="statsObjectsAdr" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.statistics.objAdr" />" class="tab" style="overflow:hidden;">
                            <div class="tableContainer" style="padding-bottom:20px;">
                                <div id="statsObjectsTable" autoHeight="8" contextMenu="none" defaultHideScrollbar="true">
                                </div>
                            </div>
                            <!-- end tableContainer -->
                            <div class="tableContainer">
                                <div id="statsAdrTable" autoHeight="5" contextMenu="none" defaultHideScrollbar="true">
                                </div>
                            </div>
                            <!-- end tableContainer -->
                            <p class="note">
                                <fmt:message key="dialog.statistics.footer" />
                            </p>
                        </div><!-- MAIN TAB 1 END -->
                        <!-- MAIN TAB 2 START -->
                        <div id="statsFreeTerms" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.statistics.customTerms" />" class="tab">
                            <div class="listInfoStatistics">
                                <span id="statsFreeTermsObjTableInfo" class="searchResultsInfo">
                                    &nbsp;
                                </span>
                                <span id="statsFreeTermsObjTablePaging" class="searchResultsPaging">
                                    &nbsp;
                                </span>
                                <div class="fill">
                                </div>
                            </div>
                            <div class="tableContainer" style="padding-bottom:20px;">
                                <div id="statsFreeTermsObjTable" autoHeight="11" contextMenu="none" defaultHideScrollbar="true">
                                </div>
                            </div>
                            <!-- tableContainer -->
                            <div class="listInfoStatistics">
                                <span id="statsFreeTermsAdrTableInfo" class="searchResultsInfo">
                                    &nbsp;
                                </span>
                                <span id="statsFreeTermsAdrTablePaging" class="searchResultsPaging">
                                    &nbsp;
                                </span>
                                <div class="fill">
                                </div>
                            </div>
                            <div class="tableContainer">
                                <div id="statsFreeTermsAdrTable" autoHeight="11" contextMenu="none" defaultHideScrollbar="true">
                                </div>
                            </div>
                            <!-- tableContainer -->
                        </div>
                        <!-- MAIN TAB 2 END -->
                        <!-- MAIN TAB 3 START -->
                        <div id="statsThesaurusTerms" dojoType="dijit.layout.ContentPane" title="<fmt:message key="dialog.statistics.thesaurusTerms" />" class="tab">
                            <div class="listInfoStatistics">
                                <span id="statsThesTermsObjTableInfo" class="searchResultsInfo">
                                    &nbsp;
                                </span>
                                <span id="statsThesTermsObjTablePaging" class="searchResultsPaging">
                                    &nbsp;
                                </span>
                                <div class="fill">
                                </div>
                            </div>
                            <div class="tableContainer" style="padding-bottom:20px;">
                                <div id="statsThesTermsObjTable" autoHeight="11" contextMenu="none" defaultHideScrollbar="true">
                                </div>
                            </div>
                            <div class="listInfoStatistics">
                                <span id="statsThesTermsAdrTableInfo" class="searchResultsInfo">
                                    &nbsp;
                                </span>
                                <span id="statsThesTermsAdrTablePaging" class="searchResultsPaging">
                                    &nbsp;
                                </span>
                                <div class="fill">
                                </div>
                            </div>
                            <div class="tableContainer">
                                <div id="statsThesTermsAdrTable" autoHeight="11" contextMenu="none" defaultHideScrollbar="true">
                                </div>
                            </div>
                            <!-- tableContainer -->
                        </div>
                        <!-- MAIN TAB 3 END -->
                    </div>
                    <!-- MAIN TAB CONTAINER END -->
                </div>
            </div>
        </div>
        <!-- RIGHT CONTENT PANE END -->
    </div>
  <!-- SPLIT CONTAINER END -->
</body>
</html>
