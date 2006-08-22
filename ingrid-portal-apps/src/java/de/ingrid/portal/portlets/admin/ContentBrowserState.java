/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

/**
 * A class for encapsulating the current state of a DB Browser.
 *
 * @author martin@wemove.com
 */
public class ContentBrowserState {

    //    private final static Log log = LogFactory.getLog(ContentPortlet.class);

    protected String sortColumnName = null;

    protected boolean ascendingOrder = true;

    protected final static String PARAM_SORT_COLUMN = "sortColumn";

    public void setSortColumnName(String sortColumnName) {
        this.sortColumnName = sortColumnName;
    }

    /**
     * Get Sort Order of the given column.
     * 
     * @param columnName
     * @return true=sort ascending
     */
    public boolean isAscendingOrder(String columnName) {
        if (columnName != null) {
            if (sortColumnName != null && sortColumnName.equals(columnName)) {
                ascendingOrder = !ascendingOrder;
            } else {
                ascendingOrder = true;
                sortColumnName = columnName;
            }
        }

        return ascendingOrder;
    }

    public boolean isAscendingOrder() {
        return ascendingOrder;
    }
}
