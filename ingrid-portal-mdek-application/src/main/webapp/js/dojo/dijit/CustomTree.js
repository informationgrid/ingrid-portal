dojo.provide("ingrid.dijit.CustomTree");

dojo.require("dijit.Tree");

dojo.declare("ingrid.dijit._CustomTreeNode", dijit._TreeNode, {
	
    templateString: dojo.cache("ingrid.dijit", "templates/CustomTreeNode.html"),

    _updateItemClasses: function(item){
        // summary:
        //      Set appropriate CSS classes for icon and label dom node
        //      (used to allow for item updates to change respective CSS)
        // tags:
        //      private
        var tree = this.tree, model = tree.model;
        if(tree._v10Compat && item === model.root){
            // For back-compat with 1.0, need to use null to specify root item (TODO: remove in 2.0)
            item = null;
        }
        this._applyClassAndStyle(item, "preIcon", "PreIcon");
        this._applyClassAndStyle(item, "icon", "Icon");
        this._applyClassAndStyle(item, "label", "Label");
        this._applyClassAndStyle(item, "row", "Row");
    }
});

dojo.declare("ingrid.dijit.CustomTree", dijit.Tree, {
	// remember the node we want to copy/cut
	nodeToCopy: null,
	nodeToCut: null,
	copySubTree: false,

    _expandNode: function(node){
        var _this = this;
        var _arguments = arguments;
        if (node.state == "UNCHECKED") {
            var theTree = this;
            node.markProcessing();
            this.model.manualTreeNotification = true;
            var def = new dojo.Deferred();
            this.lazyLoadItems(node, function(){
                node.unmarkProcessing();
                _this.inherited(_arguments);
                _this.model.manualTreeNotification = false;
                // notification for updating newly added child nodes
                theTree.itemsAvailable(node, def, 3);
                //if (node.item.root != true)
                //    _this.model.notfiyOnNewChildren(node, def);
                
            });
        }
        else {
            var def2 = this.inherited(arguments);
        }
        
        if (def)
            return def;
        else
            return def2;
    },
    
    lazyLoadItems: function(node, callback_function){
        // Default implementation. User overridable function.
        callback_function();
    },
    
    // check if items are ready to be rendered
    // it can happen that the root node 
    itemsAvailable: function(node, def, iteration) {
        if (!this.model.newItemsAvailable && iteration > 0) {
            console.debug("try again later!");
            setTimeout(dojo.hitch(this, dojo.partial(this.itemsAvailable, node, def, iteration-1)), 300);
        } else {
            this.model.notfiyOnNewChildren(node, def);
        }
    },
	
    getPreIconClass: function(/*dojo.data.Item*/ item, /*Boolean*/ opened) {
        var myClass = "TreePreIcon";
        if (item.publicationCondition && item.publicationCondition[0] != null) {
            myClass += " TreePreIcon" + item.publicationCondition[0];
            if (item.userWritePermission && item.userWritePermission[0] == false)
                myClass += " IconDisabled";
        }
        return myClass;
    },
    
    getPreIconStyle: function(/*dojo.data.Item*/ item, /*Boolean*/ opened){
        // summary:
        //      Overridable function to return CSS styles to display preIcon
        // returns:
        //      Object suitable for input to dojo.style() like {color: "red", background: "green"}
        // tags:
        //      extension
    },

	getIconClass: function(/*dojo.data.Item*/ item, /*Boolean*/ opened) {
        var myClass = "TreeIcon " + "TreeIcon" + item.nodeDocType;
        // check explicitly if set to false ! (can also be null in top nodes ...)
        if (item.userWritePermission && item.userWritePermission[0] == false)
            myClass += " IconDisabled";
		return myClass;
	},
	
	getLabelClass: function(/*dojo.data.Item*/ item, /*Boolean*/ opened) {
		var myClass = "";
        if (item.labelClass)
            myClass = item.labelClass;
        // check explicitly if set to false ! (can also be null in top nodes ...)
        if (item.userWritePermission && item.userWritePermission[0] == false)
            myClass += " TreeNodeNotSelectable";
		return myClass;
	},
	
    getTooltip: function(/*dojo.data.Item*/ item){
    	var myTooltip = "";
        if (item.objectClass && item.objectClass[0] != null) {
        	var myMsgKey = "dialog.statistics.";
        	if (item.nodeAppType[0] == "O")
        	   myMsgKey += "objClass";
        	else
               myMsgKey += "adrClass";
            myMsgKey += item.objectClass[0];
            myTooltip = message.get(myMsgKey);
        }
        if (item.publicationCondition && item.publicationCondition[0] != null) {
            myMsgKey = "tooltip.publicationCondition." + item.publicationCondition[0];
            if (myTooltip.length > 0)
                myTooltip += ", ";
            myTooltip += message.get(myMsgKey);
        }
        return myTooltip;
    },

	mayHaveChildren: function(/*dojo.data.Item*/item){
		if (item.isFolder == "true") {
			return true;
		}
	},
	
	removeChildren: function(childrenItems) {
		dojo.forEach(childrenItems, function(childItem) {
			// check if child also has children to delete/update
			if (childItem.children && childItem.children.length > 0)
				this.removeChildren(childItem.children);
			this.model.store.deleteItem(childItem);
		}, this);
	},
	
	refreshChildren: function(/*TreeNode*/node) {
		// remove children from store first
		this.removeChildren(node.item.children);
		
		// we need to save the store otherwise we cannot add new items
		// with the same (previous) id!
		this.model.store.save();
		
		node.state = "UNCHECKED";
		
		// load remote/lazy again
		return this._expandNode(node);
	},
	
	prepareCopy: function(/*TreeNodeItem*/node, /*boolean*/copySubTree) {
		this.nodeToCopy = node;
		this.copySubTree = copySubTree;
		if (this.nodeToCut) {
			dijit.byId(this.nodeToCut.id[0]).set('class','');
			this.nodeToCut = null;
		}
        dojo.publish("/prepareCopy", [node]);
	},
	
	prepareCut: function(/*TreeNodeItem*/node) {
		// if another node was chosen for cut
		// -> reset it
		if (this.nodeToCut)
			dijit.byId(this.nodeToCut.id[0]).set('class','');
			
		this.nodeToCut = node;
		dijit.byId(this.nodeToCut.id[0]).set('class','nodeCut');
		this.nodeToCopy = null;
        dojo.publish("/prepareCut", [node]);
	},
	
	doPaste: function() {
		// cut only once!
		//if (this.nodeToCut) {
			// browser crashes here when tree is being refreshed
			// but this here shouldn't be necessary since the node is removed
			//dijit.byId(this.nodeToCut.id[0]).set('class',''); 
			this.nodeToCut = null;
		//}
	},
    
    _setPathAttr: function(/*Item[] || String[]*/ path){
        // summary:
        //      Select the tree node identified by passed path.
        // path:
        //      Array of items or item id's
        // returns:
        //      Deferred to indicate when the set is complete

        var d = new dojo.Deferred();

        this._selectNode(null);
        if(!path || !path.length){
            d.resolve(true);
            return d;
        }

        // If this is called during initialization, defer running until Tree has finished loading
        this._loadDeferred.addCallback(dojo.hitch(this, function(){
            if(!this.rootNode){
                d.reject(new Error("!this.rootNode"));
                return;
            }
            if(path[0] !== this.rootNode.item && (dojo.isString(path[0]) && path[0] != this.model.getIdentity(this.rootNode.item))){
                d.reject(new Error(this.id + ":path[0] doesn't match this.rootNode.item.  Maybe you are using the wrong tree."));
                return;
            }
            path.shift();

            var node = this.rootNode;

            function advance(){
                // summary:
                //      Called when "node" has completed loading and expanding.   Pop the next item from the path
                //      (which must be a child of "node") and advance to it, and then recurse.

                // Set item and identity to next item in path (node is pointing to the item that was popped
                // from the path _last_ time.
                var item = path.shift(),
                    identity = dojo.isString(item) ? item : this.model.getIdentity(item);

                // item cannot be found so try to open virtual node "addressFreeNode"
                if (this._itemNodesMap[identity] == undefined && !this._itemNodesMap["addressFreeRoot"][0].isExpanded) {
                    // add to front the modified path
                    path.splice(0, 0, item);
                    identity = "addressFreeRoot";
                }

                // Change "node" from previous item in path to the item we just popped from path
                dojo.some(this._itemNodesMap[identity], function(n){
                    if(n.getParent() == node){
                        node = n;
                        return true;
                    }
                    return false;
                });

                if(path.length){
                    // Need to do more expanding
                    this._expandNode(node).addCallback(dojo.hitch(this, advance));
                }else{
                    // Final destination node, select it
                    this._selectNode(node);
                    
                    // signal that path setting is finished
                    d.resolve(true);
                }
            }

            this._expandNode(node).addCallback(dojo.hitch(this, advance));
        }));
            
        return d;
    },
    
    canPaste: function(node) {
        var srcNode;
        if (this.nodeToCopy != null)
            srcNode = this.nodeToCopy;
        else if (this.nodeToCut != null)
            srcNode = this.nodeToCut;
        else
            return false;

        var dstIsRootNode = (node.id == "objectRoot" || node.id == "addressRoot" || node.id == "addressFreeRoot");

        if (node != null) {
            if (node.id == "newNode" || (!dstIsRootNode && (!node.userWriteTreePermission && !node.userWriteSubTreePermission && !node.userWriteSubNodePermission)))
                return false;

            if (dstIsRootNode && !UtilSecurity.canCreateRootNodes())
                return false;

            if (node.nodeAppType[0] == srcNode.nodeAppType[0]) {
                if (node.nodeAppType == "O") {
                    return true; // Objects can be pasted anywhere below objects
                } else if (node.nodeAppType == "A") {
                    var srcType = srcNode.objectClass;
                    var dstType = node.objectClass;                 
                    if (typeof(dstType) == "undefined" || dstType[0] == null) {
                        // Target is either addressRoot or addressFreeRoot
                        if (node.id == "addressFreeRoot") {
                            return (srcType >= 2);  // Only Addresses can be converted to free addresses
                        } else if (node.id == "addressRoot") {
                            return (srcType == 0); // Only Institutions are allowed below the root node
                        }
                    }
                    // The target node is no root node. Compare the src and dst types:
                    if (dstType >= 2 || (srcType == 0 && dstType == 1))
                        return false;
                    else
                        return true;
                    
                }
            } else {
                // src and destination type are not equal
                return false;
            }
        }
    },
	
    _createTreeNode: function(/*Object*/ args){
        // summary:
        //      creates a TreeNode
        // description:
        //      Developers can override this method to define their own TreeNode class;
        //      However it will probably be removed in a future release in favor of a way
        //      of just specifying a widget for the label, rather than one that contains
        //      the children too.
        return new ingrid.dijit._CustomTreeNode(args);
    }
});

//change of creation of tree so that every node has the uuid od the document
//this behaviour is adapted from dojo version 0.4
dojo.extend(dijit._TreeNode,{
  setChildItems : function(/* Object[] */items) {
   // summary:
      //      Sets the child items of this node, removing/adding nodes
      //      from current children to match specified items[] array.
      //      Also, if this.persist == true, expands any children that were previously
      //      opened.
      // returns:
      //      Deferred object that fires after all previously opened children
      //      have been expanded again (or fires instantly if there are no such children).

      var tree = this.tree,
          model = tree.model,
          defs = [];  // list of deferreds that need to fire before I am complete


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

      return new dojo.DeferredList(defs); // dojo.Deferred
}
});