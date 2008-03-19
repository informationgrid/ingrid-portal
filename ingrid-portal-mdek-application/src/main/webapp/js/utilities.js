/**
 * Page Navigation obj
 *  The purpose of this object is to display a simple page navigation for a given result set.
 *  It is initialised with the number of displayed results per page and the info/paging span where
 *  the navigation should be displayed. 
 *  To display/update the nav bar, use setTotalNumHits to set the number of hits that should be navigated.
 *  When the user selects a page, the 'onPageSelected' function is called with the selected page number as argument.
 */
function PageNavigation(args) {
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
	
/*
		dojo.debug("totalNumHits: "+totalNumHits);
		dojo.debug("startHit: "+startHit);
		dojo.debug("endHit: "+endHit);
		dojo.debug("currentPage: "+currentPage);
		dojo.debug("lastPage: "+lastPage);
*/

		// Update the dom elements
		if (totalNumHits == 0) {
			this.infoSpan.innerHTML = "0 Treffer";
		} else if (totalNumHits == 1) {
			this.infoSpan.innerHTML = ""+(startHit+1)+"-"+(endHit+1)+" von "+totalNumHits+" Treffer";
		} else {
			this.infoSpan.innerHTML = ""+(startHit+1)+"-"+(endHit+1)+" von "+totalNumHits+" Treffern";
		}
	
		// Clear the paging span
		while (this.pagingSpan.hasChildNodes())
			this.pagingSpan.removeChild(this.pagingSpan.firstChild);
	
		// Add info text to the paging span
		if (totalNumHits == 0) {
			this.pagingSpan.appendChild(document.createTextNode("(Seite 1 von 1)"));
		} else {
			this.pagingSpan.appendChild(document.createTextNode("(Seite "+currentPage+" von "+lastPage+")"));
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
			link.setAttribute("title", "Erste Seite");
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
			link.setAttribute("title", "zurueck");
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
				link.setAttribute("title", "Seite "+i);
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
			link.setAttribute("title", "weiter");
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
			link.setAttribute("title", "Letzte Seite");
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

// Util functions for handling MdekAddressBeans 
var UtilAddress = {}

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
	if (title == null)
		return message.get("tree.newAddressName");
	else
		return dojo.string.trim(title);
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
UtilList.addObjectLinkLabels = function(list) {
	for (var i = 0; i < list.length; ++i) {
		list[i].linkLabel = "<a href='javascript:menuEventHandler.handleSelectNodeInTree(\""+list[i].uuid+"\", \"O\");'"+
		                    "title='"+list[i].title+"'>"+list[i].title+"</a>";
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
