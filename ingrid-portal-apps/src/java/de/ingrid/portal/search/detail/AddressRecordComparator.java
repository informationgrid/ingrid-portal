package de.ingrid.portal.search.detail;

import java.util.Comparator;
import java.util.HashMap;

import de.ingrid.portal.global.Settings;

/**
 * Inner class: AddressTypeComperator for address sorting;
 *
 * @author joachim@wemove.com
 */
public class AddressRecordComparator implements Comparator {
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public final int compare(Object a, Object b) {
        String sa;
        String sb;
        try {
            sa = (String) ((HashMap)((HashMap) a).get(Settings.RESULT_KEY_DETAIL)).get("title");
            sb = (String) ((HashMap)((HashMap) b).get(Settings.RESULT_KEY_DETAIL)).get("title");
        } catch (Exception e) {
            return 0;
        }

        return sa.compareTo(sb);
    }
}
