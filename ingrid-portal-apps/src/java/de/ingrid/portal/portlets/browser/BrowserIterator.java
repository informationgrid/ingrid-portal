/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ingrid.portal.portlets.browser;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * DatabaseBrowserPortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: BrowserIterator.java 188123 2004-12-31 23:59:27Z taylor $
 */
public interface BrowserIterator extends Iterator, Comparator, Serializable
{

    /**
     * This method returns the index of the row to which the cursor is pointing
     * at.
     *  
     */
    public int getTop();

    /**
     * This method returns the window size.
     *  
     */
    public int getWindowSize();

    /**
     * This method returns the last index of the row in the window displayed.
     *  
     */
    public int getBottom();

    /**
     * This method points the cursor to the index provided.
     * 
     * @param start
     *            Index to which cursor should point to
     */
    public void setTop(int start);

    /**
     * This method returns the result set vector.
     *  
     */
    public List getResultSet();

    /**
     * This method returns the number of rows in the result set.
     *  
     */
    public int getResultSetSize();

    /**
     * This method returns the List containg the column labels of the result
     * set.
     *  
     */
    public List getResultSetTitleList();

    /**
     * This method returns the List containg the Types of the columns the result
     * set.
     * 
     * @see java.sql.Types
     */
    public List getResultSetTypesList();

    /**
     * This method sorts the result set according to the value of the column as
     * specified by the parameter column name. Changes the order of the result
     * set vector. If it is called on the same columnName more than once it
     * toggles the ordering ie first it will be ascending, then it will be
     * descending, then ascending and so on.
     * 
     * @param String
     *            sortColumnName
     */
    public void sort(String sortColumnName);

}
