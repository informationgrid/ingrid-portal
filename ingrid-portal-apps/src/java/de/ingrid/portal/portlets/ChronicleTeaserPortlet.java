package de.ingrid.portal.portlets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

import de.ingrid.iplug.PlugDescription;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.IDataTypes;
import de.ingrid.utils.queryparser.QueryStringParser;

public class ChronicleTeaserPortlet extends AbstractVelocityMessagingPortlet {

    private final static Log log = LogFactory.getLog(ChronicleTeaserPortlet.class);

    public void init(PortletConfig config) throws PortletException {
        // set our message "scope" for inter portlet messaging
        //        setTopic(Settings.MSG_TOPIC_SERVICE);

        super.init(config);
    }

    public void doView(javax.portlet.RenderRequest request, javax.portlet.RenderResponse response)
            throws PortletException, IOException {
/*
        Context context = getContext(request);

        String q = "1978-07-30";
        IngridQuery query = null;
        try {
            query = QueryStringParser.parse(q);
            query.setDataType(IDataTypes.SNS);
            query.putInt(Topic.REQUEST_TYPE, Topic.ANNIVERSARY_FROM_TOPIC);            
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems fetching result details or iPlug", ex);
            }
        }
        
        int SEARCH_REQUESTED_START_HIT = 1;
        int SEARCH_REQUESTED_NUM_HITS = 10;

        Topic snsTopic = null;
        try {
            snsTopic = doSNSSearch(query, SEARCH_REQUESTED_START_HIT, SEARCH_REQUESTED_NUM_HITS);
        } catch (Exception ex) {
            if (log.isErrorEnabled()) {
                log.error("Problems fetching sns Topic", ex);
            }                
        }

        context.put("snsTopic", snsTopic);
*/
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException,
            IOException {
    }
    
    private Topic doSNSSearch(IngridQuery query, int startHit, int numHits) {

        int SEARCH_TIMEOUT = 2000;

        Topic result = null;

        try {
            IBUSInterface ibus = IBUSInterfaceImpl.getInstance(); 
            IngridHits hits = ibus.search(query, numHits, startHit, numHits, SEARCH_TIMEOUT);
            IngridHit[] results = hits.getHits();

            String plugId = null;
            PlugDescription plug = null;
            for (int i = 0; i < results.length; i++) {
                plug = null;
                try {
                    result = (Topic) results[i];
                    plugId = result.getPlugId();
                    plug = ibus.getIPlug(plugId);
                } catch (Throwable t) {
                    if (log.isErrorEnabled()) {
                        log.error("Problems fetching result details or iPlug", t);
                    }
                }
                if (result == null) {
                    continue;
                }
            }
        } catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Problems performing Search !", t);
            }
        }

        return result;
    }

}