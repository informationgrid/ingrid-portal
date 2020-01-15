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
    
    private static final Logger log = Logger.getLogger(RDFService.class);

    @Override
    public List<SNSTopic> getRootTopics(String rootUrl, Locale locale) {
    	log.debug("     !!!!!!!!!! thesaurusService.getHierarchyNextLevel() from null (toplevel), " + locale.getLanguage());
    	List<SNSTopic> resultList = new ArrayList<>(); 
    	
    	TreeTerm[] treeTerms = ((SNSServiceRDF)thesaurusService).getHierarchyTopLevel(rootUrl, locale);

    	TreeSet<TreeTerm> orderedTreeTerms = new TreeSet<>(new TermComparator(locale));
    	orderedTreeTerms.addAll(Arrays.asList(treeTerms));

    	for (TreeTerm treeTerm : orderedTreeTerms) {
    		// also contain children which will be removed in frontend
    		SNSTopic resultTopic = convertTreeTermToSNSTopic(treeTerm);
    		resultList.add(resultTopic);
    	}

    	return resultList;
    }
}
