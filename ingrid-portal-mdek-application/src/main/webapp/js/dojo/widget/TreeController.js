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
  doCopyDeep: false,
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
    node.viewSelect();
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
	doCopy: function(node, /*boolean*/doDeep) {
    this.nodeToCopy = node;
    this.doCopyDeep = doDeep;

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
	doCut: function(node) {

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
	canPaste: function(node) {
		if (this.nodeToCopy == null && this.nodeToCut == null) { return false; }
		return true;
	},
	paste: function(node) {
		return this.runStages(
			this.canPaste, this.preparePaste, this.doPaste, this.finalizePaste, this.exposePaste, arguments
		);			
	},
	doPaste: function(node) {
    // do a copy
    if (this.nodeToCopy != null) {
      this.clone(this.nodeToCopy, node, node.children.length, this.doCopyDeep/*, true*/);
    }
    // do a move
    else if (this.nodeToCut != null) {
      this.nodeToCut.labelNode.style.color = this.nodeToCutOldColor;
      this.move(this.nodeToCut, node, node.children.length/*, true*/);
      this.nodeToCut = null; // don't paste cut object twice
    }
	},
	/**
	 * Load children of the node from server
	 * Synchroneous loading doesn't break control flow
	 * I need sync mode for DnD
	 *
	 * Overwritten to work with dwr.
	*/
	loadRemote: function(node, sync){
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();
		
		EntryService.getSubTree(node.id, node.nodeAppType, 1, {
  			callback:function(res) { deferred.callback(res); },
			timeout:5000,
			errorHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); },
			exceptionHandler:function(message) { deferred.errback(new dojo.RpcError(message, this)); }
  		});
		
		deferred.addCallback(function(res) { return _this.loadProcessResponse(node,res); });
		deferred.addErrback(function(res) { alert(res.message); });
		return deferred;

	}
});
