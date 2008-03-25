dojo.provide("ingrid.widget.ValidationTextbox");

dojo.require("dojo.widget.PopupContainer");
dojo.require("dojo.widget.ValidationTextbox");

/**
 * ValidationTextbox
 */
dojo.widget.defineWidget(
	"ingrid.widget.ValidationTextbox",
	dojo.widget.ValidationTextbox,
{
});

// In this case we directly extend dojo.widget.ValidationTextbox to conventiently inject our changes
// to every subclass
dojo.lang.extend(dojo.widget.ValidationTextbox, {
  templateCssPath: dojo.uri.moduleUri("ingrid", "widget/templates/Validate.css"),
  templatePath: dojo.uri.moduleUri("ingrid", "widget/templates/ValidationTextbox.html"),

  trim: false,
  ucfirst: false,

  mode: "textbox",

  /*
   * Readonly support
   */
  disabled: false,

  /*
   * PopupContainer for the various messages
   */
  popup: null,

/*
	mixInProperties: function(localProperties, frag){
		// First initialize properties in super-class.
		dojo.widget.ValidationTextbox.superclass.mixInProperties.apply(this, arguments);

		// Get properties from markup attributes, and assign to flags object.
		dojo.debugShallow(localProperties);
		if (localProperties.maxLength) {
			this.flags.maxLength = localProperties.maxLength;
		}
	},
*/

  /*
   * Do initialization here. Override this in subclasses to add functionality
   * but don't forget to call the parent class method
   */ 

  initialize: function(args) {
    dojo.widget.ValidationTextbox.superclass.initialize.apply(this, arguments);

    if (this.disabled == true) {
	  var ee = this[this.mode.toLowerCase()];
      ee.setAttribute("disabled", "disabled");
      dojo.html.addClass(ee, "noEdit");
    }

	if(this.mode.toLowerCase() == "textarea"){
		this.textarea.style.display = "";
		this.textbox.style.display = "none";
	} else {
		this.textarea.style.display = "none";
		this.textbox.style.display = "";
	}

	this.popup = dojo.widget.createWidget("PopupContainer");
  	this.popup.domNode.innerHTML = "";
  },

	getValue: function() {
		var ee = this[this.mode.toLowerCase()];				
		return ee.value;
	},

	setValue: function(value) {
		if (dojo.render.html.ie && value == null) {
			value = "";
		}

		var ee = this[this.mode.toLowerCase()];				
		ee.value = value;
		this.update();
	},


	isEmpty: function() {
		// summary: Checks for whitespace
		var ee = this[this.mode.toLowerCase()];				
		return ( /^\s*$/.test(ee.value) ); // Boolean
	},

	isInRange: function() {
		if (this.maxlength) {
			return this[this.mode.toLowerCase()].value.length <= parseInt(this.maxlength);
		} else {
			return true;
		}
	},

// TODO write missing functions (update, highlight, ...)

	update: function() {
		// summary:
		//		Called by oninit, onblur, and onkeypress.
		// description:
		//		Show missing or invalid messages if appropriate, and highlight textbox field.
		var ee = this[this.mode.toLowerCase()];				

		// Limit the size of the input in case of a textarea
		if (this.mode.toLowerCase() == "textarea" && this.maxlength && ee.value.length > parseInt(this.maxlength)) {
			ee.value = ee.value.substr(0, this.maxlength);
		}

		this.lastCheckedValue = ee.value;
		this.missingSpan.style.display = "none";
		this.invalidSpan.style.display = "none";
		this.rangeSpan.style.display = "none";

		var empty = this.isEmpty();
		var valid = true;
		if(this.promptMessage != ee.value){ 
			valid = this.isValid(); 
		}
		var missing = this.isMissing();

		// Display at most one error message
		if(missing){
			this.missingSpan.style.display = "";
		}else if( !empty && !valid ){
			this.invalidSpan.style.display = "";
		}else if( !empty && !this.isInRange() ){
			this.rangeSpan.style.display = "";
		}
		this.highlight();
	},
		
	updateClass: function(className){
		// summary: used to ensure that only 1 validation class is set at a time
	    var ee = this[this.mode.toLowerCase()];
		var pre = this.classPrefix;
		dojo.html.removeClass(ee,pre+"Empty");
		dojo.html.removeClass(ee,pre+"Valid");
		dojo.html.removeClass(ee,pre+"Invalid");
		dojo.html.addClass(ee,pre+className);

		if (this.popup) {
			if (className == "Invalid" && this.invalidMessage){
			  	this.popup.domNode.innerHTML = this.invalidMessage;
				this.popup.open(this.domNode, this);
			} else if (className == "Empty" && this.emptyMessage){
			  	this.popup.domNode.innerHTML = this.emptyMessage;
				this.popup.open(this.domNode, this);
			} else {
				this.popup.close();
			}
		}
	},

	highlight: function() {
		// summary: by Called oninit, and onblur.

		// highlight textbox background 
		if (this.isEmpty() && !this.required) {
			this.updateClass("Empty");
		}else if (this.isValid() && this.isInRange() ){
			this.updateClass("Valid");
		}else { 
			this.updateClass("Invalid");
//		}else if(this.textbox.value != this.promptMessage){ 
//			this.updateClass("Invalid");
//		}else{
//			this.updateClass("Empty");
		}
	},

  enable: function() {
    dojo.widget.ValidationTextbox.superclass.enable.apply(this, arguments);
	var ee = this[this.mode.toLowerCase()];				
    ee.removeAttribute("disabled");
    dojo.html.removeClass(ee, "noEdit");
  },

  disable: function() {
    dojo.widget.ValidationTextbox.superclass.disable.apply(this, arguments);
	var ee = this[this.mode.toLowerCase()];				
    ee.setAttribute("disabled", "disabled");
    dojo.html.addClass(ee, "noEdit");
  }
});
