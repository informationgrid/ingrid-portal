/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
