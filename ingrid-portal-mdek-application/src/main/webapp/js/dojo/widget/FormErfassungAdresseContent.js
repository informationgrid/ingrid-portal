dojo.provide("ingrid.widget.FormErfassungAdresseContent");

dojo.require("ingrid.widget.Form");

/**
 * Erfassung Adressen
 */
dojo.widget.defineWidget(
	"ingrid.widget.FormErfassungAdresseContent",
	ingrid.widget.Form,
{
  /*
   * The html definition
   */
//  href: "erfassung_adresse_content.html",
  
  /*
   * The toggle containers contained in the page
   */
  toggleContainer: ["headerAddressType0", "headerAddressType1", "headerAddressType2", "headerAddressType3"],

  /*
   * The toggle container prefix
   */
  toggleContainerPrefix: "header"
  
});
