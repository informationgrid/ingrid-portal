/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
package de.ingrid.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class RDFUtils {
    
    public static String getName(Model model) {
        RDFNode node = getObject(model, "skos", "prefLabel", "de");
        if (node != null) {
            return node.asNode().getLiteralValue().toString();
        }
        return null;
    }
    
    public static String getName(Resource res) {
		RDFNode node = getObject(res, "skos", "prefLabel", "de");
        if (node != null) {
            return node.asNode().getLiteralValue().toString();
        }
        return null;
	}
    
    public static String getId(Model model) {
        ResIterator rIt = model.listSubjects();
        String foundUri = null;
        while (rIt.hasNext()) {
            Resource r = rIt.next();
            if (r.getURI() != null) {
                foundUri = r.getURI();
                break;            
            }
        }
        return foundUri;
    }
    
    public static String getId(Resource res) {
		RDFNode node = getObject(res, "sdc", "link");
        if (node != null) {
            return node.toString();
        }
        return null;

	}
    
    public static NodeIterator getChildren(Model model) {
        return getObjects(model, "skos", "narrower");
    }
    
    public static StmtIterator getChildren(Resource res) {
        return getObjects(res, "skos", "narrower");
    }
    
    public static NodeIterator getMembers(Model model) {
        return getObjects(model, "skos", "member");
    }
    
	public static NodeIterator getTopConcepts(Model model) {
		return getObjects(model, "skos", "hasTopConcept");
	}
	public static ResIterator getTopConceptsOf(Model model) {
		return getResources(model, "skos", "topConceptOf");
	}
    
    public static RDFNode getParent(Model model) {
        return getObject(model, "skos", "broader");
    }
    
    private static RDFNode getObject(Model model, String namespace, String name) {
        NodeIterator it = getObjects(model, namespace, name);
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
    
    private static RDFNode getObject(Resource res, String namespace, String name) {
    	String nsURI = res.getModel().getNsPrefixURI(namespace);
        Property prop = res.getModel().createProperty(nsURI + name);
        Statement stmt = res.getProperty(prop);
        return stmt != null ? stmt.getObject() : null;
    }
    
    private static RDFNode getObject(Model model, String namespace, String name, String lang) {
        NodeIterator it = getObjects(model, namespace, name);
        while (it.hasNext()) {
            RDFNode node = it.next();
            if (lang.equals(node.asLiteral().getLanguage())) {
                return node;
            }
        }
        return null;
    }
    
    private static RDFNode getObject(Resource res, String namespace, String name, String lang) {
    	String nsURI = res.getModel().getNsPrefixURI(namespace);
        Property prop = res.getModel().createProperty(nsURI + name);
        StmtIterator stmts = res.listProperties(prop);
        while (stmts.hasNext()) {
        	Statement stmt = stmts.next();
        	if (stmt.getLanguage().equals(lang)) {
        		return stmt.getObject();
        	}
        }
        return null;
    }
    
    private static NodeIterator getObjects(Model model, String namespace, String name) {
        String nsURI = model.getNsPrefixURI(namespace);
        Property prop = model.createProperty(nsURI + name);
        return model.listObjectsOfProperty(prop);
    }

    private static ResIterator getResources(Model model, String namespace, String name) {
    	String nsURI = model.getNsPrefixURI(namespace);
    	Property prop = model.createProperty(nsURI + name);
    	return model.listResourcesWithProperty(prop);
    }

    private static StmtIterator getObjects(Resource res, String namespace, String name) {
        String nsURI = res.getModel().getNsPrefixURI(namespace);
        Property prop = res.getModel().createProperty(nsURI + name);
        return res.listProperties(prop);
    }

}
