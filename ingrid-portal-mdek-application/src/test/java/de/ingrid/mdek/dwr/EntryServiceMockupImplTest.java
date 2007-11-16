package de.ingrid.mdek.dwr;

import java.util.List;

import junit.framework.TestCase;

public class EntryServiceMockupImplTest extends TestCase {

	public void testGetSubTree() throws Exception {
		EntryServiceMockupImpl e = new EntryServiceMockupImpl();
		List l = e.getSubTree(null, null, 1);
		assertEquals(l.size() > 0, true);
	}

}
