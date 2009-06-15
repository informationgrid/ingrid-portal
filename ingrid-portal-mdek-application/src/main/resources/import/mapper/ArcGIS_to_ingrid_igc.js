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
			"node":"/igc/data-sources/data-source/general/title",
  			"xpath":"/metadata/dataIdInfo/idCitation/resTitle"
  		},
  		{	
  			"node":"/igc/data-sources/data-source/general/original-control-identifier",
  			"xpath":"/metadata/Esri/MetaID"
		}]};

mapToTarget(mappingDescription, source, target);
  	
function mapToTarget(mapping, source, target) {
	
		// iterate over all mapping descriptions
		for (var i in mapping.mappings) {
			var m = mapping.mappings[i];
			log.debug("Working on " + m.node + " with xpath:'" + m.xpath + "'")
			// iterate over all xpath results
			var sourceNodeList = XPathUtils.getNodeList(source, m.xpath);
			for (j=0; j<sourceNodeList.getLength(); j++ ) {
				var value = sourceNodeList.item(j).getTextContent()
				// check for transformation
				if (hasValue(m.transform)) {
					var args = new Array(value);
					if (hasValue(m.transform.params)) {
						args = args.concat(m.transform.params);
					}
					value = call_f(m.transform.funct,args);
				}
				if (hasValue(value)) {
					node = XPathUtils.createElementFromXPath(target.getDocumentElement(), m.node);
					XMLUtils.createOrReplaceTextNode(node, value);
					log.debug("adding '" + m.node + "' = '" + value + "'.");
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

