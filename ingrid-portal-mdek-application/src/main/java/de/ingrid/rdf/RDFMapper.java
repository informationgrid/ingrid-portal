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

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.ingrid.external.om.Term;
import de.ingrid.external.om.TreeTerm;
import de.ingrid.external.om.impl.TreeTermImpl;

public class RDFMapper {
    
    public List<TreeTerm> mapToTreeTerms(List<ModelWrapper> models) {
        List<TreeTerm> resultList = new ArrayList<>();
        
        for (ModelWrapper model : models) {
            TreeTerm child = mapTreeTerm(model.getResource());
            resultList.add(child);
        }
        
        return resultList;
    }
    
    public List<TreeTerm> mapSearchToTreeTerms(Model model) {
        List<TreeTerm> resultList = new ArrayList<>();
        
        ResIterator l = model.listSubjects();
		while (l.hasNext()) {
            TreeTerm child = mapTreeTermFromResource(l.next());
            resultList.add(child);
        }
        
        return resultList;
    }
    
    private TreeTerm mapTreeTermFromResource(Resource res) {
    	TreeTerm treeTerm = new TreeTermImpl();
        treeTerm.setId(RDFUtils.getId(res));
        treeTerm.setName(RDFUtils.getName(res));
        treeTerm.setType(Term.TermType.DESCRIPTOR);
		return treeTerm;
	}

    // always add a parent for RDF terms
	private void addParentInfo(TreeTerm treeTerm) {
            TreeTerm parentTreeTerm = new TreeTermImpl();
            treeTerm.addParent(parentTreeTerm);
        
    }

    public TreeTerm mapTreeTerm(Resource resource) {
        TreeTerm treeTerm = new TreeTermImpl();
        treeTerm.setId(resource.toString());
        treeTerm.setName(RDFUtils.getName(resource));
        treeTerm.setType(Term.TermType.DESCRIPTOR);
        addParentInfo(treeTerm);
        
        // check for children (simple check)
        // needed to presentation ("plus"-sign in front of node)
        NodeIterator it = RDFUtils.getChildren(resource.getModel());
        while (it.hasNext()) {
            RDFNode node = it.next();
            TreeTerm child = new TreeTermImpl();
            child.setId(node.toString());
            treeTerm.addChild(child);
        }
        
        return treeTerm;
    }


	public List<TreeTerm> mapHierarchyToTreeTerms(ModelWrapper parent) {
		List<TreeTerm> resultList = new ArrayList<>();
		NodeIterator children = RDFUtils.getChildren(parent.getModel());
		while (children.hasNext()) {
			TreeTerm treeTerm = new TreeTermImpl();
			RDFNode child = children.next();
			String identifier = child.toString();
			treeTerm.setId(identifier);
			treeTerm.setName(RDFUtils.getName(child.asResource()));
			treeTerm.setType(Term.TermType.DESCRIPTOR);
			
			// needed to determine that it's not a top-term!
			treeTerm.addParent(new TreeTermImpl());
			
			resultList.add(treeTerm);
			
			// check for children (simple check)
	        // needed to presentation ("plus"-sign in front of node)
	        StmtIterator it = RDFUtils.getChildren(child.asResource());
	        while (it.hasNext()) {
	            Statement node = it.next();
	            TreeTerm subChild = new TreeTermImpl();
	            subChild.setId(node.toString());
	            treeTerm.addChild(subChild);
	        }
		}
		return resultList;
	}
	
	public List<TreeTerm> mapHierarchyRootToTreeTerms(List<ModelWrapper> parents) {
		List<TreeTerm> resultList = new ArrayList<>();
		for (ModelWrapper parent : parents) {
				
			ResIterator children = RDFUtils.getTopConceptsOf(parent.getModel());
			while (children.hasNext()) {
				TreeTerm treeTerm = new TreeTermImpl();
				RDFNode child = children.next();
				String identifier = child.toString();
				treeTerm.setId(identifier);
				treeTerm.setName(RDFUtils.getName(child.asResource()));
				treeTerm.setType(Term.TermType.DESCRIPTOR);
				
				// needed to determine that it's not a top-term!
				treeTerm.addParent(new TreeTermImpl());
				
				resultList.add(treeTerm);
				
				// check for children (simple check)
				// needed to presentation ("plus"-sign in front of node)
				StmtIterator it = RDFUtils.getChildren(child.asResource());
				while (it.hasNext()) {
					Statement node = it.next();
					TreeTerm subChild = new TreeTermImpl();
					subChild.setId(node.toString());
					treeTerm.addChild(subChild);
				}
			}
		}
		return resultList;
	}
    
}
