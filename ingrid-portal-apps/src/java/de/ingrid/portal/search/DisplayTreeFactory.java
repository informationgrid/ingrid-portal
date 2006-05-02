/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.search;

import java.util.Iterator;
import java.util.List;

import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class DisplayTreeFactory {
    
    public static DisplayTreeNode getTreeFromQueryTerms(IngridQuery query) {
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

    
    public static DisplayTreeNode getTreeFromPartnerProvider() {
        DisplayTreeNode root = new DisplayTreeNode("root", "root", true);
        root.setType(DisplayTreeNode.ROOT);
        List partnerList = UtilsDB.getPartners();
        PlugDescription[] plugs = IBUSInterfaceImpl.getInstance().getAllIPlugs();
        Iterator it = partnerList.iterator();
        while (it.hasNext()) {
            IngridPartner partner = (IngridPartner)it.next();
            DisplayTreeNode node = new DisplayTreeNode(partner.getIdent(), partner.getName(), false);
            node.setType(DisplayTreeNode.GENERIC);
            node.setParent(root);
            root.addChild(node);
            for (int i=0; i<plugs.length; i++) {
                PlugDescription plug = plugs[i]; 
                String[] plugProvider = plug.getProviders();
                for (int j=0; j<plugProvider.length; j++) {
                    String partnerIdent = plugProvider[j].substring(0, plugProvider[j].indexOf("_"));
                    if (partnerIdent.equals(partner.getIdent())) {
                        DisplayTreeNode providerNode = node.getChild(plugProvider[j]);
                        if (providerNode == null) {
                            providerNode = new DisplayTreeNode(plugProvider[j], UtilsDB.getProviderFromKey(plugProvider[j]), false);
                            providerNode.setType(DisplayTreeNode.GENERIC);
                            providerNode.setParent(node);
                            node.addChild(providerNode);
                        }
                    }
                }
            }
            
        }
        return root;
    }
    
    
}
