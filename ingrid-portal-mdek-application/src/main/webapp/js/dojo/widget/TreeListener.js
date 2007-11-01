dojo.provide("ingrid.widget.TreeListener");

dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.TreeCommon");

/**
 * TreeListener listens to tree events, converts them to node events and publishes them
 */
dojo.widget.defineWidget(
	"ingrid.widget.TreeListener",
	[dojo.widget.HtmlWidget, dojo.widget.TreeCommon],
	function() {
		this.eventNames = {};
		this.listenedTrees = {};
		this.selectedNode = null;
	},
{
  /*
   * The tree events to listen to
   */
	listenTreeEvents: ["afterTreeCreate", "afterNavigate", "beforeTreeDestroy"],
	listenNodeFilter: function(elem) { return elem instanceof dojo.widget.Widget},	

  /*
   * The published node events
   */
	eventNamesDefault: {
		select: "select",
		deselect: "deselect",
		mouseover: "mouseover",
		mouseout: "mouseout"
	},

	initialize: function(args) {
		for(name in this.eventNamesDefault) {
			if (dojo.lang.isUndefined(this.eventNames[name])) {
				this.eventNames[name] = this.widgetId+"/"+this.eventNamesDefault[name];
			}
		}
	},

  /*
   * Event handler for events published by the tree
   */
	onAfterTreeCreate: function(message) {
		var tree = message.source;
		dojo.event.browser.addListener(tree.domNode, "onclick", dojo.lang.hitch(this, this.onClick));
		dojo.event.browser.addListener(tree.domNode, "onKey", dojo.lang.hitch(this, this.onKey));
		dojo.event.browser.addListener(tree.domNode, "onmouseover", dojo.lang.hitch(this, this.onMouseOver));
		dojo.event.browser.addListener(tree.domNode, "onmouseout", dojo.lang.hitch(this, this.onMouseOut));
	},
	onAfterNavigate: function(message) {
		this.processNode(message.node, message.event);
	},
	onBeforeTreeDestroy: function(message) {
		this.unlistenTree(message.source);
	},

  /*
   * Event handler for dom events
   */
	onClick: function(event) {
		var node = this.domElement2TreeNode(event.target);
    if (node)
		  this.processNode(node, event);
	},
	onKey: function(e) {
		if (!e.key || e.altKey) { return; }
		
		if (e.key == ' ') {
			var node = this.domElement2TreeNode(e.target);
			if (node){
  		  this.processNode(node, e);
			}
		}
	},
	onMouseOver: function(message) {
    var node = this.domElement2TreeNode(message.target);
    if (node)
		  dojo.event.topic.publish(this.eventNames.mouseover, {node:node} );
	},
	onMouseOut: function(message) {
    var node = this.domElement2TreeNode(message.target);
    if (node)
  		dojo.event.topic.publish(this.eventNames.mouseout, {node:node} );
	},

  /*
   * Conversion of tree events to node events
   */
  processNode: function(node, event) {
    if (event.type == "focus") {
      return;
    }
    if (node.actionIsDisabled(node.actions.SELECT)) {
      return;
    }

    // select the node if the node label is clicked
    if (dojo.html.hasClass(event.target, "TreeLabel")) {
      // select the node on the tree
      node.tree.selectNode(node);

      // publish the deselect event
      if (this.selectedNode != null)
        dojo.event.topic.publish(this.eventNames.deselect, {node:this.selectedNode, target:event.target} );

      // publish the select event
      dojo.event.topic.publish(this.eventNames.select, {node:node, target:event.target} );

      // store the selected node
      this.selectedNode = node;
    }
  }
});
