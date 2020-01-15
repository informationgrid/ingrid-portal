/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class RDFReader {
    
    private static final Logger log = Logger.getLogger(RDFReader.class);
    
    public Model fetchTerm(String uri, String params) {
    	// create an empty model
        Model model = ModelFactory.createDefaultModel();

        // read the RDF/XML file
        model.read(uri + ".rdf" + params);

        // write it to standard out
        if (log.isDebugEnabled()) {
            model.write(System.out);
        }
        
        return model;
    }    	
    
    public Model fetchTerm(String uri) {
        return fetchTerm(uri, "");
    }
    
    public List<ModelWrapper> fetchAllChildren(String uri) {
        List<ModelWrapper> children = new ArrayList<>();
        
        // get parent model to find children relations
        Model parent = fetchTerm(uri);
        
        // get Iterator for all children within parent model
        NodeIterator childrenIt = getChildrenIterator(parent);
        
        while (childrenIt.hasNext()) {
            RDFNode node = childrenIt.next();  // get next statement
            children.add(new ModelWrapper(fetchTerm(node.toString()), node.toString()));
        }
        
        return children;
    }
    
    private NodeIterator getChildrenIterator(Model parent) {
    	// get Iterator for all children within parent model
        NodeIterator childrenIt = RDFUtils.getChildren(parent);

        // if no 'narrower' children then try to get members
        if (!childrenIt.hasNext()) childrenIt = RDFUtils.getMembers(parent);
        
        // if no 'member'children then try to get hasTopConcept
        if (!childrenIt.hasNext()) childrenIt = RDFUtils.getTopConcepts(parent);
        
        return childrenIt;
    }
    
    public ModelWrapper fetchHierarchy(String uri) {
    	URL url = null;
    	int pos = uri.lastIndexOf( '/' );
    	try {
			url = new URL(uri);
		} catch (MalformedURLException e) {
			log.error("Error on fetchHierarchy.", e);
			return null;
		}
    	
    	String params = "?dir=down&depth=2";
    	String doc = url.getPath().substring(url.getPath().lastIndexOf('/'));
        Model hierarchy = fetchTerm(uri.substring(0, pos) + "/de/hierarchy" + doc, params);
		return new ModelWrapper(hierarchy, uri.substring(0, pos) + doc);
    }

    public Model findTerm(String rootUrl, String queryTerm) {
        String searchQuery = buildSearchQuery(rootUrl, queryTerm);
        
        Model model = ModelFactory.createDefaultModel();
        
        // fetch the search results
        model.read(searchQuery);

        // write it to standard out
        model.write(System.out);
        return model;
    }

    private String buildSearchQuery(String rootUrl, String queryTerm) {
        return rootUrl + "search.rdf?utf8=%E2%9C%93&qt=begins_with&for=all&c=&l[]=de&q=" + queryTerm;
    }

	public Model findRootTerms() {
		//http://data.uba.de/umt/de/search.rdf?c=&amp;for=concept&amp;l%5B%5D=de&amp;page=1&amp;q=%5B&amp;qt=begins_with&amp;t=labeling-skosxl-base#
		return null;
	}

	public List<ModelWrapper> fetchHierarchiesFromRoot(String uri) {
		List<ModelWrapper> hierarchies = new ArrayList<>();
		try {
			// try to get the hierarchy from this uri
			hierarchies.add(fetchHierarchy(uri));
		} catch (Exception e) {
			// try to get hierarchy of children
			Model parent = fetchTerm(uri);
	    	NodeIterator childrenIt = getChildrenIterator(parent);
	        while (childrenIt.hasNext()) {
	        	RDFNode node = childrenIt.next();  // get next statement
	        	hierarchies.add(fetchHierarchy(node.toString()));
	        }
		}
        
    	return hierarchies;
	}
}
