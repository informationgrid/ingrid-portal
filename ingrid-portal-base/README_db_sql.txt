In dieser Datei nützliche sql skripte fuer die ingrid-portal datenbank ablegen !


===================================================================================================================================================================
  Jetspeed Admin Portlets nach PortalU integrieren
===================================================================================================================================================================

* j2-admin.war (ACHTUNG: aus gleicher Jetspeed Version wie PortalU) ins webapps Verzeichnis -> wird deployed !

* jetspeed administration in Ingrid Datenbank Seitenstruktur rein bringen, MIT NACHFOLGENDEN SQLs:

-- Folder anlegen -> folder
-- =====================

INSERT INTO folder 
(FOLDER_ID, PARENT_ID, PATH, NAME, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, DEFAULT_PAGE_NAME, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='seq_folder'), -- folder id
1, '/Administrative', 'Administrative', 'Jetspeed-Admin', 'Jetspeed-Admin', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- Menu anlegen -> folder_menu
-- =====================

INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, FOLDER_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='seq_folder_menu'), -- menu id
'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 
(SELECT max_key FROM ojb_hl_seq where tablename='seq_folder'), -- folder id
0, 'sub-menu', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL);

-- Menu Eintrag anlegen -> folder_menu
-- =====================

INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, FOLDER_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='seq_folder_menu'), 
'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 
(SELECT max_key FROM ojb_hl_seq where tablename='seq_folder_menu'), -- menu id
NULL, 0, NULL, NULL, NULL, NULL, '/Administrative/site.psml', 0, 0, 0, NULL, NULL, NULL, NULL);

-- page anlegen -> page
-- =====================

INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='seq_page'), -- page id
(SELECT max_key FROM ojb_hl_seq where tablename='seq_folder'), -- folder id
'/Administrative/site.psml', 'site.psml', NULL, 'Portal Site Manager', 'Portal Site Manager', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- page constraints referentiell -> page_constraints_ref
-- =====================

INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='seq_page_constraints_ref'),
(SELECT max_key FROM ojb_hl_seq where tablename='seq_page'), -- page id
0, 'admin');

-- page fragments -> fragment
-- =====================

-- layout fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='seq_fragment'),
NULL, 
(SELECT max_key FROM ojb_hl_seq where tablename='seq_page'), -- page id
'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- portlet fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='seq_fragment'),
(SELECT max_key FROM ojb_hl_seq where tablename='seq_fragment'), -- layout fragment (parent)
NULL, 'j2-admin::PortalSiteManager', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- am Ende ALLE BENUTZTEN SEQUENZEN hoch setzen
-- =====================

-- folder
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='seq_folder';
-- folder_menu
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='seq_folder_menu';
-- page
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='seq_page';
-- page_constraints_ref
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='seq_page_constraints_ref';
-- fragment
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='seq_fragment';


===================================================================================================================================================================
  submenue in main search, statt einer Haupt-Seite (main-search.psml)
===================================================================================================================================================================

-- Folder anlegen -> folder
-- =====================

INSERT INTO folder 
(FOLDER_ID, PARENT_ID, PATH, NAME, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, DEFAULT_PAGE_NAME, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='seq_folder'), -- folder id
1, '/search-catalog', 'search-catalog', 'Search Catalog', 'Search Catalog', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- Menu anlegen -> folder_menu
-- =====================

INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, FOLDER_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='seq_folder_menu'), -- menu id
'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 
(SELECT folder_id FROM folder where path='/'), -- root folder id
0, 'sub-menu-search', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL);

-- Menu Eintraege anlegen -> folder_menu
-- =====================

-- main search
INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, FOLDER_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='seq_folder_menu'), 
'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 
(SELECT max_key FROM ojb_hl_seq where tablename='seq_folder_menu'), -- menu id
NULL, 0, NULL, NULL, NULL, NULL, '/main-search.psml', 0, 0, 0, NULL, NULL, NULL, NULL);

-- catalog search
INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, FOLDER_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST) 
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='seq_folder_menu'), 
'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 
(SELECT max_key FROM ojb_hl_seq where tablename='seq_folder_menu'), -- menu id
NULL, 1, NULL, NULL, NULL, NULL, '/search-catalog/search-catalog-hierarchy.psml', 0, 0, 0, NULL, NULL, NULL, NULL);

-- pages anlegen/aendern -> page
-- =====================

-- Hauptseite (main-search.psml) Titel aendern 
UPDATE page SET 
TITLE='ingrid.page.search.free',  
SHORT_TITLE='ingrid.page.search.free'  
WHERE name='main-search.psml';

-- neue Seiten anlegen

-- search-catalog-hierarchy
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='seq_page'), -- page id
(SELECT max_key FROM ojb_hl_seq where tablename='seq_folder'), -- folder id
'/search-catalog/search-catalog-hierarchy.psml', 'search-catalog-hierarchy.psml', NULL, 'ingrid.page.search.catalog', 'ingrid.page.search.catalog', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- search-catalog-thesaurus.psml
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='seq_page'), -- page id
(SELECT max_key FROM ojb_hl_seq where tablename='seq_folder'), -- folder id
'/search-catalog/search-catalog-thesaurus.psml', 'search-catalog-thesaurus.psml', NULL, 'ingrid.page.search.catalog', 'ingrid.page.search.catalog', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- page constraints referentiell -> page_constraints_ref
-- =====================

-- search-catalog-hierarchy
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='seq_page_constraints_ref'),
(SELECT max_key FROM ojb_hl_seq where tablename='seq_page'), -- page id
0, 'public-view');

-- search-catalog-thesaurus.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='seq_page_constraints_ref'),
(SELECT max_key+1 FROM ojb_hl_seq where tablename='seq_page'), -- page id
0, 'public-view');

-- page fragments -> fragment
-- =====================

-- page: search-catalog-hierarchy

-- layout fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='seq_fragment'),
NULL, 
(SELECT max_key FROM ojb_hl_seq where tablename='seq_page'), -- page id
'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- portlet fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='seq_fragment'),
(SELECT max_key FROM ojb_hl_seq where tablename='seq_fragment'), -- layout fragment (parent)
NULL, 'ingrid-portal-apps::SearchCatalogHierarchy', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- page: search-catalog-thesaurus.psml

-- layout fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='seq_fragment'),
NULL, 
(SELECT max_key+1 FROM ojb_hl_seq where tablename='seq_page'), -- page id
'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- portlet fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+3 FROM ojb_hl_seq where tablename='seq_fragment'),
(SELECT max_key+2 FROM ojb_hl_seq where tablename='seq_fragment'), -- layout fragment (parent)
NULL, 'ingrid-portal-apps::SearchCatalogThesaurus', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- am Ende ALLE BENUTZTEN SEQUENZEN hoch setzen
-- =====================

-- folder
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='seq_folder';
-- folder_menu
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='seq_folder_menu';
-- page
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='seq_page';
-- page_constraints_ref
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='seq_page_constraints_ref';
-- fragment
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='seq_fragment';

