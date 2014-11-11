/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.portlets.admin;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for encapsulating the current state of a DB Browser.
 *
 * @author martin@wemove.com
 */
public class ContentBrowserState {

    //    private final static Logger log = LoggerFactory.getLogger(ContentPortlet.class);

    /** the first result to be retrieved, numbered from 0 */
    protected int firstRow = 0;

    /** limit upon the number of rows to be retrieved */
    protected int maxRows = 10;

    /** total number of rows */
    protected int totalNumRows = 0;

    /** name of the current sort column */
    protected String sortColumnName = null;

    /** current sort order */
    protected boolean ascendingOrder = true;
    
    /** filter criteria to filter the results */
    Map<String, String> filterCriteria = new HashMap<String, String>();

    // PAGING STUFF
    // ------------

    public void doFirstPage() {
        firstRow = 0;
    }

    public void doPreviousPage() {
        firstRow = firstRow - maxRows;
    }

    public void doNextPage() {
        firstRow = firstRow + maxRows;
    }

    public void doLastPage() {
        int remain = totalNumRows % maxRows;
        firstRow = totalNumRows - remain;
    }

    public boolean hasPreviousPage() {
        if (firstRow == 0) {
            return false;
        }
        return true;
    }

    public boolean hasNextPage() {
        int lastRowOnPage = firstRow + maxRows;
        if (lastRowOnPage >= totalNumRows) {
            return false;
        }
        return true;
    }

    public int renderFirstRowOnPage() {
        if (totalNumRows == 0) {
            return 0;
        }
        return firstRow + 1;
    }

    public int renderLastRowOnPage() {
        int lastRowOnPage = firstRow + maxRows;
        if (lastRowOnPage > totalNumRows) {
            lastRowOnPage = totalNumRows;
        }
        return lastRowOnPage;
    }

    // GETTERS/SETTERS
    // ---------------

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

    public int getTotalNumRows() {
        return totalNumRows;
    }

    public void setTotalNumRows(int totalNumRows) {
        this.totalNumRows = totalNumRows;
        if (firstRow >= totalNumRows) {
            firstRow = totalNumRows - maxRows;
        }
        if (firstRow < 0) {
            firstRow = 0;
        }
    }

    public String getSortColumnName() {
        return sortColumnName;
    }

    public void setSortColumnName(String sortColumnName) {
        this.sortColumnName = sortColumnName;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public void setFirstRow(int firstRow) {
        this.firstRow = firstRow;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public Map<String, String> getFilterCriteria() {
        return filterCriteria;
    }

    public void setFilterCriteria(Map<String, String> filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

}
