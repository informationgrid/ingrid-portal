/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.utils.IngridHit;
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
        terms = UtilsSearch.removeDoubleTerms(terms);
        String term;
        for (int i = 0; i < terms.length; i++) {
            if (terms[i].getType() == TermQuery.TERM) {
                term = terms[i].getTerm();
                if (term.startsWith("\"")) {
                    term = term.substring(1, term.length() - 1);
                }
                DisplayTreeNode node = new DisplayTreeNode("" + i, term, false);
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

        // process all partners
        Iterator it = partnerList.iterator();
        while (it.hasNext()) {
            IngridPartner partner = (IngridPartner) it.next();
            DisplayTreeNode partnerNode = new DisplayTreeNode(partner.getIdent(), partner.getName(), false);
            partnerNode.setType(DisplayTreeNode.GENERIC);
            partnerNode.setParent(root);
            root.addChild(partnerNode);

            // store all providers for the current partner ! every provider only once !
            HashSet providers = new HashSet();
            for (int i = 0; i < plugs.length; i++) {
                PlugDescription plug = plugs[i];
                String[] plugProvider = plug.getProviders();
                for (int j = 0; j < plugProvider.length; j++) {
                    String partnerIdent = plugProvider[j].substring(0, plugProvider[j].indexOf("_"));
                    // hack: "bund" is coded as "bu" in provider idents
                    if (partnerIdent.equals("bu")) {
                        partnerIdent = "bund";
                    }
                    if (partnerIdent.equals(partner.getIdent())) {
                        providers.add(plugProvider[j]);
                    }
                }
            }

            // get the providers with their full name as nodes and store them in a list !
            ArrayList providerList = new ArrayList(providers.size());
            Iterator itProvider = providers.iterator();
            while (itProvider.hasNext()) {
                String providerId = (String) itProvider.next();
                DisplayTreeNode providerNode = new DisplayTreeNode(providerId, UtilsDB.getProviderFromKey(providerId),
                        false);
                providerNode.setType(DisplayTreeNode.GENERIC);
                providerList.add(providerNode);
            }

            // sort providers
            Collections.sort(providerList, new DisplayTreeFactory.ProviderNodeComparator());

            // add providers to the partner
            itProvider = providerList.iterator();
            while (itProvider.hasNext()) {
                DisplayTreeNode providerNode = (DisplayTreeNode) itProvider.next();
                providerNode.setParent(partnerNode);
                partnerNode.addChild(providerNode);
            }
        }

        return root;
    }

    public static DisplayTreeNode getTreeFromEvents(IngridHit[] results) {
        Topic topic = null;

        DisplayTreeNode root = new DisplayTreeNode("root", "root", true);
        root.setType(DisplayTreeNode.ROOT);
        for (int i = 0; i < results.length; i++) {
            topic = (Topic) results[i];
            DisplayTreeNode node = new DisplayTreeNode("" + i, topic.getTopicName(), false);
            node.setType(DisplayTreeNode.GENERIC);
            node.setParent(root);
            node.put("topic", results[i]);
            root.addChild(node);
        }
        return root;
    }

    /**
     * Inner class: ProviderNodeComparator for provider sorting;
     *
     * @author Martin Maidhof
     */
    static private class ProviderNodeComparator implements Comparator {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public final int compare(Object a, Object b) {
            try {
                String aName = ((DisplayTreeNode) a).getName().toLowerCase();
                String bName = ((DisplayTreeNode) b).getName().toLowerCase();

                return aName.compareTo(bName);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
