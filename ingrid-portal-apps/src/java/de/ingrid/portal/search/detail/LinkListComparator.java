package de.ingrid.portal.search.detail;

import java.util.Comparator;
import java.util.HashMap;

/**
 * LinkListComparator for link list sorting;
 *
 * @author joachim@wemove.com
 */
public class LinkListComparator implements Comparator {
    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public final int compare(Object a, Object b) {
        String sa;
        String sb;
        try {
            sa = (String) ((HashMap) a).get("title");
            sb = (String) ((HashMap) b).get("title");
        } catch (Exception e) {
            return 0;
        }

        return sa.compareTo(sb);
    }
}
