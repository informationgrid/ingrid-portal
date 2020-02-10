/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
// JS Code für LGV HH 3.3.1 zum Hinzufügen in "Gesamtkatalogmanagement -> Zusätzliche Felder"

// ========================================================================================================================
// Zusätzliches Feld -> Checkbox: "Veröffentlichung gemäß HmbTG" (REDMINE-192)
// TODO:
// - Zusätzliches Feld anlegen unter "Allgemeines - Kategorien"
//   - Checkbox (Id:'publicationHmbTG', Sichtbarkeit:anzeigen, Beschriftung:'Veröffentlichung gemäß HmbTG', Hilfetext:?)
//   - nachfolgendes Javascript (JS) und IDF-Mapping hinzufügen
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
var contentLabel = SQL.all("SELECT add1.data FROM additional_field_data add1 WHERE add1.obj_id=? AND add1.field_key=?", [id, igcProfileControlId]);
if (contentLabel && contentLabel.size() > 0) {
    var isChecked = contentLabel.get(0).get("data") == "true";
    if (isChecked) {
        
        var objRow = SQL.first("SELECT obj_class FROM t01_object WHERE id=?", [id]);
        var objClass = objRow.get("obj_class");

        var i;
        var dataIdentification;
        if (objClass.equals("3")) {
            dataIdentification = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo/srv:SV_ServiceIdentification");
        } else {
            dataIdentification = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo/gmd:MD_DataIdentification");
        }
        
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
//   - nachfolgendes IDF-Mapping hinzufügen (z.B. in neuem Zusätzlichem Feld, das immer versteckt wird !)
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

//var id = sourceRecord.get(DatabaseSourceRecord.ID);
// add "#opendata_hh#" keyword if opendata keyword set ! (opendata keyword is set if checkbox Open Data activated !
var openDataKeyword = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo//gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword[gco:CharacterString='opendata']");
var openDataKeywordHH = DOM.getElement(idfDoc, "//idf:idfMdMetadata/gmd:identificationInfo//gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword[gco:CharacterString='#opendata_hh#']");
if (openDataKeyword && !openDataKeywordHH) {
    var hhKeyword = openDataKeyword.addElementAsSibling("gmd:keyword");
    hhKeyword.addElement("gco:CharacterString").addText("#opendata_hh#");
}

