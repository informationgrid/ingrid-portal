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
package de.ingrid.portal.search;

import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.query.TermQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;
import junit.framework.TestCase;

public class IngridQueryUtilsTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testRenderQuery() throws ParseException {
        
        IngridQuery q = QueryStringParser.parse("(gem\u00fcse OR garten T02:test) wemove T01:alles datatype:default ranking:score");
        String qStr = UtilsSearch.queryToString(q);
        assertEquals("wemove (gem\u00fcse OR garten t02:test) t01:alles datatype:default ranking:score", qStr);
        
    }

}
