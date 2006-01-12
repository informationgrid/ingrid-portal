/*
 * Created on 03.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search.mockup;

import java.util.Vector;

import de.ingrid.portal.search.SimilarTreeNode;

/**
 * @author joachim
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimilarNodeFactoryMockup {

	public static SimilarTreeNode getSimilarNodes() {
		SimilarTreeNode root = new SimilarTreeNode("1", "root", false, 0);
		
		SimilarTreeNode n;
		SimilarTreeNode n1;
		
		
		n = new SimilarTreeNode("2", "Luft", false, 1);

		n1 = new SimilarTreeNode("3", "Abgasemission", false, 2);
		n.addChild(n1);
		n1 = new SimilarTreeNode("4", "Bodenluft", false, 2);
		n.addChild(n1);
		n1 = new SimilarTreeNode("5", "Druckluft", false, 2);
		n.addChild(n1);

		root.addChild(n);

		n = new SimilarTreeNode("6", "Gesundheit", false, 1);

		n1 = new SimilarTreeNode("7","Gesundheitsrisiko",false, 2);
		n.addChild(n1);
		n1 = new SimilarTreeNode("8","Apostel",false, 2);
		n.addChild(n1);

		root.addChild(n);
		
		return root;
	}

	/**
	 * @param object
	 * @param parameter
	 * @param b
	 */
	public static void setOpen(SimilarTreeNode n, String nodeId, boolean open) {
		
		Vector c = n.getChildren();
		for (int i=0; i<c.size(); i++) {
			if (((SimilarTreeNode)c.get(i)).getId().equals(nodeId)) {
				((SimilarTreeNode)c.get(i)).setOpen(open);
				break;
			}
			setOpen(((SimilarTreeNode)c.get(i)), nodeId, open);
		}
	}
	
}
