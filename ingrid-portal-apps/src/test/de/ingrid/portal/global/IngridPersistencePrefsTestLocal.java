package de.ingrid.portal.global;

import java.util.HashMap;

import junit.framework.TestCase;

public class IngridPersistencePrefsTestLocal extends TestCase {

    /*
     * Test method for 'de.ingrid.portal.global.IngridPersistencePrefs.setPref(Long, String, Object)'
     */
    public void testSetPref() throws Exception {
        HashMap val = new HashMap();
        val.put("testentry", "testvalue");
        IngridPersistencePrefs.setPref(new Long(1),"testpref",val);
        Object obj = IngridPersistencePrefs.getPref(new Long(1), "testpref");
        assertTrue(obj instanceof HashMap);
        assertTrue(((String)((HashMap)obj).get("testentry")).equals("testvalue"));
    }

}
