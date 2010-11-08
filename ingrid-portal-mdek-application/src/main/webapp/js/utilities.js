/**
 * Indirect Page Navigation obj
 * DEPRECATED
 *  The purpose of this object is to display a simple page navigation for a given result set.
 *  It is initialised with the number of displayed results per page and the info/paging span where
 *  the navigation should be displayed. 
 *  To display/update the nav bar, use setTotalNumHits to set the number of hits that should be navigated.
 *  When the user selects a page, the 'onPageSelected' function is called with the selected page number as argument.
 */
function IndirectPageNavigation(args) {
	this.resultsPerPage = args.resultsPerPage;
	this.infoSpan = args.infoSpan;
	this.pagingSpan = args.pagingSpan;

	this.startHit = 0;
	this.totalNumHits = 0;
	this.pageSelectorStart = 1;

	this.reset = function() {
		this.startHit = 0;
		this.totalNumHits = 0;
		this.pageSelectorStart = 1;
	}

	this.setTotalNumHits = function(numHits) {
		this.totalNumHits = numHits;
	}

	this.getStartHit = function() {
		return this.startHit;
	}

	this.updateDomNodes = function() {
		var _this = this;
		var totalNumHits = this.totalNumHits;
		var startHit = this.startHit;
		var endHit = startHit+this.resultsPerPage-1;

		if (totalNumHits <= endHit)
			endHit = totalNumHits - 1;
	
		var currentPage = Math.floor(startHit/this.resultsPerPage) + 1;
		var lastPage = Math.ceil(totalNumHits/this.resultsPerPage);
	
		// Update the dom elements
		if (totalNumHits == 0) {
			this.infoSpan.innerHTML = message.get("ui.pageNav.zeroHits");
		} else if (totalNumHits == 1) {
			this.infoSpan.innerHTML = dojo.string.substituteParams(message.get("ui.pageNav.beginEndTotalHit"), startHit+1, endHit+1, totalNumHits);
		} else {
			this.infoSpan.innerHTML = dojo.string.substituteParams(message.get("ui.pageNav.beginEndTotalHits"), startHit+1, endHit+1, totalNumHits);
		}
	
		// Clear the paging span
		while (this.pagingSpan.hasChildNodes())
			this.pagingSpan.removeChild(this.pagingSpan.firstChild);
	
		// Add info text to the paging span
		if (totalNumHits == 0) {
			this.pagingSpan.appendChild(document.createTextNode(dojo.string.substituteParams(message.get("ui.pageNav.currentToLastPages"), 1, 1)));
		} else {
			this.pagingSpan.appendChild(document.createTextNode(dojo.string.substituteParams(message.get("ui.pageNav.currentToLastPages"), currentPage, lastPage)));
		}
	
		// Add navigation to the next set of page navigation links
		if (this.pageSelectorStart > 1) {
			// Add '<<' jump to page 1
			this.pagingSpan.appendChild(document.createTextNode(" "));
			var link = document.createElement("a");
			link.onclick = function() {
				_this.pageSelectorStart = 1;
				_this.updateDomNodes();
			}
			link.setAttribute("href", "javascript:void(0);");
			link.setAttribute("title", message.get("ui.pageNav.firstPage"));
			link.innerHTML = "&lt;&lt;";

			this.pagingSpan.appendChild(link);

			// Add '<' jump to currentPage - 4
			this.pagingSpan.appendChild(document.createTextNode(" "));
			var link = document.createElement("a");
			link.onclick = function() {
				_this.pageSelectorStart -= 4;
				_this.updateDomNodes();
			}
			link.setAttribute("href", "javascript:void(0);");
			link.setAttribute("title", message.get("ui.pageNav.navBack"));
			link.innerHTML = "&lt;";

			this.pagingSpan.appendChild(link);
		}
	
		// Add the page selection links
		for (var i = this.pageSelectorStart; i < this.pageSelectorStart+4; ++i) {
	//		dojo.debug("i: "+i);
	//		dojo.debug("pageSelectorStart+4: "+pageSelectorStart+4);
	
			if (i > lastPage)
				break;
	
			this.pagingSpan.appendChild(document.createTextNode(" "));
	
			if (i == currentPage) {
				var textNode = document.createElement("em");
				textNode.innerHTML = i;
				this.pagingSpan.appendChild(textNode);
			} else {		
				var link = document.createElement("a");
				link.onclick = function() {
					_this.onPageSelected(this.innerHTML);
				}
				link.setAttribute("href", "javascript:void(0);");
				link.setAttribute("title", dojo.string.substituteParams(message.get("ui.pageNav.pageNum"), i));
				link.innerHTML = i;
		
				this.pagingSpan.appendChild(link);
			}
		}
	
		// Add navigation to the next set of page navigation links
		if (this.pageSelectorStart+4 <= lastPage) {
			this.pagingSpan.appendChild(document.createTextNode(" "));
			var link = document.createElement("a");
			link.onclick = function() {
				_this.pageSelectorStart += 4;
				_this.updateDomNodes();
			}
			link.setAttribute("href", "javascript:void(0);");
			link.setAttribute("title", message.get("ui.pageNav.navForward"));
			link.innerHTML = "&gt;";
			this.pagingSpan.appendChild(link);

	
			this.pagingSpan.appendChild(document.createTextNode(" "));
			var link = document.createElement("a");
			link.onclick = function() {
				while (_this.pageSelectorStart+4 <= lastPage)
					_this.pageSelectorStart += 4;
				_this.updateDomNodes();
			}
			link.setAttribute("href", "javascript:void(0);");
			link.setAttribute("title", message.get("ui.pageNav.lastPage"));
			link.innerHTML = "&gt;&gt;";
			this.pagingSpan.appendChild(link);
		}
	}

	this.onPageSelected = function(pageIndex) {
		this.startHit = (pageIndex-1) * this.resultsPerPage;
	}
}


/**
 * Page Navigation obj
 *  The purpose of this object is to display a simple page navigation for a given result set.
 *  It behaves similar to 'IndirectPageNavigation' with the following difference:
 *    If the user clicks the '<<', '<', '>' or '>>' button, an onPageSelected event is fired for the next page
 */
function PageNavigation(args) {
	this.resultsPerPage = args.resultsPerPage;
	this.infoSpan = args.infoSpan;
	this.pagingSpan = args.pagingSpan;

	this.startHit = 0;
	this.totalNumHits = 0;

	this.reset = function() {
		this.startHit = 0;
		this.totalNumHits = 0;
	}

	this.setTotalNumHits = function(numHits) {
		this.totalNumHits = numHits;
	}

	this.getStartHit = function() {
		return this.startHit;
	}

	this.updateDomNodes = function() {
		var _this = this;
		var totalNumHits = this.totalNumHits;
		var startHit = this.startHit;
		var endHit = startHit+this.resultsPerPage-1;

		if (totalNumHits <= endHit)
			endHit = totalNumHits - 1;
	
		var currentPage = Math.floor(startHit/this.resultsPerPage) + 1;
		var lastPage = Math.ceil(totalNumHits/this.resultsPerPage);

		// startPage determines which page should be shown first
		var startPage = currentPage - 2;
		if (startPage+4 > lastPage) {
			startPage = lastPage-4;
		}
		if (startPage < 1) {
			startPage = 1;
		}
/*
		dojo.debug("totalNumHits: "+totalNumHits);
		dojo.debug("startHit: "+startHit);
		dojo.debug("endHit: "+endHit);
		dojo.debug("currentPage: "+currentPage);
		dojo.debug("lastPage: "+lastPage);
*/

		// Update the dom elements
		if (totalNumHits == 0) {
			this.infoSpan.innerHTML = message.get("ui.pageNav.zeroHits");
		} else if (totalNumHits == 1) {
			this.infoSpan.innerHTML = dojo.string.substituteParams(message.get("ui.pageNav.beginEndTotalHit"), startHit+1, endHit+1, totalNumHits);
		} else {
			this.infoSpan.innerHTML = dojo.string.substituteParams(message.get("ui.pageNav.beginEndTotalHits"), startHit+1, endHit+1, totalNumHits);
		}
	
		// Clear the paging span
		while (this.pagingSpan.hasChildNodes())
			this.pagingSpan.removeChild(this.pagingSpan.firstChild);
	
		// Add info text to the paging span
		if (totalNumHits == 0) {
			this.pagingSpan.appendChild(document.createTextNode(dojo.string.substituteParams(message.get("ui.pageNav.currentToLastPages"), 1, 1)));
		} else {
			this.pagingSpan.appendChild(document.createTextNode(dojo.string.substituteParams(message.get("ui.pageNav.currentToLastPages"), currentPage, lastPage)));
		}

		// Add navigation to the next set of page navigation links
		if (startPage > 1) {
			// Add '<<' jump to page 1
			this.pagingSpan.appendChild(document.createTextNode(" "));
			var link = document.createElement("a");
			link.onclick = function() {
				_this.onPageSelected(1);
			}
			link.setAttribute("href", "javascript:void(0);");
			link.setAttribute("title", message.get("ui.pageNav.firstPage"));
			link.innerHTML = "&lt;&lt;";
			this.pagingSpan.appendChild(link);

			// Add '<' jump to currentPage - 1
			this.pagingSpan.appendChild(document.createTextNode(" "));
			var link = document.createElement("a");
			link.previousPage = currentPage - 1;
			link.onclick = function() {
				_this.onPageSelected(this.previousPage);
			}
			link.setAttribute("href", "javascript:void(0);");
			link.setAttribute("title", message.get("ui.pageNav.navBack"));
			link.innerHTML = "&lt;";
			this.pagingSpan.appendChild(link);
		}
	
		// Add the page selection links
		for (var i = startPage; i < startPage+5; ++i) {
	//		dojo.debug("i: "+i);
	//		dojo.debug("pageSelectorStart+4: "+pageSelectorStart+4);

			if (i < 1)
				continue;

			if (i > lastPage)
				break;
	
			this.pagingSpan.appendChild(document.createTextNode(" "));
	
			if (i == currentPage) {
				var textNode = document.createElement("em");
				textNode.innerHTML = i;
				this.pagingSpan.appendChild(textNode);
			} else {		
				var link = document.createElement("a");
				link.onclick = function() {
					_this.onPageSelected(this.innerHTML);
				}
				link.setAttribute("href", "javascript:void(0);");
				link.setAttribute("title", dojo.string.substituteParams(message.get("ui.pageNav.pageNum"), i));
				link.innerHTML = i;
				this.pagingSpan.appendChild(link);
			}
		}
	
		// Add navigation to the next set of page navigation links
		if (startPage+4 < lastPage) {
			this.pagingSpan.appendChild(document.createTextNode(" "));
			var link = document.createElement("a");
			link.nextPage = currentPage + 1;
			link.onclick = function() {
				_this.onPageSelected(this.nextPage);
			}
			link.setAttribute("href", "javascript:void(0);");
			link.setAttribute("title", message.get("ui.pageNav.navForward"));
			link.innerHTML = "&gt;";
			this.pagingSpan.appendChild(link);

			this.pagingSpan.appendChild(document.createTextNode(" "));
			var link = document.createElement("a");
			link.lastPage = lastPage;
			link.onclick = function() {
				_this.onPageSelected(this.lastPage);
			}
			link.setAttribute("href", "javascript:void(0);");
			link.setAttribute("title", message.get("ui.pageNav.lastPage"));
			link.innerHTML = "&gt;&gt;";
			this.pagingSpan.appendChild(link);
		}
	}

	this.onPageSelected = function(pageIndex) {
		this.startHit = (pageIndex-1) * this.resultsPerPage;
	}
}


/*
 * General Helper Functions
 */

// Misc DWR Utility functions 
var UtilDWR = {}

// dwr preHook Function that has to be called by each dwr call that blocks certain interface functions (save, publish, etc.) 
UtilDWR.enterLoadingState = function() {
    var loadingZone = dojo.byId("loadingZone");
    var blockInputDiv = dojo.byId("blockInputDiv");
    loadingZone.style.visibility = "visible";
    blockInputDiv.style.visibility = "visible";

	UtilDWR.onEnterLoadingState();
}

// dwr postHook Function that has to be called after the dwr call returned 
UtilDWR.exitLoadingState = function() {
    var loadingZone = dojo.byId("loadingZone");
    var blockInputDiv = dojo.byId("blockInputDiv");
    loadingZone.style.visibility = "hidden";
    blockInputDiv.style.visibility = "hidden";

	UtilDWR.onExitLoadingState();
}

// Functions for users to connect to
UtilDWR.onEnterLoadingState = function() {}
UtilDWR.onExitLoadingState = function() {}



// Util functions for handling MdekAddressBeans 
var UtilAddress = {}

// Get the address class for an address type ('institution', 'person', etc.)
// If no parameter is specified, the class of the currently selected address is returned
UtilAddress.getAddressClass = function(addressType /*optional*/) {
	if (typeof(addressType) == "undefined") {
		addressType = dojo.widget.byId("addressType").getValue();
	}

	if (addressType == message.get('address.type.institution'))
		return 0;
	else if (addressType == message.get('address.type.unit'))
		return 1;
	else if (addressType == message.get('address.type.person'))
		return 2;
	else if (addressType == message.get('address.type.custom'))
		return 3;
	else {
		dojo.debug("Error in UtilAddress.getAddressClass! Invalid addressType: "+addressType);
		return -1;
	}
}

// Get the address type for an address class (0-3)
UtilAddress.getAddressType = function(addressClass) {
	if (addressClass == 0)
		return message.get('address.type.institution');
	else if (addressClass == 1)
		return message.get('address.type.unit');
	else if (addressClass == 2)
		return message.get('address.type.person');
	else if (addressClass == 3)
		return message.get('address.type.custom');
	else {
		dojo.debug("Error in UtilAddress.getAddressType! Invalid addressClass: "+addressClass);
		return "";
	}
}

// Initialize the object->address reference table with a given array of links 'linkList'. The number of references is specified with
// the parameter 'numReferences'.
// Sets the label and link to their initial values. Further links are loaded from the backend via UtilAddress.navObjectAddressReferences
UtilAddress.initObjectAddressReferenceTable = function(linkList, numReferences) {
	var tableLabel = dojo.byId("associatedObjNameLabel");
	var tableLink = dojo.byId("associatedObjNameLink");
	var tableStore = dojo.widget.byId("associatedObjName").store;

	// Add table specific data to the list
	UtilList.addTableIndices(linkList);
	UtilList.addObjectLinkLabels(linkList);  
	UtilList.addIcons(linkList);
	
	// Clear the underlying store
	tableStore.clearData();

	if (UtilAddress.objectAddressRefPageNav) {
		UtilAddress.objectAddressRefPageNav.reset();	

	} else {
		UtilAddress.objectAddressRefPageNav = new PageNavigation({ resultsPerPage: 20, infoSpan:dojo.byId("associatedObjNameInfo"), pagingSpan:dojo.byId("associatedObjNamePaging") });
		dojo.event.connectOnce("after", UtilAddress.objectAddressRefPageNav, "onPageSelected", function() { UtilAddress.navObjectAddressReferences(); });
	}

	UtilAddress.objectAddressRefPageNav.setTotalNumHits(numReferences);
	tableStore.setData(linkList);	
	UtilAddress.objectAddressRefPageNav.updateDomNodes();
}

UtilAddress.navObjectAddressReferences = function() {
	var curPos = UtilAddress.objectAddressRefPageNav.getStartHit();
	var totalNumHits = UtilAddress.objectAddressRefPageNav.totalNumHits;
	var tableStore = dojo.widget.byId("associatedObjName").store;

	// TODO Do we need to get the uuid from somewhere else?
	AddressService.fetchAddressObjectReferences(currentUdk.uuid, curPos, 20, {
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(adr){
//				dojo.debugShallow(adr);

				tableStore.clearData();

				var unpubLinkTable = adr.linksFromObjectTable;
				var pubLinkTable = adr.linksFromPublishedObjectTable;
				dojo.lang.forEach(pubLinkTable, function(link) { link.pubOnly = true; } );
				var linkTable = pubLinkTable.concat(unpubLinkTable);

				UtilList.addTableIndices(linkTable);
				UtilList.addObjectLinkLabels(linkTable);  
				UtilList.addIcons(linkTable);

				tableStore.setData(linkTable);
				UtilAddress.objectAddressRefPageNav.updateDomNodes();
			},
			errorHandler:function(message) {
				UtilDWR.exitLoadingState();
				dojo.debug("Error: "+message);
			}
	});
}

// Checks if a title can be constructed for the given adr
UtilAddress.hasValidTitle = function(adr) {
	var title = "";
	switch (adr.addressClass) {
		case 0: // Institution
			title = adr.organisation;
			break;
		case 1:	// Unit
			title = adr.organisation;
			break;
		case 2: // Person
			if (adr.name) title += adr.name;
			if (adr.givenName) title += ", "+adr.givenName;
			break;
		case 3: // Freie Adresse
			if (adr.name) title += adr.name;
			if (adr.givenName) title += ", "+adr.givenName;
			if (adr.organisation) title += " ("+adr.organisation+")";
			break;
		default:
			break;
	}
	if (title == null || title == "")
		return false;
	else
		return true;
}

// Builds an address title from a given MdekAddressBean.
// example arguments:
//   adr: {addressClass: 0, organisation: "testOrga" }
//        returns: "testOrga"
//   adr: {addressClass: 1, organisation: "testOrga" }
//        returns: "testOrga"
//   adr: {addressClass: 2, name: "Name", givenName: "Vorname" }
//        returns: "Name, Vorname"
//   adr: {addressClass: 3, name: "Name", givenName: "Vorname", organisation: "testOrga" }
//        returns: "Name, Vorname (testOrga)"
UtilAddress.createAddressTitle = function(adr) {
	var title = "";
	switch (adr.addressClass) {
		case 0: // Institution
			title = adr.organisation;
			break;
		case 1:	// Unit
			title = adr.organisation;
			break;
		case 2: // Person
			if (adr.name) title += adr.name;
			if (adr.givenName) title += ", "+adr.givenName;
			break;
		case 3: // Freie Adresse
			if (adr.name) title += adr.name;
			if (adr.givenName) title += ", "+adr.givenName;
			if (adr.organisation) title += " ("+adr.organisation+")";
			break;
		default:
			break;
	}
	if (title == null || title == "")
		return message.get("tree.newAddressName");
	else
		return dojo.string.escape("html", dojo.string.trim(title));
}

// returns a 'linkLabel' depending on the 'uuid' and 'title' of the address.
// linkLabel is a html href to directly jump to a given address in the main tree
UtilAddress.createAddressLinkLabel = function(adrUuid, adrTitle) {
	return "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\""+adrUuid+"\", \"A\");'"+
		"title='"+adrTitle+"'>"+adrTitle+"</a>";
}


// Utility functions for handling misc data from the frontend
var UtilUdk = {}

UtilUdk.isObjectSelected = function() {
	var treeWidget = dojo.widget.byId("tree");

	if (typeof(treeWidget) == "undefined") {
		return true;
	}

	var node = treeWidget.selectedNode;
	if (typeof(node) != "undefined" && node != null) {
		return (node.nodeAppType == "O");
	}
}

// check whether INSPIRE terms are in thesaurusInspireTermsList (from nodeData)  
UtilUdk.isInspire = function(thesaurusInspireTermsList) {
    if (dojo.lang.some(thesaurusInspireTermsList, function(iTermKey) {
       return (dojo.string.trim(iTermKey) != "99999"); })) {
	   return true;
    }	
	return false;
}

UtilUdk.getCurrentObjectClass = function() {	
	var objectClassWidget = dojo.widget.byId("objectClass");
	if (typeof(objectClassWidget) == "undefined") {
		return 0;
	}

	return objectClassWidget.getValue()[5];
}

UtilUdk.getCurrentAddressClass = function() {	
	return UtilAddress.getAddressClass();	
}

// Load a helpMessage for the specified guiId from the server.
// Returns a dojo deferred. The callback is invoked with a HelpMessage object
UtilUdk.loadHelpMessage = function(guiId) {
	var deferred = new dojo.Deferred();
	// Load the help message for 'guiId' from the backend.
	// First we need the current object/address class
	var cls = this.isObjectSelected() ? this.getCurrentObjectClass() : this.getCurrentAddressClass(); 

	// Then load the help message via HelpService
	HelpService.getHelpEntry(guiId, cls, {
		callback: function(helpEntry) {
			if (helpEntry) {
				deferred.callback(helpEntry);
			} else {
				deferred.errback();
			}
		},
		errorHandler: function(errMsg, err) {
			deferred.errback(new Error(errMsg));
		}
	});

	return deferred;
}



// Util functions for handling lists 
var UtilList = {}

// Iterates over an array of objects/addresses/urls and adds an 'icon' property consisting of a html image tag depending on following properties:
// objectClass = 0..5   -> udk_classx.gif
// addressClass = 0..3  -> addr_...gif
// url != undefined     -> url.gif
//
// The list is returned
UtilList.addIcons = function(list) {
	for (var i = 0; i < list.length; ++i) {
		if (typeof(list[i].objectClass) != "undefined") {
			list[i].icon = "<img src='img/UDK/udk_class"+list[i].objectClass+".gif' width=\"16\" height=\"16\" alt=\"Object\" />";
		} else if (typeof(list[i].addressClass) != "undefined") {
			switch (list[i].addressClass) {
				case 0:	// Institution
					list[i].icon = "<img src='img/UDK/addr_institution.gif' width=\"16\" height=\"16\" alt=\"Address\" />";		
					break;
				case 1:	// Unit
					list[i].icon = "<img src='img/UDK/addr_unit.gif' width=\"16\" height=\"16\" alt=\"Address\" />";		
					break;
				case 2:	// Person
					list[i].icon = "<img src='img/UDK/addr_person.gif' width=\"16\" height=\"16\" alt=\"Address\" />";		
					break;
				case 3:	// Free
					list[i].icon = "<img src='img/UDK/addr_free.gif' width=\"16\" height=\"16\" alt=\"Address\" />";		
					break;
				default:
					list[i].icon = "<img src='img/UDK/addr_institution.gif' width=\"16\" height=\"16\" alt=\"Address\" />";		
					break;
			}
		} else if (typeof(list[i].url) != "undefined") {
			list[i].icon = "<img src='img/UDK/url.gif' width=\"16\" height=\"16\" alt=\"Url\" />";		
		} else {
			list[i].icon = "noIcon";
		}
	}
	return list;
}

// Iterates over an array of addresses, extracts their titles and adds them as properties 'title'
// See UtilAddress.createAddressTitle for more info
UtilList.addAddressTitles = function(list) {
	for (var i = 0; i < list.length; ++i) {
		list[i].title = UtilAddress.createAddressTitle(list[i]);
	}
	return list;
}

// Add object link labels to a passed list.
// This function iterates over all entries in the list and adds a value: 'linkLabel' to each node
// which is a href to the menuEventHandler 'selectNodeInTree' function
// If an entry contains the variable 'pubOnly = true', the text color is set to grey
UtilList.addObjectLinkLabels = function(list) {
	for (var i = 0; i < list.length; ++i) {
		if (list[i].pubOnly) {
			list[i].linkLabel = "<a class='pubOnly' href='javascript:menuEventHandler.handleSelectNodeInTree(\""+list[i].uuid+"\", \"O\");'"+
		                    "title='"+list[i].title+"'>"+list[i].title+"</a>";			
		} else {
			list[i].linkLabel = "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\""+list[i].uuid+"\", \"O\");'"+
		                    "title='"+list[i].title+"'>"+list[i].title+"</a>";
		}
	}
	return list;
}

// Iterates over an array of addresses and adds a property 'linkLabel' depending on the 'uuid' and 'title' of the address.
// linkLabel is a html href to directly jump to a given address in the main tree
UtilList.addAddressLinkLabels = function(list) {
	for (var i = 0; i < list.length; ++i) {
		list[i].linkLabel = "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\""+list[i].uuid+"\", \"A\");'"+
		                    "title='"+list[i].title+"'>"+list[i].title+"</a>";
	}
	return list;
}


// Iterates over an array of urls and adds a property 'linkLabel' depending on the 'url' and 'name' of the url.
// linkLabel is a html href to the url target
UtilList.addUrlLinkLabels = function(list) {
	for (var i = 0; i < list.length; ++i) {
		if (list[i].url) {
			if (dojo.string.startsWith(list[i].url, "http://"))
				list[i].linkLabel = "<a href='"+list[i].url+"' target=\"_blank\" title='"+list[i].name+"'>"+list[i].name+"</a>";
			else
				list[i].linkLabel = list[i].name;
		}
	}
	return list;
}

// Iterates over an array and adds the 'displayDate' property to each object depending on the 'date' property (javscript date object)
UtilList.addDisplayDates = function(list) {
	if (list) {
		for (var i = 0; i < list.length; ++i) {
//			list[i].displayDate = list[i].date.toLocaleString();
			list[i].displayDate = dojo.date.format(list[i].date, {formatLength:"short", datePattern:"dd.MM.yyyy", timePattern:"HH:mm"});
		}
		return list;
	} else {
		return [];
	}
}


// Creates table data from a list of values.
// ["a", "b", "c"] -> [{identifier: "a"}, {identifier: "b"}, {identifier: "c"}]
UtilList.listToTableData = function(list, identifier) {
	var resultList = [];
	if (typeof(identifier) == "undefined")
		identifier = "title";

	dojo.lang.forEach(list, function(item){
		var x = {};
		x[identifier] = item;
		resultList.push(x);
	});
	return resultList;
}


//Creates a list from table data
// [{identifier: "a"}, {identifier: "b"}, {identifier: "c"}] -> ["a", "b", "c"] 
UtilList.tableDataToList = function(tableData, identifier) {
	var resultList = [];
	if (typeof(identifier) == "undefined") {
		identifier = "title";
	}

	for (var i = 0; i < tableData.length; ++i) {
		resultList.push(tableData[i][identifier]);
	}

	return resultList;
}


// Add Indices (Id values) to a passed list
UtilList.addTableIndices = function(list) {
	if (list) {
		for (var i = 0; i < list.length; ++i) {
			list[i].Id = i;
		}
		return list;
	} else {
		return [];
	}
}

// Add SNS Location labels (entry.label = entry.name + ", " + (entry.topicType || entry.type))
UtilList.addSNSLocationLabels = function(list) {
	if (list) {
		dojo.lang.forEach(list, function(entry){
			entry.label = entry.name;
			if (entry.topicType) {
				entry.label += ", " + entry.topicType;
			}
		});
		return list;

	} else {
		return [];
	}
}

// Add SNS topic labels (entry.label = entry.alternateTitle + " / " + entry.title)
UtilList.addSNSTopicLabels = function(list) {
	if (list) {
		dojo.lang.forEach(list, function(entry) {
			entry.label = entry.title;
			entry.sourceString = entry.source;
			if (entry.alternateTitle && entry.alternateTitle != entry.title) {
				// if UMTHES and GEMET is different then show "UMTHES/GEMET"
				entry.label = entry.alternateTitle + " / " + entry.title;
				entry.sourceString = "UMTHES/GEMET";
			}
		});
		return list;

	} else {
		return [];
	}
}

// Mark expired SNS locations
UtilList.markExpiredSNSLocations = function(list) {
	if (list) {
		dojo.lang.forEach(list, function(entry) {
			if (entry.locationExpiredAt != null) {
				entry.label = "<span style='color:#FF0000'>" + entry.label + "</span>";
			}
		});
		return list;

	} else {
		return [];
	}
}


// Util functions for dojo store 
var UtilStore = {}

// Helper function that iterates over all entries in a store and returns a key that is not in use yet
UtilStore.getNewKey = function(store) {
	var key = 0;
	var data = store.get();
	for(var i=0; i < data.length; i++){
		if(data[i].key >= key){
			key = data[i].key + 1;
		}
	}
	return key;
}

// Util functions for the current stored catalog 
var UtilCatalog = {}

// Helper function that returns the catalog language. If no language is specified, "de" is returned
UtilCatalog.getCatalogLanguage = function() {
	if (catalogData && typeof(catalogData.languageShort) != "undefined" && catalogData.languageShort != null) {
		return catalogData.languageShort;
	} else {
		return "de";
	}
}

// returns a specific entry (array with three values [displayValue, entryId, default(Y or N)])from a syslist
UtilCatalog.getSysListEntry = function(sysListId, entryId) {
	var def = new dojo.Deferred();

	// Setting the language code to "de". Uncomment the previous block to enable language specific settings depending on the browser language
	var languageCode = UtilCatalog.getCatalogLanguage();
	var lstIds = [sysListId];

	CatalogService.getSysLists(lstIds, languageCode, {
		callback: function(res) {
			var sysList = res[sysListId];
			for (var i = 0; i < sysList.length; ++i) {
				if (sysList[i][1] == entryId) { 
					// Syslist entry found
					def.callback(sysList[i]);
					return;
				}
			}

			// Syslist entry not found
			def.errback();
		},
		errorHandler:function(mes){
			dojo.debug("Error: "+mes);
			def.errback(mes);
		}
	});

	return def;
}

UtilCatalog.getNameForSysList = function(sysListId) {
	var messageId = "ui.sysList." + sysListId;
	var name = message.get(messageId);

	// If the name was not found (message.get returned the key), return the default name (unknown)
	// Otherwise return the name that was found
	return (name != messageId)? name : message.get("ui.sysList.unknown"); 
}

// Constants that should be used by getGenericValuesDef and setGenericValuesDef
UtilCatalog.GENERIC_KEYS = {};
UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL = "AUTOSAVE_INTERVAL";
UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL = "SESSION_REFRESH_INTERVAL";

// Fetch generic values from the catalog
// Input is a list of key identifiers or null for all values: ["keyId1", "keyId2", ...]
// Returns a map with the key/value entries: { keyId1:value1, keyId2:value2, ... }
UtilCatalog.getGenericValuesDef = function(keyNames) {
	var def = new dojo.Deferred();

	CatalogService.getSysGenericValues(keyNames, {
		callback: function(res) {
			var genericValueMap = {};
			for (var index = 0; index < res.length; index++) {
				genericValueMap[res[index].key] = res[index].value;
			}
			def.callback(genericValueMap);
		},
		errorHandler:function(msg){
			dojo.debug("Error: "+msg);
			def.errback(msg);
		}
	});

	return def;
}

// Store generic values in the backend
// Input is a map whose key/value pairs should be stored 
// e.g. valueMap: {keyId1:value1, keyId2:value2, ...}
UtilCatalog.storeGenericValuesDef = function(valueMap) {
	var def = new dojo.Deferred();

	var valueList = [];
	for (var key in valueMap) {
		valueList.push( { key: key, value: valueMap[key] } );
	}

	CatalogService.storeSysGenericValues(valueList, {
		callback: function() {
			def.callback();
		},
		errorHandler:function(msg){
			dojo.debug("Error: "+msg);
			def.errback(msg);
		}
	});

	return def;
}

UtilCatalog.GENERIC_VALUE_CACHE = null;

// Returns the session refresh interval or 0 if no interval has been set
UtilCatalog.getSessionRefreshIntervalDef = function() {
	var def = UtilCatalog.getGenericValuesDef([UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL]);

	def.addCallback(function(valueMap) {
		var refreshInterval = valueMap[UtilCatalog.GENERIC_KEYS.SESSION_REFRESH_INTERVAL];
		if (refreshInterval && refreshInterval > 0) {
			return refreshInterval;

		} else {
			return 0;
		}
	});
	return def;
}

//Returns the autosave interval or 0 if no interval has been set
UtilCatalog.getAutosaveIntervalDef = function() {
	var def = UtilCatalog.getGenericValuesDef([UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL]);

	def.addCallback(function(valueMap) {
		var autosaveInterval = valueMap[UtilCatalog.GENERIC_KEYS.AUTOSAVE_INTERVAL];
		if (autosaveInterval && autosaveInterval > 0) {
			return autosaveInterval;

		} else {
			return 0;
		}
	});
	return def;
}

// Security related utility functions
var UtilSecurity = {}

// Retrieve the role name for a given role id
UtilSecurity.getRoleName = function(roleId) {
	switch(roleId) {
		case 1:
			return message.get("security.role.catalogAdmin");
		case 2:
			return message.get("security.role.metadataAdmin");
		case 3:
			return message.get("security.role.metadataAuthor");
		default:
			return null;
	}
}

UtilSecurity.canCreateRootNodes = function() {
	if (currentGroup == null || typeof(currentGroup.groupPermissions) == "undefined") { return false; }

	for (var i = 0; i < currentGroup.groupPermissions.length; ++i) {
		if (currentGroup.groupPermissions[i] == "CREATE_ROOT") {
			return true;
		}
	}
	return false;
}

UtilSecurity.isCurrentUserQA = function() {
	if (currentGroup == null || typeof(currentGroup.groupPermissions) == "undefined") { return false; }

	for (var i = 0; i < currentGroup.groupPermissions.length; ++i) {
		if (currentGroup.groupPermissions[i] == "QUALITY_ASSURANCE") {
			return true;
		}
	}
	return false;
}

UtilSecurity.getUsersFromCurrentGroup = function() {
	var def = new dojo.Deferred();

	SecurityService.getUsersOfGroup(currentGroup.name, {
		callback: function(userList) { def.callback(userList); },
		errback: function(errMsg, err) { def.errback(err); }
	});
	return def;
}

UtilSecurity.getCatAdmin = function() {
	var def = new dojo.Deferred();

	SecurityService.getCatalogAdmin( {
		callback: function(userList) { def.callback(userList); },
		errback: function(errMsg, err) { def.errback(err); }
	});
	return def;	
}

// Language related utility functions
var UtilLanguage = {}

UtilLanguage.getCurrentLanguage = function() {
	return djConfig.locale;
}

UtilLanguage.getNextLanguage = function() {
	var lang = UtilLanguage.getCurrentLanguage();

	if (lang == "en") {
		return "de";

	} else if (lang == "de") {
		return "en";

	} else {
		// If the current language can't be determined, return 'de'
		return "de";
	}
}

UtilLanguage.getLanguageName = function(lang) {
	return message.get("general.lang."+lang);
}

UtilLanguage.getNextLanguageName = function() {
	return UtilLanguage.getLanguageName(UtilLanguage.getNextLanguage());
}

// String utilities
var UtilString = {}

UtilString.compare = function(a, b) {
    a = a.replace("ö","oe").replace("ä","ae").replace("ü","ue").replace("ß","ss").replace("Ä","Ae").replace("Ö","Oe").replace("Ü","Ue");
    b = b.replace("ö","oe").replace("ä","ae").replace("ü","ue").replace("ß","ss").replace("Ä","Ae").replace("Ö","Oe").replace("Ü","Ue");
	return a == b ? 0 : (a < b ? -1 : 1);
}

UtilString.compareIgnoreCase = function(a, b) {
	return UtilString.compare(a.toLowerCase(), b.toLowerCase());
}

// Utility functions needed for workflow control
var UtilQA = {}

// Returns whether workflow control is activated for the current catalog
UtilQA.isQAActive = function() {
	if (catalogData && typeof(catalogData.workflowControl) != "undefined" && catalogData.workflowControl == "Y") {
		return true;
	} else {
		return false;
	}
}

// Utility functions for UI specific stuff
var UtilUI = {}

// This variable serves as a cache to store whether ui elements have been activated or not
UtilUI._uiElementsActive = true;

UtilUI.getDescriptionForGuiId = function(guiId) {
	return message.get("ui."+guiId+".description");
}

// Check if the nodeId is for a container node (objectRoot, addressRoot, addressFreeRoot)
UtilUI.isContainerNodeId = function(nodeId) {
	return nodeId == "objectRoot" || nodeId == "addressRoot" || nodeId == "addressFreeRoot";
}

// Check if the nodeId belongs to a newly created node without an id
UtilUI.isNewNodeId = function(nodeId) {
	return nodeId == "newNode";
}

// get current mode of section element, to be passed to toggleFields for refresh !
UtilUI.getCurrentExpandModeOfSectionElement = function(sectionElement) {
    if (typeof(sectionElement.isExpanded) == "undefined" || sectionElement.isExpanded == false) {
      return "showRequired";
    }
	return "showAll";
}

// General utility functions for converting strings, etc.
var UtilGeneral = {}

// Returns the stack trace of an exception as string
UtilGeneral.getStackTrace = function(exception) {
	var stackTrace = "";
	stackTrace += exception.message +"<br>";
	if (exception.cause) {
		exception = exception.cause;
	}
	stackTrace += exception.javaClassName+": "+exception.message +"<br>";
	for (var i = 0; i < exception.stackTrace.length; ++i) {
		var ex = exception.stackTrace[i];
		if (!ex.nativeMethod) {
			stackTrace += "&nbsp;&nbsp;at "+ex.className+"."+ex.methodName+"("+ex.fileName+":"+ex.lineNumber+")" +"<br>";
		} else {
			stackTrace += "&nbsp;&nbsp;at "+ex.className+"."+ex.methodName+"(Native Method)" +"<br>";
		}
	}
	return stackTrace;
}

UtilGeneral.refreshSession = function() {
	UtilityService.refreshSession();
}

UtilGeneral.generateRandomString = function(strLength) {
	var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	var string_length = strLength;
	var str = '';
	for (var i=0; i<string_length; i++) {
		var rnum = Math.floor(Math.random() * chars.length);
		str += chars.substring(rnum,rnum+1);
	}
	return str;
}

