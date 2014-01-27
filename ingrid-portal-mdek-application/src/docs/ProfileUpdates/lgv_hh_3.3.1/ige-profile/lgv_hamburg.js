// JS Code für LGV HH 3.3.1 zum Hinzufügen in "Gesamtkatalogmanagement -> Zusätzliche Felder"

// ========================================================================================================================
// Defaultwerte bei neuem Objekt der Objektklasse "Geodatensatz" (REDMINE-119)
// TODO:
//   - nachfolgendes Javascript (JS) hinzufügen z.B. in 1. Feld "Allgemeines - Kurzbezeichnung"
// ========================================================================================================================
// ------------------------
// JS
// ------------------------
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


// ========================================================================================================================
// Zusätzliches Feld -> Checkbox: "Veröffentlichung gemäß HmbTG" (REDMINE-192)
// TODO:
// - Zusätzliches Feld anlegen unter "Allgemeines - Kategorien"
//   - Checkbox (Id:'publicationHmbTG', Sichtbarkeit:anzeigen, Beschriftung:'Veröffentlichung gemäß HmbTG', Hilfetext:?)
//   - nachfolgendes Javascript (JS) und IDF-Mapping hinzufügen
// ========================================================================================================================
// ------------------------
// JS
// ------------------------
dojo.connect(dijit.byId("publicationHmbTG"), "onChange", function(isChecked) {
    if (isChecked) {
        dojo.addClass("uiElementAddInformationsgegenstand", "required");
    } else {
        dojo.removeClass("uiElementAddInformationsgegenstand", "required");
    }
});

// tick checkbox if "open data" has been selected (REDMINE-194)
dojo.connect(dijit.byId("isOpenData"), "onChange", function(isChecked) {
    if (isChecked) {
        dijit.byId("publicationHmbTG").set("value", true);
    }
});

// ------------------------
// IDF
// ------------------------
importPackage(Packages.de.ingrid.iplug.dsc.om);
//add Namespaces to Utility for convenient handling of NS !
DOM.addNS("gmd", "http://www.isotc211.org/2005/gmd");
DOM.addNS("gco", "http://www.isotc211.org/2005/gco");

if (!(sourceRecord instanceof DatabaseSourceRecord)) {
  throw new IllegalArgumentException("Record is no DatabaseRecord!");
}

var id = sourceRecord.get(DatabaseSourceRecord.ID);
var igcProfileControlId = XPATH.getString(igcProfileControlNode, "igcp:id");
var contentLabel = SQL.all("SELECT add1.data FROM additional_field_data add1 WHERE add1.obj_id=? AND add1.field_key=?", [id, igcProfileControlId]);
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


// ========================================================================================================================
// Zusätzliches Auswahlfeld/Liste: "Informationsgegenstand" (REDMINE-193)
// TODO:
// - Zusätzliches Feld anlegen unter erzeugter Checkbox "Veröffentlichung gemäß HmbTG" (s.o.)
//   - Tabelle (Id:'Informationsgegenstand', Beschriftung:'Informationsgegenstand', Hilfetext:?)
//     mit einer Spalte(Typ: Liste, Id:'informationHmbTG', Breite: 691px, Indexname: 'infoHmbTG', Einträge: ensprechende Codeliste -> 
/*
Codeliste für das Feld „Informationsgegenstand“ (id / option)
{
  "hmbtg_01_senatsbeschluss": "Senatsbeschlüsse",
  "hmbtg_02_mitteilung_buergerschaft": "Mitteilungen des Senats",
  "hmbtg_03_beschluesse_oeffentliche_sitzung": "Öffentliche Beschlüsse",
  "hmbtg_04_vertraege_daseinsvorsorge": "Verträge der Daseinsvorsorge",
  "hmbtg_05_verwaltungsplaene": "Verwaltungspläne",
  "hmbtg_06_verwaltungsvorschriften": "Verwaltungsvorschriften",
  "hmbtg_07_statistiken": "Statistiken und Tätigkeitsberichte",
  "hmbtg_08_gutachten": "Gutachten und Studien",
  "hmbtg_09_geodaten": "Geodaten",
  "hmbtg_10_messungen": "Umweltdaten",
  "hmbtg_11_baumkataster": "Baumkataster",
  "hmbtg_12_oeffentliche_plaene": "Öffentliche Pläne",
  "hmbtg_13_baugenehmigungen": "Baugenehmigungen",
  "hmbtg_14_zuwendungen_subventionen": "Subventionen und Zuwendungen",
  "hmbtg_15_unternehmensdaten": "Unternehmensdaten",
  "hmbtg_16_vertraege_oeffentl_interesse": "Verträge von öffentl. Interesse",
  "hmbtg_17_dienstanweisungen": "Dienstanweisungen",
  "hmbtg_18_vergleichbar": "vergleichbare Informationen von öffentl. Interesse",
  "hmbtg_19_andere_veroeffentlichungspflicht": "Veröffentlichungspflicht außerhalb HmbTG",
  "hmbtg_20_ohne_veroeffentlichungspflicht": "Ohne gesetzliche Verpflichtung" 
}
*/
//   - nachfolgendes IDF-Mapping hinzufügen
// ========================================================================================================================
// ------------------------
// IDF
// ------------------------
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
var contentLabel = SQL.all("SELECT add2.data, add2.list_item_id FROM additional_field_data add1, additional_field_data add2 WHERE add1.obj_id=? AND add1.field_key=? AND add2.parent_field_id=add1.id AND add2.field_key=?", [id, igcProfileControlId, columnName]);

if ( contentLabel && contentLabel.size() > 0) {
  var i;
  var dataMetadata = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo");
  var dataIdentification = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo/gmd:MD_DataIdentification");

  if (!dataIdentification) dataIdentification = dataMetadata.addElement("gmd:MD_DataIdentification");

  var path = ["gmd:resourceFormat", "gmd:graphicOverview", "gmd:resourceMaintenance","gmd:pointOfContact", "gmd:status","gmd:credit","gmd:purpose"];

  // find first present node from paths
  var nodeBeforeInsert = null;
  for (var i=0; i<path.length; i++) {
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


// ========================================================================================================================
// Wenn "OpenData" gesetzt, dann neues Keyword "#opendata_hh#" und Felder ein-/ausblenden ... (REDMINE-117)
// TODO:
//   - nachfolgendes Javascript (JS) hinzufügen z.B. in Feld "Allgemeines - Open Data"
//   - nachfolgendes IDF-Mapping hinzufügen (z.B. in neuem Zusätzlichem Feld, das immer versteckt wird !)
// ========================================================================================================================
// ------------------------
// JS
// ------------------------
var openDataAddressCheck = null;
dojo.connect(dijit.byId("isOpenData"), "onChange", function(isChecked) {
    if (isChecked) {
        // add check for address of type publisher (Herausgeber) when publishing
        // we check name and not id cause is combo box ! id not adapted yet if not saved !
        openDataAddressCheck = dojo.subscribe("/onBeforeObjectPublish", function(/*Array*/notPublishableIDs) {
            // get name of codelist entry for entry-id "10" = "publisher"/"Herausgeber"
            var entryNamePublisher = UtilSyslist.getSyslistEntryName(505, 10);
        
            // check if entry already exists in table
            var data = UtilGrid.getTableData("generalAddress");
            var containsPublisher = dojo.some(data, function(item) { if (item.nameOfRelation == entryNamePublisher) return true; });
            if (!containsPublisher)
                notPublishableIDs.push("generalAddress");
        });

        // set publication condition to Internet
        dijit.byId("extraInfoPublishArea").attr("value", 1, true);

        // set Nutzungsbedingungen to "Datenlizenz Deutschland Namensnennung". Extract from syslist !
        var entryNameLicense = UtilSyslist.getSyslistEntryName(6500, 1);
        dijit.byId("availabilityUseConstraints").attr("value", entryNameLicense, true);

        // SHOW mandatory fields ONLY IF EXPANDED !
        dojo.addClass("uiElement5064", "showOnlyExpanded"); // INSPIRE-Themen
        dojo.addClass("uiElement3520", "showOnlyExpanded"); // Fachliche Grundlage
        dojo.addClass("uiElement5061", "showOnlyExpanded"); // Datensatz/Datenserie
        dojo.addClass("uiElement3565", "showOnlyExpanded"); // Datendefizit
        dojo.addClass("uiElementN006", "showOnlyExpanded"); // Geothesaurus-Raumbezug
        dojo.addClass("uiElement3500", "showOnlyExpanded"); // Raumbezugssystem
        dojo.addClass("uiElement5041", "showOnlyExpanded"); // Sprache des Metadatensatzes
        dojo.addClass("uiElement5042", "showOnlyExpanded"); // Sprache der Ressource
        dojo.addClass("uiElementN024", "showOnlyExpanded"); // Konformität
        dojo.addClass("uiElement1315", "showOnlyExpanded"); // Kodierungsschema

    } else {
        // unregister from check for publisher address
        if (openDataAddressCheck)
            dojo.unsubscribe(openDataAddressCheck);

        // remove "keine" from access constraints
        var data = UtilGrid.getTableData('availabilityAccessConstraints');
        var posToRemove = 0;
        var entryExists = dojo.some(data, function(item) {
            if (item.title == "keine") {
                return true;
            }
            posToRemove++;
        });
        if (entryExists) {
            UtilGrid.removeTableDataRow('availabilityAccessConstraints', [posToRemove]);
        }

        // remove license set when open data was clicked
        dijit.byId("availabilityUseConstraints").attr("value", "");

        // ALWAYS SHOW mandatory fields !
        dojo.removeClass("uiElement5064", "showOnlyExpanded"); // INSPIRE-Themen
        dojo.removeClass("uiElement3520", "showOnlyExpanded"); // Fachliche Grundlage
        dojo.removeClass("uiElement5061", "showOnlyExpanded"); // Datensatz/Datenserie
        dojo.removeClass("uiElement3565", "showOnlyExpanded"); // Datendefizit
        dojo.removeClass("uiElementN006", "showOnlyExpanded"); // Geothesaurus-Raumbezug
        dojo.removeClass("uiElement3500", "showOnlyExpanded"); // Raumbezugssystem
        dojo.removeClass("uiElement5041", "showOnlyExpanded"); // Sprache des Metadatensatzes
        dojo.removeClass("uiElement5042", "showOnlyExpanded"); // Sprache der Ressource
        dojo.removeClass("uiElementN024", "showOnlyExpanded"); // Konformität
        dojo.removeClass("uiElement1315", "showOnlyExpanded"); // Kodierungsschema

        // Tab containers may be rendered for the first time and needs to be layouted
        igeEvents.refreshTabContainers();
    }
});

// ------------------------
// IDF
// ------------------------
importPackage(Packages.de.ingrid.iplug.dsc.om);
//add Namespaces to Utility for convenient handling of NS !
DOM.addNS("gmd", "http://www.isotc211.org/2005/gmd");
DOM.addNS("gco", "http://www.isotc211.org/2005/gco");

if (!(sourceRecord instanceof DatabaseSourceRecord)) {
  throw new IllegalArgumentException("Record is no DatabaseRecord!");
}

//var id = sourceRecord.get(DatabaseSourceRecord.ID);
// add "#opendata_hh#" keyword if opendata keyword set ! (opendata keyword is set if checkbox Open Data activated !
var openDataKeyword = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword[gco:CharacterString='opendata']");
if (openDataKeyword) {
    var hhKeyword = openDataKeyword.addElementAsSibling("gmd:keyword");
    hhKeyword.addElement("gco:CharacterString").addText("#opendata_hh#");
}
