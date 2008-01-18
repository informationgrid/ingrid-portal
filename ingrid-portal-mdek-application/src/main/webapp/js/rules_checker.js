/*
 * Functions for checking the validity of entered values.
 */

dojo.addOnLoad(function()
{
	var objectName = dojo.widget.byId("objectName");
	objectName.isValid = function() {
		return (!this.isEmpty());
	}
});

var notEmptyFields = ["objectName", "objectClass", "generalDesc", "extraInfoLangData", "extraInfoLangMetaData"];
var notEmptyFieldsClass1 = ["ref1DataSet"]; 
var notEmptyFieldsClass3 = ["ref3ServiceType"];
var notEmptyFunc = function(element) {return (this[element] && this[element] != "");}


function isObjectPublishable(idcObject) {
	if (dojo.lang.every(notEmptyFields, notEmptyFunc, idcObject)) {
		// All the fields that need to be filled contain data
		// Continue checking
		return true;
	} else {
		return false;
	}
}
