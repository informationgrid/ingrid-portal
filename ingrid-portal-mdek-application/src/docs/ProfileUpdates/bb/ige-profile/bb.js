/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
	
	//find first present node from paths
	var path = ["gmd:editionDate", "gmd:edition", "gmd:date"];
	var nodeBeforeInsert = null;
	for (i=0; i<path.length; i++) {
	    // get the last occurrence of this path if any
	    nodeBeforeInsert = DOM.getElement(srvIdent, "//gmd:CI_Citation/" + path[i]+"[last()]");
	    if (nodeBeforeInsert) { break; }
	}
	
	var MdIdent = nodeBeforeInsert.addElementAsSibling("gmd:identifier");
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

// determine object class
var objId = sourceRecord.get(DatabaseSourceRecord.ID);
var objRows = SQL.all("SELECT * FROM t01_object WHERE id=?", [objId]);
var objClass = "-1", i;
for (i=0; i<objRows.size(); i++) {
	var objRow = objRows.get(i);
	objClass = objRow.get("obj_class");
}

// extract domain as an array ... convert to JS-String first!!!
var extractSubDomain = function(email) {
	var domainSplitted = (email.substring(email.indexOf("@")+1)+"").split(".");
	if (domainSplitted.length > 2) {
		return domainSplitted[domainSplitted.length - 3];
	} else {
		return null;
	}
};
// helper function to append the sub-domain at the right place
var appendDomain = function(uuid, domain) {
	var end = uuid.indexOf("#");
	if (end == -1) {
		return uuid + "/" + domain;
	}
	return uuid.substring(0, end) + "/" + domain + uuid.substring(end);
};

// modify namespace e.g.: //gmd:identificationInfo/gmd:MD_DataIdentification/@uuid="http://portalu.de/igc_bb#45C506E5-3E9D-4DE2-9073-C3DB636CE7CF
var elIdent = DOM.getElement(idfDoc, "//gmd:identificationInfo/gmd:MD_DataIdentification");
if (objClass.equals("3")) {
	elIdent = DOM.getElement(idfDoc, "//gmd:identificationInfo/srv:SV_ServiceIdentification");
}

var subDomain = extractSubDomain(email);

// if 3rd level domain exists
if ( subDomain ) {
	
	var uuid = null;
	if (elIdent) {
		uuid = elIdent.getElement().getAttribute( "uuid" );
		var newUuid = appendDomain( uuid, subDomain );
		elIdent.getElement().setAttribute( "uuid", newUuid );
	}
	
	// modify namespace e.g.: //gmd:identificationInfo/gmd:MD_DataIdentification/gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString = "http://portalu.de/igc_bb#45C506E5-3E9D-4DE2-9073-C3DB636CE7CF
	var elIdentCode = DOM.getElement(elIdent, "//gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString");
	if (elIdentCode) {
		uuid = elIdentCode.getElement().getTextContent();
		var newUuid = appendDomain( uuid, subDomain );
		elIdentCode.getElement().setTextContent( newUuid );
	}
	
	// modify namespace of coupled resource
	var elCoupled = DOM.getElement(elIdent, "srv:coupledResource");
	while (elCoupled) {
		var content = DOM.getElement(elCoupled, "srv:SV_CoupledResource/srv:identifier/gco:CharacterString");
		if (content) {
			uuid = content.getElement().getTextContent();
			var newUuid = appendDomain( uuid, subDomain );
			content.getElement().setTextContent( newUuid );
		}
		
		// get next coupled resource element
		var next = elCoupled.getElement().getNextSibling();
		if (next && next.getNodeName() == "srv:coupledResource" ) {
			elCoupled = DOM.getElement( next, "." );
		} else {
			elCoupled = null;
		}
	}

}

// also adapt references in operatesOn!
if (objClass.equals("3")) {
	var operatesOn = DOM.getElement( elIdent, "srv:operatesOn" );
	while (operatesOn) {
		var refUuid = operatesOn.getElement().getAttribute( "uuidref" );
		// get email contact from referenced dataset which is needed to modify metadata identifier
		var responsibleObj = SQL.first("SELECT responsible_uuid FROM t01_object WHERE obj_uuid=? AND work_state='V'", [refUuid]);
		var emailObj = SQL.first("SELECT comm_value FROM  t02_address adr, t021_communication comm WHERE  adr.adr_uuid=? AND adr.id=comm.adr_id AND commtype_key=3", [responsibleObj.get("responsible_uuid")]);
		var subDomain = extractSubDomain( emailObj.get("comm_value") );
		if ( subDomain ) {
			var operatesHref = operatesOn.getElement().getAttribute( "xlink:href" );
			var newUuid = appendDomain( operatesHref, subDomain );
			operatesOn.getElement().setAttribute( "xlink:href", newUuid );
		}
		
		// get next operatesOn element
		var next = operatesOn.getElement().getNextSibling();
		if (next) {
			operatesOn = DOM.getElement( next, "." );
		} else {
			operatesOn = null;
		}
	}
}
