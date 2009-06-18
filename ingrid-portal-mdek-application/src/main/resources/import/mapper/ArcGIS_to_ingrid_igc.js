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
  			"execute":{
				"funct":mapTimeConstraints
			}
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/resMaint/dateNext",
  			"targetNode":"/igc/data-sources/data-source/temporal-domain/description-of-temporal-domain",
  			"prefix":"N√§chste √úberarbeitung: "
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
				"funct":parseToInt
			}
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/idCitation/resAltTitle",
  			"targetNode":"/igc/data-sources/data-source/general/dataset-alternate-name",
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
  		},
  		{	
  			// set the obj_class to a fixed value
  			"defaultValue":"3",
  			"targetNode":"/igc/data-sources/data-source/general/object-class",
  			"targetAttribute":"id"
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/dataExt/vertEle/vertMinVal",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain/vertical-extent/vertical-extent-minimum",
  			"transform":{
				"funct":replaceString,
				"params":["\,", "."]
			}
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/dataExt/vertEle/vertMaxVal",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain/vertical-extent/vertical-extent-maximum",
  			"transform":{
				"funct":replaceString,
				"params":["\,", "."]
			}
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/dataExt/vertEle/vertUoM/uomName",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain/vertical-extent/vertical-extent-unit",
  			"targetAttribute":"id",
  			"transform":{
				"funct":transformGeneric,
				"params":[{"Fu√ü":"9002", "Kilometer":"9036", "Meter":"9001", "Zoll":"4"}, false, "Could not map vertical-extent uom name: "]
			}						    					
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/dataExt/vertEle/vertDatum/datumID/identCode",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain/vertical-extent/vertical-extent-vdatum",
  			"targetAttribute":"id",
  			// check for german (150) name in the code list 101
  			"transform":{
				"funct":transformToIgcDomainId,
				"params":[101, 150, "Could not map vertical-extent vdatum name: "]
			}						    					
  		},
  		{	
  			"srcXpath":"/metadata/distInfo/distributor/distorOrdPrc/ordInstr",
  			"targetNode":"/igc/data-sources/data-source/additional-information/ordering-instructions"
  		},
  		{	
  			"srcXpath":"/metadata/distInfo/distributor/distorOrdPrc/resFees",
  			"targetNode":"/igc/data-sources/data-source/additional-information/ordering-instructions",
  			"appendWith":"\n\n"
  		},
  		{	
  			"srcXpath":"/metadata/distInfo/distributor/distorOrdPrc/ordTurn",
  			"targetNode":"/igc/data-sources/data-source/additional-information/ordering-instructions",
  			"appendWith":"\n\n"
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/resConst/Consts/useLimit",
  			"targetNode":"/igc/data-sources/data-source/additional-information/ordering-instructions",
  			"appendWith":"\n\n",
  			"concatEntriesWith":", ",
  			"prefix":"Verwendungsbeschr‰nkungen: "
  		},
  		{	
  			"execute":{
				"funct":mapCreateDateTime
			}
  		},
  		{	
  			"srcXpath":"/metadata/dqInfo/dataLineage/statement",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/technical-base"
  		},
  		{	
  			"srcXpath":"/metadata/dataqual/lineage/procstep/procdesc",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/method-of-production"
  		},
  		{	
  			"srcXpath":"/metadata/dataIdInfo/envirDesc",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/method-of-production",
  			"appendWith":"\n\n"
  		},
  		{	
  			"srcXpath":"/metadata/refSysInfo/RefSystem/refSysID/identCode",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/coordinate-system"
  		},
  		{
  			"srcXpath":"/metadata/refSysInfo/RefSystem/refSysID/identCode",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/coordinate-system",
  			"targetAttribute":"id",
  			"transform":{
				"funct":transformToIgcDomainId,
				"params":[100, 150, "Could not map vertical-extent vdatum name: "]
			}						    					
  		},
  		{	
  			"srcXpath":"/metadata/dqInfo/dqScope/scpLvl/ScopeCd/@value",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/hierarchy-level",
  			"targetAttribute":"iso-code",
  			"transform":{
				"funct":parseToInt
			}
  		},
  		{	
  			"srcXpath":"/metadata/spatRepInfo/VectSpatRep/topLvl/TopoLevCd/@value",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/vector-format/vector-topology-level",
  			"targetAttribute":"iso-code",
  			"transform":{
				"funct":parseToInt
			}
  		},
  		{
  			"execute":{
				"funct":mapDataScale
			}
  		},
  		{
  			"srcXpath":"/metadata/dataIdInfo/spatRpType",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map",
  			"newNodeName":"spatial-representation-type",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"SpatRepTypCd/@value",
			  			"targetNode":"",
			  			"targetAttribute":"iso-code",
			  			"transform":{
							"funct":parseToInt
						}
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"/metadata/eainfo/detailed/attr",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map",
  			"newNodeName":"feature-type",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"attrlabl",
			  			"targetNode":""
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"/metadata/spatRepInfo/VectSpatRep/geometObjs",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/vector-format",
  			"newNodeName":"geo-vector",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"geoObjTyp/GeoObjTypCd/@value",
			  			"targetNode":"geometric-object-type",
			  			"targetAttribute":"iso-code",
			  			"transform":{
							"funct":parseToInt
						}
			  		},
	  				{
			  			"srcXpath":"geoObjCnt",
			  			"targetNode":"geometric-object-count"
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"/metadata/dataIdInfo/tpCat/TopicCatCd",
  			"targetNode":"/igc/data-sources/data-source/general/topic-categories",
  			"newNodeName":"topic-category",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"./@value",
			  			"targetNode":"",
			  			"targetAttribute":"id",
			  			"transform":{
							"funct":parseToInt
						}
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"/metadata/distInfo/distributor/distorTran/onLineSrc",
  			"targetNode":"/igc/data-sources/data-source",
  			"newNodeName":"available-linkage",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"linkage",
			  			"targetNode":"linkage-url"
			  		},
	  				{
			  			"defaultValue":"-1",
			  			"targetNode":"linkage-reference",
			  			"targetAttribute":"id"
			  		},
	  				{
			  			"srcXpath":"orFunct/OnFunctCd/@value",
			  			"targetNode":"linkage-reference",
			  			"transform":{
							"funct":transformGeneric,
							"params":[{"001":"download", "002":"information", "003":"offlineAccess", "004":"order", "005":"search"}, false, "Could not map linkage function: "]
						}
			  		},
	  				{
			  			"srcXpath":"orDesc",
			  			"targetNode":"linkage-name",
			  			"transform":{
							"funct":transformGeneric,
							"params":[{"001":"Live Data and Maps", "002":"Downloadable Data", "003":"Offline Data", "004":"Images of Static Maps", "005":"Other Documents", "006":"Applications", "007":"Geographic Services", "008":"Clearinghouses", "009":"Map Files", "010":"Geographic Activities"}, false]
						}
			  		},
	  				{
			  			"srcXpath":"protocol",
			  			"targetNode":"linkage-name",
			  			"appendWith":"\n\n",
			  			"prefix":"Verbindungsprotokoll: "
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"/metadata/distInfo/distributor/distorTran",
  			"targetNode":"/igc/data-sources/data-source/additional-information",
  			"newNodeName":"medium-option",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"offLineMed/medName/MedNameCd/@value",
			  			"targetNode":"medium-name",
			  			"targetAttribute":"iso-code",
			  			"transform":{
							"funct":parseToInt
						}
			  		},
	  				{
			  			"srcXpath":"transSize",
			  			"targetNode":"transfer-size",
			  			"transform":{
							"funct":replaceString,
							"params":["\,", "."]
						}
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"/metadata/dataIdInfo/idCitation/resRefDate",
  			"targetNode":"/igc/data-sources/data-source/temporal-domain",
  			"newNodeName":"dataset-reference",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"refDate",
			  			"targetNode":"dataset-reference-date",
			  			"transform":{
							"funct":padString,
							"params":["000000000"]
						}
			  		},
	  				{
			  			"srcXpath":"refDateType/DateTypCd/@value",
			  			"targetNode":"dataset-reference-type",
			  			"targetAttribute":"iso-code",
			  			"transform":{
							"funct":parseToInt
						}
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"/metadata/distInfo/distributor/distorFormat",
  			"targetNode":"/igc/data-sources/data-source/additional-information",
  			"newNodeName":"data-format",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"formatName",
			  			"targetNode":"format-name"
			  		},
	  				{
			  			"srcXpath":"formatName",
			  			"targetNode":"format-name",
			  			"targetAttribute":"id",
			  			"transform":{
							"funct":transformToIgcDomainId,
							"params":[1320, 150]
						}
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"/metadata/dataIdInfo/geoBox",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain",
  			"newNodeName":"geo-location",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"westBL",
			  			"targetNode":"bounding-coordinates/west-bounding-coordinate",
			  			"transform":{
							"funct":replaceString,
							"params":["\,", "."]
						}
			  		},
	  				{
			  			"srcXpath":"southBL",
			  			"targetNode":"bounding-coordinates/south-bounding-coordinate",
			  			"transform":{
							"funct":replaceString,
							"params":["\,", "."]
						}
			  		},
	  				{
			  			"srcXpath":"eastBL",
			  			"targetNode":"bounding-coordinates/east-bounding-coordinate",
			  			"transform":{
							"funct":replaceString,
							"params":["\,", "."]
						}
			  		},
	  				{
			  			"srcXpath":"northBL",
			  			"targetNode":"bounding-coordinates/north-bounding-coordinate",
			  			"transform":{
							"funct":replaceString,
							"params":["\,", "."]
						}
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"/metadata/dataIdInfo/descKeys[@KeyTypCd='002']",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain",
  			"newNodeName":"geo-location",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"keyword",
			  			"targetNode":"uncontrolled-location/location-name"
			  		}
			  	]
			}		
  		},
  		{	
  			"execute":{
				"funct":mapAccessConstraints
			}
  		},
  		{
  			"srcXpath":"/metadata/dataIdInfo/descKeys[@KeyTypCd='001' or @KeyTypCd='003' or @KeyTypCd='005']",
  			"targetNode":"/igc/data-sources/data-source/subject-terms",
  			"newNodeName":"uncontrolled-term",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"keyword",
			  			"targetNode":""
			  		}
			  	]
			}		
  		}	
  		
  		,{	
  			"srcXpath":"/metadata/distInfo/distributor/distorCont | /metadata/dataIdInfo/idCitation/citRespParty | /metadata/mdContact",
  			"targetNode":"/igc/addresses",
  			"newNodeName":"address",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"createUUID":"true"
			  		},
	  				{
			  			"setUUID":"true",
			  			"targetNode":"address-identifier"
			  		},
	  				{
			  			"defaultValue":"3",
			  			"targetNode":"type-of-address",
			  			"targetAttribute":"id"
			  		},
	  				{
			  			"srcXpath":"rpOrgName",
			  			"targetNode":"organisation"
			  		},
	  				{
			  			"srcXpath":"rpIndName",
			  			"targetNode":"name"
			  		},
	  				{
			  			"srcXpath":"rpCntInfo/cntAddress/delPoint",
			  			"targetNode":"street"
			  		},
	  				{
			  			"srcXpath":"rpCntInfo/cntAddress/postCode",
			  			"targetNode":"postal-code"
			  		},
	  				{
			  			"srcXpath":"rpCntInfo/cntAddress/city",
			  			"targetNode":"city"
			  		},
	  				{
			  			"srcXpath":"rpCntInfo/cntAddress/country",
			  			"targetNode":"country",
			  			"targetAttribute":"id",
			  			"transform":{
							"funct":UtilsCountryCodelist.getCodeFromShortcut2
						}
			  		},
	  				{
			  			"srcXpath":"rpCntInfo/cntAddress/country",
			  			"targetNode":"country",
			  			"transform":{
							"funct":UtilsCountryCodelist.getNameFromShortcut2,
							"params":"de"
						}
			  		},
	  				{
			  			"srcXpath":"rpPosName",
			  			"targetNode":"function"
			  		},
			  		{	
			  			"execute":{
							"funct":mapCommunicationData
						}
			  		},
			  		{
				  		"srcXpath":".",
			  			"targetNode":"/igc/data-sources/data-source",
			  			"newNodeName":"related-address",
			  			"subMappings":{
			  				"mappings": [
				  				{
						  			"setUUID":"true",
						  			"targetNode":"address-identifier"
						  		},
				  				{
						  			"srcXpath":"role/RoleCd/@value",
						  			"targetNode":"type-of-relation"
						  		}
						  	]
						}
					}
  				]
  			}
  		}
	]};

validateSource(source);

mapToTarget(mappingDescription, source, target.getDocumentElement());

var uuid;

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
			} else if (m.subMappings) {
				// iterate over all xpath results
				var sourceNodeList = XPathUtils.getNodeList(source, m.srcXpath);
				if (sourceNodeList) {
					log.debug("found sub mapping sources: " + m.srcXpath + "; count: " + sourceNodeList.getLength())
					for (var j=0; j<sourceNodeList.getLength(); j++ ) {
						log.debug("handle sub mapping: " + sourceNodeList.item(j))
						var node = XPathUtils.createElementFromXPath(target, m.targetNode);
						node = node.appendChild(node.getOwnerDocument().createElement(m.newNodeName));
						mapToTarget(m.subMappings, sourceNodeList.item(j), node);
					}
				} else {
					log.debug("found sub mapping sources: " + m.srcXpath + "; count: 0")
				}
			} else if (m.createUUID) {
				uuid = createUUID();
			} else {
				if (m.srcXpath) {
					log.debug("Working on " + m.targetNode + " with xpath:'" + m.srcXpath + "'")
					// iterate over all xpath results
					var sourceNodeList = XPathUtils.getNodeList(source, m.srcXpath);
					var nodeText = "";
					for (var j=0; j<sourceNodeList.getLength(); j++ ) {
						var value = sourceNodeList.item(j).getTextContent()
						if (hasValue(value)) {
							// trim
							value = value.trim();
						}
						if (m.defaultValue && (!hasValue(value) || value.startsWith("REQUIRED:"))) {
							log.debug("Setting value to default '" + m.defaultValue + "'")
							value = m.defaultValue;
						}
						// check for transformation
						if (hasValue(m.transform)) {
							log.debug("Transform value '" + value + "'")
							var args = new Array(value);
							if (hasValue(m.transform.params)) {
								args = args.concat(m.transform.params);
							}
							value = call_f(m.transform.funct,args);
						}
						if (hasValue(value) && !(value instanceof String && value.startsWith("REQUIRED:"))) {
							var node = XPathUtils.createElementFromXPath(target, m.targetNode);
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
				// check if a default value and a target node were supplied
				// -> set a target node to a default value
				} else if ((m.defaultValue || m.setUUID) && m.targetNode) {
					var nodeText = "";
					var value;
					if (m.setUUID) {
						value = uuid;
					} else {
						value = m.defaultValue;
					}
					var node = XPathUtils.createElementFromXPath(target, m.targetNode);
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


function mapAccessConstraints(source, target) {
	log.debug("mapAccessConstraints");
	var accessConsts = XPathUtils.getNodeList(source, "/metadata/dataIdInfo/resConst/LegConsts/accessConsts/RestrictCd/@value");
	var useConsts = XPathUtils.getNodeList(source, "/metadata/dataIdInfo/resConst/LegConsts/useConsts/RestrictCd/@value");
	if (!hasValue(accessConsts) && !hasValue(useConsts)) {
		
		var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/additional-information/access-constraint/restriction");
		XMLUtils.createOrReplaceTextNode(node, "keine");
		XMLUtils.createOrReplaceAttribute(node, "id", "1");
		node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/additional-information/access-constraint/terms-of-use");
		XMLUtils.createOrReplaceTextNode(node, "keine Einschr‰nkungen");
	
	} else if (hasValue(accessConsts) && accessConsts.getLength() == 1 && accessConsts.item(0).getTextContent() == "006") {
		
		var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/additional-information/access-constraint/restriction");
		XMLUtils.createOrReplaceTextNode(node, "aufgrund der Rechte des geistigen Eigentums");
		XMLUtils.createOrReplaceAttribute(node, "id", "6");
		var useConst = "";
		for (var i=0; i<useConsts.getLength(); i++ ) {
			if (i > 0) {
				useConst += ", ";
			}
			useConst += transformToIgcDomainValue(524, parseToInt(useConsts.item(i).getTextContent()), 150);
		}
		node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/additional-information/access-constraint/terms-of-use");
		XMLUtils.createOrReplaceTextNode(node, useConst);
	}
}

function mapDataScale(source, target) {
	var refNoms = XPathUtils.getNodeList(source, "/metadata/dataIdInfo/dataScale/equScale/rfDenom");
	log.debug("Found " + refNoms.getLength() + " refNom records.");
	for (var i=0; i<refNoms.getLength(); i++ ) {
		var refNom = refNoms.item(i).getTextContent()
		if (hasValue(refNom)) {
			var refNomSplitted = refNom.split("/");
			for (var j=0; j<refNomSplitted.length; j++ ) {
				var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/technical-domain/map");
				node = node.appendChild(target.getOwnerDocument().createElement("publication-scale"));
				node = node.appendChild(target.getOwnerDocument().createElement("scale"));
				refNomSplitted[j] = replaceString(refNomSplitted[j], "\,", ".");
				XMLUtils.createOrReplaceTextNode(node, refNomSplitted[j]);
			}
		}
	}
	var scaleDists = XPathUtils.getNodeList(source, "/metadata/dataIdInfo/dataScale/scaleDist/value");
	log.debug("Found " + scaleDists.getLength() + " scaleDist records.");
	for (var i=0; i<scaleDists.getLength(); i++ ) {
		var scaleDist = scaleDists.item(i).getTextContent()
		if (hasValue(scaleDist)) {
			var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/technical-domain/map");
			node = node.appendChild(target.getOwnerDocument().createElement("publication-scale"));
			node = node.appendChild(target.getOwnerDocument().createElement("resolution-ground"));
			scaleDist = replaceString(scaleDist, "\,", ".");
			XMLUtils.createOrReplaceTextNode(node, scaleDist);
		}
	}
	
}


function mapCommunicationData(source, target) {
	var email = XPathUtils.getString(source, "rpCntInfo/cntAddress/eMailAdd");
	log.debug("found email:" + email)
	if (hasValue(email)) {
		var communication = target.appendChild(target.getOwnerDocument().createElement("communication"));
		var node = XPathUtils.createElementFromXPath(communication, "communication-medium");
		XMLUtils.createOrReplaceTextNode(node, "Email");
		XMLUtils.createOrReplaceAttribute(node, "id", "3");
		node = XPathUtils.createElementFromXPath(communication, "communication-value");
		XMLUtils.createOrReplaceTextNode(node, email);
	}
	var phone = XPathUtils.getString(source, "rpCntInfo/cntPhone/voiceNum");
	if (hasValue(phone)) {
		var communication = target.appendChild(target.getOwnerDocument().createElement("communication"));
		var node = XPathUtils.createElementFromXPath(communication, "communication-medium");
		XMLUtils.createOrReplaceTextNode(node, "Telefon");
		XMLUtils.createOrReplaceAttribute(node, "id", "1");
		node = XPathUtils.createElementFromXPath(communication, "communication-value");
		XMLUtils.createOrReplaceTextNode(node, phone);
	}
	var fax = XPathUtils.getString(source, "rpCntInfo/cntPhone/faxNum");
	if (hasValue(fax)) {
		var communication = target.appendChild(target.getOwnerDocument().createElement("communication"));
		var node = XPathUtils.createElementFromXPath(communication, "communication-medium");
		XMLUtils.createOrReplaceTextNode(node, "Fax");
		XMLUtils.createOrReplaceAttribute(node, "id", "2");
		node = XPathUtils.createElementFromXPath(communication, "communication-value");
		XMLUtils.createOrReplaceTextNode(node, fax);
	}
}


function mapCreateDateTime(source, target) {
	var dateStr = XPathUtils.getString(source, "/metadata/Esri/CreaDate");
	var timeStr = XPathUtils.getString(source, "/metadata/Esri/CreaTime");
	if (hasValue(dateStr) && hasValue(dateStr)) {
		var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/general/date-of-creation");
		XMLUtils.createOrReplaceTextNode(node, dateStr + timeStr + "000");
	}
}


function mapTimeConstraints(source, target) {
	
	var timePeriods = XPathUtils.getNodeList(source, "/metadata/dataIdInfo/dataExt/tempEle/TempExtent/exTemp/TM_GeometricPrimitive/TM_Period");
	log.debug("Found " + timePeriods.getLength() + " TimePeriod records.");
	if (hasValue(timePeriods) && timePeriods.getLength() > 0) {
		var beginPosition = XPathUtils.getString(timePeriods.item(0), "begin");
		var endPosition = XPathUtils.getString(timePeriods.item(0), "end");
		if (hasValue(beginPosition) && hasValue(endPosition)) {
			if (beginPosition.equals(endPosition)) {
				var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/beginning-date");
				XMLUtils.createOrReplaceTextNode(node, UtilsCSWDate.mapDateFromIso8601ToIndex(beginPosition));
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/ending-date");
				XMLUtils.createOrReplaceTextNode(node, UtilsCSWDate.mapDateFromIso8601ToIndex(endPosition));
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/time-type");
				XMLUtils.createOrReplaceTextNode(node, "am");
			} else {
				var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/beginning-date");
				XMLUtils.createOrReplaceTextNode(node, UtilsCSWDate.mapDateFromIso8601ToIndex(beginPosition));
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/ending-date");
				XMLUtils.createOrReplaceTextNode(node, UtilsCSWDate.mapDateFromIso8601ToIndex(endPosition));
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/time-type");
				XMLUtils.createOrReplaceTextNode(node, "von");
			}
		} else if (hasValue(beginPosition)) {
				var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/beginning-date");
				XMLUtils.createOrReplaceTextNode(node, UtilsCSWDate.mapDateFromIso8601ToIndex(beginPosition));
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/time-type");
				XMLUtils.createOrReplaceTextNode(node, "seit");
		} else if (hasValue(endPosition)) {
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/ending-date");
				XMLUtils.createOrReplaceTextNode(node, UtilsCSWDate.mapDateFromIso8601ToIndex(endPosition));
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/time-type");
				XMLUtils.createOrReplaceTextNode(node, "bis");
		}
	} else {
		var timePositions = XPathUtils.getNodeList(source, "/metadata/dataIdInfo/dataExt/tempEle/TempExtent/exTemp/TM_GeometricPrimitive/TM_Instant/tmPosition/TM_DateAndTime");
		log.debug("Found " + timePositions.getLength() + " tmPosition records.");
		if (hasValue(timePositions)) {
			var calDate = XPathUtils.getString(timePositions.item(0), "calDate");
			var clkTime = XPathUtils.getString(timePositions.item(0), "clkTime");
			var dateTime = calDate + clkTime;
			var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/beginning-date");
			XMLUtils.createOrReplaceTextNode(node, dateTime);
			node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/ending-date");
			XMLUtils.createOrReplaceTextNode(node, dateTime);
			node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/time-type");
			XMLUtils.createOrReplaceTextNode(node, "am");
		}
	}
}

function replaceString(val, regExpr, replaceWith) {
	result =  val.replaceAll(regExpr, replaceWith)
	log.debug("Replaced String '" + val + "' to '" + result + "'")
	return result
}

function padString(val, padding) {
	return val+padding;
}

function parseToInt(val) {
	return java.lang.Integer.parseInt(val);
}



function transformGeneric(val, mappings, caseSensitive, logErrorOnNotFound) {
	for (var key in mappings) {
		if (caseSensitive) {
			if (key == val) {
				return mappings[key];
			}
		} else {
			if (key.toLowerCase() == val.toLowerCase()) {
				return mappings[key];
			}
		}
	}
	if (logErrorOnNotFound) {
		log.error(logErrorOnNotFound + val);
	}
	return val;
}

function transformToIgcDomainId(val, codeListId, languageId, logErrorOnNotFound) {
	if (hasValue(val)) {
		// transform to IGC domain id
		var idcCode = null;
		try {
			idcCode = UtilsUDKCodeLists.getCodeListDomainId(codeListId, val, languageId);
		} catch (e) {
			if (log.isInfoEnabled()) {
				log.info("Error tranforming value '" + val + "' with code list " + codeListId + ". Does the codeList exist?");
			}
			if (logErrorOnNotFound) {
				log.error(logErrorOnNotFound + val);
			}
		}
		if (hasValue(idcCode)) {
			return idcCode;
		} else {
			if (log.isInfoEnabled()) {
				log.info("Domain code '" + val + "' unknown in code list " + codeListId + ".");
			}
			if (logErrorOnNotFound) {
				log.error(logErrorOnNotFound + val);
			}
			return -1;
		}
	}
}


function transformToIgcDomainValue(val, codeListId, languageId, logErrorOnNotFound) {
	if (hasValue(val)) {
		// transform to IGC domain id
		var idcValue = null;
		try {
			idcValue = UtilsUDKCodeLists.getCodeListEntryName(codeListId, val, languageId);
		} catch (e) {
			if (log.isInfoEnabled()) {
				log.info("Error tranforming value '" + val + "' with code list " + codeListId + ". Does the codeList exist?");
			}
			if (logErrorOnNotFound) {
				log.error(logErrorOnNotFound + val);
			}
		}
		if (hasValue(idcValue)) {
			return idcValue;
		} else {
			if (log.isInfoEnabled()) {
				log.info("Domain code '" + val + "' unknown in code list " + codeListId + ".");
			}
			if (logErrorOnNotFound) {
				log.error(logErrorOnNotFound + val);
			}
			return -1;
		}
	}
}

function transformISOToIgcDomainId(val, codeListId, logErrorOnNotFound) {
	if (hasValue(val)) {
		// transform to IGC domain id
		var idcCode = null;
		try {
			idcCode = UtilsUDKCodeLists.getIgcIdFromIsoCodeListEntry(codeListId, val);
		} catch (e) {
			if (log.isInfoEnabled()) {
				log.info("Error tranforming value '" + val + "' with code list " + codeListId + ". Does the codeList exist?");
			}
			if (logErrorOnNotFound) {
				log.error(logErrorOnNotFound + val);
			}
		}
		if (hasValue(idcCode)) {
			return idcCode;
		} else {
			if (log.isInfoEnabled()) {
				log.info("Domain code '" + val + "' unknown in code list " + codeListId + ".");
			}
			if (logErrorOnNotFound) {
				log.error(logErrorOnNotFound + val);
			}
			return -1;
		}
	}
}

function hasValue(val) {
	if (typeof val == "undefined") {
		return false; 
	} else if (!val) {
		return false; 
	} else if (val == null) {
		return false; 
	} else if (val instanceof String && val == "") {
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

function createUUID() {
	var uuid = java.util.UUID.randomUUID();
	var idcUuid = new java.lang.StringBuffer(uuid.toString().toUpperCase());
	while (idcUuid.length() < 36) {
		idcUuid.append("0");
	}
	log.error("Created UUID:" + idcUuid.toString());

	return idcUuid.toString();
}

