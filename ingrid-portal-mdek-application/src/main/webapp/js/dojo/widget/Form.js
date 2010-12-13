dojo.provide("ingrid.widget.Form");

dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.widget.Form");

/**
 * Baseclass for Object/Address Forms
 */
dojo.widget.defineWidget(
	"ingrid.widget.Form",
    [dojo.widget.ContentPane, dojo.widget.Form],
{
  cacheContent: false,
  loadingMessage: "Lade Daten ...",

	postCreate: function(args, frag, parentComp){
    dojo.widget.ContentPane.prototype.postCreate.apply(this, arguments);

    this.addOnLoad(this, 'initForm');
	},

  /*
   * Do initialization here. Override this in subclasses to add functionality
   * but don't forget to call the parent class method
   */ 
  initForm: function(e) {
    // select a already given class
    if(this.selectedClass)
      this.setSelectedClass(this.selectedClass);

    // hide unnecessary fields
    var toggleBtn = dojo.widget.byId('toggleFieldsBtn');
    var btnImage = toggleBtn.domNode.getElementsByTagName('img')[0];
    btnImage.src = "img/ic_expand_required_grey.gif";

    // load special rules
	// -> moved to mdek_entry.jsp 
//    dojo.hostenv.loadPath(ingridJsPath+"rules_required.js");
//    dojo.hostenv.loadPath(ingridJsPath+"rules_validation.js");
  },

  /*
   * Object Class/Address Type selection
   */
  selectedClass: "",
  toggleContainer: [],
  toggleContainerPrefix: "",
  toggleContainerPostfixes: [],

  setSelectedClass: function(/* name of the object class/address type */clazz) {
    // hide all classes first
    for(var i=0; i<this.toggleContainer.length; i++) {
      var div = dojo.byId(this.toggleContainer[i]);
      if (div)
        div.style.display = 'none';
    }

    // show new one
    var div = dojo.byId(this.toggleContainerPrefix + clazz);
    if (div)
      div.style.display = 'block';

    // show new special ones including postfixes (e.g. DQ only in class 1)
    for(var i=0; i<this.toggleContainerPostfixes.length; i++) {
      var div = dojo.byId(this.toggleContainerPrefix + clazz + this.toggleContainerPostfixes[i]);
      if (div && (div.getAttribute("hiddenByRules") != "true"))
        div.style.display = 'block';
    }

    this.selectedClass = clazz;
  }
});
