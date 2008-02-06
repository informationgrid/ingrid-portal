/*
 * Special validation rules for form items
 */

/*
 * Rules that validate a field's value depending on another field's value
 */
dojo.addOnLoad(function() {
	addMinMaxValidation("spatialRefAltMin", "spatialRefAltMax", "Minimum", "Maximum");
	addMinMaxDateValidation("timeRefType", "timeRefDate1", "timeRefDate2");
	addMinMaxBoundingBoxValidation("spatialRefLocation");
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

	minWidget.isValid = function() { return !dojo.html.hasClass(this.inputNode, "fieldInvalid"); };
	maxWidget.isValid = function() { return !dojo.html.hasClass(this.inputNode, "fieldInvalid"); };

	var validate = function() {
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
	dojo.event.connect("after", typeWidget, "onValueChanged", validate);
}

function addMinMaxBoundingBoxValidation(tableId) {
	var table = dojo.widget.byId(tableId);
	var popup = dojo.widget.createWidget("PopupContainer");
  	popup.domNode.innerHTML = dojo.string.substituteParams(message.get("validation.minmax"), "L&auml;nge/Breite 2", "L&auml;nge/Breite 1");

	table._valid = true;

	table.applyValidation = function() {
		var rows = this.domNode.tBodies[0].rows;
		// Iterate over all the rows in the table
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			var rowData = this.getDataByRow(row);
			// If we have a valid row continue. rowData can be null since we also display empty rows
			if (rowData) {
				var lon1 = this.store.getField(rowData, "longitude1");
				var lon2 = this.store.getField(rowData, "longitude2");
				var lat1 = this.store.getField(rowData, "latitude1");
				var lat2 = this.store.getField(rowData, "latitude2");
				var lon1Idx = this.getColumnIndex("longitude1");
				var lon2Idx = this.getColumnIndex("longitude2");
				var lat1Idx = this.getColumnIndex("latitude1");
				var lat2Idx = this.getColumnIndex("latitude2");

				if ((lon1 == null || lon1 == "")
				 && (lon2 == null || lon2 == "")
				 && (lat1 == null || lat1 == "")
				 && (lat2 == null || lat2 == "")) {
					dojo.html.removeClass(row.cells[lon1Idx], this.fieldInvalidClass);
					dojo.html.removeClass(row.cells[lon2Idx], this.fieldInvalidClass);		
					dojo.html.removeClass(row.cells[lat1Idx], this.fieldInvalidClass);
					dojo.html.removeClass(row.cells[lat2Idx], this.fieldInvalidClass);		
					this._valid = true;
					popup.close();
					return;
				}

				this._valid = false;
				var lonValid = false;
				var latValid = false;
				dojo.html.addClass(row.cells[lon1Idx], this.fieldInvalidClass);
				dojo.html.addClass(row.cells[lon2Idx], this.fieldInvalidClass);		
				dojo.html.addClass(row.cells[lat1Idx], this.fieldInvalidClass);
				dojo.html.addClass(row.cells[lat2Idx], this.fieldInvalidClass);		

				if (dojo.validate.isRealNumber(lon1) && dojo.validate.isRealNumber(lon2) && parseFloat(lon1) < parseFloat(lon2)) {
					lonValid = true;
					dojo.html.removeClass(row.cells[lon1Idx], this.fieldInvalidClass);
					dojo.html.removeClass(row.cells[lon2Idx], this.fieldInvalidClass);		
				}
				if (dojo.validate.isRealNumber(lat1) && dojo.validate.isRealNumber(lat2) && parseFloat(lat1) < parseFloat(lat2)) {
					latValid = true;
					dojo.html.removeClass(row.cells[lat1Idx], this.fieldInvalidClass);
					dojo.html.removeClass(row.cells[lat2Idx], this.fieldInvalidClass);		
				}

				if (lonValid && latValid) {
					popup.close();
					this._valid = true;
				} else {
					popup.open(this.domNode, this);
				}
			}
		}
	}

	table.isValid = function() { return this.store.getData().length == 0 || this._valid; }
}
