dojo.provide("ingrid.widget.FloatingPane");

dojo.require("dojo.widget.FloatingPane");

/**
 * FloatingPane
 */
dojo.widget.defineWidget(
	"ingrid.widget.FloatingPane",
	dojo.widget.FloatingPane,
{
  cacheContent: false,
  templateCssPath: dojo.uri.moduleUri("ingrid", "widget/templates/FloatingPane.css"),

  loadingMessage: "Lade Maske...",

  /*
   * Custom parameters
   */
  customParams: {},

  fillInFloatingPaneTemplate: function(args, frag){
    // call parent class method
    dojo.widget.FloatingPane.prototype.fillInFloatingPaneTemplate.apply(this, arguments);    

		if(this.hasShadow){
      // change image path
			this.shadow.shadowPng = dojo.uri.moduleUri("ingrid", "widget/templates/images/shadow");
    	this.shadow.shadowThickness = 5;
	    this.shadow.shadowOffset = 0;
			// re-init with new image path
			this.domNode.removeChild(this.shadow.pieces["tl"]);
			this.domNode.removeChild(this.shadow.pieces["l"]);
			this.domNode.removeChild(this.shadow.pieces["tr"]);
			this.domNode.removeChild(this.shadow.pieces["r"]);
			this.domNode.removeChild(this.shadow.pieces["bl"]);
			this.domNode.removeChild(this.shadow.pieces["b"]);
			this.domNode.removeChild(this.shadow.pieces["br"]);
			
			this.shadow.init(this.domNode);
		}
  }
  
});

