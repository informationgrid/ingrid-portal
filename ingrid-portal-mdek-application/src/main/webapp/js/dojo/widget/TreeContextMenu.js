dojo.provide("ingrid.widget.TreeContextMenu");

dojo.require("dojo.widget.TreeContextMenuV3");
dojo.require("dojo.widget.Dialog");
dojo.require("dojo.string");
dojo.require("ingrid.widget.TreeNode");

/**
 * TreeContextMenu provides the context menu for tree nodes
 */
dojo.widget.defineWidget(
	"ingrid.widget.TreeContextMenu",
	dojo.widget.TreeContextMenuV3,
{
	templateCssPath: dojo.uri.moduleUri("ingrid", "widget/templates/Menu2.css"),

  /*
   * Overidden to open the menu only, when the menu belongs to the node
   */
	open: function() {
    var result = null;
    var node = this.getTreeNode();
    if (this.widgetId == node.contextMenu) {

      // disable paste if not possible
      node.actionsDisabled = [];
      if (dojo.lang.isObject(this.treeController) && !this.treeController.canPaste(node))
        node.actionsDisabled.push("PASTE");

      if (node.nodeAppType == "A" && (node.objectClass == 2 || node.objectClass == 3) ) {
		  node.actionsDisabled.push("ADDCHILD");
      }

      if (node.id == "newNode") {
		  node.actionsDisabled.push("MOVE");
		  node.actionsDisabled.push("ADDCHILD");
		  node.actionsDisabled.push("COPY");
		  node.actionsDisabled.push("CUT");
		  node.actionsDisabled.push("PASTE");
      }

	  if (!node.userWritePermission && node.id != "objectRoot" && node.id != "addressRoot" && node.id != "addressFreeRoot") {
	  	node.actionsDisabled.push("MOVE");
	  	node.actionsDisabled.push("ADDCHILD");
	  	node.actionsDisabled.push("CUT");
	  	node.actionsDisabled.push("PASTE");
	  	node.actionsDisabled.push("DETACH");
	  }

	  if (!UtilSecurity.canCreateRootNodes() && (node.id == "objectRoot" || node.id == "addressRoot" || node.id == "addressFreeRoot")) {
	  	node.actionsDisabled.push("ADDCHILD");        
	  }

      result = dojo.widget.TreeContextMenuV3.prototype.open.apply(this, arguments);
    }
    return result;
	},

  /*
   * The controller that handles the context menu actions
   */
  treeController: "",

  /*
   * Methods to create menu items
   */
  addItem: function(caption, treeAction, method) {
    // create the menu item
    var item = dojo.widget.createWidget("TreeMenuItemV3", {widgetId:this.widgetId+caption,treeActions:[treeAction],caption:caption});
    item.initialize();
    this.addChild(item);
    // bind the item event to the controller
  	dojo.event.topic.subscribe(this.widgetId+caption+'/engage', this, method);
  },

  /*
   * Methods to create separator items
   */
  addSeparator: function() {
    // create the separator item
    var item = dojo.widget.createWidget("MenuSeparator2");
    item.initialize();
    this.addChild(item);
  },

  /*
   * Menu item event handler
   */
  createItemClicked: function(menuItem) {
		var node = menuItem.getTreeNode();
		this.reportIfDefered(this.treeController.createChild(node, 0, {title:node.nodeAppType}));
  },

  previewItemClicked: function(menuItem) {
    var node = menuItem.getTreeNode();
    this.reportIfDefered(this.treeController.preview(node));
  },

  cutItemClicked: function(menuItem) {
    var node = menuItem.getTreeNode();
    this.reportIfDefered(this.treeController.cut(node));
  },

  copySingleItemClicked: function(menuItem) {
    var node = menuItem.getTreeNode();
    this.reportIfDefered(this.treeController.copy(node, false));
  },

  copyItemClicked: function(menuItem) {
    var node = menuItem.getTreeNode();
    this.reportIfDefered(this.treeController.copy(node, true));
  },

  pasteItemClicked: function(menuItem) {
    var parent = menuItem.getTreeNode();
    this.reportIfDefered(this.treeController.paste(parent));
  },

  markDeletedItemClicked: function(menuItem) {
		var node = menuItem.getTreeNode();
    var _this = this;
		dialog.show(
        message.get('tree.nodeMarkDeleted'),
        dojo.string.substituteParams(message.get('tree.confirmMarkDeleted'), node.title.replace(/(<([^>]+)>)/ig," ")),
        dialog.WARNING, [
          {caption:message.get('general.ok'),action:dialog.CLOSE_ACTION},
          {caption:message.get('general.cancel'),action:dialog.CLOSE_ACTION}
        ]
    );
  },

  deleteItemClicked: function(menuItem) {
		var node = menuItem.getTreeNode();
    var _this = this;
		dialog.show(
        message.get('general.delete'),
        dojo.string.substituteParams(message.get('tree.confirmDelete'), node.title.replace(/(<([^>]+)>)/ig," ")),
        dialog.WARNING, [
          {caption:message.get('general.ok'),action:function(){_this.reportIfDefered(_this.treeController.destroyChild(node))}},
          {caption:message.get('general.cancel'),action:dialog.CLOSE_ACTION}
        ]
    );
  },

  /*
   * Helper method copied from TreeDemo
   */
	reportIfDefered: function(res) {
		if (res instanceof dojo.Deferred) {			
			res.addCallbacks(
				function(res) { /* dojo.debug("OK " + (res ? res: '')); */ return res },
				function(err) { dialog.show(message.get('general.error'), message.get('general.error')+": "+err.message, dialog.WARNING); /* dojo.debugShallow(err); */ }
			);
		}		
	}
	
});
