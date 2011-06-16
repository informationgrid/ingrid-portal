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
 * @param protocol A Protocol instance to add UI protocol messages.
 * @param log A Log instance
 *
 *
 * Example to set debug message for protocol:
 * if(protocol.isDebugEnabled()){
 *		protocol.addMessage(protocol.getCurrentFilename() + ": Debug message");
 * }
 */

importPackage(Packages.de.ingrid.utils.udk);
importPackage(Packages.de.ingrid.utils.xml);
importPackage(Packages.org.w3c.dom);

var DEBUG = 1;
var INFO = 2;
var WARN = 3;
var ERROR = 4;

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
  			"storeValue":"objectClass",
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
				"funct":transformDateIso8601ToIndex
			}
		},
  		{	
  			
			"srcXpath":"//gmd:fileIdentifier/gco:CharacterString",
			// make sure we always have a UUID 
			"defaultValue":createUUID,
  			"targetNode":"/igc/data-sources/data-source/general/original-control-identifier"
  		},
  		{	
  			"srcXpath":"//gmd:metadataStandardName/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/general/metadata/metadata-standard-name"
  		},
  		{	
  			"srcXpath":"//gmd:metadataStandardVersion/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/general/metadata/metadata-standard-version"
  		},
  		{	
  			"srcXpath":"//gmd:MD_Metadata/gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue",
  			"targetNode":"/igc/data-sources/data-source/general/metadata/metadata-character-set",
  			"targetAttribute":"iso-code",
  			"transform":{
				"funct":transformISOToIgcDomainId,
				"params":[510, "Could not tranform gmd:MD_CharacterSetCode: "]
			}
  		},
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:citation/gmd:CI_Citation/gmd:alternateTitle/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/general/dataset-alternate-name",
  			"concatEntriesWith":", "
  		},
        {   
            "srcXpath":"//gmd:identificationInfo/*/gmd:characterSet/gmd:MD_CharacterSetCode/@codeListValue",
            "targetNode":"/igc/data-sources/data-source/general/dataset-character-set",
            "targetAttribute":"iso-code",
            "transform":{
                "funct":transformISOToIgcDomainId,
                "params":[510, "Could not tranform gmd:MD_CharacterSetCode: "]
            }
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
  			"srcXpath":"/",
  			"targetNode":"/",
  			"conditional": {
  				"storedValue": {
  					"name":"objectClass",
  					"value":"1"
  				}
  			},
  			"subMappings": {
  			    "mappings": [
	         		{	
	        			"srcXpath":"//gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue",
	        			"defaultValue":"5", // default to "dataset", if no hierarchyLevel is supplied
	        			"targetNode":"/igc/data-sources/data-source/technical-domain/map/hierarchy-level",
	        			"targetAttribute":"iso-code",
		      			"transform":{
		      				"funct":transformGeneric,
		      				"params":[{"dataset":"5", "series":"6"}, false, "Could not map hierarchyLevel (only 'dataset' and 'series' are supported) : "]
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
                        "srcXpath":"//gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:report/gmd:DQ_CompletenessOmission/gmd:result/gmd:DQ_QuantitativeResult/gmd:value/gco:Record",
                        "targetNode":"/igc/data-sources/data-source/technical-domain/map/degree-of-record"
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
	        		{
	        			"execute":{
	        				"funct":mapRSIdentifier
	        			}
	        		}
  			    ]
  			}
  		},

  		// ****************************************************
  		//
  		// /igc/data-sources/data-source/technical-domain/service
  		//
  		// ****************************************************
  		{
  			"srcXpath":"/",
  			"targetNode":"/",
  			"conditional": {
  				"storedValue": {
  					"name":"objectClass",
  					"value":"3"
  				}
  			},
  			"subMappings": {
  			    "mappings": [
	         		{	
	        			"srcXpath":"//gmd:identificationInfo/srv:SV_ServiceIdentification/srv:serviceType/gco:LocalName",
	        			"targetNode":"/igc/data-sources/data-source/technical-domain/service/service-type",
	        			"targetAttribute":"id",
	        			"storeValue":"serviceType",
		      			"transform":{
		      				"funct":transformGeneric,
		      				"params":[{"discovery":"1", "view":"2", "download":"3", "transformation":"4", "invoke":"5", "other":"6"}, false, "Could not map serviceType : "]
		        		}
		        	},
		        	{
		        		"execute":{
		                	"funct":mapServiceClassifications
		            	}
		        	},
		        	{	
	        			"srcXpath":"//gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:processStep/gmd:LI_ProcessStep/gmd:description/gco:CharacterString",
	        			"targetNode":"/igc/data-sources/data-source/technical-domain/service/database-of-system"
	        		},
	         		{	
		      			"srcXpath":"//gmd:identificationInfo//srv:serviceTypeVersion/gco:CharacterString",
		      			"targetNode":"/igc/data-sources/data-source/technical-domain/service",
		      			"newNodeName":"service-version",
		      			"subMappings":{
		      				"mappings": [
		    	  				{
		    			  			"srcXpath":".",
		    			  			"targetNode":""
		    			  		}
		    			  	]
		    			}
		        	},
	         		{	
		      			"srcXpath":"//gmd:identificationInfo//srv:containsOperations/srv:SV_OperationMetadata",
		      			"targetNode":"/igc/data-sources/data-source/technical-domain/service",
		      			"newNodeName":"service-operation",
		      			"subMappings":{
		      				"mappings": [
		    	  				{
		    			  			"srcXpath":"srv:operationName/gco:CharacterString",
		    			  			"targetNode":"operation-name"
		    			  		},
		    	  				{
		    			  			"conditional": {
			    		  				"storedValue": {
			    		  					"name":"serviceType",
			    		  					"value":"1" // CSW
			    		  				}
		    			  			},
		    			  			"srcXpath":"srv:operationName/gco:CharacterString",
		    			  			"targetNode":"operation-name",
		    			  			"targetAttribute":"id",
		    			  			"transform":{
		        						"funct":transformToIgcDomainId,
		        						"params":[5105, 123]
		        					}
		    			  		},
		    	  				{
		    			  			"conditional": {
			    		  				"storedValue": {
			    		  					"name":"serviceType",
			    		  					"value":"2" // WMS
			    		  				}
		    			  			},
		    			  			"srcXpath":"srv:operationName/gco:CharacterString",
		    			  			"targetNode":"operation-name",
		    			  			"targetAttribute":"id",
		    			  			"transform":{
		        						"funct":transformToIgcDomainId,
		        						"params":[5110, 123]
		        					}
		    			  		},
		    	  				{
		    			  			"conditional": {
			    		  				"storedValue": {
			    		  					"name":"serviceType",
			    		  					"value":"3" // WFS
			    		  				}
		    			  			},
		    			  			"srcXpath":"srv:operationName/gco:CharacterString",
		    			  			"targetNode":"operation-name",
		    			  			"targetAttribute":"id",
		    			  			"transform":{
		        						"funct":transformToIgcDomainId,
		        						"params":[5120, 123]
		        					}
		    			  		},
		    	  				{
		    			  			"conditional": {
			    		  				"storedValue": {
			    		  					"name":"serviceType",
			    		  					"value":"4" // WCTS
			    		  				}
		    			  			},
		    			  			"srcXpath":"srv:operationName/gco:CharacterString",
		    			  			"targetNode":"operation-name",
		    			  			"targetAttribute":"id",
		    			  			"transform":{
		        						"funct":transformToIgcDomainId,
		        						"params":[5130, 123]
		        					}
		    			  		},
		    	  				{
		    			  			"srcXpath":"srv:operationDescription/gco:CharacterString",
		    			  			"targetNode":"description-of-operation"
		    			  		},
		    	  				{
		    			  			"srcXpath":"srv:invocationName/gco:CharacterString",
		    			  			"targetNode":"invocation-name"
		    			  		},
		    	  				{
		    			  			"srcXpath":"srv:DCP/srv:DCPList",
		    			  			"targetNode":"",
	    			      			"newNodeName":"platform",
	    			      			"subMappings":{
					      				"mappings": [
					    	  				{
					    			  			"srcXpath":"./@codeListValue",
					    			  			"targetNode":""
					    			  		}
					    	  			]
		    			  			}	
		    			  		},
		    	  				{
		    			  			"srcXpath":"srv:connectPoint/gmd:CI_OnlineResource/gmd:linkage/gmd:URL",
		    			  			"targetNode":"",
	    			      			"newNodeName":"connection-point",
	    			      			"subMappings":{
					      				"mappings": [
					    	  				{
					    			  			"srcXpath":".",
					    			  			"targetNode":""
					    			  		}
					    	  			]
		    			  			}	
		    			  		},
		    	         		{	
		    		      			"srcXpath":"srv:parameters/srv:SV_Parameter",
		    		      			"targetNode":"",
		    		      			"newNodeName":"parameter-of-operation",
		    		      			"subMappings":{
		    		      				"mappings": [
		    		    	  				{
		    		    			  			"srcXpath":"srv:name/gmd:aName/gco:CharacterString",
		    		    			  			"targetNode":"name"
		    		    			  		},
		    		    	  				{
		    		    			  			"srcXpath":"srv:optionality/gco:CharacterString",
		    		    			  			"targetNode":"optional",
		    		    			  			"defaultValue":"1",
		    		    		      			"transform":{
			    				      				"funct":transformGeneric,
			    				      				"params":[{"optional":"1", "mandatory":"0"}, false, "Could not map srv:optionality : "]
			    				        		}
		    		    			  		},
		    		    	  				{
		    		    			  			"srcXpath":"srv:repeatability/gco:Boolean",
		    		    			  			"targetNode":"repeatability",
		    		    			  			"defaultValue":"0",
		    		    		      			"transform":{
			    				      				"funct":transformGeneric,
			    				      				"params":[{"true":"1", "false":"0"}, false, "Could not map srv:repeatability : "]
			    				        		}
		    		    			  		},
		    		    	  				{
		    		    			  			"srcXpath":"srv:direction/srv:SV_ParameterDirection",
		    		    			  			"targetNode":"direction",
		    		    			  			"defaultValue":"Ein -und Ausgabe",
		    		    		      			"transform":{
			    				      				"funct":transformGeneric,
			    				      				"params":[{"in/out":"Ein -und Ausgabe", "in":"Eingabe", "out":"Ausgabe"}, false, "Could not map srv:direction : "]
			    				        		}
		    		    			  		},
		    		    	  				{
		    		    			  			"srcXpath":"srv:description/gco:CharacterString",
		    		    			  			"targetNode":"description-of-parameter"
		    		    			  		}
		    		    			   ] // service operation parameter submappings
		    			  		   }
		    	         		}
		    			  	] // service operation submappings
		    			}		
		        	}
  			    ] // conditional submappings
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
            "execute":{
                "funct":mapAccessConstraints
            }
        },
        {
            "srcXpath":"//gmd:identificationInfo//gmd:resourceConstraints/*/gmd:useLimitation/gco:CharacterString",
            "targetNode":"/igc/data-sources/data-source/additional-information",
            "newNodeName":"use-constraint",
            "subMappings":{
                "mappings": [
                    {
                        "srcXpath":".",
                        "defaultValue":"no conditions apply",
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
							"params":[1320, 123]
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
			  			"srcXpath":"gmd:specification/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date/gco:Date[not(../gco:DateTime)] | gmd:specification/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date/gco:DateTime",
			  			"targetNode":"conformity-publication-date",
			  			"transform":{
							"funct":transformDateIso8601ToIndex
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
                        "srcXpath":"gmd:pass/@gco:nilReason",
                        "targetNode":"conformity-degree",
                        "targetAttribute":"id",
                        "transform":{
                            "funct":transformToStaticValue,
                            "params":["3"]
                        }
                    },
	  				{
			  			"srcXpath":"gmd:pass/gco:Boolean",
			  			"targetNode":"conformity-degree",
			  			"transform":{
							"funct":transformGeneric,
							"params":[{"true":"konform", "false":"nicht konform"}, false]
						}
			  		},
                    {
                        "srcXpath":"gmd:pass/@gco:nilReason",
                        "targetNode":"conformity-degree",
                        "transform":{
                            "funct":transformToStaticValue,
                            "params":["nicht evaluiert"]
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
            "execute":{
                "funct":mapReferenceSystemInfo
            }
        },
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:EX_Extent/gmd:verticalElement/gmd:EX_VerticalExtent/gmd:minimumValue/gco:Real",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain/vertical-extent/vertical-extent-minimum",
  			"transform":{
				"funct":transformNumberStrToIGCNumber
			}
  		},
  		{	
  			"srcXpath":"//gmd:identificationInfo//gmd:EX_Extent/gmd:verticalElement/gmd:EX_VerticalExtent/gmd:maximumValue/gco:Real",
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
				"params":[102, 123, "Could not map vertical-extent unit:"]
			}						    					
  		},
        {
            "execute":{
                "funct":mapVerticalExtentVdatum
            }
        },
  		{
  			"srcXpath":"//gmd:identificationInfo//gmd:EX_Extent/gmd:geographicElement",
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
  		{
  			"srcXpath":"//gmd:identificationInfo//gmd:descriptiveKeywords/gmd:MD_Keywords[gmd:type/gmd:MD_KeywordTypeCode/@codeListValue='place']/gmd:keyword/gco:CharacterString",
  			"targetNode":"/igc/data-sources/data-source/spatial-domain",
  			"newNodeName":"geo-location",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":".",
			  			"targetNode":"uncontrolled-location/location-name"
			  		},
	  				{
			  			"defaultValue":"-1",
			  			"targetNode":"uncontrolled-location/location-name",
			  			"targetAttribute":"id"
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
  			"srcXpath":"//gmd:identificationInfo//gmd:status/gmd:MD_ProgressCode/@codeListValue",
  			"targetNode":"/igc/data-sources/data-source/temporal-domain/time-status",
  			"targetAttribute":"iso-code",
  			"transform":{
				"funct":transformISOToIgcDomainId,
				"params":[523, "Could not map time-status to ISO code: "]
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
			  			"srcXpath":"gmd:date/gco:Date[not(../gco:DateTime)] | gmd:date/gco:DateTime",
			  			"targetNode":"dataset-reference-date",
			  			"transform":{
							"funct":transformDateIso8601ToIndex
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
			  			"srcXpath":".",
			  			"targetNode":"",
			  			"targetAttribute":"id",
			  			"transform":{
							"funct":transformToIgcDomainId,
							"params":[6100, 123, "Could not map INSPIRE theme:"]
						}
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
            "execute":{
                "funct":mapUncontrolledTerms
            }
        },

  		// ****************************************************
  		//
  		// /igc/data-sources/data-source/available-linkage
  		//
  		// ****************************************************

        {   
            "execute":{
                "funct":mapDistributionLinkages
            }
        },
  		{
  			"srcXpath":"//gmd:identificationInfo//gmd:graphicOverview/gmd:MD_BrowseGraphic[gmd:fileName/gco:CharacterString != '']",
  			"targetNode":"/igc/data-sources/data-source",
  			"newNodeName":"available-linkage",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"srcXpath":"gmd:fileDescription/gco:CharacterString",
			  			"defaultValue":"grafische Darstellung",
			  			"targetNode":"linkage-name"
			  		},
	  				{
			  			"srcXpath":"gmd:fileName/gco:CharacterString",
			  			"targetNode":"linkage-url"
			  		},
	  				{
			  			"defaultValue":"1",
			  			"targetNode":"linkage-url-type"
			  		},
	  				{
			  			"defaultValue":"-1",
			  			"targetNode":"linkage-reference",
			  			"targetAttribute":"id"
			  		}
			  	]
			}
  		},
  		{
  			"srcXpath":"//gmd:identificationInfo//srv:operatesOn",
  			"targetNode":"/igc/data-sources/data-source",
  			"newNodeName":"available-linkage",
  			"subMappings":{
  				"mappings": [
	  				{
			  			"defaultValue":"operates on",
			  			"targetNode":"linkage-name"
			  		},
	  				{
			  			"srcXpath":"./@xlink:href",
			  			"defaultValue":"invalid link",
			  			"targetNode":"linkage-url"
			  		},
	  				{
			  			"defaultValue":"1",
			  			"targetNode":"linkage-url-type"
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
  			"execute":{
				"funct":mapAddresses
			}
  		}
	]};

if (log.isDebugEnabled()) {
	log.debug("mapping CSW 2.0.2 AP ISO 1.0 document " + protocolHandler.getCurrentFilename() + " to IGC import document.");
}
protocol(INFO, "Start transformation of: " + protocolHandler.getCurrentFilename());
protocol(INFO, "-------------------------------------------------");
protocol(INFO, "\n");


log.debug("validate source");
validateSource(source);


var uuid = XPathUtils.getString(source, "//gmd:fileIdentifier/gco:CharacterString");
if (hasValue(uuid)) {
	protocol(INFO, "fileIdentifier: " + uuid);
}

var title = XPathUtils.getString(source, "//gmd:identificationInfo//gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString");
if (hasValue(title)) {
	protocol(INFO, "title: " + uuid);
}

var storedValues = new Object();


log.debug("map to target");
mapToTarget(mappingDescription, source, target.getDocumentElement());

protocol(INFO, "\n\n");


function mapToTarget(mapping, source, target) {
		
		// iterate over all mapping descriptions
		for (var i in mapping.mappings) {
			var m = mapping.mappings[i];
			// check for conditional mapping
			if (m.conditional) {
				if (m.conditional.storedValue) {
					log.debug("found mapping with stored value conditional: " + m.conditional.storedValue.name + " ? " + storedValues[m.conditional.storedValue.name] + " == " + m.conditional.storedValue.value);
					if (storedValues[m.conditional.storedValue.name] != m.conditional.storedValue.value) {
						log.debug("Skip mapping because: " + m.conditional.storedValue.name + "!=" + m.conditional.storedValue.value);
						continue;
					} else {
						log.debug("Execute mapping because: " + m.conditional.storedValue.name + "==" + m.conditional.storedValue.value);
					}
				}
			}
			
			// check for execution (special function)
			if (hasValue(m.execute)) {
				log.debug("Execute function: " + m.execute.funct.name + "...")
				var args = new Array(source, target);
				if (hasValue(m.execute.params)) {
					args = args.concat(m.execute.params);
				}
				call_f(m.execute.funct, args)
			} else if (m.subMappings) {
				if (m.srcXpath) {
					// iterate over all xpath results
					var sourceNodeList = XPathUtils.getNodeList(source, m.srcXpath);
					if (sourceNodeList) {
						log.debug("found sub mapping sources: " + m.srcXpath + "; count: " + sourceNodeList.getLength())
						for (var j=0; j<sourceNodeList.getLength(); j++ ) {
							log.debug("handle sub mapping: " + sourceNodeList.item(j))
							var node = XPathUtils.createElementFromXPath(target, m.targetNode);
							if (m.newNodeName) {
								node = node.appendChild(node.getOwnerDocument().createElement(m.newNodeName));
							}
							mapToTarget(m.subMappings, sourceNodeList.item(j), node);
						}
					} else {
						log.debug("found sub mapping sources: " + m.srcXpath + "; count: 0")
					}
				}	
			} else {
				if (m.srcXpath) {
					log.debug("Working on " + m.targetNode + " with xpath:'" + m.srcXpath + "'")
					// iterate over all xpath results
					var sourceNodeList = XPathUtils.getNodeList(source, m.srcXpath);
					var nodeText = "";
					if (sourceNodeList && sourceNodeList.getLength() > 0) {
						for (var j=0; j<sourceNodeList.getLength(); j++ ) {
							var value = sourceNodeList.item(j).getTextContent()
							log.debug("Found value: '" + value + "' hasValue:" + hasValue(value));
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
								log.debug("typeof m.defaultValue:" + typeof m.defaultValue);
								if (typeof m.defaultValue == "function" ) {
									log.debug("Call function with value:" +source);
									var args = new Array(source);
									value = call_f(m.defaultValue,args);
								} else {
									value = m.defaultValue;
								}
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
								
								if (m.storeValue) {
									log.debug("stored '" + value + "' as '" + m.storeValue + "' in store:" + storedValues + ".");
									storedValues[""+m.storeValue] = value;
								}
								
								if (m.targetAttribute) {
									log.debug("adding '" + m.targetNode + "/@" + m.targetAttribute + "' = '" + nodeText + "'.");
									XMLUtils.createOrReplaceAttribute(node, m.targetAttribute, nodeText);
								} else {
									log.debug("adding '" + m.targetNode + "' = '" + nodeText + "'.");
									XMLUtils.createOrReplaceTextNode(node, nodeText);
								}
							}
						}
					} else {
						// nothing found in srcPath, check for default values
						if (m.defaultValue) {
							var value;
							if (typeof m.defaultValue == "function" ) {
								var args = new Array(source);
								value = call_f(m.defaultValue,args);
							} else {
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
								
								if (m.storeValue) {
									log.debug("stored '" + value + "' as '" + m.storeValue + "'.");
									storedValues[m.storeValue] = value;
								}
								
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
				// check if a default value was supplied
				// -> set a target node to a default value
				} else if (m.defaultValue || m.setStoredValue) {
					var nodeText = "";
					var value;
					log.debug("typeof m.defaultValue:" + typeof m.defaultValue)
					if (m.setStoredValue) {
						log.debug("get value '" + value + "' from stored value '" + m.setStoredValue + "'.");
						value = storedValues[m.setStoredValue];
					} else if (typeof m.defaultValue == "function" ) {
						var args = new Array(source);
						value = call_f(m.defaultValue,args);
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
					
					if (m.storeValue) {
						log.debug("stored '" + value + "' as '" + m.storeValue + "'.");
						storedValues[m.storeValue] = value;
					}
					
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
	var metadataNodes = XPathUtils.getNodeList(source, "//gmd:MD_Metadata");
	if (!hasValue(metadataNodes) || metadataNodes.getLength() == 0) {
		log.error("No valid ISO metadata record.");
		protocol(ERROR, "No valid ISO metadata record.\n\n");
		throw "No valid ISO metadata record.";
	}
	var hierarchyLevel = XPathUtils.getString(source, "//gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue");
	log.debug("Found hierarchyLevel: " + hierarchyLevel);
	if (hierarchyLevel == "application") {
		log.error("HierarchyLevel 'application' is not supported.");
		protocol(ERROR, "HierarchyLevel 'application' is not supported.\n\n");
		throw "HierarchyLevel 'application' is not supported.";
	}
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
			log.debug("adding '" + "/igc/data-sources/data-source/spatial-domain/coordinate-system" + "' = '" + coordinateSystem + "' to target document.");
			var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/spatial-domain/coordinate-system");
			XMLUtils.createOrReplaceTextNode(node, coordinateSystem);
            
            // get syslist id
			var coordinateSystemId = transformToIgcDomainId(coordinateSystem, 100, 123);
            if (hasValue(coordinateSystemId) && coordinateSystemId == -1) {
                // try to parse coordsystem name for correct mapping to syslist id
                var coordinateSystemLower = coordinateSystem.toLowerCase();
                var indx = coordinateSystemLower.indexOf("epsg:");
                if (indx != -1) {
                    var tmpCoordinateSystemId = coordinateSystemLower.substring(indx+5);
                    var tmpCoordinateSystem = transformToIgcDomainValue(tmpCoordinateSystemId, 100, 123);
                    if (hasValue(tmpCoordinateSystem)) {
                        XMLUtils.createOrReplaceTextNode(node, tmpCoordinateSystem);
                        coordinateSystemId = tmpCoordinateSystemId;
                    } else {
                        var myMsg = "Could not map coordinate-system to syslist 100, use as free entry: ";
                        log.warn(myMsg + coordinateSystem);
                        protocol(WARN, myMsg + coordinateSystem)
                    }
                }
            }
			if (hasValue(coordinateSystemId)) {
				XMLUtils.createOrReplaceAttribute(node, "id", coordinateSystemId);
			}
		}
	} else {
		protocol(INFO, "No referenceSystemInfo has been found!");
		log.debug("No referenceSystemInfo has been found!");
	}
}

function mapVerticalExtentVdatum(source, target) {
    var verticalDatums = XPathUtils.getNodeList(source, "//gmd:identificationInfo//gmd:EX_Extent/gmd:verticalElement/gmd:EX_VerticalExtent/gmd:verticalCRS/gml:VerticalCRS/gml:verticalDatum/gml:VerticalDatum");
    if (hasValue(verticalDatums)) {
        for (i=0; i<verticalDatums.getLength(); i++ ) {
            var datumIdentifier = XPathUtils.getString(verticalDatums.item(i), "gml:identifier");
            log.debug("adding '/igc/data-sources/data-source/spatial-domain/vertical-extent/vertical-extent-vdatum' = '" + datumIdentifier + "' to target document.");
            var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/spatial-domain/vertical-extent/vertical-extent-vdatum");
            XMLUtils.createOrReplaceTextNode(node, datumIdentifier);
            var datumId = transformToIgcDomainId(datumIdentifier, 101, 123, "Could not map VerticalDatum: ");
            if (hasValue(datumId)) {
                XMLUtils.createOrReplaceAttribute(node, "id", datumId);
            }
        }
    } else {
        protocol(INFO, "No VerticalDatum has been found!");
        log.debug("No VerticalDatum has been found!");
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
	var timePeriods = XPathUtils.getNodeList(source, "//gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod");
	log.debug("Found " + timePeriods.getLength() + " TimePeriod records.");
	if (hasValue(timePeriods) && timePeriods.getLength() > 0) {
		var beginPosition = XPathUtils.getString(timePeriods.item(0), "gml:beginPosition");
		var endPosition = XPathUtils.getString(timePeriods.item(0), "gml:endPosition");
		if (hasValue(beginPosition) && hasValue(endPosition)) {
			if (beginPosition.equals(endPosition)) {
				var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/beginning-date");
				XMLUtils.createOrReplaceTextNode(node, transformDateIso8601ToIndex(beginPosition));
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/ending-date");
				XMLUtils.createOrReplaceTextNode(node, transformDateIso8601ToIndex(endPosition));
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/time-type");
				XMLUtils.createOrReplaceTextNode(node, "am");
			} else {
				var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/beginning-date");
				XMLUtils.createOrReplaceTextNode(node, transformDateIso8601ToIndex(beginPosition));
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/ending-date");
				XMLUtils.createOrReplaceTextNode(node, transformDateIso8601ToIndex(endPosition));
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/time-type");
				XMLUtils.createOrReplaceTextNode(node, "von");
			}
		} else if (hasValue(beginPosition)) {
				var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/beginning-date");
				XMLUtils.createOrReplaceTextNode(node, transformDateIso8601ToIndex(beginPosition));
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/time-type");
				XMLUtils.createOrReplaceTextNode(node, "seit");
		} else if (hasValue(endPosition)) {
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/ending-date");
				XMLUtils.createOrReplaceTextNode(node, transformDateIso8601ToIndex(endPosition));
				node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/temporal-domain/time-type");
				XMLUtils.createOrReplaceTextNode(node, "bis");
		}
	}
}

function mapRSIdentifier(source, target)  {
	log.debug("Map RS_Identifier.");
	var rsIdentifiers = XPathUtils.getNodeList(source, "//gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:identifier/gmd:RS_Identifier");
	if (hasValue(rsIdentifiers) && rsIdentifiers.getLength() > 0) {
		log.debug("Found " + rsIdentifiers.getLength() + " RS_Identifier records.");
		var codeSpace = XPathUtils.getString(rsIdentifiers.item(0), "gmd:codeSpace/gco:CharacterString");
		var code = XPathUtils.getString(rsIdentifiers.item(0), "gmd:code/gco:CharacterString");
		if (hasValue(code)) {
			log.debug("Found RS_Identifier: " + code);
			var dataSourceID = "";
			if (hasValue(codeSpace)) {
				dataSourceID = codeSpace + "#" + code;
			} else {
				dataSourceID = code;
			}
			var node = XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/technical-domain/map/datasource-identificator");
			XMLUtils.createOrReplaceTextNode(node, dataSourceID);
		}
	}
}

function mapAccessConstraints(source, target) {
    var accConstraints = XPathUtils.getNodeList(source, "//gmd:identificationInfo//gmd:resourceConstraints/*/gmd:accessConstraints/gmd:MD_RestrictionCode");
    if (hasValue(accConstraints)) {
        for (i=0; i<accConstraints.getLength(); i++ ) {
            var isoValue = XPathUtils.getString(accConstraints.item(i), "./@codeListValue");
            if (isoValue != "otherRestrictions") {
                addAccessConstraint(isoValue, target);
            }
        }
    }

    accConstraints = XPathUtils.getNodeList(source, "//gmd:identificationInfo//gmd:resourceConstraints/*/gmd:otherConstraints/gco:CharacterString");
    if (hasValue(accConstraints)) {
        for (i=0; i<accConstraints.getLength(); i++ ) {
            var accConstraint = XPathUtils.getString(accConstraints.item(i), ".");
            addAccessConstraint(accConstraint, target);
        }
    }
}

function addAccessConstraint(accConstraint, target) {
    if (hasValue(accConstraint)) {
        log.debug("adding '" + "/igc/data-sources/data-source/additional-information/access-constraint/restriction" + "' = '" + accConstraint + "' to target document.");
        var node = XPathUtils.createElementFromXPathAsSibling(target, "/igc/data-sources/data-source/additional-information/access-constraint");
        node = XPathUtils.createElementFromXPath(node, "restriction");
        XMLUtils.createOrReplaceTextNode(node, accConstraint);
        var accConstraintId = transformToIgcDomainId(accConstraint, 6010, 123, "Could not map access-constraint, use as free entry: ");
        if (hasValue(accConstraintId)) {
            XMLUtils.createOrReplaceAttribute(node, "id", accConstraintId);                 
        }
    }
}

function mapAddresses(source, target) {

    var isoAddressNodes = XPathUtils.getNodeList(source, "//*/gmd:CI_ResponsibleParty[gmd:role/gmd:CI_RoleCode/@codeListValue!='']");
    if (hasValue(isoAddressNodes)) {
        var igcAdressNodes = XPathUtils.createElementFromXPath(target, "/igc/addresses");
    	var parentAddressList = [];
        for (i=0; i<isoAddressNodes.getLength(); i++ ) {
        	var isoAddressNode = isoAddressNodes.item(i);
        	var organisationName = XPathUtils.getString(isoAddressNode, "gmd:organisationName/gco:CharacterString");
        	var individualName = XPathUtils.getString(isoAddressNode, "gmd:individualName/gco:CharacterString");
        	var parentUuid;
        	if (hasValue(organisationName) && hasValue(individualName)) {
        		var parentUuid = createUUIDFromString(organisationName.toString());
        		if (!hasValue(parentAddressList[parentUuid])) {
	        		var igcAddressNode = XPathUtils.createElementFromXPathAsSibling(igcAdressNodes, "address");
	                log.info("Organization in individual address detected. Create new address for organization '" + organisationName + "' with uuid=" + parentUuid + ".")
	                XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "address-identifier"), parentUuid);
	                XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "modificator-identifier"), "xxx");
	                XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "responsible-identifier"), "xxx");
	                XMLUtils.createOrReplaceAttribute(XPathUtils.createElementFromXPath(igcAddressNode, "type-of-address"), "id", "0");
	                XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "organisation"), organisationName);
	                parentAddressList[parentUuid] = 1;
        		} else {
        			log.debug("Organization in individual address detected. Use existing address for organization '" + organisationName + "' with uuid=" + parentUuid + ".")        		
        		}
        	}
        	var uuid = createUUIDFromAddress(isoAddressNode);
    		var igcAddressNode = XPathUtils.createElementFromXPathAsSibling(igcAdressNodes, "address");
            XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "address-identifier"), uuid);
            XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "modificator-identifier"), "xxx");
            XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "responsible-identifier"), "xxx");
            if (hasValue(individualName)) {
            	XMLUtils.createOrReplaceAttribute(XPathUtils.createElementFromXPath(igcAddressNode, "type-of-address"), "id", "2");
            } else {
            	XMLUtils.createOrReplaceAttribute(XPathUtils.createElementFromXPath(igcAddressNode, "type-of-address"), "id", "0");
            }
            XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "organisation"), organisationName);
            XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "name"), individualName);
            var countryCode = UtilsCountryCodelist.getCodeFromShortcut3(XPathUtils.getString(isoAddressNode, "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country/gco:CharacterString"));
            if (hasValue(countryCode)) {
                XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "country"), XPathUtils.getString(isoAddressNode, "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country/gco:CharacterString"));
                XMLUtils.createOrReplaceAttribute(XPathUtils.createElementFromXPath(igcAddressNode, "country"), "id", countryCode);
            }
            XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "postal-code"), XPathUtils.getString(isoAddressNode, "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode/gco:CharacterString"));
            XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "street"), XPathUtils.getString(isoAddressNode, "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint/gco:CharacterString"));
            XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "city"), XPathUtils.getString(isoAddressNode, "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city/gco:CharacterString"));
            mapCommunicationData(isoAddressNode, igcAddressNode);
            XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "function"), XPathUtils.getString(isoAddressNode, "gmd:positionName/gco:CharacterString"));
            if (hasValue(parentUuid)) {
            	XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcAddressNode, "parent-address/address-identifier"), parentUuid);
            }

            // add related addresses
            var igcRelatedAddressNode = XPathUtils.createElementFromXPathAsSibling(target, "/igc/data-sources/data-source/related-address");
            var addressRoleId = transformISOToIgcDomainId(XPathUtils.getString(isoAddressNode, "gmd:role/gmd:CI_RoleCode/@codeListValue"), 505, "Could not transform ISO address role code to IGC id: ");
            if (!hasValue(addressRoleId)) {
            	addressRoleId = "-1";
            }
            XMLUtils.createOrReplaceAttribute(XPathUtils.createElementFromXPath(igcRelatedAddressNode, "type-of-relation"), "entry-id", addressRoleId);
            XMLUtils.createOrReplaceAttribute(XPathUtils.createElementFromXPath(igcRelatedAddressNode, "type-of-relation"), "list-id", "505");
            var addressRoleValue = transformISOToIgcDomainValue(XPathUtils.getString(isoAddressNode, "gmd:role/gmd:CI_RoleCode/@codeListValue"), 505, transformISOToIGCLanguageCode('de'), "Could not transform ISO address role code to IGC codelist value: ");
            XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcRelatedAddressNode, "type-of-relation"), addressRoleValue);            
            XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(igcRelatedAddressNode, "address-identifier"), uuid);            
            
        }
    }

}

function mapUncontrolledTerms(source, target) {
    var terms = XPathUtils.getNodeList(source, "//gmd:identificationInfo//gmd:descriptiveKeywords/gmd:MD_Keywords[not(gmd:type/gmd:MD_KeywordTypeCode/@codeListValue='place') and (not(gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString) or ( (gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString!='GEMET - INSPIRE themes, version 1.0') and (gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString!='Service Classification, version 1.0') ))]/gmd:keyword/gco:CharacterString");
    if (hasValue(terms)) {
        for (i=0; i<terms.getLength(); i++ ) {
            var term = XPathUtils.getString(terms.item(i), ".");
            if (hasValue(term)) {
        		// make sure that service classification codes are not included in uncontrolled keywords 
            	// transform to IGC domain id
        		var igcCode = null;
        		try {
        			igcCode = UtilsUDKCodeLists.getIgcIdFromIsoCodeListEntry(5200, term);
        		} catch (e) { /* can be ignored */}
                if (!hasValue(igcCode)) {
	        		// check "inspireidentifiziert" and add flag !
	                if (term.equals("inspireidentifiziert")) {
	                    log.debug("adding '/igc/data-sources/data-source/general/is-inspire-relevant' = 'Y' to target document.");
	                    XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(target, "/igc/data-sources/data-source/general/is-inspire-relevant"), "Y");
	                } else {
	                    log.debug("adding '/igc/data-sources/data-source/subject-terms/uncontrolled-term' = '" + term + "' to target document.");
	                    XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPathAsSibling(target, "/igc/data-sources/data-source/subject-terms/uncontrolled-term"), term);
	                }
                }
            }
        }
    }
}

function mapDistributionLinkages(source, target) {
    var linkages = XPathUtils.getNodeList(source, "//gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource[gmd:linkage/gmd:URL!='']");
    if (hasValue(linkages)) {
        for (i=0; i<linkages.getLength(); i++ ) {
            var linkage = {};
            linkage.name = XPathUtils.getString(linkages.item(i), "./gmd:name/gco:CharacterString");
            linkage.url = XPathUtils.getString(linkages.item(i), "./gmd:linkage/gmd:URL");
            linkage.urlType = "1";
            linkage.referenceId = "-1";
            linkage.description = XPathUtils.getString(linkages.item(i), "./gmd:description/gco:CharacterString");
            addAvailableLinkage(linkage, target);
        }
    }
}

function addAvailableLinkage(linkage, target) {
    if (hasValue(linkage) && hasValue(linkage.url) && hasValue(linkage.urlType)) {
        log.debug("adding '/igc/data-sources/data-source/available-linkage' -> 'linkage-url' = '" + linkage.url + "' to target document.");
        var linkageNode = XPathUtils.createElementFromXPathAsSibling(target, "/igc/data-sources/data-source/available-linkage");
        if (!hasValue(linkage.name)) {
            linkage.name = linkage.url;
        }
        XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(linkageNode, "linkage-name"), linkage.name);
        XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(linkageNode, "linkage-url"), linkage.url);
        XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(linkageNode, "linkage-url-type"), linkage.urlType);
        if (hasValue(linkage.referenceId)) {
            XMLUtils.createOrReplaceAttribute(XPathUtils.createElementFromXPath(linkageNode, "linkage-reference"), "id", linkage.referenceId);
            if (hasValue(linkage.referenceName)) {
                XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(linkageNode, "linkage-reference"), linkage.referenceName);
            }
        }
        if (hasValue(linkage.description)) {
            XMLUtils.createOrReplaceTextNode(XPathUtils.createElementFromXPath(linkageNode, "linkage-description"), linkage.description);
        }
    }
}

function mapServiceClassifications(source, target) {
    var terms = XPathUtils.getNodeList(source, "//gmd:identificationInfo//gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString");
    if (hasValue(terms)) {
        for (i=0; i<terms.getLength(); i++ ) {
            var term = XPathUtils.getString(terms.item(i), ".");
            if (hasValue(term)) {
        		// transform to IGC domain id
        		var igcCode = null;
        		try {
        			igcCode = UtilsUDKCodeLists.getIgcIdFromIsoCodeListEntry(5200, term);
        		} catch (e) {
        			if (log.isWarnEnabled()) {
        				log.warn("Error tranforming INSPIRE Service Classification code '" + term + "' with code list 5200 to IGC id. Does the codeList exist?");
        			}
        			protocol(WARN, "Error tranforming ISO code '" + term + "' with code list 5200 to IGC id. Does the codeList exist?")
        		}
        		if (hasValue(igcCode)) {
            		var node = XPathUtils.createElementFromXPathAsSibling(target, "/igc/data-sources/data-source/technical-domain/service/service-classification");
            		XMLUtils.createOrReplaceAttribute(node, "id", igcCode);
        		}
            }
        }
    }
}

function parseToInt(val) {
	return java.lang.Integer.parseInt(val);
}

function transformNumberStrToIGCNumber(val) {
	return UtilsString.transformNumberStrToIGCNumber(val);		
}


function transformToStaticValue(val, staticValue) {
    return staticValue;
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
		log.warn(logErrorOnNotFound + val);
		protocol(WARN, logErrorOnNotFound + val)
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
			if (log.isWarnEnabled()) {
				log.warn("Error tranforming code '" + val + "' with code list " + codeListId + " with language '" + UtilsLanguageCodelist.getShortcutFromCode(languageId) + "' to IGC id. Does the codeList exist?");
			}
			protocol(WARN, "Error tranforming code '" + val + "' with code list " + codeListId + " with language '" + UtilsLanguageCodelist.getShortcutFromCode(languageId) + "' to IGC id. Does the codeList exist?")
			if (logErrorOnNotFound) {
				log.warn(logErrorOnNotFound + val);
				protocol(WARN, logErrorOnNotFound + val)
			}
		}
		if (hasValue(idcCode)) {
			return idcCode;
		} else {
			if (log.isWarnEnabled()) {
				log.warn("Domain code '" + val + "' unknown in code list " + codeListId + " for language '" + UtilsLanguageCodelist.getShortcutFromCode(languageId) + "'.");
				protocol(WARN, "Domain code '" + val + "' unknown in code list " + codeListId + " for language '" + UtilsLanguageCodelist.getShortcutFromCode(languageId) + "'.")
			}
			if (logErrorOnNotFound) {
				log.warn(logErrorOnNotFound + val);
				protocol(WARN, logErrorOnNotFound + val)
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
			if (log.isWarnEnabled()) {
				log.warn("Error tranforming id '" + val + "' with code list " + codeListId + " with language '" + UtilsLanguageCodelist.getShortcutFromCode(languageId) + "'. Does the codeList exist?");
			}
			protocol(WARN, "Error tranforming id '" + val + "' with code list " + codeListId + " with language '" + UtilsLanguageCodelist.getShortcutFromCode(languageId) + "'. Does the codeList exist?")
			if (logErrorOnNotFound) {
				log.warn(logErrorOnNotFound + val);
				protocol(WARN, logErrorOnNotFound + val)
			}
		}
		if (hasValue(idcValue)) {
			return idcValue;
		} else {
			if (log.isWarnEnabled()) {
				log.warn("Domain id '" + val + "' unknown in code list " + codeListId + " for language '" + UtilsLanguageCodelist.getShortcutFromCode(languageId) + "'.");
				protocol(WARN, "Domain id '" + val + "' unknown in code list " + codeListId + " for language '" + UtilsLanguageCodelist.getShortcutFromCode(languageId) + "'.")
			}
			if (logErrorOnNotFound) {
				log.warn(logErrorOnNotFound + val);
				protocol(WARN, logErrorOnNotFound + val)
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
			if (log.isWarnEnabled()) {
				log.warn("Error tranforming ISO code '" + val + "' with code list " + codeListId + " to IGC id. Does the codeList exist?");
			}
			protocol(WARN, "Error tranforming ISO code '" + val + "' with code list " + codeListId + " to IGC id. Does the codeList exist?")
			if (logErrorOnNotFound) {
				log.warn(logErrorOnNotFound + val);
				protocol(WARN, logErrorOnNotFound + val)
			}
		}
		if (hasValue(idcCode)) {
			return idcCode;
		} else {
			if (log.isWarnEnabled()) {
				log.warn("ISO code '" + val + "' unknown in code list " + codeListId + ".");
				protocol(WARN, "ISO code '" + val + "' unknown in code list " + codeListId + ".")
			}
			if (logErrorOnNotFound) {
				log.warn(logErrorOnNotFound + val);
				protocol(WARN, logErrorOnNotFound + val)
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
			idcValue = UtilsUDKCodeLists.getCodeListEntryName(codeListId, parseToInt(idcCode), parseToInt(languageId));
		} catch (e) {
			if (log.isWarnEnabled()) {
				log.warn("Error tranforming ISO code '" + val + "' with code list " + codeListId + " to IGC value with language '" + UtilsLanguageCodelist.getShortcutFromCode(languageId) + "'. Does the codeList exist?"  + e.toString());
			}
			protocol(WARN, "Error tranforming ISO code '" + val + "' with code list " + codeListId + " to IGC value with language '" + UtilsLanguageCodelist.getShortcutFromCode(languageId) + "'. Does the codeList exist?")
			if (logErrorOnNotFound) {
				log.warn(logErrorOnNotFound + val);
				protocol(WARN, logErrorOnNotFound + val)
			}
		}
		if (hasValue(idcValue)) {
			return idcValue;
		} else {
			if (log.isWarnEnabled()) {
				log.warn("ISO code '" + val + "' unknown in code list " + codeListId + ".");
				protocol(WARN, "ISO code '" + val + "' unknown in code list " + codeListId + ".")
			}
			if (logErrorOnNotFound) {
				log.warn(logErrorOnNotFound + val);
				protocol(WARN, logErrorOnNotFound + val)
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

function transformDateIso8601ToIndex(isoFormat) {
	if (!UtilsCSWDate.isCSWDate(isoFormat)) {
		log.warn("ISO Date '" + isoFormat + "' could not be transformed. Date is not ISO 8601 conform. Leaving value unchanged.");
		protocol(WARN, "ISO Date '" + isoFormat + "' could not be transformed. Date is not ISO 8601 conform. Leaving value unchanged.");
		return isoFormat;
	}
	return UtilsCSWDate.mapDateFromIso8601ToIndex(isoFormat);
}



function getTypeOfAddress(source, target) {
	var organisationName = XPathUtils.getString(source, "gmd:organisationName/gco:CharacterString");
	var individualName = XPathUtils.getString(source, "gmd:individualName/gco:CharacterString");
	var node = XPathUtils.createElementFromXPath(target, "type-of-address");
	if (hasValue(individualName)) {
		XMLUtils.createOrReplaceAttribute(node, "id", "2");
	} else if (hasValue(organisationName)) {
		XMLUtils.createOrReplaceAttribute(node, "id", "0");
	} else {
		XMLUtils.createOrReplaceAttribute(node, "id", "2");
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






function createUUIDFromAddress(source) {
	log.debug("create UUID from address node: " + source);
	var isoUuid = XPathUtils.getString(source, "./@uuid");
	var organisationName = XPathUtils.getString(source, "gmd:organisationName/gco:CharacterString");
	var individualName = XPathUtils.getString(source, "gmd:individualName/gco:CharacterString");
	var email = XPathUtils.getString(source, "gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString");
	
	var idString = "";
	if (hasValue(organisationName)) {
		idString += organisationName;
	}
	if (hasValue(individualName)) {
		idString += individualName;
	}
	if (hasValue(email)) {
		idString += email;
	}
	
	var uuid;
	if (hasValue(idString) && (hasValue(email) || (hasValue(organisationName) && hasValue(individualName)))) {
		uuid = createUUIDFromString(idString.toString());
	} else if (hasValue(isoUuid)) {
		uuid = isoUuid;
	} else {
		protocol(INFO, "Insufficient data for UUID creation (no 'email' or only one of 'individualName' or 'organisationName' has been set for this address: email='" + email + "', individualName='" + individualName + "', organisationName='" + organisationName + "'!)")
		protocol(INFO, "A new random UUID will be created!")
		log.info("Insufficient data for UUID creation (no 'email' or only one of 'individualName' or 'organisationName' has been set for this address: email='" + email + "', individualName='" + individualName + "', organisationName='" + organisationName + "'!)");
		log.info("A new random UUID will be created!");
		uuid = createUUID();
	}
	log.info("Created UUID from Address:" + uuid);
	
	return uuid;
}

function createUUIDFromString(str) {
	// replace multiple white spaces by ' '
	var uuid = java.util.UUID.nameUUIDFromBytes((new java.lang.String(str).replaceAll("\\s+", " ")).getBytes());
	var idcUuid = new java.lang.StringBuffer(uuid.toString().toUpperCase());
	while (idcUuid.length() < 36) {
		idcUuid.append("0");
	}
	uuid = idcUuid.toString();
	return uuid;
}


function createUUID() {
	var uuid = java.util.UUID.randomUUID();
	var idcUuid = new java.lang.StringBuffer(uuid.toString().toUpperCase());
	while (idcUuid.length() < 36) {
		idcUuid.append("0");
	}
	log.debug("Created UUID:" + idcUuid.toString());

	return idcUuid.toString();
}

function protocol(level, msg) {

	if (level==DEBUG){
		protocolHandler.addMessage(msg + "\n");
	} else if (level==INFO){
		protocolHandler.addMessage(msg + "\n");
	} else if (level==WARN){
		protocolHandler.addMessage("<span style=\"font-weight:bold;\">" + msg + "</span>\n");
	} else if (level==ERROR){
		protocolHandler.addMessage("<span style=\"color:red;font-weight:bold;\">" + msg + "</span>\n");
	}
}

