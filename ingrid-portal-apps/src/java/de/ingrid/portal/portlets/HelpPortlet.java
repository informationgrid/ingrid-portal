/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.portlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.ingrid.portal.hibernate.HibernateManager;
import de.ingrid.portal.om.IngridHelpContent;
import de.ingrid.portal.om.IngridHelpStructure;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class HelpPortlet extends GenericVelocityPortlet {

    private final static Log log = LogFactory.getLog(HelpPortlet.class);

    HibernateManager fHibernateManager = null;

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        Context context = getContext(request);
        Session session = this.fHibernateManager.getSession();

        boolean useKey = true;
        
        String helpKey = request.getParameter("hkey");
        Long helpId = null;
        if (helpKey == null) {
            String helpIdStr = request.getParameter("hid");
            if (helpIdStr != null) {
                helpId = Long.decode(helpIdStr);
                useKey = false;
            } else {
                helpKey="index";
            }
        }
        
        // get help id from help key
        IngridHelpStructure requestedHelpEntry = null;
        if (useKey) {
            // get help id
            List l = session.createCriteria(IngridHelpStructure.class).add(
                    Restrictions.eq("helpKey", helpKey))
                    .list();
            requestedHelpEntry = (IngridHelpStructure)l.get(0);
        } else {
            requestedHelpEntry = (IngridHelpStructure) session.load(IngridHelpStructure.class, helpId);
        }
        
        // find parent document if this help id
        IngridHelpStructure rootHelpEntry = getRootHelpEntry(requestedHelpEntry);
        
        // build content structure for the requested help id
        String language = request.getLocale().getLanguage();
        HashMap helpContent = new HashMap();
        helpContent.put("structure", rootHelpEntry);
        helpContent.put("content", getHelpContent(rootHelpEntry, language));
        helpContent.put("children", getHelpContentChildren(rootHelpEntry, language));
        
        context.put("help", helpContent);
        
        super.doView(request, response);
    }

    private ArrayList getHelpContentChildren(IngridHelpStructure parentEntry, String language) {
        Session session = this.fHibernateManager.getSession();
        ArrayList result = new ArrayList();
        
        // get all children of parent
        List l = session.createCriteria(IngridHelpStructure.class)
        .add(Restrictions.eq("parentId", parentEntry.getId()))
        .list();
        
        Iterator it = l.iterator();
        while(it.hasNext()) {
            HashMap myHelpEntryHash = new HashMap();
            IngridHelpStructure myEntry = (IngridHelpStructure)it.next();
            myHelpEntryHash.put("structure", myEntry);
            myHelpEntryHash.put("content", getHelpContent(myEntry, language));
            ArrayList children = getHelpContentChildren(myEntry, language);
            if (children.size() > 0) {
                myHelpEntryHash.put("children", children);
            }
            result.add(myHelpEntryHash);
        }
        
        return result;
    }

    private IngridHelpStructure getRootHelpEntry(IngridHelpStructure helpEntry) {
        Session session = this.fHibernateManager.getSession();
        
        if (helpEntry.getParentId() == null || helpEntry.getParentId().intValue() == 0) {
            return helpEntry;
        }
        
        IngridHelpStructure parent = (IngridHelpStructure) session.load(IngridHelpStructure.class, helpEntry.getParentId());
        if (parent == null) {
            return helpEntry;
        } else {
            return getRootHelpEntry(parent);
        }
    }
    
    private IngridHelpContent getHelpContent(IngridHelpStructure helpEntry, String language) {
        Session session = this.fHibernateManager.getSession();
        
        List l = session.createCriteria(IngridHelpContent.class)
        .add(Restrictions.eq("helpId", helpEntry.getId()))
        .add(Restrictions.eq("language", language))
        .list();
        
        if (l.isEmpty()) {
            log.info("No help entry found. (helpid: " + helpEntry.getId() + ", language: " + language + " )");
            l = session.createCriteria(IngridHelpContent.class)
            .add(Restrictions.eq("helpId", helpEntry.getId()))
            .add(Restrictions.eq("language", "de"))
            .list();
        }
        
        return (IngridHelpContent)l.get(0);
    }

    /**
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        fHibernateManager = HibernateManager.getInstance();
    }
    
    

}
