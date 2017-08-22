/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.shared.DoesNotExistException;

import de.ingrid.external.ThesaurusService;
import de.ingrid.external.om.RelatedTerm;
import de.ingrid.external.om.Term;
import de.ingrid.external.om.TreeTerm;

public class SNSServiceRDF implements ThesaurusService {
    
    private final static Logger log = Logger.getLogger(SNSServiceRDF.class);
    
    private RDFReader rdfReader;
    private RDFMapper rdfMapper;

//    private String thesaurusRootURI;
    
    public void init() {
        rdfReader = new RDFReader();
        rdfMapper = new RDFMapper();
    }

    @Override
    public Term[] findTermsFromQueryTerm(String queryTerm, MatchingType matching, boolean addDiscriptors, Locale language) {
        log.warn( "this method is not supported!" );
        return null;
    }
    
    @Override
    public Term[] findTermsFromQueryTerm(String url, String queryTerm, MatchingType matching, boolean addDescriptors, Locale locale) {
        log.warn( "this method is not supported!" );
        return null;
    }

    public TreeTerm[] getHierarchyTopLevel(String rootURI, Locale lang) {
    	List<ModelWrapper> termModels = null;
    	List<TreeTerm> resultList = null;
    	String strippedUri = rootURI;
    	
    	if (rootURI.endsWith(".rdf"))
    		strippedUri = rootURI.substring(0, rootURI.length()-4);
    	
		try {
			termModels = rdfReader.fetchHierarchiesFromRoot(strippedUri);
		} catch (DoesNotExistException e) {
			return getHierarchyNextLevel(strippedUri, lang);
		}
        resultList = rdfMapper.mapHierarchyRootToTreeTerms(termModels);
		
    	return resultList.toArray(new TreeTerm[resultList.size()]);
    	
    }
    
    @Override
    public TreeTerm[] getHierarchyNextLevel(String termURI, Locale lang) {
    	List<ModelWrapper> termModels = null;
    	List<TreeTerm> resultList = null;
    	// different handling if root nodes shall be fetched
   		// check first if description names of children are contained
    	// within the same RDF-file
    	
    	// or if we have to fetch each child to get the name information (takes long!)
    	/*if (resultList == null) {
	        termModels = rdfReader.fetchAllChildren(termURI);
	        resultList = rdfMapper.mapToTreeTerms(termModels);
    	}*/
    	
    	try {
    		ModelWrapper hierarchy = rdfReader.fetchHierarchy(termURI);
    		resultList = rdfMapper.mapHierarchyToTreeTerms(hierarchy);
    	} catch (Exception e) {
    		// if hierarchy functionality does not exist then try to fetch children individually
    		termModels = rdfReader.fetchAllChildren(termURI);
    		resultList = rdfMapper.mapToTreeTerms(termModels);
    	}
        
    	
        return resultList.toArray(new TreeTerm[resultList.size()]);
    }
    
    @Override
    public TreeTerm[] getHierarchyNextLevel(String url, String termId, Locale locale) {
        log.warn( "this method is not supported!" );
        return null;
    }

    @Override
    public TreeTerm getHierarchyPathToTop(String termId, Locale locale) {
        log.warn( "this method is not supported!" );
        return null;
    }
    
    @Override
    public TreeTerm getHierarchyPathToTop(String url, String termId, Locale locale) {
        log.warn( "this method is not supported!" );
        return null;
    }

    @Override
    public RelatedTerm[] getRelatedTermsFromTerm(String arg0, Locale arg1) {
        log.warn( "this method is not supported!" );
        return null;
    }

    @Override
    public Term[] getSimilarTermsFromNames(String[] arg0, boolean arg1, Locale arg2) {
        log.warn( "this method is not supported!" );
        return null;
    }

    @Override
    public Term getTerm(String arg0, Locale arg1) {
        log.warn( "this method is not supported!" );
        return null;
    }

    @Override
    public Term[] getTermsFromText(String arg0, int arg1, boolean arg2, Locale arg3) {
        log.warn( "this method is not supported!" );
        return null;
    }

    public void setThesaurusRootURI(String thesaurusRootURI) {
        //this.thesaurusRootURI = thesaurusRootURI;
    }

}
