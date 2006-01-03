/*
 * Created on 03.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search.mockup;

import java.util.Vector;

/**
 * @author joachim
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimilarNodeFactoryMockup {

	public static SimilarNodeMockup getSimilarNodes() {
		SimilarNodeMockup root = new SimilarNodeMockup("1", "root", false);
		
		SimilarNodeMockup n;
		SimilarNodeMockup n1;
		
		
		n = new SimilarNodeMockup("2", "Luft", false);

		n1 = new SimilarNodeMockup("3", "Abgasemission", false);
		n.addChild(n1);
		n1 = new SimilarNodeMockup("4", "Bodenluft", false);
		n.addChild(n1);
		n1 = new SimilarNodeMockup("5", "Druckluft", false);
		n.addChild(n1);

		root.addChild(n);

		n = new SimilarNodeMockup("6", "Gesundheit", false);

		n1 = new SimilarNodeMockup("7","Gesundheitsrisiko",false);
		n.addChild(n1);
		n1 = new SimilarNodeMockup("8","Apostel",false);
		n.addChild(n1);

		root.addChild(n);
		
		return root;
	}

	/**
	 * @param object
	 * @param parameter
	 * @param b
	 */
	public static void setOpen(SimilarNodeMockup n, String nodeId, boolean open) {
		
		Vector c = n.getChildren();
		for (int i=0; i<c.size(); i++) {
			if (((SimilarNodeMockup)c.get(i)).getId().equals(nodeId)) {
				((SimilarNodeMockup)c.get(i)).setOpen(open);
				break;
			}
			setOpen(((SimilarNodeMockup)c.get(i)), nodeId, open);
		}
	}
	
}
