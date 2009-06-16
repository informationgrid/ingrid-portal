/**
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 * 
 * ArcCatalog import script. This script translates an input xml from
 * the export of an ArcCatalog ISO-Editor (Format "XML") into a IGC 
 * import format structure.
 * 
 * It uses a template that provides a basic IGC import format structure.
 * 
 * If the input document is invalid an Exception will be raised.
 *
 *
 * The following global variable are passed from the application:
 *
 * @param source A org.w3c.dom.Document instance, that defines the input
 * @param target A org.w3c.dom.Document instance, that defines the output, based on the IGC import format template.
 * @param log A Log instance
 *
 */
importPackage(Packages.de.ingrid.utils.udk);
importPackage(Packages.de.ingrid.utils.xml);
importPackage(Packages.org.w3c.dom);

if (log.isDebugEnabled()) {
	log.debug("Mapping ArcCatalog ISO-Editor Export document to IGC import document.");
}

var mappingDescription = {"mappings":[
		{	
  			"srcXpath":"/metadata/dataIdInfo/idCitation/resTitle",
			"targetNode":"/igc/data-sources/data-source/general/title"
  		},
  		{	
  			"srcXpath":"/metadata/Esri/MetaID",
  			"targetNode":"/igc/data-sources/data-source/general/original-control-identifier"
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/idAbs",
  			"targetNode":"/igc/data-sources/data-source/general/abstract"
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/idCitation/resEd",
  			"targetNode":"/igc/data-sources/data-source/general/abstract",
  			"appendWith":"\n\n",
  			"prefix":"Nummer der Ausgabe/Version: "
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/idCitation/resEdDate",
  			"targetNode":"/igc/data-sources/data-source/general/abstract",
  			"appendWith":"\n\n",
  			"prefix":"Datum der Ausgabe/Version: "
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/dataLang/languageCode/@value",
  			"targetNode":"/igc/data-sources/data-source/general/abstract",
  			"appendWith":"\n\n",
  			"prefix":"Folgende Sprachen werden im beschriebenen Datensatz verwendet: ",
  			"concatEntriesWith":", ",
  			  			"transform":{
				"funct":UtilsLanguageCodelist.getNameFromShortcut,
				"params":['de']
			}
  		},
  		{	
  			"srcXpath":"/metadata/idinfo/descript/purpose",
  			"targetNode":"/igc/data-sources/data-source/additional-information/dataset-intentions"
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/dataExt/tempEle/TempExtent/exTemp/TM_GeometricPrimitive",
  			"execute":{
				"funct":mapTimeConstraints
			}
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/resMaint/dateNext",
  			"targetNode":"/igc/data-sources/data-source/temporal-domain/description-of-temporal-domain",
  			"prefix":"Nächste Überarbeitung: "
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/descKeys[@KeyTypCd='004']/keyword",
  			"targetNode":"/igc/data-sources/data-source/temporal-domain/description-of-temporal-domain",
  			"appendWith":"\n\n",
  			"concatEntriesWith":", ",
  			"prefix":"Schlagworte: "
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/resMaint/maintFreq/MaintFreqCd/@value",
  			"targetNode":"/igc/data-sources/data-source/temporal-domain/time-period",
  			"transform":{
				"funct":parseInt
			}
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/idCitation/resAltTitle",
  			"targetNode":"igc/data-sources/data-source/general/dataset-alternate-name",
  			"concatEntriesWith":", "
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/dataLang[1]/languageCode/@value",
  			"targetNode":"/igc/data-sources/data-source/additional-information/data-language",
  			"targetAttribute":"id",
  			"transform":{
				"funct":UtilsLanguageCodelist.getCodeFromShortcut
			}
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/dataLang[1]/languageCode/@value",
  			"targetNode":"/igc/data-sources/data-source/additional-information/data-language",
  			"transform":{
				"funct":UtilsLanguageCodelist.getNameFromShortcut,
				"params":['de']
			}
  		},
  		{	
  			"srcXpath":"/metadata/mdStanName",
  			"targetNode":"/igc/data-sources/data-source/general/metadata/metadata-standard-name"
  		},
  		{	
  			"srcXpath":"/metadata/mdLang/languageCode/@value",
  			"defaultValue":"de",
  			"targetNode":"/igc/data-sources/data-source/additional-information/metadata-language",
  			"transform":{
				"funct":UtilsLanguageCodelist.getNameFromShortcut,
				"params":['de']
			}
  		},
  		{	
  			"srcXpath":"/metadata/mdLang/languageCode/@value",
  			"defaultValue":"de",
  			"targetNode":"/igc/data-sources/data-source/additional-information/metadata-language",
  			"targetAttribute":"id",
  			"transform":{
				"funct":UtilsLanguageCodelist.getCodeFromShortcut
			}
  		}
  		
	]};

validateSource(source);

mapToTarget(mappingDescription, source, target);
  	
function mapToTarget(mapping, source, target) {
	
		// iterate over all mapping descriptions
		for (var i in mapping.mappings) {
			var m = mapping.mappings[i];
			// check for execution (special function)
			if (hasValue(m.execute)) {
				log.debug("Execute function: " + m.execute.funct.name + "...")
				var args = new Array(source, target);
				if (hasValue(m.execute.params)) {
					args = args.concat(m.execute.params);
				}
				call_f(m.execute.funct, args)
			} else {
				log.debug("Working on " + m.targetNode + " with xpath:'" + m.srcXpath + "'")
				// iterate over all xpath results
				var sourceNodeList = XPathUtils.getNodeList(source, m.srcXpath);
				var nodeText = "";
				for (j=0; j<sourceNodeList.getLength(); j++ ) {
					var value = sourceNodeList.item(j).getTextContent()
					if (m.defaultValue && (!hasValue(value) || value.startsWith("REQUIRED:"))) {
						log.debug("Setting value to default '" + m.defaultValue + "'")
						value = m.defaultValue;
					}
					if (hasValue(value) && !value.startsWith("REQUIRED:")) {
						var node = XPathUtils.createElementFromXPath(target.getDocumentElement(), m.targetNode);
						log.debug("Found node with content: '" + node.getTextContent() + "'")
						if (j==0) { 
							// append content to target nodes content?
							if (m.appendWith && node.getTextContent()) {
								log.debug("Append to target node...")
								nodeText = node.getTextContent() + m.appendWith;
							}
							// is a prefix has been defined with the xpath? 
							if (m.prefix) {
								log.debug("Append prefix...")
								nodeText += m.prefix;
							}
						} else {
							// concat multiple entries?
							if (m.concatEntriesWith) {
								log.debug("concat entries... ")
								nodeText += m.concatEntriesWith;
							}
						}
						
						// check for transformation
						if (hasValue(m.transform)) {
							var args = new Array(value);
							if (hasValue(m.transform.params)) {
								args = args.concat(m.transform.params);
							}
							value = call_f(m.transform.funct,args);
						}
						
						nodeText += value;
						
						if (m.targetAttribute) {
							log.debug("adding '" + m.targetNode + "/@" + m.targetAttribute + "' = '" + nodeText + "'.");
							XMLUtils.createOrReplaceAttribute(node, m.targetAttribute, nodeText);
						} else {
							log.debug("adding '" + m.targetNode + "' = '" + nodeText + "'.");
							XMLUtils.createOrReplaceTextNode(node, nodeText);
						}
					}
				}
			}
		}
		
		return target;
}

function validateSource(source) {
	var title = XPathUtils.getString(source.getDocumentElement(), "/metadata/dataIdInfo/idCitation/resTitle");
	var uuid = XPathUtils.getString(source.getDocumentElement(), "/metadata/Esri/MetaID");
	var metadataLanguage = XPathUtils.getString(source.getDocumentElement(), "/metadata/mdLang/languageCode/@value");
	var requiredMetadataLanguage = "de";
	if (!metadataLanguage) {
		metadataLanguage = requiredMetadataLanguage;
	} else if (metadataLanguage != requiredMetadataLanguage) {
		log.error("Dataset '" + title + "' (" + uuid + ") has the wrong matadata language '" + metadataLanguage + "'. Must be '" + requiredMetadataLanguage + "'");
		throw "Dataset '" + title + "' (" + uuid + ") has the wrong matadata language '" + metadataLanguage + "'. Must be '" + requiredMetadataLanguage + "'";
	}
	return true;
}


function mapTimeConstraints(source, target) {
	
	var timePeriods = XPathUtils.getNodeList(source, "/metadata/dataIdInfo/dataExt/tempEle/TempExtent/exTemp/TM_GeometricPrimitive/TM_Period");
	log.debug("Found " + timePeriods.getLength() + " TimePeriod records.");
	if (hasValue(timePeriods) && timePeriods.getLength() > 0) {
		var beginPosition = XPathUtils.getString(timePeriods.item(0), "begin");
		var endPosition = XPathUtils.getString(timePeriods.item(0), "end");
		if (hasValue(beginPosition) && hasValue(endPosition)) {
			if (beginPosition.equals(endPosition)) {
				var node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/beginning-date");
				XMLUtils.createOrReplaceTextNode(node, UtilsCSWDate.mapDateFromIso8601ToIndex(beginPosition));
				node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/ending-date");
				XMLUtils.createOrReplaceTextNode(node, UtilsCSWDate.mapDateFromIso8601ToIndex(endPosition));
				node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/time-type");
				XMLUtils.createOrReplaceTextNode(node, "am");
			} else {
				var node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/beginning-date");
				XMLUtils.createOrReplaceTextNode(node, UtilsCSWDate.mapDateFromIso8601ToIndex(beginPosition));
				node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/ending-date");
				XMLUtils.createOrReplaceTextNode(node, UtilsCSWDate.mapDateFromIso8601ToIndex(endPosition));
				node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/time-type");
				XMLUtils.createOrReplaceTextNode(node, "von");
			}
		} else if (hasValue(beginPosition)) {
				var node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/beginning-date");
				XMLUtils.createOrReplaceTextNode(node, UtilsCSWDate.mapDateFromIso8601ToIndex(beginPosition));
				node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/time-type");
				XMLUtils.createOrReplaceTextNode(node, "seit");
		} else if (hasValue(endPosition)) {
				node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/ending-date");
				XMLUtils.createOrReplaceTextNode(node, UtilsCSWDate.mapDateFromIso8601ToIndex(endPosition));
				node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/time-type");
				XMLUtils.createOrReplaceTextNode(node, "bis");
		}
	} else {
		var timePositions = XPathUtils.getNodeList(source, "/metadata/dataIdInfo/dataExt/tempEle/TempExtent/exTemp/TM_GeometricPrimitive/TM_Instant/tmPosition/TM_DateAndTime");
		log.debug("Found " + timePositions.getLength() + " tmPosition records.");
		if (hasValue(timePositions)) {
			var calDate = XPathUtils.getString(timePositions.item(0), "calDate");
			var clkTime = XPathUtils.getString(timePositions.item(0), "clkTime");
			var dateTime = calDate + clkTime;
			var node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/beginning-date");
			XMLUtils.createOrReplaceTextNode(node, dateTime);
			node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/ending-date");
			XMLUtils.createOrReplaceTextNode(node, dateTime);
			node = XPathUtils.createElementFromXPath(target.getDocumentElement(), "/igc/data-sources/data-source/temporal-domain/time-type");
			XMLUtils.createOrReplaceTextNode(node, "am");
		}
	}
}

function hasValue(val) {
	if (typeof val == "undefined") {
		return false; 
	} else if (val == null) {
		return false; 
	} else if (typeof val == "string" && val == "") {
		return false;
	} else {
	  return true;
	}
}

function call_f(f,args)
{
  f.call_self = function(ars)
  {
    var callstr = "";
    if (hasValue(ars)) {
	    for(var i = 0; i < ars.length; i++)
	    {
	      callstr += "ars["+i+"]";
	      if(i < ars.length - 1)
	      {
	        callstr += ',';
	      }
	    }
	}
    return eval("this("+callstr+")");
  };

  return f.call_self(args);
}

