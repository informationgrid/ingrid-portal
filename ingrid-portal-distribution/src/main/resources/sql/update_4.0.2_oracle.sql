-- DB Version
UPDATE ingrid_lookup SET item_value = '4.0.2', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
-- oracle way of: DROP TABLE IF EXISTS

-- !!!!!!!! -------------------------------------------------
-- !!! DIFFERENT SYNTAX FOR JDBC <-> Scripting !  Choose your syntax, default is JDBC version

-- !!! JDBC VERSION (installer):
-- !!! All in one line and DOUBLE SEMICOLON at end !!! Or causes problems when executing via JDBC in installer (ORA-06550) !

BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;;

-- !!! SCRIPT VERSION (SQL Developer, SQL Plus):
-- !!! SINGLE SEMICOLON AND "/" in separate line !

-- BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;
-- /

-- !!!!!!!! -------------------------------------------------

CREATE TABLE  ingrid_temp (
	temp_key VARCHAR2(255),
	temp_value NUMBER(10,0)
);

-- Update search-detail.psml layout
UPDATE page SET default_layout_decorator = 'ingrid-untitled', default_portlet_decorator = 'clear' WHERE path = '/search-detail.psml';

-- Delete privacy portlet from disclaimer.psml
DELETE FROM fragment WHERE name = 'ingrid-portal-apps::PrivacyPortlet';

-- Update layout of disclaimer CMSPortlet
UPDATE fragment_pref_value SET value = 'block'||chr(45)||'-padded' WHERE pref_id = (SELECT pref_id FROM fragment_pref WHERE name = 'sectionStyle' AND fragment_id = (SELECT fragment_id FROM fragment WHERE name = 'ingrid-portal-apps::CMSPortlet' AND parent_id = (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE PATH = '/disclaimer.psml'))));

-- Insert page privacy.psml
INSERT INTO page (PAGE_ID, CLASS_NAME, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE') , 'org.apache.jetspeed.om.page.impl.PageImpl', '1', '/privacy.psml', 'privacy.psml', NULL, 'ingrid.page.privacy.tooltip', 'ingrid.page.privacy', '0', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- Add fragments to privacy.psml
INSERT INTO fragment (FRAGMENT_ID ,CLASS_NAME ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL , (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridClearLayout', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL);
INSERT INTO fragment (FRAGMENT_ID ,CLASS_NAME ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,OWNER_PRINCIPAL) VALUES
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::PrivacyPortlet', NULL , NULL , 'portlet', NULL , NULL, NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL);

-- Add page_constraints_ref to privacy.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 0, 'public-view');

-- add meta_title
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/privacy.psml'), 'meta_title', 'de', 'ingrid.page.privacy.meta.title');
    
-- add meta_description
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/privacy.psml'), 'meta_descr', 'de', 'ingrid.page.privacy.meta.description');

-- add meta_keywords
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/privacy.psml'), 'meta_keywords', 'de', 'ingrid.page.privacy.meta.keywords');

-- add privacy to footer menu
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES
 ('footer_menu_id',(SELECT menu_id FROM folder_menu WHERE NAME = 'footer-menu'));
INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN) VALUES
 ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer_menu_id'), '5', NULL, NULL, NULL, NULL, '/privacy.psml', '0', '0', '0', NULL, NULL, NULL);

-- delete temporary table
DROP TABLE ingrid_temp;

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_METADATA';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER_MENU';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_CONSTRAINTS_REF';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER';
