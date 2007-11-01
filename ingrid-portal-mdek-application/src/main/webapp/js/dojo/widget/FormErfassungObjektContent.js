dojo.provide("ingrid.widget.FormErfassungObjektContent");

dojo.require("ingrid.widget.Form");

/**
 * Erfassung Objekt
 */
dojo.widget.defineWidget(
	"ingrid.widget.FormErfassungObjektContent",
	ingrid.widget.Form,
{
  /*
   * The html definition
   */
//  href: "erfassung_objekt_content.html",
  
  /*
   * Do initialization here. Override this in subclasses to add functionality
   * but don't forget to call the parent class method
   */ 
  initForm: function(e) {
    ingrid.widget.Form.prototype.initForm.apply(this, arguments);

    // set context menu item on Allgemeines -> Adressen table
    var addressTable = dojo.widget.byId("generalAddress");
    if (typeof showAddress != "function") {
      dojo.debug("Global function 'showAddress' expected. Menu item will be ignored.");
    }
    else {
      var contextMenu = addressTable.getContextMenu();
      if (contextMenu) {
        contextMenu.addSeparator();
        contextMenu.addItemObject({caption:message.get('general.showAddress'), method:showAddress});
      }
    }
  },

  /*
   * The toggle containers contained in the page
   */
  toggleContainer: ["refClass1", "refClass2", "refClass3", "refClass4", "refClass5"],
  
  /*
   * The toggle container prefix
   */
  toggleContainerPrefix: "ref",

  setSelectedClass: function(/* name of the object class/address type */clazz) {
    ingrid.widget.Form.prototype.setSelectedClass.apply(this, arguments);
    
    // hide section 'Verfügbarkeit' if 'Organisationseinheit/Fachaufgabe' (Class0) is selected
    var availabilityContainer = document.getElementById('availability');
    if (availabilityContainer) {
      if(this.selectedClass == "Class0")
        availabilityContainer.style.display = 'none';
      else
        availabilityContainer.style.display = 'block';
    }
  }
});
