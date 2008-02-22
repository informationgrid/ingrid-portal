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
    },
{
  templateCssPath: dojo.uri.moduleUri("ingrid", "widget/templates/ComboBox.css"),
  buttonSrc: dojo.uri.moduleUri("ingrid", "widget/templates/images/combo_box_arrow.png"),
  dropdownToggle: "none",

	getDisplayValueForValue: function(value) {
		for (var i=0; i<this.dataProvider._data.length; i++) {
			if (this.dataProvider._data[i][1] == value) {
				return this.dataProvider._data[i][0];
			}
		}
		return null;
	},
	
	getValueForDisplayValue: function(dispValue) {
		for (var i=0; i<this.dataProvider._data.length; i++) {
			if (this.dataProvider._data[i][0] == dispValue) {
				return this.dataProvider._data[i][1];
			}
		}
		return null;
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
