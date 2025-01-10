/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.global;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;

public class IngridHitWrapper extends IngridHit {
	private static final String PARAM_INGRID_HIT = "ingrid_hit";
	
	private static final String PARAM_INGRID_DETAIL = "ingrid_hit_detail";

	public IngridHitWrapper() { }

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
	
	@Override
    public void putBoolean(String key, boolean value) {
        put(key, value);
    }
    
	@Override
    public boolean getBoolean(String key) {
        Boolean booleanObj = (Boolean) get(key);
        if (booleanObj == null) {
            throw new IllegalArgumentException("unknown key");
        }
        return booleanObj.booleanValue();
    }
    
	@Override
    public Object get( Object key ) {
    	Object obj = null;
    	obj = super.get(key);
    	
    	if (obj == null) {
    		obj = getHit().get(key);
    	}
    	return obj;
    }
	
}
