dojo.provide("ingrid.widget.ValidationTextbox");

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

  /*
   * Readonly support
   */
  disabled: false,

  /*
   * Do initialization here. Override this in subclasses to add functionality
   * but don't forget to call the parent class method
   */ 
  initialize: function(args) {
    dojo.widget.ValidationTextbox.superclass.initialize.apply(this, arguments);
    if (this.disabled == true) {
      this.textbox.setAttribute('disabled', 'disabled');
      dojo.html.addClass(this.textbox, 'noEdit');
    }
  },

  enable: function() {
    dojo.widget.ValidationTextbox.superclass.enable.apply(this, arguments);
    this.textbox.setAttribute('disabled', '');
    dojo.html.removeClass(this.textbox, 'noEdit');
  },

  disable: function() {
    dojo.widget.ValidationTextbox.superclass.disable.apply(this, arguments);
    this.textbox.setAttribute('disabled', 'disabled');
    dojo.html.addClass(this.textbox, 'noEdit');
  }
});
