/**
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 * 
 * ArcCatalog import script. This script translates an input xml from
 * the export of an ArcCatalog ISO-Editor (Format "XML") into a IGC 
 * import format structure.
 * 
 * It uses a template that provides a basic IGC import format structure.
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
  			"concatEntriesWith":", ",
  			"prefix":"Folgende Sprachen werden im beschriebenen Datensatz verwendet: "
  		},
  		{	
  			"srcXpath":"/metadata/idinfo/descript/purpose",
  			"targetNode":"/igc/data-sources/data-source/additional-information/dataset-intentions"
  		}
	]};

mapToTarget(mappingDescription, source, target);
  	
function mapToTarget(mapping, source, target) {
	
		// iterate over all mapping descriptions
		for (var i in mapping.mappings) {
			var m = mapping.mappings[i];
			log.debug("Working on " + m.targetNode + " with xpath:'" + m.srcXpath + "'")
			// iterate over all xpath results
			var sourceNodeList = XPathUtils.getNodeList(source, m.srcXpath);
			for (j=0; j<sourceNodeList.getLength(); j++ ) {
				var value = sourceNodeList.item(j).getTextContent()
				if (hasValue(value) && !value.startsWith("REQUIRED:")) {
					var nodeText = "";
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
					
					XMLUtils.createOrReplaceTextNode(node, nodeText);
					log.debug("adding '" + m.targetNode + "' = '" + nodeText + "'.");
				}
			}
		}
		
		return target;
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

