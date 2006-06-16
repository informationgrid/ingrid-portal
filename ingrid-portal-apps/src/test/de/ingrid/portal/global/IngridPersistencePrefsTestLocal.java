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
