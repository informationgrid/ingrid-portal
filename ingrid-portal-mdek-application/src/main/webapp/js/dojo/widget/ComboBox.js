dojo.provide("ingrid.widget.ComboBox");

dojo.require("dojo.widget.ComboBox");

/**
 * ComboBox
 */
dojo.widget.defineWidget(
	"ingrid.widget.ComboBox",
	dojo.widget.ComboBox,
    function() {
      // The listId used for populating this combo box with values from sys_list
      this.listId = "";
      // Number of max items that are displayed in the list. 1000 is the default and can be changed.
      this.searchLimit = 1000;
    },
{
  templateCssPath: dojo.uri.moduleUri("ingrid", "widget/templates/ComboBox.css"),
  templatePath: dojo.uri.moduleUri("ingrid", "widget/templates/ComboBox.html"),
  buttonSrc: dojo.uri.moduleUri("ingrid", "widget/templates/images/combo_box_arrow.png"),
  dropdownToggle: "none",
  
  // Limit the size of user input to 'maxlength' chars
  maxlength: "",
	
	getDisplayValueForValue: function(value) {
		for (var i=0; i<this.dataProvider._data.length; i++) {
			if (this.dataProvider._data[i][1] == value) {
				return this.dataProvider._data[i][0];
			}
		}
		return null;
	},
	
	getValueForDisplayValue: function(dispValue) {
	    if (dispValue == null) return;
		for (var i=0; i<this.dataProvider._data.length; i++) {
			if (this.dataProvider._data[i][0].toLowerCase() == dispValue.toLowerCase()) {
				return this.dataProvider._data[i][1];
			}
		}
		return null;
	},

  setValue: function(val, key) {
	if (typeof(key) != "undefined" && key != -1) {
		// If the key is set, get the display value for the given key
		val = this.getDisplayValueForValue(key);
	}

	if (val == null)
		arguments[0] = "";

	ingrid.widget.ComboBox.superclass.setValue.apply(this, arguments);
  },

  getIdValue: function() {
  	var val = this.getValueForDisplayValue(this.comboBoxValue.value);
  	if (val == null)
  		return -1;
  	else
  		return val;
  },
  
  postCreate: function(){
    ingrid.widget.ComboBox.superclass.postCreate.apply(this, arguments);

  	if (this.dataProvider && typeof(this.dataProvider.searchLimit) != "undefined") {
  		this.dataProvider.searchLimit = this.searchLimit;
  	}
  },

  enable: function() {
    dojo.widget.ComboBox.superclass.enable.apply(this, arguments);
    dojo.html.removeClass(this.textInputNode, 'noEdit');
    this.textInputNode.removeAttribute("disabled");
  },

  disable: function() {
    dojo.widget.ComboBox.superclass.disable.apply(this, arguments);
    dojo.html.addClass(this.textInputNode, 'noEdit');
    this.textInputNode.setAttribute("disabled", "disabled");
  }
});
