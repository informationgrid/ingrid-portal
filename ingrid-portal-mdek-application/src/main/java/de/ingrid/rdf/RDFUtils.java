package de.ingrid.rdf;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

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
        if (it.hasNext()) {
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
        Statement stmt = res.getProperty(prop);
        return stmt != null ? stmt.getObject() : null;
    }
    
    private static NodeIterator getObjects(Model model, String namespace, String name) {
        String nsURI = model.getNsPrefixURI(namespace);
        Property prop = model.createProperty(nsURI + name);
        return model.listObjectsOfProperty(prop);
    }
    
    /*private static NodeIterator getObjects(Resource res, String namespace, String name) {
        String nsURI = res.getModel().getNsPrefixURI(namespace);
        Property prop = res.getModel().createProperty(nsURI + name);
        return res.getProperty(prop);
    }*/

}
