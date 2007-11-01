dojo.provide("ingrid.widget.TreeDecorator");

dojo.require("dojo.widget.HtmlWidget");
dojo.require("ingrid.widget.TreeDecorator");

/**
 * TreeDecorator listens to tree node events and changes the visual
 * appearance accordingly.
 */
dojo.widget.defineWidget(
	"ingrid.widget.TreeDecorator",
	dojo.widget.HtmlWidget,
{
	/*
	 * The listener that publishes the node events
	 */
	listener: "",
	
	initialize: function() {
		this.listener = dojo.widget.byId(this.listener);
		
		dojo.event.topic.subscribe(this.listener.eventNames.select, this, "onSelect");
		dojo.event.topic.subscribe(this.listener.eventNames.deselect, this, "onDeselect");	
		dojo.event.topic.subscribe(this.listener.eventNames.mouseover, this, "onMouseOver");
		dojo.event.topic.subscribe(this.listener.eventNames.mouseout, this, "onMouseOut");	
	},
	
	onSelect: function(message) {
		message.node.viewSelect();
	},
	
	onDeselect: function(message) {
		message.node.viewUnselect();
	},
	
	onMouseOver: function(message) {
		message.node.viewMouseOver();
	},
	
	onMouseOut: function(message) {
		message.node.viewMouseOut();
	}
});
