/*
 * Created on 02.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search.mockup;

import java.util.Vector;

import org.apache.xml.utils.UnImplNode;

/**
 * @author joachim
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimilarNodeMockup {
	
	private String name;
	private boolean isOpen = false;
	private Vector children = new Vector();
	private String id;
	
	
	public void addChild(SimilarNodeMockup n) {
		children.add(n);
	}
	
	
	/**
	 * @param name
	 * @param isOpen
	 */
	public SimilarNodeMockup(String id, String name, boolean isOpen) {
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
	 * @param isOpen The isOpen to set.
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
	 * @param name The name to set.
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
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
}
