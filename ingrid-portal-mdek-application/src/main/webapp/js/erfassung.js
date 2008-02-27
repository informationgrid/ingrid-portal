function sizeContent()
{
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

	// IE hack so the input containers are drawn correctly
	if (dojo.render.html.ie) {
		setTimeout("refreshInputContainers()", 1);
	}
}

function selectNode() {
	alert("selectNode");
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


function toggleFields(section) {
	// mode 'required' or 'all', needed for main toggle button on toolbar
	// if called from section's toggle button, then just invert display mode (determined by actual state)
	var mode = "";
	// get section or all sections respectively
	var rootNodes = [];
	if (section != undefined && section != "Object" && section != "Address") {
		rootNodes.push(dojo.byId(section));

//		dojo.debug("toggle fields in '" +section+ "'");
	} else {
		var sectionDivId = "contentFrameBodyObject";
		if (section == "Address" || currentUdk && currentUdk.nodeAppType == "A") {
			sectionDivId = "contentFrameBodyAddress";
  		}
//		dojo.debug("toggle fields in container '" + sectionDivId + "'");

		// button on toolbar clicked, toggle all fields on page
//		dojo.debug("Toolbar button clicked.");
		rootNodes = dojo.byId(sectionDivId).childNodes;
		var toggleBtn = dojo.widget.byId('toggleFieldsBtn');
		var btnImage = toggleBtn.domNode.getElementsByTagName('img')[0];
		toggleButton(btnImage, toggleBtn.domNode, 'grey');
		if (btnImage.src.indexOf("expand_required") != -1)
			mode = "all";
		else
			mode = "required";
	}

  // loop over content blocks and hide or show indvidual input containers
	for (var i=0; i<rootNodes.length; i++)
	{
		var currentSection = rootNodes[i];
		if (currentSection.nodeName == 'DIV')
		{
			var contentBlocks = rootNodes[i].childNodes;
			for (var j=0; j<contentBlocks.length; j++)
			{
				if (contentBlocks[j].nodeName == 'DIV' && contentBlocks[j].className == 'content')
				{
					if (mode == "") {
						contentBlocks[j].isExpanded = !contentBlocks[j].isExpanded;
					} else if (mode == "required") {
						contentBlocks[j].isExpanded = false;
					} else if (mode == "all") {
						contentBlocks[j].isExpanded = true;
					}

					var inputContainer = contentBlocks[j].childNodes;
					for (var k=0; k<inputContainer.length; k++)
					{
						if (inputContainer[k].className != undefined && inputContainer[k].className.indexOf('notRequired') != -1)
						{
							if (mode == "") {
								dojo.html.toggleDisplay(inputContainer[k]);
/*
								if (inputContainer[k].style.display == "" || inputContainer[k].style.display == "block")
									inputContainer[k].style.display = "none";
								else
									inputContainer[k].style.display = "block";
*/						
							} else if (mode == 'required') {
								dojo.html.hide(inputContainer[k]);
//								inputContainer[k].style.display = "none";
							} else if (mode == 'all') {
								dojo.html.show(inputContainer[k]);
//								inputContainer[k].style.display = "block";
							}
						}
					}
				}
			}
			// change toggle button images for sections
			if (currentSection.getElementsByTagName('img')[0] && currentSection.getElementsByTagName('img')[0].src.indexOf("expand") != -1)
			{
				var btnImage = currentSection.getElementsByTagName('img')[0];
				var link = currentSection.getElementsByTagName('a')[0];
				toggleButton(btnImage, link, 'blue', mode);
			}
		}
	}


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

	// IE hack so the input containers are drawn correctly
	if (dojo.render.html.ie) {
		setTimeout("refreshInputContainers('"+section+"')", 1);
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
  
  if (mode == "required")
    toggleTo = "all";
  else if (mode == "all")
    toggleTo = "required";
  else if (btnImage.src.indexOf("expand_required") != -1)
    toggleTo = "all";
  else if (btnImage.src.indexOf("expand_all") != -1)
    toggleTo = "required";

  btnImage.src = "img/ic_expand_" + toggleTo + "_" + color + ".gif";
  btnImage.setAttribute('alt', titles[toggleTo]);
  if(labelElement)
    labelElement.setAttribute('title', titles[toggleTo]);
}

function setRequiredState(/*html node to (un-)set the required class*/labelNode, 
  /*html node to (un-)set the notRequired class, maybe null*/containerNode, isRequired)
{
  // set the label to required
  // get the actual textnode
  var textNode = dojo.dom.firstElement(labelNode, "label");
  if (labelNode && textNode) {
    if (isRequired) {
      dojo.html.addClass(labelNode, "required");
      if (textNode.innerHTML.charAt(textNode.innerHTML.length-1) != "*")
        textNode.innerHTML += "*";
    }
    else {
      dojo.html.removeClass(labelNode, "required");
      dojo.html.removeClass(labelNode, "important");	// Remove the important tag too incase it is highlited
      if (textNode.innerHTML.charAt(textNode.innerHTML.length-1) == "*")
        textNode.innerHTML = textNode.innerHTML.substring(0, textNode.innerHTML.length-1);
    }
  }

  // set the container to required
	if (containerNode) {
		if (isRequired) {
			dojo.html.removeClass(containerNode, "notRequired");
		} else {
			dojo.html.addClass(containerNode, "notRequired");
		}

		var isExpanded = containerNode.parentNode.isExpanded;
		if (isExpanded || isRequired) {
			dojo.html.setDisplay(containerNode, "block");
		} else {
			dojo.html.setDisplay(containerNode, "none");
		}
	}
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
	}
}

function selectUDKAddressType()
{
	var val = dojo.widget.byId("addressType").getValue();
	if (val) {
		var contentForm = dojo.widget.byId("contentFormAddress");
		contentForm.setSelectedClass(val);
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
