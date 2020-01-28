/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
    
    private static final String FACETS = "FACETS";
	
	public IngridHitsWrapper() {
		
	}
	
	public IngridHitsWrapper(IngridHits ingridHits) {
		setHits(ingridHits.getHits());
		setLength(ingridHits.length());
		putBoolean(RANKED, ingridHits.isRanked());
		putInt(GROUPED_HITS_LENGTH, ingridHits.getGoupedHitsLength());
		setInVolvedPlugs(ingridHits.getInVolvedPlugs());
		setPlugId(ingridHits.getPlugId());
		put(FACETS, ingridHits.get(FACETS));
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
    
    public IngridHitWrapper[] getWrapperHits() {
    	return (IngridHitWrapper[]) getArray(HITS);
    }
    
    /*
    * @return number of all hits found for the search request Attention is not
    *         equals to number of <code>Hit</code>s stored in this
    *         container, if this is not setted we return 0;
    */
   @Override
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
   @Override
   public String getPlugId() {
       return (String) get(IPLUG_ID);
   }

   /**
    * @param id
    */
   @Override
   public void setPlugId(String id) {
       put(IPLUG_ID, id);
   }

   /**
    * @return true if hits are ranked.
    */
   @Override
   public boolean isRanked() {
       if (get(RANKED) != null) {
           return getBoolean(RANKED);
       }
       return false;
   }
   
   /**
    * @param ranked
    */
   @Override
   public void setRanked(boolean ranked) {
       putBoolean(RANKED,ranked);
   }

   /**
    * @return the number of hits which has been used by grouping to create this
    *         hit container
    */
   @Override
   public int getGoupedHitsLength() {
       if (containsKey(GROUPED_HITS_LENGTH)) {
           return getInt(GROUPED_HITS_LENGTH);
       }
       return 0;
   }

   /**
    * @return the number of different iplugs the hits come from
    */
   @Override
   public int getInVolvedPlugs() {
       if (containsKey(INVOLVED_PLUGS)) {
           return getInt(INVOLVED_PLUGS);
       }
       return 0;
   }

   /**
    * @param howMany
    */
   @Override
   public void setInVolvedPlugs(int howMany) {
       putInt(INVOLVED_PLUGS, howMany);
   }
}
