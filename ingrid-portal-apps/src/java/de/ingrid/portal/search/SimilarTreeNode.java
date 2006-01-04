/*
 * Created on 02.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search;

import java.util.Vector;

/**
 * @author joachim
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SimilarTreeNode {

	/**
	 * TODO: Comment for <code>name</code>
	 */
	private String name;

	/**
	 * TODO: Comment for <code>isOpen</code>
	 */
	private boolean isOpen = false;

	/**
	 * TODO: Comment for <code>children</code>
	 */
	private Vector children = new Vector();

	/**
	 * TODO: Comment for <code>id</code>
	 */
	private String id;



	/**
	 * @param n
	 */
	public void addChild(SimilarTreeNode n) {
		children.add(n);
	}

	/**
	 * @param name
	 * @param isOpen
	 */
	public SimilarTreeNode(String id, String name, boolean isOpen) {
		super();
		this.name = name;
		this.isOpen = isOpen;
		this.id = id;
	}

	/**
	 * @return Returns the isOpen.
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * @param isOpen
	 *            The isOpen to set.
	 */
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the children.
	 */
	public Vector getChildren() {
		return children;
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
}
