<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

var resultsPerPage = 10;
var statsFreeTermsObjPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("statsFreeTermsObjTableInfo"), pagingSpan:dojo.byId("statsFreeTermsObjTablePaging") });
var statsFreeTermsAdrPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("statsFreeTermsAdrTableInfo"), pagingSpan:dojo.byId("statsFreeTermsAdrTablePaging") });
var statsThesTermsObjPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("statsThesTermsObjTableInfo"), pagingSpan:dojo.byId("statsThesTermsObjTablePaging") });
var statsThesTermsAdrPageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dojo.byId("statsThesTermsAdrTableInfo"), pagingSpan:dojo.byId("statsThesTermsAdrTablePaging") });

_container_.addOnLoad(function() {
	dojo.event.connectOnce("after", statsFreeTermsObjPageNav, "onPageSelected", function() {
		showLoadingZone();
		var def = navigateFreeTermsObjTable();
		def.addBoth(hideLoadingZone);
	});
	dojo.event.connectOnce("after", statsFreeTermsAdrPageNav, "onPageSelected", function() {
		showLoadingZone();
		var def = navigateFreeTermsAdrTable();
		def.addBoth(hideLoadingZone);
	});
	dojo.event.connectOnce("after", statsThesTermsObjPageNav, "onPageSelected", function() {
		showLoadingZone();
		var def = navigateThesTermsObjTable();
		def.addBoth(hideLoadingZone);	
	});
	dojo.event.connectOnce("after", statsThesTermsAdrPageNav, "onPageSelected", function() {
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
});


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

	for (var i = 0; i < 6; ++i) {
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
	var store = dojo.widget.byId("statsObjectsTable").store;
	var storeData = store.getData();

	store.update(storeData[0], "amount", numTotal);
	store.update(storeData[0], "percentage", 100);
	store.update(storeData[0], "published", numObjs.v);
	store.update(storeData[0], "modified", numObjs.b);
	store.update(storeData[0], "qa", numObjs.q);
	store.update(storeData[0], "returned", numObjs.r);

	for (var i = 1; i < storeData.length; ++i) {
		store.update(storeData[i], "amount", amounts[i-1]);
		store.update(storeData[i], "percentage", Math.round(percentages[i-1]));
		store.update(storeData[i], "published", stats[i-1].classMap.V);
		store.update(storeData[i], "modified", stats[i-1].classMap.B);
		store.update(storeData[i], "qa", stats[i-1].classMap.Q);
		store.update(storeData[i], "returned", stats[i-1].classMap.R);
	}
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
	var store = dojo.widget.byId("statsAdrTable").store;
	var storeData = store.getData();

	store.update(storeData[0], "amount", numTotal);
	store.update(storeData[0], "percentage", 100);
	store.update(storeData[0], "published", numAdrs.v);
	store.update(storeData[0], "modified", numAdrs.b);
	store.update(storeData[0], "qa", numAdrs.q);
	store.update(storeData[0], "returned", numAdrs.r);

	for (var i = 1; i < storeData.length; ++i) {
		store.update(storeData[i], "amount", amounts[i-1]);
		store.update(storeData[i], "percentage", Math.round(percentages[i-1]));
		store.update(storeData[i], "published", stats[(i-1)+""].classMap.V);
		store.update(storeData[i], "modified", stats[(i-1)+""].classMap.B);
		store.update(storeData[i], "qa", stats[(i-1)+""].classMap.Q);
		store.update(storeData[i], "returned", stats[(i-1)+""].classMap.R);
	}
}

function setStatsThesaurusTermsTable(tableId, thesaurusResult) {
	var numTotal = thesaurusResult.numTermsTotal;
	var termList = thesaurusResult.searchTermList;

	for (var i in termList) {
		termList[i].percentage = Math.round(termList[i].numOccurences*100 / numTotal);
	}

	// Update table data
	var store = dojo.widget.byId(tableId).store;

	termList.unshift({term:message.get("dialog.statistics.numTotal"), numOccurences:numTotal, percentage:100});
	UtilList.addTableIndices(termList);
	store.setData(termList);


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


function navigateThesTermsObjTable() {
	var objUuid = dojo.widget.byId("statisticsTree").selectedNode.uuid;
	if (objUuid == "objectRoot") {
		objUuid = null;
	}
	var thesTermsObjStore = dojo.widget.byId("statsThesTermsObjTable").store;
	var startHit = statsThesTermsObjPageNav.getStartHit();

	var def = getObjectThesaurusStatistics(objUuid, true, startHit, resultsPerPage);

	def.addCallback(function(thesResult) {
		setStatsThesaurusTermsTable("statsThesTermsObjTable", thesResult);
	});

	return def;
}

function navigateFreeTermsObjTable() {
	var objUuid = dojo.widget.byId("statisticsTree").selectedNode.uuid;
	if (objUuid == "objectRoot") {
		objUuid = null;
	}
	var freeTermsObjStore = dojo.widget.byId("statsFreeTermsObjTable").store;
	var startHit = statsFreeTermsObjPageNav.getStartHit();

	var def = getObjectThesaurusStatistics(objUuid, false, startHit, resultsPerPage);

	def.addCallback(function(thesResult) {
		setStatsThesaurusTermsTable("statsFreeTermsObjTable", thesResult);
	});

	return def;
}

function navigateThesTermsAdrTable() {
	var adrUuid = dojo.widget.byId("statisticsTree").selectedNode.uuid;

	var freeAddressesOnly = (adrUuid == "addressFreeRoot");
	if (freeAddressesOnly || adrUuid == "addressRoot") {
		adrUuid = null;
	}
	var thesTermsAdrStore = dojo.widget.byId("statsThesTermsAdrTable").store;
	var startHit = statsThesTermsAdrPageNav.getStartHit();

	var def = getAddressThesaurusStatistics(adrUuid, freeAddressesOnly, true, startHit, resultsPerPage);

	def.addCallback(function(thesResult) {
		setStatsThesaurusTermsTable("statsThesTermsAdrTable", thesResult);
	});

	return def;
}

function navigateFreeTermsAdrTable() {
	var adrUuid = dojo.widget.byId("statisticsTree").selectedNode.uuid;

	var freeAddressesOnly = (adrUuid == "addressFreeRoot");
	if (freeAddressesOnly || adrUuid == "addressRoot") {
		adrUuid = null;
	}
	var freeTermsAdrStore = dojo.widget.byId("statsFreeTermsAdrTable").store;
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
		dojo.debug("Error: "+err);
		dojo.debugShallow(err);
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

	l1.addCallback(function(resultList) {
		setStatsObjectsTableContent(resultList[0][1]);
		hideLoadingZone();
	});

	l1.addErrback(function(err) {
		dojo.debug("Error: "+err);
		dojo.debugShallow(err);
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
		dojo.debug("Error: "+err);
		dojo.debugShallow(err);
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
		dojo.debug("Error: "+err);
		dojo.debugShallow(err);
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
	var d2 = navigateThesTermsAdrTable();
	var d3 = navigateFreeTermsAdrTable();

	var l1 = new dojo.DeferredList([d1, d2, d3], false, false, true);

	l1.addCallback(function(resultList) {
		setStatsAdrTableContent(resultList[0][1]);
		hideLoadingZone();
	});

	l1.addErrback(function(err) {
		dojo.debug("Error: "+err);
		dojo.debugShallow(err);
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
	var d2 = navigateThesTermsObjTable();
	var d3 = navigateFreeTermsObjTable();

	var l1 = new dojo.DeferredList([d1, d2, d3], false, false, true);

	l1.addCallback(function(resultList) {
		setStatsObjectsTableContent(resultList[0][1]);
		hideLoadingZone();
	});

	l1.addErrback(function(err) {
		dojo.debug("Error: "+err);
		dojo.debugShallow(err);
		hideLoadingZone();
	});
}


// This function is called when the user clicks a node in the tree
function onStatisticsNodeSelected(message) {
	var node = message.node;
	var nodeId = null;
	if (typeof(node.id) != "undefined") {
		// Remove the prefix from the nodeId
		nodeId = node.uuid;
	}

	if (nodeId == null) {
		showStatisticsForCatalog();

	} else if (nodeId == "objectRoot") {
		showStatisticsForAllObjects();

	} else if (nodeId == "addressRoot") {
		showStatisticsForAllAddresses();

	} else if (nodeId == "addressFreeRoot") {
		showStatisticsForAllFreeAddresses();

	} else if (node.nodeAppType == "A") {
		showStatisticsForAddress(nodeId);

	} else if (node.nodeAppType == "O") {
		showStatisticsForObject(nodeId);

	} else {
		// TODO Show error message?
		dojo.debug("clicked unknown node with id '"+nodeId+"'");
	}
}


function initStatsTree() {
	// Listen for nodeSelections
	var treeListener = dojo.widget.byId('statisticsTreeListener');
	dojo.event.topic.subscribe(treeListener.eventNames.select, onStatisticsNodeSelected);

	// Overload the loadRemote function to load objs/adrs from the server via dwr
	var treeController = dojo.widget.byId('statisticsTreeController');
	treeController.loadRemote = function(node, sync){
//		dojo.debug("loadRemote called on node with uuid: "+node.uuid);
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();

		deferred.addCallback(function(res) {
			dojo.lang.forEach(res, function(obj){
				obj.title = dojo.string.escape("html", obj.title);
				obj.uuid = obj.id;
				obj.id = null;
			});
			return _this.loadProcessResponse(node,res);
		});
		deferred.addErrback(function(res) { dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING); dojo.debug(res); return res;});

		TreeService.getSubTree(node.uuid, node.nodeAppType, {
			preHook: showLoadingZone,
			postHook: hideLoadingZone,
  			callback:function(res) { deferred.callback(res); },
			errorHandler:function(message) {
				hideLoadingZone();
				deferred.errback(new dojo.RpcError(message, this));
			}
  		});

		return deferred;
	};

	// init the context menu
	var contextMenu = dojo.widget.byId('statsTreeContextMenu');
	contextMenu.treeController = dojo.widget.byId('statisticsTreeController');
	contextMenu.addItem(message.get('tree.reload'), 'reload', reloadStatsTree);
}

function getObjectStatistics(objUuid) {
	var def = new dojo.Deferred();

	ObjectService.getObjectStatistics(objUuid, {
		callback: function(objStats) {
			def.callback(objStats.resultMap);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug("Error: "+errMsg);
			dojo.debugShallow(err);
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
			dojo.debug("Error: "+errMsg);
			dojo.debugShallow(err);
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
			dojo.debug("Error: "+errMsg);
			dojo.debugShallow(err);
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
			dojo.debug("Error: "+errMsg);
			dojo.debugShallow(err);
			def.errback(err);
		}
	});

	return def;
}


function resetStatsObjectsTable() {
	// Reset table data
	var fieldIdList = ["amount", "percentage", "published", "modified", "qa", "returned"];
	var store = dojo.widget.byId("statsObjectsTable").store;
	var data = store.getData();
	for (var i in data) {
		dojo.lang.forEach(fieldIdList, function(item) {
			store.update(data[i], item, 0);
		});
	}
}

function resetStatsAdrTable() {
	// Reset table data
	var fieldIdList = ["amount", "percentage", "published", "modified", "qa", "returned"];
	var store = dojo.widget.byId("statsAdrTable").store;
	var data = store.getData();
	for (var i in data) {
		dojo.lang.forEach(fieldIdList, function(item) {
			store.update(data[i], item, 0);
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
//	dojo.widget.byId(tableId).store.clearData();
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
	dojo.widget.byId(tableId).store.clearData();
}


function reloadStatsTree() {
	var treeController = dojo.widget.byId("statisticsTreeController");
	var rootNode = dojo.widget.byId("statsTreeRoot");
	treeController.refreshChildren(rootNode);

	// reset tables as well since no node is selected
	resetStatsObjectsTable();
	resetStatsAdrTable();
	resetThesaurusTable("statsThesTermsObjTable");
	resetThesaurusTable("statsFreeTermsObjTable");
	resetThesaurusTable("statsThesTermsAdrTable");
	resetThesaurusTable("statsFreeTermsAdrTable");
}

scriptScope.print = function() {
	var selectedTab = dojo.widget.byId("statsTable").selectedChild;
	printDivContent(selectedTab);
}

function showLoadingZone() {
	dojo.html.setVisibility(dojo.byId("statisticsLoadingZone"), "visible");
}

function hideLoadingZone() {
	dojo.html.setVisibility(dojo.byId("statisticsLoadingZone"), "hidden");
}


</script>
</head>

<body>
<div dojoType="LayoutContainer" id="statisticsLayout" class="layout" layoutChildPriority="top-bottom">

	<!-- SPLIT CONTAINER START -->
	<div dojoType="ingrid:SplitContainer" id="statisticsContentSection" orientation="horizontal" sizerWidth="15" layoutAlign="client" templateCssPath="js/dojo/widget/templates/SplitContainer.css">
		<!-- LEFT CONTENT PANE START -->
		<div dojoType="ContentPane" id="statisticsTreeContainer">
			<!-- tree components -->
			<div dojoType="ingrid:TreeController" widgetId="statisticsTreeController" RpcUrl="server/treelistener.php"></div>
			<div dojoType="ingrid:TreeListener" widgetId="statisticsTreeListener"></div>	
			<div dojoType="ingrid:TreeDocIcons" widgetId="statisticsTreeDocIcons"></div>	
			<div dojoType="ingrid:TreeDecorator" listener="statisticsTreeListener"></div>
      
      <!-- context menus -->
      <div dojoType="ingrid:TreeContextMenu" toggle="plain" contextMenuForWindow="false" widgetId="statsTreeContextMenu"></div>

      <!-- tree -->
      <div dojoType="ingrid:Tree" listeners="statisticsTreeController;statisticsTreeListener;statsTreeContextMenu;statisticsTreeDocIcons" widgetId="statisticsTree">
      	<div dojoType="ingrid:TreeNode" title="Katalog" id="statsTreeRoot" contextMenu="statsTreeContextMenu" isFolder="true" nodeDocType="Catalog" nodeAppType="C"></div>
      </div>
  	</div>
    <!-- LEFT CONTENT PANE END -->

    <!-- RIGHT CONTENT PANE START -->
  	<div dojoType="ContentPane" id="statisticsContentContainer" class="contentContainer" layoutAlign="top">

      <div id="statisticsContentSection" class="contentBlockWhite">
        <div style="z-index: 99; top:0px !important;" id="winNavi">
			<span id="statisticsLoadingZone" z-index: 100;" style="visibility:hidden;">
		        <img src="img/ladekreis.gif" style="background-color:#EEEEEE;" />
		    </span>
          <a href="javascript:scriptScope.print()" title="drucken"><img src="img/ic_fl_print.gif" width="11" height="11" alt="drucken" />drucken</a>
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=statistic-1#statistic-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="Hilfe">[?]</a>
    	  </div>
    	  <div id="stats" class="content">
          
          <!-- MAIN TAB CONTAINER START -->
        	<div id="statsTable" dojoType="ingrid:TabContainer" doLayout="false" class="full" selectedChild="statsObjectsAdr">
            <!-- MAIN TAB 1 START -->
        		<div id="statsObjectsAdr" dojoType="ContentPane" label="<fmt:message key="dialog.statistics.objAdr" />" style="overflow:hidden;">
				<div class="tableContainer rows7 full">
	        	    <table id="statsObjectsTable" dojoType="ingrid:FilteringTable" minRows="7" cellspacing="0" class="filteringTable nosort relativePos">
	        	      <thead>
	        		      <tr>
	              			<th nosort="true" field="type" dataType="String" width="40"></th>
	              			<th nosort="true" field="objectClass" dataType="String" width="364"><fmt:message key="dialog.statistics.objClass" /></th>
	              			<th nosort="true" field="amount" dataType="String" width="80"><fmt:message key="dialog.statistics.num" /></th>
	              			<th nosort="true" field="percentage" dataType="String" width="80"><fmt:message key="dialog.statistics.percentage" /></th>
	              			<th nosort="true" field="published" dataType="String" width="30">V</th>
	              			<th nosort="true" field="modified" dataType="String" width="30">B</th>
	              			<th nosort="true" field="qa" dataType="String" width="30">Q</th>
	              			<th nosort="true" field="returned" dataType="String" width="30">R</th>
	        		      </tr>
	        	      </thead>
	        	      <tbody>
	        		      <tr value="0">
	        		        <td>&nbsp;</td>
	        		        <td><fmt:message key="dialog.statistics.numTotal" /></td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td></tr>
	        		      <tr value="1">
	        		        <td><img src="img/UDK/udk_class0.gif" width="16" height="16" alt="Organisationseinheit / Fachaufgabe" /></td>
	        		        <td><fmt:message key="dialog.statistics.objClass0" /></td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td></tr>
	        		      <tr value="2">
	        		        <td><img src="img/UDK/udk_class1.gif" width="16" height="16" alt="Geo-Information / Karte" /></td>
	        		        <td><fmt:message key="dialog.statistics.objClass1" /></td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td></tr>
	        		      <tr value="3">
	        		        <td><img src="img/UDK/udk_class2.gif" width="16" height="16" alt="Dokument / Bericht / Literatur" /></td>
	        		        <td><fmt:message key="dialog.statistics.objClass2" /></td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td></tr>
	        		      <tr value="4">
	        		        <td><img src="img/UDK/udk_class3.gif" width="16" height="16" alt="Dienst / Anwendung / Informationssystem" /></td>
	        		        <td><fmt:message key="dialog.statistics.objClass3" /></td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td></tr>
	        		      <tr value="5">
	        		        <td><img src="img/UDK/udk_class4.gif" width="16" height="16" alt="Vorhaben / Projekt / Programm" /></td>
	        		        <td><fmt:message key="dialog.statistics.objClass4" /></td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td></tr>
	        		      <tr value="6">
	        		        <td><img src="img/UDK/udk_class5.gif" width="16" height="16" alt="Datensammlung / Datenbank" /></td>
	        		        <td><fmt:message key="dialog.statistics.objClass5" /></td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td></tr>
	        	      </tbody>
	        	    </table>
				</div> <!-- end tableContainer -->
              <div class="spacer"></div>

				<div class="tableContainer rows5 full">
	        	    <table id="statsAdrTable" dojoType="ingrid:FilteringTable" minRows="5" cellspacing="0" class="filteringTable nosort relativePos">
	        	      <thead>
	        		      <tr>
	              			<th nosort="true" field="type" dataType="String" width="40">&nbsp;</th>
	              			<th nosort="true" field="addressType" dataType="String" width="364"><fmt:message key="dialog.statistics.adrClass" /></th>
	              			<th nosort="true" field="amount" dataType="String" width="80"><fmt:message key="dialog.statistics.num" /></th>
	              			<th nosort="true" field="percentage" dataType="String" width="80"><fmt:message key="dialog.statistics.percentage" /></th>
	              			<th nosort="true" field="published" dataType="String" width="30">V</th>
	              			<th nosort="true" field="modified" dataType="String" width="30">B</th>
	              			<th nosort="true" field="qa" dataType="String" width="30">Q</th>
	              			<th nosort="true" field="returned" dataType="String" width="30">R</th>
	        		      </tr>
	        	      </thead>
	        	      <tbody>
	        		      <tr value="0">
	        		        <td>&nbsp</td>
	        		        <td><fmt:message key="dialog.statistics.numTotal" /></td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td></tr>
	        		      <tr value="1">
	        		        <td><img src="img/UDK/addr_institution.gif" width="16" height="16" alt="Institution" /></td>
	        		        <td><fmt:message key="dialog.statistics.adrClass0" /></td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td></tr>
	        		      <tr value="2">
	        		        <td><img src="img/UDK/addr_unit.gif" width="16" height="16" alt="Einheit" /></td>
	        		        <td><fmt:message key="dialog.statistics.adrClass1" /></td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td></tr>
	        		      <tr value="3">
	        		        <td><img src="img/UDK/addr_person.gif" width="16" height="16" alt="Person" /></td>
	        		        <td><fmt:message key="dialog.statistics.adrClass2" /></td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td></tr>
	        		      <tr value="4">
	        		        <td><img src="img/UDK/addr_free.gif" width="16" height="16" alt="Freie Adresse" /></td>
	        		        <td><fmt:message key="dialog.statistics.adrClass3" /></td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td>
	        		        <td>0</td></tr>
	        	      </tbody>
	        	    </table>
				</div> <!-- end tableContainer -->

	          <div class="spacer"></div>
	          <p class="note"><fmt:message key="dialog.statistics.footer" /></p>

            </div>
            <!-- MAIN TAB 1 END -->
        		
            <!-- MAIN TAB 2 START -->
        	<div id="statsFreeTerms" dojoType="ContentPane" label="<fmt:message key="dialog.statistics.customTerms" />">

				<div class="listInfoStatistics">
					<span id="statsFreeTermsObjTableInfo" class="searchResultsInfo">&nbsp;</span>
					<span id="statsFreeTermsObjTablePaging" class="searchResultsPaging">&nbsp;</span>
					<div class="fill"></div>
				</div>

				<div class="tableContainer rows11 full">
	        	    <table id="statsFreeTermsObjTable" dojoType="ingrid:FilteringTable" minRows="11" headClass="fixedHeader" cellspacing="0" class="filteringTable nosort relativePos">
	        	      <thead>
	        		      <tr>
	              			<th nosort="true" field="term" dataType="String" width="524"><fmt:message key="dialog.statistics.objTerms" /></th>
	              			<th nosort="true" field="numOccurences" dataType="String" width="80"><fmt:message key="dialog.statistics.num" /></th>
	              			<th nosort="true" field="percentage" dataType="String" width="80"><fmt:message key="dialog.statistics.percentage" /></th>
	        		      </tr>
	        	      </thead>
	        	      <tbody>
        		      </tbody>
	        	    </table>
				</div> <!-- tableContainer -->
				<div class="spacer"></div>

				<div class="listInfoStatistics">
					<span id="statsFreeTermsAdrTableInfo" class="searchResultsInfo">&nbsp;</span>
					<span id="statsFreeTermsAdrTablePaging" class="searchResultsPaging">&nbsp;</span>
					<div class="fill"></div>
				</div>

				<div class="tableContainer rows11 full">
        	    <table id="statsFreeTermsAdrTable" dojoType="ingrid:FilteringTable" minRows="11" headClass="fixedHeader" cellspacing="0" class="filteringTable nosort relativePos">
        	      <thead>
        		      <tr>
              			<th nosort="true" field="term" dataType="String" width="524"><fmt:message key="dialog.statistics.adrTerms" /></th>
              			<th nosort="true" field="numOccurences" dataType="String" width="80"><fmt:message key="dialog.statistics.num" /></th>
              			<th nosort="true" field="percentage" dataType="String" width="80"><fmt:message key="dialog.statistics.percentage" /></th>
        		      </tr>
        	      </thead>
        	      <tbody>
        	      </tbody>
        	    </table>
        	   </div> <!-- tableContainer -->
            </div>
            <!-- MAIN TAB 2 END -->

            <!-- MAIN TAB 3 START -->
        		<div id="statsThesaurusTerms" dojoType="ContentPane" label="<fmt:message key="dialog.statistics.thesaurusTerms" />">

				<div class="listInfoStatistics">
					<span id="statsThesTermsObjTableInfo" class="searchResultsInfo">&nbsp;</span>
					<span id="statsThesTermsObjTablePaging" class="searchResultsPaging">&nbsp;</span>
					<div class="fill"></div>
				</div>

				<div class="tableContainer rows11 full">
	        	    <table id="statsThesTermsObjTable" dojoType="ingrid:FilteringTable" minRows="11" headClass="fixedHeader" cellspacing="0" class="filteringTable nosort relativePos">
	        	      <thead>
	        		      <tr>
	              			<th nosort="true" field="term" dataType="String" width="524"><fmt:message key="dialog.statistics.objTerms" /></th>
	              			<th nosort="true" field="numOccurences" dataType="String" width="80"><fmt:message key="dialog.statistics.num" /></th>
	              			<th nosort="true" field="percentage" dataType="String" width="80"><fmt:message key="dialog.statistics.percentage" /></th>
	        		      </tr>
	        	      </thead>
	        	      <tbody>
	        	      </tbody>
	        	    </table>
				</div>
              <div class="spacer"></div>

				<div class="listInfoStatistics">
					<span id="statsThesTermsAdrTableInfo" class="searchResultsInfo">&nbsp;</span>
					<span id="statsThesTermsAdrTablePaging" class="searchResultsPaging">&nbsp;</span>
					<div class="fill"></div>
				</div>

				<div class="tableContainer rows11 full">
	        	    <table id="statsThesTermsAdrTable" dojoType="ingrid:FilteringTable" minRows="11" headClass="fixedHeader" cellspacing="0" class="filteringTable nosort relativePos">
	        	      <thead>
	        		      <tr>
	              			<th nosort="true" field="term" dataType="String" width="524"><fmt:message key="dialog.statistics.adrTerms" /></th>
	              			<th nosort="true" field="numOccurences" dataType="String" width="80"><fmt:message key="dialog.statistics.num" /></th>
	              			<th nosort="true" field="percentage" dataType="String" width="80"><fmt:message key="dialog.statistics.percentage" /></th>
	        		      </tr>
	        	      </thead>
	        	      <tbody>
	        	      </tbody>
	        	    </table>
        			</div> <!-- tableContainer -->
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
</div>
</body>
</html>
