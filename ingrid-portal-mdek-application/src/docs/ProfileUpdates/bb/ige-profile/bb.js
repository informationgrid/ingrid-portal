// Diese Mapping-Definition bewirkt, daß zu einem ServiceIdentification-Element kein
// zusätzliches DataIdentification-Element generiert wird
// (Bedingung ume Be/BB Metadatenprofil)
// (Das wäre bei Daten in folgenden Feldern der Fall:
//  Erstellungsmaßstab /Bodenauflösung, Systemumgebung/ Historie / Erläuterungen

if (XPATH.nodeExists( idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo/srv:SV_ServiceIdentification" )) {
    XPATH.removeElementAtXPath( idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo/gmd:MD_DataIdentification/.." );
}




// ------------------------
// IDF - REDMINE-354
// ------------------------
importPackage(Packages.de.ingrid.iplug.dsc.om);
//add Namespaces to Utility for convenient handling of NS !
DOM.addNS("gmd", "http://www.isotc211.org/2005/gmd");
DOM.addNS("gco", "http://www.isotc211.org/2005/gco");

if (!(sourceRecord instanceof DatabaseSourceRecord)) {
  throw new IllegalArgumentException("Record is no DatabaseRecord!");
}

var objId = sourceRecord.get(DatabaseSourceRecord.ID);
var objRows = SQL.all("SELECT * FROM t01_object WHERE id=?", [objId]);
var objClass = "-1", i;
for (i=0; i<objRows.size(); i++) {
    var objRow = objRows.get(i);
    objClass = objRow.get("obj_class");
}

if (objClass.equals("3") || objClass.equals("6")) {
	// get ID from ServiceIdentification
	var srvIdent = null;
	if (objClass.equals("3")) {
		srvIdent = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo/srv:SV_ServiceIdentification");
	} else {
		srvIdent = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo/gmd:MD_DataIdentification ");
	}
	var uuid = srvIdent.getElement().getAttribute( "uuid" );
	var citationDate = DOM.getElement(srvIdent, "//gmd:CI_Citation/gmd:date");
	
	var MdIdent = citationDate.addElementAsSibling("gmd:identifier");
	MdIdent.addElement("gmd:MD_Identifier/gmd:code/gco:CharacterString").addText( uuid );
}




//------------------------
// IDF - REDMINE-355
//------------------------
importPackage(Packages.de.ingrid.iplug.dsc.om);
//add Namespaces to Utility for convenient handling of NS !
DOM.addNS("gmd", "http://www.isotc211.org/2005/gmd");
DOM.addNS("gco", "http://www.isotc211.org/2005/gco");

if (!(sourceRecord instanceof DatabaseSourceRecord)) {
throw new IllegalArgumentException("Record is no DatabaseRecord!");
}

// get contact email address
var email = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:contact//gmd:electronicMailAddress/gco:CharacterString").getElement().getTextContent();

// extract domain as an array ... convert to JS-String first!!!
var domain = (email.substring(email.indexOf("@")+1)+"").split(".");

// if 3rd level domain exists
if ( domain.length > 2 ) {
	
	var appendDomain = function(uuid, domain) {
		var end = uuid.indexOf("#");
		if (end == -1) {
			return uuid + "/" + domain
		}
		return uuid.substring(0, end) + "/" + domain + uuid.substring(end);
	};
	
	// extract third level domain
	var tlDomain = domain[domain.length - 3];
	
	// determine object class
	var objId = sourceRecord.get(DatabaseSourceRecord.ID);
	var objRows = SQL.all("SELECT * FROM t01_object WHERE id=?", [objId]);
	var objClass = "-1", i;
	for (i=0; i<objRows.size(); i++) {
	    var objRow = objRows.get(i);
	    objClass = objRow.get("obj_class");
	}

	// modify namespace e.g.: //gmd:identificationInfo/gmd:MD_DataIdentification/@uuid="http://portalu.de/igc_bb#45C506E5-3E9D-4DE2-9073-C3DB636CE7CF
	var el = DOM.getElement(idfDoc, "//gmd:identificationInfo/gmd:MD_DataIdentification");
	if (objClass.equals("3")) {
		el = DOM.getElement(idfDoc, "//gmd:identificationInfo/srv:SV_ServiceIdentification");
	}
	
	var uuid = null;
	if (el) {
		uuid = el.getElement().getAttribute( "uuid" );
		var newUuid = appendDomain( uuid, tlDomain );
		el.getElement().setAttribute( "uuid", newUuid );
	}
	
	// modify namespace e.g.: //gmd:identificationInfo/gmd:MD_DataIdentification/gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString = "http://portalu.de/igc_bb#45C506E5-3E9D-4DE2-9073-C3DB636CE7CF
	if (el) el = DOM.getElement(el, "//gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString");
	if (el) { // 
		uuid = el.getElement().getTextContent();
		var newUuid = appendDomain( uuid, tlDomain );
		el.getElement().setTextContent( newUuid );
	}
}
