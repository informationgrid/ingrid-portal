-- ===================================================================================================================================================================
--   Hauptmenue: Neuer Punkt "mdek" fuer Mdek Benutzer; submenue "Katalogadministration", "Metadaten-Erfassungskomponente"
-- ===================================================================================================================================================================

-- Neuer Main Menu Eintrag (verweist auf /mdek folder)
-- =====================

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
CREATE TABLE  ingrid_temp (
	temp_key VARCHAR2(255),
	temp_value NUMBER(10,0)
);

-- hoechste ELEMENT_ORDER von main menu zwischen speichern
INSERT INTO ingrid_temp (temp_key, temp_value) 
VALUES (
'ELEMENT_ORDER',
(SELECT max(ELEMENT_ORDER)+1 FROM folder_menu where PARENT_ID=(SELECT MENU_ID FROM folder_menu where NAME='main-menu')) -- ELEMENT_ORDER last element + 1
);

-- menu_id von main menu zwischen speichern
INSERT INTO ingrid_temp (temp_key, temp_value) 
VALUES (
'MENU_ID',
(SELECT MENU_ID FROM folder_menu where NAME='main-menu') -- main menue id
);

-- neues menu item -> folder_menu
INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, FOLDER_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), -- new mdek menu item id
'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 
(SELECT temp_value FROM ingrid_temp where temp_key='MENU_ID'), -- main menue id
NULL, 
(SELECT temp_value FROM ingrid_temp where temp_key='ELEMENT_ORDER'), -- ELEMENT_ORDER new element
NULL, NULL, NULL, NULL, '/mdek', 0, 0, 0, NULL, NULL, NULL, NULL);

-- temporaere Tabelle löschen
DROP TABLE ingrid_temp PURGE;


-- Neuer Folder (/mdek + folder.metadata)
-- =====================

-- Mdek folder anlegen -> folder
INSERT INTO folder 
(FOLDER_ID, PARENT_ID, PATH, NAME, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, DEFAULT_PAGE_NAME, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), -- new mdek FOLDER_ID
1, '/mdek', 'mdek', 'ingrid.page.mdek', 'ingrid.page.mdek', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- Mdek folder constraints referentiell -> folder_constraints_ref
INSERT INTO folder_constraints_ref (CONSTRAINTS_REF_ID, FOLDER_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER_CONSTRAINTS_REF'), -- new CONSTRAINTS_REF_ID
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), -- mdek FOLDER_ID
0, 'admin-portal');

-- Mdek folder constraints -> folder_constraint
INSERT INTO folder_constraint
(CONSTRAINT_ID, FOLDER_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL)
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_CONSTRAINTS_REF'), -- new CONSTRAINT_ID, we use SEQ_FOLDER_CONSTRAINTS_REF (other seq not found !?)
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), -- mdek FOLDER_ID
1, NULL, 'mdek', NULL, 'view'
);


-- Neue Page Mdek Administration (mdek_portal_admin.psml)
-- =====================

-- mdek_portal_admin page anlegen -> page
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- new mdek_portal_admin PAGE_ID
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), -- mdek FOLDER_ID
'/mdek/mdek_portal_admin.psml', 'mdek_portal_admin.psml', NULL, 'ingrid.page.mdek.catadmin', 'ingrid.page.mdek.catadmin', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- mdek_portal_admin page constraints referentiell -> page_constraints_ref
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), -- new CONSTRAINTS_REF_ID
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- mdek_portal_admin PAGE_ID
0, 'admin-portal');

-- layout fragment -> fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- new layout FRAGMENT_ID
NULL, 
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- mdek_portal_admin PAGE_ID
'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- portlet fragment -> fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- new portlet FRAGMENT_ID
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- layout FRAGMENT_ID
NULL, 'ingrid-portal-mdek::MdekPortalAdminPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);


-- Neue Page Mdek Erfassung (mdek_entry.psml)
-- =====================

-- mdek_entry page anlegen -> page
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- new mdek_entry PAGE_ID
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), -- mdek FOLDER_ID
'/mdek/mdek_entry.psml', 'mdek_entry.psml', NULL, 'ingrid.page.mdek.entry', 'ingrid.page.mdek.entry', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- mdek_entry page constraints -> page_constraint
INSERT INTO page_constraint
(CONSTRAINT_ID, PAGE_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL)
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), -- new CONSTRAINT_ID, we use SEQ_PAGE_CONSTRAINTS_REF (other seq not found !?)
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- mdek_entry PAGE_ID
0, 'admin', 'admin-portal', NULL, NULL
);

-- mdek_entry page constraints -> page_constraint
INSERT INTO page_constraint
(CONSTRAINT_ID, PAGE_ID, APPLY_ORDER, USER_PRINCIPALS_ACL, ROLE_PRINCIPALS_ACL, GROUP_PRINCIPALS_ACL, PERMISSIONS_ACL)
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), -- new CONSTRAINT_ID, we use SEQ_PAGE_CONSTRAINTS_REF (other seq not found !?)
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- mdek_entry PAGE_ID
1, NULL, 'mdek', NULL, 'view'
);

-- layout fragment -> fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- new layout FRAGMENT_ID
NULL, 
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- mdek_entry PAGE_ID
'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- portlet fragment -> fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- new portlet FRAGMENT_ID
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- layout FRAGMENT_ID
NULL, 'ingrid-portal-mdek::MdekEntryPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- portlet fragment -> fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- new portlet FRAGMENT_ID
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- layout FRAGMENT_ID
NULL, 'ingrid-portal-mdek::MdekQuickViewPortlet', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);


-- set a new title for the former "Überwachung" menu entry
UPDATE page SET TITLE = 'Jobs', SHORT_TITLE = 'Jobs' WHERE PATH ='/administration/admin-component-monitor.psml';

-- add new columns to the table ingird_rss_source for more information
ALTER TABLE ingrid_rss_source ADD error VARCHAR( 255 );
ALTER TABLE ingrid_rss_source ADD numLastCount NUMBER(3,0);
-- lastUpdate will be automatically filled with a valid timestamp (not like in mysql)
ALTER TABLE ingrid_rss_source ADD lastUpdate timestamp DEFAULT CURRENT_TIMESTAMP;

-- set the default layout decorator of the search detail page to clear
-- an own header is written where title and description info can be used
UPDATE page SET DEFAULT_LAYOUT_DECORATOR = 'ingrid-clear' WHERE PATH = '/search-detail.psml';
UPDATE page SET DEFAULT_LAYOUT_DECORATOR = 'ingrid', DEFAULT_PORTLET_DECORATOR = 'ingrid-teaser' WHERE PATH='/main-maps.psml';

-- am Ende ALLE BENUTZTEN SEQUENZEN hoch setzen
-- =====================

-- folder_menu
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FOLDER_MENU';
-- folder
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FOLDER';
-- folder_constraints_ref
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FOLDER_CONSTRAINTS_REF';
-- page
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_PAGE';
-- page_constraints_ref
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_PAGE_CONSTRAINTS_REF';
-- fragment
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT';


-- update folder data to set default pages
-- =====================

UPDATE folder SET DEFAULT_PAGE_NAME = 'admin-cms.psml' WHERE folder.NAME= 'administration';
UPDATE folder SET DEFAULT_PAGE_NAME = 'search-ext-env-topic-terms.psml' WHERE folder.NAME= 'search-extended';
