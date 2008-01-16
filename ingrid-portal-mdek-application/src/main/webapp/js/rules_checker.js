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
//	return dojo.lang.every(notEmptyFields, notEmptyFunc, idcObject);

	var myObj = {objectName: "", objectClass: "", generalDesc: ""};

	dojo.debug("Should be false: "+dojo.lang.every(notEmptyFields, notEmptyFunc, myObj));

	myObj.objectName = "name";
	myObj.objectClass = "class";
	myObj.generalDesc = "general Description";
	
	dojo.debug("Should be true: "+dojo.lang.every(notEmptyFields, notEmptyFunc, myObj));
}
