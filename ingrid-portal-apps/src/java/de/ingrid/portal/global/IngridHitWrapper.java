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
package de.ingrid.portal.global;

import java.util.HashMap;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;

// FIXME AW: extend from IngridDocument!?
public class IngridHitWrapper extends IngridHit {
	private static final String PARAM_INGRID_HIT = "ingrid_hit";
	
	private static final String PARAM_INGRID_DETAIL = "ingrid_hit_detail";

	public IngridHitWrapper(IngridHit hit) {
		put(PARAM_INGRID_HIT, hit);
	}
	
	public IngridHit getHit() {
		return (IngridHit)get(PARAM_INGRID_HIT);
	}
	
	public IngridHitDetail getDetail() {
		return (IngridHitDetail)get(PARAM_INGRID_DETAIL);
	}
	
	public void setDetail(IngridHitDetail detail) {
		put(PARAM_INGRID_DETAIL, detail);
	}
	
	public int getSize() {
		return size();
	}
	
    public void putBoolean(String key, boolean value) {
        put(key, new Boolean(value));
    }
    
    public boolean getBoolean(String key) {
        Boolean booleanObj = (Boolean) get(key);
        if (booleanObj == null) {
            throw new IllegalArgumentException("unknown key");
        }
        return booleanObj.booleanValue();
    }
    
    public Object get( Object key ) {
    	Object obj = null;
    	obj = super.get(key);
    	
    	if (obj == null) {
    		obj = getHit().get(key);
    	}
    	return obj;
    }
	
}
