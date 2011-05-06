dojo.provide("ingrid.dijit.LazyTree");

dojo.require("dijit.Tree");

dojo.declare("ingrid.dijit.LazyTree", dijit.Tree, {
	
	/*
	 * Called when a node is expanded.
	 */
	_loadSubTree: function(/*Item*/item ) {
		var deferred = new dojo.Deferred();
		console.debug("item to load subtree from: " + item.id + ":" + item.nodeAppType);
		TreeService.getSubTree(item.id + "", item.nodeAppType + "", {
			callback: function(data){
				treeStore = dijit.byId('dataTree').model.store;
				dojo.forEach(data, function(node){
					treeStore.newItem(node, {
						parent: item,
						attribute: "children"
					});
				});
				deferred.callback();
			},
            errorHandler: function(errMsg, err) {
                console.debug("Tree node could not be expanded!");
                console.error(err);
                deferred.errback();
            }
		});
		return deferred;
	},
	
	/*
	 * Overwrite _expandNode to load data lazily.
	 * Check if the node to expand already was loaded and only load subtree
	 * if it wasn't.
	 */
	_expandNode: function(/*_TreeNode*/node, /*Boolean?*/ recursive){
		// summary:
		//		Called when the user has requested to expand the node
		// recursive:
		//		Internal flag used when _expandNode() calls itself, don't set.
		// returns:
		//		Deferred that fires when the node is loaded and opened and (if persist=true) all it's descendants
		//		that were previously opened too
		
		//alert("MyExpandNode Event: " + node.item.id + " -> " + node.state);
		if (node.item.id != "root" && node.state == "UNCHECKED") {
			// mark the node as loading first
			node.markProcessing();
			
			// add children to the model first but only if not already done!
			var def = this._loadSubTree(node.item);
			
			// remove loading process from node
			def.then(function() { node.unmarkProcessing();});
			
			var thisTree = this;
			
			// this doesn't work unfortunately!
			//def.then(dojo.partial(this.inherited, node, recursive));
			def.then(dojo.partial(this._expandNodeInherited, node, recursive, this));
			return def;
		} else {
			return this.inherited(arguments);
		}
	},
	
	/*
	 * This function is copied from original Tree.js since it seems not possible to
	 * call an inherited method with parameters from a Deferred!
	 * The this-object also changes why we have to add it as a parameter.
	 */
	_expandNodeInherited: function(/*_TreeNode*/node, /*Boolean?*/ recursive, /*thisObject*/ thisObject) {
		//alert("in copied function with node: " + node);
		console.debug("in inherited: " + node.id + " ... " + node.state);
		if(node._expandNodeDeferred && !recursive){
			// there's already an expand in progress (or completed), so just return
			return node._expandNodeDeferred;	// dojo.Deferred
		}

		var model = this.model,
			item = node.item,
			_this = this;

		switch(node.state){
			case "UNCHECKED":
				console.debug("node is unchecked!");
				// need to load all the children, and then expand
				node.markProcessing();
				// Setup deferred to signal when the load and expand are finished.
				// Save that deferred in this._expandDeferred as a flag that operation is in progress.
				var def = (node._expandNodeDeferred = new dojo.Deferred());

				// Get the children
				model.getChildren(
					item,
					function(items){
						node.unmarkProcessing();

						// Display the children and also start expanding any children that were previously expanded
						// (if this.persist == true).   The returned Deferred will fire when those expansions finish.
						console.debug("call setChildItems");
						var scid = this.setChildItems(items);

						// Call _expandNode() again but this time it will just to do the animation (default branch).
						// The returned Deferred will fire when the animation completes.
						// TODO: seems like I can avoid recursion and just use a deferred to sequence the events?
						var ed = _this._expandNodeInherited(node, true);

						// After the above two tasks (setChildItems() and recursive _expandNode()) finish,
						// signal that I am done.
						scid.addCallback(function(){
							ed.addCallback(function(){
								def.callback();
							})
						});
					},
					function(err){
						console.error(_this, ": error loading root children: ", err);
					}
				);
				break;

			default:	// "LOADED"
				// data is already loaded; just expand node
				def = (node._expandNodeDeferred = node.expand());

				thisObject.onOpen(node.item, node);

				if(item){
					thisObject._state(item, true);
					thisObject._saveState();
				}
		}

		return def;	// dojo.Deferred
	},
	
	/*
	 * Return the correct css-class for the icon to be displayed, depending
	 * on the type of object/address
	 */
	getIconClass: function(/*dojo.data.Item*/ item, /*Boolean*/ opened) {
		return "TreeIcon " + "TreeIcon" + item.nodeDocType;
	},
	
	// need to overwrite to set the widget id of a new created node
	setChildItems: function(/* Object[] */ items){
		// summary:
		//		Sets the child items of this node, removing/adding nodes
		//		from current children to match specified items[] array.
		//		Also, if this.persist == true, expands any children that were previously
		// 		opened.
		// returns:
		//		Deferred object that fires after all previously opened children
		//		have been expanded again (or fires instantly if there are no such children).

		console.debug("in setChildItems");
		var tree = this.tree,
			model = tree.model,
			defs = [];	// list of deferreds that need to fire before I am complete


		// Orphan all my existing children.
		// If items contains some of the same items as before then we will reattach them.
		// Don't call this.removeChild() because that will collapse the tree etc.
		dojo.forEach(this.getChildren(), function(child){
			dijit._Container.prototype.removeChild.call(this, child);
		}, this);

		this.state = "LOADED";

		if(items && items.length > 0){
			this.isExpandable = true;

			// Create _TreeNode widget for each specified tree node, unless one already
			// exists and isn't being used (presumably it's from a DnD move and was recently
			// released
			dojo.forEach(items, function(item){
				var id = model.getIdentity(item),
					existingNodes = tree._itemNodesMap[id],
					node;
				if(existingNodes){
					for(var i=0;i<existingNodes.length;i++){
						if(existingNodes[i] && !existingNodes[i].getParent()){
							node = existingNodes[i];
							node.set('indent', this.indent+1);
							break;
						}
					}
				}
				if(!node){
					node = this.tree._createTreeNode({
							id: item.id,
							item: item,
							tree: tree,
							isExpandable: model.mayHaveChildren(item),
							label: tree.getLabel(item),
							tooltip: tree.getTooltip(item),
							dir: tree.dir,
							lang: tree.lang,
							indent: this.indent + 1
						});
					if(existingNodes){
						existingNodes.push(node);
					}else{
						tree._itemNodesMap[id] = [node];
					}
				}
				this.addChild(node);

				// If node was previously opened then open it again now (this may trigger
				// more data store accesses, recursively)
				if(this.tree.autoExpand || this.tree._state(item)){
					defs.push(tree._expandNode(node));
				}
			}, this);

			// note that updateLayout() needs to be called on each child after
			// _all_ the children exist
			dojo.forEach(this.getChildren(), function(child, idx){
				child._updateLayout();
			});
		}else{
			this.isExpandable=false;
		}

		if(this._setExpando){
			// change expando to/from dot or + icon, as appropriate
			this._setExpando(false);
		}

		// Set leaf icon or folder icon, as appropriate
		this._updateItemClasses(this.item);

		// On initial tree show, make the selected TreeNode as either the root node of the tree,
		// or the first child, if the root node is hidden
		if(this == tree.rootNode){
			var fc = this.tree.showRoot ? this : this.getChildren()[0];
			if(fc){
				fc.setFocusable(true);
				tree.lastFocused = fc;
			}else{
				// fallback: no nodes in tree so focus on Tree <div> itself
				tree.domNode.setAttribute("tabIndex", "0");
			}
		}

		return new dojo.DeferredList(defs);	// dojo.Deferred
	}
	
});
		