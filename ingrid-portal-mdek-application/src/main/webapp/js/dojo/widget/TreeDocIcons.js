dojo.provide("ingrid.widget.TreeDocIcons");

dojo.require("ingrid.widget.TreeDocIconExtension");

/**
 * TreeDocIcons is a tree extension to show icons on nodes
 */
dojo.widget.defineWidget(
	"ingrid.widget.TreeDocIcons",
	ingrid.widget.TreeDocIconExtension,
{
  templateCssPath: dojo.uri.moduleUri("ingrid", "widget/templates/TreeDocIcon.css"),

  /*
   * Fix: Base class adds the icon even if already added (because the class does not stop listening to old trees)
   */
	listenNodeFilter: function(elem) { return (elem instanceof ingrid.widget.TreeNode && elem.contentIconNode == undefined) }

});
