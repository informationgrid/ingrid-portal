/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.global;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class UtilsPageLayout {

    private final static Log log = LogFactory.getLog(UtilsPageLayout.class);    
    
    public static void removeFragmentByPosition(Page page, int x, int y) {
        Fragment f = getFragmentFromPosition(page.getRootFragment(), x, y);
        page.removeFragmentById(f.getId());
        defragmentLayoutColumn(page.getRootFragment(), y);
    }
    
    public static  void moveFragmentToPosition(Fragment root, Fragment fragment, int x, int y) {
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
            for (int i=oldX - 1; i>=x; i--) {
                Fragment f = getFragmentFromPosition(root, i, y);
                f.setLayoutRow(f.getLayoutRow() + 1);
            }
        } else {
            // if fragment moves down
            // move all fragments between new and old position one up
            for (int i=oldX + 1; i<=x; i++) {
                Fragment f = getFragmentFromPosition(root, i, y);
                f.setLayoutRow(f.getLayoutRow() - 1);
            }
        }
        fragment.setLayoutRow(x);
    }

    public static  void addPortletToPosition(PageManager pageManager, Fragment root, String portletUniqueName, int x, int y, int numberOfExistingPortlets) {
        try
        {
            Fragment fragment = pageManager.newPortletFragment();
            fragment.setName(portletUniqueName);
            fragment.setLayoutColumn(y);
            fragment.setLayoutRow(numberOfExistingPortlets);
            root.getFragments().add(fragment);
            moveFragmentToPosition(root, fragment, x, y);
        }
        catch (Exception e)
        {
            log.error("failed to add portlet " + portletUniqueName + " to fragment: " + root.getName());
        }
    }
    
    /**
     * Get Fragment that has a certain position on the page.
     * 
     * @param page
     * @param x
     * @param y
     * @return
     */
    public static  Fragment getFragmentFromPosition(Fragment root, int x, int y) {
        List fragments = root.getFragments();
        Iterator it = fragments.iterator();
        while (it.hasNext()) {
            Fragment f = (Fragment)it.next();
            if (f.getLayoutColumn() == y && f.getLayoutRow() == x) {
                return f;
            }
        }
        return null;
    }
    
    /** Defragment a row of page fragment. This makes sure all fragments 
     * have a layout row according their position on the root fragment. 
     * 
     * @param root The root fragment of the layout structure. For simple page structures this is always the root fragment.
     * @param column The column to defragment.
     */
    public static  void defragmentLayoutColumn(Fragment root, int column) {
        List fragments = root.getFragments();
        Iterator it = fragments.iterator();
        ArrayList columnFragments = new ArrayList();
        // collect fragments for the specified column
        while (it.hasNext()) {
            Fragment f = (Fragment)it.next();
            if (f.getLayoutColumn() == column) {
                Utils.ensureArraySize(columnFragments, f.getLayoutRow() + 1);
                columnFragments.set(f.getLayoutRow(), f);
            }
        }
        // defragment the column
        it = columnFragments.iterator();
        int position = 0;
        while (it.hasNext()) {
            Fragment f = (Fragment)it.next();
            if (f != null) {
                f.setLayoutRow(position);
                position++;
            }
        }
    }
    
}
