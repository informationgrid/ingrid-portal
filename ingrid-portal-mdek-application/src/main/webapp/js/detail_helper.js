/*
 * InGrid detail view helper. Opened with static methods
 */
 
detailHelper={};

detailHelper.renderAddressEntry = function(address) {
	var entry = "";
	var block = "";
	if (this.isValid(address.organisation)) {
		entry += address.organisation + "\n";
	}

	var nameForm = null;
	if (address.nameForm.key != null && address.nameForm.key != -1) {
		nameForm = dojo.widget.byId("headerAddressType2Style").getDisplayValueForValue(address.nameForm.key);
	} else if (address.nameForm.value != null) {
		nameForm = address.nameForm.value;
	}
	var titleOrFunction = null;
	if (address.titleOrFunction.key != null && address.titleOrFunction.key != -1) {
		titleOrFunction = dojo.widget.byId("headerAddressType2Style").getDisplayValueForValue(address.titleOrFunction.key);
	} else if (address.titleOrFunction.value != null) {
		titleOrFunction = address.titleOrFunction.value;
	}

	if (this.isValid(nameForm)) {
		block += nameForm + " ";
	}
	if (this.isValid(titleOrFunction)) {
		block += titleOrFunction + " ";
	}
	if (this.isValid(address.givenName)) {
		block += address.givenName + " ";
	}
	if (this.isValid(address.name)) {
		block += address.name;
	}
	if (block.length > 0) {
		entry += block + "\n"
	}
	if (this.isValid(address.street)) {
		entry += address.street+"\n";
	}
	if (this.isValid(address.city)) {
		entry += address.city+"\n";
	}
	if (this.isValid(address.pobox)) {
		entry += "\n"+address.pobox+"\n";
		if (this.isValid(address.poboxPostalCode)) {
			entry += address.poboxPostalCode+" ";
		}
		if (this.isValid(address.city)) {
			entry += address.city+"\n";
		}
	}
	
	// communications
	if (address.communication && address.communication.length) {
		entry += "\n";
		var c;
		for (i=0; i<address.communication.length; i++) {
			c = address.communication[i];
			entry += c.communicationMedium + ": " + c.communicationValue + "\n"; 
		}
	}
	return entry;
}

detailHelper.isValid = function(val) {
	if (dojo.lang.isString(val) && val.length>0) {
		return true;
	} else {
		return false;
	}
}