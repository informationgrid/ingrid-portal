dojo.provide("ingrid.widget.Tree");

dojo.require("dojo.widget.TreeV3");

/**
 * Tree represents the tree
 */
dojo.widget.defineWidget(
	"ingrid.widget.Tree",
	dojo.widget.TreeV3,
	function() {
		this.selectedNode = null;
	},
{
  templateCssPath: dojo.uri.moduleUri("ingrid", "widget/templates/Tree.css"),

  defaultChildWidget: "ingrid.widget.TreeNode",

  selectedNode: null,

  /*
   * Overriden to take this.defaultChildWidget into account
   */
  initialize: function(args) {
    if (!args.defaultChildWidget && this.defaultChildWidget)
      args.defaultChildWidget = this.defaultChildWidget;

    dojo.widget.TreeV3.prototype.initialize.apply(this, arguments);
  },

  /*
   * Select a tree node. Parameter is either a node instance or a path of widget ids e.g. 'o1/o2/o3/o31/o311'
   * In case of a path parameter the select event is published (it's assumed that no user interaction
   * triggered the event before)
   */
  selectNode: function(/* path of widget ids or TreeNode */node) {
    if (typeof node == "string") {
      // find the node for the widgetId
    	var ids = node.split(/\//g);
      if (ids.length > 0) {
        // expand all nodes
      	dojo.lang.forEach(ids, function(curId) {
          var curNode = dojo.widget.byId(curId);
          if (curNode)
            curNode.expand();
        });
        // select last node
        this.selectNode(dojo.widget.byId(ids[ids.length-1]));
        // publish the select event
        // TODO Does this event exist?        
        dojo.event.topic.publish(this.eventNames.select, {node:this.selectedNode, event:{target:this.selectedNode.domNode}} );
    	}
    }
    else {
      if (this.selectedNode != node) {
        if (this.selectedNode != null)
          this.selectedNode.viewUnselect();
        node.viewSelect();
        this.selectedNode = node;
      }
    }
  }
});
