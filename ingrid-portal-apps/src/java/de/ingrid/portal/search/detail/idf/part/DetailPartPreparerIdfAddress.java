/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.portal.search.detail.idf.part;

import java.util.ArrayList;
import java.util.HashMap;

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

        this.templateName = "/WEB-INF/templates/detail/part/detail_part_preparer_address.vm";
        this.localTagName = "idfResponsibleParty";
    }
    
    public String getAddressClassType() {
        String xpathExpression = "./idf:hierarchyParty";
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
            if(XPathUtils.nodeExists(node, "./idf:addressType")){
                return XPathUtils.getString(node, "./idf:addressType").trim();
            }
        }
        return "1"; 
    }
    
    public String getAddressClassTitle(String addressType) {
        String addressTitle = null;
        String xpathExpression = "./idf:hierarchyParty";
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            Node node = XPathUtils.getNode(rootNode, xpathExpression);
            if(addressType.equals("2")){
                if(XPathUtils.nodeExists(node, "./idf:addressIndividualName")){
                    addressTitle = getIndividualName(XPathUtils.getString(node, "./idf:addressIndividualName").trim());
                }
            }else if(addressType.equals("3")){
                if(XPathUtils.nodeExists(node, "./idf:addressIndividualName")){
                    addressTitle = getIndividualName(XPathUtils.getString(node, "./idf:addressIndividualName").trim());
                }else if(XPathUtils.nodeExists(node, "./idf:addressOrganisationName")){
                    addressTitle = XPathUtils.getString(node, "./idf:addressOrganisationName").trim();
                }
            }else{
                if(XPathUtils.nodeExists(node, "./idf:addressOrganisationName")){
                    addressTitle = XPathUtils.getString(node, "./idf:addressOrganisationName").trim();
                }    
                
            }
        }
        return addressTitle;
    }
    
    public ArrayList<HashMap<String, Object>> getReference(String xpathExpression, boolean isObject) {
        ArrayList<HashMap<String, Object>> linkList = new ArrayList<HashMap<String, Object>>();
        
        int limitReferences = PortalConfig.getInstance().getInt(PortalConfig.PORTAL_DETAIL_VIEW_LIMIT_REFERENCES, 100);
        
        if(XPathUtils.nodeExists(rootNode, xpathExpression)){
            NodeList nodeList = XPathUtils.getNodeList(rootNode, xpathExpression);
            for (int i=0; i<nodeList.getLength();i++){
                
                if (i >= limitReferences){
                    if (linkList.size() >= limitReferences){
                        HashMap<String, Object> link = new HashMap<String, Object>();
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
                
                if(XPathUtils.nodeExists(node, "@uuid")){
                    uuid = XPathUtils.getString(node, "@uuid").trim();
                }
                
                if(isObject){
                    if(XPathUtils.nodeExists(node, "./idf:objectName")){
                        title = XPathUtils.getString(node, "./idf:objectName").trim();
                    }
                    
                    if(XPathUtils.nodeExists(node, "./idf:objectType")){
                        type = XPathUtils.getString(node, "./idf:objectType").trim();
                    }
                }else{
                    if(XPathUtils.nodeExists(node, "./idf:addressType")){
                        type = XPathUtils.getString(node, "./idf:addressType").trim();
                    }
                    
                    if(type.equals("2")){
                        if(XPathUtils.nodeExists(node, "./idf:addressIndividualName")){
                            title = getIndividualName(XPathUtils.getString(node, "./idf:addressIndividualName").trim());
                        }
                    }else if(type.equals("3")){
                        if(XPathUtils.nodeExists(node, "./idf:addressIndividualName")){
                            title = getIndividualName(XPathUtils.getString(node, "./idf:addressIndividualName").trim());
                        }else if(XPathUtils.nodeExists(node, "./idf:addressOrganisationName")){
                            title = XPathUtils.getString(node, "./idf:addressOrganisationName").trim();
                        }
                    }else{
                        if(XPathUtils.nodeExists(node, "./idf:addressOrganisationName")){
                            title = XPathUtils.getString(node, "./idf:addressOrganisationName").trim();
                        }
                    }
                }
                
                HashMap link = new HashMap();
                link.put("hasLinkIcon", new Boolean(true));
                link.put("isExtern", new Boolean(false));
                link.put("title", title);
                if(this.iPlugId != null){
                    if(uuid != null){
                        if(isObject){
                            link.put("href", "?cmd=doShowObjectDetail&docuuid="+uuid+"&plugid="+this.iPlugId);
                        }else{
                            link.put("href", "?cmd=doShowDocument&docuuid="+uuid+"&plugid="+this.iPlugId);
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
