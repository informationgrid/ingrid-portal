package de.ingrid.rdf;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import de.ingrid.external.om.Term;
import de.ingrid.external.om.TreeTerm;
import de.ingrid.external.om.impl.TreeTermImpl;

public class RDFMapper {
    
    public List<TreeTerm> mapToTreeTerms(List<Model> models) {
        List<TreeTerm> resultList = new ArrayList<TreeTerm>();
        
        for (Model model : models) {
            TreeTerm child = mapTreeTerm(model);
            resultList.add(child);
        }
        
        return resultList;
    }
    
    public List<TreeTerm> mapSearchToTreeTerms(Model model) {
        List<TreeTerm> resultList = new ArrayList<TreeTerm>();
        
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
        //addParentInfo(treeTerm, res);
		return treeTerm;
	}

	private void addParentInfo(TreeTerm treeTerm, Model model) {
        RDFNode parentNode = RDFUtils.getParent(model);
        if (parentNode != null) {
            TreeTerm parentTreeTerm = new TreeTermImpl();
            parentTreeTerm.setId(parentNode.toString());
            treeTerm.addParent(parentTreeTerm);
        }
        
    }

    private TreeTerm mapTreeTerm(Model model) {
        TreeTerm treeTerm = new TreeTermImpl();
        treeTerm.setId(RDFUtils.getId(model));
        treeTerm.setName(RDFUtils.getName(model));
        treeTerm.setType(Term.TermType.DESCRIPTOR);
        addParentInfo(treeTerm, model);
        
        // check for children (simple check)
        // needed to presentation ("plus"-sign in front of node)
        NodeIterator it = RDFUtils.getChildren(model);
        while (it.hasNext()) {
            RDFNode node = it.next();
            TreeTerm child = new TreeTermImpl();
            child.setId(node.toString());
            treeTerm.addChild(child);
        }
        
        return treeTerm;
    }
    
}
