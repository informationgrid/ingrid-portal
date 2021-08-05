/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import de.ingrid.codelists.CodeListService;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.IPlugHelper;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.Utils;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;
import de.ingrid.portal.search.catalog.CatalogTreeDataProvider;
import de.ingrid.portal.search.catalog.CatalogTreeDataProviderFactory;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class DisplayTreeFactory {
    
    // keys for accessing node data
    private static final String NODE_LEVEL = "level"; 
    private static final String NODE_PLUG_TYPE = "plugType"; 
    private static final String NODE_PLUG_ID = "plugId"; 
    private static final String NODE_DOC_ID = "docId"; 
    private static final String NODE_ORIG_DOC_ID = "origDocId";
    private static final String NODE_UDK_DOC_ID = "udk_docId";
    private static final String NODE_UDK_CLASS = "udk_class"; 
    private static final String NODE_UDK_CLASS_VALUE = "udk_class_value"; 
    private static final String NODE_EXPANDABLE = "expandable"; 

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
    public static DisplayTreeNode getTreeFromPartnerProviderFromDB(List filter) {
        DisplayTreeNode root = new DisplayTreeNode("root", "root", true);
        root.setType(DisplayTreeNode.ROOT);

        LinkedHashMap partnerProviderMap = (LinkedHashMap) UtilsDB.getPartnerProviderMap(filter);
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
                    int delimiterPos = plugProvider[j].indexOf('_');
                    String partnerIdent = null;
                    if (delimiterPos != -1) {
                        partnerIdent = plugProvider[j].substring(0, plugProvider[j].indexOf('_'));
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
            // NO LEADS TO java.lang.StackOverflowError !!! Why ?
            // Executed like this in various trees in portal (e.g. tree in SearchCatalogThesaurusPortlet ...) !
            // seems to cause endless "reference loop": child <-> parent !?
//            node.setParent(root);
            node.put("topic", results[i]);
            root.addChild(node);
        }
        return root;
    }

    public static DisplayTreeNode getTreeFromECSIPlugs(PlugDescription[] plugs) {
        DisplayTreeNode root = new DisplayTreeNode("root", "root", true);
        root.setType(DisplayTreeNode.ROOT);
        root.put(NODE_LEVEL, 0);
        
        DisplayTreeNode partnerNode = null;
        DisplayTreeNode catalogNode = null;

        String partnerRestriction = PortalConfig.getInstance().getString(
                PortalConfig.PORTAL_SEARCH_RESTRICT_PARTNER);
        boolean hiddenCatalogName = PortalConfig.getInstance().getBoolean(
                PortalConfig.PORTAL_HIERARCHY_CATALOGNAME_HIDDEN, false);

        for (int i = 0; i < plugs.length; i++) {
            PlugDescription plug = plugs[i];
            String queryString = "iplugs:\"" + plug.getPlugId() + "\"";

            if(hasPlugHits(queryString)) { 
                if(!hiddenCatalogName){
                    String[] partners = plug.getPartners();
                    StringBuilder partnerNameBuffer = new StringBuilder("");
                    for (int j = 0; j < partners.length; j++) {
                        partnerNameBuffer.append(UtilsDB.getPartnerFromKey(partners[j]));
                    }

                    // Partner node
                    String partnerName = partnerNameBuffer.toString();
                    if (partnerNode == null || 
                            !partnerNode.getName().equals(partnerName)) {
                        partnerNode = new DisplayTreeNode(Utils.getMD5Hash(partnerName), partnerName, false);
                        partnerNode.setType(DisplayTreeNode.GENERIC);
                        partnerNode.put(NODE_LEVEL, 1);
                        partnerNode.put(NODE_EXPANDABLE, true);
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
                        catalogNode = new DisplayTreeNode(Utils.getMD5Hash(catalogName + plug.getPlugId()), catalogName, false);
                        catalogNode.setType(DisplayTreeNode.GENERIC);
                        catalogNode.put(NODE_LEVEL, 2);
                        catalogNode.put(NODE_EXPANDABLE, true);
                        // no "plugid", no "docid" !
                         if(partnerRestriction != null && partnerRestriction.length() > 0){
                             catalogNode.setParent(root);
                             root.addChild(catalogNode);
                         }else{
                            catalogNode.setParent(partnerNode);
                             partnerNode.addChild(catalogNode);
                         }
                        
                    }
                }
                // iPlug Node
                String name;
                String type = null;
                if(hiddenCatalogName){
                    catalogNode = root;
                }
                
                if (IPlugHelper.hasDataType(plug, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS) && IPlugHelper.hasDataType(plug, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
                    name = "searchCatHierarchy.tree.objects";
                    type = Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS;
                    queryString = "iplugs:\"" + plug.getPlugId() + "\" datatype:metadata";
                    if(hasPlugHits(queryString)) { 
                        addTreeNode( root, name, type, plug.getPlugId(), catalogNode );
                    }

                    name = "searchCatHierarchy.tree.addresses";
                    type = Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS;
                    queryString = "iplugs:\"" + plug.getPlugId() + "\" datatype:address";
                    if(hasPlugHits(queryString)) { 
                        addTreeNode( root, name, type, plug.getPlugId(), catalogNode );
                    }
                } else if (IPlugHelper.hasDataType(plug, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
                    name = "searchCatHierarchy.tree.objects";
                    type = Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS;
                    queryString = "iplugs:\"" + plug.getPlugId() + "\" datatype:metadata";
                    if(hasPlugHits(queryString)) { 
                        addTreeNode( root, name, type, plug.getPlugId(), catalogNode );
                    }
                } else if (IPlugHelper.hasDataType(plug, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
                    name = "searchCatHierarchy.tree.addresses";
                    type = Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS;
                    queryString = "iplugs:\"" + plug.getPlugId() + "\" datatype:address";
                    if(hasPlugHits(queryString)) { 
                        addTreeNode( root, name, type, plug.getPlugId(), catalogNode );
                    }
                }
            }
        }
        return root;
    }
    
    public static boolean hasPlugHits(String queryString) {
        IngridHits hits = null;
        IngridQuery query = null;
        
        try {
            query = QueryStringParser.parse( queryString );
            hits = IBUSInterfaceImpl.getInstance().search( query, 1, 0, 0, PortalConfig.getInstance().getInt( PortalConfig.QUERY_TIMEOUT_RANKED, 5000 ));
        } catch (Exception e) {
        }
        if(hits != null && hits.length() > 0) {
            return true;
        }
        return false;
    }

    private static void addTreeNode(DisplayTreeNode root, String name, Object type, String plugId, DisplayTreeNode catalogNode) {
        DisplayTreeNode node = new DisplayTreeNode(Utils.getMD5Hash(name + plugId), name, false);
        node.setType(DisplayTreeNode.GENERIC);
        node.put(NODE_LEVEL, 3);
        // only "plugid", no "docid" !
        node.put(NODE_PLUG_TYPE, type);
        node.put(NODE_PLUG_ID, plugId);
        node.put(NODE_EXPANDABLE, true);
        node.setParent(catalogNode);
        catalogNode.addChild(node);
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
        String keyUdkDocId = "";
        String keyUdkClass = "";
        List hits;
        
        PlugDescription pd = IBUSInterfaceImpl.getInstance().getIPlug(plugId);
        CatalogTreeDataProvider ctdp = CatalogTreeDataProviderFactory.getDetailDataPreparer(IPlugVersionInspector.getIPlugVersion(pd));
        
        if (udkDocId == null) {
            hits = ctdp.getTopEntities(plugId, plugType);
        } else {
            hits = ctdp.getSubEntities(udkDocId, plugId, plugType, null);
        }

        // keys for extracting data
        if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
            keyUdkDocId = Settings.HIT_KEY_OBJ_ID;
            keyUdkClass = Settings.HIT_KEY_UDK_CLASS;

        } else if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
            keyUdkDocId = Settings.HIT_KEY_ADDRESS_ADDRID;
            keyUdkClass = Settings.HIT_KEY_ADDRESS_CLASS;
        }

        // set up according children nodes in tree
        int parentLevel = ((Integer) nodeToOpen.get(NODE_LEVEL)).intValue();
        int childrenLevel = parentLevel + 1;
        ArrayList<DisplayTreeNode> childNodes = new ArrayList(hits.size());
        ArrayList<DisplayTreeNode> freeAddresses = new ArrayList();
        Iterator it = hits.iterator();
        while (it.hasNext()) {
            IngridHit hit = (IngridHit) it.next();
            IngridHitDetail detail = (IngridHitDetail) hit.get(Settings.RESULT_KEY_DETAIL);
            udkDocId = UtilsSearch.getDetailValue(detail, keyUdkDocId);
            String udkClass = UtilsSearch.getDetailValue(detail, keyUdkClass);
            String origDocId = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ORG_OBJ_ID);

            String nodeName = detail.getTitle();
            // different node text, when person
            if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS) && (udkClass.equals("2") || udkClass.equals("3"))) {
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

            // check whether child node has children as well -> request only 1 child !
            boolean hasChildren = ctdp.hasChildren(udkDocId, plugId, plugType);
            
            DisplayTreeNode childNode = new DisplayTreeNode(Utils.getMD5Hash(plugId + udkDocId), nodeName, false);
            childNode.setType(DisplayTreeNode.GENERIC);
            childNode.put(NODE_LEVEL, childrenLevel);
            childNode.put(NODE_PLUG_TYPE, plugType);
            childNode.put(NODE_PLUG_ID, plugId);
            childNode.put(NODE_DOC_ID, hit.getId());
            childNode.put(NODE_UDK_DOC_ID, udkDocId);
            childNode.put(NODE_ORIG_DOC_ID, origDocId);
            childNode.put(NODE_UDK_CLASS, udkClass);
            childNode.put(NODE_EXPANDABLE, hasChildren);

            if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS) && udkClass.equals("3")) {
                freeAddresses.add(childNode);
            } else {
                childNodes.add(childNode);
            }
        }
        if (!freeAddresses.isEmpty()) {
            DisplayTreeNode childNode = new DisplayTreeNode(Utils.getMD5Hash(plugId + "searchCatHierarchy.tree.addresses.free"), "searchCatHierarchy.tree.addresses.free", false);
            childNode.setType(DisplayTreeNode.GENERIC);
            childNode.put(NODE_LEVEL, childrenLevel);
            childNode.put(NODE_PLUG_TYPE, plugType);
            childNode.put(NODE_PLUG_ID, plugId);
            childNode.put(NODE_EXPANDABLE, true);
            for (DisplayTreeNode freeAddress : freeAddresses) {
                childNode.addChild(freeAddress);
            }
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

    public static String openECSNodeString(DisplayTreeNode rootNode, DisplayTreeNode nodeToOpen, String lang, IngridResourceBundle messages, CodeListService codeListService) {

        JSONArray values = new JSONArray();
        String plugType = (String) nodeToOpen.get(NODE_PLUG_TYPE);
        String plugId = (String) nodeToOpen.get(NODE_PLUG_ID);

        if (plugType == null || plugId == null) {
            // no iplug node, is parent folder
            return values.toString();
        }

        // get ALL children (IngridHits)
        String udkDocId = (String) nodeToOpen.get(NODE_UDK_DOC_ID);
        String keyUdkDocId = "";
        String keyUdkClass = "";
        List hits;
        
        PlugDescription pd = IBUSInterfaceImpl.getInstance().getIPlug(plugId);
        CatalogTreeDataProvider ctdp = CatalogTreeDataProviderFactory.getDetailDataPreparer(IPlugVersionInspector.getIPlugVersion(pd));
        
        if (udkDocId == null) {
            hits = ctdp.getTopEntities(plugId, plugType);
        } else {
            hits = ctdp.getSubEntities(udkDocId, plugId, plugType, null);
        }

        // keys for extracting data
        if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS)) {
            keyUdkDocId = Settings.HIT_KEY_OBJ_ID;
            keyUdkClass = Settings.HIT_KEY_UDK_CLASS;

        } else if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS)) {
            keyUdkDocId = Settings.HIT_KEY_ADDRESS_ADDRID;
            keyUdkClass = Settings.HIT_KEY_ADDRESS_CLASS;
        }

        // set up according children nodes in tree
        int parentLevel = ((Integer) nodeToOpen.get(NODE_LEVEL)).intValue();
        int childrenLevel = parentLevel + 1;
        ArrayList<DisplayTreeNode> childNodes = new ArrayList(hits.size());
        ArrayList<DisplayTreeNode> freeAddresses = new ArrayList();
        Iterator it = hits.iterator();
        while (it.hasNext()) {
            IngridHit hit = (IngridHit) it.next();
            IngridHitDetail detail = (IngridHitDetail) hit.get(Settings.RESULT_KEY_DETAIL);
            udkDocId = UtilsSearch.getDetailValue(detail, keyUdkDocId);
            String udkClass = UtilsSearch.getDetailValue(detail, keyUdkClass);
            String origDocId = UtilsSearch.getDetailValue(detail, Settings.HIT_KEY_ORG_OBJ_ID);

            String nodeName = detail.getTitle();
            // different node text, when person
            if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS) && (udkClass.equals("2") || udkClass.equals("3"))) {
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

            // check whether child node has children as well -> request only 1 child !
            boolean hasChildren = ctdp.hasChildren(udkDocId, plugId, plugType);
            
            DisplayTreeNode childNode = new DisplayTreeNode(Utils.getMD5Hash(plugId + udkDocId), nodeName, false);
            childNode.setType(DisplayTreeNode.GENERIC);
            childNode.put(NODE_LEVEL, childrenLevel);
            childNode.put(NODE_PLUG_TYPE, plugType);
            childNode.put(NODE_PLUG_ID, plugId);
            childNode.put(NODE_DOC_ID, hit.getId());
            childNode.put(NODE_UDK_DOC_ID, udkDocId);
            childNode.put(NODE_ORIG_DOC_ID, origDocId);
            childNode.put(NODE_UDK_CLASS, udkClass);
            childNode.put(NODE_EXPANDABLE, hasChildren);

            if (plugType.equals(Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS) && udkClass.equals("3")) {
                childNode.put(NODE_UDK_CLASS_VALUE, messages.getString("udk_adr_class_name_" + udkClass + ""));
                freeAddresses.add(childNode);
            } else {
                childNode.put(NODE_UDK_CLASS_VALUE, codeListService.getCodeListValue("8000", udkClass, lang));
                childNodes.add(childNode);
            }
        }
        if (!freeAddresses.isEmpty()) {
            DisplayTreeNode childNode = new DisplayTreeNode(Utils.getMD5Hash(plugId + "searchCatHierarchy.tree.addresses.free"), "searchCatHierarchy.tree.addresses.free", false);
            childNode.setType(DisplayTreeNode.GENERIC);
            childNode.put(NODE_LEVEL, childrenLevel);
            childNode.put(NODE_PLUG_TYPE, plugType);
            childNode.put(NODE_PLUG_ID, plugId);
            childNode.put(NODE_EXPANDABLE, true);
            for (DisplayTreeNode freeAddress : freeAddresses) {
                childNode.addChild(freeAddress);
            }
            childNodes.add(childNode);
        }

        // sort children nodes
        Collections.sort(childNodes, new DisplayTreeFactory.ECSDocumentNodeComparator());

        // and add them to the parent
        it = childNodes.iterator();
        while (it.hasNext()) {
            DisplayTreeNode childNode = (DisplayTreeNode) it.next();
            values.put(new JSONObject(childNode));
            childNode.setParent(nodeToOpen);
            nodeToOpen.addChild(childNode);
        }
        return values.toString();
    }

    /**
     * Inner class: for provider sorting;
     */
    private static class ProviderNodeComparator implements Comparator {
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
    private static class ECSDocumentNodeComparator implements Comparator {
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
