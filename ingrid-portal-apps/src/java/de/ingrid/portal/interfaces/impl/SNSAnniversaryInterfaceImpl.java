/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
package de.ingrid.portal.interfaces.impl;

import de.ingrid.iplug.sns.utils.DetailedTopic;
import de.ingrid.iplug.sns.utils.Topic;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.interfaces.AnniversaryInterface;
import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.IDataTypes;
import de.ingrid.utils.queryparser.QueryStringParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class SNSAnniversaryInterfaceImpl implements AnniversaryInterface {

    private static final Logger log = LoggerFactory.getLogger(SNSAnniversaryInterfaceImpl.class);

    private static AnniversaryInterface instance = null;

    public static synchronized AnniversaryInterface getInstance() {
        if (instance == null) {
            try {
                instance = new SNSAnniversaryInterfaceImpl();
            } catch (Exception e) {
                log.error("Error initiating the WMS interface.", e);
            }
        }
        return instance;
    }

    private SNSAnniversaryInterfaceImpl() {
        super();
    }

    /**
     * @see de.ingrid.portal.interfaces.SimilarTermsInterface#getAnniversary(java.sql.Date)
     */
    public IngridHitDetail[] getAnniversaries(Date d, String lang) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = df.format(d);

        // ask ibus for anniversary
        IngridQuery query;
        IngridHits hits = null;
        try {
            query = QueryStringParser.parse(dateStr);
            query.remove(IngridQuery.ORIGIN);
            query.addField(new FieldQuery(true, false, Settings.QFIELD_DATATYPE, IDataTypes.SNS));
            query.addField(new FieldQuery(true, false, Settings.QFIELD_LANG, lang));
            query.putInt(Topic.REQUEST_TYPE, Topic.ANNIVERSARY_FROM_TOPIC);

            IBUSInterface iBus = IBUSInterfaceImpl.getInstance();
            hits = iBus.searchAndDetail(query, PortalConfig.getInstance().getInt( PortalConfig.SNS_CHRONICLE_HITS_LENGTH, 10 ), 1, 0, 10000, new String[0]);

            // prepare the detail array
            IngridHit[] hitArray = hits.getHits();
            IngridHitDetail[] hitDetails = new IngridHitDetail[hitArray.length];
            int i=0;
            for (IngridHit hit : hitArray) {
            	hitDetails[i++] = hit.getHitDetail();
			}

            if (hitArray.length > 0) {
                return hitDetails;
            }
            return new DetailedTopic[0];
        } catch (Exception e) {
            log.error("Exception while querying sns for anniversary. iBus results (hits) = " + hits, e);
            return new DetailedTopic[0];
        }
    }

}
