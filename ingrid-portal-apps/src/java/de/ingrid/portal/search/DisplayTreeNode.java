/*
 * Created on 02.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author joachim
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DisplayTreeNode extends HashMap {

    private static final long serialVersionUID = 76464278469123L;

    public static final int ROOT = 0;
    public static final int GENERIC = 1;
    public static final int SEARCH_TERM = 2;
    public static final int SNS_TERM = 3;
    public static final int MESSAGE_NODE = 4;

	private boolean isOpen = false;
    private boolean isLoading = false;
    private int type;

	/**
	 * @param n
	 */
	public void addChild(DisplayTreeNode n) {
        getChildren().add(n);
	}

	/**
	 * @param name
	 * @param isOpen
	 */
	public DisplayTreeNode(String id, String name, boolean isOpen) {
		super();
		this.put("name", name);
        this.put("isOpen", new Boolean(isOpen));
        this.put("id", id);
	}

	/**
	 * @return Returns the isOpen.
	 */
	public boolean isOpen() {
        return this.isOpen;
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
		return (String)this.get("name");
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
        this.put("name", name);
	}

	/**
	 * @return Returns the children.
	 */
	public ArrayList getChildren() {
        if (get("children") == null) {
            put("children", new ArrayList());
        }
		return (ArrayList)get("children");
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return (String)this.get("id");
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(String id) {
        this.put("id", id);
	}

    public DisplayTreeNode getChild(String nodeId) {
        return getChild(this, nodeId);
    }
    
    private static DisplayTreeNode getChild(DisplayTreeNode node, String nodeId) {
        ArrayList c = node.getChildren();
        for (int i=0; i<c.size(); i++) {
            DisplayTreeNode aNode = (DisplayTreeNode)c.get(i);
            if (!aNode.getId().equals(nodeId)) {
                aNode = getChild(aNode, nodeId);
            }
            if (aNode != null) {
                return aNode;
            }
        }
        return null;
    }

    public ArrayList getAllChildren() {
        return getAllChildren(this);
    }
    
    private static ArrayList getAllChildren(DisplayTreeNode node) {
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
        return (DisplayTreeNode)this.get("parent");
    }

    /**
     * @param parent The parent to set.
     */
    public void setParent(DisplayTreeNode parent) {
        this.put("parent", parent);
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

    /**
     * ONLY USE ON ROOT NODE !!! Id is stored in node !
     */
    synchronized public int getNextId() {
    	String KEY_NEXT_ID = "nextId";
        Integer nextId = (Integer) this.get(KEY_NEXT_ID);
        if (nextId == null) {
        	nextId = new Integer(0);
        }

    	this.put(KEY_NEXT_ID, new Integer(nextId.intValue() + 1));
    	
    	return nextId.intValue();
    }
}
