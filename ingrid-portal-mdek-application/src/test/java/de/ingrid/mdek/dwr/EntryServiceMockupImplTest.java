package de.ingrid.mdek.dwr;

import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

public class EntryServiceMockupImplTest extends TestCase {

	public void testGetSubTree() throws Exception {
		EntryServiceMockupImpl e = new EntryServiceMockupImpl();
		List l = e.getSubTree(null, null, 2);
		assertEquals(l.size() > 0, true);
		assertEquals(((List)((HashMap)l.get(0)).get("children")).size() > 0, true);
	}

}
