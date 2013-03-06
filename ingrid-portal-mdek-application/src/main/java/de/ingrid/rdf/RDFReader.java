package de.ingrid.rdf;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class RDFReader {
    
    private final static Logger log = Logger.getLogger(RDFReader.class);
    
    //private String root;

    /*public RDFReader(String thesaurusRootURI) {
        this.root = thesaurusRootURI;
    }*/

    public Model fetchTerm(String uri) {
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        //if (uri == null)
        //    uri = root;
        
        // read the RDF/XML file
        model.read(uri + ".rdf");

        // write it to standard out
        if (log.isDebugEnabled()) {
            model.write(System.out);
        }
        
        return model;
    }
    
    public List<Model> fetchAllChildren(String uri) {
        List<Model> children = new ArrayList<Model>();
        
        // get parent model to find children relations
        Model parent = fetchTerm(uri);
        
        // get Iterator for all children within parent model
        NodeIterator childrenIt = RDFUtils.getChildren(parent);
        
        while (childrenIt.hasNext()) {
            RDFNode node = childrenIt.next();  // get next statement
            children.add(fetchTerm(node.toString()));
        }
        
        return children;
    }
    
    public List<Model> fetchAllMembers(String uri) {
        List<Model> children = new ArrayList<Model>();
        
        // get parent model to find children relations
        Model parent = fetchTerm(uri);
        
        // get Iterator for all children within parent model
        NodeIterator childrenIt = RDFUtils.getMembers(parent);
        
        while (childrenIt.hasNext()) {
            RDFNode node = childrenIt.next();  // get next statement
            children.add(fetchTerm(node.toString()));
        }
        
        return children;
    }

    public Model findTerm(String rootUrl, String queryTerm) {
        String searchQuery = buildSearchQuery(rootUrl, queryTerm);
        
        Model model = ModelFactory.createDefaultModel();
        
        // fetch the search results
        model.read(searchQuery);

        // write it to standard out
        //if (log.isDebugEnabled()) {
            model.write(System.out);
        //}
        
        return model;
    }

    private String buildSearchQuery(String rootUrl, String queryTerm) {
        String searchQuery = rootUrl + "search.rdf?utf8=%E2%9C%93&qt=begins_with&for=all&c=&l[]=de&q=" + queryTerm;
        return searchQuery;
    }

	public Model findRootTerms(String rootURI) {
		//http://data.uba.de/umt/de/search.rdf?c=&amp;for=concept&amp;l%5B%5D=de&amp;page=1&amp;q=%5B&amp;qt=begins_with&amp;t=labeling-skosxl-base#
		return null;
	}
}
