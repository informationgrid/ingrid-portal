dojo.provide("ingrid.widget.TabContainer");

dojo.require("dojo.widget.TabContainer");

/**
 * TabContainer
 */
dojo.widget.defineWidget(
	"ingrid.widget.TabContainer",
	dojo.widget.TabContainer,
{
  templateCssPath: dojo.uri.moduleUri("ingrid", "widget/templates/TabContainer.css"),

	postCreate: function(args, frag) {	
		dojo.widget.TabContainer.prototype.postCreate.apply(this, arguments);

		// attach header hide/show method on tab change
    dojo.event.topic.subscribe(this.widgetId+"-selectChild", this, "onSelectChild");
    
    // initialize header display
    this.onSelectChild(dojo.widget.byId(this.selectedChild));
	},

  destroy: function() {
    dojo.event.topic.unsubscribe(this.widgetId+"-selectChild", this, "onSelectChild");
  },

  onSelectChild: function(/*Widget*/ page) {
    // show header for the selected tab page and hide others
    var headerName = page.widgetId+'Header';
    for(var i=0; i<this.children.length; i++){
      var curHeaderName = this.children[i].widgetId+'Header';
      var link = dojo.byId(curHeaderName);
      if (link != null) {
        if (curHeaderName == headerName)
          link.style.display = 'block';
        else
          link.style.display = 'none';
      }
    }
  }
});
