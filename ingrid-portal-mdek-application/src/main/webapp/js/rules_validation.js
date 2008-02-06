/*
 * Special validation rules for form items
 */

/*
 * Rules that validate a field's value depending on another field's value
 */
dojo.addOnLoad(function() {
	addMinMaxValidation("spatialRefAltMin", "spatialRefAltMax", "Minimum", "Maximum");
	addMinMaxDateValidation("timeRefType", "timeRefDate1", "timeRefDate2");
});


function addMinMaxValidation(minWidgetId, maxWidgetId, minCaption, maxCaption) {
	var minWidget = dojo.widget.byId(minWidgetId);
	var maxWidget = dojo.widget.byId(maxWidgetId);

	minWidget.invalidMessage = dojo.string.substituteParams(message.get("validation.minmax"), maxCaption, minCaption);
	maxWidget.invalidMessage = dojo.string.substituteParams(message.get("validation.minmax"), maxCaption, minCaption);

	minWidget.isValid = function() {
		var minValue = dojo.string.trim(minWidget.textbox.value);
		var maxValue = dojo.string.trim(maxWidget.textbox.value);
		
		if (minValue == "" || maxValue == "")
			return true;
		else
			return (dojo.validate.isRealNumber(minValue, minWidget.flags) &&
				dojo.validate.isRealNumber(maxValue, maxWidget.flags) &&
				parseFloat(minValue) <= parseFloat(maxValue));
	}
	maxWidget.isValid = function() {
		var minValue = dojo.string.trim(minWidget.textbox.value);
		var maxValue = dojo.string.trim(maxWidget.textbox.value);
		
		if (minValue == "" || maxValue == "")
			return true;
		else
			return (dojo.validate.isRealNumber(minValue, minWidget.flags) &&
				dojo.validate.isRealNumber(maxValue, maxWidget.flags) &&
				parseFloat(minValue) <= parseFloat(maxValue));
	}

	dojo.event.connect(minWidget, "onkeyup", maxWidget, "update");
	dojo.event.connect(maxWidget, "onkeyup", minWidget, "update");

	dojo.event.connect(minWidget, "setValue", maxWidget, "update");
	dojo.event.connect(maxWidget, "setValue", minWidget, "update");
}

function addMinMaxDateValidation(typeWidgetId, minWidgetId, maxWidgetId) {
	var typeWidget = dojo.widget.byId(typeWidgetId);
	var minWidget = dojo.widget.byId(minWidgetId);
	var maxWidget = dojo.widget.byId(maxWidgetId);

	var popup = dojo.widget.createWidget("PopupContainer");
  	popup.domNode.innerHTML = dojo.string.substituteParams(message.get("validation.minmax"), "bis", "von");

	validate = function() {
		var minVal = minWidget.valueNode.value;
		var maxVal = maxWidget.valueNode.value;
//		dojo.debug("minVal: "+minVal+" maxVal: "+maxVal);

		if (typeWidget.getValue() == "von" && minVal >= maxVal) {
			dojo.html.addClass(minWidget.inputNode, "fieldInvalid");
			dojo.html.addClass(maxWidget.inputNode, "fieldInvalid");
			popup.open(minWidget.inputNode, minWidget);
		} else {
			dojo.html.removeClass(minWidget.inputNode, "fieldInvalid");
			dojo.html.removeClass(maxWidget.inputNode, "fieldInvalid");
			popup.close();
		}
	}

	dojo.event.connect("after", minWidget, "onValueChanged", validate);
	dojo.event.connect("after", maxWidget, "onValueChanged", validate);
}
