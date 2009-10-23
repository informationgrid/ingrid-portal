/**
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 * 
 * CSW 2.0.2 AP ISO 1.0 import script. This script translates an input xml from
 * the CSW 2.0.2 format structure into a IGC 
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
	log.debug("Mapping CSW 2.0.2 AP ISO 1.0 document to IGC import document.");
}

var mappingDescription = {"mappings":[
  		
  		
  		// ****************************************************
  		//
  		// /igc/data-sources/data-source/general
  		//
  		// ****************************************************
  		{
  			// set the obj_class to a fixed value "Geoinformation/Karte"
  			"srcXpath":"//gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue",
  			"targetNode":"/igc/data-sources/data-source/general/object-class",
  			"targetAttribute":"id",
  			"transform":{
				"funct":getObjectClassFromHierarchyLevel
			}
  		},
		{
  			"srcXpath":"//gmd:identificationInfo//gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString",
			"targetNode":"/igc/data-sources/data-source/general/title"
  		},
  		{
  			"srcXpath":"//gmd:identificationInfo//gmd:abstract/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/general/abstract"
  		},
		{
			"srcXpath":"//gmd:dateStamp/gco:DateTime | //gmd:dateStamp/gco:Date[not(../gco:DateTime)]",
			"targetNode":"/igc/data-sources/data-source/general/date-of-creation",
			"transform":{
				"funct":UtilsCSWDate.mapDateFromIso8601ToIndex
			}
		},
  		{	
  			"srcXpath":"//gmd:fileIdentifier/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/general/original-control-identifier"
  		},
  		{	
  			"srcXpath":"//gmd:metadataStandardName/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/general/metadata/metadata-standard-name"
  		},
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:citation/gmd:CI_Citation/gmd:alternateTitle/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/general/dataset-alternate-name",
  			"concatEntriesWith":", "
  		},
  		{
  			"srcXpath":"//gmd:identificationInfo//gmd:topicCategory",
  			"targetNode":"/igc/data-sources/data-source/general/topic-categories",
  			"newNodeName":"topic-category",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"gmd:MD_TopicCategoryCode",
			  			"targetNode":"",
			  			"targetAttribute":"id",
			  			"transform":{
							"funct":transformISOToIgcDomainId,
							"params":[527, "Could not tranform topic category: "]
						}
			  		}
			  	]
			}
  		},

  		// ****************************************************
  		//
  		// /igc/data-sources/data-source/technical-domain/map
  		//
  		// ****************************************************

  		{	
  			"srcXpath":"//gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/hierarchy-level",
  			"targetAttribute":"iso-code",
			"transform":{
				"funct":transformGeneric,
				"params":[{"dataset":"5", "series":"6"}, false, "Could not map hierarchyLevel (only 'dataset' and 'series' are supported) : "]
  			}
  		},
  		{
  			"execute":{
  				"funct":mapReferenceSystemInfo
  			}
		},
  		{
  			"srcXpath":"//gmd:identificationInfo/gmd:MD_DataIdentification",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map",
  			"newNodeName":"publication-scale",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"gmd:spatialResolution/gmd:MD_Resolution/gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator/gco:Integer",
			  			"targetNode":"scale"
			  		},
	  				{
			  			"srcXpath":"gmd:spatialResolution/gmd:MD_Resolution/gmd:distance/gmd:Distance[@uom='meter']",
			  			"targetNode":"resolution-ground"
			  		},
	  				{
			  			"srcXpath":"gmd:spatialResolution/gmd:MD_Resolution/gmd:distance/gmd:Distance[@uom='dpi']",
			  			"targetNode":"resolution-scan"
			  		}
			  	]
			}
  		},
  		{	
  			"srcXpath":"//gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:processStep/gmd:LI_ProcessStep/gmd:description/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/method-of-production"
  		},
  		{	
  			"srcXpath":"//gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:statement/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/technical-base"
  		},
  		{
  			"srcXpath":"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialRepresentationType",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map",
  			"newNodeName":"spatial-representation-type",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"gmd:MD_SpatialRepresentationTypeCode/@codeListValue",
			  			"targetNode":"",
			  			"targetAttribute":"iso-code",
			  			"transform":{
    						"funct":transformISOToIgcDomainId,
    						"params":[526, "Could not transform spatial representation type: "]
    					}
			  		}
			  	]
			}
  		},
  		{	
  			"srcXpath":"//gmd:spatialRepresentationInfo/gmd:MD_VectorSpatialRepresentation/gmd:topologyLevel/gmd:MD_TopologyLevelCode/@codeListValue",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/vector-format/vector-topology-level",
  			"targetAttribute":"iso-code",
  			"transform":{
				"funct":transformISOToIgcDomainId,
				"params":[528, "Could not transform vector topology level: "]
			}
  		},
  		{
  			"srcXpath":"//gmd:spatialRepresentationInfo/gmd:MD_VectorSpatialRepresentation/gmd:geometricObjects",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map/vector-format",
  			"newNodeName":"geo-vector",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"gmd:MD_GeometricObjects/gmd:geometricObjectType/gmd:MD_GeometricObjectTypeCode/@codeListValue",
			  			"targetNode":"geometric-object-type",
			  			"targetAttribute":"iso-code",
			  			"transform":{
							"funct":transformISOToIgcDomainId,
							"params":[515, "Could not transform geometric object type code: "]
						}
			  		},
	  				{
			  			"srcXpath":"gmd:MD_GeometricObjects/gmd:geometricObjectCount/gco:Integer",
			  			"targetNode":"geometric-object-count"
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"//gmd:contentInfo/gmd:MD_FeatureCatalogueDescription/gmd:featureTypes",
  			"targetNode":"/igc/data-sources/data-source/technical-domain/map",
  			"newNodeName":"feature-type",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"gco:LocalName",
			  			"targetNode":""
			  		}
			  	]
			}
  		},

  		// ****************************************************
  		//
  		// /igc/data-sources/data-source/additional-information
  		//
  		// ****************************************************
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:language/gmd:LanguageCode/@codeListValue",
  			"targetNode":"/igc/data-sources/data-source/additional-information/data-language",
  			"targetAttribute":"id",
  			"transform":{
				"funct":transformISOToIGCLanguageCode
			}
  		},
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:language/gmd:LanguageCode/@codeListValue",
  			"targetNode":"/igc/data-sources/data-source/additional-information/data-language",
  			"transform":{
				"funct":transformISOToLanguage,
				"params":['de']
			}
  		},
  		{	
  			"srcXpath":"gmd:MD_Metadata/gmd:language/gmd:LanguageCode/@codeListValue",
  			"defaultValue":"de",
  			"targetNode":"/igc/data-sources/data-source/additional-information/metadata-language",
  			"targetAttribute":"id",
  			"transform":{
				"funct":transformISOToIGCLanguageCode
			}
  		},
  		{	
  			"srcXpath":"gmd:MD_Metadata/gmd:language/gmd:LanguageCode/@codeListValue",
  			"defaultValue":"de",
  			"targetNode":"/igc/data-sources/data-source/additional-information/metadata-language",
  			"transform":{
				"funct":transformISOToLanguage,
				"params":['de']
			}
  		},
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:purpose/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/additional-information/dataset-intentions"
  		},
  		{
  			"srcXpath":"//gmd:identificationInfo//gmd:resourceConstraints",
  			"targetNode":"/igc/data-sources/data-source/additional-information",
  			"newNodeName":"access-constraint",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"//gmd:otherConstraints/gco:CharacterString",
			  			"targetNode":"restriction",
			  			"targetAttribute":"id",
			  			"defaultValue":"-1",
			  			"transform":{
    						"funct":transformToIgcDomainId,
    						"params":[6010, 150]
    					}
			  		},
	  				{
			  			"srcXpath":"//gmd:otherConstraints/gco:CharacterString",
			  			"targetNode":"restriction"
			  		},
	  				{
			  			"srcXpath":"//gmd:useLimitation/gco:CharacterString",
			  			"targetNode":"terms-of-use"
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"//gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:offLine/gmd:MD_Medium",
  			"targetNode":"/igc/data-sources/data-source/additional-information",
  			"newNodeName":"medium-option",
  			"subMappings":{
  				"mappings": [
					{
						"srcXpath":"gmd:mediumNote/gco:CharacterString",
						"targetNode":"medium-note",
					},
	  				{
			  			"srcXpath":"gmd:name/gmd:MD_MediumNameCode/@codeListValue",
			  			"targetNode":"medium-name",
			  			"targetAttribute":"iso-code",
			  			"transform":{
							"funct":transformISOToIgcDomainId,
							"params":[520, "Could not transform meduim name code: "]
						}
			  		},
	  				{
			  			"srcXpath":"../../gmd:transferSize/gco:CharacterString",
			  			"targetNode":"transfer-size",
			  			"transform":{
							"funct":transformNumberStrToIGCNumber
						}
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"//gmd:distributionInfo/gmd:MD_Distribution/gmd:distributionFormat/gmd:MD_Format",
  			"targetNode":"/igc/data-sources/data-source/additional-information",
  			"newNodeName":"data-format",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"gmd:name/gco:CharacterString",
			  			"targetNode":"format-name"
			  		},
	  				{
			  			"srcXpath":"gmd:name/gco:CharacterString",
			  			"targetNode":"format-name",
			  			"targetAttribute":"id",
			  			"defaultValue":"-1",
			  			"transform":{
							"funct":transformToIgcDomainId,
							"params":[1320, 150]
						}
			  		},
	  				{
			  			"srcXpath":"gmd:version/gco:CharacterString",
			  			"targetNode":"version"
			  		},
	  				{
			  			"srcXpath":"gmd:fileDecompressionTechnique/gco:CharacterString",
			  			"targetNode":"file-decompression-technique"
			  		},
	  				{
			  			"srcXpath":"gmd:specification/gco:CharacterString",
			  			"targetNode":"specification"
			  		}
			  	]
			}
  		},
  		{	
  			"defaultValue":"1",
  			"targetNode":"/igc/data-sources/data-source/additional-information/publication-condition"
  		},
  		{	
  			"srcXpath":"//gmd:distributionInfo/gmd:MD_Distribution/gmd:distributor/gmd:MD_Distributor/gmd:distributionOrderProcess/gmd:MD_StandardOrderProcess/gmd:orderingInstructions/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/additional-information/ordering-instructions"
  		},
  		{
  			"srcXpath":"//gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:report/gmd:DQ_DomainConsistency/gmd:result/gmd:DQ_ConformanceResult",
  			"targetNode":"/igc/data-sources/data-source/additional-information",
  			"newNodeName":"conformity",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"gmd:specification/gmd:CI_Citation/gmd:title/gco:CharacterString",
			  			"targetNode":"conformity-specification"
			  		},
	  				{
			  			"srcXpath":"gmd:specification/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date/gco:Date",
			  			"targetNode":"conformity-publication-date",
			  			"transform":{
							"funct":UtilsCSWDate.mapDateFromIso8601ToIndex
						}
			  		},
	  				{
			  			"srcXpath":"gmd:pass/gco:Boolean",
			  			"targetNode":"conformity-degree",
			  			"targetAttribute":"id",
			  			"transform":{
							"funct":transformGeneric,
							"params":[{"true":"1", "false":"2"}, false]
						}
			  		},
	  				{
			  			"srcXpath":"gmd:pass/gco:Boolean",
			  			"targetNode":"conformity-degree",
			  			"transform":{
							"funct":transformGeneric,
							"params":[{"true":"konform", "false":"nicht konform"}, false]
						}
			  		}
			  	]
			}
  		},

  		
  		// ****************************************************
  		//
  		// /igc/data-sources/data-source/spatial-domain
  		//
  		// ****************************************************
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:extent/gmd:EX_Extent/gmd:verticalElement/gmd:EX_VerticalExtent/gmd:minimumValue/gco:Real",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain/vertical-extent/vertical-extent-minimum",
  			"transform":{
				"funct":transformNumberStrToIGCNumber
			}
  		},
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:extent/gmd:EX_Extent/gmd:verticalElement/gmd:EX_VerticalExtent/gmd:maximumValue/gco:Real",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain/vertical-extent/vertical-extent-maximum",
  			"transform":{
				"funct":transformNumberStrToIGCNumber
			}
  		},
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:EX_Extent/gmd:verticalElement/gmd:EX_VerticalExtent/gmd:verticalCRS/gmd:VerticalCRS/gmd:verticalCS/gml:VerticalCS/gml:axis/gml:CoordinateSystemAxis/@uom",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain/vertical-extent/vertical-extent-unit",
  			"targetAttribute":"id",
  			"transform":{
				"funct":transformToIgcDomainId,
				"params":[102, 150, "Could not map vertical-extent unit:"]
			}						    					
  		},
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:EX_Extent/gmd:verticalElement/gmd:EX_VerticalExtent/gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum/gml:identifier",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain/vertical-extent/vertical-extent-vdatum",
  			"targetAttribute":"id",
  			// check for german (150) name in the code list 101
  			"transform":{
				"funct":transformToIgcDomainId,
				"params":[101, 150, "Could not map vertical-extent vdatum name: "]
			}						    					
  		},
  		{
  			"srcXpath":"//gmd:identificationInfo//gmd:extent/gmd:EX_Extent/gmd:geographicElement",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain",
  			"newNodeName":"geo-location",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code/gco:CharacterString",
	  					"defaultValue":"Raumbezug des Datensatzes",
			  			"targetNode":"uncontrolled-location/location-name"
			  		},
	  				{
			  			"defaultValue":"-1",
			  			"targetNode":"uncontrolled-location/location-name",
			  			"targetAttribute":"id"
			  		},
	  				{
			  			"srcXpath":"gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude/gco:Decimal",
			  			"targetNode":"bounding-coordinates/west-bounding-coordinate",
			  			"transform":{
							"funct":transformNumberStrToIGCNumber
						}
			  		},
	  				{
			  			"srcXpath":"gmd:EX_GeographicBoundingBox/gmd:eastBoundLongitude/gco:Decimal",
			  			"targetNode":"bounding-coordinates/east-bounding-coordinate",
			  			"transform":{
							"funct":transformNumberStrToIGCNumber
						}
			  		},
	  				{
			  			"srcXpath":"gmd:EX_GeographicBoundingBox/gmd:northBoundLatitude/gco:Decimal",
			  			"targetNode":"bounding-coordinates/north-bounding-coordinate",
			  			"transform":{
							"funct":transformNumberStrToIGCNumber
						}
			  		},
	  				{
			  			"srcXpath":"gmd:EX_GeographicBoundingBox/gmd:southBoundLatitude/gco:Decimal",
			  			"targetNode":"bounding-coordinates/south-bounding-coordinate",
			  			"transform":{
							"funct":transformNumberStrToIGCNumber
						}
			  		}
			  	]
			}
  		},
  	

  		// ****************************************************
  		//
  		// /igc/data-sources/data-source/temporal-domain
  		//
  		// ****************************************************
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceNote/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/temporal-domain/description-of-temporal-domain"
  		},
  		{	
  			"execute":{
				"funct":mapTimeConstraints
			}
  		},
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:maintenanceAndUpdateFrequency/gmd:MD_MaintenanceFrequencyCode/@codeListValue",
  			"targetNode":"/igc/data-sources/data-source/temporal-domain/time-period",
  			"targetAttribute":"iso-code",
  			"transform":{
				"funct":transformISOToIgcDomainId,
				"params":[518, "Could not map time-period to ISO code: "]
			}
  		},
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:userDefinedMaintenanceFrequency/gmd:TM_PeriodDuration",
  			"targetNode":"/igc/data-sources/data-source/temporal-domain/time-step",
  			"transform":{
				"funct":new TM_PeriodDurationToTimeInterval().parse
			}
  		},
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:resourceMaintenance/gmd:MD_MaintenanceInformation/gmd:userDefinedMaintenanceFrequency/gmd:TM_PeriodDuration",
  			"targetNode":"/igc/data-sources/data-source/temporal-domain/time-step",
  			"transform":{
				"funct":new TM_PeriodDurationToTimeAlle().parse
			}
  		},
  		{
  			"srcXpath":"//gmd:identificationInfo//gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date",
  			"targetNode":"/igc/data-sources/data-source/temporal-domain",
  			"newNodeName":"dataset-reference",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"gmd:date/gco:Date",
			  			"targetNode":"dataset-reference-date",
			  			"transform":{
							"funct":UtilsCSWDate.mapDateFromIso8601ToIndex
						}
			  		},
	  				{
			  			"srcXpath":"gmd:dateType/gmd:CI_DateTypeCode/@codeListValue",
			  			"targetNode":"dataset-reference-type",
			  			"targetAttribute":"iso-code",
			  			"transform":{
    						"funct":transformISOToIgcDomainId,
    						"params":[502, "Could not map date role: "]
    					}
			  		}
			  	]
			}
  		},
  		
  		// ****************************************************
  		//
  		// /igc/data-sources/data-source/subject-terms
  		//
  		// ****************************************************
  		{
  			"srcXpath":"//gmd:identificationInfo//gmd:descriptiveKeywords/gmd:MD_Keywords[gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString='GEMET - INSPIRE themes, version 1.0']/gmd:keyword/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/subject-terms",
  			"newNodeName":"controlled-term",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":".",
			  			"targetNode":""
			  		},
	  				{
			  			"defaultValue":"INSPIRE",
			  			"targetNode":"",
			  			"targetAttribute":"source"
			  		}
			  	]
			}		
  		},		
  		{
  			"srcXpath":"//gmd:identificationInfo//gmd:descriptiveKeywords/gmd:MD_Keywords[gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString='GEMET - Concepts, version 2.1']/gmd:keyword/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/subject-terms",
  			"newNodeName":"controlled-term",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":".",
			  			"targetNode":""
			  		},
	  				{
			  			"defaultValue":"GEMET",
			  			"targetNode":"",
			  			"targetAttribute":"source"
			  		}
			  	]
			}		
  		},		
  		{
  			"srcXpath":"//gmd:identificationInfo//gmd:descriptiveKeywords/gmd:MD_Keywords[gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString='UMTHES Thesaurus']/gmd:keyword/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/subject-terms",
  			"newNodeName":"controlled-term",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":".",
			  			"targetNode":""
			  		},
	  				{
			  			"defaultValue":"UMTHES",
			  			"targetNode":"",
			  			"targetAttribute":"source"
			  		}
			  	]
			}		
  		},		
  		{
  			"srcXpath":"//gmd:identificationInfo//gmd:descriptiveKeywords/gmd:MD_Keywords[gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString!='GEMET - INSPIRE themes, version 1.0' and gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString!='GEMET - Concepts, version 2.1' and gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString!='UMTHES Thesaurus']/gmd:keyword/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/subject-terms",
  			"newNodeName":"uncontrolled-term",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":".",
			  			"targetNode":""
			  		}
			  	]
			}		
  		},		

  		// ****************************************************
  		//
  		// /igc/data-sources/data-source/available-linkage
  		//
  		// ****************************************************

  		{
  			"srcXpath":"//gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource",
  			"targetNode":"/igc/data-sources/data-source",
  			"newNodeName":"available-linkage",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"gmd:name/gco:CharacterString",
			  			"targetNode":"linkage-name"
			  		},
	  				{
			  			"srcXpath":"gmd:linkage/gmd:URL",
			  			"targetNode":"linkage-url"
			  		},
	  				{
			  			"defaultValue":"1",
			  			"targetNode":"linkage-url-type"
			  		},
	  				{
			  			"srcXpath":"gmd:description/gco:CharacterString",
			  			"targetNode":"linkage-description"
			  		},
	  				{
			  			"defaultValue":"-1",
			  			"targetNode":"linkage-reference",
			  			"targetAttribute":"id"
			  		}
			  	]
			}
  		},

  		// ****************************************************
  		//
  		// /igc/addresses
  		//
  		// ****************************************************
  		
  		{	
  			"srcXpath":"//*/gmd:CI_ResponsibleParty",
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
			  			"defaultValue":"xxx",
			  			"targetNode":"modificator-identifier"
			  		},
	  				{
			  			"defaultValue":"xxx",
			  			"targetNode":"responsible-identifier"
			  		},
	  				{
			  			"defaultValue":"3",
			  			"targetNode":"type-of-address",
			  			"targetAttribute":"id"
			  		},
	  				{
			  			"srcXpath":"gmd:organisationName/gco:CharacterString",
			  			"targetNode":"organisation"
			  		},
	  				{
			  			"srcXpath":"gmd:individualName/gco:CharacterString",
			  			"targetNode":"name"
			  		},
	  				{
			  			"srcXpath":"gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country/gco:CharacterString",
			  			"targetNode":"country",
			  			"targetAttribute":"id",
			  			"defaultValue":"-1",
			  			"transform":{
							"funct":UtilsCountryCodelist.getCodeFromShortcut3
						}
			  		},
	  				{
			  			"srcXpath":"gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country/gco:CharacterString",
			  			"targetNode":"country"
			  		},
	  				{
			  			"srcXpath":"gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode/gco:CharacterString",
			  			"targetNode":"postal-code"
			  		},
	  				{
			  			"srcXpath":"gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint/gco:CharacterString",
			  			"targetNode":"street"
			  		},
	  				{
			  			"srcXpath":"gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city/gco:CharacterString",
			  			"targetNode":"city"
			  		},
			  		{	
			  			"execute":{
							"funct":mapCommunicationData
						}
			  		},
	  				{
			  			"srcXpath":"gmd:positionName/gco:CharacterString",
			  			"targetNode":"function"
			  		},
			  		{
				  		"srcXpath":".",
			  			"targetNode":"/igc/data-sources/data-source",
			  			"newNodeName":"related-address",
			  			"subMappings":{
			  				"mappings": [
				  				{
						  			"srcXpath":"gmd:role/gmd:CI_RoleCode/@codeListValue",
						  			"targetNode":"type-of-relation",
						  			"targetAttribute":"entry-id",
						  			"transform":{
										"funct":transformISOToIgcDomainId,
										"params":[505, "Could not transform ISO address role code to IGC id: "]
									}
						  		},
				  				{
						  			"defaultValue":"505",
						  			"targetNode":"type-of-relation",
						  			"targetAttribute":"list-id"
						  		},
				  				{
						  			"srcXpath":"gmd:role/gmd:CI_RoleCode/@codeListValue",
						  			"targetNode":"type-of-relation",
							  		"transform":{
										"funct":transformISOToIgcDomainValue,
										"params":[505, "Could not transform ISO address role code to IGC codelist value: "]
									}
						  				
						  		},
				  				{
						  			"setUUID":"true",
						  			"targetNode":"address-identifier"
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
			log.debug("start working on mapping: " + m);
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
						// check for transformation
						if (hasValue(m.transform)) {
							log.debug("Transform value '" + value + "'")
							var args = new Array(value);
							if (hasValue(m.transform.params)) {
								args = args.concat(m.transform.params);
							}
							value = call_f(m.transform.funct,args);
						}
						if (m.defaultValue && !hasValue(value)) {
							log.debug("Setting value to default '" + m.defaultValue + "'")
							value = m.defaultValue;
						}
						if (hasValue(value)) {
							var node = XPathUtils.createElementFromXPath(target, m.targetNode);
							log.debug("Found node with content: '" + node.getTextContent() + "'")
							if (j==0) { 
								// append content to target nodes content?
								if (m.appendWith && hasValue(node.getTextContent())) {
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
				// check if a default value was supplied
				// -> set a target node to a default value
				} else if (m.defaultValue || m.setUUID) {
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



function getObjectClassFromHierarchyLevel(val) {
	// default to "Geo-Information / Karte"
	var result = "1"; 
	if (hasValue(val) && val.toLowerCase() == "service") {
		// "Dienst / Anwendung / Informationssystem"
		result = "3";
	}
	return result;
}



function validateSource(source) {
	// pre check source if required
	return true;
}

function mapReferenceSystemInfo(source, target) {
	var rsIdentifiers = XPathUtils.getNodeList(source, "//gmd:referenceSystemInfo/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier");
	if (hasValue(rsIdentifiers)) {
		for (i=0; i<rsIdentifiers.getLength(); i++ ) {
			var code = XPathUtils.getString(rsIdentifiers.item(i), "gmd:code/gco:CharacterString");
			var codeSpace = XPathUtils.getString(rsIdentifiers.item(i), "gmd:codeSpace/gco:CharacterString");
			var coordinateSystem;
			if (hasValue(codeSpace) && hasValue(code)) {
				coordinateSystem = codeSpace+":"+code; 
			} else if (hasValue(code)) {
				coordinateSystem = code;
			}
			log.debug("adding '" + "/igc/data-sources/data-source/technical-domain/map/coordinate-system" + "' = '" + coordinateSystem + "' to target document.");
			var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/technical-domain/map/coordinate-system");
			XMLUtils.createOrReplaceTextNode(node, coordinateSystem);
			var coordinateSystemId = transformToIgcDomainId(coordinateSystem, 100, 150, "Could not map coordinate-system: ");
			if (hasValue(coordinateSystemId)) {
				XMLUtils.createOrReplaceAttribute(node, "id", coordinateSystemId);
			}
		}
	} else {
		log.debug("No referenceSystemInfo has been found!");
	}
}


function mapCommunicationData(source, target) {
	var email = XPathUtils.getString(source, "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString");
	if (hasValue(email)) {
		var communication = target.appendChild(target.getOwnerDocument().createElement("communication"));
		var node = XPathUtils.createElementFromXPath(communication, "communication-medium");
		XMLUtils.createOrReplaceTextNode(node, "Email");
		XMLUtils.createOrReplaceAttribute(node, "id", "3");
		node = XPathUtils.createElementFromXPath(communication, "communication-value");
		XMLUtils.createOrReplaceTextNode(node, email.trim());
	}
	var phone = XPathUtils.getString(source, "gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice/gco:CharacterString");
	if (hasValue(phone)) {
		var communication = target.appendChild(target.getOwnerDocument().createElement("communication"));
		var node = XPathUtils.createElementFromXPath(communication, "communication-medium");
		XMLUtils.createOrReplaceTextNode(node, "Telefon");
		XMLUtils.createOrReplaceAttribute(node, "id", "1");
		node = XPathUtils.createElementFromXPath(communication, "communication-value");
		XMLUtils.createOrReplaceTextNode(node, phone.trim());
	}
	var fax = XPathUtils.getString(source, "gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile/gco:CharacterString");
	if (hasValue(fax)) {
		var communication = target.appendChild(target.getOwnerDocument().createElement("communication"));
		var node = XPathUtils.createElementFromXPath(communication, "communication-medium");
		XMLUtils.createOrReplaceTextNode(node, "Fax");
		XMLUtils.createOrReplaceAttribute(node, "id", "2");
		node = XPathUtils.createElementFromXPath(communication, "communication-value");
		XMLUtils.createOrReplaceTextNode(node, fax.trim());
	}
	var url = XPathUtils.getString(source, "gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL");
	if (hasValue(url)) {
		var communication = target.appendChild(target.getOwnerDocument().createElement("communication"));
		var node = XPathUtils.createElementFromXPath(communication, "communication-medium");
		XMLUtils.createOrReplaceTextNode(node, "URL");
		XMLUtils.createOrReplaceAttribute(node, "id", "4");
		node = XPathUtils.createElementFromXPath(communication, "communication-value");
		XMLUtils.createOrReplaceTextNode(node, url.trim());
	}
}


function mapTimeConstraints(source, target) {
	var timePeriods = XPathUtils.getNodeList(source, "//gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gmd:TimePeriod");
	log.debug("Found " + timePeriods.getLength() + " TimePeriod records.");
	if (hasValue(timePeriods) && timePeriods.getLength() > 0) {
		var beginPosition = XPathUtils.getString(timePeriods.item(0), "gmd:beginPosition");
		var endPosition = XPathUtils.getString(timePeriods.item(0), "gmd:endPosition");
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
	}
}

function parseToInt(val) {
	return java.lang.Integer.parseInt(val);
}

function transformNumberStrToIGCNumber(val) {
	return UtilsString.transformNumberStrToIGCNumber(val);		
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
			idcValue = UtilsUDKCodeLists.getCodeListEntryName(codeListId, parseToInt(val), languageId);
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
			return "";
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

function transformISOToIgcDomainValue(val, codeListId, languageId, logErrorOnNotFound) {
	if (hasValue(val)) {
		// transform ISO code to IGC domain value
		var idcValue = null;
		try {
			var idcCode = UtilsUDKCodeLists.getIgcIdFromIsoCodeListEntry(codeListId, val);
			idcValue = UtilsUDKCodeLists.getCodeListEntryName(codeListId, parseToInt(idcCode), languageId);
		} catch (e) {
			if (log.isInfoEnabled()) {
				log.info("Error tranforming ISO value '" + val + "' with code list " + codeListId + ". Does the codeList exist?");
			}
			if (logErrorOnNotFound) {
				log.error(logErrorOnNotFound + val);
			}
		}
		if (hasValue(idcValue)) {
			return idcValue;
		} else {
			if (log.isInfoEnabled()) {
				log.info("ISO code '" + val + "' unknown in code list " + codeListId + ".");
			}
			if (logErrorOnNotFound) {
				log.error(logErrorOnNotFound + val);
			}
			return "";
		}
	}
}

function transformISOToIGCLanguageCode(val) {
	var code = UtilsLanguageCodelist.getCodeFromIso639_2(val);
	if (!hasValue(code)) {
		code = UtilsLanguageCodelist.getCodeFromShortcut(val);
	}
	return code;
}

function transformISOToLanguage(val, iso639_1) {
	var code = UtilsLanguageCodelist.getCodeFromIso639_2(val);
	if (!hasValue(code)) {
		code = UtilsLanguageCodelist.getCodeFromShortcut(val);
	}
	if (!hasValue(iso639_1)) {
		return UtilsLanguageCodelist.getNameFromCode(code, "de");
	} else {
		return UtilsLanguageCodelist.getNameFromCode(code, iso639_1);
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

