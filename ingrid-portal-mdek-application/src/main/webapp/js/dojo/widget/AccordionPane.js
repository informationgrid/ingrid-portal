dojo.provide("ingrid.widget.AccordionPane");

dojo.require("dojo.widget.AccordionContainer");

/**
 * AccordionPane
 */
dojo.widget.defineWidget(
	"ingrid.widget.AccordionPane",
	dojo.widget.AccordionPane,
  function() {
  	this.infoUrl = "";
  },
{
  templateCssPath: dojo.uri.moduleUri("ingrid", "widget/templates/AccordionPane.css"),
  templatePath: dojo.uri.moduleUri("ingrid", "widget/templates/AccordionPane.html"),

	/*
   * The header icon
   */
	icon: "img/ic_analysis_check.gif",

	/*
   * The item count
   */
	itemCount: "0",

	/*
   * The info url
   */
	infoUrl: "#",

  /*
   * Initialize
   */ 
  initialize: function() {
    ingrid.widget.AccordionPane.superclass.initialize.apply(this, arguments);

    if (this.infoUrl.length == 0)
      this.infoLink.style.visibility = "hidden";
  }

});
