// ------------------------
// IDF - INGRID-2365
// - für alle Adressen im IGE (pointOfContact)
// - mit der Rolle "Eigentümer" (owner) (Lst 505, List-ID 3)
// - das ISO-Element 383: administrativeArea in das IDF mit ausgespielen (Bundesland)
// ------------------------
importPackage(Packages.de.ingrid.iplug.dsc.om);
//add Namespaces to Utility for convenient handling of NS !
DOM.addNS("gmd", "http://www.isotc211.org/2005/gmd");
DOM.addNS("gco", "http://www.isotc211.org/2005/gco");

if (!(sourceRecord instanceof DatabaseSourceRecord)) {
  throw new IllegalArgumentException("Record is no DatabaseRecord!");
}
var objId = sourceRecord.get(DatabaseSourceRecord.ID);

// SET ADMINISTRATIVE AREA TO USE FOR ALL OWNERS !
var myAdministrativeArea = "Hessen";

// check all pointOfContact elements and add administrativeArea where type = "owner"
var elPOC = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo/*/gmd:pointOfContact");
while (elPOC) {
    var isOwner = DOM.getElement(elPOC, "idf:idfResponsibleParty/gmd:role/gmd:CI_RoleCode[@codeListValue='owner']");
    if (isOwner) {
        // get address and node where to add administrativeArea as sibling
        var elAddress = DOM.getElement(elPOC, "idf:idfResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address");
        if (elAddress) {
            var path = ["gmd:city", "gmd:deliveryPoint"];
            // find first present node from paths
            var nodeBeforeInsert = null;
            for (var i=0; i<path.length; i++) {
                // get the last occurrence of this path if any
                nodeBeforeInsert = DOM.getElement(elAddress, path[i]+"[last()]");
                if (nodeBeforeInsert) { break; }
            }
            // add administrativeArea
            var elAArea;
            if (nodeBeforeInsert) {
                elAArea = nodeBeforeInsert.addElementAsSibling("gmd:administrativeArea");
            } else {
                elAArea = elAddress.addElement("gmd:administrativeArea");
            }
            log.debug("Additional IDF obj:" + objId + ": Add administrativeArea:" + myAdministrativeArea + " to pointOfContact of type owner.");
            elAArea.addElement("gco:CharacterString").addText(myAdministrativeArea);
        }
    }
		
    // get next pointOfContact element
    var next = elPOC.getElement().getNextSibling();
    if (next && next.getNodeName() == "gmd:pointOfContact" ) {
        elPOC = DOM.getElement( next, "." );
    } else {
        elPOC = null;
    }
}
