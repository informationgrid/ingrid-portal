dojo.provide("ingrid.dijit.GridDateTextBox");

dojo.require("dojox.grid.cells.dijit");

dojo.declare("ingrid.dijit.GridDateTextBox", dojox.grid.cells._Widget, {
    widgetClass: dijit.form.DateTextBox,
    setValue: function(inRowIndex, inValue){
		console.debug("setValue in ingrid.dijit.GridDateTextBox");
        if (this.widget) {
			if (inValue == null || inValue == "") {
				this.widget.attr('value', '');
			} else {
				this.widget.attr('value', new Date(inValue));
			}
        }
        else {
            this.inherited(arguments);
        }
    },
    getWidgetProps: function(inDatum){
		console.debug("getValue in ingrid.dijit.GridDateTextBox");
        return dojo.mixin(this.inherited(arguments), {
            value: new Date(inDatum)
        });
    }
})
