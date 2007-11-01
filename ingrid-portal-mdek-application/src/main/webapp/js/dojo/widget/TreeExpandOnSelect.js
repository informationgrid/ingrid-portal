dojo.provide("ingrid.widget.TreeExpandOnSelect");

dojo.require("dojo.widget.HtmlWidget");

/**
 * TreeExpandOnSelect listens to tree node events and expands the nodes when they are selected
 * Add like this:  <div dojoType="ingrid:TreeExpandOnSelect" listener="treeListener" controller="treeController"></div>	
 */
dojo.widget.defineWidget(
	"ingrid.widget.TreeExpandOnSelect",
	dojo.widget.HtmlWidget,
{
	/*
	 * The listener that publishes the node events
	 */
	listener: "",

	/*
	 * The controller used to expand the nodes
	 */
	controller: "",
	
	initialize: function() {
		this.listener = dojo.widget.byId(this.listener);
		this.controller = dojo.widget.byId(this.controller);
		
		dojo.event.topic.subscribe(this.listener.eventNames.select, this, "onSelect");
	},
	
	onSelect: function(message) {
		var node = message.node
		// do nothing, if the icon is clicked, because the expand/collapse 
		// action is already executed in that case
		if (message.target.className.indexOf('TreeExpand') == -1) {
  		node.isExpanded ? this.controller.collapse(node) : this.controller.expand(node)	
    }
	}
});

