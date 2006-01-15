/*
 * Copyright (c) 1997-2005 by media style GmbH
 * 
 * $Source: /cvs/asp-search/src/java/com/ms/aspsearch/PermissionDeniedException.java,v $
 */

package de.ingrid.ibus;

import de.ingrid.iplug.IPlug;
import de.ingrid.iplug.PlugDescription;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

public class DummyIPlug implements IPlug {

    public IngridHits search(IngridQuery query, int start, int lenght) {
        
        
        
        IngridHit hit;
        IngridHit[] hitArray = new IngridHit[10];
        
        for (int i=0; i<10; i++) {
            hitArray[i] = new IngridHit("bla", i, 1, (float)(Math.random()));
            hitArray[i].put("title", blindText((int)(Math.random()*20)));
            hitArray[i].put("type", "WEBSITE");
            hitArray[i].put("abstract", blindText((int)(Math.random()*100)));
            hitArray[i].put("provider", "Bundesministerium für Verbraucherschutz, Ernährung und Landwirtschaft (BMVEL)");
            hitArray[i].put("source", "Webseiten");
            hitArray[i].put("url", "http://www.verbraucherministerium.de/index-000068C0A9C31E01BB1301A5C0A8E066.html");
            hitArray[i].put("ranking", "" + Math.round(Math.random() * 100) / 100);
        }
        
        return new IngridHits("bla", 300, hitArray);
    }
    
    private String blindText(int noWords) {
        String[] words = { "lorem", "ipsum", "dolor", "sit", "amet", "consectetuer", "adipiscing", "elit", "sed", "diam", "nonummy", "nibh", "euismod", "tincidunt", "ut", "laoreet", "dolore", "magna", "aliquam", "erat", "volutpat", "wisi", "enim", "ad", "minim", "veniam", "quis", "nostrud", "exerci", "tation", "ullamcorper", "suscipit", "lobortis", "nisl", "aliquip", "ex", "ea", "commodo", "consequat", "duis", "autem", "vel", "eum", "iriure", "dolor", "in", "hendrerit", "vulputate", "velit", "esse", "molestie", "consequat", "illum", "dolore", "eu", "feugiat", "nulla", "faci" };
        
        StringBuffer sb = new StringBuffer();
        
        for (int i=0; i<noWords; i++) {
            if (i!=0) {
                sb.append(" ");  
            }
            sb.append(words[(int)(Math.random()*words.length)]);
        }
        return sb.toString();
    }
    

    public void configure(PlugDescription arg0) throws Exception {
        // TODO Auto-generated method stub
        
    }

}
