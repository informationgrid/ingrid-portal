/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.PageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class for managing the ingrid page layout. Used mainly for page
 * personalization.
 * 
 * @author joachim@wemove.com
 */
public class UtilsPageLayout {

    private final static Logger log = LoggerFactory.getLogger(UtilsPageLayout.class);

    /**
     * Remove a fragment defined by position inside a parent fragment from a
     * page.
     * 
     * @param page
     *            The page.
     * @param parentFragment
     *            The parent fragment (usually page.getRootFragment()).
     * @param x
     *            The row.
     * @param y
     *            The column.
     */
    public static void removeFragmentByPosition(Page page, Fragment parentFragment, int x, int y) {
        Fragment f = getFragmentFromPosition(parentFragment, x, y);
        if (f != null) {
            page.removeFragmentById(f.getId());
        }
    }

    /**
     * Remove all fragments in a certain column.
     * 
     * @param page
     *            The page.
     * @param parentFragment
     *            The parent fragment (usually page.getRootFragment()).
     * @param y
     *            The column.
     */
    public static void removeAllFragmentsInColumn(Page page, Fragment parentFragment, int y) {
        int numberOfPortlets = getNumberOfPortletsInColumn(parentFragment, y);
        for (int i = 0; i < numberOfPortlets; i++) {
            removeFragmentByPosition(page, parentFragment, i, y);
        }
    }

    /**
     * Move an existing fragment to a certain position.
     * 
     * @param parentFragment
     *            The parent fragment of the fragment.
     * @param fragment
     *            The fragment.
     * @param x
     *            The destination row.
     * @param y
     *            The destination column.
     */
    public static void moveFragmentToPosition(Fragment parentFragment, Fragment fragment, int x, int y) {
        // get the old position of the fragment
        int oldY = fragment.getLayoutColumn();
        int oldX = fragment.getLayoutRow();
        // check if the column is different to y -> abort
        if (oldY != y) {
            return;
        }
        // check if the row is the same -> abort
        if (oldX == x) {
            return;
        }
        // if fragment moves up
        if (oldX > x) {
            // move all fragments between new and old position one down
            for (int i = oldX - 1; i >= x; i--) {
                Fragment f = getFragmentFromPosition(parentFragment, i, y);
                if (f != null) {
                    f.setLayoutRow(f.getLayoutRow() + 1);
                }
            }
        } else {
            // if fragment moves down
            // move all fragments between new and old position one up
            for (int i = oldX + 1; i <= x; i++) {
                Fragment f = getFragmentFromPosition(parentFragment, i, y);
                if (f != null) {
                    f.setLayoutRow(f.getLayoutRow() - 1);
                }
            }
        }
        fragment.setLayoutRow(x);
    }

    /**
     * Add a new portlet to a (parent) fragment.
     * 
     * @param pageManager
     *            The page manager.
     * @param parentFragment
     *            The parent fragment.
     * @param portletUniqueName
     *            The unique name of the portlet to add.
     * @param x
     *            The destination row.
     * @param y
     *            The destination column.
     */
    public static void addPortletToPosition(PageManager pageManager, Fragment parentFragment, String portletUniqueName,
            int x, int y, List portletPrefs) {
        try {
            Fragment fragment = pageManager.newPortletFragment();
            fragment.setName(portletUniqueName);
            if (portletPrefs.size()>0)
            	fragment.setPreferences(portletPrefs);
            if (y == 0) {
                fragment.setDecorator("ingrid-teaser");
            } else {
                fragment.setDecorator("ingrid-marginal-teaser");
            }
            fragment.setLayoutColumn(y);
            fragment.setLayoutRow(getNumberOfPortletsInColumn(parentFragment, y));
            parentFragment.getFragments().add(fragment);
            moveFragmentToPosition(parentFragment, fragment, x, y);
        } catch (Exception e) {
            log.error("failed to add portlet " + portletUniqueName + " to fragment: " + parentFragment.getName(), e);
        }
    }

    /**
     * Get the number of portlets in a column of a (parent) fragment.
     * 
     * @param parentFragment
     *            The parent fragment.
     * @param column
     *            The column of the fragment.
     * @return The number of portlets in the column.
     */
    public static int getNumberOfPortletsInColumn(Fragment parentFragment, int column) {
        int cnt = 0;
        List fragments = parentFragment.getFragments();
        Iterator it = fragments.iterator();
        while (it.hasNext()) {
            Fragment f = (Fragment) it.next();
            if (f.getLayoutColumn() == column) {
                cnt++;
            }
        }
        return cnt;
    }

    public static void positionPortletOnPage(PageManager pageManager, Page page, Fragment parentFragment,
            String portletUniqueName, int x, int y) {
    	List<FragmentPreference> dummy = new ArrayList<FragmentPreference>();
    	//dummy.add(pageManager.newFragmentPreference());
    	positionPortletOnPage(pageManager, page, parentFragment, portletUniqueName, x, y, dummy);
    }
    
    /**
     * Positions a Portlet an the page. If a portlet already exists on a page,
     * move it to the new position. If a portlet does not exists, add it.
     * 
     * @param pageManager
     *            The page manager.
     * @param page
     *            The page.
     * @param parentFragment
     *            The parent fragment.
     * @param portletUniqueName
     *            The unique name of the portlet to add.
     * @param x
     *            The destination row.
     * @param y
     *            The destination column.
     */
    public static void positionPortletOnPage(PageManager pageManager, Page page, Fragment parentFragment,
            String portletUniqueName, int x, int y, List portletPrefs) {
        // if 'none' remove the fragment with this position from the page
        if (portletUniqueName.equals("none")) {
            UtilsPageLayout.removeFragmentByPosition(page, parentFragment, x, y);
        } else {
            // find the portlet in the current page
            Fragment f = null;
            List fragments = page.getFragmentsByName(portletUniqueName);
            if (fragments != null && fragments.size() > 0) {
                f = (Fragment) fragments.get(0);
            }
            if (f != null) {
            	if (portletPrefs.size()>0)
            		f.setPreferences(portletPrefs);
                // move the portlet to the configured position
                UtilsPageLayout.moveFragmentToPosition(parentFragment, f, x, y);
            } else {
                // if not found add the portlet to the fragmenet at
                // the specified position
                UtilsPageLayout.addPortletToPosition(pageManager, parentFragment, portletUniqueName, x, y, portletPrefs);
            }
        }
    }

    /**
     * Get fragment that has a certain position on the parent fragment.
     * 
     * @param parentFragment
     *            The parent fragment.
     * @param x
     *            The row.
     * @param y
     *            The column.
     * @return The fragment or null if no fragemnt found.
     */
    public static Fragment getFragmentFromPosition(Fragment parentFragment, int x, int y) {
        List fragments = parentFragment.getFragments();
        Iterator it = fragments.iterator();
        while (it.hasNext()) {
            Fragment f = (Fragment) it.next();
            if (f.getLayoutColumn() == y && f.getLayoutRow() == x) {
                return f;
            }
        }
        return null;
    }

    /**
     * Defragment a row of page fragment. This makes sure all fragments have a
     * layout row according their position on the root fragment.
     * 
     * @param parentFragment
     *            The parent fragment of the layout structure. For simple page
     *            structures this is always the root fragment.
     * @param column
     *            The column to defragment.
     */
    public static void defragmentLayoutColumn(Fragment parentFragment, int column) {
        List fragments = parentFragment.getFragments();
        Iterator it = fragments.iterator();
        ArrayList columnFragments = new ArrayList();
        // collect fragments for the specified column
        while (it.hasNext()) {
            Fragment f = (Fragment) it.next();
            if (f.getLayoutColumn() == column) {
                Utils.ensureArraySize(columnFragments, f.getLayoutRow() + 1);
                if (columnFragments.get(f.getLayoutRow()) != null) {
                    // add in case of occupied slot
                    columnFragments.add(f);
                } else {
                    // if slot is free, set the fragment here
                    columnFragments.set(f.getLayoutRow(), f);
                }
            }
        }
        // defragment the column
        it = columnFragments.iterator();
        int position = 0;
        while (it.hasNext()) {
            Fragment f = (Fragment) it.next();
            if (f != null) {
                f.setLayoutRow(position);
                position++;
            }
        }
    }

}
