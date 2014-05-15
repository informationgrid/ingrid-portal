dojo.require("dojo.date.locale"); // used for date conversions

var attributeReplacerMap = {
	listid: "listId",
	maxheight: "maxHeight",
    maxlength: "maxLength",
	onrowcontextmenu: "onRowContextMenu",
	minrows: "minRows",
	autoheight: "autoHeight",
	selectionmode: "selectionMode",
	onchange: "onChange",
	contextmenu: "contextMenu",
	multiselect: "multiSelect",
	forcegridheight: "forceGridHeight",
    defaulthidescrollbar: "defaultHideScrollbar"//,
	//class: "\"class\""
};

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
			this.infoSpan.innerHTML = dojo.string.substitute(message.get("ui.pageNav.beginEndTotalHit"), [startHit+1, endHit+1, totalNumHits]);
		} else {
			this.infoSpan.innerHTML = dojo.string.substitute(message.get("ui.pageNav.beginEndTotalHits"), [startHit+1, endHit+1, totalNumHits]);
		}
	
		// Clear the paging span
		while (this.pagingSpan.hasChildNodes())
			this.pagingSpan.removeChild(this.pagingSpan.firstChild);
	
		// Add info text to the paging span
		if (totalNumHits == 0) {
			this.pagingSpan.appendChild(document.createTextNode(dojo.string.substitute(message.get("ui.pageNav.currentToLastPages"), [1, 1])));
		} else {
			this.pagingSpan.appendChild(document.createTextNode(dojo.string.substitute(message.get("ui.pageNav.currentToLastPages"), [currentPage, lastPage])));
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
	//		console.debug("i: "+i);
	//		console.debug("pageSelectorStart+4: "+pageSelectorStart+4);
	
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
				link.setAttribute("title", dojo.string.substitute(message.get("ui.pageNav.pageNum"), [i]));
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
		console.debug("totalNumHits: "+totalNumHits);
		console.debug("startHit: "+startHit);
		console.debug("endHit: "+endHit);
		console.debug("currentPage: "+currentPage);
		console.debug("lastPage: "+lastPage);
*/

		// Update the dom elements
		if (totalNumHits == 0) {
			this.infoSpan.innerHTML = message.get("ui.pageNav.zeroHits");
		} else if (totalNumHits == 1) {
			this.infoSpan.innerHTML = dojo.string.substitute(message.get("ui.pageNav.beginEndTotalHit"), [startHit+1, endHit+1, totalNumHits]);
		} else {
			this.infoSpan.innerHTML = dojo.string.substitute(message.get("ui.pageNav.beginEndTotalHits"), [startHit+1, endHit+1, totalNumHits]);
		}
	
		// Clear the paging span
		while (this.pagingSpan.hasChildNodes())
			this.pagingSpan.removeChild(this.pagingSpan.firstChild);
	
		// Add info text to the paging span
		if (totalNumHits == 0) {
			this.pagingSpan.appendChild(document.createTextNode(dojo.string.substitute(message.get("ui.pageNav.currentToLastPages"), [1, 1])));
		} else {
			this.pagingSpan.appendChild(document.createTextNode(dojo.string.substitute(message.get("ui.pageNav.currentToLastPages"), [currentPage, lastPage])));
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
	//		console.debug("i: "+i);
	//		console.debug("pageSelectorStart+4: "+pageSelectorStart+4);

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
				link.setAttribute("title", dojo.string.substitute(message.get("ui.pageNav.pageNum"), [i]));
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
UtilDWR.enterLoadingState = function(/*optional*/otherLoadingDiv) {
    dojo.style(otherLoadingDiv ? otherLoadingDiv : "loadingZone", "visibility", "visible");
    dojo.style("blockInputDiv", "visibility", "visible");

    UtilUI.setVisibleBlockDiv(true);
	UtilDWR.onEnterLoadingState();
}

// dwr postHook Function that has to be called after the dwr call returned 
UtilDWR.exitLoadingState = function(/*optional*/otherLoadingDiv) {
	dojo.style(otherLoadingDiv ? otherLoadingDiv : "loadingZone", "visibility", "hidden");
    dojo.style("blockInputDiv", "visibility", "hidden");
    
    UtilUI.setVisibleBlockDiv(false);
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
		addressType = dijit.byId("addressType").getValue();
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
		console.debug("Error in UtilAddress.getAddressClass! Invalid addressType: "+addressType);
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
		console.debug("Error in UtilAddress.getAddressType! Invalid addressClass: "+addressClass);
		return "";
	}
}

// Initialize the object->address reference table with a given array of links 'linkList'. The number of references is specified with
// the parameter 'numReferences'.
// Sets the label and link to their initial values. Further links are loaded from the backend via UtilAddress.navObjectAddressReferences
UtilAddress.initObjectAddressReferenceTable = function(linkList, numReferences) {
	var tableLabel = dojo.byId("associatedObjNameLabel");
	var tableLink = dojo.byId("associatedObjNameLink");
	//var tableStore = dijit.byId("associatedObjName").store;

	// Add table specific data to the list
	UtilList.addObjectLinkLabels(linkList);  
	UtilList.addIcons(linkList);
	
	if (UtilAddress.objectAddressRefPageNav) {
		UtilAddress.objectAddressRefPageNav.reset();	

	} else {
		UtilAddress.objectAddressRefPageNav = new PageNavigation({ resultsPerPage: 20, infoSpan:dojo.byId("associatedObjNameInfo"), pagingSpan:dojo.byId("associatedObjNamePaging") });
		dojo.connect(UtilAddress.objectAddressRefPageNav, "onPageSelected", function() { UtilAddress.navObjectAddressReferences(); });
	}

	UtilAddress.objectAddressRefPageNav.setTotalNumHits(numReferences);
	UtilStore.updateWriteStore("associatedObjName", linkList);
	UtilAddress.objectAddressRefPageNav.updateDomNodes();
}

UtilAddress.navObjectAddressReferences = function() {
	var curPos = UtilAddress.objectAddressRefPageNav.getStartHit();
	var totalNumHits = UtilAddress.objectAddressRefPageNav.totalNumHits;

	// TODO Do we need to get the uuid from somewhere else?
	AddressService.fetchAddressObjectReferences(currentUdk.uuid, curPos, 20, {
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(adr){
				var unpubLinkTable = adr.linksFromObjectTable;
				var pubLinkTable = adr.linksFromPublishedObjectTable;
				dojo.forEach(pubLinkTable, function(link) { link.pubOnly = true; } );
				var linkTable = pubLinkTable.concat(unpubLinkTable);

				UtilList.addTableIndices(linkTable);
				UtilList.addObjectLinkLabels(linkTable);  
				UtilList.addIcons(linkTable);

				UtilGrid.setTableData("associatedObjName", linkTable);
				UtilAddress.objectAddressRefPageNav.updateDomNodes();
			},
			errorHandler:function(message) {
				UtilDWR.exitLoadingState();
				console.debug("Error: "+message);
			}
	});
}

// "PRIVATE" utility function for getting plain title !
UtilAddress._getAddressTitle = function(adr) {
    var title = "";
    switch (adr.addressClass) {
        case 0: // Institution
            title = adr.organisation;
            break;
        case 1: // Unit
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
        case 100: // HIDDEN IGE USER ADDRESS
            if (adr.name) title += adr.name;
            if (adr.givenName) title += ", "+adr.givenName;
            if (title.length == 0) {
            	title = adr.organisation;
            }
            break;
        default:
            break;
    }
    
    return title;
}

// Checks if a title can be constructed for the given adr
UtilAddress.hasValidTitle = function(adr) {
	var title = UtilAddress._getAddressTitle(adr);
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
    var title = UtilAddress._getAddressTitle(adr);
	if (title == null || title == "")
		return message.get("tree.newAddressName");
	else
		return dojo.trim(title);
		//return dojo.string.escape("html", dojo.string.trim(title));
		// FIXME: escape function doesn't exist anymore
}

// Returns first found value for the passed array of mediums (pass medium in de and en if from syslist, e.g. ["Telefon", "telephone"] !)
UtilAddress.getAddressCommunicationValue = function(addr, mediumList) {
	if (addr.communication) {
        for (var i=0; i<addr.communication.length; i++) {
        	var commMedium = addr.communication[i].medium;
            var commValue = addr.communication[i].value;
            for (var j=0; j<mediumList.length; j++) {
            	if (commMedium == mediumList[j] && commValue) {
            		return commValue;
            	}
            }
        }
	}
	return null;
}

// returns a 'linkLabel' depending on the 'uuid' and 'title' of the address.
// linkLabel is a html href to directly jump to a given address in the main tree
UtilAddress.createAddressLinkLabel = function(adrUuid, adrTitle) {
	return "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\""+adrUuid+"\", \"A\");'"+
		"title='"+adrTitle+"'>"+adrTitle+"</a>";
}

UtilAddress.determineInstitution = function(nodeData) {
    var institution = "";
    for (var i = nodeData.parentInstitutions.length-1; i >= 0; --i) {
        if (nodeData.parentInstitutions[i].addressClass == 0) {
            // Only display the first institution we encounter and break
            institution = nodeData.parentInstitutions[i].organisation+"\n"+institution;
            break;

        } else if (nodeData.parentInstitutions[i].addressClass == 1) {
            institution = "\t"+nodeData.parentInstitutions[i].organisation+"\n"+institution;
        }
    }
    return dojo.trim(institution);
}


// Utility functions for handling misc data from the frontend
var UtilUdk = {}

UtilUdk.isObjectSelected = function() {
	var treeWidget = dijit.byId("tree");

	if (typeof(treeWidget) == "undefined") {
		return true;
	}

	var node = treeWidget.selectedNode;
	if (typeof(node) != "undefined" && node != null) {
		return (node.nodeAppType == "O");
	}
}

//check whether INSPIRE terms are in thesaurusInspireTermsList (from nodeData)  
UtilUdk.isInspire = function(thesaurusInspireTermsList) {
    if (dojo.some(thesaurusInspireTermsList, function(iTermKey) {
       return (iTermKey != 99999); })) {
	   return true;
    }	
	return false;
}

//returns "1", "2" ...
UtilUdk.getCurrentObjectClass = function() {
	var objectClassWidget = dijit.byId("objectClass");
	if (typeof(objectClassWidget) == "undefined") {
		return 0;
	}

	return objectClassWidget.getValue().substring(5);
}

// returns "Class1", "Class2" ...
UtilUdk.getObjectClass = function() {	
	var objectClassWidget = dijit.byId("objectClass");
	if (typeof(objectClassWidget) == "undefined") {
		return 0;
	}

	return objectClassWidget.getValue();
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
	// Use current locale as target language (e.g. maybe switched to english)
	// Use catalog language as default language (e.g. current locale is not de/en, maybe different one from browser)
	var helpLanguage = userLocale;
	var defaultLanguage = "en";
    if (catalogData && catalogData.languageShort) {
        defaultLanguage = catalogData.languageShort;
    }
	HelpService.getHelpEntry(guiId, cls, helpLanguage, defaultLanguage, {
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
			list[i].icon = "<div class='TreeIcon TreeIconClass"+list[i].objectClass+"' ></div>";
		} else if (typeof(list[i].addressClass) != "undefined") {
			switch (list[i].addressClass) {
				case 0:	// Institution
//					list[i].icon = "<img src='img/UDK/addr_institution.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
					list[i].icon = "<div class='TreeIcon TreeIconInstitution' ></div>";
					break;
				case 1:	// Unit
//					list[i].icon = "<img src='img/UDK/addr_unit.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
					list[i].icon = "<div class='TreeIcon TreeIconInstitutionUnit' ></div>";
					break;
				case 2:	// Person
//					list[i].icon = "<img src='img/UDK/addr_person.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
					list[i].icon = "<div class='TreeIcon TreeIconInstitutionPerson' ></div>";
					break;
				case 3:	// Free
//					list[i].icon = "<img src='img/UDK/addr_free.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
					list[i].icon = "<div class='TreeIcon TreeIconPersonAddress' ></div>";
					break;
				default:
//					list[i].icon = "<img src='img/UDK/addr_institution.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
					list[i].icon = "<div class='TreeIcon TreeIconInstitution' ></div>";
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
// showPermission changes the style of the link if there is no write permission
UtilList.addObjectLinkLabels = function(list, isDirectLink, showPermission) {
    var addJump = "";
    var disabledClass;
	for (var i = 0; i < list.length; ++i) {
	    // jump to specific element
	    isDirectLink && list[i].relationType == 3600 ? addJump = ", \"uiElementN003\"" : "";
	    !isDirectLink && list[i].relationType == 3600 ? addJump = ", \"uiElement3345\"" : "";
	    
	    disabledClass = "";
	    if (showPermission) {
	        if (!list[i].writePermission)
	            disabledClass = " disabled";
	    } 
	    
		if (list[i].pubOnly) {
			list[i].linkLabel = "<a class='pubOnly"+disabledClass+"' href='#' onclick='menuEventHandler.handleSelectNodeInTree(\""+list[i].uuid+"\", \"O\""+addJump+");'"+
		                    "title='"+list[i].title+"'>"+list[i].title+"</a>";			
		} else {
			list[i].linkLabel = "<a class='"+disabledClass+"' href='#' onclick='menuEventHandler.handleSelectNodeInTree(\""+list[i].uuid+"\", \"O\""+addJump+");'"+
		                    "title='"+list[i].title+"'>"+list[i].title+"</a>";
		}
	}
	return list;
}

// Iterates over an array of addresses and adds a property 'linkLabel' depending on the 'uuid' and 'title' of the address.
// linkLabel is a html href to directly jump to a given address in the main tree
// showPermission changes the style of the link if there is no write permission
UtilList.addAddressLinkLabels = function(list, showPermission) {
    var disabledClass;
	for (var i = 0; i < list.length; ++i) {
	    disabledClass = "";
        if (showPermission) {
            if (!list[i].writePermission)
                disabledClass = " disabled";
        } 
		list[i].linkLabel = "<a class='"+disabledClass+"' href='#' onclick='javascript:menuEventHandler.handleSelectNodeInTree(\""+list[i].uuid+"\", \"A\");'"+
		                    "title='"+list[i].title+"'>"+list[i].title+"</a>";
	}
	return list;
}


// Iterates over an array of urls and adds a property 'linkLabel' depending on the 'url' and 'name' of the url.
// linkLabel is a html href to the url target
UtilList.addUrlLinkLabels = function(list) {
	for (var i = 0; i < list.length; ++i) {
		if (list[i].url) {
			if (list[i].url.indexOf("http://") === 0)
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
			//list[i].displayDate = dojo.date.format(list[i].date, {formatLength:"short", datePattern:"dd.MM.yyyy", timePattern:"HH:mm"});
			list[i].displayDate = dojo.date.locale.format(list[i].date, {selector: "date", datePattern: "dd.MM.yyyy", timePattern:"HH:mm"});
		}
		return list;
	} else {
		return [];
	}
}


// Creates table data from a list of values.
// ["a", "b", "c"] -> [{identifier: "a"}, {identifier: "b"}, {identifier: "c"}]
// @param list, contains strings to be mapped
// @param identifier, the property of the object to store the arrayfield in
// @param ignoreEmptyValues, if true then empty values will be filtered
UtilList.listToTableData = function(list, identifier, /*boolean*/ignoreEmptyValues) {
	var resultList = [];
	if (typeof(identifier) == "undefined")
		identifier = "title";

	dojo.forEach(list, function(item){
	    // if we want to map all values OR we ignore empty values and only add
	    // those who actually have a value in it
	    if (!ignoreEmptyValues || (item && dojo.trim(item).length > 0)) { 
    		var x = {};
    		x[identifier] = item;
    		resultList.push(x);
	    }
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

	// when converted back to a JSON object a list becomes a single value
	// if the list just contains one value
	if (dojo.isArray(tableData)) {
		for (var i = 0; i < tableData.length; ++i) {
			resultList.push(tableData[i][identifier]);
		}
	} else {
		resultList.push(tableData[identifier]);
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
		dojo.forEach(list, function(entry){
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
		dojo.forEach(list, function(entry) {
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
		dojo.forEach(list, function(entry) {
			if (entry.locationExpiredAt != null) {
				entry.label = "<span style='color:#FF0000'>" + entry.label + "</span>";
			}
		});
		return list;

	} else {
		return [];
	}
}

UtilList.getSelectDisplayValue = function(selectbox, value) {
	if (value == "") return value;
	var strValue = value+"";
	var dispValue = ""; 
	//var pos = Array.indexOf(selectbox.values, strValue); // not understood by any but Firefox
	//var pos = selectbox.values.indexOf(strValue); // not understood by IE
	
	// there are three possible ways how options and values are stored in select box
	if (selectbox.values) {
		var pos = dojo.indexOf(selectbox.values, strValue);
		if (pos == -1)
			dispValue = strValue;
		else
			dispValue = selectbox.options[pos];
	} else if (selectbox.options){
		dojo.some(selectbox.options, function(option) {
			if (option.value == strValue) {
				dispValue = option.label;
				return true;
			}
		});
	} else {
		return "???";
		dojo.some(selectbox.store._arrayOfTopLevelItems, function(item) {
			if (item[1] == strValue) {
				dispValue = item[0];
				return true;
			}
		});
	}
	return dispValue;
}

UtilList.getValueForDisplayValue = function(selectbox, displayValue) {
	if (displayValue == "") return displayValue;
	var strDisplayValue = displayValue+"";
	var foundValue = -1;
	dojo.some(selectbox.options, function(option) {
		if (option.label == strDisplayValue) {
			foundValue = option.value;
			return true;
		}
	});
	return foundValue;
}

UtilList.urlToListEntry = function(url) {
    var urlObject = {};
    urlObject.name = "preview-image";
    urlObject.relationType = 9000;
    urlObject.relationTypeName = "Bildvorschau";
    urlObject.url = url;
    urlObject.urlType = 1;
    return urlObject;
}

//see dojo.map in newer dojo releases
UtilList.map = function(list, myFunction) {
	var outArr = [];
	for(var i=0,l=list.length; i<l; ++i){
		outArr.push(myFunction.call(myFunction, list[i], i, list));
	}
	return outArr; // Array
}


// Util functions for dojo store 
var UtilStore = {}

UtilStore.getItemByAttribute = function(store, attr, value) {
	var def = new dojo.Deferred();
	
	var gotItems = function(items, request) {
	  dojo.forEach(items, function(item) {
		  if (item[attr] == value) {
			  def.callback(item);
			  return;
		  }
	  });
	};
	store.fetch({onComplete: gotItems});
	return def;
}

UtilStore.updateReadStore = function(id, data) {
	var updatedStore = new dojo.data.ItemFileReadStore({
		data: {	items: data }
	});
	dijit.byId(id).setStore(updatedStore);
	return updatedStore;
}

UtilStore.updateWriteStore = function(/*(name of) element that has store*/id, /*Array of items*/ data, /*StoreProperties?*/storeProps) {
	//var updatedStore = new dojo.data.ItemFileWriteStore({
	/*var updatedStore = new ingrid.data.CustomItemFileWriteStore({
		data: {	items: data },
		structure: dijit.byId(id).store.structure,
		minRows: dijit.byId(id).store.minRows,
		interactive: dijit.byId(id).store.isInteractive,
	});*/
	
	var idGrid = id;//+"Grid";
	
	// if it's a SlickGrid
	if (gridManager[idGrid]) {
		UtilGrid.setTableData(idGrid, data);
		//gridManager[idGrid].setData(data);
		//gridManager[idGrid].render();
	} else {
		var widget = dijit.byId(id);
		if (!storeProps)
			var storeProps = {label:"label"};
		
		//var updatedStore = id;
		storeProps.items = data;
		
		var updatedStore = new dojo.data.ItemFileWriteStore({data:storeProps});
		if (widget.setStore) {
		    widget.setStore(updatedStore, widget.query);
		} else {
		    widget.set("store", updatedStore);//, widget.query);
		}
	}
	return id;
/*
	if (!storeProps)
		var storeProps = {label:"label"};
	
	//var updatedStore = id;
	storeProps.items = data;
	var updatedStore = new dojo.data.ItemFileWriteStore({data:storeProps});
	
	var widget = id;
	if (typeof(id) == "string") {
		widget = dijit.byId(id);
		//if (!dijit.byId(id)) console.debug("id does not exist: " + id);
		//updatedStore = dijit.byId(id).store;
		var canEdit = widget.get("_canEdit");
		widget.setStore(updatedStore, widget.query);
		if (canEdit != undefined)
			widget.set("_canEdit", canEdit);
	}
*/
	/*
		
	if (updatedStore == null) {
		console.debug("updatedStore is null!!! id: " + id);
		updatedStore = new dojo.data.ItemFileWriteStore({});
	}
	// if it's writable
	if (updatedStore.save)
		updatedStore.save();
	
	updatedStore.data = storeProps;
	updatedStore.data.items = data;
	updatedStore.close();
		
	if (typeof(id) == "string")
		dijit.byId(id).setStore(updatedStore, dijit.byId(id).query);
	else
		updatedStore.fetch();
*/
	// resize grid to make use of autoHeight!
	//dijit.byId(id).resize();
	
//	return updatedStore;
}

UtilStore.convertItemsToJS = function(store, /*Array*/items) {
	if (items == undefined)
		items = store._arrayOfTopLevelItems;
	var convertedItems = [];
	dojo.forEach(items, function(item) {
		convertedItems.push(itemToJS(store, item));
	});
	return convertedItems;
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
			console.debug("Error: "+mes);
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
			console.debug("Error: "+msg);
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
			console.debug("Error: "+msg);
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
	if (currentUserPermissions == null || typeof(currentUserPermissions) == "undefined") { return false; }

	for (var i = 0; i < currentUserPermissions.length; ++i) {
		if (currentUserPermissions[i].permission == "CREATE_ROOT") {
			return true;
		}
	}
	return false;
}

UtilSecurity.isCurrentUserQA = function() {
	if (currentUserPermissions == null || typeof(currentUserPermissions) == "undefined") { return false; }

	for (var i = 0; i < currentUserPermissions.length; ++i) {
		if (currentUserPermissions[i].permission == "QUALITY_ASSURANCE") {
			return true;
		}
	}
	return false;
}

UtilSecurity.getUsersFromCurrentGroupsWithRootPermission = function() {
	var def = new dojo.Deferred();
	var getUsersFromGroupDefList = [];
	for (var i=0; i<currentGroups.length; i++) {
		var hasRootPermission = false;
		for (var j=0; j<currentGroups[i].groupPermissions.length; j++) {
			if (currentGroups[i].groupPermissions[j] == "CREATE_ROOT") {
				hasRootPermission = true;
				break;
			}
		}
		if (hasRootPermission) {
			getUsersFromGroupDefList.push(UtilSecurity.getUsersFromGroup(currentGroups[i].name));
		}
	}
	UtilDWR.enterLoadingState();
	var defList = new dojo.DeferredList(getUsersFromGroupDefList, false, false, true);
	defList.addErrback( function(errMsg, err) { 
		UtilDWR.exitLoadingState();
		console.debug("Error while calling getUserOfGroup: " + errMsg);
		def.errback(err); 
		} 
	);
	defList.addCallback(function(resultList) {
		UtilDWR.exitLoadingState();
		var userResultList = new Array();
		for (var i = 0; i < resultList.length; ++i) {
			for (var j=0; j<resultList[i][1].length; j++) {
				userResultList.push(resultList[i][1][j]);
			}
		}
		def.callback(userResultList);
	});

	return def;
}

UtilSecurity.getUsersFromGroup = function( groupName) {
	var def = new dojo.Deferred();

	SecurityService.getUsersOfGroup(groupName, {
		callback: function(userList) { def.callback(userList); },
		errback: function(errMsg, err) { 
			console.debug("Error while calling getUserOfGroup: " + errMsg);
			def.errback(err); 
		}
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

UtilSecurity.getAllGroups = function(includeCatAdminGroup){
    var deferred = new dojo.Deferred();
    
    SecurityService.getGroups(includeCatAdminGroup, {
        preHook: showLoadingZone,
        postHook: hideLoadingZone,
        callback: function(groupList){
            deferred.callback(groupList);
        },
        errorHandler: function(errMsg, err){
            hideLoadingZone();
            displayErrorMessage(err);
            console.debug(errMsg);
            deferred.errback(err);
        }
    });
    
    return deferred;
}

// Language related utility functions
var UtilLanguage = {}

UtilLanguage.getCurrentLanguage = function() {
	//return djConfig.locale;
	return dojo.config.locale;
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

/*
 * This function converts the long date format created and used by dojo
 * to a shorter and readable format often used in form elements
 */
UtilString.convertDate = function(dateLong, rowIdx, cell) {
	var result = "";
	if (dateLong != null && dateLong != "")
		result = UtilString.getDateString(dateLong, "dd.MM.yyyy");
	else
		cell.customClasses.push("importantBackground");
	return result;
}

UtilString.getDateString = function(dateLong, pattern) {
	if (dateLong == undefined) dateLong = new Date();
	return dojo.date.locale.format(dateLong, {selector: "date", datePattern: pattern});
}

UtilString.emptyIfNull = function(value){
	if (!value || value == null)
		return "";
	else
		return value;
}

//check passed string: returns false if undefined or null or length==0 
UtilString.hasValue = function(stringValue) {
	return UtilGeneral.hasValue(stringValue);
}

// value starting from 0..n
UtilString._getCapabilitiesType = function(value) {
    // null, Suchdienste, Darstellungsdienste, Download, Sonstige, null, Sonstige
    var types = [null, "CSW", "WMS", "WFS", "WCTS", null, "WCS"];
    return types[value];
}

UtilString.addCapabilitiesParameter = function(type, connUrl, paramServiceType) {
    var mappedType = this._getCapabilitiesType(type);
    if (!mappedType) console.error("Not supported Service Type: " + type);
    
    if (connUrl.toLowerCase().indexOf("request=getcapabilities") == -1) {
        if (connUrl.indexOf("?") == -1) {
            connUrl = connUrl + "?";
        }
        if (!(connUrl.lastIndexOf("?") == connUrl.length - 1)
                && !(connUrl.lastIndexOf("&") == connUrl.length - 1)) {
            connUrl = connUrl + "&";
        }
        
        connUrl += "REQUEST=GetCapabilities";
    }
    if (connUrl.toLowerCase().indexOf("service=") == -1) {
        if (paramServiceType) {
            connUrl += "&" + paramServiceType;
        } else {
            connUrl += "&SERVICE=" + mappedType;            
        }
    }
    return connUrl;
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

// click info has to be resetted after using it (for datagrids)
UtilUI.resetSelectedData = function() {
	selGrid = null;
	selRowNode = null;
}

UtilUI.setMandatory = function(/*html node to process*/containerNode) {
	if (containerNode) {
		UtilUI.removeVisibilityClasses(containerNode);
		dojo.addClass(containerNode, "required");
		dojo.addClass(containerNode, "show");
		// make sure tab containers and alike are displayed correctly!
		//igeEvents.refreshTabContainers();
	}
}
UtilUI.setOptional = function(/*html node to process*/containerNode) {
	if (containerNode) {
		UtilUI.removeVisibilityClasses(containerNode);
		dojo.addClass(containerNode, "optional");
	}
}
UtilUI.setShow = function(/*html node to process*/containerNode) {
	if (containerNode) {
		UtilUI.removeVisibilityClasses(containerNode);
		dojo.addClass(containerNode, "show");
	}
}
UtilUI.setHide = function(/*html node to process*/containerNode) {
	if (containerNode) {
		UtilUI.removeVisibilityClasses(containerNode);
		dojo.addClass(containerNode, "hide");
	}
}
UtilUI.removeVisibilityClasses = function(/*html node to process*/containerNode) {
	if (containerNode) {
		dojo.removeClass(containerNode, "required");
		dojo.removeClass(containerNode, "optional");
		dojo.removeClass(containerNode, "show");
		dojo.removeClass(containerNode, "hide");
	}
}

UtilUI.getSectionElement = function(node) {
    return UtilDOM.findParentNodeWithClass(node, "rubric");
}

UtilUI.toggleFunctionalLink = function(linkTab, event) {
	var selectedTab = event.id;//dijit.byId(event.currentTarget.id).selectedChildWidget.id;
	if (selectedTab == linkTab)
		dojo.style(linkTab+"Header", "display", "block");
	else
		dojo.style(linkTab+"Header", "display", "none");
	
}

UtilUI.setWidgetStateError = function(widget, /*Boolean*/error) {
	// first set the widget correctly for false validation
	if (error) {
		widget.required = true;
		// then this
		widget.state = "Error";
		widget._setStateClass();
		// or
		//widget._maskValidSubsetError = false;
		//widget.validate(false);
	}
	else {
		// to reset use the following
		widget.required = false;
		widget.validate(false);
	}
}

//get current mode of section element, to be passed to toggleFields for refresh !
UtilUI.getCurrentExpandModeOfSectionElement = function(sectionElement) {
    if (dojo.hasClass(sectionElement, "expanded")) {
      return "showAll";
    }
	return "showRequired";
}

UtilUI.disableHtmlLink = function(elementId) {
    var element = dojo.byId(elementId);
    
    if (element.onClick) {
        element._disabledOnClick = element.onClick;
        element.onClick = null;

    } else if (element.onclick) {
        element._disabledOnClick = element.onclick;
        element.onclick = null;
    }
    // add style for disabled look
    dojo.addClass(element.parentNode, "disabled");
}

UtilUI.enableHtmlLink = function(elementId) {
    var element = dojo.byId(elementId);
    
    if (element._disabledOnClick) {
        element.onclick = element._disabledOnClick;
        element._disabledOnClick = null;
    }
    // add style for disabled look
    dojo.removeClass(element.parentNode, "disabled");
}

UtilUI.setVisibleBlockDiv = function(visible) {
	var div = "loadBlockDiv";
	if (div != undefined)
		dojo.style(div, "display", visible ? "block" : "none");
}

UtilUI.updateBlockerDivInfo = function(id) {
    var waitInfo = dojo.byId("waitInfo");

    if ( !id || !waitInfo.data[id] ) return;
    waitInfo.data[id].current++;
    dojo.byId("waitInfo_"+id).innerHTML = dojo.string.substitute(waitInfo.data[id].text, [waitInfo.data[id].current, waitInfo.data[id].max]);
    
    // if maximum is reached, remove html div and data element
    if (waitInfo.data[id].current == waitInfo.data[id].max) {
        dojo.destroy( "waitInfo_" + id );
        delete waitInfo.data[id];
    }
    
    // if no elements are present, hide info
    var keys = [];
    for(var k in waitInfo.data) keys.push(k);
    if ( keys.length === 0 ) {
        dojo.style("waitInfo", "display", "none");
        UtilDWR.exitLoadingState();
    } else {
        dojo.style("waitInfo", "display", "table");
        UtilDWR.enterLoadingState();
    }
}

UtilUI.initBlockerDivInfo = function(/*STRING*/id, /*INTEGER*/max, /*STRING*/text, /*INTEGER*/current) {
    var waitInfo = dojo.byId("waitInfo");
    var curr = current ? current : 0;
    if (!waitInfo.data) waitInfo.data = {};
    waitInfo.data[id] = { max: max, current: curr, text: text };
    dojo.style("waitInfo", "display", "table");
    var newText = dojo.string.substitute(waitInfo.data[id].text, [waitInfo.data[id].current, waitInfo.data[id].max]);
    // remove div if the id already exists
    dojo.destroy( "waitInfo_" + id );
    dojo.create( "div", { id: "waitInfo_"+id, innerHTML: newText }, "waitInfo" );
}


UtilUI.showNextError = function(id, message) {
    var mightBeHidden = dojo.query(".rubric:not(.expanded) #"+id);
    if (mightBeHidden.length > 0) {
        dojo.addClass(UtilUI.getSectionElement(mightBeHidden[0]), "expanded");
    }
    var pos = dojo.position(id);
    // leave some space after the element to scroll to so that we can show tooltip
    pos.h += 50;
    dojo.window.scrollIntoView(id, pos);
    if (message)
        showToolTip(id, message);
    else {
        message = dijit.byId(id).invalidMessage;
        if (message) showToolTip(id, message);
    }
        
}

UtilUI.changeLabel = function(uiElementId, text) {
    var label = dojo.query("#"+uiElementId+" label")[0];
    if (label) {
        label.innerHTML = text + '<span class="requiredSign">*</span>';
    } else {
        console.debug("ERROR: Label in container '" + uiElementId + "' could not be found!");
    }
}

UtilUI.showNoModifiableTable = function() {
    dialog.show(message.get("dialog.general.info"), message.get("dialog.cannot.modify.table"), dialog.INFO);
}

UtilUI.enableElement = function(element) {
    // convert id to dijit if necessary
    if (!(element instanceof Object)) {
        element = dijit.byId(element);
    }
    element.set("disabled", false);
}

UtilUI.disableElement = function(element) {
    // convert id to dijit if necessary
    if (!(element instanceof Object)) {
        element = dijit.byId(element);
    }
    element.set("disabled", true);
}

UtilUI.setComboBySyslistValue = function(boxId, entryId) {
    var box = dijit.byId(boxId);
    box.store.fetch({
        onComplete: function(data) {
            dojo.some(data, function(item) {
                if (item[1] == entryId) {
                    box.set('value', item[0]);
                    return true;
                }
            });
        }
    });
}

//function to add a new entry to the conformity table if not already there
UtilUI.updateEntryToConformityTable = function(entryId, deleteEntry) {
    if (deleteEntry) {
        // remove all automatically added entries
        var itemIndexes = [];
        var entry = UtilSyslist.getSyslistEntryName(6005, entryId);
        dojo.forEach(UtilGrid.getTableData("extraInfoConformityTable"), function(row, index) { if (row.specification == entry) itemIndexes.push(index); });
        UtilGrid.removeTableDataRow("extraInfoConformityTable", itemIndexes);

    } else {
        // get name of codelist entry for entry-id "2"
        var entry = UtilSyslist.getSyslistEntryName(6005, entryId);
        
        // check if entry already exists in table
        var exists = dojo.some(UtilGrid.getTableData("extraInfoConformityTable"), function(row) { return row.specification === entry; });
        
        // add entry to table if it doesn't already exist
        if (!exists)
            UtilGrid.addTableDataRow("extraInfoConformityTable", {specification: entry});
        
    }
}

// General utility functions for converting strings, etc.
var UtilGeneral = {}

// Returns the stack trace of an exception as string
UtilGeneral.getStackTrace = function(exception) {
    if (exception.cause) {
        exception = exception.cause;
    }
	var stackTrace = "";
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

UtilGeneral.sessionValid = function() {
	// check periodically if session is still alive
	UtilityService.sessionValid({
		callback: function(valid){
			console.debug("session valid: " + valid);
			if (!valid) {
				UtilGeneral.showSessionTimoutMessage();
			} else {
				// check at a later expected timeout time again
				setTimeout("UtilGeneral.sessionValid()", (sessionTimeoutInterval+10)*1000);
			}
			if (dojo.cookie("JSESSIONID") != initialJSessionId) {
				console.debug("Session ID has changed! Everything still okay?");
				sessionExpired = true;
			}
		},
		errorHandler: function(err){
			console.debug("error: " + err);
			if (dojo.cookie("JSESSIONID") != initialJSessionId) {
				console.debug("Session ID has changed! Everything still okay?");
				sessionExpired = true;
			}
			// an error occured which might be because of a session problem
			// check again in a minute
			UtilGeneral.showSessionTimoutMessage();
			//setTimeout("UtilGeneral.sessionValid()", 60*1000);
		}
	});
}

UtilGeneral.showSessionTimoutMessage = function() {
	var dlg = dialog.show(message.get("dialog.sessionTimeoutErrorTitle"), message.get("dialog.sessionTimeoutError"), dialog.INFO, [
		{ caption: message.get("general.ok"),  action: function() { sessionExpired = true; document.location.href = "session_expired.jsp"; } }
	]);
	dojo.connect(dlg, "onHide", function(){
		sessionExpired = true;
		document.location.href = "session_expired.jsp";
	});
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

UtilGeneral.generateUUID = function() {
    var def = new dojo.Deferred();

    UtilityService.getRandomUUID( {
        callback: function(res) {
        	def.callback(res);
    }});

    return def;
}

UtilGeneral.askUserAndInvokeOrCancel = function(text, invocation){
    dialog.show(message.get('general.hint'), text, dialog.INFO, [{
        caption: message.get('general.no'),
        action: function(){;}
    }, {
        caption: message.get('general.yes'),
        action: function(){
            invocation.call();
        }
    }]);
}

UtilGeneral.getNumberFromDijit = function(id) {
    var value = dijit.byId(id).get("value");
    return isNaN(value) ? null : value;
}

//check passed value: returns false if undefined or null or "" or "    " or NaN;
UtilGeneral.hasValue = function(val) {
    if (typeof val == "undefined") {
        return false; 
    } else if (val == null) {
        return false; 
    } else if (dojo.trim(val + "") == "") {
        return false;
    } else if ((val + "") == (Number.NaN + "")) {
        return false; 
    }

    return true;
}

var itemToJS = function(store, item) {
    // summary: Function to convert an item into a simple JS object.
    // store:
    //    The datastore the item came from.
    // item:
    //    The item in question.
	//if (item == undefined)
	//	item = store._arrayOfTopLevelItems;
	
    var js = {};
    if (item && store) {
        //Determine the attributes we need to process.
        var attributes = store.getAttributes(item);
        if (attributes && attributes.length > 0) {
            var i;
            for (i = 0; i < attributes.length; i++) {
                var values = store.getValues(item, attributes[i]);
                if (values) {
					//Handle multivalued and single-valued attributes.
                    if (values.length > 1) {
                        var j;
                        js[attributes[i]] = [];
                        for (j = 0; j < values.length; j++) {
                            var value = values[j];
                            //Check that the value isn't another item. If it is, process it as an item.
                            if (store.isItem(value)) {
                                js[attributes[i]].push(itemToJS(store, value));
                            }
                            else {
                                js[attributes[i]].push(value);
                            }
                        }
                    }
                    else {
                        if (store.isItem(values[0])) {
                            js[attributes[i]] = itemToJS(store, values[0]);
                        }
                        else {
                            js[attributes[i]] = values[0];
                        }
                    }
							
                }
            }
        }
    }
    return js;
};

// Utility functions for trees
var UtilTree = {}

UtilTree.getSubTree = function(item, prefixIndex) {
	var deferred = new dojo.Deferred();
	var id = null;
	var type = null;
	
	// if node is root get the object and address node
	if (!item.root) {
		id = item.id[0].substring(prefixIndex);
		type = item.nodeAppType[0];
	}
    
    TreeService.getSubTree(id, type, userLocale, {
		//preHook: UtilDWR.enterLoadingState,
		//postHook: UtilDWR.exitLoadingState,
        callback: function(res){
			deferred.callback(res);
        },
        //			timeout:10000,
        errorHandler: function(message){
        	//UtilDWR.exitLoadingState();
            deferred.errback(message);
        }
    });
	return deferred;
}

UtilTree.selectNode = function(treeId, nodeId) {
	// remove any selection first
	// if it was already deleted it might still be in store but selection cannot
	// be resetted!
	var tree = dijit.byId(treeId);
	tree._selectNode(dijit.byId(nodeId));
	/*var curSelNode = tree.selectedNode;
	if (curSelNode && curSelNode.selected)
		curSelNode.setSelected(false);
	// set new selection of node
	var newSelectedNode = dijit.byId(nodeId);
	newSelectedNode.setSelected(true);
	tree.selectedNode = newSelectedNode;*/
}

UtilTree.reloadNode = function(treeId, node) {
    var d = new dojo.Deferred();
    d.addCallback(function() {
        UtilTree.selectNode(treeId, node.id[0]);
        dojo.window.scrollIntoView(dijit.byId(node.id[0]).domNode);
    });
    d.addErrback(function(msg) {
        dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
        console.debug(msg);
    });
    udkDataProxy.resetDirtyFlag();
    console.debug("Publishing event: /loadRequest(" + node.id + ", "
            + node.item.nodeAppType + ")");
    dojo.publish("/loadRequest", [{
        id : node.id[0],
        appType : node.item.nodeAppType[0],
        node: node.item,
        resultHandler : d
    }]);
}

/**
 * Delete a tree node with all its sub nodes
 */
UtilTree.deleteNode = function(treeId, node) {
	var tree = dijit.byId(treeId);
	// first delete all the nodes' children
	dojo.forEach(node.getChildren(), function(child) {
		if (child.getChildren().length > 0) {
			UtilTree.deleteNode(treeId, child);
		}
		tree.model.store.deleteItem(child.item);
	});
	// finally delete the node itself
	tree.model.store.deleteItem(node.item);
}

/**
 * Refresh children of a node. If no node is given then a currently selected
 * node is used.
 */
UtilTree.refreshChildren = function(treeId, node) {
    var tree = dijit.byId("dataTree");
    var selectedNode = node ? node : tree.selectedNode;
    tree.refreshChildren(selectedNode);
}

// Utility functions for DOM
var UtilDOM = {}

// get all attributes of a dom node as a hashmap
UtilDOM.getHTMLAttributes = function(item) {
	var parameter = {};
	var domItem = dojo.byId(item);
	if (domItem != null) {
		for (i = 0; i < domItem.attributes.length; i++) {
			parameter[this._replaceAttribute(domItem.attributes[i].nodeName)] = domItem.attributes[i].nodeValue;
		}
	}
	return parameter;
}

// In XHTML all attributes should be lower case, however Dojo needs some attributes
// which use upper case letters to be identified. So we need to remap them. 
UtilDOM._replaceAttribute = function(attribute){
	var lcAttr = attribute.toLowerCase();
	if (attributeReplacerMap[lcAttr])
		return attributeReplacerMap[lcAttr];
	return attribute;
}

// Search for a parent node with the uuid and check if
// it has the active selected node as a child
UtilDOM.activeNodeHasParent = function(parentUuid){
    if (!currentUdk) return false;
    
    var activeDomNode = dojo.byId(currentUdk.uuid).parentNode;
    while (activeDomNode) {
        if (activeDomNode.id == parentUuid) return true;
        activeDomNode = activeDomNode.parentNode;
    }
    return false;
}

UtilDOM.findParentNodeWithClass = function(node, clazz) {
    if (!node) return null;
    if (dojo.hasClass(node, clazz))
        return node;
    else
        return UtilDOM.findParentNodeWithClass(node.parentNode, clazz);
}

// Utility functions for DOM
var UtilSyslist = {}

// we have to clone the data, so that modification to the returned
// value won't change the original data
// the valuePrefix is used to add a prefix to each value of the syslist
// this is used for the object class select box for compatibility reason
UtilSyslist.getSyslistEntry = function(id, valuePrefix) {
    var def = new dojo.Deferred();
    if (sysLists[id].length == 0)
        console.debug("empty syslist: " + id);
    var syslist = dojo.clone(sysLists[id])
    // add a prefix to each value
    if (valuePrefix) {
        dojo.forEach(syslist, function(item) {
            item[1] = valuePrefix + item[1];
        });
    }
    def.callback(syslist);
    return def;
}

UtilSyslist.getSyslistEntryName = function(syslist, value) {
	if (value == undefined) return value;
	
	var result = value+"";
	
	var list = sysLists[syslist];
	if (list != undefined) {
		dojo.some(list, function(item) {
			if (item[1] == result) {
				result = item[0];
				return true;
			}
		});
	} else {
		displayErrorMessage("SyslistID: " +  syslist + " not loaded!");
		return null;
	}
	return result;
}

UtilSyslist.getSyslistEntryKey = function(syslist, value) {
	if (value == undefined) return value;
	
	var result = value+"";
	
	var list = sysLists[syslist];
	if (list != undefined) {
		dojo.some(list, function(item) {
			if (item[0] == result) {
				result = item[1];
				return true;
			}
		});
	} else {
		displayErrorMessage("SyslistID: " +  syslist + " not loaded!");
		return null;
	}
	return result;
}

UtilSyslist.getSyslistEntryData = function(syslist, value) {
    if (value == undefined) return null;
    
    var result = null;
    var valueStr = value+"";
    
    var list = sysLists[syslist];
    if (list != undefined) {
        dojo.some(list, function(item) {
            if (item[0] == valueStr) {
                result = item[3];
                return true;
            }
        });
    } else {
        displayErrorMessage("SyslistID: " +  syslist + " not loaded!");
        return null;
    }
    return result;
}

UtilSyslist.readSysListData = function(listId) {
    var def = new dojo.Deferred();

    var languageCode = UtilCatalog.getCatalogLanguage();
    CatalogService.getSysLists([listId], languageCode, {
        callback: function(res) {
            def.callback(res[listId]);
    }});

    return def;
}

// Input is a list of string arrays of the following form:
// [ [listEntry, entryId, isDefault], [...] ]
// e.g [ ["Inch", "4", "N"], ["Meter", "9001", "N"], ["Fuss", "9002", "N"], ["Kilometer", "9036", "N"] ]
// Output is [ {name:listItems[0][0], entryId: listItems[0][1], isDefault: listItems[0][2] == "Y"}, ...
UtilSyslist.convertSysListToTableData = function(listItems) {
    // Prepare the list items for display
    var listData = [];
    for (var index = 0; index < listItems.length; ++index) {
        listData.push({
            name: listItems[index][0],
            entryId: listItems[index][1],
            isDefault: listItems[index][2] == "Y",
            data: listItems[index][3]
        });
    }
    
    return listData;
}

// Utility functions for DataGrids
var UtilGrid = {}

UtilGrid.getTable = function(grid) {
	return gridManager[grid];
}

UtilGrid.getSelectedFromGrid = function(keywordTable) {
	var allSelected = keywordTable.selection.getSelected()
	return dojo.filter(allSelected, function(item) {return item._isEmptyRow != "true"});
}

// ALWAYS filters if not true argument passed (and if rowFilter is set on table) !
// So visible data is returned and frontend works the usual way with row indexes !
UtilGrid.getTableData = function(grid, doNOTFilter) {
	return gridManager[grid].getData(doNOTFilter);
}

UtilGrid.setTableData = function(gridId, data) {
	var grid = UtilGrid.getTable(gridId);
	// request UNFILTERED data ! Get full data store reference for check !
	var gridData = UtilGrid.getTableData(gridId, true);
	if (gridData instanceof Array) {
		grid.setData(data, true, true); // scroll to top AND do not resize!
		// just render
		grid.render();
	} else {
		gridData.setItems(data);
		grid.invalidate();
	}
	//grid.render();
	//grid.notifyChangedData({});
}

UtilGrid.addTableDataRow = function(grid, item) {
	// request UNFILTERED data ! Is added to full data store reference !
	var data = UtilGrid.getTableData(grid, true);
	if (data instanceof Array)
		data.push(item);
	else {
		var items = data.getItems();
		item._id = this.getUniqueId(items);
		items.push(item);
		data.refresh();
	}
	UtilGrid.getTable(grid).invalidate();
	UtilGrid.getTable(grid).notifyChangedData({type:"added", item:item});
}

UtilGrid.getUniqueId = function(items) {
	var idMax = 0;
	dojo.forEach(items, function(item) {
		if (item._id > idMax)
			idMax = item._id; 
	});
	return idMax+1;
}

UtilGrid.updateTableDataRow = function(grid, row, item) {
	// request FILTERED data so row indexes work !
    var data = UtilGrid.getTableData(grid);
	var theGrid = UtilGrid.getTable(grid);

	// Check row filter on Grid. If set then data is filtered data !
	if (theGrid.getRowFilter()) {
        // Filtered data ! This is not the grid store reference !!!
        // So we determine all items and update them afterwards in real grid store !
        if (data instanceof Array == false) {
            data = data.getItems();
        }

        UtilGrid._updateItemsInTable(grid, [ data[row] ], [ item ]);
	} else {
        // data is referenced store !
        data.splice(row, 1, item);
	}

	theGrid.invalidate();
	theGrid.notifyChangedData({});
}

// "private" method for replacing items with other items (full item data passed).
UtilGrid._updateItemsInTable = function(gridId, oldItems, newItems) {
    // Get UNFILTERED store -> store reference !
    var data = this.getTableData(gridId, true);
    
    var dataArray = data;
    if (dataArray instanceof Array == false) {
        dataArray = data.getItems();
        var refresh = true;
    }
    dojo.forEach(oldItems, function(oldItem, oldItemIndex) {
        dojo.some(dataArray, function(storeItem, storeItemIndex) {
            if (storeItem === oldItem) {
                console.debug("update item: " + storeItemIndex + " of: " + grid + " with new item: ");
                console.debug(newItems[oldItemIndex]);
                dataArray.splice(storeItemIndex, 1, newItems[oldItemIndex]);
            }
        });
    });

    if (refresh) {
        data.refresh();
    }
}

UtilGrid.updateTableDataRowAttr = function(grid, row, attr, value) {
	// request FILTERED data so indexes work !
	var data = UtilGrid.getTableData(grid);
    var theGrid = UtilGrid.getTable(grid);

    // Check row filter on Grid. If set then data is filtered data !
    if (theGrid.getRowFilter()) {
        // Filtered data ! This is not the grid store reference !!!
        // So we determine item and update it afterwards in real grid store !
        if (data instanceof Array == false) {
            data = data.getItems();
        }
        var itemToUpdate = [ data[row] ];
    
	    // Get UNFILTERED store -> store reference !
        var data = this.getTableData(gridId, true);
        if (data instanceof Array == false) {
            data = data.getItems();
        }
        dojo.some(data, function(storeItem) {
            if (storeItem === itemToUpdate) {
                console.debug("update item/value: " + storeItemIndex + "/" + attr + " of: " + grid + " with value: " + value);
                storeItem[attr] = value;
            }
        });

    } else {
        // data is referenced store !
        if (data instanceof Array)
            data[row][attr] = value;
        else 
            data.getItem(row)[attr] = value;
    }

	theGrid.updateRow(row);
	// we need invalidate so table renders new !!!
	theGrid.invalidate();
	theGrid.notifyChangedData({});
}

UtilGrid.getSelectedRowIndexes = function(grid) {
	return gridManager[grid].getSelectedRows();
}

UtilGrid.getSelectedData = function(grid) {
	var slickGrid = gridManager[grid];
	var data = [];
	var rows = slickGrid.getSelectedRows();
	dojo.forEach(rows, function(row) {
		data.push(slickGrid.getDataItem(row));
	});
	return data;
}

UtilGrid.clearSelection = function(grid) {
	this.getTable(grid).setSelectedRows([]);
}

UtilGrid.setSelection = function(grid, selection) {
	this.getTable(grid).setSelectedRows(selection);
}

UtilGrid.removeTableDataRow = function(grid, itemIndexes) {
    // we do not allow to delete from this table, because links only go: service -> data
    if (grid == "ref1ServiceLink") {
        UtilUI.showNoModifiableTable();
        return;
    }
    
	var table = this.getTable(grid);
	// request FILTERED data so indexes work !
	var data = this.getTableData(grid);
    if (data instanceof Array == false) {
        data = data.getItems();
        var refresh = true;
    }

    var deletedData = [];       
	// Check row filter on Grid. If set then data is filtered data !
	if (table.getRowFilter()) {
        // Filtered data ! This is not the grid store reference !!!
        // So we determine all items and delete them afterwards in real grid store !
        // get items to delete from filtered store !
        dojo.forEach(itemIndexes, function(rowIndex) {
            deletedData.push(data[rowIndex]);
            table.removeInvalidRow(rowIndex);
        });
    
        UtilGrid._removeItemsFromTable(grid, deletedData);
	} else {
        // data is referenced store !       
        var sortedIndexes = itemIndexes.sort();
        var decr = 0; // when removing an element the selected row moved up!
        dojo.forEach(sortedIndexes, function(rowNr) {
            deletedData.push(data.splice(rowNr-(decr++),1)[0]);
            table.removeInvalidRow(rowNr);
        });
        if (refresh) this.getTableData(grid).refresh();
	}

    this.clearSelection(grid);
    table.resetActiveCell();
    table.invalidate();
    table.resizeCanvas();

    gridManager[grid].notifyChangedData({type:"deleted", items:deletedData});
    gridManager[grid].onDeleteItems({items:deletedData, grid:grid});
	return deletedData;
}

// "private" method for deleting items in store (full item data passed).
UtilGrid._removeItemsFromTable = function(gridId, deletedItems) {
    // Get UNFILTERED store -> store reference !
    var data = this.getTableData(gridId, true);
    
    var dataArray = data;
    if (dataArray instanceof Array == false) {
        dataArray = data.getItems();
        var refresh = true;
    }
    dojo.forEach(deletedItems, function(deletedItem) {
        dojo.some(dataArray, function(item, i) {
            //if (dojo.every(fields, function(field) { return item[field] == deletedItem[field]; })) {
            if (item === deletedItem) {
                console.debug("remove item: " + i + " from: " + gridId);
                console.debug(item);
                dataArray.splice(i, 1);
            }
        });
    });

    if (refresh) {
    	data.refresh();
    }
}

UtilGrid.updateOption = function(grid, optionKey, optionValue) {
	var options = UtilGrid.getTable(grid).getOptions();
    options[optionKey] = optionValue;
    UtilGrid.getTable(grid).setOptions(options);
}

UtilGrid.generateIDs = function(data, id) {
	i = 0;
	dojo.forEach(data, function(d) {
		d[id] = i++;
	});
	return data;
}

UtilGrid.synchedDelete = function(checkGrids, msg){
    // do not do anything if this was the table, that deleted an item
    //if (msg.grid == this.eval("$container[0].id")) {return;}
    
    dojo.forEach(checkGrids, function(gridId) {
        UtilGrid._removeItemsFromTable(gridId, msg.items);

    	var grid = UtilGrid.getTable(gridId);
	    grid.invalidate();
	    grid.notifyChangedData({type:"deleted", items:msg.items});
    });
}

UtilGrid.addRowSelectionCallback = function(gridId, onSelectCallback, callbackData){
	if (!callbackData) {
		callbackData = {};
	}
    callbackData.gridId = gridId;

    dojo.connect(UtilGrid.getTable(gridId), "onSelectedRowsChanged", function(row) {
        var selRowsData = UtilGrid.getSelectedData(gridId);
        if (selRowsData.length == 1) {
            // react only if not an empty row was selected
            if (selRowsData[0] != null) {
                if (currentUdk.writePermission) {
                    callbackData.selectedRow = selRowsData[0];
                    onSelectCallback(callbackData);
                }
            }
        }
    });
}


var UtilThesaurus = {}

// parse a string and extract a list of terms.
// whitespace and newlines are used as delimeters, composite terms are enclosed in double quotes
// e.g the input:
// Wasser Umwelt
// "Inhalierbarer Feinstaub" Atomkraft
// results in:
// ["Wasser", "Umwelt", "Inhalierbarer Feinstaub", "Atomkraft"]
UtilThesaurus.parseQueryTerm = function(queryTerm) {
	var resultTerms = [];

	// Iterate over all characters in the query term
	// 1. If we read a newline, store the current term and continue
	// 2. If we read a whitespace and are not currently in a composite term (term enclosed in "),
	//    then store the current term and continue
	// 3. If we read double quote("), we have to check if we are at the start or end of a composite term
	//    If we are at the end, write the term and switch the flag
	//    Otherwise simply switch the flag
	// All other chars are handled as valid query chars
	// In the end we have to write the remainder from currentTerm since the query string doesn't always end
	// with a newline or whitespace

	// Helper function to only add valid terms (no empty strings) to the list 
	var addTermToResultList = function(term) {
		var trimmedTerm = dojo.trim(term);// "." are not transferred correctly when using regex! dojo.regexp.escapeString(term));
		if (trimmedTerm && trimmedTerm.length != 0) {
			resultTerms.push(trimmedTerm);
		}
	}

	var currentTerm = "";
	var readingCompositeTerm = false;
	for (var index = 0; index < queryTerm.length; index++) {
	    if (queryTerm.substr(index,1) == "\n" || (queryTerm.substr(index,1) == " " && !readingCompositeTerm)) {
			addTermToResultList(currentTerm);
	        currentTerm = "";
	    } else if (queryTerm.substr(index,1) == "\"") {
			if (readingCompositeTerm) {
				addTermToResultList(currentTerm);
		        currentTerm = "";
			}
			readingCompositeTerm = !readingCompositeTerm;
	    } else if (queryTerm.substr(index,1) == "," && !readingCompositeTerm) {
	    	// ignore comma if it's not within a phrase
	    } else {
	    	currentTerm += queryTerm.substr(index,1);
	    }
	}
	addTermToResultList(currentTerm);

	return resultTerms;
}

// Callback to find sns topics for a given topic (SNSService.findTopcis)
UtilThesaurus.findTopicsDef = function(term) {
	var def = new dojo.Deferred();
	SNSService.findTopics(term, userLocale, {
		callback: function(res) {
			UtilList.addSNSTopicLabels(res);
			UtilUI.updateBlockerDivInfo( "keywords" );
			def.callback(res);
		},
		errorHandler: function(errMsg, err) {
			console.debug("Error while calling findTopics: " + errMsg);
			def.errback(err);
		}
	});
	return def;
}

// Main function to analyze and add the found topic results to the store given in 'store'
UtilThesaurus.handleFindTopicsResult = function(termList, snsTopics, grid) {
	// termList is the list of input terms ('tokenized' user input)
	// snsTopics is a list of SNS findTopic results. The first entry in snsTopcis is the result for the
	// first term in termList and so on...

	// 1. Check if one of the input terms is an INSPIRE topic. If so, add it to the INSPIRE topic list.
	//    Show an info dialog with the INSPIRE topics that were added (TODO not implemented yet)
	// 2. Add all descriptors to the list of search terms. No dialog needed.
	// 3. If the descriptor is a synonym, add all non-descriptors to the list of search terms.
	//    Ask the user if the corresponding descriptor should be added to the list of search terms as well
	// 4. Add all other terms from termList that were not found (or are of type TOP_TERM / NODE_LABEL)

	var def = new dojo.Deferred();
	def.callback();
	console.debug("term list: "+termList);
	for (var index = 0; index < termList.length; ++index) {
		var queryTerm = termList[index];
		var curSnsTopics = snsTopics[index];

		console.debug("query term: " + queryTerm);

		if (curSnsTopics && curSnsTopics.length != 0) {
			// Try to find the queryTerm in the list of sns terms first
			var snsTopicsEqualToTerm = dojo.filter(curSnsTopics, function(t) { return t.title.toLowerCase() == queryTerm.toLowerCase(); });
			if (snsTopicsEqualToTerm.length != 0) {
				// Check if one of the found terms is a descriptor / nonDescriptor
				var snsDescriptorsEqualToTerm = dojo.filter(snsTopicsEqualToTerm, function(t) { return t.type == "DESCRIPTOR"; });
				var snsNonDescriptorsEqualToTerm = dojo.filter(snsTopicsEqualToTerm, function(t) { return t.type == "NON_DESCRIPTOR"; });

				if (snsDescriptorsEqualToTerm.length != 0) {
					// If the term was found as a descriptor, add all descriptors to the search term list 
					UtilThesaurus.addDescriptors(snsDescriptorsEqualToTerm, grid);

				} else if (snsNonDescriptorsEqualToTerm.length != 0) {
					// otherwise, if the term was found as a synonym
					// Add it to the list as a free term and ask the user if the corresponding descriptor(s)
					// should be added as well

					// create a closure with fixed arguments. Otherwise we end up adding only the last
					// nonDescriptor since snsNonDescriptorsEqualToTerm gets overwritten in every iteration
					// see test_delayedFunctions.jsp for more information
					(function(fixedDescs, fixedStore) {
						def.addCallback(function() { return UtilThesaurus.addNonDescriptorsDef(fixedDescs, fixedStore); });
					})(snsNonDescriptorsEqualToTerm, grid)
					
				} else {
					// Found topic is of type TOP_TERM or NODE_LABEL. Simply add it to the list as a free term
					UtilThesaurus.addFreeTerm(queryTerm, grid);
				}
				
				// add synonyms for inspire if available
				def.addCallback(function() { return UtilThesaurus.addInspireSynonyms(snsTopicsEqualToTerm); });
				//def.addCallback(function() { return addInspireSynonyms(curSnsTopics); });

			} else {
				// Results were returned by the SNS that did not contain the queryTerm
				// e.g. ('Inhalierbarer' results in 'Inhalierbarer Feinstaub' and 'PM10')
				// Add the search term as free term
				UtilThesaurus.addFreeTerm(queryTerm, grid);
			}

		} else {
			// No results were returned by the sns. Add the queryTerm as free term to the search term list
			UtilThesaurus.addFreeTerm(queryTerm, grid);
		}

	}
}

// if there's a term that's a synonym to an inspire topic then add it to the list in case the user
// acjnowledges it
UtilThesaurus.addInspireSynonyms = function(curSnsTopics) {
	//var inspireList; 
	//dojo.debug("SNS Topics length: " + curSnsTopics.length);
	var def = new dojo.Deferred();
	def.callback();
	dojo.forEach(curSnsTopics, function(snsTopic) {
		//dojo.debug("InspireList: " + snsTopic.inspireList.length); 
		if (snsTopic.inspireList.length > 0) {
			def.addCallback(function() {
				var closeDialogDef = new dojo.Deferred();
				var displayText = dojo.string.substitute(message.get("dialog.addInspireTopics.question"), [snsTopic.title, snsTopic.inspireList.join(",")]);
				dialog.show(message.get("dialog.addInspireTopics.title"), displayText, dialog.INFO, [
					{ caption: message.get("general.no"),  action: function() { closeDialogDef.callback(); return; } },
					{ caption: message.get("general.yes"), action: function() { closeDialogDef.callback(); UtilThesaurus.addInspireTopics(snsTopic.inspireList); return; } }
				]);
				return closeDialogDef;
			});
		}
	});
	return def;
}

//Add a list of non descriptors (synonyms) store if they don't already exist
// Ask the user (popup dialog) if the corresponding descriptors should be added as well
UtilThesaurus.addNonDescriptorsDef = function(nonDescriptors, grid) {
	var deferred = new dojo.Deferred();
	deferred.callback();
	//deferred.addErrback(handleFindTopicsError);

	dojo.forEach(nonDescriptors, function(d) {
		//console.debug("add non descriptor: " + snsTopicToString(d));
		UtilThesaurus.addFreeTerm(d.label, grid);

		deferred.addCallback(function() { return UtilThesaurus.getTopicsForTopicDef(d.topicId); });
		deferred.addCallback(function(topic) {
			var closeDialogDef = new dojo.Deferred();

			//console.debug("Add descriptor for synonym '" + snsTopicToString(d) + "' ? " + snsTopicToString(topic));
            //var descriptors = topic.descriptors;
            UtilList.addSNSTopicLabels( topic.descriptors );

			// Show the dialog
			var displayText = dojo.string.substitute(message.get("dialog.addDescriptors.message"), [d.label, dojo.map(topic.descriptors, function(item) {return item.title;})]);
//			var displayText = "Synonym: " + snsTopicToString(d) + " Deskriptor: " + snsTopicToString(topic);
			dialog.show(message.get("dialog.addDescriptor.title"), displayText, dialog.INFO, [
	            { caption: message.get("general.no"),  action: function() { closeDialogDef.callback(); return; } },
	        	{ caption: message.get("general.ok"), action: function() { closeDialogDef.callback(); UtilThesaurus.addDescriptors(topic.descriptors, grid); } }
			]);

			return closeDialogDef;
		});

	});
	return deferred;
}

// Add a 'free' term to store if it doesn't already exist
UtilThesaurus.addFreeTerm = function(queryTerm, grid) {
	console.debug("add free term: " + queryTerm);
	if (dojo.every(UtilGrid.getTableData(grid), function(item) { if (!item.title) return true; return item.title.toLowerCase() != queryTerm.toLowerCase(); })) {
		// If every term in store is != to the queryTerm, add it
		UtilGrid.addTableDataRow(grid, { label: queryTerm, title: queryTerm, source: "FREE", sourceString: "FREE"} );
	}
}

//Add a list of descriptors to store if they don't already exist
UtilThesaurus.addDescriptors = function(descriptors, grid) {
	dojo.forEach(descriptors, function(d) {
		//console.debug("add descriptor: " + snsTopicToString(d));
		if (dojo.every(UtilGrid.getTableData(grid), function(item) { return item.title != d.title; })) {
			// If every topicId in store is != to the topicId of the descriptor, add it
			UtilGrid.addTableDataRow(grid, d );
		}
	});
}
// !!!!!!!!!! use above one!
UtilThesaurus.addDescriptorsToGrid = function(descriptors, grid) {
	dojo.forEach(descriptors, function(d) {
		//console.debug("add descriptor: " + snsTopicToString(d));
		if (dojo.every(grid.getData(), function(item) { return item.title != d.title; })) {
			// If every topicId in store is != to the topicId of the descriptor, add it
			UtilGrid.addTableDataRow(grid, d);
		}
	});
}

//Callback to find an sns descriptor for a synonym (nonDescriptor)
UtilThesaurus.getTopicsForTopicDef = function(topicId) {
	var def = new dojo.Deferred();
	SNSService.getTopicsForTopic(topicId, userLocale, {
		preHook: UtilDWR.enterLoadingState,
		postHook: UtilDWR.exitLoadingState,
		callback: function(res) {
			UtilList.addSNSTopicLabels( [res] );
			def.callback(res);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug("Error while calling getTopicsForTopic: " + errMsg);
			def.errback(err);
		}
	});
	return def;
}

/* ---- Helper functions for the free terms search button ---- */ 

UtilThesaurus.getInspireTopicId = function(topic) {
	var id = null;
	dojo.some(sysLists[6100], function(list) { id = list[1]; return list[0] == topic;})
	return id;
}

// check if term is in Inspire Syslist
UtilThesaurus.isInspireTopic = function(topic) {
    var topicLow = topic.toLowerCase();
    var result = null;
	dojo.some(sysLists[6100], function(list) { if (list[0].toLowerCase() == topicLow) { result = list[0]; return true; } else return false; });
	return result;
}

//Add inspire topics to the inspire table if they don't already exist
// inspireTopics: Array of Strings
UtilThesaurus.addInspireTopics = function(inspireTopics) {
	try {
		dojo.forEach(inspireTopics, function(t) {
			var inspireEntryId = UtilThesaurus.getInspireTopicId(t);
			// add it only if there's a valid value (here are NO free entries!)
			if (inspireEntryId != null) {
				console.debug("adding entry: [" + inspireEntryId + ", " + t + "]");
				// if item does not already exist in table 
				if (dojo.every(UtilGrid.getTableData("thesaurusInspire"), function(item) { return item.title != inspireEntryId; })) {
					UtilGrid.addTableDataRow("thesaurusInspire", { title: inspireEntryId } );
				}
			} else {
				dojo.debug(t + " is not a valid INSPIRE topic!");
				var displayText = dojo.substitute(message.get("dialog.addInspireTopics.error"), [t]);
				dialog.show(message.get("dialog.addInspireTopics.title"), displayText, dialog.WARNING);
			}
		});
	} catch (error) {
		console.debug("error: "+ error);
		console.debugShallow(error);
	}
}

var UtilEvents = {};

UtilEvents.publishAndContinue = function(evt) {
	var params = { abort: false };
    // do some externally defined actions which can abort the closing of the dialog
    console.log("Publishing event: '" + evt + "' with parameter 'abort'");
    dojo.publish(evt, [params]);
    return !params.abort;
}