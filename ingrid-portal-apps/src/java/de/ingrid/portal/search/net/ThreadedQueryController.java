/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.search.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.portal.global.IngridHitsWrapper;

/**
 * Controller class for the threaded search.
 * 
 * @author joachim@wemove.com
 */
public class ThreadedQueryController {

    private final static Logger log = LoggerFactory.getLogger(ThreadedQueryController.class);

    private Object threadMonitor;

    private HashMap ingridQueryDescriptors = new HashMap();

    private HashMap ingridResults = new HashMap();

    private int timeout = 5000;

    /**
     * Adds a query represented by a id. The id is used for result retrieval.
     * 
     * @param queryId
     *            The id used for this query.
     * @param queryDescriptor
     *            The descriptor describing the query.
     */
    public void addQuery(String queryId, QueryDescriptor queryDescriptor) {
        ingridQueryDescriptors.put(queryId, queryDescriptor);
    }

    /**
     * Fires all queries to the ibus at ones! Returns a HashMap with the search
     * results. The search keys of the HashMap correspond to the keys of the
     * queries.
     * 
     * @return Returns a HashMap with the search results.
     */
    public HashMap search() {

        ArrayList mySearches = new ArrayList();
        threadMonitor = new Object();
        Iterator it;

        try {
            it = ingridQueryDescriptors.keySet().iterator();
            while (it.hasNext()) {
                String myKey = (String) it.next();
                QueryDescriptor queryDescriptor = (QueryDescriptor) ingridQueryDescriptors.get(myKey);
                ThreadedQuery search = new ThreadedQuery(myKey, queryDescriptor, this);
                search.start();
                mySearches.add(search);
            }

            synchronized (threadMonitor) {
                threadMonitor.wait(timeout);
            }

            it = mySearches.iterator();
            while (it.hasNext()) {
                ThreadedQuery myThread = (ThreadedQuery) it.next();
                if (myThread.isAlive() && !myThread.isInterrupted()) {
                    myThread.interrupt();
                    if (log.isDebugEnabled()) {
                        log.debug("Interrupt query thread: " + myThread.getKey());
                    }
                }
            }
            mySearches.clear();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return ingridResults;
    }

    /**
     * Clears all queries and results. Makes the Object ready for new queries.
     * 
     */
    public void clear() {
        this.ingridQueryDescriptors.clear();
        this.ingridResults.clear();
    }

    /**
     * This method is called by the ThreadedQuery class only. It reports search
     * results back to the controller object. If all queries are reported it
     * notifies the thread monitor.
     * 
     * @param key
     *            The key of the search
     * @param hits
     *            The IngridHits (results).
     */
    protected synchronized void addResultSet(String key, IngridHitsWrapper hits) {
        ingridResults.put(key, hits);
        if (ingridQueryDescriptors.size() == ingridResults.size()) {
            synchronized (threadMonitor) {
                threadMonitor.notify();
            }
        }
    }

    /**
     * @return Returns the timeout of the threaded queries.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout
     *            The timeout to set of the threaded queries.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Returns true if the controller has queries, false if not.
     * 
     * @return Returns true if the controller has queries, false if not.
     */
    public boolean hasQueries() {
        return ingridQueryDescriptors.size() > 0;
    }

}
