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
  toggleContainer: ["refClass1", "refClass1DQ", "refClass2", "refClass3", "refClass4", "refClass5", "refClass6"],
  
  /*
   * The toggle container prefix
   */
  toggleContainerPrefix: "ref",
  toggleContainerPostfixes: ["DQ"],

  setSelectedClass: function(/* name of the object class/address type */clazz) {
    ingrid.widget.Form.prototype.setSelectedClass.apply(this, arguments);
    
    // hide section 'Verfuegbarkeit' if 'Organisationseinheit/Fachaufgabe' (Class0) is selected
    var availabilityContainer = document.getElementById('availability');
    if (availabilityContainer) {
      if(this.selectedClass == "Class0")
        availabilityContainer.style.display = 'none';
      else
        availabilityContainer.style.display = 'block';
    }

	var confContainer = dojo.byId("extraInfoConformityTableContainer");
	if (confContainer) {
		if (this.selectedClass == "Class1" || this.selectedClass == "Class3") {
			dojo.html.show(confContainer);
		} else {
			dojo.html.hide(confContainer);
		}
	}

	   // class specials !
    
    // Fields only mandatory for Geoinformation/Karte(1)
    // NOTICE: div excluded from normal show/hide mechanism (displaytype="exclude")
    if (this.selectedClass == "Class1") {
        // "Kodierungsschema der geographischen Daten" 
        setRequiredState(dojo.byId("availabilityDataFormatInspireLabel"), dojo.byId("uiElement1315"), true);
        dojo.byId("availabilityDataFormatInspireContainer").style.display = 'block';

        // "INSPIRE-Thema"
        setRequiredState(dojo.byId("thesaurusInspireLabel"), dojo.byId("uiElement5064"), true);
        dojo.byId("thesaurusInspireContainer").style.display = 'block';
        // show / hide DQ input dependent from INSPIRE Thema !
        applyRule7();

        // "ISO-Themenkategorie" only mandatory in class 1 
        setRequiredState(dojo.byId("thesaurusTopicsLabel"), dojo.byId("uiElement5060"), true);
        dojo.byId("thesaurusTopicsContainer").style.display = 'block';

    } else {
	   // "Kodierungsschema der geographischen Daten" only in class 1
        setRequiredState(dojo.byId("availabilityDataFormatInspireLabel"), dojo.byId("uiElement1315"), false);
        dojo.byId("availabilityDataFormatInspireContainer").style.display = 'none';

        // "INSPIRE-Thema" also in other classes
        setRequiredState(dojo.byId("thesaurusInspireLabel"), dojo.byId("uiElement5064"), false);
        // DO NOT HIDE to avoid vanishing field ...

        setRequiredState(dojo.byId("thesaurusTopicsLabel"), dojo.byId("uiElement5060"), false);
        // DO NOT HIDE to avoid vanishing field ...
    }

    // Fields only mandatory for Geoinformation/Karte(1) and Geodatendienst(3)
    if (this.selectedClass == "Class1" || this.selectedClass == "Class3") {
        // "Raumbezugssystem"
        setRequiredState(dojo.byId("ref1SpatialSystemLabel"), dojo.byId("uiElement3500"), true);
        dojo.byId("ref1SpatialSystemContainer").style.display = 'block';

	} else {
        setRequiredState(dojo.byId("ref1SpatialSystemLabel"), dojo.byId("uiElement3500"), false);
		// DO NOT HIDE to avoid vanishing field ...
    }

	if (dojo.render.html.ie) {
		setTimeout("refreshInputContainers()", 1);
	}
  }
});
