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
	addTitleDateValidation("ref1SymbolsText");	
	addTitleDateValidation("ref1KeysText");	
	addServiceUrlValidation("ref6UrlList");
	
	addAddressTableInfoValidation();
	addCommunicationTableValidation();


	// Object Name must not be empty
	dojo.widget.byId("objectName").isValid = function() { return !this.isMissing(); };

	// Address fields which must not be empty
	dojo.widget.byId("headerAddressType0Unit").isValid = function() { return !this.isMissing(); };
	dojo.widget.byId("headerAddressType1Unit").isValid = function() { return !this.isMissing(); };
	dojo.widget.byId("headerAddressType2Lastname").isValid = function() { return !this.isMissing(); };
	dojo.widget.byId("headerAddressType3Lastname").isValid = function() { return !this.isMissing(); };

	// Ref1 object identifier may be empty
//	dojo.widget.byId("ref1ObjectIdentifier").isValid = function() { return !this.isMissing(); };

	// If timeRefIntervalNum is not empty, timeRefIntervalUnit must not be empty and vice versa
	var intervalNumWidget = dojo.widget.byId("timeRefIntervalNum");
	intervalNumWidget.isValid = function() {
		var unitVal = dojo.widget.byId("timeRefIntervalUnit").getValue();
		var unitEmpty = (unitVal == null || unitVal.length == 0);

		if (unitEmpty) {
			dojo.widget.byId("timeRefIntervalNum").required = false;		
			return true;

		} else {
			dojo.widget.byId("timeRefIntervalNum").required = true;
			return !this.isMissing();
		}
	};

	dojo.event.connect(intervalNumWidget, "onkeyup", function() {
		var val = dojo.widget.byId("timeRefIntervalNum").textbox.value;
		var selectVal = dojo.widget.byId("timeRefIntervalUnit").getValue();

		if (val == null || dojo.string.trim(val).length == 0) {
			dojo.widget.byId("timeRefIntervalUnit").required = false;
			dojo.widget.byId("timeRefIntervalUnit").onValueChanged(selectVal);

		} else {
			dojo.widget.byId("timeRefIntervalUnit").required = true;
			dojo.widget.byId("timeRefIntervalUnit").onValueChanged(selectVal);
		}
	});

	var intervalNumWidget = dojo.widget.byId("timeRefIntervalNum");
	var intervalUnitWidget = dojo.widget.byId("timeRefIntervalUnit");
	dojo.event.connect(intervalNumWidget, "setValue", function() {
		intervalNumWidget.onkeyup();
		intervalNumWidget.isValid();
	});
	dojo.event.connect(intervalUnitWidget, "setValue", function() {
		intervalNumWidget.onkeyup();
		intervalNumWidget.isValid();
		intervalNumWidget.update();
	});
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
  	popup.domNode.innerHTML = dojo.string.substituteParams(message.get("validation.minmax"), message.get("validation.to"), message.get("validation.from"));

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
  	popup.domNode.innerHTML = dojo.string.substituteParams(message.get("validation.minmax"), message.get("validation.latLon2"), message.get("validation.latLon1"));

	table._valid = true;

	table.applyValidation = function() {
		var rows = this.domNode.tBodies[0].rows;
		// Iterate over all the rows in the table
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			var rowData = this.getDataByRow(row);
			// If we have a valid row continue. rowData can be null since we also display empty rows
			if (rowData) {
				var spatialRefName = this.store.getField(rowData, "name");
				var lon1 = this.store.getField(rowData, "longitude1");
				var lon2 = this.store.getField(rowData, "longitude2");
				var lat1 = this.store.getField(rowData, "latitude1");
				var lat2 = this.store.getField(rowData, "latitude2");
				var nameIdx = this.getColumnIndex("name");
				var lon1Idx = this.getColumnIndex("longitude1");
				var lon2Idx = this.getColumnIndex("longitude2");
				var lat1Idx = this.getColumnIndex("latitude1");
				var lat2Idx = this.getColumnIndex("latitude2");

				if ((lon1 == null || lon1 == "")
				 && (lon2 == null || lon2 == "")
				 && (lat1 == null || lat1 == "")
				 && (lat2 == null || lat2 == "")) {
					dojo.html.removeClass(row.cells[nameIdx], this.fieldInvalidClass);
					dojo.html.removeClass(row.cells[lon1Idx], this.fieldInvalidClass);
					dojo.html.removeClass(row.cells[lon2Idx], this.fieldInvalidClass);		
					dojo.html.removeClass(row.cells[lat1Idx], this.fieldInvalidClass);
					dojo.html.removeClass(row.cells[lat2Idx], this.fieldInvalidClass);		
					this._valid = true;
					popup.close();
					return;
				}

				this._valid = false;
				var nameValid = false;
				var lonValid = false;
				var latValid = false;
				dojo.html.addClass(row.cells[nameIdx], this.fieldInvalidClass);
				dojo.html.addClass(row.cells[lon1Idx], this.fieldInvalidClass);
				dojo.html.addClass(row.cells[lon2Idx], this.fieldInvalidClass);		
				dojo.html.addClass(row.cells[lat1Idx], this.fieldInvalidClass);
				dojo.html.addClass(row.cells[lat2Idx], this.fieldInvalidClass);		

				if (spatialRefName != null && dojo.string.trim(spatialRefName).length != 0) {
					nameValid = true;
					dojo.html.removeClass(row.cells[nameIdx], this.fieldInvalidClass);
				}

				if (dojo.validate.isRealNumber(lon1) && dojo.validate.isRealNumber(lon2) && parseFloat(lon1) <= parseFloat(lon2)) {
					lonValid = true;
					dojo.html.removeClass(row.cells[lon1Idx], this.fieldInvalidClass);
					dojo.html.removeClass(row.cells[lon2Idx], this.fieldInvalidClass);		
				}
				if (dojo.validate.isRealNumber(lat1) && dojo.validate.isRealNumber(lat2) && parseFloat(lat1) <= parseFloat(lat2)) {
					latValid = true;
					dojo.html.removeClass(row.cells[lat1Idx], this.fieldInvalidClass);
					dojo.html.removeClass(row.cells[lat2Idx], this.fieldInvalidClass);		
				}

				if (nameValid && lonValid && latValid) {
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

function addTitleDateValidation(tableId){
	var table = dojo.widget.byId(tableId);
	var popup = dojo.widget.createWidget("PopupContainer");
  	popup.domNode.innerHTML = dojo.string.substituteParams(message.get("validation.titleDate"), message.get("ui.obj.type1.symbolCatTable.header.title"), message.get("ui.obj.type1.symbolCatTable.header.date"));

	table._valid = true;

	table.applyValidation = function() {
		var rows = this.domNode.tBodies[0].rows;
		// Iterate over all the rows in the table
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			var rowData = this.getDataByRow(row);
			// If we have a valid row continue. rowData can be null since we also display empty rows
			if (rowData) {
				var title = this.store.getField(rowData, "title");
				var date = this.store.getField(rowData, "date");
				var version = this.store.getField(rowData, "version");
				var titleIdx = this.getColumnIndex("title");
				var dateIdx = this.getColumnIndex("date");
				
				if ((title == null || title == "")
				 && (date == null || date == "")
				 && (version == null || version == "")){
					dojo.html.removeClass(row.cells[titleIdx], this.fieldInvalidClass);
					dojo.html.removeClass(row.cells[dateIdx], this.fieldInvalidClass);		
					this._valid = true;
					popup.close();
					return;
				}

				this._valid = false;
				var titleValid = false;
				var dateValid = false;
				var versionValid = false;
				dojo.html.addClass(row.cells[titleIdx], this.fieldInvalidClass);
				dojo.html.addClass(row.cells[dateIdx], this.fieldInvalidClass);		
				
				if (title != null && dojo.string.trim(title).length != 0) {
					titleValid = true;
					dojo.html.removeClass(row.cells[titleIdx], this.fieldInvalidClass);
				}
				
				if (date != null && dojo.string.trim(date).length != 0) {
					dateValid = true;
					dojo.html.removeClass(row.cells[dateIdx], this.fieldInvalidClass);
				}
				
				if (version != null && dojo.string.trim(version).length != 0) {
					versionValid = true;
				}
				
				
				if (titleValid && dateValid) {
					popup.close();
					this._valid = true;
				} else if (versionValid){
					if(!titleValid || !dateValid){
						popup.open(this.domNode, this);	
					}
				} else {
					popup.open(this.domNode, this);
				}
			}
		}
	}

	table.isValid = function() { return this.store.getData().length == 0 || this._valid;}
}

function addAddressTableInfoValidation() {
	var table = dojo.widget.byId("generalAddress");
	var popup = dojo.widget.createWidget("PopupContainer");

	dojo.event.connectOnce(table.store, "onAddData", table, "applyValidation");

	// Get the string for 'Auskunft' from the backend. Since this function is called in the init phase it's safer
	// to directly query the backend instead of the select box dataProvider. We can't be sure if the dp has
	// already been initialized.
	var def = getAuskunftString();

	def.addCallback(function(auskunftEntry) {
	  	var auskunftString = auskunftEntry[0];
	  	popup.domNode.innerHTML = dojo.string.substituteParams(message.get("validation.addressInfoRequired"), auskunftString);

		table._valid = false;

		table.applyValidation = function() {
			this._valid = false;
			var data = this.store.getData();
			for (var i = 0; i < data.length; ++i) {
				if (data[i].nameOfRelation == auskunftString && typeof(data[i].uuid) != "undefined") {
					this._valid = true;
				}
			}

			if (this._valid) {
				popup.close();

				var rows = this.domNode.tBodies[0].rows;
				// Iterate over all the rows in the table
				for (var i = 0; i < rows.length; i++) {
					var row = rows[i];
					var rowData = this.getDataByRow(row);
					// If we have a valid row continue. rowData can be null since we also display empty rows
					if (rowData) {
						var relNameIdx = this.getColumnIndex("nameOfRelation");
						dojo.html.removeClass(row.cells[relNameIdx], this.fieldInvalidClass);
					}
				}
			} else {
				popup.open(this.domNode, this);
				
				var rows = this.domNode.tBodies[0].rows;
				// Iterate over all the rows in the table
				for (var i = 0; i < rows.length; i++) {
					var row = rows[i];
					var rowData = this.getDataByRow(row);
					// If we have a valid row continue. rowData can be null since we also display empty rows
					if (rowData) {
						var relNameIdx = this.getColumnIndex("nameOfRelation");
						dojo.html.addClass(row.cells[relNameIdx], this.fieldInvalidClass);
					}
				}
			}
		}
	
		table.isValid = function() {
			var data = this.store.getData();
			var rows = this.domNode.tBodies[0].rows;
			var relNameIdx = this.getColumnIndex("nameOfRelation");
			for (var i in data) {
				if (typeof(data[i].uuid) == "undefined") {
					dojo.html.addClass(rows[i].cells[relNameIdx], this.fieldInvalidClass);
					return false;
				} else {
					dojo.html.removeClass(rows[i].cells[relNameIdx], this.fieldInvalidClass);				
				}
			}
			return true;
		}
	
	
		table.deleteRow = function(obj) {
	//		dojo.debug("delete called on:");
	//		dojo.debugShallow(obj);
	
			// If a row of type 'Auskunft' is deleted, check if it is the last one in the table
			if (obj.nameOfRelation == auskunftString) {
				var data = this.store.getData();
				var numInfoEntries = 0;
				for (var i in data) {
					if (data[i].nameOfRelation == auskunftString) {
						numInfoEntries += 1;
					}
				}
				if (numInfoEntries <= 1) {
					popup.open(this.domNode, this);
					return;
				}
			}
	
			this.store.removeData(obj);
			this.onValueDeleted(obj);
		}		
	});
}

function addCommunicationTableValidation() {
	var table = dojo.widget.byId("addressCom");
	var popup = dojo.widget.createWidget("PopupContainer");

	// Get the string for 'email' from the backend. Since this function is called in the init phase it's safer
	// to directly query the backend instead of the select box dataProvider. We can't be sure if the dp has
	// already been initialized.
	var def = getEmailString();

	def.addCallback(function(emailEntry) {
	  	var email = emailEntry[0];
	  	popup.domNode.innerHTML = dojo.string.substituteParams(message.get("validation.communicationEmailRequired"), email);
		dojo.event.connectOnce(table.store, "onAddData", table, "applyValidation");
	
		table._valid = false;
	
		table.applyValidation = function() {
			this._valid = false;	
			var data = this.store.getData();
			for (var i = 0; i < data.length; ++i) {
				if (data[i].medium == email
				      && typeof(data[i].value) != "undefined"
				      && dojo.string.trim(data[i].value).length != 0) {
					this._valid = true;
					dojo.html.removeClass(dojo.byId("addressComLabel"), "important");
				}
			}
	
			if (this._valid) {
				popup.close();
	
			} else {
				popup.open(this.domNode, this);			
			}
		}
	});
}

function getEmailString() {
	var def = UtilCatalog.getSysListEntry(dojo.widget.byId("addressComType").listId, 3);
	return def;
}

function getAuskunftString() {
	var def = UtilCatalog.getSysListEntry(dojo.widget.byId("generalAddressCombobox").listId, 7);
	return def;
}

function addServiceUrlValidation(tableId){
	var table = dojo.widget.byId(tableId);
	var popup = dojo.widget.createWidget("PopupContainer");
  	popup.domNode.innerHTML = dojo.string.substituteParams(message.get("validation.serviceUrl"), message.get("ui.obj.type6.urlList.header.name"), message.get("ui.obj.type6.urlList.header.url"));

	table._valid = true;

	table.applyValidation = function() {
		var rows = this.domNode.tBodies[0].rows;
		// Iterate over all the rows in the table
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			var rowData = this.getDataByRow(row);
			// If we have a valid row continue. rowData can be null since we also display empty rows
			if (rowData) {
				var name = this.store.getField(rowData, "name");
				var url = this.store.getField(rowData, "url");
				var urlDescription = this.store.getField(rowData, "urlDescription");
				var nameIdx = this.getColumnIndex("name");
				var urlIdx = this.getColumnIndex("url");
				var urlDescriptionIdx = this.getColumnIndex("urlDescription");
				
				if ((name == null || name == "")
				 && (url == null || url == "")
				 && (urlDescription == null || urlDescription == "")){
					dojo.html.removeClass(row.cells[nameIdx], this.fieldInvalidClass);
					dojo.html.removeClass(row.cells[urlIdx], this.fieldInvalidClass);		
					this._valid = true;
					popup.close();
					return;
				}

				this._valid = false;
				var nameValid = false;
				var urlValid = false;
				var urlDescriptionValid = false;
				dojo.html.addClass(row.cells[nameIdx], this.fieldInvalidClass);
				dojo.html.addClass(row.cells[urlIdx], this.fieldInvalidClass);		
				
				if (name != null && dojo.string.trim(name).length != 0) {
					nameValid = true;
					dojo.html.removeClass(row.cells[nameIdx], this.fieldInvalidClass);
				}
				
				if (url != null && dojo.string.trim(url).length != 0) {
					urlValid = true;
					dojo.html.removeClass(row.cells[urlIdx], this.fieldInvalidClass);
				}
				
				if (urlDescription != null && dojo.string.trim(urlDescription).length != 0) {
					urlDescriptionValid = true;
				}
				
				
				if (nameValid && urlValid) {
					popup.close();
					this._valid = true;
				} else if (urlDescriptionValid){
					if(!nameValid || !urlValid){
						popup.open(this.domNode, this);	
					}
				} else {
					popup.open(this.domNode, this);
				}
			}
		}
	}

	table.isValid = function() { return this.store.getData().length == 0 || this._valid;}
}

