INSERT INTO folder 
(FOLDER_ID, PARENT_ID, PATH, NAME, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, DEFAULT_PAGE_NAME, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), 
1, '/search-catalog', 'search-catalog', 'Search Catalog', 'Search Catalog', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, FOLDER_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 
'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL, 
(SELECT folder_id FROM folder where path='/'), 
0, 'sub-menu-search', NULL, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, NULL);

INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, FOLDER_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 
'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 
NULL, 0, NULL, NULL, NULL, NULL, '/main-search.psml', 0, 0, 0, NULL, NULL, NULL, NULL);

INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, FOLDER_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN, IS_NEST) 
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 
'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', 
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 
NULL, 1, NULL, NULL, NULL, NULL, '/search-catalog/search-catalog-hierarchy.psml', 0, 0, 0, NULL, NULL, NULL, NULL);


UPDATE page SET 
TITLE='ingrid.page.search.free',  
SHORT_TITLE='ingrid.page.search.free'  
WHERE name='main-search.psml';

INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), 
'/search-catalog/search-catalog-hierarchy.psml', 'search-catalog-hierarchy.psml', NULL, 'ingrid.page.search.catalog', 'ingrid.page.search.catalog', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), 
'/search-catalog/search-catalog-thesaurus.psml', 'search-catalog-thesaurus.psml', NULL, 'ingrid.page.search.catalog', 'ingrid.page.search.catalog', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'),
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 
0, 'public-view');

INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'),
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 
0, 'public-view');


INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
NULL, 
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 
'jetspeed-layouts::IngridOneColumn', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 
NULL, 'ingrid-portal-apps::SearchCatalogHierarchy', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
NULL, 
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 
'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 
NULL, 'ingrid-portal-apps::SearchCatalogThesaurus', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);


UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FOLDER';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FOLDER_MENU';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_PAGE';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_PAGE_CONSTRAINTS_REF';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT';
