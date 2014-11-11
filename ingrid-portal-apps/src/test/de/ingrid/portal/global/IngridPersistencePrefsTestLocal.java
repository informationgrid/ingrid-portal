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
package de.ingrid.portal.global;

import java.util.HashMap;

import junit.framework.TestCase;

public class IngridPersistencePrefsTestLocal extends TestCase {

    public void testPref() throws Exception {
        
        // test inserting a value
        HashMap val = new HashMap();
        val.put("testentry", "testvalue");
        IngridPersistencePrefs.setPref("principal1","testpref",val);
        Object obj = IngridPersistencePrefs.getPref("principal1", "testpref");
        assertTrue(obj instanceof HashMap);
        assertTrue(((String)((HashMap)obj).get("testentry")).equals("testvalue"));
        
        // test updating the value
        val.put("testentry2", "testvalue2");
        IngridPersistencePrefs.setPref("principal1","testpref",val);
        obj = IngridPersistencePrefs.getPref("principal1", "testpref");
        assertTrue(obj instanceof HashMap);
        assertTrue(((String)((HashMap)obj).get("testentry2")).equals("testvalue2"));
        
        // test removing a preference
        IngridPersistencePrefs.removePref("principal1","testpref");
        obj = IngridPersistencePrefs.getPref("principal1", "testpref");
        assertNull(obj);
        
        // test passing a null value: no exception should occur
        // no value should exist in storage 
        IngridPersistencePrefs.setPref("principal1","testpref",null);
        obj = IngridPersistencePrefs.getPref("principal1", "testpref");
        assertNull(obj);
        
    }

}
