/*
 * Created on 05.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search;

import java.io.Serializable;

import de.ingrid.utils.IngridDocument;

/**
 * @author joachim
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PageState extends IngridDocument {
	
	
	/**
	 * 
	 */
	public PageState() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param id
	 * @param content
	 */
	public PageState(Serializable id, Serializable content) {
		super(id, content);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 */
	public PageState(String pageName) {
		super(new Long(System.currentTimeMillis()), pageName);
	}
	
    /**
     * Puts a String value.
     * 
     * @param key
     * @param value
     */
    public void putString(Object key, String value) {
        put(key, value);

    }

    /**
     * Use this method to retrieve String values. This saves 
     * the Casting.
     * 
     * @param key
     * @return a String value for a given key
     */
    public String getString(Object key) {
        if (get(key) == null)
        	return null;
    	try {
        	return (String) get(key);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("value to key is not String");
        }
    }	
}
