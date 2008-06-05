package de.ingrid.portal.search.detail;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Inner class: AddressTypeComperator for address sorting;
 *
 * @author joachim@wemove.com
 */
public class AddressTypeComparator implements Comparator {

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public final int compare(Object a, Object b) {
        int ia;
        int ib;
        try {
            ia = Integer.parseInt((String) ((HashMap) a).get("t012_obj_adr.typ"));
            ib = Integer.parseInt((String) ((HashMap) b).get("t012_obj_adr.typ"));
        } catch (Exception e) {
            return 0;
        }

        if (ia > ib)
            return 1;
        else if (ia < ib)
            return -1;
        else
            return 0;
    }
}
