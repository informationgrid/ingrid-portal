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
package de.ingrid.mdek.dwr.services.sns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;

import de.ingrid.external.om.TreeTerm;
import de.ingrid.rdf.SNSServiceRDF;

@Qualifier("rdf")
public class RDFService extends SNSService {
    
    private final static Logger log = Logger.getLogger(RDFService.class);

    public List<SNSTopic> getRootTopics(String rootUrl, Locale locale) {
    	//Locale sessionLocale = MdekUtils.getLocaleFromSession();
    	log.debug("     !!!!!!!!!! thesaurusService.getHierarchyNextLevel() from null (toplevel), " + locale.getLanguage());
    	List<SNSTopic> resultList = new ArrayList<SNSTopic>(); 
    	
    	TreeTerm[] treeTerms = ((SNSServiceRDF)thesaurusService).getHierarchyTopLevel(rootUrl, locale);

    	TreeSet<TreeTerm> orderedTreeTerms = new TreeSet<TreeTerm>(new TermComparator(locale));
    	orderedTreeTerms.addAll(Arrays.asList(treeTerms));

    	for (TreeTerm treeTerm : orderedTreeTerms) {
    		// also contain children which will be removed in frontend
    		SNSTopic resultTopic = convertTreeTermToSNSTopic(treeTerm);
    		resultList.add(resultTopic);
    	}

    	return resultList;
    }
    
    /*@Override
    public List<SNSTopic> getSubTopics(String topicID, long depth, String direction) {
        Locale sessionLocale = MdekUtils.getLocaleFromSession();
        log.debug("     !!!!!!!!!! thesaurusService.getHierarchyNextLevel() from "
            +topicID+", "+depth+", "+direction+", " + sessionLocale.getLanguage());
        List<SNSTopic> resultList = new ArrayList<SNSTopic>(); 
        
        //TreeTerm[] treeTerms = thesaurusService.getHierarchyNextLevel(topicID, sessionLocale);
        
        SNSServiceRDF rdfService = new SNSServiceRDF();
        rdfService.init();
        TreeTerm[] treeTerms = rdfService.getHierarchyNextLevel(topicID, sessionLocale);

        TreeSet<TreeTerm> orderedTreeTerms = new TreeSet<TreeTerm>(new TermComparator());
        orderedTreeTerms.addAll(Arrays.asList(treeTerms));

        for (TreeTerm treeTerm : orderedTreeTerms) {
            // ADDING OF CHILDREN !!!!!!!!! For right behavior in JSP !
            SNSTopic resultTopic = convertTreeTermToSNSTopic(treeTerm);
            resultList.add(resultTopic);
        }

        return resultList;
    }*/
}
