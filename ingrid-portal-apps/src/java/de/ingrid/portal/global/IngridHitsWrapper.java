package de.ingrid.portal.global;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;

public class IngridHitsWrapper extends IngridHits {
	
	private static final long serialVersionUID = 5L;

    private static final String LENGTH = "length";

    private static final String HITS = "hits";

    private static final String IPLUG_ID = "iPlugId";

    private static final String RANKED = "ranked";

    private static final String GROUPED_HITS_LENGTH = "groupedHitsLength";

    private static final String INVOLVED_PLUGS = "involvedPlugs";
	
	public IngridHitsWrapper() {
		
	}
	
	public IngridHitsWrapper(IngridHits ingridHits) {
		setHits(ingridHits.getHits());
		setLength(ingridHits.length());
		putBoolean(RANKED, ingridHits.isRanked());
		putInt(GROUPED_HITS_LENGTH, ingridHits.getGoupedHitsLength());
		setInVolvedPlugs(ingridHits.getInVolvedPlugs());
		setPlugId(ingridHits.getPlugId());
	}
	
	
	/**
     * @param hits
     */
    private void setHits(IngridHit[] hits) {
    	IngridHitWrapper[] hitsWrapper = new IngridHitWrapper[hits.length];
    	
    	int i = 0;
    	for (IngridHit iH : hits) {
    		hitsWrapper[i++] = new IngridHitWrapper(iH);
    	}
        setArray(HITS, hitsWrapper);
    }
    
    /**
     * @param hits
     */
    private void setWrapperHits(IngridHitWrapper[] hitsWrapper) {
    	setArray(HITS, hitsWrapper);
    }
    
    public IngridHitWrapper[] getWrapperHits() {
    	return (IngridHitWrapper[]) getArray(HITS);
    }
    
    /*
    public IngridHit[] getWrapperHitsAsIngridHits() {
    	IngridHitWrapper[] hitWrapper = (IngridHitWrapper[]) getArray(HITS);
    	// FIXME AW: value hard encoded
    	IngridHit[] hit = new IngridHit[10];
    	int i = 0;
    	for (IngridHitWrapper hitW : hitWrapper) {
    		hit[i++] = hitW.getHit();
    	}
    	return hit;
    }*/
    
    /*
    * @return number of all hits found for the search request Attention is not
    *         equals to number of <code>Hit</code>s stored in this
    *         container, if this is not setted we return 0;
    */
   public long length() {
       if (get(LENGTH) != null) {
           return getLong(LENGTH);
       }
       return 0;
   }
   
   /**
    * @param length
    */
   private void setLength(long length) {
       putLong(LENGTH, length);
   }

   /**
    * @return the plugId
    */
   public String getPlugId() {
       return (String) get(IPLUG_ID);
   }

   /**
    * @param id
    */
   public void setPlugId(String id) {
       put(IPLUG_ID, id);
   }

   /**
    * @return true if hits are ranked.
    */
   public boolean isRanked() {
       if (get(RANKED) != null) {
           return getBoolean(RANKED);
       }
       return false;
   }
   
   /**
    * @param ranked
    */
   public void setRanked(boolean ranked) {
       putBoolean(RANKED,ranked);
   }

   /**
    * @return the number of hits which has been used by grouping to create this
    *         hit container
    */
   public int getGoupedHitsLength() {
       if (containsKey(GROUPED_HITS_LENGTH)) {
           return getInt(GROUPED_HITS_LENGTH);
       }
       return 0;
   }

   /**
    * @return the number of different iplugs the hits come from
    */
   public int getInVolvedPlugs() {
       if (containsKey(INVOLVED_PLUGS)) {
           return getInt(INVOLVED_PLUGS);
       }
       return 0;
   }

   /**
    * @param howMany
    */
   public void setInVolvedPlugs(int howMany) {
       putInt(INVOLVED_PLUGS, howMany);
   }
}
