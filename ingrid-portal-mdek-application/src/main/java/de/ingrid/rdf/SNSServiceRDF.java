package de.ingrid.rdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.hp.hpl.jena.rdf.model.Model;

import de.ingrid.external.ThesaurusService;
import de.ingrid.external.om.RelatedTerm;
import de.ingrid.external.om.Term;
import de.ingrid.external.om.TreeTerm;

public class SNSServiceRDF implements ThesaurusService {
    
    private RDFReader rdfReader;
    private RDFMapper rdfMapper;

//    private String thesaurusRootURI;
    
    public void init() {
        rdfReader = new RDFReader();
        rdfMapper = new RDFMapper();
    }

    @Override
    public Term[] findTermsFromQueryTerm(String queryTerm, MatchingType matching, boolean addDiscriptors, Locale language) {
        
        //Model model = rdfReader.findTerm(queryTerm);
        
        
        
        return null;
    }

    public TreeTerm[] getHierarchyTopLevel(String rootURI, Locale lang) {
    	List<TreeTerm> resultList = null;
    	if (rootURI.endsWith(".rdf")) {
    		// only return this document which is used to get its children later on
    		
//    		Model m = rdfReader.fetchTerm(rootURI.substring(0, rootURI.length()-4));
//    		List<Model> modelList = new ArrayList<Model>();
//    		modelList.add(m);
//    		
//    		resultList = rdfMapper.mapToTreeTerms(modelList);
    		
    		
    		return getHierarchyNextLevel(rootURI.substring(0, rootURI.length()-4), lang);
    	} else {
    		Model termModel = rdfReader.findTerm(rootURI, "[");
    		resultList = rdfMapper.mapSearchToTreeTerms(termModel);
    		
    	}
    	return resultList.toArray(new TreeTerm[resultList.size()]);
    	
    }
    
    @Override
    public TreeTerm[] getHierarchyNextLevel(String termURI, Locale lang) {
    	List<Model> termModels = null;
    	List<TreeTerm> resultList = null;
    	// different handling if root nodes shall be fetched
   		
        termModels = rdfReader.fetchAllChildren(termURI);
        // if nothing was found try to find members instead
        // TODO: Check if this is correct!!!
        if (termModels.isEmpty())
        	termModels = rdfReader.fetchAllMembers(termURI);
        
        resultList = rdfMapper.mapToTreeTerms(termModels);
	        
        return resultList.toArray(new TreeTerm[resultList.size()]);
    }

    @Override
    public TreeTerm getHierarchyPathToTop(String arg0, Locale arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RelatedTerm[] getRelatedTermsFromTerm(String arg0, Locale arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Term[] getSimilarTermsFromNames(String[] arg0, boolean arg1, Locale arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Term getTerm(String arg0, Locale arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Term[] getTermsFromText(String arg0, int arg1, boolean arg2, Locale arg3) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setThesaurusRootURI(String thesaurusRootURI) {
        //this.thesaurusRootURI = thesaurusRootURI;
    }
}
