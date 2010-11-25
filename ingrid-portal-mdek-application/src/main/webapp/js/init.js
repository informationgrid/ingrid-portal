// the root mdek object
// holds all states and encapsulates common functions
if(dj_undef("mdek", this)){
	var mdek = {};
}
var mdek = {};

dojo.addOnLoad(function()
{
  
  // initialize debug console if necessary
  if (djConfig.isDebug)
  {
    dojo.debug("The current version of dojo is: ", dojo.version.toString());
    var console = dojo.byId("dojoDebugConsole");
    console.style.visibility = "visible";
  }

  var def = initCatalogData();
  def.addCallback(initAdditionalFields);
  def.addCallback(initCurrentUser);
  def.addCallback(initCurrentGroup);
  def.addCallback(initGeneralEventListener);
  def.addCallback(initToolbar);
  def.addCallback(disableInputOnWrongPermission);
  def.addCallback(initTree);
  def.addCallback(initTableValidators);
  def.addCallback(initCTS);
  def.addCallback(initFreeTermsButtons);
  def.addCallback(initReferenceTables);
  def.addCallback(initSysLists);
  def.addCallback(initOptionalFieldStates);
  def.addCallback(initForm);
  def.addCallback(initMenu);
  def.addCallback(initSessionKeepalive);
  def.addCallback(hideSplash);	// hide the splash after everything is loaded
  def.addCallback(function() { udkDataProxy.resetDirtyFlag(); });
  def.addCallback(jumpToNodeOnInit);
  // NOTE: the undo button enable / disable function is connected to the set / resetDirtyFlag.
  // If the function is directly used as a callback (def.addCallback(resetDirtyFlag)), the connected
  // functions are never fired (dojo bug?).
  // If we create a new function that does nothing but call resetDirtyFlag, the connected functions are
  // called properly
  def.addCallback(function() { udkDataProxy.resetDirtyFlag(); });

  def.addErrback(function(err){ dojo.debug("Error: "+err); dojo.debugShallow(err); });
});

function initAdditionalFields() {
	var def = new dojo.Deferred();

	var language = UtilCatalog.getCatalogLanguage();
	CatalogService.getSysAdditionalFields(null, language, {
		callback: function(additionalFieldList) {
			if (additionalFieldList) {
				additionalFieldList.sort(function(a, b) { return (a.id - b.id); });
				addAdditionalFieldsToDocument(additionalFieldList);
			}
			def.callback();
		},
		errorHandler: function(msg, err) {
			dojo.debug("Error: "+msg);
			dojo.debugShallow(err);
			def.errback();
		}
	});

	return def;
}

// Add additional fields from the catalog if they exist
// Otherwise hide the 'additional fields' header
function addAdditionalFieldsToDocument(additionalFieldList) {
	var afContentBlock = dojo.byId("additionalFields");
	var afContainer = dojo.byId("additionalFieldsContainer");

	if (additionalFieldList && additionalFieldList.length != 0) {
		dojo.html.show(afContentBlock);
		for (var index = 0; index < additionalFieldList.length; ++index) {
			var currentField = createAdditionalFieldDomNode(additionalFieldList[index]);
			afContainer.appendChild(currentField);
		}

	} else {
		dojo.html.hide(afContentBlock);
	}
}

// Create a dom node for an additional field definition (AdditionalFieldBean)
function createAdditionalFieldDomNode(additionalField) {
	// Create the following dom structure:
	// <span id="uiElementAdd${additionalField.id}" type="optional">
	//   <span class="label">
	//     <label class="inActive">
	//       ${additionalField.name}
	//     </label>
	//     <span class="input">
	//       < ingrid:ValidationTextbox or ingrid:Select depending on ${additionalField.type} /> 
	//     </span>
	//   </span>
	// </span>

	// Create dom nodes
	var uiElementSpan = document.createElement("span");
	uiElementSpan.id = "uiElementAdd" + additionalField.id;
	uiElementSpan.setAttribute("type", "optional");
	var labelSpanElement = document.createElement("span");
	dojo.html.addClass(labelSpanElement, "label");
	var labelElement = document.createElement("label");
	dojo.html.addClass(labelElement, "inActive");
	labelElement.innerHTML = additionalField.name;
	var inputSpanElement = document.createElement("span");
	dojo.html.addClass(inputSpanElement, "input");

	// Create the dojo widget depending on the type
	var inputWidget;
	if ("TEXT" == additionalField.type) {
		inputWidget = dojo.widget.createWidget("ingrid:ValidationTextbox", {
			id: "additionalField" + additionalField.id,
			type: "text",
			maxlength: additionalField.size,
			name: additionalField.name });
		dojo.html.addClass(inputWidget.textbox, "w668");

	} else if ("LIST" == additionalField.type) {
		inputWidget = dojo.widget.createWidget("ingrid:ComboBox", {
			id: "additionalField" + additionalField.id,
			name: additionalField.name,
			autoComplete: "false" });
		dojo.html.addClass(inputWidget.textInputNode, "w648");
		// Set the correct select values via the contained data provider
		if (additionalField.listEntries) {
			var data = [];
			for (var entryIndex = 0; entryIndex < additionalField.listEntries.length; ++entryIndex) {
				var currentEntry = additionalField.listEntries[entryIndex];
				data.push([currentEntry, currentEntry]);
			}
			inputWidget.dataProvider.setData(data);
		}
	}

	// Add the widget to the global 'additionalFieldWidgets' list (from udkDataProxy.js)
	if (additionalFieldWidgets) {
		additionalFieldWidgets.push(inputWidget);
	}

	// Build the complete structure
	labelSpanElement.appendChild(labelElement);
	uiElementSpan.appendChild(labelSpanElement);
	inputSpanElement.appendChild(inputWidget.domNode);
	uiElementSpan.appendChild(inputSpanElement);
	return uiElementSpan;
}


function initMenu() {
	// Hide page3Subnavi2 if QA is deactivated
	if (!UtilQA.isQAActive() || !UtilSecurity.isCurrentUserQA()) {
		dojo.byId("page3Subnavi2").style.display = "none";
	}
}

function initForm() {
  // hide address and object panel after initialization
  // this must be done because if the panels are hidden by default, the select and comboboxes will init forever
  dojo.byId("contentAddress").style.display="none";
  dojo.byId("contentObject").style.display="none";
  dojo.byId("contentNone").style.display="block";

  // attach eventhandler to object/address type combobox and select default value
  var objectSelectBox = dojo.widget.byId("objectClass");
  if (objectSelectBox) {
    dojo.event.connect(objectSelectBox, "onValueChanged", "selectUDKClass");
    objectSelectBox.setValue("Class0");
  }
  var addressSelectBox = dojo.widget.byId("addressType");
  if (addressSelectBox) {
    dojo.event.connect(addressSelectBox, "setValue", "selectUDKAddressType");
    addressSelectBox.setValue("Institution");
  }

  // set context menu on object owner listbox
/*
  var objectOwnerWidget = dojo.widget.byId('objectOwner');
  if (objectOwnerWidget) {
    var objectOwnerCM = dojo.widget.createWidget("ingrid:ContextMenu");
    objectOwnerCM.addItemObject({caption:message.get('general.showAddress'), method:showAddress});
    objectOwnerCM.bindDomNode(objectOwnerWidget.domNode);
    objectOwnerWidget.textInputNode.style.cursor='pointer';
  }
*/

  // set context menu on last editor label
/*
  // TODO Fixme!
  var lastEditorLabel = dojo.byId('lastEditor');
  if (lastEditorLabel) {
    var lastEditorCM = dojo.widget.createWidget("ingrid:ContextMenu");
    lastEditorCM.addItemObject({caption:message.get('general.showAddress'), method:showAddress});
    lastEditorCM.bindDomNode(lastEditorLabel);
    lastEditorLabel.style.cursor='pointer';
  }
*/
  // size right content height
  dojo.event.connect(window, "onresize", window, "sizeContent");
  sizeContent();

  var contentForm = dojo.widget.byId('contentFormObject');  
  if (contentForm) {
    contentForm.initForm();
	toggleFields("Object");
  }
  var contentForm = dojo.widget.byId('contentFormAddress');  
  if (contentForm) {
    contentForm.initForm();
	toggleFields("Address");
  }

  // Init the contentBlocks
  var contentBlockIds = ["generalContent", "ref1Content", "ref1ContentDQ", "ref2Content", "ref3Content", "ref4Content", "ref5Content", "ref6Content",
  			"spatialRefContent", "timeRefContent", "extraInfoContent", "availabilityContent", "thesaurusContent", "linksContent",
  			"additionalFieldsContent",
  			"headerAddressType0Content", "headerAddressType1Content", "headerAddressType2Content", "headerAddressType3Content",
  			"address", "adrThesaurusContent", "associatedObjContent"];

	dojo.lang.forEach(contentBlockIds, function(divId) {
		dojo.byId(divId).isExpanded = false;
	});
}


function initTree() {

	// initially load data (first hierarchy level) from server 
	TreeService.getSubTree(null, null, 
		function (str) {
			var tree = dojo.widget.byId('tree');
			tree.setChildren(str);
		}
	);

/*
  -- Event Handling has been moved to a separate class 'menuEventHandler' -- 

  var contextMenu1 = dojo.widget.byId('contextMenu1');
  contextMenu1.treeController = dojo.widget.byId('treeController');
  contextMenu1.addItem(message.get('tree.nodeNew'), 'addChild', function(menuItem) {createItemClicked(menuItem)});
  contextMenu1.addItem(message.get('tree.nodePreview'), 'open', 'previewItemClicked');
  contextMenu1.addSeparator();
  contextMenu1.addItem(message.get('tree.nodeCut'), 'cut', 'cutItemClicked');
  contextMenu1.addItem(message.get('tree.nodeCopySingle'), 'copy', 'copySingleItemClicked');
  contextMenu1.addItem(message.get('tree.nodeCopy'), 'copy', 'copyItemClicked');
  contextMenu1.addItem(message.get('tree.nodePaste'), 'paste', 'pasteItemClicked');
  contextMenu1.addSeparator();
  contextMenu1.addItem(message.get('tree.nodeMarkDeleted'), 'detach', 'markDeletedItemClicked');
//  contextMenu1.addItem(message.get('tree.nodeDelete'), 'detach', 'deleteItemClicked');

  var contextMenu2 = dojo.widget.byId('contextMenu2');
  contextMenu2.treeController = dojo.widget.byId('treeController');
  contextMenu2.addItem(message.get('tree.nodeNew'), 'addChild', function(menuItem) {createItemClicked(menuItem)});
*/

  // shown when right-cicking on a child node of a root node
  var contextMenu1 = dojo.widget.byId('contextMenu1');
  contextMenu1.treeController = dojo.widget.byId('treeController');
  contextMenu1.addItem(message.get('tree.nodeNew'), 'addChild', menuEventHandler.handleNewEntity);
  contextMenu1.addItem(message.get('tree.nodePreview'), 'open', menuEventHandler.handlePreview);
  contextMenu1.addSeparator();
  contextMenu1.addItem(message.get('tree.nodeCut'), 'cut', menuEventHandler.handleCut);
  contextMenu1.addItem(message.get('tree.nodeCopySingle'), 'copy', menuEventHandler.handleCopyEntity);
  contextMenu1.addItem(message.get('tree.nodeCopy'), 'copy', menuEventHandler.handleCopyTree);
  contextMenu1.addItem(message.get('tree.nodePaste'), 'paste', menuEventHandler.handlePaste);
  contextMenu1.addItem(message.get('tree.subReload'), 'reload', menuEventHandler.reloadSubTree);
  contextMenu1.addSeparator();
  if (UtilQA.isQAActive() && !UtilSecurity.isCurrentUserQA()) {
	contextMenu1.addItem(message.get('tree.nodeMarkDeleted'), 'detach', menuEventHandler.handleMarkDeleted);
  } else {
	contextMenu1.addItem(message.get('tree.nodeDelete'), 'detach', menuEventHandler.handleDelete);
  }

  // shown when right-clicking on a root node
  var contextMenu2 = dojo.widget.byId('contextMenu2');
  contextMenu2.treeController = dojo.widget.byId('treeController');
  contextMenu2.addItem(message.get('tree.nodeNew'), 'addChild', menuEventHandler.handleNewEntity);
  contextMenu2.addItem(message.get('tree.nodePaste'), 'paste', menuEventHandler.handlePaste);
  contextMenu2.addSeparator();
  contextMenu2.addItem(message.get('tree.reload'), 'reload', menuEventHandler.reloadSubTree);



  var tree = dojo.widget.byId("tree");

  // TODO Was macht diese Verbindung?? Die 'onValueChanged' Tree Methode bzw. das Event gibt es nicht...
//  if (tree)
//    dojo.event.connect(tree, "onValueChanged", "selectUDKClass");

  // attach node selection handler
  var treeListener = dojo.widget.byId('treeListener');
  dojo.event.topic.subscribe(treeListener.eventNames.select, "nodeSelected");

  // Load children of the node from server
  // Overwritten to work with dwr.
  var treeController = dojo.widget.byId('treeController');
  treeController.loadRemote = function(node, sync){
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();

		TreeService.getSubTree(node.id, node.nodeAppType, {
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
  			callback:function(res) { deferred.callback(res); },
//			timeout:10000,
			errorHandler:function(message) {
				UtilDWR.exitLoadingState();
				deferred.errback(new dojo.RpcError(message, this));
			}
  		});
		
		deferred.addCallback(function(res) {
			dojo.lang.forEach(res, function(obj){
				if (obj.title == null) {
					obj.title = "???";
				} else {
					obj.title = dojo.string.escape("html", obj.title); 
				}
			});
			return _this.loadProcessResponse(node,res);
		});
		deferred.addErrback(function(err) {
			displayErrorMessage(err);
//			dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING);
			dojo.debug(err);
			return err;
		});
		return deferred;
	};
}

// Initialize the Coordinate Transformation Service
function initCTS() {
	
	/* Coordinate Update Handler.
	 * This object connects two tables:
	 * A source table containing coordinates (Coordinate objects).
	 * A destination table containing Coordinate objects and a srs (Spatial Reference System) entry.
	 * When the source table onSelect method is invoked the update handler does the following things:
	 *  1. updateCoordinates() is called.
	 *     This Method fetches the currently selected object from the source table and all
	 *     srs identifiers from the target table. For each entry in the target table a coordinate transformation
	 *     request is sent to the CT Service.
	 *  2. The result of the CT Service request is redirected to the updateDestinationStore method.
	 *     This method gets the correct entry from the target table and updates the stored Coordinate
	 *
	 * args is an object containing two values:  args = {srcTable: FilteringTable, dstTable: FilteringTable}
	 *  srcTable - The table containing the coordinates which should be converted
	 *  dstTable - The 'slave' table containing transformed coordinates
	 *
	 * Usage:
	 *  simply create a new CoordinateUpdateHandler like this:
	 *   new CoordinateUpdateHandler({srcTable: dojo.widget.byId("src"), dstTable: dojo.widget.byId("dst")});
	 *
	 */
	function CoordinateUpdateHandler(args){
		this.srcTable = args.srcTable;
		this.srcSelect = args.srcSelect;
		this.dstTable = args.dstTable;
		dojo.event.connectOnce(this.srcTable, "onSelect", this, "updateCoordinates");
		dojo.event.connectOnce(this.srcSelect, "onValueChanged", this, "updateCoordinates");

		this.updateDestinationStore = function(ctsResponse) {
			var data = this.dstTable.store.getData()[0];
			this.dstTable.store.update(data, "longitude1", ctsResponse.coordinate.longitude1);
			this.dstTable.store.update(data, "latitude1", ctsResponse.coordinate.latitude1);
			this.dstTable.store.update(data, "longitude2", ctsResponse.coordinate.longitude2);
			this.dstTable.store.update(data, "latitude2", ctsResponse.coordinate.latitude2);
		}

		this.clearDstStore = function() {
			var data = this.dstTable.store.getData()[0];
			var clearValue = "";
			this.dstTable.store.update(data, "longitude1", clearValue);
			this.dstTable.store.update(data, "latitude1", clearValue);
			this.dstTable.store.update(data, "longitude2", clearValue);
			this.dstTable.store.update(data, "latitude2", clearValue);
		}

		this.showError = function() {
			var data = this.dstTable.store.getData()[0];
			var clearValue = "D.n.e.";
			this.dstTable.store.update(data, "longitude1", clearValue);
			this.dstTable.store.update(data, "latitude1", clearValue);
			this.dstTable.store.update(data, "longitude2", clearValue);
			this.dstTable.store.update(data, "latitude2", clearValue);
		}

		this.updateCoordinates = function() {
			var fromSRS = "GEO84";
			var selectedData = this.srcTable.getSelectedData();
			this.clearDstStore();
			if (selectedData.length != 1) {
				return;
			}
			var coords = selectedData[0];
			var dstStore = this.dstTable.getData();
			var toSRS = this.srcSelect.getValue();
			if (coords && toSRS
					&& dojo.validate.isRealNumber(coords.longitude1)
					&& dojo.validate.isRealNumber(coords.longitude2)
					&& dojo.validate.isRealNumber(coords.latitude1)
					&& dojo.validate.isRealNumber(coords.latitude2)) {
//				dojo.debug("Calling CTService("+fromSRS+", "+toSRS+", "+coords+")");
				var _this = this;
				CTService.getCoordinates(fromSRS, toSRS, coords, {
//						preHook: UtilDWR.enterLoadingState,
//						postHook: UtilDWR.exitLoadingState,
						callback: dojo.lang.hitch(this, this.updateDestinationStore),
						timeout:8000,
						errorHandler:function(message) {
							UtilDWR.exitLoadingState();
							dojo.debug(message);
							_this.showError();
						}
					}
				);
			}
		}
	}	// StoreUpdateHandler

	
	// Connect all coordinate tables:
	new CoordinateUpdateHandler({
		srcTable: dojo.widget.byId("spatialRefAdminUnit"),
		srcSelect: dojo.widget.byId("spatialRefAdminUnitSelect"),
		dstTable: dojo.widget.byId("spatialRefAdminUnitCoords")
	});

	new CoordinateUpdateHandler({
		srcTable: dojo.widget.byId("spatialRefLocation"),
		srcSelect: dojo.widget.byId("spatialRefLocationSelect"),
		dstTable: dojo.widget.byId("spatialRefLocationCoords")
	});

}


function initFreeTermsButtons() {

	var executeSearch = function() {
		var _this = this;
		var termList = parseQueryTerm(this._inputFieldWidget.getValue());
		var inspireTopics = [];

		// For object search terms...
		if (_this.widgetId == "thesaurusFreeTermsAddButton" && termList && termList.length > 0) {
			// Check if the termList contains inspire topics
			// If so, tell the user and wait for confirmation. Then proceed with the normal search
			termList = dojo.lang.filter(termList, function(t) {
				if (isInspireTopic(t)) {
					inspireTopics.push(t);
					return false;
				} else {
					return true;
				}
			});
		}
		dojo.debug("term list: "+termList);
		dojo.debug("inspire topics: "+inspireTopics);

		var def = new dojo.Deferred();
		// add inspire topics to the list
		if (inspireTopics.length > 0) {
			addInspireTopics(inspireTopics);
			var displayText = dojo.string.substituteParams(message.get("dialog.addInspireTopics.message"), inspireTopics.join(", "));
			dialog.show(message.get("dialog.addInspireTopics.title"), displayText, dialog.INFO, [
			    { caption: message.get("general.ok"), action: function() { return def.callback(); } }
			]);
		} else {
			// Otherwise just execute the callback to continue
			def.callback();
		}

		if (termList && termList.length > 0) {
			def.addCallback(function() {
				// search for topics in SNS and put results into findTopicDefList
				var findTopicDefList = [];
				for (var i = 0; i < termList.length; ++i) {
					findTopicDefList.push(findTopicsDef(termList[i]));
				}

				UtilDWR.enterLoadingState();
				var defList = new dojo.DeferredList(findTopicDefList, false, false, true);
				defList.addErrback( function(err) { UtilDWR.exitLoadingState(); handleFindTopicsError(err); } );
				defList.addCallback(function(resultList) {
					UtilDWR.exitLoadingState();
					var snsTopicList = [];
					for (var i = 0; i < resultList.length; ++i) {
						// TODO handle sns timeout errors?
						snsTopicList.push(resultList[i][1]);
					}

					handleFindTopicsResult(termList, snsTopicList, _this._termListWidget.store);
				});
			});
		}
		
		// empty field with user added entries
		this._inputFieldWidget.setValue("");
	}

	// Add the function(s) to the button in the object form
	var button = dojo.widget.byId("thesaurusFreeTermsAddButton");
	var inputField = dojo.widget.byId("thesaurusFreeTerms");

	button._inputFieldWidget = dojo.widget.byId("thesaurusFreeTerms");
	button._termListWidget = dojo.widget.byId("thesaurusTerms");
	button.onClick = executeSearch;


/*
	// Uncomment to connect the 'enter' key with submitting a search
	// Removed since we now have a multiline input (textarea)
    dojo.event.connect(inputField.domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER && currentUdk.writePermission) {
                dojo.widget.byId("thesaurusFreeTermsAddButton").onClick();
            }
        });
*/

	// Add the function(s) to the button in the address form
	button = dojo.widget.byId("thesaurusFreeTermsAddressAddButton");

/*
	// Uncomment to connect the 'enter' key with submitting a search
	// Removed since we now have a multiline input (textarea)
	inputField = dojo.widget.byId("thesaurusFreeTermInputAddress");
    dojo.event.connect(inputField.domNode, "onkeypress",
        function(event) {
            if (event.keyCode == event.KEY_ENTER && currentUdk.writePermission) {
                dojo.widget.byId("thesaurusFreeTermsAddressAddButton").onClick();
            }
        });
*/
	button._inputFieldWidget = dojo.widget.byId("thesaurusFreeTermInputAddress");
	button._termListWidget = dojo.widget.byId("thesaurusTermsAddress");
	button.onClick = executeSearch;

    dojo.event.connect(udkDataProxy, "onAfterLoad", function() { dojo.widget.byId("thesaurusFreeTerms").setValue(""); });
    dojo.event.connect(udkDataProxy, "onAfterLoad", function() { dojo.widget.byId("thesaurusFreeTermInputAddress").setValue(""); });
}

/* ---- Helper functions for the free terms search button ---- */ 

function getInspireTopicId(topic) {
	return dojo.widget.byId("thesaurusInspireCombobox").getValueForDisplayValue(topic);
}
function isInspireTopic(topic) {
	return getInspireTopicId(topic) != null;
}


// Callback to find sns topics for a given topic (SNSService.findTopcis)
function findTopicsDef(term) {
	var def = new dojo.Deferred();
	SNSService.findTopics(term, {
		callback: function(res) {
			UtilList.addSNSTopicLabels(res);
			def.callback(res);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug("Error while calling findTopics: " + errMsg);
			def.errback(err);
		}
	});
	return def;
}

//Callback to find an sns descriptor for a synonym (nonDescriptor)
function getTopicsForTopicDef(topicId) {
	var def = new dojo.Deferred();
	SNSService.getTopicsForTopic(topicId, {
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

// Main function to analyze and add the found topic results to the store given in 'store'
function handleFindTopicsResult(termList, snsTopics, store) {
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
	dojo.debug("term list: "+termList);
	for (var index = 0; index < termList.length; ++index) {
		var queryTerm = termList[index];
		var curSnsTopics = snsTopics[index];

		dojo.debug("query term: " + queryTerm);

		if (curSnsTopics && curSnsTopics.length != 0) {
			// Try to find the queryTerm in the list of sns terms first
			var snsTopicsEqualToTerm = dojo.lang.filter(curSnsTopics, function(t) { return t.title.toLowerCase() == queryTerm.toLowerCase(); });
			if (snsTopicsEqualToTerm.length != 0) {
				// Check if one of the found terms is a descriptor / nonDescriptor
				var snsDescriptorsEqualToTerm = dojo.lang.filter(snsTopicsEqualToTerm, function(t) { return t.type == "DESCRIPTOR"; });
				var snsNonDescriptorsEqualToTerm = dojo.lang.filter(snsTopicsEqualToTerm, function(t) { return t.type == "NON_DESCRIPTOR"; });

				if (snsDescriptorsEqualToTerm.length != 0) {
					// If the term was found as a descriptor, add all descriptors to the search term list 
					addDescriptors(snsDescriptorsEqualToTerm, store);

				} else if (snsNonDescriptorsEqualToTerm.length != 0) {
					// otherwise, if the term was found as a synonym
					// Add it to the list as a free term and ask the user if the corresponding descriptor(s)
					// should be added as well

					// create a closure with fixed arguments. Otherwise we end up adding only the last
					// nonDescriptor since snsNonDescriptorsEqualToTerm gets overwritten in every iteration
					// see test_delayedFunctions.jsp for more information
					(function(fixedDescs, fixedStore) {
						def.addCallback(function() { return addNonDescriptorsDef(fixedDescs, fixedStore); });
					})(snsNonDescriptorsEqualToTerm, store)
					
				} else {
					// Found topic is of type TOP_TERM or NODE_LABEL. Simply add it to the list as a free term
					addFreeTerm(queryTerm, store);
				}
				
				// add synonyms for inspire if available
				def.addCallback(function() { return addInspireSynonyms(snsTopicsEqualToTerm); });
				//def.addCallback(function() { return addInspireSynonyms(curSnsTopics); });

			} else {
				// Results were returned by the SNS that did not contain the queryTerm
				// e.g. ('Inhalierbarer' results in 'Inhalierbarer Feinstaub' and 'PM10')
				// Add the search term as free term
				addFreeTerm(queryTerm, store);
			}

		} else {
			// No results were returned by the sns. Add the queryTerm as free term to the search term list
			addFreeTerm(queryTerm, store);
		}

	}
}

// if there's a term that's a synonym to an inspire topic then add it to the list in case the user
// acjnowledges it
function addInspireSynonyms(curSnsTopics) {
	//var inspireList; 
	//dojo.debug("SNS Topics length: " + curSnsTopics.length);
	var def = new dojo.Deferred();
	def.callback();
	dojo.lang.forEach(curSnsTopics, function(snsTopic) {
		//dojo.debug("InspireList: " + snsTopic.inspireList.length); 
		if (snsTopic.inspireList.length > 0) {
			def.addCallback(function() {
				var closeDialogDef = new dojo.Deferred();
				var displayText = dojo.string.substituteParams(message.get("dialog.addInspireTopics.question"), snsTopic.title, snsTopic.inspireList.join(","));
				dialog.show(message.get("dialog.addInspireTopics.title"), displayText, dialog.INFO, [
					{ caption: message.get("general.no"),  action: function() { closeDialogDef.callback(); return; } },
					{ caption: message.get("general.yes"), action: function() { closeDialogDef.callback(); addInspireTopics(snsTopic.inspireList); return; } }
				]);
				return closeDialogDef;
			});
		}
	});
	return def;
}

//Add inspire topics to the inspire table if they don't already exist
// inspireTopics: Array of Strings
function addInspireTopics(inspireTopics) {
	try {
		var store = dojo.widget.byId("thesaurusInspire").store;
		dojo.lang.forEach(inspireTopics, function(t) {
			var inspireEntryId = dojo.widget.byId("thesaurusInspireCombobox").getValueForDisplayValue(t);
			// add it only if there's a valid value (here are NO free entries!)
			if (inspireEntryId != null) {
				dojo.debug("adding entry: [" + inspireEntryId + ", " + t + "]");
				// if item does not already exist in table 
				if (dojo.lang.every(store.getData(), function(item) { return item.title != inspireEntryId; })) {
					var identifier = UtilStore.getNewKey(store);
					store.addData( { Id: identifier, title: inspireEntryId } );
				}
			} else {
				dojo.debug(t + " is not a valid INSPIRE topic!");
				var displayText = dojo.string.substituteParams(message.get("dialog.addInspireTopics.error"), t);
				dialog.show(message.get("dialog.addInspireTopics.title"), displayText, dialog.WARNING);
			}
		});
	} catch (error) {
		dojo.debug("error: "+ error);
		dojo.debugShallow(error);
	}
}

// needed for finding duplicated entries in Inspire list
function inspireArrayContains(array, value) {
	var i, listed = false;
	for (i=0; i<array.length; i++) {
		if (array[i].title === value) {
			listed = true;
			break;
		}
	}
	return listed;
};

// get an array of Inspire topics
function getInspireTopics(topics) {
	var inspireArray = new Array();
	dojo.lang.forEach(topics, function(topic) {
		if (topic.inspireList.length > 0) {
			dojo.lang.forEach(topic.inspireList, function(inspireTopic) {
				// exclude multiple same entries
				if (!inspireArrayContains(inspireArray,inspireTopic)) {			
					var obj = new Object();
					obj.title = inspireTopic;
					obj.source = "INSPIRE";
					inspireArray.push(obj);
				}
			});
		}
	});
	return inspireArray;
}

// Add a 'free' term to store if it doesn't already exist
function addFreeTerm(queryTerm, store) {
	dojo.debug("add free term: " + queryTerm);
	if (dojo.lang.every(store.getData(), function(item) { return item.title.toLowerCase() != queryTerm.toLowerCase(); })) {
		// If every term in store is != to the queryTerm, add it
		var identifier = UtilStore.getNewKey(store);
		store.addData( { Id: identifier, label: queryTerm, title: queryTerm, source: "FREE", sourceString: "FREE"} );
	}
}

//Add a list of descriptors to store if they don't already exist
function addDescriptors(descriptors, store) {
	dojo.lang.forEach(descriptors, function(d) {
		dojo.debug("add descriptor: " + snsTopicToString(d));
		if (dojo.lang.every(store.getData(), function(item) { return item.topicId != d.topicId; })) {
			// If every topicId in store is != to the topicId of the descriptor, add it
			d.Id = UtilStore.getNewKey(store);
			store.addData( d );
		}
	});
}

//Add a list of non descriptors (synonyms) store if they don't already exist
// Ask the user (popup dialog) if the corresponding descriptors should be added as well
function addNonDescriptorsDef(nonDescriptors, store) {
	var deferred = new dojo.Deferred();
	deferred.callback();
	deferred.addErrback(handleFindTopicsError);

	dojo.lang.forEach(nonDescriptors, function(d) {
		dojo.debug("add non descriptor: " + snsTopicToString(d));
		addFreeTerm(d.label, store);

		deferred.addCallback(function() { return getTopicsForTopicDef(d.topicId); });
		deferred.addCallback(function(topic) {
			var closeDialogDef = new dojo.Deferred();

			dojo.debug("Add descriptor for synonym '" + snsTopicToString(d) + "' ? " + snsTopicToString(topic));

			// Show the dialog
			var displayText = dojo.string.substituteParams(message.get("dialog.addDescriptors.message"), d.label, topic.label);
//			var displayText = "Synonym: " + snsTopicToString(d) + " Deskriptor: " + snsTopicToString(topic);
			dialog.show(message.get("dialog.addDescriptor.title"), displayText, dialog.INFO, [
	            { caption: message.get("general.no"),  action: function() { closeDialogDef.callback(); return; } },
	        	{ caption: message.get("general.ok"), action: function() { closeDialogDef.callback(); addDescriptors([topic], store); } }
			]);

			return closeDialogDef;
		});

	});
	return deferred;
}


// Handle error thrown by findTopics
function handleFindTopicsError(err) {
	dojo.debug("An Error occured while calling findTopics: " + err);
	dojo.debugShallow(err);
}

// pretty print a SNSTopic. Convenience function for debugging
function snsTopicToString(topic) {
	return "[" + topic.type + ", " + topic.title + ", " + topic.alternateTitle + ", " + topic.topicId +", " + topic.gemetId + "]";
}

// parse a string and extract a list of terms.
// whitespace and newlines are used as delimeters, composite terms are enclosed in double quotes
// e.g the input:
// Wasser Umwelt
// "Inhalierbarer Feinstaub" Atomkraft
// results in:
// ["Wasser", "Umwelt", "Inhalierbarer Feinstaub", "Atomkraft"]
function parseQueryTerm(queryTerm) {
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
		var trimmedTerm = dojo.string.trim(term);
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

/* ----------------------------------------------------------- */



function initReferenceTables() {
	initObjectReferenceTables();
	initAddressReferenceTables();
}

function initObjectReferenceTables() {
	var mainStore = dojo.widget.byId("linksTo").store;

	var filterTableMap =
		[{tableId: "ref1SymbolsLink", 		filterId: 3555},		// 3555
		 {tableId: "ref1KeysLink", 			filterId: 3535},		// 3535
		 {tableId: "ref1ServiceLink", 		filterId: 5066},		// 5066
		 {tableId: "ref1BasisLink", 		filterId: 3520},		// 3520
		 {tableId: "ref1DataBasisLink", 	filterId: 3570},		// 3570
		 {tableId: "ref1ProcessLink", 		filterId: 3515},		// 3515
//		 {tableId: "ref2LocationLink", 		filterId: 3360},		// 3360 - Single Address
		 {tableId: "ref2BaseDataLink", 		filterId: 3345},		// 3345
		 {tableId: "ref3BaseDataLink", 		filterId: 3210},		// 3210
		 {tableId: "ref6BaseDataLink", 		filterId: 3210},		// 3210
//		 {tableId: "ref4ParticipantsLink", 	filterId: 3410},		// 3410 - Single Address
//		 {tableId: "ref4PMLink", 			filterId: 3400},		// 3400 - Single Address
		 {tableId: "ref5MethodLink", 		filterId: 3100}];		// 3100


	dojo.lang.forEach(filterTableMap, function(tableMapping) {
		var filterStore = dojo.widget.byId(tableMapping.tableId).store;
		filterStore.relationTypeFilter = tableMapping.filterId;
		
		// Connect all the setData calls on the filtered table to the main (unfiltered) table
/*
		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: filterStore,
			srcFunc: "onRemoveData",
			adviceObj: mainStore,
			adviceFunc: function(obj) {
				var o = this.getDataByKey(obj.key);
				if (o) {
					this.removeData(o);
				}
			},
			once: true,
			delay: 10
		});
*/

		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onRemoveData",
			adviceObj: filterStore,
			adviceFunc: function(obj) {
				var o = this.getDataByKey(obj.key);
				if (o) {
					this.removeData(o);
				}
			},
			once: true,
			delay: 10
		});

		// Connect all the setData calls on the main table to the filtered tables
		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onSetData",
			adviceObj: filterStore,
			adviceFunc: function() {
				var data = mainStore.getData();
				this.clearData();
				for (i in data) {
					if (data[i].relationType == this.relationTypeFilter) {
						var item = this.getDataByKey(data[i].Id);
						if (!item) {
							this.addData(data[i]);
						}
					}
				}
			},
			once: true,
			delay: 10
		});

	});
/*
	onClearData:function(){ },
	onAddData:function(obj){ },
	onAddDataRange:function(arr){ },
	onUpdateField:function(obj, field, val){ }
*/	
}


function initAddressReferenceTables() {
	var mainStore = dojo.widget.byId("generalAddress").store;

	var filterTableMap =
		[{ tableId: "ref2LocationLink", 	filterId: 3360, relationName: "Standort" },
		 { tableId: "ref4ParticipantsLink",	filterId: 3410, relationName: "Beteiligte" },
		 { tableId: "ref4PMLink", 			filterId: 3400, relationName: "Projektleiter"}];


	dojo.lang.forEach(filterTableMap, function(tableMapping) {
		var filterStore = dojo.widget.byId(tableMapping.tableId).store;
		filterStore.relationTypeFilter = tableMapping.filterId;
		filterStore.relationName = tableMapping.relationName;
		
		// Connect all the setData calls on the filtered table to the main (unfiltered) table

		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: filterStore,
			srcFunc: "onRemoveData",
			adviceObj: mainStore,
			adviceFunc: function(obj) {
				if (obj.src.removedOnUpdateField) {
					// onUpdateField removes the entry from the filtered table if it existed.
					// Since we connect the onRemove funtion to the main store, we have to check
					// if the item was removed directly, or if it was removed because of an 'updateField'
					// call.
					obj.src.removedOnUpdateField = false;
					return;
				}

				var o = this.getDataByKey(obj.key);
				if (o) {
					this.removeData(o);
				}
			},
			once: true,
			delay: 10
		});

		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onRemoveData",
			adviceObj: filterStore,
			adviceFunc: function(obj) {
//				dojo.debug("onRemoveData() on "+this.relationTypeFilter);
				var o = this.getDataByKey(obj.key);
				if (o) {
					this.removeData(o);
				}
			},
			once: true,
			delay: 10
		});

		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onAddData",
			adviceObj: filterStore,
			adviceFunc: function(obj) {
//				dojo.debug("onAddData() on "+this.relationTypeFilter);
				var data = mainStore.getData();
				this.clearData();
				for (i in data) {
					if (data[i].typeOfRelation == this.relationTypeFilter) {
						var item = this.getDataByKey(data[i].Id);
						if (!item) {
							this.addData(data[i]);
						}
					}
				}
			},
			once: true,
			delay: 10
		});

		// Connect all the setData calls on the main table to the filtered tables
		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onSetData",
			adviceObj: filterStore,
			adviceFunc: function() {
				var data = mainStore.getData();
				this.clearData();
				for (i in data) {
					if (data[i].typeOfRelation == this.relationTypeFilter) {
						var item = this.getDataByKey(data[i].Id);
						if (!item) {
							this.addData(data[i]);
						}
					}
				}
			},
			once: true,
			delay: 10
		});

		// If the combobox value has been updated, update the target stores depending on the new link type.
		// Get the relationName and compare it to the store values. If the value was found, add or remove it.
		dojo.event.kwConnect({
			adviceType: "after",
			srcObj: mainStore,
			srcFunc: "onUpdateField",
			adviceObj: filterStore,
			adviceFunc: function(obj, path, val) {
				var item = this.getDataByKey(obj.Id);
				if (item != null) {
					// The item was found in the current table. Check if it should be displayed (its name equals relationName)
					if (obj.nameOfRelation != this.relationName) {
						// The relation does not belong in the current filter table. Remove it.
						item.removedOnUpdateField = true;
						this.removeData(item);
					}
				} else {
					// The item was not found in the current table. Check if it should be displayed (its name equals relationName)
					if (obj.nameOfRelation == this.relationName) {
						// The relation belongs in the current filter table. Add it.
						obj.typeOfRelation = this.relationTypeFilter;
						obj.refOfRelation = 2010;
						this.addData(obj);
					}
				}
			},
			once: true,
			delay: 10
		});
	});

}

function initToolbar() {
  // Check if qa is active
  var isQAActive = UtilQA.isQAActive();
  var isUserQA = UtilSecurity.isCurrentUserQA();

  // create toolbar buttons with tooltips
  var rightToolbar = dojo.widget.byId('rightToolbar');
  rightToolbar.addChild("img/ic_expand_required_grey.gif", "after", {
                            onClick:function(){toggleFields();},
                            caption:message.get("ui.toolbar.toggleFieldsCaption"),
                            widgetId:"toggleFieldsBtn"
                          });
  rightToolbar.addChild("img/ic_help.gif", "after", {
	  						onClick:function() { window.open('mdek_help.jsp?hkey=hierarchy-maintenance-1#hierarchy-maintenance-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');},
                            caption:message.get("ui.toolbar.helpCaption")
                          });
  
  var leftToolbar = dojo.widget.byId('leftToolbar');
  var newEntityButton = leftToolbar.addChild("img/ic_new.gif", "after", {
                            onClick:menuEventHandler.handleNewEntity,
                            caption:message.get("ui.toolbar.createNewEntityCaption")
                          });
  var previewButton = leftToolbar.addChild("img/ic_preview.gif", "after", {
                            onClick:menuEventHandler.handlePreview,
                            caption:message.get("ui.toolbar.previewCaption")
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  var cutButton = leftToolbar.addChild("img/ic_cut.gif", "after", {
                            onClick:menuEventHandler.handleCut,
                            caption:message.get("ui.toolbar.cutCaption")
                          });
  var copyEntityButton = leftToolbar.addChild("img/ic_copy.gif", "after", {
                            onClick:menuEventHandler.handleCopyEntity,
                            caption:message.get("ui.toolbar.copyEntityCaption")
                          });
  var copyTreeButton = leftToolbar.addChild("img/ic_copy_tree.gif", "after", {
                            onClick:menuEventHandler.handleCopyTree,
                            caption:message.get("ui.toolbar.copyTreeCaption")
                          });
  var pasteButton = leftToolbar.addChild("img/ic_paste.gif", "after", {
                            onClick:menuEventHandler.handlePaste,
                            caption:message.get("ui.toolbar.pasteCaption")
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  var saveButton = leftToolbar.addChild("img/ic_save.gif", "after", {
                            onClick:menuEventHandler.handleSave,
                            caption:message.get("ui.toolbar.saveCaption")
                          });
  var undoButton = leftToolbar.addChild("img/ic_undo.gif", "after", {
                            onClick:menuEventHandler.handleUndo,
                            caption:message.get("ui.toolbar.undoCaption")
                          });
  var discardButton = leftToolbar.addChild("img/ic_discard.gif", "after", {
                            onClick:menuEventHandler.handleDiscard,
                            caption:message.get("ui.toolbar.discardCaption")
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");

var reassignToAuthorButton = null;
  if (isQAActive && isUserQA) {
	reassignToAuthorButton = leftToolbar.addChild("img/ic_submit_author.gif", "after", {
                            onClick:menuEventHandler.handleReassignToAuthor,
                            caption:message.get("ui.toolbar.reassignCaption")
                          });
  }

  if (isQAActive && !isUserQA) {
	var finalSaveButton = leftToolbar.addChild("img/ic_submit_qs.gif", "after", {
                            onClick:menuEventHandler.handleForwardToQA,
                            caption:message.get("ui.toolbar.assignToQaCaption")
                          });
  } else {
  	var finalSaveButton = leftToolbar.addChild("img/ic_submit.gif", "after", {
                            onClick:menuEventHandler.handleFinalSave,
//                          disabled:true,
                            caption:message.get("ui.toolbar.publishCaption")
                          });
  }

  if (isQAActive && !isUserQA) {
  	var deleteButton = leftToolbar.addChild("img/ic_delete.gif", "after", {
                            onClick:menuEventHandler.handleMarkDeleted,
                            caption:message.get("ui.toolbar.markDeletedCaption")
                          });
  } else {
  	var deleteButton = leftToolbar.addChild("img/ic_delete.gif", "after", {
                            onClick:menuEventHandler.handleDelete,
                            caption:message.get("ui.toolbar.deleteCaption")
                          });  	
  }

  var removeDeleteFlagButton = null;
/*
  if (isQAActive && isUserQA) {
	removeDeleteFlagButton = leftToolbar.addChild("img/ic_delete_undo.gif", "after", {
                            onClick:menuEventHandler.handleUnmarkDeleted,
                            caption:"L�schen aufheben"
                          });
  }
*/
  var showChangesButton = leftToolbar.addChild("img/ic_original.gif", "after", {
                            onClick:menuEventHandler.handleShowChanges,
                            caption:message.get("ui.toolbar.showChangesCaption")
                          });

  leftToolbar.addSeparator("img/ic_sep.gif", "after");
  var showCommentButton = leftToolbar.addChild("img/ic_comment.gif", "after", {
                            onClick:menuEventHandler.handleShowComment,
                            caption:message.get("ui.toolbar.showCommentCaption")
                          });


	// Activate/Deactivate buttons depending on the selected node
	var treeListener = dojo.widget.byId("treeListener");
	var treeController = dojo.widget.byId("treeController");

	// Modify button tooltips depending on whether the current node is marked deleted
	if (isQAActive && isUserQA) {
	    dojo.event.topic.subscribe(treeListener.eventNames.select, function(msg) {
			var markedDeleted = msg.node.isMarkedDeleted;
			if (markedDeleted) {
				finalSaveButton.domNode.setAttribute("title", message.get("ui.toolbar.discardDeleteCaption"));
				deleteButton.domNode.setAttribute("title", message.get("ui.toolbar.finalDeleteCaption"));
			} else {
				finalSaveButton.domNode.setAttribute("title", message.get("ui.toolbar.publishCaption"));
				deleteButton.domNode.setAttribute("title", message.get("ui.toolbar.deleteCaption"));
			}
		});
	}


	// Show/hide toolbar buttons depending on the user rights
    dojo.event.topic.subscribe(treeListener.eventNames.select, function(message) {
		var hasWritePermission = message.node.userWritePermission;
		var hasWriteSinglePermission = message.node.userWriteSinglePermission;
		var hasWriteTreePermission = message.node.userWriteTreePermission;
		var hasWriteSubTreePermission = message.node.userWriteSubTreePermission;
		var isPublished = message.node.isPublished;
		var canCreateRootNodes = UtilSecurity.canCreateRootNodes();
//		dojo.debug("User has write permission? "+hasWritePermission);

		var buttonList = [showChangesButton, previewButton, cutButton, copyEntityButton, copyTreeButton, discardButton,
				saveButton, finalSaveButton, reassignToAuthorButton, deleteButton, showCommentButton, newEntityButton, pasteButton];
		var enableList = [];

		// Initially disable all buttons
		dojo.lang.forEach(buttonList, function(item) { if (item != null) { item.disable(); } });

		// Build the enable list
		if (message.node.id == "objectRoot" || message.node.id == "addressRoot" || message.node.id == "addressFreeRoot") {
			if (canCreateRootNodes) {
				enableList.push(newEntityButton);
			}

		} else if (message.node.id == "newNode") {
			enableList = enableList.concat([previewButton, saveButton, finalSaveButton, deleteButton, showCommentButton]);

		} else {
			// If a 'normal' node (obj/adr that is not root) is selected, always enable the following nodes
			enableList = enableList.concat([previewButton, copyEntityButton, showCommentButton]);

			// Only show the compare view dialog if a published version exists. Otherwise there's nothing to compare to
			if (isPublished) {
				enableList.push(showChangesButton);
			}

			// If the node has children, enable the 'copy tree' button
			if (message.node.isFolder) {
				enableList.push(copyTreeButton);
			}
			// If the the user has write permission (single or tree), he can discard, save and publish nodes
			if (hasWritePermission) {
				enableList = enableList.concat([saveButton, finalSaveButton]);				

				// The discard button is only enabled if the user has write permission, a published version exists and an edited version exists
				if (isPublished && message.node.nodeDocType.search(/_.V/) != -1) {
					enableList.push(discardButton);
				}
			}
			// If the the user has write tree permission (tree), he can delete, move and create new nodes
			if (hasWriteTreePermission) {
				enableList = enableList.concat([deleteButton, cutButton]);
				if (message.node.nodeAppType == "O") {
					enableList.push(newEntityButton);

				} else if (message.node.nodeAppType == "A" && message.node.objectClass != 2 && message.node.objectClass != 3) {
					// For addresses, the new entity button depends on the class of the selected node
					enableList.push(newEntityButton);
				}
			}
			// If the the user has write tree permission (tree), but a node is assigned to QA, the user
			// is only allowed to create new subnodes.
			if (hasWriteSubTreePermission) {
				enableList.push(newEntityButton);
			}
			
			// If the current node is assigned to the QA enable the reassign button
			if (message.node.nodeDocType.search(/_Q/) != -1) {
				enableList.push(reassignToAuthorButton);
			}
		}

		// The paste button depends on the current selection in treeController and the current selected node
		if (treeController.canPaste(message.node)) {
			enableList.push(pasteButton);
		}

		dojo.lang.forEach(enableList, function(item) { if (item != null) { item.enable(); } });
    });

	// The undo button depends on the dirty flag
	dojo.event.connect("after", udkDataProxy, "setDirtyFlag", function() {
		undoButton.enable();
	});
	dojo.event.connect("after", udkDataProxy, "resetDirtyFlag", function() {
		undoButton.disable();
	});


	var showOrHidePasteButton = function(node) {
		// The paste button depends on the current selection in treeController and the current selected node
		if (treeController.canPaste(node)) {
			pasteButton.enable();
		} else {
			pasteButton.disable();
		}		
	};
    dojo.event.connectOnce("after", treeController, "move", showOrHidePasteButton);
    dojo.event.connectOnce("after", treeController, "prepareCopy", showOrHidePasteButton);
    dojo.event.connectOnce("after", treeController, "prepareCut", showOrHidePasteButton);

	// Initially disable all icons
	var disableList = [removeDeleteFlagButton, showChangesButton, previewButton, cutButton, copyEntityButton,
			copyTreeButton, saveButton, undoButton, discardButton, finalSaveButton, reassignToAuthorButton, deleteButton,
			showCommentButton, newEntityButton, pasteButton];	
	dojo.lang.forEach(disableList, function(item) { if (item != null) { item.disable(); } });
}

function initTableValidators() {
	var unsignedIntFlags = { min:0, max:2147483647 };

	// The coordinates in the spatial reference table must be empty or real numbers
	var table = dojo.widget.byId("spatialRefLocation");
	table.setValidationFunctions([
		{target: "longitude1", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
		{target: "longitude2", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
		{target: "latitude1", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
		{target: "latitude2", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}}
	]);

	// The vector format table must contain an integer as 'numElements'
	var table = dojo.widget.byId("ref1VFormatDetails");
	table.setValidationFunctions([
		{target: "numElements", validateFunction: function(item) {return (item == null || item == "" || (dojo.validate.isInteger(item) && dojo.validate.isInRange(item+"", unsignedIntFlags)));}}
	]);

	// Availability media option 'Datenvolumen' is of type double 
	var table = dojo.widget.byId("availabilityMediaOptions");
	table.setValidationFunctions([
		{target: "transferSize", validateFunction: function(item) { return (item == null || item == "" || dojo.validate.isRealNumber(item)); }}
	]);

	// object class 1 - The 'Erstellungsmassstab' table must contain an integer and two doubles
	var table = dojo.widget.byId("ref1Scale");
	table.setValidationFunctions([
		{target: "scale", validateFunction: function(item) {return (item == null || item == "" || (dojo.validate.isInteger(item) && dojo.validate.isInRange(item+"", unsignedIntFlags)));}},
		{target: "groundResolution", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
		{target: "scanResolution", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}}		
	]);

	// object class 3 - The 'Erstellungsmassstab' table must contain an integer and two doubles
	var table = dojo.widget.byId("ref3Scale");
	table.setValidationFunctions([
		{target: "scale", validateFunction: function(item) {return (item == null || item == "" || (dojo.validate.isInteger(item) && dojo.validate.isInRange(item+"", unsignedIntFlags)));}},
		{target: "groundResolution", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}},
		{target: "scanResolution", validateFunction: function(item) {return (item == null || item == "" || dojo.validate.isRealNumber(item));}}	
	]);

/*
	var table = dojo.widget.byId("timeRefTable");
	table.setValidationFunctions([
		{target: "date", validateFunction: function(item) {return (item != null && dojo.string.trim(item).length != 0);}},
		{target: "type", validateFunction: function(item) {return (item != null && dojo.string.trim(item).length != 0);}}
	]);
*/
}

function initCatalogData() {
	var deferred = new dojo.Deferred();

	CatalogService.getCatalogData({
		preHook: UtilDWR.enterLoadingState,
		postHook: UtilDWR.exitLoadingState,
		callback: function(res) {
			// Update catalog Data in udkDataProxy
			catalogData = res;
			dojo.byId("currentCatalogName").innerHTML = catalogData.catalogName;
			deferred.callback();
		},
		errorHandler:function(mes){
			UtilDWR.exitLoadingState();
			dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
			dojo.debug(mes);
			deferred.errback();
		}
	});

	return deferred;
}

function initCurrentGroup() {
	var def = new dojo.Deferred();
	var getGroupNameDef = getCurrentGroupName();

	getGroupNameDef.addCallback(function(groupName) {
		SecurityService.getGroupDetails(groupName, {
			preHook: UtilDWR.enterLoadingState,
			postHook: UtilDWR.exitLoadingState,
			callback: function(group) {
//				dojo.debug("Group Details:");
//				dojo.debugShallow(group);
				currentGroup = group;
				def.callback();
			},

			errorHandler:function(mes){
				UtilDWR.exitLoadingState();
				dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
				dojo.debug("Error: "+mes);
				def.errback(mes);
			}
		});
	});

	return def;
}

function getCurrentGroupName() {
	var def = new dojo.Deferred();

	SecurityService.getGroups(true, {
		preHook: UtilDWR.enterLoadingState,
		postHook: UtilDWR.exitLoadingState,
		callback: function(groupList) {
			for (var i = 0; i < groupList.length; ++i) {
				if (groupList[i].id == currentUser.groupId) {
//					dojo.debug("Found user group:");
//					dojo.debugShallow(groupList[i]);
					def.callback(groupList[i].name);
					break;
				}
			}
		},
		errorHandler:function(mes){
			UtilDWR.exitLoadingState();
			dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
			dojo.debug("Error: "+mes);
			def.errback(mes);
		}
	});

	return def;		
}

function initCurrentUser() {
	var def = new dojo.Deferred();

	SecurityService.getCurrentUser({
		preHook: UtilDWR.enterLoadingState,
		postHook: UtilDWR.exitLoadingState,
		callback: function(user) {
			currentUser = user;

			var roleName = UtilSecurity.getRoleName(user.role);
			var title = UtilAddress.createAddressTitle(user.address);
			dojo.byId("currentUserName").innerHTML = title;
			dojo.byId("currentUserRole").innerHTML = roleName;

			def.callback();
		},
		errorHandler:function(mes){
			UtilDWR.exitLoadingState();
			dialog.show(message.get("general.error"), message.get("init.userError"), dialog.WARNING);
			dojo.debug("Error: "+mes);
			def.errback(mes);
		}
	});

	return def;
}

function initSysLists() {
	var def = new dojo.Deferred();

	var selectWidgetIDs = ["spatialRefAltVDate", "spatialRefAltMeasure", "timeRefTypeCombobox",
		"generalAddressCombobox", "geometryTypeEditor", "timeRefPeriodicity", "availabilityMediaOptionsMediumCombobox",
		"timeRefStatus", "ref1DataSet", "ref1RepresentationCombobox", "thesaurusTopicsCombobox", "thesaurusInspireCombobox", "ref1VFormatTopology",
		"freeReferencesEditor", "timeRefIntervalUnit", "extraInfoLegalBasicsTableEditor", "extraInfoXMLExportTableEditor",
		"thesaurusEnvCatsCombobox", "thesaurusEnvTopicsCombobox", "ref1SpatialSystem", "ref1SymbolsTitleCombobox", "ref1KeysTitleCombobox",
		"ref2DocumentType", "ref3ServiceType", "ref3ServiceTypeEditor", "ref6ServiceType", "extraInfoLangData", "extraInfoCharSetData",
		"extraInfoLangMetaData", "extraInfoPublishArea", "extraInfoConformityLevelEditor",
		"availabilityDataFormatName", "availabilityAccessConstraintsEditor",
		"dq109NameOfMeasureEditor", "dq110NameOfMeasureEditor", "dq112NameOfMeasureEditor", "dq113NameOfMeasureEditor",
        "dq114NameOfMeasureEditor", "dq115NameOfMeasureEditor", "dq117NameOfMeasureEditor", "dq120NameOfMeasureEditor",
        "dq125NameOfMeasureEditor", "dq126NameOfMeasureEditor", "dq127NameOfMeasureEditor",
		// Addresses
		"headerAddressType2Style", "headerAddressType3Style", "headerAddressType2Title", "headerAddressType3Title",
		"addressComType","addressCountry"]; 

/*
	// TODO load the initial language code map from the backend
	var locale = dojo.hostenv.normalizeLocale(dojo.locale);
	var languageCode = locale.split("-")[0];

	if (languageCode != "de" && languageCode != "en") {
		dojo.debug("Unsupported Language. Setting language to english.");
		languageCode = "en";
	}
*/
	// Setting the language code to "de". Uncomment the previous block to enable language specific settings depending on the browser language
	var languageCode = UtilCatalog.getCatalogLanguage();
	dojo.debug("LanguageShort is: " + languageCode);
	
	var lstIds = [];
	dojo.lang.forEach(selectWidgetIDs, function(item){
		lstIds.push(dojo.widget.byId(item).listId);
	});

	CatalogService.getSysLists(lstIds, languageCode, {
		preHook: UtilDWR.enterLoadingState,
		postHook: UtilDWR.exitLoadingState,
		callback: function(res) {
			dojo.lang.forEach(selectWidgetIDs, function(widgetId) {
				var selectWidget = dojo.widget.byId(widgetId);
                var listId = selectWidget.listId;
				var selectWidgetData = res[listId];
				
				// Sort list by the display values (array[0])
				// only if not sorted in backend, e.g. INSPIRE Themes (6100) !
				if (listId != 6100) {
                    selectWidgetData.sort(function(a, b) {
                        return UtilString.compareIgnoreCase(a[0], b[0]);
                    });
				}
				
				selectWidget.dataProvider.setData(selectWidgetData);	
			});
			def.callback();
		},
		errorHandler:function(mes){
			UtilDWR.exitLoadingState();
			dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
			dojo.debug("Error: "+mes);
			def.errback(mes);
		}
	});

	return def;
}


function initGeneralEventListener() {
	// Attach error messages to the DatePickers. If an invalid date is parsed, the user gets an error message.
	var datePickerIdList = ["ref1SymbolsDateDatePicker", "ref1KeysDateDatePicker", "timeRefDateDatePicker",
			"timeRefDate1", "timeRefDate2", "extraInfoConformityDatePicker"];

	var adviceFunc = function(input) {
		this.clearValue();
		dialog.show(message.get("general.error"), "Das von Ihnen eingegebene Datum '"+input+"' ist ung&uuml;ltig und wird zur&uuml;ckgesetzt.", dialog.WARNING);			
	}

	dojo.lang.forEach(datePickerIdList, function(datePickerId) {
	    var datePicker = dojo.widget.byId(datePickerId);

	    var kwArgs = { adviceType: "after", srcObj: datePicker, srcFunc: "onInvalidInput", adviceObj: datePicker,
	    			   adviceFunc: adviceFunc, once: true }
	
		dojo.event.kwConnect(kwArgs);
	});

	// Disable backspace default behaviour (browser back button)
	if (dojo.render.html.ie) {
		// Registering IE onkeydown with dojo doesn't work as it should
		// Therefore we need to directly register with the documents onkeydown function
		document.onkeydown = function(e) {
			var evt = (e) ? e : window.event; 
			dojo.event.browser.fixEvent(evt);
			if (evt.keyCode == evt.KEY_BACKSPACE) {
				var tagName = evt.target.tagName.toLowerCase();
				if (!(tagName == 'input') && !(tagName == 'textarea')) {
//					dojo.debug("Preventing backspace default behaviour on "+evt.target);
					evt.preventDefault();
				}

			} else if (evt.keyCode == evt.KEY_F5) {			
				dialog.show(message.get("general.hint"), message.get("dialog.browserFunctionDisabled"), dialog.INFO);

			    evt.keyCode = 0;
			    evt.cancelBubble = true;
			    evt.returnValue = false;
			    return false;
			}
		}

	} else {
		dojo.event.browser.addListener(document, "onkeydown", function(evt){
			if (evt.keyCode == evt.KEY_BACKSPACE) {
				if (!(evt.target instanceof HTMLInputElement) && !(evt.target instanceof HTMLTextAreaElement)) {
//					dojo.debug("Preventing backspace default behaviour on "+evt.target);
					evt.preventDefault();
				}

			} else if (evt.keyCode == evt.KEY_F5) {			
				dialog.show(message.get("general.hint"), message.get("dialog.browserFunctionDisabled"), dialog.INFO);
				evt.preventDefault();
			}
		});
	}


	// Catch the window close event
	window.onbeforeunload = function(evt){
		if (dojo.render.html.ie) {
			// Catch clicks on the upper left and upper right corner. Also catch clicks on the app's 'close' button.
			if ( (event.clientY < 0 && (event.clientX > (document.documentElement.clientWidth - 15) || event.clientX < 15))
			   ||(event.clientY < 23 && event.clientX > document.documentElement.clientWidth - 172 && event.clientX < document.documentElement.clientWidth - 9)
			   ||(event.clientY < -30 && event.clientY > -60 && event.clientX > 0 && event.clientX < 60)) {
		  		event.returnValue = message.get("general.closeWindow");
			}

		} else {
			return message.get("general.closeWindow");
		}
	}
}

function _enableInputElement(widgetId) {
	var widget = dojo.widget.byId(widgetId);
	
	// Check if the enable method exists
	if (widget.enable) {
		// If it does, enable the widget
    	widget.enable();

	} else {
		dojo.debug("Can't enable widget "+widgetId+". Method not implemented for "+widget);
	}
}

function _disableInputElement(widgetId) {
	var widget = dojo.widget.byId(widgetId);

	// Check if the disable method exists
	if (widget.disable) {
		// If it does, disable the widget
    	widget.disable();

	} else {
		dojo.debug("Can't disable widget "+widgetId+". Method not implemented for "+widget);
	}
}

function _disableHtmlLink(elementId) {
	var element = dojo.byId(elementId);
	
	if (element.onClick) {
		element._disabledOnClick = element.onClick;
		element.onClick = null;

	} else if (element.onclick) {
		element._disabledOnClick = element.onclick;
		element.onclick = null;
	}
}

function _enableHtmlLink(elementId) {
	var element = dojo.byId(elementId);
	
	if (element._disabledOnClick) {
		element.onclick = element._disabledOnClick;
		element._disabledOnClick = null;
	}
}

function disableInputOnWrongPermission() {
    var treeListener = dojo.widget.byId("treeListener");

	var htmlLinks = ["generalAddressTableLink", "spatialRefAdminUnitLink", "spatialRefLocationLink", "thesaurusTermsLink", "thesaurusTermsNavigatorLink",
					 "ref1AddSymbolsLink", "ref1AddKeysLink", "ref1AddServiceLink", "ref1AddBasisLink", "ref1AddDataBasisLink", "ref1AddProcessLink",
					 "ref2AddLocationLink", "ref2AddBaseDataLink", "ref3AddBaseDataLink", "ref4AddParticipantsLink", "ref4AddPMLink", "ref5AddMethodLink"];

	UtilUI._uiElementsActive = true;

    dojo.event.topic.subscribe(treeListener.eventNames.select, function(message) {
		var hasWritePermission = message.node.userWritePermission;
//		var hasWriteSinglePermission = message.node.userWriteSinglePermission;
//		var hasWriteTreePermission = message.node.userWriteTreePermission;
//		dojo.debug("hasWritePermission: "+hasWritePermission);

		if (hasWritePermission) {
			if (!UtilUI._uiElementsActive) {
				// Enable all input elements
				dojo.lang.forEach(headerUiInputElements, _enableInputElement);
				dojo.lang.forEach(generalUiInputElements, _enableInputElement);
				dojo.lang.forEach(spatialUiInputElements, _enableInputElement);
				dojo.lang.forEach(timeUiInputElements, _enableInputElement);
				dojo.lang.forEach(extraUiInputElements, _enableInputElement);
				dojo.lang.forEach(availUiInputElements, _enableInputElement);
				dojo.lang.forEach(thesUiInputElements, _enableInputElement);
				dojo.lang.forEach(class0UiInputElements, _enableInputElement);
				dojo.lang.forEach(class1UiInputElements, _enableInputElement);
				dojo.lang.forEach(class2UiInputElements, _enableInputElement);
				dojo.lang.forEach(class3UiInputElements, _enableInputElement);
				dojo.lang.forEach(class4UiInputElements, _enableInputElement);
				dojo.lang.forEach(class5UiInputElements, _enableInputElement);
				dojo.lang.forEach(class6UiInputElements, _enableInputElement);
			
				dojo.lang.forEach(adrUiInputElements, _enableInputElement);
				dojo.lang.forEach(adrClass0UiInputElements, _enableInputElement);
				dojo.lang.forEach(adrClass1UiInputElements, _enableInputElement);
				dojo.lang.forEach(adrClass2UiInputElements, _enableInputElement);
				dojo.lang.forEach(adrClass3UiInputElements, _enableInputElement);
	
				dojo.lang.forEach(htmlLinks, _enableHtmlLink);
	
				_enableInputElement("thesaurusFreeTermsAddButton");
				_enableInputElement("thesaurusFreeTermsAddressAddButton");
				UtilUI._uiElementsActive = true;
			}

		} else {
			if (UtilUI._uiElementsActive) {
				// Disable all input elements
				dojo.lang.forEach(headerUiInputElements, _disableInputElement);
				dojo.lang.forEach(generalUiInputElements, _disableInputElement);
				dojo.lang.forEach(spatialUiInputElements, _disableInputElement);
				dojo.lang.forEach(timeUiInputElements, _disableInputElement);
				dojo.lang.forEach(extraUiInputElements, _disableInputElement);
				dojo.lang.forEach(availUiInputElements, _disableInputElement);
				dojo.lang.forEach(thesUiInputElements, _disableInputElement);
				dojo.lang.forEach(class0UiInputElements, _disableInputElement);
				dojo.lang.forEach(class1UiInputElements, _disableInputElement);
				dojo.lang.forEach(class2UiInputElements, _disableInputElement);
				dojo.lang.forEach(class3UiInputElements, _disableInputElement);
				dojo.lang.forEach(class4UiInputElements, _disableInputElement);
				dojo.lang.forEach(class5UiInputElements, _disableInputElement);
				dojo.lang.forEach(class6UiInputElements, _disableInputElement);
			
				dojo.lang.forEach(adrUiInputElements, _disableInputElement);
				dojo.lang.forEach(adrClass0UiInputElements, _disableInputElement);
				dojo.lang.forEach(adrClass1UiInputElements, _disableInputElement);
				dojo.lang.forEach(adrClass2UiInputElements, _disableInputElement);
				dojo.lang.forEach(adrClass3UiInputElements, _disableInputElement);
	
				dojo.lang.forEach(htmlLinks, _disableHtmlLink);
	
				_disableInputElement("thesaurusFreeTermsAddButton");
				_disableInputElement("thesaurusFreeTermsAddressAddButton");
				UtilUI._uiElementsActive = false;
			}
		}
    });
}

function initOptionalFieldStates() {
	var def = getGuiIdList();

	def.addCallback(function(guiIdList) {
		dojo.lang.forEach(guiIdList, function(entry) {
			var guiId = "uiElement"+entry.id;
			var uiElement = dojo.byId(guiId);
			var dispType = "standard";

			if (uiElement) {
				if (entry.mode == "0") {
					uiElement.setAttribute("displaytype", "alwaysHide");
					dojo.html.hide(uiElement);

				} else if (entry.mode == "1") {
					uiElement.setAttribute("displaytype", "alwaysShow");
					dojo.html.show(uiElement);
				}
			}
		});
	});

	return def;
}

// Init session keepalive and autosave
function initSessionKeepalive() {

	//Init session keepalive
	var sessionKeepaliveDef = UtilCatalog.getSessionRefreshIntervalDef();

	sessionKeepaliveDef.addCallback(function(sessionKeepaliveInterval) {
		if (sessionKeepaliveInterval > 0) {
			var interval = sessionKeepaliveInterval * 60 * 1000;
			setInterval("UtilGeneral.refreshSession();", interval);
		}
	});

	// Init autosave
	var autosaveDef = UtilCatalog.getAutosaveIntervalDef();

	autosaveDef.addCallback(function(autosaveInterval) {
		// If the autosave Interval is set...
		if (autosaveInterval > 0) {
			// Calculate the time in milliseconds
			var autosaveIntervalTime = autosaveInterval * 60 * 1000;
	
			// autosaveTimer holds a reference to the 'timeout' object used by the javascript functions setTimeout, clearTimeout, ...
			var autosaveTimer = null;
			// autosave function that is executed every n minutes
			var autosaveFunction = "dojo.debug('autosave called.'); " +
										"if (udkDataProxy.dirtyFlag && " +
										"dojo.widget.byId('tree').selectedNode && " +
										"dojo.widget.byId('tree').selectedNode.userWritePermission && " +
										"!dialog.isGlassPaneVisible()) { " +
											"menuEventHandler.handleSave(); " +
									"}";

			// Functions to manipulate the autosaveTimer.
			var clearAutosaveInterval = function() {
				dojo.debug("clear autosave interval called.");
				if (autosaveTimer) {
					clearTimeout(autosaveTimer);
					autosaveTimer = null;
				}
			}
	
			var setAutosaveInterval = function() {
				dojo.debug("set autosave interval called.");
				if (autosaveTimer) {
					clearAutosaveInterval();
				}
				autosaveTimer = setInterval(autosaveFunction, autosaveIntervalTime);
			}
	
			var resetAutosaveInterval = function() {
				dojo.debug("reset autosave interval called.");
				clearAutosaveInterval();
				setAutosaveInterval();
			}

			// -- Connect the events with the autosaveTimer --
			// Reset the timer: when a dataset is loaded, stored, published or created
			dojo.event.connect(udkDataProxy, "handleLoadRequest", resetAutosaveInterval);
			dojo.event.connect(udkDataProxy, "handleSaveRequest", resetAutosaveInterval);
			dojo.event.connect(udkDataProxy, "handlePublishObjectRequest", resetAutosaveInterval);
			dojo.event.connect(udkDataProxy, "handleCreateObjectRequest", resetAutosaveInterval);
			dojo.event.connect(udkDataProxy, "handlePublishAddressRequest", resetAutosaveInterval);
			dojo.event.connect(udkDataProxy, "handleCreateAddressRequest", resetAutosaveInterval);
		}
	});
}

function jumpToNodeOnInit() {
	if (initJumpToNodeId.length != 0 && initJumpToNodeType.length != 0) {
		menuEventHandler.handleSelectNodeInTree(initJumpToNodeId, initJumpToNodeType);
	}
}

function getGuiIdList() {
	var deferred = new dojo.Deferred();

	// Get all gui ids
	CatalogService.getSysGuis(null, {
		callback: function(sysGuiList) {
			deferred.callback(sysGuiList);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
			deferred.errback(err);			
		}
	});

	return deferred;
}
