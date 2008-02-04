/*
 * Special validation rules for form items
 */

/*
 * Rules that validate a field's value depending on another field's value
 */
dojo.addOnLoad(function() {
	addMinMaxValidation("spatialRefAltMin", "spatialRefAltMax", "Minimum", "Maximum");

//	addMinMaxValidation("longitude1Editor", "longitude2Editor", "L&auml;nge 2", "L&auml;nge 1");
//	addMinMaxValidation("latitude1Editor", "latitude2Editor", "Breite 2", "Breite 1");
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
				parseFloat(minWidget.textbox.value) <= parseFloat(maxWidget.textbox.value));
	}
	maxWidget.isValid = function() {
		var minValue = dojo.string.trim(minWidget.textbox.value);
		var maxValue = dojo.string.trim(maxWidget.textbox.value);
		
		if (minValue == "" || maxValue == "")
			return true;
		else
			return (dojo.validate.isRealNumber(minWidget.textbox.value, minWidget.flags) &&
				dojo.validate.isRealNumber(maxWidget.textbox.value, maxWidget.flags) &&
				parseFloat(minWidget.textbox.value) <= parseFloat(maxWidget.textbox.value));
	}

	dojo.event.connect(minWidget, "onkeyup", maxWidget, "update");
	dojo.event.connect(maxWidget, "onkeyup", minWidget, "update");
}