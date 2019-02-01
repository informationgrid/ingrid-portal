/*
 * **************************************************-
 * ingrid-interface-search
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
package de.ingrid.portal.search.net;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

/**
 * Iterates over the result set of an query. The results are stored internally
 * in chunks of 10 results. Only 10 results are fetched at once. If more results
 * are available the ibus will be requeried dynamically.
 * 
 * 
 * @author joachim@wemove.com
 * 
 */
public class IBusQueryResultIterator implements Iterator<IngridHit> {

    private final static Log log = LogFactory.getLog(IBusQueryResultIterator.class);

    private IngridQuery q;
    private String[] requestedFields;
    private IngridHits resultBuffer = null;
    private int resultBufferCursor = 0;
    private long resultCursor = 0;
    private int resultPageCursor = 1;
    private IBus iBus;

    private int pageSize = 10;
    private int startHit = 0;
    private int maxHits = Integer.MAX_VALUE;
    private int startPage = 0;

    public IBusQueryResultIterator(IngridQuery q, String[] requestedFields, IBus iBus) {
        this.q = q;
        this.requestedFields = requestedFields;
        this.iBus = iBus;
    }

    public IBusQueryResultIterator(IngridQuery q, String[] requestedFields, IBus iBus, int pageSize) {
        this.q = q;
        this.requestedFields = requestedFields;
        this.iBus = iBus;
        this.pageSize = pageSize;
    }

    /**
     * Constructs a query iterator.
     * 
     * @param q
     * @param requestedFields
     * @param iBus
     * @param pageSize
     * @param startPage
     *            First page is 0.
     * @param maxHits
     */
    public IBusQueryResultIterator(IngridQuery q, String[] requestedFields, IBus iBus, int pageSize, int startPage, int maxHits) {
        this.q = q;
        this.requestedFields = requestedFields;
        this.iBus = iBus;
        this.pageSize = pageSize;
        this.maxHits = maxHits;
        this.startPage = startPage;
        this.startHit = this.startPage * pageSize;
    }

    @Override
    public boolean hasNext() {
        // make sure we have hits
        if (resultBuffer == null) {
            resultBuffer = fetchHits(resultPageCursor);
        }
        if (resultBuffer.getHits() != null && resultBufferCursor >= resultBuffer.getHits().length && (resultCursor + this.startHit) < resultBuffer.length() && resultCursor < maxHits) {
            resultPageCursor++;
            resultBuffer = fetchHits(resultPageCursor);
            resultBufferCursor = 0;
        }
        if ((resultCursor + this.startHit) < resultBuffer.length() && resultBuffer.length() > 0 && resultCursor < maxHits) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public IngridHit next() {
        IngridHit result = null;

        if (hasNext()) {

            // check for no more hits
            if (resultBuffer.getHits().length == 0 || resultCursor >= resultBuffer.length()) {
                log.error("No results detected anymore.");
                throw new NoSuchElementException();
            }

            result = resultBuffer.getHits()[resultBufferCursor];
            resultBufferCursor++;
            resultCursor++;
        }

        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public Long getTotalResults() {
        return resultBuffer.length();
    }

    public void cleanup() {
        resultBuffer = null;
        resultBufferCursor = 0;
        resultCursor = 0;
    }

    private IngridHits fetchHits(int currentPage) {
        IngridHits result = null;
        try {
            Long startTimer = 0L;
            if (log.isDebugEnabled()) {
                startTimer = System.currentTimeMillis();
            }

            if (log.isDebugEnabled()) {
                log.debug("Executed InGrid Query (query: " + q.toString() + ", pageSize:" + pageSize + ", currentPage:" + currentPage + ", startHit:" + ((currentPage - 1) * pageSize + startHit) + ", timeout:"
                        + 30000);
            }
            result = iBus.searchAndDetail(q, pageSize, this.startPage + currentPage, (currentPage - 1) * pageSize + startHit, 30000,
                    this.requestedFields);
            if (log.isDebugEnabled()) {
                log.debug("Executed InGrid Query (pageSize:" + pageSize + ", startHit:" + ((currentPage - 1) * pageSize + startHit) + ", timeout:"
                        + 30000 + ") within " + (System.currentTimeMillis() - startTimer) + "ms returning "
                        + (result.getHits() == null ? 0 : result.getHits().length) + " hits out of max " + result.length() + " hits.");
            }
        } catch (Exception e) {
            log.error("Error querying ibus.", e);
        }
        return result;
    }

}
