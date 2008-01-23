/*
 * Functions for checking the validity of entered values.
 */

dojo.addOnLoad(function() {});

var labels = ["objectNameLabel", "objectClassLabel", "generalDescLabel", "extraInfoLangDataLabel", "extraInfoLangMetaDataLabel",
			  "ref1DataSetLabel", "ref3ServiceTypeLabel", "generalAddressTableLabel", "thesaurusTermsLabel",
			  "thesaurusTopicsLabel", "spatialRefAdminUnitLabel", "spatialRefLocationLabel", "thesaurusEnvTopicsLabel",
			  "thesaurusEnvCatsLabel"];

var notEmptyFields = [["objectName", "objectNameLabel"],
					  ["objectClass", "objectClassLabel"],
					  ["generalDescription", "generalDescLabel"],
					  ["extraInfoLangData", "extraInfoLangDataLabel"],
					  ["extraInfoLangMetaData", "extraInfoLangMetaDataLabel"]];

var notEmptyFieldsClass1 = [["ref1DataSet", "ref1DataSetLabel"]]; 
var notEmptyFieldsClass3 = [["ref3ServiceType", "ref3ServiceTypeLabel"]];
var notEmptyFunc = function(element) {return (this[element] && this[element] != "");}

var notEmptyTables = [["generalAddressTable", "generalAddressTableLabel"],
					  ["thesaurusTopicsList", "thesaurusTopicsLabel"]];
var notEmptyTableFunc = function(element) {return (this[element].length > 0);}


function isObjectPublishable(idcObject) {
	var publishable = true;

	for (i in labels) {
		dojo.html.removeClass(dojo.byId(labels[i]), "important");	
	}


	for (i in notEmptyFields) {
		if (!idcObject[notEmptyFields[i][0]] || idcObject[notEmptyFields[i][0]] == "") {
			dojo.html.addClass(dojo.byId(notEmptyFields[i][1]), "important");
			publishable = false;
		}
	}

	for (i in notEmptyTables) {
		if (idcObject[notEmptyTables[i][0]].length == 0) {
			dojo.html.addClass(dojo.byId(notEmptyTables[i][1]), "important");
			publishable = false;
		}
	}
/*
	var span = dojo.byId("thesaurusTermsLabel");
	if (dojo.html.hasClass(span, "important")) {
		dojo.html.removeClass(span, "important");
	} else {
		dojo.html.addClass(span, "important");	
	}
*/
/*
	// Check if all the required fields and tables contain data
	if (!dojo.lang.every(notEmptyFields, notEmptyFunc, idcObject)) {
		dojo.debug("A required field is empty.");
		return false;
	}
*/
/*
	if (!dojo.lang.every(notEmptyTables, notEmptyTableFunc, idcObject)) {
		dojo.debug("A required table is empty.");
		return false;
	}
*/
	// Check if one of the 'Raumbezug' tables has an entry with a bounding box
	var snsData = idcObject.spatialRefAdminUnitTable;
	var freeData = idcObject.spatialRefLocationTable;
	var hasBB = function(item) {return (item.longitude1 && item.longitude2 && item.latitude1 && item.latitude2);};
	if ( !(dojo.lang.some(snsData, hasBB) || dojo.lang.some(freeData, hasBB)) ) {
		dojo.html.addClass(dojo.byId("spatialRefAdminUnitLabel"), "important");
		dojo.html.addClass(dojo.byId("spatialRefLocationLabel"), "important");
		dojo.debug("At least one 'spatial' table has to contain an entry with a BB.");
		publishable = false;
	}

	// Check if the thesaurus table has at least three entries
	if (idcObject.thesaurusTermsTable.length < 3) {
		dojo.html.addClass(dojo.byId("thesaurusTermsLabel"), "important");		
		dojo.debug("The thesaurus table needs at least three entries.");
		publishable = false;
	}

	// If one of the 'Umweltthemen' contains an entry, both of them need to contain at least one entry
	if ((idcObject.thesaurusEnvTopicsList.length > 0 && idcObject.thesaurusEnvCatsList.length == 0)
	  ||(idcObject.thesaurusEnvTopicsList.length == 0 && idcObject.thesaurusEnvCatsList.length > 0)) {
		dojo.html.addClass(dojo.byId("thesaurusEnvTopicsLabel"), "important");		
		dojo.html.addClass(dojo.byId("thesaurusEnvCatsLabel"), "important");		
		dojo.debug("If one of the 'Umweltthemen' contains an entry, both of them need to contain at least one entry.");
		publishable = false;
	}

	// Check the required fields per object class:
	switch (idcObject.objectClass)
	{
		case '0':
			// No additional required fields for object class 0
			break;
		case '1':
			for (i in notEmptyFieldsClass1) {
				if (!idcObject[notEmptyFieldsClass1[i][0]] || idcObject[notEmptyFieldsClass1[i][0]] == "") {
					dojo.html.addClass(dojo.byId(notEmptyFieldsClass1[i][1]), "important");
					publishable = false;
					dojo.debug("Object class one required field empty.");				
				}
			}
			break;
		case '2':
			// No additional required fields for object class 2
			break;
		case '3':
			for (i in notEmptyFieldsClass3) {
				if (!idcObject[notEmptyFieldsClass3[i][0]] || idcObject[notEmptyFieldsClass3[i][0]] == "") {
					dojo.html.addClass(dojo.byId(notEmptyFieldsClass3[i][1]), "important");
					publishable = false;
					dojo.debug("Object class three required field empty.");				
				}
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

	return publishable;
}
