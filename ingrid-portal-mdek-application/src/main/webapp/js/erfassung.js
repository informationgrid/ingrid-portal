function sizeContent() {
	// adjust height of right bottom content within split pane
	var splitPaneDiv = dojo.byId('contentSection');
	var sectionTopDivName = "sectionTopObject";
	if (currentUdk && currentUdk.nodeAppType == "A") {
		sectionTopDivName = "sectionTopAddress";
	}
	var sectionTopDiv = dojo.byId(sectionTopDivName);
	var contentFrameDivName = "contentFrameObject";
	if (currentUdk && currentUdk.nodeAppType == "A") {
		contentFrameDivName = "contentFrameAddress";
	}
	var contentFrame = dojo.widget.byId(contentFrameDivName);
	contentFrame.resizeTo(730, splitPaneDiv.offsetHeight - sectionTopDiv.offsetHeight - 4);

	refreshTabContainers();
	// IE hack so the input containers are drawn correctly
	if (dojo.render.html.ie) {
		setTimeout("refreshInputContainers()", 1);
	}

	var scrollPos = dojo.byId(contentFrameDivName).scrollTop;
	setTimeout("setContentFrameScrollPosition('"+contentFrameDivName+"', '"+scrollPos+"')", 1);
}

function setContentFrameScrollPosition(contentFrameId, scrollPos) {
	dojo.byId(contentFrameId).scrollTop = scrollPos;
}


function refreshInputContainers(section) {
	var sectionDivId = "contentFrameBodyObject";
	var mainDivId = "contentFrameObject";
	if (section == "Address" || currentUdk && currentUdk.nodeAppType == "A") {
		sectionDivId = "contentFrameBodyAddress";
		mainDivId = "contentFrameAddress";
	}

	// Get the divs
	var contentDiv = dojo.byId(sectionDivId);
	var parentDiv = dojo.byId(mainDivId);

	// Backup scroll position
	var scrollPos = parentDiv.scrollTop;

	// Refresh the content divs (disable/enalbe)
	dojo.html.toggleDisplay(contentDiv);
	dojo.html.toggleDisplay(contentDiv);

	// Reset the scroll position to the old value
	parentDiv.scrollTop = scrollPos;
}

function refreshTabContainers() {
	// Update all the tabContainers so they are displayed properly
	// First we check if the tabContainer currently is displayed. If it is, call the 'onResized' method to properly
	// size and display the container
	var tabContainerIds = ["ref1SymbolsTabContainer", "ref1KeysTabContainer", "ref1BasisTabContainer", "ref1DataBasisTabContainer",
  						 "ref1ProcessTabContainer", "ref2LocationTabContainer", "ref2BaseDataTabContainer", "ref3BaseDataTabContainer",
  						 "ref4ParticipantsTabContainer", "ref4PMTabContainer", "ref5MethodTabContainer"];

	// Helper function that determines if a node is displayed or not. All the dojo.html functions that should
	// give this information (dojo.html.isDisplayed, ...) don't work. Also the widgets 'onShow' Method is never fired
	// so we can't connect to that either
	var isDisplayed = function(domNode) {
		if (domNode.style.display != "") {
			return domNode.style.display != "none";
		} else {
			return isDisplayed(domNode.parentNode);
		}
	}

	dojo.lang.forEach(tabContainerIds, function(item) {
		var tabContainer = dojo.widget.byId(item);

		if (isDisplayed(tabContainer.domNode)) {
//			dojo.debug(item+" is displayed, resize...");
			tabContainer.onResized();
		}
//		else {
//			dojo.debug(item+" is not displayed.");
//		}
	});
}


function toggleFields(section, /* optional */ mode, /* optional flag */ refreshContainers) {
//	dojo.debug("toggleFields("+section+", "+mode+")");

	if (typeof(refreshContainers) == "undefined") {
		refreshContainers = true;
	}

	var sectionElement;
	var allSpanElements;
	var allDivElements;

	if (typeof(section) != "undefined" && section != "Object" && section != "Address") {
		sectionElement = dojo.byId(section);
		allSpanElements = sectionElement.getElementsByTagName("span");
		allDivElements = sectionElement.getElementsByTagName("div");

//		dojo.debug("number of div elements: "+allDivElements.length);
//		dojo.debug("number of span elements: "+allSpanElements.length);

		if (typeof(mode) == "undefined") {
			if (typeof(sectionElement.isExpanded) == "undefined" || sectionElement.isExpanded == false) {
				mode = "showAll";
	
			} else {
				mode = "showRequired";
			}			
		}

		if (mode == "showAll") {
			sectionElement.isExpanded = true;

		} else if (mode == "showRequired") {
			sectionElement.isExpanded = false;
		}

//		dojo.debug("mode: "+mode);
//		dojo.debug("sectionElement will be expanded: "+sectionElement.isExpanded);

		if (sectionElement.getElementsByTagName('img')[0] && sectionElement.getElementsByTagName('img')[0].src.indexOf("expand") != -1) {
			var btnImage = sectionElement.getElementsByTagName('img')[0];
			var link = sectionElement.getElementsByTagName('a')[0];
			toggleButton(btnImage, link, 'blue', mode);
		}

	} else {
		var sectionDivId;

		if (section == "Address" || currentUdk && currentUdk.nodeAppType == "A") {
			sectionDivId = "contentFrameBodyAddress";
  		} else {
			sectionDivId = "contentFrameBodyObject";
  		}
		var sectionDiv = dojo.byId(sectionDivId);

		if (typeof(sectionDiv.isExpanded) == "undefined") {
			sectionDiv.isExpanded = true;
		}

		if (sectionDiv.isExpanded == false) {
			mode = "showAll";
			sectionDiv.isExpanded = true;

		} else {
			mode = "showRequired";
			sectionDiv.isExpanded = false;			
		}

		var toggleBtn = dojo.widget.byId('toggleFieldsBtn');
		var btnImage = toggleBtn.domNode.getElementsByTagName('img')[0];
		toggleButton(btnImage, toggleBtn.domNode, 'grey', mode);

		dojo.lang.forEach(sectionDiv.childNodes, function(section) {
			if (typeof(section.id) != "undefined" && dojo.html.hasClass(section, "contentBlock")) {
//				console.log("calling toggleFields("+section.id+", "+mode+").");
				toggleFields(section.id, mode, false);
			}
		});
		
		refreshTabContainers();

		if (dojo.render.html.ie) {
			setTimeout("refreshInputContainers('"+section+"')", 1);
		}

		return;
	}
/*
	dojo.debug("Mode: "+mode);
	dojo.debug("Number of elements of type span: "+allSpanElements.length);
*/
	var allOptionalUIElements = dojo.lang.filter(allSpanElements, function(spanElement) {
		return (dojo.string.startsWith(spanElement.id, "uiElement") && (spanElement.getAttribute("type") == "optional"));
	});

//	dojo.debug("Number of optional UI Elements: "+allOptionalUIElements.length);

	// Hide all optional input elements
	if (mode == "showAll") {
		// show the input elements with displaytype == undef and displaytype == alwaysShow
		// hide the input elements with displaytype == alwaysHide
		var hiddenUIElements = dojo.lang.filter(allOptionalUIElements, function(uiElement){ return uiElement.getAttribute("displaytype") == "alwaysHide"; });
		var shownUIElements =  dojo.lang.filter(allOptionalUIElements, function(uiElement){ return uiElement.getAttribute("displaytype") != "alwaysHide"; });

		dojo.lang.forEach(hiddenUIElements, dojo.html.hide);
		dojo.lang.forEach(shownUIElements, dojo.html.show);

	} else {
		// show the input elements with displaytype == alwaysShow
		// hide the input elements with displaytype == undef and displaytype == alwaysHide
		var hiddenUIElements = dojo.lang.filter(allOptionalUIElements, function(uiElement){ return uiElement.getAttribute("displaytype") != "alwaysHide"; });
		var shownUIElements =  dojo.lang.filter(allOptionalUIElements, function(uiElement){ return uiElement.getAttribute("displaytype") == "alwaysShow"; });

		dojo.lang.forEach(hiddenUIElements, dojo.html.hide);
		dojo.lang.forEach(shownUIElements, dojo.html.show);
	}


	var allInputContainers = dojo.lang.filter(allDivElements, function(divElement) {
		return dojo.html.hasClass(divElement, "inputContainer");
	});

//	dojo.debug("Number of inputContainer Elements: "+allInputContainers.length);

	// Hide all input containers where all input elements are hidden when mode == showRequired
	// Else show all
	dojo.lang.forEach(allInputContainers, function(inputContainer) {
		// Get all span elements below the current 'inputContainer'
		var spanElements = inputContainer.getElementsByTagName("span");
		// Get a list with all span elements where the id starts with 'uiElement'
		var uiElements = dojo.lang.filter(spanElements, function(span) {
			return (span.id && typeof(span.id) == "string" && dojo.string.startsWith(span.id, "uiElement"));
		});

//		dojo.debug("Number of uiElements in inputContainer: "+uiElements.length);

		// if some uiElements are displayed, show the inputContainer, else hide
		if (dojo.lang.some(uiElements, dojo.html.isShowing)) {
			dojo.html.show(inputContainer);

		} else {
			dojo.html.hide(inputContainer);
		}
	});


	if (refreshContainers) {
		refreshTabContainers();

		if (dojo.render.html.ie) {
			setTimeout("refreshInputContainers('"+section+"')", 1);
		}
	}
}

function toggleButton(btnImage, labelElement, color, mode)
{
  if (!btnImage)
    return;

  // the button's target state
  var toggleTo = "";
  // tool tips & link titles
  var titles = [];
  titles['required'] = 'Nur Pflichtfelder aufklappen';
  titles['all'] = 'Alle Felder aufklappen';
  
  if (mode == "showAll")
    toggleTo = "required";
  else if (mode == "showRequired")
    toggleTo = "all";
/*
  else if (btnImage.src.indexOf("expand_required") != -1)
    toggleTo = "all";
  else if (btnImage.src.indexOf("expand_all") != -1)
    toggleTo = "required";
*/
  btnImage.src = "img/ic_expand_" + toggleTo + "_" + color + ".gif";
  btnImage.setAttribute('alt', titles[toggleTo]);
  if(labelElement)
    labelElement.setAttribute('title', titles[toggleTo]);
}

function setRequiredState(/*html node to (un-)set the required class*/labelNode, 
  /*html node to (un-)set the notRequired class, maybe null*/containerNode, isRequired) {
//	dojo.debug("-> setRequiredState("+labelNode.id+", "+containerNode.id+", "+isRequired+")");

  // set the label to required
  // get the actual textnode
	var textNode = dojo.dom.firstElement(labelNode, "label");
	if (labelNode && textNode) {
		if (isRequired) {
			dojo.html.addClass(labelNode, "required");
			if (textNode.innerHTML.charAt(textNode.innerHTML.length-1) != "*") {
				textNode.innerHTML += "*";
			}

		} else {
			dojo.html.removeClass(labelNode, "required");
			dojo.html.removeClass(labelNode, "important");	// Remove the important tag too incase it is highlited
			if (textNode.innerHTML.charAt(textNode.innerHTML.length-1) == "*") {
				textNode.innerHTML = textNode.innerHTML.substring(0, textNode.innerHTML.length-1);
			}
		}
	}

	// set the container to required
	if (containerNode) {
//		dojo.debug("containerNode.type before change: "+containerNode.getAttribute("type"));
		if (isRequired) {
			containerNode.setAttribute("type", "required");

		} else {
			containerNode.setAttribute("type", "optional");
		}
//		dojo.debug("Setting containerNode.type to "+containerNode.type);

		var getSectionElement = function(node) {
			if (dojo.html.hasClass(node, "contentBlock"))
				return node;
			else
				return getSectionElement(node.parentNode);
		}

		var sectionElement = getSectionElement(containerNode);
		var isExpanded = sectionElement.isExpanded;
//		dojo.debug("Section element: "+sectionElement.id);
//		dojo.debug("Section element is expanded: "+sectionElement.isExpanded);

		if (isExpanded || isRequired) {
			dojo.html.show(containerNode);
//			dojo.debug("Showing containerNode.");

		} else {
			dojo.html.hide(containerNode);
//			dojo.debug("Hiding containerNode.");
		}
	}

	// Check if we have to update some inputContainers. TODO -> Mode to helper functions
	var allInputContainers = dojo.lang.filter(sectionElement.getElementsByTagName("div"), function(divElement) {
		return dojo.html.hasClass(divElement, "inputContainer");
	});

//	dojo.debug("Number of inputContainer Elements: "+allInputContainers.length);

	// Hide all input containers where all input elements are hidden when mode == showRequired
	// Else show all
	dojo.lang.forEach(allInputContainers, function(inputContainer) {
		// Get all span elements below the current 'inputContainer'
		var spanElements = inputContainer.getElementsByTagName("span");
		// Get a list with all span elements where the id starts with 'uiElement'
		var uiElements = dojo.lang.filter(spanElements, function(span) {
			return (span.id && typeof(span.id) == "string" && dojo.string.startsWith(span.id, "uiElement"));
		});

//		dojo.debug("Number of uiElements in inputContainer: "+uiElements.length);

		// if every uiElement has the type attribute set to optional...
		if (dojo.lang.every(uiElements, function(uiElement) {
			return uiElement.getAttribute ? uiElement.getAttribute("type") == "optional" : true;
		})) {
			if (isExpanded) {
				dojo.html.show(inputContainer);

			} else {
				// ... and mode == showRequired, then hide the inputContainer
				dojo.html.hide(inputContainer);
			}
		}
	});

//	dojo.debug("<- setRequiredState()");
}

/*
 * Tree manipulation
 */
function createItemClicked(menuItem)
{
	var selectedNode = menuItem.getTreeNode();
  var dlg = 'erfassung_objekt_anlegen.html';
  if (selectedNode.nodeAppType == "A")
    dlg = 'erfassung_adresse_anlegen.html';
  dialog.showPage(message.get('tree.nodeNew'), dlg, 502, 130, true, 
        {treeId:'tree', controllerId:'treeController', nodeId:selectedNode.widgetId});
}

// TODO: did not get this working in erfassung_objekt_anlegen.html
function createObjectClicked(node, useAssistant)
{
  var dlg = dialog.find(node);
  var params = dlg.customParams;

  // close selection dialog
  dialog.close(node);

  // proceed with creation
  if (useAssistant)
  {
    // create using assistant
    dialog.showPage(message.get('tree.assistant2'), 'erfassung_assistent_erfassung.html', 755, 600, true, 
        {treeId:params['treeId'], controllerId:params['controllerId'], nodeId:params['nodeId']});
  }
  else
  {
    // simple create
    var controller = dojo.widget.byId(params['controllerId']);
    var node = dojo.widget.byId(params['nodeId']);
    controller.createChild(node, 0, {title:node.nodeAppType});
  }
}


/*
 * Content selection
 */
function selectUDKClass()
{
	var val = dojo.widget.byId("objectClass").getValue();
	if (val) {
		var contentForm = dojo.widget.byId("contentFormObject");
	    contentForm.setSelectedClass(val);
		refreshTabContainers();
	}
}

function selectUDKAddressType(addressType)
{
	var val = UtilAddress.getAddressClass(addressType);
	if (val != -1) {
		var contentForm = dojo.widget.byId("contentFormAddress");
		contentForm.setSelectedClass("AddressType"+val);
		refreshTabContainers();
	}
}


function nodeSelected(message)
{
  if (message.node.id == "objectRoot" || message.node.id == "addressRoot" || message.node.id == "addressFreeRoot") {
    dojo.byId("contentAddress").style.display="none";
    dojo.byId("contentObject").style.display="none";
    dojo.byId("contentNone").style.display="block";
  }
  else if (message.node.nodeAppType == "A") {
    dojo.byId("contentAddress").style.display="block";
    dojo.byId("contentObject").style.display="none";
    dojo.byId("contentNone").style.display="none";
  }
  else if (message.node.nodeAppType == "O") {
    dojo.byId("contentAddress").style.display="none";
    dojo.byId("contentObject").style.display="block";
    dojo.byId("contentNone").style.display="none";
  }
  sizeContent();
}

function showAddress(menuItem)
{
//  dojo.debug("getTabFromContext menuItem: " + menuItem);

  var menu = menuItem.parent;
  var rowData = menu.getRowData();

  dialog.showPage('Adresse', 'mdek_address_preview_dialog.html', 500, 240, false, { data:rowData });
}
