dojo.provide("ingrid.widget.ContextMenu");

dojo.require("dojo.widget.Menu2");

/**
 * Context menu for widgets that implement a getValue method
 * usage:
 *
 * var contextMenu = dojo.widget.createWidget("ingrid:ContextMenu");
 * contextMenu.addItemObject({caption:message.get('general.showAddress'), method:function(menuItem){...}});
 * contextMenu.bindDomNode(dojo.byId('last_editor')); // bind the menu to the HTML element with id last_editor
 *
 */
dojo.widget.defineWidget(
	"ingrid.widget.ContextMenu",
	dojo.widget.PopupMenu2,
{
	templateCssPath: dojo.uri.moduleUri("ingrid", "widget/templates/Menu2.css"),

  /*
   * Methods to create menu items
   */
  addItemObject: function(itemDef) {
    this.addItem(itemDef.caption, itemDef.method, itemDef.params);
  },

  addItem: function(caption, method, /* optional */params) {
    // create the menu item
    var item = dojo.widget.createWidget("MenuItem2", {widgetId:this.widgetId+caption,caption:caption});
    // mixin additional parameters
    if (params)
      dojo.lang.mixin(item, params);
    item.initialize();
    this.addChild(item);
    // bind the item event to the controller
  	dojo.event.topic.subscribe(this.widgetId+caption+'/engage', this, method);
  },

  /*
   * Method to create separator items
   */
  addSeparator: function() {
    // create the separator item
    var item = dojo.widget.createWidget("MenuSeparator2");
    item.initialize();
    this.addChild(item);
  },

  /*
   * Change enabled state
   */
  disableItem: function(caption) {
		var subItems = this.getChildrenOfType(dojo.widget.MenuItem2);
		for(var i=0; i<subItems.length; i++) {
      if (subItems[i].caption == caption) {
        subItems[i].setDisabled(true);
      }
    }
  },

  enableItem: function(caption) {
	  var subItems = this.getChildrenOfType(dojo.widget.MenuItem2);
	  for(var i=0; i<subItems.length; i++) {
		  if (subItems[i].caption == caption) {
			  subItems[i].setDisabled(false);
		  }
	  }
	},

  enableAllItem: function() {
		var subItems = this.getChildrenOfType(dojo.widget.MenuItem2);
    for(var i=0; i<subItems.length; i++) {
      subItems[i].setDisabled(false);
    }
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
