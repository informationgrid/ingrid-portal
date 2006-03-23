/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.search;

import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class DisplayTreeFactory {
    
    public static DisplayTreeNode getTree(IngridQuery query) {
        DisplayTreeNode root = new DisplayTreeNode("root", "root", true);
        root.setType(DisplayTreeNode.ROOT);
        TermQuery[] terms = UtilsSearch.getAllTerms(query);
        for (int i=0; i<terms.length; i++) {
            if (terms[i].getType() == TermQuery.TERM) {
                DisplayTreeNode node = new DisplayTreeNode(""+i, terms[i].getTerm(), false);
                node.setType(DisplayTreeNode.SEARCH_TERM);
                node.setParent(root);
                root.addChild(node);
            }
        }
        return root;
    }

}
