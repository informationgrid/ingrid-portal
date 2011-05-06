dojo.provide("ingrid.dijit.CustomTree");

dojo.require("dijit.Tree");

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
	
	getIconClass: function(/*dojo.data.Item*/ item, /*Boolean*/ opened) {
		return "TreeIcon " + "TreeIcon" + item.nodeDocType;
	},
	
	getLabelClass: function(/*dojo.data.Item*/ item, /*Boolean*/ opened) {
		if (item.labelClass)
			return item.labelClass;
		return "";
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
		this._expandNode(node);
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
    }
	
	
});
