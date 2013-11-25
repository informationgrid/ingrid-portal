-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '3.2.0d', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
BEGIN
execute immediate 'DROP TABLE ingrid_temp';
exception when others then null;
END;
/
CREATE TABLE  ingrid_temp (
	temp_key VARCHAR2(255),
	temp_value NUMBER(10,0)
);

INSERT INTO ingrid_temp (temp_key, temp_value) VALUES 
	('sub-menu-about', (SELECT MENU_ID FROM folder_menu WHERE NAME='sub-menu-about'));

-- Add new sub menu pages for main menu 'About'
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
	((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT temp_value FROM ingrid_temp where temp_key='sub-menu-about'), NULL, '2', NULL, NULL, NULL, NULL, '/main-about-partner.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT temp_value FROM ingrid_temp where temp_key='sub-menu-about'), NULL , '3', NULL , NULL , NULL , NULL , '/main-about-data-source.psml', NULL , NULL , NULL , NULL , NULL , NULL , NULL);

-- Add new sub menu for main menu 'Catalog'
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL , '1', '0', 'sub-menu-catalog', NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), NULL, '0', NULL, NULL, NULL, NULL, '/search-catalog/search-catalog-hierarchy.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), NULL , '1', NULL , NULL , NULL , NULL , '/search-catalog/search-catalog-thesaurus.psml', NULL , NULL , NULL , NULL , NULL , NULL , NULL);

-- Add new page for 'data source'
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE')  , '1', '/main-about-data-source.psml', 'main-about-data-source.psml', NULL, 'ingrid.page.data.source.tooltip', 'ingrid.page.data.source', '0', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- add fragments to main-about-data-source.psml
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridTwoColumns', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::ShowDataSourcePortlet', NULL , NULL , 'portlet', NULL , 'ingrid-teaser', NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);

-- add page_constraints_ref to main-about-data-source.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 0, 'public-view');

-- add meta_title
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-about-data-source.psml'), 'meta_title', 'de', 'ingrid.page.data.source.meta.title');

-- add meta_description
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-about-data-source.psml'), 'meta_descr', 'de', 'ingrid.page.data.source.meta.description');

-- add meta_keywords
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-about-data-source.psml'), 'meta_keywords', 'de', 'ingrid.page.data.source.meta.keywords');
	
-- delete temporary table
DROP TABLE ingrid_temp;

UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_METADATA';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER_MENU';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_CONSTRAINTS_REF';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER';
