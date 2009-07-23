package de.ingrid.portal.global;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.AbstractPageManager;
import org.apache.jetspeed.page.FolderNotRemovedException;
import org.apache.jetspeed.page.FolderNotUpdatedException;
import org.apache.jetspeed.page.LinkNotRemovedException;
import org.apache.jetspeed.page.LinkNotUpdatedException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.PageNotRemovedException;
import org.apache.jetspeed.page.PageNotUpdatedException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToDeleteDocumentException;
import org.apache.jetspeed.page.document.FailedToUpdateDocumentException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;

public class UtilsPageLayoutTest extends TestCase {

    private PageManager pageManager;

    private Page page;

    protected void setUp() throws Exception {
        pageManager = new TestPageManager();

        page = new PageImpl();

        Fragment root = pageManager.newFragment();
        root.setName("root");
        page.setRootFragment(root);
        Fragment f = pageManager.newPortletFragment();
        f.setLayoutRow(0);
        f.setLayoutColumn(0);
        f.setName("p-0-0");
        root.getFragments().add(f);

        f = pageManager.newPortletFragment();
        f.setLayoutRow(1);
        f.setLayoutColumn(0);
        f.setName("p-1-0");
        root.getFragments().add(f);

        f = pageManager.newPortletFragment();
        f.setLayoutRow(2);
        f.setLayoutColumn(0);
        f.setName("p-2-0");
        root.getFragments().add(f);

        f = pageManager.newPortletFragment();
        f.setLayoutRow(0);
        f.setLayoutColumn(1);
        f.setName("p-0-1");
        root.getFragments().add(f);

        f = pageManager.newPortletFragment();
        f.setLayoutRow(1);
        f.setLayoutColumn(1);
        f.setName("p-1-1");
        root.getFragments().add(f);

        f = pageManager.newPortletFragment();
        f.setLayoutRow(2);
        f.setLayoutColumn(1);
        f.setName("p-2-1");
        root.getFragments().add(f);

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for
     * 'de.ingrid.portal.global.UtilsPageLayout.removeFragmentByPosition(Page,
     * int, int)'
     */
    public void testRemoveFragmentByPosition() throws Exception {
        this.setUp();
        UtilsPageLayout.removeFragmentByPosition(page, page.getRootFragment(), 1, 0);
        assertNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0));
        assertEquals("p-2-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0).getName());
        
        // defragment page layout
        UtilsPageLayout.defragmentLayoutColumn(page.getRootFragment(), 0);
        
        // test: remove non existing fragment
        assertEquals("p-2-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0).getName());
        assertNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0));
    }

    /*
     * Test method for
     * 'de.ingrid.portal.global.UtilsPageLayout.moveFragmentToPosition(Fragment,
     * Fragment, int, int)'
     */
    public void testMoveFragmentToPosition() throws Exception {
        this.setUp();
        UtilsPageLayout.moveFragmentToPosition(page.getRootFragment(), UtilsPageLayout.getFragmentFromPosition(page
                .getRootFragment(), 0, 0), 2, 0);
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0));
        assertEquals("p-1-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0).getName());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0));
        assertEquals("p-2-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0).getName());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0));
        assertEquals("p-0-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0).getName());

        this.setUp();
        UtilsPageLayout.moveFragmentToPosition(page.getRootFragment(), UtilsPageLayout.getFragmentFromPosition(page
                .getRootFragment(), 2, 0), 0, 0);
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0));
        assertEquals("p-2-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0).getName());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0));
        assertEquals("p-0-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0).getName());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0));
        assertEquals("p-1-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0).getName());

        this.setUp();
        UtilsPageLayout.moveFragmentToPosition(page.getRootFragment(), UtilsPageLayout.getFragmentFromPosition(page
                .getRootFragment(), 2, 0), 0, 1);
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0));
        assertEquals("p-0-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0).getName());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0));
        assertEquals("p-2-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0).getName());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 1));
        assertEquals("p-0-1", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 1).getName());

        this.setUp();
        UtilsPageLayout.moveFragmentToPosition(page.getRootFragment(), UtilsPageLayout.getFragmentFromPosition(page
                .getRootFragment(), 1, 0), 1, 0);
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0));
        assertEquals("p-0-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0).getName());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0));
        assertEquals("p-1-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0).getName());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0));
        assertEquals("p-2-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0).getName());

    }

    /*
     * Test method for
     * 'de.ingrid.portal.global.UtilsPageLayout.addPortletToPosition(PageManager,
     * Fragment, String, int, int, int)'
     */
    public void testAddPortletToPosition() throws Exception {
        this.setUp();
        UtilsPageLayout.addPortletToPosition(pageManager, page.getRootFragment(), "new-portlet", 1, 0, new ArrayList<FragmentPreference>());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0));
        assertEquals("p-0-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0).getName());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 3, 0));
        assertEquals("p-2-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 3, 0).getName());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0));
        assertEquals("new-portlet", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0).getName());
    }
    
    /*
     * Test method for
     * 'de.ingrid.portal.global.UtilsPageLayout.addPortletToPosition(PageManager,
     * Fragment, String, int, int, int)'
     */
    public void testPositionPortletOnPage() throws Exception {
        this.setUp();
        UtilsPageLayout.positionPortletOnPage(pageManager, page, page.getRootFragment(), "new-portlet", 3, 0);
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 3, 0));
        assertEquals("new-portlet", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 3, 0).getName());
        UtilsPageLayout.positionPortletOnPage(pageManager, page, page.getRootFragment(), "p-0-0", 2, 0);
        assertEquals("p-1-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0).getName());
        assertEquals("p-2-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0).getName());
        assertEquals("p-0-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0).getName());
    }
    

    /*
     * Test method for
     * 'de.ingrid.portal.global.UtilsPageLayout.getFragmentFromPosition(Fragment,
     * int, int)'
     */
    public void testGetFragmentFromPosition() throws Exception {
        this.setUp();
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0));
        assertEquals("p-0-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 0, 0).getName());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0));
        assertEquals("p-2-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 2, 0).getName());
        assertNotNull(UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0));
        assertEquals("p-1-0", UtilsPageLayout.getFragmentFromPosition(page.getRootFragment(), 1, 0).getName());
    }

    /*
     * Test method for
     * 'de.ingrid.portal.global.UtilsPageLayout.defragmentLayoutColumn(Fragment,
     * int)'
     */
    public void testDefragmentLayoutColumn() {

    }

    private class TestPageManager extends AbstractPageManager {

        private int sequence = 0;

        public TestPageManager() {
            super(false, false);
            this.fragmentClass = FragmentImpl.class;
        }

        public Fragment newPortletFragment() {
            // FragmentImpl requires generated ids
            FragmentImpl fragment = (FragmentImpl) super.newFragment();
            fragment.setType(Fragment.PORTLET);
            fragment.setId(String.valueOf(sequence++));
            return fragment;
        }

        public Fragment newFragment() {
            // FragmentImpl requires generated ids
            FragmentImpl fragment = (FragmentImpl) super.newFragment();
            fragment.setId(String.valueOf(sequence++));
            return fragment;
        }

        public int addPages(Page[] pages) throws NodeException {
            // TODO Auto-generated method stub
            return 0;
        }

        public NodeSet getAll(Folder folder) throws DocumentException {
            // TODO Auto-generated method stub
            return null;
        }

        public ContentPage getContentPage(String path) throws PageNotFoundException, NodeException {
            // TODO Auto-generated method stub
            return null;
        }

        public Folder getFolder(Folder folder, String name) throws FolderNotFoundException, DocumentException {
            // TODO Auto-generated method stub
            return null;
        }

        public Folder getFolder(String folderPath) throws FolderNotFoundException, InvalidFolderException, NodeException {
            // TODO Auto-generated method stub
            return null;
        }

        public NodeSet getFolders(Folder folder) throws DocumentException {
            // TODO Auto-generated method stub
            return null;
        }

        public Link getLink(Folder folder, String name) throws DocumentNotFoundException, NodeException {
            // TODO Auto-generated method stub
            return null;
        }

        public Link getLink(String name) throws DocumentNotFoundException, UnsupportedDocumentTypeException, NodeException {
            // TODO Auto-generated method stub
            return null;
        }

        public NodeSet getLinks(Folder folder) throws NodeException {
            // TODO Auto-generated method stub
            return null;
        }

        public Page getPage(Folder folder, String name) throws PageNotFoundException, NodeException {
            // TODO Auto-generated method stub
            return null;
        }

        public Page getPage(String path) throws PageNotFoundException, NodeException {
            // TODO Auto-generated method stub
            return null;
        }

        public NodeSet getPages(Folder folder) throws NodeException {
            // TODO Auto-generated method stub
            return null;
        }

        public PageSecurity getPageSecurity() throws DocumentNotFoundException, UnsupportedDocumentTypeException, NodeException {
            // TODO Auto-generated method stub
            return null;
        }

        public PageSecurity getPageSecurity(Folder folder) throws DocumentNotFoundException, NodeException {
            // TODO Auto-generated method stub
            return null;
        }

        public void removeFolder(Folder folder) throws NodeException, FolderNotRemovedException {
            // TODO Auto-generated method stub
            
        }

        public void removeLink(Link link) throws NodeException, LinkNotRemovedException {
            // TODO Auto-generated method stub
            
        }

        public void removePage(Page page) throws NodeException, PageNotRemovedException {
            // TODO Auto-generated method stub
            
        }

        public void removePageSecurity(PageSecurity pageSecurity) throws NodeException, FailedToDeleteDocumentException {
            // TODO Auto-generated method stub
            
        }

        public void updateFolder(Folder folder, boolean deep) throws NodeException, FolderNotUpdatedException {
            // TODO Auto-generated method stub
            
        }

        public void updateFolder(Folder folder) throws NodeException, FolderNotUpdatedException {
            // TODO Auto-generated method stub
            
        }

        public void updateLink(Link link) throws NodeException, LinkNotUpdatedException {
            // TODO Auto-generated method stub
            
        }

        public void updatePage(Page page) throws NodeException, PageNotUpdatedException {
            // TODO Auto-generated method stub
            
        }

        public void updatePageSecurity(PageSecurity pageSecurity) throws NodeException, FailedToUpdateDocumentException {
            // TODO Auto-generated method stub
            
        }

        public boolean checkConstraint(String securityConstraintName, String actions) {
            // TODO Auto-generated method stub
            return true;
        }
    }

}
