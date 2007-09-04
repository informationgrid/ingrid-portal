/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.global.IPlugHelper;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
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

    /**
     * Get Partner/Provider Tree Structure FROM Data Base. Simply reads
     * Partner/Providers from Database. Filters partners by the filter param.
     * 
     * @param filter The partners to restrict. null for all partner.
     * @return
     */
    public static DisplayTreeNode getTreeFromPartnerProviderFromDB(ArrayList filter) {
        DisplayTreeNode root = new DisplayTreeNode("root", "root", true);
        root.setType(DisplayTreeNode.ROOT);

        LinkedHashMap partnerProviderMap = UtilsDB.getPartnerProviderMap(filter);
        Iterator keysPartnerMaps = partnerProviderMap.keySet().iterator();

        // process all partners
        while (keysPartnerMaps.hasNext()) {
            LinkedHashMap partnerMap = (LinkedHashMap) partnerProviderMap.get(keysPartnerMaps.next());
            IngridPartner partner = (IngridPartner) partnerMap.get("partner");

            DisplayTreeNode partnerNode = new DisplayTreeNode(partner.getIdent(), partner.getName(), false);
            partnerNode.setType(DisplayTreeNode.GENERIC);
            partnerNode.setParent(root);
            root.addChild(partnerNode);

            LinkedHashMap providerMaps = (LinkedHashMap) partnerMap.get("providers");
            Iterator keysProviderMaps = providerMaps.keySet().iterator();

            // process all providers
            while (keysProviderMaps.hasNext()) {
                LinkedHashMap providerMap = (LinkedHashMap) providerMaps.get(keysProviderMaps.next());
                IngridProvider provider = (IngridProvider) providerMap.get("provider");

                DisplayTreeNode providerNode = new DisplayTreeNode(provider.getIdent(), provider.getName(), false);
                providerNode.setType(DisplayTreeNode.GENERIC);
                providerNode.setParent(partnerNode);
                partnerNode.addChild(providerNode);
            }
        }

        return root;
    }

    /**
     * Get Partner/Provider Tree Structure FROM iBus. Providers are dynamically
     * queried from iBus (iPlugs).
     * 
     * @return
     */
    public static DisplayTreeNode getTreeFromPartnerProviderFromBus() {
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

            // store all providers for the current partner ! every provider only
            // once !
            HashSet providers = new HashSet();
            for (int i = 0; i < plugs.length; i++) {
                PlugDescription plug = plugs[i];
                String[] plugProvider = plug.getProviders();
                for (int j = 0; j < plugProvider.length; j++) {
                    int delimiterPos = plugProvider[j].indexOf("_");
                    String partnerIdent = null;
                    if (delimiterPos != -1) {
                        partnerIdent = plugProvider[j].substring(0, plugProvider[j].indexOf("_"));
                    } else {
                        partnerIdent = plugProvider[j];
                    }
                    // hack: "bund" is coded as "bu" in provider idents
                    if (partnerIdent.equals("bu")) {
                        partnerIdent = "bund";
                    }
                    if (partnerIdent.equals(partner.getIdent())) {
                        providers.add(plugProvider[j]);
                    }
                }
            }

            // get the providers with their full name as nodes and store them in
            // a list !
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

    public static DisplayTreeNode getTreeFromECSIPlugs(PlugDescription[] plugs) {
        DisplayTreeNode root = new DisplayTreeNode("root", "root", true);
        root.setType(DisplayTreeNode.ROOT);
        root.put("level", new Integer(0));
        
        DisplayTreeNode partnerNode = null;
        DisplayTreeNode catalogNode = null;

        for (int i = 0; i < plugs.length; i++) {
        	PlugDescription plug = plugs[i];

            String[] partners = plug.getPartners();
            StringBuffer partnerNameBuffer = new StringBuffer("");
            for (int j = 0; j < partners.length; j++) {
            	partnerNameBuffer.append(UtilsDB.getPartnerFromKey(partners[j]));
            }

            // Partner node
            String partnerName = partnerNameBuffer.toString();
            if (partnerNode == null || 
            		!partnerNode.getName().equals(partnerName)) {
                partnerNode = new DisplayTreeNode("" + root.getNextId(), partnerName, false);            	
                partnerNode.setType(DisplayTreeNode.GENERIC);
                partnerNode.put("level", new Integer(1));
                partnerNode.setParent(root);
                root.addChild(partnerNode);
            }

            // catalog node
            String catalogName = plug.getDataSourceName();
            if (catalogNode == null || 
            		!catalogNode.getName().equals(catalogName)) {
            	catalogNode = new DisplayTreeNode("" + root.getNextId(), catalogName, false);            	
            	catalogNode.setType(DisplayTreeNode.GENERIC);
            	catalogNode.put("level", new Integer(2));
            	catalogNode.setParent(partnerNode);
            	partnerNode.addChild(catalogNode);
            }

            // iPlug Node
            String name = "searchCatHierarchy.tree.unknown";
            if (IPlugHelper.hasDataType(plug, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
            	name = "searchCatHierarchy.tree.objects";
            } else if (IPlugHelper.hasDataType(plug, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
            	name = "searchCatHierarchy.tree.addresses";
            }
            DisplayTreeNode node = new DisplayTreeNode("" + root.getNextId(), name, false);
            node.setType(DisplayTreeNode.GENERIC);
            node.put("level", new Integer(3));
            node.setParent(catalogNode);
            catalogNode.addChild(node);
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
