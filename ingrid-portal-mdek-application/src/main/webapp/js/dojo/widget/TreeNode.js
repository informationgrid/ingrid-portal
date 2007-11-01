dojo.provide("ingrid.widget.TreeNode");

dojo.require("dojo.widget.TreeNodeV3");

/**
 * TreeNode represents a node in the tree
 */
dojo.widget.defineWidget(
	"ingrid.widget.TreeNode",
	dojo.widget.TreeNodeV3,
{
  /*
   * Add some the actions
   */
	actions: {
    MOVE: "MOVE",
    DETACH: "DETACH",
    EDIT: "EDIT",
    ADDCHILD: "ADDCHILD",
    SELECT: "SELECT",
    OPEN: "OPEN",
    COPY: "COPY",
    CUT: "CUT",
    PASTE: "PASTE"
	},

	/*
	 * The nodes context menu
	 */
	contextMenu: "",

	/*
	 * The nodes application specific type
	 */
	nodeAppType: "",

	/*
	 * Indicates wether the node has a checkbox
	 */
	hasCheckbox: false,

  /*
   * Add the contextMenu property to the properties to clone
   */
	cloneProperties: ["actionsDisabled","tryLazyInit","nodeDocType","objectId","object",
		   "title","isFolder","isExpanded","state","contextMenu","hasCheckbox"],

  /*
   * Replace the title with checkbox + title
   */ 
  buildRendering: function(/*Object*/args, /*Object*/frag, /*Widget*/parent) {
    dojo.widget.TreeNodeV3.prototype.buildRendering.apply(this, arguments);
    if (this.hasCheckbox)
      this.labelNode.innerHTML = "<input dojoAttachPoint='checkbox' class='treeCheckbox' type='checkbox'>"+this.labelNode.innerHTML+"</input>";
  },

	viewSelect: function() {
		dojo.html.addClass(this.labelNode, this.tree.classPrefix+"NodeSelect");
	},
	
	viewUnselect: function() {
		dojo.html.removeClass(this.labelNode, this.tree.classPrefix+"NodeSelect");
	},

	viewMouseOver: function() {
		dojo.html.addClass(this.labelNode, this.tree.classPrefix+"NodeHover");
	},
	
	viewMouseOut: function() {
		dojo.html.removeClass(this.labelNode, this.tree.classPrefix+"NodeHover");
	},

  /*
   * Get the node info send to the server
   */
  getInfo: function() {
    var info = {
      widgetId: this.widgetId,
      objectId: this.objectId,
      contextMenu: this.contextMenu,
      nodeAppType: this.nodeAppType,
      index: this.getParentIndex()
    }
    return info;
  },

  /*
   * Checkbox handling
   */
  isChecked: function() {
    return this.checkbox.checked;
  },
  
  setChecked: function(value) {
    this.checkbox.checked = value;
  }
});
