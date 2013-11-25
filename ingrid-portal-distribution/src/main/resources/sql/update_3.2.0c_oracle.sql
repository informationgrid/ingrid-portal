-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '3.2.0c', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Update indexes on portal preferences to increase the user manager performance
-- ======================

CREATE INDEX prefs_value_node_id_ix ON prefs_property_value (NODE_ID);
CREATE INDEX prefs_value_name_ix ON prefs_property_value (PROPERTY_NAME);
CREATE INDEX prefs_node_full_path_ix ON prefs_node (FULL_PATH);
CREATE INDEX security_perm_classname_ix ON security_permission (CLASSNAME);

-- Update layout decorator for main-maps.psml
UPDATE page SET default_layout_decorator = 'ingrid-plain', default_portlet_decorator = 'clear' WHERE page.path ='/main-maps.psml';

-- Update layout fragment for main-maps.psml
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE fragment.page_id =(SELECT page_id FROM page WHERE page.path='/main-maps.psml');

-- Delete value of helpKey for ShowMapsPortlet
DELETE FROM fragment_pref_value WHERE pref_id= (SELECT pref_id FROM fragment_pref WHERE fragment_pref.fragment_id = (SELECT fragment_id  FROM fragment WHERE fragment.name= 'ingrid-portal-apps::ShowMapsPortlet'));

-- Delete helpKey for ShowMapsPortlet
DELETE FROM fragment_pref WHERE fragment_pref.fragment_id = (SELECT fragment_id  FROM fragment WHERE fragment.name= 'ingrid-portal-apps::ShowMapsPortlet');

-- Delete SaveMapsPortlet (not in used)
DELETE FROM fragment WHERE name= 'ingrid-portal-apps::SaveMapsPortlet';

-- Temp Table
BEGIN
execute immediate 'DROP TABLE ingrid_temp';
exception when others then null;
END;
/
CREATE TABLE  ingrid_temp (
	temp_key VARCHAR2(255),
	temp_value NUMBER(10,0)
);

-- Update for profile BAW_WSV
-- ==========================
-- 
-- Add CMS pages with OneColumnLayout and add a CMS portlet (including CMS key) to a CMS page.
-- Add sub menu 'sub-menu-start' for BAW default-page (including default and cms pages). 
-- Add meta_title, meta_description, meta_keyword to new added pages.
-- Replace 'ApplicationOverviewPortlet' with 'IFramePortalPortlet' from fragment.
--
-- -----------------------------

-- FIXES and UPDATES for BAW
-- set 'ApplicationOverviewPortlet' to 'IFramePortalPortlet'
UPDATE fragment SET name = 'ingrid-portal-apps::IFramePortalPortlet' WHERE name = 'ingrid-portal-apps::ApplicationOverviewPortlet';

-- add folder
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES
 ('folder_id',(SELECT folder_id FROM folder WHERE parent_id IS NULL AND name = '/' AND path = '/'));

INSERT INTO folder (FOLDER_ID, PARENT_ID, PATH, NAME, TITLE, SHORT_TITLE, IS_HIDDEN) VALUES 
 ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'folder_id'), '/cms', 'cms', 'ingrid.page.cms', 'ingrid.page.cms', 1); 

-- add pages (for '/default-page.psml' sub-menu)
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE')  , (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), '/cms/cms-0.psml', 'cms-0.psml', NULL, 'ingrid.page.cms.0.tooltip', 'ingrid.page.cms.0', '1', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), '/cms/cms-1.psml', 'cms-1.psml', NULL, 'ingrid.page.cms.1.tooltip', 'ingrid.page.cms.1', '1', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), '/cms/cms-2.psml', 'cms-2.psml', NULL, 'ingrid.page.cms.2.tooltip', 'ingrid.page.cms.2', '1', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- add fragments to /cms/cms-0.psml
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridOneColumn', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::CMSPortlet0', NULL , NULL , 'portlet', NULL , 'clear', NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);

-- add fragments to /cms/cms-1.psml
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+2  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridOneColumn', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), (SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::CMSPortlet1', NULL , NULL , 'portlet', NULL , 'clear', NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);

-- add fragments to /cms/cms-2.psml
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , (SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridOneColumn', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), (SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::CMSPortlet2', NULL , NULL , 'portlet', NULL , 'clear', NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);

-- add page_constraints_ref to /cms/cms-0.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 0, 'public-view');

-- add page_constraints_ref to /cms/cms-1.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 0, 'public-view');
	
-- add page_constraints_ref to /cms/cms-2.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 0, 'public-view');

-- add meta_title
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key    FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/cms/cms-0.psml'), 'meta_title', 'de', 'ingrid.page.cms.0.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+1  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/cms/cms-1.psml'), 'meta_title', 'de', 'ingrid.page.cms.1.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+2  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/cms/cms-2.psml'), 'meta_title', 'de', 'ingrid.page.cms.2.meta.title');
	
-- add meta_description
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/cms/cms-0.psml'), 'meta_descr', 'de', 'ingrid.page.cms.0.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/cms/cms-1.psml'), 'meta_descr', 'de', 'ingrid.page.cms.1.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/cms/cms-2.psml'), 'meta_descr', 'de', 'ingrid.page.cms.2.meta.description');

-- add meta_keywords
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/cms/cms-0.psml'), 'meta_keywords', 'de', 'ingrid.page.cms.0.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/cms/cms-1.psml'), 'meta_keywords', 'de', 'ingrid.page.cms.1.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/cms/cms-2.psml'), 'meta_keywords', 'de', 'ingrid.page.cms.2.meta.keywords');

-- add cms submenu
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL , (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'folder_id') , '0', 'sub-menu-start', NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), NULL, '0', NULL, NULL, NULL, NULL, '/default-page.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), NULL, '1', NULL, NULL, NULL, NULL, '/cms/cms-0.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), NULL , '2', NULL , NULL , NULL , NULL , '/cms/cms-1.psml', NULL , NULL , NULL , NULL , NULL , NULL , NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), NULL , '3', NULL , NULL , NULL , NULL , '/cms/cms-2.psml', NULL , NULL , NULL , NULL , NULL , NULL , NULL);

	
-- add cms key
INSERT INTO ingrid_cms (id, item_key, item_description, item_changed_by) VALUES ((SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'portal.cms.portlet.0', 'CMSPortlet0', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.cms.portlet.0'), 'de', 'CMSPortlet0', '', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.cms.portlet.0'), 'en', 'CMSPortlet0', '', 'admin');

-- add cms key
INSERT INTO ingrid_cms (id, item_key, item_description, item_changed_by) VALUES ((SELECT max_key+9 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'portal.cms.portlet.1', 'CMSPortlet1', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.cms.portlet.1'), 'de', 'CMSPortlet1', '', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.cms.portlet.1'), 'en', 'CMSPortlet1', '', 'admin');

-- add cms key
INSERT INTO ingrid_cms (id, item_key, item_description, item_changed_by) VALUES ((SELECT max_key+12 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'portal.cms.portlet.2', 'CMSPortlet2', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+13 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.cms.portlet.2'), 'de', 'CMSPortlet2', '', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+14 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.cms.portlet.2'), 'en', 'CMSPortlet2', '', 'admin');

-- delete temporary table
DROP TABLE ingrid_temp;

-- change ingrid_rss_source.description from VARCHAR( 255 ) to VARCHAR( 1023 )
ALTER TABLE ingrid_rss_source MODIFY (
  description VARCHAR2( 1023 )
);
ALTER TABLE ingrid_rss_store MODIFY (
  title VARCHAR2( 1023 ),
  author VARCHAR2( 1023 )
);

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_METADATA';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER_MENU';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_CONSTRAINTS_REF';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER';