/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.portal.search;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IPlugHelper;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.portal.search.catalog.CatalogTreeDataProvider;
import de.ingrid.portal.search.catalog.CatalogTreeDataProviderFactory;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class DisplayTreeFactory {
	
	// keys for accessing node data
	static private String NODE_LEVEL = "level"; 
	static private String NODE_PLUG_TYPE = "plugType"; 
	static private String NODE_PLUG_ID = "plugId"; 
	static private String NODE_DOC_ID = "docId"; 
	static private String NODE_UDK_DOC_ID = "udk_docId"; 
	static private String NODE_UDK_CLASS = "udk_class"; 
	static private String NODE_EXPANDABLE = "expandable"; 

    public static DisplayTreeNode getTreeFromQueryTerms(IngridQuery query) {
        DisplayTreeNode root = new DisplayTreeNode("root", "root", true);
        root.setType(DisplayTreeNode.ROOT);
        if (query == null) {
        	return root;
        }
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
            if (providerMaps != null) {
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
        root.put(NODE_LEVEL, new Integer(0));
        
        DisplayTreeNode partnerNode = null;
        DisplayTreeNode catalogNode = null;

        String partnerRestriction = PortalConfig.getInstance().getString(
                PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
        
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
                partnerNode.put(NODE_LEVEL, new Integer(1));
                partnerNode.put(NODE_EXPANDABLE, new Boolean(true));
                // no "plugid", no "docid" !
                if(partnerRestriction == null || partnerRestriction.length() < 1){
                	partnerNode.setParent(root);
                    root.addChild(partnerNode);
                }
                
            }
            
            // catalog node
            String catalogName = plug.getDataSourceName();
            if (catalogNode == null || 
            		!catalogNode.getName().equals(catalogName)) {
            	catalogNode = new DisplayTreeNode("" + root.getNextId(), catalogName, false);            	
            	catalogNode.setType(DisplayTreeNode.GENERIC);
            	catalogNode.put(NODE_LEVEL, new Integer(2));
            	catalogNode.put(NODE_EXPANDABLE, new Boolean(true));
                // no "plugid", no "docid" !
            	 if(partnerRestriction != null && partnerRestriction.length() > 0){
             		catalogNode.setParent(root);
             		root.addChild(catalogNode);
                 }else{
                	catalogNode.setParent(partnerNode);
                 	partnerNode.addChild(catalogNode);
                 }
            	
            }

            // iPlug Node
            String name = "searchCatHierarchy.tree.unknown";
            String type = null;
            if (IPlugHelper.hasDataType(plug, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
            	name = "searchCatHierarchy.tree.objects";
            	type = Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS;
            } else if (IPlugHelper.hasDataType(plug, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
            	name = "searchCatHierarchy.tree.addresses";
            	type = Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS;
            }
            DisplayTreeNode node = new DisplayTreeNode("" + root.getNextId(), name, false);
            node.setType(DisplayTreeNode.GENERIC);
            node.put(NODE_LEVEL, new Integer(3));
        	// only "plugid", no "docid" !
            node.put(NODE_PLUG_TYPE, type);
            node.put(NODE_PLUG_ID, plug.getPlugId());
            node.put(NODE_EXPANDABLE, new Boolean(true));
            node.setParent(catalogNode);
            catalogNode.addChild(node);
        }
        return root;
    }

    public static void openECSNode(DisplayTreeNode rootNode, DisplayTreeNode nodeToOpen) {

        String plugType = (String) nodeToOpen.get(NODE_PLUG_TYPE);
        String plugId = (String) nodeToOpen.get(NODE_PLUG_ID);

        if (plugType == null || plugId == null) {
        	// no iplug node, is parent folder
        	return;
        }

        // get ALL children (IngridHits)
        String udkDocId = (String) nodeToOpen.get(NODE_UDK_DOC_ID);
        String key_udkDocId = "";
        String key_udkClass = "";
        List hits = new ArrayList();
        
        PlugDescription pd = IBUSInterfaceImpl.getInstance().getIPlug(plugId);
        CatalogTreeDataProvider ctdp = CatalogTreeDataProviderFactory.getDetailDataPreparer(IPlugVersionInspector.getIPlugVersion(pd));
        
		if (udkDocId == null) {
			hits = ctdp.getTopEntities(plugId, plugType);
		} else {
			hits = ctdp.getSubEntities(udkDocId, plugId, plugType, null);			
		}

		// keys for extracting data
    	if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
            key_udkDocId = Settings.HIT_KEY_OBJ_ID;
            key_udkClass = Settings.HIT_KEY_UDK_CLASS;

    	} else if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
            key_udkDocId = Settings.HIT_KEY_ADDRESS_ADDRID;
            key_udkClass = Settings.HIT_KEY_ADDRESS_CLASS;
    	}

        // set up according children nodes in tree
    	int parentLevel = ((Integer) nodeToOpen.get(NODE_LEVEL)).intValue();
    	int childrenLevel = parentLevel + 1;
        ArrayList childNodes = new ArrayList(hits.size());
        Iterator it = hits.iterator();
        while (it.hasNext()) {
            IngridHit hit = (IngridHit) it.next();
            IngridHitDetail detail = (IngridHitDetail) hit.get(Settings.RESULT_KEY_DETAIL);
            udkDocId = UtilsSearch.getDetailValue(detail, key_udkDocId);
            String udkClass = UtilsSearch.getDetailValue(detail, key_udkClass);

            String nodeName = detail.getTitle();
            // different node text, when person
            if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
            	if (udkClass.equals("2") || udkClass.equals("3")) {
            		String[] titleElements = new String[] {
                		UtilsSearch.getDetailValue(detail, "T02_address.address_value"),
            			UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_ADDRESS),
            			UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_TITLE),
            			UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_FIRSTNAME),
            			UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ADDRESS_LASTNAME)
            		};
            		String personStr =  UtilsString.concatStringsIfNotNull(titleElements, " ");
            		if (personStr != null && personStr.length() > 0) {
            			nodeName = personStr;
            		}
            	}
            }

            // check whether child node has children as well -> request only 1 child !
            boolean hasChildren = ctdp.hasChildren(udkDocId, plugId, plugType);
            
            DisplayTreeNode childNode = new DisplayTreeNode("" + rootNode.getNextId(), nodeName, false);
            childNode.setType(DisplayTreeNode.GENERIC);
            childNode.put(NODE_LEVEL, new Integer(childrenLevel));
            childNode.put(NODE_PLUG_TYPE, plugType);
            childNode.put(NODE_PLUG_ID, plugId);
            childNode.put(NODE_DOC_ID, hit.getId());
            childNode.put(NODE_UDK_DOC_ID, udkDocId);
            childNode.put(NODE_UDK_CLASS, udkClass);
            childNode.put(NODE_EXPANDABLE, new Boolean(hasChildren));

            childNodes.add(childNode);
        }

        // sort children nodes
        Collections.sort(childNodes, new DisplayTreeFactory.ECSDocumentNodeComparator());

        // and add them to the parent
        it = childNodes.iterator();
        while (it.hasNext()) {
            DisplayTreeNode childNode = (DisplayTreeNode) it.next();
            childNode.setParent(nodeToOpen);
            nodeToOpen.addChild(childNode);
        }
    }

    /**
     * Inner class: for provider sorting;
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

    /**
     * Inner class: for sorting of ECS Documents;
     */
    static private class ECSDocumentNodeComparator implements Comparator {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public final int compare(Object a, Object b) {
            try {
                String aName = ((DisplayTreeNode) a).getName().toLowerCase();
                String bName = ((DisplayTreeNode) b).getName().toLowerCase();

            	// Get the collator for the German Locale (for correct sorting of ä,ö,ü ...)  
            	Collator germanCollator = Collator.getInstance(Locale.GERMAN);
            	return germanCollator.compare(aName, bName);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
