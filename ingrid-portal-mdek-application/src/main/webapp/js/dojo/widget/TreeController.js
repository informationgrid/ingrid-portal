dojo.provide("ingrid.widget.TreeController");

dojo.require("dojo.widget.TreeRpcControllerV3");

/**
 * TreeController handles node actions
 */
dojo.widget.defineWidget(
	"ingrid.widget.TreeController",
	dojo.widget.TreeRpcControllerV3,
{
  /*
   * store clicked node for later paste operation
   */
  nodeToCopy: null,
  copySubTree: false,
  nodeToCut: null,
  nodeToCutOldColor: "",


  /*
   * method overidden to select node after creation
   */
	finalizeCreateChild: function(parent, index, data) {
		this.finishProcessing(parent);

    // select the newly created node
    var newNode = parent.children[index];
    parent.tree.selectNode(newNode);
	},

  /*
   * open method
   */
	open: function(node) {
		return this.runStages(
			this.canOpen, this.prepareOpen, this.doOpen, this.finalizeOpen, this.exposeOpen, arguments
		);			
	},
	doOpen: function(node) {
    node.viewSelect();
	},

  /*
   * preview method
   */
	preview: function(node) {
		return this.runStages(
			this.canPreview, this.preparePreview, this.doPreview, this.finalizePreview, this.exposePreview, arguments
		);			
	},
	doPreview: function(node) {
//    node.viewSelect();
      alert("Vorschau");
	},

  /*
   * copy method
   */
	copy: function(node, /*boolean*/doDeep) {
		return this.runStages(
			this.canCopy, this.prepareCopy, this.doCopy, this.finalizeCopy, this.exposeCopy, arguments
		);			
	},
	prepareCopy: function(node, /*boolean*/copySubTree) {
	    dojo.debug("Prepare copy called with: "+node.id+", "+copySubTree);
	    this.nodeToCopy = node;
	    this.copySubTree = copySubTree;

	    // reset old cut node
	    if (this.nodeToCut != null) {
	      this.nodeToCut.labelNode.style.color = this.nodeToCutOldColor;
	      this.nodeToCut = null;
	    }
	},
  /*
   * cut method
   */
	cut: function(node) {
		return this.runStages(
			this.canCut, this.prepareCut, this.doCut, this.finalizeCut, this.exposeCut, arguments
		);			
	},
	prepareCut: function(node) {

    // reset old cut node
    if (this.nodeToCut != null) {
      this.nodeToCut.labelNode.style.color = this.nodeToCutOldColor;
    }
    this.nodeToCut = node;
    this.nodeToCutOldColor = this.nodeToCut.labelNode.style.color;
    this.nodeToCut.labelNode.style.color = "#888888";

    // reset old copy node
    if (this.nodeToCopy != null) {
      this.nodeToCopy = null;
    }
	},

  /*
   * paste method
   */
   // the passed 'node' argument is the target node
	canPaste: function(node) {
		var srcNode;
		if (this.nodeToCopy != null)
			srcNode = this.nodeToCopy;
		else if (this.nodeToCut != null)
			srcNode = this.nodeToCut;
		else
			return false;

		if (node != null) {
			if (node.id == "newNode")
				return false;

			if (node.nodeAppType == srcNode.nodeAppType) {
				if (node.nodeAppType == "O") {
					return true; // Objects can be pasted anywhere below objects
				} else if (node.nodeAppType == "A") {
					var srcType = srcNode.objectClass;
					var dstType = node.objectClass;					
					if (typeof(dstType) == "undefined") {
						// Target is either addressRoot or addressFreeRoot
						if (node.id == "addressFreeRoot") {
							return (srcType >= 2);	// Only Addresses can be converted to free addresses
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
	paste: function(node) {
		return this.runStages(
			this.canPaste, this.preparePaste, this.doPaste, this.finalizePaste, this.exposePaste, arguments
		);			
	},
	doPaste: function(node) {
    	// do a copy
    	if (this.nodeToCopy != null) {
     		this.clone(this.nodeToCopy, node, node.children.length, this.copySubTree/*, true*/);
    	}
    	// do a move
    	else if (this.nodeToCut != null) {
     		this.nodeToCut.labelNode.style.color = this.nodeToCutOldColor;
     		this.move(this.nodeToCut, node, node.children.length/*, true*/);
     		this.nodeToCut = null; // don't paste cut object twice
    	}
	},
  /*
   * move method
   */
	doMove: function(child, newParent, index, sync) {
		// Overriden so the TreeRPCController doMove method is skipped
		child.tree.move(child, newParent, index);

		this.nodeToCut.labelNode.style.color = this.nodeToCutOldColor;
		this.nodeToCut = null; // don't paste cut object twice

		return true;
	},
  /*
   * clone method
   */
	doClone: function(child, newParent, index, deep) {
		// Overriden so the TreeRPCController doMove method is skipped
		// TODO: implement when the corresponding method in the backend is implemented
		var cloned = child.clone(deep);
		newParent.addChild(cloned, index);

		return cloned;
	},
  /*
   * create child method
   */
	doCreateChild: function(parent, index, data) {
		var newChild = parent.tree.createNode(data); 
		if (index == "last") {
			parent.addChild(newChild);
		} else {
			parent.addChild(newChild, index);
		}
		return newChild;
	}
});
