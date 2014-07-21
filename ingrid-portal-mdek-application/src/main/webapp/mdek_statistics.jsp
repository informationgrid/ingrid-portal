<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">

    var pageStatistic = _container_;
    require([
        "dojo/on",
        "dojo/aspect",
        "dojo/dom",
        "dojo/_base/array",
        "dijit/registry",
        "dojo/DeferredList",
        "dojo/Deferred",
        "ingrid/utils/PageNavigation",
        "ingrid/utils/LoadingZone",
        "ingrid/layoutCreator",
        "ingrid/grid/CustomGridFormatters",
        "ingrid/tree/MetadataTree",
        "ingrid/utils/Grid",
        /*needed for json file!*/ "ingrid/utils/Syslist",
        "ingrid/utils/Store",
        "ingrid/utils/General"
    ], function(on, aspect, dom, array, registry, DeferredList, Deferred, navigation, LoadingZone,
            layoutCreator, GridFormatters, MetadataTree, UtilGrid, UtilSyslist, UtilStore, UtilGeneral) {

            var resultsPerPage = 10;
            var statsFreeTermsObjPageNav = new navigation.PageNavigation({
                resultsPerPage: resultsPerPage,
                infoSpan: dom.byId("statsFreeTermsObjTableInfo"),
                pagingSpan: dom.byId("statsFreeTermsObjTablePaging")
            });
            var statsFreeTermsAdrPageNav = new navigation.PageNavigation({
                resultsPerPage: resultsPerPage,
                infoSpan: dom.byId("statsFreeTermsAdrTableInfo"),
                pagingSpan: dom.byId("statsFreeTermsAdrTablePaging")
            });
            var statsThesTermsObjPageNav = new navigation.PageNavigation({
                resultsPerPage: resultsPerPage,
                infoSpan: dom.byId("statsThesTermsObjTableInfo"),
                pagingSpan: dom.byId("statsThesTermsObjTablePaging")
            });
            var statsThesTermsAdrPageNav = new navigation.PageNavigation({
                resultsPerPage: resultsPerPage,
                infoSpan: dom.byId("statsThesTermsAdrTableInfo"),
                pagingSpan: dom.byId("statsThesTermsAdrTablePaging")
            });

            on(_container_, "Load", function() {

                var def = createDomElements();

                def.then(function() {
                    aspect.after(statsFreeTermsObjPageNav, "onPageSelected", function() {
                        LoadingZone.show();
                        navigateFreeTermsObjTable()
                            .then(LoadingZone.hide);
                    });
                    aspect.after(statsFreeTermsAdrPageNav, "onPageSelected", function() {
                        LoadingZone.show();
                        navigateFreeTermsAdrTable()
                            .then(LoadingZone.hide);
                    });
                    aspect.after(statsThesTermsObjPageNav, "onPageSelected", function() {
                        LoadingZone.show();
                        navigateThesTermsObjTable()
                            .then(LoadingZone.hide);
                    });
                    aspect.after(statsThesTermsAdrPageNav, "onPageSelected", function() {
                        LoadingZone.show();
                        navigateThesTermsAdrTable()
                            .then(LoadingZone.hide);
                    });

                    resetStatsObjectsTable();
                    resetStatsAdrTable();
                    resetThesaurusTable("statsThesTermsObjTable");
                    resetThesaurusTable("statsFreeTermsObjTable");
                    resetThesaurusTable("statsThesTermsAdrTable");
                    resetThesaurusTable("statsFreeTermsAdrTable");

                    registry.byId("contentPane").resize();

                    registry.byId("statsTable").watch("selectedChildWidget", function() {
                        registry.byId("statsFreeTermsObjTable").reinitLastColumn();
                        registry.byId("statsFreeTermsAdrTable").reinitLastColumn();
                        registry.byId("statsThesTermsObjTable").reinitLastColumn();
                        registry.byId("statsThesTermsAdrTable").reinitLastColumn();
                    });
                });
            });

            function createDomElements() {
                new MetadataTree({
                    showRoot: false,
                    onClick: onStatisticsNodeSelected
                }, "statisticsTree");


                var statsObjectsTableStructure = [{
                    field: 'type',
                    name: '&nbsp;',
                    width: '40px'
                }, {
                    field: 'objectClass',
                    name: "<fmt:message key='dialog.statistics.objClass' />",
                    width: '364px',
                    formatter: GridFormatters.EvalString
                }, {
                    field: 'amount',
                    name: "<fmt:message key='dialog.statistics.num' />",
                    width: '80px'
                }, {
                    field: 'percentage',
                    name: "<fmt:message key='dialog.statistics.percentage' />",
                    width: '100px'
                }, {
                    field: 'published',
                    name: "<fmt:message key='dialog.statistics.header.published' />",
                    toolTip: "<fmt:message key='dialog.statistics.tooltip.published' />",
                    width: '50px'
                }, {
                    field: 'modified',
                    name: "<fmt:message key='dialog.statistics.header.modified' />",
                    toolTip: "<fmt:message key='dialog.statistics.tooltip.modified' />",
                    width: '50px'
                }, {
                    field: 'qa',
                    name: "<fmt:message key='dialog.statistics.header.qa' />",
                    toolTip: "<fmt:message key='dialog.statistics.tooltip.qa' />",
                    width: '50px'
                }, {
                    field: 'returned',
                    name: "<fmt:message key='dialog.statistics.header.returned' />",
                    toolTip: "<fmt:message key='dialog.statistics.tooltip.returned' />",
                    width: 'auto'
                }];
                var def1 = layoutCreator.createDataGrid("statsObjectsTable", null, statsObjectsTableStructure, null, undefined, "js/data/statisticTableObject.json");

                var statsAdrTableStructure = [{
                    field: 'type',
                    name: '&nbsp;',
                    width: '40px'
                }, {
                    field: 'addressType',
                    name: "<fmt:message key='dialog.statistics.adrClass' />",
                    width: '364px',
                    formatter: GridFormatters.LocalizeString
                }, {
                    field: 'amount',
                    name: "<fmt:message key='dialog.statistics.num' />",
                    width: '80px'
                }, {
                    field: 'percentage',
                    name: "<fmt:message key='dialog.statistics.percentage' />",
                    width: '100px'
                }, {
                    field: 'published',
                    name: "<fmt:message key='dialog.statistics.header.published' />",
                    toolTip: "<fmt:message key='dialog.statistics.tooltip.published' />",
                    width: '50px'
                }, {
                    field: 'modified',
                    name: "<fmt:message key='dialog.statistics.header.modified' />",
                    toolTip: "<fmt:message key='dialog.statistics.tooltip.modified' />",
                    width: '50px'
                }, {
                    field: 'qa',
                    name: "<fmt:message key='dialog.statistics.header.qa' />",
                    toolTip: "<fmt:message key='dialog.statistics.tooltip.qa' />",
                    width: '50px'
                }, {
                    field: 'returned',
                    name: "<fmt:message key='dialog.statistics.header.returned' />",
                    toolTip: "<fmt:message key='dialog.statistics.tooltip.returned' />",
                    width: 'auto'
                }];
                var def2 = layoutCreator.createDataGrid("statsAdrTable", null, statsAdrTableStructure, null, undefined, "js/data/statisticTableAddress.json");

                var statsFreeTermsObjTableStructure = [{
                    field: 'term',
                    name: "<fmt:message key='dialog.statistics.objTerms' />",
                    width: '524px'
                }, {
                    field: 'numOccurences',
                    name: "<fmt:message key='dialog.statistics.num' />",
                    width: '80px'
                }, {
                    field: 'percentage',
                    name: "<fmt:message key='dialog.statistics.percentage' />",
                    width: 'auto'
                }];
                layoutCreator.createDataGrid("statsFreeTermsObjTable", null, statsFreeTermsObjTableStructure, null);

                var statsFreeTermsAdrTableStructure = [{
                    field: 'term',
                    name: "<fmt:message key='dialog.statistics.adrTerms' />",
                    width: '524px'
                }, {
                    field: 'numOccurences',
                    name: "<fmt:message key='dialog.statistics.num' />",
                    width: '80px'
                }, {
                    field: 'percentage',
                    name: "<fmt:message key='dialog.statistics.percentage' />",
                    width: 'auto'
                }];
                layoutCreator.createDataGrid("statsFreeTermsAdrTable", null, statsFreeTermsAdrTableStructure, null);

                var statsThesTermsObjTableStructure = [{
                    field: 'term',
                    name: "<fmt:message key='dialog.statistics.objTerms' />",
                    width: '524px'
                }, {
                    field: 'numOccurences',
                    name: "<fmt:message key='dialog.statistics.num' />",
                    width: '80px'
                }, {
                    field: 'percentage',
                    name: "<fmt:message key='dialog.statistics.percentage' />",
                    width: 'auto'
                }];
                layoutCreator.createDataGrid("statsThesTermsObjTable", null, statsThesTermsObjTableStructure, null);

                var statsThesTermsAdrTableStructure = [{
                    field: 'term',
                    name: "<fmt:message key='dialog.statistics.adrTerms' />",
                    width: '524px'
                }, {
                    field: 'numOccurences',
                    name: "<fmt:message key='dialog.statistics.num' />",
                    width: '80px'
                }, {
                    field: 'percentage',
                    name: "<fmt:message key='dialog.statistics.percentage' />",
                    width: 'auto'
                }];
                layoutCreator.createDataGrid("statsThesTermsAdrTable", null, statsThesTermsAdrTableStructure, null);

                return new DeferredList([def1, def2]);
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
                var numObjs = {
                    v: 0,
                    b: 0,
                    q: 0,
                    r: 0
                };

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
                        percentages.push(amounts[i] * 100 / numTotal);
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
                    UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, "amount", amounts[i - 1]);
                    UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, "percentage", Math.round(percentages[i - 1]));
                    UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, "published", stats[i - 1].classMap.V);
                    UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, "modified", stats[i - 1].classMap.B);
                    UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, "qa", stats[i - 1].classMap.Q);
                    UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, "returned", stats[i - 1].classMap.R);
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
                var numAdrs = {
                    v: 0,
                    b: 0,
                    q: 0,
                    r: 0
                };

                for (var i = 0; i < 4; ++i) {
                    var classMap = stats[i + ""].classMap;

                    amounts[i] = classMap.V + classMap.B + classMap.Q + classMap.R;
                    numAdrs.v += classMap.V;
                    numAdrs.b += classMap.B;
                    numAdrs.q += classMap.Q;
                    numAdrs.r += classMap.R;
                    numTotal += stats[i + ""].numTotal;
                }

                if (numTotal > 0) {
                    for (var i = 0; i < amounts.length; ++i) {
                        percentages[i] = amounts[i] * 100 / numTotal;
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
                    UtilGrid.updateTableDataRowAttr("statsAdrTable", i, "amount", amounts[i - 1]);
                    UtilGrid.updateTableDataRowAttr("statsAdrTable", i, "percentage", Math.round(percentages[i - 1]));
                    UtilGrid.updateTableDataRowAttr("statsAdrTable", i, "published", stats[(i - 1) + ""].classMap.V);
                    UtilGrid.updateTableDataRowAttr("statsAdrTable", i, "modified", stats[(i - 1) + ""].classMap.B);
                    UtilGrid.updateTableDataRowAttr("statsAdrTable", i, "qa", stats[(i - 1) + ""].classMap.Q);
                    UtilGrid.updateTableDataRowAttr("statsAdrTable", i, "returned", stats[(i - 1) + ""].classMap.R);
                }
                UtilGrid.getTable("statsAdrTable").invalidate();
            }

            function setStatsThesaurusTermsTable(tableId, thesaurusResult) {
                var numTotal = thesaurusResult.numTermsTotal;
                var termList = thesaurusResult.searchTermList;

                for (var i in termList) {
                    termList[i].percentage = Math.round(termList[i].numOccurences * 100 / numTotal);
                }

                // Update table data
                termList.unshift({
                    term: "<fmt:message key='dialog.statistics.numTotal' />",
                    numOccurences: numTotal,
                    percentage: 100
                });
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
                if (objUuid == "objectRoot") {
                    objUuid = null;
                }
                var startHit = statsThesTermsObjPageNav.getStartHit();

                var def = getObjectThesaurusStatistics(objUuid, true, startHit, resultsPerPage);

                def.then(function(thesResult) {
                    setStatsThesaurusTermsTable("statsThesTermsObjTable", thesResult);
                });

                return def;
            }

            function navigateFreeTermsObjTable(objUuid) {
                if (objUuid == "objectRoot") {
                    objUuid = null;
                }
                var startHit = statsFreeTermsObjPageNav.getStartHit();

                var def = getObjectThesaurusStatistics(objUuid, false, startHit, resultsPerPage);

                def.then(function(thesResult) {
                    setStatsThesaurusTermsTable("statsFreeTermsObjTable", thesResult);
                });

                return def;
            }

            function navigateThesTermsAdrTable(adrUuid) {

                var freeAddressesOnly = (adrUuid == "addressFreeRoot");
                if (freeAddressesOnly || adrUuid == "addressRoot") {
                    adrUuid = null;
                }
                var startHit = statsThesTermsAdrPageNav.getStartHit();

                var def = getAddressThesaurusStatistics(adrUuid, freeAddressesOnly, true, startHit, resultsPerPage);

                def.then(function(thesResult) {
                    setStatsThesaurusTermsTable("statsThesTermsAdrTable", thesResult);
                });

                return def;
            }

            function navigateFreeTermsAdrTable(adrUuid) {

                var freeAddressesOnly = (adrUuid == "addressFreeRoot");
                if (freeAddressesOnly || adrUuid == "addressRoot") {
                    adrUuid = null;
                }
                var startHit = statsFreeTermsAdrPageNav.getStartHit();

                var def = getAddressThesaurusStatistics(adrUuid, freeAddressesOnly, false, startHit, resultsPerPage);

                def.then(function(thesResult) {
                    setStatsThesaurusTermsTable("statsFreeTermsAdrTable", thesResult);
                });

                return def;
            }

            // The user clicked the catalog root node in the tree. Show statistics for the complete catalog.
            function showStatisticsForCatalog() {
                LoadingZone.show();

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

                var l1 = new DeferredList([d1, d2, d3, d4, d5, d6], false, false, true);

                l1.then(function(resultList) {
                    setStatsObjectsTableContent(resultList[0][1]);
                    setStatsAdrTableContent(resultList[1][1]);
                    LoadingZone.hide();
                }, function(err) {
                    console.debug("Error: " + err);
                    LoadingZone.hide();
                });
            }

            // The user clicked the object root node in the tree. Show statistics for all objects.
            function showStatisticsForAllObjects() {
                LoadingZone.show();

                resetStatsAdrTable();
                resetThesaurusTablePageNav("statsThesTermsObjTable");
                resetThesaurusTablePageNav("statsFreeTermsObjTable");
                resetThesaurusTable("statsThesTermsAdrTable");
                resetThesaurusTable("statsFreeTermsAdrTable");

                var d1 = getObjectStatistics(null);
                var d2 = navigateThesTermsObjTable();
                var d3 = navigateFreeTermsObjTable();

                var l1 = new DeferredList([d1, d2, d3], false, false, true);
                console.debug("show all");
                l1.then(function(resultList) {
                    setStatsObjectsTableContent(resultList[0][1]);
                    LoadingZone.hide();
                }, function(err) {
                    console.debug("Error: " + err);
                    LoadingZone.hide();
                });
            }

            // The user clicked the address root node in the tree. Show statistics for all addresses.
            function showStatisticsForAllAddresses() {
                LoadingZone.show();

                resetStatsObjectsTable();
                resetThesaurusTable("statsThesTermsObjTable");
                resetThesaurusTable("statsFreeTermsObjTable");
                resetThesaurusTablePageNav("statsThesTermsAdrTable");
                resetThesaurusTablePageNav("statsFreeTermsAdrTable");

                var d1 = getAddressStatistics(null, false);
                var d2 = navigateThesTermsAdrTable();
                var d3 = navigateFreeTermsAdrTable();

                var l1 = new DeferredList([d1, d2, d3], false, false, true);

                l1.then(function(resultList) {
                    setStatsAdrTableContent(resultList[0][1]);
                    LoadingZone.hide();
                }, function(err) {
                    console.debug("Error: " + err);
                    LoadingZone.hide();
                });
            }

            // The user clicked the free address root node in the tree. Show statistics for all free addresses.
            function showStatisticsForAllFreeAddresses() {
                LoadingZone.show();

                resetStatsObjectsTable();
                resetThesaurusTable("statsThesTermsObjTable");
                resetThesaurusTable("statsFreeTermsObjTable");
                resetThesaurusTablePageNav("statsThesTermsAdrTable");
                resetThesaurusTablePageNav("statsFreeTermsAdrTable");

                var d1 = getAddressStatistics(null, true);
                var d2 = navigateThesTermsAdrTable();
                var d3 = navigateFreeTermsAdrTable();

                var l1 = new DeferredList([d1, d2, d3], false, false, true);

                l1.then(function(resultList) {
                    setStatsAdrTableContent(resultList[0][1]);
                    LoadingZone.hide();
                }, function(err) {
                    console.debug("Error: " + err);
                    LoadingZone.hide();
                });
            }

            // The user clicked a specific address node in the tree. Show statistics for the address.
            function showStatisticsForAddress(adrUuid) {
                LoadingZone.show();

                resetStatsObjectsTable();
                resetThesaurusTable("statsThesTermsObjTable");
                resetThesaurusTable("statsFreeTermsObjTable");
                resetThesaurusTablePageNav("statsThesTermsAdrTable");
                resetThesaurusTablePageNav("statsFreeTermsAdrTable");

                var d1 = getAddressStatistics(adrUuid, false);
                var d2 = navigateThesTermsAdrTable(adrUuid);
                var d3 = navigateFreeTermsAdrTable(adrUuid);

                var l1 = new DeferredList([d1, d2, d3], false, false, true);

                l1.then(function(resultList) {
                    setStatsAdrTableContent(resultList[0][1]);
                    LoadingZone.hide();
                }, function(err) {
                    console.debug("Error: " + err);
                    LoadingZone.hide();
                });
            }

            // The user clicked a specific object node in the tree. Show statistics for the object.
            function showStatisticsForObject(uuid) {
                LoadingZone.show();

                resetStatsAdrTable();
                resetThesaurusTablePageNav("statsThesTermsObjTable");
                resetThesaurusTablePageNav("statsFreeTermsObjTable");
                resetThesaurusTable("statsThesTermsAdrTable");
                resetThesaurusTable("statsFreeTermsAdrTable");

                var d1 = getObjectStatistics(uuid);
                var d2 = navigateThesTermsObjTable(uuid);
                var d3 = navigateFreeTermsObjTable(uuid);

                var l1 = new DeferredList([d1, d2, d3], false, false, true);

                l1.then(function(resultList) {
                    console.debug("set table");
                    setStatsObjectsTableContent(resultList[0][1]);
                    console.debug("finished set table");
                    LoadingZone.hide();
                }, function(err) {
                    console.debug("Error: " + err);
                    LoadingZone.hide();
                });
            }


            // This function is called when the user clicks a node in the tree
            function onStatisticsNodeSelected(item, node) {
                console.debug("node: " + item.id);
                //var node = message.node;
                var nodeId = null;
                if (typeof(item.id) != "undefined") {
                    // Remove the prefix from the nodeId
                    nodeId = item.id;
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
                    console.debug("clicked unknown node with id '" + nodeId + "'");
                }
            }

            function getObjectStatistics(objUuid) {
                var def = new Deferred();

                ObjectService.getObjectStatistics(objUuid, {
                    callback: function(objStats) {
                        def.resolve(objStats.resultMap);
                    },
                    errorHandler: function(errMsg, err) {
                        console.debug("Error: " + errMsg);
                        def.reject(err);
                    }
                });

                return def;
            }

            function getObjectThesaurusStatistics(objUuid, thesaurusTerms, startHit, numHits) {
                var def = new Deferred();

                ObjectService.getObjectThesaurusStatistics(objUuid, thesaurusTerms, startHit, numHits, {
                    callback: function(objThesStats) {
                        def.resolve(objThesStats);
                    },
                    errorHandler: function(errMsg, err) {
                        console.debug("Error: " + errMsg);
                        def.reject(err);
                    }
                });

                return def;
            }

            function getAddressStatistics(adrUuid, freeAddressesOnly) {
                var def = new Deferred();

                AddressService.getAddressStatistics(adrUuid, freeAddressesOnly, {
                    callback: function(adrStats) {
                        def.resolve(adrStats.resultMap);
                    },
                    errorHandler: function(errMsg, err) {
                        console.debug("Error: " + errMsg);
                        def.reject(err);
                    }
                });

                return def;
            }

            function getAddressThesaurusStatistics(adrUuid, freeAddressesOnly, thesaurusTerms, startHit, numHits) {
                var def = new Deferred();

                AddressService.getAddressThesaurusStatistics(adrUuid, freeAddressesOnly, thesaurusTerms, startHit, numHits, {
                    callback: function(adrThesStats) {
                        def.resolve(adrThesStats);
                    },
                    errorHandler: function(errMsg, err) {
                        console.debug("Error: " + errMsg);
                        def.reject(err);
                    }
                });

                return def;
            }


            function resetStatsObjectsTable() {
                // Reset table data
                var fieldIdList = ["amount", "percentage", "published", "modified", "qa", "returned"];
                var data = UtilGrid.getTableData("statsObjectsTable");
                for (var i in data) {
                    array.forEach(fieldIdList, function(item) {
                        UtilGrid.updateTableDataRowAttr("statsObjectsTable", i, item, 0);
                    });
                }
            }

            function resetStatsAdrTable() {
                // Reset table data
                var fieldIdList = ["amount", "percentage", "published", "modified", "qa", "returned"];
                var data = UtilGrid.getTableData("statsAdrTable");
                for (var i in data) {
                    array.forEach(fieldIdList, function(item) {
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
                //  pageNav.updateDomNodes();
                //  registry.byId(tableId).store.clearData();
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
                //registry.byId(tableId).store.clearData();
            }


            function reloadStatsTree() {
                var treeController = registry.byId("statisticsTreeController");
                var rootNode = registry.byId("statsTreeRoot");
                treeController.refreshChildren(rootNode);

                // reset tables as well since no node is selected
                resetStatsObjectsTable();
                resetStatsAdrTable();
                resetThesaurusTable("statsThesTermsObjTable");
                resetThesaurusTable("statsFreeTermsObjTable");
                resetThesaurusTable("statsThesTermsAdrTable");
                resetThesaurusTable("statsFreeTermsAdrTable");
            }

            pageStatistic.print = function() {
                var selectedTab = registry.byId("statsTable").selectedChildWidget.id;
                var div = document.createElement("div");
                div.setAttribute("id", "printPreparation");
                if (selectedTab == "statsObjectsAdr") {
                    this.renderTable(div, UtilGrid.getTableData("statsObjectsTable"), dojo.map(UtilGrid.getTable("statsObjectsTable").getColumns(), function(col) {
                        return col.field;
                    }), dojo.map(UtilGrid.getTable("statsObjectsTable").getColumns(), function(col) {
                        return col.name;
                    }), "", [null, LocalizeString, null, null, null, null, null, null]);
                    this.renderTable(div, UtilGrid.getTableData("statsAdrTable"), dojo.map(UtilGrid.getTable("statsAdrTable").getColumns(), function(col) {
                        return col.field;
                    }), dojo.map(UtilGrid.getTable("statsAdrTable").getColumns(), function(col) {
                        return col.name;
                    }), "", [null, LocalizeString, null, null, null, null, null, null]);
                } else if (selectedTab == "statsFreeTerms") {
                    this.renderTable(div, UtilGrid.getTableData("statsFreeTermsObjTable"), dojo.map(UtilGrid.getTable("statsFreeTermsObjTable").getColumns(), function(col) {
                        return col.field;
                    }), dojo.map(UtilGrid.getTable("statsFreeTermsObjTable").getColumns(), function(col) {
                        return col.name;
                    }), "", [null, LocalizeString, null, null, null, null, null, null]);
                    this.renderTable(div, UtilGrid.getTableData("statsFreeTermsAdrTable"), dojo.map(UtilGrid.getTable("statsFreeTermsAdrTable").getColumns(), function(col) {
                        return col.field;
                    }), dojo.map(UtilGrid.getTable("statsFreeTermsAdrTable").getColumns(), function(col) {
                        return col.name;
                    }), "", [null, LocalizeString, null, null, null, null, null, null]);
                } else if (selectedTab == "statsThesaurusTerms") {
                    this.renderTable(div, UtilGrid.getTableData("statsThesTermsObjTable"), dojo.map(UtilGrid.getTable("statsThesTermsObjTable").getColumns(), function(col) {
                        return col.field;
                    }), dojo.map(UtilGrid.getTable("statsThesTermsObjTable").getColumns(), function(col) {
                        return col.name;
                    }), "", [null, LocalizeString, null, null, null, null, null, null]);
                    this.renderTable(div, UtilGrid.getTableData("statsThesTermsAdrTable"), dojo.map(UtilGrid.getTable("statsThesTermsAdrTable").getColumns(), function(col) {
                        return col.field;
                    }), dojo.map(UtilGrid.getTable("statsThesTermsAdrTable").getColumns(), function(col) {
                        return col.name;
                    }), "", [null, LocalizeString, null, null, null, null, null, null]);
                }
                UtilGeneral.printDivContent(null, null, div);
            };

            function renderTable(div, list, rowProperties, listHeader, title, cellRenderFunction) {
                if (list && list.length > 0) {
                    var t = "";
                    t += "<strong>" + title + "</strong><br/><br/>";
                    t += "<p><table class=\"filteringTable\" cellspacing=\"0\">";
                    if (listHeader && listHeader.length > 0) {
                        t += "<thead class=\"fixedHeader\"><tr>";
                        for (i = 0; i < listHeader.length; i++) {
                            t += "<th style=\"padding-right:4px\">" + listHeader[i] + "</th>";
                        }
                        t += "</tr></thead>";
                    }
                    t += "<tbody>";
                    for (var i = 0; i < list.length; i++) {
                        if (i % 2) {
                            t += "<tr class=\"alt\">";
                        } else {
                            t += "<tr>";
                        }
                        for (var j = 0; j < rowProperties.length; j++) {
                            if (cellRenderFunction && cellRenderFunction[j]) {
                                t += "<td style=\"padding-right:4px\">" + cellRenderFunction[j].call(this, -1, -1, list[i][rowProperties[j]]) + "</td>";
                            } else {
                                t += "<td style=\"padding-right:4px\">" + list[i][rowProperties[j]] + "</td>";
                            }
                        }
                        t += "</tr>";

                    }
                    t += "</tbody></table></p>";
                    div.innerHTML += t + "<br/><br/>";
                }
            }

        });

</script>
</head>

<body>
    <!-- SPLIT CONTAINER START -->
    <div data-dojo-type="dijit.layout.BorderContainer" id="statisticsContentSection" design="headline" sizerWidth="15" layoutAlign="client" style="height: 100%; border: 0;">
        <!-- LEFT CONTENT PANE START -->
        <div data-dojo-type="dijit/layout/ContentPane" class="contentContainer" id="statisticsTreeContainer" region="leading" splitter="true" style="width:300px;">
            <div id="statisticsTree">
            </div>
        </div>
        <!-- LEFT CONTENT PANE END --><!-- RIGHT CONTENT PANE START -->
        <div data-dojo-type="dijit/layout/ContentPane" id="statisticsContentContainer" class="contentContainer" region="center">
            <div id="statisticsContentSection" class="contentBlockWhite">
                <div id="stats" class="content">
                    <span class="functionalLink onTab" style="margin-bottom: 5px;">
                        <span id="statisticsLoadingZone" style="visibility:hidden;">
                            <img src="img/ladekreis.gif" style="background-color:#EEEEEE;"/>
                        </span><a href="pageStatistic.print()" title="<fmt:message key="dialog.statistics.print" />"><img src="img/ic_fl_print.gif" width="11" height="11" alt="drucken" /><fmt:message key="dialog.statistics.print" /></a><a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=statistic-1#statistic-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
                    </span>
                    <!-- MAIN TAB CONTAINER START -->
                    <div id="statsTable" data-dojo-type="dijit/layout/TabContainer" doLayout="false" style="width:100%;" selectedChild="statsObjectsAdr">
                        <!-- MAIN TAB 1 START -->
                        <div id="statsObjectsAdr" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.statistics.objAdr" />" class="tab" style="overflow:hidden;">
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
                        <div id="statsFreeTerms" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.statistics.customTerms" />" class="tab">
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
                        <div id="statsThesaurusTerms" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.statistics.thesaurusTerms" />" class="tab">
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
