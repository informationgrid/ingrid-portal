dojo.provide("ingrid.widget.Toolbar");

dojo.require("dojo.widget.Toolbar");
dojo.require("dojo.widget.Tooltip");

/**
 * Toolbar
 */
dojo.widget.defineWidget(
	"ingrid.widget.Toolbar",
	dojo.widget.Toolbar,
{
  /*
   * Additionally add a tooltip
   * Caption is given in props["caption"]
   */
	addChild: function(item, pos, props) {
		var toolbarItem = dojo.widget.Toolbar.prototype.addChild.apply(this, arguments);

    if (props) {
    	toolbarItem.menuFunction = props["onClick"];

		toolbarItem.onClick = function() {
			// If an element is active, blur it before executing the menu function
			// This needs to be done so the UI is updated with possible changes 
			if (!dojo.render.html.ie && document.activeElement && document.activeElement.blur) {
				document.activeElement.blur();
			}

			this.menuFunction();
		}

      // add the tooltip
      if (props["caption"]) {
        toolbarItem.domNode.setAttribute('title', props["caption"]);
//        toolbarItem.domNode.id = toolbarItem.widgetId;
//        var cssPath = dojo.uri.moduleUri("ingrid", "widget/templates/TooltipTemplate.css");
//        dojo.widget.createWidget("Tooltip", {connectId:toolbarItem.domNode.id,caption:props["caption"],templateCssPath:cssPath});
      }
    }
    return toolbarItem;
	},

  /*
   * Method to create separator item
   */
  addSeparator: function() {
	  var sep = this.addChild("|", "after");
	  sep.setIcon("img/ic_sep.gif");
    return sep;
  }
});

