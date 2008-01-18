/*
 * Functions for checking the validity of entered values.
 */

dojo.addOnLoad(function() {});

var notEmptyFields = ["objectName", "objectClass", "generalDescription", "extraInfoLangData",
					  "extraInfoLangMetaData"];

var notEmptyFieldsClass1 = ["ref1DataSet"]; 
var notEmptyFieldsClass3 = ["ref3ServiceType"];
var notEmptyFunc = function(element) {return (this[element] && this[element] != "");}

var notEmptyTables = ["generalAddressTable", "thesaurusTopicsList"];
var notEmptyTableFunc = function(element) {return (this[element].length > 0);}

function isObjectPublishable(idcObject) {
	// Check if all the required fields and tables contain data
	if (!dojo.lang.every(notEmptyFields, notEmptyFunc, idcObject)) {
		dojo.debug("A required field is empty.");
		return false;
	}
	if (!dojo.lang.every(notEmptyTables, notEmptyTableFunc, idcObject)) {
		dojo.debug("A required table is empty.");
		return false;
	}
	
	// Check if one of the 'Raumbezug' tables has an entry with a bounding box
	var snsData = idcObject.spatialRefAdminUnitTable;
	var freeData = idcObject.spatialRefLocationTable;
	var hasBB = function(item) {return (item.longitude1 && item.longitude2 && item.latitude1 && item.latitude2);};
	if ( !(dojo.lang.some(snsData, hasBB) || dojo.lang.some(freeData, hasBB)) ) {
		dojo.debug("At least one 'spatial' table has to contain an entry with a BB.");
		return false;
	}

	// Check if the thesaurus table has at least three entries
	if (idcObject.thesaurusTermsTable.length < 3) {
		dojo.debug("The thesaurus table needs at least three entries.");
		return false;
	}

	// If one of the 'Umweltthemen' contains an entry, both of them need to contain at least one entry
	if ((idcObject.thesaurusEnvTopicsList.length > 0 && idcObject.thesaurusEnvCatsList.length == 0)
	  ||(idcObject.thesaurusEnvTopicsList.length == 0 && idcObject.thesaurusEnvCatsList.length > 0)) {
		dojo.debug("If one of the 'Umweltthemen' contains an entry, both of them need to contain at least one entry.");
		return false;
	}

	// Check the required fields per object class:
	switch (idcObject.objectClass)
	{
		case '0':
			// No additional required fields for object class 0
			break;
		case '1':
			if (!dojo.lang.every(notEmptyFieldsClass1, notEmptyFunc, idcObject)) {
				dojo.debug("Object class one required field empty.");				
				return false;
			}
			break;
		case '2':
			// No additional required fields for object class 2
			break;
		case '3':
			if (!dojo.lang.every(notEmptyFieldsClass3, notEmptyFunc, idcObject)) {
				dojo.debug("Object class three required field empty.");				
				return false;
			}
			break;
		case '4':
			// No additional required fields for object class 4
			break;
		case '5':
			// No additional required fields for object class 5
			break;
		default:
			break;
	}

	return true;
}
