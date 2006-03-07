/*
 * Created on 02.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author joachim
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DisplayTreeNode implements Serializable {

    private static final long serialVersionUID = 76464278469123L;

    
    public static final int ROOT = 0;
    public static final int GENERIC = 1;
    public static final int SEARCH_TERM = 2;
    public static final int SNS_TERM = 3;
    
	
	/**
	 * TODO: Comment for <code>name</code>
	 */
	private String name;

	/**
	 * TODO: Comment for <code>isOpen</code>
	 */
	private boolean isOpen = false;

    /**
     * TODO: Comment for <code>isLoading</code>
     */
    private boolean isLoading = false;

	/**
	 * TODO: Comment for <code>children</code>
	 */
	private ArrayList children = new ArrayList();

	/**
	 * TODO: Comment for <code>id</code>
	 */
	private String id;

	/**
	 * TODO: Comment for <code>depth</code>
	 */
	private int depth;
    
    private int type;
    
    private DisplayTreeNode parent;


	/**
	 * @param n
	 */
	public void addChild(DisplayTreeNode n) {
		children.add(n);
	}

	public DisplayTreeNode(String id, String name, boolean isOpen, int depth) {
		this(id, name, isOpen);
		this.depth = depth;
	}
	/**
	 * @param name
	 * @param isOpen
	 */
	public DisplayTreeNode(String id, String name, boolean isOpen) {
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
	public ArrayList getChildren() {
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

	/**
	 * @return Returns the depth.
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @param depth The depth to set.
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(int type) {
        this.type = type;
    }

    public DisplayTreeNode getChild(String nodeId) {
        return getChild(this, nodeId);
    }
    
    private DisplayTreeNode getChild(DisplayTreeNode node, String nodeId) {
        ArrayList c = node.getChildren();
        for (int i=0; i<c.size(); i++) {
            DisplayTreeNode aNode = (DisplayTreeNode)c.get(i);
            if (aNode.getId().equals(nodeId)) {
                return aNode;
            }
            getChild(aNode, nodeId);
        }
        return null;
    }

    public ArrayList getAllChildren() {
        return getAllChildren(this);
    }
    
    private ArrayList getAllChildren(DisplayTreeNode node) {
        ArrayList result = new ArrayList();
        Iterator it = node.getChildren().iterator();
        while (it != null && it.hasNext()) {
            result.addAll(getAllChildren((DisplayTreeNode) it.next()));
        }
        result.add(node);
        return result;
    }
    
    /**
     * @return Returns the parent.
     */
    public DisplayTreeNode getParent() {
        return parent;
    }

    /**
     * @param parent The parent to set.
     */
    public void setParent(DisplayTreeNode parent) {
        this.parent = parent;
    }

    /**
     * @return Returns the isLoading.
     */
    public boolean isLoading() {
        return isLoading;
    }

    /**
     * @param isLoading The isLoading to set.
     */
    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }
	
}
