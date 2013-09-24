// ------------------------------------------------------------
// JS
// default values when creating new objects of class 1 - geodata (REDMINE-119)
dojo.subscribe("/onObjectClassChange", function(data) { 
    if (currentUdk.uuid === "newNode" && data.objClass === "Class1") { 
        dijit.byId("ref1BasisText").set("value", "keine Angabe");
        dijit.byId("ref1DataSet").set("value", "5"); // "Datensatz"
        UtilGrid.setTableData("thesaurusInspire", [{title: 99999}]); // "Kein INSPIRE-Thema"
        UtilGrid.setTableData("extraInfoConformityTable", [{specification:"INSPIRE-Richtlinie", level:3}]); // "nicht evaluiert"
        UtilGrid.setTableData("ref1SpatialSystem", [{title:"EPSG 25832: ETRS89 / UTM Zone 32N"}]);
        dijit.byId("availabilityDataFormatInspire").set("value", "Geographic Markup Language (GML)");
    }
});


//------------------------------------------------------------
// List: "Informationsgegenstand"
// ----------------------------
- Erstellung einer Tabelle mit einer Spalte(ID:'informationHmbTG')
- Spaltentyp ist eine Liste in der die Codeliste übertragen wird

// IDF
importPackage(Packages.de.ingrid.iplug.dsc.om);
//add Namespaces to Utility for convenient handling of NS !
DOM.addNS("gmd", "http://www.isotc211.org/2005/gmd");
DOM.addNS("gco", "http://www.isotc211.org/2005/gco");

if (!(sourceRecord instanceof DatabaseSourceRecord)) {
    throw new IllegalArgumentException("Record is no DatabaseRecord!");
}

var id = sourceRecord.get(DatabaseSourceRecord.ID);
var igcProfileControlId = XPATH.getString(igcProfileControlNode, "igcp:id");
var columnName = 'informationHmbTG'; // the column of the table to get the value from
var contentLabel = SQL.all("SELECT add2.data, add2.list_item_id FROM `additional_field_data` as add1, `additional_field_data` as add2 WHERE add1.obj_id=? AND add1.field_key=? AND add2.parent_field_id=add1.id AND add2.`field_key`=?", [id, igcProfileControlId, columnName]);

if (contentLabel && contentLabel.size() > 0) {
    var i;
    var dataIdentification = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo/gmd:MD_DataIdentification");
    
    var path = ["gmd:resourceFormat", "gmd:graphicOverview", "gmd:resourceMaintenance","gmd:pointOfContact", "gmd:status","gmd:credit","gmd:purpose"];
    
    // find first present node from paths
    var nodeBeforeInsert = null;
    for (i=0; i<path.length; i++) {
        // get the last occurrence of this path if any
        nodeBeforeInsert = DOM.getElement(dataIdentification, path[i]+"[last()]");
        if (nodeBeforeInsert) { break; }
    }
    
    // write keys of thesaurus codelist
    var keywords;
    var keywordsParent;
    if (nodeBeforeInsert) {
        keywordsParent = nodeBeforeInsert.addElementAsSibling("gmd:descriptiveKeywords");
    } else {
        keywordsParent = dataIdentification.addElement("gmd:descriptiveKeywords");
    }
    keywords = keywordsParent.addElement("gmd:MD_Keywords");
    
    for (i=0; i<contentLabel.size(); i++) {
        keywords.addElement("gmd:keyword/gco:CharacterString").addText(contentLabel.get(i).get("list_item_id"));
    }
        
    keywords.addElement("gmd:type/gmd:MD_KeywordTypeCode")
        .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml#MD_KeywordTypeCode")
        .addAttribute("codeListValue", "theme");
    
    var thesCit = keywords.addElement("gmd:thesaurusName/gmd:CI_Citation");
    thesCit.addElement("gmd:title/gco:CharacterString").addText("HmbTG-Informationsgegenstand");
    var thesCitDate = thesCit.addElement("gmd:date/gmd:CI_Date");
    thesCitDate.addElement("gmd:date/gco:Date").addText("2013-08-02");
    thesCitDate.addElement("gmd:dateType/gmd:CI_DateTypeCode")
    .addAttribute("codeListValue", "publication")
    .addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#CI_DateTypeCode");
    
    // write values of all keywords (no relation to thesaurus anymore!)
    keywords = keywordsParent.addElementAsSibling("gmd:descriptiveKeywords/gmd:MD_Keywords");
    for (i=0; i<contentLabel.size(); i++) {
        keywords.addElement("gmd:keyword/gco:CharacterString").addText(contentLabel.get(i).get("data"));
    }
}


// ------------------------------------------------------------
// Checkbox: "Veröffentlichung gemäß HmbTG" (REDMINE-192)
- Erstellung einer einfachen Checkbox mit der ID: publicationHmbTG

// JS
dojo.connect(dijit.byId("publicationHmbTG"), "onChange", function(isChecked) {
    if (isChecked) {
        UtilUI.setMandatory("uiElementAddtableInformation");
    } else {
        UtilUI.setOptional("uiElementAddtableInformation");
    }
});

// IDF
importPackage(Packages.de.ingrid.iplug.dsc.om);
//add Namespaces to Utility for convenient handling of NS !
DOM.addNS("gmd", "http://www.isotc211.org/2005/gmd");
DOM.addNS("gco", "http://www.isotc211.org/2005/gco");

if (!(sourceRecord instanceof DatabaseSourceRecord)) {
  throw new IllegalArgumentException("Record is no DatabaseRecord!");
}

var id = sourceRecord.get(DatabaseSourceRecord.ID);
var igcProfileControlId = XPATH.getString(igcProfileControlNode, "igcp:id");
var contentLabel = SQL.all("SELECT add1.data FROM `additional_field_data` as add1 WHERE add1.obj_id=? AND add1.field_key=?", [id, igcProfileControlId]);
if (contentLabel && contentLabel.size() > 0) {
    var isChecked = contentLabel.get(0).get("data") == "true";
    if (isChecked) {
        
        var i;
        var dataIdentification = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo/gmd:MD_DataIdentification");
        
        var path = ["gmd:resourceFormat", "gmd:graphicOverview", "gmd:resourceMaintenance","gmd:pointOfContact", "gmd:status","gmd:credit","gmd:purpose"];
        
        // find first present node from paths
        var nodeBeforeInsert = null;
        for (i=0; i<path.length; i++) {
            // get the last occurrence of this path if any
        nodeBeforeInsert = DOM.getElement(dataIdentification, path[i]+"[last()]");
            if (nodeBeforeInsert) { break; }
        }
        
        // write keys of thesaurus codelist
        var keywords;
        var keywordsParent;
        if (nodeBeforeInsert) {
            keywordsParent = nodeBeforeInsert.addElementAsSibling("gmd:descriptiveKeywords");
        } else {
            keywordsParent = dataIdentification.addElement("gmd:descriptiveKeywords");
        }
        keywords = keywordsParent.addElement("gmd:MD_Keywords");
        keywords.addElement("gmd:keyword/gco:CharacterString").addText("hmbtg");
    }
}

//------------------------------------------------------------