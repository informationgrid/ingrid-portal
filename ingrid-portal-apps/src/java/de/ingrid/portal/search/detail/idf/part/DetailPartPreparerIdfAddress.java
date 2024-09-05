/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.search.detail.idf.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.context.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.config.PortalConfig;

public class DetailPartPreparerIdfAddress extends DetailPartPreparer{

    @Override
    public void init(Node node, String iPlugId, RenderRequest request, RenderResponse response, Context context) {
        super.init( node, iPlugId, request, response, context );

        this.templateName = "/WEB-INF/templates/detail/parts/detail_part_preparer_address.vm";
        this.localTagName = "idfResponsibleParty";
    }
    
    public String getAddressClassType() {
        String xpathExpression = "./idf:hierarchyParty";
        if(xPathUtils.nodeExists(rootNode, xpathExpression)){
            Node node = xPathUtils.getNode(rootNode, xpathExpression);
            if(xPathUtils.nodeExists(node, "./idf:addressType")){
                return xPathUtils.getString(node, "./idf:addressType").trim();
            }
        }
        return "1"; 
    }
    
    public String getAddressClassTitle(String addressType, String uuid) {
        String addressTitle = null;
        String xpathExpression = "./idf:hierarchyParty[@uuid='" + uuid + "']";
        if(xPathUtils.nodeExists(rootNode, xpathExpression)){
            Node node = xPathUtils.getNode(rootNode, xpathExpression);
            if(addressType.equals("2")){
                if(xPathUtils.nodeExists(node, "./idf:addressIndividualName")){
                    addressTitle = getIndividualName(xPathUtils.getString(node, "./idf:addressIndividualName").trim());
                }
            }else if(addressType.equals("3")){
                if(xPathUtils.nodeExists(node, "./idf:addressIndividualName")){
                    addressTitle = getIndividualName(xPathUtils.getString(node, "./idf:addressIndividualName").trim());
                }else if(xPathUtils.nodeExists(node, "./idf:addressOrganisationName")){
                    addressTitle = removeLocalisation(xPathUtils.getString(node, "./idf:addressOrganisationName")).trim();
                }
            }else{
                if(xPathUtils.nodeExists(node, "./idf:addressOrganisationName")){
                    addressTitle = removeLocalisation(xPathUtils.getString(node, "./idf:addressOrganisationName")).trim();
                }    
                
            }
        }
        return addressTitle;
    }
    
    public List<HashMap<String, Object>> getReference(String xpathExpression, boolean isObject) {
        ArrayList<HashMap<String, Object>> linkList = new ArrayList<>();
        
        int limitReferences = PortalConfig.getInstance().getInt(PortalConfig.PORTAL_DETAIL_VIEW_LIMIT_REFERENCES, 100);
        
        if(xPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList nodeList = xPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i<nodeList.getLength();i++){
                
                if (i >= limitReferences){
                    if (linkList.size() >= limitReferences){
                        HashMap<String, Object> link = new HashMap<>();
                        link.put("type", "html");
                        link.put("body", messages.getString("info_limit_references"));
                        linkList.add(link);
                    }
                    break;
                }
                
                Node node = nodeList.item(i);
                String uuid = "";
                String title = "";
                String type = "";
                String attachedToField = "";
                String entryId = "";
                String description = "";
                String serviceUrl = null;
                String objServiceType = null;
                String[] graphicOverview = null;
                String tmp = null;
                String[] tmpList = null;

                xpathExpression = "./@uuid";
                tmp = xPathUtils.getString(node, xpathExpression);
                if(tmp != null){
                    uuid = tmp.trim();
                }
                
                if(isObject){
                    xpathExpression = "./idf:objectName";
                    tmp = xPathUtils.getString(node, xpathExpression);
                    if(tmp != null){
                        title = tmp.trim();
                    }
                    
                    xpathExpression = "./idf:objectType";
                    tmp = xPathUtils.getString(node, xpathExpression);
                    if(tmp != null){
                        type = tmp.trim();
                    }

                    xpathExpression = "./idf:attachedToField";
                    tmp = xPathUtils.getString(node, xpathExpression);
                    if(tmp != null){
                        attachedToField = tmp.trim();
                    }
                    
                    xpathExpression = "./idf:attachedToField/@entry-id";
                    tmp = xPathUtils.getString(node, xpathExpression);
                    if(tmp != null){
                        entryId = tmp.trim();
                    }
                
                    xpathExpression = "./idf:description";
                    tmp = xPathUtils.getString(node, xpathExpression);
                    if(tmp != null){
                        description = tmp.trim();
                    }

                    xpathExpression = "./idf:serviceType";
                    tmp = xPathUtils.getString(node, xpathExpression);
                    if(tmp != null){
                        objServiceType = tmp.trim();
                    }
                    
                    xpathExpression = "./idf:serviceUrl";
                    tmp = xPathUtils.getString(node, xpathExpression);
                    if(tmp != null){
                        serviceUrl = tmp.trim();
                    }
                    
                    xpathExpression = "./idf:graphicOverview";
                    tmpList = xPathUtils.getStringArray(node, xpathExpression);
                    if(tmpList != null){
                        graphicOverview = tmpList;
                    }

                }else{
                    if(xPathUtils.nodeExists(node, "./idf:addressType")){
                        type = xPathUtils.getString(node, "./idf:addressType").trim();
                    }
                    
                    if(type.equals("2")){
                        if(xPathUtils.nodeExists(node, "./idf:addressIndividualName")){
                            title = getIndividualName(xPathUtils.getString(node, "./idf:addressIndividualName").trim());
                        }
                    }else if(type.equals("3")){
                        if(xPathUtils.nodeExists(node, "./idf:addressIndividualName")){
                            title = getIndividualName(xPathUtils.getString(node, "./idf:addressIndividualName").trim());
                        }else if(xPathUtils.nodeExists(node, "./idf:addressOrganisationName")){
                            title = removeLocalisation(xPathUtils.getString(node, "./idf:addressOrganisationName")).trim();
                        }
                    }else{
                        if(xPathUtils.nodeExists(node, "./idf:addressOrganisationName")){
                            title = removeLocalisation(xPathUtils.getString(node, "./idf:addressOrganisationName")).trim();
                        }
                    }
                }
                
                HashMap link = new HashMap();
                link.put("hasLinkIcon", true);
                link.put("isExtern", false);
                link.put("title", title);
                if(this.iPlugId != null){
                    if(uuid != null){
                        if(isObject){
                            link.put("objectClass", type);
                            link.put("serviceType", objServiceType);
                            link.put("graphicOverview", graphicOverview);
                            if (description.length() > 0) {
                                link.put("description", description);
                            }
                            if (attachedToField.length() > 0) {
                                link.put("attachedToField", attachedToField);
                            }
                            String href = "?docuuid=" + uuid;
                            if(PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_DETAIL_USE_PARAMETER_PLUGID)) {
                                href += "&plugid=" + this.iPlugId;
                            }
                            link.put("href", href);
                        }else{
                            link.put("addressClass", type);
                            String href = "?docuuid=" + uuid + "&type=address";
                            if(PortalConfig.getInstance().getBoolean( PortalConfig.PORTAL_DETAIL_USE_PARAMETER_PLUGID)) {
                                href += "&plugid=" + this.iPlugId;
                            }
                            link.put("href", href);
                        }
                    }else{
                        link.put("href", "");
                    }
                }else{
                    link.put("href", "");
                }
                linkList.add(link);
            }
        }
        return linkList;
    }
}
