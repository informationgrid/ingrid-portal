<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <title><fmt:message key="dialog.popup.addressTable.link" /></title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <meta name="author" content="wemove digital solutions" />
        <meta name="copyright" content="wemove digital solutions GmbH" />
        <script type="text/javascript">
            dojo.require("dijit.layout.TabContainer");
            
            var adrPageNav = new PageNavigation({
                resultsPerPage: 10,
                infoSpan: dojo.byId("addressLinkSearchResultsInfo"),
                pagingSpan: dojo.byId("addressLinkSearchResultsPaging")
            });
            
            var currentQuery = {
                organisation: null,
                name: null,
                givenName: null
            };
			
            // All the possible values for special ref links
            var referenceMap = [["Standort", "3360"], ["Projektleiter", "3400"], ["Beteiligte", "3410"]];
            
            dojo.addOnUnload(function(){
                if (dijit.byId("pageDialog").customParams && dijit.byId("pageDialog").customParams.resultHandler) {
                    if (dijit.byId("pageDialog").customParams.resultHandler.fired == -1) {
                        dijit.byId("pageDialog").customParams.resultHandler.errback();
                    }
                }
            });
            
            
            createCustomTree("treeAdr", null, "id", "title", loadAddressData);

            prepareDataGrid();
            
            // Hide the result table
            dojo.byId("addressLinkSearchResults").style.display = "none";
            
            // Connect the 'enter' button on the three input fields with startNewSearch
            var startSearchOnEnter = function(event) {
                if (event.keyCode == dojo.keys.ENTER) {
                    startNewSearch();
                }
            }
            
            dojo.connect(dijit.byId("pageDialog"), "onLoad", function() {
                dojo.connect(adrPageNav, "onPageSelected", browseSearchResults);
                
                dojo.connect(dojo.byId("addressSearchUnit"), "onkeypress", startSearchOnEnter);
                dojo.connect(dojo.byId("addressSearchLastname"), "onkeypress", startSearchOnEnter);
                dojo.connect(dojo.byId("addressSearchFirstname"), "onkeypress", startSearchOnEnter);
                
                dijit.byId("addressSearchUnit").focus();
            });
            
            function loadAddressData(node, callback_function){
                var parentItem = node.item;
                var prefix = "addrDlg_";
                var store = dijit.byId("treeAdr").model.store;
                
                var def = UtilTree.getSubTree(parentItem, prefix.length);
                
                def.addCallback(function(data){
                    // just use the object node here!
                    if (parentItem.root) {
                        var origId = data[1].id;
                        data[1].id = prefix + data[1].id;
                        store.newItem(data[1]);
                    }
                    else {
                        dojo.forEach(data, function(entry){
                            var origId = entry.id;
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
			
			function prepareDataGrid() {
                var addressSearchResultsTableStructure = [
                    {field: 'icon',name: '&nbsp',width: '23px'},
                    {field: 'title',name: "<fmt:message key='general.addresses' />",width: 668-scrollBarWidth+'px'}
                ];
                createDataGrid("addressSearchResultsTable", null, addressSearchResultsTableStructure, null);
			}
			
            function addAddressIcon(adr){
                switch (adr.addressClass) {
                    case 0: // Institution
                        adr.icon = "<img src='img/UDK/addr_institution.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
                        break;
                    case 1: // Unit
                        adr.icon = "<img src='img/UDK/addr_unit.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
                        break;
                    case 2: // Person
                        adr.icon = "<img src='img/UDK/addr_person.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
                        break;
                    case 3: // Free
                        adr.icon = "<img src='img/UDK/addr_free.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
                        break;
                    default:
                        adr.icon = "<img src='img/UDK/addr_institution.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
                        break;
                }
            }
            
            function addAddressTitle(adr){
                adr.title = UtilAddress.createAddressTitle(adr);
            }
            
            function addAddressLinkLabel(adr){
                adr.linkLabel = "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\"" + adr.uuid + "\", \"A\");'" +
                "title='" +
                adr.title +
                "'>" +
                adr.title +
                "</a>";
            }
            
            function addAddressToStore(adrId){
                var dlg = dijit.byId("pageDialog");
                // If the dialog was called with a resultHandler, call it with the selected address uuid and return
                if (dlg.customParams && dlg.customParams.resultHandler) {
                	dlg.customParams.resultHandler.callback(adrId);
                    return;
                }
                
                //var store = dijit.byId("generalAddress").store;
                
                var linkType = null;
                var nameOfRelation = "";
                var typeOfRelation = 0;
                if (dlg.customParams && dlg.customParams.linkType) {
                    linkType = dlg.customParams.linkType;
                    // TODO get value for link Type, add it to the address
                    // getIdValueForLinkType(linkType);
                    // ...
                    for (var i = 0; i < referenceMap.length; ++i) {
                        if (referenceMap[i][1] == linkType) {
                            nameOfRelation = referenceMap[i][0];
                            break;
                        }
                    }
                }
                
                AddressService.getAddressData(adrId+"", "false", {
                    callback: function(res){
                        if (res != null) {
                            addAddressIcon(res);
                            addAddressTitle(res);
                            addAddressLinkLabel(res);
                            // If the user adds a new address to the 'generalAddress' table
                            if (nameOfRelation == "") {
								var data = UtilGrid.getTableData("generalAddress");
                                // Ckeck if there already is an empty row where the address can be added
                                for (var i in data) {
                                    if (typeof(data[i].uuid) == "undefined") {
                                        // Remove the object references since we don't need them in the address table
                                        // This information is removed because it can be quite large if the address is referenced by many objects 
                                        res.linksFromObjectTable = [];
                                        
                                        res.typeOfRelation = linkType;
										res.nameOfRelation = data[i].nameOfRelation;
                                        res.refOfRelation = 2010;
										// delete first empty available item and replace it with new address
                                        UtilGrid.updateTableDataRow("generalAddress", i, res);
                                        return;
                                    }
                                }
                            }
                            
                            res.typeOfRelation = linkType;
                            res.nameOfRelation = nameOfRelation;
                            res.refOfRelation = 2010;
                            // Remove the object references since we don't need them in the address table
                            // This information is removed because it can be quite large if the address is referenced by many objects 
                            res.linksFromObjectTable = [];
                            // if an address is added from another location, also add it to generalAddress!
                            UtilGrid.addTableDataRow("generalAddress", res);
                            if (dijit.byId("pageDialog").customParams.grid != "generalAddress") { 
                                UtilGrid.addTableDataRow(dijit.byId("pageDialog").customParams.grid, res);
                            }
                        }
                    },
                    //		timeout:20000,
                    errorHandler: function(message){
                        console.debug("Error in js/udkDataProxy.js: Error while waiting for addressData: " + message);
                    }
                });
            }
            
            addSelectedAddressFromTree = function(){
                var selectedItem = dijit.byId("treeAdr").selectedNode.item;
                
                if (selectedItem) {
                    var nodeId = selectedItem.id[0].substring(8);
                    if (nodeId != "addressRoot" && nodeId != "addressFreeRoot") {
                        //			if (dojo.lang.every(dstStore.getData(), function(item){ return (item.uuid != nodeId); })) {
                        //addAddressToStore(itemToJS(store, selectedItem), nodeId);
						addAddressToStore(nodeId);
                        //			}
                    }
                }
                dijit.byId("pageDialog").hide();
            }
            
            addSelectedAddress = function(){
                var selectedNodes = UtilGrid.getSelectedData("addressSearchResultsTable");//dijit.byId("addressSearchResultsTable").selection.getSelected();
                
                if (selectedNodes.length > 0) {
					var nodeId = selectedNodes[0].uuid;
					// since it's only possible to choose one item, use the first (and only item ) of the array
                    //addAddressToStore(itemToJS(store, selectedNode[0]), nodeId);
					addAddressToStore(nodeId);
                } else {
					// TODO: no item selected
				}
                dijit.byId("pageDialog").hide();
            }
            
            
            function updateResults(searchResult){
                dojo.style("addressLinkSearchResults", "display", "block");
                
                UtilList.addIcons(searchResult.resultList);
                UtilList.addAddressTitles(searchResult.resultList);
				UtilGrid.setTableData("addressSearchResultsTable", searchResult.resultList);

                adrPageNav.setTotalNumHits(searchResult.totalNumHits);
                adrPageNav.updateDomNodes();
            }
			
            function updateQueryFromInput(){
                currentQuery.organisation = dojo.trim(dijit.byId("addressSearchUnit").getValue());
                currentQuery.name = dojo.trim(dijit.byId("addressSearchLastname").getValue());
                currentQuery.givenName = dojo.trim(dijit.byId("addressSearchFirstname").getValue());
                
                if (currentQuery.organisation.length == 0) 
                    currentQuery.organisation = null;
                
                if (currentQuery.name.length == 0) 
                    currentQuery.name = null;
                
                if (currentQuery.givenName.length == 0) 
                    currentQuery.givenName = null;
            }
            
            function browseSearchResults(){
                QueryService.searchAddresses(currentQuery, adrPageNav.getStartHit(), 10, {
                    callback: function(res){
                        updateResults(res);
                    },
                    //		timeout:30000,
                    errorHandler: function(message){
                        console.debug("Error in mdek_address_dialog.jsp: Error while searching for addresses:" + message);
                    }
                });
            }
            
            startNewSearch = function(){
                dojo.byId("addressLinkSearchResults").style.display = "none";
                
                // build query
                updateQueryFromInput();
                
                // reset the page navigation
                adrPageNav.reset();
				//var adrPageNav = 0;
                QueryService.searchAddresses(currentQuery, adrPageNav.getStartHit(), 10, {
                    callback: function(res){
                        updateResults(res);
                    },
                    //		timeout:30000,
                    errorHandler: function(message){
                        console.debug("Error in mdek_address_dialog.jsp: Error while searching for addresses:" + message);
                    }
                });
            }
            
        </script>
    </head>
    <body>
        <div class="" style="width:100%;">
        	<div id="winNavi">
                	<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-2#maintanance-of-objects-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
            </div>
           <div id="addressContent" class="content">
                <!-- LEFT HAND SIDE CONTENT START -->
                <div id="addressContainer" dojoType="dijit.layout.TabContainer" style="height:528px; overflow:visible;" selectedChild="addressSearch">
                    <!-- TAB 1 START -->
                    <div id="addressSearch" dojoType="dijit.layout.ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.searchAddress.directSearch" />">
                        <div class="inputContainer field grey">
                            <span class="outer">
                            <div>
                                <span class="label">
                                    <label for="addressSearchUnit" onclick="javascript:dialog.showContextHelp(arguments[0], 7017)">
                                        <fmt:message key="dialog.searchAddress.unitOrInstitution" />
                                    </label>
                                </span><span class="input spaceBelow"><input id="addressSearchUnit" class="dijitInputContainer" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                            </div></span>
                            <span class="outer halfWidth"><div>
                                <span class="label">
                                    <label for="addressSearchLastname" onclick="javascript:dialog.showContextHelp(arguments[0], 7018)">
                                        <fmt:message key="dialog.searchAddress.surName" />
                                    </label>
                                </span><span class="input"><input id="addressSearchLastname" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                            </div>
                            </span>
                            <span class="outer halfWidth"><div>
                                <span class="label">
                                    <label for="addressSearchFirstname" onclick="javascript:dialog.showContextHelp(arguments[0], 7019)">
                                        <fmt:message key="dialog.searchAddress.foreName" />
                                    </label>
                                </span><span class="input"><input id="addressSearchFirstname" style="width:100%;" dojoType="dijit.form.ValidationTextBox" /></span>
                            </div>
                            </span>
                            <div class="fill"></div>
                        </div>
                        <div class="inputContainerFooter">
                            <span class="button"><span style="float:right;">
                                    <button dojoType="dijit.form.Button" onClick="startNewSearch" type="button">
                                        <fmt:message key="dialog.searchAddress.search" />
                                    </button>
                                </span><span id="thesLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden"><img id="thesImageZone" src="img/ladekreis.gif" /></span></span>
                        </div>
                        <div class="fill"></div>
                        <div id="addressLinkSearchResults" class="inputContainer" style="margin: 5px;">
                            <div class="">
                                <span class="label"><fmt:message key="dialog.searchAddress.results" /></span>
                                <div class="listInfo full">
                                    <span id="addressLinkSearchResultsInfo" class="searchResultsInfo">&nbsp;</span>
                                    <span id="addressLinkSearchResultsPaging" class="searchResultsPaging">&nbsp;</span>
                                    <div class="fill">
                                    </div>
                                </div>
                            </div>
                            <div class="tableContainer">
                                <div id="addressSearchResultsTable" autoHeight="10"></div>
                            </div>
                            <div class="">
                                <span class="button" style="float:right;"><span style="float:right;">
                                        <button dojoType="dijit.form.Button" onClick="addSelectedAddress">
                                            <fmt:message key="dialog.searchAddress.selectAddress" />
                                        </button>
                                    </span></span>
                            </div>
                        </div>
                    </div><!-- TAB 1 END --><!-- TAB 2 START -->
                    <!-- TAB 2 START -->
					<div id="addressHierarchy" dojoType="dijit.layout.ContentPane" class="blueTopBorder" style="width: 100%;" title="<fmt:message key="dialog.searchAddress.treeSearch" />">
                        <div dojoType="dijit.layout.ContentPane" class="inputContainer grey" style="height: 450px; padding:0px !important;" >
                            <div id="treeAdr">
                            </div>
                        </div>
                        <div class="inputContainerFooter" style="width:100%;">
                            <span class="button" style="float:right;"><span style="float:right;">
                                    <button dojoType="dijit.form.Button" onClick="addSelectedAddressFromTree">
                                        <fmt:message key="dialog.searchAddress.selectAddress" />
                                    </button>
                                </span>
							</span>
                        </div>
                    </div><!-- TAB 2 END -->
                </div><!-- LEFT HAND SIDE CONTENT END -->
                </div>
                </div>
                </body>
            </html>