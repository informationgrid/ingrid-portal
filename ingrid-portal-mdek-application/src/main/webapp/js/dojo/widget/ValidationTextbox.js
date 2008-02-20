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
		var ee = this[this.mode.toLowerCase()];				
		ee.value = value;
		this.update();
	},


	isEmpty: function() {
		// summary: Checks for whitespace
		var ee = this[this.mode.toLowerCase()];				
		return ( /^\s*$/.test(ee.value) ); // Boolean
	},

// TODO write missing functions (update, highlight, ...)

	updateClass: function(className){
		// summary: used to ensure that only 1 validation class is set at a time
		var pre = this.classPrefix;
		dojo.html.removeClass(this.textbox,pre+"Empty");
		dojo.html.removeClass(this.textbox,pre+"Valid");
		dojo.html.removeClass(this.textbox,pre+"Invalid");
		dojo.html.addClass(this.textbox,pre+className);

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


  enable: function() {
    dojo.debug("enable called on "+this);
    dojo.widget.ValidationTextbox.superclass.enable.apply(this, arguments);
	var ee = this[this.mode.toLowerCase()];				
    ee.removeAttribute("disabled");
    dojo.html.removeClass(ee, "noEdit");
  },

  disable: function() {
    dojo.debug("disable called on "+this);
    dojo.widget.ValidationTextbox.superclass.disable.apply(this, arguments);
	var ee = this[this.mode.toLowerCase()];				
    ee.setAttribute("disabled", "disabled");
    dojo.html.addClass(ee, "noEdit");
  }
});
