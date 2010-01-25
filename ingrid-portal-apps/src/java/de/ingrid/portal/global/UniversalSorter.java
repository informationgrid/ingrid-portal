package de.ingrid.portal.global;

import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.PropertyUtils;

public class UniversalSorter {
    private Locale _lang;
    
    public UniversalSorter(Locale lang) {
        _lang = lang;
    }
    
    @SuppressWarnings("unchecked")
    public Collection sort(List list, String property) {
        //return 
        Collections.sort(list, new GermanLangComparator(property, _lang));
        return list;
    }
    
    public class GermanLangComparator implements Comparator {
        private String _field;
        private Locale _lang;
        
        public GermanLangComparator(String field, Locale lang) {
            _field = field;
            _lang  = lang;
        }

        public final int compare(Object a, Object b) {
            try {
                String aField = (String)PropertyUtils.getProperty(a, _field);
                String bField = (String)PropertyUtils.getProperty(b, _field);
                
                // Get the collator for the German Locale 
                Collator gerCollator = Collator.getInstance(_lang);
                return gerCollator.compare(aField, bField);
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
