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
