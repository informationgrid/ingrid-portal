package de.ingrid.mdek.dwr;

import junit.framework.TestCase;

public class EntryServiceMockupImplTest extends TestCase {

	public void testGetSubTree() throws Exception {
		EntryServiceMockupImpl e = new EntryServiceMockupImpl();
		e.getSubTree(null, null, 1);
		fail("Not yet implemented");
	}

}
