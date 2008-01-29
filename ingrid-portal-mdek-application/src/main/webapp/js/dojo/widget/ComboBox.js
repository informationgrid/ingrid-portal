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
  dropdownToggle: "none"
});
