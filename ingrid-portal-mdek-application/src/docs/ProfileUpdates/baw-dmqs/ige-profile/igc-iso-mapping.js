/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
// JS Code für BAW DMGS zum Hinzufügen in "Gesamtkatalogmanagement -> Zusätzliche Felder"

// ========================================================================================================================
// Zusätzliches Feld -> Liste: "Simulation / Räumliche Dimensionalität"
// TODO:
// - Zusätzliches Feld anlegen unter "Allgemeines - Kategorien"
//   - Liste (Id:'simSpatialDimension', Sichtbarkeit: Pflichtfeld, Beschriftung:'Simulation / Räumliche Dimensionalität', Hilfetext:?)
//   - nachfolgendes Javascript in Feld IDF-Mapping hinzufügen
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
var simSpatialDimensionRecord = SQL.all("SELECT add1.data FROM additional_field_data add1 WHERE add1.obj_id=? AND add1.field_key=?", [id, igcProfileControlId]);
if (simSpatialDimensionRecord && simSpatialDimensionRecord.size() > 0) {
	var simSpatialDimension = simSpatialDimensionRecord.get(0).get("data");
    if (simSpatialDimension) {
        
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
        
        // write keyword and thesaurus
        var keywords;
        var keywordsParent;
        if (nodeBeforeInsert) {
            keywordsParent = nodeBeforeInsert.addElementAsSibling("gmd:descriptiveKeywords");
        } else {
            keywordsParent = dataIdentification.addElement("gmd:descriptiveKeywords");
        }
        keywords = keywordsParent.addElement("gmd:MD_Keywords");
        keywords.addElement("gmd:keyword/gco:CharacterString").addText(simSpatialDimension);
        keywords.addElement("gmd:type/gmd:MD_KeywordTypeCode").addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_KeywordTypeCode").addAttribute("codeListValue", "theme");
        var thesCitation = keywords.addElement("gmd:thesaurusName/gmd:CI_Citation");
        thesCitation.addElement("gmd:title/gco:CharacterString").addText("BAW-DMQS Spatial Dimensions");
        var ciDate = thesCitation.addElement("gmd:date/gmd:CI_Date");
        ciDate.addElement("gmd:date/gco:Date").addText("2016-04-08");
        ciDate.addElement("gmd:dateType/gmd:CI_DateTypeCode").addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#CI_DateTypeCode").addAttribute("codeListValue", "publication");;
    }
}

//========================================================================================================================
//Zusätzliches Feld -> Liste: "Simulation / Verfahren"
//TODO:
//- Zusätzliches Feld anlegen unter "Allgemeines - Kategorien"
//- Liste (Id:'simProcess', Sichtbarkeit: Pflichtfeld, Beschriftung:'Simulation / Verfahren', Hilfetext:?)
//- nachfolgendes Javascript in Feld IDF-Mapping hinzufügen
//========================================================================================================================


//------------------------
//IDF
//------------------------
importPackage(Packages.de.ingrid.iplug.dsc.om);
//add Namespaces to Utility for convenient handling of NS !
DOM.addNS("gmd", "http://www.isotc211.org/2005/gmd");
DOM.addNS("gco", "http://www.isotc211.org/2005/gco");

if (!(sourceRecord instanceof DatabaseSourceRecord)) {
throw new IllegalArgumentException("Record is no DatabaseRecord!");
}

var id = sourceRecord.get(DatabaseSourceRecord.ID);
var igcProfileControlId = XPATH.getString(igcProfileControlNode, "igcp:id");
var simProcessRecord = SQL.all("SELECT add1.data FROM additional_field_data add1 WHERE add1.obj_id=? AND add1.field_key=?", [id, igcProfileControlId]);
if (simProcessRecord && simProcessRecord.size() > 0) {
	var simProcess = simProcessRecord.get(0).get("data");
 if (simProcess) {
     
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
     
     // write keyword and thesaurus
     var keywords;
     var keywordsParent;
     if (nodeBeforeInsert) {
         keywordsParent = nodeBeforeInsert.addElementAsSibling("gmd:descriptiveKeywords");
     } else {
         keywordsParent = dataIdentification.addElement("gmd:descriptiveKeywords");
     }
     keywords = keywordsParent.addElement("gmd:MD_Keywords");
     keywords.addElement("gmd:keyword/gco:CharacterString").addText(simProcess);
     keywords.addElement("gmd:type/gmd:MD_KeywordTypeCode").addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_KeywordTypeCode").addAttribute("codeListValue", "theme");
     var thesCitation = keywords.addElement("gmd:thesaurusName/gmd:CI_Citation");
     thesCitation.addElement("gmd:title/gco:CharacterString").addText("BAW-DMQS Modelling Method");
     var ciDate = thesCitation.addElement("gmd:date/gmd:CI_Date");
     ciDate.addElement("gmd:date/gco:Date").addText("2016-04-08");
     ciDate.addElement("gmd:dateType/gmd:CI_DateTypeCode").addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#CI_DateTypeCode").addAttribute("codeListValue", "publication");;
 }
}

//========================================================================================================================
//Zusätzliches Feld -> Liste: "Simulation / Modellart"
//TODO:
//- Zusätzliches Feld anlegen unter "Allgemeines - Kategorien"
//- Tabelle (Id:'simModelTypeTable', Sichtbarkeit: Pflichtfeld, Beschriftung:'Simulation / Modellart', Hilfetext:?)
//  - Spalten: (Type: Liste, id:simModelType, indexName:simModelType)
//- nachfolgendes Javascript in Feld IDF-Mapping hinzufügen
//========================================================================================================================


//------------------------
//IDF
//------------------------
importPackage(Packages.de.ingrid.iplug.dsc.om);
//add Namespaces to Utility for convenient handling of NS !
DOM.addNS("gmd", "http://www.isotc211.org/2005/gmd");
DOM.addNS("gco", "http://www.isotc211.org/2005/gco");

if (!(sourceRecord instanceof DatabaseSourceRecord)) {
throw new IllegalArgumentException("Record is no DatabaseRecord!");
}

var id = sourceRecord.get(DatabaseSourceRecord.ID);
var igcProfileControlId = XPATH.getString(igcProfileControlNode, "igcp:id");
var columnName = 'simModelType'; // the column of the table to get the value from
var columnData = SQL.all("SELECT add2.data, add2.list_item_id FROM additional_field_data add1, additional_field_data add2 WHERE add1.obj_id=? AND add1.field_key=? AND add2.parent_field_id=add1.id AND add2.field_key=?", [id, igcProfileControlId, columnName]);

if ( columnData && columnData.size() > 0) {

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
   
   // write keyword and thesaurus
   var keywords;
   var keywordsParent;
   if (nodeBeforeInsert) {
       keywordsParent = nodeBeforeInsert.addElementAsSibling("gmd:descriptiveKeywords");
   } else {
       keywordsParent = dataIdentification.addElement("gmd:descriptiveKeywords");
   }
   keywords = keywordsParent.addElement("gmd:MD_Keywords");
   for (i=0; i<columnData.size(); i++) {
	      keywords.addElement("gmd:keyword/gco:CharacterString").addText(columnData.get(i).get("data"));
   }
   keywords.addElement("gmd:type/gmd:MD_KeywordTypeCode").addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_KeywordTypeCode").addAttribute("codeListValue", "theme");
   var thesCitation = keywords.addElement("gmd:thesaurusName/gmd:CI_Citation");
   thesCitation.addElement("gmd:title/gco:CharacterString").addText("BAW-DMQS Modelling Type");
   var ciDate = thesCitation.addElement("gmd:date/gmd:CI_Date");
   ciDate.addElement("gmd:date/gco:Date").addText("2016-04-08");
   ciDate.addElement("gmd:dateType/gmd:CI_DateTypeCode").addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#CI_DateTypeCode").addAttribute("codeListValue", "publication");;
}

//========================================================================================================================
//Zusätzliches Feld -> Liste: "Daten / Größen / Parameter"
//TODO:
//- Zusätzliches Feld anlegen unter "Allgemeines - Kategorien"
//- Tabelle (Id:'simParamTable', Sichtbarkeit: , Beschriftung:'Daten / Größen / Parameter', Hilfetext:?)
//- Spalten: (Type: Liste, id:simParamType, indexName:simParamType)
//- Spalten: (Type: Text, id:simParamName, indexName:simParamName)
//- Spalten: (Type: Text, id:simParamUnit, indexName:simParamUnit)
//- Spalten: (Type: Text, id:simParamValue, indexName:simParamValue)
//- Spalten: (Type: Text, id:simParamInfo, indexName:simParamInfo)
//- Spalten: (Type: Text, id:simParamMdInfo, indexName:simParamMdInfo)
//- nachfolgendes Javascript in Feld IDF-Mapping hinzufügen
//========================================================================================================================


//------------------------
//IDF
//------------------------
importPackage(Packages.de.ingrid.iplug.dsc.om);
//add Namespaces to Utility for convenient handling of NS !
DOM.addNS("gmd", "http://www.isotc211.org/2005/gmd");
DOM.addNS("gco", "http://www.isotc211.org/2005/gco");
DOM.addNS("gml", "http://www.opengis.net/gml");
DOM.addNS("xlink", "http://www.w3.org/1999/xlink");

if (!(sourceRecord instanceof DatabaseSourceRecord)) {
throw new IllegalArgumentException("Record is no DatabaseRecord!");
}

var id = sourceRecord.get(DatabaseSourceRecord.ID);
var igcProfileControlId = XPATH.getString(igcProfileControlNode, "igcp:id");


var rowData = SQL.all("SELECT DISTINCT add2.sort, add2.parent_field_id FROM additional_field_data add1, additional_field_data add2 WHERE add1.obj_id=? AND add1.field_key=? AND add2.parent_field_id=add1.id ORDER BY add2.sort", [id, igcProfileControlId]);
if ( rowData && rowData.size() > 0) {
	log.info("Record ID: " + id );
	log.info("rowData.size(): " + rowData.size() );

	// find node to add the ISO elements to
	var mdMetadata = DOM.getElement(idfDoc, "//idf:idfMdMetadata");
	var path = ["gmd:dataQualityInfo", "gmd:distributionInfo", "gmd:contentInfo","gmd:identificationInfo"];
	// find first present node from paths
	var nodeBeforeInsert = null;
	for (var i=0; i<path.length; i++) {
	    // get the last occurrence of this path if any
	    nodeBeforeInsert = DOM.getElement(mdMetadata, path[i]+"[last()]");
	    if (nodeBeforeInsert) { break; }
	}
    if (nodeBeforeInsert) {
    	dataQualitySibling = nodeBeforeInsert;
	} else {
	    log.error("Could not find any element in [gmd:dataQualityInfo, gmd:distributionInfo, gmd:contentInfo, gmd:identificationInfo] to attache the dataquality Node to. Error Mapping record: " + sourceRecord.toString());
	}
	
	// iterate over DGS table rows
	for (var i=0; i<rowData.size(); i++) {
		
		// initialize variables
		var simParamType = null;
		var simParamMdInfo = null;
		var simParamName = null;
		var simParamUnit = null;
		var simParamValue = null;
		var simParamInfo = null;

		// gather the DGS data for this row
		var sort = rowData.get(i).get("sort");
		log.info("sort: " + sort);
		var parentFieldId = rowData.get(i).get("parent_field_id");
		var columnData = SQL.all("SELECT add1.data, add1.list_item_id, add1.field_key FROM additional_field_data add1 WHERE add1.parent_field_id=? AND add1.sort=?", [parentFieldId, sort]);
		for (var j=0; j<columnData.size(); j++) {
			log.info("Add: " + columnData.get(j).get("field_key"));
			
			if (columnData.get(j).get("field_key") == "simParamType") {
				simParamType = columnData.get(j).get("data");
			}
			if (columnData.get(j).get("field_key") == "simParamMdInfo") {
				simParamMdInfo = columnData.get(j).get("data");
			}
			if (columnData.get(j).get("field_key") == "simParamName") {
				simParamName = columnData.get(j).get("data");
			}
			if (columnData.get(j).get("field_key") == "simParamUnit") {
				simParamUnit = columnData.get(j).get("data");
			}
			if (columnData.get(j).get("field_key") == "simParamValue") {
				simParamValue = columnData.get(j).get("data");
			}
			if (columnData.get(j).get("field_key") == "simParamInfo") {
				simParamInfo = columnData.get(j).get("data");
			}
		}
		
		// now build the ISO XML from the data
		var dataQuality = dataQualitySibling.addElementAsSibling("gmd:dataQualityInfo");
		var dqDataQuality = dataQuality.addElement("gmd:DQ_DataQuality");
		dqDataQuality.addElement("gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode").addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode").addAttribute("codeListValue", "model");
		// 	Online-Ressource: /MD_Metadata/dataQualityInfo/DQ_DataQuality/report/DQ_QuantitativeAttributeAccuracy/result[@href]
		var dqQuantitativeResult = dqDataQuality.addElement("gmd:report/gmd:DQ_QuantitativeAttributeAccuracy/gmd:result").addAttribute("xlink:href", simParamInfo).addElement("gmd:DQ_QuantitativeResult");
		// 	Name: /MD_Metadata/dataQualityInfo/DQ_DataQuality/report/DQ_QuantitativeAttributeAccuracy/result/DQ_QuantitativeResult/valueType
		dqQuantitativeResult.addElement("gmd:valueType/gco:RecordType").addText(simParamName);
		// 	Einheit: /MD_Metadata/dataQualityInfo/DQ_DataQuality/report/DQ_QuantitativeAttributeAccuracy/result/DQ_QuantitativeResult/valueUnit
		var unitDefinition = dqQuantitativeResult.addElement("gmd:valueUnit").addElement("gml:UnitDefinition").addAttribute("gml:id", "unitDefinition_ID_" + Math.floor(Math.random()*1000));
		unitDefinition.addElement("gml:identifier").addAttribute("codeSpace", "");
		unitDefinition.addElement("gml:name").addText(simParamUnit);
		// 	Werte / Wertebereich: /MD_Metadata/dataQualityInfo/DQ_DataQuality/report/DQ_QuantitativeAttributeAccuracy/result/DQ_QuantitativeResult/value
		dqQuantitativeResult.addElement("gmd:value/gco:Record").addText(simParamValue);
		
		// 	Metadaten-Verweis: /MD_Metadata/dataQualityInfo/DQ_DataQuality/lineage/LI_Lineage/source[@href]
		var liSource = dqDataQuality.addElement("gmd:lineage/gmd:LI_Lineage").addElement("gmd:source").addAttribute("xlink:href", simParamMdInfo);
		// Rolle: /MD_Metadata/dataQualityInfo/DQ_DataQuality/lineage/LI_Lineage/source/LI_Source/description
		liSource.addElement("gmd:LI_Source").addElement("gmd:description/gco:CharacterString").addText(simParamType);
		dataQualitySibling = dataQuality;
	  }
}

//========================================================================================================================
//Zusätzliches Feld -> Liste: "Zeitliche Genauigkeit"
//TODO:
//- Zusätzliches Feld anlegen unter "Fachbezug"
//- Text (Id:'dqAccTimeMeas', Sichtbarkeit: Pflichtfeld, Beschriftung:'dqAccTimeMeas', Hilfetext:?)
//- nachfolgendes Javascript in Feld IDF-Mapping hinzufügen
//========================================================================================================================


//------------------------
//IDF
//------------------------
importPackage(Packages.de.ingrid.iplug.dsc.om);
//add Namespaces to Utility for convenient handling of NS !
DOM.addNS("gmd", "http://www.isotc211.org/2005/gmd");
DOM.addNS("gco", "http://www.isotc211.org/2005/gco");
DOM.addNS("gml", "http://www.opengis.net/gml");
DOM.addNS("xlink", "http://www.w3.org/1999/xlink");


if (!(sourceRecord instanceof DatabaseSourceRecord)) {
throw new IllegalArgumentException("Record is no DatabaseRecord!");
}

var id = sourceRecord.get(DatabaseSourceRecord.ID);
var igcProfileControlId = XPATH.getString(igcProfileControlNode, "igcp:id");
var valueRecord = SQL.all("SELECT add1.data FROM additional_field_data add1 WHERE add1.obj_id=? AND add1.field_key=?", [id, igcProfileControlId]);
if (valueRecord && valueRecord.size() > 0) {
	var value = valueRecord.get(0).get("data");
	if (value) {
		// find node to add the ISO elements to
		var mdMetadata = DOM.getElement(idfDoc, "//idf:idfMdMetadata");
		var path = ["gmd:dataQualityInfo", "gmd:distributionInfo", "gmd:contentInfo","gmd:identificationInfo"];
		// find first present node from paths
		var nodeBeforeInsert = null;
		for (var i=0; i<path.length; i++) {
		    // get the last occurrence of this path if any
		    nodeBeforeInsert = DOM.getElement(mdMetadata, path[i]+"[last()]");
		    if (nodeBeforeInsert) { break; }
		}
	    if (nodeBeforeInsert) {
	    	dataQualitySibling = nodeBeforeInsert;
		} else {
		    log.error("Could not find any element in [gmd:dataQualityInfo, gmd:distributionInfo, gmd:contentInfo, gmd:identificationInfo] to attache the dataquality Node to. Error Mapping record: " + sourceRecord.toString());
		}

		// now build the ISO XML from the data
		var dataQuality = dataQualitySibling.addElementAsSibling("gmd:dataQualityInfo");
		var dqDataQuality = dataQuality.addElement("gmd:DQ_DataQuality");
		dqDataQuality.addElement("gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode").addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode").addAttribute("codeListValue", "model");
		var dqQuantitativeResult = dqDataQuality.addElement("gmd:report/gmd:DQ_AccuracyOfATimeMeasurement/gmd:result/gmd:DQ_QuantitativeResult");
		// 	Name: /MD_Metadata/dataQualityInfo/DQ_DataQuality/report/DQ_QuantitativeAttributeAccuracy/result/DQ_QuantitativeResult/valueType
		dqQuantitativeResult.addElement("gmd:valueType/gco:RecordType").addText("temporal accuracy");
		// 	Einheit: /MD_Metadata/dataQualityInfo/DQ_DataQuality/report/DQ_QuantitativeAttributeAccuracy/result/DQ_QuantitativeResult/valueUnit
		var unitDefinition = dqQuantitativeResult.addElement("gmd:valueUnit").addElement("gml:UnitDefinition").addAttribute("gml:id", "unitDefinition_sec_" + Math.floor(Math.random()*1000));
		unitDefinition.addElement("gml:identifier").addAttribute("codeSpace", "");
		unitDefinition.addElement("gml:name").addText("second");
		unitDefinition.addElement("gml:catalogSymbol").addText("s");
		// 	Werte / Wertebereich: /MD_Metadata/dataQualityInfo/DQ_DataQuality/report/DQ_QuantitativeAttributeAccuracy/result/DQ_QuantitativeResult/value
		dqQuantitativeResult.addElement("gmd:value/gco:Record").addText(value);

	}
}